package ec.gob.ambiente.prevencion.participacionsocial.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;

import org.apache.log4j.Logger;

import ec.gob.ambiente.prevencion.participacionsocial.bean.RevisarComentarioObservacionesPSBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@RequestScoped
public class RevisarComentarioObservacionesPSController implements Serializable {


    private static final Logger LOGGER = Logger.getLogger(RevisarComentarioObservacionesPSController.class);
    private static final long serialVersionUID = -5292428193148376366L;


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
    @ManagedProperty(value = "#{revisarComentarioObservacionesPSBean}")
    private RevisarComentarioObservacionesPSBean revisarComentarioObservacionesPSBean;

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

    }

    public String finalizar() {
        try {
            if (observacionesController.validarObservaciones(loginBean.getUsuario(), !revisarComentarioObservacionesPSBean.getInformacionCompleta(), "ingresarDocumentacion")) {

                Map<String, Object> params = new HashMap<>();
                params.put("existenObservaciones", revisarComentarioObservacionesPSBean.getInformacionCompleta()
                );
                params.put("revisarComentarioObservaciones", revisarComentarioObservacionesPSBean.getInformacionCompleta());
                params.put("existenObservacionesInformacionProponente", revisarComentarioObservacionesPSBean.getInformacionCompleta());
                if( !revisarComentarioObservacionesPSBean.getInformacionCompleta()){

                    params.put("completadoProcesoPS", true);
                }

                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);

                //Finalizar la tarea
                Map<String, Object> data = new HashMap<>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
                procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
                return "/bandeja/bandejaTareas.jsf";
            } else {
                return "";
            }

        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return "";
        }


    }


    public void validarTareaBpm() {

        String url = "/prevencion/participacionsocial/revisarComentarioObservaciones.jsf";
        if (!revisarComentarioObservacionesPSBean.getTipo().isEmpty()) {
            url += "?tipo=" + revisarComentarioObservacionesPSBean.getTipo();
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

}