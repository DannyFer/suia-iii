package ec.gob.ambiente.prevencion.participacionsocial.controller;


import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.participacionsocial.bean.IngresarDocumentacionBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.PreguntasFacilitadoresAmbientales;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.PreguntasFacilitadoresAmbientalesFacade;
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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ManagedBean
@ViewScoped
public class IngresarDocumentacionController implements Serializable {

    private static final long serialVersionUID = 514387047964538189L;
    private static final Logger LOGGER = Logger.getLogger(IngresarDocumentacionController.class);
    private static final String COMPLETADA = "Completada";


    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

    @EJB
    private PreguntasFacilitadoresAmbientalesFacade preguntasFacilitadoresAmbientalesFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{ingresarDocumentacionBean}")
    private IngresarDocumentacionBean ingresarDocumentacionBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;

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

    @Getter
    @Setter
    private boolean revisar;

    @Getter
    @Setter
    private Boolean criterioTecnico;

    @Getter
    @Setter
    private String tipo = "";

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private Boolean requiereModificaciones;

    @Getter
    @Setter
    private boolean actualizado;

    @Getter
    @Setter
    private List<PreguntasFacilitadoresAmbientales> listaPreguntas;

    @Getter
    @Setter
    private ParticipacionSocialAmbiental proyectoPPS;

    @PostConstruct
    private void init() {
        url = "/prevencion/participacionsocial/ingresarDocumentacion.jsf";
        this.documentos = ingresarDocumentacionBean.getDocumentos();
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        if (params.containsKey("tipo")) {
            url += "?tipo=revisar";
            tipo = params.get("tipo");
            if (params.get("tipo").equals("revisar")) {
                revisar = true;

            }
        }
        try {

            proyectoPPS = participacionSocialFacade.getProyectoParticipacionSocialByProject(proyectosBean.getProyecto());

            listaPreguntas = preguntasFacilitadoresAmbientalesFacade.obtenerPreguntasPorParticipacion(proyectoPPS.getId());
        } catch (Exception e) {
            LOGGER.error(e);

        }
    }


    /*GUARDAR DOCUMENTO inicio*/
    public void uploadListenerDocumentos(FileUploadEvent event) {
        documentoActivo = event.getComponent().getAttributes().get("clave").toString();
        Documento documento = this.uploadListener(event, RegistroMediosParticipacionSocial.class, "pdf");
        documentos.put(documentoActivo, documento);
        documentoActivo = "";
        actualizado = true;
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
            participacionSocialFacade.guardar(ingresarDocumentacionBean.getProyectoPPS());
            preguntasFacilitadoresAmbientalesFacade.guardarPreguntasPorParticipacion(listaPreguntas);

            if (ingresarDocumentacionBean.getDocumentos().size() > 0) {
                //Guardar el archivo adjunto

                participacionSocialFacade.ingresarInformes(ingresarDocumentacionBean.getDocumentos(), bandejaTareasBean.getProcessId(),
                        bandejaTareasBean.getTarea().getTaskId(), proyectosBean.getProyecto().getCodigo());
                actualizado = false;

            }
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al guardar la información.");
        }
    }

    public String finalizarR() {
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        try {
            if (observacionesController.validarObservaciones(loginBean.getUsuario(), !requiereModificaciones, "ingresarDocumentacion")) {


                params.put("existenObservacionesInformacionProponente", requiereModificaciones);
                params.put("completadoProcesoPS", !requiereModificaciones);


                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);
                Map<String, Object> data = new HashMap<>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                return "/bandeja/bandejaTareas.jsf";
            } else {
                return "";
            }
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return "";
        }
    }

    public String finalizar() {
        try {

            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            if (revisar) {
                if (criterioTecnico || observacionesController.validarObservaciones(loginBean.getUsuario(), !requiereModificaciones, "ingresarDocumentacion")) {


                    params.put("requiereCriterioTecnico", criterioTecnico);
                    if (!criterioTecnico) {
                        params.put("existenObservacionesInformacionProponente", requiereModificaciones);
                    }

                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params);
                    Map<String, Object> data = new HashMap<>();
                    procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                    return "/bandeja/bandejaTareas.jsf";
                } else {
                    return "";
                }

            } else {
                params.put("completadoProcesoPS", false);
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);

                //GUARDAR TEXTO RESPUESTA
                participacionSocialFacade.guardar(ingresarDocumentacionBean.getProyectoPPS());

                //Guardar el archivo adjunto
                participacionSocialFacade.ingresarInformes(ingresarDocumentacionBean.getDocumentos(), bandejaTareasBean.getProcessId(),
                        bandejaTareasBean.getTarea().getTaskId(), proyectosBean.getProyecto().getCodigo());

                preguntasFacilitadoresAmbientalesFacade.guardarPreguntasPorParticipacion(listaPreguntas);

                //Finalizar la tarea
                Map<String, Object> data = new HashMap<>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                return "/bandeja/bandejaTareas.jsf";

            }

        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return "";
        }

    }

    public void validarTareaBpm() {

        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

    public void validarTareaBpmR() {

        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/participacionsocial/revisarInformacionPronunciamientos.jsf");
    }

}