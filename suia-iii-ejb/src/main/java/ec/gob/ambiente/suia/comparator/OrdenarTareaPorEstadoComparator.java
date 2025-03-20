/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comparator;

import java.util.Comparator;

import ec.gob.ambiente.suia.dto.Tarea;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 02/03/2015]
 *          </p>
 */
public class OrdenarTareaPorEstadoComparator implements Comparator<Tarea> {

	@Override
	public int compare(Tarea tarea1, Tarea tarea2) {
		int estadoComp = tarea2.getEstado().compareTo(tarea1.getEstado());
		return ((estadoComp == 0) ? tarea2.getFechaInicio().compareTo(tarea1.getFechaInicio()) : estadoComp);
	}

}
