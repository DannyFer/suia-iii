package ec.gob.ambiente.control.retce.controllers;

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

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.EliminacionDesechosBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.retce.model.CatalogoSustanciasRetce;
import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.DesechoGestorAlmacenamiento;
import ec.gob.ambiente.retce.model.DesechoGestorAlmacenamientoExportacion;
import ec.gob.ambiente.retce.model.DesechoGestorTransporte;
import ec.gob.ambiente.retce.model.DesechoGestorTransporteExportacion;
import ec.gob.ambiente.retce.model.DesechoPeligrosoEliminacionRETCE;
import ec.gob.ambiente.retce.model.DesechoPeligrosoTransporteRETCE;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.GestorDesechosDisposicionFinal;
import ec.gob.ambiente.retce.model.GestorDesechosEliminacion;
import ec.gob.ambiente.retce.model.GestorDesechosEliminacionDesechoNoPeligroso;
import ec.gob.ambiente.retce.model.GestorDesechosEliminacionDesechoPeligroso;
import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.InformacionProyectoAprobacionRequisitosTecnicos;
import ec.gob.ambiente.retce.model.InformacionProyectoCambioDesechosPeligrosos;
import ec.gob.ambiente.retce.model.InformacionProyectoDesechoFases;
import ec.gob.ambiente.retce.model.InformacionProyectoDesechosPeligrosos;
import ec.gob.ambiente.retce.model.LaboratorioGeneral;
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
import ec.gob.ambiente.retce.services.LaboratorioGeneralFacade;
import ec.gob.ambiente.retce.services.ManifiestoUnicoFacade;
import ec.gob.ambiente.retce.services.ManifiestoUnicoTransferenciaFacade;
import ec.gob.ambiente.retce.services.RutaFacade;
import ec.gob.ambiente.retce.services.TecnicoResponsableFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.DesechoPeligrosoTransporteFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.EliminacionDesechoFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadDisposicionFinalFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadesFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.RecepcionDesechoPeligrosoFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.desechoPeligroso.facade.DesechoPeligrosoFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Laboratorio;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicosModalidad;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoPeligrosoTransporte;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionRecepcion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RecepcionDesechoPeligroso;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;


@ManagedBean
@ViewScoped
public class GestorDesechosPeligrososController {
	
	private static final Logger LOG = Logger.getLogger(GestorDesechosPeligrososController.class);
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
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
	@EJB
    private TecnicoResponsableFacade tecnicoResponsableFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ObservacionesFacade observacionesFacade;
    
    // FACADES GENERALES
    @EJB
	private GestorDesechosPeligrososFacade gestorDesechosPeligrososFacade;
    @EJB
    private DocumentosFacade documentosFacade;

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
    
    // FACADES PARA ELIMINACION
    @EJB
    private RecepcionDesechoPeligrosoFacade recepcionDesechoPeligrosoFacade;
    
    
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
    private List<ResumenManifiesto> listTotalManifiestoTransporte, listTotalManifiestoAlmacenamiento;
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
    @Setter
    @Getter
	private List<SedePrestadorServiciosDesechos> listSedePrestadorServiciosSelected;
    @Getter
	@Setter
	private List<DesechoGestorTransporteExportacion> listDesechoGestorTransporteExportacion;
    
    // LIST PARA ALMACENAMIENTO
    @Setter
	@Getter
	private List<ManifiestoUnico> listManifiestoRecepcion, listManifiestoTransferencia;
    @Setter
	@Getter
    private DetalleCatalogoGeneral[] tipoAlmacenamientoSeleccionada;
    @Setter
	@Getter
	private List<DesechoPeligroso> listDesechosAlmacenamientoRecepcion = new ArrayList<DesechoPeligroso>();
    @Setter
	@Getter
	private List<DesechoPeligroso> listDesechosAlmacenamientoTransferencia = new ArrayList<DesechoPeligroso>();
    @Setter
	@Getter
    private List<InformacionProyectoDesechosPeligrosos> listInformacionProyectoDesechosPeligrosos;
    @Setter
    @Getter
    private List<ManifiestoUnicoTransferencia> listEmpresasAlmacenamientoRecepcion, listEmpresasAlmacenamientoTransferencia;
    @Setter
    @Getter
    private List<DesechoGestorAlmacenamiento> listDesechoGestorAlmacenamientoRecepcion, listDesechoGestorAlmacenamientoTransferencia;
    @Setter
    @Getter
    private List<DesechoGestorAlmacenamientoExportacion> listDesechoGestorAlmacenamientoExportacion;
    
    // LIST PARA ELIMINACION
    @Setter
    @Getter
    private List<DesechoPeligrosoEliminacionRETCE> listDesechosEliminacion, listDesechosDisposicionFinal;
    
    /*Object*/
    @Setter
    @Getter
    private DetalleCatalogoGeneral detalleCatalogoGeneral;
    @Setter
    @Getter
    private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;
    @Setter
    @Getter
    private DesechoPeligroso desechoPeligroso;
    @Setter
	@Getter
	private GestorDesechosPeligrosos gestorDesechosPeligrosos;
    @Getter
	@Setter
	private InformacionProyecto informacionProyecto;
    @Getter
	@Setter
	private DesechoGestorTransporteExportacion desechoGestorTransporteExportacion;
    @Getter
	@Setter
	private TecnicoResponsable tecnicoResponsable = new TecnicoResponsable();
    
    // OBJETOS PARA TRANSPORTE
    @Getter
	@Setter
	private Ruta ruta = new Ruta();
    @Setter
	@Getter
	private ManifiestoUnico manifiestoUnico;
    @Setter
	@Getter
	private ManifiestoUnicoTransferencia manifiestoUnicoTransferencia;
    @Setter
    @Getter
	private Documento documentoManifiestoTransporte, documentoTransporteExportacionAutorizacion;
    @Setter
    @Getter
	private DesechoGestorTransporte desechoGestorTransporte;
    @Setter
    @Getter
	private DetalleCatalogoGeneral unidadDesechoTransporte;
    @Setter
    @Getter
	private DesechoPeligrosoTransporteRETCE desechoPeligrosoTransporteRETCE;
    
    // OBJETOS PARA ALMACENAMIENTO
    @Setter
    @Getter
	private Documento documentoManifiestoAlmacenamientoRecepcion, documentoManifiestoAlmacenamientoTransferencia;
    @Setter
    @Getter
	private DesechoGestorAlmacenamiento desechoGestorAlmacenamiento;
    @Setter
    @Getter
	private InformacionProyectoDesechosPeligrosos informacionProyectoDesechosPeligrosos;
    @Setter
    @Getter
	private SedePrestadorServiciosDesechos sedePrestadorServiciosDesechos;
    @Setter
    @Getter
	private DesechoGestorAlmacenamientoExportacion desechoGestorAlmacenamientoExportacion;
    @Setter
    @Getter
	private Documento documentoAlmacenamientoExportacionNotificacion, documentoAlmacenamientoExportacionAutorizacion, documentoAlmacenamientoExportacionMovimiento, documentoAlmacenamientoExportacionDestruccion;
    
    
    /*String*/
    @Getter       
    private String nombreUsuario,representanteLegal;
    @Getter
    @Setter
    private String provinciaDesde, provinciaHasta, solicitudAprobacionRequisitosTecnicos;
    
    /*Integer*/
    @Getter
    @Setter
    private Integer cantidadDesechoPeligroso, idAprobacionRequisitosTecnicos, anio;
    
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
    private boolean verFormulario, disabledCabecera, esDesechoES, esTransporteExportacion = false;
    @Setter
    @Getter
    private boolean esDesechoRecepcionES, esDesechoTransferenciaES, esDesechoExportacion = false;
    @Getter       
    private boolean habilitarCorreccion=false;
    @Getter       
    private boolean verObservaciones=false;
    
    
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
    private List<GestorDesechosEliminacion> listGestorDesechosEliminacion = new ArrayList<GestorDesechosEliminacion>();
    @Setter
	@Getter
    private GestorDesechosEliminacionDesechoPeligroso gestorDesechosEliminacionDesechoPeligroso = new GestorDesechosEliminacionDesechoPeligroso();
    @Setter
	@Getter
    private List<GestorDesechosEliminacionDesechoPeligroso> listarGestorDesechosEliminacionDesechoPeligroso = new ArrayList<GestorDesechosEliminacionDesechoPeligroso>();
    @Setter
	@Getter
    private GestorDesechosEliminacionDesechoNoPeligroso gestorDesechosEliminacionDesechoNoPeligroso = new GestorDesechosEliminacionDesechoNoPeligroso();
    @Setter
	@Getter
    private List<GestorDesechosEliminacionDesechoNoPeligroso> listarGestorDesechosEliminacionDesechoNoPeligroso = new ArrayList<GestorDesechosEliminacionDesechoNoPeligroso>();    
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
    Documento documentoCalculo, documentoInformeLaboratorio;
    @EJB
    LaboratorioGeneralFacade laboratorioGeneralFacade;
    @Setter
	@Getter
    private DatosLaboratorio datosLaboratorio = new DatosLaboratorio();
    @Setter
	@Getter
	private LaboratorioGeneral laboratorio = new LaboratorioGeneral();
    @Setter
	@Getter
    private boolean esMedicionDirecta;
    
    private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
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
    
    @PostConstruct
	public void init() {
    	List<Integer> listART = new ArrayList<Integer>();
    	buscarDatosOperador();
    	tecnicoResponsable = new TecnicoResponsable();
    	gestorDesechosPeligrosos = new GestorDesechosPeligrosos();
    	Integer idInformacionBasica = null;
    	JsfUtil.cargarObjetoSession(GestorDesechosPeligrosos.class.getSimpleName(), null);
    	Integer idGestorDesechosPeligrosos = (Integer)(JsfUtil.devolverObjetoSession("idGestorDesechosPeligrosos"));
    	
    	verObservaciones = false;
    	if(JsfUtil.getCurrentTask() != null) {
			String codigoGestor = JsfUtil.getCurrentTask().getVariable("tramite").toString();
			gestorDesechosPeligrosos = gestorDesechosPeligrososFacade.getByCode(codigoGestor);
			JsfUtil.cargarObjetoSession("idGestorDesechosPeligrosos", gestorDesechosPeligrosos.getId());
			if (gestorDesechosPeligrosos != null) {
				idInformacionBasica = gestorDesechosPeligrosos.getInformacionProyecto().getId();
				idGestorDesechosPeligrosos = gestorDesechosPeligrosos.getId();
			} else {
				idInformacionBasica = (Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName()));
			}
			listObservacionesAlmacenamiento = new ArrayList<ObservacionesFormularios>();
			listObservacionesTransporte = new ArrayList<ObservacionesFormularios>();
			listObservacionesEliminacion = new ArrayList<ObservacionesFormularios>();
			listObservacionesDisposicionFinal = new ArrayList<ObservacionesFormularios>();
			List<ObservacionesFormularios> listObservacionesFormularios = cargarObservaciones();
			if (listObservacionesFormularios.size() > 0) {
				for (ObservacionesFormularios observaciones : listObservacionesFormularios) {
					if (!observaciones.isObservacionCorregida()) {
						if (observaciones.getSeccionFormulario().equals("GestorDesechosPeligrososAlmacenamiento")) {
							listObservacionesAlmacenamiento.add(observaciones);
						}
						if (observaciones.getSeccionFormulario().equals("GestorDesechosPeligrososTransporte")) {
							listObservacionesTransporte.add(observaciones);
						}
						if (observaciones.getSeccionFormulario().equals("GestorDesechosPeligrososEliminacion")) {
							listObservacionesEliminacion.add(observaciones);
						}
						if (observaciones.getSeccionFormulario().equals("GestorDesechosPeligrososDisposicionFinal")) {
							listObservacionesDisposicionFinal.add(observaciones);
						}
					}
				}
			}
			verObservaciones = true;
			habilitarCorreccion=true;
		} else {
			idInformacionBasica = (Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName()));
			if(idGestorDesechosPeligrosos != null) {
				gestorDesechosPeligrosos = gestorDesechosPeligrososFacade.getById(idGestorDesechosPeligrosos);
				idInformacionBasica = gestorDesechosPeligrosos.getInformacionProyecto().getId();
			}
		}
    	
    	unidadDesechoTransporte = new DetalleCatalogoGeneral();
    	desechoPeligroso = new DesechoPeligroso();
    	setListUnidad(detalleCatalogGeneralFacade.findByCatalogoGeneralString("generador.tipo_unidad"));
    	factorKgT = Double.parseDouble(detalleCatalogGeneralFacade.findByCodigo("factor.kg_toneladas").getParametro());
    	if (idInformacionBasica != null) {
    		informacionProyecto = informacionProyectoFacade.findById(idInformacionBasica);
    		cargarListaAnios();
    		cargaDeclaraciones(idInformacionBasica);
    		if (idGestorDesechosPeligrosos != null) {
    			gestorDesechosPeligrosos = gestorDesechosPeligrososFacade.getById(idGestorDesechosPeligrosos);
    			tecnicoResponsable = asignarTecnicoReporta(gestorDesechosPeligrosos);
    			listART = cargarListaART(informacionProyecto);
    			if (listART.size() == 0) {
    				JsfUtil.addMessageInfo("No ha seleccionado ningún ART.");
    				JsfUtil.cargarObjetoSession("idGestorDesechosPeligrosos", null);
    				return;
				}
    			List<Integer> listFases = new ArrayList<Integer>();
    			listFases = cargarListaFases(informacionProyecto);
    			verFormulario = true;
    			DetalleCatalogoGeneral faseAlmacenamiento = detalleCatalogGeneralFacade.findByCodigo(ALMACENAMIENTO);
    			if (listFases.contains(faseAlmacenamiento.getId())) {
    				resetFaseAlmacenamiento();
					tabAlmacenamiento = true;
					cargarDatosFaseAlmacenamiento();
				}
    			DetalleCatalogoGeneral faseTransporte = detalleCatalogGeneralFacade.findByCodigo(TRANSPORTE);
    			if (listFases.contains(faseTransporte.getId())) {
    				resetFaseTransporte();
					tabTransporte = true;
					cargarDatosFaseTransporte();
				}
    			DetalleCatalogoGeneral faseEliminacion = detalleCatalogGeneralFacade.findByCodigo(ELIMINACION);
    			if (listFases.contains(faseEliminacion.getId())) {
    				cargarListaGestroDesechosEliminados();
    				resetFaseEliminacion();
					tabEliminacion = true;
				}
    			DetalleCatalogoGeneral faseDisposicion = detalleCatalogGeneralFacade.findByCodigo(DISPOCISION_FINAL);
    			if (listFases.contains(faseDisposicion.getId())) {
    				cargarListaGestroDisFinal();
    				resetFaseDisposicionFinal();
					tabDisposicion = true;
				}
    		}
    	} else {
    		return;
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
    public void agregarDeclaracion(){
    	Usuario userLogged=JsfUtil.getLoggedUser();
    	Integer idInformacionBasica = (Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName()));
    	informacionProyecto = informacionProyectoFacade.findById(idInformacionBasica);
    	gestorDesechosPeligrosos.setInformacionProyecto(informacionProyecto);
    	List<InformacionProyectoDesechoFases> listInformacionProyectoDesechoFases = new ArrayList<InformacionProyectoDesechoFases>();
    	listInformacionProyectoDesechoFases = informacionProyectoDesechoFasesFacade.findByProyecto(informacionProyecto);
    	TecnicoResponsable tecnico = new TecnicoResponsable(); 
    	if (listInformacionProyectoDesechoFases.size() > 0) {
    		gestorDesechosPeligrosos.setFaseAlmacenamiento(false);
    		gestorDesechosPeligrosos.setFaseTransporte(false);
    		gestorDesechosPeligrosos.setFaseEliminacion(false);
    		gestorDesechosPeligrosos.setFaseDisposicionFinal(false);
    		gestorDesechosPeligrosos.setArea(informacionProyecto.getAreaSeguimiento().getId());
    		tecnico = informacionProyecto.getTecnicoResponsable();
    		gestorDesechosPeligrosos.setTecnicoResponsable(tecnico);
			for (InformacionProyectoDesechoFases row : listInformacionProyectoDesechoFases) {
				if (row.getDesechoFases().getCodigo().equals(ALMACENAMIENTO)) {
					gestorDesechosPeligrosos.setFaseAlmacenamiento(true);
				}
				if (row.getDesechoFases().getCodigo().equals(TRANSPORTE)) {
					gestorDesechosPeligrosos.setFaseTransporte(true);
				}
				if (row.getDesechoFases().getCodigo().equals(ELIMINACION)) {
					gestorDesechosPeligrosos.setFaseEliminacion(true);
					gestorDesechosPeligrosos.setFaseAlmacenamiento(true);
				}
				if (row.getDesechoFases().getCodigo().equals(DISPOCISION_FINAL)) {
					gestorDesechosPeligrosos.setFaseDisposicionFinal(true);
					gestorDesechosPeligrosos.setFaseAlmacenamiento(true);
				}
			}
			gestorDesechosPeligrosos = gestorDesechosPeligrososFacade.guardar(gestorDesechosPeligrosos, userLogged);
			JsfUtil.cargarObjetoSession("idGestorDesechosPeligrosos", gestorDesechosPeligrosos.getId());
			verFormulario = true;
			JsfUtil.redirectTo("/control/retce/gestorDesechos/gestorDesechosPeligrosos.jsf");
		} else {
			JsfUtil.addMessageError("Debe seleccionar al menos una fase en la Información Básica.");
			JsfUtil.redirectTo("/control/retce/gestorDesechosPeligrososLista.jsf");
		}
    }
    public void editarDeclaracion(GestorDesechosPeligrosos item) {
    	gestorDesechosPeligrosos = item;
    	verFormulario = true;
    	JsfUtil.cargarObjetoSession("idGestorDesechosPeligrosos", item.getId());
    	JsfUtil.redirectTo("/control/retce/gestorDesechos/gestorDesechosPeligrosos.jsf");
    }
    public void eliminarDeclaracion(GestorDesechosPeligrosos item) {
    	Usuario userLogged=JsfUtil.getLoggedUser();
    	item.setEstado(false);
    	listGestorDesechosPeligrosos.remove(item);
    	gestorDesechosPeligrosos = gestorDesechosPeligrososFacade.guardar(gestorDesechosPeligrosos, userLogged);
    	JsfUtil.addMessageInfo("Declaración eliminada");
    }
    public void resetDeclaracion() {
    	gestorDesechosPeligrosos = new GestorDesechosPeligrosos();
    }
    public void verDeclaracion(GestorDesechosPeligrosos item) {
    	JsfUtil.cargarObjetoSession("idGestorDesechosPeligrosos", item.getId());
    	JsfUtil.redirectTo("/control/retce/gestorDesechos/gestorDesechosPeligrososVer.jsf");
    }
    private void cargaDeclaraciones(Integer idInformacionBasica) {
    	listGestorDesechosPeligrosos = new ArrayList<GestorDesechosPeligrosos>();
    	try {
			listGestorDesechosPeligrosos = gestorDesechosPeligrososFacade.getByInformacionBasica(idInformacionBasica);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private TecnicoResponsable asignarTecnicoReporta(GestorDesechosPeligrosos gestorDesechosPeligrosos) {
    	TecnicoResponsable tecnico = gestorDesechosPeligrosos.getTecnicoResponsable();
    	if (tecnico == null) {
    		tecnico = informacionProyecto.getTecnicoResponsable();
    		gestorDesechosPeligrosos.setTecnicoResponsable(tecnico);
    		gestorDesechosPeligrosos = gestorDesechosPeligrososFacade.guardar(gestorDesechosPeligrosos, JsfUtil.getLoggedUser());
		}
    	return tecnico;
    }
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
    
    public void cargarListaAnios() {
    	List<String> listaAniosMetaAux;
		Date nuevaFecha = new Date();
		Integer i= JsfUtil.getYearFromDate(nuevaFecha);
		listaAniosMetaAux = new ArrayList<String>();
		for (int j = 0; j < 3; j++) {
			i--;
			listaAniosMetaAux.add(i.toString());
		}
		listaAniosMeta = listaAniosMetaAux;
	}
    private List<Integer> cargarListaFases(InformacionProyecto informacionBasica) {
    	List<Integer> lista = new ArrayList<Integer>();
    	Integer idGestorDesechosPeligrosos = (Integer)(JsfUtil.devolverObjetoSession("idGestorDesechosPeligrosos"));
    	GestorDesechosPeligrosos rowGestor = gestorDesechosPeligrososFacade.getById(idGestorDesechosPeligrosos);
    	if (rowGestor != null) {
			if (rowGestor.getFaseAlmacenamiento()) {
				DetalleCatalogoGeneral faseAlmacenamiento = detalleCatalogGeneralFacade.findByCodigo(ALMACENAMIENTO);
				lista.add(faseAlmacenamiento.getId());
			}
			if (rowGestor.getFaseTransporte()) {
				DetalleCatalogoGeneral faseTransporte = detalleCatalogGeneralFacade.findByCodigo(TRANSPORTE);
				lista.add(faseTransporte.getId());
			}
			if (rowGestor.getFaseEliminacion()) {
				DetalleCatalogoGeneral faseEliminacion = detalleCatalogGeneralFacade.findByCodigo(ELIMINACION);
				lista.add(faseEliminacion.getId());
			}
			if (rowGestor.getFaseDisposicionFinal()) {
				DetalleCatalogoGeneral faseDisposicion = detalleCatalogGeneralFacade.findByCodigo(DISPOCISION_FINAL);
				lista.add(faseDisposicion.getId());
			}
			if ((rowGestor.getFaseDisposicionFinal() || rowGestor.getFaseDisposicionFinal()) && !rowGestor.getFaseAlmacenamiento()) {
				DetalleCatalogoGeneral faseAlmacenamiento = detalleCatalogGeneralFacade.findByCodigo(ALMACENAMIENTO);
				lista.add(faseAlmacenamiento.getId());
			}
		}
    	return lista;
    }
    public List<Integer> cargarListaART(InformacionProyecto informacionBasica){
    	List<InformacionProyectoAprobacionRequisitosTecnicos> listInformacionProyectoAprobacionRequisitosTecnicos = new ArrayList<InformacionProyectoAprobacionRequisitosTecnicos>();
    	listInformacionProyectoAprobacionRequisitosTecnicos = informacionProyectoAprobacionRequisitosTecnicosFacade.findByProyecto(informacionBasica);
    	List<Integer> lista = new ArrayList<Integer>();
    	if (listInformacionProyectoAprobacionRequisitosTecnicos.size() > 0) {
			for (InformacionProyectoAprobacionRequisitosTecnicos row : listInformacionProyectoAprobacionRequisitosTecnicos) {
				lista.add(row.getAprobacionRequisitosTecnicos().getId());
			}
		}
    	return lista;
    }
    private List<SedePrestadorServiciosDesechos> cargarEmpresasGestoras(Integer fase){
    	List<SedePrestadorServiciosDesechos> lista = new ArrayList<SedePrestadorServiciosDesechos>(); 
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("idFasesGestion", fase);
    	lista = (List<SedePrestadorServiciosDesechos>) crudServiceBean.findByNamedQuery(SedePrestadorServiciosDesechos.FILTRAR_POR_FASE_GESTION, params);
    	return lista;
    }
    
    // Resets fases
    private void resetFaseTransporte() {
    	listRutas = new ArrayList<Ruta>();
    	listManifiestoUnicoTransporte = new ArrayList<ManifiestoUnico>();
    	listEmpresasTransporte = new ArrayList<ManifiestoUnicoTransferencia>();
    	listSedePrestadorServiciosDesechos = new ArrayList<SedePrestadorServiciosDesechos>();
    	listDesechoGestorTransporte = new ArrayList<DesechoGestorTransporte>();
    	listTotalManifiestoTransporte = new ArrayList<ResumenManifiesto>();
    	listDesechoGestorTransporteExportacion = new ArrayList<DesechoGestorTransporteExportacion>();
    	listDesechosTransporte = new ArrayList<DesechoPeligroso>();
    	
    	listSedePrestadorServiciosDesechos = cargarEmpresasGestoras(FASE_TRANSPORTE);
    	ruta = new Ruta();
    	manifiestoUnico = new ManifiestoUnico();
    	manifiestoUnicoTransferencia = new ManifiestoUnicoTransferencia();
    	documentoManifiestoTransporte = new Documento();
    	desechoGestorTransporte = new DesechoGestorTransporte();
    	desechoPeligrosoTransporteRETCE = new DesechoPeligrosoTransporteRETCE();
    	desechoGestorTransporteExportacion = new DesechoGestorTransporteExportacion();
    	documentoTransporteExportacionAutorizacion = new Documento();

    	listDesechoPeligrosoTransporte = new ArrayList<DesechoPeligrosoTransporteRETCE>();
    	listART = cargarListaART(informacionProyecto);
    	listDesechosTransporte = listaDesechosTransporte(informacionProyecto);
    	listUbicacionesGeografica = new ArrayList<UbicacionesGeografica>();
    	listUbicacionesGeografica = ubicacionGeograficaFacade.getListaPaises();
    }
    private void resetFaseAlmacenamiento() {
    	listUbicacionesGeografica = new ArrayList<UbicacionesGeografica>();
    	listUbicacionesGeografica = ubicacionGeograficaFacade.getListaPaises();
    	listAlmacenamiento = new ArrayList<DetalleCatalogoGeneral>();
    	listAlmacenamiento = detalleCatalogGeneralFacade.findByCatalogoGeneralString(LISTA_TIPO_ALMACENAMIENTO);

    	listDesechosAlmacenamientoRecepcion = new ArrayList<DesechoPeligroso>();
    	listDesechosAlmacenamientoRecepcion = listaDesechosRecepcion(informacionProyecto);
    	listDesechosAlmacenamientoTransferencia = new ArrayList<DesechoPeligroso>();
    	listDesechosAlmacenamientoTransferencia = listaDesechosTranferencia(informacionProyecto);

    	listSedePrestadorServiciosDesechosAlmacenamiento = new ArrayList<SedePrestadorServiciosDesechos>();
    	listSedePrestadorServiciosDesechosAlmacenamiento = cargarEmpresasGestoras(FASE_ALMACENAMIENTO);
    	listSedePrestadorServiciosSelected = new ArrayList<SedePrestadorServiciosDesechos>();
    	listEmpresasAlmacenamientoRecepcion = new ArrayList<ManifiestoUnicoTransferencia>();

    	listManifiestoRecepcion = new ArrayList<ManifiestoUnico>();
    	listDesechoGestorAlmacenamientoRecepcion = new ArrayList<DesechoGestorAlmacenamiento>();
    	listTotalManifiestoAlmacenamiento = new ArrayList<ResumenManifiesto>();
    	listDesechoGestorAlmacenamientoExportacion = new ArrayList<DesechoGestorAlmacenamientoExportacion>();

    	listManifiestoTransferencia = new ArrayList<ManifiestoUnico>();
    	listDesechoGestorAlmacenamientoTransferencia = new ArrayList<DesechoGestorAlmacenamiento>();
        listEmpresasAlmacenamientoTransferencia = new ArrayList<ManifiestoUnicoTransferencia>();
    	
    	manifiestoUnico = new ManifiestoUnico();
    	manifiestoUnicoTransferencia = new ManifiestoUnicoTransferencia();
    	documentoManifiestoAlmacenamientoRecepcion = new Documento();
    	documentoManifiestoAlmacenamientoTransferencia = new Documento();
    	desechoGestorAlmacenamiento = new DesechoGestorAlmacenamiento();
    	sedePrestadorServiciosDesechos = new SedePrestadorServiciosDesechos();
    	desechoGestorAlmacenamientoExportacion = new DesechoGestorAlmacenamientoExportacion();
    	documentoAlmacenamientoExportacionNotificacion = new Documento();
    	documentoAlmacenamientoExportacionAutorizacion = new Documento();
    	documentoAlmacenamientoExportacionMovimiento = new Documento();
    	documentoAlmacenamientoExportacionDestruccion = new Documento();
    }
    private void resetFaseEliminacion() {
    	listDesechosEliminacion = new ArrayList<DesechoPeligrosoEliminacionRETCE>();
    	listDesechosEliminacion = listaDesechoEliminacion(informacionProyecto);
    	catalogoSustanciasRetceList = new ArrayList<CatalogoSustanciasRetce>();
		catalogoSustanciasRetceList = catalogoSustanciasRetceFacade.findAll();
    }
    private void resetFaseDisposicionFinal() {
    	listDesechosDisposicionFinal = new ArrayList<DesechoPeligrosoEliminacionRETCE>();
    	listDesechosDisposicionFinal = listaDesechoDisposicionFinal(informacionProyecto);
    	catalogoSustanciasRetceList = new ArrayList<CatalogoSustanciasRetce>();
		catalogoSustanciasRetceList = catalogoSustanciasRetceFacade.findAll();
    }

// ------------------------------------------- FASE DE TRANSPORTE -------------------------------------------
    // Rutas Transporte
    public void agregarRuta() {
    	try {
    		String origen = ubicacionGeograficaFacade.buscarPorId(ruta.getProvinciaOrigen()).getNombre();
    		String destino = ubicacionGeograficaFacade.buscarPorId(ruta.getProvinciaDestino()).getNombre();
    		String nombre = origen + " - " + destino;
    		ruta.setNombreProvinciaOrigen(origen);
    		ruta.setNombreProvinciaDestino(destino);
    		ruta.setGestorDesechosPeligrosos(gestorDesechosPeligrosos);
    		ruta.setNombre(nombre);
    		if (listRutas.isEmpty()) {
    			ruta = rutaFacade.guardar(ruta);
    			listRutas.add(ruta);
				JsfUtil.addMessageInfo("Ruta ingresada.");
			} else if (listRutas.size() < 5) {
				if (ruta.getId() == null) {
					if (!listRutas.contains(ruta)) {
						ruta = rutaFacade.guardar(ruta);
	    	    		listRutas.add(ruta);
	    	    		JsfUtil.addMessageInfo("Ruta ingresada.");
	    			} else {
	    				JsfUtil.addMessageInfo("Ruta modificada.");
	    			}
				} else {
					Ruta rutaBase = rutaFacade.getById(ruta.getId());
					ruta = rutaFacade.guardar(ruta);
					if (!listRutas.contains(ruta)) {
	    	    		listRutas.add(ruta);
	    	    		JsfUtil.addMessageInfo("Ruta ingresada.");
	    			} else {
	    				rutaBase.setEstado(false);
	    				rutaBase.setHistorial(true);
	    				rutaBase.setIdPertenece(ruta.getId());
	    				rutaBase.setId(null);
	    				rutaBase = rutaFacade.guardar(rutaBase);
	    				JsfUtil.addMessageInfo("Ruta modificada.");
	    			}
				}
			} else {
				JsfUtil.addMessageInfo("Estimado usuario, el número máximo de rutas es de 5.");
			}
    		resetRuta();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void editarRuta(Ruta item) {
    	ruta=item;
	}
    public void eliminarRuta(Ruta item) {
    	listRutas.remove(item);
    	item.setEstado(false);
    	item = rutaFacade.guardar(item);
    	JsfUtil.addMessageInfo("Ruta eliminada");
    }
    public void resetRuta(){
    	ruta = new Ruta();
    }
    
    // Manifiestos Transporte
    public void agregarManifiestoUnico() {
    	try {
    		DetalleCatalogoGeneral faseTransporte = detalleCatalogGeneralFacade.findByCodigo(TRANSPORTE);
            manifiestoUnico.setGestorDesechosPeligrosos(gestorDesechosPeligrosos);
            manifiestoUnico.setFaseManifiesto(faseTransporte);
            if (listSedePrestadorServiciosSelected.size() <= 0) {
                JsfUtil.addMessageError("No ha seleccionado ninguna empresa gestora.");
                return;
            }
            if (manifiestoUnico.listDesechoTransporte.size() <= 0) {
                JsfUtil.addMessageError("No ha seleccionado ningún desecho peligroso.");
                return;
            }
            
            // Ingreso a base de manifiesto recepcion
            manifiestoUnico = manifiestoUnicoFacade.guardar(manifiestoUnico);
            // Ingreso a alfresco de documento manifiesto recepcion
            if (documentoManifiestoTransporte.getNombre() != null) {
                Documento documentoTransporte = new Documento();
                documentoManifiestoTransporte.setIdTable(manifiestoUnico.getId());
                documentoManifiestoTransporte.setNombreTabla(ManifiestoUnico.class.getSimpleName());
                documentoTransporte = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", manifiestoUnico.getId().longValue(), documentoManifiestoTransporte, TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO, null);
                manifiestoUnico.setAdjuntoManifiesto(documentoTransporte);
            }
            
            // Ingreso a base de empresa gestora almacenamiento recepcion
            for (SedePrestadorServiciosDesechos rowEmpresa : listSedePrestadorServiciosSelected) {
            	ManifiestoUnicoTransferencia transferencia = new ManifiestoUnicoTransferencia();
                transferencia.setFechaTransferencia(manifiestoUnicoTransferencia.getFechaTransferencia());
                transferencia.setManifiestoUnico(manifiestoUnico);
                transferencia.setSedePrestadorServiciosDesechos(rowEmpresa);
                transferencia = manifiestoUnicoTransferenciaFacade.guardar(transferencia);
                manifiestoUnico.listEmpresasTransporte.add(transferencia);
            }
    		
            // Ingreso a base de los desechos almacenamiento recepcion
            for (DesechoGestorTransporte rowDesecho : manifiestoUnico.listDesechoTransporte) {
                rowDesecho.setManifiestoUnico(manifiestoUnico);
                rowDesecho = desechoGestorTransporteFacade.guardar(rowDesecho);
                listDesechoGestorTransporte.add(rowDesecho);
            }
            listManifiestoUnicoTransporte.add(manifiestoUnico);
            listTotalManifiestoTransporte = calcularSumatoriaTotal(listManifiestoUnicoTransporte, listDesechoGestorTransporte);
    		
            for (DesechoGestorTransporteExportacion rowExportacion : listDesechoGestorTransporteExportacion) {
                if (rowExportacion.getId() == null) {
                    rowExportacion = desechoGestorTransporteExportacionFacade.guardar(rowExportacion);
                    
                    documentoTransporteExportacionAutorizacion = rowExportacion.getDocumentoAutorizacion();
                    if (documentoTransporteExportacionAutorizacion.getNombre() != null) {
                        Documento documentoAutorizacion = new Documento();
                        documentoTransporteExportacionAutorizacion.setIdTable(rowExportacion.getId());
                        documentoAutorizacion = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", rowExportacion.getId().longValue(), documentoTransporteExportacionAutorizacion, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION, null);
                        rowExportacion.setDocumentoAutorizacion(documentoAutorizacion);
                    }
                }
            }
            JsfUtil.addMessageInfo("Manifiesto ingresado.");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al guardar el adjunto del Manifiesto - Transporte. Por favor comuníquese con Mesa de Ayuda");
            LOG.error(e);
		}
    	resetTransporte();
    }
    public void editarManifiestoUnico(ManifiestoUnico item){
    	manifiestoUnico=item;
    	documentoManifiestoTransporte = item.getAdjuntoManifiesto();
	}
    public void eliminarManifiestoUnico(ManifiestoUnico item) {
    	try {
    		listEmpresasTransporte = manifiestoUnicoTransferenciaFacade.getByManifiestoUnico(item.getId());
    		if (listEmpresasTransporte.size() > 0) {
    			for (ManifiestoUnicoTransferencia row : listEmpresasTransporte) {
    				eliminarEmpresaGestora(row);
    			}
    		}
    		listDesechoGestorTransporte = desechoGestorTransporteFacade.getByManifiestoUnico(item.getId());
    		if (listDesechoGestorTransporte.size() > 0) {
    			for (DesechoGestorTransporte row : listDesechoGestorTransporte) {
    				eliminarDesechoGestorTransporte(row);
    			}
    		}
    		listManifiestoUnicoTransporte.remove(item);
    		item.setEstado(false);
    		item = manifiestoUnicoFacade.guardar(item);
    		JsfUtil.addMessageInfo("Manifiesto Único eliminado");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error el Manifiesto - Transporte. Por favor comuníquese con Mesa de Ayuda");
			LOG.error(e);
		}
    }
    public void resetManifiestoUnico(){
    	manifiestoUnico = new ManifiestoUnico();
    	documentoManifiestoTransporte = new Documento();
    }
    public void resetTransporte(){
    	resetManifiestoUnico();
    	resetDesechoGestorTransporte();
    	resetEmpresaGestora();
    	resetDesechoGestorTransporteExportacion();
    }

	// Documento Manifiesto Transporte  
    public void asignarDocumentoManifiestoTransporte(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	documentoManifiestoTransporte.setNombre(file.getFile().getFileName());
    	documentoManifiestoTransporte.setMime(file.getFile().getContentType());
    	documentoManifiestoTransporte.setContenidoDocumento(file.getFile().getContents());
    	documentoManifiestoTransporte.setExtesion(extension);
    	documentoManifiestoTransporte.setNombreTabla(ManifiestoUnico.class.getSimpleName());
    }
    // Documento Exportacion Transporte
    public void asignarDocumentoTransporteAutorizacion(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	documentoTransporteExportacionAutorizacion.setNombre(file.getFile().getFileName());
    	documentoTransporteExportacionAutorizacion.setMime(file.getFile().getContentType());
    	documentoTransporteExportacionAutorizacion.setContenidoDocumento(file.getFile().getContents());
    	documentoTransporteExportacionAutorizacion.setExtesion(extension);
    	documentoTransporteExportacionAutorizacion.setNombreTabla(DesechoGestorTransporteExportacion.class.getSimpleName());
    }

    // Desechos Peligrosos Transporte y Exportacion
    private List<DesechoPeligroso> listaDesechosTransporte(InformacionProyecto informacionProyecto){
    	List<DesechoPeligroso> listDesechosART = new ArrayList<DesechoPeligroso>();
    	try {
    		List<Integer> listadoART = cargarListaART(informacionProyecto);
    		// Desechos Eliminacion
    		for (Integer idART : listadoART) {
    			List<DesechoPeligrosoTransporte> lista;
    			lista = desechoPeligrosoTransporteFacade.getListaListaDesechoPeligrosoTransporte(idART);
    			for (DesechoPeligrosoTransporte row : lista) {
    				listDesechosART.add(row.getDesechoPeligroso());
    			}
    		}
    		
    		// Desechos Peligroso a no Peligroso
			List<InformacionProyectoCambioDesechosPeligrosos> listDesechosCambio = informacionProyectoCambioDesechosPeligrososFacade.findByProyecto(informacionProyecto);
			for (InformacionProyectoCambioDesechosPeligrosos row : listDesechosCambio) {
				if (row.isTransporte()) {
					listDesechosART.add(row.getDesechosPeligrosos());
				}
			}
			// Desechos Informacion Basica
			List<InformacionProyectoDesechosPeligrosos> listDesechosInformacion = new ArrayList<InformacionProyectoDesechosPeligrosos>();
			listDesechosInformacion = informacionProyectoDesechosPeligrososFacade.findByProyecto(informacionProyecto);
			for (InformacionProyectoDesechosPeligrosos row : listDesechosInformacion) {
				listDesechosART.add(row.getDesechosPeligrosos());
			}
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return listDesechosART;
    }
    public void agregarDesechoGestorTransporte() {
    	DetalleCatalogoGeneral catalogoUnidad = detalleCatalogGeneralFacade.findByCodigo("tipounidad.kilogramo");
    	desechoGestorTransporte.setDesechoPeligroso(desechoPeligroso);
    	desechoGestorTransporte.setClave(desechoPeligroso.getClave());
        Integer idUnidad = desechoGestorTransporte.getUnidadDesechoTransporte().getId();
        Double cantidadIngresada = desechoGestorTransporte.getCantidad();
        if (idUnidad.equals(catalogoUnidad.getId())) {
            Double cantidadConvertida = cantidadIngresada * factorKgT;
            desechoGestorTransporte.setCantidadKilogramos(cantidadIngresada);
            desechoGestorTransporte.setCantidadToneladas(cantidadConvertida);
        } else {
        	desechoGestorTransporte.setCantidadKilogramos(cantidadIngresada);
        	desechoGestorTransporte.setCantidadToneladas(cantidadIngresada);
        }
        
        manifiestoUnico.listDesechoTransporte.add(desechoGestorTransporte);
        JsfUtil.addMessageInfo("Desecho Peligroso ingresado.");
        
        if (desechoGestorTransporte.getEsExportacion() != null && desechoGestorTransporte.getEsExportacion()) {
            try {
                desechoGestorTransporteExportacion.setDesechoGestorTransporte(desechoGestorTransporte);
                desechoGestorTransporteExportacion.setDesechoPeligroso(desechoGestorTransporte.getDesechoPeligroso());
                desechoGestorTransporteExportacion.setClave(desechoGestorTransporte.getDesechoPeligroso().getClave());

                if (documentoTransporteExportacionAutorizacion.getNombre() != null) {
                	documentoTransporteExportacionAutorizacion.setNombreTabla(DesechoGestorTransporteExportacion.class.getSimpleName());
                	desechoGestorTransporteExportacion.setDocumentoAutorizacion(documentoTransporteExportacionAutorizacion);
                }
                if (!listDesechoGestorTransporteExportacion.contains(desechoGestorTransporteExportacion)) {
                	listDesechoGestorTransporteExportacion.add(desechoGestorTransporteExportacion);
                    JsfUtil.addMessageInfo("Exportación ingresada.");
                } else {
                    JsfUtil.addMessageInfo("Exportacion modificada.");
                }
            } catch (Exception e) {
                JsfUtil.addMessageError("Error al guardar los adjuntos de Exportación. Por favor comuníquese con Mesa de Ayuda");
                LOG.error(e);
            }
        }
        resetDesechoGestorTransporte();
    }
    public void eliminarDesechoGestorTransporte(DesechoGestorTransporte item) {
    	if (item.getEsExportacion()) {
			eliminarDesechoGestorTransporteExportacion(item);
        }
        item.getManifiestoUnico().listDesechoTransporte = desechoGestorTransporteFacade.getByManifiestoUnico(item.getManifiestoUnico().getId());
        item.getManifiestoUnico().listDesechoTransporte.remove(item);
        listDesechoGestorTransporte.remove(item);
        item.setEstado(false);
        item = desechoGestorTransporteFacade.guardar(item);
        listTotalManifiestoAlmacenamiento = calcularSumatoriaTotal(listManifiestoUnicoTransporte, listDesechoGestorTransporte);
        JsfUtil.addMessageInfo("Desecho Peligroso eliminado");
    }
    private void eliminarDesechoGestorTransporteExportacion(DesechoGestorTransporte item) {
        Integer idDesechoGestorTransporte = item.getId();
        DesechoGestorTransporteExportacion row = desechoGestorTransporteExportacionFacade.getByDesechoGestorTransporte(idDesechoGestorTransporte);
        if (row != null) {
            listDesechoGestorTransporteExportacion.remove(row);
            row.setEstado(false);
            row = desechoGestorTransporteExportacionFacade.guardar(row);
        }
    }
    public void resetDesechoGestorTransporte(){
    	desechoGestorTransporte = new DesechoGestorTransporte();
    	documentoTransporteExportacionAutorizacion = new Documento();
        desechoGestorTransporteExportacion = new DesechoGestorTransporteExportacion();
        desechoPeligroso = new DesechoPeligroso();
        listDesechosTransporte = new ArrayList<DesechoPeligroso>();
        listDesechosTransporte = listaDesechosTransporte(informacionProyecto);
        esDesechoExportacion = false;
    }
    public void resetDesechoGestorTransporteExportacion(){
    	List<DesechoGestorTransporteExportacion> listExportacion = listDesechoGestorTransporteExportacion;
    	for (DesechoGestorTransporteExportacion rowExportacion : listExportacion) {
			if (rowExportacion.getId() == null) {
				listDesechoGestorTransporteExportacion.remove(rowExportacion);
			}
		}
    	desechoGestorTransporteExportacion = new DesechoGestorTransporteExportacion();
    }
    public void validarDesechoTransporte(SelectEvent event){
    	DesechoPeligroso desecho = (DesechoPeligroso) event.getObject();
    	String clave = desecho.getClave();
    	if (clave.equals("ES-04") || clave.equals("ES-06")) {
    		esDesechoES = true;
		} else {
			esDesechoES = false;
		}
    }
    
    // Seleccion Empresa Gestora Transporte
    public void seleccionarEmpresaGestora(SedePrestadorServiciosDesechos item) {
    	manifiestoUnicoTransferencia.setManifiestoUnico(manifiestoUnico);
     	manifiestoUnicoTransferencia.setSedePrestadorServiciosDesechos(item);
    	if (manifiestoUnicoTransferencia.getId() == null) {
			if (!listEmpresasTransporte.contains(manifiestoUnicoTransferencia)) {
				manifiestoUnicoTransferencia = manifiestoUnicoTransferenciaFacade.guardar(manifiestoUnicoTransferencia);
				listEmpresasTransporte.add(manifiestoUnicoTransferencia);
				JsfUtil.addMessageInfo("Desecho Peligroso ingresado.");
			} else {
				JsfUtil.addMessageInfo("Desecho Peligroso modificado.");
			}
		} else {
			ManifiestoUnicoTransferencia empresaTransferenciaBase = manifiestoUnicoTransferenciaFacade.getById(manifiestoUnicoTransferencia.getId());
			if (!listEmpresasTransporte.contains(manifiestoUnicoTransferencia)) {
				manifiestoUnicoTransferencia = manifiestoUnicoTransferenciaFacade.guardar(manifiestoUnicoTransferencia);
				listEmpresasTransporte.add(manifiestoUnicoTransferencia);
				JsfUtil.addMessageInfo("Empresa Gestora ingresada.");
			} else {
				if (!manifiestoUnicoTransferencia.equals(empresaTransferenciaBase)) {
					empresaTransferenciaBase.setEstado(false);
					empresaTransferenciaBase.setHistorial(true);
					empresaTransferenciaBase.setIdPertenece(manifiestoUnicoTransferencia.getId());
					empresaTransferenciaBase.setId(null);
					empresaTransferenciaBase = manifiestoUnicoTransferenciaFacade.guardar(empresaTransferenciaBase);
				}
				JsfUtil.addMessageInfo("Empresa Gestora modificado.");
			}
		}
     	resetManifiestoUnico();
     	resetEmpresaGestora();
    }
    public void eliminarEmpresaGestora(ManifiestoUnicoTransferencia item) {
    	try {
            item.getManifiestoUnico().listEmpresasTransporte = manifiestoUnicoTransferenciaFacade.getByManifiestoUnico(item.getManifiestoUnico().getId());
            item.getManifiestoUnico().listEmpresasTransporte.remove(item);
            listEmpresasTransporte.remove(item);
            item.setEstado(false);
            item = manifiestoUnicoTransferenciaFacade.guardar(item);
            JsfUtil.addMessageInfo("Empresa Gestora eliminada");
        } catch (ServiceException e) {
            JsfUtil.addMessageError("Error al eliminar la empresa gestora. Por favor comuníquese con Mesa de Ayuda");
            LOG.error(e);
        }
    }
    public void resetEmpresaGestora() {
    	manifiestoUnicoTransferencia = new ManifiestoUnicoTransferencia();
        listSedePrestadorServiciosDesechos = new ArrayList<SedePrestadorServiciosDesechos>();
        listSedePrestadorServiciosDesechos = cargarEmpresasGestoras(FASE_TRANSPORTE);
        listSedePrestadorServiciosSelected = new ArrayList<SedePrestadorServiciosDesechos>();
    }
    
// ------------------------------------------- FASE DE ALMACENAMIENTO -------------------------------------------
    // Manifiesto Almacenamiento
    public void agregarManifiestoRecepcion() {
    	try {
    		DetalleCatalogoGeneral faseAlmacenamiento = detalleCatalogGeneralFacade.findByCodigo(ALMACENAMIENTO);
    		DetalleCatalogoGeneral tipoRecepcion = detalleCatalogGeneralFacade.findByCodigo(MANIFIESTO_RECEPCION);
    		manifiestoUnico.setGestorDesechosPeligrosos(gestorDesechosPeligrosos);
    		manifiestoUnico.setFaseManifiesto(faseAlmacenamiento);
    		manifiestoUnico.setTipoManifiesto(tipoRecepcion);
    		if (listSedePrestadorServiciosSelected.size() <= 0) {
				JsfUtil.addMessageError("No ha seleccionado ninguna empresa gestora.");
				return;
			}
    		if (manifiestoUnico.listDesechoRecepcion.size() <= 0) {
				JsfUtil.addMessageError("No ha seleccionado ningún desecho peligroso.");
				return;
			}

    		// Ingreso a base de manifiesto recepcion
    		manifiestoUnico = manifiestoUnicoFacade.guardar(manifiestoUnico);
    		// Ingreso a alfresco de documento manifiesto recepcion
    		if (documentoManifiestoAlmacenamientoRecepcion.getNombre() != null) {
    			Documento documentoRecepcion = new Documento();
    			documentoManifiestoAlmacenamientoRecepcion.setIdTable(manifiestoUnico.getId());
    			documentoManifiestoAlmacenamientoRecepcion.setNombreTabla(ManifiestoUnico.class.getSimpleName());
    			documentoRecepcion = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", manifiestoUnico.getId().longValue(), documentoManifiestoAlmacenamientoRecepcion, TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO, null);
    			manifiestoUnico.setAdjuntoManifiesto(documentoRecepcion);
			}

    		// Ingreso a base de empresa gestora almacenamiento recepcion
    		for (SedePrestadorServiciosDesechos rowEmpresa : listSedePrestadorServiciosSelected) {
    			ManifiestoUnicoTransferencia transferencia = new ManifiestoUnicoTransferencia();
    			transferencia.setFechaTransferencia(manifiestoUnicoTransferencia.getFechaTransferencia());
    			transferencia.setManifiestoUnico(manifiestoUnico);
    			transferencia.setSedePrestadorServiciosDesechos(rowEmpresa);
    			transferencia = manifiestoUnicoTransferenciaFacade.guardar(transferencia);
    			manifiestoUnico.listEmpresasRecepcion.add(transferencia);
    		}
    		// Ingreso a base de los desechos almacenamiento recepcion
    		for (DesechoGestorAlmacenamiento rowDesecho : manifiestoUnico.listDesechoRecepcion) {
    			rowDesecho.setManifiestoUnico(manifiestoUnico);
    			rowDesecho = desechoGestorAlmacenamientoFacade.guardar(rowDesecho);
    			listDesechoGestorAlmacenamientoRecepcion.add(rowDesecho);
			}
        	// Sumantoria manifiesto desechos almacenamiento
    		listManifiestoRecepcion.add(manifiestoUnico);
        	listTotalManifiestoAlmacenamiento = calcularSumatoriaAlmacenamientoTotal(listManifiestoRecepcion, listDesechoGestorAlmacenamientoRecepcion);
        	
    		for (DesechoGestorAlmacenamientoExportacion rowExportacion : listDesechoGestorAlmacenamientoExportacion) {
    			if (rowExportacion.getId() == null) {
    				rowExportacion = desechoGestorAlmacenamientoExportacionFacade.guardar(rowExportacion);
    				
    				documentoAlmacenamientoExportacionNotificacion = rowExportacion.getDocumentoNotificacion();
    				if (documentoAlmacenamientoExportacionNotificacion.getNombre() != null) {
    					Documento documentoNotificacion = new Documento();
    					documentoAlmacenamientoExportacionNotificacion.setIdTable(rowExportacion.getId());
    					documentoNotificacion = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", rowExportacion.getId().longValue(), documentoAlmacenamientoExportacionNotificacion, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_NOTIFICACION, null);
    					rowExportacion.setDocumentoNotificacion(documentoNotificacion);
    				}
    				
    				documentoAlmacenamientoExportacionAutorizacion = rowExportacion.getDocumentoAutorizacion();
    				if (documentoAlmacenamientoExportacionAutorizacion.getNombre() != null) {
    					Documento documentoAutorizacion = new Documento();
    					documentoAlmacenamientoExportacionAutorizacion.setIdTable(rowExportacion.getId());
    					documentoAutorizacion = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", rowExportacion.getId().longValue(), documentoAlmacenamientoExportacionAutorizacion, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION, null);
    					rowExportacion.setDocumentoAutorizacion(documentoAutorizacion);
    				}
    				
    				documentoAlmacenamientoExportacionMovimiento = rowExportacion.getDocumentoMovimiento();
    				if (documentoAlmacenamientoExportacionMovimiento.getNombre() != null) {
    					Documento documentoMovimiento = new Documento();
    					documentoAlmacenamientoExportacionMovimiento.setIdTable(rowExportacion.getId());
    					documentoMovimiento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", rowExportacion.getId().longValue(), documentoAlmacenamientoExportacionMovimiento, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_MOVIMIENTOS, null);
    					rowExportacion.setDocumentoMovimiento(documentoMovimiento);
    				}
    				
    				documentoAlmacenamientoExportacionDestruccion = rowExportacion.getDocumentoDestruccion();
    				if (documentoAlmacenamientoExportacionDestruccion.getNombre() != null) {
    					Documento documentoDestruccion = new Documento();
    					documentoAlmacenamientoExportacionDestruccion.setIdTable(rowExportacion.getId());
    					documentoDestruccion = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", rowExportacion.getId().longValue(), documentoAlmacenamientoExportacionDestruccion, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_DESTRUCCION, null);
    					rowExportacion.setDocumentoDestruccion(documentoDestruccion);
    				}
				}
			}
    		JsfUtil.addMessageInfo("Manifiesto ingresado.");
    	} catch (Exception e) {
			JsfUtil.addMessageError("Error al guardar Información. Por favor comuníquese con Mesa de Ayuda");
			LOG.error(e);
		}
    	resetRecepcion();
    }
    public void editarManifiestoRecepcion(ManifiestoUnico item){
    	manifiestoUnico=item;
    	documentoManifiestoAlmacenamientoRecepcion = item.getAdjuntoManifiesto();
	}
    public void eliminarManifiestoRecepcion(ManifiestoUnico item) {
		List<DesechoGestorAlmacenamiento> listDesechoRecepcion = new ArrayList<DesechoGestorAlmacenamiento>();
		listDesechoRecepcion = desechoGestorAlmacenamientoFacade.getByManifiestoUnico(item.getId());
		if (listDesechoRecepcion.size() > 0) {
			for (DesechoGestorAlmacenamiento rowDesecho : listDesechoRecepcion) {
				eliminarDesechoGestorRecepcion(rowDesecho);
			}
		}
		listManifiestoRecepcion.remove(item);
		item.setEstado(false);
		item = manifiestoUnicoFacade.guardar(item);
		Documento documentoRecepcionBase = documentosFacade.documentoXTablaIdXIdDocUnico(manifiestoUnico.getId(),ManifiestoUnico.class.getSimpleName(),TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO);
		if (documentoRecepcionBase != null) {
			documentosFacade.eliminarDocumento(documentoRecepcionBase);
		}
		JsfUtil.addMessageInfo("Manifiesto Único eliminado");
    }
    public void resetManifiestoRecepcion(){
    	manifiestoUnico = new ManifiestoUnico();
    	documentoManifiestoAlmacenamientoRecepcion = new Documento();
    }
    public void resetRecepcion(){
    	resetManifiestoRecepcion();
    	resetDesechoGestorRecepcion();
    	resetEmpresaGestoraRecepcion();
    	resetDesechoGestorAlmacenamientoExportacion();
    }
    // Documento Manifiesto Transporte  
    public void asignarDocumentoManifiestoRecepcion(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	documentoManifiestoAlmacenamientoRecepcion.setNombre(file.getFile().getFileName());
    	documentoManifiestoAlmacenamientoRecepcion.setMime(file.getFile().getContentType());
    	documentoManifiestoAlmacenamientoRecepcion.setContenidoDocumento(file.getFile().getContents());
    	documentoManifiestoAlmacenamientoRecepcion.setExtesion(extension);
    	documentoManifiestoAlmacenamientoRecepcion.setNombreTabla(ManifiestoUnico.class.getSimpleName());
    }
    
    // Mostrar los formularios de tipos de almacenamiento
    public void validarDesechoAlmacenamiento(SelectEvent event){
    	DesechoPeligroso desecho = (DesechoPeligroso) event.getObject();
    	String clave = desecho.getClave();
    	if (clave.equals("ES-04") || clave.equals("ES-06")) {
    		esDesechoRecepcionES = true;
		} else {
			esDesechoRecepcionES = false;
		}
    	desechoGestorAlmacenamiento.setEsDesechoRecepcionES(esDesechoRecepcionES);
    }
    public void validarTipoAlmacenamiento() {
    	desechoGestorAlmacenamiento.setEsExportacion(false);
    	desechoGestorAlmacenamiento.setEsAcopio(false);
    	desechoGestorAlmacenamiento.setEsEliminacion(false);
    	desechoGestorAlmacenamiento.setEsDisposicionFinal(false);
    	if (tipoAlmacenamientoSeleccionada.length == 0) {
    		esDesechoExportacion = false;
		} else {
			esDesechoExportacion = false;
			for (DetalleCatalogoGeneral tipoAlmacenamiento : tipoAlmacenamientoSeleccionada) {
				if (tipoAlmacenamiento.getCodigo().equals("exportacion")) {
					desechoGestorAlmacenamiento.setEsExportacion(true);
					esDesechoExportacion = true;
				}
				if (tipoAlmacenamiento.getCodigo().equals("acopio"))
					desechoGestorAlmacenamiento.setEsAcopio(true);
				if (tipoAlmacenamiento.getCodigo().equals("eliminacion"))
					desechoGestorAlmacenamiento.setEsEliminacion(true);
				if (tipoAlmacenamiento.getCodigo().equals("disposicionfinal"))
					desechoGestorAlmacenamiento.setEsDisposicionFinal(true);
			}
		}
    }
    public void validarTransferenciaExportacion() {
    	desechoGestorAlmacenamiento.setEsExportacion(false);
    	desechoGestorAlmacenamiento.setEsAcopio(false);
    	desechoGestorAlmacenamiento.setEsEliminacion(false);
    	desechoGestorAlmacenamiento.setEsDisposicionFinal(false);
    	if (esDesechoExportacion) {
    		desechoGestorAlmacenamiento.setEsExportacion(true);
    		esDesechoExportacion = true;
    	}
    }
    public void validarTransporteExportacion() {
    	desechoGestorTransporte.setEsExportacion(false);
    	if (esDesechoExportacion) {
    		desechoGestorTransporte.setEsExportacion(true);
    		esDesechoExportacion = true;
    	}
    }
    // Desechos Peligrosos Almacenamiento - Recepcion
    private List<DesechoPeligroso> listaDesechosRecepcion(InformacionProyecto informacionProyecto){
    	List<DesechoPeligroso> listDesechosART = new ArrayList<DesechoPeligroso>();
    	List<Integer> fases = cargarListaFases(informacionProyecto);
    	boolean almacenamientoSeleccionado = false, transporteSeleccionado = false, eliminacionSeleccionado = false, disposicionSeleccionado = false;
    	DetalleCatalogoGeneral faseAlmacenamiento = detalleCatalogGeneralFacade.findByCodigo(ALMACENAMIENTO);
		if (fases.contains(faseAlmacenamiento.getId())) {
			almacenamientoSeleccionado = true;
		}
		DetalleCatalogoGeneral faseTransporte = detalleCatalogGeneralFacade.findByCodigo(TRANSPORTE);
		if (fases.contains(faseTransporte.getId())) {
			transporteSeleccionado = true;
		}
		DetalleCatalogoGeneral faseEliminacion = detalleCatalogGeneralFacade.findByCodigo(ELIMINACION);
		if (fases.contains(faseEliminacion.getId())) {
			eliminacionSeleccionado = true;
		}
		DetalleCatalogoGeneral faseDisposicion = detalleCatalogGeneralFacade.findByCodigo(DISPOCISION_FINAL);
		if (fases.contains(faseDisposicion.getId())) {
			disposicionSeleccionado = true;
		}
		try {
			// Caso 1 RF - Si en información básica seleccionó solamente la fase almacenamiento
			if (almacenamientoSeleccionado && !transporteSeleccionado && !eliminacionSeleccionado && !disposicionSeleccionado) {
				List<InformacionProyectoCambioDesechosPeligrosos> listDesechosCambio = informacionProyectoCambioDesechosPeligrososFacade.findByProyecto(informacionProyecto);
				for (InformacionProyectoCambioDesechosPeligrosos row : listDesechosCambio) {
					listDesechosART.add(row.getDesechosPeligrosos());
				}
			}
			// Caso 2 RF - Si en información básica seleccionó las opciones eliminación y disposición final
			if (eliminacionSeleccionado && disposicionSeleccionado && !almacenamientoSeleccionado && !transporteSeleccionado ) {
				List<Integer> listadoART = cargarListaART(informacionProyecto);
				// Desechos Eliminacion
				for (Integer idART : listadoART) {
					List<EliminacionRecepcion> listaEliminacionRecepcion = eliminacionDesechoFacade.listarEliminacionPorAprobacionRequistos(idART);
					for (EliminacionRecepcion eliminacionRecepcion : listaEliminacionRecepcion) {
						DesechoPeligrosoEliminacionRETCE desechoPeligrosoEliminacionRETCE = new DesechoPeligrosoEliminacionRETCE();
						Integer idEliminacionRecepcion = eliminacionRecepcion.getId();
						Integer idRecepcionDesechoPeligroso = eliminacionRecepcion.getIdRecepcionDesechoPeligroso();
						RecepcionDesechoPeligroso recepcionDesechoPeligroso = recepcionDesechoPeligrosoFacade.getById(idRecepcionDesechoPeligroso);
						if (recepcionDesechoPeligroso != null) {
							listDesechosART.add(recepcionDesechoPeligroso.getDesecho());
						}
					}
				}
				// Desechos Disposicion Final
				for (Integer idART : listadoART) {
					List<EliminacionRecepcion> listaEliminacionRecepcion = eliminacionDesechoFacade.listarEliminacionPorAprobacionRequistos(idART);
					for (EliminacionRecepcion eliminacionRecepcion : listaEliminacionRecepcion) {
						Integer idEliminacionRecepcion = eliminacionRecepcion.getId();
						List<EliminacionDesecho> listaEliminacionDesecho = eliminacionDesechoFacade.listarEliminacionRecepcionPorId(idEliminacionRecepcion);
						for (EliminacionDesecho eliminacionDesecho : listaEliminacionDesecho) {
							if (eliminacionDesecho.getTipoEliminacionDesecho().getCodigoModalidad() == ModalidadGestionDesechos.ID_MODALIDAD_DISPOSICION_FINAL) {
								listDesechosART.add(eliminacionDesecho.getDesecho());
							}
						}
					}
				}
				// Desechos Informacion Basica
				List<InformacionProyectoCambioDesechosPeligrosos> listDesechosCambio = informacionProyectoCambioDesechosPeligrososFacade.findByProyecto(informacionProyecto);
				for (InformacionProyectoCambioDesechosPeligrosos row : listDesechosCambio) {
					listDesechosART.add(row.getDesechosPeligrosos());
				}
			}
			// Caso 3 RF - Si en información básica seleccionó las fases: almacenamiento, tratamiento y/o disposición final
			if ((eliminacionSeleccionado || disposicionSeleccionado) && almacenamientoSeleccionado) {
				List<Integer> listadoART = cargarListaART(informacionProyecto);
				// Desechos Disposicion Final
				for (Integer idART : listadoART) {
					List<EliminacionRecepcion> listaEliminacionRecepcion = eliminacionDesechoFacade.listarEliminacionPorAprobacionRequistos(idART);
					for (EliminacionRecepcion eliminacionRecepcion : listaEliminacionRecepcion) {
						Integer idEliminacionRecepcion = eliminacionRecepcion.getId();
						List<EliminacionDesecho> listaEliminacionDesecho = eliminacionDesechoFacade.listarEliminacionRecepcionPorId(idEliminacionRecepcion);
						for (EliminacionDesecho eliminacionDesecho : listaEliminacionDesecho) {
							if (eliminacionDesecho.getTipoEliminacionDesecho().getCodigoModalidad() == ModalidadGestionDesechos.ID_MODALIDAD_DISPOSICION_FINAL && eliminacionDesecho.getDesecho() != null) {
								listDesechosART.add(eliminacionDesecho.getDesecho());
							}
						}
					}
				}
				// Desechos Peligroso a no Peligroso
				List<InformacionProyectoCambioDesechosPeligrosos> listDesechosCambio = informacionProyectoCambioDesechosPeligrososFacade.findByProyecto(informacionProyecto);
				for (InformacionProyectoCambioDesechosPeligrosos row : listDesechosCambio) {
					listDesechosART.add(row.getDesechosPeligrosos());
				}
				// Desechos Informacion Basica
				List<InformacionProyectoDesechosPeligrosos> listDesechosInformacion = new ArrayList<InformacionProyectoDesechosPeligrosos>();
				listDesechosInformacion = informacionProyectoDesechosPeligrososFacade.findByProyecto(informacionProyecto);
				for (InformacionProyectoDesechosPeligrosos row : listDesechosInformacion) {
					listDesechosART.add(row.getDesechosPeligrosos());
				}
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al cargar los desechos para Almacenamiento Recepción. Por favor comuníquese con Mesa de Ayuda");
			LOG.error(e);
		}
    	return listDesechosART;
    }
    // Desechos Peligrosos Almacenamiento - Transferencia
    private List<DesechoPeligroso> listaDesechosTranferencia(InformacionProyecto informacionProyecto){
    	List<DesechoPeligroso> listDesechosART = new ArrayList<DesechoPeligroso>();
    	// Desechos Peligroso a no Peligroso
		List<InformacionProyectoCambioDesechosPeligrosos> listDesechosCambio = informacionProyectoCambioDesechosPeligrososFacade.findByProyecto(informacionProyecto);
		for (InformacionProyectoCambioDesechosPeligrosos row : listDesechosCambio) {
			listDesechosART.add(row.getDesechosPeligrosos());
		}
		// Desechos Informacion Basica
		List<InformacionProyectoDesechosPeligrosos> listDesechosInformacion = new ArrayList<InformacionProyectoDesechosPeligrosos>();
		listDesechosInformacion = informacionProyectoDesechosPeligrososFacade.findByProyecto(informacionProyecto);
		for (InformacionProyectoDesechosPeligrosos row : listDesechosInformacion) {
			listDesechosART.add(row.getDesechosPeligrosos());
		}
    	return listDesechosART;
    }
    
    public void asignarDesechoPeligroso(DesechoPeligroso desecho) {
    	desechoPeligroso = desecho;
    }
    public void agregarDesechoGestorRecepcion() {
    	DetalleCatalogoGeneral catalogoUnidad = detalleCatalogGeneralFacade.findByCodigo("tipounidad.kilogramo");
    	desechoGestorAlmacenamiento.setDesechoPeligroso(desechoPeligroso);
    	desechoGestorAlmacenamiento.setClave(desechoPeligroso.getClave());
    	Integer idUnidad = desechoGestorAlmacenamiento.getUnidadDesechoRecepcion().getId();
    	Double cantidadIngresada = desechoGestorAlmacenamiento.getCantidad();
    	if (idUnidad.equals(catalogoUnidad.getId())) {
    		Double cantidadConvertida = cantidadIngresada * factorKgT;
    		desechoGestorAlmacenamiento.setCantidadKilogramos(cantidadIngresada);
    		desechoGestorAlmacenamiento.setCantidadToneladas(cantidadConvertida);
    	} else {
    		desechoGestorAlmacenamiento.setCantidadKilogramos(cantidadIngresada);
    		desechoGestorAlmacenamiento.setCantidadToneladas(cantidadIngresada);
    	}

    	manifiestoUnico.listDesechoRecepcion.add(desechoGestorAlmacenamiento);
    	JsfUtil.addMessageInfo("Desecho Peligroso ingresado.");

    	if (desechoGestorAlmacenamiento.getEsExportacion() != null && desechoGestorAlmacenamiento.getEsExportacion()) {
    		try {
    			desechoGestorAlmacenamientoExportacion.setDesechoGestorAlmacenamiento(desechoGestorAlmacenamiento);
    			desechoGestorAlmacenamientoExportacion.setDesechoPeligroso(desechoGestorAlmacenamiento.getDesechoPeligroso());
    			desechoGestorAlmacenamientoExportacion.setClave(desechoGestorAlmacenamiento.getDesechoPeligroso().getClave());

    			if (documentoAlmacenamientoExportacionNotificacion.getNombre() != null) {
    				documentoAlmacenamientoExportacionNotificacion.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    				desechoGestorAlmacenamientoExportacion.setDocumentoNotificacion(documentoAlmacenamientoExportacionNotificacion);
    			}
    			if (documentoAlmacenamientoExportacionAutorizacion.getNombre() != null) {
    				documentoAlmacenamientoExportacionAutorizacion.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    				desechoGestorAlmacenamientoExportacion.setDocumentoAutorizacion(documentoAlmacenamientoExportacionAutorizacion);
    			}
    			if (documentoAlmacenamientoExportacionMovimiento.getNombre() != null) {
    				documentoAlmacenamientoExportacionMovimiento.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    				desechoGestorAlmacenamientoExportacion.setDocumentoMovimiento(documentoAlmacenamientoExportacionMovimiento);
    			}
    			if (documentoAlmacenamientoExportacionDestruccion.getNombre() != null) {
    				documentoAlmacenamientoExportacionDestruccion.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    				desechoGestorAlmacenamientoExportacion.setDocumentoDestruccion(documentoAlmacenamientoExportacionDestruccion);
    			}
    			if (!listDesechoGestorAlmacenamientoExportacion.contains(desechoGestorAlmacenamientoExportacion)) {
    				listDesechoGestorAlmacenamientoExportacion.add(desechoGestorAlmacenamientoExportacion);
    				JsfUtil.addMessageInfo("Exportación ingresada.");
    			} else {
    				JsfUtil.addMessageInfo("Exportacion modificada.");
    			}
    		} catch (Exception e) {
    			JsfUtil.addMessageError("Error al guardar los adjuntos de Exportación. Por favor comuníquese con Mesa de Ayuda");
    			LOG.error(e);
    		}
		}
    	resetDesechoGestorRecepcion();
    }
    public void eliminarDesechoGestorRecepcion(DesechoGestorAlmacenamiento item) {
    	if (item.getEsExportacion()) {
    		eliminarDesechoGestorAlmacenamientoExportacion(item);
		}
    	item.getManifiestoUnico().listDesechoRecepcion = desechoGestorAlmacenamientoFacade.getByManifiestoUnico(item.getManifiestoUnico().getId());
		item.getManifiestoUnico().listDesechoRecepcion.remove(item);
    	listDesechoGestorAlmacenamientoRecepcion.remove(item);
    	item.setEstado(false);
    	item = desechoGestorAlmacenamientoFacade.guardar(item);
    	listTotalManifiestoAlmacenamiento = calcularSumatoriaAlmacenamientoTotal(listManifiestoRecepcion, listDesechoGestorAlmacenamientoRecepcion);
    	JsfUtil.addMessageInfo("Desecho Peligroso eliminada");
    }
    private void eliminarDesechoGestorAlmacenamientoExportacion(DesechoGestorAlmacenamiento item) {
    	Integer idDesechoGestorAlmacenamiento = item.getId();
		DesechoGestorAlmacenamientoExportacion row = desechoGestorAlmacenamientoExportacionFacade.getByDesechoGestorAlmacenamiento(idDesechoGestorAlmacenamiento);
		if (row != null) {
			listDesechoGestorAlmacenamientoExportacion.remove(row);
			row.setEstado(false);
			row = desechoGestorAlmacenamientoExportacionFacade.guardar(row);
		}
    }
    public void resetDesechoGestorRecepcion(){
    	desechoGestorAlmacenamiento = new DesechoGestorAlmacenamiento();
    	documentoAlmacenamientoExportacionNotificacion = new Documento();
    	documentoAlmacenamientoExportacionAutorizacion = new Documento();
    	documentoAlmacenamientoExportacionMovimiento = new Documento();
    	documentoAlmacenamientoExportacionDestruccion = new Documento();
    	desechoGestorAlmacenamientoExportacion = new DesechoGestorAlmacenamientoExportacion();
    	desechoPeligroso = new DesechoPeligroso();
    	listDesechosAlmacenamientoRecepcion = new ArrayList<DesechoPeligroso>();
    	listDesechosAlmacenamientoRecepcion = listaDesechosRecepcion(informacionProyecto);
    	listAlmacenamiento = new ArrayList<DetalleCatalogoGeneral>();
    	listAlmacenamiento = detalleCatalogGeneralFacade.findByCatalogoGeneralString(LISTA_TIPO_ALMACENAMIENTO);
    	esDesechoExportacion = false;
    	tipoAlmacenamientoSeleccionada = null;
    }
    public void resetDesechoGestorAlmacenamientoExportacion() {
    	List<DesechoGestorAlmacenamientoExportacion> listExportacion = listDesechoGestorAlmacenamientoExportacion;
    	for (DesechoGestorAlmacenamientoExportacion rowExportacion : listExportacion) {
			if (rowExportacion.getId() == null) {
				listDesechoGestorAlmacenamientoExportacion.remove(rowExportacion);
			}
		}
    	desechoGestorAlmacenamientoExportacion = new DesechoGestorAlmacenamientoExportacion();
    }
    // Empresa Gestora Almacenamiento Recepcion
    public void seleccionarEmpresaGestoraRecepcion(SelectEvent event) {
//    	SedePrestadorServiciosDesechos item = (SedePrestadorServiciosDesechos) event.getObject();
//    	manifiestoUnicoTransferencia.setManifiestoUnico(manifiestoUnico);
//     	manifiestoUnicoTransferencia.setSedePrestadorServiciosDesechos(item);
//     	if (!listEmpresasAlmacenamientoRecepcion.contains(manifiestoUnicoTransferencia)) {
//     		listEmpresasAlmacenamientoRecepcion.add(manifiestoUnicoTransferencia);
//     		manifiestoUnico.addlistEmpresasRecepcion(manifiestoUnicoTransferencia);
//     		JsfUtil.addMessageInfo("Desecho Peligroso ingresado.");
//     	} else {
//     		JsfUtil.addMessageInfo("Desecho Peligroso modificado.");
//     	}
//    	resetEmpresaGestoraRecepcion();
    }
    public void eliminarEmpresaGestoraRecepcion(ManifiestoUnicoTransferencia item) {
    	try {
			item.getManifiestoUnico().listEmpresasRecepcion = manifiestoUnicoTransferenciaFacade.getByManifiestoUnico(item.getManifiestoUnico().getId());
			item.getManifiestoUnico().listEmpresasRecepcion.remove(item);
			listEmpresasAlmacenamientoRecepcion.remove(item);
			item.setEstado(false);
			item = manifiestoUnicoTransferenciaFacade.guardar(item);
			JsfUtil.addMessageInfo("Empresa Gestora eliminada");
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Error al eliminar la empresa gestora. Por favor comuníquese con Mesa de Ayuda");
			LOG.error(e);
		}
    }
    public void resetEmpresaGestoraRecepcion() {
    	manifiestoUnicoTransferencia = new ManifiestoUnicoTransferencia();
    	listSedePrestadorServiciosDesechosAlmacenamiento = new ArrayList<SedePrestadorServiciosDesechos>();
    	listSedePrestadorServiciosDesechosAlmacenamiento = cargarEmpresasGestoras(FASE_ALMACENAMIENTO);
    	listSedePrestadorServiciosSelected = new ArrayList<SedePrestadorServiciosDesechos>();
    }
    // Documentos Almacenamiento Exportacion Fase-Notificacion
    public void asignarDocumentoAlmacenamientoNotificacion(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	documentoAlmacenamientoExportacionNotificacion.setNombre(file.getFile().getFileName());
    	documentoAlmacenamientoExportacionNotificacion.setMime(file.getFile().getContentType());
    	documentoAlmacenamientoExportacionNotificacion.setContenidoDocumento(file.getFile().getContents());
    	documentoAlmacenamientoExportacionNotificacion.setExtesion(extension);
    	documentoAlmacenamientoExportacionNotificacion.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    }
    // Documentos Almacennamiento Exportacion Fase-Autorizacion
    public void asignarDocumentoAlmacenamientoAutorizacion(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	documentoAlmacenamientoExportacionAutorizacion.setNombre(file.getFile().getFileName());
    	documentoAlmacenamientoExportacionAutorizacion.setMime(file.getFile().getContentType());
    	documentoAlmacenamientoExportacionAutorizacion.setContenidoDocumento(file.getFile().getContents());
    	documentoAlmacenamientoExportacionAutorizacion.setExtesion(extension);
    	documentoAlmacenamientoExportacionAutorizacion.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    }
    // Documentos Exportacion Fase-Movimiento
    public void asignarDocumentoAlmacenamientoMovimiento(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	documentoAlmacenamientoExportacionMovimiento.setNombre(file.getFile().getFileName());
    	documentoAlmacenamientoExportacionMovimiento.setMime(file.getFile().getContentType());
    	documentoAlmacenamientoExportacionMovimiento.setContenidoDocumento(file.getFile().getContents());
    	documentoAlmacenamientoExportacionMovimiento.setExtesion(extension);
    	documentoAlmacenamientoExportacionMovimiento.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    }
    // Documentos Exportacion Fase-Destruccion
    public void asignarDocumentoAlmacenamientoDestruccion(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	documentoAlmacenamientoExportacionDestruccion.setNombre(file.getFile().getFileName());
    	documentoAlmacenamientoExportacionDestruccion.setMime(file.getFile().getContentType());
    	documentoAlmacenamientoExportacionDestruccion.setContenidoDocumento(file.getFile().getContents());
    	documentoAlmacenamientoExportacionDestruccion.setExtesion(extension);
    	documentoAlmacenamientoExportacionDestruccion.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    }
    
    // Manifiesto Almacenamiento Transferencia
    public void agregarManifiestoTransferencia(){
		try {
			DetalleCatalogoGeneral faseAlmacenamiento = detalleCatalogGeneralFacade.findByCodigo(ALMACENAMIENTO);
			DetalleCatalogoGeneral tipoRecepcion = detalleCatalogGeneralFacade.findByCodigo(MANIFIESTO_TRANSFERENCIA);
			manifiestoUnico.setGestorDesechosPeligrosos(gestorDesechosPeligrosos);
			manifiestoUnico.setFaseManifiesto(faseAlmacenamiento);
			manifiestoUnico.setTipoManifiesto(tipoRecepcion);
			if (listSedePrestadorServiciosSelected.size() <= 0) {
                JsfUtil.addMessageError("No ha seleccionado ninguna empresa gestora.");
                return;
            }
            if (manifiestoUnico.listDesechoTransferencia.size() <= 0) {
                JsfUtil.addMessageError("No ha seleccionado ningún desecho peligroso.");
                return;
            }
			
            // Ingreso a base de manifiesto recepcion
            manifiestoUnico = manifiestoUnicoFacade.guardar(manifiestoUnico);
            // Ingreso a alfresco de documento manifiesto recepcion
            if (documentoManifiestoAlmacenamientoTransferencia.getNombre() != null) {
                Documento documentoTransferencia = new Documento();
                documentoManifiestoAlmacenamientoTransferencia.setIdTable(manifiestoUnico.getId());
                documentoManifiestoAlmacenamientoTransferencia.setNombreTabla(ManifiestoUnico.class.getSimpleName());
                documentoTransferencia = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", manifiestoUnico.getId().longValue(), documentoManifiestoAlmacenamientoTransferencia, TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO, null);
                manifiestoUnico.setAdjuntoManifiesto(documentoTransferencia);
            }
            
        	// Ingreso a base de empresa gestora almacenamiento recepcion
            for (SedePrestadorServiciosDesechos rowEmpresa : listSedePrestadorServiciosSelected) {
                ManifiestoUnicoTransferencia transferencia = new ManifiestoUnicoTransferencia();
                transferencia.setFechaTransferencia(manifiestoUnicoTransferencia.getFechaTransferencia());
                transferencia.setManifiestoUnico(manifiestoUnico);
                transferencia.setSedePrestadorServiciosDesechos(rowEmpresa);
                transferencia = manifiestoUnicoTransferenciaFacade.guardar(transferencia);
                manifiestoUnico.listEmpresasTransferencia.add(transferencia);
            }
            // Ingreso a base de los desechos almacenamiento recepcion
            for (DesechoGestorAlmacenamiento rowDesecho : manifiestoUnico.listDesechoTransferencia) {
                rowDesecho.setManifiestoUnico(manifiestoUnico);
                rowDesecho = desechoGestorAlmacenamientoFacade.guardar(rowDesecho);
                listDesechoGestorAlmacenamientoTransferencia.add(rowDesecho);
            }
            listManifiestoTransferencia.add(manifiestoUnico);
            
            for (DesechoGestorAlmacenamientoExportacion rowExportacion : listDesechoGestorAlmacenamientoExportacion) {
            	if (rowExportacion.getId() == null) {
            		rowExportacion = desechoGestorAlmacenamientoExportacionFacade.guardar(rowExportacion);
            		
            		documentoAlmacenamientoExportacionNotificacion = rowExportacion.getDocumentoNotificacion();
            		if (documentoAlmacenamientoExportacionNotificacion.getNombre() != null) {
            			Documento documentoNotificacion = new Documento();
            			documentoAlmacenamientoExportacionNotificacion.setIdTable(rowExportacion.getId());
            			documentoNotificacion = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", rowExportacion.getId().longValue(), documentoAlmacenamientoExportacionNotificacion, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_NOTIFICACION, null);
            			rowExportacion.setDocumentoNotificacion(documentoNotificacion);
            		}
            		
            		documentoAlmacenamientoExportacionAutorizacion = rowExportacion.getDocumentoAutorizacion();
            		if (documentoAlmacenamientoExportacionAutorizacion.getNombre() != null) {
            			Documento documentoAutorizacion = new Documento();
            			documentoAlmacenamientoExportacionAutorizacion.setIdTable(rowExportacion.getId());
            			documentoAutorizacion = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", rowExportacion.getId().longValue(), documentoAlmacenamientoExportacionAutorizacion, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION, null);
            			rowExportacion.setDocumentoAutorizacion(documentoAutorizacion);
            		}
            		
            		documentoAlmacenamientoExportacionMovimiento = rowExportacion.getDocumentoMovimiento();
            		if (documentoAlmacenamientoExportacionMovimiento.getNombre() != null) {
            			Documento documentoMovimiento = new Documento();
            			documentoAlmacenamientoExportacionMovimiento.setIdTable(rowExportacion.getId());
            			documentoMovimiento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", rowExportacion.getId().longValue(), documentoAlmacenamientoExportacionMovimiento, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_MOVIMIENTOS, null);
            			rowExportacion.setDocumentoMovimiento(documentoMovimiento);
            		}
            		
            		documentoAlmacenamientoExportacionDestruccion = rowExportacion.getDocumentoDestruccion();
            		if (documentoAlmacenamientoExportacionDestruccion.getNombre() != null) {
            			Documento documentoDestruccion = new Documento();
            			documentoAlmacenamientoExportacionDestruccion.setIdTable(rowExportacion.getId());
            			documentoDestruccion = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", rowExportacion.getId().longValue(), documentoAlmacenamientoExportacionDestruccion, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_DESTRUCCION, null);
            			rowExportacion.setDocumentoDestruccion(documentoDestruccion);
            		}
				}
            }
            JsfUtil.addMessageInfo("Manifiesto ingresado.");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al guardar el adjunto del Manifiesto - Transferencia. Por favor comuníquese con Mesa de Ayuda");
			LOG.error(e);
		}
		resetTransferencia();
    }
    public void editarManifiestoTransferencia(ManifiestoUnico item){
    	manifiestoUnico=item;
    	documentoManifiestoAlmacenamientoTransferencia = item.getAdjuntoManifiesto();
	}
    public void eliminarManifiestoTransferencia(ManifiestoUnico item) {
		List<DesechoGestorAlmacenamiento> listDesechoTransferencia = new ArrayList<DesechoGestorAlmacenamiento>();
		listDesechoTransferencia = desechoGestorAlmacenamientoFacade.getByManifiestoUnico(item.getId());
		if (listDesechoTransferencia.size() > 0) {
			for (DesechoGestorAlmacenamiento rowDesecho : listDesechoTransferencia) {
				eliminarDesechoGestorTransferencia(rowDesecho);
			}
		}
		listManifiestoTransferencia.remove(item);
		item.setEstado(false);
		item = manifiestoUnicoFacade.guardar(item);
		JsfUtil.addMessageInfo("Manifiesto Único eliminado");
    }
    public void resetManifiestoTransferencia(){
    	manifiestoUnico = new ManifiestoUnico();
    	documentoManifiestoAlmacenamientoTransferencia = new Documento();
    }
    public void resetTransferencia(){
    	resetManifiestoTransferencia();
    	resetDesechoGestorTransferencia();
    	resetEmpresaGestoraTransferencia();
    	resetDesechoGestorAlmacenamientoExportacion();
    }
    // Documento Manifiesto Transporte  
    public void asignarDocumentoManifiestoTransferencia(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	documentoManifiestoAlmacenamientoTransferencia.setNombre(file.getFile().getFileName());
    	documentoManifiestoAlmacenamientoTransferencia.setMime(file.getFile().getContentType());
    	documentoManifiestoAlmacenamientoTransferencia.setContenidoDocumento(file.getFile().getContents());
    	documentoManifiestoAlmacenamientoTransferencia.setExtesion(extension);
    	documentoManifiestoAlmacenamientoTransferencia.setNombreTabla(ManifiestoUnico.class.getSimpleName());
    }
    // Desechos Peligrosos Almacenamiento - Transferencia
    public void agregarDesechoGestorTransferencia() {
    	DetalleCatalogoGeneral catalogoUnidad = detalleCatalogGeneralFacade.findByCodigo("tipounidad.kilogramo");
    	desechoGestorAlmacenamiento.setDesechoPeligroso(desechoPeligroso);
    	desechoGestorAlmacenamiento.setClave(desechoPeligroso.getClave());
    	Integer idUnidad = desechoGestorAlmacenamiento.getUnidadDesechoRecepcion().getId();
    	Double cantidadIngresada = desechoGestorAlmacenamiento.getCantidad();
    	if (idUnidad.equals(catalogoUnidad.getId())) {
    		Double cantidadConvertida = cantidadIngresada * factorKgT;
    		desechoGestorAlmacenamiento.setCantidadKilogramos(cantidadIngresada);
    		desechoGestorAlmacenamiento.setCantidadToneladas(cantidadConvertida);
    	} else {
    		desechoGestorAlmacenamiento.setCantidadKilogramos(cantidadIngresada);
    		desechoGestorAlmacenamiento.setCantidadToneladas(cantidadIngresada);
    	}

    	manifiestoUnico.listDesechoTransferencia.add(desechoGestorAlmacenamiento);
    	JsfUtil.addMessageInfo("Desecho Peligroso ingresado.");

    	if (desechoGestorAlmacenamiento.getEsExportacion() != null && desechoGestorAlmacenamiento.getEsExportacion()) {
    		try {
    			desechoGestorAlmacenamientoExportacion.setDesechoGestorAlmacenamiento(desechoGestorAlmacenamiento);
    			desechoGestorAlmacenamientoExportacion.setDesechoPeligroso(desechoGestorAlmacenamiento.getDesechoPeligroso());
    			desechoGestorAlmacenamientoExportacion.setClave(desechoGestorAlmacenamiento.getDesechoPeligroso().getClave());

    			if (documentoAlmacenamientoExportacionNotificacion.getNombre() != null) {
    				documentoAlmacenamientoExportacionNotificacion.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    				desechoGestorAlmacenamientoExportacion.setDocumentoNotificacion(documentoAlmacenamientoExportacionNotificacion);
    			}
    			if (documentoAlmacenamientoExportacionAutorizacion.getNombre() != null) {
    				documentoAlmacenamientoExportacionAutorizacion.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    				desechoGestorAlmacenamientoExportacion.setDocumentoAutorizacion(documentoAlmacenamientoExportacionAutorizacion);
    			}
    			if (documentoAlmacenamientoExportacionMovimiento.getNombre() != null) {
    				documentoAlmacenamientoExportacionMovimiento.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    				desechoGestorAlmacenamientoExportacion.setDocumentoMovimiento(documentoAlmacenamientoExportacionMovimiento);
    			}
    			if (documentoAlmacenamientoExportacionDestruccion.getNombre() != null) {
    				documentoAlmacenamientoExportacionDestruccion.setNombreTabla(DesechoGestorAlmacenamientoExportacion.class.getSimpleName());
    				desechoGestorAlmacenamientoExportacion.setDocumentoDestruccion(documentoAlmacenamientoExportacionDestruccion);
    			}
    			if (!listDesechoGestorAlmacenamientoExportacion.contains(desechoGestorAlmacenamientoExportacion)) {
    				listDesechoGestorAlmacenamientoExportacion.add(desechoGestorAlmacenamientoExportacion);
    				JsfUtil.addMessageInfo("Exportación ingresada.");
    			} else {
    				JsfUtil.addMessageInfo("Exportacion modificada.");
    			}
    		} catch (Exception e) {
    			JsfUtil.addMessageError("Error al guardar los adjuntos de Exportación. Por favor comuníquese con Mesa de Ayuda");
    			LOG.error(e);
    		}
		}
    	resetDesechoGestorTransferencia();
    }
    public void eliminarDesechoGestorTransferencia(DesechoGestorAlmacenamiento item) {
    	eliminarDesechoGestorAlmacenamientoExportacion(item);
    	listDesechoGestorAlmacenamientoTransferencia.remove(item);
    	item.setEstado(false);
    	item = desechoGestorAlmacenamientoFacade.guardar(item);
    	JsfUtil.addMessageInfo("Desecho Peligroso eliminada");
    }
    public void resetDesechoGestorTransferencia(){
    	desechoGestorAlmacenamiento = new DesechoGestorAlmacenamiento();
        documentoAlmacenamientoExportacionNotificacion = new Documento();
        documentoAlmacenamientoExportacionAutorizacion = new Documento();
        documentoAlmacenamientoExportacionMovimiento = new Documento();
        documentoAlmacenamientoExportacionDestruccion = new Documento();
        desechoGestorAlmacenamientoExportacion = new DesechoGestorAlmacenamientoExportacion();
        desechoPeligroso = new DesechoPeligroso();
        listDesechosAlmacenamientoTransferencia = new ArrayList<DesechoPeligroso>();
        listDesechosAlmacenamientoTransferencia = listaDesechosTranferencia(informacionProyecto);
        esDesechoExportacion = false;
    }
    // Empresa Gestora Almacenamiento Transferencia
    public void seleccionarEmpresaGestoraTransferencia(SedePrestadorServiciosDesechos item) {
        manifiestoUnicoTransferencia.setManifiestoUnico(manifiestoUnico);
        manifiestoUnicoTransferencia.setSedePrestadorServiciosDesechos(item);
        if (manifiestoUnicoTransferencia.getId() == null) {
            if (!listEmpresasAlmacenamientoTransferencia.contains(manifiestoUnicoTransferencia)) {
                manifiestoUnicoTransferencia = manifiestoUnicoTransferenciaFacade.guardar(manifiestoUnicoTransferencia);
                listEmpresasAlmacenamientoTransferencia.add(manifiestoUnicoTransferencia);
                JsfUtil.addMessageInfo("Desecho Peligroso ingresado.");
            } else {
                JsfUtil.addMessageInfo("Desecho Peligroso modificado.");
            }
        } else {
            ManifiestoUnicoTransferencia empresaTransferenciaBase = manifiestoUnicoTransferenciaFacade.getById(manifiestoUnicoTransferencia.getId());
            if (!listEmpresasAlmacenamientoTransferencia.contains(manifiestoUnicoTransferencia)) {
                manifiestoUnicoTransferencia = manifiestoUnicoTransferenciaFacade.guardar(manifiestoUnicoTransferencia);
                listEmpresasAlmacenamientoTransferencia.add(manifiestoUnicoTransferencia);
                JsfUtil.addMessageInfo("Empresa Gestora ingresada.");
            } else {
                empresaTransferenciaBase.setEstado(false);
                empresaTransferenciaBase.setHistorial(true);
                empresaTransferenciaBase.setIdPertenece(manifiestoUnicoTransferencia.getId());
                empresaTransferenciaBase.setId(null);
                empresaTransferenciaBase = manifiestoUnicoTransferenciaFacade.guardar(empresaTransferenciaBase);
                JsfUtil.addMessageInfo("Empresa Gestora modificado.");
            }
        }
        resetManifiestoUnico();
        resetEmpresaGestoraTransferencia();
    }
    public void eliminarEmpresaGestoraTransferencia(ManifiestoUnicoTransferencia item) {
    	listEmpresasAlmacenamientoTransferencia.remove(item);
    	item.setEstado(false);
    	item = manifiestoUnicoTransferenciaFacade.guardar(item);
    	JsfUtil.addMessageInfo("Empresa Gestora eliminada");
    }
    public void resetEmpresaGestoraTransferencia() {
    	manifiestoUnicoTransferencia = new ManifiestoUnicoTransferencia();
        listSedePrestadorServiciosDesechosAlmacenamiento = new ArrayList<SedePrestadorServiciosDesechos>();
        listSedePrestadorServiciosDesechosAlmacenamiento = cargarEmpresasGestoras(FASE_ALMACENAMIENTO);
        listSedePrestadorServiciosSelected = new ArrayList<SedePrestadorServiciosDesechos>();
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
    
    // Validar datos transporte
    private boolean validarDatosTransporte() {
    	boolean validarTransporte = true;
    	try {
    		if (listRutas.isEmpty()) {
    			JsfUtil.addMessageError("Ingrese al menos una Ruta");
    			validarTransporte = false;
    		}
    		if (listManifiestoUnicoTransporte.isEmpty()) {
    			JsfUtil.addMessageError("Ingrese al menos un Manifiesto");
    			JsfUtil.addMessageError("Ingrese al menos un Desecho");
    			JsfUtil.addMessageError("Ingrese al menos una Empresa Gestora");
    			validarTransporte = false;
    		} else {
    			for (ManifiestoUnico row : listManifiestoUnicoTransporte) {
    				Integer idManifiestoUnico = row.getId();
    				listEmpresasTransporte = manifiestoUnicoTransferenciaFacade.getByManifiestoUnico(idManifiestoUnico);
    				if (listEmpresasTransporte.size() == 0) {
    					JsfUtil.addMessageError("Ingrese al menos una Empresa Gestora por cada Manifiesto");
    	    			validarTransporte = false;
					}
    				listDesechoGestorTransporte = desechoGestorTransporteFacade.getByManifiestoUnico(idManifiestoUnico);
    				if (listDesechoGestorTransporte.size() == 0) {
    					JsfUtil.addMessageError("Ingrese al menos un Desecho por cada Manifiesto");
    	    			validarTransporte = false;
					}
    			}
    		}
		} catch (Exception e) {
			LOG.error("Error al enviar el formulario.", e);
			JsfUtil.addMessageError("No se puede enviar el formulario.");
		}
    	return validarTransporte;
    }
    private boolean validarDatosAlmacenamiento() {
    	boolean validarAlmacenamiento = true;
    	try {
    		if (listManifiestoRecepcion.isEmpty()) {
    			JsfUtil.addMessageError("Ingrese al menos un Manifiesto - Recepción");
    			JsfUtil.addMessageError("Ingrese al menos un Desecho por cada Manifiesto - Recepción");
    			JsfUtil.addMessageError("Ingrese al menos una Empresa Gestora por cada Manifiesto - Recepción");
    			validarAlmacenamiento = false;
    		} else {
    			for (ManifiestoUnico row : listManifiestoRecepcion) {
    				Integer idManifiestoUnico = row.getId();
    				listDesechoGestorAlmacenamientoRecepcion = desechoGestorAlmacenamientoFacade.getByManifiestoUnico(idManifiestoUnico);
    				if (listDesechoGestorAlmacenamientoRecepcion.size() == 0) {
    					JsfUtil.addMessageError("Ingrese al menos un Desecho por cada Manifiesto");
    					validarAlmacenamiento = false;
    				}
    			}
    		}
		} catch (Exception e) {
			LOG.error("Error al enviar el formulario.", e);
			JsfUtil.addMessageError("No se puede enviar el formulario.");
		}
    	return validarAlmacenamiento;
    }
    
    public void regresarInformacionBasica() {
        JsfUtil.redirectTo("/control/retce/informacionBasica.jsf");
    }
    public void regresarDeclaracionGestor() {
    	JsfUtil.redirectTo("/control/retce/gestorDesechosPeligrososLista.jsf");
    }
    
    // Metodo para enviar el formulario
    private boolean validarDatos() {
    	boolean validar = true;
    	
    	boolean enviarAlmacenamiento = true;
    	boolean enviarTransporte = true;
		if (tabAlmacenamiento) {
			enviarAlmacenamiento = validarDatosAlmacenamiento();
		}
		if (tabTransporte) {
			enviarTransporte = validarDatosTransporte();
		}
		if (!enviarAlmacenamiento || !enviarTransporte) {
			validar = false;
		}
		return validar;
    }
    public void enviar() {
    	if (validarDatos()) {
    		boolean operacionCorrecta = false;
    		if(habilitarCorreccion){
				try {
					procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
					operacionCorrecta=true;
				} catch (JbpmException e) {					
					e.printStackTrace();
				}
			}else{
				ProcesoRetceController procesoRetceController = JsfUtil.getBean(ProcesoRetceController.class);
				if(procesoRetceController.iniciarProceso(gestorDesechosPeligrosos)){
					operacionCorrecta = true;
				}
			}
			
			if (operacionCorrecta) {
				gestorDesechosPeligrosos.setEnviado(true);
				gestorDesechosPeligrososFacade.guardar(gestorDesechosPeligrosos, JsfUtil.getLoggedUser());
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			}
		} else {
			JsfUtil.addMessageError("Error al enviar la Información. Por favor comuníquese con Mesa de Ayuda");
		}
    }

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
                    List<DesechoGestorTransporte> listAllDesechoTransporte = new ArrayList<DesechoGestorTransporte>();
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
                        listAllDesechoTransporte.addAll(listDesechoGestorTransporte);
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
                    listTotalManifiestoTransporte = calcularSumatoriaTotal(listManifiestoUnicoTransporte, listAllDesechoTransporte);
                }
    	} catch (ServiceException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
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
    
//    --WR----------
    private List<DesechoPeligrosoEliminacionRETCE> listaDesechoEliminacion(InformacionProyecto informacionProyecto){
    	List<DesechoPeligrosoEliminacionRETCE> listDesechoPeligrosoEliminacionRETCE = new ArrayList<DesechoPeligrosoEliminacionRETCE>();
    	List<Integer> listadoART = cargarListaART(informacionProyecto);
    	try {
    		for (Integer idART : listadoART) {
				List<EliminacionRecepcion> listaEliminacionRecepcion = eliminacionDesechoFacade.listarEliminacionPorAprobacionRequistos(idART);
    			for (EliminacionRecepcion eliminacionRecepcion : listaEliminacionRecepcion) {
    				DesechoPeligrosoEliminacionRETCE desechoPeligrosoEliminacionRETCE = new DesechoPeligrosoEliminacionRETCE();
    				Integer idEliminacionRecepcion = eliminacionRecepcion.getId();
    				Integer idRecepcionDesechoPeligroso = eliminacionRecepcion.getIdRecepcionDesechoPeligroso();
    				RecepcionDesechoPeligroso recepcionDesechoPeligroso = recepcionDesechoPeligrosoFacade.getById(idRecepcionDesechoPeligroso);
    				desechoPeligrosoEliminacionRETCE.setDesechoPeligroso(recepcionDesechoPeligroso.getDesecho());
    				List<EliminacionDesecho> listaEliminacionDesecho = eliminacionDesechoFacade.listarEliminacionRecepcionPorId(idEliminacionRecepcion);
    				for (EliminacionDesecho eliminacionDesecho : listaEliminacionDesecho) {
    					desechoPeligrosoEliminacionRETCE.setEliminacionDesecho(eliminacionDesecho);
    					desechoPeligrosoEliminacionRETCE.setTipoEliminacionDesecho(eliminacionDesecho.getTipoEliminacionDesecho());
    					desechoPeligrosoEliminacionRETCE.setModalidad(eliminacionDesecho.getTipoEliminacionDesecho().getTipoEliminacionDesecho());
    				}
    				listDesechoPeligrosoEliminacionRETCE.add(desechoPeligrosoEliminacionRETCE);
    			}
    		}
    		
    		List<InformacionProyectoDesechosPeligrosos> listDesechosInformacion = new ArrayList<InformacionProyectoDesechosPeligrosos>();
    		listDesechosInformacion = informacionProyectoDesechosPeligrososFacade.findByProyecto(informacionProyecto);
    		for (InformacionProyectoDesechosPeligrosos row : listDesechosInformacion) {
    			DesechoPeligrosoEliminacionRETCE desechoPeligrosoEliminacionRETCE = new DesechoPeligrosoEliminacionRETCE();
    			desechoPeligrosoEliminacionRETCE.setDesechoPeligroso(row.getDesechosPeligrosos());
    			listDesechoPeligrosoEliminacionRETCE.add(desechoPeligrosoEliminacionRETCE);
			}
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Error al cargar los desechos para Eliminación. Por favor comuníquese con Mesa de Ayuda");
			LOG.error(e);
		}
    	return listDesechoPeligrosoEliminacionRETCE;
    }
    private List<DesechoPeligrosoEliminacionRETCE> listaDesechoDisposicionFinal(InformacionProyecto informacionProyecto){
    	List<DesechoPeligrosoEliminacionRETCE> listDesechoPeligrosoEliminacionRETCE = new ArrayList<DesechoPeligrosoEliminacionRETCE>();
    	List<Integer> listadoART = cargarListaART(informacionProyecto);
    	try {
    		for (Integer idART : listadoART) {
				List<EliminacionRecepcion> listaEliminacionRecepcion = eliminacionDesechoFacade.listarEliminacionPorAprobacionRequistos(idART);
    			for (EliminacionRecepcion eliminacionRecepcion : listaEliminacionRecepcion) {
    				DesechoPeligrosoEliminacionRETCE desechoPeligrosoEliminacionRETCE = new DesechoPeligrosoEliminacionRETCE();
    				Integer idEliminacionRecepcion = eliminacionRecepcion.getId();
    				Integer idRecepcionDesechoPeligroso = eliminacionRecepcion.getIdRecepcionDesechoPeligroso();
    				RecepcionDesechoPeligroso recepcionDesechoPeligroso = recepcionDesechoPeligrosoFacade.getById(idRecepcionDesechoPeligroso);
    				List<EliminacionDesecho> listaEliminacionDesecho = eliminacionDesechoFacade.listarEliminacionRecepcionPorId(idEliminacionRecepcion);
    				for (EliminacionDesecho eliminacionDesecho : listaEliminacionDesecho) {
    					if (eliminacionDesecho.getTipoEliminacionDesecho().getCodigoModalidad() == ModalidadGestionDesechos.ID_MODALIDAD_DISPOSICION_FINAL) {
    						desechoPeligrosoEliminacionRETCE.setDesechoPeligroso(recepcionDesechoPeligroso.getDesecho());
    						desechoPeligrosoEliminacionRETCE.setEliminacionDesecho(eliminacionDesecho);
    						desechoPeligrosoEliminacionRETCE.setTipoEliminacionDesecho(eliminacionDesecho.getTipoEliminacionDesecho());
    						desechoPeligrosoEliminacionRETCE.setModalidad(eliminacionDesecho.getTipoEliminacionDesecho().getTipoEliminacionDesecho());
						}
    				}
    				if (desechoPeligrosoEliminacionRETCE.getDesechoPeligroso() != null) {
    					listDesechoPeligrosoEliminacionRETCE.add(desechoPeligrosoEliminacionRETCE);
					}
    			}
    		}
    		
    		List<InformacionProyectoCambioDesechosPeligrosos> listDesechosCambio = informacionProyectoCambioDesechosPeligrososFacade.findByProyecto(informacionProyecto);
    		for (InformacionProyectoCambioDesechosPeligrosos row : listDesechosCambio) {
    			DesechoPeligrosoEliminacionRETCE desechoPeligrosoEliminacionRETCE = new DesechoPeligrosoEliminacionRETCE();
    			desechoPeligrosoEliminacionRETCE.setDesechoPeligroso(row.getDesechosPeligrosos());
    			listDesechoPeligrosoEliminacionRETCE.add(desechoPeligrosoEliminacionRETCE);
			}
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Error al cargar los desechos para Disposición Final. Por favor comuníquese con Mesa de Ayuda");
			LOG.error(e);
		}
    	return listDesechoPeligrosoEliminacionRETCE;
    }

    public List<EliminacionRecepcion> cargarArtDesechos(Integer art)
    {
    	try {
    		eliminacionDesechosBean.setEliminacionesRecepciones(new ArrayList<EliminacionRecepcion>());
			aprobacionRequisitosTecnicos=aprobacionRequisitosTecnicosFacade.getAprobacionRequisitosTecnicosPorId(art);
			obtenerListaDesechos();
			obtenerListaEliminaciones();
			return eliminacionDesechosBean.getEliminacionesRecepciones();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return eliminacionDesechosBean.getEliminacionesRecepciones();
    }
    public void obtenerListaDesechos() {
		try {
			List<EntityRecepcionDesecho> enti = eliminacionDesechoFacade
					.obtenerPorAprobacionRequisitosTecnicos(getAprobacionRequisitosTecnicos().getId());
			eliminacionDesechosBean.setListaEntityRecepcionDesecho(enti);
		} catch (Exception e) {
			LOG.error("Error al obtener los desechos.", e);
			JsfUtil.addMessageError("No se puede obtener los almacenes.");
		}
	}
    public void obtenerListaEliminaciones() {
		try {
			List<EliminacionRecepcion> listaEliminacionRecepcion = eliminacionDesechoFacade
					.listarEliminacionPorAprobacionRequistos(aprobacionRequisitosTecnicos.getId());
			if (listaEliminacionRecepcion != null && !listaEliminacionRecepcion.isEmpty()) {
				eliminacionDesechosBean.setEliminacionesRecepciones(listaEliminacionRecepcion);
				int indice = 0;
				for (EliminacionRecepcion eli : eliminacionDesechosBean.getEliminacionesRecepciones()) {
					eli.setIndice(indice);
					entityRecepcion(eli);
					cargarDetalleEliminacion(eli.getEliminacionDesechos());
					indice++;
				}
			}
		} catch (Exception e) {
			LOG.error("Error al obtener los desechos.", e);
			JsfUtil.addMessageError("No se puede obtener los almacenes.");
		}
	}
    public void entityRecepcion(EliminacionRecepcion eliminacionRecepcion) {
		for (EntityRecepcionDesecho e : eliminacionDesechosBean.getListaEntityRecepcionDesecho()) {
			if (eliminacionRecepcion.getIdRecepcionDesechoPeligroso().intValue() == e.getIdRecepcion()) {
				eliminacionRecepcion.setEntityRecepcionDesecho(e);
				break;
			}
		}
	}
    public void cargarDetalleEliminacion(List<EliminacionDesecho> listaEliminacionDesecho) throws ServiceException {
		int indice = 0;
		for (EliminacionDesecho eliDes : listaEliminacionDesecho) {
			eliDes.setIndice(indice);
			if (eliDes.getDesecho() != null) {
				eliDes.setDesecho(eliminacionDesechoFacade.obtenerDesecho(eliDes.getIdDesecho()));
			}
			indice++;
		}
	}
    
    public void cargarListaGestroDesechosEliminados()
    {
    	listGestorDesechosEliminacion = new ArrayList<GestorDesechosEliminacion>();
    	listGestorDesechosEliminacion=gestorDesechosEliminacioFacade.listarGestorEliminacion(gestorDesechosPeligrosos);
    }
    public void cargarListaGestroDisFinal()
    {
    	listaDesechosDisFinal = new ArrayList<GestorDesechosDisposicionFinal>();
    	listaDesechosDisFinal=gestorDesechosDisposicionFinalFacade.listarGestorDisfinal(gestorDesechosPeligrosos);
    }
    
    public String verArtEliminacion(GestorDesechosEliminacion elimi) throws ServiceException
    {    	
//    	return gestorDesechosDisposicionFinalFacade.getArtPorTipoElimiancion(elimi.getEliminacionDesecho()).getSolicitud();
    	return "";
    }
    public String verNombreDesecho(GestorDesechosEliminacion elimi) throws ServiceException
    {    	
    	return gestorDesechosEliminacioFacade.obtenerDesecho(elimi).getDescripcion();
    }
    
    public String verArtDisfinal(GestorDesechosDisposicionFinal elimi) throws ServiceException
    {    	
//    	return gestorDesechosDisposicionFinalFacade.getArtPorTipoElimiancion(elimi.getEliminacionDesecho()).getSolicitud();
    	return "";
    }
    public String verNombreDesechoDisfinal(GestorDesechosDisposicionFinal elimi) throws ServiceException
    {    	
    	return gestorDesechosDisposicionFinalFacade.obtenerDesecho(elimi).getDescripcion();
    }
    
    public void tipoEliminacionSeleccionado(GestorDesechosEliminacion gestor) throws ServiceException
    {
    	gestorDesechosEliminacion= new GestorDesechosEliminacion();
    	gestorDesechosEliminacion=gestor;
    	generaTipoResiduo=0;
    	desechoES0406=false;
    	laboratorio= new LaboratorioGeneral();
    	if(gestorDesechosEliminacion.getTipoDesecho()==1)
    	{
    		listarGestorDesechosEliminacionDesechoPeligroso=gestorDesechosEliminacionDesechoPeligrosoFacade.getByDesechoEliminacion(gestorDesechosEliminacion.getId());
    		
    	}
    	if(gestorDesechosEliminacion.getTipoDesecho()==2)
    	{
    		listarGestorDesechosEliminacionDesechoNoPeligroso=gestorDesechosEliminacionDesechoNoPeligrosoFacade.getByDesechoEliminacion(gestorDesechosEliminacion.getId());
    	}
    	String desechoCodigo=gestor.getDesechoPeligroso().getClave();
    	if(desechoCodigo.equals("ES-04") || desechoCodigo.equals("ES-06"))
    		desechoES0406=true;
    	
    	verAdjuntoCalculo();
    }
    
    public void tipoDesecho()
    {	
    	if(gestorDesechosEliminacion.getTipoDesecho()==1)
    	{
    		listarGestorDesechosEliminacionDesechoPeligroso=gestorDesechosEliminacionDesechoPeligrosoFacade.getByDesechoEliminacion(gestorDesechosEliminacion.getId());
    		
    	}
    	if(gestorDesechosEliminacion.getTipoDesecho()==2)
    	{
    		listarGestorDesechosEliminacionDesechoNoPeligroso=gestorDesechosEliminacionDesechoNoPeligrosoFacade.getByDesechoEliminacion(gestorDesechosEliminacion.getId());
    	}
    }
    
    public void tipoDesechoSeleccionado(DesechoPeligrosoEliminacionRETCE item) throws ServiceException
    {	
    	GestorDesechosEliminacion gde;
    	listGestorDesechosEliminacion = new ArrayList<GestorDesechosEliminacion>();

    	gde= new GestorDesechosEliminacion();
    	if(item.getEliminacionDesecho()!=null)
    	{
    		gde=gestorDesechosEliminacioFacade.gestorDesechoEliminacion(gestorDesechosPeligrosos, item.getEliminacionDesecho());
    		if(gde.getId()==null)
    		{    		
    			gde.setEliminacionDesecho(item.getEliminacionDesecho());
    			gde.setGestorDesechosPeligrosos(gestorDesechosPeligrosos);
    			gde.setEstado(true);
    			gde.setGeneraDesecho(false);
    			gde.setTipoDesecho(0);
    			gde.setDesechoPeligroso(item.getDesechoPeligroso());
    			gde=gestorDesechosEliminacioFacade.guardarEliminacion(gde);
    		}
    	}
    	else
    	{
    		gde=gestorDesechosEliminacioFacade.gestorDesechoEliminacion(gestorDesechosPeligrosos, item.getDesechoPeligroso());
    		if(gde.getId()==null)
    		{    		
    			gde.setEliminacionDesecho(item.getEliminacionDesecho());
    			gde.setGestorDesechosPeligrosos(gestorDesechosPeligrosos);
    			gde.setEstado(true);
    			gde.setGeneraDesecho(false);
    			gde.setTipoDesecho(0);
    			gde.setDesechoPeligroso(item.getDesechoPeligroso());
    			gde=gestorDesechosEliminacioFacade.guardarEliminacion(gde);
    		}
    		
    	}
    	cargarListaGestroDesechosEliminados();
    }
    
    public void desechoDispoFinalSeleccionado(DesechoPeligrosoEliminacionRETCE item) throws ServiceException
    {	    	
    	GestorDesechosDisposicionFinal gdf;
    	gdf= new GestorDesechosDisposicionFinal();
    	if(item.getEliminacionDesecho()!=null)
    	{
    		gdf=gestorDesechosDisposicionFinalFacade.gestorDesechoDisFinal(gestorDesechosPeligrosos, item.getEliminacionDesecho());
    		if(gdf.getId()==null)
    		{
    			gdf.setEliminacionDesecho(item.getEliminacionDesecho());
    			gdf.setGestorDesechosPeligrosos(gestorDesechosPeligrosos);
    			gdf.setEstado(true);
    			gdf.setDesechoPeligroso(item.getDesechoPeligroso());
    			gdf=gestorDesechosDisposicionFinalFacade.guardarDisFinal(gdf);
    		}
    	}
    	else
    	{
    		gdf=gestorDesechosDisposicionFinalFacade.gestorDesechoDisFinal(gestorDesechosPeligrosos, item.getDesechoPeligroso());
    		if(gdf.getId()==null)
    		{
    			gdf.setEliminacionDesecho(item.getEliminacionDesecho());
    			gdf.setGestorDesechosPeligrosos(gestorDesechosPeligrosos);
    			gdf.setEstado(true);
    			gdf.setDesechoPeligroso(item.getDesechoPeligroso());
    			gdf=gestorDesechosDisposicionFinalFacade.guardarDisFinal(gdf);
    		}
    	}   		
    	cargarListaGestroDisFinal();
    }
    
    public void onNodeSelect(NodeSelectEvent event) {
    	gestorDesechosEliminacionDesechoPeligroso.setDesechoPeligroso((DesechoPeligroso) event.getTreeNode().getData());
    }
    
    public void refrescarDesechos()
    {
    	gestorDesechosEliminacion.setTipoDesecho(0);
    	gestorDesechosEliminacionDesechoPeligroso.setDesechoPeligroso(new DesechoPeligroso());
    }
    public void fileUploadCalculo(FileUploadEvent event) {
    	documentoCalculo= new Documento();
    	fileAdjuntoCalculo = event.getFile();
		documentoCalculo=UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoCalculo.getContents(),fileAdjuntoCalculo.getFileName());
	}
    public void fileUploadInformeLaboratorio(FileUploadEvent event) {
    	documentoInformeLaboratorio= new Documento();
    	fileAdjuntoCalculo = event.getFile();
    	documentoInformeLaboratorio=UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoCalculo.getContents(),fileAdjuntoCalculo.getFileName());
	}
    public void verAdjuntoCalculo()
    {
    	adjuntaArchivoCalculo=false;
    	esMedicionDirecta=false;
    	if(gestorDesechosEliminacion.getMetodoEstimacion()==null || gestorDesechosEliminacion.getMetodoEstimacion().isEmpty())
    	{
    		adjuntaArchivoCalculo=false;
    		return;
    	}    	
    	if(gestorDesechosEliminacion.getMetodoEstimacion().equals("Medición directa"))
    	{
    		esMedicionDirecta=true;
    		documentoInformeLaboratorio= new Documento();
    		adjuntaArchivoCalculo=true;
    		if(gestorDesechosEliminacion.getDocuId()!=null)
    		{
    			documentoInformeLaboratorio = documentosFacade.buscarDocumentoPorId(gestorDesechosEliminacion.getDocuId());			
    		}
    	}
    	else
    	{
    		esMedicionDirecta=false;
    		documentoCalculo= new Documento();
    		adjuntaArchivoCalculo=true;
    		if(gestorDesechosEliminacion.getDocuId()!=null)
    		{
    			documentoCalculo = documentosFacade.buscarDocumentoPorId(gestorDesechosEliminacion.getDocuId());			
    		}
    	}
    		    		
    }
    public void guardarEliminacion()
    {	
    	DetalleCatalogoGeneral catalogoUnidad = detalleCatalogGeneralFacade.findByCodigo("tipounidad.kilogramo");
    	if(gestorDesechosEliminacion.getUnidadMedida()==null)
    	{    		
    		JsfUtil.addMessageError("Seleccione unidad");
    		return;
    	}
    	if(gestorDesechosEliminacion.getGeneraDesecho())
    	{
    		if(gestorDesechosEliminacion.getTipoDesecho()==0)
    		{
    			JsfUtil.addMessageError("Seleccione el tipo de residuo/desecho generado");
    			return;
    		}


    		if(gestorDesechosEliminacion.getTipoDesecho()==1)
    		{
    			if(listarGestorDesechosEliminacionDesechoPeligroso.size()==0)
    			{
    				JsfUtil.addMessageError("Agregar un residuo/desecho peligroso o especial generado");
    				return;
    			}
    		}
    		if(gestorDesechosEliminacion.getTipoDesecho()==2)
    		{
    			if(listarGestorDesechosEliminacionDesechoNoPeligroso.size()==0)
    			{
    				JsfUtil.addMessageError("Agregar un residuo/desecho no peligroso");
    				return;
    			}
    		}
    	}    	
    	if(gestorDesechosEliminacion.getCatalogoSustancias()==null)
    	{    		
    		JsfUtil.addMessageError("Seleccione sustancias RETCE");
    		return;
    	}
    	if(gestorDesechosEliminacion.getMetodoEstimacion()==null || gestorDesechosEliminacion.getMetodoEstimacion().isEmpty())
    	{
    		JsfUtil.addMessageError("Seleccione un método de estimación");
    		return;
    	}
    	if(gestorDesechosEliminacion.getMetodoEstimacion().equals("Medición directa") && gestorDesechosEliminacion.getLaboratorioGeneral().getId()==null)
    	{
    		JsfUtil.addMessageError("Seleccione un laboratorio");
    		return;
    	}    		
    	if(documentoCalculo.getId()==null && documentoCalculo.getContenidoDocumento()==null)
    	{
    		JsfUtil.addMessageError("Adjuntar un archivo");
    		return;
    	}
    	
    	Integer idUnidad = gestorDesechosEliminacion.getUnidadMedida().getId();
    	Double cantidadIngresada = gestorDesechosEliminacion.getCantidad();    	
    	if (idUnidad.equals(catalogoUnidad.getId())) {
    		Double cantidadConvertida = cantidadIngresada * factorKgT;
    		gestorDesechosEliminacion.setCantidadKilo(cantidadIngresada);
    		gestorDesechosEliminacion.setCantidadTonelada(cantidadConvertida);
		} else {
			gestorDesechosEliminacion.setCantidadKilo(cantidadIngresada);
			gestorDesechosEliminacion.setCantidadTonelada(cantidadIngresada);
		}
    	
    	gestorDesechosEliminacioFacade.guardarEliminacion(gestorDesechosEliminacion);    	
		if(adjuntaArchivoCalculo && documentoCalculo.getContenidoDocumento()!=null)
		{
			try {
				documentoCalculo.setIdTable(gestorDesechosEliminacion.getId());
				documentoCalculo.setNombreTabla(GestorDesechosEliminacion.class.getSimpleName());
				documentoCalculo = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", gestorDesechosEliminacion.getId().longValue(), documentoCalculo, TipoDocumentoSistema.RETCE_SUSTANCIA_CALCULO, null);
				gestorDesechosEliminacion.setDocuId(documentoCalculo.getId());
				gestorDesechosEliminacioFacade.guardarEliminacion(gestorDesechosEliminacion);
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}
		}
		cargarListaGestroDesechosEliminados();
		gestorDesechosEliminacion=new GestorDesechosEliminacion();
		gestorDesechosEliminacionDesechoPeligroso= new GestorDesechosEliminacionDesechoPeligroso();
		gestorDesechosEliminacionDesechoNoPeligroso=new GestorDesechosEliminacionDesechoNoPeligroso();
		laboratorio=new LaboratorioGeneral();
		
		RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('idDlgTipoEliminacion').hide();");
		
    }
    
    public void nuevoResiduoPeligrososNoPeligrosos()
    {
    	if(gestorDesechosEliminacion.getTipoDesecho()==1)
    	{	
    		gestorDesechosEliminacionDesechoPeligroso= new GestorDesechosEliminacionDesechoPeligroso();
    	}
    	if(gestorDesechosEliminacion.getTipoDesecho()==2)
    	{    	    		
    		gestorDesechosEliminacionDesechoNoPeligroso=new GestorDesechosEliminacionDesechoNoPeligroso();
    	}
    }
    
    public void guardarResiduosPeligorsosNoPeligrosos()
    {	
    	if(gestorDesechosEliminacion.getTipoDesecho()==1)
    	{	
    		if(!gestorDesechosEliminacionDesechoPeligrosoFacade.existeDesechoPeligroso(gestorDesechosEliminacion, gestorDesechosEliminacionDesechoPeligroso.getDesechoPeligroso()))
    		{
    			gestorDesechosEliminacionDesechoPeligroso.setGestorDesechosEliminacion(gestorDesechosEliminacion);
    			gestorDesechosEliminacionDesechoPeligrosoFacade.guardarDesechoPeligroso(gestorDesechosEliminacionDesechoPeligroso);
    			listarGestorDesechosEliminacionDesechoPeligroso=gestorDesechosEliminacionDesechoPeligrosoFacade.getByDesechoEliminacion(gestorDesechosEliminacion.getId());
    			RequestContext context = RequestContext.getCurrentInstance();
    	        context.execute("PF('idDialogResiduosPeligrosos').hide();");
    		}
    		else
    		{
    			JsfUtil.addMessageError("Residuo/desecho peligroso o especial generado ya existe");
    			RequestContext context = RequestContext.getCurrentInstance();
    	        context.execute("PF('idDialogResiduosPeligrosos').show();");
        		return;
    			
    		}
    	}
    	if(gestorDesechosEliminacion.getTipoDesecho()==2)
    	{	
    			gestorDesechosEliminacionDesechoNoPeligroso.setGestorDesechosEliminacion(gestorDesechosEliminacion);
    			gestorDesechosEliminacionDesechoNoPeligrosoFacade.guardarDesechoNoPeligroso(gestorDesechosEliminacionDesechoNoPeligroso);
    			listarGestorDesechosEliminacionDesechoNoPeligroso=gestorDesechosEliminacionDesechoNoPeligrosoFacade.getByDesechoEliminacion(gestorDesechosEliminacion.getId());
    	
    	}
    }
    
    public void residuoPeligrosoSeleccionado(GestorDesechosEliminacionDesechoPeligroso gestor)
    {
    	gestorDesechosEliminacionDesechoPeligroso=gestor;
    }
    
    public void residuoNoPeligrosoSeleccionado(GestorDesechosEliminacionDesechoNoPeligroso gestor)
    {
    	gestorDesechosEliminacionDesechoNoPeligroso=gestor;
    }
    
    public void modificarResiduoPeligroso(GestorDesechosEliminacionDesechoPeligroso gestor)
    {
    	gestorDesechosEliminacionDesechoPeligroso=gestor;
    	gestorDesechosEliminacionDesechoPeligroso.setEstado(false);
    	gestorDesechosEliminacionDesechoPeligrosoFacade.guardarDesechoPeligroso(gestorDesechosEliminacionDesechoPeligroso);
    	listarGestorDesechosEliminacionDesechoPeligroso=gestorDesechosEliminacionDesechoPeligrosoFacade.getByDesechoEliminacion(gestorDesechosEliminacion.getId());
    }
    
    public void modificarResiduoNoPeligroso(GestorDesechosEliminacionDesechoNoPeligroso gestor)
    {
    	gestorDesechosEliminacionDesechoNoPeligroso=gestor;
    	gestorDesechosEliminacionDesechoNoPeligroso.setEstado(false);
    	gestorDesechosEliminacionDesechoNoPeligrosoFacade.guardarDesechoNoPeligroso(gestorDesechosEliminacionDesechoNoPeligroso);
    	listarGestorDesechosEliminacionDesechoNoPeligroso=gestorDesechosEliminacionDesechoNoPeligrosoFacade.getByDesechoEliminacion(gestorDesechosEliminacion.getId());
    }
    
    public void modificarEliminacion(GestorDesechosEliminacion gestor)
    {
    	gestor.setEstado(false);
    	gestorDesechosEliminacioFacade.guardarEliminacion(gestor);
    	listGestorDesechosEliminacion.remove(gestor);
    }
    public void guardarDisFinal()
    {
    	DetalleCatalogoGeneral catalogoUnidad = detalleCatalogGeneralFacade.findByCodigo("tipounidad.kilogramo");
    	if(desechoDisFinalSeleccionado.getUnidadMedida()==null)
    	{    		
    		JsfUtil.addMessageError("Seleccione unidad");
    		return;
    	}
    	if(desechoDisFinalSeleccionado.getCatalogoSustancias()==null)
    	{    		
    		JsfUtil.addMessageError("Seleccione sustancias RETCE");
    		return;
    	}
    	if(desechoDisFinalSeleccionado.getMetodoEstimacion()==null || desechoDisFinalSeleccionado.getMetodoEstimacion().isEmpty())
    	{
    		JsfUtil.addMessageError("Seleccione un método de estimación");
    		return;
    	}
    	if(!desechoDisFinalSeleccionado.getMetodoEstimacion().equals("Medición directa"))
    	{
    		if(documentoCalculo.getId()==null && documentoCalculo.getContenidoDocumento()==null)
    		{
    			JsfUtil.addMessageError("Adjuntar un archivo");
    			return;
    		}
    	}
    	
    	Integer idUnidad = desechoDisFinalSeleccionado.getUnidadMedida().getId();
    	Double cantidadIngresada = desechoDisFinalSeleccionado.getCantidad();
    	if (idUnidad.equals(catalogoUnidad.getId())) {
    		Double cantidadConvertida = cantidadIngresada * factorKgT;
    		desechoDisFinalSeleccionado.setCantidadKilo(cantidadIngresada);
    		desechoDisFinalSeleccionado.setCantidadTonelada(cantidadConvertida);
		} else {
			desechoDisFinalSeleccionado.setCantidadKilo(cantidadIngresada);
			desechoDisFinalSeleccionado.setCantidadTonelada(cantidadIngresada);
		}
    	gestorDesechosDisposicionFinalFacade.guardarDisFinal(desechoDisFinalSeleccionado);
    	if(adjuntaArchivoCalculo && documentoCalculo.getContenidoDocumento()!=null)
		{
			try {
				documentoCalculo.setIdTable(desechoDisFinalSeleccionado.getId());
				documentoCalculo.setNombreTabla(GestorDesechosDisposicionFinal.class.getSimpleName());
				documentoCalculo = documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechosPeligrosos.getCodigo(), "GESTOR_DESECHOS_PELIGROSOS", desechoDisFinalSeleccionado.getId().longValue(), documentoCalculo, TipoDocumentoSistema.RETCE_SUSTANCIA_CALCULO, null);
				desechoDisFinalSeleccionado.setDocuId(documentoCalculo.getId());
				gestorDesechosDisposicionFinalFacade.guardarDisFinal(desechoDisFinalSeleccionado);
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}
		}
    	cargarListaGestroDisFinal();
    	
    	RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('idDlgTipoDisFinal').hide();");
    }
    
    public void modificarDisFinal(GestorDesechosDisposicionFinal gestor)
    {
    	gestor.setEstado(false);
    	gestorDesechosDisposicionFinalFacade.guardarDisFinal(gestor);
    	listaDesechosDisFinal.remove(gestor);
    }

    public void tipoDisFinalSeleccionado(GestorDesechosDisposicionFinal gestor) throws ServiceException
    {
    	desechoDisFinalSeleccionado=gestor;
    	desechoES0406=false;
    	String desechoCodigo=gestor.getDesechoPeligroso().getClave();
    	if(desechoCodigo.equals("ES-04") || desechoCodigo.equals("ES-06"))
    		desechoES0406=true;
    	
    	verAdjuntoCalculoDisFinal();
    	
    	RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('idDlgTipoDisFinal').show();");
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
    
    public void validarCedula() {
        try {           
        	String ruc="";
            if (laboratorio.getRuc() != null) {
                if (laboratorio.getRuc().length() == 13) {
                	ruc=laboratorio.getRuc();
                	//validar si existe ruc ya registrado en LaboratorioGeneral
                	laboratorio=laboratorioGeneralFacade.laboratorioGeneralXRuc(ruc);
                	if(laboratorio.getId()!=null)
                		return;
                	//validar si existe ruc ya registrado en datosLaboratorio
                	datosLaboratorio=laboratorioGeneralFacade.datoLaboratorioXRuc(ruc);
                	if(datosLaboratorio.getId()!=null)
                	{
                		laboratorio.setRuc(datosLaboratorio.getRuc());
                		laboratorio.setNombre(datosLaboratorio.getNombre());
                		laboratorio.setResgistroSae(datosLaboratorio.getNumeroRegistroSAE());
                		laboratorio.setFechaVigencia(datosLaboratorio.getFechaVigenciaRegistro());
                		laboratorio.setEstado(true);
                		laboratorio=laboratorioGeneralFacade.guardar(laboratorio);
                		return;
                	}
                	
                    ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula.obtenerPorRucSRI(Constantes.USUARIO_WS_MAE_SRI_RC,Constantes.PASSWORD_WS_MAE_SRI_RC,ruc);
                    if (contribuyenteCompleto.getNumeroRuc() == null) {
                        JsfUtil.addMessageError("RUC inválido");
                        return;
                    }	
                    if (!contribuyenteCompleto.getCodEstado().equals("PAS")
                            || Constantes.getPermitirRUCPasivo()) {
                        cargarDatosWsRucPersonaNatural(contribuyenteCompleto);
                    } else {
                        JsfUtil.addMessageError("El estado de su RUC es PASIVO. Si desea registrarse con el mismo debe activarlo en el SRI.");
                    }
                } else {
                    JsfUtil.addMessageError("El RUC debe tener 13 dígitos.");
                }

            } else {
                JsfUtil.addMessageError("El campo 'Cédula / RUC / Pasaporte' es requerido.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void cargarDatosWsRucPersonaNatural(
            ContribuyenteCompleto contribuyenteCompleto) {
        if (contribuyenteCompleto != null) {
            if (contribuyenteCompleto.getRazonSocial() != null) {               
            	laboratorio.setRuc(contribuyenteCompleto.getNumeroRuc());
            	laboratorio.setNombre(contribuyenteCompleto.getRazonSocial());

            } else {
                JsfUtil.addMessageError("RUC no encontrado.");
            }
        } else {
            JsfUtil.addMessageError("Sin Servicio");
        }
    }
	
	public void buscarLaboratorio()
	{
		laboratorio= new LaboratorioGeneral();
	}
	
	public void guardarLaboratorio()
	{
		laboratorio.setEstado(true);
		laboratorio=laboratorioGeneralFacade.guardar(laboratorio);
		gestorDesechosEliminacion.setLaboratorioGeneral(laboratorio);
	}
}