package ec.gob.ambiente.control.retce.controllers;

import index.Campos_coordenada;
import index.ContieneCapa_entrada;
import index.IntersecadoCapa_capa;
import index.InterseccionCapa_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.digitalizacion.CoordenadasPoligonos;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.CoordenadaDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.DocumentoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ShapeDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.UbicacionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.CoordenadaDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.DocumentoDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.ShapeDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.retce.model.ActividadCiiu;
import ec.gob.ambiente.retce.model.CatalogoEstadoFisico;
import ec.gob.ambiente.retce.model.CatalogoPeriodicidad;
import ec.gob.ambiente.retce.model.CatalogoProductos;
import ec.gob.ambiente.retce.model.CatalogoServicios;
import ec.gob.ambiente.retce.model.CatalogoTipoContenedor;
import ec.gob.ambiente.retce.model.CatalogoUnidades;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.InformacionProyectoAprobacionRequisitosTecnicos;
import ec.gob.ambiente.retce.model.InformacionProyectoDesechosPeligrosos;
import ec.gob.ambiente.retce.model.InformacionProyectoCambioDesechosPeligrosos;
import ec.gob.ambiente.retce.model.InformacionProyectoDesechoFases;
import ec.gob.ambiente.retce.model.FaseRetce;
import ec.gob.ambiente.retce.model.FormaInformacionProyecto;
import ec.gob.ambiente.retce.model.Inclusion;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.Producto;
import ec.gob.ambiente.retce.model.Servicio;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.retce.model.ProyectoInformacionUbicacionGeografica;
import ec.gob.ambiente.retce.services.ActividadCiiuFacade;
import ec.gob.ambiente.retce.services.CatalogoEstadoFisicoFacade;
import ec.gob.ambiente.retce.services.CatalogoPeriodicidadFacade;
import ec.gob.ambiente.retce.services.CatalogoProductosFacade;
import ec.gob.ambiente.retce.services.CatalogoServiciosFacade;
import ec.gob.ambiente.retce.services.CatalogoTipoContenedorFacade;
import ec.gob.ambiente.retce.services.CatalogoUnidadesFacade;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.FaseRetceFacade;
import ec.gob.ambiente.retce.services.InclusionFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoAprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.ProductoFacade;
import ec.gob.ambiente.retce.services.ServicioFacade;
import ec.gob.ambiente.retce.services.TecnicoResponsableFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoCambioDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoDesechoFasesFacade;
import ec.gob.ambiente.retce.services.ProyectoInformacionUbicacionGeograficaFacade;
import ec.gob.ambiente.services.MaeLicenseResponse;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.comun.bean.AdicionarUbicacionesBean;
import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.BuscarProyectoFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.FormaProyecto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityFichaCompleta;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.service.FichaAmbientalPmaServiceBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class InformacionBasicaController {
	
	private static final Logger LOG = Logger.getLogger(InformacionBasicaController.class);
	private static 	final String BANDEJA_RETCE ="/control/retce/informacionBasica.jsf";
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{adicionarUbicacionesBean}")
    private AdicionarUbicacionesBean adicionarUbicacionesBean;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    
	/*@Getter
	@Setter
	@ManagedProperty(value = "#{cargarCoordenadasBean}")
	private CargarCoordenadasBean cargarCoordenadasBean;*/
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    /*EJBs*/ 
	
	@EJB
	private ActividadCiiuFacade catalogoActividadCiiuFacade;

	@EJB
	private AreaFacade areaFacade;

	@EJB
	private BuscarProyectoFacade buscarProyectoFacade;

	@EJB
	private CatalogoCategoriasFacade catalogoCategoriasFacade;

	@EJB
	private CatalogoEstadoFisicoFacade catalogoEstadoFisicoFacade;

	@EJB
	private CatalogoPeriodicidadFacade catalogoPeriodicidadFacade;

	@EJB
	private CatalogoProductosFacade catalogoProductosFacade;

	@EJB
	private CatalogoServiciosFacade catalogoServiciosFacade;

	@EJB
	private CatalogoTipoContenedorFacade catalogoTipoContenedorFacade;

	@EJB
	private CatalogoUnidadesFacade catalogoUnidadesFacade;

	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;

	@EJB
	private InformacionProyectoDesechosPeligrososFacade informacionProyectoDesechosPeligrososFacade;

	@EJB
	private InformacionProyectoCambioDesechosPeligrososFacade informacionProyectoCambioDesechosPeligrososFacade;

	@EJB
	private InformacionProyectoAprobacionRequisitosTecnicosFacade informacionProyectoAprobacionRequisitosTecnicosFacade;

	@EJB
	private InformacionProyectoDesechoFasesFacade InformacionProyectoDesechoFasesFacade;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private FaseRetceFacade faseRetceFacade;

	@EJB
	private FichaAmbientalPmaServiceBean fichaAmbientalPmaServiceBean;

	@EJB
	private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;

	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;

	@EJB
	private ObservacionesFacade observacionesFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private ContactoFacade contactoFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private ProductoFacade productoFacade;

	@EJB
	private ProyectoLicenciaAmbientalFacade projectoFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private ServicioFacade servicioFacade;

	@EJB
	private TecnicoResponsableFacade tecnicoResponsableFacade;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private ProyectoInformacionUbicacionGeograficaFacade proyectoInformacionUbicacionGeograficaFacade;

	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	
	/***********************  rcoa ***********************************/
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;

	@EJB
	private DocumentosRegistroAmbientalFacade documentosRegistroRCOAFacade;
	
	@EJB
	private DocumentosCoaFacade documentosCoaFacade;
	
	@EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalShapeFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionfacade;
	
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;

	/*List*/
	@Getter
	private List<ActividadCiiu> actividadCiiuList,subActividadCiiuList;

	@Getter
	private List<Area> areasList, areasSeguimientoList;

	@Getter
	private List<CatalogoEstadoFisico> catalogoEstadoFisicoList;

	@Getter
	private List<CatalogoCategoriaSistema> catalogoActividadesList;	

	@Getter
	private List<CatalogoPeriodicidad> catalogoPeriodicidadList;

	@Getter
	private List<CatalogoProductos> catalogoProductosList;

	@Getter
	private List<CatalogoServicios> catalogoServiciosList;

	@Getter
	private List<CatalogoTipoContenedor> catalogoTipoContenedorList;
		
	@Getter
	private List<CatalogoUnidades> catalogoUnidadesList;
	
	@Getter
	private List<FaseRetce> faseRetceList;
	
	@Getter
	private List<FormaInformacionProyecto> listaCoordenadas;
	
	@Getter
	private FormaInformacionProyecto formasProyectos;
	
	@Getter
	private List<InformacionProyecto> informacionProyectoList;
	
	@Getter
	private List<UbicacionesGeografica> listaUbicacionesGeograficas;
	
	@Getter
	private List<ProyectoInformacionUbicacionGeografica> ubicacionesGeograficas, ubicacionesGeograficasEliminar;
	
	@Setter
	@Getter
	private List<Producto> productosList;
	
	private List<Producto> productosEliminadosList, productosListOriginal;
	
	@Getter
	private List<ProyectoCustom> proyectosBuscarList;

	@Setter
	@Getter
	private List<DetalleCatalogoGeneral> catalogoTipoGestionList;

	@Setter
	@Getter
	private List<DetalleCatalogoGeneral> catalogoFasesList;

	@Setter
	@Getter
	private List<InformacionProyectoDesechosPeligrosos> listaProyectoDesechos, listaProyectoDesechosOriginal, listaEliminarProyectoDesechos;

	@Setter
	@Getter
	private List<InformacionProyectoAprobacionRequisitosTecnicos> listaProyectoART, listaProyectoEliminarART;

	@Setter
	@Getter
	private List<InformacionProyectoDesechoFases> listaProyectoDesechoFases;

	@Setter
	@Getter
	private List<InformacionProyectoCambioDesechosPeligrosos> listaProyectoCambioDesechos, listaProyectoCambioDesechosOriginal, listaEliminarProyectoCambioDesechos;

	@Setter
	@Getter
	private List<AprobacionRequisitosTecnicos> listaAprobacionRequisitostecnicos;

	@Getter
	@Setter
	private List<String> listaFasesGestionDesechosId, listaFasesGestionDesechosAuxId, listaDesechosId, listaCambiosDesechosId, listaARTId;

	@Setter
	@Getter
	private List<Servicio> serviciosList;
	private List<Servicio> serviciosEliminadosList;
	
	@Getter
	private List<TipoSector> tipoSectorList;
	
	@Getter
	private List<Documento> ListarDocumentosPorProceso;

	@Setter
	@Getter
	private ActividadCiiu actividadCiiu;
	
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
	@Setter
	@Getter
	private MaeLicenseResponse maeLicenseResponse;
	
	@Setter
	@Getter
	private InformacionProyecto informacionProyecto, objInformacinOriginal;
	
	@Setter
	@Getter
	private TecnicoResponsable tecnicoResponsable;
	
	@Setter
	@Getter
	private UbicacionesGeografica provincia,canton,parroquia;
	
	@Setter
	@Getter
	private TipoSector tipoSector;
	
	@Setter
	@Getter
	private Servicio servicio;
	
	@Setter
	@Getter
	private Producto producto;
	
	@Setter
	@Getter
	private Documento adjuntoMapa,adjuntoCoordenadas,adjuntoResolucion;
	
	private boolean actualizaMapa,actualizaResolucion,actualizaInclusiones, actualizaCoordenadas;

	@Setter
	@Getter
	private boolean habilitarIngProductos, proyectoSinProcesar, esRcoa;
	
	@Setter
	@Getter
	private UploadedFile fileAdjuntoResolucion,fileAdjuntoMapa,fileAdjuntoInclusion,fileAdjuntoOficio, fileAdjuntoEstudioInclusion, fileAdjuntoCoordenadas;	
	
	private Usuario usuarioOperador;

	@Getter
	@Setter
	private Boolean emiteSuia;

	@Getter
	@Setter
	private Boolean gestionDesechos, gestor;

	@Getter
	@Setter   
	private Integer tipoGestion, tipoGestionResiduosEspeciales, tipoGestionResiduosPeligrosos;

	@Getter
	@Setter
	private String faseAlmacenamiento, faseTransporte, faseEliminación, faseDisposición;

	@Getter
	private boolean disabledFase,verFormulario,habilitarIngreso,habilitarRevision,habilitarCorreccion,habilitarObservaciones,editarObservaciones,mostrarMensajeART;

	@Setter
	@Getter
	private String buscarCodigo,buscarNombre,buscarTipoTramite, urlContenido;

	@Getter
	private String nombreUsuario,representanteLegal;

	@Getter       
	private String correo,telefono,direccion, msjLicenciaFisica="";

	private String clausulaOperador;

	@Getter
	@Setter   
	private String modalidad;

	@Setter
	@Getter
	private String nombreAdjuntoResolucion;

	@Getter
	@Setter	
	private Map<Integer, List<String>> filtrosSettingsMap = new HashMap<>(), filtrosMapResiduosDesechos = new HashMap<>();

	@Getter
	@Setter 
	private List<String> listarFasesdesechosCambio, listarFasesResiduosDesechos;
	
	private Map<String, Object> variables;

	@Getter
	private List<UbicacionesGeografica> provinciasList,cantonesList,parroquiasList;
	
	@Getter
	@Setter
	private List<EntityFichaCompleta> getLicenses;
	
	/*************************************  Digitalizacion ***************************************************************/
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private CoordenadaDigitalizacionFacade coordenadaDigitalizacionFacade;
	@EJB
	private ShapeDigitalizacionFacade shapeDigitalizacionFacade;
	@EJB
	private UbicacionDigitalizacionFacade ubicacionDigitalizacionFacade;
	@EJB
	private DocumentoDigitalizacionFacade documentoDigitalizacionFacade;
	
	/*************************************  RCOA ***************************************************************/
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Setter
	@Getter
	private DocumentoRegistroAmbiental adjuntoResolucionRcoa;
	
	@Setter
	@Getter
	private DocumentosCOA adjuntoMapaRcoa,adjuntoCoordenadasRcoa;
	
	@Getter
	@Setter
	private List<ProyectoLicenciaAmbientalCoaShape> formas;
	
	@Getter
	@Setter
	private List<CoordenadasProyecto> coordenadasGeograficas;
	
	@Getter
	@Setter
    private List<CoordendasPoligonos> coordinatesWrappers, coordinatesWrappersGeo;
	
	@Getter
	@Setter
	private List<Integer> wolframSocial= new ArrayList<Integer>();
	
	@Getter
	@Setter
    private List<Integer> wolframBiofisica= new ArrayList<Integer>();
	
    @Getter
	@Setter
	private UbicacionesGeografica ubicacionOficinaTecnica = new UbicacionesGeografica();
    
	@Getter
	@Setter
	private boolean estadoFrontera, estadoZonaIntangible, estadoZonaIntangibleAux, esZonaSnapEspecial;
	
	@Getter
	@Setter
	private HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> listaCapasInterseccionPrincipal = new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();

	@Getter
	@Setter
	private List<String> listaNombresCapasInterseca = new ArrayList<>();
	
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
	
	@Getter
	@Setter
	private BigDecimal superficieProyecto, superficieMetrosCuadrados;
	
	@Getter
	@Setter
	private Boolean mostrarDetalleInterseccion;
	@Getter
	@Setter
	private HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> capasIntersecciones = new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
	
	@PostConstruct
	public void init() 
	{
		urlContenido = "/control/retce/formularios/formInformacionSuia.xhtml";
		esRcoa=false;
		JsfUtil.cargarObjetoSession("idGestorDesechosPeligrosos", null);
		cargarListaInfbasica();		
		if(informacionProyectoList.isEmpty()){
			cargarDatosIniciales();
			verFormulario=true;
		}
				
		buscarDatosOperador();
		habilitarProductos();
		
		Object tarea= FacesContext.getCurrentInstance().getViewRoot().getAttributes().get("tarea");
		if(tarea!=null)
			cargarProyectoTarea();
	}
	
	private void habilitarProductos(){
		String fecha_ini = "01/01", fecha_fin ="03/31";
		List<DetalleCatalogoGeneral> obgDetallecat = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("operador.periodo_productos");
		if(obgDetallecat.size() > 0){
			fecha_ini = obgDetallecat.get(0).getParametro();
			fecha_fin = obgDetallecat.get(0).getParametro2(); 
		}
		Calendar fecha = Calendar.getInstance();
		int anio = fecha.get(Calendar.YEAR);
		Date fechaInicio=null, fechaFin=null, fechaactual = new Date();
		try {
			fechaInicio = new SimpleDateFormat("yyyy/dd/MM").parse(anio+"/"+fecha_ini);
			fechaFin = new SimpleDateFormat("yyyy/dd/MM").parse(anio+"/"+fecha_fin);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		habilitarIngProductos = (fechaactual.before(fechaFin) && fechaactual.after(fechaInicio));
	}
	
	private void cargarListaInfbasica(){
		informacionProyectoList=informacionProyectoFacade.findByUser(JsfUtil.getLoggedUser());
		habilitarIngreso=true;
		proyectoSinProcesar=true;
		habilitarRevision=false;
		mostrarMensajeART=false;
		gestionDesechos=null;
		tipoGestion=null;
		listaFasesGestionDesechosId= new ArrayList<String>();
		listaFasesGestionDesechosAuxId= new ArrayList<String>();
		listaDesechosId= new ArrayList<String>();
		listaDesechosId.add("0");
		listaCambiosDesechosId= new ArrayList<String>();
		listaCambiosDesechosId.add("0");
		listaARTId= new ArrayList<String>();
		listaARTId.add("0");
		filtrosMapResiduosDesechos = null;
	}
	
	private void cargarProyectoTarea(){		
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			String tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);		
			informacionProyecto=informacionProyectoFacade.findByCodigo(tramite);
			editarInfBasica(informacionProyecto);
			
			if(bandejaTareasBean.getTarea().getTaskName().toUpperCase().contains("INGRESAR")){
				habilitarIngreso=true;
				habilitarRevision=false;
				habilitarCorreccion=true;
			}else{
				habilitarIngreso=false;
				habilitarRevision=true;
				habilitarCorreccion=false;
				editarObservaciones=true;
			}
			
			habilitarObservaciones=true;
			
		} catch (Exception e) {
			LOG.error("No se pudo cargar Informacion Basica de la tarea.");			
		}
	}
	
	
	private void cargarDatosIniciales(){
		catalogoActividadesList=new ArrayList<CatalogoCategoriaSistema>();
		actividadCiiuList=new ArrayList<ActividadCiiu>();				
		faseRetceList=new ArrayList<FaseRetce>();
		informacionProyecto=new InformacionProyecto();
		objInformacinOriginal=new InformacionProyecto();
		productosList=new ArrayList<Producto>();
		productosListOriginal=new ArrayList<Producto>();
		productosEliminadosList=new ArrayList<Producto>();
		proyectosBuscarList=new ArrayList<ProyectoCustom>();
		serviciosList=new ArrayList<Servicio>();
		serviciosEliminadosList=new ArrayList<Servicio>();
		tecnicoResponsable=new TecnicoResponsable();
		inclusionList= new ArrayList<Inclusion>();
		inclusionEliminadosList= new ArrayList<Inclusion>();
		listaProyectoDesechos= new ArrayList<InformacionProyectoDesechosPeligrosos>();
		listaProyectoDesechosOriginal= new ArrayList<InformacionProyectoDesechosPeligrosos>();
		listaEliminarProyectoDesechos= new ArrayList<InformacionProyectoDesechosPeligrosos>();
		listaProyectoCambioDesechos= new ArrayList<InformacionProyectoCambioDesechosPeligrosos>();
		listaProyectoCambioDesechosOriginal= new ArrayList<InformacionProyectoCambioDesechosPeligrosos>();
		listaEliminarProyectoCambioDesechos= new ArrayList<InformacionProyectoCambioDesechosPeligrosos>();
		listaProyectoART= new ArrayList<InformacionProyectoAprobacionRequisitosTecnicos>();
		listaProyectoEliminarART= new ArrayList<InformacionProyectoAprobacionRequisitosTecnicos>();
		listaAprobacionRequisitostecnicos= new ArrayList<AprobacionRequisitosTecnicos>();
		areasSeguimientoList = new ArrayList<Area>();
		listarFasesdesechosCambio= new ArrayList<String>();
		listarFasesResiduosDesechos= new ArrayList<String>();
		ubicacionesGeograficas = new ArrayList<ProyectoInformacionUbicacionGeografica>();
		ubicacionesGeograficasEliminar = new ArrayList<ProyectoInformacionUbicacionGeografica>();
		filtrosMapResiduosDesechos = null;
		cargarCatalogos();
	}

	private void buscarDatosOperador()	 
	{
		usuarioOperador=informacionProyecto!=null && informacionProyecto.getUsuarioCreacion()!=null?usuarioFacade.buscarUsuario(informacionProyecto.getUsuarioCreacion()):JsfUtil.getLoggedUser();
		Organizacion organizacion=null;
		try {
			if(usuarioOperador.getNombre().length() == 13){
				organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());				
				if(organizacion != null && organizacion.getNombre() != null){
					List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);					
					for (Contacto contact : contacto) {
				if (contact.getFormasContacto().getId() == FormasContacto.EMAIL && !contact.getValor().isEmpty() && contact.getEstado().equals(true)) {
		                	correo = contact.getValor();
		                }
		                if (contact.getFormasContacto().getId() == FormasContacto.DIRECCION && !contact.getValor().isEmpty() && contact.getEstado().equals(true)) {
		                	direccion = contact.getValor();
		                }
		                if (contact.getFormasContacto().getId() == FormasContacto.TELEFONO && !contact.getValor().isEmpty() && contact.getEstado().equals(true)) {
		                	telefono = contact.getValor();
		                }
		            }
				}else{
					List<Contacto> contacto = contactoFacade.buscarPorPersona(usuarioOperador.getPersona());				
					for (Contacto contact : contacto) {	                
		                if (contact.getFormasContacto().getId() == 5 && contact.getEstado().equals(true)) {
		                	correo = contact.getValor();
		                }
		                if (contact.getFormasContacto().getId() == 2 && contact.getEstado().equals(true)) {
		                	direccion = contact.getValor();
		                }
		                if (contact.getFormasContacto().getId() == FormasContacto.TELEFONO && !contact.getValor().isEmpty() && contact.getEstado().equals(true)) {
		                	telefono = contact.getValor();
		                }   
		            }	
				}
			}else{
				List<Contacto> contacto = contactoFacade.buscarPorPersona(usuarioOperador.getPersona());				
				for (Contacto contact : contacto) {	                
	                if (contact.getFormasContacto().getId() == 5 && contact.getEstado().equals(true)) {
	                	correo = contact.getValor();
	                }
	                if (contact.getFormasContacto().getId() == 2 && contact.getEstado().equals(true)) {
	                	direccion = contact.getValor();
	                }
	                if (contact.getFormasContacto().getId() == FormasContacto.TELEFONO && !contact.getValor().isEmpty() && contact.getEstado().equals(true)) {
	                	telefono = contact.getValor();
	                }
	            }	
			}
			nombreUsuario=JsfUtil.getNombreOperador(usuarioOperador, organizacion);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	private void cargarCatalogos()
	{
		try {
			provinciasList=ubicacionGeograficaFacade.getProvincias();
			actividadCiiuList=catalogoActividadCiiuFacade.findAll(null);
			areasList=areaFacade.getAreasResponsables();
			catalogoEstadoFisicoList=catalogoEstadoFisicoFacade.findAll();
			catalogoProductosList=catalogoProductosFacade.findAll();
			catalogoTipoContenedorList=catalogoTipoContenedorFacade.findAll();
			catalogoServiciosList=catalogoServiciosFacade.findAll();
			catalogoUnidadesList=catalogoUnidadesFacade.findAll();
			catalogoTipoGestionList=detalleCatalogoGeneralFacade.findByCatalogoGeneralString("tipo.gestion.desechos");
			if(catalogoTipoGestionList != null && catalogoTipoGestionList.size() > 0){
				for (DetalleCatalogoGeneral objCatalogo : catalogoTipoGestionList) {
					if(objCatalogo.getDescripcion().toLowerCase().contains("residuos o desechos especiales"))
						tipoGestionResiduosEspeciales = objCatalogo.getId();
					if(objCatalogo.getDescripcion().toLowerCase().contains("residuos o desechos peligrosos"))
						tipoGestionResiduosPeligrosos = objCatalogo.getId();
				}
			}
			catalogoFasesList=detalleCatalogoGeneralFacade.findByCatalogoGeneralString("gestor.fase_gestion");
			if(catalogoFasesList != null && catalogoFasesList.size() > 0 ){
				for (DetalleCatalogoGeneral objCatalogo : catalogoFasesList) {
					if(objCatalogo.getDescripcion().toLowerCase().contains("almacenamiento"))
						faseAlmacenamiento = objCatalogo.getId().toString();
					if(objCatalogo.getDescripcion().toLowerCase().contains("transporte"))
						faseTransporte = objCatalogo.getId().toString();
					if(objCatalogo.getDescripcion().toLowerCase().contains("eliminación"))
						faseEliminación = objCatalogo.getId().toString();
					if(objCatalogo.getDescripcion().toLowerCase().contains("disposición"))
						faseDisposición = objCatalogo.getId().toString();
				}
			}
			cargarTipoSectores();			
		} catch (Exception e) {
			LOG.error("Error al cargar los catalogos en InformacionBasicaController.");
		}
	}
	
	private void cargarProyectodesechos(){
		listaProyectoDesechos = informacionProyectoDesechosPeligrososFacade.findByProyecto(informacionProyecto);
		listaProyectoCambioDesechos = informacionProyectoCambioDesechosPeligrososFacade.findByProyecto(informacionProyecto);
		listaProyectoART = informacionProyectoAprobacionRequisitosTecnicosFacade.findByProyecto(informacionProyecto);
	}
	
	private void cargarListasOriginales(){
		listaProyectoCambioDesechosOriginal = informacionProyectoCambioDesechosPeligrososFacade.findByProyecto(informacionProyecto);
		listaProyectoDesechosOriginal = informacionProyectoDesechosPeligrososFacade.findByProyecto(informacionProyecto);
		productosListOriginal=productoFacade.findByProyecto(informacionProyecto);
	}
	
	public void cambioNumeroResolucion(){
		listaAprobacionRequisitostecnicos= new ArrayList<AprobacionRequisitosTecnicos>();
	}
	
	public void seleccionarFaseGestion(){
		mostrarMensajeART=false;
		// para seleccionar la modalidad de acuerdo a la fase
		if(tipoGestion != null && tipoGestion.equals(tipoGestionResiduosPeligrosos)){
			modalidad = "0";
			if(listaFasesGestionDesechosId.toString().contains(faseTransporte.toString()) ){
				modalidad +=", 7";
			}
			if(listaFasesGestionDesechosId.toString().contains(faseEliminación.toString()) ){
				modalidad +=", 1, 2, 3, 4, 5 ";
			}
			if(listaFasesGestionDesechosId.toString().contains(faseDisposición.toString()) ){
				modalidad +=", 6";
			}
			if((listaFasesGestionDesechosId.toString().contains(faseEliminación.toString()) && !listaFasesGestionDesechosAuxId.toString().contains(faseEliminación.toString()) )
					||  (listaFasesGestionDesechosId.toString().contains(faseDisposición.toString()) && !listaFasesGestionDesechosAuxId.toString().contains(faseDisposición.toString()))
					||  (listaFasesGestionDesechosId.toString().contains(faseTransporte.toString()) && !listaFasesGestionDesechosAuxId.toString().contains(faseTransporte.toString())) 
					|| ( (listaFasesGestionDesechosId.toString().contains(faseTransporte.toString()) || listaFasesGestionDesechosId.toString().contains(faseEliminación.toString()) || listaFasesGestionDesechosId.toString().contains(faseDisposición.toString())) &&  listaFasesGestionDesechosId.size() == listaFasesGestionDesechosAuxId.size() ) ){
				listaAprobacionRequisitostecnicos= new ArrayList<AprobacionRequisitosTecnicos>();
				if(informacionProyecto.getInformacionEnviada()  == null || informacionProyecto.getInformacionValidada() == null){
					mostrarMensajeART = true;
				}else if(!informacionProyecto.getInformacionEnviada() && (informacionProyecto.getInformacionValidada()==null || informacionProyecto.getInformacionValidada()==true))
					mostrarMensajeART = true;
			}
			if((!listaFasesGestionDesechosId.toString().contains(faseEliminación.toString()) && listaFasesGestionDesechosAuxId.toString().contains(faseEliminación.toString()) ) 
					|| (!listaFasesGestionDesechosId.toString().contains(faseDisposición.toString()) && listaFasesGestionDesechosAuxId.toString().contains(faseDisposición.toString())) 
					|| (!listaFasesGestionDesechosId.toString().contains(faseTransporte.toString()) && listaFasesGestionDesechosAuxId.toString().contains(faseTransporte.toString())) 
					|| (!listaFasesGestionDesechosId.toString().contains(faseEliminación.toString()) && !listaFasesGestionDesechosId.toString().contains(faseDisposición.toString()) && !listaFasesGestionDesechosId.toString().contains(faseTransporte.toString())  ) ){
				listaAprobacionRequisitostecnicos= new ArrayList<AprobacionRequisitosTecnicos>();
			}
			listaFasesGestionDesechosAuxId = listaFasesGestionDesechosId;
			if(mostrarMensajeART){
				//JsfUtil.addMessageInfo("Estimado usuario, los desechos que corresponden a las fases de Eliminación (reúso, reciclaje, tratamiento, coprocesamiento, incineración y/o Disposición final (celda/relleno de seguridad) deberán ser declarados en la sección de almacenamiento en la declaración gestión anual  de residuos y desechos peligrosos y especiales en el periodo correspondiente.");
			}
		}
	}

	public String getEtiquetaFase(){
		String etiqueta = "";
		if(tipoGestion != null){
			for (DetalleCatalogoGeneral catalogo : catalogoTipoGestionList) {
				if(catalogo.getId().equals(tipoGestion)){
					etiqueta = catalogo.getDescripcion();
					break;
				}
			}
		}
		return etiqueta ;
	}
	
	private void cargarTipoSectores()
	{
		tipoSectorList=new ArrayList<TipoSector>();		
		for (TipoSector tipoSector : proyectoLicenciamientoAmbientalFacade.getTiposSectores()) {
			if(tipoSector.getId()!=5){
				tipoSectorList.add(tipoSector);
			}
		}		
	}
	
	private void buscarProyectos() {
		if(emiteSuia && proyectosBuscarList.isEmpty())
		{
			//List<ProyectoCustom> proyectos=proyectoLicenciamientoAmbientalFacade.getAllProjectsByUser(loginBean.getUsuario()); //se elimina para ocultar proyectos de 4categorias (porque no tienen toda la información requerida en el formulario)
			List<ProyectoCustom> proyectos = proyectoLicenciamientoAmbientalFacade.getProjectsSuiaAzulHidrocarburosByUsuario(loginBean.getUsuario());
			for (ProyectoCustom proyecto : proyectos) {
				if(proyecto.getCategoriaNombrePublico().contains("Registro Ambiental") || proyecto.getCategoriaNombrePublico().contains("Licencia Ambiental")){
					InformacionProyecto proyectoAux=informacionProyectoFacade.findByCodigo(proyecto.getCodigo());
					if(proyectoAux == null || proyectoAux.getId() == null )
						proyectosBuscarList.add(proyecto);
				}
			}			
		}
	}
	
	public void seleccionarProyecto(String codigo){
		if(!validarProyectoRegistrado(codigo))
			return;
		informacionProyecto=new InformacionProyecto();
		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(codigo);
		if(proyectoLicenciaCoa != null && proyectoLicenciaCoa.getId() != null ){
			seleccionarProyectoRCOA(codigo);
		}else{
			AutorizacionAdministrativaAmbiental autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(codigo);
			if(autorizacionAdministrativa != null && autorizacionAdministrativa.getId() != null){
				seleccionarProyectoDigitalizacion(autorizacionAdministrativa);
			}else{
				informacionProyecto=new InformacionProyecto();
				seleccionarProyectoSUIA(codigo);
			}
		}
		getUrlFormulario(informacionProyecto.isEsProyectoRcoa());
	}
	
	private void getUrlFormulario(boolean esProyectoRCOA){
		if(esProyectoRCOA){
			urlContenido = "/control/retce/formularios/formInformacionRcoa.xhtml";
			esRcoa=true;
		}else{
			urlContenido = "/control/retce/formularios/formInformacionSuia.xhtml";
			esRcoa=false;
		}
	}

	public void seleccionarProyectoDigitalizacion(AutorizacionAdministrativaAmbiental autorizacionAdministrativa)
	{
		try {
			informacionProyecto=new InformacionProyecto();
			parroquia=null;
			documentosListener(null);
						
			switch (autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental()) {
			case "Registro Ambiental":
			case "Licencia Ambiental":
				informacionProyecto.setNombreProceso(autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental());
				break;
			default:
				JsfUtil.addMessageWarning("El proyecto debe ser Registro Ambiental o Licencia Ambiental");
				return;
			}

			
			informacionProyecto.setCodigo(autorizacionAdministrativa.getCodigoProyecto());
			informacionProyecto.setNombreProyecto(autorizacionAdministrativa.getNombreProyecto());

				if(!autorizacionAdministrativa.getEstado())
				{
					JsfUtil.addMessageError("Proyecto no encontrado o archivado.");
					return;
				}
				
				informacionProyecto.setTipoSector(autorizacionAdministrativa.getTipoSector());
				//TODO: MODIFICAR A OFICINAS TECNICAS
				String coodenadas="";
				
				List<ShapeDigitalizacion> objShape = shapeDigitalizacionFacade.obtenerShape(autorizacionAdministrativa.getId(), 2);
				if(objShape != null && objShape.size() > 0){
					for (ShapeDigitalizacion shapeAux : objShape) {
						coodenadas="";
						List<CoordenadaDigitalizacion> objCoordenadasDigitalizacion = coordenadaDigitalizacionFacade.obtenerCoordenadas(shapeAux.getId());
						if(objCoordenadasDigitalizacion != null){
							for (CoordenadaDigitalizacion objCoordenada : objCoordenadasDigitalizacion) {
								coodenadas += (coodenadas == "") ? objCoordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+objCoordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString() : ","+objCoordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+objCoordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString();
							}
						}
						verificarCapasInterseccion(coodenadas);
						informacionProyecto.setAreaResponsable(ubicacionOficinaTecnica.getAreaCoordinacionZonal());
						informacionProyecto.setAreaSeguimiento(ubicacionOficinaTecnica.getAreaCoordinacionZonal());
					}
				}
				ubicacionDigitalizacionListener(autorizacionAdministrativa);
				informacionProyecto.setFechaInicioOperaciones(autorizacionAdministrativa.getFechaInicioResolucion());

				informacionProyecto.setNumeroResolucion(autorizacionAdministrativa.getResolucion());
				informacionProyecto.setFechaEmision(autorizacionAdministrativa.getFechaInicioResolucion());

				documentosDigitalizacionListener(autorizacionAdministrativa.getId());
				
				sectorListener();

				listaAprobacionRequisitostecnicos = aprobacionRequisitosTecnicosFacade.findARTByCodigoByModo(loginBean.getUsuario(), autorizacionAdministrativa.getCodigoProyecto(), getEmiteSuia(), "");
				for (AprobacionRequisitosTecnicos requisitos : listaAprobacionRequisitostecnicos) {
					requisitos.setSeleccionado(true);
				}
				agregarARTProyecto();
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo encontrar el proyecto.");
			LOG.error(e.getMessage());
			return;
		}
	}

	public void seleccionarProyectoSUIA(String codigo)
	{
		try {
			informacionProyecto=new InformacionProyecto();
			parroquia=null;
			ubicacionListener();
			documentosListener(null);
			maeLicenseResponse = buscarProyectoFacade.buscarProyecto(codigo,loginBean.getUsuario().getNombre());		
			if(maeLicenseResponse.getCodigoProyecto()==null){
				if(!maeLicenseResponse.getMensaje().equals("No se puede conectar a la BD..,.")){
					JsfUtil.addMessageWarning(maeLicenseResponse.getMensaje());
					return;
				}else{
					ProyectoLicenciamientoAmbiental proyecto=projectoFacade.getProyectoPorCodigo(codigo);
					if(proyecto!=null)
					{
						maeLicenseResponse = new MaeLicenseResponse();
						maeLicenseResponse.setNombreProyecto(proyecto.getNombre());
						maeLicenseResponse.setCodigoProyecto(proyecto.getCodigo());
						maeLicenseResponse.setProyectoSuia("verde");
						maeLicenseResponse.setLicencia(true);
						maeLicenseResponse.setCodigoTipoEstudio(null);
						maeLicenseResponse.setCategoria(proyecto.getCatalogoCategoria().getCategoria().getNombrePublico());
						maeLicenseResponse.setIsHidrocarburos(false);
						informacionProyecto.setNombreProceso(proyecto.getCatalogoCategoria().getCategoria().getNombrePublico());
					}
				}
			}
						
			switch (maeLicenseResponse.getCategoria()) {
			case "I":				
				JsfUtil.addMessageWarning("El proyecto debe ser Registro Ambiental o Licencia Ambiental");
				return;
				//break;
			case "II":				
				informacionProyecto.setNombreProceso("Registro Ambiental");
				break;
			case "III":				
				informacionProyecto.setNombreProceso("Licencia Ambiental");
				break;
			case "IV":				
				informacionProyecto.setNombreProceso("Licencia Ambiental");
				break;
			default:				
				break;
			}
			
			informacionProyecto.setCodigo(maeLicenseResponse.getCodigoProyecto());
			informacionProyecto.setNombreProyecto(maeLicenseResponse.getNombreProyecto());
			if(maeLicenseResponse.getProyectoSuia().compareTo("verde")==0 && maeLicenseResponse.isIsHidrocarburos() && !projectoFacade.validarPermisoHidrocarburos(maeLicenseResponse.getCodigoProyecto()))
			{
				JsfUtil.addMessageWarning("El proyecto no tiene permiso ambiental.");
				informacionProyecto=new InformacionProyecto();
				return;
			}
			
			ProyectoLicenciamientoAmbiental proyecto=projectoFacade.getProyectoPorCodigo(maeLicenseResponse.getCodigoProyecto());
			if(proyecto!=null)
			{
				if(!proyecto.getEstado())
				{
					JsfUtil.addMessageError("Proyecto no encontrado o archivado.");
					return;
				}
				
				if(maeLicenseResponse.getProyectoSuia().compareTo("azul")==0 && !projectoFacade.validarPermisoPorIdProyecto(proyecto))
				{
					JsfUtil.addMessageWarning("El proyecto no tiene permiso ambiental.");
					informacionProyecto=new InformacionProyecto();
					return;
				}
				
				informacionProyecto.setTipoSector(proyecto.getTipoSector());
				//TODO: MODIFICAR A OFICINAS TECNICAS
				for (FormaProyecto listaFormas : proyecto.getFormasProyectos()) {
					String coodenadas="";
					if (listaFormas.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO)){
						for (Coordenada coordenada : listaFormas.getCoordenadas()) {
							coodenadas += (coodenadas == "") ? BigDecimal.valueOf(coordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString() : ","+BigDecimal.valueOf(coordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString();
						}
					}
					if(coodenadas.isEmpty())
						continue;
					verificarCapasInterseccion(coodenadas);
					informacionProyecto.setAreaResponsable(ubicacionOficinaTecnica.getAreaCoordinacionZonal());
					informacionProyecto.setAreaSeguimiento(ubicacionOficinaTecnica.getAreaCoordinacionZonal());
					if(ubicacionOficinaTecnica != null && ubicacionOficinaTecnica.getAreaCoordinacionZonal() != null)
						break;
				}
				informacionProyecto.setFechaInicioOperaciones(proyecto.getFechaInicioOperaciones());
				parroquia=ubicacionGeograficaFacade.listarPorProyecto(proyecto).get(0);
				if (parroquia != null ){
					areasSeguimientoList=areaFacade.getAreasSeguimiento(parroquia);
				}
				if(maeLicenseResponse.getCategoria().compareTo("II")==0)
				{
					if(informacionProyecto.getTipoSector().getId()==2 && proyecto.getCatalogoCategoria().getId()!=851)
					{
						FichaAmbientalMineria fichaAmbientalMineria= fichaAmbientalMineriaFacade.obtenerPorId(proyecto.getId());
						if(fichaAmbientalMineria!=null){
							informacionProyecto.setNumeroResolucion(fichaAmbientalMineria.getNumeroResolucion());
							informacionProyecto.setFechaEmision(fichaAmbientalMineria.getFechaEmisionPermiso());
						}
					}else{
						FichaAmbientalPma fichaAmbientalPma= fichaAmbientalPmaServiceBean.getFichaAmbientalPorIdProyecto(proyecto.getId());
						if(fichaAmbientalPma!=null){
							informacionProyecto.setNumeroResolucion(fichaAmbientalPma.getNumeroLicencia());
							informacionProyecto.setFechaEmision(fichaAmbientalPma.getFechaModificacion());
						}
					}
				}
				ubicacionListener();
				documentosListener(proyecto.getId());
				sectorListener();

				listaAprobacionRequisitostecnicos = aprobacionRequisitosTecnicosFacade.findARTByCodigoByModo(loginBean.getUsuario(), codigo, getEmiteSuia(), "");
				for (AprobacionRequisitosTecnicos requisitos : listaAprobacionRequisitostecnicos) {
					requisitos.setSeleccionado(true);
				}
				agregarARTProyecto();
			}
		} catch (ServiceException e) {
			JsfUtil.addMessageError("No se pudo encontrar el proyecto.");
			LOG.error(e.getMessage());
			return;
		}
	}

	public void seleccionarProyectoRCOA(String codigo)
	{
		try {
			parroquia=null;
		
			if(proyectoLicenciaCoa.getCategorizacion() == 2) {
				informacionProyecto.setNombreProceso("Registro Ambiental");
			}else if(proyectoLicenciaCoa.getCategorizacion() == 3 || proyectoLicenciaCoa.getCategorizacion() == 4) {
				informacionProyecto.setNombreProceso("Licencia Ambiental");
			}else{				
				JsfUtil.addMessageWarning("El proyecto debe ser Registro Ambiental o Licencia Ambiental");
			}
			
			informacionProyecto.setCodigo(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
			informacionProyecto.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
			informacionProyecto.setEsProyectoRcoa(true);
			if(proyectoLicenciaCoa!=null)
			{
				ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
				CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
				
				informacionProyecto.setTipoSector(actividadPrincipal.getTipoSector());
				//TODO: OFICINA TECNICA 
				formas = proyectoLicenciaAmbientalShapeFacade.buscarFormaGeograficaPorProyecto(proyectoLicenciaCoa, 2, 0); //coordenadas geograficas
				if(formas == null){
					formas = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();				
				}else{
					coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
					for(ProyectoLicenciaAmbientalCoaShape forma : formas){
						coordenadasGeograficas = coordenadasProyectoCoaFacade.buscarPorForma(forma);
						CoordendasPoligonos poligono = new CoordendasPoligonos();
						poligono.setCoordenadas(coordenadasGeograficas);
						poligono.setTipoForma(forma.getTipoForma());
						
						coordinatesWrappers.add(poligono);
					}
					String coodenadas="";
					for(int i=0;i<=coordinatesWrappers.size()-1;i++){
						coodenadas="";
						for (int j = 0; j <=coordinatesWrappers.get(i).getCoordenadas().size()-1; j++) {
							coodenadas += (coodenadas == "") ? BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getX().longValue()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getY().longValue()).setScale(5, RoundingMode.HALF_DOWN).toString() : ","+BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getX().longValue()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getY().longValue()).setScale(5, RoundingMode.HALF_DOWN).toString();
						}
					}
					verificarCapasInterseccion(coodenadas);
				}				
				informacionProyecto.setAreaResponsable(ubicacionOficinaTecnica.getAreaCoordinacionZonal());
				informacionProyecto.setAreaSeguimiento(proyectoLicenciaCoa.getAreaInventarioForestal());
				//informacionProyecto.setFechaInicioOperaciones(proyectoLicenciaCoa.getFechaInicioOperaciones());
				UbicacionesGeografica ubicacionPrincipal=proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa).getUbicacionesGeografica();
				if (ubicacionPrincipal != null && ubicacionPrincipal.getUbicacionesGeografica() != null){
					parroquia = ubicacionPrincipal;
				}
				if (parroquia != null ){
					areasSeguimientoList=areaFacade.getAreasSeguimiento(parroquia);
				}
				if(proyectoLicenciaCoa.getCategorizacion() == 2)
				{
					RegistroAmbientalRcoa registroAmbientalRcoa = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyectoLicenciaCoa);
					if(registroAmbientalRcoa != null && registroAmbientalRcoa.getFechaFinalizacion() != null){
						informacionProyecto.setNumeroResolucion(registroAmbientalRcoa.getNumeroResolucion());
						informacionProyecto.setFechaEmision(registroAmbientalRcoa.getFechaFinalizacion());
					}else{
						// verifico si se trata de un proyecto de scout drilling
						PerforacionExplorativa perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyectoLicenciaCoa.getId());
						if(perforacionExplorativa != null && perforacionExplorativa.getId() != null && perforacionExplorativa.getFinalized()){
							
						}else{
							informacionProyecto=new InformacionProyecto();
							JsfUtil.addMessageError("El proyecto no se encuentra finalizado.");
							return;
						}
					}
					documentosListenerRCOA(proyectoLicenciaCoa.getId());
				}else if(proyectoLicenciaCoa.getCategorizacion() == 3 || proyectoLicenciaCoa.getCategorizacion() == 4){
					getLicenses= new ArrayList<EntityFichaCompleta>();
					getLicenses=fichaAmbientalPmaFacade.getLicenses(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
					//getLicenses=fichaAmbientalPmaFacade.getLicenses("MAE-RA-2015-213988");
					String stringDate=getLicenses.get(0).getFechaEmision();
					String[] parts=stringDate.split(" ");
					Date fechaEmision=new SimpleDateFormat("yyyy-MM-dd").parse(parts[0]);
					informacionProyecto.setNumeroResolucion(getLicenses.get(0).getNumeroResolucion());
					informacionProyecto.setFechaEmision(fechaEmision);
				} else{
					// aumentar para licencia
					informacionProyecto=new InformacionProyecto();
					JsfUtil.addMessageError("El proyecto no se encuentra finalizado.");
					return;
				}
				ubicacionListenerRCOA();
				sectorListener();

				listaAprobacionRequisitostecnicos = aprobacionRequisitosTecnicosFacade.findARTByCodigoByModo(loginBean.getUsuario(), codigo, getEmiteSuia(), "");
				for (AprobacionRequisitosTecnicos requisitos : listaAprobacionRequisitostecnicos) {
					requisitos.setSeleccionado(true);
				}
				agregarARTProyecto();
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo encontrar el proyecto.");
			LOG.error(e.getMessage());
			return;
		}
	}
	
	public void mostrarDocumentosPorProceso(Inclusion objInclusion){
		ListarDocumentosPorProceso= new ArrayList<Documento>();
		if(objInclusion.getDocumento() != null){
			ListarDocumentosPorProceso.add(objInclusion.getDocumento());
		}
		if(objInclusion.getDocumentoEstudio() != null){
			ListarDocumentosPorProceso.add(objInclusion.getDocumentoEstudio());
		}
	}
	
	public void emiteListener()
	{
		informacionProyecto=new InformacionProyecto();
		parroquia=null;
		ubicacionListener();
		documentosListener(null);
		buscarProyectos();
		adjuntoResolucion=new Documento();
		adjuntoMapa=new Documento();
		adjuntoCoordenadas = new Documento();
	}
	
	private void ubicacionListener(){
		/*if(parroquia!=null)
		{
			canton=parroquia.getUbicacionesGeografica();
			provincia=canton.getUbicacionesGeografica();
		}else{
			canton=null;
			provincia=null;			
		}*/
		//informacionProyecto.setUbicacionesGeografica(parroquia);
		if(informacionProyecto.getCodigo() != null && !informacionProyecto.getCodigo().isEmpty()){
			if(urlContenido.contains("Rcoa")){
				proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(informacionProyecto.getCodigo());
				ubicacionListenerRCOA();
			}else{
				AutorizacionAdministrativaAmbiental autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(informacionProyecto.getCodigo());
				if(autorizacionAdministrativa != null && autorizacionAdministrativa.getId() != null){
					ubicacionDigitalizacionListener(autorizacionAdministrativa);
					documentosDigitalizacionListener(autorizacionAdministrativa.getId());
				}else{
					ProyectoLicenciamientoAmbiental proyecto=projectoFacade.getProyectoPorCodigo(informacionProyecto.getCodigo());
					if(proyecto != null){
						listaUbicacionesGeograficas = ubicacionGeograficaFacade.listarPorProyecto(proyecto);	
					}
				}
			}
		}
	}
	
	private void ubicacionDigitalizacionListener(AutorizacionAdministrativaAmbiental autorizacionAdministrativa){
		// listo las ubicaciones del proyecto original
		List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), 1, "WGS84", "17S");
		// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
		if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
			ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), 3, "WGS84", "17S");
		}
		// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
		if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
			ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 2);
		}
		listaUbicacionesGeograficas = new ArrayList<UbicacionesGeografica>();
		if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
			for (UbicacionDigitalizacion ubicacionDigitalizacion : ListaUbicacionTipo) {
				parroquia=ubicacionDigitalizacion.getUbicacionesGeografica();
				listaUbicacionesGeograficas.add(parroquia);
				if (parroquia != null ){
					areasSeguimientoList=areaFacade.getAreasSeguimiento(parroquia);
				}
			}
		}
	}
	
	private void documentosListener(Integer proyectoId){
		try {
			adjuntoMapa=null;
			adjuntoCoordenadas=null;
			adjuntoResolucion=null;
			if(proyectoId!=null){
				adjuntoMapa=documentosFacade.documentoXTablaIdXIdDocUnico(proyectoId,ProyectoLicenciamientoAmbiental.class.getSimpleName(),TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA);
				if(adjuntoMapa!=null)
					adjuntoMapa.setContenidoDocumento(documentosFacade.descargar(adjuntoMapa.getIdAlfresco()));
				
				adjuntoCoordenadas=documentosFacade.documentoXTablaIdXIdDocUnico(proyectoId,ProyectoLicenciamientoAmbiental.class.getSimpleName(), TipoDocumentoSistema.TIPO_COORDENADAS);
				if(adjuntoCoordenadas!=null)
					adjuntoCoordenadas.setContenidoDocumento(documentosFacade.descargar(adjuntoCoordenadas.getIdAlfresco()));
				adjuntoResolucion=documentosFacade.documentoXTablaIdXIdDocUnico(proyectoId,"CategoriaIILicencia", TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL);
				if(adjuntoResolucion!=null)
					adjuntoResolucion.setContenidoDocumento(documentosFacade.descargar(adjuntoResolucion.getIdAlfresco()));
			}		
		} catch (Exception e) {
			LOG.error("No se pudo descargar Documentos. InformacionBasicaController");
		}
	}
	
	private void documentosDigitalizacionListener(Integer proyectoId){
		try {
			adjuntoMapa=null;
			adjuntoCoordenadas=null;
			adjuntoResolucion=null;
			if(proyectoId!=null){
				List<DocumentoDigitalizacion> documentosMapa = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(proyectoId, AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_MAPA);
				if(documentosMapa != null && !documentosMapa.isEmpty()){
					byte[] contenido = documentoDigitalizacionFacade.descargar(documentosMapa.get(0).getIdAlfresco());
					if(contenido != null){
						adjuntoMapa = new Documento();
						adjuntoMapa.setDescripcion("Mapa");
						adjuntoMapa.setExtesion(".pdf");
						adjuntoMapa.setMime("application/pdf");
						adjuntoMapa.setNombre("Mapa");
						adjuntoMapa.setId(1);
						adjuntoMapa.setContenidoDocumento(documentosMapa.get(0).getContenidoDocumento());
					}
				}
				//Documento resolucion
				List<DocumentoDigitalizacion> documentosResolucion = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(proyectoId, AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_RESOLUCION);
				if(documentosResolucion != null && !documentosResolucion.isEmpty()){
					byte[] contenido = documentoDigitalizacionFacade.descargar(documentosResolucion.get(0).getIdAlfresco());
					if(contenido != null){
						adjuntoResolucion = new Documento();
						adjuntoResolucion.setDescripcion("Resolucion");
						adjuntoResolucion.setNombre("Resolucion");
						adjuntoResolucion.setExtesion(".pdf");
						adjuntoResolucion.setMime("application/pdf");
						adjuntoResolucion.setId(1);
						adjuntoResolucion.setContenidoDocumento(contenido);
					}
				}
				// coordenadas proyecto
				List<DocumentoDigitalizacion> documentosCoordenads = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(proyectoId, AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_DOCUMENTO_COORDENADAS);
				if(documentosCoordenads != null && !documentosCoordenads.isEmpty()){
					byte[] contenido = documentoDigitalizacionFacade.descargar(documentosCoordenads.get(0).getIdAlfresco());
					if(contenido != null){
						adjuntoCoordenadas = new Documento();
						adjuntoCoordenadas.setDescripcion("Coordenadas del proyecto");
						adjuntoCoordenadas.setNombre("Coordenadas del proyecto.xls");
						adjuntoCoordenadas.setExtesion(".xls");
						adjuntoCoordenadas.setMime("application/vnd.ms-excel");
						adjuntoCoordenadas.setId(1);
						adjuntoCoordenadas.setContenidoDocumento(contenido);
					}
				}
			}		
		} catch (Exception e) {
			LOG.error("No se pudo descargar Documentos. InformacionBasicaController");
		}
	}
	
	public void cargarCoordenadasListener()
	{
		provincia = null;
		JsfUtil.cargarObjetoSession("verUbicacionRetce",true);
	}
	
	public boolean getMostrarUpload(){
		boolean resultado=false;
		resultado = !(informacionProyecto.getFormatoCoordenadas() == null || informacionProyecto.getFormatoCoordenadas().isEmpty()) && !(informacionProyecto.getZona() == null || informacionProyecto.getZona().isEmpty());
		return resultado;
	}

	public void validarCedulaListener()
	{
		ListaContactosRetceController contactosController = JsfUtil.getBean(ListaContactosRetceController.class);
		String cedulaRuc=tecnicoResponsable.getIdentificador();
		tecnicoResponsable=contactosController.validarCedulaListener(cedulaRuc);
		if(tecnicoResponsable.getIdentificador()==null)
		{
			JsfUtil.addMessageError("Error en Cédula o Ruc no válido");
		}
	}
	
	public boolean validarCorreoListener()
	{
		if(JsfUtil.validarMail(tecnicoResponsable.getCorreo())){
			return true;
		}else{
			JsfUtil.addMessageError("Debe Ingresar un Correo Válido del Técnico Responsable");
			tecnicoResponsable.setCorreo(null);
			return false;
		}
	}
	
	public void sectorListener()
	{
		catalogoActividadesList=catalogoCategoriasFacade.listarCatalogoCategoriasPorSector(informacionProyecto.getTipoSector());
		faseRetceList=faseRetceFacade.findByTipoSector(informacionProyecto.getTipoSector());		
		if(informacionProyecto.getTipoSector().getId().intValue()>=3){
			disabledFase=true;
			informacionProyecto.setFaseRetce(null);
		}else{
			disabledFase=false;
		}
	}
	
	public void obtenerCoordenadas(){
		/*listaCoordenadas=informacionProyectoFacade.getCoordenadasPorIdInformacionProyecto(informacionProyecto.getId());
		if(listaCoordenadas != null && listaCoordenadas.size() > 0){
			cargarCoordenadasBean.setFormato(listaCoordenadas.get(0).getFormato());
			cargarCoordenadasBean.setZonaDescripcion(listaCoordenadas.get(0).getZona());
			//if(listaCoordenadas.get(0).getCoordenadas() != null && listaCoordenadas.get(0).getCoordenadas().size() > 0){
				//cargarCoordenadasBean.setZonaDescripcion(listaCoordenadas.get(0).getCoordenadas().get(0).getZona());	
			//}
			formasProyectos = listaCoordenadas.get(0);
		}*/
	}
	
	public void actividadCiiuListener()
	{
		subActividadCiiuList=new ArrayList<ActividadCiiu>();		
		if(actividadCiiu!=null){
			subActividadCiiuList=catalogoActividadCiiuFacade.findAll(actividadCiiu);
		}
	}
	
	public void fileUploadResolucion(FileUploadEvent event) {
		actualizaResolucion=true;
		fileAdjuntoResolucion = event.getFile();
		setAdjuntoResolucion(UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoResolucion.getContents(),fileAdjuntoResolucion.getFileName()));		
	}
	public void fileUploadMapa(FileUploadEvent event) {
		actualizaMapa=true;
		fileAdjuntoMapa = event.getFile();
		setAdjuntoMapa(UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoMapa.getContents(),fileAdjuntoMapa.getFileName()));		
	}
	public void fileUploadCoordenadas(FileUploadEvent event) {
		actualizaCoordenadas=true;
		fileAdjuntoCoordenadas= event.getFile();
		setAdjuntoCoordenadas(UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoCoordenadas.getContents(),fileAdjuntoCoordenadas.getFileName()));		
	}
	
	public Boolean getEmiteFisico(){
		return emiteSuia==null?false:!emiteSuia;		
	}
	
	public Date getFechaActual(){
		return new Date();
	}
	
	public void crearServicio(){
		servicio=new Servicio();
	}
	
	public void agregarServicio(){
		if(!serviciosList.contains(servicio))
			serviciosList.add(servicio);
	}
	
	public void editarServicio(Servicio item){
		servicio=item;
	}
	
	public void eliminarServicio(Servicio item){
		if(item.getId()!=null)
			serviciosEliminadosList.add(item);
		serviciosList.remove(item);
	}
	
	public void crearProducto(){
		producto=new Producto();
	}
	
	public void agregarProducto(){
		if(!productosList.contains(producto))
			productosList.add(producto);
	}
	
	public void editarProducto(Producto item){
		producto=item;
	}
	
	public void eliminarProducto(Producto item){
		if(item.getId()!=null)
			productosEliminadosList.add(item);
		productosList.remove(item);
	}

	public void eliminarDesechoProyecto(InformacionProyectoDesechosPeligrosos item){
		if(item.getId()!=null)
			listaEliminarProyectoDesechos.add(item);
		listaProyectoDesechos.remove(item);
		for(int indice = 0;indice<listaDesechosId.size();indice++)
		{
			if(item.getDesechosPeligrosos().getId().toString().equals(listaDesechosId.get(indice))){
				listaDesechosId.remove(indice);
				break;
			}
		}
	}

	public void eliminarCambioDesechoProyecto(InformacionProyectoCambioDesechosPeligrosos item){
		if(item.getId()!=null)
			listaEliminarProyectoCambioDesechos.add(item);
		listaProyectoCambioDesechos.remove(item);
		for(int indice = 0;indice<listaCambiosDesechosId.size();indice++)
		{
			if(item.getDesechosPeligrosos().getId().toString().equals(listaCambiosDesechosId.get(indice))){
				listaCambiosDesechosId.remove(indice);
				break;
			}
		}
	}

	public void eliminarARTProyecto(InformacionProyectoAprobacionRequisitosTecnicos item){
		if(item.getId()!=null)
			listaProyectoEliminarART.add(item);
		listaProyectoART.remove(item);
		for(int indice = 0;indice<listaARTId.size();indice++)
		{
			if(item.getAprobacionRequisitosTecnicos().getId().toString().equals(listaARTId.get(indice))){
				listaARTId.remove(indice);
				break;
			}
		}
	}

	private StreamedContent descargarDocumento(Documento documento){
		try {
			if(documento!=null){
				if (documento.getExtesion().contains(".pdf")){
					if(documento.getContenidoDocumento()==null)
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));	
					return UtilDocumento.getStreamedContent(documento);
				}else{
					DefaultStreamedContent content = null;
					if(documento.getContenidoDocumento() == null){
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));	
					}					
					if (documento.getNombre() != null && documento.getContenidoDocumento() != null) {
						content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
						content.setName(documento.getNombre());
						return content;
					}
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public StreamedContent getDocumentoMapa(){		
		return descargarDocumento(adjuntoMapa);
	}
	
	public StreamedContent getDocumentoCoordenadas(){		
		return descargarDocumento(adjuntoCoordenadas);
	}
	
	public StreamedContent getDocumentoAdjuntoResolucion() {
		return descargarDocumento(adjuntoResolucion);
	}


	public StreamedContent getDocumentoOficio(Documento objDocumento){		
		return descargarDocumento(objDocumento);
	}
	
	private List<FormaInformacionProyecto> getFormasCoordenadas() {
        List<FormaInformacionProyecto> result = new ArrayList<FormaInformacionProyecto>();
        /*CargarCoordenadasBean cargarCoordenadasBean = JsfUtil.getBean(CargarCoordenadasBean.class);
        Iterator<CoordinatesWrapper> coords = cargarCoordenadasBean.getCoordinatesWrappers().iterator();
        while (coords.hasNext()) {
            CoordinatesWrapper coordinatesWrapper = coords.next();
            FormaInformacionProyecto formaCoordenada = new FormaInformacionProyecto();
            formaCoordenada.setInformacionProyecto(informacionProyecto);
            formaCoordenada.setTipoForma(coordinatesWrapper.getTipoForma());
            formaCoordenada.setCoordenadas(coordinatesWrapper.getCoordenadas());
            formaCoordenada.setFormato(cargarCoordenadasBean.getFormato());
            formaCoordenada.setZona(cargarCoordenadasBean.getZonaDescripcion());
            result.add(formaCoordenada);
        }*/
        return result;
    }
	
	public void agregarInfBasica(){
		verFormulario=true;
		emiteSuia=null;
		gestionDesechos=null;
		gestor=null;
		actividadCiiu=null;
		cargarDatosIniciales();		
		
		//cargarCoordenadasBean.setCoordinatesWrappers(new ArrayList<CoordinatesWrapper>());
		//adicionarUbicacionesBean.setUbicacionesSeleccionadas(new ArrayList<UbicacionesGeografica>());
	}
	public void verTablaInfBasica(){
		cargarListaInfbasica();
		verFormulario=false;
		refresh();
	}
	
	public void refresh() {
	    FacesContext context = FacesContext.getCurrentInstance();
	    Application application = context.getApplication();
	    ViewHandler viewHandler = application.getViewHandler();
	    UIViewRoot viewRoot = viewHandler.createView(context, context.getViewRoot().getViewId());
	    context.setViewRoot(viewRoot);
	    context.renderResponse(); 
	 }
	
	public void activarDesechoFasesSeleccionadas(){
		listaFasesGestionDesechosId= new ArrayList<String>();
		for (InformacionProyectoDesechoFases objFase : listaProyectoDesechoFases) {
			listaFasesGestionDesechosId.add(objFase.getDesechoFases().getId().toString());
		}

		for (InformacionProyectoAprobacionRequisitosTecnicos objART : listaProyectoART) {
			listaARTId.add(objART.getAprobacionRequisitosTecnicos().getId().toString());
		}
		if(filtrosMapResiduosDesechos  == null && listaProyectoDesechos.size() > 0){
			filtrosMapResiduosDesechos = new HashMap<>();
		}
		for (InformacionProyectoDesechosPeligrosos objInfDesechos : listaProyectoDesechos) {
			listaDesechosId.add(objInfDesechos.getDesechosPeligrosos().getId().toString());
			List<String> listaFasesSeleccionadas;
			listaFasesSeleccionadas= new ArrayList<String>();
			if(objInfDesechos.isAlmacenamiento())	listaFasesSeleccionadas.add(faseAlmacenamiento.toString());
			if(objInfDesechos.isTransporte())	listaFasesSeleccionadas.add(faseTransporte.toString());
			if(objInfDesechos.isEliminacion())	listaFasesSeleccionadas.add(faseEliminación.toString());
			if(objInfDesechos.isDisposicion())	listaFasesSeleccionadas.add(faseDisposición.toString());
			filtrosMapResiduosDesechos.put(objInfDesechos.getDesechosPeligrosos().getId(), listaFasesSeleccionadas);
		}
		for (InformacionProyectoCambioDesechosPeligrosos objInfDesechos : listaProyectoCambioDesechos) {
			listaCambiosDesechosId.add(objInfDesechos.getDesechosPeligrosos().getId().toString());
			List<String> listaFasesSeleccionadas;
			listaFasesSeleccionadas= new ArrayList<String>();
			if(objInfDesechos.isAlmacenamiento())	listaFasesSeleccionadas.add(faseAlmacenamiento.toString());
			if(objInfDesechos.isTransporte())	listaFasesSeleccionadas.add(faseTransporte.toString());
			if(objInfDesechos.isEliminacion())	listaFasesSeleccionadas.add(faseEliminación.toString());
			if(objInfDesechos.isDisposicion())	listaFasesSeleccionadas.add(faseDisposición.toString());
			filtrosSettingsMap.put(objInfDesechos.getDesechosPeligrosos().getId(), listaFasesSeleccionadas);
		}
	}
	

	public void eliminarInfBasica(InformacionProyecto infBasica){
		Usuario userLogged=JsfUtil.getLoggedUser(); 
		infBasica.setEstado(false);
		informacionProyectoFacade.save(infBasica, userLogged);
		informacionProyectoList=informacionProyectoFacade.findByUser(JsfUtil.getLoggedUser());
	}
	
	public void editarInfBasica(InformacionProyecto infBasica){
		actualizaMapa=false;
		actualizaResolucion=false;
		actualizaInclusiones=false;
		actualizaCoordenadas=false;
		verFormulario=true;
		cargarDatosIniciales();
		informacionProyecto=infBasica;
		getUrlFormulario(informacionProyecto.isEsProyectoRcoa());
		objInformacinOriginal = informacionProyectoFacade.findById(infBasica.getId());
		buscarDatosOperador();
		gestionDesechos = informacionProyecto.isGestionDesechos();
		gestor = informacionProyecto.isGestor();
		tipoGestion = (informacionProyecto.getTipoGestion() != null) ? informacionProyecto.getTipoGestion().getId() : null;
		emiteSuia=!informacionProyecto.getEsEmisionFisica();
		buscarProyectos();
		serviciosList=servicioFacade.findByProyecto(informacionProyecto);
		productosList=productoFacade.findByProyecto(informacionProyecto);
		inclusionList=inclusionFacade.findByProyecto(informacionProyecto);
		listaProyectoDesechoFases=InformacionProyectoDesechoFasesFacade.findByProyecto(informacionProyecto);
		cargarProyectodesechos();
		cargarListasOriginales();
		tecnicoResponsable=informacionProyecto.getTecnicoResponsable();
		if(tecnicoResponsable == null){
			tecnicoResponsable=new TecnicoResponsable();
		}
		ubicacionesGeograficas = proyectoInformacionUbicacionGeograficaFacade.findByProyecto(informacionProyecto.getId());
//		parroquia=informacionProyecto.getUbicacionesGeografica();
		if (parroquia != null ){
			areasSeguimientoList=areaFacade.getAreasSeguimiento(parroquia);
		}
		ubicacionListener();
		sectorListener();
		obtenerCoordenadas();
		ActividadCiiu subAct=(informacionProyecto.getActividadCiiu() == null)? null: informacionProyecto.getActividadCiiu();
		actividadCiiu=(subAct == null)?null:subAct.getActividadCiiu();
		actividadCiiuListener();
		activarDesechoFasesSeleccionadas();
		// para saber si es nuevo o ya fue enviado 
		proyectoSinProcesar = !informacionProyecto.getInformacionEnviada() && informacionProyecto.getInformacionValidada()== null;
		// para cargar la lista de ART para ser seleccionados
		seleccionarFaseGestion();
		//informacionProyecto.setActividadCiiu(subAct);
		if(getEmiteFisico())
		{
			try {
				adjuntoResolucion=documentosFacade.documentoXTablaIdXIdDocUnico(informacionProyecto.getId(),InformacionProyecto.class.getSimpleName(),TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL);
				if(adjuntoResolucion==null)
					adjuntoResolucion=new Documento();				
					
				adjuntoMapa=documentosFacade.documentoXTablaIdXIdDocUnico(informacionProyecto.getId(),InformacionProyecto.class.getSimpleName(),TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA);
				if(adjuntoMapa==null)
					adjuntoMapa=new Documento();
					
				adjuntoCoordenadas=documentosFacade.documentoXTablaIdXIdDocUnico(informacionProyecto.getId(),InformacionProyecto.class.getSimpleName(),TipoDocumentoSistema.TIPO_COORDENADAS);
				adjuntoCoordenadas.setContenidoDocumento(documentosFacade.descargar(adjuntoCoordenadas.getIdAlfresco()));
				if(adjuntoCoordenadas==null)
					adjuntoCoordenadas=new Documento();
			} catch (Exception e) {
				LOG.error(e.getCause()+" "+e.getMessage());
			}
			
			//cargarCoordenadasBean.setCoordinatesWrappers(new ArrayList<CoordinatesWrapper>());
			//adicionarUbicacionesBean.setUbicacionesSeleccionadas(new ArrayList<UbicacionesGeografica>());
			//adicionarUbicacionesBean.setParroquia(informacionProyecto.getUbicacionesGeografica());
			ingresarInclusion();
		}else{
			if(urlContenido.contains("Rcoa")){
				proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(informacionProyecto.getCodigo());
				documentosListenerRCOA(proyectoLicenciaCoa.getId());
			}else{
				ProyectoLicenciamientoAmbiental proyecto=projectoFacade.getProyectoPorCodigo(informacionProyecto.getCodigo());
				if(proyecto != null){
					documentosListener(proyecto.getId());
				}
			}
		}
		obtenerEnteSeguimientoLista();
		if(infBasica.getInformacionEnviada() && infBasica.getInformacionValidada()==null)
			habilitarIngreso=false;
		 try{
	        Thread.sleep(500);
		 }catch(InterruptedException ex){
			 
		 }
	}
	
	public boolean mostrarEditar(InformacionProyecto infBasica){
		return (infBasica.getInformacionEnviada() && infBasica.getInformacionValidada()==null);
	}
	
	private boolean validarProyectoRegistrado(String codigo){
		String codigoBuscar=codigo!=null?codigo:informacionProyectoFacade.generarCodigoProyecto(informacionProyecto);
		
		InformacionProyecto infPro=informacionProyectoFacade.findByCodigo(codigoBuscar);
		if(infPro!=null && (informacionProyecto.getId()==null ||(informacionProyecto.getId().intValue()!=infPro.getId().intValue()))){
			JsfUtil.addMessageWarning("Ya se ha registrado un proyecto con código "+codigoBuscar);
			return false;
		}
		if(codigo != null && infPro!=null && (informacionProyecto.getId()==null ||(informacionProyecto.getId().intValue()==infPro.getId().intValue()))){
			return false;
		}
		return true;
	}
	
	public boolean validarDatos(){
		boolean validar=true;
		if(emiteSuia && informacionProyecto.getCodigo()==null){
			JsfUtil.addMessageError("Seleccione un proyecto");
			validar= false;
		}
		if(!emiteSuia && adjuntoResolucion==null){
			JsfUtil.addMessageError("Adjunte Resolución Ambiental");
			validar= false;
		}
		/*if(productosList.isEmpty() && serviciosList.isEmpty()){
			JsfUtil.addMessageError("Debe agregar al menos 1 Producto o Servicio");
			validar= false;
		}*/
		/*if(tecnicoResponsable.getNombre().isEmpty()){
			JsfUtil.addMessageError("Debe Ingresar la Información del Técnico Responsable");
			validar= false;
		}

		if(!validarCorreoListener())
			validar=false;
		*/
		if(getEmiteFisico()){
			if(adjuntoResolucion.getId()==null && adjuntoResolucion.getContenidoDocumento()==null){
				JsfUtil.addMessageError("Debe adjuntar Resolución Ambiental");
				validar= false;
			}
			/*if(adjuntoMapa.getId()==null && adjuntoMapa.getContenidoDocumento()==null){
				JsfUtil.addMessageError("Debe adjuntar Mapa de Intersección");
				validar= false;
			}*/
			if(ubicacionesGeograficas ==null || ubicacionesGeograficas.size() == 0 ){
				JsfUtil.addMessageError("Debe seleccionar la ubicación");
				validar= false;
			}
		}
		return validar;
	}
	
	public void guardar(){
		boolean validar=validarDatos();
		informacionProyecto.setEsEmisionFisica(!emiteSuia);
		if(!validarProyectoRegistrado(null))
			return;
		try {
			Usuario userLogged=JsfUtil.getLoggedUser(); 
			/*tecnicoResponsableFacade.save(tecnicoResponsable, userLogged);
			informacionProyecto.setTecnicoResponsable(tecnicoResponsable);*/
			
			//informacionProyecto.setEsEmisionFisica(!emiteSuia);
			gestionDesechos = false;
			informacionProyecto.setGestionDesechos(gestionDesechos);
			if(tipoGestion != null){
				DetalleCatalogoGeneral objTipoGestion = detalleCatalogoGeneralFacade.findById(tipoGestion);
				if(objTipoGestion != null)
					informacionProyecto.setTipoGestion(objTipoGestion);
			}
			if(!gestionDesechos){
				informacionProyecto.setTipoGestion(null);
				informacionProyecto.setGestionCambioDesechos(false);
			}
			//informacionProyecto.setActividadCiiu(new ActividadCiiu(actividadCiuuSeleccionada));
			informacionProyectoFacade.save(informacionProyecto, userLogged);
			/*if(getEmiteFisico()){
				parroquia = informacionProyecto.getUbicacionesGeografica();
			}*/
			for (Servicio servicio : serviciosList) {
				servicio.setInformacionProyecto(informacionProyecto);
				servicioFacade.save(servicio, userLogged);
			}
			for (Servicio servicio : serviciosEliminadosList) {
				servicio.setEstado(false);
				servicioFacade.save(servicio, userLogged);
			}
			for (Producto producto : productosList) {
				producto.setInformacionProyecto(informacionProyecto);
				productoFacade.save(producto, userLogged);
				guardarHistorialProductos(producto, userLogged);
			}
			for (Producto producto : productosEliminadosList) {
				producto.setEstado(false);
				productoFacade.save(producto, userLogged);
			}
			// si realiza gestion de desechos guardo los datos
			if(informacionProyecto.isGestionDesechos()){
				// guardar los ART seleccionadas del proyecto
				if(informacionProyecto.getTipoGestion() != null && informacionProyecto.getTipoGestion().getId().equals(tipoGestionResiduosPeligrosos)){
					for (InformacionProyectoAprobacionRequisitosTecnicos objART : listaProyectoART) {
						objART.setInformacionProyecto(informacionProyecto);
						informacionProyectoAprobacionRequisitosTecnicosFacade.save(objART, userLogged);
					}
				}
				// desabilitos los registros de ART si escogio gestion de residuos especiales
				if(informacionProyecto.getTipoGestion() != null && informacionProyecto.getTipoGestion().getId().equals(tipoGestionResiduosEspeciales) && listaProyectoART.size() > 0 ){
					for (InformacionProyectoAprobacionRequisitosTecnicos objART : listaProyectoART) {
						objART.setEstado(false);
						informacionProyectoAprobacionRequisitosTecnicosFacade.save(objART, userLogged);
					}
				}
				// guardar los desechos peligrosos seleccionadas del proyecto
				if(( informacionProyecto.getTipoGestion() != null && informacionProyecto.getTipoGestion().getId().equals(tipoGestionResiduosPeligrosos) && listaFasesGestionDesechosId.toString().contains(faseAlmacenamiento.toString()) )
					|| ( informacionProyecto.getTipoGestion() != null && informacionProyecto.getTipoGestion().getId().equals(tipoGestionResiduosEspeciales)  )	
						){
					if( informacionProyecto.getTipoGestion() != null && informacionProyecto.getTipoGestion().getId().equals(tipoGestionResiduosEspeciales)  ){
						listaFasesGestionDesechosId= new ArrayList<String>();
					}
					if( informacionProyecto.getTipoGestion() != null && informacionProyecto.getTipoGestion().getId().equals(tipoGestionResiduosPeligrosos) && listaFasesGestionDesechosId.toString().contains(faseAlmacenamiento.toString()) ){
						filtrosMapResiduosDesechos = new HashMap<>();
					}
					for (InformacionProyectoDesechosPeligrosos objDesecho : listaProyectoDesechos) {
						objDesecho.setInformacionProyecto(informacionProyecto);
						objDesecho.setAlmacenamiento(true);
						objDesecho.setTransporte(false);
						objDesecho.setEliminacion(false);
						objDesecho.setDisposicion(false);
						// verifico las fases seleccionadas
						for (Map.Entry<Integer, List<String>> entry : filtrosMapResiduosDesechos.entrySet()) {
							List<String>  datos = entry.getValue();
						   if ( objDesecho.getDesechosPeligrosos().getId().equals(entry.getKey())){
							   objDesecho.setAlmacenamiento(false);
							   if(datos.contains(faseAlmacenamiento.toString()) ){
								   objDesecho.setAlmacenamiento(true);
								   listaFasesGestionDesechosId.add(faseAlmacenamiento.toString());
							   }
							   if(datos.contains(faseTransporte.toString()) ){
								   objDesecho.setTransporte(true);
								   listaFasesGestionDesechosId.add(faseTransporte.toString());
							   }
							   if(datos.contains(faseEliminación.toString()) ){
								   objDesecho.setEliminacion(true);
								   listaFasesGestionDesechosId.add(faseEliminación.toString());
							   }
							   if(datos.contains(faseDisposición.toString()) ){
								   objDesecho.setDisposicion(true);
								   listaFasesGestionDesechosId.add(faseDisposición.toString());
							   }
							   break;
						    }
						}
						informacionProyectoDesechosPeligrososFacade.save(objDesecho, userLogged);
						// guardo historico si hay cambios
						if(objDesecho.getId() != null){
							guardarHistorialResiduosDesechos(objDesecho, userLogged);
						}
					}	
				}
				// desabilitos los registros de desechos peligrosos si escogio 
				if( informacionProyecto.getTipoGestion() != null && informacionProyecto.getTipoGestion().getId().equals(tipoGestionResiduosPeligrosos) && !listaFasesGestionDesechosId.toString().contains(faseAlmacenamiento.toString()) 	
							){
						for (InformacionProyectoDesechosPeligrosos objDesecho : listaProyectoDesechos) {
							objDesecho.setEstado(false);
							objDesecho.setAlmacenamiento(true);
							informacionProyectoDesechosPeligrososFacade.save(objDesecho, userLogged);
						}	
				}
				// guardar los desechos peligrosos que cambiaron de peligrosos a no peligrosos seleccionadas del proyecto
				if(informacionProyecto.isGestionCambioDesechos()){
					for (InformacionProyectoCambioDesechosPeligrosos objDesecho : listaProyectoCambioDesechos) {
						Documento objDocumento = new Documento();
						objDocumento = objDesecho.getDocumento();
						if(objDocumento != null && objDocumento.getContenidoDocumento() != null){
							objDesecho.setDocumento(null);
						}
						objDesecho.setInformacionProyecto(informacionProyecto);
						// verifico las fases seleccionadas
						for (Map.Entry<Integer, List<String>> entry : filtrosSettingsMap.entrySet()) {
							List<String>  datos = entry.getValue();
						   if ( objDesecho.getDesechosPeligrosos().getId().equals(entry.getKey())){
							   objDesecho.setAlmacenamiento(false);
							   objDesecho.setTransporte(false);
							   objDesecho.setEliminacion(false);
							   objDesecho.setDisposicion(false);
							   if(datos.contains(faseAlmacenamiento.toString())){
								   objDesecho.setAlmacenamiento(true);
							   }
							   if(datos.contains(faseTransporte.toString())){
								   objDesecho.setTransporte(true);
							   }
							   if(datos.contains(faseEliminación.toString())){
								   objDesecho.setEliminacion(true);
							   }
							   if(datos.contains(faseDisposición.toString())){
								   objDesecho.setDisposicion(true);
							   }
							   break;
						    }
						}
						if(objDocumento != null && objDocumento.getContenidoDocumento() != null){
							// adjunto el documento del oficio
							objDocumento = guardarOficio(objDocumento);
							objDesecho.setDocumento(objDocumento);
						}
						if(objDesecho.getDocumento() != null ){
							informacionProyectoCambioDesechosPeligrososFacade.save(objDesecho, userLogged);
						}
						// guardo historico si hay cambios
						if(objDesecho.getId() != null){
							guardarHistorialDesechosCambios(objDesecho, userLogged);
						}
					}
				}
				// guardar las fases de desechos seleccionadas del proyecto
				if(!listaFasesGestionDesechosId.toString().equals("0,")){
					InformacionProyectoDesechoFasesFacade.guardarDesechosFases(informacionProyecto, listaFasesGestionDesechosId, userLogged);
				}
				// eliminacion registros ART
				for (InformacionProyectoAprobacionRequisitosTecnicos objART : listaProyectoEliminarART) {
					objART.setEstado(false);
					informacionProyectoAprobacionRequisitosTecnicosFacade.save(objART, userLogged);
				}
				// eliminacion registros de desechos peligrosos y especiales
				for (InformacionProyectoDesechosPeligrosos objDesecho : listaEliminarProyectoDesechos) {
					objDesecho.setEstado(false);
					informacionProyectoDesechosPeligrososFacade.save(objDesecho, userLogged);
				}
				// eliminacion registros de desechos peligrosos que cambiaron a no peligrosso o especiales cuando selecciono que no tiene desechos que hayan cambiado
				for (InformacionProyectoCambioDesechosPeligrosos objDesecho : listaEliminarProyectoCambioDesechos) {
					objDesecho.setEstado(false);
					informacionProyectoCambioDesechosPeligrososFacade.save(objDesecho, userLogged);
				}
				// desabilitos los registros de desechos que cambiaron de peligrosos a no peñigrosos y escogio que no realiza gestionsi escogio 
				if((!informacionProyecto.isGestionCambioDesechos() || !informacionProyecto.isGestionCambioDesechos()) && listaProyectoCambioDesechos.size() > 0){
					for (InformacionProyectoCambioDesechosPeligrosos objDesecho : listaProyectoCambioDesechos) {
						objDesecho.setEstado(false);
						informacionProyectoCambioDesechosPeligrososFacade.save(objDesecho, userLogged);
					}
				}
			}else{
				// elimino las fases de desechos seleccionadas del proyecto
				if(!listaFasesGestionDesechosId.toString().equals("0,")){
					List<String> listaFasesEliminarId = new ArrayList<String>();
					listaFasesEliminarId.add("0");
					InformacionProyectoDesechoFasesFacade.guardarDesechosFases(informacionProyecto, listaFasesEliminarId, userLogged);
				}
				// eliminacion registros ART
				for (InformacionProyectoAprobacionRequisitosTecnicos objART : listaProyectoART) {
					if(objART.getId() != null){
						objART.setEstado(false);
						informacionProyectoAprobacionRequisitosTecnicosFacade.save(objART, userLogged);
					}
				}
				// eliminacion registros de desechos peligrosos y especiales
				for (InformacionProyectoDesechosPeligrosos objDesecho : listaProyectoDesechos) {
					if(objDesecho.getId() != null){
						objDesecho.setEstado(false);
						informacionProyectoDesechosPeligrososFacade.save(objDesecho, userLogged);	
					}
				}
				// eliminacion registros de desechos peligrosos que cambiaron a no peligrosso o especiales cuando selecciono que no tiene desechos que hayan cambiado
				for (InformacionProyectoCambioDesechosPeligrosos objDesecho : listaProyectoCambioDesechos) {
					if(objDesecho.getId() != null){
						objDesecho.setEstado(false);
						informacionProyectoCambioDesechosPeligrososFacade.save(objDesecho, userLogged);
					}
				}
				listaFasesGestionDesechosId= new ArrayList<String>();
				listaProyectoDesechos= new ArrayList<InformacionProyectoDesechosPeligrosos>();
				listaProyectoART= new ArrayList<InformacionProyectoAprobacionRequisitosTecnicos>();
				listaProyectoCambioDesechos= new ArrayList<InformacionProyectoCambioDesechosPeligrosos>();
			}
			// guardo el historial si hay cambios
			//guardarHistorialInformacionBasica();
			if(getEmiteFisico())
			{
				if(actualizaResolucion && adjuntoResolucion.getContenidoDocumento()!=null){
					adjuntoResolucion.setIdTable(informacionProyecto.getId());
					adjuntoResolucion.setNombreTabla(InformacionProyecto.class.getSimpleName());
					adjuntoResolucion.setDescripcion("Resolucion Ambiental Física");
					adjuntoResolucion.setEstado(true);
					documentosFacade.guardarDocumentoAlfrescoSinProyecto(informacionProyecto.getCodigoRetce(), "RESOLUCION_AMBIENTAL_FISICA", informacionProyecto.getId().longValue(), adjuntoResolucion, TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL, null);
					actualizaResolucion=false;
				}
				
				if(actualizaMapa && adjuntoMapa.getContenidoDocumento()!=null){
					adjuntoMapa.setIdTable(informacionProyecto.getId());
					adjuntoMapa.setNombreTabla(InformacionProyecto.class.getSimpleName());
					adjuntoMapa.setDescripcion("Mapa");
					adjuntoMapa.setEstado(true);
					documentosFacade.guardarDocumentoAlfrescoSinProyecto(informacionProyecto.getCodigoRetce(), Constantes.CARPETA_CARTIFICADO_INTERSECCION_ALFRESCO, informacionProyecto.getId().longValue(), adjuntoMapa, TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA, null);
					actualizaMapa=false;
				}
				
				/*if(cargarCoordenadasBean.getUploadedFile()!=null){
					setAdjuntoCoordenadas(UtilDocumento.generateDocumentXLSFromUpload(cargarCoordenadasBean.getUploadedFile().getContents(), cargarCoordenadasBean.getUploadedFile().getFileName()));
					adjuntoCoordenadas.setIdTable(informacionProyecto.getId());
					adjuntoCoordenadas.setNombreTabla(InformacionProyecto.class.getSimpleName());
					adjuntoCoordenadas.setDescripcion("Coordenadas");
					adjuntoCoordenadas.setEstado(true);				
					documentosFacade.guardarDocumentoAlfrescoSinProyecto(informacionProyecto.getCodigoRetce(), Constantes.CARPETA_COORDENADAS, informacionProyecto.getId().longValue(), adjuntoCoordenadas, TipoDocumentoSistema.TIPO_COORDENADAS, null);

					List<FormaInformacionProyecto> formasCoordenadas=getFormasCoordenadas();
					if(formasCoordenadas.size()>0){
						informacionProyectoFacade.guardarFormasCoordenadas(formasCoordenadas);
					}
								
					listaCoordenadas=informacionProyectoFacade.getCoordenadasPorIdInformacionProyecto(informacionProyecto.getId());
					if(listaCoordenadas==null)
					{
						JsfUtil.addMessageError("Ingrese las coordenadas");
						return;
					}
				}*/
			}
			// guardo las actualizaciones (inclusiones)
			guardarInclusion(userLogged);
			actualizaInclusiones=false;
			for (Inclusion inclusion : inclusionEliminadosList) {
				inclusion.setEstado(false);
				inclusionFacade.save(inclusion, userLogged);
			}
			// guardo las ubicaciones geograficas
			guardarUbicacion(userLogged);
			if(validar){
				JsfUtil.addMessageInfo("Información guardada con éxito");				
			}
			cargarListasOriginales();
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			LOG.error(e.getCause()+" "+e.getMessage());
		}
	}
	
	public void guardarHistorialResiduosDesechos(InformacionProyectoDesechosPeligrosos objDesechoCambio, Usuario userLogged){
		try{
			for (InformacionProyectoDesechosPeligrosos desechoOriginal : listaProyectoDesechosOriginal) {
				if(objDesechoCambio.getId().equals(desechoOriginal.getId())){
					if( objDesechoCambio.isTransporte() != desechoOriginal.isTransporte()  
							|| objDesechoCambio.isAlmacenamiento() != desechoOriginal.isAlmacenamiento()  
							|| objDesechoCambio.isEliminacion() != desechoOriginal.isEliminacion()  
							|| objDesechoCambio.isDisposicion() != desechoOriginal.isDisposicion()  ){

						desechoOriginal.setId(null);
						desechoOriginal.setEstado(false);
						desechoOriginal.setHistorico(true);
						desechoOriginal.setIdPropietario(objDesechoCambio.getId());
						informacionProyectoDesechosPeligrososFacade.save(desechoOriginal, userLogged);
					}
					break;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void guardarHistorialDesechosCambios(InformacionProyectoCambioDesechosPeligrosos objDesechoCambio, Usuario userLogged){
		try{
			for (InformacionProyectoCambioDesechosPeligrosos desechoOriginal : listaProyectoCambioDesechosOriginal) {
				if(objDesechoCambio.getId().equals(desechoOriginal.getId())){
					if( !objDesechoCambio.getNumeroOficio().equals(desechoOriginal.getNumeroOficio()) 
							|| !objDesechoCambio.getFechaOficio().equals(desechoOriginal.getFechaOficio()) 
							|| !objDesechoCambio.getDocumento().getId().equals(desechoOriginal.getDocumento().getId()) 
							|| objDesechoCambio.isTransporte() != desechoOriginal.isTransporte()  
							|| objDesechoCambio.isAlmacenamiento() != desechoOriginal.isAlmacenamiento()  
							|| objDesechoCambio.isEliminacion() != desechoOriginal.isEliminacion()  
							|| objDesechoCambio.isDisposicion() != desechoOriginal.isDisposicion()  ){

						desechoOriginal.setId(null);
						desechoOriginal.setEstado(false);
						desechoOriginal.setHistorico(true);
						desechoOriginal.setIdPropietario(objDesechoCambio.getId());
						informacionProyectoCambioDesechosPeligrososFacade.save(desechoOriginal, userLogged);
					}
					break;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void guardarHistorialProductos(Producto objProducto, Usuario userLogged){
		boolean hayCambios = false;
		try{
			for (Producto productoOriginal : productosListOriginal) {
				hayCambios = false;
				if(objProducto.getId().equals(productoOriginal.getId())){
					if(productoOriginal.getOtros() != null && objProducto.getOtros() != null){
						if (!productoOriginal.getOtros().equals(objProducto.getOtros())){
							hayCambios = true;
						}
					}
					if( productoOriginal.getOtros() != null && !(objProducto.getOtros() != null) ){
						hayCambios = true;
					}
					if(hayCambios && !objProducto.getNombreComercial().equals(productoOriginal.getNombreComercial()) 
							|| !objProducto.getCantidad().equals(productoOriginal.getCantidad()) 
							|| !objProducto.getCatalogoTipoContenedor().getId().equals(productoOriginal.getCatalogoTipoContenedor().getId()) 
							|| !objProducto.getCatalogoProductos().getId().equals(productoOriginal.getCatalogoProductos().getId())  
							|| !objProducto.getCatalogoEstadoFisico().getId().equals(productoOriginal.getCatalogoEstadoFisico().getId())
							|| !objProducto.getCatalogoUnidades().getId().equals(productoOriginal.getCatalogoUnidades().getId())){

						productoOriginal.setId(null);
						productoOriginal.setEstado(false);
						productoOriginal.setHistorico(true);
						productoOriginal.setIdPropietario(objProducto.getId());
						productoFacade.save(productoOriginal, userLogged);
					}
					break;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void guardarHistorialInformacionBasica(){
		try{
			boolean guardarHistorico = false;
			Usuario usuario=JsfUtil.getLoggedUser();
			if(objInformacinOriginal.getNombreProyecto() != null){
				if( !informacionProyecto.getNombreProyecto().equals(objInformacinOriginal.getNombreProyecto()) 
						|| !informacionProyecto.getNombreProceso().equals(objInformacinOriginal.getNombreProceso()) 
						|| !informacionProyecto.getNumeroResolucion().equals(objInformacinOriginal.getNumeroResolucion())
						|| !informacionProyecto.getCodigoRetce().equals(objInformacinOriginal.getCodigoRetce())
						//|| !informacionProyecto.getActividadCiiu().getId().equals(objInformacinOriginal.getActividadCiiu().getId())
						|| !informacionProyecto.getTipoSector().getId().equals(objInformacinOriginal.getTipoSector().getId())
						//|| !informacionProyecto.getTecnicoResponsable().getId().equals(objInformacinOriginal.getTecnicoResponsable().getId())
						|| !informacionProyecto.getAreaResponsable().getId().equals(objInformacinOriginal.getAreaResponsable().getId())
						|| !informacionProyecto.getAreaSeguimiento().getId().equals(objInformacinOriginal.getAreaSeguimiento().getId())
						|| informacionProyecto.getEsEmisionFisica() != objInformacinOriginal.getEsEmisionFisica()
						|| informacionProyecto.isGestionDesechos() != objInformacinOriginal.isGestionDesechos()
						//|| informacionProyecto.isGestionCambioDesechos() != objInformacinOriginal.isGestionCambioDesechos()
						){
					guardarHistorico = true;
				}
				//codigo
				if(!guardarHistorico && informacionProyecto.getCodigo() != null && objInformacinOriginal.getCodigo() != null){
					if (!informacionProyecto.getCodigo().equals(objInformacinOriginal.getCodigo())){
						guardarHistorico = true;
					}
				}
				if( !guardarHistorico &&  informacionProyecto.getCodigo() != null && !(objInformacinOriginal.getCodigo() != null) ){
					guardarHistorico = true;
				}
				//certificado interseccion
				if(!guardarHistorico && informacionProyecto.getCertificadoInterseccion() != null && objInformacinOriginal.getCertificadoInterseccion() != null){
					if (!informacionProyecto.getCertificadoInterseccion().equals(objInformacinOriginal.getCertificadoInterseccion())){
						guardarHistorico = true;
					}
				}
				if( !guardarHistorico &&  informacionProyecto.getCertificadoInterseccion() != null && !(objInformacinOriginal.getCertificadoInterseccion() != null) ){
					guardarHistorico = true;
				}
				//fase retce
				if(!guardarHistorico && informacionProyecto.getFaseRetce() != null && objInformacinOriginal.getFaseRetce() != null){
					if (!informacionProyecto.getFaseRetce().equals(objInformacinOriginal.getFaseRetce())){
						guardarHistorico = true;
					}
				}
				if( !guardarHistorico && (informacionProyecto.getFaseRetce() != null && !(objInformacinOriginal.getFaseRetce() != null) || objInformacinOriginal.getFaseRetce() != null && !(informacionProyecto.getFaseRetce() != null) )){
					guardarHistorico = true;
				}
				//tipo gestion
				if(!guardarHistorico && informacionProyecto.getTipoGestion() != null && objInformacinOriginal.getTipoGestion() != null){
					if (!informacionProyecto.getTipoGestion().getId().equals(objInformacinOriginal.getTipoGestion().getId())){
						guardarHistorico = true;
					}
				}
				if( !guardarHistorico && (informacionProyecto.getTipoGestion() != null && !(objInformacinOriginal.getTipoGestion() != null) || objInformacinOriginal.getTipoGestion() != null && !(informacionProyecto.getTipoGestion() != null) )){
					guardarHistorico = true;
				}
				//area seguimiento
				if(!guardarHistorico && informacionProyecto.getAreaSeguimiento() != null && objInformacinOriginal.getAreaSeguimiento() != null){
					if (!informacionProyecto.getAreaSeguimiento().getId().equals(objInformacinOriginal.getAreaSeguimiento().getId())){
						guardarHistorico = true;
					}
				}
				if( !guardarHistorico && (informacionProyecto.getAreaSeguimiento() != null && !(objInformacinOriginal.getAreaSeguimiento() != null) || objInformacinOriginal.getAreaSeguimiento() != null && !(informacionProyecto.getAreaSeguimiento() != null) )){
					guardarHistorico = true;
				}
				//actividda CIIU
				if(!guardarHistorico && informacionProyecto.getActividadCiiu() != null && objInformacinOriginal.getActividadCiiu() != null){
					if (!informacionProyecto.getActividadCiiu().getId().equals(objInformacinOriginal.getActividadCiiu().getId())){
						guardarHistorico = true;
					}
				}
				if( !guardarHistorico && (informacionProyecto.getActividadCiiu() != null && !(objInformacinOriginal.getActividadCiiu() != null) || objInformacinOriginal.getActividadCiiu() != null && !(informacionProyecto.getActividadCiiu() != null) )){
					guardarHistorico = true;
				}
				//tecnico responsable
				if(!guardarHistorico && informacionProyecto.getTecnicoResponsable() != null && objInformacinOriginal.getTecnicoResponsable() != null){
					if (!informacionProyecto.getTecnicoResponsable().getId().equals(objInformacinOriginal.getTecnicoResponsable().getId())){
						guardarHistorico = true;
					}
				}
				if( !guardarHistorico && (informacionProyecto.getTecnicoResponsable() != null && !(objInformacinOriginal.getTecnicoResponsable() != null) || objInformacinOriginal.getTecnicoResponsable() != null && !(informacionProyecto.getTecnicoResponsable() != null) )){
					guardarHistorico = true;
				}
				//fecha de inicio de operaciones
				if(!guardarHistorico && informacionProyecto.getFechaInicioOperaciones() != null && objInformacinOriginal.getFechaInicioOperaciones() != null){
					if (!informacionProyecto.getFechaInicioOperaciones().equals(objInformacinOriginal.getFechaInicioOperaciones())){
						guardarHistorico = true;
					}
				}
				if( !guardarHistorico &&  informacionProyecto.getFechaInicioOperaciones() != null && !(objInformacinOriginal.getFechaInicioOperaciones() != null) ){
					guardarHistorico = true;
				}
				//fecha de emision
				if(!guardarHistorico && informacionProyecto.getFechaEmision() != null && objInformacinOriginal.getFechaEmision() != null){
					if (!informacionProyecto.getFechaEmision().equals(objInformacinOriginal.getFechaEmision())){
						guardarHistorico = true;
					}
				}
				if( !guardarHistorico &&  informacionProyecto.getFechaEmision() != null && !(objInformacinOriginal.getFechaEmision() != null) ){
					guardarHistorico = true;
				}
				//fecha de certificado interseccion
				if(!guardarHistorico && informacionProyecto.getFechaCertificadoInterseccion() != null && objInformacinOriginal.getFechaCertificadoInterseccion() != null){
					if (!informacionProyecto.getFechaCertificadoInterseccion().equals(objInformacinOriginal.getFechaCertificadoInterseccion())){
						guardarHistorico = true;
					}
				}
				if( !guardarHistorico &&  informacionProyecto.getFechaCertificadoInterseccion() != null && !(objInformacinOriginal.getFechaCertificadoInterseccion() != null) ){
					guardarHistorico = true;
				}
				if(guardarHistorico){
					objInformacinOriginal.setId(null);
					objInformacinOriginal.setEstado(false);
					objInformacinOriginal.setHistorico(true);
					objInformacinOriginal.setIdPropietario(informacionProyecto.getId());
					informacionProyectoFacade.save(objInformacinOriginal, usuario);
					objInformacinOriginal = informacionProyectoFacade.findById(informacionProyecto.getId());
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void  obtenerEnteSeguimiento(){
		if(getEmiteFisico() && adicionarUbicacionesBean.getParroquia()!=null){
			areasSeguimientoList = new ArrayList<Area>();
			parroquia = adicionarUbicacionesBean.getParroquia();
			areasSeguimientoList=areaFacade.getAreasSeguimiento(parroquia);
		}
	}
	
	public List<Area> obtenerEnteSeguimientoLista(){
		if(getEmiteFisico() && adicionarUbicacionesBean.getParroquia()!=null){
			areasSeguimientoList = new ArrayList<Area>();
			parroquia = adicionarUbicacionesBean.getParroquia();
			areasSeguimientoList=areaFacade.getAreasSeguimiento(parroquia);
		}else{
			if(informacionProyecto.getAreaSeguimiento() != null)
				areasSeguimientoList.add(informacionProyecto.getAreaSeguimiento());
		}
		return areasSeguimientoList;
	}
	
	public List<Area> obtenerEnteSeguimientoListaUno(){
		if(getEmiteFisico() && ubicacionesGeograficas != null && ubicacionesGeograficas.size() > 0){
			areasSeguimientoList = new ArrayList<Area>();
			areasSeguimientoList=areaFacade.getAreasSeguimientoPorParroquias(ubicacionesGeograficas);
		}
		return areasSeguimientoList;
	}

	public List<Area> obtenerEnteSeguimientoListaUnoBackup(){
		/*if(getEmiteFisico() && informacionProyecto.getUbicacionesGeografica() !=null){
			areasSeguimientoList = new ArrayList<Area>();
			parroquia = informacionProyecto.getUbicacionesGeografica();
			areasSeguimientoList=areaFacade.getAreasSeguimiento(parroquia);
		}*/
		return areasSeguimientoList;
	}
	
	private void guardarInclusion(Usuario userLogged){
		if(actualizaInclusiones){
			for (Inclusion inclusion : inclusionList) {
				inclusion.setInformacionProyecto(informacionProyecto);
				inclusionFacade.save(inclusion, userLogged);

				if(inclusion.getDocumento()!=null && inclusion.getDocumento().getContenidoDocumento()!=null){
					inclusion.getDocumento().setIdTable(inclusion.getId());
					inclusion.getDocumento().setNombreTabla(Inclusion.class.getSimpleName());
					inclusion.getDocumento().setDescripcion("Resolución Ambiental inclusión");
					inclusion.getDocumento().setEstado(true);				
					try {
						documentosFacade.guardarDocumentoAlfrescoSinProyecto(informacionProyecto.getCodigoRetce(), Constantes.CARPETA_RETCE_INCLUSION, inclusion.getId().longValue(), inclusion.getDocumento(), TipoDocumentoSistema.RETCE_RESOLUCION_INCLUSION, null);
					} catch (ServiceException | CmisAlfrescoException e) {
						e.printStackTrace();
					}
				}
				// guardo el documento del estudio complementario
				if(inclusion.getDocumentoEstudio()!=null && inclusion.getDocumentoEstudio().getContenidoDocumento()!=null){
					inclusion.getDocumentoEstudio().setIdTable(inclusion.getId());
					inclusion.getDocumentoEstudio().setNombreTabla(Inclusion.class.getSimpleName());
					inclusion.getDocumentoEstudio().setDescripcion("Estudio Complementario");
					inclusion.getDocumentoEstudio().setEstado(true);				
					try {
						documentosFacade.guardarDocumentoAlfrescoSinProyecto(informacionProyecto.getCodigoRetce(), Constantes.CARPETA_RETCE_INCLUSION, inclusion.getId().longValue(), inclusion.getDocumentoEstudio(), TipoDocumentoSistema.RETCE_ESTUDIO_COMPLEMENTARIO, null);
					} catch (ServiceException | CmisAlfrescoException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void guardarUbicacion(Usuario userLogged){
		for (ProyectoInformacionUbicacionGeografica ubicacion : ubicacionesGeograficas) {
			ubicacion.setIdProyecto(informacionProyecto.getId());
			ubicacion.setEstado(true);
			proyectoInformacionUbicacionGeograficaFacade.save(ubicacion, userLogged);
		}
		// deshabilito los registro eliminados
		for (ProyectoInformacionUbicacionGeografica objUbicacion : ubicacionesGeograficasEliminar) {
			objUbicacion.setEstado(false);
			proyectoInformacionUbicacionGeograficaFacade.save(objUbicacion, userLogged);
		}
	}
	
	private Documento guardarOficio(Documento objDocumento ) throws ServiceException, CmisAlfrescoException{
		objDocumento.setIdTable(informacionProyecto.getId());
		objDocumento.setNombreTabla(InformacionProyecto.class.getSimpleName());
		objDocumento.setDescripcion(objDocumento.getNombre());
		objDocumento.setEstado(true);				
		objDocumento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(informacionProyecto.getCodigoRetce(), "OFICIO_DECLARACION_DESECHO", informacionProyecto.getId().longValue(), objDocumento, TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL, null);
		return objDocumento;
	}

	public void agregarARTProyecto(){
		for (AprobacionRequisitosTecnicos requisitos : listaAprobacionRequisitostecnicos) {
			if (requisitos.isSeleccionado() && !listaARTId.toString().contains(", "+requisitos.getId().toString())){
				InformacionProyectoAprobacionRequisitosTecnicos objProyectoART = new InformacionProyectoAprobacionRequisitosTecnicos();
				objProyectoART.setAprobacionRequisitosTecnicos(requisitos);
				objProyectoART.setInformacionProyecto(informacionProyecto);
				listaProyectoART.add(objProyectoART);
				listaARTId.add(requisitos.getId().toString());
			}
		}
	}
	
	public void obtenerListaProyectoART(){
		if(emiteSuia == null){
			JsfUtil.addMessageError("Debe indicar el tipo de emisión del permiso ambiental.");
			return;
		}
		if(emiteSuia && informacionProyecto.getCodigo() == null){
			JsfUtil.addMessageError("Debe ingresar el código del proyecto.");
			return;
		}
		if(!emiteSuia && informacionProyecto.getNumeroResolucion() == null){
			JsfUtil.addMessageError("Debe ingresar el número de la resolución del proyecto.");
			return;
		}
		String codigo = emiteSuia ? informacionProyecto.getCodigo() : informacionProyecto.getNumeroResolucion();
		if(listaAprobacionRequisitostecnicos.size() == 0 ){
			listaAprobacionRequisitostecnicos = aprobacionRequisitosTecnicosFacade.findARTByCodigoByModo(loginBean.getUsuario(), codigo, getEmiteSuia(), modalidad);
		}
		if(listaAprobacionRequisitostecnicos.size() > 0 ){
			//RequestContext.getCurrentInstance().execute("PF('proyectoARTDiag').show();");
		}else{
			JsfUtil.addMessageInfo("No se encontraron registros de Aprobación de Requisitos Técnicos para este proyecto.");
		}
	}
	

	private boolean requiereRevisionProyecto(){
		
		//Si el proyecto es emitido fisico
		if(informacionProyecto.getEsEmisionFisica() &&(informacionProyecto.getInformacionValidada()==null || informacionProyecto.getInformacionValidada()==false))
			return true;
		
		//Si el proyecto tiene actualizaciones
		for (Inclusion inc : inclusionList) {
			if(inc.getInformacionValidada()==null ||inc.getInformacionValidada()==false){
				return true;
				
			}
		}
		//Si el proyecto tiene cambios en la lista de desechos peligrosos que cambiaron a no peligrosos o especiales
		for (InformacionProyectoCambioDesechosPeligrosos desechos : listaProyectoCambioDesechos) {
			if(desechos.getInformacionValidada()==null || desechos.getInformacionValidada()==false){
				return true;
			}
		}
		//Si el proyecto tiene cambios en la lista de desechos peligrosos 
		for (InformacionProyectoDesechosPeligrosos desechos : listaProyectoDesechos) {
			if(desechos.getInformacionValidada()==null || desechos.getInformacionValidada()==false){
				return true;
			}
		}
		return false;
	}
	
	public String getClausulaOperador(){
		if(clausulaOperador==null){
			try {						
				clausulaOperador = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("mensajeClausulaOperadorRegistroProyectoRetce", new Object[]{nombreUsuario, usuarioOperador.getNombre()});			
			} catch (Exception e) {
				LOG.error("No se recupero la clausula la notificacion al usuario. "+e.getCause()+" "+e.getMessage());
			}
		}
		return clausulaOperador;
	}
	
	public void enviar(){
		if(validarDatos()){
			guardar();
			if(habilitarCorreccion){
				try {
					procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
					JsfUtil.redirectTo(BANDEJA_RETCE);
				} catch (JbpmException e) {					
					e.printStackTrace();
				}
			}else{
				if(requiereRevisionProyecto()){
					ProcesoRetceController procesoRetceController =JsfUtil.getBean(ProcesoRetceController.class);
					if(procesoRetceController.iniciarProceso(informacionProyecto)){
						informacionProyecto.setInformacionEnviada(true);
						guardar();
						JsfUtil.redirectTo(BANDEJA_RETCE);
					}else{
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						//return;
					}
				//Si el proyecto es emitido en Sistema y no requiere revision
				}else if(!informacionProyecto.getEsEmisionFisica()){
					informacionProyecto.setInformacionValidada(true);
					informacionProyectoFacade.save(informacionProyecto, JsfUtil.getLoggedUser());
					cargarListaInfbasica();
					verTablaInfBasica();
				}
			}
		}
	}
	
	@Getter
	@Setter
	private Boolean verInclusion;
	
	@Getter 
	@Setter
	private String textoInclusion="";
	
	@Setter
	@Getter
	private Inclusion inclusion;
	
	@Getter
	@Setter
	private List<Inclusion>inclusionList;
	
	@Getter
	@Setter
	private List<Inclusion>inclusionEliminadosList;
	
	@EJB
	private InclusionFacade inclusionFacade;
	
	public void ingresarInclusion(){
		verInclusion=true;
		if(informacionProyecto.getNombreProceso().equals("Registro Ambiental")){
			textoInclusion="Número de actualización ambiental a la autorización ambiental administrativa";
		}else{
			textoInclusion="Número de inclusión ambiental a la autorización ambiental administrativa";
		}
	}
	
	public void crearInclusion(){
		inclusion=new Inclusion();
	}
	
	public void agregarInclusion(){
		if(inclusion.getDocumento()!=null && inclusion.getDocumentoEstudio()!=null){
			if(!inclusionList.contains(inclusion)){
				inclusionList.add(inclusion);
			}
			RequestContext.getCurrentInstance().execute("PF('inclusionDiag').hide();");
			actualizaInclusiones=true;
		}else if(inclusion.getDocumento()==null){
			JsfUtil.addMessageError("Debe adjuntar Resolución Ambiental");
		}else if(inclusion.getDocumentoEstudio()==null){
			JsfUtil.addMessageError("Debe adjuntar el Estudio Complementario");
		}
	}
	
	public void fileUploadInclusion(FileUploadEvent event) {
		fileAdjuntoInclusion = event.getFile();
		inclusion.setDocumento(UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoInclusion.getContents(),fileAdjuntoInclusion.getFileName()));
	}
	
	public void fileUploadEstudioInclusion(FileUploadEvent event) {
		fileAdjuntoEstudioInclusion = event.getFile();
		inclusion.setDocumentoEstudio(UtilDocumento.generateDocumentZipRarFromUpload(fileAdjuntoEstudioInclusion.getContents(),fileAdjuntoEstudioInclusion.getFileName()));		
	}

	public void fileUploadOficio(FileUploadEvent event) {
		fileAdjuntoOficio = event.getFile();
		InformacionProyectoCambioDesechosPeligrosos cambioProyectoDesecho = (InformacionProyectoCambioDesechosPeligrosos) event.getComponent().getAttributes().get("proyectoDesechoCambio");
		cambioProyectoDesecho.setDocumento(UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoOficio.getContents(),fileAdjuntoOficio.getFileName()));		
	}

	public StreamedContent descargarDocumentoInclusion(Documento objDocumento){
		if (objDocumento != null)
			return descargarDocumento(objDocumento);
		return null;
	}
	
	public void editarInclusion(Inclusion item){
		inclusion=item;
	}
	
	public void eliminarInclusion(Inclusion item){
		if(item.getId()!=null)
			inclusionEliminadosList.add(item);
		inclusionList.remove(item);
	}
	
	public void eliminarUbicacion(ProyectoInformacionUbicacionGeografica item){
		if(item.getId()!=null)
			ubicacionesGeograficasEliminar.add(item);
		ubicacionesGeograficas.remove(item);
	}

	public void irReporteListener(Integer idInformacionBasica)
	{
		JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), idInformacionBasica);
	}
	
	public void guardarRevision(){
		if(!informacionProyecto.getInformacionValidada()){
			try {
				List<ObservacionesFormularios>  obsInforme = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(informacionProyecto.getId(),InformacionProyecto.class.getSimpleName());
				if(obsInforme.isEmpty()){
					JsfUtil.addMessageWarning("Debe agregar al menos una observacion.");
					return;
				}
			} catch (ServiceException e) {
				LOG.error("No se pudo cargar observaciones en InformacionBasicaController guardarRevision");
			}
		}
		
		informacionProyectoFacade.saveRevision(informacionProyecto, JsfUtil.getLoggedUser());
		for (Inclusion inc : inclusionList) {
			if(inc.getInformacionValidada()==null){
				inc.setInformacionValidada(informacionProyecto.getInformacionValidada());
				inclusionFacade.saveRevision(inc,  JsfUtil.getLoggedUser());
			}
		}
		// para los desechos peligrosos 
		for (InformacionProyectoDesechosPeligrosos desechos : listaProyectoDesechos) {
			if(desechos.getInformacionValidada()==null){
				desechos.setInformacionValidada(informacionProyecto.getInformacionValidada());
				informacionProyectoDesechosPeligrososFacade.saveRevision(desechos,  JsfUtil.getLoggedUser());
			}
		}
		// para los desechos peligrosos que cambiaron a no peligrosos o especiales
		for (InformacionProyectoCambioDesechosPeligrosos desechos : listaProyectoCambioDesechos) {
			if(desechos.getInformacionValidada()==null){
				desechos.setInformacionValidada(informacionProyecto.getInformacionValidada());
				informacionProyectoCambioDesechosPeligrososFacade.saveRevision(desechos,  JsfUtil.getLoggedUser());
			}
		}

		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		Map<String,Object> params =new HashMap<>();
		params.put("tiene_observaciones_proyecto", !informacionProyecto.getInformacionValidada());
		try {
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getProcessInstanceId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			notificacionOperadorRevisionProyecto();
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (JbpmException e) {
			LOG.error("No se pudo enviar la tarea de revision de proyecto");
			e.printStackTrace();
		}
	}
	
	private void notificacionOperadorRevisionProyecto(){
		try {						
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(informacionProyecto.getInformacionValidada()?"bodyNotificacionOperadorRevisionProyectoRetceAprobacion":"bodyNotificacionOperadorRevisionProyectoRetceObservacion", new Object[]{nombreUsuario, informacionProyecto.getCodigo()});
			Email.sendEmail(usuarioOperador, "Ingresar Informacion Proyecto RETCE", mensaje,"",JsfUtil.getLoggedUser());
		} catch (Exception e) {
			LOG.error("No se envio la notificacion al usuario. "+e.getCause()+" "+e.getMessage());
		}		
	}

	public void cargarListaFiltros(Integer id){
		filtrosSettingsMap.put(id, listarFasesdesechosCambio);
	}
	
	public List<DetalleCatalogoGeneral> getListaFases(Integer desechosId){
		//List<String>  datos= new ArrayList<String>();
		listarFasesdesechosCambio= new ArrayList<String>();
		for (Map.Entry<Integer, List<String>> entry : filtrosSettingsMap.entrySet()) {
		   if ( desechosId.equals(entry.getKey())){
			   listarFasesdesechosCambio = entry.getValue();
			   break;
		    }
		}
		return catalogoFasesList;
	}

	public void cargarListaFiltrosResiduosdesechos(Integer id){
		if(filtrosMapResiduosDesechos  == null){
			filtrosMapResiduosDesechos = new HashMap<>();
		}
		filtrosMapResiduosDesechos.put(id, listarFasesResiduosDesechos);
	}
	
	public List<DetalleCatalogoGeneral> getListaFasesResiduosDesechos(Integer desechosId){
		listarFasesResiduosDesechos= null;
		if(filtrosMapResiduosDesechos  != null){
			for (Map.Entry<Integer, List<String>> entry : filtrosMapResiduosDesechos.entrySet()) {
			   if ( desechosId.equals(entry.getKey())){
				   listarFasesResiduosDesechos = entry.getValue();
				   break;
			    }
			}
		}
		return catalogoFasesList;
	}

	public void onNodeSelect(NodeSelectEvent event) {
		DesechoPeligroso desechoSeleccionado= (DesechoPeligroso) event.getTreeNode().getData();
		if ( !listaDesechosId.toString().contains(", "+desechoSeleccionado.getId().toString())){
			InformacionProyectoDesechosPeligrosos objDesechoProyecto = new InformacionProyectoDesechosPeligrosos();
			objDesechoProyecto.setDesechosPeligrosos(desechoSeleccionado);
			objDesechoProyecto.setInformacionProyecto(informacionProyecto);
			listaProyectoDesechos.add(objDesechoProyecto);
			listaDesechosId.add(desechoSeleccionado.getId().toString());
		}
	}

	public void onNodeSelectCambio(NodeSelectEvent event) {
		DesechoPeligroso desechoSeleccionado= (DesechoPeligroso) event.getTreeNode().getData();
		if ( !listaCambiosDesechosId.toString().contains(", "+desechoSeleccionado.getId().toString())){
			InformacionProyectoCambioDesechosPeligrosos objDesechoProyecto = new InformacionProyectoCambioDesechosPeligrosos();
			objDesechoProyecto.setDesechosPeligrosos(desechoSeleccionado);
			objDesechoProyecto.setInformacionProyecto(informacionProyecto);
			listaProyectoCambioDesechos.add(objDesechoProyecto);
			listaCambiosDesechosId.add(desechoSeleccionado.getId().toString());
		}
	}
	
	public void seleccionarART(AprobacionRequisitosTecnicos objART){
		objART.setSeleccionado(true);
	}

	
	public void seleccionarProvincia()
	{
		cantonesList=new ArrayList<UbicacionesGeografica>();
		parroquiasList=new ArrayList<UbicacionesGeografica>();
		canton=null;
		//informacionProyecto.setUbicacionesGeografica(null);
		
		if(provincia!=null){
			cantonesList=ubicacionGeograficaFacade.getUbicacionPorPadre(provincia);
		}
	}
	
	public void seleccionarCanton()
	{		
		parroquiasList=new ArrayList<UbicacionesGeografica>();	
		//informacionProyecto.setUbicacionesGeografica(null);
		
		if(canton!=null){
			parroquiasList=ubicacionGeograficaFacade.getUbicacionPorPadre(canton);
		}
	}
	/****************************************  RCOA  *********************************************/

	private void ubicacionListenerRCOA(){
		if(informacionProyecto.getCodigo() != null && !informacionProyecto.getCodigo().isEmpty() && proyectoLicenciaCoa != null){
			listaUbicacionesGeograficas = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa);
		}
	}
	
	private void documentosListenerRCOA(Integer proyectoId){
		try {
			adjuntoMapaRcoa=null;
			adjuntoCoordenadas=null;
			adjuntoResolucionRcoa=null;
			if(proyectoId!=null){

				List<DocumentosCOA> listaDocumentosMap = documentosCoaFacade.documentoXTablaIdXIdDoc(proyectoId, TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA,"ProyectoLicenciaCoa");		
				if (listaDocumentosMap.size() > 0) {
					adjuntoMapaRcoa = listaDocumentosMap.get(0);
				}
				if(adjuntoMapaRcoa!=null)
					adjuntoMapaRcoa.setContenidoDocumento(documentosCoaFacade.descargar(adjuntoMapaRcoa.getIdAlfresco()));
				

				List<DocumentosCOA> listaDocumentos = documentosCoaFacade.documentoXTablaIdXIdDoc(proyectoId, TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA,"ProyectoLicenciaCoa");
				if (listaDocumentos.size() > 0) {				
					adjuntoCoordenadasRcoa = listaDocumentos.get(0);
				}
				if(adjuntoCoordenadasRcoa!=null)
					adjuntoCoordenadasRcoa.setContenidoDocumento(documentosCoaFacade.descargar(adjuntoCoordenadasRcoa.getIdAlfresco()));
				
				adjuntoResolucionRcoa = documentosRegistroRCOAFacade.documentoXTablaIdXIdDoc(proyectoId, ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RESOLUCION_AMBIENTAL_RCOA);
				if(adjuntoResolucionRcoa!=null)
					adjuntoResolucionRcoa.setContenidoDocumento(documentosRegistroRCOAFacade.descargar(adjuntoResolucionRcoa.getAlfrescoId()));
			}		
		} catch (Exception e) {
			LOG.error("No se pudo descargar Documentos. InformacionBasicaController");
		}
		
	}

	private StreamedContent descargarDocumentoRcoa(byte[] documento, String nombreDocumento){
		try {
			if(documento!=null){
					DefaultStreamedContent content = null;				
					if (nombreDocumento != null) {
						content = new DefaultStreamedContent(new ByteArrayInputStream(documento));
						content.setName(nombreDocumento);
						return content;
					}
				
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public StreamedContent getDocumentoMapaRcoa(){		
		return descargarDocumentoRcoa(adjuntoMapaRcoa.getContenidoDocumento(), adjuntoMapaRcoa.getNombreDocumento());
	}
	

	public StreamedContent getDocumentoCoordenadasRcoa(){		
		return descargarDocumentoRcoa(adjuntoCoordenadasRcoa.getContenidoDocumento(), adjuntoCoordenadasRcoa.getNombreDocumento());
	}

	public StreamedContent getDocumentoAdjuntoResolucionRcoa() {
		return descargarDocumentoRcoa(adjuntoResolucionRcoa.getContenidoDocumento(), adjuntoResolucionRcoa.getNombre());
	}
	private void verificarCapasInterseccion(String coodenadas){
		try{			
	        SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
	        ws.setEndpoint(Constantes.getInterseccionesWS());
	
			//Capas de intersección----------------------------------------------------------
			ContieneCapa_entrada capaImpla = new ContieneCapa_entrada(); 
			capaImpla.setU(Constantes.getUserWebServicesSnap());
			capaImpla.setXy(coodenadas);
			InterseccionCapa_resultado[]intRestCapaImpl;
			intRestCapaImpl=ws.interseccionCapa(capaImpla);
			
			if (intRestCapaImpl[0].getInformacion().getError() != null) {            		
        		JsfUtil.addMessageError(intRestCapaImpl[0].getInformacion().getError().toString());
        		return;
        	}
			
			InterseccionProyectoLicenciaAmbiental capaInterseccion = new InterseccionProyectoLicenciaAmbiental();	                			
			List<DetalleInterseccionProyectoAmbiental> listaIntersecciones = new ArrayList<DetalleInterseccionProyectoAmbiental>();
			if(intRestCapaImpl[0].getCapa() != null)
			for (IntersecadoCapa_capa capas : intRestCapaImpl[0].getCapa()) {
				if(Integer.valueOf(capas.getNum())>0)
				{
					capaInterseccion = new InterseccionProyectoLicenciaAmbiental();
					
					if(capas.getNombre().equals("COMPONENTE SOCIAL"))
					{
						for(Campos_coordenada social : capas.getCampos())
						{
							 wolframSocial.add(Integer.valueOf(social.getPonde()));
						}
					}
					else if(capas.getNombre().equals("COMPONENTE ACODIGA MARITIMA"))
					{
						for(Campos_coordenada maritima : capas.getCampos())
						{
							wolframBiofisica.add(Integer.valueOf(maritima.getPonde()));
						}
					}
					else if(capas.getNombre().equals("COMPONENTE ACOGIDA"))
					{
						for(Campos_coordenada biofisica : capas.getCampos())
						{
							wolframBiofisica.add(Integer.valueOf(biofisica.getPonde()));
						}
					}
					else if(capas.getNombre().equals("OFICINAS_TECNICAS"))
					{
						for(Campos_coordenada oficiona : capas.getCampos())
						{
							System.out.println("oficina tecnica:"+ oficiona.getFcode());
							ubicacionOficinaTecnica=ubicacionfacade.buscarUbicacionPorCodigoInec(oficiona.getFcode());
							break;
						}
					}
					else
					{	
						if(capas.getNombre().equals("ZONAS INTANGIBLES Y ZONAS AMORTIGUAMIENTO"))
						{
							if(Integer.valueOf(capas.getNum())>0)
							estadoZonaIntangibleAux=true;
						}
						if(capas.getNombre().equals("ZONAS DE AMORTIGUAMIENTO YASUNI"))
						{
							if(Integer.valueOf(capas.getNum())>0)
							estadoZonaIntangibleAux=true;
						}
						if(capas.getNombre().equals("LIMITE INTERNO 20 KM"))
						{
							if(Integer.valueOf(capas.getNum())>0)
								estadoFrontera=true;
						}	                						

					}	                					
				}
			}
		} catch (RemoteException e) {
             	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
             	System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
             	
             	ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
                superficieProyecto = BigDecimal.ZERO;
         		superficieMetrosCuadrados = BigDecimal.ZERO;
             	coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
        		coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
             	mostrarDetalleInterseccion = false;
         		estadoFrontera=false;
             }
	}
}
