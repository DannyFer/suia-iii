package ec.gob.ambiente.suia.domain;


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
 * The persistent class for the retce.catalog_services database table.
 * 
 */
@Entity
@Table(name="url_bi", schema="public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "urbi_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "urbi_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "urbi_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "urbi_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "urbi_user_update")) })
public class UrlBiReportes extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="urbi_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="urbi_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name="urbi_url")
	private String url;
	
	@Getter
	@Setter
	@Column(name="urbi_type")
	private Integer tipoUrl;
	
	@Getter
	@Setter
	@Column(name="urbi_menu")
	private Integer tipoReporte;
	
	@Getter
	@Setter
	@Column(name="urbi_value")
	private Integer valorId;
}