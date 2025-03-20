package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.EquipoApoyoProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ObservacionesEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.PlanManejoAmbientalEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.EquipoApoyoProyecto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ViewScoped
@ManagedBean
public class ElaborarInformeTecnicoIndividualController {
	
	private final Logger LOG = Logger.getLogger(ElaborarInformeTecnicoIndividualController.class);
	
	@EJB
	private InformeTecnicoEsIAFacade informeTecnicoEsIAFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private ProcesoFacade procesoFacade;	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;	
	@EJB
	private OrganizacionFacade organizacionFacade;	
	@EJB
	private UsuarioFacade usuarioFacade;	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;	
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;	
	@EJB
	private DocumentosImpactoEstudioFacade documentosFacade;	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
    private ObservacionesEsIAFacade observacionesEsIAFacade;
	@EJB
	private EquipoApoyoProyectoEIACoaFacade equipoApoyoProyectoEIACoaFacade;
	@EJB
	private PlanManejoAmbientalEsIAFacade planManejoAmbientalEsIAFacade;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    @Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;	
    
    @ManagedProperty(value = "#{informeTecnicoEsIABean}")
	@Getter
	@Setter
	private InformeTecnicoEsIABean informeTecnicoEsIABean;
    
    @Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporte;	
	
	@Getter
	@Setter
	private Boolean  esReporteAprobacion;	
	
	@Getter
	@Setter
	private boolean habilitarObservaciones, editarObservaciones, informeGenerado, informeFirmado;	
	
	@Getter
	@Setter
	private InformeTecnicoEsIA informe;	
	
	@Getter
	@Setter
	private InformacionProyectoEia esiaProyecto;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoInforme;
	
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	
	@Getter
	@Setter
	private Area areaTramite;	
	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionPrincipal = new UbicacionesGeografica();
	
	@Getter
	@Setter
	private Boolean uploadFirma;
	
	@Getter
	@Setter
	private Boolean token;
	
	@Getter
    @Setter
    private byte[] informeByte;
	
	@Getter
    @Setter
    private String nombreReporte;
	
	@Getter
	@Setter
	private PlantillaReporte plantilla;
	
	@Getter
	@Setter
	private Integer idProyecto, numeroObservaciones, idTarea;
	
	@Getter
	@Setter
	private String urlPdf;	
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private boolean habilitarFirma, informeGuardado, descargado, subido;
	
	@Getter
	@Setter
	private String urlAlfresco, nombreClaseObservacionesPma;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoManual;
	
	@Getter
	@Setter
	private Boolean existeObservacionesEstudio;	
	
	@Getter
	@Setter
	private InformacionProyectoEia informacionEstudio;
	
	@Getter
	@Setter
	private EquipoApoyoProyecto equipoApoyoProyecto;
	
	@Getter
	@Setter
	private boolean esSocial = false;
	
	@Getter
	@Setter
	private boolean enviado = false;
	
	@Getter
	@Setter
	private boolean firmaSoloToken;
	
	@PostConstruct
	private void init() {
		JsfUtil.getLoggedUser().getNombre();
		try {			
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());	
			
			informacionEstudio = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectosBean.getProyectoRcoa());
			
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");			
			
			if(!firmaSoloToken){
				verificaToken();
			}else{
				token = true;
			}
			
			informeGuardado = false;
			descargado = false;
			habilitarFirma = false;
			subido = false;
			
			if(!informeTecnicoEsIABean.getCargaCompleta()) {
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN,("Error al definir el tipo de informe del EsIA"), null));
			}
			
			informeTecnicoEsIABean.generarInforme(true);
	        
	        validarExisteObservacionesEstudio();

			if(existeObservacionesEstudio){
				informeTecnicoEsIABean.getInformeTecnico().setTipoPronunciamiento(2);
				esReporteAprobacion = false;
			}
			else{
				informeTecnicoEsIABean.getInformeTecnico().setTipoPronunciamiento(1);
				esReporteAprobacion = true;
			}	
			
			equipoApoyoProyecto = equipoApoyoProyectoEIACoaFacade.obtenerEquipoApoyoProyecto(informacionEstudio);
			
			if(equipoApoyoProyecto.isTecnicoSocial()){
				
				ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectosBean.getProyectoRcoa());
				CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
				
				List<RolUsuario> listaRolUsuario = usuarioFacade.listarPorIdUsuario(JsfUtil.getLoggedUser().getId());
				Integer idSector = actividadPrincipal.getTipoSector().getId();
				
				String tipoRol = "role.esia.pc.tecnico.social.tipoSector." + idSector;
				
				if (!proyectosBean.getProyectoRcoa().getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {					
					tipoRol = "role.esia.gad.tecnico.social";
				}
				
				String rolTecnico = Constantes.getRoleAreaName(tipoRol);
				
				for(RolUsuario rol: listaRolUsuario){
					if(rol.getRol().getNombre().equals(rolTecnico)){
						esSocial = true;
						return;
					}
				}				
			}
			
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del informe técnico.");
		}
		
	}	
	
	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	public void guardarInforme() {
		try {
			
			informeTecnicoEsIABean.guardarDatos();
			
			informacionProyectoEIACoaFacade.guardar(informacionEstudio);
			
			if(informeTecnicoEsIABean.getRequiereIngresoPlan()) {
				String observacionesPma = planManejoAmbientalEsIAFacade.validarIngresoObservacionesPma(informeTecnicoEsIABean.getEsiaProyecto().getId(), nombreClaseObservacionesPma);
				if(observacionesPma != null) {
					habilitarFirma = false;
					JsfUtil.addMessageError(observacionesPma);
					return;
				}
			}
			
			habilitarFirma = true;
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			informeTecnicoEsIABean.generarInforme(true);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void guardarDocumentos() {
		try {
			guardarInforme();
			if(!validacionCampos()){
				return;
			}
			
			informeTecnicoEsIABean.guardarDocumentosAlfresco();

			if (informeTecnicoEsIABean.getDocumentoInforme() != null) {
				String documentOffice = documentosFacade.direccionDescarga(informeTecnicoEsIABean.getDocumentoInforme());
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		
		RequestContext.getCurrentInstance().update("formDialogFirma:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public void uploadFileFirmar(FileUploadEvent event) {		
		documentoInforme.setContenidoDocumento(event.getFile().getContents());
		uploadFirma=true;
		JsfUtil.addMessageInfo("Documento subido exitosamente");		
	}
	
	public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (informeByte != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
            		informeByte), "application/octet-stream");
            content.setName(nombreReporte);
        }
        return content;
    }
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
	
	public StreamedContent descargar() {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = informeTecnicoEsIABean.getInformeTecnico().getArchivo();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(informeTecnicoEsIABean.getInformeTecnico().getNombreReporte());
			} else
				JsfUtil.addMessageError("Error al generar el archivo");

			descargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargado== true) {
			byte[] contenidoDocumento = event.getFile().getContents();

			documentoManual=new DocumentoEstudioImpacto();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");		
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(InformeTecnicoEsIA.class.getSimpleName());
			documentoManual.setIdTable(informeTecnicoEsIABean.getInformeTecnico().getId());

			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public void finalizar() {
		try {			
			if(!enviado){
				
				if (token) {
					String idAlfresco = informeTecnicoEsIABean.getDocumentoInforme().getAlfrescoId();

					if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
						JsfUtil.addMessageError("El informe técnico no está firmado electrónicamente.");
						return;
					}
				} else {
					if(subido) {
						DocumentoEstudioImpacto documentoFinal = documentosFacade.guardarDocumentoAlfrescoCA(informeTecnicoEsIABean.getProyecto().getCodigoUnicoAmbiental(), 
							"INFORME_TECNICO", documentoManual, TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);

						informeTecnicoEsIABean.setDocumentoInforme(documentoFinal);
					} else {
						JsfUtil.addMessageError("Debe adjuntar el informe técnico firmado.");
						return;
					}				
				}
				
				Map<String, Object> parametros = new HashMap<>();

			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				Map<String, Object> data = new ConcurrentHashMap<String, Object>();	
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
				
				informeTecnicoEsIABean.getDocumentoInforme().setIdProceso(bandejaTareasBean.getProcessId());
				documentosFacade.guardar(informeTecnicoEsIABean.getDocumentoInforme());
				
				enviado = true;
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void validarExisteObservacionesEstudio() {
		try {
			existeObservacionesEstudio = false;
			String nombreClaseObservaciones1 = InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnicoEsIABean.getInformeTecnico().getUsuarioCreacion() + "_" + informeTecnicoEsIABean.getInformeTecnico().getTipoInforme();
			String nombreClaseObservaciones2 = InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnicoEsIABean.getInformeTecnico().getTipoInforme();
			List<String> nombreClaseObservaciones = new ArrayList<>();
			nombreClaseObservaciones.add(nombreClaseObservaciones1);
			nombreClaseObservaciones.add(nombreClaseObservaciones2);
			
			nombreClaseObservacionesPma = nombreClaseObservaciones1; //esta clase se usa tanto para tecnicos de apoyo (6) como para los de equipo multidisciplinario (3,4,5)
			if(!informeTecnicoEsIABean.getInformeTecnico().getTipoInforme().equals(InformeTecnicoEsIA.apoyo)){
				nombreClaseObservacionesPma = nombreClaseObservaciones2;
			}
			
			Integer idClaseObservaciones = informeTecnicoEsIABean.getEsiaProyecto().getId();
			List<ObservacionesEsIA> observaciones = observacionesEsIAFacade.listarPorIdClaseNombreClaseNoCorregidas(
					idClaseObservaciones, nombreClaseObservaciones);
			
			if(observaciones.size() > 0) {
				existeObservacionesEstudio = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/estudioImpactoAmbiental/elaborarInformeTecnicoIndividual.jsf");
	}
	
	public String abrirEstudio(){
		JsfUtil.cargarObjetoSession(InformeTecnicoEsIA.class.getSimpleName(), informeTecnicoEsIABean.getInformeTecnico().getId());
		JsfUtil.cargarObjetoSession("RevisionEIA"+proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental(), true);
		return JsfUtil.actionNavigateTo("/pages/rcoa/estudioImpactoAmbiental/default.jsf");		
	}
	
	public boolean habilitarGuardar() {
		if(existeObservacionesEstudio)
			return true;
		
		Object revisadoEIA=JsfUtil.devolverObjetoSession("RevisionEIA"+proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());
		return revisadoEIA!=null;
	}
	
	private boolean validacionCampos(){
		
		List<String> mensajes = new ArrayList<String>();
	
		if(informeTecnicoEsIABean.getInformeTecnico().getAntecedentes() == null || 
				informeTecnicoEsIABean.getInformeTecnico().getAntecedentes().equals("")){
			mensajes.add("Antecedentes es requerido");			
		}
		
		if(informeTecnicoEsIABean.getInformeTecnico().getObjetivos() == null || 
				informeTecnicoEsIABean.getInformeTecnico().getObjetivos().equals("")){
			mensajes.add("Objetivos es requerido");			
		}
		if(informeTecnicoEsIABean.getInformeTecnico().getCaracteristicas() == null || 
				informeTecnicoEsIABean.getInformeTecnico().getCaracteristicas().equals("")){
			mensajes.add("Características importantes es requerido");			
		}
		
		if(informeTecnicoEsIABean.getInformeTecnico().getEvaluacionTecnica()== null || 
				informeTecnicoEsIABean.getInformeTecnico().getEvaluacionTecnica().equals("")){
			mensajes.add("Evaluación Técnica es requerido");
		}
		
//		if(informeTecnicoEsIABean.getInformeTecnico().getObservaciones()== null || 
//				informeTecnicoEsIABean.getInformeTecnico().getObservaciones().equals("")){
//			mensajes.add("Observaciones es requerido");			
//		}
		
		if(informeTecnicoEsIABean.getInformeTecnico().getConclusionesRecomendaciones() == null || 
				informeTecnicoEsIABean.getInformeTecnico().getConclusionesRecomendaciones().equals("")){
			mensajes.add("Conclusiones y recomendaciones es requerido");
		}		
		
		if(mensajes.isEmpty()){			
			return true;
		}else{
			JsfUtil.addMessageError(mensajes);
			return false;
		}
		
	}

}
