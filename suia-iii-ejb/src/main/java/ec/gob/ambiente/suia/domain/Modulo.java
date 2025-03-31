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
 * The persistent class for the modules database table.
 * 
 */
@Entity
@Table(name="modules", schema = "public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "modu_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "modu_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "modu_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "modu_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "modu_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "modu_status = 'TRUE'")
@NamedQuery(name="Modulo.findAll", query="SELECT m FROM Modulo m")
public class Modulo extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="modu_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="modu_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name="modu_order")
	private Integer orden;

	public Modulo() {
	}

}