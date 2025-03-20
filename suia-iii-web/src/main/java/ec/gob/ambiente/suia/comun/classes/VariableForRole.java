/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comun.classes;

import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 13/01/2015]
 *          </p>
 */
public class VariableForRole {

	@Getter
	@Setter
	private String visibleName;

	@Getter
	@Setter
	private String role;

	@Getter
	@Setter
	private String area;

	public VariableForRole(String role, String area, String visibleName) {
		this.role = role;
		this.area = area;
		this.visibleName = visibleName;
	}

	public VariableForRole() {

	}
}
