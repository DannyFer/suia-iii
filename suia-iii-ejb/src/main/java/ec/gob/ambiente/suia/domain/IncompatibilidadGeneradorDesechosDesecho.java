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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 08/06/2015]
 *          </p>
 */
@Entity
@Table(name = "incompatibilities_hazardous_wastes_generators", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ihwg_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ihwg_status = 'TRUE'")
public class IncompatibilidadGeneradorDesechosDesecho extends EntidadBase {

	private static final long serialVersionUID = -3184865730192095594L;

	@Getter
	@Setter
	@Id
	@Column(name = "ihwg_id")
	@SequenceGenerator(name = "INCOMPATIBILITIES_WASTES_IHWG_ID_GENERATOR", sequenceName = "seq_ihwg_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INCOMPATIBILITIES_WASTES_IHWG_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wdin_id")
	@ForeignKey(name = "fk_ihwg_id_wdin_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdin_status = 'TRUE'")
	private IncompatibilidadDesechoPeligroso incompatibilidadDesechoPeligroso;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwwd_id")
	@ForeignKey(name = "fk_ihwg_id_hwwd_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwd_status = 'TRUE'")
	private GeneradorDesechosDesechoPeligroso generadorDesechosDesechoPeligroso;

	@Getter
	@Setter
	@Transient
	private DesechoPeligroso desechoPeligroso;

	@Getter
	@Setter
	@Column(name = "ihwg_other")
	private String otro;

	@Override
	public String toString() {
		if (incompatibilidadDesechoPeligroso != null) {
			if (!incompatibilidadDesechoPeligroso.isOtro())
				return incompatibilidadDesechoPeligroso.toString();
			return "(" + IncompatibilidadDesechoPeligroso.INCOMPATIBILIDAD_DESECHO_PELIGROSO_OTRO + ") " + otro;
		}
		return "";
	}
}
