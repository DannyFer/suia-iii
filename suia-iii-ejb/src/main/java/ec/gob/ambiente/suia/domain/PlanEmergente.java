package ec.gob.ambiente.suia.domain;

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

/**
 * The persistent class for the proyectos_licenciamiento_ambiental database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = PlanEmergente.FIND_ALL, query = "select p FROM PlanEmergente p") })
@Entity
@Table(name = "emergency_plan", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "empa_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "empa_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "empa_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "empa_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "empa_updater_user")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "empa_status = 'TRUE'")
public class PlanEmergente extends EntidadAuditable {

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.PlanEmergente.findAll";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "EMERGENCY_PLAN_EMPA_ID_GENERATOR", initialValue = 1, schema = "suia_iii", sequenceName = "emergency_plan_empa_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMERGENCY_PLAN_EMPA_ID_GENERATOR")
	@Column(name = "empa_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cacs_id")
	@ForeignKey(name = "emergency_plan_cacs_id_fkey")
	private CatalogoCategoriaSistema catalogoCategoria;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "sety_id")
	@ForeignKey(name = "emergency_plan_sety_id_fkey")
	private TipoSector tipoSector;

	@Getter
	@Setter
	@Column(name = "empa_name", length = 10485760)
	private String nombre;

	@Getter
	@Setter
	@Column(name = "empa_representative_type", length = 2)
	private String tipoRepresentante;

	@Getter
	@Setter
	@Column(name = "empa_representative_id", length = 13)
	private String ciRepresentante;

	@Getter
	@Setter
	@Column(name = "empa_representative_name", length = 500)
	private String nombreRepresentante;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "occurred_event_pren_id_fkey")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@Column(name = "water_composition")
	private boolean composicionAgua;

	@Getter
	@Setter
	@Column(name = "soil_composition")
	private boolean composicionSuelo;

	@Getter
	@Setter
	@Column(name = "air_composition")
	private boolean composicionAire;

	@Getter
	@Setter
	@Column(name = "flora")
	private boolean flora;

	@Getter
	@Setter
	@Column(name = "wildlife")
	private boolean fauna;

	@Getter
	@Setter
	@Column(name = "economic")
	private boolean socioEconomico;

	@Getter
	@Setter
	@Column(name = "cultural")
	private boolean socioCultural;

	@Getter
	@Setter
	@Column(name = "archeological")
	private boolean arqueologico;

	@Getter
	@Setter
	@Column(name = "other_descritcion", length = 150)
	private String descripcionOtroFactor;

	@Getter
	@Setter
	@Column(name = "up_to_ten")
	private boolean hastaDiezDiasParaImplementar;

	@Getter
	@Setter
	@Column(name = "empa_transact", length = 150)
	private String tramite;

	@Override
	public String toString() {
		return nombre;
	}

	public PlanEmergente() {

	}

}