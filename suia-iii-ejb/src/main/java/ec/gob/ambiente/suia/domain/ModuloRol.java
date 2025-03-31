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
 * The persistent class for the modules_rol database table.
 * 
 */
@Entity
@Table(name="modules_rol", schema = "public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "moro_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "moro_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "moro_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "moro_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "moro_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "moro_status = 'TRUE'")
@NamedQuery(name="ModuloRol.findAll", query="SELECT m FROM ModuloRol m")
public class ModuloRol extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="moro_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="modu_id")
	private Modulo modulo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="role_id")
	private Rol rol;
	
	public ModuloRol() {
	}	

}