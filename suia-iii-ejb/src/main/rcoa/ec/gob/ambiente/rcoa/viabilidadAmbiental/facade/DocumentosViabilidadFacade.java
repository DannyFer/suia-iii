package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.client.api.Document;
import org.jfree.util.Log;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosViabilidadFacade {

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public DocumentoViabilidad guardar(DocumentoViabilidad obj) {
		return crudServiceBean.saveOrUpdate(obj);
	}

	public DocumentoViabilidad guardarDocumentoProceso(String codigoProyecto,
			String nombreCarpeta, DocumentoViabilidad documento, Integer tipoUsuario, Long idProceso) throws ServiceException,
			CmisAlfrescoException {

		String modulo = null;
		if (nombreCarpeta.contains("/")) {
			String[] estructuraCarpetas = nombreCarpeta.split("/");
			modulo = estructuraCarpetas[estructuraCarpetas.length - 1];
		} else {
			modulo = nombreCarpeta;
		}

		String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreCarpeta, null);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootRcoaId());
		
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-" + documento.getNombre().replace("/", "-"), folderId,
				modulo, folderName);
		
		documento.setIdProceso(idProceso);
		documento.setTipoUsuario(tipoUsuario);
		documento.setIdAlfresco(documentCreate.getId());
		documento.setDescripcion(documento.getDescripcion() == null ? documentCreate.getName() : documento.getDescripcion());
		
		DocumentoViabilidad documentoViabilidad = crudServiceBean.saveOrUpdate(documento);

		return documentoViabilidad;

	}
	
	public DocumentoViabilidad guardarDocumento(String codigoProyecto,
			String nombreCarpeta, DocumentoViabilidad documento, Integer tipoUsuario) throws ServiceException,
			CmisAlfrescoException {

		String modulo = null;
		if (nombreCarpeta.contains("/")) {
			String[] estructuraCarpetas = nombreCarpeta.split("/");
			modulo = estructuraCarpetas[estructuraCarpetas.length - 1];
		} else {
			modulo = nombreCarpeta;
		}

		String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreCarpeta, null);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootRcoaId());
		
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-" + documento.getNombre().replace("/", "-"), folderId,
				modulo, folderName);
		
		documento.setTipoUsuario(tipoUsuario);
		documento.setIdAlfresco(documentCreate.getId());
		documento.setDescripcion(documento.getDescripcion() == null ? documentCreate.getName() : documento.getDescripcion());
		
		DocumentoViabilidad documentoViabilidad = crudServiceBean.saveOrUpdate(documento);

		return documentoViabilidad;

	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoViabilidad> getDocumentoPorTipoTramite(Integer idTramite, Integer tipoDocumento) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTramite", idTramite);
		params.put("idTipo", tipoDocumento);
		List<DocumentoViabilidad> listaDocumentos = (List<DocumentoViabilidad>) crudServiceBean.findByNamedQuery(
				DocumentoViabilidad.GET_POR_TIPO_Y_TRAMITE, params);
		return listaDocumentos;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoViabilidad> getDocumentoPorTipoViabilidadTramite(Integer idTramite, Integer tipoDocumento) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTramite", idTramite);
		params.put("idTipo", tipoDocumento);
		List<DocumentoViabilidad> listaDocumentos = (List<DocumentoViabilidad>) crudServiceBean.findByNamedQuery(
				DocumentoViabilidad.GET_POR_TIPOPROYECTOVIABILIDAD, params);
		return listaDocumentos;
	}
	
	public byte[] descargar(String idDocAlfresco, Date fechaDocumento) throws CmisAlfrescoException {    	
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	byte[] archivo = alfrescoServiceBean.downloadDocumentByIdLectura(idDocAlfresco, fechaDocumento);
        return archivo;
    }
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
		return archivo;
	}
	
	public String direccionDescarga(DocumentoViabilidad documento) throws CmisAlfrescoException {
    	String workspace=(documento.getIdAlfresco().split(";"))[0];
        return alfrescoServiceBean.downloadDocumentByObjectId(workspace, documento.getFechaCreacion());
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
	
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	public Integer verificarFirmasAlfresco(String idDocAlfresco) {
        File file = null;
        try {
        	String url = idDocAlfresco;
        	url=(url.split(";"))[0];
        	file = alfrescoServiceBean.downloadDocumentByIdFile(url);
        	PdfReader reader = new PdfReader(file.getAbsolutePath());
            AcroFields af = reader.getAcroFields();
            ArrayList<String> names = af.getSignatureNames();
            return names.size();
        } catch (CmisAlfrescoException e) {
            return 0;
        } catch (IOException e) {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
	public List<DocumentoViabilidad> getDocumentoXTablaIdXIdDoc(Integer idTabla, TipoDocumentoSistema tipoDocumento,String nombreTabla) throws CmisAlfrescoException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTabla", idTabla);
		params.put("idTipo", tipoDocumento.getIdTipoDocumento());
		params.put("nombreTabla", nombreTabla);
		List<DocumentoViabilidad> listaDocumentos = (List<DocumentoViabilidad>) crudServiceBean.findByNamedQuery(
				DocumentoViabilidad.GET_POR_IDTABLA_TIPODOC, params);
		return listaDocumentos;
	}
	
}
