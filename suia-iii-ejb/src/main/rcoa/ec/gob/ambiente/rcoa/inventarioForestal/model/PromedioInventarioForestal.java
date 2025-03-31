package ec.gob.ambiente.rcoa.inventarioForestal.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "average_environmental_register", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "saer_status")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "saer_user_creator")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "saer_date_creator")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "saer_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "saer_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "saer_status = 'TRUE'")
public class PromedioInventarioForestal extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="saer_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="saer_average_basal_area")
	private Double promedioAreaBasal;
	
	@Getter
	@Setter
	@Column(name="saer_avarege_volumen")
	private Double promedioVolumen;
	
	@Getter
	@Setter
	@Column(name="saer_average_basal_area_hectarian")
	private Double valorAreaBasalPromedio;
	
	@Getter
	@Setter
	@Column(name="saer_average_total_volume_hectaria")
	private Double valorVolumenTotalPromedio;
	
	@Getter
	@Setter
	@Column(name="saer_value_standing_wood")
	private Double valorMaderaPie;
	
	@Getter
	@Setter
	@Column(name="saer_payment_clearing")
	private Double pagoDesbroceCobertura;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="foin_id")
	private InventarioForestalAmbiental inventarioForestalAmbiental;
	
	@Getter
	@Setter
	@Column(name="saer_observation")
	private String observacion;

}
