/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comparator;

import java.util.Comparator;

import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 02/03/2015]
 *          </p>
 */
public class OrdenarCategoriaSistemaPorRangoComparator implements Comparator<CatalogoCategoriaSistema> {

	@Override
	public int compare(CatalogoCategoriaSistema catatoCategoriaSistema1,
			CatalogoCategoriaSistema catatoCategoriaSistema2) {
		if (catatoCategoriaSistema1.getRango() == null || catatoCategoriaSistema2.getRango() == null
				|| catatoCategoriaSistema1.getRango().getInicioRango() == null
				|| catatoCategoriaSistema2.getRango().getInicioRango() == null) {
			return catatoCategoriaSistema1.getId().compareTo(catatoCategoriaSistema2.getId());
		} else {
			return catatoCategoriaSistema1.getRango().getInicioRango()
					.compareTo(catatoCategoriaSistema2.getRango().getInicioRango());
		}
	}
}
