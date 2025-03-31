package ec.gob.ambiente.suia.dto;

import lombok.Getter;
import lombok.Setter;
public class EntityResolucion020 {
	
	@Getter
	@Setter
	private String numeroResolucion;
	
	@Getter
	@Setter
	private String numero_certificado_interseccion;
	
	@Getter
	@Setter
	private String fecha;
	
	@Getter
	@Setter
	private String fecha_certificado_interseccion;
	
	
	@Getter
	@Setter
	private String consecionMinera;
	
	@Getter
	@Setter
	private String interseca;
	
	@Getter
	@Setter
	private String coordenadasMineria;

	@Getter
	@Setter
	private String nombre_concesion;
		
	@Getter
	@Setter
	private String codigo_proyecto;
	
	@Getter
	@Setter
	private String provincias;
	
	@Getter
	@Setter
	private String titular_minero;
		
	@Getter
	@Setter
	private String cantones;
	
	@Getter
	@Setter
	private String canton;
	
	@Getter
	@Setter
	private String UbicacionFirma;
	
	@Getter
	@Setter
	private String fechaCompleta;
	
	@Getter
	@Setter
	private String subsecretario;
	
	@Getter
	@Setter
	private String cedula_titular_minero;
	
	@Getter	
	private String textoRegistroActualizacion;
	
	public void setTextoRegistroActualizacion(boolean actualiza){
		if(actualiza)
			textoRegistroActualizacion="REGISTRO AMBIENTAL DE LA ACTUALIZACIÓN DEL REGISTRO AMBIENTAL No. "+codigo_proyecto+ "PARA LA FASE DE EXPLORACIÓN INICIAL DE LA CONCESIÓN MINERA ";
		else
			textoRegistroActualizacion="REGISTRO AMBIENTAL PARA LA FASE DE EXPLORACIÓN INICIAL CON SONDEOS DE PRUEBA O RECONOCIMIENTO DE LA CONCESIÓN MINERA ";
			
		textoRegistroActualizacion+=nombre_concesion+" " +codigo_proyecto+", UBICADA EN LA PROVINCIA/PROVINCIAS "+provincias+" FAVOR DE "+titular_minero+".";
	}
}
