package ec.gob.ambiente.prevencion.viabilidadtecnica.controller;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
@ManagedBean
public class AsignarTecnicoResposableVTController implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3259687492908394088L;
    private static final Logger LOGGER = Logger
            .getLogger(AsignarTecnicoResposableVTController.class);
    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private AreaFacade areaFacade;

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

    @Getter
    @Setter
    @ManagedProperty(value = "#{reasignarTareaComunBean}")
    private ReasignarTareaComunBean reasignarTareaComunBean;

    private ProyectoLicenciamientoAmbiental proyecto;

    private ProcessInstanceLog proces;
    private String roleType;
    private String subarea;

    public void delegarTecnico() {
        InicializarDatos();
        reasignarTareaComunBean
                .initFunctionOnNotStatartedTask(proces.getExternalId(),
                        bandejaTareasBean.getTarea().getProcessInstanceId(),
                        "Asignar tecnico responsable.",
                        "u_TecnicoResponsable", Constantes.getRoleAreaName(roleType),
                        subarea, "/bandeja/bandejaTareas.jsf",
                        new CompleteOperation() {

                            public Object endOperation(Object object) {
                                Map<String, Object> data = new HashMap<String, Object>();
                                try {
                                    procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),
                                             bandejaTareasBean.getProcessId(), data);
                                } catch (JbpmException e1) {
                                    LOGGER.error(e1);
                                }
                                return null;
                            }
                        });
    }

    /**
     * Inicializar los datos para la reasignación de usuario.
     */
    public void InicializarDatos() {
        Map<String, Object> variables;
        subarea = null;
        try {
            variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
                    bandejaTareasBean.getTarea().getProcessInstanceId());
            Integer idProyecto = Integer.parseInt((String) variables
                    .get(Constantes.ID_PROYECTO));

            proyecto = proyectoLicenciaAmbientalFacade.getProyectoAreaPorId(idProyecto);
            roleType = "role.area.tecnico";

            if (!proyecto.getAreaResponsable().getTipoArea().getSiglas()
                    .equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {

            }else {
                roleType = "role.pc.tecnico.area." + proyecto.getTipoSector().getId().toString();
            }
            subarea = proyecto.getAreaResponsable().getAreaName();

            proces = procesoFacade.getProcessInstanceLog(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
        } catch (JbpmException e) {
            LOGGER.error("Error al recuperar los datos del proyecto.", e);
        } catch (Exception e) {
            LOGGER.error("Error al recuperar los datos del proyecto.", e);
        }
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/emisionviabilidadtecnica/asignarTecnicoResponsable.jsf");
    }
}
