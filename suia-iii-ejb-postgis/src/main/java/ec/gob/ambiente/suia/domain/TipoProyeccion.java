package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

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

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Feb 26, 2015]
 *          </p>
 */
@Table(name = "projection_types", schema = "suia_iii")
@Entity
@NamedQueries({ @NamedQuery(name = TipoProyeccion.FIND_ALL_WITHOUT_WGS84_ZONA_17S, query = "SELECT tp FROM TipoProyeccion tp WHERE tp.id <> " + TipoProyeccion.TIPO_PROYECCION_WGS84_ZONA_17S) })
public class TipoProyeccion implements Serializable {

	private static final long serialVersionUID = -1096169335030037088L;

	public static final int TIPO_PROYECCION_WGS84_ZONA_17S = 1;
	public static final String FIND_ALL_WITHOUT_WGS84_ZONA_17S = "ec.gob.ambiente.suia.domain.TipoProyeccion.find_all_without_wgs84_zona_17s";

	@Id
	@SequenceGenerator(name = "PROJECTION_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_prty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTION_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "prty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "prty_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "prty_value")
	private int valor;

	@Override
	public String toString() {
		return nombre;
	}
}
