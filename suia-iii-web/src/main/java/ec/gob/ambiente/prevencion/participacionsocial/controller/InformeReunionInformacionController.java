package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.participacionsocial.bean.InformeReunionInformacionBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.InformeReunionInformacionFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean
@RequestScoped
public class InformeReunionInformacionController implements Serializable {

    private static final long serialVersionUID = -5000821815587464004L;


    private static final Logger LOGGER = Logger.getLogger(InformeReunionInformacionController.class);
    private static final String COMPLETADA = "Completada";


    @EJB
    private InformeReunionInformacionFacade informeReunionInformacionFacade;

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
    @ManagedProperty(value = "#{informeReunionInformacionBean}")
    private InformeReunionInformacionBean informeReunionInformacionBean;

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

    @PostConstruct
    private void init() {
        this.documentos = informeReunionInformacionBean.getDocumentos();
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
    
    public StreamedContent getStreamContentFile(String documentoActivo) throws Exception {
    	
    	
        DefaultStreamedContent content = null;
        Documento documento = this.descargarAlfrescoFile(documentos.get(documentoActivo));
        try {
            if (documento != null && documento.getNombre() != null
                    && documento.getContenidoDocumentoFile() != null) {
            	File file=documento.getContenidoDocumentoFile();
            	content = new DefaultStreamedContent(new FileInputStream(file), new MimetypesFileTypeMap().getContentType(file));
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
			try {
				documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        if (documentoContenido != null)
            documento.setContenidoDocumento(documentoContenido);
        return documento;
    }
    
    public Documento descargarAlfrescoFile(Documento documento) throws CmisAlfrescoException {
        File documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
			try {
				documentoContenido = documentosFacade.descargarFile(documento.getIdAlfresco());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        if (documentoContenido != null)
            documento.setContenidoDocumentoFile(documentoContenido);
        return documento;
    }
    /*GUARDAR DOCUMENTO fin de bloque*/


    public void limpiar() {
        try {

            informeReunionInformacionBean.setRegistroMediosParticipacionSocial(new RegistroMediosParticipacionSocial());
            informeReunionInformacionBean.getRegistroMediosParticipacionSocial().setCatalogoMedio(informeReunionInformacionBean.getCatalogoMedio());
            informeReunionInformacionBean.loadDataTable();
            informeReunionInformacionBean.setCatalogoMedio(null);

        } catch (Exception e) {
            // JsfUtil.addMessageError("No se pudo limpiar el registro");
        }

    }

    public void guardarInformeReunion() {
        try {
            informeReunionInformacionBean.getRegistroMediosParticipacionSocial().setParticipacionSocialAmbiental(informeReunionInformacionBean.getParticipacionSocialAmbiental());
            informeReunionInformacionBean.getRegistroMediosParticipacionSocial().setCatalogoMedio(informeReunionInformacionBean.getCatalogoMedio());
            informeReunionInformacionFacade.guardarMedioVerificacion(informeReunionInformacionBean.getRegistroMediosParticipacionSocial());
            limpiar();
            RequestContext.getCurrentInstance().execute(
                    "PF('docResp').hide();");
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("No se pudo agregar el informe de Reunión");
        }

    }

    public void editarInformeReunion(RegistroMediosParticipacionSocial recordEdit) {
        try {
            informeReunionInformacionBean.setRegistroMediosParticipacionSocial(recordEdit);
            informeReunionInformacionBean.setCatalogoMedio(recordEdit.getCatalogoMedio());

        } catch (Exception e) {
            JsfUtil.addMessageError("No se pudo editar el informe de Reunión");
        }

    }

    public void eliminarInformeReunion(RegistroMediosParticipacionSocial recordDelete) {
        try {
            informeReunionInformacionFacade.eliminarMedioVerificacion(recordDelete);
            informeReunionInformacionBean.loadDataTable();
            JsfUtil.addMessageInfo("Informe de Reunión eliminado correctamente");
        } catch (Exception e) {
            JsfUtil.addMessageError("No se pudo eliminar el Informe de Reunion");
        }

    }

    public void guardar() {
        try {
            if (documentos.size() > 0) {


                //Guardar el archivo adjunto
                participacionSocialFacade.ingresarInformes(informeReunionInformacionBean.getDocumentos(), bandejaTareasBean.getProcessId(),
                        bandejaTareasBean.getTarea().getTaskId(), proyectosBean.getProyecto().getCodigo());


                List<Documento> documentosGuardados = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                        (RegistroMediosParticipacionSocial.class.getSimpleName()), TipoDocumentoSistema.PS_INFORME_REUNION_INFORMATIVA);

               /* if (documentosGuardados.size() > 0) {
                    Documento doc = documentosGuardados.get(0);


                    //Guardar el id del archivo
                    for (RegistroMediosParticipacionSocial record : informeReunionInformacionBean.getRegistrosMediosParticipacionSocial()) {
                        record.setDocumento(doc);
                        informeReunionInformacionFacade.guardarMedioVerificacion(record);

                    }
                }*/
            }
            this.informeReunionInformacionBean.getMecanismosParticipacionSocialFacade().guardar(this.informeReunionInformacionBean.getListaMecanismos(),
                    this.informeReunionInformacionBean.getListaMecanismosEliminados());

            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
        } catch (Exception e) {
            JsfUtil.addMessageInfo(JsfUtil.ERROR_GUARDAR_REGISTRO);
        }

    }

    public String finalizar() {
        try {
            if (informeReunionInformacionBean.isRevisar()) {

                if (observacionesController.validarObservaciones(loginBean.getUsuario(), informeReunionInformacionBean.getInformacionCompleta(), "informeReunionInformativa")) {
                    //Guardar el archivo adjunto

                    Map<String, Object> params = new HashMap<>();
                    params.put("informacionCompleta", informeReunionInformacionBean.getInformacionCompleta()
                    );

                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params);
                } else {
                    return "";
                }
            }

            if (documentos.size() > 0) {


                //Guardar el archivo adjunto
                participacionSocialFacade.ingresarInformes(informeReunionInformacionBean.getDocumentos(), bandejaTareasBean.getProcessId(),
                        bandejaTareasBean.getTarea().getTaskId(), proyectosBean.getProyecto().getCodigo());


                List<Documento> documentosGuardados = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                        (RegistroMediosParticipacionSocial.class.getSimpleName()), TipoDocumentoSistema.PS_INFORME_REUNION_INFORMATIVA);
                this.informeReunionInformacionBean.getMecanismosParticipacionSocialFacade().guardar(this.informeReunionInformacionBean.getListaMecanismos(),
                        this.informeReunionInformacionBean.getListaMecanismosEliminados());

  
               /* if (documentosGuardados.size() > 0) {
                    Documento doc = documentosGuardados.get(0);


                    //Guardar el id del archivo
                    for (RegistroMediosParticipacionSocial record : informeReunionInformacionBean.getRegistrosMediosParticipacionSocial()) {
                        record.setDocumento(doc);
                        informeReunionInformacionFacade.guardarMedioVerificacion(record);

                    }
                }*/
                //Finalizar la tarea
                Map<String, Object> data = new HashMap<>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
                return "/bandeja/bandejaTareas.jsf";
            } else {
                JsfUtil.addMessageError("No se han ingresado resplados de informe o el archivo es nulo");
                return "";

            }

        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }

    }


    public void validarTareaBpm() {

        String url = "/prevencion/participacionsocial/ingresarInformeReunionInform.jsf";
        if (!informeReunionInformacionBean.getTipo().isEmpty()) {
            url += "?tipo=" + informeReunionInformacionBean.getTipo();
        }
        if(!informeReunionInformacionBean.getTipo().equals("revisarDatos")) {
            JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
        }
    }

}