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
public class RecibirPlanCierreController {

	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(RecibirPlanCierreController.class);

	@Getter
	@Setter
	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	private ReasignarTareaComunBean reasignarTareaComunBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	public String aceptar() {
		JsfUtil.addMessageInfo("Cambios aplicados");
		return JsfUtil.actionNavigateToBandeja();
	}

	public String cancelar() {
		return JsfUtil.actionNavigateToBandeja();
	}
}
