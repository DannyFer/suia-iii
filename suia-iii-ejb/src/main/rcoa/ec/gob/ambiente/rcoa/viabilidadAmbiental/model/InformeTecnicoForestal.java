package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

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
@Table(name="technical_report_forest", schema = "coa_viability")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "terf_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "terf_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "terf_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "terf_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "terf_user_update")) })

@NamedQueries({ 
@NamedQuery(name="InformeTecnicoForestal.findAll", query="SELECT i FROM InformeTecnicoForestal i"),
@NamedQuery(name=InformeTecnicoForestal.GET_POR_VIABILIDAD, query="SELECT i FROM InformeTecnicoForestal i where i.idViabilidad = :idViabilidad and i.estado = true order by id desc"), 
@NamedQuery(name=InformeTecnicoForestal.GET_POR_VIABILIDAD_TIPO_INFORME, query="SELECT i FROM InformeTecnicoForestal i  where i.idViabilidad = :idViabilidad and i.estado = true and tipoInforme = :tipoInforme order by id desc"),
@NamedQuery(name=InformeTecnicoForestal.GET_POR_VIABILIDAD_TIPO_INFORME_REVISION, query="SELECT i FROM InformeTecnicoForestal i  where i.idViabilidad = :idViabilidad and i.estado = true and tipoInforme = :tipoInforme and numeroRevision = :numeroRevision order by id desc") })

public class InformeTecnicoForestal extends EntidadAuditable implements
		Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_POR_VIABILIDAD = PAQUETE + "InformeTecnicoForestal.getPorViabilidad";
	public static final String GET_POR_VIABILIDAD_TIPO_INFORME = PAQUETE + "InformeTecnicoForestal.getPorViabilidadTipoInforme";
	public static final String GET_POR_VIABILIDAD_TIPO_INFORME_REVISION = PAQUETE + "InformeTecnicoForestal.getPorViabilidadTipoInformeRevision";


	// tipo de informe
	public static Integer técnicoAnterior = 1;
	public static Integer apoyo = 2;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "terf_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prvi_id")
	private Integer idViabilidad;

	@Getter
	@Setter
	@Column(name="terf_approval_observation")
	private Boolean esPronunciamientoFavorable;

	@Getter
	@Setter
	@Column(name="terf_background")
	private String antecedentes;

	@Getter
	@Setter
	@Column(name="terf_code")
	private String numeroInforme;

	@Getter
	@Setter
	@Column(name="terf_conclusions")
	private String conclusiones;
	
	@Getter
	@Setter
	@Column(name="terf_legal_framework_application")
	private String marcoLegal;

	@Getter
	@Setter
	@Column(name="terf_objective")
	private String objetivo;

	@Getter
	@Setter
	@Column(name="terf_recommendations")
	private String recomendaciones;

	@Getter
	@Setter
	@Column(name="terf_review_results")
	private String resultadosRevision;
	
	@Getter
	@Setter
	@Column(name="terf_technical_name")
	private String nombreTecnico;
	
	@Getter
	@Setter
	@Column(name="terf_technical_position")
	private String cargoTecnico;
	
	@Getter
	@Setter
	@Column(name="terf_technical_area")
	private String areaTecnico;
	
	@Getter
	@Setter
	@Column(name="terf_date_elaboration")
	private Date fechaElaboracion;

	@Getter
	@Setter
	@Column(name = "terf_type_report")
	private Integer tipoInforme = 1;

	@Getter
	@Setter
	@Column(name="task_id")
	private Integer idTarea;
	
	@Getter
	@Setter
	@Column(name="terf_review_number")
	private Integer numeroRevision;
	
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
	private byte[] archivoInforme;
	
	@Getter
	@Setter
	@Transient
	private String informePath;
}