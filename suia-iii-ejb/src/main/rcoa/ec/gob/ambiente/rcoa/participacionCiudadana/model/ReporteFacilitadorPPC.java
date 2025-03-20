package ec.gob.ambiente.rcoa.participacionCiudadana.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reports_facilitators_ppc", schema = "coa_citizen_participation")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "refp_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "refp_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "refp_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "refp_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "refp_user_update")) 
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "refp_status = 'TRUE'")

public class ReporteFacilitadorPPC extends EntidadAuditable{
	
	private static final long serialVersionUID = 1801511545851715084L;

	@Getter
	@Setter
	@Id
	@Column(name = "refp_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "prfa_id")
	@ForeignKey(name = "fk_prfa_id")
	@Getter
	@Setter
	private ProyectoFacilitadorPPC proyectoFacilitadorPPC;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id")
    private CatalogoGeneralCoa tipoReporte;
	
	@Getter
	@Setter
	@Column(name="refp_code_report")
	private String codigoReporte;
	
	@Getter
	@Setter
	@Column(name="refp_data_sheet_question_one")
	private String comunidadesAreaInfluencia;																																																			
	
	@Getter
	@Setter
	@Column(name="refp_data_sheet_question_two")
	private String numeroRegistroCalificacion;
	
	@Setter
	@Getter
	@Column(name="refp_data_sheet_question_three")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaVisitaPrevia;
	
	@Getter
	@Setter
	@Column(name="refp_data_sheet_question_four")
	private String comunidadesVisitados;
	
	@Getter
	@Setter
	@Column(name="refp_background")
	private String antecedentes;
	
	@Getter
	@Setter
	@Column(name="refp_observations")
	private String observaciones;
	
	@Getter
	@Setter
	@Column(name="refp_conclusion")
	private String conclusion;
	
	@Getter
	@Setter
	@Column(name="refp_recommendation")
	private String recomendacion;
	
	@Getter
	@Setter
	@Column(name="refp_affair")
	private String asunto;
	
	@Getter
	@Setter
	@Column(name="refp_legal_body_text")
	private String cuerpoOficio;
	
	@Getter
	@Setter
	@Column(name="refp_status_fixes")
	private Boolean correccionesInformeOficio;
	
	@Getter
	@Setter
	@Column(name="refp_data_sheet_question_five")
	private String analisisRespuestas;
	
	@Getter
	@Setter
	@Column(name="refp_status_approval_report")
	private Boolean estadoAprobacion;
	
	@Getter
	@Setter
	@Column(name="refp_data_sheet_question_six")
	private String documentoRevision;
	
	@Setter
	@Getter
	@Column(name="refp_data_sheet_question_seven")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaAperturaCentro;
	
	@Setter
	@Getter
	@Column(name="refp_data_sheet_question_eight")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCierreCentro;
	
	@Setter
	@Getter
	@Column(name="refp_data_sheet_question_nine")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaPresentacionPublica;
	
	@Getter
	@Setter
	@Column(name="refp_observations_operator")
	private Boolean existeObservacionesOperador;
	
	@Getter
	@Setter
	@Column(name="refp_data_sheet_question_ten")
	private String accionesRefuerzo;
	
	@Getter
	@Setter
	@Column(name="refp_operator_responsibility")
	private Boolean operadorCumpleResponsabilidades;
	
	@Getter
	@Setter
	@Column(name="refp_facilitator_responsibility")
	private Boolean facilitadorCumpleResponsabilidades;
	
	@Getter
	@Setter
	@Column(name="refp_observations_eia_ppc")
	private Boolean existeObservacionEconomicamente;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="refp_type_users")
    private CatalogoGeneralCoa tipoUsuario;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="lorp_id")
    private CargaDocumentosPPC cargaDocumentosPPC;
	
	@Getter
	@Setter
	@Column(name="refp_technical_evaluation")
	private String evaluacionTecnica;
	
	@Getter
	@Setter
	@Column(name="refp_creation_date_report")
	private Date fechaElaboracion;
	
	@Getter
	@Setter
	@Column(name="refp_task_id")
	private Integer idTarea;
	
	@Getter
	@Setter
	@Column(name="refp_id_previous_report")
	private Integer idInformePrincipal;
	
	@Getter
	@Setter
	@Column(name="refp_review_number")
	private Integer numeroRevision;
	
}
