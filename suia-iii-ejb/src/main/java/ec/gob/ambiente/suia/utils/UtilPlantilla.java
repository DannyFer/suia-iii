package ec.gob.ambiente.suia.utils;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class UtilPlantilla {

	public static ResourceBundle getResourcePlantillaInformes() {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "plantillas");
		return bundle;
	}
	public static String getPlantillaConParametros(String keyPlantilla, String[] parametros) {
		String htmlPage =keyPlantilla ;
		//getResourcePlantillaInformes().getString(keyPlantilla)
		if (parametros != null && parametros.length >= 1) {
			for (int i = 0; i < parametros.length; i++) {
				if (parametros[i] == null) {
					parametros[i] = "";
				}
			}
			htmlPage = String.format(htmlPage, (Object[])parametros);

		}
		return htmlPage;
	}

}
