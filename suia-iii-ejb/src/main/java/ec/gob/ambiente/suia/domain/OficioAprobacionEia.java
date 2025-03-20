package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

@NamedQueries({
	@NamedQuery(name = OficioAprobacionEia.OBTENER_OFICIO_APROBACION_EIA_POR_ESTUDIO_TIPO, 
			query = "select i from OficioAprobacionEia i where i.estudioImpactoAmbientalId = :p_estudioImpactoAmbientalId "
					+ "and i.tipoDocumentoId = :p_tipoDocumentoId"), 
@NamedQuery(name = OficioAprobacionEia.OBTENER_INFORME_TECNICO_EIA_APROBACION_POR_PROYECTO, 
query = "select i from OficioAprobacionEia i where i.estudioImpactoAmbiental.proyectoLicenciamientoAmbiental = :p_proyecto"
		)})
@Entity
@Table(name = "job_approval_eia", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "jaei_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "jaei_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "jaei_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "jaei_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "jaei_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "jaei_status = 'TRUE'")
public class OficioAprobacionEia extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6318425276206201047L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_OFICIO_APROBACION_EIA_POR_ESTUDIO_TIPO = PAQUETE + "OficioAprobacionEia.OficioAprobacionPorEstudioTipo";
	public static final String OBTENER_INFORME_TECNICO_EIA_APROBACION_POR_PROYECTO = PAQUETE + "OficioAprobacionEia.InformeTecnicoEiaAprobacionPorProyecto";


	@Id
	@SequenceGenerator(name = "job_approval_eia_id_generator", initialValue = 1, sequenceName = "seq_jaei_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_approval_eia_id_generator")
	@Column(name = "jaei_id")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_jaei_id_doty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@ManyToOne
	@JoinColumn(name = "eist_id")
	@ForeignKey(name = "fk_jaei_id_eist_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "jaei_status = 'TRUE'")
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
	@Column(name = "jaei_job_number")
	private String numeroOficio;
	@Getter
	@Setter
	@Column(name = "jaei_job_number_old")
	private String numeroOficioAnterior;
	@Getter
	@Setter
	@Column(name = "jaei_job_city")
	private String ciudadInforme;
	@Getter
	@Setter
	@Column(name = "jaei_job_date")
	private String fechaInforme;
	@Getter
	@Setter
	@Column(name = "jaei_subject")
	private String asunto;
	@Getter
	@Setter
	@Column(name = "jaei_receiver_name")
	private String nombreReceptor;
	@Getter
	@Setter
	@Column(name = "jaei_intersection")
	private String interseccion;
	@Getter
	@Setter
	@Column(name = "jaei_update_intersection")
	private String actualizacionCertificadoInterseccion;
	@Getter
	@Setter
	@Column(name = "jaei_number_job_observations")
	private String numeroOficioObservaciones;
	@Getter
	@Setter
	@Column(name = "jaei_date_job_observations")
	private String fechaOficioObservaciones;
	@Getter
	@Setter
	@Column(name = "jaei_date_answers")
	private String fechaRespuestas;
	@Getter
	@Setter
	@Column(name = "jaei_have_observations")
	private boolean tieneObservaciones = false;;
	@Getter
	@Setter
	@Column(name = "jaei_number_pronouncing")
	private String numeroPronunciamiento;
	@Getter
	@Setter
	@Column(name = "jaei_date_pronouncing")
	private String fechaPronunciamiento;
	@Getter
	@Setter
	@Column(name = "jaei_transmitter_pronouncing")
	private String emisorPronunciamiento;
	@Getter
	@Setter
	@Column(name = "jaei_technical_report_number")
	private String numeroInformeTecnico;
	@Getter
	@Setter
	@Column(name = "jaei_technical_report_date")
	private String fechaInformeTecnico;
	@Getter
	@Setter
	@Column(name = "jaei_laws")
	private String disposicionesLegales;
	@Getter
	@Setter
	@Column(name = "jaei_propitious_name")
	private String nombreFavorable;
	@Getter
	@Setter
	@Column(name = "jaei_activities")
	private String actividades;
	@Getter
	@Setter
	@Column(name = "jaei_requirements_1")
	private String requisitos1;
	@Getter
	@Setter
	@Column(name = "jaei_formula")
	private String formula;
	@Getter
	@Setter
	@Column(name = "jaei_daily_inspection_payment")
	private Double pagoInspeccionDiaria;
	@Getter
	@Setter
	@Column(name = "jaei_technical_number")
	private Integer numeroTecnicos;
	@Getter
	@Setter
	@Column(name = "jaei_days_number")
	private Integer numeroDias;
	@Getter
	@Setter
	@Column(name = "jaei_total_value")
	private Double valorTotal;
	@Getter
	@Setter
	@Column(name = "jaei_requirements_2")
	private String requisitos2;
	@Getter
	@Setter
	@Column(name = "jaei_sender_name")
	private String nombreRemitente;

}
