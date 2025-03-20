package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.OficioPronunciamientoPPCFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.OficioPronunciamientoPPC;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FirmarOficioArchivoController {
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentoRegistroAmbientalFacade;
	@EJB
	private OficioPronunciamientoPPCFacade oficioPronunciamientoPPCFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	private Map<String, Object> variables;
	
	private String tramite;
	
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
	private RegistroAmbientalRcoa registroAmbiental;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private String urlReporte;
	
	@Getter
	@Setter
	private OficioPronunciamientoPPC oficioPronunciamientoPPC;
	
	@Getter
	@Setter
	private Boolean documentoDescargado = false;
	
	@Getter
	@Setter
	private boolean token, documentoSubido, habilitarSeleccionToken, emitioRespuestaEnTiempo, pagoTerceraRevision, firmaSoloToken;
	
	@Getter
	@Setter
	private DocumentoRegistroAmbiental documento, documentoManual;
	
	@PostConstruct
	public void init(){
		try {
			
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
			
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
					
			registroAmbiental = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyecto);
			
			oficioPronunciamientoPPC = new OficioPronunciamientoPPC();
			
			List<OficioPronunciamientoPPC> listaPronunciamiento = oficioPronunciamientoPPCFacade.buscarPorRegistro(registroAmbiental.getId());
			
			if(listaPronunciamiento != null && !listaPronunciamiento.isEmpty()){
				oficioPronunciamientoPPC = listaPronunciamiento.get(0);
			}
						
			verificaToken();
			
			List<DocumentoRegistroAmbiental> listaDocumentos = documentoRegistroAmbientalFacade.documentoXTablaIdXIdDocLista(oficioPronunciamientoPPC.getId(),
							OficioPronunciamientoPPC.class.getSimpleName(),
							TipoDocumentoSistema.RA_PPC_OFICIO_ARCHIVO);
			
			generarDocumento();
			
			if(listaDocumentos != null && !listaDocumentos.isEmpty()){
				documento = listaDocumentos.get(0);
				
				if(!documento.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre()) 
						|| !JsfUtil.getSimpleDateFormat(documento.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
					documento.setEstado(false);
					documentoRegistroAmbientalFacade.guardar(documento);					
					generarDocumento();
					guardarDocumento();
					
				} else {
					if (documentoRegistroAmbientalFacade.verificarFirmaVersion(documento.getAlfrescoId())) {
						JsfUtil.addMessageInfo("El documento ya está firmado electrónicamente, debe finalizar la tarea.");
						
						token = true;
						habilitarSeleccionToken = false;
					}
				}
			}else{
				guardarDocumento();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
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
	
	public void generarDocumento(){
		try {
						
			GenerarDocumentoArchivoController generarDocumento = (GenerarDocumentoArchivoController)BeanLocator.getInstance(GenerarDocumentoArchivoController.class);
			oficioPronunciamientoPPC = generarDocumento.generarResolucionRegistroAmbiental(registroAmbiental, oficioPronunciamientoPPC,false);
			urlReporte = oficioPronunciamientoPPC.getUrl();	
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void guardarDocumento(){
		try {			
			documento = new DocumentoRegistroAmbiental();
			documento.setContenidoDocumento(oficioPronunciamientoPPC.getContenido());
			documento.setNombre(oficioPronunciamientoPPC.getNombreReporte());
			documento.setExtesion(".pdf");		
			documento.setMime("application/pdf");
			documento.setNombreTabla(OficioPronunciamientoPPC.class.getSimpleName());
			documento.setIdTable(oficioPronunciamientoPPC.getId());
			
			documento = documentoRegistroAmbientalFacade.guardarDocumentoAlfrescoCA(registroAmbiental.getProyectoCoa().getCodigoUnicoAmbiental(), 
					"REGISTRO AMBIENTAL PPC", documento, TipoDocumentoSistema.RA_PPC_OFICIO_ARCHIVO);	
			
		} catch (Exception e) {
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
	
	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentoRegistroAmbientalFacade
						.descargar(documento.getAlfrescoId()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getContenidoDocumento()), documento.getExtesion());
			content.setName(documento.getNombre());
		}
		documentoDescargado = true;
		return content;

	}
	
	public String firmarDocumento() {
		try {
			String documentOffice = documentoRegistroAmbientalFacade.direccionDescarga(documento);
			return DigitalSign.sign(documentOffice, loginBean.getUsuario().getNombre());
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (documentoDescargado == true) {
			documentoManual = new DocumentoRegistroAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(OficioPronunciamientoPPC.class.getSimpleName());
			documentoManual.setIdTable(oficioPronunciamientoPPC.getId());
			
			documentoSubido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public void completarTarea(){
		try {
			
			if (token) {
				String idAlfrescoInforme = documento.getAlfrescoId();

				if (!documentoRegistroAmbientalFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El oficio no está firmado electrónicamente.");
					return;
				}
			} else {
				if (documentoSubido) {					
					documento = documentoRegistroAmbientalFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(),
									"REGISTRO AMBIENTAL PPC", documentoManual,
									TipoDocumentoSistema.RA_PPC_OFICIO_ARCHIVO);

				} else {
					JsfUtil.addMessageError("Debe adjuntar el oficio firmado.");
					return;
				}			
			}			
			
			oficioPronunciamientoPPC.setFechaElaboracion(new Date());
			oficioPronunciamientoPPCFacade.guardiarOficioArchivo(oficioPronunciamientoPPC);
			
			documento.setIdProceso(bandejaTareasBean.getProcessId());
			documentoRegistroAmbientalFacade.guardar(documento);
			
			Map<String, Object> parametros = new HashMap<>();
						
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			
			notificacionOperador();
						
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
						
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void notificacionOperador(){
		try {
			String nombreOperador = "";
			Usuario usuarioOperador = registroAmbiental.getProyectoCoa().getUsuario();
			
			if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
				nombreOperador = usuarioOperador.getPersona().getNombre();
			} else {
				Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
				nombreOperador = organizacion.getNombre();
				usuarioOperador.getPersona().setContactos(organizacion.getContactos());
			}									
									
			Object[] parametrosCorreoNuevo = new Object[] {nombreOperador};			
					
			MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionRAPPCOficioArchivo");
			
			String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);
			
			List<DocumentoRegistroAmbiental> listaDocumentosInt = documentoRegistroAmbientalFacade
					.documentoXTablaIdXIdDocLista(
							oficioPronunciamientoPPC.getId(),
							OficioPronunciamientoPPC.class.getSimpleName(),
							TipoDocumentoSistema.RA_PPC_OFICIO_ARCHIVO);
			
			List<String> listaArchivos = new ArrayList<>();
			FileOutputStream file;
			String nombreArchivo = "PronunciamientoArchivo_" + proyecto.getCodigoUnicoAmbiental() + ".pdf";
			File fileArchivo = new File(System.getProperty("java.io.tmpdir")+"/"+nombreArchivo);
			
			try {
				file = new FileOutputStream(fileArchivo);
				file.write(documentoRegistroAmbientalFacade.descargar(listaDocumentosInt.get(0).getAlfrescoId()));
				file.close();
				
				listaArchivos.add(nombreArchivo);
			} catch (Exception e) {
				e.printStackTrace();
			}	
						
			Email.sendEmailAdjuntos(usuarioOperador, mensajeNotificacion.getAsunto(), notificacion, listaArchivos, registroAmbiental.getProyectoCoa().getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
