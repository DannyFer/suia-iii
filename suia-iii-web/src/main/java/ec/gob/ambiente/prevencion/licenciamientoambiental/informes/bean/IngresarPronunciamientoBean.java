package ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ManagedBean
@ViewScoped
public class IngresarPronunciamientoBean implements Serializable {

    private static final long serialVersionUID = 165683211928358047L;

    private final Logger LOG = Logger.getLogger(IngresarPronunciamientoBean.class);

    @Getter
    @Setter
    private File oficioPdf;
    @Getter
    @Setter
    private byte[] archivoInforme;
    @Getter
    @Setter
    private String informePath;
    @Getter
    @Setter
    private Pronunciamiento pronunciamiento;

    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @EJB
    private PronunciamientoFacade pronunciamientoFacade;

    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

    @EJB
    private AreaFacade areaFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private PersonaFacade personaFacade;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @ManagedProperty(value = "#{loginBean}")
    @Getter
    @Setter
    private LoginBean loginBean;


    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @PostConstruct
    public void init() {
        try {

            // estudioImpactoAmbiental =
            // estudioImpactoAmbientalFacade.obtenerPorProyectoTipo(proyectosBean.getProyecto(),
            // "FINAL");
            estudioImpactoAmbiental = new EstudioImpactoAmbiental();
            estudioImpactoAmbiental.setId(400);

            if (pronunciamiento == null) {
                pronunciamiento = new Pronunciamiento();
            }

        } catch (Exception e) {
            LOG.error("Error al inicializar: InformeTecnicoGirsBean: ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
    }

    public void guardar() {
        try {


            JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/ingresarPronunciamiento.jsf");
        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
        }
    }

    public String completarTarea() {
        try {

            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), data);

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (Exception e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }

    public void validarTareaBpm() {
        String url = "/prevencion/licenciamiento-ambiental/ingresarPronunciamiento.jsf";
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

}
