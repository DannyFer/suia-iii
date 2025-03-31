package ec.gob.ambiente.suia.eia.descripcionProyecto.controler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.coordenadageneral.facade.CoordenadaGeneralFacade;
import ec.gob.ambiente.suia.domain.ActividadLicenciamiento;
import ec.gob.ambiente.suia.domain.ActividadesPorEtapa;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.CronogramaFasesProyectoEia;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.EtapasProyecto;
import ec.gob.ambiente.suia.domain.Fase;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaEia;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.ZonasFase;
import ec.gob.ambiente.suia.eia.descripcionProyecto.bean.DescripcionProyectoBean;
import ec.gob.ambiente.suia.eia.descripcionProyecto.facade.ActividadLicenciamientoFacade;
import ec.gob.ambiente.suia.eia.descripcionProyecto.facade.DescripcionProyectoFacade;
import ec.gob.ambiente.suia.eia.descripcionProyecto.service.SustanciaQuimicaEiaService;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FaseFichaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * @author liliana.chacha
 */
@ManagedBean
@ViewScoped
public class DescripcionProyectoController implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2342589800998026855L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(DescripcionProyectoController.class);

	@Getter
	@Setter
	private DescripcionProyectoBean descripcionProyectoBean;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@EJB
	private FaseFichaAmbientalFacade faseFichaAmbientalFacade;

	@EJB
	private DescripcionProyectoFacade descripcionProyectoFacade;

	@EJB
	private ActividadLicenciamientoFacade actividadLicenciamientoFacade;

	@EJB
	private CoordenadaGeneralFacade coordenadaGeneralFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	@Setter
	private EstudioImpactoAmbiental estudio, estudioHistorico;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private SustanciaQuimicaEiaService sustanciaQuimicaEiaService;

	@Getter
	@Setter
	private List<CronogramaFasesProyectoEia> calendario, calendarioOriginal, calendarioEnBdd, calendarioEliminadoEnBdd;

	@Getter
	@Setter
	private CronogramaFasesProyectoEia fasesProyectoEia;

	@Getter
	@Setter
	private SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionada;

	@Setter
	@Getter
	private UploadedFile file;

	@Setter
	@Getter
	private Boolean proyectoHidrocarburos;
		
	@Getter
	private Boolean esMineriaNoMetalicos;

	@Getter
	@Setter
	public EtapasProyecto etapaPorFase;

	@Setter
	@Getter
	private Fase faseDelProyecto;

	@Setter
	@Getter
	private ActividadesPorEtapa actividadPorEtapaDeFase;

	private boolean existeNuevoDocumento1;
	private boolean existeNuevoDocumento2;
	
	//Cris F: aumento de variables
	@Getter
	@Setter
	private Documento documentoDescripcion;
		
	@Getter
	@Setter
	private Documento documentoInsumos;
	
	private Map<String, Object> processVariables;
	private Integer numeroNotificaciones;
	private boolean existeObservaciones;
	private String promotor;
	
	@Getter
	@Setter
	private List<ActividadLicenciamiento> listaActividadesOriginales, listaActividadesEliminadas, listaActividadesModificadas, listaActividadesHistorico;
	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> listaSustanciasOriginales;
	
	@Getter
	@Setter
	private ActividadLicenciamiento actividadOriginal;
	
	@Setter
	@Getter
	private Boolean faseActividadUpdated;
	
	@Setter
	@Getter
	private Boolean nombreActividadUpdated;
	
	@Getter
	@Setter
	private List<CronogramaFasesProyectoEia> calendarioEtapasEliminadas;
	
	@Getter
	@Setter
	private List<EstudioImpactoAmbiental> listaEstudiosHistorico;
	
	@Getter
	@Setter
	public List<Documento> listaDocumentoDescripcionHistorico, listaDocumentosHistorico, listaDocumentoInsumoHistorico;
	
	@Getter
	@Setter
	private List<CronogramaFasesProyectoEia> listaCalendarioHistorial;
	
	@Getter
	@Setter
	private List<CoordenadaGeneral> coordenadaGeneralesEnBase, listaCoordenadasOriginal, listaCoordenadasEliminadasBdd, listaCoordenadasHistorico;

	@PostConstruct
	public void init() {
		proyectoHidrocarburos = false;
		try {
			
			/**
			 * Cristina Flores obtener variables
			 */
			processVariables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
			String numNotificaciones = (String) processVariables.get("cantidadNotificaciones");
			if(numNotificaciones != null){
				numeroNotificaciones = Integer.valueOf(numNotificaciones);
			}else{
				numeroNotificaciones = 0;
			}	
			promotor = (String) processVariables.get("u_Promotor");
			
			calendarioEtapasEliminadas = new ArrayList<CronogramaFasesProyectoEia>();
			//Fin CF								
			
			this.existeNuevoDocumento1 = false;
			this.existeNuevoDocumento2 = false;
			setDescripcionProyectoBean(new DescripcionProyectoBean());
			getDescripcionProyectoBean().iniciar();
			sustanciaQuimicaSeleccionada = new SustanciaQuimicaPeligrosa();
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			estudio =(EstudioImpactoAmbiental) request.getSession().getAttribute("estudio");			
			
			esMineriaNoMetalicos=estudio.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudio.getResumenEjecutivo()==null?true:false;
			
            String url = request.getRequestURI();
            if(!url.equals("/suia-iii/prevencion/licenciamiento-ambiental/eia/impactoAmbiental/identificacionEvaluacionImpactos.jsf"))
            {
            	if(!esMineriaNoMetalicos){
	            	sustanciasQuimicas();
	    			actividadesPorEtapa();
            	}
            	
            	cargarNombresDocumentos();
            	
    			//cargarEtapasParaEvaluacion();
    			if (estudio != null) {
    				if (estudio.getDocumentoDescripcion() == null) {
    					estudio.setDocumentoDescripcion(new Documento());
    				}
    				if (estudio.getDocumentoInsumos() == null) {
    					estudio.setDocumentoInsumos(new Documento());
    				}
    				
    				if(!esMineriaNoMetalicos){
	    				//MarielaG
	    				//Cambio metodo para recuperar todos lo registros de base y evitar realizar doble consulta para recuperar los registros originales
	    				//calendario = descripcionProyectoFacade.obtenerCronogramaFasesPorEIA(estudio.getId());
	    				calendarioEnBdd= descripcionProyectoFacade.obtenerCronogramaFasesEnBddPorEIA(estudio.getId());
	    				calendario = new ArrayList<>();
	    				for(CronogramaFasesProyectoEia cronograma : calendarioEnBdd){
	    					if (cronograma.getIdHistorico() == null) {
	    						calendario.add(cronograma);
	    					}
	    				}
	    				
	    				
	    				//MarielaG
	    				//Cambio metodo para recuperar todos lo registros de base y evitar realizar doble consulta para recuperar los registros originales
	    				//List<SustanciaQuimicaEia> sustancias = descripcionProyectoFacade.obtenerSustanciasQuimicasPorEia(estudio.getId());
						List<SustanciaQuimicaEia> listaSustanciaQuimicasEnBdd = sustanciaQuimicaEiaService.obtenerSustanciasQuimicasEnBddPorEia(estudio.getId());
						descripcionProyectoBean.setSustanciaQuimicaEiaListaBdd(listaSustanciaQuimicasEnBdd);
						
						List<SustanciaQuimicaEia> listaSustanciaQuimicas = new ArrayList<>();
	    				for (SustanciaQuimicaEia sqeia : listaSustanciaQuimicasEnBdd) {
	    					if (sqeia.getIdHistorico() == null) {
	    						listaSustanciaQuimicas.add(sqeia);
		    					for (SustanciaQuimicaPeligrosa sq : descripcionProyectoBean.getSustanciaQuimicasPeligrosas()) {
		    						if (sqeia.getSustanciaQuimicaPeligrosa().getDescripcion().equals(sq.getDescripcion())) {
		    							sq.setSeleccionado(true);
		    							break;
		    						}
		    					}
		    					sqeia.getSustanciaQuimicaPeligrosa().setSeleccionado(true);
		    					descripcionProyectoBean.getSustanciaQuimicasSeleccionadas().add(
		    							sqeia.getSustanciaQuimicaPeligrosa());
	    					}
	    				}
	
	    				//MarielaG
	    				//Cambio para evitar doble consulta a la base de datos
	    				//descripcionProyectoBean.setSustanciaQuimicaEiaLista(sustanciaQuimicaEiaService.obtenerSustanciasQuimicasPorEia(estudio.getId()));
	    				descripcionProyectoBean.setSustanciaQuimicaEiaLista(listaSustanciaQuimicas);
    				}
    			}
    			if (calendario == null)
    				calendario = new ArrayList<CronogramaFasesProyectoEia>();
    			
    			descripcionProyectoBean.setEstudioAmbiental(estudio);
    			descripcionProyectoBean.setFasesPorSubsector(cargarFases());
    			
    			if(!esMineriaNoMetalicos)
    				cargarActividadesLicenciamiento(descripcionProyectoBean.getEstudioAmbiental());
//    			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
//    					bandejaTareasBean.getTarea().getProcessInstanceId());
//    			Integer idProyecto = Integer.parseInt((String) variables.get(Constantes.ID_PROYECTO));
//    			proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);

    			if(estudio.getProyectoLicenciamientoAmbiental().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)) {
    				proyectoHidrocarburos = true;
    				faseDelProyecto = estudio.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getFase();
    				ZonasFase zonaEstudio = estudio.getZonasFase();
    				if(zonaEstudio == null) {
    					RequestContext.getCurrentInstance().execute("PF('aviso').show();");
    				} else {
    					descripcionProyectoBean.setListaEtapasPorfaseYzonas(descripcionProyectoFacade.getEtapasPorFaseYzonas(faseDelProyecto.getId(), zonaEstudio.getId()));
    				}
    				//descripcionProyectoBean.setListaActividadesPorEtapas(new ArrayList<ActividadesPorEtapa>());
    			}else{
    				descripcionProyectoBean.setListaEtapasPorfaseYzonas(new ArrayList<EtapasProyecto>());
    			}
    			actividadPorEtapaDeFase = new ActividadesPorEtapa();
    			
    			if(numeroNotificaciones > 0){
    				existeObservaciones = true;
    				
    				//MarielaG
    				//consultar datos originales cuando el usuario es diferente al proponente
//    				if(!promotor.equals(loginBean.getNombreUsuario())){
    					consultarEstudioOriginal(); 
    					if(!esMineriaNoMetalicos){
	    					cargarActividadesOriginales(); 
	    					cargarSustanciasOriginales(); 
	    					cargarCronogramaOriginal(); 
    					}
//    				}else{
    					//Cris F: historico
    					consultarRegistroAnterior();
//    				}
    			}
			
            }
            
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
			LOG.error("Error al realizar la operación.", e);
		}
	}

	public StreamedContent descargar(int indice) throws IOException {
		DefaultStreamedContent content = null;
		try {
			switch (indice) {
			case 0:
				this.descargarAlfresco(estudio.getDocumentoDescripcion());
				if (estudio.getDocumentoDescripcion() != null && documentoDescripcion.getNombre() != null
						&& documentoDescripcion.getContenidoDocumento() != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(documentoDescripcion.getContenidoDocumento()));
					content.setName(documentoDescripcion.getNombre());
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
				break;
			case 1:
				//Cris F: Este es el codigo que estaba en la parte del historial antes de actualizar el proyecto 
//				this.descargarAlfresco(estudio.getDocumentoInsumos());
//				if (estudio.getDocumentoInsumos() != null && estudio.getDocumentoInsumos().getNombre() != null
//						&& estudio.getDocumentoInsumos().getContenidoDocumento() != null) {
//					content = new DefaultStreamedContent(new ByteArrayInputStream(estudio.getDocumentoInsumos()

				if (estudio.getDocumentoInsumos() != null && documentoInsumos.getNombre() != null
						&& documentoInsumos.getContenidoDocumento() != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(documentoInsumos.getContenidoDocumento()));
					content.setName(documentoInsumos.getNombre());
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
				break;
			case 2:
				this.descargarAlfresco(descripcionProyectoBean.getActividadesPorEtapa().getCoordenadaActividad());
				if(descripcionProyectoBean.getActividadesPorEtapa().getCoordenadaActividad() != null && descripcionProyectoBean.getActividadesPorEtapa().getCoordenadaActividad().getNombre() != null
						&& descripcionProyectoBean.getActividadesPorEtapa().getCoordenadaActividad().getContenidoDocumento() != null){
					content = new DefaultStreamedContent(new ByteArrayInputStream(descripcionProyectoBean.getActividadesPorEtapa().getCoordenadaActividad()
							.getContenidoDocumento()));
					content.setName(descripcionProyectoBean.getActividadesPorEtapa().getCoordenadaActividad().getNombre());
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

				break;
			default:
				LOG.error("Indice de archivo adjunto no esperado para descargar el adjunto");
				JsfUtil.addMessageError("Ocurrió un error al tratar de descargar el archivo, por favor comunicarse con mesa de ayuda.");
				break;
			}
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	public List<EtapasProyecto> cargarEtapasParaEvaluacion(){
		List<EtapasProyecto> listaEtapasParaEvaluacion = new ArrayList<>();
		for(ActividadesPorEtapa actEt : getDescripcionProyectoBean().getListaActividadesPorEtapas()){
			if(!listaEtapasParaEvaluacion.contains(actEt.getEtapasProyecto())){
				listaEtapasParaEvaluacion.add(actEt.getEtapasProyecto());
			}
		}
		if(listaEtapasParaEvaluacion == null || listaEtapasParaEvaluacion.isEmpty()){
			return null;
		}else{
			return listaEtapasParaEvaluacion;
		}
	}


	public void cargarNombresDocumentos() throws CmisAlfrescoException {
		if (estudio.getDocumentoDescripcion() != null) {
			//Cris f: esto estaba antes de la actualizacion
//			descripcionProyectoBean.setNombreDocumentoDescripcion(estudio.getDocumentoDescripcion().getNombre());
//			//this.descargarAlfresco(estudio.getDocumentoDescripcion());
			
			documentoDescripcion = documentosFacade.buscarDocumentoPorId(estudio.getIdDocumentoDescripcion());
			estudio.setDocumentoDescripcion(documentoDescripcion);
			descripcionProyectoBean.setNombreDocumentoDescripcion(documentoDescripcion.getNombre());
			this.descargarAlfresco(documentoDescripcion);
		}
		if (estudio.getDocumentoInsumos() != null && !esMineriaNoMetalicos) {
			//Cris F: este código estaba antes de la actualizacion
//			descripcionProyectoBean.setNombreDocumentoInsumos(estudio.getDocumentoInsumos().getNombre());
//			//this.descargarAlfresco(estudio.getDocumentoInsumos());

			documentoInsumos = documentosFacade.buscarDocumentoPorId(estudio.getIdDocumentoInsumos());
			estudio.setDocumentoInsumos(documentoInsumos);
			String nombre = estudio.getDocumentoInsumos().getNombre();
			descripcionProyectoBean.setNombreDocumentoInsumos(documentoInsumos.getNombre());
			this.descargarAlfresco(documentoInsumos);
		}
		if(descripcionProyectoBean.getActividadesPorEtapa().getCoordenadaActividad() != null && !esMineriaNoMetalicos){
			descripcionProyectoBean.setNombreDocumentoCoordenada(descripcionProyectoBean.getActividadesPorEtapa().getCoordenadaActividad().getNombre());
			//this.descargarAlfresco(descripcionProyectoBean.getActividadesPorEtapa().getCoordenadaActividad());
		}
	}

	public void seleccionarActividadCalendario(CronogramaFasesProyectoEia cronogramaFasesProyectoEia) {
		fasesProyectoEia = cronogramaFasesProyectoEia;
	}

	public void seleccionarActividadCalendarioEtapa(ActividadesPorEtapa actividadesPorEtapa) {
		actividadPorEtapaDeFase = actividadesPorEtapa;
	}

	public void agregarFechasAFaseProyecto() {
		// if (fasesProyectoEia.getFechaInicio() != null
		// && fasesProyectoEia.getFechaFin() != null)

		if (fasesProyectoEia.getFechaInicio() != null) {
			if(fasesProyectoEia.getFechaFin()!= null && fasesProyectoEia.getFechaInicio().compareTo(fasesProyectoEia.getFechaFin())>0){
				JsfUtil.addMessageError("La fecha de inicio no puede ser mayor que la fecha de fin");
			}
			else{
			for (CronogramaFasesProyectoEia cfp : calendario) {
				if (cfp.getCatalogoCategoriaFase().getFase().getNombre()
						.equals(fasesProyectoEia.getCatalogoCategoriaFase().getFase().getNombre())) {
					cfp.setFechaInicio(fasesProyectoEia.getFechaInicio());
					cfp.setFechaFin(fasesProyectoEia.getFechaFin());
					break;
				}
			}
				RequestContext.getCurrentInstance().execute("PF('agregarActividadCalendarioWdgt').hide();");
				fasesProyectoEia = new CronogramaFasesProyectoEia();
			}


		}

	}

	public void agregarFechasAactividadDeEtapaDeFase() {
		RequestContext.getCurrentInstance().execute("PF('agregarActividadEtapaCalendarioWdgt').hide();");
			actividadPorEtapaDeFase = new ActividadesPorEtapa();

	}

	public void agregarSustanciasQuimicasSeleccionadas() {
		for (SustanciaQuimicaPeligrosa sq : descripcionProyectoBean.getSustanciaQuimicasPeligrosas()) {
			if (sq.isSeleccionado() && !descripcionProyectoBean.getSustanciaQuimicasSeleccionadas().contains(sq)) {
				descripcionProyectoBean.getSustanciaQuimicasSeleccionadas().add(sq);
			}
			if (!sq.isSeleccionado() && descripcionProyectoBean.getSustanciaQuimicasSeleccionadas().contains(sq)) {
				descripcionProyectoBean.getSustanciaQuimicasSeleccionadas().remove(sq);
				
				for (SustanciaQuimicaEia sustanciaQuimicaEiaEliminar : descripcionProyectoBean.getSustanciaQuimicaEiaLista()) {
					if (sustanciaQuimicaEiaEliminar.getSustanciaQuimicaPeligrosa().getDescripcion()
							.equals(sq.getDescripcion())) {
						descripcionProyectoBean.getSustanciaQuimicaEiaEliminadas().add(sustanciaQuimicaEiaEliminar);
					}
				}
			}
			
		}
		RequestContext.getCurrentInstance().execute("PF('adicionarSustanciaQuimica').hide();");
	}

	public void eliminarSustanciaQuimica() {
		descripcionProyectoBean.getSustanciaQuimicasSeleccionadas().remove(sustanciaQuimicaSeleccionada);
		for (SustanciaQuimicaPeligrosa sq : descripcionProyectoBean.getSustanciaQuimicasPeligrosas()) {
			if (sq.getDescripcion().equals(sustanciaQuimicaSeleccionada.getDescripcion())) {
				sq.setSeleccionado(false);
				break;
			}
		}
		for (SustanciaQuimicaEia sustanciaQuimicaEiaEliminar : descripcionProyectoBean.getSustanciaQuimicaEiaLista()) {
			if (sustanciaQuimicaEiaEliminar.getSustanciaQuimicaPeligrosa().getDescripcion()
					.equals(sustanciaQuimicaSeleccionada.getDescripcion())) {
				descripcionProyectoBean.getSustanciaQuimicaEiaEliminadas().add(sustanciaQuimicaEiaEliminar);
			}
		}

	}

	/**
	 * Lista de todas las sustancias químicas
	 */
	public void sustanciasQuimicas() {

		descripcionProyectoBean.setSustanciaQuimicasPeligrosas(descripcionProyectoFacade
				.obtenerSustanciasQuimicasPeligrosas());
	}

	public void actividadesPorEtapa() throws Exception{
		descripcionProyectoBean.setListaActividadesPorEtapas(descripcionProyectoFacade
				.obtenerActividadesPorEtapas(estudio));

	}

	public boolean validarRegistro() {
		List<String> mensajesError = new ArrayList<String>();
		StringBuilder functionJs = new StringBuilder();

		if ((estudio != null && estudio.getDocumentoDescripcion() == null)
				|| estudio.getDocumentoDescripcion().getNombre() == null
				|| estudio.getDocumentoDescripcion().getNombre().equals("")) {
			mensajesError.add("Debe adjuntar el documento con la descripción del Proyecto, Obra o Actividad.");
			functionJs.append("highlightComponent('form:lblDescripcion');");
		} else {
			functionJs.append("removeHighLightComponent('form:lblDescripcion');");
		}
		
		if(!esMineriaNoMetalicos)
		{
			if (descripcionProyectoBean.getSustanciaQuimicasSeleccionadas() == null ||
					descripcionProyectoBean.getSustanciaQuimicasSeleccionadas().isEmpty()) {
				mensajesError.add("El campo 'Sustancias químicas' es requerido.");
				functionJs.append("highlightComponent('form:tablaSustancias');");
			} else {
				functionJs.append("removeHighLightComponent('form:tablaSustancias');");
			}

			if (descripcionProyectoBean.getListaActividades() == null ||
					descripcionProyectoBean.getListaActividades().isEmpty() && !proyectoHidrocarburos) {
				mensajesError.add("El campo 'Cronograma de fases de proyecto' es requerido.");
				functionJs.append("highlightComponent('form:tablaCalendario');");
			} else {
				functionJs.append("removeHighLightComponent('form:tablaCalendario');");
			}


			if ((descripcionProyectoBean.getListaActividadesPorEtapas() == null ||
					descripcionProyectoBean.getListaActividadesPorEtapas().isEmpty()) && proyectoHidrocarburos) {
				mensajesError.add("El campo 'Cronograma de actividades por etapa' es requerido.");
				functionJs.append("highlightComponent('form:tablaCalendarioActEtapa');");
			} else {
				functionJs.append("removeHighLightComponent('form:tablaCalendarioActEtapa');");
			}
			
			if ((estudio.getDocumentoInsumos() == null || estudio.getDocumentoInsumos().getNombre() == null
					|| estudio.getDocumentoInsumos().getNombre().equals("")) && !proyectoHidrocarburos) {
					mensajesError
							.add("Debe adjuntar el documento con la descripción de materiales, insumos, equipos y herramientas de proyecto.");
					functionJs.append("highlightComponent('form:lblInsumos');");
				} else {
				functionJs.append("removeHighLightComponent('form:lblInsumos');");
			}			
		}

		

		RequestContext.getCurrentInstance().execute(functionJs.toString());
		if (mensajesError.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(mensajesError);
			return false;
		}
	}

	public void guardar() {
		try {
			if (!validarRegistro())
				return;
			if(!esMineriaNoMetalicos)
			{						
				for (CronogramaFasesProyectoEia cfp : calendario) {
					if(existeObservaciones){
						cfp.setEstudioAmbiental(descripcionProyectoBean.estudioAmbiental);
						descripcionProyectoFacade.guardarCronogramaFasesProyectoHistorico(cfp, numeroNotificaciones);
					}else{
						cfp.setEstudioAmbiental(descripcionProyectoBean.estudioAmbiental);
						descripcionProyectoFacade.guardarCronogramaFasesProyecto(cfp);
					}
				}

				for (SustanciaQuimicaPeligrosa sq : descripcionProyectoBean.getSustanciaQuimicasSeleccionadas()) {
					SustanciaQuimicaEia sustanciaQuimicaEia = new SustanciaQuimicaEia();
					sustanciaQuimicaEia.setEstudioAmbiental(descripcionProyectoBean.estudioAmbiental);
					sustanciaQuimicaEia.setSustanciaQuimicaPeligrosa(sq);

					if (!existeSustanciaQuimica(sustanciaQuimicaEia)) {
						if(existeObservaciones){
							descripcionProyectoFacade.guardarSustanciaQuimicaEiaHistorico(sustanciaQuimicaEia, numeroNotificaciones);
							descripcionProyectoBean.getSustanciaQuimicaEiaLista().add(sustanciaQuimicaEia);
						}else{
							descripcionProyectoFacade.guardarSustanciaQuimicaEia(sustanciaQuimicaEia);
							descripcionProyectoBean.getSustanciaQuimicaEiaLista().add(sustanciaQuimicaEia);
						}
					}
				}
			}
			if(existeObservaciones){
				if(!validarGuardarHistoricoEA()){
					estudioHistorico = null;					
				}else{
					if(!existeNuevoDocumento1 && !existeNuevoDocumento2){
						estudioHistorico = null;
					}
				}
				
				descripcionProyectoFacade.guardarHistorico(descripcionProyectoBean.getEstudioAmbiental(),
						estudioHistorico,
						descripcionProyectoBean.getListaActividades(),
						descripcionProyectoBean.getListaActividadesEliminadas(),
						descripcionProyectoBean.getCoordenadasGeneralEliminadas(),
						descripcionProyectoBean.getListaActividadesPorEtapas(),
						descripcionProyectoBean.getListaActividadesPorEtapasEliminadas(),
						descripcionProyectoBean.getSustanciaQuimicaEiaEliminadas(),
						this.existeNuevoDocumento1,
						this.existeNuevoDocumento2, numeroNotificaciones,
						calendarioEtapasEliminadas, esMineriaNoMetalicos);
				
			}else{			
				descripcionProyectoFacade.guardar(descripcionProyectoBean.getEstudioAmbiental(),
								descripcionProyectoBean.getListaActividades(),
								descripcionProyectoBean.getListaActividadesEliminadas(),
								descripcionProyectoBean.getCoordenadasGeneralEliminadas(),
								descripcionProyectoBean.getListaActividadesPorEtapas(),
								descripcionProyectoBean.getListaActividadesPorEtapasEliminadas(),
								descripcionProyectoBean.getSustanciaQuimicaEiaEliminadas(),
								this.existeNuevoDocumento1,
								this.existeNuevoDocumento2,
								calendarioEtapasEliminadas, esMineriaNoMetalicos);
			}
			validacionSeccionesFacade.guardarValidacionSeccion("EIA", "descripcionProyectoObraActividad",
					descripcionProyectoBean.getEstudioAmbiental().getId().toString());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/descripcionObraActividad/descripcionProyectoObraActividad.jsf");
			
			existeNuevoDocumento1 = false;
			existeNuevoDocumento2 = false;
			
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			LOG.error("Error al guardar descripcion del proyecto en estudio de impacto ambiental", e);
		}
	}

	public Boolean existeSustanciaQuimica(SustanciaQuimicaEia sustancia) {
		for (SustanciaQuimicaEia sustanciaQuimicaEia : descripcionProyectoBean.getSustanciaQuimicaEiaLista()) {
			if (sustanciaQuimicaEia.getSustanciaQuimicaPeligrosa().getId()
					.equals(sustancia.getSustanciaQuimicaPeligrosa().getId()))
				return true;
		}
		return false;
	}

	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/descripcionObraActividad/descripcionProyectoObraActividad.jsf");
	}

	/**
	 * Método para agregar una actividad de licencimiento
	 */
	public void agregarActividadLicenciamiento() {
		if (!getDescripcionProyectoBean().getListaActividades().contains(
				getDescripcionProyectoBean().getActividadLicenciamiento())) {
			descripcionProyectoBean.getActividadLicenciamiento().setCoordenadasGeneral(
					descripcionProyectoBean.getListaCoordenadasGenerales());
			descripcionProyectoBean.getListaActividades().add(descripcionProyectoBean.getActividadLicenciamiento());
			CronogramaFasesProyectoEia cronogramaFasesProyecto = new CronogramaFasesProyectoEia();
			cronogramaFasesProyecto.setCatalogoCategoriaFase(descripcionProyectoBean.getActividadLicenciamiento()
					.getCatalogoCategoriaFase());
			String nombre = descripcionProyectoBean.getActividadLicenciamiento().getCatalogoCategoriaFase().getFase()
					.getNombre();
			if (calendario.isEmpty())
				calendario.add(cronogramaFasesProyecto);
			else {
				boolean nuevo = true;
				for (CronogramaFasesProyectoEia cf : calendario) {
					if (nombre.equals(cf.getCatalogoCategoriaFase().getFase().getNombre())) {
						nuevo = false;
						break;
					}
				}
				if (nuevo) {
					calendario.add(cronogramaFasesProyecto);
				}
			}
			descripcionProyectoBean.setActividadLicenciamiento(null);
			descripcionProyectoBean.setListaCoordenadasGenerales(new ArrayList<CoordenadaGeneral>());
		}
		JsfUtil.addCallbackParam("addActividadLicenciamiento");
	}

	public void agregarActividadPorEtapa(){
		if (!getDescripcionProyectoBean().getListaActividadesPorEtapas().contains(
				getDescripcionProyectoBean().getActividadesPorEtapa())) {
			descripcionProyectoBean.getListaActividadesPorEtapas().add(descripcionProyectoBean.getActividadesPorEtapa());
			descripcionProyectoBean.setActividadesPorEtapa(null);
		}
		JsfUtil.addCallbackParam("addActividadEtapa");
	}

	/**
	 * Método para eliminar una actividad de licenciamiento
	 * 
	 * @param actividad
	 */
	public void eliminarActividadLicenciameinto(ActividadLicenciamiento actividad) {
		if(actividad.getId() != null){
			descripcionProyectoBean.getListaActividadesEliminadas().add(actividad);
			if(actividad.getCoordenadasGeneral() != null)
				getDescripcionProyectoBean().getCoordenadasGeneralEliminadas().addAll(actividad.getCoordenadasGeneral());
		}
		
		int i = 0;
		boolean isDeleted = false;
		for (ActividadLicenciamiento al : descripcionProyectoBean.getListaActividades()) {
			if (al.getCatalogoCategoriaFase().getFase().getNombre()
					.equals(actividad.getCatalogoCategoriaFase().getFase().getNombre())) {
				i++;
				if (i > 1) {
					descripcionProyectoBean.getListaActividades().remove(actividad);
					isDeleted = true;
					break;
				}
			}
		}
		if (!isDeleted) {
			descripcionProyectoBean.getListaActividades().remove(actividad);
			
			for (CronogramaFasesProyectoEia cfp : calendario) {
				if (cfp.getCatalogoCategoriaFase().getFase().getNombre()
						.equals(actividad.getCatalogoCategoriaFase().getFase().getNombre())) {
					if(actividad.getId() != null){
						calendarioEtapasEliminadas.add(cfp);
					}					
					calendario.remove(cfp);
					break;
				}
			}		
					
		}
	}

	public void eliminarActividadPorEtapa(ActividadesPorEtapa actividadesPorEtapa){
		descripcionProyectoBean.getListaActividadesPorEtapasEliminadas().add(actividadesPorEtapa);
		if(actividadesPorEtapa.getId() != null)
			descripcionProyectoBean.getListaActividadesPorEtapas().remove(actividadesPorEtapa);
	}

	/**
	 * Seleccionar una actividad de licenciamiento
	 * 
	 * @param actividad
	 */
	public void seleccionarActividadLicenciamiento(ActividadLicenciamiento actividad) {
		getDescripcionProyectoBean().setActividadLicenciamiento(actividad);
		getDescripcionProyectoBean().setListaCoordenadasGenerales(
				getDescripcionProyectoBean().getActividadLicenciamiento().getCoordenadasGeneral());
		
		//MarielaG para buscar informacion original
		if (existeObservaciones) {
//			if (!promotor.equals(loginBean.getNombreUsuario())) {
				cargarActividadOriginal(actividad);
				consultarCoordenadasOriginales(actividad.getId());
//			}
		}
	}

	public void seleccionarActividadEtapa(ActividadesPorEtapa actividad) {
		getDescripcionProyectoBean().setActividadesPorEtapa(actividad);
	}

	/**
	 * Limpiar actividad de licenciamiento
	 */
	public void limpiarActividadLicenciamiento() {
		getDescripcionProyectoBean().setActividadLicenciamiento(new ActividadLicenciamiento());
		getDescripcionProyectoBean().setListaCoordenadasGenerales(new ArrayList<CoordenadaGeneral>());
	}

	public void limpiarActividadEtapas() {
		getDescripcionProyectoBean().setActividadesPorEtapa(new ActividadesPorEtapa());
	}

	/**
	 * Método para obtener el catálogo de fases correspondientes al sector al
	 * que pertenece el proyecto
	 * 
	 * @return
	 */
	public List<CatalogoCategoriaFase> cargarFases() {
		try {
			// EstudioImpactoAmbiental estudio = getDescripcionProyectoBean()
			// .getEstudioAmbiental();
			// ProyectoLicenciamientoAmbiental proyecto = estudio
			// .getProyectoLicenciamientoAmbiental();
			return faseFichaAmbientalFacade.obtenerCatalogoCategoriaFasesLA();
		} catch (Exception e) {
			LOG.info("", e);
			return null;
		}
	}

	/**
	 * Método para agregar coordenadas
	 */
	public void agregarCoordenada() {
		if (!getDescripcionProyectoBean().getListaCoordenadasGenerales().contains(
				getDescripcionProyectoBean().getCoordenadaGeneral())) {
			getDescripcionProyectoBean().getListaCoordenadasGenerales().add(
					getDescripcionProyectoBean().getCoordenadaGeneral());
			getDescripcionProyectoBean().setCoordenadaGeneral(null);
		}
		JsfUtil.addCallbackParam("addCordenadas");
	}

	/**
	 * Método para limpiar coordenada general
	 */
	public void limpiarCoordenada() {
		seleccionarCoordenada(new CoordenadaGeneral());
	}

	/**
	 * Método para seleccionar una coordenada
	 * 
	 * @param coordenadaGeneral
	 */
	public void seleccionarCoordenada(CoordenadaGeneral coordenadaGeneral) {
		getDescripcionProyectoBean().setCoordenadaGeneral(coordenadaGeneral);
	}

	/**
	 * Eliminar coordenada general
	 * 
	 * @param coordenadaGeneral
	 */
	public void eliminarCoordenada(CoordenadaGeneral coordenadaGeneral) {
		getDescripcionProyectoBean().getListaCoordenadasGenerales().remove(coordenadaGeneral);
		getDescripcionProyectoBean().getCoordenadasGeneralEliminadas().add(coordenadaGeneral);
	}

	/**
	 * Cargar actividades de licenciamiento
	 *
	 * @param estudio
	 */
	public void cargarActividadesLicenciamiento(EstudioImpactoAmbiental estudio) {
		try {
			coordenadaGeneralesEnBase = new ArrayList<>();
			List<ActividadLicenciamiento> actividadesLicenciamiento = actividadLicenciamientoFacade.actividadesLicenciamiento(estudio);
			if (!actividadesLicenciamiento.isEmpty() && actividadesLicenciamiento != null) {
				/*MARIELAG 22-03-2018
				//se quita codigo porque realiza la consulta de las coordenadas con el id del estudio 
				//cuando las coordenadas se relacionan con las actividades
				//setea la listaCoordenadasGenerales la misma que es seteada cuando de abre el popup de la actividad 
				 
				for (ActividadLicenciamiento actividadLicenciamiento : actividadesLicenciamiento) {
					if (!actividadLicenciamiento.getCoordenadasGeneral().isEmpty()) {
						List<CoordenadaGeneral> coordenadaGeneralesEnBase = coordenadaGeneralFacade.coordenadasGeneralXTablaId(estudio.getId(),CoordenadaGeneral.ACTIVITY_LICENSING_TABLE_CLASS);						
						getDescripcionProyectoBean().setListaCoordenadasGenerales(coordenadaGeneralesEnBase);
						break;
					}
				}
				 */
				//c.idHistorico = null
				for (ActividadLicenciamiento actividadLicenciamiento : actividadesLicenciamiento) {
					if (!actividadLicenciamiento.getCoordenadasGeneral().isEmpty()) {
						List<CoordenadaGeneral> coordenadasEnBase = coordenadaGeneralFacade.buscarCoordenadasPorActividadEnBdd(actividadLicenciamiento.getId());
						List<CoordenadaGeneral> coordenadasActividad = new ArrayList<>();
						for(CoordenadaGeneral coordenada : coordenadasEnBase){
							if(coordenada.getIdHistorico() == null)
								coordenadasActividad.add(coordenada);
						}
						actividadLicenciamiento.setCoordenadasGeneral(coordenadasActividad);
						coordenadaGeneralesEnBase.addAll(coordenadasEnBase);
					}
				}
				
				getDescripcionProyectoBean().setListaActividades(actividadesLicenciamiento);
				
				if (numeroNotificaciones > 0) {
//					if (!promotor.equals(loginBean.getNombreUsuario())) {
						buscarCambiosActividades();
//					}
				}
			}
		} catch (Exception e) {
			LOG.info("Error al cargar actividades de licenciamiento", e);
		}
	}

	public void fileUploadListenerDescripcion(FileUploadEvent event) {
		this.existeNuevoDocumento1 = true;
		file = event.getFile();
		estudio.setDocumentoDescripcion(UtilDocumento.generateDocumentPDFFromUpload(file.getContents(),
				file.getFileName()));
		descripcionProyectoBean.setNombreDocumentoDescripcion(estudio.getDocumentoDescripcion().getNombre());
	}

	public void fileUploadListenerInsumos(FileUploadEvent event) {
		this.existeNuevoDocumento2 = true;
		file = event.getFile();
		estudio.setDocumentoInsumos(UtilDocumento.generateDocumentPDFFromUpload(file.getContents(), file.getFileName()));
		descripcionProyectoBean.setNombreDocumentoInsumos(estudio.getDocumentoInsumos().getNombre());
	}

	public void fileUploadListenerCoordenada(FileUploadEvent event) {
		file = event.getFile();
		descripcionProyectoBean.getActividadesPorEtapa().setCoordenadaActividad(UtilDocumento.generateDocumentPDFFromUpload(file.getContents(), file.getFileName()));
		descripcionProyectoBean.setNombreDocumentoCoordenada(estudio.getDocumentoInsumos().getNombre());
	}

	public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}

	public Date getCurrentDate() {
		return new Date();
	}
	
	/**
	 * Cristina F: Cargar los documentos para historico
	 */	
	
	private boolean validarGuardarHistoricoEA(){		
		try {
			List<EstudioImpactoAmbiental> lista = estudioImpactoAmbientalFacade.busquedaRegistrosHistorico(estudio.getId());
		
			/**
			 * Si el tamaño de la lista es el mismo que el número de notificiaciones entonces no se guarda
			 * Si la lista es nula entonces se guarda
			 * Si el tamaño de la lista es menor que el número de notificaciones se guarda
			 */
			
			if(lista != null && !lista.isEmpty()){
				if(numeroNotificaciones > lista.size())
					return true; // se guarda
				else
					return false;  //no se guarda
			}else {
				return true; // se guarda
			}				
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	private void consultarRegistroAnterior(){
		try {
			estudioHistorico = estudio.clone();
			estudioHistorico.setFechaModificacion(null);
			estudioHistorico.setIdHistorico(estudio.getId());	
			if(estudio.getDocumentoInsumos().getId() == null)
				estudioHistorico.setDocumentoInsumos(null);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * MarielaG
	 * Consultar el estudio ingresado antes de las correcciones
	 */
	private void consultarEstudioOriginal() {
		try {
			listaDocumentoDescripcionHistorico = new ArrayList<>();
			listaDocumentoInsumoHistorico = new ArrayList<>();
			
			List<EstudioImpactoAmbiental> lista = estudioImpactoAmbientalFacade
					.busquedaRegistrosHistorico(estudio.getId());
			if (lista != null && !lista.isEmpty()) {
				
				for(EstudioImpactoAmbiental estudioHistorial : lista){
					Documento documentoDescripcionHistorial = documentosFacade.buscarDocumentoPorId(estudioHistorial.getIdDocumentoDescripcion());
					Documento documentoDescripcion = documentosFacade.buscarDocumentoPorId(estudio.getIdDocumentoDescripcion());
					
					if(!documentoDescripcionHistorial.getId().equals(documentoDescripcion.getId())){
						documentoDescripcionHistorial.setNumeroNotificacion(estudioHistorial.getNumeroNotificacion());
						if(!listaDocumentoDescripcionHistorico.contains(documentoDescripcionHistorial)){
							listaDocumentoDescripcionHistorico.add(0, documentoDescripcionHistorial);
						}
					}
					
					Documento documentoInsumosHistorial = documentosFacade.buscarDocumentoPorId(estudioHistorial.getIdDocumentoInsumos());
					Documento documentoInsumos = documentosFacade.buscarDocumentoPorId(estudio.getIdDocumentoInsumos());
					
					if(documentoInsumosHistorial != null && 
							!documentoInsumosHistorial.getId().equals(documentoInsumos.getId())){
						documentoInsumosHistorial.setNumeroNotificacion(estudioHistorial.getNumeroNotificacion());
						if(!listaDocumentoInsumoHistorico.contains(documentoInsumosHistorial)){
							listaDocumentoInsumoHistorico.add(0, documentoInsumosHistorial);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Buscar si las actividades seleccionadas tienen cambios en su informacion como en las coordenadas para cambiar color del icono en tabla
	 */
	public boolean buscarCambiosActividades() {
		try {
			listaActividadesModificadas = new ArrayList<>();

			List<ActividadLicenciamiento> actividadesSeleccionadas = getDescripcionProyectoBean().getListaActividades();

			List<ActividadLicenciamiento> actividadesModificadas = actividadLicenciamientoFacade.buscarActividadesModificadas(estudio.getId());
			listaActividadesModificadas = actividadesModificadas;

			if (!actividadesModificadas.isEmpty() && actividadesModificadas != null) {
				for (ActividadLicenciamiento actividad : actividadesSeleccionadas) {
					//si es un registro que se ingreso originalmente
					//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
					if (actividad.getNumeroNotificacion() == null ||
							!actividad.getNumeroNotificacion().equals(numeroNotificaciones)) {
						for (ActividadLicenciamiento actividadMod : actividadesModificadas) {
							if (actividadMod.getIdHistorico() != null
									&& actividadMod.getIdHistorico().equals(actividad.getId())) {
								actividad.setActividadModificada(true);
								break;
							}
						}
					} else {
						if (actividad.getIdHistorico() == null && actividad.getNumeroNotificacion().equals(numeroNotificaciones)) {
							// nuevo registro ingresado durante la modificacion
							actividad.setActividadModificada(true);
							actividad.setNuevoRegistro(true);
						} else {
							actividad.setActividadModificada(true);
						}
					}
				}
			}

		} catch (Exception e) {
			LOG.info("Error al cargar consultor no calificado.", e);
		}
		return false;
	}
	
	/**
	 * MarielaG
	 * Consultar el listado de actividades originales
	 * @throws ServiceException 
	 */
	private void cargarActividadesOriginales() throws ServiceException{
		List<ActividadLicenciamiento> actividadesOriginales = new ArrayList<>();
		List<ActividadLicenciamiento> actividadesEliminados = new ArrayList<>();
		
		int totalActividadesModificados = 0;
		List<ActividadLicenciamiento> actividadesBdd = listaActividadesModificadas;
		for(ActividadLicenciamiento actividadBdd : actividadesBdd){
			if(actividadBdd.getNumeroNotificacion() == null || 
					!actividadBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
    			boolean agregarItemLista = true;
    			//buscar si tiene historial
				for (ActividadLicenciamiento puntoHistorico : actividadesBdd) {
					if (puntoHistorico.getIdHistorico() != null  
							&& puntoHistorico.getIdHistorico().equals(actividadBdd.getId())) {
						//si existe un registro historico, no se agrega a la lista en este paso
						agregarItemLista = false;
						actividadBdd.setRegistroModificado(true);
						break;
					}
				}
				if (agregarItemLista) {
					actividadesOriginales.add(actividadBdd);
				}
			} else {
				totalActividadesModificados++;
    			//es una modificacion
    			if(actividadBdd.getIdHistorico() == null && actividadBdd.getNumeroNotificacion() == numeroNotificaciones){
    				//es un registro nuevo
    				//no ingresa en el lista de originales
    				actividadBdd.setNuevoEnModificacion(true);
    			}else{
    				actividadBdd.setRegistroModificado(true);
    				if(!actividadesOriginales.contains(actividadBdd)){
    					actividadesOriginales.add(actividadBdd);
    				}
    			}
    		}
			
			//para consultar eliminados
			if (actividadBdd.getIdHistorico() != null
					&& actividadBdd.getNumeroNotificacion() != null) {
				boolean existePunto = false;
				for (ActividadLicenciamiento itemActual : getDescripcionProyectoBean().getListaActividades()) {
					if (itemActual.getId().equals(actividadBdd.getIdHistorico())) {
						existePunto = true;
						break;
					}
				}

				if (!existePunto) {
					String nombreTabla = ActividadLicenciamiento.class.getSimpleName().substring(0,1).toLowerCase()
					        + ActividadLicenciamiento.class.getSimpleName().substring(1);
					List<CoordenadaGeneral> coordenadasActividad = coordenadaGeneralFacade.listarCoordenadaGeneralEnBdd(
							nombreTabla, actividadBdd.getIdHistorico());
					actividadBdd.setCoordenadasGeneral(coordenadasActividad);
					actividadesEliminados.add(actividadBdd);
				}
			}
		}
		
		if (totalActividadesModificados > 0)
			listaActividadesOriginales = actividadesOriginales;
		
		listaActividadesEliminadas = actividadesEliminados;
	}
	/**
	 * MarielaG
	 * Consultar las coordenadas originales por actividad 
	 */
	public void consultarCoordenadasOriginales(int idActividad) {
		List<CoordenadaGeneral> listaCoordenadasOriginales = new ArrayList<>();
		List<CoordenadaGeneral> coordenadasEliminadas = new ArrayList<>();
		
		listaCoordenadasOriginal = new ArrayList<>();
		listaCoordenadasEliminadasBdd = new ArrayList<>();

		int totalCoordenadasModificadas = 0;

		for (CoordenadaGeneral coordenada : coordenadaGeneralesEnBase) {
			if(coordenada.getActividadLicenciamiento().getId().equals(idActividad)){
				// si es un registro que se ingreso originalmente
				// o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
				if (coordenada.getNumeroNotificacion() == null
						|| !coordenada.getNumeroNotificacion().equals(numeroNotificaciones)) {
					boolean agregarItemLista = true;
					// validar si el registro seleccionado ha sido modificado (si
					// tiene un registrohistorico)
					// para no ingresarlo en la lista de originales
					for (CoordenadaGeneral coordenadaModificada : coordenadaGeneralesEnBase) {
						if (coordenadaModificada.getIdHistorico() != null
								&& coordenadaModificada.getIdHistorico().equals(coordenada.getId())) {
							agregarItemLista = false;
							coordenada.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						listaCoordenadasOriginales.add(coordenada);
					}
				} else {
					totalCoordenadasModificadas++;
					// es una modificacion
					if (coordenada.getIdHistorico() == null && coordenada.getNumeroNotificacion() == numeroNotificaciones) {
						// es un registro nuevo
						// no ingresa en el lista de originales
						coordenada.setNuevoEnModificacion(true);
					} else {
						coordenada.setRegistroModificado(true);
						if (!listaCoordenadasOriginales.contains(coordenada)) {
							listaCoordenadasOriginales.add(coordenada);
						}
					}
				}
	
				// para consultar eliminados
				if (coordenada.getIdHistorico() != null
						&& coordenada.getNumeroNotificacion() != null) {
					boolean existePunto = false;
					for (CoordenadaGeneral itemActual : descripcionProyectoBean.getListaCoordenadasGenerales()) {
						if (itemActual.getId().equals(coordenada.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}
	
					if (!existePunto) {
						coordenadasEliminadas.add(coordenada);
					}
				}
			}
		}

		if (totalCoordenadasModificadas > 0) {
			listaCoordenadasOriginal = listaCoordenadasOriginales;
		}

		listaCoordenadasEliminadasBdd = coordenadasEliminadas;

	}
	/**
	 * MarielaG
	 * Consultar la actividad original de la actividad seleccionada para ver informacion 
	 */
	private void cargarActividadOriginal(ActividadLicenciamiento actividadLicenciamiento){
		List<ActividadLicenciamiento> listaActividades = new ArrayList<>();
		listaActividadesHistorico = new ArrayList<>();
		
		//si es un registro que se ingreso originalmente
		//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
		if (actividadLicenciamiento.getNumeroNotificacion() == null ||
				!actividadLicenciamiento.getNumeroNotificacion().equals(numeroNotificaciones)) {
			for (ActividadLicenciamiento actividadOriginal : listaActividadesOriginales) {
				if (actividadOriginal.getIdHistorico() != null
						&& actividadOriginal.getIdHistorico().equals(actividadLicenciamiento.getId())) {

					if (!actividadLicenciamiento.getCatalogoCategoriaFase().getId().equals(actividadOriginal.getCatalogoCategoriaFase().getId()) ||
							!actividadLicenciamiento.getNombreActividad().equals(actividadOriginal.getNombreActividad())) {
						listaActividades.add(actividadOriginal);
					}
				}
			}
			
			listaActividadesHistorico = listaActividades;
		} else {
			if (actividadLicenciamiento.getIdHistorico() == null && actividadLicenciamiento.getNumeroNotificacion() == numeroNotificaciones) {
				// punto nuevo no tiene original
			}
		}
	}
	/**
	 * MarielaG
	 * Consultar las sustancias originales
	 */
	private void cargarSustanciasOriginales() {
		try {
			List<SustanciaQuimicaEia> listaSustanciasBdd = descripcionProyectoBean.getSustanciaQuimicaEiaListaBdd();
			
			List<SustanciaQuimicaPeligrosa> sustanciasEliminadas = new ArrayList<>();
			int totalModificados = 0;
			
			
			for(SustanciaQuimicaEia sustanciaBdd : listaSustanciasBdd){
				//si es un registro que se ingreso originalmente
				//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
				if(sustanciaBdd.getNumeroNotificacion() == null ||
						!sustanciaBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
	    			//buscar si tiene historial
					for (SustanciaQuimicaEia sustanciaHistorico : listaSustanciasBdd) {
						if (sustanciaHistorico.getIdHistorico() != null  
								&& sustanciaHistorico.getIdHistorico().equals(sustanciaBdd.getId())) {
							//si existe un registro historico, no se agrega a la lista en este paso
							sustanciaBdd.getSustanciaQuimicaPeligrosa().setRegistroModificado(true);
							sustanciaBdd.getSustanciaQuimicaPeligrosa().setFechaCreacion(sustanciaBdd.getFechaCreacion());
							break;
						}
					}
				} else {
					totalModificados++;
	    			//es una modificacion
	    			if(sustanciaBdd.getIdHistorico() == null && sustanciaBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
	    				//es un registro nuevo
	    				//no ingresa en el lista de originales
	    				sustanciaBdd.getSustanciaQuimicaPeligrosa().setNuevoEnModificacion(true);
	    				sustanciaBdd.getSustanciaQuimicaPeligrosa().setFechaCreacion(sustanciaBdd.getFechaCreacion());
	    			}else{
	    				sustanciaBdd.getSustanciaQuimicaPeligrosa().setRegistroModificado(true);
	    				sustanciaBdd.getSustanciaQuimicaPeligrosa().setFechaCreacion(sustanciaBdd.getFechaCreacion());
	    			}
	    		}
				
				//para consultar eliminados
				if (sustanciaBdd.getIdHistorico() != null
						&& sustanciaBdd.getNumeroNotificacion() != null) {
					boolean existePunto = false;
					for (SustanciaQuimicaEia itemActual : descripcionProyectoBean.getSustanciaQuimicaEiaLista()) {
						if (itemActual.getId().equals(sustanciaBdd.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto) {
						sustanciaBdd.getSustanciaQuimicaPeligrosa().setNumeroNotificacion(sustanciaBdd.getNumeroNotificacion());
						sustanciaBdd.getSustanciaQuimicaPeligrosa().setFechaCreacion(sustanciaBdd.getFechaCreacion());
						sustanciasEliminadas.add(0, sustanciaBdd.getSustanciaQuimicaPeligrosa());
					}
				}
			}

				descripcionProyectoBean.setSustanciasModificadas(totalModificados);
				descripcionProyectoBean.setSustanciasQuimicasHistorico(sustanciasEliminadas);
				
		} catch (Exception e) {
			LOG.info("Error al cargar sustancias quimicas originales.", e);
		}
	}

	/**
	 * MarielaG
	 * Consultar los cronogramas originales
	 */
	public void cargarCronogramaOriginal(){
		try {
			List<CronogramaFasesProyectoEia> cronogramaOriginal = new ArrayList<CronogramaFasesProyectoEia>();
			List<CronogramaFasesProyectoEia> cronogramasEliminados = new ArrayList<>();
			int totalModificados = 0;
			
			for(CronogramaFasesProyectoEia cronogramaBdd : calendarioEnBdd){
				//si es un registro que se ingreso originalmente
				//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
				if(cronogramaBdd.getNumeroNotificacion() == null ||
						!cronogramaBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
					boolean agregarItemLista = true;
	    			//buscar si tiene historial
					for (CronogramaFasesProyectoEia planHistorico : calendarioEnBdd) {
						if (planHistorico.getIdHistorico() != null  
								&& planHistorico.getIdHistorico().equals(cronogramaBdd.getId())) {
							//si existe un registro historico, no se agrega a la lista en este paso
							agregarItemLista = false;
							cronogramaBdd.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						cronogramaOriginal.add(cronogramaBdd);
					}
				} else {
					totalModificados++;
	    			//es una modificacion
	    			if(cronogramaBdd.getIdHistorico() == null && cronogramaBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
	    				//es un registro nuevo
	    				//no ingresa en el lista de originales
	    				cronogramaBdd.setNuevoEnModificacion(true);
	    			}else{
	    				cronogramaBdd.setRegistroModificado(true);
	    				if(!cronogramaOriginal.contains(cronogramaBdd)){
	    					cronogramaOriginal.add(cronogramaBdd);
	    				}
	    			}
	    		}
				
				//para consultar eliminados
				if (cronogramaBdd.getIdHistorico() != null
						&& cronogramaBdd.getNumeroNotificacion() != null) {
					boolean existePunto = false;
					for (CronogramaFasesProyectoEia itemActual : calendario) {
						if (itemActual.getId().equals(cronogramaBdd.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto) {
						cronogramasEliminados.add(cronogramaBdd);
					}
				}
			}

			if (totalModificados > 0)
				calendarioOriginal = cronogramaOriginal;

			calendarioEliminadoEnBdd = cronogramasEliminados;

		} catch (Exception e) {
			LOG.info("Error al cargar cronograma original.", e);
		}
	}
	
	/**
	 * MarielaG para mostrar el historial de los documentos
	 */
	public void fillHistorialDocumentos(Integer tipoDocumento) {
		listaDocumentosHistorico = new ArrayList<>();
		
		switch (tipoDocumento) {
			case 0:
				listaDocumentosHistorico = listaDocumentoDescripcionHistorico;
				break;
			case 1:
				listaDocumentosHistorico = listaDocumentoInsumoHistorico;
				break;
		}
	}
	
	/**
	 * MarielaG
	 * Para descargar documentos originales
	 */
	public StreamedContent descargarOriginal(Documento documento) throws Exception {
        DefaultStreamedContent content = null;
        try {
			Documento documentoOriginal = this.descargarAlfresco(this.descargarAlfresco(documento));
            if (documentoOriginal != null && documentoOriginal.getNombre() != null && documentoOriginal.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documentoOriginal.getContenidoDocumento()));
                content.setName(documentoOriginal.getNombre());
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
	
	/**
	 * MarielaG
	 * Para mostrar el historial de cambios del cronograma
	 */
	public void mostrarCronogramasOriginales(CronogramaFasesProyectoEia cronograma) {
		listaCalendarioHistorial = new ArrayList<>();
		
		for(CronogramaFasesProyectoEia cronogramaOriginal : calendarioOriginal){
			if(cronogramaOriginal.getIdHistorico() != null && cronograma.getId().equals(cronogramaOriginal.getIdHistorico())){
				listaCalendarioHistorial.add(0, cronogramaOriginal);
			}
		}
	}
	
	/**
	 * MarielaG Para mostrar los cronogramas eliminados
	 */
	public void fillCronogramasEliminados() {
		listaCalendarioHistorial = calendarioEliminadoEnBdd;
	}
	
	/**
	 * MarielaG
	 * Para mostrar el historial de cambios de coordenadas
	 */
	public void mostrarCoordenadaHistorial(CoordenadaGeneral coordenada) {
		List<CoordenadaGeneral> listaCoordenadasHistorial = new ArrayList<>();
		
		for(CoordenadaGeneral coordenadaOriginal : listaCoordenadasOriginal){
			if(coordenadaOriginal.getIdHistorico() != null && coordenada.getId().equals(coordenadaOriginal.getIdHistorico())){
				listaCoordenadasHistorial.add(coordenadaOriginal);
			}
		}
		
		listaCoordenadasHistorico = listaCoordenadasHistorial;
	}
	
	/**
	 * MarielaG Para mostrar las coordenadas eliminadas
	 */
	public void fillCoordenadasEliminadas() {
		listaCoordenadasHistorico = listaCoordenadasEliminadasBdd;
	}	
}