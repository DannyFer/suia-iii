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

import ec.gob.ambiente.control.programasremediacion.bean.DeterminarRequerirCriteriosBean;
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
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 23/12/2014]
 *          </p>
 */
@RequestScoped
@ManagedBean
public class DeterminarRequerirCriteriosController {

	private static final Logger LOGGER = Logger
			.getLogger(DeterminarRequerirCriteriosController.class);

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@ManagedProperty(value = "#{determinarRequerirCriteriosBean}")
	@Setter
	private DeterminarRequerirCriteriosBean determinarRequerirCriteriosBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	public void initIngresarPlanCierreAction() {
		// JsfUtil.addMessageInfo(Long.toString(bandejaTareasBean.getTarea()
		// .getProcessInstanceId()) + " ");
		// tecnicoProvincial

		// solicitarApoyoComunBean.initFunction("Requiere acompañamiento", null,
		// null, new CompleteOperation() {
		//
		// @Override
		// public Object endOperation(Object object) {
		// // 
		// @SuppressWarnings("unchecked")
		// List<String> l = (List<String>) object;
		// System.out.println(Integer.toString(l.size()));
		// JsfUtil.addMessageInfo(".........");
		// return null;
		// }
		// });

		// reasignarTareaComunBean.initFunctionOnNotStatartedTask(
		// bandejaTareasBean.getTarea().getProcessInstanceId(),
		// "Recibir programa de remediación.", "u_tecnico_ca", "admin",
		// null, "/bandeja/bandejaTareas.jsf", new CompleteOperation() {
		//
		// public Object endOperation(Object object) {
		// Map<String, Object> data = new HashMap<String, Object>();
		//
		// try {
		// taskBeanFacade.approveTask(
		// loginBean.getNombreUsuario(),
		// bandejaTareasBean.getTarea().getTaskId(), data,
		// Constantes.getDeploymentId(),
		// loginBean.getPassword(),
		// Constantes.URL_BUSINESS_CENTRAL);
		// } catch (JbpmException e) {
		// // 
		// e.printStackTrace();
		// }
		// 
		// return null;
		// }
		// });
	}

	public String enviarDatos() {
		Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		data.put("revision_tecnicos_pn", false);
		data.put("revision_tecnicos_ca", false);
		data.put("revision_tecnicos_pras", false);
		data.put("revision_tecnicos_extras", false);
		if (determinarRequerirCriteriosBean.getRequiereApoyo()) {
			if (determinarRequerirCriteriosBean.getListaApoyo().length == 0) {
				JsfUtil.addMessageError("Debe seleccionar al menos un área.");
				return "";
			} else {
				// this.areasDisponibles =
				// "Calidad Amiental, Patrimonio Provincial,PRAS";

				data.put("revision_tecnicos_extras", true);
				for (int i = 0; i < determinarRequerirCriteriosBean
						.getListaApoyo().length; i++) {
					if (determinarRequerirCriteriosBean.getListaApoyo()[i]
							.trim().equals("Patrimonio Provincial")) {
						data.put("revision_tecnicos_pn", true);

					} else if (determinarRequerirCriteriosBean.getListaApoyo()[i]
							.trim().equals("Calidad Amiental")) {
						data.put("revision_tecnicos_ca", true);
					} else if (determinarRequerirCriteriosBean.getListaApoyo()[i]
							.trim().equals("PRAS")) {
						data.put("revision_tecnicos_pras", true);
					}

				}
			}
		}

		try {
			List<String> cronograma = new ArrayList<String>();
			cronograma.add("GGGGGG");
			cronograma.add("WWWWWWWW");
			cronograma.add("ZZZZZZZZ");
			data.put("cronogramaA", "aa,bb,vv");

			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
					.getTarea().getProcessInstanceId(), data);

			Map<String, Object> dataT = new ConcurrentHashMap<String, Object>();
			taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
					bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), dataT,
					loginBean.getPassword(),
					Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

			JsfUtil.addMessageInfo("Se realizó correctamente la operación..");
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOGGER.error(e);
		}
		return "";
		// revision_tecnicos_extras , revision_tecnicos_ca,
		// revision_tecnicos_pn, revision_tecnicos_pras

	}
}
