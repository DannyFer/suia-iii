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
 *          [Autor: Carlos Pupo, Fecha: 14/01/2015]
 *          </p>
 */
public class Selectable<T> {

	@Getter
	@Setter
	private boolean selected;

	@Getter
	@Setter
	private T value;

	public Selectable(T value) {
		this.value = value;
	}

	public Selectable() {
	}
}
