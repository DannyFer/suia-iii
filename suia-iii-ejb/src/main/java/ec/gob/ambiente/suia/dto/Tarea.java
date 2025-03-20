package ec.gob.ambiente.suia.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author lili
 * 
 */
public class Tarea implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Long id;

	@Getter
	@Setter
	private String nombre;

	@Getter
	@Setter
	private String responsable;

	@Getter
	@Setter
	private String estado;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInicio;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaFin;
	
	@Getter
	@Setter
	private String usuarioId;
}