/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.utils.configuration;

import java.io.File;
import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Extension;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 02/04/2015]
 *          </p>
 */
public class Configurations implements Extension {

	public void onStartup(@Observes AfterDeploymentValidation afterDeploymentValidation) throws IOException {
		new File(ConfigurationsLoader.CONFIGS_DIR).mkdirs();
		for (ConfigurationsLoader.Files name : ConfigurationsLoader.Files.values()) {
			new File(ConfigurationsLoader.CONFIGS_DIR + name.toString() + ".properties").createNewFile();
		}
	}
}
