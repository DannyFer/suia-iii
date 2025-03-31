package ec.gob.ambiente.suia.eia.identificacionHallazgos.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.AnalisisRiesgoEia;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.IdentificacionHallazgosEia;
import ec.gob.ambiente.suia.domain.enums.TipoConformidad;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.bean.AnalisisRiesgoEIABean;
import ec.gob.ambiente.suia.eia.identificacionHallazgos.facade.IdentificacionHallazgosEiaFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * @author Oscar Campana
 */
@ManagedBean
@ViewScoped
public class IdentificacionHallazgosEIAController implements Serializable {

	private static final long serialVersionUID = 1572525482381028668L;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(IdentificacionHallazgosEIAController.class);

	// public static final String OTROS="Otros";


	@EJB
	private ValidacionSeccionesFacade validacionesSeccionesFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{analisisRiesgoEIABean}")
	private AnalisisRiesgoEIABean analisisRiesgoEIABean;

	@Getter
	@Setter
	private Documento documentoGeneral, documentoHistorico, documentoGeneralOriginal;

	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@EJB
	private DocumentosFacade documentosFacade;
	
	private boolean adjuntoDocumento;
	

	/*************************/
	// ///////////

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	IdentificacionHallazgosEia identificacionHallazgosEia;

	@Getter
	@Setter
	List<IdentificacionHallazgosEia> listaIdentificacionHallazgos, listaHallazgosOriginales, listaHallazgosEliminadosBdd, listaHallazgosHistorico;

	@Getter
	@Setter
	List<IdentificacionHallazgosEia> listaIdentificacionHallazgosEliminados;

	@EJB
	private IdentificacionHallazgosEiaFacade identificacionHallazgosEiaFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

	@Setter
	@Getter
	private TipoConformidad[] tiposConformidad = TipoConformidad.values();

	@Setter
	@Getter
	private String criterioBusquedaArticulo;

	@Setter
	@Getter
	private boolean isEditing;

	// //******************/////////////////////////
	
	private Map<String, Object> processVariables;
	private Integer numeroNotificaciones;
	private boolean existeObservaciones;
	private String promotor;
	
	@Getter
	@Setter
	private Integer totalHallazgosModificados;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentoGeneralHistorico;

	@PostConstruct
	private void postInit() throws CmisAlfrescoException, JbpmException {
		
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
//		promotor = (String) processVariables.get("u_Promotor");
		
		if(numeroNotificaciones > 0)
			existeObservaciones = true;
		//Fin CF	
		
		estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
		cargarDatos();

	}

	private void cargarDatos() throws CmisAlfrescoException {

		this.isEditing = false;
		this.adjuntoDocumento=false;
		identificacionHallazgosEia = new IdentificacionHallazgosEia();
		estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
		listaIdentificacionHallazgos = new ArrayList<IdentificacionHallazgosEia>();
		listaIdentificacionHallazgosEliminados = new ArrayList<IdentificacionHallazgosEia>();

		//this.normativas = this.obtenerNormativaLicenciamiento();
		this.cargarAdjuntosEIA(TipoDocumentoSistema.IDENTIFICACION_HALLAZGOS);
		
		try {
			List<IdentificacionHallazgosEia> listaIdentificacionHallazgosEia = new ArrayList<IdentificacionHallazgosEia>();
			listaIdentificacionHallazgosEia = identificacionHallazgosEiaFacade.listarTodosRegistrosPorEIA(estudioImpactoAmbiental);
			for (IdentificacionHallazgosEia identificacionHallazgosEia : listaIdentificacionHallazgosEia) {
				if(identificacionHallazgosEia.getIdHistorico() == null){
					this.listaIdentificacionHallazgos.add(identificacionHallazgosEia); 
				}
			}
			
			//MarielaG para buscar informacion original
			if (existeObservaciones) {
//				if (!promotor.equals(loginBean.getNombreUsuario())) {
					consultarHallazgosOriginales(listaIdentificacionHallazgosEia);
//				}
			}

		} catch (Exception e) {
			LOG.error(e, e);
		}
	}


	/*
	public void buscarArticulos() {
		this.articulos = this.identificacionHallazgosEiaFacade.getArticulosNormativa(this.identificacionHallazgosEia
				.getNormativa().getId(), criterioBusquedaArticulo);

	}
	*/

	/**
	 * Guarda la lista de Análisis de Riesgo ingresadas y el documento al Alfresco
	 * 
	 * @throws CmisAlfrescoException
	 */
	public void guardar() throws CmisAlfrescoException {
		try {
			
			if(existeObservaciones){
				identificacionHallazgosEiaFacade.guardarHistorico(listaIdentificacionHallazgos,
						listaIdentificacionHallazgosEliminados, numeroNotificaciones);
			}else{
				identificacionHallazgosEiaFacade.guardar(listaIdentificacionHallazgos,
						listaIdentificacionHallazgosEliminados);
			}
			
			if(this.adjuntoDocumento){
				if(existeObservaciones){
					salvarDocumentoHistorico();
				}else{
					this.salvarDocumento();
				}
			}
			cargarDatos();
			if (this.listaIdentificacionHallazgos.size() > 0) {
				this.actualizarEstadoValidacionSeccion();
			}
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/identificacionHallazgos/identificacionHallazgos.jsf?id=9&amp;faces-redirect=true");
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
		} catch (RuntimeException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
		}

	}

	/**
	 * Asigna la sección válida
	 * 
	 * @throws ServiceException
	 * @throws CmisAlfrescoException
	 */
	private void actualizarEstadoValidacionSeccion() throws ServiceException, CmisAlfrescoException {
		validacionesSeccionesFacade.guardarValidacionSeccion("EIA", "identificacionHallazgos", estudioImpactoAmbiental
				.getId().toString());

	}

//	/**
//	 * Indica si ya se agregado por lo menos un Análisis de Riesgo a la tabla
//	 * 
//	 * @return
//	 */
//	private boolean tienePorLoMenosUnAnalisis() {
//		if (getAnalisisRiesgoEIABean().getListaAnalisisRiesgoEia().size() > 0) {
//			return true;
//		} else {
//			return false;
//		}
//	}

	/**
	 * Acción del botón cancelar.
	 */
	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/analisisRiesgo/analisisRiesgo.jsf");
	}

	/**
	 * Prepara la edición de un hallazgo
	 */
	public void editarHallazgo(IdentificacionHallazgosEia hallazgo) {

		this.identificacionHallazgosEia = hallazgo;
		this.isEditing = true;

		/*
		 * this.identificacionHallazgosEia.setArticulo(this.identificacionHallazgosEia.getArticulo());
		 * this.identificacionHallazgosEia.setConformidad(this.identificacionHallazgosEia.getConformidad());
		 * this.identificacionHallazgosEia.setEistId(this.identificacionHallazgosEia.getEistId());
		 * this.identificacionHallazgosEia.setEstado(this.identificacionHallazgosEia.getEstado());
		 * this.identificacionHallazgosEia.setEvidencia(this.identificacionHallazgosEia.getEvidencia());
		 * this.identificacionHallazgosEia.setId(this.identificacionHallazgosEia.getId());
		 * this.identificacionHallazgosEia.setNormativa(this.identificacionHallazgosEia.getNormativa());
		 * //this.identificacionHallazgosEia.setSeleccionado(this.identificacionHallazgosEia.get); //
		 * cargarDatosEdicion();
		 */

	}

	/**
	 * Asigna un articulo al Hallazgo
	 * 
	 * @param analisisDeRiesgo
	 */
	/*
	public void asignarArticulo(ArticuloNormativa articulo) {

		this.identificacionHallazgosEia.setArticulo(articulo);
		JsfUtil.addCallbackParam("buscar");
		// cargarDatosEdicion();
	}
	*/

	/**
	 * Inicialización de variables del Análisis de Riesgo
	 */
	public void inicializarAnalisis() {
		getAnalisisRiesgoEIABean().setAnalisisActivo(new AnalisisRiesgoEia());
		EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil
				.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
		getAnalisisRiesgoEIABean().getAnalisisActivo().setEistId(es);
		getAnalisisRiesgoEIABean().setSubtipo(null);
		getAnalisisRiesgoEIABean().setTipoRiesgo(null);
		getAnalisisRiesgoEIABean().setEditing(false);
	}

	/**
	 * Inicialización de variables de la Identificacion de hallazgos
	 */
	public void inicializarHallazgo() {

		this.identificacionHallazgosEia = new IdentificacionHallazgosEia();

		this.identificacionHallazgosEia.setEistId(this.estudioImpactoAmbiental);
//		this.identificacionHallazgosEia.setNormativa(new Normativa());
	//	this.identificacionHallazgosEia.setArticulo(new ArticuloNormativa());

		this.isEditing = false;
		JsfUtil.addCallbackParam("hallazgo");

	}

	/**
	 * Inicialización de variables de la Identificacion de hallazgos
	 */
	public void cambioNormativa() {

		this.identificacionHallazgosEia.setEistId(this.estudioImpactoAmbiental);
		//this.identificacionHallazgosEia.setNormativa(new Normativa());
		//this.identificacionHallazgosEia.setArticulo(new ArticuloNormativa());
		JsfUtil.addCallbackParam("hallazgo");

	}

	/**
	 * Agregar hallazgo a la lista Hallazgos
	 */
	public void agregarHallazgo() {
		if (!this.isEditing) {
			this.listaIdentificacionHallazgos.add(this.identificacionHallazgosEia);
		}
		JsfUtil.addCallbackParam("hallazgo");
	}

	/**
	 * Elimina una Identificacion de hallazgo de la tabla
	 * 
	 * @param hallazgo
	 *            Identificacion de hallazgo a eliminar
	 */
	public void removerHallazgo(IdentificacionHallazgosEia hallazgo) {
		this.isEditing = false;
		this.listaIdentificacionHallazgos.remove(hallazgo);
		this.listaIdentificacionHallazgosEliminados.add(hallazgo);

	}
	
	
	 public StreamedContent getStreamContent() throws Exception {
	        DefaultStreamedContent content = null;
	        this.documentoGeneral = descargarAlfresco(documentoGeneral);
	        try {
	            if (documentoGeneral != null
	                    && documentoGeneral.getNombre() != null
	                    && documentoGeneral.getContenidoDocumento() != null) {
	                content = new DefaultStreamedContent(
	                        new ByteArrayInputStream(
	                                documentoGeneral.getContenidoDocumento()));
	                content.setName(documentoGeneral.getNombre());
	                //this.adjuntoDocumento=true;

	            } else
	                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


	        } catch (Exception exception) {
	            LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
	            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
	        }
	        return content;
	   }
	 
	 /**
	     * Descarga documento desde el Alfresco
	     * @param documento
	     * @return
	     * @throws CmisAlfrescoException
	     */
	public Documento descargarAlfresco(Documento documento)
			throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento
					.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		documentoGeneral = this.uploadListener(event,
				EstudioImpactoAmbiental.class, "pdf");
		this.adjuntoDocumento = true;
		// Entidad que tiene el documento adjuntado
		// getPuntoRecuperacion().setCertificadoCompatibilidadUsoSuelos(documento);
	}

	private Documento uploadListener(FileUploadEvent event, Class<?> clazz,
			String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumento(contenidoDocumento, clazz,
				extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}

	/**
	 * Crea el documento
	 *
	 * @param contenidoDocumento
	 *            arreglo de bytes
	 * @param clazz
	 *            Clase a la cual se va a ligar al documento
	 * @param extension
	 *            extension del archivo
	 * @return Objeto de tipo Documento
	 */
	public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz,
			String extension) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setIdTable(0);
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf"
				: "application/vnd.ms-excel");
		return documento;
	}

	public void salvarDocumento() {
		try {
			documentoGeneral.setIdTable(this.estudioImpactoAmbiental.getId());
			documentoGeneral.setDescripcion("Documento General");
			documentoGeneral.setEstado(true);
			documentosFacade.guardarDocumentoAlfresco(
					this.estudioImpactoAmbiental
							.getProyectoLicenciamientoAmbiental().getCodigo(),
					Constantes.CARPETA_EIA, 0L, documentoGeneral,
					TipoDocumentoSistema.IDENTIFICACION_HALLAZGOS, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param tipoDocumento
	 * @throws CmisAlfrescoException
	 */
	private void cargarAdjuntosEIA(TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException {
		
		List<Documento> documentosXEIA = documentosFacade.documentosTodosXTablaIdXIdDoc(this.estudioImpactoAmbiental.getId(),
				"EstudioImpactoAmbiental", tipoDocumento);

		if (documentosXEIA.size() > 0) {
			this.documentoGeneral = documentosXEIA.get(0);
			
			if(existeObservaciones){
				documentoHistorico = validarDocumentoHistorico(documentoGeneral, documentosXEIA);
//				if(!promotor.equals(loginBean.getNombreUsuario())){
					consultarDocumentosOriginales(documentosXEIA.get(0).getId(), documentosXEIA);
//				}
			}
			// this.descargarAlfresco(this.documentoGeneral);
		}
	}
	    
	public void salvarDocumentoHistorico() {
		try {
							
			documentoGeneral.setIdTable(this.estudioImpactoAmbiental.getId());
			documentoGeneral.setDescripcion("Documento General");
			documentoGeneral.setEstado(true);
			Documento documentoG = documentosFacade.guardarDocumentoAlfresco(this.estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getCodigo(),
					Constantes.CARPETA_EIA, 0L, documentoGeneral,TipoDocumentoSistema.IDENTIFICACION_HALLAZGOS, null);
			
			
			if (documentoHistorico != null) {
				documentoHistorico.setIdHistorico(documentoG.getId());
				documentoHistorico.setNumeroNotificacion(numeroNotificaciones);
				documentosFacade.actualizarDocumento(documentoHistorico);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Documento validarDocumentoHistorico(Documento documentoIngresado, List<Documento> documentosXEIA){		
		try {
			List<Documento> documentosList = new ArrayList<>();
			for(Documento documento : documentosXEIA){
				if(documento.getIdHistorico() != null && 
						documento.getIdHistorico().equals(documentoIngresado.getId()) && 
						documento.getNumeroNotificacion().equals(numeroNotificaciones)){
					documentosList.add(documento);
				}
			}			
			
			if(documentosList != null && !documentosList.isEmpty()){		        
				return documentosList.get(0);
			}else{
				return documentoIngresado;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return documentoIngresado = new Documento();
		}
	}

	/**
	 * MarielaG
	 * Consultar el listado de hallazgos ingresado antes de las correcciones
	 */
	private void consultarHallazgosOriginales(List<IdentificacionHallazgosEia> listaIdentificacionHallazgosEia) {
		try {
			List<IdentificacionHallazgosEia> hallazgosOriginales = new ArrayList<IdentificacionHallazgosEia>();
			List<IdentificacionHallazgosEia> hallazgosEliminados = new ArrayList<>();
			totalHallazgosModificados = 0;
			
			for(IdentificacionHallazgosEia hallazgoBdd : listaIdentificacionHallazgosEia){
				//si es un registro que se ingreso originalmente
				//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
				if(hallazgoBdd.getNumeroNotificacion() == null ||
						!hallazgoBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
					boolean agregarItemLista = true;
	    			//buscar si tiene historial
					for (IdentificacionHallazgosEia hallazgoHistorico : listaIdentificacionHallazgosEia) {
						if (hallazgoHistorico.getIdHistorico() != null  
								&& hallazgoHistorico.getIdHistorico().equals(hallazgoBdd.getId())) {
							//si existe un registro historico, no se agrega a la lista en este paso
							agregarItemLista = false;
							hallazgoBdd.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						hallazgosOriginales.add(hallazgoBdd);
					}
				} else {
					totalHallazgosModificados++;
	    			//es una modificacion
	    			if(hallazgoBdd.getIdHistorico() == null && hallazgoBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
	    				//es un registro nuevo
	    				//no ingresa en el lista de originales
	    				hallazgoBdd.setNuevoEnModificacion(true);
	    			}else{
	    				hallazgoBdd.setRegistroModificado(true);
	    				if(!hallazgosOriginales.contains(hallazgoBdd)){
	    					hallazgosOriginales.add(hallazgoBdd);
	    				}
	    			}
	    		}
				
				//para consultar eliminados
				if (hallazgoBdd.getIdHistorico() != null
						&& hallazgoBdd.getNumeroNotificacion() != null) {
					boolean existePunto = false;
					for (IdentificacionHallazgosEia itemActual : this.listaIdentificacionHallazgos) {
						if (itemActual.getId().equals(hallazgoBdd.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto) {
						hallazgosEliminados.add(hallazgoBdd);
					}
				}
			}
			
			if (totalHallazgosModificados > 0){
				this.listaHallazgosOriginales = hallazgosOriginales;
			}
			
			listaHallazgosEliminadosBdd = hallazgosEliminados; 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Consultar los documentos originales
	 */
	private void consultarDocumentosOriginales(Integer idDocumento, List<Documento> documentosList){		
		try {
			listaDocumentoGeneralHistorico = new ArrayList<>();
			if (documentosList != null && !documentosList.isEmpty() && documentosList.size() > 1) {
				while (idDocumento > 0) {
					idDocumento = recuperarHistoricos(idDocumento, documentosList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Consultar los documentos historicos en bucle
	 */
	private int recuperarHistoricos(Integer idDocumento, List<Documento> documentosList) {
		try {
			int nextDocument = 0;
			for (Documento documento : documentosList) {
				if (documento.getIdHistorico() != null && documento.getIdHistorico().equals(idDocumento)) {
					nextDocument = documento.getId();
					listaDocumentoGeneralHistorico.add(0, documento);
				}
			}

			return nextDocument;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * MarielaG
	 * Para descargar documentos originales
	 */
	public StreamedContent getStreamContentOriginal(Documento documento) throws Exception {
        DefaultStreamedContent content = null;
        try {
        	this.documentoGeneralOriginal = this.descargarAlfresco(documento);
        	if (documentoGeneralOriginal != null
					&& documentoGeneralOriginal.getNombre() != null
					&& documentoGeneralOriginal.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoGeneralOriginal.getContenidoDocumento()));
				content.setName(documentoGeneralOriginal.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
	
	/**
	 * MarielaG
	 * Para mostrar el historial de cambios del hallazgo
	 */
	public void mostrarHallazgosOriginales(IdentificacionHallazgosEia hallazgo) {
		listaHallazgosHistorico = new ArrayList<>();
		
		for(IdentificacionHallazgosEia hallazgoOriginal : listaHallazgosOriginales){
			if(hallazgoOriginal.getIdHistorico() != null && hallazgo.getId().equals(hallazgoOriginal.getIdHistorico())){
				listaHallazgosHistorico.add(hallazgoOriginal);
			}
		}
	}
	
	/**
	 * MarielaG Para mostrar los hallazgos eliminados
	 */
	public void fillHallazgosEliminados() {
		listaHallazgosHistorico = listaHallazgosEliminadosBdd;
	}
}
