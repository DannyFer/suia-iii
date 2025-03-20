package ec.gob.ambiente.rcoa.viabilidadAmbientalSnap.controllers;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.CabeceraFormulario;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PreguntaFormulario;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarInformeViabilidadSnapController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{generarInformeViabilidadSnapBean}")
	@Getter
	@Setter
	private GenerarInformeViabilidadSnapBean generarInformeViabilidadSnapBean;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
    private ProcesoFacade procesoFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoInformeAlfresco;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private Boolean informeGuardado, habilitarEnviar, esProduccion, esCorreccion;
	
	@Getter
	@Setter
	private String urlAlfresco;
	
	@Getter
	@Setter
	private Integer numeroRevision;

	@Getter
	@Setter
	private byte[] archivoInforme;
	
	private Map<String, Object> variables;

	@PostConstruct
	private void iniciar() {
		try {
			variables = generarInformeViabilidadSnapBean.getVariables();

			esCorreccion = variables.containsKey("existeObservacionesInforme") ? (Boolean
					.valueOf((String) variables.get("existeObservacionesInforme"))) : false;

			numeroRevision = variables.containsKey("numeroRevision") ? (Integer
					.valueOf((String) variables.get("numeroRevision"))) : 1;
			
			esProduccion = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
						
			generarInformeViabilidadSnapBean.generarInforme(true);
			generarInformeViabilidadSnapBean.setCamposSoloLectura(false);
			
			informeGuardado = false;
			habilitarEnviar = false;
			
			viabilidadProyecto = generarInformeViabilidadSnapBean.getViabilidadProyecto();
			
			proyectoLicenciaCoa = generarInformeViabilidadSnapBean.getProyectoLicenciaCoa();
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al iniciar la tarea.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalSnap/elaborarInforme.jsf");
	}
	
	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (archivoInforme != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					archivoInforme), "application/octet-stream");
			content.setName(generarInformeViabilidadSnapBean.getInformeInspeccion().getNombreReporte());
		}
		return content;
	}
	
	public void validateDatosIngreso(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(generarInformeViabilidadSnapBean.getInformeInspeccion().getFechaInspeccion()== null)
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Fecha inspección de campo' es requerido", null));
		
		for (CabeceraFormulario cabecera : generarInformeViabilidadSnapBean.getListaZonasAreas()) {
			Boolean zonaSeleccionada = false;
			for (PreguntaFormulario preguntaForm : cabecera.getListaPreguntas()) {
				if(preguntaForm.getRespuesta() != null && preguntaForm.getRespuesta().getRespBoolean() != null && preguntaForm.getRespuesta().getRespBoolean()) {
					zonaSeleccionada = true;
				}
			}
			
			if(!zonaSeleccionada) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"El campo 'Zonas del área protegida' es requerido", null));
			}
		}
		
		for (CabeceraFormulario cabecera : generarInformeViabilidadSnapBean.getPreguntasZonificacion()) {
			Boolean existePreguntaTipo3 = false;
			Boolean respuestaPreguntaTipo3 = false;
			for (PreguntaFormulario preguntaForm : cabecera.getListaPreguntas()) {
				if(preguntaForm.getTipo().equals(3)) {
					existePreguntaTipo3 = true;
					if(preguntaForm.getRespuesta() != null
						&& preguntaForm.getRespuesta().getRespBoolean() != null) {
						respuestaPreguntaTipo3 = true;
						break;
					}
				}
			}
			
			if(existePreguntaTipo3 && !respuestaPreguntaTipo3) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Debe seleccionar una opción para el campo '" + cabecera.getDescripcion()+ "'.", null));
			}
		}
		
		for (CabeceraFormulario cabecera : generarInformeViabilidadSnapBean.getPreguntasInspeccion()) {
			Boolean existePreguntaTipo3 = false;
			Boolean respuestaPreguntaTipo3 = false;
			for (PreguntaFormulario preguntaForm : cabecera.getListaPreguntas()) {
				if(preguntaForm.getTipo().equals(3)) {
					existePreguntaTipo3 = true;
					if(preguntaForm.getRespuesta() != null
						&& preguntaForm.getRespuesta().getRespBoolean() != null) {
						respuestaPreguntaTipo3 = true;
						break;
					}
				}
			}
			
			if(existePreguntaTipo3 && !respuestaPreguntaTipo3) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Debe seleccionar una opción para el campo '" + cabecera.getDescripcion()+ "'.", null));
			}
		}
		
		if(context.getMessageList().size() > 0 || errorMessages.size() > 0)
			informeGuardado = false;
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void guardarInforme() {
		try{
			generarInformeViabilidadSnapBean.guardarInforme();
			
			informeGuardado = true;
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void guardarDocumentos() {
		try {
			documentoInformeAlfresco = generarInformeViabilidadSnapBean.guardarDocumentos();
			
			if(documentoInformeAlfresco != null && documentoInformeAlfresco.getId() != null) {
				habilitarEnviar = true;
				String documentOffice = documentosFacade.direccionDescarga(documentoInformeAlfresco);
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		
		RequestContext.getCurrentInstance().update("formDialogFirma");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlgConfirmar').hide();");
        context.execute("PF('signDialog').show();");
	}
	
	public void cancelarFirma() {
		informeGuardado = false;
		
		RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlgConfirmar').hide();");
	}

	public void aceptar() {
		try {
			String idAlfresco = documentoInformeAlfresco.getIdAlfresco();

			if (esProduccion && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
				JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
				return;
			}
			
			try {
				Map<String, Object> parametros = new HashMap<>();
				
				if(generarInformeViabilidadSnapBean.getAreasInterseca().size() > 0) {
					parametros.put("intersecaVariasAreas", true);
					parametros.put("nroFirmasAdicionalesInforme", 0);
					
					parametros.put("otroResponsableAP", generarInformeViabilidadSnapBean.getAreasInterseca().get(0).getTecnicoResponsable().getNombre());
				} else {
					parametros.put("intersecaVariasAreas", false);
				}
				
				if(!generarInformeViabilidadSnapBean.getVariables().containsKey("numeroRevision")) {
					parametros.put("numeroRevision", 1);
				}
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				if(generarInformeViabilidadSnapBean.getAreasInterseca().size() > 0) {
					notificarInformeJefeArea(generarInformeViabilidadSnapBean.getAreasInterseca().get(0).getTecnicoResponsable());
				}

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				
			} catch (Exception e) {
				e.printStackTrace();
				JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public void notificarInformeJefeArea(Usuario tecnicoResponsable) throws ServiceException {
		Object[] parametrosCorreo = new Object[] {tecnicoResponsable.getPersona().getNombre(),
				proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), 
				JsfUtil.getLoggedUser().getPersona().getNombre(), viabilidadProyecto.getAreaSnap().getNombreAreaCompleto()};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionViabilidadSnapRevisarInforme");
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Email.sendEmail(tecnicoResponsable, mensajeNotificacion.getAsunto(), notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
	}
	
}
