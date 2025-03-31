package ec.gob.ambiente.prevencion.certificado.ambiental.controllers;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.DocumentosCertificadoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DescargarCertificadoAmbientalController {
	
	@EJB
	private SurveyResponseFacade surveyResponseFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private DocumentosCertificadoAmbientalFacade documentosCertificadoAmbientalFacade;
	
	@Getter
    @Setter
    public static String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@Getter
	@Setter
	private String tramite;
	@Getter
	@Setter
	private String urlLinkSurvey;
	
	@Getter
	@Setter
	private boolean mostrarEncuesta = true;
	
	@Getter
	@Setter
	private boolean showSurveyD = false;
	
	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

	private Map<String, Object> variables;

	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@Setter
	private DocumentoCertificadoAmbiental documentoCertificado;
	
	@Getter
	@Setter
	private boolean descargado = false;
	
	@PostConstruct
	public void init(){
		try {
						
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);	
			
			verEncuesta();
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			List<DocumentoCertificadoAmbiental> listaDocumentos = documentosCertificadoAmbientalFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoCertificadoAmbiental.class.getSimpleName(), TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_MAE);
			
			if(listaDocumentos != null && !listaDocumentos.isEmpty()){
				documentoCertificado = listaDocumentos.get(0);
			} else{
				listaDocumentos = documentosCertificadoAmbientalFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoCertificadoAmbiental.class.getSimpleName(), TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_GAD);
				documentoCertificado = listaDocumentos.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public boolean verEncuesta(){		
		if(!surveyResponseFacade.findByProjectApp(tramite, "certificado-ambiental-rcoa")){			
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
	
	public StreamedContent getDocumentoCertificadoAmbiental(){
		
		try {
			if (documentoCertificado != null && documentoCertificado.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documentoCertificado.getContenidoDocumento()),documentoCertificado.getMime(),documentoCertificado.getNombre());
				descargado = true;
				return streamedContent;
			}

		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar Documento Certificado");			
			e.printStackTrace();
		}
		return null;
	}	
	
	public void finalizar(){
		try {			
			
			proyecto.setProyectoFinalizado(true);
			proyecto.setProyectoFechaFinalizado(new Date());
			
			proyectoLicenciaCoaFacade.guardar(proyecto);
			
			documentoCertificado.setIdProceso(bandejaTareasBean.getProcessId());
			documentosCertificadoAmbientalFacade.guardar(documentoCertificado);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void cerrar(){
		if(descargado){
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}else{
			JsfUtil.addMessageError("Debe descargar el documento de Certificado Ambiental para poder Cerrar.");
		}		
	}
	
}
