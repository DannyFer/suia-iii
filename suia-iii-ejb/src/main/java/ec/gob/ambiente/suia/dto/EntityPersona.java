package ec.gob.ambiente.suia.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class EntityPersona implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2749916373372822949L;

	@Getter
	@Setter
	private String nombre;

	@Getter
	@Setter
	private String pin;

	@Getter
	@Setter
	private String trato;

}
