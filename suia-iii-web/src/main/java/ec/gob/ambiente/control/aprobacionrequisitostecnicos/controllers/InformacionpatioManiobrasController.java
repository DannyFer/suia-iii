package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.InformacionPatioManiobrasBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformacionPatioManiobrasFacade;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.InformacionPatioManiobra;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@ManagedBean
@ViewScoped
public class InformacionpatioManiobrasController {

	@Setter
	@Getter
	@ManagedProperty(value = "#{informacionPatioManiobrasBean}")
	private InformacionPatioManiobrasBean informacionPatioManiobrasBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private InformacionPatioManiobrasFacade informacionPatioManiobrasFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	private static final Logger LOGGER = Logger.getLogger(InformacionpatioManiobrasController.class);

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@PostConstruct
	private void init() {
		informacionPatioManiobrasBean.setProvincias(ubicacionGeograficaFacade.getProvincias());
		aprobacionRequisitosTecnicosBean.verART(InformacionPatioManiobra.class.getName());
		try {
			obtenerInformacionPatio();
		} catch (ServiceException e) {
			JsfUtil.addMessageError(e.getMessage());
			LOGGER.error(e, e);
		}
	}

	private void obtenerInformacionPatio() throws ServiceException {
		informacionPatioManiobrasBean.setInformacionPatioManiobra(informacionPatioManiobrasFacade
				.getInformacionPatioManiobra(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
		if (informacionPatioManiobrasBean.getInformacionPatioManiobra() == null) {
			informacionPatioManiobrasBean.setInformacionPatioManiobra(new InformacionPatioManiobra());
			informacionPatioManiobrasBean.getInformacionPatioManiobra().setAprobacionRequisitosTecnicos(
					aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
		} else {
			cargarUbicacionesSeleccionadas();
		}
	}

	public void guardarPagina() {
		informacionPatioManiobrasBean.getInformacionPatioManiobra().setUbicacionesGeografica(
				informacionPatioManiobrasBean.getParroquia());
		try {
			informacionPatioManiobrasFacade.guardar(informacionPatioManiobrasBean.getInformacionPatioManiobra());
			validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
					"informacionPatioManiobra", informacionPatioManiobrasBean.getInformacionPatioManiobra()
							.getAprobacionRequisitosTecnicos().getId().toString());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (ServiceException e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al guardar.");
		}

	}

	public UbicacionesGeografica getUbicacionSeleccionada() {
		if (informacionPatioManiobrasBean.getParroquia() != null)
			return informacionPatioManiobrasBean.getParroquia();
		if (informacionPatioManiobrasBean.getCanton() != null)
			return informacionPatioManiobrasBean.getCanton();
		return informacionPatioManiobrasBean.getProvincia();
	}

	public void cargarCantones() {
		informacionPatioManiobrasBean.setCantones(new ArrayList<UbicacionesGeografica>());
		informacionPatioManiobrasBean.setParroquias(new ArrayList<UbicacionesGeografica>());
		if (informacionPatioManiobrasBean.getProvincia() != null)
			informacionPatioManiobrasBean.setCantones(ubicacionGeograficaFacade
					.getCantonesParroquia(informacionPatioManiobrasBean.getProvincia()));
		else
			informacionPatioManiobrasBean.setCantones(new ArrayList<UbicacionesGeografica>());
	}

	public void cargarParroquias() {
		informacionPatioManiobrasBean.setParroquias(new ArrayList<UbicacionesGeografica>());
		if (informacionPatioManiobrasBean.getCanton() != null)
			informacionPatioManiobrasBean.setParroquias(ubicacionGeograficaFacade
					.getCantonesParroquia(informacionPatioManiobrasBean.getCanton()));
		else
			informacionPatioManiobrasBean.setParroquias(new ArrayList<UbicacionesGeografica>());
	}

	private void cargarUbicacionesSeleccionadas() {
		informacionPatioManiobrasBean.setParroquia(informacionPatioManiobrasBean.getInformacionPatioManiobra()
				.getUbicacionesGeografica());
		informacionPatioManiobrasBean
				.setCanton(informacionPatioManiobrasBean.getParroquia().getUbicacionesGeografica());
		informacionPatioManiobrasBean
				.setProvincia(informacionPatioManiobrasBean.getCanton().getUbicacionesGeografica());
		cargarCantones();
		cargarParroquias();
	}

}
