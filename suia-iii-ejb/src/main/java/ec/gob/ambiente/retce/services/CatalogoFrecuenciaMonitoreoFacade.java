package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoFrecuenciaMonitoreo;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean; 
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoFrecuenciaMonitoreoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CatalogoFrecuenciaMonitoreo findById(Integer id){
		try {
			CatalogoFrecuenciaMonitoreo frecuencia = (CatalogoFrecuenciaMonitoreo) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoFrecuenciaMonitoreo o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return frecuencia;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(CatalogoFrecuenciaMonitoreo obj, Usuario usuario){
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
	
	@SuppressWarnings("unchecked")
	public List<CatalogoFrecuenciaMonitoreo> findAll(){
		List<CatalogoFrecuenciaMonitoreo> frecuenciaList = new ArrayList<CatalogoFrecuenciaMonitoreo>();
		try {
			frecuenciaList = (ArrayList<CatalogoFrecuenciaMonitoreo>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoFrecuenciaMonitoreo o where o.estado = true order by o.orden")
					.getResultList();
			return frecuenciaList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return frecuenciaList;
	}
	
	@SuppressWarnings("unchecked")
	public List<CatalogoFrecuenciaMonitoreo> findByName(String nombres){
		List<CatalogoFrecuenciaMonitoreo> frecuenciaList = new ArrayList<CatalogoFrecuenciaMonitoreo>();
		List<String> nombresList=Arrays.asList(nombres.split(","));
		try {
			frecuenciaList = (ArrayList<CatalogoFrecuenciaMonitoreo>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoFrecuenciaMonitoreo o where o.estado = true and o.descripcion in :nombres order by o.orden")
					.setParameter("nombres", nombresList)
					.getResultList();
			return frecuenciaList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return frecuenciaList;
	}
	
}
