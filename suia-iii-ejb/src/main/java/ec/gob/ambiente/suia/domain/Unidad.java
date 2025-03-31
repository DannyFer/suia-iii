package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the units database table.
 * 
 */
@Entity
@Table(name="units", schema = "public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "unit_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "unit_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "unit_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "unit_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "unit_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unit_status = 'TRUE'")
@NamedQuery(name="Unidad.findAll", query="SELECT u FROM Unidad u")
public class Unidad extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="unit_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="unit_abbreviation")
	private String abreviacion;

	@Getter
	@Setter
	@Column(name="unit_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="unit_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name="unit_order")
	private Integer orden;
	
	@Getter
	@Setter
	@Column(name="unit_type")
	private Integer tipo;

	public Unidad() {
	}

}