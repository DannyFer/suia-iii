/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

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
@NamedQueries({ @NamedQuery(name = PuntoInmision.FIND_ALL, query = "SELECT p FROM PuntoInmision p") })
@Entity
@Table(name = "immission_points", catalog = "", schema = "suia_iii")
public class PuntoInmision extends PuntoMonitoreo {

	private static final long serialVersionUID = 5950274376263260687L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.PuntoInmision.findAll";

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "impo_average_flow")
	private float caudalPromedio;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "impo_meteorological_conditions")
	private String condicionesMeteorologicas;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "impo_distance_to_the_download_point")
	private float distanciaAPuntoDescarga;

	// Duda tipo dato
	@Getter
	@Setter
	@Column(name = "impo_other_features")
	private String otrasCaracteristicas;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getNombre();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return ((PuntoInmision) obj).getId() == getId();
	}
}
