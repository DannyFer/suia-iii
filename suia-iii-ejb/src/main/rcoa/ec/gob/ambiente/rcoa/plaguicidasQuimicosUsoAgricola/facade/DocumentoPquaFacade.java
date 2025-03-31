package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

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
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentoPquaFacade {
	
	private static final Logger LOG = Logger.getLogger(DocumentoPquaFacade.class);

	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public DocumentoPqua guardar(DocumentoPqua documento){
		return crudServiceBean.saveOrUpdate(documento);
	}
	
	public DocumentoPqua guardarDocumentoAlfresco(String codigoProyecto, String nombreProceso, Long idProceso, DocumentoPqua documento, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException{
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		if (idProceso != null) {
			idProces = idProceso.toString();
		}
		
		String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootRcoaId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-" + documento.getNombre().replace("/", "-"), 
				folderId, nombreProceso, folderName, documento.getIdTabla());
		
		DocumentoPqua documentoNuevo = crearDocumentoPQUA(
				documento.getExtension(), documentCreate.getId(),
				documento.getIdTabla(), documento.getMime(),
				documento.getNombre(), documento.getNombreTabla(),
				tipoDocumento);
		
		documentoNuevo.setIdProceso(documento.getIdProceso());

		DocumentoPqua documentoAlfresco = crudServiceBean.saveOrUpdate(documentoNuevo);
		return documentoAlfresco;
	}
	
	public static DocumentoPqua crearDocumentoPQUA(String extension, String idAlfresco, Integer idTable, 
			String mime, String nombre, String nombreTabla, TipoDocumentoSistema tipoDocumentoSistema) {
		
		DocumentoPqua documento = new DocumentoPqua();
		
		documento.setEstado(true);
		documento.setExtension(extension);
		documento.setIdAlfresco(idAlfresco);
		documento.setIdTabla(idTable);
		documento.setNombre(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setIdTipoDocumento(tipoDocumentoSistema.getIdTipoDocumento());
		documento.setMime(mime);

		return documento;
	}
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
		return archivo;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoPqua> documentoPorTablaIdPorIdDoc(Integer idTabla, TipoDocumentoSistema tipoDocumento,String nombreTabla) {
		
		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM DocumentoPqua d WHERE d.idTabla = :idTabla AND d.nombreTabla=:nombreTabla AND "
						+ "d.idTipoDocumento = :idTipo AND d.estado = true order by d.id DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());
		query.setParameter("nombreTabla", nombreTabla);
		
		List<DocumentoPqua> listaDocumentos = (List<DocumentoPqua>)query.getResultList();
		
		return listaDocumentos;
		
	}
	
	public String direccionDescarga(DocumentoPqua documento) throws CmisAlfrescoException {
		String workspace = (documento.getIdAlfresco().split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
    }
	
	public byte[] descargarDocumentoPorNombre(String nombreDocumento) throws CmisAlfrescoException {
		return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
	}
	
	public String direccionDescarga(String idAlfresco) throws CmisAlfrescoException {
		String workspace = (idAlfresco.split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
    }
	
	public File descargarFile(DocumentoPqua documento) throws CmisAlfrescoException, MalformedURLException, IOException {
		String idDocAlfresco = documento.getIdAlfresco();
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	return alfrescoServiceBean.downloadDocumentByIdFile(idDocAlfresco);        
    }

	public boolean verificarFirmaAlfresco(String idDocAlfresco) {
		File file = null;
		try {
			String url = idDocAlfresco;
			url = (url.split(";"))[0];
			file = alfrescoServiceBean.downloadDocumentByIdFile(url);
			if (verificarFirma(file.getAbsolutePath()))
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

}
