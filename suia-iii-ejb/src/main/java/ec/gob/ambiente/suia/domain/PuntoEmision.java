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
 *          [Autor: mayriliscs, Fecha: 19/12/2014]
 *          </p>
 */
@NamedQueries({ @NamedQuery(name = PuntoEmision.FIND_ALL, query = "SELECT p FROM PuntoEmision p") })
@Entity
@Table(name = "emission_points", catalog = "", schema = "suia_iii")
public class PuntoEmision extends PuntoMonitoreo {

	private static final long serialVersionUID = 7282649587879554172L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.PuntoEmision.findAll";

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "soty_id")
	@ForeignKey(name = "fk_emission_pointsmopo_id_source_typessoty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "soty_status = 'TRUE'")
	private TipoFuente tipoFuente;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "futy_id")
	@ForeignKey(name = "fk_emission_pointsmopo_id_fuel_typesfuty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "futy_status = 'TRUE'")
	private TipoCombustible tipoCombustible;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_lighter_dimension")
	private float dimensionMechero;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_lighter_height")
	private float alturaMechero;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_lighter_width")
	private float anchoMechero;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_lighter_type")
	private String tipoMechero;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_lighter_technical_details")
	private String detallesTecnicosMechero;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_lighter_operating_frequency")
	private float periodicidadFuncionamientoMechero;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_source_capacity")
	private float capacidadFuente;

	@Getter
	@Setter
	@Column(name = "empo_source_operation_time")
	private String tiempoFuncionamientoFuente;

	@Getter
	@Setter
	@Column(name = "empo_source_height_broadcast")
	private String alturaFuenteEmision;

	@Getter
	@Setter
	@Column(name = "empo_fuel_consumption")
	private String consumoCombustible;

	@Getter
	@Setter
	@Column(name = "empo_incinerate_materials")
	private String materialesAIncinerar;

	@Getter
	@Setter
	@Column(name = "empo_average_emission_volume")
	private String volumenPromedioEmision;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_average_wind_speed")
	private float velocidadPromedioViento;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_main_wind_direction")
	private String direccionPrincipalViento;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_x_lat_degrees")
	private float coordenadaGeogXLatGrados;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_x_lat_minutes")
	private float coordenadaGeogXLatMinutos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_x_lat_seconds")
	private float coordenadaGeogXLatSegundos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_y_lat_degrees")
	private float coordenadaGeogYLatGrados;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_y_lat_minutes")
	private float coordenadaGeogYLatMinutos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_y_lat_seconds")
	private float coordenadaGeogYLatSegundos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_x_lng_degrees")
	private float coordenadaGeogXLngGrados;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_x_lng_minutes")
	private float coordenadaGeogXLngMinutos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_x_lng_seconds")
	private float coordenadaGeogXLngSegundos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_y_lng_degrees")
	private float coordenadaGeogYLngGrados;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_y_lng_minutes")
	private float coordenadaGeogYLngMinutos;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "empo_geographic_coordinates_y_lng_seconds")
	private float coordenadaGeogYLngSegundos;

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
