package ec.gob.ambiente.suia.eia.diagnosticoAmbiental.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DiagnosticoAmbiental;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DiagnosticoAmbientalService {

	private static final Logger LOG = Logger
			.getLogger(DiagnosticoAmbientalService.class);

	@EJB
	CrudServiceBean crudServiceBean;
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	AlfrescoServiceBean alfrescoServiceBean;

	@SuppressWarnings("unchecked")
	public void guardarDiagnostico(DiagnosticoAmbiental diagnosticoAmbiental)
			throws Exception {
		try {
			boolean archivoEditado = diagnosticoAmbiental.isArchivoEditado();
			byte[] archivo = diagnosticoAmbiental.getAnexoDiagnostico();
			String contentType = diagnosticoAmbiental
					.getAnexoDiagnosticoContenType();

			if (diagnosticoAmbiental.getId() != null) {
				diagnosticoAmbiental = crudServiceBean.saveOrUpdate(diagnosticoAmbiental);
			} else {
				diagnosticoAmbiental = crudServiceBean.saveOrUpdate(diagnosticoAmbiental);
			}

			if (archivoEditado) {
				Map<String, Object> parameters4 = new HashMap<String, Object>();
				parameters4.put("nombreTabla",
						DiagnosticoAmbiental.class.getSimpleName());
				parameters4.put("idTabla", diagnosticoAmbiental.getId());

				List<Documento> documentos = (List<Documento>) crudServiceBean
						.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA,
								parameters4);
				if (!documentos.isEmpty()) {
					crudServiceBean.delete(crudServiceBean.find(
							Documento.class, documentos.get(0).getId()));
				}
			}

			String folderName = UtilAlfresco.generarEstructuraCarpetas(
					EstudioImpactoAmbiental.PROJECT_CODE,
					EstudioImpactoAmbiental.PROCESS_NAME,
					EstudioImpactoAmbiental.PROCESS_ID.toString());
			String folderId = alfrescoServiceBean.createFolderStructure(
					folderName, Constantes.getRootId());
			Document documentCreate = alfrescoServiceBean.fileSaveStream(
					archivo, diagnosticoAmbiental.getAnexoDiagnosticoName(),
					folderId, "EIA", folderName, diagnosticoAmbiental.getId());

			Documento documento = crudServiceBean.saveOrUpdate(UtilAlfresco
					.crearDocumento(documentCreate.getName(), contentType,
							documentCreate.getId(),
							diagnosticoAmbiental.getId(), contentType,
							diagnosticoAmbiental.getAnexoDiagnosticoName(),
							DiagnosticoAmbiental.class.getSimpleName(),
							TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

			crudServiceBean.saveOrUpdate(UtilAlfresco
					.crearDocumentoTareaProceso(documento, 123, 148L));

		} catch (Exception e) {
			LOG.error(e.getCause() + " - " + e.getMessage());
			throw new Exception(e);
		}
	}

	@SuppressWarnings("unchecked")
	public DiagnosticoAmbiental obtenerDiagnostico(Integer eiaId) throws Exception {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_eiaId", eiaId);

			DiagnosticoAmbiental diagnosticoAmbiental = null;

			List<DiagnosticoAmbiental> diagnosticos = (List<DiagnosticoAmbiental>) crudServiceBean
					.findByNamedQuery(
							DiagnosticoAmbiental.BUSCAR_POR_ESTUDIO_ID,
							parameters);

			if (!diagnosticos.isEmpty()) {
				diagnosticoAmbiental = diagnosticos.get(0);
				parameters = new HashMap<String, Object>();
				parameters.put("nombreTabla",
						DiagnosticoAmbiental.class.getSimpleName());
				parameters.put("idTabla", diagnosticoAmbiental.getId());

				List<Documento> documentos = (List<Documento>) crudServiceBean
						.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA,
								parameters);
				if (!documentos.isEmpty()) {
					byte[] archivo = alfrescoServiceBean
							.downloadDocumentById(documentos.get(0)
									.getIdAlfresco());
					diagnosticoAmbiental.setAnexoDiagnostico(archivo);
					diagnosticoAmbiental
							.setAnexoDiagnosticoContenType(documentos.get(0)
									.getMime());
				}
			}
			return diagnosticoAmbiental;
		} catch (Exception e) {
			LOG.error(e.getCause() + " - " + e.getMessage());
			throw new Exception(e);
		}
	}

}
