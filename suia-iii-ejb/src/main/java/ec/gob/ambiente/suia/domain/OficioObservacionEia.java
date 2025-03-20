package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({
	@NamedQuery(name = OficioObservacionEia.OBTENER_OFICIO_APROBACION_EIA_POR_ESTUDIO_TIPO, 
			query = "select i from OficioObservacionEia i where i.estudioImpactoAmbientalId = :p_estudioImpactoAmbientalId "
					+ "and i.tipoDocumentoId = :p_tipoDocumentoId order by 1 desc"),
	@NamedQuery(name = OficioObservacionEia.OBTENER_OFICIO_APROBACION_POR_PROYECTO, 
	query = "select i from OficioObservacionEia i where i.estudioImpactoAmbiental.proyectoLicenciamientoAmbiental = :p_proyecto"
			)})
@Entity
@Table(name = "job_observation_eia", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "joei_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "joei_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "joei_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "joei_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "joei_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "joei_status = 'TRUE'")
public class OficioObservacionEia extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6318425276206201047L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_OFICIO_APROBACION_EIA_POR_ESTUDIO_TIPO = PAQUETE + "OficioObservacionEia.OficioAprobacionPorEstudioTipo";
	public static final String OBTENER_OFICIO_APROBACION_POR_PROYECTO = PAQUETE + "OficioObservacionEia.OficioAprobacionPorProyecto";
	

	@Id
	@SequenceGenerator(name = "job_observation_eia_id_generator", initialValue = 1, sequenceName = "seq_joei_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_observation_eia_id_generator")
	@Column(name = "joei_id")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_joei_id_doty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@ManyToOne
	@JoinColumn(name = "eist_id")
	@ForeignKey(name = "fk_joei_id_eist_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "joei_status = 'TRUE'")
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
	@Column(name = "joei_job_number")
	private String numeroOficio;
	@Getter
	@Setter
	@Column(name = "joei_job_number_old")
	private String numeroOficioAnterior;
	@Getter
	@Setter
	@Column(name = "joei_job_city")
	private String ciudadInforme;
	@Getter
	@Setter
	@Column(name = "joei_job_date")
	private String fechaInforme;
	@Getter
	@Setter
	@Column(name = "joei_subject")
	private String asunto;
	@Getter
	@Setter
	@Column(name = "joei_receiver_name")
	private String nombreReceptor;
	@Getter
	@Setter
	@Column(name = "joei_number_job_observations")
	private String numeroOficioObservaciones;
	@Getter
	@Setter
	@Column(name = "joei_date_job_observations")
	private String fechaOficioObservaciones;
	@Getter
	@Setter
	@Column(name = "joei_date_answers")
	private String fechaRespuestas;
	@Getter
	@Setter
	@Column(name = "joei_have_observations")
	private boolean tieneObservaciones = false;
	@Getter
	@Setter
	@Column(name = "joei_number_pronouncing")
	private String numeroPronunciamiento;
	@Getter
	@Setter
	@Column(name = "joei_date_pronouncing")
	private String fechaPronunciamiento;
	@Getter
	@Setter
	@Column(name = "joei_transmitter_pronouncing")
	private String emisorPronunciamiento;
	@Getter
	@Setter
	@Column(name = "joei_technical_report_number")
	private String numeroInformeTecnico;
	@Getter
	@Setter
	@Column(name = "joei_technical_report_date")
	private String fechaInformeTecnico;
	@Getter
	@Setter
	@Column(name = "joei_laws")
	private String disposicionesLegales;
	@Getter
	@Setter
	@Column(name = "joei_observations_list")
	private String listaObservaciones;
	@Getter
	@Setter
	@Column(name = "joei_sender_name")
	private String nombreRemitente;

}
