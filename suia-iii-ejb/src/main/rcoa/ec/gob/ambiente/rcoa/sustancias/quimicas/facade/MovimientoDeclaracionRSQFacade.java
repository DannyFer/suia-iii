package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DeclaracionSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.MovimientoDeclaracionRSQ;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;


@Stateless
public class MovimientoDeclaracionRSQFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardar(MovimientoDeclaracionRSQ obj, Usuario usuario){			
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
	public List<MovimientoDeclaracionRSQ> obtenerMovPorDeclaracion(DeclaracionSustanciaQuimica declaracionSustanciaQuimica) {
		List<MovimientoDeclaracionRSQ> resultList=new ArrayList<>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT o FROM MovimientoDeclaracionRSQ o WHERE o.estado=true and o.declaracionSustanciaQuimica.id=:idDeclaracion "
							+ " ORDER BY 1");
			query.setParameter("idDeclaracion",
					declaracionSustanciaQuimica.getId());
			resultList = (List<MovimientoDeclaracionRSQ>) query.getResultList();		
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return resultList;
	}
		
	@SuppressWarnings("unchecked")
	public List<MovimientoDeclaracionRSQ> obtenerPorDeclaracion(DeclaracionSustanciaQuimica declaracionSustanciaQuimica) {
		List<MovimientoDeclaracionRSQ> resultList=new ArrayList<>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT o FROM MovimientoDeclaracionRSQ o WHERE o.estado=true and o.declaracionSustanciaQuimica.id=:idDeclaracion "
					+ "and o.tipoMovimiento.nombre not in(:im, :sm, :ex, :el, :co) ORDER BY 1");
			query.setParameter("idDeclaracion",
					declaracionSustanciaQuimica.getId());
			query.setParameter("im", "IMPORTACIÓN");
			query.setParameter("sm", "SIN MOVIMIENTOS");
			query.setParameter("ex", "EXPORTACIÓN");
			query.setParameter("el", "ELIMINACIÓN O DISPOSICIÓN FINAL");
			query.setParameter("co", "CONSUMO");
			resultList = (List<MovimientoDeclaracionRSQ>) query.getResultList();		
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return resultList;
	}
	
	public MovimientoDeclaracionRSQ obtenerPorUsuarioFacturaMes(String usuario,String numeroFactura,int mesDeclaracion) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM MovimientoDeclaracionRSQ o WHERE o.estado=true and o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuarioCreacion=:usuario and o.numeroFactura=:numeroFactura and o.declaracionSustanciaQuimica.mesDeclaracion=:mesDeclaracion ORDER BY 1 desc");
			query.setParameter("usuario", usuario);
			query.setParameter("numeroFactura", numeroFactura);
			query.setParameter("mesDeclaracion", mesDeclaracion);
			query.setMaxResults(1);
			return (MovimientoDeclaracionRSQ)query.getSingleResult();	
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}
	
	public MovimientoDeclaracionRSQ obtenerPorUsuarioMesAnio(String usuario,int mesDeclaracion, int anioDeclaracion) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM MovimientoDeclaracionRSQ o WHERE o.estado=true and o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuarioCreacion=:usuario and o.declaracionSustanciaQuimica.mesDeclaracion=:mesDeclaracion and o.declaracionSustanciaQuimica.anioDeclaracion=:anioDeclaracion ORDER BY 1 desc");
			query.setParameter("usuario", usuario);
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
			query.setMaxResults(1);
			return (MovimientoDeclaracionRSQ)query.getSingleResult();	
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MovimientoDeclaracionRSQ> obtenerListaPorUsuarioMesAnio(String usuario,int mesDeclaracion, int anioDeclaracion, String factura) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM MovimientoDeclaracionRSQ o WHERE o.estado=true and o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuarioCreacion=:usuario and o.declaracionSustanciaQuimica.mesDeclaracion=:mesDeclaracion and o.declaracionSustanciaQuimica.anioDeclaracion=:anioDeclaracion and o.numeroFactura = :factura ORDER BY 1 desc");
			query.setParameter("usuario", usuario);
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
			query.setParameter("factura", factura);
						
			return (List<MovimientoDeclaracionRSQ>)query.getResultList();	
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MovimientoDeclaracionRSQ> obtenerListaPorUsuarioFactura(String usuario,String factura) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM MovimientoDeclaracionRSQ o WHERE o.estado=true and o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuarioCreacion=:usuario and o.numeroFactura = :factura ORDER BY 1 asc");
			query.setParameter("usuario", usuario);
			query.setParameter("factura", factura);
						
			return (List<MovimientoDeclaracionRSQ>)query.getResultList();	
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MovimientoDeclaracionRSQ> obtenerListaPorUsuarioMesAnio(String usuario,int mesDeclaracion, int anioDeclaracion, String factura, int idSustancia, int idUsuario) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT o FROM MovimientoDeclaracionRSQ o "
							+ "WHERE "
							+ "((o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuario is null and o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuario.nombre =:usuario) "
							+ "or "
							+ "(o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuario is not null and o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuario.id=:idUsuario))"
							+ "and o.declaracionSustanciaQuimica.mesDeclaracion =:mesDeclaracion "
							+ "and o.declaracionSustanciaQuimica.anioDeclaracion =:anioDeclaracion "
							+ "and o.numeroFactura = :factura "
							+ "and o.declaracionSustanciaQuimica.sustanciaQuimica.id = :idSustancia "
							+ "and o.estado=true " 
							+ "ORDER BY 1 desc");
			query.setParameter("usuario", usuario);
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
			query.setParameter("factura", factura);
			query.setParameter("idSustancia", idSustancia);
			query.setParameter("idUsuario", idUsuario);
			
			List<MovimientoDeclaracionRSQ> lista = query.getResultList();			
						
			return lista;	
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MovimientoDeclaracionRSQ> obtenerListaPorUsuarioIdMesAnio(Integer usuario, int mesDeclaracion, int anioDeclaracion, String factura, int idSustancia) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT o FROM MovimientoDeclaracionRSQ o "
							+ "WHERE o.estado=true and o.operador.id = :usuario"
							+ "and o.declaracionSustanciaQuimica.mesDeclaracion=:mesDeclaracion "
							+ "and o.declaracionSustanciaQuimica.anioDeclaracion=:anioDeclaracion "
							+ "and o.numeroFactura = :factura "
							+ "and o.declaracionSustanciaQuimica.sustanciaQuimica.id = :idSustancia " 
							+ "ORDER BY 1 desc");
			query.setParameter("usuario", usuario);
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
			query.setParameter("factura", factura);
			query.setParameter("idSustancia", idSustancia);
			
			List<MovimientoDeclaracionRSQ> lista = query.getResultList();			
						
			return lista;	
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MovimientoDeclaracionRSQ> obtenerListaPorUsuarioMesAnioSustancia(String usuario,int mesDeclaracion, int anioDeclaracion, int idSustancia, int idUsuario) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT o FROM MovimientoDeclaracionRSQ o "
							+ "WHERE "
							+ "((o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuario is null and o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuario.nombre =:usuario) "
							+ "or "
							+ "(o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuario is not null and o.declaracionSustanciaQuimica.registroSustanciaQuimica.usuario.id=:idUsuario))"
							+ "and o.declaracionSustanciaQuimica.mesDeclaracion =:mesDeclaracion "
							+ "and o.declaracionSustanciaQuimica.anioDeclaracion =:anioDeclaracion "
							+ "and o.declaracionSustanciaQuimica.sustanciaQuimica.id = :idSustancia "
							+ "and o.estado=true " 
							+ "ORDER BY 1 desc");
			query.setParameter("usuario", usuario);
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
			query.setParameter("idSustancia", idSustancia);
			query.setParameter("idUsuario", idUsuario);
			
			List<MovimientoDeclaracionRSQ> lista = query.getResultList();			
						
			return lista;	
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<MovimientoDeclaracionRSQ> obtenerListaPorRegistroFacMesAnio(int idRegistro, int mesDeclaracion, int anioDeclaracion, String factura, int idSustancia, int idPresentacion) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT o FROM MovimientoDeclaracionRSQ o "
							+ "WHERE "							
							+ "o.declaracionSustanciaQuimica.mesDeclaracion = :mesDeclaracion "
							+ "and o.declaracionSustanciaQuimica.anioDeclaracion = :anioDeclaracion "
							+ "and o.numeroFactura = :factura "
							+ "and o.declaracionSustanciaQuimica.sustanciaQuimica.id = :idSustancia "
							+ "and o.estado=true "
							+ "and o.declaracionSustanciaQuimica.registroSustanciaQuimica.id = :idRegistro "
							+ "and o.tipoPresentacion.id = :idPresentacion " 
							+ "ORDER BY 1 desc");
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
			query.setParameter("factura", factura);
			query.setParameter("idSustancia", idSustancia);
			query.setParameter("idRegistro", idRegistro);
			query.setParameter("idPresentacion", idPresentacion);
			
			List<MovimientoDeclaracionRSQ> lista = query.getResultList();			
						
			return lista;	
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}
	
}
