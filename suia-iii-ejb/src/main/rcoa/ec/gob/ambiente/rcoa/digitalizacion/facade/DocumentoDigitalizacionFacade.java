package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.digitalizacion.model.DocumentoDigitalizacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentoDigitalizacionFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public DocumentoDigitalizacion guardar(DocumentoDigitalizacion objDocumento) {        	
    	return crudServiceBean.saveOrUpdate(objDocumento);        
	}
	
	public DocumentoDigitalizacion findById(Integer id){
		try {
			return (DocumentoDigitalizacion) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM DocumentosDigitalizacion o where o.id = :id").setParameter("id", id)
					.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void eliminarDocumentosReemplazados(Integer idTipoDocumento, Integer idDigitalizacion, String usuario){	
		try{
		String sql =" update coa_digitalization_linkage.documents "
				+ " set docu_status = false, docu_date_update = now(), docu_user_update = '"+usuario+"' "
				+ " where enaa_id = "+idDigitalizacion+" and docu_status = true and doty_id = "+idTipoDocumento+" ;";
		crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		}catch(Exception e){
			System.out.println("Error al eliminar los planes");;
		}
	}
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
		return archivo;
	}
		
	@SuppressWarnings("unchecked")
	public List<DocumentoDigitalizacion> descargarDocumentoDigitalizacion(Integer idTabla, String nombreTabla,
			TipoDocumentoSistema tipoDocumento) {
		List<DocumentoDigitalizacion> listaDocumentos = new ArrayList<DocumentoDigitalizacion>();
		try {

			Query query = crudServiceBean.getEntityManager().createQuery(
					"Select d from DocumentoDigitalizacion d where d.tipoDocumento.id = :idTipo and d.idTabla = :idTabla and d.estado = true order by d.fechaCreacion DESC");
			query.setParameter("idTabla", idTabla);
			query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());

			listaDocumentos = query.getResultList();

			for (DocumentoDigitalizacion documento : listaDocumentos) {
				byte[] archivoObtenido = descargar(documento.getIdAlfresco());
				documento.setContenidoDocumento(archivoObtenido);
			}

			return listaDocumentos;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaDocumentos;

	}
	
	public static DocumentoDigitalizacion crearDocumento(String descripcion, String extension, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema, Integer tipoIngreso) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());
		DocumentoDigitalizacion documento = new DocumentoDigitalizacion();
		documento.setDescripcion(descripcion);
		documento.setEstado(true);
		documento.setExtension(extension);;
		documento.setIdAlfresco(idAlfresco);
		documento.setIdTabla(idTable);
		documento.setMime(mime);
		documento.setNombre(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setTipoDocumento(tipoDocumento);
		documento.setTipoIngreso(tipoIngreso);

		return documento;
	}
	
	public DocumentoDigitalizacion guardarDocumentoAlfresco(String codigoProyecto, String nombreProceso,
			Long idProceso, DocumentoDigitalizacion documento, TipoDocumentoSistema tipoDocumento)
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

		DocumentoDigitalizacion documentoAlfrescoSave = crearDocumento(documentCreate.getName(),
				documento.getExtension(), documentCreate.getId(), documento.getIdTabla(), documento.getMime(),
				documento.getNombre(), documento.getNombreTabla(), tipoDocumento, documento.getTipoIngreso());
		
		documentoAlfrescoSave.setAutorizacionAdministrativaAmbiental(documento.getAutorizacionAdministrativaAmbiental());

		DocumentoDigitalizacion documentoAlfresco = crudServiceBean.saveOrUpdate(documentoAlfrescoSave);

		return documentoAlfresco;

	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoDigitalizacion> descargarDocumentoFisico(Integer idTabla, String nombreTabla,
			TipoDocumentoSistema tipoDocumento) {
		List<DocumentoDigitalizacion> listaDocumentos = new ArrayList<DocumentoDigitalizacion>();
		try {

			Query query = crudServiceBean.getEntityManager().createQuery(
					"Select d from DocumentoDigitalizacion d where d.tipoDocumento.id = :idTipo and d.idTabla = :idTabla and d.estado = true order by d.fechaCreacion DESC");
			query.setParameter("idTabla", idTabla);
			query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());

			listaDocumentos = query.getResultList();

			for (DocumentoDigitalizacion documento : listaDocumentos) {
				byte[] archivoObtenido = descargar(documento.getIdAlfresco());
				documento.setContenidoDocumento(archivoObtenido);
			}

			return listaDocumentos;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaDocumentos;

	}
	
//	public byte[] descargarPorNombre(String nombre) throws CmisAlfrescoException {		
//		byte[] archivo = alfrescoServiceBean.downloadDocumentByName(nombre);
//		alfrescoServiceBean.downloadDocumentByNameAndFolder(documentName, documentFolder, inTree, date)
//		return archivo;
//	}
	
	public String getUrl(String idDocumento, String fecha) throws ParseException{
		String urlString="";
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/mm/dd");
		fecha = fecha.replace("-", "/");
		Date fechaAux = formato.parse(fecha);
		try {
			urlString = alfrescoServiceBean.getWorkspaceByObjectId(idDocumento, fechaAux);
		} catch (CmisAlfrescoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlString;
	}
}
