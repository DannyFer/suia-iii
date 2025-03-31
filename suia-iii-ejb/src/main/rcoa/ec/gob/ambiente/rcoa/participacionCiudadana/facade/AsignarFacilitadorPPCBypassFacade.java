package ec.gob.ambiente.rcoa.participacionCiudadana.facade;

import java.text.DateFormat;
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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.FacilitadorPPC;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FacilitadorProyectoLog;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Holiday;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorFacade;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoLogFacade;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@Stateless 
public class AsignarFacilitadorPPCBypassFacade {
	
	public static final Integer ID_TIPO_AREA_PLANTA_CENTRAL = 1;
	public static final Integer ID_TIPO_AREA_OFICINA = 6;
	public static final Integer ID_TIPO_AREA_GAD = 3;
	public static final Integer ID_TIPO_AREA_GALAPAGOS = 2;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;	
	@EJB
	private FeriadosFacade feriadosFacade;
	@EJB
	private FacilitadorPPCFacade facilitadorPPCFacade;	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private FacilitadorProyectoLogFacade facilitadorProyectoLogFacade;	
	@EJB
	private AsignarTareaFacade asignarTareaFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private ProyectoFacilitadorPPCFacade proyectoFacilitadorPPCFacade;
	@EJB
	private FacilitadorFacade facilitadorFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private InformeTecnicoEsIAFacade informeTecnicoEsIAFacade;
	
	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
	
	@Getter
	@Setter
	private FacilitadorPPC facilitador = new FacilitadorPPC();
	
	@SuppressWarnings("unchecked")
	public void asignarNuevoFacilitador(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%confirmarFacilitadorPPCBypass%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') and t.actualowner_id is not null and "
					+ "t.formname is not null and t.processid = ''rcoa.ProcesoParticipacionCiudadanaBypass'' ') "
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
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

				Date fecha = sdf.parse(codigoProyecto[4]); 
				
				Integer dias = 3;
				
				Date fechaRechazo = fechaFinal(fecha, dias);
				Date fechaActual = new Date();
				
				ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(codigoProyecto[3]);
				
				SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy-MM-dd");
				
				Date fechaSuspensionBypass = sdfShort.parse(Constantes.getFechaSuspensionPpcBypass());
				Date fechaProyecto = sdfShort.parse(proyecto.getFechaGeneracionCua().toString());
				
				if(fechaProyecto.compareTo(fechaSuspensionBypass) < 0) {
					if(fechaRechazo.before(fechaActual) || fechaRechazo.equals(fechaActual)){
						try {
							Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
							
							Map<String, Object> params = new HashMap<String, Object>();
		
			                params.put("aceptaProyecto", false);
			                
			                facilitador= facilitadorPPCFacade.facilitadorPendienteAceptacion (proyecto, usuario);
			                
			                String[] facilitadoresLista = new String[1];
			                List<String> facilitadores = new ArrayList<String>();
			                if(facilitador.getTarifaEspecial() != null && facilitador.getTarifaEspecial()){
			                	facilitadores = buscarFacilitadorContinental(proyecto);
			                	if(facilitadores == null)
								{
			                		continue;
								}
			                }else{
			                	facilitadores = facilitadores(1,proyecto);			
								if(facilitadores.size()==0)
								{
									continue;
								}
			                }	                                
		
							if(facilitador != null && facilitador.getId() != null){
								facilitador.setFechaAceptaProyecto(new Date());
								facilitador.setAceptaProyecto(false);
								facilitadorPPCFacade.guardar(facilitador);
							}					
							
							Integer cont = 0;
							Usuario nuevoFacilitador = null;
							for (String usuarioF : facilitadores) {
				              facilitadoresLista[cont++] = usuarioF;
				              FacilitadorPPC facilitador= new FacilitadorPPC();
				              facilitador.setEstado(true);
				              facilitador.setEsAdicional(false);
				              facilitador.setProyectoLicenciaCoa(proyecto);
				              facilitador.setUsuario(usuarioFacade.buscarUsuario(usuarioF));
				              facilitador.setTarifaEspecial(this.facilitador.getTarifaEspecial());
				              facilitador.setFechaRegistroPago(this.facilitador.getFechaRegistroPago());
				              facilitadorPPCFacade.guardar(facilitador);
				              params.put("facilitadorNuevo",usuarioF);		              
		
				              nuevoFacilitador = facilitador.getUsuario();
		
							}
			                
			                
			                FacilitadorProyectoLog facilitadorProyectoLog = new FacilitadorProyectoLog();
			                facilitadorProyectoLog.setUsuario(usuario);
			                facilitadorProyectoLog.setFechaNegacion(new Date());
			                facilitadorProyectoLog.setObservacion("Tarea no atendida en " + dias + " dias");
			                facilitadorProyectoLog.setResetarTarea(1);
			                facilitadorProyectoLog.setProyecto(null);
			                facilitadorProyectoLog.setNegacionAutomatica(true);
			                facilitadorProyectoLog.setProyectoRcoa(proyecto.getId());
			                facilitadorProyectoLogFacade.guardar(facilitadorProyectoLog);	                
		                
			    			String usuarioResponsable = null;
			    			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]));
		    				usuarioResponsable = (String) variables.get("tecnicoResponsableEIA");
			               
			                Usuario tecnicoResponsablePPC = usuarioFacade.buscarUsuario(usuarioResponsable);
			                
			                procesoFacade.modificarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]), params);

			                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), null);
			                
			                System.out.println("Asignacion de facilitador proceso " + codigoProyecto[2]);
			                
			                InformacionProyectoEia esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
							InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, InformeTecnicoEsIA.social);
							if(informeTecnico != null && informeTecnico.getId() != null) {
								//si existe técnico social
								tecnicoResponsablePPC = usuarioFacade.buscarUsuario(informeTecnico.getUsuarioCreacion());
							}
			                
			                notificaciones(tecnicoResponsablePPC, usuario, proyecto, nuevoFacilitador);
		                } catch (Exception e) {
							e.printStackTrace();
						}
		                
					}
				}					                
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		
		/**
		 * Método para calcular días laborables, no toma en cuenta sábados y domingos
		 * @param fechaInicial
		 * @param diasRequisitos
		 * @return
		 */
		private Date fechaFinal(Date fechaInicial, int diasRequisitos){		
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date fechaFinal = new Date();		
				Calendar fechaPrueba = Calendar.getInstance();
				fechaPrueba.setTime(fechaInicial);	
			
				int i = 0;
				while(i < diasRequisitos){
					fechaPrueba.add(Calendar.DATE, 1);
					
					Integer anio = fechaPrueba.get(Calendar.YEAR);
					Integer mes = fechaPrueba.get(Calendar.MONTH) + 1;
					Integer dia = fechaPrueba.get(Calendar.DATE);
				
					String fechaFeriado = anio.toString() + "-" + mes.toString() + "-"+dia.toString();
					Date fechaComparar = format.parse(fechaFeriado);
				
					List<Holiday> listaFeriados = feriadosFacade.listarFeriadosNacionalesPorRangoFechas(fechaComparar, fechaComparar);
					
					if(fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){
						if(listaFeriados == null)
							i++;
					}
				}
			
				fechaFinal = fechaPrueba.getTime();
				return fechaFinal;	
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		private void notificaciones(Usuario tecnicoResponsablePPC, Usuario facilitador, ProyectoLicenciaCoa proyecto, Usuario nuevoFacilitador) {
			try {
				Usuario usuarioEnvio = new Usuario();
				usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
				//enviar notificacion de rechazo
				
				Object[] parametrosCorreoTecnicos = new Object[] {facilitador.getPersona().getTipoTratos().getNombre(), 
						facilitador.getPersona().getNombre(), proyecto.getNombreProyecto(), 
						proyecto.getCodigoUnicoAmbiental() };
				
				String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
								"bodyNotificacionRechazoFacilitadorSinJustificativoByPass",
								parametrosCorreoTecnicos);
				
				if(tecnicoResponsablePPC != null && tecnicoResponsablePPC.getId() != null) {
					List<Contacto> contactos = tecnicoResponsablePPC.getPersona().getContactos();
					String emailDestino = "";
					for (Contacto contacto : contactos) {
						if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
							emailDestino=contacto.getValor();
							break;
						}
					}
					
					NotificacionAutoridadesController email = new NotificacionAutoridadesController();
	    			email.sendEmailInformacionProponente(emailDestino, "", notificacion, "Regularización Ambiental Nacional", proyecto.getCodigoUnicoAmbiental(), tecnicoResponsablePPC, usuarioEnvio);
	    		}

	    		//envio notificación a nuevo facilitador asignado
				if(nuevoFacilitador != null) {
					List<Contacto> contactos = nuevoFacilitador.getPersona().getContactos();
					String emailDestino = "";
					for (Contacto contacto : contactos) {
						if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
							emailDestino=contacto.getValor();
							break;
						}
					}

					Object[] parametrosCorreoNuevo = new Object[] {nuevoFacilitador.getPersona().getTipoTratos().getNombre(), 
							nuevoFacilitador.getPersona().getNombre(), proyecto.getNombreProyecto(), 
							proyecto.getCodigoUnicoAmbiental() };
					
					String notificacionNuevo = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
									"bodyNotificacionDesignacionFacilitadorByPass",
									parametrosCorreoNuevo);
					
					NotificacionAutoridadesController email = new NotificacionAutoridadesController();
	    			email.sendEmailInformacionProponente(emailDestino, "", notificacionNuevo, "Regularización Ambiental Nacional", proyecto.getCodigoUnicoAmbiental(), nuevoFacilitador, usuarioEnvio);
				}
		        
			} catch (Exception e) {					
				e.printStackTrace();
			}
		}
		
		public List<String> facilitadores(Integer numeroFacilitadores, ProyectoLicenciaCoa proyecto) {
			List<Usuario> confirmado = new ArrayList<Usuario>();
	        List<Usuario> rechazado = new ArrayList<Usuario>();
	        List<String> lista = new ArrayList<String>();
			///selecciono los facilitadores
	        confirmado=facilitadorPPCFacade.facilitadoresConfirmado(proyecto);
	        rechazado=facilitadorPPCFacade.facilitadoresRezhazado(proyecto);
	        
	      //se excluyen tambien los q estan asignados y no confirman
	        List<Usuario> facilitadoresAsignados = facilitadorPPCFacade.usuariosFacilitadoresAsignadosPendientesAceptacion(proyecto);
	        confirmado.addAll(facilitadoresAsignados);
	        
	        List<Usuario> facilitadores = facilitadorFacade.seleccionarFacilitadoresBypass(numeroFacilitadores,confirmado,rechazado,proyecto.getAreaResponsable().getAreaName());
	        if (facilitadores.size() == numeroFacilitadores) {            
	            for (Usuario usuario : facilitadores) {
	            	lista.add(usuario.getNombre());
	            }
	        }	
	        
			return lista;
		}
		
		private List<String> buscarFacilitadorContinental(ProyectoLicenciaCoa proyecto){
			List<String> result = new ArrayList<String>();
			List<String> facilitadores = facilitadores(2, proyecto);			
			if(facilitadores.size()==0)
				return null;
			
			String codeGalapagos = Constantes.CODIGO_INEC_GALAPAGOS;
			
			for (String usuario : facilitadores) {
				Usuario ufacilitador = usuarioFacade.buscarUsuario(usuario);
				String codeProvinciaFacilitador = ufacilitador.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec();
				if(!codeProvinciaFacilitador.equals(codeGalapagos)) {
					result.add(usuario);
					break;
				}
			}
			
			if(result.size() == 0)
				buscarFacilitadorContinental(proyecto);
			
			return result;
		}

}
