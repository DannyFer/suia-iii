package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name = "scout_drilling", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "scdr_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "scdr_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "scdr_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "scdr_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "scdr_user_update"))})
@Filter(name = EntidadAuditable.FILTER_ACTIVE, condition = "scdr_status = 'TRUE'")
public class PerforacionExplorativa extends EntidadAuditable{

	private static final long serialVersionUID = -6702663610906558834L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="scdr_id")
	@Getter
    @Setter
	private Integer id;	
	
    @ManyToOne
    @JoinColumn(name = "pren_id")
    @ForeignKey(name = "pren_id_fk")
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

    @ManyToOne
    @JoinColumn(name = "cons_id")
    @ForeignKey(name = "cons_id_fk")
    @Getter
    @Setter    
    private Consultor Consultor;
    
    @Getter
	@Setter
	@Column(name = "scdr_project_execution_permit")
	private String ProjectExecutionPermit;
    
    @Getter
	@Setter
	@Column(name = "scdr_intersection_certificate")
	private String intersectionCertificate;
    
    @Getter
	@Setter
	@Column(name = "scdr_feasibility_certificate")
	private String feasibilityCertificate;
    @Getter
	@Setter
	@Column(name = "scdr_previous_environmental_permits")
	private String previousEnvironmentalPermits;
    @Getter
	@Setter
	@Column(name = "scdr_project_summary")
	private String projectSummary;
    @Getter
	@Setter
	@Column(name = "scdr_intervened_areas")
	private String intervenedAreas;
    @Getter
	@Setter
	@Column(name = "scdr_exploration_techniques")
	private String explorationTechniques;
    @Getter
	@Setter
	@Column(name = "scdr_project_closure_stages")
	private String projectClosureStages;
    @Getter
   	@Setter
   	@Column(name = "scdr_infrastructure_complementary_activities")
   	private String infrastructureComplementaryActivities;
    @Getter
   	@Setter
   	@Column(name = "scdr_accesses_trails")
   	private String accessesTrails;
    @Getter
   	@Setter
   	@Column(name = "scdr_temporary_infrastructure_implementation")
   	private String temporaryInfrastructureImplementation;
    @Getter
   	@Setter
   	@Column(name = "scdr_temporary_camps")
   	private String temporaryCamps;
    @Getter
   	@Setter
   	@Column(name = "scdr_qualified_hand_work")
   	private String qualifiedHandWork;
    @Getter
   	@Setter
   	@Column(name = "scdr_base_flow_diagram")
   	private String baseFlowDiagram;
    @Getter
   	@Setter
   	@Column(name = "scdr_water_human_consumption")
   	private Boolean waterHumanConsumption;
    @Getter
   	@Setter
   	@Column(name = "scdr_water_drilling")
   	private Boolean waterDrilling;
    @Getter
   	@Setter
   	@Column(name = "scdr_electric_power")
   	private Boolean electricPower;
    @Getter
   	@Setter
   	@Column(name = "scdr_fuel_consumption")
   	private Boolean fuelConsumption;
    @Getter
   	@Setter
   	@Column(name = "scdr_water_consumption")
   	private String waterConsumption;
    @Getter
   	@Setter
   	@Column(name = "scdr_water_consumption_drilling")
   	private String waterConsumptionDrilling;
    @Getter
   	@Setter
   	@Column(name = "scdr_electric_power_consumption")
   	private String electricPowerConsumption;
    @Getter
   	@Setter
   	@Column(name = "scdr_fuel_type_consumption")
   	private String fuelTypeConsumption;
    @Getter
   	@Setter
   	@Column(name = "scdr_direct_physical_influence")
   	private String directPhysicalInfluence;
    @Getter
   	@Setter
   	@Column(name = "scdr_direct_biotic_influence")
   	private String directBioticInfluence;
    @Getter
   	@Setter
   	@Column(name = "scdr_direct_social_influence")
   	private String directSocialInfluence;
    @Getter
   	@Setter
   	@Column(name = "scdr_indirect_physical_influence")
   	private String indirectPhysicalInfluence;
    @Getter
   	@Setter
   	@Column(name = "scdr_indirect_biotic_influence")
   	private String indirectSocialInfluence; 
    @Getter
   	@Setter
   	@Column(name = "scdr_indirect_social_influence")
   	private String indirectBioticInfluence; 
    @Getter
   	@Setter
   	@Column(name = "scdr_legal_framework")
   	private Boolean legalFramework;
    @Getter
   	@Setter
   	@Column(name = "scdr_code_update")
   	private String codeUpdate;
    @Getter
   	@Setter
   	@Column(name = "scdr_finalized")
   	private Boolean finalized;
    @Getter
   	@Setter
   	@Column(name = "scdr_approve_technical")
   	private Boolean approveTechnical;
    @Getter
   	@Setter
   	@Column(name = "scdr_total_pma")
   	private Double totalPma;
    @Getter
   	@Setter
   	@Column(name = "scdr_judicial_locker")
   	private String judicialLocker;
    @Getter
   	@Setter
   	@Column(name = "scdr_technical_observation")
   	private String technicalObservation;
	@Getter
	@Setter
	@Column(name="scdr_environmental_record_review")
	private Boolean revisionFichaAmbiental;
    
    @ManyToOne
    @JoinColumn(name = "prco_id")
    @ForeignKey(name = "prco_id_fk")
    @Getter
    @Setter
    private ProyectoLicenciaCoa proyectoLicenciaCoa;
}