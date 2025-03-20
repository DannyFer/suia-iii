package ec.gob.ambiente.prevencion.tdr.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaMultipleComunBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;

@RequestScoped
@ManagedBean
public class FirmarPronunciamientoObservacionController implements Serializable {
	private static final long serialVersionUID = -352222222888882286L;

	private static final Logger LOGGER = Logger
			.getLogger(FirmarPronunciamientoObservacionController.class);
	@EJB
	private ProcesoFacade procesoFacade;

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
	@ManagedProperty(value = "#{reasignarTareaMultipleComunBean}")
	private ReasignarTareaMultipleComunBean reasignarTareaMultipleComunBean;

	public String iniciarTarea() {

		try {

			Map<String, Object> data = new ConcurrentHashMap<String, Object>();
			taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
					bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data,
					loginBean.getPassword(),
					Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOGGER.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
		return "";
		//
		// reasignarTareaMultipleComunBean.initFunctionOnNotStatartedTask(
		// bandejaTareasBean.getTarea().getProcessInstanceId(),
		// "Asignar equipo multidiciplinario",
		// variablesFormUsersNamesAndRoles, null, new CompleteOperation() {
		//
		// public Object endOperation(Object object) {
		// Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		//
		// try {
		// taskBeanFacade.approveTask(
		// loginBean.getNombreUsuario(),
		// bandejaTareasBean.getTarea().getTaskId(), data,
		// Constantes.getDeploymentId(),
		// loginBean.getPassword(),
		// Constantes.URL_BUSINESS_CENTRAL);
		// } catch (JbpmException e) {
		// LOGGER.error(e);
		// }
		// return null;
		// }
		// });
	}
}
