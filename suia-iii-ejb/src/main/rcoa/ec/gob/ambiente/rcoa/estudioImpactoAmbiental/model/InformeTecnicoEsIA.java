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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the technical_report_forest database table.
 * 
 */
@Entity
@Table(name="technical_report", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "tere_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tere_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tere_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tere_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tere_update_user")) })

@NamedQueries({ 
@NamedQuery(name="InformeTecnicoEsIA.findAll", query="SELECT i FROM InformeTecnicoEsIA i"),
@NamedQuery(name=InformeTecnicoEsIA.GET_POR_ESTUDIO, query="SELECT i FROM InformeTecnicoEsIA i where i.idEstudio = :idEstudio and i.estado = true and i.tipoInforme <> 7 order by id desc"),
@NamedQuery(name=InformeTecnicoEsIA.GET_POR_ESTUDIO_TAREA, query="SELECT i FROM InformeTecnicoEsIA i where i.idEstudio = :idEstudio and i.idTarea = :idTarea and i.estado = true order by id desc"),
@NamedQuery(name=InformeTecnicoEsIA.GET_POR_ESTUDIO_INFORME, query="SELECT i FROM InformeTecnicoEsIA i where i.idEstudio = :idEstudio and i.tipoInforme = :tipoInforme and i.estado = true order by id desc"),
@NamedQuery(name=InformeTecnicoEsIA.GET_POR_ID, query="SELECT i FROM InformeTecnicoEsIA i where i.id = :idInforme and i.estado = true order by id desc"),
@NamedQuery(name=InformeTecnicoEsIA.GET_POR_ESTUDIO_INFORME_REVISION, query="SELECT i FROM InformeTecnicoEsIA i where i.idEstudio = :idEstudio and i.tipoInforme = :tipoInforme and i.numeroRevision = :numeroRevision and i.estado = true order by id desc")
})
public class InformeTecnicoEsIA extends EntidadAuditable implements
		Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.";
	
	public static final String GET_POR_ESTUDIO = PAQUETE + "InformeTecnicoEsIA.getPorEstudio";
	public static final String GET_POR_ESTUDIO_TAREA = PAQUETE + "InformeTecnicoEsIA.getPorEstudioTarea";
	public static final String GET_POR_ESTUDIO_INFORME = PAQUETE + "InformeTecnicoEsIA.getPorEstudioInforme";
	public static final String GET_POR_ID = PAQUETE + "InformeTecnicoEsIA.getPorId";
	public static final String GET_POR_ESTUDIO_INFORME_REVISION = PAQUETE + "InformeTecnicoEsIA.getPorEstudioInformeRevision";
	
	public static Integer forestal = 1;
	public static Integer snap = 2;
	public static Integer social = 3;
	public static Integer biotico = 4;
	public static Integer cartografo = 5;
	public static Integer apoyo = 6;
	public static Integer consolidado = 7;
	public static Integer forestalInventario = 8;
	
	// tipo de pronunciamiento
	public static Integer aprobado = 1;
	public static Integer observado = 2;
	public static Integer terceraRevision = 3;
	public static Integer observadoSustancial = 4;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tere_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prin_id")
	private Integer idEstudio;
	
	
	@Getter
	@Setter
	@Column(name="tere_task_id")
	private Integer idTarea;

	@Getter
	@Setter
	@Column(name="tere_id_previous_report")
	private Integer idInformePrincipal;

	@Getter
	@Setter
	@Column(name="tere_pronouncement_type")
	private Integer tipoPronunciamiento; //1.- Pronunciamiento aprobación; 2.- Pronunciamiento observación
	
	@Getter
	@Setter
	@Column(name="tere_report_type")
	private Integer tipoInforme; //1.- Forestal  2.- Snap 3.- Social 4.- Biótico 5 .- Cartógrafo 6.- De apoyo 7.- Consolidado

	@Getter
	@Setter
	@Column(name="tere_code_document")
	private String codigoInforme;

	@Getter
	@Setter
	@Column(name="tere_date_elaboration")
	private Date fechaInforme;

	@Getter
	@Setter
	@Column(name="tere_background")
	private String antecedentes;

	@Getter
	@Setter
	@Column(name="tere_objectives")
	private String objetivos;
	
	@Getter
	@Setter
	@Column(name="tere_important_features")
	private String caracteristicas;

	@Getter
	@Setter
	@Column(name="tere_technical_evaluation")
	private String evaluacionTecnica;

	@Getter
	@Setter
	@Column(name="tere_observations")
	private String observaciones;

	@Getter
	@Setter
	@Column(name="tere_conclusions_recommendations")
	private String conclusionesRecomendaciones;
	
	@Getter
	@Setter
	@Column(name="tere_corrections")
	private Boolean requiereCorrecciones;	
	
	@Getter
	@Setter
	@Column(name="tere_review_number")
	private Integer numeroRevision;

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
}