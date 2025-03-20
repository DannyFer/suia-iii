/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

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
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.ModalidadTratamientoBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadTratamientoFacade;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoTratamiento;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> Controlador de la pagina modalidad tratamiento. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class CalendarioModalidadTratamientoController {

	private static final Logger LOG = Logger.getLogger(CalendarioModalidadTratamientoController.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{modalidadTratamientoBean}")
	private ModalidadTratamientoBean modalidadTratamientoBean;

	@EJB
	private ModalidadTratamientoFacade modalidadTratamientoFacade;

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
			modalidadTratamientoBean.setActividadesProtocoloPrueba(modalidadTratamientoFacade.getActividadesProtocoloPrueba());
			obtenerCalendario();
			//plantillaModalidad = modalidadTratamientoFacade
			//		.getDocumentoInformativo(ModalidadTratamientoFacade.PLANTILLA_TRATAMIENTO);
			
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al crear el calendario inicial con las actividades.");
		}
		
		/* catch (CmisAlfrescoException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar la plantilla del alfresco.");
		}*/
		aprobacionRequisitosTecnicosBean.verART(ProgramaCalendarizadoTratamiento.class.getName());
	}
	
	private void descargarPlantillaProceso() {
		try {
			plantillaModalidad = modalidadTratamientoFacade.getDocumentoInformativo(ModalidadTratamientoFacade.PLANTILLA_TRATAMIENTO);
			

	} catch (ServiceException e) {
		LOG.error(e, e);
		JsfUtil.addMessageError("Ocurrió un error al crear el calendario inicial con las actividades.");

	} catch (CmisAlfrescoException e) {
		LOG.error(e, e);
		JsfUtil.addMessageError("Ocurrió un error al recuperar la plantilla del alfresco.");
	}
	}

	private void obtenerCalendario() throws ServiceException {
		if (modalidadTratamientoBean.getModalidadTratamiento() == null) {
			modalidadTratamientoBean.setModalidadTratamiento(modalidadTratamientoFacade
					.getModalidadTratamiento(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
			if (modalidadTratamientoBean.getModalidadTratamiento() != null) {
				modalidadTratamientoBean.setCalendario(modalidadTratamientoFacade
						.getCalendarioActividades(modalidadTratamientoBean.getModalidadTratamiento()));
			} else {
				modalidadTratamientoBean.setCalendario(new ArrayList<ProgramaCalendarizadoTratamiento>());
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
				modalidadTratamientoFacade.guardar(modalidadTratamientoBean.getCalendario(), modalidadTratamientoBean
						.getModalidadTratamiento().getId());
				validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
						"modalidadTratamientoCalendarioActividades", modalidadTratamientoBean.getModalidadTratamiento()
								.getAprobacionRequisitosTecnicos().getId().toString());
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Ocucció un error al guardar los datos.");
			LOG.error(e, e);
		}
	}

	private boolean isCalendarioEmpty() {
		return modalidadTratamientoBean.getCalendario().isEmpty();
	}

	public void guardarActividadCalendario() {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean isNew = false;
		if (modalidadTratamientoBean.getCalendarioActividad() == null) {
			isNew = true;
			modalidadTratamientoBean.setCalendarioActividad(new ProgramaCalendarizadoTratamiento());
		}

		modalidadTratamientoBean.getCalendarioActividad().setModalidadTratamiento(
				modalidadTratamientoBean.getModalidadTratamiento());
		modalidadTratamientoBean.getCalendarioActividad().setActividad(
				modalidadTratamientoBean.getActividadSeleccionada());
		modalidadTratamientoBean.getCalendarioActividad().setFechaInicio(modalidadTratamientoBean.getFechaIni());
		modalidadTratamientoBean.getCalendarioActividad().setFechaFin(modalidadTratamientoBean.getFechaFin());

		if (isNew) {
			modalidadTratamientoBean.getCalendario().add(modalidadTratamientoBean.getCalendarioActividad());
		}

		context.addCallbackParam("actividadIn", true);
		modalidadTratamientoBean.setFechaFin(null);
		modalidadTratamientoBean.setFechaIni(null);
		modalidadTratamientoBean.setActividadSeleccionada(null);
		modalidadTratamientoBean.setCalendarioActividad(null);
	}

	public void seleccionarActividadCalendario(ProgramaCalendarizadoTratamiento calendarioActividad) {
		modalidadTratamientoBean.setCalendarioActividad(calendarioActividad);
		modalidadTratamientoBean.setActividadSeleccionada(calendarioActividad.getActividad());
		modalidadTratamientoBean.setFechaIni(calendarioActividad.getFechaInicio());
		modalidadTratamientoBean.setFechaFin(calendarioActividad.getFechaFin());
	}

	public void eliminarActividadCalendario(ProgramaCalendarizadoTratamiento calendarioActividad) {
		modalidadTratamientoBean.getCalendario().remove(calendarioActividad);
		modalidadTratamientoBean.setCalendarioActividad(null);
	}

	public StreamedContent getPlantillaModalidad() throws Exception {
		DefaultStreamedContent content = null;
		descargarPlantillaProceso();
		try {
			if (plantillaModalidad != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaModalidad));
				content.setName(ModalidadTratamientoFacade.PLANTILLA_TRATAMIENTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;

	}

}
