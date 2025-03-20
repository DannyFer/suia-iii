package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
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

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.EstadoAprobacionTdrCatalogo;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.persona.service.PersonaServiceBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class SubirDocumentosAprobacionTDRLAController implements Serializable {

	private static final long serialVersionUID = 1572525482381028668L;
	private static final Logger LOG = Logger
			.getLogger(SubirDocumentosAprobacionTDRLAController.class);

	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@EJB
	ContactoFacade contactoFacade;

	@EJB
	private PersonaServiceBean personaServiceBean;

	@Getter
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	private Documento respaldoAprobacionTdr;

	@Getter
	@Setter
	private String tipoPronunciamiento;

	@Getter
	@Setter
	private Boolean pronunciamientoTdrs;

	private Map<String, Object> processVariables;

	@Getter
	private String tecnicoResponsable;

	@Getter
	@Setter
	private List<EstadoAprobacionTdrCatalogo> estadosAprobacionTdr;

	@Getter
	@Setter
	private EstadoAprobacionTdrCatalogo pronunciamientoSeleccionado;

	@PostConstruct
	public void init() throws Exception {
		proyecto = proyectosBean.getProyecto();

		if (proyecto != null && proyecto.getId() != null) {
			processVariables = procesoFacade.recuperarVariablesProceso(JsfUtil
					.getLoggedUser(), bandejaTareasBean.getTarea()
					.getProcessInstanceId());
			tecnicoResponsable = (String) processVariables.get("u_TecnicoResponsable");
			
			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(JsfUtil.getLoggedUser(),
							bandejaTareasBean.getTarea().getProcessInstanceId());
			if (variables.containsKey("esPronunciamientoAprobacion")) {
				estadosAprobacionTdr = proyectoLicenciamientoAmbientalFacade.recuperarEstadosPorTipo(2);
			} else {
				estadosAprobacionTdr = proyectoLicenciamientoAmbientalFacade.recuperarEstadosPorTipo(1);
			}

		} else {
			// si no existe el proyecto en sesion redirige a la bandeja esPronunciamientoAprobacion
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}

	}

	public void adjuntarDocumento(FileUploadEvent event) {
		if (event != null) {
			StringBuilder functionJs = new StringBuilder();

			respaldoAprobacionTdr = this.uploadListener(event,
					ProyectoLicenciamientoAmbiental.class);
			functionJs
					.append("removeHighLightComponent('frmDatos:fileUploadRespaldos');");

			RequestContext.getCurrentInstance().execute(functionJs.toString());
			JsfUtil.addMessageInfo("El archivo "
					+ event.getFile().getFileName()
					+ " fue adjuntado correctamente.");
		}
	}

	private Documento uploadListener(FileUploadEvent event, Class<?> clazz) {
		String typeFile = event.getFile().getContentType();
		String extension = (typeFile.toLowerCase().contains("pdf")) ? "pdf"
				: "zip";

		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumento(contenidoDocumento, clazz,
				extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}

	/**
	 * Crea el documento
	 *
	 * @param contenidoDocumento
	 *            arreglo de bytes
	 * @param clazz
	 *            Clase a la cual se va a ligar al documento
	 * @param extension
	 *            extension del archivo
	 * @return Objeto de tipo Documento
	 */
	public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz,
			String extension) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setIdTable(0);
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf"
				: "application/zip");
		return documento;
	}

	public StreamedContent getStreamContent() throws Exception {

		Documento documento = null;

		respaldoAprobacionTdr = descargarAlfresco(respaldoAprobacionTdr);
		documento = respaldoAprobacionTdr;

		DefaultStreamedContent content = null;

		try {
			if (documento != null && documento.getNombre() != null
					&& documento.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documento.getContenidoDocumento()));
				content.setName(documento.getNombre());

			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}

	/**
	 * Descarga documento desde el Alfresco
	 *
	 * @param documento
	 * @return
	 * @throws CmisAlfrescoException
	 */
	public Documento descargarAlfresco(Documento documento)
			throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento
					.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}

	public String finalizarCarga() {
		try {
			if (!validarFinalizar())
				return "";

			pronunciamientoTdrs = pronunciamientoSeleccionado.getEstadoLogicoAprobacion();

			guardarDocumento();

			enviarNotificacion();

			// para aprobar tarea actual si es Descarga TDR
			if (pronunciamientoTdrs
					&& bandejaTareasBean.getTarea().getTaskName().equals("Descargar TDR"))
				avanzarTarea();

			proyecto.setIdEstadoAprobacionTdr(pronunciamientoSeleccionado.getId());
			proyectoLicenciamientoAmbientalFacade.actualizarProyecto(proyecto);

			proyectosBean = new ProyectosBean();

			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");

			return JsfUtil
					.actionNavigateTo("/prevencion/licenciamiento-ambiental/proyectosPendientesAprobacionTDR.jsf");
		} catch (Exception e) {
			LOG.error("Error al finalizar la carga de archivos.", e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
		}
		return "";
	}

	public boolean validarFinalizar() {
		List<String> mensajesError = new ArrayList<String>();
		StringBuilder functionJs = new StringBuilder();

		if (respaldoAprobacionTdr == null
				|| respaldoAprobacionTdr.getNombre() == null
				|| respaldoAprobacionTdr.getNombre().equals("")) {// NO VALIDO
			mensajesError.add("El archivo de respaldo es requerido.");
			functionJs
					.append("highlightComponent('frmDatos:fileUploadRespaldos');");
		} else {
			functionJs
					.append("removeHighLightComponent('frmDatos:fileUploadRespaldos');");
		}

		RequestContext.getCurrentInstance().execute(functionJs.toString());
		if (mensajesError.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(mensajesError);
			return false;
		}
	}

	public void guardarDocumento() {
		try {
			respaldoAprobacionTdr.setIdTable(this.proyecto.getId());
			respaldoAprobacionTdr.setDescripcion("Respaldos de aprobacion de TDRs");
			respaldoAprobacionTdr.setEstado(true);

			DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();

			documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
			documentoTarea.setProcessInstanceId(bandejaTareasBean.getTarea().getProcessInstanceId());

			TipoDocumentoSistema tipoDocumento;
			if (pronunciamientoTdrs) {
				tipoDocumento = TipoDocumentoSistema.TIPO_RESPALDOS_APROBACION_TDRS;
			} else {
				tipoDocumento = TipoDocumentoSistema.TIPO_RESPALDOS_OBSERVACION_TDRS;
			}

			documentosFacade.guardarDocumentoAlfresco(
					this.proyecto.getCodigo(), Constantes.CARPETA_EIA, 0L,
					respaldoAprobacionTdr, tipoDocumento, documentoTarea);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enviarNotificacion() throws Exception {
		NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();

		String[] parts = pronunciamientoSeleccionado.getNombre().split(" ");
		parts[0] = parts[0] + "s";
		String estadoTdrs = "";
		for (int i = 0; i < parts.length; i++) {
			estadoTdrs += parts[i] + " ";
		}
		String newEstadoTdrs = estadoTdrs.substring(0, estadoTdrs.length() - 1)
				.toUpperCase();

		String mensaje = "";
		if (pronunciamientoTdrs) {
			mensaje = "Los TDR's del proyecto " + proyecto.getCodigo()
					+ " se encuentran " + newEstadoTdrs
					+ " en el Sistema Único de Información Ambiental.";
		} else {
				mensaje = "Los TDR's del proyecto "
						+ proyecto.getCodigo()
						+ " se encuentran "
						+ newEstadoTdrs
						+ " en el Sistema Único de Información Ambiental. <br/>"
						+ "Utilice el siguiente enlace para ingresar la información solicitada."
						+ "<br/>"
						+ JsfUtil.devolverContexto("/mineriaTdrReestructurado.jsf?proyecto="+ proyecto.getId());
		}

		Map<String, String> listaDestinatarios = new HashMap<>();

		// notificacion para usuario
		List<Contacto> listContactoUser = contactoFacade
				.buscarPorPersona(proyecto.getUsuario().getPersona());
		for (int j = 0; j < listContactoUser.size(); j++) {
			if (listContactoUser.get(j).getFormasContacto().getId().equals(5)) {
				listaDestinatarios.put(proyectosBean.getProponente(),
						listContactoUser.get(j).getValor());
			}
		}

		// notificacion para el tecnico responsable del tramite
		if (tecnicoResponsable != null) {
			Persona tecnico = personaServiceBean.getPersona(tecnicoResponsable);
			List<Contacto> listContactoTecnico = contactoFacade
					.buscarPorPersona(tecnico);
			for (int j = 0; j < listContactoTecnico.size(); j++) {
				if (listContactoTecnico.get(j).getFormasContacto().getId()
						.equals(5)) {
					listaDestinatarios.put(tecnico.getNombre(),
							listContactoTecnico.get(j).getValor());
				}
			}
		}

		for (Map.Entry<String, String> entry : listaDestinatarios.entrySet()) {
			mail_a.sendEmailDocumentosTdrsMineria(entry.getValue(),
					" Aprobacion TDRS ", entry.getKey(), mensaje);

			Thread.sleep(2000);
		}
	}

	public void avanzarTarea() {
		try {
			if (bandejaTareasBean.getTarea().getTaskSummary().getCreatedBy() != null) {
				String usuarioTarea = bandejaTareasBean.getTarea()
						.getTaskSummary().getCreatedBy().getId();
				Usuario usuario = ((UsuarioServiceBean) BeanLocator
						.getInstance(UsuarioServiceBean.class))
						.buscarUsuarioWithOutFilters(usuarioTarea);
				procesoFacade.aprobarTarea(usuario, bandejaTareasBean
						.getTarea().getTaskId(), bandejaTareasBean.getTarea()
						.getProcessInstanceId(), null);
			}
		} catch (JbpmException e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}

}
