package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "waste_dangerous_source_categories", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wdsc_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdsc_status = 'TRUE'")
public class CategoriaFuenteDesechoPeligroso extends EntidadBase implements Serializable {

	private static final long serialVersionUID = 1033244532392714938L;
	
	public static final String CODIGO_NO_ESPECIFICA = "NE";
	public static final String CODIGO_ESPECIALES = "ES";
	public static final String CODIGO_ESPECIFICAS = "FE";
	
	public static final String NOMBRE_FUENTES_ESPECIFICAS = "DESECHOS PELIGROSOS POR FUENTES ESPECÍFICAS";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "WASTE_DANGEROUS_SOURCE_CATEGORIES_WDSC_GENERATOR", sequenceName = "seq_wdsc_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DANGEROUS_SOURCE_CATEGORIES_WDSC_GENERATOR")
	@Column(name = "wdsc_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "wdsc_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "wdsc_ciiu", length = 2)
	private String codigo;

	@Getter
	@Setter
	@OneToMany(mappedBy = "categoriaFuenteDesechoPeligroso")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdso_status = 'TRUE'")
	private List<FuenteDesechoPeligroso> fuentesDesechosPeligroso;

	@Setter
	@Transient
	private List<CategoriaFuenteDesechoPeligroso> categoriasFuenteDesechoPeligroso;

	public List<CategoriaFuenteDesechoPeligroso> getCategoriasFuenteDesechoPeligroso() {
		return categoriasFuenteDesechoPeligroso == null ? categoriasFuenteDesechoPeligroso = new ArrayList<CategoriaFuenteDesechoPeligroso>()
				: categoriasFuenteDesechoPeligroso;
	}

	@Override
	public String toString() {
		return this.nombre;
	}

}