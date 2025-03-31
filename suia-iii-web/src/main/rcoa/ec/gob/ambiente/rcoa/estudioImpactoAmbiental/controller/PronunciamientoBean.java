package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.dto.EntityOficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.OficioPronunciamientoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarPdf;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class PronunciamientoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(PronunciamientoBean.class);

	@Getter
	@Setter
	private Integer idTarea;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	@ManagedProperty(value = "#{informeTecnicoConsolidadoEIABean}")
	@Getter
	@Setter
	private InformeTecnicoConsolidadoEIABean informeTecnicoConsolidadoEIABean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{buscarUsuarioBean}")
	private BuscarUsuarioBean buscarUsuarioBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;

	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;

	@EJB
	private OficioPronunciamientoEsIAFacade oficioPronunciamientoEsIAFacade;

	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;

	@EJB
	private DocumentosImpactoEstudioFacade documentosFacade;

	@EJB
	private AreaFacade areaFacade;

	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporte, plantillaOficioMemo;

	@Getter
	@Setter
	private InformacionProyectoEia esiaProyecto;

	@Getter
	@Setter
	private OficioPronunciamientoEsIA oficioPronunciamiento;

	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;

	@Getter
	@Setter
	private Usuario usuarioAutoridad;

	@Getter
	@Setter
	private Area areaTramite;

	@Getter
	@Setter
	private UbicacionesGeografica ubicacionPrincipal = new UbicacionesGeografica();

	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoOficio;

	@Getter
	@Setter
	private Integer idProyecto, numeroObservaciones, tipoOficio, idInforme;

	@Getter
	@Setter
	private String urlReporte, nombreReporte, nombreAutoridad;

	@Getter
	@Setter
	private byte[] archivoReporte;

	@Getter
	@Setter
	private Map<String, Object> variables;

	@Getter
	@Setter
	private Integer numeroRevision;

	@Getter
	@Setter
	private Double valorMinimo;

	@Getter
	@Setter
	private boolean esTerceraObservacion = false, esRequerido;
	
	@Getter
	@Setter
	private boolean permitirCero = false;

	@PostConstruct
	public void init() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			idTarea = (int) bandejaTareasBean.getTarea().getTaskId();
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),
					bandejaTareasBean.getProcessId());
			esRequerido = false;
			valorMinimo = 0.0;
			nombreAutoridad = "";
			String idProyectoString = (String) variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);
			
			int numeroObs = 4;
			if(variables.get("totalRevisiones")==null){
				numeroObs = 3;
			}			

			if (variables.get("numeroRevision") != null) {
				String valor = (String) variables.get("numeroRevision");
				numeroObservaciones = Integer.valueOf(valor);
				esTerceraObservacion = numeroObservaciones.equals(numeroObs);
			}

			String revision = (String) variables.get("numeroRevision");
			numeroRevision = (revision != null) ? Integer.parseInt(revision) : 0;

			proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);

			esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);

			oficioPronunciamiento = oficioPronunciamientoEsIAFacade.getPorIdEstudio(esiaProyecto);

			areaTramite = proyecto.getAreaResponsable();

			if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
				areaTramite = areaTramite.getArea();
			ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto)
					.getUbicacionesGeografica();
			
			if(Constantes.getTramiteInventarioCero().equals(proyecto.getCodigoUnicoAmbiental())){
				permitirCero = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del oficio.");
		}
	}

	public void cargarDatos() {
		String rolAutoridad = "";
		tipoDocumento = TipoDocumentoSistema.EIA_OFICIO_APROBACION_CONSOLIDADO;
		if (areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES)
				|| areaTramite.getTipoArea().getSiglas().equals("EA")) {
			rolAutoridad = "role.esia.cz.autoridad";
		} else if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
			if (informeTecnicoConsolidadoEIABean.getInformeTecnico().getTipoPronunciamiento()
					.equals(InformeTecnicoEsIA.aprobado)) {
				rolAutoridad = "role.area.subsecretario.calidad.ambiental";
				List<Usuario> usuarios = usuarioFacade.buscarUsuarioPorRol(Constantes.getRoleAreaName(rolAutoridad));
				if (usuarios != null && !usuarios.isEmpty()) {
					usuarioAutoridad = usuarios.get(0);
					areaTramite = areaTramite.getArea();
				} else {
					LOG.error("No se encontro usuario " + Constantes.getRoleAreaName(rolAutoridad) + " en "
							+ areaTramite.getAreaName());
					JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
					buscarUsuarioBean.buscarUsuario(rolAutoridad, areaTramite);
				}
				return;
			} else
				rolAutoridad = "role.esia.pc.autoridad";
		} else if (areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
			if (informeTecnicoConsolidadoEIABean.getInformeTecnico().getTipoPronunciamiento()
					.equals(InformeTecnicoEsIA.aprobado)) {
				rolAutoridad = "role.esia.ga.autoridad";
			} else
				rolAutoridad = "role.esia.ga.autoridad.calidad";
		}
		usuarioAutoridad = buscarUsuarioBean.buscarUsuario(rolAutoridad, areaTramite);

		if (usuarioAutoridad == null) {
			LOG.error("No se encontro usuario " + Constantes.getRoleAreaName(rolAutoridad) + " en "
					+ areaTramite.getAreaName());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return;
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

	public void generarOficio(Boolean marcaAgua) {
		try {
			plantillaReporte = plantillaReporteFacade.getPlantillaReporte(tipoDocumento);
			
			if(idInforme != null)
				oficioPronunciamiento = oficioPronunciamientoEsIAFacade.getPorEstudioInforme(esiaProyecto.getId(), idInforme);
			
			if(oficioPronunciamiento == null) {
				oficioPronunciamiento = new OficioPronunciamientoEsIA();
				oficioPronunciamiento.setNombreReporte("oficioPronunciamiento.pdf");
				oficioPronunciamiento.setInformacionProyecto(esiaProyecto);
			} else {
				oficioPronunciamiento.setNombreReporte("oficioPronunciamiento_" + UtilViabilidad.getFileNameEscaped(oficioPronunciamiento.getCodigoOficio().replace("/", "-")) + ".pdf");
			}
			
			EntityOficioPronunciamientoEsIA entity = cargarDatosOficio();

			File oficioPdfAux = UtilGenerarPdf.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					oficioPronunciamiento.getNombreReporte(), true, entity);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(oficioPdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(oficioPdf.getAbsolutePath());
			oficioPronunciamiento.setArchivo(Files.readAllBytes(path));
			String reporteHtmlfinal = oficioPronunciamiento.getNombreReporte().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(oficioPronunciamiento.getArchivo());
			file.close();
			oficioPronunciamiento.setPath(JsfUtil.devolverContexto("/reportesHtml/" + oficioPronunciamiento.getNombreReporte()));
			
			urlReporte = oficioPronunciamiento.getPath();
			nombreReporte = oficioPronunciamiento.getNombreReporte();
			archivoReporte = oficioPronunciamiento.getArchivo();

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(
					"Error al cargar el oficio de pronunciamiento",
					e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public EntityOficioPronunciamientoEsIA cargarDatosOficio() {
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));

		// recuperar destinatario
		String nombreDestinatario = usuarioFacade.recuperarNombreOperador(proyecto.getUsuario()).get(0);
		String cargoDestinatario = "";
		cargoDestinatario = areaTramite.getAreaName();

		EntityOficioPronunciamientoEsIA entity = new EntityOficioPronunciamientoEsIA();

		entity.setNumeroOficio(oficioPronunciamiento.getCodigoOficio());
		entity.setFechaEmision(formatoFechaEmision.format(new Date()));
		entity.setNombreDestinatario(nombreDestinatario);
		entity.setCargoDestinatario(cargoDestinatario);
		entity.setAsunto(oficioPronunciamiento.getAsunto());
		if (informeTecnicoConsolidadoEIABean.getInformeTecnico().getTipoPronunciamiento()
				.equals(InformeTecnicoEsIA.aprobado)) {
			entity.setMostrarAprobado("style=\"display: block;\"");
			entity.setMostrarObservado("style=\"display: none;\"");
		} else {
			entity.setMostrarAprobado("style=\"display: none;\"");
			entity.setMostrarObservado("style=\"display: block;\"");
		}
		entity.setAntecedentes(oficioPronunciamiento.getAntecedentes());
		entity.setPronunciamiento(oficioPronunciamiento.getPronunciamiento());
		entity.setConclusion(oficioPronunciamiento.getConclusiones());
		if (usuarioAutoridad != null) {
	        	// incluir informacion de la sede de la zonal en el documento
			ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
			String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyecto, null, null);
			entity.setUbicacion(sedeZonal);
			//entity.setUbicacion(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			entity.setNombreAutoridad("<br/><br/><br/>" + usuarioAutoridad.getPersona().getNombre());

			Area areaAutoridad = new Area();
			if (usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1) {
				areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
			} else {
				areaAutoridad = areaTramite;
			}

//			entity.setAreaAutoridad(usuarioAutoridad.getArea().getAreaName());
			entity.setAreaAutoridad(areaAutoridad.getAreaName());
			nombreAutoridad = usuarioAutoridad.getPersona().getNombre();
		} else {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		if (areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES))
			entity.setAreaAutoridad("DIRECCIÓN ZONAL");
		if (areaTramite.getTipoArea().getSiglas().equals("EA"))
			entity.setSiglasEnte(areaTramite.getAreaAbbreviation());
		return entity;
	}

	public void guardarDatos() {
		informacionProyectoEIACoaFacade.guardar(esiaProyecto);
		oficioPronunciamiento.setTipoOficio(tipoOficio);
		oficioPronunciamiento.setFechaOficio(new Date());
		oficioPronunciamiento.setIdInforme(idInforme);
		if (oficioPronunciamiento.getCodigoOficio() != null
				&& areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)
				&& !oficioPronunciamiento.getCodigoOficio().contains(areaTramite.getAreaAbbreviation()))
			oficioPronunciamiento.setCodigoOficio(null);// se cambia a null para actualizar el codigo si se cambio el
														// tipo de pronunciamiento

		oficioPronunciamientoEsIAFacade.guardarConsolidado(oficioPronunciamiento, areaTramite, tipoOficio);
		generarOficio(true);
	}

	public void guardarDocumentosAlfresco() {
		try {
			generarOficio(false);

			documentoOficio = new DocumentoEstudioImpacto();
			documentoOficio.setContenidoDocumento(oficioPronunciamiento.getArchivo());
			documentoOficio.setNombre(oficioPronunciamiento.getNombreReporte());
			documentoOficio.setExtesion(".pdf");
			documentoOficio.setMime("application/pdf");
			documentoOficio.setNombreTabla(OficioPronunciamientoEsIA.class.getSimpleName());
			documentoOficio.setIdTable(oficioPronunciamiento.getId());

			documentoOficio = documentosFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(),
					"OFICIO_PRONUNCIAMIENTO", documentoOficio, tipoDocumento);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void guardarDocumentoFirmaManual(DocumentoEstudioImpacto documentoManual) {
		try {
			documentoOficio = null;

			documentoManual.setNombreTabla(OficioPronunciamientoEsIA.class.getSimpleName());
			documentoManual.setIdTable(oficioPronunciamiento.getId());

			documentoOficio = documentosFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(),
					"OFICIO_PRONUNCIAMIENTO", documentoManual, tipoDocumento);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void actualizarCodigoOficio() {

		if (oficioPronunciamiento.getCodigoOficio() != null
				&& areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)
				&& !oficioPronunciamiento.getCodigoOficio().contains(areaTramite.getAreaAbbreviation())) {
			oficioPronunciamiento.setCodigoOficio(null); // se cambia a null para actualizar el codigo si se cambio el
															// tipo de pronunciamiento

			oficioPronunciamientoEsIAFacade.guardarConsolidado(oficioPronunciamiento, areaTramite, tipoOficio);
			generarOficio(true);
		}
	}
}
