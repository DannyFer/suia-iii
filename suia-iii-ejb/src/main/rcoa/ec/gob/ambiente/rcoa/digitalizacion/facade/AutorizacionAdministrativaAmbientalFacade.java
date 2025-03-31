package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.DocumentoResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class AutorizacionAdministrativaAmbientalFacade {
	
	private static final Logger LOG = Logger.getLogger(DocumentoResolucionAmbientalFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public String dblinkIdeMaae = Constantes.getDblinkIdeMaae();
	public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	public String dblinkSuiaSubsector=Constantes.getDblinkSectorSubsector();
	public String dblinkSuiaHidro=Constantes.getDblinkBpmsHyD();
	
	@SuppressWarnings("unchecked")
	public List<AreasSnapProvincia> getUbicacionSnapPorCodigoCapa() {
		Query sql = crudServiceBean
				.getEntityManager()
				.createQuery(
						"Select a from AreasSnapProvincia a where a.estado=true");		

		if (sql.getResultList().size() > 0)
			return sql.getResultList();

		return null;
	}
	
	//getDblinkIdeCartografia
	
	@SuppressWarnings("unchecked")
	public List<String> getAreasProtegidas() {
		Query query = crudServiceBean
				.getEntityManager()
				.createNativeQuery(
						"SELECT * FROM  dblink('"
								+ dblinkIdeMaae
								+ "',"
								+ "'select map, nam from h_demarcacion.fa210_snap_a')"
								+ " as (codigo character varying, nombre character varying)");
		
		List<Object> result = query.getResultList();
		if(result.size()>0){
			List<String> projects = new ArrayList<String>();
			for (Object object : result) {
				Object[] obj=(Object[])object;
				projects.add(obj[0] + " " + obj[1]);
			}
			
			return projects;
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getBosquesProtectores() {
		Query query = crudServiceBean
				.getEntityManager()
				.createNativeQuery(
						"SELECT * FROM  dblink('"
								+ dblinkIdeMaae
								+ "',"
								+ "'select nam from h_demarcacion.hc000_bvp_a')"
								+ " as (nombre character varying)");
		List<String> result = query.getResultList();
				
		return result;
	}
	
	public void completarTareasPendientes(String codigoProyecto, String tipoSistema, Integer idProyectoDigitalizacion){
		String sqlTareasPendientes="", sqlUpdateproyecto="";
		Query query, queryProyect;
		List<Object[]> resultList = new ArrayList<>();
		switch (tipoSistema) {
		case "0": // nuevo
		case "1":	//BDD_FISICO
			break;
		case "2":	//BDD_CUATRO_CATEGORIAS
			//actualizo el id de digitalizacion en el proyecto del suia_iii
			sqlUpdateproyecto = "update proyectolicenciaambiental  "
					+ " set enaa_id = "+idProyectoDigitalizacion
					+ " where id = ''"+codigoProyecto+"'';";
			queryProyect = crudServiceBean.getEntityManager().createNativeQuery("select  dblink_exec('"+dblinkSuiaVerde+"', '"+ sqlUpdateproyecto+ " ')as result ");
			queryProyect.getResultList();
			// busco las tareas pendientes 
			sqlTareasPendientes="select * from dblink('"+dblinkSuiaVerde+"',"
					+ "'select distinct dbid_, execution_ "
					+ "from jbpm4_task"
					+ " where execution_id_ like ''%"+codigoProyecto+"%''  ') "
					+ " as (idtarea Integer, idproceso Integer)";
			query = crudServiceBean.getEntityManager().createNativeQuery(sqlTareasPendientes);
			resultList = (List<Object[]>) query.getResultList();
			// si hay tareas pendientes las desabilito 
			if (resultList.size() > 0) {
	    		for (int i = 0; i < resultList.size(); i++) {
	    			Object[] objTarea = (Object[]) resultList.get(i);
	    			Integer idTarea, idProceso;
	    			idTarea = (Integer) objTarea[0];
	    			idProceso = (Integer) objTarea[1];
	    			
	    			Query sqlUpdateTarea = crudServiceBean.getEntityManager().createNativeQuery("select  dblink_exec('"+dblinkSuiaVerde+"',"
	    					+ " 'update jbpm4_task set execution_id_=execution_id_ ||''.Digitalizado'', assignee_ = assignee_ || ''.Digitalizado'' where dbid_ = "+idTarea+" and execution_ = "+idProceso+"  ') as result ");
	    			sqlUpdateTarea.getResultList();
	    		}
	    	}
			//en caso de que sea de hidrocarburos busco en la base de hidrocarburos
			// busco las tareas pendientes 
			sqlTareasPendientes="select * from dblink('"+dblinkSuiaHidro+"',"
					+ "'select distinct a.id, a.processinstanceid "
					+ "from task a inner join processinstancelog b using(processinstanceid) "
					+ " inner join variableinstancelog c on b.processinstanceid = c.processinstanceid "
					+ " where a.status in ( ''Reserved'', ''InProgress'') and c.value = ''"+codigoProyecto+"'' "
					+ "  ') "
					+ " as (idtarea Integer, idproceso Integer)";
			query = crudServiceBean.getEntityManager().createNativeQuery(sqlTareasPendientes);
			resultList = (List<Object[]>) query.getResultList();
			// si hay tareas pendientes las desabilito 
			if (resultList.size() > 0) {
	    		for (int i = 0; i < resultList.size(); i++) {
	    			Object[] objTarea = (Object[]) resultList.get(i);
	    			Integer idTarea, idProceso;
	    			idTarea = (Integer) objTarea[0];
	    			idProceso = (Integer) objTarea[1];
	    			Query sqlUpdateTarea = crudServiceBean.getEntityManager().createNativeQuery("select  dblink_exec('"+dblinkSuiaHidro+"',"
	    					+ " 'update task set status=''Digitalizado'' where id = "+idTarea+" and status in ( ''Reserved'', ''InProgress'') and processinstanceid = "+idProceso+"; "
	    					+ " "
	    					+ " update bamtasksummary set status=''Digitalizado'', enddate = now() where taskid = "+idTarea+" and status in ( ''Reserved'', ''InProgress'') and processinstanceid = "+idProceso+"; "
	    					+ " "
	    					+ " update processinstancelog set status=2, end_date = now(), processinstancedescription = '' Finalizado por proceso de digitalizacion enaa_id = "+idProyectoDigitalizacion+"'' where processinstanceid = "+idProceso+"  ') as result ");
	    			sqlUpdateTarea.getResultList();
	    		}
	    	}
			
			
			break;
		case "3":	//BDD_SECTOR_SUBSECTOR
			//actualizo el id de digitalizacion en el proyecto del suia_iii
			sqlUpdateproyecto = "update proyectolicenciaambiental  "
					+ " set enaa_id = "+idProyectoDigitalizacion
					+ " where id = ''"+codigoProyecto+"'';";
			queryProyect = crudServiceBean.getEntityManager().createNativeQuery("select  dblink_exec('"+dblinkSuiaSubsector+"', '"+ sqlUpdateproyecto+ " ')as result ");
			queryProyect.getResultList();
			// busco las tareas pendientes 
			sqlTareasPendientes="select * from dblink('"+dblinkSuiaSubsector+"',"
					+ "'select distinct dbid_, execution_ "
					+ "from jbpm4_task"
					+ " where execution_id_ like ''%"+codigoProyecto+"%''  ') "
					+ " as (idtarea Integer, idproceso Integer)";
			query = crudServiceBean.getEntityManager().createNativeQuery(sqlTareasPendientes);
			resultList = (List<Object[]>) query.getResultList();
			// si hay tareas pendientes las desabilito 
			if (resultList.size() > 0) {
	    		for (int i = 0; i < resultList.size(); i++) {
	    			Object[] objTarea = (Object[]) resultList.get(i);
	    			Integer idTarea, idProceso;
	    			idTarea = (Integer) objTarea[0];
	    			idProceso = (Integer) objTarea[1];
	    			
	    			Query sqlUpdateTarea = crudServiceBean.getEntityManager().createNativeQuery("select  dblink_exec('"+dblinkSuiaSubsector+"',"
	    					+ " 'update jbpm4_task set execution_id_=execution_id_ ||''.Digitalizado'', assignee_ = assignee_ || ''.Digitalizado'' where dbid_ = "+idTarea+" and execution_ = "+idProceso+"  ') as result ");
	    			sqlUpdateTarea.getResultList();
	    		}
	    	}
			break;
		case "4":	//REGULARIZACIÓN
			//actualizo el id de digitalizacion en el proyecto del suia_iii
			sqlUpdateproyecto = "update suia_iii.projects_environmental_licensing  "
					+ " set enaa_id = "+idProyectoDigitalizacion
					+ " where pren_code = '"+codigoProyecto+"';";
			queryProyect = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateproyecto);
			queryProyect.executeUpdate();
			// busco las tareas pendientes 
			sqlTareasPendientes="select * from dblink('"+dblinkBpmsSuiaiii+"',"
					+ "'select distinct a.id, a.processinstanceid "
					+ "from task a inner join processinstancelog b using(processinstanceid) "
					+ " inner join variableinstancelog c on b.processinstanceid = c.processinstanceid "
					+ " where a.status in ( ''Reserved'', ''InProgress'') and c.value = ''"+codigoProyecto+"'' "
					+ " and b.processid not in (''rcoa.DigitalizacionProyectos'')  ') "
					//+ " and b.processid  in (''mae-procesos.registro-ambiental'', ''SUIA.LicenciaAmbiental'', ''mae-procesos.RegistroAmbiental'')  ') "
					+ " as (idtarea Integer, idproceso Integer)";
			query = crudServiceBean.getEntityManager().createNativeQuery(sqlTareasPendientes);
			resultList = (List<Object[]>) query.getResultList();
			// si hay tareas pendientes las desabilito 
			if (resultList.size() > 0) {
	    		for (int i = 0; i < resultList.size(); i++) {
	    			Object[] objTarea = (Object[]) resultList.get(i);
	    			Integer idTarea, idProceso;
	    			idTarea = (Integer) objTarea[0];
	    			idProceso = (Integer) objTarea[1];
	    			Query sqlUpdateTarea = crudServiceBean.getEntityManager().createNativeQuery("select  dblink_exec('"+dblinkBpmsSuiaiii+"',"
	    					+ " 'update task set status=''Digitalizado'' where id = "+idTarea+" and status in ( ''Reserved'', ''InProgress'') and processinstanceid = "+idProceso+"; "
	    					+ " "
	    					+ " update bamtasksummary set status=''Digitalizado'', enddate = now() where taskid = "+idTarea+" and status in ( ''Reserved'', ''InProgress'') and processinstanceid = "+idProceso+"; "
	    					+ " "
	    					+ " update processinstancelog set status=2, end_date = now(), processinstancedescription = '' Finalizado por proceso de digitalizacion enaa_id = "+idProyectoDigitalizacion+"'' where processinstanceid = "+idProceso+"  ') as result ");
	    			sqlUpdateTarea.getResultList();
	    		}
	    	}
			break;
		default:
			break;
		}
	}
	
	public void save(AutorizacionAdministrativaAmbiental obj, Usuario usuario){
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
	public List<AutorizacionAdministrativaAmbiental> obtenerNumeroResolucion(AutorizacionAdministrativaAmbiental objetoAAA){
		try {
			List<AutorizacionAdministrativaAmbiental> lista = new ArrayList<>();
			Integer idDigitalizacion=0;
			if(objetoAAA.getId() != null)
				idDigitalizacion = objetoAAA.getId();
			Query query = crudServiceBean.getEntityManager().createQuery("Select a from AutorizacionAdministrativaAmbiental a where a.id != :id and a.resolucion = :resolucion and a.estado = true");
			query.setParameter("resolucion", objetoAAA.getResolucion());
			query.setParameter("id", idDigitalizacion);
			lista = (List<AutorizacionAdministrativaAmbiental>) query.getResultList();			
			return lista;
		} catch (Exception e) {
			LOG.error("Error al realizar la consulta", e);
		}
		return null;
	}
	
	public List<InterseccionProyecto> listaIntersecciones(ProyectoLicenciamientoAmbiental proyecto){
		try {
			List<InterseccionProyecto> lista = new ArrayList<InterseccionProyecto>();
			Query query = crudServiceBean.getEntityManager().createQuery("Select a from InterseccionProyecto a where a.proyecto = :proyecto and a.estado = true");
			query.setParameter("proyecto", proyecto);
			lista = (List<InterseccionProyecto>) query.getResultList();
			return lista;
		} catch (Exception e) {
			LOG.error("Error al realizar la consulta", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public AutorizacionAdministrativaAmbiental obtenerAAAPorId(Integer idProyecto){
		try {
			List<AutorizacionAdministrativaAmbiental> lista = new ArrayList<>();
			Query query = crudServiceBean.getEntityManager().createQuery("Select a from AutorizacionAdministrativaAmbiental a where a.id = :codigoProyecto and a.estado = true");
			query.setParameter("codigoProyecto", idProyecto);
			lista = (List<AutorizacionAdministrativaAmbiental>) query.getResultList();
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}
		} catch (Exception e) {
			LOG.error("Error al realizar la consulta", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public AutorizacionAdministrativaAmbiental obtenerAAAPorCodigoProyecto(String codigoProyecto){
		try {
			List<AutorizacionAdministrativaAmbiental> lista = new ArrayList<>();
			Query query = crudServiceBean.getEntityManager().createQuery("Select a from AutorizacionAdministrativaAmbiental a where a.codigoProyecto = :codigoProyecto and a.estado = true");
			query.setParameter("codigoProyecto", codigoProyecto);
			lista = (List<AutorizacionAdministrativaAmbiental>) query.getResultList();
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}
		} catch (Exception e) {
			LOG.error("Error al realizar la consulta", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public AutorizacionAdministrativaAmbiental obtenerAAAPorIdProyecto(Integer idProyecto, Integer sistema){
		try {
			List<AutorizacionAdministrativaAmbiental> lista = new ArrayList<>();
			Query query = crudServiceBean.getEntityManager().createQuery("Select a from AutorizacionAdministrativaAmbiental a where a.idProyecto = :codigoProyecto and a.sistema = :sistema and a.estado = true");
			query.setParameter("codigoProyecto", idProyecto);
			query.setParameter("sistema", sistema);
			lista = (List<AutorizacionAdministrativaAmbiental>) query.getResultList();
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}
		} catch (Exception e) {
			LOG.error("Error al realizar la consulta", e);
		}
		return null;
	}

	/**********************************************************************************************************************
	 * 
	 * 		funciones listado de proyectos
	 * 
	 * ******************************************************************************************************************/

	public List<ProyectoCustom> listarProyectosDigitalizados (String nombre, String codigo, String sector,String siglasResponsable, String permiso, Usuario usuario, 
			boolean rolAdmin,  boolean rolEnte, boolean rolPatrimonio, boolean delimitado, Integer limite, Integer offset) {
		CriteriaBuilder cb = crudServiceBean.getEntityManager().getCriteriaBuilder();

		Metamodel m = crudServiceBean.getEntityManager().getMetamodel();
		EntityType<AutorizacionAdministrativaAmbiental> consultaProyectoLicencia = m.entity(AutorizacionAdministrativaAmbiental.class);
		CriteriaQuery<ProyectoCustom> cq = cb.createQuery(ProyectoCustom.class);

		Root<AutorizacionAdministrativaAmbiental> rootProyectoCiiu = cq.from(consultaProyectoLicencia);
		//Join<AutorizacionAdministrativaAmbiental, Usuario> rootProyectoLicencia = rootProyectoCiiu.join("usuario", JoinType.INNER);
		cq.orderBy(cb.asc(rootProyectoCiiu.get("codigoProyecto")));
		Join<AutorizacionAdministrativaAmbiental, Area> area = rootProyectoCiiu.join("areaResponsableControl", JoinType.INNER);
		
		Join<AutorizacionAdministrativaAmbiental, CatalogoCIUU> catalogo = rootProyectoCiiu.join("catalogoCIUU", JoinType.INNER);
		Join<AutorizacionAdministrativaAmbiental, TipoSector> tipoSector = catalogo.join("tipoSector", JoinType.INNER);
		
		
		
		Predicate predicado = cb.conjunction();
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("estado"), true));
		
		if (!nombre.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoCiiu.<String>get("nombreProyecto")),  "%" + nombre.toLowerCase() + "%"));
		}
		if (!codigo.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoCiiu.<String>get("codigoProyecto")),  "%" + codigo.toLowerCase() + "%"));
		}
		if (!sector.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(tipoSector.<String>get("nombre")),  "%" + sector.toLowerCase() + "%"));
		}
		if (!siglasResponsable.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(area.<String>get("areaAbbreviation")),  "%" + siglasResponsable.toLowerCase() + "%"));
		}
		
		if(!rolAdmin) {
			List<Integer> listaAreasUsuario  = new ArrayList<Integer>();
			
			for(AreaUsuario areaUs : usuario.getListaAreaUsuario()){
				listaAreasUsuario.add(areaUs.getArea().getId());
				// incluyo el area padre para la busqueda
				if(areaUs.getArea().getArea() != null){
					if(!listaAreasUsuario.contains(areaUs.getArea().getArea())){
						listaAreasUsuario.add(areaUs.getArea().getArea().getId());
					}
				}
			}
			
			if(!rolEnte && !rolPatrimonio) {
				//Direccion provincial
				Expression<Integer> listaAreas = area.get("id");
				Predicate predicado1 = cb.and(predicado, listaAreas.in(listaAreasUsuario));
				
				Expression<Integer> listaAreasPadres = area.get("area").get("id");
				Predicate predicado2 = cb.or(predicado1, cb.and(predicado, listaAreasPadres.in(listaAreasUsuario)));
				
				predicado = cb.and(predicado, predicado2);
				
			} else {
				if(rolEnte) {
					//nno estrategicos
					Expression<Integer> listaAreas = area.get("id");
					predicado = cb.and(predicado, cb.and(predicado, listaAreas.in(listaAreasUsuario)));
				}
				if (rolPatrimonio) {

				}
			}
		}
		
		cq.multiselect(rootProyectoCiiu.get("id"), rootProyectoCiiu.get("codigoProyecto"),
				rootProyectoCiiu.get("nombreProyecto"), rootProyectoCiiu.get("fechaCreacion"),
				tipoSector.get("nombre"), rootProyectoCiiu.get("autorizacionAdministrativaAmbiental"),
				rootProyectoCiiu.get("nombreProyecto"),
				area.get("areaAbbreviation"), area.get("areaName"))
				.where(predicado);
				;
		
		if(delimitado)
			return this.crudServiceBean.getEntityManager().createQuery(cq) .setFirstResult(offset).setMaxResults(limite).getResultList();
		else
			return this.crudServiceBean.getEntityManager().createQuery(cq).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public AutorizacionAdministrativaAmbiental obtenerAAAPorResolucionAmbiental(String codigoResolucion){
		try {
			List<AutorizacionAdministrativaAmbiental> lista = new ArrayList<>();
			Query query = crudServiceBean.getEntityManager().createQuery("Select a from AutorizacionAdministrativaAmbiental a where a.resolucion = :codigoResolucion and a.estado = true");
			query.setParameter("codigoResolucion", codigoResolucion);
			lista = (List<AutorizacionAdministrativaAmbiental>) query.getResultList();
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}
		} catch (Exception e) {
			LOG.error("Error al realizar la consulta", e);
		}
		return null;
	}	
	
	public void desactivarInstanciaRegistroambiental(Usuario usuario, Long idProceso){
		Query sqlUpdateTarea = crudServiceBean.getEntityManager().createNativeQuery("select  dblink_exec('"+dblinkBpmsSuiaiii+"',"
				+ " 'update task set status=''Existed'' where status in ( ''Reserved'', ''InProgress'') and processinstanceid = "+idProceso+"; "
				+ " "
				+ " update bamtasksummary set status=''Existed'', enddate = now() where status in ( ''Reserved'', ''InProgress'') and processinstanceid = "+idProceso+"; "
				+ " "
			+ " update processinstancelog set status=4, end_date = now(), processinstancedescription = '' Desactivado para iniciar nuevo proceso de Registro ambiental con PPC '' where processinstanceid = "+idProceso+"  ') as result ");
		sqlUpdateTarea.getSingleResult();
	}
}
