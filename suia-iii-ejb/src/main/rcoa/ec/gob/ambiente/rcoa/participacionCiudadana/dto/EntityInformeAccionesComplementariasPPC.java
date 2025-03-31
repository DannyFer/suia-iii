package ec.gob.ambiente.rcoa.participacionCiudadana.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityInformeAccionesComplementariasPPC {
	
	// PPC ANEXO 13
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
	private String encabezadoBloqueConcesion;
	@Getter
	@Setter
	private String estiloBloqueConcesion;
	@Getter
	@Setter
	private String nombreBloqueConcesion;
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
	private String comunidadesInfluencia;
	@Getter
	@Setter
	private String documentoRevision;
	@Getter
	@Setter
	private String facilitador;
	@Getter
	@Setter
	private String tecnicoRegistro;
	@Getter
	@Setter
	private String fechaInicioPPC;
	@Getter
	@Setter
	private String fechaAperturaCentro;
	@Getter
	@Setter
	private String fechaCierreCentro;
	@Getter
	@Setter
	private String fechaAsambleaPresentacion;
	@Getter
	@Setter
	private String nombreTecnicoResponsable;
	@Getter
	@Setter
	private String fechaPresentacionInformeSistematizacion;
	@Getter
	@Setter
	private String fechaDesignacionTramiteRevision;
	@Getter
	@Setter
	private String fechaElaboracionInforme;
	@Getter
	@Setter
	private String antecedentes;
	@Getter
	@Setter
	private String evaluacionTecnica;
	@Getter
	@Setter
	private String accionesRefuerzo;
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
	private String nombreCoordinador;
	@Getter
	@Setter
	private String siglasEnte;
	@Getter
	@Setter
	private String displayMAAE;
	
}
