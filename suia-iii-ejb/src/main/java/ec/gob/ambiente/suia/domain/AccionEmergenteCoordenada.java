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
@NamedQueries({ @NamedQuery(name = AccionEmergenteCoordenada.FIND_ALL, query = "select c FROM AccionEmergenteCoordenada c where c.accionEmergente = :accionEmergente") })
@Entity
@Table(name = "coordinate_emerging_action", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ceac_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ceac_status = 'TRUE'")
public class AccionEmergenteCoordenada extends EntidadBase {

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.AccionEmergenteCoordenada.findAllByAccion";

	@Getter
	@Setter
	@Id
	@Column(name = "ceac_id")
	@SequenceGenerator(name = "COORDINATE_EMERGING_ACTION_CEAC_ID_GENERATOR", sequenceName = "coordinate_emerging_action_ceac_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COORDINATE_EMERGING_ACTION_CEAC_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "ceac_shape", length = 255)
	private String shape;

	@Getter
	@Setter
	@Column(name = "ceac_x")
	private double x;

	@Getter
	@Setter
	@Column(name = "ceac_y")
	private double y;

	@Getter
	@Setter
	@Column(name = "ceac_type", length = 255)
	private String tipo;

	@Getter
	@Setter
	@Column(name = "ceac_description", length = 255)
	private String descripcion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "emac_id")
	@ForeignKey(name = "emerging_action_emac_id_fkey")
	private AccionEmergente accionEmergente;

	public AccionEmergenteCoordenada(CoordinatesWrapper wrapper, AccionEmergente accionEmergente) {
		this.shape = wrapper.getCoordenadas().get(0).getOrden().toString();
		this.x = wrapper.getCoordenadas().get(0).getX();
		this.y = wrapper.getCoordenadas().get(0).getY();
		this.tipo = wrapper.getTipoForma().getNombre();
		this.descripcion = wrapper.getCoordenadas().get(0).getDescripcion();
		this.accionEmergente = accionEmergente;
		this.estado = true;
	}

	public AccionEmergenteCoordenada(){ }

	@Override
	public String toString() {
		return x + " " + y;
	}
}