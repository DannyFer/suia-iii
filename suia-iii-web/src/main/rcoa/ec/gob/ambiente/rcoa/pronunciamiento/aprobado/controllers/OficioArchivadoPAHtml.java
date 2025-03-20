package ec.gob.ambiente.rcoa.pronunciamiento.aprobado.controllers;

import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Plantilla Html OficioArchivadoPAHtml
 * @author carlos.guevara
 *
 */
public class OficioArchivadoPAHtml {
	
	private OficioPronunciamientoEsIA oficio;
	private Usuario usuarioElabora;
	private String proponente;
	private UbicacionesGeografica ubicacionesGeografica;
	private boolean intersecaProyecto;
	private Area area;
	private CertificadoInterseccionOficioCoa oficioCI;
	
	public OficioArchivadoPAHtml(){}
	
	public OficioArchivadoPAHtml(OficioPronunciamientoEsIA oficio,
			String proponente,UbicacionesGeografica ubicacionesGeografica,boolean intersecaProyecto,Usuario usuarioElabora, CertificadoInterseccionOficioCoa oficioCI){
		this.oficio=oficio;		
		this.proponente=proponente;
		this.ubicacionesGeografica=ubicacionesGeografica;
		this.intersecaProyecto=intersecaProyecto;
		this.usuarioElabora=usuarioElabora;	
		this.area=oficio.getInformacionProyecto().getProyectoLicenciaCoa().getAreaResponsable();
		this.oficioCI = oficioCI;
	}
	
	public String getCodigoOficio()
	{
		return oficioCI.getCodigo();
	}
	
	public String getRazonSocial()
	{
		return proponente;
	}
	
	public String getCodigoProyecto()
	{
		return oficio.getInformacionProyecto().getProyectoLicenciaCoa().getCodigoUnicoAmbiental();
	}
	
	public String getNombreProyecto()
	{
		return oficio.getInformacionProyecto().getProyectoLicenciaCoa().getNombreProyecto();
	}	
	
	public String getRucOperador()
	{
		return oficio.getInformacionProyecto().getProyectoLicenciaCoa().getUsuario().getNombre();		
	}
	
	public String getNombreRepresentanteLegal()
	{
		return oficio.getInformacionProyecto().getProyectoLicenciaCoa().getUsuario().getPersona().getNombre();
	}	
	
	public String getFechaOficio()
	{
		return (oficioCI.getFechaOficio() != null) ? JsfUtil.getDateFormat(oficioCI.getFechaOficio()) : JsfUtil.getDateFormat(oficioCI.getFechaCreacion());
	}
	
	public String getFechaRegistro()
	{
		return JsfUtil.getDateFormat(oficio.getInformacionProyecto().getProyectoLicenciaCoa().getFechaGeneracionCua());
	}
	
	public String getCiudad()
	{
		return usuarioElabora!=null?usuarioElabora.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre():null;
	}	
	
	public String getNombreAutoridad()
	{
		return usuarioElabora!=null?usuarioElabora.getPersona().getNombre():null;
	}
	
	public String getCargoAutoridad()
	{
		Area areaElabora = new Area();
		if(usuarioElabora != null && usuarioElabora.getListaAreaUsuario() != null && usuarioElabora.getListaAreaUsuario().size() == 1){
			areaElabora = usuarioElabora.getListaAreaUsuario().get(0).getArea();
		}else if(usuarioElabora != null){
			areaElabora = this.area;
		}
		
		return usuarioElabora!=null?areaElabora.getAreaName():null;
	}
	
	public String getParroquia()
	{
		return ubicacionesGeografica.getNombre();
	}
	
	public String getCanton()
	{
		return ubicacionesGeografica.getUbicacionesGeografica().getNombre();
	}
	
	public String getProvincia()
	{
		return ubicacionesGeografica.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
	}
	
	public String getInterseca()
	{
		return intersecaProyecto?"INTERSECA":"NO INTERSECA";
	}
	
	public Area getArea(){
		return oficio.getInformacionProyecto().getProyectoLicenciaCoa().getAreaResponsable();
	}
}