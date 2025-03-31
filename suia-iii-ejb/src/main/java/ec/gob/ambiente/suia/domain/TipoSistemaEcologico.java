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
@Table(name = "ecological_system_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "esty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "esty_status = 'TRUE'")
@Entity
public class TipoSistemaEcologico extends EntidadBase {

	private static final long serialVersionUID = -3626818879796340472L;
	
	public static final int TIPO_SISTEMA_ECOLOGICO_OTRO = 11;

	@Id
	@SequenceGenerator(name = "ECOLOGICAL_SYSTEM_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_esty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ECOLOGICAL_SYSTEM_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "esty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "esty_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}
}
