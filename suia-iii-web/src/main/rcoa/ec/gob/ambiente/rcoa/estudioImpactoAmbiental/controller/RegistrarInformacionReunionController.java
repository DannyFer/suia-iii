package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.AclaracionProrrogaEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.AclaracionProrrogaEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.util.NotificacionInternaUtil;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RegistrarInformacionReunionController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	
	@EJB
	private AclaracionProrrogaEsIAFacade aclaracionProrrogaEsIAFacade;
	
	@EJB
	private DocumentosImpactoEstudioFacade documentosFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private InformacionProyectoEia esiaProyecto;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoRespaldo;
	
	@Getter
	@Setter
	private AclaracionProrrogaEsIA aclaracionProrrogaEsIA;
	
	@Getter
	@Setter
	private Integer idProyecto;
	
	@Getter
	@Setter
	private Date fechaReunion, horaReunion;
	
	@Getter
	@Setter
	private String lugarReunion;
	
	@Getter
	@Setter
	private Boolean reunionConfirmada;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			String idProyectoString = (String) variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);

			proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
			
			aclaracionProrrogaEsIA = aclaracionProrrogaEsIAFacade.getPorEstudio(esiaProyecto.getId());
			
			reunionConfirmada = false;
			if(aclaracionProrrogaEsIA == null)
				aclaracionProrrogaEsIA = new AclaracionProrrogaEsIA();
			else  
				reunionConfirmada = (aclaracionProrrogaEsIA.getFechaReunion() != null) ? true : false;
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/estudioImpactoAmbiental/registrarInformacionReunion.jsf");
	}
	
	public void uploadRespaldo(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		
		documentoRespaldo = new DocumentoEstudioImpacto();
		documentoRespaldo.setContenidoDocumento(contenidoDocumento);
		documentoRespaldo.setNombre(event.getFile().getFileName());
		documentoRespaldo.setExtesion(".pdf");		
		documentoRespaldo.setMime("application/pdf");
		documentoRespaldo.setNombreTabla("RespaldoReunionAclaratoria");
		documentoRespaldo.setIdTable(esiaProyecto.getId());
		documentoRespaldo.setIdProceso(bandejaTareasBean.getProcessId());
	
		JsfUtil.addMessageInfo("Documento subido exitosamente");
		
	}
	
	public void guardarInfoReunion() {
		try {
			
			SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
		    String hora = localDateFormat.format(horaReunion);
		    
		    aclaracionProrrogaEsIA.setHoraReunion(hora);
			aclaracionProrrogaEsIA.setIdEstudio(esiaProyecto.getId());
			
			aclaracionProrrogaEsIAFacade.guardar(aclaracionProrrogaEsIA);
			
			//enviar notificacion al operador
			String nombreOperador = usuarioFacade.recuperarNombreOperador(proyecto.getUsuario()).get(0);
			
			DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.MEDIUM, new Locale("es"));
			
			Object[] parametrosCorreo = new Object[] {nombreOperador, proyecto.getCodigoUnicoAmbiental(), 
					proyecto.getNombreProyecto(), formatoFecha.format(aclaracionProrrogaEsIA.getFechaReunion()), 
					aclaracionProrrogaEsIA.getHoraReunion(), aclaracionProrrogaEsIA.getLugarReunion()};
			String notificacion = mensajeNotificacionFacade
					.recuperarValorMensajeNotificacion(
							"bodyNotificacionRegistroFechaReunionEsIA",
							parametrosCorreo);

			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			Email.sendEmail(proyecto.getUsuario(), "Regularización Ambiental Nacional", notificacion, proyecto.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
			
			NotificacionInternaUtil.remitirNotificacionesEstudio(proyecto, "Regularización Ambiental Nacional", notificacion, null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void validateDatos(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(aclaracionProrrogaEsIA.getReunionRealizada() != null) {
			String respaldo = (aclaracionProrrogaEsIA.getReunionRealizada()) ? "Adjuntar Acta de Reunión aclaratoria" : "Adjuntar justificación";
			if (aclaracionProrrogaEsIA.getReunionRealizada() != null && (documentoRespaldo == null || documentoRespaldo.getContenidoDocumento() == null))
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "El campo '" + respaldo + "' es requerido.", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void limpiarDocumento() {
		documentoRespaldo = null;
	}
	
	public void finalizar() {
		try {
			
			TipoDocumentoSistema tipo = (aclaracionProrrogaEsIA.getReunionRealizada()) ? TipoDocumentoSistema.EIA_ACTA_REUNION : TipoDocumentoSistema.EIA_JUSTIFICACION_REUNION;
			
			documentoRespaldo = documentosFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(), 
					"RESPALDOS_REUNION", documentoRespaldo, tipo);
			
			if(documentoRespaldo != null && documentoRespaldo.getId() != null) {			
				aclaracionProrrogaEsIAFacade.guardar(aclaracionProrrogaEsIA);
	
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} else 
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
}
