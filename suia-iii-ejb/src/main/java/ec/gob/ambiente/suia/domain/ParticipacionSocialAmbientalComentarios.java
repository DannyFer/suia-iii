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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the environmental_social_participation_comments
 * database table.
 *
 */
@Entity
@Table(name = "environmental_social_participation_comments", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = ParticipacionSocialAmbientalComentarios.GET_ALL, query = "SELECT u FROM ParticipacionSocialAmbientalComentarios u"),
	@NamedQuery(name = ParticipacionSocialAmbientalComentarios.FIND_BY_PROJECT, query = "SELECT m FROM ParticipacionSocialAmbientalComentarios m WHERE m.estado=true and m.participacionSocialAmbiental.proyectoLicenciamientoAmbiental.id=:prenId")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "espc_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "espc_status = 'TRUE'")
public class ParticipacionSocialAmbientalComentarios extends EntidadBase {

	private static final long serialVersionUID = 5641115571321784474L;
	public static final String GET_ALL = "ec.com.magmasoft.business.domain.ParticipacionSocialAmbientalComentarios.getAll";
	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.ParticipacionSocialAmbientalComentarios.findByProject";

	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_SOCIAL_PARTICIPATION_COMMENTS_GENERATOR", sequenceName = "seq_espc_id",initialValue = 1,allocationSize = 1,schema ="suia_iii" )
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_SOCIAL_PARTICIPATION_COMMENTS_GENERATOR")
	@Column(name = "espc_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "espc_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "espc_entity")
	private String nombreEntidad;

	@Getter
	@Setter
	@Column(name = "espc_position")
	private String cargo;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_enviromental_social_participation_commentsgelo_id_geographic")
	@JoinColumn(name = "gelo_id")
	private UbicacionesGeografica ubicacionGeografica;

	@Getter
	@Setter
	@Column(name = "espc_phone")
	private String telefono;

	@Getter
	@Setter
	@Column(name = "espc_mail")
	private String correo;

	@Getter
	@Setter
	@Column(name = "espc_comment_date")
	private Date fecha;

	@Getter
	@Setter
	@Column(name = "espc_social_observaction_comment")
	private String comentario;

	@Getter
	@Setter
	@Column(name = "espc_promotor_observation_complemetary")
	private String observacionComplementaria;

	@Getter
	@Setter
	@Column(name = "espc_observation_action")
	private String accion;

	/*
	 * @Getter
	 * 
	 * @Setter
	 * 
	 * @Column(name = "espc_status") private String estado;
	 */

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_environmental_social_participation_ensp_id")
	@JoinColumn(name = "ensp_id")
	private ParticipacionSocialAmbiental participacionSocialAmbiental;

	public ParticipacionSocialAmbientalComentarios() {
	}

	public ParticipacionSocialAmbientalComentarios(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return getNombre();
	}

}
