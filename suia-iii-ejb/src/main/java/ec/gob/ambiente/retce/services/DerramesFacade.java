package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import ec.gob.ambiente.retce.model.Derrames;
import ec.gob.ambiente.retce.model.DerramesComponenteAfectado;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class DerramesFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public Derrames findById(Integer id){
		try {
			Derrames obj = (Derrames) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM Derrames o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return obj;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(Derrames obj, Usuario usuario){		
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
			obj.setCodigo(generarCodigo());
			if(obj.getEnviado()==null)
				obj.setEnviado(false);
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(obj);
	}
	
	public void save(DerramesComponenteAfectado obj, Usuario usuario){		
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
	public List<Derrames> findByUsuario(Usuario usuario){
		List<Derrames> lista = new ArrayList<Derrames>();
		try {
			lista = (ArrayList<Derrames>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM Derrames o where o.estado = true and o.usuarioCreacion = :usuario order by 1 desc")
					.setParameter("usuario", usuario.getNombre())
					.getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Derrames> findByInformacionBasica(InformacionProyecto informacionProyecto){
		List<Derrames> lista = new ArrayList<Derrames>();
		try {
			lista = (ArrayList<Derrames>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM Derrames o where o.estado = true and o.informacionProyecto.id = :informacionProyecto order by 1 desc")
					.setParameter("informacionProyecto", informacionProyecto.getId())
					.getResultList();
			return lista;
		} catch (NoResultException e) {
			return lista;
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			return lista;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<Derrames> findByArea(Area area){
		List<Derrames> lista = new ArrayList<Derrames>();
		try {
			lista = (ArrayList<Derrames>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM Derrames o where o.estado = true and o.enviado=true and o.informacionProyecto.areaSeguimiento.id = :id order by 1 desc")
					.setParameter("id", area.getId())
					.getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<DerramesComponenteAfectado> findByDerrames(Derrames derrames){
		List<DerramesComponenteAfectado> lista = new ArrayList<DerramesComponenteAfectado>();
		try {
			lista = (ArrayList<DerramesComponenteAfectado>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DerramesComponenteAfectado o where o.estado = true and o.derrames.id = :id order by 1 desc")
					.setParameter("id", derrames.getId())
					.getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	private String generarCodigo() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RETCE-SEA-"
					+ secuenciasFacade.getCurrentYear()					
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence("MAAE-RETCE-SEA",4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
