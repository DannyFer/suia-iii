package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformativoFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ValidacionesPagesAprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@ManagedBean
@ViewScoped
public class InformativoController {

	private static final Logger LOGGER = Logger.getLogger(InformativoController.class);

	@EJB
	private ValidacionesPagesAprobacionRequisitosTecnicosFacade validacionesPagesAprobacionRequisitosTecnicosFacade;

	@EJB
	private InformativoFacade informativoFacade;

//	private byte[] file;

	private StreamedContent imageInformativoTratamiento;

	private StreamedContent imageInformativoIncineracion;

	private StreamedContent imageInformativoCoprocesamiento;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	public String recuperarPageSiguiente() {
		if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()) {
			if (!aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestionSoloConTransporte()) {
				return "/control/aprobacionRequisitosTecnicos/recepcionDesechosPeligrosos.jsf?faces-redirect=true";
			} else {
				return "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitos.jsf?faces-redirect=true";
			}
		} else {
			return "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitos.jsf?faces-redirect=true";
		}
	}

	public String recuperarPaginaAnterior() {

		try {
			if (validacionesPagesAprobacionRequisitosTecnicosFacade
					.isPageRecoleccionTransporteDesechosVisible(aprobacionRequisitosTecnicosBean
							.getAprobacionRequisitosTecnicos())) {
				return "/control/aprobacionRequisitosTecnicos/recoleccionYTransporteDesechos.jsf?faces-redirect=true";
			} else {
				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestionConTransporte()) {
					return "/control/aprobacionRequisitosTecnicos/desechoPeligrosoTransporte.jsf?faces-redirect=true";
				} else {
					return "/control/aprobacionRequisitosTecnicos/sustanciasQuimicasPeligrosas.jsf?faces-redirect=true";
				}

			}
		} catch (ServiceException e) {
			LOGGER.error(e, e);
			return "";
		}
	}

//	private DefaultStreamedContent getDefaultStreamedContentImagen(String nombre) {
//		try {
//			file = informativoFacade.getDocumentoInformativo(nombre);
//
//		} catch (Exception e) {
//			LOGGER.error(e, e);
//		}
//
//		return new DefaultStreamedContent(new ByteArrayInputStream(file), "image/png");
//
//	}

}
