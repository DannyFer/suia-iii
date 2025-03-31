package ec.gob.ambiente.rcoa.facade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;



@Stateless
public class ProyectoLicenciaCoaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	@EJB
	private UsuarioFacade usuarioFacade;

	public ProyectoLicenciaCoa guardar(ProyectoLicenciaCoa proyecto) {
		return crudServiceBean.saveOrUpdate(proyecto);
	}

	public ProyectoLicenciaCoa buscarProyecto(String codigo) {
		ProyectoLicenciaCoa obj = new ProyectoLicenciaCoa();
		Query sql = crudServiceBean.getEntityManager().createQuery(
				"Select p from ProyectoLicenciaCoa p where p.codigoUnicoAmbiental=:codigo and p.estado=true");
		sql.setParameter("codigo", codigo);
		if (sql.getResultList().size() > 0)
			obj = (ProyectoLicenciaCoa) sql.getSingleResult();

		return obj;

	}

	public ProyectoLicenciaCoa buscarProyectoPorId(Integer idProyecto) {
		ProyectoLicenciaCoa obj = new ProyectoLicenciaCoa();
		Query sql = crudServiceBean.getEntityManager()
				.createQuery("Select p from ProyectoLicenciaCoa p where p.id=:idProyecto and p.estado=true");
		sql.setParameter("idProyecto", idProyecto);
		if (sql.getResultList().size() > 0)
			obj = (ProyectoLicenciaCoa) sql.getSingleResult();

		return obj;

	}

	public String buscarPago(String codigoPago, String tramite) {
		String obj= "i";
		String sql="select * from dblink('"+dblinkSuiaVerde+"',"
			+ "'select h.project_id  "
			+ "from online_payment.online_payments_historical h "
			+ "where h.tramit_number=''"+codigoPago+"'' "
			+ "and project_id = ''"+tramite+"''"
			+ "order by 1 desc limit 1')"
			+ "as (project_id text)";
	Query queryPago = crudServiceBean.getEntityManager().createNativeQuery(sql);
		if (queryPago.getResultList().size() > 0)
		 obj = (String) queryPago.getSingleResult();

		return obj;

	}
	
	public ProyectoLicenciaCoa buscarProyectoPorIdWithOutFilters(Integer idProyecto) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idProyecto", idProyecto);
		try {
			ProyectoLicenciaCoa proyecto = (ProyectoLicenciaCoa) crudServiceBean.findByNamedQuerySingleResultWithOutFilters(
					ProyectoLicenciaCoa.GET_PROYECTO_SIN_FILTRO, params);
			return proyecto;
		} catch (NoResultException ex) {

		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<SustanciaQuimicaPeligrosa> listaSustanciasQuimicas() {
		List<SustanciaQuimicaPeligrosa> lista = new ArrayList<SustanciaQuimicaPeligrosa>();
		Query sql = crudServiceBean.getEntityManager().createQuery(
				//"Select s From SustanciaQuimicaPeligrosa s where s.sustanciaQuimicaPeligrosa.id in(3953,3952,3951,5925,5188) and s.estado=true order by s.descripcion");
				"Select s From SustanciaQuimicaPeligrosa s where (s.sustanciaQuimicaPeligrosa.id in(3953,3952,3951,5925,5188) or s.id in (2106, 1389, 4081, 3240, 1484, 2061, 3443, 454, 5246, 819, 3477, 1391)) and s.estado=true order by s.descripcion");
		if (sql.getResultList().size() > 0)
			lista = sql.getResultList();

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<ProyectoCustom> listarLicenciasRcoaEnTramite(Area area) {

		String queryString = "select p.id, p.codigoUnicoAmbiental, p.nombreProyecto, p.fechaGeneracionCua, s.catalogoCIUU.tipoSector.nombre "
				+ " from ProyectoLicenciaCoa p, ProyectoLicenciaCuaCiuu s "
				+ " where p.estado=true and s.proyectoLicenciaCoa=p and s.primario=true and s.estado=true "
				+ " and p.proyectoFechaFinalizado = null and p.categorizacion in (3, 4) "
				+ " and p.estadoRegistro = true "
				+ " and (p.areaResponsable.id =:idArea or (p.areaResponsable.area != null and p.areaResponsable.area.id = :idArea)) "
				+ " and (p.tieneViabilidadFavorable = null or p.tieneViabilidadFavorable = true) "
				+ " and (p.estadoActualizacionCertInterseccion = null or p.estadoActualizacionCertInterseccion != 2) "
				+ " order by p.codigoUnicoAmbiental";
		Query query = crudServiceBean.getEntityManager().createQuery(queryString);
		query.setParameter("idArea", area.getId());
		List<ProyectoCustom> proyectosNoFinalizados = new ArrayList<ProyectoCustom>();
		if (query.getResultList().size() > 0) {
			List<Object[]> lista = query.getResultList();
			for (Object[] proyectos : lista) {
				ProyectoCustom proyecto = new ProyectoCustom((Integer) proyectos[0], (String) proyectos[1],
						(String) proyectos[2], (Date) proyectos[3], (String) proyectos[4], "", "", "SN", "SN");
				proyectosNoFinalizados.add(proyecto);
			}
		}
		return proyectosNoFinalizados;
	}

	@SuppressWarnings("unchecked")
	public List<ProyectoCustom> listarLicenciasRcoaEnTramite(List<AreaUsuario> areasUsuario) {
		String areas = "";

		for (AreaUsuario au : areasUsuario) {
			areas += au.getArea().getId() + ",";
		}

		areas = areas.substring(0, areas.length() - 1);

		String queryString = "select p.id, p.codigoUnicoAmbiental, p.nombreProyecto, p.fechaGeneracionCua, s.catalogoCIUU.tipoSector.nombre "
				+ " from ProyectoLicenciaCoa p, ProyectoLicenciaCuaCiuu s "
				+ " where p.estado=true and s.proyectoLicenciaCoa=p and s.primario=true and s.estado=true "
				+ " and p.proyectoFechaFinalizado = null and p.categorizacion in (3, 4) "
				+ " and p.estadoRegistro = true " + " and (p.areaResponsable.id in (" + areas
				+ ") or (p.areaResponsable.area != null and p.areaResponsable.area.id in (" + areas + "))) "
				+ " and (p.tieneViabilidadFavorable = null or p.tieneViabilidadFavorable = true) "
				+ " and (p.estadoActualizacionCertInterseccion = null or p.estadoActualizacionCertInterseccion != 2) "
				+ " order by p.codigoUnicoAmbiental";
		Query query = crudServiceBean.getEntityManager().createQuery(queryString);

		List<ProyectoCustom> proyectosNoFinalizados = new ArrayList<ProyectoCustom>();
		if (query.getResultList().size() > 0) {
			List<Object[]> lista = query.getResultList();
			for (Object[] proyectos : lista) {
				ProyectoCustom proyecto = new ProyectoCustom((Integer) proyectos[0], (String) proyectos[1],
						(String) proyectos[2], (Date) proyectos[3], (String) proyectos[4], "", "", "SN", "SN");
				proyectosNoFinalizados.add(proyecto);
			}
		}
		return proyectosNoFinalizados;
	}

	@SuppressWarnings("unchecked")
	public List<ProyectoCustom> listarLicenciasPendientesActualizacion(String usuario) {

		String queryString = "select p.id, p.codigoUnicoAmbiental, p.nombreProyecto, p.fechaGeneracionCua, s.catalogoCIUU.tipoSector.nombre "
				+ " from ProyectoLicenciaCoa p, ProyectoLicenciaCuaCiuu s "
				+ " where p.estado=true and s.proyectoLicenciaCoa=p and s.primario=true and s.estado=true "
				+ " and p.categorizacion in (3, 4) " + " and p.usuarioCreacion = :usuario "
				+ " and p.estadoActualizacionCertInterseccion = 2 " + " order by p.codigoUnicoAmbiental";
		Query query = crudServiceBean.getEntityManager().createQuery(queryString);
		query.setParameter("usuario", usuario);
		List<ProyectoCustom> proyectosNoFinalizados = new ArrayList<ProyectoCustom>();
		if (query.getResultList().size() > 0) {
			List<Object[]> lista = query.getResultList();
			for (Object[] proyectos : lista) {
				ProyectoCustom proyecto = new ProyectoCustom((Integer) proyectos[0], (String) proyectos[1],
						(String) proyectos[2], (Date) proyectos[3], (String) proyectos[4], "", "", "SN", "SN");
				proyectosNoFinalizados.add(proyecto);
			}
		}
		return proyectosNoFinalizados;
	}

	@SuppressWarnings("unchecked")
	public List<ProyectoCustom> listarProyectosPorAsignarArea() {

		String queryString = "select p.id, p.codigoUnicoAmbiental, p.nombreProyecto, p.fechaGeneracionCua, s.catalogoCIUU.tipoSector.nombre "
				+ " from ProyectoLicenciaCoa p, ProyectoLicenciaCuaCiuu s "
				+ " where s.proyectoLicenciaCoa=p and s.primario=true and s.estado=true " + " and p.estado=true "
				+ " and p.areaResponsable.areaAbbreviation = 'ND' " + " order by p.codigoUnicoAmbiental";
		Query query = crudServiceBean.getEntityManager().createQuery(queryString);
		List<ProyectoCustom> proyectosNoFinalizados = new ArrayList<ProyectoCustom>();
		if (query.getResultList().size() > 0) {
			List<Object[]> lista = query.getResultList();
			for (Object[] proyectos : lista) {
				ProyectoCustom proyecto = new ProyectoCustom((Integer) proyectos[0], (String) proyectos[1],
						(String) proyectos[2], (Date) proyectos[3], (String) proyectos[4], "", "", "SN", "SN");
				proyectosNoFinalizados.add(proyecto);
			}
		}
		return proyectosNoFinalizados;
	}

	
	@SuppressWarnings("unchecked")
	public String AutoridadAmbientalProyecto(ProyectoLicenciaCoa proyecto) {


		String queryString = "select cont_value from areas a " + 
				"inner join areas_users au on a.area_id = au.area_id " + 
				"inner join users u on u.user_id = au.user_id " + 
				"inner join suia_iii.roles_users ru on ru.user_id = au.user_id " + 
				"inner join contacts c on c.pers_id  = u.peop_id " + 
				"WHERE a.area_id = :area_id and role_id in (1056,8,1) and c.cont_status is true " + 
				"and c.cofo_id = 5 and ru.rous_status = true and au.arus_status = true";

		try {
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(queryString);
			query.setParameter("area_id", proyecto.getAreaResponsable().getId());
			List<String> result = query.getResultList();
			
			if (result.isEmpty()) {
				query.setParameter("area_id", proyecto.getAreaResponsable().getArea().getId());
				List<String> result_2 = query.getResultList();
				if (result_2 != null && !result_2.isEmpty()) {

					return result_2.get(0).toString();
				}
			}
			
			if (result != null && !result.isEmpty()) {

				return result.get(0).toString();
			}
			
			
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex);
		}
		return null;

	}
	
	@SuppressWarnings("unchecked")
	public List<String> flujoProyecto(String codigoUnicoAmbiental) {

		String sql2="select * from dblink('"+dblinkBpmsSuiaiii+"', ' select v.processid from public.variableinstancelog v where v.value =''"+codigoUnicoAmbiental+"''') as (process_id varchar)";

		try {
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql2);
			List<String> result = query.getResultList();

			if (result != null && !result.isEmpty()) {

				return result;
			}
	
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex);
		}
		return null;

	}
	public SustanciaQuimicaPeligrosa buscaSustanciasQuimicas(String cadena) {
		SustanciaQuimicaPeligrosa sustancia = new SustanciaQuimicaPeligrosa();
		try {
			String sql = "Select s From SustanciaQuimicaPeligrosa s where lower(s.descripcion)=lower('"+cadena+"') and s.estado=true ";
			Query query = crudServiceBean.getEntityManager().createQuery(sql);
						  //crudServiceBean.getEntityManager().createQuery(queryString)
			if (query.getResultList().size() > 0)
						sustancia = (SustanciaQuimicaPeligrosa) query.getResultList().get(0);	
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex);
		}
		return sustancia;
	}	
	
	@SuppressWarnings("unchecked")
	public void modificarTareaBpm(String tramite, String motivo){
		try {
			/**
			 * Busco el trámite verificando que sea un proceso de licencia ambiental en el anterior flujo de licencia
			 */
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select p.processinstanceid, v.value, p.delete_reason "
					+ "from processinstancelog p inner join variableinstancelog v on p.processinstanceid = v.processinstanceid "
					+ "where p.processid = ''SUIA.LicenciaAmbiental'' and v.variableid = ''tramite'' "
					+ "and v.value = ''" + tramite + "''"					
					+ " ') "
					+ "as (processinstanceid varchar, tramite varchar, motivoP varchar) "
					+ "order by 1 desc";			
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1], (String) row[2] });					
				}
			}
			
			/**
			 * Si se encuentra el proyecto se busca las tareas asociadas al proyecto que fueron deshabilitadas 
			 */
			for (String[] variable : listaCodigosProyectos) { 
				
				 String sqlTask="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.processinstanceid, t.status "
							+ "from task t  "
							+ "where t.processinstanceid =" + Integer.valueOf(variable[0])
							+ " and t.previousstatus = 0 ') "
							+ "as (id varchar, processinstanceid varchar, estado varchar) "
							+ "order by 1 desc";
										
					Query queryTask = crudServiceBean.getEntityManager().createNativeQuery(sqlTask);
					List<Object>  resultTaskList = new ArrayList<Object>();
					resultTaskList=queryTask.getResultList();
					List<String[]>listaCodigosTareas= new ArrayList<String[]>();		
					if (resultList.size() > 0) {
						for (Object a : resultTaskList) {
							Object[] row = (Object[]) a;
							listaCodigosTareas.add(new String[] { (String) row[0],(String) row[1], (String) row[2] });					
						}
					}
					
					for (String[] tarea : listaCodigosTareas) {
						/**
						 * Si tiene una tarea en estado Exited se debe activar de nuevo
						 */
						
						String pattern = "yyyy-MM-dd HH:mm";
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
						String date = simpleDateFormat.format(new Date());
						
						if(tarea[2].equals("Exited")){
							
							 String sqlTarea = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
				                		+ "'UPDATE task "
				                		+ "set status = ''Reserved'' "
				                		+ " where id = " + Integer.valueOf(tarea[0])
				                		+ "')" ;
				                		
							 Query queryTarea = crudServiceBean.getEntityManager().createNativeQuery(sqlTarea);	
							 queryTarea.getResultList();
							 
						}
						
						/**
						 * se actualiza el bamtamsummary y el processinstance id
						 */
						
						if(tarea[2].equals("Exited") || tarea[2].equals("Reserved")){
							
							String motivoString = "";
							if(variable[2] != null){
								motivoString = variable[2].toString() + " - " + motivo;
							}else{
								motivoString = motivo;
							}
							
							 String sqlTarea = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
				                		+ "'UPDATE bamtasksummary "
				                		+ "set status = ''Reserved'', "
				                		+ "delete_reason = ''"+ motivoString + "'', "
				                		+ "deactivation_date = ''" + date + "'' "
				                		+ " where taskid = " + Integer.valueOf(tarea[0])
				                		+ "')" ;
				                		
				             Query queryTarea = crudServiceBean.getEntityManager().createNativeQuery(sqlTarea);	
				             queryTarea.getResultList();
				             
						}
						String motivoString = "";
						if(variable[2] != null){
							motivoString = variable[2].toString() + " - " + motivo;
						}else{
							motivoString = motivo;
						}
						
						
						 String sqlTarea = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
			                		+ "'UPDATE processinstancelog "
			                		+ "set status = 1, "
			                		+ "delete_reason = ''"+ motivoString + "'', "
			                		+ "date_delete_reason = ''" + date + "'' "
			                		+ " where id = " + Integer.valueOf(variable[0])
			                		+ "')" ;
			                		
			             Query queryTarea = crudServiceBean.getEntityManager().createNativeQuery(sqlTarea);
			             queryTarea.getResultList();			             
						
					}
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;			
		}
	}
	
	@SuppressWarnings("unchecked")
	public void modificarTareaBpmArchivo(String tramite, String motivo){
		try {
			/**
			 * Busco el trámite verificando que sea un proceso de licencia ambiental en el anterior flujo de licencia
			 */
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select p.processinstanceid, v.value, p.delete_reason "
					+ "from processinstancelog p inner join variableinstancelog v on p.processinstanceid = v.processinstanceid "
					+ "where p.processid = ''SUIA.LicenciaAmbiental'' and v.variableid = ''tramite'' "
					+ "and v.value = ''" + tramite + "''"					
					+ " ') "
					+ "as (processinstanceid varchar, tramite varchar, motivoP varchar) "
					+ "order by 1 desc";			
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1], (String) row[2] });					
				}
			}
			
			/**
			 * Si se encuentra el proyecto se busca las tareas asociadas al proyecto que fueron deshabilitadas 
			 */
			for (String[] variable : listaCodigosProyectos) { 
				
				 String sqlTask="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.processinstanceid, t.status "
							+ "from task t  "
							+ "where t.processinstanceid =" + Integer.valueOf(variable[0])
							+ " and t.previousstatus = 0 ') "
							+ "as (id varchar, processinstanceid varchar, estado varchar) "
							+ "order by 1 desc";
										
					Query queryTask = crudServiceBean.getEntityManager().createNativeQuery(sqlTask);
					List<Object>  resultTaskList = new ArrayList<Object>();
					resultTaskList=queryTask.getResultList();
					List<String[]>listaCodigosTareas= new ArrayList<String[]>();		
					if (resultList.size() > 0) {
						for (Object a : resultTaskList) {
							Object[] row = (Object[]) a;
							listaCodigosTareas.add(new String[] { (String) row[0],(String) row[1], (String) row[2] });					
						}
					}
					
					for (String[] tarea : listaCodigosTareas) {
						/**
						 * Si tiene una tarea en estado Reserved se debe activar de nuevo
						 */
						
						String pattern = "yyyy-MM-dd HH:mm";
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
						String date = simpleDateFormat.format(new Date());
						
						if(tarea[2].equals("Reserved")){
							
							 String sqlTarea = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
				                		+ "'UPDATE task "
				                		+ "set status = ''Exited'' "
				                		+ " where id = " + Integer.valueOf(tarea[0])
				                		+ "')" ;
				                		
							 Query queryTarea = crudServiceBean.getEntityManager().createNativeQuery(sqlTarea);	
							 queryTarea.getResultList();
							 
						}
						
						/**
						 * se actualiza el bamtamsummary y el processinstance id
						 */
						
						if(tarea[2].equals("Exited") || tarea[2].equals("Reserved")){
							
							String motivoString = "";
							if(variable[2] != null){
								motivoString = variable[2].toString() + " - " + motivo;
							}else{
								motivoString = motivo;
							}
							
							 String sqlTarea = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
				                		+ "'UPDATE bamtasksummary "
				                		+ "set status = ''Exited'', "
				                		+ "delete_reason = ''"+ motivoString + "'', "
				                		+ "deactivation_date = ''" + date + "'' "
				                		+ " where taskid = " + Integer.valueOf(tarea[0])
				                		+ "')" ;
				                		
				             Query queryTarea = crudServiceBean.getEntityManager().createNativeQuery(sqlTarea);	
				             queryTarea.getResultList();
				             
						}
						String motivoString = "";
						if(variable[2] != null){
							motivoString = variable[2].toString() + " - " + motivo;
						}else{
							motivoString = motivo;
						}
						
						
						 String sqlTarea = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
			                		+ "'UPDATE processinstancelog "
			                		+ "set status = 1, "
			                		+ "delete_reason = ''"+ motivoString + "'', "
			                		+ "date_delete_reason = ''" + date + "'' "
			                		+ " where id = " + Integer.valueOf(variable[0])
			                		+ "')" ;
			                		
			             Query queryTarea = crudServiceBean.getEntityManager().createNativeQuery(sqlTarea);
			             queryTarea.getResultList();			             
						
					}
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;			
		}
	}
	
	@SuppressWarnings("unchecked")
	public String modificarTareaBpmRcoa(String tramite, String motivo){
		try {
			
//			boolean existenTareas = false;
			List<String> listaExisteTareas = new ArrayList<>();
			/**
			 * obtengo todos los procesos relacionados al código de trámite
			 */
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select p.processinstanceid, v.value, p.delete_reason, p.processid "
					+ "from processinstancelog p inner join variableinstancelog v on p.processinstanceid = v.processinstanceid "
					+ "where v.variableid = ''tramite'' "
					+ "and v.value = ''" + tramite + "''"					
					+ " ') "
					+ "as (processinstanceid varchar, tramite varchar, motivo varchar, processid varchar) "
					+ "order by 1 desc";			
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1], (String) row[2], (String) row[3] });					
				}
			}
			/**
			 * Si se encuentra el proceso se buscan las tareas de los procesos 
			 */
			boolean cambioPreliminar = false;			
			String pattern = "yyyy-MM-dd HH:mm";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			
			for (String[] variable : listaCodigosProyectos) {               
                
                String sqlTask="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.processinstanceid, t.status "
						+ "from task t  "
						+ "where t.processinstanceid =" + Integer.valueOf(variable[0])
						+ " and t.previousstatus = 0 ') "
						+ "as (id varchar, processinstanceid varchar, estado varchar) "
						+ "order by 1 desc";
				
				
				Query queryTask = crudServiceBean.getEntityManager().createNativeQuery(sqlTask);
				List<Object>  resultTaskList = new ArrayList<Object>();
				resultTaskList=queryTask.getResultList();
				List<String[]>listaCodigosTareas= new ArrayList<String[]>();		
				if (resultList.size() > 0) {
					for (Object a : resultTaskList) {
						Object[] row = (Object[]) a;
						listaCodigosTareas.add(new String[] { (String) row[0],(String) row[1], (String) row[2] });					
					}
				}
				
				/**
				 * si se encuentran las tareas se las actualiza para que sean visibles para el usuario
				 */
				if(listaCodigosTareas != null && !listaCodigosTareas.isEmpty()){
					for (String[] tarea : listaCodigosTareas) {
						/**
						 * Si tiene una tarea en estado Exited se debe activar de nuevo
						 */									
						boolean tareaActualizada = false;
						if(tarea[2].equals("Exited")){
							
							 String sqlTarea = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
				                		+ "'UPDATE task "
				                		+ "set status = ''Reserved'' "
				                		+ " where id = " + Integer.valueOf(tarea[0])
				                		+ "')" ;
				                		
							 Query queryTarea = crudServiceBean.getEntityManager().createNativeQuery(sqlTarea);	
							 queryTarea.getResultList();
							 tareaActualizada = true;
							 listaExisteTareas.add("1");
						}
						
						/**
						 * se actualiza el bamtamsummary y el processinstancelog y se ingresa fecha y motivo de desactivacion-eliminación
						 */
						
						if(tarea[2].equals("Exited") || tarea[2].equals("Reserved")){
							String motivoS = "";
							if(variable[2] != null){
								motivoS = variable[2].toString() + " -Activacion-" + motivo;
							}else{
								motivoS = "Activacion-" + motivo;
							}
							
							
							 String sqlTarea = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
				                		+ "'UPDATE bamtasksummary "
				                		+ "set status = ''Reserved'', "
				                		+ "delete_reason = ''"+ motivoS + "'', "
				                		+ "deactivation_date = ''" + date + "'' "
				                		+ " where taskid = " + Integer.valueOf(tarea[0])
				                		+ "')" ;
				                		
				             Query queryTarea = crudServiceBean.getEntityManager().createNativeQuery(sqlTarea);	
				             queryTarea.getResultList();
				             listaExisteTareas.add("1");
						}
						
						String motivoS = "";
						if(variable[2] != null){
							motivoS = variable[2].toString() + " -Activacion-" + motivo;
						}else{
							motivoS = "Activacion-" + motivo;
						}
						
						/**
						 * Solo si se actualizó una tarea se activa el proceso
						 */
						if(tareaActualizada){
							 String sqlTarea = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
				                		+ "'UPDATE processinstancelog "
				                		+ "set status = 1, "
				                		+ "delete_reason = ''"+ motivoS + "'', "
				                		+ "date_delete_reason = ''" + date + "'' "
				                		+ " where id = " + Integer.valueOf(variable[0])
				                		+ "')" ;
				                		
				             Query queryTarea = crudServiceBean.getEntityManager().createNativeQuery(sqlTarea);
				             queryTarea.getResultList();
				             
				             if(variable[3].toString().equals("rcoa.RegistroPreliminar")){
				            	 cambioPreliminar = true;
				             }
						}					
					}					
				}				
			}	
			
			if(listaExisteTareas.isEmpty()){
				return "El trámite no tiene tareas en bpm";
			}else{
				/**
				 * Como los procesos son asociado el registro preliminar solo si no se actualizo antes se actualiza el preliminar al final
				 * para que sea un proceso activo. 
				 */
				if(!cambioPreliminar){
					
					for (String[] variable : listaCodigosProyectos) {       
						if(variable[3].toString().equals("rcoa.RegistroPreliminar")){
							String motivoP = "";
							
							if(variable[2] != null){
								motivoP = variable[2].toString() + " -Activacion-" + motivo;
							}else{
								motivoP = "Activacion-" + motivo;
							}
							
							String sqlTareaP = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
			                		+ "'UPDATE processinstancelog "
			                		+ "set status = 1, "
			                		+ "delete_reason = ''"+ motivoP + "'', "
			                		+ "date_delete_reason = ''" + date + "'' "
			                		+ " where id = " + Integer.valueOf(variable[0])
			                		+ "')" ;
			                		
							Query queryTareaP = crudServiceBean.getEntityManager().createNativeQuery(sqlTareaP);
							queryTareaP.getResultList();							 
						}						
					}					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return "";
	}
	
@SuppressWarnings("unchecked")
public String archivarTramiteRcoa(String tramite, String motivo){
		
		String resp ="0";
		String sqlrcoa_pry = " select prco_id from  coa_mae.project_licencing_coa  where prco_cua in ( '"+tramite+"') and prco_status =true";
		
		Query queryproyecto_consutla = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_pry);			
		if(queryproyecto_consutla.getResultList().size()>0)
		{		
		
		String sqlrcoa="update coa_mae.project_licencing_coa set prco_delete_reason = '" + motivo + "', prco_deactivation_date= now(), prco_status = false " 
				+" where prco_cua in ( select prco_cua from coa_mae.project_licencing_coa  where prco_cua in ( '"+tramite+"')	)";
		Query queryproyectorcoa = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa);
		queryproyectorcoa.executeUpdate();

		
		String sqlrcoa_bpmconsulta = " select * from dblink('"+dblinkBpmsSuiaiii+"',"
				+ " ' select id from processinstancelog  where id in ("
				+ " select id from processinstancelog  where processinstanceid  in ( select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''))"
			    + " ) and status not in (3,4)"
			    + "') as (id integer) " ;
		Query queryproyectorcoabpm_consutla = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpmconsulta);		
		if(queryproyectorcoabpm_consutla.getResultList().size()>0)
		{
			String sqlrcoa_bpm = " select dblink_exec('"+dblinkBpmsSuiaiii+"',"
					+ " ' update processinstancelog  set status = 4, delete_reason = ''"+motivo+"'', date_delete_reason=  now() where id in ("
					+ " select id from processinstancelog  where processinstanceid  in ( select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''))"
				    + " ) and status not in (3,4)"
				    + "') as result " ;
			Query queryproyectorcoa_ = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpm);
			queryproyectorcoa_.getResultList();
			
			String sqlrcoa_bpm2_Consutla =" select * from dblink('"+dblinkBpmsSuiaiii+"', "  
					+ " 'select pk from bamtasksummary  where pk in ("
					+ " select pk from bamtasksummary    where processinstanceid  in ( "
					+ " select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''	) ) and status not in (''Completed''))"
					 + "') as (pk integer)  " ;
			Query queryproyectorcoabpm2_consulta = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpm2_Consutla);		
			if(queryproyectorcoabpm2_consulta.getResultList().size()>0) {
				String sqlrcoa_bpm2_actualizar =" select from dblink_exec('"+dblinkBpmsSuiaiii+"', "  
						+ " 'update bamtasksummary  set status = ''Exited'',  delete_reason = ''"+motivo+"'' , deactivation_date=  now() where pk in ("
						+ " select pk from bamtasksummary    where processinstanceid  in ( "
						+ " select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''	) ) and status not in (''Completed''))"
						 + "')" ;
				Query queryproyectorcoabpm2_actualizar = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpm2_actualizar);
				queryproyectorcoabpm2_actualizar.getResultList();
			}
			
			String sqlrcoa_bpm3_cosulta =" select * from  dblink('"+dblinkBpmsSuiaiii+"', "  
					+ "' select  id from task  where id in ( select id from task    where processinstanceid  in ("
					+" select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''	) )  and status not in (''Completed''))"
					 + "') as (id integer) " ;
			Query queryproyectorcoabpm3_consutla = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpm3_cosulta);		
			if(queryproyectorcoabpm3_consutla.getResultList().size()>0) {
				String sqlrcoa_bpm3 =" select  from dblink_exec('"+dblinkBpmsSuiaiii+"', "  
						+ "' update task set status = ''Exited'' where id in ( select id from task    where processinstanceid  in ("
						+" select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''	) )  and status not in (''Completed''))"
						 + "')" ;
				Query queryproyectorcoabpm3 = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpm3);
				queryproyectorcoabpm3.getResultList();

			}
			
			// liberar rgd 
			
			String sqlrcoa_rgd_consulta = " select  wapr_id from coa_waste_generator_record.waste_generator_record_coa a "
					+ " inner join coa_waste_generator_record.waste_generator_record_project_licencing_coa  b on a.ware_id = b.ware_id "
					+ " inner join coa_mae.project_licencing_coa c on c.prco_id = b.prco_id "
					+ " where prco_cua in ('" + tramite +"')and prco_categorizacion = 2";
			
			Query queryproyecto_rgd_consutla = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_rgd_consulta);
			List<Object>  resultListrcoa_rgd = new ArrayList<Object>();
			resultListrcoa_rgd=queryproyecto_rgd_consutla.getResultList();
		
			for (Object object : resultListrcoa_rgd) {
				String sql_rgd_id =" select prco_id from coa_waste_generator_record.waste_generator_record_project_licencing_coa "
						+ " where wapr_id in ("+object+")";
				Query queryproyecto_rgd_ = crudServiceBean.getEntityManager().createNativeQuery(sql_rgd_id);
				List<Object>  resultListrcoa_rgd_ = new ArrayList<Object>();
				resultListrcoa_rgd_=queryproyecto_rgd_.getResultList();
				
				String sql_rgd_id_libre =" update coa_waste_generator_record.waste_generator_record_project_licencing_coa set prco_id =null, wapr_status = false,"
						+ " wapr_id_history = "+resultListrcoa_rgd_.get(0)+", wapr_observation_bd ='"+motivo+" - Desactivación del proyecto"+tramite+"',"
								+ " wapr_date_update = now() where wapr_id in ("+object+")";
				
				Query queryproyectorgd_libre = crudServiceBean.getEntityManager().createNativeQuery(sql_rgd_id_libre);
				queryproyectorgd_libre.executeUpdate();
				
			}
			
		}
		resp="1";
	}
		return resp;
		
		
				
	}
@SuppressWarnings("unchecked")
public String archivarTramiteCoa(String tramite, String motivo){
	

//	
			
	String resp ="0";
	String sqlrcoa_pry = " select pren_id from  suia_iii.projects_environmental_licensing where pren_code in ( '"+tramite+"') and pren_status =true";
	
	Query queryproyecto_consutla = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_pry);
//			
	if(queryproyecto_consutla.getResultList().size()>0)
	{		
	
	String sqlrcoa="update suia_iii.projects_environmental_licensing set pren_delete_reason = '" + motivo + "', pren_deactivation_date= now(), pren_status = false " 
			+" where pren_code in ( select pren_code from suia_iii.projects_environmental_licensing  where pren_code in ( '"+tramite+"')	)";
	Query queryproyectorcoa = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa);
	queryproyectorcoa.executeUpdate();

	
	String sqlrcoa_bpmconsulta = " select * from dblink('"+dblinkBpmsSuiaiii+"',"
			+ " ' select id from processinstancelog  where id in ("
			+ " select id from processinstancelog  where processinstanceid  in ( select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''))"
		    + " ) and status not in (3,4)"
		    + "') as (id integer) " ;
	Query queryproyectorcoabpm_consutla = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpmconsulta);		
	if(queryproyectorcoabpm_consutla.getResultList().size()>0)
	{
		String sqlrcoa_bpm = " select dblink_exec('"+dblinkBpmsSuiaiii+"',"
				+ " ' update processinstancelog  set status = 4, delete_reason = ''"+motivo+"'', date_delete_reason=  now() where id in ("
				+ " select id from processinstancelog  where processinstanceid  in ( select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''))"
			    + " ) and status not in (3,4)"
			    + "') as result " ;
		Query queryproyectorcoa_ = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpm);
		queryproyectorcoa_.getResultList();
		
		String sqlrcoa_bpm2_Consutla =" select * from dblink('"+dblinkBpmsSuiaiii+"', "  
				+ " 'select pk from bamtasksummary  where pk in ("
				+ " select pk from bamtasksummary    where processinstanceid  in ( "
				+ " select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''	) ) and status not in (''Completed''))"
				 + "') as (pk integer)  " ;
		Query queryproyectorcoabpm2_consulta = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpm2_Consutla);		
		if(queryproyectorcoabpm2_consulta.getResultList().size()>0) {
			String sqlrcoa_bpm2_actualizar =" select from dblink_exec('"+dblinkBpmsSuiaiii+"', "  
					+ " 'update bamtasksummary  set status = ''Exited'',  delete_reason = ''"+motivo+"'' , deactivation_date=  now() where pk in ("
					+ " select pk from bamtasksummary    where processinstanceid  in ( "
					+ " select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''	) ) and status not in (''Completed''))"
					 + "')" ;
			Query queryproyectorcoabpm2_actualizar = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpm2_actualizar);
			queryproyectorcoabpm2_actualizar.getResultList();
		}
		
		String sqlrcoa_bpm3_cosulta =" select * from  dblink('"+dblinkBpmsSuiaiii+"', "  
				+ "' select  id from task  where id in ( select id from task    where processinstanceid  in ("
				+" select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''	) )  and status not in (''Completed''))"
				 + "') as (id integer) " ;
		Query queryproyectorcoabpm3_consutla = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpm3_cosulta);		
		if(queryproyectorcoabpm3_consutla.getResultList().size()>0) {
			String sqlrcoa_bpm3 =" select from dblink_exec('"+dblinkBpmsSuiaiii+"', "  
					+ "' update task set status = ''Exited'' where id in ( select id from task    where processinstanceid  in ("
					+" select processinstanceid from variableinstancelog  where value  in (''"+tramite+"''	) )  and status not in (''Completed''))"
					 + "')" ;
			Query queryproyectorcoabpm3 = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_bpm3);
			queryproyectorcoabpm3.getResultList();

		}
		
		// liberar rgd 
		
		
		String sqlrcoa_rgd_consulta = " select pren_id from suia_iii.projects_environmental_licensing "
				+ " where pren_code in ('" + tramite +"') and pren_project_finished = true" ;
		
		Query queryproyecto_rgd_consutla = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_rgd_consulta);
		List<Object>  resultListrcoa_rgd = new ArrayList<Object>();
		resultListrcoa_rgd=queryproyecto_rgd_consutla.getResultList();
	
		for (Object object : resultListrcoa_rgd) {
			String sql_rgd_id =" select pren_id from suia_iii.projects_environmental_licensing "
					+ " where pren_code in ("+object+")";
			Query queryproyecto_rgd_ = crudServiceBean.getEntityManager().createNativeQuery(sql_rgd_id);
			List<Object>  resultListrcoa_rgd_ = new ArrayList<Object>();
			resultListrcoa_rgd_=queryproyecto_rgd_.getResultList();
			
			String sql_rgd_id_libre =" update suia_iii.hazardous_wastes_generators set pren_id =null, hwge_status = false,"
					+ " hwge_observations ='"+motivo+" - Desactivación del proyecto"+tramite+"',"
							+ " hwge_date_desactivation  = now() where pren_id in ("+resultListrcoa_rgd_.get(0)+")";
			
			Query queryproyectorgd_libre = crudServiceBean.getEntityManager().createNativeQuery(sql_rgd_id_libre);
			queryproyectorgd_libre.executeUpdate();
			
		}
			
		
	}
	resp="1";
}
	return resp;
				
				
	}


@SuppressWarnings({ "unchecked", "unused" })
public String liberarpago (String numeroTramitePago, String proyecto, String ip, String motivo) {
	String retorno="0";
	String sql="select * from dblink('"+dblinkSuiaVerde+"',"
			+ "'select h.id_online_payment_historical,h.online_payment_id,h.retired_value,h.tramit_number,p.used_value,h.project_id "
			+ "from online_payment.online_payments_historical h "
			+ "inner join online_payment.online_payments p on (p.id_online_payment=h.online_payment_id)  "
			+ "where h.project_id=''"+proyecto+"'' "
			+ "and h.tramit_number=''"+numeroTramitePago+"'' "
			+ "order by 1 desc limit 1')"
			+ "as (id_online_payment_historical integer,online_payment_id integer,retired_value text,tramit_number text,used_value text,project_id text)";
	Query queryPago = crudServiceBean.getEntityManager().createNativeQuery(sql);
	List<Object[]> result = (List<Object[]>) queryPago.getResultList();
	if (result.size() > 0) {
		for (int i = 0; i < result.size(); i++) {
			Object[] pagoHistorico = (Object[]) result.get(i);
			Integer idPagoHistorico=(Integer)pagoHistorico[0];
			Integer idPago=(Integer)pagoHistorico[1];
			String valorS=(String)pagoHistorico[2];
			String tramite=(String)pagoHistorico[3];
			String valorUsadoS=(String)pagoHistorico[4];
			String proyecto1=(String)pagoHistorico[5];
			
			Double valor=Double.valueOf(valorS);	    		

			
			
			String sqlrcoa_pry = "select pren_id from  suia_iii.projects_environmental_licensing where pren_code in ( '"+proyecto1+"') and pren_status =true";
			
			Query queryproyecto_consutla = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_pry);
			String sqlrcoa_pryr = " select prco_id from  coa_mae.project_licencing_coa where prco_cua in ( '"+proyecto1+"') and prco_status =true";
			
			Query queryproyecto_consutlar = crudServiceBean.getEntityManager().createNativeQuery(sqlrcoa_pryr);
					
								
			if(queryproyecto_consutla.getResultList().size()>0 || queryproyecto_consutlar.getResultList().size()>0)
			{		
				 retorno=tramite;
				
			}else {	
			if(valor>0)
			{
				String sqlInsert="select dblink_exec('"+dblinkSuiaVerde+"',"
						+ "'insert into online_payment.online_payments_historical  "
						+ "(id_online_payment_historical, description, project_id, retired_value,sender_ip, tramit_number, update_date, value_updated, online_payment_id, observations) "
						+ "VALUES (nextval(''online_payment.seq_id_online_payments_historical''), ''Se reactiva la transacción por desactivación del proyecto'', "
						+ "''"+proyecto1+"'', ''0.00'',''"+ip+"'', ''"+tramite+"'', clock_timestamp(),  ''"+valorS+"'',"+idPago+", ''"+motivo+"'')') as result";
				crudServiceBean.getEntityManager().createNativeQuery(sqlInsert).getResultList();	    				
				
				Double valorUsado=Double.valueOf(valorUsadoS)-valor;
				valorUsado=valorUsado>=0.0?valorUsado:0.0;
				
				String sqlUpdate="select dblink_exec('"+dblinkSuiaVerde+"',"
						+ "'update online_payment.online_payments  "
						+ "SET is_used = false, used_value = ''"+valorUsado+"'' "
						+ "WHERE tramit_number = ''"+tramite+"''') as result ";	    						
				crudServiceBean.getEntityManager().createNativeQuery(sqlUpdate).getResultList();
			}	
			retorno= "1";
		}
		}
	}
	 return retorno;

}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciaCoa> buscarProyectoHistorialRGD(Integer idProyecto){ 
		try {
			List<ProyectoLicenciaCoa>  proy = new ArrayList<ProyectoLicenciaCoa>();
			StringBuilder query = new StringBuilder();
			query.append("select plc.* ");
			query.append("from  coa_mae.project_licencing_coa plc ");
			query.append("where plc.prco_id =" + idProyecto);		
			proy = (List<ProyectoLicenciaCoa>)crudServiceBean.findNativeQuery(query.toString(),ProyectoLicenciaCoa.class);
			return proy;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}