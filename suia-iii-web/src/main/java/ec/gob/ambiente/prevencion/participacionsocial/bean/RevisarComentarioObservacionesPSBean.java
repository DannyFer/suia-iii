package ec.gob.ambiente.prevencion.participacionsocial.bean;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbientalComentarios;
import ec.gob.ambiente.suia.domain.PreguntasFacilitadoresAmbientales;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialAmbientalComentariosFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.PreguntasFacilitadoresAmbientalesFacade;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ViewScoped
@ManagedBean
public class RevisarComentarioObservacionesPSBean implements Serializable {

    private static final long serialVersionUID = -5313896011375914273L;

    private static final Logger LOGGER = Logger.getLogger(RevisarComentarioObservacionesPSBean.class);
    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;
    @EJB
    private ParticipacionSocialAmbientalComentariosFacade participacionSocialAmbientalComentariosFacade;
    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private PreguntasFacilitadoresAmbientalesFacade preguntasFacilitadoresAmbientalesFacade;
    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;


    @Setter
    @Getter
    private ParticipacionSocialAmbiental participacionSocialAmbiental;


    @Setter
    @Getter
    private List<ParticipacionSocialAmbientalComentarios> comentarios;

    @Getter
    @Setter
    private boolean revisar;

    @Getter
    @Setter
    private Boolean informacionCompleta;

    @Getter
    @Setter
    private String tipo = "";


    @Getter
    @Setter
    private Map<Integer, Documento> documentos;

    @Getter
    @Setter
    private List<PreguntasFacilitadoresAmbientales> listaPreguntas;

    @Getter
    @Setter
    private ParticipacionSocialAmbiental proyectoPPS;

    @PostConstruct
    public void init() {
        revisar = true;
        comentarios = participacionSocialAmbientalComentariosFacade.getCommentsByProjectId(proyectosBean.getProyecto());
        List<Integer> ids = new ArrayList<>();
        for (ParticipacionSocialAmbientalComentarios com : comentarios) {
            ids.add(com.getId());
        }
        try {
            documentos = participacionSocialFacade.recuperarDocumentosTipoIds(ids, ParticipacionSocialAmbientalComentarios.class.getSimpleName(), "convocatoriaPublica");

            proyectoPPS = participacionSocialFacade.getProyectoParticipacionSocialByProject(proyectosBean.getProyecto());
            listaPreguntas = preguntasFacilitadoresAmbientalesFacade.obtenerPreguntasPorParticipacion(proyectoPPS.getId());
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            documentos = new HashMap<>();
        }

        if (proyectosBean.getProyecto() != null) {
            setParticipacionSocialAmbiental(participacionSocialFacade.getProyectoParticipacionSocialByProject(proyectosBean.getProyecto()));

        }


    }

    public StreamedContent getStreamContent(Integer id) throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (documentos.containsKey(id) && documentos.get(id).getNombre() != null
                    ) {

                if (documentos.get(id).getContenidoDocumento() == null) {
                    Documento documento = this.descargarAlfresco(documentos.get(id));
                    documentos.put(id, documento);
                }

                if (documentos.get(id).getContenidoDocumento() != null) {
                    content = new DefaultStreamedContent(new ByteArrayInputStream(documentos.get(id).getContenidoDocumento()));
                    content.setName(documentos.get(id).getNombre());
                } else {
                    JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
                }
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
}
