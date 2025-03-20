/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
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

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

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
public class DelegarTecnicoSolicitudController {

	private static final Logger LOGGER = Logger
			.getLogger(DelegarTecnicoSolicitudController.class);

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	@Setter
	private ReasignarTareaComunBean reasignarTareaComunBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	public void sendAction() {
		reasignarTareaComunBean.initFunctionOnNotStatartedTask(
				bandejaTareasBean.getTarea().getProcessInstanceId(),
				"Revisar solicitud y licencia.", "u_tecnico", "admin", null,
				"/bandeja/bandejaTareas.jsf", new CompleteOperation() {

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

}
