/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.InformacionPatioManiobrasBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.RequisitosConductorBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ConductorFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformacionPatioManiobrasFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.RequisitosConductorFacade;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.Conductor;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RequisitosConductor;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> Clase controlador para los requisitos del conductor. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 04/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class RequisitosConductorControllers {

	private static final Logger LOG = Logger.getLogger(RequisitosConductorControllers.class);

	@EJB
	private RequisitosConductorFacade requisitosConductorFacade;

	@EJB
	private ConductorFacade conductorFacade;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{requisitosConductorBean}")
	private RequisitosConductorBean requisitosConductorBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{informacionPatioManiobrasBean}")
	private InformacionPatioManiobrasBean informacionPatioManiobrasBean;

	@EJB
	private InformacionPatioManiobrasFacade informacionPatioManiobrasFacade;

	@Getter
	@Setter
	private String cedula;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@PostConstruct
	public void init() {
		requisitosConductorBean.setRequisitoConductores(new ArrayList<RequisitosConductor>());
		requisitosConductorBean.setRequisitoConductoresEli(new ArrayList<RequisitosConductor>());
		aprobacionRequisitosTecnicosBean.verART(RequisitosConductor.class.getName());
		obtenerConductores();
		try {
			informacionPatioManiobrasBean.setInformacionPatioManiobra(informacionPatioManiobrasFacade
					.getInformacionPatioManiobra(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
			if (informacionPatioManiobrasBean.getInformacionPatioManiobra() == null) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('importanteWdgt').show();");
			}

		} catch (ServiceException e) {
			JsfUtil.addMessageError("Ocurrió un error al recuperar la información del patio de maniobras.");
			LOG.error(e, e);
		}
	}

	public void obtenerConductores() {
		if (requisitosConductorBean.getRequisitoConductores().isEmpty()) {
			List<RequisitosConductor> requisitosConductors = requisitosConductorFacade
					.getListaConductores(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			if (requisitosConductors != null) {
				setConductores(requisitosConductors);
			}
		}

	}

	public void buscarConductor() {
		RequisitosConductor conductorAux = new RequisitosConductor();
		Conductor conductor = new Conductor();
		conductor = this.conductorFacade.buscarConductoresPorCedula(this.cedula);
		if (conductor == null) {
			requisitosConductorBean.setConductorEncontrado(false);
			JsfUtil.addMessageError("No se encontro el conductor con la cédula: " + getCedula());
		} else {

			if (conductor.isPermisoCaducado()) {
				requisitosConductorBean.setConductorEncontrado(false);
				JsfUtil.addMessageError("Estimado usuario el conductor que usted ha seleccionado se encuentra con Certificado Caducado, por lo que  para continuar con el proceso deberá buscar otro.");
			} else if (isConductorRegistrado(conductor)) {
				requisitosConductorBean.setConductorEncontrado(false);
				conductor = null;
				JsfUtil.addMessageError("El conductor ya se encuentra registrado, por favor adicione otro.");
			} else {
				conductorAux.setConductor(conductor);
				requisitosConductorBean.setRequisitosConductor(conductorAux);
				requisitosConductorBean.setMostrarPanel(true);
				requisitosConductorBean.setConductorEncontrado(true);
			}
		}
	}

	private boolean isConductorRegistrado(Conductor conductor) {
		for (RequisitosConductor requisitosConductor : requisitosConductorBean.getRequisitoConductores()) {
			if (requisitosConductor.getConductor().equals(conductor)) {
				return true;
			}
		}
		return false;
	}

	public void agregarConductor() {
		requisitosConductorBean.setRequisitosConductor(new RequisitosConductor());
		setCedula(null);
	}

	public void anadirConductor() {
		if (requisitosConductorBean.isConductorEncontrado()) {
			requisitosConductorBean.getRequisitoConductores().add(requisitosConductorBean.getRequisitosConductor());
			requisitosConductorBean.setRequisitosConductor(new RequisitosConductor());
			setCedula(null);
			requisitosConductorBean.setMostrarPanel(false);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

		} else {
			JsfUtil.addMessageError("El conductor no ha sido encontrado.");
		}
	}

	private void validarNumeroConductoresIngresados() throws ServiceException {
		RequestContext context = RequestContext.getCurrentInstance();
		if (getConductores().size() < informacionPatioManiobrasBean.getInformacionPatioManiobra().getNumeroVehiculos()) {
			context.addCallbackParam("numConductores", true);
		} else {
			context.addCallbackParam("numConductores", false);
			requisitosConductorFacade.guardarPaginaComoCompleta(aprobacionRequisitosTecnicosBean
					.getAprobacionRequisitosTecnicos());
		}

	}

	public void guardar() {
		try {
			if (!requisitosConductorBean.getRequisitoConductoresEli().isEmpty()) {
				for (RequisitosConductor requisito : requisitosConductorBean.getRequisitoConductoresEli()) {
					if (requisito.getId() != null) {
						requisito.setEstado(false);
						requisitosConductorFacade.guardarConductor(requisito);
					}
				}
			}
			for (RequisitosConductor requisitosConductor : getConductores()) {
				requisitosConductor.setAprobacionRequisitosTecnicos(aprobacionRequisitosTecnicosBean
						.getAprobacionRequisitosTecnicos());
				requisitosConductorFacade.guardarConductor(requisitosConductor);
			}
			if (requisitosConductorFacade.isPageRequitosConductorRequerida(aprobacionRequisitosTecnicosBean
					.getAprobacionRequisitosTecnicos())) {
				validarNumeroConductoresIngresados();
			} else {
				requisitosConductorFacade.guardarPaginaComoCompleta(aprobacionRequisitosTecnicosBean
						.getAprobacionRequisitosTecnicos());
			}

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			LOG.error("Error al Guardar los requisitos del conductor.", e);
			JsfUtil.addMessageError("No se puede guardar los conductores.");
		}

	}

	public void seleccionarConductor(RequisitosConductor conductorSeleccionado) {
		requisitosConductorBean.setRequisitosConductor(conductorSeleccionado);
		this.cedula = conductorSeleccionado.getConductor().getDocumento();
	}

	public void remover() {
		requisitosConductorBean.getRequisitoConductoresEli().add(requisitosConductorBean.getRequisitosConductor());
		getConductores().remove(requisitosConductorBean.getRequisitosConductor());
	}

	/**
	 * @return the conductores
	 */
	public List<RequisitosConductor> getConductores() {
		if (requisitosConductorBean.getRequisitoConductores() == null) {
			requisitosConductorBean.setRequisitoConductores(new ArrayList<RequisitosConductor>());
		}
		return requisitosConductorBean.getRequisitoConductores();
	}

	/**
	 * @param conductores
	 *            the conductores to set
	 */
	public void setConductores(List<RequisitosConductor> conductores) {
		requisitosConductorBean.setRequisitoConductores(conductores);
	}

	public String recuparPaginaSiguiente(boolean isModoVer) {
		String extencionModo = (isModoVer ? "Ver" : "") + ".jsf?faces-redirect=true";
		if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isTransporte()) {
			return "/control/aprobacionRequisitosTecnicos/sustanciasQuimicasPeligrosas" + extencionModo;
		} else {
			return "/control/aprobacionRequisitosTecnicos/desechoPeligrosoTransporte" + extencionModo;
		}
	}

}
