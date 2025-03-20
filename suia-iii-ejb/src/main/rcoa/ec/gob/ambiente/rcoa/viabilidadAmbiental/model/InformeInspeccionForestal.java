package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the inspection_report_forest database table.
 * 
 */
@Entity
@Table(name = "inspection_report_forest", schema = "coa_viability")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "inrf_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "inrf_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "inrf_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "inrf_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "inrf_user_update")) })

@NamedQueries({ 
	@NamedQuery(name = "InformeInspeccionForestal.findAll", query = "SELECT i FROM InformeInspeccionForestal i"),
	@NamedQuery(name = InformeInspeccionForestal.GET_POR_VIABILIDAD, query = "SELECT i FROM InformeInspeccionForestal i where i.idViabilidad = :idViabilidad and i.estado = true order by id desc"),
	@NamedQuery(name = InformeInspeccionForestal.GET_POR_VIABILIDAD_REVISION, query = "SELECT i FROM InformeInspeccionForestal i where i.idViabilidad = :idViabilidad and i.estado = true and numeroRevision = :numeroRevision order by id desc"),
})
public class InformeInspeccionForestal extends EntidadAuditable implements
		Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_POR_VIABILIDAD = PAQUETE + "InformeInspeccionForestal.getPorViabilidad";
	public static final String GET_POR_VIABILIDAD_REVISION = PAQUETE + "InformeInspeccionForestal.getPorViabilidadRevision";

	// tipo de informe
	public static Integer inspeccion = 1;
	public static Integer observado = 2;
	public static Integer viabilidad = 3;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inrf_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "inrf_background")
	private String antecedentes;

	@Getter
	@Setter
	@Column(name = "inrf_conclusions")
	private String conclusiones;

	@Getter
	@Setter
	@Column(name = "inrf_ecosystem_type")
	private String tipoEcosistema;

	@Getter
	@Setter
	@Column(name = "inrf_inspection_date")
	private Date fechaInspeccion;

	@Getter
	@Setter
	@Column(name = "inrf_legal_framework")
	private String marcoLegal;

	@Getter
	@Setter
	@Column(name = "inrf_objective")
	private String objetivo;

	@Getter
	@Setter
	@Column(name = "inrf_plant_cover")
	private String tipoCoberturaVegetal;

	@Getter
	@Setter
	@Column(name = "inrf_project_implementation")
	private String areaImplantacion;

	@Getter
	@Setter
	@Column(name = "inrf_recommendations")
	private String recomendaciones;

	@Getter
	@Setter
	@Column(name = "inrf_report_number")
	private String numeroInforme;

	@Getter
	@Setter
	@Column(name = "inrf_system_report_date")
	private Date fechaElaboracion;

	@Getter
	@Setter
	@Column(name = "prvi_id")
	private Integer idViabilidad;

	@Getter
	@Setter
	@Column(name = "infr_type_report")
	private Integer tipoInforme = 1;

	@Getter
	@Setter
	@Column(name="infr_technical_name")
	private String nombreTecnico;
	
	@Getter
	@Setter
	@Column(name="infr_technical_position")
	private String cargoTecnico;
	
	@Getter
	@Setter
	@Column(name="infr_technical_area")
	private String areaTecnico;

	@Getter
	@Setter
	@Column(name="task_id")
	private Integer idTarea;

	@Getter
	@Setter
	@Column(name="infr_review_number")
	private Integer numeroRevision;

	@Getter
	@Setter
	@Column(name="infr_zoning")
	private Boolean bpTieneZonificacion;

	@Getter
	@Setter
	@Column(name="infr_pmi")
	private Boolean bpTienePlanManejo;

	@Getter
	@Setter
	@Column(name="infr_infrastructure")
	private Boolean bpTieneInfraestructura;

	@Getter
	@Setter
	@Column(name="infr_characteristics")
	private String caracteristicasInfraestructura;

	@Getter
	@Setter
	@Column(name="infr_fragile_ecosystems")
	private String detalleEcosistemasFragiles;

	@Getter
	@Setter
	@Column(name="infr_activities")
	private String descripcionActividades;
	
	@Getter
	@Setter
	@Column(name="infr_enable_sampling_census")
	private Boolean esMuestreoCenso;
	
	@Getter
	@Setter
	@Column(name="infr_enable_qualitative_characterization")
	private Boolean esCaracterizacionCualitativa;

	@Getter
	@Setter
	@Column(name="infr_enable_protective_forest_zoning")
	private Boolean habilitarZonificacionBosque;
	
	@Getter
	@Setter
	@Column(name="infr_enable_fragile_ecosystems")
	private Boolean habilitarEcosistemasFragiles;
	
	@Getter
	@Setter
	@Column(name="infr_enable_hydrographic_units")
	private Boolean habilitarUnidadesHidrograficas;
	
	@Getter
	@Setter
	@Column(name="infr_enable_activities_description")
	private Boolean habilitarDescripcionActividades;
	
	@Getter
	@Setter
	@Column(name="infr_enable_quantitative_sampling")
	private Boolean habilitarCuantitativaMuestreo;
	
	@Getter
	@Setter
	@Column(name="infr_enable_quantitative_census")
	private Boolean habilitarCuantitativaCenso;
	
	@Getter
	@Setter
	@Column(name="infr_enable_species_importance")
	private Boolean habilitarEspeciesImportancia;
	
	@Getter
	@Setter
	@Column(name="infr_enable_species_qualitative_characterization")
	private Boolean habilitarCaracterizacionCualitativa;
	
	@Getter
	@Setter
	@Column(name="infr_enable_affectations_pf")
	private Boolean habilitarAfectacionesPfn;
	
	@Getter
	@Setter
	@Transient
	private String nombreFichero;

	@Getter
	@Setter
	@Transient
	private String nombreReporte;
	
	@Getter
	@Setter
	@Transient
	private byte[] archivoInforme;
	
	@Getter
	@Setter
	@Transient
	private String informePath;

}