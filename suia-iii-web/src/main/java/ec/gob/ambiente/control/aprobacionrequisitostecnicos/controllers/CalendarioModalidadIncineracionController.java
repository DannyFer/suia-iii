/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.ByteArrayInputStream;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.ModalidadIncineracionBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadIncineracionFacade;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoIncineracion;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 * <b> Controlador de la pagina calendario modalidad incineracion. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 22/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class CalendarioModalidadIncineracionController {

	private static final Logger LOG = Logger.getLogger(CalendarioModalidadIncineracionController.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{modalidadIncineracionBean}")
	private ModalidadIncineracionBean modalidadIncineracionBean;

	@EJB
	private ModalidadIncineracionFacade modalidadIncineracionFacade;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	private byte[] plantillaModalidad;

	@PostConstruct
	public void init() {
		try {
			modalidadIncineracionBean.setActividadesProtocolo(modalidadIncineracionFacade
					.getActividadesProtocoloPrueba());
			obtenerCalendario();
			plantillaModalidad = modalidadIncineracionFacade
					.getDocumentoInformativo(ModalidadIncineracionFacade.PLANTILLA_INCINERACION);
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al crear el calendario inicial con las actividades.");
		} catch (CmisAlfrescoException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar la plantilla del alfresco.");
		}
		aprobacionRequisitosTecnicosBean.verART(ProgramaCalendarizadoIncineracion.class.getName());
	}

	private void obtenerCalendario() throws ServiceException {
		if (modalidadIncineracionBean.getModalidadIncineracion() == null) {
			modalidadIncineracionBean.setModalidadIncineracion(modalidadIncineracionFacade
					.getModalidadIncineracion(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
			if (modalidadIncineracionBean.getModalidadIncineracion() != null) {
				modalidadIncineracionBean.setCalendario(modalidadIncineracionFacade
						.getCalendarioActividades(modalidadIncineracionBean.getModalidadIncineracion()));
			} else {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('importanteWdgt').show();");
			}
		}
	}

	public void guardarPagina() {
		try {
			if (isCalendarioEmpty()) {
				JsfUtil.addMessageError("Debe agregar al menos una actividad.");
			} else {
				modalidadIncineracionFacade.guardar(modalidadIncineracionBean.getCalendario(),
						modalidadIncineracionBean.getModalidadIncineracion().getId());
				validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
						"modalidadIncineracionCalendarioActividades", modalidadIncineracionBean
								.getModalidadIncineracion().getAprobacionRequisitosTecnicos().getId().toString());
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Ocucció un error al guardar los datos.");
			LOG.error(e, e);
		}
	}

	private boolean isCalendarioEmpty() {
		return modalidadIncineracionBean.getCalendario().isEmpty();
	}

	public void guardarActividadCalendario() {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean isNew = false;
		if (modalidadIncineracionBean.getCalendarioActividad() == null) {
			isNew = true;
			modalidadIncineracionBean.setCalendarioActividad(new ProgramaCalendarizadoIncineracion());
		}

		modalidadIncineracionBean.getCalendarioActividad().setModalidadIncineracion(
				modalidadIncineracionBean.getModalidadIncineracion());
		modalidadIncineracionBean.getCalendarioActividad().setActividad(
				modalidadIncineracionBean.getActividadSeleccionada());
		modalidadIncineracionBean.getCalendarioActividad().setFechaInicio(modalidadIncineracionBean.getFechaInicio());
		modalidadIncineracionBean.getCalendarioActividad().setFechaFin(modalidadIncineracionBean.getFechaFin());

		if (isNew) {
			modalidadIncineracionBean.getCalendario().add(modalidadIncineracionBean.getCalendarioActividad());
		}

		context.addCallbackParam("actividadIn", true);
		modalidadIncineracionBean.setFechaFin(null);
		modalidadIncineracionBean.setFechaInicio(null);
		modalidadIncineracionBean.setActividadSeleccionada(null);
		modalidadIncineracionBean.setCalendarioActividad(null);
	}

	public void seleccionarActividadCalendario(ProgramaCalendarizadoIncineracion calendarioActividad) {
		modalidadIncineracionBean.setCalendarioActividad(calendarioActividad);
		modalidadIncineracionBean.setActividadSeleccionada(calendarioActividad.getActividad());
		modalidadIncineracionBean.setFechaInicio(calendarioActividad.getFechaInicio());
		modalidadIncineracionBean.setFechaFin(calendarioActividad.getFechaFin());
	}

	public void eliminarActividadCalendario(ProgramaCalendarizadoIncineracion calendarioActividad) {
		modalidadIncineracionBean.getCalendario().remove(calendarioActividad);
		modalidadIncineracionBean.setCalendarioActividad(null);
	}

	public StreamedContent getPlantillaModalidad() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (plantillaModalidad != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaModalidad));
				content.setName(ModalidadIncineracionFacade.PLANTILLA_INCINERACION_PROCESOS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;

	}

}
