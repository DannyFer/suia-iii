package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoPeriodicidad;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoPeriodicidadFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public CatalogoPeriodicidad findById(Integer id){		
		try{
			CatalogoPeriodicidad catalogoPeriodicidad = (CatalogoPeriodicidad) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoPeriodicidad o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			return catalogoPeriodicidad;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(CatalogoPeriodicidad obj,Usuario usuario) {
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
	public List<CatalogoPeriodicidad> findAll(){
		List<CatalogoPeriodicidad> lista =new ArrayList<CatalogoPeriodicidad>();
		try{
			lista = (List<CatalogoPeriodicidad>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoPeriodicidad o where o.estado = true")									
					.getResultList();
			return lista;			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
}