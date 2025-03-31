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

import lombok.Getter;
import lombok.Setter;

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

@ManagedBean
@ViewScoped
public class MemoOficioPatrimonioBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(MemoOficioPatrimonioBean.class);
	
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
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private  UsuarioFacade usuarioFacade;
	
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
	//@Setter
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
	private InformeTecnicoEsIA informeTecnico;
	
	@Getter
	@Setter
	private Integer idProyecto, numeroObservaciones, tipoOficio, idInforme;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private Boolean esRequerido;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@PostConstruct
	public void init() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			
			esRequerido = false;

			String idProyectoString = (String) variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);

			proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
			
			areaTramite = proyecto.getAreaResponsable();
			
			ubicacionPrincipal=proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto).getUbicacionesGeografica();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del oficio.");
		}
	}
	
	public void cargarDatos() {
		if(idInforme != null)
			oficioPronunciamiento = oficioPronunciamientoEsIAFacade.getPorEstudioInforme(esiaProyecto.getId(), idInforme);
		
		
		String rolAutoridad = "";
//		Area area = JsfUtil.getLoggedUser().getArea();
		Area area = new Area();
		if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 1){
			area = JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea();
		}else if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null){
			area = proyecto.getAreaInventarioForestal(); //no área trámite inventario lo gestiona MAAE
		}
		
		
		if(oficioPronunciamiento != null && oficioPronunciamiento.getAreaResponsable()!=null) {
			area = oficioPronunciamiento.getAreaResponsable();
		}
		
		if (area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
			area = area.getArea();
		
		if(tipoOficio.equals(1)) {
			tipoDocumento = TipoDocumentoSistema.EIA_MEMORANDO_RCOA;
			
			if (informeTecnico.getTipoInforme().equals(InformeTecnicoEsIA.snap)) {
				rolAutoridad = Constantes.getRoleAreaName("role.esia.pc.autoridad.conservacion");
			} else if (informeTecnico.getTipoInforme().equals(InformeTecnicoEsIA.forestal) ||
					informeTecnico.getTipoInforme().equals(InformeTecnicoEsIA.forestalInventario)) {
				rolAutoridad = Constantes.getRoleAreaName("role.esia.pc.autoridad.bosques");
			}
		} else {
			tipoDocumento = TipoDocumentoSistema.EIA_OFICIO_RESPUESTA_RCOA;
			rolAutoridad = "AUTORIDAD AMBIENTAL";
		}		
	
		List<Usuario> listaUsuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, area);
		
		if (listaUsuarioAutoridad == null || listaUsuarioAutoridad.isEmpty()) {
			LOG.error("No se encontro usuario " + rolAutoridad + " en " + area.getAreaName());
			return;
		}
		
		usuarioAutoridad = listaUsuarioAutoridad.get(0);
		
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
			
			String nombreOficio = (tipoOficio.equals(1)) ? "Memorando" : "OficioPronunciamiento";
			if(oficioPronunciamiento == null) {				
				oficioPronunciamiento = new OficioPronunciamientoEsIA();
				oficioPronunciamiento.setNombreReporte(nombreOficio + ".pdf");
				oficioPronunciamiento.setInformacionProyecto(esiaProyecto);
				
//				Area area = JsfUtil.getLoggedUser().getArea();
				/**
				 * Cambio para un técnico con varias oficinas técnicas.
				 */
				Area area = new Area();
				
				if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 1){
					area = JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea();
				}else{
					area = proyecto.getAreaInventarioForestal();
				}
				
				if (area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
					area = area.getArea();
				
				oficioPronunciamiento.setAreaResponsable(area);
			} else {
				if(oficioPronunciamiento.getCodigoOficio()== null)
					oficioPronunciamiento.setNombreReporte(nombreOficio + ".pdf");
				else
					oficioPronunciamiento.setNombreReporte(nombreOficio + "_" + UtilViabilidad.getFileNameEscaped(oficioPronunciamiento.getCodigoOficio().replace("/", "-")) + ".pdf");
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
	
	
	public EntityOficioPronunciamientoEsIA cargarDatosOficio () {
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		//recuperar destinatario
		String nombreDestinatario = "";
		String cargoDestinatario = "";
		if(tipoOficio.equals(1)) {
			Area area = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL);
			String rolAutoridad = "AUTORIDAD AMBIENTAL MAE";
			
			List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, area);
			if (listaUsuario == null || listaUsuario.size() == 0)			
			{
				System.out.println("No se encontró autoridad ambiental en "+ area);
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return null;
			}
			
			nombreDestinatario = listaUsuario.get(0).getPersona().getNombre();
			cargoDestinatario = area.getAreaName();
			
		} else {
			String rolAutoridad = "AUTORIDAD AMBIENTAL";
			
			List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaTramite);
			if (listaUsuario == null || listaUsuario.size() == 0)			
			{
				System.out.println("No se encontró autoridad ambiental en "+ areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return null;
			}
			
			nombreDestinatario = listaUsuario.get(0).getPersona().getNombre();
			cargoDestinatario = areaTramite.getAreaName();
		}
		
		
		EntityOficioPronunciamientoEsIA entity = new EntityOficioPronunciamientoEsIA();
		
		if(usuarioAutoridad != null) {
			if(tipoOficio.equals(1)) {
				entity.setUbicacion(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			}else{
		        // incluir informacion de la sede de la zonal en el documento
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
				String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyecto, null, null);
				entity.setUbicacion(sedeZonal);
			}
			entity.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
			
			/**
			 * Cambio para un técnico con varias oficinas técnicas
			 */
			Area areaAutoridad = new Area(); 
			
			if(usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1){
				areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
			}else if(usuarioAutoridad.getListaAreaUsuario() != null){
				areaAutoridad = oficioPronunciamiento.getAreaResponsable();
			}
			
//			entity.setAreaAutoridad(usuarioAutoridad.getArea().getAreaName());
			entity.setAreaAutoridad(areaAutoridad.getAreaName());
			if(areaAutoridad.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES))
				entity.setAreaAutoridad(Constantes.CARGO_AUTORIDAD_ZONAL);
		} else {
			JsfUtil.addMessageError("Error al generar el oficio de pronunciamiento");
		}
		
		entity.setNumeroOficio(oficioPronunciamiento.getCodigoOficio());
		
		entity.setFechaEmision(formatoFechaEmision.format(new Date()));
		entity.setNombreDestinatario(nombreDestinatario);
		entity.setCargoDestinatario(cargoDestinatario);
		entity.setAsunto(oficioPronunciamiento.getAsunto());
		entity.setAntecedentes(oficioPronunciamiento.getAntecedentes());
		entity.setPronunciamiento(oficioPronunciamiento.getPronunciamiento());
		
		
		return entity;
	}
	
	public void guardarDatos() {
		
//		Area areaTramite = (usuarioAutoridad != null) ? usuarioAutoridad.getArea()  : null;
		
		Area areaTramite = new Area();
		
		if(usuarioAutoridad.getListaAreaUsuario() == null || usuarioAutoridad.getListaAreaUsuario().isEmpty()){
			areaTramite = null;
		}else if(usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1){
			areaTramite = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
		}else{
			if(proyecto.getAreaInventarioForestal().getTipoArea().getSiglas().equals("DP")){//galápagos
				areaTramite = proyecto.getAreaInventarioForestal();
			}else
				areaTramite = proyecto.getAreaInventarioForestal().getArea();
		}
		
		oficioPronunciamiento.setIdInforme(idInforme);		
		oficioPronunciamiento.setFechaOficio(new Date());
		oficioPronunciamiento.setTipoOficio(tipoOficio);
		oficioPronunciamientoEsIAFacade.guardar(oficioPronunciamiento, areaTramite, tipoOficio);
		
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
			
			documentoOficio = documentosFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(), "OFICIO_PRONUNCIAMIENTO", documentoOficio, tipoDocumento);

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
			
			documentoOficio = documentosFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(), "OFICIO_PRONUNCIAMIENTO", documentoManual, tipoDocumento);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
}
