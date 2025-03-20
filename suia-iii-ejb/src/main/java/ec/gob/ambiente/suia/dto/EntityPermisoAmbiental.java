package ec.gob.ambiente.suia.dto;

import lombok.Getter;
import lombok.Setter;

public class EntityPermisoAmbiental {
	
	@Getter
	@Setter
	private String txtEstadoProyecto;
	@Getter
	@Setter
	private String txtEstadoPagoProyecto;
	
	@Getter
	@Setter
	private String codigoProyecto;
	@Getter
	@Setter
	private String nombreProyecto;
	@Getter
	@Setter
	private String fechaProyecto;
	@Getter
	@Setter
	private String enteResponsable;
	@Getter
	@Setter
	private String proponente;
	@Getter
	@Setter
	private String nroOficioCI;
	@Getter
	@Setter
	private String fechaOficioCI;
	@Getter
	@Setter
	private String provinciaProyecto;
	@Getter
	@Setter
	private String comentarioInterseccion;
	
	@Getter
	@Setter
	private String fechaPago;
	
	@Getter
	@Setter
	private String nroOficioAprobacion;
	@Getter
	@Setter
	private String fechaOficioAprobacion;
	@Getter
	@Setter
	private String nroOficioObservacion;
	@Getter
	@Setter
	private String fechaOficioObservacion;
	@Getter
	@Setter
	private String fechaSubsanacion;
	@Getter
	@Setter
	private String urlFirmaEnte;
	@Getter
	@Setter
	private String nombreAutoridad;
	@Getter
	@Setter
	private String qrCode;
	@Getter
	@Setter
	private String qrCodeFirma;
	@Getter
	@Setter
	private String fechaEmision;
	
	

}
