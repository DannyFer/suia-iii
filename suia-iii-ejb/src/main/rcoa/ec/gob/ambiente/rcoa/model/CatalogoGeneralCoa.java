package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the general_catalogs_coa database table.
 * 
 */
@Entity
@Table(name="general_catalogs_coa", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "geca_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "geca_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "geca_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "geca_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "geca_user_update")) })
public class CatalogoGeneralCoa extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "geca_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="geca_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name="geca_description")
	private String descripcion;
	
	@ManyToOne
	@JoinColumn(name = "geca_parent_id")	
	@Getter
	@Setter
	private CatalogoGeneralCoa catalogoGeneralCoaPadre;
	
	@ManyToOne
	@JoinColumn(name = "caty_id")	
	@Getter
	@Setter
	private CatalogoTipoCoa catalogoTipoCoa;
	
	@Getter
	@Setter
	@Column(name="geca_order")
	private Integer orden;
	
	@Getter
	@Setter
	@Column(name="geca_value")
	private String valor;
	
	@Getter
	@Setter
	@Column(name="geca_code")
	private String codigo;
	
	public CatalogoGeneralCoa() {}
	
	public CatalogoGeneralCoa(int id) {
		this.id=id;
	}
}