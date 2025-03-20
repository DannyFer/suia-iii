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

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.bean.AsignarTecnicoAreaTRDBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.ResourcesUtil;

@RequestScoped
@ManagedBean
public class AsignarTecnicoAreaTRDController implements Serializable {

	private static final long serialVersionUID = -3526371283333777322L;
	private static final Logger LOGGER = Logger
			.getLogger(AsignarTecnicoAreaTRDController.class);
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
	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	private ReasignarTareaComunBean reasignarTareaComunBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{asignarTecnicoAreaTRDBean}")
	private AsignarTecnicoAreaTRDBean asignarTecnicoAreaTRDBean;

	public void delegarTecnico() {

		String usuario = asignarTecnicoAreaTRDBean.getUsuario();
		String role = "role.pc.coordinador.";
		if (usuario == null || !usuario.isEmpty()) {
			usuario = "";
			String area = asignarTecnicoAreaTRDBean.getArea();
			if (area != null && !area.isEmpty()) {
				usuario = "u_Tecnico" + area;
				role += area;
			}
			// if (usuario != null && !usuario.isEmpty()) {
			// usuario = "u_TecnicoArea";
			// }
		}

		if (usuario != null && !usuario.isEmpty()) {

			// buscar el rol del usuario y determinar cual debe asignar

			reasignarTareaComunBean.initFunctionOnNotStatartedTask(
					bandejaTareasBean.getTarea().getProcessInstanceId(),
					"Delegar a Tecnico para Analizar TDRs.", usuario,
					ResourcesUtil.getRoleAreaName(role), null,
					"/bandeja/bandejaTareas.jsf", new CompleteOperation() {

						public Object endOperation(Object object) {
							Map<String, Object> data = new ConcurrentHashMap<String, Object>();

							try {
								taskBeanFacade.approveTask(
										loginBean.getNombreUsuario(),
										bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(),
										data,
										loginBean.getPassword(),
										Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
							} catch (JbpmException e) {
								LOGGER.error(e);
							}
							return null;
						}
					});
		} else {
			JsfUtil.addMessageError("Error al procesar la información. Regrese a la bandeja de tareas.");
		}
	}
}
