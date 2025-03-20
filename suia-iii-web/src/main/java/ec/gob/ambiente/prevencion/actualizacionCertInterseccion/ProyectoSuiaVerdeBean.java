package ec.gob.ambiente.prevencion.actualizacionCertInterseccion;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import ec.gob.ambiente.suia.utils.Constantes;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@SessionScoped
public class ProyectoSuiaVerdeBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9171477865659294613L;

	@Getter
	@Setter
	private String codigo;
	
	@Getter
	@Setter
	private String nombre;
	
	@Getter
	@Setter
	private String resumen;
	
	@Getter
	@Setter
	private Date fechaRegistro;
	
	@Getter
	@Setter
	private String proponente;
	
	@Getter
	@Setter
	private String actividad;
	
	@Getter
	@Setter
	private Object[] proyecto;
	
	@Getter
	@Setter
	public boolean esProyectoFinalizado = false;
	
	@Getter
	@Setter
	public boolean proyectoRequiereActualizacionCertInterseccion;
	
	@Getter
	@Setter
	public boolean esActualizacionCertInterseccion = false;
	
	@Getter
	@Setter
	public boolean esSoloActualizacionCI = false;
	
	@Getter
	@Setter
	public boolean solicitarActualizacionCertificado = false;
	
	@Getter
	@Setter
	public boolean showModalCertificadoInterseccion;
	
	@Getter
	@Setter
	public boolean showMensajeErrorGeneracionDocumentos;
	
	@Getter
	@Setter
	public boolean showModalErrorProcesoInterseccion;
	
	@Getter
	@Setter
	private String mensaje;

	@Getter
	@Setter
	private String pathImagen;
	
	@Getter
	@Setter
	public boolean esCertificadoActualizado = false;
	
}
