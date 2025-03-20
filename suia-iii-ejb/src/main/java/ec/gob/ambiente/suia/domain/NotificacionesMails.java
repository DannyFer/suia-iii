/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author christian
 */
public class NotificacionesMails implements Serializable {

	private static final long serialVersionUID = 585048864660716133L;
	
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	private String asunto;

	@Getter
	@Setter
	private String contenido;

	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private String observacion;

	@Getter
	@Setter
	private Date fechaObservacion;

	@Getter
	@Setter
	private String tipoMensaje;
	
	@Getter
	@Setter
	private String adjuntos;
	
	public NotificacionesMails() {
	}

	public NotificacionesMails(Integer id) {
		this.id = id;
	}
}
