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
 * The persistent class for the maximum_permissible_limits database table.
 * 
 */
@Entity
@Table(name="maximum_permissible_limits", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "mapl_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "mapl_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "mapl_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mapl_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mapl_user_update")) })
public class LimiteMaximoPermitido extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="mapl_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="auth_id")
	private AutorizacionEmisiones autorizacionEmisiones;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="futy_id")
	private TipoCombustibleRetce tipoCombustible;
	
	@Getter
	@Setter
	@Column(name="mapl_value")
	private Double valor;
	
	@Getter
	@Setter
	@Column(name="mapl_calculated")
	private boolean parametroCalculado;
	
	@Getter
	@Setter
	@Column(name="mapl_application")
	private String applicacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="para_id")
	private Parametro parametro;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="fsco_id")
	private FuenteFijaCombustion fuenteFijaCombustion;
	
}