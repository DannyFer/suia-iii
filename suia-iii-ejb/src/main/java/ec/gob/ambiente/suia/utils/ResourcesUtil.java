package ec.gob.ambiente.suia.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;


public class ResourcesUtil {

	private static final Logger LOG = Logger.getLogger(Constantes.class);
	
	public static String getRoleAreaName(String role) {
		InputStream inputStream = Constantes.class.getClassLoader().getResourceAsStream(
				Constantes.NOMBRE_PROPIEDADES_UTILS);
		Properties prop = new Properties();
		try {
			prop.load(inputStream);
			return prop.getProperty(role);
		} catch (IOException e) {
			LOG.error(e, e);
		}
		return "";
	}

	public static String getMessageResourceString(String key) {
		InputStream inputStream = Constantes.class.getClassLoader().getResourceAsStream(
				Constantes.MENSAJES_PROPIEDADES_UTILS);
		Properties prop = new Properties();
		try {
			prop.load(inputStream);
			return prop.getProperty(key);
		} catch (IOException e) {
			LOG.error(e, e);
		}
		return "";
	}
}
