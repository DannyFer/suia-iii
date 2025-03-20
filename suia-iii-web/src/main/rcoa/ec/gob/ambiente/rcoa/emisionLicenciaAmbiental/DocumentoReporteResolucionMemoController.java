package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
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

import javax.ejb.EJB;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.ExpedienteBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ExpedienteBPC;
import ec.gob.ambiente.rcoa.dto.EntityRegistroGeneradorDesecho;
import ec.gob.ambiente.rcoa.dto.EntityRegistroGeneradorDesechoOficio;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.DocumentoResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.OficioResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.ResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.OficioResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ResolucionAmbiental;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.controller.DocumentoReporteInformeTecnicoController;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers.ReporteRegistroGeneradorDesechosController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.OficioPronunciamientoRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PermisoRegistroGeneradorDesechosFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.OficioPronunciamientoRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PermisoRegistroGeneradorDesechos;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.controllers.RegistroSustanciasQuimicasHtml;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ActividadSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosSustanciasQuimicasRcoaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.UbicacionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import lombok.Getter;
import lombok.Setter;

public class DocumentoReporteResolucionMemoController implements Serializable {
	
	private static final long serialVersionUID = -42729579304117925L;

	final Logger LOG = Logger.getLogger(DocumentoReporteInformeTecnicoController.class);

	@EJB
	ProcesoFacade procesoFacade;
	public Map<String, Object> variables;
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	public BandejaTareasBean bandejaTareasBean;

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	public ResolucionAmbientalFacade resolucionAmbientalFacade;
	@EJB
	public OficioResolucionAmbientalFacade oficioResolucionAmbientalFacade;
	@EJB
	public CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	public DocumentoResolucionAmbientalFacade  documentoResolucionAmbientalFacade;
	@EJB
	public DocumentosFacade documentosFacade;
	@EJB
	public ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
    public AsignarTareaFacade asignarTareaFacade;
	@EJB
	public UsuarioFacade usuarioFacade;
	@EJB
	public AreaFacade areaFacade;
	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
	@EJB
	private ActividadSustanciaQuimicaFacade actividadSustanciaQuimicaFacade;
	@EJB
    private UbicacionSustanciaQuimicaFacade ubicacionSustanciaQuimicaFacade;
	@EJB
    private OrganizacionFacade organizacionFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
    private OficioPronunciamientoRgdRcoaFacade oficioPronunciamientoRgdRcoaFacade;
	@EJB
	private PermisoRegistroGeneradorDesechosFacade permisoRegistroGeneradorDesechosFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
    private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
    private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
    private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	@EJB
    private PuntoRecuperacionRgdRcoaFacade puntoRecuperacionRgdRcoaFacade;
	@EJB
	public DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
	@EJB
	public DocumentosSustanciasQuimicasRcoaFacade documentoSustanciasQuimicasFacade;
	@EJB
	private ExpedienteBPCFacade expedienteBPCFacade;
	
	/* BEANs */
	@Getter
	@Setter
	@ManagedProperty(value = "#{observacionesResolucionLicenciaController}")
	ObservacionesResolucionLicenciaController obserbacionesController;
	 
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteResolucion;
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteMemorando;
	@Getter
	@Setter
	private PlantillaReporte plantillaReportePronunciamiento;
	@Getter
	@Setter
	public ProyectoLicenciaCoa proyectoLicenciaCoa;
	@Getter
	@Setter
	public ResolucionAmbiental resolucionAmbiental;
	@Getter
	@Setter
	public OficioResolucionAmbiental documentoResolucion = new OficioResolucionAmbiental();
	@Getter
	@Setter
	public OficioResolucionAmbiental documentoMemorando;
	@Getter
	@Setter
	public OficioResolucionAmbiental documentoPronunciamiento;
	@Getter
	@Setter
	public OficioResolucionAmbiental documentoMemorandoTecnicoJuridico;
	@Setter
    @Getter
	public RegistroSustanciaQuimica registroSustanciaQuimica;
	@Setter
    @Getter
	private InformeOficioRSQ informeRsq, oficioRsq;
	@Setter
    @Getter
	private ProyectoLicenciaCoaUbicacion ubicacionPrincipal;
	@Setter
    @Getter
	public RegistroGeneradorDesechosRcoa registroRcoa;
	@Setter
    @Getter
	private PermisoRegistroGeneradorDesechos permisoRgd;
	@Setter
    @Getter
    public DocumentosRgdRcoa documentoOficioRgd, documentoOficioRgdFirmado;
	@Setter
    @Getter
    public DocumentosRgdRcoa documentoPermisoRgd, documentoPermisoRgdFirmado;
	@Setter
    @Getter
    public DocumentosSustanciasQuimicasRcoa documentoPermisoRsq, documentoPermisoRsqFirmado; 
	@Getter
	@Setter
	private ExpedienteBPC expedienteBPCMemo = new ExpedienteBPC();
	
	@Getter
	@Setter
	public Integer idProyecto;
	
	@Getter
	@Setter
	public Usuario usuarioFirma, usuarioFirmaJuridico, usuarioFirmaRsq, usuarioFirmaRgd;
	
	@Getter
	@Setter
	public Area areaFirmaJuridico, areaFirmaRgd;
	
	@Getter
	@Setter
	public String nombreReporteRgd, nombreReporteOficioRgd, nombreReporteRsq;
	
	@Getter
	@Setter
	public String pathReporteRgd, pathReporteOficioRgd, pathReporteRsq;
	
	@Getter
	@Setter
	public byte[] archivoReporteRgd, archivoReporteOficioRgd, archivoReporteRsq;
	
	@Getter
	@Setter
	public String actividadPrincipal, codigoCiiu, cargoAutoridad, areaResponsableFirmaRgd, cargo, nombreOperador, nombreEmpresa, ruc, identificacionUsuario;
	
	@Getter
	@Setter
	public Boolean juridico, existeProvisional;
	
	
	// CONSTANTES
	// Id's tabla coa_mae.general_catalogs_coa
	public static final Integer RESOLUCION_GECA_ID = 64;
	public static final Integer MEMORANDO_GECA_ID = 65;
	public static final Integer PRONUNCIAMIENTO_GECA_ID = 66;
	public static final Integer MEMORANDO_JURIDICO_GECA_ID = 67;

	public void visualizarResolucion(boolean setCurrentDate) throws Exception {
		expedienteBPCMemo = new ExpedienteBPC();
		expedienteBPCMemo = expedienteBPCFacade.getByProyectoLicenciaCoa(proyectoLicenciaCoa.getId());
		if ((expedienteBPCMemo != null) && (expedienteBPCMemo.getId() != null) && (expedienteBPCMemo.getTieneResolucionFisica()))
		{
			ResolucionAmbiental resolucion = new ResolucionAmbiental();
			resolucion = resolucionAmbientalFacade.getByIdRegistroFlujoByPass(proyectoLicenciaCoa.getId(), 1);			
			List<DocumentoResolucionAmbiental> listado = new ArrayList<DocumentoResolucionAmbiental>();
			listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucion.getId(), TipoDocumentoSistema.RCOA_LA_RESOLUCION);
			DocumentoResolucionAmbiental documento = new DocumentoResolucionAmbiental();
			documento =listado.get(0); 
			byte[] fileArchivo = documentoResolucionAmbientalFacade.descargar(documento.getIdAlfresco());
			//Path path = Paths.get(resolucionPdf.getAbsolutePath());
			String extension = ".pdf";
			String mime = "application/pdf";
			documentoResolucion.setArchivoInforme(fileArchivo);
			documentoResolucion.setNombreFichero(documento.getNombre());
			String nombreReporte = documentoResolucion.getNombreFichero();		
			String reporteHtmlfinal = nombreReporte.replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(documentoResolucion.getArchivoInforme());
			file.close();
			documentoResolucion.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + documentoResolucion.getNombreFichero()));				
		}
		else
		{
			if (null == plantillaReporteResolucion) {
				plantillaReporteResolucion = plantillaReporteFacade.obtenerPlantillaReportePorCodigo(TipoDocumentoSistema.RCOA_LA_RESOLUCION.getIdTipoDocumento(), "Resolución de licencia ambiental");
			}
			documentoResolucion.setNombreFichero("Resolucion");
			
			if(documentoResolucion.getConsiderandoLegal() == null)
				documentoResolucion.setConsiderandoLegal("");
			
			Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
			String unidadEnte = areaTramite.getAreaName();
			String enteResponsable = Constantes.NOMBRE_INSTITUCION;
			String mostrarUnidad = "display: inline";
			
	    	if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
	    		unidadEnte = areaTramite.getArea().getAreaName();
			} else if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
	    		unidadEnte = areaTramite.getArea().getAreaName();
			}else if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_EA)) {
	    		enteResponsable = areaTramite.getAreaName();
	    		mostrarUnidad = "display: none";
			}

	    	Usuario usuarioFirma = asignarAutoridad();    	
	    	if(usuarioFirma == null) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
	    	
	    	documentoResolucion.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
			documentoResolucion.setUnidadEnte(unidadEnte);
			documentoResolucion.setEnteResponsable(enteResponsable);
			documentoResolucion.setMostrarUnidad(mostrarUnidad);
			documentoResolucion.setFecha(JsfUtil.devuelveFechaEnLetras(new Date()));
			// incluir informacion de la sede de la zonal en el documento
			ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
			String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioFirma, "PROYECTORCOA", proyectoLicenciaCoa, null, null);
			documentoResolucion.setUbicacion(nombreCanton);
			
			String nombreReporte = "Resolucion de Licencia Ambiental.pdf";
			
			File resolucionAux = UtilGenerarInforme.generarFichero(plantillaReporteResolucion.getHtmlPlantilla(),documentoResolucion.getNombreFichero(), true, documentoResolucion);
					
			PlantillaReporte plantillaObligacionesResolucion = plantillaReporteFacade.obtenerPlantillaReportePorCodigo(TipoDocumentoSistema.RCOA_LA_RESOLUCION.getIdTipoDocumento(), "Obligaciones Resolución de licencia ambiental");
			File obligacionesAux = UtilGenerarInforme.generarFichero(plantillaObligacionesResolucion.getHtmlPlantilla(),documentoResolucion.getNombreFichero(), true, documentoResolucion);
			
			List<File> listaFiles = new ArrayList<File>();
			listaFiles.add(resolucionAux);
			listaFiles.add(obligacionesAux);
			File resoluciontotalAux = UtilFichaMineria.unirPdf(listaFiles, nombreReporte); 
			
			File resolucionPdf = JsfUtil.fileMarcaAgua(resoluciontotalAux, setCurrentDate ? " - - BORRADOR - - " : " ", BaseColor.GRAY);
			
			Path path = Paths.get(resolucionPdf.getAbsolutePath());
			documentoResolucion.setArchivoInforme(Files.readAllBytes(path));
			documentoResolucion.setNombreFichero(nombreReporte);
			String reporteHtmlfinal = documentoResolucion.getNombreFichero().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(documentoResolucion.getArchivoInforme());
			file.close();
			documentoResolucion.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + documentoResolucion.getNombreFichero()));			
		}
	}
	
	public void visualizarMemorando(boolean setCurrentDate) throws Exception {
		if (null == plantillaReporteMemorando) {
			plantillaReporteMemorando = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_LA_MEMORANDO);
		}
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		String cargoTemporal = usuarioFacade.getTipoUsuarioAutoridad(usuarioFirma);
		
		documentoMemorando.setNombreFichero("Memorando");
		// incluir informacion de la sede de la zonal en el documento
		ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
		String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioFirma, "PROYECTORCOA", proyectoLicenciaCoa, null, null);
		documentoMemorando.setUbicacion(nombreCanton);
		//documentoMemorando.setUbicacion(usuarioFirma.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		documentoMemorando.setFecha(formatoFechaEmision.format(new Date()));
		documentoMemorando.setNombreAutoridad(usuarioFirma.getPersona().getNombre());
		documentoMemorando.setCargoAutoridad(proyectoLicenciaCoa.getAreaResponsable().getAreaName() + cargoTemporal);
		
		File informePdfAux = UtilGenerarInforme.generarFichero(plantillaReporteMemorando.getHtmlPlantilla(),documentoMemorando.getNombreFichero(), true, documentoMemorando);
				
		File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, setCurrentDate ? " - - BORRADOR - - " : " ", BaseColor.GRAY);
		
		Path path = Paths.get(informePdf.getAbsolutePath());
		documentoMemorando.setArchivoInforme(Files.readAllBytes(path));
		documentoMemorando.setNombreFichero("Memorando.pdf");
		String reporteHtmlfinal = documentoMemorando.getNombreFichero().replace("/", "-");
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(documentoMemorando.getArchivoInforme());
		file.close();
		documentoMemorando.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + documentoMemorando.getNombreFichero()));
	}
	
	public void visualizarMemorandoTecnicoJuridico(boolean setCurrentDate) throws Exception {
		if (null == plantillaReporteMemorando) {
			plantillaReporteMemorando = plantillaReporteFacade
					.getPlantillaReporte(TipoDocumentoSistema.RCOA_LA_MEMORANDO);
		}
		
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		documentoMemorandoTecnicoJuridico.setNombreFichero("Memorando Juridico");

		// incluir informacion de la sede de la zonal en el documento
		ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
		String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioFirmaJuridico, "PROYECTORCOA", proyectoLicenciaCoa, null, null);
		documentoMemorandoTecnicoJuridico.setUbicacion(nombreCanton);
		//documentoMemorandoTecnicoJuridico.setUbicacion(usuarioFirmaJuridico.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		documentoMemorandoTecnicoJuridico.setFecha(formatoFechaEmision.format(new Date()));
		documentoMemorandoTecnicoJuridico.setNombreAutoridad(usuarioFirmaJuridico.getPersona().getNombre());
		documentoMemorandoTecnicoJuridico.setCargoAutoridad(areaFirmaJuridico.getAreaName());
		
		File informePdfAux = UtilGenerarInforme.generarFichero(plantillaReporteMemorando.getHtmlPlantilla(),
				documentoMemorandoTecnicoJuridico.getNombreFichero(), true, documentoMemorandoTecnicoJuridico);
				
		File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, setCurrentDate ? "- - BORRADOR - - " : " ", BaseColor.GRAY);
		
		Path path = Paths.get(informePdf.getAbsolutePath());
		documentoMemorandoTecnicoJuridico.setArchivoInforme(Files.readAllBytes(path));
		documentoMemorandoTecnicoJuridico.setNombreFichero("Memorando Juridico.pdf");
		String reporteHtmlfinal = documentoMemorandoTecnicoJuridico.getNombreFichero().replace("/", "-");
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(documentoMemorandoTecnicoJuridico.getArchivoInforme());
		file.close();
		documentoMemorandoTecnicoJuridico.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + documentoMemorandoTecnicoJuridico.getNombreFichero()));
	}
	
	public void visualizarPronunciamiento(boolean setCurrentDate) throws Exception {
		if (null == plantillaReportePronunciamiento) {
			plantillaReportePronunciamiento = plantillaReporteFacade
					.getPlantillaReporte(TipoDocumentoSistema.RCOA_LA_PRONUNCIAMIENTO);
		}
		
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		documentoPronunciamiento.setNombreFichero("Pronunciamiento");
		// incluir informacion de la sede de la zonal en el documento
		ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
		String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioFirmaJuridico, "PROYECTORCOA", proyectoLicenciaCoa, null, null);
		documentoPronunciamiento.setUbicacion(nombreCanton);
		//documentoPronunciamiento.setUbicacion(usuarioFirmaJuridico.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		documentoPronunciamiento.setFecha(formatoFechaEmision.format(new Date()));
		documentoPronunciamiento.setNombreAutoridad(usuarioFirmaJuridico.getPersona().getNombre());
		documentoPronunciamiento.setCargoAutoridad(areaFirmaJuridico.getAreaName());
		
		File informePdfAux = UtilGenerarInforme.generarFichero(plantillaReportePronunciamiento.getHtmlPlantilla(),
				documentoPronunciamiento.getNombreFichero(), true, documentoPronunciamiento);
				
		File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, setCurrentDate ? " - - BORRADOR - - " : " ", BaseColor.GRAY);
		
		Path path = Paths.get(informePdf.getAbsolutePath());
		documentoPronunciamiento.setArchivoInforme(Files.readAllBytes(path));
		documentoPronunciamiento.setNombreFichero("Pronunciamiento.pdf");
		String reporteHtmlfinal = documentoPronunciamiento.getNombreFichero().replace("/", "-");
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(documentoPronunciamiento.getArchivoInforme());
		file.close();
		documentoPronunciamiento.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + documentoPronunciamiento.getNombreFichero()));
	}
	

	public String elimuinarParrafoHtml(String texto) {
		if (texto == null) {
			return null;
		}
		texto = texto.replace("<p>\r\n", "");
		texto = texto.replace("</p>\r\n", "");
		return texto;
	}	

	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}

	public void inicializarResolucionMemorando() {
		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
		resolucionAmbiental = resolucionAmbientalFacade.getByIdRegistroPreliminar(idProyecto);
		Date fechaActual = new Date();
		if (resolucionAmbiental.getId()  != null) {
			List<OficioResolucionAmbiental> listaResolucion = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalAndCategoria(resolucionAmbiental.getId(), RESOLUCION_GECA_ID);
			if(listaResolucion.isEmpty()) {
				documentoResolucion = new OficioResolucionAmbiental();
				documentoResolucion.setResolucionAmbiental(resolucionAmbiental);
				documentoResolucion.setNombreFichero("Resolucin.pdf");
				documentoResolucion.setFechaEstado(fechaActual);
				documentoResolucion.setEstadoAprobacion(false);
				documentoResolucion.setTipoUsuarioAlmacena(catalogoCoaFacade.obtenerCatalogoById(CatalogoTipoCoaEnum.RCOA_LA_RESOLUCION));
				documentoResolucion.setEstado(true);
				documentoResolucion.setUsuarioCreacion("USUARIO");
				documentoResolucion.setFechaCreacion(fechaActual);
				documentoResolucion.setAreaFirma(proyectoLicenciaCoa.getAreaResponsable());
				
				if(proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
					documentoResolucion.setAreaFirma(proyectoLicenciaCoa.getAreaResponsable().getArea());
				}
			} else {
				documentoResolucion = listaResolucion.get(0);
			}
								
			List<OficioResolucionAmbiental> listaMemorando = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalAndCategoria(resolucionAmbiental.getId(), MEMORANDO_GECA_ID);
			if(listaMemorando.isEmpty()) {
				documentoMemorando = new OficioResolucionAmbiental();
				documentoMemorando.setResolucionAmbiental(resolucionAmbiental);
				documentoMemorando.setNombreFichero("Memorando.pdf");
				documentoMemorando.setFechaEstado(fechaActual);
				documentoMemorando.setEstadoAprobacion(false);
				documentoMemorando.setTipoUsuarioAlmacena(catalogoCoaFacade.obtenerCatalogoById(CatalogoTipoCoaEnum.RCOA_LA_MEMORANDO));
				documentoMemorando.setEstado(true);
				documentoMemorando.setUsuarioCreacion("USUARIO");
				documentoMemorando.setFechaCreacion(fechaActual);
				documentoMemorando.setAreaFirma(proyectoLicenciaCoa.getAreaResponsable());
			} else {
				documentoMemorando = listaMemorando.get(0);
				if(documentoMemorando.getEstadoAprobacion()) {
					documentoMemorando = new OficioResolucionAmbiental();
					documentoMemorando.setResolucionAmbiental(resolucionAmbiental);
					documentoMemorando.setNombreFichero("Memorando.pdf");
					documentoMemorando.setFechaEstado(fechaActual);
					documentoMemorando.setEstadoAprobacion(false);
					documentoMemorando.setTipoUsuarioAlmacena(catalogoCoaFacade.obtenerCatalogoById(CatalogoTipoCoaEnum.RCOA_LA_MEMORANDO));
					documentoMemorando.setEstado(true);
					documentoMemorando.setUsuarioCreacion("USUARIO");
					documentoMemorando.setFechaCreacion(fechaActual);
					documentoMemorando.setAreaFirma(proyectoLicenciaCoa.getAreaResponsable());
				}
			}
			
			Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
			String tipoArea = areaTramite.getTipoArea().getSiglas().toUpperCase();
			String rolPrefijo;
			String rolTecnico;
			
			//si es PC lo firma el director sino los coordinadores			
			if(tipoArea.equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				rolPrefijo = "role.resolucion.pc.director";
			} else if(tipoArea.equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
				rolPrefijo = "role.esia.cz.coordinador";
			}else if(areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)){
				rolPrefijo = "role.resolucion.galapagos.director.calidad";
			} else
				rolPrefijo = "role.esia.gad.coordinador";
			
			rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
	    	
	    	List<Usuario> listaTecnicosResponsables = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaTramite);

			if (listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
				LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
			
			usuarioFirma = listaTecnicosResponsables.get(0);
			
		}
	}
	
	public void inicializarMemorandoPronunciamiento(Boolean editar) {
		resolucionAmbiental = resolucionAmbientalFacade.getByIdRegistroPreliminar(idProyecto);
		Date fechaActual = new Date();
		if (resolucionAmbiental.getId() != null) {
			Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
			String tipoArea = areaTramite.getTipoArea().getSiglas().toUpperCase();
			
			areaFirmaJuridico = areaTramite;
			
			//si es PC lo firma el director sino los coordinadores			
			if(tipoArea.equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
				usuarioFirmaJuridico = JsfUtil.getLoggedUser();
				
				if(tipoArea.equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
					areaFirmaJuridico = areaTramite.getArea();
				}
			} else {
				Boolean esPlantaCentral = tipoArea.equals(Constantes.SIGLAS_TIPO_AREA_PC);
				String rolPrefijo = esPlantaCentral ? "role.resolucion.pc.coordinador.juridico" : "role.resolucion.coordinador.juridico";
				String rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
				
				if(esPlantaCentral) {
					areaTramite = areaFacade.getAreaSiglas("CGAJ") ;
					areaFirmaJuridico = areaTramite;
				} 
		    	
		    	List<Usuario> listaTecnicosResponsables = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaTramite);

				if (listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
					LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
				}
				
				usuarioFirmaJuridico = listaTecnicosResponsables.get(0);
			}
			
			List<OficioResolucionAmbiental> listaResolucion = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalAndCategoria(resolucionAmbiental.getId(), RESOLUCION_GECA_ID);
			if(listaResolucion.isEmpty()) {
				documentoResolucion = new OficioResolucionAmbiental();
				documentoResolucion.setResolucionAmbiental(resolucionAmbiental);
				documentoResolucion.setNombreFichero("Resolucin.pdf");
				documentoResolucion.setFechaEstado(fechaActual);
				documentoResolucion.setEstadoAprobacion(false);
				documentoResolucion.setTipoUsuarioAlmacena(catalogoCoaFacade.obtenerCatalogoById(CatalogoTipoCoaEnum.RCOA_LA_RESOLUCION));
				documentoResolucion.setEstado(true);
				documentoResolucion.setUsuarioCreacion("USUARIO");
				documentoResolucion.setFechaCreacion(fechaActual);
				documentoResolucion.setAreaFirma(proyectoLicenciaCoa.getAreaResponsable());
			} else {
				documentoResolucion = listaResolucion.get(0);
			}
				
			List<OficioResolucionAmbiental> listaPronunciamiento = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalAndCategoria(resolucionAmbiental.getId(), PRONUNCIAMIENTO_GECA_ID);
			if(listaPronunciamiento.isEmpty()) {
				documentoPronunciamiento = new OficioResolucionAmbiental();
				documentoPronunciamiento.setResolucionAmbiental(resolucionAmbiental);
				documentoPronunciamiento.setNombreFichero("Pronunciamiento.pdf");
				documentoPronunciamiento.setFechaEstado(fechaActual);
				documentoPronunciamiento.setEstadoAprobacion(false);
				documentoPronunciamiento.setTipoUsuarioAlmacena(catalogoCoaFacade.obtenerCatalogoById(CatalogoTipoCoaEnum.RCOA_LA_PRONUNCIAMIENTO));
				documentoPronunciamiento.setEstado(true);
				documentoPronunciamiento.setUsuarioCreacion("USUARIO");
				documentoPronunciamiento.setFechaCreacion(fechaActual);
				documentoPronunciamiento.setAreaFirma(areaFirmaJuridico);
			} else {
				documentoPronunciamiento = listaPronunciamiento.get(0);
			}
			
			List<OficioResolucionAmbiental> listaMemorandoTecnicoJuridico = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalAndCategoria(resolucionAmbiental.getId(), MEMORANDO_JURIDICO_GECA_ID);
			if(listaMemorandoTecnicoJuridico.isEmpty()) {				
				documentoMemorandoTecnicoJuridico = new OficioResolucionAmbiental();
				documentoMemorandoTecnicoJuridico.setResolucionAmbiental(resolucionAmbiental);
				documentoMemorandoTecnicoJuridico.setNombreFichero("Memorando Juridico.pdf");
				documentoMemorandoTecnicoJuridico.setFechaEstado(fechaActual);
				documentoMemorandoTecnicoJuridico.setEstadoAprobacion(false);
				documentoMemorandoTecnicoJuridico.setTipoUsuarioAlmacena(catalogoCoaFacade.obtenerCatalogoById(CatalogoTipoCoaEnum.RCOA_LA_MEMORANDO_JURIDICO));
				documentoMemorandoTecnicoJuridico.setEstado(true);
				documentoMemorandoTecnicoJuridico.setUsuarioCreacion("USUARIO");
				documentoMemorandoTecnicoJuridico.setFechaCreacion(fechaActual);
				documentoMemorandoTecnicoJuridico.setAreaFirma(areaFirmaJuridico);
			} else {
				documentoMemorandoTecnicoJuridico = listaMemorandoTecnicoJuridico.get(0);
				
				if(editar && documentoMemorandoTecnicoJuridico.getEstadoAprobacion()) {
					documentoMemorandoTecnicoJuridico = new OficioResolucionAmbiental();
					documentoMemorandoTecnicoJuridico.setResolucionAmbiental(resolucionAmbiental);
					documentoMemorandoTecnicoJuridico.setNombreFichero("Memorando Juridico.pdf");
					documentoMemorandoTecnicoJuridico.setFechaEstado(fechaActual);
					documentoMemorandoTecnicoJuridico.setEstadoAprobacion(false);
					documentoMemorandoTecnicoJuridico.setTipoUsuarioAlmacena(catalogoCoaFacade.obtenerCatalogoById(CatalogoTipoCoaEnum.RCOA_LA_MEMORANDO_JURIDICO));
					documentoMemorandoTecnicoJuridico.setEstado(true);
					documentoMemorandoTecnicoJuridico.setUsuarioCreacion("USUARIO");
					documentoMemorandoTecnicoJuridico.setFechaCreacion(fechaActual);
					documentoMemorandoTecnicoJuridico.setAreaFirma(areaFirmaJuridico);
				} 
			}
		}
	}
	
	public void inicializarResolucionPronunciamiento() {
		expedienteBPCMemo = new ExpedienteBPC();
		expedienteBPCMemo = expedienteBPCFacade.getByProyectoLicenciaCoa(proyectoLicenciaCoa.getId());
		if ((expedienteBPCMemo != null) && (expedienteBPCMemo.getId() != null) && (expedienteBPCMemo.getTieneResolucionFisica()))
		{
			ResolucionAmbiental resolucion = new ResolucionAmbiental();
			resolucion = resolucionAmbientalFacade.getByIdRegistroFlujoByPass(proyectoLicenciaCoa.getId(), 1);
			resolucionAmbiental = resolucion;
		}
		else
		{
			resolucionAmbiental = resolucionAmbientalFacade.getByIdRegistroPreliminar(idProyecto);
			if (null != resolucionAmbiental) {
				List<OficioResolucionAmbiental> listaResolucion = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalAndCategoria(resolucionAmbiental.getId(), RESOLUCION_GECA_ID);
				if(!listaResolucion.isEmpty()) {
					documentoResolucion = listaResolucion.get(0);
				}
					
				List<OficioResolucionAmbiental> listaPronunciamiento = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalAndCategoria(resolucionAmbiental.getId(), PRONUNCIAMIENTO_GECA_ID);
				if(!listaPronunciamiento.isEmpty()) {
					documentoPronunciamiento = listaPronunciamiento.get(0);
				}
			}			
		}
	}
	
	public void inicializarVisualizarResolucionMemorando() {
		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
		resolucionAmbiental = resolucionAmbientalFacade.getByIdRegistroPreliminar(idProyecto);
		
		if (resolucionAmbiental.getId()  != null) {
			List<OficioResolucionAmbiental> listaResolucion = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalAndCategoria(resolucionAmbiental.getId(), RESOLUCION_GECA_ID);
			if(listaResolucion.isEmpty()) {
				documentoResolucion = new OficioResolucionAmbiental();
			} else {
				documentoResolucion = listaResolucion.get(0);
			}
								
			List<OficioResolucionAmbiental> listaMemorando = oficioResolucionAmbientalFacade.getByIdResolucionAmbientalAndCategoria(resolucionAmbiental.getId(), MEMORANDO_GECA_ID);
			if(listaMemorando.isEmpty()) {
				documentoMemorando = new OficioResolucionAmbiental();
			} else {
				documentoMemorando = listaMemorando.get(0);
			}
			
			Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
			String tipoArea = areaTramite.getTipoArea().getSiglas().toUpperCase();
			String rolPrefijo;
			String rolTecnico;
			
			//si es PC lo firma el director sino los coordinadores			
			if(tipoArea.equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				rolPrefijo = "role.resolucion.pc.director";
			} else if(tipoArea.equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
				rolPrefijo = "role.esia.cz.coordinador";
			} else
				rolPrefijo = "role.esia.gad.coordinador";
			
			rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
	    	
	    	List<Usuario> listaTecnicosResponsables = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaTramite);

			if (listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
				LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
			
			usuarioFirma = listaTecnicosResponsables.get(0);
			
		}
	}
	
	public void visualizarMemorandoFirmado() {
		try {
			List<DocumentoResolucionAmbiental> documentos = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(
					documentoMemorando.getId(), 
    				TipoDocumentoSistema.RCOA_LA_MEMORANDO);
			if(documentos.size() > 0) {
				documentoMemorando.setNombreFichero("Memorando.pdf");
				
				DocumentoResolucionAmbiental documentoResolucion = documentos.get(0);
				
				File fileDoc = documentoResolucionAmbientalFacade.descargarFile(documentoResolucion);
				
				Path path = Paths.get(fileDoc.getAbsolutePath());
				byte[] contenido = Files.readAllBytes(path);
				String reporteHtmlfinal = documentoMemorando.getNombreFichero().replace("/", "-");
				File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
				FileOutputStream file = new FileOutputStream(archivoFinal);
				file.write(contenido);
				file.close();
				
				documentoMemorando.setArchivoInforme(Files.readAllBytes(path));
				documentoMemorando.setNombreFichero(documentoMemorando.getNombreFichero());
				documentoMemorando.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + documentoMemorando.getNombreFichero()));

			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public void visualizarMemorandoJuridicoFirmado() {
		try {
			List<DocumentoResolucionAmbiental> documentos = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(
					documentoMemorandoTecnicoJuridico.getId(), 
    				TipoDocumentoSistema.EMISION_LICENCIA_MEMORANDO_TECNICO_JURIDICO);
			if(documentos.size() > 0) {
				documentoMemorandoTecnicoJuridico.setNombreFichero("Memorando Juridico.pdf");
				
				DocumentoResolucionAmbiental documentoResolucion = documentos.get(0);
				
				File fileDoc = documentoResolucionAmbientalFacade.descargarFile(documentoResolucion);
				
				Path path = Paths.get(fileDoc.getAbsolutePath());
				byte[] contenido = Files.readAllBytes(path);
				String reporteHtmlfinal = documentoMemorandoTecnicoJuridico.getNombreFichero().replace("/", "-");
				File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
				FileOutputStream file = new FileOutputStream(archivoFinal);
				file.write(contenido);
				file.close();
				
				documentoMemorandoTecnicoJuridico.setArchivoInforme(Files.readAllBytes(path));
				documentoMemorandoTecnicoJuridico.setNombreFichero(documentoMemorandoTecnicoJuridico.getNombreFichero());
				documentoMemorandoTecnicoJuridico.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + documentoMemorandoTecnicoJuridico.getNombreFichero()));

			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public void visualizarPronunciamientoFirmado() {
		try {
			List<DocumentoResolucionAmbiental> documentos = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(
					documentoPronunciamiento.getId(), 
    				TipoDocumentoSistema.RCOA_LA_PRONUNCIAMIENTO);
			if(documentos.size() > 0) {
				documentoPronunciamiento.setNombreFichero("Pronunciamiento conformidad legal_" + documentoPronunciamiento.getCodigoReporte() + ".pdf");
				
				DocumentoResolucionAmbiental documentoResolucion = documentos.get(0);
				
				File fileDoc = documentoResolucionAmbientalFacade.descargarFile(documentoResolucion);
				
				Path path = Paths.get(fileDoc.getAbsolutePath());
				byte[] contenido = Files.readAllBytes(path);
				String reporteHtmlfinal = documentoPronunciamiento.getNombreFichero().replace("/", "-");
				File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
				FileOutputStream file = new FileOutputStream(archivoFinal);
				file.write(contenido);
				file.close();
				
				documentoPronunciamiento.setArchivoInforme(Files.readAllBytes(path));
				documentoPronunciamiento.setNombreFichero(documentoPronunciamiento.getNombreFichero().replace("/", "-"));
				documentoPronunciamiento.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + documentoPronunciamiento.getNombreFichero()));

			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	private Usuario asignarAutoridad() {		
		String rolPrefijo = "role.resolucion.cz.gad.autoridad";
		String rolDirector = "";
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
    	
    	List<Usuario> listaUsuariosResponsables = new ArrayList<Usuario>();
		
		if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
			rolPrefijo = "role.resolucion.pc.autoridad";
			rolDirector = Constantes.getRoleAreaName(rolPrefijo);
			listaUsuariosResponsables = usuarioFacade.buscarUsuarioPorRol(rolDirector);
		} else if (areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
    		rolPrefijo = "role.resolucion.galapagos.autoridad";
		} else if (areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT)) {
			areaTramite = areaTramite.getArea();
		} 
		
		if(!areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
			rolDirector = Constantes.getRoleAreaName(rolPrefijo);
	    	listaUsuariosResponsables = usuarioFacade.buscarUsuariosPorRolYAreaEspecifica(rolDirector, areaTramite);
		}

		if (listaUsuariosResponsables==null || listaUsuariosResponsables.isEmpty()){
			LOG.error("No se encontro usuario " + rolDirector + " en " + areaTramite.getAreaName());
			return null;
		}
		
		Usuario usuarioResponsable = listaUsuariosResponsables.get(0);
		return usuarioResponsable;
    }
	
	public void generarDocumentosRgd() throws Exception {
		
		resolucionAmbiental = resolucionAmbientalFacade.getByIdRegistroPreliminar(proyectoLicenciaCoa.getId());
		if(resolucionAmbiental.getFechaResolucion() == null) {
			resolucionAmbiental.setFechaResolucion(new Date());
			resolucionAmbiental.setNumeroResolucion(documentoResolucion.getCodigoReporte());
			resolucionAmbiental.getNumeroResolucion();
		}
		
		List<RegistroGeneradorDesechosProyectosRcoa> registroList = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyectoLicenciaCoa.getId());
		registroRcoa = registroList.get(0).getRegistroGeneradorDesechosRcoa();
		
		List<PermisoRegistroGeneradorDesechos> permisoList = permisoRegistroGeneradorDesechosFacade.findByRegistroGenerador(registroRcoa.getId());
		permisoRgd = null;
		PermisoRegistroGeneradorDesechos permisoRgdProvisional = null;
		OficioPronunciamientoRgdRcoa oficio = null;
		
		existeProvisional = false;
		
		Boolean generarNuevo = false;
		if(permisoList != null && !permisoList.isEmpty()){
			permisoRgd = permisoList.get(0);
			if(permisoRgd.getNumeroDocumento().contains("PROVISIONAL")) {
				generarNuevo = true;
				existeProvisional = true;
				permisoRgdProvisional = permisoRgd;
				
				permisoRgdProvisional.setEsRegistroFinal(false);
	            permisoRegistroGeneradorDesechosFacade.save(permisoRgdProvisional, JsfUtil.getLoggedUser());
			} 
		}else{
			generarNuevo = true;
		}
		
		if(!existeProvisional && permisoList.size() > 1){
			for (PermisoRegistroGeneradorDesechos item : permisoList) {
				if(item.getNumeroDocumento().contains("PROVISIONAL")) {
					existeProvisional = true;
					permisoRgdProvisional = item;
					
					permisoRgdProvisional.setEsRegistroFinal(false);
		            permisoRegistroGeneradorDesechosFacade.save(permisoRgdProvisional, JsfUtil.getLoggedUser());
		            
		            break;
				} 
			}
		}
		
		Area areaTramite = null;
		if (proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
			UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyectoLicenciaCoa.getIdCantonOficina());
			areaTramite = ubicacion.getAreaCoordinacionZonal();
		} else {
			areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal();
		}
		
		if(generarNuevo) {
			String codigoRgd = null;
			if(existeProvisional) {
				int intIndex = permisoRgdProvisional.getNumeroDocumento().indexOf("PROVISIONAL");
				codigoRgd = permisoRgdProvisional.getNumeroDocumento().substring(0, intIndex - 1);
			} else{
				codigoRgd = JsfUtil.getBean(ReporteRegistroGeneradorDesechosController.class).generarCodigoRGD(areaTramite);
			}
			permisoRgd = new PermisoRegistroGeneradorDesechos();
            permisoRgd.setRegistroGeneradorDesechosRcoa(registroRcoa);
            permisoRgd.setEstado(true);
            permisoRgd.setFechaCreacion(new Date());
            permisoRgd.setFechaCreacionDocumento(new Date());
            permisoRgd.setNumeroDocumento(codigoRgd);
            permisoRgd.setEsRegistroFinal(true);
            permisoRegistroGeneradorDesechosFacade.save(permisoRgd, JsfUtil.getLoggedUser());
            
            String codigoOficioRgd = JsfUtil.getBean(ReporteRegistroGeneradorDesechosController.class).generarCodigoOficio(areaTramite);
            oficio = new OficioPronunciamientoRgdRcoa();
            oficio.setEstado(true);
            oficio.setRegistroGeneradorDesechosRcoa(registroRcoa);
            oficio.setFechaCreacion(new Date());
            oficio.setFechaCreacionDocumento(new Date());
            oficio.setNumeroDocumento(codigoOficioRgd);
            oficioPronunciamientoRgdRcoaFacade.save(oficio, JsfUtil.getLoggedUser());
		} else {
			oficio = new OficioPronunciamientoRgdRcoa();
	        List<OficioPronunciamientoRgdRcoa> listaoficio = oficioPronunciamientoRgdRcoaFacade.findByGeneradorDesechos(registroRcoa.getId());
	        if(listaoficio != null && listaoficio.size() > 0){
	            oficio = listaoficio.get(0);
	        }
		}
		
		//actualizo la fecha de firma de los documentos
		permisoRgd.setFechaCreacionDocumento(new Date());
        permisoRegistroGeneradorDesechosFacade.save(permisoRgd, JsfUtil.getLoggedUser());
        
        oficio.setFechaCreacionDocumento(new Date());
        oficioPronunciamientoRgdRcoaFacade.save(oficio, JsfUtil.getLoggedUser());
        
        Area areaAutoridad = null;
        String rolDirector = null;
        
        if (proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
			UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyectoLicenciaCoa.getIdCantonOficina());
			areaAutoridad = ubicacion.getAreaCoordinacionZonal().getArea();
			areaTramite = ubicacion.getAreaCoordinacionZonal();
			rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");
		} else {
			rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");
			if(proyectoLicenciaCoa.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
				areaAutoridad = proyectoLicenciaCoa.getAreaResponsable();
				rolDirector = Constantes.getRoleAreaName("role.resolucion.galapagos.autoridad");
			} else {
				areaAutoridad = proyectoLicenciaCoa.getAreaInventarioForestal().getArea();
			}
			areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal();
		}
        
        areaFirmaRgd = areaAutoridad;
		
        List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYAreaEspecifica(rolDirector, areaAutoridad);
		if(listaUsuarios != null && !listaUsuarios.isEmpty()){
			usuarioFirmaRgd = listaUsuarios.get(0);
		}else{
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			LOG.error("No se encontro usuario " + rolDirector + " en " + areaAutoridad.getAreaName());						
			return;
		}
		
		actividadPrincipal = "";
		codigoCiiu = "";
        List<ProyectoLicenciaCuaCiuu> listaProyectoActividadesPrincipal = registroGeneradorDesechosRcoaFacade.buscarActividadesCiuPrincipal(proyectoLicenciaCoa);
        if(listaProyectoActividadesPrincipal != null && !listaProyectoActividadesPrincipal.isEmpty()){
            actividadPrincipal = listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getNombre();
            codigoCiiu = listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getCodigo();
        }
        
		areaResponsableFirmaRgd = areaFirmaRgd.getAreaName();

        cargoAutoridad = "";
        
        if(usuarioFirmaRgd.getPersona().getGenero().equals("FEMENINO")){
            cargoAutoridad = "DIRECTORA ZONAL";
        }else if(usuarioFirmaRgd.getPersona().getGenero().equals("MASCULINO")){
            cargoAutoridad = "DIRECTOR ZONAL";
        }else{
            cargoAutoridad = "DIRECTOR/A ZONAL";
        }
        
        identificacionUsuario = proyectoLicenciaCoa.getUsuario().getNombre();
        cargo = "";
        nombreEmpresa = "";
        nombreOperador = "";
        ruc = "";
        juridico = false;
        
        ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa);
        
        if(identificacionUsuario.length() == 10){
            cargo = proyectoLicenciaCoa.getUsuario().getPersona().getTitulo();
            nombreOperador = proyectoLicenciaCoa.getUsuario().getPersona().getNombre();
            nombreEmpresa = " ";
            ruc = " ";
        }else{
            Organizacion organizacion = organizacionFacade.buscarPorRuc(identificacionUsuario);
            if(organizacion != null){
                cargo = organizacion.getCargoRepresentante();
                nombreOperador = organizacion.getPersona().getNombre();
                nombreEmpresa = organizacion.getNombre();
                ruc = organizacion.getRuc();
                juridico = true;
            }else{
                cargo = proyectoLicenciaCoa.getUsuario().getPersona().getTitulo();
                nombreOperador = proyectoLicenciaCoa.getUsuario().getPersona().getNombre();
                nombreEmpresa = " ";
                ruc = " ";
            }
        }
        
        generarOficioRgdFinal(oficio, permisoRgdProvisional);
        
        generarRegistroFinal();
		
	}
	
	public void generarOficioRgdFinal(OficioPronunciamientoRgdRcoa oficio, PermisoRegistroGeneradorDesechos permisoRgdProvisional) throws Exception {
                
        EntityRegistroGeneradorDesechoOficio entityOficio = new EntityRegistroGeneradorDesechoOficio();
        
        entityOficio.setCodigo(oficio.getNumeroDocumento());
        entityOficio.setActividadProyecto(actividadPrincipal);
        entityOficio.setCargo(cargo);
        entityOficio.setCargoAutoridad(areaResponsableFirmaRgd);
        entityOficio.setCedula(usuarioFirmaRgd.getNombre());
        entityOficio.setCodigoCIIU(codigoCiiu);
        entityOficio.setCodigoPermisoRgd(permisoRgd.getNumeroDocumento());
        entityOficio.setCodigoRgd(registroRcoa.getCodigo());
        entityOficio.setFecha(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
        entityOficio.setFechaLetras(JsfUtil.devuelveFechaEnLetrasSinHora(registroRcoa.getFechaCreacion()));
        entityOficio.setNombreAutoridad(usuarioFirmaRgd.getPersona().getNombre());
        
        if(!juridico){
        	entityOficio.setMostrarInfoEmpresa("display: none");
            entityOficio.setNombreEmpresa(" ");
            entityOficio.setOperadorEmpresa(" ");
            entityOficio.setRuc(ruc);
        }else{
        	entityOficio.setMostrarInfoEmpresa("display: inline");
            entityOficio.setNombreEmpresa(nombreEmpresa);
            entityOficio.setOperadorEmpresa("representante de la empresa " + nombreEmpresa + ",");
            entityOficio.setRuc(ruc);
        }
        
        entityOficio.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
        entityOficio.setOperador(nombreOperador);
        
        if(ubicacionPrincipal != null && ubicacionPrincipal.getId() != null){
            entityOficio.setProvincia(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
            entityOficio.setCanton(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
        }else {
            entityOficio.setProvincia(" ");
            entityOficio.setCanton("  ");
        }
        
        entityOficio.setInstitucion(Constantes.NOMBRE_INSTITUCION);
        
        if(existeProvisional) {
        	entityOficio.setMostrarProvisional("display: inline");
        	entityOficio.setMostrarDefinitivo("display: none");
        	
        	entityOficio.setRgdProvisional(permisoRgdProvisional.getNumeroDocumento());
        	entityOficio.setFechaRgdProvisional(JsfUtil.devuelveFechaEnLetras(permisoRgdProvisional.getFechaCreacionDocumento()));
        } else {
        	entityOficio.setMostrarProvisional("display: none");
        	entityOficio.setMostrarDefinitivo("display: inline");
        }
        
//        nombreFichero = "OficioPronunciamiento" + registro.getCodigo()+".pdf";
//        nombreReporte = "OficioPronunciamiento.pdf";
        nombreReporteOficioRgd = "OficioPronunciamiento.pdf";
        
        PlantillaReporte plantillaReporteOficio = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RGD_FINAL_OFICIO_PRONUNCIAMIENTO);
        
        File oficioPdf = UtilGenerarInforme.generarFichero(
                plantillaReporteOficio.getHtmlPlantilla(),
                nombreReporteOficioRgd, true, entityOficio);

        Path pathOficio = Paths.get(oficioPdf.getAbsolutePath());
        String reporteHtmlfinalOficio = nombreReporteOficioRgd;
        archivoReporteOficioRgd = Files.readAllBytes(pathOficio);
        File archivoFinalOficio = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinalOficio));
        FileOutputStream fileOficio = new FileOutputStream(archivoFinalOficio);
        fileOficio.write(Files.readAllBytes(pathOficio));
        fileOficio.close();
        
        setPathReporteOficioRgd(JsfUtil.devolverContexto("/reportesHtml/"+ reporteHtmlfinalOficio));
        
	}
	
	public void generarRegistroFinal() throws Exception {
		SimpleDateFormat fecha = new SimpleDateFormat("dd-MM-yy",new Locale("es"));
        
        String fechaResolucion = fecha.format(resolucionAmbiental.getFechaResolucion());
        
        String infoRgd = "<table border=\"1\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\">";
        infoRgd += "<tbody><tr><td colspan=\"2\"><strong>A. INFORMACION GENERAL DEL REGISTRO DE GENERADOR</strong></td></tr>";
        infoRgd += "<tr><td style=\"width:35%\">Código del trámite:</td><td style=\"width:65%\">" + registroRcoa.getCodigo()  + "</td></tr>";
        infoRgd += "<tr><td style=\"width:35%\">Fecha de primera emisión del Registro de Generador:</td><td style=\"width:65%\">" + fechaResolucion  + "</td></tr>";
        infoRgd+="<tr><td>Número de actualización:</td><td>0</td></tr>";
        infoRgd +="</tbody></table>";
        
        String direccionProyecto = "<ul>";
        if(proyectoLicenciaCoa.getDireccionProyecto() != null && !proyectoLicenciaCoa.getDireccionProyecto().isEmpty())
        	direccionProyecto += "<li>" + proyectoLicenciaCoa.getDireccionProyecto() + "</li>";
        direccionProyecto += "<li>" + ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</li>";
        direccionProyecto += "<li>" + ubicacionPrincipal.getUbicacionesGeografica().getNombre() + "</li>";
        direccionProyecto += "<li>" + ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</li>";
		direccionProyecto += "</ul>";
        
        EntityRegistroGeneradorDesecho entity = new EntityRegistroGeneradorDesecho();
        entity.setInformacionRgd(infoRgd);
        
        entity.setCedulaOperador(identificacionUsuario);
        entity.setNombreOperador(nombreEmpresa);
        entity.setResponsableEmpresa(nombreOperador);
        entity.setMostrarCargo("display: none");
        if(juridico){
            entity.setCargoEmpresa(cargo);
            entity.setMostrarCargo("display: inline");
        }
        
        entity.setCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
        entity.setCodigoCiiu(codigoCiiu);
        entity.setNombreCiiu(actividadPrincipal);
        entity.setNroResolucion(resolucionAmbiental.getNumeroResolucion());
        
        entity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
        entity.setCodigoRegistroGenerador(permisoRgd.getNumeroDocumento());
        entity.setDesechos(getDesechosPeligrosos(registroRcoa));
        entity.setDireccionProyectoRgd(direccionProyecto);
        List<PuntoRecuperacionRgdRcoa> puntosRecuperacion = puntoRecuperacionRgdRcoaFacade.buscarPorRgd(registroRcoa.getId());
        if(puntosRecuperacion != null && puntosRecuperacion.size() > 10)
            entity.setPuntosGeneracion("Ver anexo");
        else
        	entity.setPuntosGeneracion(getUbicacionPuntosMonitoreo(puntosRecuperacion, false));
        
        
        entity.setNombreAutoridad(usuarioFirmaRgd.getPersona().getNombre());
        entity.setCargoAutoridad(cargoAutoridad);
        entity.setEntidadAutoridad(areaFirmaRgd.getAreaName());
        entity.setInstitucion(Constantes.NOMBRE_INSTITUCION);
        
        PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RGD_FINAL_REGISTRO_GENERADOR_DESECHOS);
		
		nombreReporteRgd = "RegistroGeneradorDesechos.pdf";
		
        File informePdf = UtilGenerarInforme.generarFichero(
                plantillaReporte.getHtmlPlantilla(),
                "RegistroGeneradorDesechos.pdf", true, entity);
        
        if(puntosRecuperacion != null && puntosRecuperacion.size() > 10){ 
            File anexoPuntos = UtilGenerarInforme.generarFichero(getUbicacionPuntosMonitoreo(puntosRecuperacion, true), "anexos", true, entity);
            List<File> listaFiles = new ArrayList<File>();
            listaFiles.add(informePdf);
            listaFiles.add(anexoPuntos);
            informePdf = UtilFichaMineria.unirPdf(listaFiles, informePdf.getName()); 
        }

        Path path = Paths.get(informePdf.getAbsolutePath());
        String reporteHtmlfinal = "RegistroGeneradorDesechos.pdf";
        archivoReporteRgd = Files.readAllBytes(path);
        File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
        FileOutputStream file = new FileOutputStream(archivoFinal);
        file.write(Files.readAllBytes(path));
        file.close();
        setPathReporteRgd(JsfUtil.devolverContexto("/reportesHtml/"+ reporteHtmlfinal));
    }
	
	public String getDesechosPeligrosos(RegistroGeneradorDesechosRcoa registro)
    {
        List<DesechosRegistroGeneradorRcoa> listaDesechos = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGeneradorGenerados(registro);
        String desechos = "<table style=\"width: 400px; font-size: 11px !important;\" border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\">"
                + "<tbody><tr>"
                + "<td><strong>Código del residuo o desecho</strong></td>"
                + "<td><strong>Nombre del residuo o desecho peligroso y/o especial</strong></td>"
                + "</tr>";
        for (DesechosRegistroGeneradorRcoa desecho : listaDesechos) {
            desechos += "<tr>";
            desechos += "<td>" + desecho.getDesechoPeligroso().getClave() + "</td>";
            desechos += "<td>" + desecho.getDesechoPeligroso().getDescripcion() + "</td>";
            desechos += "</tr>";
        }
        desechos += "</tbody></table>";
        return desechos;
    }
	
	public String getUbicacionPuntosMonitoreo(List<PuntoRecuperacionRgdRcoa> puntosRecuperacion, Boolean generarCabecera)
    {
        String puntos = "";
        String estilo = (generarCabecera) ? "style=\"margin-top: 5px; margin-bottom: 5px;\"" : "style=\"margin-top: 0; margin-bottom: 0; margin-left:-5px;\"";
        
        puntos += "<table style=\"width: 100%; font-size: 11px !important;\" border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\">";
        puntos += "<tbody>";
        int item = 1;
        
        if(generarCabecera){
        	puntos += "<tr>"
                    + "<td><strong>F. ANEXO <br /> Ubicación de puntos de generación:</td>"
                    + "</tr>";
        }
        
        for (PuntoRecuperacionRgdRcoa punto : puntosRecuperacion) {
        	puntos += "<tr>"
					+ "<td>"
        			+ "<ul " + estilo + ">"
					+ "<li><strong>Punto " + item + "</strong></li>"
					+ "<li><strong>Nombre del punto generación: </strong> " + punto.getNombre()+ "</li>"
					+ "</ul>"
					+ "</td>"
					+ "</tr>";
		}
        
        puntos += "</tbody></table>";
        
        return puntos;
    }
	
	public DocumentosRgdRcoa guardarDocumentoOficioRgd() throws Exception {
		TipoDocumento tipoDoc = new TipoDocumento();
        tipoDoc.setId(TipoDocumentoSistema.RGD_FINAL_OFICIO_PRONUNCIAMIENTO.getIdTipoDocumento());
        
		DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
        documento.setNombre(nombreReporteOficioRgd);
        documento.setExtesion(".pdf");
        documento.setTipoContenido("application/pdf");
        documento.setMime("application/pdf");
        documento.setContenidoDocumento(archivoReporteOficioRgd);
        documento.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
        documento.setTipoDocumento(tipoDoc);
        documento.setIdTable(registroRcoa.getId());
        documento.setRegistroGeneradorDesechosRcoa(registroRcoa);

        documento = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
                registroRcoa.getCodigo(), "GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documento, TipoDocumentoSistema.RGD_FINAL_OFICIO_PRONUNCIAMIENTO);
        
        return documento;
	}
	
	public DocumentosRgdRcoa guardarDocumentoRegistroRgd() throws Exception {
		TipoDocumento tipoDoc = new TipoDocumento();
        tipoDoc.setId(TipoDocumentoSistema.RGD_FINAL_REGISTRO_GENERADOR_DESECHOS.getIdTipoDocumento());
        
		DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
        documento.setNombre(nombreReporteRgd);
        documento.setExtesion(".pdf");
        documento.setTipoContenido("application/pdf");
        documento.setMime("application/pdf");
        documento.setContenidoDocumento(archivoReporteRgd);
        documento.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
        documento.setTipoDocumento(tipoDoc);
        documento.setIdTable(registroRcoa.getId());
        documento.setRegistroGeneradorDesechosRcoa(registroRcoa);

        documento = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
                registroRcoa.getCodigo(), "GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documento, TipoDocumentoSistema.RGD_FINAL_REGISTRO_GENERADOR_DESECHOS);
        
        return documento;
	}
	
	public void cargarDatosRsq() {
		resolucionAmbiental = resolucionAmbientalFacade.getByIdRegistroPreliminar(idProyecto);
		if(resolucionAmbiental.getFechaResolucion() == null) {
			resolucionAmbiental.setFechaResolucion(new Date());
			resolucionAmbiental.setNumeroResolucion(documentoResolucion.getCodigoReporte());
		}
		
		registroSustanciaQuimica = registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
		
		String tipo = "autoridad";
		Area area = registroSustanciaQuimica.getArea();
		List<Usuario> listaUsuarios = null;
		
		String roleKey = "role.rsq." + (area.getTipoArea().getId().intValue() == 1 ? "pc" : "cz") + "." + tipo;
		String rolDirector = Constantes.getRoleAreaName(roleKey);
		try {
			if(area.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
				listaUsuarios = usuarioFacade.buscarUsuarioPorRol(rolDirector);
			} else {
				listaUsuarios = usuarioFacade.buscarUsuariosPorRolYAreaEspecifica(rolDirector, area.getArea()); //si no es PC es zonal
			}
			
			if(listaUsuarios != null && !listaUsuarios.isEmpty()){
				usuarioFirmaRsq = listaUsuarios.get(0);
			} else {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				LOG.error("No se encontro usuario " + rolDirector + " en " + area.getAreaName());						
				return;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			System.out.println("Error al recuperar la zonal correspondiente.");					
			return;
		}
		
		informeRsq = informesOficiosRSQFacade.obtenerPorRSQListaPorTipo(registroSustanciaQuimica,TipoInformeOficioEnum.INFORME_TECNICO).get(0);
		oficioRsq = informesOficiosRSQFacade.obtenerPorRSQListaPorTipo(registroSustanciaQuimica,TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO).get(0);
		
	}
	
	public void generarDocumentoRsq() throws Exception {
		
		List<ActividadSustancia> actividadSustanciaLista=new ArrayList<>();
		List<ActividadSustancia> actividadSustanciaListaTodo = actividadSustanciaQuimicaFacade.obtenerActividadesPorRSQ(registroSustanciaQuimica);
		for (ActividadSustancia actividadSustancia : actividadSustanciaListaTodo) {
			if(actividadSustancia.getCupo()!=null) {
				actividadSustanciaLista.add(actividadSustancia);
			}
		}
		
		List<UbicacionSustancia> ubicacionSustanciaProyectoLista = ubicacionSustanciaQuimicaFacade.obtenerUbicacionesPorRSQ(registroSustanciaQuimica);
		
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		String nombreOperador = usuarioOperador.getPersona().getNombre();
		
		Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
        if (organizacion != null) {
        	nombreOperador = organizacion.getNombre();
        }
		
		nombreReporteRsq = "Registro de sustancias quimicas.pdf";
		
		PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_RSQ_FINAL_REGISTRO_SUSANCIAS_QUIMICAS);
		
		File filePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporteRsq, true,new RegistroSustanciasQuimicasHtml(informeRsq,oficioRsq,actividadSustanciaLista,ubicacionSustanciaProyectoLista,nombreOperador,usuarioFirmaRsq, resolucionAmbiental.getNumeroResolucion(), resolucionAmbiental.getFechaResolucion()),null);
        
		Path path = Paths.get(filePdf.getAbsolutePath());
        archivoReporteRsq = Files.readAllBytes(path);
        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(filePdf.getName()));
        FileOutputStream file = new FileOutputStream(fileArchivo);
        file.write(Files.readAllBytes(path));
        file.close();
        setPathReporteRsq(JsfUtil.devolverContexto("/reportesHtml/"+ filePdf.getName()));
	}
	public DocumentosSustanciasQuimicasRcoa guardarDocumentoRsq() throws Exception {
		TipoDocumento tipoDoc = new TipoDocumento();
        tipoDoc.setId(TipoDocumentoSistema.RCOA_RSQ_FINAL_REGISTRO_SUSANCIAS_QUIMICAS.getIdTipoDocumento());
        
		DocumentosSustanciasQuimicasRcoa documento = new DocumentosSustanciasQuimicasRcoa();
		documento.setNombre(nombreReporteRsq);
		documento.setExtesion(".pdf");
		documento.setMime("application/pdf");					
		documento.setContenidoDocumento(archivoReporteRsq);
		documento.setNombreTabla(RegistroSustanciaQuimica.class.getSimpleName());
		documento.setTipoDocumento(tipoDoc);
		documento.setIdTable(registroSustanciaQuimica.getId());
		
		documento = documentoSustanciasQuimicasFacade.guardarDocumentoAlfrescoImportacion(registroSustanciaQuimica.getNumeroAplicacion(),"REGISTRO SUSTANCIAS QUIMICAS", 0L, documento, TipoDocumentoSistema.RCOA_RSQ_FINAL_REGISTRO_SUSANCIAS_QUIMICAS);
		
		return documento;
	}

}
