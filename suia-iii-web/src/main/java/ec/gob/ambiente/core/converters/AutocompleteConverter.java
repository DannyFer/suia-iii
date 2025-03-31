/* 
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.core.converters;

import ec.gob.ambiente.suia.domain.Laboratorio;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import java.util.List;


@FacesConverter("autocompleteConverter")
@ManagedBean
@RequestScoped
public class AutocompleteConverter implements Converter {

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
		UIParameter param = ((UIParameter)(arg1.getChildren().get(0)));

		Object myObj= null;
		try {
			@SuppressWarnings("unchecked")
			List<Laboratorio> laboratoriosCargados = (List<Laboratorio>) param.getValue();
			for (Laboratorio lab: laboratoriosCargados) {
				if (lab.toString().equals(arg2)) {
					myObj = lab;
					break;
				}
			}
			return myObj;

		} catch (  Exception e) {
			return null;
		}



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
