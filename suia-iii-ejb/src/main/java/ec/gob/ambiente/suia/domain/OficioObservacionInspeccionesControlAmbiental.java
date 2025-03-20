package ec.gob.ambiente.suia.domain;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@NamedQueries({
		@NamedQuery(name = OficioObservacionInspeccionesControlAmbiental.GET_BY_SOLICITUD_INSPECCION, query = "SELECT o FROM OficioObservacionInspeccionesControlAmbiental o WHERE o.solicitudInspeccionControlAmbiental.id = :idSolicitudInspeccionControlAmbiental and o.tipoDocumento.id = :idTipoDocumento and o.processInstanceId = :idInstanciaProceso and o.contadorBandejaTecnico = :contadorBandejaTecnico") })
@Entity
@Table(name = "office_observation_inspection_requests", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ooir_status") ),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ooir_creation_date") ),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ooir_date_update") ),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ooir_creator_user") ),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ooir_user_update") ) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ooir_status = 'TRUE'")
public class OficioObservacionInspeccionesControlAmbiental extends EntidadAuditable {

	private static final long serialVersionUID = -3683287241934149025L;

	public static final String GET_BY_SOLICITUD_INSPECCION = "ec.gob.ambiente.suia.domain.OficioObservacionInspeccionesControlAmbiental.get_by_solicitud_inspeccion";

	@Id
	@SequenceGenerator(name = "OFFICE_OBSERVATION_ICA_GENERATOR", sequenceName = "seq_ooir_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OFFICE_OBSERVATION_ICA_GENERATOR")
	@Column(name = "ooir_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "ooir_time_in_technical")
	@Getter
	@Setter
	private Integer contadorBandejaTecnico;

	@Column(name = "ooir_process_instance_id")
	@Getter
	@Setter
	private Long processInstanceId;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_ooir_id_doty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "doty_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@ManyToOne
	@JoinColumn(name = "eire_id")
	@ForeignKey(name = "fk_ooir_id_eire_")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eire_status = 'TRUE'")
	@Getter
	@Setter
	private SolicitudInspeccionControlAmbiental solicitudInspeccionControlAmbiental;

	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_ooir_id_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento documento;

	@Getter
	@Setter
	@Column(name = "ooir_office_number")
	private String numero;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ooir_report_date")
	private Date fecha;

	@Getter
	@Setter
	@Transient
	private String fechaMostrar;

	@Getter
	@Setter
	@Transient
	private String sujetoControl;

	@ManyToOne
	@JoinColumn(name = "user_evaluator_id")
	@ForeignKey(name = "fk_ooir_id_user_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "user_status = 'TRUE'")
	@Getter
	@Setter
	private Usuario evaluador;

	@Getter
	@Setter
	@Transient
	private String paraProyecto;

	@Getter
	@Setter
	@Transient
	private String solicitud;

	@Getter
	@Setter
	@Transient
	private String fechaSolicitud;

	@Getter
	@Setter
	@Transient
	private String empresaCargo;

	@Getter
	@Setter
	@Column(name = "ooir_office_compliment", columnDefinition="text")
	private String cumplimiento;

	@Getter
	@Setter
	@Transient
	private String numeroInforme;

	@Getter
	@Setter
	@Transient
	private String fechaInforme;

	@Getter
	@Setter
	@Column(name = "ooir_office_set", columnDefinition="text")
	private String establecido;

	@Getter
	@Setter
	@Column(name = "ooir_office_comments", columnDefinition="text")
	private String observaciones;

	@Getter
	@Setter
	@Column(name = "ooir_office_recommendations", columnDefinition="text")
	private String recomendaciones;

	@Getter
	@Setter
	@Transient
	private String autoridad;

	@Getter
	@Setter
	@Transient
	private byte[] archivoOficio;

	@Getter
	@Setter
	@Transient
	private String nombreEmpresaMostrar;

	@Getter
	@Setter
	@Transient
	private String cargoRepresentanteLegalMostrar;

	@Getter
	@Setter
	@Transient
	private String cargoAutoridad;

	@Getter
	@Setter
	@Transient
	private String sujetoControlEncabez;

	@Getter
	@Setter
	@Transient
	private String deLaEmpresa;

	@Getter
	@Setter
	@Transient
	private String nombreFichero;

	@Getter
	@Setter
	@Transient
	private String oficioPath;

	@Getter
	@Setter
	@Transient
	private String oficioRealPath;

	@Getter
	@Setter
	@Transient
	private String nombreReporte;

	@Getter
	@Setter
	@Transient
	private String sector;

	@Getter
	@Setter
	@Transient
	private String proyectoCodigo;

	@Getter
	@Setter
	@Transient
	private String proyectoFecha;
}
