package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.util.List;

import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeForestal;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeForestalFormaCoordenada;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Plantilla Html InformeForestal
 * @author carlos.guevara
 *
 */
public class InformeInspeccionForestalCAHtml {
	
	private CertificadoAmbientalInformeForestal informeForestal;
	private Usuario usuarioElabora,usuarioDirector;
	private String proponente;
	
	private List<CertificadoAmbientalInformeForestalFormaCoordenada> listaCoordenadas;
	private List<ProyectoUbicacionGeografica> ubicaciones;
	
	
	public InformeInspeccionForestalCAHtml()
	{
		
	}
	
	public InformeInspeccionForestalCAHtml(CertificadoAmbientalInformeForestal informeForestal,List<ProyectoUbicacionGeografica> ubicaciones,List<CertificadoAmbientalInformeForestalFormaCoordenada> listaCoordenadas,String proponente,Usuario usuarioElabora,Usuario usuarioDirector)
	{
		this.informeForestal=informeForestal;
		this.ubicaciones=ubicaciones;
		this.proponente=proponente;
		this.usuarioElabora=usuarioElabora;
		this.usuarioDirector=usuarioDirector;
		this.listaCoordenadas=listaCoordenadas;
	}
	
	public String getNombreProyecto()
	{
		return informeForestal.getProyecto().getNombre();
	}
	
	public String getCodigo()
	{
		return informeForestal.getCodigo();
	}
	
	public String getNombreAreaResponsable()
	{
		return informeForestal.getProyecto().getAreaResponsable().getAreaName();
	}
	
	public String getDescripcionProyecto()
	{
		return informeForestal.getDescripcionProyecto();
	}
	
	public String getDireccionProyecto()
	{
		return informeForestal.getProyecto().getDireccionProyecto();
	}
	
	public String getCodigoProyecto()
	{
		return informeForestal.getProyecto().getCodigo();
	}

	
	public String getCodigoInforme()
	{
		return informeForestal.getCodigo();
	}
	
	public String getObjetivo()
	{
		return informeForestal.getObjetivo();
	}
	
	public String getMarcoLegal()
	{
		return informeForestal.getMarcoLegal();
	}
    
	public String getDescripcionVegeetacion()
	{
		return informeForestal.getDescripcionVegetacion();
	}
	
	public String getConclusiones()
	{
		return informeForestal.getConclusiones();
	}
	
	public String getRecomendaciones()
	{
		return informeForestal.getRecomendaciones();	
	}
	
	public String getProponente()
	{
		return proponente;
	}
	
	public String getProponenteIdentificacion()
	{
		return informeForestal.getProyecto().getUsuario().getNombre();
	}
	
	public String getProponenteTelefono()
	{
		String telefonos="";
		List<Contacto> contactos=informeForestal.getProyecto().getUsuario().getPersona().getContactos();
		for (Contacto contacto : contactos) {
			if(contacto.getFormasContacto().getId().compareTo(FormasContacto.CELULAR)==0 || contacto.getFormasContacto().getId().compareTo(FormasContacto.TELEFONO)==0)
			{
				if(!telefonos.isEmpty())
					telefonos+=" / ";
				telefonos+= contacto.getValor();
			}
				
		}
		
		return telefonos;
	}
	
	public String getProponenteDireccion()
	{		
		List<Contacto> contactos=informeForestal.getProyecto().getUsuario().getPersona().getContactos();
		for (Contacto contacto : contactos) {
			if(contacto.getFormasContacto().getId().compareTo(FormasContacto.DIRECCION)==0)
			{
				return contacto.getValor();
			}
				
		}
		return "";		
	}
	
	public String getTecnicoResponsable()
	{
		return usuarioElabora.toString();
	}
	
	public String getCargoTecnico()
	{
		//return usuarioElabora.getPersona().getPosicion();
		
		//Belen solicita el cambio en el Ticket#10213323 Item #45 informe 31 octubre pagina 5 
		return usuarioElabora.getPersona().getTipoTratos().getNombre()+" "+ usuarioElabora.getPersona().getTitulo();
	}
	
	public String getDirector()
	{
		return usuarioDirector.toString();
	}
	
	public String getFechaElaboracion()
	{
		return informeForestal.getFechaModificacion()==null?JsfUtil.getDateFormat(informeForestal.getFechaCreacion()):JsfUtil.getDateFormat(informeForestal.getFechaModificacion());
	}
	
	public String getFechaInspeccion()
	{
		return JsfUtil.getDateFormat(informeForestal.getFechaInspeccion());
	}
	
	public String getCoordenadas() {
		String strTable = "<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody><tr BGCOLOR=\"#B2E6FF\">"
				+ "<td><strong>COORDENADA X</strong></td><td><strong>COORDENADA Y</strong></td><td><strong>DESCRIPCIÓN</strong></td><td><strong>FORMA</strong></td></tr>";

		for (CertificadoAmbientalInformeForestalFormaCoordenada tipoForma : listaCoordenadas) {
			for (Coordenada coor : tipoForma.getCoordenadas()) {
				strTable += "<tr>";
				strTable += "<td>" + coor.getX() + "</td>";
				strTable += "<td>" + coor.getY() + "</td>";
				strTable += "<td>" + coor.getDescripcion() + "</td>";
				strTable += "<td>" + tipoForma.getTipoForma().getNombre()
						+ "</td>";
				strTable += "</tr>";
			}
		}
		strTable += "</tbody></table>";
		strTable = strTable.replace("&", "&amp;");
		return strTable;
	}

	
	public String getUbicaciones() {
		String strTable = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody><tr BGCOLOR=\"#B2E6FF\">"
				+ "<td colspan=\"3\"><strong>UBICACION</strong></td></tr>"
				+ "<tr BGCOLOR=\"#B2E6FF\">";
		strTable+= "<td width=\"33%\"><strong>Provincia</strong></td>"
				+ "<td width=\"33%\"><strong>Cantón</strong></td>"
				+ "<td width=\"33%\"><strong>Parroquia</strong></td>";		
		strTable += "</tr>";	
		
		for (ProyectoUbicacionGeografica ubicacion : ubicaciones) {
			strTable += "<tr>";
			strTable += "<td>" + ubicacion.getUbicacionesGeografica().getNombre() + "</td>";
			strTable += "<td>" + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td>";
			strTable += "<td>" + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td>";			
			strTable += "</tr>";
		}
		
		strTable += "</tbody></table></center>";		
		return strTable;
	}
	
	public String getRemocionCobertura() {			
		return informeForestal.getRemocionCoberturaVegetal()?"Si":"No";
	}
	
		
}