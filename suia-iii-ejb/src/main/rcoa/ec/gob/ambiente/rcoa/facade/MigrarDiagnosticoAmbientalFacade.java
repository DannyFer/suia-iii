package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

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
public class MigrarDiagnosticoAmbientalFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private EnvioNotificacionesMailFacade envioNotificacionesMailFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciCoaFacade;
	
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void avanzarProceso(Object[] dataProject) {
		try {
			
			Long idProcesoActual = Long.parseLong(dataProject[0].toString());
			Usuario usuario = usuarioFacade.buscarUsuarioWithOutFilters(dataProject[1].toString());
			
			System.out.println("--Migrando tramite proceso: " + idProcesoActual);
			
			String sqlTareasProcesoActual="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select id, status, formname from task "
	            	+ "where processinstanceid = "+ idProcesoActual +" order by id') as (id text, status text, formname text)"; 
			
	    	List<Object[]> listTareasProcesoActual = (List<Object[]>) crudServiceBean.getEntityManager().createNativeQuery(sqlTareasProcesoActual).getResultList();
			
			Map<String, Object> parametros = procesoFacade.recuperarVariablesProceso(usuario, idProcesoActual);
			
			String tramite = (String) parametros.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			
			parametros = obtenerParametros(parametros);
			
			Long idNuevoProceso = procesoFacade.iniciarProceso(usuario, Constantes.RCOA_REGISTRO_PRELIMINAR, tramite, parametros);
			
			//avanza tarea de descarga y firma
			String sqlTareaActivaNuevoProceso="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select id, actualowner_id, formname from task "
	            	+ "where processinstanceid = "+ idNuevoProceso +" and status = ''Reserved'' ') as (id text, actualowner_id text, formname text)";
			
			List<Object[]> listTareaActivaNuevoProceso = (List<Object[]>) crudServiceBean.getEntityManager().createNativeQuery(sqlTareaActivaNuevoProceso).getResultList();
			
			if (listTareaActivaNuevoProceso.size() > 0) {
				Object[] dataTarea = (Object[]) listTareaActivaNuevoProceso.get(0);
				
				Long idTareaNuevoProceso = Long.parseLong(dataTarea[0].toString());
				Usuario usuarioTarea = usuarioFacade.buscarUsuario(dataTarea[1].toString());
				
				procesoFacade.aprobarTarea(usuarioTarea, idTareaNuevoProceso, idNuevoProceso, null);
				
				int j = 0;
				Object[] dataTareaProcesoActual = (Object[]) listTareasProcesoActual.get(j);
				Long idTareaProcesoActual = Long.parseLong(dataTareaProcesoActual[0].toString());
				
				igualFechaTareas(idProcesoActual, idNuevoProceso, idTareaProcesoActual, idTareaNuevoProceso);
			}
			
			//iguala fecha tarea de cargar diagnostico
			String sqlTareaActivaNuevoProceso2="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select id, actualowner_id, formname from task "
	            	+ "where processinstanceid = "+ idNuevoProceso +" and status = ''Reserved'' ') as (id text, actualowner_id text, formname text)";
			
			List<Object[]> listTareaActivaNuevoProceso2 = (List<Object[]>) crudServiceBean.getEntityManager().createNativeQuery(sqlTareaActivaNuevoProceso2).getResultList();
			
			if (listTareaActivaNuevoProceso2.size() > 0) {
				Object[] dataTarea = (Object[]) listTareaActivaNuevoProceso2.get(0);
				
				Long idTareaNuevoProceso = Long.parseLong(dataTarea[0].toString());
				
				int j = listTareasProcesoActual.size() - 1;
				Object[] dataTareaProcesoActual = (Object[]) listTareasProcesoActual.get(j);
				Long idTareaProcesoActual = Long.parseLong(dataTareaProcesoActual[0].toString());
				
				igualFechaTareas(idProcesoActual, idNuevoProceso, idTareaProcesoActual, idTareaNuevoProceso);
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
			String sqlUpdateDocumentos = "update coa_mae.documents_coa "
					+ "set docu_process_instance_id = " + idNuevoProceso 
					+ " where docu_process_instance_id = " + idProcesoActual ;

			crudServiceBean.insertUpdateByNativeQuery(sqlUpdateDocumentos.toString(), null);
			
			System.out.println("--Migrado tramite OK: " + tramite + " procesoAnterior: " + idProcesoActual + "  procesoNuevo:" + idNuevoProceso);
			
			ProyectoLicenciaCoa proyecto = proyectoLicenciCoaFacade.buscarProyecto(tramite);
			enviarMailOperador(proyecto, idProcesoActual);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private Map<String, Object> obtenerParametros(Map<String, Object> parametros) {
		parametros.put("tipoProyecto", Integer.parseInt((String) parametros.get("tipoProyecto")));
		parametros.put("u_categoria", Integer.parseInt((String) parametros.get("u_categoria")));
		
		Boolean requierePagoInicial = parametros.containsKey("requierePagoInicial") ? (Boolean.valueOf((String) parametros.get("requierePagoInicial"))) : null;
		Boolean zonificacionBiodiversidad = parametros.containsKey("zonificacionBiodiversidad") ? (Boolean.valueOf((String) parametros.get("zonificacionBiodiversidad"))) : null;
		Boolean esPermitidaActividad = parametros.containsKey("esPermitidaActividad") ? (Boolean.valueOf((String) parametros.get("esPermitidaActividad"))) : null;
		Boolean esActividadExtractiva = parametros.containsKey("esActividadExtractiva") ? (Boolean.valueOf((String) parametros.get("esActividadExtractiva"))) : null;
		Boolean tieneExcepcion = parametros.containsKey("tieneExcepcion") ? (Boolean.valueOf((String) parametros.get("tieneExcepcion"))) : null;
		Boolean esProyectoEnEjecucion = parametros.containsKey("esProyectoEnEjecucion") ? (Boolean.valueOf((String) parametros.get("esProyectoEnEjecucion"))) : null;
		
		Boolean intersecaSnapForestIntanManglar = parametros.containsKey("intersecaSnapForestIntanManglar") ? (Boolean.valueOf((String) parametros.get("intersecaSnapForestIntanManglar"))) : null;
		Boolean generaDesecho = parametros.containsKey("generaDesecho") ? (Boolean.valueOf((String) parametros.get("generaDesecho"))) : null;
		Boolean coberturaVegetal = parametros.containsKey("coberturaVegetal") ? (Boolean.valueOf((String) parametros.get("coberturaVegetal"))) : null;
		Boolean esCertificadoRegistro = parametros.containsKey("esCertificadoRegistro") ? (Boolean.valueOf((String) parametros.get("esCertificadoRegistro"))) : null;
		Boolean gadMunicipalDesecho = parametros.containsKey("gadMunicipalDesecho") ? (Boolean.valueOf((String) parametros.get("gadMunicipalDesecho"))) : null;
		Boolean gestionaResiduoTransporte = parametros.containsKey("gestionaResiduoTransporte") ? (Boolean.valueOf((String) parametros.get("gestionaResiduoTransporte"))) : null;
		Boolean empleaSustanciaQuimica = parametros.containsKey("empleaSustanciaQuimica") ? (Boolean.valueOf((String) parametros.get("empleaSustanciaQuimica"))) : null;
		
		if(requierePagoInicial != null) {
			parametros.put("requierePagoInicial", requierePagoInicial);
		}
		
		if(zonificacionBiodiversidad != null) {
			parametros.put("zonificacionBiodiversidad", zonificacionBiodiversidad);
		}
		
		if(esPermitidaActividad != null) {
			parametros.put("esPermitidaActividad", esPermitidaActividad);
		}
		
		if(esActividadExtractiva != null) {
			parametros.put("esActividadExtractiva", esActividadExtractiva);
		}
		
		if(tieneExcepcion != null) {
			parametros.put("tieneExcepcion", tieneExcepcion);
		}
		
		if(esProyectoEnEjecucion != null) {
			parametros.put("esProyectoEnEjecucion", esProyectoEnEjecucion);
		}
		
		if(intersecaSnapForestIntanManglar != null) {
			parametros.put("intersecaSnapForestIntanManglar", intersecaSnapForestIntanManglar);
		}
		
		if(generaDesecho != null) {
			parametros.put("generaDesecho", generaDesecho);
		}
		
		if(coberturaVegetal != null) {
			parametros.put("coberturaVegetal", coberturaVegetal);
		}
		
		if(esCertificadoRegistro != null) {
			parametros.put("esCertificadoRegistro", esCertificadoRegistro);
		}
		
		if(gadMunicipalDesecho != null) {
			parametros.put("gadMunicipalDesecho", gadMunicipalDesecho);
		}
		
		if(gestionaResiduoTransporte != null) {
			parametros.put("gestionaResiduoTransporte", gestionaResiduoTransporte);
		}
		
		if(empleaSustanciaQuimica != null) {
			parametros.put("empleaSustanciaQuimica", empleaSustanciaQuimica);
		}
		
		Boolean esPrimeraVez = parametros.containsKey("esPrimeraVez") ? (Boolean.valueOf((String) parametros.get("esPrimeraVez"))) : null;
		Boolean u_intersecaSnap = parametros.containsKey("u_intersecaSnap") ? (Boolean.valueOf((String) parametros.get("u_intersecaSnap"))) : null;
		Boolean u_intersecaForestal = parametros.containsKey("u_intersecaForestal") ? (Boolean.valueOf((String) parametros.get("u_intersecaForestal"))) : null;
		Boolean u_existeInventarioForestal = parametros.containsKey("u_existeInventarioForestal") ? (Boolean.valueOf((String) parametros.get("u_existeInventarioForestal"))) : null;
		Boolean u_emisionVariosProyectos = parametros.containsKey("u_emisionVariosProyectos") ? (Boolean.valueOf((String) parametros.get("u_emisionVariosProyectos"))) : null;
		Boolean viabilidadFavorable = parametros.containsKey("viabilidadFavorable") ? (Boolean.valueOf((String) parametros.get("viabilidadFavorable"))) : null;
		Boolean requierePagoRSQ = parametros.containsKey("requierePagoRSQ") ? (Boolean.valueOf((String) parametros.get("requierePagoRSQ"))) : null;
		Boolean artRcoa = parametros.containsKey("artRcoa") ? (Boolean.valueOf((String) parametros.get("artRcoa"))) : null;
		Boolean scoutDrilling = parametros.containsKey("scoutDrilling") ? (Boolean.valueOf((String) parametros.get("scoutDrilling"))) : null;
		Boolean u_exentoPago = parametros.containsKey("u_exentoPago") ? (Boolean.valueOf((String) parametros.get("u_exentoPago"))) : null;
		Boolean u_vieneRcoa = parametros.containsKey("u_vieneRcoa") ? (Boolean.valueOf((String) parametros.get("u_vieneRcoa"))) : null;
		
		if(esPrimeraVez != null) {
			parametros.put("esPrimeraVez", esPrimeraVez);
		}
		if(u_intersecaSnap != null) {
			parametros.put("u_intersecaSnap", u_intersecaSnap);
		}
		if(u_intersecaForestal != null) {
			parametros.put("u_intersecaForestal", u_intersecaForestal);
		}
		if(u_existeInventarioForestal != null) {
			parametros.put("u_existeInventarioForestal", u_existeInventarioForestal);
		}
		if(u_emisionVariosProyectos != null) {
			parametros.put("u_emisionVariosProyectos", u_emisionVariosProyectos);
		}
		if(viabilidadFavorable != null) {
			parametros.put("viabilidadFavorable", viabilidadFavorable);
		}
		if(requierePagoRSQ != null) {
			parametros.put("requierePagoRSQ", requierePagoRSQ);
		}
		if(artRcoa != null) {
			parametros.put("artRcoa", artRcoa);
		}
		if(scoutDrilling != null) {
			parametros.put("scoutDrilling", scoutDrilling);
		}
		if(u_exentoPago != null) {
			parametros.put("u_exentoPago", u_exentoPago);
		}
		if(u_vieneRcoa != null) {
			parametros.put("u_vieneRcoa", u_vieneRcoa);
		}

		Integer u_idProyecto = parametros.containsKey("u_idProyecto") ? (Integer.parseInt((String) parametros.get("u_idProyecto"))) : null;

		Float u_factorCovertura = parametros.containsKey("u_factorCovertura") ? (Float.parseFloat((String) parametros.get("u_factorCovertura"))) : null;
		Float u_costoTramite = parametros.containsKey("u_costoTramite") ? (Float.parseFloat((String) parametros.get("u_costoTramite"))) : null;
		
		if(u_idProyecto != null) {
			parametros.put("u_idProyecto", u_idProyecto);
		}
		if(u_factorCovertura != null) {
			parametros.put("u_factorCovertura", u_factorCovertura);
		}
		if(u_costoTramite != null) {
			parametros.put("u_costoTramite", u_costoTramite);
		}
		
		parametros.put("u_urlGeneracionAutomatica", Constantes.getUrlGeneracionAutomatica()+ u_idProyecto);
		
		parametros.put("diagnosticoMigradoAutomatico", true);
		
		return parametros;
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
		//abortar proceso anterior
		
		String sqlUpdateAbortarProcess="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','update processinstancelog set status = 4 "
				 + " where processinstanceid = "+idProcesoActual+"') as result";
		 
		 Query queryUpdateAbortarProcess = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateAbortarProcess);
        if(queryUpdateAbortarProcess.getResultList().size()>0){
        	queryUpdateAbortarProcess.getSingleResult();
        }
        
        String sqlUpdateAbortarSumm="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','update bamtasksummary set status = ''Exited'' "
				 + "where processinstanceid =  "+idProcesoActual+"') as result";
		 
		 Query queryUpdateAbortarSumm = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateAbortarSumm);
        if(queryUpdateAbortarSumm.getResultList().size()>0) {
        	queryUpdateAbortarSumm.getSingleResult();
        }
        
        String sqlUpdate="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','update task set status = ''Exited'' "
				 + "where processinstanceid = "+idProcesoActual+"') as result";
		 
		 Query queryUpdate = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdate);
        if(queryUpdate.getResultList().size()>0) {
        	queryUpdate.getSingleResult();
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
			e.printStackTrace();
		}		
	}	
}
