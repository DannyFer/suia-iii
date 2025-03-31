/* 
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.core.converters;

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
 *         FacesConverter para usar en cualquier componente que haga uso de
 *         <f:selectItems> y tenga como primer hijo este componente. La clase
 *         que sea pasada como seleccionable debe implementar el toString()
 */
@FacesConverter("selectItemConverter2")
public class SelectItemConverter2 implements Converter {

	/**
	 * Devuelve la cadena pasada como objeto
	 * 
	 * @param arg0
	 *            Contexto de JSF
	 * @param arg1
	 *            Componente al que se le aplica el converter
	 * @param arg2
	 *            seleccionable pasado
	 * @return el objeto que corresponde con el toString() de algun objeto de la
	 *         coleccion
	 */
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Object objeto = arg1.getChildren().get(0);
		if(objeto instanceof UISelectItem){
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
			if (object.toString().equals(arg2)) {
				myObject = object;
				break;
			}
		}
		return myObject;
	}

	/**
	 * Muestra el objeto como cadena
	 * 
	 * @param arg0
	 *            Contexto de JSF
	 * @param arg1
	 *            Componente al que se le aplica el converter
	 * @param arg2
	 *            el objeto a mostrar
	 * @return el valor del toString() del objeto pasado
	 */
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		return arg2 == null ? null : arg2.toString();
	}

}
