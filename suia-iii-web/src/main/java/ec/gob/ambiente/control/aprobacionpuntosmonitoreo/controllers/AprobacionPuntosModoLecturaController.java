/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.aprobacionpuntosmonitoreo.controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.control.aprobacionpuntosmonitoreo.bean.PuntosMonitoreoModoLecturaBean;
import ec.gob.ambiente.suia.domain.PuntoDescargaLiquida;
import ec.gob.ambiente.suia.domain.PuntoEmision;
import ec.gob.ambiente.suia.domain.PuntoInmision;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 19/12/2014]
 *          </p>
 */
@RequestScoped
@ManagedBean
public class AprobacionPuntosModoLecturaController {

	@Getter
	@Setter
	@ManagedProperty(value = "#{puntosMonitoreoModoLecturaBean}")
	private PuntosMonitoreoModoLecturaBean puntosMonitoreoModoLecturaBean;

	public void editarPuntoDescargaLiquidaModoLectura(PuntoDescargaLiquida punto) {
		puntosMonitoreoModoLecturaBean.setVisualizarDetallePuntosDescargaLiquida(true);
		puntosMonitoreoModoLecturaBean.setPuntoDescargaLiquida(punto);
	}

	public void editarPuntoEmisionAtmosferaModoLectura(PuntoEmision punto) {
		puntosMonitoreoModoLecturaBean.setVisualizarDetallePuntosEmision(true);
		puntosMonitoreoModoLecturaBean.setPuntoEmision(punto);
	}

	public void editarPuntoInmisionModoLectura(PuntoInmision punto) {
		puntosMonitoreoModoLecturaBean.setVisualizarDetallePuntosInmision(true);
		puntosMonitoreoModoLecturaBean.setPuntoInmision(punto);
	}
}
