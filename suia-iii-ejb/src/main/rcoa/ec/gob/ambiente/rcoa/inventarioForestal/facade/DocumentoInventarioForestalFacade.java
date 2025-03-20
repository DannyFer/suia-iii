package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ReporteInventarioForestal;
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
public class DocumentoInventarioForestalFacade {
	
	private static final Logger LOG = Logger.getLogger(DocumentoInventarioForestalFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public DocumentoInventarioForestal guardar(DocumentoInventarioForestal documentoInventarioForestal){
		return crudServiceBean.saveOrUpdate(documentoInventarioForestal);
	}
	
	public DocumentoInventarioForestal getByIdDocumentoInventarioForestal(Integer idDocumentoInventarioForestal) {
		DocumentoInventarioForestal result = new DocumentoInventarioForestal();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentoInventarioForestal d WHERE d.id=:idDocumentoInventarioForestal");
		sql.setParameter("idDocumentoInventarioForestal", idDocumentoInventarioForestal);
		if (sql.getResultList().size() > 0)
			result = (DocumentoInventarioForestal) sql.getResultList().get(0);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoInventarioForestal> getByIdCertificado(Integer idCertificado) {
		List<DocumentoInventarioForestal> list = new ArrayList<DocumentoInventarioForestal>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentoInventarioForestal d WHERE d.inventarioForestalCertificado.id=:idCertificado");
		sql.setParameter("idCertificado", idCertificado);
		if (sql.getResultList().size() > 0)
			list =(List<DocumentoInventarioForestal>) sql.getResultList();
		return list;
	}
	
	public DocumentoInventarioForestal getByInventarioTipoDocumento(Integer idInventarioForestalAmbiental, TipoDocumentoSistema tipoDocumentoSistema) throws CmisAlfrescoException {
		Integer idTipoDocumento = tipoDocumentoSistema.getIdTipoDocumento();
		DocumentoInventarioForestal result = new DocumentoInventarioForestal();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentoInventarioForestal d WHERE d.inventarioForestalAmbiental.id=:idInventarioForestalAmbiental and d.tipoDocumento.id=:idTipoDocumento");
		sql.setParameter("idInventarioForestalAmbiental", idInventarioForestalAmbiental);
		sql.setParameter("idTipoDocumento", idTipoDocumento);
		if (sql.getResultList().size() > 0) {
			result =(DocumentoInventarioForestal) sql.getResultList().get(0);
			byte[] archivoObtenido = descargar(result.getIdAlfresco());
			result.setContenidoDocumento(archivoObtenido);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoInventarioForestal> getByInventarioForestalReporte(Integer idReporte) throws CmisAlfrescoException {
		List<DocumentoInventarioForestal> list = new ArrayList<DocumentoInventarioForestal>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentoInventarioForestal d WHERE d.reporteInventarioForestal.id=:idReporte");
		sql.setParameter("idReporte", idReporte);
		if (sql.getResultList().size() > 0) {
			list =(List<DocumentoInventarioForestal>) sql.getResultList();
			for (DocumentoInventarioForestal rowDocumento : list) {
				byte[] archivoObtenido = descargar(rowDocumento.getIdAlfresco());
				rowDocumento.setContenidoDocumento(archivoObtenido);
			}
		}
		return list;
	}
	
	public static DocumentoInventarioForestal asignarDocumentoInventarioForestal(DocumentoInventarioForestal documento, Document documentCreate, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema) {
		String nombre = documento.getNombreDocumento() == null ? documentCreate.getName() : documento.getNombreDocumento(); 
		String extension = documento.getExtencionDocumento();
		String idAlfresco = documentCreate.getId();
		Integer idTable = documento.getIdTabla(); 
		String nombreTabla = documento.getNombreTabla();
		String mimeDocumento = documento.getMimeDocumento();
		String descripcionTabla = documento.getDescripcionTabla();
		InventarioForestalAmbiental inventarioForestalAmbiental = documento.getInventarioForestalAmbiental();
		ReporteInventarioForestal inventarioReporte = documento.getReporteInventarioForestal();
		
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentoInventarioForestal documentoTemp = new DocumentoInventarioForestal();
		
		documentoTemp.setEstado(true);
		documentoTemp.setNombreDocumento(nombre);
		documentoTemp.setExtencionDocumento(extension);
		documentoTemp.setIdAlfresco(idAlfresco);
		documentoTemp.setIdTabla(idTable);
		documentoTemp.setNombreTabla(nombreTabla);
		documentoTemp.setMimeDocumento(mimeDocumento);
		documentoTemp.setDescripcionTabla(descripcionTabla);
		documentoTemp.setInventarioForestalAmbiental(inventarioForestalAmbiental);
		documentoTemp.setReporteInventarioForestal(inventarioReporte);
		documentoTemp.setTipoDocumento(tipoDocumento);
		return documentoTemp;
	}
	
	public DocumentoInventarioForestal guardarDocumentoAlfrescoInventarioForestal(String codigoProyecto, String nombreProceso, Long idProceso, DocumentoInventarioForestal documento, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException{
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		if (idProceso != null) {
			idProces = idProceso.toString();
		}
		String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootRcoaId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-" + documento.getNombreDocumento().replace("/", "-"), folderId,
				nombreProceso, folderName, documento.getIdTabla());
		
		DocumentoInventarioForestal documentoAl = asignarDocumentoInventarioForestal(documento, documentCreate,  tipoDocumento);
		
		DocumentoInventarioForestal documentoAlfreco = crudServiceBean.saveOrUpdate(documentoAl);
		return documentoAlfreco;
	}
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {    	
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
        return archivo;
    }
	
	 public String direccionDescarga(DocumentoInventarioForestal documentoInventarioForestal)
	            throws CmisAlfrescoException, ServiceException {	       
		 return alfrescoServiceBean.downloadDocumentByObjectId(documentoInventarioForestal.getIdAlfresco(), documentoInventarioForestal.getFechaCreacion());	        
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
	 
	@SuppressWarnings("unchecked")
	public List<DocumentoInventarioForestal> getDocumentosByInventarioTipoDocumento(Integer idInventarioForestalAmbiental,TipoDocumentoSistema tipoDocumentoSistema) throws CmisAlfrescoException {
		Integer idTipoDocumento = tipoDocumentoSistema.getIdTipoDocumento();
		List<DocumentoInventarioForestal> result = new ArrayList<>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentoInventarioForestal d WHERE d.inventarioForestalAmbiental.id=:idInventarioForestalAmbiental and d.tipoDocumento.id=:idTipoDocumento");
		sql.setParameter("idInventarioForestalAmbiental", idInventarioForestalAmbiental);
		sql.setParameter("idTipoDocumento", idTipoDocumento);
		if (sql.getResultList().size() > 0) {
			result = (List<DocumentoInventarioForestal>) sql.getResultList();
			
			for(DocumentoInventarioForestal doc : result){
				byte[] archivoObtenido = descargar(doc.getIdAlfresco());
				doc.setContenidoDocumento(archivoObtenido);
			}			
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
	
	@SuppressWarnings("unchecked")
	public List<DocumentoInventarioForestal> getByInventarioForestalReporteTipo(Integer idReporte, TipoDocumentoSistema tipoDocumentoSistema) throws CmisAlfrescoException {
		List<DocumentoInventarioForestal> list = new ArrayList<DocumentoInventarioForestal>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentoInventarioForestal d WHERE d.reporteInventarioForestal.id=:idReporte and d.tipoDocumento.id=:idTipoDocumento order by 1 desc");
		sql.setParameter("idReporte", idReporte);
		sql.setParameter("idTipoDocumento", tipoDocumentoSistema.getIdTipoDocumento());
		if (sql.getResultList().size() > 0) {
			list =(List<DocumentoInventarioForestal>) sql.getResultList();
			for (DocumentoInventarioForestal rowDocumento : list) {
				byte[] archivoObtenido = descargar(rowDocumento.getIdAlfresco());
				rowDocumento.setContenidoDocumento(archivoObtenido);
			}
		}
		return list;
	}
	
	public File getDocumentoPorIdAlfresco(String idDocAlfresco) {
		File file = null;
		try {
			String url = idDocAlfresco;
			url = (url.split(";"))[0];
	
			file = alfrescoServiceBean.downloadDocumentByIdFile(url);
			
		} catch (Exception e) {
			Log.debug(e.toString());
        } 
		
		return file;
	}

}
