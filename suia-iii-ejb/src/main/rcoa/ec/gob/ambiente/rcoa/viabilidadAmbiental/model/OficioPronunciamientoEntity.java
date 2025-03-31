package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import lombok.Getter;
import lombok.Setter;

public class OficioPronunciamientoEntity {
	@Getter
	@Setter
	private String provincia;
	
	@Getter
	@Setter
	private String ciudad;
	
	@Getter
	@Setter
	private String fechaOficio;
	
	@Getter
	@Setter
	private String nroOficio;
	
	@Getter
	@Setter
	private String enteResponsable;
	
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String ubicacionAreas;
	
	@Getter
	@Setter
	private String nroInforme;
	
	@Getter
	@Setter
	private String parametrosTecnicos;
	
	@Getter
	@Setter
	private String recomendaciones;
	
	@Getter
	@Setter
	private String nombreAutoridad;
	
	@Getter
	@Setter
	private String cargoAutoridad;
	
	@Getter
	@Setter
	private String razonSocial;
	
	//FORESTAL
	@Getter
	@Setter
	private String proponente;
	@Getter
	@Setter
	private String asunto;
	@Getter
	@Setter
	private String antecedente;
	@Getter
	@Setter
	private String marcoLegal;
	@Getter
	@Setter
	private String pronunciamiento;
	@Getter
	@Setter
	private String conclusiones;
	@Getter
	@Setter
	private String nombreDestinatario;
	@Getter
	@Setter
	private String siglasElabora;	
}
