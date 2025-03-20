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
@Table(name = "socioeconomic_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "soty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "soty_status = 'TRUE'")
@Entity
public class TipoSistemaSocioeconomico extends EntidadBase {

	private static final long serialVersionUID = 4036351716753357226L;
	
	public static final int TIPO_SISTEMA_SOCIOECONOMICO_OTRO = 9;

	@Id
	@SequenceGenerator(name = "SOCIOECONOMIC_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_soty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOCIOECONOMIC_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "soty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "soty_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}
}
