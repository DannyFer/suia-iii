package ec.gob.ambiente.suia.licenciamiento.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ClasificacionClave;
import ec.gob.ambiente.suia.domain.ClaveOperacion;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FuenteDesechoPeligroso;

@Stateless
public class RequisitosPreviosServiceBean {

	private static final Logger LOG = Logger
			.getLogger(RequisitosPreviosServiceBean.class);

	@EJB
	CrudServiceBean crudServiceBean;

	/**
	 * 
	 * @param tipo
	 *            : Este tipo define el nivel de obtención de datos 0: Todo 1:
	 *            Especificos 2: No Especificos 3: Especiales
	 * @return List<FuenteEspecificaDesechos>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<FuenteDesechoPeligroso> obtenerFuentesDesechosPeligrosos(
			int tipoId) throws Exception {
		List<FuenteDesechoPeligroso> fuentes = null;
		try {
			if (tipoId == 0) {
				fuentes = (List<FuenteDesechoPeligroso>) crudServiceBean
						.findByNamedQuery("FuenteDesechosPeligrosos.findAll",
								null);
			} else {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("p_tipo", tipoId);
				fuentes = (List<FuenteDesechoPeligroso>) crudServiceBean
						.findByNamedQuery(
								"FuenteDesechosPeligrosos.findByType",
								parameters);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e.getMessage());
		}

		return fuentes;
	}

	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> obtenerDesechosPeligrosos(int fuenteDesechosId)
			throws Exception {
		List<DesechoPeligroso> fuentes = null;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_fuente", fuenteDesechosId);
			fuentes = (List<DesechoPeligroso>) crudServiceBean
					.findByNamedQuery("DesechosPeligrosos.findByFuente",
							parameters);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e.getMessage());
		}

		return fuentes;
	}

	@SuppressWarnings("unchecked")
	public List<ClasificacionClave> obtenerClasificacionesClave(int numeroClave)
			throws Exception {
		List<ClasificacionClave> clasificaciones = null;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_numeroClave", numeroClave);
			clasificaciones = (List<ClasificacionClave>) crudServiceBean
					.findByNamedQuery("ClasificacionClave.findByNumeroClave",
							parameters);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e.getMessage());
		}

		return clasificaciones;
	}

	@SuppressWarnings("unchecked")
	public List<ClaveOperacion> obtenerClavesOperacion(int clasificacionId)
			throws Exception {
		List<ClaveOperacion> claves = null;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_clasificacion", clasificacionId);
			claves = (List<ClaveOperacion>) crudServiceBean
					.findByNamedQuery("ClaveOperacion.findByClasificacion",
							parameters);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e.getMessage());
		}

		return claves;
	}

}
