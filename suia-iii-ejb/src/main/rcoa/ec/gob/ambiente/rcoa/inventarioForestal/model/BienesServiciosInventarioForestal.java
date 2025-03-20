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
@Table(name = "estate_services", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "esse_status")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "esse_user_creator")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "esse_date_creation")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "esse_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "esse_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "esse_status = 'TRUE'")
public class BienesServiciosInventarioForestal extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="esse_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="esse_pro_wood_no_wood")
	private Double maderableNoMaderable;
	
	@Getter
	@Setter
	@Column(name="esse_carbon_starege")
	private Double almacenamientoCarbono;
	
	@Getter
	@Setter
	@Column(name="esse_scenic_beauty")
	private Double bellezaEscenica;
	
	@Getter
	@Setter
	@Column(name="esse_water")
	private Double agua;
	
	@Getter
	@Setter
	@Column(name="esse_products_medicinal")
	private Double productosMedicinales;
	
	@Getter
	@Setter
	@Column(name="esse_products_ornamental")
	private Double productosOrnamentales;
	
	@Getter
	@Setter
	@Column(name="esse_products_handmade")
	private Double productosArtesanales;
	
	@Getter
	@Setter
	@Column(name="esse_total_payment_environmental_state_services")
	private Double pagoTotal;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="foin_id")
	private InventarioForestalAmbiental inventarioForestalAmbiental;

}
