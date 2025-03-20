package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityInformeTecnicoEsIA {
	
	@Getter
	@Setter
	private String numeroInforme;
	
	@Getter
	@Setter
	private String ciudad;
	
	@Getter
	@Setter
	private String fecha;
	
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String codigoProyecto;
	
	@Getter
	@Setter
	private String nombreOperador;
	
	@Getter
	@Setter
	private String nombreArea;
	
	@Getter
	@Setter
	private String ubicacion;
	
	@Getter
	@Setter
	private String direccionProyecto;
	
	@Getter
	@Setter
	private String sector;
	
	@Getter
	@Setter
	private String superficie;
	
	@Getter
	@Setter
	private String altitud;
	
	@Getter
	@Setter
	private String antecedentes;
	
	@Getter
	@Setter
	private String objetivos;
	
	@Getter
	@Setter
	private String caracteristicas;
	
	@Getter
	@Setter
	private String evaluacion;
	
	@Getter
	@Setter
	private String observaciones;
	
	@Getter
	@Setter
	private String justificacion;
	
	@Getter
	@Setter
	private String conclusionesRecomendaciones;
	
	@Getter
	@Setter
	private String nombreTecnico;
	
	@Getter
	@Setter
	private String areaTecnico;
	
	@Getter
	@Setter
	private String siglasEnte;
	
	@Getter
	@Setter
	private String nombreResponsable;
	
	@Getter
	@Setter
	private String areaResponsable;
	
}
