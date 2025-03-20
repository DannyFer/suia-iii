package ec.gob.ambiente.prevencion.requisitosprevios.controller;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.requisitosprevios.bean.InicioCertificadoViabilidadBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoViabilidadAmbientalInterseccionProyectoFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

@RequestScoped
@ManagedBean
public class IniciarCertificadoViabilidadController implements Serializable {

    private static final long serialVersionUID = -35903562563924686L;
    private static final Logger LOGGER = Logger
            .getLogger(IniciarCertificadoViabilidadController.class);
    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private AreaFacade areaFacade;

    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

    @EJB
    private CertificadoViabilidadAmbientalInterseccionProyectoFacade certificadoViabilidadService;

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
    @ManagedProperty(value = "#{inicioCertificadoViabilidadBean}")
    private InicioCertificadoViabilidadBean inicioCertificadoViabilidadBean;

    public String iniciarProcesoCertificadoViabilidad() {
                try {
                    //Run task for process ending
                    ProcessInstanceLog process = procesoFacade.getProcessInstanceLog(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());

                    taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                            bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), new ConcurrentHashMap<String, Object>(), loginBean.getPassword(),
                            Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

                    //Start Process
                    certificadoViabilidadService.inicarProceso(inicioCertificadoViabilidadBean.getProyecto(), loginBean.getUsuario());
                    //Go to the firts process task
                    return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
                }catch (Exception e) {
                    LOGGER.error("Terminar proceso de Requisitos previos e iniciar Certificado de interseccion.", e);
                    JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
                }
        return "";
    }
}
