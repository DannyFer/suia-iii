package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the entity database table.
 * 
 */
@Entity
@Table(name="entity", schema = "public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "enti_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "enti_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "enti_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enti_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enti_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enti_status = 'TRUE'")
@NamedQuery(name="Entidad.findAll", query="SELECT e FROM Entidad e")
public class Entidad extends EntidadAuditable{
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="enti_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="enti_name")
	private String nombre;
	
	public Entidad() {
	}

	public Entidad(Integer id) {
		super();
		this.id = id;
	}
	
	

}