package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ProrrogaModificacionEstudio;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.utils.DiasHabilesUtil;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@Stateless 
public class ModificacionEsIAFacade {
	
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;	
	@EJB
	private FeriadosFacade feriadosFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProrrogaModificacionEstudioFacade prorrogaModificacionEstudioFacade;
	@EJB
	private DiasHabilesUtil diasHabilesUtil;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private AsignarTareaFacade asignarTareaFacade;
	

	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
	
	public ProrrogaModificacionEstudio prorrogaModificacionEstudio;
	
	private Integer totalMigrados = 0;
	
	@SuppressWarnings("unchecked")
	public void obtenerTareasModificacion(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%modificarEstudioImpactoAmbiental%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.actualowner_id is not null and "
					+ "t.formname is not null and t.processid in (''rcoa.EstudioImpactoAmbiental_v2'', ''rcoa.EstudioImpactoAmbiental'') ') "
					+ "as (id varchar, usuario varchar, processinstanceid varchar, tramite varchar, fecha varchar) "
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
				try{
					String tramite = codigoProyecto[3];
					
					ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
					
					InformacionProyectoEia estudio = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
					
					ProrrogaModificacionEstudio prorroga = prorrogaModificacionEstudioFacade.getPorEstudio(estudio.getId());
					
					Date fechaActual = new Date();

					Area areaTramite = proyecto.getAreaResponsable();
					if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
						areaTramite = areaTramite.getArea();
					
					if(prorroga != null){
						if(prorroga.getNumeroDiasProrroga() != null){
							
							if (prorroga.getFechaFinModificacionProrroga().before(fechaActual) || prorroga.getFechaFinModificacionProrroga().equals(fechaActual)){
								
								Date fechaInicio = prorroga.getFechaSolicitudProrroga();
								Date fechaFinProrroga = diasHabilesUtil.recuperarFechaHabil(fechaInicio, prorroga.getNumeroDiasProrroga() - 1, false);
								
								if(fechaFinProrroga.compareTo(prorroga.getFechaFinModificacionProrroga()) <= 0) {
									Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
									String autoridad = recuperarAutoridad(areaTramite);
			    					
			    					Map<String, Object> params = new HashMap<String, Object>();
		
			    	                params.put("finalizoModificacionEstudio", true);
			    	                params.put("emitioRespuesta", false);

			    	                if(autoridad != null) {
			    	                	params.put("usuarioAutoridad", autoridad);
			    	                }
			    	                
			    	                procesoFacade.modificarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]), params);
			
			    	                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
			    	                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), data);
								} else {
									prorroga.setFechaFinModificacionProrroga(fechaFinProrroga);
									prorrogaModificacionEstudioFacade.guardar(prorroga);
								}
							}
						}else{					
							
							if (prorroga.getFechaFinModificacion().before(fechaActual) || prorroga.getFechaFinModificacion().equals(fechaActual)){
								Date fechaInicio = prorroga.getFechaInicioModificacion();
								Date fechaFin = diasHabilesUtil.recuperarFechaHabil(fechaInicio, prorroga.getNumeroDiasPorRevision() - 1, false);
								
								if(fechaFin.compareTo(prorroga.getFechaFinModificacion()) <= 0) {
									Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
									String autoridad = recuperarAutoridad(areaTramite);
			    					
			    					Map<String, Object> params = new HashMap<String, Object>();
		
			    	                params.put("finalizoModificacionEstudio", true);
			    	                params.put("emitioRespuesta", false);

			    	                if(autoridad != null) {
			    	                	params.put("usuarioAutoridad", autoridad);
			    	                }
			    	                
			    	                procesoFacade.modificarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]), params);
			
			    	                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
			    	                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), data);						
								} else {
									prorroga.setFechaFinModificacion(fechaFin);
									prorrogaModificacionEstudioFacade.guardar(prorroga);
								}
								
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String recuperarAutoridad(Area areaTramite) {
		String rolAutoridad = "";
		Usuario usuarioAutoridad = null;

		if (areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES)
				|| areaTramite.getTipoArea().getSiglas().equals("EA")) {
			rolAutoridad = "role.esia.cz.autoridad";
		} else if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
			rolAutoridad = "role.area.subsecretario.calidad.ambiental";
			List<Usuario> usuarios = usuarioFacade.buscarUsuarioPorRol(Constantes.getRoleAreaName(rolAutoridad));
			if (usuarios != null && !usuarios.isEmpty()) {
				usuarioAutoridad = usuarios.get(0);
				areaTramite = areaTramite.getArea();

				return usuarioAutoridad.getNombre();
			} else {
				System.out.println("No se encontro usuario " + Constantes.getRoleAreaName(rolAutoridad) + " en " + areaTramite.getAreaName());
			}
		} else if (areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
			rolAutoridad = "role.esia.ga.autoridad";
		}

		List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(rolAutoridad), areaTramite);
		if (usuarios != null && !usuarios.isEmpty()) {
			usuarioAutoridad = usuarios.get(0);
			return usuarioAutoridad.getNombre();
		} else {
			System.out.println("No se encontro usuario " + Constantes.getRoleAreaName(rolAutoridad) + " en " + areaTramite.getAreaName());
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void terminarTareaPago(){
		try {			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%realizarPagoTerceraRevision%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.actualowner_id is not null and "
					+ "t.formname is not null and t.processid in (''rcoa.EstudioImpactoAmbiental_v2'', ''rcoa.EstudioImpactoAmbiental'') ') "
					+ "as (id varchar, usuario varchar, processinstanceid varchar, tramite varchar, fecha varchar) "
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
				try{

					String tramite = codigoProyecto[3];
					
					ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
					
					InformacionProyectoEia estudio = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
					
					ProrrogaModificacionEstudio prorroga = prorrogaModificacionEstudioFacade.getPorEstudio(estudio.getId());
					
					Date fechaActual = new Date();
					
					if(prorroga != null){
						if(prorroga.getNumeroDiasProrroga() != null){
							
							if (prorroga.getFechaFinModificacionProrroga().before(fechaActual) || prorroga.getFechaFinModificacionProrroga().equals(fechaActual)){
								Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
		    					
		    					Map<String, Object> params = new HashMap<String, Object>();
	
		    	                params.put("pagoTerceraRevision", false);
		    	                
		    	                procesoFacade.modificarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]), params);
		
		    	                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		    	                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), data);						
								
							}
						}else{					
							
							if (prorroga.getFechaFinModificacion().before(fechaActual) || prorroga.getFechaFinModificacion().equals(fechaActual)){
								Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
		    					
		    					Map<String, Object> params = new HashMap<String, Object>();
	
		    					params.put("pagoTerceraRevision", false);
		    	                
		    	                procesoFacade.modificarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]), params);
		
		    	                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		    	                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), data);						
								
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerRegistraFechaReunion(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%registrarFechaReunionAclaratoria%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.actualowner_id is not null and "
					+ "t.formname is not null and t.processid in (''rcoa.EstudioImpactoAmbiental_v2'', ''rcoa.EstudioImpactoAmbiental'') ') "
					+ "as (id varchar, usuario varchar, processinstanceid varchar, tramite varchar, fecha varchar) "
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
				try {
					String tramite = codigoProyecto[3];
					
					ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
					
					InformacionProyectoEia estudio = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
					
					ProrrogaModificacionEstudio prorroga = prorrogaModificacionEstudioFacade.getPorEstudio(estudio.getId());
					
					Date fechaActual = new Date();
					
					if(prorroga != null){
						if(prorroga.getNumeroDiasProrroga() != null){
							
							if (prorroga.getFechaFinModificacionProrroga().before(fechaActual) || prorroga.getFechaFinModificacionProrroga().equals(fechaActual)){
								Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
		    						    	                	
		    	                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		    	                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), data);						
								
							}
						}else{					
							
							if (prorroga.getFechaFinModificacion().before(fechaActual) || prorroga.getFechaFinModificacion().equals(fechaActual)){
								Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
		    						
		    	                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		    	                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), data);						
								
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerTareaSubirActaReunion(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%subirActaReunion%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.actualowner_id is not null and "
					+ "t.formname is not null and t.processid in (''rcoa.EstudioImpactoAmbiental_v2'', ''rcoa.EstudioImpactoAmbiental'') ') "
					+ "as (id varchar, usuario varchar, processinstanceid varchar, tramite varchar, fecha varchar) "
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
				try {
					String tramite = codigoProyecto[3];
					
					ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
					
					InformacionProyectoEia estudio = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
					
					ProrrogaModificacionEstudio prorroga = prorrogaModificacionEstudioFacade.getPorEstudio(estudio.getId());
					
					Date fechaActual = new Date();
					
					if(prorroga != null){
						if(prorroga.getNumeroDiasProrroga() != null){
							
							if (prorroga.getFechaFinModificacionProrroga().before(fechaActual) || prorroga.getFechaFinModificacionProrroga().equals(fechaActual)){
								Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
		    						    	                	
		    	                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		    	                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), data);						
								
							}
						}else{					
							
							if (prorroga.getFechaFinModificacion().before(fechaActual) || prorroga.getFechaFinModificacion().equals(fechaActual)){
								Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
		    						
		    	                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		    	                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), data);						
								
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void migrarProyectosEsia() {
		try {
			String sqlProcesos="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select distinct processinstanceid, actualowner_id from task "
	            	+ "where processinstanceid in (select processinstanceid "
	            	+ "from processinstancelog where processid = ''rcoa.EstudioImpactoAmbiental'' and processversion = ''1.1'') "
	            	+ "and status = ''Reserved'' "
	            	+ "') as (processinstanceid text, actualowner_id text)"; 
			
	    	Query q_tareas = crudServiceBean.getEntityManager().createNativeQuery(sqlProcesos);		
			
			List<Object[]> listProcesosActivos = (List<Object[]>) q_tareas.getResultList();
			if (listProcesosActivos.size() > 0) {
				for (int i = 0; i < listProcesosActivos.size(); i++) {
					Object[] dataProject = (Object[]) listProcesosActivos.get(i);
					
					Long idProcesoActual = Long.parseLong(dataProject[0].toString());
					Usuario usuario = usuarioFacade.buscarUsuario(dataProject[1].toString());
					
					String sqlTareasProcesoActual="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
			            	+ "'select id, status, formname from task "
			            	+ "where processinstanceid = "+ idProcesoActual +" order by id') as (id text, status text, formname text)"; 
					
			    	List<Object[]> listTareasProcesoActual = (List<Object[]>) crudServiceBean.getEntityManager().createNativeQuery(sqlTareasProcesoActual).getResultList();
					
					Map<String, Object> parametros = procesoFacade.recuperarVariablesProceso(usuario, idProcesoActual);
					
					Boolean existeNormativaPago = Constantes.getPropertyAsBoolean("rcoa.esia.existe.normativa.pago");
					parametros.put("existeNormativaPago", existeNormativaPago);
					
					String tramite = (String) parametros.get(Constantes.VARIABLE_PROCESO_TRAMITE);
					
					Long idNuevoProceso = procesoFacade.iniciarProceso(usuario, Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2, tramite, parametros);
					
					if(listTareasProcesoActual.size() > 0) {
						for (int j = 0; j < listTareasProcesoActual.size(); j++) {							
							Object[] dataTareaProcesoActual = (Object[]) listTareasProcesoActual.get(j);
							Long idTareaProcesoActual = Long.parseLong(dataTareaProcesoActual[0].toString());
							String estadoTareaProcesoActual = dataTareaProcesoActual[1].toString();
							
							String sqlTareaActivaNuevoProceso="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
					            	+ "'select id, actualowner_id, formname from task "
					            	+ "where processinstanceid = "+ idNuevoProceso +" and status = ''Reserved'' ') as (id text, actualowner_id text, formname text)";
							
							List<Object[]> listTareaActivaNuevoProceso = (List<Object[]>) crudServiceBean.getEntityManager().createNativeQuery(sqlTareaActivaNuevoProceso).getResultList();
							
							if (listTareaActivaNuevoProceso.size() > 0) {
								Object[] dataTarea = (Object[]) listTareaActivaNuevoProceso.get(0);
								
								Long idTareaNuevoProceso = Long.parseLong(dataTarea[0].toString());
								Usuario usuarioTarea = usuarioFacade.buscarUsuario(dataTarea[1].toString());
								
								if(estadoTareaProcesoActual.equals("Completed")) {
									procesoFacade.aprobarTarea(usuarioTarea, idTareaNuevoProceso, idNuevoProceso, null);
								}
								
								if(dataTarea[2].toString().equals(dataTareaProcesoActual[2].toString())
										|| (dataTarea[2].toString().equals("firmarResponsabilidadEsia") && 
												dataTareaProcesoActual[2].toString().equals("pagarTasaRevisionEsIA"))) {
									
									igualFechaTareas(idProcesoActual, idNuevoProceso, idTareaProcesoActual, idTareaNuevoProceso);
								
								}
							}
						}
					}

					// igualar fecha proceso
					String sqlUpdateProcess = "select dblink_exec('" + Constantes.getDblinkBpmsSuiaiii() + "',"
							+ "'update processinstancelog set "
							+ "start_date = (select start_date from  processinstancelog where processinstanceid = " + idProcesoActual + "), "
							+ "parentprocessinstanceid = (select parentprocessinstanceid from  processinstancelog where processinstanceid = " + idProcesoActual + ") "
							+ "where processinstanceid = " + idNuevoProceso+ "') as result";

					Query queryUpdateProcess = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateProcess);
					if (queryUpdateProcess.getResultList().size() > 0) {
						queryUpdateProcess.getSingleResult();
					}

					abortarProcesoAnterior(idProcesoActual);
					
					//actualizar documentos del proceso
					String sqlUpdateDocumentos = "update coa_environmental_impact_study.documents_impact_study "
							+ "set dois_process_instance_id = " + idNuevoProceso 
							+ " where dois_process_instance_id = " + idProcesoActual ;

					crudServiceBean.insertUpdateByNativeQuery(sqlUpdateDocumentos.toString(), null);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void igualFechaTareas(Long idProcesoActual, Long idNuevoProceso, Long idTareaProcesoActual, Long idTareaNuevoProceso) {
		//igualar fechas tareas
		
		String sqlUpdateBamtasksummary="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','UPDATE bamtasksummary "
				 + "SET createddate = (select createddate from bamtasksummary where processinstanceid = " + idProcesoActual+" and taskid = "+idTareaProcesoActual+"),  "
				 + "enddate = (select enddate from bamtasksummary where processinstanceid = " + idProcesoActual+" and taskid = "+idTareaProcesoActual+"),  "
				 + "startdate = (select startdate from bamtasksummary where processinstanceid = " + idProcesoActual+" and taskid = "+idTareaProcesoActual+") "
				 + "WHERE processinstanceid = "+idNuevoProceso+" and taskid = "+idTareaNuevoProceso+"') as result";
		
		Query queryUpdateBamtasksummary = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateBamtasksummary);
		if (queryUpdateBamtasksummary.getResultList().size() > 0) {
			queryUpdateBamtasksummary.getSingleResult();
		}
        
        String sqlUpdateTask="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','UPDATE task "
				 + "set activationtime = (select activationtime from task where processinstanceid = "+idProcesoActual+" and id = "+idTareaProcesoActual+"),  "
				 + "createdon = (select createdon from task where processinstanceid = "+idProcesoActual+" and id = "+idTareaProcesoActual+") "
				 + "where processinstanceid = "+idNuevoProceso+" and id = "+idTareaNuevoProceso+"') as result";
        
        Query queryUpdateTask = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateTask);
		if(queryUpdateTask.getResultList().size()>0) {
			queryUpdateTask.getSingleResult();
		}
		
		String sqlTaskevent="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
			       	+ "' SELECT taskid, logtime, type FROM taskevent  "
			       	+ "WHERE taskid = "+idTareaProcesoActual+" ') as (id text, time varchar, type text)";
		
		List<Object[]> listTaskevents = (List<Object[]>) crudServiceBean.getEntityManager().createNativeQuery(sqlTaskevent).getResultList();
		
		for (int t = 0; t < listTaskevents.size(); t++) {
			Object[] dataTaskevent = (Object[]) listTaskevents.get(t);
			String sqlUpdateTaskevent="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
							 +"','update taskevent set logtime = ''"+dataTaskevent[1].toString()+"'' "
							 		+ " where taskid = "+idTareaNuevoProceso+" and type = ''"+dataTaskevent[2].toString()+"'' ') as result";
			 
			Query queryUpdate = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateTaskevent);
			if(queryUpdate.getResultList().size()>0)
				queryUpdate.getSingleResult();
		}
	}
	
	private void abortarProcesoAnterior(Long idProcesoActual) {
		abortarProcesoAnterior(idProcesoActual, null);
	}
	
	private void abortarProcesoAnterior(Long idProcesoActual, String razon) {
		//abortar proceso anterior
		
		String sqlUpdateAbortarProcess="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','update processinstancelog set status = 4, " 
				 +" delete_reason = ''" + razon + "'', "
				 +" date_delete_reason = now()"
				 +" where processinstanceid = "+idProcesoActual+"') as result";
		 
		 Query queryUpdateAbortarProcess = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateAbortarProcess);
        if(queryUpdateAbortarProcess.getResultList().size()>0){
        	queryUpdateAbortarProcess.getSingleResult();
        }
        
        String sqlUpdateAbortarSumm="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','update bamtasksummary set status = ''Exited'' "
				 + "where processinstanceid =  "+idProcesoActual+" and status not in (''Completed'') ') as result";
		 
		 Query queryUpdateAbortarSumm = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateAbortarSumm);
        if(queryUpdateAbortarSumm.getResultList().size()>0) {
        	queryUpdateAbortarSumm.getSingleResult();
        }
        
        String sqlUpdate="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','update task set status = ''Exited'' "
				 + "where processinstanceid = "+idProcesoActual+" and status not in (''Completed'')') as result";
		 
		 Query queryUpdate = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdate);
        if(queryUpdate.getResultList().size()>0) {
        	queryUpdate.getSingleResult();
        }
	}

	@SuppressWarnings("unchecked")
	public void actualizarFechasProrroga(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"', "
					+ "'select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%modificarEstudioImpactoAmbiental%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.actualowner_id is not null and "
					+ "t.formname is not null and t.processid in (''rcoa.EstudioImpactoAmbiental_v2'', ''rcoa.EstudioImpactoAmbiental'') ') "
					+ "as (id varchar, usuario varchar, processinstanceid varchar, tramite varchar, fecha varchar) "
					+ "order by 1";	
			
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(
					sql);
			List<Object> resultList = new ArrayList<Object>();
			resultList = query.getResultList();
			List<String[]> listaCodigosProyectos = new ArrayList<String[]>();
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add(new String[] { (String) row[0], (String) row[1], (String) row[2], (String) row[3], (String) row[4] });
				}
			}

			for (String[] codigoProyecto : listaCodigosProyectos) {
				String tramite = codigoProyecto[3];

				try {
					Boolean fechasActualizadas = false;
					Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
					Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]));

					if(variables.get("actualizacionFechasModificacion") != null){
						fechasActualizadas = Boolean.parseBoolean(variables.get("actualizacionFechasModificacion").toString());	
					}

					if(!fechasActualizadas) {
						String revision = (String) variables.get("numeroRevision");
						Integer numeroRevision = 0;
						if(revision != null) {
							numeroRevision = Integer.parseInt(revision);
						}

						ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);

						InformacionProyectoEia estudio = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);

						ProrrogaModificacionEstudio prorroga = prorrogaModificacionEstudioFacade.getPorEstudioRevision(estudio.getId(), numeroRevision);

						if (prorroga != null) {
							if (prorroga.getFechaInicioModificacion() != null) {
								Date fechaInicioActual = prorroga.getFechaInicioModificacion();
								Date fechaInicio = diasHabilesUtil.recuperarFechaHabil(fechaInicioActual, 1, true); //se agrega 1 dia porque el plazo inicia desde mañana o el siguiente dia habil
								Date fechaFin = diasHabilesUtil.recuperarFechaHabil(fechaInicioActual, prorroga.getNumeroDiasPorRevision(), false);

								prorroga.setFechaInicioModificacion(fechaInicio);
								prorroga.setFechaFinModificacion(fechaFin);
							}
							if (prorroga.getNumeroDiasProrroga() != null) {
								Date fechaFinMod = prorroga.getFechaFinModificacion(); //la prorroga se debe agregar a los días q tiene disponible el operador
								Date fechaInicioProrroga = diasHabilesUtil.recuperarFechaHabil(fechaFinMod, 1, true); //se agrega 1 dia porque el plazo inicia desde mañana o el siguiente dia habil
								Date fechaFinProrroga = diasHabilesUtil.recuperarFechaHabil(fechaFinMod, prorroga.getNumeroDiasProrroga(), false);

								prorroga.setFechaSolicitudProrroga(fechaInicioProrroga);
								prorroga.setFechaFinModificacionProrroga(fechaFinProrroga);
							}

							prorrogaModificacionEstudioFacade.guardar(prorroga);
							
							Map<String, Object> params = new HashMap<String, Object>();
	    	                params.put("actualizacionFechasModificacion", true);
	    	                procesoFacade.modificarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]), params);

							System.out.println("Actualización de fechas de EsIA del proyecto " + tramite);
						} else {
							System.out.println("No se actualiza fechas de EsIA del proyecto " + tramite);
						}
					}
					
				} catch (Exception e) {
					System.out.println("Error en la actualización de fechas EsIA del proyecto " + tramite);
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@SuppressWarnings("unchecked")
	public String iniciarNuevoBypass(){
		try {
			
			totalMigrados = 0;
			
			//listado de proyectos aprobados estudio detenidos por la sentencia
			String sql="select prco_id, prco_cua, prco_cua_date, prco_project_finished,	prco_project_completion_date "
					+ " from coa_mae.project_licencing_coa p "
					+ " join (select * FROM dblink('" + dblinkBpmsSuiaiii + "', "
					+ " 'select DISTINCT value from ( "
					+ " select v.value, (select value from variableinstancelog  "
					+ " where processinstanceid = v.processinstanceid "
					+ " and variableid = ''tipoPronunciamiento'' "
					+ " order by id desc  "
					+ " limit 1) as pronunciamiento "
					+ " from variableinstancelog v "
					+ " where v.variableid = ''tramite'' "
					+ " and v.processinstanceid in (select processinstanceid "
					+ " from processinstancelog p "
					+ " where processid in (''rcoa.EstudioImpactoAmbiental_v2'', ''rcoa.EstudioImpactoAmbiental'') "
					+ " and status = 2)) as t1 "
					+ " where t1.pronunciamiento = ''1'' ') t1(tramite varchar)"
					+ " ) y_3 ON y_3.tramite = p.prco_cua "
					+ " where prco_cua_date >= '2021-10-12' "
					+ " and prco_categorizacion in (3,4)  "
					+ " and prco_status = true "
					+ " order by prco_cua;";	
			
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object> resultList = new ArrayList<Object>();
			resultList = query.getResultList();
			List<String> listaCodigosProyectos = new ArrayList<String>();
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add((String) row[1]);
				}
			}

			for (String codigoProyecto : listaCodigosProyectos) {
				try {
					//validar si ya tiene iniciado el nuevo PPC rcoa.PpcConsultaAmbiental
					String sqlProcesoPpc = "select * from dblink('"+dblinkBpmsSuiaiii+"', "
							+ "'select p.processinstanceid "
							+ "from processinstancelog p "
							+ "inner join variableinstancelog v on p.processinstanceid = v.processinstanceid "
							+ "where v.variableid = ''tramite'' "
							+ "and v.value = ''" + codigoProyecto + "'' "
							+ "and p.processid in (''rcoa.PpcConsultaAmbiental'') ') "
							+ "as (processinstanceid varchar) "
							+ "order by 1";
					
					Query queryPpc = crudServiceBean.getEntityManager().createNativeQuery(sqlProcesoPpc);
					List<Object> resultListPpc = queryPpc.getResultList();
					if (resultListPpc.size() > 0) {
						continue;
					}
					
					//valido si el proyecto tiene procesos activos para finalizarlos
					String sqlProcesos = "select * from dblink('"+dblinkBpmsSuiaiii+"', "
							+ "'select t.processinstanceid, t.processid "
							+ "from task t "
							+ "inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
							+ "where v.variableid = ''tramite'' "
							+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
							+ "and t.actualowner_id is not null "
							+ "and v.value = ''" + codigoProyecto + "'' ') "
							+ "as (processinstanceid varchar, process varchar) "
							+ "order by 1";
					
					Query q_tareas = crudServiceBean.getEntityManager().createNativeQuery(sqlProcesos);	
					
					List<Object[]> listProcesosActivos = (List<Object[]>) q_tareas.getResultList();
					if (listProcesosActivos.size() > 0) {
						for (int i = 0; i < listProcesosActivos.size(); i++) {
							Object[] dataProject = (Object[]) listProcesosActivos.get(i);
							
							Long idProcesoActual = Long.parseLong(dataProject[0].toString());
							String proceso = dataProject[1].toString();
							
							System.out.println("Proyecto " + codigoProyecto + "    abortar proceso " + proceso + " e iniciar");
							abortarProcesoAnterior(idProcesoActual, "Abortado por inicio de nuevo proceso PPC Consulta Ambiental");
						}
						
						iniciarProcesoNuevoBypass(codigoProyecto);
						
					} else {
						System.out.println("Proyecto " + codigoProyecto + "    iniciar Bypass");
						
						iniciarProcesoNuevoBypass(codigoProyecto);
					}
					
				} catch (Exception e) {
					System.out.println("Error al procesar el proyecto " + codigoProyecto);
					e.printStackTrace();
				}
			}
			
			return "Proyectos migrados " + totalMigrados + " de " + listaCodigosProyectos.size(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void iniciarProcesoNuevoBypass(String tramite) throws JbpmException {
		ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		InformacionProyectoEia estudio = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
		
		Usuario usuarioTarea = usuarioFacade.buscarUsuario(proyecto.getUsuarioCreacion());
		
		String tecnicoEstudio = recuperarTecnicoEstudio(tramite);
		
		String tecnicoResponsable = recuperarTecnicoResponsable(proyecto, tecnicoEstudio);
		
		if(tecnicoResponsable != null) {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("operador", usuarioTarea.getNombre());
			parametros.put("tramite", tramite);					
			parametros.put("idProyecto", proyecto.getId());
			parametros.put("numeroFacilitadores", estudio.getNumeroFacilitadores());
			parametros.put("facilitadorAdicional", false);
			parametros.put("tecnicoResponsable", tecnicoResponsable);
			parametros.put("iniciadoAutomaticoPpc", new Date());
			
			procesoFacade.iniciarProceso(usuarioTarea, Constantes.RCOA_PROCESO_BYPASS_PARTICIPACION_CIUDADANA, tramite, parametros);
			
			totalMigrados++;
		} else {
			System.out.println("Error al iniciar proceso de proyecto " + tramite);
		}
	}

	private String recuperarTecnicoResponsable(ProyectoLicenciaCoa proyecto, String tecnicoEstudio) {
		String tipoRol = "role.esia.cz.tecnico.responsable";

		Area areaResponsable = proyecto.getAreaResponsable();
		if (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
			ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
			CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();

			Integer idSector = actividadPrincipal.getTipoSector().getId();

			tipoRol = "role.esia.pc.tecnico.responsable.tipoSector." + idSector;
		} else if (!areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
			tipoRol = "role.esia.gad.tecnico.responsable";
		}

		String rolTecnico = Constantes.getRoleAreaName(tipoRol);

		// buscar usuarios por rol y area
		List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
		if (listaUsuario == null || listaUsuario.size() == 0) {
			System.out.println("No se encontró técnico responsable en " + areaResponsable.getAreaName());
			return null;
		}
		
		Usuario tecnicoResponsable = null;
		
		if (tecnicoEstudio != null) {
			// recuperar tecnico de bpm y validar si el usuario existe en el listado anterior
			Usuario usuarioTecnico = usuarioFacade.buscarUsuario(tecnicoEstudio);
			if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
				if (listaUsuario != null && listaUsuario.size() >= 0
						&& listaUsuario.contains(usuarioTecnico)) {
					tecnicoResponsable = usuarioTecnico;
				}
			}
		}
		
		// si no se encontró el usuario se realiza la busqueda de uno nuevo
		// y se actualiza en el bpm
		if (tecnicoResponsable == null) {

			String proceso = Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL
					+ "'', ''" + Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2;

			List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosProceso(listaUsuario, proceso);
			tecnicoResponsable = listaTecnicosResponsables.get(0);
		}
		
		if(tecnicoResponsable != null) {
			return tecnicoResponsable.getNombre();
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private String recuperarTecnicoEstudio(String tramite) {
		String sql = "select * from dblink('"+dblinkBpmsSuiaiii+"', "
				+ "'select value"
				+ " from variableinstancelog v "
				+ " where v.variableid = ''tecnicoResponsable'' "
				+ " and processinstanceid in (select distinct processinstanceid "
				+ " from variableinstancelog v "
				+ " where v.variableid = ''tramite'' "
				+ " and value = ''" + tramite + "'' "
				+ " and processid in (''rcoa.EstudioImpactoAmbiental_v2'', ''rcoa.EstudioImpactoAmbiental'') ) "
				+ " order by processinstanceid desc "
				+ " limit 1 ') "
				+ " as (valor varchar) "
				+ " order by 1";
		
		Query queryTecnico = crudServiceBean.getEntityManager().createNativeQuery(sql);
		
		List<Object> resultList = queryTecnico.getResultList();
		if (resultList.size() > 0) {
			return resultList.get(0).toString();
		}
		
		return null;
	}

}
