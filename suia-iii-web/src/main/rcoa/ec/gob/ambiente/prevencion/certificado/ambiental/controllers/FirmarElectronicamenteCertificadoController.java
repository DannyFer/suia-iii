package ec.gob.ambiente.prevencion.certificado.ambiental.controllers;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.kie.api.task.model.TaskSummary;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.DocumentosCertificadoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class FirmarElectronicamenteCertificadoController {
	
	private final Logger LOG = Logger.getLogger(FirmarElectronicamenteCertificadoController.class);
	
	@EJB
	private DocumentosCertificadoAmbientalFacade documentosCertificadoAmbientalFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private BandejaFacade bandejaFacade;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{certificadoAmbientalController}")
    private CertificadoAmbientalController certificadoAmbientalController;
    
    private Map<String, Object> variables;
	
	private String tramite;
		
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private DocumentoCertificadoAmbiental documentoCertificado, documentoManual;
	
	@Getter
	@Setter
	private boolean token;
	
	@Getter
	@Setter
	private Boolean documentoDescargado = false, documentoSubido = false;
	
	@Getter
	@Setter
	private boolean ambienteProduccion;
	
	@Getter
    @Setter
    public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	
	@PostConstruct
	public void init(){
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);	
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			token = true;
			if(!ambienteProduccion){
				verificaToken();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public boolean verificaToken() {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String firmarDocumento() {
		try {
			creaDocumento();
			
			if(documentoCertificado != null) {
				String documentOffice = documentosCertificadoAmbientalFacade.direccionDescarga(documentoCertificado);
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre()); 
			}
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del certificado", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}	
	
	public void validarTareaBpm() {
//      JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/v2/pagoServicios.jsf");
	}
	
	public void completarTarea() {
		try {
			if (token) {
				String idAlfrescoInforme = documentoCertificado.getAlfrescoId();

				if (!documentosCertificadoAmbientalFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}
				
				if(documentoCertificado.getIdProceso() == null) {
					documentoCertificado.setIdProceso(JsfUtil.getCurrentProcessInstanceId());
					documentosCertificadoAmbientalFacade.guardar(documentoCertificado);
				}

			} else {
				if (!documentoSubido) {
					JsfUtil.addMessageError("Debe adjuntar el certificado de intersección firmado.");
					return;
				} else  {
					documentoCertificado.setIdProceso(null);
					documentosCertificadoAmbientalFacade.guardar(documentoCertificado);
					
					documentoCertificado = documentosCertificadoAmbientalFacade
							.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(), "CertificadoAmbiental", 0L, documentoManual, TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_MAE);
				}
			}
			
			proyecto.setProyectoFinalizado(true);
			proyecto.setProyectoFechaFinalizado(new Date());			
			proyectoLicenciaCoaFacade.guardar(proyecto);
			
			documentoCertificado.setIdProceso(bandejaTareasBean.getProcessId());
			//documentoCertificado.setDescargaOperador(false);
			documentosCertificadoAmbientalFacade.guardar(documentoCertificado);
			
			Map<String, Object> parametros = new HashMap<>();			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			
			//para finalizar la tarea de encuesta
			List<TaskSummary> listaTareas = procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			if(listaTareas != null && listaTareas.size() > 0) {
				for (TaskSummary tarea : listaTareas) {
					String nombretarea = bandejaFacade.getNombreTarea(tarea.getId());
					if(nombretarea.equals("descargarCertificadoAmbiental")) {
						Usuario usuarioTarea = usuarioFacade.buscarUsuario(tarea.getActualOwner().getId());

						String fechaAvance = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS").format(new Date());

						Map<String, Object> parametrosE = new HashMap<>();
				
						parametrosE.put("finalizadoAutomaticoSisEncuestaCA", fechaAvance);
						
						procesoFacade.modificarVariablesProceso(usuarioTarea, bandejaTareasBean.getTarea().getProcessInstanceId(), parametrosE);

						procesoFacade.aprobarTarea(usuarioTarea, tarea.getId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
								
						break;
					} 
				}
			}
							
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			LOG.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (documentoDescargado == true) {
			documentoManual=new DocumentoCertificadoAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");		
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(ProyectoCertificadoAmbiental.class.getSimpleName());
			documentoManual.setIdTable(proyecto.getId());
			documentoSubido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}

	}

	public StreamedContent descargarDocumento() throws Exception {
		
		creaDocumento();
		
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documentoCertificado != null) {
			if (documentoCertificado.getContenidoDocumento() == null) {
				documentoCertificado.setContenidoDocumento(documentosCertificadoAmbientalFacade
						.descargar(documentoCertificado.getAlfrescoId()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documentoCertificado.getContenidoDocumento()), documentoCertificado.getExtesion());
			content.setName(documentoCertificado.getNombre());
		}
		
		documentoDescargado = true;
		return content;
	}
	
	private void creaDocumento(){
		try {			
			certificadoAmbientalController.enviarFicha(proyecto.getId(), 0L);
			
			String siglas=proyecto.getAreaResponsable().getTipoArea().getSiglas();			
			if(siglas.compareTo("DP")==0 || siglas.compareTo("PC")==0 || siglas.compareTo("ZONALES")==0 || siglas.compareTo("OT")==0){
				documentoCertificado = documentosCertificadoAmbientalFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoCertificadoAmbiental.class.getSimpleName(), TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_MAE).get(0);
			}else{
				documentoCertificado = documentosCertificadoAmbientalFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoCertificadoAmbiental.class.getSimpleName(), TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_GAD).get(0);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
