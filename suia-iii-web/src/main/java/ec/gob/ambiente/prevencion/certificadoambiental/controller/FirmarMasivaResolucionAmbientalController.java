package ec.gob.ambiente.prevencion.certificadoambiental.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.categoria2.v2.controller.FichaAmbientalGeneralFinalizarControllerV2;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.VerProyectoController;
import ec.gob.ambiente.proyectos.bean.ProyectosAdminBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.proyectos.controllers.ProyectosAdminController;
import ec.gob.ambiente.rcoa.dto.DocumentoTareaDTO;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.autoridadambiental.service.AutoridadAmbientalFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.comun.classes.Selectable;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.control.documentos.service.DocumentosService;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentoProyecto;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.PlantillaHeaderFooter;
import ec.gob.ambiente.suia.reportes.PlantillaHeaderFooterOficio;
import ec.gob.ambiente.suia.reportes.ReporteLicenciaAmbientalCategoriaII;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import lombok.Getter;
import lombok.Setter;
@ManagedBean
@ViewScoped
public class FirmarMasivaResolucionAmbientalController {
	
	private static final String[] LIBRE_APROVECHAMIENTO = new String[] { "21.02.06.02" };
	private static final String[] MINERIA_ARTESANAL = new String[] { "21.02.01.01" };
	private static final String[] MINERIA_EXPLORACION_INICIAL = new String[] { "21.02.02.01", "21.02.03.06" };
	private static final String[] MINERIA_PERFORACION_EXPLORATIVA = new String[] { "21.02.03.05", "21.02.04.03",
			"21.02.05.03", "21.02.02.03" };
	public static final String CategoriaIILicencia = "CategoriaIILicencia";

	private static final Logger LOG = Logger.getLogger(ProyectosAdminController.class);
	@Getter
	@Setter
	private List<Selectable<ProyectoLicenciamientoAmbiental>> listaProyectos, listaSelectProyectos, listaProyectosFilter;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosAdminBean}")
	private ProyectosAdminBean proyectosBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBeanR;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;
	
	@Getter
	@Setter
	private Integer docId;
	
	@Getter
	@Setter
	private Long processId;
	
	@Getter
	@Setter
	private String nombreFichero, nombreReporte;
	
	@Getter
	@Setter
	private String pathTotal;
	
	@Getter
	@Setter
	private String idsDocumentosAlfresco;
	
	@Getter
	@Setter
	private String nombreDocumentoFirmado, urlAlfresco;
	
	@Getter
	@Setter
	private String tramite;
	
	@Getter
	@Setter
	private Documento documentoCertificado, documentoManual, documento, documentoCargado, documentoInformacionManual;

	@Getter
	@Setter
	private List<Documento> listaDocumentos;
	
	//RCOA
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoRcoa;
	
	@Getter
	@Setter
	private List<String> notificaciones;
	
	@Getter
	@Setter
	private Boolean habilitarForm;
	
	@Getter
	@Setter
	private boolean token, mostrarBotonFirma;
	
	@Getter
	@Setter
	private boolean documentoDescargado = false, documentoSubido = false, ingresoFirma = false;
	
	@Getter
	@Setter
	private boolean ambienteProduccion, mostrarFirma;
	
	@Getter
	@Setter
	private List<DocumentoTareaDTO> listaDocumentoTarea;
	
	@Getter
	@Setter
	public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	
	@Getter
	@Setter
	private String idProceso, idTarea;
	
	@Getter
	@Setter
	private Boolean mostrarFirmaMasiva;
	
	@Getter
	@Setter
	private boolean firmaSoloToken, informacionSubida;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private CategoriaIIFacade categoriaIIFacade;
	
	@EJB
	private InformeProvincialGADFacade informeProvincialGADFacade;
	
	@EJB
	private DocumentosService documentosService;
	
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private DocumentosCoaFacade documentosCoaFacade;	
	
	@Getter
	@Setter
	private List<ProyectoLicenciamientoAmbiental> listaRegistrosSeleccionados;
	
	@Getter
	@Setter
	private Boolean mostrarDocumento;

	
	@PostConstruct
	public void init() {
		try {

			listaProyectos = new ArrayList<Selectable<ProyectoLicenciamientoAmbiental>>();
			listaSelectProyectos = new ArrayList<Selectable<ProyectoLicenciamientoAmbiental>>();
			listaProyectosFilter = new ArrayList<Selectable<ProyectoLicenciamientoAmbiental>>();
			listaRegistrosSeleccionados = new ArrayList<>();
			
			notificaciones = new ArrayList<String>();
			listaDocumentos = new ArrayList<>();
			idsDocumentosAlfresco = "";
			habilitarForm = true;
			mostrarBotonFirma = true;
			mostrarDocumento = false;

			listaDocumentoTarea = new ArrayList<>();
			
			proyectosBeanR.setProyecto(null);

			if (JsfUtil.devolverObjetoSession("codigoProyecto") == null
					|| JsfUtil.devolverObjetoSession("codigoProyecto").equals("")) {

				if (loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getSiglas().equals("PC")
						|| loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getSiglas()
								.toUpperCase().equals("ZONALES")
						|| loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getSiglas()
								.equals("OT")) {

					List<ProyectoLicenciamientoAmbiental> proyectos = proyectoLicenciamientoAmbientalFacade
							.obtenerRegistroAmbientales(loginBean.getUsuario().getListaAreaUsuario().get(0).getArea());
					listaProyectos = new ArrayList<>();
					for (ProyectoLicenciamientoAmbiental proyecto : proyectos) {
						listaProyectos.add(new Selectable<>(proyecto));
					}
				} else {
					List<ProyectoLicenciamientoAmbiental> proyectos = proyectoLicenciamientoAmbientalFacade
							.obtenerRegistroAmbientales(loginBean.getUsuario().getListaAreaUsuario().get(0).getArea());
					listaProyectos = new ArrayList<>();
					for (ProyectoLicenciamientoAmbiental proyecto : proyectos) {
						listaProyectos.add(new Selectable<>(proyecto));
					}
				}
				
				listaProyectosFilter.addAll(listaProyectos);

				ingresoFirma = false;
			}

			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			token = true;

			if (!ambienteProduccion) {
				verificaToken();
				documentoDescargado = true;
			}
			urlAlfresco = "";

			if (ingresoFirma) {
				JsfUtil.cargarObjetoSession("codigoProyecto", "");

				proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(tramite);

				List<String[]> lista = proyectoLicenciamientoAmbientalFacade.obtenerProcesoId(proyecto.getCodigo());

				for (String[] codigoProyecto : lista) {

					idTarea = codigoProyecto[0];
					idProceso = codigoProyecto[2];
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public File generarLicenciaAmbiental(ProyectoLicenciamientoAmbiental proyectoActivo, Integer marcaAgua) {
		File archivoTemporal = null;
		File archivoLicenciaTemporal = null;
		File archivoAnexoTemporal = null;
		List<File> listaFiles = new ArrayList<File>();

		Boolean mineria = isMineriaArtesanal(proyectoActivo);
		Boolean libreAprovechamiento = isLibreAprovechamiento(proyectoActivo);
		Boolean exploracionInicial = isExploracionInicial(proyectoActivo);
		Boolean perforacionExplorativa = isPerforacionExplorativa(proyectoActivo);
		Boolean comercializacionHidrocarburos = proyectoActivo.getCatalogoCategoria().getCodigo().equals("21.01.07.03"); // false;
		Persona persona = null;
		/**
		 * Nombre:SUIA Descripción: Validación para obtener el cargo de la persona que
		 * firmará el documentos registro ambiental. ParametrosIngreso:
		 * PArametrosSalida: Fecha:16/08/2015
		 */

		String cargo = null;		
		/**
		 * FIN Validación para obtener el cargo de la persona que firmará el documentos
		 * registro ambiental.
		 */

//		byte[] firma = null;

		Usuario usuarioSecretario = null;
		String reporteFinal = null;
		//
		try {

			/**
			 * Nombre:SUIA Descripción: Para obterner el anexo de las coordenadas de acuerdo
			 * al área. ParametrosIngreso: PArametrosSalida: Fecha:16/08/2015
			 */
			if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
				cargo = proyectoActivo.getAreaResponsable().getAreaName();
				if (proyectoActivo.getAreaResponsable().getTipoEnteAcreditado().equals("MUNICIPIO")) {
					reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo_entes_municipio";
				} else {
					reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo_entes";
				}			
			} else {				
				reporteFinal = "anexo_coordenadas_licencia_ambiental_categoria_II_Completo";
			}
			/**
			 * FIN Para obterner el anexo de las coordenadas de acuerdo al área
			 */
			
			usuarioSecretario = JsfUtil.getLoggedUser();
			persona = usuarioSecretario.getPersona();
			
			if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("OT")){
				cargo = "DIRECCIÓN ZONAL";		
			}else if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("EA")){
				if (usuarioSecretario != null) {					
					if (Usuario.isUserInRole(usuarioSecretario, AutoridadAmbientalFacade.GAD_MUNICIPAL)) {
						cargo = AutoridadAmbientalFacade.GAD_MUNICIPAL;
					} else {
						cargo = AutoridadAmbientalFacade.DIRECTOR_PROVINCIAL;
					}
				}
			}else{
				cargo = "SUBSECRETARIO DE CALIDAD AMBIENTAL";
			}
			
			
		} catch (Exception e) {
		}

		try {
			/**
			 * CAMBIO DE LOGO
			 */

			Area areaResponsable = proyectoActivo.getAreaResponsable();
			String nombre_pie = null;
			String nombre_logo = null;
			URL pie = null;
			URL logo = null;
			byte[] pie_datos = null;
			pie_datos = categoriaIIFacade.getLogo(proyectoActivo.getAreaResponsable());

			nombre_pie = "pie__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
			nombre_logo = "logo__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
			pie = UtilDocumento.getRecursoImage("ente/" + pie);
			System.out.println("VALOR DE LOGO =======>" + logo);

			if (pie == null) {
				try {
					nombre_pie = "logo_ministerio_pie_documento.png";
					
					// pie_datos = documentosFacade.descargarDocumentoPorNombre(nombre_pie);
					// File fi = new File(JsfUtil.devolverPathImagenMae());
					// byte[] fileContent = Files.readAllBytes(fi.toPath());
					File archivo = new File(JsfUtil.devolverPathImagenEnte(nombre_pie));
					// File archivo = new File(JsfUtil.devolverPathImagenMae());
					FileOutputStream file = new FileOutputStream(archivo);
					// file.write(pie_datos);
					// file.write(fileContent);
					file.close();
				} catch (Exception e) {
					LOG.error("Error al obtener la imagen del pie para el área " + areaResponsable.getAreaAbbreviation()
							+ " en /Documentos Fijos/DatosEnte/" + nombre_pie);
					File fi = new File(JsfUtil.devolverPathImagenMae());
					byte[] fileContent = Files.readAllBytes(fi.toPath());
					File archivo = new File(JsfUtil.devolverPathImagenMae());
					FileOutputStream file = new FileOutputStream(archivo);
					file.write(fileContent);
					file.close();
				}
			}

			if (mineria) {

				PlantillaReporte plantillaReporte = informeProvincialGADFacade.obtenerPlantillaReporte(
						TipoDocumentoSistema.RESOLUCION_REGISTRO_AMBIENTAL_020.getIdTipoDocumento());

//				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaIIA("LicenciaAmbientalCategoríaII",categoriaIIFacade.cargarDatosLicenciaAmbientalMineroLibreAprovechamiento(fichaAmbientalPmaBean.getFichaAmbientalMineria().getNumeroResolucion(), proyectoActivo,mineria, JsfUtil.getLoggedUser().getNombre()),
//						"licencia_ambiental_categoria_ii_mineria", "Licencia Ambiental Categoría II", persona, firma,
//						pie_datos, cargo, proyectoActivo.getAreaResponsable(), marcaAgua, false);
			} else if (libreAprovechamiento) {
//				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaIIA(
//						"LicenciaAmbientalCategoríaII",
//						categoriaIIFacade.cargarDatosLicenciaAmbientalMineroLibreAprovechamiento(
//								fichaAmbientalPmaBean.getFicha().getNumeroOficio(), proyectoActivo, mineria,
//								JsfUtil.getLoggedUser().getNombre()),
//						"licencia_ambiental_categoria_ii_mineria_la", "Licencia Ambiental Categoría II", persona, firma,
//						pie_datos, cargo, proyectoActivo.getAreaResponsable(), marcaAgua, false);
			} else if (exploracionInicial) {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaII(
						"LicenciaAmbientalCategoríaII",
						categoriaIIFacade.cargarDatosLicenciaAmbientalExploracionInicial(proyectoActivo,
								JsfUtil.getLoggedUser().getNombre()),
						"licencia_ambiental_categoria_ii_exploracion_inicial", "Licencia Ambiental Categoría II",
						persona, null, cargo, proyectoActivo.getAreaResponsable(), marcaAgua, false);
			} else if (perforacionExplorativa) {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaII(
						"LicenciaAmbientalCategoríaII",
						categoriaIIFacade.cargarDatosLicenciaAmbientalPerforacionExplorativa(proyectoActivo,
								JsfUtil.getLoggedUser().getNombre()),
						"licencia_ambiental_categoria_ii_exploracion_inicial", "Licencia Ambiental Categoría II",
						persona, null, cargo, proyectoActivo.getAreaResponsable(), marcaAgua, true);
			} else if (comercializacionHidrocarburos) {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaIIA(
						"LicenciaAmbientalCategoríaII",
						categoriaIIFacade.cargarDatosLicenciaAmbientalCompleto(proyectoActivo,
								JsfUtil.getLoggedUser().getNombre()),
						"licencia_ambiental_categoria_ii_comercializacion_hidrocarburos",
						"Licencia Ambiental Categoría II", persona, null,pie_datos, cargo, proyectoActivo.getAreaResponsable(),
						marcaAgua, false);
			} else {
				archivoLicenciaTemporal = ReporteLicenciaAmbientalCategoriaII.crearLicenciaAmbientalCategoriaIIA(
						"LicenciaAmbientalCategoríaII",
						categoriaIIFacade.cargarDatosLicenciaAmbientalCompleto(proyectoActivo,
								JsfUtil.getLoggedUser().getNombre()),
						"licencia_ambiental_categoria_ii_n", "Licencia Ambiental Categoría II", persona, null,
						pie_datos, cargo, proyectoActivo.getAreaResponsable(), marcaAgua, false);
			}
			// cargarDatosAnexosLicenciaAmbiental
			// anexo_coordenadas_licencia_ambiental_categoria_IIMINERIA_PERFORACION_EXPLORATIVA
			archivoAnexoTemporal = ReporteLicenciaAmbientalCategoriaII
					.crearAnexoCoordenadasLicenciaAmbientalCategoriaII("Anexo coordenadas",
							categoriaIIFacade.cargarDatosAnexosCompletoLicenciaAmbiental(proyectoActivo), reporteFinal,
							"Anexo coordenadas", proyectoActivo.getAreaResponsable(), marcaAgua);

			listaFiles.add(archivoLicenciaTemporal);
			listaFiles.add(archivoAnexoTemporal);
			archivoTemporal = UtilFichaMineria.unirPdf(listaFiles, "Ficha_Ambiental");

			Path path = Paths.get(archivoTemporal.getAbsolutePath());
			byte[] archivo = Files.readAllBytes(path);
			String reporteHtmlfinal = archivoTemporal.getName().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));

			// archivoFinalMemory = archivoFinal;
			FileOutputStream file = new FileOutputStream(archivoFinal);
			Document document = new Document(PageSize.A4, 36, 36, 54, 54);
			PdfWriter pdfWriter = PdfWriter.getInstance(document, file);
			pdfWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));
			boolean isInforme = true;
			pdfWriter.setPageEvent(
					(PdfPageEvent) (isInforme ? new PlantillaHeaderFooter() : new PlantillaHeaderFooterOficio()));
			file.write(archivo);
			file.close();
			nombreReporte = archivoTemporal.getName();
			pathTotal = archivoFinal.getAbsolutePath();
			pathTotal = (JsfUtil.devolverContexto("/reportesHtml/" + nombreReporte));

			Documento documentoOficio = new Documento();

			documentoOficio.setNombre("Resolucion_del_Registro_Ambiental.pdf");
			documentoOficio.setContenidoDocumento(archivo);
			documentoOficio.setNombreTabla("CategoriaIILicencia");
			documentoOficio.setIdTable(proyectoActivo.getId());
			documentoOficio.setDescripcion("Resolucion de Registro Ambiental");
			documentoOficio.setMime("application/pdf");
			documentoOficio.setExtesion(".pdf");

			documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(proyectoActivo.getCodigo(),
					CategoriaIILicencia, null, documentoOficio,
					TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL, null);			

			String idAlfrescoWks = (documento.getIdAlfresco().split(";"))[0];
			String documentUrl = getUrlAlfresco(documento.getIdAlfresco());

			idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
			
			documento.setIdProyecto(proyectoActivo.getId());
			listaDocumentos.add(documento);
			proyectoActivo.setResolucion(documento);

			return archivoTemporal;

		} catch (Exception e) {
			LOG.error("Error al intentar generar el archivo del Registro Ambiental.", e);
			return null;
		}

	}

	public void cargarDatosBandeja(ProyectoLicenciamientoAmbiental proyectoSeleccionado, boolean individual) throws MalformedURLException, CmisAlfrescoException, IOException {
		// solo si es categoria II y esta en la tarea de "Validar Pago por servicios
		// administrativos"
		
		proyecto = proyectoSeleccionado;
		docId = documentosFacade.findDocuIdByDocuTableId(proyectoSeleccionado.getId());
		processId = documentosFacade.findProcessInstanceIdByDocuId(docId);
				
		proyectosBeanR.setProyecto(proyectoSeleccionado);
				
		SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");	
		
		TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

		tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

		Documento documento = documentosFacade.documentoXTablaIdXIdDocUnicos(proyectoSeleccionado.getId(),
				CategoriaIILicencia, tipoDocumento);		
		
		boolean firmado = false;
		if(documento != null && documento.getId() != null && documentosFacade.verificarFirmaAlfresco(documento.getIdAlfresco())){
			firmado = true;
		}		
		
		if(documento != null && formateador.format(documento.getFechaCreacion()).equals(formateador.format(new Date()))
				&& !documento.getNombre().equals("Resolución del Registro Ambiental.pdf") && !firmado){
			
			File fileDoc = documentosFacade.descargarFile(documento.getIdAlfresco());
			
			Path path = Paths.get(fileDoc.getAbsolutePath());
			
			byte[] contenido = Files.readAllBytes(path);
			String reporteHtmlfinal = documento.getNombre().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(contenido);
			file.close();
			
			pathTotal = JsfUtil.devolverContexto("/reportesHtml/" + documento.getNombre());
			nombreReporte = documento.getNombre();
//			archivoReporte = contenido;				
			
			String idAlfrescoWks = (documento.getIdAlfresco().split(";"))[0];
			String documentUrl = getUrlAlfresco(documento.getIdAlfresco());

			idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
			
			documento.setIdProyecto(proyectoSeleccionado.getId());
			listaDocumentos.add(documento);
			proyectoSeleccionado.setResolucion(documento);
			
			if(individual){
				mostrarDocumento = true;
			}	
			
		}else{
			
			if(documento != null && documento.getId() != null){
				documento.setEstado(false);
				documentosFacade.actualizarDocumento(documento);
			}			
			
			FichaAmbientalGeneralFinalizarControllerV2 fichaAmbientalGeneralFinalizarControllerV2 = JsfUtil
					.getBean(FichaAmbientalGeneralFinalizarControllerV2.class);
			if (proyectoSeleccionado != null && proyectoSeleccionado.getCodigo() != null && 
					proyectoSeleccionado.getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_II) {

				try {
					File archivoTemporal = generarLicenciaAmbiental(proyectoSeleccionado, 2);
					
					if(individual){
						mostrarDocumento = true;
					}				

				} catch (Exception e) {
					LOG.error(e.getMessage());
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				}
			} else {
				if (proyectosBeanR.getProyectoRcoa() != null) {

					try {
						PerforacionExplorativa objProyectoPE = fichaAmbientalMineria020Facade
								.cargarPerforacionExplorativaRcoa(proyectosBeanR.getProyectoRcoa().getId());
						if (objProyectoPE != null && objProyectoPE.getId() != null) {
							// recupero la tarea actual que se genero
							Long processInstanceId = bandejaTareasBean.getProcessId();
							TaskSummary tareaActual = procesoFacade.getCurrenTask(JsfUtil.getLoggedUser(),
									processInstanceId);
							// si la tarea actual es "Descargar documentos de Registro Ambiental " finalizo
							// el proceso
							if (tareaActual.getName().toLowerCase()
									.contains("descargar documentos de registro ambiental")) {
								Map<String, Object> processVariables = procesoFacade
										.recuperarVariablesProceso(JsfUtil.getLoggedUser(), processInstanceId);
								ProcessInstanceLog processLog = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(),
										processInstanceId);
								bandejaTareasBean.setTarea(new TaskSummaryCustomBuilder()
										.fromSuiaIII(processVariables, processLog.getProcessName(), tareaActual).build());
//								fichaAmbientalGeneralFinalizarControllerV2.generarFichaRegistroAmbientalRCOA();
								fichaAmbientalGeneralFinalizarControllerV2.setDescargarFicha(true);
								fichaAmbientalGeneralFinalizarControllerV2.setDescargarRegistro(true);
								fichaAmbientalGeneralFinalizarControllerV2.redireccionarBandeja();
								if (fichaAmbientalGeneralFinalizarControllerV2.getDescargarRegistro()) {
									fichaAmbientalGeneralFinalizarControllerV2.direccionar();
									fichaAmbientalGeneralFinalizarControllerV2.cambiarEstadoTarea();
								}
								if(individual){
									mostrarDocumento = true;
								}								
							}

						}
					} catch (ec.gob.ambiente.suia.exceptions.ServiceException | JbpmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}		
	}		

	public String seleccionar(ProyectoLicenciamientoAmbiental proyectoCustom) {
		
		try {
			ProyectoLicenciamientoAmbiental proyecto = proyectoCustom;
			proyectosBean.setProyectoToShow(proyecto);
						
			return JsfUtil.actionNavigateTo("/proyectos/resumenProyecto.jsf");
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return null;
		}

	}
	
	private boolean isMineriaArtesanal(ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto, MINERIA_ARTESANAL);
		return result;
	}
	
	private boolean isLibreAprovechamiento(ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto, LIBRE_APROVECHAMIENTO);
		return result;
	}
	
	private boolean isExploracionInicial(ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto, MINERIA_EXPLORACION_INICIAL);
		return result;
	}
	
	private boolean isPerforacionExplorativa(ProyectoLicenciamientoAmbiental proyecto) {
		boolean result = isCatalogoCategoriaCodigoInicia(proyecto, MINERIA_PERFORACION_EXPLORATIVA);
		return result;
	}
	
	public String getUrlAlfresco(String documento) {
		String urlAlfresco = null;
		try {
			urlAlfresco = documentosCoaFacade.direccionDescarga(documento);
			String tiketStr = "alf_ticket=";
			if (!urlAlfresco.endsWith(tiketStr)) {
				int pos = urlAlfresco.lastIndexOf(tiketStr) + tiketStr.length();
				urlAlfresco = urlAlfresco.substring(0, pos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return urlAlfresco;
	}
	protected boolean isCatalogoCategoriaCodigoInicia(ProyectoLicenciamientoAmbiental proyecto, String[] values) {
		try {
			String code = proyecto.getCatalogoCategoria().getCodigo();
			for (String string : values) {
				if (code.startsWith(string))
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * FIRMA ELECTRONICA
	 */
	public boolean verificaToken() {
		if (firmaSoloToken) {
			token = true;
			return token;
		}

		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
	
	public void setProyectoToShow(ProyectoLicenciamientoAmbiental proyecto) throws CmisAlfrescoException {
		this.proyecto = proyecto;
		JsfUtil.getBean(VerProyectoController.class).verProyecto(proyecto.getId());
	}
	
	public void enviarParaFirma() {	
		try {			
			
			idsDocumentosAlfresco = "";
			proyectosBeanR.setProyecto(null);
			
			listaRegistrosSeleccionados = new ArrayList<ProyectoLicenciamientoAmbiental>();
			for(Selectable<ProyectoLicenciamientoAmbiental> obj : listaSelectProyectos){
				listaRegistrosSeleccionados.add(obj.getValue());
			}
			
			for(ProyectoLicenciamientoAmbiental proyecto : listaRegistrosSeleccionados){
				cargarDatosBandeja(proyecto, false);
			}
			
			habilitarForm = true;
			
			if(idsDocumentosAlfresco.equals("")) {
				JsfUtil.addMessageError("Error al recuperar los documentos a firmar.");
				habilitarForm = false;
			}else{
				RequestContext.getCurrentInstance().execute("PF('dlgProyecto').show();");	
			}						
			
		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
		}
	}
	
	public StreamedContent descargarDocumento(Documento itemFirma) {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			String nombreDocumento = "";
			
			documentoContent = documentosFacade.descargar(itemFirma.getIdAlfresco());
			nombreDocumento = itemFirma.getNombre();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(nombreDocumento);
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	
	public String firmarDocumento() {
		try {
					
			if (!idsDocumentosAlfresco.equals("")) {

				return DigitalSign.sign(idsDocumentosAlfresco, loginBean
						.getUsuario().getNombre());
			}
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
		
		return "";
	}
	
	@SuppressWarnings("static-access")
	public String firmarOficio() {
		try {			
			
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;
			tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

			if (proyectosBeanR.getProyecto() != null && proyectosBeanR.getProyecto().getId() != null) {
				documentoCargado = documentosFacade.documentoXTablaIdXIdDocUnicos(proyectosBeanR.getProyecto().getId(),
								CategoriaIILicencia, tipoDocumento);
			}

			if (documentoCargado != null && documentoCargado.getIdAlfresco() != null) {
				String documento = documentosFacade.direccionDescarga(documentoCargado);
				DigitalSign firmaE = new DigitalSign();
				documentoDescargado = true;
								
				return firmaE.sign(documento, loginBean.getUsuario().getNombre());

			} else
				return "";
		} catch (Throwable e) {
			e.printStackTrace();
			return "";
		}
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
	
	
	public StreamedContent descargarPdf() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent;
			Documento auxdoc = new Documento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

			tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

			auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(proyectosBeanR.getProyecto().getId(),
					CategoriaIILicencia, tipoDocumento);

			documentoContent = documentosFacade.descargar(auxdoc.getIdAlfresco());
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent), "application/octet-stream");
				content.setName(auxdoc.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}

		catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
	
	
	public StreamedContent descargarInformacion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent;
			Documento auxdoc = new Documento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

			tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

			auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(proyectosBeanR.getProyecto().getId(),
					CategoriaIILicencia, tipoDocumento);

			documentoContent = documentosFacade.descargar(auxdoc.getIdAlfresco());
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(auxdoc.getNombre());
				documentoDescargado = true;
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}

		catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerInformacionFirmada(FileUploadEvent event) throws ServiceException, CmisAlfrescoException {
		System.out.println("Valor de documentoDescargado===========>" + documentoDescargado);

		if (documentoDescargado) {

			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInformacionManual = new Documento();
			documentoInformacionManual.setId(null);
			documentoInformacionManual.setContenidoDocumento(contenidoDocumento);
			documentoInformacionManual.setNombre(event.getFile().getFileName());
			documentoInformacionManual.setExtesion(".pdf");
			documentoInformacionManual.setMime("application/pdf");
			documentoInformacionManual.setNombre("Resolucion_del_Registro_Ambiental.pdf");

			documentoInformacionManual.setIdTable(proyectosBeanR.getProyecto().getId());
			documentoInformacionManual.setNombreTabla(CategoriaIILicencia);
			String proyectoIdStr = String.valueOf(proyecto.getId());
			
			documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(proyectoIdStr, CategoriaIILicencia,
					processId, documentoInformacionManual,
					TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL, null);
			
			DocumentoProyecto documentoProyecto = new DocumentoProyecto();
			documentoProyecto.setDocumento(documento);
			documentoProyecto.setProyectoLicenciamientoAmbiental(proyecto);
			documentosService.guardarDocumentoProyecto(documentoProyecto);

			informacionSubida = true;
			nombreDocumentoFirmado = event.getFile().getFileName();
		} else {
			JsfUtil.addMessageError("No ha descargado el documento para la firmas");
		}
	}
	
	public void cerrar(){
		mostrarDocumento = false;
	}
	
	public void enviar(){
		try {

			if (!idsDocumentosAlfresco.equals("")) {				
				for (Documento documento : listaDocumentos) {

					ProyectoLicenciamientoAmbiental proyecto = new ProyectoLicenciamientoAmbiental();
					proyecto = proyectoLicenciamientoAmbientalFacade.cargarProyectoFullPorId(documento.getIdProyecto());
					if (proyecto != null) {
						DocumentoProyecto documentoProyecto = new DocumentoProyecto();
						documentoProyecto.setDocumento(documento);
						documentoProyecto.setProyectoLicenciamientoAmbiental(proyecto);
						documentosService.guardarDocumentoProyecto(documentoProyecto);
						
						List<DocumentosTareasProceso> lista = documentosFacade.obtenerDocumentosProcesos(documento.getId());
						
						for(DocumentosTareasProceso doc : lista){
							
							Long idProcess = proyectoLicenciamientoAmbientalFacade.obtenerInstanciaProyecto(proyecto.getCodigo());
							
							doc.setProcessInstanceId(idProcess);
							documentosFacade.guardarDocumentoProceso(doc);
						}
					}
				}
				proyectosBeanR.setProyecto(null);
			}
			
			if(proyectosBeanR.getProyecto() != null){
				
				TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;
				tipoDocumento = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL;

				if (proyectosBeanR.getProyecto() != null && proyectosBeanR.getProyecto().getId() != null) {
					documentoCargado = documentosFacade.documentoXTablaIdXIdDocUnicos(proyectosBeanR.getProyecto().getId(),
									CategoriaIILicencia, tipoDocumento);
				}

				if (documentoCargado != null && documentoCargado.getIdAlfresco() != null) {
					DocumentoProyecto documentoProyecto = new DocumentoProyecto();
					documentoProyecto.setDocumento(documentoCargado);
					documentoProyecto.setProyectoLicenciamientoAmbiental(proyectosBeanR.getProyecto());
					documentosService.guardarDocumentoProyecto(documentoProyecto);
					
					List<DocumentosTareasProceso> lista = documentosFacade.obtenerDocumentosProcesos(documentoCargado.getId());
					
					for(DocumentosTareasProceso doc : lista){
						Long idProcess = proyectoLicenciamientoAmbientalFacade.obtenerInstanciaProyecto(proyectosBeanR.getProyecto().getCodigo());
						
						doc.setProcessInstanceId(idProcess);
						documentosFacade.guardarDocumentoProceso(doc);
					}
				}
			}
			
			proyectosBeanR.setProyecto(null);
			
			JsfUtil.redirectTo("/prevencion/certificadoambiental/listadoResolucionFirma.jsf");			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
