package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.InterseccionProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.DocumentoInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InformeOficioIFFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ObservacionesInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ReporteInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.TipoReportesInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InformeTecnicoEntity;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ObservacionesInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.OficioPronunciamientoEntity;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ReporteInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.TipoReporteInventarioForestal;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import lombok.Getter;
import lombok.Setter;

public class DocumentoReporteInformeTecnicoController implements Serializable {


	final Logger LOG = Logger.getLogger(DocumentoReporteInformeTecnicoController.class);

	@EJB
	public ProcesoFacade procesoFacade;
	public Map<String, Object> variables;
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	public BandejaTareasBean bandejaTareasBean;
	@Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

	final int INFORME_INSPECCION = 1;
	final int INFORME_TECNICO = 2;
	final int OFICIO_PRONUNIAMIENTO = 3;

	final String INFORME_TECNICO_DESCRIPCION = "Informe Tecnico";
	final String OFICIO_PRONUNCIAMIENTO_DESCRIPCION = "Oficio Pronunciamiento";

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	ReporteInventarioForestalFacade reporteInventarioForestalFacade;

	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;

	@EJB
	ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;

	@EJB
	InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;

	@EJB
	private ObservacionesInventarioForestalFacade observacionesInventarioForestalFacade;

	@EJB
	private InformeOficioIFFacade informeOficioIFFacade;

	@EJB
	DocumentoInventarioForestalFacade documentoInventarioForestalFacade;
	@EJB
	private TipoReportesInventarioForestalFacade tipoReportesInventarioForestalFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
    private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private CapasCoaFacade capasCoaFacade;
    @EJB
	private InterseccionProyectoLicenciaAmbientalFacade interseccionProyectoLicenciaAmbientalFacade;
    @EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
    @EJB
    private AsignarTareaFacade asignarTareaFacade;
    @EJB
	private UbicacionGeograficaFacade ubicacionfacade;
    
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporteOficio;

	@Getter
	@Setter
	public ProyectoLicenciaCoa proyectoLicenciaCoa;

	@Getter
	@Setter
	public ReporteInventarioForestal informeTecnico;

	@Getter
	@Setter
	private InventarioForestalAmbiental inventarioForestalAmbiental;

	@Getter
	@Setter
	public ReporteInventarioForestal oficioPronunciamiento;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	@Getter
	@Setter
	public String tramite, tecnicoForestal, coordinadorForestal, autoridadAmbiental;
	@Getter
	@Setter
	public Integer idProyecto, categoria, cantidadObservacionesNoCorregidas;
	@Getter
	@Setter
	public Integer cantidadNotificaciones;

	@Setter
	@Getter
	private boolean pronunciamientoParaArchivo, pronunciamientoFavorable;
	@Getter
	@Setter
	private InformeTecnicoEntity informeTecnicoEntity = new InformeTecnicoEntity();
	@Getter
	@Setter
	public OficioPronunciamientoEntity oficioPronunciamientoEntity = new OficioPronunciamientoEntity();
	@Getter
    @Setter  
    private Usuario usuarioTecnicoForestal = new Usuario();
	@Getter
    @Setter  
    private Usuario usuarioCoordinadorForestal = new Usuario();
	@Getter
    @Setter  
    private Usuario usuarioAutoridad = new Usuario();
	private UbicacionesGeografica ubicacionOficinaTecnica = new UbicacionesGeografica();

	List<DocumentoInventarioForestal> listaDocumentos, listaOficios;
	
	public static final Integer ID_LAYER_COBERTURA = 12;

	public void visualizarInforme(boolean setCurrentDate) throws Exception {
		if (plantillaReporteInforme == null) {
			plantillaReporteInforme = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_TECNICO);
		}
		if (informeTecnico == null) {
			consultarInformeTecnico();
		}
		
		File informePdfAux = UtilGenerarInforme.generarFichero(plantillaReporteInforme.getHtmlPlantilla(),informeTecnicoEntity.getNombreFichero(), true, informeTecnicoEntity);
		File informePdf = JsfUtil.fileMarcaAgua(informePdfAux,setCurrentDate ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

		Path path = Paths.get(informePdf.getAbsolutePath());
		informeTecnico.setArchivoInforme(Files.readAllBytes(path));
		
		String reporteHtmlfinal = informeTecnico.getNombreFichero().replace("/", "-");
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(informeTecnico.getArchivoInforme());
		file.close();
		informeTecnico.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informeTecnico.getNombreFichero()));
		Boolean realizarCorrecion = (informeTecnico.getRealizarCorrecion() == null) ? false : informeTecnico.getRealizarCorrecion();
		informeTecnico.setRealizarCorrecion(realizarCorrecion);
	}

	public void visualizarOficio(boolean setCurrentDate) throws Exception {
		if (plantillaReporteOficio == null) {
			plantillaReporteOficio = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.INVENTARIO_FORESTAL_OFICIO_PRONUNCIAMIENTO);
		}
		if (oficioPronunciamiento == null) {
			consultarOficioPronunciamiento();
		}	
		
		File informePdfAux = UtilGenerarInforme.generarFichero(plantillaReporteOficio.getHtmlPlantilla(), oficioPronunciamientoEntity.getNombreFichero(), true, oficioPronunciamientoEntity);

		File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, setCurrentDate ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

		Path path = Paths.get(informePdf.getAbsolutePath());
		oficioPronunciamiento.setArchivoInforme(Files.readAllBytes(path));
		String reporteHtmlfinal = oficioPronunciamiento.getNombreFichero().replace("/", "-");
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(oficioPronunciamiento.getArchivoInforme());
		file.close();
		oficioPronunciamiento.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + oficioPronunciamiento.getNombreFichero()));
	}

	public String elimuinarParrafoHtml(String texto) {
		if (texto == null) {
			return null;
		}
		texto = texto.replace("<p>\r\n", "");
		texto = texto.replace("</p>\r\n", "");
		return texto;
	}

	public StreamedContent getStream(String name, byte[] fileContent)
			throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}

	public void consultarInformeTecnico() {
		String mensaje = "Ocurrió un error al recuperar los datos del Inventario Forestal";
		try {
			if (variables == null) {
				variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			}
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			tecnicoForestal = (String) variables.get("tecnicoForestal");
			coordinadorForestal = (String) variables.get("coordinadorForestal");
			autoridadAmbiental = (String) variables.get("autoridadAmbiental");
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);			
			
			asignarCoordinadorForestal();
			
			if (proyectoLicenciaCoa.getId() != null) {
				inventarioForestalAmbiental = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(idProyecto);
				if (inventarioForestalAmbiental.getId() != null){
					// Informe tecnico
					informeTecnico = reporteInventarioForestalFacade.getByIdCertificadoByIdTipoDocumento(inventarioForestalAmbiental.getId(),INFORME_TECNICO);
					Boolean realizarCorrecion = (informeTecnico.getRealizarCorrecion() == null) ? false : informeTecnico.getRealizarCorrecion();
					informeTecnico.setRealizarCorrecion(realizarCorrecion);
					cantidadObservacionesNoCorregidas = numeroObservacionesNoCorregidas();
					
					if (cantidadNotificaciones >= 2) {
						pronunciamientoParaArchivo= (cantidadObservacionesNoCorregidas>0) ? true : false;
					} else {
						pronunciamientoFavorable=(cantidadObservacionesNoCorregidas>0) ? false : true;
						pronunciamientoParaArchivo=false;
						informeTecnico.setPronunciamientoFavorable(pronunciamientoParaArchivo);
					}
					informeTecnico.setPronunciamientoParaArchivo(pronunciamientoParaArchivo);
				}
			}
			if (informeTecnico.getId() != null) {
				listaDocumentos = documentoInventarioForestalFacade.getByInventarioForestalReporte(informeTecnico.getId());
				DocumentoInventarioForestal informe = new DocumentoInventarioForestal();
				for (DocumentoInventarioForestal item : listaDocumentos) {
					if (item.getDescripcionTabla().equals(INFORME_TECNICO_DESCRIPCION)) {
						informe = item;
						break;
					}
				}
				if (informe.getNombreDocumento() == null) {
					informe.setNombreDocumento("InformeTecnico.pdf");
				}
				informeTecnico.setNombreFichero(informe.getNombreDocumento());
				asignaInformeTecnico();
			} else {
				inicializarInformeTecnico();
			}			
		} catch (CmisAlfrescoException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(mensaje);
		} catch (JbpmException e) {
			LOG.error(e);
			JsfUtil.addMessageError(mensaje);
		}
	}
	
	public void consultarOficioPronunciamiento() {
		String mensaje = "Ocurrió un error al recuperar los datos del Inventario Forestal";
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			usuarioAutoridad = asignarAutoridadAmbiental();
			
			String nombreAutoridad = (usuarioAutoridad == null) ? "" : usuarioAutoridad.getPersona().getNombre();
			Area areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal().getArea();
			if (proyectoLicenciaCoa.getAreaInventarioForestal().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC) || proyectoLicenciaCoa.getAreaInventarioForestal().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS))
				areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal();
			String nombreArea = areaTramite.getAreaName();
			oficioPronunciamientoEntity.setNombreAutoridad(nombreAutoridad);
			oficioPronunciamientoEntity.setNombreArea(nombreArea);

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			if (proyectoLicenciaCoa.getId() != null) {
				// Oficio pronunciamiento
				oficioPronunciamiento = reporteInventarioForestalFacade.getByIdCertificadoByIdTipoDocumento(inventarioForestalAmbiental.getId(), OFICIO_PRONUNIAMIENTO);

				if (oficioPronunciamiento.getId() != null) {
					listaOficios = documentoInventarioForestalFacade.getByInventarioForestalReporte(oficioPronunciamiento.getId());
					DocumentoInventarioForestal oficio = new DocumentoInventarioForestal();
					for (DocumentoInventarioForestal item : listaOficios) {
						if (item.getDescripcionTabla().equals(OFICIO_PRONUNCIAMIENTO_DESCRIPCION)) {
							oficio = item;
							break;
						}
					}
					asignaOficioPronunciamiento();
					if (oficio.getNombreDocumento() == null) {
						oficio.setNombreDocumento("oficioPronunciamiento.pdf");
					}
					oficioPronunciamiento.setNombreFichero(oficio.getNombreDocumento());
					oficioPronunciamientoEntity.setNombreFichero(oficio.getNombreDocumento());
				} else {
					inicializarOficioPronunicamiennto();
				}
				// Validacion pronunciamiento aprobado / observado
				cantidadObservacionesNoCorregidas = numeroObservacionesNoCorregidas();
				
				if (cantidadNotificaciones >= 2) {
					pronunciamientoParaArchivo= (cantidadObservacionesNoCorregidas>0) ? true : false;
					if (pronunciamientoParaArchivo == false) {
						pronunciamientoFavorable=(cantidadObservacionesNoCorregidas>0) ? false : true;
					} else {
						pronunciamientoFavorable = false;
					}
				} else {
					pronunciamientoFavorable=(cantidadObservacionesNoCorregidas>0) ? false : true;
					pronunciamientoParaArchivo=false;
				}
				oficioPronunciamiento.setPronunciamientoFavorable(pronunciamientoFavorable);
				oficioPronunciamiento.setPronunciamientoParaArchivo(pronunciamientoParaArchivo);	
				oficioPronunciamiento.setInventarioForestalAmbiental(inventarioForestalAmbiental);
			}
		} catch (JbpmException e) {
			LOG.error(e);
			JsfUtil.addMessageError(mensaje);
		} catch (CmisAlfrescoException e) {
			LOG.error(e);
			JsfUtil.addMessageError(mensaje);
		}
	}
	
	private Integer numeroObservacionesNoCorregidas() {
		Integer result = 0;
		Integer idClase = 0;
		String nombreClase = "";
		try {
			InventarioForestalAmbiental inventarioForestalAmbiental = inventarioForestalAmbientalFacade
					.getByIdRegistroPreliminar(idProyecto);
			idClase = inventarioForestalAmbiental.getId();
			nombreClase = "InventarioForestalAmbiental";
			List<ObservacionesInventarioForestal> listObservacionesInventarioForestal = observacionesInventarioForestalFacade
					.listarPorIdClaseNombreClaseNoCorregidas(idClase,
							nombreClase);
			result = listObservacionesInventarioForestal.size();
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Error extraer las observaciones");
			e.printStackTrace();
			return 0;
		}
		return result;
	}

	public void inicializarInformeTecnico() {
		try {
			usuarioTecnicoForestal = usuarioFacade.buscarUsuario(tecnicoForestal);
			usuarioCoordinadorForestal = usuarioFacade.buscarUsuario(coordinadorForestal);
			String direccionForestal = proyectoLicenciaCoa.getAreaInventarioForestal().getAreaName();
			String direccionSiglas = proyectoLicenciaCoa.getAreaInventarioForestal().getAreaAbbreviation();
			String codigoReporte = informeOficioIFFacade.generarCodigoInformeTecnicoDescripcion(proyectoLicenciaCoa.getAreaInventarioForestal());
			String ubicacionProyecto = "";
			List<UbicacionesGeografica> proyectoUbicaciones = proyectoLicenciaCoaUbicacionFacade.ubicacionesGeograficas(proyectoLicenciaCoa);
			for (UbicacionesGeografica row : proyectoUbicaciones) {
				String parroquia = row.getNombre();
				String canton = row.getUbicacionesGeografica().getNombre();
				String provincia = row.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
				String ubicacion = provincia+"/"+canton+"/"+parroquia;
				ubicacionProyecto = (ubicacionProyecto == "") ? ubicacion : " ,"+ubicacion;
			}
			Date fechaActual = new Date();
			SimpleDateFormat fechaFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
			SimpleDateFormat fechaFormatSimple = new SimpleDateFormat("dd/MM/yyy");
			String elaboradoPor = (usuarioTecnicoForestal == null) ? "" : usuarioTecnicoForestal.getPersona().getNombre();
			String denominacionCargo;
			if (usuarioTecnicoForestal.getPersona().getPosicion() != null) {
				denominacionCargo = usuarioTecnicoForestal.getPersona().getPosicion();
			} else {
				denominacionCargo = usuarioTecnicoForestal.getPersona().getTitulo();
			}
			String revisadoPor = (usuarioCoordinadorForestal == null) ? "" : usuarioCoordinadorForestal.getPersona().getNombre();
			String revisadoCargo = (usuarioCoordinadorForestal == null) ? "" : usuarioCoordinadorForestal.getPersona().getTitulo();
			if (usuarioCoordinadorForestal.getPersona().getPosicion() != null) {
				revisadoCargo = usuarioCoordinadorForestal.getPersona().getPosicion();
			} else {
				revisadoCargo = usuarioCoordinadorForestal.getPersona().getTitulo();
			}
			String interseccionProyecto = getInterseccionCapa(ID_LAYER_COBERTURA);
			ReporteInventarioForestal informeInspeccion = reporteInventarioForestalFacade.getByIdCertificadoByIdTipoDocumento(inventarioForestalAmbiental.getId(), INFORME_INSPECCION);
			String fecha = (informeInspeccion.getId() != null) ? ((informeInspeccion.getFechaFin() != null) ? fechaFormatSimple.format(informeInspeccion.getFechaFin()) : "") : "No se realiza inspección";
			Boolean realizarCorrecion = (informeTecnico.getRealizarCorrecion() == null) ? false : informeTecnico.getRealizarCorrecion();
			
			informeTecnicoEntity.setDireccionForestal(direccionForestal);
			informeTecnicoEntity.setSiglas(direccionSiglas);
			informeTecnicoEntity.setCodigoReporte(codigoReporte);
			informeTecnicoEntity.setNombreFichero("InformeTecnico.pdf");
			informeTecnicoEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
			informeTecnicoEntity.setCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
			informeTecnicoEntity.setRazonSocialOperador(proyectosBean.getProponente());
			informeTecnicoEntity.setUbicacionProyecto(ubicacionProyecto);
			informeTecnicoEntity.setFechaFin(fecha);
			informeTecnicoEntity.setFecha(fechaFormat.format(fechaActual));
			informeTecnicoEntity.setSuperficieTotalProyecto(proyectoLicenciaCoa.getSuperficie().toString());
			informeTecnicoEntity.setInterseccionProyecto(interseccionProyecto);
			informeTecnicoEntity.setElaboradoPor(elaboradoPor);
			informeTecnicoEntity.setDenominacionCargo(denominacionCargo);
			informeTecnicoEntity.setRevisadoPor(revisadoPor);
			informeTecnicoEntity.setRevisadoCargo(revisadoCargo);
			
			informeTecnico = new ReporteInventarioForestal();
			TipoReporteInventarioForestal tipoReporte = tipoReportesInventarioForestalFacade.getByIdTipoReporteInventarioForestal(INFORME_TECNICO);
			// Informe tecnico
			informeTecnico.setTipoReporteInventarioForestal(tipoReporte);
			informeTecnico.setInventarioForestalAmbiental(inventarioForestalAmbiental);
			informeTecnico.setCodigoReporte(codigoReporte);
			informeTecnico.setNombreFichero(informeTecnicoEntity.getNombreFichero());
			informeTecnico.setFechaFin(informeInspeccion.getFechaFin());
			informeTecnico.setFecha(fechaActual);
			informeTecnico.setRealizarCorrecion(realizarCorrecion);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	public void inicializarOficioPronunicamiennto() {
		try {
			ProyectoLicenciaCoaUbicacion proyectoLicenciaCoaUbicacion = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa);
			String codigoReporte = informeOficioIFFacade.generarCodigoOficioPronunciamiento(proyectoLicenciaCoa.getAreaInventarioForestal());
			Date fechaActual = new Date();
			SimpleDateFormat fechaFormat = new SimpleDateFormat("dd MMMM yyy");
			//ubicacionOficinaTecnica=ubicacionfacade.buscarPorId(proyectoLicenciaCoa.getIdCantonOficina());
			String ciudad = "";//ubicacionOficinaTecnica.getNombre();

			// incluir informacion de la sede de la zonal en el documento
			ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
			ciudad = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyectoLicenciaCoa, null, null);
			oficioPronunciamientoEntity.setCiudad(ciudad);
			oficioPronunciamientoEntity.setCodigoReporte(codigoReporte);
			oficioPronunciamientoEntity.setFecha(fechaFormat.format(fechaActual));
			oficioPronunciamientoEntity.setRazonSocialOperador(proyectosBean.getProponente());
			//oficioPronunciamientoEntity.setCiudad(proyectoLicenciaCoaUbicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			oficioPronunciamientoEntity.setNombreFichero("oficioPronunciamiento.pdf");
			oficioPronunciamientoEntity.setSiglasCoordinador("");
			oficioPronunciamientoEntity.setSiglasTecnico("");
			
			// oficio pronunciamiento
			TipoReporteInventarioForestal tipoReporte = tipoReportesInventarioForestalFacade.getByIdTipoReporteInventarioForestal(OFICIO_PRONUNIAMIENTO);
			oficioPronunciamiento.setTipoReporteInventarioForestal(tipoReporte);
			oficioPronunciamiento.setInventarioForestalAmbiental(inventarioForestalAmbiental);
			oficioPronunciamiento.setCodigoReporte(codigoReporte);
			oficioPronunciamiento.setCiudad(proyectoLicenciaCoaUbicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			oficioPronunciamiento.setNombreFichero("oficioPronunciamiento.pdf");
		} catch (Exception e) {
			LOG.error(e);
		}
	}
	
	public void asignaInformeTecnico() {
		try {
			ReporteInventarioForestal informeTecnico = reporteInventarioForestalFacade.getByIdCertificadoByIdTipoDocumento(inventarioForestalAmbiental.getId(), INFORME_TECNICO);
			informeTecnico.setNombreFichero("InformeTecnico.pdf");
			Boolean realizarCorrecion = (informeTecnico.getRealizarCorrecion() == null) ? false : informeTecnico.getRealizarCorrecion();
			informeTecnico.setRealizarCorrecion(realizarCorrecion);
			usuarioTecnicoForestal = usuarioFacade.buscarUsuario(tecnicoForestal);
			usuarioCoordinadorForestal = usuarioFacade.buscarUsuario(coordinadorForestal);
			String direccionForestal = proyectoLicenciaCoa.getAreaInventarioForestal().getAreaName();
			String direccionSiglas = proyectoLicenciaCoa.getAreaInventarioForestal().getAreaAbbreviation();
			String codigoReporte = informeTecnico.getCodigoReporte();
			String ubicacionProyecto = "";
			List<UbicacionesGeografica> proyectoUbicaciones = proyectoLicenciaCoaUbicacionFacade.ubicacionesGeograficas(proyectoLicenciaCoa);
			for (UbicacionesGeografica row : proyectoUbicaciones) {
				String parroquia = row.getNombre();
				String canton = row.getUbicacionesGeografica().getNombre();
				String provincia = row.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
				String ubicacion = provincia+"/"+canton+"/"+parroquia;
				ubicacionProyecto = (ubicacionProyecto == "") ? ubicacion : " ,"+ubicacion;
			}
			Date fechaActual = new Date();
			SimpleDateFormat fechaFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
			SimpleDateFormat fechaFormatSimple = new SimpleDateFormat("dd/MM/yyy");
			String elaboradoPor = (usuarioTecnicoForestal == null) ? "" : usuarioTecnicoForestal.getPersona().getNombre();
			String denominacionCargo = (usuarioTecnicoForestal == null) ? "" : usuarioTecnicoForestal.getPersona().getTitulo();
			String revisadoPor = (usuarioCoordinadorForestal == null) ? "" : usuarioCoordinadorForestal.getPersona().getNombre();
			String revisadoCargo = (usuarioCoordinadorForestal == null) ? "" : usuarioCoordinadorForestal.getPersona().getTitulo();
			String fecha = (informeTecnico.getFechaFin() != null) ? fechaFormatSimple.format(informeTecnico.getFechaFin()) : "No se realiza inspección";
			String superficieCobertura = (informeTecnico.getSuperficieCoberturaVegetal() == null) ? "" : informeTecnico.getSuperficieCoberturaVegetal().toString();
			String interseccionProyecto = getInterseccionCapa(ID_LAYER_COBERTURA);
			String nombresDelegadoInspeccion = (informeTecnico.getNombresDelegadoInspeccion() == null) ? "" : informeTecnico.getNombresDelegadoInspeccion();
			String areaDelegado =  (informeTecnico.getAreaDelegado() == null) ? "" : informeTecnico.getAreaDelegado();
			String cargoDelegado =  (informeTecnico.getCargoDelegado() == null) ? "" : informeTecnico.getCargoDelegado();
			String antecedentes =  (informeTecnico.getAntecedentes() == null) ? "" : informeTecnico.getAntecedentes();
			String marcoLegal =  (informeTecnico.getMarcoLegal() == null) ? "" : informeTecnico.getMarcoLegal();
			String objetivo =  (informeTecnico.getObjetivo() == null) ? "" : informeTecnico.getObjetivo();
			String resultadoRevision =  (informeTecnico.getResultadoRevision() == null) ? "" : informeTecnico.getResultadoRevision();
			String conclusiones =  (informeTecnico.getConclusiones() == null) ? "" : informeTecnico.getConclusiones();
			String recomendaciones =  (informeTecnico.getRecomendaciones() == null) ? "" : informeTecnico.getRecomendaciones();
			

			informeTecnicoEntity.setDireccionForestal(direccionForestal);
			informeTecnicoEntity.setSiglas(direccionSiglas);
			informeTecnicoEntity.setCodigoReporte(codigoReporte);
			informeTecnicoEntity.setNombreFichero("InformeTecnico.pdf");
			informeTecnicoEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
			informeTecnicoEntity.setCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
			informeTecnicoEntity.setRazonSocialOperador(proyectosBean.getProponente());
			informeTecnicoEntity.setUbicacionProyecto(ubicacionProyecto);
			informeTecnicoEntity.setFechaFin(fecha);
			informeTecnicoEntity.setFecha(fechaFormat.format(fechaActual));
			informeTecnicoEntity.setSuperficieTotalProyecto(proyectoLicenciaCoa.getSuperficie().toString());
			informeTecnicoEntity.setElaboradoPor(elaboradoPor);
			informeTecnicoEntity.setDenominacionCargo(denominacionCargo);
			informeTecnicoEntity.setUbicacionProyecto(ubicacionProyecto);
			informeTecnicoEntity.setSuperficieCobertura(superficieCobertura);
			informeTecnicoEntity.setElaboradoPor(elaboradoPor);
			informeTecnicoEntity.setDenominacionCargo(denominacionCargo);
			informeTecnicoEntity.setRevisadoPor(revisadoPor);
			informeTecnicoEntity.setRevisadoCargo(revisadoCargo);
			informeTecnicoEntity.setInterseccionProyecto(interseccionProyecto);
			informeTecnicoEntity.setNombresDelegadoInspeccion(nombresDelegadoInspeccion);
			informeTecnicoEntity.setAreaDelegado(areaDelegado);
			informeTecnicoEntity.setCargoDelegado(cargoDelegado);
			informeTecnicoEntity.setAntecedentes(antecedentes);
			informeTecnicoEntity.setMarcoLegal(marcoLegal);
			informeTecnicoEntity.setObjetivo(objetivo);
			informeTecnicoEntity.setResultadoRevision(resultadoRevision);
			informeTecnicoEntity.setConclusiones(conclusiones);
			informeTecnicoEntity.setRecomendaciones(recomendaciones);
		} catch (Exception e) {
			LOG.error(e);
		}
	}
	
	private String getInterseccionCapa(Integer idCapa) {
    	String capaValue = "---";
    	InterseccionProyectoLicenciaAmbiental interseca = new InterseccionProyectoLicenciaAmbiental();
    	CapasCoa interseccionCapa = new CapasCoa();
    	interseccionCapa = capasCoaFacade.getCapasById(idCapa);
    	List<InterseccionProyectoLicenciaAmbiental> interseccionProyecto = new ArrayList<InterseccionProyectoLicenciaAmbiental>();
    	interseccionProyecto = interseccionProyectoLicenciaAmbientalFacade.getByIdProyectoCoa(inventarioForestalAmbiental.getId(), 0);
    	for (InterseccionProyectoLicenciaAmbiental rowInterseccion : interseccionProyecto) {
			if (rowInterseccion.getCapa().getId() == interseccionCapa.getId()) {
				interseca = rowInterseccion;
				break;
			}
		}
    	List<DetalleInterseccionProyectoAmbiental> listDetalleInterseccion = new ArrayList<DetalleInterseccionProyectoAmbiental>();
    	listDetalleInterseccion = detalleInterseccionProyectoAmbientalFacade.getByInterseccionProyecto(interseca.getId());
    	if (listDetalleInterseccion.size() > 0) {
			capaValue = listDetalleInterseccion.get(0).getNombreGeometria();
		}
    	return capaValue;
    }

	public void asignaOficioPronunciamiento() {
		try {
			ReporteInventarioForestal oficioPronunciamiento = reporteInventarioForestalFacade.getByIdCertificadoByIdTipoDocumento(inventarioForestalAmbiental.getId(), OFICIO_PRONUNIAMIENTO);
			oficioPronunciamiento.setNombreFichero("oficioPronunciamiento.pdf");
			String codigoReporte = oficioPronunciamiento.getCodigoReporte();
			Date fechaActual = new Date();
			SimpleDateFormat fechaFormat = new SimpleDateFormat("dd MMMM yyy");
			String asuntoOficio = oficioPronunciamiento.getAsuntoOficio();
			String antecedentes = oficioPronunciamiento.getAntecedentes();
			String marcoLegal = oficioPronunciamiento.getMarcoLegal();
			String pronunciamiento = oficioPronunciamiento.getPronunciamiento();
			ProyectoLicenciaCoaUbicacion proyectoLicenciaCoaUbicacion = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa);
			//ubicacionOficinaTecnica=ubicacionfacade.buscarPorId(proyectoLicenciaCoa.getIdCantonOficina());
			String ciudad = proyectoLicenciaCoaUbicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
			
			Area areaFirma;
			String nombreAreaFirma = "DIRECTOR ZONAL";
			if(proyectoLicenciaCoa.getAreaInventarioForestal().getTipoArea().getSiglas().equals("PC")){
				areaFirma = proyectoLicenciaCoa.getAreaInventarioForestal();
				ciudad = usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
				nombreAreaFirma = areaFirma.getAreaName();
			}else{
				areaFirma = proyectoLicenciaCoa.getAreaInventarioForestal().getArea();
				// incluir informacion de la sede de la zonal en el documento
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
				ciudad = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyectoLicenciaCoa, null, null);
			}
			oficioPronunciamientoEntity.setCiudad(ciudad);
			oficioPronunciamientoEntity.setCodigoReporte(codigoReporte);
			oficioPronunciamientoEntity.setFecha(fechaFormat.format(fechaActual));
			oficioPronunciamientoEntity.setRazonSocialOperador(proyectosBean.getProponente());
			oficioPronunciamientoEntity.setNombreFichero("oficioPronunciamiento.pdf");
			oficioPronunciamientoEntity.setSiglasCoordinador("");
			oficioPronunciamientoEntity.setSiglasTecnico("");
			oficioPronunciamientoEntity.setAsuntoOficio(asuntoOficio);
			oficioPronunciamientoEntity.setAntecedentes(antecedentes);
			oficioPronunciamientoEntity.setMarcoLegal(marcoLegal);
			oficioPronunciamientoEntity.setPronunciamiento(pronunciamiento);
			oficioPronunciamientoEntity.setNombreArea(nombreAreaFirma);
		} catch (Exception e) {
			LOG.error(e);
		}
	}
	
	private Usuario asignarAutoridadAmbiental() {
    	Area areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal().getArea();
    	String rolPrefijo;
		String rolDirector;
		Usuario autoridad = new Usuario();
    	if (proyectoLicenciaCoa.getAreaInventarioForestal().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
    		areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal();
    		rolPrefijo = "role.inventario.pc.autoridad";
		} else {
			if(proyectoLicenciaCoa.getAreaInventarioForestal().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS))
				areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal();
			rolPrefijo = "role.inventario.cz.autoridad";
		}
    	rolDirector = Constantes.getRoleAreaName(rolPrefijo);
    	List<Usuario> listaTecnicosResponsables = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolDirector, areaTramite.getAreaName());			

		if (listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
			LOG.error("No se encontro usuario " + rolDirector + " en " + areaTramite.getAreaName());
			JsfUtil.addMessageError("No se encontro usuario " + rolDirector + " en " + areaTramite.getAreaName());
			autoridad = null;
		} else {
			autoridad = listaTecnicosResponsables.get(0);
			return autoridad;
		}
		return autoridad;
    }

	private Usuario asignarCoordinadorForestal() throws JbpmException {
    	Area areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal();
    	String rolPrefijo;
		String rolCoordinador;
    	if (areaTramite.getAreaAbbreviation() == "DRA") {
    		rolPrefijo = "role.inventario.pc.coordinador";
		} else {
			rolPrefijo = "role.inventario.cz.coordinador";
		}
    	rolCoordinador = Constantes.getRoleAreaName(rolPrefijo);
    	List<Usuario> listaUsuario = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolCoordinador, areaTramite.getAreaName());			

		if (listaUsuario==null || listaUsuario.isEmpty()){
			LOG.error("No se encontro usuario " + rolCoordinador + " en " + areaTramite.getAreaName());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}
		Usuario tecnicoResponsable = listaUsuario.get(0);
		
		if(!coordinadorForestal.equals(tecnicoResponsable.getNombre())) {
			coordinadorForestal = tecnicoResponsable.getNombre();
			
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("coordinadorForestal", coordinadorForestal);
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
		}
		
		return tecnicoResponsable;
    }
	
}
