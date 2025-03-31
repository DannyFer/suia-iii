package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.SustanciaQuimicaPeligrosaDetalle;
import ec.gob.ambiente.retce.model.SustanciaQuimicaPeligrosaRetce;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class SustanciaQuimicaPeligrosaDetalleFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public SustanciaQuimicaPeligrosaDetalle findById(Integer id){
		try {
			SustanciaQuimicaPeligrosaDetalle sustanciaQuimicaDetalle = (SustanciaQuimicaPeligrosaDetalle) crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT o FROM SustanciaQuimicaPeligrosaDetalle o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			
			return sustanciaQuimicaDetalle;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(SustanciaQuimicaPeligrosaDetalle obj, Usuario usuario){
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
	public List<SustanciaQuimicaPeligrosaDetalle> findBySustanciaQuimica(SustanciaQuimicaPeligrosaRetce sustancia){
		List<SustanciaQuimicaPeligrosaDetalle> lista = new ArrayList<SustanciaQuimicaPeligrosaDetalle>();
		
		try {
			lista = (List<SustanciaQuimicaPeligrosaDetalle>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM SustanciaQuimicaPeligrosaDetalle o where o.sustanciaQuimicaPeligrosaRetce = :sustancia and o.estado = true")
					.setParameter("sustancia", sustancia).getResultList();
			
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}

}
