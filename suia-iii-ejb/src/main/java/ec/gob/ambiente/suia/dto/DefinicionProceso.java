/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 10/03/2015]
 *          </p>
 */
public class DefinicionProceso {
	
	@Getter
	@Setter
	private String id;
	
	@Getter
	@Setter
	private String name;
	
	@Getter
	@Setter
	private String version;
	
	@Getter
	@Setter
	private String packageName;
	
	@Getter
	@Setter
	private boolean seleccionado;
}
