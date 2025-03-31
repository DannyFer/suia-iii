package ec.gob.ambiente.rcoa.revisionPreliminar.controllers;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.jfree.util.Log;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.PronunciamientoObsNoSubsanablesFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.PronunciamientoObservacionesNoSubsanables;
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
public class RecibirOficioObsNoSubsanablesController implements Serializable{

	 private static final long serialVersionUID = -875087443147320594L;
	  
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@EJB
	private ProcesoFacade procesoFacade;	
	
	@EJB
	private DocumentosCoaFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private SurveyResponseFacade surveyResponseFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private PronunciamientoObsNoSubsanablesFacade pronunciamientoFacade;

	@Getter
	@Setter
	private DocumentosCOA documento;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;

	@Getter
	@Setter
	private Boolean documentoDescargado = false;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private String tramite ="", docuTableClass;
	
	@Getter
	@Setter
	private Boolean mostrarEncuesta;
	
	@Getter
	@Setter
	private boolean showSurveyD = false;
	
	@Getter
	@Setter
	private String urlLinkSurvey = Constantes.getPropertyAsString("suia.survey.link");
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		tramite = (String) variables.get("u_tramite");
		
		tipoDocumento = TipoDocumentoSistema.RCOA_PRONUNCIAMIENTO_OBSERVACIONES_NO_SUBSANABLES;
		docuTableClass = "PronunciamientoObservacionesNoSubsanables";		
		
		proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		PronunciamientoObservacionesNoSubsanables oficioPronunciamiento = pronunciamientoFacade.obtenerPorProyecto(proyecto.getId());
		
		verEncuesta();
		
		List<DocumentosCOA> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDoc(oficioPronunciamiento.getId(), tipoDocumento, docuTableClass);
		if (listaDocumentosInt.size() > 0) 
			documento = listaDocumentosInt.get(0);
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
		
		String url = urlLinkSurvey;
		String usuarioUrl = loginBean.getNombreUsuario();
		String proyectoUrl = tramite;
		String appUlr = "pronunciamiento-observaciones-no-subsanables-rcoa";
		String tipoPerUrl = getProponente();
		String tipoUsr = "externo";
		url = url + "/faces/index.xhtml?" 
				+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
				+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
		Log.debug("enlace>>>" + url);
		urlLinkSurvey = url;
		showSurveyD = true;
	}
	
	private String getProponente() {
		try {
			Usuario user = loginBean.getUsuario();
			if (user.getNombre().length() == 13) {
				Organizacion orga = organizacionFacade.buscarPorRuc(user.getNombre());
				if (orga != null)
					return "juridico";
			}
			return "natural";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public StreamedContent descargar() throws Exception {
		try{
			DefaultStreamedContent content = new DefaultStreamedContent();
			if (documento != null) {
				if (documento.getContenidoDocumento() == null) {
					documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
				}
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documento.getContenidoDocumento()), documento.getExtencionDocumento());
				content.setName(documento.getNombreDocumento());
				
				documentoDescargado = true;
			} else {
				content = null;
				JsfUtil.addMessageError("No se pudo descargar documento.");
			}
			
			return content;
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar documento.");			
			e.printStackTrace();
		}
		return null;
	}
	
	public void finalizar() {
		try {
			if (!documentoDescargado) {
				JsfUtil.addMessageError("Debe descargar el oficio de pronunciamiento antes de finalizar.");
				return;
			} 
			
			try {
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				
				documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
				documentosFacade.guardar(getDocumento());

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			} catch (JbpmException e) {
				JsfUtil.addMessageError("Error al realizar la operación.");
			}


		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
}