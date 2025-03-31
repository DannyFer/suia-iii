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
 * The persistent class for the catalog_estimation_method database table.
 * 
 */
@Entity
@Table(name="catalog_estimation_method", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "caem_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "caem_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "caem_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "caem_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "caem_user_update")) })
public class CatalogoMetodoEstimacion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="caem_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="caem_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="caem_order")
	private Integer orden;

}