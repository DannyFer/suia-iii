package ec.gob.ambiente.suia.eia.pma.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlanManejoAmbiental;

@Stateless
public class PlanManejoAmbientalService implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6440814985076320036L;
	
	Logger LOG = Logger.getLogger(PlanManejoAmbientalService.class);
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public List<PlanManejoAmbiental> obtenerListaPMA(Integer tipoPlanId,
			Integer eiaId) throws Exception {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_tipoId", tipoPlanId);
			parameters.put("p_eiaId", eiaId);

			return crudServiceBean.findByNamedQueryGeneric(
					PlanManejoAmbiental.LISTAR_POR_TIPOID_EIAID, parameters);
		} catch (Exception e) {
			LOG.error("Error al cargar los tipos de PMA", e);
			throw new Exception(e);
		}
	}

	

}
