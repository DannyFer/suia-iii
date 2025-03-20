/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.ByteArrayInputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.ModalidadCoprocesamientoBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadCoprocesamientoFacade;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoCoprocesamiento;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> Controlador de la página calendario modalidad coprocesamiento. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 02/07/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class CalendarioModalidadCoprocesamientoController {

	private static final Logger LOG = Logger.getLogger(CalendarioModalidadCoprocesamientoController.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{modalidadCoprocesamientoBean}")
	private ModalidadCoprocesamientoBean modalidadCoprocesamientoBean;

	@EJB
	private ModalidadCoprocesamientoFacade modalidadCoprocesamientoFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	private byte[] plantillaModalidad;

	@PostConstruct
	public void init() {
		try {
			modalidadCoprocesamientoBean.setModalidadCoprocesamiento(modalidadCoprocesamientoFacade
					.getModalidadCoprocesamiento(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
			if(modalidadCoprocesamientoBean.getModalidadCoprocesamiento() == null) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('importantWdgt').show();");
			} else {
				modalidadCoprocesamientoBean.setActividadesProtocolo(modalidadCoprocesamientoFacade
						.getActividadesProtocoloPrueba());
				modalidadCoprocesamientoBean.setCalendario(modalidadCoprocesamientoFacade
						.getCalendarioActividades(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
				plantillaModalidad = modalidadCoprocesamientoFacade
						.getDocumentoInformativo(ModalidadCoprocesamientoFacade.PLANTILLA_COOPROCESAMIENTO);

				aprobacionRequisitosTecnicosBean.verART(ProgramaCalendarizadoCoprocesamiento.class.getName());
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al crear el calendario inicial con las actividades.");
		} catch (CmisAlfrescoException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar la plantilla del alfresco.");
		}

	}

	public void guardarPagina() {
		try {
			if (isCalendarioEmpty()) {
				JsfUtil.addMessageError("Debe agregar al menos una actividad.");
			} else {
				modalidadCoprocesamientoFacade.guardar(modalidadCoprocesamientoBean.getCalendario(),
						modalidadCoprocesamientoBean.getModalidadCoprocesamiento().getId());
				validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
						"modalidadCoprocesamientoCalendarioActividades", modalidadCoprocesamientoBean
								.getModalidadCoprocesamiento().getAprobacionRequisitosTecnicos().getId().toString());
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}

		} catch (ServiceException e) {
			JsfUtil.addMessageError("Ocurrió un error al guardar los datos.");
			LOG.error(e, e);
		}
	}

	private boolean isCalendarioEmpty() {
		return modalidadCoprocesamientoBean.getCalendario().isEmpty();
	}

	public void guardarActividadCalendario() {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean isNew = false;
		if (modalidadCoprocesamientoBean.getCalendarioActividad() == null) {
			isNew = true;
			modalidadCoprocesamientoBean.setCalendarioActividad(new ProgramaCalendarizadoCoprocesamiento());
		}

		modalidadCoprocesamientoBean.getCalendarioActividad().setModalidadCoprocesamiento(
				modalidadCoprocesamientoBean.getModalidadCoprocesamiento());
		modalidadCoprocesamientoBean.getCalendarioActividad().setActividad(
				modalidadCoprocesamientoBean.getActividadSeleccionada());
		modalidadCoprocesamientoBean.getCalendarioActividad().setFechaInicio(
				modalidadCoprocesamientoBean.getFechaInicio());
		modalidadCoprocesamientoBean.getCalendarioActividad().setFechaFin(modalidadCoprocesamientoBean.getFechaFin());

		if (isNew) {
			modalidadCoprocesamientoBean.getCalendario().add(modalidadCoprocesamientoBean.getCalendarioActividad());
		}

		context.addCallbackParam("actividadIn", true);
		modalidadCoprocesamientoBean.setFechaFin(null);
		modalidadCoprocesamientoBean.setFechaInicio(null);
		modalidadCoprocesamientoBean.setActividadSeleccionada(null);
		modalidadCoprocesamientoBean.setCalendarioActividad(null);
	}

	public void seleccionarActividadCalendario(ProgramaCalendarizadoCoprocesamiento calendarioActividad) {
		modalidadCoprocesamientoBean.setCalendarioActividad(calendarioActividad);
		modalidadCoprocesamientoBean.setActividadSeleccionada(calendarioActividad.getActividad());
		modalidadCoprocesamientoBean.setFechaInicio(calendarioActividad.getFechaInicio());
		modalidadCoprocesamientoBean.setFechaFin(calendarioActividad.getFechaFin());
	}

	public void eliminarActividadCalendario(ProgramaCalendarizadoCoprocesamiento calendarioActividad) {
		modalidadCoprocesamientoBean.getCalendario().remove(calendarioActividad);
		modalidadCoprocesamientoBean.setCalendarioActividad(null);
	}

	public StreamedContent getPlantillaModalidad() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (plantillaModalidad != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaModalidad));
				content.setName(ModalidadCoprocesamientoFacade.PLANTILLA_COOPROCESAMIENTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

//	private void obtenerCalendario() throws ServiceException {
//		if (modalidadCoprocesamientoBean.getModalidadCoprocesamiento() == null) {
//			modalidadCoprocesamientoBean.setModalidadCoprocesamiento(modalidadCoprocesamientoFacade
//					.getModalidadCoprocesamiento(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
//			if (modalidadCoprocesamientoBean.getModalidadCoprocesamiento() != null) {
//				modalidadCoprocesamientoBean.setCalendario(modalidadCoprocesamientoFacade
//						.getCalendarioActividades(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
//			} else {
//				RequestContext context = RequestContext.getCurrentInstance();
//				context.execute("PF('importantWdgt').show();");
//			}
//		}
//	}

}
