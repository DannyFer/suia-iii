package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the catalog_receiver_body_type database table.
 * 
 */
@Entity
@Table(name="catalog_receiver_body_type", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "crbt_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "crbt_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "crbt_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "crbt_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "crbt_user_update")) })
public class CatalogoTipoCuerpoReceptor extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="crbt_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="crbt_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="crbt_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="crbt_param")
	private String parametro;
	
}