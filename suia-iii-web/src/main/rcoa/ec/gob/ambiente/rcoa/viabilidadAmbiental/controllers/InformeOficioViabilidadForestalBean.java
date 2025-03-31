package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

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

import com.itextpdf.text.BaseColor;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
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
public class InformeOficioViabilidadForestalBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(InformeOficioViabilidadForestalBean.class);
	
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
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private InformeTecnicoForestal informeRevision;
	
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
	private Integer idProyecto, idViabilidad, numeroObservaciones;
	
	@Getter
	@Setter
	private String razonSocial;
	
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

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);
			idViabilidad = viabilidadProyecto.getId(); 
			
			areaTramite = viabilidadProyecto.getAreaResponsable();
			
			informeRevision = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_REVISION);
			
			String roldirector = "";
			if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				roldirector = Constantes.getRoleAreaName("role.va.pc.director.forestal");
			} else {
				roldirector = Constantes.getRoleAreaName("role.va.cz.director.forestal");
				areaTramite = areaTramite.getArea();
			}
		
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
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos Informe Revision Forestal.");
		}
	}
	
	public void generarInforme(Boolean marcaAgua) {
		try {

			informeRevision = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			if(informeRevision == null) {
				informeRevision = new InformeTecnicoForestal();
				informeRevision.setNombreFichero("InformeRevision.pdf");
			} else {
				if(informeRevision.getNumeroInforme() == null
						|| !informeRevision.getNumeroInforme().contains(viabilidadProyecto.getAreaResponsable().getAreaAbbreviation())) { //para actualizar el código del informe cuando hay cambio de área
					informeRevision.setNumeroInforme(null);
					informeInspeccionFacade.guardar(informeRevision, viabilidadProyecto.getAreaResponsable());
					informeRevision = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
				}
				
				informeRevision.setNombreFichero("informeRevision_" + UtilViabilidad.getFileNameEscaped(informeRevision.getNumeroInforme().replace("/", "-")) + ".pdf");
			}
			informeRevision.setNombreReporte("informeRevision.pdf");
			
			InformeRevisionForestalEntity informeTecnicoGenerador = cargarDatosInforme();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					informeRevision.getNombreReporte(), true, informeTecnicoGenerador);
			
			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(informePdf.getAbsolutePath());
			informeRevision.setArchivoInforme(Files.readAllBytes(path));
			String reporteHtmlfinal = informeRevision.getNombreFichero().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(informeRevision.getArchivoInforme());
			file.close();
			informeRevision.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informeRevision.getNombreFichero()));

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(
					"Error al cargar el informe tecnico de revision forestal",
					e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public InformeRevisionForestalEntity cargarDatosInforme() {
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		String usuarioTecnico = (String) variables.get("tecnicoForestal");
		Date fechaInforme = new Date();
		if(!JsfUtil.getLoggedUser().getNombre().equals(usuarioTecnico)) {
			fechaInforme = informeRevision.getFechaElaboracion();
		} else
			informeRevision.setFechaElaboracion(new Date());
		
		razonSocial = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
		
		String ubicacion = ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + " - "
				+ ubicacionPrincipal.getUbicacionesGeografica().getNombre() + " - " 
				+ ubicacionPrincipal.getNombre();
		
		InformeRevisionForestalEntity informeEntity = new InformeRevisionForestalEntity();
		
		informeEntity.setUnidadResponsable(viabilidadProyecto.getAreaResponsable().getAreaAbbreviation());
		informeEntity.setNombreUnidadResponsable(viabilidadProyecto.getAreaResponsable().getAreaName());
		informeEntity.setNroInforme(informeRevision.getNumeroInforme());
		informeEntity.setUbicacion(ubicacion);
		informeEntity.setCiudad(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		informeEntity.setFecha(formatoFecha.format(fechaInforme));
		informeEntity.setNroTramite(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		informeEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
		informeEntity.setRazonSocial(razonSocial);
		informeEntity.setSuperficieProyecto(proyectoLicenciaCoa.getSuperficie().toString());
		informeEntity.setNombreTecnico(informeRevision.getNombreTecnico());
		informeEntity.setCargoTecnico(informeRevision.getCargoTecnico());
		informeEntity.setAreaTecnico(informeRevision.getAreaTecnico());
		informeEntity.setAntecedentes(informeRevision.getAntecedentes());
		informeEntity.setObjetivo(informeRevision.getObjetivo());
		informeEntity.setMarcoLegal(informeRevision.getMarcoLegal());
		informeEntity.setResultadosRevision(informeRevision.getResultadosRevision());
		informeEntity.setConclusiones(informeRevision.getConclusiones());
		informeEntity.setRecomendaciones(informeRevision.getRecomendaciones());
		informeEntity.setElaboradoPor("");
		
		//interseca
		String interseccionesProyecto = interseccionViabilidadCoaFacade.getInterseccionesForestal(idProyecto, 2);
		informeEntity.setIntersecaProyecto(interseccionesProyecto);
		
		
		return informeEntity;
	}
	
	public void guardarInforme() {
		informeRevision.setIdViabilidad(viabilidadProyecto.getId());
		informeInspeccionFacade.guardar(informeRevision, viabilidadProyecto.getAreaResponsable());
	}
	
	public void getOficio() {
		if(informeRevision != null && informeRevision.getEsPronunciamientoFavorable())
			 tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_FAVORABLE;
		else 
			tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_NO_FAVORABLE;
		
		oficioPronunciamiento = pronunciamientoFacade.getInformePorViabilidad(viabilidadProyecto.getId());
	}

	public void generarOficio(Boolean marcaAgua) {
		try {
			
			if(informeRevision != null && informeRevision.getEsPronunciamientoFavorable())
				 tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_FAVORABLE;
			else 
				tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_NO_FAVORABLE;
			
			plantillaReporteOficio = plantillaReporteFacade.getPlantillaReporte(tipoOficio);
			
			oficioPronunciamiento = pronunciamientoFacade.getInformePorViabilidad(viabilidadProyecto.getId());			
			
			if(oficioPronunciamiento == null) {
				oficioPronunciamiento = new PronunciamientoForestal();
				oficioPronunciamiento.setConclusiones(informeRevision.getConclusiones());
				oficioPronunciamiento.setRecomendaciones(informeRevision.getRecomendaciones());
				oficioPronunciamiento.setNombreFichero("OficioPronunciamientoForestal.pdf");
			} else {
				if(oficioPronunciamiento.getNumeroOficio() == null
						|| !oficioPronunciamiento.getNumeroOficio().contains(areaAutoridad.getAreaAbbreviation())) { //para actualizar el código del oficio cuando hay cambio de área
					oficioPronunciamiento.setNumeroOficio(null);
					pronunciamientoFacade.guardar(oficioPronunciamiento, areaAutoridad, informeRevision.getEsPronunciamientoFavorable());
					
					oficioPronunciamiento = pronunciamientoFacade.getInformePorViabilidad(viabilidadProyecto.getId());
				}
				
				oficioPronunciamiento.setNombreFichero("OficioPronunciamientoForestal" + UtilViabilidad.getFileNameEscaped(oficioPronunciamiento.getNumeroOficio().replace("/", "-")) + ".pdf");
			}
			
			oficioPronunciamiento.setNombreOficio("OficioPronunciamientoForestal.pdf");
			
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
		areaTramite = viabilidadProyecto.getAreaResponsable();
		if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
			oficioEntity.setCiudad(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		} else {
	        // incluir informacion de la sede de la zonal en el documento
			ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
			String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyectoLicenciaCoa, null, null);
			oficioEntity.setCiudad(sedeZonal);
		}
		oficioEntity.setFechaOficio(formatoFecha.format(oficioPronunciamiento.getFechaOficio()));
		oficioEntity.setNroOficio(oficioPronunciamiento.getNumeroOficio());
		oficioEntity.setProponente(razonSocial);
		oficioEntity.setAsunto(oficioPronunciamiento.getAsunto());
		oficioEntity.setAntecedente(oficioPronunciamiento.getAntecedentes());
		oficioEntity.setMarcoLegal(oficioPronunciamiento.getMarcoLegal());
		oficioEntity.setPronunciamiento(oficioPronunciamiento.getPronunciamiento());
		oficioEntity.setConclusiones(oficioPronunciamiento.getConclusiones());
		oficioEntity.setRecomendaciones(oficioPronunciamiento.getRecomendaciones());
		oficioEntity.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());				
		
//		oficioEntity.setCargoAutoridad(usuarioAutoridad.getArea().getAreaName());
		oficioEntity.setCargoAutoridad(areaAutoridad.getAreaName());
		
		if(areaAutoridad.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES))
			oficioEntity.setCargoAutoridad(Constantes.CARGO_AUTORIDAD_ZONAL);
		
		return oficioEntity;
	}
	
	public void guardarOficio() {
		oficioPronunciamiento.setIdViabilidad(viabilidadProyecto.getId());
//		pronunciamientoFacade.guardar(oficioPronunciamiento, usuarioAutoridad.getArea(), informeRevision.getEsPronunciamientoFavorable());
		pronunciamientoFacade.guardar(oficioPronunciamiento, areaAutoridad, informeRevision.getEsPronunciamientoFavorable());
	}
	
	public void guardarDocumentosAlfresco() {
		try {
			String tipo = informeRevision.getEsPronunciamientoFavorable() ? "Favorable" : "No Favorable";
			DocumentoViabilidad documentoInformeRevision = new DocumentoViabilidad();
			documentoInformeRevision.setNombre("InformeRevisionForestal_" + informeRevision.getNumeroInforme() + ".pdf");
			documentoInformeRevision.setContenidoDocumento(informeRevision.getArchivoInforme());
			documentoInformeRevision.setMime("application/pdf");
			documentoInformeRevision.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_REVISION.getIdTipoDocumento());
			documentoInformeRevision.setIdViabilidad(viabilidadProyecto.getId());

			documentoInformeRevision = documentosFacade.guardarDocumentoProceso(
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					"VIABILIDAD_AMBIENTAL", documentoInformeRevision, 2, JsfUtil.getCurrentProcessInstanceId());

			DocumentoViabilidad documentoOficioPronunciamiento = new DocumentoViabilidad();
			documentoOficioPronunciamiento = new DocumentoViabilidad();
			documentoOficioPronunciamiento = new DocumentoViabilidad();
			documentoOficioPronunciamiento.setNombre("Viabilidad_" + tipo + "_" + oficioPronunciamiento.getNumeroOficio() + ".pdf");
			documentoOficioPronunciamiento.setContenidoDocumento(oficioPronunciamiento.getArchivoOficio());
			documentoOficioPronunciamiento.setMime("application/pdf");
			documentoOficioPronunciamiento.setIdTipoDocumento(tipoOficio.getIdTipoDocumento());
			documentoOficioPronunciamiento.setIdViabilidad(viabilidadProyecto.getId());

			documentoOficioPronunciamiento = documentosFacade.guardarDocumentoProceso(
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					"VIABILIDAD_AMBIENTAL", documentoOficioPronunciamiento, 2, JsfUtil.getCurrentProcessInstanceId());
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public DocumentoViabilidad guardarDocumentoOficioAlfresco() throws Exception {
		pronunciamientoFacade.guardar(oficioPronunciamiento);
				
		String tipo = informeRevision.getEsPronunciamientoFavorable() ? "Favorable" : "No Favorable";
		
		DocumentoViabilidad documentoOficioPronunciamiento = new DocumentoViabilidad();
		documentoOficioPronunciamiento = new DocumentoViabilidad();
		documentoOficioPronunciamiento = new DocumentoViabilidad();
		documentoOficioPronunciamiento.setNombre("Viabilidad_" + tipo + "_" + oficioPronunciamiento.getNumeroOficio() + ".pdf");
		documentoOficioPronunciamiento.setContenidoDocumento(oficioPronunciamiento.getArchivoOficio());
		documentoOficioPronunciamiento.setMime("application/pdf");
		documentoOficioPronunciamiento.setIdTipoDocumento(tipoOficio.getIdTipoDocumento());
		documentoOficioPronunciamiento.setIdViabilidad(viabilidadProyecto.getId());

		documentoOficioPronunciamiento = documentosFacade.guardarDocumentoProceso(
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
				"VIABILIDAD_AMBIENTAL", documentoOficioPronunciamiento, 2, JsfUtil.getCurrentProcessInstanceId());
		return documentoOficioPronunciamiento;
	}
}
