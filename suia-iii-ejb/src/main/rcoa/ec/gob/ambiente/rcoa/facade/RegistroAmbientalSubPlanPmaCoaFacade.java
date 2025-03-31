package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalSubPlanPma;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class RegistroAmbientalSubPlanPmaCoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public RegistroAmbientalSubPlanPma guardar(RegistroAmbientalSubPlanPma pmaRegistroAmbiental) {
    	return crudServiceBean.saveOrUpdate(pmaRegistroAmbiental);
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroAmbientalSubPlanPma> obtenerSubPlanPorProyecto(RegistroAmbientalRcoa registroAmbiental)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("registroAnbientalId", registroAmbiental.getId());
		
		List<RegistroAmbientalSubPlanPma> lista = (List<RegistroAmbientalSubPlanPma>) crudServiceBean
					.findByNamedQuery(
							RegistroAmbientalSubPlanPma.GET_SUBPLANES_POR_PROYECTO,
							parameters);
		if(lista.size() > 0 ){
			return lista;
		}
		return  new ArrayList<RegistroAmbientalSubPlanPma>();
	}	

	public void eliminarDocumentos(String tablaName, Integer tablaId, Integer tipoDocumentoId){	
		try{
		String sql ="UPDATE coa_environmental_record.documents_environmental_record set doer_status = false, doer_date_update=now(), doer_user_update = doer_creator_user "
				+ " where doty_id = "+tipoDocumentoId+" and doer_table_class = '"+tablaName+"' and doer_table_id = "+tablaId+" ;";
		crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		}catch(Exception e){
			System.out.println("Error al eliminar los planes");;
		}
	}
	  
}