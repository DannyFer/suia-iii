package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityOficioArchivoRAPPC {
	
	@Getter
	@Setter
	private String numeroOficio;
	
	@Getter
	@Setter
	private String ubicacion;
	
	@Getter
	@Setter
	private String fechaEmision;
	
	@Getter
	@Setter
	private String nombreOperador;
	
	@Getter
	@Setter
	private String asunto;
	
	@Getter
	@Setter
	private String antecedentes;
	
	@Getter
	@Setter
	private String pronunciamiento;
	
	@Getter
	@Setter
	private String nombreAutoridad;
	
	@Getter
	@Setter
	private String areaAutoridad;

}
