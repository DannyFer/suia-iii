/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.aprobacionpuntosmonitoreo.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionpuntosmonitoreo.facade.AprobacionPuntosMonitoreoFacade;
import ec.gob.ambiente.suia.domain.HusoHorario;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.PuntoDescargaLiquida;
import ec.gob.ambiente.suia.domain.PuntoEmision;
import ec.gob.ambiente.suia.domain.PuntoInmision;
import ec.gob.ambiente.suia.domain.TipoCombustible;
import ec.gob.ambiente.suia.domain.TipoFuente;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

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
public class PuntosMonitoreoModoLecturaBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4494910945991939017L;
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

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	private static final Logger LOG = Logger.getLogger(AdjuntarComprobantePagoBean.class);

	private boolean visualizarDetallePuntosEmision = false;
	private boolean visualizarDetallePuntosDescargaLiquida = false;
	private boolean visualizarDetallePuntosInmision = false;

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

		long idInstanciaProceso = bandejaTareasBean.getTarea().getProcessInstanceId();
		try {
			if (idInstanciaProceso != 0) {
				Map<String, Object> variablesProceso = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), idInstanciaProceso);
				String puntosSeleccionadosPagar = (String) variablesProceso.get("puntosSeleccionadosPagar");

				if (!"".equals(puntosSeleccionadosPagar)) {

					if (puntosAPagar == null)
						puntosAPagar = new ArrayList<Integer>();

					String[] arreglo = puntosSeleccionadosPagar.split(Constantes.SEPARADOR);

					for (int i = 0; i < arreglo.length; i++) {
						puntosAPagar.add(Integer.parseInt(arreglo[i]));
					}

					puntosInmision = aprobacionPuntosMonitoreoFacade.getPuntosInmision(puntosAPagar);
					puntosEmision = aprobacionPuntosMonitoreoFacade.getPuntosEmision(puntosAPagar);
					puntosDescargaLiquida = aprobacionPuntosMonitoreoFacade.getPuntosDescargaLiquida(puntosAPagar);
				}

			}
		} catch (JbpmException e) {
			LOG.error("Error al iniciar inicializar el bean", e);
		}

		setHusosHorarios(aprobacionPuntosMonitoreoFacade.getHusosHorario());
		tiposCombustibles = aprobacionPuntosMonitoreoFacade.getTiposCombustible();
		tiposFuentes = aprobacionPuntosMonitoreoFacade.getTiposFuente();
	}

	public boolean isVisualizarDetallePuntosEmision() {
		return visualizarDetallePuntosEmision;
	}

	public void setVisualizarDetallePuntosEmision(boolean visualizarDetallePuntosEmision) {
		this.visualizarDetallePuntosEmision = visualizarDetallePuntosEmision;
	}

	public boolean isVisualizarDetallePuntosDescargaLiquida() {
		return visualizarDetallePuntosDescargaLiquida;
	}

	public void setVisualizarDetallePuntosDescargaLiquida(boolean visualizarDetallePuntosDescargaLiquida) {
		this.visualizarDetallePuntosDescargaLiquida = visualizarDetallePuntosDescargaLiquida;
	}

	public boolean isVisualizarDetallePuntosInmision() {
		return visualizarDetallePuntosInmision;
	}

	public void setVisualizarDetallePuntosInmision(boolean visualizarDetallePuntosInmision) {
		this.visualizarDetallePuntosInmision = visualizarDetallePuntosInmision;
	}

}
