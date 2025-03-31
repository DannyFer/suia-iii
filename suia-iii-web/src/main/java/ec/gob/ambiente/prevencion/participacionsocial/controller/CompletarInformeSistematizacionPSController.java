package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.participacionsocial.bean.CompletarInformeSistematizacionPSBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
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
import java.util.HashMap;
import java.util.Map;

@RequestScoped
@ManagedBean
public class CompletarInformeSistematizacionPSController implements Serializable {

    private static final Logger LOGGER = Logger
            .getLogger(CompletarInformeSistematizacionPSController.class);
    private static final long serialVersionUID = -805697069146961560L;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;


    @Getter
    @Setter
    @ManagedProperty(value = "#{completarInformeSistematizacionPSBean}")
    private CompletarInformeSistematizacionPSBean completarInformeSistematizacionPSBean;


    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;


    public String enviarDatos() {
        if (completarInformeSistematizacionPSBean.completado()) {
            try {

                try {
                    if (!completarInformeSistematizacionPSBean.isRevisar()) {
                        participacionSocialFacade.guardar(completarInformeSistematizacionPSBean.getParticipacionSocialAmbiental());
                        // insertar adjunto
                        participacionSocialFacade.ingresarInformes(
                                completarInformeSistematizacionPSBean.getDocumentos(), bandejaTareasBean
                                        .getProcessId(), bandejaTareasBean.getTarea()
                                        .getTaskId(), completarInformeSistematizacionPSBean.getProyectosBean().getProyecto().getCodigo());
                    } else {
                        if (observacionesController.validarObservaciones(loginBean.getUsuario(), !completarInformeSistematizacionPSBean.getObservado(), "visitaPrevia")) {

                            Map<String, Object> data = new HashMap<>();
                            data.put("aceptarInformacion", !completarInformeSistematizacionPSBean.getObservado());
                            data.put("visitaPrevia", true);
                            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(),  bandejaTareasBean.getProcessId(), data);

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


    public String enviarDatos(Integer pantalla) {
        try {
            Map<String, Object> data = new HashMap<>();

            switch (pantalla) {
                case 1:
                    data.put("completaCorrecta", true);
                    data.put("requiereInformacionPromotor", false);
                    break;
                case 2:
                    data.put("apruebaPPS", true);
                    data.put("requiereCriterioTecnico", true);
                    break;
                default:
                    break;
            }
            if (!data.keySet().isEmpty()) {
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(),  bandejaTareasBean.getProcessId(), data);

            }
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil
                    .actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (Exception e) {
            LOGGER.error("Error al subir el pronunciamiento", e);
            JsfUtil.addMessageError("Error al subir el pronunciamiento al servidor.");

        }

        return "";
    }
}
