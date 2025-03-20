package ec.gob.ambiente.rcoa.facade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.model.DocumentoFlujoNoVisible;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Flujo;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class DocumentosProcesoRcoaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public List<Documento> getDocumentosPorFlujoNombresUnicos(Flujo flujo, Long processInstanceId, Boolean mostrarTodo) {
        List<Documento> documentos = recuperarDocumentosPorFlujo(flujo, processInstanceId, mostrarTodo);
        Map<String, Documento> documentosFiltrados = new HashMap<String, Documento>();
        for (Documento doc : documentos) {
        	if (!documentosFiltrados.containsKey(doc.getNombre()))
                documentosFiltrados.put(doc.getNombre(), doc);
            else {
            	if(documentosFiltrados.get(doc.getNombre()).getTipoDocumento().getId().equals(doc.getTipoDocumento().getId())) {
                Integer maxId = documentosFiltrados.get(doc.getNombre()).getId();
                if (doc.getId() > maxId) {
                    documentosFiltrados.put(doc.getNombre(), doc);
                }
            	} else {
            		documentosFiltrados.put(doc.getNombre() + "_" + doc.getTipoDocumento().getId() , doc);
            	}
            }          
        }

        List<Documento> documentosResult = new ArrayList<Documento>();
        for (String nombreDocumento : documentosFiltrados.keySet()) {
            Documento doc = documentosFiltrados.get(nombreDocumento);
            if (doc != null && doc.getIdAlfresco() != null && !doc.getIdAlfresco().isEmpty()) {
            	documentosResult.add(doc);
            }
        }

        Collections.sort(documentosResult, new Comparator<Documento>() {

            @Override
            public int compare(Documento o1, Documento o2) {
                if (o1.getFechaCreacion().before(o2.getFechaCreacion()))
                    return 1;
                if (o1.getFechaCreacion().after(o2.getFechaCreacion()))
                    return -1;
                return 0;
            }
        });

        return documentosResult;
    }
	
	public List<Documento> recuperarDocumentosPorFlujo(Flujo flujo, Long idInstanciaProceso, Boolean mostrarTodo) {		
		String prefijo = flujo.getPrefijoTablaDocumento();
		String queryString=null;
		if (String.valueOf(flujo.getIdProceso()).equals("rcoa.AprobracionRequisitosTecnicosGesTrans2")){
			queryString = "SELECT " + prefijo + "_id, " + prefijo + "_name, " + prefijo + "_creation_date, " + prefijo + "_alfresco_id, doty_id "
					+ "FROM  " + flujo.getTablaDocumento() + " d "
					+ "WHERE " + prefijo + "_id in ( SELECT docu_id "
					+ "FROM  suia_iii.documents_process_tasks "
					+ "WHERE proc_inst_id = "
					+ idInstanciaProceso + " and dota_status = true) and " + prefijo + "_status = true ";					

		} else if (String.valueOf(flujo.getIdProceso()).equals("Suia.AprobracionRequisitosTecnicosGesTrans2")){
			queryString = "SELECT " + prefijo + "_id, " + prefijo + "_name, " + prefijo + "_creation_date, " + prefijo + "_alfresco_id, doty_id "
					+ "FROM  " + flujo.getTablaDocumento() + " d "
					+ "WHERE " + prefijo + "_task_bpm_id = "
					+ idInstanciaProceso + " and " + prefijo + "_status = true ";
		} else{
		 queryString = "SELECT " + prefijo + "_id, " + prefijo + "_name, " + prefijo + "_creation_date, " + prefijo + "_alfresco_id, doty_id "
				+ "FROM  " + flujo.getTablaDocumento() + " d "
				+ "WHERE " + prefijo + "_process_instance_id = "
				+ idInstanciaProceso + " and " + prefijo + "_status = true ";
		}
		if(!mostrarTodo) {
			String idsNoVisibles = recuperarIdsNoVisibles(flujo.getId());
			
			if(idsNoVisibles != null && idsNoVisibles != "") 
				queryString += " and doty_id not in (" + idsNoVisibles + ")";
		}

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

		List<Documento> documentos = new ArrayList<Documento>();
		for (Object object : result) {
			Object[] resDoc = (Object[]) object;
			
			TipoDocumento tipoDoc = new TipoDocumento();
			tipoDoc.setId((Integer) resDoc[4]);
			
			Documento doc = new Documento();
			doc.setId((Integer) resDoc[0]);
			doc.setNombre((String) resDoc[1]);
			doc.setFechaCreacion((Date) resDoc[2]);
			doc.setIdAlfresco((String) resDoc[3]);
			doc.setTipoDocumento(tipoDoc);
			
			documentos.add(doc);
		}

		return documentos;
	}
	
	@SuppressWarnings("unchecked")
	public String recuperarIdsNoVisibles(Integer idFlujo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idFlujo", idFlujo);
		List<DocumentoFlujoNoVisible> listaResult = (List<DocumentoFlujoNoVisible>) crudServiceBean.findByNamedQuery(
				DocumentoFlujoNoVisible.GET_POR_FLUJO, params);
		
		String idsNoVisibles = "";
		if(listaResult != null && listaResult.size() > 0) {
			for (DocumentoFlujoNoVisible documentoFlujoNoVisible : listaResult) {
				idsNoVisibles += documentoFlujoNoVisible.getIdTipoDocumento() + ",";
			}
			
			idsNoVisibles = idsNoVisibles.substring(0, idsNoVisibles.length()-1);
		}
		return idsNoVisibles;
	}
	
	public byte[] descargar(String idDocAlfresco, Date fecha) throws CmisAlfrescoException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		byte[] archivo = alfrescoServiceBean.downloadDocumentByIdLectura(idDocAlfresco, fecha);
		return archivo;
	}
	
	public List<Documento> descargarDocumentosProcesoRgd(ProyectoLicenciaCoa proyecto) {
		List<RegistroGeneradorDesechosProyectosRcoa>  registroGeneradorDesechosProyectosRcoas = registroGeneradorDesechosProyectosRcoaFacade.asociados(proyecto.getId());
		RegistroGeneradorDesechosRcoa registro = registroGeneradorDesechosProyectosRcoas.get(0).getRegistroGeneradorDesechosRcoa();
		List<DesechosRegistroGeneradorRcoa> listaDesechos = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGeneradorGenerados(registro);
		List<DocumentosRgdRcoa> listaDocumentos = new ArrayList<DocumentosRgdRcoa>();

		byte[] byteGuiasEtiquetado = null;
		List<Documento> documentos = new ArrayList<Documento>();
		
		for(DesechosRegistroGeneradorRcoa desecho : listaDesechos){
			String nombreDocumento = null;
			boolean existeDocumento = false;
			try{
				
				String clave = desecho.getDesechoPeligroso().getClave();
				String claveNombre = clave.replace("-", "."); 
				nombreDocumento = claveNombre + ".pdf";
				
				byteGuiasEtiquetado = documentosRgdRcoaFacade.descargarDocumentoPorNombre(nombreDocumento);
				existeDocumento = true;
			}catch(Exception ex){
				existeDocumento = false;
			}
			if(existeDocumento){
				DocumentosRgdRcoa documento = new DocumentosRgdRcoa();				
				documento.setContenidoDocumento(byteGuiasEtiquetado);
				documento.setNombre(desecho.getDesechoPeligroso().getClave());
				documento.setDescripcion(desecho.getDesechoPeligroso().getClave() + "-" + desecho.getDesechoPeligroso().getDescripcion());
				listaDocumentos.add(documento);
				
				Documento doc = new Documento();
				doc.setNombre(nombreDocumento);
				doc.setContenidoDocumento(byteGuiasEtiquetado);
				documentos.add(doc);
			}
		}
		
		Boolean existeGuias = false;
		String nombreGuiaAlmacenamiento="GUÍA REFERENCIAL DE ALMACENAMIENTO DE RESIDUOS O DESECHOS PELIGROSOS.pdf";
		byte[] byteGuiasAlmacenamiento  = null;
		try {
			byteGuiasAlmacenamiento = documentosRgdRcoaFacade
					.descargarDocumentoPorNombre(nombreGuiaAlmacenamiento);
			existeGuias = true;
		} catch (Exception ex) {
			existeGuias = false;
		}

		if (existeGuias) {
			Documento doc = new Documento();
			doc.setNombre(nombreGuiaAlmacenamiento);
			doc.setContenidoDocumento(byteGuiasAlmacenamiento);
			documentos.add(doc);
		}
		
		return documentos;
	}
	
	
	public List<Documento> descargarDocumentosProcesoRgdZip(ProyectoLicenciaCoa proyecto) throws Exception {
		List<RegistroGeneradorDesechosProyectosRcoa>  registroGeneradorDesechosProyectosRcoas = registroGeneradorDesechosProyectosRcoaFacade.asociados(proyecto.getId());
		RegistroGeneradorDesechosRcoa registro = registroGeneradorDesechosProyectosRcoas.get(0).getRegistroGeneradorDesechosRcoa();
		List<DesechosRegistroGeneradorRcoa> listaDesechos = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGeneradorGenerados(registro);

		List<Documento> documentos = new ArrayList<Documento>();
		List<String> filePaths = new ArrayList<String>();
		
			
		for(DesechosRegistroGeneradorRcoa desecho : listaDesechos){
			String nombreDocumento = null;
			String clave = desecho.getDesechoPeligroso().getClave();
			String claveNombre = clave.replace("-", ".");
			nombreDocumento = claveNombre + ".pdf";

			documentosRgdRcoaFacade.descargarDocumentoPorNombre(nombreDocumento);

			filePaths.add(System.getProperty("java.io.tmpdir") + "/" + nombreDocumento);
		}

		String nombreZip = "EtiquetasRegistroGeneradorDesechos.zip";
		String zipPath = System.getProperty("java.io.tmpdir") + "/" + nombreZip;
		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipPath))) {
			for (String filePath : filePaths) {
				File fileToZip = new File(filePath);
				zipOut.putNextEntry(new ZipEntry(fileToZip.getName()));
				Files.copy(fileToZip.toPath(), zipOut);
			}
		}

		File fileArchivo = new File(zipPath);
		Path path = Paths.get(fileArchivo.getAbsolutePath());
		Documento docEtiquetas = new Documento();
		docEtiquetas.setNombre(nombreZip);
		docEtiquetas.setContenidoDocumento(Files.readAllBytes(path));
		documentos.add(docEtiquetas);
		
		Boolean existeGuias = false;
		String nombreGuiaAlmacenamiento="GUÍA REFERENCIAL DE ALMACENAMIENTO DE RESIDUOS O DESECHOS PELIGROSOS.pdf";
		byte[] byteGuiasAlmacenamiento  = null;
		try {
			byteGuiasAlmacenamiento = documentosRgdRcoaFacade
					.descargarDocumentoPorNombre(nombreGuiaAlmacenamiento);
			existeGuias = true;
		} catch (Exception ex) {
			existeGuias = false;
		}

		if (existeGuias) {
			Documento doc = new Documento();
			doc.setNombre(nombreGuiaAlmacenamiento);
			doc.setContenidoDocumento(byteGuiasAlmacenamiento);
			documentos.add(doc);
		}
		
		return documentos;
	}
	
	public byte[] descargarPorNombre(String nombre) throws CmisAlfrescoException {
		byte[] archivo = documentosRgdRcoaFacade.descargarDocumentoPorNombre(nombre);
		return archivo;
	}
}
