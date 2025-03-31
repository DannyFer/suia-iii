package ec.gob.ambiente.prevencion.tdr.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ResourcesUtil;

@RequestScoped
@ManagedBean
public class AsignarTecnicoTDRController implements Serializable {

	private static final long serialVersionUID = -35263712833L;
	private static final Logger LOGGER = Logger
			.getLogger(AsignarTecnicoTDRController.class);
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
	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	private ReasignarTareaComunBean reasignarTareaComunBean;

	public void delegarTecnico() {

		Map<String, Object> variables;
		try {
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
					bandejaTareasBean.getTarea().getProcessInstanceId());
			String tipoAreaProyecto = (String) variables
					.get("tipoAreaProyecto");
			String roleType = "role.pc.tecnico";
			String subarea = null;
			if (!tipoAreaProyecto
					.equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
				roleType = "role.area.tecnico";
				Integer areaProyectoId = Integer.parseInt((String) variables
						.get("areaProyectoId"));
				Area areaProyecto = areaFacade.getArea(areaProyectoId);
				subarea = areaProyecto.getAreaAbbreviation(); 
			}

			reasignarTareaComunBean.initFunctionOnNotStatartedTask(
					bandejaTareasBean.getTarea().getProcessInstanceId(),
					"Delegar a Tecnico para Analizar TDRs.",
					"u_TecnicoResponsable",
					ResourcesUtil.getRoleAreaName(roleType), subarea,
					"/bandeja/bandejaTareas.jsf", new CompleteOperation() {

						public Object endOperation(Object object) {
							Map<String, Object> data = new HashMap<String, Object>();

							try {
								taskBeanFacade.approveTask(
										loginBean.getNombreUsuario(),
										bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(),
										data, 
										loginBean.getPassword(),
										Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
							} catch (JbpmException e1) {
								LOGGER.error(e1);
							}
							return null;
						}
					});
		} catch (JbpmException e) {
			LOGGER.error("Error al recuperar los datos del proyecto.", e);
		}
	}
}
