package ec.gob.ambiente.rcoa.facade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.model.DocumentoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosCertificadoAmbientalFacade {
	
	private static final Logger LOG = Logger.getLogger(DocumentosCertificadoAmbientalFacade.class);
	
	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public void ingresarDocumentoCategoriaII(File file, Integer id,
			String codProyecto, long idProceso, long idTarea,
			TipoDocumentoSistema tipoDocumento, String nombreDocumento)
			throws Exception {

		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		DocumentoCertificadoAmbiental documento1 = new DocumentoCertificadoAmbiental();
		documento1.setIdTable(id);
		documento1.setNombre(nombreDocumento + ".pdf");
		documento1.setExtesion(mimeTypesMap.getContentType(file));
		documento1.setNombreTabla(ProyectoCertificadoAmbiental.class.getSimpleName());
		documento1.setMime(mimeTypesMap.getContentType(file));
		documento1.setContenidoDocumento(data);
		documento1.setIdProceso(idProceso);

		guardarDocumentoAlfrescoCA(codProyecto,
				"CertificadoAmbiental", idProceso, documento1,
				tipoDocumento);
	}

	public byte[] descargarDocumentoPorNombre(String nombreDocumento) throws CmisAlfrescoException {
		return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
	}
	
	public DocumentoCertificadoAmbiental guardarDocumentoAlfrescoCA(String codigoProyecto,
			String nombreProceso, Long idProceso, DocumentoCertificadoAmbiental documento,
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

		DocumentoCertificadoAmbiental documentoAl = UtilAlfresco.crearDocumentoCA(documento
				.getDescripcion() == null ? documentCreate.getName()
				: documento.getDescripcion(), documento.getExtesion(),
				documentCreate.getId(), documento.getIdTable(), documento
						.getMime(), documento.getNombre(), documento
						.getNombreTabla(), tipoDocumento);
		documentoAl.setCodigoPublico(documento.getCodigoPublico());
		documentoAl.setIdProceso(idProceso);
		DocumentoCertificadoAmbiental documentoAlfresco = crudServiceBean.saveOrUpdate(documentoAl);
		return documentoAlfresco;

	}
	
	public String direccionDescarga(DocumentoCertificadoAmbiental documento)
			throws CmisAlfrescoException {
		String workspace = (documento.getAlfrescoId().split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoCertificadoAmbiental> documentoXTablaIdXIdDoc(Integer idTabla,
			String nombreTabla, TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException {

		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM DocumentoCertificadoAmbiental d WHERE d.nombreTabla = :nombreTabla AND d.idTable = :idTabla AND "
						+ "d.tipoDocumento.id = :idTipo AND d.estado = true order by 1 DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("nombreTabla", nombreTabla);
		query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());	
		query.setMaxResults(1);
						
		List<DocumentoCertificadoAmbiental> listaDocumentos = (List<DocumentoCertificadoAmbiental>) query.getResultList();
		
		for(DocumentoCertificadoAmbiental documento : listaDocumentos){
			byte[] archivoObtenido = descargar(documento.getAlfrescoId());
			documento.setContenidoDocumento(archivoObtenido);
		}				
		return listaDocumentos;
	}
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {

		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);

		return archivo;
	}
	
	public DocumentoCertificadoAmbiental guardar(DocumentoCertificadoAmbiental doc) {
		DocumentoCertificadoAmbiental documentoAlfresco = crudServiceBean.saveOrUpdate(doc);
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
	
	public String descargarDireccionDocumentoPorNombre(String nombreDocumento) throws CmisAlfrescoException {
		return alfrescoServiceBean.downloadDocument(nombreDocumento);
	}
	
	
}
