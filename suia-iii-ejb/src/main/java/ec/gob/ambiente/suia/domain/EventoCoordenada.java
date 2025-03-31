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

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;

/**
 * The persistent class for the proyectos_licenciamiento_ambiental database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = EventoCoordenada.FIND_ALL, query = "select c FROM EventoCoordenada c where c.eventoOcurrido = :eventoOcurrido") })
@Entity
@Table(name = "coordinate_event", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "coev_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "coev_status = 'TRUE'")
public class EventoCoordenada extends EntidadBase {

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.EventoCoordenada.findAllByEvento";

	@Getter
	@Setter
	@Id
	@Column(name = "coev_id")
	@SequenceGenerator(name = "OCCURRED_EVENT_OCEV_ID_GENERATOR", sequenceName = "occurred_event_ocev_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OCCURRED_EVENT_OCEV_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "coev_shape", length = 255)
	private String shape;

	@Getter
	@Setter
	@Column(name = "coev_x")
	private double x;

	@Getter
	@Setter
	@Column(name = "coev_y")
	private double y;

	@Getter
	@Setter
	@Column(name = "coev_type", length = 255)
	private String tipo;

	@Getter
	@Setter
	@Column(name = "coev_description", length = 255)
	private String descripcion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "ocev_id")
	@ForeignKey(name = "occurred_event_ocev_id_fkey")
	private EventoOcurrido eventoOcurrido;

	public EventoCoordenada(CoordinatesWrapper wrapper, EventoOcurrido eventoOcurrido) {
		this.shape = wrapper.getCoordenadas().get(0).getOrden().toString();
		this.x = wrapper.getCoordenadas().get(0).getX();
		this.y = wrapper.getCoordenadas().get(0).getY();
		this.tipo = wrapper.getTipoForma().getNombre();
		this.descripcion = wrapper.getCoordenadas().get(0).getDescripcion();
		this.eventoOcurrido = eventoOcurrido;
		this.estado = true;
	}

	public EventoCoordenada(){ }

	@Override
	public String toString() {
		return x + " " + y;
	}
}