/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.utils;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Extension;

import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 12/08/2015]
 *          </p>
 */
public class ValidatorTD implements Extension {

	public void onStartup(@Observes AfterDeploymentValidation afterDeploymentValidation) throws Exception {
		TipoDocumentoSistema[] values = TipoDocumentoSistema.class.getEnumConstants();
		Map<Integer, String> scanned = new HashMap<Integer, String>();
		for (TipoDocumentoSistema tipoDocumento : values) {
			if(!scanned.containsKey(tipoDocumento.getIdTipoDocumento()))
				scanned.put(tipoDocumento.getIdTipoDocumento(), tipoDocumento.toString());
			else {
				throw new Exception("\n\n\nError: ya existe una entrada para TipoDocumento = " + tipoDocumento.getIdTipoDocumento() + ", usandose en " + scanned.get(tipoDocumento.getIdTipoDocumento()) + ", duplicada en " + tipoDocumento.toString() + "\n\n");
			}
		}
	}
}
