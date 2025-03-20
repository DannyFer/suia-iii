/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comun.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.AceptarRechazarPronunciamientosComunBean;
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
 *          [Autor: mayriliscs, Fecha: 14/01/2015]
 *          </p>
 */
@ManagedBean
@RequestScoped
public class AceptarRechazarPronunciamientosComunController {
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{aceptarRechazarPronunciamientosComunBean}")
	private AceptarRechazarPronunciamientosComunBean aceptarRechazarPronunciamientosComunBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private PronunciamientoFacade pronunciamientoFacade;

	private static final Logger LOG = Logger.getLogger(AceptarRechazarPronunciamientosComunController.class);

	public void completarTareaAceptarPronunciamientos() {
		long idTarea = bandejaTareasBean.getTarea().getTaskId();
		long idInstanciaProceso = bandejaTareasBean.getProcessId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("aceptaPronunciamientos", true);

		try {
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), idInstanciaProceso, params);

			procesoFacade.aprobarTarea(loginBean.getUsuario(), idTarea, idInstanciaProceso, null);
		} catch (JbpmException e) {
			LOG.error("Error al iniciar el proceso", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}

		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}

	public void completarTareaRechazarPronunciamientos() {
		long idTarea = bandejaTareasBean.getTarea().getTaskId();
		long idInstanciaProceso = bandejaTareasBean.getProcessId();
		List<String> usuariosCorregirPronunciamiento = new ArrayList<String>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("aceptaPronunciamientos", false);

		for (int i = 0; i < aceptarRechazarPronunciamientosComunBean.getPronunciamientos().size(); i++) {
			if (!aceptarRechazarPronunciamientosComunBean.getPronunciamientos().get(i).isAprobado())
				usuariosCorregirPronunciamiento.add(aceptarRechazarPronunciamientosComunBean.getPronunciamientos()
						.get(i).getUsuario().getNombre());
		}
		params.put("usuariosEmitirPronunciamiento", usuariosCorregirPronunciamiento);

		try {
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), idInstanciaProceso, params);

			procesoFacade.aprobarTarea(loginBean.getUsuario(), idTarea, idInstanciaProceso, null);
		} catch (JbpmException e) {
			LOG.error("Error al iniciar el proceso", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}

		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}

}
