package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.util.Date;
import java.util.List;

import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.AnalisisTecnicoRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ObservacionesFormulariosRSQ;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Plantilla Html InformeTecnicoRSQHtml
 * @author carlos.guevara
 *
 */
public class InformeTecnicoRSQHtml {
	
	private InformeOficioRSQ informe;
	private Usuario usuarioElabora;
	private String proponente;
	private boolean pronunAprobado;
	
	private List<AnalisisTecnicoRSQ> analisisTecnicoRSQLista;
	private List<ActividadSustancia> actividadSustanciaLista;
	private List<ObservacionesFormulariosRSQ> observacionesRsqLista;
	private List<UbicacionesGeografica> ubicacionProyectoLista;
	
	public InformeTecnicoRSQHtml(){}
	
	public InformeTecnicoRSQHtml(InformeOficioRSQ informe,boolean pronunAprobado,List<AnalisisTecnicoRSQ> analisisTecnicoRSQLista,List<ActividadSustancia> actividadSustanciaLista,List<ObservacionesFormulariosRSQ> observacionesRsqLista,List<UbicacionesGeografica> ubicacionProyectoLista,String proponente,Usuario usuarioElabora)
	{
		this.informe=informe;
		this.pronunAprobado=pronunAprobado;		
		this.proponente=proponente;
		this.usuarioElabora=usuarioElabora;
		this.analisisTecnicoRSQLista=analisisTecnicoRSQLista;
		this.actividadSustanciaLista=actividadSustanciaLista;
		this.observacionesRsqLista=observacionesRsqLista;
		this.ubicacionProyectoLista=ubicacionProyectoLista;
	}
	
	public String getCodigoInforme()
	{
		return informe.getCodigo();
	}
	
	public String getNumeroTramite()
	{
		return informe.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getCodigoUnicoAmbiental();
	}	
	
	public String getRazonSocial()
	{
		return proponente;
	}
	
	public String getUsuarioElabora()
	{
		return usuarioElabora.toString();
	}
	
	public String getUbicacion()
	{
		String ret="";
		for (UbicacionesGeografica ubicacion : ubicacionProyectoLista) {
			ret+=ret.isEmpty()?"":", ";
			ret+=ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
			ret+="/"+ubicacion.getUbicacionesGeografica().getNombre();
			ret+="/"+ubicacion.getNombre();			
		}
		return ret;
	}
	
	public String getFechaEvaluacion()
	{
		return informe.getFechaEvaluacion()==null?JsfUtil.getDateFormat(new Date()):JsfUtil.getDateFormat(informe.getFechaEvaluacion());
	}
	
	public String getFechaIngresoTramite()
	{
		return JsfUtil.getDateFormat(informe.getRegistroSustanciaQuimica().getFechaModificacion());
	}	
	
	public String getTablaRequisitos() {
		String strTable = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody><tr BGCOLOR=\"#C5C5C5\">";
		strTable+= "<td width=\"50%\"><strong>Requisito</strong></td>";	
		if(!pronunAprobado) {
			strTable+= "<td width=\"10%\"><strong>Cumplimiento</strong></td>";	
			strTable+= "<td width=\"40%\"><strong>Observaciones</strong></td>";	
		}
		
		strTable += "</tr>";	
		
		for (AnalisisTecnicoRSQ item : analisisTecnicoRSQLista) {
			if(item.getHabilitado()) {
				strTable += "<tr>";
				strTable += "<td style=\"text-align:justify\">" + item.getHallazgo() + "</td>";
				if(!pronunAprobado) {
					strTable += "<td style=\"text-align:center\">" + (item.getTieneCumplimiento()!=null&&item.getTieneCumplimiento()?"Si":"No") + "</td>";
					strTable += "<td style=\"text-align:justify\">" + (item.getObservacion()!=null?item.getObservacion():"") + "</td>";
				}
				strTable += "</tr>";
			}
		}
		
		strTable += "</tbody></table></center>";		
		return strTable;
	}	
	
	public String getTablaActividades() {
		String strTable = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody>"
				+ "<tr BGCOLOR=\"#C5C5C5\">"
		  		+ "<td width=\"20%\" rowspan=\"2\"><strong>Sustancia Química</strong></td>"
				+ "<td width=\"20%\" rowspan=\"2\"><strong>Actividad</strong></td>"
				+ "<td width=\"40%\" colspan=\"2\"><strong>CUPO ASIGNADO</strong></td>"	
				+ "</tr>"
				+ "<tr BGCOLOR=\"#C5C5C5\">"		
				+ "<td width=\"20%\"><strong>Cantidad</strong></td>"
				+ "<td width=\"20%\"><strong>Unidad</strong></td>"
				+ "</tr>";	
		
		for (ActividadSustancia item : actividadSustanciaLista) {
			strTable += "<tr>";
			strTable += "<td>" + item.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion() + "</td>";
			strTable += "<td>" + item.getCaracteristicaActividad().getNombre()+ "</td>";
			strTable += "<td>" + item.getCupo() + "</td>";
			strTable += "<td>" + item.getUnidadMedida().getNombre() + "</td>";
			strTable += "</tr>";
		}
		
		strTable += "</tbody></table></center>";		
		return strTable;
	}
	
	public String getTablaObservaciones() {
		String strTable = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody>"
				+ "<tr BGCOLOR=\"#C5C5C5\">";
		strTable+= "<td width=\"20%\"><strong>Formulario</strong></td>"
				+ "<td width=\"20%\"><strong>Campo Observado</strong></td>"
				+ "<td width=\"60%\"><strong>Observación</strong></td>";	
		strTable += "</tr>";	
		
		for (ObservacionesFormulariosRSQ item : observacionesRsqLista) {
			strTable += "<tr>";
			strTable += "<td>" + item.getSeccionFormulario() + "</td>";
			strTable += "<td>" + item.getCampo()+ "</td>";
			strTable += "<td>" + item.getDescripcion() + "</td>";			
			strTable += "</tr>";
		}
		
		strTable += "</tbody></table></center>";		
		return strTable;
	}
}