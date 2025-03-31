package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "mining_enviromental_record", schema = "suia_iii")
@NamedQueries({
    @NamedQuery(name = FichaAmbientalMineria.LISTAR_POR_PROYECTO, query = "SELECT f FROM FichaAmbientalMineria f WHERE f.idProyecto = :idProyecto AND f.estado = true and idRegistroOriginal = null")
})
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "mien_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "mien_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "mien_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mien_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mien_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mien_status = 'TRUE'")
public class FichaAmbientalMineria extends EntidadAuditable {

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_PROYECTO = PAQUETE + "FichaAmbientalMineria.listarPorProyecto";
    private static final long serialVersionUID = 1154755965481020655L;

    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "MINING_ENVIROMENTAL_RECORD_MIENID_GENERATOR", sequenceName = "seq_mien_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MINING_ENVIROMENTAL_RECORD_MIENID_GENERATOR")
    @Column(name = "mien_id")
    private Integer id;

    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    @Column(name = "mien_permit_issuance_date")
    private Date fechaEmisionPermiso;

    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    @Column(name = "mien_contract_registration_date")
    private Date fecharegistroContrato;

    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    @Column(name = "mien_record_presentation_date")
    private Date fechaPresentacionFichaAmbiental;

    @Getter
    @Setter
    @Column(name = "mien_duration_permission")
    private Integer duracionPermiso;

    @Getter
    @Setter
    @Column(name = "mien_duration_contract")
    private Integer duracionContrato;

    @Getter
    @Setter
    @Column(name = "mien_observations")
    private String observaciones;

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "pren_id")
    @ForeignKey(name = "fk_mining_enviromental_record_pren_id_project_envirolment_licensing_pren_id")
    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

    @Getter
    @Setter
    @Column(name = "pren_id", insertable = false, updatable = false)
    private Integer idProyecto;

    @ManyToOne
    @JoinColumn(name = "maty_id")
    @ForeignKey(name = "fk_mining_enviromental_record_maty_id_material_types_maty_id")
    @Getter
    @Setter
    private TipoMaterial tipoMaterial;

    @Getter
    @Setter
    @Column(name = "mien_material_type_catalog", length = 500)
    private String catalogoTipoMaterial;

    @ManyToOne
    @JoinColumn(name = "gcph_property_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcph_id_property_general_catalog_physical_gcph_id_property")
    @Getter
    @Setter
    private CatalogoGeneralFisico predio;

    @ManyToOne
    @JoinColumn(name = "gcph_stage_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcph_stage_id_general_catalog_physical_gcph_stage_id")
    @Getter
    @Setter
    private CatalogoGeneralFisico etapa;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcbi_vegetal_id", referencedColumnName = "gcbi_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcbi_vegetal_id_general_catalog_biotic_gcbi_id")
    private CatalogoGeneralBiotico formacionVegetal;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcbi_habitat_id", referencedColumnName = "gcbi_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcbi_habitat_id_general_catalog_biotic_gcbi_id")
    private CatalogoGeneralBiotico habitat;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcbi_forestType_id", referencedColumnName = "gcbi_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcbi_forestType_id_general_catalog_biotic_gcbi_id")
    private CatalogoGeneralBiotico tipoBosque;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcbi_degreeIntervention_id", referencedColumnName = "gcbi_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcbi_degreeIntervention_id_general_catalog_biotic_gcbi_id")
    private CatalogoGeneralBiotico gradoIntervencion;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcbi_zoogeographic_id", referencedColumnName = "gcbi_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcbi_zoogeographic_id_general_catalog_biotic_gcbi_id")
    private CatalogoGeneralBiotico pisoZoogeografico;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_area_influence_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_area_influence_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial nivelAreaInfluencia;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_population_size_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_population_size_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial tamanioPoblacion;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_water_supply_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_water_supply_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial abastecimientoAgua;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_wastewater_evacuation_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_wastewater_evacuation_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial evacuacionAguasServidas;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_rainwater_evacuation_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_rainwater_evacuation_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial evacuacionAguasLluvia;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_solid_waste_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_solid_waste_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial desechosSolidos;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_electrification_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_electrification_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial electrificacion;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_public_transport_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_public_transport_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial transportePublico;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_roads_accesses_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_roads_accesses_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial vialidadYAcceso;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_telephony_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_telephony_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial telefonia;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_land_use_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_land_use_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial aprovechamientoTierra;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_land_tenure_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_land_tenure_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial tenenciaTierra;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_social_organization_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_social_organization_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial organizacionSocial;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_landscape_tourism_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_landscape_tourism_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial paisajeTurismo;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_landslide_hazard_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_landslide_hazard_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial peligroDeslizamiento;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_flood_hazard_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_flood_hazard_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial peligroInundacion;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_earthquake_hazard_id", referencedColumnName = "gcso_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcso_earthquake_hazard_id_general_catalog_social_gcso_id")
    private CatalogoGeneralSocial peligroTerremoto;

    @Getter
    @Setter
    @Column(name = "mien_other_property")
    private String otrosPredios;

    @Getter
    @Setter
    @Column(name = "mien_description_source_water_pollution", length = 500)
    private String descripcionFuentesContaminacionAgua;

    @Getter
    @Setter
    @Column(name = "mien_description_noise_sources", length = 500)
    private String descripcionFuentesRuido;

    @Getter
    @Setter
    @Column(name = "gcph_property_id", insertable = false, updatable = false)
    private Integer idPredio;

    @Getter
    @Setter
    @Column(name = "gcph_stage_id", insertable = false, updatable = false)
    private Integer idEtapa;

    @Getter
    @Setter
    @Column(name = "gcbi_vegetal_id", insertable = false, updatable = false)
    private Integer idVegetal;

    @Getter
    @Setter
    @Column(name = "gcbi_habitat_id", insertable = false, updatable = false)
    private Integer idHabitat;

    @Getter
    @Setter
    @Column(name = "gcbi_forestType_id", insertable = false, updatable = false)
    private Integer idTipoBosque;

    @Getter
    @Setter
    @Column(name = "gcbi_degreeIntervention_id", insertable = false, updatable = false)
    private Integer idGradoIntervencion;

    @Getter
    @Setter
    @Column(name = "gcbi_zoogeographic_id", insertable = false, updatable = false)
    private Integer idPisoZoogeografico;

    @Getter
    @Setter
    @Column(name = "gcso_area_influence_id", insertable = false, updatable = false)
    private Integer idNivelAreaInfluencia;

    @Getter
    @Setter
    @Column(name = "gcso_population_size_id", insertable = false, updatable = false)
    private Integer idTamanioPoblacion;

    @Getter
    @Setter
    @Column(name = "gcso_water_supply_id", insertable = false, updatable = false)
    private Integer idAbastecimientoAgua;

    @Getter
    @Setter
    @Column(name = "gcso_wastewater_evacuation_id", insertable = false, updatable = false)
    private Integer idAguasServidas;

    @Getter
    @Setter
    @Column(name = "gcso_rainwater_evacuation_id", insertable = false, updatable = false)
    private Integer idAguasLluvia;

    @Getter
    @Setter
    @Column(name = "gcso_solid_waste_id", insertable = false, updatable = false)
    private Integer idDesechosSolidos;

    @Getter
    @Setter
    @Column(name = "gcso_electrification_id", insertable = false, updatable = false)
    private Integer idElectrificacion;

    @Getter
    @Setter
    @Column(name = "gcso_public_transport_id", insertable = false, updatable = false)
    private Integer idTransportePublico;

    @Getter
    @Setter
    @Column(name = "gcso_roads_accesses_id", insertable = false, updatable = false)
    private Integer idVialidadYAcceso;

    @Getter
    @Setter
    @Column(name = "gcso_telephony_id", insertable = false, updatable = false)
    private Integer idTelefonia;
    @Getter
    @Setter
    @Column(name = "gcso_land_use_id", insertable = false, updatable = false)
    private Integer idAprovechamientoTierra;
    @Getter
    @Setter
    @Column(name = "gcso_land_tenure_id", insertable = false, updatable = false)
    private Integer idTenenciaTierra;
    @Getter
    @Setter
    @Column(name = "gcso_social_organization_id", insertable = false, updatable = false)
    private Integer idOrganizacionSocial;
    @Getter
    @Setter
    @Column(name = "gcso_landscape_tourism_id", insertable = false, updatable = false)
    private Integer idPaisaje;
    @Getter
    @Setter
    @Column(name = "gcso_landslide_hazard_id", insertable = false, updatable = false)
    private Integer idPeligroDeslizamiento;
    @Getter
    @Setter
    @Column(name = "gcso_flood_hazard_id", insertable = false, updatable = false)
    private Integer idPeligroInundacion;
    @Getter
    @Setter
    @Column(name = "gcso_earthquake_hazard_id", insertable = false, updatable = false)
    private Integer idPeligroTerremoto;

    //catalogos
    @Getter
    @Setter
    @Column(name = "mien_areas_catalog", length = 500)
    private String catalogoAreasIntervenidas;

    @Getter
    @Setter
    @Column(name = "mien_ecological_catalog", length = 500)
    private String catalogoDatosEcologicos;

    @Getter
    @Setter
    @Column(name = "mien_use_catalog", length = 500)
    private String catalogoUsoRecurso;

    @Getter
    @Setter
    @Column(name = "mien_biotic_component_catalog", length = 500)
    private String catalogoComponenteBiotico;

    @Getter
    @Setter
    @Column(name = "mien_sensitivity_catalog", length = 500)
    private String catalogoSensibilidad;

    @Getter
    @Setter
    @Column(name = "mien_ecological_wildlife_catalog", length = 500)
    private String catalogoDatosEcologicosFauna;

    @Getter
    @Setter
    @Column(name = "mien_use_wildlife_catalog", length = 500)
    private String catalogoUsoRecursoFauna;

    @Getter
    @Setter
    @Column(name = "mien_ethnic_composition_catalog", length = 500)
    private String catalogoComposicionEtnica;

    @Getter
    @Setter
    @Column(name = "mien_league_catalog", length = 500)
    private String catalogoLengua;

    @Getter
    @Setter
    @Column(name = "mien_religion_catalog", length = 500)
    private String catalogoReligion;

    @Getter
    @Setter
    @Column(name = "mien_traditions_catalog", length = 500)
    private String catalogoTradiciones;

    @ManyToOne
    @JoinColumn(name = "gcph_weather_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcph_weather_id_general_catalog_physical_gcph_stage_id")
    @Getter
    @Setter
    private CatalogoGeneralFisico clima;

    @Getter
    @Setter
    @Column(name = "gcph_weather_id", insertable = false, updatable = false)
    private Integer idClima;

    @Getter
    @Setter
    @Column(name = "mien_influence_area", length = 500)
    private String areaInfluencia;

    @Getter
    @Setter
    @Column(name = "mien_sloping_floor", length = 500)
    private String pendienteSuelo;

    @Getter
    @Setter
    @Column(name = "mien_soil_type", length = 500)
    private String tipoSuelo;

    @Getter
    @Setter
    @Column(name = "mien_soil_quality", length = 500)
    private String calidadSuelo;

    @ManyToOne
    @JoinColumn(name = "gcph_soil_permeability_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcph_soil_permeability_id_general_catalog_physical_gcph_id")
    @Getter
    @Setter
    private CatalogoGeneralFisico permeabilidadSuelo;

    @Getter
    @Setter
    @Column(name = "gcph_soil_permeability_id", insertable = false, updatable = false)
    private Integer idPermeabilidadSuelo;

    @ManyToOne
    @JoinColumn(name = "gcph_drainage_conditions_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcph_drainage_conditions_id_general_catalog_physical_gcph_id")
    @Getter
    @Setter
    private CatalogoGeneralFisico condicionesDrenaje;

    @Getter
    @Setter
    @Column(name = "gcph_drainage_conditions_id", insertable = false, updatable = false)
    private Integer idCondicionesDrenaje;

    @Getter
    @Setter
    @Column(name = "mien_water_resources", length = 500)
    private String recursosHidricos;

    @ManyToOne
    @JoinColumn(name = "gcph_water_table_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcph_water_table_id_general_catalog_physical_gcph_id")
    @Getter
    @Setter
    private CatalogoGeneralFisico nivelFreatico;

    @Getter
    @Setter
    @Column(name = "gcph_water_table_id", insertable = false, updatable = false)
    private Integer idNivelFreatico;

    @ManyToOne
    @JoinColumn(name = "gcph_rainfall_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcph_rainfall_id_general_catalog_physical_gcph_id")
    @Getter
    @Setter
    private CatalogoGeneralFisico precipitacionesAgua;

    @Getter
    @Setter
    @Column(name = "gcph_rainfall_id", insertable = false, updatable = false)
    private Integer idPrecipitacionesAgua;

    @ManyToOne
    @JoinColumn(name = "gcph_features_water_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcph_features_water_id_general_catalog_physical_gcph_id")
    @Getter
    @Setter
    private CatalogoGeneralFisico caracteristicasAgua;

    @Getter
    @Setter
    @Column(name = "gcph_features_water_id", insertable = false, updatable = false)
    private Integer idCaracteristicasAgua;

    @ManyToOne
    @JoinColumn(name = "gcph_features_air_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcph_features_air_id_general_catalog_physical_gcph_id")
    @Getter
    @Setter
    private CatalogoGeneralFisico caracteristicasAire;

    @Getter
    @Setter
    @Column(name = "gcph_features_air_id", insertable = false, updatable = false)
    private Integer idCaracteristicasAire;

    @ManyToOne
    @JoinColumn(name = "gcph_recirculation_air_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcph_recirculation_air_id_general_catalog_physical_gcph_id")
    @Getter
    @Setter
    private CatalogoGeneralFisico recirculacionAire;

    @Getter
    @Setter
    @Column(name = "gcph_recirculation_air_id", insertable = false, updatable = false)
    private Integer idRecirculacionAire;

    @ManyToOne
    @JoinColumn(name = "gcph_noise_id")
    @ForeignKey(name = "fk_mining_enviromental_record_gcph_noise_id_general_catalog_physical_gcph_id")
    @Getter
    @Setter
    private CatalogoGeneralFisico ruido;

    @Getter
    @Setter
    @Column(name = "gcph_noise_id", insertable = false, updatable = false)
    private Integer idRuido;

    @Getter
    @Setter
    @Column(name = "mien_other_use_flora")
    private String otrosUsosFlora;

    @Getter
    @Setter
    @Column(name = "mien_other_use_wildlife")
    private String otrosUsosFauna;

    @Getter
    @Setter
    @Column(name = "mien_other_solid_waste")
    private String otrosDesechoSolido;

    @Getter
    @Setter
    @Column(name = "mien_other_public_transportation")
    private String otrosTransportePublico;

    @Getter
    @Setter
    @Column(name = "mien_other_vialidad")
    private String otrosVialidad;

    @Getter
    @Setter
    @Column(name = "mien_other_land_use")
    private String otrosAprovechamientoTierra;

    @Getter
    @Setter
    @Column(name = "mien_other_language")
    private String otrosLengua;

    @Getter
    @Setter
    @Column(name = "mien_other_religion")
    private String otrosReligion;

    @Getter
    @Setter
    @Column(name = "mien_other_landscape")
    private String otrosPaisaje;

    @Getter
    @Setter
    @Column(name = "mien_other_custom")
    private String otrosTradiciones;

    @Getter
    @Setter
    @Column(name = "mien_other_ethnic_composition")
    private String otrosComposicionEtnica;

    @Getter
    @Setter
    @Column(name = "mien_other_social_organization")
    private String otrosOrganizacionSocial;

    @Getter
    @Setter
    @Column(name = "mien_other_area_influence")
    private String otrosAreaInfluencia;

    @Getter
    @Setter
    @Column(name = "mien_other_soil_quality")
    private String otrosCalidadSuelo;

    @Getter
    @Setter
    @Column(name = "mien_validate_overview")
    private Boolean validarInformacionGeneral = false;

    @Getter
    @Setter
    @Column(name = "mien_validate_main_feature")
    private Boolean validarCaracteristicasGenerales = false;

    @Getter
    @Setter
    @Column(name = "mien_validate_activity_description")
    private Boolean validarDescripcionActividad = false;

    @Getter
    @Setter
    @Column(name = "mien_validate_features_area")
    private Boolean validarCaracteristicasAreaInfluencia = false;

    @Getter
    @Setter
    @Column(name = "mien_validate_initial_sampling")
    private Boolean validarMuestreoInicialLineaBase = false;

    @Getter
    @Setter
    @Column(name = "mien_validate_environmental_impacts")
    private Boolean validarMatrizIdentificacionImpactosAmbientales = false;

    @Getter
    @Setter
    @Column(name = "mien_validate_environmental_plan")
    private Boolean validarPlanManejoAmbiental = false;

    @Getter
    @Setter
    @Column(name = "mien_validate_forestal_inventory")
    private Boolean validarInventarioForestal = false;
    
	@Column(name = "mien_validate_ppc")
	@Getter
	@Setter
	private Boolean validarParticipacionCiudadana=null;

    @Getter
    @Setter
    @Transient
    private String nombreConcesion;

    @Getter
    @Setter
    @Transient
    private String codigoConcesion;
	
	//LAST MOD 26/01/2016
    @Column(name = "mien_resolution_number")
    @Getter
    @Setter
    private String numeroResolucion;

    @Getter
    @Setter
    @Column(name = "mien_finalized")
    private Boolean finalizado = null;
    
    //para historial
    @Getter
    @Setter
    @Column(name = "mien_original_record_id")
    private Integer idRegistroOriginal;
   
    @Getter
    @Setter
    @Column(name = "mien_historical_date")
    private Date fechaHistorico;
    
    @Getter
    @Setter
    @Transient
    private String areaInfluenciaMostrar;
    
    @Getter
    @Setter
    @Transient
    private String pendienteSueloMostrar;
    
    @Getter
    @Setter
    @Transient
    private String tipoSueloMostrar;
    
    @Getter
    @Setter
    @Transient
    private String calidadSueloMostrar;
    
    @Getter
    @Setter
    @Transient
    private String recursosHidricosMostrar;    
    
    @Getter
    @Setter
    @Transient
    private String catalogoTipoMaterialMostrar;        
    
    @ManyToOne
    @JoinColumn(name = "prco_id")
    @ForeignKey(name = "prco_id_fk")
    @Getter
    @Setter
    private ProyectoLicenciaCoa proyectoLicenciaCoa;
    
    //para validar si se han realizado cambios en el objeto para guardar historial
  	public boolean equalsObjectMuestreoInicial(Object obj) {
  		if (obj == null)
  			return false;
  		FichaAmbientalMineria base = (FichaAmbientalMineria) obj;
  		if (this.getId() == null && base.getId() == null)
  			return super.equals(obj);
  		if (this.getId() == null || base.getId() == null)
  			return false;
  		
  		if(this.getId().equals(base.getId()) && 
  				this.getFormacionVegetal().getId().equals(base.getFormacionVegetal().getId()) && 
  				this.getHabitat().getId().equals(base.getHabitat().getId()) &&
  				this.getTipoBosque().getId().equals(base.getTipoBosque().getId()) && 
  				this.getGradoIntervencion().getId().equals(base.getGradoIntervencion().getId()) &&
  				this.getPisoZoogeografico().getId().equals(base.getPisoZoogeografico().getId()) && 
  		  		this.getCatalogoAreasIntervenidas().equals(base.getCatalogoAreasIntervenidas()) &&
  		  		this.getCatalogoComponenteBiotico().equals(base.getCatalogoComponenteBiotico()) && 
  	  			this.getCatalogoDatosEcologicos().equals(base.getCatalogoDatosEcologicos()) &&
  	  			this.getCatalogoDatosEcologicosFauna().equals(base.getCatalogoDatosEcologicosFauna()) && 
  	  			this.getCatalogoSensibilidad().equals(base.getCatalogoSensibilidad()) &&
  	  			this.getCatalogoUsoRecurso().equals(base.getCatalogoUsoRecurso()) && 
  	  		  	this.getCatalogoUsoRecursoFauna().equals(base.getCatalogoUsoRecursoFauna()) &&
  	  		  	this.getTamanioPoblacion().getId().equals(base.getTamanioPoblacion().getId()) && 
  	    		this.getAbastecimientoAgua().getId().equals(base.getAbastecimientoAgua().getId()) &&
  	    		this.getEvacuacionAguasServidas().getId().equals(base.getEvacuacionAguasServidas().getId()) && 
  	    		this.getEvacuacionAguasLluvia().getId().equals(base.getEvacuacionAguasLluvia().getId()) &&
  	    		this.getDesechosSolidos().getId().equals(base.getDesechosSolidos().getId()) && 
  	    		this.getElectrificacion().getId().equals(base.getElectrificacion().getId()) &&
  	    		this.getTransportePublico().getId().equals(base.getTransportePublico().getId()) &&
  	    		this.getVialidadYAcceso().getId().equals(base.getVialidadYAcceso().getId()) && 
  	    	  	this.getTelefonia().getId().equals(base.getTelefonia().getId()) &&
  	    	  	this.getAprovechamientoTierra().getId().equals(base.getAprovechamientoTierra().getId()) && 
  	    	  	this.getTenenciaTierra().getId().equals(base.getTenenciaTierra().getId()) &&
  	    	  	this.getOrganizacionSocial().getId().equals(base.getOrganizacionSocial().getId()) && 
  	    	  	this.getPaisajeTurismo().getId().equals(base.getPaisajeTurismo().getId()) &&
  	    	  	this.getPeligroDeslizamiento().getId().equals(base.getPeligroDeslizamiento().getId()) && 
  	    	    this.getPeligroInundacion().getId().equals(base.getPeligroInundacion().getId()) &&
  	    	    this.getPeligroTerremoto().getId().equals(base.getPeligroTerremoto().getId()) && 
  	    	    this.getCatalogoComposicionEtnica().equals(base.getCatalogoComposicionEtnica()) &&
  	    	    this.getCatalogoLengua().equals(base.getCatalogoLengua()) && 
  	    	    this.getCatalogoReligion().equals(base.getCatalogoReligion()) &&
  	    	    this.getCatalogoTradiciones().equals(base.getCatalogoTradiciones()) &&
  	    	    (this.getOtrosUsosFlora() == null && base.getOtrosUsosFlora() == null ||
  	    	    	this.getOtrosUsosFlora().equals(base.getOtrosUsosFlora())) &&
  	    	    (this.getOtrosComposicionEtnica() == null && base.getOtrosComposicionEtnica() == null ||
  	    	    	this.getOtrosComposicionEtnica().equals(base.getOtrosComposicionEtnica())) &&
  	    		(this.getOtrosDesechoSolido() == null && base.getOtrosDesechoSolido() == null ||
  	    			this.getOtrosDesechoSolido().equals(base.getOtrosDesechoSolido())) &&
  	    		(this.getOtrosTransportePublico() == null && base.getOtrosTransportePublico() == null ||
  	    			this.getOtrosTransportePublico().equals(base.getOtrosTransportePublico())) &&
  	    		(this.getOtrosVialidad() == null && base.getOtrosVialidad() == null ||
  	    			this.getOtrosVialidad().equals(base.getOtrosVialidad())) &&
  	    		(this.getOtrosAprovechamientoTierra() == null && base.getOtrosAprovechamientoTierra() == null ||
  	    			this.getOtrosAprovechamientoTierra().equals(base.getOtrosAprovechamientoTierra())) &&
  	    		(this.getOtrosLengua() == null && base.getOtrosLengua() == null ||
  	    			this.getOtrosLengua().equals(base.getOtrosLengua())) &&
  	    		(this.getOtrosReligion() == null && base.getOtrosReligion() == null ||
  	    			this.getOtrosReligion().equals(base.getOtrosReligion())) &&
  	    		(this.getOtrosTradiciones() == null && base.getOtrosTradiciones() == null ||
  	    			this.getOtrosTradiciones().equals(base.getOtrosTradiciones())) &&
  	    		(this.getOtrosPaisaje() == null && base.getOtrosPaisaje() == null ||
  	    				this.getOtrosPaisaje().equals(base.getOtrosPaisaje())) &&
  	    		(this.getOtrosOrganizacionSocial() == null && base.getOtrosOrganizacionSocial() == null ||
  	    			this.getOtrosOrganizacionSocial().equals(base.getOtrosOrganizacionSocial()))
  				){
  			return true;
  		}
  		else
  			return false;
  	}
}
