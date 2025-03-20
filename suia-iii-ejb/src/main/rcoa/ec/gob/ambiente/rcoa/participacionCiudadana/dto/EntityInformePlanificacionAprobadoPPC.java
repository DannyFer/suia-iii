package ec.gob.ambiente.rcoa.participacionCiudadana.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityInformePlanificacionAprobadoPPC {
	
	@Getter
	@Setter
	private String enteResponsable;
	@Getter
	@Setter
	private String numeroInforme;
	@Getter
	@Setter
	private String nombreProyecto;
	@Getter
	@Setter
	private String encabezadoBloqueConcesion;
	@Getter
	@Setter
	private String nombreBloqueConcesion;
	@Getter
	@Setter
	private String estiloBloqueConcesion;
	@Getter
	@Setter
	private String fase;
	@Getter
	@Setter
	private String areaIntervencion;
	@Getter
	@Setter
	private String nombreOperador;
	@Getter
	@Setter
	private String nombreRepresentante;
	@Getter
	@Setter
	private String codigoProyecto;
	@Getter
	@Setter
	private String provincia;
	@Getter
	@Setter
	private String canton;
	@Getter
	@Setter
	private String parroquia;
	@Getter
	@Setter
	private String comunidades;
	@Getter
	@Setter
	private String facilitador;
	@Getter
	@Setter
	private String numeroRegistro;
	@Getter
	@Setter
	private String fechaVisitaPrevia;
	@Getter
	@Setter
	private String pobladosVisitados;
	@Getter
	@Setter
	private String fechaInformePlanificacion;
	@Getter
	@Setter
	private String fechaAsiganacionTecnicoApoyo;
	@Getter
	@Setter
	private String fechaElaboracionTecnico;
	@Getter
	@Setter
	private String antecedentes;
	@Getter
	@Setter
	private String conclusiones;
	@Getter
	@Setter
	private String recomendaciones;
	@Getter
	@Setter
	private String nombreTecnico;
	@Getter
	@Setter
	private String nombreCoordinador;
	@Getter
	@Setter
	private String nombreEnte;
	@Getter
	@Setter
	private String siglasEnte;
	@Getter
	@Setter
	private String analisisTecnico;
	@Getter
	@Setter
	private String displayMAAE;
}
