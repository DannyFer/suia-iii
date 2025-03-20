package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

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
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeRevisionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PronunciamientoForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeTecnicoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.OficioPronunciamientoEntity;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class OficioRevisionViabilidadPfnBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(OficioRevisionViabilidadPfnBean.class);
	
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
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private InformeRevisionForestalFacade informeInspeccionFacade;
	
	@EJB
	private PronunciamientoForestalFacade pronunciamientoFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private  UsuarioFacade usuarioFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private PersonaFacade personaFacade;
	
	@EJB
	private InformeRevisionForestalFacade informeRevisionForestalFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private PronunciamientoForestal oficioRevision;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteOficio;
	
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
	private Integer idProyecto, idViabilidad, numeroRevision, tipoRegistroOficio;
	
	@Getter
	@Setter
	private String razonSocial, nombreTipoOficio,siglasElabora;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	private Area areaAutoridad;
	
	@Getter
	@Setter
	private Persona personaCrea;
	
	@PostConstruct
	public void init() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			String idProyectoString = (String) variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);
			idViabilidad = viabilidadProyecto.getId();
			
			
			numeroRevision = Integer.valueOf((String) variables.get("numeroRevisionInformacion"));
			
			areaTramite = viabilidadProyecto.getAreaResponsable();
			
			String roldirector = Constantes.getRoleAreaName("role.va.pfn.autoridad.responsable");
			areaTramite = areaTramite.getArea();
		
			List<Usuario> listaUsuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(roldirector, areaTramite);
			
			if(listaUsuarioAutoridad==null || listaUsuarioAutoridad.isEmpty()){
				LOG.error("No se encontro usuario " + roldirector + " en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			usuarioAutoridad = listaUsuarioAutoridad.get(0);
			
			ubicacionPrincipal=proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa).getUbicacionesGeografica();
			
			areaAutoridad = new Area();
			
			if(usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1){
				areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
			}else{
				areaAutoridad = areaTramite;
			}
			
			razonSocial = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos Informe Revision Forestal.");
		}
	}

	public void generarOficio(Boolean marcaAgua) {
		try {
			if(tipoOficio.equals(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_OFICIO_OBSERVACIONES)) {
				tipoRegistroOficio = 2;
			} else if(tipoOficio.equals(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_MEMORANDO_VIABILIDAD)) {
				tipoRegistroOficio = 3;
			}
			
			plantillaReporteOficio = plantillaReporteFacade.getPlantillaReporte(tipoOficio);
			
			oficioRevision = pronunciamientoFacade.getInformePorViabilidad(viabilidadProyecto.getId(), tipoRegistroOficio, numeroRevision);			
			
			if(oficioRevision == null) {
				oficioRevision = new PronunciamientoForestal();
				oficioRevision.setNombreFichero(nombreTipoOficio + ".pdf");
				
				if(viabilidadProyecto.getRequiereApoyo() != null && viabilidadProyecto.getRequiereApoyo()) {
					InformeTecnicoForestal informeApoyo = informeRevisionForestalFacade.getInformePorViabilidad(viabilidadProyecto.getId(), InformeTecnicoForestal.apoyo);
					oficioRevision.setConclusiones(informeApoyo.getConclusiones());
					oficioRevision.setRecomendaciones(informeApoyo.getRecomendaciones());
				}
				siglasElabora=JsfUtil.getSiglas(loginBean.getUsuario().getPersona().getNombre());
			} else {
				personaCrea=personaFacade.buscarPersonaPorPin(oficioRevision.getUsuarioCreacion());
				siglasElabora=JsfUtil.getSiglas(personaCrea.getNombre());
				if(oficioRevision.getDocumentoFirmado() != null && oficioRevision.getDocumentoFirmado()) {
					oficioRevision = new PronunciamientoForestal();
					oficioRevision.setNombreFichero(nombreTipoOficio + ".pdf");
				} else {
					oficioRevision.setNombreFichero(nombreTipoOficio + " " + UtilViabilidad.getFileNameEscaped(oficioRevision.getNumeroOficio().replace("/", "-")) + ".pdf");
				}
			}
			
			oficioRevision.setNombreOficio(nombreTipoOficio + ".pdf");
			
			OficioPronunciamientoEntity oficioEntity = cargarDatos();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporteOficio.getHtmlPlantilla(),
					oficioRevision.getNombreOficio(), true, oficioEntity);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(oficioPdf.getAbsolutePath());
			oficioRevision.setArchivoOficio(Files.readAllBytes(path));
			String reporteHtmlfinal = oficioRevision.getNombreFichero().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(oficioRevision.getArchivoOficio());
			file.close();
			oficioRevision.setOficioPath(JsfUtil.devolverContexto("/reportesHtml/" + oficioRevision.getNombreFichero()));

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public OficioPronunciamientoEntity cargarDatos() {
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		oficioRevision.setFechaOficio(new Date());

		OficioPronunciamientoEntity oficioEntity = new OficioPronunciamientoEntity();
		oficioEntity.setCiudad(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		oficioEntity.setFechaOficio(formatoFecha.format(oficioRevision.getFechaOficio()));
		oficioEntity.setNroOficio(oficioRevision.getNumeroOficio());
		oficioEntity.setAsunto(oficioRevision.getAsunto());
		oficioEntity.setAntecedente(oficioRevision.getAntecedentes());
		oficioEntity.setMarcoLegal(oficioRevision.getMarcoLegal());
		oficioEntity.setConclusiones(oficioRevision.getConclusiones());
		oficioEntity.setRecomendaciones(oficioRevision.getRecomendaciones());
		oficioEntity.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());				
		oficioEntity.setCargoAutoridad(areaAutoridad.getAreaName());
		oficioEntity.setSiglasElabora(siglasElabora);
		
		if(tipoOficio.equals(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_OFICIO_OBSERVACIONES)) {
			oficioEntity.setProponente(razonSocial);
			oficioEntity.setPronunciamiento(oficioRevision.getPronunciamiento());
		} else if(tipoOficio.equals(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_MEMORANDO_VIABILIDAD)) {
			//director de bosques
			Area areaDestino = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
			
			String roldirector = Constantes.getRoleAreaName("role.va.pfn.pc.director.forestal");
		
			List<Usuario> listaUsuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(roldirector, areaDestino);
			
			if(listaUsuarioAutoridad==null || listaUsuarioAutoridad.isEmpty()){
				LOG.error("No se encontro usuario " + roldirector + " en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return null;
			}
			
			Usuario usuarioDestino = listaUsuarioAutoridad.get(0);
			oficioEntity.setNombreDestinatario(usuarioDestino.getPersona().getNombre());
		}
		
		return oficioEntity;
	}
	
	public void guardarOficio() {
		oficioRevision.setNumeroRevision(numeroRevision);
		oficioRevision.setTipoOficio(tipoRegistroOficio);
		oficioRevision.setIdViabilidad(viabilidadProyecto.getId());
		pronunciamientoFacade.guardar(oficioRevision, areaAutoridad);
	}
	
	public void actualizarOficioFirmado() {
		oficioRevision.setDocumentoFirmado(true);
		oficioRevision = pronunciamientoFacade.guardar(oficioRevision);
	}
	
	public DocumentoViabilidad guardarDocumentoOficioAlfresco() throws Exception {
		
		DocumentoViabilidad documentoOficioPronunciamiento = new DocumentoViabilidad();
		documentoOficioPronunciamiento = new DocumentoViabilidad();
		documentoOficioPronunciamiento = new DocumentoViabilidad();
		documentoOficioPronunciamiento.setNombre(oficioRevision.getNombreFichero());
		documentoOficioPronunciamiento.setContenidoDocumento(oficioRevision.getArchivoOficio());
		documentoOficioPronunciamiento.setMime("application/pdf");
		documentoOficioPronunciamiento.setIdTipoDocumento(tipoOficio.getIdTipoDocumento());
		documentoOficioPronunciamiento.setIdViabilidad(viabilidadProyecto.getId());

		documentoOficioPronunciamiento = documentosFacade.guardarDocumentoProceso(
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
				"VIABILIDAD_AMBIENTAL", documentoOficioPronunciamiento, 2, JsfUtil.getCurrentProcessInstanceId());
		return documentoOficioPronunciamiento;
	}
}
