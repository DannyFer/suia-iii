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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the environmental_social_participation_mechanisms
 * database table.
 *@NamedQuery(name = OficioObservacionEia.OBTENER_OFICIO_APROBACION_POR_PROYECTO, 
query = "select i from MecanismoParticipacionSocialAmbiental i "
	+ "where i.participacionSocialAmbiental.proyectoLicenciamientoAmbiental = :p_proyecto")
 */
@Entity
@Table(name = "environmental_social_participation_mechanisms", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "espm_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "espm_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "espm_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "espm_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "espm_user_update"))
})
@NamedQueries({@NamedQuery(name = "MecanismoParticipacionSocialAmbiental.findAll", 
query = "SELECT u FROM MecanismoParticipacionSocialAmbiental u"),
@NamedQuery(name = MecanismoParticipacionSocialAmbiental.OBTENER_MECANISMO_ASAMBLEA_PUBLICA, 
query = "select i from MecanismoParticipacionSocialAmbiental i "
	+ "where i.participacionSocialAmbiental.proyectoLicenciamientoAmbiental = :p_proyecto")
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "espm_status = 'TRUE'")
public class MecanismoParticipacionSocialAmbiental extends EntidadAuditable {

	private static final long serialVersionUID = 3459624017554710669L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_MECANISMO_ASAMBLEA_PUBLICA = PAQUETE + "MecanismoParticipacionSocialAmbiental.ObtenerMecanismoAsambleaPublica";

	@Id
	@SequenceGenerator(name = "MECHANISMS_PPS_GENERATOR", sequenceName = "seq_espm_id",schema="suia_iii",initialValue=1,allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MECHANISMS_PPS_GENERATOR")
	@Column(name = "espm_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_espmensp_id_environmental_social_participationensp_id")
	@JoinColumn(name = "ensp_id", referencedColumnName = "ensp_id")
	private ParticipacionSocialAmbiental participacionSocialAmbiental;

	@Getter
	@Setter
	@Column(name = "espm_start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInicio;

	@Getter
	@Setter
	@Column(name = "espm_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaFin;

	@Getter
	@Setter
	@Column(name = "espm_hour")
	@Temporal(TemporalType.TIMESTAMP)
	private Date hora;

	@Getter
	@Setter
	@Column(name = "espm_place")
	private String lugar;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_environmental_social_participation_mechanismsgelo_id_geograp")
	@JoinColumn(name = "gelo_id", referencedColumnName = "gelo_id")
	private UbicacionesGeografica ubicacionesGeografica;

	@Getter
	@Setter
	@JoinColumn(name = "spmc_id", referencedColumnName = "spmc_id")
	@ForeignKey(name = "fk_catalogspmc_id_envsocparmecspmc_id")
	@ManyToOne
	private CatalogoMediosParticipacionSocial catalogoMedio;

	@Getter
	@Setter
	@Column(name = "espm_entity")
	private String comunidad;

	@Getter
	@Setter
	@Column(name = "espm_doc_name")
	private String nombreDoc;

}
