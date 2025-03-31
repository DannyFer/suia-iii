package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeFactibilidadForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class InformeFactibilidadForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public InformeFactibilidadForestal guardar(InformeFactibilidadForestal informe) {			
		return crudServiceBean.saveOrUpdate(informe);
	}

	@SuppressWarnings("unchecked")
	public InformeFactibilidadForestal getInformePorViabilidad(Integer idViabilidad) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idViabilidad", idViabilidad);

		try {
			List<InformeFactibilidadForestal> lista = (List<InformeFactibilidadForestal>) crudServiceBean
					.findByNamedQuery(InformeFactibilidadForestal.GET_INFORME_POR_VIABILIDAD, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
}
