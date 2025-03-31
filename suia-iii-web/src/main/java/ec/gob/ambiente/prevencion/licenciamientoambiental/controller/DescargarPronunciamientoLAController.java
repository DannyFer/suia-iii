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
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class DescargarPronunciamientoLAController implements Serializable {

    private static final long serialVersionUID = -35361475834217786L;
    private static final Logger LOGGER = Logger
            .getLogger(DescargarPronunciamientoLAController.class);
    @EJB
    private ProcesoFacade procesoFacade;


    @EJB
    private AreaFacade areaFacade;

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
    @ManagedProperty(value = "#{descargarPronunciamientoLABean}")
    private DescargarPronunciamientoLABean descargarPronunciamientoLABean;

    public String iniciarTarea() {

        if (descargarPronunciamientoLABean.isDescargado()) {
            try {
                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                        bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId()
                        , data, loginBean.getPassword(),
                        Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } catch (JbpmException e) {
                LOGGER.error(e);
                JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
            }
        } else {
            JsfUtil.addMessageError("Para continuar debe descargar el documento.");
        }

        return "";
    }
}
