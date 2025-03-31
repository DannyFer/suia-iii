package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;

@Stateless
public class EspecieValorImportanciaBean {
	
	@Getter
	@Setter
	private HigherClassification familiaEspecie;

	@Getter
	@Setter
	private HigherClassification generoEspecie;

	@Getter
	@Setter
	private SpecieTaxa especie;
	
	@Getter
	@Setter
	private String nombreComun;

	@Getter
	@Setter
	private Integer frecuencia;	
	
	@Getter
	@Setter
	private Double areaBasal;
	
	@Getter
	@Setter 
	private Double dnr;
	
	@Getter
	@Setter
	private Double dmr;
	
	@Getter
	@Setter
	private Double ivi;
	

}
