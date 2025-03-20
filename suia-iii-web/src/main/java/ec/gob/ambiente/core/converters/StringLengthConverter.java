package ec.gob.ambiente.core.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * 
 * <b> Convertidor deprecado. </b>
 * 
 * Ya hay una clase de estilo que te formatea el texto de manerca correcta. Este
 * converter no tiene en cuenta el ancho de cada caracter y no es uniforme,
 * tampoco tiene en cuenta el hecho de redimensionar el nevagador. Usar
 * styleClass="singleLine"
 * 
 */
@Deprecated
public class StringLengthConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// No hacer nada
		return arg2;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent component, Object value) {
		String string = (String) value;
		int maxlength = Integer.valueOf((String) component.getAttributes().get("maxlength"));

		if (string.length() > maxlength) {
			return string.substring(0, maxlength) + "...";
		} else {
			return string;
		}
	}

}
