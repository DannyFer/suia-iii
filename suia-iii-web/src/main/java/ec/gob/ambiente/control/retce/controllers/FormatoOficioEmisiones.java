package ec.gob.ambiente.control.retce.controllers;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

public class FormatoOficioEmisiones {
	
	private OficioPronunciamientoRetce oficio;
	private EmisionesAtmosfericas emisionAtmosferica;
	private Usuario usuarioElabora;
	private String operador;

	private String ubicacion;
	
	@Getter
	@Setter	
	private String razonSocial,representanteLegal, nombreFirma;

	
	public FormatoOficioEmisiones(OficioPronunciamientoRetce oficio,
			EmisionesAtmosfericas emisionAtmosferica, Usuario usuarioElabora,
			String operador) {
		
		this.oficio = oficio;
		this.emisionAtmosferica = emisionAtmosferica;
		this.usuarioElabora = usuarioElabora;
		this.operador = operador;
	}


	public FormatoOficioEmisiones() {
	}
	
	public String getNumeroOficio() {
		return oficio.getNumeroOficio();
	}
	
	public String getAsunto() {
		return oficio.getAsunto();
	}
	
	public String getPronunciamiento() {
		return oficio.getPronunciamiento();
	}
	
	public String getFecha(){
		return JsfUtil.devuelveFechaEnLetrasSinHora(oficio.getFechaOficio());
	}
	
	public void setUbicacion(String ubicacion){
		this.ubicacion = ubicacion;
	}
	
	public String getUbicacion(){
		return ubicacion;
	}
	
}
