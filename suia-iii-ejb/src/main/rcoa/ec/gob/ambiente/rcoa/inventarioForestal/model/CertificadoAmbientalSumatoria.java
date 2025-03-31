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
@Table(name = "sumation_environmental_certificate", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "suce_status")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "suce_user_creator")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "suce_date_creator")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "suce_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "suce_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "suce_status = 'TRUE'")
public class CertificadoAmbientalSumatoria extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="suce_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="suce_sum_basal_area")
	private BigDecimal sumatoriaAreaBasal;
	
	@Getter
	@Setter
	@Column(name="suce_sum_total_volume")
	private BigDecimal sumatoriaVolumenTotal;
	
	@Getter
	@Setter
	@Column(name="suce_value_standing_wood")
	private Double valorMaderaPie;
	
	@Getter
	@Setter
	@Column(name="suce_payment_clearing")
	private Double pagoDesbroceCobertura;
	
	@Getter
	@Setter
	@Transient
	private BigDecimal superficieDesbroce;
	
	// Registro Preliminar
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="foin_id")
	private InventarioForestalAmbiental inventarioForestalAmbiental;

}
