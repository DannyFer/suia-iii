package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.ws.WebServiceException;

import org.jfree.util.Log;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.mapa.webservices.GenerarMapaWSService;
import ec.gob.ambiente.mapa.webservices.ResponseCertificado;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.proyecto.controller.VerProyectoRcoaBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AsignarAreaResponsableController implements Serializable {

	private static final long serialVersionUID = 2198593440217678246L;
	
	@EJB
    private AreaFacade areaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
    private OrganizacionFacade organizacionFacade;
	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
    private DocumentosCoaFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
	@EJB
	private CapasCoaFacade capasCoaFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoRcoa;
	
	@Getter
	@Setter
	private List<Area> listaAutoridadAmbientalCompetente;
	
	@Getter
	@Setter
	private List<Area> listaAutoridadAmbientalMaae;
	
	private GenerarMapaWSService wsMapas;
	
	@PostConstruct
    public void init() {
		
		proyectoRcoa = JsfUtil.getBean(VerProyectoRcoaBean.class).getProyectoLicenciaCoa();
		listaAutoridadAmbientalCompetente = areaFacade.getAutoridadAmbientalCompetente();
		listaAutoridadAmbientalMaae = areaFacade.getAutoridadAmbientalMaae();
	}
	
	public boolean validarServicioMapa()
	{
		boolean estado=false;
		try {			
			wsMapas = new GenerarMapaWSService(new URL(Constantes.getGenerarMapaWS()));
			estado=true;
		} catch (WebServiceException e) {
			estado=false;
			e.printStackTrace();
			System.out.println("Servicio no disponible ---> "+Constantes.getGenerarMapaWS());			
		} catch (MalformedURLException e) {
			estado=false;
		}
		return estado;
	}
	
	public void guardar() {
		try {
			
			if(!validarServicioMapa())
			{	
				JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
				return;
			}
			
			proyectoRcoa.setEstadoAsignacionArea(3);
			if(proyectoRcoa.getAreaInventarioForestal() == null)
				proyectoRcoa.setAreaInventarioForestal(proyectoRcoa.getAreaResponsable());
			
			certificado();
			
			Boolean existeMapa = generarMapa();
			
			if(!existeMapa)
				return; //si no se genera el mapa sale del método y muestra el mensaje de error
			
			proyectoLicenciaCoaFacade.guardar(proyectoRcoa);
			
			//enviar notificación operador
	    	String nombreOperador = "";
	    	Organizacion organizacion = null;
	    	Usuario usuarioProyecto = proyectoRcoa.getUsuario();
	    	if (usuarioProyecto.getNombre().length()<=10) {
				nombreOperador = usuarioProyecto.getPersona().getNombre();
			
			} else {
				organizacion = organizacionFacade.buscarPorRuc(usuarioProyecto.getNombre());					
	
				if(organizacion==null){
					nombreOperador = usuarioProyecto.getPersona().getNombre();
				}else{
					nombreOperador = organizacion.getNombre();
				}
			}
	    	
	    	Object[] parametrosNotificacionOperador = new Object[] {nombreOperador, 
	    			proyectoRcoa.getNombreProyecto(), 
	    			proyectoRcoa.getCodigoUnicoAmbiental() };
			
			String notificacionOperador = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
							"bodyNotificacionAreaRevisadaOperador",
							parametrosNotificacionOperador);
	
			if(organizacion != null)
				Email.sendEmail(organizacion, "Regularización Ambiental Nacional", notificacionOperador, proyectoRcoa.getCodigoUnicoAmbiental(), usuarioProyecto, JsfUtil.getLoggedUser());
			else
				Email.sendEmail(proyectoRcoa.getUsuario(), "Regularización Ambiental Nacional", notificacionOperador, proyectoRcoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
	        JsfUtil.redirectTo("/pages/rcoa/preliminar/proyectosPendienteAsignacion.jsf");
	        
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
		}
	}
	
	public void certificado() throws Exception {
		CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyectoRcoa.getCodigoUnicoAmbiental());
		if(oficioCI == null) {
			oficioCI = nuevoCertificado();
		}
		
		Usuario usuarioAutoridad = null;
		if(!proyectoRcoa.getAreaResponsable().getAreaAbbreviation().equalsIgnoreCase("ND")) {
			Area areaInternaTramite = proyectoRcoa.getAreaInventarioForestal();
			String tipoRol = "role.esia.cz.autoridad";
			if(areaInternaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
				tipoRol = "role.esia.cz.autoridad";
				areaInternaTramite = areaInternaTramite.getArea();
			}else if(areaInternaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
				tipoRol = "role.esia.pc.autoridad";
			}else if(areaInternaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)){
				tipoRol = "role.esia.ga.autoridad";
			}
			
			String rolAutoridad = Constantes.getRoleAreaName(tipoRol);
			
			usuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaInternaTramite).get(0);
		}
		
		
		if(usuarioAutoridad != null) {
			Area areaAutoridad = new Area();
			
			if(usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1){
				areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
			}else{
				areaAutoridad = proyectoRcoa.getAreaInventarioForestal();
			}
			
        	oficioCI.setAreaUsuarioFirma(areaAutoridad.getId());
        	oficioCI.setUsuarioFirma(usuarioAutoridad.getNombre());
		} else {
			oficioCI.setAreaUsuarioFirma(null);
        	oficioCI.setUsuarioFirma(null);
		}
		oficioCI=certificadoInterseccionCoaFacade.guardar(oficioCI);
		
		List<String> resultadoQr = GenerarQRCertificadoInterseccion.getCodigoQrUrlContent(true, 
				oficioCI.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), oficioCI.getCodigo(),
				GenerarQRCertificadoInterseccion.tipo_suia_rcoa, 0);
		oficioCI.setUrlCodigoValidacion(resultadoQr.get(0));
		oficioCI=certificadoInterseccionCoaFacade.guardar(oficioCI);
	}
	
	public CertificadoInterseccionOficioCoa nuevoCertificado() {
		CertificadoInterseccionOficioCoa oficioCI = new CertificadoInterseccionOficioCoa();
		oficioCI.setProyectoLicenciaCoa(proyectoRcoa);

		List<UbicacionesGeografica> ubicacionProyectoLista = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoRcoa);
		List<DetalleInterseccionProyectoAmbiental> interseccionLista = detalleInterseccionProyectoAmbientalFacade.buscarPorProyecto(proyectoRcoa);
		List<CapasCoa> capasCoaLista = capasCoaFacade.listaCapasCertificadoInterseccion();

		//generacion Ubicacion
		String strTableUbicacion = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody><tr BGCOLOR=\"#B2B2B2\">"			
				+ "<td><strong>Provincia</strong></td>"
				+ "<td><strong>Cantón</strong></td>"
				+ "<td><strong>Parroquia</strong></td>"
				+ "</tr>";
		
		for (UbicacionesGeografica ubicacion : ubicacionProyectoLista) {
			strTableUbicacion += "<tr>";
			strTableUbicacion += "<td>" + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td>";
			strTableUbicacion += "<td>" + ubicacion.getUbicacionesGeografica().getNombre() + "</td>";
			strTableUbicacion += "<td>" + ubicacion.getNombre() + "</td>";
			strTableUbicacion += "</tr>";
		}		
		strTableUbicacion += "</tbody></table></center>";
		
		//generacion Capas
		String strTableCapas = "";		
		for (CapasCoa capa : capasCoaLista) {			
			String fecha = "ND";
			if(capa.getFechaActualizacionCapa() != null)
				fecha = JsfUtil.getSimpleDateFormat(capa.getFechaActualizacionCapa());
			strTableCapas += capa.getNombre() + " (" + fecha + ")<br/>";			
		}
		
		//generacion interseccion
		List<String> detalleIntersecaCapasViabilidad=new ArrayList<String>();
		List<String> detalleIntersecaOtrasCapas=new ArrayList<String>();
		for (DetalleInterseccionProyectoAmbiental detalleInterseccion : interseccionLista) {
			String capaDetalle=detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getNombre()+": "+detalleInterseccion.getNombreGeometria();
			if(detalleInterseccion.getCodigoConvenio() != null)
				capaDetalle=detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getNombre()+": "+detalleInterseccion.getNombreGeometria() + " (" + detalleInterseccion.getCodigoConvenio() + ")";
			
			if(detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getViabilidad()){
				if(!detalleIntersecaCapasViabilidad.contains(capaDetalle))
					detalleIntersecaCapasViabilidad.add(capaDetalle);
			}else{				
				if(!detalleIntersecaOtrasCapas.contains(capaDetalle))
					detalleIntersecaOtrasCapas.add(capaDetalle);
			}
		}
		
		String strTableIntersecaViabilidad = "";
		for (String detalleInterseca : detalleIntersecaCapasViabilidad) {
			strTableIntersecaViabilidad += detalleInterseca + "<br/>";
		}

		String strTableOtrasIntersecciones = "";
		for (String detalleInterseca : detalleIntersecaOtrasCapas) {
			strTableOtrasIntersecciones += detalleInterseca + "<br/>";
		}
		
		oficioCI.setUbicacion(strTableUbicacion);
		oficioCI.setCapas(strTableCapas);
		oficioCI.setInterseccionViabilidad(strTableIntersecaViabilidad);
		oficioCI.setOtraInterseccion(strTableOtrasIntersecciones);
		oficioCI.setFechaOficio(new Date());
		oficioCI.setAreaUsuarioFirma(null);
		oficioCI.setUsuarioFirma(null);

		oficioCI = certificadoInterseccionCoaFacade.guardar(oficioCI);

		List<String> resultadoQr = GenerarQRCertificadoInterseccion
				.getCodigoQrUrlContent(true, oficioCI.getProyectoLicenciaCoa()
						.getCodigoUnicoAmbiental(), oficioCI.getCodigo(),
						GenerarQRCertificadoInterseccion.tipo_suia_rcoa, 0);
		oficioCI.setUrlCodigoValidacion(resultadoQr.get(0));
		oficioCI = certificadoInterseccionCoaFacade.guardar(oficioCI);
		
		return oficioCI;
	}

	public Boolean generarMapa()
	{
		ResponseCertificado resCer = new ResponseCertificado();
		resCer=wsMapas.getGenerarMapaWSPort().generarCertificadoInterseccion(proyectoRcoa.getCodigoUnicoAmbiental());
		if(resCer.getWorkspaceAlfresco()==null)
		{
			JsfUtil.addMessageError("Ocurrió un error al generar el mapa de intersección");
			return false;
		}
		else
		{
			TipoDocumento tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA.getIdTipoDocumento());
			DocumentosCOA documentoMapa = new DocumentosCOA();
			documentoMapa.setIdAlfresco(resCer.getWorkspaceAlfresco());
			documentoMapa.setExtencionDocumento(".pdf");		
			documentoMapa.setTipo("application/pdf");
			documentoMapa.setTipoDocumento(tipoDocumento);
			documentoMapa.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
			documentoMapa.setNombreDocumento("Mapa_Certificado_intersección.pdf");
			documentoMapa.setIdTabla(proyectoRcoa.getId());
			documentoMapa.setProyectoLicenciaCoa(proyectoRcoa);
			documentoMapa = documentosFacade.guardar(documentoMapa);
			
			return true;
		}
	}
	
	public void descargarCoorGeo()
	{
		try {
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyectoRcoa.getId(), TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA,"ProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarExcel(documentosFacade.descargar(listaDocumentos.get(0).getIdAlfresco()),"Coordenadas área geográfica (Anexo 1)");
			}
		} catch (Exception e) {
			Log.debug(e.toString());
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	public void descargarCoorImpl()
	{
		try {
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyectoRcoa.getId(), TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION,"ProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarExcel(documentosFacade.descargar(listaDocumentos.get(0).getIdAlfresco()),"Coordenadas área de implantación");
			}
		} catch (Exception e) {
			Log.debug(e.toString());
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
}
