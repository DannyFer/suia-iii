package ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.DocumentoBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.ExpedienteBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.ReporteBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.DocumentoBPC;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ExpedienteBPC;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ReporteBPC;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.OficioPronunciamientoEsIAFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProcesosArchivadosFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.participacionCiudadana.dto.EntityOficioPPCBypass;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FirmarOficioPronunciamientoBPCController {

	private static final Logger LOG = Logger.getLogger(FirmarOficioPronunciamientoBPCController.class);
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private BandejaFacade bandejaFacade;
	@EJB
	private ReporteBPCFacade reporteBPCFacade;
	@EJB
	private ExpedienteBPCFacade expedienteBPCFacade;
	@EJB
	private DocumentoBPCFacade documentoBPCFacade;
	@EJB
	public CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProcesosArchivadosFacade procesosArchivadosFacade;
	@EJB
	private OficioPronunciamientoEsIAFacade oficioPronunciamientoEsIAFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;

	@Inject
	private UtilBPCBypass utilPPC;

	@Getter
	@Setter
	private DocumentoBPC documentoBPC = new DocumentoBPC();
	@Getter
	@Setter
	private DocumentoBPC documentoManual = new DocumentoBPC();
	@Getter
	@Setter
	private ExpedienteBPC expedienteBPC = new ExpedienteBPC();
	@Getter
	@Setter
	private ReporteBPC reporteBPC = new ReporteBPC();
	@Getter
	@Setter
	private EntityOficioPPCBypass entityOficio = new EntityOficioPPCBypass();
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	@Getter
	@Setter
	private Usuario usuarioOperador;
	@Getter
	@Setter
	private Area areaAutoridad;
	@Getter
	@Setter
	private CatalogoGeneralCoa tipoOficio;
	@Getter
	@Setter
	private Map<String, Object> variables;
	@Getter
	@Setter
	private File oficio;

	@Getter
	@Setter
	private String urlAlfresco;
	@Getter
	@Setter
	private Boolean oficioGuardado;
	@Getter
	@Setter
	private Boolean descargarOficio = false;
	@Getter
	@Setter
	private boolean token, subido, documentoSubido, firmaSoloToken;
	@Getter
	@Setter
	private String tramite, tecnicoResponsable, operador, docuTableClass;
	@Getter
	@Setter
	private Integer idProyecto, tipoDocumentoExpediente;
	@Getter
	@Setter
	private Boolean esAprobacion;
	@Getter
	@Setter
	private String oficioPath, nombreReporte;
	@Getter
	@Setter
	private byte[] archivoOficio;
	@Getter
	@Setter
	private String idProceso;

	// Constantes
	public static final String FOLDER_ALFRESCO = "PARTICIPACION_CIUDADANA_BYPASS";

	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException {

		variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
		tramite = (String) variables.get("tramite");
		operador = (String) variables.get("operador");
		tecnicoResponsable = (String) variables.get("tecnicoResponsable");
		esAprobacion = Boolean.parseBoolean(variables.get("esAprobacion").toString());
		idProyecto = Integer.valueOf((String) variables.get(Constantes.ID_PROYECTO));

		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
		if (esAprobacion) {
			tipoDocumentoExpediente = 2;
			tipoOficio = catalogoCoaFacade.obtenerCatalogoPorCodigo("ppc.bypass.oficio.aprobacion");
			tipoDocumento = TipoDocumentoSistema.RCOA_PPC_BYPASS_OFICIO_APROBACION;
		} else {
			tipoDocumentoExpediente = 1;
			tipoOficio = catalogoCoaFacade.obtenerCatalogoPorCodigo("ppc.bypass.oficio.archivo");
			tipoDocumento = TipoDocumentoSistema.RCOA_PPC_BYPASS_OFICIO_ARCHIVO;
		}

		expedienteBPC = expedienteBPCFacade.getByProyectoLicenciaCoa(idProyecto);
		reporteBPC = reporteBPCFacade.getByIdExpediente(expedienteBPC.getId(), tipoDocumentoExpediente);
		documentoBPC = documentoBPCFacade.documentoXTablaIdXIdDocUnico(reporteBPC.getId(),ReporteBPC.class.getSimpleName(), tipoDocumento);

		String rolAutoridad = "autoridad";
		if (proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("PC"))
			rolAutoridad = "subsecretario";

		usuarioAutoridad = utilPPC.asignarRol(proyectoLicenciaCoa, rolAutoridad);

		areaAutoridad = new Area();

		if (usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1) {
			areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
		} else {
			areaAutoridad = proyectoLicenciaCoa.getAreaResponsable();
		}

		urlAlfresco = "";
		oficioGuardado = false;

		firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");

		verificaToken();

		generarOficio(true);

	}

	public void generarOficio(boolean marcaAgua) {
		try {
			PlantillaReporte plantillaReporte = documentoBPCFacade.obtenerPlantillaReporte(tipoDocumento.getIdTipoDocumento());

			asignarDatosEntityOficio();

			nombreReporte = "Oficio pronunciamiento" + reporteBPC.getCodigoReporte().replace("/", "-") + ".pdf";
			File oficioPdfAux = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,true, entityOficio);
			File oficioPdf = JsfUtil.fileMarcaAgua(oficioPdfAux, marcaAgua ? " - - BORRADOR - - " : " ",BaseColor.GRAY);

			Path path = Paths.get(oficioPdf.getAbsolutePath());
			String reporteHtmlfinal = oficioPdf.getName();
			archivoOficio = Files.readAllBytes(path);
			oficio = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(oficio);
			file.write(Files.readAllBytes(path));
			file.close();
			setOficioPath(JsfUtil.devolverContexto("/reportesHtml/" + reporteHtmlfinal));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void asignarDatosEntityOficio() {
		try {
			DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));

			Usuario proponente = proyectoLicenciaCoa.getUsuario();
			Organizacion organizacion = organizacionFacade.buscarPorRuc(proponente.getNombre());
			String nombreProponente = proponente.getPersona().getNombre();
			String tratamiento = (organizacion == null)
					? (proponente.getPersona().getTipoTratos() == null ? " "
							: proponente.getPersona().getTipoTratos().getNombre())
					: (organizacion.getPersona().getTipoTratos() == null ? " "
							: organizacion.getPersona().getTipoTratos().getNombre());

			entityOficio = new EntityOficioPPCBypass();

			entityOficio.setNumero(reporteBPC.getCodigoReporte());
			entityOficio.setCiudad(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica()
					.getUbicacionesGeografica().getNombre());
			if (!proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("PC")) {
				// incluir informacion de la sede de la zonal en el documento
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil
						.getBean(ProyectoSedeZonalUbicacionController.class);
				String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad,
						"PROYECTORCOA", proyectoLicenciaCoa, null, null);
				entityOficio.setCiudad(sedeZonal);
			}
			entityOficio.setFechaEmision(formatoFechaEmision.format(new Date()));

			String cargo;
			if (organizacion == null) {
				if (proponente.getPersona().getPosicion() != null) {
					cargo = proponente.getPersona().getPosicion();
					entityOficio.setDisplayCargo("inline");
				} else {
					cargo = "";
					entityOficio.setDisplayCargo("none");
				}
				entityOficio.setDisplayEmpresa("none");
				entityOficio.setNombreOperador(nombreProponente);
				entityOficio.setCargoOperador(cargo);
				entityOficio.setNombreEmpresa(" ");
				entityOficio.setTratamientoOperador(tratamiento);
			} else {
				if (organizacion.getCargoRepresentante() != null) {
					cargo = organizacion.getCargoRepresentante();
					entityOficio.setDisplayCargo("inline");
				} else {
					cargo = "";
					entityOficio.setDisplayCargo("none");
				}
				entityOficio.setNombreOperador(nombreProponente);
				entityOficio.setCargoOperador(cargo);
				entityOficio.setNombreEmpresa(organizacion.getNombre());
				entityOficio.setDisplayEmpresa("inline");
				entityOficio.setTratamientoOperador(tratamiento);
			}
			entityOficio.setAsunto(reporteBPC.getAsunto());
			entityOficio.setAntecedentes(reporteBPC.getAntecedente());
			entityOficio.setConclusiones(reporteBPC.getPronunciamiento());
			entityOficio.getAsunto();

			entityOficio.setNombreResponsable(usuarioAutoridad.getPersona().getNombre());
			entityOficio.setAreaResponsable(areaAutoridad.getAreaName());

			if (proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
				entityOficio.setAreaResponsable(Constantes.CARGO_AUTORIDAD_ZONAL);

			if (JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea().getTipoArea().getId() == 3)
				entityOficio.setSiglasEnte(JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation());

		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del Oficio.");
		}
	}

	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;

		if (firmaSoloToken)
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
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/pdf");
			content.setName(name);
		}
		return content;
	}

	public void subirDocumento() {
		try {
			if (documentoManual.getContenidoDocumento() == null) {
				String documentOffice = documentoBPCFacade.direccionDescarga(documentoBPC);
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

			byte[] documentoContent = archivoOficio;

			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent), "application/pdf");
				content.setName(getNombreReporte());
			} else
				JsfUtil.addMessageError("Error al generar el archivo");

			descargarOficio = true;
		} catch (Exception e) {
			JsfUtil.addMessageError(
					"Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	public void guardarDocumentoOficioAlfresco(FileUploadEvent event) {
		if (descargarOficio == true) {
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombreDocumento(documentoBPC.getNombreDocumento());
			documentoManual.setExtencionDocumento(documentoBPC.getExtencionDocumento());
			documentoManual.setMimeDocumento(documentoBPC.getMimeDocumento());
			documentoManual.setNombreTabla(documentoBPC.getNombreTabla());
			documentoManual.setIdTabla(reporteBPC.getId());
			documentoManual.setIdProceso(bandejaTareasBean.getProcessId());
			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else {
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public void guardarOficioAlfresco() {
		try {
			generarOficio(false);
			documentoBPC.setContenidoDocumento(archivoOficio);
			documentoBPC.setNombreDocumento(documentoBPC.getNombreDocumento());
			documentoBPC.setExtencionDocumento(documentoBPC.getExtencionDocumento());
			documentoBPC.setMimeDocumento(documentoBPC.getMimeDocumento());
			documentoBPC.setNombreTabla(documentoBPC.getNombreTabla());
			documentoBPC.setIdTabla(reporteBPC.getId());
			documentoBPC.setIdProceso(bandejaTareasBean.getProcessId());
			documentoBPC = documentoBPCFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),FOLDER_ALFRESCO, 0L, documentoBPC, tipoDocumento);
			subirDocumento();
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void completarTarea() {
		try {
			if (token) {
				documentoManual = new DocumentoBPC();
				subido = false;
				String idAlfresco = documentoBPC.getIdAlfresco();
				if (!documentoBPCFacade.verificarFirmaAlfresco(idAlfresco)) {
					JsfUtil.addMessageError("El oficio de pronunciamiento no está firmado electrónicamente.");
					return;
				}
			} else {
				if (subido) {
					documentoManual = documentoBPCFacade.guardarDocumentoAlfresco(
							proyectoLicenciaCoa.getCodigoUnicoAmbiental(), FOLDER_ALFRESCO,
							bandejaTareasBean.getTarea().getProcessInstanceId(), documentoManual, tipoDocumento);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el oficio de pronunciamiento firmado.");
					return;
				}
			}

			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);

			if (reporteBPC.getTipoDocumento().equals(2)) {
				try {
					iniciarProcesoResolucionLicenciaAmbiental();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				procesosArchivadosFacade.guardarArchivado(proyectoLicenciaCoa, 2);
			}
			notificacionOperador();

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
			e.printStackTrace();
		}
	}

	
	public void notificacionOperador() {
		try {
			String nombreOperador = "";
			Usuario usuarioOperador = expedienteBPC.getProyectoLicenciaCoa().getUsuario();
		
			if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
				nombreOperador = usuarioOperador.getPersona().getNombre();
			} else {
				Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
				nombreOperador = organizacion.getNombre();
				usuarioOperador.getPersona().setContactos(organizacion.getContactos());
			}

			documentoBPC = documentoBPCFacade.documentoXTablaIdXIdDocUnico(reporteBPC.getId(),ReporteBPC.class.getSimpleName(), tipoDocumento);
			MensajeNotificacion mensajeNotificacion = new MensajeNotificacion();

			List<String> listaArchivos = new ArrayList<>();
			FileOutputStream file;
			String prefijoNombre = "";
			if (esAprobacion) {
				prefijoNombre = "PronunciamientoFinal_";
				mensajeNotificacion = mensajeNotificacionFacade
						.buscarMensajesNotificacion("bodyNotificacionPronunciamientoFinalPPCv2");
			} else {
				prefijoNombre = "PronunciamientoArchivo_";
				mensajeNotificacion = mensajeNotificacionFacade
						.buscarMensajesNotificacion("bodyNotificacionPronunciamientoArchivoPPCv2");
			}
			Object[] parametrosCorreoNuevo = new Object[] { nombreOperador };

			String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);

			String nombreArchivo = prefijoNombre + proyectoLicenciaCoa.getCodigoUnicoAmbiental() + ".pdf";
			File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreArchivo);

			try {
				file = new FileOutputStream(fileArchivo);
				file.write(documentoBPCFacade.descargar(documentoBPC.getIdAlfresco()));
				file.close();

				listaArchivos.add(nombreArchivo);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Email.sendEmailAdjuntos(usuarioOperador, mensajeNotificacion.getAsunto(), notificacion, listaArchivos,
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void iniciarProcesoResolucionLicenciaAmbiental() {
		ProyectoLicenciaCoa proyectoLicenciaCoaAux = new ProyectoLicenciaCoa();
		proyectoLicenciaCoaAux = proyectoLicenciaCoa;
		proyectoLicenciaCoaAux = proyectoLicenciaCoaFacade.buscarProyectoPorId(proyectoLicenciaCoaAux.getId());
			if ((expedienteBPC != null) && (expedienteBPC.getId() != null) && (expedienteBPC.getTieneResolucionFisica() != null) && (expedienteBPC.getTieneResolucionFisica())) {
				generaTareaResolucionAmbiental(proyectoLicenciaCoaAux);
			} else {
				generarTareaNoResolucionFisica();
			}
	}

	private void generaTareaResolucionAmbiental(ProyectoLicenciaCoa proyectoLicenciaCoaAux) {
		lanzarTareaResolucionAmbiental();
	}

	private void lanzarTareaResolucionAmbiental() {
		Usuario usuarioTarea = new Usuario();
		usuarioTarea = loginBean.getUsuario();
		Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
		parametros.put("tramite", tramite);
		parametros.put("idProyecto", idProyecto);
		parametros.put("operador", operador);
		parametros.put("tecnicoResponsable",tecnicoResponsable);
		parametros.put("esFisico", true);
		parametros.put("autoridadAmbiental", usuarioAutoridad.getNombre());
		try {
			Long idProceso = procesoFacade.iniciarProceso(usuarioTarea,
					Constantes.ROCA_PROCESO_RESOLUCION_LICENCIA_AMBIENTAL, tramite, parametros);
			System.out.println("proceso ResolucionLicenciaAmbiental ::::" + idProceso);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (JbpmException e) {
			e.printStackTrace();
		}
	}

	private void generarTareaNoResolucionFisica() {
			Usuario usuarioTarea = new Usuario();
			usuarioTarea = loginBean.getUsuario();
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("tramite", tramite);
			parametros.put("idProyecto", idProyecto);
			parametros.put("operador", operador);
			parametros.put("tecnicoResponsable", tecnicoResponsable);
			parametros.put("esFisico", false);
			parametros.put("autoridadAmbiental", usuarioAutoridad.getNombre());
			try {
				Long idProceso = procesoFacade.iniciarProceso(usuarioTarea,
						Constantes.ROCA_PROCESO_RESOLUCION_LICENCIA_AMBIENTAL, tramite, parametros);
				System.out.println("proceso ResolucionLicenciaAmbiental ::::" + idProceso);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			} catch (JbpmException e) {
				e.printStackTrace();
			}
	}
	
}