/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.flora.service;

import java.util.Collection;
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
import ec.gob.ambiente.suia.domain.FloraEspecie;
import ec.gob.ambiente.suia.domain.FloraGeneral;
import ec.gob.ambiente.suia.domain.PuntosMuestreoFlora;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

/**
 * 
 * @author
 */
@Stateless
public class FloraService {

	private static final Logger LOG = Logger.getLogger(FloraService.class);

	@EJB
	CrudServiceBean crudServiceBean;

	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	AlfrescoServiceBean alfrescoServiceBean;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Integer guardarFlora(FloraGeneral flora,
			List<List<Object>> eliminados) throws Exception {

		try {
			TipoDocumento tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS.getIdTipoDocumento());

			List<PuntosMuestreoFlora> listaPuntosMuestreo = flora
					.getListaPuntosMuestreo();
			byte[] fileAnexo = flora.getFileAnexo();
			String contentType = flora.getContentType();
			boolean archivoMetodologiaEditado = flora.isArchivoEditado();

			if (flora.getId() == null) {
				flora = crudServiceBean.saveOrUpdate(flora);
			} else {
				flora = crudServiceBean.saveOrUpdate(flora);
			}
			for (PuntosMuestreoFlora punto : listaPuntosMuestreo) {
				Collection<CoordenadaGeneral> listaCoordenadas = punto
						.getListaCoordenadas();
				Collection<FloraEspecie> listaFloraEspecie = punto
						.getListaFloraEspecie();

				if (punto.getId() == null) {
					punto.setIdFlora(flora.getId());
					punto = crudServiceBean.saveOrUpdate(punto);
				} else {
					punto.setIdFlora(flora.getId());
					punto = crudServiceBean.saveOrUpdate(punto);
				}

				for (CoordenadaGeneral coordenada : listaCoordenadas) {
					coordenada.setIdTable(punto.getId());
					coordenada.setNombreTabla(PuntosMuestreoFlora.class
							.getSimpleName());
					if (coordenada.getId() == null) {
						crudServiceBean.saveOrUpdate(coordenada);
					} else {
						crudServiceBean.saveOrUpdate(coordenada);
					}
				}
				for (FloraEspecie especie : listaFloraEspecie) {
					byte[] fileFoto = especie.getFotoEspecie();
					String contentTypeFoto = especie.getContentType();
					boolean archivoEspecieEditado = especie.isArchivoEditado();

					if (especie.getId() == null) {
						especie.setPuntosMuestreoId(punto.getId());
						crudServiceBean.saveOrUpdate(especie);
					} else {
						especie.setPuntosMuestreoId(punto.getId());
						crudServiceBean.saveOrUpdate(especie);
					}
					if (especie.getAnexoFoto() != null
							&& !especie.getAnexoFoto().equals("")
							&& fileFoto != null) {

						if (archivoEspecieEditado) {
							Map<String, Object> parameters4 = new HashMap<String, Object>();
							parameters4.put("nombreTabla",
									FloraEspecie.class.getSimpleName());
							parameters4.put("idTabla", especie.getId());

							List<Documento> documentos = (List<Documento>) crudServiceBean
									.findByNamedQuery(
											Documento.LISTAR_POR_ID_NOMBRE_TABLA,
											parameters4);
							if (!documentos.isEmpty()) {
								crudServiceBean
										.delete(crudServiceBean.find(
												Documento.class, documentos
														.get(0).getId()));
							}
						}

						String folderName = UtilAlfresco
								.generarEstructuraCarpetas(
										EstudioImpactoAmbiental.PROJECT_CODE,
										EstudioImpactoAmbiental.PROCESS_NAME,
										EstudioImpactoAmbiental.PROCESS_ID
												.toString());
						String folderId = alfrescoServiceBean
								.createFolderStructure(folderName,
										Constantes.getRootId());
						Document documentCreate = alfrescoServiceBean
								.fileSaveStream(fileFoto,
										especie.getAnexoFoto(), folderId,
										"EIA", folderName, especie.getId());

						Documento documento = crudServiceBean
								.saveOrUpdate(UtilAlfresco.crearDocumento(
										especie.getDescripcionFoto(),
										contentTypeFoto,
										documentCreate.getId(),
										especie.getId(), contentTypeFoto,
										especie.getAnexoFoto(),
										FloraEspecie.class.getSimpleName(),
										TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

						crudServiceBean
								.saveOrUpdate(UtilAlfresco
										.crearDocumentoTareaProceso(documento,
												123, 148L));
					}
				}
			}

			if (archivoMetodologiaEditado) {
				Map<String, Object> parameters4 = new HashMap<String, Object>();
				parameters4.put("nombreTabla",
						FloraGeneral.class.getSimpleName());
				parameters4.put("idTabla", flora.getId());

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
					fileAnexo, flora.getAnexoMetodologia(), folderId, "EIA",
					folderName, flora.getId());

			Documento documento = crudServiceBean.saveOrUpdate(UtilAlfresco
					.crearDocumento(documentCreate.getName(), contentType,
							documentCreate.getId(), flora.getId(), contentType,
							flora.getAnexoMetodologia(),
							FloraGeneral.class.getSimpleName(),
							TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

			crudServiceBean.saveOrUpdate(UtilAlfresco
					.crearDocumentoTareaProceso(documento, 123, 148L));

			if (eliminados != null && !eliminados.isEmpty()) {
				for (List<Object> o : eliminados) {
					crudServiceBean.delete(crudServiceBean.find(
							(Class) o.get(0), (Integer) o.get(1)));
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e.getMessage());
		}

		return flora.getId();
	}

	@SuppressWarnings("unchecked")
	public FloraGeneral obtenerFlora(Integer eiaId) throws Exception {
		FloraGeneral floraGeneral = null;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_eiaId", eiaId);

			List<FloraGeneral> floras = (List<FloraGeneral>) crudServiceBean
					.findByNamedQuery(FloraGeneral.BUSCAR_POR_ESTUDIO_ID,
							parameters);

			if (!floras.isEmpty()) {
				floraGeneral = floras.get(0);
				parameters = new HashMap<String, Object>();
				parameters.put("nombreTabla", FloraGeneral.class.getSimpleName());
				parameters.put("idTabla", floraGeneral.getId());

				List<Documento> documentos = (List<Documento>) crudServiceBean
						.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA,
								parameters);
				if (!documentos.isEmpty()) {
					byte[] archivo = alfrescoServiceBean
							.downloadDocumentById(documentos.get(0).getIdAlfresco());
					floraGeneral.setFileAnexo(archivo);
					floraGeneral.setContentType(documentos.get(0).getMime());
				}

			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e.getMessage());
		}

		return floraGeneral;
	}

	@SuppressWarnings("unchecked")
	public List<PuntosMuestreoFlora> obtenerPuntosMuestreo(Integer floraId)
			throws Exception {
		List<PuntosMuestreoFlora> puntosMuestreo = null;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_floraId", floraId);

			puntosMuestreo = (List<PuntosMuestreoFlora>) crudServiceBean
					.findByNamedQuery("PuntosMuestreoFlora.findByFloraId",
							parameters);

			for (PuntosMuestreoFlora puntosMuestreoFlora : puntosMuestreo) {
				Map<String, Object> parameters2 = new HashMap<String, Object>();
				parameters2.put("nombreTabla",
						PuntosMuestreoFlora.class.getSimpleName());
				parameters2.put("idTabla", puntosMuestreoFlora.getId());

				puntosMuestreoFlora
						.setListaCoordenadas((List<CoordenadaGeneral>) crudServiceBean
								.findByNamedQuery(
										CoordenadaGeneral.LISTAR_POR_ID_NOMBRE_TABLA,
										parameters2));

				Map<String, Object> parameters3 = new HashMap<String, Object>();
				parameters3.put("p_puntoId", puntosMuestreoFlora.getId());

				puntosMuestreoFlora
						.setListaFloraEspecie((List<FloraEspecie>) crudServiceBean
								.findByNamedQuery(
										FloraEspecie.FIND_BY_PUNTO_MUESTREO,
										parameters3));

				for (FloraEspecie especie : puntosMuestreoFlora
						.getListaFloraEspecie()) {
					Map<String, Object> parameters4 = new HashMap<String, Object>();
					parameters4.put("nombreTabla",
							FloraEspecie.class.getSimpleName());
					parameters4.put("idTabla", especie.getId());

					List<Documento> documentos = (List<Documento>) crudServiceBean
							.findByNamedQuery(
									Documento.LISTAR_POR_ID_NOMBRE_TABLA,
									parameters4);
					if (!documentos.isEmpty()) {
						byte[] archivo = alfrescoServiceBean
								.downloadDocumentById(documentos.get(0)
										.getIdAlfresco());
						especie.setFotoEspecie(archivo);
						especie.setContentType(documentos.get(0).getMime());
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e.getMessage());
		}

		return puntosMuestreo;
	}

}
