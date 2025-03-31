package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityOficioPronunciamiento {
	
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
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String actividadesCiiu;
	
	@Getter
	@Setter
	private String nombreAutoridad;
	
	@Getter
	@Setter
	private String areaResponsable;
	
	@Getter
	@Setter
	private String displayZonificacion;
	
	@Getter
	@Setter
	private String displayExtractiva;
	
}
