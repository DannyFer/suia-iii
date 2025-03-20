package ec.gob.ambiente.suia.recaudaciones.facade;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.recaudaciones.model.DocumentoNUT;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosNUTFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public DocumentoNUT guardar(DocumentoNUT obj)
	{
		return crudServiceBean.saveOrUpdate(obj);
	}
	
	public DocumentoNUT guardarDocumentoAlfresco(String codigoProyecto, String nombreProceso, Long idProceso, DocumentoNUT documento, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException{
		
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
				nombreProceso, folderName, documento.getIdTabla());
		
		DocumentoNUT documentoAl =crearDocumento(documento
				.getDescripcion() == null ? documentCreate.getName()
				: documento.getDescripcion(), documento.getExtesion(),
				documentCreate.getId(), documento.getIdTabla(), documento
						.getMime(), documento.getNombre(), documento
						.getNombreTabla(), documento.getSolicitudId(), tipoDocumento, documento.getIdProceso());
		documentoAl.setCodigoPublico(documento.getCodigoPublico());
		
		DocumentoNUT documentoAlfreco = crudServiceBean.saveOrUpdate(documentoAl);
		return documentoAlfreco;
	}
	public static DocumentoNUT crearDocumento(String descripcion, String extesion, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, Integer solicitudId, TipoDocumentoSistema tipoDocumentoSistema, Long idProceso) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentoNUT documento = new DocumentoNUT();
		documento.setDescripcion(descripcion);
		documento.setEstado(true);
		documento.setExtesion(extesion);
		documento.setAlfrescoId(idAlfresco);
		documento.setIdTabla(idTable);
		documento.setMime(mime);
		documento.setNombre(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setSolicitudId(solicitudId);
		documento.setTipoDocumento(tipoDocumento);
		documento.setIdProceso(idProceso);

		return documento;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoNUT> documentoXTablaIdXIdDoc(Integer idTabla, TipoDocumentoSistema tipoDocumento,String nombreTabla) throws CmisAlfrescoException {

		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM DocumentoNUT d WHERE d.idTabla = :idTabla AND d.nombreTabla=:nombreTabla AND "
						+ "d.tipoDocumento.id = :idTipo AND d.estado = true order by d.id DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());
		query.setParameter("nombreTabla", nombreTabla);
						
		List<DocumentoNUT> listaDocumentos = (List<DocumentoNUT>) query.getResultList();
					
		return listaDocumentos;
	}
	
	public String direccionDescarga(DocumentoNUT documento) throws CmisAlfrescoException {
		String workspace = (documento.getAlfrescoId().split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
    }
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {    	
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
        return archivo;
    }

	
	public byte[] descargarDocumentoPorNombre(String nombreDocumento) throws CmisAlfrescoException {
		return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
	}
	
	public String direccionDescarga(String idAlfresco) throws CmisAlfrescoException {
		String workspace = (idAlfresco.split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
    }

    @SuppressWarnings("unchecked")
	public List<DocumentoNUT> listaPorNutConProcesoAsignadoSinFiltros(Integer idNut) {

		Query sql= crudServiceBean.getEntityManagerWithOutFilters().createQuery("select d from NumeroUnicoTransaccional n "
				+ " inner join n.solicitudUsuario s, DocumentoNUT d "
				+ " where s.id = d.solicitudId "
				+ " and d.estado= true and "
				+ " n.id=:idNut and d.idProceso is not null "
				+ " order by n.id desc " );
		sql.setParameter("idNut", idNut);
						
		List<DocumentoNUT> listaDocumentos = (List<DocumentoNUT>) sql.getResultList();
					
		return listaDocumentos;
	}
}