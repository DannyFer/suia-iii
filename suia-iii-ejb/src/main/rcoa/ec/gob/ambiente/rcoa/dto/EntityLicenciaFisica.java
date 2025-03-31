package ec.gob.ambiente.rcoa.dto;

import lombok.Getter;
import lombok.Setter;

public class EntityLicenciaFisica {
	
	@Getter
	@Setter
	private Integer id;
	
	@Getter
	@Setter
	private String numeroResolucion;
	
	@Getter
	@Setter
	private String fechaEmision;
	
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String cedulaRucOperador;
	
	@Getter
	@Setter
	private String nombreOperador;
	
	@Getter
	@Setter
	private String fechaIngresoSistema;
	
	@Getter
	@Setter
	private boolean interseca;
	
	@Getter
	@Setter
	private String shape; //poligono linea punto
	
	@Getter
	@Setter
	private String sector;
	
	@Getter
	@Setter
	private String actividad;
	
	@Getter
	@Setter
	private String intersectaDescripcion;
	
	@Getter
	@Setter
	private Integer idProceso;
	

}
