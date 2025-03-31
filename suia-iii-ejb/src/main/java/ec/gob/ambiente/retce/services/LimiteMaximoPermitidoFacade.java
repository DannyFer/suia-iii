package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.AutorizacionEmisiones;
import ec.gob.ambiente.retce.model.FuenteFijaCombustion;
import ec.gob.ambiente.retce.model.LimiteMaximoPermitido;
import ec.gob.ambiente.retce.model.TipoCombustibleRetce;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class LimiteMaximoPermitidoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public LimiteMaximoPermitido findById(Integer id){
		try {
			LimiteMaximoPermitido limite = (LimiteMaximoPermitido) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM LimiteMaximoPermitido o where o.id = :id")
					.getSingleResult();
			return limite;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(LimiteMaximoPermitido obj, Usuario usuario){
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
	public List<LimiteMaximoPermitido> findByAutorizacion(AutorizacionEmisiones autorizacion){
		List<LimiteMaximoPermitido> lista = new ArrayList<LimiteMaximoPermitido>();
		try {
			lista = (ArrayList<LimiteMaximoPermitido>) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM LimiteMaximoPermitido o where o.estado = true and autorizacionEmisiones.id = :id")
					.setParameter("id", autorizacion.getId()).getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<LimiteMaximoPermitido> findByAutorizacionCombustible(AutorizacionEmisiones autorizacion, TipoCombustibleRetce combustible){
		List<LimiteMaximoPermitido> lista = new ArrayList<LimiteMaximoPermitido>();
		try {
			lista = (ArrayList<LimiteMaximoPermitido>) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM LimiteMaximoPermitido o where o.estado = true and o.autorizacionEmisiones = :autorizacion and "
							+ "o.tipoCombustible = :combustible")
					.setParameter("autorizacion", autorizacion).setParameter("combustible", combustible).getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<LimiteMaximoPermitido> findByFuenteFija(FuenteFijaCombustion fuenteFija){
		List<LimiteMaximoPermitido> lista = new ArrayList<LimiteMaximoPermitido>();
		try {
			lista = (ArrayList<LimiteMaximoPermitido>) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM LimiteMaximoPermitido o where o.estado = true and "
							+ "o.fuenteFijaCombustion = :fuenteFija and o.autorizacionEmisiones = null and o.tipoCombustible = null")
					.setParameter("fuenteFija", fuenteFija).getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<LimiteMaximoPermitido> findByCombustible(TipoCombustibleRetce combustible){
		List<LimiteMaximoPermitido> lista = new ArrayList<LimiteMaximoPermitido>();
		try {
			lista = (ArrayList<LimiteMaximoPermitido>) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM LimiteMaximoPermitido o where o.estado = true and "
							+ "o.tipoCombustible = :combustible and o.autorizacionEmisiones = null")
					.setParameter("combustible", combustible).getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<LimiteMaximoPermitido> findByAutorizacionFuenteFija(AutorizacionEmisiones autorizacion, FuenteFijaCombustion fuente){
		List<LimiteMaximoPermitido> lista = new ArrayList<LimiteMaximoPermitido>();
		try {
			lista = (ArrayList<LimiteMaximoPermitido>) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM LimiteMaximoPermitido o where o.estado = true and o.autorizacionEmisiones = :autorizacion and "
							+ "o.fuenteFijaCombustion = :fuente")
					.setParameter("autorizacion", autorizacion).setParameter("fuente", fuente).getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<AutorizacionEmisiones> findBySoloFuenteFija(FuenteFijaCombustion fuenteFija){
		List<AutorizacionEmisiones> lista = new ArrayList<AutorizacionEmisiones>();
		try {
			lista = (ArrayList<AutorizacionEmisiones>) crudServiceBean.getEntityManager()
					.createQuery("SELECT Distinct o.autorizacionEmisiones FROM LimiteMaximoPermitido o where o.estado = true and "
							+ "o.fuenteFijaCombustion = :fuenteFija and o.tipoCombustible = null")
					.setParameter("fuenteFija", fuenteFija).getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<AutorizacionEmisiones> findBySoloCombustible(TipoCombustibleRetce combustible){
		List<AutorizacionEmisiones> lista = new ArrayList<AutorizacionEmisiones>();
		try {
			lista = (ArrayList<AutorizacionEmisiones>) crudServiceBean.getEntityManager()
					.createQuery("SELECT Distinct o.autorizacionEmisiones FROM LimiteMaximoPermitido o where o.estado = true and "
							+ "o.tipoCombustible = :combustible")
					.setParameter("combustible", combustible).getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	

}
