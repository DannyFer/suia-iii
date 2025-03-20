package ec.gob.ambiente.proyectos.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jfree.util.Log;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.proyectos.bean.ResumenYEstadosEtapasBean;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.proyectos.datamodel.LazyProjectArchivedDataModel;
import ec.gob.ambiente.rcoa.facade.EncuestaFacade;
import ec.gob.ambiente.rcoa.facade.OpcionesEncuestaFacade;
import ec.gob.ambiente.rcoa.facade.ProcesosArchivadosFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.EncuestaProyecto;
import ec.gob.ambiente.rcoa.model.OpcionesEncuesta;
import ec.gob.ambiente.rcoa.model.ProcesosArchivados;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.proyecto.controller.ResumenYEstadosEtapasRcoaBean;
import ec.gob.ambiente.rcoa.proyecto.controller.VerProyectoRcoaBean;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.catalogocategoriasflujo.facade.CatalogoCategoriasFlujoFacade;
import ec.gob.ambiente.suia.comparator.OrdenarTareaPorEstadoComparator;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.CategoriaFlujo;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Flujo;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.dto.ResumeTarea;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.integracion.bean.ContenidoExterno;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.survey.SurveyAcceptance;
import ec.gob.ambiente.suia.survey.SurveyAcceptanceFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorObjetosDominioUtil;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.domain.EnvioNotificacionesMail;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;
import ec.gob.ambiente.suia.notificaciones.facade.EnvioNotificacionesMailFacade;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class ProyectosController implements Serializable {

	private static final long serialVersionUID = -4523371587113629686L;

	private static final Logger LOG = Logger.getLogger(ProyectosController.class);
	private static final String COMPLETADA = "Completada";
    static final Logger LOGGER = Logger.getLogger(ResumenYEstadosEtapasRcoaBean.class);
    static final String COMPLETADO = "Completado";
    static final String ENCURSO = "En curso";
    static final String ACTIVO = "Activo";
    static final String NOFAVORABLE = "No Favorable";

	// EJB
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private OpcionesEncuestaFacade opcionesEncuestaFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private EnvioNotificacionesMailFacade envioNotificacionesMailFacade;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;

	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;

	@EJB
	private SurveyResponseFacade surveyResponseFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private SurveyAcceptanceFacade surveyAcceptanceFacade;

	@EJB
	private EncuestaFacade encuestaFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	
	@EJB 
	private ProcesosArchivadosFacade procesosArchivadosFacade;

	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;

	// GETTERS & SETTERS

	@Getter
	@Setter
	private OpcionesEncuesta opcionesEncuesta;

	@Getter
	@Setter
	private EncuestaProyecto encuesta;

	@Getter
	@Setter
	private List<EncuestaProyecto> listEncuesta;

	@Getter
	@Setter
	private String fechaEncuesta;

	@Getter
	@Setter
	private boolean habilitarEstadosEtapas;

	@Getter
	@Setter
	private boolean deletionActive;

	@Getter
	@Setter
	private boolean updateSuiaActive;

	@Getter
	@Setter
	private boolean showSurveyG, mostrarEncuesta, habilitarGuardar;

	@Getter
	private List<OpcionesEncuesta> listaOpcionesEncuesta;

	@Getter
	@Setter
	ProyectoLicenciaCoa proyectoRcoaEncuesta;

	@Getter
	@Setter
	ProyectoLicenciamientoAmbiental proyectoEncuesta;

	@Getter
	@Setter
	private SurveyAcceptance aceptacion = new SurveyAcceptance();

	@Getter
	@Setter
	private EnvioNotificacionesMail EnvioNotificacionesMail;

	public static String surveyLink = Constantes.getPropertyAsString("suia.survey.link");

	String codigoProyecto;

	String autoridadAmbiental;

	@Getter
	@Setter
	Boolean esSujetoDeControl;

	@Getter
	@Setter
	Boolean esDireccionProvincial;

	@Getter
	@Setter
	String emailAutoridad;

	@Getter
	@Setter
	String emailOperador;

	@Getter
	@Setter
	Boolean existeEncuesta;

	@Getter
	@Setter
	Boolean proyectoFinalizado;

	@Getter
	@Setter
	List<CategoriaFlujo> flujos;

	@Getter
	@Setter
	Boolean proyectoObservado;
		
	@PostConstruct
	private void init() {
		try {
			
			proyectosBean.setEsArchivado(null);

			proyectosBean
					.setProyectos(proyectoLicenciamientoAmbientalFacade.getAllProjectsByUser(JsfUtil.getLoggedUser()));
			proyectosBean.setProyectosNoFinalizados(
					proyectoLicenciamientoAmbientalFacade.getAllProjectsUnfinalizedByUser(JsfUtil.getLoggedUser()));
			
			proyectosBean.setProyectosArchivadosLazy(new LazyProjectArchivedDataModel(JsfUtil.getLoggedUser()));

			List<ProyectoCustom> proyectosRcoaNoFinalizados = proyectoLicenciamientoAmbientalFacade
					.listarProyectosCoaNoFinalizados(JsfUtil.getLoggedUser());
			proyectosBean.getProyectosNoFinalizados().addAll(proyectosRcoaNoFinalizados);

			habilitarGuardar = false;

			listaOpcionesEncuesta = opcionesEncuestaFacade.obtenerCatalogoOpciones();
			

			encuesta = new EncuestaProyecto();
			this.fechaEncuesta = EncuestaFacade.fechaEncuesta();

			esSujetoDeControl = JsfUtil.getLoggedUser().isUserInRole(JsfUtil.getLoggedUser(), "sujeto de control");

			habilitarEstadosEtapas = false;
			deletionActive = Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin")
					|| Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL")
					|| /*
						 * Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL2 MAE") ||
						 */ Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL MAE");
			updateSuiaActive = Usuario.isUserInRole(JsfUtil.getLoggedUser(),
					"TÉCNICO REASIGNACIÓN COORDINADOR PROVINCIAL")
					|| Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin");
		} catch (Exception e) {
			LOG.error("Error cargando proyectos", e);
		}
	}

	public void consultaEncuesta(ProyectoCustom proyectoCustom) {
		
		listEncuesta = new ArrayList<EncuestaProyecto>();
		proyectoRcoaEncuesta = new ProyectoLicenciaCoa();
		proyectoEncuesta = new ProyectoLicenciamientoAmbiental();

		List<String> flujo = null;
		
			
		
		
		proyectoRcoaEncuesta = proyectoLicenciaCoaFacade.buscarProyecto(proyectoCustom.getCodigo());
		
		
		
		proyectoEncuesta = proyectoLicenciamientoAmbientalFacade
				.buscarProyectoPorCodigoCompleto(proyectoCustom.getCodigo());
		autoridadAmbiental = "";

		// para verificar si son proyectos Antiguos

		// SELECT encuestaFacade -> listEncuesta
		Usuario usuario = JsfUtil.getLoggedUser();
		if (proyectoRcoaEncuesta.getId() != null) {
			flujo = proyectoLicenciaCoaFacade.flujoProyecto(proyectoRcoaEncuesta.getCodigoUnicoAmbiental());
			listEncuesta = encuestaFacade.obtenerListaEncuestaProyectoRCOA(usuario.getId(),
					proyectoRcoaEncuesta.getId());
			proyectoFinalizado = proyectoRcoaEncuesta.getProyectoFinalizado();
			esDireccionProvincial = proyectoRcoaEncuesta.getAreaResponsable().getAreaName()
					.contains("DIRECCIÓN PROVINCIAL");

			if (!esDireccionProvincial) {

				autoridadAmbiental = proyectoLicenciaCoaFacade.AutoridadAmbientalProyecto(proyectoRcoaEncuesta);
			} else {
				JsfUtil.addMessageError(
						"El proyecto pertenece a una Dirección Provincial, No se pudo recuperar el correo electrónico de la Autoridad Ambiental ");
			}

			if (autoridadAmbiental == null) {
				JsfUtil.addMessageError("No se pudo recuperar el correo electrónico de la Autoridad Ambiental");
				autoridadAmbiental = "";
			}

		} else if (proyectoEncuesta != null) {
			flujo = proyectoLicenciaCoaFacade.flujoProyecto(proyectoEncuesta.getCodigo());
			listEncuesta = encuestaFacade.obtenerListaEncuestaProyecto(usuario.getId(), proyectoEncuesta.getId());
			proyectoFinalizado = proyectoEncuesta.isFinalizado();
			esDireccionProvincial = proyectoEncuesta.getAreaResponsable().getAreaName()
					.contains("DIRECCIÓN PROVINCIAL");

			if (!esDireccionProvincial) {
				autoridadAmbiental = proyectoLicenciamientoAmbientalFacade.AutoridadAmbientalProyecto(proyectoEncuesta);
			} else {
				JsfUtil.addMessageError(
						"El proyecto pertenece a una Dirección Provincial, No se pudo recuperar el correo electrónico de la Autoridad Ambiental");
			}

			if (autoridadAmbiental == null) {
				JsfUtil.addMessageError("No se pudo recuperar el correo electrónico de la Autoridad Ambiental");
				autoridadAmbiental = "";
			}

		}
		
		if (flujo != null && flujo.size() > 0) {
			flujo.remove("rcoa.RegistroPreliminar");
			flujo.remove("rcoa.RegistroPreliminar");
			if(flujo != null && flujo.size() > 0) {
				proyectoObservado = true;
			}
			else {
				proyectoObservado = false;
			}
		}
		

		codigoProyecto = proyectoCustom.getCodigo();

		if (listEncuesta.size() == 0) {
			for (OpcionesEncuesta opcionesEncuesta : listaOpcionesEncuesta) {
				EncuestaProyecto encuesta = new EncuestaProyecto();
				encuesta.setOpcionesEncuesta(opcionesEncuesta);
				encuesta.setValor(false);
				listEncuesta.add(encuesta);
			}
		}
	}

	public void asignarEncuesta(EncuestaProyecto encuesta) {
		encuesta.setUsuario(JsfUtil.getLoggedUser());
		encuesta.setProyectoLicenciaCoa((proyectoRcoaEncuesta.getId() == null) ? null : proyectoRcoaEncuesta);
		encuesta.setProyectoLicenciamientoAmbiental(proyectoEncuesta);
		habilitarGuardar = true;
	}

	public void cancelarEncuesta() {
		habilitarGuardar = false;

	}

	public String seleccionar(ProyectoCustom proyectoCustom) {
		switch (proyectoCustom.getSourceType()) {
		case ProyectoCustom.SOURCE_TYPE_INTERNAL:

			try {
				ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyectoCustom.getId()));
				proyectosBean.setProyectoToShow(proyecto);
				return JsfUtil.actionNavigateTo("/proyectos/resumenProyecto.jsf");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return null;
			}

		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_HIDROCARBUROS:
		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA:

			JsfUtil.getBean(ContenidoExterno.class).executeAction(proyectoCustom,
					IntegracionFacade.IntegrationActions.mostrar_dashboard);

			break;
		case ProyectoCustom.SOURCE_TYPE_DIGITALIZACION:
			try {
				JsfUtil.addMessageError("Proyecto, obra o actividad gestionado 100%  en físico, por favor visualizar la información en la columna Digitalizado");
				return null;
			} catch (Exception e) {
				LOG.error(e.getMessage());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return null;
			}
		case ProyectoCustom.SOURCE_TYPE_RCOA:

			try {
				Integer idProyecto = Integer.parseInt(proyectoCustom.getId());
				ProyectoLicenciaCoa proyectoRcoa = proyectoLicenciaCoaFacade.buscarProyectoPorIdWithOutFilters(idProyecto);
				proyectosBean.setProyectoRcoa(proyectoRcoa);
				
				proyectosBean.setEsArchivado(false);
				if(proyectoRcoa != null && proyectoRcoa.getId() != null && !proyectoRcoa.getEstado()) {
					if(proyectoRcoa.getRazonEliminacion().equals("ARCHIVADO")) {
						proyectosBean.setEsArchivado(true);
					} else {
						ProcesosArchivados procesoArchivo = procesosArchivadosFacade.getPorCodigoProyecto(proyectoRcoa.getCodigoUnicoAmbiental());
						if(procesoArchivo != null) {
							proyectosBean.setEsArchivado(true);
						}
					}
				}
								
				JsfUtil.getBean(VerProyectoRcoaBean.class).getProyectosBean().setProyectoRcoa(proyectoRcoa);
				JsfUtil.getBean(VerProyectoRcoaBean.class).cargarDatos();
				
				return JsfUtil.actionNavigateTo("/proyectos/resumenProyectoRcoa.jsf");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return null;
			}
		}

		return null;
	}
	
	public String mostrarDigitalizacion(ProyectoCustom proyectoCustom){
		AutorizacionAdministrativaAmbiental autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(proyectoCustom.getCodigo());
		/*0 = NUEVO
		1= BDD_FISICO
		2= BDD_CUATRO_CATEGORIAS
		3 = BDD_SECTOR_SUBSECTOR
		4 = REGULARIZACIÓN
		5 = RCOA';*/
		if(autorizacionAdministrativa != null && autorizacionAdministrativa.getId() != null){
			//JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoInformacionAAAVer.xhtml?codigo="+proyectoCustom.getCodigo());
			return "/pages/rcoa/digitalizacion/ingresoFormDigitalizacionVer.xhtml?codigo="+proyectoCustom.getCodigo();
		}
		return null;
	} 

	public String eliminarProyectoIntegracion(ProyectoCustom proyectoCustom) {
		switch (proyectoCustom.getSourceType()) {
		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA:

			JsfUtil.getBean(ContenidoExterno.class).executeAction(proyectoCustom,
					IntegracionFacade.IntegrationActions.eliminar_proyecto_II);

			break;
		}

		return null;
	}

	public String verProyectoNoFinalizado(ProyectoCustom proyecto) throws NumberFormatException, CmisAlfrescoException {
		if (proyecto.getSourceType().equals(ProyectoCustom.SOURCE_TYPE_RCOA)) {
			Integer idProyecto = Integer.parseInt(proyecto.getId());
			ProyectoLicenciaCoa proyectoCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			proyectosBean.setModificarProyectoRcoa(true);
			proyectosBean.setProyectoRcoa(proyectoCoa);
			return JsfUtil.actionNavigateTo("/pages/rcoa/preliminar/informacionPreliminar.jsf");
		}
		proyectosBean.setProyectoToEdit(proyectoLicenciamientoAmbientalFacade
				.buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyecto.getId())));
		return JsfUtil.actionNavigateTo("/prevencion/registroProyecto/verProyecto.jsf");
	}

	public void verificarProyectosNoFinalizados() {
		if (!proyectosBean.getProyectosNoFinalizados().isEmpty()) {
			JsfUtil.addCallbackParam("mostrarNoFinalizados");
		}
	}

	public void marcar(ProyectoCustom proyectoCustom) {
		switch (proyectoCustom.getSourceType()) {
		case ProyectoCustom.SOURCE_TYPE_INTERNAL:

			proyectoCustom.setMotivoEliminar("");
			proyectosBean.setProyectoCustom(proyectoCustom);

			break;

		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA:

			JsfUtil.getBean(ContenidoExterno.class).executeAction(proyectoCustom,
					IntegracionFacade.IntegrationActions.eliminar_proyecto);

			break;
		}
	}

	public void eliminar() {
		try {
			ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
					.buscarProyectosLicenciamientoAmbientalPorId(
							Integer.parseInt(proyectosBean.getProyectoCustom().getId()));

			Usuario director = usuarioFacade.getDirectorProvincialArea(proyecto.getAreaResponsable());
			if (director == null) {
				JsfUtil.addMessageError("No se ha podido identificar la autoridad responsable para este proyecto.");
				return;
			}

			String notificacionInicioProcesoTitle = "Se ha iniciado el proceso de eliminación del proyecto";
			String notificacionInicioProcesoBody = "Atendiendo a su solicitud, se ha dado inicio al proceso de eliminación de su proyecto: <br/><br/>"
					+ "<b>" + proyecto.getCodigo() + "</b><br/><br/>" + proyecto.getNombre() + ".<br/><br/>"
					+ "Usted recibirá en poco tiempo, por esta vía, el resultado de este proceso.";

			String notificacionSolicitudDirectorTitle = "El proyecto está pendiente para su eliminación";
			String notificacionSolicitudDirectorBody = "El proyecto está pendiente para su eliminación, por favor, revise la solicitud.";

			Map<String, Object> parametros = new HashMap<String, Object>();

			parametros.put(Constantes.ID_PROYECTO, proyecto.getId());
			parametros.put("codigoProyecto", proyecto.getCodigo());
			parametros.put("administrador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("proponente", proyecto.getUsuario().getNombre());
			parametros.put("director", director.getNombre());
			parametros.put("notificarInicioProcesoProponenteTitle", notificacionInicioProcesoTitle);
			parametros.put("notificarInicioProcesoProponenteBody", notificacionInicioProcesoBody);
			parametros.put("notificarSolicitudDirectorTitle", notificacionSolicitudDirectorTitle);
			parametros.put("notificarSolicitudDirectorBody", notificacionSolicitudDirectorBody);

			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.NOMBRE_PROCESO_ELIMINAR_PROYECTO,
					proyectosBean.getProyectoCustom().getCodigo(), parametros);
			proyecto.setMotivoEliminar(proyectosBean.getProyectoCustom().getMotivoEliminar());
			proyectoLicenciamientoAmbientalFacade.actualizarSimpleProyecto(proyecto);

			JsfUtil.addMessageInfo("Se ha iniciado el proceso de eliminación del proyecto.");

			JsfUtil.addCallbackParam("eliminarProyecto");
		} catch (Exception ex) {
			LOG.error(ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
		}
	}

	public void iniciarProceso(Flujo flujo) {
		try {
			JsfUtil.redirectTo(flujo.getUrlInicioFlujo() + "?flujoId=" + flujo.getId());
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void verTareas(Long processInstanceId, String estadoProceso) {
		try {
			List<Tarea> tareas = new ArrayList<Tarea>();
			if (COMPLETADA.equals(estadoProceso) || "Activo".equals(estadoProceso) || "Completado".equals(estadoProceso)
					|| "Completado - Observado (3)".equals(estadoProceso)) {
				List<ResumeTarea> resumeTareas = BeanLocator.getInstance(JbpmSuiaCustomServicesFacade.class)
						.getResumenTareas(processInstanceId);
				for (ResumeTarea resumeTarea : resumeTareas) {
					Tarea tarea = new Tarea();
					ConvertidorObjetosDominioUtil.convertirBamTaskSummaryATarea(resumeTarea, tarea);
					tareas.add(tarea);
				}
				Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());

				JsfUtil.getBean(ResumenYEstadosEtapasBean.class).setTareas(tareas);

			} else {
				List<TaskSummary> taskSummaries = procesoFacade.getTaskBySelectFlow(JsfUtil.getLoggedUser(),
						processInstanceId);
				int longitud = taskSummaries.size();
				for (int i = 0; i < longitud; i++) {
					Tarea tarea = new Tarea();
					ConvertidorObjetosDominioUtil.convertirTaskSummaryATarea(taskSummaries.get(i), tarea);
					tareas.add(tarea);
				}

				Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());

				JsfUtil.getBean(ResumenYEstadosEtapasBean.class).setTareas(tareas);

			}
		} catch (Exception exception) {
			exception.getMessage();
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION + exception.getMessage());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void verDocumentos(Long processInstanceId) {
		try {

			ProcessInstanceLog proceso = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(),
					processInstanceId);

			if (isSujetoControl(JsfUtil.getLoggedUser())) {
				List<Documento> documentosList = new ArrayList<Documento>();

				if (proceso.getProcessId().equals("suia.participacion-social")) {
					for (Documento documento : documentosFacade
							.recuperarDocumentosConArchivosPorFlujoTodasVersiones(processInstanceId)) {
						if (documento.getTipoDocumento().getId() != 1001) {
							documentosList.add(documento);
						}
					}
				} else {
					for (Documento documento : documentosFacade
							.recuperarDocumentosConArchivosPorFlujoTodasVersionesNombresUnicos(processInstanceId)) {
						if (documento.getTipoDocumento().getId() != 1001) {
							documentosList.add(documento);
						}

						// Informes tecnicos y adjuntos Biodiverssidad y Forestal en Certificado
						// Ambiental
						ArrayList<Integer> dotyIds = new ArrayList<Integer>() {
							{
								add(3300);
								add(3301);
								add(3302);
								add(3304);
							}
						};
						if (dotyIds.contains(documento.getTipoDocumento().getId())) {
							Map<String, Object> variables = procesoFacade
									.recuperarVariablesProceso(JsfUtil.getLoggedUser(), processInstanceId);
							boolean pronunciamientoFavorable = Boolean
									.valueOf((String) variables.get("pronunciamiento_favorable"));
							ProcessInstanceLog process = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(),
									processInstanceId);
							if (!pronunciamientoFavorable || process.getStatus() != 2)
								documentosList.remove(documento);
						}
					}
				}
				JsfUtil.getBean(ResumenYEstadosEtapasBean.class).setDocumentos(documentosList);
				JsfUtil.getBean(
						ResumenYEstadosEtapasBean.class).mostrarMsjAcuerdosCamaroneras = esProyectoAcuerdosCamaronerasOpcional();
			} else {
				if (proceso.getProcessId().equals("suia.participacion-social")) {
					JsfUtil.getBean(ResumenYEstadosEtapasBean.class).setDocumentos(
							documentosFacade.recuperarDocumentosConArchivosPorFlujoTodasVersiones(processInstanceId));
				} else {
					JsfUtil.getBean(ResumenYEstadosEtapasBean.class).setDocumentos(documentosFacade
							.recuperarDocumentosConArchivosPorFlujoTodasVersionesNombresUnicos(processInstanceId));
				}
			}

			verEncuesta(proceso.getProcessId());

		} catch (Exception e) {
			LOG.error("Error obteniendo documentos por proceso.", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public boolean isSujetoControl(Usuario usuario) {
		for (RolUsuario rolUsuario : usuario.getRolUsuarios()) {
			if (rolUsuario.getRol().getNombre().contains("sujeto")) {
				return true;
			}
		}
		return false;
	}

	public StreamedContent getStream(Documento documento) {
		if (documento.getContenidoDocumento() != null) {
			InputStream is = new ByteArrayInputStream(documento.getContenidoDocumento());
			return new DefaultStreamedContent(is, documento.getMime(), documento.getNombre());
		} else {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return null;
	}

	public boolean isProyectoSuia(ProyectoCustom proyectoCustom) {
		if (ProyectoCustom.SOURCE_TYPE_RCOA.equals(proyectoCustom.getSourceType())) {
			return false;
		} else {
			if (!proyectoCustom.isInternal() && !proyectoLicenciamientoAmbientalFacade
					.isProyectoLegadoHidrocarburos(proyectoCustom.getCodigo(), JsfUtil.getLoggedUser())) {
				proyectoCustom.setSourceType(ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA);
				return true;
			}
		}

		return false;
	}

	public Boolean esProyectoAcuerdosCamaronerasOpcional() {
		boolean resultado = false;
		try {
			Date fechaInicioCambio = new SimpleDateFormat("yyyy-MM-dd")
					.parse(Constantes.getFechaAcuerdosCamaronerasOpcional());
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaFin = formatter.parse(formatter.format(proyectosBean.getProyecto().getFechaRegistro()));

			if ((proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("11.03.04")
					|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("11.03.03"))
					&& fechaFin.compareTo(fechaInicioCambio) >= 0) {
				resultado = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return resultado;
	}

	public String urlLinkSurvey() throws ServiceException {
		try {
			Organizacion organizacion = new Organizacion();
			if (proyectosBean.getProyecto().getUsuario() != null
					&& proyectosBean.getProyecto().getUsuario().getPersona().getOrganizaciones().size() > 0) {
				organizacion = organizacionFacade.buscarPorRuc(proyectosBean.getProyecto().getUsuario().getNombre());
			} else {
				organizacion = null;
			}

			String url = surveyLink;
			String usuarioUrl = proyectosBean.getProyecto().getUsuario() == null ? ""
					: proyectosBean.getProyecto().getUsuario().getNombre();
			String proyectoUrl = proyectosBean.getProyecto().getCodigo();
			String appUlr = proceso;
			String tipoPerUrl = (organizacion != null) ? "juridico" : "natural";
			String tipoUsr = "externo";
			url = url + "/faces/index.xhtml?" + "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl
					+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
			System.out.println("enlace>>>" + url);
			return url;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private String proceso;

	public boolean verEncuesta(String nombreProceso) {
		aceptacion = new SurveyAcceptance();
		if (!verificacionEncuesta(proyectosBean.getProyecto().getCodigo(), nombreProceso)) {
			if (Usuario.isUserInRole(JsfUtil.getLoggedUser(), "sujeto de control")) {
				if (!surveyResponseFacade.findByProjectApp(proyectosBean.getProyecto().getCodigo(), nombreProceso)) {
					proceso = nombreProceso;
					mostrarEncuesta = true;
				} else {
					mostrarEncuesta = false;
				}
			} else
				mostrarEncuesta = false;
		} else {
			mostrarEncuesta = false;
		}

		return mostrarEncuesta;
	}

	public boolean verificacionEncuesta(String tramite, String proceso) {
		try {

			List<SurveyAcceptance> lista = surveyAcceptanceFacade.buscarAceptacion(tramite, proceso);

			if (lista != null && !lista.isEmpty()) {
				return true;
			} else
				return false;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public void guardarAceptacion() {
		try {

			aceptacion.setEstado(true);
			aceptacion.setFechaCreacion(new Date());
			aceptacion.setUsuarioCreacion(proyectosBean.getProyecto().getUsuario() == null ? ""
					: proyectosBean.getProyecto().getUsuario().getNombre());
			aceptacion.setUsuario(proyectosBean.getProyecto().getUsuario());
			aceptacion.setNombreProceso(proceso);
			aceptacion.setTramite(proyectosBean.getProyecto().getCodigo());

			surveyAcceptanceFacade.guardar(aceptacion);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void guardarEncuesta() {

		for (EncuestaProyecto encuesta : listEncuesta) {
			if (encuesta.getUsuario() == null) {
				encuesta.setUsuario(JsfUtil.getLoggedUser());
				encuesta.setProyectoLicenciaCoa((proyectoRcoaEncuesta.getId() == null) ? null : proyectoRcoaEncuesta);
				encuesta.setProyectoLicenciamientoAmbiental(proyectoEncuesta);

			}
			encuesta = encuestaFacade.guardar(encuesta);
		}

		JsfUtil.addMessageInfo("Consultas de Gestón Enviadas");

		notificacionEncuesta(JsfUtil.getLoggedUser());

		habilitarGuardar = false;
	}

	private void notificacionEncuesta(Usuario usuarioNotifica) {
		try {
			Usuario uOperador = JsfUtil.getLoggedUser();
			String nombreDestino = usuarioNotifica.getPersona().getNombre();

			boolean esOperador = Usuario.isUserInRole(JsfUtil.getLoggedUser(), "sujeto de control");

			List<String> pyrMostrar = new ArrayList<>();

			String pyr[] = { listEncuesta.get(0).getOpcionesEncuesta().getNombre().toString(),
					listEncuesta.get(0).getValor().toString(),
					listEncuesta.get(1).getOpcionesEncuesta().getNombre().toString(),
					listEncuesta.get(1).getValor().toString(),
					listEncuesta.get(2).getOpcionesEncuesta().getNombre().toString(),
					listEncuesta.get(2).getValor().toString() };

			for (int i = 0; i < (pyr.length); i++) {
				if (pyr[i] == "true") {
					pyrMostrar.add(pyr[i - 1]);
				}
			}
			int temp = pyrMostrar.size();
			for (int i = 0; i < (pyr.length / 2) - temp; i++) {
				pyrMostrar.add("");
			}

			String NombreProyecto = codigoProyecto;

			emailAutoridad = autoridadAmbiental;

			emailOperador = usuarioFacade.getEmailFromUser(usuarioNotifica.getNombre());

			String mensajeOperador = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
					"bodyNotificacionRevisarRespuestasEncuestaProyectos",
					new Object[] { "Estimado/a <strong>" + nombreDestino + "</strong>", esOperador
							? "Se envió sus consultas de Gestón a la Autoridad Ambiental competente del proyecto: "
							: "Usted ha recibido las consultas de Gestón registradas del proyecto: ", NombreProyecto,
							pyrMostrar.get(0), pyrMostrar.get(1), pyrMostrar.get(2) });

			Email.sendEmail(emailOperador, "Seguimiento del proyecto " + NombreProyecto,
					mensajeOperador  , codigoProyecto );

			String mensajeAutoridad = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
					"bodyNotificacionRevisarRespuestasEncuestaProyectos",
					new Object[] { "", !esOperador
							? "Se envió sus consultas de Gestón a la Autoridad Ambiental competente del proyecto: "
							: "Usted ha recibido las consultas de Gestón registradas del proyecto: ", NombreProyecto,
							pyrMostrar.get(0), pyrMostrar.get(1), pyrMostrar.get(2) });

			Email.sendEmail(emailAutoridad, "Seguimiento del proyecto " + NombreProyecto,
					mensajeAutoridad , codigoProyecto );

			EnvioNotificacionesMail envioNotificacionesMailOperardor = new EnvioNotificacionesMail();
			envioNotificacionesMailOperardor.setCodigoProyecto(codigoProyecto);
			envioNotificacionesMailOperardor.setEmail(emailOperador);
			envioNotificacionesMailOperardor.setAsunto("Seguimiento del proyecto " + NombreProyecto);
			envioNotificacionesMailOperardor.setContenido(mensajeOperador);
			envioNotificacionesMailOperardor.setEnviado(true);
			envioNotificacionesMailOperardor.setFechaEnvio(new Date());
			envioNotificacionesMailOperardor.setProcesoId(null);
			envioNotificacionesMailOperardor.setTareaId(null);
			envioNotificacionesMailOperardor.setUsuarioEnvioId(null);
			envioNotificacionesMailOperardor.setUsuarioDestinoId(uOperador.getId());
			envioNotificacionesMailOperardor.setNombreUsuarioDestino(uOperador.getNombre());
			envioNotificacionesMailFacade.save(envioNotificacionesMailOperardor);

			EnvioNotificacionesMail envioNotificacionesMailAutoridad = new EnvioNotificacionesMail();
			envioNotificacionesMailAutoridad.setCodigoProyecto(codigoProyecto);
			envioNotificacionesMailAutoridad.setEmail(emailAutoridad);
			envioNotificacionesMailAutoridad.setAsunto("Seguimiento del proyecto " + NombreProyecto);
			envioNotificacionesMailAutoridad.setContenido(mensajeAutoridad);
			envioNotificacionesMailAutoridad.setEnviado(true);
			envioNotificacionesMailAutoridad.setFechaEnvio(new Date());
			envioNotificacionesMailAutoridad.setProcesoId(null);
			envioNotificacionesMailAutoridad.setTareaId(null);
			envioNotificacionesMailAutoridad.setUsuarioEnvioId(null);
			envioNotificacionesMailAutoridad.setUsuarioDestinoId(uOperador.getId());
			envioNotificacionesMailOperardor.setNombreUsuarioDestino(uOperador.getNombre());
			envioNotificacionesMailFacade.save(envioNotificacionesMailAutoridad);

		} catch (Exception e) {
			LOG.error("No se envio la notificacion al usuario. " + e.getCause() + " " + e.getMessage());
		}
	}

}
