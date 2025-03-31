package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.Servicio;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class ServicioFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public Servicio findById(Integer id){		
		try{
			Servicio servicio = (Servicio) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM Servicio o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			return servicio;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(Servicio obj,Usuario usuario) {
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
	public List<Servicio> findByProyecto(InformacionProyecto informacionProyecto){		
		List<Servicio> servicio = new ArrayList<Servicio>();
		try{
			servicio = (List<Servicio>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM Servicio o where o.estado=true and o.informacionProyecto.id = :id")
					.setParameter("id", informacionProyecto.getId())					
					.getResultList();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return servicio;
	}
}