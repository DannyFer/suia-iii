package ec.gob.ambiente.rcoa.participacionCiudadanaBypass.controllers;

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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.DocumentoPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.ProyectoFacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.ReporteFacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.ProyectoFacilitadorPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.ReporteFacilitadorPPC;
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
public class RecibirPronunciamientoPPCBypassController implements Serializable {
	  
	private static final long serialVersionUID = 1L;

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
	private DocumentoPPCFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private ReporteFacilitadorPPCFacade reporteFacilitadorPPCFacade;

	@EJB
	public CatalogoCoaFacade catalogoCoaFacade;

	@EJB
	private ProyectoFacilitadorPPCFacade proyectoFacilitadorPPCFacade;

	@EJB
	private SurveyResponseFacade surveyResponseFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@Getter
	@Setter
	private DocumentosPPC documento;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;

	@Getter
	@Setter
	private ProyectoFacilitadorPPC proyectoFacilitadorPPC;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;

	@Getter
	@Setter
	private CatalogoGeneralCoa tipoOficio;

	@Getter
	@Setter
	private Boolean documentoDescargado = false;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private String tramite ="", docuTableClass, appUlr;

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
	public void iniciar() throws Exception
	{
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		tramite = (String) variables.get("tramite");

		String tipoPronunciamiento = (String) variables.get("esAprobacion");
		Boolean esAprobacion = Boolean.parseBoolean(tipoPronunciamiento);
		
		docuTableClass = "OficioPronunciamientoPPC";
		if (esAprobacion) {
			tipoOficio = catalogoCoaFacade.obtenerCatalogoPorCodigo("ppc.bypass.oficio.aprobacion");
			tipoDocumento = TipoDocumentoSistema.RCOA_PPC_BYPASS_OFICIO_APROBACION;
		} else {
			tipoOficio = catalogoCoaFacade.obtenerCatalogoPorCodigo("ppc.bypass.oficio.archivo");
			tipoDocumento = TipoDocumentoSistema.RCOA_PPC_BYPASS_OFICIO_ARCHIVO;
		}
		
		proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		proyectoFacilitadorPPC = proyectoFacilitadorPPCFacade.getByIdProyecto(proyecto.getId());
		
		ReporteFacilitadorPPC oficioPronunciamiento = reporteFacilitadorPPCFacade.getByIdProyectoFacilitadorTipoReporte(proyectoFacilitadorPPC.getId(), tipoOficio.getId());
		
		List<DocumentosPPC> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDoc(oficioPronunciamiento.getId(), tipoDocumento, docuTableClass);
		if (listaDocumentosInt.size() > 0) 
			documento = listaDocumentosInt.get(0);
		
		appUlr = "suia-pccBypass-rcoa";

		mostrarEncuesta = false;
		if (esAprobacion)
			verEncuesta();
	}

	public boolean verEncuesta(){		
		if(!surveyResponseFacade.findByProjectApp(tramite, appUlr)){
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
		String tipoPerUrl = getProponente();
		String tipoUsr = "externo";
		url = url + "/faces/index.xhtml?" 
				+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
				+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
		System.out.println("enlace>>>" + url);
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