package ec.gob.ambiente.rcoa.viabilidadTecnica.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the medium_frequency_measure database table.
 * 
 */
@Entity
@Table(name="medium_frequency_measure", schema="coa_viability_technical")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "mefm_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "mefm_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "mefm_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mefm_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mefm_user_update")) })
@NamedQuery(name="MedioFrecuenciaMedida.findAll", query="SELECT m FROM MedioFrecuenciaMedida m")
public class MedioFrecuenciaMedida extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="mefm_id")
	private Integer id;	

	@Getter
	@Setter
	@Column(name="mefm_frequency")
	private String frecuencia;

	@Getter
	@Setter
	@Column(name="mefm_measure")
	private String medidaPropuesta;

	@Getter
	@Setter
	@Column(name="mefm_medium")
	private String medioVerificacion;	

	@Getter
	@Setter
	@Column(name="mefm_order")
	private Integer orden;	

	@Getter
	@Setter
	@Column(name="mefm_term")
	private Boolean plazo;	
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "aspe_id")
	private AspectoViabilidadTecnica aspectoViabilidad;

	public MedioFrecuenciaMedida() {
	}	

}