package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.EquipoApoyoProyecto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class EquipoApoyoProyectoEIACoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public EquipoApoyoProyecto guardar(EquipoApoyoProyecto equipoConsultor) {        
    	return crudServiceBean.saveOrUpdate(equipoConsultor);        
	}
	
	@SuppressWarnings("unchecked")
	public EquipoApoyoProyecto obtenerEquipoApoyoProyecto(InformacionProyectoEia proyectoEIA )
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("proyectoEIAId", proyectoEIA.getId());
		
		List<EquipoApoyoProyecto> lista = (List<EquipoApoyoProyecto>) crudServiceBean
					.findByNamedQuery(
							EquipoApoyoProyecto.GET_EQUIPOAPOYO_PROYECTO,
							parameters);
		if(lista != null && lista.size() > 0){
			return lista.get(0);
		}
		return  new EquipoApoyoProyecto();
	}

}