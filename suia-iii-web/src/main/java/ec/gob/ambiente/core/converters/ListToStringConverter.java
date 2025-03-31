package ec.gob.ambiente.core.converters;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author carlos.pupo
 * 
 *         FacesConverter para usar en cualquier componente que haga uso de una
 *         lista y se quiera mostrar la misma separando elementos por comas
 */
@FacesConverter("listToStringConverter")
public class ListToStringConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		return arg2;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent component, Object value) {
		List<?> list = (List<?>) value;
		return getListToString(list);
	}

	public static String getListToString(List<?> list) {
		String result = "";
		for (Object object : list) {
			result += object.toString() + ", ";
		}
		if (result.length() > ", ".length())
			result = result.substring(0, result.length() - ", ".length());
		return result;
	}
}
