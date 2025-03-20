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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the sample_type database table.
 * 
 */
@Entity
@Table(name="sample_type", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "saty_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "saty_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "saty_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "saty_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "saty_user_update")) })
public class TipoMuestra extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="saty_id")
	private Integer id;	

	@Getter
	@Setter
	@Column(name="saty_description")
	private String descripcion;

	@Setter
	@Getter
	@Column(name="saty_order")
	private Integer orden;

	@Setter
	@Getter
	@Column(name="saty_param")
	private String parametro;

}