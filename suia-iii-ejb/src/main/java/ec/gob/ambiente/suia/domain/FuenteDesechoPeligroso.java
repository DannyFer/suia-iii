package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
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

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table.
 * 
 */
@Entity
@Table(name = "waste_dangerous_source", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wdso_status")) })
@NamedQueries({ @NamedQuery(name = FuenteDesechoPeligroso.FIND_BY_CATEGORIA, query = "SELECT f FROM FuenteDesechoPeligroso f where f.categoriaFuenteDesechoPeligroso.id = :idCategoria") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdso_status = 'TRUE'")
public class FuenteDesechoPeligroso extends EntidadBase implements Serializable {

	private static final long serialVersionUID = -6123374036350157760L;

	public static final String FIND_BY_CATEGORIA = "ec.gob.ambiente.suia.domain.FuenteDesechoPeligroso.findByCategoria";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "WASTE_DANGEROUS_SOURCE_WDSOID_GENERATOR", sequenceName = "seq_wdso_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DANGEROUS_SOURCE_WDSOID_GENERATOR")
	@Column(name = "wdso_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "wdso_category", length = 255)
	private String categoria;

	@Getter
	@Setter
	@Column(name = "wdso_code", length = 255)
	private String codigo;

	@Getter
	@Setter
	@Column(name = "wdso_description", length = 1000)
	private String descripcion;

	@Getter
	@Setter
	@JoinColumn(name = "wdty_id")
	@ManyToOne
	@ForeignKey(name = "fk_wdty_id_to_wdso_id")
	private TipoFuenteDesechoPeligroso tipoFuenteDesechoPeligroso;

	@Getter
	@Setter
	@JoinColumn(name = "wdsc_id")
	@ManyToOne
	@ForeignKey(name = "fk_waste_d_categories_source_wdcs_id_waste_d_sourcewdcs_id")
	private CategoriaFuenteDesechoPeligroso categoriaFuenteDesechoPeligroso;

	@Getter
	@Setter
	@OneToMany(mappedBy = "fuenteDesechoPeligroso")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wada_status = 'TRUE'")
	private List<DesechoPeligroso> desechosPeligrosos;

	@Override
	public String toString() {
		return this.descripcion;
	}

}