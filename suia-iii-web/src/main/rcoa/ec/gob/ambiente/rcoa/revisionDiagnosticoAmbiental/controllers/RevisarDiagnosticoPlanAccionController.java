package ec.gob.ambiente.rcoa.revisionDiagnosticoAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ObservacionesPreliminarFacade;
import ec.gob.ambiente.rcoa.facade.PronunciamientoObsNoSubsanablesFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ObservacionesPreliminar;
import ec.gob.ambiente.rcoa.model.PronunciamientoObservacionesNoSubsanables;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.proyecto.controller.VerProyectoRcoaBean;
import ec.gob.ambiente.rcoa.revisionDiagnosticoAmbiental.controllers.InformeOficioDiagnosticoAmbientalBean;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisarDiagnosticoPlanAccionController implements Serializable{

	 private static final long serialVersionUID = -875087443147320594L;
	  
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
    @ManagedProperty(value = "#{verProyectoRcoaBean}")
    private VerProyectoRcoaBean verProyectoRcoaBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{informeOficioDiagnosticoAmbientalBean}")
    private InformeOficioDiagnosticoAmbientalBean informeOficioDiagnosticoAmbientalBean;
	
	@EJB
	private ProcesoFacade procesoFacade;	
	
	@EJB
	private DocumentosCoaFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
    private ObservacionesPreliminarFacade observacionesPreliminarFacade;
	
	@EJB
	private PronunciamientoObsNoSubsanablesFacade pronunciamientoFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;

	@Getter
	@Setter
	private DocumentosCOA documento, documentoFirmaManual, documentoInforme;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private List<DocumentosCOA> listaDocumentosDiagnostico, listaPlanAccion;
	
	@Getter
	@Setter
	private List<ObservacionesPreliminar> observacionesOficio;
	
	@Getter
	@Setter
	private List<ObservacionesPreliminar> listaObservacionesDiagnostico;
	
	@Getter
	@Setter
	private PronunciamientoObservacionesNoSubsanables oficioPronunciamiento;
	
	private Map<String, Object> variables;

	@Setter
	@Getter
	private Integer numeroObservaciones;

	@Setter
    @Getter
    private int activeIndex = 0;
	
	@Getter
	@Setter
	private String tramite;
	
	@Getter
	@Setter
	private Boolean esProyectoEnEjecucion, esActualizacionAAA, esProyectoNuevo, existeObservacionDiagnostico;
	
	@Getter
	@Setter
	private Boolean existeObservacionesAutoridad, mostrarDiagnosticoAmbiantel;

	 @Getter
	@Setter
	private Boolean token, subido, descargado, firmaSoloToken;
	
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		try {
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
			verificaToken();

			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			tramite = (String) variables.get("u_tramite");
			
			String strProyectoEnEjecucion = (String) variables.get("esProyectoEnEjecucion");		
			Boolean esProyectoEnEjecucion = (strProyectoEnEjecucion != null) ? Boolean.valueOf(strProyectoEnEjecucion) : false;
			
			if(variables.get("numeroObservacionesDiagnostico") == null)
				numeroObservaciones=0;
			else
				numeroObservaciones = Integer.valueOf(variables.get("numeroObservacionesDiagnostico").toString());
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			esProyectoNuevo = (!esProyectoEnEjecucion) ? true: false;
			
			descargado = false;
			subido = false;
			
			listaDocumentosDiagnostico = new ArrayList<>();
			listaPlanAccion = new ArrayList<>();
			
			if(!esProyectoNuevo) {
				listaDocumentosDiagnostico = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_DIAGNOSTICO_AMBIENTAL,"Diagnostico ambiental");		
				
				listaPlanAccion = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_PLAN_ACCION,"Diagnostico ambiental");
			}
			
			informeOficioDiagnosticoAmbientalBean.generarInforme(true);
			informeOficioDiagnosticoAmbientalBean.generarOficio(true);

			 observacionesOficio = observacionesPreliminarFacade.listarPorIdClaseNombreClaseNoCorregidas(
					 informeOficioDiagnosticoAmbientalBean.getIdInformePrincipal(), "pronunciamientoDiagnosticoAmbiental");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/revisionPreliminar/tecnicoRevision.jsf");
	}

	public boolean verificaToken() {
		if(firmaSoloToken) {
			token = true;
			return token;
		}
		
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
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

	public void irTabOficio() {
		informeOficioDiagnosticoAmbientalBean.generarInforme(true);
        setActiveIndex(1);
	}
	public void irTabInforme() {
		informeOficioDiagnosticoAmbientalBean.generarInforme(true);
        setActiveIndex(0);
	}

	public void validarExisteObservacionesDiagnostico() throws ServiceException {
		existeObservacionDiagnostico = false;
		String nombreClaseObservaciones = "observacionesDiagnosticoAmbiental";
		Integer idClaseObservaciones = proyecto.getId();
		List<ObservacionesPreliminar> observacionesPendientes = observacionesPreliminarFacade.listarPorIdClaseNombreClaseNoCorregidas(
				idClaseObservaciones, nombreClaseObservaciones);
		
		if(observacionesPendientes.size() > 0) 
			existeObservacionDiagnostico = true;
		
		informeOficioDiagnosticoAmbientalBean.validarExisteObservacionesDiagnostico();

		RequestContext.getCurrentInstance().update("form:obsDiagnostico");
		RequestContext.getCurrentInstance().update("form:tabDocumentos");
	}
	
	public StreamedContent descargarDocumento(DocumentosCOA documento) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentosFacade
						.descargar(documento.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getContenidoDocumento()), documento.getExtencionDocumento());
			content.setName(documento.getNombreDocumento());
		}
		return content;

	}

	public void  firmar() {
		try {
			List<String> msgError= new ArrayList<String>();
			if(informeOficioDiagnosticoAmbientalBean.getInformeTecnico().getPronunciamiento() == null || informeOficioDiagnosticoAmbientalBean.getInformeTecnico().getPronunciamiento() == "")
				msgError.add("El campo 'Pronunciamiento' es requerido.");
	
			if(informeOficioDiagnosticoAmbientalBean.getOficioPronunciamiento().getAsunto() == null || informeOficioDiagnosticoAmbientalBean.getOficioPronunciamiento().getAsunto() == "")
				msgError.add("El campo 'Asunto' es requerido.");
			if(informeOficioDiagnosticoAmbientalBean.getOficioPronunciamiento().getPronunciamiento() == null || informeOficioDiagnosticoAmbientalBean.getOficioPronunciamiento().getPronunciamiento() == "")
				msgError.add("El campo 'Pronunciamiento' es requerido.");
			
			if(msgError.size() > 0) {
				JsfUtil.addMessageError(msgError);
				return;
			}
			
			informeOficioDiagnosticoAmbientalBean.guardarInforme();
			informeOficioDiagnosticoAmbientalBean.guardarOficio();
	
			RequestContext.getCurrentInstance().execute("PF('signDialogFirmar').show();");
			
		} catch (Throwable e) {
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
	}

	public String firmaElectronica() {
        try {
        	informeOficioDiagnosticoAmbientalBean.generarInforme(false);
        	documentoInforme = informeOficioDiagnosticoAmbientalBean.guardarDocumentoInformeAlfresco();
        	return DigitalSign.sign(documentosFacade.direccionDescarga(documentoInforme), JsfUtil.getLoggedUser().getNombre());
        } catch (Throwable e) {
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }
    }

    public StreamedContent getStreamInforme() throws Exception {
    	informeOficioDiagnosticoAmbientalBean.generarInforme(false);
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (informeOficioDiagnosticoAmbientalBean.getInformeTecnico().getArchivoReporte() != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(informeOficioDiagnosticoAmbientalBean.getInformeTecnico().getArchivoReporte()), "application/pdf");
            content.setName(informeOficioDiagnosticoAmbientalBean.getInformeTecnico().getNombreReporte());
            descargado = true;
        }
        return content;
    }

    public void uploadListenerDocumentos(FileUploadEvent event) {
		if(descargado){
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoFirmaManual = new DocumentosCOA();
			documentoFirmaManual.setIdTabla(informeOficioDiagnosticoAmbientalBean.getInformeTecnico().getId());
			documentoFirmaManual.setNombreTabla("InformeDiagnosticoAmbiental");
			documentoFirmaManual.setTipo("application/pdf");
			documentoFirmaManual.setExtencionDocumento(".pdf");
			documentoFirmaManual.setContenidoDocumento(contenidoDocumento);
			documentoFirmaManual.setNombreDocumento(event.getFile().getFileName());
			
			subido = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento");
		}
	}
	
	public void finalizar() {
		try {
			
			if (token) {
				String idAlfrescoInforme = documentoInforme.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El documento no está firmado electrónicamente.");
					return;
				}
			} else {
				if(subido) {
					informeOficioDiagnosticoAmbientalBean.guardarDocumentoInformeAlfresco();
					documentoInforme = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "DIAGNOSTICO_AMBIENTAL", 1L, documentoFirmaManual, TipoDocumentoSistema.RCOA_INFORME_DIAGNOSTICO_AMBIENTAL);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el informe firmado.");
					return;
				}				
			}

				Map<String, Object> parametros = new HashMap<>();
				parametros.put("observacionesRevisionDiagnostico", existeObservacionDiagnostico);
				
				String usrAutoridad = (String) variables.get("autoridadCompetente");
				if(usrAutoridad == null || (!usrAutoridad.equals(informeOficioDiagnosticoAmbientalBean.getUsuarioAutoridad().getNombre())))
					parametros.put("autoridadCompetente", informeOficioDiagnosticoAmbientalBean.getUsuarioAutoridad().getNombre());
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public void finalizar_() {
		try {
			
			Map<String, Object> parametros = new HashMap<>();
			
			Boolean tramiteTieneObservaciones = false;
			if(esProyectoNuevo) {
				if((esProyectoEnEjecucion != null && esProyectoEnEjecucion) || (esActualizacionAAA != null && esActualizacionAAA))
					tramiteTieneObservaciones = true;
				
				if(esProyectoEnEjecucion != null && esProyectoEnEjecucion)
					parametros.put("esProyectoEnEjecucion", true);
					
				parametros.put("observacionDiagnosticoAmbiental", esProyectoEnEjecucion);
			} else {
				if((existeObservacionDiagnostico != null && existeObservacionDiagnostico) || (esActualizacionAAA != null && esActualizacionAAA))
					tramiteTieneObservaciones = true;
				
				parametros.put("observacionDiagnosticoAmbiental", existeObservacionDiagnostico);
			}
			
			
				parametros.put("numeroObservacionesDiagnostico", numeroObservaciones + 1);
			
			parametros.put("observacionesRevisionDiagnostico", tramiteTieneObservaciones);
			parametros.put("observacionAcualizacionAdministrativa", false); //parametros.put("observacionAcualizacionAdministrativa", esActualizacionAAA); //habilitar cuando este listo el proceso

			
			proyectoLicenciaCoaFacade.guardar(proyecto);
			
			if (oficioPronunciamiento != null && oficioPronunciamiento.getId() != null) {
				oficioPronunciamiento.setEstado(false);
				pronunciamientoFacade.guardar(oficioPronunciamiento, proyecto.getAreaResponsable());
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			
			//enviar notificacion al operador
			if(!tramiteTieneObservaciones && proyecto.getFechaInicioRegularizacionAmbiental() != null) {
				String nombreOperador = usuarioFacade.recuperarNombreOperador(proyecto.getUsuario()).get(0);
				
				DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
				
				Object[] parametrosCorreo = new Object[] {nombreOperador, proyecto.getNombreProyecto(), 
						proyecto.getCodigoUnicoAmbiental(), formatoFecha.format(proyecto.getFechaInicioRegularizacionAmbiental())};
				String notificacion = mensajeNotificacionFacade
						.recuperarValorMensajeNotificacion(
								"bodyNotificacionInicioRegularizacionAmbiental",
								parametrosCorreo);
	
				Email.sendEmail(proyecto.getUsuario(), "Regularización Ambiental Nacional", notificacion, tramite, loginBean.getUsuario());
			}

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);

		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	public String abrirDiagnosticoAmbiental(){
		JsfUtil.cargarObjetoSession(InformeTecnicoEsIA.class.getSimpleName(), proyecto.getId());
		return JsfUtil.actionNavigateTo("/pages/rcoa/revisionPreliminar/observacionesDiagnosticoAmbiental.jsf");		
	}
}
