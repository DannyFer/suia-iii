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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({ @NamedQuery(name = FacilitadorProyecto.GET_ALL, query = "SELECT m FROM FacilitadorProyecto m") })
@Entity
@Table(name = "project_facilitators", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prfa_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prfa_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prfa_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prfa_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prfa_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prfa_status = 'TRUE'")
public class FacilitadorProyecto extends EntidadAuditable {
	/**
	 * 
	 */
	
	public static final String GET_ALL = "ec.com.magmasoft.business.domain.FacilitadorProyecto.getAll";
	
	private static final long serialVersionUID = -8816530990512091942L;

	@Id
	@SequenceGenerator(name = "PROJECT_FACILITATORS_PRFAID_GENERATOR", sequenceName = "seq_prfa_id", schema = "suia_iii", allocationSize = 1,initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECT_FACILITATORS_PRFAID_GENERATOR")
	@Column(name = "prfa_id")
	@Getter
	@Setter
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "ensp_id")
	@ForeignKey(name = "fk_projects_facilitatos_ensp_id_environmental_social_participat")
	@Getter
	@Setter
	private ParticipacionSocialAmbiental participacionSocialAmbiental;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_projects_facilitatos_user_id_users_user_id")
	@Getter
	@Setter
	private Usuario usuario;
	
	@Getter
	@Setter
	@Column(name = "prfa_acceptation")
	private boolean aceptacion;
	
	@Getter
	@Setter
	@Column(name = "prfa_report")
	private boolean informe;
	
	@Getter
	@Setter
	@Column(name = "prfa_attendant")
	private boolean facilitadorEncargado;
	
//	@Getter
//	@Setter
//	@Column(name = "prfa_status")
//	private boolean estado;
	
	@Getter
	@Setter
	@Column(name = "prfa_project_accept_date")
	private Date fechaAceptacion;
	
	@Getter
	@Setter
	@Column(name = "prfa_generation_date")
	private Date fechaNotificacion;


	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_projects_enviromental_licensing_pren_id_projects_facilitator")
	@JoinColumn(name = "pren_id")
	private ProyectoLicenciamientoAmbiental proyecto;
	
	@Getter
	@Setter
	@Column(name = "prfl_resettaskok")
	private Integer resetarTareaok;
	
	@Getter
	@Setter
	@Column(name = "prco_id")
	private Integer proyectoRcoa;
}
