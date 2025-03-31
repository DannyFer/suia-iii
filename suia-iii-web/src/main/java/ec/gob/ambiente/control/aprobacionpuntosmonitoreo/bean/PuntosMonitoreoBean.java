/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.aprobacionpuntosmonitoreo.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.control.aprobacionpuntosmonitoreo.facade.AprobacionPuntosMonitoreoFacade;
import ec.gob.ambiente.suia.domain.HusoHorario;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.PuntoDescargaLiquida;
import ec.gob.ambiente.suia.domain.PuntoEmision;
import ec.gob.ambiente.suia.domain.PuntoInmision;
import ec.gob.ambiente.suia.domain.TipoCombustible;
import ec.gob.ambiente.suia.domain.TipoFuente;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 29/12/2014]
 *          </p>
 */
@ViewScoped
@ManagedBean
public class PuntosMonitoreoBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4018465067920506198L;
	private PuntoDescargaLiquida puntoDescargaLiquida;
	private List<PuntoDescargaLiquida> puntosDescargaLiquida;
	private List<PuntoDescargaLiquida> puntosDescargaLiquidaFiltrados;
	private List<HusoHorario> husosHorarios;

	private PuntoEmision puntoEmision;
	private List<PuntoEmision> puntosEmision;
	private List<PuntoEmision> puntosEmisionFiltrados;
	private List<TipoCombustible> tiposCombustibles;
	private List<TipoFuente> tiposFuentes;

	private PuntoInmision puntoInmision;
	private List<PuntoInmision> puntosInmision;
	private List<PuntoInmision> puntosInmisionFiltrados;

	private ProyectoLicenciamientoAmbiental proyecto;
	private List<Integer> puntosAPagar;

	@EJB
	private AprobacionPuntosMonitoreoFacade aprobacionPuntosMonitoreoFacade;

	public PuntoDescargaLiquida getPuntoDescargaLiquida() {
		return puntoDescargaLiquida == null ? puntoDescargaLiquida = new PuntoDescargaLiquida() : puntoDescargaLiquida;
	}

	public void setPuntoDescargaLiquida(PuntoDescargaLiquida puntoDescargaLiquida) {
		this.puntoDescargaLiquida = puntoDescargaLiquida;
	}

	public PuntoEmision getPuntoEmision() {
		return puntoEmision == null ? puntoEmision = new PuntoEmision() : puntoEmision;
	}

	public void setPuntoEmision(PuntoEmision puntoEmision) {
		this.puntoEmision = puntoEmision;
	}

	public PuntoInmision getPuntoInmision() {
		return puntoInmision == null ? puntoInmision = new PuntoInmision() : puntoInmision;
	}

	public void setPuntoInmision(PuntoInmision puntoInmision) {
		this.puntoInmision = puntoInmision;
	}

	public List<PuntoDescargaLiquida> getPuntosDescargaLiquida() {
		return puntosDescargaLiquida == null ? puntosDescargaLiquida = new ArrayList<PuntoDescargaLiquida>()
				: puntosDescargaLiquida;
	}

	public void setPuntosDescargaLiquida(List<PuntoDescargaLiquida> puntosDescargaLiquida) {
		this.puntosDescargaLiquida = puntosDescargaLiquida;
	}

	public List<PuntoEmision> getPuntosEmision() {
		return puntosEmision == null ? puntosEmision = new ArrayList<PuntoEmision>() : puntosEmision;
	}

	public void setPuntosEmision(List<PuntoEmision> puntosEmision) {
		this.puntosEmision = puntosEmision;
	}

	public List<PuntoInmision> getPuntosInmision() {
		return puntosInmision == null ? puntosInmision = new ArrayList<PuntoInmision>() : puntosInmision;
	}

	public void setPuntosInmision(List<PuntoInmision> puntosInmision) {
		this.puntosInmision = puntosInmision;
	}

	public List<HusoHorario> getHusosHorarios() {
		return husosHorarios == null ? husosHorarios = new ArrayList<HusoHorario>() : husosHorarios;
	}

	public void setHusosHorarios(List<HusoHorario> husosHorarios) {
		this.husosHorarios = husosHorarios;
	}

	public List<PuntoDescargaLiquida> getPuntosDescargaLiquidaFiltrados() {
		return puntosDescargaLiquidaFiltrados;
	}

	public void setPuntosDescargaLiquidaFiltrados(List<PuntoDescargaLiquida> puntosDescargaLiquidaFiltrados) {
		this.puntosDescargaLiquidaFiltrados = puntosDescargaLiquidaFiltrados;
	}

	public List<TipoCombustible> getTiposCombustibles() {
		return tiposCombustibles == null ? tiposCombustibles = new ArrayList<TipoCombustible>() : tiposCombustibles;
	}

	public void setTiposCombustibles(List<TipoCombustible> tiposCombustibles) {
		this.tiposCombustibles = tiposCombustibles;
	}

	public List<TipoFuente> getTiposFuentes() {
		return tiposFuentes == null ? tiposFuentes = new ArrayList<TipoFuente>() : tiposFuentes;
	}

	public void setTiposFuentes(List<TipoFuente> tiposFuentes) {
		this.tiposFuentes = tiposFuentes;
	}

	public List<PuntoEmision> getPuntosEmisionFiltrados() {
		return puntosEmisionFiltrados;
	}

	public List<PuntoInmision> getPuntosInmisionFiltrados() {
		return puntosInmisionFiltrados;
	}

	public void setPuntosInmisionFiltrados(List<PuntoInmision> puntosInmisionFiltrados) {
		this.puntosInmisionFiltrados = puntosInmisionFiltrados;
	}

	public ProyectoLicenciamientoAmbiental getProyecto() {
		return proyecto == null ? proyecto = new ProyectoLicenciamientoAmbiental() : proyecto;
	}

	public void setProyecto(ProyectoLicenciamientoAmbiental proyecto) {
		this.proyecto = proyecto;
	}

	public List<Integer> getPuntosAPagar() {
		return puntosAPagar == null ? puntosAPagar = new ArrayList<Integer>() : puntosAPagar;
	}

	public void setPuntosAPagar(List<Integer> puntosAPagar) {
		this.puntosAPagar = puntosAPagar;
	}

	@PostConstruct
	public void iniciar() {
		setHusosHorarios(aprobacionPuntosMonitoreoFacade.getHusosHorario());
		puntosInmision = aprobacionPuntosMonitoreoFacade.getPuntosInmision();
		puntosEmision = aprobacionPuntosMonitoreoFacade.getPuntosEmision();
		puntosDescargaLiquida = aprobacionPuntosMonitoreoFacade.getPuntosDescargaLiquida();
		tiposCombustibles = aprobacionPuntosMonitoreoFacade.getTiposCombustible();
		tiposFuentes = aprobacionPuntosMonitoreoFacade.getTiposFuente();
	}
}
