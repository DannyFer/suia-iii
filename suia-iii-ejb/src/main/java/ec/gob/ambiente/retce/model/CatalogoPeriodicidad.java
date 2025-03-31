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
 * The persistent class for the retce.catalog_periodicity database table.
 * 
 */
@Entity
//@Table(name="catalog_periodicity", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "peri_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "peri_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "peri_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "peri_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "peri_user_update")) })
public class CatalogoPeriodicidad extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="peri_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="peri_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name="peri_order")
	private Integer orden;
	
	@Getter
	@Setter
	@Column(name="peri_param")
	private String parametro;

}
