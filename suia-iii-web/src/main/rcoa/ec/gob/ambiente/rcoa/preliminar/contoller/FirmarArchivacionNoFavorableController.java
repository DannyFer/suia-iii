package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.dto.EntityOficioArchivacionViabilidad;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProcesosArchivadosFacade;
import ec.gob.ambiente.rcoa.facade.PronunciamientoArchivacionProyectoFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.PronunciamientoArchivacionProyecto;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PronunciamientoBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PronunciamientoForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ProyectoTipoViabilidadCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
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


@ManagedBean
@ViewScoped
public class FirmarArchivacionNoFavorableController {
	
	private static final Logger LOG = Logger.getLogger(FirmarArchivacionNoFavorableController.class);
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
	private DocumentosCoaFacade documentosFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private PronunciamientoBiodiversidadFacade pronunciamientoBiodiversidadFacade;
	
	@EJB
	private PronunciamientoForestalFacade pronunciamientoForestalFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private PronunciamientoArchivacionProyectoFacade pronunciamientoArchivacionProyectoFacade;
	
	@EJB
	private ProcesosArchivadosFacade procesosArchivadosFacade;
	
	@Getter
	@Setter
	private DocumentosCOA documentoOficio;
	
	@Getter
	@Setter
	private DocumentosCOA documentoManual = new DocumentosCOA();
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	
	@Getter
	@Setter
	private PronunciamientoArchivacionProyecto oficioArchivo;
	
	@Getter
	@Setter
	private List<ViabilidadCoa> viabilidadesProyecto;
	
	@Getter
	@Setter
	private String urlAlfresco, oficioPath, nombreReporte, claseDocumento, tipoViabilidad;

	@Getter
	@Setter
	private Boolean esForestal, oficioGuardado;

	@Getter
	@Setter
	private Boolean descargarOficio = false;
	
	@Getter
	@Setter
	private boolean token, subido, documentoSubido, firmaSoloToken;
	
	@Getter
	@Setter
	Integer idProyecto;
	
	@Getter
    @Setter
    private byte[] archivoOficio;
	
	private Map<String, Object> variables;

	@PostConstruct
	public void iniciar() {
		try {
			urlAlfresco = "";
			oficioGuardado = false;
	
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
	
			idProyecto = Integer.valueOf(variables.get("u_idProyecto").toString()); 
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
	
			verificaToken();
			
			usuarioAutoridad = JsfUtil.getLoggedUser();
			
			claseDocumento = "OficioPronunciamientoArchivoNoFavorable";
	
			generarOficio(false);
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos para revisión.");
		}
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/preliminar/firmaArchivoNoFavorable.jsf");
	}
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		
		if(firmaSoloToken)
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
	
	public void generarOficio(Boolean marcaAgua) {
		try {
			viabilidadesProyecto = viabilidadCoaFacade.getViabilidadesPorProyecto(idProyecto);
			
			Boolean archivoSnap = false;
			Boolean archivoForestal = false;
			
			for (ViabilidadCoa viabilidadCoa : viabilidadesProyecto) {
				tipoViabilidad = viabilidadCoa.getTipoFlujoViabilidad();
				if(!viabilidadCoa.getEsPronunciamientoFavorable() && viabilidadCoa.getEsViabilidadSnap()) {
					archivoSnap = true;
				}
				if(!viabilidadCoa.getEsPronunciamientoFavorable() && !viabilidadCoa.getEsViabilidadSnap()) {
					archivoForestal = true;
				}
			}
			
			oficioArchivo = pronunciamientoArchivacionProyectoFacade.obtenerPorProyecto(proyectoLicenciaCoa.getId());
			if(oficioArchivo == null) {
				Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
				if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
					areaTramite = areaTramite.getArea();
				}
				
				oficioArchivo = new PronunciamientoArchivacionProyecto();
				oficioArchivo.setIdProyecto(proyectoLicenciaCoa.getId());
				oficioArchivo.setCodigo(generarCodigoOficio(areaTramite));
				oficioArchivo.setFechaOficio(new Date());
				oficioArchivo = pronunciamientoArchivacionProyectoFacade.guardar(oficioArchivo);
			}
			
			TipoDocumentoSistema tipoDoc = null;
			
			if(viabilidadesProyecto.size() > 1) {
				if(archivoSnap && archivoForestal) {
					tipoDoc = TipoDocumentoSistema.RCOA_VIABILIDAD_BYPASS_OFICIO_ARCHIVO_DOS_NOFAVORABLE;
				} else {
					tipoDoc = TipoDocumentoSistema.RCOA_VIABILIDAD_BYPASS_OFICIO_ARCHIVO_UNO_NOFAVORABLE;
				}
			} else {
				tipoDoc = (archivoSnap) ? TipoDocumentoSistema.RCOA_VIABILIDAD_BYPASS_OFICIO_ARCHIVO_SNAP
						: TipoDocumentoSistema.RCOA_VIABILIDAD_BYPASS_OFICIO_ARCHIVO_FORESTAL;
			}
			
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(tipoDoc);
			
			EntityOficioArchivacionViabilidad entityOficio = asignarDatosEntityOficio(archivoSnap, archivoForestal);
			
			nombreReporte = "Oficio archivo " + oficioArchivo.getCodigo().replace("/","-") + ".pdf";
			File oficioPdfAux = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(),nombreReporte, true, entityOficio);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(oficioPdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(oficioPdf.getAbsolutePath());
			String reporteHtmlfinal = oficioPdf.getName();
			archivoOficio = Files.readAllBytes(path);
			File oficio = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(oficio);
			file.write(Files.readAllBytes(path));
			file.close();
			setOficioPath(JsfUtil.devolverContexto("/reportesHtml/"+ reporteHtmlfinal));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private EntityOficioArchivacionViabilidad asignarDatosEntityOficio(Boolean archivoSnap, Boolean archivoForestal) {
		try {
			DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
			SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-YYYY");
			
			List<String> datosOperador = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario());
			String nombreOperador = datosOperador.get(0);
			String tratamientoOperador = datosOperador.get(2);
			Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
			if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
				areaTramite = areaTramite.getArea();
			}

			//informacion certificado interseccion
			CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());

			EntityOficioArchivacionViabilidad entityOficio = new EntityOficioArchivacionViabilidad();
			entityOficio.setNumero(oficioArchivo.getCodigo());
			// incluir informacion de la sede de la zonal en el documento
			ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
			String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyectoLicenciaCoa, null, null);
			entityOficio.setCiudad(nombreCanton);
			//entityOficio.setCiudad(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			entityOficio.setFechaEmision(formatoFechaEmision.format(new Date()));
			entityOficio.setNombreOperador(nombreOperador);
			entityOficio.setTratamientoOperador(tratamientoOperador);
			entityOficio.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
			entityOficio.setCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
			entityOficio.setFechaRegistro(formatoFecha.format(proyectoLicenciaCoa.getFechaCreacion()));
			entityOficio.setNroOficioCertificado(oficioCI.getCodigo());
			
			String intersecaSnap = interseccionViabilidadCoaFacade.getNombresAreasProtegidasSnap(idProyecto, 1);
			entityOficio.setDetalleInterseccionSnap(intersecaSnap);
			
			if(archivoSnap) {
				ProyectoTipoViabilidadCoa proyectoTipoViabilidadCoa = viabilidadCoaFacade.getTipoViabilidadPorProyecto(idProyecto, true);
				ViabilidadCoa viabilidadProyecto = viabilidadCoaFacade.getViabilidadPorTipoProyecto(proyectoTipoViabilidadCoa.getId());
				PronunciamientoBiodiversidad oficioPronunciamiento = pronunciamientoBiodiversidadFacade.getInformePorViabilidad(viabilidadProyecto.getId());
				
				entityOficio.setNroOficioViabilidadSnap(oficioPronunciamiento.getNumeroOficio());
				entityOficio.setFechaOficioViabilidadSnap(formatoFecha.format(oficioPronunciamiento.getFechaOficio()));
				entityOficio.setDisplaySnap("inline");
			}
			
			String intersecaForestal = interseccionViabilidadCoaFacade.getNombresAreasProtegidasForestal(idProyecto, 1);
			entityOficio.setDetalleInterseccionForestal(intersecaForestal);
			
			if(archivoForestal) {
				ProyectoTipoViabilidadCoa proyectoTipoViabilidadCoa = viabilidadCoaFacade.getTipoViabilidadPorProyecto(idProyecto, false);
				ViabilidadCoa viabilidadProyecto = viabilidadCoaFacade.getViabilidadPorTipoProyecto(proyectoTipoViabilidadCoa.getId());
				PronunciamientoForestal oficioPronunciamiento = pronunciamientoForestalFacade.getInformePorViabilidad(viabilidadProyecto.getId());			
				
				entityOficio.setNroOficioViabilidadForestal(oficioPronunciamiento.getNumeroOficio());
				entityOficio.setFechaOficioViabilidadForestal(formatoFecha.format(oficioPronunciamiento.getFechaOficio()));
				entityOficio.setDisplayForestal("inline");
			}
			
			String cargoTemporal = "";
			entityOficio.setNombreAutoridad(null);
			if(usuarioAutoridad != null) {
				entityOficio.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
				cargoTemporal = usuarioFacade.getTipoUsuarioAutoridad(usuarioAutoridad);
			}
			
			entityOficio.setAreaResponsable(areaTramite.getAreaName() + cargoTemporal);
			
			return entityOficio;
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del Oficio.");
		}
		
		return null;
	}
	
	public String generarCodigoOficio(Area area) {
		String anioActual=secuenciasFacade.getCurrentYear();		
		String nombreSecuencia="MAAE-SUIA-ARCH-"+anioActual+"_"+area.getAreaAbbreviation();
		String codigo = Constantes.SIGLAS_INSTITUCION + "-";
		try {
			codigo += area.getAreaAbbreviation()
					+ "-"
					+ anioActual
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4)
					+ "-O";
			
			return codigo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public StreamedContent getStreamOficio(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent),  "application/pdf");
			content.setName(name);
		}
		return content;
	}
	
	public void subirDocumento() {
		try {
			generarOficio(false);
			guardarDocumentoOficioAlfresco();

			if (documentoOficio != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoOficio);
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
			
			RequestContext.getCurrentInstance().update("formDialogs:");
	        RequestContext context = RequestContext.getCurrentInstance();
	        context.execute("PF('signDialog').show();");
	        
		} catch (Exception exception) {
			LOG.error("Ocurrió un error al guardar el oficio", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public DocumentosCOA guardarDocumentoOficioAlfresco() throws Exception {

		documentoOficio = null ; 
			Boolean generarNuevo = false;
			
			List<DocumentosCOA> documentosArchivo = documentosFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), 
					TipoDocumentoSistema.RCOA_PRONUNCIAMIENTO_ARCHIVO_NO_FAVORABLE, claseDocumento); 
			if (documentosArchivo.size() > 0) {
				DocumentosCOA documentoBdd = documentosArchivo.get(0);
				documentoOficio = documentoBdd;
				if(!documentoBdd.getUsuarioCreacion().equals(usuarioAutoridad.getNombre()) 
						|| !JsfUtil.getSimpleDateFormat(documentoBdd.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
					documentoBdd.setEstado(false);
					documentosFacade.guardar(documentoBdd);
					
					generarNuevo = true;
				} 
			} else {
				generarNuevo = true;
			}
			
			if(generarNuevo) {
				documentoOficio = new DocumentosCOA();
				documentoOficio.setContenidoDocumento(archivoOficio);
				documentoOficio.setNombreDocumento(nombreReporte);
				documentoOficio.setTipo("application/pdf");
				documentoOficio.setNombreTabla(claseDocumento);
				documentoOficio.setIdTabla(proyectoLicenciaCoa.getId());
				documentoOficio.setProyectoLicenciaCoa(proyectoLicenciaCoa);

				documentoOficio = documentosFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), 
						"INFORMACION_PRELIMINAR", 0L, documentoOficio, TipoDocumentoSistema.RCOA_PRONUNCIAMIENTO_ARCHIVO_NO_FAVORABLE); 
			}
			
			oficioArchivo.setFechaOficio(new Date()); //para actualizar fecha de oficio
			oficioArchivo = pronunciamientoArchivacionProyectoFacade.guardar(oficioArchivo);

			return documentoOficio;

	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = archivoOficio;
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent),  "application/pdf");
				content.setName(nombreReporte);
			} else
				JsfUtil.addMessageError("Error al generar el archivo");
			
			descargarOficio = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargarOficio == true) {
			documentoManual = new DocumentosCOA();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombreDocumento(event.getFile().getFileName());
			documentoManual.setTipo("application/pdf");
			documentoManual.setNombreTabla(claseDocumento);
			documentoManual.setIdTabla(proyectoLicenciaCoa.getId());
			documentoManual.setProyectoLicenciaCoa(proyectoLicenciaCoa);

			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public void completarTarea() {
		try {
			
			if (token) {
				String idAlfresco = documentoOficio.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El oficio de pronunciamiento no está firmado electrónicamente.");
					return;
				} 
			} else {
				if(subido) {
					DocumentosCOA documento = documentosFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), 
							"INFORMACION_PRELIMINAR", JsfUtil.getCurrentProcessInstanceId(), documentoManual, 
							TipoDocumentoSistema.RCOA_PRONUNCIAMIENTO_ARCHIVO_NO_FAVORABLE); 
					documentoOficio = documento;
				} else {
					JsfUtil.addMessageError("Debe adjuntar el oficio de pronunciamiento firmado.");
					return;
				}				
			}
			
			documentoOficio.setIdProceso(JsfUtil.getCurrentProcessInstanceId());
			documentosFacade.guardar(documentoOficio);

			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			
			procesosArchivadosFacade.guardarArchivado(proyectoLicenciaCoa, 1);
			
			notificarNoFavorable();
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);

		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void notificarNoFavorable() throws Exception {
		String nombreOperador = "";
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		Object[] parametrosCorreoNuevo = null;
		String plantillaMsj = null;
		if (tipoViabilidad.equals("2")) {
			ProyectoLicenciaCoaUbicacion proyectoLicenciaCoaUbicacion = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa);
			UbicacionesGeografica ubicacion = proyectoLicenciaCoaUbicacion.getUbicacionesGeografica();
			
			String provincia = ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
			String canton = ubicacion.getUbicacionesGeografica().getNombre();
			String parroquia = ubicacion.getNombre();
			
			String intersecaF = interseccionViabilidadCoaFacade.getNombresAreasProtegidasForestal(idProyecto, 1);
			String intersecaS = interseccionViabilidadCoaFacade.getNombresAreasProtegidasSnap(idProyecto, 1);
			String interseca = (!intersecaS.equals("")) ? ((!intersecaF.equals("")) ? intersecaS + ", " + intersecaF : intersecaS) : intersecaF;
			
			
			parametrosCorreoNuevo = new Object[] {nombreOperador, proyectoLicenciaCoa.getNombreProyecto(),
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(), provincia, canton, parroquia,
					interseca};
			
			plantillaMsj = "bodyNotificacionViabilidadBypassNoFavorable";
		} else {
			parametrosCorreoNuevo = new Object[] {nombreOperador, proyectoLicenciaCoa.getNombreProyecto(),
					proyectoLicenciaCoa.getCodigoUnicoAmbiental()};
			
			plantillaMsj = "bodyNotificacionViabilidadesNoFavorable";
		}
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(plantillaMsj);
		
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);
		
		if (tipoViabilidad.equals("2")) {
			Email.sendEmail(usuarioOperador, mensajeNotificacion.getAsunto(), notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
		} else {
			String nombreOficio = documentoOficio.getNombreDocumento();
			File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreOficio);
			FileOutputStream file = new FileOutputStream(fileArchivo);
			byte[] oficio = documentosFacade.descargar(documentoOficio.getIdAlfresco());
			file.write(oficio);
			file.close();
			
			List<File> filesDocumentos = new ArrayList<>();
			filesDocumentos.add(fileArchivo);
			
			double tamanioOficio = fileArchivo.length() / (1024 * 1024);
			
			if(tamanioOficio <= 20) {
				
				List<String> listaArchivos = new ArrayList<>();
				listaArchivos.add(nombreOficio);
				
				Email.sendEmailAdjuntos(usuarioOperador, mensajeNotificacion.getAsunto(), notificacion, listaArchivos, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
			} else {
				Email.sendEmail(usuarioOperador, mensajeNotificacion.getAsunto(), notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
			}
		}
		
	}
	
}
