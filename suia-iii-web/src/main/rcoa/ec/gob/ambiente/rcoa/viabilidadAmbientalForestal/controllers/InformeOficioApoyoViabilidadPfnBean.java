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

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

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
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeRevisionForestalEntity;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeTecnicoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.OficioPronunciamientoEntity;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
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
public class InformeOficioApoyoViabilidadPfnBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(InformeOficioApoyoViabilidadPfnBean.class);
	
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
	private  UsuarioFacade usuarioFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private InformeTecnicoForestal informeApoyo;
	
	@Getter
	@Setter
	private PronunciamientoForestal oficioPronunciamiento;
	
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
	private Integer idProyecto, idTarea, numeroRevision;
	
	@Getter
	@Setter
	private String razonSocial;
	
	@Getter
	@Setter
	private Boolean esTecnico;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	private Area areaAutoridad;
	
	@PostConstruct
	public void init() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			String idProyectoString = (String) variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);
			idTarea = (int)bandejaTareasBean.getTarea().getTaskId();
			
			numeroRevision = Integer.valueOf((String) variables.get("numeroRevisionInformacion"));

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto); 
			
			esTecnico = false;
			
			informeApoyo = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId(), InformeTecnicoForestal.apoyo, numeroRevision);
			
			String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			if (tarea.equals("elaborarDocumentosApoyoForestal")) {
				esTecnico = true;
				
				if(informeApoyo != null && !informeApoyo.getIdTarea().equals(idTarea)) {
					InformeTecnicoForestal informeNuevo = (InformeTecnicoForestal) SerializationUtils.clone(informeApoyo);
					informeNuevo.setId(null);
					informeNuevo.setNumeroInforme(null);
					informeNuevo.setIdTarea(idTarea);
					informeApoyo = informeInspeccionFacade.guardarInformeApoyo(informeNuevo, viabilidadProyecto.getAreaApoyo());
				}
			}
			
			plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_APOYO);
			
			areaTramite = viabilidadProyecto.getAreaApoyo();
			
			String roldirector = Constantes.getRoleAreaName("role.va.pfn.autoridad.responsable");
		
			List<Usuario> listaUsuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(roldirector, areaTramite.getArea());
			
			if(listaUsuarioAutoridad==null || listaUsuarioAutoridad.isEmpty()){
				LOG.error("No se encontro usuario " + roldirector + " en " + areaTramite.getArea().getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			usuarioAutoridad = listaUsuarioAutoridad.get(0);
			
			ubicacionPrincipal=proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa).getUbicacionesGeografica();
			
			areaAutoridad = areaTramite.getArea();
			
			razonSocial = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del informe.");
		}
	}
	
	public void generarInforme(Boolean marcaAgua) {
		try {
			String nombreDocInforme = "Informe tecnico";
			
			if(informeApoyo == null) {
				informeApoyo = new InformeTecnicoForestal();
				informeApoyo.setNombreFichero(nombreDocInforme + ".pdf");
			} else {
				informeApoyo.setNombreFichero(nombreDocInforme + " " + UtilViabilidad.getFileNameEscaped(informeApoyo.getNumeroInforme().replace("/", "-")) + ".pdf");
			}
			informeApoyo.setNombreReporte(nombreDocInforme + ".pdf");
			
			InformeRevisionForestalEntity informeTecnicoGenerador = cargarDatosInforme();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					informeApoyo.getNombreReporte(), true, informeTecnicoGenerador);
			
			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(informePdf.getAbsolutePath());
			informeApoyo.setArchivoInforme(Files.readAllBytes(path));
			String reporteHtmlfinal = informeApoyo.getNombreFichero().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(informeApoyo.getArchivoInforme());
			file.close();
			informeApoyo.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informeApoyo.getNombreFichero()));

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error al cargar el informe tecnico de apoyo forestal", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public InformeRevisionForestalEntity cargarDatosInforme() {
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		informeApoyo.setFechaElaboracion(new Date());
		
		String ubicacion = ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + " - "
				+ ubicacionPrincipal.getUbicacionesGeografica().getNombre() + " - " 
				+ ubicacionPrincipal.getNombre();
		
		InformeRevisionForestalEntity informeEntity = new InformeRevisionForestalEntity();
		
		informeEntity.setUnidadResponsable(areaTramite.getArea().getAreaAbbreviation());
		informeEntity.setNombreUnidadResponsable(areaTramite.getArea().getAreaName());
		informeEntity.setNroInforme(informeApoyo.getNumeroInforme());
		informeEntity.setUbicacion(ubicacion);
		informeEntity.setCiudad(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		informeEntity.setFecha(formatoFecha.format(informeApoyo.getFechaElaboracion()));
		informeEntity.setNroTramite(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		informeEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
		informeEntity.setRazonSocial(razonSocial);
		informeEntity.setSuperficieProyecto(proyectoLicenciaCoa.getSuperficie().toString());
		informeEntity.setAntecedentes(informeApoyo.getAntecedentes());
		informeEntity.setMarcoLegal(informeApoyo.getMarcoLegal());
		informeEntity.setResultadosRevision(informeApoyo.getResultadosRevision());
		informeEntity.setConclusiones(informeApoyo.getConclusiones());
		informeEntity.setRecomendaciones(informeApoyo.getRecomendaciones());
		informeEntity.setElaboradoPor("");
		
		//interseca
		String interseccionesProyecto = interseccionViabilidadCoaFacade.getInterseccionesForestal(idProyecto, 2);
		informeEntity.setIntersecaProyecto(interseccionesProyecto);
		
		informeEntity.setNombreTecnico(JsfUtil.getLoggedUser().getPersona().getNombre());
		informeEntity.setAreaTecnico(viabilidadProyecto.getAreaApoyo().getAreaName());
		
		
		return informeEntity;
	}
	
	public void guardarInforme() {
		informeApoyo.setNumeroRevision(numeroRevision);
		informeApoyo.setIdViabilidad(viabilidadProyecto.getId());
		informeApoyo.setIdTarea(idTarea);
		informeApoyo.setTipoInforme(InformeTecnicoForestal.apoyo);
		
		informeInspeccionFacade.guardarInformeApoyo(informeApoyo, areaTramite);
	}

	public void generarOficio(Boolean marcaAgua) {
		try {
			
			String nombreDocOficio = "Memorando";
			
			tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_MEMORANDO_APOYO;
			
			plantillaReporteOficio = plantillaReporteFacade.getPlantillaReporte(tipoOficio);
			
			oficioPronunciamiento = pronunciamientoFacade.getInformePorViabilidad(viabilidadProyecto.getId(), PronunciamientoForestal.memorandoApoyo, numeroRevision);
			
			if(oficioPronunciamiento == null) {
				oficioPronunciamiento = new PronunciamientoForestal();
				oficioPronunciamiento.setNombreFichero(nombreDocOficio + ".pdf");
			} else {
				oficioPronunciamiento.setNombreFichero(nombreDocOficio + " " + UtilViabilidad.getFileNameEscaped(oficioPronunciamiento.getNumeroOficio().replace("/", "-")) + ".pdf");
			}
			
			oficioPronunciamiento.setNombreOficio(nombreDocOficio + ".pdf");
			
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
		oficioEntity.setAsunto(oficioPronunciamiento.getAsunto());
		oficioEntity.setAntecedente(oficioPronunciamiento.getAntecedentes());
		oficioEntity.setMarcoLegal(oficioPronunciamiento.getMarcoLegal());
		oficioEntity.setPronunciamiento(oficioPronunciamiento.getPronunciamiento());
		oficioEntity.setConclusiones(oficioPronunciamiento.getConclusiones());
		oficioEntity.setRecomendaciones(oficioPronunciamiento.getRecomendaciones());
		oficioEntity.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());				
		oficioEntity.setCargoAutoridad(areaAutoridad.getAreaName());
		
		String roldirector = Constantes.getRoleAreaName("role.va.pfn.cz.autoridad");
		
		List<Usuario> listaUsuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(roldirector, viabilidadProyecto.getAreaResponsable().getArea());
		
		if(listaUsuarioAutoridad==null || listaUsuarioAutoridad.isEmpty()){
			LOG.error("No se encontro usuario " + roldirector + " en " + viabilidadProyecto.getAreaResponsable().getArea());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			return null;
		}
		
		Usuario usuarioAutoridadDestino = listaUsuarioAutoridad.get(0);
		oficioEntity.setNombreDestinatario(usuarioAutoridadDestino.getPersona().getNombre());
		
		return oficioEntity;
	}
	
	public void guardarOficio() {
		oficioPronunciamiento.setIdViabilidad(viabilidadProyecto.getId());
		oficioPronunciamiento.setNumeroRevision(numeroRevision);
		oficioPronunciamiento.setTipoOficio(PronunciamientoForestal.memorandoApoyo);
		
		pronunciamientoFacade.guardar(oficioPronunciamiento, areaAutoridad);
	}
	
	public void actualizarOficioFirmado() {
		oficioPronunciamiento.setDocumentoFirmado(true);
		oficioPronunciamiento = pronunciamientoFacade.guardar(oficioPronunciamiento);
	}
	
	public DocumentoViabilidad guardarDocumentoInformeAlfresco() throws Exception {
		DocumentoViabilidad documentoInformeRevision = new DocumentoViabilidad();
		documentoInformeRevision.setNombre("Informe tecnico " + informeApoyo.getNumeroInforme() + ".pdf");
		documentoInformeRevision.setContenidoDocumento(informeApoyo.getArchivoInforme());
		documentoInformeRevision.setMime("application/pdf");
		documentoInformeRevision.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_APOYO.getIdTipoDocumento());
		documentoInformeRevision.setIdViabilidad(viabilidadProyecto.getId());

		documentoInformeRevision = documentosFacade.guardarDocumento(
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
				"VIABILIDAD_AMBIENTAL", documentoInformeRevision, 2);
		return documentoInformeRevision;
	}
	
	public DocumentoViabilidad guardarDocumentoOficioAlfresco() throws Exception {
		DocumentoViabilidad documentoOficioPronunciamiento = new DocumentoViabilidad();
		documentoOficioPronunciamiento.setNombre("Memorando " + oficioPronunciamiento.getNumeroOficio() + ".pdf");
		documentoOficioPronunciamiento.setContenidoDocumento(oficioPronunciamiento.getArchivoOficio());
		documentoOficioPronunciamiento.setMime("application/pdf");
		documentoOficioPronunciamiento.setIdTipoDocumento(tipoOficio.getIdTipoDocumento());
		documentoOficioPronunciamiento.setIdViabilidad(viabilidadProyecto.getId());

		documentoOficioPronunciamiento = documentosFacade.guardarDocumento(
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
				"VIABILIDAD_AMBIENTAL", documentoOficioPronunciamiento, 3);
		return documentoOficioPronunciamiento;
	}
	
	public DocumentoViabilidad guardarDocumentoOficioManualAlfresco(FileUploadEvent event) throws Exception {
		DocumentoViabilidad documentoOficioPronunciamiento = new DocumentoViabilidad();
		documentoOficioPronunciamiento.setNombre(event.getFile().getFileName());
		documentoOficioPronunciamiento.setContenidoDocumento(event.getFile().getContents());
		documentoOficioPronunciamiento.setMime("application/pdf");
		documentoOficioPronunciamiento.setIdTipoDocumento(tipoOficio.getIdTipoDocumento());
		documentoOficioPronunciamiento.setIdViabilidad(viabilidadProyecto.getId());

		documentoOficioPronunciamiento = documentosFacade.guardarDocumentoProceso(
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
				"VIABILIDAD_AMBIENTAL", documentoOficioPronunciamiento, 2, JsfUtil.getCurrentProcessInstanceId());
		return documentoOficioPronunciamiento;
	}
}
