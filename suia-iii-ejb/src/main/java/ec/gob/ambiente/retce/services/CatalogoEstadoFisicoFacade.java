package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoEstadoFisico;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoEstadoFisicoFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public CatalogoEstadoFisico findById(Integer id){		
		try{
			CatalogoEstadoFisico catalogoEstadoFisico = (CatalogoEstadoFisico) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoEstadoFisico o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			return catalogoEstadoFisico;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(CatalogoEstadoFisico obj,Usuario usuario) {
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
	public List<CatalogoEstadoFisico> findAll(){
		List<CatalogoEstadoFisico> lista =new ArrayList<CatalogoEstadoFisico>();
		try{
			lista = (List<CatalogoEstadoFisico>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoEstadoFisico o where o.estado = true")									
					.getResultList();
			return lista;			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
}