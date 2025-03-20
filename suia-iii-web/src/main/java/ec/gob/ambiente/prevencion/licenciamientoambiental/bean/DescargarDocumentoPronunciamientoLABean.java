package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

@ManagedBean
@ViewScoped
public class DescargarDocumentoPronunciamientoLABean implements Serializable {

    private static final long serialVersionUID = 2975389742323455827L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(DescargarDocumentoPronunciamientoLABean.class);


    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

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
    private Integer idProyecto;

    private byte[] documento;

    private String nombreDocumento;

    @Getter
    @Setter
    private boolean descargado = false;

    @Getter
    @Setter
    private UploadedFile fichero;

    @Setter
    @Getter
    private File pronunciamiento;

    @Setter
    @Getter
    private String pronunciamientoNombre = "";

     @Setter
    @Getter
    private ProyectoLicenciamientoAmbiental proyecto;

    @PostConstruct
    public void init() {


        Map<String, Object> variables = null;
        try {
            variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());


            idProyecto = Integer.parseInt((String) variables
                    .get(Constantes.ID_PROYECTO));
            proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);

            Boolean intersecaBP = Boolean.parseBoolean((String) variables
                    .get("intersecaBP"));
            Boolean intersecaSNAP = Boolean.parseBoolean((String) variables
                    .get("intersecaSNAP"));
            Boolean intersecaRAMSARPUNTO = Boolean.parseBoolean((String) variables
                    .get("intersecaRAMSARPUNTO"));
            Boolean intersecaRAMSARAREA = Boolean.parseBoolean((String) variables
                    .get("intersecaRAMSARAREA"));

            nombreDocumento = "pregunta_pronunciamiento";
            if (intersecaBP) {
                nombreDocumento += "_intersecaBP";
            }
            if (intersecaSNAP || intersecaRAMSARPUNTO || intersecaRAMSARAREA) {
                nombreDocumento += "_intersecaSNAP";
            }
            nombreDocumento += ".pdf";

            documento = documentosFacade.descargarDocumentoPorNombre(nombreDocumento);

        } catch (JbpmException e) {
            JsfUtil.addMessageError("Error al obtener la intersección del proyecto. Intente más tarde.");
            LOG.error("Error al obtener la intersección del proyecto", e);

        } catch (CmisAlfrescoException e) {
            JsfUtil.addMessageError("Error al descargar el documento. Intente más tarde.");
            LOG.error("Error al descargar el documento. Intente más tarde.", e);
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }

    }

    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documento != null) {
            descargado = true;
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documento), "application/pdf");
            content.setName(nombreDocumento);

        }
        return content;

    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/licenciamiento-ambiental/documentosPronunciamiento.jsf");
    }


    public void uploadListener(FileUploadEvent event) {

        setPronunciamiento(JsfUtil.upload(event));
        setPronunciamientoNombre(getPronunciamiento().getName());

    }

}

