package ec.gob.ambiente.rcoa.certificado.interseccion;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.preliminar.contoller.GenerarQRCertificadoInterseccion;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

public class CertificadoInterseccionRcoaOficioHtml {
	
	private CertificadoInterseccionOficioCoa oficioCI;	
	private String nombreOperador,cedulaOperador,razonSocial,codigoCiiu,capasCONALI;	
	private Usuario usuarioAutoridad;
	private List<UbicacionesGeografica> ubicacionProyectoLista;	
	private List<CapasCoa> capasLista;
	
	private List<String> detalleIntersecaCapasViabilidad;
	private List<String> detalleIntersecaOtrasCapas;
	
	private Boolean generarQrValido;
		
	public CertificadoInterseccionRcoaOficioHtml(){}
	
	public CertificadoInterseccionRcoaOficioHtml(
			CertificadoInterseccionOficioCoa oficioCI,
			String nombreOperador,
			String cedulaOperador,
			String razonSocial,
			String codigoCiiu,
			String capasCONALI,
			Usuario usuarioAutoridad,
			List<UbicacionesGeografica> ubicacionProyectoLista,
			List<DetalleInterseccionProyectoAmbiental> detalleInterseccionLista,
			List<CapasCoa> capasLista){
		this.oficioCI=oficioCI;
		this.nombreOperador=nombreOperador;
		this.cedulaOperador=cedulaOperador;
		this.razonSocial=razonSocial;
		this.codigoCiiu=codigoCiiu;
		this.capasCONALI=capasCONALI;
		this.usuarioAutoridad=usuarioAutoridad;
		this.ubicacionProyectoLista=ubicacionProyectoLista;		
		this.capasLista=capasLista;
		
		detalleIntersecaCapasViabilidad=new ArrayList<String>();
		detalleIntersecaOtrasCapas=new ArrayList<String>();
		for (DetalleInterseccionProyectoAmbiental detalleInterseccion : detalleInterseccionLista) {
			String capaDetalle=detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getNombre()+": "+detalleInterseccion.getNombreGeometria();
			if(detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getViabilidad()){
				if(!detalleIntersecaCapasViabilidad.contains(capaDetalle))
					detalleIntersecaCapasViabilidad.add(capaDetalle);
			}else{				
				if(!detalleIntersecaOtrasCapas.contains(capaDetalle))
					detalleIntersecaOtrasCapas.add(capaDetalle);
			}
		}
	}
	
	public CertificadoInterseccionRcoaOficioHtml(
			CertificadoInterseccionOficioCoa oficioCI,
			String nombreOperador,
			String cedulaOperador,
			String razonSocial,
			String codigoCiiu,
			String capasCONALI,
			Usuario usuarioAutoridad, Boolean generarQrValido){
		this.oficioCI=oficioCI;
		this.nombreOperador=nombreOperador;
		this.cedulaOperador=cedulaOperador;
		this.razonSocial=razonSocial;
		this.codigoCiiu=codigoCiiu;
		this.capasCONALI=capasCONALI;
		this.usuarioAutoridad=usuarioAutoridad;
		this.generarQrValido = generarQrValido;
	}
	
	public String getNumeroOficio(){
		return oficioCI.getCodigo();
	}
	
	public String getUbicacion(){
		ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
		String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", oficioCI.getProyectoLicenciaCoa(), null, null);
		return sedeZonal;
		//return usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
	}
	
	public String getFechaEmision(){
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		return formatoFecha.format(new Date());
	}
	
	public String getNombreOperador(){
		return nombreOperador;
	}
	
	public String getCedulaOperador(){
		return cedulaOperador;
	}
	
	public String getCodigoCiiu(){
		return codigoCiiu;
	}
	
	public String getCapasCONALI(){
		return capasCONALI;
	}
	
	public String getRazonSocial(){
		return razonSocial;
	}
	
	public String getCodigoProyecto(){
		return oficioCI.getProyectoLicenciaCoa().getCodigoUnicoAmbiental();
	}
	
	public String getNombreProyecto(){
		return oficioCI.getProyectoLicenciaCoa().getNombreProyecto().toUpperCase();
	}	
	
	public String getAreaResponsable(){
		return oficioCI.getProyectoLicenciaCoa().getAreaResponsable().getAreaName();
	}	
	
	public String getNombreAutoridad(){
		return usuarioAutoridad.getPersona().getNombre();
	}
	
	public String getCedulaRepresentanteLegal(){
		return oficioCI.getEstado()?"SI":"NO";
	}
	
	public String getAutoridadEncargado(){
		return " ";//TODO
	}
	
	public String getOcultarAntecedentesObservacion() {
		return "";
	}
	
	public String getCategorizacionAmbiental(){
		
		switch (oficioCI.getProyectoLicenciaCoa().getCategorizacion()) {
		case 1:
			return "CERTIFICADO AMBIENTAL";
		case 2:
			return "REGISTRO AMBIENTAL";
		default:
			return "LICENCIA AMBIENTAL";
		}		
	}
	
	public String getTipoImpacto(){
		switch (oficioCI.getProyectoLicenciaCoa().getCategorizacion()) {
		case 1:
			return "NO SIGNIFICATIVO";
		case 2:
			return "BAJO";
		case 3:
			return "MEDIO";
		case 4:
			return "ALTO";
		default:
			return "BAJO";
		}
	}
	
	public String getUbicacionProyecto() {
		return oficioCI.getUbicacion();
	}
	
	public String getActualizacionCapas() {
		return oficioCI.getCapas();
	}
	
	public String getMostarNoIntersecaViabilidad() {
		return (oficioCI.getInterseccionViabilidad() == null || oficioCI.getInterseccionViabilidad().isEmpty()) ? "display: inline" : "display: none";
	}
	
	public String getMostarIntersecaViabilidad() {
		return (oficioCI.getInterseccionViabilidad() == null || oficioCI.getInterseccionViabilidad().isEmpty()) ? "display: none" : "display: inline";
	}
	
	public String getDetalleIntersecaCapasViabilidad(){
		return oficioCI.getInterseccionViabilidad();
	}
	
	public String getOperador() {
		String operador = nombreOperador;
		if(!razonSocial.equals(" "))
			operador = razonSocial;
		
		return operador;
	}
	
	public String getDisplayRazon() {
		String display = "none";
		if(!razonSocial.equals(" "))
			display = "inline";
		
		return display;
	}
	
	public String getCodigoQrUrl() {
		
		List<String> resultado = GenerarQRCertificadoInterseccion.getCodigoQrUrl(generarQrValido, 
						oficioCI.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
						oficioCI.getCodigo(), GenerarQRCertificadoInterseccion.tipo_suia_rcoa, oficioCI.getNroActualizacion());
		
		oficioCI.setUrlCodigoValidacion(resultado.get(0));

		return resultado.get(1);
	}
	
	public String getCodigoQrFirma() {
		return getCodigoQrUrl();
	}
}
