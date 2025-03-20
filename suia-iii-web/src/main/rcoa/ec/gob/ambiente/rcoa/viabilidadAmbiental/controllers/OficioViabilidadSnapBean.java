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

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PronunciamientoBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.OficioPronunciamientoEntity;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class OficioViabilidadSnapBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(OficioViabilidadSnapBean.class);
	
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
	private InformeInspeccionBiodiversidadFacade informeInspeccionFacade;
	
	@EJB
	private PronunciamientoBiodiversidadFacade pronunciamientoBiodiversidadFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private InformeInspeccionBiodiversidad informeInspeccion;
	
	@Getter
	@Setter
	private PronunciamientoBiodiversidad oficioPronunciamiento;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporte;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoOficio;
	
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoOficioPronunciamiento, informeFirmaManual;
	
	@Getter
	@Setter
	private Area areaTramite;
	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionPrincipal;
	
	@Getter
	@Setter
	private Integer idProyecto, idViabilidad;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@PostConstruct
	public void init() {
		try {
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			
			String idProyectoString=(String)variables.get("idProyecto");
			
			idProyecto = Integer.valueOf(idProyectoString);
			
			String nombre = viabilidadCoaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			Boolean esSnapMae = false;
			if (nombre.contains("SnapMae")) {
				esSnapMae = true;
			}
			
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(idProyecto, esSnapMae);
			String roldirector = "";
			idViabilidad = viabilidadProyecto.getId();
			
			areaTramite = viabilidadProyecto.getAreaResponsable();

			if (viabilidadProyecto.getEsAdministracionMae()) {
				if(viabilidadProyecto.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) { //272 ID AREA DIRECCIÓN DEL PARQUE NACIONAL GALÁPAGOS
					roldirector = Constantes.getRoleAreaName("role.va.cz.director.snap.galapagos");
					areaTramite = viabilidadProyecto.getAreaResponsable();
				} else {
					roldirector = Constantes.getRoleAreaName("role.va.pc.director.dpa");
					areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS); //siempre firma la direccion areas protegidas
				}
			} else {
				//areas delegadas se envía a Planta central
				roldirector = Constantes.getRoleAreaName("role.va.pc.director.dpa");
				areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS);
			}
			
			List<Usuario> listaUsuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(roldirector, areaTramite);
			
			if(listaUsuarioAutoridad==null || listaUsuarioAutoridad.isEmpty()){
				LOG.error("No se encontro usuario " + roldirector + " en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			usuarioAutoridad = listaUsuarioAutoridad.get(0);
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			informeInspeccion = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			oficioPronunciamiento = pronunciamientoBiodiversidadFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			if(oficioPronunciamiento == null) {
				oficioPronunciamiento = new PronunciamientoBiodiversidad();
				oficioPronunciamiento.setIdViabilidad(viabilidadProyecto.getId());
				oficioPronunciamiento.setRecomendaciones(informeInspeccion.getRecomendaciones());
				pronunciamientoBiodiversidadFacade.guardar(oficioPronunciamiento, areaTramite, informeInspeccion.getEsPronunciamientoFavorable());
			} else {
				if(oficioPronunciamiento.getRecomendaciones() == null) {
					oficioPronunciamiento.setRecomendaciones(informeInspeccion.getRecomendaciones());
					pronunciamientoBiodiversidadFacade.guardar(oficioPronunciamiento);
				}
				
				if(oficioPronunciamiento.getNumeroOficio() == null) {
					pronunciamientoBiodiversidadFacade.guardar(oficioPronunciamiento, areaTramite, informeInspeccion.getEsPronunciamientoFavorable());
				}
			}
			
			tipoOficio = informeInspeccion.getEsPronunciamientoFavorable() ? TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_FAVORABLE : TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_NO_FAVORABLE;
			
			plantillaReporte = plantillaReporteFacade.getPlantillaReporte(tipoOficio);	
			
			ubicacionPrincipal=proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa).getUbicacionesGeografica();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}

	public void generarOficio(Boolean marcaAgua) {
		try {
			
			oficioPronunciamiento = pronunciamientoBiodiversidadFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			String nombre = (oficioPronunciamiento == null) ? "Pronunciamiento" : "Pronunciamiento_" + UtilViabilidad.getFileNameEscaped(oficioPronunciamiento.getNumeroOficio().replace("/", "-")); 
			oficioPronunciamiento.setNombreFichero(nombre + ".pdf");
			oficioPronunciamiento.setNombreOficio(nombre + ".pdf");
			
			OficioPronunciamientoEntity oficioEntity = cargarDatos();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
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
	
	public OficioPronunciamientoEntity cargarDatos() throws Exception {
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		oficioPronunciamiento.setFechaOficio(new Date());

		OficioPronunciamientoEntity oficioEntity = new OficioPronunciamientoEntity();
//		oficioEntity.setProvincia(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
//		oficioEntity.setCiudad(ubicacionPrincipal.getUbicacionesGeografica().getNombre());
		oficioEntity.setProvincia(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		oficioEntity.setCiudad(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		oficioEntity.setFechaOficio(formatoFecha.format(oficioPronunciamiento.getFechaOficio()));
		oficioEntity.setNroOficio(oficioPronunciamiento.getNumeroOficio());
		oficioEntity.setEnteResponsable(areaTramite.getAreaName());
		oficioEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
		oficioEntity.setNroInforme(informeInspeccion.getNumeroInforme());
		oficioEntity.setRecomendaciones(oficioPronunciamiento.getRecomendaciones());
		oficioEntity.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
		oficioEntity.setCargoAutoridad(JsfUtil.obtenerCargoUsuario(usuarioAutoridad));
		
		String razonSocial = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
		
		oficioEntity.setRazonSocial(razonSocial);
		
		List<UbicacionesGeografica> ubicacionesProyecto = proyectoLicenciaCoaUbicacionFacade.ubicacionesGeograficas(proyectoLicenciaCoa);
		
		StringBuilder stringBuilder = new StringBuilder();
		if(!ubicacionesProyecto.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Provincia</th><th>Cantón</th><th>Parroquia</th><th>Área protegida</th></tr>");
			for (UbicacionesGeografica ubicacion : ubicacionesProyecto) {
				
				Integer idProvincia = ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getId();
				String areasProtegidas = "";
				
				List<DetalleInterseccionProyectoAmbiental> interseccionesProyecto = interseccionViabilidadCoaFacade.getDetalleInterseccionSnapPorProyecto(idProyecto);
				if(interseccionesProyecto != null) {
					for (DetalleInterseccionProyectoAmbiental detalle : interseccionesProyecto) {
						if(detalle.getInterseccionProyectoLicenciaAmbiental().getCapa().getId().equals(Constantes.ID_CAPA_SNAP)){
							AreasSnapProvincia areaSnap = interseccionViabilidadCoaFacade.getSnapProvinciaPorProyecto(detalle.getCodigoUnicoCapa(), idProvincia);
							if(areaSnap != null) {
								areasProtegidas = detalle.getNombreGeometria();
								break;
							}
						}
					}
				}
				
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>");
				stringBuilder.append(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(ubicacion.getUbicacionesGeografica().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(ubicacion.getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(areasProtegidas);
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		oficioEntity.setUbicacionAreas(stringBuilder.toString());
		
		return oficioEntity;
	}
	
	public void guardarOficio() {
		pronunciamientoBiodiversidadFacade.guardar(oficioPronunciamiento);
	}
	
	public void subirOficio() {
		try {			
			guardarOficio();
			generarOficio(false);
			guardarDocumentoOficio();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al guardar los documentos de informe y oficio retce generador.");
		}
	}
	
	public void guardarDocumentoOficio() throws ServiceException, CmisAlfrescoException {
		String tipo = informeInspeccion.getEsPronunciamientoFavorable() ? "Favorable" : "NoFavorable";
		
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
	}
	
	public void crearDocumentoFirmaManual(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		
		informeFirmaManual = new DocumentoViabilidad();
        informeFirmaManual.setId(null);
        informeFirmaManual.setContenidoDocumento(contenidoDocumento);
        informeFirmaManual.setNombre(event.getFile().getFileName());
        informeFirmaManual.setMime("application/pdf");
        informeFirmaManual.setIdTipoDocumento(tipoOficio.getIdTipoDocumento());
        informeFirmaManual.setIdViabilidad(viabilidadProyecto.getId());
	}
	
	public void guardarDocumentoOficioFirmaManual() throws ServiceException, CmisAlfrescoException {
		documentoOficioPronunciamiento = documentosFacade.guardarDocumentoProceso(
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
				"VIABILIDAD_AMBIENTAL", informeFirmaManual, 2, JsfUtil.getCurrentProcessInstanceId());
	}
	
	public Usuario getResponsableAreasProtegidas () {
		Area areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS);
		String tipoRol = "role.va.pc.tecnico.snap";
		
		String rolTecnico = Constantes.getRoleAreaName(tipoRol);
		
		//buscar usuarios por rol y area
		List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaTramite);
		if (listaUsuario == null || listaUsuario.size() == 0)
			return null;
		
		Usuario tecnicoResponsable = null;
		String usrTecnico = (String) variables.get("responsableAreasPC");
		if(usrTecnico != null) {
			Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
			if (usuarioTecnico != null
					&& usuarioTecnico.getEstado().equals(true)) {
				if (listaUsuario != null && listaUsuario.size() >= 0
						&& listaUsuario.contains(usuarioTecnico)) {
					tecnicoResponsable = usuarioTecnico;
				}
			}
		}
		
		if (tecnicoResponsable == null) {
			String proceso = Constantes.RCOA_PROCESO_VIABILIDAD + "'' , ''" + Constantes.RCOA_PROCESO_VIABILIDAD_SNAP;
			List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosProceso(listaUsuario, proceso);
			tecnicoResponsable = listaTecnicosResponsables.get(0);
		}
		
		return tecnicoResponsable;
	}
}
