package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * Entidad para informe técnico para aprobación de requistos técnicos
 * 
 * @author christian
 * 
 */
@NamedQueries({ @javax.persistence.NamedQuery(name = InformeTecnicoAproReqTec.OBTENER_INFORME_TECNICO_POR_ART, query = "select i from InformeTecnicoAproReqTec i where i.aprobacionRequisitosTecnicosId = :apte_id and i.tipoDocumentoId = :doty_id and i.contadorBandejaTecnico=:trart_time_in_technical and i.estado=true"),
				@javax.persistence.NamedQuery(name = InformeTecnicoAproReqTec.OBTENER_INFORME_TECNICO_POR_NUM_INFORME, query = "select i from InformeTecnicoAproReqTec i where i.numeroOficio=:numeroInforme and i.estado=true")})
@Entity
@Table(name = "technical_report_art", schema = "suia_iii")
@AttributeOverrides({
		@javax.persistence.AttributeOverride(name = "estado", column = @Column(name = "trart_status")),
		@javax.persistence.AttributeOverride(name = "fechaCreacion", column = @Column(name = "trart_creation_date")),
		@javax.persistence.AttributeOverride(name = "fechaModificacion", column = @Column(name = "trart_date_update")),
		@javax.persistence.AttributeOverride(name = "usuarioCreacion", column = @Column(name = "trart_creator_user")),
		@javax.persistence.AttributeOverride(name = "usuarioModificacion", column = @Column(name = "trart_user_update")) })
@Filter(name = "filterActive", condition = "trart_status = 'TRUE'")
public class InformeTecnicoAproReqTec extends EntidadAuditable {

	private static final long serialVersionUID = 1424311780366411580L;

	public static final String OBTENER_INFORME_TECNICO_POR_ART = "ec.gob.ambiente.suia.domain.InformeTecnicoAproReqTec.InformeTecnicoAproReqTecPorART";
	
	public static final String OBTENER_INFORME_TECNICO_POR_NUM_INFORME = "ec.gob.ambiente.suia.domain.InformeTecnicoAproReqTec.InformeTecnicoAproReqTecPorNumInforme";

	public static final String NOMBRE_INFORME_APROBACION_REQUISITO_PDF = "InformeTecnico-AprobacionRequisitos-";
	
	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "technical_report_art_id_generator", initialValue = 1, sequenceName = "seq_trart_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "technical_report_art_id_generator")
	@Column(name = "trart_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_trart_id_apte_id")
	@Filter(name = "filterActive", condition = "trart_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@Column(name = "apte_id", updatable = false, insertable = false)
	private Integer aprobacionRequisitosTecnicosId;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_trart_id_doty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "docu_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_trart_id_documents_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoInformeTecnicoAprobacion;

	@Getter
	@Setter
	@Column(name = "docu_id", insertable = false, updatable = false)
	private Integer documentoInformeAprobacion;

	@Getter
	@Setter
	@Column(name = "doty_id", updatable = false, insertable = false)
	private Integer tipoDocumentoId;

	@Getter
	@Setter
	@Column(name = "trart_report_number")
	private String numeroOficio;
	
	@Getter
	@Setter
	@Column(name = "trart_report_number_old")
	private String numeroOficioAnterior;

	@Getter
	@Setter
	@Column(name = "trart_proponent")
	private String proponente;

	@Getter
	@Setter
	@Column(name = "trart_report_phase")
	private String fase;

	@Getter
	@Setter
	@Column(name = "trart_mode")
	private String modalidad;

	@Getter
	@Setter
	@Column(name = "trart_evaluation_date")
	private String fechaEvaluacion;

	@Getter
	@Setter
	@Column(name = "trart_date")
	private String fecha;

	@Getter
	@Setter
	@Column(name = "trart_technical")
	private String tecnico;

	@Getter
	@Setter
	@Column(name = "trart_project_number")
	private String numeroProyecto;

	@Getter
	@Setter
	@Column(name = "trart_project_name")
	private String nombreProyecto;

	@Getter
	@Setter
	@Column(name = "trart_background")
	private String antecedentes;

	@Getter
	@Setter
	@Column(name = "trart_province")
	private String provinciaProyecto;

	@Getter
	@Setter
	@Column(name = "trart_canton")
	private String cantonProyecto;

	@Getter
	@Setter
	@Column(name = "trart_parish")
	private String parroquiaProyecto;

	@Getter
	@Setter
	@Column(name = "trart_company")
	private String empresa;

	@Getter
	@Setter
	@Column(name = "trart_goals")
	private String objetivos;

	@Getter
	@Setter
	@Column(name = "trart_scope")
	private String alcance;

	@Getter
	@Setter
	@Column(name = "trart_technical_analysis")
	private String analisisTecnico;

	@Getter
	@Setter
	@Column(name = "trart_conclusions")
	private String conclusiones;

	@Getter
	@Setter
	@Column(name = "trart_conclusions_add")
	private String conclusionesAdicional;

	@Getter
	@Setter
	@Column(name = "trart_recommendations")
	private String recomendaciones;

	@Getter
	@Setter
	@Column(name = "trart_pronouncing")
	private String pronunciamiento;

	@Getter
	@Setter
	@Column(name = "trart_corrections")
	private Boolean existeCorrecciones;

	@Getter
	@Setter
	@Column(name = "trart_report_law")
	private String normaVigente;
	
	@Column(name = "trart_time_in_technical")
	@Getter
	@Setter
	private Integer contadorBandejaTecnico;
	
	@Getter
	@Setter
	@Column(name = "trart_evaluation_city")
	private String ciudadEmision;
	
	
	public String getNombreInforme(){
		return NOMBRE_INFORME_APROBACION_REQUISITO_PDF+getNumeroOficio()+".pdf";
	}

}
