package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.participacionsocial.bean.VerificarNumeroFacilitadoresAdicionalesPSBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
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
public class VerificarNumeroFacilitadoresAdicionalesPSController implements
        Serializable {

    /**
     *
     */
    private static final Logger LOGGER = Logger
            .getLogger(VerificarNumeroFacilitadoresAdicionalesPSController.class);
    private static final long serialVersionUID = 1898488105452902026L;
    @EJB
    MensajeNotificacionFacade mensajeNotificacionFacade;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private AreaFacade areaFacade;

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
    @ManagedProperty(value = "#{verificarNumeroFacilitadoresAdicionalesPSBean}")
    private VerificarNumeroFacilitadoresAdicionalesPSBean verificarNumeroFacilitadoresAdicionalesPSBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    public String iniciarTarea() {

        try {

            Map<String, Object> params = new HashMap<String, Object>();

            params.put("requiereMasFacilitadores",
                    verificarNumeroFacilitadoresAdicionalesPSBean
                            .getRequiereFacilitador());

            if (verificarNumeroFacilitadoresAdicionalesPSBean
                    .getRequiereFacilitador()) {
                params.put("numeroFacilitadoresAdicionales",
                        verificarNumeroFacilitadoresAdicionalesPSBean
                                .getNumeroFacilitadoresAdicionales());

                params.put("bodyNotificacionFacilitadoresAdicionalesPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                        "bodyNotificacionFacilitadoresAdicionalesPS", new Object[]{proyectosBean.getProyecto().getCodigo(), verificarNumeroFacilitadoresAdicionalesPSBean
                                .getNumeroFacilitadoresAdicionales()}));
                params.put("asuntoNotificacionFacilitadoresAdicionalesPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                        "asuntoNotificacionFacilitadoresAdicionalesPS", new Object[]{}));
                
                //Cris F: aumento para enviar el mensaje de notificación al proponente para el pago.
                String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionPagoFacilitadores", new Object[]{});

    			mensaje = mensaje.replace("nombre_proponente", proyectosBean.getProponente());
            	//cre el usuario del proponente para enviar como variable
            	Usuario usuarioProponente = null;
                NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
    			mail_a.sendEmailInformacionProponente(proyectosBean.getCorreo(), proyectosBean.getProponente(), mensaje, "Pago Facilitadores", proyectosBean.getProyecto().getCodigo(), usuarioProponente, loginBean.getUsuario());
                
            }

          //  Usuario usuario = proyectosBean.getProyecto().getUsuario();
            params.put("bodyNotificacionIniciarMecanismosPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                    "bodyNotificacionIniciarMecanismosPS", new Object[]{proyectosBean.getProyecto().getCodigo()}));
            params.put("asuntoNotificacionIniciarMecanismosPS", mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
                    "asuntoNotificacionIniciarMecanismosPS", new Object[]{}));//usuario.getPersona().getNombre()


            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                    .getTarea().getProcessInstanceId(), params);

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

        return "";
    }


    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/participacionsocial/verificarNumeroFacilitadores.jsf");
    }
}
