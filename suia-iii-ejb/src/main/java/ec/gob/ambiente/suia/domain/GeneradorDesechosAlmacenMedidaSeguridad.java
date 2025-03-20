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
 * <b> Entity que representa la tabla clousure_plans_documents. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 07/01/2015]
 *          </p>
 */
@Entity
@Table(name = "h_w_warehouse_security_measures", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwsm_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwsm_status = 'TRUE'")
public class GeneradorDesechosAlmacenMedidaSeguridad extends EntidadBase {

	private static final long serialVersionUID = 7402864747335668723L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "WAREHOUSE_SECURITY_MEASURES_ID_GENERATOR", schema="suia_iii", sequenceName = "seq_hwsm_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WAREHOUSE_SECURITY_MEASURES_ID_GENERATOR")
	@Column(name = "hwsm_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "hwsm_other_value")
	private String otro;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwwa_id")
	@ForeignKey(name = "fk_hwsm_id_hwwa_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwa_status = 'TRUE'")
	private AlmacenGeneradorDesechos generadorDesechosAlmacen;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "smty_id")
	@ForeignKey(name = "fk_hwsm_id_smty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "smty_status = 'TRUE'")
	private TipoMedidaSeguridad tipoMedidaSeguridad;
}
