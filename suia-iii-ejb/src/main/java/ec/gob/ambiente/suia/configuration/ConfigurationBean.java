/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import ec.gob.ambiente.suia.configuration.domain.ConfigEntry;
import ec.gob.ambiente.suia.configuration.facade.ConfigurationFacade;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 06/04/2015]
 *          </p>
 */
@ApplicationScoped
public class ConfigurationBean {

	public static final String KEY_VALUES_SEPARATOR = ";";
	public static final String KEY_OVERRIDE_INDICATOR = "OVERRIDE.";

	private Map<String, String> configurations;

	@EJB
	private ConfigurationFacade configurationFacade;

	public String getConfigurationValue(String key) {
		Map<String, String> config = loadConfigurations();
		if (config != null && config.containsKey(key))
			return config.get(key);
		return null;
	}
	
	public boolean containsIntegerConfigurationValue(String key, Integer value) {
		return false;
	}

	public void resetConfigurations() {
		configurations = null;
		loadConfigurations();
	}

	private Map<String, String> loadConfigurations() {
		if (configurations == null) {
			configurations = new HashMap<String, String>();
			try {
				List<ConfigEntry> entries = configurationFacade.getConfigEntries();
				for (ConfigEntry configEntry : entries) {
					configurations.put(configEntry.getKey(), configEntry.getValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return configurations;
	}
}
