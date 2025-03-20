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
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PronunciamientoForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.OficioPronunciamientoEntity;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
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
public class OficioViabilidadPfnBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(OficioViabilidadPfnBean.class);
	
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
	private InformeInspeccionForestalFacade informeInspeccionFacade;
	
	@EJB
	private PronunciamientoForestalFacade pronunciamientoFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private  UsuarioFacade usuarioFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private PersonaFacade personaFacade;

	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private PronunciamientoForestal oficioPronunciamiento;
	
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
	private InformeInspeccionForestal informeViabilidad;
	
	@Getter
	@Setter
	private Integer idProyecto, numeroRevision;
	
	@Getter
	@Setter
	private String razonSocial,siglasElabora;
	
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
			
			numeroRevision = Integer.valueOf((String) variables.get("numeroRevisionInformacion"));

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);
			
			informeViabilidad = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			if(!informeViabilidad.getTipoInforme().equals(InformeInspeccionForestal.viabilidad)) {
				return;
			}
			
			areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
			
			String roldirector = Constantes.getRoleAreaName("role.va.pfn.pc.director.forestal");
		
			List<Usuario> listaUsuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(roldirector, areaTramite);
			
			if(listaUsuarioAutoridad==null || listaUsuarioAutoridad.isEmpty()){
				LOG.error("No se encontro usuario " + roldirector + " en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			usuarioAutoridad = listaUsuarioAutoridad.get(0);
			
			areaAutoridad = new Area();
			
			if(usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1){
				areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
			}else{
				areaAutoridad = areaTramite.getArea();
			}
			
			razonSocial = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del informe.");
		}
	}
	
	public void seleccionarTipoPronunciamiento() {
		if(oficioPronunciamiento.getEsPronunciamientoFavorable() != null) {
			if (oficioPronunciamiento.getEsPronunciamientoFavorable()) {
				tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_FAVORABLE;
			} else {
				tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_NO_FAVORABLE;
			}
		}
	}

	public void generarOficio(Boolean marcaAgua) {
		try {
			
			String nombreDocOficio = "Oficio";
			
			oficioPronunciamiento = pronunciamientoFacade.getInformePorViabilidad(viabilidadProyecto.getId(), PronunciamientoForestal.oficioViabilidad, numeroRevision);
			
			if(oficioPronunciamiento == null) {
				oficioPronunciamiento = new PronunciamientoForestal();
				oficioPronunciamiento.setNombreFichero(nombreDocOficio + ".pdf");
				siglasElabora=JsfUtil.getSiglas(loginBean.getUsuario().getPersona().getNombre());
			} else {
				personaCrea=personaFacade.buscarPersonaPorPin(oficioPronunciamiento.getUsuarioCreacion());
				siglasElabora=JsfUtil.getSiglas(personaCrea.getNombre());
				oficioPronunciamiento.setNombreFichero(nombreDocOficio + " " + UtilViabilidad.getFileNameEscaped(oficioPronunciamiento.getNumeroOficio().replace("/", "-")) + ".pdf");
				
				seleccionarTipoPronunciamiento();
			}
			
			oficioPronunciamiento.setNombreOficio(nombreDocOficio + ".pdf");
			
			plantillaReporteOficio = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_FAVORABLE);
			
			OficioPronunciamientoEntity oficioEntity = cargarDatos();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporteOficio.getHtmlPlantilla(),
					oficioPronunciamiento.getNombreOficio(), true, oficioEntity);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(oficioPdf.getAbsolutePath());
			oficioPronunciamiento.setArchivoOficio(Files.readAllBytes(path));
			String reporteHtmlfinal = oficioPronunciamiento.getNombreFichero().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(oficioPronunciamiento.getArchivoOficio());
			file.close();
			oficioPronunciamiento.setOficioPath(JsfUtil.devolverContexto("/reportesHtml/" + oficioPronunciamiento.getNombreFichero()));

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public OficioPronunciamientoEntity cargarDatos() {
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		oficioPronunciamiento.setFechaOficio(new Date());

		OficioPronunciamientoEntity oficioEntity = new OficioPronunciamientoEntity();
		oficioEntity.setCiudad(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		oficioEntity.setFechaOficio(formatoFecha.format(oficioPronunciamiento.getFechaOficio()));
		oficioEntity.setNroOficio(oficioPronunciamiento.getNumeroOficio());
		oficioEntity.setProponente(razonSocial);
		oficioEntity.setAsunto(oficioPronunciamiento.getAsunto());
		oficioEntity.setAntecedente(oficioPronunciamiento.getAntecedentes());
		oficioEntity.setMarcoLegal(oficioPronunciamiento.getMarcoLegal());
		oficioEntity.setPronunciamiento(oficioPronunciamiento.getPronunciamiento());
		oficioEntity.setConclusiones(oficioPronunciamiento.getConclusiones());
		oficioEntity.setRecomendaciones(oficioPronunciamiento.getRecomendaciones());
		oficioEntity.setPronunciamiento(oficioPronunciamiento.getPronunciamiento());
		oficioEntity.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());				
		
		oficioEntity.setCargoAutoridad(areaAutoridad.getAreaName());
		oficioEntity.setSiglasElabora(siglasElabora);
		
		return oficioEntity;
	}
	
	public void guardarOficio() {
		oficioPronunciamiento.setIdViabilidad(viabilidadProyecto.getId());
		oficioPronunciamiento.setNumeroRevision(numeroRevision);
		oficioPronunciamiento.setTipoOficio(PronunciamientoForestal.oficioViabilidad);
		
		pronunciamientoFacade.guardar(oficioPronunciamiento, areaAutoridad);
	}
	
	public void actualizarOficioFirmado() {
		oficioPronunciamiento.setDocumentoFirmado(true);
		oficioPronunciamiento = pronunciamientoFacade.guardar(oficioPronunciamiento);
	}
	
	public DocumentoViabilidad guardarDocumentoOficioAlfresco() throws Exception {
		DocumentoViabilidad documentoOficioPronunciamiento = new DocumentoViabilidad();
		documentoOficioPronunciamiento.setNombre("Oficio " + oficioPronunciamiento.getNumeroOficio() + ".pdf");
		documentoOficioPronunciamiento.setContenidoDocumento(oficioPronunciamiento.getArchivoOficio());
		documentoOficioPronunciamiento.setMime("application/pdf");
		documentoOficioPronunciamiento.setIdTipoDocumento(tipoOficio.getIdTipoDocumento());
		documentoOficioPronunciamiento.setIdViabilidad(viabilidadProyecto.getId());

		documentoOficioPronunciamiento = documentosFacade.guardarDocumento(
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
				"VIABILIDAD_AMBIENTAL", documentoOficioPronunciamiento, 3);
		return documentoOficioPronunciamiento;
	}
}
