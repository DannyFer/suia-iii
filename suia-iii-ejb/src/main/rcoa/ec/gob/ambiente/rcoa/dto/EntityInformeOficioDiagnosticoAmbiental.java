package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityInformeOficioDiagnosticoAmbiental {
	
	@Getter
	@Setter
	private String numero;
	
	@Getter
	@Setter
	private String ciudad;
	
	@Getter
	@Setter
	private String fechaEmision;
	
	@Getter
	@Setter
	private String tratamientoOperador;
	
	@Getter
	@Setter
	private String nombreOperador;

	@Getter
	@Setter
	private String asunto;

	@Getter
	@Setter
	private String pronunciamiento;
	
	@Getter
	@Setter
	private String cargoOperador;
	
	@Getter
	@Setter
	private String nombreEmpresa;
	
	@Getter
	@Setter
	private String displayCargo;
	
	@Getter
	@Setter
	private String displayEmpresa;
	
	@Getter
	@Setter
	private String codigoProyecto;
	
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String fechaRegistro;
	
	@Getter
	@Setter
	private String fechaCoordenadas;
	
	@Getter
	@Setter
	private String nroOficioCertificado;
	
	@Getter
	@Setter
	private String fechaOficioCertificado;
	
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
	private String resultadoInterseccion;
	
	@Getter
	@Setter
	private String fechaIngresoAnalisis;
	
	@Getter
	@Setter
	private String diasNormativa;
	
	@Getter
	@Setter
	private String pronunciamientoTecnico;
	
	@Getter
	@Setter
	private String nombreResponsable;
	
	@Getter
	@Setter
	private String areaResponsable;
	
	@Getter
	@Setter
	private String siglasEnte;
	
}
