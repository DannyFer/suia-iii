package ec.gob.ambiente.rcoa.inventarioForestal.model;

import java.math.BigDecimal;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sumation_environmental_register", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "sure_status")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "sure_saer_user_creator")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "sure_date_creator")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "sure_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "sure_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sure_status = 'TRUE'")
public class RegistroAmbientalSumatoria extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="sure_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="sure_sum_basal_sample_site")
	private BigDecimal sumatoriaAreaBasal;
	
	@Getter
	@Setter
	@Column(name="sure_sum_total_volume_sample_site")
	private BigDecimal sumatoriaVolumen;
	
	@Getter
	@Setter
	@Column(name="sure_sampling_site_surface")
	private Double superficieSitio;
	
	@Getter
	@Setter
	@Column(name="sure_sample_code")
	private String codigoMuestra;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="foin_id")
	private InventarioForestalAmbiental inventarioForestalAmbiental;
		
	@Getter
	@Setter
	@Column(name="sure_sum_volume_total")
	private BigDecimal sumatoriaVolumenTotal;
	
	@Getter
	@Setter
	@Column(name="sure_value_wood_standing")
	private Double valorMaderaPie;
	
	@Getter
	@Setter
	@Column(name="sure_payment_clearing")
	private Double pagoDesbroceCobertura;
	
	@Getter
	@Setter
	@Transient
	private BigDecimal superficieDesbroce;
}
