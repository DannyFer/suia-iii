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
 *          [Autor: Carlos Pupo, Fecha: Jun 26, 2015]
 *          </p>
 */
@Table(name = "technical_criteria_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tcty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tcty_status = 'TRUE'")
@Entity
public class TipoCriterioTecnico extends EntidadBase {

	private static final long serialVersionUID = 1241608422368234653L;
	
	public static final int TIPO_CRITERIO_TECNICO_OTRO = 4;

	@Id
	@SequenceGenerator(name = "TECHNICAL_CRITERIA_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_tcty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECHNICAL_CRITERIA_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "tcty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tcty_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}
}
