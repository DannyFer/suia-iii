/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.composite.controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 11/05/2015]
 *          </p>
 */
@ManagedBean
public class FormatearAyudaController {

	public String obtenerAyuda(String html) {
		return html.replace("__ImageUrl__", FacesContext.getCurrentInstance().getExternalContext()
				.getRequestContextPath()
				+ "/resources/images/help");
	}

}
