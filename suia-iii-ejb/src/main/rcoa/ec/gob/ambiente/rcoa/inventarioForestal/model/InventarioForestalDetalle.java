package ec.gob.ambiente.rcoa.inventarioForestal.model;

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
@Table(name = "forest_inventory_detail", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "fide_status")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fide_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fide_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fide_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fide_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fide_status = 'TRUE'")
public class InventarioForestalDetalle extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="fide_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="fide_ecosystem_project_area")
	private String ecosistemaAreaProyecto;
	
	@Getter
	@Setter
	@Column(name="fide_vegetable_and_soil")
	private String coberturaVegetalSuelo;
	
	@Getter
	@Setter
	@Column(name="fide_technique_justificative")
	private String justificacionTecnica;
	
	// Inventario Forestal
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="foin_id")
	private InventarioForestalAmbiental inventarioForestalAmbiental;
	
}
