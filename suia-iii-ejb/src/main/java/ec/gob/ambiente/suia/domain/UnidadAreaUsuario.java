package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the units_areas_users database table.
 * 
 */
@Entity
@Table(name="units_areas_users", schema = "public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "unau_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "unau_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "unau_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "unau_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "unau_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unau_status = 'TRUE'")
@NamedQuery(name="UnidadAreaUsuario.findAll", query="SELECT u FROM UnidadAreaUsuario u")
public class UnidadAreaUsuario extends EntidadAuditable  {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="unau_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="arus_id")
	private AreaUsuario areaUsuario;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="unar_id")
	private UnidadArea unidadArea;

	public UnidadAreaUsuario() {
	}

}