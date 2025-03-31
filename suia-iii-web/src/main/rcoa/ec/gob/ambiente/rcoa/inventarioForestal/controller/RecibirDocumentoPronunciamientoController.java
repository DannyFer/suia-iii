package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.DocumentoInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ReporteInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ReporteInventarioForestal;
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
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RecibirDocumentoPronunciamientoController {
	
	@EJB
	private SurveyResponseFacade surveyResponseFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private DocumentoInventarioForestalFacade  documentoInventarioForestalFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
	@EJB
	private ReporteInventarioForestalFacade reporteInventarioForestalFacade;
	
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
	
	@Getter
	@Setter
	private boolean oficioDescargado = false;
	
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
	
	@Getter
	@Setter
	private DocumentoInventarioForestal documentoOficio, documentoInforme, documentoInspeccion = null;
	
	@Getter
	@Setter
	private InventarioForestalAmbiental inventario;
	
	@Getter
	@Setter
	public ReporteInventarioForestal oficioPronunciamiento, informeTecnico, informeInspeccion;
	
	@Getter
	@Setter
	public String cantidadObservaciones;
	
	@PostConstruct
	public void init(){
		try {
			
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);	
			cantidadObservaciones = (String)variables.get("cantidadNotificaciones");
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);			
			
			inventario= inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(proyecto.getId());
			
			oficioPronunciamiento = reporteInventarioForestalFacade.getByIdCertificadoByIdTipoDocumento(inventario.getId(), 3);
			
			verEncuesta();
			
			informeTecnico = reporteInventarioForestalFacade.getByIdCertificadoByIdTipoDocumento(inventario.getId(), 2);
			
			informeInspeccion = reporteInventarioForestalFacade.getByIdCertificadoByIdTipoDocumento(inventario.getId(), 1);
			
			List<DocumentoInventarioForestal> listaDocumentos = documentoInventarioForestalFacade.getByInventarioForestalReporteTipo(oficioPronunciamiento.getId(), TipoDocumentoSistema.INVENTARIO_FORESTAL_OFICIO_PRONUNCIAMIENTO);
    		
			List<DocumentoInventarioForestal> listaDocumentosInforme = documentoInventarioForestalFacade.getByInventarioForestalReporteTipo(informeTecnico.getId(), TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_TECNICO);
			
			List<DocumentoInventarioForestal> listaDocumentosInspeccion = documentoInventarioForestalFacade.getByInventarioForestalReporteTipo(informeInspeccion.getId(), TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_INSPECCION);
			
    		if(listaDocumentos != null && !listaDocumentos.isEmpty()){    			
    			documentoOficio= listaDocumentos.get(0);    			
    		} 
    		
    		if(listaDocumentosInforme != null && !listaDocumentosInforme.isEmpty()){
    			documentoInforme = listaDocumentosInforme.get(0);
    		}	
    		
    		if(listaDocumentosInspeccion != null && !listaDocumentosInspeccion.isEmpty()){
    			documentoInspeccion = listaDocumentosInspeccion.get(0);
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean verEncuesta(){		
		if(!surveyResponseFacade.findByProject(tramite)){
			// verifica si el pronunciamiento es favorable
			if (oficioPronunciamiento.getPronunciamientoFavorable()) {
				mostrarEncuesta = true;
			} else {
				mostrarEncuesta = false;
			}
		}else{
			mostrarEncuesta = false;
		}	
		
		return mostrarEncuesta;
	}
	
	public void showSurvey() {		
		
		String url = surveyLink;
		String usuarioUrl = loginBean.getNombreUsuario();
		String proyectoUrl = tramite;
		String appUlr = "inventario-forestal-rcoa";
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
	
	public StreamedContent getDocumentoInventarioForestal(){		
		try {
			if (documentoOficio != null && documentoOficio.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documentoOficio.getContenidoDocumento()),documentoOficio.getMimeDocumento(),documentoOficio.getNombreDocumento());
				oficioDescargado = true;
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
			if (oficioDescargado) {
				
				if(!cantidadObservaciones.equals("0")){
					documentoOficio.setNombreDocumento("oficioPronunciamiento_" + cantidadObservaciones + ".pdf");
					documentoInforme.setNombreDocumento("InformeTecnico_" +cantidadObservaciones + ".pdf");
				}			
				
				documentoOficio.setIdProceso(bandejaTareasBean.getProcessId());
				documentoInforme.setIdProceso(bandejaTareasBean.getProcessId());
				if (documentoInspeccion != null) {
					documentoInspeccion.setIdProceso(bandejaTareasBean.getProcessId());
				}
				
				documentoInventarioForestalFacade.guardar(documentoOficio);
				documentoInventarioForestalFacade.guardar(documentoInforme);
				
				
				Map<String, Object> params=new HashMap<String, Object>();
				
				params.put("requiereSubsanarObservaciones",!oficioPronunciamiento.getPronunciamientoFavorable());			
				params.put("admin","");
				if (oficioPronunciamiento.getPronunciamientoFavorable()) {
					params.put("inventarioForestalFavorable", true);
				}
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} else {
				JsfUtil.addMessageError("Debe descargar el pronunciamiento para continuar");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	

}
