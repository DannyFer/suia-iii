package ec.gob.ambiente.suia.eia.mediofisico.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CalidadAgua;
@Stateless
public class CalidadAguaService {
	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<CalidadAgua> calidadAguaXEiaId(Integer eiaId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("eiaId", eiaId);

		List<CalidadAgua> listaCalidadAgua = (List<CalidadAgua>) crudServiceBean
				.findByNamedQuery(CalidadAgua.LISTAR_POR_ID_EIA, params);
		return listaCalidadAgua;
	}
}
