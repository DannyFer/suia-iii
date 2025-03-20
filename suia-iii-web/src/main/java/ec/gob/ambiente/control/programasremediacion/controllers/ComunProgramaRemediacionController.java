/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.programasremediacion.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.programasremediacion.bean.ComunProgramaRemediacionBean;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.comun.bean.SolicitarApoyoComunBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 23/12/2014]
 *          </p>
 */
@RequestScoped
@ManagedBean
public class ComunProgramaRemediacionController {

	private static final Logger LOGGER = Logger
			.getLogger(ComunProgramaRemediacionController.class);

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	@Setter
	private ReasignarTareaComunBean reasignarTareaComunBean;

	@ManagedProperty(value = "#{solicitarApoyoComunBean}")
	@Setter
	private SolicitarApoyoComunBean solicitarApoyoComunBean;

	@ManagedProperty(value = "#{comunProgramaRemediacionBean}")
	@Setter
	private ComunProgramaRemediacionBean comunProgramaRemediacionBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	public void sendAction() {

	}

	public String elaborarInformeOficio() {
		// observaciones_programa boolean
		//
		List<String> lista;
		lista = new ArrayList<String>();
		lista.add("hola");
		lista.add(" frank");
		try {
			Map<String, Object> params = new ConcurrentHashMap<String, Object>();
			params.put("observaciones_programa",
					comunProgramaRemediacionBean.getObservaciones());
			// params.put("u_coordinador", "msit");
			// params.put("mensaje_director_seguimiento",
			// "El Sujeto de Control "
			// + loginBean.getNombreUsuario() + " " + " ingresó al proyecto '"
			// + proyecto.getNombre() + "' en la fecha " + dt1.format(fecha)
			// + " un programa de remediación.");
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
			LOGGER.error(
					"Error al iniciar la tarea de identificar proyecto del programa de remediación",
					e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información, intente mas tarde.");
		}
		return "";

	}

	public String revisarOficioCoordinador() {
		// observaciones_programa boolean
		//
		try {
			Map<String, Object> params = new ConcurrentHashMap<String, Object>();

			params.put("informe_oficio_correcto",
					comunProgramaRemediacionBean.getObservaciones());
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
			LOGGER.error(
					"Error al iniciar la tarea de identificar proyecto del programa de remediación",
					e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información, intente mas tarde.");
		}
		return "";

	}

	public void recibirInformeAsignarTecnico(String usuario) {

		JsfUtil.addMessageInfo(usuario);
		reasignarTareaComunBean.initFunctionOnNotStatartedTask(
				bandejaTareasBean.getTarea().getProcessInstanceId(),
				"Elaborar criterio tecnico y remitir a Tecnico de Control",
				usuario, "admin", null, null, new CompleteOperation() {

					public Object endOperation(Object object) {
						Map<String, Object> data = new ConcurrentHashMap<String, Object>();

						try {
							taskBeanFacade.approveTask(
									loginBean.getNombreUsuario(),
									bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data,
									loginBean.getPassword(),
									Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
						} catch (JbpmException e) {
							LOGGER.error(e);
						}
						return null;
					}
				});

	}

	public String elaborarCriterioTecnico() {
		// observaciones_programa boolean
		//
		try {
			Map<String, Object> params = new ConcurrentHashMap<String, Object>();
			// params.put("informe_oficio_correcto", false);
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
			LOGGER.error(
					"Error al iniciar la tarea de Elaborar criterio tecnico y remitir a Tecnico de Control CC a su .coordinador",
					e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información, intente mas tarde.");
		}
		return "";

	}

	public String revisarOficioDirector() {

		try {
			Map<String, Object> params = new ConcurrentHashMap<String, Object>();

			params.put("informe_oficio_correcto",
					comunProgramaRemediacionBean.getObservaciones());
			params.put("direccion_provincial",
					comunProgramaRemediacionBean.getObservaciones());
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
			LOGGER.error(
					"Error al iniciar la tarea de identificar proyecto del programa de remediación",
					e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información, intente mas tarde.");
		}
		return "";
	}

	public String consolidarCriterios() {

		try {
			Map<String, Object> params = new ConcurrentHashMap<String, Object>();
			params.put("modificaciones_criterios_tecnicos",
					comunProgramaRemediacionBean.getObservaciones());
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
			LOGGER.error(
					"Error al iniciar la tarea de identificar proyecto del programa de remediación",
					e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información, intente mas tarde.");
		}
		return "";
	}

	public String archivarInformeOficioDirector() {

		try {
			Map<String, Object> params = new ConcurrentHashMap<String, Object>();
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
			LOGGER.error(
					"Error al iniciar la tarea de Archivar informe tecnico y firmar oficio para envia a Sujeto de Control",
					e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información, intente mas tarde.");
		}
		return "";
	}

	public String enviarRespuestaSujetoC() {
		return archivarInformeOficioDirector();
	}

	public void registroProyecto() {

		JsfUtil.addMessageInfo("GG");
	}
}
