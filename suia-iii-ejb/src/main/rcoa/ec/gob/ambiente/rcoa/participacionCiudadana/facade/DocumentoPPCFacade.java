package ec.gob.ambiente.rcoa.participacionCiudadana.facade;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ReporteInventarioForestal;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.ProyectoFacilitadorPPC;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ReporteFacilitadorParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentoPPCFacade {
	
	private static final Logger LOG = Logger.getLogger(DocumentoPPCFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	
	public DocumentosPPC guardar(DocumentosPPC documento){
		return crudServiceBean.saveOrUpdate(documento);
	}
	
	public DocumentosPPC guardarDocumentoAlfresco(String codigoProyecto, String nombreProceso, Long idProceso, DocumentosPPC documento, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException{
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		if (idProceso != null) {
			idProces = idProceso.toString();
		}
		String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,Constantes.getRootRcoaId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),Calendar.getInstance().getTimeInMillis() + "-"+ documento.getNombreDocumento().replace("/", "-"), folderId,nombreProceso, folderName, documento.getIdTabla());
		
		DocumentosPPC documentoAl = UtilAlfresco.crearDocumentoPPC(documento.getNombreDocumento() == null ? documentCreate.getName()
				: documento.getNombreDocumento(), documento.getExtencionDocumento(),
				documentCreate.getId(), documento.getIdTabla(), documento.getTipo(),
						 documento.getNombreDocumento(), documento.getNombreTabla(), tipoDocumento);
		documentoAl.setIdProceso(documento.getIdProceso());
		
		DocumentosPPC documentoAlfreco = crudServiceBean.saveOrUpdate(documentoAl);		
		
		return documentoAlfreco;
	}
	
	public DocumentosPPC guardarDocumentoAlfrescoPPC(String codigoProyecto, String nombreProceso, Long idProceso, DocumentosPPC documento, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException{
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		if (idProceso != null) {
			idProces = idProceso.toString();
		}
		String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootRcoaId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),Calendar.getInstance().getTimeInMillis() + "-" + documento.getNombreDocumento().replace("/", "-"), folderId,nombreProceso, folderName, documento.getIdTabla());
		
		DocumentosPPC documentoAl = asignarDocumentoPPC(documento, documentCreate,  tipoDocumento);
		
		DocumentosPPC documentoAlfreco = crudServiceBean.saveOrUpdate(documentoAl);
		return documentoAlfreco;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentosPPC> documentoXTablaIdXIdDoc(Integer idTabla, TipoDocumentoSistema tipoDocumento,String nombreTabla) throws CmisAlfrescoException {

		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM DocumentosPPC d WHERE d.idTabla = :idTabla AND d.nombreTabla=:nombreTabla AND "
						+ "d.tipoDocumento.id = :idTipo AND d.estado = true order by d.id DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());
		query.setParameter("nombreTabla", nombreTabla);
						
		List<DocumentosPPC> listaDocumentos = (List<DocumentosPPC>) query.getResultList();
		
//		for(DocumentosPPC documento : listaDocumentos){
//			byte[] archivoObtenido = descargar(documento.getIdAlfresco());
//			documento.setContenidoDocumento(archivoObtenido);
//		}				
		return listaDocumentos;
	}
	
	@SuppressWarnings("unchecked")
	public DocumentosPPC getByDocumentoTipoDocumento(Integer idTabla, TipoDocumentoSistema tipoDocumento, String nombreTabla) throws CmisAlfrescoException {
		Integer idTipoDocumento = tipoDocumento.getIdTipoDocumento();
		DocumentosPPC result = new DocumentosPPC();
		Query query =  crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentosPPC d WHERE d.idTabla = :idTabla AND d.nombreTabla=:nombreTabla AND d.tipoDocumento.id = :idTipo AND d.estado = true order by d.id DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("idTipo", idTipoDocumento);
		query.setParameter("nombreTabla", nombreTabla);
		if (query.getResultList().size() > 0) {
			result =(DocumentosPPC) query.getResultList().get(0);
			byte[] archivoObtenido = descargar(result.getIdAlfresco());
			result.setContenidoDocumento(archivoObtenido);
		}
		return result;
	}
	
	public String direccionDescarga(DocumentosPPC documento) throws CmisAlfrescoException {
		String workspace = (documento.getIdAlfresco().split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
    }
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {    	
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
        return archivo;
    }
	
	public boolean verificarFirmaVersion(String idAlfresco) {
        return verificarFirmaAlfresco(idAlfresco);

    }
	
	public boolean verificarFirmaAlfresco(String idDocAlfresco) {
        File file = null;
        try {
        	String url = idDocAlfresco;
        	url=(url.split(";"))[0];
        	file = alfrescoServiceBean.downloadDocumentByIdFile(url);
        	if(verificarFirma(file.getAbsolutePath()))
        		return true;

        	return false;
        } catch (CmisAlfrescoException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
	
	public boolean verificarFirma(String fileUrl) {
        try {

            PdfReader reader = new PdfReader(fileUrl);
            AcroFields af = reader.getAcroFields();
            ArrayList names = af.getSignatureNames();
            return names.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
	
	public PlantillaReporte obtenerPlantillaReporte(Integer tipoDocumentoId) {
		try {
			Map<String, Object> parametros = new HashMap();
			parametros.put("p_tipoDocumentoId", tipoDocumentoId);

			List<PlantillaReporte> lista = this.crudServiceBean.findByNamedQueryGeneric(
					PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (PlantillaReporte) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public static DocumentosPPC asignarDocumentoPPC(DocumentosPPC documento, Document documentCreate, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema) {
		String nombre = documento.getNombreDocumento() == null ? documentCreate.getName() : documento.getNombreDocumento(); 
		String extension = documento.getExtencionDocumento();
		String idAlfresco = documentCreate.getId();
		Integer idTable = documento.getIdTabla(); 
		String nombreTabla = documento.getNombreTabla();
		String mimeDocumento = documento.getTipo();
		String descripcionTabla = documento.getDescripcion();
		ProyectoFacilitadorPPC proyectoFacilitadorPPC = documento.getProyectoFacilitadorPPC();
		Long idProceso = documento.getIdProceso();
		
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentosPPC documentoTemp = new DocumentosPPC();
		
		documentoTemp.setEstado(true);
		documentoTemp.setNombreDocumento(nombre);
		documentoTemp.setExtencionDocumento(extension);
		documentoTemp.setIdAlfresco(idAlfresco);
		documentoTemp.setIdTabla(idTable);
		documentoTemp.setNombreTabla(nombreTabla);
		documentoTemp.setTipo(mimeDocumento);
		documentoTemp.setDescripcion(descripcionTabla);
		documentoTemp.setTipo(mimeDocumento);
		documentoTemp.setTipoDocumento(tipoDocumento);
		documentoTemp.setProyectoFacilitadorPPC(proyectoFacilitadorPPC);
		documentoTemp.setIdProceso(idProceso);
		return documentoTemp;
	}

	public File obtenerFileByObjectId(String objectId) throws CmisAlfrescoException, MalformedURLException, IOException {    	
    	File archivo = alfrescoServiceBean.downloadDocumentByIdFile(objectId);
        return archivo;
    }
	
	public DocumentosPPC eliminar(DocumentosPPC documentosPPC) {
		documentosPPC.setEstado(false);
		return crudServiceBean.saveOrUpdate(documentosPPC);
	}
	
	public byte[] descargarDocumentoPorNombre(String nombreDocumento) throws CmisAlfrescoException {
        return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
    }
}
