package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

/**
 * The persistent class for the fase database table.
 * 
 */
@Entity
@Table(name="catalog_unloading_location", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "unlo_state")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "unlo_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "unlo_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "unlo_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "unlo_user_update")) })
@NamedQueries({
	@NamedQuery(name="LugarDescarga.findAll", query="SELECT m FROM LugarDescarga m")
})
public class LugarDescarga extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="unlo_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="unlo_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name="unlo_order")
	private String orden;

}
