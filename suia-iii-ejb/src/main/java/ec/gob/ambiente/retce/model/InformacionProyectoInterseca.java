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
 * The persistent class for the project_information_shapes database table.
 * 
 */
@Entity
@Table(name="project_information_interseca", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "prii_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "prii_create_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "prii_update_date")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prii_create_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prii_update_user")) })
public class InformacionProyectoInterseca extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prii_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "prii_forest_id")
	private Integer bosqueId;

	@Getter
	@Setter
	@Column(name = "prii_forest_name")
	private String bosqueNombre;

	@Getter
	@Setter
	@Column(name = "prii_protected_area_id")
	private Integer areaProtegidaId;

	@Getter
	@Setter
	@Column(name = "prii_protected_area_name")
	private String areaProtegidaNombre;

	@Getter
	@Setter
	@Column(name = "proj_id")
	private Integer proyectoId;
}
