package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityIngresoDiagnosticoAmbiental {
	
	@Getter
	@Setter
	private String ubicacion;
	
	@Getter
	@Setter
	private String fecha;
	
	@Getter
	@Setter
	private String nombreOperador;
	
	@Getter
	@Setter
	private String numeroCI;
	
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String codigoProyecto;
	
}
