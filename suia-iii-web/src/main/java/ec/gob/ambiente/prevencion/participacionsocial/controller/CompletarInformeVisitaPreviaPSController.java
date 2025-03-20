package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.participacionsocial.bean.CompletarInformeVisitaPreviaPSBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FacilitadorProyecto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;

import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
@ManagedBean
public class CompletarInformeVisitaPreviaPSController implements Serializable {

    private static final Logger LOGGER = Logger
            .getLogger(CompletarInformeVisitaPreviaPSController.class);
    private static final long serialVersionUID = 8464688603365819500L;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;


    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

    @EJB
    private AreaFacade areaFacade;

    //CrisF: aumento de EJB
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
    private ContactoFacade contactoFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciamientoAmbientalFacade;
    @EJB
    private FacilitadorProyectoFacade facilitadorProyectoFacade;
    @EJB
    private MensajeNotificacionFacade mensajeNotificacionFacade;

    @Getter
    @Setter
    private Documento documento;
    
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    //Fin 
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{completarInformeVisitaPreviaPSBean}")
    private CompletarInformeVisitaPreviaPSBean completarInformeVisitaPreviaPSBean;


    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;


    public String enviarDatos() {
        if (completarInformeVisitaPreviaPSBean.completado()) {
            try {

                try {
                    if (!completarInformeVisitaPreviaPSBean.isRevisar()) {
                        participacionSocialFacade.guardar(completarInformeVisitaPreviaPSBean.getParticipacionSocialAmbiental());
                        // insertar adjunto
                        participacionSocialFacade.ingresarInformes(
                                completarInformeVisitaPreviaPSBean.getDocumentos(), bandejaTareasBean
                                        .getProcessId(), bandejaTareasBean.getTarea()
                                        .getTaskId(), completarInformeVisitaPreviaPSBean.getProyectosBean().getProyecto().getCodigo());
                        
                        //Cris F: cambio para que tome el tecnico correspondiente de minería
                        Map<String, Object> variables = new HashMap<>();
                        Area areaProyecto = completarInformeVisitaPreviaPSBean.getProyectosBean().getProyecto().getAreaResponsable();
                        areaProyecto = areaFacade.getAreaFull(areaProyecto.getId());
                        
                        if (areaProyecto == null || areaProyecto.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
                        	
                        	Usuario coordinador = new Usuario();
                        	Usuario tecnico = new Usuario();
                        	if(completarInformeVisitaPreviaPSBean.getProyectosBean().getProyecto().getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().getId() ==2){
                        		coordinador = areaFacade.getUsuarioPorRolArea("role.pc.coordinador.Social.mineria", areaProyecto);    
                        		tecnico = areaFacade.getUsuarioPorRolArea("role.pc.tecnico.Social.mineria", areaProyecto);
                        	}else{        	
                        		coordinador = areaFacade.getUsuarioPorRolArea("role.pc.coordinador.Social", areaProyecto);   
                        		tecnico = areaFacade.getUsuarioPorRolArea("role.pc.tecnico.Social", areaProyecto);
                        	}
                        	variables.put("u_Coordinador", coordinador.getNombre());
                        	variables.put("u_Tecnico", tecnico.getNombre());
                        	
                        }else{
                        	Usuario coordinadorArea = new Usuario();
                        	Usuario tecnicoArea = new Usuario();
                        	
                        	coordinadorArea = areaFacade.getCoordinadorPorArea(areaProyecto);
                        	tecnicoArea = areaFacade.getUsuarioPorRolArea("role.pc.tecnico.Social", areaProyecto);
                        	variables.put("u_Tecnico", tecnicoArea.getNombre());
                            variables.put("u_Coordinador", coordinadorArea.getNombre());                                
                        }     
                        
                        procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId(), variables);
                        //Fin
                        
                        
                    } else {
                        if (observacionesController.validarObservaciones(loginBean.getUsuario(), !completarInformeVisitaPreviaPSBean.getObservado(), "visitaPrevia")) {

                        	//CrisF: validacion de factura de facilitadores
                    		if(!completarInformeVisitaPreviaPSBean.getObservado()){
                    			
    							if (completarInformeVisitaPreviaPSBean.getDocumentosInforme() == null || completarInformeVisitaPreviaPSBean.getDocumentosInforme().isEmpty()) {    								
    								JsfUtil.addMessageError("Debe adjuntar el informe técnico.");
    								return null;
    							}
                        		
                            	participacionSocialFacade.ingresarInformes(
                                        completarInformeVisitaPreviaPSBean.getDocumentosInforme(), bandejaTareasBean
                                                .getProcessId(), bandejaTareasBean.getTarea()
                                                .getTaskId(), completarInformeVisitaPreviaPSBean.getProyectosBean().getProyecto().getCodigo());
                            	
                            	//para correo de aprobación que se notifica al proponente y facilitador
                            	enviarMail();
                            	
                    		}else{
                    			//CF: para correo de rechazo que se notifica al proponente y facilitador
                    			enviarMail();
                    		}
                        	
                        	//Fin nuevo codigo
                        	
                            Map<String, Object> data = new HashMap<>();
                            data.put("aceptarInformacion", !completarInformeVisitaPreviaPSBean.getObservado());
                            data.put("visitaPrevia", true);
                            
                            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId(), data);
                            if (!completarInformeVisitaPreviaPSBean.getObservado()) {
                                completarInformeVisitaPreviaPSBean.getParticipacionSocialAmbiental().setPublicacion(true);
                                participacionSocialFacade.guardar(completarInformeVisitaPreviaPSBean.getParticipacionSocialAmbiental());
                            }

                        } else {
                            return "";
                        }
                    }
                    
                    Map<String, Object> data = new HashMap<>();
                    procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);


                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                    return JsfUtil
                            .actionNavigateTo("/bandeja/bandejaTareas.jsf");
                } catch (JbpmException e) {
                    LOGGER.error(e);
                    JsfUtil.addMessageError("Error al realizar la operación.");
                }

            } catch (Exception e) {
                LOGGER.error("Error al subir el pronunciamiento", e);
                JsfUtil.addMessageError("Error al subir el pronunciamiento al servidor.");
            }
        } else {
            JsfUtil.addMessageError("Faltan documentos por adjuntar.");
        }

        return "";
    }
    
    /**
     * CF: envio de mail para los facilitadores
     * @throws CmisAlfrescoException 
     */
    private void enviarMail(){    	
    	
    	try {
    		           	
           	List<FacilitadorProyecto> facilitadores = facilitadorProyectoFacade.listarFacilitadoresProyecto(proyectosBean.getProyecto());
           	
           	if(facilitadores != null && !facilitadores.isEmpty()){
           		
           		NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
           		
       			String mensaje = "";
       			String mensajeProponente = "";
       			
       			if(!completarInformeVisitaPreviaPSBean.getObservado()){
       				mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionVisitaPreviaAprobado", new Object[]{});
       				mensajeProponente = mensaje;
       				mensaje = mensaje.replace("fecha_ingreso", JsfUtil.getDateFormat(new Date()));
       				mensajeProponente = mensajeProponente.replace("fecha_ingreso", JsfUtil.getDateFormat(new Date()));
       			}else{
       				mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionVisitaPreviaRechazado", new Object[]{});
       				mensajeProponente = mensaje;
       			}
           		
           		for(FacilitadorProyecto facilitadorMail : facilitadores){
           			
           			Usuario usuario = usuarioFacade.buscarUsuarioPorId(facilitadorMail.getUsuario().getId());
		           		
               		List<Contacto> listaContactos = contactoFacade.buscarPorPersona(usuario.getPersona());
           			String emailFacilitador = "";
           			for(Contacto contacto : listaContactos){
           				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
           					emailFacilitador = contacto.getValor();
           					break;
           				}				
           			}
           			 
           			//mail facilitador          		
           			mensaje = mensaje.replace("nombre_usuario", usuario.getPersona().getNombre());
           			mail_a.sendEmailInformacion(emailFacilitador, mensaje, "Notificación de Visita Previa", proyectosBean.getProyecto().getCodigo(), usuario, loginBean.getUsuario());           			
        			break;
           		}  
           		
           		//mail proponente
           		mensajeProponente = mensajeProponente.replace("nombre_usuario", proyectosBean.getProponente());
    			mail_a.sendEmailInformacion(proyectosBean.getCorreo(), mensajeProponente, "Notificación de Visita Previa", proyectosBean.getProyecto().getCodigo(), proyectosBean.getProyecto().getUsuario(), loginBean.getUsuario());
           	}       	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
