package ec.gob.ambiente.suia.eia.descripcionProyecto.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadLicenciamiento;
import ec.gob.ambiente.suia.domain.ActividadesPorEtapa;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by msit-erislan on 18/12/2015.
 */
@Stateless
public class ActividadesPorEtapaService {

    @EJB
    private CrudServiceBean crudServiceBean;

    public List<ActividadesPorEtapa> getListaActividadPorEtapa(EstudioImpactoAmbiental estudio) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("estudio", estudio);
        return (List<ActividadesPorEtapa>) crudServiceBean.findByNamedQuery(ActividadesPorEtapa.FIND_BY_ESTUDIO, parameters);
    }
    
    @SuppressWarnings("unchecked")
    public ActividadesPorEtapa getActividadPorEtapaPorId(Integer id){
    	try{
    		Query query = crudServiceBean.getEntityManager().createQuery("Select c FROM ActividadesPorEtapa c WHERE c.id = :id");
    		query.setParameter("id", id);
    		
    		List<ActividadesPorEtapa> actividades = query.getResultList();
    		if(actividades == null || !actividades.isEmpty()){
    			return null;
    		}else{
    			return actividades.get(0);
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
    
    @SuppressWarnings("unchecked")
    public List<ActividadesPorEtapa> getListActividadPorEtapaPorIdHistorico(Integer idHistorico, Integer notificacion){
    	try{
    		Query query = crudServiceBean.getEntityManager().createQuery("Select c FROM ActividadesPorEtapa c WHERE c.id = :id AND c.numeroNotificacion = :notificacion");
    		query.setParameter("id", idHistorico);
    		query.setParameter("notificacion", notificacion);
    		
    		List<ActividadesPorEtapa> actividades = query.getResultList();
    		if(actividades == null || !actividades.isEmpty()){
    			return null;
    		}else{
    			return actividades;
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }  
   
}
