package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the areas_users database table.
 * 
 */
@Entity
@Table(name="areas_users", schema = "public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "arus_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "arus_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "arus_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "arus_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "arus_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "arus_status = 'TRUE'")
@NamedQuery(name="AreaUsuario.findAll", query="SELECT a FROM AreaUsuario a")
public class AreaUsuario extends EntidadAuditable{
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="arus_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="area_id")
	private Area area;

	@Getter
	@Setter
	@Column(name="arus_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="arus_principal")
	private Boolean principal;

	@Getter
	@Setter
	@Column(name="arus_user_assigns")
	private Integer usuarioAsignaArea;

	@Getter
	@Setter
	@Column(name="arus_user_withdraw")
	private Integer usuarioRetiraArea;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="user_id")
	private Usuario usuario;
	
	@Getter
	@Setter
	@Column(name="user_id", insertable = false, updatable = false)
	private Integer idUsuario;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="unar_id")
	private UnidadArea unidadArea;

	public AreaUsuario() {
	}

}