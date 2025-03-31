package ec.gob.ambiente.control.retce.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.EliminacionDesechosBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.retce.model.CatalogoSustanciasRetce;
import ec.gob.ambiente.retce.model.DesechoGestorAlmacenamiento;
import ec.gob.ambiente.retce.model.DesechoGestorAlmacenamientoExportacion;
import ec.gob.ambiente.retce.model.DesechoGestorTransporte;
import ec.gob.ambiente.retce.model.DesechoGestorTransporteExportacion;
import ec.gob.ambiente.retce.model.DesechoPeligrosoTransporteRETCE;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.GestorDesechosDisposicionFinal;
import ec.gob.ambiente.retce.model.GestorDesechosEliminacion;
import ec.gob.ambiente.retce.model.GestorDesechosEliminacionDesechoNoPeligroso;
import ec.gob.ambiente.retce.model.GestorDesechosEliminacionDesechoPeligroso;
import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.InformacionProyectoDesechosPeligrosos;
import ec.gob.ambiente.retce.model.ManifiestoUnico;
import ec.gob.ambiente.retce.model.ManifiestoUnicoTransferencia;
import ec.gob.ambiente.retce.model.ResumenManifiesto;
import ec.gob.ambiente.retce.model.Ruta;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.retce.services.CatalogoSustanciasRetceFacade;
import ec.gob.ambiente.retce.services.DesechoGestorAlmacenamientoExportacionFacade;
import ec.gob.ambiente.retce.services.DesechoGestorAlmacenamientoFacade;
import ec.gob.ambiente.retce.services.DesechoGestorTransporteExportacionFacade;
import ec.gob.ambiente.retce.services.DesechoGestorTransporteFacade;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.GestorDesechosDisposicionFinalFacade;
import ec.gob.ambiente.retce.services.GestorDesechosEliminacionDesechoNoPeligrosoFacade;
import ec.gob.ambiente.retce.services.GestorDesechosEliminacionDesechoPeligrosoFacade;
import ec.gob.ambiente.retce.services.GestorDesechosEliminacionFacade;
import ec.gob.ambiente.retce.services.GestorDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoAprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoCambioDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoDesechoFasesFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.ManifiestoUnicoFacade;
import ec.gob.ambiente.retce.services.ManifiestoUnicoTransferenciaFacade;
import ec.gob.ambiente.retce.services.RutaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.DesechoPeligrosoTransporteFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.EliminacionDesechoFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadDisposicionFinalFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadesFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.desechoPeligroso.facade.DesechoPeligrosoFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicosModalidad;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionRecepcion;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;


@ManagedBean
@ViewScoped
public class GestorDesechosPeligrososVerController {
	
	private static final Logger LOG = Logger.getLogger(GestorDesechosPeligrososVerController.class);
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
	/*EJBs*/
	@EJB
	private CrudServiceBean crudServiceBean;
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    @EJB
    private DetalleCatalogoGeneralFacade detalleCatalogGeneralFacade;
    @EJB
    private InformacionProyectoFacade informacionProyectoFacade;
    @EJB
    private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;
    @EJB
    private DesechoPeligrosoFacade desechoPeligrosoFacade;
    @EJB
	private ModalidadesFacade modalidadesFacade;
    @EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private InformacionProyectoDesechoFasesFacade informacionProyectoDesechoFasesFacade;
	@EJB
	private InformacionProyectoAprobacionRequisitosTecnicosFacade informacionProyectoAprobacionRequisitosTecnicosFacade;
    
    // FACADES GENERALES
    @EJB
	private GestorDesechosPeligrososFacade gestorDesechosPeligrososFacade;
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
	private ObservacionesFacade observacionesFacade;

    // FACADES PARA TRANSPORTE
    @EJB
	private RutaFacade rutaFacade;
    @EJB
    private ManifiestoUnicoFacade manifiestoUnicoFacade;
    @EJB
    private ManifiestoUnicoTransferenciaFacade manifiestoUnicoTransferenciaFacade;
    @EJB
    private DesechoPeligrosoTransporteFacade desechoPeligrosoTransporteFacade;
    @EJB
    private DesechoGestorTransporteFacade desechoGestorTransporteFacade;
    @EJB
    private DesechoGestorTransporteExportacionFacade desechoGestorTransporteExportacionFacade;
    
    
    
    // FACADES PARA ALMACENAMIENTO
    @EJB
    private InformacionProyectoDesechosPeligrososFacade informacionProyectoDesechosPeligrososFacade;
    @EJB
    private DesechoGestorAlmacenamientoFacade desechoGestorAlmacenamientoFacade;
    @EJB
    private DesechoGestorAlmacenamientoExportacionFacade desechoGestorAlmacenamientoExportacionFacade;
    @EJB
    private InformacionProyectoCambioDesechosPeligrososFacade informacionProyectoCambioDesechosPeligrososFacade;
    
    
    /*List*/
    List<Integer> listART, listFases;
    @Setter
	@Getter
	private List<GestorDesechosPeligrosos> listGestorDesechosPeligrosos;
    @Getter
	private List<String> listaAniosMeta;
    @Setter
	@Getter
	private List<DetalleCatalogoGeneral> listFaseGestion, listUnidad, listAlmacenamiento;
    @Setter
    @Getter
    private List<AprobacionRequisitosTecnicos> listAprobacionRequisitosTecnicos;
    @Setter
    @Getter
    private List<AprobacionRequisitosTecnicosModalidad> listAprobacionRequisitosTecnicosModalidad;
    @Setter
	@Getter
	private List<UbicacionesGeografica> listUbicacionesGeografica;
    @Setter
    @Getter
    private List<ResumenManifiesto> listTotalManifiestoTransporte, listTotalManifiestoAlmacenamiento, listTotalManifiestoEliminacion, listTotalManifiestoDisposicion;
	
    
    // LIST PARA TRANSPORTE
    @Setter
	@Getter
	private List<Ruta> listRutas;
    @Setter
	@Getter
	private List<ManifiestoUnico> listManifiestoUnicoTransporte;
    @Setter
    @Getter
    private List<DesechoGestorTransporte> listDesechoGestorTransporte;
    @Setter
	@Getter
	private List<DesechoPeligrosoTransporteRETCE> listDesechoPeligrosoTransporte;
    @Setter
	@Getter
	private List<DesechoPeligroso> listDesechosTransporte = new ArrayList<DesechoPeligroso>();
    @Setter
    @Getter
    private List<ManifiestoUnicoTransferencia> listEmpresasTransporte;
    @Setter
    @Getter
	private List<SedePrestadorServiciosDesechos> listSedePrestadorServiciosDesechos, listSedePrestadorServiciosDesechosAlmacenamiento;
    @Getter
	@Setter
	private List<DesechoGestorTransporteExportacion> listDesechoGestorTransporteExportacion;
    @Setter
    @Getter
    private List<ObservacionesFormularios> listObservacionesAlmacenamiento = new ArrayList<ObservacionesFormularios>();
    @Setter
    @Getter
    private List<ObservacionesFormularios> listObservacionesTransporte = new ArrayList<ObservacionesFormularios>();
    @Setter
    @Getter
    private List<ObservacionesFormularios> listObservacionesEliminacion = new ArrayList<ObservacionesFormularios>();
    @Setter
    @Getter
    private List<ObservacionesFormularios> listObservacionesDisposicionFinal = new ArrayList<ObservacionesFormularios>();
    
    // LIST PARA ALMACENAMIENTO
    @Setter
	@Getter
	private List<ManifiestoUnico> listManifiestoRecepcion, listManifiestoTransferencia;
    @Setter
	@Getter
    private DetalleCatalogoGeneral[] tipoAlmacenamientoSeleccionada;
    @Setter
	@Getter
	private List<DesechoPeligroso> listDesechosAlmacenamiento = new ArrayList<DesechoPeligroso>();
    @Setter
	@Getter
    private List<InformacionProyectoDesechosPeligrosos> listInformacionProyectoDesechosPeligrosos;
    @Setter
    @Getter
    private List<ManifiestoUnicoTransferencia> listEmpresasAlmacenamientoRecepcion, listEmpresasAlmacenamientoRecepcionEliminar, listEmpresasAlmacenamientoTransferencia;
    @Setter
    @Getter
    private List<DesechoGestorAlmacenamiento> listDesechoGestorAlmacenamientoRecepcion, listDesechoGestorAlmacenamientoRecepcionEliminar, listDesechoGestorAlmacenamientoTransferencia;
    @Setter
    @Getter
    private List<DesechoGestorAlmacenamientoExportacion> listDesechoGestorAlmacenamientoExportacion;

    // LIST PARA ELIMINACION
    @Setter
    @Getter
    private List<GestorDesechosEliminacion> listGestorDesechosEliminacion;
//    listDesechosDisposicionFinal;
    
    /*Object*/
    @Setter
    @Getter
    private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;
    @Setter
	@Getter
	private GestorDesechosPeligrosos gestorDesechosPeligrosos;
    @Getter
	@Setter
	private InformacionProyecto informacionProyecto;
    @Getter
    @Setter
    private TecnicoResponsable tecnicoResponsable = new TecnicoResponsable();
    
    // OBJETOS PARA TRANSPORTE
    @Setter
    @Getter
	private DesechoPeligrosoTransporteRETCE desechoPeligrosoTransporteRETCE;
    
    // OBJETOS PARA ALMACENAMIENTO
    
    
    /*String*/
    @Getter       
    private String nombreUsuario,representanteLegal;
    
    /*Integer*/
    @Getter
    @Setter
    private Integer idAprobacionRequisitosTecnicos;
    
    /*Double*/
    @Getter
	@Setter
	private Double factorKgT;
    
	/*Boolean*/
    @Setter
    @Getter
    private boolean tabAlmacenamiento, tabTransporte, tabEliminacion, tabDisposicion = false;
    @Setter
    @Getter
    private boolean verFormulario = false;
    
    
    //  ----WR
    @EJB
    private EliminacionDesechoFacade eliminacionDesechoFacade;
    @EJB
	private CatalogoSustanciasRetceFacade catalogoSustanciasRetceFacade;
    @EJB
    private GestorDesechosEliminacionFacade gestorDesechosEliminacioFacade;
    @EJB
    private GestorDesechosDisposicionFinalFacade gestorDesechosDisposicionFinalFacade;
    @EJB
    private GestorDesechosEliminacionDesechoPeligrosoFacade gestorDesechosEliminacionDesechoPeligrosoFacade;
    @EJB
    private GestorDesechosEliminacionDesechoNoPeligrosoFacade gestorDesechosEliminacionDesechoNoPeligrosoFacade;
    @Setter
    @Getter
    private EliminacionDesechosBean eliminacionDesechosBean = new EliminacionDesechosBean();
    @Setter
	@Getter
	private List<EliminacionRecepcion> seleccionadosEliminaciones;
    @Setter
	@Getter
    private boolean desechoES0406;
    @Setter
	@Getter
    private boolean adjuntaArchivoCalculo;
    @Setter
	@Getter
    private Integer generaTipoResiduo=0;
    @Setter
	@Getter
	private List<EliminacionRecepcion> eliminacionesRecepciones;
    @Getter
	private List<CatalogoSustanciasRetce> catalogoSustanciasRetceList;
    @Setter
	@Getter
	private  SubstanciasRetce sustanciasRetce= new SubstanciasRetce();
    @Setter
	@Getter
	private UploadedFile fileAdjuntoCalculo;
    @Setter
	@Getter
    private String metodoEstimacion;
    @Setter
    @Getter
    private GestorDesechosEliminacion gestorDesechosEliminacion = new GestorDesechosEliminacion();
    @Setter
	@Getter
    private GestorDesechosEliminacionDesechoPeligroso gestorDesechosEliminacionDesechoPeligroso = new GestorDesechosEliminacionDesechoPeligroso();
    @Setter
	@Getter
    private GestorDesechosEliminacionDesechoNoPeligroso gestorDesechosEliminacionDesechoNoPeligroso = new GestorDesechosEliminacionDesechoNoPeligroso();
    @Setter
	@Getter
    private List<GestorDesechosEliminacionDesechoNoPeligroso> listGestorDesechosEliminacionDesechoNoPeligroso;
    @Setter
	@Getter
    private List<GestorDesechosEliminacionDesechoPeligroso> listGestorDesechosEliminacionDesechoPeligroso;
    
    @EJB
	private ModalidadDisposicionFinalFacade modalidadDisposicionFinalFacade;
    @Setter
	@Getter
	private List<GestorDesechosDisposicionFinal> listaDesechosDisFinal = new ArrayList<GestorDesechosDisposicionFinal>();
    @Setter
	@Getter
	private GestorDesechosDisposicionFinal desechoDisFinalSeleccionado = new GestorDesechosDisposicionFinal();
    @Setter
	@Getter
    Documento documentoCalculo;
    
    //  ---
    
    
    /*CONSTANTES*/
    public static final String ALMACENAMIENTO = "fasegestion.almacenamiento";
    public static final String TRANSPORTE = "fasegestion.transporte";
    public static final String ELIMINACION = "fasegestion.eliminacion";
    public static final String DISPOCISION_FINAL = "fasegestion.disposicionfinal";
    public static final int FASE_ALMACENAMIENTO = 1;
	public static final int FASE_TRANSPORTE = 2;
	public static final int FASE_ELIMINACION = 3;
	public static final int FASE_DISPOSICION_FINAL = 4;
	public static final String MANIFIESTO_RECEPCION = "almacenamiento.recepcion";
	public static final String MANIFIESTO_TRANSFERENCIA = "almacenamiento.transferencia";
	public static final String LISTA_TIPO_ALMACENAMIENTO = "almacenamiento.tipo_almacenamiento";
    
	@Setter
	@Getter
    Boolean verObservaciones = true;
	
    @PostConstruct
	public void init() {
    	List<Integer> listART = new ArrayList<Integer>();
    	buscarDatosOperador();
    	
    	gestorDesechosPeligrosos = new GestorDesechosPeligrosos();
    	tecnicoResponsable = new TecnicoResponsable();
    	
    	if(JsfUtil.getCurrentTask() != null) {
			String codigoGestor = JsfUtil.getCurrentTask().getVariable("tramite").toString();
			gestorDesechosPeligrosos = gestorDesechosPeligrososFacade.getByCode(codigoGestor);
		} else {
			verObservaciones = false;
			Integer idGestorDesechosPeligrosos = (Integer)(JsfUtil.devolverObjetoSession("idGestorDesechosPeligrosos"));
			JsfUtil.cargarObjetoSession(GestorDesechosPeligrosos.class.getSimpleName(), null);
			if(idGestorDesechosPeligrosos != null)
				gestorDesechosPeligrosos = gestorDesechosPeligrososFacade.getById(idGestorDesechosPeligrosos);
		}
    	
    	if (gestorDesechosPeligrosos.getEnviado() != null && gestorDesechosPeligrosos.getEnviado()) {
    		List<ObservacionesFormularios> listObservacionesFormularios = cargarObservaciones();
    		listObservacionesAlmacenamiento = asignarObservaciones(listObservacionesFormularios, "GestorDesechosPeligrososAlmacenamiento");
    		listObservacionesTransporte = asignarObservaciones(listObservacionesFormularios, "GestorDesechosPeligrososTransporte");
    		listObservacionesEliminacion = asignarObservaciones(listObservacionesFormularios, "GestorDesechosPeligrososEliminacion");
    		listObservacionesDisposicionFinal = asignarObservaciones(listObservacionesFormularios, "GestorDesechosPeligrososDisposicionFinal");
		}
    	tecnicoResponsable = gestorDesechosPeligrosos.getTecnicoResponsable();
    	
    	if (gestorDesechosPeligrosos.getFaseAlmacenamiento()) {
    		resetFaseAlmacenamiento();
    		tabAlmacenamiento = true;
			cargarDatosFaseAlmacenamiento();
		}
    	if (gestorDesechosPeligrosos.getFaseTransporte()) {
    		resetFaseTransporte();
    		tabTransporte = true;
			cargarDatosFaseTransporte();
		}
    	if (gestorDesechosPeligrosos.getFaseEliminacion()) {
    		resetFaseEliminacion();
    		tabEliminacion = true;
    		cargarDatosFaseEliminacion();
		}
    	if (gestorDesechosPeligrosos.getFaseDisposicionFinal()) {
    		resetFaseDisposicion();
    		tabDisposicion = true;
    		cargarDatosFaseDisposicion();
		}
	}

    private void buscarDatosOperador() {
		Usuario user = loginBean.getUsuario();
		Organizacion orga = null;
		try {
			orga = organizacionFacade.buscarPorRuc(user.getNombre());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (orga != null) {
			nombreUsuario = orga.getNombre();
			representanteLegal = orga.getPersona().getNombre();
		} else {
			nombreUsuario = user.getPersona().getNombre();
		}
	}
    // Carga las observaciones
    private List<ObservacionesFormularios> cargarObservaciones() {
    	List<ObservacionesFormularios> observacionesTramite = new ArrayList<ObservacionesFormularios>();
		try {
			observacionesTramite = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
					gestorDesechosPeligrosos.getId(), GestorDesechosPeligrosos.class.getSimpleName());
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Error al cargar las observaciones. Por favor comuníquese con Mesa de Ayuda");
			LOG.error(e);
		}
    	return observacionesTramite;
    }
    private List<ObservacionesFormularios> asignarObservaciones(List<ObservacionesFormularios> allObservaciones, String tipo) {
    	List<ObservacionesFormularios> listObservacionesPorTipo = new ArrayList<ObservacionesFormularios>();
    	if (allObservaciones.size() > 0) {
			for (ObservacionesFormularios observaciones : allObservaciones) {
				if (!observaciones.isObservacionCorregida()) {
					if (observaciones.getSeccionFormulario().equals(tipo)) {
						listObservacionesPorTipo.add(observaciones);
					}
				}
			}
		}
    	return listObservacionesPorTipo;
    }
    
    // Resets fases
    private void resetFaseTransporte() {
    	listRutas = new ArrayList<Ruta>();
    	listManifiestoUnicoTransporte = new ArrayList<ManifiestoUnico>();
    	listEmpresasTransporte = new ArrayList<ManifiestoUnicoTransferencia>();
    	listDesechoGestorTransporte = new ArrayList<DesechoGestorTransporte>();
    	listTotalManifiestoTransporte = new ArrayList<ResumenManifiesto>();
    	listDesechoGestorTransporteExportacion = new ArrayList<DesechoGestorTransporteExportacion>();
    }
    private void resetFaseAlmacenamiento() {
    	listManifiestoRecepcion = new ArrayList<ManifiestoUnico>();
    	listDesechoGestorAlmacenamientoRecepcion = new ArrayList<DesechoGestorAlmacenamiento>();
    	listEmpresasAlmacenamientoRecepcion = new ArrayList<ManifiestoUnicoTransferencia>();
    	listTotalManifiestoAlmacenamiento = new ArrayList<ResumenManifiesto>();
    	listDesechoGestorAlmacenamientoExportacion = new ArrayList<DesechoGestorAlmacenamientoExportacion>();

    	listManifiestoTransferencia = new ArrayList<ManifiestoUnico>();
    	listDesechoGestorAlmacenamientoTransferencia = new ArrayList<DesechoGestorAlmacenamiento>();
        listEmpresasAlmacenamientoTransferencia = new ArrayList<ManifiestoUnicoTransferencia>();
    }
    private void resetFaseEliminacion() {
    	listGestorDesechosEliminacion = new ArrayList<GestorDesechosEliminacion>();
    	listGestorDesechosEliminacionDesechoPeligroso = new ArrayList<GestorDesechosEliminacionDesechoPeligroso>();
    	listGestorDesechosEliminacionDesechoNoPeligroso = new ArrayList<GestorDesechosEliminacionDesechoNoPeligroso>();
    	listTotalManifiestoEliminacion = new ArrayList<ResumenManifiesto>();
    }
    private void resetFaseDisposicion() {
    	listaDesechosDisFinal = new ArrayList<GestorDesechosDisposicionFinal>();
    	listTotalManifiestoDisposicion = new ArrayList<ResumenManifiesto>();
    }
    
    public StreamedContent getStreamedContentDocumento(Documento documento) {
		try {
			if(documento.getContenidoDocumento()==null && documento.getId()!=null)
				documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
			return UtilDocumento.getStreamedContent(documento);
		} catch (CmisAlfrescoException e) {
			LOG.error(e);
			e.printStackTrace();
			return null;
		}
	}

// ------------------------------------------- FASE DE TRANSPORTE -------------------------------------------
    public void cargarDatosFaseTransporte() {
    	try {
	    	Integer idGestorDesechoPeligroso = gestorDesechosPeligrosos.getId();
	    	// Listado de rutas
	    	listRutas = rutaFacade.getByGestorDesechoPeligroso(idGestorDesechoPeligroso);
	    	if (listRutas.size() > 0) {
	    		for (Ruta rowRuta : listRutas) {
					String origen = ubicacionGeograficaFacade.buscarPorId(rowRuta.getProvinciaOrigen()).getNombre();
	        		String destino = ubicacionGeograficaFacade.buscarPorId(rowRuta.getProvinciaDestino()).getNombre();
					rowRuta.setNombreProvinciaOrigen(origen);
					rowRuta.setNombreProvinciaDestino(destino);
				}
			}
	    	// Listado Manifiesto Transporte
	    	DetalleCatalogoGeneral faseTransporte = detalleCatalogGeneralFacade.findByCodigo(TRANSPORTE);
	    	listManifiestoUnicoTransporte = manifiestoUnicoFacade.getByGestorFase(idGestorDesechoPeligroso, faseTransporte.getId());
	    	if (listManifiestoUnicoTransporte.size() > 0) {
	    		for (ManifiestoUnico rowManifiesto : listManifiestoUnicoTransporte) {
	    			Integer idManifiestoUnico = rowManifiesto.getId();
	    			Documento documentoTransporte = new Documento();
	    			documentoTransporte = documentosFacade.documentoXTablaIdXIdDocUnico(idManifiestoUnico,ManifiestoUnico.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO);
	    			rowManifiesto.setAdjuntoManifiesto(documentoTransporte);
	    			// Listado Empresa Gestora Transporte
	    			listEmpresasTransporte = manifiestoUnicoTransferenciaFacade.getByManifiestoUnico(idManifiestoUnico);
                    rowManifiesto.listEmpresasTransferencia = listEmpresasTransporte;
	    			// Listado Desechos Peligrosos Transporte
                    listDesechoGestorTransporte = desechoGestorTransporteFacade.getByManifiestoUnico(idManifiestoUnico);
                    if (listDesechoGestorTransporte.size() > 0) {
                        rowManifiesto.listDesechoTransporte = listDesechoGestorTransporte;
                        for (DesechoGestorTransporte rowAsignar : listDesechoGestorTransporte) {
                            rowAsignar.setClave(rowAsignar.getDesechoPeligroso().getClave());
                            if (rowAsignar.getUnidadDesechoTransporte().getDescripcion().equals("t")) {
                                rowAsignar.setCantidad(rowAsignar.getCantidadToneladas());
                            } else {
                                rowAsignar.setCantidad(rowAsignar.getCantidadKilogramos());
                            }
                            DesechoGestorAlmacenamientoExportacion almacenamientoExportacion = desechoGestorAlmacenamientoExportacionFacade.getByDesechoGestorAlmacenamiento(rowAsignar.getId());
                            if (almacenamientoExportacion != null) {
                                Documento documentoAlmacenamientoAutorizacion = new Documento();
                                documentoAlmacenamientoAutorizacion = documentosFacade.documentoXTablaIdXIdDocUnico(almacenamientoExportacion.getId(),DesechoGestorAlmacenamientoExportacion.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION);
                                if (documentoAlmacenamientoAutorizacion != null) {
                                    almacenamientoExportacion.setDocumentoAutorizacion(documentoAlmacenamientoAutorizacion);
                                }
                                
                                listDesechoGestorAlmacenamientoExportacion.add(almacenamientoExportacion);
                            }
                        }
                    }
	    		}
	    	}
    	} catch (ServiceException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }

    // Calculo de sumatoria de desechos transporte
    public List<ResumenManifiesto> calcularSumatoriaTotal(List<ManifiestoUnico> listaManifiestosFinal, List<DesechoGestorTransporte> ListManifiestoDesechos) {
		List<DesechoGestorTransporte> listDesechosFinal = new ArrayList<>();
		List<ResumenManifiesto> listaResumen = new ArrayList<>();
		if(listaManifiestosFinal != null && listaManifiestosFinal.size() > 0){
			listDesechosFinal.addAll(ListManifiestoDesechos);
			for (DesechoGestorTransporte manifiestoDesecho : listDesechosFinal) {
				Boolean enResumen = false;
				for(ResumenManifiesto resumen : listaResumen){
					if(resumen.getDesechoPeligroso().getId().equals(manifiestoDesecho.getDesechoPeligroso().getId())){
						enResumen = true;
						Double total = resumen.getTotalDesecho() + manifiestoDesecho.getCantidadToneladas();
						resumen.setTotalDesecho(total);
						if (resumen.getDesechoPeligroso().getClave().equals("ES-04") || resumen.getDesechoPeligroso().getClave().equals("ES-06")) {
							Double totalUnidadesES = resumen.getTotalDesecho() + manifiestoDesecho.getCantidadToneladas();
							resumen.setTotalUnidadesES(totalUnidadesES);
						}
						break;
					}
				}
				
				if (!enResumen) {
					ResumenManifiesto newResumen = new ResumenManifiesto();
					newResumen.setDesechoPeligroso(manifiestoDesecho.getDesechoPeligroso());
					newResumen.setTotalDesecho(manifiestoDesecho.getCantidadToneladas());
					listaResumen.add(newResumen);
				}
			}
		}
		return listaResumen;
	}
    
// ------------------------------------------- FASE DE ALMACENAMIENTO -------------------------------------------
    // Calculo de sumatoria de desechos almacenamiento
    public List<ResumenManifiesto> calcularSumatoriaAlmacenamientoTotal(List<ManifiestoUnico> listaManifiestosFinal, List<DesechoGestorAlmacenamiento> ListManifiestoDesechos) {
		List<DesechoGestorAlmacenamiento> listDesechosFinal = new ArrayList<>();
		List<ResumenManifiesto> listaResumen = new ArrayList<>();
		if(listaManifiestosFinal != null && listaManifiestosFinal.size() > 0){
			listDesechosFinal.addAll(ListManifiestoDesechos);
			for (DesechoGestorAlmacenamiento manifiestoDesecho : listDesechosFinal) {
				Boolean enResumen = false;
				for(ResumenManifiesto resumen : listaResumen){
					if(resumen.getDesechoPeligroso().getId().equals(manifiestoDesecho.getDesechoPeligroso().getId())){
						enResumen = true;
						Double total = resumen.getTotalDesecho() + manifiestoDesecho.getCantidadToneladas();
						resumen.setTotalDesecho(total);
						if (resumen.getDesechoPeligroso().getClave().equals("ES-04") || resumen.getDesechoPeligroso().getClave().equals("ES-06")) {
							Double totalUnidadesES = resumen.getTotalDesecho() + manifiestoDesecho.getCantidadToneladas();
							resumen.setTotalUnidadesES(totalUnidadesES);
						}
						break;
					}
				}
				
				if (!enResumen) {
					ResumenManifiesto newResumen = new ResumenManifiesto();
					newResumen.setDesechoPeligroso(manifiestoDesecho.getDesechoPeligroso());
					newResumen.setTotalDesecho(manifiestoDesecho.getCantidadToneladas());
					listaResumen.add(newResumen);
				}
			}
		}
		return listaResumen;
	}
    
    private void cargarDatosFaseAlmacenamiento() {
    	try {
    		DetalleCatalogoGeneral faseAlmacenamiento = detalleCatalogGeneralFacade.findByCodigo(ALMACENAMIENTO);
    		DetalleCatalogoGeneral tipoRecepcion = detalleCatalogGeneralFacade.findByCodigo(MANIFIESTO_RECEPCION);
    		Integer idGestorDesechoPeligroso = gestorDesechosPeligrosos.getId();
    		// Listado Manifiesto Recepcion
    		listManifiestoRecepcion = manifiestoUnicoFacade.getByGestorFaseTipo(idGestorDesechoPeligroso, faseAlmacenamiento.getId(), tipoRecepcion.getId());
	    	if (listManifiestoRecepcion.size() > 0) {
	    		for (ManifiestoUnico rowManifiesto : listManifiestoRecepcion) {
	    			Integer idManifiestoUnico = rowManifiesto.getId();
	    			Documento documentoRecepcion = new Documento();
	    			documentoRecepcion = documentosFacade.documentoXTablaIdXIdDocUnico(idManifiestoUnico,ManifiestoUnico.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO);
	    			rowManifiesto.setAdjuntoManifiesto(documentoRecepcion);
	    			// Listado Empresa Gestora Almacenamiento
	    			listEmpresasAlmacenamientoRecepcion = manifiestoUnicoTransferenciaFacade.getByManifiestoUnico(idManifiestoUnico);
	    			rowManifiesto.listEmpresasRecepcion = listEmpresasAlmacenamientoRecepcion;
	    			// Listado Desechos Peligrosos Almacenamiento
	    			listDesechoGestorAlmacenamientoRecepcion = desechoGestorAlmacenamientoFacade.getByManifiestoUnico(idManifiestoUnico);
	    			if (listDesechoGestorAlmacenamientoRecepcion.size() > 0) {
	    				rowManifiesto.listDesechoRecepcion = listDesechoGestorAlmacenamientoRecepcion;
	    				listTotalManifiestoAlmacenamiento = calcularSumatoriaAlmacenamientoTotal(listManifiestoRecepcion, listDesechoGestorAlmacenamientoRecepcion);
	    				for (DesechoGestorAlmacenamiento rowAsignar : listDesechoGestorAlmacenamientoRecepcion) {
	    					rowAsignar.setClave(rowAsignar.getDesechoPeligroso().getClave());
	    					if (rowAsignar.getUnidadDesechoRecepcion().getDescripcion().equals("t")) {
	    						rowAsignar.setCantidad(rowAsignar.getCantidadToneladas());
	    					} else {
	    						rowAsignar.setCantidad(rowAsignar.getCantidadKilogramos());
	    					}
	    					Integer idDesechoGestorAlmacenamiento = rowAsignar.getId(); 
	    					DesechoGestorAlmacenamientoExportacion almacenamientoExportacion = desechoGestorAlmacenamientoExportacionFacade.getByDesechoGestorAlmacenamiento(idDesechoGestorAlmacenamiento);
	    					if (almacenamientoExportacion != null) {
	    						Documento documentoAlmacenamientoNotificacion = new Documento();
	    						documentoAlmacenamientoNotificacion = documentosFacade.documentoXTablaIdXIdDocUnico(almacenamientoExportacion.getId(),DesechoGestorAlmacenamientoExportacion.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_EXPORTACION_NOTIFICACION);
	    						if (documentoAlmacenamientoNotificacion != null) {
	    							almacenamientoExportacion.setDocumentoNotificacion(documentoAlmacenamientoNotificacion);
	    						}
	    						
	    						Documento documentoAlmacenamientoAutorizacion = new Documento();
	    						documentoAlmacenamientoAutorizacion = documentosFacade.documentoXTablaIdXIdDocUnico(almacenamientoExportacion.getId(),DesechoGestorAlmacenamientoExportacion.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION);
	    						if (documentoAlmacenamientoAutorizacion != null) {
	    							almacenamientoExportacion.setDocumentoAutorizacion(documentoAlmacenamientoAutorizacion);
	    						}
	    						
	    						Documento documentoAlmacenamientoMovimiento = new Documento();
	    						documentoAlmacenamientoMovimiento = documentosFacade.documentoXTablaIdXIdDocUnico(almacenamientoExportacion.getId(),DesechoGestorAlmacenamientoExportacion.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_EXPORTACION_MOVIMIENTOS);
	    						if (documentoAlmacenamientoMovimiento != null) {
	    							almacenamientoExportacion.setDocumentoMovimiento(documentoAlmacenamientoMovimiento);
	    						}
	    						
	    						Documento documentoAlmacenamientoDestruccion = new Documento();
	    						documentoAlmacenamientoDestruccion = documentosFacade.documentoXTablaIdXIdDocUnico(almacenamientoExportacion.getId(),DesechoGestorAlmacenamientoExportacion.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_EXPORTACION_DESTRUCCION);
	    						if (documentoAlmacenamientoDestruccion != null) {
	    							almacenamientoExportacion.setDocumentoDestruccion(documentoAlmacenamientoDestruccion);
	    						}
	    						
	    						listDesechoGestorAlmacenamientoExportacion.add(almacenamientoExportacion);
							}
	    				}
	    			}
	    		}
	    	}
	    	// Listado Manifiesto Transferencia
	    	DetalleCatalogoGeneral tipoTransferencia = detalleCatalogGeneralFacade.findByCodigo(MANIFIESTO_TRANSFERENCIA);
    		listManifiestoTransferencia = manifiestoUnicoFacade.getByGestorFaseTipo(idGestorDesechoPeligroso, faseAlmacenamiento.getId(), tipoTransferencia.getId());
    		if (listManifiestoTransferencia.size() > 0) {
    			for (ManifiestoUnico rowManifiesto : listManifiestoTransferencia) {
	    			Integer idManifiestoUnico = rowManifiesto.getId();
	    			Documento documentoRecepcion = new Documento();
	    			documentoRecepcion = documentosFacade.documentoXTablaIdXIdDocUnico(idManifiestoUnico,ManifiestoUnico.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO);
	    			rowManifiesto.setAdjuntoManifiesto(documentoRecepcion);
	    			// Listado Empresa Gestora Almacenamiento
	    			listEmpresasAlmacenamientoTransferencia = manifiestoUnicoTransferenciaFacade.getByManifiestoUnico(idManifiestoUnico);
	    			rowManifiesto.listEmpresasTransferencia = listEmpresasAlmacenamientoTransferencia;
	    			// Listado Desechos Peligrosos Almacenamiento
					listDesechoGestorAlmacenamientoTransferencia = desechoGestorAlmacenamientoFacade.getByManifiestoUnico(idManifiestoUnico);
	    			if (listDesechoGestorAlmacenamientoTransferencia.size() > 0) {
	    				rowManifiesto.listDesechoTransferencia = listDesechoGestorAlmacenamientoTransferencia;
	    				for (DesechoGestorAlmacenamiento rowAsignar : listDesechoGestorAlmacenamientoTransferencia) {
	    					rowAsignar.setClave(rowAsignar.getDesechoPeligroso().getClave());
	    					if (rowAsignar.getUnidadDesechoRecepcion().getDescripcion().equals("t")) {
	    						rowAsignar.setCantidad(rowAsignar.getCantidadToneladas());
	    					} else {
	    						rowAsignar.setCantidad(rowAsignar.getCantidadKilogramos());
	    					}
	    					DesechoGestorAlmacenamientoExportacion almacenamientoExportacion = desechoGestorAlmacenamientoExportacionFacade.getByDesechoGestorAlmacenamiento(rowAsignar.getId());
	    					if (almacenamientoExportacion != null) {
	    						Documento documentoAlmacenamientoNotificacion = new Documento();
	    						documentoAlmacenamientoNotificacion = documentosFacade.documentoXTablaIdXIdDocUnico(almacenamientoExportacion.getId(),DesechoGestorAlmacenamientoExportacion.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_EXPORTACION_NOTIFICACION);
	    						if (documentoAlmacenamientoNotificacion != null) {
	    							almacenamientoExportacion.setDocumentoNotificacion(documentoAlmacenamientoNotificacion);
	    						}

	    						Documento documentoAlmacenamientoAutorizacion = new Documento();
	    						documentoAlmacenamientoAutorizacion = documentosFacade.documentoXTablaIdXIdDocUnico(almacenamientoExportacion.getId(),DesechoGestorAlmacenamientoExportacion.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION);
	    						if (documentoAlmacenamientoAutorizacion != null) {
	    							almacenamientoExportacion.setDocumentoAutorizacion(documentoAlmacenamientoAutorizacion);
	    						}
	    						
	    						Documento documentoAlmacenamientoMovimiento = new Documento();
	    						documentoAlmacenamientoMovimiento = documentosFacade.documentoXTablaIdXIdDocUnico(almacenamientoExportacion.getId(),DesechoGestorAlmacenamientoExportacion.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_EXPORTACION_MOVIMIENTOS);
	    						if (documentoAlmacenamientoMovimiento != null) {
	    							almacenamientoExportacion.setDocumentoMovimiento(documentoAlmacenamientoMovimiento);
	    						}
	    						
	    						Documento documentoAlmacenamientoDestruccion = new Documento();
	    						documentoAlmacenamientoDestruccion = documentosFacade.documentoXTablaIdXIdDocUnico(almacenamientoExportacion.getId(),DesechoGestorAlmacenamientoExportacion.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_EXPORTACION_DESTRUCCION);
	    						if (documentoAlmacenamientoDestruccion != null) {
	    							almacenamientoExportacion.setDocumentoDestruccion(documentoAlmacenamientoDestruccion);
	    						}

	    						listDesechoGestorAlmacenamientoExportacion.add(almacenamientoExportacion);
							}
	    				}
	    			}
	    		}
    		}
    	} catch (ServiceException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
    
// ------------------------------------------- FASE DE ELIMINACION -------------------------------------------
    private void cargarDatosFaseEliminacion() {
    	listGestorDesechosEliminacion = gestorDesechosEliminacioFacade.listarGestorEliminacion(gestorDesechosPeligrosos);
    	if (listGestorDesechosEliminacion != null) {
			for (GestorDesechosEliminacion row : listGestorDesechosEliminacion) {
				Integer idGestorDesechosEliminacion = row.getId();
				List<GestorDesechosEliminacionDesechoPeligroso> listGestorDesechosEliminacionDesechoPeligroso = gestorDesechosEliminacionDesechoPeligrosoFacade.getByDesechoEliminacion(idGestorDesechosEliminacion);
				if (listGestorDesechosEliminacionDesechoPeligroso.size() > 0) {
					row.setListGestorDesechosEliminacionDesechoPeligroso(listGestorDesechosEliminacionDesechoPeligroso);
				}
				List<GestorDesechosEliminacionDesechoNoPeligroso> listGestorDesechosEliminacionDesechoNoPeligroso = gestorDesechosEliminacionDesechoNoPeligrosoFacade.getByDesechoEliminacion(idGestorDesechosEliminacion);
				if (listGestorDesechosEliminacionDesechoNoPeligroso.size() > 0) {
					row.setListGestorDesechosEliminacionDesechoNoPeligroso(listGestorDesechosEliminacionDesechoNoPeligroso);
				}
				List<GestorDesechosEliminacion> listSustanciasRETCE = new ArrayList<GestorDesechosEliminacion>();
				if (row.getCatalogoSustancias() != null) {
					if (row.getDocuId() != null) {
						Documento documentoCalculo = documentosFacade.buscarDocumentoPorId(row.getDocuId());
						row.setAdjuntoCalculo(documentoCalculo);
					}
					listSustanciasRETCE.add(row);
				}
				if (listSustanciasRETCE.size() > 0) {
					row.setListGestorDesechosEliminacionSustanciasRETCE(listSustanciasRETCE);
				}
			}
			listTotalManifiestoEliminacion = calcularSumatoriaEliminacionTotal(listGestorDesechosEliminacion);
		}
    }
    public List<ResumenManifiesto> calcularSumatoriaEliminacionTotal(List<GestorDesechosEliminacion> listDesechosEliminacion) {
    	List<GestorDesechosEliminacion> listDesechosFinal = new ArrayList<>();
		List<ResumenManifiesto> listaResumen = new ArrayList<>();
    	listDesechosFinal.addAll(listDesechosEliminacion);
    	for (GestorDesechosEliminacion manifiestoDesecho : listDesechosFinal) {
    		Boolean enResumen = false;
    		for(ResumenManifiesto resumen : listaResumen){
    			if (manifiestoDesecho.getDesechoPeligroso() != null) {
    				if(resumen.getDesechoPeligroso().getId().equals(manifiestoDesecho.getDesechoPeligroso().getId())){
    					enResumen = true;
    					Double total = resumen.getTotalDesecho() + manifiestoDesecho.getCantidadTonelada();
    					resumen.setTotalDesecho(total);
    					if (resumen.getDesechoPeligroso().getClave().equals("ES-04") || resumen.getDesechoPeligroso().getClave().equals("ES-06")) {
    						Double totalUnidadesES = resumen.getTotalDesecho() + manifiestoDesecho.getCantidadTonelada();
    						resumen.setTotalUnidadesES(totalUnidadesES);
    					}
    					break;
    				}
				} else {
					enResumen = true;
				}
    		}
    		
    		if (!enResumen) {
    			ResumenManifiesto newResumen = new ResumenManifiesto();
    			newResumen.setDesechoPeligroso(manifiestoDesecho.getDesechoPeligroso());
    			newResumen.setTotalDesecho(manifiestoDesecho.getCantidadTonelada());
    			listaResumen.add(newResumen);
    		}
    	}
		return listaResumen;
	}
    
// ------------------------------------------- FASE DE DISPOSICION FINAL -------------------------------------------
    private void cargarDatosFaseDisposicion() {
    	listaDesechosDisFinal = gestorDesechosDisposicionFinalFacade.listarGestorDisfinal(gestorDesechosPeligrosos);
    	if (listaDesechosDisFinal != null) {
			for (GestorDesechosDisposicionFinal row : listaDesechosDisFinal) {
				List<GestorDesechosDisposicionFinal> listSustanciasRETCE = new ArrayList<GestorDesechosDisposicionFinal>();
				if (row.getCatalogoSustancias() != null) {
					if (row.getDocuId() != null) {
						Documento documentoCalculo = documentosFacade.buscarDocumentoPorId(row.getDocuId());
						row.setAdjuntoCalculo(documentoCalculo);
					}
					listSustanciasRETCE.add(row);
				}
				if (listSustanciasRETCE.size() > 0) {
					row.setListGestorDesechosEliminacionSustanciasRETCE(listSustanciasRETCE);
				}
			}
			listTotalManifiestoDisposicion = calcularSumatoriaDisposicionTotal(listaDesechosDisFinal);
		}
    }
    public List<ResumenManifiesto> calcularSumatoriaDisposicionTotal(List<GestorDesechosDisposicionFinal> listaDesechosDisposicion) {
    	List<GestorDesechosDisposicionFinal> listDesechosFinal = new ArrayList<>();
		List<ResumenManifiesto> listaResumen = new ArrayList<>();
    	listDesechosFinal.addAll(listaDesechosDisposicion);
    	for (GestorDesechosDisposicionFinal manifiestoDesecho : listDesechosFinal) {
    		Boolean enResumen = false;
    		for(ResumenManifiesto resumen : listaResumen){
    			if (manifiestoDesecho.getDesechoPeligroso() != null) {
    				if(resumen.getDesechoPeligroso().getId().equals(manifiestoDesecho.getDesechoPeligroso().getId())){
    					enResumen = true;
    					Double total = resumen.getTotalDesecho() + manifiestoDesecho.getCantidadTonelada();
    					resumen.setTotalDesecho(total);
    					if (resumen.getDesechoPeligroso().getClave().equals("ES-04") || resumen.getDesechoPeligroso().getClave().equals("ES-06")) {
    						Double totalUnidadesES = resumen.getTotalDesecho() + manifiestoDesecho.getCantidadTonelada();
    						resumen.setTotalUnidadesES(totalUnidadesES);
    					}
    					break;
    				}
				} else {
					enResumen = true;
				}
    		}
    		
    		if (!enResumen) {
    			ResumenManifiesto newResumen = new ResumenManifiesto();
    			newResumen.setDesechoPeligroso(manifiestoDesecho.getDesechoPeligroso());
    			newResumen.setTotalDesecho(manifiestoDesecho.getCantidadTonelada());
    			listaResumen.add(newResumen);
    		}
    	}
		return listaResumen;
    }

//    --WR----------
    
    public void fileUploadCalculo(FileUploadEvent event) {
    	documentoCalculo= new Documento();
    	fileAdjuntoCalculo = event.getFile();
		documentoCalculo=UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoCalculo.getContents(),fileAdjuntoCalculo.getFileName());
	}
    public void verAdjuntoCalculo()
    {
    	adjuntaArchivoCalculo=false;
    	if(gestorDesechosEliminacion.getMetodoEstimacion()==null)
    	{
    		adjuntaArchivoCalculo=false;
    		return;
    	}    	
    	if(!gestorDesechosEliminacion.getMetodoEstimacion().equals("Medición directa"))
    	{
    		documentoCalculo= new Documento();
    		adjuntaArchivoCalculo=true;
    		if(gestorDesechosEliminacion.getDocuId()!=null)
    		{
    			documentoCalculo = documentosFacade.buscarDocumentoPorId(gestorDesechosEliminacion.getDocuId());			
    		}
    	}
    		    		
    }
    
    
    public void verAdjuntoCalculoDisFinal()
    {
    	adjuntaArchivoCalculo=false;
    	if(desechoDisFinalSeleccionado.getMetodoEstimacion()==null)
    	{
    		adjuntaArchivoCalculo=false;
    		return;
    	}    	
    	if(!desechoDisFinalSeleccionado.getMetodoEstimacion().equals("Medición directa"))
    	{
    		documentoCalculo= new Documento();
    		adjuntaArchivoCalculo=true;
    		if(desechoDisFinalSeleccionado.getDocuId()!=null)
    		{
    			documentoCalculo = documentosFacade.buscarDocumentoPorId(desechoDisFinalSeleccionado.getDocuId());			
    		}
    	}	    		
    }
}