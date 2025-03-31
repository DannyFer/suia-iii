package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the pronouncement_job_biodiversity database table.
 * 
 */
@Entity
@Table(name="pronouncement_job_biodiversity", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "prjb_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prjb_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prjb_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prjb_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prjb_user_update")) })

@NamedQueries({
@NamedQuery(name="PronunciamientoBiodiversidad.findAll", query="SELECT p FROM PronunciamientoBiodiversidad p"),
@NamedQuery(name=PronunciamientoBiodiversidad.GET_POR_VIABILIDAD, query="SELECT p FROM PronunciamientoBiodiversidad p  where p.idViabilidad = :idViabilidad and p.estado = true order by id desc") })
public class PronunciamientoBiodiversidad extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_POR_VIABILIDAD = PAQUETE + "PronunciamientoBiodiversidad.getPorViabilidad";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prjb_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="prjb_number_job")
	private String numeroOficio;

	@Getter
	@Setter
	@Column(name="prvi_id")
	private Integer idViabilidad;
	
	@Getter
	@Setter
	@Column(name="prjb_recommendations")
	private String recomendaciones;
	
	@Getter
	@Setter
	@Column(name="prjb_job_date")
	private Date fechaOficio;
	
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