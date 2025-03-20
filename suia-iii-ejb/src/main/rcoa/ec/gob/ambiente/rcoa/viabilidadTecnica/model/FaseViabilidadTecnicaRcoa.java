package ec.gob.ambiente.rcoa.viabilidadTecnica.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.rcoa.model.FasesCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the technical_viability_phase database table.
 * 
 */
@Entity
@Table(name="technical_viability_phase", schema="coa_viability_technical")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "tvph_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tvph_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tvph_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tvph_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tvph_user_update")) })
@NamedQuery(name="FaseViabilidadTecnica.findAll", query="SELECT f FROM FaseViabilidadTecnica f")
public class FaseViabilidadTecnicaRcoa extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="tvph_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="phas_id")
	private FasesCoa fasesCoa;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="tevp_id")
	private ViabilidadTecnicaProyecto viabilidadTecnicaProyecto;
	
	@Getter
	@Setter
	@Column(name="tvph_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="tvph_end_date")
	private Date fechaFin;
	
	@Getter
	@Setter
	@Column(name="tvph_start_date")
	private Date fechaInicio;
	
	@Getter
	@Setter
	@Transient
	private boolean nuevoRegistro;

	public FaseViabilidadTecnicaRcoa() {
	}

}