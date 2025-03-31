package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityOficioArchivacionEsIA {
	
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
	private String detalleInterseccion;
	
	@Getter
	@Setter
	private String mostrarDetalleInterseccion;
	
	@Getter
	@Setter
	private String fechaIngresoEstudio;
	
	@Getter
	@Setter
	private String nombreAutoridad;
	
	@Getter
	@Setter
	private String areaResponsable;
	
	@Getter
	@Setter
	private String siglasEnte;
	
	@Getter
	@Setter
	private String mostrarArchivoPorPago;
	
	@Getter
	@Setter
	private String mostrarArchivoPorTiempo;
	
	@Getter
	@Setter
	private String fechaOficioObs1;
	
	@Getter
	@Setter
	private String codigoOficioObs1;
	
	@Getter
	@Setter
	private String fechaOficioObs2;
	
	@Getter
	@Setter
	private String codigoOficioObs2;
	
	@Getter
	@Setter
	private String fechaOficioObs3;
	
	@Getter
	@Setter
	private String codigoOficioObs3;
	
	@Getter
	@Setter
	private String mostrarArchivoPorTiempoObs2;
	
	@Getter
	@Setter
	private String mostrarArchivoPorTiempoObs3;
	
	@Getter
	@Setter
	private String fechaIngresoEstudio2;
	
	@Getter
	@Setter
	private String fechaIngresoEstudio3;
}
