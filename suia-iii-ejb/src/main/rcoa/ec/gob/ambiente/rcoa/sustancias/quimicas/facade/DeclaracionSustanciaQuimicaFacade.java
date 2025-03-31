package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DeclaracionSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;


@Stateless
public class DeclaracionSustanciaQuimicaFacade {
	
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	/*@EJB
	private TarifasFacade tarifasFacade;*/
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	
	public void guardar(DeclaracionSustanciaQuimica obj, Usuario usuario){			
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
			obj.setTramite(generarCodigo());
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}	
		crudServiceBean.saveOrUpdate(obj);
	} 
	
	public DeclaracionSustanciaQuimica obtenerRegistroPorId(Integer id) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o WHERE o.estado=true and o.id=:id");
			query.setParameter("id", id);
			query.setMaxResults(1);
			return (DeclaracionSustanciaQuimica)query.getSingleResult();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return null;
	}
		
	@SuppressWarnings("unchecked")
	public List<DeclaracionSustanciaQuimica> obtenerPorRSQ(RegistroSustanciaQuimica registroSustanciaQuimica) {
		List<DeclaracionSustanciaQuimica> resultList=new ArrayList<>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ ORDER BY o.anioDeclaracion,o.mesDeclaracion,o.id");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());			
			resultList= (List<DeclaracionSustanciaQuimica>)query.getResultList();			
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return resultList;
	}
		
	public DeclaracionSustanciaQuimica obtenerPorRSQ(RegistroSustanciaQuimica registroSustanciaQuimica,int anioDeclaracion,int mesDeclaracion) {
		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.anioDeclaracion = :anioDeclaracion and o.mesDeclaracion = :mesDeclaracion ORDER BY 1 desc");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
			query.setMaxResults(1);
			DeclaracionSustanciaQuimica obj= (DeclaracionSustanciaQuimica)query.getSingleResult();
			return obj;
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public boolean existePagoPendiente(RegistroSustanciaQuimica registroSustanciaQuimica) {
		List<DeclaracionSustanciaQuimica> resultList=new ArrayList<>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.pagoPendiente = true ORDER BY 1 desc");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());			
			resultList= (List<DeclaracionSustanciaQuimica>)query.getResultList();
			
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return !resultList.isEmpty();
	}
		
	@SuppressWarnings("unchecked")
	public TaskSummaryCustom iniciarProceso(
			DeclaracionSustanciaQuimica declaracionRsq, Usuario usuario) {

		try {

			HashMap parametros = new HashMap<String, Object>();
			parametros.put("usuario_operador", usuario.getNombre());
			parametros.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, usuario.getNombre());
			parametros.put("idProyecto", declaracionRsq.getRegistroSustanciaQuimica().getProyectoLicenciaCoa() == null ? 
							declaracionRsq.getRegistroSustanciaQuimica().getId()
							: declaracionRsq.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getId());
			
			parametros.put("codigoProyecto", declaracionRsq.getTramite());
			parametros.put("anio_declaracion",declaracionRsq.getAnioDeclaracion());
			parametros.put("mes_declaracion",declaracionRsq.getMesDeclaracion());
			parametros.put("requiere_pago", declaracionRsq.getPagoPendiente());

			long processInstanceId = procesoFacade.iniciarProceso(usuario,Constantes.RCOA_DECLARACION_SUSTANCIA_QUIMICA,
					declaracionRsq.getTramite(), parametros);

			TaskSummary tarea = procesoFacade.getCurrenTask(usuario,processInstanceId);

			TaskSummaryCustom tareaCustom = new TaskSummaryCustomBuilder().fromSuiaIII1(
							tarea.getProcessId(),tarea,
							declaracionRsq.getTramite()).build();

			return tareaCustom;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public DeclaracionSustanciaQuimica obtenerUltimaDeclaracionPorRSQ(RegistroSustanciaQuimica registroSustanciaQuimica) {
		
		DeclaracionSustanciaQuimica declaracion = null;
		List<DeclaracionSustanciaQuimica> resultList=new ArrayList<>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ ORDER BY o.anioDeclaracion,o.mesDeclaracion desc");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());			
			resultList= (List<DeclaracionSustanciaQuimica>)query.getResultList();	
			
			if(resultList != null && !resultList.isEmpty()){
				declaracion = resultList.get(0);
			}
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return declaracion;
	}
	
	@SuppressWarnings("unchecked")
	public List<DeclaracionSustanciaQuimica> obtenerRSQPendientes(RegistroSustanciaQuimica registroSustanciaQuimica) {
		List<DeclaracionSustanciaQuimica> resultList=new ArrayList<>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.pagoPendiente = true ORDER BY o.anioDeclaracion,o.mesDeclaracion,o.id");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());			
			resultList= (List<DeclaracionSustanciaQuimica>)query.getResultList();			
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return resultList;
	}
	
	public DeclaracionSustanciaQuimica obtenerRSQPendiente(RegistroSustanciaQuimica registroSustanciaQuimica,int anioDeclaracion,int mesDeclaracion) {
		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.anioDeclaracion = :anioDeclaracion and o.mesDeclaracion = :mesDeclaracion and o.pagoPendiente = true ORDER BY 1 desc");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
			query.setMaxResults(1);
			DeclaracionSustanciaQuimica obj= (DeclaracionSustanciaQuimica)query.getSingleResult();
			return obj;
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<DeclaracionSustanciaQuimica> buscarPorSustancia(RegistroSustanciaQuimica registro, List<Integer> listaSustancias, int anioDeclaracion,int mesDeclaracion){
		
		List<DeclaracionSustanciaQuimica> lista = new ArrayList<DeclaracionSustanciaQuimica>();		
		
		try {			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o "
							+ "WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ "
							+ "and o.sustanciaQuimica.id in (:sustancia) and o.anioDeclaracion = :anioDeclaracion and o.mesDeclaracion = :mesDeclaracion "
							+ "ORDER BY 1 desc");
			query.setParameter("idRSQ", registro.getId());
			query.setParameter("sustancia", listaSustancias);
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
						
			lista= (List<DeclaracionSustanciaQuimica>)query.getResultList();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<DeclaracionSustanciaQuimica> buscarPorMesAnio(RegistroSustanciaQuimica registro, int anioDeclaracion,int mesDeclaracion){
		
		List<DeclaracionSustanciaQuimica> lista = new ArrayList<DeclaracionSustanciaQuimica>();		
		
		try {			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o "
							+ "WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ "
							+ "and o.anioDeclaracion = :anioDeclaracion and o.mesDeclaracion = :mesDeclaracion "
							+ "ORDER BY 1 desc");
			query.setParameter("idRSQ", registro.getId());			
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
						
			lista= (List<DeclaracionSustanciaQuimica>)query.getResultList();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	
	public DeclaracionSustanciaQuimica buscarPorSustanciaPeligrosa(RegistroSustanciaQuimica registro, SustanciaQuimicaPeligrosa sustancia, int anioDeclaracion, int mesDeclaracion){				
		try {			
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o "
					+ "WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.anioDeclaracion = :anioDeclaracion and "
					+ "o.mesDeclaracion = :mesDeclaracion and o.sustanciaQuimica.id = :sustancia ORDER BY 1 desc");
			query.setParameter("idRSQ", registro.getId());
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
			query.setParameter("sustancia", sustancia.getId());
			query.setMaxResults(1);
			DeclaracionSustanciaQuimica obj= (DeclaracionSustanciaQuimica)query.getSingleResult();
			return obj;			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String generarCodigo() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RSQ-DEC"
					+ "-"
					+ secuenciasFacade.getCurrentYear()					
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(Constantes.SIGLAS_INSTITUCION + "-RSQ-DEC-"+secuenciasFacade.getCurrentYear(),4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<DeclaracionSustanciaQuimica> obtenerPorRSQSustancia(RegistroSustanciaQuimica registroSustanciaQuimica,SustanciaQuimicaPeligrosa sustancia) {
		List<DeclaracionSustanciaQuimica> resultList = new ArrayList<>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT o FROM DeclaracionSustanciaQuimica o WHERE "
							+ "o.estado=true and o.registroSustanciaQuimica.id=:idRSQ "
							+ "and o.sustanciaQuimica = :sustancia "
							+ "ORDER BY o.anioDeclaracion,o.mesDeclaracion,o.id");
			
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());
			query.setParameter("sustancia", sustancia);
			
			resultList = (List<DeclaracionSustanciaQuimica>) query.getResultList();
		} catch (NoResultException nre) {
			// TODO
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	public RegistroSustanciaQuimica obtenerRegistroPorCodigo(String codigo) {		
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o.registroSustanciaQuimica FROM DeclaracionSustanciaQuimica o WHERE o.estado=true and o.tramite=:codigo");
			query.setParameter("codigo", codigo);
			query.setMaxResults(1);
			return (RegistroSustanciaQuimica)query.getSingleResult();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<DeclaracionSustanciaQuimica> buscarPorSustanciaDeclaracionEnviada(RegistroSustanciaQuimica registro, SustanciaQuimicaPeligrosa sustancia, int anioDeclaracion, int mesDeclaracion){				
		List<DeclaracionSustanciaQuimica> resultList = new ArrayList<>();
		try {			
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o "
					+ "WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.anioDeclaracion = :anioDeclaracion and "
					+ "o.mesDeclaracion = :mesDeclaracion and o.sustanciaQuimica.id = :sustancia and o.estadoDeclaracion.id = 241"
					+ "ORDER BY 1 desc");
			query.setParameter("idRSQ", registro.getId());
			query.setParameter("anioDeclaracion", anioDeclaracion);
			query.setParameter("mesDeclaracion", mesDeclaracion);
			query.setParameter("sustancia", sustancia.getId());
			
			resultList = (List<DeclaracionSustanciaQuimica>) query.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<DeclaracionSustanciaQuimica> obtenerDeclaracionesPendientesPorRSQSustancia(
			RegistroSustanciaQuimica registroSustanciaQuimica,
			SustanciaQuimicaPeligrosa sustancia) {
		List<DeclaracionSustanciaQuimica> resultList = new ArrayList<>();
		try {
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT o FROM DeclaracionSustanciaQuimica o WHERE o.estado=true and "
							+ "o.registroSustanciaQuimica.id=:idRSQ and o.pagoPendiente = true "
							+ "and o.sustanciaQuimica.id = :sustancia "
							+ "ORDER BY o.anioDeclaracion,o.mesDeclaracion,o.id");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());
			query.setParameter("sustancia", sustancia.getId());
			resultList = (List<DeclaracionSustanciaQuimica>) query
					.getResultList();
		} catch (NoResultException nre) {
			// TODO
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	public DeclaracionSustanciaQuimica buscarPorTramite(String tramite){				
		try {			
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o "
					+ "WHERE o.estado=true and o.tramite = :tramite");
			query.setParameter("tramite", tramite);
			query.setMaxResults(1);
			DeclaracionSustanciaQuimica obj= (DeclaracionSustanciaQuimica)query.getSingleResult();
			return obj;			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public DeclaracionSustanciaQuimica obtenerUltimaDeclaracionEnviadaPorRSQ(RegistroSustanciaQuimica registroSustanciaQuimica, SustanciaQuimicaPeligrosa sustancia) {
		
		DeclaracionSustanciaQuimica declaracion = null;
		List<DeclaracionSustanciaQuimica> resultList=new ArrayList<>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o "
				+ "WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.estadoDeclaracion.id = 241 "
				+ "and o.sustanciaQuimica.id = :idSustancia ORDER BY o.anioDeclaracion,o.mesDeclaracion desc");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());	
			query.setParameter("idSustancia", sustancia.getId());	
		
			resultList= (List<DeclaracionSustanciaQuimica>)query.getResultList();	
			
			if(resultList != null && !resultList.isEmpty()){
				declaracion = resultList.get(0);
			}
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return declaracion;
	}
	
	@SuppressWarnings("unchecked")
	public List<DeclaracionSustanciaQuimica> buscarDeclaracionesFaltantesPorRegistroSustanciaAnio(RegistroSustanciaQuimica registro, SustanciaQuimicaPeligrosa sustancia , int anioDeclaracion){
		
		List<DeclaracionSustanciaQuimica> lista = new ArrayList<DeclaracionSustanciaQuimica>();		
		
		try {			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM DeclaracionSustanciaQuimica o "
							+ "WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ "
							+ "and o.sustanciaQuimica.id = :idSustancia and o.anioDeclaracion = :anioDeclaracion "
							+ "and o.estadoDeclaracion.id = 240"
							+ "ORDER BY 1 desc");
			query.setParameter("idRSQ", registro.getId());
			query.setParameter("idSustancia", sustancia.getId());
			query.setParameter("anioDeclaracion", anioDeclaracion);
						
			lista= (List<DeclaracionSustanciaQuimica>)query.getResultList();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<DeclaracionSustanciaQuimica> obtenerPorRSQSustanciaAnio(RegistroSustanciaQuimica registroSustanciaQuimica,SustanciaQuimicaPeligrosa sustancia, int anio) {
		List<DeclaracionSustanciaQuimica> resultList = new ArrayList<>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery(
							"SELECT o FROM DeclaracionSustanciaQuimica o WHERE "
							+ "o.estado=true and o.registroSustanciaQuimica.id=:idRSQ "
							+ "and o.sustanciaQuimica = :sustancia "
							+ "and o.anioDeclaracion = :anio "
							+ "ORDER BY o.anioDeclaracion,o.mesDeclaracion,o.id");
			
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());
			query.setParameter("sustancia", sustancia);
			query.setParameter("anio", anio);
			
			resultList = (List<DeclaracionSustanciaQuimica>) query.getResultList();
		} catch (NoResultException nre) {
			// TODO
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

}
