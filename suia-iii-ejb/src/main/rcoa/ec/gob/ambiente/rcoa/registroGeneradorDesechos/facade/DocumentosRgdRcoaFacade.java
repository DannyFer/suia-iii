package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

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
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosRgdRcoaFacade {

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

	public DocumentosRgdRcoa findById(Integer id) {
		try {
			return (DocumentosRgdRcoa) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM DocumentosRgdRcoa o where o.id = :id").setParameter("id", id)
					.getSingleResult();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<DocumentosRgdRcoa> documentoXTablaIdXIdDoc(Integer idTabla, String nombreTabla,
			TipoDocumentoSistema tipoDocumento) {
		List<DocumentosRgdRcoa> listaDocumentos = new ArrayList<DocumentosRgdRcoa>();
		try {

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idTable", idTabla);
			params.put("nombreTabla", nombreTabla);
			params.put("idTipo", tipoDocumento.getIdTipoDocumento());
			listaDocumentos = (List<DocumentosRgdRcoa>) crudServiceBean
					.findByNamedQuery(DocumentosRgdRcoa.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, params);

			for (DocumentosRgdRcoa documento : listaDocumentos) {
				byte[] archivoObtenido = descargar(documento.getIdAlfresco());
				documento.setContenidoDocumento(archivoObtenido);
			}

			return listaDocumentos;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaDocumentos;
	}

	public void save(DocumentosRgdRcoa obj, Usuario usuario) {
		if (obj.getId() == null) {
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
		} else {
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());
		}
		crudServiceBean.saveOrUpdate(obj);
	}

	public DocumentosRgdRcoa guardarDocumento(DocumentosRgdRcoa documento, String nombreProceso,
			TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException {
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		if (documento.getRegistroGeneradorDesechosRcoa().getId() != null) {
			idProces = documento.getRegistroGeneradorDesechosRcoa().getId().toString();
		}

		String modulo = null;
		if (nombreProceso.contains("/")) {
			String[] estructuraCarpetas = nombreProceso.split("/");
			modulo = estructuraCarpetas[estructuraCarpetas.length - 1];
		} else {
			modulo = nombreProceso;
		}

		String folderName = UtilAlfrescoRgd.generarEstructuraCarpetas(
				documento.getRegistroGeneradorDesechosRcoa().getCodigo(), nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName, "Documentos_RCOA");
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-" + documento.getNombre().replace("/", "-"), folderId,
				modulo, folderName);

		DocumentosRgdRcoa documentoAlfrescoGuardar = UtilAlfrescoRgd.crearDocumento(documento.getExtesion(),
				documentCreate.getId(), documento.getMime(), documento.getNombre(), tipoDocumento);
		documentoAlfrescoGuardar.setIdProceso(documento.getIdProceso());
		documentoAlfrescoGuardar.setRegistroGeneradorDesechosRcoa(documento.getRegistroGeneradorDesechosRcoa());
		documentoAlfrescoGuardar.setIdTable(documento.getIdTable());
		documentoAlfrescoGuardar.setNombreTabla(documento.getNombreTabla());

		DocumentosRgdRcoa documentoAlfresco = crudServiceBean.saveOrUpdate(documentoAlfrescoGuardar);

		return documentoAlfresco;
	}

	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
		return archivo;
	}

	public byte[] descargarDocumentoPorNombre(String nombreDocumento) throws CmisAlfrescoException {
		return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
	}

	public byte[] descargarDocumentoPorNombreYDirectorioBase(String nombreDocumento, String directorioBase)
			throws CmisAlfrescoException {
		if (nombreDocumento == null)
			return null;
		if (directorioBase == null)
			directorioBase = Constantes.getRootStaticDocumentsId();
		return alfrescoServiceBean.downloadDocumentByNameAndFolder(nombreDocumento, directorioBase, true);
	}

	public byte[] getBytesFromFile(File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
		long length = file.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;

		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file " + file.getName());
		}

		is.close();
		return bytes;
	}

	public DocumentosRgdRcoa guardarDocumentoAlfrescoSinProyecto(String codigoProyecto, String nombreProceso,
			Long idProceso, DocumentosRgdRcoa documento, TipoDocumentoSistema tipoDocumento)
			throws ServiceException, CmisAlfrescoException {
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

		String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName, "Documentos_RCOA");
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-" + documento.getNombre().replace("/", "-"), folderId,
				modulo, folderName);

		DocumentosRgdRcoa documentoAlfrescoSave = UtilAlfresco.crearDocumentoRGD(documentCreate.getName(),
				documento.getExtesion(), documentCreate.getId(), documento.getIdTable(), documento.getMime(),
				documento.getNombre(), documento.getNombreTabla(), tipoDocumento);
		documentoAlfrescoSave.setIdProceso(documento.getIdProceso());
		documentoAlfrescoSave.setRegistroGeneradorDesechosRcoa(documento.getRegistroGeneradorDesechosRcoa());

		DocumentosRgdRcoa documentoAlfresco = crudServiceBean.saveOrUpdate(documentoAlfrescoSave);

		return documentoAlfresco;

	}

	@SuppressWarnings("unchecked")
	public List<DocumentosRgdRcoa> descargarDocumentoRgd(Integer idTabla, String nombreTabla,
			TipoDocumentoSistema tipoDocumento) {
		List<DocumentosRgdRcoa> listaDocumentos = new ArrayList<DocumentosRgdRcoa>();
		try {

			Query query = crudServiceBean.getEntityManager().createQuery(
					"Select d from DocumentosRgdRcoa d where d.tipoDocumento.id = :idTipo and d.idTable = :idTabla and d.estado = true order by d.fechaCreacion DESC");
			query.setParameter("idTabla", idTabla);
			query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());

			listaDocumentos = query.getResultList();

			for (DocumentosRgdRcoa documento : listaDocumentos) {
				byte[] archivoObtenido = descargar(documento.getIdAlfresco());
				documento.setContenidoDocumento(archivoObtenido);
			}

			return listaDocumentos;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaDocumentos;

	}

	public String direccionDescarga(DocumentosRgdRcoa documento) throws CmisAlfrescoException {
		String workspace = (documento.getIdAlfresco().split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
	}

	public DocumentosRgdRcoa descargarDocumentoUnicoRgd(Integer idTabla, String nombreTabla,
			TipoDocumentoSistema tipoDocumento) {
		List<DocumentosRgdRcoa> listaDocumentos = new ArrayList<DocumentosRgdRcoa>();
		try {

			Query query = crudServiceBean.getEntityManager().createQuery(
					"Select d from DocumentosRgdRcoa d where d.tipoDocumento.id = :idTipo and d.idTable = :idTabla and d.estado = true order by d.fechaCreacion DESC");
			query.setParameter("idTabla", idTabla);
			query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());

			listaDocumentos = query.getResultList();

			DocumentosRgdRcoa documento = new DocumentosRgdRcoa();

			if (listaDocumentos != null && !listaDocumentos.isEmpty()) {
				documento = listaDocumentos.get(0);
				byte[] archivoObtenido = descargar(documento.getIdAlfresco());
				documento.setContenidoDocumento(archivoObtenido);

				return documento;
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

}
