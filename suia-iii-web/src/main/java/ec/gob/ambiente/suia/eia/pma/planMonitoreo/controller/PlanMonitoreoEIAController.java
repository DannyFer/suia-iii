package ec.gob.ambiente.suia.eia.pma.planMonitoreo.controller;

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

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.AspectoAmbientalPMA;
import ec.gob.ambiente.suia.domain.ComponentePMA;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Normativas;
import ec.gob.ambiente.suia.domain.ParametrosNormativas;
import ec.gob.ambiente.suia.domain.ParametrosPlanMonitoreo;
import ec.gob.ambiente.suia.domain.PlanMonitoreoEia;
import ec.gob.ambiente.suia.domain.TablasNormativas;
import ec.gob.ambiente.suia.domain.TablasPlanMonitoreo;
import ec.gob.ambiente.suia.domain.enums.Periodicidad;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.NormativasFacade;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.ParametrosNormativaFacade;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.PlanMonitoreoEiaFacade;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.TablasNormativasFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * @author Oscar Campana
 */
@ManagedBean
@ViewScoped
public class PlanMonitoreoEIAController implements Serializable {

	private static final long serialVersionUID = 1572523482381028668L;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(PlanMonitoreoEIAController.class);

	@EJB
	private ValidacionSeccionesFacade validacionesSeccionesFacade;

	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@EJB
	private PlanMonitoreoEiaFacade planMonitoreoFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	private List<ComponentePMA> componentes;

	@Getter
	@Setter
	private List<AspectoAmbientalPMA> aspectoAmbientalLista;

	@EJB
	private NormativasFacade normativasFacade;

	@EJB
	private TablasNormativasFacade tablasNormativasFacade;

	@EJB
	private ParametrosNormativaFacade parametrosNormativaFacade;

	@Getter
	@Setter
	PlanMonitoreoEia planMonitoreoEia;

	@Getter
	@Setter
	TablasNormativas tablaNormativas;

	@Getter
	@Setter
	TablasPlanMonitoreo tablasPlanMonitoreo;

	@Getter
	@Setter
	ParametrosNormativas parametrosNormativas;

	@Getter
	@Setter
	List<Normativas> listaNormativas;

	@Getter
	@Setter
	List<ParametrosNormativas> listaParametrosNormativas;
	
	@Getter
	@Setter
	List<PlanMonitoreoEia> listaPlanMonitoreoEia, listaPlanMonitoreoOriginales, listaPlanMonitoreoEliminadosBdd;

	@Getter
	@Setter
	List<TablasNormativas> listaTablasNormativas;

	@Getter
	@Setter
	List<TablasPlanMonitoreo> listaTablasPlanMonitoreo, listaTablasOriginales, listaTablasHistorico;

	@Getter
	@Setter
	List<ParametrosNormativas> listaParametrosNormativa;

	@Getter
	@Setter
	List<ParametrosPlanMonitoreo> listaParametrosPlanMonitoreo;

	@Getter
	@Setter
	List<PlanMonitoreoEia> listaPlanMonitoreoEiaEliminados;

	@EJB
	private PlanMonitoreoEiaFacade planMonitoreoEiaFacade;

	@Setter
	@Getter
	private Periodicidad[] periodicidad = Periodicidad.values();

	@Setter
	@Getter
	private ComponentePMA componente;

	@Setter
	@Getter
	private AspectoAmbientalPMA aspectoAmbiental;

	@Setter
	@Getter
	private boolean isEditing;

	@Setter
	@Getter
	private boolean isExPost;

	@Setter
	@Getter
	private boolean isEditingTablas;

	private boolean agregarTablas;
	
	@Getter
	private boolean esMineriaNoMetalicos,existeDocumentoAdjunto;

	@Setter
	@Getter
	Normativas normativa;
	
	@Getter
	@Setter
	private Documento documento, documentoHistorico, documentoOriginal;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentoHistorico;
	
	//////////////////////////Variables//////////////////////////////////
	
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
	private List<PlanMonitoreoEia> listaPlanHistorial;
		
	//////////////////////////////////////////////////////////////////////////

	/**
	 * Metodo que se ejecuta automaticamente al cargar la pagina
	 * 
	 * @throws CmisAlfrescoException
	 * @throws ServiceException
	 * @throws JbpmException 
	 */
	@PostConstruct
	private void postInit() throws CmisAlfrescoException, ServiceException, JbpmException {
		
		/**
		 * Cris F: obtener variables
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

		this.listaTablasPlanMonitoreo = new ArrayList<TablasPlanMonitoreo>();
		this.listaParametrosPlanMonitoreo = new ArrayList<ParametrosPlanMonitoreo>();
		this.componentes = new ArrayList<ComponentePMA>();

		cargarDatos();
		evaluarTipoestudio();
		
		esMineriaNoMetalicos=estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudioImpactoAmbiental.getResumenEjecutivo()==null?true:false;
		
		if(esMineriaNoMetalicos)
		{
			cargarAdjuntosEIA(TipoDocumentoSistema.PLAN_MONITOREO,"Adjunto");
		}

	}

	/**
	 * Evalua el tipo de estudio
	 */
	private void evaluarTipoestudio() {
		if (this.estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getTipoEstudio().getId().equals(1)) {
			this.isExPost = false;
		} else {
			this.isExPost = true;
		}

	}

	/**
	 * Carga datos y consultas iniciales
	 * 
	 * @throws CmisAlfrescoException
	 * @throws ServiceException
	 */
	private void cargarDatos() throws CmisAlfrescoException, ServiceException {

		this.isEditing = false;
		this.isEditingTablas = false;
//		this.isExPost = false;
		evaluarTipoestudio();
		this.agregarTablas = false;

		this.planMonitoreoEia = new PlanMonitoreoEia();

		this.tablaNormativas = new TablasNormativas();

		this.normativa = new Normativas();

		listaPlanMonitoreoEia = new ArrayList<PlanMonitoreoEia>();
		listaPlanMonitoreoEiaEliminados = new ArrayList<PlanMonitoreoEia>();
		
		listaParametrosNormativas = new ArrayList<ParametrosNormativas>();

		this.listaNormativas = new ArrayList<Normativas>();

		this.listaNormativas = this.normativasFacade.listar(this.estudioImpactoAmbiental
				.getProyectoLicenciamientoAmbiental().getTipoSector().getId(), this.estudioImpactoAmbiental
				.getProyectoLicenciamientoAmbiental().getTipoEstudio().getId());

		for (Normativas nor : this.listaNormativas) {
			nor.setEtiqueta(nor.getDescripcion() + " - " + nor.getComponenteFisico());
		}

		try {
			//MarielaG
        	//Cambio para recuperar los registros actuales y originales
			List<PlanMonitoreoEia> listaPlan = planMonitoreoEiaFacade.listarTodosPorEIA(estudioImpactoAmbiental);
			List<PlanMonitoreoEia> listaPlanOriginal = new ArrayList<PlanMonitoreoEia>();

			for (PlanMonitoreoEia planEia : listaPlan) {
				if(planEia.getIdHistorico() == null){
					this.listaPlanMonitoreoEia.add(planEia); //agrego solo actuales para visualizacion
				}
				PlanMonitoreoEia planEiaOriginal = planEia.clone();
				planEiaOriginal.setId(planEia.getId());
				listaPlanOriginal.add(planEiaOriginal);
			}
						
			for(PlanMonitoreoEia planMonitoreo : listaPlanMonitoreoEia){
				List<TablasPlanMonitoreo> listaPlanMonitoreo = new ArrayList<TablasPlanMonitoreo>();
				for(TablasPlanMonitoreo tablaPlanMonitoreo : planMonitoreo.getTablasPlanMonitoreo()){
					if(tablaPlanMonitoreo.getIdHistorico() == null){
						listaPlanMonitoreo.add(tablaPlanMonitoreo);
					}
				}
				planMonitoreo.setTablasPlanMonitoreo(listaPlanMonitoreo);
			}			

			//MarielaG para buscar informacion original
			if (existeObservaciones) {
//				if (!promotor.equals(loginBean.getNombreUsuario())) {
					consultarPlanesOriginales(listaPlan);
					consultarTablasOriginales(listaPlanOriginal);
//				}
			}
			
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	/**
	 * Metodo de guardado general
	 * 
	 * @throws CmisAlfrescoException
	 */
	public void guardar() throws CmisAlfrescoException {
		try {
			if(!esMineriaNoMetalicos)
			{
				if(existeObservaciones){
					planMonitoreoEiaFacade.guardarHistorico(estudioImpactoAmbiental, listaPlanMonitoreoEia, listaPlanMonitoreoEiaEliminados, numeroNotificaciones);
				}else{				
					planMonitoreoEiaFacade.guardar(listaPlanMonitoreoEia, listaPlanMonitoreoEiaEliminados);
				}
				cargarDatos();

				if (this.listaPlanMonitoreoEia.size() > 0) {
					this.actualizarEstadoValidacionSeccion();
				}
			}else{
				if (documento != null && documento.getContenidoDocumento() != null) {
					documento.setIdTable(this.obtenerEIA().getId());
					documento.setDescripcion("Adjunto");
					documento.setEstado(true);
					Documento documentoGuardado = documentosFacade.guardarDocumentoAlfresco(this.obtenerEIA().getProyectoLicenciamientoAmbiental()
							.getCodigo(), Constantes.CARPETA_EIA, 0L, documento, TipoDocumentoSistema.PLAN_MONITOREO, null);
					
					if(existeObservaciones && documentoHistorico != null){
						documentoHistorico.setIdHistorico(documentoGuardado.getId());
						documentoHistorico.setNumeroNotificacion(numeroNotificaciones);
						documentosFacade.actualizarDocumento(documentoHistorico);
					}
					
					this.actualizarEstadoValidacionSeccion();
				} else {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_NO_EXISTE_DOCUMENTO);
					return;
				}
			}
			
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/planMonitoreo/planMonitoreo.jsf?id=16&amp;faces-redirect=true");
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
		} catch (RuntimeException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
		}

	}

	/**
	 * Asigna la seccion valida
	 * 
	 * @throws ServiceException
	 * @throws CmisAlfrescoException
	 */

	private void actualizarEstadoValidacionSeccion() throws ServiceException, CmisAlfrescoException {
		validacionesSeccionesFacade.guardarValidacionSeccion("EIA", "planMonitoreo", estudioImpactoAmbiental.getId()
				.toString());

	}

	/**
	 * Prepara la edicion de un plan de monitoreo
	 * 
	 * @param planMonitoreo
	 */
	public void editarPlanMonitoreo(PlanMonitoreoEia planMonitoreo) {

		this.planMonitoreoEia = planMonitoreo;
		this.isEditing = true;

	}

	/**
	 * Prepara la edicion de una tabla
	 * 
	 * @param tablasPlanMonitoreo
	 * @throws ServiceException
	 */
	public void editarTabla(TablasPlanMonitoreo tablasPlanMonitoreo) throws ServiceException {

		this.tablasPlanMonitoreo = tablasPlanMonitoreo;
		this.isEditingTablas = true;
		this.listaParametrosNormativa = this.parametrosNormativaFacade.listarPorTabla(this.tablasPlanMonitoreo
				.getIdReferencia());
		JsfUtil.addCallbackParam("planMonitoreoP");

	}

	/**
	 * Inicializacion de variables y objetos
	 */
	public void inicializarPlanMonitoreo() {

		this.agregarTablas = true;
		this.planMonitoreoEia = new PlanMonitoreoEia();

		this.planMonitoreoEia.setEia(this.estudioImpactoAmbiental);
		this.planMonitoreoEia.setNormativas(new Normativas());
		this.planMonitoreoEia.setTablasPlanMonitoreo(new ArrayList<TablasPlanMonitoreo>());
		this.planMonitoreoEia.setComponente("");
		this.planMonitoreoEia.setTipoComponente("");

		this.componentes = new ArrayList<ComponentePMA>();
		this.componente = new ComponentePMA();
		this.aspectoAmbiental = new AspectoAmbientalPMA();
		this.aspectoAmbientalLista = new ArrayList<AspectoAmbientalPMA>();

		listaTablasPlanMonitoreo = new ArrayList<TablasPlanMonitoreo>();

		this.isEditing = false;
		JsfUtil.addCallbackParam("planMonitoreo");

	}

	/**
	 * Agregar un plan de monitoreo
	 */
	public void agregarPlanMonitoreo() {
		if (!this.isEditing) {
			this.planMonitoreoEia.setFechaCreacion(new Date());
			this.listaPlanMonitoreoEia.add(this.planMonitoreoEia);
		}
		JsfUtil.addCallbackParam("planMonitoreo");
	}

	/**
	 * Elimina un plan de monitoreo
	 * 
	 * @param planMonitoreoEia
	 *            plan de monitoreo
	 */
	public void removerPlanMonitoreo(PlanMonitoreoEia planMonitoreoEia) {
		this.isEditing = false;
		this.listaPlanMonitoreoEia.remove(planMonitoreoEia);
		if(planMonitoreoEia.getId() != null)
			this.listaPlanMonitoreoEiaEliminados.add(planMonitoreoEia);

	}

	/**
	 * Elimina un parametro
	 * 
	 * @param parametrosPlanMonitoreoEia
	 *            Parametros Plan de Monitoreo
	 */
	public void removerParametro(ParametrosPlanMonitoreo parametrosPlanMonitoreoEia) {
		this.isEditing = false;
		this.tablasPlanMonitoreo.getParametrosPlanMonitoreo().remove(parametrosPlanMonitoreoEia);

	}

	/**
	 * Consulta tablas por normativa
	 * 
	 * @throws ServiceException
	 */
	public void cargaTablas() throws ServiceException {

		if (agregarTablas) {
			this.listaTablasNormativas = this.tablasNormativasFacade.listarPorNormativa(this.planMonitoreoEia
					.getNormativas());

			for (TablasNormativas tablas : this.listaTablasNormativas) {
				TablasPlanMonitoreo tablaPlan = new TablasPlanMonitoreo();
				tablaPlan.setParametrosPlanMonitoreo(new ArrayList<ParametrosPlanMonitoreo>());
				tablaPlan.setDescripcion(tablas.getDescripcion());
				tablaPlan.setEstado(tablas.getEstado());
				tablaPlan.setIdReferencia(tablas.getId());
				tablaPlan.setPlanMonitoreoEia(this.planMonitoreoEia);				

				this.listaTablasPlanMonitoreo.add(tablaPlan);
				agregarTablas = false;
			}

			this.planMonitoreoEia.setTablasPlanMonitoreo(this.listaTablasPlanMonitoreo);
		}
	}

	/**
	 * Consultar parametros por tablas
	 * 
	 * @throws ServiceException
	 */
	public void cargaParametros() throws ServiceException {
		this.listaParametrosNormativa = this.parametrosNormativaFacade.listarPorTabla(this.tablasPlanMonitoreo
				.getIdReferencia());
	}

	/**
	 * Agregar parametro
	 */
	public void agregarParametro() {

		ParametrosPlanMonitoreo para = new ParametrosPlanMonitoreo();

		if(!listaParametrosNormativas.contains(this.parametrosNormativas))
		{
			listaParametrosNormativas.add(this.parametrosNormativas);
			para.setDescripcion(this.parametrosNormativas.getDescripcion());			
			para.setTablasPlanMonitoreo(this.tablasPlanMonitoreo);
			this.tablasPlanMonitoreo.getParametrosPlanMonitoreo().add(para);
		}

	}
	
	public void agregarParam()
	{
		JsfUtil.addCallbackParam("planMonitoreoP");
	}

	/**
	 * Consultar componentes
	 */
	public void cargarComponentes() {
		try {

			this.planMonitoreoEia.setComponente(null);
			this.planMonitoreoEia.setTablasPlanMonitoreo(new ArrayList<TablasPlanMonitoreo>());

			this.planMonitoreoEia.setComponente(this.planMonitoreoEia.getNormativas().getComponenteFisico());

			this.aspectoAmbientalLista = normativasFacade.getAspectosAmbientalesPorComponente(this.normativasFacade
					.getComponentes(this.planMonitoreoEia.getComponente()).get(0));

		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
		}
	}

	/**
	 * Consultar aspectos ambientales
	 */
	public void cargarAspectosAmbientales() {
		try {

			if (this.componente != null) {
				this.planMonitoreoEia.setComponente(componente.getNombre());
				this.aspectoAmbientalLista = normativasFacade.getAspectosAmbientalesPorComponente(this.componente);
			}
		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
		}
	}

	/**
	 * Procesar aspecto ambiental seleccionado
	 */
	public void procesarAspectosAmbientales() {
		try {

			this.cargaTablas();
			if (this.aspectoAmbiental != null) {
				this.planMonitoreoEia.setTipoComponente(aspectoAmbiental.getNombre());
			}
		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
		}
	}

	/**
	 * Metodo para redireccionar
	 */
	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/planMonitoreo/planMonitoreo.jsf");
	}
	
	public void descargarPlantilla() throws CmisAlfrescoException, IOException {

		JsfUtil.descargarMimeType(documentosFacade.descargarDocumentoPorNombre("Plantilla_Plan_Monitoreo_Mineria_No_Metalicos.xls"),
				"Plantilla_Plan_Monitoreo_Mineria_No_Metalicos.xls","xls", "application/vnd.ms-excel");

	}
	
	public void uploadListenerDocumentosEIA(FileUploadEvent event) {
		documento = this.uploadListener(event, EstudioImpactoAmbiental.class, "xls");
		this.existeDocumentoAdjunto = true;		
	}
	
	private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumento(contenidoDocumento, clazz, extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}
	
	public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setIdTable(0);
		documento.setExtesion("." + extension);

		documento.setMime("application/vnd.ms-excel");
		return documento;
	}
	
	public StreamedContent getStreamContent() throws Exception {
		DefaultStreamedContent content = null;
		try {
			
			if (documento != null && documento.getNombre() != null
					&& documento.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documento.getContenidoDocumento()));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}
	
	private EstudioImpactoAmbiental obtenerEIA() {
		EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil
				.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);

		return es;
	}
	
	private void cargarAdjuntosEIA(TipoDocumentoSistema tipoDocumento, String tipo) throws CmisAlfrescoException {

		List<Documento> documentosXEIA = documentosFacade.documentosTodosXTablaIdXIdDoc(this.obtenerEIA().getId(),
				"EstudioImpactoAmbiental", tipoDocumento);

		if (documentosXEIA.size() > 0) {

			if (tipo.equalsIgnoreCase("Adjunto")) {
				this.documento = documentosXEIA.get(0);
				this.descargarAlfresco(this.documento);
				this.existeDocumentoAdjunto = true;
			} 
			
			if(existeObservaciones){
				documentoHistorico = validarDocumentoHistorico(documento, documentosXEIA);
//				if(!promotor.equals(loginBean.getNombreUsuario())){
					consultarDocumentosOriginales(documentosXEIA.get(0).getId(), documentosXEIA);
//				}
			}
		}
	}
	
	public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}

	/**
	 * Cris F: Documento Historico Proyecto Minero
	 * @param documentoIngresado
	 * @return
	 */
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
			listaDocumentoHistorico = new ArrayList<>();
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
					listaDocumentoHistorico.add(0, documento);
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
	 * Consultar el listado de planes ingresados antes de las correcciones
	 */
	private void consultarPlanesOriginales(List<PlanMonitoreoEia> listaPlanMonitoreo) {
		try {
			listaPlanMonitoreoOriginales = new ArrayList<PlanMonitoreoEia>();
			List<PlanMonitoreoEia> planesOriginales = new ArrayList<PlanMonitoreoEia>();
			List<PlanMonitoreoEia> planesEliminados = new ArrayList<>();
			int totalModificados = 0;
			
			for(PlanMonitoreoEia planBdd : listaPlanMonitoreo){
				//si es un registro que se ingreso originalmente
				//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
				if(planBdd.getNumeroNotificacion() == null ||
						!planBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
					boolean agregarItemLista = true;
	    			//buscar si tiene historial
					for (PlanMonitoreoEia planHistorico : listaPlanMonitoreo) {
						if (planHistorico.getIdHistorico() != null  
								&& planHistorico.getIdHistorico().equals(planBdd.getId())) {
							//si existe un registro historico, no se agrega a la lista en este paso
							agregarItemLista = false;
							planBdd.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						planesOriginales.add(planBdd);
					}
				} else {
					totalModificados++;
	    			//es una modificacion
	    			if(planBdd.getIdHistorico() == null && planBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
	    				//es un registro nuevo
	    				//no ingresa en el lista de originales
	    				planBdd.setNuevoEnModificacion(true);
	    			}else{
	    				planBdd.setRegistroModificado(true);
	    				if(!planesOriginales.contains(planBdd)){
	    					planesOriginales.add(planBdd);
	    				}
	    			}
	    		}
				
				//para consultar eliminados
				if (planBdd.getIdHistorico() != null
						&& planBdd.getNumeroNotificacion() != null
						&& planBdd.getNumeroNotificacion().equals(numeroNotificaciones)) {
					boolean existePunto = false;
					for (PlanMonitoreoEia itemActual : this.listaPlanMonitoreoEia) {
						if (itemActual.getId().equals(planBdd.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto) {
						planesEliminados.add(planBdd);
					}
				}
			}
			
			if (totalModificados > 0){
				this.listaPlanMonitoreoOriginales = planesOriginales;
			}
			
			if (planesEliminados.size() > 0) {
				for(PlanMonitoreoEia planEliminado : planesEliminados){
					List<TablasPlanMonitoreo> tablas = planMonitoreoEiaFacade.getTablasPlanHistorico(planEliminado.getIdHistorico());
					planEliminado.setTablasPlanMonitoreo(tablas);
				}
			}
			this.listaPlanMonitoreoEliminadosBdd = planesEliminados;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Consultar el listado de tablas ingresadas antes de las correcciones
	 */
	private void consultarTablasOriginales(List<PlanMonitoreoEia> listaPlanMonitoreo) {
		try {
			
			List<TablasPlanMonitoreo> tablasOriginales = new ArrayList<TablasPlanMonitoreo>();
			listaTablasOriginales = new ArrayList<>();
			
			for (PlanMonitoreoEia planBdd : listaPlanMonitoreo) {
				for (PlanMonitoreoEia planActual : this.listaPlanMonitoreoEia) {
					if (planActual.getId().equals(planBdd.getId())) {
						
						int totalModificadosIndiv = 0;

						for (TablasPlanMonitoreo tablaPlanBdd : planBdd.getTablasPlanMonitoreo()) {
							if (tablaPlanBdd.getNumeroNotificacion() == null ||
									!tablaPlanBdd.getNumeroNotificacion().equals(numeroNotificaciones)) {
								boolean agregarItemLista = true;
								// buscar si tiene historial
								for (TablasPlanMonitoreo planHistorico : planBdd.getTablasPlanMonitoreo()) {
									if (planHistorico.getIdHistorico() != null
											&& planHistorico.getIdHistorico().equals(tablaPlanBdd.getId())) {
										// si existe un registro historico, no
										// se agrega a la lista en este paso
										agregarItemLista = false;
										tablaPlanBdd.setRegistroModificado(true);
										break;
									}
								}
								if (agregarItemLista) {
									tablasOriginales.add(tablaPlanBdd);
								}
							} else {
								totalModificadosIndiv++;
								// es una modificacion
								if (tablaPlanBdd.getIdHistorico() == null && tablaPlanBdd.getNumeroNotificacion().equals(numeroNotificaciones)) {
									// es un registro nuevo
									// no ingresa en el lista de originales
									tablaPlanBdd.setNuevoEnModificacion(true);
								} else {
									tablaPlanBdd.setRegistroModificado(true);
				    				if(!tablasOriginales.contains(tablaPlanBdd)){
				    					tablasOriginales.add(tablaPlanBdd);
				    				}
								}
							}

						}
						
						if (totalModificadosIndiv > 0) {
							planActual.setTablasModificadas(true);
							listaTablasOriginales.addAll(tablasOriginales);
						}
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Visualizar informacion de un plan de monitoreo
	 */
	public void mostrarOriginal(PlanMonitoreoEia planMonitoreo) {

		this.planMonitoreoEia = planMonitoreo;
		listaPlanHistorial = new ArrayList<>();
		
		for(PlanMonitoreoEia planOriginal : listaPlanMonitoreoOriginales){
			if(planOriginal.getIdHistorico() != null && planMonitoreo.getId().equals(planOriginal.getIdHistorico())){
				listaPlanHistorial.add(planOriginal);
			}
		}
	}

	/**
	 * MarielaG
	 * Para descargar documentos originales
	 */
	public StreamedContent getStreamContentOriginal(Documento documento) throws Exception {
        DefaultStreamedContent content = null;
        try {
        	this.documentoOriginal = this.descargarAlfresco(documento);
        	if (documentoOriginal != null
					&& documentoOriginal.getNombre() != null
					&& documentoOriginal.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoOriginal.getContenidoDocumento()));
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
	 * Para mostrar el historial de cambios de las tablas
	 */
	public void mostrarTablasOriginales(TablasPlanMonitoreo tabla) {
		listaTablasHistorico = new ArrayList<>();
		
		for(TablasPlanMonitoreo tablaOriginal : listaTablasOriginales){
			if(tablaOriginal.getIdHistorico() != null && tabla.getId().equals(tablaOriginal.getIdHistorico())){
				listaTablasHistorico.add(tablaOriginal);
			}
		}
	}
}
