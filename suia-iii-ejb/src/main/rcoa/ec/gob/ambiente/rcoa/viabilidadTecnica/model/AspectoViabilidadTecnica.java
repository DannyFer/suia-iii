package ec.gob.ambiente.rcoa.viabilidadTecnica.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.rcoa.model.PlanManejoAmbientalPma;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the aspect database table.
 * 
 */
@Entity
@Table(name="aspect", schema="coa_viability_technical")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "aspe_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "aspe_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "aspe_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "aspe_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "aspe_user_update")) })
@NamedQuery(name="AspectoViabilidadTecnica.findAll", query="SELECT a FROM AspectoViabilidadTecnica a")
public class AspectoViabilidadTecnica extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="aspe_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="aspe_aspect")
	private String aspecto;

	@Getter
	@Setter
	@Column(name="aspe_status_management")
	private Boolean manejaDesechos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="mapr_id")
	private PlanManejoAmbientalPma planManejoAmbientalPma;

	public AspectoViabilidadTecnica() {
	}

}