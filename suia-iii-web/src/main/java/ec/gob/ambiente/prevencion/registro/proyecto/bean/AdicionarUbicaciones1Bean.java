/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registro.proyecto.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.comun.bean.AdicionarUbicacionesBean;

/**
 * <b> Para reutilizar la misma implementacion que el padre, pues es necesario
 * incluir 2 veces la misma logica en una misma pantalla. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 10/02/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class AdicionarUbicaciones1Bean extends AdicionarUbicacionesBean {

	private static final long serialVersionUID = 4379009924668348522L;

	@PostConstruct
	public void init() {
		super.init();
		tablaUbicaciones = "tbl_ubicaciones1";
		dialogWidgetVar = "adicionarUbicaciones1";
		panelUbicacion = "panelUbicacion1";
	}

}
