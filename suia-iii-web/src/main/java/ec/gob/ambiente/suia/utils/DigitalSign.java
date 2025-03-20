package ec.gob.ambiente.suia.utils;


public class DigitalSign {

	public static String sign(String urlAlfresco, String cedula) {
		String urlGlobal = null;
		try {
			String tiketStr = "alf_ticket=";
			if (!urlAlfresco.endsWith(tiketStr)) {
				int pos = urlAlfresco.lastIndexOf(tiketStr) + tiketStr.length();
				urlAlfresco = urlAlfresco.substring(0, pos);
			}
			urlGlobal = Constantes.getSuiaDigitalSign()
					+ "index.jsf?cedulaUser="
					+ cedula
					+ "&urlWsdl="
					+ Constantes.getSuiaDigitalSign()
					+ "digitalSign/DigitalSignServices&system=SUIA&tipoFirma=1&urlAlfrescoFile="
					+ urlAlfresco;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return urlGlobal;

	}
	
	/**
	 * Método que genera la firma en la parte superior de la ultima hoja del documento
	 * @param urlAlfresco
	 * @param cedula
	 * @return
	 */
	public static String signBlankPage(String urlAlfresco, String cedula) {
		String urlGlobal = null;
		try {
			String tiketStr = "alf_ticket=";
			if (!urlAlfresco.endsWith(tiketStr)) {
				int pos = urlAlfresco.lastIndexOf(tiketStr) + tiketStr.length();
				urlAlfresco = urlAlfresco.substring(0, pos);
			}
			urlGlobal = Constantes.getSuiaDigitalSign()
					+ "index.jsf?cedulaUser="
					+ cedula
					+ "&urlWsdl="
					+ Constantes.getSuiaDigitalSign()
					+ "digitalSign/DigitalSignServices&system=SUIA&tipoFirma=2&urlAlfrescoFile="
					+ urlAlfresco;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return urlGlobal;

	}
}
