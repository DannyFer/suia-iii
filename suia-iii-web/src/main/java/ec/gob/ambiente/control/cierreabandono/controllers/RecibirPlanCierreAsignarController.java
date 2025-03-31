/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.cierreabandono.controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
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
@ManagedBean
public class RecibirPlanCierreAsignarController {

	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(RecibirPlanCierreAsignarController.class);

	@Getter
	@Setter
	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	private ReasignarTareaComunBean reasignarTareaComunBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	public void asignarTecnico() {
		final long processId = bandejaTareasBean.getProcessId();
		final long taskId = bandejaTareasBean.getTarea().getTaskId();
		reasignarTareaComunBean.initFunctionOnNotStatartedTask(processId, taskId, null,
				"Recibir plan de cierre y abandono y analizar información", "tecnicoControlProvincial",
				"tecnico", null, null, null);
	}

	public String cancelar() {
		return JsfUtil.actionNavigateToBandeja();
	}
}
