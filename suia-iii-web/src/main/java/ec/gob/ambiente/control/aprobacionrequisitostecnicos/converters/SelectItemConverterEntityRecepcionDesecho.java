/* 
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.converters;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author magmasoft
 * 
 *         FacesConverter para usar como convertidor de la entidad
 *         EntityRecepcionDesecho
 */

@FacesConverter("selectItemConverterEntityRecepcionDesecho")
public class SelectItemConverterEntityRecepcionDesecho implements Converter {


	/*public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		Object objeto = arg1.getChildren().get(0);
		if (objeto instanceof UISelectItem) {
			objeto = arg1.getChildren().get(1);
			try {
				arg1.getChildren().remove(0);
			} catch (Exception e) {
			}
		}
		UISelectItems items = (UISelectItems) objeto;
		List<?> elements = (List<?>) items.getValue();
		Object myObject = null;
		for (Object object : elements) {
             /*if(arg2.isEmpty()){
				 myObject = null;
			 }
			else //if (((EntityRecepcionDesecho) object).getIdRecepcion().equals(Integer.valueOf(arg2))) {
			if (object.toString().equals(arg2)){
				myObject = object;
				break;
			}

		}

		return myObject;
	}*/

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Object objeto = arg1.getChildren().get(0);
		if (objeto instanceof UISelectItem)
			objeto = arg1.getChildren().get(1);
		UISelectItems items = (UISelectItems) objeto;
		List<?> elements = (List<?>) items.getValue();
		Object myObject = null;
		if(elements!=null){
			for (Object object : elements) {
				if (object.toString().equals(arg2)) {
					myObject = object;
					break;
				}
			}
		}
		return myObject;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		return arg2 == null ? null : arg2.toString();
		/*if (arg2 != null && arg2.toString() != "")

			return ((EntityRecepcionDesecho) arg2).getIdRecepcion().toString();
		return  "";*/


	}

}
