package ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class BPCBypassFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public boolean verificarProcesoIniciado(String codigoTramite){
		try {
			String sql="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select variableid, value "
	            	+ "from variableinstancelog where processinstanceid in ("
	            	+ "select processinstanceid from variableinstancelog where value = " 
	            	+ "''" + codigoTramite +"'' and processid = ''"+Constantes.RCOA_PROCESO_BYPASS_PARTICIPACION_CIUDADANA+"'' limit 1)') as (variableid text, value text)"; 
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			
			//se lo coloca en un hashmap para poder tener acceso a la variable mediante la key processintanceid    		
			Map<String, Object> variables = new HashMap<String, Object>();
        	List<Object[]> resultList = (List<Object[]>) query.getResultList();
    		if (resultList.size() > 0) {
    			for (int i = 0; i < resultList.size(); i++) {
    				Object[] dataProject = (Object[]) resultList.get(i);
    				if(dataProject[1].toString().contains("MAE-RA") || dataProject[1].toString().contains("MAAE-RA")
    					|| dataProject[1].toString().contains("MAATE-RA")){	
    					variables.put(dataProject[0].toString(), dataProject[1].toString());	
    				}
    			}
    		}
    		
    		if(variables.isEmpty()){
    			return false;
    		}else{
    			return true;
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> getProyectosEiaAprobados() {
		String sql="select prco_cua "
	            	+ "from coa_environmental_impact_study.project_information i "
	            	+ "inner join coa_mae.project_licencing_coa p on p.prco_id = i.prco_id "
	            	+ "where prin_ending_date_study is not null "
	            	+ "and prin_final_result_eia_cata_id = 8 "
	            	+ "and p.prco_cua_date < '"+ Constantes.getFechaSuspensionPpcBypass() +"' "
	            	+ "order by 1";
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object> resultList = new ArrayList<Object>();
		resultList = query.getResultList();
		List<String> listaCodigosProyectos = new ArrayList<String>();
		if (resultList.size() > 0) {
			for (Object a : resultList) {
				listaCodigosProyectos.add(a.toString());

			}
		}

		return listaCodigosProyectos;
	}

}
