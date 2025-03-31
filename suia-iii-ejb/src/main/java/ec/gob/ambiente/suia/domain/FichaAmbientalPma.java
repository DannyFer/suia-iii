package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the catii_fapma database table.
 *
 */
@NamedQueries({
		@NamedQuery(name = FichaAmbientalPma.GET_FICHA_PMA_PROYECTO, query = "SELECT f FROM FichaAmbientalPma f WHERE f.proyectoLicenciamientoAmbiental.codigo = :codigo and f.idRegistroOriginal = null"),
		@NamedQuery(name = FichaAmbientalPma.GET_FICHA_PMA_PROYECTOID, query = "SELECT f FROM FichaAmbientalPma f WHERE f.proyectoLicenciamientoAmbiental.id = :p_id and f.idRegistroOriginal = null") })
@Entity
@Table(name = "catii_fapma", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "cafa_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cafa_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cafa_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cafa_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cafa_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cafa_status = 'TRUE'")
public class FichaAmbientalPma extends EntidadAuditable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1217459732115153929L;
	public static final String GET_FICHA_PMA_PROYECTO = "ec.com.magmasoft.business.domain.categoriaii.FichaAmbientalPma.getFichaPmaProyecto";
	public static final String GET_FICHA_PMA_PROYECTOID = "ec.com.magmasoft.business.domain.categoriaii.FichaAmbientalPma.getFichaPmaProyectoId";
	public static final String SEQUENCE_CODE = "catii_fapma_cafa_id_seq";

	@Id
	@Column(name = "cafa_id")
	@SequenceGenerator(name = "CATII_FAPMA_CAFAID_GENERATOR", sequenceName = "catii_fapma_cafa_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATII_FAPMA_CAFAID_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "cafa_bibliographic_referenacias")
	@Getter
	@Setter
	private String referenciasBibliograficas;

	@Column(name = "cafa_city")
	@Getter
	@Setter
	private String ciudad;

	@Getter
	@Setter
	@Column(name = "cafa_drinking_water")
	private Boolean aguaPotable = false;

	@Column(name = "cafa_electric_power")
	@Getter
	@Setter
	private Boolean energiaElectrica = false;

	@Column(name = "cafa_electric_power_consumption")
	@Getter
	@Setter
	private BigDecimal consumoElectrico;

	@Column(name = "cafa_implantation_area")
	@Getter
	@Setter
	private BigDecimal areaImplantacion;

	@Column(name = "cafa_observations_accessory_equipment")
	@Getter
	@Setter
	private String observacionesEquiposAccesorios;

	@Column(name = "cafa_observations_estate_situation")
	@Getter
	@Setter
	private String observacionesPredio;

	@Column(name = "cafa_border_limit")
	@Getter
	@Setter
	private String periferico;

	@Column(name = "cafa_physical_space_observations")
	@Getter
	@Setter
	private String observacionesEspacioFisico;

	@Column(name = "cafa_project_description")
	@Getter
	@Setter
	private String descripcionProyecto;

	@Column(name = "cafa_sewerage")
	@Getter
	@Setter
	private Boolean alcantarillado = false;

	@Column(name = "cafa_vehicular_access")
	@Getter
	@Setter
	private Boolean accesoVehicular = false;

	@Column(name = "cafa_vehicular_access_facilities")
	@Getter
	@Setter
	private String facilidadesAccesoTransporte;

	@Column(name = "cafa_other_infraestructure")
	@Getter
	@Setter
	private String otrosInfraestructura;

	@Column(name = "cafa_other_telephony")
	@Getter
	@Setter
	private String otrosTelefonia;

	@Column(name = "cafa_other_property")
	@Getter
	@Setter
	private String otrosPredio;

	@Column(name = "cafa_water_consumption")
	@Getter
	@Setter
	private BigDecimal consumoAgua;

	@Column(name = "cafa_zone_description")
	@Getter
	@Setter
	private String descripcionZona;

	@Column(name = "cafa_person_number")
	@Getter
	@Setter
	private Integer numeroPersonas;

	@Getter
	@Setter
	@Column(name = "cafa_unit")
	private String unidad;

	@ManyToOne
	@JoinColumn(name = "cafa_type_way")
	@ForeignKey(name = "general_catalogs_tipo_via_catii_fapma_fk")
	@Getter
	@Setter
	private CatalogoGeneralSocial tipoVia;

	// bi-directional many-to-one association to ProjectsEnvironmentalLicensing
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "projects_environmental_licensing_catii_fa_pma_fk")
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	// bi-directional many-to-one association to FapmaAccessoryEquipment
	@OneToMany(mappedBy = "fichaAmbientalPma")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "faae_status = 'TRUE'")
	@Getter
	@Setter
	private List<EquipoAccesorioPma> equiposAccesorios;

	// bi-directional many-to-one association to FapmaActivitiesSchedule
	@OneToMany(mappedBy = "fichaAmbientalPma")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "caas_status = 'TRUE'")
	@Getter
	@Setter
	private List<CronogramaActividadesPma> cronogramasActividades;

	// bi-directional many-to-one association to FapmaEnvironmentalImpact
	// @OneToMany(mappedBy = "fichaAmbientalPma")
	// @LazyCollection(LazyCollectionOption.FALSE)
	// @Filter(name = EntidadBase.FILTER_ACTIVE, condition =
	// "faen_status = 'TRUE'")
	// @Getter
	// @Setter
	// private List<ImpactoAmbientalPma> impactosAmbientales;

	// bi-directional many-to-one association to FapmaHeadPma
	@OneToMany(mappedBy = "fichaAmbientalPma")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fahe_status = 'TRUE'")
	@Getter
	@Setter
	private List<CabeceraPma> cabecerasPma;

	// bi-directional many-to-one association to FapmaLegalFramework
	@OneToMany(mappedBy = "fichaAmbientalPma")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fale_status = 'TRUE'")
	@Getter
	@Setter
	private List<MarcoLegalPma> marcosLegales;

	// bi-directional many-to-one association to FapmaProcessDescription
	@OneToMany(mappedBy = "fichaAmbientalPma")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapi_status = 'TRUE'")
	@Getter
	@Setter
	private List<InsumoProcesoPma> insumosProcesos;

	// bi-directional many-to-one association to FilepmaDetail
	@OneToMany(mappedBy = "fichaAmbientalPma")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "desp_status = 'TRUE'")
	@Getter
	@Setter
	private List<DetalleFichaPma> ArchivoDetallesPma;

	@OneToMany(mappedBy = "fichaAmbientalPma")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prde_status = 'TRUE'")
	@Getter
	@Setter
	private List<DetalleFichaPlan> detalleFichaList;

	@Column(name = "cafa_process_id")
	@Getter
	@Setter
	private Long processId;

	// -----------------
	@Column(name = "cafa_validate_general_data")
	@Getter
	@Setter
	private Boolean validarDatosGenerales = false;

	@Column(name = "cafa_validate_reference_legal_framework")
	@Getter
	@Setter
	private Boolean validarMarcoLegalReferencial = false;

	@Column(name = "cafa_validate_project_description_work_activity")
	@Getter
	@Setter
	private Boolean validarDescripcionProyectoObraActividad = false;

	@Column(name = "cafa_validate_process_description")
	@Getter
	@Setter
	private Boolean validarDescripcionProceso = false;

	@Column(name = "cafa_validate_description_area_implementation")
	@Getter
	@Setter
	private Boolean validarDescripcionAreaImplantacion = false;

	@Column(name = "cafa_validate_main_environmental_impacts")
	@Getter
	@Setter
	private Boolean validarPrincipalesImpactosAmbientales = false;

	@Column(name = "cafa_validate_environmental_management_plan")
	@Getter
	@Setter
	private Boolean validarPlanManejoAmbiental = false;

	@Column(name = "cafa_validate_project_schedule_construction_operation")
	@Getter
	@Setter
	private Boolean validarCronogramaConstruccionOperacionProyecto = false;

	@Column(name = "cafa_validate_schedule_valued_environmental_management_plan")
	@Getter
	@Setter
	private Boolean validarCronogramaValoradoPlanManejoAmbiental = false;

	@Column(name = "cafa_validate_forestal_inventory_pma")
	@Getter
	@Setter
	private Boolean validarInventarioForestal=null;
	
	@Column(name = "cafa_validate_ppc")
	@Getter
	@Setter
	private Boolean validarParticipacionCiudadana=null;

	@Column(name = "cafa_license_number")
	@Getter
	@Setter
	private String numeroLicencia;

	@Column(name = "cafa_office_number")
	@Getter
	@Setter
	private String numeroOficio;

	@Getter
	@Setter
	@Column(name = "cafa_finalized")
	private Boolean finalizado = null;
		
	@Getter
	@Setter
	@Column(name = "cafa_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "cafa_historical_date")
	private Date fechaHistorico;

	public FichaAmbientalPma() {
		super();
	}
	
	
	
	

}