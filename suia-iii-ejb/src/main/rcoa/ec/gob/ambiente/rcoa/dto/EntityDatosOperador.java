package ec.gob.ambiente.rcoa.dto;

import java.io.Serializable;
import java.util.List;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import lombok.Getter;
import lombok.Setter;

public class EntityDatosOperador implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2749916373372822949L;

	@Getter
	@Setter
	private String nombre;

	@Getter
	@Setter
	private String representanteLegal;

	@Getter
	@Setter
	private String tipoPersona;  // N=Natural, J= Juridica

	@Getter
	@Setter
	private String correo;

	@Getter
	@Setter
	private String direccion;

	@Getter
	@Setter
	private String telefono;

	@Getter
	@Setter
	private String celular;

	@Getter
	@Setter
	private List<UbicacionesGeografica> listaUbicacionGeografica;

}
