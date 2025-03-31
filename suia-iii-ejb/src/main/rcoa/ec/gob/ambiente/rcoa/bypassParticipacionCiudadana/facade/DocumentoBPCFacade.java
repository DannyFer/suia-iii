package ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.DocumentoBPC;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
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
public class DocumentoBPCFacade {
	
	private static final Logger LOG = Logger.getLogger(DocumentoBPCFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public DocumentoBPC guardar(DocumentoBPC documento){
		return crudServiceBean.saveOrUpdate(documento);
	}
	
	public DocumentoBPC guardarDocumentoAlfresco(String codigoProyecto, String nombreProceso, Long idProceso, DocumentoBPC documento, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException{
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
		
		DocumentoBPC documentoAl = asignarDocumento(documento, documentCreate,  tipoDocumento);
		
		DocumentoBPC documentoAlfreco = guardar(documentoAl);
		return documentoAlfreco;
	}
	public PlantillaReporte obtenerPlantillaReporte(Integer tipoDocumentoId) {
		try {
			Map<String, Object> parametros = new HashMap();
			parametros.put("p_tipoDocumentoId", tipoDocumentoId);

			List<PlantillaReporte> lista = this.crudServiceBean.findByNamedQueryGeneric(
					PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (PlantillaReporte) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public static DocumentoBPC asignarDocumento(DocumentoBPC documento, Document documentCreate, TipoDocumentoSistema tipoDocumentoSistema) {
		String nombre = documento.getNombreDocumento() == null ? documentCreate.getName() : documento.getNombreDocumento(); 
		String extension = documento.getExtencionDocumento();
		String idAlfresco = documentCreate.getId();
		Integer idTable = documento.getIdTabla(); 
		String nombreTabla = documento.getNombreTabla();
		String mimeDocumento = documento.getMimeDocumento();
		String descripcionTabla = documento.getDescripcionTabla();
		Long idProceso = documento.getIdProceso();
		
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentoBPC documentoTemp = new DocumentoBPC();
		documentoTemp.setEstado(true);
		documentoTemp.setIdProceso(idProceso);
		documentoTemp.setNombreDocumento(nombre);
		documentoTemp.setExtencionDocumento(extension);
		documentoTemp.setIdAlfresco(idAlfresco);
		documentoTemp.setIdTabla(idTable);
		documentoTemp.setNombreTabla(nombreTabla);
		documentoTemp.setMimeDocumento(mimeDocumento);
		documentoTemp.setDescripcionTabla(descripcionTabla);
		documentoTemp.setTipoDocumento(tipoDocumento);
		return documentoTemp;
	}

	public DocumentoBPC getdocumento(Integer id) {
		DocumentoBPC result = new DocumentoBPC();			
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT dd FROM DocumentoBPC dd WHERE dd.id=:id ORDER BY dd.id desc");
		sql.setParameter("id", id);
		if (sql.getResultList().size() > 0)
		{
			result = (DocumentoBPC) sql.getResultList().get(0);
		}
		else
		{
			result = null;
		}
		return result;
    }
	
	public List<DocumentoBPC> documentoXTablaId(Integer idTabla, String nombreTabla) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTabla", idTabla);
		params.put("nombreTabla", nombreTabla);
		@SuppressWarnings("unchecked")
		List<DocumentoBPC> listaDocumentos = (List<DocumentoBPC>) crudServiceBean.findByNamedQuery(DocumentoBPC.LISTAR_POR_ID_NOMBRE_TABLA, params);
		return listaDocumentos;
    }
	
	public List<DocumentoBPC> documentoXTablaIdXIdDoc(Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTabla", idTabla);
		params.put("nombreTabla", nombreTabla);
		params.put("idTipo", tipoDocumento.getIdTipoDocumento());
		List<DocumentoBPC> listaDocumentos = (List<DocumentoBPC>) crudServiceBean.findByNamedQuery(DocumentoBPC.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, params);
		return listaDocumentos;
	}
	
	public DocumentoBPC documentoXTablaIdXIdDocUnico(Integer idTabla, String nombreTabla,TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idTabla", idTabla);
		params.put("nombreTabla", nombreTabla);
		params.put("idTipo", tipoDocumento.getIdTipoDocumento());
		List<DocumentoBPC> listaDocumentos = (List<DocumentoBPC>) crudServiceBean.findByNamedQuery(DocumentoBPC.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, params);
    	if(listaDocumentos == null || listaDocumentos.isEmpty() || listaDocumentos.get(0).getIdAlfresco() == null || listaDocumentos.get(0).getIdAlfresco().isEmpty())
    		return null;
    	return listaDocumentos.get(0);
    }
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {    	
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
        return archivo;
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
	
	public String obtenerUrlAlfresco(String idAlfresco) {
		try {
			return alfrescoServiceBean.downloadDocumentByObjectId(idAlfresco);
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		}
		return null;
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
	
	@SuppressWarnings("unchecked")
	public Integer documentoNumeroVersiones(Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) {
		Integer version=0;
		try {
			List<DocumentoBPC> result = new ArrayList<DocumentoBPC>();			
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT dd FROM DocumentoBPC dd WHERE dd.idTabla=:tablaId and dd.nombreTabla=:strTabla and dd.tipoDocumento.id=:idTipo ORDER BY dd.id desc");
			sql.setParameter("tablaId", idTabla);
			sql.setParameter("strTabla", nombreTabla);
			sql.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());
			if (sql.getResultList().size() > 0)
			{
				result = (List<DocumentoBPC>)sql.getResultList();
			}
			version = result.size() + 1;
			return version;
			//return version;
		} catch (Exception e) {
			return 0;
		}
	}
	
	public DocumentoBPC getDocumento(DocumentoBPC documento) {
		try {
			DocumentoBPC result = new DocumentoBPC();			
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT dd FROM DocumentoBPC dd WHERE dd.id=:idDocumento ORDER BY dd.id desc");
			sql.setParameter("idDocumento", documento.getId());
			if (sql.getResultList().size() > 0)
			{
				result = (DocumentoBPC)sql.getResultList().get(0);
			}
			else
			{
				result = null;
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public DocumentoBPC documentoXTablaIdXIdDocumento(Integer idTabla, String nombreTabla,String descDocumento) {
		DocumentoBPC result = new DocumentoBPC();			
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT dd FROM DocumentoBPC dd WHERE dd.idTabla=:tablaId and dd.nombreTabla=:strTabla and dd.descripcionTabla=:strTipo ORDER BY dd.id desc");
		sql.setParameter("tablaId", idTabla);
		sql.setParameter("strTabla", nombreTabla);
		sql.setParameter("strTipo", descDocumento);
		if (sql.getResultList().size() > 0)
		{
			result = (DocumentoBPC) sql.getResultList().get(0);
		}
		else
		{
			result = null;
		}
		return result;
    }
	
	public String direccionDescarga(DocumentoBPC documento) throws CmisAlfrescoException {
		String workspace = (documento.getIdAlfresco().split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace, documento.getFechaCreacion());
	}	
	
	
	public Integer eliminarDocumentos(DocumentoBPC documento) {
		try {
			guardar(documento);
			return 1;
		} catch (Exception e) {
			return 0;
		}		
	}	
	
}
