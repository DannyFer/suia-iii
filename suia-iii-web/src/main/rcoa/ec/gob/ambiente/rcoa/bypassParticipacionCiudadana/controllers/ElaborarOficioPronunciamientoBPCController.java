package ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.DocumentoBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.ExpedienteBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.ReporteBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.DocumentoBPC;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ExpedienteBPC;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ReporteBPC;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.participacionCiudadana.dto.EntityOficioPPCBypass;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class ElaborarOficioPronunciamientoBPCController {

	private static final Logger LOG = Logger.getLogger(ElaborarOficioPronunciamientoBPCController.class);

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	// Facades
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private DocumentoBPCFacade documentosBPCFacade;
	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;
	@EJB
	public CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ExpedienteBPCFacade expedienteBPCFacade;
	@EJB
	private ReporteBPCFacade reporteBPCFacade;
	@EJB
	private DocumentoBPCFacade documentoBPCFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	@EJB
	private GestionarProductosQuimicosProyectoAmbientalFacade gestionarProductosQuimicosProyectoAmbientalFacade;

	@Inject
	private UtilBPCBypass utilPPC;

	// Objetos
	@Getter
	@Setter
	private ReporteBPC reporteBPC = new ReporteBPC();
	@Getter
	@Setter
	private EntityOficioPPCBypass entityOficio = new EntityOficioPPCBypass();
	@Getter
	@Setter
	private ExpedienteBPC expedienteBPC = new ExpedienteBPC();
	@Getter
	@Setter
	private DocumentoBPC documentoBPC = new DocumentoBPC();

	@Setter
	@Getter
	private PlantillaReporte plantillaReporte = new PlantillaReporte();
	@Getter
	@Setter
	private File oficio;
	@Getter
	@Setter
	private Map<String, Object> variables;

	// Variables
	@Getter
	@Setter
	private Boolean oficioGuardado = false, esAprobacion;
	@Getter
	@Setter
	private String oficioPath, nombreReporte;
	@Getter
	@Setter
	private byte[] archivoOficio;
	@Getter
	@Setter
	private CatalogoGeneralCoa tipoOficio;
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumentoSistema;
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	@Getter
	@Setter
	private Usuario usuarioTecnico;
	@Getter
	@Setter
	private Area areaAutoridad;
	@Getter
	@Setter
	private String tramite, operador;
	@Getter
	@Setter
	private Integer idProyecto, tipoPronunciamiento;
	@Getter
	@Setter
	private String asunto;
	@Getter
	@Setter
	private String antecedentes;
	@Getter
	@Setter
	private String pronunciamiento;
	@Getter
	@Setter
	private Integer idMercurio;
	@Getter
	@Setter
	private Integer idCianuroSodio;
	@Getter
	@Setter
	private Integer idCianuroPotasio;
	@Getter
	@Setter
	@Column(name = "prfa_full_payment_inspection")
	private Double pagoInspeccion;

	// Constantes
	public static final String FOLDER_ALFRESCO = "PARTICIPACION_CIUDADANA_BYPASS";
	public static final Integer LICENCIA_AMBIENTAL_BI = 3;
	public static final Integer LICENCIA_AMBIENTAL_AI = 4;

	@PostConstruct
	public void inicio() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
					bandejaTareasBean.getTarea().getProcessInstanceId());
			tramite = (String) variables.get("tramite");
			esAprobacion = Boolean.parseBoolean(variables.get("esAprobacion").toString());
			idProyecto = Integer.valueOf((String) variables.get(Constantes.ID_PROYECTO));
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			expedienteBPC = expedienteBPCFacade.getByProyectoLicenciaCoa(proyectoLicenciaCoa.getId());
			if (variables.get("operador") != null)
			{
				operador = (String) variables.get("operador");
			}
			else
			{
				operador = proyectoLicenciaCoa.getUsuario().getNombre();
			}
			if (esAprobacion) {
				reporteBPC.setTipoDocumento(2);
				tipoPronunciamiento = 2;
				tipoOficio = catalogoCoaFacade.obtenerCatalogoPorCodigo("ppc.bypass.oficio.aprobacion");
				tipoDocumentoSistema = TipoDocumentoSistema.RCOA_PPC_BYPASS_OFICIO_APROBACION;
			} else {
				reporteBPC.setTipoDocumento(1);
				tipoPronunciamiento = 1;
				tipoOficio = catalogoCoaFacade.obtenerCatalogoPorCodigo("ppc.bypass.oficio.archivo");
				tipoDocumentoSistema = TipoDocumentoSistema.RCOA_PPC_BYPASS_OFICIO_ARCHIVO;
			}
			reporteBPC = reporteBPCFacade.getByIdExpediente(expedienteBPC.getId(), tipoPronunciamiento);

			if (reporteBPC.getId() == null) {
				String numeroOficio = utilPPC.generarCodigoOficio(proyectoLicenciaCoa.getAreaResponsable());
				reporteBPC.setCodigoReporte(numeroOficio);
				reporteBPC.setExpedienteBPC(expedienteBPC);
				reporteBPC.setTipoDocumento(tipoPronunciamiento);
			}

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
			rolAutoridad = "tecnicoRLA";
			usuarioTecnico = new Usuario();
			usuarioTecnico = utilPPC.asignarRol(proyectoLicenciaCoa, rolAutoridad);
			generarOficio(true);
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Error visualizar informe / oficio");
		}
	}

	public void generarOficio(boolean marcaAgua) {
		try {
			PlantillaReporte plantillaReporte = this.documentosBPCFacade
					.obtenerPlantillaReporte(tipoDocumentoSistema.getIdTipoDocumento());

			asignarDatosEntityOficio();

			nombreReporte = "Oficio pronunciamiento" + reporteBPC.getCodigoReporte().replace("/", "-") + ".pdf";
			File oficioPdfAux = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					true, entityOficio);
			File oficioPdf = JsfUtil.fileMarcaAgua(oficioPdfAux, marcaAgua ? " - - BORRADOR - - " : " ",
					BaseColor.GRAY);

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

			if (proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas()
					.equals(Constantes.SIGLAS_TIPO_AREA_OT))
				entityOficio.setAreaResponsable(Constantes.CARGO_AUTORIDAD_ZONAL);

			if (JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea().getTipoArea().getId() == 3)
				entityOficio.setSiglasEnte(
						JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation());

		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del Oficio.");
		}
	}

	public void guardarOficio() {
		reporteBPC.setExpedienteBPC(expedienteBPC);
		reporteBPC = reporteBPCFacade.guardar(reporteBPC);
		expedienteBPC = expedienteBPCFacade.guardar(expedienteBPC);
		oficioGuardado = true;
		generarOficio(true);
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}

	public void guardarDocumentoOficioAlfresco() {
		try {
			generarOficio(true);
			documentoBPC.setContenidoDocumento(archivoOficio);
			documentoBPC.setNombreDocumento(nombreReporte);
			documentoBPC.setExtencionDocumento(".pdf");
			documentoBPC.setMimeDocumento("application/pdf");
			documentoBPC.setNombreTabla(ReporteBPC.class.getSimpleName());
			documentoBPC.setIdTabla(reporteBPC.getId());
			documentoBPC.setIdProceso(bandejaTareasBean.getProcessId());

			if (esAprobacion) {
				documentoBPC.setDescripcionTabla("Pronunciamiento Final");
			} else {
				documentoBPC.setDescripcionTabla("Pronunciamiento Archivo");
			}

			documentoBPC = documentoBPCFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					FOLDER_ALFRESCO, 0L, documentoBPC, tipoDocumentoSistema);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
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

	public void completarTarea() {
		try {
			guardarDocumentoOficioAlfresco();
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("autoridadAmbiental", usuarioAutoridad.getNombre());
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(),
					parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),
					bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
}
