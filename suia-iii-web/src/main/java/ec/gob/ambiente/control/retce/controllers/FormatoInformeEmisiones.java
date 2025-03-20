package ec.gob.ambiente.control.retce.controllers;

import lombok.Getter;
import lombok.Setter;

public class FormatoInformeEmisiones {
	
	@Getter
	@Setter
	private String codigoInforme, razon_social, representante_legal, nombre_proyecto, sector_proyecto, fase_proyecto,
		periodo_medicion, frecuencia_monitoreo, fecha_ingreso, fecha_evaluacion, tecnico_evaluador, numero_tramite;
	
	@Getter
	@Setter
	private String texto_observacion, puntos_monitoreo, analisis_tablas,
	datos_laboratorios, conclusiones_recomendaciones, nombreElabora, nombreRevisa, enteResponsable;
	
	@Getter
	@Setter
	private String workspace;
	
	
}
