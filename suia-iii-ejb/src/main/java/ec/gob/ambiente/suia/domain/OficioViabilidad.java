package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Apr 9, 2015]
 *          </p>
 */
@Table(name = "feasibility_offices", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "feof_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "feof_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = OficioViabilidad.FIND_BY_CODIGO, query = "SELECT o FROM OficioViabilidad o WHERE o.codigo = :codigo") })
@Entity
public class OficioViabilidad extends EntidadBase {

	private static final long serialVersionUID = 1L;
	
	public final static String FIND_BY_CODIGO = "ec.gob.ambiente.suia.domain.OficioViabilidad.findByCodigo";

	@Id
	@SequenceGenerator(name = "FEASIBILITY_OFFICES_GENERATOR", sequenceName = "seq_feof_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FEASIBILITY_OFFICES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "feof_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "feof_code")
	private String codigo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_feasibility_officesfeof_id_geographical_locationsgelo_id")
	private UbicacionesGeografica ubicacionesGeografica;

	@Override
	public String toString() {
		return codigo;
	}
}
