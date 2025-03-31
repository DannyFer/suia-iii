package ec.gob.ambiente.retce.model;

import lombok.Getter;
import lombok.Setter;

public class InformeTecnicoGenerador {

	@Getter
	@Setter
	private String enteResponsable;
	@Getter
	@Setter
	private String informeNumero;
	@Getter
	@Setter
	private String razonSocial;
	@Getter
	@Setter
	private String representante;
	@Getter
	@Setter
	private String proyecto;
	@Getter
	@Setter
	private String sector;
	@Getter
	@Setter
	private String autorizacion;
	@Getter
	@Setter
	private String codigoRgd;
	@Getter
	@Setter
	private String anioReporte;
	@Getter
	@Setter
	private String fechaTramite;
	@Getter
	@Setter
	private String fechaEvaluacion;
	@Getter
	@Setter
	private String evaluador;
	@Getter
	@Setter
	private String numeroTramite;
	@Getter
	@Setter
	private String antecedentesAdicionales;
	@Getter
	@Setter
	private String observaciones;
	@Getter
	@Setter
	private String conclusiones;
	@Getter
	@Setter
	private String recomendaciones;
	@Getter
	@Setter
	private String tecnicoElabora;
	@Getter
	@Setter
	private String coordinador;
	@Getter
	@Setter
	private String headerObservaciones = "";
	
	@Getter
	@Setter
	private String initObservaciones = "";
	@Getter
	@Setter
	private String headerConclusiones = "";
	@Getter
	@Setter
	private String headerRecomendaciones = "";
	@Getter
	@Setter
	private String headerElaborado = "";

}
