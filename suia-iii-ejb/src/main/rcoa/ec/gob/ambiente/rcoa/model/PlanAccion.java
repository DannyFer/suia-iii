package ec.gob.ambiente.rcoa.model;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


@Entity
@Table(name="project_licencing_coa_action_plan", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prap_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prap_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prap_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prap_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prap_user_update")) })

@NamedQueries({ 
@NamedQuery(name=PlanAccion.GET_POR_PROYECTO, query="SELECT a FROM PlanAccion a where a.proyectoLicenciaCoa.id = :idProyecto and a.estado = true order by id desc")
})
public class PlanAccion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String GET_POR_PROYECTO = "ec.gob.ambiente.rcoa.model.PlanAccion.getPorProyecto";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prap_id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "fk_prco_id")
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;

	@Getter
	@Setter
	@Column(name="prap_objective")
	private String objetivo;

	@Getter
	@Setter
	@Column(name="prap_responsible")
	private String responsable;

	@Getter
	@Setter
	@Column(name="prap_sign")
	private Boolean firmado;

	@Getter
	@Setter
	@Column(name="prap_send")
	private Boolean enviado;


}