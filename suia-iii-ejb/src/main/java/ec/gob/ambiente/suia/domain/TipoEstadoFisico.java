package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
 *          [Autor: Carlos Pupo, Fecha: Feb 2, 2015]
 *          </p>
 */
@Table(name = "phisical_state_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "psty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "psty_status = 'TRUE'")
@Entity
public class TipoEstadoFisico extends EntidadBase {

	private static final long serialVersionUID = 1637349454902293372L;

	public static final int TIPO_ESTADO_GASEOSO = 1;
	public static final int TIPO_ESTADO_LIQUIDO = 2;
	public static final int TIPO_ESTADO_SEMISOLIDO = 3;
	public static final int TIPO_ESTADO_SOLIDO = 4;
	
	@Id
	@SequenceGenerator(name = "PHISICAL_STATE_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_psty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PHISICAL_STATE_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "psty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "psty_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}
}
