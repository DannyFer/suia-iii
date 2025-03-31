package ec.gob.ambiente.prevencion.participacionsocial.controller;


import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.participacionsocial.bean.IngresarDocumentacionComplementariaBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ManagedBean
@RequestScoped
public class IngresarDocumentacionComplementariaController implements Serializable {


    private static final long serialVersionUID = 2723751322991475905L;
    private static final Logger LOGGER = Logger.getLogger(IngresarDocumentacionComplementariaController.class);
    private static final String COMPLETADA = "Completada";


    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;

    @Getter
    @Setter
    @ManagedProperty(value = "#{ingresarDocumentacionComplementariaBean}")
    private IngresarDocumentacionComplementariaBean ingresarDocumentacionComplementariaBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;


    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    private String documentoActivo = "";

    @Getter
    @Setter
    private Map<String, Documento> documentos;


    @PostConstruct
    private void init() {
        this.documentos = ingresarDocumentacionComplementariaBean.getDocumentos();
    }


    /*GUARDAR DOCUMENTO inicio*/
    public void uploadListenerDocumentos(FileUploadEvent event) {
        documentoActivo = event.getComponent().getAttributes().get("clave").toString();
        Documento documento = this.uploadListener(event, RegistroMediosParticipacionSocial.class, "rar");
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

        documento.setMime("application/octet-stream");
        return documento;
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
    /*GUARDAR DOCUMENTO fin de bloque*/

    public void guardar() {
        try {


            //GUARDAR TEXTO RESPUESTA
            participacionSocialFacade.guardar(ingresarDocumentacionComplementariaBean.getProyectoPPS());


            //Guardar el archivo adjunto
            participacionSocialFacade.ingresarInformes(ingresarDocumentacionComplementariaBean.getDocumentos(), bandejaTareasBean.getProcessId(),
                    bandejaTareasBean.getTarea().getTaskId(), proyectosBean.getProyecto().getCodigo());


            JsfUtil.addMessageInfo("Datos guardados correctamente.");
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("No se proceso correctamente");
        }

    }

    public String finalizar() {
        try {
            if (ingresarDocumentacionComplementariaBean.isRevisar()) {

                if (observacionesController.validarObservaciones(loginBean.getUsuario(), !ingresarDocumentacionComplementariaBean.getInformacionCompleta(), "ingresarCorreccionesDocumentacion")) {

                    Map<String, Object> params = new HashMap<>();
                    params.put("ingresarCorreccionesDocumentacion", !ingresarDocumentacionComplementariaBean.getInformacionCompleta()
                    );
                    params.put("existenObservacionesInformacionProponente", ingresarDocumentacionComplementariaBean.getInformacionCompleta()
                    );
                    params.put("requiereCriterioTecnico", ingresarDocumentacionComplementariaBean.getCriterioTecnico()
                    );
                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params);
                } else {
                    return "";
                }
            }

            /*Map<String, Object> param = new HashMap<>();

            param.put("completaCorrecta", true);
            param.put("requiereInformacionPromotor", false);
            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), param);
*/

            //GUARDAR TEXTO RESPUESTA
            participacionSocialFacade.guardar(ingresarDocumentacionComplementariaBean.getProyectoPPS());


            //Guardar el archivo adjunto
            participacionSocialFacade.ingresarInformes(ingresarDocumentacionComplementariaBean.getDocumentos(), bandejaTareasBean.getProcessId(),
                    bandejaTareasBean.getTarea().getTaskId(), proyectosBean.getProyecto().getCodigo());

            //Finalizar la tarea
            Map<String, Object> data = new HashMap<>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

            JsfUtil.addMessageInfo("Información Guardada Correctamente");
            return "/bandeja/bandejaTareas.jsf";

        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("No se proceso correctamente");
            return "#";
        }

    }


    public String finalizar2() {
        try {

            if (observacionesController.validarObservaciones(loginBean.getUsuario(), !ingresarDocumentacionComplementariaBean.getInformacionCompleta(), "ingresarCorreccionesDocumentacion")) {

                Map<String, Object> params = new HashMap<>();
                params.put("existenObservacionesInformacionProponente", ingresarDocumentacionComplementariaBean.getInformacionCompleta()
                );

                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);
            } else {
                return "";
            }

            //Finalizar la tarea
            Map<String, Object> data = new HashMap<>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

            JsfUtil.addMessageInfo("Información Guardada Correctamente");
            return "/bandeja/bandejaTareas.jsf";

        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("No se proceso correctamente");
            return "#";
        }

    }

    public void validarTareaBpm() {

        String url = "/prevencion/participacionsocial/ingresarCorreccionesDocumentacion.jsf";
        if (!ingresarDocumentacionComplementariaBean.getTipo().isEmpty()) {
            url += "?tipo=" + ingresarDocumentacionComplementariaBean.getTipo();
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

    public void validarTareaBpm2() {

        String url = "/prevencion/participacionsocial/requiereCorrecciones.jsf";
        if (!ingresarDocumentacionComplementariaBean.getTipo().isEmpty()) {
            url += "?tipo=" + ingresarDocumentacionComplementariaBean.getTipo();
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }
}