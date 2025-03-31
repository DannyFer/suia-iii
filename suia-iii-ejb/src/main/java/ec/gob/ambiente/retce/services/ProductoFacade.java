package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.Producto;
import ec.gob.ambiente.retce.model.Servicio;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class ProductoFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public Producto findById(Integer id){		
		try{
			Producto producto = (Producto) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM Producto o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			return producto;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(Producto obj,Usuario usuario) {
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
	public List<Producto> findByProyecto(InformacionProyecto informacionProyecto){		
		List<Producto> producto = new ArrayList<Producto>();
		try{
			producto = (List<Producto>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM Producto o where o.estado=true and o.informacionProyecto.id = :id")
					.setParameter("id", informacionProyecto.getId())					
					.getResultList();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return producto;
	}
}
