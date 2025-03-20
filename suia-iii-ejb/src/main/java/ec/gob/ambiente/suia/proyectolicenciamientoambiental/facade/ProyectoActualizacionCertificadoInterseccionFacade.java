package ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.DetalleInterseccionProyecto;
import ec.gob.ambiente.suia.domain.FormaProyecto;
import ec.gob.ambiente.suia.domain.HistorialActualizacionCertificadoInterseccion;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class ProyectoActualizacionCertificadoInterseccionFacade {

	private static final Logger LOG = Logger.getLogger(ProyectoActualizacionCertificadoInterseccionFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;

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
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public String dblinkSuiaHidro=Constantes.getDblinkBpmsHyD();
	
	public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();

	public List<ProyectoCustom> listarProyectosPendientesActualizacionCI(Usuario usuario) {
		String queryString;
		Map<String, Object> params = new HashMap<String, Object>();
		queryString = "SELECT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a "
				+ "WHERE p.user_id = :userId AND p.pren_status = true "
				+ " and p.sety_id = st.sety_id and a.area_id = p.area_id and p.cacs_id = cs.cacs_id and cs.cate_id = ca.cate_id "
				+ " AND p.pren_intersection_update_status = 2 ORDER BY p.pren_code";
		params.put("userId", usuario.getId());
	
		List<Object> result = crudServiceBean.findByNativeQuery(queryString,
				params);
	
		List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
		for (Object object : result) {
			projects.add(new ProyectoCustom((Object[]) object));
		}
	
		return projects;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoCustom>  listarProyectos4CategoriasEnTramiteActualizacionCI(String usuario) {
		List<ProyectoCustom> proyectos = new ArrayList<ProyectoCustom>();
		
		String sqlPproyecto = "select * from dblink('" + dblinkSuiaVerde + "',"
				+ "'  SELECT z.codigo, z.nombre, z.fecha_registro, z.sector, z.categoria, z.estrategico "
				+ " FROM( "
				+ " SELECT a.ID AS codigo, a.nombre AS nombre, a.fecharegistro AS fecha_registro, y .ca_categoria AS categoria, y.cata_sector as sector, xxx.nombre as canton, y.estrategico "
				+ " FROM proyectolicenciaambiental a "
				+ " INNER JOIN catalogo_categoria y ON a.id_catalogo = y.id_catalogo "
				+ " LEFT JOIN (SELECT ubicacion_proyecto (MAX (ubicacion.parroquia), 5) AS nombre, ubicacion.proyecto_id FROM ubicacion GROUP BY ubicacion.proyecto_id) as xxx on xxx.proyecto_id = a.id "
				+ " WHERE a.estadoproyecto=true and requiere_actualizacion_certificado_interseccion = 2 and a.usuarioldap = ''" + usuario +"'' "
				+ " ) as z "
				+ "') "
				+ "as t1 (codigo varchar, nombre varchar, fecharegistro date, sector varchar, categoria varchar, "
				+ "estrategico boolean) "
				+ " ORDER BY 1 ";

		Query queryProyecto = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		List<Object[]> result = (List<Object[]>) queryProyecto.getResultList();

		for (Object row : result) {
			Object[] obj = (Object[]) row;
			ProyectoCustom proyecto = getProyectoCustom(obj);
			proyectos.add(proyecto);				
		}
		return proyectos;
	}
	
	public Integer guardarCoordenadasActualizadas(
			ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental,
			List<UbicacionesGeografica> ubicaciones,
			List<CoordinatesWrapper> coordinatesWrappers, List<CoordinatesWrapper> coordinatesWrappersGeo) throws CmisAlfrescoException {
	
		try {
	
			Integer nro_actualizacion = 1;
			List<ProyectoUbicacionGeografica> ubicacionesActualizadas = getUbicacionProyectoActualizadaPorIdProyecto(proyectoLicenciamientoAmbiental
					.getId());
			if (ubicacionesActualizadas != null) {
				nro_actualizacion = ubicacionesActualizadas.get(0).getNroActualizacion() + 1;
			}
			
			List<ProyectoUbicacionGeografica> ubicacionesProyecto = new ArrayList<ProyectoUbicacionGeografica>();
			if (ubicaciones != null) {
				for (UbicacionesGeografica ubicacionesGeografica : ubicaciones) {
					ProyectoUbicacionGeografica proyectoUbicacionGeografica = new ProyectoUbicacionGeografica();
					proyectoUbicacionGeografica.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
					proyectoUbicacionGeografica.setUbicacionesGeografica(ubicacionesGeografica);
					proyectoUbicacionGeografica.setOrden(ubicacionesGeografica.getOrden());
					proyectoUbicacionGeografica.setNroActualizacion(nro_actualizacion);
					crudServiceBean.saveOrUpdate(proyectoUbicacionGeografica);
					ubicacionesProyecto.add(proyectoUbicacionGeografica);
				}
			}
			
			for (int i = 0; i <= coordinatesWrappersGeo.size() - 1; i++) {
				FormaProyecto formaProyecto = new FormaProyecto();
				formaProyecto.setTipoForma(coordinatesWrappersGeo.get(i).getTipoForma());
				formaProyecto.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				formaProyecto.setNroActualizacion(nro_actualizacion);
				formaProyecto.setTipoCoordenada(2);
				crudServiceBean.saveOrUpdate(formaProyecto);
				
				for (Coordenada coordenada : coordinatesWrappersGeo.get(i).getCoordenadas()) {
					coordenada.setEstado(true);
					coordenada.setFormasProyecto(formaProyecto);
				}
				
				String queryCoordenadasGeo = "INSERT INTO suia_iii.coordinates"
						+ "(coor_id, coor_status, coor_description, coor_order, coor_x, coor_y, coor_zone, prsh_id, rpsh_id, coor_update_number, prsh_type, coor_geographic_area)"
						+ "VALUES ";
				for (Coordenada coordenada : coordinatesWrappersGeo.get(i).getCoordenadas()) {
					String id = crudServiceBean.getSecuenceNextValue("seq_coor_id", "suia_iii").toString();
					String zona = (coordenada.getZona() == null) ? "" : coordenada.getZona();
					String queryAdd = "(" + id + ", true, '"
							+ coordenada.getDescripcion() + "', "
							+ coordenada.getOrden() + ", " + coordenada.getX()
							+ ", " + coordenada.getY() + ", '"
							+ zona + "', "
							+ formaProyecto.getId() + ", null, "
							+ nro_actualizacion + ", 2, "+coordenada.getAreaGeografica()+"),";
					queryCoordenadasGeo += queryAdd;
				}
				queryCoordenadasGeo = queryCoordenadasGeo.substring(0, queryCoordenadasGeo.length() - 1);
				queryCoordenadasGeo += ";";

				crudServiceBean.insertUpdateByNativeQuery(queryCoordenadasGeo, null);
				
			}
	
			for (CoordinatesWrapper coordinatesWrapper : coordinatesWrappers) {
				FormaProyecto formaProyecto = new FormaProyecto();
				formaProyecto.setTipoForma(coordinatesWrapper.getTipoForma());
				formaProyecto.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				formaProyecto.setNroActualizacion(nro_actualizacion);
				formaProyecto.setTipoCoordenada(1);
				crudServiceBean.saveOrUpdate(formaProyecto);
	
				for (Coordenada coordenada : coordinatesWrapper.getCoordenadas()) {
					coordenada.setEstado(true);
					coordenada.setFormasProyecto(formaProyecto);
				}
	
				String queryCoordenadas = "INSERT INTO suia_iii.coordinates"
						+ "(coor_id, coor_status, coor_description, coor_order, coor_x, coor_y, coor_zone, prsh_id, rpsh_id, coor_update_number, prsh_type, coor_geographic_area)"
						+ "VALUES ";
				for (Coordenada coordenada : coordinatesWrapper.getCoordenadas()) {
					String id = crudServiceBean.getSecuenceNextValue("seq_coor_id", "suia_iii").toString();
					String zona = (coordenada.getZona() == null) ? "" : coordenada.getZona();
					String queryAdd = "(" + id + ", true, '"
							+ coordenada.getDescripcion() + "', "
							+ coordenada.getOrden() + ", " + coordenada.getX()
							+ ", " + coordenada.getY() + ", '"
							+ zona + "', "
							+ formaProyecto.getId() + ", null, "
							+ nro_actualizacion + ", 1, "+coordenada.getAreaGeografica()+"),";
					queryCoordenadas += queryAdd;
				}
				queryCoordenadas = queryCoordenadas.substring(0, queryCoordenadas.length() - 1);
				queryCoordenadas += ";";
	
				crudServiceBean.insertUpdateByNativeQuery(queryCoordenadas, null);
			}
	
			return nro_actualizacion;
			
		} catch (Exception e) {
			LOG.error("Error al guardar el proyecto.", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoUbicacionGeografica> getUbicacionProyectoActualizadaPorIdProyecto(
			Integer idProyecto) throws ServiceException {
		List<ProyectoUbicacionGeografica> ubicacionesProyecto = (List<ProyectoUbicacionGeografica>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From ProyectoUbicacionGeografica u where u.idProyecto =:idProyecto and u.estado = true "
								+ "and u.nroActualizacion > 0 order by nroActualizacion desc")
				.setParameter("idProyecto", idProyecto).getResultList();
	
		if (ubicacionesProyecto != null && !ubicacionesProyecto.isEmpty()) {
			Integer nro_actualizacion = ubicacionesProyecto.get(0).getNroActualizacion();
			List<ProyectoUbicacionGeografica> ultimasUbicacionesProyecto = (List<ProyectoUbicacionGeografica>) crudServiceBean
					.getEntityManager()
					.createQuery(
							"From ProyectoUbicacionGeografica u where u.idProyecto =:idProyecto and u.estado = true "
									+ "and u.nroActualizacion =:nroActualizacion order by orden")
					.setParameter("idProyecto", idProyecto)
					.setParameter("nroActualizacion", nro_actualizacion)
					.getResultList();
			return ultimasUbicacionesProyecto;
		} else {
			return null;
		}
	}
	
	public void guardarInterseccionesActualizacion(Integer nroActualizacion, HashMap<InterseccionProyecto, List<DetalleInterseccionProyecto>> capasIntersecciones) throws Exception {
		for (InterseccionProyecto interseccion : capasIntersecciones.keySet()) {
			if(interseccion.getCapaCoa() != null) {
				Integer id = Integer.parseInt(crudServiceBean.getSecuenceNextValue("seq_inpr_id", "suia_iii").toString());
	
				String queryInterseccion = "INSERT INTO suia_iii.intersections_project(inpr_id, coa_laye_id, pren_id, inpr_process_date, inpr_buffer_intersection,inpr_layer_description,inpr_status,inpr_update_number) "
						+ "VALUES ";
				String query = "(" + id + ", "
							+ interseccion.getCapaCoa().getId() + ", "
							+ interseccion.getProyecto().getId() + ", now()" + ", "
							+ interseccion.getIntersectaConCapaAmortiguamiento() + ", '" 
							+ interseccion.getDescripcion() + "', true , "
							+ nroActualizacion + " );";
				queryInterseccion += query;
	
				crudServiceBean.insertUpdateByNativeQuery(queryInterseccion, null);
				
				String queryDetalles = "INSERT INTO suia_iii.details_intersection_project (dein_id, inpr_id, dein_geometry_id, dein_geometry_name,dein_status,dein_subsystem_layer) "
						+ "VALUES ";
				for(DetalleInterseccionProyecto detalle : capasIntersecciones.get(interseccion)) {
					String idDetalle = crudServiceBean.getSecuenceNextValue("seq_inde_id", "suia_iii").toString();
					String queryAdd = "(" + idDetalle + ", "
							+ id + ", "
							+ detalle.getIdGeometria() + ", '" + detalle.getNombre()
							+ "', true , null ),";
					queryDetalles += queryAdd;
				}
	
				queryDetalles = queryDetalles.substring(0, queryDetalles.length() - 1);
				queryDetalles += ";";
	
				crudServiceBean.insertUpdateByNativeQuery(queryDetalles, null);
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void eliminarInformacionActualizada(Integer idProyecto, Integer nroActualizacion, Integer idOficio, String idCuatroCategorias) throws Exception {
		//ubicacion geografica actualizada
		Query sqlEliminarUbicacion = crudServiceBean.getEntityManager().
		createQuery("update ProyectoUbicacionGeografica u set u.estado=false where " 
				+ ((idProyecto != null) ? " u.idProyecto =:idProyecto " : " u.idCuatroCategorias =:idProyecto") + " and u.nroActualizacion =:nroActualizacion ")
			.setParameter("idProyecto", (idProyecto != null) ? idProyecto : idCuatroCategorias)
			.setParameter("nroActualizacion", nroActualizacion);
		
		sqlEliminarUbicacion.executeUpdate();
	
		//formas proyecto actualizadas
		List<FormaProyecto> formasProyecto = (List<FormaProyecto>) crudServiceBean
					.getEntityManager()
					.createQuery(
							"From FormaProyecto u where "
							+ ((idProyecto != null) ? " u.idProyecto =:idProyecto " : " u.idCuatroCategorias =:idProyecto") + " and u.estado = true "
									+ "and u.nroActualizacion =:nroActualizacion")
					.setParameter("idProyecto", (idProyecto != null) ? idProyecto : idCuatroCategorias)
					.setParameter("nroActualizacion", nroActualizacion)
					.getResultList();
		
	
		Query sqlEliminarforma = crudServiceBean.getEntityManager().
		createQuery("update FormaProyecto u set u.estado=false where "
			+ ((idProyecto != null) ? " u.idProyecto =:idProyecto " : " u.idCuatroCategorias =:idProyecto") +" and u.nroActualizacion =:nroActualizacion ")
			.setParameter("idProyecto", (idProyecto != null) ? idProyecto : idCuatroCategorias)
			.setParameter("nroActualizacion", nroActualizacion);
		
		sqlEliminarforma.executeUpdate();
	
	
		//coordenadas actualizadas
		for(FormaProyecto forma : formasProyecto){
			Query sqlEliminarCoordenada = crudServiceBean.getEntityManager().
			createQuery("update Coordenada u set u.estado=false where u.formasProyecto.id =:idForma and u.nroActualizacion =:nroActualizacion ")
				.setParameter("idForma", forma.getId())
				.setParameter("nroActualizacion", nroActualizacion);
		
			sqlEliminarCoordenada.executeUpdate();
		}
	
		//certificado de intersección actualizado
		Query sqlEliminarOficio= crudServiceBean.getEntityManager().
		createQuery("update CertificadoInterseccion u set u.estado=false where u.id =:idOficio ")
			.setParameter("idOficio", idOficio);
		
		sqlEliminarOficio.executeUpdate();
	
		//interseccion actualizada
		List<InterseccionProyecto> interseccionesActualizacion = (List<InterseccionProyecto>) crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT ip FROM InterseccionProyecto ip where "
							+ ((idProyecto != null) ? " ip.proyecto.id=:idProyecto " : " ip.idCuatroCategorias =:idProyecto") +" and ip.estado = true "
									+ "and ip.nroActualizacion =:nroActualizacion")
					.setParameter("idProyecto", (idProyecto != null) ? idProyecto : idCuatroCategorias)
					.setParameter("nroActualizacion", nroActualizacion)
					.getResultList();
	
		Query sqlEliminarInterseccion = crudServiceBean.getEntityManager().
				createQuery("update InterseccionProyecto ip set ip.estado=false where "
					+ ((idProyecto != null) ? " ip.proyecto.id=:idProyecto  " : " ip.idCuatroCategorias =:idProyecto") + " and ip.nroActualizacion =:nroActualizacion ")
					.setParameter("idProyecto", (idProyecto != null) ? idProyecto : idCuatroCategorias)
					.setParameter("nroActualizacion", nroActualizacion);
		
		sqlEliminarInterseccion.executeUpdate();
	
	
		for(InterseccionProyecto interseccion : interseccionesActualizacion) {
			Query sqlEliminarDetalle= crudServiceBean.getEntityManager().
				createQuery("update DetalleInterseccionProyecto u set u.estado=false where u.interseccionProyecto.id =:idInterseccion")
					.setParameter("idInterseccion", interseccion.getId());
		
			sqlEliminarDetalle.executeUpdate();
		}
		
	}
	
	public List<ProyectoCustom> listarLicenciasEnTramite(Area area) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("areaId", area.getId());
	
		String queryString = "SELECT DISTINCT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a, "
				+ "(select * from dblink('"+dblinkBpmsSuiaiii+"',' select v.value "
						+ "from  task t,variableinstancelog v, processinstancelog p ,i18ntext i "
						+ "where v.processinstanceid=t.processinstanceid and "
						+ "p.processinstanceid=t.processinstanceid and "
						+ "i.task_names_id=t.id and "
						+ "(p.processid = ''"+Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL+"'') and "
						+ "t.status in(''Reserved'',''InProgress'') and "
						+ "v.variableid=''tramite''') as t1 (tramite character varying(255))) as t "
				+ "WHERE p.pren_status = true AND p.area_id = a.area_id "
				+ "AND (a.area_id = :areaId or a.area_parent_id = :areaId)  "
				+ "AND cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id "
				+ "AND cs.cate_id in (3,4) "
				+ "AND t.tramite = P.pren_code "
				+ "and (pren_intersection_update_status is null or pren_intersection_update_status != 2) "
				+ "ORDER BY p.pren_code";
	
	    List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);
	
	    List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
	    for (Object object : result) {
	        projects.add(new ProyectoCustom((Object[]) object));
	    }
	
	    return projects;
	}
	
	public List<ProyectoCustom> listarLicenciasEnTramite(List<AreaUsuario> areasUsuarios) {
		Map<String, Object> params = new HashMap<String, Object>();
		
		Integer idUsuario = areasUsuarios.get(0).getIdUsuario();
				
		params.put("userId", idUsuario);
	
		String queryString = "SELECT DISTINCT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a, "
				+ "(select * from dblink('"+dblinkBpmsSuiaiii+"',' select v.value "
						+ "from  task t,variableinstancelog v, processinstancelog p ,i18ntext i "
						+ "where v.processinstanceid=t.processinstanceid and "
						+ "p.processinstanceid=t.processinstanceid and "
						+ "i.task_names_id=t.id and "
						+ "(p.processid = ''"+Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL+"'') and "
						+ "t.status in(''Reserved'',''InProgress'') and "
						+ "v.variableid=''tramite''') as t1 (tramite character varying(255))) as t "
				+ "WHERE p.pren_status = true AND p.area_id = a.area_id "
				+ "AND (a.area_id in (select area_id from areas_users where user_id = :userId) or a.area_parent_id in (select area_id from areas_users where user_id = :userId))  "
				+ "AND cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id "
				+ "AND cs.cate_id in (3,4) "
				+ "AND t.tramite = P.pren_code "
				+ "and (pren_intersection_update_status is null or pren_intersection_update_status != 2) "
				+ "ORDER BY p.pren_code";
	
	    List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);
	
	    List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
	    for (Object object : result) {
	        projects.add(new ProyectoCustom((Object[]) object));
	    }
	
	    return projects;
	}
	
	public List<ProyectoCustom> listarLicenciasHidrocarburosEnTramite(Area area) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("areaId", area.getId());
	
		String queryString = "SELECT DISTINCT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a, "
				+ "(select * from dblink('"+dblinkSuiaHidro+"',' select v.value "
						+ "from variableinstancelog v "
						+ "inner join processinstancelog p on p.processinstanceid = v.processinstanceid "
						+ "inner join bamtasksummary t on t.processinstanceid = p.processinstanceid and (v.variableid=''tramite'' or v.variableid=''codigoProyecto'' or v.variableid=''numeroSolicitud'' or v.variableid=''tramite'' )"
						+ "left join task ta on p.processinstanceid =ta.processinstanceid and ta.id = t.taskid "
						+ "left join taskevent te on te.taskid = t.taskid and te.type =''COMPLETED''"
						+ "where  p.status in (1)"
						+ "') as t1 (tramite character varying(255))) as t "
				+ "WHERE p.pren_status = true AND p.area_id = a.area_id "
				+ "AND (a.area_id = :areaId or a.area_parent_id = :areaId)  "
				+ "AND cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id "
				+ "AND cs.cate_id in (3,4) "
				+ "AND t.tramite = P.pren_code "
				+ "AND P .pren_code NOT IN (SELECT codigo_proyecto FROM dblink ('"+dblinkSuiaHidro+"','  select y.processinstanceid, y.codigo_proyecto "
						+ "from (SELECT distinct a.processinstanceid processinstanceid, a.value codigo_proyecto "
						+ "FROM variableinstancelog a "
						+ "inner join variableinstancelog b on a.processinstanceid = b.processinstanceid and b.variableid = ''cantidadNotificaciones'' and b.value =''3'' "
						+ "where a.variableid in (''codigoProyecto'', ''tramite'') "
						+ "and a.oldvalue = '''') y "
						+ "') as t1 (processinstanceid BIGINT, codigo_proyecto TEXT))"
				+ "and (pren_intersection_update_status is null or pren_intersection_update_status != 2) "
				+ "ORDER BY p.pren_code";
	
	    List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);
	
	    List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
	    for (Object object : result) {
	        projects.add(new ProyectoCustom((Object[]) object));
	    }
	
	    return projects;
	}
	
	public List<ProyectoCustom> listarLicenciasHidrocarburosEnTramite(List<AreaUsuario> areasUsuarios) {
		Map<String, Object> params = new HashMap<String, Object>();
				
		params.put("userId", areasUsuarios.get(0).getIdUsuario());
	
		String queryString = "SELECT DISTINCT p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name "
				+ "FROM suia_iii.projects_environmental_licensing p, suia_iii.sector_types st, suia_iii.categories ca, suia_iii.categories_catalog_system cs, areas a, "
				+ "(select * from dblink('"+dblinkSuiaHidro+"',' select v.value "
						+ "from variableinstancelog v "
						+ "inner join processinstancelog p on p.processinstanceid = v.processinstanceid "
						+ "inner join bamtasksummary t on t.processinstanceid = p.processinstanceid and (v.variableid=''tramite'' or v.variableid=''codigoProyecto'' or v.variableid=''numeroSolicitud'' or v.variableid=''tramite'' )"
						+ "left join task ta on p.processinstanceid =ta.processinstanceid and ta.id = t.taskid "
						+ "left join taskevent te on te.taskid = t.taskid and te.type =''COMPLETED''"
						+ "where  p.status in (1)"
						+ "') as t1 (tramite character varying(255))) as t "
				+ "WHERE p.pren_status = true AND p.area_id = a.area_id "
				+ "AND (a.area_id in (select area_id from areas_users where user_id = :userId) or a.area_parent_id in (select area_id from areas_users where user_id = :userId))  "
				+ "AND cs.cacs_id = p.cacs_id AND cs.cate_id = ca.cate_id AND st.sety_id = p.sety_id "
				+ "AND cs.cate_id in (3,4) "
				+ "AND t.tramite = P.pren_code "
				+ "AND P .pren_code NOT IN (SELECT codigo_proyecto FROM dblink ('"+dblinkSuiaHidro+"','  select y.processinstanceid, y.codigo_proyecto "
						+ "from (SELECT distinct a.processinstanceid processinstanceid, a.value codigo_proyecto "
						+ "FROM variableinstancelog a "
						+ "inner join variableinstancelog b on a.processinstanceid = b.processinstanceid and b.variableid = ''cantidadNotificaciones'' and b.value =''3'' "
						+ "where a.variableid in (''codigoProyecto'', ''tramite'') "
						+ "and a.oldvalue = '''') y "
						+ "') as t1 (processinstanceid BIGINT, codigo_proyecto TEXT))"
				+ "and (pren_intersection_update_status is null or pren_intersection_update_status != 2) "
				+ "ORDER BY p.pren_code";
	
	    List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);
	
	    List<ProyectoCustom> projects = new ArrayList<ProyectoCustom>();
	    for (Object object : result) {
	        projects.add(new ProyectoCustom((Object[]) object));
	    }
	
	    return projects;
	}

	@SuppressWarnings("unchecked")
	public List<ProyectoCustom>  listarLicencias4CategoriasEnTramite(Area area) {
		List<ProyectoCustom> proyectos = new ArrayList<ProyectoCustom>();
		
		String sqlPproyecto = "select * from dblink('" + dblinkSuiaVerde + "',"
				+ "'  SELECT z.codigo, z.nombre, z.fecha_registro, z.sector, z.categoria, z.estrategico, z.provincia "
				+ " FROM( "
				+ " SELECT a.ID AS codigo, a.nombre AS nombre, a.fecharegistro AS fecha_registro, y .ca_categoria AS categoria, y.cata_sector as sector, xxx.nombre as provincia, y.estrategico,  "
				+ " t1.totaltareas as total_tareas "
				+ " FROM proyectolicenciaambiental a "
				+ " INNER JOIN catalogo_categoria y ON a.id_catalogo = y.id_catalogo "
				+ " LEFT JOIN (SELECT ubicacion_proyecto (MAX (ubicacion.parroquia), 4) AS nombre, ubicacion.proyecto_id FROM ubicacion GROUP BY ubicacion.proyecto_id) as xxx on xxx.proyecto_id = a.id "
				+ " LEFT JOIN (SELECT substring(b.execution_id_, \"position\"(b.execution_id_, ''.'') + 1, length(b.execution_id_)) as idproy, count(*) as totaltareas  FROM public.jbpm4_task b GROUP BY b.execution_id_ ) t1 ON t1.idproy = a.ID "
				+ " WHERE a.estadoproyecto=true and (requiere_actualizacion_certificado_interseccion is null or requiere_actualizacion_certificado_interseccion != 2)"
				+ " ) as z "
				+ " WHERE "
				+ " z.categoria in (''III'',''IV'') "
				+ " AND z.total_tareas > 0 "
				+ "') "
				+ "as t1 (codigo varchar, nombre varchar, fecharegistro date, sector varchar, categoria varchar, "
				+ "estrategico boolean, provincia varchar ) "
				+ " ORDER BY 1 ";

		Query queryProyecto = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		List<Object[]> result = (List<Object[]>) queryProyecto.getResultList();
		
		List<String> provinciasDz = new ArrayList<>();
		if(area.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES)) {
			String sqlUbicaciones = "SELECT u.gelo_codification_inec FROM public.geographical_locations u where u.area_id_zonal = " +area.getId()+" ";
			Query queryUbicaciones = crudServiceBean.getEntityManager().createNativeQuery(sqlUbicaciones);
			
			provinciasDz = (List<String>) queryUbicaciones.getResultList();
		}

		for (Object row : result) {
			Object[] obj = (Object[]) row;
			
			Boolean estrategico = Boolean.parseBoolean(obj[5].toString());
			if(area.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				//si es del área PC mostrar solo los estrategicos
				if(estrategico) {
					ProyectoCustom proyecto = getProyectoCustom(obj);
					proyectos.add(proyecto);
				}
			} else if(area.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES)) {
				if(!estrategico && provinciasDz.contains(obj[6].toString())){
					ProyectoCustom proyecto = getProyectoCustom(obj);
					proyectos.add(proyecto);
				}
			}
							
		}
		return proyectos;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoCustom>  listarLicencias4CategoriasEnTramite(List<AreaUsuario> areasUsuario) {
		List<ProyectoCustom> proyectos = new ArrayList<ProyectoCustom>();
		
		String sqlPproyecto = "select * from dblink('" + dblinkSuiaVerde + "',"
				+ "'  SELECT z.codigo, z.nombre, z.fecha_registro, z.sector, z.categoria, z.estrategico, z.provincia "
				+ " FROM( "
				+ " SELECT a.ID AS codigo, a.nombre AS nombre, a.fecharegistro AS fecha_registro, y .ca_categoria AS categoria, y.cata_sector as sector, xxx.nombre as provincia, y.estrategico,  "
				+ " t1.totaltareas as total_tareas "
				+ " FROM proyectolicenciaambiental a "
				+ " INNER JOIN catalogo_categoria y ON a.id_catalogo = y.id_catalogo "
				+ " LEFT JOIN (SELECT ubicacion_proyecto (MAX (ubicacion.parroquia), 4) AS nombre, ubicacion.proyecto_id FROM ubicacion GROUP BY ubicacion.proyecto_id) as xxx on xxx.proyecto_id = a.id "
				+ " LEFT JOIN (SELECT substring(b.execution_id_, \"position\"(b.execution_id_, ''.'') + 1, length(b.execution_id_)) as idproy, count(*) as totaltareas  FROM public.jbpm4_task b GROUP BY b.execution_id_ ) t1 ON t1.idproy = a.ID "
				+ " WHERE a.estadoproyecto=true and (requiere_actualizacion_certificado_interseccion is null or requiere_actualizacion_certificado_interseccion != 2)"
				+ " ) as z "
				+ " WHERE "
				+ " z.categoria in (''III'',''IV'') "
				+ " AND z.total_tareas > 0 "
				+ "') "
				+ "as t1 (codigo varchar, nombre varchar, fecharegistro date, sector varchar, categoria varchar, "
				+ "estrategico boolean, provincia varchar ) "
				+ " ORDER BY 1 ";

		Query queryProyecto = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		List<Object[]> result = (List<Object[]>) queryProyecto.getResultList();
		
		List<String> provinciasDz = new ArrayList<>();
		if(areasUsuario.get(0).getArea().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES)) {
			String sqlUbicaciones = "SELECT u.gelo_codification_inec FROM public.geographical_locations u where u.area_id_zonal = " +areasUsuario.get(0).getArea().getId()+" ";
			Query queryUbicaciones = crudServiceBean.getEntityManager().createNativeQuery(sqlUbicaciones);
			
			provinciasDz = (List<String>) queryUbicaciones.getResultList();
		}

		for (Object row : result) {
			Object[] obj = (Object[]) row;
			
			Boolean estrategico = Boolean.parseBoolean(obj[5].toString());
			if(areasUsuario.get(0).getArea().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				//si es del área PC mostrar solo los estrategicos
				if(estrategico) {
					ProyectoCustom proyecto = getProyectoCustom(obj);
					proyectos.add(proyecto);
				}
			} else if(areasUsuario.get(0).getArea().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES)) {
				if(!estrategico && provinciasDz.contains(obj[6].toString())){
					ProyectoCustom proyecto = getProyectoCustom(obj);
					proyectos.add(proyecto);
				}
			}
							
		}
		return proyectos;
	}
	
	public void guardarEstadoActualizacionCertificado(ProyectoLicenciamientoAmbiental proyecto, String observacion) {
		crudServiceBean.saveOrUpdate(proyecto);
		
		HistorialActualizacionCertificadoInterseccion actualizacion = new HistorialActualizacionCertificadoInterseccion();
		actualizacion.setCodigoPryecto(proyecto.getCodigo());
		actualizacion.setEstadoActualizacion(proyecto.getEstadoActualizacionCertInterseccion());
		actualizacion.setObservacion(observacion);
	
		crudServiceBean.saveOrUpdate(actualizacion);
	}
	
	public void guardarHistorialActualizacionCertificado(String codigo, Integer estado, String observacion) {		
		HistorialActualizacionCertificadoInterseccion actualizacion = new HistorialActualizacionCertificadoInterseccion();
		actualizacion.setCodigoPryecto(codigo);
		actualizacion.setEstadoActualizacion(estado);
		actualizacion.setObservacion(observacion);
	
		crudServiceBean.saveOrUpdate(actualizacion);
	}

	public void guardarSolicitudActualizacion(String proyecto, Integer estado) {
		String sqlActualizaProyecto = "select dblink_exec('" + dblinkSuiaVerde + "','update proyectolicenciaambiental "
				+ "set requiere_actualizacion_certificado_interseccion= ''" + estado + "'' "
				+ "where id=''" + proyecto + "''') as result";
		Query queryActualizaProyecto = crudServiceBean.getEntityManager()
				.createNativeQuery(sqlActualizaProyecto);
		if (queryActualizaProyecto.getResultList().size() > 0)
			queryActualizaProyecto.getSingleResult();	
	}
	
	@SuppressWarnings("unchecked")
	public Object[] getProyecto(String proyecto) {
		String sqlPproyecto = "select * from dblink('" + dblinkSuiaVerde + "',"
				+ "'select codigoproyecto, nombreproyecto, fecharegistro, ca_categoria, ca_descripcion,provincia, representante, org, proponente, fecha_licencia, estrategico, catalogocategoria"
						+ " from vw_licencias_todas "
						+ " where codigoproyecto = ''"+proyecto+"'' ') "
						+ "as t1 (codigoproyecto varchar, nombreproyecto varchar, fecharegistro date, ca_categoria varchar, ca_descripcion varchar,"
						+ "provincia varchar, representante varchar, org varchar, proponente varchar, fecha_licencia date, estrategico boolean, "
						+ "catalogocategoria varchar)";

		Query queryProyecto = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		List<Object[]> resultList = (List<Object[]>) queryProyecto.getResultList();

		if (resultList.size() > 0) {
			return (Object[]) resultList.get(0);
		}
		return null;
	}
	
	public void migrarDocumentos(String nombre, String descripcion, String key, String extension, String mime, String proyecto, String urlAlfresco, Integer nroActualizacion) throws Exception {
		String sql = "select dblink_exec('"
				+ dblinkSuiaVerde
				+ "',' INSERT INTO public.documento (id, nombre, extension, mime, descripcion, fecha, key, destinatario, flujo, actividad, proyecto, visible, urlalfresco, numero_actualizacion) "
				+ "values((SELECT CASE WHEN max(id) is null then 1 else max(id) + 1 end FROM public.documento), ''" + nombre + "'', ''"+extension+"'', ''"+mime+"'', ''" + descripcion + "'', now(), "
				+ " ''" + key + "'', ''PROPONENTE'', ''Certificado de Intersección'', ''Actualización Certificado de Intersección'', "
				+ " ''" + proyecto + "'', ''true'', ''"+ urlAlfresco +"'', "+nroActualizacion+")')";
			Query query = crudServiceBean.getEntityManager().createNativeQuery(
					sql);
			query.getSingleResult();
	}

	private ProyectoCustom getProyectoCustom(Object[] array) {
		ProyectoCustom proyectoCustom=new ProyectoCustom();		
		proyectoCustom.setSourceType(ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA);
		proyectoCustom.setId((String)array[0]);
		proyectoCustom.setCodigo((String)array[0]);
		proyectoCustom.setNombre((String)array[1]);		
		proyectoCustom.setCategoria((String)array[4]);
		
		String categoriaNombrePublico="";
		if (proyectoCustom.getCategoria() == null)
			categoriaNombrePublico= "";
		if (proyectoCustom.getCategoria().equals("I"))
			categoriaNombrePublico= "Certificado Ambiental";
		if (proyectoCustom.getCategoria().equals("II"))
			categoriaNombrePublico= "Registro Ambiental";
		else
			categoriaNombrePublico= "Licencia Ambiental";
		
		proyectoCustom.setCategoriaNombrePublico(categoriaNombrePublico);
				
		String sector = (String)array[3];
		try {
			sector = sector.substring(0, 1).toUpperCase() + sector.substring(1);
		} catch (Exception e) {
		}
		proyectoCustom.setSector(sector);

		Date registro = (Date)array[2];
		String registroString = "";
		if (registro != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			registroString = dateFormat.format(registro);
		}
		proyectoCustom.setRegistro(registroString);
		proyectoCustom.setResponsableSiglas("N/D");
		proyectoCustom.setResponsable("No disponible");		
		return proyectoCustom;
	}
	
	public Integer guardarCoordenadasActualizadasSuiaVerde(
			String idProyecto4Categorias,
			List<UbicacionesGeografica> ubicaciones,
			List<CoordinatesWrapper> coordinatesWrappers, List<CoordinatesWrapper> coordinatesWrappersGeo) throws CmisAlfrescoException {
	
		try {
	
			Integer nro_actualizacion = 1;
			List<ProyectoUbicacionGeografica> ubicacionesActualizadas = getUbicacionProyectoActualizadaSuiaVerde(idProyecto4Categorias);
			if (ubicacionesActualizadas != null) {
				nro_actualizacion = ubicacionesActualizadas.get(0).getNroActualizacion() + 1;
			}
			
			List<ProyectoUbicacionGeografica> ubicacionesProyecto = new ArrayList<ProyectoUbicacionGeografica>();
			if (ubicaciones != null) {
				for (UbicacionesGeografica ubicacionesGeografica : ubicaciones) {
					ProyectoUbicacionGeografica proyectoUbicacionGeografica = new ProyectoUbicacionGeografica();
					proyectoUbicacionGeografica.setIdCuatroCategorias(idProyecto4Categorias);
					proyectoUbicacionGeografica.setUbicacionesGeografica(ubicacionesGeografica);
					proyectoUbicacionGeografica.setOrden(ubicacionesGeografica.getOrden());
					proyectoUbicacionGeografica.setNroActualizacion(nro_actualizacion);
					crudServiceBean.saveOrUpdate(proyectoUbicacionGeografica);
					ubicacionesProyecto.add(proyectoUbicacionGeografica);
				}
			}

			for (int i = 0; i <= coordinatesWrappersGeo.size() - 1; i++) {
				FormaProyecto formaProyecto = new FormaProyecto();
				formaProyecto.setTipoForma(coordinatesWrappersGeo.get(i).getTipoForma());
				formaProyecto.setIdCuatroCategorias(idProyecto4Categorias);
				formaProyecto.setNroActualizacion(nro_actualizacion);
				formaProyecto.setTipoCoordenada(2);
				crudServiceBean.saveOrUpdate(formaProyecto);
				
				for (Coordenada coordenada : coordinatesWrappersGeo.get(i).getCoordenadas()) {
					coordenada.setEstado(true);
					coordenada.setFormasProyecto(formaProyecto);
				}
				
				String queryCoordenadasGeo = "INSERT INTO suia_iii.coordinates"
						+ "(coor_id, coor_status, coor_description, coor_order, coor_x, coor_y, coor_zone, prsh_id, rpsh_id, coor_update_number, prsh_type, coor_geographic_area)"
						+ "VALUES ";
				for (Coordenada coordenada : coordinatesWrappersGeo.get(i).getCoordenadas()) {
					String id = crudServiceBean.getSecuenceNextValue("seq_coor_id", "suia_iii").toString();
					String zona = (coordenada.getZona() == null) ? "" : coordenada.getZona();
					String queryAdd = "(" + id + ", true, '"
							+ coordenada.getDescripcion() + "', "
							+ coordenada.getOrden() + ", " + coordenada.getX()
							+ ", " + coordenada.getY() + ", '"
							+ zona + "', "
							+ formaProyecto.getId() + ", null, "
							+ nro_actualizacion + ", 2, "+coordenada.getAreaGeografica()+"),";
					queryCoordenadasGeo += queryAdd;
				}
				queryCoordenadasGeo = queryCoordenadasGeo.substring(0, queryCoordenadasGeo.length() - 1);
				queryCoordenadasGeo += ";";

				crudServiceBean.insertUpdateByNativeQuery(queryCoordenadasGeo, null);
				
			}
	
			for (CoordinatesWrapper coordinatesWrapper : coordinatesWrappers) {
				FormaProyecto formaProyecto = new FormaProyecto();
				formaProyecto.setTipoForma(coordinatesWrapper.getTipoForma());
				formaProyecto.setIdCuatroCategorias(idProyecto4Categorias);
				formaProyecto.setNroActualizacion(nro_actualizacion);
				formaProyecto.setTipoCoordenada(1);
				crudServiceBean.saveOrUpdate(formaProyecto);
	
				for (Coordenada coordenada : coordinatesWrapper.getCoordenadas()) {
					coordenada.setEstado(true);
					coordenada.setFormasProyecto(formaProyecto);
				}
	
				String queryCoordenadas = "INSERT INTO suia_iii.coordinates"
						+ "(coor_id, coor_status, coor_description, coor_order, coor_x, coor_y, coor_zone, prsh_id, rpsh_id, coor_update_number, prsh_type, coor_geographic_area)"
						+ "VALUES ";
				for (Coordenada coordenada : coordinatesWrapper.getCoordenadas()) {
					String id = crudServiceBean.getSecuenceNextValue("seq_coor_id", "suia_iii").toString();
					String zona = (coordenada.getZona() == null) ? "" : coordenada.getZona();
					String queryAdd = "(" + id + ", true, '"
							+ coordenada.getDescripcion() + "', "
							+ coordenada.getOrden() + ", " + coordenada.getX()
							+ ", " + coordenada.getY() + ", '"
							+ zona + "', "
							+ formaProyecto.getId() + ", null, "
							+ nro_actualizacion + ", 1, "+coordenada.getAreaGeografica()+"),";
					queryCoordenadas += queryAdd;
				}
				queryCoordenadas = queryCoordenadas.substring(0, queryCoordenadas.length() - 1);
				queryCoordenadas += ";";
	
				crudServiceBean.insertUpdateByNativeQuery(queryCoordenadas, null);
			}
	
			return nro_actualizacion;
			
		} catch (Exception e) {
			LOG.error("Error al guardar el proyecto.", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoUbicacionGeografica> getUbicacionProyectoActualizadaSuiaVerde(
			String idProyecto) throws ServiceException {
		List<ProyectoUbicacionGeografica> ubicacionesProyecto = (List<ProyectoUbicacionGeografica>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From ProyectoUbicacionGeografica u where u.idCuatroCategorias =:idProyecto and u.estado = true "
								+ "and u.nroActualizacion > 0 order by nroActualizacion desc")
				.setParameter("idProyecto", idProyecto).getResultList();
	
		if (ubicacionesProyecto != null && !ubicacionesProyecto.isEmpty()) {
			Integer nro_actualizacion = ubicacionesProyecto.get(0).getNroActualizacion();
			List<ProyectoUbicacionGeografica> ultimasUbicacionesProyecto = (List<ProyectoUbicacionGeografica>) crudServiceBean
					.getEntityManager()
					.createQuery(
							"From ProyectoUbicacionGeografica u where u.idCuatroCategorias =:idProyecto and u.estado = true "
									+ "and u.nroActualizacion =:nroActualizacion order by orden")
					.setParameter("idProyecto", idProyecto)
					.setParameter("nroActualizacion", nro_actualizacion)
					.getResultList();
			return ultimasUbicacionesProyecto;
		} else {
			return null;
		}
	}

	public void guardarInterseccionesActualizacionSuiaVerde(Integer nroActualizacion, HashMap<InterseccionProyecto, List<DetalleInterseccionProyecto>> capasIntersecciones) throws Exception {
		for (InterseccionProyecto interseccion : capasIntersecciones.keySet()) {
			if(interseccion.getCapaCoa() != null) {
				Integer id = Integer.parseInt(crudServiceBean.getSecuenceNextValue("seq_inpr_id", "suia_iii").toString());
	
				String queryInterseccion = "INSERT INTO suia_iii.intersections_project(inpr_id, coa_laye_id, id_4_cat, inpr_process_date, inpr_buffer_intersection,inpr_layer_description,inpr_status,inpr_update_number) "
						+ "VALUES ";
				String query = "(" + id + ", "
							+ interseccion.getCapaCoa().getId() + ", "
							+ "'" + interseccion.getIdCuatroCategorias() + "', now()" + ", "
							+ interseccion.getIntersectaConCapaAmortiguamiento() + ", '" 
							+ interseccion.getDescripcion() + "', true , "
							+ nroActualizacion + " );";
				queryInterseccion += query;
	
				crudServiceBean.insertUpdateByNativeQuery(queryInterseccion, null);
				
				String queryDetalles = "INSERT INTO suia_iii.details_intersection_project (dein_id, inpr_id, dein_geometry_id, dein_geometry_name,dein_status,dein_subsystem_layer) "
						+ "VALUES ";
				for(DetalleInterseccionProyecto detalle : capasIntersecciones.get(interseccion)) {
					String idDetalle = crudServiceBean.getSecuenceNextValue("seq_inde_id", "suia_iii").toString();
					String queryAdd = "(" + idDetalle + ", "
							+ id + ", "
							+ detalle.getIdGeometria() + ", '" + detalle.getNombre()
							+ "', true , null ),";
					queryDetalles += queryAdd;
				}
	
				queryDetalles = queryDetalles.substring(0, queryDetalles.length() - 1);
				queryDetalles += ";";
	
				crudServiceBean.insertUpdateByNativeQuery(queryDetalles, null);
			}
		}		
	}
	
	public String guardarDocumentosSuiaVerdeAlfresco(String codigoProyecto, byte[] contenidoDocumento, String nombreFile, String extension) {
		String resultado = null;
		try{
			String nombreArchivoAlfresco = nombreFile + "." + extension;
			String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, "Actualizacion Certificado Interseccion", null);
	        String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootId());
	        Document documentCreate = alfrescoServiceBean.fileSaveStream(contenidoDocumento,
	        		Calendar.getInstance().getTimeInMillis()+"-"+nombreArchivoAlfresco.replace("/", "-"), folderId, "Actualizacion Certificado Interseccion", folderName);
			resultado = documentCreate.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultado;
	}

	public String guardarDocumentosSuiaVerde(String codigoProyecto, byte[] contenidoDocumento, String nombreFile, String descripcion, 
			String extension, String tipo, String key, String flujo, Integer nroActualizacion) {
		String resultado = null;
		try{
			String nombreArchivoAlfresco = nombreFile + "." + extension;
			String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, "Actualizacion Certificado Interseccion", null);
	        String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootId());
	        Document documentCreate = alfrescoServiceBean.fileSaveStream(contenidoDocumento,
	        		Calendar.getInstance().getTimeInMillis()+"-"+nombreArchivoAlfresco.replace("/", "-"), folderId, "Actualizacion Certificado Interseccion", folderName);
	
	        
			String sqlInsert = "select dblink_exec('" + dblinkSuiaVerde + "',"
					+ "'INSERT INTO public.documento "
					+ "(id, proyecto, nombre, descripcion, extension, mime, fecha, visible, urlalfresco, key, flujo, actividad, destinatario, numero_actualizacion) "
					+ "VALUES ((SELECT CASE WHEN max(id) is null then 1 else max(id) + 1 end FROM public.documento), ''"+codigoProyecto+"'', ''"+nombreFile+"'', ''"+descripcion+"'', ''"+extension+"'', ''"+tipo+"'', now(), true, ''"+documentCreate.getId()+"'', ''"+key+"'', ''"+flujo+"'',  "
									+ "''Inicio Registro Proyecto'', ''PROPONENTE'', "+nroActualizacion+")') as result";
			Query queryInsert = crudServiceBean.getEntityManager().createNativeQuery(sqlInsert);
			queryInsert.getSingleResult();
			resultado = documentCreate.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultado;
	}
	
}