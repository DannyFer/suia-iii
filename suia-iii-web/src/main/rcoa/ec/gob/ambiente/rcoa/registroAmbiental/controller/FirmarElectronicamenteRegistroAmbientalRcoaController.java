package ec.gob.ambiente.rcoa.registroAmbiental.controller;

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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
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
public class FirmarElectronicamenteRegistroAmbientalRcoaController {
	
	private final Logger LOG = Logger.getLogger(FirmarElectronicamenteRegistroAmbientalRcoaController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
    @Getter
    @Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
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
    
    private Map<String, Object> variables;

	@Getter
	@Setter
	private String tramite;

	@Getter
	@Setter
	private boolean token, descargarRegistroAmbiental = false, subido = false;
		
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private DocumentoRegistroAmbiental documentoRegistroAmbiental, documentoManual;
	
	@PostConstruct
	public void init(){
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			documentoRegistroAmbiental = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.REGISTRO_AMBIENTAL_RCOA);
			verificaToken();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public String firmarDocumento() {
		try {
			if(documentoRegistroAmbiental != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoRegistroAmbiental);
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del certificado", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}	

	public void completarTarea() {
		try {				
			if (documentoRegistroAmbiental != null && documentoRegistroAmbiental.getId() != null) {
				if (token) {
					String idAlfrescoInforme = documentoRegistroAmbiental.getAlfrescoId();

					if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
						JsfUtil.addMessageError("El Certificado Ambiental  no está firmado electrónicamente.");
						return;
					}
				} else {
					if (!subido) {
						JsfUtil.addMessageError("Debe adjuntar el certificado de intersección firmado.");
						return;
					}
					if(descargarRegistroAmbiental){
						documentoRegistroAmbiental= documentosFacade.ingresarDocumento(documentoManual, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), TipoDocumentoSistema.REGISTRO_AMBIENTAL_RCOA, "Ficha_Ambiental.pdf", ProyectoLicenciaCoa.class.getSimpleName(), bandejaTareasBean.getProcessId());		
					}
				}
				String tienePago = (String)variables.get("tienePago");
				if(tienePago.equals("false")){
					Map<String, Object> parametros = new HashMap<>();
					// inicializo la variable de la autoridad ambiental
					List<Usuario> listaUsuario = usuarioFacade.buscarUsuarioPorRolNombreArea("AUTORIDAD AMBIENTAL",proyecto.getAreaResponsable().getAreaName() );
					if(listaUsuario != null && listaUsuario.size() > 0){
						String autoridad =(String)variables.get(Constantes.VARIABLE_PROCESO_AUTORIDADAMBIENTAL);
						// si es distinto guardo la nueva autoridad ambiental
						if(!autoridad.equals(listaUsuario.get(0).getNombre())){
							parametros.put("autoridadAmbiental", listaUsuario.get(0).getNombre());
							procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);	
						}
					}else{
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						return;
					}
				}
				try{
					procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				} catch (JbpmException e) {
					JsfUtil.addMessageError("Error al realizar la operación.");
				}
			} else {
				JsfUtil.addMessageError("Error al realizar la operación el oficio no está firmado.");
			}
		} catch (Exception e) {
			LOG.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
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
			e.printStackTrace();
		}
	}
	
	public StreamedContent getDocumentoRegistroA(){
		DescargarDocumentosCoaController descargarDocumentosCoaController = JsfUtil.getBean(DescargarDocumentosCoaController.class);
		StreamedContent file = descargarDocumentosCoaController.descargarDocumento(documentoRegistroAmbiental); 
		if(file != null){
			descargarRegistroAmbiental = true;
			return file;
		}
		return file; 
	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargarRegistroAmbiental == true) {
			documentoManual=new DocumentoRegistroAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");		
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
			documentoManual.setIdTable(proyecto.getId());
			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}

	}
}
