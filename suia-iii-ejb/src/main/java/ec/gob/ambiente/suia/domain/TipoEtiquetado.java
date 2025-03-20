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
@Table(name = "labeled_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "laty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "laty_status = 'TRUE'")
@Entity
public class TipoEtiquetado extends EntidadBase {

	private static final long serialVersionUID = -6987168037150557182L;

	public static final int TIPO_ETIQUETADO_OTRO = 1;
	
	@Id
	@SequenceGenerator(name = "LABELED_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_laty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LABELED_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "laty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "laty_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "laty_key")
	private String clave;

	@Override
	public String toString() {
		return nombre;
	}
	
	public boolean isOtro() {
		return this.getId().intValue() == TIPO_ETIQUETADO_OTRO;
	}
}
