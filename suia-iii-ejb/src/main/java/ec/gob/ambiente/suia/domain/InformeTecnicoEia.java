package ec.gob.ambiente.suia.domain;

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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({
	@NamedQuery(name = InformeTecnicoEia.OBTENER_INFORME_TECNICO_EIA_POR_ESTUDIO_TIPO, 
			query = "select i from InformeTecnicoEia i where i.estudioImpactoAmbientalId = :p_estudioImpactoAmbientalId "
					+ "and i.tipoDocumentoId = :p_tipoDocumentoId"),
					@NamedQuery(name = InformeTecnicoEia.OBTENER_INFORME_TECNICO_EIA_POR_PROYECTO, 
					query = "select i from InformeTecnicoEia i where i.estudioImpactoAmbiental.proyectoLicenciamientoAmbiental = :p_proyecto"
							)})
@Entity
@Table(name = "technical_report_eia", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "trei_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "trei_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "trei_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "trei_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "trei_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "trei_status = 'TRUE'")
public class InformeTecnicoEia extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6318425276206201047L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_INFORME_TECNICO_EIA_POR_ESTUDIO_TIPO = PAQUETE + "InformeTecnicoEia.InformeTecnicoEiaPorEstudioTipo";
	
	public static final String OBTENER_INFORME_TECNICO_EIA_POR_PROYECTO = PAQUETE + "InformeTecnicoEia.InformeTecnicoEiaPorProyecto";
	
	@Id
	@SequenceGenerator(name = "technical_report_eia_id_generator", initialValue = 1, sequenceName = "seq_trei_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "technical_report_eia_id_generator")
	@Column(name = "trei_id")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_trei_id_doty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "eist_id")
	@ForeignKey(name = "fk_trei_id_eist_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "trei_status = 'TRUE'")
	@Getter
	@Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental; 

	@Getter
	@Setter
	@Column(name = "eist_id", updatable = false, insertable = false)
	private Integer estudioImpactoAmbientalId;

	@Getter
	@Setter
	@Column(name = "doty_id", updatable = false, insertable = false)
	private Integer tipoDocumentoId;

	@Getter
	@Setter
	@Column(name = "trei_report_number")
	private String numeroInforme;
	@Getter
	@Setter
	@Column(name = "trei_report_number_old")
	private String numeroInformeAnterior;
	@Getter
	@Setter
	@Column(name = "trei_report_city")
	private String ciudadInforme;
	@Getter
	@Setter
	@Column(name = "trei_report_date")
	private String fechaInforme;
	@Getter
	@Setter
	@Column(name = "trei_antecendentes")
	private String antecendentes;
	@Getter
	@Setter
	@Column(name = "trei_goals")
	private String objetivos;
	@Getter
	@Setter
	@Column(name = "trei_project_features")
	private String caracteristicasProyecto;
	@Getter
	@Setter
	@Column(name = "trei_technical_evaluation")
	private String evaluacionTecnica;
	@Getter
	@Setter
	@Column(name = "trei_conclusions_recommendations")
	private String conclusionesRecomendaciones;
	@Getter
	@Setter
	@Column(name = "trei_observations")
	private String observaciones;
	@Getter
	@Setter
	@Column(name = "trei_other_obligations")
	private String otrasObligaciones;
	@Getter
	@Setter
	@Column(name = "trei_pronouncing")
	private String pronunciamiento;
	
	@Getter
	@Setter
	@Column(name = "trei_number")
	private Integer numero;
	
	@Getter
	@Setter
	@Column(name = "trei_finalized")
	private Boolean finalizado;
	
	

	public InformeTecnicoEia() {
		super();
	}

	public InformeTecnicoEia(Integer id) {
		super();
		this.id = id;
	}	

}
