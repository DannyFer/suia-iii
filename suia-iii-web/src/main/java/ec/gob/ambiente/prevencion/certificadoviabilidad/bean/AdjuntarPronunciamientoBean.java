package ec.gob.ambiente.prevencion.certificadoviabilidad.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoViabilidadAmbientalInterseccionProyectoFacade;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AdjuntarPronunciamientoBean implements Serializable {

	private static final long serialVersionUID = -4920792922839127645L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AdjuntarPronunciamientoBean.class);

    @Setter
    @Getter
    private File respuestasProponente;

    @Setter
    @Getter
    private Boolean descargado = false;

	@Setter
	@Getter
	private File adjuntoPronunciamiento;

    @Setter
    @Getter
    private Boolean esPronunciamientoFavorable;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private CertificadoViabilidadAmbientalInterseccionProyectoFacade certificadoViabilidadFacade;

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
        try {
            Map<String, Object> variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());

            Integer idProyecto = Integer.parseInt((String) variables.get(Constantes.ID_PROYECTO));
            proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);

            File respuestasProponenteDoc = certificadoViabilidadFacade.descargarRespuestas(proyecto.getId(),
                    TipoDocumentoSistema.PREGUNTAS_RESPUESTAS_INTERSECCION);
            if(respuestasProponenteDoc != null){
                respuestasProponente = respuestasProponenteDoc;
            }

        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }
    }

    public void uploadListener(FileUploadEvent event) {
        setAdjuntoPronunciamiento(JsfUtil.upload(event));
    }

    public StreamedContent fileStreamPreguntasProponente() throws IOException {
        InputStream stream = new FileInputStream(respuestasProponente);
        DefaultStreamedContent content = new DefaultStreamedContent(stream, "application/pdf", respuestasProponente.getName());
        descargado = true;
        return content;
    }
}
