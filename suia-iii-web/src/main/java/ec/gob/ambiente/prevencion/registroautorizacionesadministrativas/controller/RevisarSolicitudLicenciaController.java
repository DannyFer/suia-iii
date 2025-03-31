package ec.gob.ambiente.prevencion.registroautorizacionesadministrativas.controller;

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
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Juan Carlos Gras
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Juan Carlos Gras, Fecha: 23/01/2015]
 *          </p>
 */

@RequestScoped
@ManagedBean
public class RevisarSolicitudLicenciaController {

	private static final Logger LOGGER = Logger
			.getLogger(RevisarSolicitudLicenciaController.class);

	@Getter
	@Setter
	private boolean observaciones;
	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	public String sendAction() {
		try {

			Map<String, Object> params = new ConcurrentHashMap<String, Object>();

			params.put("observaciones", this.observaciones);

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
			LOGGER.error("Error al enviar la información.", e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
		}
		return "";

	}

}
