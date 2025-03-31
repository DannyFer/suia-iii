package ec.gob.ambiente.suia.recaudaciones.bean;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditCard implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String token_data;
	private String nombre;
	private String apellido;
	private String documento;
	private String mensaje;
	private String tipo;
	private String idSubscripcion;
	private String numTicket;
	private String parametro;

}
