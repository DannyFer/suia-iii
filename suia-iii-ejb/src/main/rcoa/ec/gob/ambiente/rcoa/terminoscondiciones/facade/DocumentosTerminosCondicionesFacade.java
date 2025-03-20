package ec.gob.ambiente.rcoa.terminoscondiciones.facade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.terminoscondiciones.model.DocumentoTerminosCondiciones;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DocumentosTerminosCondicionesFacade {

	private static final Logger LOG = Logger.getLogger(DocumentosTerminosCondicionesFacade.class);

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;

	@EJB
	private CrudServiceBean crudServiceBean;

	private static final String NOMBRE_CARPETA = "TERMINOS_CONDICIONES";

	public DocumentoTerminosCondiciones guardarDocumento(String usuario, DocumentoTerminosCondiciones documento)
			throws ServiceException, CmisAlfrescoException {

		String folderName = UtilAlfresco.generarEstructuraCarpetas(NOMBRE_CARPETA, usuario, null);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootRcoaId());

		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento.getContenidoDocumento(),
				documento.getNombre().replace("/", "-"), folderId, NOMBRE_CARPETA, folderName);

		documento.setIdAlfresco(documentCreate.getId());
		DocumentoTerminosCondiciones documentoTC = crudServiceBean.saveOrUpdate(documento);
		return documentoTC;

	}

	public DocumentoTerminosCondiciones obtenerDocumentoPorUsuario(TipoDocumentoSistema tipo, Integer usuarioId) {
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT o FROM DocumentoTerminosCondiciones o WHERE o.estado=true and o.tipoDocumento.id=:idTipo and o.usuarioId=:usuarioId ORDER BY 1 DESC");
			query.setParameter("idTipo", tipo.getIdTipoDocumento());
			query.setParameter("usuarioId", usuarioId);
			query.setMaxResults(1);
			return (DocumentoTerminosCondiciones) query.getSingleResult();
		} catch (NoResultException e) {
			Log.debug(e.toString());
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public byte[] descargar(String idDocAlfresco) throws CmisAlfrescoException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		byte[] archivo = alfrescoServiceBean.downloadDocumentById(idDocAlfresco);
		return archivo;
	}

	public String direccionDescarga(String idDocAlfresco) throws CmisAlfrescoException {
		idDocAlfresco = (idDocAlfresco.split(";"))[0];
		return alfrescoServiceBean.downloadDocumentByObjectId(idDocAlfresco);
	}

	public boolean verificarFirma_(String idDocAlfresco) {
		File file = null;
		try {
			String url = idDocAlfresco;
			url = (url.split(";"))[0];
			file = alfrescoServiceBean.downloadDocumentByIdFile(url);
			if (verificarFirma(file.getAbsolutePath()))
				return true;

			return false;
		} catch (CmisAlfrescoException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean verificarFirmaVersion(String idAlfresco) {
		return verificarFirmaAlfresco(idAlfresco);

	}

	public boolean verificarFirmaAlfresco(String idDocAlfresco) {
		File file = null;
		try {
			String url = idDocAlfresco;
			url = (url.split(";"))[0];
			file = alfrescoServiceBean.downloadDocumentByIdFile(url);
			if (verificarFirma(file.getAbsolutePath()))
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
}
