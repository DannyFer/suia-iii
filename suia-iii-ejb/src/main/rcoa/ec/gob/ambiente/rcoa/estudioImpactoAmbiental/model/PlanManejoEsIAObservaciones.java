package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "management_plan_study_observation", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "mpso_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "mpso_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "mpso_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mpso_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mpso_user_update")) })
@NamedQueries({
			@NamedQuery(name="PlanManejoEsIAObservaciones.findAll", query="SELECT p FROM PlanManejoEsIAObservaciones p"),
			@NamedQuery(name=PlanManejoEsIAObservaciones.GET_POR_PLAN_OBSERVACION, query="SELECT d FROM PlanManejoEsIAObservaciones d where d.idPlanManejo = :idPlan and d.nombreClaseObservacion = :observacion order by id desc"),
			@NamedQuery(name=PlanManejoEsIAObservaciones.GET_POR_PLAN_ESTADO, query="SELECT d FROM PlanManejoEsIAObservaciones d where d.idPlanManejo = :idPlan and d.tieneObservaciones = :estado order by id desc")
		})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mpso_status = 'TRUE'")
public class PlanManejoEsIAObservaciones extends EntidadAuditable {

	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.";

	public static final String GET_POR_PLAN_OBSERVACION = PAQUETE + "PlanManejoEsIAObservaciones.getPorPlanObservacion";
	public static final String GET_POR_PLAN_ESTADO = PAQUETE + "PlanManejoEsIAObservaciones.getPorPlanEstado";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mpso_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="maps_id")
	private Integer idPlanManejo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "user_id")
	private Usuario usuarioRegistro;

	@Getter
	@Setter
	@Column(name = "mpso_observation")
	private Boolean tieneObservaciones;

	@Getter
	@Setter
	@Column(name = "mpso_class_name")
	private String nombreClaseObservacion;

}


