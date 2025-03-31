package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityInformePreliminar {
	
	@Getter
	@Setter
	private String codigo;
	
	@Getter
	@Setter
	private String sector;
	
	@Getter
	@Setter
	private String fechaRegistro;
	
	@Getter
	@Setter
	private String superficie;
	
	@Getter
	@Setter
	private String operador;	
	
	@Getter
	@Setter
	private String telefonoFijo;
	
	@Getter
	@Setter
	private String celular;
	
	@Getter
	@Setter
	private String email;
	
	@Getter
	@Setter
	private String enteResponsable;
	
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String resumenProyecto;
	
	@Getter
	@Setter
	private String actividades;
	
	@Getter
	@Setter
	private String magnitud;
	
	@Getter
	@Setter
	private String tipoTramite;
	
	@Getter
	@Setter
	private String ubicacionGeografica;
	
	@Getter
	@Setter
	private String displayDireccion;
	
	@Getter
	@Setter
	private String direccionProyecto;
	
	@Getter
	@Setter
	private String tipoZona;
	
	@Getter
	@Setter
	private String coordenadasGeograficas;
	
	@Getter
	@Setter
	private String coordenadasImplantacion;
	
	@Getter
	@Setter
	private String informacionProyecto;
	
	@Getter
	@Setter
	private String displaySustancias;

	@Getter
	@Setter
	private String sustanciasQuimicas;
	
	@Getter
	@Setter
	private String representanteLegal;
	
	@Getter
	@Setter
	private String displayOperador;
	
	@Getter
	@Setter
	private String impactoAmbiental;

}
