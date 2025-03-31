package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade.InspeccionControlAmbientalFacade;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.SolicitudInspeccionControlAmbiental;
import ec.gob.ambiente.suia.domain.SolicitudInspeccionRespuestaAclaratoria;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;

@ManagedBean
@ViewScoped
public class SujetoControlIngresarRespuestasAclaratoriasController implements Serializable {

	private static final long serialVersionUID = -258676130404353282L;

	private static final Logger LOG = Logger.getLogger(SujetoControlIngresarRespuestasAclaratoriasController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private InspeccionControlAmbientalFacade inspeccionControlAmbientalFacade;

	@Getter
	private SolicitudInspeccionControlAmbiental solicitud;

	private int contadorBandejaTecnico;

	@Getter
	private SolicitudInspeccionRespuestaAclaratoria solicitudInspeccionRespuestaAclaratoria;

	@PostConstruct
	private void init() {
		try {
			Integer idSolicitud = Integer.parseInt(JsfUtil.getBean(BandejaTareasBean.class).getTarea()
					.getVariable(SolicitudInspeccionControlAmbiental.VARIABLE_ID_SOLICITUD).toString());

			solicitud = inspeccionControlAmbientalFacade.get(idSolicitud);

			if (JsfUtil.getRequestParameter("ingresar") != null
					&& JsfUtil.getRequestParameter("ingresar").equals("ingresar")) {
				Object contadorBandejaTecnicoValue = JsfUtil.getCurrentTask()
						.getVariable(GeneradorDesechosPeligrosos.VARIABLE_CANTIDAD_OBSERVACIONES);
				if (contadorBandejaTecnicoValue == null || contadorBandejaTecnicoValue.toString().isEmpty()) {
					contadorBandejaTecnico = 0;
				} else {
					try {
						contadorBandejaTecnico = Integer.parseInt(contadorBandejaTecnicoValue.toString());
					} catch (Exception e) {
						LOG.error("Error recuperado cantidad de observaciones en registro de generador", e);
					}
				}

				solicitudInspeccionRespuestaAclaratoria = inspeccionControlAmbientalFacade.getRespuesta(solicitud,
						contadorBandejaTecnico);
				if (solicitudInspeccionRespuestaAclaratoria == null)
					solicitudInspeccionRespuestaAclaratoria = new SolicitudInspeccionRespuestaAclaratoria();
			}
		} catch (Exception e) {
			LOG.error("Error al recuperar los datos de la inspeccion.", e);
		}
	}

	public void uploadListener(FileUploadEvent event) {

	}

	public String aceptar() {
		try {
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(),
					JsfUtil.getCurrentProcessInstanceId(), null);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			return JsfUtil.actionNavigateTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (JbpmException e) {
			LOG.debug("Error completando tarea actual del procesos inspecciones de control ambiental", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return "";
		}
	}

	public String siguiente() {
		return JsfUtil.actionNavigateTo(
				"/control/inspeccionescontrolambiental/sujetoControlIngresarRespuestasAclaratoriasIngresar.jsf",
				"ingresar=ingresar");
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/sujetoControlIngresarRespuestasAclaratorias.jsf");
	}
}
