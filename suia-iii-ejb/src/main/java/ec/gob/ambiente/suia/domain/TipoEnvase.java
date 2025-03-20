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
@Table(name = "package_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "paty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "paty_status = 'TRUE'")
@Entity
public class TipoEnvase extends EntidadBase {

	private static final long serialVersionUID = 9014778420758362316L;

	public static final int TIPO_ENVASE_OTRO = 1;

	@Id
	@SequenceGenerator(name = "PACKAGE_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_paty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PACKAGE_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "paty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "paty_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "paty_key")
	private String clave;

	@Override
	public String toString() {
		return nombre;
	}

	public boolean isOtro() {
		return this.getId().intValue() == TIPO_ENVASE_OTRO;
	}
}
