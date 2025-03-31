package ec.gob.ambiente.retce.model;

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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the fixed_source_combustion database table.
 * 
 */
@Entity
@Table(name="fixed_source_combustion", schema = "retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "fsco_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "fsco_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "fsco_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fsco_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fsco_user_update")) })
public class FuenteFijaCombustion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="fsco_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="fsco_correction_percent")
	private Double porcentajeCorrecion;

	@Getter
	@Setter
	@Column(name="fsco_normative")
	private String normativa;

	@Getter
	@Setter
	@Column(name="fsco_source")
	private String fuente;

	@Getter
	@Setter
	@Column(name="fsco_table_name")
	private String nombreTabla;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="sety_id")	
	private TipoSector tipoSector;
	
	@Getter
	@Setter
	@Column(name = "fsco_authorization_type_source")
	private Integer autorizacionTipoFuente;

	

}