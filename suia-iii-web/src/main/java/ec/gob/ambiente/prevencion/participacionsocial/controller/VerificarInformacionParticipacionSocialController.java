package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.participacionsocial.bean.VerificarInformacionParticipacionSocialBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestScoped
@ManagedBean
public class VerificarInformacionParticipacionSocialController implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3704058563323622190L;
    private static final Logger LOGGER = Logger
            .getLogger(VerificarInformacionParticipacionSocialController.class);
    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private MensajeNotificacionFacade mensajeNotificacionFacade;
    @EJB
    private AreaFacade areaFacade;

    @EJB
    private ConexionBpms conexionBpms;
    
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
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{verificarInformacionParticipacionSocialBean}")
    private VerificarInformacionParticipacionSocialBean verificarInformacionParticipacionSocialBean;

    public String iniciarTarea() {

        try {

            Map<String, Object> params = new HashMap<String, Object>();

            params.put("requiereFacilitador",
                    verificarInformacionParticipacionSocialBean
                            .getRequiereFacilitador());

            if (verificarInformacionParticipacionSocialBean
                    .getRequiereFacilitador() && verificarInformacionParticipacionSocialBean.getNumeroFacilitadores() != null ) {
                params.put("numeroFacilitadores",
                        verificarInformacionParticipacionSocialBean
                                .getNumeroFacilitadores());
                params.put("bodyNotificacionFacilitadoresAdicionalesPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                        "bodyNotificacionFacilitadoresPS", new Object[]{proyectosBean.getProyecto().getCodigo(), verificarInformacionParticipacionSocialBean
                                .getNumeroFacilitadores()}));
                params.put("asuntoNotificacionFacilitadoresAdicionalesPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                        "asuntoNotificacionFacilitadoresPS", new Object[]{}));
                
                //Cris F: aumento para enviar el mensaje de notificación al proponente para el pago.
                String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionPagoFacilitadores", new Object[]{});

    			mensaje = mensaje.replace("nombre_proponente", proyectosBean.getProponente());

            	//creo el usuario del proponente para enviar como variable
            	Usuario usuarioProponente = null;
                NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
    			mail_a.sendEmailInformacionProponente(proyectosBean.getCorreo(), proyectosBean.getProponente(), mensaje, "Pago Facilitadores", proyectosBean.getProyecto().getCodigo(), usuarioProponente, loginBean.getUsuario());
    			                
            }

            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                    .getTarea().getProcessInstanceId(), params);
//            for (Map.Entry<String, Object> parametros : params.entrySet()) {
//                conexionBpms.updateVariables(bandejaTareasBean.getTarea().getProcessInstanceId(), parametros.getKey(),parametros.getValue().toString());
//            }
            
            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        }catch (JbpmException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }catch (ServiceException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }

        return "";/*
        Una vez revisado el informe de visita previa del proyecto %s me permito informar que para continuar con el proceso de participación social es necesario la parecencia de %s facilitador/es socioambiental/es adicional/es registrado/s por el Ministerio del Ambiente. Para lo cual deberá cancelar los valores correspondientes a los servicios de facilitación establecidos en la norma vigente.<br /> Recuerde que una vez cancelado el pago por servicios de facilitación deberá ingresar el Número de referencia del comprobante de pago o el número de transferencia del sistema SUIA.
        */
    }


    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/participacionsocial/verificarInformacion.jsf");
    }
}
