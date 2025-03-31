package ec.gob.ambiente.control.retce.controllers;

import java.util.Date;
import java.util.List;

import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.retce.model.InformeTecnicoRetce;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;


public class OficioPronunciamientoGestorHTML{
	private OficioPronunciamientoRetce oficio;
	private InformeTecnicoRetce informe;
	private GestorDesechosPeligrosos gestorDesechos;
	private Usuario usuarioElabora,usuarioFirma;
	private String operador,representanteLegal;
	private List<ObservacionesFormularios> observacionesTecnicoList;
	
	public OficioPronunciamientoGestorHTML(){}
	
	public OficioPronunciamientoGestorHTML(OficioPronunciamientoRetce oficio,InformeTecnicoRetce informe,GestorDesechosPeligrosos gestorDesechos,String operador,String representanteLegal,Usuario usuarioElabora,Usuario usuarioFirma,List<ObservacionesFormularios> observacionesTecnicoList)
	{
		this.oficio=oficio;
		this.informe=informe;
		this.gestorDesechos=gestorDesechos;
		this.operador=operador;
		this.representanteLegal=representanteLegal;
		this.usuarioElabora=usuarioElabora;	
		this.usuarioFirma=usuarioFirma;
		this.observacionesTecnicoList=observacionesTecnicoList;
	}
	
	public String getMostrarAprobacion() {
		return informe.getEsReporteAprobacion()?"":"overflow:hidden;height:0;";
	}
	
	public String getMostrarObservacion() {
		return !informe.getEsReporteAprobacion()?"":"overflow:hidden;height:0;";
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
	
	public String getFechaOficio() {
		return JsfUtil.getDateFormat(oficio.getFechaModificacion()!=null?oficio.getFechaModificacion():new Date());
	}
	
	public String getNumeroInforme() {
		return informe.getNumeroInforme();
	}
	
	public String getFechaInforme() {		
		return JsfUtil.getDateFormat(informe.getFechaModificacion()!=null?informe.getFechaModificacion():new Date());
	}
	
	public String getCodigoProyecto() {
		return gestorDesechos.getInformacionProyecto().getCodigo();
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
	
	public String getNombreFirma() {
		return usuarioFirma!=null?usuarioFirma.getPersona().getNombre():"Nombre de Autoridad Ambiental";
	}
	
	public String getObservaciones() {	
		String strTable = "";
		for (ObservacionesFormularios observacion : observacionesTecnicoList) {
			strTable += "<strong>"+observacion.getCampo()+"</strong><br/>";
			strTable += observacion.getDescripcion()+"<br/><br/>";
		}				
		return strTable;
	}
}
