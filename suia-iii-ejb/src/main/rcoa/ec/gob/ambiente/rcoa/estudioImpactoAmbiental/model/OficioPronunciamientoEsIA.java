package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the technical_report_forest database table.
 * 
 */
@Entity
@Table(name="memo_office", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "meof_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "meof_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "meof_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "meof_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "meof_update_user")) })

@NamedQueries({ 
@NamedQuery(name="OficioPronunciamientoEsIA.findAll", query="SELECT i FROM OficioPronunciamientoEsIA i"),
@NamedQuery(name=OficioPronunciamientoEsIA.GET_POR_ESTUDIO, query="SELECT i FROM OficioPronunciamientoEsIA i where i.informacionProyecto.id = :idEstudio and i.estado = true and i.estudioPronunciamiento=1 order by id desc"),
@NamedQuery(name=OficioPronunciamientoEsIA.GET_POR_ESTUDIO_INFORME, query="SELECT i FROM OficioPronunciamientoEsIA i where i.informacionProyecto.id = :idEstudio and i.idInforme = :idInforme and i.estado = true and i.estudioPronunciamiento=1 order by id desc") ,
@NamedQuery(name=OficioPronunciamientoEsIA.GET_POR_ESTUDIO_TIPO_OFICIO, query="SELECT i FROM OficioPronunciamientoEsIA i where i.informacionProyecto.id = :idEstudio and i.tipoOficio = :tipoOficio and i.estado = true and i.estudioPronunciamiento=1 order by id desc"),
@NamedQuery(name=OficioPronunciamientoEsIA.GET_POR_ESTUDIO_TIPO_OFICIO_ASC, query="SELECT i FROM OficioPronunciamientoEsIA i where i.informacionProyecto.id = :idEstudio and i.tipoOficio = :tipoOficio and i.estado = true and i.estudioPronunciamiento=1 order by id asc")
})
public class OficioPronunciamientoEsIA extends EntidadAuditable implements
		Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.";
	
	public static final String GET_POR_ESTUDIO = PAQUETE + "OficioPronunciamientoEsIA.getPorEstudio";
	public static final String GET_POR_ESTUDIO_INFORME = PAQUETE + "OficioPronunciamientoEsIA.getPorEstudioInforme";
	public static final String GET_POR_ESTUDIO_TIPO_OFICIO = PAQUETE + "OficioPronunciamientoEsIA.getPorEstudioTipooficio";
	public static final String GET_POR_ESTUDIO_TIPO_OFICIO_ASC = PAQUETE + "OficioPronunciamientoEsIA.getPorEstudioTipooficioAsc";
	
	// tipo de oficio
	public static Integer memorando = 1;//EIA
	public static Integer oficio = 2;//EIA
	public static Integer oficioArchivoAutomatico = 3;//PF
	public static Integer oficioAprobado = 4;//PF
	public static Integer oficioObservado = 5;//PF

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "meof_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prin_id")
	@ForeignKey(name = "prin_id")
	private InformacionProyectoEia informacionProyecto;
	
//	@Getter
//	@Setter
//	@ManyToOne
//	@JoinColumn(name = "tere_id")
//	@ForeignKey(name = "tere_id")
//	private InformeTecnicoEsIA informeTecnico;
	@Getter
	@Setter
	@Column(name="tere_id")
	private Integer idInforme;

	@Getter
	@Setter
	@Column(name="meof_code_document")
	private String codigoOficio;

	@Getter
	@Setter
	@Column(name="meof_date_elaboration")
	private Date fechaOficio;

	@Getter
	@Setter
	@Column(name="meof_affair")
	private String asunto;

	@Getter
	@Setter
	@Column(name="meof_background")
	private String antecedentes;
	
	@Getter
	@Setter
	@Column(name="meof_pronouncement")
	private String pronunciamiento;

	@Getter
	@Setter
	@Column(name="meof_forest_inventory")
	private String inventarioForestal;
	
	@Getter
	@Setter
	@Column(name="meof_type")
	private Integer tipoOficio;//-- 1.- MEMORANDO EIA, 2.- OFICIO EIA, 3.- OFICIO ARCHIVADO PF, 4.- OFICIO APROBADO PF, 5.- OFICIO OBSERVADO
	
	@Getter
	@Setter
	@Column(name="meof_conclutions")
	private String conclusiones;
	
	@Getter
	@Setter
	@Column(name="meof_study_pronouncement")
	private Integer estudioPronunciamiento;//1.- Estudio de impacto ambiental, 2.- Pronunciamiento favorable

	@Getter
	@Setter
	@Column(name="meof_revision_number")
	private Integer numeroRevision;
	
	
	@Getter
	@Setter
	@Column(name="meof_task_id")
	private Integer idTarea;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id")
	@ForeignKey(name = "area_id")
	private Area areaResponsable;

	@Getter
	@Setter
	@Transient
	private String nombreReporte;
	
	@Getter
	@Setter
	@Transient
	private byte[] archivo;
	
	@Getter
	@Setter
	@Transient
	private String path;	
	
	public OficioPronunciamientoEsIA() {
		this.estudioPronunciamiento=1;
	}
	
	public OficioPronunciamientoEsIA(InformacionProyectoEia informacionProyectoEia,Integer estudioPronunciamiento,Integer numeroRevision) {
		this.informacionProyecto = informacionProyectoEia;
		this.estudioPronunciamiento = estudioPronunciamiento;
		this.numeroRevision = numeroRevision;
	}
}