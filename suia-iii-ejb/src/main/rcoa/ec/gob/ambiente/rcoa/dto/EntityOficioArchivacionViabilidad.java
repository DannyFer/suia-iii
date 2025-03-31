package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityOficioArchivacionViabilidad {
	
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
	private String codigoProyecto;
	
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String fechaRegistro;
	
	@Getter
	@Setter
	private String nroOficioCertificado;
	
	@Getter
	@Setter
	private String detalleInterseccionSnap;
	
	@Getter
	@Setter
	private String detalleInterseccionForestal;
	
	@Getter
	@Setter
	private String nroOficioViabilidadSnap;
	
	@Getter
	@Setter
	private String fechaOficioViabilidadSnap;
	
	@Getter
	@Setter
	private String nroOficioViabilidadForestal;
	
	@Getter
	@Setter
	private String fechaOficioViabilidadForestal;
	
	@Getter
	@Setter
	private String nombreAutoridad;
	
	@Getter
	@Setter
	private String areaResponsable;
	
	@Getter
	@Setter
	private String displaySnap = "none";
	
	@Getter
	@Setter
	private String displayForestal = "none";
	
}
