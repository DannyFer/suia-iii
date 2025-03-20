package ec.gob.ambiente.rcoa.viabilidadAmbientalSnap.controllers;

import lombok.Getter;
import lombok.Setter;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;

public class DocumentosViabilidad {

	@Getter
	@Setter
	private Integer tipo;
	
	@Getter
	@Setter
	private DocumentoViabilidad documento;
	
	@Getter
	@Setter
	private Boolean descargada = false;
}
