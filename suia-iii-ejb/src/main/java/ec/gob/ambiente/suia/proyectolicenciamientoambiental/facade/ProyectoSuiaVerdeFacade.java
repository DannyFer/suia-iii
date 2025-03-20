package ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class ProyectoSuiaVerdeFacade {
	
	private static final Logger LOG = Logger.getLogger(ProyectoSuiaVerdeFacade.class);
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public String dblinkSuiaHidro=Constantes.getDblinkBpmsHyD();
	

	public void actualizarProyectoEnSuiaVerde(String proyecto, Boolean estado) {
		String sqlActualizaProyecto = "select dblink_exec('" + dblinkSuiaVerde + "','update proyectolicenciaambiental "
				+ "set requiere_actualizacion_certificado_interseccion= ''" + estado + "'' "
				+ "where id=''" + proyecto + "''') as result";
		Query queryActualizaProyecto = crudServiceBean.getEntityManager()
				.createNativeQuery(sqlActualizaProyecto);
		if (queryActualizaProyecto.getResultList().size() > 0)
			queryActualizaProyecto.getSingleResult();	
	}
	
	public List<Object> getProcessPagoHidrocarburos(String proyecto) {
		String sqltaskbpmhyd = "select * from dblink('" + dblinkSuiaHidro + "',"
				+ "'select processinstanceid from variableinstancelog where value=''" + proyecto + "'' "
				+ " and processid in (''Hydrocarbons.pagoLicencia'', ''Hydrocarbons.pagoLicenciaEnte'') " + "') as (id integer)";

		Query queryProceso = crudServiceBean.getEntityManager()
				.createNativeQuery(sqltaskbpmhyd);
		List<Object> resultPro = queryProceso.getResultList();

		if (resultPro.size() > 0) {
			return resultPro;
		}
		return null;
	}
	
	public List<Object> getProcessPago4Categorias(String proceso) {
		String sqltask = "select * from dblink('" + dblinkSuiaVerde + "',"
				+ "'select dbid_ from public.jbpm4_hist_actinst where execution_=''" + proceso + "'' "
				+ " and activity_name_ = ''cat_iv_borradorLicencia'' ') as (id integer)";

		Query queryProceso = crudServiceBean.getEntityManager()
				.createNativeQuery(sqltask);
		List<Object> resultPro = queryProceso.getResultList();

		if (resultPro.size() > 0) {
			return resultPro;
		}
		return null;
	}
	
	public Object getResumenProyectoSuiaVerde(String proyecto) {
		String sqlPproyecto = "select * from dblink('" + dblinkSuiaVerde + "',"
				+ "'select resumen"
						+ " from proyectolicenciaambiental "
						+ " where id = ''"+proyecto+"'' ') "
						+ "as t1 (resumen varchar)";

		Query queryProyecto = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		List<Object> resultList = (List<Object>) queryProyecto.getResultList();

		if (resultList.size() > 0) {
			return (Object) resultList.get(0);
		}
		return null;
	}
	
	public Object[] getProyectoSuiaVerde(String proyecto) {
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
	
	public Object[] getProyectoSuiaVerde(String proyecto, String usuario) {
		String sqlPproyecto = "select * from dblink('" + dblinkSuiaVerde + "',"
				+ "'select codigoproyecto, nombreproyecto, fecharegistro, "
				+ "CASE WHEN licencia is not null THEN licencia ELSE ca_categoria END as ca_categoria, "
				+ "ca_descripcion,provincia, representante, org, proponente, fecha_licencia, estrategico, catalogocategoria"
				+ " from vw_licencias_todas "
				+ " where codigoproyecto = ''"+proyecto+"'' and rucproponente = ''"+usuario+"''') "
				+ "as t1 (codigoproyecto varchar, nombreproyecto varchar, fecharegistro date, ca_categoria varchar, ca_descripcion varchar,"
				+ "provincia varchar, representante varchar, org varchar, proponente varchar, fecha_licencia date, estrategico boolean, "
				+ "catalogocategoria varchar)";
//		0 codigoproyecto,
//		1 nombreproyecto,
//		2 fecharegistro,
//		3 ca_categoria,
//		4 ca_descripcion,
//		5 provincia,
//		6 representante,
//		7 org,
//		8 proponente,
//		9 fecha_licencia,
//		10 estrategico,
//		11 catalogocategoria
		Query queryProyecto = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		List<Object[]> resultList = (List<Object[]>) queryProyecto.getResultList();

		if (resultList.size() > 0) {
			return (Object[]) resultList.get(0);
		}
		return null;
	}
	
	public Boolean esRequeridoActualizacionProyectoSuiaVerde(String proyecto) {
		String sqlPproyecto = "select * from dblink('" + dblinkSuiaVerde + "',"
				+ "'select id"
						+ " from proyectolicenciaambiental "
						+ " where id = ''"+proyecto+"'' and requiere_actualizacion_certificado_interseccion = true') "
						+ "as t1 (codigoproyecto varchar)";
		Query queryProyecto = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		
		List<Object[]> resultList = (List<Object[]>) queryProyecto.getResultList();

		if (resultList.size() > 0) {
			return true;
		}
		return false;
	}
	
	public Integer getNumeroActualizacion(String proyecto) {
		String sqlPproyecto = "select * from dblink('" + dblinkSuiaVerde + "',"
				+ "'SELECT parroquia, numero_actualizacion "
				+ "FROM public.ubicacionactualizacion "
				+ "where proyecto_id=''" + proyecto + "'' order by numero_actualizacion desc"
				+ "') as (parroquia integer, numero_actualizacion integer)";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		List<Object[]> resultPro = queryProceso.getResultList();

		Integer nro_actualizacion = 0;
		if (resultPro.size() > 0) {
			nro_actualizacion = (Integer) resultPro.get(0)[1];
		}

		return nro_actualizacion;
	}
	
	public void guardarDocumentosSuiaVerde(String codigoProyecto, byte[] contenidoDocumento, String nombreFile, String descripcion, 
			String extension, String tipo, String key, String flujo, Integer nroActualizacion) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void guardarActualizacionCoordenadasProyecto(String proyecto,
			List<UbicacionesGeografica> ubicaciones,
			List<CoordinatesWrapper> coordinatesWrappers) {
			
			Integer nro_actualizacion = getNumeroActualizacion(proyecto) + 1;
			
			if (ubicaciones != null) {
				for (UbicacionesGeografica ubicacionesGeografica : ubicaciones) {
					Integer parroquia = getIdParroquia(ubicacionesGeografica.getCodificacionInec());
					guardarUbicacionActualizada(proyecto, parroquia, nro_actualizacion);
				}
			}

			for (CoordinatesWrapper coordinatesWrapper : coordinatesWrappers) {
				Integer idShape = guardarShapeActualizada(proyecto, coordinatesWrapper.getTipoForma().getId(), nro_actualizacion);
				
				for (Coordenada coordenada : coordinatesWrapper.getCoordenadas()) {					
					guardarCoordenadasActualizadas(proyecto, coordenada.getX(), coordenada.getY(), idShape, coordenada.getDescripcion(), nro_actualizacion);
				}
			}
	
		
	}
	
	public void guardarUbicacionActualizada(String codigoProyecto, Integer parroquia, Integer actualizacion) {
        
		String sqlInsert = "select dblink_exec('" + dblinkSuiaVerde + "',"
				+ "'INSERT INTO public.ubicacionactualizacion "
				+ "(parroquia, proyecto_id, ubicacioncmo, numero_actualizacion) "
				+ "VALUES ( "+parroquia+", "
						+ "''"+codigoProyecto+"'', ''0'', "+actualizacion+" )') as result";
		Query queryInsert = crudServiceBean.getEntityManager().createNativeQuery(sqlInsert);
		queryInsert.getSingleResult();
	}
	
	public Integer getIdParroquia(String codigoInec) {
		String sqlPproyecto = "select * from dblink('" + dblinkSuiaVerde + "',"
				+ "'SELECT id "
				+ "FROM public.parroquia "
				+ "where parroquia_inec=''" + codigoInec + "'' "
				+ "') as (parroquia integer)";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		List<Object> resultPro = queryProceso.getResultList();

		if (resultPro.size() > 0) {
			return (Integer) resultPro.get(0);
		}

		return null;
	}
	
	public Integer guardarShapeActualizada(String proyecto, Integer tipoShape, Integer actualizacion) {
        
		String sqlInsert = "select dblink_exec('" + dblinkSuiaVerde + "',"
				+ "'INSERT INTO public.shapexproyectoactualizacion "
				+ "(id, proyecto, tiposhape_id, tiposproyecto, numero_actualizacion) "
				+ "VALUES ((SELECT CASE WHEN max(id) is null then 1 else max(id) + 1 end FROM public.shapexproyectoactualizacion), ''"+proyecto+"'', "
						+ "''"+tipoShape+"'', ''5'', "+actualizacion+" )') as result";
		Query queryInsert = crudServiceBean.getEntityManager().createNativeQuery(sqlInsert);
		
		if (queryInsert.getResultList().size() > 0) {
			String sqlPproyecto = "select * from dblink('" + dblinkSuiaVerde + "',"
					+ "'SELECT id "
					+ "FROM public.shapexproyectoactualizacion "
					+ "where proyecto=''" + proyecto + "'' order by id desc"
					+ "') as (id integer)";

			Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
			List<Object> resultPro = queryProceso.getResultList();

			if (resultPro.size() > 0) {
				return (Integer) resultPro.get(0);
			}
		}
		
		return null;
	}
	
	public void guardarCoordenadasActualizadas(String proyecto, Double coordenadax, Double coordenaday, Integer idShape, String descripcion, Integer actualizacion) {
        
		String sqlInsert = "select dblink_exec('" + dblinkSuiaVerde + "',"
				+ "'INSERT INTO public.coordenadaactualizacion "
				+ "(id, coordenadax, coordenaday, shapexproyecto, proyecto_id, descripcion, numero_actualizacion) "
				+ "VALUES ((SELECT CASE WHEN max(id) is null then 1 else max(id) + 1 end FROM public.coordenadaactualizacion), "
				+ ""+coordenadax+", "+coordenaday+", "+idShape+", ''"+proyecto+"'', ''"+descripcion+"'', "+actualizacion+" )') as result";
		Query queryInsert = crudServiceBean.getEntityManager().createNativeQuery(sqlInsert);
		
		queryInsert.getSingleResult();
	}

	public String getEnteResponsable4cat (String provincia, Boolean isEstrategico){		
		String condicionSql = "";
		if(isEstrategico){
			condicionSql+= "  r.nombre LIKE ''AUTORIDAD AMBIENTAL MAE'' ";
		} else {
			condicionSql+= "  r.nombre LIKE ''AUTORIDAD AMBIENTAL'' and pr.nombre = ''"+provincia+"''  ";
		}
		
		String sqltaskbpmhyd="select * from dblink('"+dblinkSuiaVerde+"',"
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
    		return resultPro.get(0)[4].toString();
		}
		return null;
	}
	
	public List<Object> getFormasProyecto(String proyecto) {
		String sqltaskbpmhyd="select * from dblink('"+dblinkSuiaVerde+"',"
		                + "'SELECT id "
		                + "from shapexproyecto "
		                + "where proyecto = ''"+proyecto+"''') "
                		+ "as (shape int)";

		Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
		List<Object>  resultPro = queryProceso.getResultList();
		
		if(resultPro.size()>0){
    		return resultPro;
		}
		return null;
	}
}
