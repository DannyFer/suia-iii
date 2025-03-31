package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.EquipoConsultor;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class EquipoConsultorEIACoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public EquipoConsultor guardar(EquipoConsultor equipoConsultor) {        
    	return crudServiceBean.saveOrUpdate(equipoConsultor);        
	}
	
	public void desactivarEquipoConsultor(InformacionProyectoEia informacion, String usuario) {
		try {
			StringBuilder sql = new StringBuilder();
			// para deshabilita los no seleccionados
			sql.append("UPDATE coa_environmental_impact_study.team_consultant ");
			sql.append("set teco_status = false, teco_update_user = '"+usuario+"', teco_update_date = now() "); 
			sql.append("where prin_id = "+informacion.getId()+" ");
			sql.append("and teco_id not in ("+informacion.getConsultor().getId()+") ");
			sql.append("and teco_status = true ");
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		} catch (Exception e) {

		}		
	}
	@SuppressWarnings("unchecked")
	public List<EquipoConsultor> obtenerInformacionProyectoEIAPorProyecto(InformacionProyectoEia proyectoEIA )
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("proyectoEIAId", proyectoEIA.getId());
		
		List<EquipoConsultor> lista = (List<EquipoConsultor>) crudServiceBean
					.findByNamedQuery(
							EquipoConsultor.GET_EQUIPOCONSULTOR_POR_PROYECTO,
							parameters);
		if(lista.size() > 0 ){
			return lista;
		}
		return  new ArrayList<EquipoConsultor>();
	}
	
	@SuppressWarnings("unchecked")
	public List<EquipoConsultor> obtenerInformacionProyectoEIAPorProyectoPorIdConsultor(InformacionProyectoEia proyectoEIA, Integer id)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("proyectoEIAId", proyectoEIA.getId());
		parameters.put("id", id);
		
		List<EquipoConsultor> lista = (List<EquipoConsultor>) crudServiceBean
					.findByNamedQuery(
							EquipoConsultor.GET_EQUIPOCONSULTOR_POR_PROYECTO_ID_CONSULTOR,
							parameters);
		if(lista.size() > 0 ){
			return lista;
		}
		return  new ArrayList<EquipoConsultor>();
	}

}