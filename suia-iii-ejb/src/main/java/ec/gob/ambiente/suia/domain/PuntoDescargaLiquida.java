/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 29/12/2014]
 *          </p>
 */

/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia Todos los derechos reservados
 */

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 19/12/2014]
 *          </p>
 */
@NamedQueries({ @NamedQuery(name = PuntoDescargaLiquida.FIND_ALL, query = "SELECT p FROM PuntoDescargaLiquida p") })
@Entity
@Table(name = "liquid_discharge_points", catalog = "", schema = "suia_iii")
public class PuntoDescargaLiquida extends PuntoMonitoreo {

	private static final long serialVersionUID = -6743448699213303545L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.PuntoDescargaLiquida.findAll";

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_x_lat_degrees")
	private float coordenadaGeogXLatGrados;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_x_lat_minutes")
	private float coordenadaGeogXLatMinutos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_x_lat_seconds")
	private float coordenadaGeogXLatSegundos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_y_lat_degrees")
	private float coordenadaGeogYLatGrados;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_y_lat_minutes")
	private float coordenadaGeogYLatMinutos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_y_lat_seconds")
	private float coordenadaGeogYLatSegundos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_x_lng_degrees")
	private float coordenadaGeogXLngGrados;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_x_lng_minutes")
	private float coordenadaGeogXLngMinutos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_x_lng_seconds")
	private float coordenadaGeogXLngSegundos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_y_lng_degrees")
	private float coordenadaGeogYLngGrados;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_y_lng_minutes")
	private float coordenadaGeogYLngMinutos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_geographic_coordinates_y_lng_seconds")
	private float coordenadaGeogYLngSegundos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_monitoring_code_point")
	private String codigoPuntoMonitoreo;

	// Duda cardinalidad
	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name = "impo_id")
	@ForeignKey(name = "fk_liquid_discharge_pointsmopo_id_immission_pointsmopo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mopo_status = 'TRUE'")
	private PuntoInmision puntoInmision;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_download_type")
	private String tipoDescarga;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_block")
	private String bloque;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_facility")
	private String facilidad;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_prior_discharge_treatment")
	private String tratamientoPrevioDescarga;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_other_features")
	private String otrasCaracteristicas;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_average_flow")
	private float caudalPromedio;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "lidi_reference_system")
	private String sistemaReferencia;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tizo_id")
	@ForeignKey(name = "fk_liquid_discharge_pointsmopo_id_time_zonestizo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tizo_status = 'TRUE'")
	private HusoHorario husoHorario;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getNombre();
	}

}
