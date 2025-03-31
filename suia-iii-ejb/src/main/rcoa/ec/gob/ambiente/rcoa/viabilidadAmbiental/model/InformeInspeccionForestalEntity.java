package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import lombok.Getter;
import lombok.Setter;

public class InformeInspeccionForestalEntity {
	
	@Getter
	@Setter
	private String nroInforme;
	
	@Getter
	@Setter
	private String fechaInspeccion;
	
	@Getter
	@Setter
	private String fechaElaboracion;
	
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String razonSocial;
	
	@Getter
	@Setter
	private String intersecaProyecto;
	
	@Getter
	@Setter
	private String delegadosOperador;
	
	@Getter
	@Setter
	private String unidadResponsable;
	
	@Getter
	@Setter
	private String nombreUnidadResponsable;
	
	@Getter
	@Setter
	private String equipoTecnico;
	
	@Getter
	@Setter
	private String antecedentes;
	
	@Getter
	@Setter
	private String marcoLegal;
	
	@Getter
	@Setter
	private String objetivo;
	
	@Getter
	@Setter
	private String tablaCoordenadas;
	
	@Getter
	@Setter
	private String fotografiasTipoCobertura;
	
	@Getter
	@Setter
	private String detalleTipoCobertura;
	
	@Getter
	@Setter
	private String detalleTipoEcosistema;
	
	@Getter
	@Setter
	private String fotografiasTipoEcosistema;
	
	@Getter
	@Setter
	private String detalleAreaImplantacion;
	
	@Getter
	@Setter
	private String fotografiasAreaImplantacion;
	
	@Getter
	@Setter
	private String conclusiones;
	
	@Getter
	@Setter
	private String recomendaciones;
	
	@Getter
	@Setter
	private String nombreTecnico;
	
	//viabilidad v2
	
	@Getter
	@Setter
	private String tipoInforme;
	
	@Getter
	@Setter
	private String codigoProyecto;
	
	@Getter
	@Setter
	private String ciudad;
	
	@Getter
	@Setter
	private String superficieProyecto;
	
	@Getter
	@Setter
	private String ubicacionProyecto;
	
	@Getter
	@Setter
	private String nombreTecnicoRevision;
	
	@Getter
	@Setter
	private String cargoTecnicoRevision;
	
	@Getter
	@Setter
	private String areaTecnicoRevision;
	
	@Getter
	@Setter
	private String anexos;
	
	@Getter
	@Setter
	private String verificacionRecursoForestal;
	
	@Getter
	@Setter
	private String fotografiasRecorrido;
	
	@Getter
	@Setter
	private String areaInterseccion;
	
	@Getter
	@Setter
	private String zonificacionBosque;
	
	@Getter
	@Setter
	private String detalleZonificacionBosque;
	
	@Getter
	@Setter
	private String ecosistemasProyecto;
	
	@Getter
	@Setter
	private String detalleEcosistemasFragiles;
	
	@Getter
	@Setter
	private String unidadesHidrograficas;
	
	@Getter
	@Setter
	private String afectacionConvenios;
	
	@Getter
	@Setter
	private String descripcionActividades;
	
	@Getter
	@Setter
	private String datosCuantitativaMuestreo;
	
	@Getter
	@Setter
	private String datosCuantitativaCenso;
	
	@Getter
	@Setter
	private String especiesImportancia;
	
	@Getter
	@Setter
	private String caracterizacionCualitativa;
	
	@Getter
	@Setter
	private String afectacionPatrimonioForestal;
	
	@Getter
	@Setter
	private String displayZonificacion = "none";
	
	@Getter
	@Setter
	private String displayEcosistemasFragiles = "none";
	
	@Getter
	@Setter
	private String displayUnidadesHidrograficas = "none";
	
	@Getter
	@Setter
	private String displayDescripcionActividades = "none";
	
	@Getter
	@Setter
	private String displayCuantitativaMuestreo = "none";
	
	@Getter
	@Setter
	private String displayCuantitativaCenso = "none";
	
	@Getter
	@Setter
	private String displayEspeciesImportancia = "none";

	@Getter
	@Setter
	private String displayCaracterizacionCualitativa = "none";
	
	@Getter
	@Setter
	private String displayAfectacionPfn = "none";

	@Getter
	@Setter
	private String displayDatosInspeccion = "none";
	
	@Getter
	@Setter
	private String displayNoInspeccion = "none";	
}
