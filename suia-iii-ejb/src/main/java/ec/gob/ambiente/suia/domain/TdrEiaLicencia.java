package ec.gob.ambiente.suia.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the tdr_eia_license database table.
 * 
 */
@NamedQueries({
		@NamedQuery(name = "TdrEiaLicencia.findAll", query = "SELECT t FROM TdrEiaLicencia t"),
		@NamedQuery(name = TdrEiaLicencia.FIND_BY_PROJECT, query = "SELECT t FROM TdrEiaLicencia t WHERE t.proyecto.id = :idProyecto") })
@Entity
@Table(name = "tdr_eia_license", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "tdel_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tdel_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tdel_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tdel_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tdel_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tdel_status = 'TRUE'")
public class TdrEiaLicencia extends EntidadAuditable {

	private static final long serialVersionUID = 1L;
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.TdrEiaLicencia.findAll";
	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.TdrEiaLicencia.findByProject";
	public static final String SEQUENCE_CODE = "seq_tdr_eia_code";
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "TDREIALICENSE_TDELID_GENERATOR", sequenceName = "seq_tdel_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TDREIALICENSE_TDELID_GENERATOR")
	@Column(name = "tdel_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "cons_id")
	private Integer consId;

	@Getter
	@Setter
	@Column(name = "project_id")
	private String projectId;

	@Getter
	@Setter
	@Column(name = "tdel_alternatives_analysis")
	private String tdelAlternativesAnalysis;

	@Getter
	@Setter
	@Column(name = "tdel_capital_contributions")
	private String tdelCapitalContributions;

	@Getter
	@Setter
	@Column(name = "tdel_characteristics_project")
	private String tdelCharacteristicsProject;

	@Getter
	@Setter
	@Column(name = "tdel_definition_area_influence")
	private String tdelDefinitionAreaInfluence;

	@Getter
	@Setter
	@Column(name = "tdel_detail_economic_aspects")
	private String tdelDetailEconomicAspects;

	@Getter
	@Setter
	@Column(name = "tdel_environmental_management_plan")
	private String tdelEnvironmentalManagementPlan;

	@Getter
	@Setter
	@Column(name = "tdel_execution_time")
	private Integer tdelExecutionTime;

	@Getter
	@Setter
	@Column(name = "tdel_general_goals")
	private String tdelGeneralGoals;

	@Getter
	@Setter
	@Column(name = "tdel_impact_assessment_methodology")
	private String tdelImpactAssessmentMethodology;

	@Getter
	@Setter
	@Column(name = "tdel_identification_methodology")
	private String tdelIdentificationMethodology;

	@Getter
	@Setter
	@Column(name = "tdel_background")
	private String tdelBackground;

	@Getter
	@Setter
	@Column(name = "tdel_mapping")
	private String tdelMapping;

	@Getter
	@Setter
	@Column(name = "tdel_methodology_forest")
	private String tdelMethodologyForest;

	@Getter
	@Setter
	@Column(name = "tdel_monitoring_plan")
	private String tdelMonitoringPlan;

	@Getter
	@Setter
	@Column(name = "tdel_project_description")
	private String tdelProjectDescription;

	@Getter
	@Setter
	@Column(name = "tdel_referential_influence_area")
	private String tdelReferentialInfluenceArea;

	@Getter
	@Setter
	@Column(name = "tdel_required_contributions")
	private Boolean tdelRequiredContributions = false;

	@Getter
	@Setter
	@Column(name = "tdel_required_forest_inventory")
	private Boolean tdelRequiredForestInventory = false;

	@Getter
	@Setter
	@Column(name = "tdel_required_passive_environmental")
	private Boolean tdelRequiredPassiveEnvironmental = false;

	@Getter
	@Setter
	@Column(name = "tdel_specifics_goals")
	private String tdelSpecificsGoals;

	@Getter
	@Setter
	@Column(name = "tdel_surface_area")
	private Double tdelSurfaceArea;

	@Getter
	@Setter
	@Column(name = "tdel_type_inputs")
	private String tdelTypeInputs;

	@Getter
	@Setter
	@Column(name = "tdel_other_thematic_maps")
	private String tdelOtherThematicMaps;

	@Getter
	@Setter
	@Column(name = "tdel_technical_scope")
	private String tdelTechnicalScope;

	@Getter
	@Setter
	@Column(name = "tdel_determination_reference_area")
	private String tdelDeterminationReferenceArea;

	@Getter
	@Setter
	@Column(name = "tdel_methodology_socioeconomic_cultural")
	private String tdelMethodologySocioeconomicCultural;

	@Getter
	@Setter
	@Column(name = "tdel_methodology_physical_abiotic")
	private String tdelMethodologyPhysicalAbiotic;

	@Getter
	@Setter
	@Column(name = "tdel_methodology_biotic")
	private String tdelMethodologyBiotic;

	@Getter
	@Setter
	@Column(name = "tdel_general_methodology")
	private String tdelGeneralMethodology;

	@Getter
	@Setter
	@Column(name = "tdel_administrative_framework")
	private String tdelAdministrativeFramework;

	@Getter
	@Setter
	@Column(name = "tdel_date_add")
	private Date tdelDateAdd;

	@Getter
	@Setter
	@Column(name = "tdel_methodology_influence_area")
	private String tdelMethodologyInfluenceArea;

	@Getter
	@Setter
	@Column(name = "tdel_methodology_direct_influence_area")
	private String tdelMethodologyDirectInfluenceArea;

	@Getter
	@Setter
	@Column(name = "tdel_methodology_indirect_social_influence_area")
	private String tdelMethodologyIndirectSocialInfluenceArea;

	@Getter
	@Setter
	@Column(name = "tdel_sensitive_areas")
	private String tdelSensitiveAreas;

	@Getter
	@Setter
	@Column(name = "tdel_methodology_physical_sensitivity")
	private String tdelMethodologyPhysicalSensitivity;

	@Getter
	@Setter
	@Column(name = "tdel_methodology_sensitivity_biotic")
	private String tdelMethodologySensitivityBiotic;

	@Getter
	@Setter
	@Column(name = "tdel_methodology_sensitivity_socioeconomic")
	private String tdelMethodologySensitivitySocioeconomic;

	@Getter
	@Setter
	@Column(name = "tdel_risk_analysis_methodology")
	private String tdelRiskAnalysisMethodology;

	@Getter
	@Setter
	@Column(name = "tdel_assessment_environmental_services")
	private String tdelAssessmentEnvironmentalServices;

	@Getter
	@Setter
	@Column(name = "tdel_identification_contaminates_sites")
	private String tdelIdentificationContaminatesSites;

	@Getter
	@Setter
	@Column(name = "tdel_other_annexes")
	private String tdelOtherAnnexes;

	@Getter
	@Setter
	@Column(name = "user_id_add")
	private Long userIdAdd;

	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_tdr_eia_license_pren_id_project_envirolment_licensing_pren_i")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	@Setter
	@OneToMany(mappedBy = "tdrEiaLicencia")
	private List<ProyectoGeneralCatalogo> proyectoGeneralCatalogo;
	
	@Getter
	@Setter
	@Column(name="tdel_number_facilitators")
	private Integer numeroFacilitadores;

	//
	// // bi-directional many-to-one association to LegalFrameworkLicense
	// @OneToMany(mappedBy = "tdrEiaLicense")
	// private List<LegalFrameworkLicense> legalFrameworkLicenses;
	//
	// bi-directional many-to-one association to TechnicalTeam
	@Getter
	@Setter
	@OneToMany(mappedBy = "tdrEiaLicencia")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tete_status = 'TRUE'")
	private List<EquipoTecnico> equipoTecnico;

	//
	// // bi-directional many-to-one association to ThematicMap
	// @OneToMany(mappedBy = "tdrEiaLicense")
	// private List<ThematicMap> thematicMaps;
	//
	// // bi-directional many-to-one association to ProjectAnnexe
	// @OneToMany(mappedBy = "tdrEiaLicense")
	// private List<ProjectAnnexe> projectAnnexes;
	//
	// // bi-directional many-to-one association to ProjectBlock
	// @OneToMany(mappedBy = "tdrEiaLicense")
	// private List<ProjectBlock> projectBlocks;
	//
	// // bi-directional many-to-one association to Coordinate
	// @OneToMany(mappedBy = "tdrEiaLicense")
	// private List<Coordinate> coordinates;
	//
	// public TdrEiaLicencia() {
	// }
	//
}