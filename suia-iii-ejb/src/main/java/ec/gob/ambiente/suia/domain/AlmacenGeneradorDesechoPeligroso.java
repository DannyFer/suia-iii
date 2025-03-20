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
@Table(name = "hazardous_wastes_warehouses_dangerous_waste", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwdw_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwdw_status = 'TRUE'")
public class AlmacenGeneradorDesechoPeligroso extends EntidadBase {

	private static final long serialVersionUID = 465969874038220931L;

	@Getter
	@Setter
	@Id
	@Column(name = "hwdw_id")
	@SequenceGenerator(name = "HAZARDOUS_WASTES_DANGEROUS_WASTE_HWDW_ID_GENERATOR", sequenceName = "seq_hwdw_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_DANGEROUS_WASTE_HWDW_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwwa_id")
	@ForeignKey(name = "fk_hwdw_id_hwwa_id")
	private AlmacenGeneradorDesechos almacenGeneradorDesechos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwwd_id")
	@ForeignKey(name = "fk_hwdw_id_hwwd_id")
	private GeneradorDesechosDesechoPeligroso generadorDesechosDesechoPeligroso;

}
