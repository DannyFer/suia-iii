package ec.gob.ambiente.suia.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * The persistent class for the proyectos_licenciamiento_ambiental database table.
 * 
 */
@NamedQueries({
		@NamedQuery(name = EventoOcurrido.FIND_ALL, query = "select e FROM EventoOcurrido e"),
		@NamedQuery(name = EventoOcurrido.FIND_ALL_BY_PROYECTO, query = "select e FROM EventoOcurrido e where e.proyectoLicenciamientoAmbiental = :proyecto"),
		@NamedQuery(name = EventoOcurrido.FIND_ALL_BY_PLAN_EMERGENTE, query = "select e FROM EventoOcurrido e where e.planEmergente.id = :planEmergente")
})
@Entity
@Table(name = "occurred_event", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ocev_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ocev_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ocev_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ocev_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ocev_updater_user")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ocev_status = 'TRUE'")
public class EventoOcurrido extends EntidadAuditable {

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.EventoOcurrido.findAll";
	public static final String FIND_ALL_BY_PROYECTO = "ec.com.magmasoft.business.domain.EventoOcurrido.findAllByProyecto";
	public static final String FIND_ALL_BY_PLAN_EMERGENTE = "ec.com.magmasoft.business.domain.EventoOcurrido.findAllByPlanEmrgente";

	@Getter
	@Setter
	@Id
	@Column(name = "ocev_id")
	@SequenceGenerator(name = "OCCURRED_EVENT_OCEV_ID_GENERATOR", sequenceName = "occurred_event_ocev_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OCCURRED_EVENT_OCEV_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "empa_id")
	@ForeignKey(name = "occurred_event_empa_id_fkey")
	private PlanEmergente planEmergente;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "occurred_event_pren_id_fkey")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@Column(name = "ocev_description", length = 500)
	private String descripcion;

	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	@Column(name = "ocev_date")
	protected Date fecha;

	@Getter
	@Setter
	@Column(name = "ocev_hour")
	private Integer hora;

	@Getter
	@Setter
	@Column(name = "ocev_minute")
	private Integer minuto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "occurred_event_gelo_id_fkey")
	private UbicacionesGeografica parroquia;

	@Getter
	@Setter
	@Column(name = "ocev_cocession", length = 500)
	private String concesion;

	@Getter
	@Setter
	@Column(name = "ocev_benefit", length = 500)
	private String beneficio;

	@Getter
	@Setter
	@Column(name = "ocev_tailings", length = 500)
	private String relavera;

	@Getter
	@Setter
	@OneToMany(mappedBy = "eventoOcurrido")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "coev_status = 'TRUE'")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<EventoCoordenada> coordenadas;

	@Getter
	@Setter
	@OneToMany(mappedBy = "eventoOcurrido")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spoe_status = 'TRUE'")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<EventoEtapaFase> etapas;

	@Transient
	private String tiempo;

	public String getTiempo() {
		return String.format("%02d", hora) + ":" + String.format("%02d", minuto);
	}

	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
		hora = new Integer(tiempo.split(":")[0]);
		minuto = new Integer(tiempo.split(":")[1]);
	}

	@Getter
	@Setter
	@Column(name = "ocev_meridian", length = 1)
	private String meridiano;

	@Getter
	@Setter
	@Column(name = "ocev_causative", length = 500)
	private String causante;

	public EventoOcurrido() {
		hora = 12;
		minuto = 0;
	}

	/*@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "phas_id")
	@ForeignKey(name = "emergency_plan_phas_id_fkey")
	private Fase fase;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prst_id")
	@ForeignKey(name = "emergency_plan_prst_id_fkey")
	private EtapasProyecto etapa;*/

}