package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "managament_plan_study_program", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "mpsp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "mpsp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "mpsp_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mpsp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mpsp_user_update")) })
@NamedQueries({
			@NamedQuery(name="ProgramaPlanManejoEsIA.findAll", query="SELECT p FROM ProgramaPlanManejoEsIA p"),
			@NamedQuery(name=ProgramaPlanManejoEsIA.GET_PRINCIPALES_POR_PLAN, query="SELECT d FROM ProgramaPlanManejoEsIA d where d.idPlanManejo = :idPlan order by id")
		})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mpsp_status = 'TRUE'")
public class ProgramaPlanManejoEsIA extends EntidadAuditable {

	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.";

	public static final String GET_PRINCIPALES_POR_PLAN = PAQUETE + "DetallePlanManejoEsIA.getPorPlan";
	public static final String GET_POR_PADRE = PAQUETE + "DetallePlanManejoEsIA.getPorPadre";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mpsp_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="maps_id")
	private Integer idPlanManejo;

	@Getter
	@Setter
	@Column(name = "mpsp_name_program")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "mpsp_objective_program")
	private String objetivo;

	@Getter
	@Setter
	@Column(name = "mpsp_cost")
	private Double costoPrograma;

	@Getter
    @Setter
    @Transient
    private List<DetallePlanManejoEsIA> listaDetallePlan;

}


