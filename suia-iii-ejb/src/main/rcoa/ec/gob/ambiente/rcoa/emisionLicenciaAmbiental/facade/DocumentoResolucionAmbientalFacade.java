package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade;

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
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ResolucionAmbiental;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

/**
 * <b> FACADE </b>
 *
 * @author Luis Lema
 * @version Revision: 1.0
 */

@Stateless
public class DocumentoResolucionAmbientalFacade {
	
	private static final Logger LOG = Logger.getLogger(DocumentoResolucionAmbientalFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public DocumentoResolucionAmbiental guardar(DocumentoResolucionAmbiental DocumentoResolucionAmbiental){
		return crudServiceBean.saveOrUpdate(DocumentoResolucionAmbiental);
	}
	
	public DocumentoResolucionAmbiental getById(Integer idDocumentoResolucionAmbiental) {
		DocumentoResolucionAmbiental result = new DocumentoResolucionAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentoResolucionAmbiental d WHERE d.id=:idDocumentoResolucionAmbiental");
		sql.setParameter("idDocumentoResolucionAmbiental", idDocumentoResolucionAmbiental);
		if (sql.getResultList().size() > 0)
			result = (DocumentoResolucionAmbiental) sql.getResultList().get(0);
		return result;
	}
	
	public DocumentoResolucionAmbiental getByDocumentoTipoDocumento(Integer idDocumentoResolucionAmbiental, TipoDocumentoSistema tipoDocumentoSistema) throws CmisAlfrescoException {
		Integer idTipoDocumento = tipoDocumentoSistema.getIdTipoDocumento();
		DocumentoResolucionAmbiental result = new DocumentoResolucionAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentoResolucionAmbiental d WHERE d.id=:idDocumentoResolucionAmbiental and d.tipoDocumento.id=:idTipoDocumento");
		sql.setParameter("idDocumentoResolucionAmbiental", idDocumentoResolucionAmbiental);
		sql.setParameter("idTipoDocumento", idTipoDocumento);
		if (sql.getResultList().size() > 0) {
			result =(DocumentoResolucionAmbiental) sql.getResultList().get(0);
			byte[] archivoObtenido = descargar(result.getIdAlfresco());
			result.setContenidoDocumento(archivoObtenido);
		}
		return result;
	}
	
	public static DocumentoResolucionAmbiental asignarDocumentoResolucionAmbiental(DocumentoResolucionAmbiental documento, Document documentCreate, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema) {
		String nombre = documento.getNombre() == null ? documentCreate.getName() : documento.getNombre(); 
		String extension = documento.getExtension();
		String idAlfresco = documentCreate.getId();
		Integer idTable = documento.getIdTabla(); 
		String nombreTabla = documento.getNombreTabla();
		String mimeDocumento = documento.getMime();
		String descripcionTabla = documento.getDescripcionTabla();
		ResolucionAmbiental resolucionAmbiental = documento.getResolucionAmbiental();
		
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentoResolucionAmbiental documentoTemp = new DocumentoResolucionAmbiental();
		
		documentoTemp.setEstado(true);
		documentoTemp.setNombre(nombre);
		documentoTemp.setExtension(extension);
		documentoTemp.setIdAlfresco(idAlfresco);
		documentoTemp.setIdTabla(idTable);
		documentoTemp.setNombreTabla(nombreTabla);
		documentoTemp.setMime(mimeDocumento);
		documentoTemp.setDescripcionTabla(descripcionTabla);
		documentoTemp.setResolucionAmbiental(resolucionAmbiental);
		documentoTemp.setTipoDocumento(tipoDocumento);
		return documentoTemp;
	}
	
	public DocumentoResolucionAmbiental guardarDocumentoAlfrescoResolucionAmbiental(String codigoProyecto, String nombreProceso, Long idProceso, DocumentoResolucionAmbiental documento, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException{
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		if (idProceso != null) {
			idProces = idProceso.toString();
		}
		String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootRcoaId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-" + documento.getNombre().replace("/", "-"), folderId,
				nombreProceso, folderName, documento.getIdTabla());
		
		DocumentoResolucionAmbiental documentoAl = asignarDocumentoResolucionAmbiental(documento, documentCreate,  tipoDocumento);
		documentoAl.setIdProceso(idProceso);
		
		DocumentoResolucionAmbiental documentoAlfreco = crudServiceBean.saveOrUpdate(documentoAl);
		return documentoAlfreco;
	}
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {    	
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
        return archivo;
    }
	
	 public String direccionDescarga(DocumentoResolucionAmbiental DocumentoResolucionAmbiental)
	            throws CmisAlfrescoException, ServiceException {	       
		 return alfrescoServiceBean.downloadDocumentByObjectId(DocumentoResolucionAmbiental.getIdAlfresco(), DocumentoResolucionAmbiental.getFechaCreacion());	        
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
	            LOG.error(e);
	            return false;
	        } catch (IOException e) {
	            LOG.error(e);
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
	            LOG.error(e);
	            return false;
	        }
	    }
	 
	 public byte[] descargarDocumentoPorNombreYDirectorioBase(String nombreDocumento, String directorioBase)
	            throws CmisAlfrescoException {
	        if (nombreDocumento == null)
	            return null;
	        if (directorioBase == null)
	            directorioBase = Constantes.getRootStaticDocumentsId();
	        return alfrescoServiceBean.downloadDocumentByNameAndFolder(nombreDocumento, directorioBase, true);
	    }
	 
	public byte[] descargarDocumentoPorNombre(String nombreDocumento)
			throws CmisAlfrescoException {
		return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
	}
	 
	@SuppressWarnings("unchecked")
	public List<DocumentoResolucionAmbiental> getDocumentosByIdTablaTipoDocumento(Integer idTabla,TipoDocumentoSistema tipoDocumentoSistema) throws CmisAlfrescoException {
		Integer idTipoDocumento = tipoDocumentoSistema.getIdTipoDocumento();
		List<DocumentoResolucionAmbiental> result = new ArrayList<>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentoResolucionAmbiental d WHERE d.idTabla=:idTabla and d.tipoDocumento.id=:idTipoDocumento and d.estado=true order by fechaCreacion desc");
		sql.setParameter("idTabla", idTabla);
		sql.setParameter("idTipoDocumento", idTipoDocumento);
		if (sql.getResultList().size() > 0) {
			result = (List<DocumentoResolucionAmbiental>) sql.getResultList();
		}
		return result;
	}
	
	public DocumentoResolucionAmbiental getDocumentoByResolucionAmbiental(Integer idResolucionAmbiental,TipoDocumentoSistema tipoDocumentoSistema) throws CmisAlfrescoException {
		Integer idTipoDocumento = tipoDocumentoSistema.getIdTipoDocumento();
		DocumentoResolucionAmbiental result = new DocumentoResolucionAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentoResolucionAmbiental d WHERE d.resolucionAmbiental.id=:idResolucionAmbiental and d.tipoDocumento.id=:idTipoDocumento and d.estado=true order by fechaCreacion desc");
		sql.setParameter("idResolucionAmbiental", idResolucionAmbiental);
		sql.setParameter("idTipoDocumento", idTipoDocumento);
		if (sql.getResultList().size() > 0) {
			result = (DocumentoResolucionAmbiental) sql.getResultList().get(0);
		}
		return result;
	}
	
	public String obtenerUrlAlfresco(String idAlfresco) {
		try {
			return alfrescoServiceBean.downloadDocumentByObjectId(idAlfresco);
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static DocumentoResolucionAmbiental crearDocumento(String descripcion, String extesion, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema, Long idProceso) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentoResolucionAmbiental documento = new DocumentoResolucionAmbiental();
		documento.setDescripcionTabla(descripcion);
		documento.setEstado(true);
		documento.setExtension(extesion);
		documento.setIdAlfresco(idAlfresco);
		documento.setIdTabla(idTable);
		documento.setMime(mime);
		documento.setNombre(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setTipoDocumento(tipoDocumento);
		documento.setIdProceso(idProceso);

		return documento;
	}
	
	public DocumentoResolucionAmbiental guardarDocumentoAlfrescoSinProyecto(String codigoProyecto,
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

		DocumentoResolucionAmbiental documentoAlfrescoSave = crearDocumento(documentCreate.getName(),
						documento.getExtesion(), documentCreate.getId(),
						documento.getIdTable(), documento.getMime(),
						documento.getNombre(), documento.getNombreTabla(),
						tipoDocumento, documento.getIdProceso());
		documentoAlfrescoSave.setIdProceso(documento.getIdProceso());
		
		DocumentoResolucionAmbiental documentoAlfresco = crudServiceBean.saveOrUpdate(documentoAlfrescoSave);
		return documentoAlfresco;

	}
	
	public File descargarFile(DocumentoResolucionAmbiental documento) throws CmisAlfrescoException, MalformedURLException, IOException {
		String idDocAlfresco = documento.getIdAlfresco();
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	return alfrescoServiceBean.downloadDocumentByIdFile(idDocAlfresco);        
    }
	
	public TipoDocumento obtenerTipoDocumento(Integer codTipo) {
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM TipoDocumento o WHERE o.estado=true and o.id=:id");
			query.setParameter("id", codTipo);
			return (TipoDocumento) query.getResultList().get(0);
		} catch (Exception e) {
			return null;
		}
	}

}
