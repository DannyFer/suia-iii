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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the fuel_type database table.
 * 
 */
@Entity
@Table(name="fuel_type", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "futy_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "futy_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "futy_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "futy_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "futy_user_update")) })
public class TipoCombustibleRetce extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="futy_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="fsco_id")
	private FuenteFijaCombustion fuenteFijaCombustion;
	
	@Getter
	@Setter
	@Column(name="futy_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="futy_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="futy_oxygen_correction_percentage")
	private Double porcentajeCorrecionOxigeno;	
	
	@Getter
	@Setter
	@Column(name="futy_state")
	private String estadoCombustible;
	
	@Getter
	@Setter
	@Column(name = "futy_table_name")
	private String nombreTabla;
	
	@Getter
	@Setter
	@Column(name="futy_has_maximum_permissible_limits")
	private Boolean tieneLimitesMaximos;

}