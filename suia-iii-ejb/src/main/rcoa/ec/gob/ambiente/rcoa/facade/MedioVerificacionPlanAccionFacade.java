package ec.gob.ambiente.rcoa.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.MedioVerificacionPlanAccion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class MedioVerificacionPlanAccionFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
		
	public MedioVerificacionPlanAccion guardar(MedioVerificacionPlanAccion medio) {
		return crudServiceBean.saveOrUpdate(medio);
	}
	
	@SuppressWarnings("unchecked")
	public List<MedioVerificacionPlanAccion> getPorHallazgo(Integer idHallazgo)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idHallazgo", idHallazgo);
		
		List<MedioVerificacionPlanAccion> lista = (List<MedioVerificacionPlanAccion>) crudServiceBean.findByNamedQuery(MedioVerificacionPlanAccion.GET_POR_HALLAZGO, parameters);
		if(lista.size() > 0 ){
			return lista;
		}

		return  null;
	}

}