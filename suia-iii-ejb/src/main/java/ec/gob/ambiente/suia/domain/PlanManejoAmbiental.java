package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table.
 *
 */
@Entity
@Table(name = "environmental_management_plan", catalog = "", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "empl_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "empl_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "empl_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "empl_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "empl_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "empl_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = PlanManejoAmbiental.LISTAR_POR_TIPOID_EIAID, query = "SELECT c FROM PlanManejoAmbiental c WHERE c.tipoPlanManejoAmbiental.id = :p_tipoId and c.estudioImpactoAmbiental = :p_eiaId") })
public class PlanManejoAmbiental extends EntidadAuditable implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2222123796122625158L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_TIPOID_EIAID = PAQUETE + "PlanManejoAmbiental.listarPorTipoId";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "PMA_EMPL_GENERATOR", sequenceName = "seq_empl_id", initialValue = 1, schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PMA_EMPL_GENERATOR")
	@Column(name = "empl_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "empl_environmental_aspect", nullable = true, length = 255)
	private String aspectoAmbiental;

	@Getter
	@Setter
	@Column(name = "empl_identified_impact", nullable = true, length = 255)
	private String impactoIdentificado;

	@Getter
	@Setter
	@Column(name = "empl_proposed_measures", nullable = false, length = 255)
	private String medidaPropuesta;

	@Getter
	@Setter
	@Column(name = "empl_indicators", nullable = false, length = 255)
	private String indicador;

	@Getter
	@Setter
	@Column(name = "empl_verification_means", nullable = false, length = 255)
	private String medioVerificacion;

	@Getter
	@Setter
	@Column(name = "empl_responsible", nullable = false, length = 255)
	private String responsable;

	@Getter
	@Setter
	@Column(name = "empl_frequency", nullable = false)
	private Integer frecuencia;

	@Getter
	@Setter
	@Column(name = "empl_period", nullable = false)
	private String periodo;

	@Getter
	@Setter
	@ManyToOne(optional=false)
	@JoinColumn(name="eist_id")
	@ForeignKey(name="fk_environmental_management_plan_eist_id_environmental_impact_studies_eist_id")
	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@Getter
	@Setter
	@ManyToOne(optional=false)
	@JoinColumn(name="eade_id")
	@ForeignKey(name="fk_environmental_management_eade_id_environmental_aspect_detail_eval_eade_id")
	private DetalleEvaluacionAspectoAmbiental detalleEvaluacionAspectoAmbiental;

	@Getter
	@Setter
	@JoinColumn(name = "empt_id", referencedColumnName = "empt_id", nullable = false)
	@ForeignKey(name = "fk_empt_id_to_empl_id")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private TipoPlanManejoAmbiental tipoPlanManejoAmbiental;


	public PlanManejoAmbiental(Integer id, String aspectoAmbiental,
							   String impactoIdentificado, String medidaPropuesta,
							   String indicador, String medioVerificacion, String responsable,
							   Integer frecuencia, String periodo,
							   TipoPlanManejoAmbiental tipoPlanManejoAmbiental) {
		super();
		this.id = id;
		this.aspectoAmbiental = aspectoAmbiental;
		this.impactoIdentificado = impactoIdentificado;
		this.medidaPropuesta = medidaPropuesta;
		this.indicador = indicador;
		this.medioVerificacion = medioVerificacion;
		this.responsable = responsable;
		this.frecuencia = frecuencia;
		this.periodo = periodo;
		this.tipoPlanManejoAmbiental = tipoPlanManejoAmbiental;
	}

	public PlanManejoAmbiental() {

	}

}