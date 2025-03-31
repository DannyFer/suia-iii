package ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;

import ec.fugu.ambiente.consultoring.projects.ProyectoLicIntegacionVo;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.integracion.facade.IntegracionFacade.IntegrationActions;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.builders.ProjectCustomBuilder;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Bloque;
import ec.gob.ambiente.suia.domain.Camaroneras;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.ConcesionMineraUbicacionGeografica;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstadoAprobacionTdrCatalogo;
import ec.gob.ambiente.suia.domain.FaseDesecho;
import ec.gob.ambiente.suia.domain.FormaProyecto;
import ec.gob.ambiente.suia.domain.MineroArtesanal;
import ec.gob.ambiente.suia.domain.OficioViabilidad;
import ec.gob.ambiente.suia.domain.ProyectoBloque;
import ec.gob.ambiente.suia.domain.ProyectoCamaronera;
import ec.gob.ambiente.suia.domain.ProyectoDesechoPeligroso;
import ec.gob.ambiente.suia.domain.ProyectoFaseDesecho;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoMineroArtesanal;
import ec.gob.ambiente.suia.domain.ProyectoRutaUbicacionGeografica;
import ec.gob.ambiente.suia.domain.ProyectoSustanciaQuimica;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.SustanciaQuimica;
import ec.gob.ambiente.suia.domain.TipoArea;
import ec.gob.ambiente.suia.domain.TipoEmisionInclusionAmbiental;
import ec.gob.ambiente.suia.domain.TipoEstudio;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.TipoMaterial;
import ec.gob.ambiente.suia.domain.TipoPoblacion;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.TipoUbicacion;
import ec.gob.ambiente.suia.domain.TipoUsoSustancia;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityFichaCompletaRgd;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

@Stateless
public class ProyectoLicenciamientoAmbientalFacade {

	private static final Logger LOG = Logger.getLogger(ProyectoLicenciamientoAmbientalFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private IntegracionFacade integracionFacade;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private EntidadResponsableFacade entidadResponsableFacade;

	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private UsuarioServiceBean usuarioServiceBean;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
   	private EjecutarSentenciasNativas ejecutarSentenciasNativas;
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public String dblinkSuiaHidro=Constantes.getDblinkBpmsHyD();
	
	public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();
	
	public ProyectoLicenciamientoAmbiental guardar(ProyectoLicenciamientoAmbiental obj) {
		return crudServiceBean.saveOrUpdate(obj);
	}

	@SuppressWarnings("unchecked")
	public boolean eliminarProyecto(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental, Usuario usuario) {
		boolean eliminar=false;
		try {
			try {
				List<ProcessInstanceLog> process = procesoFacade.getProcessInstancesLogsVariableValue(usuario,
						Constantes.ID_PROYECTO, proyectoLicenciamientoAmbiental.getId().toString());
				for (ProcessInstanceLog processInstanceLog : process) {
					try {
						if(processInstanceLog.getProcessName().equals("Eliminar proyecto")){
							eliminar=true;
						} else {
							List<TaskSummary> listTS=  procesoFacade.obtenerTareaReserved(processInstanceLog.getId(), usuario);
							for (TaskSummary taskSummary : listTS) {
								Usuario usuarioTarea= new Usuario();
								usuarioTarea.setNombre(taskSummary.getCreatedBy().getId().toString());
								procesoFacade.abortProcessDelete(taskSummary.getId(), usuario,usuarioTarea);
								eliminar=true;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (JbpmException jex) {
				jex.printStackTrace();
			}
			if(eliminar){
				crudServiceBean.delete(proyectoLicenciamientoAmbiental);
	//			eliminar proyecto jbpmdb-hidrocarburos
				String sqlHyd="select dblink_exec('"+dblinkSuiaVerde+"','update proyectolicenciaambiental set estadoproyecto=false,motivoeliminar=''"+proyectoLicenciamientoAmbiental.getMotivoEliminar()+"'',fechaestadoproyecto=now() where id=''"+proyectoLicenciamientoAmbiental.getCodigo()+"''') as result";
				Query query = crudServiceBean.getEntityManager().createNativeQuery(sqlHyd);
				if(query.getResultList().size()>0)
				{
					query.getSingleResult();					
					String sqltaskbpmhyd="select * from dblink('"+dblinkSuiaHidro+"',"
							+ "'select id from task where processinstanceid "
							+ "in(select processinstanceid from variableinstancelog where value=''"+proyectoLicenciamientoAmbiental.getCodigo()+"'') "
							+ "and status in(''InProgress'',''Ready'',''Reserved'')') as (id integer)";
					Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
					List<Object>  resultPro = new ArrayList<Object>();
		    		resultPro=queryProceso.getResultList();
		    		if (resultPro!=null) {		    	 
		    			for(Object a: resultPro)
		    			{
		    				String sqlupdateTask="select dblink_exec('"+dblinkSuiaHidro+"','update task set actualowner_id=null,status=''Exited'' where id="+a+"') as result";
		    				Query queryTask = crudServiceBean.getEntityManager().createNativeQuery(sqlupdateTask);
		    				queryTask.getSingleResult();
		    			}
		    		}
					String sqllistfacilitadores="select * from dblink('"+dblinkSuiaVerde+"',"
							+ "'select u.nombreusuario,upper(p.nombresapellidos),c.valor "
							+ "from proyectofacilitador f,usuario u,persona p,contacto c "
							+ "where f.aceptaproyecto=''SI'' and f.proyecto_id=''"+proyectoLicenciamientoAmbiental.getCodigo()+"'' "
							+ "and f.usuario_id=u.id and u.nombreusuario=p.pin and c.entidad=p.id and c.tipocontacto=''EMAIL''')"
							+ "as (nombreusuario text,nombresapellidos text,valor text)";
					Query queryFacilitadores = crudServiceBean.getEntityManager().createNativeQuery(sqllistfacilitadores);
					List<Object[]> resultListFacilitadores = (List<Object[]>) queryFacilitadores.getResultList();
			    	if (resultListFacilitadores.size() > 0) {
			    		for (int i = 0; i < resultListFacilitadores.size(); i++) {
			    			Object[] facilitador = (Object[]) resultListFacilitadores.get(i);
			    			NotificacionAutoridadesController email=new NotificacionAutoridadesController();
			    			email.sendEmailBajaProyectoFacilitadores("Notificaci&oacute;n", facilitador[2].toString(), proyectoLicenciamientoAmbiental.getCodigo(), facilitador[1].toString());
			    		}
			    	}
				}
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean isProyectoLegadoHidrocarburos(String codigo, Usuario usuario) {
		if (Constantes.getAppIntegrationHydrocarbonsEnabled()) {
			try {
				return integracionFacade.isProjectHydrocarbons(codigo, usuario.getNombre(),
						usuario.getPasswordSha1Base64());
			} catch (Exception e) {
				LOG.error("Error al preguntar por proyecto de hidrocarburo.", e);
			}
		}
		return false;
	}

	public List<ProyectoCustom> listarProyectosLicenciamientoAmbientalFinalizados() {
		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE p.pren_status = true AND p.pren_project_finalized = true AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id ORDER BY p.pren_code limit 100";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}

		return projects;
	}	

	private List<ProyectoCustom> listarProyectosLicenciamientoAmbientalFinalizados34() {
		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE p.pren_status = true AND p.pren_project_finalized = true AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id "
				+ " and ca.cate_id in('3','4') ORDER BY p.pren_code";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}

		return projects;
	}

	public List<ProyectoCustom> getAllProjectsUnfinalizedByUser(Usuario usuario) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", usuario.getId());

		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE p.user_id = :userId AND p.pren_status = true AND p.pren_project_finalized = false AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id ORDER BY p.pren_code";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}

		return projects;
	}

	private List<ProyectoCustom> listarProyectosLicenciamientoAmbiental(Usuario usuario, boolean admin) {
		if (admin)
			return listarProyectosLicenciamientoAmbientalFinalizados();
		else {
		  String queryString;
		  Map<String, Object> params = new HashMap<String, Object>();
		  queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
						+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
						+ "WHERE p.user_id = :userId AND p.pren_status = true AND p.pren_project_finalized = true AND p.area_id = a.area_id AND "
						+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id ORDER BY p.pren_code";
		  params.put("userId", usuario.getId());
		  
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

			List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
			for (Object object : result) {
				projects.add(new ProyectoCustom((Object[]) object));
			}

			return projects;
		}
	}

	private List<ProyectoCustom> listarProyectosLicenciamientoAmbiental34(Usuario usuario, boolean admin) {
		if (admin)
			return listarProyectosLicenciamientoAmbientalFinalizados34();
		else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", usuario.getId());

			String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
					+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
					+ "WHERE p.user_id = :userId AND p.pren_status = true AND p.pren_project_finalized = true AND p.area_id = a.area_id AND "
					+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id "
					+ "and ca.cate_id in('3','4') ORDER BY p.pren_code";

			List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

			List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
			for (Object object : result) {
				projects.add(new ProyectoCustom((Object[]) object));
			}

			return projects;
		}
	}

	public List<ProyectoCustom> listarProyectosLicenciamientoAmbientalPaginado(Usuario usuario, boolean admin,
			Integer inicio, Integer total, String nombre, String codigo, String sector,String siglasResponsable, String permiso) {
		if (admin)
			return listarProyectosLicenciamientoAmbientalAdminPaginado(inicio, total, nombre, codigo, sector, siglasResponsable, permiso);
		else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", usuario.getId());
			params.put("total", total);
			params.put("inicio", inicio);

			String like = "";
			if (!nombre.isEmpty()) {
				params.put("nombre", "%" + nombre + "%");
				like += " AND LOWER(p.pren_name) like LOWER(:nombre)";
			}
			if (!codigo.isEmpty()) {
				params.put("codigo", "%" + codigo + "%");
				like += " AND  LOWER(p.pren_code) like LOWER(:codigo) ";
			}

			String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
					+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
					+ "WHERE  p.user_id = :userId AND p.pren_status = true AND p.area_id = a.area_id AND "
					+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id " + like
					+ " ORDER BY p.pren_id desc LIMIT :total OFFSET :inicio";

			List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

			List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
			for (Object object : result) {
				projects.add(new ProyectoCustom((Object[]) object));
			}

			return projects;
		}
	}
	
	private List<ProyectoCustom> listarProyectosLicenciamientoAmbientalAdminPaginado(Integer inicio, Integer total,
			String nombre, String codigo, String sector,String siglasResponsable, String permiso) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("total", total);
		params.put("inicio", inicio);

		String like = "";
		if (!nombre.isEmpty()) {
			params.put("nombre", "%" + nombre + "%");
			like += " AND LOWER(p.pren_name) like LOWER(:nombre)";
		}
		if (!codigo.isEmpty()) {
			params.put("codigo", "%" + codigo + "%");
			like += " AND  LOWER(p.pren_code) like LOWER(:codigo) ";
		}
		if (!sector.isEmpty()) {
			params.put("sector", "%" + sector + "%");
			like += " AND  LOWER(st.sety_name) like LOWER(:sector) ";
		}
		if (!siglasResponsable.isEmpty()) {
			params.put("siglasResponsable", "%" + siglasResponsable + "%");
			like += " AND  LOWER(a.area_abbreviation) like LOWER(:siglasResponsable) ";
		}
		if (!permiso.isEmpty()) {
			params.put("permiso", "%" + permiso + "%");
			like += " AND  LOWER(ca.cate_public_name) like LOWER(:permiso) ";
		}

		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE   p.pren_status = true  AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id " + like
				+ " ORDER BY p.pren_id desc LIMIT :total OFFSET :inicio";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}

		return projects;
	}
	
	public List<ProyectoCustom> listarProyectosLicenciamientoAmbientalPaginado(Integer inicio, Integer total,
			String nombre, String codigo, String sector,String siglasResponsable, String permiso,Usuario usuario,boolean rolEnte,boolean rolPatrimonio) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("total", total);
		params.put("inicio", inicio);

		String like = "";
		if (!nombre.isEmpty()) {
			params.put("nombre", "%" + nombre + "%");
			like += " AND LOWER(p.pren_name) like LOWER(:nombre)";
		}
		if (!codigo.isEmpty()) {
			params.put("codigo", "%" + codigo + "%");
			like += " AND  LOWER(p.pren_code) like LOWER(:codigo) ";
		}
		if (!sector.isEmpty()) {
			params.put("sector", "%" + sector + "%");
			like += " AND  LOWER(st.sety_name) like LOWER(:sector) ";
		}
		if (!siglasResponsable.isEmpty()) {
			params.put("siglasResponsable", "%" + siglasResponsable + "%");
			like += " AND  LOWER(a.area_abbreviation) like LOWER(:siglasResponsable) ";
		}
		if (!permiso.isEmpty()) {
			params.put("permiso", "%" + permiso + "%");
			like += " AND  LOWER(ca.cate_public_name) like LOWER(:permiso) ";
		}
		if (!permiso.isEmpty()) {
			params.put("permiso", "%" + permiso + "%");
			like += " AND  LOWER(ca.cate_public_name) like LOWER(:permiso) ";
		}
		//Rol ente acreditado no debe permitir listar proyectos del sector estrategico ni proyectos que intersecten 
		if(rolEnte){
			like += " and p.area_id=:areaId  AND  cacs_strategic=false AND p.pren_id not in (select pren_id from suia_iii.intersections_project where inpr_status=true and laye_id not in (1, 10)) ";
//			params.put("areaId",usuario.getIdArea());
			params.put("areaId",usuario.getListaAreaUsuario().get(0).getArea());
		}
		//Rol Patrimonio muestra solo los que intersecten 
		if(rolPatrimonio){
			like += " AND p.pren_id in (select pren_id from suia_iii.intersections_project where inpr_status=true) ";
		}
		//Direccion provincial
		if(!rolEnte && !rolPatrimonio){
//			Area area = new Area();
			
			List<AreaUsuario> listaAreasUsuario = new ArrayList<AreaUsuario>();
			listaAreasUsuario = usuario.getListaAreaUsuario();
			
			String areas = "";
			
			areas += " and p.area_id in (";
			
			for(AreaUsuario areaUs : listaAreasUsuario){
				
				if(areaUs.getArea()!=null && (areaUs.getArea().getArea() != null && (areaUs.getArea().getArea().getIdDireccionesProvinciales()!=null))){
					
					areas += areaUs.getArea().getId() + "," + areaUs.getArea().getArea().getIdDireccionesProvinciales() + "," ;
				}else{
					areas += areaUs.getArea().getId() + ",";
				}				
			}
			
			areas = areas.substring(0, areas.length() - 1);
			
			areas += ")";
			
			like += areas;
			
//			area=areaFacade.getArea(usuario.getIdArea());
			// if(area!=null && area.getArea().getIdDireccionesProvinciales()!=null)//ya estuvo comentado
//			if(area!=null && area.getArea() != null && area.getArea().getIdDireccionesProvinciales()!=null)
//			{
//				like += " and p.area_id in ("+usuario.getIdArea()+","+area.getArea().getIdDireccionesProvinciales()+") ";
//			}				
//			else
//			{
//				like += " and p.area_id=:areaId ";
//				params.put("areaId",usuario.getIdArea());
//			}
		}
		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE   p.pren_status = true  AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id  " + like
				+ " ORDER BY p.pren_id desc LIMIT :total OFFSET :inicio";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}

		return projects;
	}
	public Integer contarProyectosLicenciamientoAmbiental(String nombre, String codigo, String sector,String siglasResponsable, String permiso,Usuario usuario,boolean rolEnte,boolean rolPatrimonio) {
		Map<String, Object> params = new HashMap<String, Object>();
		String like = "";
		if (!nombre.isEmpty()) {
			params.put("nombre", "%" + nombre + "%");
			like += " AND LOWER(p.pren_name) like LOWER(:nombre)";
		}
		if (!codigo.isEmpty()) {
			params.put("codigo", "%" + codigo + "%");
			like += " AND  LOWER(p.pren_code) like LOWER(:codigo) ";
		}
		if (!sector.isEmpty()) {
			params.put("sector", "%" + sector + "%");
			like += " AND  LOWER(st.sety_name) like LOWER(:sector) ";
		}
		if (!siglasResponsable.isEmpty()) {
			params.put("siglasResponsable", "%" + siglasResponsable + "%");
			like += " AND  LOWER(a.area_abbreviation) like LOWER(:siglasResponsable) ";
		}
		if (!permiso.isEmpty()) {
			params.put("permiso", "%" + permiso + "%");
			like += " AND  LOWER(ca.cate_public_name) like LOWER(:permiso) ";
		}
		if (!permiso.isEmpty()) {
			params.put("permiso", "%" + permiso + "%");
			like += " AND  LOWER(ca.cate_public_name) like LOWER(:permiso) ";
		}
		
		
		//Rol ente acreditado no debe permitir listar proyectos del sector estrategico ni proyectos que intersecten 
		if(rolEnte){
			like += "  and p.area_id=:areaId AND  cacs_strategic=false AND p.pren_id not in (select pren_id from suia_iii.intersections_project where inpr_status=true and laye_id not in (1, 10)) ";
			
			params.put("areaId",usuario.getListaAreaUsuario().get(0).getArea().getId());
//			params.put("areaId",usuario.getIdArea());
		}
		//Rol Patrimonio muestra solo los que intersecten 
		if(rolPatrimonio){
			like += " AND p.pren_id in (select pren_id from suia_iii.intersections_project where inpr_status=true) ";
		}
		//Direccion provincial
		if(!rolEnte && !rolPatrimonio){
			List<AreaUsuario> listaAreasUsuario = new ArrayList<AreaUsuario>();
			listaAreasUsuario = usuario.getListaAreaUsuario();
			
			String areas = "";
			
			areas += " and p.area_id in (";
			
			for(AreaUsuario areaUs : listaAreasUsuario){
				
				if(areaUs.getArea()!=null && (areaUs.getArea().getArea() != null && (areaUs.getArea().getArea().getIdDireccionesProvinciales()!=null))){
					
					areas += areaUs.getArea().getId() + "," + areaUs.getArea().getArea().getIdDireccionesProvinciales() + "," ;
				}else{
					areas += areaUs.getArea().getId() + ",";
				}				
			}
			
			areas = areas.substring(0, areas.length() - 1);
			
			areas += ")";
			
			like += areas;
			
//			Area area = new Area();
//			area=areaFacade.getArea(usuario.getIdArea());
//			if(area!=null && area.getArea() != null && area.getArea().getIdDireccionesProvinciales()!=null)
//			{
//				like += " and p.area_id in ("+usuario.getIdArea()+","+area.getArea().getIdDireccionesProvinciales()+") ";
//			}				
//			else
//			{
//				like += " and p.area_id=:areaId ";
//				params.put("areaId",usuario.getIdArea());
//			}			
		}		
		
		String queryString = "SELECT count (*) "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE   p.pren_status = true  AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id " + like;

		List<Object> result = (List<Object>)crudServiceBean.findByNativeQuery(queryString, params);
		for (Object object : result) {
			return (((BigInteger) object).intValue());
		}
		
		return 0;
	}
		
	public List<ProyectoCustom> listarProyectosLicenciamientoAmbientalHidrocarburos(){
		Map<String, Object> params = new HashMap<String, Object>();

		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a, suia_iii.phases ph "
				+ "WHERE   p.pren_status = true  AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id AND cs.phas_id = ph.phas_id AND "
				+ "p.sety_id = 1 AND ph.phas_id = 3"
				+ " ORDER BY p.pren_code";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString,params);

		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}
		return projects;
	}

	public Integer contarProyectosLicenciamientoAmbiental(Usuario usuario, boolean admin, String nombre,
			String codigo, String sector,String siglasResponsable, String permiso) {
		if (admin)
			return contarProyectosLicenciamientoAmbientalAdmin(nombre, codigo, sector, siglasResponsable, permiso);
		else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", usuario.getId());
			String like = "";
			if (!nombre.isEmpty()) {
				params.put("nombre", "%" + nombre + "%");
				like += " AND LOWER(p.pren_name) like LOWER(:nombre) ";
			}
			if (!codigo.isEmpty()) {
				params.put("codigo", "%" + codigo + "%");
				like += " AND  LOWER(p.pren_code) like LOWER(:codigo) ";
			}
			String queryString = "SELECT count(*) "
					+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
					+ "WHERE   p.user_id = :userId AND p.pren_status = true  AND p.area_id = a.area_id AND "
					+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id " + like;

			List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

			for (Object object : result) {
				return (((BigInteger) object).intValue());
			}

			return 0;
		}
	}	

	private Integer contarProyectosLicenciamientoAmbientalAdmin(String nombre, String codigo, String sector,String siglasResponsable, String permiso) {
		String queryString = "SELECT count (*) "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE    p.pren_status = true  AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id";
		Map<String, Object> params = new HashMap<String, Object>();
		if (!nombre.isEmpty()) {
			params.put("nombre", "%" + nombre + "%");
			queryString += " AND LOWER(p.pren_name) like LOWER(:nombre)";
		}
		if (!codigo.isEmpty()) {
			params.put("codigo", "%" + codigo + "%");
			queryString += " AND  LOWER(p.pren_code) like LOWER(:codigo) ";
		}
		if (!sector.isEmpty()) {
			params.put("sector", "%" + sector + "%");
			queryString += " AND  LOWER(st.sety_name) like LOWER(:sector) ";
		}
		if (!siglasResponsable.isEmpty()) {
			params.put("siglasResponsable", "%" + siglasResponsable + "%");
			queryString += " AND  LOWER(a.area_abbreviation) like LOWER(:siglasResponsable) ";
		}
		if (!permiso.isEmpty()) {
			params.put("permiso", "%" + permiso + "%");
			queryString += " AND  LOWER(ca.cate_public_name) like LOWER(:permiso) ";
		}

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

		for (Object object : result) {
			return (((BigInteger) object).intValue());
		}

		return 0;
	}

	public List<ProyectoCustom> getAllProjectsByUser(Usuario usuario) {
		boolean admin = Usuario.isUserInRole(usuario, "admin");

		List<String> idsProyectos = new ArrayList<String>();
		Set<ProyectoCustom> proyectos = new HashSet<ProyectoCustom>();
		List<ProyectoCustom> projectsByUser = listarProyectosLicenciamientoAmbiental(usuario, admin);

		try {
			if (Constantes.getAppIntegrationSuiaEnabled()) {
				List<ProyectoLicIntegacionVo> result = integracionFacade.getProjectsByUserFromSuia(usuario.getNombre(),
						usuario.getPasswordSha1Base64());
				if (result!=null && !result.isEmpty())
				{
				for (ProyectoLicIntegacionVo proyectoLicIntegacionVo : result) {
					ProyectoCustom proyectoCustom = new ProjectCustomBuilder().fromProyectoSuia(proyectoLicIntegacionVo)
							.build();
					// if (proyectoCustom.getSector().equals("Hidrocarburos")) {
					proyectos.add(proyectoCustom);
					idsProyectos.add(proyectoCustom.getCodigo());
					// }
				}
			}
			}
		} catch (Exception ex) {
			LOG.error("Error al recuperar los proyecto por usuario.", ex);

		}
		
		//esto se añade para coa
		List<ProyectoCustom> listaCoa = listarProyectosCoa(usuario, false, admin, false, false);
		for (ProyectoCustom item : listaCoa) {
			proyectos.add(item);
			idsProyectos.add(item.getCodigo());
		}
		//fin de proyectos digitalizados
		for (ProyectoCustom proyectoLicenciamientoAmbiental : projectsByUser) {
			if (!idsProyectos.contains(proyectoLicenciamientoAmbiental.getCodigo())){
				proyectos.add(proyectoLicenciamientoAmbiental);
				idsProyectos.add(proyectoLicenciamientoAmbiental.getCodigo());
			}
		}
		//fin de esto se añade para coa
		//para opcion digitalizados
		List<ProyectoCustom> listaDigitalizados = listarProyectosDigitalizados(usuario, admin);
		for (ProyectoCustom item : listaDigitalizados) {
			item.setSourceType(ProyectoCustom.SOURCE_TYPE_DIGITALIZACION);
			item.setCategoria("Digitalizacion");
			if (!idsProyectos.contains(item.getCodigo())){
				proyectos.add(item);
			}else{
				for (ProyectoCustom objProyecto : proyectos) {
					if(objProyecto.getCodigo().equals(item.getCodigo())){
						objProyecto.setCategoria("Digitalizacion");
						break;
					}
				}
			}
		}
		//listado de proyectos digitalizados

		return new ArrayList<ProyectoCustom>(proyectos);
	}

	public List<ProyectoCustom> getAllProjects34ByUser(Usuario usuario) {
		boolean admin = Usuario.isUserInRole(usuario, "admin");

		List<String> idsProyectos = new ArrayList<String>();
		Set<ProyectoCustom> proyectos = new HashSet<ProyectoCustom>();
		List<ProyectoCustom> projectsByUser = listarProyectosLicenciamientoAmbiental34(usuario, admin);

		/*
		 * try { if (Constantes.getAppIntegrationSuiaEnabled()) { List<ProyectoLicIntegacionVo> result =
		 * integracionFacade.getProjectsByUserFromSuia(usuario.getNombre(), usuario.getPasswordSha1Base64()); for
		 * (ProyectoLicIntegacionVo proyectoLicIntegacionVo : result) { ProyectoCustom proyectoCustom = new
		 * ProjectCustomBuilder() .fromProyectoSuia(proyectoLicIntegacionVo).build(); //if
		 * (proyectoCustom.getSector().equals("Hidrocarburos")) { proyectos.add(proyectoCustom);
		 * idsProyectos.add(proyectoCustom.getCodigo()); //} } } } catch (Exception ex) { LOG.error(
		 * "Error al recuperar los proyecto por usuario.", ex);
		 * 
		 * }
		 */

		for (ProyectoCustom proyectoLicenciamientoAmbiental : projectsByUser) {
			if (!idsProyectos.contains(proyectoLicenciamientoAmbiental.getCodigo()))
				proyectos.add(proyectoLicenciamientoAmbiental);
		}

		return new ArrayList<ProyectoCustom>(proyectos);
	}

	public List<ProyectoCustom> listarProyectosLicenciamientoAmbientalPorAreaResponsable(Area area) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("areaId", area.getId());

		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE p.pren_status = true AND p.pren_project_finalized = true AND p.area_id = :areaId AND a.area_id = :areaId AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id ORDER BY p.pren_code";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}

		return projects;
	}
	
	public List<ProyectoCustom> listarProyectosLicenciamientoAmbientalPorAreaResponsable(List<AreaUsuario> areas) {
		Map<String, Object> params = new HashMap<String, Object>();
		String idAreas = "";
		for(AreaUsuario area :areas){
			idAreas += (idAreas.equals("")) ? area.getArea() .getId() : "," + area.getArea() .getId();
		}
				
		params.put("areaId", idAreas);

		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE p.pren_status = true AND p.pren_project_finalized = true AND p.area_id IN (:areaId) AND a.area_id = IN(:areaId) AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id ORDER BY p.pren_code";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}

		return projects;
	}

	public List<ProyectoCustom> listarProyectosDigitalizados(Usuario usuario, boolean admin) {
		  String queryString;
		  Map<String, Object> params = new HashMap<String, Object>();
		  queryString = "SELECT p.enaa_id, p.enaa_project_code, p.enaa_project_name, p.enaa_register_date, st.sety_name, p.enaa_environmental_administrative_authorization, p.enaa_catalog_activity, a.area_abbreviation, a.area_name "
		  		+ "FROM coa_digitalization_linkage.environmental_administrative_authorizations  p, suia_iii.sector_types st, coa_mae.catalog_ciuu ca, areas a "
		  		+ "WHERE p.user_id = :userId AND p.enaa_status = true AND p.area_id_aaa = a.area_id AND "
		  		+ "	ca.caci_id = p.caci_id AND st.sety_id = p.sety_id and p.enaa_system in (0, 1, 2, 3, 4, 5)"
		  		+ "ORDER BY p.enaa_project_code";
		  params.put("userId", usuario.getId());
		  List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);
		  List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		  for (Object object : result) {
			  projects.add(new ProyectoCustom((Object[]) object));
		  }
		return projects;
	}

	public void actualizarSimpleProyecto(ProyectoLicenciamientoAmbiental proyecto) {
		crudServiceBean.saveOrUpdate(proyecto);
	}

	public ProyectoLicenciamientoAmbiental buscarProyectosLicenciamientoAmbientalPorId(Integer id) {
		ProyectoLicenciamientoAmbiental p = crudServiceBean.find(ProyectoLicenciamientoAmbiental.class, id);
		try {
			p.getUsuario().getNombre();
			if (p.getCatalogoCategoria().getTipoSubsector() != null) {
				p.getCatalogoCategoria().getTipoSubsector().getId();
			}
		} catch (Exception e) {
			LOG.error("Error al buscar proyectos.", e);
		}
		try {
			p.getAreaResponsable().getId();
		} catch (Exception e) {
		}
		try {
			p.getProyectoBloques().size();
		} catch (Exception e) {
		}

		return p;
	}

	public ProyectoLicenciamientoAmbiental cargarProyectoFullPorId(Integer id) throws Exception {
		try {
			ProyectoLicenciamientoAmbiental p = crudServiceBean.find(ProyectoLicenciamientoAmbiental.class, id);
			p.getProyectoBloques().size();
			p.getProyectoUbicacionesGeograficas().size();
			p.getProyectoRutaUbicacionesGeograficas().size();
			for (ProyectoRutaUbicacionGeografica proyUbi : p.getProyectoRutaUbicacionesGeograficas()){

				if(proyUbi.getUbicacionesGeografica().getEnteAcreditado()!=null)
					proyUbi.getUbicacionesGeografica().getEnteAcreditado().getId();

				if(proyUbi.getProyectoLicenciamientoAmbiental().getUbicacionesGeograficas()!=null)
					proyUbi.getProyectoLicenciamientoAmbiental().getUbicacionesGeograficas().size();
			}

			p.getProyectoDesechoPeligrosos().size();
			p.getCatalogoCategoria().getTipoArea().getNombre();
			if (p.getAreaResponsable() != null)
				p.getAreaResponsable().getTipoArea().getNombre();
			try {
				if (p.getCatalogoCategoria().getTipoSubsector() != null)
					p.getCatalogoCategoria().getTipoSubsector().getId();
			} catch (Exception e) {
				LOG.error("Error al cargar proyecto.", e);
			}

			for (FormaProyecto formaProyecto : p.getFormasProyectos()) {
				formaProyecto.getCoordenadas().size();
			}
			for (ProyectoFaseDesecho proyectoFaseDesecho : p.getFasesDesechos()) {
				proyectoFaseDesecho.getFaseDesecho().toString();
			}
			for (ProyectoSustanciaQuimica proyectoSustanciaQuimica : p.getProyectoSustanciasQuimicas()) {
				proyectoSustanciaQuimica.getSustanciaQuimica().toString();
			}
			for (ConcesionMinera concesionMinera : p.getConcesionesMineras()) {
				if (concesionMinera.getConcesionesUbicacionesGeograficas() != null)
					concesionMinera.getConcesionesUbicacionesGeograficas().size();
			}
			for (ProyectoDesechoPeligroso proyectoDesechoPeligroso : p.getProyectoDesechoPeligrosos()) {
				proyectoDesechoPeligroso.getDesechoPeligroso().toString();
			}
			for (ProyectoMineroArtesanal proyectoMineroArtesanal : p.getProyectoMinerosArtesanales()) {
				proyectoMineroArtesanal.getMineroArtesanal().toString();
			}
			for (UbicacionesGeografica u : p.getUbicacionesGeograficas()) {
				u.getId();
			}
			return p;
		} catch (Exception e) {
			throw new Exception("No se puede cargar el proyecto, hay datos incorrectos guardados.", e);
		}
	}

	public boolean validarNumeroResolucion(String numero) {
		return true;
	}

	public OficioViabilidad verificarOficioViabilidad(String numero) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigo", numero);
		@SuppressWarnings("unchecked")
		List<OficioViabilidad> result = (List<OficioViabilidad>) crudServiceBean
				.findByNamedQuery(OficioViabilidad.FIND_BY_CODIGO, parameters);
		if (result != null && !result.isEmpty())
			return result.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public void guardar(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental,
			CatalogoCategoriaSistema categoria, List<UbicacionesGeografica> ubicaciones,
			List<UbicacionesGeografica> ubicacionesRutas, List<Bloque> bloques,
			List<ConcesionMinera> concesionesMineras, List<MineroArtesanal> minerosArtesanales,
			List<CoordinatesWrapper> coordinatesWrappers, Documento documentoCoordenadas,
			List<FaseDesecho> fasesDesechos, List<DesechoPeligroso> desechosPeligrosos,
			List<SustanciaQuimica> sustanciasQuimicas, String userNameLogin) throws CmisAlfrescoException {
		
		if(proyectoLicenciamientoAmbiental.getTipoEmisionInclusionAmbiental()!=null)
		{
			if(proyectoLicenciamientoAmbiental.getTipoEmisionInclusionAmbiental().getId().equals(2))
				proyectoLicenciamientoAmbiental.setNumeroDeResolucion("N/A");
			else
			{
				proyectoLicenciamientoAmbiental.setNumeroDeResolucion(null);
			}
		}

		if (proyectoLicenciamientoAmbiental.getRefineria() != null)
			crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental.getRefineria());
		if (proyectoLicenciamientoAmbiental.getInfraestructura() != null)
			crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental.getInfraestructura());
		if (proyectoLicenciamientoAmbiental.getComercializadora() != null)
			crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental.getComercializadora());
		if (proyectoLicenciamientoAmbiental.getEstacionServicio() != null)
			crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental.getEstacionServicio());

		try {
			categoria = crudServiceBean.find(CatalogoCategoriaSistema.class, categoria.getId());
			categoria.getTipoArea().getId();
		} catch (Exception e) {
			LOG.error("Error al guardar e proyecto.", e);
		}

		proyectoLicenciamientoAmbiental.setCatalogoCategoria(categoria);
		if (!proyectoLicenciamientoAmbiental.isPersisted()) {
			proyectoLicenciamientoAmbiental.setFechaRegistro(new Date());
			if(proyectoLicenciamientoAmbiental.getCodigo()==null)
			proyectoLicenciamientoAmbiental.setCodigo(secuenciasFacade.getSecuenciaProyecto());
		}

		crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental);

		if (proyectoLicenciamientoAmbiental.getProyectoUbicacionesGeograficas() != null) {
			for (ProyectoUbicacionGeografica proyectoUbicacionGeografica : proyectoLicenciamientoAmbiental
					.getProyectoUbicacionesGeograficas())
				crudServiceBean.delete(proyectoUbicacionGeografica);
		}

		List<ProyectoUbicacionGeografica> ubicacionesProyecto = new ArrayList<ProyectoUbicacionGeografica>();
		if (ubicaciones != null) {
			for (UbicacionesGeografica ubicacionesGeografica : ubicaciones) {
				ProyectoUbicacionGeografica proyectoUbicacionGeografica = new ProyectoUbicacionGeografica();
				proyectoUbicacionGeografica.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				proyectoUbicacionGeografica.setUbicacionesGeografica(ubicacionesGeografica);
				proyectoUbicacionGeografica.setOrden(ubicacionesGeografica.getOrden());
				crudServiceBean.saveOrUpdate(proyectoUbicacionGeografica);
				ubicacionesProyecto.add(proyectoUbicacionGeografica);
			}
		}

		if (proyectoLicenciamientoAmbiental.getProyectoRutaUbicacionesGeograficas() != null) {
			for (ProyectoRutaUbicacionGeografica proyectoRutaUbicacionGeografica : proyectoLicenciamientoAmbiental
					.getProyectoRutaUbicacionesGeograficas())
				crudServiceBean.delete(proyectoRutaUbicacionGeografica);
		}
		if (ubicacionesRutas != null)
			for (UbicacionesGeografica ubicacionesGeografica : ubicacionesRutas) {
				ProyectoRutaUbicacionGeografica proyectoRutaUbicacionGeografica = new ProyectoRutaUbicacionGeografica();
				proyectoRutaUbicacionGeografica.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				proyectoRutaUbicacionGeografica.setUbicacionesGeografica(ubicacionesGeografica);
				crudServiceBean.saveOrUpdate(proyectoRutaUbicacionGeografica);
			}

		if (proyectoLicenciamientoAmbiental.getFasesDesechos() != null) {
			for (ProyectoFaseDesecho proyectoFaseDesecho : proyectoLicenciamientoAmbiental.getFasesDesechos())
				crudServiceBean.delete(proyectoFaseDesecho);
		}
		for (FaseDesecho faseDesecho : fasesDesechos) {
			ProyectoFaseDesecho proyectoFaseDesecho = new ProyectoFaseDesecho();
			proyectoFaseDesecho.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
			proyectoFaseDesecho.setFaseDesecho(faseDesecho);
			crudServiceBean.saveOrUpdate(proyectoFaseDesecho);
		}

		if (proyectoLicenciamientoAmbiental.getProyectoDesechoPeligrosos() != null) {
			for (ProyectoDesechoPeligroso proyectoDesechoPeligroso : proyectoLicenciamientoAmbiental
					.getProyectoDesechoPeligrosos())
				crudServiceBean.delete(proyectoDesechoPeligroso);
		}
		for (DesechoPeligroso desechoPeligroso : desechosPeligrosos) {
			ProyectoDesechoPeligroso proyDesecho = new ProyectoDesechoPeligroso();
			proyDesecho.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
			proyDesecho.setDesechoPeligroso(desechoPeligroso);
			crudServiceBean.saveOrUpdate(proyDesecho);
		}

		if (proyectoLicenciamientoAmbiental.getProyectoSustanciasQuimicas() != null) {
			for (ProyectoSustanciaQuimica proyectoSustanciaQuimica : proyectoLicenciamientoAmbiental
					.getProyectoSustanciasQuimicas())
				crudServiceBean.delete(proyectoSustanciaQuimica);
		}
		for (SustanciaQuimica sustanciaQuimica : sustanciasQuimicas) {
			ProyectoSustanciaQuimica proyectoSustanciaQuimica = new ProyectoSustanciaQuimica();
			proyectoSustanciaQuimica.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
			proyectoSustanciaQuimica.setSustanciaQuimica(sustanciaQuimica);
			crudServiceBean.saveOrUpdate(proyectoSustanciaQuimica);
		}

		if (proyectoLicenciamientoAmbiental.getFormasProyectos() != null
				&& !proyectoLicenciamientoAmbiental.getFormasProyectos().isEmpty()) {
			try {
				crudServiceBean.delete(proyectoLicenciamientoAmbiental.getFormasProyectos().get(0));
			} catch (Exception e) {
				LOG.error("Error al eliminar las formas del proyecto.", e);
			}
		}

		if (proyectoLicenciamientoAmbiental.getFormasProyectos() != null)
			for (FormaProyecto formaProyecto : proyectoLicenciamientoAmbiental.getFormasProyectos()) {
				crudServiceBean.delete(formaProyecto);
				
				if(formaProyecto.getCoordenadas().size()>0)
				{
					String queryCoordenadas = "UPDATE suia_iii.coordinates " + "SET coor_status=false " + "WHERE ";
					for (Coordenada coordenada : formaProyecto.getCoordenadas()) {
						queryCoordenadas += " coor_id=" + coordenada.getId() + " OR";
					}
					queryCoordenadas = queryCoordenadas.substring(0, queryCoordenadas.length() - 3);
					queryCoordenadas += ";";

					crudServiceBean.insertUpdateByNativeQuery(queryCoordenadas, null);
				}
			}
		for (CoordinatesWrapper coordinatesWrapper : coordinatesWrappers) {
			FormaProyecto formaProyecto = new FormaProyecto();
			formaProyecto.setTipoForma(coordinatesWrapper.getTipoForma());
			formaProyecto.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);

			crudServiceBean.saveOrUpdate(formaProyecto);
			for (Coordenada coordenada : coordinatesWrapper.getCoordenadas()) {
				coordenada.setEstado(true);
				coordenada.setFormasProyecto(formaProyecto);
			}

			String queryCoordenadas = "INSERT INTO suia_iii.coordinates"
					+ "(coor_id, coor_status, coor_description, coor_order, coor_x, coor_y, coor_zone, prsh_id, rpsh_id)"
					+ "VALUES ";
			for (Coordenada coordenada : coordinatesWrapper.getCoordenadas()) {
				String id = crudServiceBean.getSecuenceNextValue("seq_coor_id", "suia_iii").toString();
				String queryAdd = "(" + id + ", true, '" + coordenada.getDescripcion() + "', " + coordenada.getOrden()
						+ ", " + coordenada.getX() + ", " + coordenada.getY() + ", '" + coordenada.getZona() + "', " + formaProyecto.getId()
						+ ", null),";
				queryCoordenadas += queryAdd;
			}
			queryCoordenadas = queryCoordenadas.substring(0, queryCoordenadas.length() - 1);
			queryCoordenadas += ";";

			crudServiceBean.insertUpdateByNativeQuery(queryCoordenadas, null);
		}

		if (proyectoLicenciamientoAmbiental.getProyectoBloques() != null) {
			for (ProyectoBloque proyectoBloque : proyectoLicenciamientoAmbiental.getProyectoBloques())
				crudServiceBean.delete(proyectoBloque);
		}
		if (bloques != null) {
			for (Bloque bloque : bloques) {
				ProyectoBloque proyectoBloque = new ProyectoBloque();
				proyectoBloque.setBloque(bloque);
				proyectoBloque.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				crudServiceBean.saveOrUpdate(proyectoBloque);
			}
		}

		if (proyectoLicenciamientoAmbiental.getProyectoMinerosArtesanales() != null) {
			for (ProyectoMineroArtesanal proyectoMineroArtesanal : proyectoLicenciamientoAmbiental
					.getProyectoMinerosArtesanales())
				crudServiceBean.delete(proyectoMineroArtesanal);
		}

		List<Documento> listaDocumentos = documentosFacade.documentoXTablaId(proyectoLicenciamientoAmbiental.getId(),
				ProyectoLicenciamientoAmbiental.class.getSimpleName());
		for (Documento d : listaDocumentos) {
			if (documentoCoordenadas == null
					&& TipoDocumentoSistema.TIPO_COORDENADAS.equals(d.getTipoDocumento().getId())) {
				d.setEstado(true);
			} else {
				d.setEstado(false);
			}
		}

		if (documentoCoordenadas != null) {
			try {
				documentoCoordenadas.setIdTable(proyectoLicenciamientoAmbiental.getId());
				documentoCoordenadas.setDescripcion(Constantes.NOMBRE_DOCUMENTO_COORDENADAS_PROYECTO);
				documentoCoordenadas.setEstado(true);
				documentoCoordenadas = documentosFacade.guardarDocumentoAlfresco(
						proyectoLicenciamientoAmbiental.getCodigo(), Constantes.CARPETA_COORDENADAS, 0L,
						documentoCoordenadas, TipoDocumentoSistema.TIPO_COORDENADAS, null);
			} catch (ServiceException ex) {
				LOG.error("Error al actualizar los ficheros.", ex);
			}
		} 
		if (minerosArtesanales != null) {
			for (MineroArtesanal minero : minerosArtesanales) {

				try {
					if (minero.getContratoOperacion() != null) {
						minero.getContratoOperacion().setIdTable(proyectoLicenciamientoAmbiental.getId());
						minero.getContratoOperacion().setDescripcion(Constantes.NOMBRE_DOCUMENTO_CONTRATO_OPERACION);
						Documento contratoOperacion = documentosFacade.guardarDocumentoAlfresco(
								proyectoLicenciamientoAmbiental.getCodigo(), Constantes.CARPETA_CONTRATOS_OPERACION, 0L,
								minero.getContratoOperacion(), TipoDocumentoSistema.TIPO_CONTRATO_OPERACION, null);
						minero.setContratoOperacion(contratoOperacion);
					}
					if (minero.getRegistroMineroArtesanal() != null) {
						minero.getRegistroMineroArtesanal().setIdTable(proyectoLicenciamientoAmbiental.getId());
						minero.getRegistroMineroArtesanal()
								.setDescripcion(Constantes.NOMBRE_DOCUMENTO_REGISTRO_MINERO_ARTESANAL);
						Documento registroMinero = documentosFacade.guardarDocumentoAlfresco(
								proyectoLicenciamientoAmbiental.getCodigo(), Constantes.CARPETA_REGISTROS_MINEROS, 0L,
								minero.getRegistroMineroArtesanal(), TipoDocumentoSistema.TIPO_REGISTRO_MINERO, null);
						minero.setRegistroMineroArtesanal(registroMinero);
					}
				} catch (ServiceException exception) {
					LOG.error("Error al salvar en el alfresco", exception);
				}

				crudServiceBean.saveOrUpdate(minero);
				ProyectoMineroArtesanal proyectoMineroArtesanal = new ProyectoMineroArtesanal();
				proyectoMineroArtesanal.setMineroArtesanal(minero);
				proyectoMineroArtesanal.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				crudServiceBean.saveOrUpdate(proyectoMineroArtesanal);
			}
		}
		
		Map<String, Object> paramsConcesion = new HashMap<String, Object>();
		paramsConcesion.put("p_proyecto", proyectoLicenciamientoAmbiental);
		List<ConcesionMinera> concesionesAnteriores=(List<ConcesionMinera>)crudServiceBean.findByNamedQuery(ConcesionMinera.OBTENER_CONSECION_MINERA, paramsConcesion);
		if (concesionesAnteriores != null) {
			for (ConcesionMinera concesionMinera : concesionesAnteriores) {
				crudServiceBean.delete(concesionMinera);
				for (ConcesionMineraUbicacionGeografica concesionMineraUbicacionGeografica : concesionMinera.getConcesionesUbicacionesGeograficas())
					crudServiceBean.delete(concesionMineraUbicacionGeografica);
			}
		}
		if (concesionesMineras != null) {
			for (ConcesionMinera concesionMinera : concesionesMineras) {
				concesionMinera.setEstado(true);
				concesionMinera.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				List<ConcesionMineraUbicacionGeografica> ubicacionesConcesion = concesionMinera
						.getConcesionesUbicacionesGeograficas();
				concesionMinera.setConcesionesUbicacionesGeograficas(null);
				concesionMinera.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				crudServiceBean.saveOrUpdate(concesionMinera);
				if (ubicacionesConcesion != null) {
					for (ConcesionMineraUbicacionGeografica concesionMineraUbicacionGeografica : ubicacionesConcesion) {
						concesionMineraUbicacionGeografica.setConcesionMinera(concesionMinera);
						crudServiceBean.saveOrUpdate(concesionMineraUbicacionGeografica);
					}
					concesionMinera.setConcesionesUbicacionesGeograficas(ubicacionesConcesion);
				}
			}
		}

		if (concesionesMineras != null)
			proyectoLicenciamientoAmbiental.setConcesionesMineras(concesionesMineras);
		proyectoLicenciamientoAmbiental.setProyectoUbicacionesGeograficas(ubicacionesProyecto);

		try {
			Area area = new Area();
			area=entidadResponsableFacade.obtenerEntidadResponsable(proyectoLicenciamientoAmbiental);
//			if(area.getTipoArea().getId()==3 &&(area.getId()==584 || area.getId()==580 || area.getId()==577 || area.getId()==576 || area.getId()==579)){
			if (area.getHabilitarArea()==null){
				area.setHabilitarArea(false);
	        }
			if (area.getTipoArea().getId()==3  && (area.getHabilitarArea()==true)){
				proyectoLicenciamientoAmbiental.setAreaResponsable(area);
			}else if (area.getTipoArea().getId()==3) {
				proyectoLicenciamientoAmbiental.setAreaResponsable(null);
			}else {
				proyectoLicenciamientoAmbiental.setAreaResponsable(area);
			}
			proyectoLicenciamientoAmbiental.setAreaResponsable(area);
			crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental);
		} catch (Exception e) {
			LOG.error("Error al guardar el proyecto.", e);
		}
	}
	
	public void guardarConCamaronera(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental,
			CatalogoCategoriaSistema categoria, List<UbicacionesGeografica> ubicaciones,
			List<UbicacionesGeografica> ubicacionesRutas, List<Bloque> bloques,
			List<ConcesionMinera> concesionesMineras, List<MineroArtesanal> minerosArtesanales,
			List<CoordinatesWrapper> coordinatesWrappers, Documento documentoCoordenadas,
			List<FaseDesecho> fasesDesechos, List<DesechoPeligroso> desechosPeligrosos,
			List<SustanciaQuimica> sustanciasQuimicas, String userNameLogin,List<ProyectoCamaronera> listProyectoCamaroneras) throws CmisAlfrescoException {
		
		if (proyectoLicenciamientoAmbiental.getRefineria() != null)
			crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental.getRefineria());
		if (proyectoLicenciamientoAmbiental.getInfraestructura() != null)
			crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental.getInfraestructura());
		if (proyectoLicenciamientoAmbiental.getComercializadora() != null)
			crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental.getComercializadora());
		if (proyectoLicenciamientoAmbiental.getEstacionServicio() != null)
			crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental.getEstacionServicio());

		try {
			categoria = crudServiceBean.find(CatalogoCategoriaSistema.class, categoria.getId());
			categoria.getTipoArea().getId();
		} catch (Exception e) {
			LOG.error("Error al guardar e proyecto.", e);
		}

		proyectoLicenciamientoAmbiental.setCatalogoCategoria(categoria);
		if (!proyectoLicenciamientoAmbiental.isPersisted()) {
			proyectoLicenciamientoAmbiental.setFechaRegistro(new Date());
			if(proyectoLicenciamientoAmbiental.getCodigo()==null)
			proyectoLicenciamientoAmbiental.setCodigo(secuenciasFacade.getSecuenciaProyecto());
		}

		crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental);

		if (proyectoLicenciamientoAmbiental.getProyectoUbicacionesGeograficas() != null) {
			for (ProyectoUbicacionGeografica proyectoUbicacionGeografica : proyectoLicenciamientoAmbiental
					.getProyectoUbicacionesGeograficas())
				crudServiceBean.delete(proyectoUbicacionGeografica);
		}

		List<ProyectoUbicacionGeografica> ubicacionesProyecto = new ArrayList<ProyectoUbicacionGeografica>();
		if (ubicaciones != null) {
			for (UbicacionesGeografica ubicacionesGeografica : ubicaciones) {
				ProyectoUbicacionGeografica proyectoUbicacionGeografica = new ProyectoUbicacionGeografica();
				proyectoUbicacionGeografica.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				proyectoUbicacionGeografica.setUbicacionesGeografica(ubicacionesGeografica);
				proyectoUbicacionGeografica.setOrden(ubicacionesGeografica.getOrden());
				crudServiceBean.saveOrUpdate(proyectoUbicacionGeografica);
				ubicacionesProyecto.add(proyectoUbicacionGeografica);
			}
		}

		if (proyectoLicenciamientoAmbiental.getProyectoRutaUbicacionesGeograficas() != null) {
			for (ProyectoRutaUbicacionGeografica proyectoRutaUbicacionGeografica : proyectoLicenciamientoAmbiental
					.getProyectoRutaUbicacionesGeograficas())
				crudServiceBean.delete(proyectoRutaUbicacionGeografica);
		}
		if (ubicacionesRutas != null)
			for (UbicacionesGeografica ubicacionesGeografica : ubicacionesRutas) {
				ProyectoRutaUbicacionGeografica proyectoRutaUbicacionGeografica = new ProyectoRutaUbicacionGeografica();
				proyectoRutaUbicacionGeografica.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				proyectoRutaUbicacionGeografica.setUbicacionesGeografica(ubicacionesGeografica);
				crudServiceBean.saveOrUpdate(proyectoRutaUbicacionGeografica);
			}

		if (proyectoLicenciamientoAmbiental.getFasesDesechos() != null) {
			for (ProyectoFaseDesecho proyectoFaseDesecho : proyectoLicenciamientoAmbiental.getFasesDesechos())
				crudServiceBean.delete(proyectoFaseDesecho);
		}
		for (FaseDesecho faseDesecho : fasesDesechos) {
			ProyectoFaseDesecho proyectoFaseDesecho = new ProyectoFaseDesecho();
			proyectoFaseDesecho.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
			proyectoFaseDesecho.setFaseDesecho(faseDesecho);
			crudServiceBean.saveOrUpdate(proyectoFaseDesecho);
		}

		if (proyectoLicenciamientoAmbiental.getProyectoDesechoPeligrosos() != null) {
			for (ProyectoDesechoPeligroso proyectoDesechoPeligroso : proyectoLicenciamientoAmbiental
					.getProyectoDesechoPeligrosos())
				crudServiceBean.delete(proyectoDesechoPeligroso);
		}
		for (DesechoPeligroso desechoPeligroso : desechosPeligrosos) {
			ProyectoDesechoPeligroso proyDesecho = new ProyectoDesechoPeligroso();
			proyDesecho.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
			proyDesecho.setDesechoPeligroso(desechoPeligroso);
			crudServiceBean.saveOrUpdate(proyDesecho);
		}

		if (proyectoLicenciamientoAmbiental.getProyectoSustanciasQuimicas() != null) {
			for (ProyectoSustanciaQuimica proyectoSustanciaQuimica : proyectoLicenciamientoAmbiental
					.getProyectoSustanciasQuimicas())
				crudServiceBean.delete(proyectoSustanciaQuimica);
		}
		for (SustanciaQuimica sustanciaQuimica : sustanciasQuimicas) {
			ProyectoSustanciaQuimica proyectoSustanciaQuimica = new ProyectoSustanciaQuimica();
			proyectoSustanciaQuimica.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
			proyectoSustanciaQuimica.setSustanciaQuimica(sustanciaQuimica);
			crudServiceBean.saveOrUpdate(proyectoSustanciaQuimica);
		}

		if (proyectoLicenciamientoAmbiental.getFormasProyectos() != null
				&& !proyectoLicenciamientoAmbiental.getFormasProyectos().isEmpty()) {
			try {
				crudServiceBean.delete(proyectoLicenciamientoAmbiental.getFormasProyectos().get(0));
			} catch (Exception e) {
				LOG.error("Error al eliminar las formas del proyecto.", e);
			}
		}

		if (proyectoLicenciamientoAmbiental.getFormasProyectos() != null)
			for (FormaProyecto formaProyecto : proyectoLicenciamientoAmbiental.getFormasProyectos()) {
				crudServiceBean.delete(formaProyecto);
				
				if(formaProyecto.getCoordenadas().size()>0)
				{
					String queryCoordenadas = "UPDATE suia_iii.coordinates " + "SET coor_status=false " + "WHERE ";
					for (Coordenada coordenada : formaProyecto.getCoordenadas()) {
						queryCoordenadas += " coor_id=" + coordenada.getId() + " OR";
					}
					queryCoordenadas = queryCoordenadas.substring(0, queryCoordenadas.length() - 3);
					queryCoordenadas += ";";

					crudServiceBean.insertUpdateByNativeQuery(queryCoordenadas, null);
				}
			}
		for (CoordinatesWrapper coordinatesWrapper : coordinatesWrappers) {
			FormaProyecto formaProyecto = new FormaProyecto();
			formaProyecto.setTipoForma(coordinatesWrapper.getTipoForma());
			formaProyecto.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);

			crudServiceBean.saveOrUpdate(formaProyecto);
			for (Coordenada coordenada : coordinatesWrapper.getCoordenadas()) {
				coordenada.setEstado(true);
				coordenada.setFormasProyecto(formaProyecto);
			}

			String queryCoordenadas = "INSERT INTO suia_iii.coordinates"
					+ "(coor_id, coor_status, coor_description, coor_order, coor_x, coor_y, coor_zone, prsh_id, rpsh_id)"
					+ "VALUES ";
			for (Coordenada coordenada : coordinatesWrapper.getCoordenadas()) {
				String id = crudServiceBean.getSecuenceNextValue("seq_coor_id", "suia_iii").toString();
				String queryAdd = "(" + id + ", true, '" + coordenada.getDescripcion() + "', " + coordenada.getOrden()
						+ ", " + coordenada.getX() + ", " + coordenada.getY() + ", '" + coordenada.getZona() + "', " + formaProyecto.getId()
						+ ", null),";
				queryCoordenadas += queryAdd;
			}
			queryCoordenadas = queryCoordenadas.substring(0, queryCoordenadas.length() - 1);
			queryCoordenadas += ";";

			crudServiceBean.insertUpdateByNativeQuery(queryCoordenadas, null);
		}

		if (proyectoLicenciamientoAmbiental.getProyectoBloques() != null) {
			for (ProyectoBloque proyectoBloque : proyectoLicenciamientoAmbiental.getProyectoBloques())
				crudServiceBean.delete(proyectoBloque);
		}
		if (bloques != null) {
			for (Bloque bloque : bloques) {
				ProyectoBloque proyectoBloque = new ProyectoBloque();
				proyectoBloque.setBloque(bloque);
				proyectoBloque.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				crudServiceBean.saveOrUpdate(proyectoBloque);
			}
		}

		if (proyectoLicenciamientoAmbiental.getProyectoMinerosArtesanales() != null) {
			for (ProyectoMineroArtesanal proyectoMineroArtesanal : proyectoLicenciamientoAmbiental
					.getProyectoMinerosArtesanales())
				crudServiceBean.delete(proyectoMineroArtesanal);
		}

		List<Documento> listaDocumentos = documentosFacade.documentoXTablaId(proyectoLicenciamientoAmbiental.getId(),
				ProyectoLicenciamientoAmbiental.class.getSimpleName());
		for (Documento d : listaDocumentos) {
			if (documentoCoordenadas == null
					&& TipoDocumentoSistema.TIPO_COORDENADAS.equals(d.getTipoDocumento().getId())) {
				d.setEstado(true);
			} else {
				d.setEstado(false);
			}
		}

		if (documentoCoordenadas != null) {
			try {
				documentoCoordenadas.setIdTable(proyectoLicenciamientoAmbiental.getId());
				documentoCoordenadas.setDescripcion(Constantes.NOMBRE_DOCUMENTO_COORDENADAS_PROYECTO);
				documentoCoordenadas.setEstado(true);
				documentoCoordenadas = documentosFacade.guardarDocumentoAlfresco(
						proyectoLicenciamientoAmbiental.getCodigo(), Constantes.CARPETA_COORDENADAS, 0L,
						documentoCoordenadas, TipoDocumentoSistema.TIPO_COORDENADAS, null);
			} catch (ServiceException ex) {
				LOG.error("Error al actualizar los ficheros.", ex);
			}
		} 
		if (minerosArtesanales != null) {
			for (MineroArtesanal minero : minerosArtesanales) {

				try {
					if (minero.getContratoOperacion() != null) {
						minero.getContratoOperacion().setIdTable(proyectoLicenciamientoAmbiental.getId());
						minero.getContratoOperacion().setDescripcion(Constantes.NOMBRE_DOCUMENTO_CONTRATO_OPERACION);
						Documento contratoOperacion = documentosFacade.guardarDocumentoAlfresco(
								proyectoLicenciamientoAmbiental.getCodigo(), Constantes.CARPETA_CONTRATOS_OPERACION, 0L,
								minero.getContratoOperacion(), TipoDocumentoSistema.TIPO_CONTRATO_OPERACION, null);
						minero.setContratoOperacion(contratoOperacion);
					}
					if (minero.getRegistroMineroArtesanal() != null) {
						minero.getRegistroMineroArtesanal().setIdTable(proyectoLicenciamientoAmbiental.getId());
						minero.getRegistroMineroArtesanal()
								.setDescripcion(Constantes.NOMBRE_DOCUMENTO_REGISTRO_MINERO_ARTESANAL);
						Documento registroMinero = documentosFacade.guardarDocumentoAlfresco(
								proyectoLicenciamientoAmbiental.getCodigo(), Constantes.CARPETA_REGISTROS_MINEROS, 0L,
								minero.getRegistroMineroArtesanal(), TipoDocumentoSistema.TIPO_REGISTRO_MINERO, null);
						minero.setRegistroMineroArtesanal(registroMinero);
					}
				} catch (ServiceException exception) {
					LOG.error("Error al salvar en el alfresco", exception);
				}

				crudServiceBean.saveOrUpdate(minero);
				ProyectoMineroArtesanal proyectoMineroArtesanal = new ProyectoMineroArtesanal();
				proyectoMineroArtesanal.setMineroArtesanal(minero);
				proyectoMineroArtesanal.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				crudServiceBean.saveOrUpdate(proyectoMineroArtesanal);
			}
		}

		if (proyectoLicenciamientoAmbiental.getConcesionesMineras() != null) {
			for (ConcesionMinera concesionMinera : proyectoLicenciamientoAmbiental.getConcesionesMineras()) {
				List<ConcesionMineraUbicacionGeografica> ubicacionesConcesion = concesionMinera
						.getConcesionesUbicacionesGeograficas();
				concesionMinera.setConcesionesUbicacionesGeograficas(null);
				crudServiceBean.delete(concesionMinera);
				for (ConcesionMineraUbicacionGeografica concesionMineraUbicacionGeografica : ubicacionesConcesion)
					crudServiceBean.delete(concesionMineraUbicacionGeografica);
				concesionMinera.setConcesionesUbicacionesGeograficas(ubicacionesConcesion);
			}
		}
		if (concesionesMineras != null) {
			for (ConcesionMinera concesionMinera : concesionesMineras) {
				concesionMinera.setEstado(true);
				concesionMinera.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				List<ConcesionMineraUbicacionGeografica> ubicacionesConcesion = concesionMinera
						.getConcesionesUbicacionesGeograficas();
				concesionMinera.setConcesionesUbicacionesGeograficas(null);
				concesionMinera.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				crudServiceBean.saveOrUpdate(concesionMinera);
				if (ubicacionesConcesion != null) {
					for (ConcesionMineraUbicacionGeografica concesionMineraUbicacionGeografica : ubicacionesConcesion) {
						concesionMineraUbicacionGeografica.setConcesionMinera(concesionMinera);
						crudServiceBean.saveOrUpdate(concesionMineraUbicacionGeografica);
					}
					concesionMinera.setConcesionesUbicacionesGeograficas(ubicacionesConcesion);
				}
			}
		}

		if (concesionesMineras != null)
			proyectoLicenciamientoAmbiental.setConcesionesMineras(concesionesMineras);
		proyectoLicenciamientoAmbiental.setProyectoUbicacionesGeograficas(ubicacionesProyecto);

		try {
			Area area = new Area();
			area=entidadResponsableFacade.obtenerEntidadResponsable(proyectoLicenciamientoAmbiental);
//			if(area.getTipoArea().getId()==3 &&(area.getId()==584 || area.getId()==580 || area.getId()==577 || area.getId()==576 || area.getId()==579)){
			if (area.getHabilitarArea()==null){
				area.setHabilitarArea(false);
	        }
			if (area.getTipoArea().getId()==3  && (area.getHabilitarArea()==true)){
				proyectoLicenciamientoAmbiental.setAreaResponsable(area);
			}else if (area.getTipoArea().getId()==3) {
				proyectoLicenciamientoAmbiental.setAreaResponsable(null);
			}else {
				proyectoLicenciamientoAmbiental.setAreaResponsable(area);
			}
			crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental);
		} catch (Exception e) {
			LOG.error("Error al guardar el proyecto.", e);
		}
		
		for (ProyectoCamaronera proyectoCamaronera : listProyectoCamaroneras) {
			try {
				if(proyectoCamaronera.getDocumento()!=null){
				Documento documentoConsecionCamaronera= new Documento();
				proyectoCamaronera.getDocumento().setIdTable(proyectoLicenciamientoAmbiental.getId());
				documentoConsecionCamaronera = documentosFacade.guardarDocumentoAlfresco(
						proyectoLicenciamientoAmbiental.getCodigo(), Constantes.CARPETA_CONSECION_CAMARONERA, 0L,
						proyectoCamaronera.getDocumento(), TipoDocumentoSistema.TIPO_CONSECION_CAMARONERA, null);
						proyectoCamaronera.setDocumento(documentoConsecionCamaronera);
				}
				
				proyectoCamaronera.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				crudServiceBean.saveOrUpdate(proyectoCamaronera);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}

	public boolean actualizarProyecto(ProyectoLicenciamientoAmbiental proyecto){
		if (proyecto.getId() != null){
			this.crudServiceBean.saveOrUpdate(proyecto);
			return true;
		}else{
			return false;
		}
	}
	
	public boolean actualizarProyectoCamaroneras(List<ProyectoCamaronera> proyectoCamaroneras){
		boolean actualizarProyectoCamaronera=false;
		if (proyectoCamaroneras != null){
			for (ProyectoCamaronera proyectoCamaronera : proyectoCamaroneras) {
				proyectoCamaronera.setEstado(false);
				this.crudServiceBean.saveOrUpdate(proyectoCamaronera);
				actualizarProyectoCamaronera=true;
			}
			return actualizarProyectoCamaronera;
		}else{
			return actualizarProyectoCamaronera;
		}
	}
	
	public void  eliminarProyecto4Cat(String proyecto, String motivo) {
		String sqlPproyecto="select id from dblink('"+dblinkSuiaVerde+"','select p.id from proyectolicenciaambiental p  where p.id=''"+proyecto+"'') as t1 (id character varying(255))";		
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		if(queryProyecto.getResultList().size()>0){
			 String sqlActualizaProyecto="select dblink_exec('"+dblinkSuiaVerde+"','update proyectolicenciaambiental set motivoeliminar=''"+motivo+"'', fechaestadoproyecto =''now()'', estadoproyecto =''false'' where id=''"+proyecto+"''') as result";
		        Query queryActualizaProyecto = crudServiceBean.getEntityManager().createNativeQuery(sqlActualizaProyecto);
		        if(queryActualizaProyecto.getResultList().size()>0)
		        	queryActualizaProyecto.getSingleResult();	
		}
			
		
	}

	public void ejecutarMigracionProyecto(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental,
			Usuario usuario) throws Exception {
		if (Constantes.getAppIntegrationSuiaEnabled() && Constantes.getAppIntegrationSuiaETLEnabled()) {
			Integer idCategoria = proyectoLicenciamientoAmbiental.getCatalogoCategoria().getCategoria().getId();
			if (idCategoria == Categoria.CATEGORIA_III || idCategoria == Categoria.CATEGORIA_IV) {
				try {
					String pentaho = Constantes.getAppIntegrationPentahoDir();
					String fileToRun = pentaho + "migrarproyecto/job_migrar_suiaiii_4categorias.kjb";				

					String linux = pentaho + "kitchen.sh";
					String[] linuxArguments = new String[]{
							" -file=" + fileToRun,
							" -level=Debug",
							" -param:param_codigo_categoria="+proyectoLicenciamientoAmbiental.getCatalogoCategoria().getCodigo(),
							" -param:param_codigo_proyecto="+proyectoLicenciamientoAmbiental.getCodigo(),
							" -param:param_usuario="+proyectoLicenciamientoAmbiental.getUsuario().getNombre()
					};
					
					String windows = pentaho + "Kitchen.bat";
					String[] windowsArguments = new String[] { " /file:" + fileToRun, " /level:Minimal" };

					if (SystemUtils.IS_OS_WINDOWS)
						integracionFacade.executeExternalCommand(windows, windowsArguments);
					else
						integracionFacade.executeExternalCommand(linux, linuxArguments);

					boolean isHidrocarburos = false;
					try {
						isHidrocarburos = integracionFacade.isProjectHydrocarbons(
								proyectoLicenciamientoAmbiental.getCodigo(), usuario.getNombre(),
								usuario.getPasswordSha1Base64());
					} catch (Exception e) {
					}

					if (isHidrocarburos)
						IntegracionFacade.sendExternalCallHidrocarburos(usuario.getNombre(),
								usuario.getPasswordSha1Base64(), IntegrationActions.registrar_proyecto, "idDelProyecto",
								proyectoLicenciamientoAmbiental.getCodigo());
					else
						IntegracionFacade.sendExternalCallSuia(usuario.getNombre(), usuario.getPasswordSha1Base64(),
								IntegrationActions.registrar_proyecto, "idDelProyecto",
								proyectoLicenciamientoAmbiental.getCodigo());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Usuario getRepresentanteProyecto(int idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		List<Usuario> result = (List<Usuario>) crudServiceBean
				.findByNamedQuery(ProyectoLicenciamientoAmbiental.GET_REPRESENTANTE_PROYECTO, parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<FaseDesecho> getFasesDesecho() {
		return (List<FaseDesecho>) crudServiceBean.findAll(FaseDesecho.class);
	}

	@SuppressWarnings("unchecked")
	public List<SustanciaQuimica> getSustanciasQuimicas() {
		return (List<SustanciaQuimica>) crudServiceBean.findAll(SustanciaQuimica.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoForma> getTiposFormas() {
		return (List<TipoForma>) crudServiceBean.findAll(TipoForma.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoEmisionInclusionAmbiental> getTiposEmisionesInclusionAmbiental() {
		return (List<TipoEmisionInclusionAmbiental>) crudServiceBean.findAll(TipoEmisionInclusionAmbiental.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoEstudio> getTiposEstudios() {
		@SuppressWarnings("rawtypes")
		List estudios = crudServiceBean.findAll(TipoEstudio.class);
		if (estudios != null) {
			Collections.sort(estudios);
		}
		return (List<TipoEstudio>) estudios;
	}

	@SuppressWarnings("unchecked")
	public List<TipoUsoSustancia> getTiposUsosSustancias() {
		return (List<TipoUsoSustancia>) crudServiceBean.findAll(TipoUsoSustancia.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoUbicacion> getTiposUbicaciones() {
		return (List<TipoUbicacion>) crudServiceBean.findAll(TipoUbicacion.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoSector> getTiposSectores() {
		return (List<TipoSector>) crudServiceBean.findAll(TipoSector.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoMaterial> getTiposMateriales() {
		return (List<TipoMaterial>) crudServiceBean.findAll(TipoMaterial.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoPoblacion> getTiposPoblaciones() {
		return (List<TipoPoblacion>) crudServiceBean.findAll(TipoPoblacion.class);
	}

	public TipoSector getTipoSector(int id) {
		return crudServiceBean.find(TipoSector.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Categoria> getCategorias() {
		List<Categoria> result = (List<Categoria>) crudServiceBean.findAll(Categoria.class);
		Collections.sort(result, new Comparator<Categoria>() {

			@Override
			public int compare(Categoria o1, Categoria o2) {
				if (o1.getId() > o2.getId())
					return 1;
				return 0;
			}
		});
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Bloque> getBloques(String filter) {
		if (filter == null || filter.trim().isEmpty())
			return (List<Bloque>) crudServiceBean.findAll(Bloque.class);
		else {
			filter = filter.toLowerCase();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("filter", "%" + filter + "%");
			return (List<Bloque>) crudServiceBean.findByNamedQuery(Bloque.FILTRAR, params);
		}
	}

	public String getIdentificacion(Integer idUsuario) throws ServiceException {
		return usuarioFacade.obtenerCedulaNaturalJuridico(idUsuario);
	}

	/*public ProyectoLicenciamientoAmbiental getProyectoPorCodigo(String codigo) {
		try {
			ProyectoLicenciamientoAmbiental proyecto = (ProyectoLicenciamientoAmbiental) crudServiceBean
					.getEntityManager()
					.createQuery("From ProyectoLicenciamientoAmbiental p where p.codigo =:codigo and p.estado = true")
					.setParameter("codigo", codigo).getSingleResult();
			try {
				proyecto.getUsuario().getId();
				proyecto.getUsuario().getPersona().getId();
			} catch (Exception e) {
			}

			return proyecto;
		} catch (Exception e) {
			return null;
		}
	}*/

	/***
	 * @Autor Denis Linares
	 * @param codigo
	 * @return
	 */
	public ProyectoLicenciamientoAmbiental getProyectoPorCodigo(String codigo) {
		try {
			ProyectoLicenciamientoAmbiental proyecto = (ProyectoLicenciamientoAmbiental) crudServiceBean
					.getEntityManager()
					.createQuery("From ProyectoLicenciamientoAmbiental p where p.codigo =:codigo and p.estado = true")
					.setParameter("codigo", codigo).getSingleResult();
			try {
				proyecto.getUsuario().getId();
				proyecto.getUsuario().getPersona().getId();
				proyecto.getCatalogoCategoria().getId();
				proyecto.getCatalogoCategoria().getCategoria().getId();
			} catch (Exception e) {
			}

			return proyecto;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public UbicacionesGeografica getUbicacionProyectoPorIdProyecto(Integer idProyecto) {
		List<UbicacionesGeografica> ubicacionProyecto = (List<UbicacionesGeografica>) crudServiceBean.getEntityManager()
				.createQuery(
						"select u.ubicacionesGeografica From ProyectoUbicacionGeografica u where u.proyectoLicenciamientoAmbiental.id =:idProyecto and u.estado = true")
				.setParameter("idProyecto", idProyecto).getResultList();

		if (ubicacionProyecto != null && !ubicacionProyecto.isEmpty()) {
			return ubicacionProyecto.get(0);
		} else {
			return null;
		}
	}

	public ProyectoLicenciamientoAmbiental getProyectoPorId(Integer idProyecto) {
		try {
			ProyectoLicenciamientoAmbiental proyecto = (ProyectoLicenciamientoAmbiental) crudServiceBean
					.getEntityManager()
					.createQuery("From ProyectoLicenciamientoAmbiental p where p.id =:id and p.estado = true")
					.setParameter("id", idProyecto).getSingleResult();
			/*
			 * proyecto.getUsuario().getPersona() .setContactos(getContactosPorPersona
			 * (proyecto.getUsuario().getPersona().getId()));
			 */
			return proyecto;
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean verPanCam(String codigo) {
		CatalogoCategoriaSistema catalogoCategoriaSistema = (CatalogoCategoriaSistema) crudServiceBean
				.getEntityManager()
				.createQuery(
						"from CatalogoCategoriaSistema c where c.codigo=:codigo")
				.setParameter("codigo", codigo).getSingleResult();
		if (catalogoCategoriaSistema.getCodigo().equals("11.03.04")
				|| catalogoCategoriaSistema.getCodigo().equals("11.03.03")
				|| catalogoCategoriaSistema.getCodigo().equals("11.03.05")
				|| catalogoCategoriaSistema.getCodigo().equals("11.03.06")
				|| catalogoCategoriaSistema.getCodigo().equals("11.03.01")) {
			return true;
		} else {
			return false;
		}
	}

	public Camaroneras verificarOficioViabilidadcamaronera(String acuerdo) {
		Camaroneras camaroneras= new Camaroneras();
		try {			
			camaroneras = (Camaroneras) crudServiceBean.getEntityManager()
					.createQuery("from Camaroneras c where c.acuerdo=:acuerdo")
					.setParameter("acuerdo", acuerdo).getSingleResult();
		} catch (Exception e) {
		}
		return camaroneras;
	}
		
	@SuppressWarnings("unchecked")
	public Integer consultaTaskhyd4cat (String codigo) {
		Integer resultado=0;
		String consultaTaskhyd="select count (dbid_ )from dblink('"+dblinkSuiaVerde+"', "
				+ "'select dbid_ from  jbpm4_hist_task where execution_ like ''%"+codigo+"%'''"
				+ ") t1 (dbid_ integer)";		    			
		List<BigInteger> lista = crudServiceBean.getEntityManager().createNativeQuery(consultaTaskhyd).getResultList();	
		if (!(lista.get(0).intValue()==0)){
			resultado=1;
		}		    		
		return resultado;
		
	}
	
	@SuppressWarnings("unchecked")
	public Integer consultaTaskhyd(String codigo) {
		Integer resultado=0;	    			
		String consultaTaskhyd="select count (id )from dblink('"+dblinkSuiaHidro+"', "
				+ "'select id from  variableinstancelog where value= ''"+codigo+"'''"
				+ ") t1 (id integer)";		    			
		List<BigInteger> lista = crudServiceBean.getEntityManager().createNativeQuery(consultaTaskhyd).getResultList();
		if (!(lista.get(0).intValue()==0)){
			resultado=1;
		}		    		
		return resultado;
		
	}
	
	/**
	 * Busca los proyectos por area y categoria 
	 * @param area
	 * @param listadoCategorias
	 * @return
	 */
	public List<ProyectoCustom> listarProyectosMineriaPendienteTdr(Area area, List<String> listadoCategorias, String listadoTiposDocumentos) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("areaId", area.getId());
		params.put("listadoCategorias", listadoCategorias);

		String queryString = "SELECT DISTINCT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a, "
				+ "(select * from dblink('"+dblinkBpmsSuiaiii+"',' select v.value "
						+ "from  task t,variableinstancelog v, processinstancelog p ,i18ntext i "
						+ "where v.processinstanceid=t.processinstanceid and "
						+ "p.processinstanceid=t.processinstanceid and "
						+ "i.task_names_id=t.id and "
						+ "(p.processid = ''"+Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL+"'' or p.processid = ''"+Constantes.NOMBRE_PROCESO_PARTICIPACION_SOCIAL+"'') and "
						+ "t.status in(''Reserved'',''InProgress'') and "
						+ "v.variableid=''tramite''') as t1 (tramite character varying(255))) as t "
				+ "WHERE p.pren_status = true AND p.area_id = a.area_id AND a.area_id = :areaId AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id "
				+ "AND cs.cacs_code in :listadoCategorias "
				+ "AND t.tramite = P.pren_code "
				+ "AND p.pren_id not in (SELECT DISTINCT docu_table_id FROM suia_iii.documents where docu_status = true and doty_id in "+listadoTiposDocumentos+")"
				+ "ORDER BY p.pren_code";

        List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

        List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
        for (Object object : result) {
            projects.add(new ProyectoCustom((Object[]) object));
        }

        return projects;
    }
	
	@SuppressWarnings("unchecked")
	public List<EstadoAprobacionTdrCatalogo> recuperarEstadosPorTipo(Integer tipo) throws Exception {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tipo", tipo);
		
		List<EstadoAprobacionTdrCatalogo> listaEstados = (List<EstadoAprobacionTdrCatalogo>) crudServiceBean
				.findByNamedQuery(
						EstadoAprobacionTdrCatalogo.LISTAR_ESTADOS_POR_ORDEN,
						parameters);

		return listaEstados;

	}
	
	/**
	 * listados de procesos archivados
	 * @param usuario
	 * @return
	 */
	public List<ProyectoCustom> getAllProjectsArchivedByUser(Usuario usuario) {
		boolean admin = Usuario.isUserInRole(usuario, "admin");
		//boolean autoridad = Usuario.isUserInRole(usuario, "AUTORIDAD AMBIENTAL")||/*Usuario.isUserInRole(usuario, "AUTORIDAD AMBIENTAL2 MAE") ||*/ Usuario.isUserInRole(usuario, "AUTORIDAD AMBIENTAL MAE");
		
		List<String> idsProyectos = new ArrayList<String>();
		Set<ProyectoCustom> proyectos = new HashSet<ProyectoCustom>();
		
		/*if(admin||autoridad)
		{*/
			List<ProyectoCustom> projectsByUser = listarProyectosLicenciamientoAmbientalArchivados(usuario, admin);

			for (ProyectoCustom proyectoLicenciamientoAmbiental : projectsByUser) {
				if (!idsProyectos.contains(proyectoLicenciamientoAmbiental.getCodigo()))
					proyectos.add(proyectoLicenciamientoAmbiental);
			}
		//}
		

		return new ArrayList<ProyectoCustom>(proyectos);
	}
	
	@SuppressWarnings("unchecked")
	private List<ProyectoCustom> listarProyectosLicenciamientoAmbientalArchivados(Usuario usuario, boolean admin) {
		List<Object> result;
		List<Object> lista;
		String provincia=usuario.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec();
		if (admin){
			String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, ps.prsu_desciption, a.area_abbreviation, a.area_name "
					+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a, suia_iii.process_suspended ps "
					+ "WHERE p.pren_delete_reason='SUSPENDIDO' and p.pren_code = ps.prsu_code and p.pren_status = FALSE "
					+ "AND p.area_id = a.area_id AND "
					+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id ORDER BY p.pren_code";

			result = crudServiceBean.findByNativeQuery(queryString, null);
			
			String consultaProyectosArchivadosIVCategorias="select * from dblink('"+dblinkSuiaVerde+"', "
					+ "'select 0,p.id,p.nombre,p.fecharegistro,cc.cata_sector,case cc.ca_categoria when ''III'' then ''Licencia Ambiental'' when ''IV'' then ''Licencia Ambiental'' when ''II'' then ''Registro Ambiental'' else ''Certificado Ambiental''end ,p.motivoeliminar,''N/D'',p.descripcion from proyectolicenciaambiental p, catalogo_categoria cc"
					+ "	where p.id_catalogo=cc.id_catalogo and p.eliminadopor is not null and p.estadoproyecto=false'"
					+ ") t1 (id Integer, codigo varchar,nombre varchar,fecha timestamp,sector varchar,categoria varchar,eliminar varchar,areaSiglas varchar,areaNombre varchar)";		   			
			lista = crudServiceBean.getEntityManager().createNativeQuery(consultaProyectosArchivadosIVCategorias).getResultList();
			
		}else {
			Map<String, Object> params = new HashMap<String, Object>();		
			
			List<Integer> areasId = new ArrayList<Integer>();
			for(AreaUsuario au : usuario.getListaAreaUsuario()){
				areasId.add(au.getArea().getId());
			}
						
			params.put("areaId", areasId);

			String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, ps.prsu_desciption, a.area_abbreviation, a.area_name "
					+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a, suia_iii.process_suspended ps "
					+ "WHERE p.pren_delete_reason='SUSPENDIDO' and p.pren_code = ps.prsu_code  AND p.pren_status = FALSE and p.area_id IN :areaId "
					+ "AND p.area_id = a.area_id AND "
					+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND "
					+ "st.sety_id = p.sety_id ORDER BY p.pren_code";

				result = crudServiceBean.findByNativeQuery(queryString, params);
				
				String consultaProyectosArchivadosIVCategorias="select * from dblink('"+dblinkSuiaVerde+"', "
						+ "'select 0,p.id,p.nombre,p.fecharegistro,cc.cata_sector,case cc.ca_categoria when ''III'' then ''Licencia Ambiental'' when ''IV'' then ''Licencia Ambiental'' "
						+ "when ''II'' then ''Registro Ambiental'' else ''Certificado Ambiental''end ,p.motivoeliminar,''N/D'',p.descripcion "
						+ "from proyectolicenciaambiental p inner join ubicacion u on p.id=u.proyecto_id inner join parroquia pr on u.parroquia=pr.id, "
						+ "catalogo_categoria cc where p.id_catalogo=cc.id_catalogo and p.eliminadopor is not null and p.estadoproyecto=false and pr.parroquia_inec like ''"+provincia+"%'''"
						+ ") t1 (id Integer, codigo varchar,nombre varchar,fecha timestamp,sector varchar,categoria varchar,eliminar varchar,areaSiglas varchar,areaNombre varchar)";		   			
				lista = crudServiceBean.getEntityManager().createNativeQuery(consultaProyectosArchivadosIVCategorias).getResultList();
			}
		
			List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
			for (Object object : result) {
				projects.add(new ProyectoCustom((Object[]) object));
			}
			
			for (Object object : lista) {
				projects.add(new ProyectoCustom((Object[]) object));
			}
			return projects;
	}

	public ProyectoLicenciamientoAmbiental getProyectoPorCodigoyNombre(String codigo, String nombre) {
		try {
			ProyectoLicenciamientoAmbiental proyecto = (ProyectoLicenciamientoAmbiental) crudServiceBean
					.getEntityManager()
					.createQuery("From ProyectoLicenciamientoAmbiental p where (p.codigo =:codigo or p.nombre=:nombre) and p.estado = true")
					.setParameter("codigo", codigo)
					.setParameter("nombre", nombre)
					.getSingleResult();
			try {
				proyecto.getUsuario().getId();
				proyecto.getUsuario().getPersona().getId();
				proyecto.getCatalogoCategoria().getId();
				proyecto.getCatalogoCategoria().getCategoria().getId();
			} catch (Exception e) {
			}

			return proyecto;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public ProyectoLicenciamientoAmbiental getproyecto4cat_ (String proyecto4cat){
		ProyectoLicenciamientoAmbiental proyecto=new ProyectoLicenciamientoAmbiental();
		String sqlPproyecto="select id from dblink('"+dblinkSuiaVerde+"','select p.id from proyectolicenciaambiental p  where p.id=''"+proyecto4cat+"''') as t1 (id character varying(255))";		
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		if(queryProyecto.getResultList().size()>0){
			proyecto.getCatalogoCategoria().setId(1);
			proyecto.getCatalogoCategoria().getCategoria().setId(1);
		}
		return proyecto;
	}
	
	@SuppressWarnings("unchecked")
	public Object[] getProyecto4cat (String proyecto4cat, String nombreProyecto){
		String sqlPproyecto="select * from dblink('"+dblinkSuiaVerde+"',"
				+ "'select p.id, p.nombre, p.usuarioldap, p.estadoproyecto, p.motivoeliminar, cc.ca_categoria  "
				+ "from proyectolicenciaambiental p  "
				+ "inner join catalogo_categoria cc on cc.id_catalogo = p.id_catalogo "
				+ "where p.id=''"+proyecto4cat+"'' or p.nombre=''"+nombreProyecto+"''') as t1 "
						+ "(id character varying(255), nombre text, usuario character varying(255), estado BOOLEAN, motivo text, categoria character varying(255))";		
		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		if(queryProyecto.getResultList().size()>0){
			List<Object>  resultPro = new ArrayList<Object>();
    		resultPro=queryProyecto.getResultList();
    		return (Object[]) resultPro.get(0);
		}
		return null;
	}
	
	public ProyectoLicenciamientoAmbiental getProyectoArchivadoPorCodigoyNombre(String codigo, String nombre) {
		try {
			ProyectoLicenciamientoAmbiental proyecto = (ProyectoLicenciamientoAmbiental) crudServiceBean
					.getEntityManagerWithOutFilters()
					.createQuery("From ProyectoLicenciamientoAmbiental p where (p.codigo =:codigo or p.nombre=:nombre) and p.estado = FALSE and motivoEliminar='SUSPENDIDO'")
					.setParameter("codigo", codigo)
					.setParameter("nombre", nombre)
					.getSingleResult();

			try {
				proyecto.getUsuario().getId();
				proyecto.getUsuario().getPersona().getId();
				proyecto.getCatalogoCategoria().getId();
				proyecto.getCatalogoCategoria().getCategoria().getId();
			} catch (Exception e) {
			}

			return proyecto;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getTaskId4cat (String proyecto4cat){
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
	public List<Object[]> getDocumentosProyecto4cat (String proyecto4cat){
		String sqlPproyecto="select * from dblink('"+dblinkSuiaVerde+"','select d.id, d.nombre, d.fecha::date, d.key from documento d where d.proyecto=''"+proyecto4cat+"''') as t1 (id integer, nombre character varying(255), fecha date, clave character varying(255))";		
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
	public boolean obtenerProcesoActivo(String codigoProyecto){
		try {
			
			String sql="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select processinstanceid "
	            	+ "from processinstancelog where processinstanceid in  ("
	            	+ "select processinstanceid from variableinstancelog where value = " 
	            	+ "''" + codigoProyecto +"'') and end_date is null and processid = ''mae-procesos.RegistroAmbiental''') "
	            	+ "as (processinstanceid text)"; 
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			
		   	List<String> resultList = (List<String>) query.getResultList();
    		if (resultList.size() > 0) {
    			return true;
    		}else{
    			return false;
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//Cris F: aumento de método para vinculación de rgd
	public ProyectoLicenciamientoAmbiental buscarProyectoPorCodigoCompleto(String codigo){
		try {
			ProyectoLicenciamientoAmbiental proyecto = (ProyectoLicenciamientoAmbiental) crudServiceBean
					.getEntityManager()
					.createQuery("From ProyectoLicenciamientoAmbiental p where p.codigo =:codigo and p.estado = true")
					.setParameter("codigo", codigo).getSingleResult();
			
			return proyecto;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return null;
	}
	public Integer numDiasPorFechaProyecto(Date fechaRegistro){
		String queryString = "(SELECT 1+count (*) as days FROM generate_series(0, (cast(now() as date) - cast('"+fechaRegistro+"' as date)))"
				+ " i WHERE date_part('isodow', cast(now() as date) + i) not in (6,7))";
		
		List<Object> numDias = crudServiceBean.findByNativeQuery(queryString,null);
		if(numDias!=null)
			return ((BigInteger) numDias.get(0)).intValue();
			
		return 0;
	}
	
	public Integer numDiasPorFecha(String fechaRegistro){
		String queryString = "(SELECT 1+count (*) as days FROM generate_series(0, (cast(now() as date) - cast('"+fechaRegistro+"' as date)))"
				+ " i WHERE date_part('isodow', cast(now() as date) + i) not in (6,7))";
		
		List<Object> numDias = crudServiceBean.findByNativeQuery(queryString,null);
		if(numDias!=null)
			return ((BigInteger) numDias.get(0)).intValue();
			
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciamientoAmbiental> listProyectosNoFinalizanRegistro() {
		
		try {
			String queryString = "select pren_id from suia_iii.projects_environmental_licensing where (SELECT 1+count (*) as days "
					+ " FROM generate_series(0, (cast(now() as date) - cast(pren_register_date as date))) i WHERE date_part('isodow', cast(now() as date) + i) "
					+ " not in (6,7))>=90 and pren_status=true and pren_project_finalized=false order by 1 desc  limit 25";
			
			List<Object> idProyectos = crudServiceBean.findByNativeQuery(queryString,null);
			
			List<ProyectoLicenciamientoAmbiental> proyecto= new ArrayList<ProyectoLicenciamientoAmbiental>();
			proyecto =crudServiceBean
					.getEntityManager()
					.createQuery("From ProyectoLicenciamientoAmbiental p where p.id IN :id")
					.setParameter("id", idProyectos)
					.getResultList();
			if (proyecto.size() > 0) {
				return proyecto;
			}
			return null;
			
		} catch (Exception e) {
			return null;
		}
	}
	
	public ProyectoLicenciamientoAmbiental getProyectoPorCodigoSinFiltro(String codigo) {
		try {
			ProyectoLicenciamientoAmbiental proyecto = (ProyectoLicenciamientoAmbiental) crudServiceBean
					.getEntityManagerWithOutFilters()
					.createQuery(
							"From ProyectoLicenciamientoAmbiental p where p.codigo =:codigo ")
					.setParameter("codigo", codigo).getSingleResult();

			try {
				proyecto.getUsuario().getId();
				proyecto.getUsuario().getPersona().getId();
				proyecto.getCatalogoCategoria().getId();
				proyecto.getCatalogoCategoria().getCategoria().getId();
			} catch (Exception e) {
			}

			return proyecto;
		} catch (Exception e) {
			return null;
		}
	}
	public Integer getProyectoPorCodigo(String codigo,boolean estado) {
		try {
			Integer proyectoId = (Integer) crudServiceBean
					.getEntityManager()
					.createNativeQuery("select pren_id from suia_iii.projects_environmental_licensing p where p.pren_code ='"+codigo+"' and p.pren_status = "+estado)					
					.getSingleResult();
			return proyectoId;
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<ProyectoCustom> getProjectsSuiaAzulHidrocarburosByUsuario(Usuario usuario) {
		boolean admin = Usuario.isUserInRole(usuario, "admin");

		List<String> idsProyectos = new ArrayList<String>();
		Set<ProyectoCustom> proyectos = new HashSet<ProyectoCustom>();
		List<ProyectoCustom> projectsByUser = listarProyectosLicenciamientoAmbiental(usuario, admin);

		try {
			if (Constantes.getAppIntegrationSuiaEnabled()) {
				List<Object> result = getProjects4CatByUsuario(usuario.getNombre());
				if (result != null) {
					for (Object object : result) {
						idsProyectos.add(object.toString());
					}
				}
			}
		} catch (Exception ex) {
			LOG.error("Error al recuperar los proyectos de cuatro categorias por usuario.", ex);

		}

		//esto se añade para coa
		List<ProyectoCustom> listaCoa = listarProyectosCoa(usuario, false, admin, false, false);
		for (ProyectoCustom item : listaCoa) {
			proyectos.add(item);
			idsProyectos.add(item.getCodigo());
		}
		//fin de esto se añade para coa
		// para procesos digiotalizados
		List<ProyectoCustom> listaDigitalizados = listarProyectosDigitalizados(usuario, admin);
		for (ProyectoCustom item : listaDigitalizados) {
			if(!idsProyectos.contains(item.getCodigo())){
				proyectos.add(item);
				idsProyectos.add(item.getCodigo());	
			}
		}
		//fin de esto se añade para coa
		for (ProyectoCustom proyectoLicenciamientoAmbiental : projectsByUser) {
			if (!idsProyectos.contains(proyectoLicenciamientoAmbiental.getCodigo()))
				proyectos.add(proyectoLicenciamientoAmbiental);
		}

		return new ArrayList<ProyectoCustom>(proyectos);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getProjects4CatByUsuario (String usuario){
		String sql="select * from dblink('"+dblinkSuiaVerde+"',"
                + "'select  DISTINCT p.id from proyectolicenciaambiental p "
                + "inner join jbpm4_hist_task t on split_part(execution_, ''.'', 2) = p.id "
                + "where p.usuarioldap = ''"+usuario+"'' ') as (id character varying)";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object>  resultPro = queryProceso.getResultList();
		
		if(resultPro.size()>0){
    		return resultPro;
		}
		return null;
	}

	public boolean esGranjasAcuicolas(Integer idActividad) {
		CatalogoCategoriaSistema catalogoCategoriaSistema = (CatalogoCategoriaSistema) crudServiceBean
				.getEntityManager()
				.createQuery(
						"from CatalogoCategoriaSistema c where c.id=:id")
				.setParameter("id", idActividad).getSingleResult();
		if (catalogoCategoriaSistema.getCodigo().equals("11.03.04")
				|| catalogoCategoriaSistema.getCodigo().equals("11.03.03")) {
			return true;
		} else {
			return false;
		}
	}
	
//	---RDG recuperar si esta arhivado -WR
	public boolean getRGDArchivado(Integer prenId) {
		boolean estado=false;
		try {			
			estado = (boolean) crudServiceBean
					.getEntityManager()
					.createNativeQuery("select ps.prsu_suspended from suia_iii.hazardous_wastes_generators rg,suia_iii.process_suspended ps "
							+ "where rg.hwge_request=ps.prsu_code and rg.pren_id="+prenId+"")					
					.getSingleResult();
			return estado;
		} catch (Exception e) {			
			return estado;
		}
	}
	public boolean getRGDArchivado(String codigoRGD) {
		boolean estado=false;
		try {			
			estado = (boolean) crudServiceBean
					.getEntityManager()
					.createNativeQuery("select prsu_suspended from suia_iii.process_suspended where prsu_code='"+codigoRGD+"'")					
					.getSingleResult();
			return estado;
		} catch (Exception e) {			
			return estado;
		}
	}
	
	// CF: consulta para obtener proyectos iniciados en suia y terminados de
	// forma física
	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciamientoAmbiental> listProyectosPorOperador(String identificacion) {

		try {
			String queryString = "SELECT o FROM ProyectoLicenciamientoAmbiental o" + " WHERE o.estado = true"
					+ " AND (o.catalogoCategoria.categoria.id=" + Categoria.CATEGORIA_III
					+ " OR o.catalogoCategoria.categoria.id=" + Categoria.CATEGORIA_IV + ")"
					+ " AND o.fechaRegistro >= '2018-04-12 00:00:00.000'" + " AND o.usuario.nombre='" + identificacion
					+ "'" + " ORDER BY o.fechaRegistro desc";

			Query query = crudServiceBean.getEntityManager().createQuery(queryString);

			List<ProyectoLicenciamientoAmbiental> proyecto = query.getResultList();

			if (proyecto != null && !proyecto.isEmpty()) {
				return proyecto;
			}

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<EntityFichaCompletaRgd> getProyectosLicenciamientoNoFinalizadosPorProponente(String cedulaProponente) {
   		try {
   			Map<String, Object> parametros = new HashMap<String, Object>();
   			String like = "";
   			like = " LOWER(u.user_name) like LOWER(:cedulaProponente)";
   			parametros.put("cedulaProponente", "%" + cedulaProponente + "%");
   			
   			StringBuilder sb = new StringBuilder();
			sb.append("select CAST(pren_id AS varchar(255)), area_abbreviation, pren_code, pren_name, CAST(pren_register_date AS varchar), "
					+ "sety_name, CAST(2 AS varchar) AS sistema, CAST('licencia' AS varchar) AS tipo "
					+ "from suia_iii.projects_environmental_licensing p "
					+ "join suia_iii.sector_types s on s.sety_id = p.sety_id "
					+ "join areas a on a.area_id = p.area_id "
					+ "join suia_iii.categories_catalog_system g on g.cacs_id = p.cacs_id "
					+ "join users u on u.user_id = p.user_id "
					+ "where (g.cate_id = " + Categoria.CATEGORIA_III + " or g.cate_id =" + Categoria.CATEGORIA_IV  +") and "
							+ "p.pren_register_date >= '2018-04-12 00:00:00.000' and " +like).append(" ORDER BY pren_code DESC ");
   			return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityFichaCompletaRgd.class, parametros);

   		} catch (Exception e) {
   			e.printStackTrace();
   			return null;
   		}
   	}    
	

	@SuppressWarnings("unchecked")
	public List<Coordenada> listaCoordenadasPorShape(FormaProyecto forma) {

		List<Coordenada> listaCoordenadas = new ArrayList<Coordenada>();

		try {
			String queryString = "SELECT o FROM Coordenada o" + " WHERE o.estado = true and o.formasProyecto.id = "
					+ forma.getId();

			Query query = crudServiceBean.getEntityManager().createQuery(queryString);

			listaCoordenadas = query.getResultList();

			if (listaCoordenadas != null && !listaCoordenadas.isEmpty()) {
				return listaCoordenadas;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaCoordenadas;
	}

	public List<ProyectoCustom> listarProyectosLicenciamientoAmbientalFinalizadosPorUsuario(Usuario usuario) {
		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE p.pren_status = true AND p.pren_project_finalized = true AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND p.user_id = " + usuario.getId() + " AND "
				+ "st.sety_id = p.sety_id ORDER BY p.pren_code";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}

		return projects;
	}
	
	public List<ProyectoCustom> listarProyectosCoa (Usuario usuario, boolean paginaAdmin,
			boolean rolAdmin,  boolean rolEnte, boolean rolPatrimonio) {
		CriteriaBuilder cb = crudServiceBean.getEntityManager().getCriteriaBuilder();

		Metamodel m = crudServiceBean.getEntityManager().getMetamodel();
//		EntityType<ProyectoLicenciaCoa> consultaProyectoLicencia = m.entity(ProyectoLicenciaCoa.class);
		EntityType<ProyectoLicenciaCuaCiuu> consultaProyectoLicencia = m.entity(ProyectoLicenciaCuaCiuu.class);
		CriteriaQuery<ProyectoCustom> cq = cb.createQuery(ProyectoCustom.class);

//		Root<ProyectoLicenciaCoa> rootProyectoLicencia = cq.from(consultaProyectoLicencia);
		Root<ProyectoLicenciaCuaCiuu> rootProyectoCiiu = cq.from(consultaProyectoLicencia);
		Join<ProyectoLicenciaCuaCiuu, ProyectoLicenciaCoa> rootProyectoLicencia = rootProyectoCiiu.join("proyectoLicenciaCoa", JoinType.INNER);
		cq.orderBy(cb.asc(rootProyectoLicencia.get("codigoUnicoAmbiental")));
		Join<ProyectoLicenciaCoa, Area> area = rootProyectoLicencia.join("areaResponsable", JoinType.INNER);
		Join<Area, TipoArea> Tipoarea = area.join("tipoArea", JoinType.INNER);
		Join<ProyectoLicenciaCoa, Categoria> categoria = rootProyectoLicencia.join("categoria", JoinType.INNER);
		
		Join<ProyectoLicenciaCuaCiuu, CatalogoCIUU> catalogo = rootProyectoCiiu.join("catalogoCIUU", JoinType.INNER);
		Join<ProyectoLicenciaCuaCiuu, TipoSector> tipoSector = catalogo.join("tipoSector", JoinType.INNER);
		
		Predicate predicado = cb.conjunction();
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("primario"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("estadoRegistro"), true));
		if(rolAdmin) {
			
		} else {
			if(paginaAdmin) {
				List<Integer> rolEnteList = new ArrayList<Integer>();
				Expression<Integer> inExpression = Tipoarea.get("id");
				if(!rolEnte && !rolPatrimonio) {
					rolEnteList.add(2);
					predicado = cb.and(predicado, inExpression.in(rolEnteList));
				} else {
					if(rolEnte) {
						rolEnteList.add(3);
						rolEnteList.add(4);
						predicado = cb.and(predicado, inExpression.in(rolEnteList));
					}
					if (rolPatrimonio) {
						Predicate predicado1 = cb.equal(rootProyectoLicencia.get("interecaSnap"), true);
						Predicate predicado2 = cb.or(predicado1, cb.equal(rootProyectoLicencia.get("interecaBosqueProtector"), true));
						Predicate predicado3 = cb.or(predicado2, cb.equal(rootProyectoLicencia.get("interecaPatrimonioForestal"), true));
						
						predicado = cb.and(predicado, predicado3);
					}
				}
			} else {
				predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("usuario"), usuario));
				
				//No muestra los tramites archivados por viabilidad
				Predicate predicado1 = cb.notEqual(rootProyectoLicencia.get("tieneViabilidadFavorable"), false);
				Predicate predicado2 = cb.or(predicado1, cb.isNull(rootProyectoLicencia.get("tieneViabilidadFavorable")));
				predicado = cb.and(predicado, predicado2);
				//fin archivo
			}
		}
		
		cq.multiselect(rootProyectoLicencia.get("id"), rootProyectoLicencia.get("codigoUnicoAmbiental"),
				rootProyectoLicencia.get("nombreProyecto"), rootProyectoLicencia.get("fechaGeneracionCua"),
				tipoSector.get("nombre"), categoria.get("nombrePublico"),
				rootProyectoLicencia.get("razonEliminacion"),
				area.get("areaAbbreviation"), area.get("areaName"))
				.where(predicado);

		return this.crudServiceBean.getEntityManager().createQuery(cq).getResultList();
	}
	
	public List<ProyectoCustom> listarProyectosCoa (String nombre, String codigo, String sector,String siglasResponsable, String permiso, Usuario usuario, 
			boolean rolAdmin,  boolean rolEnte, boolean rolPatrimonio, boolean delimitado, Integer limite, Integer offset) {
		CriteriaBuilder cb = crudServiceBean.getEntityManager().getCriteriaBuilder();

		Metamodel m = crudServiceBean.getEntityManager().getMetamodel();
		EntityType<ProyectoLicenciaCuaCiuu> consultaProyectoLicencia = m.entity(ProyectoLicenciaCuaCiuu.class);
		CriteriaQuery<ProyectoCustom> cq = cb.createQuery(ProyectoCustom.class);

		Root<ProyectoLicenciaCuaCiuu> rootProyectoCiiu = cq.from(consultaProyectoLicencia);
		Join<ProyectoLicenciaCuaCiuu, ProyectoLicenciaCoa> rootProyectoLicencia = rootProyectoCiiu.join("proyectoLicenciaCoa", JoinType.INNER);
		cq.orderBy(cb.asc(rootProyectoLicencia.get("codigoUnicoAmbiental")));
		Join<ProyectoLicenciaCoa, Area> area = rootProyectoLicencia.join("areaResponsable", JoinType.INNER);
		Join<ProyectoLicenciaCoa, Categoria> categoria = rootProyectoLicencia.join("categoria", JoinType.INNER);
		
		Join<ProyectoLicenciaCuaCiuu, CatalogoCIUU> catalogo = rootProyectoCiiu.join("catalogoCIUU", JoinType.INNER);
		Join<ProyectoLicenciaCuaCiuu, TipoSector> tipoSector = catalogo.join("tipoSector", JoinType.INNER);
		
		Predicate predicado = cb.conjunction();
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("primario"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("estado"), true));
//		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("estadoRegistro"), true));
		
		if (!nombre.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoLicencia.<String>get("nombreProyecto")),  "%" + nombre.toLowerCase() + "%"));
		}
		if (!codigo.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoLicencia.<String>get("codigoUnicoAmbiental")),  "%" + codigo.toLowerCase() + "%"));
		}
		if (!sector.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(tipoSector.<String>get("nombre")),  "%" + sector.toLowerCase() + "%"));
		}
		if (!siglasResponsable.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(area.<String>get("areaAbbreviation")),  "%" + siglasResponsable.toLowerCase() + "%"));
		}
		if (!permiso.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(categoria.<String>get("nombrePublico")),  "%" + permiso.toLowerCase() + "%"));
		}
		
		if(!rolAdmin) {			
			List<Integer> listaAreasUsuario  = new ArrayList<Integer>();
			
			for(AreaUsuario areaUs : usuario.getListaAreaUsuario()){
				listaAreasUsuario.add(areaUs.getArea().getId());
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
					
					Predicate predicado1 = cb.equal(rootProyectoLicencia.get("interecaSnap"), false);
					Predicate predicado2 = cb.or(predicado1, cb.equal(rootProyectoLicencia.get("interecaBosqueProtector"), false));
					Predicate predicado3 = cb.or(predicado2, cb.equal(rootProyectoLicencia.get("interecaPatrimonioForestal"), false));
					
					predicado = cb.and(predicado, predicado3);
				}
				if (rolPatrimonio) {
					Predicate predicado1 = cb.equal(rootProyectoLicencia.get("interecaSnap"), true);
					Predicate predicado2 = cb.or(predicado1, cb.equal(rootProyectoLicencia.get("interecaBosqueProtector"), true));
					Predicate predicado3 = cb.or(predicado2, cb.equal(rootProyectoLicencia.get("interecaPatrimonioForestal"), true));
					
					predicado = cb.and(predicado, predicado3);
				}
			}
		}
		
		cq.multiselect(rootProyectoLicencia.get("id"), rootProyectoLicencia.get("codigoUnicoAmbiental"),
				rootProyectoLicencia.get("nombreProyecto"), rootProyectoLicencia.get("fechaGeneracionCua"),
				tipoSector.get("nombre"), categoria.get("nombrePublico"),
				rootProyectoLicencia.get("razonEliminacion"),
				area.get("areaAbbreviation"), area.get("areaName"))
				.where(predicado);
		
		if(delimitado)
			return this.crudServiceBean.getEntityManager().createQuery(cq) .setFirstResult(offset).setMaxResults(limite).getResultList();
		else
			return this.crudServiceBean.getEntityManager().createQuery(cq).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoCustom> listarProyectosCoaNoFinalizados (Usuario usuario) {
		
		String queryString = "select p from ProyectoLicenciaCoa p where p.usuario=:usuario and p.estado=true and p.estadoRegistro=false order by p.codigoUnicoAmbiental";
		Query query = crudServiceBean.getEntityManager().createQuery(queryString);
		query.setParameter("usuario", usuario);
		List<ProyectoCustom> proyectosNoFinalizados = new ArrayList<ProyectoCustom>();
		if(query.getResultList().size()>0)
		{
			List<ProyectoLicenciaCoa> lista =query.getResultList();
			for(ProyectoLicenciaCoa proyectos : lista)
			{	
				ProyectoCustom proyecto = new ProyectoCustom(proyectos.getId(), proyectos.getCodigoUnicoAmbiental(), proyectos.getNombreProyecto(), proyectos.getFechaGeneracionCua(), "", "", "", "SN", "SN");
				proyectosNoFinalizados.add(proyecto);
			}
		}
		return proyectosNoFinalizados;
	}
	
	public List<ProyectoCustom> listarProyectosRcoaViablidadNoFavorable(String nombre, String codigo, String sector,String siglasResponsable, String permiso, Usuario usuario, 
			boolean delimitado, Integer limite, Integer offset) {
		CriteriaBuilder cb = crudServiceBean.getEntityManager().getCriteriaBuilder();

		Metamodel m = crudServiceBean.getEntityManager().getMetamodel();
		EntityType<ProyectoLicenciaCuaCiuu> consultaProyectoLicencia = m.entity(ProyectoLicenciaCuaCiuu.class);
		CriteriaQuery<ProyectoCustom> cq = cb.createQuery(ProyectoCustom.class);

		Root<ProyectoLicenciaCuaCiuu> rootProyectoCiiu = cq.from(consultaProyectoLicencia);
		Join<ProyectoLicenciaCuaCiuu, ProyectoLicenciaCoa> rootProyectoLicencia = rootProyectoCiiu.join("proyectoLicenciaCoa", JoinType.INNER);
		cq.orderBy(cb.asc(rootProyectoLicencia.get("codigoUnicoAmbiental")));
//		Join<ProyectoLicenciaCoa, Area> area = rootProyectoLicencia.join("areaResponsable", JoinType.INNER);
		Join<ProyectoLicenciaCoa, Categoria> categoria = rootProyectoLicencia.join("categoria", JoinType.INNER);
		Join<ProyectoLicenciaCoa, ViabilidadCoa> viabilidad = rootProyectoLicencia.join("listaViabilidad", JoinType.INNER);
		Join<ViabilidadCoa, Area> area = viabilidad.join("areaResponsable", JoinType.INNER);
		
		Join<ProyectoLicenciaCuaCiuu, CatalogoCIUU> catalogo = rootProyectoCiiu.join("catalogoCIUU", JoinType.INNER);
		Join<ProyectoLicenciaCuaCiuu, TipoSector> tipoSector = catalogo.join("tipoSector", JoinType.INNER);
		
		Predicate predicado = cb.conjunction();
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("primario"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("estadoRegistro"), true));
		
		if (!nombre.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoLicencia.<String>get("nombreProyecto")),  "%" + nombre.toLowerCase() + "%"));
		}
		if (!codigo.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoLicencia.<String>get("codigoUnicoAmbiental")),  "%" + codigo.toLowerCase() + "%"));
		}
		if (!sector.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(tipoSector.<String>get("nombre")),  "%" + sector.toLowerCase() + "%"));
		}
		if (!siglasResponsable.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(area.<String>get("areaAbbreviation")),  "%" + siglasResponsable.toLowerCase() + "%"));
		}
		if (!permiso.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(categoria.<String>get("nombrePublico")),  "%" + permiso.toLowerCase() + "%"));
		}
		
		List<Integer> rolEnteList  = new ArrayList<Integer>();
		
		for(AreaUsuario areaUs : usuario.getListaAreaUsuario()){
			rolEnteList.add(areaUs.getArea().getId());
		}
						
		Expression<Integer> inExpressionAnd = area.get("id");
		Expression<Integer> inExpressionOr = area.get("area").get("id");
		predicado = cb.and(predicado, inExpressionAnd.in(rolEnteList));		
		
		predicado = cb.or(predicado, inExpressionOr.in(rolEnteList));
		
		
//		predicado = cb.and(predicado, cb.equal(area.get("id"), usuario.getIdArea()));
//		predicado = cb.or(predicado, cb.equal(area.get("area").get("id"), usuario.getIdArea()));
		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("tieneViabilidadFavorable"), false));
		
		Predicate predicado1 = cb.equal(rootProyectoLicencia.get("interecaSnap"), true);
		Predicate predicado2 = cb.or(predicado1, cb.equal(rootProyectoLicencia.get("interecaBosqueProtector"), true));
		Predicate predicado3 = cb.or(predicado2, cb.equal(rootProyectoLicencia.get("interecaPatrimonioForestal"), true));
		
		predicado = cb.and(predicado, predicado3);
		
		cq.multiselect(rootProyectoLicencia.get("id"), rootProyectoLicencia.get("codigoUnicoAmbiental"),
				rootProyectoLicencia.get("nombreProyecto"), rootProyectoLicencia.get("fechaGeneracionCua"),
				tipoSector.get("nombre"), categoria.get("nombrePublico"),
				rootProyectoLicencia.get("razonEliminacion"),
				area.get("areaAbbreviation"), area.get("areaName"))
				.where(predicado).distinct(true);
		
		if(delimitado)
			return this.crudServiceBean.getEntityManager().createQuery(cq) .setFirstResult(offset).setMaxResults(limite).getResultList();
		else
			return this.crudServiceBean.getEntityManager().createQuery(cq).getResultList();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciamientoAmbiental> obtenerCertificadosAmbientales(Area area){
		
		List<ProyectoLicenciamientoAmbiental> listaProyecto = new ArrayList<ProyectoLicenciamientoAmbiental>();
		try {					
			
			String sql="select id, usuario, processinstanceid, tramite, fecha from suia_iii.projects_environmental_licensing inner join dblink('"+dblinkBpmsSuiaiii+"',"
					+ "'select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%DescargarDocumentacion%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.actualowner_id is not null and "
					+ "t.formname is not null and t.processid = ''mae-procesos.CertificadoAmbiental'' ') "
					+ "as tabla2 (id varchar, usuario varchar, processinstanceid varchar, tramite varchar, fecha varchar) "
					+ "on pren_code = tabla2.tramite "
					+ "inner join areas a on suia_iii.projects_environmental_licensing.area_id = a.area_id where a.area_parent_id ='" + area.getId() + "' "
					+ "order by 1";	
			
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1], (String) row[2], (String) row[3], (String) row[4] });
				}
			}
			
			for (String[] codigoProyecto : listaCodigosProyectos) {
				
				ProyectoLicenciamientoAmbiental proyecto = getProyectoPorCodigo(codigoProyecto[3]);
				listaProyecto.add(proyecto);		
				
			}				
			
			return listaProyecto;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaProyecto;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciamientoAmbiental> obtenerCertificadosAmbientalesEA(Area area){
		
		List<ProyectoLicenciamientoAmbiental> listaProyecto = new ArrayList<ProyectoLicenciamientoAmbiental>();
		try {					
			
			String sql="select id, usuario, processinstanceid, tramite, fecha from suia_iii.projects_environmental_licensing inner join dblink('"+dblinkBpmsSuiaiii+"',"
					+ "'select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%DescargarDocumentacion%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.actualowner_id is not null and "
					+ "t.formname is not null and t.processid = ''mae-procesos.CertificadoAmbiental'' ') "
					+ "as tabla2 (id varchar, usuario varchar, processinstanceid varchar, tramite varchar, fecha varchar) "
					+ "on pren_code = tabla2.tramite "
					+ "inner join areas a on suia_iii.projects_environmental_licensing.area_id = a.area_id where a.area_id ='" + area.getId() + "' "
					+ "order by 1";	
			
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1], (String) row[2], (String) row[3], (String) row[4] });
				}
			}
			
			for (String[] codigoProyecto : listaCodigosProyectos) {
				
				ProyectoLicenciamientoAmbiental proyecto = getProyectoPorCodigo(codigoProyecto[3]);
				listaProyecto.add(proyecto);		
				
			}				
			
			return listaProyecto;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaProyecto;
	}
	
	@SuppressWarnings("unchecked")
	public List<String[]> obtenerProcesoId(String tramite){	
		
		List<String[]> lista = new ArrayList<String[]>();
		
		try {					
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"',"
					+ "'select t.id, t.actualowner_id, t.processinstanceid, v.value "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where v.variableid = ''tramite'' and v.value = ''" + tramite + "''"
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.processid = ''mae-procesos.CertificadoAmbiental'' ') "
					+ "as (id varchar, usuario varchar, processinstanceid varchar, tramite varchar) "
					+ "order by 1";	
			
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
			
			lista.addAll(listaCodigosProyectos);
						
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	
	/////////////////proyectos pc/////
	public List<ProyectoCustom> listarProyectosRcoaViablidadNoFavorablePC(String nombre, String codigo, String sector,String siglasResponsable, String permiso, Usuario usuario, 
			boolean delimitado, Integer limite, Integer offset) {
		CriteriaBuilder cb = crudServiceBean.getEntityManager().getCriteriaBuilder();

		Metamodel m = crudServiceBean.getEntityManager().getMetamodel();
		EntityType<ProyectoLicenciaCuaCiuu> consultaProyectoLicencia = m.entity(ProyectoLicenciaCuaCiuu.class);
		CriteriaQuery<ProyectoCustom> cq = cb.createQuery(ProyectoCustom.class);

		Root<ProyectoLicenciaCuaCiuu> rootProyectoCiiu = cq.from(consultaProyectoLicencia);
		Join<ProyectoLicenciaCuaCiuu, ProyectoLicenciaCoa> rootProyectoLicencia = rootProyectoCiiu.join("proyectoLicenciaCoa", JoinType.INNER);
		cq.orderBy(cb.asc(rootProyectoLicencia.get("codigoUnicoAmbiental")));
//		Join<ProyectoLicenciaCoa, Area> area = rootProyectoLicencia.join("areaResponsable", JoinType.INNER);
		Join<ProyectoLicenciaCoa, Categoria> categoria = rootProyectoLicencia.join("categoria", JoinType.INNER);
		Join<ProyectoLicenciaCoa, ViabilidadCoa> viabilidad = rootProyectoLicencia.join("listaViabilidad", JoinType.INNER);
		Join<ViabilidadCoa, Area> area = viabilidad.join("areaResponsable", JoinType.INNER);
		Join<TipoArea, Area> areaTipo = area.join("tipoArea", JoinType.INNER);
		
		Join<ProyectoLicenciaCuaCiuu, CatalogoCIUU> catalogo = rootProyectoCiiu.join("catalogoCIUU", JoinType.INNER);
		Join<ProyectoLicenciaCuaCiuu, TipoSector> tipoSector = catalogo.join("tipoSector", JoinType.INNER);
		
		Predicate predicado = cb.conjunction();
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("primario"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("estadoRegistro"), true));
		
		if (!nombre.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoLicencia.<String>get("nombreProyecto")),  "%" + nombre.toLowerCase() + "%"));
		}
		if (!codigo.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoLicencia.<String>get("codigoUnicoAmbiental")),  "%" + codigo.toLowerCase() + "%"));
		}
		if (!sector.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(tipoSector.<String>get("nombre")),  "%" + sector.toLowerCase() + "%"));
		}
		if (!siglasResponsable.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(area.<String>get("areaAbbreviation")),  "%" + siglasResponsable.toLowerCase() + "%"));
		}
		if (!permiso.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(categoria.<String>get("nombrePublico")),  "%" + permiso.toLowerCase() + "%"));
		}
		
		List<Integer> rolEnteList  = new ArrayList<Integer>();
		
		for(AreaUsuario areaUs : usuario.getListaAreaUsuario()){
			rolEnteList.add(areaUs.getArea().getId());
		}
						
		Expression<Integer> inExpressionAnd = area.get("id");
		Expression<Integer> inExpressionOr = area.get("area").get("id");
		predicado = cb.and(predicado, inExpressionAnd.in(rolEnteList));		
		
		predicado = cb.or(predicado, inExpressionOr.in(rolEnteList));
		
//		predicado = cb.and(predicado, cb.equal(area.get("id"), usuario.getIdArea()));
//		predicado = cb.or(predicado, cb.equal(area.get("area").get("id"), usuario.getIdArea()));
		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("tieneViabilidadFavorable"), false));
		predicado = cb.and(predicado, cb.equal(areaTipo.get("siglas"), "PC"));
		
		Predicate predicado1 = cb.equal(rootProyectoLicencia.get("interecaSnap"), true);
		Predicate predicado2 = cb.or(predicado1, cb.equal(rootProyectoLicencia.get("interecaBosqueProtector"), true));
		Predicate predicado3 = cb.or(predicado2, cb.equal(rootProyectoLicencia.get("interecaPatrimonioForestal"), true));
		
		predicado = cb.and(predicado, predicado3);
		
		cq.multiselect(rootProyectoLicencia.get("id"), rootProyectoLicencia.get("codigoUnicoAmbiental"),
				rootProyectoLicencia.get("nombreProyecto"), rootProyectoLicencia.get("fechaGeneracionCua"),
				tipoSector.get("nombre"), categoria.get("nombrePublico"),
				rootProyectoLicencia.get("razonEliminacion"),
				area.get("areaAbbreviation"), area.get("areaName"))
				.where(predicado).distinct(true);
		
		if(delimitado)
			return this.crudServiceBean.getEntityManager().createQuery(cq) .setFirstResult(offset).setMaxResults(limite).getResultList();
		else
			return this.crudServiceBean.getEntityManager().createQuery(cq).getResultList();
	}
	
	private Integer contarProyectosLicenciamientoAmbientalAdminPC(String nombre, String codigo, String sector,String siglasResponsable, String permiso) {
		String queryString = "SELECT count (*) "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE    p.pren_status = true  AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id AND a.arty_id = 1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		if (!nombre.isEmpty()) {
			params.put("nombre", "%" + nombre + "%");
			queryString += " AND LOWER(p.pren_name) like LOWER(:nombre)";
		}
		if (!codigo.isEmpty()) {
			params.put("codigo", "%" + codigo + "%");
			queryString += " AND  LOWER(p.pren_code) like LOWER(:codigo) ";
		}
		if (!sector.isEmpty()) {
			params.put("sector", "%" + sector + "%");
			queryString += " AND  LOWER(st.sety_name) like LOWER(:sector) ";
		}
		if (!siglasResponsable.isEmpty()) {
			params.put("siglasResponsable", "%" + siglasResponsable + "%");
			queryString += " AND  LOWER(a.area_abbreviation) like LOWER(:siglasResponsable) ";
		}
		if (!permiso.isEmpty()) {
			params.put("permiso", "%" + permiso + "%");
			queryString += " AND  LOWER(ca.cate_public_name) like LOWER(:permiso) ";
			
		}

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

		for (Object object : result) {
			return (((BigInteger) object).intValue());
		}

		return 0;
	}
	
	public Integer contarProyectosLicenciamientoAmbientalPC(Usuario usuario, boolean admin, String nombre,
			String codigo, String sector,String siglasResponsable, String permiso) {
		if (admin)
			return contarProyectosLicenciamientoAmbientalAdminPC(nombre, codigo, sector, siglasResponsable, permiso);
		else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", usuario.getId());
			String like = "";
			if (!nombre.isEmpty()) {
				params.put("nombre", "%" + nombre + "%");
				like += " AND LOWER(p.pren_name) like LOWER(:nombre) ";
			}
			if (!codigo.isEmpty()) {
				params.put("codigo", "%" + codigo + "%");
				like += " AND  LOWER(p.pren_code) like LOWER(:codigo) ";
			}
			
			String queryString = "SELECT count(*) "
					+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
					+ " WHERE   p.user_id = :userId AND p.pren_status = true  AND p.area_id = a.area_id AND "
					+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id AND a.arty_id = 1 " + like;

			List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

			for (Object object : result) {
				return (((BigInteger) object).intValue());
			}

			return 0;
		}
	}
	
	public List<ProyectoCustom> listarProyectosCoaPC(String nombre, String codigo, String sector,String siglasResponsable, String permiso, Usuario usuario, 
			boolean rolAdmin,  boolean rolEnte, boolean rolPatrimonio, boolean delimitado, Integer limite, Integer offset) {
		CriteriaBuilder cb = crudServiceBean.getEntityManager().getCriteriaBuilder();

		Metamodel m = crudServiceBean.getEntityManager().getMetamodel();
		EntityType<ProyectoLicenciaCuaCiuu> consultaProyectoLicencia = m.entity(ProyectoLicenciaCuaCiuu.class);
		CriteriaQuery<ProyectoCustom> cq = cb.createQuery(ProyectoCustom.class);

		Root<ProyectoLicenciaCuaCiuu> rootProyectoCiiu = cq.from(consultaProyectoLicencia);
		Join<ProyectoLicenciaCuaCiuu, ProyectoLicenciaCoa> rootProyectoLicencia = rootProyectoCiiu.join("proyectoLicenciaCoa", JoinType.INNER);
		cq.orderBy(cb.asc(rootProyectoLicencia.get("codigoUnicoAmbiental")));
		Join<ProyectoLicenciaCoa, Area> area = rootProyectoLicencia.join("areaResponsable", JoinType.INNER);
		Join<ProyectoLicenciaCoa, Categoria> categoria = rootProyectoLicencia.join("categoria", JoinType.INNER);
		Join<TipoArea, Area> areaTipo = area.join("tipoArea", JoinType.INNER);
		
		Join<ProyectoLicenciaCuaCiuu, CatalogoCIUU> catalogo = rootProyectoCiiu.join("catalogoCIUU", JoinType.INNER);
		Join<ProyectoLicenciaCuaCiuu, TipoSector> tipoSector = catalogo.join("tipoSector", JoinType.INNER);
		
		Predicate predicado = cb.conjunction();
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("primario"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoCiiu.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("estado"), true));
		predicado = cb.and(predicado, cb.equal(areaTipo.get("siglas"), "PC"));
//		predicado = cb.and(predicado, cb.equal(rootProyectoLicencia.get("estadoRegistro"), true));
		
		if (!nombre.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoLicencia.<String>get("nombreProyecto")),  "%" + nombre.toLowerCase() + "%"));
		}
		if (!codigo.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(rootProyectoLicencia.<String>get("codigoUnicoAmbiental")),  "%" + codigo.toLowerCase() + "%"));
		}
		if (!sector.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(tipoSector.<String>get("nombre")),  "%" + sector.toLowerCase() + "%"));
		}
		if (!siglasResponsable.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(area.<String>get("areaAbbreviation")),  "%" + siglasResponsable.toLowerCase() + "%"));
		}
		if (!permiso.isEmpty()) {
			predicado = cb.and(predicado, cb.like(cb.lower(categoria.<String>get("nombrePublico")),  "%" + permiso.toLowerCase() + "%"));
		}
		
		if(!rolAdmin) {			
			List<Integer> listaAreasUsuario  = new ArrayList<Integer>();
			
			for(AreaUsuario areaUs : usuario.getListaAreaUsuario()){
				listaAreasUsuario.add(areaUs.getArea().getId());
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
					
					Predicate predicado1 = cb.equal(rootProyectoLicencia.get("interecaSnap"), false);
					Predicate predicado2 = cb.or(predicado1, cb.equal(rootProyectoLicencia.get("interecaBosqueProtector"), false));
					Predicate predicado3 = cb.or(predicado2, cb.equal(rootProyectoLicencia.get("interecaPatrimonioForestal"), false));
					
					predicado = cb.and(predicado, predicado3);
				}
				if (rolPatrimonio) {
					Predicate predicado1 = cb.equal(rootProyectoLicencia.get("interecaSnap"), true);
					Predicate predicado2 = cb.or(predicado1, cb.equal(rootProyectoLicencia.get("interecaBosqueProtector"), true));
					Predicate predicado3 = cb.or(predicado2, cb.equal(rootProyectoLicencia.get("interecaPatrimonioForestal"), true));
					
					predicado = cb.and(predicado, predicado3);
				}
			}
		}
		
		cq.multiselect(rootProyectoLicencia.get("id"), rootProyectoLicencia.get("codigoUnicoAmbiental"),
				rootProyectoLicencia.get("nombreProyecto"), rootProyectoLicencia.get("fechaGeneracionCua"),
				tipoSector.get("nombre"), categoria.get("nombrePublico"),
				rootProyectoLicencia.get("razonEliminacion"),
				area.get("areaAbbreviation"), area.get("areaName"))
				.where(predicado);
		
		if(delimitado)
			return this.crudServiceBean.getEntityManager().createQuery(cq) .setFirstResult(offset).setMaxResults(limite).getResultList();
		else
			return this.crudServiceBean.getEntityManager().createQuery(cq).getResultList();
	}
	
	public List<ProyectoCustom> listarProyectosLicenciamientoAmbientalPaginadoPC(Usuario usuario, boolean admin,
			Integer inicio, Integer total, String nombre, String codigo, String sector,String siglasResponsable, String permiso) {
		if (admin)
			return listarProyectosLicenciamientoAmbientalAdminPaginadoPC(inicio, total, nombre, codigo, sector, siglasResponsable, permiso);
		else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", usuario.getId());
			params.put("total", total);
			params.put("inicio", inicio);

			String like = "";
			if (!nombre.isEmpty()) {
				params.put("nombre", "%" + nombre + "%");
				like += " AND LOWER(p.pren_name) like LOWER(:nombre)";
			}
			if (!codigo.isEmpty()) {
				params.put("codigo", "%" + codigo + "%");
				like += " AND  LOWER(p.pren_code) like LOWER(:codigo) ";
			}

			String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, "
					+ "p.pren_delete_reason, a.area_abbreviation, a.area_name "
					+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, "
					+ "suia_iii.categories_catalog_system cs, areas a "
					+ "WHERE  p.user_id = :userId AND p.pren_status = true AND p.area_id = a.area_id AND "
					+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id AND a.arty_id = 1 " + like
					+ " ORDER BY p.pren_id desc LIMIT :total OFFSET :inicio";

			List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

			List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
			for (Object object : result) {
				projects.add(new ProyectoCustom((Object[]) object));
			}

			return projects;
		}
	}
	
	private List<ProyectoCustom> listarProyectosLicenciamientoAmbientalAdminPaginadoPC(Integer inicio, Integer total,
			String nombre, String codigo, String sector,String siglasResponsable, String permiso) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("total", total);
		params.put("inicio", inicio);

		String like = "";
		if (!nombre.isEmpty()) {
			params.put("nombre", "%" + nombre + "%");
			like += " AND LOWER(p.pren_name) like LOWER(:nombre)";
		}
		if (!codigo.isEmpty()) {
			params.put("codigo", "%" + codigo + "%");
			like += " AND  LOWER(p.pren_code) like LOWER(:codigo) ";
		}
		if (!sector.isEmpty()) {
			params.put("sector", "%" + sector + "%");
			like += " AND  LOWER(st.sety_name) like LOWER(:sector) ";
		}
		if (!siglasResponsable.isEmpty()) {
			params.put("siglasResponsable", "%" + siglasResponsable + "%");
			like += " AND  LOWER(a.area_abbreviation) like LOWER(:siglasResponsable) ";
		}
		if (!permiso.isEmpty()) {
			params.put("permiso", "%" + permiso + "%");
			like += " AND  LOWER(ca.cate_public_name) like LOWER(:permiso) ";
		}

		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, "
				+ "ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, "
				+ "suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE   p.pren_status = true  AND p.area_id = a.area_id AND "
				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id AND a.arty_id = 1 " + like
				+ " ORDER BY p.pren_id desc LIMIT :total OFFSET :inicio";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);

		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}

		return projects;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean tieneProcesoRgdIniciado(String tramite){
		try {					
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"',"
					+ "'select v.processinstanceid from variableinstancelog v "
					+ "inner join processinstancelog p on p.processinstanceid = v.processinstanceid "
					+ "where v.variableid = ''tramite'' "
					+ "and v.value = ''" + tramite + "'' and v.processid = ''" + Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS + "'' "
					+ "and p.status != 4 ') "
					+ "as (id varchar) "
					+ "order by 1";	
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			if (resultList.size() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean tieneProcesoArtIniciado(String tramite, String usuario){
		try {					
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"',"
					+ "'select v1.value from variableinstancelog v1 "
					+ "where v1.processinstanceid in ("
					+ "select v.processinstanceid from variableinstancelog v "
					+ "inner join processinstancelog p on p.processinstanceid = v.processinstanceid "
					+ "where v.variableid = ''codigoProyecto'' "
					+ "and v.value = ''" + tramite + "'' and v.processid = ''" + Constantes.NOMBRE_PROCESO_APROBACION_REQUISITOS_TECNICOS + "'' "
					+ "and p.status = 1) "
					+ "and v1.variableid = ''proponente'' ' )"
					+ "as (id varchar) "
					+ "order by 1";	
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			if (resultList.size() > 0) {
				for (Object object : resultList) {
					String proponente = object.toString();
					if(proponente.equals(usuario))
						return true;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<String[]> obtenerProcesoIdPorTramite(String tramite){	
		
		List<String[]> lista = new ArrayList<String[]>();
		
		try {					
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"',"
					+ "'select t.id, t.processinstanceid, v.value "
					+ "from processinstancelog t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where v.variableid = ''tramite'' and v.value = ''" + tramite + "'' and t.status = 2') "
					+ "as (id varchar, processinstanceid varchar, tramite varchar) "
					+ "order by 1";	
			
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
			
			lista.addAll(listaCodigosProyectos);
						
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public String AutoridadAmbientalProyecto(ProyectoLicenciamientoAmbiental proyecto) {


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

    public List<EntityFichaCompletaRgd> getProyectosFinalizadosRCoa(String cedulaProponente) {
   		try {
   			Map<String, Object> parametros = new HashMap<String, Object>();
   			String sqlProyecto="SELECT cast(a.prco_id as varchar), b.area_abbreviation, prco_cua, prco_name, cast(prco_creation_date as text), sety_name, CAST(1 as varchar) AS sistema, case prco_categorizacion when 2 then 'Registro Ambiental' else 'Licencia Ambiental' end  AS tipo "
   					+ " FROM coa_mae.project_licencing_coa a inner join areas b on a.area_id_forest_inventory = b.area_id "
   					+ " inner join coa_mae.project_licencing_coa_ciuu c on a.prco_id = c.prco_id "
   					+ " inner join coa_mae.catalog_ciuu d on c.caci_id = d.caci_id "
   					+ " inner join suia_iii.sector_types e on d.sety_id = e.sety_id "
   					+ " inner join ( select * from dblink('"+dblinkBpmsSuiaiii+"', 'select v.value "
   					+ " from  variableinstancelog v inner join processinstancelog p on v.processinstanceid = p.processinstanceid "
   					+ " where p.status = 2 "
   					+ " and v.variableid=''tramite'' "
   					+ " and p.processid in (''rcoa.RegistroAmbiental'', ''rcoa.ResolucionLicenciaAmbiental'') "
   					+ " ') as t1 (tramite character varying(255))) as t on a.prco_cua = t.tramite "
   					+ " where prco_status = true and prco_categorizacion in (2, 3, 4) and prli_primary = true "
   					+ " and a.prco_creator_user = '"+cedulaProponente+"' ";
   		
   			return ejecutarSentenciasNativas.listarPorSentenciaSql(sqlProyecto, EntityFichaCompletaRgd.class, parametros);

   		} catch (Exception e) {
   			e.printStackTrace();
   			return null;
   		}
   	}

    public List<EntityFichaCompletaRgd> getProyectosDigitalizados(String cedulaProponente) {
   		try {
   			Map<String, Object> parametros = new HashMap<String, Object>();
   			String sqlProyecto="select a.enaa_id, b.area_abbreviation, enaa_project_code, enaa_project_name, CAST(enaa_resolution_date AS varchar), sety_name, CAST(6 as varchar) AS sistema, enaa_environmental_administrative_authorization, a.enaa_resolution, "
   					+ " case enaa_status_finalized when true then 'Completado' else 'Por completar' end as estado "
   					+ "from coa_digitalization_linkage.environmental_administrative_authorizations a inner join areas b on a.area_id_aaa = b.area_id "
   					+ "inner join suia_iii.sector_types c on a.sety_id = c.sety_id "
   					+ "inner join users u on a.user_id = u.user_id "
   					+ "where enaa_status = true and enaa_history = false "
   					+ " and u.user_name = '"+cedulaProponente+"' ";
   		
   			return ejecutarSentenciasNativas.listarPorSentenciaSql(sqlProyecto, EntityFichaCompletaRgd.class, parametros);

   		} catch (Exception e) {
   			e.printStackTrace();
   			return null;
   		}
   	}

    public EntityFichaCompletaRgd getProyectosDigitalizadoPorCodigo(String cedulaProponente, String codigo) {
   		try {
   			Map<String, Object> parametros = new HashMap<String, Object>();
   			String sqlProyecto="select a.enaa_id, b.area_abbreviation, enaa_project_code, enaa_project_name, CAST(enaa_resolution_date AS varchar), sety_name, CAST(6 as varchar) AS sistema, enaa_environmental_administrative_authorization, a.enaa_resolution, "
   					+ " case enaa_status_finalized when true then 'Completado' else 'Por completar' end as estado "
   					+ "from coa_digitalization_linkage.environmental_administrative_authorizations a inner join areas b on a.area_id_aaa = b.area_id "
   					+ "inner join suia_iii.sector_types c on a.sety_id = c.sety_id "
   					+ "inner join users u on a.user_id = u.user_id "
   					+ "where enaa_status = true and enaa_history = false "
   					+ " and u.user_name = '"+cedulaProponente+"' and enaa_project_code = '"+codigo+"' ";
   		 
   			List<EntityFichaCompletaRgd> litaRgd = ejecutarSentenciasNativas.listarPorSentenciaSql(sqlProyecto, EntityFichaCompletaRgd.class, parametros);
   			if(litaRgd != null && litaRgd.size() > 0)
   				return litaRgd.get(0);
   			return null;
   		} catch (Exception e) {
   			e.printStackTrace();
   			return null;
   		}
   	}
    
	public ProyectoCustom buscarProyectoPorCodigoTipo(String codigo){
    	try {
    		
    		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, "
    				+ "p.pren_delete_reason, p.pren_deactivation_date, p.caca_id, p.pren_project_finalized " //falta p.pren_status
    				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.categories ca, "
    				+ "suia_iii.categories_catalog_system cs "
    				+ "WHERE p.pren_status = false  AND  "
    				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id and (ca.cate_id = 3 or ca.cate_id = 4) AND "
    				+ "p.pren_code = '" + codigo + "'";

    		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

    		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
    		for (Object object : result) {
    			projects.add(new ProyectoCustom((Object[]) object, 1));
    		}
    		    		
			if (projects != null && !projects.isEmpty()) {
				return projects.get(0);
			}

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ProyectoCustom buscarProyectoPorCodigo(String codigo){
    	try {
    		
    		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, "
    				+ "p.pren_delete_reason, pren_deactivation_date "
    				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.categories ca, "
    				+ "suia_iii.categories_catalog_system cs "
    				+ "WHERE p.pren_status = false  AND  "
    				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id and (ca.cate_id = 3 or ca.cate_id = 4) AND "
    				+ "p.pren_code = '" + codigo + "'";

    		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

    		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
    		for (Object object : result) {
    			projects.add(new ProyectoCustom((Object[]) object, 1));
    		}
    		    		
			if (projects != null && !projects.isEmpty()) {
				return projects.get(0);
			}

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ProyectoCustom buscarProyectoCoa(String codigo){
    	try {
    		
    		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, "
    				+ "p.pren_delete_reason, pren_deactivation_date "
    				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.categories ca, "
    				+ "suia_iii.categories_catalog_system cs "
    				+ "WHERE p.pren_status = false  AND  "
    				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id and (ca.cate_id = 3 or ca.cate_id = 4) AND "
    				+ "p.pren_code = '" + codigo + "'";

    		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

    		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
    		for (Object object : result) {
    			projects.add(new ProyectoCustom((Object[]) object, 1));
    		}
    		    		
			if (projects != null && !projects.isEmpty()) {
				return projects.get(0);
			}

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ProyectoCustom buscarProyectoPorCodigoTipoArchivo(String codigo){
    	try {
    		
    		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, "
    				+ "p.pren_delete_reason, pren_deactivation_date "
    				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.categories ca, "
    				+ "suia_iii.categories_catalog_system cs "
    				+ "WHERE p.pren_status = true  AND  "
    				+ "cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id and (ca.cate_id = 1 or ca.cate_id = 2 or ca.cate_id = 3 or ca.cate_id = 4) AND "
    				+ "p.pren_code = '" + codigo + "'";

    		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

    		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
    		for (Object object : result) {
    			projects.add(new ProyectoCustom((Object[]) object, 1));
    		}
    		    		
			if (projects != null && !projects.isEmpty()) {
				return projects.get(0);
			}

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void actualizarEstadoProyectoSuia(Integer id, String motivo){
		try {
			
			String pattern = "yyyy-MM-dd HH:mm";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			
			StringBuilder sql = new StringBuilder();			
			
			sql.append("update suia_iii.projects_environmental_licensing ");
			sql.append("set pren_status = true, pren_deactivation_date = '" + date + "', pren_delete_reason = '" + motivo + "' ");
			sql.append("where pren_id = "+ id);
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);			
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void actualizarEstadoProyectoSuiaArchivo(Integer id, String motivo){
		try {
			
			String pattern = "yyyy-MM-dd HH:mm";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			
			StringBuilder sql = new StringBuilder();			
			
			sql.append("update suia_iii.projects_environmental_licensing ");
			sql.append("set pren_status = false, pren_deactivation_date = '" + date + "', pren_delete_reason = '" + motivo + "' ");
			sql.append("where pren_id = "+ id);
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);			
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	public ProyectoCustom buscaProyectoPorCodigoTipoRcoa(String codigo){
    	try {
    		
    		String queryString = "SELECT p.prco_id, p.prco_cua, p.prco_name,prco_deactivation_date, p.prco_delete_reason, null, p.prco_categorizacion, p.prco_project_finished, p.prco_status  "
    				+ "FROM coa_mae.project_licencing_coa p "
    				+ "WHERE (prco_categorizacion = 3 or prco_categorizacion = 4) "
    				+ "and p.prco_status = false  "
    				+ "and p.prco_cua = '" + codigo + "'";

    		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

    		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
    		for (Object object : result) {
    			projects.add(new ProyectoCustom((Object[]) object, 2));
    		}
    		    		
			if (projects != null && !projects.isEmpty()) {
				return projects.get(0);
			}

			return null;    		

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	
	public ProyectoCustom buscaProyectoRcoa(String codigo){
    	try {
    		
    		String queryString = "SELECT p.prco_id, p.prco_cua, p.prco_name,p.prco_cua_date,p.prco_delete_reason,prco_deactivation_date, p.prco_categorizacion,p.prco_project_finished,p.prco_status, null  "
    				+ "FROM coa_mae.project_licencing_coa p "
    				+ "WHERE p.prco_cua = '" + codigo + "' ";

    		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

    		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
    		for (Object object : result) {
    			projects.add(new ProyectoCustom((Object[]) object, 2));
    		}
    		    		
			if (projects != null && !projects.isEmpty()) {
				return projects.get(0);
			}

			return null;    		

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public void actualizarEstadoProyectoRcoa(Integer id, String motivo){
		try {
			
			String pattern = "yyyy-MM-dd HH:mm";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			
			StringBuilder sql = new StringBuilder();			
			
			sql.append("update coa_mae.project_licencing_coa ");
			sql.append("set prco_status = true, prco_deactivation_date = '" + date + "', prco_delete_reason = '" + motivo + "' ");
			sql.append("where prco_id = "+ id);
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);			
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    @SuppressWarnings("unchecked")
	public List<String[]> obtenerProcesoIdPorCodigo(String codigo){	
		
		List<String[]> lista = new ArrayList<String[]>();
		
		try {					
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"',"
					+ "'select t.id, t.processinstanceid, v.value "
					+ "from processinstancelog t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where v.variableid = ''tramite'' and v.value = ''" + codigo + "'' and t.status = 2') "
					+ "as (id varchar, processinstanceid varchar, tramite varchar) "
					+ "order by 1";	
			
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
			
			lista.addAll(listaCodigosProyectos);
						
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciamientoAmbiental> obtenerRegistroAmbientales(Area area) {
		List<ProyectoLicenciamientoAmbiental> listaProyecto = new ArrayList<>();
		try {
			// Verificación del crudServiceBean y su EntityManager
			if (crudServiceBean == null || crudServiceBean.getEntityManager() == null) {
				throw new IllegalStateException("crudServiceBean o su EntityManager es null");
			}

			// Obtener el ID del área
			Integer areaIdInteger = area.getId();
			if (areaIdInteger == null) {
				throw new IllegalArgumentException("El ID del área no puede ser nulo");
			}

			// Construcción de la consulta SQL utilizando COALESCE para manejar id_zonal o
			// id_area
			String sql = "select distinct on (pren_code) * from (select xx.*, y.cedula  from (select distinct on (pren_code) q.* from (  SELECT DISTINCT ON (pren_code) "
					+ " pren_code,            ar.area_id AS id_area,            ar.area_name, "
					+ "	COALESCE(a2.area_id, ar.area_id) AS id_area_final     FROM suia_iii.projects_environmental_licensing a  "
					+ " INNER JOIN suia_iii.categories_catalog_system ccs ON ccs.cacs_id = a.cacs_id "
					+ " LEFT JOIN areas ar ON ar.area_id = a.area_id "
					+ " LEFT JOIN areas a2 ON ar.area_parent_id = a2.area_id "
					+ " LEFT JOIN suia_iii.projects_document pd ON pd.pren_id = a.pren_id "
					+ " INNER JOIN suia_iii.documents d ON d.docu_id = pd.docu_id "
					+ " LEFT JOIN suia_iii.document_types dt ON dt.doty_id = d.doty_id "
					+ " INNER JOIN suia_iii.sector_types st ON st.sety_id = a.sety_id "
					+ " INNER JOIN suia_iii.categories cat ON cat.cate_id = ccs.cate_id "
					+ " INNER JOIN areas_types at ON ar.arty_id = at.arty_id "
					+ " where  pren_status is true and ccs.cate_id =  2 and "
					+ " a.pren_id not in (select distinct a.pren_id from suia_iii.projects_environmental_licensing a "
					+ " inner join suia_iii.projects_document b on  a.pren_id = b.pren_id "
					+ " inner join suia_iii.documents c on c.docu_id = b.docu_id "
					+ " where  pren_status is true and prdo_status = true and docu_name in ('Resolución del Registro Ambiental.pdf', 'Resolucion_del_Registro_Ambiental.pdf') "
					+ " ) "
					+ " or "
					+ " a.pren_id in (select distinct a.pren_id from suia_iii.projects_environmental_licensing a "
					+ " INNER JOIN suia_iii.projects_document pd ON pd.pren_id = a.pren_id "
					+ " INNER JOIN suia_iii.documents d ON d.docu_id = pd.docu_id "
					+ " where "
					+ " pren_status is true  and pren_for_sing_auth is true and pd.pren_id not in ( "
					+ " select distinct pd.pren_id from "
					+ " suia_iii.projects_document pd "
					+ " left JOIN suia_iii.documents d ON d.docu_id = pd.docu_id "
					+ " where prdo_status = true and docu_name in ('Resolución del Registro Ambiental.pdf', 'Resolucion_del_Registro_Ambiental.pdf') "
					+ " )) "
					+ " order by 1 " 
					+ " ) q  " 
					+ "inner join ( " + "SELECT "
					+ "    codigoProyecto , nombreProceso, estadoProceso, fechaInicioProceso, fechaFinProceso, nombreTarea, estadoTarea, fechaInicioTarea, fechaFinTarea, usuarioTarea "
					+ " FROM dblink (' " + dblinkBpmsSuiaiii + " ', '"
					+ "select " + "  distinct v.value codigoProyecto, " + "  t.processinstanceid processinstanceid, "
					+ "  p.processname as nombreProceso," + "  CASE" + "	WHEN p.status = 1 THEN ''En trámite''"
					+ "	WHEN p.status = 2 THEN ''Completado''" + "	WHEN p.status = 3 THEN ''Abortado''"
					+ "	WHEN p.status = 4 THEN ''Abortado''" + "	ELSE ''Abortado''" + "  END as estadoProceso, "
					+ "  p.start_date  as fechaInicioProceso ," + "  p.end_date as fechaFinProceso,"
					+ "  t.taskname nombreTarea,"
					+ "   case when p.status = ''1'' then ta.status else t.status end as estadoTarea,  "
					+ " case when p.status = ''1'' then activationtime else t.createddate end as fechaInicioTarea, "
					+ " case when p.status = ''1'' then logtime else t.enddate end as fechaFinTarea, "
					+ " case when p.status = ''1'' then coalesce(actualowner_id, createdby_id) else t.userid end as usuarioTarea  "
					+ "from variableinstancelog v"
					+ "  inner join processinstancelog p on p.processinstanceid = v.processinstanceid"
					+ "  left join bamtasksummary t on t.processinstanceid = p.processinstanceid  "
					+ "  left join task ta on p.processinstanceid =ta.processinstanceid and ta.id = t.taskid "
					+ "  left join ("
					+ "    select distinct on (te.taskid) te.id, te.taskid, te.userid, te.logtime  from taskevent te where te.type =''COMPLETED''  "
					+ "  ) te on te.taskid =  t.taskid  " + "  " + "  where " + "    user_identity not like ''%msit%''"
					+ "  and user_identity not like ''%admin%''" + "  and v.variableid=''tramite''"
					+ "  and (case when p.status = ''1'' then ta.status else t.status end) not in ( ''Exited'')"
					+ "  and p.processname in (''Registro ambiental v2'', ''Registro ambiental'', ''Registro Ambiental'', ''Eliminar proyecto'')"
					+ "  and p.processname not in (''Eliminar proyecto'')" 				
					+ "  and p.status in (2)" + "order by 1,7"
					+ "') t1( codigoProyecto text, processinstanceid text, nombreProceso text, estadoProceso text, fechaInicioProceso timestamp, fechaFinProceso timestamp, nombreTarea text, estadoTarea text, "
					+ "             fechaInicioTarea timestamp, fechaFinTarea timestamp, usuarioTarea text"
					+ "            )" + "group by "
					+ "  codigoProyecto , processinstanceid, nombreProceso, estadoProceso, fechaInicioProceso, fechaFinProceso, nombreTarea, estadoTarea, fechaInicioTarea, fechaFinTarea, usuarioTarea"
					+ " order by 1" + ") y  on pren_code = codigoProyecto" + ") xx " + "LEFT JOIN ("
					+ "    SELECT distinct " + "        u.user_name AS cedula, " + "        p.peop_name, "
					+ "        a.area_id, " + "        a.area_name, " + "        r.role_name "
					+ "    FROM areas_users au " + "    INNER JOIN users u ON au.user_id = u.user_id "
					+ "    INNER JOIN people p ON u.user_name = p.peop_pin "
					+ "    INNER JOIN suia_iii.roles_users ru ON au.user_id = ru.user_id "
					+ "    INNER JOIN areas a ON au.area_id = a.area_id "
					+ "    INNER JOIN suia_iii.roles r ON ru.role_id = r.role_id "
					+ "    WHERE au.arus_status = 'true' " + "    AND ru.rous_status = 'true' "
					+ "    AND r.role_name LIKE '%AUTO%' " + "    ORDER BY 1 " + ") y ON y.area_name = xx.area_name "

					+ ") qq " + "WHERE qq.id_area_final = " + areaIdInteger + " " + "order by 1;";

			// Crear la consulta
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);

			System.out.println("SQL=========>" + sql);
			// Ejecutar la consulta
			List<Object[]> resultList = query.getResultList();

			// Conversión de los resultados
			for (Object[] row : resultList) {
				String[] proyectoData = new String[row.length];
				for (int i = 0; i < row.length; i++) {
					proyectoData[i] = row[i] != null ? row[i].toString() : null;
				}
				// Procesamiento de los datos obtenidos
				ProyectoLicenciamientoAmbiental proyecto = getProyectoPorCodigo(proyectoData[0]); // Suponiendo que el
																									// código del
																									// proyecto está en
																									// la posición 0
				if (proyecto != null) {
					listaProyecto.add(proyecto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listaProyecto;
	}
	
	@SuppressWarnings("unchecked")
	public Long obtenerInstanciaProyecto(String codigo){
		try {
			String sql = "SELECT * FROM dblink (' " + dblinkBpmsSuiaiii + " ', '"
					+ "select  distinct p.processinstanceid from processinstancelog p "
					+ " inner join variableinstancelog v "
					+ " on p.processinstanceid = v.processinstanceid "
					+ " where p.processname in (''Registro ambiental v2'', ''Registro ambiental'', ''Registro Ambiental'') "
					+ " and v.variableid = ''tramite'' and v.value = ''"+ codigo +"''  ') "
					+ " as (id varchar) ";
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			
			String idProceso = "";
			if (resultList.size() > 0) {
				idProceso = (String)resultList.get(0);
			}
									
			if(idProceso != ""){
				return Long.valueOf(idProceso);
			}else{
				return null;
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ProyectoCustom buscaProyectoRCOA(String codigo){
    	try {
    		String queryString = "SELECT p.prco_id, p.prco_cua, p.prco_name,p.prco_cua_date,p.prco_delete_reason,prco_deactivation_date, "
    				+ "p.prco_categorizacion, p.prco_project_finished, p.prco_status, u.user_name, p.gelo_id, null  "
    				+ "FROM coa_mae.project_licencing_coa p, public.users u WHERE p.user_id = u.user_id and p.prco_cua = '" + codigo + "' ";
    		
    		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
    		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
    		for (Object object : result) {
    			projects.add(new ProyectoCustom((Object[]) object, true));
    		}
			if (projects != null && !projects.isEmpty()) {
				return projects.get(0);
			}
			return null;    		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	
	public ProyectoCustom buscaProyectoLicencia(String codigo){
    	try {
    		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, p.pren_delete_reason, "
    				+ "p.pren_deactivation_date, cs.cate_id, p.pren_project_finished, p.pren_status, u.user_name, l.gelo_id, null "
    				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.categories ca, suia_iii.categories_catalog_system cs, "
    				+ "suia_iii.projects_locations l, public.users u WHERE cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id "
    				+ "AND p.pren_id = l.pren_id and p.user_id = u.user_id AND p.pren_code = '" + codigo + "'";
 
    		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
    		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
    		for (Object object : result) {
    			projects.add(new ProyectoCustom((Object[]) object, true));
    		}
			if (projects != null && !projects.isEmpty()) {
				return projects.get(0);
			}
			return null;    		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	
	public ProyectoCustom buscaProyectoDigital(String codigo){
    	try {
    		
    		String queryString = "SELECT d.enaa_id, d.enaa_project_code, d.enaa_project_name, d.enaa_creation_date, NULL AS razon_eliminacion, NULL AS fecha_eliminacion, "
    				+ "CASE WHEN d.enaa_environmental_administrative_authorization LIKE '%Certificado%' THEN 1 "
    				+ "WHEN d.enaa_environmental_administrative_authorization LIKE '%Registro%' THEN 2 "
    				+ "WHEN d.enaa_environmental_administrative_authorization LIKE '%Licencia%' THEN 3 ELSE NULL END "
    				+ "AS categoria, d.enaa_status_finalized, d.enaa_status, u.user_name, l.gelo_id, NULL "
    				+ "FROM coa_digitalization_linkage.environmental_administrative_authorizations d, coa_digitalization_linkage.location l, public.users u "
    				+ "WHERE l.enaa_id = d.enaa_id and d.user_id = u.user_id AND d.enaa_project_code = '" + codigo + "' ";

    		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
    		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
    		for (Object object : result) {
    			projects.add(new ProyectoCustom((Object[]) object, true));
    		}
			if (projects != null && !projects.isEmpty()) {
				return projects.get(0);
			}
			return null;    		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
}
