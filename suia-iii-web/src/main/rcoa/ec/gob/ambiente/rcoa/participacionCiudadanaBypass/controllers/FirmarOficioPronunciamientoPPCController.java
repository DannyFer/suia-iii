package ec.gob.ambiente.rcoa.participacionCiudadanaBypass.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.DocumentoPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;


@ManagedBean
@ViewScoped
public class FirmarOficioPronunciamientoPPCController {
	
	private static final Logger LOG = Logger.getLogger(FirmarOficioPronunciamientoPPCController.class);
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
    @ManagedProperty(value = "#{oficioPPCBypassBean}")
    private OficioPPCBypassBean oficioPPCBypassBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;

	@EJB
	private DocumentoPPCFacade documentosFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@Getter
	@Setter
	private DocumentosPPC documentoManual = new DocumentosPPC();
	
	@Getter
	@Setter
	private String urlAlfresco;

	@Getter
	@Setter
	private Boolean oficioGuardado;

	@Getter
	@Setter
	private Boolean descargaroficio = false;
	
	@Getter
	@Setter
	private boolean token, subido, documentoSubido, firmaSoloToken;

	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException {
		urlAlfresco = "";
		oficioGuardado = false;

		firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");

		verificaToken();

		oficioPPCBypassBean.generarOficio(true);
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/participacionCiudadanaBypass/firmarOficioPronunciamiento.jsf");
	}
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		
		if(firmaSoloToken)
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
	
	public StreamedContent getStreamOficio(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent),  "application/pdf");
			content.setName(name);
		}
		return content;
	}
	
	public void subirDocumento() {
		try {
			oficioPPCBypassBean.generarOficio(false);
			oficioPPCBypassBean.guardarDocumentoOficioAlfresco();

			if (oficioPPCBypassBean.getDocumentoOficio() != null) {
				String documentOffice = documentosFacade.direccionDescarga(oficioPPCBypassBean.getDocumentoOficio());
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
			
			byte[] documentoContent = oficioPPCBypassBean.getArchivoOficio();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent),  "application/pdf");
				content.setName(oficioPPCBypassBean.getNombreReporte());
			} else
				JsfUtil.addMessageError("Error al generar el archivo");
			
			descargaroficio = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargaroficio == true) {
			documentoManual = new DocumentosPPC();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombreDocumento(event.getFile().getFileName());
			documentoManual.setExtencionDocumento(".pdf");		
			documentoManual.setTipo("application/pdf");
			documentoManual.setNombreTabla("OficioPronunciamientoPPC");
			documentoManual.setIdTabla(oficioPPCBypassBean.getOficioPronunciamiento().getId());

			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public void completarTarea() {
		try {
			
			if (token) {
				String idAlfresco = oficioPPCBypassBean.getDocumentoOficio().getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El oficio de pronunciamiento no está firmado electrónicamente.");
					return;
				}
			} else {
				if(subido) {
					DocumentosPPC documento = documentosFacade.guardarDocumentoAlfresco(oficioPPCBypassBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), 
							"PARTICIPACIONCIUDADANA", 1L, documentoManual, oficioPPCBypassBean.getTipoDocumento());
					oficioPPCBypassBean.setDocumentoOficio(documento);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el oficio de pronunciamiento firmado.");
					return;
				}				
			}

			try {
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				
				//para finalizar la tarea de encuesta y descarga de pronunciamiento
				List<TaskSummary> listaTareas = procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
				if(listaTareas != null && listaTareas.size() > 0) {				
					for (TaskSummary tarea : listaTareas) {
						String nombretarea = bandejaFacade.getNombreTarea(tarea.getId());
						if(nombretarea.equals("recibirOficioPPCBypass")) {
							Usuario usuarioTarea = usuarioFacade.buscarUsuario(tarea.getActualOwner().getId());

							String fechaAvance = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS").format(new Date());

							Map<String, Object> param = new HashMap<>();
					
							param.put("finalizadoAutomaticoSisDescargaBypass", fechaAvance);
							
							procesoFacade.modificarVariablesProceso(usuarioTarea, bandejaTareasBean.getTarea().getProcessInstanceId(), param);

							procesoFacade.aprobarTarea(usuarioTarea, tarea.getId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
							
							oficioPPCBypassBean.getDocumentoOficio().setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
							documentosFacade.guardar(oficioPPCBypassBean.getDocumentoOficio());
							
							//iniciar proceso de Resolución de Licencia Ambiental para proyecto aprobados
							if (oficioPPCBypassBean.getEsAprobacion()) {
								Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
								parametros.put("tramite",  oficioPPCBypassBean.getTramite());					
								parametros.put("idProyecto", oficioPPCBypassBean.getIdProyecto());				
								parametros.put("operador",  (String) oficioPPCBypassBean.getVariables().get("operador"));			
								parametros.put("tecnicoResponsable", (String) oficioPPCBypassBean.getVariables().get("tecnicoResponsableEIA"));							
								parametros.put("esFisico", false);							
								try {
									Long idProceso = procesoFacade.iniciarProceso(usuarioTarea, Constantes.ROCA_PROCESO_RESOLUCION_LICENCIA_AMBIENTAL, oficioPPCBypassBean.getTramite(), parametros);
									System.out.println("proceso ResolucionLicenciaAmbiental ::::"+idProceso);
								} catch (JbpmException e) {
									e.printStackTrace();
								}
							}
							
							break;
						} 
					}
				}

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
