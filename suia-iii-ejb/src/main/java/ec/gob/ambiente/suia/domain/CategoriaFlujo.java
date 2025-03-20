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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Table(name = "categories_flows", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "cafl_status")) })
@NamedQueries({ @NamedQuery(name = CategoriaFlujo.FIND_BY_CATEGORIA, query = "SELECT cf FROM CategoriaFlujo cf WHERE cf.categoria.id = :categoriaId and flujo.versionSistema = 1 ORDER BY cf.orden ASC"),
	 @NamedQuery(name = CategoriaFlujo.FIND_RCOA_BY_CATEGORIA, query = "SELECT cf FROM CategoriaFlujo cf WHERE cf.categoria.id = :categoriaId and flujo.versionSistema = 2 ORDER BY cf.orden ASC")})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cafl_status = 'TRUE'")
@Entity
public class CategoriaFlujo extends EntidadBase {

	private static final long serialVersionUID = -4623150641073092844L;

	public static final String FIND_BY_CATEGORIA = "ec.gob.ambiente.suia.domain.find_by_categoria";
	public static final String FIND_RCOA_BY_CATEGORIA = "ec.gob.ambiente.suia.domain.find_rcoa_by_categoria";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "CATEGORIA_FLUJO_ID_GENERATOR", sequenceName="seq_cafl_id", schema="suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORIA_FLUJO_ID_GENERATOR")
	@Column(name = "cafl_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "cafl_order")
	private Integer orden = 1;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "flow_id")
	private Flujo flujo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cate_id")
	private Categoria categoria;
}
