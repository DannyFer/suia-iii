package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CoordinadorRevisarInformeOficioObservacionesRGController extends OficioObservacionRGController {

	private static final long serialVersionUID = 8879574313687647896L;

	private final Logger LOG = Logger.getLogger(CoordinadorRevisarInformeOficioObservacionesRGController.class);


	@PostConstruct
	private void initCoordinador() throws Exception {
		getDocumentoRGBean().visualizarInforme(true);
	}
	
	@Override
	public String getDiscriminador() {
		return "coordinador";
	}

	@Override
	public void ejecutarLogicaAdicional() {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("existenObservaciones", getDocumentoRGBean().getOficioObservaciones().getRequiereCorrecciones());
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(),
					params);
		} catch (JbpmException e) {
			LOG.error("Error actualizando las variables del proceso de registro de generador", e);
		}
	}

	@Override
	public boolean validarPuedeContinuarExistenObservacionesSinCorregir() {
		boolean puedeContinuar = true;
		if (!getDocumentoRGBean().validarExistenciaObservaciones() && !getDocumentoRGBean().getOficioObservaciones().getRequiereCorrecciones())
			puedeContinuar = false;
		return puedeContinuar;
	}
	
	@Override
	public boolean validarPuedeContinuarNoExistenObservacionesSinCorregir() {
		boolean puedeContinuar = true;
		if (getDocumentoRGBean().validarExistenciaObservaciones() && getDocumentoRGBean().getOficioObservaciones().getRequiereCorrecciones())
			puedeContinuar = false;
		return puedeContinuar;
	}

	@Override
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/prevencion/registrogeneradordesechos/coordinadorRevisarInformeOficioObservaciones.jsf");
	}

}
