package ec.gob.ambiente.suia.eia.mediofisico.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.FisicoMecanicaSuelo;
@Stateless
public class FisicoMecanicaService {
	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<FisicoMecanicaSuelo> fisoMecanicaXEiaId(Integer eiaId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("eiaId", eiaId);

		List<FisicoMecanicaSuelo> listaFisicaMecanica = (List<FisicoMecanicaSuelo>) crudServiceBean
				.findByNamedQuery(FisicoMecanicaSuelo.LISTAR_POR_ID_EIA, params);
		return listaFisicaMecanica;
	}
}
