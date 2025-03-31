package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class AsignarEquipoMultidisciplinarioLABean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3795575879353061744L;
    private static final Logger LOGGER = Logger
            .getLogger(AsignarEquipoMultidisciplinarioLABean.class);

    @Setter
    @Getter
    private String tipo;

    @Getter
    @Setter
    private String[] areasSeleccionadas;

    @Getter
    @Setter
    private List<String> areas;

    @EJB
    private ProcesoFacade procesoFacade;


    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        areas = new ArrayList<String>();

        try {
            Map<String, String> params = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();
            tipo = params.get("tipo");


            Map<String, Object> variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());
            Integer idProyecto = Integer.parseInt((String) variables
                    .get(Constantes.ID_PROYECTO));

            ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciaAmbientalFacade.getProyectoAreaPorId(idProyecto);
            Area areaProyecto = proyecto.getAreaResponsable();

            if (areaProyecto.getTipoArea().getSiglas().equalsIgnoreCase("PC")) {

                areas.add("tecnicoSocial");
                areas.add("tecnicoCartografo");
                areas.add("tecnicoElectrico");
                areas.add("tecnicoMineria");
                areas.add("tecnicoOtrosSectores");
              /*  areas.add("tecnicoFisico");
                areas.add("tecnicoAmbiental");
                areas.add("tecnicoBiotico");*/
                areasSeleccionadas = new String[1];
//                areasSeleccionadas[0] = "tecnicoSocial";
            } else {
                areas.add("tecnicoGeneral");
            }
        } catch (Exception e) {

            JsfUtil.addMessageError("Error al obtener los datos del proyecto.");
            LOGGER.error("Error al obtener los datos del proyecto.", e);

        }
    }

    public void validarTareaBpm() {
        String url = "/prevencion/licenciamiento-ambiental/asignarEquipoTecnico.jsf";
        if (tipo != null && !tipo.isEmpty()) {
            url += "?tipo=" + tipo;
        }

        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }
}
