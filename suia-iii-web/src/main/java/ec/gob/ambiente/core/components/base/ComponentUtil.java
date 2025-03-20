/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.core.components.base;

import java.lang.reflect.Method;
import java.util.List;

import javax.faces.component.UIComponent;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 09/01/2015]
 *          </p>
 */
public class ComponentUtil {

	public static String FAMILY = "ec.gob.ambiente.core.components.mae";

	private static void executeFunction(ExecuteComponentFunction function) {
		if (function != null)
			function.execute();
	}

	public static Object getParameter(UIComponent component, String name) {
		if (component.getAttributes().containsKey(name))
			return component.getAttributes().get(name);
		return null;
	}

	public static void setReadonly(UIComponent component, boolean value) {
		setReadonly(component, value, null);
	}

	public static void setReadonly(UIComponent component, boolean value, ExecuteComponentFunction function) {
		try {
			Method method = component.getClass().getMethod("setReadonly", new Class<?>[] { boolean.class });
			method.invoke(component, new Object[] { value });
			executeFunction(function);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setDisabled(UIComponent component, boolean value) {
		setDisabled(component, value, null);
	}

	public static void setDisabled(UIComponent component, boolean value, ExecuteComponentFunction function) {
		try {
			Method method = component.getClass().getMethod("setDisabled", new Class<?>[] { boolean.class });
			method.invoke(component, new Object[] { value });
			executeFunction(function);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addStyleClass(UIComponent component, String classes) {
		addStyleClass(component, classes, null);
	}

	public static void addStyleClass(UIComponent component, String classes, ExecuteComponentFunction function) {
		try {
			Method method = component.getClass().getMethod("getStyleClass");
			String currentClasses = (String) method.invoke(component);
			if (currentClasses == null)
				currentClasses = "";
			currentClasses += " " + classes;

			method = component.getClass().getMethod("setStyleClass", new Class<?>[] { String.class });
			method.invoke(component, new Object[] { currentClasses });
			executeFunction(function);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removeStyleClass(UIComponent component, String clazz) {
		try {
			Method method = component.getClass().getMethod("getStyleClass");
			String currentClasses = (String) method.invoke(component);
			if (currentClasses == null)
				currentClasses = "";
			currentClasses = currentClasses.replace(clazz, "");

			method = component.getClass().getMethod("setStyleClass", new Class<?>[] { String.class });
			method.invoke(component, new Object[] { currentClasses });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isComponentAnyClass(UIComponent component, List<String> classesNames) {
		return isComponentAnyClass(component, classesNames, null);
	}

	public static boolean isComponentAnyClass(UIComponent component, List<String> classesNames,
			ExecuteComponentFunction function) {
		for (String className : classesNames) {
			if (isComponentClass(component, className, function))
				return true;
		}
		return false;
	}
	
	public static boolean isNotComponentAnyClass(UIComponent component, List<String> classesNames) {
		return isNotComponentAnyClass(component, classesNames, null);
	}

	public static boolean isNotComponentAnyClass(UIComponent component, List<String> classesNames,
			ExecuteComponentFunction function) {
		boolean found = false;
		for (String className : classesNames) {
			if (isComponentClass(component, className, function)) {
				found = true;
				break;
			}
		}
		return !found;
	}

	public static boolean isComponentClass(UIComponent component, String className) {
		return component.getClass().getName().equals(className);
	}

	public static boolean isComponentClass(UIComponent component, String className, ExecuteComponentFunction function) {
		if (component.getClass().getName().equals(className)) {
			executeFunction(function);
			return true;
		}
		return false;
	}
}
