package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.controller.RevisarDocumentacionGeneralController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ManagedBean
@ViewScoped
public class DescargarOficioPSController implements Serializable {
    private static final Logger LOGGER = Logger
            .getLogger(RevisarDocumentacionGeneralController.class);
    private static final long serialVersionUID = -744255275891512368L;

    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

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

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    private Documento documento;

    private String documentOffice = "";

    @Getter
    @Setter
    private boolean mostrarInforme;

    @Getter
    @Setter
    private boolean descargado;


    @PostConstruct
    public void init() {
        try {

            List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                    (ParticipacionSocialAmbiental.class.getSimpleName()), TipoDocumentoSistema.PS_OFICIO_FINAL);
            if (documentos.size() > 0) {
                documento = documentos.get(0);
            } else {
                JsfUtil.addMessageError("Error al realizar la operación.");

            }


        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al cargar los datos. Por favor intente más tarde.");
        }

    }


    public String completarTarea() {
        try {


            if (descargado) {

                Map<String, Object> params = new HashMap<>();
                params.put("completadoProcesoPS", true);

                params.put("cambiarNumeroOficio", true);
                params.put("cambiarNumeroOficioES", true);

                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);


                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } else {
                JsfUtil.addMessageError("Debe descargar el documento.");
                return "";
            }
        } catch (JbpmException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }

    public void validarTareaBpm() {

        String url = "/prevencion/participacionsocial/descargarOficio.jsf";

        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }


    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documento != null) {
            if (documento.getContenidoDocumento() == null) {
                documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
            }
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documento.getContenidoDocumento()), "application/pdf");
            content.setName(documento.getNombre());
            descargado = true;
        }
        return content;

    }



}