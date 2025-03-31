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

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coordinates_fience", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "coce_status")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "coce_user_creator")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "coce_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "coce_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "coce_update_date"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "coce_status = 'TRUE'")
public class CoordenadasInventarioForestalCertificado extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="coce_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="coce_description")
	private String descripcionCoordenada;
	
	@Getter
	@Setter
	@Column(name="coce_order")
	private Integer ordenCoordenada;
	
	@Getter
	@Setter
	@Column(name="coce_x")
	private BigDecimal x;
	
	@Getter
	@Setter
	@Column(name="coce_y")
	private BigDecimal y;
	
	// Inventario Forestal Certificado
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="foce_id")
	private ShapeInventarioForestalCertificado shapeInventarioForestalCertificado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="refi_id")
	private ReporteInventarioForestal reporteInventarioForestal;

	@Getter
	@Setter	
	@Column(name="coor_geographic_area")
	private Integer areaGeografica;	
}
