package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the inspection_report_biodiversity database table.
 * 
 */
@Entity
@Table(name="inspection_report_biodiversity", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "inre_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "inre_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "inre_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "inre_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "inre_user_update")) })

@NamedQueries({
@NamedQuery(name="InformeInspeccionBiodiversidad.findAll", query="SELECT i FROM InformeInspeccionBiodiversidad i"),
@NamedQuery(name=InformeInspeccionBiodiversidad.GET_POR_VIABILIDAD, query="SELECT i FROM InformeInspeccionBiodiversidad i where i.idViabilidad = :idViabilidad and i.estado = true order by id desc") })
public class InformeInspeccionBiodiversidad extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_POR_VIABILIDAD = PAQUETE + "InformeInspeccionBiodiversidad.getPorViabilidad";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="inre_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="inre_conclusions")
	private String conclusiones;

	@Getter
	@Setter
	@Column(name="inre_favorable_pronouncement")
	private Boolean esPronunciamientoFavorable;

	@Getter
	@Setter
	@Column(name="inre_field_inspection_date")
	private Date fechaInspeccion;

	@Getter
	@Setter
	@Column(name="inre_inspection_number")
	private String numeroInforme;

	@Getter
	@Setter
	@Column(name="inre_outcome")
	private Integer resultadoInforme;

	@Getter
	@Setter
	@Column(name="inre_recommendations")
	private String recomendaciones;

	@Getter
	@Setter
	@Column(name="prvi_id")
	private Integer idViabilidad;
	
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
	
	@Getter
	@Setter
	@Column(name="task_id")
	private Integer idTarea;
	
}