package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoProductos;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoProductosFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public CatalogoProductos findById(Integer id){		
		try{
			CatalogoProductos catalogoProductos = (CatalogoProductos) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoProductos o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			return catalogoProductos;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(CatalogoProductos obj,Usuario usuario) {
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
	public List<CatalogoProductos> findAll(){
		List<CatalogoProductos> lista =new ArrayList<CatalogoProductos>();
		try{
			lista = (List<CatalogoProductos>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoProductos o where o.estado = true")									
					.getResultList();
			return lista;			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
}