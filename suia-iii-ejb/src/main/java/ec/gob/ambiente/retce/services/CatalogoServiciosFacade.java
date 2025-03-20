package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoServicios;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoServiciosFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public CatalogoServicios findById(Integer id){		
		try{
			CatalogoServicios catalogoServicios = (CatalogoServicios) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoServicios o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			return catalogoServicios;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(CatalogoServicios obj,Usuario usuario) {
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
	public List<CatalogoServicios> findAll(){
		List<CatalogoServicios> lista =new ArrayList<CatalogoServicios>();
		try{
			lista = (List<CatalogoServicios>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoServicios o where o.estado = true")									
					.getResultList();
			return lista;			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
}