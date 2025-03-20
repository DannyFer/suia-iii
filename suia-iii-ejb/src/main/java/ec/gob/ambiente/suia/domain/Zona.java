package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b>Entity que representa la tabla roles. </b>
 * 
 * @author pganan
 * @version Revision: 1.0
 *          <p>
 *          [Autor: pganan, Fecha: 22/12/2014]
 *          </p>
 */
@Entity
@Table(name = "zones", schema = "public")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "zone_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "zone_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = "Zona.findAll", query = "SELECT z FROM Zona z ORDER BY z.nombre ") })
public class Zona extends EntidadBase {

	private static final long serialVersionUID = -2531527832133142146L;
	@Id
	@SequenceGenerator(name = "ZONES_GENERATOR", sequenceName = "seq_zone_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ZONES_GENERATOR")
	@Column(name = "zone_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "zone_name")
	private String nombre;

	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "zona")
	private List<UbicacionesGeografica> ubicacionesGeograficas;

}
