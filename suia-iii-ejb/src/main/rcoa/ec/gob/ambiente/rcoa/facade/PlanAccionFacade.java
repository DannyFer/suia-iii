package ec.gob.ambiente.rcoa.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.PlanAccion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class PlanAccionFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
		
	public PlanAccion guardar(PlanAccion plan) {
		return crudServiceBean.saveOrUpdate(plan);
	}
	
	@SuppressWarnings("unchecked")
	public PlanAccion getPorProyecto(Integer idProyecto)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		
		List<PlanAccion> lista = (List<PlanAccion>) crudServiceBean.findByNamedQuery(PlanAccion.GET_POR_PROYECTO,parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}

}