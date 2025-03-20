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
 * The persistent class for the parameters_liquid_downloads database table.
 * 
 */
@Entity
@Table(name="parameters_liquid_downloads", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "pald_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "pald_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "pald_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pald_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pald_user_update")) })
public class ParametrosDescargasLiquidas extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pald_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="pald_acronym")
	private String siglas;

	@Getter
	@Setter
	@Column(name="pald_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="pald_density")
	private Double densidad;

	@Getter
	@Setter
	@Column(name="pald_unit")
	private String unidad;
	
	@Getter
	@Setter
	@Column(name="pald_name")
	private String nombre;

	

}