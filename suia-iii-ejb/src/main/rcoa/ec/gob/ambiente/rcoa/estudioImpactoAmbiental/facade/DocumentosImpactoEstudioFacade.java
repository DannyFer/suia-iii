package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.model.PmaAceptadoRegistroAmbiental;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosImpactoEstudioFacade {
	
	private static final Logger LOG = Logger.getLogger(DocumentosRegistroAmbientalFacade.class);
	
	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public DocumentoEstudioImpacto guardar(DocumentoEstudioImpacto documento) {        	
    	return crudServiceBean.saveOrUpdate(documento);        
	}

	public void eliminarDocumentos(InformacionProyectoEia informacion, String clase, TipoDocumentoSistema tipoDocumento, Integer documetoId, String usuario) {
		try {
			StringBuilder sql = new StringBuilder();
			// para deshabilita los no seleccionados
			sql.append("UPDATE coa_environmental_impact_study.documents_impact_study ");
			sql.append("set dois_status = false, dois_user_update = '"+usuario+"', dois_date_update = now() "); 
			sql.append("where doty_id = "+tipoDocumento.getIdTipoDocumento()+" ");
			sql.append("and dois_table_id = "+informacion.getId()+" ");
			sql.append("and dois_tabla_class = '"+clase+"' ");
			sql.append("and dois_status = true ");
			sql.append("and dois_id <> "+documetoId+" ");
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		} catch (Exception e) {
			
		}
	}					

	public DocumentoEstudioImpacto ingresarDocumento(DocumentoEstudioImpacto  documentoRegistro, Integer id,
			String codProyecto, TipoDocumentoSistema tipoDocumento, String nombreDocumento, String clase, Long idProceso)
			throws Exception {
		String extension = "";
		String idProces = null;
		if (idProceso != null) {
			idProces = idProceso.toString();
		}
		int i = nombreDocumento.lastIndexOf('.');
		if (i > 0) {
		    extension = nombreDocumento.substring(i+1);
		}
		byte[] data = documentoRegistro.getContenidoDocumento();
		DocumentoEstudioImpacto documento1 = new DocumentoEstudioImpacto();
		documento1.setIdTable(id);
		documento1.setNombre(nombreDocumento);
		documento1.setMime("application/pdf");
		if(extension != null ){
			switch (extension) {
			/*case "xls":
				documento1.setMime("text/plain; charset=UTF-8");
				break;*/
			case "jpg":
				documento1.setMime("image/jpeg");
				break;
			case "jpeg":
				documento1.setMime("image/jpeg");
				break;			
			case "gif":
				documento1.setMime("image/gif");	
				break;		
			case "png":
				documento1.setMime("image/png");	
				break;	
			case "zip":
				documento1.setMime("application/zip");
				break;
			case "rar":
				documento1.setMime("application/rar");
				break;
			case "mp3":
				documento1.setMime("audio/mpeg3");
				break;
			case "apk":
				documento1.setMime("application/vnd.android.package-archive");
				break;
			case "bmp":
				documento1.setMime("image/bmp");
				break;

			default:
				break;
			}
		}
		documento1.setExtesion("."+extension);
		documento1.setNombreTabla(clase);
		documento1.setContenidoDocumento(data);
		documento1.setIdProceso(idProceso);
		documento1 = guardarDocumentoAlfrescoCA(codProyecto, "ESTUDIO_IMPACTO_AMBIENTAL", documento1, tipoDocumento);
		return documento1;
	}
	
	public byte[] descargarDocumentoPorNombre(String nombreDocumento) throws CmisAlfrescoException {
		return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
	}
	
	public DocumentoEstudioImpacto guardarDocumentoAlfrescoCA(String codigoProyecto, String nombreProceso, DocumentoEstudioImpacto documento,
			TipoDocumentoSistema tipoDocumento) throws ServiceException,
			CmisAlfrescoException {
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		String folderName = UtilAlfresco.generarEstructuraCarpetas(
				codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,
				Constantes.getRootRcoaId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento
				.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-"
						+ documento.getNombre().replace("/", "-"), folderId,
				nombreProceso, folderName, documento.getIdTable());

		DocumentoEstudioImpacto documentoAl = crearDocumento(documento
				.getDescripcion() == null ? documentCreate.getName()
				: documento.getDescripcion(), documento.getExtesion(),
				documentCreate.getId(), documento.getIdTable(), documento
						.getMime(), documento.getNombre(), documento
						.getNombreTabla(), tipoDocumento, documento.getIdProceso());
		documentoAl.setCodigoPublico(documento.getCodigoPublico());
		DocumentoEstudioImpacto documentoAlfresco = crudServiceBean.saveOrUpdate(documentoAl);
		return documentoAlfresco;

	}
	

	
	public static DocumentoEstudioImpacto crearDocumento(String descripcion, String extesion, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema, Long idProceso) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentoEstudioImpacto documento = new DocumentoEstudioImpacto();
		documento.setDescripcion(descripcion);
		documento.setEstado(true);
		documento.setExtesion(extesion);
		documento.setAlfrescoId(idAlfresco);
		documento.setIdTable(idTable);
		documento.setMime(mime);
		documento.setNombre(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setTipoDocumento(tipoDocumento);
		documento.setIdProceso(idProceso);

		return documento;
	}
	
	public String direccionDescarga(DocumentoEstudioImpacto documento)
			throws CmisAlfrescoException {
		String workspace = (documento.getAlfrescoId().split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
	}
	
	@SuppressWarnings("unchecked")
	public DocumentoEstudioImpacto documentoXTablaIdXIdDoc(Integer idTabla,
			String nombreTabla, TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException {

		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM DocumentoEstudioImpacto d WHERE d.nombreTabla = :nombreTabla AND d.idTable = :idTabla AND "
						+ "d.tipoDocumento.id = :idTipo AND d.estado = true order by d.fechaCreacion DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("nombreTabla", nombreTabla);
		query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());						
						
		List<DocumentoEstudioImpacto> listaDocumentos = (List<DocumentoEstudioImpacto>) query.getResultList();
		
		for(DocumentoEstudioImpacto documento : listaDocumentos){
			byte[] archivoObtenido = descargar(documento.getAlfrescoId());
			documento.setContenidoDocumento(archivoObtenido);
			return documento;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoEstudioImpacto> documentoXTablaIdXIdDocLista(Integer idTabla,
			String nombreTabla, TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException {

		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM DocumentoEstudioImpacto d WHERE d.nombreTabla = :nombreTabla AND d.idTable = :idTabla AND "
						+ "d.tipoDocumento.id = :idTipo AND d.estado = true order by d.fechaCreacion DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("nombreTabla", nombreTabla);
		query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());						
						
		List<DocumentoEstudioImpacto> listaDocumentos = (List<DocumentoEstudioImpacto>) query.getResultList();
		
		return listaDocumentos;
	}
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
		return archivo;
	}
	
	public boolean verificarFirmaVersion(String idDocAlfresco) {
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
            ArrayList<String> names = af.getSignatureNames();
            return names.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
	
	public File descargarFile(DocumentoEstudioImpacto documento) throws CmisAlfrescoException, MalformedURLException, IOException {
		String idDocAlfresco = documento.getAlfrescoId();
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	return alfrescoServiceBean.downloadDocumentByIdFile(idDocAlfresco, documento.getFechaCreacion());        
    }
	
	public DocumentoEstudioImpacto guardarDocumentoAlfrescoSinProyecto(String codigoProyecto,
			String nombreProceso, Long idProceso, DocumentoEstudioImpacto documento,
			TipoDocumentoSistema tipoDocumento) throws ServiceException,
			CmisAlfrescoException {
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		if (idProceso != null) {
			idProces = idProceso.toString();
		}

		String modulo = null;
		if (nombreProceso.contains("/")) {
			String[] estructuraCarpetas = nombreProceso.split("/");
			modulo = estructuraCarpetas[estructuraCarpetas.length - 1];
		} else {
			modulo = nombreProceso;
		}

		String folderName = UtilAlfresco.generarEstructuraCarpetas(
				codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,
				"Documentos_RCOA");
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento
				.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-"
						+ documento.getNombre().replace("/", "-"), folderId,
				modulo, folderName);

		DocumentoEstudioImpacto documentoAlfrescoSave = crearDocumento(documentCreate.getName(),
						documento.getExtesion(), documentCreate.getId(),
						documento.getIdTable(), documento.getMime(),
						documento.getNombre(), documento.getNombreTabla(),
						tipoDocumento, documento.getIdProceso());
		documentoAlfrescoSave.setIdProceso(documento.getIdProceso());
		
		DocumentoEstudioImpacto documentoAlfresco = crudServiceBean.saveOrUpdate(documentoAlfrescoSave);

		return documentoAlfresco;

	}

}