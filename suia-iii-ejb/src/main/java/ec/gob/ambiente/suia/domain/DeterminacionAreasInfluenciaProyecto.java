package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Table(name = "determination_influence_project_areas", schema = "suia_iii")
@Entity
@NamedQueries({ @NamedQuery(name = DeterminacionAreasInfluenciaProyecto.LISTAR_POR_EIA_TIPO, query = "SELECT d FROM DeterminacionAreasInfluenciaProyecto d WHERE d.estado = true AND d.idEstudioImpactoAmbiental = :idEstudioImpactoAmbiental AND d.tipoInfluecia = :tipo ORDER BY d.infraestructuraActividadesProyecto") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dipa_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dipa_status = 'TRUE'")
public class DeterminacionAreasInfluenciaProyecto extends EntidadBase {

	private static final long serialVersionUID = -8265313708479773877L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_EIA_TIPO = PAQUETE + "DeterminacionAreasInfluenciaProyecto.listarPorEIATipo";
	public static final char TIPO_INFLUENCIA_DIRECTA = 'D';
	public static final char TIPO_INFLUENCIA_INDIRECTA = 'I';
	public static final char DIFERENCIA_ELEMENTOS_PROYECTO = 'E';
	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "DETERMINACION_AREAS_ID_GENERATOR", initialValue = 1, sequenceName = "seq_dipa_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETERMINACION_AREAS_ID_GENERATOR")
	@Column(name = "dipa_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "dipa_infrastructure_project_activities", length = 500)
	private String infraestructuraActividadesProyecto;

	@Getter
	@Setter
	@Column(name = "dipa_proprietary", length = 500)
	private String propietarios;

	@Getter
	@Setter
	@Column(name = "dipa_communities_towns", length = 500)
	private String comunidadesCentrosPoblados;

	@Getter
	@Setter
	@Column(name = "dipa_indigenous_nationalities_parish_territories", length = 500)
	private String territoriosIndigenas;

	@Getter
	@Setter
	@Column(name = "dipa_other_jurisdictions", length = 500)
	private String otrasJurisdicciones;

	@Getter
	@Setter
	@Column(name = "dipa_description_sensitive_elements", length = 500)
	private String descripcionElementosSensibles;

	@Getter
	@Setter
	@Column(name = "dipa_distance_meters")
	private Integer distanciaMetros;

	@Getter
	@Setter
	@Column(name = "dipa_influence_type")
	private Character tipoInfluecia;

	@Getter
	@Setter
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "fk_determination_influence_project_areas_eist_id_environmental_impact_studies_eist_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EstudioImpactoAmbiental eistId;

	@Getter
	@Setter
	@Column(name = "eist_id", insertable = false, updatable = false)
	private Integer idEstudioImpactoAmbiental;

	@Getter
	@Setter
	@JoinColumn(name = "geca_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_determination_influence_project_areas_geca_id_general_catalog_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral gecaId;

	@Getter
	@Setter
	@Column(name = "geca_id", insertable = false, updatable = false)
	private Integer idCatalogoElementosSensibles;

	@Transient
	@Getter
	@Setter
	private int indice;

}
