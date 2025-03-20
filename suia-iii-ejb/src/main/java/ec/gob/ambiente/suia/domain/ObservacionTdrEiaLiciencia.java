package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the tdr_eia_license database table.
 * 
 */
@NamedQueries({
		@NamedQuery(name = "ObservacionTdrEiaLiciencia.findAll", query = "SELECT o FROM ObservacionTdrEiaLiciencia o"),
		@NamedQuery(name = ObservacionTdrEiaLiciencia.FIND_BY_TDR, query = "SELECT o FROM ObservacionTdrEiaLiciencia o WHERE o.tdrEia.id = :idTdrEia"),
		@NamedQuery(name = ObservacionTdrEiaLiciencia.FIND_BY_TDR_COMPONENT, query = "SELECT o FROM ObservacionTdrEiaLiciencia o WHERE o.tdrEia.id = :idTdrEia AND o.componente = :componente") })
@Entity
@Table(name = "observation_tdr_eia_license", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "obte_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "obte_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "obte_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "obte_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "obte_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "obte_status = 'TRUE'")
public class ObservacionTdrEiaLiciencia extends EntidadAuditable {

	private static final long serialVersionUID = 1L;
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.ObservacionTdrEiaLiciencia.findAll";
	public static final String FIND_BY_TDR = "ec.com.magmasoft.business.domain.ObservacionTdrEiaLiciencia.findByTdr";
	public static final String FIND_BY_TDR_COMPONENT = "ec.com.magmasoft.business.domain.ObservacionTdrEiaLiciencia.findByTdrComponente";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "OBSERVATION_TDREIALICENSE_obteID_GENERATOR", sequenceName = "seq_obte_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBSERVATION_TDREIALICENSE_obteID_GENERATOR")
	@Column(name = "obte_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "obte_component")
	private String componente;
	// general---------
	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_background")
	private String antecedentes;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_objectives")
	private String objetivos;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_conclusions_recommendations")
	private String conclusionesRecomendaciones;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_observation")
	private String observacion;

	// general-------------

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_general_technical_evaluation")
	private String evaluacionTecnicaGeneral;

	@Getter
	@Setter
	@Column(name = "obte_obs_background")
	private Boolean obs_antecedentes;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_technical_evaluation")
	private String evaluacionTecnica;

	@Getter
	@Setter
	@Column(name = "obte_obs_technical_evaluation")
	private Boolean obs_evaluacionTecnica;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_project_highlight")
	private String caracteristicasImportantesProyecto;

	@Getter
	@Setter
	@Column(name = "obte_obs_project_highlight")
	private String obs_caracteristicasImportantesProyecto;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_technical_data")
	private String fichaTecnica;

	@Getter
	@Setter
	@Column(name = "obte_obs_technical_data")
	private Boolean obs_fichaTecnica;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_introduction")
	private String introduccion;

	@Getter
	@Setter
	@Column(name = "obte_obs_introduction")
	private Boolean obs_introduccion;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_environmental_baseline_diagnosis")
	private String diagnosticoAmbientalLineaBase;

	@Getter
	@Setter
	@Column(name = "obte_obs_environmental_baseline_diagnosis")
	private Boolean obs_diagnosticoAmbientalLineaBase;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_project_description")
	private String descripcionProyecto;

	@Getter
	@Setter
	@Column(name = "obte_obs_project_description")
	private Boolean obs_descripcionProyecto;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_determining_influence_area")
	private String determinacionAreaInfluencia;

	@Getter
	@Setter
	@Column(name = "obte_obs_determining_influence_area")
	private Boolean obs_determinacionAreaInfluencia;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_identification_assessment_evaluation")
	private String identificacionEvaluacionValoracion;

	@Getter
	@Setter
	@Column(name = "obte_obs_identification_assessment_evaluation")
	private Boolean obs_identificacionEvaluacionValoracion;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_contaminated_sites_identification")
	private String identificacionSitiosContaminados;

	@Getter
	@Setter
	@Column(name = "obte_obs_contaminated_sites_identification")
	private Boolean obs_identificacionSitiosContaminados;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_environmental_management_plan")
	private String planManejoAmbiental;

	@Getter
	@Setter
	@Column(name = "obte_obs_environmental_management_plan")
	private Boolean obs_planManejoAmbiental;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_monitoring_plan")
	private String planMonitoreo;

	@Getter
	@Setter
	@Column(name = "obte_obs_monitoring_plan")
	private Boolean obs_planMonitoreo;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_forest_inventory")
	private String inventarioForestal;

	@Getter
	@Setter
	@Column(name = "obte_obs_forest_inventory")
	private Boolean obs_inventarioForestal;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "obte_annexes")
	private String anexos;

	@Getter
	@Setter
	@Column(name = "obte_obs_annexes")
	private Boolean obs_anexos;

	@Getter
	@Setter
	@Column(name = "obte_type")
	private String tipo;

	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name = "tdel_id")
	@ForeignKey(name = "fk_observation_tdr_eia_licensetdel_id_tdr_eia_licensetdel_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tdel_status = 'TRUE'")
	private TdrEiaLicencia tdrEia;

}