package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ProyectoFasesEiaCoa;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProyectoFasesCoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public ProyectoFasesEiaCoa guardar(ProyectoFasesEiaCoa faseProyecto) {        
    	return crudServiceBean.saveOrUpdate(faseProyecto);        
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoFasesEiaCoa> obtenerInformacionProyectoEIAPorProyecto(InformacionProyectoEia proyectoEIA )
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("proyectoEIAId", proyectoEIA.getId());
		
		List<ProyectoFasesEiaCoa> lista = (List<ProyectoFasesEiaCoa>) crudServiceBean
					.findByNamedQuery(
							ProyectoFasesEiaCoa.GET_FASES_POR_PROYECTO,
							parameters);
		if(lista.size() > 0 ){
			return lista;
		}
		return  new ArrayList<ProyectoFasesEiaCoa>();
	}

	public void eliminarFasesProyecto(List<String> listaIdNoBorrar, List<String> listaIdBorrar, Integer proyectoId){	
		try{
			String listaNotId = "0", listaId="0";
			for (String objString : listaIdNoBorrar) {
				listaNotId += ", "+objString;
			}
			for (String objString : listaIdBorrar) {
				listaId += ", "+objString;
			}
			
			String sql =" update coa_environmental_impact_study.project_phase set ppha_status = false, ppha_update_date=now(), ppha_update_user = ppha_creator_user "
					+ " where capp_id not in ("+listaNotId+") and  capp_id in ("+listaId+") and prin_id = "+proyectoId+" ;";
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		}catch(Exception e){
			System.out.println("Error al actualizar las fases del proyecto");;
		}
	}
}