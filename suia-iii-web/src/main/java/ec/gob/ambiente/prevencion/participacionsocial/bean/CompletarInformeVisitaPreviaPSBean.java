package ec.gob.ambiente.prevencion.participacionsocial.bean;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class CompletarInformeVisitaPreviaPSBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(CompletarInformeVisitaPreviaPSBean.class);
    private static final long serialVersionUID = 1890250980501300973L;

    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoFacade;


    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Setter
    @Getter
    private ProyectoLicenciamientoAmbiental proyectoActivo;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    private Map<String, Documento> documentos, documentosInforme; //Cris F: aumento de documento de Informe
    @Getter
    @Setter
    private String documentoActivo = "";
    @Getter
    @Setter
    private boolean revisar;
    @Getter
    @Setter
    private String tipo = "";
    @Getter
    @Setter
    private List<String> listaClaves;
    @Getter
    @Setter
    private ParticipacionSocialAmbiental participacionSocialAmbiental;
    @Getter
    @Setter
    private Boolean observado;    
    /*
     * Cris F: aumento de descarga de informe técnico
     */
    @Getter
    @Setter
    private byte[] documento;
    
    private final String nombreDocumento = "plantilla de informe tecnico IVP PPS con facilitador.doc";

    @PostConstruct
    public void init() {
        // inicialización de las variables para cargar pantalla
        listaClaves = new ArrayList<>(7);
        listaClaves.add("visitaPrevia");
        listaClaves.add("invitacionesPersonales");
        listaClaves.add("convocatoriaPublica");
        listaClaves.add("actaAperturaCierre");
        listaClaves.add("actaAsamblea");
        listaClaves.add("registroCip");
        listaClaves.add("registroAsistencia");
        //CF: aumento de documentos para subir
        listaClaves.add("informeTecnicoVisitaPreviaPPS");

        /*
            if (!documentos.containsKey("registroAsistencia")) {
                stringBuilder.append("highlightComponent('form:registroAsistencia');");
            }
        * */
        try {
            Map<String, String> params = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();
            if (params.containsKey("tipo")) {
                tipo = params.get("tipo");
                if (params.get("tipo").equals("revisar")) {
                    revisar = true;

                }
            }

            documentos = new HashMap<>();
            //Cris F: inicio de documentos de Informe
            documentosInforme = new HashMap<>();


            try {
                proyectoActivo = proyectoFacade.cargarProyectoFullPorId(proyectosBean.getProyecto().getId());
            } catch (Exception e) {
                LOGGER.error("Error al cargar el proyecto.", e);
                JsfUtil.addMessageError("Ha ocurrido un error recuperando los datos del proyecto.");
            }

            participacionSocialAmbiental = participacionSocialFacade.buscarCrearParticipacionSocialAmbiental(proyectoActivo);

            documentos = participacionSocialFacade.recuperarDocumentosTipo(proyectoActivo.getId(), ParticipacionSocialFacade.class.getSimpleName(), listaClaves);
            
            //Cris F: Descarga de documentos de informe técnico
            documento = documentosFacade.descargarDocumentoPorNombre(nombreDocumento);
            observado = null;

        } catch (JbpmException e) {

            LOGGER.error(JsfUtil.ERROR_INICIALIZAR_DATOS, e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        } catch (Exception e) {

            LOGGER.error(JsfUtil.ERROR_INICIALIZAR_DATOS, e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
    }


    public StreamedContent getStreamContent(String documentoActivo) throws Exception {
        DefaultStreamedContent content = null;
        Documento documento = this.descargarAlfresco(documentos.get(documentoActivo));
        try {
            if (documento != null && documento.getNombre() != null
                    && documento.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
                content.setName(documento.getNombre());
            } else {
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
            }
        } catch (Exception exception) {
            LOGGER.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }


    public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
            documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
        if (documentoContenido != null)
            documento.setContenidoDocumento(documentoContenido);
        return documento;
    }


    public void uploadListenerDocumentos(FileUploadEvent event) {
        documentoActivo = event.getComponent().getAttributes().get("clave").toString();
        Documento documento = this.uploadListener(event, ParticipacionSocialFacade.class, "pdf");
        documentos.put(documentoActivo, documento);
        documentoActivo = "";
    }


    private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
        byte[] contenidoDocumento = event.getFile().getContents();
        Documento documento = crearDocumento(contenidoDocumento, clazz, extension);
        documento.setNombre(event.getFile().getFileName());
        return documento;
    }

    /**
     * Crea el documento
     *
     * @param contenidoDocumento arreglo de bytes
     * @param clazz              Clase a la cual se va a ligar al documento
     * @param extension          extension del archivo
     * @return Objeto de tipo Documento
     */
    public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
        Documento documento = new Documento();
        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombreTabla(clazz.getSimpleName());
        documento.setIdTable(proyectosBean.getProyecto().getId());
        documento.setExtesion("." + extension);

        documento.setMime("application/pdf");
        return documento;
    }

    public Boolean completado() {
        StringBuilder stringBuilder = new StringBuilder();
        if (documentos.keySet().size() < 7) {
            for (String clave : listaClaves) {
                if (!documentos.containsKey(clave)) {
                    stringBuilder.append("highlightComponent('form:" + clave + "');");
                }
            }
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.execute(stringBuilder.toString());
            return false;
        }
        return true;
    }


    public void validarTareaBpm() {

        String url = "/prevencion/participacionsocial/completarInformeVisitaPrevia.jsf";
        if (!tipo.isEmpty()) {
            url += "?tipo=" + tipo;
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }
    
    /**
     * Cris F: Descargar Formatos de Informe Técnico
     */
    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documento != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documento), "application/rar");
            content.setName(nombreDocumento);

        }
        return content;
    }   
    
    public void uploadListenerDocumentosInforme(FileUploadEvent event) {
        documentoActivo = event.getComponent().getAttributes().get("clave").toString();
        Documento documento = this.uploadListener(event, ParticipacionSocialAmbiental.class, "pdf");
        documentosInforme.put(documentoActivo, documento);
        documentoActivo = "";
    }
    
}
