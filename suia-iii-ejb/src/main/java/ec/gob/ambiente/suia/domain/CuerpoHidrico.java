package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "water_bodies", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "wabo_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "wabo_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "wabo_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wabo_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wabo_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wabo_status = 'TRUE'")
@NamedQueries({@NamedQuery(name = CuerpoHidrico.LISTAR_POR_ID_EIA, query = "SELECT c FROM CuerpoHidrico c WHERE c.estado=true AND c.estudioImpactoAmbiental = :estudioImpactoAmbiental")})
public class CuerpoHidrico extends EntidadAuditable {

	private static final long serialVersionUID = 1218304605831535129L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ID_EIA = PAQUETE
			+ "CuerpoHidrico.listarPorIdEia";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "WATER_BODIES_GENERATOR", initialValue = 1, schema = "suia_iii", sequenceName = "seq_wabo_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WATER_BODIES_GENERATOR")
	@Column(name = "wabo_id", unique = true, nullable = false)
	private Integer id;


	@Getter
	@Setter
	@Column(name = "wabo_name")
	private String nombreCuerpo;

	@Getter
	@Setter
	@Column(name = "wabo_type")
	private Integer tipo;


	@Getter
	@Setter
	@Column(name = "wabo_section_width")
	private BigDecimal anchoSeccion;


	@Getter
	@Setter
	@Column(name = "wabo_average_depth")
	private BigDecimal profundidadMedia;

	@Getter
	@Setter
	@Column(name = "wabo_average_speed")
	private BigDecimal velocidadMedia;

	@Getter
	@Setter
	@Column(name = "wabo_average_flow")
	private BigDecimal cudalMedio;

	@Getter
	@Setter
	@Column(name = "wabo_uses")
	private String usos;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eist_id")
	@ForeignKey(name = "fk_environmental_impact_studieseist_id_water_bodies_id")
	@Getter
	@Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@Getter
	@Setter
	@OneToMany(mappedBy = "cuerpoHidrico")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<CoordenadaGeneral> coordenadaGeneralList;


	@Getter
	@Setter
	@OneToMany(mappedBy = "cuerpoHidrico")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<UsoCuerpoHidrico> usosCuerposHidricos;


	@Transient
	@Getter
	@Setter
	private int indice;
	@Transient
	@Getter
	@Setter
	private boolean editar;

}