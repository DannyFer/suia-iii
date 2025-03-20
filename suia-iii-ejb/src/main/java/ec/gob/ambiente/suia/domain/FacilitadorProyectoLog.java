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
 * The persistent class for the project_facilitators_log database table.
 *
 */
@Entity
@Table(name = "project_facilitators_log", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = "FacilitadorProyectoLog.findAll", query = "SELECT u FROM FacilitadorProyectoLog u") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prfl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prfl_status = 'TRUE'")
public class FacilitadorProyectoLog extends EntidadBase {

	private static final long serialVersionUID = -1064770990461331874L;

	@Id
	@SequenceGenerator(name = "PROJECT_FACILITATORS_LOG_GENERATOR", sequenceName = "seq_prfl_id", schema = "suia_iii", allocationSize = 1,initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECT_FACILITATORS_LOG_GENERATOR")
	@Column(name = "prfl_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "prfl_project_negation_date")
	private Date fechaNegacion;

	@Getter
	@Setter
	@Column(name = "prfl_observation")
	private String observacion;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_projects_facilitatos_log_user_id_users_user_id")
	@JoinColumn(name = "user_id")
	private Usuario usuario;

	/*
	 * @Getter
	 * 
	 * @Setter
	 * 
	 * @Column(name = "prfa_status") private String estado;
	 */

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_projects_enviromental_licensing_pren_id_projects_facilitator_log")
	@JoinColumn(name = "pren_id")
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	@Setter
	@Column(name = "prfl_automatic_negation")
	private boolean negacionAutomatica;

	@Getter
	@Setter
	@Column(name = "prfl_resettask")
	private Integer resetarTarea;	
	
	@Getter
	@Setter
	@Column(name = "prco_id")
	private Integer proyectoRcoa;
}
