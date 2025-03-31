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
@NamedQueries({ @NamedQuery(name = EventoEtapaFase.FIND_ALL, query = "select e FROM EventoEtapaFase e where e.eventoOcurrido = :eventoOcurrido order by e.fase.id, e.etapa.id") })
@Entity
@Table(name = "stage_phase_occurred_event", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "spoe_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "spoe_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "spoe_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "spoe_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "spoe_updater_user")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spoe_status = 'TRUE'")
public class EventoEtapaFase extends EntidadAuditable {

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.EventoEtapaFase.findAllByEvento";

	@Getter
	@Setter
	@Id
	@Column(name = "spoe_id")
	@SequenceGenerator(name = "STAGE_PHASE_OCCURRED_EVENT_SPOE_ID_GENERATOR", sequenceName = "stage_phase_occurred_event_spoe_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STAGE_PHASE_OCCURRED_EVENT_SPOE_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "ocev_id")
	@ForeignKey(name = "occurred_event_ocev_id_fkey")
	private EventoOcurrido eventoOcurrido;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "phas_id")
	@ForeignKey(name = "phases_phas_id_fkey")
	private Fase fase;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prst_id")
	@ForeignKey(name = "project_stages_prst_id_fkey")
	private EtapasProyecto etapa;

	@Getter
	@Setter
	@Column(name = "spoe_description", length = 500)
	private String descripcion;


	public EventoEtapaFase(){ }
}