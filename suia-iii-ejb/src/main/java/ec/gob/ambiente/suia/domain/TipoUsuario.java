package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the users_type database table.
 * 
 */
@Entity
@Table(name="users_type", schema = "public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "usty_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "usty_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "usty_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "usty_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "usty_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "usty_status = 'TRUE'")
@NamedQuery(name="TipoUsuario.findAll", query="SELECT t FROM TipoUsuario t")
public class TipoUsuario extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="usty_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="user_id")
	private Usuario usuario;

	@Getter
	@Setter
	@Column(name="usty_date_end")
	private Date fechaFin;
	
	@Getter
	@Setter
	@Column(name="usty_date_start")
	private Date fechaInicio;
	
	@Getter
	@Setter
	@Column(name="usty_type")
	private Integer tipo; //1-subrogante 2-encargado


	public TipoUsuario() {
	}

}