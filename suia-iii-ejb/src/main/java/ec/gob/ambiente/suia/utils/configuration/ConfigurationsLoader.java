/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.utils.configuration;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 02/04/2015]
 *          </p>
 */
public class ConfigurationsLoader {

	public static final String CONFIGS_DIR = System.getProperty("jboss.home.dir")
			+ "/standalone/configuration/mae-custom/";

	public static final String KEY_VALUES_SEPARATOR = ";";

	public enum Files {
		proyecto_registro
	}

	public static String loadAsString(Files fileId, String key) {
		try {
			return loadFromExternalProperties(fileId, key);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static List<String> loadAsStringList(Files fileId, String key) {
		try {
			return splitAndTrimBySeparator(loadFromExternalProperties(fileId, key));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Integer loadAsInteger(Files fileId, String key) {
		try {
			return new Integer(loadFromExternalProperties(fileId, key));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static List<Integer> loadAsIntList(Files fileId, String key) {
		try {
			List<String> values = splitAndTrimBySeparator(loadFromExternalProperties(fileId, key));
			if (values.isEmpty())
				return null;
			List<Integer> result = new ArrayList<Integer>();
			for (int i = 0; i < values.size(); i++)
				result.add(Integer.parseInt(values.get(i)));
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private static List<String> splitAndTrimBySeparator(String value) {
		List<String> finalVaues = new ArrayList<String>();
		String[] values = value.split(KEY_VALUES_SEPARATOR);
		for (String string : values) {
			String result = string.trim();
			if (!result.isEmpty())
				finalVaues.add(result);
		}
		return finalVaues;
	}

	private static String loadFromExternalProperties(Files fileId, String key) {
		try {
			String realAdd = CONFIGS_DIR + fileId + ".properties";
			Properties properties = new Properties();
			properties.load(new FileInputStream(realAdd));
			return properties.get(key).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
