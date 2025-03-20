package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.CaracteristicaActividad;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Plantilla Html RegistroSustanciasQuimicasHtml
 * @author carlos.guevara
 *
 */
public class RegistroSustanciasQuimicasHtml {
	
	private InformeOficioRSQ informe, oficio;
	private Usuario usuarioAutoridad;
	private String proponente,codigoOficio;
	@Getter
	@Setter
	private String nroAutorizacion, fechaAutorizacion;
	
	private List<ActividadSustancia> actividadSustanciaLista;
	private List<UbicacionSustancia> ubicacionSustanciaLista;
	private String vigenciaDesde;
	private String vigenciaHasta;
	private String actividades;
	private List<ActividadSustancia> actividadesLista;
	
	public RegistroSustanciasQuimicasHtml(){}
	
	public RegistroSustanciasQuimicasHtml(InformeOficioRSQ informe,String codigoOficio,
			List<ActividadSustancia> actividadSustanciaLista,
			List<UbicacionSustancia> ubicacionSustanciaLista,
			String proponente,
			Usuario usuarioAutoridad, List<ActividadSustancia> actividadesLista){		
		this.informe=informe;
		this.codigoOficio=codigoOficio;
		this.proponente=proponente;
		this.usuarioAutoridad=usuarioAutoridad;
		this.actividadSustanciaLista=actividadSustanciaLista;
		this.ubicacionSustanciaLista=ubicacionSustanciaLista;
		this.actividadesLista=actividadesLista;
	}
	
	public RegistroSustanciasQuimicasHtml(InformeOficioRSQ informe,InformeOficioRSQ oficio,
			List<ActividadSustancia> actividadSustanciaLista,
			List<UbicacionSustancia> ubicacionSustanciaLista,
			String proponente,
			Usuario usuarioAutoridad, String nroResolucion, Date fechaResolucion){		
		this.informe=informe;
		this.oficio=oficio;
		this.codigoOficio=oficio.getCodigo();
		this.proponente=proponente;
		this.usuarioAutoridad=usuarioAutoridad;
		this.actividadSustanciaLista=actividadSustanciaLista;
		this.ubicacionSustanciaLista=ubicacionSustanciaLista;
		this.nroAutorizacion = nroResolucion;
		this.fechaAutorizacion = JsfUtil.devuelveFechaEnLetras(fechaResolucion);
	}	
	
	public String getRazonSocial()
	{
		return proponente;
	}	
	
	public String getCodigoRsq()
	{
		return informe.getRegistroSustanciaQuimica().getNumeroAplicacion();
	}
	
	public String getRucOperador()
	{
		return informe.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getUsuario().getNombre();
		
	}
	
	public String getNombreRepresentanteLegal()
	{
		return informe.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getUsuario().getPersona().getNombre();
	}
	
	public String getFechaRsq()
	{
		return JsfUtil.devuelveFechaEnLetras(new Date());
	}
	
	private Date fechaInicioVigencia() {
		return informe.getRegistroSustanciaQuimica().getVigenciaDesde()!=null?informe.getRegistroSustanciaQuimica().getVigenciaDesde():new Date();
	}
	
	public String getVigenciaInicio()
	{
		return JsfUtil.getDateFormat(fechaInicioVigencia());
	}
	
	public String getVigenciaFin()
	{
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(fechaInicioVigencia());
		calendar.add(Calendar.YEAR, 2);
		return JsfUtil.getDateFormat(calendar.getTime());
	}
	
	public String getCodigoInforme()
	{
		return informe.getCodigo();
	}
	
	public String getCodigoOficio()
	{
		return codigoOficio;
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
	
	public String getTablaUbicaciones() {
		String strTable = "";
		if(!ubicacionSustanciaLista.isEmpty()){
			strTable = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
					+ "<tbody>"
					+ "<tr BGCOLOR=\"#C5C5C5\">";
			strTable+= "<td width=\"20%\"><strong>Sustancia Química</strong></td>"
					+ "<td width=\"20%\"><strong>Lugar</strong></td>"
					+ "<td width=\"20%\"><strong>Provincia</strong></td>"
					+ "<td width=\"20%\"><strong>Cantón</strong></td>";	
			strTable += "</tr>";	
			
			for (UbicacionSustancia ubicacion : ubicacionSustanciaLista) {
				strTable += "<tr>";
				strTable += "<td>" + ubicacion.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion() + "</td>";
				strTable += "<td>" + ubicacion.getLugares()+ "</td>";
				strTable += "<td>" + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre()+ "</td>";
				strTable += "<td>" + ubicacion.getUbicacionesGeografica().getNombre() + "</td>";
				
				strTable += "</tr>";
			}
			
			strTable += "</tbody></table></center>";	
		}
			
		return strTable;
	}
	
	public String getFechaOficio() {
		return JsfUtil.devuelveFechaEnLetras(this.oficio.getFechaCreacion());
	}
	
	public String getTipoAutorizacion() {
		return informe.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getCategoria().getNombrePublico();
	}
	
	public String getFechaInicio() {
		return JsfUtil.devuelveFechaEnLetras(new Date());
	}
	
	public String getFechaFin() throws ParseException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date fechaFinal = new Date();		
		Calendar fechaPrueba = Calendar.getInstance();
		fechaPrueba.setTime(fechaFinal);
		
		Integer anio = fechaPrueba.get(Calendar.YEAR);
		Integer mes = 12;
		Integer dia = 31;
	
		String fechaFeriado = anio.toString() + "-" + mes.toString() + "-"+dia.toString();
		Date fechaComparar = format.parse(fechaFeriado);
		
		return JsfUtil.devuelveFechaEnLetras(fechaComparar);
	}
	
	public String getVigenciaDesde(){
		if(informe.getRegistroSustanciaQuimica().getVigenciaDesde() != null){
			return JsfUtil.devuelveFechaEnLetras(informe.getRegistroSustanciaQuimica().getVigenciaDesde());
		}else
			return null;
	}
	
	public String getVigenciaHasta(){
		if(informe.getRegistroSustanciaQuimica().getVigenciaDesde() != null){
			return JsfUtil.devuelveFechaEnLetras(informe.getRegistroSustanciaQuimica().getVigenciaHasta());
		}else
			return null;
	}
	
	public String getActividades() {
		String actividad = "";
		List<CaracteristicaActividad> lista = new ArrayList<CaracteristicaActividad>();
		for (ActividadSustancia item : actividadesLista) {
			if(item.getActividadSeleccionada() && (item.getCaracteristicaActividad().getActividadNivel().getNivel().equals(1) || 
					item.getCaracteristicaActividad().getActividadNivel().getNivel().equals(2))){
				if(lista.isEmpty()){
					lista.add(item.getCaracteristicaActividad());
				}else{
					if(!lista.contains(item.getCaracteristicaActividad())){
						lista.add(item.getCaracteristicaActividad());
					}
				}
			}					
		}
		
		for (CaracteristicaActividad item : lista) {
			String itemActividad = "";
			String nombre = item.getNombre();
			String[] dato =nombre.split("\\.");
			if(dato.length>1){
				itemActividad = "-" + dato[1];
			}else
				itemActividad = item.getNombre();
			
			actividad += itemActividad + "<br/>";	
		}
				
		return actividad;
	}
}