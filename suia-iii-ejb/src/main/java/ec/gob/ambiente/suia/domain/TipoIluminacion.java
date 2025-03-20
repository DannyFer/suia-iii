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
@Table(name = "ilumination_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ilty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ilty_status = 'TRUE'")
@Entity
public class TipoIluminacion extends EntidadBase {

	private static final long serialVersionUID = -6930700358557562389L;

	@Id
	@SequenceGenerator(name = "ILUMINATION_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_ilty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ILUMINATION_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "ilty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "ilty_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "ilty_key")
	private String clave;

	@Override
	public String toString() {
		return nombre;
	}
}
