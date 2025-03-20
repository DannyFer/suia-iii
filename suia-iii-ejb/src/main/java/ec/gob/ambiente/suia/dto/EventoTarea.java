/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 11/03/2015]
 *          </p>
 */
public class EventoTarea {
	@Getter
	@Setter
	private long id;
	@Getter
	@Setter
	private long taskId;
	@Getter
	@Setter
	private String userId;
	@Getter
	@Setter
	private Date logTime;
	@Getter
	@Setter
	private String type;
}
