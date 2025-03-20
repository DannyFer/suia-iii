package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the catalog_types_coa database table.
 * 
 */
@Entity
@Table(name="catalog_types_coa", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "caty_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "caty_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "caty_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "caty_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "caty_user_update")) })
public class CatalogoTipoCoa extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "caty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="caty_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name="caty_description")
	private String descripcion;
	
	public CatalogoTipoCoa() {}

	public CatalogoTipoCoa(int id) {
		this.id=id;
	}
}