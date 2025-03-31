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
import javax.persistence.Lob;
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
		@NamedQuery(name = InformeTecnicoInspeccionesControlAmbiental.GET_BY_SOLICITUD_INSPECCION, query = "SELECT i FROM InformeTecnicoInspeccionesControlAmbiental i WHERE i.solicitudInspeccionControlAmbiental.id = :idSolicitudInspeccionControlAmbiental and i.tipoDocumento.id = :idTipoDocumento and i.processInstanceId = :idInstanciaProceso and i.contadorBandejaTecnico = :contadorBandejaTecnico") })
@Entity
@Table(name = "technical_report_environmental_inspections", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "trei_status") ),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "trei_creation_date") ),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "trei_date_update") ),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "trei_creator_user") ),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "trei_user_update") ) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "trei_status = 'TRUE'")
public class InformeTecnicoInspeccionesControlAmbiental extends EntidadAuditable {

	private static final long serialVersionUID = 8342862025659256298L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String GET_BY_SOLICITUD_INSPECCION = PAQUETE
			+ "InformeTecnicoInspeccionesControlAmbiental.get_by_solicitud_inspeccion";

	@Id
	@SequenceGenerator(name = "TECHNICAL_REPORT_ICA_GENERATOR", sequenceName = "seq_trei_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECHNICAL_REPORT_ICA_GENERATOR")
	@Column(name = "trei_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "trei_time_in_technical")
	@Getter
	@Setter
	private Integer contadorBandejaTecnico;

	@Column(name = "trei_process_instance_id")
	@Getter
	@Setter
	private Long processInstanceId;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_trei_id_doty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@ManyToOne
	@JoinColumn(name = "eire_id")
	@ForeignKey(name = "fk_trei_id_eire_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eire_status = 'TRUE'")
	@Getter
	@Setter
	private SolicitudInspeccionControlAmbiental solicitudInspeccionControlAmbiental;

	@Getter
	@Setter
	@Column(name = "trei_report_number")
	private String numero;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "trei_report_date")
	private Date fecha;

	@Getter
	@Setter
	@Column(name = "trei_report_city")
	private String ciudad;

	@ManyToOne
	@JoinColumn(name = "user_evaluator_id")
	@ForeignKey(name = "fk_trei_id_user_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "user_status = 'TRUE'")
	@Getter
	@Setter
	private Usuario evaluador;

	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_trei_id_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento documento;

	@Getter
	@Setter
	@Lob
	@Column(name = "trei_report_law")
	private String normaVigente;

	@Getter
	@Setter
	@Lob
	@Column(name = "trei_report_background")
	private String antecedentesAdicional;

	@Getter
	@Setter
	@Lob
	@Column(name = "trei_report_goals")
	private String objetivosAdicional;

	@Getter
	@Setter
	@Lob
	@Column(name = "trei_report_document_comments")
	private String observacionesDocumentoAdjuntado;

	@Getter
	@Setter
	@Lob
	@Column(name = "trei_report_comments")
	private String observacionesAdicional;

	@Getter
	@Setter
	@Lob
	@Column(name = "trei_report_conclusions")
	private String conclusiones;

	@Getter
	@Setter
	@Column(name = "trei_report_sucess")
	private Boolean cumple;

	@Getter
	@Setter
	@Lob
	@Column(name = "trei_report_custom_conclusions")
	private String conclusionesAdicional;

	@Getter
	@Setter
	@Lob
	@Column(name = "trei_report_recommendations")
	private String recomendaciones;

	@Getter
	@Setter
	@Transient
	private String fechaMostrar;

	@Getter
	@Setter
	@Transient
	private String nombreFichero;

	@Getter
	@Setter
	@Transient
	private String nombreReporte;

	@Getter
	@Setter
	@Transient
	private String informePath;

	@Getter
	@Setter
	@Transient
	private String sujetoControl;

	@Getter
	@Setter
	@Transient
	private String proponente;

	@Getter
	@Setter
	@Transient
	private String evaluadorMostrar;

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
	private String tipoDocumentoAdjuntado;

	@Getter
	@Setter
	@Transient
	private String cumpleMostrar;

	@Getter
	@Setter
	@Transient
	private String observaciones;

	@Getter
	@Setter
	@Transient
	private byte[] archivoInforme;

	@Getter
	@Setter
	@Transient
	private String sujetoControlEncabez;

	@Getter
	@Setter
	@Transient
	private String recomendacionesEncabezado;
	
	@Getter
	@Setter
	@Transient
	private String proyectoCodigo;
	
	@Getter
	@Setter
	@Transient
	private String proyectoNombre;
	
	@Getter
	@Setter
	@Transient
	private String proyectoFecha;

}
