package ec.gob.ambiente.prevencion.requisitosprevios.controller;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.requisitosprevios.bean.InicioLicenciaAmbientalBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoViabilidadAmbientalInterseccionProyectoFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class IniciarLicenciaAmbientalController implements Serializable {

    private static final long serialVersionUID = -35903562563924686L;
    private static final Logger LOGGER = Logger
            .getLogger(IniciarLicenciaAmbientalController.class);
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
    @ManagedProperty(value = "#{inicioLicenciaAmbientalBean}")
    private InicioLicenciaAmbientalBean inicioLicenciaAmbientalBean;

    public String iniciarProcesoLicenciaAmbiental() {
                try {

                    //Start Process
                    licenciaAmbientalFacade.iniciarProcesoLicenciaAmbiental(loginBean.getUsuario(), inicioLicenciaAmbientalBean.getProyecto());

                    taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                            bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), new ConcurrentHashMap<String, Object>(),  loginBean.getPassword(),
                            Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                     //Go to the firts process task
                    return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
                }catch (Exception e) {
                    LOGGER.error("Terminar proceso de Requisitos previos e iniciar Certificado de interseccion.", e);
                    JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
                }
        return "";
    }
}
