package ec.gob.ambiente.rcoa.participacionCiudadana.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityOficioPPCBypass {
	
	@Getter
	@Setter
	private String numero;
	
	@Getter
	@Setter
	private String ciudad;
	
	@Getter
	@Setter
	private String fechaEmision;
	
	@Getter
	@Setter
	private String tratamientoOperador;
	
	@Getter
	@Setter
	private String nombreOperador;

	@Getter
	@Setter
	private String asunto;

	@Getter
	@Setter
	private String antecedentes;
	
	@Getter
	@Setter
	private String conclusiones;
	
	@Getter
	@Setter
	private String cargoOperador;
	
	@Getter
	@Setter
	private String nombreEmpresa;
	
	@Getter
	@Setter
	private String displayCargo;
	
	@Getter
	@Setter
	private String displayEmpresa;
	
	@Getter
	@Setter
	private String nombreResponsable;
	
	@Getter
	@Setter
	private String areaResponsable;
	
	@Getter
	@Setter
	private String siglasEnte;
	
}
