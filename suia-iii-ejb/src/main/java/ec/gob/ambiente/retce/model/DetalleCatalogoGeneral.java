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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the general_catalog_details database table.
 * 
 */
@Entity
@Table(name="general_catalog_details", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "gcde_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "gcde_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "gcde_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "gcde_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "gcde_user_update")) })
public class DetalleCatalogoGeneral extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="gcde_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="gcde_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name="gcde_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="gcde_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="gcde_param")
	private String parametro;
	
	@Getter
	@Setter
	@Column(name="gcde_param2")
	private String parametro2;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gect_id")
	private CatalogoGeneralRetce catalogoGeneral;

	
}