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
 * The persistent class for the parameters database table.
 * 
 */
@Entity
@Table(name="parameters", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "para_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "para_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "para_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "para_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "para_user_update")) })
public class Parametro extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="para_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="para_acronym")
	private String siglas;

	@Getter
	@Setter
	@Column(name="para_density")
	private Double densidad;

	@Getter
	@Setter
	@Column(name="para_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name="para_unit")
	private String unidad;

}