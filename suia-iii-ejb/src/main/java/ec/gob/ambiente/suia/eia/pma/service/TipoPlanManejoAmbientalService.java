package ec.gob.ambiente.suia.eia.pma.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoPlanManejoAmbiental;

@Stateless
public class TipoPlanManejoAmbientalService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5716717657061819106L;

	Logger LOG = Logger.getLogger(TipoPlanManejoAmbientalService.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<TipoPlanManejoAmbiental> obtenerListaTipoPlanManejoAmbiental()
			throws Exception {
		try {
			return (List<TipoPlanManejoAmbiental>) crudServiceBean
					.findAll(TipoPlanManejoAmbiental.class);
		} catch (Exception e) {
			LOG.error("Error al cargar los tipos de PMA", e);
			throw new Exception(e);
		}
	}

	public TipoPlanManejoAmbiental obtenerTipoPlanManejoAmbiental(Integer id)
			throws Exception {
		try {
			return (TipoPlanManejoAmbiental) crudServiceBean.find(
					TipoPlanManejoAmbiental.class, id);
		} catch (Exception e) {
			LOG.error("Error al cargar los tipos de PMA", e);
			throw new Exception(e);
		}
	}
	
	public List<TipoPlanManejoAmbiental> obtenerTipoPlanManejoAmbientalPorTipo(String tipo)
			throws Exception {
		try {
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("tipoProceso", tipo);
			
			return crudServiceBean.findByNamedQueryGeneric(
					TipoPlanManejoAmbiental.FIND_PLAN, parameters);
			
		} catch (Exception e) {
			LOG.error("Error al cargar los tipos de PMA", e);
			throw new Exception(e);
		}
	}

}
