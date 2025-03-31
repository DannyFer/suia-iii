package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import lombok.Getter;
import lombok.Setter;

public class FotografiaBean {
	
	@Getter
	@Setter
	private String descripcion;
	
	@Getter
	@Setter
	private DocumentoInventarioForestal fotografia;
	
	@Getter
	@Setter
	private String url;

}
