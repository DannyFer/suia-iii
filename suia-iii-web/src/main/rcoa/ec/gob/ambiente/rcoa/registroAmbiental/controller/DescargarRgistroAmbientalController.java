package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityResolucionAmbientalRCOA;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;


@ManagedBean
@ViewScoped
public class DescargarRgistroAmbientalController {


	@EJB
	private SurveyResponseFacade surveyResponseFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
    @EJB
    private ContactoFacade contactoFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
   
	@Getter
    @Setter
    @ManagedProperty(value = "#{marcoLegalReferencialController}")
    private MarcoLegalReferencialController marcoLegalReferencialBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
	@Getter
    @Setter
    private String mensaje, pdf, tipoEnteResponsable, urlLinkSurvey;
	
    @Getter
    @Setter
    private boolean descargaRegistro, descargaResolucion, descargaDesechos, descargaInventario, mostrarEncuesta = true, showSurveyD = false;
    
	@Getter
	@Setter
	private EntityResolucionAmbientalRCOA entityInforme;
	

    private Map<String, Object> variables;
	
	private String tramite;
	
	@Getter
    @Setter
    public static String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
		
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;

	@Getter
	@Setter
	private DocumentoRegistroAmbiental adjuntoResolucion, adjuntoRegistro;

	@Getter
	@Setter
	private DocumentosRgdRcoa adjuntoRgd, adjuntoInventarioForestal;
    
    @PostConstruct
	private void init() {
    	try{
	    	descargaRegistro = false; 
	    	descargaResolucion = false;
	    	descargaDesechos = false;
	    	descargaInventario = false;
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			verEncuesta();
			
			List<DocumentoRegistroAmbiental> listaDocumentos = documentosFacade.documentoXTablaIdXIdDocLista(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.REGISTRO_AMBIENTAL_RCOA);
			
			if(listaDocumentos != null && !listaDocumentos.isEmpty()){
				adjuntoRegistro = listaDocumentos.get(0);
			}
			if(adjuntoRegistro == null){
				adjuntoRegistro = new DocumentoRegistroAmbiental();
			}
			
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDocLista(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RESOLUCION_AMBIENTAL_RCOA);
			
			if(listaDocumentos != null && !listaDocumentos.isEmpty()){
				adjuntoResolucion = listaDocumentos.get(0);
			}
			if(adjuntoResolucion == null){
				adjuntoResolucion = new DocumentoRegistroAmbiental();
			}
			// para documentos de rgd
			List<DocumentosRgdRcoa> listaDocumentosRgd = documentosRgdRcoaFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.TIPO_BORRADOR_RGD);
			if(listaDocumentosRgd != null && !listaDocumentosRgd.isEmpty()){
				adjuntoRgd = listaDocumentosRgd.get(0);
			}
			if(adjuntoRgd == null){
				adjuntoRgd = new DocumentosRgdRcoa();
				descargaDesechos = true;
			}
			// para documentos de inventario forestal
			 listaDocumentosRgd = documentosRgdRcoaFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.INVENTARIO_FORESTAL);
			if(listaDocumentosRgd != null && !listaDocumentosRgd.isEmpty()){
				adjuntoInventarioForestal = listaDocumentosRgd.get(0);
			}
			if(adjuntoInventarioForestal == null){
				adjuntoInventarioForestal = new DocumentosRgdRcoa();
				descargaInventario = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
  
	public boolean verEncuesta(){		
		if(!surveyResponseFacade.findByProject(tramite)){
			mostrarEncuesta = true;
		}else{
			mostrarEncuesta = false;
		}	
		
		return mostrarEncuesta;
	}
	
	public void showSurvey() {		
		
		String url = surveyLink;
		String usuarioUrl = loginBean.getNombreUsuario();
		String proyectoUrl = tramite;
		String appUlr = "certificado-ambiental-rcoa";
		String tipoPerUrl = getProponente();
		String tipoUsr = "externo";
		url = url + "/faces/index.xhtml?" 
				+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
				+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
		System.out.println("enlace>>>" + url);
		urlLinkSurvey = url;
		showSurveyD = true;
	}

	private String getProponente()
	{		
		try {
			Usuario user=loginBean.getUsuario();
			if(user.getNombre().length()==13)
			{
				Organizacion orga=organizacionFacade.buscarPorRuc(user.getNombre());
				if(orga!=null)
					return "juridico";
			}		
			return "natural";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}		
	}
	
	public StreamedContent getDocumentoRegistroA(){
		DescargarDocumentosCoaController descargarDocumentosCoaController = JsfUtil.getBean(DescargarDocumentosCoaController.class);
		StreamedContent file = descargarDocumentosCoaController.descargarDocumento(adjuntoRegistro); 
		if(file != null){
	    	descargaRegistro = true; 
			return file;
		}
		return file; 
	}
	
	public StreamedContent getDocumentoResolucion(){
		DescargarDocumentosCoaController descargarDocumentosCoaController = JsfUtil.getBean(DescargarDocumentosCoaController.class);
		StreamedContent file = descargarDocumentosCoaController.descargarDocumento(adjuntoResolucion); 
		if(file != null){
	    	descargaResolucion = true; 
			return file;
		}
		return file; 
	}

	public StreamedContent getDocumentoDesechos(){
		DescargarDocumentosCoaController descargarDocumentosCoaController = JsfUtil.getBean(DescargarDocumentosCoaController.class);
		StreamedContent file = descargarDocumentosCoaController.descargarDocumentoRGD(adjuntoRgd); 
		if(file != null){
	    	descargaDesechos = true; 
			return file;
		}
		return file; 
	}

	public StreamedContent getDocumentoInventario(){
		DescargarDocumentosCoaController descargarDocumentosCoaController = JsfUtil.getBean(DescargarDocumentosCoaController.class);
		StreamedContent file = descargarDocumentosCoaController.descargarDocumentoRGD(adjuntoInventarioForestal); 
		if(file != null){
	    	descargaInventario = true; 
			return file;
		}
		return file; 
	}
	
	public void finalizarDescarga(){
		if(descargaRegistro && descargaResolucion && descargaDesechos && descargaInventario){
		}
	}
	
	public void completarTarea(){
		try {
			if(descargaRegistro && descargaResolucion && descargaDesechos && descargaInventario){
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				RegistroAmbientalRcoa registroAmbientalRcoa = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyecto);
				registroAmbientalRcoa.setFinalizado(true);
				registroAmbientalCoaFacade.guardar(registroAmbientalRcoa);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			}else{
				JsfUtil.addMessageError("Debe descargar todos los documentos.");
			}
		} catch (JbpmException e) {					
			e.printStackTrace();
		}catch (Exception e) {					
			e.printStackTrace();
		}
	}
	

	private void notificacion(){
		try {
			String correoOperador = "";		
			String nombreOperador = "";
			String listaDocumentos = "Ficha Ambiental<br/>"
					+ "Resolución Ambiental<br/>"
					+ "Formato de sub plan de cierre y abandono<br/>";
			if(adjuntoInventarioForestal.getId() != null)
				listaDocumentos +="Guías de Vida Silvestre<br/>";
			if(adjuntoInventarioForestal.getId() != null)
				listaDocumentos +="Guías de Vida Silvestre<br/>";
			if(adjuntoInventarioForestal.getId() != null)
				listaDocumentos +="Registro de Sustancias Químicas<br/>";
			
			if(proyecto.getUsuario().getNombre().length() == 10){			
				List<Contacto> contacto = contactoFacade.buscarPorPersona(proyecto.getUsuario().getPersona());				
				for (Contacto con : contacto) {	                
	                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
	                	correoOperador = con.getValor();
	                	break;
	                }	               
	            }				
				nombreOperador = proyecto.getUsuario().getPersona().getNombre();				
			}else{
				Organizacion organizacion = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());
				if(organizacion != null){					
					List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);					
					for (Contacto con : contacto) {		               
		                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
		                    correoOperador = con.getValor();
		                    break;
		                }
		            }					
					nombreOperador = organizacion.getNombre();
				}else{					
					List<Contacto> contacto = contactoFacade.buscarPorPersona(proyecto.getUsuario().getPersona());					
					for (Contacto con : contacto) {		                
		                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
		                	correoOperador = con.getValor();
		                	break;
		                }	               
		            }					
					nombreOperador = proyecto.getUsuario().getPersona().getNombre();					
				}				
			}		
			
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionOperadorRcoaRegistroAmbiental", new Object[]{});
			
			mensaje = mensaje.replace("nombre_operador", nombreOperador);
			mensaje = mensaje.replace("codigo_proyecto", proyecto.getCodigoUnicoAmbiental());
			mensaje = mensaje.replace("nombre_proyecto", proyecto.getNombreProyecto());
			mensaje = mensaje.replace("lista_documentos", listaDocumentos);
			
			NotificacionAutoridadesController mail = new NotificacionAutoridadesController();
 			mail.sendEmailInformacionProponente(correoOperador, "", mensaje, "Notificación", proyecto.getCodigoUnicoAmbiental(), proyecto.getUsuario(), loginBean.getUsuario()); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
