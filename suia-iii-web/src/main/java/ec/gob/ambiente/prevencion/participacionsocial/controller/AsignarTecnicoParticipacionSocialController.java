package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.ResourcesUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.io.Serializable;
import java.util.Map;

@RequestScoped
@ManagedBean
public class AsignarTecnicoParticipacionSocialController implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3257750492908394088L;
    private static final Logger LOGGER = Logger
            .getLogger(AsignarTecnicoParticipacionSocialController.class);
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


    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    @Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{reasignarTareaComunBean}")
    private ReasignarTareaComunBean reasignarTareaComunBean;
    private String roleType;
    private String subarea;

    public void delegarTecnico() {
        InicializarDatos();
        reasignarTareaComunBean
                .initFunctionOnNotStatartedTask(
                        bandejaTareasBean.getTarea().getProcessInstanceId(),
                        "Delegar a técnico responsable para Verificar Información de Participación Social",
                        "u_Tecnico", ResourcesUtil.getRoleAreaName(roleType),
                        subarea, "/bandeja/bandejaTareas.jsf", null
                       /* new CompleteOperation() {

                            public Object endOperation(Object object) {
                                Map<String, Object> data = new HashMap<String, Object>();
                                try {
                                     procesoFacade.aprobarTarea(loginBean.getUsuario(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(),data);
                   } catch (JbpmException e1) {
                                    LOGGER.error(e1);
                                }
                                return null;
                            }
                        }*/);
    }

    /**
     * Inicializar los datos para la reasignación de usuario.
     */
    public void InicializarDatos() {
        Map<String, Object> variables;
        subarea = proyectosBean.getProyecto().getAreaResponsable().getAreaName();
        try {
            variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
                    bandejaTareasBean.getTarea().getProcessInstanceId());
            String tipoAreaProyecto = (String) variables
                    .get("tipoAreaProyecto");
            roleType = "role.pc.tecnico.Social";

            if (!tipoAreaProyecto
                    .equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
                roleType = "role.pc.tecnico.Social";
            }
            ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorCodigo((String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE));
            if(proyecto.getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().getId() ==2)
            	roleType = "role.pc.tecnico.Social.mineria";
    		
            
        } catch (JbpmException e) {
            LOGGER.error("Error al recuperar los datos del proyecto.", e);
        }
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/participacionsocial/asignarTecnico.jsf");
    }

}
