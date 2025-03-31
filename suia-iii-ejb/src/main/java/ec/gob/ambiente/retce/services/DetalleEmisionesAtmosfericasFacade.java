package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DetalleEmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class DetalleEmisionesAtmosfericasFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public DetalleEmisionesAtmosfericas findById(Integer id){
		try {
			DetalleEmisionesAtmosfericas detalleEmisionesAtmosfericas = (DetalleEmisionesAtmosfericas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleEmisionesAtmosfericas o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			
			return detalleEmisionesAtmosfericas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(DetalleEmisionesAtmosfericas obj, Usuario usuario){
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
	public List<DetalleEmisionesAtmosfericas> findByEmisionesAtmosfericas(EmisionesAtmosfericas emision){
		List<DetalleEmisionesAtmosfericas> lista = new ArrayList<DetalleEmisionesAtmosfericas>();
		try {
			lista = (ArrayList<DetalleEmisionesAtmosfericas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleEmisionesAtmosfericas o where o.estado = true and "
							+ "o.emisionesAtmosfericas.id = :id and o.idRegistroOriginal = null order by 1 desc")
					.setParameter("id", emision.getId()).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleEmisionesAtmosfericas> findByEmisionesAtmosfericasOrd(EmisionesAtmosfericas emision){
		List<DetalleEmisionesAtmosfericas> lista = new ArrayList<DetalleEmisionesAtmosfericas>();
		try {
			lista = (ArrayList<DetalleEmisionesAtmosfericas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleEmisionesAtmosfericas o where o.estado = true and "
							+ "o.emisionesAtmosfericas.id = :id and o.idRegistroOriginal = null order by 1 asc")
					.setParameter("id", emision.getId()).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleEmisionesAtmosfericas> findByIdHistory(Integer id, Integer numObservacion){
		List<DetalleEmisionesAtmosfericas> lista = new ArrayList<DetalleEmisionesAtmosfericas>();
		try {
			lista = (ArrayList<DetalleEmisionesAtmosfericas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleEmisionesAtmosfericas o where o.estado = true and "
							+ "o.idRegistroOriginal = :id and o.numeroObservacion = :numObservacion order by 1 desc")
					.setParameter("id", id).setParameter("numObservacion", numObservacion).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleEmisionesAtmosfericas> findByEmisionesAtmosfericasBdd(EmisionesAtmosfericas emision){
		List<DetalleEmisionesAtmosfericas> lista = new ArrayList<DetalleEmisionesAtmosfericas>();
		try {
			lista = (ArrayList<DetalleEmisionesAtmosfericas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleEmisionesAtmosfericas o where o.estado = true and "
							+ "o.emisionesAtmosfericas.id = :id and o.idRegistroOriginal is not null order by 1 desc")
					.setParameter("id", emision.getId()).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleEmisionesAtmosfericas> findByIdIdRegistroOriginal(Integer id){
		List<DetalleEmisionesAtmosfericas> lista = new ArrayList<DetalleEmisionesAtmosfericas>();
		try {
			lista = (ArrayList<DetalleEmisionesAtmosfericas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleEmisionesAtmosfericas o where o.estado = true and "
							+ "o.idRegistroOriginal = :id order by 1 desc")
					.setParameter("id", id).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

}
