package ec.gob.ambiente.rcoa.revisionPreliminar.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ObservacionesPreliminarFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ObservacionesPreliminar;
import ec.gob.ambiente.rcoa.model.PronunciamientoObservacionesNoSubsanables;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisarOficioPronunciamientoController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RevisarOficioPronunciamientoController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{oficioPronunciamientoBean}")
	@Getter
	@Setter
	private OficioPronunciamientoBean oficioPronunciamientoBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
    private ObservacionesPreliminarFacade observacionesPreliminarFacade;
	
	@EJB
	private DocumentosCoaFacade documentoFacade;
	
	@Getter
	@Setter
	private DocumentosCOA documentoManual;

	@Getter
	@Setter
	private Boolean requiereCorrecciones;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte, tipoOficio, nombreDocumentoFirmado;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private boolean token, subido, soloToken;
	
	@Getter
	@Setter
	private Boolean descargaroficio = false;
	
	@Getter
	@Setter
	private Boolean oficioGuardado;
	
	@Getter
	@Setter
	private String urlAlfresco;
	
	@PostConstruct
	private void iniciar() {
		try {
			
			urlAlfresco = "";
			oficioGuardado = false;
			
			soloToken = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
			
			verificaToken();
			
			oficioPronunciamientoBean.generarOficio(true);
			
			urlReporte = oficioPronunciamientoBean.getOficioPronunciamiento().getOficioPath();
			nombreReporte = oficioPronunciamientoBean.getOficioPronunciamiento().getNombreOficio();
			archivoReporte = oficioPronunciamientoBean.getOficioPronunciamiento().getArchivoOficio();
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		
		if(soloToken)
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
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
	
	public void validarExisteObservacionesOficio() {
		try {
			requiereCorrecciones = false;
			List<ObservacionesPreliminar> observacionesOficio = observacionesPreliminarFacade.listarPorIdClaseNombreClaseNoCorregidas(
					oficioPronunciamientoBean.getOficioPronunciamiento().getId(), "oficioObservacionesNoSubsanables");
			
			if(observacionesOficio.size() > 0) {
				requiereCorrecciones = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public void guardar() {
		oficioPronunciamientoBean.guardarOficio();
		oficioGuardado = true;
		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

	}
	
	public void subirDocumento() {
		try {
			oficioPronunciamientoBean.guardarDocumentosAlfresco();

			if (oficioPronunciamientoBean.getDocumentoOficioPronunciamiento() != null) {
				String documentOffice = documentoFacade.direccionDescarga(oficioPronunciamientoBean.getDocumentoOficioPronunciamiento());
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			LOG.error("Ocurrió un error al guardar el oficio", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		
		RequestContext.getCurrentInstance().update("formDialogs:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = oficioPronunciamientoBean.getOficioPronunciamiento().getArchivoOficio();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(oficioPronunciamientoBean.getOficioPronunciamiento().getNombreOficio());
			} else {
				JsfUtil.addMessageError("Error al generar el archivo");
			}
			descargaroficio = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargaroficio == true) {
			documentoManual=new DocumentosCOA();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombreDocumento(event.getFile().getFileName());
			documentoManual.setExtencionDocumento(".pdf");		
			documentoManual.setTipo("application/pdf");
			documentoManual.setNombreTabla(PronunciamientoObservacionesNoSubsanables.class.getSimpleName());
			documentoManual.setIdTabla(oficioPronunciamientoBean.getOficioPronunciamiento().getId());
			documentoManual.setProyectoLicenciaCoa(oficioPronunciamientoBean.getProyectoLicenciaCoa());
			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public String firmarDocumento() {
		try {
			if(oficioPronunciamientoBean.getDocumentoOficioPronunciamiento() != null) {
				String documentOffice = documentoFacade.direccionDescarga(oficioPronunciamientoBean.getDocumentoOficioPronunciamiento());
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			} 
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del oficio", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		return "";
	}
	
	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
			
			try {
				
				parametros.put("observacionesPronunciamiento", requiereCorrecciones);
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public void finalizar() {
		try {
			
			if (token) {
				String idAlfresco = oficioPronunciamientoBean.getDocumentoOficioPronunciamiento().getIdAlfresco();

				if (!documentoFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El oficio de pronunciamiento no está firmado electrónicamente.");
					return;
				}
			} else {
				if(subido) {
					documentoFacade.guardarDocumentoAlfresco(oficioPronunciamientoBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), 
							"INFORMACION_PRELIMINAR_PRONUNCIAMIENTO", 1L, documentoManual, oficioPronunciamientoBean.getTipoOficio());
				} else {
					JsfUtil.addMessageError("Debe adjuntar el oficio de pronunciamiento firmado.");
					return;
				}				
			}
			
			Map<String, Object> parametros = new HashMap<>();
			
			try {
				
				parametros.put("observacionesPronunciamiento", requiereCorrecciones);
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
}
