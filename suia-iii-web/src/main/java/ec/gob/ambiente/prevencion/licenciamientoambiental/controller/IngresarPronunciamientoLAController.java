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

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.licenciamientoambiental.bean.IngresarPronunciamientoLABean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class IngresarPronunciamientoLAController implements Serializable {

    private static final long serialVersionUID = -35903712834524686L;
    private static final Logger LOGGER = Logger
            .getLogger(IngresarPronunciamientoLAController.class);
    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private AreaFacade areaFacade;

    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

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
    @ManagedProperty(value = "#{ingresarPronunciamientoLABean}")
    private IngresarPronunciamientoLABean ingresarPronunciamientoLABean;


    public String realizarTarea() {
        if (ingresarPronunciamientoLABean.isDescargado()) {
            if (ingresarPronunciamientoLABean.getOficioPdf() != null) {
                try {
                    //Subir Pronunciamiento de Ministerio de Justicia
                    licenciaAmbientalFacade.ingresarPronunciamiento(ingresarPronunciamientoLABean.getPronunciamiento(),
                            ingresarPronunciamientoLABean.getProyecto().getId(), ingresarPronunciamientoLABean.getProyecto().getCodigo(),
                            bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.PRONUNCIAMIENTO_MINISTERIO_JUSTICIA);

                    //Set process variables
                    Map<String, Object> params = new ConcurrentHashMap<>();
                    params.put("pronunciamientoFavorable", ingresarPronunciamientoLABean.isPronunciamientoFavorable());
                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);

                    //Aprobar tarea
                    taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                            bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId() , new ConcurrentHashMap<String, Object>(), loginBean.getPassword(),
                            Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                    return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
                }catch (Exception e) {
                    LOGGER.error("Error al enviar los datos de la tarea de ingreso de pronunciamiento.", e);
                    JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
                }
            } else {
                JsfUtil.addMessageError("Para continuar debe adjuntar el documento de pronunciamiento.");
            }
        } else {
            JsfUtil.addMessageError("Para continuar debe descargar el documento.");
        }
        return "";
    }
}
