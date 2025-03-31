package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class UbicacionDigitalizacionFacade {
	private static final Logger LOG = Logger.getLogger(UbicacionDigitalizacionFacade.class);
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardar(UbicacionDigitalizacion obj , Usuario usuario){
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
	
	public List<UbicacionDigitalizacion> obtenerUbicacinesPorProyecto(Integer idDigitalizacion, Integer tipo){
		List<UbicacionDigitalizacion> listaUbicaciones = new ArrayList<UbicacionDigitalizacion>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("Select a from UbicacionDigitalizacion a where a.autorizacionAdministrativaAmbiental.id = :id and a.tipoIngreso = :tipo and a.estado = true");
			query.setParameter("id", idDigitalizacion);
			query.setParameter("tipo", tipo);
			listaUbicaciones = (List<UbicacionDigitalizacion>) query.getResultList();
			if(listaUbicaciones != null && listaUbicaciones.size() > 0)
				return listaUbicaciones;
		} catch (Exception e) {
			LOG.error("Error al realizar la consulta", e);
		}
		return new ArrayList<UbicacionDigitalizacion>();
	}
	
	public List<UbicacionesGeografica> obtenerTodasUbicacinesPorProyecto(Integer idDigitalizacion){
		List<UbicacionesGeografica> listaUbicaciones = new ArrayList<UbicacionesGeografica>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("Select a.ubicacionesGeografica from UbicacionDigitalizacion a where a.autorizacionAdministrativaAmbiental.id = :id and a.estado = true");
			query.setParameter("id", idDigitalizacion);
			listaUbicaciones = (List<UbicacionesGeografica>) query.getResultList();
			if(listaUbicaciones != null && listaUbicaciones.size() > 0)
				return listaUbicaciones;
		} catch (Exception e) {
			LOG.error("Error al realizar la consulta", e);
		}
		return new ArrayList<UbicacionesGeografica>();
	}
	
	public List<UbicacionDigitalizacion> obtenerUbicacinesPorProyectoPorSistema(Integer idDigitalizacion, Integer tipo, String sistemaR, String zona){
		List<UbicacionDigitalizacion> listaUbicaciones = new ArrayList<UbicacionDigitalizacion>();
		try {
			Query query=null;
			if(zona == null && sistemaR == null)
				query = crudServiceBean.getEntityManager().createQuery("Select a from UbicacionDigitalizacion a where a.autorizacionAdministrativaAmbiental.id = :id and a.tipoIngreso = :tipo and  a.estado = true");
			else if(zona != null)
				query = crudServiceBean.getEntityManager().createQuery("Select a from UbicacionDigitalizacion a where a.autorizacionAdministrativaAmbiental.id = :id and a.tipoIngreso = :tipo and a.sistemaReferencia = :sistema  and a.zona = :zona and  a.estado = true");
			else
				query = crudServiceBean.getEntityManager().createQuery("Select a from UbicacionDigitalizacion a where a.autorizacionAdministrativaAmbiental.id = :id and a.tipoIngreso = :tipo and a.sistemaReferencia = :sistema  and  a.estado = true");
			query.setParameter("id", idDigitalizacion);
			query.setParameter("tipo", tipo);
			if(sistemaR != null)
				query.setParameter("sistema", sistemaR);
			if(zona != null)
				query.setParameter("zona", zona);
			listaUbicaciones = (List<UbicacionDigitalizacion>) query.getResultList();
			if(listaUbicaciones != null && listaUbicaciones.size() > 0)
				return listaUbicaciones;
			else{
				// busco ubicaciones guardadas con la version anterior 
				query = crudServiceBean.getEntityManager().createQuery("Select a from UbicacionDigitalizacion a where a.autorizacionAdministrativaAmbiental.id = :id and a.tipoIngreso in(1, 2) and a.sistemaReferencia is null  and a.zona is null and  a.estado = true");
				query.setParameter("id", idDigitalizacion);
				listaUbicaciones = (List<UbicacionDigitalizacion>) query.getResultList();
				if(listaUbicaciones != null && listaUbicaciones.size() > 0)
					return listaUbicaciones;
			}
		} catch (Exception e) {
			LOG.error("Error al realizar la consulta", e);
		}
		return new ArrayList<UbicacionDigitalizacion>();
	}
}
