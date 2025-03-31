package ec.gob.ambiente.rcoa.participacionCiudadana.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityInformeSistematizacionObservadoPPC {
	
	@Getter
	@Setter
	private String enteResponsable;
	@Getter
	@Setter
	private String numeroInforme;
	@Getter
	@Setter
	private String codigoProyecto;
	@Getter
	@Setter
	private String nombreProyecto;
	@Getter
	@Setter
	private String informacionTecnico;
	@Getter
	@Setter
	private String fechaElaboracion;
	@Getter
	@Setter
	private String	nombreConcesion;
	@Getter
	@Setter
	private String estiloBloqueConcesion;
	@Getter
	@Setter
	private String fase;	
	@Getter
	@Setter
	private String superficie;
	@Getter
	@Setter
	private String nombreOperador;
	@Getter
	@Setter
	private String nombreRepresentante;
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
	private String siglasEnte;
	
	@Getter
	@Setter
	private String numeroRegistroCalificacion;
	
	@Getter
	@Setter
	private String antecedentes;
	
	@Getter
	@Setter
	private String observacion;
	@Getter
	@Setter
	private String conclusion;
	
	@Getter
	@Setter
	private String recomendacion;
	
	@Getter
	@Setter
	private String nombreTecnico;
	
	@Getter
	@Setter
	private String nombreEnte;
	
	@Getter
	@Setter
	private String fechaApertura;
	@Getter
	@Setter
	private String fechaCierre;
	
	@Getter
	@Setter
	private String fechaPresentacion;
	@Getter
	@Setter
	private String nombreResponsable;
	@Getter
	@Setter
	private String fechaPresentacionSistematizacion;
	
	@Getter
	@Setter
	private String fechaDesignacion;
	
	@Getter
	@Setter
	private String documentoRevision;
	@Getter
	@Setter
	private String fechaInicioPPC;
	@Getter
	@Setter
	private String displayMAAE;
	@Getter
	@Setter
	private String nombreCoordinador;
}
