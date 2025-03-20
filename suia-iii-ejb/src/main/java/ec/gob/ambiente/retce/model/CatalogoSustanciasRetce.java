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
 * The persistent class for the catalog_substances_retce database table.
 * 
 */
@Entity
@Table(name="catalog_substances_retce", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "csre_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "csre_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "csre_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "csre_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "csre_user_update")) })
public class CatalogoSustanciasRetce extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="csre_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="csre_agreement")
	private String convenio;

	@Getter
	@Setter
	@Column(name="csre_chemical_product_description")
	private String descripcionProductoQuimico;

	@Getter
	@Setter
	@Column(name="csre_order")
	private Integer orden;
	
	@Getter
	@Setter
	@Column(name="csre_component")
	private Integer tipoComponente;
	
}