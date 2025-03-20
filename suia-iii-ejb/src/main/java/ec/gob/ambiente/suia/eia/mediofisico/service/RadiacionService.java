package ec.gob.ambiente.suia.eia.mediofisico.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Radiacion;

@Stateless
public class RadiacionService {
	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<Radiacion> radiacionXEiaId(Integer eiaId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("eiaId", eiaId);

		List<Radiacion> listaRadiacion = (List<Radiacion>) crudServiceBean
				.findByNamedQuery(Radiacion.LISTAR_POR_ID_EIA, params);
		return listaRadiacion;

	}

}