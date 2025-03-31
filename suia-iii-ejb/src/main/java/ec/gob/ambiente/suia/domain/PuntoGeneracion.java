package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 08/06/2015]
 *          </p>
 */
@Entity
@Table(name = "generation_points", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "gepo_status")) })
@NamedQueries({ @NamedQuery(name = PuntoGeneracion.FILTRAR, query = "SELECT p FROM PuntoGeneracion p WHERE (lower(p.nombre) LIKE :filter OR lower(p.clave) LIKE :filter )") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gepo_status = 'TRUE'")
public class PuntoGeneracion extends EntidadBase {

	private static final long serialVersionUID = 8310298753100964914L;

	public static final String FILTRAR = "ec.gob.ambiente.suia.domain.PuntoGeneracion.FILTRAR";

	public static final int PUNTO_GENERACION_OTRO = 1;

	@Getter
	@Setter
	@Id
	@Column(name = "gepo_id")
	@SequenceGenerator(name = "GENERATION_POINTS_GEPO_ID_GENERATOR", sequenceName = "seq_gepo_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GENERATION_POINTS_GEPO_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "gepo_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "gepo_key")
	private String clave;

}
