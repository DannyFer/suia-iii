package ec.gob.ambiente.rcoa.facade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosRegistroAmbientalFacade {
	
	private static final Logger LOG = Logger.getLogger(DocumentosRegistroAmbientalFacade.class);
	
	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public DocumentoRegistroAmbiental guardar(DocumentoRegistroAmbiental documento) {        	
    	return crudServiceBean.saveOrUpdate(documento);        
	}
	
	public DocumentoRegistroAmbiental ingresarDocumento(DocumentoRegistroAmbiental  documentoRegistro, Integer id,
			String codProyecto, TipoDocumentoSistema tipoDocumento, String nombreDocumento, String clase, Long idProceso)
			throws Exception {
		String extension = "";
		String idProces = null;
		if (idProceso != null) {
			idProces = idProceso.toString();
		}
		int i = nombreDocumento.lastIndexOf('.');
		if (i > 0) {
		    extension = nombreDocumento.substring(i+1);
		}
		byte[] data = documentoRegistro.getContenidoDocumento();
		DocumentoRegistroAmbiental documento1 = new DocumentoRegistroAmbiental();
		documento1.setIdTable(id);
		documento1.setNombre(nombreDocumento);
		documento1.setMime("application/pdf");
		if(documento1.getExtesion() != null && documento1.getExtesion().equals("mp3")){
			documento1.setMime("audio/mpeg3");
		}
		documento1.setExtesion("."+extension);
		documento1.setNombreTabla(clase);
		documento1.setContenidoDocumento(data);
		documento1.setIdProceso(idProceso);
		documento1 = guardarDocumentoAlfrescoCA(codProyecto, "REGISTRO_AMBIENTAL", documento1, tipoDocumento);
		return documento1;
	}
	
	public byte[] descargarDocumentoPorNombre(String nombreDocumento) throws CmisAlfrescoException {
		return alfrescoServiceBean.downloadDocumentByName(nombreDocumento);
	}
	
	public DocumentoRegistroAmbiental guardarDocumentoAlfrescoCA(String codigoProyecto,
			String nombreProceso, DocumentoRegistroAmbiental documento,
			TipoDocumentoSistema tipoDocumento) throws ServiceException,
			CmisAlfrescoException {
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		String folderName = UtilAlfresco.generarEstructuraCarpetas(
				codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,
				Constantes.getRootRcoaId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento
				.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-"
						+ documento.getNombre().replace("/", "-"), folderId,
				nombreProceso, folderName, documento.getIdTable());

		DocumentoRegistroAmbiental documentoAl = crearDocumento(documento
				.getDescripcion() == null ? documentCreate.getName()
				: documento.getDescripcion(), documento.getExtesion(),
				documentCreate.getId(), documento.getIdTable(), documento
						.getMime(), documento.getNombre(), documento
						.getNombreTabla(), tipoDocumento, documento.getIdProceso());
		documentoAl.setCodigoPublico(documento.getCodigoPublico());
		DocumentoRegistroAmbiental documentoAlfresco = crudServiceBean.saveOrUpdate(documentoAl);
		return documentoAlfresco;
	}
	
	public static DocumentoRegistroAmbiental crearDocumento(String descripcion, String extesion, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema, Long idProceso) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentoRegistroAmbiental documento = new DocumentoRegistroAmbiental();
		documento.setDescripcion(descripcion);
		documento.setEstado(true);
		documento.setExtesion(extesion);
		documento.setAlfrescoId(idAlfresco);
		documento.setIdTable(idTable);
		documento.setMime(mime);
		documento.setNombre(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setTipoDocumento(tipoDocumento);
		documento.setIdProceso(idProceso);

		return documento;
	}
	
	public String direccionDescarga(DocumentoRegistroAmbiental documento)
			throws CmisAlfrescoException {
		String workspace = (documento.getAlfrescoId().split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(workspace);
	}
	
	@SuppressWarnings("unchecked")
	public DocumentoRegistroAmbiental documentoXTablaIdXIdDoc(Integer idTabla,
			String nombreTabla, TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException {

		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM DocumentoRegistroAmbiental d WHERE d.nombreTabla = :nombreTabla AND d.idTable = :idTabla AND "
						+ "d.tipoDocumento.id = :idTipo AND d.estado = true order by d.fechaCreacion DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("nombreTabla", nombreTabla);
		query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());						
						
		List<DocumentoRegistroAmbiental> listaDocumentos = (List<DocumentoRegistroAmbiental>) query.getResultList();
		
		for(DocumentoRegistroAmbiental documento : listaDocumentos){
			byte[] archivoObtenido = descargar(documento.getAlfrescoId());
			documento.setContenidoDocumento(archivoObtenido);
			return documento;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocumentoRegistroAmbiental> documentoXTablaIdXIdDocLista(Integer idTabla,
			String nombreTabla, TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException {

		Query query =  crudServiceBean.getEntityManager().createQuery(
				"SELECT d FROM DocumentoRegistroAmbiental d WHERE d.nombreTabla = :nombreTabla AND d.idTable = :idTabla AND "
						+ "d.tipoDocumento.id = :idTipo AND d.estado = true order by d.fechaCreacion DESC");
		query.setParameter("idTabla", idTabla);
		query.setParameter("nombreTabla", nombreTabla);
		query.setParameter("idTipo", tipoDocumento.getIdTipoDocumento());						
						
		List<DocumentoRegistroAmbiental> listaDocumentos = (List<DocumentoRegistroAmbiental>) query.getResultList();
		

		return listaDocumentos;
	}
	
	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
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
}