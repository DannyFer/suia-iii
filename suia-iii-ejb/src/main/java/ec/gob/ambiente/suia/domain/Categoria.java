package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Table(name = "categories", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "cate_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cate_status = 'TRUE'")
@Entity
public class Categoria extends EntidadBase {

	private static final long serialVersionUID = -2318354557513452875L;

	public static final int CATEGORIA_I = 1;
	public static final int CATEGORIA_II = 2;
	public static final int CATEGORIA_III = 3;
	public static final int CATEGORIA_IV = 4;

	@Id
	@Getter
	@Setter
	@Column(name = "cate_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "cate_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name = "cate_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "cate_public_name")
	private String nombrePublico;

	@Getter
	@Setter
	@OneToMany(mappedBy = "categoria")
	private List<CatalogoCategoriaSistema> catalogoCategorias;

	@OneToMany(mappedBy = "categoria")
	@Getter
	@Setter
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cafl_status = 'TRUE'")
	private List<CategoriaFlujo> categoriaFlujos;

	@Override
	public String toString() {
		return descripcion;
	}
}
