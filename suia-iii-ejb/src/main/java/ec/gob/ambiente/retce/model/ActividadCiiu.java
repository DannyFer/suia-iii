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
 * The persistent class for the retce.activities_ciiu database table.
 * 
 */
@Entity
@Table(name="activities_ciiu", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "acti_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "acti_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "acti_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "acti_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "acti_user_update")) })
public class ActividadCiiu extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public ActividadCiiu() {
		// TODO Auto-generated constructor stub
	}
	
	public ActividadCiiu(Integer id) {
		this.id=id;
	}
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acti_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="acti_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name="acti_order")
	private Integer orden;
	
	@Getter
	@Setter
	@Column(name="acti_code")
	private String codigo;
	
	@Getter
	@Setter
	@Column(name="acti_observation")
	private String observacion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="acti_parent_id")
	private ActividadCiiu actividadCiiu;
}

