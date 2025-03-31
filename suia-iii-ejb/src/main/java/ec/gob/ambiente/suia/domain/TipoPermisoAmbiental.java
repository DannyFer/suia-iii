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
@Table(name = "environmental_permit_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "epty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "epty_status = 'TRUE'")
@Entity
public class TipoPermisoAmbiental extends EntidadBase {

	private static final long serialVersionUID = 2275481272853412911L;

	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL PERMIT_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_epty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL PERMIT_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "epty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "epty_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}
}
