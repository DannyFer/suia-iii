package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class TecnicoResposableRGController implements Serializable {

	private static final long serialVersionUID = 2819951491085498041L;

	private static final Logger LOGGER = Logger.getLogger(TecnicoResposableRGController.class);

	@Getter
	private GeneradorDesechosPeligrosos generador;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ConexionBpms conexionBpms;

	@Getter
	private boolean responsabilidadExtendida;

	@Getter
	@Setter
	private boolean esApoyoRequerido = false;

	@PostConstruct
	private void init() {
		try {
			Integer idGenerador = Integer.parseInt(JsfUtil.getBean(BandejaTareasBean.class).getTarea()
					.getVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR).toString());

//			generador = registroGeneradorDesechosFacade.get(idGenerador, JsfUtil.getLoggedUser().getArea().getId());
			generador = registroGeneradorDesechosFacade.get(idGenerador);

			responsabilidadExtendida = Boolean.parseBoolean(JsfUtil.getBean(BandejaTareasBean.class).getTarea()
					.getVariable(GeneradorDesechosPeligrosos.VARIABLE_RESPONSABILIDAD_EXTENDIDA).toString());
		} catch (Exception e) {
			LOGGER.error("Error al recuperar los datos del registro de generador.", e);
		}
	}

	public String aceptar() {
		try {
			procesoFacade.reasignarTarea(JsfUtil.getLoggedUser(), 
					JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskId(),
					JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskSummary().getActualOwner().getId(), 
					JsfUtil.getLoggedUser().getNombre(),
					conexionBpms.deploymentId(JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskId(), "S"));
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(GeneradorDesechosPeligrosos.VARIABLE_APOYO_REQUERIDO, esApoyoRequerido);
			params.put(GeneradorDesechosPeligrosos.VARIABLE_RESPONSABILIDAD_EXTENDIDA, (generador.getResponsabilidadExtendida()!=null && generador.getResponsabilidadExtendida()?true:false));
			params.put("tecnico", JsfUtil.getLoggedUser().getNombre());
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getBean(BandejaTareasBean.class).getProcessId(), params);
			
			procesoFacade.buscarAprobarActualTareaProceso(JsfUtil.getLoggedUser(), JsfUtil.getBean(BandejaTareasBean.class).getProcessId(), params);
			procesoFacade.envioSeguimientoRGD(JsfUtil.getLoggedUser(), JsfUtil.getBean(BandejaTareasBean.class).getProcessId());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			return JsfUtil.actionNavigateTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (JbpmException e) {
			LOGGER.debug("Error completando tarea de pago del proceso Registro de generador", e);
			LOGGER.error("Error completando tarea de pago del proceso Registro de generador", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return "";
		}
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getBean(BandejaTareasBean.class).getTarea(),
				"/prevencion/registrogeneradordesechos/tecnicoResponsable.jsf");
	}
}
