package ec.gob.ambiente.rcoa.participacionCiudadana.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityOficioPlanificacionObservadoPPC {
	
	@Getter
	@Setter
	private String numeroOficio;
	@Getter
	@Setter
	private String ciudadInforme;
	@Getter
	@Setter
	private String fechaInforme;
	@Getter
	@Setter
	private String asunto;
	@Getter
	@Setter
	private String nombreFacilitador;
	@Getter
	@Setter
	private String ingresoAntecedentes;
	@Getter
	@Setter
	private String nombreAutoridad;
	@Getter
	@Setter
	private String nombreEnte;
	@Getter
	@Setter
	private String siglasEnte;
	


}
