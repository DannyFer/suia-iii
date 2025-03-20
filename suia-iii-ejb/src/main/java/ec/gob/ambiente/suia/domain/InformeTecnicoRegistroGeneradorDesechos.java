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

@NamedQueries({ @NamedQuery(name = InformeTecnicoRegistroGeneradorDesechos.GET_BY_REGISTRO_GENERADOR, query = "SELECT i FROM InformeTecnicoRegistroGeneradorDesechos i WHERE i.generadorDesechosPeligrosos.id = :idGeneradorDesechosPeligrosos and i.tipoDocumento.id = :idTipoDocumento and i.processInstanceId = :idInstanciaProceso and i.contadorBandejaTecnico = :contadorBandejaTecnico AND i.estado=true") })
@Entity
@Table(name = "technical_report_waste_generators", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "trrg_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "trrg_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "trrg_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "trrg_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "trrg_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "trrg_status = 'TRUE'")
public class InformeTecnicoRegistroGeneradorDesechos extends EntidadAuditable {

	private static final long serialVersionUID = 8849225714312178522L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String GET_BY_REGISTRO_GENERADOR = PAQUETE
			+ "InformeTecnicoRegistroGeneradorDesechos.get_by_registro_generador";

	@Id
	@SequenceGenerator(name = "TECHNICAL_REPORT_RGD_GENERATOR", sequenceName = "seq_trrg_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECHNICAL_REPORT_RGD_GENERATOR")
	@Column(name = "trrg_id")
	@Getter
	@Setter
	private Integer id;
	
	@Column(name = "trrg_time_in_technical")
	@Getter
	@Setter
	private Integer contadorBandejaTecnico;

	@Column(name = "trrg_process_instance_id")
	@Getter
	@Setter
	private Long processInstanceId;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_trrg_id_doty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@ManyToOne
	@JoinColumn(name = "hwge_id")
	@ForeignKey(name = "fk_trrg_id_hwge_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwge_status = 'TRUE'")
	@Getter
	@Setter
	private GeneradorDesechosPeligrosos generadorDesechosPeligrosos;

	@Getter
	@Setter
	@Column(name = "trrg_report_number")
	private String numero;
	
	@Getter
	@Setter
	@Column(name = "trrg_report_number_old")
	private String numeroAnterior;

	@Getter
	@Setter
	@Column(name = "trrg_report_action")
	private String accion;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "trrg_report_date")
	private Date fecha;

	@Getter
	@Setter
	@Column(name = "trrg_report_city")
	private String ciudad;

	@ManyToOne
	@JoinColumn(name = "user_evaluator_id")
	@ForeignKey(name = "fk_trrg_id_user_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "user_status = 'TRUE'")
	@Getter
	@Setter
	private Usuario evaluador;

	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_trrg_id_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento documento;

	@Getter
	@Lob
	@Column(name = "trrg_report_law")
	private String normaVigente;

	public void setNormaVigente(String normaVigente){
		if (this.normaVigente == null || !this.normaVigente.equals(normaVigente)){
			this.modificado = true;
		}
		this.normaVigente = normaVigente;
	}

	@Getter
	@Lob
	@Column(name = "trrg_report_background")
	private String antecedentesAdicional;

	public void setAntecedentesAdicional(String antecedentesAdicional){
		if (this.antecedentesAdicional==null  ||  !this.antecedentesAdicional.equals(antecedentesAdicional)){
			this.modificado = true;
		}
		this.antecedentesAdicional = antecedentesAdicional;
	}

	@Getter
	@Lob
	@Column(name = "trrg_report_goals")
	private String objetivosAdicional;

	public void setObjetivosAdicional(String objetivosAdicional){
		if (this.objetivosAdicional==null || !this.objetivosAdicional.equals(objetivosAdicional)){
			this.modificado = true;
		}
		this.objetivosAdicional = objetivosAdicional;
	}

	@Getter
	@Setter
	@Lob
	@Column(name = "trrg_report_document_comments")
	private String observacionesDocumentoAdjuntado;

	@Getter
	@Lob
	@Column(name = "trrg_report_comments")
	private String observacionesAdicional;

	public void setObservacionesAdicional(String observacionesAdicional){
		if (this.observacionesAdicional == null || !this.observacionesAdicional.equals(observacionesAdicional)){
			this.modificado = true;
		}
		this.observacionesAdicional = observacionesAdicional;
	}


	@Getter
	@Lob
	@Column(name = "trrg_report_conclusions")
	private String conclusiones;

	public void setConclusiones(String conclusiones){
		if (this.conclusiones != null && !this.conclusiones.equals(conclusiones)){
					this.modificado = true;
		}
		this.conclusiones = conclusiones;
	}


	@Column(name = "trrg_report_sucess")
	private Boolean cumple;

	public void setCumple(Boolean cumple){
		if (this.cumple == null || this.cumple.booleanValue()!=cumple.booleanValue()){
			this.modificado = true;
		}
		this.cumple = cumple;
	}

	public Boolean getCumple() {
		if (cumple != null)
			return cumple;
		else
			return false;
	}

	@Getter
	@Setter
	@Lob
	@Column(name = "trrg_report_custom_conclusions")
	private String conclusionesAdicional;

	@Getter
	@Lob
	@Column(name = "trrg_report_recommendations")
	private String recomendaciones;

	public void setRecomendaciones(String recomendaciones){
		if (this.recomendaciones == null || !this.recomendaciones.equals(recomendaciones)){
			this.modificado = true;
		}
		this.recomendaciones = recomendaciones;
	}


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
	private String proyectoCodigo;

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
	private boolean modificado;//Para saber si se editó algún campo y generar una nueva versión del documento.

}
