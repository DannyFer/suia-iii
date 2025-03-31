package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProcesosDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProyectoAsociadoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class AutorizacionesAdministrativasFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private ProcesosDigitalizacionFacade procesosDigitalizacionFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private ProyectoAsociadoDigitalizacionFacade proyectoAsociadoDigitalizacionFacade;
	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public String dblinkSuiaHidro=Constantes.getDblinkBpmsHyD();
	
	public String dblinkSectorSubsector=Constantes.getDblinkSectorSubsector();
	
	public String dblinkSuiaBpm=Constantes.getDblinkBpmsSuiaiii();
	
	
	public String getQuery4Categorias(String camposRetorno, String nombreProyecto, String nombreOperador, Boolean soloHidrocarburos){
		String condicionSql = "";

		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+=  " and p.nombre= ''%"+nombreProyecto+"%''";
		}
		if(nombreOperador!= null && !nombreOperador.isEmpty()){
			condicionSql+= " and (r.nombresapellidos like ''%"+nombreOperador+"%'' or o.nombre like ''%"+nombreOperador+"%'')";
		}
		if(soloHidrocarburos) {
			condicionSql+= " and t2.tareas is null and upper(cc.cata_sector) = upper(''Hidrocarburos'') ";
		}

		String sqlProyecto="select " +camposRetorno+ " from dblink('"+dblinkSuiaVerde+"',"
				+ "'select distinct null,  p.id, p.nombre, p.usuarioldap, r.nombresapellidos, o.nombre,  "
				+ "cc.ca_categoria, cc.estrategico, (select count(*) from jbpm4_task where execution_id_ like ''%'' || p.id || ''%'') as tareas, cc.cata_sector, "
				+ "t2.tareas as tareas_4cat, "
				+ "y2.end_date "
				+ "from proyectolicenciaambiental p  "
				+ "inner join catalogo_categoria cc on cc.id_catalogo = p.id_catalogo "
				+ "left join persona r on r.pin = p.usuarioldap "
				+ "left join organizacion o on o.pin = p.usuarioldap "
				+ "left join (select proyecto, count(*) as tareas "
				+ "from (select substring(execution_, \"position\"(execution_ , ''.'') + 1) AS proyecto, dbid_ from jbpm4_hist_task ) as t GROUP BY t.proyecto) as t2 on t2.proyecto = p.id "
				+ "left join (select codigo_proyecto, end_date from dblink ( "
				+ "''"+dblinkSuiaHidro+"'', "
				+ "''select distinct v.value, p.end_date from variableinstancelog as v "
				+ "inner join processinstancelog as p on v.processinstanceid = p.processinstanceid "
				+ "where p.processid in (''''Hydrocarbons.pagoLicencia'''',''''Hydrocarbons.pagoLicenciaEnte'''') "
				+ "and p.status = 2 and p.end_date is not null and v.variableid = ''''codigoProyecto'''' '') as t1 ("
				+ "codigo_proyecto character varying(255), end_date date)) y2 on y2.codigo_proyecto = p.id "
				+ "where estadoproyecto = true and cc.ca_categoria != ''I'' " + condicionSql + "') as t1 "
				+ "(id character varying(255), codigo character varying(255), nombre text, usuario character varying(255), "
				+ "nombre_persona character varying(255), organizacion character varying(255), categoria character varying(255), "
				+ "estrategico boolean, tareas integer, sector character varying(255), tareas_4cat integer, fecha_fin date)";
		
		return sqlProyecto;
	}
	
	public String getQueryJbpm(String procesos){
		String sqlProyecto="(select t1.codigoproyecto, t1.estadoproceso, t1.fechaInicio, t1.fechaFin  "
				+ "from dblink('"+dblinkSuiaBpm+"',"
				+ "'select distinct v.value codigoProyecto, "
				+ "CASE WHEN p.status = 1 THEN ''En trámite'' "
				+ "WHEN p.status = 2 THEN ''Completado'' "
				+ "WHEN p.status = 3 THEN ''Abortado'' "
				+ "ELSE ''En trámite'' END as estadoProceso, p.start_date, p.end_date  "
				+ "from variableinstancelog v "
				+ "inner join processinstancelog p on p.processinstanceid = v.processinstanceid "
				+ "where  "
				+ "p.processname in (" + procesos + ") "
				+ "and (v.variableid=''tramite'' ) "
				+ "and user_identity not like ''%msit%'' "
				+ "and user_identity not like ''%admin%'' "
				+ "and user_identity not like ''%diana.pabon%'' "
				+ "and user_identity not IN ( ''1714419148'', ''1712876364'', ''1717679441'', ''1720283686'') "
				+ "and p.status in ( 2 ) order by 1') as t1 "
				+ "(codigoproyecto text, estadoproceso text, fechaInicio timestamp, fechafin timestamp) "
				+ "GROUP BY t1.codigoproyecto, t1.estadoproceso, t1.fechaInicio, t1.fechaFin "
				+ "ORDER BY t1.codigoproyecto) y ";
		
		return sqlProyecto;
	}

	public String getQueryJbpm2018(String procesos){
		String sqlProyecto="(select t1.codigoproyecto, t1.estadoproceso, t1.fechaInicio, t1.fechaFin  "
				+ "from dblink('"+dblinkSuiaBpm+"',"
				+ "'select distinct v.value codigoProyecto, "
				+ "CASE WHEN p.status = 1 THEN ''En trámite'' "
				+ "WHEN p.status = 2 THEN ''Completado'' "
				+ "WHEN p.status = 3 THEN ''Abortado'' "
				+ "ELSE ''En trámite'' END as estadoProceso, p.start_date, p.end_date  "
				+ "from variableinstancelog v "
				+ "inner join processinstancelog p on p.processinstanceid = v.processinstanceid "
				+ "inner join task t on p.processinstanceid = t.processinstanceid "
				+ "where   t.formname not in (''CompletarTDRenelsistema'') "
				+ "and t.status in (''Reserved'', ''InProgress'') "
				+ "and p.processname in (" + procesos + ") "
				+ "and (v.variableid=''tramite'' ) "
				+ "and user_identity not like ''%msit%'' "
				+ "and user_identity not like ''%admin%'' "
				+ "and user_identity not like ''%diana.pabon%'' "
				+ "and user_identity not IN ( ''1714419148'', ''1712876364'', ''1717679441'', ''1720283686'') "
				+ "and p.status in ( 1 ) order by 1') as t1 "
				+ "(codigoproyecto text, estadoproceso text, fechaInicio timestamp, fechafin timestamp) "
				+ "GROUP BY t1.codigoproyecto, t1.estadoproceso, t1.fechaInicio, t1.fechaFin "
				+ "ORDER BY t1.codigoproyecto) y ";
		
		return sqlProyecto;
	}
	
	@SuppressWarnings("unchecked")
	public List<AutorizacionAdministrativa> getProyectos4Categorias(String nombreProyecto, String nombreProponente, Boolean soloHidrocarburos){
		String sqlPproyecto = getQuery4Categorias("*", nombreProyecto, nombreProponente, soloHidrocarburos);
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		List<Object> result = queryProyecto.getResultList();
		if(result.size()>0){
			List<AutorizacionAdministrativa> projects = new ArrayList<AutorizacionAdministrativa>();
			for (Object object : result) {
				projects.add(new AutorizacionAdministrativa((Object[]) object, "1"));
			}
			
			return projects;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<AutorizacionAdministrativa> getProyectosSectorSubsector(String nombreProyecto, String nombreOperador, String codigoProyectoPrincipal) {
		String condicionSql = "";

		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= (condicionSql == "") ? "and p.nombre like ''%"+nombreProyecto+"%''" : " and p.nombre like ''%"+nombreProyecto+"%''";
		}
		if(nombreOperador!= null && !nombreOperador.isEmpty()){
			condicionSql+= (condicionSql == "") ? "and  (TRIM(primerapellido || '' '' || segundoapellido || '' '' || primernombre || '' '' || segundonombre) like ''%"+nombreOperador+"%'' or o.nombre like ''%"+nombreOperador+"%'')" : " and (TRIM(primerapellido || '' '' || segundoapellido || '' '' || primernombre || '' '' || segundonombre) like ''%"+nombreOperador+"%'' or o.nombre like ''%"+nombreOperador+"%'')";
		}
		if(codigoProyectoPrincipal != null && !codigoProyectoPrincipal.isEmpty()){
			condicionSql+= " and p.id = ''"+codigoProyectoPrincipal+"'' ";
		}

		String sqlPproyecto="select * from dblink('"+dblinkSectorSubsector+"',"
				+ "'select distinct null, p.id, p.nombre, p.usuarioldap, "
				+ "TRIM(primerapellido || '' '' || segundoapellido || '' '' || primernombre || '' '' || segundonombre)  AS nombresapellidos, "
				+ "o.nombre, "
				+ "(SELECT stringvalue FROM public.variable where executionid like ''%''||p.id||''%'' "
				+ "and (nombre = ''Categorización'' or nombre =  ''Tipo de proyecto'') order by 1 desc limit 1), "
				+ "s.estrategico, (select count(*) from jbpm4_task where execution_id_ like ''%'' || p.id || ''%'') as tareas,"
				+ "p.fecharegistro as fecha "
				+ "from proyectolicenciaambiental p  "
				+ "left join persona r on r.pin = p.usuarioldap "
				+ "left join organizacion o on o.pin = p.usuarioldap "
				+ "left join subsector s on s.id = p.subsector "
				+ "where estadoproyecto = true " + condicionSql
				+ " ') as t1 "
				+ "(id character varying(255), codigo character varying(255), nombre text, usuario character varying(255), "
				+ "nombre_persona character varying(255), organizacion character varying(255), "
				+ "categoria character varying(255), "
				+ "estrategico boolean, tareas integer, fecha character varying(255))";
		
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		List<Object> result = queryProyecto.getResultList();
		if(result.size()>0){
			List<AutorizacionAdministrativa> projects = new ArrayList<AutorizacionAdministrativa>();
			for (Object object : result) {
				projects.add(new AutorizacionAdministrativa((Object[]) object, "5"));
			}
			
			return projects;
		}
		return null;
	}
	
	public List<AutorizacionAdministrativa> getProyectosRegularizacion(Integer sector, String area, String bloque, String concesion, String nombre, String nombreOperador) {
		try {
			String condicionSql = "";
			
			if(sector!= null){
				condicionSql += " and cp.sety_id = " + sector;
			}
			if(area!= null && !area.isEmpty()){
				condicionSql+=  "and bloc_area_nomination like '%"+area+"%'";
			}
			if(bloque!= null && !bloque.isEmpty()){
				condicionSql+= "and bloc_name like '%"+bloque+"%'";
			}
			if(nombre!= null && !nombre.isEmpty()){
				condicionSql+=  " and p.pren_name like '%"+nombre+"%'";
			}
			if(nombreOperador!= null && !nombreOperador.isEmpty()){
				condicionSql +=  " and (l.peop_name like '%" + nombreOperador + "%' or o.orga_name_organization like '%" + nombreOperador + "%')";
			}

			String queryString = "SELECT distinct p.pren_id, p.pren_code, p.pren_name, p.pren_creator_user, l.peop_name, "
					+ "o.orga_name_organization, c.cate_code, y.estadoproceso, st.sety_name  "
					+ "FROM suia_iii.projects_environmental_licensing p "
					+ "inner join public.users u on u.user_id = p.user_id "
					+ "left join public.people l on l.peop_id = u.peop_id "
				    + "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
				    + "inner join suia_iii.categories_catalog_system cs on cs.cacs_id = p.cacs_id "
				    + "inner join suia_iii.categories c on c.cate_id = cs.cate_id "
				    + "inner join suia_iii.categories_catalog_public cp on cs.cacp_id = cp.cacp_id "
				    + "inner join suia_iii.sector_types st on st.sety_id = cp.sety_id "
				    + "left join public.areas a on a.area_id = p.area_id "
				    + "left join suia_iii.projects_blocks pb on pb.pren_id = p.pren_id and pb.prbl_status = true "
				    + "left join suia_iii.blocks b on b.bloc_id = pb.bloc_id "
				    + "left join suia_iii.mining_grants mg on mg.pren_id = p.pren_id and mg.migr_status = true "
				    + "join " + getQueryJbpm("''Licencia Ambiental'', ''Registro ambiental v2'', ''Registro ambiental''") + " ON y.codigoproyecto = p.pren_code"
					+ " WHERE p.pren_status = true and p.pren_project_finalized = true and cate_code != 'I' " + condicionSql ;

			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if(result.size() > 0){
				List<AutorizacionAdministrativa> projects = new ArrayList<AutorizacionAdministrativa>();
				for (Object object : result) {
					projects.add(new AutorizacionAdministrativa((Object[]) object, "2"));
				}
				
				return projects;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<AutorizacionAdministrativa> getProyectosAprobacionRequisitosTecnicos(String nombre, String nombreProponente) {

		String condicionSql = "";
		if (nombre != null && !nombre.isEmpty()) {
			condicionSql += " and p.apte_name_proyect like '%" + nombre + "%'";
		}
		if (nombreProponente != null && !nombreProponente.isEmpty()) {
			condicionSql += " and (l.peop_name like '%" + nombreProponente + "%' or o.orga_name_organization like '%"+ nombreProponente + "%')";
		}

		String queryString = "SELECT distinct p.apte_id, p.apte_request, p.apte_name_proyect, p.apte_creator_user, "
				+ "l.peop_name, o.orga_name_organization, COALESCE(null), y.estadoproceso, "
				+ "CASE WHEN apte_started_of_necessity = false THEN apte_proyect ELSE null END "
				+ "FROM suia_iii.approval_technical_requirements p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
				+ "left join public.organizations o on o.peop_id = l.peop_id  and o.orga_ruc = u.user_name  "
				+ "join " + getQueryJbpm("''Aprobacion Requisitos Tecnicos Gestion de Desechos''") + " on y.codigoproyecto = p.apte_request or y.codigoproyecto = apte_proyect "
				+ " where apte_status = true " + condicionSql ;

		if(condicionSql != "") {
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				List<AutorizacionAdministrativa> projects = new ArrayList<AutorizacionAdministrativa>();
				for (Object object : result) {
					projects.add(new AutorizacionAdministrativa((Object[]) object, "3"));
				}
				
				return projects;
			}
		}

		return null;
	}
	
	public List<AutorizacionAdministrativa> getProyectosGeneradorDesechosPeligrosos( String nombreProponente) {

		String condicionSql = "";
		if (nombreProponente != null && !nombreProponente.isEmpty()) {
			condicionSql += " and (l.peop_name like '%" + nombreProponente + "%' or o.orga_name_organization like '%"+ nombreProponente + "%')";
		}
		//id, codigo, nombre_pro, usuario, nombre_per, orga, motivo_delete, stado, categoria
		String queryString = "SELECT distinct p.hwge_id, p.hwge_request, 'N/A', p.hwge_creator_user, l.peop_name, o.orga_name_organization, "
				+ "COALESCE(null) as sector, y.estadoproceso, p.pren_id "
				+ "FROM suia_iii.hazardous_wastes_generators p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
				+ "left join public.organizations o on o.peop_id = l.peop_id  and o.orga_ruc = u.user_name  "
				+ "left join suia_iii.projects_environmental_licensing pl on pl.pren_id = p.pren_id "
				+ "join " + getQueryJbpm("''Registro de generador de desechos especiales y peligrosos''")
				+ " on y.codigoproyecto = p.hwge_request or y.codigoproyecto = pl.pren_code "
				+ " where hwge_status = true " + condicionSql ;

		if(condicionSql != "") {
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				List<AutorizacionAdministrativa> projects = new ArrayList<AutorizacionAdministrativa>();
				for (Object object : result) {
					projects.add(new AutorizacionAdministrativa((Object[]) object, "4"));
				}
				
				return projects;
			}
		}

		return null;
	}
	
	public List<AutorizacionAdministrativa> getProyectosAprobacionRequisitosTecnicosDig(String nombre, String identificacionProponente) {
		String condicionSql = "";
		if (nombre != null && !nombre.isEmpty()) {
			condicionSql += " and p.apte_name_proyect like '%" + nombre + "%'";
		}
		if (identificacionProponente != null && !identificacionProponente.isEmpty()) {
			condicionSql += " and (l.peop_pin like '%" + identificacionProponente + "%' or o.orga_ruc like '%"+ identificacionProponente + "%')";
		}

		String queryString = "SELECT distinct p.apte_id, p.apte_request, of.ofart_report_number, p.apte_creator_user, "
				+ "l.peop_name, o.orga_name_organization, COALESCE(null), cast(true as boolean ) estadoproceso, "
				+ "CASE WHEN apte_started_of_necessity = false THEN apte_proyect ELSE null END, y.fechaInicio, y.fechaFin "
				+ "FROM suia_iii.approval_technical_requirements p "
				+ "inner join suia_iii.office_art of on of.apte_id = p.apte_id "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
				+ "left join public.organizations o on o.peop_id = l.peop_id  and o.orga_ruc = u.user_name  "
				+ "join " + getQueryJbpmOtro("''Aprobacion Requisitos Tecnicos Gestion de Desechos''") + " on y.codigoproyecto = p.apte_request or y.codigoproyecto = apte_proyect "
				+ " where apte_status = true and apte_voluntary is true " + condicionSql
				+ " and p.apte_id not in ( SELECT aspr_code_id FROM coa_digitalization_linkage.associated_projects where aspr_status and aspr_type = 3 and aspr_code_id is not null ) ";

		if(condicionSql != "") {
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				List<AutorizacionAdministrativa> projects = new ArrayList<AutorizacionAdministrativa>();
				for (Object object : result) {
					projects.add(new AutorizacionAdministrativa((Object[]) object, "3"));
				}
				return projects;
			}
		}
		return null;
	}
	
	public List<AutorizacionAdministrativa> getProyectosGeneradorDesechosPeligrososDig(String identificacionProponente) {
		String condicionSql = "";
		if (identificacionProponente != null && !identificacionProponente.isEmpty()) {
			condicionSql += " and (l.peop_pin like '%" + identificacionProponente + "%' or o.orga_ruc like '%"+ identificacionProponente + "%')";
		}
		//id, codigo, nombre_pro, usuario, nombre_per, orga, motivo_delete, stado, categoria
		String queryString = "with registrodesechos as ("
				+ "	 SELECT hwge_request, hwge_code, max(hwge_id) hwge_id "
				+ "	FROM suia_iii.hazardous_wastes_generators  "
				+ "	 where hwge_status "
				+ "	 group by hwge_request, hwge_code"
				+ " )"
				+ ""
				+ "SELECT distinct p.hwge_id, p.hwge_request, p.hwge_code, p.hwge_creator_user, l.peop_name, o.orga_name_organization,  "
				+ "COALESCE(null) as sector, cast(true as boolean ) estadoproceso, null pren_id, y.fechaInicio, y.fechaFin "
				+ "FROM registrodesechos rgd inner join suia_iii.hazardous_wastes_generators p on rgd.hwge_id = p.hwge_id "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
				+ "left join public.organizations o on o.peop_id = l.peop_id  and o.orga_ruc = u.user_name  "
				+ "join " + getQueryJbpmOtro("''Registro de generador de desechos especiales y peligrosos''")
				+ " on y.codigoproyecto = p.hwge_request "
				+ " where p.hwge_status = true and p.pren_id is null " + condicionSql
				+ " and p.hwge_id not in ( SELECT aspr_code_id FROM coa_digitalization_linkage.associated_projects where aspr_status and aspr_type = 4 and aspr_code_id is not null ) ";

		if(condicionSql != "") {
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				List<AutorizacionAdministrativa> projects = new ArrayList<AutorizacionAdministrativa>();
				for (Object object : result) {
					projects.add(new AutorizacionAdministrativa((Object[]) object, "4"));
				}
				return projects;
			}
		}
		return null;
	}
	
	
	public String getQueryJbpmOtro(String procesos){
		String sqlProyecto="(select t1.codigoproyecto, max(t1.fechaInicio) fechaInicio, max(t1.fechaFin) fechaFin, t1.estadoproceso "
				+ "from dblink('"+dblinkSuiaBpm+"',"
				+ "'select distinct v.value codigoProyecto, p.start_date fechaInicio, p.end_date fechaFin, "
				+ "CASE WHEN p.status = 1 THEN ''En trámite'' "
				+ "WHEN p.status = 2 THEN ''Completado'' "
				+ "WHEN p.status = 3 THEN ''Abortado'' "
				+ "ELSE ''En trámite'' END as estadoProceso "
				+ "from variableinstancelog v "
				+ "inner join processinstancelog p on p.processinstanceid = v.processinstanceid "
				+ "where  "
				+ "p.processname in (" + procesos + ") "
				+ "and (v.variableid=''tramite'' ) "
				+ "and user_identity not like ''%msit%'' "
				+ "and user_identity not like ''%admin%'' "
				+ "and user_identity not like ''%diana.pabon%'' "
				+ "and user_identity not IN ( ''1714419148'', ''1712876364'', ''1717679441'', ''1720283686'') "
				+ "and p.status in (2) order by 1') as t1 "
				+ "(codigoproyecto text, fechainicio text, fechaFin text, estadoproceso text) "
				+ "GROUP BY t1.codigoproyecto, t1.estadoproceso "
				+ "ORDER BY t1.codigoproyecto) y ";
		
		return sqlProyecto;
	}
	
	//busqueda de campos 4 categorias
	public String getQuery4CategoriasVariableCategoria(String codigoProyecto){
		String condicionSql = "";
		condicionSql+=  " and v.codigoProyecto= ''%"+codigoProyecto+"%''";
		String sqlProyecto="select descripcion from dblink('"+dblinkSuiaVerde+"',"
				+ "'select c.ca_description from variable v "
				+ "inner join catalogo_categoria cc on cc.id_catalogo = p.id_catalogo "
				+ "where estadoproyecto = true" + condicionSql + "') as t1 "
				+ "(descripcion varying(255), codigo character varying(255), nombre text, usuario character varying(255), "
				+ "nombre_persona character varying(255))";
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlProyecto);
		
		List<Object> result = queryProyecto.getResultList();
		if(result.size()>0){
			return result.get(0).toString();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String getVariableSectorSubsector(String proyecto, String tipo){
		
		String sqlProyecto="select * from dblink('"+dblinkSectorSubsector+"',"
				+ "'select stringvalue "
				+ "from variable "
				+ "where nombre = ''" + tipo
				+ "'' and proyecto = ''"+ proyecto + "'' order by id desc' ) as t1 "
				+ "(variable character varying(255))";
		
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlProyecto);
		
		List<Object> result = queryProyecto.getResultList();
		if(result.size()>0){
			return result.get(0).toString();
		}
		
		return null;
	}
	
	public String getSqlNotDigitalizacion(String campo, String operadorDocumento){
		return " and "+ campo +" not in ( select enaa_project_code from coa_digitalization_linkage.environmental_administrative_authorizations a left join coa_digitalization_linkage.associated_projects b on a.enaa_id = b.aspr_code_id and b.aspr_status = true and b.aspr_code_id is not null "
				+ "	where a.enaa_status = true and enaa_history is false and enaa_project_code is not null "+((operadorDocumento != null && !operadorDocumento.isEmpty())?" and a.enaa_ci_user = '"+operadorDocumento+"'": "" )+" )";
	}
	
	public List<AutorizacionAdministrativa> getProyectosDigitalizados(String nombreOperador, Integer idProyectoPrincipal, boolean asociacion) {
		try {
			String condicionSql = "";
			
//			if(sector!= null){
//				condicionSql += " and cp.sety_id = " + sector;
//			}
//			if(area!= null && !area.isEmpty()){
//				condicionSql+=  "and bloc_area_nomination like '%"+area+"%'";
//			}
//			if(bloque!= null && !bloque.isEmpty()){
//				condicionSql+= "and bloc_name like '%"+bloque+"%'";
//			}
//			if(nombre!= null && !nombre.isEmpty()){
//				condicionSql+=  " and p.pren_name like '%"+nombre+"%'";
//			}
			if(nombreOperador!= null && !nombreOperador.isEmpty()){
				condicionSql +=  " and d.enaa_ci_user  like '%" + nombreOperador + "%' ";
			}
			if(idProyectoPrincipal != null ){
				condicionSql +=  " and d.enaa_id <> "+idProyectoPrincipal+" and d.enaa_parent_id is null " ;
			}
			if(asociacion){
				condicionSql += " and d.enaa_status_finalized = true " ;
			}

			String queryString = "SELECT distinct d.enaa_id, d.enaa_project_code, d.enaa_project_name, d.enaa_ci_user, d.enaa_name_user, null, "
					+ " d.enaa_environmental_administrative_authorization, null estrategico, 0 tareas, 0 tareas_4cat, d.enaa_register_date, d.enaa_register_finalized_date, d.enaa_project_summary, c.caci_name, st.sety_name, a.area_abbreviation, d.enaa_resolution, cast(d.enaa_resolution_date as varchar) enaa_resolution_date, d.enaa_status_finalized, d.enaa_id as idDigitalizacion , d.enaa_process_instance_id, cast('6' as varchar) tiposistema, d.enaa_system "
					+ "FROM coa_digitalization_linkage.environmental_administrative_authorizations d "
				    + "left join suia_iii.sector_types st on st.sety_id = d.sety_id "
				    + " left join coa_mae.catalog_ciuu c on d.caci_id = c.caci_id  "
				    + " left join areas a on a.area_id = d.area_id_aaa  "
				    + " WHERE d.enaa_status = true  " + condicionSql ;

			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if(result.size() > 0){
				List<AutorizacionAdministrativa> projects = new ArrayList<AutorizacionAdministrativa>();
				for (Object object : result) {
					projects.add(new AutorizacionAdministrativa((Object[]) object, "6"));
				}
				return projects;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<AutorizacionAdministrativa>();
	}
	
	public List<AutorizacionAdministrativa> getProyectosDigitalizadosPorArea(Integer usuarioId) {
		try {
			String queryString = "SELECT distinct d.enaa_id, d.enaa_project_code, d.enaa_project_name, d.enaa_ci_user, d.enaa_name_user, null, "
					+ " d.enaa_environmental_administrative_authorization, null estrategico, 0 tareas, 0 tareas_4cat, d.enaa_register_date, d.enaa_register_finalized_date, d.enaa_project_summary, c.caci_name, st.sety_name, a.area_abbreviation, d.enaa_resolution, cast(d.enaa_resolution_date as varchar) enaa_resolution_date, d.enaa_status_finalized, d.enaa_id as idDigitalizacion , d.enaa_process_instance_id, cast('6' as varchar) tiposistema, d.enaa_system  "
					+ "FROM coa_digitalization_linkage.environmental_administrative_authorizations d "
				    + "left join suia_iii.sector_types st on st.sety_id = d.sety_id "
				    + " left join coa_mae.catalog_ciuu c on d.caci_id = c.caci_id  "
				    + " left join areas a on a.area_id = d.area_id_aaa "
				    + " WHERE d.enaa_status = true  and area_id_ercs in ("
				    + "select area_id "
				    + "from areas_users "
				    + "where user_id = "+usuarioId+" "
				    + ") "  ;
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if(result.size() > 0){
				List<AutorizacionAdministrativa> projects = new ArrayList<AutorizacionAdministrativa>();
				for (Object object : result) {
					Object[] array = (Object[]) object;
					AutorizacionAdministrativa aaa = new AutorizacionAdministrativa((Object[]) object, (String)array[21]);
					if(aaa.getIdDigitalizacion() != null){
						List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados = buscarProyectosAsociados(aaa);
						aaa.setListaProyectosAsociadosVer(listaProyectosAsociados);
					}
					projects.add(aaa);
				}
				return projects;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<AutorizacionAdministrativa>();
	}
	/**********************************************************************************************************************
	 * 
	 * 		funciones con lazy
	 * 
	 * ******************************************************************************************************************/

	@SuppressWarnings("unchecked")
	public List<AutorizacionAdministrativa> getProyectos4CategoriasLazy(Integer tipo, Integer inicio, Integer total, String nombreProyecto, String nombreProponente, String documentoOperador, Boolean soloHidrocarburos, String codigoProyectoPrincipal, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal){
		
		List<Object> result = null;
		switch (tipo) {
		case 1:
			result = getQueryProyectosSectorSubSector(inicio, total, documentoOperador, codigoProyectoPrincipal, nombreProyecto, sector, tipoProyecto, fechaRegistro, idProyectoPrincipal);
			break;
		case 2:
			result = getQueryProyectosCuatroCategorias(inicio, total, documentoOperador, codigoProyectoPrincipal, nombreProyecto, sector, tipoProyecto, fechaRegistro, idProyectoPrincipal);
			break;
		case 3:
			result = getProyectosSuiaiiiAntes2018(inicio, total, documentoOperador, codigoProyectoPrincipal, nombreProyecto, sector, tipoProyecto, fechaRegistro, idProyectoPrincipal);
			break;
		case 4:
			result = getProyectosSuiaiii2018(inicio, total, documentoOperador, codigoProyectoPrincipal, nombreProyecto, sector, tipoProyecto, fechaRegistro, idProyectoPrincipal);
			break;
		case 5:
			result = getProyectosRcoa(inicio, total, documentoOperador, codigoProyectoPrincipal, nombreProyecto, sector, tipoProyecto, fechaRegistro, idProyectoPrincipal);
			break;
		case 6:
			result = getProyectosDigitalizados(inicio, total, documentoOperador, codigoProyectoPrincipal, nombreProyecto, sector, tipoProyecto, fechaRegistro, idProyectoPrincipal);
			break;
		default:
			break;
		}

		Integer index =1;
		if(result.size()>0){
			List<AutorizacionAdministrativa> projects = new ArrayList<AutorizacionAdministrativa>();
			for (Object object : result) {
				Object[] array = (Object[]) object;
				AutorizacionAdministrativa aaa = new AutorizacionAdministrativa((Object[]) object, (String)array[21]);
				// busco las fechas de finalizacion del proyecto
				String sistema="";
				switch ((String)array[21]) {
				case "1":
					sistema = "4CAT";
					break;
				case "5":
					sistema = "SECTOR";
					break;

				default:
					break;
				}
				// cargo la lista de proyectos asociados cuando se lista los proyectos
				if(aaa.getIdDigitalizacion() != null && idProyectoPrincipal == null){
					List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados = buscarProyectosAsociados(aaa);
					aaa.setListaProyectosAsociadosVer(listaProyectosAsociados);
				}
				
				if(!sistema.isEmpty()){
					aaa.setId(index);
					index +=1;
					List<Object> objectAux = procesosDigitalizacionFacade.getProcesoFechas(aaa.getCodigo(), 0L, sistema);
					if(objectAux != null && objectAux.size() > 0){
						for (Object object2 : objectAux) {
							Object[] arrayAux = (Object[]) object2;
							if(arrayAux[2] != null){
								Date fecha = (Date)arrayAux[2];
								aaa.setFecha(fecha.toString());
							}
							if(arrayAux[3] != null){
								Date fecha = (Date)arrayAux[3];
								aaa.setFechaFin(fecha.toString());
							}
							//verifico si es de interseccion
							if(((String)arrayAux[1]).contains("CertificadoInterseccion"))
								aaa.setEstado("Actualización de Certificado de Intersección");
						}
					}
				}
				projects.add(aaa);
			}
			return projects;
		}
		return new ArrayList<AutorizacionAdministrativa>();
	}

	
	public String getQueryProyectosTodos(String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal){
		String condicionSql = "";
		dblinkSuiaVerde = dblinkSuiaVerde.replace("233", "175");
		/// proyectos suia anterior
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.pren_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.pren_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.pren_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			//condicionSql+= " and  lower(p.pren_name) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(y.fechaInicio) as varchar like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}

		String sqlProyectoSuia = "SELECT distinct p.pren_id, p.pren_code as codigo, p.pren_name, p.pren_creator_user, l.peop_name, "
				+ "o.orga_name_organization, c.cate_code, cast(true as boolean) estado, 0 numtareas, 0 numtareas4cat, y.fechaInicio, y.fechafin, null resumen, null actividad, st.sety_name, a.area_abbreviation, "
				+ "cf.numeroresolucion, cf.fecharesolucion, "
				+ "cast(null as boolean) digitalizado, cast(null as integer) iddigitalizacion,  cast(null as integer) idproceso, cast('2' as varchar) tiposistema, cast(0 as integer) enaa_system "
				+ "FROM suia_iii.projects_environmental_licensing p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
			    + "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
			    + "inner join suia_iii.categories_catalog_system cs on cs.cacs_id = p.cacs_id "
			    + "inner join suia_iii.categories c on c.cate_id = cs.cate_id "
			    + "inner join suia_iii.categories_catalog_public cp on cs.cacp_id = cp.cacp_id "
			    + "inner join suia_iii.sector_types st on st.sety_id = cp.sety_id "
			    + "left join public.areas a on a.area_id = p.area_id "
			    + "left join suia_iii.projects_blocks pb on pb.pren_id = p.pren_id and pb.prbl_status = true "
			    + "left join (select max(case when cafa_license_number is null then cafa_office_number else cafa_license_number end) numeroresolucion, max(cast(cafa_date_update as varchar)) fecharesolucion, pren_id from suia_iii.catii_fapma where cafa_status group by pren_id) cf on p.pren_id = cf.pren_id "
			    + "left join suia_iii.blocks b on b.bloc_id = pb.bloc_id "
			    + "left join suia_iii.mining_grants mg on mg.pren_id = p.pren_id and mg.migr_status = true "
			    + "join " + getQueryJbpm("''Licencia Ambiental'', ''Registro ambiental v2'', ''Registro ambiental''") + " ON y.codigoproyecto = p.pren_code "
			    + " WHERE p.pren_status = true  " + condicionSql + getSqlNotDigitalizacion("p.pren_code", documentoOperador);

		
		condicionSql = "";
		/// proyectos suia anterior
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.pren_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.pren_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.pren_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			//condicionSql+= " and  lower(p.pren_name) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(y.fechaInicio) as varchar like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		String sqlProyectoSuia2018 = "SELECT distinct p.pren_id, p.pren_code, p.pren_name, p.pren_creator_user, l.peop_name, "
				+ "o.orga_name_organization, c.cate_code, cast(true as boolean) estado, 0 numtareas, 0 numtareas4cat, y.fechaInicio, y.fechafin, null resumen, null actividad, st.sety_name, a.area_abbreviation, v.numeroresolucion, cast(v.fecharesolucion as varchar) , "
				+ "cast(null as boolean) digitalizado, cast(null as integer) iddigitalizacion,  cast(null as integer) idproceso, cast('2' as varchar) tiposistema, cast(0 as integer) enaa_system "
				+ "FROM suia_iii.projects_environmental_licensing p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
			    + "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
			    + "inner join suia_iii.categories_catalog_system cs on cs.cacs_id = p.cacs_id "
			    + "inner join suia_iii.categories c on c.cate_id = cs.cate_id "
			    + "inner join suia_iii.categories_catalog_public cp on cs.cacp_id = cp.cacp_id "
			    + "inner join suia_iii.sector_types st on st.sety_id = cp.sety_id "
			    + "left join public.areas a on a.area_id = p.area_id "
			    + "left join suia_iii.projects_blocks pb on pb.pren_id = p.pren_id and pb.prbl_status = true "
			    + "left join suia_iii.blocks b on b.bloc_id = pb.bloc_id "
			    + "left join suia_iii.mining_grants mg on mg.pren_id = p.pren_id and mg.migr_status = true "
			    + "left join coa_digitalization_linkage.v_proyecto_resolucion v on p.pren_id = v.pren_id "
			    + "join " + getQueryJbpm2018("''Licencia Ambiental''") + " ON y.codigoproyecto = p.pren_code "
			    + " WHERE p.pren_status = true and pren_register_date > '"+Constantes.getFechaBloqueoRegistroFisico()+"' " + condicionSql + getSqlNotDigitalizacion("p.pren_code", documentoOperador);

		
		/// proyectos 4 categorias
		condicionSql="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.usuarioldap = ''"+documentoOperador+"''" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.id) like ''%"+codigo.toLowerCase()+"%''" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.nombre) like ''%"+nombreProyecto.toLowerCase()+"%''" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(cc.cata_sector) like ''%"+sector.toLowerCase()+"%''" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(cc.ca_categoria) like ''%"+tipoProyecto.toLowerCase()+"%''" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
		}
		String sqlProyecto4Cat ="select cast(id as integer) id, codigo, nombre, usuario, nombre_persona, organizacion, categoria, estrategico, tareas, tareas_4cat, cast(null as date) fecha_inicio, cast(null as date) fecha_fin, resumen, actividad, sector, cast(null as varchar) area_abbreviation, cast(null as varchar) numeroresolucion, cast(null as varchar) fecharesolucion, cast(null as boolean) digitalizado, cast(null as integer) iddigitalizacion,  cast(null as integer) idproceso,  cast('1' as varchar) tiposistema, cast(0 as integer) enaa_system "
				+ "from dblink('"+dblinkSuiaVerde+"',"
				+ "'select distinct 0,  p.id, p.nombre, p.usuarioldap, r.nombresapellidos, o.nombre,  "
				+ "cc.ca_categoria, cc.estrategico, (select count(*) from jbpm4_task where execution_id_ like ''%'' || p.id || ''%'') as tareas, cc.cata_sector, "
				+ "t2.tareas as tareas_4cat, "
				+ " p.resumen, cc.ca_descripcion "
				+ "from proyectolicenciaambiental p   "
				+ "inner join catalogo_categoria cc on cc.id_catalogo = p.id_catalogo "
				+ "left join persona r on r.pin = p.usuarioldap "
				+ "left join organizacion o on o.pin = p.usuarioldap "
				+ "left join (select proyecto, count(*) as tareas "
				+ "from (select substring(execution_, \"position\"(execution_ , ''.'') + 1) AS proyecto, dbid_ from jbpm4_hist_task ) as t GROUP BY t.proyecto) as t2 on t2.proyecto = p.id "
				+ "left join (select codigo_proyecto, end_date from dblink ( "
				+ "''"+dblinkSuiaHidro+"'', "
				+ "''select distinct v.value, p.end_date from variableinstancelog as v "
				+ "inner join processinstancelog as p on v.processinstanceid = p.processinstanceid "
				+ "where p.processid in (''''Hydrocarbons.pagoLicencia'''',''''Hydrocarbons.pagoLicenciaEnte'''') "
				+ "and p.status = 2 and p.end_date is not null and v.variableid = ''''codigoProyecto'''' '') as t1 ("
				+ "codigo_proyecto character varying(255), end_date date)) y2 on y2.codigo_proyecto = p.id "
				+ "where estadoproyecto = true  and cc.ca_categoria != ''I'' " + condicionSql + "') as t1 "
				+ "(id character varying(255), codigo character varying(255), nombre text, usuario character varying(255), "
				+ "nombre_persona character varying(255), organizacion character varying(255), categoria character varying(255), "
				+ "estrategico boolean, tareas integer, sector character varying(255), tareas_4cat integer, resumen text, actividad character varying(800) ) "
				+ " where true " +getSqlNotDigitalizacion("codigo", documentoOperador);
		

		/// proyectosector subsector
		condicionSql="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.usuarioldap = ''"+documentoOperador+"''" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.id) like ''%"+codigo.toLowerCase()+"%''" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.nombre) like ''%"+nombreProyecto.toLowerCase()+"%''" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower( t.name) like ''%"+sector.toLowerCase()+"%''" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
		}

		String sqlPproyectoSector="select cast(id as integer), codigo, nombre, usuario, nombre_persona, organizacion, categoria, estrategico, tareas, 0 numTare4cat, cast(fecha as date), cast(null as date) fechafin, resumen, actividad, sector, cast(null as varchar) area_abbreviation, cast(null as varchar) numeroresolucion, cast(null as varchar) fecharesolucion, cast(null as boolean) digitalizado, cast(null as integer) iddigitalizacion,  cast(null as integer) idproceso,  cast('5' as varchar) tiposistema, cast(0 as integer) enaa_system "
				+ "from dblink('"+dblinkSectorSubsector+"',"
				+ "'select distinct 0, p.id, p.nombre, p.usuarioldap, "
				+ "TRIM(primerapellido || '' '' || segundoapellido || '' '' || primernombre || '' '' || segundonombre)  AS nombresapellidos, "
				+ "o.nombre, "
				+ "null, "
				+ "s.estrategico, (select count(*) from jbpm4_task where execution_id_ like ''%'' || p.id || ''%'') as tareas,"
				+ "p.fecharegistro as fecha, p.resumen, s.nombre, t.name "
				+ "from proyectolicenciaambiental p  "
				+ "left join persona r on r.pin = p.usuarioldap "
				+ "left join organizacion o on o.pin = p.usuarioldap "
				+ "left join subsector s on s.id = p.subsector "
				+ "left join tiposector t on p.sectorla = t.id "
				+ "where estadoproyecto = true and tp = ''LICENCIA'' " + condicionSql
				+ " ') as t1 "
				+ "(id character varying(255), codigo character varying(255), nombre text, usuario character varying(255), "
				+ "nombre_persona character varying(255), organizacion character varying(255), "
				+ "categoria character varying(255), "
				+ "estrategico boolean, tareas integer, fecha character varying(255), resumen text, actividad text, sector text) "
				+ " where true " +getSqlNotDigitalizacion("codigo", documentoOperador);
		

		/// proyectos RCOA
		condicionSql="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  d.prco_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(d.prco_cua) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(d.prco_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			//condicionSql+= " and  lower(d.enaa_environmental_administrative_authorization) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(y.fechaInicio) as varchar like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		String sqlPproyectoRcoa = "SELECT d.prco_id, d.prco_cua, d.prco_name, d.prco_creator_user, l.peop_name, null, "
				+ " case d.prco_categorizacion when 2 then 'Registro Ambiental' else '' end, "
				+ " cast(false as boolean) estrategico, 0 tareas, 0 tareas_4cat, prco_creation_date fechaInicio, null fechafin, null summary, c.caci_name, st.sety_name, a.area_abbreviation, null resolution, null resolution_date, null estadof, null as idDigitalizacion , cast(null as integer) idproceso,  cast('7' as varchar) tiposistema, cast(0 as integer) enaa_system  "
				+ "FROM coa_mae.project_licencing_coa d "
				+ "inner join coa_mae.project_licencing_coa_ciuu b on d.prco_id = b.prco_id and prli_status is true "
				+ "inner join public.users u on u.user_id = d.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
			    + " left join coa_mae.catalog_ciuu c on b.caci_id = c.caci_id and caci_status is true "
			    + "left join suia_iii.sector_types st on st.sety_id = c.sety_id "
			    + " left join areas a on a.area_id = d.area_id  "
			    + " WHERE d.prco_status = true and prco_creation_date > '2021-12-01' and prco_categorizacion in (2) "
			    + " and caci_code in ('B0990.01', 'B0990.01.01', 'B0990.02', 'B0990.02.01', 'B0990.09', 'B0990.09.01') "
			    + " and prco_registration_status" + condicionSql ;


		/// proyectos digitalizados
		condicionSql="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  d.enaa_ci_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(d.enaa_project_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(d.enaa_project_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(d.enaa_environmental_administrative_authorization) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(d.enaa_register_date) as varchar like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		if(idProyectoPrincipal!= null ){
			condicionSql +=  " and d.enaa_id <> "+idProyectoPrincipal ;
		}
		String sqlPproyectoDigitalizados = "SELECT distinct d.enaa_id, d.enaa_project_code, d.enaa_project_name, d.enaa_ci_user, d.enaa_name_user, null, "
				+ " d.enaa_environmental_administrative_authorization, cast(false as boolean) estrategico, 0 tareas, 0 tareas_4cat, d.enaa_register_date, d.enaa_register_finalized_date, d.enaa_project_summary, c.caci_name, st.sety_name, a.area_abbreviation, d.enaa_resolution, cast(d.enaa_resolution_date as varchar) enaa_resolution_date, d.enaa_status_finalized, d.enaa_id as idDigitalizacion , d.enaa_process_instance_id,  cast('6' as varchar) tiposistema, d.enaa_system "
				+ "FROM coa_digitalization_linkage.environmental_administrative_authorizations d "
			    + "left join suia_iii.sector_types st on st.sety_id = d.sety_id "
			    + " left join coa_mae.catalog_ciuu c on d.caci_id = c.caci_id  "
			    + " left join areas a on a.area_id = d.area_id_aaa  "
			    + " WHERE d.enaa_status = true and d.enaa_environmental_administrative_authorization not in ('Certificado Ambiental') " + condicionSql ;
		
		return  sqlProyectoSuia + " union "+ sqlProyectoSuia2018+" union " + sqlProyecto4Cat + " union " + sqlPproyectoSector + " union " + sqlPproyectoDigitalizados + " union " +sqlPproyectoRcoa;
	}
	
	public Integer contarRegistros(String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal){
		String condicionSql = "";
		dblinkSuiaVerde = dblinkSuiaVerde.replace("233", "175");
		/// proyectos suia anterior
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.pren_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.pren_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.pren_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			//condicionSql+= " and  lower(p.pren_name) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(y.fechaInicio) as varchar like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		if(idProyectoPrincipal!= null ){
			condicionSql +=  " and p.pren_id <> "+idProyectoPrincipal ;
		}

		String sqlProyectoSuia = "SELECT count(*) "
				+ "FROM suia_iii.projects_environmental_licensing p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
			    + "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
			    + "inner join suia_iii.categories_catalog_system cs on cs.cacs_id = p.cacs_id "
			    + "inner join suia_iii.categories c on c.cate_id = cs.cate_id "
			    + "inner join suia_iii.categories_catalog_public cp on cs.cacp_id = cp.cacp_id "
			    + "inner join suia_iii.sector_types st on st.sety_id = cp.sety_id "
			    + "left join public.areas a on a.area_id = p.area_id "
			    + "left join suia_iii.projects_blocks pb on pb.pren_id = p.pren_id and pb.prbl_status = true "
			    + "left join suia_iii.blocks b on b.bloc_id = pb.bloc_id "
			    + "left join suia_iii.mining_grants mg on mg.pren_id = p.pren_id and mg.migr_status = true "
			    + "left join coa_digitalization_linkage.v_proyecto_resolucion v on p.pren_id = v.pren_id "
			    + "join " + getQueryJbpm("''Licencia Ambiental'', ''Registro ambiental v2'', ''Registro ambiental''") + " ON y.codigoproyecto = p.pren_code "
			    + " WHERE p.pren_status = true  " + condicionSql + getSqlNotDigitalizacion("p.pren_code", documentoOperador);

		condicionSql = "";
		/// proyectos suia anterior
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.pren_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.pren_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.pren_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			//condicionSql+= " and  lower(p.pren_name) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(y.fechaInicio) as varchar like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		String sqlProyectoSuia2018 = "SELECT count(*) "
				+ "FROM suia_iii.projects_environmental_licensing p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
			    + "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
			    + "inner join suia_iii.categories_catalog_system cs on cs.cacs_id = p.cacs_id "
			    + "inner join suia_iii.categories c on c.cate_id = cs.cate_id "
			    + "inner join suia_iii.categories_catalog_public cp on cs.cacp_id = cp.cacp_id "
			    + "inner join suia_iii.sector_types st on st.sety_id = cp.sety_id "
			    + "left join public.areas a on a.area_id = p.area_id "
			    + "left join suia_iii.projects_blocks pb on pb.pren_id = p.pren_id and pb.prbl_status = true "
			    + "left join suia_iii.blocks b on b.bloc_id = pb.bloc_id "
			    + "left join suia_iii.mining_grants mg on mg.pren_id = p.pren_id and mg.migr_status = true "
			    + "left join coa_digitalization_linkage.v_proyecto_resolucion v on p.pren_id = v.pren_id "
			    + "join " + getQueryJbpm2018("''Licencia Ambiental''") + " ON y.codigoproyecto = p.pren_code "
			    + " WHERE p.pren_status = true and pren_register_date > '"+Constantes.getFechaBloqueoRegistroFisico()+"' " + condicionSql + getSqlNotDigitalizacion("p.pren_code", documentoOperador);

		/// proyectos 4 categorias
		condicionSql="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.usuarioldap = ''"+documentoOperador+"''" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.id) like ''%"+codigo.toLowerCase()+"%''" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.nombre) like ''%"+nombreProyecto.toLowerCase()+"%''" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(cc.cata_sector) like ''%"+sector.toLowerCase()+"%''" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(cc.ca_categoria) like ''%"+tipoProyecto.toLowerCase()+"%''" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
		}

		String sqlProyecto4Cat="select count(*) from dblink('"+dblinkSuiaVerde+"',"
				+ "'select distinct null,  p.id, p.nombre, p.usuarioldap, r.nombresapellidos, o.nombre,  "
				+ "cc.ca_categoria, cc.estrategico, (select count(*) from jbpm4_task where execution_id_ like ''%'' || p.id || ''%'') as tareas, cc.cata_sector, "
				+ "t2.tareas as tareas_4cat, "
				+ " p.resumen, cc.ca_descripcion "
				+ "from proyectolicenciaambiental p   "
				+ "inner join catalogo_categoria cc on cc.id_catalogo = p.id_catalogo "
				+ "left join persona r on r.pin = p.usuarioldap "
				+ "left join organizacion o on o.pin = p.usuarioldap "
				+ "left join (select proyecto, count(*) as tareas "
				+ "from (select substring(execution_, \"position\"(execution_ , ''.'') + 1) AS proyecto, dbid_ from jbpm4_hist_task ) as t GROUP BY t.proyecto) as t2 on t2.proyecto = p.id "
				+ "left join (select codigo_proyecto, end_date from dblink ( "
				+ "''"+dblinkSuiaHidro+"'', "
				+ "''select distinct v.value, p.end_date from variableinstancelog as v "
				+ "inner join processinstancelog as p on v.processinstanceid = p.processinstanceid "
				+ "where p.processid in (''''Hydrocarbons.pagoLicencia'''',''''Hydrocarbons.pagoLicenciaEnte'''') "
				+ "and p.status = 2 and p.end_date is not null and v.variableid = ''''codigoProyecto'''' '') as t1 ("
				+ "codigo_proyecto character varying(255), end_date date)) y2 on y2.codigo_proyecto = p.id "
				+ "where estadoproyecto = true  and cc.ca_categoria != ''I'' " + condicionSql + "') as t1 "
				+ "(id character varying(255), codigo character varying(255), nombre text, usuario character varying(255), "
				+ "nombre_persona character varying(255), organizacion character varying(255), categoria character varying(255), "
				+ "estrategico boolean, tareas integer, sector character varying(255), tareas_4cat integer, resumen text, actividad character varying(800) ) "
				+ " where true " +getSqlNotDigitalizacion("codigo", documentoOperador);

		/// proyectossector subsector
		condicionSql="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.usuarioldap = ''"+documentoOperador+"''" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.id) like ''%"+codigo.toLowerCase()+"%''" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.nombre) like ''%"+nombreProyecto.toLowerCase()+"%''" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(t.name) like ''%"+sector.toLowerCase()+"%''" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
		}
		String sqlPproyectoSector="select count(*) "
				+ "from dblink('"+dblinkSectorSubsector+"',"
				+ "'select distinct null, p.id, p.nombre, p.usuarioldap, "
				+ "TRIM(primerapellido || '' '' || segundoapellido || '' '' || primernombre || '' '' || segundonombre)  AS nombresapellidos, "
				+ "o.nombre, "
				+ "null, "
				+ "s.estrategico, (select count(*) from jbpm4_task where execution_id_ like ''%'' || p.id || ''%'') as tareas,"
				+ "p.fecharegistro as fecha, p.resumen, s.nombre, t.name "
				+ "from proyectolicenciaambiental p  "
				+ "left join persona r on r.pin = p.usuarioldap "
				+ "left join organizacion o on o.pin = p.usuarioldap "
				+ "left join subsector s on s.id = p.subsector "
				+ "left join tiposector t on p.sectorla = t.id "
				+ "where estadoproyecto = true and tp = ''LICENCIA'' " + condicionSql
				+ " ') as t1 "
				+ "(id character varying(255), codigo character varying(255), nombre text, usuario character varying(255), "
				+ "nombre_persona character varying(255), organizacion character varying(255), "
				+ "categoria character varying(255), "
				+ "estrategico boolean, tareas integer, fecha character varying(255), resumen text, actividad text, sector text) "
				+ " where true " +getSqlNotDigitalizacion("codigo", documentoOperador);
		

		/// proyectos RCOA
		condicionSql="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  d.prco_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(d.prco_cua) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(d.prco_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			//condicionSql+= " and  lower(d.enaa_environmental_administrative_authorization) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(y.fechaInicio) as varchar like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		String sqlPproyectoRcoa = "SELECT count(*)  "
				+ "FROM coa_mae.project_licencing_coa d "
				+ "inner join coa_mae.project_licencing_coa_ciuu b on d.prco_id = b.prco_id and prli_status is true "
				+ "inner join public.users u on u.user_id = d.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
			    + " left join coa_mae.catalog_ciuu c on b.caci_id = c.caci_id and caci_status is true "
			    + "left join suia_iii.sector_types st on st.sety_id = c.sety_id "
			    + " left join areas a on a.area_id = d.area_id  "
			    + " WHERE d.prco_status = true and prco_creation_date > '2021-12-01' and prco_categorizacion in (2) "
			    + " and caci_code in ('B0990.01', 'B0990.01.01', 'B0990.02', 'B0990.02.01', 'B0990.09', 'B0990.09.01') "
			    + " and prco_registration_status" + condicionSql ;

		/// proyectos digitalizados
		condicionSql="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  d.enaa_ci_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(d.enaa_project_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(d.enaa_project_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(d.enaa_environmental_administrative_authorization) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(d.enaa_register_date) as varchar like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		if(idProyectoPrincipal!= null ){
			condicionSql +=  " and d.enaa_id <> "+idProyectoPrincipal ;
		}
		String sqlPproyectoDigitalizados = "SELECT count(*) "
				+ "FROM coa_digitalization_linkage.environmental_administrative_authorizations d "
			    + "left join suia_iii.sector_types st on st.sety_id = d.sety_id "
			    + " left join coa_mae.catalog_ciuu c on d.caci_id = c.caci_id  "
			    + " left join areas a on a.area_id = d.area_id_aaa  "
			    + " WHERE d.enaa_status = true  and d.enaa_environmental_administrative_authorization not in ('Certificado Ambiental') " + condicionSql ;
		
		List<Object> result = (List<Object>)crudServiceBean.getEntityManager().createNativeQuery
				(sqlProyectoSuia + " union " + sqlProyectoSuia2018 + " union " + sqlProyecto4Cat + " union " 
		+ sqlPproyectoSector + " union " + sqlPproyectoDigitalizados + " union " + sqlPproyectoRcoa)
	    		.getResultList();
		Integer total =0;
		for (Object object : result) {
			total += ((BigInteger) object).intValue();
		}
		return total;
	}
	
	public List<ProyectoAsociadoDigitalizacion> buscarProyectosAsociados(AutorizacionAdministrativa proyectoPrincipal){
		try {
			List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados = new ArrayList<ProyectoAsociadoDigitalizacion>();
			AutorizacionAdministrativaAmbiental autorizacion = new AutorizacionAdministrativaAmbiental();
			autorizacion = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(proyectoPrincipal.getCodigo());
			if(autorizacion != null && autorizacion.getId() != null){
				listaProyectosAsociados = proyectoAsociadoDigitalizacionFacade.buscarProyectosAsociados(autorizacion.getId());
			}
			for (ProyectoAsociadoDigitalizacion proyectoAsociadoDigitalizacion : listaProyectosAsociados) {
				AutorizacionAdministrativa aaa = new AutorizacionAdministrativa();
				if(proyectoAsociadoDigitalizacion.getSistemaOriginal() != null){
					AutorizacionAdministrativaAmbiental objAutorizacion = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(proyectoAsociadoDigitalizacion.getProyectoAsociadoId());
					if(objAutorizacion != null){
						aaa.setId(objAutorizacion.getIdProyecto());
						aaa.setFuente(objAutorizacion.getSistema().toString());
						if("0".equals(objAutorizacion.getSistema().toString()))
							aaa.setId(objAutorizacion.getId());
						aaa.setIdDigitalizacion(objAutorizacion.getId());
						aaa.setTipoProyecto("Proyecto Inclusión");
						aaa.setAreaEmisora(objAutorizacion.getAreaEmisora().getAreaAbbreviation());
						aaa.setCodigoDocumento(objAutorizacion.getResolucion());
						aaa.setCodigo(objAutorizacion.getCodigoProyecto());
						aaa.setNombre(objAutorizacion.getNombreProyecto());
						if(objAutorizacion.getFechaRegistro() != null)
							aaa.setFecha(objAutorizacion.getFechaRegistro().toString().substring(0, 10));
						if(objAutorizacion.getFechaFinalizacionRegistro()!= null)
							aaa.setFechaFin(objAutorizacion.getFechaFinalizacionRegistro().toString().substring(0, 10));
						if(proyectoAsociadoDigitalizacion.isEsActualizacionCI())
							aaa.setTipoProyecto("Actualización de Certificado de Intersección");
						if("0".equals(objAutorizacion.getSistema().toString()) || "1".equals(objAutorizacion.getSistema().toString())){
							if(objAutorizacion.getFechaInicioResolucion() != null)
								aaa.setFecha(objAutorizacion.getFechaInicioResolucion().toString().substring(0, 10));
							if(objAutorizacion.getFechaResolucion() != null)
								aaa.setFechaFin(objAutorizacion.getFechaResolucion().toString().substring(0, 10));
						}
					}else{
						aaa.setTipoProyecto("Actualización de Certificado de Intersección");
						aaa.setCodigo(proyectoAsociadoDigitalizacion.getCodigo());
						aaa.setFuente("CI");
					}
				}else{
					switch (proyectoAsociadoDigitalizacion.getTipoProyecto().toString()) {
					case "3":  //ART
						AprobacionRequisitosTecnicos art = aprobacionRequisitosTecnicosFacade.getAprobacionRequisitosTecnicosPorId(proyectoAsociadoDigitalizacion.getProyectoAsociadoId());
						if(art != null){
							aaa.setId(art.getId());
							aaa.setFuente("11");
							aaa.setTipoProyecto("ART Asociado");
							aaa.setAreaEmisora(art.getAreaResponsable().getAreaAbbreviation());
							aaa.setCodigoDocumento(art.getNombreProyecto());
							aaa.setCodigo(art.getSolicitud());
							// busco las fecha de uinicio y fin del proceso
							aaa = buscarFechasFin(aaa, "SUIA");
						}
						break;
					case "4":  //RGD
						switch (proyectoAsociadoDigitalizacion.getNombreTabla()) {
						case "suia_iii.hazardous_wastes_generators":
							GeneradorDesechosPeligrosos rgd = registroGeneradorDesechosFacade.get(proyectoAsociadoDigitalizacion.getProyectoAsociadoId());
							if(rgd != null){
								aaa.setId(rgd.getId());
								aaa.setNombre(rgd.getUsuario().getNombre());
								aaa.setFuente("10");
								aaa.setTipoProyecto("RGD Asociado");
								aaa.setAreaEmisora(rgd.getAreaResponsable().getAreaAbbreviation());
								aaa.setCodigoDocumento(rgd.getCodigo());
								aaa.setCodigo(rgd.getSolicitud());
								// busco las fecha de uinicio y fin del proceso
								aaa = buscarFechasFin(aaa, "SUIA");
							}
							break;
						case "coa_waste_generator_record.waste_generator_record_coa":
							RegistroGeneradorDesechosRcoa registroGeneradorDesechos = registroGeneradorFacade.findById(proyectoAsociadoDigitalizacion.getProyectoAsociadoId());
							if(registroGeneradorDesechos != null){
								aaa.setId(registroGeneradorDesechos.getId());
								aaa.setNombre(registroGeneradorDesechos.getUsuarioCreacion());
								aaa.setFuente("10");
								aaa.setTipoProyecto("RGD Asociado");
								if(registroGeneradorDesechos.getAreaResponsable() != null)
									aaa.setAreaEmisora(registroGeneradorDesechos.getAreaResponsable().getAreaAbbreviation());
								aaa.setCodigoDocumento(registroGeneradorDesechos.getCodigo());
								aaa.setCodigo(registroGeneradorDesechos.getCodigo());
								aaa.setIdDigitalizacion(proyectoAsociadoDigitalizacion.getAutorizacionAdministrativaAmbiental().getId());
								// busco las fecha de uinicio y fin del proceso
								aaa = buscarFechasFin(aaa, "SUIA");
							}
							break;

						default:
							break;
						}
						
						break;

					default:
						break;
					}
				}
				proyectoAsociadoDigitalizacion.setDatosProyectosAsociados(aaa);
			}
			return listaProyectosAsociados;
		} catch (Exception e) {
			return new ArrayList<ProyectoAsociadoDigitalizacion>();
		}
	}
	
	
	private AutorizacionAdministrativa buscarFechasFin(AutorizacionAdministrativa aaa, String sistema) throws JbpmException{
		Long procesoId = getProcessInstanceId(aaa);
		List<Object> object = procesosDigitalizacionFacade.getProcesoFechas("", procesoId, sistema);
		if(object != null && object.size() > 0){
			for (Object object2 : object) {
				Object[] array = (Object[]) object2;
				if(array[2] != null){
					Date fecha = (Date)array[2];
					aaa.setFecha(fecha.toString().substring(0, 10));
				}
				if(array[3] != null){
					Date fecha = (Date)array[3];
					aaa.setFechaFin(fecha.toString().substring(0, 10));
				}
			}
		}
		return aaa;
	}
	
	private Long getProcessInstanceId(AutorizacionAdministrativa proyectoAsociado) throws JbpmException{
		List<ProcessInstanceLog> listProcessProject = new ArrayList<ProcessInstanceLog>();
		List<String> listaProcesso = new ArrayList<String>();
		Usuario usuario = usuarioFacade.buscarUsuario(proyectoAsociado.getNombre());
		switch (proyectoAsociado.getFuente()) {
		case "4": // proyectos SUIA
			listaProcesso.add(Constantes.NOMBRE_PROCESO_CATEGORIA2V2);
			listaProcesso.add(Constantes.NOMBRE_PROCESO_CATEGORIA2);
			listaProcesso.add(Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL);
			listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(usuario, Constantes.ID_PROYECTO, proyectoAsociado.getId().toString());
			break;
		case "10":  // RGD
			listaProcesso.add(Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS);
			listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(usuario, "idRegistroGenerador", proyectoAsociado.getId().toString());
			if(listProcessProject == null || listProcessProject.size() == 0){
				// RGD
				listaProcesso.add(Constantes.RCOA_REGISTRO_GENERADOR_DESECHOS);
				listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(usuario, "idProyecto", proyectoAsociado.getCodigo());
			}
			break;
		case "11":  // ART
			listaProcesso.add(Constantes.NOMBRE_PROCESO_APROBACION_REQUISITOS_TECNICOS);
			listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(usuario, "numeroSolicitud", proyectoAsociado.getCodigo());
			break;

		default:
			break;
		}
		Long proceso = 0L;
		Collections.sort(listProcessProject, new Comparator<ProcessInstanceLog>() {
			@Override
			public int compare(ProcessInstanceLog o1, ProcessInstanceLog o2) {
				return new Long(o1.getProcessInstanceId()).compareTo(new Long(o2.getProcessInstanceId()));
			}
		});
		
		if (listProcessProject != null) {
			for (int j = 0; j < listProcessProject.size(); j++) {
				if (listProcessProject.get(j).getStatus() != 4) {
					String nombreproceso = listProcessProject.get(j).getProcessId();
					if(listaProcesso.contains(nombreproceso)){
						proceso = listProcessProject.get(j).getProcessInstanceId();
					}
				}
			}
		}
		return proceso;
	}

	//separacion métodos de consulta proyectos digitalizacion
	public Integer contarProyectosSectorSubSector(String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal){
		Integer total = 0;

		String condicionSql = "";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  \"CED/RUC PROPONENTE\" = ''"+documentoOperador+"''" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(\"CÓDIGO PROYECTO\") like ''%"+codigo.toLowerCase()+"%''" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(\"NOMBRE PROYECTO\") like ''%"+nombreProyecto.toLowerCase()+"%''" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(\"TIPO SECTOR\") like ''%"+sector.toLowerCase()+"%''" ;
		}
		if (fechaRegistro != null && !fechaRegistro.isEmpty()) {
			condicionSql += " and  cast(\"FECHA REGISTRO\" as varchar) like ''%" + fechaRegistro.toLowerCase() + "%''";
		}
		if (tipoProyecto != null && !tipoProyecto.isEmpty()) {
			return total;
		}
		
		String sqlProyecto4Cat="select count(*) from dblink('"+dblinkSectorSubsector+"',"
				+ "'select * "
				+ "from (SELECT distinct \"CÓDIGO PROYECTO\" "
				+ "FROM vm_sector_subsector_bi  "
				+ "where \"ESTADO PROYECTO\" = ''En trámite'' and \"TIPO PERMISO AMBIENTAL\" != ''Licencia Ambiental'' " 
				+ condicionSql + ") as t') as t1 "
				+ "(codigo character varying ) "
				+ "where true " + getSqlNotDigitalizacion("codigo", documentoOperador) ;

		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlProyecto4Cat);
		
		List<Object> resultList = (List<Object>) q.getResultList();
		
		if (resultList.size() > 0) {
			total = Integer.parseInt(resultList.get(0).toString());
		}
		
		return total;
		
	}


	public List<Object> getQueryProyectosSectorSubSector(Integer inicio, Integer total, String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal){

		List<Object> result = null;
		String condicionSql = "";
		
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  \"CED/RUC PROPONENTE\" = ''"+documentoOperador+"''" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(\"CÓDIGO PROYECTO\") like ''%"+codigo.toLowerCase()+"%''" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(\"NOMBRE PROYECTO\") like ''%"+nombreProyecto.toLowerCase()+"%''" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(\"TIPO SECTOR\") like ''%"+sector.toLowerCase()+"%''" ;
		}
		if (fechaRegistro != null && !fechaRegistro.isEmpty()) {
			condicionSql += " and  cast(\"FECHA REGISTRO\" as varchar) like ''%" + fechaRegistro.toLowerCase() + "%''";
		}
		if (tipoProyecto != null && !tipoProyecto.isEmpty()) {
			return result;
		}
		
		String sqlProyecto4Cat="select cast(id as integer), codigo, nombre, usuario, nombre_persona, organizacion, categoria, estrategico, "
				+ "tareas, 0 numTare4cat, cast(fecha as date), cast(null as date) fechafin, resumen, actividad, sector, cast(null as varchar) area_abbreviation, cast(null as varchar) numeroresolucion, cast(null as varchar) fecharesolucion, cast(null as boolean) digitalizado, cast(null as integer) iddigitalizacion,  cast(null as integer) idproceso,  cast('5' as varchar) tiposistema, cast(0 as integer) enaa_system "
				+ "from dblink('"+dblinkSectorSubsector+"',"
				+ "'select * "
				+ "from (SELECT distinct 0, \"CÓDIGO PROYECTO\", \"NOMBRE PROYECTO\", \"CED/RUC PROPONENTE\" , "
				+ "\"NOMBRE PROPONENTE\", null, null, \"ESTRATÉGICO\", 0, \"FECHA REGISTRO\", "
				+ " \"RESUMEN PROYECTO\", null, \"TIPO SECTOR\" "
				+ "FROM vm_sector_subsector_bi  "
				+ "where \"ESTADO PROYECTO\" = ''En trámite'' and \"TIPO PERMISO AMBIENTAL\" != ''Licencia Ambiental'' " 
				+ condicionSql + ") as t order by \"CÓDIGO PROYECTO\" LIMIT " + total + " OFFSET " + inicio + "') as t1 "
				+ "(id character varying(255), codigo character varying(255), nombre text, usuario character varying(255), "
				+ "nombre_persona character varying(255), organizacion character varying(255), "
				+ "categoria character varying(255), "
				+ "estrategico boolean, tareas integer, fecha character varying(255), resumen text, actividad text, sector text) "
				+ "where true " + getSqlNotDigitalizacion("codigo", documentoOperador) ;

		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlProyecto4Cat);
		
		result = q.getResultList();
		
		return result;
		
	}
	
	
	public Integer contarProyectosCuatroCategorias(String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal){
		Integer total = 0;

		String condicionSql = "";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  \"CED/RUC PROPONENTE\" = ''"+documentoOperador+"''" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(\"CÓDIGO PROYECTO\") like ''%"+codigo.toLowerCase()+"%''" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(\"NOMBRE PROYECTO\") like ''%"+nombreProyecto.toLowerCase()+"%''" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(\"TIPO SECTOR\") like ''%"+sector.toLowerCase()+"%''" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(\"CATEGORIA\") like ''%"+tipoProyecto.toLowerCase()+"%''" ;
		}
		if (fechaRegistro != null && !fechaRegistro.isEmpty()) {
			condicionSql += " and  cast(\"FECHA REGISTRO\" as varchar) like ''%" + fechaRegistro.toLowerCase() + "%''";
		}
		
		String sqlProyecto4Cat="select count(*) from dblink('"+dblinkSuiaVerde+"',"
				+ "'select * "
				+ "from (SELECT distinct \"CÓDIGO PROYECTO\" "
				+ "FROM vm_cuatro_categorias_bi vccb "
				+ "where \"ESTADO PROYECTO\" = ''En trámite'' and \"CATEGORIA\" != ''I'' " 
				+ condicionSql + ") as t') as t1 "
				+ "(codigo character varying ) "
				+ "where true " + getSqlNotDigitalizacion("codigo", documentoOperador) ;

		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlProyecto4Cat);
		
		List<Object> resultList = (List<Object>) q.getResultList();
		
		if (resultList.size() > 0) {
			total = Integer.parseInt(resultList.get(0).toString());
		}
		
		return total;
		
	}


	public List<Object> getQueryProyectosCuatroCategorias(Integer inicio, Integer total, String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal){

		List<Object> result = null;
		String condicionSql = "";
		
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  \"CED/RUC PROPONENTE\" = ''"+documentoOperador+"''" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(\"CÓDIGO PROYECTO\") like ''%"+codigo.toLowerCase()+"%''" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(\"NOMBRE PROYECTO\") like ''%"+nombreProyecto.toLowerCase()+"%''" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(\"TIPO SECTOR\") like ''%"+sector.toLowerCase()+"%''" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(\"CATEGORIA\") like ''%"+tipoProyecto.toLowerCase()+"%''" ;
		}
		if (fechaRegistro != null && !fechaRegistro.isEmpty()) {
			condicionSql += " and  cast(\"FECHA REGISTRO\" as varchar) like ''%" + fechaRegistro.toLowerCase() + "%''";
		}
		
		String sqlProyecto4Cat="select cast(id as integer) id, codigo, nombre, usuario, nombre_persona, organizacion, categoria, estrategico, tareas, tareas_4cat, cast(fecha as date) fecha_inicio, cast(null as date) fecha_fin, resumen, actividad, sector, cast(null as varchar) area_abbreviation, cast(null as varchar) numeroresolucion, cast(null as varchar) fecharesolucion, cast(null as boolean) digitalizado, cast(null as integer) iddigitalizacion,  cast(null as integer) idproceso,  cast('1' as varchar) tiposistema, cast(0 as integer) enaa_system "
				+ "from dblink('"+dblinkSuiaVerde+"',"
				+ "'select * "
				+ "from (SELECT distinct 0, \"CÓDIGO PROYECTO\", \"NOMBRE PROYECTO\", \"CED/RUC PROPONENTE\" , "
				+ "\"NOMBRE PROPONENTE\", null, \"CATEGORIA\", \"ESTRATÉGICO\", 0, \"TIPO SECTOR\", "
				+ " 0, \"RESUMEN PROYECTO\", \"ACTIVIDAD ECONÓMICA\", \"FECHA REGISTRO\" "
				+ "FROM vm_cuatro_categorias_bi vccb "
				+ "where \"ESTADO PROYECTO\" = ''En trámite'' and \"CATEGORIA\" != ''I'' " 
				+ condicionSql + ") as t order by \"CÓDIGO PROYECTO\" LIMIT " + total + " OFFSET " + inicio + " ') as t1 "
				+ "(id character varying(255), codigo character varying(255), nombre text, usuario character varying(255), "
				+ "nombre_persona character varying(255), organizacion character varying(255), categoria character varying(255), "
				+ "estrategico text, tareas integer, sector character varying(255), tareas_4cat integer, resumen text, "
				+ "actividad character varying(800), fecha character varying(255) ) "
				+ "where true " + getSqlNotDigitalizacion("codigo", documentoOperador) ;


		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlProyecto4Cat);
		
		result = q.getResultList();
		
		return result;
		
	}

	public Integer contarProyectosSuiaiiiAntes2018(String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal) {
		Integer total = 0;
		String condicionSql = "";

		/// proyectos suia anterior
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.pren_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.pren_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.pren_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql += " and lower(c.cate_public_name) like '%" + tipoProyecto.toLowerCase() + "%'";
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(y.fechaInicio as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}

		String sqlProyectoSuia = "SELECT count(distinct p.pren_id) "
				+ "FROM suia_iii.projects_environmental_licensing p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
				
			    + "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
			    + "inner join suia_iii.categories_catalog_system cs on cs.cacs_id = p.cacs_id "
			    + "inner join suia_iii.categories c on c.cate_id = cs.cate_id "
			    + "inner join suia_iii.categories_catalog_public cp on cs.cacp_id = cp.cacp_id "
			    + "inner join suia_iii.sector_types st on st.sety_id = cp.sety_id "
			    + "left join public.areas a on a.area_id = p.area_id "
			    + "left join (select max(case when cafa_license_number is null then cafa_office_number else cafa_license_number end) numeroresolucion, max(cast(cafa_date_update as varchar)) fecharesolucion, pren_id from suia_iii.catii_fapma where cafa_status group by pren_id) cf on p.pren_id = cf.pren_id "
			    + "join " + getQueryJbpmEnTramite("''Licencia Ambiental'', ''Registro ambiental v2'', ''Registro ambiental''") + " ON y.codigoproyecto = p.pren_code "
			    + " WHERE p.pren_status = true and pren_register_date <= '" + Constantes.getFechaBloqueoRegistroFisico() + "' "
				+ condicionSql
				+ getSqlNotDigitalizacion("p.pren_code", documentoOperador);

		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlProyectoSuia);
		
		List<Object> resultList = (List<Object>) q.getResultList();
		
		if (resultList.size() > 0) {
			total = Integer.parseInt(resultList.get(0).toString());
		}
		
		return total;
	}

	public List<Object> getProyectosSuiaiiiAntes2018(Integer inicio, Integer total, String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal) {
		List<Object> result = null;
		String condicionSql = "";

		/// proyectos suia anterior
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.pren_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.pren_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.pren_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql += " and lower(c.cate_public_name) like '%" + tipoProyecto.toLowerCase() + "%'";
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(y.fechaInicio as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}

		String sqlProyectoSuia = "SELECT distinct p.pren_id, p.pren_code as codigo, p.pren_name, p.pren_creator_user, l.peop_name, "
				+ "o.orga_name_organization, c.cate_code, cast(true as boolean) estado, 0 numtareas, 0 numtareas4cat, y.fechaInicio, y.fechafin, null resumen, null actividad, st.sety_name, a.area_abbreviation, "
				+ "cf.numeroresolucion, cf.fecharesolucion, "
				+ "cast(null as boolean) digitalizado, cast(null as integer) iddigitalizacion,  cast(null as integer) idproceso, cast('2' as varchar) tiposistema, cast(0 as integer) enaa_system "
				+ "FROM suia_iii.projects_environmental_licensing p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
			    + "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
			    + "inner join suia_iii.categories_catalog_system cs on cs.cacs_id = p.cacs_id "
			    + "inner join suia_iii.categories c on c.cate_id = cs.cate_id "
			    + "inner join suia_iii.categories_catalog_public cp on cs.cacp_id = cp.cacp_id "
			    + "inner join suia_iii.sector_types st on st.sety_id = cp.sety_id "
			    + "left join public.areas a on a.area_id = p.area_id "
			    + "left join (select max(case when cafa_license_number is null then cafa_office_number else cafa_license_number end) numeroresolucion, max(cast(cafa_date_update as varchar)) fecharesolucion, pren_id from suia_iii.catii_fapma where cafa_status group by pren_id) cf on p.pren_id = cf.pren_id "
			    + "join " + getQueryJbpmEnTramite("''Licencia Ambiental'', ''Registro ambiental v2'', ''Registro ambiental''") + " ON y.codigoproyecto = p.pren_code "
				+ " WHERE p.pren_status = true and pren_register_date <= '" + Constantes.getFechaBloqueoRegistroFisico() + "' "
				+ condicionSql
				+ getSqlNotDigitalizacion("p.pren_code", documentoOperador)
			    + " order by p.pren_code LIMIT " + total + " OFFSET " + inicio;

		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlProyectoSuia);
		
		result = q.getResultList();
		
		return result;
	}

	public String getQueryJbpmEnTramite(String procesos){
		String sqlProyecto="(select t1.codigoproyecto, t1.fechaInicio, t1.fechaFin  "
				+ "from dblink('"+dblinkSuiaBpm+"',"
				+ "'select distinct v.value codigoProyecto, "
				+ " p.start_date, p.end_date  "
				+ "from variableinstancelog v "
				+ "inner join processinstancelog p on p.processinstanceid = v.processinstanceid "
				+ "where  "
				+ "p.processname in (" + procesos + ") "
				+ "and (v.variableid=''tramite'' ) "
				+ "and user_identity not like ''%msit%'' "
				+ "and user_identity not like ''%admin%'' "
				+ "and user_identity not like ''%diana.pabon%'' "
				+ "and user_identity not IN ( ''1714419148'', ''1712876364'', ''1717679441'', ''1720283686'') "
				+ "and p.status in ( 1 ) order by 1') as t1 "
				+ "(codigoproyecto text, fechaInicio timestamp, fechafin timestamp) "
				+ "GROUP BY t1.codigoproyecto, t1.fechaInicio, t1.fechaFin "
				+ "ORDER BY t1.codigoproyecto) y ";
		
		return sqlProyecto;
	}

	public Integer contarProyectosSuiaiii2018(String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal) {
		//Consultas originales de digitalizacion Byron
		Integer total = 0;
		String condicionSql = "";
		/// proyectos suia anterior
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.pren_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.pren_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.pren_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql += " and lower(c.cate_public_name) like '%" + tipoProyecto.toLowerCase() + "%'";
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(y.fechaInicio as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		String sqlProyectoSuia2018 = "SELECT count(*) "
				+ "FROM suia_iii.projects_environmental_licensing p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
			    + "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
			    + "inner join suia_iii.categories_catalog_system cs on cs.cacs_id = p.cacs_id "
			    + "inner join suia_iii.categories c on c.cate_id = cs.cate_id "
			    + "inner join suia_iii.categories_catalog_public cp on cs.cacp_id = cp.cacp_id "
			    + "inner join suia_iii.sector_types st on st.sety_id = cp.sety_id "
			    + "left join public.areas a on a.area_id = p.area_id "
			    + "left join suia_iii.projects_blocks pb on pb.pren_id = p.pren_id and pb.prbl_status = true "
			    + "left join suia_iii.blocks b on b.bloc_id = pb.bloc_id "
			    + "left join suia_iii.mining_grants mg on mg.pren_id = p.pren_id and mg.migr_status = true "
			    + "left join coa_digitalization_linkage.v_proyecto_resolucion v on p.pren_id = v.pren_id "
			    + "join " + getQueryJbpm2018("''Licencia Ambiental''") + " ON y.codigoproyecto = p.pren_code "
			    + " WHERE p.pren_status = true and pren_register_date > '"+Constantes.getFechaBloqueoRegistroFisico()+"' " 
			    + condicionSql 
			    + getSqlNotDigitalizacion("p.pren_code", documentoOperador);

		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlProyectoSuia2018);
		
		List<Object> resultList = (List<Object>) q.getResultList();
		
		if (resultList.size() > 0) {
			total = Integer.parseInt(resultList.get(0).toString());
		}
		
		return total;
	}

	public List<Object> getProyectosSuiaiii2018(Integer inicio, Integer total, String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal) {
		//Consultas originales de digitalizacion Byron
		List<Object> result = null;
		String condicionSql = "";
		/// proyectos suia anterior
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  p.pren_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(p.pren_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(p.pren_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql += " and lower(c.cate_public_name) like '%" + tipoProyecto.toLowerCase() + "%'";
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(y.fechaInicio as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}

		String sqlProyectoSuia2018 = "SELECT distinct p.pren_id, p.pren_code, p.pren_name, p.pren_creator_user, l.peop_name, "
				+ "o.orga_name_organization, c.cate_code, cast(true as boolean) estado, 0 numtareas, 0 numtareas4cat, y.fechaInicio, y.fechafin, null resumen, null actividad, st.sety_name, a.area_abbreviation, v.numeroresolucion, cast(v.fecharesolucion as varchar) , "
				+ "cast(null as boolean) digitalizado, cast(null as integer) iddigitalizacion,  cast(null as integer) idproceso, cast('2' as varchar) tiposistema, cast(0 as integer) enaa_system "
				+ "FROM suia_iii.projects_environmental_licensing p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
			    + "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
			    + "inner join suia_iii.categories_catalog_system cs on cs.cacs_id = p.cacs_id "
			    + "inner join suia_iii.categories c on c.cate_id = cs.cate_id "
			    + "inner join suia_iii.categories_catalog_public cp on cs.cacp_id = cp.cacp_id "
			    + "inner join suia_iii.sector_types st on st.sety_id = cp.sety_id "
			    + "left join public.areas a on a.area_id = p.area_id "
			    + "left join suia_iii.projects_blocks pb on pb.pren_id = p.pren_id and pb.prbl_status = true "
			    + "left join suia_iii.blocks b on b.bloc_id = pb.bloc_id "
			    + "left join suia_iii.mining_grants mg on mg.pren_id = p.pren_id and mg.migr_status = true "
			    + "left join coa_digitalization_linkage.v_proyecto_resolucion v on p.pren_id = v.pren_id "
			    + "join " + getQueryJbpm2018("''Licencia Ambiental''") + " ON y.codigoproyecto = p.pren_code "
				+ " WHERE p.pren_status = true and pren_register_date > '" + Constantes.getFechaBloqueoRegistroFisico() + "' "
				+ condicionSql
				+ getSqlNotDigitalizacion("p.pren_code", documentoOperador)
				+ " order by p.pren_code LIMIT " + total + " OFFSET " + inicio;

		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlProyectoSuia2018);
		
		result = q.getResultList();
		
		return result;
	}

	public Integer contarProyectosRcoa(String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal)  {
		/// proyectos RCOA
		Integer total = 0;
		String condicionSql = "";

		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  d.prco_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(d.prco_cua) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(d.prco_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			//condicionSql+= " and  lower(d.enaa_environmental_administrative_authorization) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(d.prco_creation_date as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		String sqlProyectoRcoa = "SELECT count(*)  "
				+ "FROM coa_mae.project_licencing_coa d "
				+ "inner join coa_mae.project_licencing_coa_ciuu b on d.prco_id = b.prco_id and prli_status is true "
				+ "inner join public.users u on u.user_id = d.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
			    + " left join coa_mae.catalog_ciuu c on b.caci_id = c.caci_id and caci_status is true "
			    + "left join suia_iii.sector_types st on st.sety_id = c.sety_id "
			    + " left join areas a on a.area_id = d.area_id  "
			    + " WHERE d.prco_status = true and prco_creation_date > '2021-12-01' and prco_categorizacion in (2) "
			    + " and caci_code in ('B0990.01', 'B0990.01.01', 'B0990.02', 'B0990.02.01', 'B0990.09', 'B0990.09.01') "
			    + " and prco_registration_status" + condicionSql 
			    + getSqlNotDigitalizacion("d.prco_cua", documentoOperador);

		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlProyectoRcoa);
		
		List<Object> resultList = (List<Object>) q.getResultList();
		
		if (resultList.size() > 0) {
			total = Integer.parseInt(resultList.get(0).toString());
		}
		
		return total;
	}

	public List<Object> getProyectosRcoa(Integer inicio, Integer total, String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal)  {
		//Consultas originales de digitalizacion Byron
		List<Object> result = null;
		String condicionSql = "";

		/// proyectos RCOA
		condicionSql="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  d.prco_creator_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(d.prco_cua) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(d.prco_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			//condicionSql+= " and  lower(d.enaa_environmental_administrative_authorization) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and cast(d.prco_creation_date as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		String sqlProyectoRcoa = "SELECT d.prco_id, d.prco_cua, d.prco_name, d.prco_creator_user, l.peop_name, null, "
				+ " case d.prco_categorizacion when 2 then 'Registro Ambiental' else '' end, "
				+ " cast(false as boolean) estrategico, 0 tareas, 0 tareas_4cat, prco_creation_date fechaInicio, null fechafin, null summary, c.caci_name, st.sety_name, a.area_abbreviation, null resolution, null resolution_date, null estadof, null as idDigitalizacion , cast(null as integer) idproceso,  cast('7' as varchar) tiposistema, cast(0 as integer) enaa_system  "
				+ "FROM coa_mae.project_licencing_coa d "
				+ "inner join coa_mae.project_licencing_coa_ciuu b on d.prco_id = b.prco_id and prli_status is true "
				+ "inner join public.users u on u.user_id = d.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
			    + " left join coa_mae.catalog_ciuu c on b.caci_id = c.caci_id and caci_status is true "
			    + "left join suia_iii.sector_types st on st.sety_id = c.sety_id "
			    + " left join areas a on a.area_id = d.area_id  "
			    + " WHERE d.prco_status = true and prco_creation_date > '2021-12-01' and prco_categorizacion in (2) "
			    + " and caci_code in ('B0990.01', 'B0990.01.01', 'B0990.02', 'B0990.02.01', 'B0990.09', 'B0990.09.01') "
			    + " and prco_registration_status" + condicionSql 
			    + getSqlNotDigitalizacion("d.prco_cua", documentoOperador)
			    + " order by d.prco_cua LIMIT " + total + " OFFSET " + inicio;

		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlProyectoRcoa);
		
		result = q.getResultList();
		
		return result;
	}

	public Integer contarProyectosDigitalizados(String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal)  {
		/// proyectos digitalizados
		Integer total = 0;
		String condicionSql = "";

		condicionSql="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  d.enaa_ci_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(d.enaa_project_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(d.enaa_project_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(d.enaa_environmental_administrative_authorization) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(d.enaa_register_date as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		if(idProyectoPrincipal!= null ){
			condicionSql +=  " and d.enaa_id <> "+idProyectoPrincipal ;
		}
		String sqlPproyectoDigitalizados = "SELECT count(*) "
				+ "FROM coa_digitalization_linkage.environmental_administrative_authorizations d "
			    + "left join suia_iii.sector_types st on st.sety_id = d.sety_id "
			    + " left join coa_mae.catalog_ciuu c on d.caci_id = c.caci_id  "
			    + " left join areas a on a.area_id = d.area_id_aaa  "
			    + " WHERE d.enaa_status = true  and d.enaa_environmental_administrative_authorization not in ('Certificado Ambiental') " + condicionSql ;
		
		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyectoDigitalizados);
		
		List<Object> resultList = (List<Object>) q.getResultList();
		
		if (resultList.size() > 0) {
			total = Integer.parseInt(resultList.get(0).toString());
		}
		
		return total;
	}

	public List<Object> getProyectosDigitalizados(Integer inicio, Integer total, String documentoOperador, String codigo, String nombreProyecto, String sector, String tipoProyecto, String fechaRegistro, Integer idProyectoPrincipal)  {
		//Consultas originales de digitalizacion Byron
		List<Object> result = null;
		String condicionSql = "";

		/// proyectos digitalizados
		condicionSql="";
		if(documentoOperador!= null && !documentoOperador.isEmpty()){
			condicionSql+= " and  d.enaa_ci_user = '"+documentoOperador+"'" ;
		}
		if(codigo!= null && !codigo.isEmpty()){
			condicionSql+= " and  lower(d.enaa_project_code) like '%"+codigo.toLowerCase()+"%'" ;
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= " and  lower(d.enaa_project_name) like '%"+nombreProyecto.toLowerCase()+"%'" ;
		}
		if(sector!= null && !sector.isEmpty()){
			condicionSql+= " and  lower(st.sety_name) like '%"+sector.toLowerCase()+"%'" ;
		}
		if(tipoProyecto!= null && !tipoProyecto.isEmpty()){
			condicionSql+= " and  lower(d.enaa_environmental_administrative_authorization) like '%"+tipoProyecto.toLowerCase()+"%'" ;
		}
		if(fechaRegistro!= null && !fechaRegistro.isEmpty()){
			condicionSql+= " and  cast(d.enaa_register_date as varchar) like '%"+fechaRegistro.toLowerCase()+"%'" ;
		}
		if(idProyectoPrincipal!= null ){
			condicionSql +=  " and d.enaa_id <> "+idProyectoPrincipal ;
		}
		String sqlPproyectoDigitalizados = "SELECT distinct d.enaa_id, d.enaa_project_code, d.enaa_project_name, d.enaa_ci_user, d.enaa_name_user, null, "
				+ " d.enaa_environmental_administrative_authorization, cast(false as boolean) estrategico, 0 tareas, 0 tareas_4cat, d.enaa_register_date, d.enaa_register_finalized_date, d.enaa_project_summary, c.caci_name, st.sety_name, a.area_abbreviation, d.enaa_resolution, cast(d.enaa_resolution_date as varchar) enaa_resolution_date, d.enaa_status_finalized, d.enaa_id as idDigitalizacion , d.enaa_process_instance_id,  cast('6' as varchar) tiposistema, d.enaa_system "
				+ "FROM coa_digitalization_linkage.environmental_administrative_authorizations d "
			    + "left join suia_iii.sector_types st on st.sety_id = d.sety_id "
			    + " left join coa_mae.catalog_ciuu c on d.caci_id = c.caci_id  "
			    + " left join areas a on a.area_id = d.area_id_aaa  "
			    + " WHERE d.enaa_status = true and d.enaa_environmental_administrative_authorization not in ('Certificado Ambiental') " + condicionSql 
			    + " order by d.enaa_project_code LIMIT " + total + " OFFSET " + inicio;

		Query q = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyectoDigitalizados);
		
		result = q.getResultList();
		
		return result;
		
	}
}
