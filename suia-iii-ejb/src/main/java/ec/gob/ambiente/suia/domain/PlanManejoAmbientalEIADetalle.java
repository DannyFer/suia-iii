package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table.
 *
 */
@Entity
@Table(name = "environmental_management_plan_eia_detail", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "empd_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "empd_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = PlanManejoAmbientalEIADetalle.LISTAR_POR_TIPO, query = "SELECT c FROM PlanManejoAmbientalEIADetalle c WHERE c.tipoPlan= :p_tipo and c.planManejoAmbientalEIA.id = :p_pma and c.idHistorico = null order by 1"),
@NamedQuery(name = PlanManejoAmbientalEIADetalle.LISTAR_POR_TIPO_EIA, query = "SELECT c FROM PlanManejoAmbientalEIADetalle c WHERE c.tipoPlan in('11','12','13','14','15','16','17','18') and c.planManejoAmbientalEIA.id = :p_pma and c.idHistorico = null"), 
@NamedQuery(name = PlanManejoAmbientalEIADetalle.LISTAR_TODOS_POR_TIPO_EIA, query = "SELECT c FROM PlanManejoAmbientalEIADetalle c WHERE c.tipoPlan in('11','12','13','14','15','16','17','18') and c.planManejoAmbientalEIA.id = :p_pma")})
public class PlanManejoAmbientalEIADetalle extends EntidadBase implements Serializable, Cloneable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2222123796122625158L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_TIPO = PAQUETE + "PlanManejoAmbientalEIADetalle.listarPorTipo";
	public static final String LISTAR_POR_TIPO_EIA = PAQUETE + "PlanManejoAmbientalEIADetalle.listarPorTipoEia";
	public static final String LISTAR_TODOS_POR_TIPO_EIA = PAQUETE + "PlanManejoAmbientalEIADetalle.listarTodosPorTipoEia";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "PMA_EIA_EMPD_GEN", sequenceName = "seq_empd_eia_id", initialValue = 1, schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PMA_EIA_EMPD_GEN")
	@Column(name = "empd_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "empl_id", referencedColumnName = "empl_id")
	@ForeignKey(name = "fk_env_man_plan_empl_id_env_man_plan_empl_id")
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private PlanManejoAmbientalEIA planManejoAmbientalEIA;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "eade_id", nullable=true)
	@ForeignKey(name = "fk_env_man_plan_id_env_imp_ide_id")
	private DetalleEvaluacionAspectoAmbiental detalleEvaluacionLista;

	@Getter
	@Setter
	@Column(name = "empd_component_enviromental", length = 255)
	private String componenteAmbiental;

	@Getter
	@Setter
	@Column(name = "empd_proposed_measures", length = 255)
	private String medidaPropuesta;

	@Getter
	@Setter
	@Column(name = "empd_indicators", length = 255)
	private String indicador;

	@Getter
	@Setter
	@Column(name = "empd_verification_means", length = 255)
	private String medioVerificacion;

	@Getter
	@Setter
	@Column(name = "empd_responsible", length = 255)
	private String responsable;

	@Getter
	@Setter
	@Column(name = "empd_frequency")
	private Integer frecuencia;

	@Getter
	@Setter
	@Column(name = "empd_period")
	private String periodo;

	@Getter
	@Setter
	@Column(name = "empd_risk")
	private String riesgo;

	@Getter
	@Setter
	@Column(name = "empd_plan_type")
	private String tipoPlan;

	@Getter
	@Setter
	@Column(name = "empd_edited_register")
	private Integer registroEditado;

	@Getter
	@Setter
	@Column(name = "empd_is_change")
	private boolean cambio;
	
	@Getter
	@Setter
	@Column(name = "empd_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "empd_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;


	public PlanManejoAmbientalEIADetalle() {

	}
	
	public PlanManejoAmbientalEIADetalle(Integer id) {
		this.id = id;
	}
	
	@Override
	public PlanManejoAmbientalEIADetalle clone() throws CloneNotSupportedException {

		 PlanManejoAmbientalEIADetalle clone = (PlanManejoAmbientalEIADetalle)super.clone();
		 clone.setId(null);		 
		 return clone;
	}	
	
	//Cris F: cambpo para guardar la fecha para historial
	@Getter
	@Setter
	@Column(name = "empd_date_create")
	private Date fechaCreacion;

}