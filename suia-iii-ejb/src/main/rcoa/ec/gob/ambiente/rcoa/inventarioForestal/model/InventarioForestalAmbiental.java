package ec.gob.ambiente.rcoa.inventarioForestal.model;

import java.math.BigDecimal;
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

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.EspecialistaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "forest_inventory_environmental", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "foin_status")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "foin_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "foin_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "foin_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "foin_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "foin_status = 'TRUE'")
public class InventarioForestalAmbiental extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="foin_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="foin_inventory_type")
	private Integer tipoInventario;
	
	@Getter
	@Setter
	@Column(name="foin_project_surface")
	private BigDecimal superficieProyecto;
	
	@Getter
	@Setter
	@Column(name="foin_clearing_surface")
	private BigDecimal superficieDesbroce;
	
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	@Column(name="foin_date_sampling_elaboration")
	private Date fechaElaboracion;
	
	@Getter
	@Setter
	@Column(name="foin_technical_inspection")
	private Boolean inspeccionTecnica;
	
	// Registro Preliminar
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prco_id")
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	// Tecnico Forestal Responsable
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="ensp_id")
	private EspecialistaAmbiental especialistaAmbiental;
	
	// Metodo recoleccion datos
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id")	
	private CatalogoGeneralCoa metodoRecoleccionDatos;
	
}
