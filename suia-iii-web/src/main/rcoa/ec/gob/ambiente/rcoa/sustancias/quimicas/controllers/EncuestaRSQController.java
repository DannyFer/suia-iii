package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class EncuestaRSQController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2473064315917885386L;

	private static final Logger LOG = Logger.getLogger(EncuestaRSQController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private OrganizacionFacade orgaFacade;
	
    @EJB
	private SurveyResponseFacade surveyResponseFacade;
    
    @EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	    
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
	
	@EJB
	private DocumentosRSQFacade documentosRSQFacade;
        
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;   

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
    
    @Getter
    @Setter
    public String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
       
    private Organizacion organizacion;
    
    @Setter
	@Getter
	private boolean showSurvey = false;
	
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	private RegistroSustanciaQuimica registroSustanciaQuimica;

	
	private Map<String, Object> variables;
	
	private String varTramite,codigoRsq;
	
	@PostConstruct
	public void init() {
		try {
			cargarDatosTarea();
			cargarDatosProyecto();
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos EncuestaRsqController.");
		}
	}
	
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			varTramite=(String)variables.get("tramite");			
		} catch (JbpmException e) {
			LOG.error("Error al cargarDatosTarea "+e.getCause()+" "+e.getMessage());
		}
	}
	private void cargarDatosProyecto() throws ServiceException{		
		proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyecto(varTramite);
		registroSustanciaQuimica=registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
		
		
		if(registroSustanciaQuimica!=null) {
			codigoRsq = registroSustanciaQuimica.getCodigo();
		}
	}
	
	
	// metodo para crear url de la encuesta
	public String urlLinkSurvey() {
		String url = surveyLink;
		String usuarioUrl = registroSustanciaQuimica.getProyectoLicenciaCoa().getUsuario().getNombre();
		String proyectoUrl = codigoRsq;
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
		if(surveyResponseFacade.findByProject(codigoRsq)) {			
			return false;
		} else {			
			return true;
		}
	}	
	
	public void enviar()
	{
		try {
			if(surveyResponseFacade.findByProject(codigoRsq)) {
				
				DocumentosSustanciasQuimicasRcoa documentoRsq=documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_REGISTRO_SUSANCIAS_QUIMICAS,RegistroSustanciaQuimica.class.getSimpleName(),registroSustanciaQuimica.getId());
				
				Long id = bandejaTareasBean.getTarea().getProcessInstanceId();
				documentoRsq.setProcessinstanceid(id.intValue());
				
				documentosRSQFacade.guardar(documentoRsq);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}			
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
		
	}
}