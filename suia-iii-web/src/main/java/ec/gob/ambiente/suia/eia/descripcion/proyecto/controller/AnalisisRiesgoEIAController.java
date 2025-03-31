package ec.gob.ambiente.suia.eia.descripcion.proyecto.controller;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.domain.enums.TipoRiesgo;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.bean.AnalisisRiesgoEIABean;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.AnalisisRiesgoEiaFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author christian
 */
@ManagedBean
@ViewScoped
public class AnalisisRiesgoEIAController implements Serializable {



    private static final long serialVersionUID = 1572525482381028668L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(AnalisisRiesgoEIAController.class);

    public static final String OTROS="Otros";

    @EJB
    private AnalisisRiesgoEiaFacade analisisRiesgoEiaFacade;

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
    
    @Getter
    private Boolean esMineriaNoMetalicos;
    
    ///////////////////////////////////////////////////////

    private Map<String, Object> processVariables;
	private Integer numeroNotificaciones;
	private boolean existeObservaciones;
	private String promotor;
	
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
		promotor = (String) processVariables.get("u_Promotor");
		
		if(numeroNotificaciones > 0)
			existeObservaciones = true;
		//Fin CF	
    	
		estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
		esMineriaNoMetalicos=estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudioImpactoAmbiental.getResumenEjecutivo()==null?true:false;
		
        cargarDatos();
        documentoGeneral = new Documento();  
        documentoHistorico = new Documento();
        documentoGeneralOriginal = new Documento();

        cargarAdjuntosEIA(TipoDocumentoSistema.ANALISIS_RIESGOS_GEN);

    }

    private void cargarDatos() throws CmisAlfrescoException {

        getAnalisisRiesgoEIABean().iniciarDatos();
        
        if(!esMineriaNoMetalicos)
        	cargarAnalisisEia();
        //   cargarAdjuntosEIA(TipoDocumentoSistema.ANALISIS_RIESGOS_GEN);
    }
    /**
     * Recupera el Estudio de Impacto Ambiental de la Sesión
     */
    private void cargarAnalisisEia() {
        //LOG.info("ESTUDIO DE IMPACTO AMBIENTAL=" + estudioImpactoAmbiental.getId());
        try {
        	//MarielaG
        	//Cambio para recuperar los registros actuales y originales
        	List<AnalisisRiesgoEia> listaAnalisisEia = new ArrayList<AnalisisRiesgoEia>();
        	List<AnalisisRiesgoEia> listaAnalisisActuales = new ArrayList<AnalisisRiesgoEia>();
			listaAnalisisEia = analisisRiesgoEiaFacade.listarTodosRegistrosPorEIA(estudioImpactoAmbiental);
			for (AnalisisRiesgoEia analisisEia : listaAnalisisEia) {
				if(analisisEia.getIdHistorico() == null){
					listaAnalisisActuales.add(analisisEia); 
				}
			}
			
            getAnalisisRiesgoEIABean().setListaAnalisisRiesgoEia(listaAnalisisActuales);
            
          //MarielaG para buscar informacion original
			if (existeObservaciones) {
//				if (!promotor.equals(loginBean.getNombreUsuario())) {
					consultarAnalisisOriginal(listaAnalisisEia);
//				}
			}
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    /**
     * Guarda la lista de Análisis de Riesgo ingresadas y el documento al Alfresco
     * @throws CmisAlfrescoException
     */
    public void guardar() throws CmisAlfrescoException {
        try {
        	
        	if(existeObservaciones){
        		analisisRiesgoEiaFacade.guardarHistorico(getAnalisisRiesgoEIABean().getListaAnalisisRiesgoEia(), getAnalisisRiesgoEIABean().getListaAnalisisRiesgoEiaEliminados(), numeroNotificaciones);        		
        	}else{
        		analisisRiesgoEiaFacade.guardar(getAnalisisRiesgoEIABean().getListaAnalisisRiesgoEia(), getAnalisisRiesgoEIABean().getListaAnalisisRiesgoEiaEliminados());
        	}
           
        	if(existeObservaciones){
        		salvarDocumentoHistorico();
        	}else{
        		this.salvarDocumento();
        	}
        	
            this.actualizarEstadoValidacionSeccion();
            cargarDatos();
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
           
//            if(estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getTipoEstudio().getId() != 2){
//            	JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/analisisRiesgo/analisisRiesgo.jsf?id=19");
//            }else{
//            	JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/analisisRiesgo/analisisRiesgo.jsf?id=9");
//            }
            
        } catch (ServiceException e) {
            LOG.error(e, e);
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
        } catch (RuntimeException e) {
            LOG.error(e, e);
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
        }
    }

    /**
     * Realiza la validación de que la lista de análisis de riesgos ingresado contenga por lo menos un elemento y que haya adjuntado un documento
     *
     * @return
     */
//    private boolean isSeccionValida() throws CmisAlfrescoException {
//        if (tienePorLoMenosUnAnalisis() && documentoGuardado()) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * Asigna la sección válida
     * @throws ServiceException
     * @throws CmisAlfrescoException
     */
    private void actualizarEstadoValidacionSeccion() throws ServiceException, CmisAlfrescoException {
        validacionesSeccionesFacade.guardarValidacionSeccion("EIA", "analisisRiesgo", estudioImpactoAmbiental.getId().toString());

    }

    /**
     * Indica si ya se agregado por lo menos un Análisis de Riesgo a la tabla
     * @return
     */
    private boolean tienePorLoMenosUnAnalisis() {
        if (getAnalisisRiesgoEIABean().getListaAnalisisRiesgoEia().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Acción del botón cancelar.
     */
    public void cancelar() {
        JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/analisisRiesgo/analisisRiesgo.jsf");
    }

    /**
     * Prepara la edición de un Análisis de Riesgo
     * @param analisisDeRiesgo
     */
    public void editar(AnalisisRiesgoEia analisisDeRiesgo) {
        getAnalisisRiesgoEIABean().setEditing(true);
        getAnalisisRiesgoEIABean().setAnalisisActivo(analisisDeRiesgo);
        cargarDatosEdicion();
    }

    /**
     * Carga los datos para la edición de un Análisis de Riesgo
     */
    private void cargarDatosEdicion() {
        getAnalisisRiesgoEIABean().setTipoRiesgo(getAnalisisRiesgoEIABean().getAnalisisActivo().getRiesgo().getSubTipo().getTipo());
        cargarSubtiposRiesgoPorTipo();
        getAnalisisRiesgoEIABean().setSubtipo(getAnalisisRiesgoEIABean().getAnalisisActivo().getRiesgo().getSubTipo());
        cargarRiesgosPorSubTipo();
    }


    /**
     * Inicialización de variables del Análisis de Riesgo
     */
    public void inicializarAnalisis() {
        getAnalisisRiesgoEIABean().setAnalisisActivo(new AnalisisRiesgoEia());
        EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
        getAnalisisRiesgoEIABean().getAnalisisActivo().setEistId(es);
        getAnalisisRiesgoEIABean().setSubtipo(null);
        getAnalisisRiesgoEIABean().setTipoRiesgo(null);
        getAnalisisRiesgoEIABean().setEditing(false);
    }

    /**
     * Carga los Subtipos de riesgo de acuerdo al tipo de riesgo seleccionado
     */
    public void cargarSubtiposRiesgoPorTipo() {
        try {
            TipoRiesgo tipo = getAnalisisRiesgoEIABean().getTipoRiesgo();
            getAnalisisRiesgoEIABean().setSubTiposRiesgo(analisisRiesgoEiaFacade.buscarSubTipoPorTipo(tipo));
            if (tipo.equals(TipoRiesgo.ENDOGENO)) {
                getAnalisisRiesgoEIABean().setRiesgos(new ArrayList<Riesgo>());
                getAnalisisRiesgoEIABean().setSubtipo(getAnalisisRiesgoEIABean().getSubTiposRiesgo().get(0));
                cargarRiesgosPorSubTipo();
                getAnalisisRiesgoEIABean().setRenderSubtipo(false);
            } else {
                getAnalisisRiesgoEIABean().setRenderSubtipo(true);
            }

        } catch (ServiceException e) {
            LOG.error(e);
            JsfUtil.addMessageError(e.getMessage());
        }

    }

    /**
     * Carga Riesgos de acuerdo al Subtipo.
     */
    public void cargarRiesgosPorSubTipo() {
        try {
            SubTipoRiesgo subTipo = getAnalisisRiesgoEIABean().getSubtipo();
            getAnalisisRiesgoEIABean().setRiesgos(analisisRiesgoEiaFacade.buscarRiesgoPorSubTipo(subTipo));
        } catch (ServiceException e) {
            LOG.error(e);
            JsfUtil.addMessageError(e.getMessage());
        }

    }

    /**
     * Agrega o edita un Análisis de Riesgo
     */
    public void agregar() {
        if (!getAnalisisRiesgoEIABean().isEditing()) {
            agregarAnalisis();
        }

    }

    /**
     * Elimina un Análisis de Riesgo de la tabla
     * @param analisisDeRiesgo El Análisis de Riesgo a eliminar
     */
    public void remover(AnalisisRiesgoEia analisisDeRiesgo) {
        getAnalisisRiesgoEIABean().getListaAnalisisRiesgoEia().remove(analisisDeRiesgo);
        if(analisisDeRiesgo.getId() != null)
        	getAnalisisRiesgoEIABean().getListaAnalisisRiesgoEiaEliminados().add(analisisDeRiesgo);
    }

    /**
     * Indica si el riesgo asociado al Análisis de Riesgo ingresado ya se encuentra registrado.
     * @return
     */
    private boolean riesgoYaRegistrado() {
        boolean riesgoRegistrado = false;
        for (AnalisisRiesgoEia analisis : getAnalisisRiesgoEIABean().getListaAnalisisRiesgoEia()) {
            if (analisis.getRiesgo().equals(getAnalisisRiesgoEIABean().getAnalisisActivo().getRiesgo())) {
                riesgoRegistrado = true;
            }
        }
        return riesgoRegistrado;
    }

    /**
     * Agrega un análisis a la tabla
     */
    private void agregarAnalisis() {
        if (riesgoYaRegistrado()) {
            JsfUtil.addMessageError("El riesgo ya se encuentra registrado");
            RequestContext context = RequestContext.getCurrentInstance();
            context.addCallbackParam("riesgoYaRegistrado", true);
        } else {
            getAnalisisRiesgoEIABean().getListaAnalisisRiesgoEia().add(getAnalisisRiesgoEIABean().getAnalisisActivo());
        }
    }

    /**
     * Controla el render del input que permite el ingreso de otro Riesgo
     */
    public void controlarOtroRiesgo() {
        if (getAnalisisRiesgoEIABean().getAnalisisActivo().getRiesgo().getNombre().equals(OTROS)) {
            getAnalisisRiesgoEIABean().setHabilitarOtroRiesgo(true);
        } else {
            getAnalisisRiesgoEIABean().setHabilitarOtroRiesgo(false);
        }
    }


    public void salvarDocumento() {
        try {
            documentoGeneral.setIdTable(this.estudioImpactoAmbiental.getId());
            documentoGeneral.setDescripcion("Documento General");
            documentoGeneral.setEstado(true);
            documentosFacade.guardarDocumentoAlfresco(this.estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getCodigo(), Constantes.CARPETA_EIA, 0L, documentoGeneral, TipoDocumentoSistema.ANALISIS_RIESGOS_GEN, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadListenerDocumentos(FileUploadEvent event) {
        documentoGeneral = this.uploadListener(event,EstudioImpactoAmbiental.class, "pdf");
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
     * @param contenidoDocumento arreglo de bytes
     * @param clazz              Clase a la cual se va a ligar al documento
     * @param extension          extension del archivo
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

            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }

    /**
     *
     * @param tipoDocumento
     * @throws CmisAlfrescoException
     */
    private void cargarAdjuntosEIA(TipoDocumentoSistema tipoDocumento)
            throws CmisAlfrescoException {
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
            //this.descargarAlfresco(this.documentoGeneral);
        }
    }

//    /**
//     * Verifica si el documento adjunto de Análisis de Riesgo ya se encuentra subido
//     * @return <code>true</code> si el archivo ya ha sido adjuntado y <code>false</code> si el documento aún no se ha adjuntado
//     * @throws CmisAlfrescoException Si existe un error en la conexión con Alfresco
//     */
//    private boolean documentoGuardado() throws CmisAlfrescoException {
//        List<Documento> documentosXEIA = documentosFacade.documentoXTablaIdXIdDoc(this.estudioImpactoAmbiental.getId(), "EstudioImpactoAmbiental", TipoDocumentoSistema.ANALISIS_RIESGOS_GEN);
//        if (documentosXEIA.size() > 0) {
//            return true;
//        } else
//            return false;
//    }

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
            documentoContenido = documentosFacade
                    .descargar(documento.getIdAlfresco());
        if (documentoContenido != null)
            documento.setContenidoDocumento(documentoContenido);
        return documento;
    }


    public void validateResultados(FacesContext context, UIComponent validate, Object value) {
        StringBuilder  functionJs = new StringBuilder();
        List<FacesMessage> mensajes = new ArrayList<>();
        if(documentoGeneral.getNombre()==null){//NO VALIDO
            FacesMessage mensajeValidacionDocumento = new FacesMessage(FacesMessage.SEVERITY_ERROR,"El campo 'Documento de respaldo' es requerido.",null);
            mensajes.add(mensajeValidacionDocumento);
            functionJs.append("highlightComponent('frmDatos:panelGridAdjuntos');");
        }
        else{
            functionJs.append("removeHighLightComponent('frmDatos:panelGridAdjuntos');");
        }
        if(!tienePorLoMenosUnAnalisis() && !esMineriaNoMetalicos){
            FacesMessage mensajeValidacionDocumento = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe ingresar por lo menos un 'Análisis de Riesgo'",null);
            mensajes.add(mensajeValidacionDocumento);
            functionJs.append("highlightComponent('frmDatos:panelGridAnalisis');");
        }
        else{
            functionJs.append("removeHighLightComponent('frmDatos:panelGridAnalisis');");
        }
        RequestContext.getCurrentInstance().execute(functionJs.toString());
        if (!mensajes.isEmpty())
            throw new ValidatorException(mensajes);
    }
    
	public void salvarDocumentoHistorico() {
		try {
			
			Documento documentoG = new Documento();
			
			if(documentoGeneral.getId() == null){
				 documentoGeneral.setIdTable(this.estudioImpactoAmbiental.getId());
		         documentoGeneral.setDescripcion("Documento General");
		         documentoGeneral.setEstado(true);
		         documentoG = documentosFacade.guardarDocumentoAlfresco(this.estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getCodigo(), Constantes.CARPETA_EIA, 0L, documentoGeneral, TipoDocumentoSistema.ANALISIS_RIESGOS_GEN, null);
		         documentoGeneral = documentoG;
			}
			
			if (documentoHistorico != null && documentoG.getId() != null) {
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
			return documentoIngresado;
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
			System.out.println("dh");
			return nextDocument;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * MarielaG
	 * Para visualizar el documento original
	 * @return
	 * @throws Exception
	 */
	public StreamedContent getStreamContentOriginal(Documento documento) throws Exception {
        DefaultStreamedContent content = null;
        try {
        	this.documentoGeneralOriginal = descargarAlfresco(documento);
        	if (documentoGeneralOriginal != null
					&& documentoGeneralOriginal.getNombre() != null
					&& documentoGeneralOriginal.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoGeneralOriginal.getContenidoDocumento()));
				content.setName(documentoGeneralOriginal.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }

	/**
	 * MarielaG
	 * Consultar el listado de analisis ingresado antes de las correcciones
	 */
	private void consultarAnalisisOriginal(List<AnalisisRiesgoEia>  listaAnalisisRiesgoEia) {
		try {
			List<AnalisisRiesgoEia> analisisOriginales = new ArrayList<AnalisisRiesgoEia>();
			List<AnalisisRiesgoEia> analisisEliminados = new ArrayList<>();
			int totalModificados = 0;
			
			for(AnalisisRiesgoEia analisisBdd : listaAnalisisRiesgoEia){
				//si es un registro que se ingreso originalmente
				//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
				if(analisisBdd.getNumeroNotificacion() == null ||
						!analisisBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
					boolean agregarItemLista = true;
	    			//buscar si tiene historial
					for (AnalisisRiesgoEia planHistorico : listaAnalisisRiesgoEia) {
						if (planHistorico.getIdHistorico() != null  
								&& planHistorico.getIdHistorico().equals(analisisBdd.getId())) {
							//si existe un registro historico, no se agrega a la lista en este paso
							agregarItemLista = false;
							analisisBdd.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						analisisOriginales.add(analisisBdd);
					}
				} else {
					totalModificados++;
	    			//es una modificacion
	    			if(analisisBdd.getIdHistorico() == null && analisisBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
	    				//es un registro nuevo
	    				//no ingresa en el lista de originales
	    				analisisBdd.setNuevoEnModificacion(true);
	    			}else{
	    				analisisBdd.setRegistroModificado(true);
	    				if(!analisisOriginales.contains(analisisBdd)){
	    					analisisOriginales.add(analisisBdd);
	    				}
	    			}
	    		}
				
				//para consultar eliminados
				if (analisisBdd.getIdHistorico() != null
						&& analisisBdd.getNumeroNotificacion() != null) {
					boolean existePunto = false;
					for (AnalisisRiesgoEia itemActual : getAnalisisRiesgoEIABean().getListaAnalisisRiesgoEia()) {
						if (itemActual.getId().equals(analisisBdd.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto) {
						analisisEliminados.add(analisisBdd);
					}
				}
			}
			
			if (totalModificados > 0){
				getAnalisisRiesgoEIABean().setListaAnalisisRiesgoEiaOriginales(analisisOriginales);
			}
			
			getAnalisisRiesgoEIABean().setListaAnalisisRiesgoEliminadosBdd(analisisEliminados); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Para mostrar el historial de cambios del analisis
	 */
	public void mostrarOriginal(AnalisisRiesgoEia analisis) {
		List<AnalisisRiesgoEia> listaHistorico = new ArrayList<>();
		//listaAnalisisRiesgoEiaHistorico
		
		if(analisisRiesgoEIABean.getListaAnalisisRiesgoEiaOriginales() != null && !analisisRiesgoEIABean.getListaAnalisisRiesgoEiaOriginales().isEmpty()){
			for(AnalisisRiesgoEia analisisOriginal : analisisRiesgoEIABean.getListaAnalisisRiesgoEiaOriginales()){
				if(analisisOriginal.getIdHistorico() != null && analisis.getId().equals(analisisOriginal.getIdHistorico())){
					listaHistorico.add(analisisOriginal);
				}
			}
		}
		
		analisisRiesgoEIABean.setListaAnalisisRiesgoEiaHistorico(listaHistorico);
	}
    
    /**
	 * MarielaG Para mostrar los analisis eliminados
	 */
	public void fillAnalisisEliminados() {
		analisisRiesgoEIABean.setListaAnalisisRiesgoEiaHistorico(analisisRiesgoEIABean.getListaAnalisisRiesgoEliminadosBdd());
	}
	
}
