package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityPlanAccion {
	
	@Getter
	@Setter
	private String codigo;

	@Getter
	@Setter
	private String fechaRegistro;

	@Getter
	@Setter
	private String operador;

	@Getter
	@Setter
	private String enteResponsable;
	
	@Getter
	@Setter
	private String sector;
	
	@Getter
	@Setter
	private String superficie;
	
	@Getter
	@Setter
	private String provincia;
	
	@Getter
	@Setter
	private String canton;
	
	@Getter
	@Setter
	private String parroquia;
	
	@Getter
	@Setter
	private String ubicacion;
	
	@Getter
	@Setter
	private String tablaPlan;
	
	@Getter
	@Setter
	private String representanteLegal;
	
	@Getter
	@Setter
	private String cedula;

}
