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
 *          [Autor: Carlos Pupo, Fecha: Jun 6, 2015]
 *          </p>
 */
@Table(name = "local_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "loty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "loty_status = 'TRUE'")
@Entity
public class TipoLocal extends EntidadBase {

	private static final long serialVersionUID = 1558819508739828909L;

	@Id
	@SequenceGenerator(name = "LOCAL_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_loty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LOCAL_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "loty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "loty_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "loty_key")
	private String clave;

	@Override
	public String toString() {
		return nombre;
	}
}
