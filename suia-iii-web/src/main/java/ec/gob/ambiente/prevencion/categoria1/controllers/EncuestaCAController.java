package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class EncuestaCAController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2473064315917885386L;

	private static final Logger LOG = Logger.getLogger(EncuestaCAController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private OrganizacionFacade orgaFacade;
	
    @EJB
	private SurveyResponseFacade surveyResponseFacade;
        
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
    
    @Getter
    @Setter
    public String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
    
    @Getter
	@Setter
	ProyectoLicenciamientoAmbiental proyecto;
    
    private Organizacion organizacion;
    
    @Setter
	@Getter
	private boolean showSurvey = false;

	@PostConstruct
	public void init() {
		try {			
			proyecto=proyectosBean.getProyecto();			
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos EncuestaCAController.");
		}
	}
	
	
	// metodo para crear url de la encuesta
	public String urlLinkSurvey() {
		String url = surveyLink;
		String usuarioUrl = proyecto.getUsuario().getNombre();
		String proyectoUrl = proyecto.getCodigo();
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
		if(surveyResponseFacade.findByProject(proyecto.getCodigo())) {			
			return false;
		} else {			
			return true;
		}
	}	
	
	public void enviar()
	{
		try {
			if(surveyResponseFacade.findByProject(proyecto.getCodigo())) {
				procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}			
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
		
	}
}