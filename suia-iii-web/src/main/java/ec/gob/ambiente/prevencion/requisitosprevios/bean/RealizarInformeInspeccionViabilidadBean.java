package ec.gob.ambiente.prevencion.requisitosprevios.bean;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
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
import javax.faces.context.FacesContext;
import java.io.*;
import java.util.Map;

@ManagedBean
@ViewScoped
public class RealizarInformeInspeccionViabilidadBean implements Serializable {

    private static final long serialVersionUID = -4920792856339127645L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RealizarInformeInspeccionViabilidadBean.class);

    @Setter
    @Getter
    private String tipoPronunciamiento;

    @Setter
    @Getter
    private File respuestasProponente;

    @Setter
    @Getter
    private File adjuntoInformeInspeccion;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;

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
    
    @Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;

	@Getter
	@Setter
	private Documento documentoGuardado;
	
	@Getter
	@Setter
	private boolean habilitar;	
	@Getter
	@Setter
	private boolean mostrarBotonFirmar = false;
	@Getter
	@Setter
	private boolean habilitarBotonFinalizar = false;

    @PostConstruct
    public void init() {
        Map<String, String> webParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        tipoPronunciamiento = webParams.get("pronunciamiento");
        habilitar = true;

        try {
            Map<String, Object> variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());

            Integer idProyecto = Integer.parseInt((String) variables.get(Constantes.ID_PROYECTO));
            proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);

            if (tipoPronunciamiento.equals("Biodiversidad")) {
                respuestasProponente = requisitosPreviosFacade.descargarDocumentoRequisitosPrevios(proyecto.getId(),
                        TipoDocumentoSistema.PREGUNTAS_RESPUESTAS_INTERSECCION_SNAP);
            } else if (tipoPronunciamiento.equals("Forestal")) {
                respuestasProponente = requisitosPreviosFacade.descargarDocumentoRequisitosPrevios(proyecto.getId(),
                        TipoDocumentoSistema.PREGUNTAS_RESPUESTAS_INTERSECCION);
            }

        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/requisitosprevios/realizarInformeInspeccionViabilidad.jsf?pronunciamiento=" + tipoPronunciamiento);
    }

    public void uploadListener(FileUploadEvent event) {
//      setAdjuntoInformeInspeccion(JsfUtil.upload(event));
		habilitar = false;
		mostrarBotonFirmar = true;
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form");
  }

    public StreamedContent fileStreamPreguntasProponente() throws IOException {
        InputStream stream = new FileInputStream(respuestasProponente);
        DefaultStreamedContent content = new DefaultStreamedContent(stream, "application/pdf", respuestasProponente.getName());
        return content;
    }
}
