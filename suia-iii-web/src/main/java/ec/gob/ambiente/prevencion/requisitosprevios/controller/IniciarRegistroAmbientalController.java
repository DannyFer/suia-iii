package ec.gob.ambiente.prevencion.requisitosprevios.controller;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.requisitosprevios.bean.InicioRegistroAmbientalBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.v2.CategoriaIIFacadeV2;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

@RequestScoped
@ManagedBean
public class IniciarRegistroAmbientalController implements Serializable {

    private static final long serialVersionUID = -35908514563924686L;
    private static final Logger LOGGER = Logger
            .getLogger(IniciarRegistroAmbientalController.class);
    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private AreaFacade areaFacade;

    @EJB
    private CategoriaIIFacadeV2 categoriaIIFacade;

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
    @ManagedProperty(value = "#{inicioRegistroAmbientalBean}")
    private InicioRegistroAmbientalBean inicioRegistroAmbientalBean;

    public String iniciarProcesoRegistroAmbiental() {
        try {
            //Start Process
            categoriaIIFacade.inicarProcesoCategoriaII(loginBean.getUsuario(),
                    inicioRegistroAmbientalBean.getProyecto());

            //Finalizar tarea
            taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(),
                    new ConcurrentHashMap<String, Object>(), loginBean.getPassword(),
                    Constantes.getUrlBusinessCentral(),Constantes.getRemoteApiTimeout(),Constantes.getNotificationService());

            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (Exception e) {
            LOGGER.error("Terminar proceso de Requisitos previos e iniciar Registro Ambiental.", e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }
        return "";
    }
}
