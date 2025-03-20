/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.composite.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaMultipleComunBean;
import ec.gob.ambiente.suia.comun.classes.VariableForRole;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 19/01/2015]
 *          </p>
 */
@ManagedBean
@RequestScoped
public class SolicitarPronunciamientoController implements Serializable {

	private static final long serialVersionUID = -3628140158494769416L;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private PronunciamientoFacade pronunciamientoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{reasignarTareaMultipleComunBean}")
	private ReasignarTareaMultipleComunBean reasignarTareaMultipleComunBean;

	private static final Logger LOG = Logger.getLogger(SolicitarPronunciamientoController.class);

	public void completarTareaRecibirSolicitudPronunciamientos() {
		final long idInstanciaProceso = bandejaTareasBean.getProcessId();
		final long idTarea = bandejaTareasBean.getTarea().getTaskId();

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
			List<String> usuariosEmitirPronunciamiento = new ArrayList<String>();

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Object endOperation(Object object) {
				usuarios = (Map<String, Object>) object;

				Iterator<Entry<String, Object>> it = usuarios.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry) it.next();
					usuariosAsolicitarPronunciamiento.add((Usuario) e.getValue());
					usuariosEmitirPronunciamiento.add(((Usuario) e.getValue()).getNombre());

					// System.out.println(e.getKey() + " " + e.getValue());
				}

				pronunciamientoFacade.solicitarPronunciamientos(usuariosAsolicitarPronunciamiento, idInstanciaProceso,
						loginBean.getUsuario());

				Map<String, Object> params = new HashMap<String, Object>();
				params.put("usuariosEmitirPronunciamiento_", usuariosEmitirPronunciamiento);

				// long idInstanciaProceso = bandejaTareasBean.getProcessId();

				try {

					/*
					 * procesoFacade.modificarVariablesProceso(idInstanciaProceso
					 * , params, loginBean.getNombreUsuario(),
					 * loginBean.getPassword());
					 */

					procesoFacade.aprobarTarea(loginBean.getUsuario(), idTarea, idInstanciaProceso, params);

				} catch (JbpmException e) {
					LOG.error("Error al iniciar el proceso", e);
				}
				return true;
			}
		};
		// Codigo para solicitar acompañamiento

		/*
		 * reasignarTareaMultipleComunBean.initFunctionOnNotStatartedTask(
		 * idInstanciaProceso, idTarea, null,
		 * "Revisar y analizar la documentacion ingresada",
		 * variablesFormUsersNamesAndRoles, null, completeOperation);
		 */

		reasignarTareaMultipleComunBean.initFunctionOnNotStatartedTask(idInstanciaProceso,
				"Solicitar Pronunciamientos", variablesFormUsersNamesAndRoles, null, completeOperation);

	}
}
