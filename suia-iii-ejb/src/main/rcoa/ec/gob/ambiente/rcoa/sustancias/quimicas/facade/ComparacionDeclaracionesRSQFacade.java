package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DeclaracionSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.MovimientoDeclaracionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Holiday;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class ComparacionDeclaracionesRSQFacade {
	
	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
	
	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private DeclaracionSustanciaQuimicaFacade declaracionSustanciaQuimicaFacade;
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
	@EJB
	private MovimientoDeclaracionRSQFacade movimientoDeclaracionRSQFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private FeriadosFacade feriadoFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	private Map<String, Object> variables;
	
	
	public void validarDia(){
		try {
			
			SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
			
			Calendar fechaActual = Calendar.getInstance();
			int mesActual = fechaActual.get(Calendar.MONTH);
			int anioActual = fechaActual.get(Calendar.YEAR);
						
			String date = sf.format(fechaActual.getTime());					
			
			Calendar inicioMes = Calendar.getInstance();
			inicioMes.set(Calendar.DATE, 1);
			inicioMes.set(Calendar.MONTH, mesActual);
			inicioMes.set(Calendar.YEAR, anioActual);		
			
			CatalogoGeneralCoa valorDias = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.DSQ_DIAS_DECLARACION, 1);
    		
    		int dias = Integer.valueOf(valorDias.getValor()) + 1;
			
			Date fechaLimite = fechaFinal(inicioMes.getTime(), dias);
						
			String datel = sf.format(fechaLimite);
			
			if(date.equals(datel)){
				obtenerTareas();
			}					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private Date fechaFinal(Date fechaInicial, int diasRequisitos) throws ServiceException{
		Date fechaFinal = new Date();		
		Calendar fechaPrueba = Calendar.getInstance();
		fechaPrueba.setTime(fechaInicial);	
		
		int i = 0;
		while(i < diasRequisitos){
			fechaPrueba.add(Calendar.DATE, 1);
			
			Date fechaFeriado = fechaPrueba.getTime();
			
			List<Holiday> listaFeriados = feriadoFacade.listarFeriadosNacionalesPorRangoFechas(fechaFeriado, fechaFeriado);
			
			if(fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){
				if(listaFeriados == null || listaFeriados.isEmpty()){
					i++;
				}					
			}					
		}
		
		fechaFinal = fechaPrueba.getTime();			
		return fechaFinal;	
	}
	
	@SuppressWarnings("unchecked")
	public void  obtenerTareas(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%actualizarInformacionIngresada%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') and "//t.processinstanceid = 259777 and "
					+ "t.formname is not null and t.processid = ''rcoa.DeclaracionSustanciasQuimicas'' ') "
					+ "as (id varchar, processinstanceid varchar, tramite varchar, fecha varchar) "
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
			
			for (String[] variable : listaCodigosProyectos) {
								
				String codigoProyecto = variable[2];
				
				RegistroSustanciaQuimica registroRSQ = registroSustanciaQuimicaFacade.obtenerRegistroPorCodigo(codigoProyecto);

				if(registroRSQ == null || registroRSQ.getId() == null){
					DeclaracionSustanciaQuimica declaracionSeleccionada = new DeclaracionSustanciaQuimica();
					declaracionSeleccionada = declaracionSustanciaQuimicaFacade.buscarPorTramite(codigoProyecto);
					registroRSQ = declaracionSeleccionada.getRegistroSustanciaQuimica();
				}
				
				Usuario usuarioDeclarante;
				if(registroRSQ.getUsuario() == null){
					usuarioDeclarante = usuarioFacade.buscarUsuario(registroRSQ.getUsuarioCreacion());
				}else{
					usuarioDeclarante = usuarioFacade.buscarUsuarioPorId(registroRSQ.getUsuario().getId());
				}		
				
				variables=procesoFacade.recuperarVariablesProceso(usuarioDeclarante, Long.parseLong(variable[1]));
				String idDeclaracion=(String)variables.get("idDeclaracion");
				
				DeclaracionSustanciaQuimica declaracionSeleccionada = new DeclaracionSustanciaQuimica();
				if(idDeclaracion != null){
					declaracionSeleccionada = declaracionSustanciaQuimicaFacade.obtenerRegistroPorId(Integer.valueOf(idDeclaracion));
				}else{
					declaracionSeleccionada = declaracionSustanciaQuimicaFacade.obtenerUltimaDeclaracionPorRSQ(registroRSQ);
				}
												
				/**Los movimientos en esta consulta ya vienen sin los tipos de movimientos que no se deben tomar en cuenta para la comparación ej. Importación */
				List<MovimientoDeclaracionRSQ> listaMovimientos = movimientoDeclaracionRSQFacade.obtenerPorDeclaracion(declaracionSeleccionada);
				
				boolean movimientoObservado = false;
				
				List<String> listaObservados = new ArrayList<String>();
				List<String> listaFaltantes = new ArrayList<String>();
				int movimientos = 0;
				
				List<String> listaMovimientosDeclaradoCorrectamente = new ArrayList<>();
				for(MovimientoDeclaracionRSQ movimiento : listaMovimientos){
					movimientos = listaMovimientos.size();
									
					List<MovimientoDeclaracionRSQ> movimientosContrario = new ArrayList<MovimientoDeclaracionRSQ>();
					/**este movimiento contrario se lo va a buscar por medio de la id de rsq si es que lo tiene para que sea más facil
					//si no  tiene la id buscamos de la forma anterior			*/			
						
					try {
						if(movimiento.getOperador() != null){
							movimientosContrario = movimientoDeclaracionRSQFacade.obtenerListaPorUsuarioMesAnio(movimiento.getOperador().getNombre(),
											declaracionSeleccionada.getMesDeclaracion(),declaracionSeleccionada.getAnioDeclaracion(),
											movimiento.getNumeroFactura(),declaracionSeleccionada.getSustanciaQuimica().getId(), movimiento.getOperador().getId());
							
						}else{
							movimientosContrario = null;
						}
					} catch (Exception e) {
						movimientosContrario = null;
						e.printStackTrace();
					}					
					
					if(movimientosContrario != null && !movimientosContrario.isEmpty()){
						
						for(MovimientoDeclaracionRSQ movimientoContrario : movimientosContrario){
							if(movimiento.getValorEgreso() != null && !movimiento.getValorEgreso().equals(0.0)){
								if(movimientoContrario.getValorIngreso() != null && !movimientoContrario.getValorIngreso().equals(0.0)){
									if(!movimiento.getValorEgreso().equals(movimientoContrario.getValorIngreso())){
										/**movimiento observado*/
										listaObservados.add("Observado");
									}else{
										listaMovimientosDeclaradoCorrectamente.add("Correcto");
									}
									
								}else{
									listaObservados.add("Observado");
								}
							}else if(movimiento.getValorIngreso() != null && !movimiento.getValorIngreso().equals(0.0)){	
								if(movimientoContrario.getValorEgreso() != null && !movimientoContrario.getValorEgreso().equals(0.0)){
									if(!movimiento.getValorIngreso().equals(movimientoContrario.getValorEgreso())){
										/**movimiento observado*/
										listaObservados.add("Observado");
									}else{
										listaMovimientosDeclaradoCorrectamente.add("Correcto");
									}
								}else{
									listaObservados.add("Observado");
								}
							}else{
								listaObservados.add("Observado");
							}
						}	
					}else{
						listaObservados.add("Observado");
						listaFaltantes.add("faltante");
					}									
				}
				
				if(movimientos != listaMovimientosDeclaradoCorrectamente.size()){
					movimientoObservado = true;
				}
				
				if(!listaObservados.isEmpty()){
					movimientoObservado = true;
				}
				
				Usuario usuarioOperador = new Usuario();
				if(registroRSQ.getUsuario() != null){
					usuarioOperador = usuarioFacade.buscarUsuarioPorId(registroRSQ.getUsuario().getId());
				}else{
					usuarioOperador = usuarioFacade.buscarUsuario(registroRSQ.getUsuarioCreacion());
				}
				
				boolean modificarTarea = true;
				if(!listaFaltantes.isEmpty()){
					modificarTarea = false;					
				}
				
				
				if(modificarTarea){
					if(movimientoObservado){
						try {
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("usuario_operador",usuarioOperador.getNombre());
							params.put("declaracionCorrecta", false);
							params.put("idDeclaracion", declaracionSeleccionada.getId());
			                
			                procesoFacade.modificarVariablesProceso(usuarioOperador, Long.parseLong(variable[1]), params);
			                
			                /**modificar la tarea para que aparezca en el usuario*/
			                
			                String sqlTarea = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
			                		+ "'UPDATE task "
			                		+ "SET actualowner_id = ''" + usuarioOperador.getNombre() + "'', "
			                		+ " status = ''Reserved'', "
			                		+ " createdby_id = ''"+ usuarioOperador.getNombre() + "'' " 
			                		+ " WHERE id = " + Integer.valueOf(variable[0]) + " ')" ;
			                		
			                Query queryTarea = crudServiceBean.getEntityManager().createNativeQuery(sqlTarea);
			                
			                queryTarea.getResultList();
						} catch (Exception e) {
							e.printStackTrace();
						}				
						
					}else{
						 try {
							 Map<String, Object> params = new HashMap<String, Object>();
								params.put("declaracionCorrecta", true);
								params.put("idDeclaracion", declaracionSeleccionada.getId());
				                
				                procesoFacade.modificarVariablesProceso(usuarioOperador, Long.parseLong(variable[1]), params);
								
								
				                String sqlTarea = "select from dblink_exec('"+dblinkBpmsSuiaiii+"', "
				                		+ "'UPDATE task "
				                		+ "SET actualowner_id = ''" + usuarioOperador.getNombre() + "'', "
				                		+ " status = ''Reserved'', "
				                		+ " createdby_id = ''"+ usuarioOperador.getNombre() + "'' " 
				                		+ " WHERE id = " + Integer.valueOf(variable[0]) + " ')" ;
				                		
				                Query queryTarea = crudServiceBean.getEntityManager().createNativeQuery(sqlTarea);
				                
				                queryTarea.getResultList();
				                
				                
								Map<String, Object> data = new ConcurrentHashMap<String, Object>();
								aprobarTarea(usuarioOperador, Long.parseLong(variable[0]), Long.parseLong(variable[1]), data);
								
								String sqlTareaBam = "select from dblink_exec('"
										+ dblinkBpmsSuiaiii + "', " + "'UPDATE bamtasksummary "
										+ " SET status = ''Exited'' " + " WHERE taskid = "
										+ Integer.valueOf(variable[0]) + " ')";

								Query queryTareaBam = crudServiceBean.getEntityManager()
										.createNativeQuery(sqlTareaBam);

								queryTareaBam.getResultList(); 
						} catch (Exception e) {
							e.printStackTrace();
						}				            
					}
				}				
						
//				usuarioOperador = usuarioFacade.buscarUsuario("1719815878");
				if(listaFaltantes != null && !listaFaltantes.isEmpty()){
					notificacionesFaltantes(usuarioOperador, codigoProyecto, declaracionSeleccionada.getMesDeclaracion());
				}else{
					notificaciones(usuarioOperador, codigoProyecto, movimientoObservado, declaracionSeleccionada.getMesDeclaracion());	
				}						
			}					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void notificaciones(Usuario operador, String codigoProyecto, boolean observado, int mes) {
		try {		
		    		    
		    Locale locale = new Locale("es", "ES");
		    Calendar calendarInicio = Calendar.getInstance();
		    /**Restamos 1 a la variable por lo dicho al inicio*/
		    calendarInicio.set(Calendar.MONTH, mes-1);
		    String monthName=calendarInicio.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);		   			
			
			Object[] parametrosCorreo = new Object[] {operador.getPersona().getNombre(), codigoProyecto, monthName.toUpperCase()};
			
			String notificacion = "";
			if(observado){
				notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
						"bodyNotificacionObservacionDeclaracionRSQ",
						parametrosCorreo);	
			}else{
				notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
						"bodyNotificacionAprobacionDeclaracionRSQ",
						parametrosCorreo);	
			}
									
						
			String emailDestino = "";
			
			if(operador.getNombre().length() == 10){
				List<Contacto> contacto = contactoFacade.buscarPorPersona(operador.getPersona());
				for (Contacto con : contacto){
					if(con.getFormasContacto().getId() == 5	&& con.getEstado().equals(true)){
						emailDestino = con.getValor();
						break;
					}
				}
			}else{
				Organizacion organizacion = organizacionFacade.buscarPorRuc(operador.getNombre());
				if(organizacion != null){
					List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);
					for (Contacto con : contacto){
						if(con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)){
							emailDestino = con.getValor();
							break;
						}
					}
				}else{
					List<Contacto> contacto = contactoFacade.buscarPorPersona(operador.getPersona());
					for (Contacto con : contacto){
						if(con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)){
							emailDestino = con.getValor();
							break;
						}
					}
				}
			}

			Usuario usuarioEnvio = new Usuario();
			usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
			NotificacionAutoridadesController email = new NotificacionAutoridadesController();
			email.sendEmailInformacionProponente(emailDestino, "", notificacion, "Declaración de Sustancias Químicas", codigoProyecto, operador, usuarioEnvio);		        	
	        
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}
	
	private void notificacionesFaltantes(Usuario operador, String codigoProyecto, int mes) {
		try {		
		    		    
		    Locale locale = new Locale("es", "ES");
		    Calendar calendarInicio = Calendar.getInstance();
		    /**Restamos 1 a la variable por lo dicho al inicio*/
		    calendarInicio.set(Calendar.MONTH, mes-1);
		    String monthName=calendarInicio.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);		   			
			
			Object[] parametrosCorreo = new Object[] {operador.getPersona().getNombre(), codigoProyecto, monthName.toUpperCase()};
			
			String notificacion = "";
			notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
					"bodyNotificacionDeclaracionFaltanteRSQ",
					parametrosCorreo);
						
			String emailDestino = "";
			if(operador.getNombre().length() == 10){
				List<Contacto> contacto = contactoFacade.buscarPorPersona(operador.getPersona());
				for (Contacto con : contacto){
					if(con.getFormasContacto().getId() == 5	&& con.getEstado().equals(true)){
						emailDestino = con.getValor();
						break;
					}
				}
			}else{
				Organizacion organizacion = organizacionFacade.buscarPorRuc(operador.getNombre());
				if(organizacion != null){
					List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);
					for (Contacto con : contacto){
						if(con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)){
							emailDestino = con.getValor();
							break;
						}
					}
				}else{
					List<Contacto> contacto = contactoFacade.buscarPorPersona(operador.getPersona());
					for (Contacto con : contacto){
						if(con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)){
							emailDestino = con.getValor();
							break;
						}
					}
				}
			}

			Usuario usuarioEnvio = new Usuario();
			usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
			NotificacionAutoridadesController email = new NotificacionAutoridadesController();
			email.sendEmailInformacionProponente(emailDestino, "", notificacion, "Declaración de Sustancias Químicas", codigoProyecto, operador, usuarioEnvio);		        	
	        
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}
	
	public synchronized void aprobarTarea(Usuario usuario, Long taskId, Long processId, Map<String, Object> data)
			throws JbpmException {
		if(data != null) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				if(entry.getValue() instanceof String) {
					data.put(entry.getKey(), new String(((String) entry.getValue()).getBytes(Charset.forName("UTF-8"))));
				}
			}
		}
		taskBeanFacade.approveTask(usuario.getNombre(), taskId, processId, data, usuario.getPassword(),
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(),
				Constantes.getNotificationService());
	}
	
	/**
	 * ------------------------------------------------------------------------------------------------------------------------------------
	 * ------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public void validarDiaPago(){
		try {
			SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
			
			Calendar fechaActual = Calendar.getInstance();
			int mesActual = fechaActual.get(Calendar.MONTH);
			int anioActual = fechaActual.get(Calendar.YEAR);
						
			String date = sf.format(fechaActual.getTime());					
			
			Calendar inicioMes = Calendar.getInstance();
			inicioMes.set(Calendar.DATE, 1);
			inicioMes.set(Calendar.MONTH, mesActual);
			inicioMes.set(Calendar.YEAR, anioActual);			
			
			CatalogoGeneralCoa valorDias = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.DSQ_DIAS_DECLARACION, 1);
    		
    		int dias = Integer.valueOf(valorDias.getValor()) + 1;
			
			Date fechaLimite = fechaFinal(inicioMes.getTime(), dias);
						
			String datel = sf.format(fechaLimite);
			
			if(date.equals(datel)){
				obtenerTareasPago();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerTareasPago(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%declaracionMensualSustancias%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') and " //t.processinstanceid = 260608 and "
					+ "t.formname is not null and t.processid = ''rcoa.DeclaracionSustanciasQuimicas'' ') "
					+ "as (id varchar, processinstanceid varchar, tramite varchar, fecha varchar) "
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
			
			for (String[] variable : listaCodigosProyectos) {
								
				String codigoProyecto = variable[2];
				
				RegistroSustanciaQuimica registroRSQ = registroSustanciaQuimicaFacade.obtenerRegistroPorCodigo(codigoProyecto);
				
				if(registroRSQ == null || registroRSQ.getId() == null){
					DeclaracionSustanciaQuimica declaracionSeleccionada = new DeclaracionSustanciaQuimica();
					declaracionSeleccionada = declaracionSustanciaQuimicaFacade.buscarPorTramite(codigoProyecto);
					registroRSQ = declaracionSeleccionada.getRegistroSustanciaQuimica();
					
					Usuario usuarioDeclarante;
					if(registroRSQ.getUsuario() == null){
						usuarioDeclarante = usuarioFacade.buscarUsuario(registroRSQ.getUsuarioCreacion());
					}else{
						usuarioDeclarante = usuarioFacade.buscarUsuarioPorId(registroRSQ.getUsuario().getId());
					}	
					
					variables=procesoFacade.recuperarVariablesProceso(usuarioDeclarante, Long.parseLong(variable[1]));
					String requiere_Pago=(String)variables.get("requiere_pago");
					
					Boolean requierePago = Boolean.valueOf(requiere_Pago);
					
					if(!requierePago){
												
						declaracionSeleccionada.setPagoPendiente(true);
						CatalogoGeneralCoa valorTasa = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.DSQ_VALOR_TASA, 1);
			    		
			    		double valorMulta = Double.valueOf(valorTasa.getValor());
						declaracionSeleccionada.setValorMulta(valorMulta);
						
						declaracionSustanciaQuimicaFacade.guardar(declaracionSeleccionada, usuarioDeclarante);
						
						Map<String, Object> params = new HashMap<String, Object>();

		                params.put("declaracionEnviada", false);
		                
		                procesoFacade.modificarVariablesProceso(usuarioDeclarante, Long.parseLong(variable[1]), params);

		                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		                aprobarTarea(usuarioDeclarante, Long.parseLong(variable[0]), Long.parseLong(variable[1]), data);
					}
				}				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
