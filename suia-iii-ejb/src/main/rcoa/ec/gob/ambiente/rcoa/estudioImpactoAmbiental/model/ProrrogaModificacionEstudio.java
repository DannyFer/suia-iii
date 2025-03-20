package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the study_modification_extension database table.
 * 
 */
@Entity
@Table(name="study_modification_extension", schema = "coa_environmental_impact_study")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "stme_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "stme_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "stme_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "stme_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "stme_user_update")) })

@NamedQueries({ 
@NamedQuery(name="ProrrogaModificacionEstudio.findAll", query="SELECT p FROM ProrrogaModificacionEstudio p"),
@NamedQuery(name=ProrrogaModificacionEstudio.GET_POR_ESTUDIO, query="SELECT p FROM ProrrogaModificacionEstudio p where p.informacionProyectoEia.id = :idEstudio and p.estado = true order by id desc"),
@NamedQuery(name=ProrrogaModificacionEstudio.GET_POR_ESTUDIO_REVISION, query="SELECT p FROM ProrrogaModificacionEstudio p where p.informacionProyectoEia.id = :idEstudio and p.numeroRevision = :nroRevision and p.estado = true order by id desc")
})
public class ProrrogaModificacionEstudio extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
private static final String PAQUETE = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.";
	
	public static final String GET_POR_ESTUDIO = PAQUETE + "ProrrogaModificacionEstudio.getPorEstudio";
	public static final String GET_POR_ESTUDIO_REVISION = PAQUETE + "ProrrogaModificacionEstudio.getPorEstudioRevision";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="stme_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prin_id")
	@ForeignKey(name = "prin_id")
	private InformacionProyectoEia informacionProyectoEia;

	@Getter
	@Setter
	@Column(name="stme_date_end_modification")
	private Date fechaFinModificacion;

	@Getter
	@Setter
	@Column(name="stme_date_end_modification_extension")
	private Date fechaFinModificacionProrroga;

	@Getter
	@Setter
	@Column(name="stme_date_request_extension")
	private Date fechaSolicitudProrroga;

	@Getter
	@Setter
	@Column(name="stme_date_start_modification")
	private Date fechaInicioModificacion;

	@Getter
	@Setter
	@Column(name="stme_number_days_extension")
	private Integer numeroDiasProrroga;

	@Getter
	@Setter
	@Column(name="stme_number_days_review")
	private Integer numeroDiasPorRevision;

	@Getter
	@Setter
	@Column(name="stme_number_review")
	private Integer numeroRevision;
	
	public ProrrogaModificacionEstudio() {
	}

}