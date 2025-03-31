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

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import java.io.Serializable;

@RequestScoped
@ManagedBean
public class AsignarTecnicoBiodiversidadTRDController implements Serializable {

	private static final long serialVersionUID = -352637128333388883L;
	private static final Logger LOGGER = Logger
			.getLogger(AsignarTecnicoBiodiversidadTRDController.class);
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

	public void delegarTecnico() {
		reasignarTareaComunBean.initFunctionOnNotStatartedTask(
				bandejaTareasBean.getTarea().getProcessInstanceId(),
				"Delegar a Tecnico para Analizar TDRs.",
				"u_TecnicoBiodiversidad", "admin", null,
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
	}
}
