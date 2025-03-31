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
@Table(name = "hazardous_wastes_waste_dangerous_generation_points", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwwp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwp_status = 'TRUE'")
public class GeneradorDesechosDesechoPeligrosoPuntoGeneracion extends EntidadBase {

	private static final long serialVersionUID = -755149657570660930L;

	@Getter
	@Setter
	@Id
	@Column(name = "hwwp_id")
	@SequenceGenerator(name = "HAZARDOUS_WASTES_WASTE_HWWP_ID_GENERATOR", sequenceName = "seq_hwwp_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_WASTE_HWWP_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwwd_id")
	@ForeignKey(name = "fk_hwwp_id_hwwd_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwd_status = 'TRUE'")
	private GeneradorDesechosDesechoPeligrosoDatosGenerales generaDesechosDesechoPeligrosoDatosGenerales;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gepo_id")
	@ForeignKey(name = "fk_hwwp_id_gepo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gepo_status = 'TRUE'")
	private PuntoGeneracion puntoGeneracion;

	public GeneradorDesechosDesechoPeligrosoPuntoGeneracion() {
	}

	public GeneradorDesechosDesechoPeligrosoPuntoGeneracion(PuntoGeneracion puntoGeneracion) {
		this.puntoGeneracion = puntoGeneracion;
	}

	@Override
	public String toString() {
		return puntoGeneracion == null ? "" : puntoGeneracion.getNombre();
	}
}
