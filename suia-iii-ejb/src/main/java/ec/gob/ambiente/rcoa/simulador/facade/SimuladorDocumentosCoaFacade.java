package ec.gob.ambiente.rcoa.simulador.facade;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.model.DocumentoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorDocumentosCOA;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class SimuladorDocumentosCoaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public SimuladorDocumentosCOA guardar(SimuladorDocumentosCOA obj)
	{
		return crudServiceBean.saveOrUpdate(obj);
	}
	
//	public SimuladorDocumentosCOA guardarDocumentoAlfresco(String codigoProyecto, String nombreProceso, Long idProceso, SimuladorDocumentosCOA documento, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException{
//		
//		nombreProceso = nombreProceso.replace(".", "_");
//		String idProces = null;
//		if (idProceso != null) {
//			idProces = idProceso.toString();
//		}
//		
//		String folderName = UtilAlfresco.generarEstructuraCarpetas(
//				codigoProyecto, nombreProceso, idProces);
//		String folderId = alfrescoServiceBean.createFolderStructure(folderName,
//				Constantes.getRootRcoaId());
//		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento
//				.getContenidoDocumento(),
//				Calendar.getInstance().getTimeInMillis() + "-"
//						+ documento.getNombreDocumento().replace("/", "-"), folderId,
//				nombreProceso, folderName, documento.getIdTabla());
//		
//		SimuladorDocumentosCOA documentoAl = UtilAlfresco.crearDocumentoCOA(documento.getNombreDocumento() == null ? documentCreate.getName()
//				: documento.getNombreDocumento(), documento.getExtencionDocumento(),
//				documentCreate.getId(), documento.getIdTabla(), documento.getTipo(),
//						 documento.getNombreDocumento(), documento.getNombreTabla(), tipoDocumento);
//		documentoAl.setIdProceso(documento.getIdProceso());
//		
//		SimuladorDocumentosCOA documentoAlfreco = crudServiceBean.saveOrUpdate(documentoAl);
//		
//		
//		return documentoAlfreco;
//	}
	
	@SuppressWarnings("unchecked")
	public List<SimuladorDocumentosCOA> documentoXTablaIdXIdDoc(Integer idTabla, TipoDocumentoSistema tipoDocumento,String nombreTabla) throws CmisAlfrescoException {

		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM SimuladorDocumentosCOA d WHERE d.idTabla = :idTabla AND d.nombreTabla=:nombreTabla AND "
						+ "d.tipoDocumento.id = :idTipo AND d.estado = true order by d.id DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());
		query.setParameter("nombreTabla", nombreTabla);
						
		List<SimuladorDocumentosCOA> listaDocumentos = (List<SimuladorDocumentosCOA>) query.getResultList();
		
//		for(DocumentosCOA documento : listaDocumentos){
//			byte[] archivoObtenido = descargar(documento.getIdAlfresco());
//			documento.setContenidoDocumento(archivoObtenido);
//		}				
		return listaDocumentos;
	}
	
	public String direccionDescarga(SimuladorDocumentosCOA documento) throws CmisAlfrescoException {
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
	
	public byte[] descargarDocumentoPorNombre(String nombreDocumento) throws CmisAlfrescoException {
		return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
	}
	
	public String direccionDescarga(String idAlfresco) throws CmisAlfrescoException {
		String workspace = (idAlfresco.split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
    }
	
	public File descargarFile(SimuladorDocumentosCOA documento) throws CmisAlfrescoException, MalformedURLException, IOException {
		String idDocAlfresco = documento.getIdAlfresco();
    	idDocAlfresco=(idDocAlfresco.split(";"))[0];
    	return alfrescoServiceBean.downloadDocumentByIdFile(idDocAlfresco);        
    }

}
