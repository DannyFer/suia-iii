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
@Table(name = "waste_dangerous_categories", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wdca_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdca_status = 'TRUE'")
public class CategoriaDesechoPeligroso extends EntidadBase {

	private static final long serialVersionUID = 8039451731796408730L;

	public static final int CATEGORIA_DESECHO_OTRO = 1;

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "WASTE_DANGEROUS_CATEGORIES_ID_GENERATOR", sequenceName = "seq_wdca_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DANGEROUS_CATEGORIES_ID_GENERATOR")
	@Column(name = "wdca_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "wdca_key", length = 255)
	private String clave;

	@Getter
	@Setter
	@Column(name = "wdca_name")
	private String nombre;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wdca_parent_id")
	@ForeignKey(name = "fk_wdca_parent_id_wdca_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdca_status = 'TRUE'")
	private CategoriaDesechoPeligroso categoriaDesechoPeligroso;

	@Getter
	@Setter
	@OneToMany(mappedBy = "categoriaDesechoPeligroso")
	private List<CategoriaDesechoPeligroso> categoriasDesechosPeligrososHijos;

	public boolean isCategoriaDesechoFinal() {
		return categoriasDesechosPeligrososHijos == null || categoriasDesechosPeligrososHijos.isEmpty();
	}

	public String getNombreCompuesto() {
		if (this.categoriaDesechoPeligroso != null
				&& !this.nombre.toLowerCase().equals(this.categoriaDesechoPeligroso.nombre.toLowerCase()))
			return this.categoriaDesechoPeligroso.nombre + ", " + this.nombre;
		return this.nombre;
	}

	@Override
	public String toString() {
		return this.nombre;
	}

	public boolean isOtro() {
		return this.nombre.toLowerCase().contains("otro") || this.nombre.toLowerCase().contains("otra");
	}

}