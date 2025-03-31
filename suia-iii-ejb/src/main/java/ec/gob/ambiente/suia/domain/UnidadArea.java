package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the units database table.
 * 
 */
@Entity
@Table(name="units_areas", schema = "public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "unar_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "unar_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "unar_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "unar_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "unar_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unar_status = 'TRUE'")
@NamedQuery(name="UnidadArea.findAll", query="SELECT u FROM UnidadArea u")
public class UnidadArea extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="unar_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="area_id")
	private Area area;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "unit_id")
	private Unidad unidad;

}