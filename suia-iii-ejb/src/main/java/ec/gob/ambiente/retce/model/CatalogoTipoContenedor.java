package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
/**
 * The persistent class for the retce.catalog_container_type database table.
 * 
 */
@Entity
@Table(name="catalog_container_type", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "cont_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "cont_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "cont_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cont_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cont_user_update")) })
public class CatalogoTipoContenedor extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="cont_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="cont_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name="cont_order")
	private Integer orden;
	
	@Getter
	@Setter
	@Column(name="cont_param")
	private String parametro;
	
}
