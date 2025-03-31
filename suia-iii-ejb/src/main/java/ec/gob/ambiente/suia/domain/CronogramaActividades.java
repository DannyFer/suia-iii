package ec.gob.ambiente.suia.domain;

import java.sql.Timestamp;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the activities_schedule database table.
 * 
 */
@Entity
@Table(name = "activities_schedule", catalog = "", schema = "suia_iii")
@NamedQuery(name = "CronogramaActividades.findAll", query = "SELECT c FROM CronogramaActividades c")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "acsc_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "acsc_date_creation")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "acsc_date_modification")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "acsc_user_creation")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "acsc_user_modification")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "acsc_status = 'TRUE'")
public class CronogramaActividades extends EntidadAuditable {

	private static final long serialVersionUID = 8867308242883257003L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.CronogramaActividades.findAll";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "ACTIVITIES_SCHEDULE_ACSCID_GENERATOR", initialValue = 1, sequenceName = "seq_acsc_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVITIES_SCHEDULE_ACSCID_GENERATOR")
	@Column(name = "acsc_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "acsc_accountable", length = 50)
	private String responsable;

	@Getter
	@Setter
	@Column(name = "acsc_activity_description", length = 1048576)
	private String descripcionActividades;

	@Getter
	@Setter
	@Column(name = "acsc_requirement", length = 1048576)
	private String requerimientos;

	@Getter
	@Setter
	@Column(name = "acsc_start_date")
	private Timestamp fechaInicio;

	@Getter
	@Setter
	@Column(name = "acsc_end_date")
	private Timestamp fechaFin;

	@Getter
	@Setter
	@Column(name = "acsc_verification_means", length = 255)
	private String mediosVerificacion;

	@Getter
	@Setter
	@Column(name = "acsc_process_id", nullable = false)
	private long idProceso;

	// bi-directional many-to-one association to CatalogoCategorias
	@ManyToOne
	@Getter
	@Setter
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_activities_schedpren_id_projects_environt_licensingpren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	public CronogramaActividades() {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		CronogramaActividades c = (CronogramaActividades) obj;
		return id == c.id;
	}
}