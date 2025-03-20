package ec.gob.ambiente.control.inspeccionescontrolambiental.bean;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade.InformeOficioICAFacade;
import ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade.InspeccionControlAmbientalFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class DocumentoICABean implements Serializable {

	private static final long serialVersionUID = 1590466855326654933L;

	private final Logger LOG = Logger.getLogger(DocumentoICABean.class);

	@EJB
	private ObservacionesFacade observacionesFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private InformeOficioICAFacade informeOficioICAFacade;

	@EJB
	private InspeccionControlAmbientalFacade inspeccionControlAmbientalFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private UsuarioFacade usuarioFacade;

	@Getter
	private SolicitudInspeccionControlAmbiental solicitud;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporteOficio;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporteOficioObservacion;

	@Getter
	private InformeTecnicoInspeccionesControlAmbiental informe;

	@Getter
	private OficioPronunciamientoInspeccionesControlAmbiental oficio;

	@Getter
	public OficioObservacionInspeccionesControlAmbiental oficioObservaciones /*= informeOficioICAFacade.getOficioObservacion(solicitud,
			JsfUtil.getCurrentTask().getProcessInstanceId(), contadorBandejaTecnico)*/;

	private String nombreEmpresa;

	private String nombreEmpresaMostrar;

	private String cargoRepresentanteLegalMostrar;

	private String[] cargoEmpresa;

	private Usuario usuarioAutoridad;

	private Usuario tecnicoResponsable;

	@Getter
	@Setter
	private int contadorBandejaTecnico;

	@Getter
	private boolean tipoOficioPronunciamiento;

	@PostConstruct
	public void init() {
		try {
			Integer idSolicitudInspeccion = Integer.parseInt(JsfUtil.getCurrentTask()
					.getVariable(SolicitudInspeccionControlAmbiental.VARIABLE_ID_SOLICITUD).toString());

			Object contadorBandejaTecnicoValue = JsfUtil.getCurrentTask()
					.getVariable(GeneradorDesechosPeligrosos.VARIABLE_CANTIDAD_OBSERVACIONES);

			if (contadorBandejaTecnicoValue == null || contadorBandejaTecnicoValue.toString().isEmpty()) {
				contadorBandejaTecnico = 0;
			} else {
				try {
					contadorBandejaTecnico = Integer.parseInt(contadorBandejaTecnicoValue.toString());
				} catch (Exception e) {
					LOG.error("Error recuperado cantidad de observaciones en registro de generador", e);
				}
			}

			solicitud = inspeccionControlAmbientalFacade.cargarSolicitudParaDocumentoPorId(idSolicitudInspeccion);

			try {
				tipoOficioPronunciamiento = JsfUtil.getCurrentTask()
						.getVariable(SolicitudInspeccionControlAmbiental.VARIABLE_TIPO_OFICIO).toString()
						.equals("Pronunciamiento");
			} catch (Exception e) {
			}

			boolean esDireccionProvincial = Boolean
					.parseBoolean(JsfUtil.getCurrentTask().getVariable("esDireccionProvincial").toString());

			String cedulaAutoridad = JsfUtil.getCurrentTask()
					.getVariable(esDireccionProvincial ? "director" : "subsecretaria").toString();
			usuarioAutoridad = usuarioFacade.buscarUsuarioWithOutFilters(cedulaAutoridad);

			cargoEmpresa = getEmpresa();
			if (cargoEmpresa != null) {
				nombreEmpresa = cargoEmpresa[0] + " de la empresa " + cargoEmpresa[1];
				nombreEmpresaMostrar = cargoEmpresa[1];
				cargoRepresentanteLegalMostrar = cargoEmpresa[0];
			}
		} catch (Exception e) {
			LOG.error("Error al cargar el informe tecnico del registro de generador", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
		}
	}

	public void visualizarInforme(boolean setCurrentDate) throws Exception {
		if (plantillaReporteInforme == null)
			plantillaReporteInforme = informeOficioICAFacade
					.getPlantillaReporte(TipoDocumentoSistema.TIPO_INFORME_TECNICO_ICA);

		if (informe == null)
			inicializarInformeTecnicoAsociado();

		if (informe == null) {
			informe = new InformeTecnicoInspeccionesControlAmbiental();
			informe.setContadorBandejaTecnico(contadorBandejaTecnico);
			informe.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
			informe.setNumero(informeOficioICAFacade.generarCodigoInforme(solicitud));
			informe.setCiudad(solicitud.getAreaResponsable().getUbicacionesGeografica().getNombre());

			informe.setAntecedentesAdicional("");
			informe.setObjetivosAdicional("");
			informe.setObservacionesDocumentoAdjuntado("");
			informe.setObservacionesAdicional("");
			informe.setAntecedentesAdicional("");
			informe.setConclusionesAdicional("");
			informe.setRecomendaciones("");
			informe.setRecomendacionesEncabezado("");

			informeOficioICAFacade.guardarInforme(informe, solicitud, JsfUtil.getLoggedUser());

			borrarObservacionesInformeTecnico();
		}

		if (tecnicoResponsable == null) {
			tecnicoResponsable = usuarioFacade.buscarUsuario(informe.getUsuarioCreacion());
		}

		if (setCurrentDate)
			informe.setFecha(new Date());
		informe.setFechaMostrar(JsfUtil.devuelveFechaEnLetrasSinHora(informe.getFecha()));
		informe.setNombreFichero("InformeTecnico_" + getFileNameEscaped(informe.getNumero()) + ".pdf");
		informe.setNombreReporte("InformeTecnico.pdf");

		String articulo = "";
		if (solicitud.getUsuario().getPersona().getTipoTratos() != null) {
			articulo += JsfUtil.obtenerArticuloSegunTratamiento(
					solicitud.getUsuario().getPersona().getTipoTratos().getNombre()) + " ";
		}

		String sujetoControl = "Sra./Sr.";
		String sujetoControlEncabez = "Sra./Sr.";

		if (solicitud.getUsuario().getPersona().getTipoTratos() != null
				&& solicitud.getUsuario().getPersona().getTipoTratos().getNombre() != null
				&& !solicitud.getUsuario().getPersona().getTipoTratos().getNombre().isEmpty()) {
			sujetoControl = solicitud.getUsuario().getPersona().getTipoTratos().getNombre();
			sujetoControlEncabez = solicitud.getUsuario().getPersona().getTipoTratos().getNombre();
		}

		sujetoControl = articulo + sujetoControl;

		if (solicitud.getUsuario().getPersona().getTitulo() != null
				&& !solicitud.getUsuario().getPersona().getTitulo().trim().isEmpty()) {
			sujetoControl += " / " + solicitud.getUsuario().getPersona().getTitulo() + ",";
		}

		if (solicitud.getUsuario().getPersona().getNombre() != null) {
			sujetoControl += " " + solicitud.getUsuario().getPersona().getNombre();
			sujetoControlEncabez += " " + solicitud.getUsuario().getPersona().getNombre();
		}

		if (informe.getRecomendaciones() == null || informe.getRecomendaciones().isEmpty())
			informe.setRecomendacionesEncabezado("");
		else
			informe.setRecomendacionesEncabezado("<p><strong>6. RECOMENDACIONES</strong></p>");

		informe.setSujetoControl(sujetoControl);
		informe.setSujetoControlEncabez(sujetoControlEncabez);

		informe.setSolicitud(solicitud.getSolicitud());

		informe.setProyectoCodigo(solicitud.getProyecto().getCodigo());
		informe.setProyectoNombre(solicitud.getProyecto().getNombre());
		informe.setProyectoFecha(JsfUtil.devuelveFechaEnLetrasSinHora(solicitud.getProyecto().getFechaRegistro()));

		informe.setProponente(getProponente(sujetoControl));
		informe.setEvaluadorMostrar(
				tecnicoResponsable != null ? tecnicoResponsable.getPersona().getNombre() : "TECNICO RESPONSABLE");
		informe.setSolicitud(solicitud.getSolicitud());
		informe.setFechaSolicitud(JsfUtil.devuelveFechaEnLetrasSinHora(solicitud.getFechaCreacion()));
		informe.setTipoDocumentoAdjuntado(null);
		informe.setCumpleMostrar(
				informe.getCumple() == null ? "PENDIENTE" : informe.getCumple() ? "CUMPLEN" : "NO CUMPLEN");

		File informePdf = UtilGenerarInforme.generarFichero(plantillaReporteInforme.getHtmlPlantilla(),
				informe.getNombreReporte(), true, informe);

		Path path = Paths.get(informePdf.getAbsolutePath());
		informe.setArchivoInforme(Files.readAllBytes(path));
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(informe.getNombreFichero()));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(informe.getArchivoInforme());
		file.close();
		informe.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informe.getNombreFichero()));
	}

	public void visualizarOficio(boolean setCurrentDate, boolean generarDatosAutoridad) throws Exception {
		if (plantillaReporteOficio == null)
			plantillaReporteOficio = informeOficioICAFacade
					.getPlantillaReporte(TipoDocumentoSistema.TIPO_OFICIO_PRONUNCIAMIENTO_ICA);

		if (oficio == null)
			oficio = informeOficioICAFacade.getOficioPronunciamiento(solicitud,
					JsfUtil.getCurrentTask().getProcessInstanceId(), contadorBandejaTecnico);

		if (oficio == null) {
			oficio = new OficioPronunciamientoInspeccionesControlAmbiental();
			oficio.setContadorBandejaTecnico(contadorBandejaTecnico);
			oficio.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
			oficio.setNumero(informeOficioICAFacade.generarCodigoOficioPronunciamiento(solicitud));

			oficio.setRecomendaciones("");

			informeOficioICAFacade.guardarOficioPronunciamiento(oficio, solicitud, JsfUtil.getLoggedUser());

			borrarObservacionesOficios();
		}

		if (setCurrentDate)
			oficio.setFecha(new Date());
		oficio.setFechaMostrar(JsfUtil.devuelveFechaEnLetrasSinHora(oficio.getFecha()));

		String articulo = "";
		if (solicitud.getUsuario().getPersona().getTipoTratos() != null) {
			articulo += JsfUtil.obtenerArticuloSegunTratamiento(
					solicitud.getUsuario().getPersona().getTipoTratos().getNombre()) + " ";
		}

		String sujetoControl = "Sra./Sr.";
		String sujetoControlEncabez = "<strong>Sra./Sr. &nbsp; &nbsp;&nbsp; </strong>";

		if (solicitud.getUsuario().getPersona().getTipoTratos() != null
				&& solicitud.getUsuario().getPersona().getTipoTratos().getNombre() != null
				&& !solicitud.getUsuario().getPersona().getTipoTratos().getNombre().isEmpty()) {
			sujetoControl = solicitud.getUsuario().getPersona().getTipoTratos().getNombre();
			sujetoControlEncabez = "<strong>" + solicitud.getUsuario().getPersona().getTipoTratos().getNombre()
					+ " &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp; </strong>";
		}

		sujetoControl = articulo + sujetoControl;

		if (solicitud.getUsuario().getPersona().getTitulo() != null
				&& !solicitud.getUsuario().getPersona().getTitulo().trim().isEmpty()) {
			sujetoControl += " / " + solicitud.getUsuario().getPersona().getTitulo() + ",";
		}

		if (solicitud.getUsuario().getPersona().getNombre() != null) {
			sujetoControl += " " + solicitud.getUsuario().getPersona().getNombre();
			sujetoControlEncabez += solicitud.getUsuario().getPersona().getNombre();
		}

		oficio.setSujetoControl(sujetoControl.trim());
		oficio.setSujetoControlEncabez(sujetoControlEncabez);
		oficio.setEvaluadorMostrar(JsfUtil.getLoggedUser().getPersona().getNombre());
		oficio.setSolicitud(solicitud.getSolicitud());
		oficio.setNumeroInforme(informe.getNumero());
		oficio.setFechaInforme(JsfUtil.devuelveFechaEnLetrasSinHora(informe.getFecha()));
		oficio.setAutoridad(generarDatosAutoridad ? usuarioAutoridad.getPersona().getNombre() : "");
		oficio.setCargoAutoridad(generarDatosAutoridad ? JsfUtil.obtenerCargoUsuario(usuarioAutoridad) : "");

		oficio.setSector(solicitud.getProyecto().getTipoSector().getNombre());

		oficio.setSolicitud(solicitud.getSolicitud());

		oficio.setProyectoCodigo(solicitud.getProyecto().getCodigo());
		oficio.setProyectoNombre(solicitud.getProyecto().getNombre());
		oficio.setProyectoFecha(JsfUtil.devuelveFechaEnLetrasSinHora(solicitud.getProyecto().getFechaRegistro()));

		oficio.setEmpresaCargo("");
		oficio.setNombreEmpresaMostrar("");
		oficio.setCargoRepresentanteLegalMostrar("");
		oficio.setDeLaEmpresa("");
		if (nombreEmpresa != null) {
			oficio.setEmpresaCargo(nombreEmpresa);
			oficio.setNombreEmpresaMostrar("<p><strong>Empresa:&nbsp; </strong>" + nombreEmpresaMostrar + "</p>");
			oficio.setCargoRepresentanteLegalMostrar(
					"<p><strong>Cargo:</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							+ cargoRepresentanteLegalMostrar + "</p>");
			oficio.setDeLaEmpresa("de la empresa " + cargoEmpresa[1]);
		}

		oficio.setNombreFichero("OficioPronunciamiento_" + getFileNameEscaped(oficio.getNumero()) + ".pdf");
		oficio.setNombreReporte("OficioPronunciamiento.pdf");

		File informePdf = UtilGenerarInforme.generarFichero(plantillaReporteOficio.getHtmlPlantilla(),
				oficio.getNombreReporte(), true, oficio);

		Path path = Paths.get(informePdf.getAbsolutePath());
		oficio.setArchivoOficio(Files.readAllBytes(path));
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(oficio.getNombreFichero()));
		oficio.setOficioRealPath(archivoFinal.getAbsolutePath());
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(oficio.getArchivoOficio());
		file.close();
		oficio.setOficioPath(JsfUtil.devolverContexto("/reportesHtml/" + oficio.getNombreFichero()));
	}

	public void visualizarOficioObservaciones(boolean setCurrentDate, boolean generarDatosAutoridad) throws Exception {
		if (plantillaReporteOficioObservacion == null)
			plantillaReporteOficioObservacion = informeOficioICAFacade
					.getPlantillaReporte(TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_ICA);

		if (oficioObservaciones == null)
			oficioObservaciones = informeOficioICAFacade.getOficioObservacion(solicitud,
					JsfUtil.getCurrentTask().getProcessInstanceId(), contadorBandejaTecnico);

		if (oficioObservaciones == null) {
			oficioObservaciones = new OficioObservacionInspeccionesControlAmbiental();
			oficioObservaciones.setContadorBandejaTecnico(contadorBandejaTecnico);
			oficioObservaciones.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
			oficioObservaciones.setNumero(informeOficioICAFacade.generarCodigoOficioObservacion(solicitud));

			oficioObservaciones.setRecomendaciones("");

			informeOficioICAFacade.guardarOficioObservacion(oficioObservaciones, solicitud, JsfUtil.getLoggedUser());

			borrarObservacionesOficios();
		}

		if (setCurrentDate)
			oficioObservaciones.setFecha(new Date());
		oficioObservaciones.setFechaMostrar(JsfUtil.devuelveFechaEnLetrasSinHora(oficioObservaciones.getFecha()));

		String articulo = "";
		if (solicitud.getUsuario().getPersona().getTipoTratos() != null) {
			articulo += JsfUtil.obtenerArticuloSegunTratamiento(
					solicitud.getUsuario().getPersona().getTipoTratos().getNombre()) + " ";
		}

		String sujetoControl = "Sra./Sr.";
		String sujetoControlEncabez = "<strong>Sra./Sr. &nbsp; &nbsp;&nbsp; </strong>";

		if (solicitud.getUsuario().getPersona().getTipoTratos().getNombre() != null
				&& !solicitud.getUsuario().getPersona().getTipoTratos().getNombre().isEmpty()) {
			sujetoControl = solicitud.getUsuario().getPersona().getTipoTratos().getNombre();
			sujetoControlEncabez = "<strong>" + solicitud.getUsuario().getPersona().getTipoTratos().getNombre()
					+ " &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; </strong>";
		}

		sujetoControl = articulo + sujetoControl;

		if (solicitud.getUsuario().getPersona().getTitulo() != null
				&& !solicitud.getUsuario().getPersona().getTitulo().trim().isEmpty()) {
			sujetoControl += " / " + solicitud.getUsuario().getPersona().getTitulo() + ",";
		}

		if (solicitud.getUsuario().getPersona().getNombre() != null) {
			sujetoControl += " " + solicitud.getUsuario().getPersona().getNombre();
			sujetoControlEncabez += solicitud.getUsuario().getPersona().getNombre();
		}

		oficioObservaciones.setSector(solicitud.getProyecto().getTipoSector().getNombre());

		oficioObservaciones.setSolicitud(solicitud.getSolicitud());

		oficioObservaciones.setProyectoCodigo(solicitud.getProyecto().getCodigo());
		oficioObservaciones
				.setProyectoFecha(JsfUtil.devuelveFechaEnLetrasSinHora(solicitud.getProyecto().getFechaRegistro()));

		oficioObservaciones.setSujetoControl(sujetoControl.trim());
		oficioObservaciones.setSujetoControlEncabez(sujetoControlEncabez);
		oficioObservaciones.setFechaSolicitud(JsfUtil.devuelveFechaEnLetrasSinHora(solicitud.getFechaCreacion()));
		oficioObservaciones.setNumeroInforme(informe.getNumero());
		oficioObservaciones.setFechaInforme(JsfUtil.devuelveFechaEnLetrasSinHora(informe.getFecha()));
		oficioObservaciones.setAutoridad(generarDatosAutoridad ? usuarioAutoridad.getPersona().getNombre() : "");
		oficioObservaciones
				.setCargoAutoridad(generarDatosAutoridad ? JsfUtil.obtenerCargoUsuario(usuarioAutoridad) : "");

		oficioObservaciones.setEmpresaCargo("");
		oficioObservaciones.setNombreEmpresaMostrar("");
		oficioObservaciones.setCargoRepresentanteLegalMostrar("");
		oficioObservaciones.setDeLaEmpresa("");
		if (nombreEmpresa != null) {
			oficioObservaciones.setEmpresaCargo(nombreEmpresa);
			oficioObservaciones.setNombreEmpresaMostrar(
					"<p><strong>Empresa:&nbsp;&nbsp; </strong>" + nombreEmpresaMostrar + "</p>");
			oficioObservaciones.setCargoRepresentanteLegalMostrar(
					"<p><strong>Cargo:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong>"
							+ cargoRepresentanteLegalMostrar + "</p>");
			oficioObservaciones.setDeLaEmpresa("de la empresa " + cargoEmpresa[1]);
		}

		oficioObservaciones.setNombreFichero("OficioObservacion_" + getFileNameEscaped(oficioObservaciones.getNumero()) + ".pdf");
		oficioObservaciones.setNombreReporte("OficioObservacion.pdf");

		File informePdf = UtilGenerarInforme.generarFichero(plantillaReporteOficioObservacion.getHtmlPlantilla(),
				oficioObservaciones.getNombreReporte(), true, oficioObservaciones);

		Path path = Paths.get(informePdf.getAbsolutePath());
		oficioObservaciones.setArchivoOficio(Files.readAllBytes(path));
		oficioObservaciones.setOficioRealPath(informePdf.getAbsolutePath());
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(oficioObservaciones.getNombreFichero()));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(oficioObservaciones.getArchivoOficio());
		file.close();
		oficioObservaciones
				.setOficioPath(JsfUtil.devolverContexto("/reportesHtml/" + oficioObservaciones.getNombreFichero()));
	}

	public void guardarInforme() {
		if (informe.getRecomendaciones() == null || informe.getRecomendaciones().isEmpty())
			informe.setRecomendacionesEncabezado("");
		else
			informe.setRecomendacionesEncabezado("<p><strong>6. RECOMENDACIONES</strong></p>");

		informe.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
		informeOficioICAFacade.guardarInforme(informe, solicitud, JsfUtil.getLoggedUser());
	}

	public void guardarOficio() {
		oficio.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
		informeOficioICAFacade.guardarOficioPronunciamiento(oficio, solicitud, JsfUtil.getLoggedUser());
	}

	public void guardarOficioObservaciones() {
		oficioObservaciones.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
		informeOficioICAFacade.guardarOficioObservacion(oficioObservaciones, solicitud, JsfUtil.getLoggedUser());
	}

	public Documento guardarInformeDocumento() throws ServiceException, CmisAlfrescoException {
		Documento documento = new Documento();
		documento.setNombre("InformeTecnico-" + informe.getNumero() + ".pdf");
		documento.setContenidoDocumento(informe.getArchivoInforme());
		documento.setNombreTabla(informe.getClass().getSimpleName());
		documento.setIdTable(informe.getId());
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		documento = documentosFacade.guardarDocumentoAlfresco(solicitud.getProyecto().getCodigo(),
				Constantes.NOMBRE_PROCESO_INSPECCIONES_CONTROL_AMBIENTAL, JsfUtil.getCurrentProcessInstanceId(),
				documento, informe.getTipoDocumento().getTipoDocumentoSistema(), null);
		return documento;
	}

	public Documento guardarOficioDocumento() throws ServiceException, CmisAlfrescoException {
		Documento documento = new Documento();
		documento.setNombre("OficioPronunciamiento" + "-" + oficio.getNumero() + ".pdf");
		documento.setContenidoDocumento(oficio.getArchivoOficio());
		documento.setNombreTabla(oficio.getClass().getSimpleName());
		documento.setIdTable(oficio.getId());
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		documento = documentosFacade.guardarDocumentoAlfresco(solicitud.getProyecto().getCodigo(),
				Constantes.NOMBRE_PROCESO_INSPECCIONES_CONTROL_AMBIENTAL, JsfUtil.getCurrentProcessInstanceId(),
				documento, oficio.getTipoDocumento().getTipoDocumentoSistema(), null);
		return documento;
	}

	public Documento guardarOficioObservacionesDocumento() throws ServiceException, CmisAlfrescoException {
		Documento documento = new Documento();
		documento.setNombre("OficioObservacion-" + oficioObservaciones.getNumero() + ".pdf");
		documento.setContenidoDocumento(oficioObservaciones.getArchivoOficio());
		documento.setNombreTabla(oficioObservaciones.getClass().getSimpleName());
		documento.setIdTable(oficioObservaciones.getId());
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		documento = documentosFacade.guardarDocumentoAlfresco(solicitud.getProyecto().getCodigo(),
				Constantes.NOMBRE_PROCESO_INSPECCIONES_CONTROL_AMBIENTAL, JsfUtil.getCurrentProcessInstanceId(),
				documento, oficioObservaciones.getTipoDocumento().getTipoDocumentoSistema(), null);
		return documento;
	}

	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}

	public void inicializarInformeTecnicoAsociado() {
		informe = informeOficioICAFacade.getInforme(solicitud, JsfUtil.getCurrentTask().getProcessInstanceId(),
				contadorBandejaTecnico);
	}

	public OficioPronunciamientoInspeccionesControlAmbiental inicializarOficioPronunciamientoAsociado() {
		oficio = informeOficioICAFacade.getOficioPronunciamiento(solicitud,
				JsfUtil.getCurrentTask().getProcessInstanceId(), contadorBandejaTecnico);
		return oficio;
	}

	public OficioObservacionInspeccionesControlAmbiental inicializarOficioObservacionesAsociado() {
		oficioObservaciones = informeOficioICAFacade.getOficioObservacion(solicitud,
				JsfUtil.getCurrentTask().getProcessInstanceId(), contadorBandejaTecnico);
		return oficioObservaciones;
	}

	private String getProponente(String label) throws ServiceException {
		if (label == null)
			label = "";
		Organizacion organizacion = organizacionFacade.buscarPorPersona(solicitud.getUsuario().getPersona(),
				solicitud.getUsuario().getNombre());
		if (organizacion != null) {
			return organizacion.getNombre();
		}
		return label;
	}

	private String[] getEmpresa() throws ServiceException {
		Organizacion organizacion = organizacionFacade.buscarPorPersona(solicitud.getUsuario().getPersona(),
				solicitud.getUsuario().getNombre());
		if (organizacion != null) {
			String[] cargoEmpresa = new String[2];
			cargoEmpresa[0] = organizacion.getCargoRepresentante();
			cargoEmpresa[1] = organizacion.getNombre();
			return cargoEmpresa;
		}
		return null;
	}

	public boolean validarExistenciaObservaciones() {
		boolean puedeContinuar = false;
		try {
			List<ObservacionesFormularios> observacionesUsuarioAutenticado = observacionesFacade
					.listarPorIdClaseNombreClaseUsuarioNoCorregidas(solicitud.getId(),
							"SolicitudInspeccionControlAmbiental", JsfUtil.getLoggedUser().getId());
			if (observacionesUsuarioAutenticado == null)
				puedeContinuar = true;
			else if (observacionesUsuarioAutenticado.isEmpty())
				puedeContinuar = true;
			else
				puedeContinuar = false;

		} catch (ServiceException exception) {
			LOG.error(
					"Ocurrió un error al recuperar las observaciones del usuario logueado en el proceso inspeccion de control",
					exception);
		}
		return puedeContinuar;
	}

	public void borrarObservacionesInformeTecnico() throws Exception {
		List<ObservacionesFormularios> observaciones = observacionesFacade.listarPorIdClaseNombreClase(
				solicitud.getId(), "SolicitudInspeccionControlAmbiental", "Informe técnico");
		if (observaciones != null) {
			crudServiceBean.delete(observaciones);
		}
	}

	public void borrarObservacionesOficios() throws Exception {
		List<ObservacionesFormularios> observaciones = observacionesFacade
				.listarPorIdClaseNombreClase(solicitud.getId(), "SolicitudInspeccionControlAmbiental", "Oficio");
		if (observaciones != null) {
			crudServiceBean.delete(observaciones);
		}
	}

	private String getFileNameEscaped(String file) {
		String result = file.replaceAll("Ñ", "N");
		result = result.replaceAll("Á", "A");
		result = result.replaceAll("É", "E");
		result = result.replaceAll("Í", "I");
		result = result.replaceAll("Ó", "O");
		result = result.replaceAll("Ú", "U");
		return result;
	}
}
