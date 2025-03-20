package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosRSQFacade {
	
	private static final Logger LOG = Logger.getLogger(InformesOficiosRSQFacade.class);	

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public static String NOMBRE_CARPETA="REGISTRO_SUSTANCIAS_QUIMICAS";

	public DocumentosSustanciasQuimicasRcoa guardarDocumento(String codigoProyecto,
			String nombreCarpeta, DocumentosSustanciasQuimicasRcoa documento) throws ServiceException,
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
		
		//documento.setTipoUsuario(tipoUsuario);
		documento.setIdAlfresco(documentCreate.getId());
		documento.setDescripcion(documento.getDescripcion() == null ? documentCreate.getName() : documento.getDescripcion());
		
		DocumentosSustanciasQuimicasRcoa documentosSustanciasQuimicasRcoa = crudServiceBean.saveOrUpdate(documento);

		return documentosSustanciasQuimicasRcoa;

	}
	
	public DocumentosSustanciasQuimicasRcoa guardarDocumento(String codigoProyecto, DocumentosSustanciasQuimicasRcoa documento,Integer idTabla) throws ServiceException,
			CmisAlfrescoException {
		documento.setIdTable(idTabla);
		return guardarDocumento(codigoProyecto, NOMBRE_CARPETA, documento);

	}
	
	public DocumentosSustanciasQuimicasRcoa obtenerDocumentoPorTipo(TipoDocumentoSistema tipo,String nombreTabla, Integer idTabla) {
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT o FROM DocumentosSustanciasQuimicasRcoa o "
					+ "WHERE o.estado=true and o.tipoDocumento.id=:idTipo and o.nombreTabla=:nombreTabla and o.idTable=:idTabla ORDER BY 1 DESC");
			query.setParameter("idTipo", tipo.getIdTipoDocumento());
			query.setParameter("nombreTabla", nombreTabla);
			query.setParameter("idTabla",idTabla);
			
			query.setMaxResults(1);			
			return (DocumentosSustanciasQuimicasRcoa)query.getSingleResult();
		}catch (NoResultException e) {
			// TODO: handle exception
		}catch (Exception e) {
			LOG.error(e.getMessage());
		}		
		return null;
	}
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {    	
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
        return archivo;
    }
	
	public byte[] descargarPorNombre(String nombreDocumento) throws CmisAlfrescoException {          
        //byte[] archivo= alfrescoServiceBean.downloadDocumentByNameAndFolder(nombreDocumento, Constantes.getRootStaticDocumentsId(), true);
		byte[] archivo= alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
		return archivo;
    }
	
	public String direccionDescarga(String idDocAlfresco) throws CmisAlfrescoException {
		idDocAlfresco=(idDocAlfresco.split(";"))[0];
        return alfrescoServiceBean.downloadDocumentByObjectId(idDocAlfresco);
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
	public List<DocumentosSustanciasQuimicasRcoa> obtenerListDocumentoPorTipo(TipoDocumentoSistema tipo,String nombreTabla, Integer idTabla) {
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT o FROM DocumentosSustanciasQuimicasRcoa o "
					+ "WHERE o.estado=true and o.tipoDocumento.id=:idTipo and o.nombreTabla=:nombreTabla and o.idTable=:idTabla ORDER BY 1 DESC");
			query.setParameter("idTipo", tipo.getIdTipoDocumento());
			query.setParameter("nombreTabla", nombreTabla);
			query.setParameter("idTabla",idTabla);
			
			List<DocumentosSustanciasQuimicasRcoa> lista = (List<DocumentosSustanciasQuimicasRcoa>)query.getResultList();
			
			for(DocumentosSustanciasQuimicasRcoa doc : lista){
				if(doc.getIdAlfresco() != null){
					try{
						doc.setContenidoDocumento(descargar(doc.getIdAlfresco()));
					}catch(Exception e){						
					}
				}					
			}
			
			return lista;
		}catch (NoResultException e) {
			// TODO: handle exception
		}catch (Exception e) {
			LOG.error(e.getMessage());
		}		
		return null;
	}
	
	public DocumentosSustanciasQuimicasRcoa obtenerDocumentoPorTipoIdTabla(TipoDocumentoSistema tipo,String nombreTabla, Integer idTabla) {
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT o FROM DocumentosSustanciasQuimicasRcoa o "
					+ "WHERE o.estado=true and o.tipoDocumento.id=:idTipo and o.nombreTabla=:nombreTabla and o.idTable=:idTabla ORDER BY 1 DESC");
			query.setParameter("idTipo", tipo.getIdTipoDocumento());
			query.setParameter("nombreTabla", nombreTabla);
			query.setParameter("idTabla",idTabla);
			
			query.setMaxResults(1);			
			
			DocumentosSustanciasQuimicasRcoa documento = new DocumentosSustanciasQuimicasRcoa();
			documento = (DocumentosSustanciasQuimicasRcoa)query.getSingleResult();
			
			if(documento.getIdAlfresco() != null){
				try{
					documento.setContenidoDocumento(descargar(documento.getIdAlfresco()));
				}catch(Exception e){						
				}
			}	
			
			return documento;
			
		}catch (NoResultException e) {
			// TODO: handle exception
		}catch (Exception e) {
			LOG.error(e.getMessage());
		}		
		return null;
	}
	
	public void guardar(DocumentosSustanciasQuimicasRcoa documento){
		crudServiceBean.saveOrUpdate(documento);
	}
	
	
	
}
