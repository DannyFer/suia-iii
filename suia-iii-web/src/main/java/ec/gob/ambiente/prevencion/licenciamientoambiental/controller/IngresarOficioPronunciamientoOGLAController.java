package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.InformeTecnicoGeneralLA;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ViewScoped
@ManagedBean
public class IngresarOficioPronunciamientoOGLAController implements Serializable {

    private static final Logger LOGGER = Logger
            .getLogger(IngresarOficioPronunciamientoOGLAController.class);
    private static final long serialVersionUID = 7869008437182550577L;
    @EJB
    private ProcesoFacade procesoFacade;

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
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    private File pronunciamiento;


    @Getter
    @Setter
    private Boolean tipoPronunciamiento;


    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/licenciamiento-ambiental/ingresarOficioPronunciamientoOG.jsf");
    }


    public void adjuntarPronunciamiento(FileUploadEvent event) {
        if (event != null) {
            pronunciamiento = JsfUtil.upload(event);
            JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " fue adjuntado correctamente.");
        }
    }

    public StreamedContent getStreamContent() throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (pronunciamiento != null) {
                content = new DefaultStreamedContent(new FileInputStream(pronunciamiento));
                content.setName(pronunciamiento.getName());
            } else {
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            }
        } catch (Exception exception) {
            LOGGER.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
        return content;
    }


    public String iniciarTarea() {
        try {


            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            params.put("esPronunciamientoAprobacion", tipoPronunciamiento);
            if (tipoPronunciamiento) {
                params.put("requiereOficioOG", false);
            }
            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                    .getTarea().getProcessInstanceId(), params);


            licenciaAmbientalFacade.ingresarPronunciamiento(pronunciamiento,
                    proyectosBean.getProyecto().getId(), proyectosBean.getProyecto().getCodigo(),
                    bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.ADJUNTO_OFICIO_OG, InformeTecnicoGeneralLA.class.getSimpleName());
            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (JbpmException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }

        return "";
    }
}
