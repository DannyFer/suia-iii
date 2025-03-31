package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.util.ArrayList;
import java.util.List;

import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeSnap;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeSnapEspecie;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Plantilla Html InformeSnap
 * @author carlos.guevara
 *
 */
public class InformeInspeccionSnapCAHtml {
	
	private CertificadoAmbientalInformeSnap informeSnap;	
	private String proponente;
	private List<CertificadoAmbientalInformeSnapEspecie> especiesLista;
	private String areaInterseca;
	private boolean intersecaEstatal;
	private Usuario usuarioElaboracion,usuarioAprobacion;
	
	public InformeInspeccionSnapCAHtml()
	{
		
	}
	
	public InformeInspeccionSnapCAHtml(CertificadoAmbientalInformeSnap informeSnap,List<CertificadoAmbientalInformeSnapEspecie> especiesLista,String proponente,String areaInterseca,boolean intersecaEstatal,Usuario usuarioElaboracion,Usuario usuarioAprobacion)
	{
		this.informeSnap=informeSnap;
		this.especiesLista=especiesLista;
		this.proponente=proponente;		
		this.areaInterseca=areaInterseca;
		this.intersecaEstatal=intersecaEstatal;				
		this.usuarioElaboracion=usuarioElaboracion;
		this.usuarioAprobacion=usuarioAprobacion;
	}
	
	public String getCodigoInforme()
	{
		return informeSnap.getCodigo();
	}
	
	public String getNombreProyecto()
	{
		return informeSnap.getProyecto().getNombre();
	}
	
	public String getNombreAreaResponsable()
	{
		return informeSnap.getProyecto().getAreaResponsable().getAreaName();
	}

	
	public String getNombreAreaProtegida()
	{
		return areaInterseca;
	}
	
	
	public String getEtiquetaLongitud()
	{
		return informeSnap.getUnidadLongitud()!=null && !informeSnap.getUnidadLongitud().isEmpty()?"Longitud":"";
	}
    
	public String getLongitud()
	{
		return informeSnap.getUnidadLongitud()!=null && !informeSnap.getUnidadLongitud().isEmpty()?informeSnap.getLongitud().toString():"";
	}
	
	public String getUnidadLongitud()
	{
		return informeSnap.getUnidadLongitud();
	}
	
	public String getEtiquetaSuperficie()
	{
		return informeSnap.getUnidadSuperficie()!=null && !informeSnap.getUnidadSuperficie().isEmpty()?"Superficie":"";
	}
	
	public String getSuperficie()
	{
		return informeSnap.getUnidadSuperficie()!=null && !informeSnap.getUnidadSuperficie().isEmpty()?informeSnap.getSuperficie().toString():"";
	}
	
	public String getUnidadSuperficie()
	{
		return informeSnap.getUnidadSuperficie();
	}
	
	public String getOtraExtension()
	{
		return informeSnap.getOtraExtension()!=null && !informeSnap.getOtraExtension().isEmpty()?informeSnap.getOtraExtension():"";
	}
	
	public String getValorOtraExtension()
	{
		return informeSnap.getOtraExtension()!=null && !informeSnap.getOtraExtension().isEmpty()?informeSnap.getValorOtraExtension().toString():"";
	}

	public String getUnidadOtraExtension()
	{
		return informeSnap.getOtraExtension()!=null && !informeSnap.getOtraExtension().isEmpty()?informeSnap.getUnidadOtraExtension():"";
	}

	public String getPuntaje()
	{
		return informeSnap.getPuntaje().toString();
	}
	
	public String getPronunciamientoFavorable()
	{
		return informeSnap.getPronunciamientoFavorable()?"SI":"NO";
	}
	
	public String getPronunciamientoAfecta()
	{
		return informeSnap.getPronunciamientoFavorable()?"NO":"SI";
	}
	
	public String getZonaProteccion()
	{
		return informeSnap.getZonaProteccion()?"SI":"NO";
	}
	
	public String getZonaUsoPublicaTurismo()
	{
		return informeSnap.getZonaUsoPublicaTurismo()?"SI":"NO";
	}
	
	public String getZonaRecuperacion()
	{
		return informeSnap.getZonaRecuperacion()?"SI":"NO";
	}
	
	public String getZonaUsoSostenible()
	{
		return informeSnap.getZonaUsoSostenible()?"SI":"NO";
	}
	
	public String getZonaManejoComunitario()
	{
		return informeSnap.getZonaManejoComunitario()?"SI":"NO";
	}
	
	public String getActividadPermitida()
	{
		return informeSnap.getActividadPermitida()?"SI":"NO";
	}

	public String getIntersecaRamsar()
	{
		return informeSnap.getIntersecaRamsar()?"SI":"NO";
	}
	
	public String getIntersecaRamsarDescripcion()
	{
		return informeSnap.getIntersecaRamsar()?informeSnap.getIntersecaRamsarDescripcion():"";
	}
	
	public String getTerritorioAnsestral()
	{
		return informeSnap.getTerritorioAnsestral()?"SI":"NO";
	}
	
	public String getTerritorioAnsestralDescripcion()
	{
		return informeSnap.getTerritorioAnsestral()?informeSnap.getTerritorioAnsestralDescripcion():"";
	}
	
	//obs: Vegeetacion tiene dos 'ee' para evitar que se reemplace la palabra 'get' al generar el html
	public String getEstadoVegeetacion()
	{
		return informeSnap.getEstadoVegetacion();
	}	
	
	public String getVegeetacionNativa()
	{
		return informeSnap.getVegetacionNativa()?"SI":"NO";
	}
	
	
	public String getAfectacionValoresConservacion()
	{
		return informeSnap.getAfectacionValoresConservacion()?"SI":"NO";
	}
	
	public String getAfectacionValoresConservacionDescripcion()
	{
		return informeSnap.getAfectacionValoresConservacion()?informeSnap.getAfectacionValoresConservacionDescripcion():"";
	}
	
	public String getAfectacionEspecies()
	{
		return informeSnap.getAfectacionEspecies()?"SI":"NO";
	}
	
	public String getAfectacionFuentesHidricas()
	{
		return informeSnap.getAfectacionFuentesHidricas()?"SI":"NO";
	}
	
	public String getAfectacionFuentesHidricasDescripcion()
	{
		return informeSnap.getAfectacionFuentesHidricas()?informeSnap.getAfectacionFuentesHidricasDescripcion():"";
	}
	
	public String getAfectacionServiciosEcosistemicos()
	{
		return informeSnap.getAfectacionServiciosEcosistemicos()?"SI":"NO";
	}
	
	public String getAfectacionServiciosEcosistemicosDescripcion()
	{
		return informeSnap.getAfectacionServiciosEcosistemicos()?informeSnap.getAfectacionServiciosEcosistemicosDescripcion():"";
	}
	
	public String getExisteProyectosInversion()
	{
		return informeSnap.getExisteProyectosInversion()?"SI":"NO";
	}
	
	public String getExisteProyectosInversionDescripcion()
	{
		return informeSnap.getExisteProyectosInversion()?informeSnap.getExisteProyectosInversionDescripcion():"";
	}
	
	public String getProponente()
	{
		return proponente;
	}
	
	public String getFechaElaboracion()
	{
		return JsfUtil.getDateFormat(informeSnap.getFechaModificacion()==null?informeSnap.getFechaCreacion():informeSnap.getFechaModificacion());		
	}
	
	public String getUsuarioElaboracion()
	{
		return usuarioElaboracion==null?"<br/><br/>":usuarioElaboracion.toString();
	}
	
	public String getUsuarioAprobacion()
	{
		return usuarioAprobacion.toString();
	}
	
	public String getCargoUsuarioElaboracion(){
		return intersecaEstatal?"Técnico y/o Guardaparque del área protegida":"Técnico Dirección Provincial";
	}
	
	public String getCargoUsuarioAprobacion(){
		return intersecaEstatal?"Jefe de Área":"Coordinador de patrimonio";
	}
		
	public String getEspeciesNativas() {
		return getEspecies("NATIVA");	
	}
	
	public String getEspeciesAmenazadas() {	
		return getEspecies("AMENAZADA");
	}
	
	private String getEspecies(String tipo) {
		List<CertificadoAmbientalInformeSnapEspecie> especies=new ArrayList<CertificadoAmbientalInformeSnapEspecie>();
		for (CertificadoAmbientalInformeSnapEspecie especie : especiesLista) {
			if(especie.getTipo().contains(tipo) && especie.getEstado())
				especies.add(especie);
		}
		
		if(especies.size()==0)
			return "";

		String strTable = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody><tr BGCOLOR=\"#B2E6FF\">"
				+ "<td colspan=\""+(tipo.contains("NATIVA")?2:7)+"\"><strong>DESCRIPCION DE LAS ESPECIES</strong></td></tr>"
				+ "<tr BGCOLOR=\"#B2E6FF\">";
		if(tipo.contains("NATIVA"))
		{
			strTable+= "<td><strong>Nombre Comun</strong></td>"
					 + "<td><strong>Nombre Cientifico</strong></td>";
		}
			
		if(tipo.contains("AMENAZADA"))
		{
			strTable+= "<td><strong>Especie</strong></td>"
					+ "<td><strong>Nombre Cientifico</strong></td>"
					+ "<td width=\"75px\"><strong>Amenaza Categoria del Libro Rojo</strong></td>"
					+ "<td width=\"75px\"><strong>Endemica</strong></td>"
					+ "<td width=\"75px\"><strong>Sensible</strong></td>"
					+ "<td width=\"75px\"><strong>Migratoria</strong></td>"
					+ "<td width=\"75px\"><strong>CITES</strong></td>";					
		}
		
		strTable += "</tr>";
		
		
		for (CertificadoAmbientalInformeSnapEspecie especie : especies) {
			strTable += "<tr>";
			strTable += "<td>" + especie.getNombreEspecie() + "</td>";
			strTable += "<td>" + especie.getNombreCientifico() + "</td>";
			if(tipo.contains("AMENAZADA"))
			{
				strTable += "<td>" + especie.getAmenazaCategoriaLibroRojo() + "</td>";
				strTable += "<td>" + especie.getEndemica() + "</td>";
				strTable += "<td>" + especie.getSensible() + "</td>";
				strTable += "<td>" + especie.getMigratoria() + "</td>";
				strTable += "<td>" + especie.getCites() + "</td>";
			}
			strTable += "</tr>";
		}
		
		
		strTable += "</tbody></table></center>";
		strTable = strTable.replace("&", "&amp;");
		return strTable;
	}
	
		
}