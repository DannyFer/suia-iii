package ec.gob.ambiente.rcoa.revisionPreliminar.controllers;

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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.jfree.util.Log;
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
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisarDiagnosticoAmbientalController implements Serializable{

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
	private DocumentosCOA documento;
	
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
	
	@Getter
	@Setter
	private String tramite;
	
	@Getter
	@Setter
	private Boolean existeObservacionRegistroPreliminar, sonObservacionesSubsanables, esProyectoEnEjecucion, esActualizacionAAA, esProyectoNuevo, existeObservacionDiagnostico;
	
	@Getter
	@Setter
	private Boolean existeObservacionesAutoridad, mostrarDiagnosticoAmbiantel;
	
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		try {
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
			
			observacionesOficio = new ArrayList<>();
			
			String strRegistroPreliminar = (String) variables.get("observacionIngresoInformacionPreliminar");		
			existeObservacionRegistroPreliminar = (strRegistroPreliminar != null) ? Boolean.valueOf(strRegistroPreliminar) : null;
			
			String strObservacionDiagnostico = (String) variables.get("observacionDiagnosticoAmbiental");		
			existeObservacionDiagnostico = (strObservacionDiagnostico != null) ? Boolean.valueOf(strObservacionDiagnostico) : null;
			
			if(esProyectoNuevo) 
				this.esProyectoEnEjecucion = existeObservacionDiagnostico;
			mostrarDiagnosticoAmbiantel = (proyecto.getTipoProyecto() == 2 || !esProyectoNuevo);
			
			String strObservacionesSubsanables = (String) variables.get("sonObservacionsSubsanables");		
			sonObservacionesSubsanables = (strObservacionesSubsanables != null) ? Boolean.valueOf(strObservacionesSubsanables) : null;
			
			oficioPronunciamiento = pronunciamientoFacade.obtenerPorProyecto(proyecto.getId());
			if (oficioPronunciamiento != null && oficioPronunciamiento.getId() != null)
				observacionesOficio = observacionesPreliminarFacade.listarPorIdClaseNombreClaseNoCorregidas(
								oficioPronunciamiento.getId(), "oficioObservacionesNoSubsanables");
			
			listaDocumentosDiagnostico = new ArrayList<>();
			listaPlanAccion = new ArrayList<>();
			
			if(!esProyectoNuevo) {
				listaDocumentosDiagnostico = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_DIAGNOSTICO_AMBIENTAL,"Diagnostico ambiental");		
				
				listaPlanAccion = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_PLAN_ACCION,"Diagnostico ambiental");
			}
			
			cargarDatos();
			
			validarExisteObservacionesInformacionRegistro();
			
			listaObservacionesDiagnostico = observacionesPreliminarFacade.listarPorIdClaseNombreClaseNoCorregidas(proyecto.getId(), "observacionesDiagnosticoAmbiental");
			if(listaObservacionesDiagnostico.size() > 0)
				existeObservacionDiagnostico = true;
			else
				existeObservacionDiagnostico = false;
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/revisionPreliminar/tecnicoRevision.jsf");
	}
	
	public void cargarDatos() throws ServiceException {
		verProyectoRcoaBean.getProyectosBean().setProyectoRcoa(proyecto);
		verProyectoRcoaBean.cargarDatos();
	}
	
	public void validarExisteObservacionesInformacionRegistro() {
		try {
			existeObservacionRegistroPreliminar = false;
			// verifico las observaciones de informacion preliminar
			List<ObservacionesPreliminar> observacionesIRP = observacionesPreliminarFacade.listarPorIdClaseNombreClaseNoCorregidas(
					proyecto.getId(), "revisionRegistroInformacionPreliminar");
			
			if(observacionesIRP.size() > 0) {
				existeObservacionRegistroPreliminar = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
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
	
	public void validateDatos(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();		
		if(!esProyectoNuevo) {
			if(existeObservacionDiagnostico != null && existeObservacionDiagnostico) {
				try {
					List<ObservacionesPreliminar> observacionesOficio = observacionesPreliminarFacade.listarPorIdClaseNombreClaseNoCorregidas(
							proyecto.getId(), "observacionesDiagnosticoAmbiental");
					
					if(observacionesOficio.size() == 0) 
						errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe ingresar observaciones del Diagnóstico Ambiental .", null));
				} catch (Exception e) {
					e.printStackTrace();
					JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
				}
			}
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public Boolean habilitarObservacionesDiagnostico() {
		try {
			
			List<ObservacionesPreliminar> observacionesOficio = observacionesPreliminarFacade.listarPorIdClaseNombreClaseNoCorregidas(
					proyecto.getId(), "observacionesDiagnosticoAmbiental");
			
			if(observacionesOficio.size() > 0) 
				return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
		
		return false;
	}
	
	public void atras(){
		try{
			JsfUtil.redirectTo("/pages/rcoa/revisionPreliminar/tecnicoRevision.jsf");
		}catch(Exception e){
			Log.debug(e.toString());
		}
	}
	
	public void finalizar() {
		try {
			
			Map<String, Object> parametros = new HashMap<>();
			
			Boolean tramiteTieneObservaciones = false;
			if(esProyectoNuevo) {
				if(existeObservacionRegistroPreliminar || (esProyectoEnEjecucion != null && esProyectoEnEjecucion) || (esActualizacionAAA != null && esActualizacionAAA))
					tramiteTieneObservaciones = true;
				
				if(esProyectoEnEjecucion != null && esProyectoEnEjecucion)
					parametros.put("esProyectoEnEjecucion", true);
					
				parametros.put("observacionDiagnosticoAmbiental", esProyectoEnEjecucion);
			} else {
				if(existeObservacionRegistroPreliminar || (existeObservacionDiagnostico != null && existeObservacionDiagnostico) || (esActualizacionAAA != null && esActualizacionAAA))
					tramiteTieneObservaciones = true;
				
				parametros.put("observacionDiagnosticoAmbiental", existeObservacionDiagnostico);
			}
			
			parametros.put("sonObservacionsSubsanables", (sonObservacionesSubsanables == null) ? true : sonObservacionesSubsanables);
			
			if(tramiteTieneObservaciones && (sonObservacionesSubsanables == null || sonObservacionesSubsanables))
				parametros.put("numeroObservacionesDiagnostico", numeroObservaciones + 1);
			
			parametros.put("observacionesRevisionDiagnostico", tramiteTieneObservaciones);
			parametros.put("observacionAcualizacionAdministrativa", false); //parametros.put("observacionAcualizacionAdministrativa", esActualizacionAAA); //habilitar cuando este listo el proceso
			parametros.put("observacionIngresoInformacionPreliminar", existeObservacionRegistroPreliminar);
			
			proyectoLicenciaCoaFacade.guardar(proyecto);
			
			if (sonObservacionesSubsanables != null && sonObservacionesSubsanables && oficioPronunciamiento != null && oficioPronunciamiento.getId() != null) {
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
