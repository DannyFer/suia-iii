package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.ActividadCiiu;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class ActividadCiiuFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public ActividadCiiu findById(Integer id){		
		try{
			ActividadCiiu activitiesCiiu = (ActividadCiiu) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM ActivitiesCiiu o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			return activitiesCiiu;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(ActividadCiiu obj,Usuario usuario) {
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(obj);
	}
	
	/**
	 * Buscar Actividades CIIU
	 * @param parentId (Si es nulo retorna Actividades, caso contrario subactividades)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ActividadCiiu> findAll(ActividadCiiu parent){	
		List<ActividadCiiu> activitiesCiiu=new ArrayList<ActividadCiiu>();
		try{
			 activitiesCiiu = (ArrayList<ActividadCiiu>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM ActividadCiiu o where o.estado=true and "+(parent==null?"o.actividadCiiu is null or o.actividadCiiu":"o.actividadCiiu.id")+"=:id order by o.orden,o.descripcion")
					.setParameter("id", parent==null?null:parent.getId())					
					.getResultList();
			return activitiesCiiu;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return activitiesCiiu;
	}
}