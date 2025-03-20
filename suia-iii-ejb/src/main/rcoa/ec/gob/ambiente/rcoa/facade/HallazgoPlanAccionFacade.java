package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.HallazgoPlanAccion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class HallazgoPlanAccionFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
		
	public HallazgoPlanAccion guardar(HallazgoPlanAccion hallazgo) {
		return crudServiceBean.saveOrUpdate(hallazgo);
	}
	
	@SuppressWarnings("unchecked")
	public List<HallazgoPlanAccion> getPorPlan(Integer idPlan)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idPlan", idPlan);
		
		List<HallazgoPlanAccion> lista = (List<HallazgoPlanAccion>) crudServiceBean.findByNamedQuery(HallazgoPlanAccion.GET_POR_PLAN, parameters);
		if(lista.size() > 0 ){
			return lista;
		}

		return new ArrayList<>();
	}

}