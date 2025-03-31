package ec.gob.ambiente.retce.model;


import java.io.Serializable;

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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
/**
 * The persistent class for the retce.product database table.
 * 
 */
@Entity
@Table(name="product", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "pro_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "pro_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "pro_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pro_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pro_user_update")) })
public class Producto extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="pro_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="pro_quantity")
	private Double cantidad;
	
	@Getter
	@Setter
	@Column(name="pro_tradename")
	private String nombreComercial;
	
	@Getter
	@Setter
	@Column(name="pro_others")
	private String otros;

	@Getter
	@Setter
	@Column(name = "pro_history")
	private boolean historico;

	@Getter
	@Setter
	@Column(name = "pro_id_owner")
	private Integer idPropietario;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cont_id")
	private CatalogoTipoContenedor catalogoTipoContenedor;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prod_id")
	private CatalogoProductos catalogoProductos;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="phys_id")
	private CatalogoEstadoFisico catalogoEstadoFisico;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="unit_id")
	private CatalogoUnidades catalogoUnidades;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="proj_id")
	private InformacionProyecto informacionProyecto;
		
}

