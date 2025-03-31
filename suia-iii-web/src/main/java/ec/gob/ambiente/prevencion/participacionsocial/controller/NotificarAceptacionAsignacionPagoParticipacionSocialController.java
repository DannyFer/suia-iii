package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.participacionsocial.bean.NotificarAceptacionAsignacionPagoParticipacionSocialBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.autoridadambiental.service.AutoridadAmbientalFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FacilitadorProyecto;
import ec.gob.ambiente.suia.domain.FacilitadorProyectoLog;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorFacade;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoFacade;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoLogFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@RequestScoped
@ManagedBean
public class NotificarAceptacionAsignacionPagoParticipacionSocialController
        implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -518426463625882048L;
    private static final Logger LOGGER = Logger
            .getLogger(NotificarAceptacionAsignacionPagoParticipacionSocialController.class);
    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private FacilitadorProyectoFacade facilitadorProyectoFacade;

    @EJB
    private FacilitadorFacade facilitadorFacade;

    @EJB
    private MensajeNotificacionFacade mensajeNotificacionFacade;

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;        
   
    @EJB
    private FacilitadorProyectoLogFacade facilitadorProyectoLogFacade;
    
    //aumento de EJB para  obtener el usuario mediante el rol
    @EJB
    private AutoridadAmbientalFacade autoridadAmbientalFacade;
    @EJB
    private ContactoFacade contactoFacade;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{notificarAceptacionAsignacionPagoParticipacionSocialBean}")
    private NotificarAceptacionAsignacionPagoParticipacionSocialBean notificarAceptacionAsignacionPagoParticipacionSocialBean;

    public String iniciarTarea() {
        if (!notificarAceptacionAsignacionPagoParticipacionSocialBean
                .getCorrecto() || notificarAceptacionAsignacionPagoParticipacionSocialBean.isDescargado()) {
            try {


                Map<String, Object> params = new HashMap<String, Object>();

                params.put("aceptarInformacion", notificarAceptacionAsignacionPagoParticipacionSocialBean.getCorrecto());

                if (notificarAceptacionAsignacionPagoParticipacionSocialBean.getCorrecto()) {
                    FacilitadorProyecto facilitadorProyecto = new FacilitadorProyecto();
                    facilitadorProyecto.setUsuario(loginBean.getUsuario());
                    facilitadorProyecto.setProyecto(notificarAceptacionAsignacionPagoParticipacionSocialBean.getProyectosBean().getProyecto());
                    facilitadorProyecto.setAceptacion(true);
                    String facilitador = Objects.toString(notificarAceptacionAsignacionPagoParticipacionSocialBean.getVariables().get("u_Facilitador"), "");
                    facilitadorProyecto.setFacilitadorEncargado(facilitador == null || facilitador.isEmpty());
                    facilitadorProyecto.setFechaAceptacion(new Date());
                    facilitadorProyecto.setResetarTareaok(1);
                    ParticipacionSocialAmbiental participacionSocialAmbiental = participacionSocialFacade.buscarCrearParticipacionSocialAmbiental(
                            notificarAceptacionAsignacionPagoParticipacionSocialBean.getProyectosBean().getProyecto());
                    facilitadorProyecto.setParticipacionSocialAmbiental(participacionSocialAmbiental);

                    facilitadorProyectoFacade.guardarNoExiste(facilitadorProyecto);
                    String listaFacilitadores = participacionSocialFacade.listadoFacilitadores(
                            notificarAceptacionAsignacionPagoParticipacionSocialBean.getProyectosBean().getProyecto());
                    params.put("bodyNotificacionFacilitadoresAsignadosPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                            "bodyNotificacionFacilitadoresAsignadosPS", new Object[]{
                                    notificarAceptacionAsignacionPagoParticipacionSocialBean.getProyectosBean().getProyecto().getCodigo(),
                                    listaFacilitadores}));
                }
                
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);

                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), data);
                try {
                    if (notificarAceptacionAsignacionPagoParticipacionSocialBean.getCorrecto()) {
                    	
                    	//Cris F: Tercer cambio facilitadores 2018/12/28 
                    	//Aumento de email para FACILITADOR COORDINADOR de facilitadores del proyecto
                    	List<FacilitadorProyecto> facilitadorProyectoList = facilitadorProyectoFacade.listarFacilitadoresProyecto(notificarAceptacionAsignacionPagoParticipacionSocialBean.getProyectosBean().getProyecto());
                    	
                    	if(facilitadorProyectoList != null && !facilitadorProyectoList.isEmpty()){
                    		
                    		if(Integer.valueOf(notificarAceptacionAsignacionPagoParticipacionSocialBean.getNumeroFacilitadores()).equals(facilitadorProyectoList.size())){
                    			
                    			Usuario usuario = usuarioFacade.buscarUsuarioPorId(facilitadorProyectoList.get(0).getUsuario().getId());
        			    		
        			    		List<Contacto> listaContactos = contactoFacade.buscarPorPersona(usuario.getPersona());
        						String emailFacilitador = "";
        						for(Contacto contacto : listaContactos){
        							if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
        								emailFacilitador = contacto.getValor();
        								break;
        							}				
        						}
        						
        						String nombreFacilitador = usuario.getPersona().getNombre();
        						String nombresFacilitadores = "";
        						String datosFacilitadores = "";
        						
        						nombresFacilitadores += facilitadorProyectoList.get(0).getUsuario().getPersona().getNombre() + ", ";
        						facilitadorProyectoList.remove(0);
        						
        						if(facilitadorProyectoList != null && !facilitadorProyectoList.isEmpty())
        							datosFacilitadores = "Los datos de contacto son:<br/><br/>";
        						        						
        						for(FacilitadorProyecto facilitador : facilitadorProyectoList){
        							
        							Usuario usuarioFacilitador = usuarioFacade.buscarUsuarioPorId(facilitador.getUsuario().getId());
            			    		
            			    		List<Contacto> listaContactosFacilitador = contactoFacade.buscarPorPersona(usuarioFacilitador.getPersona());
            						String emailFacilitadorSecundario = "";
            						String telefonoFacilitadorSecundario = "";
            						for(Contacto contacto : listaContactosFacilitador){
            							if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
            								emailFacilitadorSecundario = contacto.getValor();            								
            							}		
            							if(contacto.getFormasContacto().getId() == FormasContacto.TELEFONO){
            								telefonoFacilitadorSecundario = contacto.getValor();
            							}	
            						}
            						
            						String nombreFacilitadorSecundario = usuarioFacilitador.getPersona().getNombre();
        							nombresFacilitadores += nombreFacilitadorSecundario + ", ";
            						datosFacilitadores += "Facilitador: " + nombreFacilitadorSecundario + "<br/>Teléfono: " + telefonoFacilitadorSecundario + "<br/>e-mail: " + emailFacilitadorSecundario + "<br/><br/>";
        						}
        						
        						String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionCoordinadorFacilitador", new Object[]{});
        						mensaje = mensaje.replace("nombre_facilitador", nombreFacilitador);
        						mensaje = mensaje.replace("datos_facilitadores", datosFacilitadores);
        						mensaje = mensaje.replace("nombres_facilitadores", nombresFacilitadores);
        						mensaje = mensaje.replace("codigo_proyecto", notificarAceptacionAsignacionPagoParticipacionSocialBean.getProyectosBean().getProyecto().getCodigo());
        		                
        		                NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
        		    			mail_a.sendEmailInformacionProponente(emailFacilitador, nombreFacilitador, mensaje, "Notificación de Coordinador de Facilitadores", notificarAceptacionAsignacionPagoParticipacionSocialBean.getProyectosBean().getProyecto().getCodigo(), usuario, loginBean.getUsuario());
                    		}
                    	}                    	
                    } else {
                    	
                    	//CFlores: aumento de adjunto de documento y envio de emails
                    	//guardar documento ingresado
                    	// insertar adjunto  
                    	if(notificarAceptacionAsignacionPagoParticipacionSocialBean.getDocumentos().isEmpty()){
                    		JsfUtil.addMessageError("Debe adjuntar un documento de Justificación para el rechazo.");
                    		return "";
                    	}else{
                        participacionSocialFacade.ingresarInformes(
                        		notificarAceptacionAsignacionPagoParticipacionSocialBean.getDocumentos(), bandejaTareasBean
                                        .getProcessId(), bandejaTareasBean.getTarea()
                                        .getTaskId(), notificarAceptacionAsignacionPagoParticipacionSocialBean.getProyectosBean().getProyecto().getCodigo());
                    	
                    	}
                    	
                    	Usuario usuarioDirector = autoridadAmbientalFacade.getUsuarioDirectorPrevencionContaminacionAmbiental();
                    	String nombreDirector = usuarioDirector.getPersona().getNombre();
                    	
                    	List<Contacto> listaContactosDirector = contactoFacade.buscarPorPersona(usuarioDirector.getPersona());
            			String emailDirector = "";
            			for(Contacto contacto : listaContactosDirector){
            				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
            					emailDirector = contacto.getValor();
            					break;
            				}            						
            			}                    
            			Usuario facilitador = JsfUtil.getLoggedUser();
            			String nombreFacilitador = facilitador.getPersona().getNombre();            			  
                    	
                    	NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
                    	
            			mail_a.sendEmailRechazoFacilitadores("Rechazo por Parte de Facilitadores",nombreFacilitador, 
            					emailDirector, 
            					notificarAceptacionAsignacionPagoParticipacionSocialBean.getProyectosBean().getProyecto().getCodigo(), 
            					nombreDirector, usuarioDirector, loginBean.getUsuario());
            			
            			
                    	Persona tecnico = autoridadAmbientalFacade.getTecnicoNotificacionesPPS();
                    	
                    	List<Contacto> listaContactoTecnico = contactoFacade.buscarPorPersona(tecnico);
                    	String emailTecnico = "";
                    	for(Contacto contacto : listaContactoTecnico){
                    		if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
                    			emailTecnico = contacto.getValor();
                    			break;
                    		}
                    	}
                    	//busco el usuario del tecnico
                    	Usuario usuarioTecnico = usuarioFacade.buscarUsuario(tecnico.getPin());
                    	
                    	mail_a.sendEmailRechazoFacilitadores("Rechazo por Parte de Facilitadores",nombreFacilitador, 
            					emailTecnico, 
            					notificarAceptacionAsignacionPagoParticipacionSocialBean.getProyectosBean().getProyecto().getCodigo(), 
            					tecnico.getNombre(), usuarioTecnico, loginBean.getUsuario());
                    	
                    	//Fin de documento y mails
                    	
                    	
                        if (notificarAceptacionAsignacionPagoParticipacionSocialBean
                                .getCantidadRechazo() >= 3) {
                            Usuario usuario = loginBean.getUsuario();
                            usuario.setEsFacilitador(false);
                            usuarioFacade.guardar(usuario);
                        }
                        FacilitadorProyectoLog facilitadorProyectoLog = new FacilitadorProyectoLog();
                        facilitadorProyectoLog.setUsuario(loginBean.getUsuario());
                        facilitadorProyectoLog.setFechaNegacion(new Date());
                        facilitadorProyectoLog.setObservacion(notificarAceptacionAsignacionPagoParticipacionSocialBean.getObservacionCorreccion());
                        facilitadorProyectoLog.setResetarTarea(1);
                        facilitadorProyectoLog.setProyecto(notificarAceptacionAsignacionPagoParticipacionSocialBean.getProyectosBean().getProyecto());
                        facilitadorProyectoLogFacade.guardar(facilitadorProyectoLog);
                    	
                    }
                }catch (ServiceException e) {
                    LOGGER.error(e);
                    JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
                    return "";
                }
                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            }catch (JbpmException e) {
                LOGGER.error(e);
                JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
                return "";
            } 
            catch (ServiceException e) {
                LOGGER.error(e);
                JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
                return "";
            } 
            catch (Exception e) {
                LOGGER.error(e);
                JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
                return "";
            }
        } else {
            JsfUtil.addMessageError("Debe descargar los documentos del proceso de Participación Social.");
        }
        return "";
    }

}
