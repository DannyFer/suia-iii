package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class AsignarTecnicoAreaCAController implements Serializable {

    private static final long serialVersionUID = -3526371283333777322L;
    private static final Logger LOGGER = Logger
            .getLogger(AsignarTecnicoAreaCAController.class);
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private AreaFacade areaFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    
    @Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

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

    public void delegarTecnico(String tipoArea) {
    	try {
	        String usuario = "usuario_tecnico_"+tipoArea;//forestal o patrimonio
	        
	        String role = "role.responsable.pronunciamiento."+(tipoArea.equals("forestal")?"Forestal":"biodiversidad");
	        
	        Area areaTramite = proyectosBean.getProyecto().getAreaResponsable();
	        if(areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
		        switch (tipoArea) {
				case "forestal":
					areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
					break;
				case "patrimonio":
					areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS);
					break;
				default:
					break;
				}
	        }
        	
        	if(areaTramite == null) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return;
			}
        	
        	ProcessInstanceLog proces = procesoFacade.getProcessInstanceLog(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());

            if (usuario != null && !usuario.isEmpty()) {

                // buscar el rol del usuario y determinar cual debe asignar
                reasignarTareaComunBean.initFunctionOnNotStatartedTask(proces.getExternalId(),
                        bandejaTareasBean.getTarea().getProcessInstanceId(),
                        "Delegar a técnico para analizar la información..", usuario,
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
    	
    	TaskSummaryCustom taskSummaryCustom=bandejaTareasBean.getTarea();    	
		if (taskSummaryCustom == null|| taskSummaryCustom.getTaskSummary() == null || !taskSummaryCustom.getTaskSummary().getDescription().contains("/prevencion/certificadoambiental/asignarTecnico")) {
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} 
    }
}
