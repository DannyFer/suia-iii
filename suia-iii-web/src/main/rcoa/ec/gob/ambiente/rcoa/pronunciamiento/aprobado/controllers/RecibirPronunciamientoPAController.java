package ec.gob.ambiente.rcoa.pronunciamiento.aprobado.controllers;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.OficioPronunciamientoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RecibirPronunciamientoPAController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2473064315917885386L;

	private static final Logger LOG = Logger.getLogger(RecibirPronunciamientoPAController.class);
	
	//EJBs
	@EJB
	private DocumentosImpactoEstudioFacade documentosImpactoEstudioFacade;
	
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	
	@EJB
	private OficioPronunciamientoEsIAFacade oficioPronunciamientoEsIAFacade;
	
	@EJB
	private OrganizacionFacade orgaFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;    
    
    @EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
    
    @EJB
	private SurveyResponseFacade surveyResponseFacade;
	    
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
	
	//BEANs        
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;   
    
    //OBJs
    @Getter
    private DocumentoEstudioImpacto documento;
    
    private Organizacion organizacion;
    
    @Getter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	//MAPs
	private Map<String, Object> variables;
	
	//STRINGs	    
    @Getter
    @Setter
    public String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
    
    //INTEGERs
  	private Integer numeroRevision;
       
    private String varTramite,codigoEncuesta,tipoPronunciamiento;
    
    //BOOLEANs    
    @Setter
	@Getter
	private boolean showSurvey = false;
	
    @Setter
	@Getter
	public boolean pronunciamientoAprobado;
	
	@PostConstruct
	public void init() {
		try {
			cargarDatosTarea();
			cargarDatosProyecto();
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos RecibirPronunciamientoPAController.");
		}
	}
	
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			varTramite=(String)variables.get("tramite");			
			tipoPronunciamiento=variables.containsKey("tipo_pronunciamiento")?((String)variables.get("tipo_pronunciamiento")):"";
			numeroRevision=variables.containsKey("numero_revision")?(Integer.valueOf((String)variables.get("numero_revision"))):1;
			pronunciamientoAprobado=tipoPronunciamiento.contains("aprobado");
		} catch (JbpmException e) {
			LOG.error("Error al cargarDatosTarea "+e.getCause()+" "+e.getMessage());
		}
	}
	
	private void cargarDatosProyecto() throws ServiceException, CmisAlfrescoException{		
		proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyecto(varTramite);
		InformacionProyectoEia informacionProyectoEia=informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectoLicenciaCoa);
		OficioPronunciamientoEsIA oficio=oficioPronunciamientoEsIAFacade.obtenerOficioPronunciamiento(informacionProyectoEia,2,numeroRevision);
		documento=documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(oficio.getId(), oficio.getClass().getSimpleName(), tipoDocumentoOficio());
		codigoEncuesta=oficio.getCodigoOficio();
		if(documento==null) {
			JsfUtil.addMessageError();
		}
	}
	
	
	// metodo para crear url de la encuesta
	public String urlLinkSurvey() {
		String url = surveyLink;
		String usuarioUrl = JsfUtil.getLoggedUser().getNombre();
		String proyectoUrl = codigoEncuesta;
		String appUlr = "suia";
		String tipoPerUrl = (organizacion!=null)?"juridico":"natural";
		String tipoUsr = "externo";
		url = url + "/faces/index.xhtml?" 
				+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
				+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
		return url;
	}
	
	public void showDialogSurvey() {
		showSurvey = true;
	}
	
	public boolean showLinkSurvey() {
		// si el proyecto tiene registrada la encuesta
		if(pronunciamientoAprobado && !surveyResponseFacade.findByProject(codigoEncuesta)) {			
			return true;
		} else {			
			return false;
		}
	}
	
	private TipoDocumentoSistema tipoDocumentoOficio() {
		if(tipoPronunciamiento.contains("aprobado")) {
			return TipoDocumentoSistema.RCOA_PA_OFICIO_APROBADO;
		}
		if(tipoPronunciamiento.contains("observado")) {
			return TipoDocumentoSistema.RCOA_PA_OFICIO_OBSERVADO;
		}	
		if(tipoPronunciamiento.contains("archivado")) {
			return TipoDocumentoSistema.RCOA_PA_OFICIO_ARCHIVADO;
		}
		return null;
	}
	
	public StreamedContent descargarDocumento() {
		try {
			if(documento.getContenidoDocumento()==null && documento.getId()!=null)
				documento.setContenidoDocumento(documentosImpactoEstudioFacade.descargar(documento.getAlfrescoId()));
						
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),documento.getMime(),documento.getNombre());
				return streamedContent;
			}
		} catch (CmisAlfrescoException e) {
			LOG.error(e);
			e.printStackTrace();
			
		}	
		return null;
	}
	
			
	public void enviar()
	{
		try {
			if(pronunciamientoAprobado && !surveyResponseFacade.findByProject(codigoEncuesta)) {
				return;
			}
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			if(tipoPronunciamiento.contains("aprobado")) {
				Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
				parametros.put("tramite",varTramite);					
				parametros.put("idProyecto", proyectoLicenciaCoa.getId());				
				parametros.put("operador", (String) variables.get("usuario_operador"));			
				parametros.put("tecnicoResponsable", (String) variables.get("usuario_tecnico"));
				parametros.put("esFisico", false);	
				try {
					Long idProceso = procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.ROCA_PROCESO_RESOLUCION_LICENCIA_AMBIENTAL, varTramite, parametros);
					System.out.println("proceso ResolucionLicenciaAmbiental ::::"+idProceso);
				} catch (JbpmException e) {
					e.printStackTrace();
				}
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");			
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
		
	}
}
