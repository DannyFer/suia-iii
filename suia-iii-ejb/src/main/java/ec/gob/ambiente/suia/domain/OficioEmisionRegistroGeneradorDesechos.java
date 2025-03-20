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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({ @NamedQuery(name = OficioEmisionRegistroGeneradorDesechos.GET_BY_REGISTRO_GENERADOR, query = "SELECT o FROM OficioEmisionRegistroGeneradorDesechos o WHERE o.generadorDesechosPeligrosos.id = :idGeneradorDesechosPeligrosos and o.tipoDocumento.id = :idTipoDocumento and o.processInstanceId = :idInstanciaProceso and o.contadorBandejaTecnico = :contadorBandejaTecnico") })
@Entity
@Table(name = "office_emision_waste_generators", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "oewg_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "oewg_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "oewg_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "oewg_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "oewg_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "oewg_status = 'TRUE'")
public class OficioEmisionRegistroGeneradorDesechos extends EntidadAuditable {

	private static final long serialVersionUID = 7598420296231751887L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String GET_BY_REGISTRO_GENERADOR = PAQUETE
			+ "OficioEmisionRegistroGeneradorDesechos.get_by_registro_generador";

	@Id
	@SequenceGenerator(name = "OFFICE_EMISION_RGD_GENERATOR", sequenceName = "seq_oewg_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OFFICE_EMISION_RGD_GENERATOR")
	@Column(name = "oewg_id")
	@Getter
	@Setter
	private Integer id;
	
	@Column(name = "oewg_time_in_technical")
	@Getter
	@Setter
	private Integer contadorBandejaTecnico;

	@Column(name = "oewg_process_instance_id")
	@Getter
	@Setter
	private Long processInstanceId;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_oewg_id_doty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "doty_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@ManyToOne
	@JoinColumn(name = "hwge_id")
	@ForeignKey(name = "fk_oewg_id_hwge_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwge_status = 'TRUE'")
	@Getter
	@Setter
	private GeneradorDesechosPeligrosos generadorDesechosPeligrosos;

	@Getter
	@Setter
	@Column(name = "oewg_office_number")
	private String numero;
	
	@Getter
	@Setter
	@Column(name = "oewg_office_number_old")
	private String numeroAnterior;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "oewg_report_date")
	private Date fecha;

	@ManyToOne
	@JoinColumn(name = "user_evaluator_id")
	@ForeignKey(name = "fk_oewg_id_user_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "user_status = 'TRUE'")
	@Getter
	@Setter
	private Usuario evaluador;

	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_oewg_id_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento documento;

	@Getter
	@Lob
	@Column(name = "oewg_office_compliment")
	private String cumplimiento;

	public void setCumplimiento(String cumplimiento){
		if (this.cumplimiento== null || !this.cumplimiento.equals(cumplimiento)){
				this.modificado = true;
		}
		this.cumplimiento = cumplimiento;
	}

	@Getter
	@Lob
	@Column(name = "oewg_office_set")
	private String establecido;

	public void setEstablecido(String establecido){
		if (this.establecido== null || !this.establecido.equals(establecido)){
			this.modificado = true;
		}
		this.establecido = establecido;
	}

	@Getter
	@Lob
	@Column(name = "oewg_office_recommendations")
	private String recomendaciones;

	public void setRecomendaciones(String recomendaciones){
		if (this.recomendaciones== null || !this.recomendaciones.equals(recomendaciones)){
			this.modificado = true;
		}
		this.recomendaciones = recomendaciones;
	}

	@Setter
	@Column(name = "oewg_required_corrections")
	private Boolean requiereCorrecciones;

	public Boolean getRequiereCorrecciones() {
		return requiereCorrecciones == null ? false : requiereCorrecciones;
	}

	@Getter
	@Setter
	@Transient
	private String fechaMostrar;

	@Getter
	@Setter
	@Transient
	private String nombreReporte;

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
	private String sujetoControl;

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
	private String paraProyecto;

	@Getter
	@Setter
	@Transient
	private String paraActividad;

	@Getter
	@Setter
	@Transient
	private String empresaCargo;

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
	@Transient
	private String sector;

	@Getter
	@Setter
	@Transient
	private String autoridad;

	@Getter
	@Setter
	@Transient
	private String numeroRegistroGenerador;

	@Getter
	@Setter
	@Transient
	private String accionOficioAsunto;

	@Getter
	@Setter
	@Transient
	private String accionOficio;

	@Getter
	@Setter
	@Transient
	private String accionOficioResultado;

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
	private boolean modificado;//Para saber si se editó algún campo y generar una nueva versión del documento.
	
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
