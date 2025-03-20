package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class CoordinadorRevisarInformeOficioICAController extends OficioPronunciamientoICAController {

	private static final long serialVersionUID = -5705979046585025967L;

	private final Logger LOG = Logger.getLogger(CoordinadorRevisarInformeOficioICAController.class);

	@Getter
	@Setter
	private Boolean requiereCorrecciones;

	@PostConstruct
	private void initCoordinador() throws Exception {
		getDocumentoICABean().visualizarInforme(true);
	}

	@Override
	public String getDiscriminador() {
		return "coordinador";
	}

	@Override
	public void ejecutarLogicaAdicional() {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("existenObservaciones", requiereCorrecciones);
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
					params);
		} catch (JbpmException e) {
			LOG.error("Error actualizando las variables del proceso inspecciones de control ambiental", e);
		}
	}

	@Override
	public boolean validarPuedeContinuarExistenObservacionesSinCorregir() {
		boolean puedeContinuar = true;
		if (!getDocumentoICABean().validarExistenciaObservaciones() && !requiereCorrecciones)
			puedeContinuar = false;
		return puedeContinuar;

	}

	@Override
	public boolean validarPuedeContinuarNoExistenObservacionesSinCorregir() {
		boolean puedeContinuar = true;
		if (getDocumentoICABean().validarExistenciaObservaciones() && requiereCorrecciones)
			puedeContinuar = false;
		return puedeContinuar;
	}

	@Override
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/coordinadorRevisarInformeOficioPronunciamiento.jsf");
	}
}
