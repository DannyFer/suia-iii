package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import lombok.Getter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade.InspeccionControlAmbientalFacade;
import ec.gob.ambiente.suia.domain.SolicitudInspeccionControlAmbiental;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

public abstract class TecnicoTramiteICAController implements Serializable {

	private static final long serialVersionUID = 3528791022644730831L;

	private static final Logger LOGGER = Logger.getLogger(TecnicoTramiteICAController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private InspeccionControlAmbientalFacade inspeccionControlAmbientalFacade;

	@Getter
	private SolicitudInspeccionControlAmbiental solicitud;

	@PostConstruct
	private void init() {
		try {
			Integer idSolicitud = Integer.parseInt(JsfUtil.getBean(BandejaTareasBean.class).getTarea()
					.getVariable(SolicitudInspeccionControlAmbiental.VARIABLE_ID_SOLICITUD).toString());

			solicitud = inspeccionControlAmbientalFacade.get(idSolicitud);
		} catch (Exception e) {
			LOGGER.error("Error al recuperar los datos de la inspeccion.", e);
		}
	}

	public String aceptar() {
		try {
			if (!validate())
				return "";
			
			Map<String, Object> params = generateParameters();
			if (params != null)
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(),
						JsfUtil.getBean(BandejaTareasBean.class).getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(),
					JsfUtil.getCurrentProcessInstanceId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			return JsfUtil.actionNavigateTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (JbpmException e) {
			LOGGER.debug("Error completando tarea actual del procesos inspecciones de control ambiental", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return "";
		}
	}

	public abstract Map<String, Object> generateParameters();

	public abstract String getPage();

	public boolean validate() {
		return true;
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/" + getPage());
	}
}
