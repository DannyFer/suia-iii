package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.util.Date;
import java.util.List;

import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Plantilla Html OficioPronunciamientoRSQHtml
 * @author carlos.guevara
 *
 */
public class OficioPronunciamientoRSQHtml {
	
	private InformeOficioRSQ oficio,informe;
	private Usuario usuarioElabora,usuarioAutoridad;
	private String proponente;
	
	private List<ActividadSustancia> actividadSustanciaLista;
	
	public OficioPronunciamientoRSQHtml(){}
	
	public OficioPronunciamientoRSQHtml(InformeOficioRSQ oficio,InformeOficioRSQ informe,
			List<ActividadSustancia> actividadSustanciaLista,
			String proponente,
			Usuario usuarioAutoridad,Usuario usuarioElabora){
		this.oficio=oficio;
		this.informe=informe;
		this.proponente=proponente;
		this.usuarioElabora=usuarioElabora;
		this.usuarioAutoridad=usuarioAutoridad;
		this.actividadSustanciaLista=actividadSustanciaLista;
	}
	
	public String getCodigoOficio()
	{
		return oficio.getCodigo();
	}
	
	public String getRazonSocial()
	{
		return proponente;
	}
	
	public String getCodigoProyecto()
	{
		return oficio.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getCodigoUnicoAmbiental();
	}
	
	public String getNombreProyecto()
	{
		return oficio.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getNombreProyecto();
	}
	
	public String getCodigoRsq()
	{
		return oficio.getRegistroSustanciaQuimica().getNumeroAplicacion();
	}
	
	public String getRucOperador()
	{
		return oficio.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getUsuario().getNombre();
		
	}
	
	public String getNombreRepresentanteLegal()
	{
		return oficio.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getUsuario().getPersona().getNombre();
	}
	
	public String getUsuarioElabora()
	{
		return usuarioElabora.toString();
	}
	
	public String getFechaOficio()
	{
		return oficio.getFechaModificacion()==null?JsfUtil.getDateFormat(new Date()):JsfUtil.getDateFormat(oficio.getFechaModificacion());
	}
	
	public String getFechaRsq()
	{
		return JsfUtil.getDateFormat(new Date());
	}
	
	public String getCodigoInforme()
	{
		return informe.getCodigo();
	}
	
	public String getFechaInforme()
	{
		return JsfUtil.getDateFormat(informe.getFechaEvaluacion()!=null?informe.getFechaEvaluacion():new Date());
	}
	
	public String getNombreAutoridad()
	{
		return usuarioAutoridad.getPersona().getNombre();
	}
	
	public String getCargoAutoridad()
	{
		Area areaAutoridad = new Area();
		
		if(usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1){
			areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
		}else{
			areaAutoridad = informe.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getAreaResponsable().getArea();
		}		
		
		return areaAutoridad.getTipoArea().getId().intValue()==1?"SUBSECRETARÍA DE CALIDAD AMBIENTAL":"DIRECCIÓN ZONAL";
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
}