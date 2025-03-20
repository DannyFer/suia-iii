package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the entity_rol database table.
 * 
 */
@Entity
@Table(name="entity_rol", schema = "public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "enro_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "enro_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "enro_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enro_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enro_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enro_status = 'TRUE'")
@NamedQuery(name="EntidadRol.findAll", query="SELECT e FROM EntidadRol e")
public class EntidadRol extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="enro_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="enti_id")
	private Entidad entidad;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="role_id")
	private Rol rol;

	public EntidadRol() {
	}

	public EntidadRol(Integer id) {
		super();
		this.id = id;
	}

	

}