package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityCertificadoAmbiental {
	
	@Getter
	@Setter
	private String numeroResolucion;
	@Getter
	@Setter
	private String provincia;
	@Getter
	@Setter
	private String nombreproyecto;
	@Getter
	@Setter
	private String cantonproy;
	@Getter
	@Setter
	private String sectorActividad;
	
	@Getter
	@Setter
	private String sectorActividadSec1;
	@Getter
	@Setter
	private String sectorActividadSec2;

	@Getter
	@Setter
	private String codigoActividadCiuu;

	@Getter
	@Setter
	private String codigoActividadCiuuSec1;
	@Getter
	@Setter
	private String codigoActividadCiuuSec2;

	@Getter
	@Setter
	private String provinciaproy;
	@Getter
	@Setter
	private String promotor;
	@Getter
	@Setter
	private String registroambiental;
	@Getter
	@Setter
	private String sector;
	@Getter
	@Setter
	private String telefonopromotro;
	@Getter
	@Setter
	private String emailpromotor;
	@Getter
	@Setter
	private String codigoproy;
	@Getter
	@Setter
	private String coordenadas;	
	@Getter
	@Setter
	private String cantonpromotor;	
	@Getter
	@Setter
	private String provinciapromotor;
	@Getter
	@Setter
	private String parroquiaproy;	
	@Getter
	@Setter
	private String cedulausu;
	@Getter
	@Setter
	private String direccionpromotor;
	@Getter
	@Setter
	private String fechaactual;
	@Getter
	@Setter
	private String representanteLegal;
	@Getter
    @Setter
    private String nombreRepresentanteLegal;
	
	@Getter
	@Setter
	private String ubicacionCompleta;
	
	@Getter
	@Setter
	private String nombreDireccionProvincial;
	
	@Getter
	@Setter
	private String nombreAutoridad;
	
	@Getter
	@Setter
	private String displayDesechos = "none";
	@Getter
	@Setter
	private String infoDesechos;
	@Getter
	@Setter
	private String displayViabilidad  = "none";
	@Getter
	@Setter
	private String infoViabilidad;
	@Getter
	@Setter
	private String infoInventarioForestal;
	@Getter
	@Setter
	private String infoSustanciasQuimicas;
	@Getter
	@Setter
	private String codigoQrFirma;
	
}
