package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoUnidades;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoUnidadesFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public CatalogoUnidades findById(Integer id){		
		try{
			CatalogoUnidades catalogoUnidades = (CatalogoUnidades) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoUnidades o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			return catalogoUnidades;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(CatalogoUnidades obj,Usuario usuario) {
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
	public List<CatalogoUnidades> findAll(){
		List<CatalogoUnidades> lista =new ArrayList<CatalogoUnidades>();
		try{
			lista = (List<CatalogoUnidades>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoUnidades o where o.estado = true")									
					.getResultList();
			return lista;			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<CatalogoUnidades> findByParametro(String parametro){
		List<CatalogoUnidades> lista =new ArrayList<CatalogoUnidades>();
		try{
			lista = (List<CatalogoUnidades>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoUnidades o where o.estado = true and o.parametro = :parametro order by o.orden").setParameter("parametro", parametro)							
					.getResultList();
			return lista;			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	
}