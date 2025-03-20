package ec.gob.ambiente.rcoa.revisionPreliminar.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.dto.EntityOficioObsNoSubsanables;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.PronunciamientoObsNoSubsanablesFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.PronunciamientoObservacionesNoSubsanables;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
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
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class OficioPronunciamientoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(OficioPronunciamientoBean.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private DocumentosCoaFacade documentoFacade;
	
	@EJB
	private PronunciamientoObsNoSubsanablesFacade pronunciamientoFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private  UsuarioFacade usuarioFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private PronunciamientoObservacionesNoSubsanables oficioPronunciamiento;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporte, plantillaReporteOficio;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoOficio;
	
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
	private DocumentosCOA documentoOficioPronunciamiento;
	
	@Getter
	@Setter
	private Integer idProyecto, numeroObservaciones;
	
	@Getter
	@Setter
	private String razonSocial;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@PostConstruct
	public void init() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			String idProyectoString = (String) variables.get("u_idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			areaTramite = proyectoLicenciaCoa.getAreaResponsable();
			
			oficioPronunciamiento = pronunciamientoFacade.obtenerPorProyecto(proyectoLicenciaCoa.getId());
			
			plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_PRONUNCIAMIENTO_OBSERVACIONES_NO_SUBSANABLES);
			
			tipoOficio = TipoDocumentoSistema.RCOA_PRONUNCIAMIENTO_OBSERVACIONES_NO_SUBSANABLES;
		
			//Recuperar el usuario responsable de la firma del oficio de archivación de proyecto
			String tipoRol = "role.esia.cz.autoridad";
			
			if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC))
				tipoRol = "role.esia.pc.autoridad";
			else if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
				areaTramite = areaTramite.getArea();
			
			String rolAutoridad = Constantes.getRoleAreaName(tipoRol);
			
			List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaTramite);
			if (listaUsuario == null || listaUsuario.size() == 0)			
			{
				Log.debug("No se encontró autoridad ambiental en "+ areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return;
			}else{
				usuarioAutoridad = listaUsuario.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}

	public void generarOficio(Boolean marcaAgua) {
		try {
			if(usuarioAutoridad == null)
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"No se encontró autoridad ambiental en "+ areaTramite.getAreaName(), null));
			
			plantillaReporteOficio = plantillaReporteFacade.getPlantillaReporte(tipoOficio);
			
			oficioPronunciamiento = pronunciamientoFacade.obtenerPorProyecto(proyectoLicenciaCoa.getId());			
			
			if(oficioPronunciamiento == null) {
				oficioPronunciamiento = new PronunciamientoObservacionesNoSubsanables();
				oficioPronunciamiento.setIdProyecto(proyectoLicenciaCoa.getId());
			}			
			
			String nombreOficio = "OficioPronunciamientoArchivoProyecto_";
			if(oficioPronunciamiento.getCodigo() != null)
				nombreOficio = nombreOficio + UtilViabilidad.getFileNameEscaped(oficioPronunciamiento.getCodigo().replace("/", "-"));
			
			oficioPronunciamiento.setNombreOficio(nombreOficio + ".pdf");
			
			EntityOficioObsNoSubsanables oficioEntity = cargarDatos();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporteOficio.getHtmlPlantilla(),
					oficioPronunciamiento.getNombreOficio(), true, oficioEntity);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(oficioPdf.getAbsolutePath());
			oficioPronunciamiento.setArchivoOficio(Files.readAllBytes(path));
			String reporteHtmlfinal = oficioPronunciamiento.getNombreOficio().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(oficioPronunciamiento.getArchivoOficio());
			file.close();
			oficioPronunciamiento.setOficioPath(JsfUtil.devolverContexto("/reportesHtml/" + oficioPronunciamiento.getNombreOficio()));

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public EntityOficioObsNoSubsanables cargarDatos() throws ParseException {
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-YYYY");

		String nombreOperador = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
		
		//fecha ingreso coordenadas
		ProyectoLicenciaCoaUbicacion ubicacionProyecto = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa);
		Date fechaCoordenadas = ubicacionProyecto.getFechaCreacion();
		ubicacionPrincipal = ubicacionProyecto.getUbicacionesGeografica();
		
		//informacion certificado interseccion
		CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		String resultadoInterseccion = (oficioCI.getInterseccionViabilidad() == null || oficioCI.getInterseccionViabilidad().isEmpty()) ? "NO INTERSECA" : "INTERSECA";
		Date fechaOficioCI = (oficioCI.getFechaOficio() != null) ? oficioCI.getFechaOficio() : oficioCI.getFechaCreacion();
		
		String fechaTarea = JsfUtil.getCurrentTask().getActivationDate();
		Date fechaInicioAnalisis = new SimpleDateFormat("dd/MM/yyyy h:m a").parse(fechaTarea);
		

		EntityOficioObsNoSubsanables entityOficio = new EntityOficioObsNoSubsanables();
		entityOficio.setNumeroOficio(oficioPronunciamiento.getCodigo());
		if(usuarioAutoridad != null)
			entityOficio.setUbicacion(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entityOficio.setFechaEmision(formatoFechaEmision.format(new Date()));
		entityOficio.setNombreOperador(nombreOperador);
		entityOficio.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
		entityOficio.setCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		entityOficio.setFechaRegistro(formatoFecha.format(proyectoLicenciaCoa.getFechaCreacion()));
		entityOficio.setFechaCoordenadas(formatoFecha.format(fechaCoordenadas));
		entityOficio.setNroOficioCertificado(oficioCI.getCodigo());
		entityOficio.setFechaOficioCertificado(formatoFecha.format(fechaOficioCI));
		entityOficio.setParroquia(ubicacionPrincipal.getNombre());
		entityOficio.setCanton(ubicacionPrincipal.getUbicacionesGeografica().getNombre());
		entityOficio.setProvincia(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entityOficio.setResultadoInterseccion(resultadoInterseccion);
		entityOficio.setFechaIngresoAnalisis(formatoFecha.format(fechaInicioAnalisis));
		entityOficio.setPronunciamientoTecnico(oficioPronunciamiento.getPronunciamiento());
		
		if(usuarioAutoridad != null)
			entityOficio.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
		else 
			entityOficio.setNombreAutoridad(null);
		
		entityOficio.setAreaResponsable(areaTramite.getAreaName());
		if(areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES))
			entityOficio.setAreaResponsable(Constantes.CARGO_AUTORIDAD_ZONAL);
		
		if(areaTramite.getTipoArea().getId()==3)
			entityOficio.setSiglasEnte(areaTramite.getAreaAbbreviation());
		
		if(usuarioAutoridad != null && !usuarioAutoridad.getNombre().equals(JsfUtil.getLoggedUser().getNombre()))
			oficioPronunciamiento.setFechaInicioAnalisisTramite(fechaInicioAnalisis);
		
		return entityOficio;
	}
	
	public void guardarOficio() {
		oficioPronunciamiento.setFechaOficio(new Date());
		
		pronunciamientoFacade.guardar(oficioPronunciamiento, areaTramite);
	}
	
	public void guardarDocumentosAlfresco() {
		try {
			generarOficio(false);
			
			List<DocumentosCOA> listaDocumentos = documentoFacade.documentoXTablaIdXIdDoc(oficioPronunciamiento.getId(), tipoOficio, PronunciamientoObservacionesNoSubsanables.class.getSimpleName());
			if (listaDocumentos.size() > 0) {
				DocumentosCOA documentoAnteriorCoa = listaDocumentos.get(0);
				documentoAnteriorCoa.setEstado(false);

				documentoFacade.guardar(documentoAnteriorCoa);
			}
			
			documentoOficioPronunciamiento = new DocumentosCOA();
			documentoOficioPronunciamiento.setContenidoDocumento(oficioPronunciamiento.getArchivoOficio());
			documentoOficioPronunciamiento.setNombreDocumento(oficioPronunciamiento.getNombreOficio());
			documentoOficioPronunciamiento.setExtencionDocumento(".pdf");		
			documentoOficioPronunciamiento.setTipo("application/pdf");
			documentoOficioPronunciamiento.setNombreTabla(PronunciamientoObservacionesNoSubsanables.class.getSimpleName());
			documentoOficioPronunciamiento.setIdTabla(oficioPronunciamiento.getId());
			documentoOficioPronunciamiento.setProyectoLicenciaCoa(proyectoLicenciaCoa);
			
			documentoOficioPronunciamiento = documentoFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR_PRONUNCIAMIENTO", 1L, documentoOficioPronunciamiento, tipoOficio);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
}
