package ec.gob.ambiente.prevencion.viabilidadtecnica.bean;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.*;
import java.util.Map;

@ManagedBean
@ViewScoped
public class RevisarInformeOficioVTBean implements Serializable {

	private static final long serialVersionUID = -4920792953639127645L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RevisarInformeOficioVTBean.class);

    @Setter
    @Getter
    private String tipoRevisor;

    @Setter
    @Getter
    private Boolean requiereCorreciones;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    @EJB
    private ProcesoFacade procesoFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @PostConstruct
    public void init(){
        Map<String, String> webParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        tipoRevisor = webParams.get("tipoRevisor");

        try {
            Map<String, Object> variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());

            Integer idProyecto = Integer.parseInt((String) variables.get(Constantes.ID_PROYECTO));
            proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);

            /*if(tipoPronunciamiento.equals("Biodiversidad")){
                respuestasProponente = requisitosPreviosFacade.descargarDocumentoRequisitosPrevios(proyecto.getId(),
                        TipoDocumentoSistema.PREGUNTAS_RESPUESTAS_INTERSECCION_SNAP);
            }
            else if(tipoPronunciamiento.equals("Forestal")){
                respuestasProponente = requisitosPreviosFacade.descargarDocumentoRequisitosPrevios(proyecto.getId(),
                        TipoDocumentoSistema.PREGUNTAS_RESPUESTAS_INTERSECCION);
            }*/
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/emisionviabilidadtecnica/revisarInformeOficio.jsf?tipoRevisor=" + tipoRevisor);
    }

    /*public StreamedContent fileStreamPreguntasProponente() throws IOException {
        InputStream stream = new FileInputStream(respuestasProponente);
        DefaultStreamedContent content = new DefaultStreamedContent(stream, "application/pdf", respuestasProponente.getName());
        descargado = true;
        return content;
    }*/
}
