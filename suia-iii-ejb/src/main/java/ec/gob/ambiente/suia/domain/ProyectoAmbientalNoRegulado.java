package ec.gob.ambiente.suia.domain;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.jboss.netty.util.EstimatableObjectWrapper;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the proyectos_licenciamiento_ambiental database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = ProyectoAmbientalNoRegulado.GET_REPRESENTANTE_PROYECTO, query = "SELECT p.usuario FROM ProyectoAmbientalNoRegulado p WHERE p.id = :idProyecto") })
@Entity
@Table(name = "unregulated_environmental_projects", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "unep_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "unep_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "unep_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "unep_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "unep_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unep_status = 'TRUE'")
public class ProyectoAmbientalNoRegulado extends EntidadAuditable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String GET_REPRESENTANTE_PROYECTO = "ec.com.magmasoft.business.domain.ProyectoAmbientalNoRegulado.getRepresentanteProyecto";
	public static final String SEQUENCE_CODE = "seq_unep_code";

	@Getter
	@Setter
	@Id
	@Column(name = "unep_id")
	@SequenceGenerator(name = "ProyectoAmbientalNoRegulado_unepID_GENERATOR", sequenceName = "seq_unep_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ProyectoAmbientalNoRegulado_unepID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "unep_address", length = 255)
	private String direccionProyecto;

	@Getter
	@Setter
	@Column(name = "unep_description", length = 10485750)
	private String descripcion;

	@Column(length = 10485750, name = "unep_name")
	@Getter
	@Setter
	private String nombre;

	@Getter
	@Setter
	@Column(name = "unep_routes", length = 500)
	private String rutas;

	@Getter
	@Setter
	@Column(length = 10485750, name = "unep_resume")
	private String resumen;

	@Getter
	@Setter
	@Column(name = "unep_unit")
	private String unidad;

	@Getter
	@Setter
	@Column(name = "unep_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name = "unep_mining_code")
	private String codigoMinero;

	@Getter
	@Setter
	@Column(name = "unep_resolution_number")
	private String numeroDeResolucion;

	@Getter
	@Setter
	@Column(name = "unep_project_status")
	private boolean estadoProyecto = true;

	@Getter
	@Setter
	@Column(name = "unep_is_mining_grants")
	private boolean esConcecionMinera;

	@Getter
	@Setter
	@Column(name = "unep_mining_grants")
	private boolean concesionesMinerasMultiples;
	
	@Getter
	@Setter
	@Column(name = "unep_artisanal_miners")
	private boolean minerosArtesanales;
	
	@Getter
	@Setter
	@Column(name = "unep_legal_representative_pin")
	private String pinRepresentanteLegal;
	
	@Getter
	@Setter
	@Column(name = "unep_legal_representatice_name")
	private String nombreRepresentanteLegal;
	
	@Getter
	@Setter
	@Column(name = "unep_population_urban")
	private Double publacionUrbana;
	
	@Getter
	@Setter
	@Column(name = "unep_population_rural")
	private Double poblacionRural;
	
	@Getter
	@Setter
	@Column(name = "unep_growth_rate")
	private Double tasaCrecimiento;
	
	@Getter
	@Setter
	@Column(name = "unep_mortality")
	private Double mortalidad;
	@Getter
	@Setter
	@Column(name = "unep_morbility")
	private Double mobilidad;
	@Getter
	@Setter
	@Column(name = "unep_pea")
	private Double pea;
	
	
	@Getter
	@Setter
	@Column(name = "unep_region_description")
	private String descripcionRegion;
	
	@Getter
	@Setter
	@Column(name = "unep_urbans_road_system")
	private String sistemaCarreteraUrbana;
	
	@Getter
	@Setter
	@Column(name = "unep_sector")
	private String sector;


	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_projectsunep_id_usersuser_id")
	@Getter
	@Setter
	private Usuario usuario;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "sety_id")
	@ForeignKey(name = "fk_projectsunep_id_sector_typessety_id")
	private TipoSector tipoSector;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "proyectoAmbientalNoRegulado")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unpl_status = 'TRUE'")
	private List<ProyectoNoRegularizadoUbicacionGeografica> proyectoUbicacionesGeograficas;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "proyectoAmbientalNoRegulado")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unpc_status = 'TRUE'")
	private List<CoordenadsProyectosNoRegulados> coordenadsProyectosNoRegulados;

	@Getter
	@Setter
	@OneToMany(mappedBy = "proyectoAmbientalNoRegulado")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvst_status = 'TRUE'")
	private List<EstudioViabilidadTecnica> listaEstudioViabilidad;
	

	@Override
	public String toString() {
		return nombre;
	}

	@Getter
	@Setter
	@Column(name = "unep_diagnostic_feasibility")
	private String diagnosticoFactibilidad;
	
	@Getter
	@Setter
	@Column(name = "unep_information_basic_area_project")
	private String informacionBasicaAreaProyecto;
	

}