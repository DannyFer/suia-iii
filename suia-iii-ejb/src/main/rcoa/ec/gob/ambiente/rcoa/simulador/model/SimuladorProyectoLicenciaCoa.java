package ec.gob.ambiente.rcoa.simulador.model;



import java.math.BigDecimal;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.TipoPoblacion;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the proyectos_licenciamiento_ambiental database
 * table.
 * 
 */

@Entity
@Table(name = "project_licencing_coa", schema = "coa_simulator")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prco_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prco_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prco_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prco_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prco_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prco_status = 'TRUE'")
public class SimuladorProyectoLicenciaCoa extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6112385639568874872L;

	@Getter
	@Setter
	@Id
	@Column(name = "prco_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_COA_MAE_GENERATOR", sequenceName = "project_licencing_coa_prco_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_COA_MAE_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "prco_name", length = 500)
	private String nombreProyecto;

	@Getter
	@Setter
	@Column(name = "prco_description", length = 2500)
	private String descripcionProyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "poty_id")
	@ForeignKey(name = "fk_poty_id")
	private TipoPoblacion tipoPoblacion;

	@Getter
	@Setter
	@Column(name = "prco_address", length = 255)
	private String direccionProyecto;

	@Getter
	@Setter
	@Column(name = "prco_waste_generation")
	private Boolean generaDesechos;

	@Getter
	@Setter
	@Column(name = "prco_waste_management")
	private Boolean gestionDesechos;

	@Getter
	@Setter
	@Column(name = "prco_vegetable_verture")
	private Boolean renocionCobertura;

	@Getter
	@Setter
	@Column(name = "prco_chemical_substances")
	private Boolean sustanciasQuimicas;

	@Getter
	@Setter
	@Column(name = "prco_chemical_substances_transport")
	private Boolean transportaSustanciasQuimicas;

	@Getter
	@Setter
	@Column(name = "prco_enviromental_impact")
	private Boolean altoImpacto;

	@Getter
	@Setter
	@Column(name = "prco_magnitud", length = 255)
	private String magnitud;

	@Getter
	@Setter
	@Column(name = "prco_observation_bd", length = 255)
	private String observacionDB;

	@Getter
	@Setter
	@Column(name = "prco_project_type")
	private int tipoProyecto;

	@Getter
	@Setter
	@Column(name = "prco_enviromental_impact_doc", length = 255)
	private String documentoResolucion;

	@Getter
	@Setter
	@Column(name = "prco_cua", length = 30)
	private String codigoUnicoAmbiental;

	@Getter
	@Setter
	@Column(name = "prco_registration_status")
	private boolean estadoRegistro;

	@Getter
	@Setter
	@Column(name = "prco_delete_reason", length = 255)
	private String razonEliminacion;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "prco_deactivation_date")
	private Date fechaDesactivacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id")
	@ForeignKey(name = "area_id")
	private Area areaResponsable;

	@Getter
	@Setter
	@Column(name = "prco_count_coordinates")
	private int cuentaCoordenadas;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "prco_cua_date")
	private Date fechaGeneracionCua;

	@Getter
	@Setter
	@Column(name = "prco_categorizacion")
	private int categorizacion;

	@Getter
	@Setter
	@Column(name = "prco_code_pma")
	private String codigoPma;
	
	@Getter
	@Setter
	@Column(name = "prco_surface")
	private BigDecimal superficie;
	
	@Getter
	@Setter
	@Column(name = "prco_snap_intersection")
	private Boolean interecaSnap;
	
	@Getter
	@Setter
	@Column(name = "prco_bp_intersection")
	private Boolean interecaBosqueProtector;
	
	@Getter
	@Setter
	@Column(name = "prco_forest_heritage")
	private Boolean interecaPatrimonioForestal;
	
	@Getter
	@Setter
	@Column(name = "prco_project_finished")
	private Boolean proyectoFinalizado;
	
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "prco_project_completion_date")
	private Date proyectoFechaFinalizado;
	
	@Getter
	@Setter
	@Column(name = "prco_viability_favorable")
	private Boolean tieneViabilidadFavorable;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id_forest_inventory")
	@ForeignKey(name = "area_id")
	private Area areaInventarioForestal;
	
	@Getter
	@Setter
	@Column(name = "prco_mining_operations")
	private Boolean operacionMinera;
	
	@Getter
	@Setter
	@Column(name = "prco_mining_concessions")
	private Boolean concesionMinera;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prco_categorizacion", insertable = false, updatable = false)
	@ForeignKey(name = "prco_categorizacion")
	private Categoria categoria;
	
	@Getter
	@Setter
	@Column(name = "prco_rgd_associated_code")
	private String codigoRgdAsociado;
	
	@Getter
	@Setter
	@Column(name = "prco_rsq_associated_code")
	private String codigoRsqAsociado;
	
	@Getter
	@Setter
	@Column(name = "prco_is_waste_self_management")
	private Boolean esGestionPropia;
	
	@Getter
	@Setter
	@Column(name = "prco_environmental_regularization_start_date")
	private Date fechaInicioRegularizacionAmbiental;
	
	@Getter
	@Setter
	@Column(name = "prco_environmental_diagnostic_reason")
	private String motivoDiagnosico;
	
	@Getter
	@Setter
	@Column(name = "gelo_id")
	private Integer idCantonOficina;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "idProyectoLicencia")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prvi_status = 'TRUE'")
	private List<ViabilidadCoa> listaViabilidad;
	
	@Getter
	@Setter
	@Column(name = "prco_resolution_number")
	private String numeroResolucion;
	
	@Getter
	@Setter
	@Column(name = "zona_camaronera")
	private String zona_camaronera;
	
	@Getter
	@Setter
	@Column(name = "acuerdo_camaronera")
	private String acuerdo_camaronera;
	
	@Getter
	@Setter
	@Column(name = "prco_office_number")
	private String numeroOficio;
	
	@Getter
	@Setter
	@Transient
	private Boolean interecaRamsar;
	
	@Getter
	@Setter
	@Transient
	private String concesionCamaronera;
}
