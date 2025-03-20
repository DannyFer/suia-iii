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

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Jun 9, 2015]
 *          </p>
 */
@Table(name = "security_measures_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "smty_status")) })
@NamedQueries({ @NamedQuery(name = TipoMedidaSeguridad.FILTRAR, query = "SELECT m FROM TipoMedidaSeguridad m WHERE (lower(m.nombre) LIKE :filter)") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "smty_status = 'TRUE'")
@Entity
public class TipoMedidaSeguridad extends EntidadBase {

	private static final long serialVersionUID = 8035130572199703080L;

	public static final String FILTRAR = "ec.gob.ambiente.suia.domain.TipoMedidaSeguridad.FILTRAR";

	public static final int TIPO_MEDIDA_SEGURIDAD_OTRO = 1;

	@Id
	@SequenceGenerator(name = "SECURITY MEASURES_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_smty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECURITY MEASURES_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "smty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "smty_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}
}
