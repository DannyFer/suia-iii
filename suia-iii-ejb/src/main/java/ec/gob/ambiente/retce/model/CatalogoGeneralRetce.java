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
 * The persistent class for the general_catalog database table.
 * 
 */
@Entity
@Table(name="general_catalog", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "gect_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "gect_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "gect_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "gect_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "gect_user_update")) })
public class CatalogoGeneralRetce extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="gect_id")
	private Integer id;

	@Column(name="gect_description")
	private String descripcion;

	@Column(name="gect_code")
	private String codigo;

}