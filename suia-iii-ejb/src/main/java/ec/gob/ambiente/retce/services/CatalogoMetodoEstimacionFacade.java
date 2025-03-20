package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoMetodoEstimacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoMetodoEstimacionFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CatalogoMetodoEstimacion findById(Integer id){
		try {
			CatalogoMetodoEstimacion catalogoMetodoEstimacion = (CatalogoMetodoEstimacion) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoMetodoEstimacion o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			
			return catalogoMetodoEstimacion;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(CatalogoMetodoEstimacion obj, Usuario usuario){
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
	public List<CatalogoMetodoEstimacion> findAll(){
		List<CatalogoMetodoEstimacion> catalogoMetodoEstimacionList = new ArrayList<CatalogoMetodoEstimacion>();
		try {
			catalogoMetodoEstimacionList = (ArrayList<CatalogoMetodoEstimacion>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoMetodoEstimacion o where o.estado = true order by o.orden")
					.getResultList();
			
			return catalogoMetodoEstimacionList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return catalogoMetodoEstimacionList;
	}
	
	public CatalogoMetodoEstimacion findByNombre(String metodo){
		try {
			CatalogoMetodoEstimacion catalogoMetodoEstimacion = (CatalogoMetodoEstimacion) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoMetodoEstimacion o where o.descripcion = :metodo")
					.setParameter("metodo", metodo).getSingleResult();
			
			return catalogoMetodoEstimacion;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

}
