package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.util.List;

import ec.gob.ambiente.suia.domain.CertificadoAmbientalPronunciamiento;
import ec.gob.ambiente.suia.domain.DetalleInterseccionProyecto;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Plantilla Html PronunciamientoDirectorHtml
 * @author carlos.guevara
 *
 */
public class PronunciamientoDirectorHtml {
	
	private CertificadoAmbientalPronunciamiento pronunciamiento;
	private List<DetalleInterseccionProyecto> detalleInterseccion;
	private Usuario usuarioElabora;
	private String proponente;
	
	
	public PronunciamientoDirectorHtml()
	{
		
	}
	
	public PronunciamientoDirectorHtml(CertificadoAmbientalPronunciamiento pronunciamiento,List<DetalleInterseccionProyecto> detalleInterseccion,String proponente,Usuario usuarioElabora)
	{
		this.pronunciamiento=pronunciamiento;
		this.detalleInterseccion=detalleInterseccion;		
		this.proponente=proponente;
		this.usuarioElabora=usuarioElabora;		
	}
	
	public String getNombreProyecto()
	{
		return pronunciamiento.getProyecto().getNombre();
	}
	
	public String getActividadProyecto()
	{
		return pronunciamiento.getProyecto().getCatalogoCategoria().getDescripcion();
	}
	
	public String getNombreAreaProtegida()
	{
		String areaInterseca="";
		for (DetalleInterseccionProyecto detalle : detalleInterseccion) {			
			areaInterseca+= " "+detalle.getInterseccionProyecto().getDescripcion()+": "+detalle.getNombre()+(detalle.getCapaSubsistema()!=null?" "+detalle.getCapaSubsistema():"");
		}
		return areaInterseca;
	}
    
	public String getCodigoPronunciamiento()
	{
		return pronunciamiento.getCodigo();
	}
	
	public String getAreaResponsable()
	{
		return pronunciamiento.getProyecto().getAreaResponsable().getAreaName();
	}	

	
	public String getProponente()
	{
		return proponente;
	}	
	
	public String getDirector()
	{
		return usuarioElabora.toString();
	}
	
	public String getFechaElaboracion()
	{
		return JsfUtil.getDateFormat(pronunciamiento.getFechaModificacion()==null?pronunciamiento.getFechaCreacion():pronunciamiento.getFechaModificacion());
	}
	
	public String getLugarEmision()
	{
		return usuarioElabora.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
	}
	
	public String getPronunciamientoFavorable()
	{
		return pronunciamiento.getPronunciamientoFavorable()==null?"*****":pronunciamiento.getPronunciamientoFavorable()?"":" NO ";
	}
	
	public String getAfectacionSubsistema()
	{
		boolean esFaborable=pronunciamiento.getPronunciamientoFavorable()==null?true:pronunciamiento.getPronunciamientoFavorable();
		for (DetalleInterseccionProyecto detalle : detalleInterseccion) {			
			if(detalle.getCapaSubsistema()!=null && detalle.getCapaSubsistema().contains("ESTATAL"))
				return esFaborable?"No afectación a la funcionalidad del área y estar acorde al Plan de Manejo y zonificación del área protegida.":"Afectación a la funcionalidad del área y no estar acorde al Plan de Manejo y zonificación del área protegida.";
		}
		return esFaborable?"No afectación a la funcionalidad del área protegida.":"Afectación a la funcionalidad del área protegida.";
	}
		
}