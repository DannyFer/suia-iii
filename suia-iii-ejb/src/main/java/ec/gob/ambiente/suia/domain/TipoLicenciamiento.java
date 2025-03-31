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
@Table(name = "licensing_types")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "lity_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "lity_status = 'TRUE'")
@Entity
public class TipoLicenciamiento extends EntidadBase {

	private static final long serialVersionUID = 1L;

	public static final int TIPO_LICENCIAMIENTO_CERTIFICADO = 1;
	public static final int TIPO_LICENCIAMIENTO_REGISTRO = 2;
	public static final int TIPO_LICENCIAMIENTO_LICENCIA = 3;

	@Id
	@SequenceGenerator(name = "LICENSING_TYPES_GENERATOR", sequenceName = "seq_lity_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LICENSING_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "lity_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "lity_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "lity_cost", nullable = true)
	private Float costo;

	@Getter
	@Setter
	@Column(name = "lity_duration")
	private String duracionTramite;

	@Getter
	@Setter
	@Column(name = "lity_currency")
	private String moneda;

	@Getter
	@Setter
	@Column(name = "lity_cost_description")
	private String descripcionCosto;

	@Override
	public String toString() {
		return nombre;
	}
}
