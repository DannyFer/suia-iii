package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.licenciamientoambiental.bean.AnalizarEstudioLABean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class AnalizarEstudioLAController implements Serializable {

    private static final long serialVersionUID = -35263714834217786L;
    private static final Logger LOGGER = Logger
            .getLogger(AnalizarEstudioLAController.class);
    @EJB
    private ProcesoFacade procesoFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{analizarEstudioLABean}")
    private AnalizarEstudioLABean analizarEstudioLABean;


    public String iniciarTarea() {

        try {
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            params.put("requiereEquipo",
                    analizarEstudioLABean.getEquipoMultidiciplinario());

            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                    .getTarea().getProcessInstanceId(), params);

            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (JbpmException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }

        return "";
    }


    public void validarTareaBpm() {
        String url = "/prevencion/licenciamiento-ambiental/analizarEstudio.jsf";
        if (analizarEstudioLABean.getTipo() != null && !analizarEstudioLABean.getTipo().isEmpty()) {
            url += "?tipo=" + analizarEstudioLABean.getTipo();
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

    public void validarTareaInformacionBpm() {
        String url = "/prevencion/licenciamiento-ambiental/analizarInformacion.jsf";
        if (analizarEstudioLABean.getTipo() != null && !analizarEstudioLABean.getTipo().isEmpty()) {
            url += "?tipo=" + analizarEstudioLABean.getTipo();
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }


    public String iniciarTareaAnalizar() {
        try {
            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (JbpmException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }

        return "";
    }
}
