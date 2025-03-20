package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.EnvioNotificacionesMail;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.notificaciones.facade.EnvioNotificacionesMailFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class NotificacionDiagnosticoAmbientalFacade {
	
	private static final Logger LOG = Logger.getLogger(NotificacionDiagnosticoAmbientalFacade.class);
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciCoaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private EnvioNotificacionesMailFacade envioNotificacionesMailFacade;
	
	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
	
	@SuppressWarnings("unchecked")
	public void obtenerTareasFirma(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, v.value, t.actualowner_id, t.processinstanceid, t.formname  "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "inner join processinstancelog p on p.processinstanceid = t.processinstanceid "
					+ "where t.formname in (''revisarInformacionRegistroPreliminar'', ''revisarOficioDiagnosticoAmbiental'') and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') and p.processversion in (''1.1'', ''1.2'') "
					+ "and t.formname is not null and t.processid in (''rcoa.RegistroPreliminar'') and t.actualowner_id is not null')  "
					+ "as (id varchar, tramite varchar, usuario varchar, processinstanceid varchar, tarea varchar) "
					+ "order by 1 ";
			
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1], (String) row[2] ,(String) row[3], (String) row[4]});
				}
			}
			
			for (String[] codigoProyecto : listaCodigosProyectos) {	
				
				try {
					
					boolean correctoPasoTarea = false;
					Usuario usuarioAutoridad =usuarioFacade.buscarUsuarioWithOutFilters(codigoProyecto[2]);	
					
					if(usuarioAutoridad == null){
						LOG.error("Diagnostico ambiental: " + codigoProyecto[0] + " usuario tecnico inactivo");
						continue;
					}
					
					Map<String, Object> variables =procesoFacade.recuperarVariablesProceso(usuarioAutoridad, Long.parseLong(codigoProyecto[3]));
					String numeroRevisionString=(String)variables.get("numeroRevisionDiagnostico");	
					Integer numeroRevision = null;
					if(numeroRevisionString != null){
						numeroRevision = Integer.valueOf(numeroRevisionString);
					}					
										
					if(numeroRevision != null && numeroRevision <= 1){					
											
						String idTask = codigoProyecto[0];
						String codigo = codigoProyecto[1];
						String tarea = codigoProyecto[4];						
						
						ProyectoLicenciaCoa proyecto = proyectoLicenciCoaFacade.buscarProyecto(codigo);
						
						Map<String, Object> params=new HashMap<>();
						params.put("notificacionDiagnostico",true);
						
						if(tarea.equals("revisarOficioDiagnosticoAmbiental")){
							params.put("observacionesRevisionDiagnostico", true);
							params.put("observacionesPronunciamiento",false);
						}else{
							params.put("autoridadCompetente", usuarioAutoridad.getNombre());
						}
						
						
						try {
							procesoFacade.modificarVariablesProceso(usuarioAutoridad, Long.parseLong(codigoProyecto[3]), params);
							procesoFacade.aprobarTarea(usuarioAutoridad, Long.valueOf(idTask), Long.parseLong(codigoProyecto[3]), null);
							
							if(tarea.equals("revisarOficioDiagnosticoAmbiental")){
								correctoPasoTarea = true;
							}						
							
							//eliminar tarea del listado de tareas de preliminar
							actualizarTarea(Integer.valueOf(idTask));			
							
						} catch (Exception e) {
							LOG.error("Diagnostico ambiental: " + codigoProyecto[0] + " error al enviar notificacion");
							LOG.error("Error al modificar variables o aprobar la primera tarea", e );
						}
						
												
						String sqlDescarga="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, v.value, t.actualowner_id, t.formname  "
								+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
								+ "where t.formname = ''revisarOficioDiagnosticoAmbiental'' and v.variableid = ''tramite'' and v.value = ''" + codigo +"'' "
								+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
								+ "and t.formname is not null and t.processid = ''rcoa.RegistroPreliminar'' ') "
								+ "as (id varchar, tramite varchar, usuario varchar, tarea varchar) "
								+ "order by 1";	
						
						Query queryFirma = crudServiceBean.getEntityManager().createNativeQuery(sqlDescarga);
						List<Object>  resultListDescarga = new ArrayList<Object>();
						resultListDescarga=queryFirma.getResultList();
						List<String[]>listaCodigosProyectosDescarga= new ArrayList<String[]>();		
						if (resultListDescarga.size() > 0) {
							for (Object a : resultListDescarga) {
								Object[] row = (Object[]) a;
								listaCodigosProyectosDescarga.add(new String[] { (String) row[0],(String) row[1], (String) row[2], (String) row[3]});
							}
							
							for (String[] codigoPr : listaCodigosProyectosDescarga) {
								String idTarea = codigoPr[0];		
								tarea = codigoPr[3];
																
								Usuario usuarioTarea = usuarioFacade.buscarUsuarioWithOutFilters(codigoPr[2]);	
								Map<String, Object> paramsTarea=new HashMap<>();
								paramsTarea.put("observacionesPronunciamiento",false);
								paramsTarea.put("observacionesRevisionDiagnostico", true);
								
								
								try {
									procesoFacade.modificarVariablesProceso(usuarioTarea, Long.parseLong(codigoProyecto[3]), paramsTarea);
									procesoFacade.aprobarTarea(usuarioTarea, Long.valueOf(idTarea), Long.parseLong(codigoProyecto[3]), null);
//									
									correctoPasoTarea = true;
									
									actualizarTarea(Integer.valueOf(idTarea));
									break;
								} catch (Exception e) {
									correctoPasoTarea = false;
									LOG.error("Diagnostico ambiental: " + codigoProyecto[0] + " error al enviar notificacion");
									LOG.error("Error al modificar variables o aprobar la segunda tarea", e);
								}								
							}						
							
						}
						
						System.out.println("Proceso ejecutado para proyecto: " + codigoProyecto[1]);
						
						if(tarea.equals("revisarOficioDiagnosticoAmbiental") && correctoPasoTarea){
							enviarMailOperador(proyecto, Long.parseLong(codigoProyecto[3]));
						}	
										 
					}	
				} catch (Exception e) {
					LOG.error("Diagnostico ambiental: " + codigoProyecto[0] + " error al enviar notificacion");
					LOG.error("Error al notificar Diagnostico ambiental", e);
				}				
				
			}
			
		}catch (Exception e) {
			LOG.error("Error al ejecutar la generación de certificado ambientales", e);
		}
	}
	
	public void actualizarTarea(Integer idTarea){
		try {
			
			String sqlTareaBam = "select from dblink_exec('"
					+ dblinkBpmsSuiaiii + "', " + "'UPDATE bamtasksummary "
					+ " SET status = ''Exited'' " + " WHERE taskid = "
					+ idTarea + " ')";

			Query queryTareaBam = crudServiceBean.getEntityManager()
					.createNativeQuery(sqlTareaBam);

			queryTareaBam.getResultList();  
			
			String sqlTareaTask = "select from dblink_exec('"
					+ dblinkBpmsSuiaiii + "', " + "'UPDATE task "
					+ " SET status = ''Exited'' " + " WHERE id = "
					+ idTarea + " ')";

			Query queryTareaTask = crudServiceBean.getEntityManager()
					.createNativeQuery(sqlTareaTask);

			queryTareaTask.getResultList();  
			
		} catch (Exception e) {
			LOG.error("Diagnostico ambiental id Tarea: " + idTarea + " error al modificar tarea");
			LOG.error("Error al actualizar tareas", e);
		}
		
	}
	
	public void enviarMailOperador(ProyectoLicenciaCoa proyecto, long idProcess){
		try {					
			
			Organizacion organizacion = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());
			
			List<Contacto> listaContactos = new ArrayList<Contacto>();
			if(organizacion != null && organizacion.getId() != null){
				listaContactos = contactoFacade.buscarPorOrganizacion(organizacion);
			}else{
				listaContactos = contactoFacade.buscarPorPersona(proyecto.getUsuario().getPersona());
			}			
			
			String emailOperador = "";
			for(Contacto contacto : listaContactos){
				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
					emailOperador = contacto.getValor();
					break;
				}						
			}					
			
			Object[] parametrosCorreo = new Object[] {proyecto.getUsuario().getPersona().getNombre(), 
					proyecto.getUsuario().getPersona().getNombre(), proyecto.getNombreProyecto(),
					proyecto.getCodigoUnicoAmbiental()};
			
			String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
							"bodyNotificacionCargarDiagnosticoAmbiental",
							parametrosCorreo);			
			
			EnvioNotificacionesMail envioNotificacionesMail = new EnvioNotificacionesMail();
			envioNotificacionesMail.setCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
			envioNotificacionesMail.setEmail(emailOperador); 
			envioNotificacionesMail.setAsunto("Continuar el proceso de regularización ambiental");
			envioNotificacionesMail.setContenido(notificacion);
			envioNotificacionesMail.setProcesoId((int) idProcess);
			envioNotificacionesMail.setTareaId(null);
			envioNotificacionesMail.setUsuarioDestinoId(proyecto.getUsuario().getId());
			envioNotificacionesMail.setNombreUsuarioDestino(proyecto.getUsuario().getPersona().getNombre());
			envioNotificacionesMail.setEsVisible(false);
			
			envioNotificacionesMailFacade.save(envioNotificacionesMail);
			
			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailInformacionProponente(emailOperador, "Sistema de Regularización y Control Ambiental", notificacion, "Continuar el proceso de regularización ambiental", proyecto.getCodigoUnicoAmbiental(), proyecto.getUsuario(), null);			
			
			envioNotificacionesMail.setEnviado(true);
			envioNotificacionesMail.setFechaEnvio(new Date());
			
			envioNotificacionesMailFacade.save(envioNotificacionesMail);
			
		} catch (Exception e) {
			LOG.error("Error en envio de notificación", e);
		}		
	}	
	
	
	//Metodo para pasar tarea de plan de accion
	
	@SuppressWarnings("unchecked")
	public void finalizarDiagnostico(String codigoProy){
		try {
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, v.value, t.actualowner_id, t.processinstanceid, t.formname  "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "inner join processinstancelog p on p.processinstanceid = t.processinstanceid "
					+ "where t.formname = ''revisarInformacionRegistroPreliminar'' and v.variableid = ''tramite'' and v.value = ''" + codigoProy +"'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.formname is not null and t.processid = ''rcoa.RegistroPreliminar'' and t.actualowner_id is not null')  "
					+ "as (id varchar, tramite varchar, usuario varchar, processinstanceid varchar, tarea varchar) "
					+ "order by 1";						
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1], (String) row[2] ,(String) row[3], (String) row[4]});
				}
			}
			
			for (String[] codigoProyecto : listaCodigosProyectos) {	
				
				Usuario usuarioAutoridad =usuarioFacade.buscarUsuario(codigoProyecto[2]);								
				
				String idTask = codigoProyecto[0];					
										
				Map<String, Object> params=new HashMap<>();								
				params.put("autoridadCompetente", usuarioAutoridad.getNombre());						
										
				try {
					procesoFacade.modificarVariablesProceso(usuarioAutoridad, Long.parseLong(codigoProyecto[3]), params);
					procesoFacade.aprobarTarea(usuarioAutoridad, Long.valueOf(idTask), Long.parseLong(codigoProyecto[3]), null);										
					
					//eliminar tarea del listado de tareas de preliminar
					actualizarTarea(Integer.valueOf(idTask));			
					
				} catch (Exception e) {
					LOG.error("Error al modificar variables o aprobar la primera tarea", e );
				}		
					
			}
			
			String sqlDescarga="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, v.value, t.actualowner_id, t.processinstanceid  "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname = ''revisarOficioDiagnosticoAmbiental'' and v.variableid = ''tramite'' and v.value = ''" + codigoProy +"'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.formname is not null and t.processid = ''rcoa.RegistroPreliminar'' ') "
					+ "as (id varchar, tramite varchar, usuario varchar, processinstanceid varchar) "
					+ "order by 1";	
			
			Query queryFirma = crudServiceBean.getEntityManager().createNativeQuery(sqlDescarga);
			List<Object>  resultListDescarga = new ArrayList<Object>();
			resultListDescarga=queryFirma.getResultList();
			List<String[]>listaCodigosProyectosDescarga= new ArrayList<String[]>();		
			if (resultListDescarga.size() > 0) {
				for (Object a : resultListDescarga) {
					Object[] row = (Object[]) a;
					listaCodigosProyectosDescarga.add(new String[] { (String) row[0],(String) row[1], (String) row[2], (String) row[3]});
				}
				
				for (String[] codigoPr : listaCodigosProyectosDescarga) {
					String idTarea = codigoPr[0];
													
					Usuario usuarioTarea = usuarioFacade.buscarUsuario(codigoPr[2]);	
					Map<String, Object> paramsTarea=new HashMap<>();
					paramsTarea.put("observacionesPronunciamiento",false);
					paramsTarea.put("observacionesRevisionDiagnostico", false);
					
					
					try {
						procesoFacade.modificarVariablesProceso(usuarioTarea, Long.parseLong(codigoPr[3]), paramsTarea);
						procesoFacade.aprobarTarea(usuarioTarea, Long.valueOf(idTarea), Long.parseLong(codigoPr[3]), null);
															
						actualizarTarea(Integer.valueOf(idTarea));
						break;
					} catch (Exception e) {
						LOG.error("Error al modificar variables o aprobar la segunda tarea", e);
					}								
				}						
				
			}	
			
			//tarea descargar pronunciamiento						
			String sqlPronunciamiento="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, v.value, t.actualowner_id, t.processinstanceid  "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname = ''recibirOficioAprobacionDiagnostico'' and v.variableid = ''tramite'' and v.value = ''" + codigoProy +"'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.formname is not null and t.processid = ''rcoa.RegistroPreliminar'' ') "
					+ "as (id varchar, tramite varchar, usuario varchar, processintanceid varchar) "
					+ "order by 1";	
			
			Query queryPronunciamiento = crudServiceBean.getEntityManager().createNativeQuery(sqlPronunciamiento);
			List<Object>  resultListPronunciamiento = new ArrayList<Object>();
			resultListPronunciamiento=queryPronunciamiento.getResultList();
			List<String[]>listaCodigosProyectosPronunciamiento= new ArrayList<String[]>();	
			
			if (resultListPronunciamiento.size() > 0) {
				for (Object a : resultListPronunciamiento) {
					Object[] row = (Object[]) a;
					listaCodigosProyectosPronunciamiento.add(new String[] { (String) row[0],(String) row[1], (String) row[2], (String) row[3]});
				}
				
				for (String[] codigoPr : listaCodigosProyectosPronunciamiento) {
					String idTarea = codigoPr[0];
													
					Usuario usuarioTarea = usuarioFacade.buscarUsuario(codigoPr[2]);	
					Map<String, Object> paramsTarea=new HashMap<>();
					paramsTarea.put("iniciaProceso", true);
					paramsTarea.put("diagnosticoAmbientalFinalizadoAutomatico", true);
					
					try {
						procesoFacade.modificarVariablesProceso(usuarioTarea, Long.parseLong(codigoPr[3]), paramsTarea);
						procesoFacade.aprobarTarea(usuarioTarea, Long.valueOf(idTarea), Long.parseLong(codigoPr[3]), null);
															
						actualizarTarea(Integer.valueOf(idTarea));
						break;
					} catch (Exception e) {
						LOG.error("Error al modificar variables o aprobar la tercer tarea", e);
					}								
				}							
			}		
			
		} catch (Exception e) {
			LOG.error("Error al finalizar el proceso de diagnostico", e);
		}
	}

}
