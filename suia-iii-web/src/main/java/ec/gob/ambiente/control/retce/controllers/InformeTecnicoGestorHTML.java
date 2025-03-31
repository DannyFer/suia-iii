package ec.gob.ambiente.control.retce.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.DetalleDescargasLiquidas;
import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.retce.model.InformeTecnicoRetce;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;


public class InformeTecnicoGestorHTML{
	private InformeTecnicoRetce informe;
	private OficioPronunciamientoRetce oficioAnterior;
	private GestorDesechosPeligrosos gestorDesechos;	
	private Usuario usuarioElabora,usuarioRevisa;
	private String operador,representanteLegal;	
	private List<ObservacionesFormularios> observacionesTecnicoList;
	
	public InformeTecnicoGestorHTML(){}
	
	public InformeTecnicoGestorHTML(InformeTecnicoRetce informe,GestorDesechosPeligrosos gestorDesechos,String operador,String representanteLegal,Usuario usuarioElabora,Usuario usuarioRevisa,OficioPronunciamientoRetce oficioAnterior,List<ObservacionesFormularios> observacionesTecnicoList){
		this.informe=informe;
		this.gestorDesechos=gestorDesechos;		
		this.operador=operador;
		this.representanteLegal=representanteLegal;
		this.usuarioElabora=usuarioElabora;
		this.usuarioRevisa=usuarioRevisa;
		this.oficioAnterior=oficioAnterior;	
		this.observacionesTecnicoList=observacionesTecnicoList;	
	}	
	
	public String getNumeroInforme() {
		return informe.getNumeroInforme();
	}
	
	public String getObservacionesGenerales() {
		return informe.getObservaciones();
	}
	
	public String getConclusiones() {
		return informe.getConclusiones();
	}
	
	public String getRecomendaciones() {
		return informe.getRecomendaciones();
	}
	
	public String getNombreElabora() {
		return usuarioElabora.getPersona().getNombre();
	}
	
	public String getNombreEvaluador() {
		return usuarioElabora.getPersona().getNombre();
	}
	
	public String getNombreRevisa() {
		return usuarioRevisa.getPersona().getNombre();
	}
	
	public String getRazonSocial() {
		return operador;
	}
	
	public String getRepresentanteLegal() {
		return representanteLegal;
	}
	
	public String getMostrarAntecedentesObservacion() {
		return oficioAnterior!=null?"":"overflow:hidden;height:0;";
	}
	
	public String getNumeroOficioAnterior() {
		return oficioAnterior!=null?oficioAnterior.getNumeroOficio():null;
	}
	
	public String getFechaOficioAnterior() {
		return oficioAnterior!=null?JsfUtil.getDateFormat(oficioAnterior.getFechaOficio()):null;
	}
	
	public String getNumeroResolucion() {
		return gestorDesechos.getInformacionProyecto().getCodigo();
	}
	
	public String getNombreProyecto() {
		return gestorDesechos.getInformacionProyecto().getNombreProyecto();
	}
	
	public String getFechaIngresoTramite() {		
		return JsfUtil.getDateFormat(gestorDesechos.getFechaModificacion());
	}
	
	public String getFechaEvaluacion() {
		return JsfUtil.getDateFormat(informe.getFechaModificacion()!=null?informe.getFechaModificacion():new Date());		
	}	
	
	public String getCodigoTramite() {
		return gestorDesechos.getCodigo();
	}
	
	public String getAnioReporte() {
		return gestorDesechos.getAnio().toString();
	}
	
	public String getNombreArea() {
		return gestorDesechos.getInformacionProyecto().getAreaSeguimiento().getAreaName();
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
