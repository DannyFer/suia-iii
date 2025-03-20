package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.io.File;
import java.io.FileInputStream;
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
import org.apache.log4j.Logger;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.UtilAlfrescoRgd;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosSustanciasQuimicasRcoaFacade {
	
	private static final Logger LOG = Logger.getLogger(DocumentosSustanciasQuimicasRcoaFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;

	public DocumentosSustanciasQuimicasRcoa findById(Integer id) {
		try {

			DocumentosSustanciasQuimicasRcoa documento = (DocumentosSustanciasQuimicasRcoa) crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT o FROM DocumentosSustanciasQuimicasRcoa o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return documento;

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public List<DocumentosSustanciasQuimicasRcoa> documentoXTablaIdXIdDoc(Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTipo", tipoDocumento.getIdTipoDocumento());
		List<DocumentosSustanciasQuimicasRcoa> listaDocumentos = (List<DocumentosSustanciasQuimicasRcoa>) crudServiceBean.findByNamedQuery(
				DocumentosSustanciasQuimicasRcoa.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, params);
		return listaDocumentos;
	}
	
	public void save(DocumentosSustanciasQuimicasRcoa obj, Usuario usuario) {
		if (obj.getId() == null) {
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
		} else {
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());
		}
		crudServiceBean.saveOrUpdate(obj);
	}

	public DocumentosSustanciasQuimicasRcoa guardarDocumento(DocumentosSustanciasQuimicasRcoa documento,
			String nombreProceso, TipoDocumentoSistema tipoDocumento)
			throws ServiceException, CmisAlfrescoException {
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		if (documento.getGestionarProductosQuimicosProyectoAmbiental().getId() != null) {
			idProces = documento.getGestionarProductosQuimicosProyectoAmbiental().getId()
					.toString();
		}

		String modulo = null;
		if (nombreProceso.contains("/")) {
			String[] estructuraCarpetas = nombreProceso.split("/");
			modulo = estructuraCarpetas[estructuraCarpetas.length - 1];
		} else {
			modulo = nombreProceso;
		}

		String folderName = UtilAlfrescoRgd.generarEstructuraCarpetas(documento
				.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getCodigoOnu(), nombreProceso,
				idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,
				"Documentos-RCOA");
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento
				.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-"
						+ documento.getNombre().replace("/", "-"), folderId,
				modulo, folderName);

		DocumentosSustanciasQuimicasRcoa documentoAlfresco = crudServiceBean
				.saveOrUpdate(UtilAlfrescoSustanciaQuimica.crearDocumento(
						documento.getExtesion(), documentCreate.getId(),
						documento.getMime(), documento.getNombre(),
						tipoDocumento));

		return documentoAlfresco;
	}

	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		byte[] archivo = alfrescoServiceBean
				.downloadDocumentById(idDocAlfresco);
		return archivo;
	}

	public byte[] descargarDocumentoPorNombre(String nombreDocumento)
			throws CmisAlfrescoException {
		return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
	}

	public byte[] descargarDocumentoPorNombreYDirectorioBase(
			String nombreDocumento, String directorioBase)
			throws CmisAlfrescoException {
		if (nombreDocumento == null)
			return null;
		if (directorioBase == null)
			directorioBase = Constantes.getRootStaticDocumentsId();
		return alfrescoServiceBean.downloadDocumentByNameAndFolder(
				nombreDocumento, directorioBase, true);
	}

	public byte[] getBytesFromFile(File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
		long length = file.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;

		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		is.close();
		return bytes;
	}
	
	public DocumentosSustanciasQuimicasRcoa guardarDocumentoAlfrescoSinProyecto(String codigoProyecto,
			String nombreProceso, Long idProceso, DocumentosSustanciasQuimicasRcoa documento,
			TipoDocumentoSistema tipoDocumento,
			DocumentosTareasProceso documentoTarea) throws ServiceException,
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
				Constantes.getRootId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento
				.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-"
						+ documento.getNombre().replace("/", "-"), folderId,
				modulo, folderName);

		DocumentosSustanciasQuimicasRcoa documentoAlfresco = crudServiceBean.saveOrUpdate(UtilAlfresco
				.crearDocumentoSustanciasQuimicas(documentCreate.getName(),
						documento.getExtesion(), documentCreate.getId(),
						documento.getIdTable(), documento.getMime(),
						documento.getNombre(), documento.getNombreTabla(),
						tipoDocumento));
		return documentoAlfresco;

	}
	
	public String direccionDescarga(DocumentosSustanciasQuimicasRcoa documento)
			throws CmisAlfrescoException {
		String workspace = (documento.getIdAlfresco().split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
	}
	
	public DocumentosSustanciasQuimicasRcoa guardarDocumentoAlfrescoImportacion(String codigoProyecto,
			String nombreProceso, Long idProceso, DocumentosSustanciasQuimicasRcoa documento,
			TipoDocumentoSistema tipoDocumento) throws ServiceException,
			CmisAlfrescoException {
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
						+ documento.getNombre().replace("/", "-"), folderId,
				nombreProceso, folderName, documento.getIdTable());

		DocumentosSustanciasQuimicasRcoa documentoAl = UtilAlfresco.crearDocumentoSustanciasQuimicas(documento
				.getDescripcion() == null ? documentCreate.getName()
				: documento.getDescripcion(), documento.getExtesion(),
				documentCreate.getId(), documento.getIdTable(), documento
						.getMime(), documento.getNombre(), documento
						.getNombreTabla(), tipoDocumento);		
		
		DocumentosSustanciasQuimicasRcoa documentoAlfresco = crudServiceBean.saveOrUpdate(documentoAl);
		return documentoAlfresco;

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
	
	public DocumentosSustanciasQuimicasRcoa guardar(DocumentosSustanciasQuimicasRcoa doc) {
		DocumentosSustanciasQuimicasRcoa documentoAlfresco = crudServiceBean.saveOrUpdate(doc);
		return documentoAlfresco;
	}
	
	public List<DocumentosSustanciasQuimicasRcoa> documentoXIdTablaIdTipoDoc(Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) {
		List<DocumentosSustanciasQuimicasRcoa> documentoList = (List<DocumentosSustanciasQuimicasRcoa>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT o FROM DocumentosSustanciasQuimicasRcoa o where o.idTable = :id and o.estado = true and o.tipoDocumento.id = :idTipoDocumento order by 1 desc")
				.setParameter("id", idTabla).setParameter("idTipoDocumento", tipoDocumento.getIdTipoDocumento()).getResultList();
		return documentoList;
	}
	
}
