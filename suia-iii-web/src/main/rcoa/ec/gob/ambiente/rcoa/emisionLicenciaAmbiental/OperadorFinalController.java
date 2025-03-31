package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
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

import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.OficioResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.OficioResolucionAmbiental;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class OperadorFinalController extends DocumentoReporteResolucionMemoController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5382084905489409831L;
		
	public OperadorFinalController() {	
		super();
	}

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;

	/* EJBs */
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private OficioResolucionAmbientalFacade oficioResolucionAmbientalFacade;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private DocumentoResolucionAmbiental documentoResolucionAlfresco, documentoMemorandoAlfresco, documentoPronunciamientoAlfresco, documentoMemorandoJuridicoAlfresco; 

	@Getter
	@Setter
	private String urlLinkSurvey;
	@Getter
	@Setter
	private Boolean mostrarEncuesta = true, showSurveyD = false, oficioDescargado = false;
	@Getter
    @Setter
    public static String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
	@Getter
	@Setter
	private boolean esAutoridad = false;
	@Getter
	@Setter
	private String tramite, autoridadAmbiental;
	
	
	/*CONSTANTES*/
	// Id's tabla public.areas_types
    public static final Integer ID_TIPO_AREA_PLANTA_CENTRAL = 1;
    public static final Integer RESOLUCION_GECA_ID = 64;
	public static final Integer MEMORANDO_GECA_ID = 65;
	public static final Integer PRONUNCIAMIENTO_GECA_ID = 66;
	public static final Integer MEMORANDO_JURIDICO_GECA_ID = 67;
	
	@PostConstruct
	public void init() {	
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			autoridadAmbiental = (String) variables.get("autoridadAmbiental");
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			tramite = proyectoLicenciaCoa.getCodigoUnicoAmbiental();
			inicializarResolucionPronunciamiento();
			
			resolucionAmbiental = resolucionAmbientalFacade.getByIdRegistroPreliminar(idProyecto);
			OficioResolucionAmbiental resolucion = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalByIdTipoDocumento(resolucionAmbiental.getId(), RESOLUCION_GECA_ID);
			OficioResolucionAmbiental momerando = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalByIdTipoDocumento(resolucionAmbiental.getId(), MEMORANDO_GECA_ID);
			OficioResolucionAmbiental pronunciamiento = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalByIdTipoDocumento(resolucionAmbiental.getId(), PRONUNCIAMIENTO_GECA_ID);
			OficioResolucionAmbiental memorandoJuridico = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalByIdTipoDocumento(resolucionAmbiental.getId(), MEMORANDO_JURIDICO_GECA_ID);
			
			documentoResolucionAlfresco = documentoResolucionAmbientalFacade.getByDocumentoTipoDocumento(resolucion.getId(), TipoDocumentoSistema.RCOA_LA_RESOLUCION);
			documentoMemorandoAlfresco = documentoResolucionAmbientalFacade.getByDocumentoTipoDocumento(momerando.getId(), TipoDocumentoSistema.RCOA_LA_MEMORANDO);
			documentoPronunciamientoAlfresco = documentoResolucionAmbientalFacade.getByDocumentoTipoDocumento(pronunciamiento.getId(), TipoDocumentoSistema.RCOA_LA_PRONUNCIAMIENTO);
			documentoMemorandoJuridicoAlfresco = documentoResolucionAmbientalFacade.getByDocumentoTipoDocumento(memorandoJuridico.getId(), TipoDocumentoSistema.EMISION_LICENCIA_MEMORANDO_TECNICO_JURIDICO);
			
			esAutoridad = verificaEsAutoridad();
		} catch (Exception e) {
			JsfUtil.addMessageError("Error visualizar resolucion / pronunciamiento");
			e.printStackTrace();
		}
	}
	
	private Boolean verificaEsAutoridad() {
		Boolean verifica = false;
		String usuario = JsfUtil.getLoggedUser().getNombre();
		Usuario usuarioLogeado = usuarioFacade.buscarUsuario(usuario);
		if (autoridadAmbiental == null) {
			Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
	    	String rolPrefijo, rolTecnico;
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
				rolPrefijo = "role.resolucion.pc.autoridad";
			} else {
				rolPrefijo = "role.resolucion.cz.gad.autoridad";
			}
	    	rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
	    	List<Usuario> listaTecnicosResponsables = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());			
			Usuario usuarioAutoridadAmbiental = listaTecnicosResponsables.get(0);
			if (usuarioLogeado.equals(usuarioAutoridadAmbiental)) {
				esAutoridad = true;
			}
		} else {
			if(usuario.equals(autoridadAmbiental)) {
				esAutoridad = true;
			}
		}
    	return verifica;
    }
	
	public void encuaesta() {
		try {					
			JsfUtil.addMessageInfo("Datos Guardados Correctamente");
		} catch (Exception e) {							
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void showSurvey() {		
		
		String url = surveyLink;
		String usuarioUrl = loginBean.getNombreUsuario();
		String proyectoUrl = tramite;
		String appUlr = "emision-licencia-rcoa";
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
			Usuario user=loginBean.getUsuario();
			if(user.getNombre().length()==13)
			{
				Organizacion orga = organizacionFacade.buscarPorRuc(user.getNombre());
				if(orga!=null)
					return "juridico";
			}		
			return "natural";
		} catch (Exception e) {
			return "";
		}		
	}
	
	public StreamedContent getDocumentoResolucionPronunciamiento(){		
		try {
			if (documentoPronunciamientoAlfresco != null && documentoPronunciamientoAlfresco.getContenidoDocumento() != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(documentoPronunciamientoAlfresco.getContenidoDocumento()),
						documentoPronunciamientoAlfresco.getMime(),
						documentoPronunciamientoAlfresco.getNombre());
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
				documentoResolucionAlfresco.setIdProceso(bandejaTareasBean.getProcessId());
				documentoMemorandoAlfresco.setIdProceso(bandejaTareasBean.getProcessId());
				documentoPronunciamientoAlfresco.setIdProceso(bandejaTareasBean.getProcessId());
				documentoMemorandoJuridicoAlfresco.setIdProceso(bandejaTareasBean.getProcessId());
				
				documentoResolucionAlfresco = documentoResolucionAmbientalFacade.guardar(documentoResolucionAlfresco);
				documentoMemorandoAlfresco = documentoResolucionAmbientalFacade.guardar(documentoMemorandoAlfresco);
				documentoPronunciamientoAlfresco = documentoResolucionAmbientalFacade.guardar(documentoPronunciamientoAlfresco);
				documentoMemorandoJuridicoAlfresco = documentoResolucionAmbientalFacade.guardar(documentoMemorandoJuridicoAlfresco);
				
				Map<String, Object> params=new HashMap<String, Object>();
				
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
