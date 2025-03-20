package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class SubsecretariaRevisarRegistroInformeOficioRGController extends OficioEmisionRGController {

	private static final long serialVersionUID = 165683211928358047L;

	private final Logger LOG = Logger.getLogger(SubsecretariaRevisarRegistroInformeOficioRGController.class);

	@Getter
	@Setter
	private Boolean requiereCorrecciones;

	@PostConstruct
	private void initCoordinador() throws Exception {
		getDocumentoRGBean().visualizarInforme(true);
		getDocumentoRGBean().visualizarBorrador(true,false);
	}
	
	@Override
	public String getDiscriminador() {
		return "subsecretaria";
	}

	@Override
	public void ejecutarLogicaAdicional() {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("existenObservaciones", requiereCorrecciones);
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
					params);
		} catch (JbpmException e) {
			LOG.error("Error actualizando las variables del proceso de registro de generador", e);
		}
	}

	@Override
	public boolean validarPuedeContinuarExistenObservacionesSinCorregir() {
		boolean puedeContinuar = true;
		if (!getDocumentoRGBean().validarExistenciaObservaciones() && !requiereCorrecciones)
			puedeContinuar = false;
		return puedeContinuar;

	}

	@Override
	public boolean validarPuedeContinuarNoExistenObservacionesSinCorregir() {
		boolean puedeContinuar = true;
		if (getDocumentoRGBean().validarExistenciaObservaciones() && requiereCorrecciones)
			puedeContinuar = false;
		return puedeContinuar;
	}

	@Override
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/prevencion/registrogeneradordesechos/subsecretariaRevisarInformeOficioAprobacion.jsf");
	}
}
