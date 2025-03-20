package ec.gob.ambiente.rcoa.pronunciamiento.aprobado.controllers;

import java.util.Date;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Plantilla Html OficioArchivadoPAHtml
 * @author carlos.guevara
 *
 */
public class OficioObservadoPAHtml {
	
	private OficioPronunciamientoEsIA oficio;	
	private Usuario usuarioElabora;
	private String proponente;
	private Area area;
	
	public OficioObservadoPAHtml(){}
	
	public OficioObservadoPAHtml(OficioPronunciamientoEsIA oficio,
			String proponente,Usuario usuarioElabora){		
		this.oficio=oficio;	
		this.proponente=proponente;		
		this.usuarioElabora=usuarioElabora;	
		this.area=oficio.getInformacionProyecto().getProyectoLicenciaCoa().getAreaResponsable();
	}
	
	public String getCodigoOficio()
	{
		return oficio.getCodigoOficio();
	}
	
	public String getRazonSocial()
	{
		return proponente;
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
		return oficio.getFechaModificacion()==null?JsfUtil.getDateFormat(new Date()):JsfUtil.getDateFormat(oficio.getFechaModificacion());
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
	
	public String getAsuntoOficio(){
		return oficio.getAsunto();
	}
	
	public String getPronunciamiento(){
		return oficio.getPronunciamiento();
	}
	
	public Area getArea(){
		return oficio.getInformacionProyecto().getProyectoLicenciaCoa().getAreaResponsable();
	}
	
}