package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.AfectacionForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class AfectacionForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(AfectacionForestal afectacion) {			
		crudServiceBean.saveOrUpdate(afectacion);
	}

	@SuppressWarnings("unchecked")
	public List<AfectacionForestal> getListaPorInforme(Integer idInformeFactibilidad) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInformeFactibilidad", idInformeFactibilidad);

		try {
			List<AfectacionForestal> lista = (List<AfectacionForestal>) crudServiceBean
					.findByNamedQuery(AfectacionForestal.GET_LISTA_POR_INFORME, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
}
