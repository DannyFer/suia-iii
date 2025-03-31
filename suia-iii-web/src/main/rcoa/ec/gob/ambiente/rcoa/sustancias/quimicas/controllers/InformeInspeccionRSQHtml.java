package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.util.Date;
import java.util.List;

import ec.gob.ambiente.rcoa.sustancias.quimicas.model.AnalisisTecnicoRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Plantilla Html InformeInspeccionRSQ
 * @author carlos.guevara
 *
 */
public class InformeInspeccionRSQHtml {
	
	private InformeOficioRSQ informe;
	private Usuario usuarioElabora;
	private String proponente;
	
	private List<AnalisisTecnicoRSQ> analisisTecnicoRSQLista;
	private List<UbicacionSustancia> ubicacionSustanciaLista;	
	private List<UbicacionesGeografica> ubicacionProyectoLista;
	
	public InformeInspeccionRSQHtml(){}
	
	public InformeInspeccionRSQHtml(InformeOficioRSQ informe,List<UbicacionSustancia> ubicacionSustanciaLista,List<AnalisisTecnicoRSQ> analisisTecnicoRSQLista,List<UbicacionesGeografica> ubicacionProyectoLista,String proponente,Usuario usuarioElabora)
	{
		this.informe=informe;
		this.ubicacionSustanciaLista=ubicacionSustanciaLista;
		this.proponente=proponente;
		this.usuarioElabora=usuarioElabora;
		this.analisisTecnicoRSQLista=analisisTecnicoRSQLista;
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
	
	public String getAprobadoObservado()
	{
		for (UbicacionSustancia item : ubicacionSustanciaLista) {
			if(item.getCumpleValor()==null || !item.getCumpleValor())
				return "OBSERVADO";
		}
		return "APROBADO";
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
		return JsfUtil.getDateFormat(informe.getRegistroSustanciaQuimica().getFechaCreacion());
	}
	
	public String getMostrarApoyo() {
		return informe.isInformeApoyo()?"":"overflow:hidden;height:0;";
	}
	
	public String getFechaPeticionApoyo()
	{
		return informe.isInformeApoyo()?JsfUtil.getDateFormat(ubicacionSustanciaLista.get(0).getFechaSolicitudApoyo()):"";
	}
	
	public String getDpApoyo()
	{
		return informe.isInformeApoyo()?informe.getArea().getAreaName():"";
	}
	
	public String getTablaHallazgos() {
		String strTable = "";
		for (UbicacionSustancia ubicacion : ubicacionSustanciaLista) {
			strTable+= "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
					+ "<tbody><tr BGCOLOR=\"#C5C5C5\">";
			strTable+= "<td width=\"20%\"><strong>Sustancia Química</strong></td>"
					+ "<td width=\"20%\"><strong>Lugar</strong></td>"
					+ "<td width=\"20%\"><strong>Provincia</strong></td>"
					+ "<td width=\"20%\"><strong>Canton</strong></td>"
					+ "<td width=\"20%\"><strong>Dirección</strong></td>";	
			strTable += "</tr>";
			
			strTable += "<tr>";
			strTable += "<td>" + ubicacion.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion() + "</td>";
			strTable += "<td>" + ubicacion.getLugares()+ "</td>";
			strTable += "<td>" + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td>";
			strTable += "<td>" + ubicacion.getUbicacionesGeografica().getNombre() + "</td>";
			strTable += "<td>" + ubicacion.getDireccion() + "</td>";
			strTable += "</tr>";
			
			strTable += "</tbody></table></center>";
			
			strTable += getTablaHallazgosPorUbicacion(ubicacion);
			
			strTable += "<br/>";
		}
						
		return strTable;
	}
	
	private String getTablaHallazgosPorUbicacion(UbicacionSustancia ubicacion) {
		String strTable = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody><tr BGCOLOR=\"#C5C5C5\">";
		strTable+= "<td width=\"50%\"><strong>Hallazgos</strong></td>"
				+ "<td width=\"50%\"><strong>Descripcion</strong></td>";	
		strTable += "</tr>";	
		
		for (AnalisisTecnicoRSQ item : analisisTecnicoRSQLista) {
			if(item.getHabilitado() && item.getUbicacionSustancia().getId().intValue()==ubicacion.getId().intValue()) {
				strTable += "<tr>";
				strTable += "<td style=\"text-align:justify\">" + item.getHallazgo() + "</td>";
				strTable += "<td style=\"text-align:justify\">" + (item.getHallazgoDescripcion()!=null?item.getHallazgoDescripcion():"")+ "</td>";
				strTable += "</tr>";
			}
			
		}
		
		strTable += "</tbody></table></center>";		
		return strTable;
	}
	
	public String getTablaUbicaciones() {
		String strTable = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody><tr BGCOLOR=\"#C5C5C5\">"
				+ "<td colspan=\"5\"><strong>PLANTAS, BODEGAS,SUCURSALES Y PROYECTOS</strong></td></tr>"
				+ "<tr BGCOLOR=\"#C5C5C5\">";
		strTable+= "<td width=\"20%\"><strong>Sustancia Química</strong></td>"
				+ "<td width=\"20%\"><strong>Lugar</strong></td>"
				+ "<td width=\"20%\"><strong>Provincia/Canton</strong></td>"
				+ "<td width=\"20%\"><strong>Cumple</strong></td>"
				+ "<td width=\"20%\"><strong>Observaciones</strong></td>";	
		strTable += "</tr>";	
		
		for (UbicacionSustancia ubicacion : ubicacionSustanciaLista) {
			strTable += "<tr>";
			strTable += "<td>" + ubicacion.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion() + "</td>";
			strTable += "<td>" + ubicacion.getLugares()+ "</td>";
			strTable += "<td>" + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre()+"/"+ubicacion.getUbicacionesGeografica().getNombre() + "</td>";
			strTable += "<td>" + (ubicacion.getCumpleValor()==null||ubicacion.getCumpleValor()==false?"No":"Si") + "</td>";
			strTable += "<td>" + (ubicacion.getObservacionesInforme()==null?" ":ubicacion.getObservacionesInforme()) + "</td>";
			strTable += "</tr>";
		}
		
		strTable += "</tbody></table></center>";		
		return strTable;
	}
}