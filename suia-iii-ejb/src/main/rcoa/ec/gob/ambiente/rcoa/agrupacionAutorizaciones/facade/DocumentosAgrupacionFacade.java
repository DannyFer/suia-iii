package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.client.api.Document;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.DocumentoAgrupacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosAgrupacionFacade {

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;

	@EJB
	private CrudServiceBean crudServiceBean;

	public DocumentoAgrupacion guardarDocumento(String codigoProyecto,
			String nombreCarpeta, DocumentoAgrupacion documento, Integer tipoUsuario) throws ServiceException,
			CmisAlfrescoException {

		String modulo = null;
		if (nombreCarpeta.contains("/")) {
			String[] estructuraCarpetas = nombreCarpeta.split("/");
			modulo = estructuraCarpetas[estructuraCarpetas.length - 1];
		} else {
			modulo = nombreCarpeta;
		}

		String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreCarpeta, null);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootId());
		
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-" + documento.getNombre().replace("/", "-"), folderId,
				modulo, folderName);
		
		documento.setTipoUsuario(tipoUsuario);
		documento.setIdAlfresco(documentCreate.getId());
		documento.setDescripcion(documento.getDescripcion() == null ? documentCreate.getName() : documento.getDescripcion());
		
		DocumentoAgrupacion DocumentoAgrupacion = crudServiceBean.saveOrUpdate(documento);

		return DocumentoAgrupacion;

	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoAgrupacion> getDocumentoPorIdTablaTipo(Integer idTabla, Integer tipoDocumento) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTabla", idTabla);
		params.put("idTipo", tipoDocumento);
		List<DocumentoAgrupacion> listaDocumentos = (List<DocumentoAgrupacion>) crudServiceBean.findByNamedQuery(
				DocumentoAgrupacion.GET_POR_ID_TABLA, params);
		return listaDocumentos;
	}
	
	public byte[] descargar(String idDocAlfresco, Date fechaDocumento) throws CmisAlfrescoException {    	
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	byte[] archivo = alfrescoServiceBean.downloadDocumentByIdLectura(idDocAlfresco, fechaDocumento);
        return archivo;
    }
	
	public String direccionDescarga(DocumentoAgrupacion documento) throws CmisAlfrescoException {
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

        } 
		
		return file;
	}
	
}
