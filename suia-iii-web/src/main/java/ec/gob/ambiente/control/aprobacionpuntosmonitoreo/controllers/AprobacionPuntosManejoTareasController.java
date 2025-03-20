/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.aprobacionpuntosmonitoreo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.aprobacionpuntosmonitoreo.bean.PuntosMonitoreoBean;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.EmitirPronunciamientoComunBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaMultipleComunBean;
import ec.gob.ambiente.suia.comun.classes.VariableForRole;
import ec.gob.ambiente.suia.control.aprobacionpuntosmonitoreo.facade.AprobacionPuntosMonitoreoFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 07/01/2015]
 *          </p>
 */
@RequestScoped
@ManagedBean
public class AprobacionPuntosManejoTareasController {

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private AprobacionPuntosMonitoreoFacade aprobacionPuntosMonitoreoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{puntosMonitoreoBean}")
	private PuntosMonitoreoBean puntosMonitoreoBean;

	@EJB
	private PronunciamientoFacade pronunciamientoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	private ReasignarTareaComunBean reasignarTareaComunBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{reasignarTareaMultipleComunBean}")
	private ReasignarTareaMultipleComunBean reasignarTareaMultipleComunBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{emitirPronunciamientoComunBean}")
	private EmitirPronunciamientoComunBean emitirPronunciamientoComunBean;

	private static final Logger LOG = Logger.getLogger(AprobacionPuntosManejoTareasController.class);

	public void completarTareaAdjuntarComprobantePago() {
		long idTarea = bandejaTareasBean.getTarea().getTaskId();
		try {
			procesoFacade.aprobarTarea(loginBean.getUsuario(), idTarea, bandejaTareasBean.getTarea().getProcessInstanceId(), null);
		} catch (JbpmException e) {
			LOG.error("Error al iniciar el proceso", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}

		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}

	public void completarTareaRecibirSolicitudAprobacionPMAsignarTecnico() {
		final long idInstanciaProceso = bandejaTareasBean.getProcessId();
		long idTarea = bandejaTareasBean.getTarea().getTaskId();

		Map<String, String> variablesForUsersNames = new ConcurrentHashMap<String, String>();
		variablesForUsersNames.put("tecnicoca", "Técnico de Control Ambiental");
		variablesForUsersNames.put("tecnicocartografico", "Técnico Cartógrafo");

		Map<String, VariableForRole> variablesFormUsersNamesAndRoles = new ConcurrentHashMap<String, VariableForRole>();

		variablesFormUsersNamesAndRoles.put("tecnicoca", new VariableForRole("tecnicoca", null,
				"Técnico de Control Ambiental"));
		variablesFormUsersNamesAndRoles.put("tecnicocartografico", new VariableForRole("tecnicocartografico", null,
				"Técnico Cartógrafo"));

		// Codigo para solicitar acompañamiento
		CompleteOperation completeOperation = new CompleteOperation() {
			Map<String, Object> usuarios;
			List<Usuario> usuariosAsolicitarPronunciamiento = new ArrayList<Usuario>();

			@SuppressWarnings("unchecked")
			@Override
			public Object endOperation(Object object) {
				usuarios = (Map<String, Object>) object;
				Usuario tecnico = (Usuario) usuarios.get("tecnicocartografico");
				usuariosAsolicitarPronunciamiento.add(tecnico);
				pronunciamientoFacade.solicitarPronunciamientos(usuariosAsolicitarPronunciamiento, idInstanciaProceso,
						loginBean.getUsuario());
				return true;
			}
		};
		// Codigo para solicitar acompañamiento

		reasignarTareaMultipleComunBean.initFunctionOnNotStatartedTask(idInstanciaProceso, idTarea, null,
				"Revisar y analizar la documentacion ingresada", variablesFormUsersNamesAndRoles, null,
				completeOperation);

	}

	public void completarTareaAnalizarUbicacionPuntosMonitoreo() {
		long idTarea = bandejaTareasBean.getTarea().getTaskId();
		long idInstaciaProceso = bandejaTareasBean.getProcessId();
		Usuario usuario = loginBean.getUsuario();

		 emitirPronunciamientoComunBean.initFunction(idTarea,
		 idInstaciaProceso, usuario, null, null, null);

		/*try {
			procesoFacade.aprobarTarea(loginBean.getNombreUsuario(), idTarea, null, Constantes.getDeploymentId(),
					loginBean.getPassword(), Constantes.URL_BUSINESS_CENTRAL);
		} catch (JbpmException e) {
			LOG.error("Error al iniciar el proceso", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}

		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");*/
	}

	public void completarTareaGenerico() {
		long idTarea = bandejaTareasBean.getTarea().getTaskId();
		try {
			procesoFacade.aprobarTarea(loginBean.getUsuario(), idTarea, bandejaTareasBean.getTarea().getProcessInstanceId(), null);
		} catch (JbpmException e) {
			LOG.error("Error al iniciar el proceso", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}

		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}
}
