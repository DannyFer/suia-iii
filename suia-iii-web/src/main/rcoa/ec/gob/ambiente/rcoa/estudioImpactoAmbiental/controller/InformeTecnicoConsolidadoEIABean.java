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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
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
import ec.gob.ambiente.rcoa.util.NotificacionInternaUtil;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoActualizacionCertificadoInterseccionFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarPdf;

@ManagedBean
@ViewScoped
public class InformeTecnicoConsolidadoEIABean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(InformeTecnicoConsolidadoEIABean.class);

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
	private UsuarioFacade usuarioFacade;

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
	private ObservacionesEsIAFacade observacionesEsIAFacade;
	@EJB
	private AreaFacade areaFacade;

	@EJB
	private BandejaFacade bandejaFacade;
	@EJB
	private EquipoApoyoProyectoEIACoaFacade equipoApoyoProyectoEIACoaFacade;
	@EJB
	private ProyectoActualizacionCertificadoInterseccionFacade proyectoActualizacionCIFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private OficioPronunciamientoEsIAFacade oficioPronunciamientoEsIAFacade;

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
	private DocumentoEstudioImpacto documentoInforme, documentoInspeccion;

	@Getter
	@Setter
	private List<DocumentoEstudioImpacto> listaDocumentosInformesTecnicos;

	@Getter
	@Setter
	private List<InformeTecnicoEsIA> listaInformes, selectedTecnicos;

	@Getter
	private List<SelectItem> listaTipoPronunciamiento;

	@Getter
	@Setter
	private Integer idProyecto, numeroObservaciones, idTarea, tipoInforme;

	@Getter
	@Setter
	private String urlReporte, nombreReporte, justificacionInspecion, resumenObservaciones;

	@Getter
	@Setter
	private boolean realizoInspeccion, esRequerido;

	@Getter
	@Setter
	private byte[] archivoReporte;

	@Getter
	@Setter
	private Map<String, Object> variables;

	@Getter
	@Setter
	private boolean esTerceraObservacion = false, existeObservacionesEstudio, observacionesDocumentos;

	@Getter
	@Setter
	private boolean esPlantaCentral = false, mostrarFacilitadores = false, ingresarFacilitadores = false;
	@Getter
	@Setter
	private Boolean esTecnico, observacionesSustanciales, mostrarInformesApoyo, requiereIngresoPlan;
	@Getter
	@Setter
	
	private DocumentoEstudioImpacto documentoAdjunto;
	@Getter
	@Setter
	private List<DocumentoEstudioImpacto> listaDocumentosAdjuntos;
	@Getter
    @Setter
    private String paginaActiva,titulo="",tipoDocumento;;
	private Map<String, List<DocumentoEstudioImpacto>> listadoDocumentosEIA = new HashMap<String, List<DocumentoEstudioImpacto>>();
	@Getter
	@Setter
	private List<EntityAdjunto> listaDocumentosPorPagina;
	@Getter
    @Setter
	private boolean habilitadoIngreso=true;

	@PostConstruct
	public void init() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			paginaActiva="EIA_DOCUMENTO_HABILITANTE";
			tipoInforme = InformeTecnicoEsIA.consolidado;
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),
					bandejaTareasBean.getProcessId());
			esRequerido = false;
			String idProyectoString = (String) variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);
			String plantaCentral = (String) variables.get("esPlantaCentral");
			esPlantaCentral = Boolean.parseBoolean(plantaCentral);
			
			int numeroObs = 4;
			if(variables.get("totalRevisiones")==null){
				numeroObs = 3;
			}
			
			// busco el numero de la revision
			if (variables.get("numeroRevision") != null) {
				String valor = (String) variables.get("numeroRevision");
				numeroObservaciones = Integer.valueOf(valor);
				esTerceraObservacion = numeroObservaciones.equals(numeroObs);
			}
			
			requiereIngresoPlan = false;
			if(variables.get("requiereIngresoPlan") != null){
				requiereIngresoPlan=Boolean.parseBoolean(variables.get("requiereIngresoPlan").toString());	
			}

			proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);

			esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);

			areaTramite = proyecto.getAreaResponsable();

			ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto)
					.getUbicacionesGeografica();
			idTarea = (int) bandejaTareasBean.getTarea().getTaskId();
			String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, tipoInforme);

			// verifico si existen observaciones ingresdas
			validarObservaciones();
			mostrarInformesApoyo = false;
			selectedTecnicos = new ArrayList<InformeTecnicoEsIA>();
			if (esiaProyecto.getEquipoMultidisciplinario()) {
				mostrarInformesApoyo = true;
				// obtengo los informes tecnicos del proyecto
				listaInformes = informeTecnicoEsIAFacade.obtenerInformesPorEstudio(esiaProyecto);
				if (listaInformes != null && listaInformes.size() > 0) {
					generarListaInformesApoyo();
				} else {
					listaInformes = new ArrayList<InformeTecnicoEsIA>();
					listaDocumentosInformesTecnicos = new ArrayList<DocumentoEstudioImpacto>();
				}
			} else {
				listaInformes = informeTecnicoEsIAFacade.obtenerInformesPorEstudio(esiaProyecto);
				if (listaInformes != null && listaInformes.size() > 0) {
					mostrarInformesApoyo = true;
					generarListaInformesApoyo();
				}
			}
			
			// verifi si escogio q necesitaba inspeccion
			realizoInspeccion = (esiaProyecto.getInspeccion() == null) ? false : esiaProyecto.getInspeccion();
			justificacionInspecion = "";
			if (esiaProyecto.getInspeccion() == null || !esiaProyecto.getInspeccion()) {
				justificacionInspecion = esiaProyecto.getJustificacionInspeccion();
			} else {
				if (informeTecnico != null)
					documentoInspeccion = documentosFacade.documentoXTablaIdXIdDoc(esiaProyecto.getId(),
							InformacionProyectoEia.class.getSimpleName(),
							TipoDocumentoSistema.EIA_INFORME_INSPECCION_RCOA);
				if (documentoInspeccion == null) {
					documentoInspeccion = new DocumentoEstudioImpacto();
				}
			}
			observacionesDocumentos = false;
			if (variables.get("observacionesInformeConsolidado") != null && esiaProyecto.getEquipoMultidisciplinario()
					&& listaInformes.size() > 0) {
				observacionesDocumentos = Boolean
						.parseBoolean((String) variables.get("observacionesInformeConsolidado"));
				;
			}
			// cargo la lista de proninciamientos
			cargarListaTipoPronunciamiento();
			
			esTecnico = false;
			if(tarea.contains("elaborar"))
				esTecnico = true;
			
			// cargo los archivo subidos
		    tipoDocumento = "EIA_DOCUMENTO_HABILITANTE";
        	listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(proyecto.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_DOCUMENTO_HABILITANTE);
	        if(listaDocumentosAdjuntos!=null){
	        	listadoDocumentosEIA.put(tipoDocumento, listaDocumentosAdjuntos);
	        }
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos Informe.");
		}
	}
	
	public void generarListaInformesApoyo() throws Exception {
		listaDocumentosInformesTecnicos = new ArrayList<DocumentoEstudioImpacto>();
		List<Integer> listaIdInfoVersionAnterior = new ArrayList<>();
		List<String> listaTecnicos = new ArrayList<>();
		List<InformeTecnicoEsIA> listaInformesVersionAnterior = new ArrayList<InformeTecnicoEsIA>();
		for (InformeTecnicoEsIA objinforme : listaInformes) {
			// verifico si no tienen una nueva version el informe caso contrario lo elimino
			if (!listaIdInfoVersionAnterior.contains(objinforme.getId()) && !listaTecnicos
					.contains(objinforme.getUsuarioCreacion() + "-" + objinforme.getTipoInforme())) {
				listaTecnicos.add(objinforme.getUsuarioCreacion() + "-" + objinforme.getTipoInforme());
				List<DocumentoEstudioImpacto> listaDocumentosAdjuntos = documentosFacade
						.documentoXTablaIdXIdDocLista(objinforme.getId(),
								InformeTecnicoEsIA.class.getSimpleName(),
								TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);
				if (listaDocumentosAdjuntos != null && listaDocumentosAdjuntos.size() > 0) {
					listaDocumentosInformesTecnicos.add(listaDocumentosAdjuntos.get(0));
				}
				// si el informe no es de apoyo quito de la lista de tecnicos
				if (!objinforme.getTipoInforme().equals(InformeTecnicoEsIA.apoyo))
					listaInformesVersionAnterior.add(objinforme);
			} else {
				listaInformesVersionAnterior.add(objinforme);
			}
			if (objinforme.getIdInformePrincipal() != null)
				listaIdInfoVersionAnterior.add(objinforme.getIdInformePrincipal());
		}
		// elimino los informes que ya tienen otra version
		if (listaDocumentosInformesTecnicos.size() > 0)
			listaInformes.removeAll(listaInformesVersionAnterior);
	}

	public void cargarListaTipoPronunciamiento() {
		listaTipoPronunciamiento = new ArrayList<SelectItem>();
		listaTipoPronunciamiento.add(new SelectItem(InformeTecnicoEsIA.aprobado, "Pronunciamiento Aprobación"));
		if (informeTecnico != null && informeTecnico.getTipoPronunciamiento() != null
				&& informeTecnico.getTipoPronunciamiento().equals(InformeTecnicoEsIA.observadoSustancial))
			listaTipoPronunciamiento.add(
					new SelectItem(InformeTecnicoEsIA.observadoSustancial, "Pronunciamiento Observación Sustancial"));
		else{
			if (esTerceraObservacion)
				listaTipoPronunciamiento.add(new SelectItem(InformeTecnicoEsIA.terceraRevision, "Pronunciamiento Observación"));
			else
				listaTipoPronunciamiento.add(new SelectItem(InformeTecnicoEsIA.observado, "Pronunciamiento Observación"));
		}
			

		// mostrar facilitadores cuando es OT o si es PC o GAD pero no se asigno un
		// técnico social
		mostrarFacilitadores = false;
		ingresarFacilitadores = false;
		if (informeTecnico != null && informeTecnico.getTipoPronunciamiento() != null
				&& informeTecnico.getTipoPronunciamiento() == 1) {
			String esGadString = (String) variables.get("esGad");
			Boolean esGad = Boolean.parseBoolean(esGadString);
			mostrarFacilitadores = true;

			if (!esPlantaCentral && !esGad)
				ingresarFacilitadores = true;
			else {
				EquipoApoyoProyecto equipoApoyoProyecto = equipoApoyoProyectoEIACoaFacade
						.obtenerEquipoApoyoProyecto(esiaProyecto);
				if (!equipoApoyoProyecto.isTecnicoSocial())
					ingresarFacilitadores = true;
			}
		}
	}

	public void validarObservaciones() throws ServiceException {
		
		existeObservacionesEstudio = false;
		if (informeTecnico == null)
			return;
		
		String nombreClaseObservaciones1 = InformeTecnicoEsIA.class.getSimpleName() + "_"
				+ informeTecnico.getUsuarioCreacion() + "_" + informeTecnico.getTipoInforme();
		String nombreClaseObservaciones2 = InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnico.getTipoInforme();
		List<String> nombreClaseObservaciones = new ArrayList<>();
		nombreClaseObservaciones.add(nombreClaseObservaciones1);
		nombreClaseObservaciones.add(nombreClaseObservaciones2);
		
		Integer idClaseObservaciones = esiaProyecto.getId();
		List<ObservacionesEsIA> observacionesPendientes = observacionesEsIAFacade
				.listarPorIdClaseNombreClaseNoCorregidas(idClaseObservaciones, nombreClaseObservaciones);

		List<ObservacionesEsIA> observacionesInfApoyo = observacionesInformesApoyo(idClaseObservaciones, nombreClaseObservaciones);
		
		if (observacionesPendientes.size() > 0 || observacionesInfApoyo.size() >0) {
			existeObservacionesEstudio = true;
		}
		
		if(observacionesInfApoyo.size() >0){
			observacionesPendientes.addAll(observacionesInfApoyo);
		}
		
		if (observacionesPendientes.size() > 0) {
			resumenObservaciones = "<ul>";
			for (ObservacionesEsIA observacionesEsIA : observacionesPendientes) {
				resumenObservaciones += "<li>";
				resumenObservaciones += observacionesEsIA.getCampo() + ": " + observacionesEsIA.getDescripcion();
				resumenObservaciones += "</li>";
			}
			resumenObservaciones += "</ul>";
		} else
			resumenObservaciones = "";
	}
	
	private List<ObservacionesEsIA> observacionesInformesApoyo(Integer idClase, List<String> nombreClaseObservaciones){
		List<ObservacionesEsIA> listaObs = new ArrayList<>();		
		try {
			
			List<ObservacionesEsIA> listaObsAux = observacionesEsIAFacade.listarPorIdClaseNoCorregidas(idClase, nombreClaseObservaciones.get(0)); // recupera las observaciones que no corresponden al nombre de la clase
			
			if(nombreClaseObservaciones.size() > 1) {
				for (ObservacionesEsIA item : listaObsAux) {
					if(!item.getNombreClase().equals(nombreClaseObservaciones.get(1))) { //si hay mas de 2 nombres también se valida que no correspondan al nombre de la otra clase
						listaObs.add(item);
					}
				}
			} else {
				listaObs.addAll(listaObsAux);
			}
			
		} catch (Exception e) {
			LOG.error("Error en obtener las observaciones", e);
		}
		
		return listaObs;
	}
	

	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}

	public void generarInformeConsolidado(Boolean marcaAgua) {
		try {
			
			plantillaReporte = plantillaReporteFacade
					.getPlantillaReporte(TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);

			if(esTecnico) {
				if (informeTecnico == null || (informeTecnico.getNumeroRevision() != numeroObservaciones)) {
					informeTecnico = new InformeTecnicoEsIA();
					informeTecnico.setNombreReporte("informeTecnico.pdf");
					informeTecnico.setIdEstudio(esiaProyecto.getId());
					informeTecnico.setIdTarea(idTarea);
					informeTecnico.setTipoInforme(tipoInforme);
					informeTecnico.setNumeroRevision(numeroObservaciones);
	//				informeTecnicoEsIAFacade.guardar(informeTecnico, JsfUtil.getLoggedUser().getArea());
					informeTecnicoEsIAFacade.guardar(informeTecnico, areaTramite);
				} else {
					if (informeTecnico.getIdTarea() != null && !informeTecnico.getIdTarea().equals(idTarea)) {
						Integer idInforme = informeTecnico.getId();
						InformeTecnicoEsIA informeTecnicoOriginal = (InformeTecnicoEsIA) SerializationUtils
								.clone(informeTecnico);
						informeTecnicoOriginal.setId(null);
						informeTecnicoOriginal.setCodigoInforme(null);
						informeTecnicoOriginal.setIdTarea(idTarea);
						informeTecnicoOriginal.setFechaInforme(new Date());
						informeTecnicoOriginal.setIdInformePrincipal(
								informeTecnico.getIdInformePrincipal() != null ? informeTecnico.getIdInformePrincipal()
										: informeTecnico.getId());
						
						informeTecnico = informeTecnicoEsIAFacade.guardar(informeTecnicoOriginal, areaTramite);
						
						OficioPronunciamientoEsIA oficioPronunciamiento = oficioPronunciamientoEsIAFacade.getPorEstudioInforme(esiaProyecto.getId(), idInforme);;
						if(oficioPronunciamiento != null){
							OficioPronunciamientoEsIA oficioOriginal = (OficioPronunciamientoEsIA) SerializationUtils.clone(oficioPronunciamiento);
							oficioOriginal.setId(null);
							oficioOriginal.setCodigoOficio(null);
							oficioOriginal.setFechaOficio(new Date());
							oficioOriginal.setIdInforme(informeTecnico.getId());
							
							Area areaTramite = proyecto.getAreaResponsable();

							if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
								areaTramite = areaTramite.getArea();
							else if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
								if (informeTecnico.getTipoPronunciamiento().equals(InformeTecnicoEsIA.aprobado)) {
									areaTramite = areaTramite.getArea();
								}
							}
							
							oficioPronunciamientoEsIAFacade.guardarConsolidado(oficioOriginal, areaTramite, oficioPronunciamiento.getTipoOficio());	
						}
	
					}
					informeTecnico.setNombreReporte("informeTecnico_"
							+ UtilViabilidad.getFileNameEscaped(informeTecnico.getCodigoInforme().replace("/", "-"))
							+ ".pdf");
				}
			}
			
			validarObservaciones();
			if (resumenObservaciones != null && !resumenObservaciones.isEmpty()) {
				informeTecnico.setObservaciones(resumenObservaciones);
			}else{
				informeTecnico.setObservaciones("");
			}

			EntityInformeTecnicoEsIA entity = cargarDatosInforme();

			File informePdfAux = UtilGenerarPdf.generarFichero(plantillaReporte.getHtmlPlantilla(),
					informeTecnico.getNombreReporte(), true, entity);

			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ",
					BaseColor.GRAY);

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
			LOG.error("Error al cargar el informe tecnico", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public EntityInformeTecnicoEsIA cargarDatosInforme() throws ServiceException {

		EntityInformeTecnicoEsIA entity = new EntityInformeTecnicoEsIA();

		String nombreOperador = usuarioFacade.recuperarNombreOperador(proyecto.getUsuario()).get(0);

		// generacion Ubicacion
		List<UbicacionesGeografica> ubicacionProyectoLista = proyectoLicenciaCoaUbicacionFacade
				.buscarPorProyecto(proyecto);
		String strTableUbicacion = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody><tr BGCOLOR=\"#B2B2B2\">" + "<td><strong>Provincia</strong></td>"
				+ "<td><strong>Cantón</strong></td>" + "<td><strong>Parroquia</strong></td>" + "</tr>";

		for (UbicacionesGeografica ubicacion : ubicacionProyectoLista) {
			strTableUbicacion += "<tr>";
			strTableUbicacion += "<td>" + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre()
					+ "</td>";
			strTableUbicacion += "<td>" + ubicacion.getUbicacionesGeografica().getNombre() + "</td>";
			strTableUbicacion += "<td>" + ubicacion.getNombre() + "</td>";
			strTableUbicacion += "</tr>";
		}
		strTableUbicacion += "</tbody></table></center>";

		ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
		CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();

		entity.setNumeroInforme(informeTecnico.getCodigoInforme());
		entity.setCiudad(
				JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entity.setFecha(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
		entity.setNombreProyecto(proyecto.getNombreProyecto());
		entity.setCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
		entity.setNombreOperador(nombreOperador);
		entity.setNombreArea(areaTramite.getAreaName());
		entity.setUbicacion(strTableUbicacion);
		entity.setDireccionProyecto(proyecto.getDireccionProyecto());
		entity.setSector(actividadPrincipal.getTipoSector().getNombre());
		entity.setSuperficie(proyecto.getSuperficie() + " ha");
		entity.setAltitud(null);
		entity.setAntecedentes(informeTecnico.getAntecedentes());
		entity.setObjetivos(informeTecnico.getObjetivos());
		entity.setCaracteristicas(informeTecnico.getCaracteristicas());
		entity.setEvaluacion(informeTecnico.getEvaluacionTecnica());
		entity.setObservaciones(informeTecnico.getObservaciones());
		entity.setJustificacion(justificacionInspecion == null || justificacionInspecion.equals("") ? ""
				: "<strong>Justificaci&oacute;n</strong><br />" + justificacionInspecion);
		entity.setConclusionesRecomendaciones(informeTecnico.getConclusionesRecomendaciones());
		entity.setNombreTecnico(JsfUtil.getLoggedUser().getPersona().getNombre());
//		entity.setAreaTecnico(JsfUtil.getLoggedUser().getArea().getAreaName());
		entity.setAreaTecnico(proyecto.getAreaResponsable().getAreaName());
		if (areaTramite.getTipoArea().getSiglas().equals("EA"))
			entity.setSiglasEnte(areaTramite.getAreaAbbreviation());

		String rolAutoridad = "";
		Usuario usuarioAutoridad = new Usuario();
		Area areaResponsable = proyecto.getAreaResponsable();
		if (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
			rolAutoridad = "role.zonal.responsable.oficina.tecnica";
		} else if (areaResponsable.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES)
				|| areaResponsable.getTipoArea().getSiglas().equals("EA")) {
			rolAutoridad = "role.esia.gad.coordinador";
		} else if (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
			Integer idSector = actividadPrincipal.getTipoSector().getId();
			String tipoRol = "role.esia.pc.coordinador.tipoSector." + idSector;
			rolAutoridad = tipoRol;
		} else if (areaResponsable.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
			rolAutoridad = "role.esia.ga.coordinador";
		}
		if (usuarioAutoridad.getId() == null) {
			usuarioAutoridad = buscarUsuarioBean.buscarUsuario(rolAutoridad, areaResponsable);
		}
		if (usuarioAutoridad != null) {
			entity.setNombreResponsable(usuarioAutoridad.getPersona().getNombre());
			entity.setAreaResponsable(areaResponsable.getAreaName());
		} else {
			return null;
		}

		return entity;
	}

	public void guardarDatos() {
		try {
			informeTecnico.setFechaInforme(new Date());
			if (informeTecnico.getTipoPronunciamiento() != null) {
				if (!informeTecnico.getTipoPronunciamiento().equals(InformeTecnicoEsIA.aprobado)) {
					if (observacionesSustanciales != null) {
						if (observacionesSustanciales) {
							informeTecnico.setTipoPronunciamiento(InformeTecnicoEsIA.observadoSustancial);
						} else {
							if (esTerceraObservacion) {
								informeTecnico.setTipoPronunciamiento(InformeTecnicoEsIA.terceraRevision);
							} else {
								informeTecnico.setTipoPronunciamiento(InformeTecnicoEsIA.observado);
							}
						}
					}
					cargarListaTipoPronunciamiento();
				}
			}
//			informeTecnicoEsIAFacade.guardar(informeTecnico, JsfUtil.getLoggedUser().getArea());
			informeTecnicoEsIAFacade.guardar(informeTecnico, areaTramite);
			resumenObservaciones = informeTecnico.getObservaciones();
			generarInformeConsolidado(true);

			if (!esPlantaCentral) {
				// si es proyecto de PC no modifico xq la solicitud ya la hizo el cartografo
				if (informeTecnico.getTipoPronunciamiento().equals(2)) {
					// si es observado
					proyectoLicenciaCoaFacade.guardar(proyecto); // para guardar solicitud de actualizacion de CI
					proyectoActualizacionCIFacade.guardarHistorialActualizacionCertificado(
							proyecto.getCodigoUnicoAmbiental(), proyecto.getEstadoActualizacionCertInterseccion(),
							null);
				} else {
					proyecto.setEstadoActualizacionCertInterseccion(0);
					proyectoLicenciaCoaFacade.guardar(proyecto);
				}
			}

			if (documentoInspeccion != null && documentoInspeccion.getContenidoDocumento() != null
					&& documentoInspeccion.getId() == null) {
				Integer documetoId = documentoInspeccion.getId() != null ? documentoInspeccion.getId() : 0;
				documentosFacade.eliminarDocumentos(esiaProyecto, InformacionProyectoEia.class.getSimpleName(),
						TipoDocumentoSistema.EIA_INFORME_INSPECCION_RCOA, documetoId,
						loginBean.getUsuario().getNombre());
				documentoInspeccion = documentosFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(),
						"INFORME_INSPECCION", documentoInspeccion, TipoDocumentoSistema.EIA_INFORME_INSPECCION_RCOA);
			}
			guardarDocumentosHabilitantes();
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}

	public void guardarDocumentosAlfresco() {
		try {
			generarInformeConsolidado(false);

			documentoInforme = new DocumentoEstudioImpacto();
			documentoInforme.setContenidoDocumento(informeTecnico.getArchivo());
			documentoInforme.setNombre(informeTecnico.getNombreReporte());
			documentoInforme.setExtesion(".pdf");
			documentoInforme.setMime("application/pdf");
			documentoInforme.setNombreTabla(InformeTecnicoEsIA.class.getSimpleName());
			documentoInforme.setIdTable(informeTecnico.getId());

			documentoInforme = documentosFacade.guardarDocumentoAlfrescoCA(proyecto.getCodigoUnicoAmbiental(),
					"INFORME_TECNICO", documentoInforme, TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);

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
					"INFORME_TECNICO CONSOLIDADO", documentoManual, TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void recuperarInformeTecnico() {
		try {
			informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, tipoInforme);
			List<DocumentoEstudioImpacto> documentos = documentosFacade.documentoXTablaIdXIdDocLista(
					informeTecnico.getId(), InformeTecnicoEsIA.class.getSimpleName(),
					TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);
			if (documentos.size() > 0) {
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

	// Documento
	public void uploadDocumentoEIA(FileUploadEvent file) {
		String[] split = file.getFile().getContentType().split("/");
		String extension = "." + split[split.length - 1];
		documentoInspeccion = new DocumentoEstudioImpacto();
		documentoInspeccion.setNombre(file.getFile().getFileName());
		documentoInspeccion.setMime(file.getFile().getContentType());
		documentoInspeccion.setContenidoDocumento(file.getFile().getContents());
		documentoInspeccion.setExtesion(extension);
		documentoInspeccion.setNombreTabla(InformacionProyectoEia.class.getSimpleName());
		documentoInspeccion.setIdTable(esiaProyecto.getId());
		documentoInspeccion.setIdProceso(bandejaTareasBean.getProcessId());
	}

	public void eliminarAdjunto(DocumentoEstudioImpacto objDocumentoEIA ){
		if(objDocumentoEIA != null ){
			listaDocumentosAdjuntos.remove(objDocumentoEIA);
			if(objDocumentoEIA.getId() != null){
				objDocumentoEIA.setEstado(false);
				documentosFacade.guardar(objDocumentoEIA);
			}
		}
	}
	public String getnombreTecnico(String nombre) {
		Usuario objUsuario = usuarioFacade.buscarUsuario(nombre);
		if (objUsuario != null && objUsuario.getPersona() != null) {
			return objUsuario.getPersona().getNombre();
		}
		return "";
	}

	public StreamedContent descargarInforme() throws Exception {
		recuperarInformeTecnico();

		DefaultStreamedContent content = new DefaultStreamedContent();
		if (archivoReporte != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(archivoReporte), "application/octet-stream");
			content.setName(nombreReporte);
		}
		return content;
	}

	public void guardarEsia() {
		informacionProyectoEIACoaFacade.guardar(esiaProyecto);
	}

	public void guadarProyecto() throws Exception {
		proyecto.setEstadoActualizacionCertInterseccion(2);// 2 Habilitado para modificacion del operador
		proyectoLicenciaCoaFacade.guardar(proyecto);

		proyectoActualizacionCIFacade.guardarHistorialActualizacionCertificado(proyecto.getCodigoUnicoAmbiental(), 2,
				null);

		Object[] parametrosCorreo = new Object[] { proyecto.getCodigoUnicoAmbiental() };

		String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
				"bodyNotificacionActualizacionCertificadoInterseccion", parametrosCorreo);

		Organizacion orga = organizacionFacade.buscarPorRuc(proyecto.getUsuarioCreacion());
		if (orga != null) {
			//Email.sendEmail(orga, "Regularización Ambiental Nacional", notificacion);
			Email.sendEmail(orga, "Regularización Ambiental Nacional", notificacion, proyecto.getCodigoUnicoAmbiental(),proyecto.getUsuario(), loginBean.getUsuario());
		} else {
			//Email.sendEmail(proyecto.getUsuario().getPersona(), "Regularización Ambiental Nacional", notificacion);
			Email.sendEmail(proyecto.getUsuario(), "Regularización Ambiental Nacional", notificacion,proyecto.getCodigoUnicoAmbiental(), loginBean.getUsuario());
		}
		
		NotificacionInternaUtil.remitirNotificacionesEstudio(proyecto, "Regularización Ambiental Nacional", notificacion, null);
	}
	 // Documento 
    public void asignarDocumentoEIA(FileUploadEvent file) {	
        String[] split=file.getFile().getContentType().split("/");
        String extension = "."+split[split.length-1];
        documentoAdjunto = new DocumentoEstudioImpacto();
        documentoAdjunto.setNombre(file.getFile().getFileName());
        documentoAdjunto.setMime(file.getFile().getContentType());
        documentoAdjunto.setContenidoDocumento(file.getFile().getContents());
        documentoAdjunto.setExtesion(extension);
        documentoAdjunto.setNombreTabla(DocumentoEstudioImpacto.class.getSimpleName());
    }
    public void agregarAdjunto(){
		try{
	        // Ingreso a alfresco de documento manifiesto recepcion
	        if (documentoAdjunto.getContenidoDocumento() != null) {
	        	cargarDocumentosLista(documentoAdjunto);
	        }
	        documentoAdjunto = new DocumentoEstudioImpacto();
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al guardar los adjuntos Habilitantes. Por favor comuníquese con Mesa de Ayuda");
		}
	}
	public void cargarDocumentosLista(DocumentoEstudioImpacto objDocumento ){
		for (Map.Entry<String, List<DocumentoEstudioImpacto>> entry : listadoDocumentosEIA.entrySet()) {
			if(paginaActiva.equals(entry.getKey())){
				   listaDocumentosAdjuntos = entry.getValue();
				   cargarArchivoTmp(objDocumento, entry.getKey());
				   break;
			    }
			}
		
	}
	public void cargarArchivoTmp(DocumentoEstudioImpacto objDocumento, String key ){
		   if(objDocumento != null && objDocumento.getContenidoDocumento() != null){
			   listaDocumentosAdjuntos.add(objDocumento);
			   listadoDocumentosEIA.put(key, listaDocumentosAdjuntos);
		   }
	}
	public String getEtiqueta(String key){
		return titulo="DOCUMENTOS HABILITANTES";
	}
	public void agregarListaDocumentos(String clave){
		try{
			if(listaDocumentosAdjuntos != null && listaDocumentosAdjuntos.size() > 0){
				EntityAdjunto objAdjunto = new EntityAdjunto();
				objAdjunto.setNombre(getEtiqueta(clave));
				objAdjunto.setExtension(clave);
				listaDocumentosPorPagina.add(objAdjunto);
			}
		}catch(Exception e){
			e.printStackTrace();
			LOG.error(e, e);
		}
	}
	public void guardarDocumentosHabilitantes() throws Exception{
		if(listaDocumentosAdjuntos!=null){
			TipoDocumentoSistema objTipoDocumento  = TipoDocumentoSistema.EIA_DOCUMENTO_HABILITANTE;
			for (DocumentoEstudioImpacto objDocumentoEIA : listaDocumentosAdjuntos) {
				if(objDocumentoEIA.getId() == null && objDocumentoEIA.getContenidoDocumento() != null){
					documentosFacade.ingresarDocumento(objDocumentoEIA, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), objTipoDocumento, objDocumentoEIA.getNombre(), InformacionProyectoEia.class.getSimpleName(), bandejaTareasBean.getProcessId());
				}
			}
		}
	}
	
	public StreamedContent descargarDocumentoEIA(DocumentoEstudioImpacto documento){
		try {
			if(documento!=null){
					if(documento.getContenidoDocumento()==null)
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getAlfrescoId()));	
					return getStreamedContentEIA(documento);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static StreamedContent getStreamedContentEIA(DocumentoEstudioImpacto documento){		
		try {
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),documento.getMime(),documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;		
	}	
}