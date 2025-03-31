package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

import lombok.Getter;
import lombok.Setter;

public class ResultadoRevisionForestal {

	@Getter
	@Setter
	private Double promedioAreaBasal;
	
	@Getter
	@Setter
	private Double promedioVolumenT;
	
	@Getter
	@Setter
	private Double promedioVolumenC;
	
	@Getter
	@Setter
	private Double areaBasalPorH;
	
	@Getter
	@Setter
	private Double volumenTotalPorH;
	
	@Getter
	@Setter
	private Double volumenComercialPorH;
	
	
	@Getter
	@Setter
	private String nombreSitio;
	
	@Getter
	@Setter
	private String especieDnr;
	
	@Getter
	@Setter
	private String especieDmr;
	
	@Getter
	@Setter
	private String especieIvi;
	
	@Getter
	@Setter
	private Double valorDnr;
	
	@Getter
	@Setter
	private Double valorDmr;
	
	@Getter
	@Setter
	private Double valorIvi;
}
