package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "measurement_units", schema = "public")
@NamedQueries({ @NamedQuery(name = UnidadMedida.GET_BY_UNIDAD, query = "SELECT u FROM UnidadMedida u WHERE lower(u.siglas) = lower(:unidad)") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "meun_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "meun_status = 'TRUE'")
public class UnidadMedida extends EntidadBase {

	private static final long serialVersionUID = 1L;

	public static final String GET_BY_UNIDAD = "ec.gob.ambiente.suia.domain.UnidadMedida";

	@Id
	@Column(name = "meun_id")
	@SequenceGenerator(name = "MEASUREMENT_UNITS_GENERATOR", sequenceName = "seq_meun_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEASUREMENT_UNITS_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "meun_acronym", length = 255)
	@Getter
	@Setter
	private String siglas;

	@Column(name = "meun_name", length = 255)
	@Getter
	@Setter
	private String nombre;

	@Column(name = "meun_type", length = 255)
	@Getter
	@Setter
	private String tipo;

	@Column(name = "meun_description", length = 255)
	@Getter
	@Setter
	private String descripcion;

	@Override
	public String toString() {
		return nombre + " (" + siglas + ")";
	}
}
