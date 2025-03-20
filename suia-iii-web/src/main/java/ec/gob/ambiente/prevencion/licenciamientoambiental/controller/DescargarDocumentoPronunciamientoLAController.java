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
import ec.gob.ambiente.prevencion.licenciamientoambiental.bean.DescargarDocumentoPronunciamientoLABean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class DescargarDocumentoPronunciamientoLAController implements Serializable {

    private static final long serialVersionUID = -35903789674217786L;
    private static final Logger LOGGER = Logger
            .getLogger(DescargarDocumentoPronunciamientoLAController.class);


    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

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
    @ManagedProperty(value = "#{descargarDocumentoPronunciamientoLABean}")
    private DescargarDocumentoPronunciamientoLABean descargarDocumentoPronunciamientoLABean;


    public String iniciarTarea() {

        if (descargarDocumentoPronunciamientoLABean.isDescargado() && descargarDocumentoPronunciamientoLABean.getPronunciamiento() != null) {
            try {
                estudioImpactoAmbientalFacade.ingresarDocumento(descargarDocumentoPronunciamientoLABean.getPronunciamiento(),
                        descargarDocumentoPronunciamientoLABean.getProyecto().getId(), descargarDocumentoPronunciamientoLABean.getProyecto().getCodigo(), bandejaTareasBean.getTarea().getProcessInstanceId(),
                        bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA,
                        descargarDocumentoPronunciamientoLABean.getPronunciamiento().getName());
                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                        bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data, loginBean.getPassword(),
                        Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } catch (JbpmException e) {
                LOGGER.error(e);
                JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
            } catch (Exception e) {
                LOGGER.error(e);
                JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
            }
        } else {
            if(!descargarDocumentoPronunciamientoLABean.isDescargado() ) {
                JsfUtil.addMessageError("Para continuar debe descargar el documento.");
            }

            if(descargarDocumentoPronunciamientoLABean.getPronunciamiento() == null ) {
                JsfUtil.addMessageError("Para continuar debe subir las preguntas contestadas.");
            }
        }

        return "";
    }
}
