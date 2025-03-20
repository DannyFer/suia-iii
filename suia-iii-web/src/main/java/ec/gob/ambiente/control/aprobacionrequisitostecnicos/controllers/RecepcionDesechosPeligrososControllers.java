/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AgregarDesechoPeligroso;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.RecepcionDesechosPeligrososBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.RecepcionDesechoPeligrosoFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RecepcionDesechoPeligroso;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@ManagedBean
@ViewScoped
public class RecepcionDesechosPeligrososControllers {

	private static final Logger LOGGER = Logger.getLogger(RecepcionDesechosPeligrososControllers.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{recepcionDesechosPeligrososBean}")
	private RecepcionDesechosPeligrososBean recepcionDesechosPeligrososBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{agregarDesechoPeligroso}")
	private AgregarDesechoPeligroso agregarDesechoPeligroso;

	@EJB
	private RecepcionDesechoPeligrosoFacade recepcionDesechoPeligrosoFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@PostConstruct
	private void init() {

		obtenerRecepcionDesechos();
		aprobacionRequisitosTecnicosBean.verART(RecepcionDesechoPeligroso.class.getName());
		try {
			recepcionDesechosPeligrososBean.setListaEstados(recepcionDesechoPeligrosoFacade.getEstadosFisicos());
		} catch (Exception e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar el catálogo de estados físicos.");
		}
	}

	private void obtenerRecepcionDesechos() {
		try {
			recepcionDesechosPeligrososBean.setListaRecepcionDesechoPeligroso(recepcionDesechoPeligrosoFacade
					.getListaRecepcionDesechoPeligroso(aprobacionRequisitosTecnicosBean
							.getAprobacionRequisitosTecnicos()));
		} catch (ServiceException e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar la lista de desechos receptados.");
		}

		recepcionDesechosPeligrososBean.setRecepcionDesechoPeligroso(new RecepcionDesechoPeligroso());
		recepcionDesechosPeligrososBean
				.setListaRecepcionDesechoPeligrosoModificados(new ArrayList<RecepcionDesechoPeligroso>());
		recepcionDesechosPeligrososBean
				.setListaRecepcionDesechoPeligrosoEliminados(new ArrayList<RecepcionDesechoPeligroso>());

		if (recepcionDesechosPeligrososBean.getListaRecepcionDesechoPeligroso() == null) {
			recepcionDesechosPeligrososBean
					.setListaRecepcionDesechoPeligroso(new ArrayList<RecepcionDesechoPeligroso>());
		}

	}

	public void agregarDesechoPeligroso() {
		recepcionDesechosPeligrososBean.getRecepcionDesechoPeligroso().setDesecho(
				agregarDesechoPeligroso.getDesechoSeleccionado());
		if (!recepcionDesechosPeligrososBean.isEditar()) {
			recepcionDesechosPeligrososBean.getRecepcionDesechoPeligroso().setAprobacionRequisitosTecnicos(
					aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			recepcionDesechosPeligrososBean.getListaRecepcionDesechoPeligroso().add(
					recepcionDesechosPeligrososBean.getRecepcionDesechoPeligroso());
		}
		recepcionDesechosPeligrososBean.getListaRecepcionDesechoPeligrosoModificados().add(
				recepcionDesechosPeligrososBean.getRecepcionDesechoPeligroso());
		recepcionDesechosPeligrososBean.setRecepcionDesechoPeligroso(new RecepcionDesechoPeligroso());
		recepcionDesechosPeligrososBean.setEditar(false);
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}

	public void remover() {
		recepcionDesechosPeligrososBean.getListaRecepcionDesechoPeligroso().remove(
				recepcionDesechosPeligrososBean.getRecepcionDesechoPeligroso());
		recepcionDesechosPeligrososBean.getListaRecepcionDesechoPeligrosoEliminados().add(
				recepcionDesechosPeligrososBean.getRecepcionDesechoPeligroso());
	}

	public void seleccionarDesechoRegistrado(RecepcionDesechoPeligroso recepcionDesechoPeligroso) {
		recepcionDesechosPeligrososBean.setRecepcionDesechoPeligroso(recepcionDesechoPeligroso);
		agregarDesechoPeligroso.setDesechoSeleccionado(recepcionDesechoPeligroso.getDesecho());
		recepcionDesechosPeligrososBean.setEditar(true);

	}

	public void prepararNuevo() {
		recepcionDesechosPeligrososBean.setEditar(false);
		recepcionDesechosPeligrososBean.setRecepcionDesechoPeligroso(new RecepcionDesechoPeligroso());
		agregarDesechoPeligroso.setDesechoSeleccionado(new DesechoPeligroso());

	}

	public void validarPaginas() {
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			if (!recepcionDesechosPeligrososBean.getListaRecepcionDesechoPeligrosoEliminados().isEmpty()) {

				if (recepcionDesechoPeligrosoFacade.validarPaginasRecepcion(recepcionDesechosPeligrososBean
						.getListaRecepcionDesechoPeligrosoEliminados())) {
					context.addCallbackParam("eliminarRegistro", true);
				} else {
					context.addCallbackParam("eliminarRegistro", false);
					guardarPagina();
				}
			} else {
				guardarPagina();
			}
		} catch (ServiceException e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al guardar la recepción de desechos peligrosos.");
		}

	}

	public void guardarPagina() {
		try {
			recepcionDesechoPeligrosoFacade.guardar(recepcionDesechosPeligrososBean
					.getListaRecepcionDesechoPeligrosoModificados());
			if (!recepcionDesechosPeligrososBean.getListaRecepcionDesechoPeligrosoEliminados().isEmpty()) {
				recepcionDesechoPeligrosoFacade.eliminar(
						recepcionDesechosPeligrososBean.getListaRecepcionDesechoPeligrosoEliminados(),
						aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
				recepcionDesechosPeligrososBean.setListaRecepcionDesechoPeligrosoEliminados(new ArrayList<RecepcionDesechoPeligroso>());
			}
			validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
					"recepcionDesechosPeligrosos", aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()
							.getId().toString());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al guardar la recepción de desechos peligrosos.");
		}
	}

	public String recuperarPageAtras(boolean isModoVer) {
		if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isTransporte()) {
			if (isModoVer) {
				return "/control/aprobacionRequisitosTecnicos/recoleccionYTransporteDesechosVer.jsf?faces-redirect=true";
			} else {
				return "/control/aprobacionRequisitosTecnicos/informativo.jsf?faces-redirect=true";
			}
		} else {
			if (isModoVer) {
				return "/control/aprobacionRequisitosTecnicos/defaultAnalisisRevision.jsf?faces-redirect=true";
			} else {
				return "/control/aprobacionRequisitosTecnicos/default.jsf?faces-redirect=true";
			}
		}

	}

	public void validarSeleccionDesecho(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		StringBuilder functionJs = new StringBuilder();
		if (agregarDesechoPeligroso.getDesechoSeleccionado() == null
				|| (agregarDesechoPeligroso.getDesechoSeleccionado() != null && agregarDesechoPeligroso
						.getDesechoSeleccionado().getClave() == null)) {
			functionJs.append("highlightComponent('form:desechoContainer');");
			String errorMessage = "Debe seleccionar un desecho.";
			RequestContext.getCurrentInstance().execute(functionJs.toString());
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, null));
		} else {
			functionJs.append("removeHighLightComponent('form:tipoTransporte');");
		}
		RequestContext.getCurrentInstance().execute(functionJs.toString());

	}

}
