package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.dto.EntityInformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.EquipoApoyoProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ObservacionesEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.OficioPronunciamientoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.EquipoApoyoProyecto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoActualizacionCertificadoInterseccionFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarPdf;

@ManagedBean
@ViewScoped
public class InformeTecnicoEsIABean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(InformeTecnicoEsIABean.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private  UsuarioFacade usuarioFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	
	@EJB
	private InformeTecnicoEsIAFacade informeTecnicoEsIAFacade;
	
	@EJB
	private DocumentosImpactoEstudioFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@EJB
    private ObservacionesEsIAFacade observacionesEsIAFacade;
	
	@EJB
	private OficioPronunciamientoEsIAFacade oficioPronunciamientoEsIAFacade;
	
	@EJB
	private ProyectoActualizacionCertificadoInterseccionFacade proyectoActualizacionCIFacade;
	
	@EJB
	private EquipoApoyoProyectoEIACoaFacade equipoApoyoProyectoEIACoaFacade;
	
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
	private InformeTecnicoEsIA informeTecnico;
	
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
	private DocumentoEstudioImpacto documentoInforme;
	
	@Getter
	@Setter
	private Integer idProyecto, numeroObservaciones, idTarea, tipoInforme, numeroRevision;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte, resumenObservaciones, nombreClaseObservacionesPma;
	
	@Getter
	@Setter
	private Boolean existeObservacionesEstudio, esTecnico, requiereCorrecciones, esRequerido, cargaCompleta, requiereIngresoPlan;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	private String nombreTarea;
	
	@PostConstruct
	public void init() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			cargaCompleta = false;
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			String idProyectoString = (String) variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);
						
			numeroRevision = 1;
			String revision = (String) variables.get("numeroRevision");
			if(revision != null) 
				numeroRevision = Integer.parseInt(revision);
			
			requiereIngresoPlan = false;
			if(variables.get("requiereIngresoPlan") != null){
				requiereIngresoPlan=Boolean.parseBoolean(variables.get("requiereIngresoPlan").toString());	
			}

			proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
			
			areaTramite = proyecto.getAreaResponsable();
			
			ubicacionPrincipal=proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto).getUbicacionesGeografica();
			
			idTarea = (int)bandejaTareasBean.getTarea().getTaskId();
			
			esRequerido = false;
			existeObservacionesEstudio = false;
			
			cargarDatos();
			
			if(tipoInforme == null) {
				LOG.error("Error al definir el tipo de informe del EsIA");
				return;
			}
			
			if(esTecnico) {
				informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInformeNroRevision(esiaProyecto, tipoInforme, numeroRevision);
				
				if(informeTecnico == null) {
					informeTecnico = new InformeTecnicoEsIA();
					informeTecnico.setNombreReporte("informeTecnico.pdf");
					informeTecnico.setIdEstudio(esiaProyecto.getId());
					informeTecnico.setIdTarea(idTarea);
					informeTecnico.setTipoInforme(tipoInforme);
					informeTecnico.setFechaInforme(new Date());
					informeTecnico.setNumeroRevision(numeroRevision);
					
					/*Cambio para técnico con varias áreas*/
					Area areaInforme = new Area();
					if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 1){
						areaInforme = JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea();
					}else if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && !JsfUtil.getLoggedUser().getListaAreaUsuario().isEmpty()){
						areaInforme = proyecto.getAreaInventarioForestal(); //no área trámite inventario lo gestiona MAAE
					}
						
					informeTecnicoEsIAFacade.guardar(informeTecnico, areaInforme);
					
//					informeTecnicoEsIAFacade.guardar(informeTecnico, JsfUtil.getLoggedUser().getArea());
				} else {
					if(!informeTecnico.getIdTarea().equals(idTarea)) {
						if(informeTecnico.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre())) {
							Integer idInforme = informeTecnico.getId(); 
							InformeTecnicoEsIA informeTecnicoOriginal = (InformeTecnicoEsIA) SerializationUtils.clone(informeTecnico);
							informeTecnicoOriginal.setId(null);
							informeTecnicoOriginal.setCodigoInforme(null);
							informeTecnicoOriginal.setIdTarea(idTarea);
							informeTecnicoOriginal.setFechaInforme(new Date());
							informeTecnicoOriginal.setIdInformePrincipal(informeTecnico.getIdInformePrincipal() != null ? informeTecnico.getIdInformePrincipal() : informeTecnico.getId());
							
							/*Cambio de codigo para técnicos con varias oficinas técnicas*/
							Area areaInforme = new Area();
							if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 1){
								areaInforme = JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea();
								
							}else if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && !JsfUtil.getLoggedUser().getListaAreaUsuario().isEmpty()){
								areaInforme = proyecto.getAreaInventarioForestal();							
							}
							
							informeTecnico = informeTecnicoEsIAFacade.guardar(informeTecnicoOriginal, areaInforme);
							
//							informeTecnico = informeTecnicoEsIAFacade.guardar(informeTecnicoOriginal, JsfUtil.getLoggedUser().getArea());
							
							OficioPronunciamientoEsIA oficioPronunciamiento = oficioPronunciamientoEsIAFacade.getPorEstudioInforme(esiaProyecto.getId(), idInforme);
							if(oficioPronunciamiento != null){
								OficioPronunciamientoEsIA oficioOriginal = (OficioPronunciamientoEsIA) SerializationUtils.clone(oficioPronunciamiento);
								oficioOriginal.setId(null);;
								oficioOriginal.setCodigoOficio(null);
								oficioOriginal.setFechaOficio(new Date());
								oficioOriginal.setIdInforme(informeTecnico.getId());
								oficioPronunciamientoEsIAFacade.guardar(oficioOriginal, oficioPronunciamiento.getAreaResponsable(), oficioPronunciamiento.getTipoOficio());	
							}
						} else {
							informeTecnico = new InformeTecnicoEsIA();
							informeTecnico.setNombreReporte("informeTecnico.pdf");
							informeTecnico.setIdEstudio(esiaProyecto.getId());
							informeTecnico.setIdTarea(idTarea);
							informeTecnico.setTipoInforme(tipoInforme);
							informeTecnico.setFechaInforme(new Date());
							informeTecnico.setNumeroRevision(numeroRevision);
							
							/*Cambio para técnico con varias áreas*/
							
							Area areaInforme = new Area();
							if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 1){
								areaInforme = JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea();
							}else if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && !JsfUtil.getLoggedUser().getListaAreaUsuario().isEmpty()){
								areaInforme = proyecto.getAreaInventarioForestal();								
							}
							
							informeTecnicoEsIAFacade.guardar(informeTecnico, areaInforme);
//							informeTecnicoEsIAFacade.guardar(informeTecnico, JsfUtil.getLoggedUser().getArea());
						}
					}
				}
				
				validarObservaciones();
			} 
			cargaCompleta = true;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos Informe.");
		}
	}
	
	public void cargarDatos() throws Exception {
		EquipoApoyoProyecto equipoApoyoProyecto = equipoApoyoProyectoEIACoaFacade.obtenerEquipoApoyoProyecto(esiaProyecto);
		
		
		String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
		nombreTarea = tarea;
		requiereCorrecciones = false;
		if(tarea.contains("Conservacion")) {
			tipoInforme = InformeTecnicoEsIA.snap;
		} else if(tarea.contains("Interseccion")) {
			tipoInforme = InformeTecnicoEsIA.forestal;
		} else if(tarea.contains("Inventario")) {
			tipoInforme = InformeTecnicoEsIA.forestalInventario;
		} else {
			if(areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
				CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
				
				List<RolUsuario> listaRolUsuario = usuarioFacade.listarPorIdUsuario(JsfUtil.getLoggedUser().getId());
				Integer idSector = actividadPrincipal.getTipoSector().getId();
				
				String rolTecnicoSocial = Constantes.getRoleAreaName("role.esia.pc.tecnico.social.tipoSector." + idSector);
				String rolTecnicoBiotico = Constantes.getRoleAreaName("role.esia.pc.tecnico.biotico.tipoSector." + idSector);
				String rolTecnicoCartografo = Constantes.getRoleAreaName("role.esia.pc.tecnico.cartografo.tipoSector." + idSector);
				
				for(RolUsuario rol: listaRolUsuario){
					if(rol.getRol().getNombre().equals(rolTecnicoSocial)){
						tipoInforme = InformeTecnicoEsIA.social;
						break;
					} else if(rol.getRol().getNombre().equals(rolTecnicoBiotico)){
						tipoInforme = InformeTecnicoEsIA.biotico;
						break;
					} else if(rol.getRol().getNombre().equals(rolTecnicoCartografo)){
						tipoInforme = InformeTecnicoEsIA.cartografo;
						break;
					}
				}
			} else if (!areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)
					&& !areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)
					&& equipoApoyoProyecto.getNumeroTecnicosApoyo() == null) { 
				//si numeroTecnicosApoyo null son proyectos GADs nuevos que definen apoyo por sectores
				//si numeroTecnicosApoyo > 0 son proyectos GADs anteriores que no definian apoyo por sectores
				Area areaT = areaTramite;
				if (areaT.getTipoEnteAcreditado().equals("GOBIERNO")
						|| (areaT.getTipoEnteAcreditado().equals("MUNICIPIO")
							&& (areaT.getAreaName().contains("QUITO")
							|| areaT.getAreaName().contains("GUAYAQUIL")
							|| areaT.getAreaName().contains("CUENCA")))) {
					
					String rolTecnicoSocial = Constantes.getRoleAreaName("role.esia.gad.tecnico.social");
					String rolTecnicoBiotico = Constantes.getRoleAreaName("role.esia.gad.tecnico.biotico");
					String rolTecnicoCartografo = Constantes.getRoleAreaName("role.esia.gad.tecnico.cartografo");
					
					List<RolUsuario> listaRolUsuario = usuarioFacade.listarPorIdUsuario(JsfUtil.getLoggedUser().getId());
					
					for(RolUsuario rol: listaRolUsuario){
						if(rol.getRol().getNombre().equals(rolTecnicoSocial)){
							tipoInforme = InformeTecnicoEsIA.social;
							break;
						} else if(rol.getRol().getNombre().equals(rolTecnicoBiotico)){
							tipoInforme = InformeTecnicoEsIA.biotico;
							break;
						} else if(rol.getRol().getNombre().equals(rolTecnicoCartografo)){
							tipoInforme = InformeTecnicoEsIA.cartografo;
							break;
						}
					}
					
				} else
					tipoInforme = InformeTecnicoEsIA.apoyo;
			} else {
				tipoInforme = InformeTecnicoEsIA.apoyo;
			}
		}
		
		esTecnico = false;
		if(tarea.contains("Elaborar"))
			esTecnico = true;
			
	}
	
	public void validarObservaciones() throws ServiceException {
		existeObservacionesEstudio = false;
		String nombreClaseObservaciones1 = InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnico.getUsuarioCreacion() + "_" + informeTecnico.getTipoInforme();
		String nombreClaseObservaciones2 = InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnico.getTipoInforme();
		List<String> nombreClaseObservaciones = new ArrayList<>();
		nombreClaseObservaciones.add(nombreClaseObservaciones1);
		nombreClaseObservaciones.add(nombreClaseObservaciones2);
		
		nombreClaseObservacionesPma = nombreClaseObservaciones2;
		
		Integer idClaseObservaciones = esiaProyecto.getId();
		List<ObservacionesEsIA> observacionesPendientes = observacionesEsIAFacade.listarPorIdClaseNombreClaseNoCorregidas(
				idClaseObservaciones, nombreClaseObservaciones);
		
		if(observacionesPendientes.size() > 0) {
			existeObservacionesEstudio = true;
		}
		
//		List<ObservacionesEsIA> observaciones = observacionesEsIAFacade.listarPorIdClaseNombreClase(
//				idClaseObservaciones, nombreClaseObservaciones);
		if(observacionesPendientes.size() > 0) {
			resumenObservaciones = "<ul>";
			for (ObservacionesEsIA observacionesEsIA : observacionesPendientes) {
				resumenObservaciones += "<li>";
				resumenObservaciones += observacionesEsIA.getCampo() + ": " + observacionesEsIA.getDescripcion();
				resumenObservaciones += "</li>";
			}
			resumenObservaciones += "</ul>";
			
		} 
		else{
//			resumenObservaciones = (existeObservacionesEstudio) ? "" : (informeTecnico.getObservaciones() == null) ? "" : informeTecnico.getObservaciones();
			resumenObservaciones = "";
		}
		informeTecnico.setObservaciones(resumenObservaciones);
	}
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
	
	public void generarInforme(Boolean marcaAgua) {
		try {

			plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);
			
			if(informeTecnico == null)
				informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTarea(esiaProyecto, idTarea);
			
			if(informeTecnico == null) {
				informeTecnico = new InformeTecnicoEsIA();
				informeTecnico.setNombreReporte("InformeTecnico.pdf");
				informeTecnico.setIdEstudio(esiaProyecto.getId());
				informeTecnico.setIdTarea(idTarea);
				informeTecnico.setTipoInforme(tipoInforme);
			} else {
				informeTecnico.setNombreReporte("InformeTecnico_" + UtilViabilidad.getFileNameEscaped(informeTecnico.getCodigoInforme().replace("/", "-")) + ".pdf");
			}

			if(existeObservacionesEstudio)
				informeTecnico.setTipoPronunciamiento(2);
			else
				informeTecnico.setTipoPronunciamiento(1);
			
			EntityInformeTecnicoEsIA entity = cargarDatosInforme();

			File informePdfAux = UtilGenerarPdf.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					informeTecnico.getNombreReporte(), true, entity);
			
			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(informePdf.getAbsolutePath());
			informeTecnico.setArchivo(Files.readAllBytes(path));
			String reporteHtmlfinal = informeTecnico.getNombreReporte().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(informeTecnico.getArchivo());
			file.close();
			informeTecnico.setPath(JsfUtil.devolverContexto("/reportesHtml/" + informeTecnico.getNombreReporte()));
			
			urlReporte = informeTecnico.getPath();
			nombreReporte = informeTecnico.getNombreReporte();
			archivoReporte = informeTecnico.getArchivo();

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(
					"Error al cargar el informe tecnico",
					e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public EntityInformeTecnicoEsIA cargarDatosInforme() {
		
		EntityInformeTecnicoEsIA entity = new EntityInformeTecnicoEsIA();
		
		String nombreOperador = usuarioFacade.recuperarNombreOperador(proyecto.getUsuario()).get(0);
		
		//generacion Ubicacion
		List<UbicacionesGeografica> ubicacionProyectoLista = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyecto);
		String strTableUbicacion = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody><tr BGCOLOR=\"#B2B2B2\">"			
				+ "<td><strong>Provincia</strong></td>"
				+ "<td><strong>Cantón</strong></td>"
				+ "<td><strong>Parroquia</strong></td>"
				+ "</tr>";
		
		for (UbicacionesGeografica ubicacion : ubicacionProyectoLista) {
			strTableUbicacion += "<tr>";
			strTableUbicacion += "<td>" + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td>";
			strTableUbicacion += "<td>" + ubicacion.getUbicacionesGeografica().getNombre() + "</td>";
			strTableUbicacion += "<td>" + ubicacion.getNombre() + "</td>";
			strTableUbicacion += "</tr>";
		}		
		strTableUbicacion += "</tbody></table></center>";
		
		ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
		CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
		
		entity.setNumeroInforme(informeTecnico.getCodigoInforme());		
		entity.setCiudad(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entity.setFecha(JsfUtil.getDateFormat(new Date()));
		entity.setNombreProyecto(proyecto.getNombreProyecto());
		entity.setCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
		entity.setNombreOperador(nombreOperador);
		entity.setNombreArea(areaTramite.getAreaName());
		entity.setUbicacion(strTableUbicacion);
		entity.setDireccionProyecto(proyecto.getDireccionProyecto());
		entity.setSector(actividadPrincipal.getTipoSector().getNombre());
		entity.setSuperficie(proyecto.getSuperficie() + " ha");
		entity.setAltitud(null);
		entity.setJustificacion("");
		entity.setAntecedentes(informeTecnico.getAntecedentes());
		entity.setObjetivos(informeTecnico.getObjetivos());
		entity.setCaracteristicas(informeTecnico.getCaracteristicas());
		entity.setEvaluacion(informeTecnico.getEvaluacionTecnica());
		entity.setObservaciones(informeTecnico.getObservaciones());
		entity.setConclusionesRecomendaciones(informeTecnico.getConclusionesRecomendaciones());
		
		String titulo = (JsfUtil.getLoggedUser().getPersona().getPosicion() == null ? "" : JsfUtil.getLoggedUser().getPersona().getPosicion()) + " " 
					+ (JsfUtil.getLoggedUser().getPersona().getTitulo() == null ? "" : JsfUtil.getLoggedUser().getPersona().getTitulo());
		
		entity.setNombreTecnico(titulo + "<br />"+ JsfUtil.getLoggedUser().getPersona().getNombre());
		
		/**
		 * Cambio para un técnico en varias oficinas técnicas.
		 */
		Area areaTecnico = new Area();
		if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 1){
			areaTecnico = JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea();
		}else if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && !JsfUtil.getLoggedUser().getListaAreaUsuario().isEmpty()){
			areaTecnico = proyecto.getAreaInventarioForestal();
		}	
		entity.setAreaTecnico(areaTecnico.getAreaName());
//		entity.setAreaTecnico(JsfUtil.getLoggedUser().getArea().getAreaName());
		
		String tipoRol = "";		
		
		if(nombreTarea.contains("Conservacion")) {
			if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				tipoRol = "role.esia.pc.coordinador.conservacion";
			}else if (!areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
				tipoRol = "role.esia.cz.coordinador";
			}				
		} else if(nombreTarea.contains("Interseccion")) {
			if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				tipoRol = "role.esia.pc.coordinador.bosques";
			}else if (!areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
				tipoRol = "role.esia.cz.coordinador";
			}
		} else if(nombreTarea.contains("Inventario")) {
			if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				tipoRol = "role.esia.pc.coordinador.bosques";
			}else if (!areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
				tipoRol = "role.pc.director.provincial";
			}			
		} else {
			entity.setNombreResponsable("");
			entity.setAreaResponsable(" ");
		}
		
		/*Cambio para el nuevo cambio de varias oficinas técnicas a un usuario*/
		Area areaCoordinador = new Area();	
		
		if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 1){
			areaCoordinador = JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea();
		}else if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() > 1){
			/*Mas de un registro de areaUsuario significa que es un oficina técnica*/
			areaCoordinador = proyecto.getAreaInventarioForestal();;
		}
		
		if(!tipoRol.equals("")){									
			
			Usuario usuarioResponsable=buscarUsuarioBean.buscarUsuario(tipoRol, areaCoordinador);
//			Usuario usuarioResponsable=buscarUsuarioBean.buscarUsuario(tipoRol, JsfUtil.getLoggedUser().getArea());
			
			String tituloRes = (usuarioResponsable.getPersona().getPosicion() == null ? "" : usuarioResponsable.getPersona().getPosicion())+ " " + 
								(usuarioResponsable.getPersona().getTitulo() == null ? "" : usuarioResponsable.getPersona().getTitulo());
			entity.setNombreResponsable(tituloRes + "<br />"+ (usuarioResponsable!=null?usuarioResponsable.getPersona().getNombre():null));	
//			entity.setAreaResponsable(JsfUtil.getLoggedUser().getArea().getAreaName());
			entity.setAreaResponsable(areaCoordinador.getAreaName());
		}else {
			entity.setNombreResponsable("");
			entity.setAreaResponsable(" ");
		}
				
		
		if(areaCoordinador.getTipoArea().getId()==3)
			entity.setSiglasEnte(areaCoordinador.getAreaAbbreviation());
		
		return entity;
	}
	
	public void guardarDatos() {
		
		informeTecnico.setFechaInforme(new Date());
		
		Area areaCoordinador = new Area();	
		
		if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 1){
			areaCoordinador = JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea();
		}else if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() > 1){
			/*Mas de un registro de areaUsuario significa que es un oficina técnica*/
			areaCoordinador = areaTramite;
		}
		
//		informeTecnicoEsIAFacade.guardar(informeTecnico, JsfUtil.getLoggedUser().getArea());
		informeTecnicoEsIAFacade.guardar(informeTecnico, areaCoordinador);
		
		if(informeTecnico.getTipoPronunciamiento().equals(2)) {
			//si es observado
			proyectoLicenciaCoaFacade.guardar(proyecto); //para guardar solicitud de actualizacion de CI
			proyectoActualizacionCIFacade.guardarHistorialActualizacionCertificado(proyecto.getCodigoUnicoAmbiental(), proyecto.getEstadoActualizacionCertInterseccion(), null);
		} else {
			proyecto.setEstadoActualizacionCertInterseccion(0);
			proyectoLicenciaCoaFacade.guardar(proyecto);
		}
			
		generarInforme(true);
		
	}
	
	public void guardarDocumentosAlfresco() {
		try {
			generarInforme(false);
			
			documentoInforme = new DocumentoEstudioImpacto();
			documentoInforme.setContenidoDocumento(informeTecnico.getArchivo());
			documentoInforme.setNombre(informeTecnico.getNombreReporte());
			documentoInforme.setExtesion(".pdf");		
			documentoInforme.setMime("application/pdf");
			documentoInforme.setNombreTabla(InformeTecnicoEsIA.class.getSimpleName());
			documentoInforme.setIdTable(informeTecnico.getId());
			
			documentoInforme = documentosFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(), "INFORME_TECNICO", documentoInforme, TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void guardarDocumentoFirmaManual(DocumentoEstudioImpacto documentoManual) {
		try {
			documentoInforme = null;
			
			documentoManual.setNombreTabla(InformeTecnicoEsIA.class.getSimpleName());
			documentoManual.setIdTable(informeTecnico.getId());
			
			documentoInforme = documentosFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(), 
					"INFORME_TECNICO", documentoManual, TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void recuperarInformeTecnico() {
		try{
			informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, tipoInforme);
			List<DocumentoEstudioImpacto> documentos = documentosFacade.documentoXTablaIdXIdDocLista(informeTecnico.getId(), InformeTecnicoEsIA.class.getSimpleName(), TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);
			if(documentos.size() > 0) {
				documentoInforme = documentos.get(0);
				
				File fileDoc = documentosFacade.descargarFile(documentoInforme);
				
				Path path = Paths.get(fileDoc.getAbsolutePath());
				byte[] contenido = Files.readAllBytes(path);
				String reporteHtmlfinal = documentoInforme.getNombre().replace("/", "-");
				File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
				FileOutputStream file = new FileOutputStream(archivoFinal);
				file.write(contenido);
				file.close();
				
				urlReporte = JsfUtil.devolverContexto("/reportesHtml/" + documentoInforme.getNombre());
				nombreReporte = documentoInforme.getNombre();
				archivoReporte = contenido;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
}
