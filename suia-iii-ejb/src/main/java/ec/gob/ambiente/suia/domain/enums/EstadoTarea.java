/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain.enums;

import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 27/02/2015]
 *          </p>
 */
public enum EstadoTarea {

	Completed,
	Digitalizado;

	public static String getNombreEstado(String estado) {
		if (estado.equals(EstadoTarea.Completed.name())) {
			return Constantes.ESTADO_TAREA_COMPLETADA;
		} else if (estado.equals(EstadoTarea.Digitalizado.name())) {
			return Constantes.ESTADO_TAREA_COMPLETADA + " - Proceso " + EstadoTarea.Digitalizado.name() ; 
		} else
			return Constantes.ESTADO_TAREA_INICIADA;
	}
}
