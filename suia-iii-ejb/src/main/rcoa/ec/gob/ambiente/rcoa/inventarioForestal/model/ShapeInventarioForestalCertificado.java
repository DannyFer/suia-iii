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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "forest_inventory_environmental_certificate_shapes", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "foce_status")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "foce_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "foce_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "foce_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "foce_date_update"))
})
@NamedQueries({
	@NamedQuery(name = "ShapeInventarioForestalCertificado.findAll", query = "SELECT sifc FROM ShapeInventarioForestalCertificado sifc"),
	@NamedQuery(name = ShapeInventarioForestalCertificado.FIND_BY_ID, query = "SELECT sifc FROM ShapeInventarioForestalCertificado sifc WHERE sifc.id= :idShape")
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "foce_status = 'TRUE'")
public class ShapeInventarioForestalCertificado extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String FIND_BY_ID = "ec.com.magmasoft.business.domain.ShapeInventarioForestalCertificado.findById";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="foce_id")
	private Integer id;
	
	// Inventario Forestal Certificado
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="foin_id")
	private InventarioForestalAmbiental inventarioForestalAmbiental;

}
