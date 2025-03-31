package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ManagedBean
@ViewScoped
public class IngresarParticipacionSocialLABean implements Serializable {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(IngresarParticipacionSocialLABean.class);
    private static final long serialVersionUID = -8446328297902369148L;

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private UsuarioFacade usuarioFacade;


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

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;


    @Getter
    private File archivoPS;

    @Getter
    @Setter
    private boolean esEnte;
    @Getter
    @Setter
    private boolean subido = false;

    @Getter
    @Setter
    private String nombreFichero;


    @Getter
    @Setter
    private byte[] archivo;

    @Getter
    @Setter
    private Documento documentoAlfresco;


    public Date getCurrentDate() {
        return new Date();
    }

    @PostConstruct
    public void init() {
        if( bandejaTareasBean.getTarea().getVariable("esEnte")!= null){
            esEnte = Boolean.parseBoolean(bandejaTareasBean.getTarea().getVariable("esEnte").toString());
        }else{
            esEnte =  proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getId()==3;
        }
    }

    public void adjuntarComprobante(FileUploadEvent event) {
        if (event != null) {
            archivoPS = JsfUtil.upload(event);
            subido = true;
            nombreFichero = archivoPS.getName();

            JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " fue adjuntado correctamente.");
        }
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/licenciamiento-ambiental/ingresarParticipacionSocial.jsf");
    }


    public String realizarTarea() {
        try {

            if (archivoPS != null) {
                subirDocuemntoAlfresco();
            }
            //Set process variables
            Map<String, Object> params = new ConcurrentHashMap<>();
            taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), new ConcurrentHashMap<String, Object>(), loginBean.getPassword(),
                    Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (Exception e) {
            LOG.error("Error al enviar los datos de validar pago de licencia.", e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }


        return "";
    }

    /**
     * @param data
     * @return
     */
    public long iniciaParticipacionSocial(Map<String, Object> data) {
        long idProceso = 0;
        try {
            idProceso = procesoFacade.iniciarProceso(loginBean.getUsuario(),
                    Constantes.NOMBRE_PROCESO_PARTICIPACION_SOCIAL, proyectosBean.getProyecto().getCodigo(), data);

        } catch (JbpmException e) {
            LOG.error("Error al iniciar Participacion Social", e);
            JsfUtil.addMessageError("Error al iniciar la tarea Participacion Social.");
        }
        return idProceso;
    }

    public String finalizarIngresoParticipacionSocial() {
        try {
            Map<String, Object> data = new ConcurrentHashMap<String, Object>();


            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),
                    bandejaTareasBean.getProcessId(), data);


            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (JbpmException e) {
            LOG.error(e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }
        return "";
    }

    public String iniciarProceso() {
        try {
            Map<String, Object> data = participacionSocialFacade.inicializarParticipacionSocial(proyectosBean.getProyecto());

            iniciaParticipacionSocial(data);
            return finalizarIngresoParticipacionSocial();
        } catch (ServiceException e) {
            LOG.error("Error al iniciar participación social", e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }
        return  "";
    }


    public void subirDocuemntoAlfresco() throws Exception {
        licenciaAmbientalFacade.ingresarDocumentos(archivoPS,
                proyectosBean.getProyecto().getId(),
                proyectosBean.getProyecto().getCodigo(),
                bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.PARTICIPACION_SOCIAL,
                "Participacion_Social");

    }


    public StreamedContent getStreamContent() throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (archivoPS != null) {
                Path path = Paths.get(archivoPS.getAbsolutePath());
                byte[] data = Files.readAllBytes(path);
                content = new DefaultStreamedContent(new ByteArrayInputStream(data));
                content.setName(archivoPS.getName());
            } else if (archivo != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(archivo));
                content.setName(documentoAlfresco.getNombre());
            } else {
                JsfUtil.addMessageError("Error al obtener el archivo.");
            }
        } catch (Exception exception) {
            LOG.error("Error al obtener el archivo.", exception);
            JsfUtil.addMessageError("Error al obtener el archivo.");
        }
        return content;
    }
}


