package ec.gob.ambiente.control.retce.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.itextpdf.tool.xml.html.Image;

import ec.gob.ambiente.retce.model.CaracteristicasPuntoMonitoreoTabla;
import ec.gob.ambiente.retce.model.DatoObtenidoMedicionDescargas;
import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.DescargasLiquidas;
import ec.gob.ambiente.retce.model.DetalleDescargasLiquidas;
import ec.gob.ambiente.retce.model.InformeTecnicoRetce;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;


public class InformeTecnicoDescargasHTML{
	private InformeTecnicoRetce informe;
	private DescargasLiquidas descargasLiquidas;
	private List<DetalleDescargasLiquidas> detalleDescargasLiquidasList;
	private Usuario usuarioElabora,usuarioRevisa;
	private String operador,representanteLegal;
	private String numeroOficioAnterior;
	private String workspace;
	
	public InformeTecnicoDescargasHTML(){}
	
	public InformeTecnicoDescargasHTML(InformeTecnicoRetce informe,DescargasLiquidas descargasLiquidas,List<DetalleDescargasLiquidas> detalleDescargasLiquidasList,String operador,String representanteLegal,String numeroOficioAnterior,Usuario usuarioElabora,Usuario usuarioRevisa,String workspace)
	{
		this.informe=informe;
		this.descargasLiquidas=descargasLiquidas;
		this.detalleDescargasLiquidasList=detalleDescargasLiquidasList;
		this.operador=operador;
		this.representanteLegal=representanteLegal;
		this.usuarioElabora=usuarioElabora;
		this.usuarioRevisa=usuarioRevisa;
		this.numeroOficioAnterior=numeroOficioAnterior;
		this.workspace=workspace;
	}
	
	public String getNumeroInforme() {
		return informe.getNumeroInforme();
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
	
	public String getCodigoMonitoreo() {
		return descargasLiquidas.getCodigo();
	}
	
	public String getRazonSocial() {
		return operador;
	}
	
	public String getRepresentanteLegal() {
		return representanteLegal;
	}
	
	public String getNumeroOficioAnterior() {
		return numeroOficioAnterior;
	}
	
	public String getNumeroResolucion() {
		return descargasLiquidas.getInformacionProyecto().getCodigo();
	}
	
	public String getNombreProyecto() {
		return descargasLiquidas.getInformacionProyecto().getNombreProyecto();
	}
	
	public String getSector() {
		return descargasLiquidas.getInformacionProyecto().getTipoSector().getNombre();
	}
	
	public String getFase() {
		return descargasLiquidas.getInformacionProyecto().getFaseRetce()==null?"N/A":descargasLiquidas.getInformacionProyecto().getFaseRetce().getDescripcion();
	}
	
	public String getWorkspace() {
		return workspace;
	}
	public String getPeriodoMedicion() {
		String periodoMedicion ="";
		for (DetalleDescargasLiquidas detalleDescargasLiquidas : detalleDescargasLiquidasList) {
			periodoMedicion += (periodoMedicion.isEmpty()?"":"<br/>")+ "Desde "+detalleDescargasLiquidas.getFechaInicioMonitoreo()+" hasta "+detalleDescargasLiquidas.getFechaFinMonitoreo();
		}
		return periodoMedicion;
		//return "Desde "+descargasLiquidas.getFechaInicioMonitoreo()+" hasta "+descargasLiquidas.getFechaFinMonitoreo();
	}
	
	public String getFrecuenciaMonitoreo() {
		String frecuencia ="";
		for (DetalleDescargasLiquidas detalleDescargasLiquidas : detalleDescargasLiquidasList) {
			frecuencia += (frecuencia.isEmpty()?"":"<br/>")+ detalleDescargasLiquidas.getCatalogoFrecuenciaMonitoreo().getDescripcion();
		}
		return frecuencia;
		//return descargasLiquidas.getCatalogoFrecuenciaMonitoreo().getDescripcion();
	}
	
	public String getFechaIngresoTramite() {		
		return JsfUtil.getDateFormat(descargasLiquidas.getFechaModificacion());
	}
	
	public String getFechaEvaluacion() {
		return JsfUtil.getDateFormat(informe.getFechaModificacion()!=null?informe.getFechaModificacion():new Date());		
	}	
	
	public String getCodigoReferencia() {
		return descargasLiquidas.getCodigo();
	}
	
	public String getOcultarAntecedentesObservacion() {
		return numeroOficioAnterior!=null?"":"overflow:hidden;height:0;";
	}
	
	public String getTablaLaboratorios() {
		List<String> rucLabs=new ArrayList<String>();
		List<DatosLaboratorio> datosLaboratorioList=new ArrayList<DatosLaboratorio>();
		for (DetalleDescargasLiquidas detalle : detalleDescargasLiquidasList) {
			for (DatosLaboratorio laboratorio : detalle.getDatosLaboratoriosList()) {
				if(!rucLabs.contains(laboratorio.getRuc())){
					rucLabs.add(laboratorio.getRuc());
					datosLaboratorioList.add(laboratorio);
				}
			}
		}
		
		String strTable = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody>"
				+ "<tr BGCOLOR=\"#C5C5C5\">"
				+ "<td width=\"15%\"><strong>RUC</strong></td>"
				+ "<td width=\"50%\"><strong>Nombre o Razón Social</strong></td>"
				+ "<td width=\"20%\"><strong>Nro de Registro de SAE</strong></td>"
				+ "<td width=\"15%\"><strong>Vigencia del Registro</strong></td>";						
		strTable += "</tr>";	
		
		for (DatosLaboratorio laboratorio : datosLaboratorioList) {
			strTable += "<tr>";
			strTable += "<td>" + laboratorio.getRuc() + "</td>";
			strTable += "<td>" + laboratorio.getNombre() + "</td>";
			strTable += "<td>" + laboratorio.getNumeroRegistroSAE() + "</td>";
			strTable += "<td>" + JsfUtil.getDateFormat(laboratorio.getFechaVigenciaRegistro()) + "</td>";			
			strTable += "</tr>";
		}
		strTable += "</tbody></table></center>";		
		return strTable;
	}
	
	public String getTablaPuntosMonitoreo() {
		String strTable = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody>"
				+ "<tr BGCOLOR=\"#C5C5C5\">"
				+ "<td width=\"10%\"><strong>Código punto monitoreo</strong></td>"
				+ "<td width=\"30%\"><strong>Número de identificación del punto de monitoreo</strong></td>"
				+ "<td width=\"30%\"><strong>Nombre del lugar del punto de muestreo</strong></td>"
				+ "<td width=\"15%\"><strong>Tipo de cuerpo receptor</strong></td>"
				+ "<td width=\"15%\"><strong>Frecuencia de monitoreo</strong></td>";		
		strTable += "</tr>";	
		
		for (DetalleDescargasLiquidas detalle : detalleDescargasLiquidasList) {
			strTable += "<tr>";
			strTable += "<td>" + detalle.getCodigoPuntoMonitoreo() + "</td>";
			strTable += "<td>" + detalle.getNumeroPuntoMonitoreo() + "</td>";
			strTable += "<td>" + detalle.getLugarPuntoMonitoreo() + "</td>";
			if(detalle.getCatalogoTipoCuerpoReceptorCaracteristicasPunto().getCatalogoTipoCuerpoReceptor() != null )
				strTable += "<td>" + detalle.getCatalogoTipoCuerpoReceptorCaracteristicasPunto().getCatalogoTipoCuerpoReceptor().getDescripcion() + "</td>";
			else
				strTable += "<td>" + "" + "</td>";
			strTable += "<td>" + detalle.getCatalogoFrecuenciaMonitoreo().getDescripcion() + "</td>";
			strTable += "</tr>";
		}
		strTable += "</tbody></table></center>";	
		strTable +=imagenPuntosMonitoreo();
		return strTable;
	}
	
	public String getTablaDetallePuntosMonitoreo() {
		String strCabecera = "<strong>CARACTERÍSTICAS DEL PUNTO DE MUESTREO:</strong><br/>";		
		for (DetalleDescargasLiquidas detalle : detalleDescargasLiquidasList) {
			strCabecera +="<p><strong>Número de identificación del punto de monitoreo:</strong> "+detalle.getNumeroPuntoMonitoreo() + "<br/>";
			strCabecera +="<strong>Nombre del lugar del punto de muestreo:</strong> "+detalle.getLugarPuntoMonitoreo() + "<br/>";
			if(detalle.getCatalogoTipoCuerpoReceptorCaracteristicasPunto().getCatalogoTipoCuerpoReceptor() != null )
				strCabecera +="<strong>Tipo de cuerpo receptor:</strong> "+detalle.getCatalogoTipoCuerpoReceptorCaracteristicasPunto().getCatalogoTipoCuerpoReceptor().getDescripcion() + "</p>";
			else
				strCabecera +="<strong>Tipo de cuerpo receptor:</strong> "+ "" + "</p>";
			strCabecera +=tablaDatoObtenido(detalle);
		}		
		return strCabecera;
	}
	
	private String tablaDatoObtenido(DetalleDescargasLiquidas detalle) {
		List<CaracteristicasPuntoMonitoreoTabla> tablas=new ArrayList<CaracteristicasPuntoMonitoreoTabla>();
		
		for (DatoObtenidoMedicionDescargas datoObtenido : detalle.getDatoObtenidoMedicionDescargasList()) {
			if(!tablas.contains(datoObtenido.getParametrosTablas().getTabla())){
				tablas.add(datoObtenido.getParametrosTablas().getTabla());
			}
		}
		
		String strTable="";
		for (CaracteristicasPuntoMonitoreoTabla tabla : tablas) {
			strTable+= "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
					+ "<tbody>"
					+ "<tr BGCOLOR=\"#C5C5C5\"><td colspan=\"7\"><strong>"+tabla.getNombreTabla()+"</strong></td></tr>"
					+ "<tr BGCOLOR=\"#C5C5C5\">"
					+ "<td width=\"10%\"><strong>Parámetro</strong></td>"
					+ "<td width=\"10%\"><strong>Unidad</strong></td>"
					+ "<td width=\"10%\"><strong>Límite máximo permisible</strong></td>"
					+ "<td width=\"20%\"><strong>Aplicación</strong></td>"
					+ "<td width=\"20%\"><strong>Método de estimación</strong></td>"
					+ "<td width=\"15%\"><strong>Reporte</strong></td>"
					+ "<td width=\"15%\"><strong>Estado</strong></td>";
			strTable += "</tr>";
			
			for (DatoObtenidoMedicionDescargas datoObtenido : detalle.getDatoObtenidoMedicionDescargasList()) {
				
				if(datoObtenido.getParametrosTablas().getTabla().getId().intValue()==tabla.getId().intValue()){
					strTable += "<tr>";
					strTable += "<td>" + datoObtenido.getParametrosTablas().getParametro().getNombre() + "</td>";
					strTable += "<td>" + datoObtenido.getParametrosTablas().getParametro().getUnidad() + "</td>";
					strTable += "<td>" + (datoObtenido.getParametrosTablas().getLimiteMaximoPermisible()==null?"---":datoObtenido.getParametrosTablas().getLimiteMaximoPermisible().toString()) + "</td>";
					strTable += "<td>" + (datoObtenido.getParametrosTablas().getAplicacion()!= null ?datoObtenido.getParametrosTablas().getAplicacion() : "---") + "</td>";
					strTable += "<td>" + datoObtenido.getMetodoEstimacion().getDescripcion() + "</td>";
					strTable += "<td>" + datoObtenido.getValorIngresado() + "</td>";
					strTable += "<td>" + (datoObtenido.getValorIngresadoCorrecto()==null?"---":datoObtenido.getValorIngresadoCorrecto()?"CUMPLE":"NO CUMPLE") + "</td>";
					strTable += "</tr>";
				}
				
			}
			strTable += "</tbody></table></center><br/>";	
		}	
		return strTable;
	}
	public String imagenPuntosMonitoreo(){
		String strTable="";
		if(workspace!=null){
			strTable +="<strong>Ubicación de los puntos de monitoreo</strong><center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">";
			strTable += "<tr><td><img src='"+workspace+"' width='100%' height='100%' ></td></tr>";
			strTable += "</table></center><br/>";			
		}
		return strTable;
	}
}
