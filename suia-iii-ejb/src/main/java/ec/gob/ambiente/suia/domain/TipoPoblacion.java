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
 *          [Autor: Carlos Pupo, Fecha: May 11, 2015]
 *          </p>
 */
@Table(name = "population_types")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "poty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "poty_status = 'TRUE'")
@Entity
public class TipoPoblacion extends EntidadBase {

	private static final long serialVersionUID = 1371747656567020872L;

	public static final int TIPO_POBLACION_URBANA = 1;
	public static final int TIPO_POBLACION_RURAL = 2;
	public static final int TIPO_POBLACION_MARITIMA = 3;
	public static final int TIPO_POBLACION_PERIFERICA = 4;
	
	public static final int TIPO_POBLACION_CONTINENTAL = 5; 
	public static final int TIPO_POBLACION_NO_CONTINENTAL = 6;
	public static final int TIPO_POBLACION_MIXTO = 7;

	@Id
	@SequenceGenerator(name = "POPULATION_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_poty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POPULATION_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "poty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "poty_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}
	
	public TipoPoblacion() {
	}
	
	public TipoPoblacion(Integer id) {
		this.id = id;
	}
}
