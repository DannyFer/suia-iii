package ec.gob.ambiente.rcoa.viabilidadTecnica.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the technical_viability_project database table.
 * 
 */
@Entity
@Table(name="technical_viability_project", schema="coa_viability_technical")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "tevp_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tevp_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tevp_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tevp_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tevp_user_update")) })
@NamedQuery(name="ViabilidadTecnicaProyecto.findAll", query="SELECT v FROM ViabilidadTecnicaProyecto v")
public class ViabilidadTecnicaProyecto extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="tevp_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prco_id")
	private ProyectoLicenciaCoa proyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="tevi_id")
	private ViabilidadTecnica viabilidadTecnica;
	
	@Getter
	@Setter
	@Column(name="tevp_sanitary_waste")
	private Boolean manejaDesechosSanitarios;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "enre_id ")
	private RegistroAmbientalRcoa registroAmbientalRcoa;

	public ViabilidadTecnicaProyecto() {
	}

}