package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.prevencion.participacionsocial.bean.PublicarEstudioBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.dto.EntityProyectoParticipacionSocial;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialAmbientalComentariosFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.PublicarEsudioParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ManagedBean
@ViewScoped
public class PublicarEstudioController implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 6638752335045667658L;
    private static final Logger LOG = Logger.getLogger(PublicarEstudioController.class);
    private static final String COMPLETADA = "Completada";


    @Getter
    @Setter
    @ManagedProperty(value = "#{publicarEstudioBean}")
    private PublicarEstudioBean publicarEstudioBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;


	/*@EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;*/

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private ParticipacionSocialAmbientalComentariosFacade participacionSocialAmbientalComentariosFacade;

    @EJB
    private PublicarEsudioParticipacionSocialFacade publicarEsudioParticipacionSocialFacade;

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;
    @Getter
    @Setter
    private boolean habilitarEstadosEtapas;

    @Getter
    @Setter
    private boolean deletionActive;

    @Getter
    @Setter
    private boolean updateSuiaActive;

    @Getter
    @Setter
    private String documentoActivo = "";

    @Getter
    @Setter
    private Documento documento;

    @EJB
    ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;


    @Getter
    @Setter
    ProyectosBean proyectosBean;


    @PostConstruct
    private void init() {
        try {
            publicarEstudioBean.setProyectosPS(publicarEsudioParticipacionSocialFacade.getProyectosPPS());
            //publicarEstudioBean.setProyectosPPS(publicarEsudioParticipacionSocialFacade.getProyectosParticipacionSocial());
            publicarEstudioBean.setProyectos(publicarEsudioParticipacionSocialFacade.getProyectosPublicarEstudio(/*JsfUtil.getLoggedUser()*/));
            publicarEstudioBean.setProvincias(publicarEsudioParticipacionSocialFacade.getProvincias());
            cargarCantones();
            habilitarEstadosEtapas = false;
            deletionActive = false;
            updateSuiaActive = false;
        } catch (Exception e) {
            LOG.error("Error cargando proyectos", e);
        }
    }

    public void cargarCantones() {
        if (publicarEstudioBean.getProvincia() != null)
            publicarEstudioBean.setCantones(publicarEsudioParticipacionSocialFacade
                    .getCantonesParroquia(publicarEstudioBean.getProvincia()));
    }

    public void cargarParroquias() {
        publicarEstudioBean.setParroquias(publicarEsudioParticipacionSocialFacade
                .getCantonesParroquia(publicarEstudioBean.getCanton()));
    }

    public void iniciarProceso(Flujo flujo) {
        try {
            JsfUtil.redirectTo(flujo.getUrlInicioFlujo() + "?flujoId=" + flujo.getId());
        } catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }


    public StreamedContent getStream(Documento documento) {
        if (documento.getContenidoDocumento() != null) {
            InputStream is = new ByteArrayInputStream(documento.getContenidoDocumento());
            return new DefaultStreamedContent(is, documento.getMime(), documento.getNombre());
        } else {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
        return null;
    }


    public void guardarComentario() {
        try {
            this.publicarEstudioBean.getComentarioPPS().setFecha(new Date());
            this.publicarEstudioBean.getComentarioPPS().setUbicacionGeografica(this.publicarEstudioBean.getParroquia());
            ParticipacionSocialAmbientalComentarios comentarioAlmacenado = participacionSocialAmbientalComentariosFacade.guardarComentario(this.publicarEstudioBean.getComentarioPPS());
            if (documento != null) {
                documento.setIdTable(comentarioAlmacenado.getId());
                Map<String, Documento> lDocumento = new HashMap<>();
                lDocumento.put("convocatoriaPublica", documento);
                participacionSocialFacade.ingresarInformes(
                        lDocumento,0, 0, publicarEstudioBean.getProyectoPPS().getProyectoLicenciamientoAmbiental().getCodigo());
                documento = null;
            }

            this.publicarEstudioBean.setComentarioPPS(new ParticipacionSocialAmbientalComentarios());
            this.publicarEstudioBean.setProvincia(null);
            this.publicarEstudioBean.setCanton(null);
            this.publicarEstudioBean.setParroquia(null);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.execute("PF('comentarios').hide()");

        } catch (Exception e) {
            JsfUtil.addMessageError("No se ha guardado el Comentario");
        }

    }


    public void seleccionarProyecto(ParticipacionSocialAmbiental proyectoPPS) {
        this.publicarEstudioBean.setProyectoPPS(proyectoPPS);
        this.publicarEstudioBean.getComentarioPPS().setParticipacionSocialAmbiental(proyectoPPS);
        documento = null;
    }
    public void seleccionarProyectoPS(EntityProyectoParticipacionSocial proyectoPPS) {
            Integer pren_id = Integer.parseInt(proyectoPPS.getId_pps());
            ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbientall = this.proyectoLicenciaAmbientalFacade.getProyectoPorId(pren_id);
            ParticipacionSocialAmbiental participacionSocialAmbientall = this.participacionSocialFacade.getProyectoParticipacionSocialByProject(proyectoLicenciamientoAmbientall);
            participacionSocialAmbientall.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbientall);
            this.publicarEstudioBean.setProyectoPPS(participacionSocialAmbientall);
            this.publicarEstudioBean.getComentarioPPS().setParticipacionSocialAmbiental(participacionSocialAmbientall);
            documento = null;

    }


    public void uploadListenerDocumentos(FileUploadEvent event) {
        this.documento = this.uploadListener(event, ParticipacionSocialAmbientalComentarios.class, "pdf");
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
        //documento.setIdTable(publicarEstudioBean.getProyectoPPS().getProyectoLicenciamientoAmbiental().getId());
        documento.setExtesion("." + extension);

        documento.setMime("application/pdf");
        return documento;
    }


    public StreamedContent getStreamContent() throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (documento != null && documento.getNombre() != null
                    && documento.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
                content.setName(documento.getNombre());
            } else {
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
            }
        } catch (Exception exception) {
            LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }


}