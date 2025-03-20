package ec.gob.ambiente.prevencion.tdr.controller;

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
import org.jfree.util.Log;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.bean.ElaborarInformeBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class ElaborarInformeController implements Serializable {
	private static final long serialVersionUID = -3526226L;

	private static final Logger LOGGER = Logger
			.getLogger(ElaborarInformeController.class);

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
	@ManagedProperty(value = "#{elaborarInformeBean}")
	private ElaborarInformeBean elaborarInformeBean;
	
	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private TdrFacade tdrFacade;
	
	public void guardarDatos() {
		try {
			tdrFacade.saveOrUpdate(elaborarInformeBean.getTdrEia());
			elaborarInformeBean.guardarInformeTecnico(elaborarInformeBean.getTdrInformeTecnico());
			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
		} catch (Exception e) {
			Log.error("Error al guardar datos en Informe: ", e);
		}
	}

	public String enviarDatos() {

		Map<String, Object> params = new ConcurrentHashMap<String, Object>();

		params.put("cumpleCriterios", elaborarInformeBean.isCumpleCriterios());

		try {
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
					.getTarea().getProcessInstanceId(), params);
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
