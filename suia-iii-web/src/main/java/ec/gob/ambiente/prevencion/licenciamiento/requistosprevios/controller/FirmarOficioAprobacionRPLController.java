/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.licenciamiento.requistosprevios.controller;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.prevencion.licenciamiento.requistosprevios.bean.FirmarOficioAprobacionBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 06/03/2015]
 *          </p>
 */
@ManagedBean
public class FirmarOficioAprobacionRPLController {

	private static final Logger LOGGER = Logger.getLogger(FirmarOficioAprobacionRPLController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@Setter
	@ManagedProperty(value = "#{firmarOficioAprobacionBean}")
	private FirmarOficioAprobacionBean firmarOficioAprobacionBean;

	public String aceptar() {
		try {
			procesoFacade
					.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(), JsfUtil.getCurrentProcessInstanceId(), null);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			return JsfUtil.actionNavigateTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
			LOGGER.error(e);
		}
		return null;
	}
}
