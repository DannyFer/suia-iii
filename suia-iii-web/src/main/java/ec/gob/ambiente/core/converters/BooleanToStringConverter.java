package ec.gob.ambiente.core.converters;

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
@FacesConverter("booleanToStringConverter")
public class BooleanToStringConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		return arg2;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent component, Object value) {
		boolean result = (Boolean) value;
		return result ? "Sí" : "No";
	}

}
