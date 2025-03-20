package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import lombok.Getter;
import lombok.Setter;

public class InformeInspeccionEntity {
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String razonSocial;
	
	@Getter
	@Setter
	private String nombreAreaProtegida;
	
	@Getter
	@Setter
	private String fechaInspeccion;
	
	@Getter
	@Setter
	private String fechaElaboracion;
	
	@Getter
	@Setter
	private String nombreTecnico;
	
	@Getter
	@Setter
	private String esZonaProteccion;
	
	@Getter
	@Setter
	private String esZonaRecuperacion;
	
	@Getter
	@Setter
	private String esZonaUsoPublico;
	
	@Getter
	@Setter
	private String esZonaUsoSostenible;
	
	@Getter
	@Setter
	private String esZonaManejo;
	
	@Getter
	@Setter
	private String detalleInforme;
	
	@Getter
	@Setter
	private String tipoPronunciamiento;
	
	@Getter
	@Setter
	private String conclusiones;
	
	@Getter
	@Setter
	private String recomendaciones;
	
	@Getter
	@Setter
	private String cargoTecnico;
	
	@Getter
	@Setter
	private String nroInforme;
	
	@Getter
	@Setter
	private String otrosResponsables;
	
}
