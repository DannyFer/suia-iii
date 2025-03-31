package ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.dto.PermisoAmbiental;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class PermisoAmbientalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private DocumentosFacade documentosFacade;
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public String dblinkSuiaHidro=Constantes.getDblinkBpmsHyD();
	
	public String dblinkSectorSubsector=Constantes.getDblinkSectorSubsector();
	
	
	public String getQuery4Categorias(String camposRetorno, String proyecto4cat, String nombreProyecto, String cedulaProponente, String nombreProponente){
		String condicionSql = "";
		if(proyecto4cat!= null && !proyecto4cat.isEmpty()){
			condicionSql+= " p.id like ''%"+proyecto4cat+"%''";
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= (condicionSql == "") ? " p.nombre like ''%"+nombreProyecto+"%''" : " and p.nombre= ''%"+nombreProyecto+"%''";
		}
		if(cedulaProponente!= null && !cedulaProponente.isEmpty()){
			condicionSql+= (condicionSql == "") ? " p.usuarioldap like ''%"+cedulaProponente+"''" : " and p.usuarioldap= ''"+cedulaProponente+"%''";
		}
		if(nombreProponente!= null && !nombreProponente.isEmpty()){
			condicionSql+= (condicionSql == "") ? " (r.nombresapellidos like ''%"+nombreProponente+"%'' or o.nombre like ''%"+nombreProponente+"%'')" : " and (r.nombresapellidos like ''%"+nombreProponente+"%'' or o.nombre like ''%"+nombreProponente+"%'')";
		}
		

		String sqlProyecto="select " +camposRetorno+ " from dblink('"+dblinkSuiaVerde+"',"
				+ "'select distinct null, p.id, p.nombre, p.usuarioldap, r.nombresapellidos, o.nombre, p.motivoeliminar, "
				+ "p.estadoproyecto, cc.ca_categoria, p.fecharegistro, "
				+ "t1.nombre || COALESCE ('' '' || t2.ente::TEXT, '''') as ente, fechaestadoproyecto, t1.nombre, cc.estrategico  "
				+ "from proyectolicenciaambiental p  "
				+ "inner join catalogo_categoria cc on cc.id_catalogo = p.id_catalogo "
				+ "left join persona r on r.pin = p.usuarioldap "
				+ "left join organizacion o on o.pin = p.usuarioldap "
				+ "left join (SELECT ubicacion (MAX (ubicacion.parroquia), 1) AS nombre, ubicacion.proyecto_id "
				+ "FROM ubicacion GROUP BY ubicacion.proyecto_id) as t1 on t1.proyecto_id = p.id  "
				+ "left join (SELECT proyecto, ''ENTE ECREDITADO'' AS ente "
				+ "from variable where key::TEXT = ''enteacreditado'' and stringvalue::TEXT = ''SI'') as t2 on t2.proyecto = p.id "
				+ "where " + condicionSql 
				+ " and (estadoproyecto = true or (estadoproyecto = false and motivoeliminar is not null))') as t1 "
				+ "(id character varying(255), codigo character varying(255), nombre text, usuario character varying(255), "
				+ "nombre_persona character varying(255), organizacion character varying(255), motivo text, "
				+ "estado BOOLEAN, categoria character varying(255), fecha_registro date, "
				+ "ente_responsable character varying(255), fecha_archivacion date, provincia character varying(255), "
				+ "estrategico boolean)";
		
		return sqlProyecto;
	}
	
	@SuppressWarnings("unchecked")
	public List<PermisoAmbiental> getProyectos4cat(String proyecto4cat, String nombreProyecto, String cedulaProponente, String nombreProponente){
		String sqlPproyecto = getQuery4Categorias("*", proyecto4cat, nombreProyecto, cedulaProponente, nombreProponente);
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		List<Object> result = queryProyecto.getResultList();
		if(result.size()>0){
			List<PermisoAmbiental> projects = new ArrayList<PermisoAmbiental>();
			for (Object object : result) {
				projects.add(new PermisoAmbiental((Object[]) object, "1"));
			}
			
			return projects;
		}
		return null;
	}
	
	public List<PermisoAmbiental> getRegistrosAmbientales(String codigo, String nombre, String cedulaProponente, String nombreProponente) {
		try {	
			String condicionSql = "";
			if(codigo!= null && !codigo.isEmpty()){
				condicionSql+= " p.pren_code like '%"+codigo+"%'";
			}
			if(nombre!= null && !nombre.isEmpty()){
				condicionSql+= (condicionSql == "") ? " p.pren_name like '%"+nombre+"%'" : " and p.pren_name like '%"+nombre+"%'";
			}
			if(cedulaProponente!= null && !cedulaProponente.isEmpty()){
				condicionSql+= (condicionSql == "") ? " p.pren_creator_user like '%"+cedulaProponente+"%'" : " and p.pren_creator_user like '%"+cedulaProponente+"%'";
			}
			if(nombreProponente!= null && !nombreProponente.isEmpty()){
				condicionSql+= (condicionSql == "") ? " (l.peop_name like '%"+nombreProponente+"%' or o.orga_name_organization like '%"+nombreProponente+"%')" : " and (l.peop_name like '%"+nombreProponente+"%' or o.orga_name_organization like '%"+nombreProponente+"%')";
			}

			String query4Categorias = getQuery4Categorias("codigo", codigo, nombre, cedulaProponente, nombreProponente);
			String queryString = "SELECT distinct p.pren_id, p.pren_code, p.pren_name, p.pren_creator_user, l.peop_name, "
					+ "o.orga_name_organization, p.pren_delete_reason, p.pren_status, c.cate_code, pren_register_date, a.area_name "
					+ "FROM suia_iii.projects_environmental_licensing p "
					+ "inner join public.users u on u.user_id = p.user_id "
					+ "left join public.people l on l.peop_id = u.peop_id "
				    + "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
				    + "inner join suia_iii.categories_catalog_system cs on cs.cacs_id = p.cacs_id "
				    + "inner join suia_iii.categories c on c.cate_id = cs.cate_id "
				    + "left join public.areas a on a.area_id = p.area_id "
					+ " WHERE" + condicionSql 
					+ " and ((p.pren_status = true and p.pren_project_finalized = true) "
					+ " or (p.pren_status = false and p.pren_delete_reason is not null)) "
					+ " and p.pren_code not in ("+query4Categorias+")";

			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if(result.size() > 0){				
				List<PermisoAmbiental> projects = new ArrayList<PermisoAmbiental>();
				for (Object object : result) {
					projects.add(new PermisoAmbiental((Object[]) object, "2"));
				}
				
				return projects;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<PermisoAmbiental> getProyectosSectorSubsector(String proyectoSector, String nombreProyecto, String cedulaProponente, String nombreProponente){
		String condicionSql = "";
		if(proyectoSector!= null && !proyectoSector.isEmpty()){
			condicionSql+= " p.id like ''%"+proyectoSector+"%''";
		}
		if(nombreProyecto!= null && !nombreProyecto.isEmpty()){
			condicionSql+= (condicionSql == "") ? " p.nombre like ''%"+nombreProyecto+"%''" : " and p.nombre like ''%"+nombreProyecto+"%''";
		}
		if(cedulaProponente!= null && !cedulaProponente.isEmpty()){
			condicionSql+= (condicionSql == "") ? " p.usuarioldap like ''%"+cedulaProponente+"%''" : " and p.usuarioldap like ''%"+cedulaProponente+"%''";
		}
		if(nombreProponente!= null && !nombreProponente.isEmpty()){
			condicionSql+= (condicionSql == "") ? " (TRIM(primerapellido || '' '' || segundoapellido || '' '' || primernombre || '' '' || segundonombre) like ''%"+nombreProponente+"%'' or o.nombre like ''%"+nombreProponente+"%'')" : " and (TRIM(primerapellido || '' '' || segundoapellido || '' '' || primernombre || '' '' || segundonombre) like ''%"+nombreProponente+"%'' or o.nombre like ''%"+nombreProponente+"%'')";
		}
		

		String sqlPproyecto="select * from dblink('"+dblinkSectorSubsector+"',"
				+ "'select distinct null, p.id, p.nombre, p.usuarioldap, "
				+ "TRIM(primerapellido || '' '' || segundoapellido || '' '' || primernombre || '' '' || segundonombre)  AS nombresapellidos, "
				+ "o.nombre, p.motivoeliminar, p.estadoproyecto, "
				+ "(SELECT stringvalue FROM public.variable where executionid like ''%''||p.id||''%'' "
				+ "and (nombre = ''Categorización'' or nombre =  ''Tipo de proyecto'') order by 1 desc limit 1), "
				+ "p.fecharegistro, t1.nombre || COALESCE ('' '' || t2.ente::TEXT, '''') as ente, fechaestadoproyecto, "
				+ "t1.nombre, s.estrategico  "
				+ "from proyectolicenciaambiental p  "
				+ "left join persona r on r.pin = p.usuarioldap "
				+ "left join organizacion o on o.pin = p.usuarioldap "
				+ "left join subsector s on s.id = p.subsector "
				+ "left join (SELECT ubicacion (MAX (ubicacion.parroquia), 1) AS nombre, ubicacion.proyecto_id "
				+ "FROM ubicacion GROUP BY ubicacion.proyecto_id) as t1 on t1.proyecto_id = p.id  "
				+ "left join (SELECT proyecto, ''ENTE ECREDITADO'' AS ente "
				+ "from variable where key::TEXT = ''enteacreditado'' and stringvalue::TEXT = ''SI'') as t2 on t2.proyecto = p.id "
				+ "where " + condicionSql 
				+ " and (estadoproyecto = true or (estadoproyecto = false and motivoeliminar is not null))') as t1 "
				+ "(id character varying(255), codigo character varying(255), nombre text, usuario character varying(255), "
				+ "nombre_persona character varying(255), organizacion character varying(255), motivo text, "
				+ "estado BOOLEAN, categoria character varying(255), fecha_registro date, "
				+ "ente_responsable character varying(255), fecha_archivacion date, provincia character varying(255), "
				+ "estrategico boolean)";
		
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		List<Object> result = queryProyecto.getResultList();
		if(result.size()>0){
			List<PermisoAmbiental> projects = new ArrayList<PermisoAmbiental>();
			for (Object object : result) {
				projects.add(new PermisoAmbiental((Object[]) object, "5"));
			}
			
			return projects;
		}
		return null;
	}
	
	public List<PermisoAmbiental> getProyectosAprobacionRequisitosTecnicos(String codigo,
			String nombre, String cedulaProponente, String nombreProponente) {

		String condicionSql = "";
		if (codigo != null && !codigo.isEmpty()) {
			condicionSql += " p.apte_request like '%" + codigo + "%'";
		}
		if (nombre != null && !nombre.isEmpty()) {
			condicionSql += (condicionSql == "") ? " p.apte_name_proyect like '%" + nombre + "%'" : " and p.apte_name_proyect like '%" + nombre + "%'";
		}
		if (cedulaProponente != null && !cedulaProponente.isEmpty()) {
			condicionSql += (condicionSql == "") ? " p.apte_creator_user like '%"+ cedulaProponente + "%'" : 
				" and p.apte_creator_user like '%" + cedulaProponente + "%'";
		}
		if (nombreProponente != null && !nombreProponente.isEmpty()) {
			condicionSql += (condicionSql == "") ? " (l.peop_name like '%" + nombreProponente + "%' or o.orga_name_organization like '%"
					+ nombreProponente + "%')" : " and (l.peop_name like '%" + nombreProponente + "%' or o.orga_name_organization like '%"+ nombreProponente + "%')";
		}

		String queryString = "SELECT p.apte_id, p.apte_request, p.apte_name_proyect, p.apte_creator_user, "
				+ "l.peop_name, o.orga_name_organization, apte_delete_reason, apte_status, "
				+ "CASE WHEN apte_started_of_necessity = false THEN apte_proyect ELSE null END, apte_date, a.area_name "
				+ "FROM suia_iii.approval_technical_requirements p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
				+ "left join public.organizations o on o.peop_id = l.peop_id  and o.orga_ruc = u.user_name  "
				+ "left join public.areas a on a.area_id = p.area_id "
				+ "where " + condicionSql 
				+ " and apte_status = true";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
		if (result.size() > 0) {
			List<PermisoAmbiental> projects = new ArrayList<PermisoAmbiental>();
			for (Object object : result) {
				projects.add(new PermisoAmbiental((Object[]) object, "3"));
			}
			
			return projects;
		}

		return null;
	}
	
	public List<PermisoAmbiental> getProyectosGeneradorDesechosPeligrosos(String codigo,
			String cedulaProponente, String nombreProponente) {

		String condicionSql = "";
		if (codigo != null && !codigo.isEmpty()) {
			condicionSql += " p.hwge_request like '%" + codigo + "%'";
		}
		if (cedulaProponente != null && !cedulaProponente.isEmpty()) {
			condicionSql += (condicionSql == "") ? " p.hwge_creator_user like '%"+ cedulaProponente + "%'" : 
				" and p.hwge_creator_user like '%" + cedulaProponente + "%'";
		}
		if (nombreProponente != null && !nombreProponente.isEmpty()) {
			condicionSql += (condicionSql == "") ? " (l.peop_name like '%" + nombreProponente + "%' or o.orga_name_organization like '%"
					+ nombreProponente + "%')" : " and (l.peop_name like '%" + nombreProponente + "%' or o.orga_name_organization like '%"+ nombreProponente + "%')";
		}
		//id, codigo, nombre_pro, usuario, nombre_per, orga, motivo_delete, stado, categoria
		String queryString = "SELECT p.hwge_id, p.hwge_request, p.hwge_creator_user, l.peop_name, o.orga_name_organization, "
				+ "hwge_delete_reason, hwge_status, p.pren_id, hwge_creation_date, a.area_name "
				+ "FROM suia_iii.hazardous_wastes_generators p "
				+ "inner join public.users u on u.user_id = p.user_id "
				+ "left join public.people l on l.peop_id = u.peop_id "
				+ "left join public.organizations o on o.peop_id = l.peop_id  and o.orga_ruc = u.user_name  "
				+ "left join public.areas a on a.area_id = p.area_id "
				+ "where " + condicionSql 
				+ " and hwge_status = true";

		if(condicionSql != "") {
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				List<PermisoAmbiental> projects = new ArrayList<PermisoAmbiental>();
				for (Object object : result) {
					projects.add(new PermisoAmbiental((Object[]) object));
				}
				
				return projects;
			}
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getInstanceId4cat (String proyecto4cat){
		String sqltaskbpmhyd="select * from dblink('"+dblinkSuiaHidro+"',"
                + "'select processinstanceid from variableinstancelog where value=''"+proyecto4cat+"'' "
                + "') as (id integer)";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
		List<Object>  resultPro = queryProceso.getResultList();
		
		if(resultPro.size()>0){
    		return resultPro;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getTaskIdHidrocarburos(String proyecto4cat){
		String sqltaskbpmhyd="select * from dblink('"+dblinkSuiaHidro+"',"
                + "'select id from task where processinstanceid "
                + "in(select processinstanceid from variableinstancelog where value=''"+proyecto4cat+"'') "
                + "and status in(''InProgress'',''Ready'',''Reserved'')') as (id integer)";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
		List<Object>  resultPro = queryProceso.getResultList();
		
		if(resultPro.size()>0){
    		return resultPro;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getTaskId4cat (String proyecto4cat, String fuente){
		String dblink = (fuente.equals("1")) ? dblinkSuiaVerde : dblinkSectorSubsector;
		
		String sqltaskbpmhyd="select * from dblink('"+dblink+"',"
                + "'select dbid_, execution_, state_ from jbpm4_task where execution_id_ like ''%"+proyecto4cat+"%'' ') "
                		+ "as (id integer, flujo character varying(255), estado character varying(255))";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
		List<Object[]>  resultPro = queryProceso.getResultList();
		
		if(resultPro.size()>0){
    		return resultPro;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getHidrocarburosFinalizados (String proyecto){
		String sqltaskbpmhyd="select * from dblink('"+dblinkSuiaHidro+"',"
                + "'select id from processinstancelog where processinstanceid in "
                + "(select processinstanceid from variableinstancelog where value =''"+proyecto+"''"
                		+ " and processid in(''Hydrocarbons.pagoLicencia'',''Hydrocarbons.pagoLicenciaEnte'')) "
                		+ "and status=2') as (id integer)";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
		List<Object>  resultPro = queryProceso.getResultList();
		
		if(resultPro.size()>0){
    		return resultPro;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getDocumentosProyecto4cat (String proyecto4cat, String tiposDocumentos, String fuente){
		String dblink = (fuente.equals("1")) ? dblinkSuiaVerde : dblinkSectorSubsector;
		
		String sqlPproyecto = "";
		if(fuente.equals("1")) {
			sqlPproyecto="select * from dblink('"+dblink+"',"
					+ "'select d.id, d.nombre, d.fecha::date, d.key, d.urlalfresco, d.numero_actualizacion, d.extension "
					+ "from documento d where d.proyecto=''"+proyecto4cat+"'' and key in ("+tiposDocumentos+")') "
							+ "as t1 (id integer, nombre character varying(255), fecha date, clave character varying(255), "
							+ "urlalfresco character varying(255), nroactualizacion integer,  extension character varying(255))";	
		} else {
			sqlPproyecto="select * from dblink('"+dblink+"',"
					+ "'select d.id, d.nombre, d.fecha::date, d.key, d.urlalfresco, 0, d.extension "
					+ "from documento d where d.proyecto=''"+proyecto4cat+"'' and key in ("+tiposDocumentos+")') "
							+ "as t1 (id integer, nombre character varying(255), fecha date, clave character varying(255), "
							+ "urlalfresco character varying(255), nroactualizacion integer,  extension character varying(255))";	
		}
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		List<Object[]>  resultPro = queryProyecto.getResultList();
		
		if(resultPro.size()>0){
			Map<String, Object[]> documentosFiltrados = new HashMap<String, Object[]>();
			for (int i = 0; i < resultPro.size(); i++) {
				Object[] doc = resultPro.get(i);
				if (!documentosFiltrados.containsKey(doc[3]))
	                documentosFiltrados.put(doc[3].toString(), doc);
	            else {
	                Integer maxId = Integer.valueOf(documentosFiltrados.get(doc[3])[0].toString());
	                if (Integer.valueOf(doc[0].toString()) > maxId) {
	                    documentosFiltrados.put(doc[3].toString(), doc);
	                }
	            }
			}
			
			List<Object[]> documentosResult = new ArrayList<Object[]>();
	        for (String clave : documentosFiltrados.keySet()) {
	        	Object[] doc = documentosFiltrados.get(clave);
	                    documentosResult.add(doc);
	               
	        }
    		return documentosResult;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getDocumentosProyectoHidrocarburos (String proyecto, String tiposDocumentos){
		String sqlPproyecto="select * from dblink('"+dblinkSuiaVerde+"',"
				+ "'select docu_id, docu_date_add, docu_type, docu_process_name || ''-'' || docu_name || ''.'' || docu_extension as nombre_doc, docu_name "
				+ "from suia_licensing.documents d where d.docu_project_id=''"+proyecto+"'' and docu_type in ("+tiposDocumentos+")') "
						+ "as t1 (id integer, fecha date, tipo integer, nombre_alfresco text, nombre character varying(255))";		
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		List<Object[]>  resultPro = queryProyecto.getResultList();
		
		if(resultPro.size()>0){
			Map<String, Object[]> documentosFiltrados = new HashMap<String, Object[]>();
			for (int i = 0; i < resultPro.size(); i++) {
				Object[] doc = resultPro.get(i);
				if (!documentosFiltrados.containsKey(doc[2]))
	                documentosFiltrados.put(doc[2].toString(), doc);
	            else {
	                Integer maxId = Integer.valueOf(documentosFiltrados.get(doc[2])[0].toString());
	                if (Integer.valueOf(doc[0].toString()) > maxId) {
	                    documentosFiltrados.put(doc[2].toString(), doc);
	                }
	            }
			}
			
			List<Object[]> documentosResult = new ArrayList<Object[]>();
	        for (String clave : documentosFiltrados.keySet()) {
	        	Object[] doc = documentosFiltrados.get(clave);
	                    documentosResult.add(doc);
	               
	        }
    		return documentosResult;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getFlujos4cat (String proyecto4cat, String fuente){
		String dblink = (fuente.equals("1")) ? dblinkSuiaVerde : dblinkSectorSubsector;
		
		String sqltaskbpmhyd="select * from dblink('"+dblink+"',"
                + "'select dbid_, execution_, state_ from jbpm4_hist_task where execution_ like ''%"+proyecto4cat+"%'' order by 1 ') "
                		+ "as (id integer, flujo character varying(255), estado character varying(255))";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
		List<Object[]>  resultPro = queryProceso.getResultList();
		
		if(resultPro.size()>0){
    		return resultPro;
		}
		return null;
	}
	
	public PlantillaReporte obtenerPlantillaReporte(Integer tipoDocumentoId) {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("p_tipoDocumentoId", tipoDocumentoId);

			List<PlantillaReporte> lista = this.crudServiceBean.findByNamedQueryGeneric(
					PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (PlantillaReporte) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public byte[] getFirmaEnteResponsable(String firma) {
		try {
			return documentosFacade.descargarDocumentoPorNombre(firma);
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getHistorialTareas4cat (String procesoProyecto4cat, String fuente){
		String dblink = (fuente.equals("1")) ? dblinkSuiaVerde : dblinkSectorSubsector;
		
		String sqltaskbpmhyd="select * from dblink('"+dblink+"',"
		                + "'select activity_name_, transition_, start_, end_, htask_ from jbpm4_hist_actinst "
		                + "where execution_ = ''"+procesoProyecto4cat+"'' "
                		+ "order by start_ desc') "
                		+ "as (tarea character varying(255), valor character varying(255), fecha_inicio date, fecha_fin date, id_task int)";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
		List<Object[]>  resultPro = queryProceso.getResultList();
		
		if(resultPro.size()>0){
    		return resultPro;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getInterseccion4cat (String proyecto4cat, String fuente){
		String dblink = (fuente.equals("1")) ? dblinkSuiaVerde : dblinkSectorSubsector;
		
		String sqltaskbpmhyd="select * from dblink('"+dblink+"',"
		                + "'select id, stringvalue, key from variable "
		                + "where proyecto=''"+proyecto4cat+"'' and nombre = ''¿Intersecta?'' "
                		+ "order by 1 desc') "
                		+ "as (id_task int, valor character varying(255), clave character varying(255))";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
		List<Object[]>  resultPro = queryProceso.getResultList();
		
		if(resultPro.size()>0){
    		return resultPro;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getEnteResponsable4cat (String provincia, Boolean isEstrategico, String fuente){
		String dblink = (fuente.equals("1")) ? dblinkSuiaVerde : dblinkSectorSubsector;
		
		String condicionSql = "";
		if(isEstrategico){
			condicionSql+= "  r.nombre LIKE ''AUTORIDAD AMBIENTAL MAE'' ";
		} else {
			condicionSql+= "  r.nombre LIKE ''AUTORIDAD AMBIENTAL'' and pr.nombre = ''"+provincia+"''  ";
		}
		
		String sqltaskbpmhyd="select * from dblink('"+dblink+"',"
		                + "'select u.nombreusuario, p.primernombre, p.primerapellido, r.nombre, a.nombre, a.abreviacion from rolusuario ru "
		                + "inner join rol r on ru.rolid=r.id "
		                + "inner join usuario u on ru.usuarioid=u.id "
		                + "inner join persona p on u.entidad_id=p.id "
		                + "inner join parroquia pa on u.parroquia=pa.id "
		                + "inner join canton c on c.id=pa.canton "
		                + "inner join provincia pr on pr.id=c.provincia "
		                + "inner join area a on a.id = u.area "
		                + "where " + condicionSql
                		+ " order by 1 desc') "
                		+ "as (usuario character varying(255), nombre character varying(255), apellido character varying(255), "
                		+ "rol character varying(255), area character varying(255), abreviacion character varying(255))";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
		List<Object[]>  resultPro = queryProceso.getResultList();
		
		if(resultPro.size()>0){
    		return resultPro;
		}
		return null;
	}
	
	public List<PermisoAmbiental> getProyectoSuiaPorcodigo(String codigo) {
		try {	
			String condicionSql = "";
			if(codigo!= null && !codigo.isEmpty()){
				condicionSql+= " p.pren_code like '%"+codigo+"%'";
			}
			
			String queryString = "SELECT distinct p.pren_id, p.pren_code, p.pren_name, p.pren_creator_user, l.peop_name, "
					+ "o.orga_name_organization, p.pren_delete_reason, p.pren_status, c.cate_code, pren_register_date, a.area_name "
					+ "FROM suia_iii.projects_environmental_licensing p "
					+ "inner join public.users u on u.user_id = p.user_id "
					+ "left join public.people l on l.peop_id = u.peop_id "
				    + "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
				    + "inner join suia_iii.categories_catalog_system cs on cs.cacs_id = p.cacs_id "
				    + "inner join suia_iii.categories c on c.cate_id = cs.cate_id "
				    + "left join public.areas a on a.area_id = p.area_id "
					+ " WHERE" + condicionSql 
					+ " ";

			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if(result.size() > 0){				
				List<PermisoAmbiental> projects = new ArrayList<PermisoAmbiental>();
				for (Object object : result) {
					projects.add(new PermisoAmbiental((Object[]) object, "2"));
				}
				
				return projects;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<PermisoAmbiental> getProyectoRcoaPorCodigo(String codigo) {
		try {	
			String queryString = "SELECT distinct p.prco_id, p.prco_cua, p.prco_name, p.prco_creator_user, l.peop_name, "
					+ "o.orga_name_organization, p.prco_delete_reason, p.prco_status, ''||p.prco_categorizacion||'', prco_cua_date, a.area_name "
					+ "FROM coa_mae.project_licencing_coa p  "
					+ "inner join public.users u on u.user_id = p.user_id "
					+ "left join public.people l on l.peop_id = u.peop_id "
					+ "left join public.organizations o on o.peop_id = l.peop_id and o.orga_ruc = u.user_name "
					+ "left join public.areas a on a.area_id = p.area_id  "
					+ "WHERE p.prco_status = true and prco_cua = '"+codigo+"'";

			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if(result.size() > 0){				
				List<PermisoAmbiental> projects = new ArrayList<PermisoAmbiental>();
				for (Object object : result) {
					projects.add(new PermisoAmbiental((Object[]) object, "2"));
				}
				
				return projects;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
