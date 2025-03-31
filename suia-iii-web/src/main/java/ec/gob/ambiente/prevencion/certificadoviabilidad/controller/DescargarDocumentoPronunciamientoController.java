package ec.gob.ambiente.prevencion.certificadoviabilidad.controller;

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
import ec.gob.ambiente.prevencion.certificadoviabilidad.bean.DescargarDocumentoPronunciamientoBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoViabilidadAmbientalInterseccionProyectoFacade;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class DescargarDocumentoPronunciamientoController implements Serializable {

    private static final long serialVersionUID = -65871475834217786L;
    private static final Logger LOGGER = Logger
            .getLogger(DescargarDocumentoPronunciamientoController.class);

    @EJB
    private CertificadoViabilidadAmbientalInterseccionProyectoFacade certificadoViabilidadFacade;

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
    @ManagedProperty(value = "#{descargarDocumentoPronunciamientoBean}")
    private DescargarDocumentoPronunciamientoBean descargarDocumentoPronunciamientoBean;

    public String iniciarTarea() {

        if (descargarDocumentoPronunciamientoBean.isDescargado()) {
            if(descargarDocumentoPronunciamientoBean.getCuestionario() != null) {
                try {
                    //Adjuntar documento con respuestas
                    certificadoViabilidadFacade.adjuntarPreguntas(descargarDocumentoPronunciamientoBean.getCuestionario(),
                            descargarDocumentoPronunciamientoBean.getProyecto().getId(), descargarDocumentoPronunciamientoBean.getProyecto().getCodigo(),
                            getBandejaTareasBean().getProcessId(), getBandejaTareasBean().getTarea().getTaskId(), TipoDocumentoSistema.PREGUNTAS_RESPUESTAS_INTERSECCION);

                    //Aprobar tarea
                    Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                    taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                            bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId()
                            , data, loginBean.getPassword(),
                            Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                    return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
                } catch (Exception e) {
                    LOGGER.error(e);
                    JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
                }
            }
            else {
                JsfUtil.addMessageError("Para continuar debe adjuntar el documento con las respuestas.");
            }
        } else {
            JsfUtil.addMessageError("Para continuar debe descargar el documento.");
        }

        return "";
    }
}
