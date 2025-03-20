package ec.gob.ambiente.prevencion.actualizacionCertInterseccion;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityActualizacionCertificadoInters {
	
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
	private String razonSocial;
	@Getter
	@Setter
	private String nombreOperador;
	@Getter
	@Setter
	private String nombreProyecto;
	@Getter
	@Setter
	private String ubicacionProyecto;
	@Getter
	@Setter
	private String codigoProyecto;
	@Getter
	@Setter
	private String areaResponsable;
	@Getter
	@Setter
	private String detalleIntersecaCapasViabilidad;
	@Getter
	@Setter
	private String detalleIntersecaOtrasCapas;
	@Getter
	@Setter
	private String cedulaOperador;
	@Getter
	@Setter
	private String actualizacionCapas;
	@Getter
	@Setter
	private String capasCONALI;
	@Getter
	@Setter
	private String mostarIntersecaViabilidad;
	@Getter
	@Setter
	private String mostarNoIntersecaViabilidad;
	@Getter
	@Setter
	private String mostarIntersecaOtrasCapas;
	@Getter
	@Setter
	private String nombreAutoridad;
	@Getter
	@Setter
	private String areaAutoridad;
	@Getter
	@Setter
	private String codigoQrFirma;
	
	public String getOperador() {
		String operador = nombreOperador;
		if(!razonSocial.equals(" "))
			operador = razonSocial;
		
		return operador;
	}
	
	public String getDisplayRazon() {
		String display = "none";
		if(!razonSocial.equals(" "))
			display = "inline";
		
		return display;
	}

}
