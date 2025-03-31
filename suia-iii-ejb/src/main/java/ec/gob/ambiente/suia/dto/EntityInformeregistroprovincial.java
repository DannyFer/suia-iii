package ec.gob.ambiente.suia.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;
@Stateless
public class EntityInformeregistroprovincial {
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
	private String actividad;
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
	
}
