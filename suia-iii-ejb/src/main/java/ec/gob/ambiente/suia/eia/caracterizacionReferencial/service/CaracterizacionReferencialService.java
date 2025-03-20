package ec.gob.ambiente.suia.eia.caracterizacionReferencial.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.Archivo;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class CaracterizacionReferencialService {

	private static final Logger LOG = Logger
			.getLogger(CaracterizacionReferencialService.class);

	@EJB
	CrudServiceBean crudServiceBean;
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	AlfrescoServiceBean alfrescoServiceBean;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void guardar(List<CoordenadaGeneral> coordenadasGenerales,
			List<List<Object>> eliminados) throws Exception {
		try {
			for (CoordenadaGeneral coordenadaGeneral : coordenadasGenerales) {
				Map<String, Archivo> archivos = coordenadaGeneral.getArchivos();
				boolean editado = coordenadaGeneral.isEditar();

				if (coordenadaGeneral.getId() == null) {
					coordenadaGeneral = crudServiceBean.saveOrUpdate(coordenadaGeneral);
				} else {
					coordenadaGeneral = crudServiceBean.saveOrUpdate(coordenadaGeneral);
				}

				// DESCRIPCION
				if (editado) {
					Map<String, Object> parameters4 = new HashMap<String, Object>();
					parameters4.put("nombreTabla",
							CoordenadaGeneral.PREFIX_FILE_DESCRIPCION
									+ CoordenadaGeneral.class.getSimpleName());
					parameters4.put("idTabla", coordenadaGeneral.getId());

					List<Documento> documentos = (List<Documento>) crudServiceBean
							.findByNamedQuery(
									Documento.LISTAR_POR_ID_NOMBRE_TABLA,
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
						archivos.get(CoordenadaGeneral.PREFIX_FILE_DESCRIPCION)
								.getFile(),
						archivos.get(CoordenadaGeneral.PREFIX_FILE_DESCRIPCION)
								.getName(), folderId, "EIA", folderName,
						coordenadaGeneral.getId());

				Documento documento = crudServiceBean
						.saveOrUpdate(UtilAlfresco.crearDocumento(
								documentCreate.getName(),
								archivos.get(
										CoordenadaGeneral.PREFIX_FILE_DESCRIPCION)
										.getContentTye(),
								documentCreate.getId(),
								coordenadaGeneral.getId(),
								archivos.get(
										CoordenadaGeneral.PREFIX_FILE_DESCRIPCION)
										.getContentTye(),
								archivos.get(
										CoordenadaGeneral.PREFIX_FILE_DESCRIPCION)
										.getName(),
								CoordenadaGeneral.PREFIX_FILE_DESCRIPCION
										+ CoordenadaGeneral.class
												.getSimpleName(),
								TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

				crudServiceBean.saveOrUpdate(UtilAlfresco
						.crearDocumentoTareaProceso(documento, 123, 148L));

				// IMAGEN
				if (editado) {
					Map<String, Object> parameters4 = new HashMap<String, Object>();
					parameters4.put("nombreTabla",
							CoordenadaGeneral.PREFIX_FILE_IMAGE
									+ CoordenadaGeneral.class.getSimpleName());
					parameters4.put("idTabla", coordenadaGeneral.getId());

					List<Documento> documentos = (List<Documento>) crudServiceBean
							.findByNamedQuery(
									Documento.LISTAR_POR_ID_NOMBRE_TABLA,
									parameters4);
					if (!documentos.isEmpty()) {
						crudServiceBean.delete(crudServiceBean.find(
								Documento.class, documentos.get(0).getId()));
					}
				}

				folderName = UtilAlfresco.generarEstructuraCarpetas(
						EstudioImpactoAmbiental.PROJECT_CODE,
						EstudioImpactoAmbiental.PROCESS_NAME,
						EstudioImpactoAmbiental.PROCESS_ID.toString());
				documentCreate = alfrescoServiceBean.fileSaveStream(archivos
						.get(CoordenadaGeneral.PREFIX_FILE_IMAGE).getFile(),
						archivos.get(CoordenadaGeneral.PREFIX_FILE_IMAGE)
								.getName(), folderId, "EIA", folderName,
						coordenadaGeneral.getId());

				documento = crudServiceBean.saveOrUpdate(UtilAlfresco
						.crearDocumento(
								documentCreate.getName(),
								archivos.get(
										CoordenadaGeneral.PREFIX_FILE_IMAGE)
										.getContentTye(),
								documentCreate.getId(),
								coordenadaGeneral.getId(),
								archivos.get(
										CoordenadaGeneral.PREFIX_FILE_IMAGE)
										.getContentTye(),
								archivos.get(
										CoordenadaGeneral.PREFIX_FILE_IMAGE)
										.getName(),
								CoordenadaGeneral.PREFIX_FILE_IMAGE
										+ CoordenadaGeneral.class
												.getSimpleName(),
								TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

				crudServiceBean.saveOrUpdate(UtilAlfresco
						.crearDocumentoTareaProceso(documento, 123, 148L));

			}

			if (eliminados != null && !eliminados.isEmpty()) {
				for (List<Object> o : eliminados) {
					crudServiceBean.delete(crudServiceBean.find(
							(Class) o.get(0), (Integer) o.get(1)));
				}
			}

		} catch (Exception e) {
			LOG.error(e.getCause() + " - " + e.getMessage());
			throw new Exception(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<CoordenadaGeneral> obtenerCoordenadas(Integer eiaId,
			String tabla) throws Exception {
		List<CoordenadaGeneral> listaCoordenadas = new ArrayList<CoordenadaGeneral>();

		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("nombreTabla", tabla);
			parameters.put("idTabla", eiaId);

			List<CoordenadaGeneral> coordenadas = (List<CoordenadaGeneral>) crudServiceBean
					.findByNamedQuery(
							CoordenadaGeneral.LISTAR_POR_ID_NOMBRE_TABLA,
							parameters);

			for (CoordenadaGeneral coordenadaGeneral : coordenadas) {
				parameters = new HashMap<String, Object>();
				parameters.put("nombreTabla",
						CoordenadaGeneral.PREFIX_FILE_DESCRIPCION
								+ CoordenadaGeneral.class.getSimpleName());
				parameters.put("idTabla", coordenadaGeneral.getId());

				List<Documento> documentos = (List<Documento>) crudServiceBean
						.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA,
								parameters);
				if (!documentos.isEmpty()) {
					byte[] archivo = alfrescoServiceBean
							.downloadDocumentById(documentos.get(0)
									.getIdAlfresco());
					Archivo file = new Archivo(archivo, documentos.get(0)
							.getNombre(), documentos.get(0).getMime());
					coordenadaGeneral.getArchivos().put(
							CoordenadaGeneral.PREFIX_FILE_DESCRIPCION, file);
				}

				parameters = new HashMap<String, Object>();
				parameters.put("nombreTabla",
						CoordenadaGeneral.PREFIX_FILE_IMAGE
								+ CoordenadaGeneral.class.getSimpleName());
				parameters.put("idTabla", coordenadaGeneral.getId());

				documentos = (List<Documento>) crudServiceBean
						.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA,
								parameters);
				if (!documentos.isEmpty()) {
					byte[] archivo = alfrescoServiceBean
							.downloadDocumentById(documentos.get(0)
									.getIdAlfresco());
					Archivo file = new Archivo(archivo, documentos.get(0)
							.getNombre(), documentos.get(0).getMime());
					coordenadaGeneral.getArchivos().put(
							CoordenadaGeneral.PREFIX_FILE_IMAGE, file);
				}

				listaCoordenadas.add(coordenadaGeneral);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e.getMessage());
		}

		return listaCoordenadas;
	}

}
