/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.aprobacionpuntosmonitoreo.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.aprobacionpuntosmonitoreo.bean.PuntosMonitoreoBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionpuntosmonitoreo.facade.AprobacionPuntosMonitoreoFacade;
import ec.gob.ambiente.suia.domain.PuntoDescargaLiquida;
import ec.gob.ambiente.suia.domain.PuntoEmision;
import ec.gob.ambiente.suia.domain.PuntoInmision;
import ec.gob.ambiente.suia.domain.PuntoMonitoreo;
import ec.gob.ambiente.suia.domain.PuntoMonitoreo.EstadoPuntoMonitoreo;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

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
public class AprobacionPuntosController {

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

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	private static final Logger LOG = Logger.getLogger(AprobacionPuntosController.class);

	public void iniciarProceso() {
		/*identificarProyectoComunBean.initFunction("/control/aprobacionpuntosmonitoreo/adicionarPuntoMonitoreo.jsf",
				new CompleteOperation() {

					@Override
					public Object endOperation(Object object) {

						puntosMonitoreoBean.setProyecto((ProyectoLicenciamientoAmbiental) object);

						Map<String, Object> params = new HashMap<String, Object>();
						params.put("idProyecto", puntosMonitoreoBean.getProyecto().getId());
						params.put("sujetoControl", loginBean.getNombreUsuario());
						params.put("subsecretaria", Constantes.USUARIO_PRUEBA_BC);
						params.put("directornacionalca", Constantes.USUARIO_PRUEBA_BC);
						params.put("coordinadorca", Constantes.USUARIO_PRUEBA_BC);
						params.put("tecnicoca", Constantes.USUARIO_PRUEBA_BC);

						try {

							long processInstanceId = procesoFacade.iniciarProceso(
									Constantes.NOMBRE_PROCESO_APROBACION_PUNTOS_MONITOREO, "Punto-Monitoreo-Tramite",
									Constantes.getDeploymentId(), params, Constantes.getUrlBusinessCentral(),
									loginBean.getNombreUsuario(), loginBean.getPassword());

							bandejaTareasBean.setProcessId(processInstanceId);

						} catch (JbpmException e) {
							LOG.error("Error al iniciar el proceso", e);
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
						}
						return null;
					}
				});*/

	}

	public void completarTareaSeleccionarPuntosMonitoreo() {
		String puntosSeleccionadosPagar = "";
		int sizePuntosPagar = puntosMonitoreoBean.getPuntosAPagar().size();
		// nuevo
		for (int i = 0; i < sizePuntosPagar; i++) {
			PuntoMonitoreo punto = aprobacionPuntosMonitoreoFacade.getPuntoMonitoreo(puntosMonitoreoBean
					.getPuntosAPagar().get(i));
			punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.PENDIENTE_APROBACION);
			aprobacionPuntosMonitoreoFacade.saveOrUpdate(punto);
		}

		// nuevo
		for (int i = 0; i < sizePuntosPagar; i++) {
			puntosSeleccionadosPagar += puntosMonitoreoBean.getPuntosAPagar().get(i) + Constantes.SEPARADOR;

		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nuevoOModificacion", sizePuntosPagar > 0 ? true : false);
		params.put("puntosSeleccionadosPagar", puntosSeleccionadosPagar);

		long idInstanciaProceso = bandejaTareasBean.getProcessId();

		try {

			long idTarea = procesoFacade.recuperarIdTareaActual(loginBean.getUsuario(), idInstanciaProceso);

			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), idInstanciaProceso, params);

			procesoFacade.aprobarTarea(loginBean.getUsuario(), idTarea, idInstanciaProceso, null);

			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");

		} catch (JbpmException e) {
			LOG.error("Error al iniciar el proceso", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}

	}

	// Gestion Punto Descarga Liquida
	public void editarPuntoDescargaLiquida(PuntoDescargaLiquida punto) {
		puntosMonitoreoBean.setPuntoDescargaLiquida(punto);
	}

	public void eliminarPuntoDescargaLiquida(PuntoDescargaLiquida punto) {

		if (punto.getEstadoPuntoMonitoreo().equals(EstadoPuntoMonitoreo.RECIEN_INSERTADO)
				&& puntosMonitoreoBean.getPuntosAPagar().contains(punto.getId())) {
			puntosMonitoreoBean.getPuntosAPagar().remove((Integer) punto.getId());
		}

		aprobacionPuntosMonitoreoFacade.delete(punto);
		puntosMonitoreoBean.setPuntosDescargaLiquida(aprobacionPuntosMonitoreoFacade.getPuntosDescargaLiquida());
	}

	public void adicionarPuntoDescargaLiquida() {
		PuntoDescargaLiquida punto = puntosMonitoreoBean.getPuntoDescargaLiquida();

		if (!punto.isPersisted()) {
			if (punto.isAprobado()) {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.APROBADO_SIN_VERIFICAR);
				aprobacionPuntosMonitoreoFacade.saveOrUpdate(punto);
			} else {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.RECIEN_INSERTADO);
				aprobacionPuntosMonitoreoFacade.saveOrUpdate(punto);
				puntosMonitoreoBean.getPuntosAPagar().add(punto.getId());
			}

		} else {
			if (!punto.getEstadoPuntoMonitoreo().equals(EstadoPuntoMonitoreo.RECIEN_INSERTADO)) {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.RECIEN_INSERTADO);
				puntosMonitoreoBean.getPuntosAPagar().add(punto.getId());
			} else if (punto.isAprobado()) {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.APROBADO_SIN_VERIFICAR);
				if (puntosMonitoreoBean.getPuntosAPagar().contains(punto.getId()))
					puntosMonitoreoBean.getPuntosAPagar().remove((Integer) punto.getId());
			}
			aprobacionPuntosMonitoreoFacade.saveOrUpdate(punto);
		}

		puntosMonitoreoBean.setPuntosDescargaLiquida(aprobacionPuntosMonitoreoFacade.getPuntosDescargaLiquida());
		puntosMonitoreoBean.setPuntoDescargaLiquida(null);
	}

	public void limpiarTabPuntoDescargaLiquida() {
		puntosMonitoreoBean.setPuntoDescargaLiquida(null);

	}

	// Gestion Punto Emision Atmosfera
	public void editarPuntoEmisionAtmosfera(PuntoEmision punto) {
		puntosMonitoreoBean.setPuntoEmision(punto);
	}

	public void eliminarPuntoEmisionAtmosfera(PuntoEmision punto) {
		if (punto.getEstadoPuntoMonitoreo().equals(EstadoPuntoMonitoreo.RECIEN_INSERTADO)
				&& puntosMonitoreoBean.getPuntosAPagar().contains(punto.getId())) {
			puntosMonitoreoBean.getPuntosAPagar().remove((Integer) punto.getId());
		}

		aprobacionPuntosMonitoreoFacade.delete(punto);
		puntosMonitoreoBean.setPuntosEmision(aprobacionPuntosMonitoreoFacade.getPuntosEmision());
	}

	public void adicionarPuntoEmisionAtmosfera() {
		PuntoEmision punto = puntosMonitoreoBean.getPuntoEmision();
		if (!punto.isPersisted()) {
			if (punto.isAprobado()) {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.APROBADO_SIN_VERIFICAR);
				aprobacionPuntosMonitoreoFacade.saveOrUpdate(punto);
			} else {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.RECIEN_INSERTADO);
				aprobacionPuntosMonitoreoFacade.saveOrUpdate(punto);
				puntosMonitoreoBean.getPuntosAPagar().add(punto.getId());
			}

		} else {
			if (!punto.getEstadoPuntoMonitoreo().equals(EstadoPuntoMonitoreo.RECIEN_INSERTADO)) {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.RECIEN_INSERTADO);
				puntosMonitoreoBean.getPuntosAPagar().add(punto.getId());
			} else if (punto.isAprobado()) {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.APROBADO_SIN_VERIFICAR);
				if (puntosMonitoreoBean.getPuntosAPagar().contains(punto.getId()))
					puntosMonitoreoBean.getPuntosAPagar().remove((Integer) punto.getId());
			}
			aprobacionPuntosMonitoreoFacade.saveOrUpdate(punto);
		}

		puntosMonitoreoBean.setPuntosEmision(aprobacionPuntosMonitoreoFacade.getPuntosEmision());
		puntosMonitoreoBean.setPuntoEmision(null);
	}

	public void limpiarTabPuntoEmisionAtmosfera() {
		puntosMonitoreoBean.setPuntoEmision(null);

	}

	// Gestion Punto Inmision
	public void editarPuntoInmision(PuntoInmision punto) {
		puntosMonitoreoBean.setPuntoInmision(punto);
	}

	public void eliminarPuntoInmision(PuntoInmision punto) {
		if (punto.getEstadoPuntoMonitoreo().equals(EstadoPuntoMonitoreo.RECIEN_INSERTADO)
				&& puntosMonitoreoBean.getPuntosAPagar().contains(punto.getId())) {
			puntosMonitoreoBean.getPuntosAPagar().remove((Integer) punto.getId());
		}

		aprobacionPuntosMonitoreoFacade.delete(punto);
		puntosMonitoreoBean.setPuntosInmision(aprobacionPuntosMonitoreoFacade.getPuntosInmision());
	}

	public void adicionarPuntoInmision() {
		PuntoInmision punto = puntosMonitoreoBean.getPuntoInmision();

		if (!punto.isPersisted()) {
			if (punto.isAprobado()) {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.APROBADO_SIN_VERIFICAR);
				aprobacionPuntosMonitoreoFacade.saveOrUpdate(punto);
			} else {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.RECIEN_INSERTADO);
				aprobacionPuntosMonitoreoFacade.saveOrUpdate(punto);
				puntosMonitoreoBean.getPuntosAPagar().add(punto.getId());
			}

		} else {
			if (!punto.getEstadoPuntoMonitoreo().equals(EstadoPuntoMonitoreo.RECIEN_INSERTADO)) {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.RECIEN_INSERTADO);
				puntosMonitoreoBean.getPuntosAPagar().add(punto.getId());
			} else if (punto.isAprobado()) {
				punto.setEstadoPuntoMonitoreo(EstadoPuntoMonitoreo.APROBADO_SIN_VERIFICAR);
				if (puntosMonitoreoBean.getPuntosAPagar().contains(punto.getId()))
					puntosMonitoreoBean.getPuntosAPagar().remove((Integer) punto.getId());
			}
			aprobacionPuntosMonitoreoFacade.saveOrUpdate(punto);
		}

		puntosMonitoreoBean.setPuntosInmision(aprobacionPuntosMonitoreoFacade.getPuntosInmision());
		puntosMonitoreoBean.setPuntoInmision(null);
	}

	public void limpiarTabPuntoInmision() {
		puntosMonitoreoBean.setPuntoInmision(null);

	}

}
