package ec.gob.ambiente.prevencion.participacionsocial.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.prevencion.participacionsocial.bean.RegistrarPagoFacilitadoresBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.PagoServiciosBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FacilitadorProyecto;
import ec.gob.ambiente.suia.domain.FacilitadorProyectoLog;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorFacade;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoFacade;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoLogFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class RegistrarPagoFacilitadoresController implements Serializable {

    private static final Logger LOG = Logger
            .getLogger(RegistrarPagoFacilitadoresController.class);
    /**
     *
     */
    private static final long serialVersionUID = 7552884096550363870L;

    @EJB
    ProcesoFacade procesoFacade;

    @EJB
    FacilitadorProyectoFacade facilitadorProyectoFacade;

    @EJB
    FacilitadorProyectoLogFacade facilitadorProyectoLogFacade;

    @EJB
    FacilitadorFacade facilitadorFacade;
    
    @EJB
    private ContactoFacade contactoFacade;
    
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
    
    @EJB
    private UsuarioFacade usuarioFacade;
    
    @EJB
    private DocumentosFacade documentosFacade;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{registrarPagoFacilitadoresBean}")
    private RegistrarPagoFacilitadoresBean registrarPagoFacilitadoresBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{pagoServiciosBean}")
    private PagoServiciosBean pagoServiciosBean;
    

    public void uploadListener(FileUploadEvent event) {
        registrarPagoFacilitadoresBean.setAdjuntoPago(JsfUtil.upload(event));
        registrarPagoFacilitadoresBean
                .setNombreAdjunto(registrarPagoFacilitadoresBean
                        .getAdjuntoPago().getName());
    }

    /**
     * Guarda o actualiza un pago de facilitadores
     */
    public String guardarPago() {
        if (registrarPagoFacilitadoresBean.getCompletado()) {
            try {
            	 Map<String, Float> paramsValorAPagar = new ConcurrentHashMap<String, Float>();
            	 paramsValorAPagar.put("valorAPagar", (float) registrarPagoFacilitadoresBean.getTotalAPagarXFacilitadores());
                JsfUtil.getBean(PagoServiciosBean.class).initFunctionWithProjectOrMotive(
                        "/bandeja/bandejaTareas.jsf", new CompleteOperation() {

                            @Override
                            public Object endOperation(Object object) {
                                try {
                                    List<Usuario> confirmado = new ArrayList<Usuario>();
                                    List<Usuario> rechazado = new ArrayList<Usuario>();
                                    String facilitadoresConfirmados = "";
                                    String facilitadoresRechazados = "";
                                    String separador = "";
                                    Integer numeroFacilitadores = registrarPagoFacilitadoresBean.getNroFacilitadores();
                                    if (registrarPagoFacilitadoresBean.isFacilitadoresAdicionales()) {
                                        numeroFacilitadores += registrarPagoFacilitadoresBean.getNroFacilitadoresPrevios();
                                        List<FacilitadorProyecto> facilitadorProyectoList =
                                                facilitadorProyectoFacade.listarFacilitadoresProyecto(
                                                        registrarPagoFacilitadoresBean.getProyectosBean().getProyecto());


                                        for (FacilitadorProyecto facilitadorProyecto : facilitadorProyectoList) {
                                            confirmado.add(facilitadorProyecto.getUsuario());
                                            facilitadoresConfirmados += "," + facilitadorProyecto.getUsuario().getNombre();
                                        }

                                        List<FacilitadorProyectoLog> facilitadorProyectoLogs = facilitadorProyectoLogFacade.listarFacilitadoresRechazadosProyecto(
                                                registrarPagoFacilitadoresBean.getProyectosBean().getProyecto());

                                        for (FacilitadorProyectoLog facilitadorProyectoLog : facilitadorProyectoLogs) {
                                            rechazado.add(facilitadorProyectoLog.getUsuario());
                                            facilitadoresRechazados += separador + facilitadorProyectoLog.getUsuario().getNombre();
                                            separador = ",";
                                        }
                                    }

                                    ///selecciono los facilitadores
                                    List<Usuario> facilitadores = facilitadorFacade
                                            .seleccionarFacilitadores(registrarPagoFacilitadoresBean.getNroFacilitadores(),
                                                    confirmado,
                                                    rechazado,registrarPagoFacilitadoresBean.getProyectosBean().getProyecto().getAreaResponsable().getAreaName());
                                    if (facilitadores.size() == registrarPagoFacilitadoresBean.getNroFacilitadores()) {
                                        String[] facilitadoresLista = new String[facilitadores.size()];
                                        String facilitadoresS = "";
                                        separador = "";
                                        Integer cont = 0;
                                        for (Usuario usuario : facilitadores) {
                                            facilitadoresLista[cont++] = usuario.getNombre();
                                            facilitadoresS += separador + usuario.getNombre();
                                            separador = ",";
                                        }                           
                                        		
                                        //Cris F: Guardado de documento adjunto de la factura del pago de los facilitadores
                                        documentosFacade.guardarDocumentoAlfresco(registrarPagoFacilitadoresBean.getProyectosBean().getProyecto().getCodigo(),
                                        		Constantes.NOMBRE_PROCESO_PARTICIPACION_SOCIAL, bandejaTareasBean.getProcessId(), 
                                        		pagoServiciosBean.getDocumento(), TipoDocumentoSistema.FACTURA_PAGO_FACILITADORES,
                                        		null);                                                                                
                                        
                                        //CF: Fin
                                        
                                        facilitadoresS += facilitadoresConfirmados;
                                        Map<String, Object> params = new HashMap<String, Object>();

                                        params.put("facilitadorSRechazo", facilitadoresRechazados);
                                        params.put("listaFacilitadoresS", facilitadoresS);
                                        params.put("listaFacilitadores", facilitadoresLista);
                                        params.put("numeroFacilitadores", numeroFacilitadores);
                                        procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
                                        //Se aprueba la tarea
                                        Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                                        
                                        procesoFacade.aprobarTarea(loginBean.getUsuario(),
                                                bandejaTareasBean.getTarea().getTaskId(),
                                                bandejaTareasBean.getProcessId(), data
                                        );
                                      /*  params.put("bodyNotificacionIniciarMecanismosPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                                                "bodyNotificacionIniciarMecanismosPS", new Object[]{proyectosBean.getProyecto().getCodigo()}));
                                        params.put("asuntoNotificacionIniciarMecanismosPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                                                "asuntoNotificacionIniciarMecanismosPS", new Object[]{}));//usuario.getPersona().getNombre()
                                        */
                                        
                                      //CF: enviar mail a facilitador
                                        enviarMailFacilitadores(facilitadores); 
                                        //Fin historial

                                        JsfUtil.addMessageInfo("Se realizó correctamente la operación.");

                                    } else {
                                        JsfUtil.addMessageError("Error al completar la tarea. No existen la cantidad de facilitadores solicitados disponibles.");
                                        return "";
                                    }
                                } catch (Exception e) {
                                    LOG.error(e);
                                    JsfUtil.addMessageError("Error al completar la tarea.");
                                }

                                return "";
                            }
                        }, paramsValorAPagar, null, null, null, false);
            } catch (Exception e) {
                LOG.info("Error al guardar pago facilitadores", e);
                JsfUtil.addMessageError("El pago No ha sido guardado.");
            }
        } else {
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";

    }
    
    //CF: enviar mail a facilitador
    private void enviarMailFacilitadores(List<Usuario> facilitadores) throws ServiceException{    	
    	
    	for(Usuario facilitador :facilitadores){
    		Usuario usuario = usuarioFacade.buscarUsuarioPorId(facilitador.getId());
    		
    		List<Contacto> listaContactos = contactoFacade.buscarPorPersona(usuario.getPersona());
			String emailFacilitador = "";
			for(Contacto contacto : listaContactos){
				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
					emailFacilitador = contacto.getValor();
					break;
				}				
			}
    		
    		String nombreFacilitador = usuario.getPersona().getNombre();
    		String nombreProceso = registrarPagoFacilitadoresBean.getProyectosBean().getProyecto().getNombre();
    		
    		Usuario representanteLegal = proyectoLicenciamientoAmbientalFacade.getRepresentanteProyecto(registrarPagoFacilitadoresBean.getProyectosBean().getProyecto().getId());
    		UbicacionesGeografica ubicacion = proyectoLicenciamientoAmbientalFacade.getUbicacionProyectoPorIdProyecto(registrarPagoFacilitadoresBean.getProyectosBean().getProyecto().getId());
    		
    		String ubicacionProyecto = registrarPagoFacilitadoresBean.getProyectosBean().getProyecto().getDireccionProyecto()+ 
    				" - Cantón: " + ubicacion.getUbicacionesGeografica().getNombre() + 
    				" - Provincia: " + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
    		
    		List<Contacto> listaContactosProponente = contactoFacade.buscarPorPersona(representanteLegal.getPersona());
			String emailProponente = "";
			String telefonoProponente = "";
			for(Contacto contacto : listaContactosProponente){
				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
					emailProponente = contacto.getValor();
				}
				
				if(contacto.getFormasContacto().getId() == FormasContacto.TELEFONO){
					telefonoProponente = contacto.getValor();
				}
				
				if(contacto.getFormasContacto().getId() == FormasContacto.CELULAR){
					telefonoProponente += " - " + contacto.getValor();
			
				}			
			}
						
			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailFacilitadores("Asignación Facilitador", nombreFacilitador, nombreProceso, 
					ubicacionProyecto, emailFacilitador,representanteLegal.getPersona().getNombre(), emailProponente, telefonoProponente, registrarPagoFacilitadoresBean.getProyectosBean().getProyecto().getCodigo(), usuario, loginBean.getUsuario());
    	
    	}
    }
    
    
    
}
