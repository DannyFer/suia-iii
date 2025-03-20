package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityFirmaResponsabilidadPPC {
	
	@Getter
	@Setter
	private String ciudadInforme;
	
	@Getter
	@Setter
	private String fechaInforme;
	
	@Getter
	@Setter
	private String facilitador;
	
	@Getter
	@Setter
	private String cedulaFacilitador;
	
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String codigoProyecto;

	@Getter
	@Setter
	private String nombreFacilitador;

}
