package ec.gob.ambiente.rcoa.participacionCiudadana.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityOficioPlanificacionAprobadoPPC {
	
	@Getter
	@Setter
	private String numeroOficio;
	@Getter
	@Setter
	private String ciudad;
	@Getter
	@Setter
	private String fechaOficio;
	@Getter
	@Setter
	private String asunto;
	@Getter
	@Setter
	private String nombreProyecto;
	@Getter
	@Setter
	private String nombreProponente;
	@Getter
	@Setter
	private String nombreCargo;
	@Getter
	@Setter
	private String nombreEmpresa;
	@Getter
	@Setter
	private String nombreFacilitador;
	@Getter
	@Setter
	private String cuerpoOficio;
	@Getter
	@Setter
	private String nombreAutoridad;
	@Getter
	@Setter
	private String nombreEnte;
	@Getter
	@Setter
	private String siglasEnte;
	@Getter
	@Setter
	private String titulo;
	
}
