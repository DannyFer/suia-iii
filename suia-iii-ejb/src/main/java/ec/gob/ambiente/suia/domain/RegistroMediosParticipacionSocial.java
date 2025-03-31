package ec.gob.ambiente.suia.domain;


import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the environmental_social_participation_media_records
 * database table.
 *
 */
@NamedQueries({ @NamedQuery(name = RegistroMediosParticipacionSocial.GET_ALL, query = "SELECT m FROM RegistroMediosParticipacionSocial m"),
	@NamedQuery(name = RegistroMediosParticipacionSocial.FIND_BY_PROJECT, query = "SELECT m FROM RegistroMediosParticipacionSocial m where m.estado=true and m.participacionSocialAmbiental.id=:enspId and m.catalogoMedio.tipoCatalogo.id=:spmtId")})
@Entity
@Table(name = "environmental_social_participation_media_records", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "espm_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "espm_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "espm_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "espm_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "espm_user_update")) })
@Filter(name = EntidadAuditable.FILTER_ACTIVE, condition = "espm_status = 'TRUE'")
public class RegistroMediosParticipacionSocial extends EntidadAuditable {

	private static final long serialVersionUID = 7972923797782547149L;

	public static final String GET_ALL = "ec.com.magmasoft.business.domain.RegistroMediosParticipacionSocial.getAll";
	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.RegistroMediosParticipacionSocial.findByProject";
	
	@Id
	@SequenceGenerator(name = "RECORD_MEDIA_PPS_GENERATOR", sequenceName = "seq_espm_id",schema="suia_iii",initialValue=1,allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECORD_MEDIA_PPS_GENERATOR")
	@Column(name = "espm_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_espmrensp_id_environmental_social_participationensp_id")
	@JoinColumn(name = "ensp_id", referencedColumnName = "ensp_id")
	private ParticipacionSocialAmbiental participacionSocialAmbiental;

	@Getter
	@Setter
	@Column(name = "espm_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "espm_docu_name")
	private String nombreDocumentoResplado;

	@Getter
	@Setter
	@JoinColumn(name = "spmc_id", referencedColumnName = "spmc_id")
	@ForeignKey(name = "fk_espmrspmc_id_environmental_social_participation_media_catalo")
	@ManyToOne
	private CatalogoMediosParticipacionSocial catalogoMedio;

	@Getter
	@Setter
	@Column(name = "user_id")
	private Integer usuario;

	@Getter
	@Setter
	@Column(name = "espm_places")
	private String lugar;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "espm_start_date")
	private Date fechaInicio;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "espm_end_date")
	private Date fechaFin;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_espmrdocu_id_documentsdocu_id")
	@JoinColumn(name = "docu_id")
	private Documento documento;
	
	//Cris F: campo para evaluación del medio
	@Getter
	@Setter
	@Column(name = "espm_evaluation")
	private String evaluacion;
}
