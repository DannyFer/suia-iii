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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 10/06/2015]
 *          </p>
 */
@Entity
@Table(name = "hazardous_wastes_collector_locations", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwcl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwcl_status = 'TRUE'")
public class GeneradorDesechosRecoletorUbicacionGeografica extends EntidadBase {

	private static final long serialVersionUID = -8859457697907693779L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "H_W_COLLECTOR_LOCATIONS_ID_GENERATOR", schema="suia_iii", sequenceName = "seq_hwcl_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "H_W_COLLECTOR_LOCATIONS_ID_GENERATOR")
	@Column(name = "hwcl_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwco_id")
	@ForeignKey(name = "fk_hwcl_id_hwco_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwco_status = 'TRUE'")
	private GeneradorDesechosRecolector generadorDesechosRecolector;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_hwcl_id_geographical_locationsgelo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
	private UbicacionesGeografica ubicacionesGeografica;

	@Override
	public String toString() {
		String provincia = "", canton = "";
		if (ubicacionesGeografica != null && ubicacionesGeografica.getNombre() != null)
			canton = ubicacionesGeografica.getNombre();
		if (ubicacionesGeografica.getUbicacionesGeografica() != null
				&& ubicacionesGeografica.getUbicacionesGeografica().getNombre() != null)
			provincia = ubicacionesGeografica.getUbicacionesGeografica().getNombre();
		if (!provincia.isEmpty() && !canton.isEmpty())
			return provincia + " - " + canton;
		else if (!provincia.isEmpty())
			return provincia;
		else
			return canton;
	}
}
