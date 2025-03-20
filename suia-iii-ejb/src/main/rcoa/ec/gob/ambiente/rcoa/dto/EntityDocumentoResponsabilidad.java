package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.Area;
import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityDocumentoResponsabilidad {
	
	@Getter
	@Setter
	private String fecha;
	
	@Getter
	@Setter
	private String operador;
	
	@Getter
	@Setter
	private String codigo;
	
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String codigoCiiu;
	
	@Getter
	@Setter
	private String nombreActividad;
	
	@Getter
	@Setter
	private Area area;
	
	@Getter
	@Setter
	private String nombreRepresentante;
	
	@Getter
	@Setter
	private String displayRepresentante;
	
	@Getter
	@Setter
	private String displayOperador;
	
}
