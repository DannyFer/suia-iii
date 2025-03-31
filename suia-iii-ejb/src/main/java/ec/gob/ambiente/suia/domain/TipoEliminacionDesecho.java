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
@Table(name = "waste_disposal_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wdty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdty_status = 'TRUE'")
public class TipoEliminacionDesecho extends EntidadBase {

	private static final long serialVersionUID = 6133699716882487831L;

	public static final String TIPO_ELIMINACION_NO_APLICA = "N/A";
	

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "WASTE_DISPOSAL_TYPES_ID_GENERATOR", sequenceName = "seq_wdty_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DISPOSAL_TYPES_ID_GENERATOR")
	@Column(name = "wdty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "wdty_key", length = 255)
	private String clave;

	@Getter
	@Setter
	@Column(name = "wdty_name")
	private String nombre;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wdty_parent_id")
	@ForeignKey(name = "fk_wdty_parent_id_wdty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdty_status = 'TRUE'")
	private TipoEliminacionDesecho tipoEliminacionDesecho;
	
	@Getter
	@Setter
	@Column(name = "wdty_code_modality")
	private Integer codigoModalidad;

	@Getter
	@Setter
	@OneToMany(mappedBy = "tipoEliminacionDesecho")
	private List<TipoEliminacionDesecho> tipoEliminacionDesechosHijos;

	public boolean isTipoEliminacionFinal() {
		return tipoEliminacionDesechosHijos == null || tipoEliminacionDesechosHijos.isEmpty();
	}

	@Override
	public String toString() {
		return this.nombre;
	}

}