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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({ @NamedQuery(name = OficioObservacionRegistroGeneradorDesechos.GET_BY_REGISTRO_GENERADOR, query = "SELECT o FROM OficioObservacionRegistroGeneradorDesechos o WHERE o.generadorDesechosPeligrosos.id = :idGeneradorDesechosPeligrosos and o.tipoDocumento.id = :idTipoDocumento and o.processInstanceId = :idInstanciaProceso and o.contadorBandejaTecnico = :contadorBandejaTecnico") })
@Entity
@Table(name = "office_observation_waste_generators", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "oowg_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "oowg_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "oowg_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "oowg_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "oowg_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "oowg_status = 'TRUE'")
public class OficioObservacionRegistroGeneradorDesechos extends EntidadAuditable {

	private static final long serialVersionUID = -3683287241934149025L;

	public static final String GET_BY_REGISTRO_GENERADOR = "ec.gob.ambiente.suia.domain.OficioObservacionRegistroGeneradorDesechos.get_by_registro_generador";

	@Id
	@SequenceGenerator(name = "OFFICE_OBSERVATION_RGD_GENERATOR", sequenceName = "seq_oowg_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OFFICE_OBSERVATION_RGD_GENERATOR")
	@Column(name = "oowg_id")
	@Getter
	@Setter
	private Integer id;
	
	@Column(name = "oowg_time_in_technical")
	@Getter
	@Setter
	private Integer contadorBandejaTecnico;

	@Column(name = "oowg_process_instance_id")
	@Getter
	@Setter
	private Long processInstanceId;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_oowg_id_doty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "doty_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@ManyToOne
	@JoinColumn(name = "hwge_id")
	@ForeignKey(name = "fk_oowg_id_hwge_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwge_status = 'TRUE'")
	@Getter
	@Setter
	private GeneradorDesechosPeligrosos generadorDesechosPeligrosos;

	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_oowg_id_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento documento;

	@Getter
	@Setter
	@Column(name = "oowg_office_number")
	private String numero;
	
	@Getter
	@Setter
	@Column(name = "oowg_office_number_old")
	private String numeroAnterior;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "oowg_report_date")
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
	@ForeignKey(name = "fk_oowg_id_user_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "user_status = 'TRUE'")
	@Getter
	@Setter
	private Usuario evaluador;


	@Setter
	@Column(name = "oowg_required_corrections")
	private Boolean requiereCorrecciones;

	public Boolean getRequiereCorrecciones() {
		return requiereCorrecciones == null ? false : requiereCorrecciones;
	}

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
	@Lob
	@Column(name = "oowg_office_compliment")
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
	@Lob
	@Column(name = "oewg_office_set")
	private String establecido;

	@Getter
	@Setter
	@Lob
	@Column(name = "oowg_office_recommendations")
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
	private String observaciones;

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
	private String ruc;
	
	@Getter
	@Setter
	@Transient
	private String desecho;
	
	@Getter
	@Setter
	@Transient
	private String acuerdo;
	
	@Getter
	@Setter
	@Transient
	private String operador;
	
	@Getter
	@Setter
	@Transient
	private String representante;
	
	@Getter
	@Setter
	@Transient
	private String emite;
	
	
}
