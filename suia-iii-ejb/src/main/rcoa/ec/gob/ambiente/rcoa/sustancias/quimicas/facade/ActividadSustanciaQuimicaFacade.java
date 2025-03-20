package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.CaracteristicaActividad;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.Usuario;


@Stateless
public class ActividadSustanciaQuimicaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardar(ActividadSustancia obj, Usuario usuario){			
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
	public List<ActividadSustancia> obtenerActividadesPorRSQ(RegistroSustanciaQuimica registroSustanciaQuimica) {
		
		List<ActividadSustancia> resultList=new ArrayList<ActividadSustancia>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM ActividadSustancia o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.caracteristicaActividad != null ORDER BY 1");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());			
			resultList=(List<ActividadSustancia>)query.getResultList();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<CaracteristicaActividad> obtenerCaracteristicasActividades() {
		
		List<CaracteristicaActividad> resultList=new ArrayList<CaracteristicaActividad>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM CaracteristicaActividad o WHERE o.estado=true and o.actividadNivel.estado=true ORDER BY o.actividadNivel.orden,o.orden");						
			resultList=(List<CaracteristicaActividad>)query.getResultList();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<ActividadSustancia> obtenerActividadesPorRSQImportacion(RegistroSustanciaQuimica registroSustanciaQuimica) {
		
		List<ActividadSustancia> resultList=new ArrayList<ActividadSustancia>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT o FROM ActividadSustancia o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.caracteristicaActividad.id = 1 and o.actividadSeleccionada = true ORDER BY 1");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());			
			resultList=(List<ActividadSustancia>)query.getResultList();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public ActividadSustancia obtenerActividadesPorId(Integer id) {
		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT o FROM ActividadSustancia o WHERE o.estado=true and o.id=:id");
			query.setParameter("id", id);			
			List<ActividadSustancia> resultList=(List<ActividadSustancia>)query.getResultList();
			
			if(resultList != null && !resultList.isEmpty()){
				return resultList.get(0);
			}
			
			return null;
		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<GestionarProductosQuimicosProyectoAmbiental> obtenerActividadesPorRSQDistinct(RegistroSustanciaQuimica registroSustanciaQuimica) {
		
		List<GestionarProductosQuimicosProyectoAmbiental> resultList=new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT DISTINCT o.gestionarProductosQuimicosProyectoAmbiental FROM ActividadSustancia o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ ORDER BY 1");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());			
			resultList=(List<GestionarProductosQuimicosProyectoAmbiental>)query.getResultList();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return resultList;
	}
			
	@SuppressWarnings("unchecked")
	public List<ActividadSustancia> obtenerActividadesSeleccionadasImpSusPorRSQ(RegistroSustanciaQuimica registroSustanciaQuimica) {
		
		List<ActividadSustancia> resultList=new ArrayList<ActividadSustancia>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM ActividadSustancia o "
					+ "WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ "
					+ "and o.actividadSeleccionada = true "
					+ "and o.caracteristicaActividad.id = 1 ORDER BY 1");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());			
			resultList=(List<ActividadSustancia>)query.getResultList();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return resultList;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<GestionarProductosQuimicosProyectoAmbiental> obtenerSustanciasPorRSQDistinct(RegistroSustanciaQuimica registroSustanciaQuimica, SustanciaQuimicaPeligrosa sustancia) {
		
		List<GestionarProductosQuimicosProyectoAmbiental> resultList=new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT DISTINCT o.gestionarProductosQuimicosProyectoAmbiental "
					+ "FROM ActividadSustancia o WHERE o.estado=true and "
					+ "o.registroSustanciaQuimica.id=:idRSQ and o.gestionarProductosQuimicosProyectoAmbiental.sustanciaquimica = :sustancia ORDER BY 1");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());		
			query.setParameter("sustancia", sustancia);
			resultList=(List<GestionarProductosQuimicosProyectoAmbiental>)query.getResultList();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<ActividadSustancia> obtenerActividadesSeleccionadasPorProductosQuimicos(GestionarProductosQuimicosProyectoAmbiental producto) {
		
		List<ActividadSustancia> resultList=new ArrayList<ActividadSustancia>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM ActividadSustancia o "
					+ "WHERE o.estado=true "
					+ "and o.gestionarProductosQuimicosProyectoAmbiental.id = :id ORDER BY 1");
			query.setParameter("id", producto.getId());			
			resultList=(List<ActividadSustancia>)query.getResultList();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<ActividadSustancia> obtenerActividadesImportacion(RegistroSustanciaQuimica registroSustanciaQuimica, SustanciaQuimicaPeligrosa sustancia) {

		List<ActividadSustancia> resultList = new ArrayList<ActividadSustancia>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT o FROM ActividadSustancia o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ "
							+ "and o.caracteristicaActividad.id = 1 and " 
							+ "o.gestionarProductosQuimicosProyectoAmbiental.sustanciaquimica.id = :idSustancia ORDER BY 1");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());
			query.setParameter("idSustancia", sustancia.getId());
			resultList = (List<ActividadSustancia>) query.getResultList();
		} catch (NoResultException nre) {

		} catch (Exception e) {
			e.printStackTrace();

		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<ActividadSustancia> obtenerActividadesSeleccionadasPorProductosQuimicosImportacion(GestionarProductosQuimicosProyectoAmbiental producto) {
		
		List<ActividadSustancia> resultList=new ArrayList<ActividadSustancia>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM ActividadSustancia o "
					+ "WHERE o.estado=true "
					+ "and o.gestionarProductosQuimicosProyectoAmbiental.id = :id and o.caracteristicaActividad.id = 1 ORDER BY 1");
			query.setParameter("id", producto.getId());			
			resultList=(List<ActividadSustancia>)query.getResultList();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return resultList;
	}

}
