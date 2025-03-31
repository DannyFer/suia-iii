/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comparator;

import java.util.Comparator;

import ec.gob.ambiente.suia.domain.TipoSector;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 28/07/2015]
 *          </p>
 */
public class OrdenarTipoSectorPorNombreComparator implements Comparator<TipoSector> {

	@Override
	public int compare(TipoSector tipoSector1, TipoSector tipoSector2) {
		return tipoSector1.getNombre().compareTo(tipoSector2.getNombre());
	}
}
