package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

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

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the pronouncement_job_forest database table.
 * 
 */
@Entity
@Table(name="pronouncement_job_forest", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "prjf_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prjf_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prjf_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prjf_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prjf_user_update")) })

@NamedQueries({
@NamedQuery(name="PronunciamientoForestal.findAll", query="SELECT p FROM PronunciamientoForestal p"),
@NamedQuery(name=PronunciamientoForestal.GET_POR_VIABILIDAD, query="SELECT p FROM PronunciamientoForestal p  where p.idViabilidad = :idViabilidad and p.estado = true order by id desc"),
@NamedQuery(name=PronunciamientoForestal.GET_POR_VIABILIDAD_TIPO_OFICIO, query="SELECT p FROM PronunciamientoForestal p  where p.idViabilidad = :idViabilidad and p.estado = true and tipoOficio = :tipoOficio order by id desc"),
@NamedQuery(name=PronunciamientoForestal.GET_POR_VIABILIDAD_TIPO_OFICIO_REVISION, query="SELECT p FROM PronunciamientoForestal p  where p.idViabilidad = :idViabilidad and p.estado = true and tipoOficio = :tipoOficio and numeroRevision = :numeroRevision order by id desc")  
})
public class PronunciamientoForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_POR_VIABILIDAD = PAQUETE + "PronunciamientoForestal.getPorViabilidad";
	public static final String GET_POR_VIABILIDAD_TIPO_OFICIO = PAQUETE + "PronunciamientoForestal.getPorViabilidadTipoOficio";
	public static final String GET_POR_VIABILIDAD_TIPO_OFICIO_REVISION = PAQUETE + "PronunciamientoForestal.getPorViabilidadTipoOficioRevision";


	// tipo de oficio
	public static Integer oficioPronunciamientoAnterior = 1;
	public static Integer observado = 2;
	public static Integer memorandoViabilidad = 3;
	public static Integer memorandoApoyo = 4;
	public static Integer oficioViabilidad = 5;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prjf_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prjf_affair")
	private String asunto;

	@Getter
	@Setter
	@Column(name="prjf_background")
	private String antecedentes;

	@Getter
	@Setter
	@Column(name="prjf_legal_framework")
	private String marcoLegal;

	@Getter
	@Setter
	@Column(name="prjf_number_job")
	private String numeroOficio;

	@Getter
	@Setter
	@Column(name="prjf_pronouncement")
	private String pronunciamiento;

	@Getter
	@Setter
	@Column(name="prjf_job_date")
	private Date fechaOficio;

	@Getter
	@Setter
	@Column(name="prvi_id")
	private Integer idViabilidad;
	
	@Getter
	@Setter
	@Column(name="prjf_conclusions")
	private String conclusiones;
	
	@Getter
	@Setter
	@Column(name="prjf_recommendations")
	private String recomendaciones;

	@Getter
	@Setter
	@Column(name = "prjf_type_pronouncement")
	private Integer tipoOficio = 1;

	@Getter
	@Setter
	@Column(name="prjf_review_number")
	private Integer numeroRevision;
	
	@Getter
	@Setter
	@Column(name="prjf_signed_document")
	private Boolean documentoFirmado;
	
	@Getter
	@Setter
	@Column(name="prjf_favorable_pronouncement")
	private Boolean esPronunciamientoFavorable;

	@Getter
	@Setter
	@Transient
	private String nombreFichero;

	@Getter
	@Setter
	@Transient
	private String nombreOficio;
	
	@Getter
	@Setter
	@Transient
	private byte[] archivoOficio;
	
	@Getter
	@Setter
	@Transient
	private String oficioPath;

}