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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the catalog_download_type database table.
 * 
 */
@Entity
@Table(name="catalog_download_type", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "cadt_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "cadt_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "cadt_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cadt_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cadt_user_update")) })
public class CatalogoTipoDescarga extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cadt_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="cadt_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="cadt_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="cadt_param")
	private String parametro;



}