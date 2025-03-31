package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

@ManagedBean
@ViewScoped
public class AdjuntarOficioViabilidadBean implements Serializable {

    private static final long serialVersionUID = 2975076547923496247L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(AdjuntarOficioViabilidadBean.class);

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    private File oficio;

    @Getter
    @Setter
    private boolean aprobacionFavorable = false;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    @Getter
    @Setter
    private boolean subido = false;

    @PostConstruct
    public void init() {
        try {
            Map<String, Object> variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());

            Integer idProyecto = Integer.parseInt((String) variables.get(Constantes.ID_PROYECTO));
            proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }
    }

    public void adjuntarOficio(FileUploadEvent event) {
        if (event != null) {
            oficio = JsfUtil.upload(event);
            subido = true;
            JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " fue adjuntado correctamente.");
        }
    }
    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/requisitosprevios/adjuntarOficioViabilidad.jsf");
    }
}

