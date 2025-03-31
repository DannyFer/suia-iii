package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
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
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FirmaElectronicaController implements Serializable{

	  private static final long serialVersionUID = -875087443147320594L;
	
	
	@EJB
	private ProcesoFacade procesoFacade;	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private DocumentosCoaFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	private boolean token;

	@Getter
	@Setter
	private DocumentosCOA documentoMapa,documento,documentoManual;	

	@Getter
	@Setter
	private Boolean descargaroficio = false;

	@Getter
	@Setter
	private boolean subido;

	@Getter
	private boolean esPronunciamientoAprobacion;

	private String documentOffice = "";

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private String tramite ="";

	private int idproyecto=0;
	
	private CertificadoInterseccionOficioCoa oficioCI;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		tramite = (String) variables.get("u_tramite");
		idproyecto = Integer.valueOf((String) variables.get("u_idProyecto"));
		
		verificaToken();
		
		oficioCI=certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(tramite);
		proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		GeneracionDocumentosController oficioController = (GeneracionDocumentosController) BeanLocator.getInstance(GeneracionDocumentosController.class);
		
		documento = oficioController.generarCertificadoInterseccion(proyecto,usuarioFacade.buscarUsuario((String)variables.get("autoridadAmbiental")));

		List<DocumentosCOA> listaDocumentosMap = documentosFacade.documentoXTablaIdXIdDoc(idproyecto, TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA,"ProyectoLicenciaCoa");		
		if (listaDocumentosMap.size() > 0) {
			documentoMapa = listaDocumentosMap.get(0);
		}
	}

	public void completarTarea() {
		try {
			if(descargaroficio){
				documento = documentosFacade
						.guardarDocumentoAlfresco(tramite, "INFORMACION_PRELIMINAR", 0L, documentoManual,TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO);
			}
			if (token) {
				String idAlfrescoInforme = documento.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}			

			} else {
				if (!subido) {
					JsfUtil.addMessageError("Debe adjuntar el certificado de intersección firmado.");
					return;
				}				
			}

			try {
				
				documentoMapa.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
				documentosFacade.guardar(documentoMapa);
				
				List<DocumentosCOA> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDoc(oficioCI.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO,"CertificadoInterseccionOficioCoa");
				if (listaDocumentosInt.size() > 0) {
					DocumentosCOA documento = listaDocumentosInt.get(0);
					documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
					documentosFacade.guardar(documento);
				}
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			} catch (JbpmException e) {
				JsfUtil.addMessageError("Error al realizar la operación.");
			}


		} catch (ServiceException | CmisAlfrescoException e) {
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

	public void descargarOficio() {
		if (documento != null) {
			descargaroficio = true;
		}
	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargaroficio == true) {
			documentoManual=new DocumentosCOA();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombreDocumento(event.getFile().getFileName());
			documentoManual.setExtencionDocumento(".pdf");		
			documentoManual.setTipo("application/pdf");
			documentoManual.setNombreTabla(CertificadoInterseccionOficioCoa.class.getSimpleName());
			documentoManual.setIdTabla(oficioCI.getId());
			documentoManual.setProyectoLicenciaCoa(proyecto);
			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
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
		return content;

	}
	
	public StreamedContent getStreamMapa() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documentoMapa != null) {
			if (documentoMapa.getContenidoDocumento() == null) {
				documentoMapa.setContenidoDocumento(documentosFacade
						.descargar(documentoMapa.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documentoMapa.getContenidoDocumento()), documentoMapa.getExtencionDocumento());
			content.setName(documentoMapa.getNombreDocumento());
		}
		return content;

	}

	public String firmarDocumento() {
		try {
			documentOffice = documentosFacade.direccionDescarga(documento);
			DigitalSign firmaE = new DigitalSign();
			return firmaE.sign(documentOffice, loginBean.getUsuario().getNombre());
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}

	public void cancelar() {
		JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
	}
}