package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ConsultorForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ConsultorForestalFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public ConsultorForestal guardar(ConsultorForestal consultor) {			
		return crudServiceBean.saveOrUpdate(consultor);
	}
	
	@SuppressWarnings("unchecked")
	public ConsultorForestal getConsultorPorCedula(String cedula) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cedula", cedula);

		try {
			List<ConsultorForestal> lista = (List<ConsultorForestal>) crudServiceBean
					.findByNamedQuery(ConsultorForestal.GET_CONSULTOR_POR_CEDULA, parameters);

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
