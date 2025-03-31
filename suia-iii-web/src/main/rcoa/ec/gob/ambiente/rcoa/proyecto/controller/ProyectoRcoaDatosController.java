package ec.gob.ambiente.rcoa.proyecto.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CriterioMagnitudFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosProcesoRcoaFacade;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.VariableCriterioFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.util.BuscarDocumentosBean;
import ec.gob.ambiente.suia.administracion.facade.FlujosCategoriaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.survey.SurveyAcceptance;
import ec.gob.ambiente.suia.survey.SurveyAcceptanceFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ProyectoRcoaDatosController extends ResumenYEstadosEtapasRcoaBean {
	
	private static final long serialVersionUID = -6963992638158983944L;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{verProyectoRcoaBean}")
    private VerProyectoRcoaBean verProyectoRcoaBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarDocumentosBean}")
    private BuscarDocumentosBean buscarDocumentosBean;
	
	@EJB
    private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
    @EJB
    private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalCoaShapeFacade;
    @EJB
    private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
    @EJB
    private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
    @EJB
    private CriterioMagnitudFacade criterioMagnitudFacade;
    @EJB
	private VariableCriterioFacade variableCriterioFacade;
    @EJB
	private GestionarProductosQuimicosProyectoAmbientalFacade gestionarProductosQuimicosProyectoAmbientalFacade;    
    @EJB
	private OrganizacionFacade organizacionFacade;
    
    @EJB
	private FlujosCategoriaFacade flujosCategoriaFacade;
    
    @EJB
	private DocumentosProcesoRcoaFacade documentosProcesoRcoaFacade;
    
    @EJB 
	private SurveyResponseFacade surveyResponseFacade;
    
    @EJB
    private SurveyAcceptanceFacade surveyAcceptanceFacade;
    
    @Getter
    @Setter
    public static String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
	
	private Organizacion organizacion;
	
	@Getter
	@Setter
	private boolean mostrarEncuesta;
	
	@Getter
	@Setter
	private boolean mostrarNotaLicenciamiento = false;
	
	@Getter
	@Setter
	private boolean showSurvey;
	
	@Getter
	@Setter
	private SurveyAcceptance aceptacion;

	public ProyectoRcoaDatosController() {
		super();
	}	
	
	@SuppressWarnings("unlikely-arg-type")
	@PostConstruct
	public void inicio() throws ServiceException {
		
		verProyectoRcoaBean.cargarDatos();
		if (verProyectoRcoaBean.getProyectoLicenciaCoa().getCategoria().getNombrePublico().equals("Licencia Ambiental"))
		{
			mostrarNotaLicenciamiento = true;
		}
		else
		{
			mostrarNotaLicenciamiento = false;
		}
		proyectoLicenciaCoa = verProyectoRcoaBean.getProyectoLicenciaCoa();
		
		//Para Obtener los flujos
	    flujos = new ArrayList<>();
	    tareas = new ArrayList<>();
	    documentos = new ArrayList<>();
		
	    getFlujosProyecto();	    
//	    verEncuesta();
	}
	
	public void cargarUbicacionProyecto() {
		ubicacionesSeleccionadas = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa);
	}
	
	public String getNombreProponente() {
		
		String nombreOperador = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
		
		return nombreOperador;
	}
	
	public boolean isOperador(Usuario usuario){
		for (RolUsuario rolUsuario : usuario.getRolUsuarios()) {
			if(rolUsuario.getRol().getNombre().contains("sujeto") && rolUsuario.getEstado()){
					return true;
			}
		}
		return false;
	}
	
	public void verDocumentos(Long processInstanceId, String nombreProceso, ProyectoLicenciaCoa proyecto) {
		try {
			documentos = buscarDocumentosBean.buscarDocumentos(processInstanceId, false, proyecto);
			if ((nombreProceso.equals("rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales")) && (verProyectoRcoaBean.getProyectoLicenciaCoa().getCategoria().getNombrePublico().equals("Licencia Ambiental")))
			{
				List<Documento> documentosRGD = new ArrayList<Documento>();
				documentosRGD = documentos;
				documentos  = new ArrayList<Documento>();
				for (Documento docu : documentosRGD)
				{
					if((docu.getNombre().contains("RegistroGeneradorDesechos")) && ((docu.getTipoDocumento() != null) && (docu.getTipoDocumento().getId().equals(TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS.getIdTipoDocumento())))) 
					{
						docu.setNombre("RegistroGeneradorDesechosProvisional.pdf");
					}
					documentos.add(docu);
				}
			}
			
			if ((nombreProceso.equals("rcoa.ResolucionLicenciaAmbiental")) && (verProyectoRcoaBean.getProyectoLicenciaCoa().getCategoria().getNombrePublico().equals("Licencia Ambiental")))
			{
				List<Documento> documentosRGD = new ArrayList<Documento>();
				documentosRGD = documentos;
				documentos  = new ArrayList<Documento>();
				for (Documento docu : documentosRGD)
				{
					if((docu.getNombre().contains("RegistroGeneradorDesechos")) && ((docu.getTipoDocumento() != null) && (docu.getTipoDocumento().getId().equals(TipoDocumentoSistema.RGD_FINAL_REGISTRO_GENERADOR_DESECHOS.getIdTipoDocumento()))))
					{
						docu.setNombre("RegistroGeneradorDesechosFinal.pdf");
					}
					documentos.add(docu);
				}
			}			
			
			
			verEncuesta(nombreProceso);
			
			RequestContext.getCurrentInstance().update("dlg_verDocumentos");
			RequestContext.getCurrentInstance().update(":form:tab:pnlEncuesta");

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public StreamedContent getStream(Documento documento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosProcesoRcoaFacade.descargar(documento.getIdAlfresco(), documento.getFechaCreacion());
			} else if(documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}
			
			if (documento != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	
	public String urlLinkSurvey() {		
		
		String url = surveyLink;
		String usuarioUrl = proyectoLicenciaCoa.getUsuario().getNombre();
		String proyectoUrl = proyectoLicenciaCoa.getCodigoUnicoAmbiental();
		String appUlr = proceso;
		String tipoPerUrl = (organizacion!=null)?"juridico":"natural";
		String tipoUsr = "externo";
		url = url + "/faces/index.xhtml?" 
				+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
				+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
		
		return url;
	}
	
	public void cerrar(){
		try {
			showSurvey = false;
			RequestContext.getCurrentInstance().execute("PF('surveyDialog').hide();");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cerrarDocumentos(){
		try {
			RequestContext.getCurrentInstance().execute("PF('dlg_verDocumentos').hide();");			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String proceso;
	public boolean verEncuesta(String nombreProceso){
		aceptacion = new SurveyAcceptance();
		if(!verificacionEncuesta(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), nombreProceso)){
			if(Usuario.isUserInRole(JsfUtil.getLoggedUser(),"sujeto de control")){
				if(!surveyResponseFacade.findByProjectApp(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), nombreProceso)){
					proceso = nombreProceso;
					mostrarEncuesta = true;			
				}else{
					mostrarEncuesta = false;
				}
			}else
				mostrarEncuesta = false;
		}else{
			mostrarEncuesta = false;
		}			
		
		return mostrarEncuesta;
	}
	
	public boolean verificacionEncuesta(String tramite, String proceso){
		try {
			
			List<SurveyAcceptance> lista = surveyAcceptanceFacade.buscarAceptacion(tramite, proceso);
			
			if(lista != null && !lista.isEmpty()){
				return true;
			}else
				return false;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void guardarAceptacion(){
		try {
						
			aceptacion.setEstado(true);
			aceptacion.setFechaCreacion(new Date());			
			aceptacion.setUsuarioCreacion(proyectoLicenciaCoa.getUsuario().getNombre());
			aceptacion.setUsuario(proyectoLicenciaCoa.getUsuario());
			aceptacion.setNombreProceso(proceso);
			aceptacion.setTramite(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
			
			surveyAcceptanceFacade.guardar(aceptacion);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
