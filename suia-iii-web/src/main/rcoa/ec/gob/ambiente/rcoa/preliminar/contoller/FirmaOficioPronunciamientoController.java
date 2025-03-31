package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
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

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.PronunciamientoArchivacionProyectoFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
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
public class FirmaOficioPronunciamientoController implements Serializable{

	 private static final long serialVersionUID = -875087443147320594L;
	  
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;	
	
	@EJB
	private DocumentosCoaFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private PronunciamientoArchivacionProyectoFacade pronunciamientoProhibicionActividadFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;

	@Getter
	@Setter
	private DocumentosCOA documento,documentoManual,documentoMapa,documentoCertificado;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;

	@Getter
	@Setter
	private Boolean documentoDescargado = false, firmaSoloToken;
	
	@Getter
	@Setter
	private boolean token, documentoSubido, habilitarSeleccionToken, esZonificacionBiodiversidad,mapaDescargado,ciDescargado;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private String tramite ="", docuTableClass;
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
		
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		tramite = (String) variables.get("u_tramite");
		
		esZonificacionBiodiversidad =  Boolean.parseBoolean(variables.get("zonificacionBiodiversidad").toString()) ;
		
		habilitarSeleccionToken = true;
		tipoDocumento = TipoDocumentoSistema.RCOA_PRONUNCIAMIENTO_PROHIBICION_ACTIVIDAD;
		docuTableClass = "PronunciamientoProhibicionActividad";
		
		verificaToken();
		
		proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		
		List<DocumentosCOA> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), tipoDocumento, docuTableClass);
		if (listaDocumentosInt.size() > 0) {
			documento = listaDocumentosInt.get(0);
			
			if(documento.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre())
					&& JsfUtil.getSimpleDateFormat(documento.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date())) ) {
				if (documentosFacade.verificarFirmaVersion(documento.getIdAlfresco())) {
					JsfUtil.addMessageInfo("El documento ya está firmado electrónicamente, debe finalizar la tarea.");
					
					token = true;
					habilitarSeleccionToken = false;
				}
			} else {
				documento.setEstado(false);
				documentosFacade.guardar(documento);
				
				GeneracionDocumentosController oficioController = (GeneracionDocumentosController) BeanLocator.getInstance(GeneracionDocumentosController.class);
				
				documento = oficioController.generarOficioArchivacion(proyecto, esZonificacionBiodiversidad);
			}
		} else {
			GeneracionDocumentosController oficioController = (GeneracionDocumentosController) BeanLocator.getInstance(GeneracionDocumentosController.class);
			
			documento = oficioController.generarOficioArchivacion(proyecto, esZonificacionBiodiversidad);
		}
			
		GeneracionDocumentosController oficioController = (GeneracionDocumentosController) BeanLocator.getInstance(GeneracionDocumentosController.class);
		documentoCertificado = oficioController.generarCertificadoInterseccion(proyecto,usuarioFacade.buscarUsuario((String)variables.get("autoridadAmbiental")));
		
		List<DocumentosCOA> listaDocumentosMap = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA,"ProyectoLicenciaCoa");		
		if (listaDocumentosMap.size() > 0) {
			documentoMapa = listaDocumentosMap.get(0);
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
	
	public StreamedContent getStream() throws Exception {
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
		documentoDescargado = true;
		return content;

	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (documentoDescargado == true) {
			documentoManual=new DocumentosCOA();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombreDocumento(event.getFile().getFileName());
			documentoManual.setExtencionDocumento(".pdf");		
			documentoManual.setTipo("application/pdf");
			documentoManual.setNombreTabla(docuTableClass);
			documentoManual.setIdTabla(proyecto.getId());
			documentoManual.setProyectoLicenciaCoa(proyecto);
			documentoSubido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}

	}
	
	public String firmarDocumento() {
		try {
			String documentOffice = documentosFacade.direccionDescarga(documento);
			return DigitalSign.sign(documentOffice, loginBean.getUsuario().getNombre());
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}
	
	public void completarTarea() {
		try {
			
//			if (!mapaDescargado || !ciDescargado) {
//				if (!mapaDescargado)
//					JsfUtil.addMessageError("Debe descargar el Mapa de certificado de intersección.");
//
//				if (!ciDescargado)
//					JsfUtil.addMessageError("Debe descargar el Certificado de intersección.");				
//				
//				return;
//			}
			
			if (token) {
				String idAlfrescoInforme = documento.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}
			} else {
				if(documentoSubido) {
					documento = documentosFacade
							.guardarDocumentoAlfresco(tramite, "INFORMACION_PRELIMINAR", 0L, documentoManual, tipoDocumento);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el certificado de intersección firmado.");
					return;
				}				
			}
			
			//enviar notificacion al operador
			String nombreOperador = usuarioFacade.recuperarNombreOperador(proyecto.getUsuario()).get(0);
			
			Object[] parametrosCorreoTecnicos = new Object[] {nombreOperador};
			String notificacion = mensajeNotificacionFacade
					.recuperarValorMensajeNotificacion(
							"bodyNotificacionPronunciamientoProibicionActividad",
							parametrosCorreoTecnicos);

			Email.sendEmail(proyecto.getUsuario(), "Regularización Ambiental Nacional", notificacion, tramite, loginBean.getUsuario());

			try {
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				
				documentoCertificado.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
				documentosFacade.guardar(documentoCertificado);
				documentoMapa.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
				documentosFacade.guardar(documentoMapa);

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			} catch (JbpmException e) {
				JsfUtil.addMessageError("Error al realizar la operación.");
			}


		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public StreamedContent descargarMapa() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
				
			if (documentoMapa != null && documentoMapa.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoMapa.getIdAlfresco());
			}
			
			if (documentoMapa != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoMapa.getNombreDocumento());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			mapaDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public StreamedContent descargarCertificadoInterseccion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
				
			if (documentoCertificado != null && documentoCertificado.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoCertificado.getIdAlfresco());
			}
			
			if (documentoCertificado != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoCertificado.getNombreDocumento());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			ciDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
}
