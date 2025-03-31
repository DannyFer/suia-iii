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
import javax.persistence.Version;

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
 *          [Autor: Carlos Pupo, Fecha: Feb 27, 2015]
 *          </p>
 */
@Table(name = "dedicated_sequences")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dese_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dese_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = SecuenciaDedicada.FIND_BY_NAME, query = "SELECT sd FROM SecuenciaDedicada sd where sd.nombre = :nombre ") })
@Entity
public class SecuenciaDedicada extends EntidadBase {

	private static final long serialVersionUID = -1393294198751084762L;

	public static final String FIND_BY_NAME = "ec.gob.ambiente.suia.domain.SecuenciaDedicada.find_by_name";

	@Id
	@SequenceGenerator(name = "DEDICATED_SEQUENCES_TYPES_GENERATOR", sequenceName = "seq_dese_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEDICATED_SEQUENCES_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "dese_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "dese_name", unique = true)
	private String nombre;

	@Getter
	@Setter
	@Column(name = "dese_value")
	private long valor;

	@Getter
	@Setter
	@Version
	@Column(name = "dese_version_for_lock")
	private Integer version;

	@Override
	public String toString() {
		return nombre + ": " + valor;
	}

	public long incrementSequence() {
		this.valor = this.valor + 1;
		return this.valor;
	}
}
