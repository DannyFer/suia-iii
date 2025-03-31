package ec.gob.ambiente.suia.domain;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.List;

@Entity
@Table(name = "environmental_impact_studies", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "eist_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "eist_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "eist_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "eist_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "eist_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eist_status = 'TRUE'")
@NamedQueries({
		@NamedQuery(name = EstudioImpactoAmbiental.BUSCAR_POR_ID, query = "SELECT e FROM EstudioImpactoAmbiental e WHERE e.id =:id AND e.estado = true "),
		@NamedQuery(name = EstudioImpactoAmbiental.BUSCAR_POR_PROYECTO, query = "SELECT c FROM EstudioImpactoAmbiental c WHERE c.idProyecto = :idProyecto AND c.estado = true AND c.idHistorico = null "),
		@NamedQuery(name = EstudioImpactoAmbiental.BUSCAR_ESTUDIO_POR_ID_PROYECTO_TIPO, query = "SELECT c FROM EstudioImpactoAmbiental c WHERE c.idProyecto = :p_idProyecto AND c.tipoEstado = :p_tipo AND c.estado = true AND c.idHistorico = null "),
		@NamedQuery(name = EstudioImpactoAmbiental.BUSCAR_POR_ID_PROCESO, query = "SELECT c FROM EstudioImpactoAmbiental c WHERE c.idProceso = :idProceso AND c.tipoEstado = :p_tipo AND c.estado = true AND c.idHistorico = null"),
		@NamedQuery(name = EstudioImpactoAmbiental.BUSCAR_POR_HISTORICO, query = "SELECT e FROM EstudioImpactoAmbiental e WHERE e.idHistorico =:idHistorico AND e.estado = true order by id desc")})
public class EstudioImpactoAmbiental extends EntidadAuditable implements Cloneable {
	private static final long serialVersionUID = -1948131919746177457L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";

	public static final String BUSCAR_POR_ID = PAQUETE
			+ "EstudioImpactoAmbiental.buscarPorId";
	public static final String BUSCAR_POR_PROYECTO = PAQUETE
			+ "EstudioImpactoAmbiental.buscarPorProyecto";
	public static final String BUSCAR_ESTUDIO_POR_ID_PROYECTO_TIPO = PAQUETE
			+ "EstudioImpactoAmbiental.buscarEstudioPorIdProyectoTipo";
	public static final String BUSCAR_POR_ID_PROCESO = PAQUETE
			+ "EstudioImpactoAmbiental.buscarEstudioPorIdProceso";
	public static final String BUSCAR_POR_HISTORICO = PAQUETE
			+ "EstudioImpactoAmbiental.buscarEstudioPorHistorico";

	public static String EIA_TIPO_ESTADO_BORRADOR = "BORRADOR";
	public static String EIA_TIPO_ESTADO_FINAL = "FINAL";

	public static String PROJECT_CODE = "AT-123";
	public static Long PROCESS_ID = 112L;
	public static String PROCESS_NAME = "EIA";

	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_IMPACT_STUDIES_ID_GENERATOR", initialValue = 1, sequenceName = "seq_eist_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_IMPACT_STUDIES_ID_GENERATOR")
	@Column(name = "eist_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@ForeignKey(name = "fk_environmental_impact_studiespren_id_projects_environmental_licensing_pren_id")
	@JoinColumn(name = "pren_id")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@Column(name = "pren_id", insertable = false, updatable = false)
	private Integer idProyecto;

	@Getter
	@Setter
	@Column(name = "eist_process_id")
	private Long idProceso;

	@Getter
	@Setter
	@Column(name = "eist_status_type")
	private String tipoEstado;

	@Getter
	@Setter
	@Column(name = "eist_executive_summary")
	private String resumenEjecutivo;

	@Getter
	@Setter
	@Column(name = "eist_definition_of_study_area")
	private String definicionAreaEstudio;

	@Getter
	@Setter
	@Column(name = "eist_introduction")
	private String introduccion;

	@Getter
	@Setter
	@Column(name = "eist_analysis_alternatives")
	private String analisisAlternativas;

	@Getter
	@Setter
	@Column(name = "eist_status_send")
	private Boolean tipoEstadoEnviado;

	@Getter
	@Setter
	@Column(name = "eist_legal_framework")
	@AssertTrue(message = "El campo 'He leído y comprendo las Normativas' es requerido.")
	private Boolean tipoEstadoLegal;

	@Getter
	@Setter
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "cons_id")
	@ForeignKey(name = "fk_environmental_impact_studies_cons_id_consultants_cons_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cons_status = 'TRUE'")
	private Consultor consultor;

	@Getter
	@Setter
	@Column(name = "eist_has_different_mining_phases")
	private Boolean tieneFasesMineras;

	@Getter
	@Setter
	@Column(name = "eist_mining_phases")
	private String faseMinera;

	@Getter
	@Setter
	@Column(name = "eist_type_mining")
	private String tipoDeMineria;

	@Getter
	@Setter
	@Column(name = "eist_exploitation_volume")
	private Integer volumenDeExplotacion;

	@Getter
	@Setter
	@Column(name = "eist_unit_volume")
	private String unidadMedidaVolumen;

	@Getter
	@Setter
	@Column(name = "eist_exploitation_method")
	private String metodoDeExplotacion;

	@Getter
	@Setter
	@Column(name = "eist_number_facade_exploitation")
	private Integer numeroDeFrentesExplotacion;

	@Getter
	@Setter
	@Column(name = "eist_has_built_slaghes")
	private Boolean tieneConstruccionEscombreras;

	@Getter
	@Setter
	@Column(name = "eist_slaghes_location")
	private String localizacionEscombreras;

	@Getter
	@Setter
	@Column(name = "eist_slaghes_capacity")
	private Integer capacidadEscombrera;

	@Getter
	@Setter
	@Column(name = "eist_explotation_state")
	private String estadoExplotacion;

	@Getter
	@Setter
	@Column(name = "eist_activity_benefit")
	private String actividadBeneficio;

	@Getter
	@Setter
	@Column(name = "eist_metal_recovery")
	private Integer recuperacionMetalica;

	@Getter
	@Setter
	@Column(name = "eist_has_built_tailings")
	private Boolean tieneConstruccionRelaves;

	@Getter
	@Setter
	@Column(name = "eist_tailings_location")
	private String localizacionRelaves;

	@Getter
	@Setter
	@Column(name = "eist_capacity")
	private Integer capacidad;

	@Getter
	@Setter
	@Column(name = "eist_benefit_state")
	private String estadoBeneficio;

	@Getter
	@Setter
	@Column(name = "eist_accepts_clause")
	private Boolean aceptaClausula;

	@Getter
	@Setter
	@Column(name = "eist_has_contaminated_sites")
	private Boolean tieneIdentificacionSitiosContaminados;

	@Getter
	@Setter
	@Column(name = "eist_justification_contaminated_sites")
	private String justificacionIdentificacionSitiosContaminados;

	@Getter
	@Setter
	@Column(name = "eist_plmo_justification")
	private String justificacion;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "eist_description")
	@ForeignKey(name = "fk_environmental_impact_studies_eist_description_docu_id")
	private Documento documentoDescripcion;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "eist_supplies")
	@ForeignKey(name = "fk_environmental_impact_studies_eist_supplies_docu_id")
	private Documento documentoInsumos;

	@Getter
	@Setter
	@Column(name = "eist_rage_days")
	private Integer plazo;

	@Getter
	@Setter
	@Column(name = "eist_cordinate_proyection_type")
	private String tipoProyeccionParaCoordenada;
	
	/**
	 * Cris F: Nuevos campos en tablas
	 */
	@Getter
	@Setter
	@Column(name = "eist_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "eist_notification_number")
	private Integer numeroNotificacion;	
	//Fin nuevos campos en tablas

	@Getter
	@Setter
	@OneToMany(mappedBy = "estudioImpactoAmbiental", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private List<FormaCoordenadasEIA> listaformaCoordenadasEIAs;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "phzo_id")
	private ZonasFase zonasFase;

	public EstudioImpactoAmbiental() {

	}

	/**
	 * Cris F: Nuevos cambios en tablas
	 */
	public EstudioImpactoAmbiental(Integer id) {
		this.id = id;
	}
	
	//Cris F: para evitar el error de estudio ambiental
	@Getter
	@Column(name = "cons_id", insertable = false, updatable = false)
	private Integer idConsultor;	
	
	@Getter
	@Column(name = "pren_id", insertable = false, updatable = false)
	private Integer idProyectoLicenciamientoAmbiental;
	
	@Getter
	@Setter
	@Column(name = "eist_description", insertable = false, updatable = false)
	private Integer idDocumentoDescripcion;
	
	@Getter
	@Setter
	@Column(name = "eist_supplies", insertable = false, updatable = false)
	private Integer idDocumentoInsumos;
	
	@Override
	public EstudioImpactoAmbiental clone() throws CloneNotSupportedException {

		 EstudioImpactoAmbiental clone = (EstudioImpactoAmbiental)super.clone();
		 clone.setId(null);		 
		 return clone;
	}
}
