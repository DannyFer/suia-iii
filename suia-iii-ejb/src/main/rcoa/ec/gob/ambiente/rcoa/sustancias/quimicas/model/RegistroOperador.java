package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Usuario;

public class RegistroOperador {

	@Getter
	@Setter
	private RegistroSustanciaQuimica registro;

	@Getter
	@Setter
	private Usuario usuario;

	@Getter
	@Setter
	private String nombreOperador;
	
	@Getter
	@Setter
	private Integer idRegistro;
	
	@Getter
	@Setter
	private String codigoRSQ;
	
	@Getter
	@Setter
	private Integer idUsuario;
	
	
	public RegistroOperador() {
		
	}
	
	public RegistroOperador(Object[] array) {
		
		this.idRegistro = (Integer)array[0];
		this.codigoRSQ = (String)array[1];
		
		if(array[3] == null && array[4] == null){
			nombreOperador = (String)array[2];
		}else{
			if(array[4] == null){
				nombreOperador = (String)array[3];
			}else{
				nombreOperador = (String)array[4];
			}
		}	
		
		if(array[6] != null){
			this.idUsuario = (Integer)array[6];
		}
	}
}
