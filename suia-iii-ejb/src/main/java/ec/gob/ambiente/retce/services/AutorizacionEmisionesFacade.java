package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.AutorizacionEmisiones;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class AutorizacionEmisionesFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public AutorizacionEmisiones findById(Integer id){
		try{
			AutorizacionEmisiones autorizacionesEmisiones = (AutorizacionEmisiones) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT a FROM AutorizacionEmisiones a where a.id = :id")
					.setParameter("id", id).getSingleResult();
			return autorizacionesEmisiones;
		}catch(RuntimeException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(AutorizacionEmisiones obj, Usuario usuario){
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
	public List<AutorizacionEmisiones> findAll(){
		
		List<AutorizacionEmisiones> autorizacionEmisionesList = new ArrayList<AutorizacionEmisiones>();
		try {
			autorizacionEmisionesList = (ArrayList<AutorizacionEmisiones>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT a FROM AutorizacionEmisiones a where a.estado = true order  by a.orden")
					.getResultList();
			
			return autorizacionEmisionesList;			
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return autorizacionEmisionesList;
	}

}
