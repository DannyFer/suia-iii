package ec.gob.ambiente.control.retce.controllers;

import java.util.Date;

import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;


public class OficioPronunciamientoDescargasHTML{
	private OficioPronunciamientoRetce oficio;	
	private Usuario usuarioElabora,usuarioFirma;
	private String operador,representanteLegal;
	private Area area;
	
	public OficioPronunciamientoDescargasHTML(){}
	
	public OficioPronunciamientoDescargasHTML(OficioPronunciamientoRetce oficio,String operador,String representanteLegal,Usuario usuarioElabora,Usuario usuarioFirma, Area area)
	{
		this.oficio=oficio;		
		this.operador=operador;
		this.representanteLegal=representanteLegal;
		this.usuarioElabora=usuarioElabora;	
		this.usuarioFirma=usuarioFirma;	
		this.area=area;
	}
	
	public String getNumeroOficio() {
		if(oficio.getNumeroOficio()==null || usuarioFirma==null || !oficio.getNumeroOficio().contains(area.getAreaAbbreviation()))
			return ""; //"XXXX";
		else 
			return oficio.getNumeroOficio();
	}
	
	public String getAsunto() {
		return oficio.getAsunto();
	}
	
	public String getPronunciamiento() {
		return oficio.getPronunciamiento();
	}
	
	public String getRazonSocial() {
		return operador;
	}
	
	public String getRepresentanteLegal() {
		return representanteLegal;
	}
	
	public String getUbicacion() {
		return usuarioElabora.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
	}
	
	public String getFecha() {
		return JsfUtil.getDateFormat(oficio.getFechaModificacion()!=null?oficio.getFechaModificacion():new Date());
	}
	
	public String getNombreFirma() {
		return usuarioFirma!=null?usuarioFirma.getPersona().getNombre():"Nombre de Autoridad Ambiental";
	}
}
