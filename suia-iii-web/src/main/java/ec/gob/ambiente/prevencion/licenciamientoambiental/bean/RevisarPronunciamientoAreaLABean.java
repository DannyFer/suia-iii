package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
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
import java.io.Serializable;
import java.util.Map;

@ManagedBean
@ViewScoped
public class RevisarPronunciamientoAreaLABean implements Serializable {

    private static final long serialVersionUID = 2975347857643455827L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(RevisarPronunciamientoAreaLABean.class);
    private final String clasePronunciamiento = "Pronunciamiento";
    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private PronunciamientoFacade pronunciamientoFacade;
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
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;
    @Getter
    @Setter
    private Boolean correcto;
    @Getter
    @Setter
    private String area;
    @Getter
    @Setter
    private String tipo;
    @Getter
    @Setter
    private Pronunciamiento pronunciamiento;

    @PostConstruct
    public void init() {
        correcto = true;
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        area = params.get("area");
        tipo = params.get("tipo");

        String areaEsp = area;
        if (tipo != null) {
            areaEsp += tipo;
        }
        Map<String, Object> variables = null;
        try {
            variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());
            Integer idProyecto = Integer.parseInt((String) variables
                    .get(Constantes.ID_PROYECTO));

            proyecto = proyectoLicenciaAmbientalFacade.getProyectoAreaPorId(idProyecto);

            pronunciamiento = pronunciamientoFacade.getPronunciamientosPorClaseTipo(clasePronunciamiento, Long.parseLong(proyecto.getId().toString()), areaEsp);


        } catch (JbpmException e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }


    }

}
