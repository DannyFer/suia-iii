package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.FuenteFijaCombustion;
import ec.gob.ambiente.retce.model.TipoCombustibleRetce;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class TipoCombustibleRetceFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public TipoCombustibleRetce findById(Integer id){
		try {
			TipoCombustibleRetce tipoCombustible = (TipoCombustibleRetce) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM TipoCombustibleRetce o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return tipoCombustible;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(TipoCombustibleRetce obj, Usuario usuario){
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
	public List<TipoCombustibleRetce> findByFuenteFijaCombustion(FuenteFijaCombustion fuente){
		List<TipoCombustibleRetce> combustibles = new ArrayList<TipoCombustibleRetce>();
		try {
			combustibles = (ArrayList<TipoCombustibleRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM TipoCombustibleRetce o where o.estado = true and o.fuenteFijaCombustion.id = :id order by o.orden")
					.setParameter("id", fuente.getId()).getResultList();
			return combustibles;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return combustibles;
	}
	
}
