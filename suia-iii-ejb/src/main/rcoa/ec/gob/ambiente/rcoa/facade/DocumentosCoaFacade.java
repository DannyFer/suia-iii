package ec.gob.ambiente.rcoa.facade;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosCoaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public DocumentosCOA guardar(DocumentosCOA obj)
	{
		return crudServiceBean.saveOrUpdate(obj);
	}
	
	public DocumentosCOA guardarDocumentoAlfresco(String codigoProyecto, String nombreProceso, Long idProceso, DocumentosCOA documento, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException{
		
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		if (idProceso != null) {
			idProces = idProceso.toString();
		}
		
		String folderName = UtilAlfresco.generarEstructuraCarpetas(
				codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,
				Constantes.getRootRcoaId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento
				.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-"
						+ documento.getNombreDocumento().replace("/", "-"), folderId,
				nombreProceso, folderName, documento.getIdTabla());
		
		DocumentosCOA documentoAl = UtilAlfresco.crearDocumentoCOA(documento.getNombreDocumento() == null ? documentCreate.getName()
				: documento.getNombreDocumento(), documento.getExtencionDocumento(),
				documentCreate.getId(), documento.getIdTabla(), documento.getTipo(),
						 documento.getNombreDocumento(), documento.getNombreTabla(), tipoDocumento);
		documentoAl.setIdProceso(documento.getIdProceso());
		
		DocumentosCOA documentoAlfreco = crudServiceBean.saveOrUpdate(documentoAl);
		
		
		return documentoAlfreco;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentosCOA> documentoXTablaIdXIdDoc(Integer idTabla, TipoDocumentoSistema tipoDocumento,String nombreTabla) throws CmisAlfrescoException {

		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM DocumentosCOA d WHERE d.idTabla = :idTabla AND d.nombreTabla=:nombreTabla AND "
						+ "d.tipoDocumento.id = :idTipo AND d.estado = true order by d.id DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());
		query.setParameter("nombreTabla", nombreTabla);
						
		List<DocumentosCOA> listaDocumentos = (List<DocumentosCOA>) query.getResultList();
		
		return listaDocumentos;
	}
	
	public DocumentosCOA documentoXTablaIdXIdDocUno(Integer idTabla, TipoDocumentoSistema tipoDocumento, String nombreTabla) throws CmisAlfrescoException {
	    try {
	        Query query = crudServiceBean.getEntityManager().createQuery("SELECT d FROM DocumentosCOA d WHERE d.idTabla = :idTabla "
	                + "AND d.nombreTabla = :nombreTabla AND d.tipoDocumento.id = :idTipo AND d.estado = true");
	        query.setParameter("idTabla", idTabla);
	        query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());
	        query.setParameter("nombreTabla", nombreTabla);
	        return (DocumentosCOA) query.setMaxResults(1).getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}
	
	public String direccionDescarga(DocumentosCOA documento) throws CmisAlfrescoException {
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
	
	@SuppressWarnings("rawtypes")
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
	
	@SuppressWarnings("rawtypes")
	public String verificarFirma(byte[] contenido) {
		String result = "false";
        try {

            PdfReader reader = new PdfReader(contenido);
            if(!reader.isEncrypted()) {
	            AcroFields af = reader.getAcroFields();
	            ArrayList names = af.getSignatureNames();
	            if(names.size() > 0) {
	            	result = "true";
	            }	
            } else {
            	result = "No se puede verificar la firma electrónica porque el documento está protegido";
            }
        } catch (Exception e) {
            
        }
        return result;
    }
	
	public byte[] descargarDocumentoPorNombre(String nombreDocumento) throws CmisAlfrescoException {
		return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
	}
	
	public String direccionDescarga(String idAlfresco) throws CmisAlfrescoException {
		String workspace = (idAlfresco.split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
    }
	
	public File descargarFile(DocumentosCOA documento) throws CmisAlfrescoException, MalformedURLException, IOException {
		String idDocAlfresco = documento.getIdAlfresco();
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	return alfrescoServiceBean.downloadDocumentByIdFile(idDocAlfresco);        
    }
	
	@SuppressWarnings("unchecked")
	public List<DocumentosCOA> documentoXTablaIdXIdDocXNroActualizacion(Integer idTabla, TipoDocumentoSistema tipoDocumento,String nombreTabla, Integer nroActualizacion) throws CmisAlfrescoException {

		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM DocumentosCOA d WHERE d.idTabla = :idTabla AND d.nombreTabla=:nombreTabla AND "
						+ "d.tipoDocumento.id = :idTipo AND d.estado = true and d.nroActualizacion =:nroActualizacion order by d.id DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());
		query.setParameter("nombreTabla", nombreTabla);
		query.setParameter("nroActualizacion", nroActualizacion);
						
		List<DocumentosCOA> listaDocumentos = (List<DocumentosCOA>) query.getResultList();
		return listaDocumentos;
	}

	@SuppressWarnings("unchecked")
	public List<DocumentosCOA> recuperarDocumentosPorTipoActualizacion(Integer idTabla,
			String nombreTabla, List<Integer> listaTipos, Integer nroActualizacion) throws Exception {

		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM DocumentosCOA d WHERE d.nombreTabla = :nombreTabla AND d.idTabla = :idTabla "
				+ " AND d.tipoDocumento.id in :idTipos AND d.estado = true and nroActualizacion =:nroActualizacion order by d.fechaCreacion DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("idTipos", listaTipos);
		query.setParameter("nombreTabla", nombreTabla);
		query.setParameter("nroActualizacion", nroActualizacion);
						
		List<DocumentosCOA> listaDocumentos = (List<DocumentosCOA>) query.getResultList();

		return listaDocumentos;

	}

}
