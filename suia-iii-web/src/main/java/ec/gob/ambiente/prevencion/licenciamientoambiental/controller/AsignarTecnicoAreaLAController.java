package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.licenciamientoambiental.bean.AsignarTecnicoAreaLABean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.login.bean.LoginBean;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestScoped
@ManagedBean
public class AsignarTecnicoAreaLAController implements Serializable {

    private static final long serialVersionUID = -3526371283333777322L;
    private static final Logger LOGGER = Logger
            .getLogger(AsignarTecnicoAreaLAController.class);
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private AreaFacade areaFacade;

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
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{reasignarTareaComunBean}")
    private ReasignarTareaComunBean reasignarTareaComunBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{asignarTecnicoAreaLABean}")
    private AsignarTecnicoAreaLABean asignarTecnicoAreaLABean;

    private ProcessInstanceLog proces;

    public void delegarTecnico() {

        String usuario = asignarTecnicoAreaLABean.getUsuario();
        String role = "role.pc.tecnico.";
        String area = null;
        if (usuario == null || !usuario.isEmpty()) {
        	area = asignarTecnicoAreaLABean.getArea().replace("role.pc.coordinador.", "");
            if (area != null && !area.isEmpty()) {
                usuario = "u_Tecnico" + area;
                role += area;
            }
        }else {
            area = "General";
            usuario = "u_Tecnico" + area;
            role += area;
        }
        try {
        	Area areaTramite = proyectosBean.getProyecto().getAreaResponsable();
	        if(areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
		        switch (area) {
				case "Forestal":
					areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
					break;
				case "Biodiversidad":
					areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS);
					break;
				default:
					break;
				}
	        }
	        
            proces = procesoFacade.getProcessInstanceLog(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());

            if (usuario != null && !usuario.isEmpty()) {

                // buscar el rol del usuario y determinar cual debe asignar
                reasignarTareaComunBean.initFunctionOnNotStatartedTask(proces.getExternalId(),
                        bandejaTareasBean.getTarea().getProcessInstanceId(),
                        "Delegar a técnico para analizar y elaborar el pronunciamiento.", usuario,
//                        Constantes.getRoleAreaName(role), null,
                        // Busca los tecnicos de la misma area del coordinador
                        Constantes.getRoleAreaName(role), areaTramite.getAreaName(),
                        "/bandeja/bandejaTareas.jsf", new CompleteOperation() {

                            public Object endOperation(Object object) {
                                Map<String, Object> data = new ConcurrentHashMap<String, Object>();

                                try {
                                    procesoFacade.aprobarTarea(
                                            loginBean.getUsuario(),
                                            bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(),
                                            data);
                                } catch (JbpmException e) {
                                    LOGGER.error(e);
                                }
                                return null;
                            }
                        });
            } else {
                JsfUtil.addMessageError("Error al procesar la información. Regrese a la bandeja de tareas.");
            }
        } catch (JbpmException e) {
            JsfUtil.addMessageError("Error al procesar la información.");
            LOGGER.error(e);
        }
    }

    public void validarTareaBpm() {
        String url = "/prevencion/licenciamiento-ambiental/asignarTecnicoArea.jsf?area=" + asignarTecnicoAreaLABean.getArea();
        if(asignarTecnicoAreaLABean.getTipo()!=null&& !asignarTecnicoAreaLABean.getTipo().isEmpty()){
            url+= "&tipo="+asignarTecnicoAreaLABean.getTipo();
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }
}
