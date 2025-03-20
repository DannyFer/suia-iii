package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.digitalizacion.model.CoordenadaDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.ShapeDigitalizacion;
import ec.gob.ambiente.rcoa.dto.EntityLicenciaFisica;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

@LocalBean
@Stateless
public class LicenciaAmbientalFisicaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;

	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public String dblinkSectorSubsector=Constantes.getDblinkSectorSubsector();
	
		
	
	public List<EntityLicenciaFisica> busquedaProyectos(String operador){
		
		List<EntityLicenciaFisica> listaProyectos = new ArrayList<>();
		try {
			String sql ="select * from dblink('"+dblinkSuiaVerde+"',"
					+ "'select f.lic_secuencial, f.lic_num_licencia, f.lic_fecha_emision, f.lic_nom_proyecto, f.lic_nom_proponente, f.lic_fecha_ingreso,  f.lic_intersecta, "
					+ " f.lic_shape, t.name, s.nombre, f.lic_intersecta_descripcion "
					+ " FROM licencia_ambiental_fisica f "
					+ " INNER JOIN tiposector t on f.lic_sector = t.id "
					+ " INNER JOIN subsector s on s.id = f.lic_subsector "
					+ " where f.lic_borrado = true  ') "
					+ "as (id varchar, numeroResolucion varchar, fechaEmision varchar, nombreProyecto varchar, nombreOperador varchar, fechaIngresoSistema varchar, "
					+ " interseca varchar, shape varchar, sector varchar, actividad varchar, intersectaDescripcion varchar)"
					+ " where true " +getSqlNotDigitalizacion("id");

			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();			
			
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					
					EntityLicenciaFisica objeto = new EntityLicenciaFisica();
					
					objeto.setId(Integer.valueOf((String) row[0]));
					objeto.setNumeroResolucion((String) row[1]);
					objeto.setFechaEmision((String) row[2]);
					objeto.setNombreProyecto((String) row[3]);
					objeto.setNombreOperador((String) row[4]);
					objeto.setFechaIngresoSistema((String) row[5]);
					objeto.setInterseca(Boolean.valueOf((String) row[6]));
					objeto.setShape((String) row[7]);
					objeto.setSector((String) row[8]);
					objeto.setActividad((String) row[9]);
					objeto.setIntersectaDescripcion((String) row[10]);
					listaProyectos.add(objeto);
				}
			}					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaProyectos;
	}

	public String getSqlNotDigitalizacion(String campo){
		return " and "+ campo +" not in ( select cast(enaa_id_proyecto as varchar) from coa_digitalization_linkage.environmental_administrative_authorizations where enaa_status is true and enaa_history is false and enaa_id_proyecto is not null and enaa_system = 1 )";
	}
	
	/**
	 * busqueda de coordenadas
	 * @param idSecuencial
	 * @return
	 */
	public List<CoordenadaDigitalizacion> busquedaCoordenadasPorCodigo(Integer idSecuencial){
		
		List<CoordenadaDigitalizacion> listaProyectos = new ArrayList<CoordenadaDigitalizacion>();
		try {
			
			String sql ="select * from dblink('"+dblinkSuiaVerde+"',"
					+ "'select f.coo_codigo, f.coo_coordenada_x, f.coo_coordenada_y "
					+ " FROM licencia_fisica_coordenadas f where coo_borrado = true and coo_lic_ambiental_fisica = " + idSecuencial + "') "
					+ "as (id varchar, coordenadaX varchar, coordenadaY varchar)";			

			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();			
			
			if (resultList.size() > 0) {
				for (Object a : resultList) {					
					listaProyectos.add(new CoordenadaDigitalizacion((Object[]) a));
				}
			}					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaProyectos;
	}
	
	public List<CoordenadaDigitalizacion> busquedaCoordenadas4CategoriasPorCodigo(String codigoProyecto, Integer shapeId){
		
		List<CoordenadaDigitalizacion> listaProyectos = new ArrayList<CoordenadaDigitalizacion>();
		try {
			
			String sql ="select * from dblink('"+dblinkSuiaVerde+"',"
					+ "'select f.id, f.coordenadax, f.coordenaday "
					+ " FROM coordenada f where proyecto_id = ''" + codigoProyecto + "'' and shapexproyecto = "+ shapeId
					+ " order by f.id ') "
					+ "as (id varchar, coordenadaX varchar, coordenadaY varchar)";			

			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();			
			
			if (resultList.size() > 0) {
				for (Object a : resultList) {					
					listaProyectos.add(new CoordenadaDigitalizacion((Object[]) a));
				}
			}					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaProyectos;
	}
	
	public List<ShapeDigitalizacion> busquedaShape4CategoriasPorCodigo(String codigoProyecto){
		
		List<ShapeDigitalizacion> listaProyectos = new ArrayList<ShapeDigitalizacion>();
		try {
			
			String sql ="select * from dblink('"+dblinkSuiaVerde+"',"
					+ "'select s.tiposhape_id, s.id "
					+ " FROM shapexproyecto s where proyecto = ''" + codigoProyecto + "'' ') "
					+ "as (tipoForma varchar, id integer)";			

			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaProyectos.add(new ShapeDigitalizacion((String) row[0], (Integer) row[1]));
				}
			}					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaProyectos;
	}
	
	/**
	 * 
	 * @param codigo
	 * @param sistema  2=4categorias , 3= sector subsector
	 * @return
	 */
	public List<String> busquedaUbicacion4Cat(String codigo, String sistema){
		List<String> ubicacionesList = new ArrayList<String>();
		try {
			String sql ="select * from dblink('"+(sistema.equals("2") ? dblinkSuiaVerde : dblinkSectorSubsector )+"',"
					+ "'select p.parroquia_inec "
					+ " FROM ubicacion u "
					+ " INNER JOIN parroquia p on u.parroquia = p.id"
					+ " where u.proyecto_id = ''" + codigo + "'' ') "
					+ "as (inec varchar)";

			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					String codigoInec = ((String) a).replaceAll("\n", "");
					ubicacionesList.add((String) codigoInec);
				}
				return ubicacionesList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ubicacionesList;
	}
	
	
	public List<CoordenadaDigitalizacion> busquedaCoordenadasSectorPorCodigo(String codigoProyecto, Integer shapeId){
		List<CoordenadaDigitalizacion> listaProyectos = new ArrayList<CoordenadaDigitalizacion>();
		try {
			String sql ="select * from dblink('"+dblinkSectorSubsector+"',"
					+ "'select f.id, f.coordenadax, f.coordenaday "
					+ " FROM coordenada f where proyecto_id = ''" + codigoProyecto + "'' and shapexproyecto = "+ shapeId
					+ " order by f.id ') "
					+ "as (id varchar, coordenadaX varchar, coordenadaY varchar)";

			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					listaProyectos.add(new CoordenadaDigitalizacion((Object[]) a));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaProyectos;
	}
	
	public List<ShapeDigitalizacion> busquedaShapeSectorPorCodigo(String codigoProyecto){
		
		List<ShapeDigitalizacion> listaProyectos = new ArrayList<ShapeDigitalizacion>();
		try {
			
			String sql ="select * from dblink('"+dblinkSectorSubsector+"',"
					+ "'select s.tiposhape_id, s.id  "
					+ " FROM shapexproyecto s where proyecto = ''" + codigoProyecto + "'' ') "
					+ "as (tipoForma varchar, id integer)";			

			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();			
			
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaProyectos.add(new ShapeDigitalizacion((String) row[0], (Integer) row[1]));
				}
			}					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaProyectos;
	}
	
	public List<String> busquedaUbicacionSector(String codigo){
		
		List<String> ubicacionesList = new ArrayList<String>();
		try {
			
			String sql ="select * from dblink('"+dblinkSectorSubsector+"',"
					+ "'select p.parroquia_inec "
					+ " FROM ubicacion u "
					+ " INNER JOIN parroquia p on u.parroquia = p.id"
					+ " where u.proyecto_id = ''" + codigo + "'' ') "
					+ "as (inec varchar)";			

			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();			
			
			if (resultList.size() > 0) {
				for (Object a : resultList) {					
					ubicacionesList.add((String) a);
				}
				return ubicacionesList;
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ubicacionesList;
	}
	
	@SuppressWarnings("unchecked")
	public List<AutorizacionAdministrativa> busquedaProyectosAsociacion(){
		
		List<AutorizacionAdministrativa> listaProyectos = new ArrayList<>();
		try {
			
			String sql ="select * from dblink('"+dblinkSuiaVerde+"',"
					+ "'select f.lic_secuencial, f.lic_num_licencia, f.lic_fecha_emision, f.lic_nom_proyecto, f.lic_nom_proponente, f.lic_fecha_ingreso,  f.lic_intersecta, "
					+ " f.lic_shape, t.name, s.nombre, f.lic_intersecta_descripcion "
					+ " FROM licencia_ambiental_fisica f "
					+ " INNER JOIN tiposector t on f.lic_sector = t.id "
					+ " INNER JOIN subsector s on s.id = f.lic_subsector "
					+ " where f.lic_borrado = true ') "
					+ "as (id varchar, numeroResolucion varchar, fechaEmision varchar, nombreProyecto varchar, nombreOperador varchar, fechaIngresoSistema varchar, "
					+ " interseca varchar, shape varchar, sector varchar, actividad varchar, intersectaDescripcion varchar)"		
					+ " where true " +getSqlNotDigitalizacion("id");
					
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();			
			
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					
					AutorizacionAdministrativa objeto = new AutorizacionAdministrativa();
					
					objeto.setId(Integer.valueOf((String) row[0]));
					objeto.setCodigo((String) row[1]); //resolucion
					objeto.setFecha((String) row[2]);
					objeto.setNombre((String) row[3]);
					objeto.setNombreProponente((String) row[4]);
					objeto.setSector((String) row[8]);
					objeto.setActividad((String) row[9]);
					objeto.setFuente("1");
					objeto.setEstado("Por completar");
					listaProyectos.add(objeto);
				}
			}					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaProyectos;
	}
	
	@SuppressWarnings("unchecked")
	public EntityLicenciaFisica buscarProyectoFisicoPorId(Integer id){
		
		EntityLicenciaFisica proyecto = new EntityLicenciaFisica();
		try {
			
			String sql ="select * from dblink('"+dblinkSuiaVerde+"',"
					+ "'select f.lic_secuencial, f.lic_num_licencia, f.lic_fecha_emision, f.lic_nom_proyecto, f.lic_nom_proponente, f.lic_fecha_ingreso,  f.lic_intersecta, "
					+ " f.lic_shape, t.name, s.nombre, f.lic_intersecta_descripcion "
					+ " FROM licencia_ambiental_fisica f "
					+ " INNER JOIN tiposector t on f.lic_sector = t.id "
					+ " INNER JOIN subsector s on s.id = f.lic_subsector "
					+ " where f.lic_borrado = true and f.lic_secuencial = " 
					+ id 
					+ " ') "
					+ "as (id varchar, numeroResolucion varchar, fechaEmision varchar, nombreProyecto varchar, nombreOperador varchar, fechaIngresoSistema varchar, "
					+ " interseca varchar, shape varchar, sector varchar, actividad varchar, intersectaDescripcion varchar)";			

			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();			
			
			if (resultList.size() > 0) {
				Object[] row = (Object[]) resultList.get(0);					
				
				proyecto.setId(Integer.valueOf((String) row[0]));
				proyecto.setNumeroResolucion((String) row[1]);
				proyecto.setFechaEmision((String) row[2]);
				proyecto.setNombreProyecto((String) row[3]);
				proyecto.setNombreOperador((String) row[4]);
				proyecto.setFechaIngresoSistema((String) row[5]);
				proyecto.setInterseca(Boolean.valueOf((String) row[6]));
				proyecto.setShape((String) row[7]);
				proyecto.setSector((String) row[8]);
				proyecto.setActividad((String) row[9]);
				proyecto.setIntersectaDescripcion((String) row[10]);
				
				return proyecto;				
			}					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proyecto;
	}

	public List<String> busquedaDocumentoTipo(String proyecto, String tipoDocumento){
		List<String> ubicacionesList = new ArrayList<String>();
		try {
			String sql ="select * from dblink('"+dblinkSuiaVerde+"',"
					+ "'select d.id, d.urlalfresco, d.nombre, d.extension, d.mime, cast(d.fecha as date) "
					+ " FROM documento d "
					+ " where d.proyecto = ''" + proyecto + "'' "
					+ " and d.key = ''" + tipoDocumento + "'' order by d.numero_actualizacion desc ') "
					+ "as (documento varchar, alfrescoId varchar, mombre varchar, extension varchar, mine varchar, fecha varchar )";

			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] aux = (Object[])a;
					ubicacionesList.add((String) (aux[0] == null ? "" : aux[0])+"##"+(String) (aux[1] == null ? "" : aux[1])+"##"+(String) (aux[2] == null ? "" : aux[2])+"##"+(String) (aux[3] == null ? "" : aux[3])+"##"+(String) (aux[4] == null ? "" : aux[4])+"##"+(String) (aux[5] == null ? "" : aux[5]));
				}
				return ubicacionesList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ubicacionesList;
	}
	
/*
	@SuppressWarnings("unchecked")
	public List<Documento> getFiltroDocumentos(String flujo) {
		String flujo1 = null;		
		try {
			tareaHome.isUserLogged("PROPONENTE");
			String proyecto = instance.getId();
			List<Documento> documentos;
			if (Util2.getUsuarioByRol("PROPONENTE", instance).getNombreUsuario().equals(credentials.getUsername())) {
				if (flujo.equals("Registro Proyecto")){
					documentos = getEntityManager().createQuery(
							"select d from Documento d where (d.proyecto='" + proyecto + "' ) and (d.flujo like '" + flujo
							+ "%' or d.key like '%CertificadoInterseccionProvisional%' or d.key like '%MapaCIProvisional%' or d.key like '%CertificadoInterseccionAutomatico%' and d.visible=TRUE)").getResultList();
				}else if (flujo.equals("Certificado de Intersección")|| flujo.equals("Categoria III")||flujo.equals("Categoria II") ){
					if (flujo.equals("Categoria III")){
						documentos = getEntityManager().createQuery(
								"select d from Documento d where (d.proyecto='" + proyecto + "' ) and (d.flujo like '" + flujo
										+ "%'  and d.visible=TRUE) and (key <>'informeTecnico') and (key <> 'catIIinformePronunciamiento')").getResultList();
					}else{
						documentos = getEntityManager().createQuery(
								"select d from Documento d where (d.proyecto='" + proyecto + "' ) and (d.flujo like '" + flujo
										+ "%'  and d.visible=TRUE)").getResultList();
					}	
				}else if (flujo.equals("Categoria IV EsIA Documentos")){
					documentos = getEntityManager().createQuery(
												
												"select d from Documento d" 
														+" where d.proyecto='"+proyecto+"' and ( d.flujo like '%CategoriaIV2%'"
														+" or d.flujo like '%Categoria IV2%'" 
														+" or d.flujo like '%Categoría IV2%' or" 
														+" d.flujo like '%CategoríaIV2%'" 
														+" or d.flujo like '%Categoría IV Estudio de Impacto Ambiental%' ) and d.visible=TRUE and d.key<>'catIV2informePronunciamiento' and d.key<>'informeTecnicoCATIV' and d.key<>'catIVInformeTecnico' order by d.id DESC"
												
												).getResultList();				
				}
				else if (flujo.equals("Registro Proyecto 1")){
					documentos = getEntityManager().createQuery(
							"select d from Documento d where (d.proyecto='" + proyecto + "' ) and ("
							+ " d.key like '%CertificadoInterseccionProvisional%' " 
							+ " or d.key like '%pciCertificadoInterseccion%' "
							+ " or d.key like '%pciOficioInterseccionFirmado%' "
							+ " or d.key like '%CertificadoInterseccionAutomatico%' "
							+ " or d.key like '%MapaCIFinal%' "
							+ " or d.key like '%coordUbicacion%' "
							+ " or d.key like '%CertificadoInterseccionAutomatico%' "
							+" or d.key like '%MapaCIProvisional%' or  d.flujo like 'Registro Proyecto%') and (d.visible=TRUE)").getResultList();
				}
				
				else{
					if (flujo.equals("Categoria IV")){
						flujo1=flujo;
						flujo ="Categoría IV";
					}
					if (flujo.equals("EsIA EXANTE")){
						flujo1=flujo;
						flujo ="Participación Social";
						
					}
					String desicion=null;
					if (flujo.equals("Categoria IV") || flujo.equals("EsIA EXANTE") || flujo.equals("Categoría IV") || flujo.equals("Participación Social")){
						try{
							 desicion = (String)getEntityManager().createNativeQuery("select stringvalue from variable where proyecto ='" + proyecto + "' and key='desicionOk' order by fecha desc limit 1 ").getSingleResult();						 
						}catch(Exception e){
							
						}
					}
					
					Integer projects=0;
					Integer projects1=0; 
					if (desicion != null){
					if (desicion.equals("Aprobado")){
						projects= (Integer) getEntityManager().createNativeQuery("select dd.id from Documento dd where dd.proyecto='" + proyecto + "' and dd.key='pexInformeTecnicoPS' order by dd.fecha desc limit 1 ").getSingleResult();
						projects1= (Integer) getEntityManager().createNativeQuery("select dd.id from Documento dd where dd.proyecto='" + proyecto + "' and dd.key='pexParticipacionSocial' order by dd.fecha desc limit 1 ").getSingleResult();
						documentos = getEntityManager().createQuery(
								"select d from Documento d where d.proyecto='" + proyecto + "' and ( d.flujo like '" + flujo
//								+ "%' or d.flujo like '"+flujo1 +"%') and d.visible=TRUE and d.key <>'pexInformeTecnicoPS' and d.key<> 'pexInformeVisitaPrevia' and d.key<>'pexParticipacionSocial'").getResultList();
								+ "%' or d.flujo like '"+flujo1 +"%') and d.visible=TRUE and d.key <>'pexInformeTecnicoPS' and d.key<> 'pexInformeVisitaPrevia' and d.key<>'pexParticipacionSocial' or d.id="+projects +" or d.id="+projects1).getResultList();
					}else{
						documentos = getEntityManager().createQuery(
								"select d from Documento d where d.proyecto='" + proyecto + "' and ( d.flujo like '" + flujo
//								+ "%' or d.flujo like '"+flujo1 +"%') and d.visible=TRUE and d.key <>'pexInformeTecnicoPS' and d.key<> 'pexInformeVisitaPrevia' and d.key<>'pexParticipacionSocial'").getResultList();
								+ "%' or d.flujo like '"+flujo1 +"%') and d.visible=TRUE and d.key <>'pexInformeTecnicoPS' and d.key<> 'pexInformeVisitaPrevia' and d.key<>'pexParticipacionSocial'").getResultList();
					}
					}else{
						documentos = getEntityManager().createQuery(
								"select d from Documento d where d.proyecto='" + proyecto + "' and ( d.flujo like '" + flujo
//								+ "%' or d.flujo like '"+flujo1 +"%') and d.visible=TRUE and d.key <>'pexInformeTecnicoPS' and d.key<> 'pexInformeVisitaPrevia' and d.key<>'pexParticipacionSocial'").getResultList();
								+ "%' or d.flujo like '"+flujo1 +"%') and d.visible=TRUE and d.key <>'pexInformeTecnicoPS' and d.key<> 'pexInformeVisitaPrevia' and d.key<>'pexParticipacionSocial'").getResultList();
					}
					
				}
				
			} else {
				if (flujo.equals("Registro Proyecto")){
					documentos = getEntityManager().createQuery(
							"select d from Documento d where (d.proyecto='" + proyecto + "' ) and (d.flujo like '" + flujo
							+ "%' or d.key like '%CertificadoInterseccionProvisional%' or d.key like '%MapaCIProvisional%' or d.key like '%CertificadoInterseccionAutomatico%' and d.visible=TRUE)").getResultList();
				}else if (flujo.equals("Certificado de Intersección")|| flujo.equals("Categoria III")||flujo.equals("Categoria II") ){
					documentos = getEntityManager().createQuery(
							"select d from Documento d where (d.proyecto='" + proyecto + "' ) and (d.flujo like '" + flujo
									+ "%'  and d.visible=TRUE)").getResultList();
				}else if (flujo.equals("Categoria IV EsIA Documentos")){
					documentos = getEntityManager().createQuery(
							
							"select d from Documento d" 
									+" where d.proyecto='"+proyecto+"' and ( d.flujo like '%CategoriaIV2%'"
									+" or d.flujo like '%Categoria IV2%'" 
									+" or d.flujo like '%Categoría IV2%' or" 
									+" d.flujo like '%CategoríaIV2%'" 
									+" or d.flujo like '%Categoría IV Estudio de Impacto Ambiental%' ) and (key<>'catIVInformeTecnico') and d.visible=TRUE order by d.id DESC"
							
							).getResultList();
				}
				else if (flujo.equals("Registro Proyecto 1")){
					documentos = getEntityManager().createQuery(
							"select d from Documento d where (d.proyecto='" + proyecto + "' ) and ("
							+ " d.key like '%CertificadoInterseccionProvisional%' " 
							+ " or d.key like '%pciCertificadoInterseccion%' "
							+ " or d.key like '%pciOficioInterseccionFirmado%' "
							+ " or d.key like '%CertificadoInterseccionAutomatico%' "
							+ " or d.key like '%MapaCIFinal%' "
							+ " or d.key like '%coordUbicacion%' "
							+" or d.key like '%MapaCIProvisional%' or  d.flujo like 'Registro Proyecto%') ").getResultList();
				}
				else{
					if (flujo.equals("Categoria IV")){
						flujo1=flujo;
						flujo ="Categoría IV";
						
					}			
					if (flujo.equals("EsIA EXANTE")){
						flujo1=flujo;
						flujo ="Participación Social";
						
					}
				}
			}
			return documentos;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}*/
}
