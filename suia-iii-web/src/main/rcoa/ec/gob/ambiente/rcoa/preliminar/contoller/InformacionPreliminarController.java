package ec.gob.ambiente.rcoa.preliminar.contoller;	  

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.DefaultRequestContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.client.SuiaServicesArcon;
import ec.gob.ambiente.client.SuiaServices_Service_Arcon;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.mapa.webservices.GenerarMapaWSService;
import ec.gob.ambiente.mapa.webservices.ResponseCertificado;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.BloquesBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.certificado.interseccion.CertificadoInterseccionRcoaOficioHtml;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.dto.GeneradorCustom;
import ec.gob.ambiente.rcoa.dto.SubactividadDto;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCIUUConcurrenteFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCIUUFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCapaPermisoFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CombinacionActividadesFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CriterioMagnitudFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.InterseccionProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.NombreProyectosFacade;
import ec.gob.ambiente.rcoa.facade.NombreProyectosMspFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoCoaCiuuSubActividadesFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaCiuuBloquesFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalConcesionesMinerasFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RucMspFacade;
import ec.gob.ambiente.rcoa.facade.SubActividadesFacade;
import ec.gob.ambiente.rcoa.facade.ValorCalculoFacade;
import ec.gob.ambiente.rcoa.facade.ValorMagnitudFacade;
import ec.gob.ambiente.rcoa.facade.VariableCriterioFacade;
import ec.gob.ambiente.rcoa.facade.VinculoProyectoRgdSuiaFacade;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CatalogoCIUUConcurrente;
import ec.gob.ambiente.rcoa.model.CatalogoCapaPermiso;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.CombinacionSubActividades;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.CriterioMagnitud;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.model.NombreProyectos;
import ec.gob.ambiente.rcoa.model.NombreProyectosMsp;
import ec.gob.ambiente.rcoa.model.ProyectoCoaCiuuSubActividades;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaCiuuBloques;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalConcesionesMineras;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaCiiuResiduos;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RucMsp;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.rcoa.model.ValorCalculo;
import ec.gob.ambiente.rcoa.model.ValorMagnitud;
import ec.gob.ambiente.rcoa.model.VariableCriterio;
import ec.gob.ambiente.rcoa.model.VinculoProyectoRgdSuia;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PermisoRegistroGeneradorDesechosFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.FormaPuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PermisoRegistroGeneradorDesechos;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.util.CatalogoImportanciaVO;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.rcoa.util.WolframVO;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.OrganizacionViabilidadTecnicaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecnicaProyectoFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecnicaRcoaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.OrganizacionViabilidadTecnica;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnica;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnicaProyecto;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.NotificacionActividadCIIUFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.administracion.service.AreaService;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Bloque;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.FormaPuntoRecuperacion;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.NotificacionActividadCIIU;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.TipoPoblacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.tipoforma.facade.TipoFormaFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.arcom.dm.ws.DerechoMineroMAEDTO;
import index.ContienePoligono_entrada;
import index.ContienePoligono_resultado;
import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class InformacionPreliminarController {

	private static final Logger LOGGER = Logger.getLogger(InformacionPreliminarController.class);
	private static final String IMAGEN_LICENCIA_AMBIENTAL = "/resources/images/mensajes/rcoa_fin_registro_preliminar.png";
	private static final String MENSAJE_FINALIZADO="Para continuar con el proceso, usted debe seguir en el sistema los pasos descritos en la imagen";
	
	@Setter
	@Getter
	@ManagedProperty(value = "#{coordenadasRcoaBean}")
	private CoordenadasRcoaBean coordenadasRcoaBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

    @ManagedProperty(value = "#{residuosActividadesCiiuBean}")
	@Getter
	@Setter
	private ResiduosActividadesCiiuBean residuosActividadesCiiuBean;
    
    @ManagedProperty(value = "#{subActividadesCiiuBean}")
	@Getter
	@Setter
	private SubActividadesCiiuBean subActividadesCiiuBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;	
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
    private CrudServiceBean crudServiceBean;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private TipoFormaFacade tipoFormaFacade; 
	@EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalCoaShapeFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private GestionarProductosQuimicosProyectoAmbientalFacade gestionarProductosQuimicosProyectoAmbientalFacade;
	@EJB
	private CatalogoCIUUFacade catalogoCIUUFacade;
	@EJB
	private VariableCriterioFacade variableCriterioFacade; 
	@EJB
	private ValorMagnitudFacade valorMagnitudFacade; 
	@EJB
	private UbicacionGeograficaFacade ubicacionfacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private CriterioMagnitudFacade criterioMagnitudFacade;
	@EJB
	private ValorCalculoFacade valorCalculoFacade;
	@EJB
	private CapasCoaFacade capasCoaFacade;
	@EJB
	private InterseccionProyectoLicenciaAmbientalFacade interseccionProyectoLicenciaAmbientalFacade;
	@EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
	@EJB
    private AreaService areaService;
	@EJB
	private DocumentosCoaFacade documentosFacade;
	@EJB
	private DocumentosFacade documentosSuiaIIIFacade;
	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
    private OrganizacionFacade organizacionFacade;
	@EJB
    private DocumentosCoaFacade documentoFacade;
	@EJB
	private ProyectoLicenciaAmbientalConcesionesMinerasFacade proyectoLicenciaAmbientalConcesionesMinerasFacade;
	@EJB
	private ProyectoLicenciaAmbientalCoaCiuuBloquesFacade proyectoLicenciaAmbientalCoaCiuuBloquesFacade;
	@EJB
	private CatalogoCIUUConcurrenteFacade catalogoCIUUConcurrenteFacade;
	@EJB
	private RucMspFacade rucMspFacade;
	@EJB
	private NombreProyectosFacade nombreProyectosFacade;
	@EJB
	private NombreProyectosMspFacade nombreProyectosMspFacade;
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;
	@EJB
    private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
    private AreaFacade areaFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private SubActividadesFacade subActividadesFacade;
	@EJB
	private NotificacionActividadCIIUFacade notificacionActividadCIIUFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;

	@EJB
	private ViabilidadTecnicaRcoaFacade viabilidadTecnicaFacade;
	@EJB
	private ViabilidadTecnicaProyectoFacade viabilidadTecnicaProyectoFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
    private PuntoRecuperacionRgdRcoaFacade puntoRecuperacionRgdRcoaFacade;
	@EJB
    private VinculoProyectoRgdSuiaFacade vinculoProyectoRgdSuiaFacade;
	@EJB
    private PermisoRegistroGeneradorDesechosFacade permisoRegistroGeneradorDesechosFacade;
    @EJB
    private CatalogoCapaPermisoFacade catalogoCapaPermisoFacade;
    @EJB
    private OrganizacionViabilidadTecnicaFacade organizacionViabilidadTecnicaFacade;

	@Getter
	private List<TipoPoblacion> tiposPoblaciones;
	@Getter
	@Setter
	private boolean skip;	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> sustanciaQuimicaSeleccionada = new ArrayList<SustanciaQuimicaPeligrosa>();
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> listaSustanciasQuimicas;
	@Setter
	@Getter
	private HashMap<String, UbicacionesGeografica> parroquiaSeleccionadas;
	@Setter
	@Getter
	private UbicacionesGeografica provincia;
	@Setter
	@Getter
	private UbicacionesGeografica parroquia;
	@Setter
	@Getter
	private UbicacionesGeografica canton;
	@Setter
	@Getter
	private List<UbicacionesGeografica> provincias;
	@Setter
	@Getter
	private List<UbicacionesGeografica> cantones;
	@Setter
	@Getter
	private List<UbicacionesGeografica> parroquias;
	@Setter
	@Getter
	private List<UbicacionesGeografica> listParroquias;
	@Getter
	@Setter
	private List<CatalogoCIUU> listaCatalogoCiiu = new ArrayList<CatalogoCIUU>();
	
	private TipoPoblacion tipoPoblacionSimple;	
	private Integer ubicacionRepetida=0;
	@Getter
    private List<TipoForma> tiposFormas;
	
	private TipoForma poligono;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto= new ProyectoLicenciaCoa();
	
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;
	
	private ProyectoLicenciaAmbientalCoaShape shape = new ProyectoLicenciaAmbientalCoaShape();
	
	JSONObject categoria;
	
	private Integer categoriaXNormativa=0;
	
	private Integer tipoAutoridadAmbiental=1;
	
	@Getter
	@Setter
	private CatalogoCIUU ciiuPrincipal= new CatalogoCIUU();
	@Getter
	@Setter
	private CatalogoCIUU ciiuComplementaria1= new CatalogoCIUU();
	@Getter
	@Setter
	private CatalogoCIUU ciiuComplementaria2= new CatalogoCIUU();
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu ciiuArearesponsable= new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	private List<VariableCriterio> listaCriterioMagnitudConsumo = new ArrayList<VariableCriterio>();
	
	@Getter
	@Setter
	private VariableCriterio criterioSeleccion = new VariableCriterio();
	@Getter
	@Setter
	private List<VariableCriterio> listaCriterioMagnitudDimensionamiento = new ArrayList<VariableCriterio>();
	@Getter
	@Setter
	private List<VariableCriterio> listaCriterioMagnitudCapacidad = new ArrayList<VariableCriterio>();
	@Getter
	@Setter
	private List<ValorMagnitud> listaValoresMagnitud1 = new ArrayList<ValorMagnitud>();
	@Getter
	@Setter
	private List<ValorMagnitud> listaValoresMagnitud2 = new ArrayList<ValorMagnitud>();
	@Getter
	@Setter
	private List<ValorMagnitud> listaValoresMagnitud3 = new ArrayList<ValorMagnitud>();
	@Getter
	@Setter
	private ValorMagnitud valorMagnitud1 = new ValorMagnitud();
	@Getter
	@Setter
	private ValorMagnitud valorMagnitud2 = new ValorMagnitud();
	@Getter
	@Setter
	private ValorMagnitud valorMagnitud3 = new ValorMagnitud();	
	@Getter
	@Setter
	private ValorMagnitud valorMagnitudCalculo = new ValorMagnitud();	
	@Getter
	@Setter
	private String criteriomagnitud="";
	
	@Getter
	@Setter
	private List<InterseccionProyectoLicenciaAmbiental> intersecciones = new ArrayList<InterseccionProyectoLicenciaAmbiental>();
	
	@Getter
	@Setter
	private DocumentosCOA documentoMapa, documentoCertificado,documentoAltoImpacto,documentoGeneticoCiiu1,documentoGeneticoCiiu2,documentoGeneticoCiiu3,documentoDocSectorial,documentoDocFrontera, documentoDocCamaronera;
	
	private GenerarMapaWSService wsMapas;
	
	private CertificadoInterseccionOficioCoa oficioCI;
	private String nombreOperador,cedulaOperador,razonSocial,codigoCiiu;
	private Usuario usuarioAutoridad;
	private List<UbicacionesGeografica> ubicacionProyectoLista;
    private List<DetalleInterseccionProyectoAmbiental> interseccionLista;			
    private List<CapasCoa> capasCoaLista;
    
	private boolean estadoMagnitud;
	private boolean estadoSustanciasQuimicas;
	@Getter
	@Setter
	private boolean esMercurio;
	private boolean esActividadExtractiva=false;
	@Getter
	@Setter
	private boolean fabricaSustancia;
	@Getter
	@Setter
	private String nombreSector="";	
	@Getter
	@Setter
	private CatalogoCIUU actividadPrimaria= new CatalogoCIUU();
	
	private List<String> msgError= new ArrayList<String>();
	
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu ciiu1= new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu ciiu2= new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu ciiu3= new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	private List<ProyectoLicenciaAmbientalConcesionesMineras> listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
	@Getter
	@Setter
	private ProyectoLicenciaAmbientalConcesionesMineras concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
	@Getter
	@Setter
	private ProyectoLicenciaAmbientalConcesionesMineras concesionesMineras = new ProyectoLicenciaAmbientalConcesionesMineras();	
	@Setter
	@ManagedProperty(value = "#{bloquesBean}")
	private BloquesBean bloquesBean;
	
	@Getter
	@Setter
	private String pathImagen =IMAGEN_LICENCIA_AMBIENTAL;
	@Getter
	@Setter
	private String mensaje =MENSAJE_FINALIZADO;
	
	@Getter
	@Setter
	private List<RucMsp> listaProyectosMsp;
	
	@Getter
	@Setter
	private List<NombreProyectos> listaNombresProyectosMsp;
	
	@Getter
	@Setter
	private boolean usuarioMsp = false;
	
	@Getter
	@Setter
	private Integer idRucMsp;
	
	private NombreProyectos nombreProyecto;
	
	private RucMsp rucMsp;
	
	@Getter
	@Setter
	private boolean otroMsp = false;
	
	@Getter
	@Setter
	private boolean mostrarTextArea = true;	
	
	private NombreProyectosMsp nombreProyectoMsp;
	
	private byte[] ayudaCatalogoCiiu;
	
	@Getter
	@Setter
	private Boolean existeHidrocarburos = false;
	
	@Getter
	@Setter
	private Boolean existeMineria = false;
	
	@Getter
	@Setter
	private Boolean irFinalizar = false;
	
	@Getter
	@Setter
	private Boolean tramiteEnProceso = false;
	
	@Getter
	@Setter
	private Boolean esCiiu1HidrocarburoMineriaElectrico = false, esCiiu2HidrocarburoMineriaElectrico = false, esCiiu3HidrocarburoMineriaElectrico = false;
	
	@Setter
	@Getter
	private List<DocumentosCOA> listaDocumentosEliminar = new ArrayList<DocumentosCOA>();
	
	@Getter
	@Setter
	private DocumentosCOA documentoSenaguaCiiu1, documentoSenaguaCiiu2, documentoSenaguaCiiu3;
	
	@Getter
	@Setter
	private DocumentosCOA documentoPorSectorCiiu1, documentoPorSectorCiiu2, documentoPorSectorCiiu3; //autorizaciones para las actividades de Hidrocarburos, Minería o Eléctrico
	
	@Getter
	@Setter
	private DocumentosCOA documentoViabilidadPngidsCiiu1, documentoViabilidadPngidsCiiu2, documentoViabilidadPngidsCiiu3;
	
	@Getter
	@Setter
	private Boolean operadorEsGobiernoOMunicipio = false;
	
	@Getter
	@Setter
	private Boolean existeRgdObtenidoAnterior, esRgdValido;
	
	@Getter
	@Setter
	private Integer tipoProyecto;
	
	@Getter
	@Setter
	private boolean dialogoArt, bloqueoCamposGestionTransporte;
	
	@Getter
	@Setter
	private boolean subActivadesAlcantarillado1,subActivadesAlcantarillado2,subActivadesAlcantarillado3;
	
	@Getter
	@Setter
	private boolean subActivadesGalapagos1,subActivadesGalapagos2,subActivadesGalapagos3;
	
	@Getter
	@Setter
	private String mensajeArt;
	
	@Getter
	@Setter
	private List<CatalogoGeneralCoa> listaTiposNombresMsp, listaTipoRegimenMinero;
	
	@Getter
	@Setter
	private int idTipoNombreMsp;
	
	@Getter
	@Setter
	private List<NombreProyectos> listaNombresProyectosMspVal;
	
	@Getter
	@Setter
	private NotificacionActividadCIIU notificacionActividadCIIU = new NotificacionActividadCIIU();
	
	@Getter
	@Setter
	private Boolean actividadEncontrada=true, rbVerificaEncuentraActividadCIIU=true;
	
	@Getter
	@Setter
	private Boolean areaPendienteAsignar = false;
	
	@Getter
	@Setter
	private String mensajeNotificacion, mensajeNotificacionCamaroneras, mensajeBotonAyudaCamaroneras;
		
	@Getter
	@Setter
	private Boolean validarNextSetp;	
	
	@Getter
	@Setter
	private Boolean catalogoCamaronera_ciuu1 = false;
	
	@Getter
	@Setter
	private Boolean catalogoCamaronera_ciuu2 = false;
	
	@Getter
	@Setter
	private Boolean catalogoCamaronera_ciuu3 = false;
	
	@Getter
	@Setter
	private String codigoOficioGRECI1,codigoOficioGRECI2, codigoOficioGRECI3;
	
	@Getter
	@Setter
	private ViabilidadTecnica viabilidad1, viabilidad2, viabilidad3;
	
	@Getter
	@Setter
	private List<GeneradorCustom> listaRegistrosGeneradores, vinculacionesRgdEliminar;
	
	@Getter
	@Setter
	private GeneradorCustom registroGeneradorSeleccionado;
	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> listaSustanciaQuimicaSeleccionadaOtros = new ArrayList<SustanciaQuimicaPeligrosa>();
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> listaSustanciaQuimicaSeleccionadaOtrosTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
	

	@Getter
	@Setter
	private boolean esControlSustancia = false;
	
	@Getter
	@Setter
	private boolean esMercurioTransporta, verProgresoCapas;
	
	@Getter
	@Setter
	private Boolean tieneDocumentoRsq;
	
	@Getter
	@Setter
	private Boolean rsqVigente;
	
	@Getter
	@Setter
	private SubActividades actividadBloque1=new SubActividades(),actividadBloque2=new SubActividades(),actividadBloque3=new SubActividades(),subActividadBloque2=new SubActividades();
	
	@Getter
	@Setter
	private String validaSubcategoria=null;

	@Getter
	@Setter
	private boolean esSubactividadSubestacionElectrica=false;

	@Getter
	@Setter
	private Boolean ingresoViabilidadTecnica1, ingresoViabilidadTecnica2, ingresoViabilidadTecnica3;
	
	@Getter
	@Setter
	private Boolean tieneContratoMineria;
	
	@Getter
	@Setter
	private String zona;
	
	@Getter
	@Setter
	private Boolean esValidacionZonaMixta = false;
	
	private Integer valorProgresoInterseccion;

	@PostConstruct
	public void inicio()
	{
		notificacionActividadCIIU.setDestinatario(Constantes.getMailNotificacionActividadCIIU());
		try {
			ayudaCatalogoCiiu = documentosSuiaIIIFacade.descargarDocumentoPorNombreYDirectorioBase(Constantes.AYUDA_CATALOGO_CIIU,null);
        } catch (Exception e) {
        }
		tiposPoblaciones = proyectoLicenciamientoAmbientalFacade.getTiposPoblaciones();		
		tiposPoblaciones.remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_PERIFERICA));
		tiposPoblaciones.remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_CONTINENTAL));
		tiposPoblaciones.remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_NO_CONTINENTAL));
		tiposPoblaciones.remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_MIXTO));
		listaSustanciasQuimicas = proyectoLicenciaCoaFacade.listaSustanciasQuimicas();
		tiposFormas = tipoFormaFacade.listarTiposForma();
		poligono=getTipoForma(TipoForma.TIPO_FORMA_POLIGONO);
		listaCatalogoCiiu=catalogoCIUUFacade.listaCatalogoCiuu();
		proyecto.setSuperficie(BigDecimal.ZERO);
		nivelCriterios1();
		nivelCriterios2();
		nivelCriterios3();
		validarCodigoRgd(false);
		
		ingresoViabilidadTecnica1 = true;
		ingresoViabilidadTecnica2 = true;
		ingresoViabilidadTecnica3 = true;
		
		//mineria =MAE-RA-2020-415509
		//hidrocarburos =MAE-RA-2020-415514
//		proyecto=proyectoLicenciaCoaFacade.buscarProyecto("MAE-RA-2020-415560");
		listaSustanciaQuimicaSeleccionadaOtros = new ArrayList<SustanciaQuimicaPeligrosa>();
	    listaSustanciaQuimicaSeleccionadaOtrosTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
		tipoProyecto = proyectosBean.getTipoProyectoRcoa();
		if(proyecto.getCodigoUnicoAmbiental()!=null)
		{			
			cargaDatos();
		} else {
			//cuando el proyecto viene desde proyecto pendiente o por modificación de informacion
			if(proyectosBean.getProyectoRcoa() != null) {
				proyecto = proyectosBean.getProyectoRcoa();
				tipoProyecto = proyecto.getTipoProyecto();
				
				if(proyectosBean.getModificarProyectoRcoa())
					irFinalizar = true;
				else
					tramiteEnProceso = true;
				
				cargaDatos();
				
				proyectosBean.setModificarProyectoRcoa(false);
				proyectosBean.setProyectoRcoa(null);
			}
		}
		
		listaProyectosMsp = new ArrayList<RucMsp>();
		listaNombresProyectosMsp = new ArrayList<NombreProyectos>();
		listaNombresProyectosMspVal = new ArrayList<NombreProyectos>();
		vinculacionesRgdEliminar = new ArrayList<>();
		
//		listaNombresProyectosMspVal = rucMspFacade.buscarProyectosPorRuc(loginBean.getNombreUsuario());
		
		RucMsp userMsp = rucMspFacade.buscarPorRuc(loginBean.getNombreUsuario());
		
		if(userMsp != null && userMsp.getId() != null){
			usuarioMsp = true;		
			mostrarTextArea = false;
			
			listaTiposNombresMsp = nombreProyectosFacade.listaTiposNombres(6);
			setIdTipoNombreMsp(-1);
			
			if(proyecto != null && proyecto.getId() != null){
				nombreProyectoMsp = nombreProyectosMspFacade.buscarPorIdProyecto(proyecto.getId());
				
				if(nombreProyectoMsp != null){
					if(nombreProyectoMsp.getNombreProyectos() != null){
						
						nombreProyecto = nombreProyectoMsp.getNombreProyectos();
						
						if(nombreProyectoMsp.getRucMsp() != null){
							rucMsp = nombreProyectoMsp.getRucMsp();
						}
						if(nombreProyecto.getCatalogoGeneralCoa() != null){
							setIdTipoNombreMsp(nombreProyecto.getCatalogoGeneralCoa().getId());
						}						
						setIdRucMsp(nombreProyecto.getId());
						
					}else{
						if(proyecto.getNombreProyecto() != null){
							setIdTipoNombreMsp(0);
							setIdRucMsp(0);
							mostrarTextArea = true;		
							otroMsp = true;
						}
					}
				}else{
					if(proyecto.getNombreProyecto() != null){
						setIdTipoNombreMsp(0);
						setIdRucMsp(0);
						mostrarTextArea = true;		
						otroMsp = true;
					}
				}							
			}			
		}
		
//		if(usuarioMsp){
//			DefaultRequestContext.getCurrentInstance().execute("PF('mensajeMsp').show();");			
//		}
		
		operadorEsGobiernoOMunicipio = false;
		Area areaEnteAcreditado = areaService.getAreaEnteAcreditado(3, loginBean.getUsuario().getNombre());
		if (areaEnteAcreditado != null
				&& (areaEnteAcreditado.getTipoEnteAcreditado().equals("MUNICIPIO") 
						|| areaEnteAcreditado.getTipoEnteAcreditado().equals("GOBIERNO")))
			operadorEsGobiernoOMunicipio = true;

		residuosActividadesCiiuBean.buscarCatalogoResiduosActividades();

		mensajeNotificacionCamaroneras = "Las coordenadas ingresadas no corresponden a la concesión otorgada por favor verifique y vuelva a adjuntar las coordenadas. En el caso de que el inconveniente persista, por favor comunicarse con la Subsecretaría de Acuacultura.";
		try {
			mensajeBotonAyudaCamaroneras = mensajeNotificacionFacade.recuperarValorMensajeNotificacionSD("mensajeRegistroPreliminarBotonAyudaCamaroneras");
		} catch (ServiceException e) {
			e.printStackTrace();
		};
		
		listaTipoRegimenMinero = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.TIPO_REGIMEN_MINERO);
		mostrarSubBloque=false;
	}
	
	public void cargaDatos()
	{
		if(proyecto != null && proyecto.getId() != null){
			List<ProyectoLicenciaAmbientalCoaShape> formas = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();
			formas = proyectoLicenciaAmbientalCoaShapeFacade.buscarFormaGeograficaPorProyecto(proyecto, 2, 0);			
			if(formas == null){
				formas = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();				
			}else{
				coordenadasRcoaBean.setCoordenadasGeograficas(new ArrayList<CoordenadasProyecto>());
				for(ProyectoLicenciaAmbientalCoaShape forma : formas){
					List<CoordenadasProyecto> coordenadasGeograficasGeo = coordenadasProyectoCoaFacade.buscarPorForma(forma);
					coordenadasRcoaBean.getCoordenadasGeograficas().addAll(coordenadasGeograficasGeo);
					String coodenadasgeograficas = "";
					for(CoordenadasProyecto geo: coordenadasRcoaBean.getCoordenadasGeograficas())
					{
						coodenadasgeograficas += (coodenadasgeograficas == "") ? geo.getX() + " " + geo.getY() : "," + geo.getX() + " " + geo.getY();
					}
					coordenadasRcoaBean.setCoodenadasgeograficas(coodenadasgeograficas);
					CoordendasPoligonos poligono = new CoordendasPoligonos();
					poligono.setCoordenadas(coordenadasGeograficasGeo);
					poligono.setTipoForma(forma.getTipoForma());					
					coordenadasRcoaBean.getCoordinatesWrappersGeo().add(poligono);
				}
			}			
			List<ProyectoLicenciaAmbientalCoaShape> formasImplantacion = proyectoLicenciaAmbientalCoaShapeFacade.buscarFormaGeograficaPorProyecto(proyecto, 1, 0);			
			if(formasImplantacion == null){
				formasImplantacion = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();				
			}else{
				List<String> listaCoordenadas = new ArrayList<>();
				for(ProyectoLicenciaAmbientalCoaShape forma : formasImplantacion){
					List<CoordenadasProyecto> coordenadasGeograficasImplantacion = coordenadasProyectoCoaFacade.buscarPorForma(forma);					
					CoordendasPoligonos poligono = new CoordendasPoligonos();
					poligono.setCoordenadas(coordenadasGeograficasImplantacion);
					poligono.setTipoForma(forma.getTipoForma());					
					coordenadasRcoaBean.getCoordinatesWrappers().add(poligono);
					
					String coodenadas = "";
					for (int j = 0; j <= coordenadasGeograficasImplantacion
							.size() - 1; j++) {
						CoordenadasProyecto coordenadasProyecto = coordenadasGeograficasImplantacion.get(j);
						coodenadas += (coodenadas == "") ? coordenadasProyecto.getX().toString() + " " + coordenadasProyecto.getY().toString() 
								: "," + coordenadasProyecto.getX().toString() + " " + coordenadasProyecto.getY().toString();
	                }
					listaCoordenadas.add(coodenadas);
				}
				
				coordenadasRcoaBean.setCoordenadasImplantacion(listaCoordenadas);
			}
			try {
			coordenadasRcoaBean.setUbicacionesSeleccionadas(proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyecto));			
			coordenadasRcoaBean.setUbicacionPrincipal(proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto).getUbicacionesGeografica());
			} catch (Exception e) {
			}
			
			try {
				if(proyecto.getIdCantonOficina() != null)
					coordenadasRcoaBean.setUbicacionOficinaTecnica(ubicacionfacade.buscarPorId(proyecto.getIdCantonOficina()));
			} catch (Exception e) {
			}
			
//			if(proyecto.getConcesionCamaronera() != null ) {
//				coordenadasRcoaBean.setIntersecaConCamaronera(true);
//			}
			
			if(proyecto.getZona_camaronera() != null){
				zona = proyecto.getZona_camaronera();
				
				if(proyecto.getZona_camaronera() != null){
					coordenadasRcoaBean.setMap(proyecto.getZona_camaronera());
				}				
			}
			
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = new ArrayList<ProyectoLicenciaCuaCiuu>();
			listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
			
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				if(actividad.getOrderJerarquia() == 1){
					ciiuPrincipal = actividad.getCatalogoCIUU();
					ciiu1=actividad;
					ciiu1(ciiuPrincipal, false);
					if(actividad.getGenetico()!=null && actividad.getGenetico())
					{
						List<DocumentosCOA> listaDocumentos;
						try {
							listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu1.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO,"ProyectoLicenciaCuaCiuu");
							if (listaDocumentos.size() > 0) {
								documentoGeneticoCiiu1 = listaDocumentos.get(0);
							}
						} catch (CmisAlfrescoException e) {
						}
					}
					
					if(ciiuPrincipal.getSaneamiento()) {
						List<DocumentosCOA> listaDocumentos;
						try {
							listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu1.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_SANEAMIENTO,"ProyectoLicenciaCuaCiuu");
							if (listaDocumentos.size() > 0) {
								documentoSenaguaCiiu1 = listaDocumentos.get(0);
							}
						} catch (CmisAlfrescoException e) {
						}
					}
					
					if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuPrincipal)) {
						if(listaSubActividades1.size() > 0) {
							subActividadesCiiuBean.buscarSubActividadesCiiu(ciiu1, listaSubActividades1, 1);
							//buscar residuos
							residuosActividadesCiiuBean.buscarResiduosActividades(actividad, 1);
						}
					}
					else
					{
						List<ProyectoCoaCiuuSubActividades> cargarSubActividades=proyectoCoaCiuuSubActividadesFacade.cargarSubActividades(actividad);
						requiereTablaResiduo1=false;
						if(cargarSubActividades.size()>0)
						{	
							ArrayList<Integer> list = new ArrayList<Integer>();
							for(ProyectoCoaCiuuSubActividades subSelecionada: cargarSubActividades)
							{
								if(subSelecionada.getRespuesta()!=null && !subSelecionada.getRespuesta())
								{
									parent1=subSelecionada.getSubActividad();
									parent1.setValorOpcion(subSelecionada.getRespuesta());
									subActividad1.setValorOpcion(subSelecionada.getRespuesta());
								}
								else if(subSelecionada.getRespuesta()!=null && subSelecionada.getRespuesta())
								{
									parent1=subSelecionada.getSubActividad();
									parent1.setValorOpcion(subSelecionada.getRespuesta());
									subActividad1.setValorOpcion(subSelecionada.getRespuesta());
									listaSubActividades1 = subActividadesFacade.actividadHijosPorRespuestaPadre(parent1);
								}
								else
								{
									list.add(subSelecionada.getSubActividad().getId());
									listaSubActividadSeleccionad1.add(subSelecionada.getSubActividad());
								}
								if(!requiereTablaResiduo1)
									requiereTablaResiduo1=subSelecionada.getSubActividad().getRequiereIngresoResiduos()==null?false:subSelecionada.getSubActividad().getRequiereIngresoResiduos();
							}
							if(list.size()>1)
							{							
								idSubactividades1 = list.toArray(new Integer[list.size()]);
								combinacion1=actividad.getCombinacionSubActividades().getCombinaciones();
							}
							else
								idSubactividades1 = list.toArray(new Integer[list.size()]);

							residuosActividadesCiiuBean.buscarResiduosActividades(actividad, 1);
						}					
						else if(actividad.getSubActividad()!=null)
						{
							if(actividad.getCatalogoCIUU().getLibreAprovechamiento()) {
								subActividad1 = actividad.getSubActividad();
								parent1.setValorOpcion(subActividad1.getPadreVerdadero());							

								getHijosSubActividad(parent1, 1);

								if(!actividad.getSubActividad().getEsMultiple())
									subActividad1.setValorOpcion(actividad.getValorOpcion());
								continue;
							}
							
							if(actividad.getCatalogoCIUU().getRequiereViabilidadPngids() && actividad.getCatalogoCIUU().getTipoPregunta() == null){
								subActividad1.setValorOpcion(actividad.getValorOpcion());
								parent1.setValorOpcion(actividad.getValorOpcion());
							}
							
							boolean doblePregunta=false;
							String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
							String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
							for (String actividades : actividadesDoblePreguntaArray) {							
								if(actividad.getCatalogoCIUU().getCodigo().equals(actividades))
								{
									doblePregunta=true;
									break;
								}
							}
							boolean alcantarillado=false;
							String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
							String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
							for (String actividades : actividadesAlcantarilladoArray) {
								if(actividad.getCatalogoCIUU().getCodigo().equals(actividades))
								{
									alcantarillado=true;
									break;
								}
							}
							boolean galapagos=false;
							String actividadGalapagos= Constantes.getActividadesGalapagos();			
							String[] actividadesGalapagosArray = actividadGalapagos.split(",");				
							for (String actividades : actividadesGalapagosArray) {
								if(actividad.getCatalogoCIUU().getCodigo().equals(actividades))
								{
									galapagos=true;
								}					
							}
							
							if(doblePregunta)
							{
								if(actividad.getFinanciadoBancoDesarrollo()!=null)//para recuperar cuando es false
								{
									bancoEstado1=subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
									bancoEstado1.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
								}

								//SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades().getId());
								SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
								parent1=parent;
								parent1.setValorOpcion(actividad.getPotabilizacionAgua());
								if(actividad.getPotabilizacionAgua())
									subActividad1=actividad.getSubActividad();

							} else if (actividad.getIdActividadFinanciadoBancoEstado() != null) {
								SubActividades subActividadBancoEstado = subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
								subActividadBancoEstado.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());

								bancoEstado1 = subActividadBancoEstado;
								parent1 = subActividad1;
								parent1.setValorOpcion(actividad.getValorOpcion());
								
								if(actividad.getSubActividad()!=null && actividad.getSubActividad().getEsMultiple()) {
									subActividad1 = actividad.getSubActividad();
								}
							}
							else if(alcantarillado)
							{
								SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
								parent1=parent;
								parent1.setValorOpcion(actividad.getAlcantarilladoMunicipio());
								if(!actividad.getAlcantarilladoMunicipio())
								{
									subActividad1=actividad.getSubActividad();
									subActivadesAlcantarillado1=true;
								}
							}else if(galapagos){
								subActividad1=actividad.getSubActividad();
								parent1.setValorOpcion(actividad.getValorOpcion());
							}
							else if(!actividad.getSubActividad().getEsMultiple())
							{
								subActividad1=actividad.getSubActividad();
								subActividad1.setValorOpcion(actividad.getValorOpcion());
							}else if(actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_PADRE) || actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_HIJO) ) {
								nombrePadreActividad=actividad.getSubActividad().getNombre();
								actividadBloque1=actividad.getSubActividad();
								subActividad1=actividad.getSubActividad();
							}
							else {
								subActividad1=actividad.getSubActividad();
							}
							
							cargarDatosConsideraciones(1);
						} else {
							if (actividad.getIdActividadFinanciadoBancoEstado() != null) {
								SubActividades subActividadBancoEstado = subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
								subActividadBancoEstado.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
								
								subActividad1 = subActividadBancoEstado;
							}
						}
					}					
					
					if(actividad.getCatalogoCIUU().getRequiereViabilidadPngids() != null && actividad.getCatalogoCIUU().getRequiereViabilidadPngids()){
						List<ViabilidadTecnicaProyecto> listaViabilidades = viabilidadTecnicaProyectoFacade.buscarPorProyecto(proyecto.getId());
						if(listaViabilidades.size() > 0)
						codigoOficioGRECI1 = listaViabilidades.get(0).getViabilidadTecnica().getNumeroOficio();
					}
				}else if(actividad.getOrderJerarquia() == 2){
					ciiuComplementaria1 = actividad.getCatalogoCIUU();
					ciiu2=actividad;
					ciiu2(ciiuComplementaria1, false);
					if(actividad.getGenetico()!=null && actividad.getGenetico())
					{
						List<DocumentosCOA> listaDocumentos;
						try {
							listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu2.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO,"ProyectoLicenciaCuaCiuu");
							if (listaDocumentos.size() > 0) {
								documentoGeneticoCiiu2 = listaDocumentos.get(0);
							}
						} catch (CmisAlfrescoException e) {
						}
					}
					
					if(ciiuComplementaria1.getSaneamiento()) {
						List<DocumentosCOA> listaDocumentos;
						try {
							listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu2.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_SANEAMIENTO,"ProyectoLicenciaCuaCiuu");
							if (listaDocumentos.size() > 0) {
								documentoSenaguaCiiu2 = listaDocumentos.get(0);
							}
						} catch (CmisAlfrescoException e) {
						}
					}
					
					if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria1)) {
						if(listaSubActividades2.size() > 0) {
							subActividadesCiiuBean.buscarSubActividadesCiiu(ciiu2, listaSubActividades2, 2);
							//buscar residuos
							residuosActividadesCiiuBean.buscarResiduosActividades(actividad, 2);
						}
					}
					else
					{
						List<ProyectoCoaCiuuSubActividades> cargarSubActividades=proyectoCoaCiuuSubActividadesFacade.cargarSubActividades(actividad);
						requiereTablaResiduo2=false;
						if(cargarSubActividades.size()>0)
						{	
							ArrayList<Integer> list = new ArrayList<Integer>();
							for(ProyectoCoaCiuuSubActividades subSelecionada: cargarSubActividades)
							{
								if(subSelecionada.getRespuesta()!=null && !subSelecionada.getRespuesta())
								{
									parent2=subSelecionada.getSubActividad();
									parent2.setValorOpcion(subSelecionada.getRespuesta());
									subActividad2.setValorOpcion(subSelecionada.getRespuesta());
								}
								else if(subSelecionada.getRespuesta()!=null && subSelecionada.getRespuesta())
								{
									parent2=subSelecionada.getSubActividad();
									parent2.setValorOpcion(subSelecionada.getRespuesta());
									subActividad2.setValorOpcion(subSelecionada.getRespuesta());
									listaSubActividades2 = subActividadesFacade.actividadHijosPorRespuestaPadre(parent2);
								}
								else
								{
									list.add(subSelecionada.getSubActividad().getId());
									listaSubActividadSeleccionad2.add(subSelecionada.getSubActividad());
								}
								if(!requiereTablaResiduo2)
									requiereTablaResiduo2=subSelecionada.getSubActividad().getRequiereIngresoResiduos()==null?false:subSelecionada.getSubActividad().getRequiereIngresoResiduos();
							}
							if(list.size()>1)
							{							
								idSubactividades2 = list.toArray(new Integer[list.size()]);
								combinacion2=actividad.getCombinacionSubActividades().getCombinaciones();
							}
							else
								idSubactividades2 = list.toArray(new Integer[list.size()]);

							residuosActividadesCiiuBean.buscarResiduosActividades(actividad, 2);
						}
						else if(actividad.getSubActividad()!=null)
						{
							if(actividad.getCatalogoCIUU().getLibreAprovechamiento()) {
								subActividad2 = actividad.getSubActividad();
								parent2.setValorOpcion(subActividad2.getPadreVerdadero());							

								getHijosSubActividad(parent2, 2);

								if(!actividad.getSubActividad().getEsMultiple())
									subActividad2.setValorOpcion(actividad.getValorOpcion());
								continue;
							}
							
							if(actividad.getCatalogoCIUU().getRequiereViabilidadPngids() && actividad.getCatalogoCIUU().getTipoPregunta() == null){
								subActividad1.setValorOpcion(actividad.getValorOpcion());
								parent1.setValorOpcion(actividad.getValorOpcion());								
							}

							boolean doblePregunta=false;
							String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
							String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
							for (String actividades : actividadesDoblePreguntaArray) {							
								if(actividad.getCatalogoCIUU().getCodigo().equals(actividades))
								{
									doblePregunta=true;
									break;
								}
							}
							boolean alcantarillado=false;
							String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
							String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
							for (String actividades : actividadesAlcantarilladoArray) {
								if(actividad.getCatalogoCIUU().getCodigo().equals(actividades))
								{
									alcantarillado=true;
									break;
								}
							}
							if(doblePregunta)
							{
								if(actividad.getFinanciadoBancoDesarrollo()!=null)//para recuperar cuando es false
								{
									bancoEstado2=subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
									bancoEstado2.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
								}

								//							SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades().getId());
								SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
								parent2=parent;
								parent2.setValorOpcion(actividad.getPotabilizacionAgua());
								if(actividad.getPotabilizacionAgua())
									subActividad2=actividad.getSubActividad();

							} else if (actividad.getIdActividadFinanciadoBancoEstado() != null) {
								SubActividades subActividadBancoEstado = subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
								subActividadBancoEstado.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());

								bancoEstado2 = subActividadBancoEstado;
								parent2 = subActividad2;
								parent2.setValorOpcion(actividad.getValorOpcion());
								
								if(actividad.getSubActividad()!=null && actividad.getSubActividad().getEsMultiple()) {
									subActividad2 = actividad.getSubActividad();
								}
							}
							else if(alcantarillado)
							{
								SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
								parent2=parent;
								parent2.setValorOpcion(actividad.getAlcantarilladoMunicipio());
								if(!actividad.getAlcantarilladoMunicipio())
								{
									subActividad2=actividad.getSubActividad();
									subActivadesAlcantarillado2=true;
								}
							}
							else if(!actividad.getSubActividad().getEsMultiple())
							{
								subActividad2=actividad.getSubActividad();
								subActividad2.setValorOpcion(actividad.getValorOpcion());
							}else if(actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_PADRE) || actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_HIJO) ) {
								nombrePadreActividad=actividad.getSubActividad().getNombre();
								actividadBloque2=actividad.getSubActividad();
								subActividad2=actividad.getSubActividad();
							}
							else {
								subActividad2=actividad.getSubActividad();
							}
							
							cargarDatosConsideraciones(2);
						} else {
							if (actividad.getIdActividadFinanciadoBancoEstado() != null) {
								SubActividades subActividadBancoEstado = subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
								subActividadBancoEstado.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
								
								subActividad2 = subActividadBancoEstado;
							}
						}
					}
					
					if(actividad.getCatalogoCIUU().getRequiereViabilidadPngids() != null && actividad.getCatalogoCIUU().getRequiereViabilidadPngids()){
						List<ViabilidadTecnicaProyecto> listaViabilidades = viabilidadTecnicaProyectoFacade.buscarPorProyecto(proyecto.getId());
						if(listaViabilidades.size() > 0)
						codigoOficioGRECI2 = listaViabilidades.get(0).getViabilidadTecnica().getNumeroOficio();
					}
				}else if(actividad.getOrderJerarquia() == 3){
					ciiuComplementaria2 = actividad.getCatalogoCIUU();
					ciiu3=actividad;
					ciiu3(ciiuComplementaria2, false);
					if(actividad.getGenetico()!=null && actividad.getGenetico())
					{
						List<DocumentosCOA> listaDocumentos;
						try {
							listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu3.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO,"ProyectoLicenciaCuaCiuu");
							if (listaDocumentos.size() > 0) {
								documentoGeneticoCiiu3 = listaDocumentos.get(0);
							}
						} catch (CmisAlfrescoException e) {
						}
					}
					
					if(ciiuComplementaria2.getSaneamiento()) {
						List<DocumentosCOA> listaDocumentos;
						try {
							listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu3.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_SANEAMIENTO,"ProyectoLicenciaCuaCiuu");
							if (listaDocumentos.size() > 0) {
								documentoSenaguaCiiu3 = listaDocumentos.get(0);
							}
						} catch (CmisAlfrescoException e) {
						}
					}
					if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria2)) {
						if(listaSubActividades3.size() > 0) {
							subActividadesCiiuBean.buscarSubActividadesCiiu(ciiu3, listaSubActividades3, 3);
							//buscar residuos
							residuosActividadesCiiuBean.buscarResiduosActividades(actividad, 3);
						}
					}
					else
					{
						List<ProyectoCoaCiuuSubActividades> cargarSubActividades=proyectoCoaCiuuSubActividadesFacade.cargarSubActividades(actividad);
						requiereTablaResiduo3=false;
						if(cargarSubActividades.size()>0)
						{	
							ArrayList<Integer> list = new ArrayList<Integer>();
							for(ProyectoCoaCiuuSubActividades subSelecionada: cargarSubActividades)
							{
								if(subSelecionada.getRespuesta()!=null && !subSelecionada.getRespuesta())
								{
									parent3=subSelecionada.getSubActividad();
									parent3.setValorOpcion(subSelecionada.getRespuesta());
									subActividad3.setValorOpcion(subSelecionada.getRespuesta());
								}
								else if(subSelecionada.getRespuesta()!=null && subSelecionada.getRespuesta())
								{
									parent3=subSelecionada.getSubActividad();
									parent3.setValorOpcion(subSelecionada.getRespuesta());
									subActividad3.setValorOpcion(subSelecionada.getRespuesta());
									listaSubActividades3 = subActividadesFacade.actividadHijosPorRespuestaPadre(parent3);
								}
								else
								{
									list.add(subSelecionada.getSubActividad().getId());
									listaSubActividadSeleccionad3.add(subSelecionada.getSubActividad());
								}
								if(!requiereTablaResiduo3)
									requiereTablaResiduo3=subSelecionada.getSubActividad().getRequiereIngresoResiduos()==null?false:subSelecionada.getSubActividad().getRequiereIngresoResiduos();
							}
							if(list.size()>1)
							{							
								idSubactividades3 = list.toArray(new Integer[list.size()]);
								combinacion3=actividad.getCombinacionSubActividades().getCombinaciones();
							}
							else
								idSubactividades3 = list.toArray(new Integer[list.size()]);

							residuosActividadesCiiuBean.buscarResiduosActividades(actividad, 3);
						}
						else if(actividad.getSubActividad()!=null)
						{
							if(actividad.getCatalogoCIUU().getLibreAprovechamiento()) {
								subActividad3 = actividad.getSubActividad();
								parent3.setValorOpcion(subActividad3.getPadreVerdadero());							

								getHijosSubActividad(parent3, 3);

								if(!actividad.getSubActividad().getEsMultiple())
									subActividad3.setValorOpcion(actividad.getValorOpcion());
								continue;
							}
							
							if(actividad.getCatalogoCIUU().getRequiereViabilidadPngids() && actividad.getCatalogoCIUU().getTipoPregunta() == null){
								subActividad1.setValorOpcion(actividad.getValorOpcion());
								parent1.setValorOpcion(actividad.getValorOpcion());
							}

							boolean doblePregunta=false;
							String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
							String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
							for (String actividades : actividadesDoblePreguntaArray) {							
								if(actividad.getCatalogoCIUU().getCodigo().equals(actividades))
								{
									doblePregunta=true;
								}
							}
							boolean alcantarillado=false;
							String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
							String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
							for (String actividades : actividadesAlcantarilladoArray) {
								if(actividad.getCatalogoCIUU().getCodigo().equals(actividades))
								{
									alcantarillado=true;
									break;
								}
							}
							if(doblePregunta)
							{
								if(actividad.getFinanciadoBancoDesarrollo()!=null)//para recuperar cuando es false
								{
									bancoEstado3=subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
									bancoEstado3.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
								}

								//							SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades().getId());
								SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
								parent3=parent;
								parent3.setValorOpcion(actividad.getPotabilizacionAgua());
								if(actividad.getPotabilizacionAgua())
									subActividad3=actividad.getSubActividad();

							} else if (actividad.getIdActividadFinanciadoBancoEstado() != null) {
								SubActividades subActividadBancoEstado = subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
								subActividadBancoEstado.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());

								bancoEstado3 = subActividadBancoEstado;
								parent3 = subActividad3;
								parent3.setValorOpcion(actividad.getValorOpcion());
								
								if(actividad.getSubActividad()!=null && actividad.getSubActividad().getEsMultiple()) {
									subActividad3 = actividad.getSubActividad();
								}
							}
							else if(alcantarillado)
							{
								SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
								parent3=parent;
								parent3.setValorOpcion(actividad.getAlcantarilladoMunicipio());
								if(!actividad.getAlcantarilladoMunicipio())
								{
									subActividad3=actividad.getSubActividad();
									subActivadesAlcantarillado3=true;
								}
							}
							else if(!actividad.getSubActividad().getEsMultiple())
							{
								subActividad3=actividad.getSubActividad();
								subActividad3.setValorOpcion(actividad.getValorOpcion());
							}
							else if(actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_PADRE) || actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_HIJO) ) {
								nombrePadreActividad=actividad.getSubActividad().getNombre();
								actividadBloque3=actividad.getSubActividad();
								subActividad3=actividad.getSubActividad();
							}
							else {
								subActividad3=actividad.getSubActividad();
							}
							
							cargarDatosConsideraciones(3);
						} else {
							if (actividad.getIdActividadFinanciadoBancoEstado() != null) {
								SubActividades subActividadBancoEstado = subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
								subActividadBancoEstado.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
								
								subActividad3 = subActividadBancoEstado;
							}
						}
					}
					if(actividad.getCatalogoCIUU().getRequiereViabilidadPngids() != null && actividad.getCatalogoCIUU().getRequiereViabilidadPngids()){
						List<ViabilidadTecnicaProyecto> listaViabilidades = viabilidadTecnicaProyectoFacade.buscarPorProyecto(proyecto.getId());
						if(listaViabilidades.size() > 0)
						codigoOficioGRECI3 = listaViabilidades.get(0).getViabilidadTecnica().getNumeroOficio();
					}
				}
			}
			
			ciiuArearesponsable=proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
			if(ciiuArearesponsable != null && ciiuArearesponsable.getCatalogoCIUU().getBloques())
				esActividadExtractiva=true;
			
			recuperarAutorizacionesPorSector();
			recuperarDocumentosViabilidadPngids();
			recuperarDocumentoCamaroneras();
			
			List<CriterioMagnitud> listaCriterioMagnitud =  new ArrayList<CriterioMagnitud>();
			listaCriterioMagnitud = criterioMagnitudFacade.buscarCriterioMagnitudPorProyecto(proyecto);
			
			if(listaCriterioMagnitud != null && !listaCriterioMagnitud.isEmpty()){
				for(CriterioMagnitud criterioMag : listaCriterioMagnitud){
					VariableCriterio variableCriterio = new VariableCriterio();
					variableCriterio = variableCriterioFacade.buscarVariableCriterioPorId(criterioMag.getVariableCriterio());
					if(variableCriterio.getNivelMagnitud().getId() == 1){							
						valorMagnitud1 = criterioMag.getValorMagnitud();
					}else if(variableCriterio.getNivelMagnitud().getId() == 2){
						valorMagnitud2 = criterioMag.getValorMagnitud();
					}else{
						valorMagnitud3 = criterioMag.getValorMagnitud();
					}
				}					
			}
			
			ValorCalculo valoresFormula = valorCalculoFacade.formulaProyecto(proyecto);
			Integer valorCapacidadSocial = valoresFormula==null?0:valoresFormula.getValorCapacidadSocial();
			Integer valorCapacidadBiofisica = valoresFormula==null?0:valoresFormula.getValorCapacidadBiofisica();
			coordenadasRcoaBean.setValorCapacidadSocial(valorCapacidadSocial);
			coordenadasRcoaBean.setValorCapacidadBiofisica(valorCapacidadBiofisica);
				
//			valorCapacidadSocial = valoresFormula.getValorCapacidadSocial();				
//			valorCapacidadBiofisica = valoresFormula.getValorCapacidadBiofisica();
			
			
			if(proyecto.getAltoImpacto())
			{
				List<DocumentosCOA> listaDocumentos;
				try {
					listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_ALTO_IMPACTO,"ProyectoLicenciaCoa");
					if (listaDocumentos.size() > 0) {
						documentoAltoImpacto = listaDocumentos.get(0);
					}
				} catch (CmisAlfrescoException e) {
				}
			}
			
			if(detalleInterseccionProyectoAmbientalFacade.zonaIntangible(proyecto))
			{
				coordenadasRcoaBean.setEstadoZonaIntangible(true);
				List<DocumentosCOA> listaDocumentosSector;
				try {
					listaDocumentosSector = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_SECTORIAL,"ProyectoLicenciaCoa");
					if (listaDocumentosSector.size() > 0) {
						documentoDocSectorial = listaDocumentosSector.get(0);
					}
				} catch (CmisAlfrescoException e) {
				}
			}
			
			if(detalleInterseccionProyectoAmbientalFacade.zonaFrontera(proyecto))
			{
				coordenadasRcoaBean.setEstadoFrontera(true);
				List<DocumentosCOA> listaDocumentosFrontera;
				try {
					listaDocumentosFrontera = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_FRONTERA,"ProyectoLicenciaCoa");
					if (listaDocumentosFrontera.size() > 0) {
						documentoDocFrontera = listaDocumentosFrontera.get(0);
					}
				} catch (CmisAlfrescoException e) {
				}
			}
			estadoSustanciasQuimicas=false;
			for(GestionarProductosQuimicosProyectoAmbiental lista:gestionarProductosQuimicosProyectoAmbientalFacade.listaSustanciasQuimicas(proyecto))
			{
				sustanciaQuimicaSeleccionada.add(lista.getSustanciaquimica());
				if(lista.getSustanciaquimica().getControlSustancia()!=null && lista.getSustanciaquimica().getControlSustancia()) {
					esControlSustancia=true;
					estadoSustanciasQuimicas=true;
				}
						
			}
			
			for(GestionarProductosQuimicosProyectoAmbiental listat:gestionarProductosQuimicosProyectoAmbientalFacade.listaSustanciasQuimicasTransporta(proyecto))
			{
				sustanciaQuimicaSeleccionadaTransporta.add(listat.getSustanciaquimica());
				if(listat.getSustanciaquimica().getControlSustancia()!=null && listat.getSustanciaquimica().getControlSustancia()) {
					esControlSustancia=true;
					estadoSustanciasQuimicas=true;
				}
						
			}
			/**
			 * Buscar sustancias ingresadas (otros), fabrica y transporte
			 * */
	
			for(GestionarProductosQuimicosProyectoAmbiental listaO:gestionarProductosQuimicosProyectoAmbientalFacade.listaSustanciasQuimicasOtros(proyecto) ) {
				tblSustanciaOtros=true;
				SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtros = new SustanciaQuimicaPeligrosa();
				sustanciaQuimicaSeleccionadaOtros.setDescripcion(listaO.getOtraSustancia());
				sustanciaQuimicaSeleccionadaOtros.setId(Constantes.ID_SUSTANCIA_OTROS);
				listaSustanciaQuimicaSeleccionadaOtros.add(sustanciaQuimicaSeleccionadaOtros);	
			}
				
					
			for(GestionarProductosQuimicosProyectoAmbiental listaOt:gestionarProductosQuimicosProyectoAmbientalFacade.listaSustanciasQuimicasOtrosTransporta(proyecto)) {
				tblSustanciaOtrosTransporta=true;
				SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtrosTransporta = new SustanciaQuimicaPeligrosa();
				sustanciaQuimicaSeleccionadaOtrosTransporta.setDescripcion(listaOt.getOtraSustancia());
				sustanciaQuimicaSeleccionadaOtrosTransporta.setId(Constantes.ID_SUSTANCIA_OTROS);
				listaSustanciaQuimicaSeleccionadaOtrosTransporta.add(sustanciaQuimicaSeleccionadaOtrosTransporta);
				
			}
			
			/****fin busqueda sustancias (otros)****/
			
			listaConcesionesMineras=proyectoLicenciaAmbientalConcesionesMinerasFacade.cargarConcesiones(proyecto);
			
			if(listaConcesionesMineras != null  && !listaConcesionesMineras.isEmpty()){
				concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
				if(listaConcesionesMineras.size() == 1){
					if(listaConcesionesMineras.get(0).getTieneContrato() != null && listaConcesionesMineras.get(0).getTieneContrato()){
						concesionMinera = listaConcesionesMineras.get(0);
						tieneContratoMineria = true;
						esContratoOperacion = true;
						noEsMineriaArtesanal = false;
					}
				}				
			}
			
			
			Iterator<ProyectoLicenciaAmbientalCoaCiuuBloques> iteratorPB = proyectoLicenciaAmbientalCoaCiuuBloquesFacade.cargarBloques(proyecto).iterator();
			while (iteratorPB.hasNext()) {
				bloquesBean.setBloqueSeleccionado(iteratorPB.next().getBloque());
			}
			
			
			autoridadMAE();
			
			catalogoMayorImportancia();
			
			try{
				CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
				if(oficioCI  != null) {
					List<DocumentosCOA> certificadoInter = documentosFacade.documentoXTablaIdXIdDoc(oficioCI.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO, "CertificadoInterseccionOficioCoa");
					if (certificadoInter.size() > 0) 
						documentoCertificado = certificadoInter.get(0);
				} else
					irFinalizar = false;
				
				List<DocumentosCOA> docsMapas = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA, "ProyectoLicenciaCoa");
				if (docsMapas.size() > 0) 
					documentoMapa = docsMapas.get(0);
				else
					msgError.add("No se ha generado el mapa de intersección, adjunte nuevamente las coordenadas");
				
				if(tramiteEnProceso) {
					if(documentoMapa != null && documentoMapa.getId() != null)
						listaDocumentosEliminar.add(documentoMapa);
					
					if(documentoCertificado != null && documentoCertificado.getId() != null)
						listaDocumentosEliminar.add(documentoCertificado);
				}
			} catch (CmisAlfrescoException e) {
			}
			
			if(proyecto.getAreaResponsable() == null) {
				msgError.add("Ocurrió un error al guardar la información");
			} else if(proyecto.getAreaResponsable().getAreaAbbreviation().equalsIgnoreCase("ND")) {
				areaPendienteAsignar = true;
			}
			
			if(proyecto.getGeneraDesechos()) {
				existeRgdObtenidoAnterior = false;
				esRgdValido = false;
				registroGeneradorSeleccionado = null;
				VinculoProyectoRgdSuia proyectoVinculado = vinculoProyectoRgdSuiaFacade.getProyectoVinculadoRgd(proyecto.getId());
				if(proyectoVinculado != null) {
					esRgdValido = true;
					existeRgdObtenidoAnterior = true;
					
					GeneradorDesechosPeligrosos generador = new GeneradorDesechosPeligrosos();
					try {
						generador = registroGeneradorDesechosFacade.get(proyectoVinculado.getIdRgdSuia());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					registroGeneradorSeleccionado = new GeneradorCustom();
					registroGeneradorSeleccionado.setSourceType(GeneradorCustom.SOURCE_TYPE_SUIA_III);
					registroGeneradorSeleccionado.setId(proyectoVinculado.getIdRgdSuia());
					registroGeneradorSeleccionado.setDocumento(generador.getCodigo());
					
					validarCodigoRgd(true);
				} else {
					List<RegistroGeneradorDesechosProyectosRcoa> listaRgdProyecto = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId());
					if(listaRgdProyecto != null && !listaRgdProyecto.isEmpty()){
						
						List<PermisoRegistroGeneradorDesechos> permisos = permisoRegistroGeneradorDesechosFacade.findByRegistroGenerador(listaRgdProyecto.get(0).getRegistroGeneradorDesechosRcoa().getId());
						
						
						esRgdValido = true;
						existeRgdObtenidoAnterior = true;
						
						registroGeneradorSeleccionado = new GeneradorCustom();
						registroGeneradorSeleccionado.setSourceType(GeneradorCustom.SOURCE_TYPE_RCOA);
						registroGeneradorSeleccionado.setId(listaRgdProyecto.get(0).getRegistroGeneradorDesechosRcoa().getId());
						registroGeneradorSeleccionado.setIdGeneradorProyectoRcoa(listaRgdProyecto.get(0).getId());
						registroGeneradorSeleccionado.setDocumento(permisos.get(0).getNumeroDocumento());
						
						validarCodigoRgd(true);
					}
				}
			}

			if(ciiuArearesponsable != null && Constantes.getActividadesCamaroneras().contains(ciiuArearesponsable.getCatalogoCIUU().getCodigo()) &&  proyecto.getZona_camaronera() != null){
				
				Calendar fechaAcuerdo= Calendar.getInstance();
		    	fechaAcuerdo.set(Calendar.YEAR, 2023);
		    	fechaAcuerdo.set(Calendar.MONTH, 10);
		    	fechaAcuerdo.set(Calendar.DATE, 7);
		    	
		    	System.out.println(fechaAcuerdo.getTime());
		    	
		    	Calendar fechaProyecto = Calendar.getInstance();
		    	fechaProyecto.setTime(proyecto.getFechaCreacion());
				
		    	if(!proyecto.getEstadoAsignacionArea().equals(3) && (fechaProyecto.after(fechaAcuerdo) || fechaProyecto.equals(fechaAcuerdo))){
		    		areaResponsable();
		    	}
			}	
		}
		
	}
	
	public boolean validarServicios()
	{
		boolean estado=false;
		try {			
			wsMapas = new GenerarMapaWSService(new URL(Constantes.getGenerarMapaWS()));				
			estado=true;
		} catch (WebServiceException e) {
			estado=false;
			e.printStackTrace();
			System.out.println("Servicio no disponible ---> "+Constantes.getGenerarMapaWS());			
		} catch (MalformedURLException e) {
			estado=false;
		}
		return estado;
	}
	
	public String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return "["+sb.toString()+"]";
    }

    public JSONArray readJsonFromUrl(String url) throws IOException {        
        InputStream is = new URL(url).openStream();
        JSONArray json = null;
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            json = new JSONArray(jsonText);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return json;
    }
	
	public TipoForma getTipoForma(Integer id) {
        for (TipoForma tf : tiposFormas) {
            if (tf.getId()==id)
                return tf;
        }
        return null;
    }
	
	public String onFlowProcess(FlowEvent event) throws Exception {
		List<String> msg= new ArrayList<String>();
//		valida el paso siguiente para proyectos de camaronera		
		if (validarNextSetp != null) {
			if(event.getNewStep().equals("paso2") && (validarNextSetp == false) 
					&& (catalogoCamaronera_ciuu1 == true || catalogoCamaronera_ciuu2 == true || catalogoCamaronera_ciuu3 == true)) {			
				JsfUtil.addMessageError(mensajeNotificacionCamaroneras);			
				limpiarCamposModificarProyecto();
				limpiarCiiu1();
				limpiarCiiu2();
				limpiarCiiu3();
				limpiarNotificacion();
				RequestContext.getCurrentInstance().execute("resetPageScroll()");
				validarNextSetp = true;
				return "paso1";
			}
			if (event.getNewStep().equals("paso2") && (catalogoCamaronera_ciuu1 == true || catalogoCamaronera_ciuu2 == true || catalogoCamaronera_ciuu3 == true) 
					&& validarNextSetp == true && coordenadasRcoaBean.getIntersecaConCamaronera()== false) {					
				JsfUtil.addMessageError(mensajeNotificacionCamaroneras);			
				limpiarCamposModificarProyecto();
				limpiarCiiu1();
				limpiarCiiu2();
				limpiarCiiu3();
				limpiarNotificacion();
				RequestContext.getCurrentInstance().execute("resetPageScroll()");
				validarNextSetp = true;
				return "paso1"; //se activa el primer paso
			}
		}	
		mostarMensaje=false;
		/*Integer interseca = coordenadasRcoaBean.getListaNombresCapasInterseca().size();
		if ((event.getNewStep().equals("paso2") && ((bloquearCiiu1) || (bloquearCiiu2) || (bloquearCiiu3)) && interseca==0)) {				
			limpiarCamposModificarProyecto();
			limpiarCiiu1();
			limpiarCiiu2();
			limpiarCiiu3();
			limpiarNotificacion();
			RequestContext.getCurrentInstance().execute("resetPageScroll()");
			RequestContext context = RequestContext.getCurrentInstance();
			mostarMensaje=true;
			bloquearGad = false;
			bloquearCiiu1 = false;
			bloquearCiiu2 = false;
			bloquearCiiu3 = false;
			context.execute("PF('dlgBloqueoGad').show();");
			return "paso1";
		}*/
		
		if(event.getOldStep().equals("paso3")) {
			//cuando se regresa desde el paso3 Finalizar para modificar los datos 
			limpiarCamposModificarProyecto();
			
			RequestContext.getCurrentInstance().execute("resetPageScroll()");
			return "paso1"; //se activa el primer paso
		}
		if (event.getNewStep().equals("paso2") ) {
			if(coordenadasRcoaBean.getCoordenadasGeograficas().size()==0)
				msg.add("Ingrese coordenadas del área geográfica");
			if(coordenadasRcoaBean.getCoordinatesWrappers().size()==0)
				msg.add("Ingrese coordenadas del área implantación");
			if(coordenadasRcoaBean.getUbicacionesSeleccionadas().size()==0 && coordenadasRcoaBean.getCoordinatesWrappers().size()>0)
				msg.add("No se ha establecido la ubicación del proyecto obra o actividad, comuníquese con mesa de ayuda");
			if(ubicacionRepetida==1)
				msg.add("Parroquia se encuentra repetida");
			if(ciiuPrincipal.getId()==null)
				msg.add("Seleccione la actividad principal del CIIU");
			if(ciiu1.getGenetico()!=null && ciiu1.getGenetico() && documentoGeneticoCiiu1==null)
				msg.add("Adjuntar la resolución otorgada por la Comisión Nacional de Bioseguridad CONABIO, Actividad Principal");
			if(ciiu2.getGenetico()!=null && ciiu2.getGenetico() && documentoGeneticoCiiu2==null)
				msg.add("Adjuntar la resolución otorgada por la Comisión Nacional de Bioseguridad CONABIO, Actividad Complementaria 1");
			if(ciiu3.getGenetico()!=null && ciiu3.getGenetico() && documentoGeneticoCiiu3==null)
				msg.add("Adjuntar la resolución otorgada por la Comisión Nacional de Bioseguridad CONABIO, Actividad Complementaria 2");
			if(coordenadasRcoaBean.isEstadoZonaIntangible() && documentoDocSectorial==null)
				msg.add("Adjuntar autorizaciones o pronunciamientos sectoriales");
//			if(estadoFrontera && documentoDocFrontera==null)
//				msg.add("Adjuntar autorización o pronunciamiento (Ministerio de Defensa)");		
			
			if(nombreSector.equals("MINERÍA") && listaConcesionesMineras.size()==0){				
				if(esContratoOperacion){
					msg.add("Realizar la validación de campo Código minero o contrato de operación");
				}else{
					msg.add("Agregar la o las concesiones minera");
				}				
			}
			
			if(nombreSector.equals("HIDROCARBUROS") && actividadPrimaria.getBloques() && bloquesBean.getBloquesSeleccionados().size()==0)
				msg.add("Seleccionar minimo un bloque");
			
//			if(ciiuPrincipal.getSaneamiento() != null && ciiuPrincipal.getSaneamiento() && documentoSenaguaCiiu1 == null)
//				msg.add("Adjuntar la autorización de SENAGUA, Actividad Principal");
//			if(ciiuComplementaria1.getSaneamiento() != null && ciiuComplementaria1.getSaneamiento() && documentoSenaguaCiiu2 == null)
//				msg.add("Adjuntar la autorización de SENAGUA, Actividad Complementaria 1");
//			if(ciiuComplementaria2.getSaneamiento() != null && ciiuComplementaria2.getSaneamiento() && documentoSenaguaCiiu3 == null)
//				msg.add("Adjuntar la autorización de SENAGUA, Actividad Complementaria 2");
			
			if(esCiiu1HidrocarburoMineriaElectrico && documentoPorSectorCiiu1 == null)
				msg.add("Adjuntar la resolución de asignación de bloque o campo, la autorización de operación o factibilidad o título minero emitida por la autoridad sectorial de hidrocarburos y minería, según corresponda, Actividad Principal");
			if(esCiiu2HidrocarburoMineriaElectrico && documentoPorSectorCiiu2 == null)
				msg.add("Adjuntar la resolución de asignación de bloque o campo, la autorización de operación o factibilidad o título minero emitida por la autoridad sectorial de hidrocarburos y minería, según corresponda, Actividad Complementaria 1");
			if(esCiiu3HidrocarburoMineriaElectrico && documentoPorSectorCiiu3 == null)
				msg.add("Adjuntar la resolución de asignación de bloque o campo, la autorización de operación o factibilidad o título minero emitida por la autoridad sectorial de hidrocarburos y minería, según corresponda, Actividad Complementaria 2");
			
			catalogoMayorImportancia();
			bloqueoCamposGestionTransporte = false;
			if(actividadPrimaria.getScoutDrilling()!=null && proyecto.getConcesionMinera()!=null)
			{
				if(actividadPrimaria.getScoutDrilling() && proyecto.getConcesionMinera() && listaConcesionesMineras.size() > 5)
					msg.add("El límite máximo de concesiones es 5");

				//las actividades de scout drilling inician obligatorio el proceso de RGD
				if(actividadPrimaria.getScoutDrilling())
					proyecto.setGeneraDesechos(true);
				else if(proyecto.getId() == null)
					proyecto.setGeneraDesechos(null);
			} else {
				if(subActividadesCiiuBean.esActividadRecupercionMateriales(actividadPrimaria)) {
					subActividadesCiiuBean.recuperarConsideracionesPorSubActividades(actividadPrimaria);
					if(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte() == null) {
						msg.add("Revise que todas las opciones para la actividad estén seleccionadas");
					}
					if(subActividadesCiiuBean.getCombinacionSubActividadesProcesos() != null) {
						proyecto.setGestionDesechos(subActividadesCiiuBean.getCombinacionSubActividadesProcesos().getRequiereGestionResiduosDesechos());
						proyecto.setTransportaSustanciasQuimicas(subActividadesCiiuBean.getCombinacionSubActividadesProcesos().getRequiereTransporteSustanciasQuimicas());
						bloqueoCamposGestionTransporte = true;
					} else {
						proyecto.setGestionDesechos(null);
						proyecto.setTransportaSustanciasQuimicas(null);
					}
				}
			}
			if(actividadSubYCombi.getSubActividades()!=null || actividadSubYCombi.getCombinacionSubActividades()!=null)
			{
				Boolean gestion = null, transporteSustancias=null;
				if(actividadSubYCombi.getSubActividades()!=null)
				{
					gestion=actividadSubYCombi.getSubActividades().getRequiereGestionResiduosDesechos();
					transporteSustancias=actividadSubYCombi.getSubActividades().getRequiereTransporteSustanciasQuimicas();
				}
				if(actividadSubYCombi.getCombinacionSubActividades()!=null)
				{
					gestion=actividadSubYCombi.getCombinacionSubActividades().getRequiereGestionResiduosDesechos();
					transporteSustancias=actividadSubYCombi.getCombinacionSubActividades().getRequiereTransporteSustanciasQuimicas();
				}
				if(gestion!=null || transporteSustancias!=null)
					bloqueoCamposGestionTransporte = true;
				
				proyecto.setGestionDesechos(gestion);
				proyecto.setTransportaSustanciasQuimicas(transporteSustancias);
			}
			String actividadesBloqueoGestionTransporte = Constantes.getActividadesBloquearGestionTransporte();				
			List<String> actividadesBloqueoGestionTransporteArray = Arrays.asList(actividadesBloqueoGestionTransporte.split(","));
			if(actividadSubYCombi.getCatalogo() != null) {
				if(actividadesBloqueoGestionTransporteArray.contains(actividadSubYCombi.getCatalogo().getCodigo()))
				{	
					proyecto.setGestionDesechos(false);
					proyecto.setTransportaSustanciasQuimicas(false);
					bloqueoCamposGestionTransporte = true;
				}
			}
			
			
			if(requiereTablaResiduo1 && residuosActividadesCiiuBean.getListaCiiuResiduos1().size()==0)
				msg.add("Ingrese mínimo una identificación de residuos o desechos no peligrosos o especiales no peligrosos");
			if(requiereTablaResiduo2 && residuosActividadesCiiuBean.getListaCiiuResiduos2().size()==0)
				msg.add("Ingrese mínimo una identificación de residuos o desechos no peligrosos o especiales no peligrosos");
			if(requiereTablaResiduo3 && residuosActividadesCiiuBean.getListaCiiuResiduos3().size()==0)
				msg.add("Ingrese mínimo una identificación de residuos o desechos no peligrosos o especiales no peligrosos");
			
			
			if(ciiuPrincipal.getRequiereViabilidadPngids() != null && ciiuPrincipal.getRequiereViabilidadPngids()){
				Boolean validar = validarRequiereIngresoViabilidadTecnica(ciiuPrincipal, parent1, 1);
				if(validar) {
					if(codigoOficioGRECI1.isEmpty()){
						msg.add("Ingrese el código del Oficio de Viabilidad Técnica");
					}else{
						String mensaje = validarCodigoViabilidad(codigoOficioGRECI1, 1, ciiuPrincipal, parent1);
						if(mensaje != null) {
							msg.add(mensaje);
						}
					}
					
					if(documentoViabilidadPngidsCiiu1==null || documentoViabilidadPngidsCiiu1.getContenidoDocumento() == null)
						msg.add("Adjuntar el oficio de aprobación de viabilidad técnica emitido por el Proyecto Gestión de Residuos Sólidos y Economía Circular Inclusiva GRECI");
				}
			}
			if(ciiuComplementaria1 != null && ciiuComplementaria1.getRequiereViabilidadPngids() != null && ciiuComplementaria1.getRequiereViabilidadPngids()){
				Boolean validar = validarRequiereIngresoViabilidadTecnica(ciiuComplementaria1, parent2, 2);
				if(validar) {
					if(codigoOficioGRECI2.isEmpty()){
						msg.add("Ingrese el código del Oficio de Viabilidad Técnica");
					}else{
						String mensaje = validarCodigoViabilidad(codigoOficioGRECI2, 2, ciiuComplementaria1, parent2);
						if(mensaje != null) {
							msg.add(mensaje);
						}
					}
					
					if(documentoViabilidadPngidsCiiu2==null || documentoViabilidadPngidsCiiu2.getContenidoDocumento() == null)
						msg.add("Adjuntar el oficio de aprobación de viabilidad técnica emitido por el Proyecto Gestión de Residuos Sólidos y Economía Circular Inclusiva GRECI");
				}
			}
			if(ciiuComplementaria2 != null &&  ciiuComplementaria2.getRequiereViabilidadPngids() != null && ciiuComplementaria2.getRequiereViabilidadPngids()){
				Boolean validar = validarRequiereIngresoViabilidadTecnica(ciiuComplementaria2, parent3, 3);
				
				if(validar) {
					if(codigoOficioGRECI3.isEmpty()){
						msg.add("Ingrese el código del Oficio de Viabilidad Técnica");
					}else{
						String mensaje = validarCodigoViabilidad(codigoOficioGRECI3, 3, ciiuComplementaria2, parent3);
						if(mensaje != null) {
							msg.add(mensaje);
						}
					}
					
					if(documentoViabilidadPngidsCiiu3==null || documentoViabilidadPngidsCiiu3.getContenidoDocumento() == null)
						msg.add("Adjuntar el oficio de aprobación de viabilidad técnica emitido por el Proyecto Gestión de Residuos Sólidos y Economía Circular Inclusiva GRECI");
				}
			}
			
			if (catalogoCamaronera_ciuu1 == true || catalogoCamaronera_ciuu2 == true || catalogoCamaronera_ciuu3 == true) {
				
				if(zona.equals("")){
					msg.add("Seleccione la zona en la cuál se encuentra su proyecto");
				}else if(!esValidacionZonaMixta && zona.equals("MIXTA")){
					msg.add("Debe realizar validación de los polígonos por seleccionar zona Mixta");
				}else if(!coordenadasRcoaBean.getPoligonosContiguos() && zona.equals("MIXTA")){
					msg.add("Los polígonos ingresados no son contiguos");
				}
				
//				if (proyecto.getConcesionCamaronera() == null || proyecto.getConcesionCamaronera().isEmpty())
//					msg.add("El acuerdo de la concesión camaronera es requerido");
//				if (documentoDocCamaronera == null 
//						|| (documentoDocCamaronera.getContenidoDocumento() == null && documentoDocCamaronera.getId() == null))
//					msg.add("Adjuntar la Autorización Administrativa o Título de Concesión camaronera");
			}
			
			if(msg.size()>0)
			{
				JsfUtil.addMessageError(msg);
				return event.getOldStep();
			}
			else {
				RequestContext.getCurrentInstance().execute("resetPageScroll()");
				return event.getNewStep();
			}
		}
		
		if (event.getNewStep().equals("paso3") ) {			
			if(proyecto.getAltoImpacto()!=null && proyecto.getAltoImpacto())
			{
				if(documentoAltoImpacto==null)
					msg.add("Adjuntar documento resolución de alto impacto ambiental o interés nacional");
			}
			
			if(proyecto.getSustanciasQuimicas())
				if(sustanciaQuimicaSeleccionada.size()==0)
					msg.add("Seleccionar una sustancia química");
			
			if(proyecto.getTransportaSustanciasQuimicas())
				if(sustanciaQuimicaSeleccionadaTransporta.size()==0)
					msg.add("Seleccionar una sustancia química");
			
			magnitudMayorImportancia();
			
			if(!calculoWolfram())
			{
				System.out.println("error wolfram");
				msg.add("Error inesperado, comuníquese con mesa de ayuda");
			}
			if(!validarServicios())
			{	
				msg.add("Error inesperado, comuníquese con mesa de ayuda");
			}
			
			if(msg.size()>0)
				{
					JsfUtil.addMessageError(msg);
					return event.getOldStep();
				}
				else
				{
					if(!irFinalizar)
						guardar();
					else {
						RequestContext.getCurrentInstance().update("frmPreliminar:certificadoIntercepcionRcoa");
				        RequestContext context = RequestContext.getCurrentInstance();
			            
				        
				        if(proyecto.getAreaResponsable() != null && proyecto.getAreaResponsable().getAreaAbbreviation().equalsIgnoreCase("ND")) {
				        	if(!getMensajeIntersecaMar().equals("")){
				        		context.execute("PF('dlgAreaPendiente').show();");
				        	}			            	
			            } else {
			            	context.execute("PF('certificadoIntercepcionRcoa').show();");
			            }
					}
					
					RequestContext.getCurrentInstance().execute("removeBtnAtras()");
					return event.getNewStep();
				}
		}
		
        if(skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }
	
	private String validarCodigoViabilidad(String codigo, int ciiu, CatalogoCIUU ciiuC, SubActividades parent){
		int respuesta = validarCodigoViabilidadInterno(codigo, ciiu, ciiuC, parent);
		String mensaje = "";
		
		switch (respuesta) {
		case 3:
			mensaje = null;
			break;
		case 1:
			mensaje = "El código de oficio "+ codigo + " ya fue utilizado en otro trámite.";
			break;
		case 2:
			mensaje = "El código de oficio "+ codigo + " no fue encontrado";
			break;
		case 0:
			mensaje = "Código de oficio no encontrado";
			break;
		case 4:
			mensaje = "El código de oficio "+ codigo + " corresponde a otro operador";
			
			if(ciiuC.getTipoPregunta() != null && ciiuC.getTipoPregunta().equals(4)
				&& parent.getTieneInformacionAdicional() != null && parent.getTieneInformacionAdicional().equals(4)) {
				mensaje = "Estimado usuario/a el oficio ingresado no corresponde a la Empresa Pública que usted representa";
			}
			break;
		case 5:
			mensaje = "El código de oficio "+ codigo + " no corresponde a Celda Emergente";
			break;
		case 6:
			mensaje = "El código de oficio "+ codigo + " no corresponde a Cierres Técnicos o GIRS";
			break;
		case 7:
			mensaje = "El código de oficio "+ codigo + " corresponde a otro operador y ya fue utilizado en otro trámite";
			break;
		case 8:
			mensaje = "El código de oficio "+ codigo + " no corresponde a Escombreras";
			break;
		case 9:
			mensaje = "El código de oficio "+ codigo + " no corresponde a Relleno sanitario";
			break;
		case 10:
			mensaje = "El código de oficio "+ codigo + " no corresponde a Cierres Técnicos";
			break;
		default:
			break;
		}		
		
		return mensaje;
	}
	
	private Integer validarCodigoViabilidadInterno(String codigo, int ciiu, CatalogoCIUU ciiuC, SubActividades parent){
		try {			
			boolean modificacion = false;
			boolean celdaEmergente = false;
			boolean escombreras = false;
			boolean rellenoSanitario = false;
			boolean cierreBotaderos = false;
			
			List<ViabilidadTecnicaProyecto> listaViabilidadesProyecto = new ArrayList<>();
			List<ViabilidadTecnicaProyecto> listaViabilidadesProyecto2 = new ArrayList<>();
			if(ciiuC.getCodigo().compareTo("E3821.01")==0 || ciiuC.getCodigo().compareTo("E3821.01.01") == 0){
				if (parent.getTieneInformacionAdicional() != null) {
					Integer tipoActividad = parent
							.getTieneInformacionAdicional();
					Integer tipoOficioViabilidad = null;

					switch (tipoActividad) {
					case 2:
						rellenoSanitario = true;
						tipoOficioViabilidad = 3;
						break;
					case 3:
						celdaEmergente = true;
						tipoOficioViabilidad = 3;
						break;
					case 4:
						escombreras = true;
						tipoOficioViabilidad = 4;
						break;
					case 5:
						cierreBotaderos = true;
						tipoOficioViabilidad = 1;
						break;
					default:
						celdaEmergente = false;
						tipoOficioViabilidad = 3;
						break;
					}

					if (proyecto.getId() != null) {
						listaViabilidadesProyecto = viabilidadTecnicaProyectoFacade.buscarPorCodigoOficioProyectoTipoExiste(codigo, proyecto.getId(), tipoOficioViabilidad);
						if (listaViabilidadesProyecto != null
								&& !listaViabilidadesProyecto.isEmpty()) {
							modificacion = true;
						}
					} else {
						modificacion = false;
					}

				} else {
					celdaEmergente = false;
					if (proyecto.getId() != null) {
						listaViabilidadesProyecto = viabilidadTecnicaProyectoFacade.buscarPorCodigoOficioProyectoTipoExiste(codigo, proyecto.getId(), 3);
						if (listaViabilidadesProyecto != null
								&& !listaViabilidadesProyecto.isEmpty()) {
							modificacion = true;
						}
					} else {
						modificacion = false;
					}
				}
	
			}else{
				celdaEmergente = false;
				if(proyecto.getId() != null){		
					listaViabilidadesProyecto = viabilidadTecnicaProyectoFacade.buscarPorCodigoOficioProyectoTipoExiste(codigo, proyecto.getId(), 1);
					listaViabilidadesProyecto2 = viabilidadTecnicaProyectoFacade.buscarPorCodigoOficioProyectoTipoExiste(codigo, proyecto.getId(), 2);					
					
					if((listaViabilidadesProyecto != null && !listaViabilidadesProyecto.isEmpty()) || 
							(listaViabilidadesProyecto2 != null && !listaViabilidadesProyecto2.isEmpty())){
						modificacion = true;
					}				
				}else{
					modificacion = false;
				}				
			}					
						
			if(!modificacion){	
				
				List<ViabilidadTecnicaProyecto> listaAux = viabilidadTecnicaProyectoFacade.buscarPorCodigoOficioExiste(codigo);
				boolean desvincular = false;
				
				if(listaAux != null && !listaAux.isEmpty()){
					if(!listaAux.get(0).getProyecto().getEstado()){
						desvincular = true;
					}else{
						//viabilidad
						if(listaAux.get(0).getProyecto().getTieneViabilidadFavorable() != null && !listaAux.get(0).getProyecto().getTieneViabilidadFavorable()){
							desvincular = true;
						}
						//estuido 
						
						desvincular = viabilidadTecnicaProyectoFacade.validarArchivoProyecto(listaAux.get(0).getProyecto());
					}
				}
				
				if(desvincular){
					ViabilidadTecnicaProyecto viabilidadP = listaAux.get(0);
					viabilidadP.setEstado(false);
					viabilidadTecnicaProyectoFacade.guardar(viabilidadP, JsfUtil.getLoggedUser());
				}
				
				List<ViabilidadTecnicaProyecto> lista = viabilidadTecnicaProyectoFacade.buscarPorCodigoOficioExiste(codigo);
				if(lista != null && !lista.isEmpty()){
					if(lista.get(0).getViabilidadTecnica().getRuc().equals(JsfUtil.getLoggedUser().getNombre())){
						return 1; //el código del oficio fue utilizado en otro proyecto.
					}else{
						return 7;
					}					
				}else{
					List<ViabilidadTecnica> listaViabilidades = new ArrayList<>();
					
					List<Integer> tipos = new ArrayList<>();

					if(celdaEmergente){
						tipos.add(2);
					} else if(escombreras){
						tipos.add(4);
					} else if(rellenoSanitario){
						tipos.add(3);
					} else if(cierreBotaderos){
						tipos.add(1);
					} else{
						tipos.add(1);
						tipos.add(3);
					}
					
					listaViabilidades = viabilidadTecnicaFacade.buscarPorCodigoUsuarioTipo(codigo, JsfUtil.getLoggedUser(), tipos);
					if(rellenoSanitario && listaViabilidades.size() > 0 
							&& (listaViabilidades.get(0).getEsRellenoSanitario() == null || !listaViabilidades.get(0).getEsRellenoSanitario())) {
						listaViabilidades = new ArrayList<>();
					}
					
					if(listaViabilidades == null || listaViabilidades.isEmpty()){
						List<ViabilidadTecnica> listaExisteCodigo = viabilidadTecnicaFacade.buscarPorCodigo(codigo);
						if(listaExisteCodigo != null && !listaExisteCodigo.isEmpty()){
							if(listaExisteCodigo.get(0).getRuc().equals(JsfUtil.getLoggedUser().getNombre())){
								if(celdaEmergente)
									return 5;
								else if(escombreras) {
									return 8;
								} else if(rellenoSanitario) {
										return 9;
								} else if(cierreBotaderos) {
									return 10;
								} else
									return 6;
							}else{
								return 4;
							}							
						}else{
							return 2; //el codigo del oficio no existe
						}						
					}else{
						if(ciiu == 1){
							viabilidad1 = listaViabilidades.get(0);
						}else if(ciiu == 2){
							viabilidad2 = listaViabilidades.get(0);
						}else if(ciiu == 3){
							viabilidad3 = listaViabilidades.get(0);
						}
					}
				}
			}else{
				if(ciiu == 1){
					viabilidad1 = listaViabilidadesProyecto.get(0).getViabilidadTecnica();
				}else if(ciiu == 2){
					viabilidad2 = listaViabilidadesProyecto.get(0).getViabilidadTecnica();
				}else if(ciiu == 3){
					viabilidad3 = listaViabilidadesProyecto.get(0).getViabilidadTecnica();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 3;		
	}
	
	
	public TipoPoblacion getTipoPoblacionSimple(Integer id) {
		if (tipoPoblacionSimple != null && !tipoPoblacionSimple.getId().equals(id))
			tipoPoblacionSimple = null;
		return tipoPoblacionSimple == null ? tipoPoblacionSimple = new TipoPoblacion(id) : tipoPoblacionSimple;
	}

	public void handleFileUpload(final FileUploadEvent event) {
		valorProgresoInterseccion = 0;
		
		enviarRgdVinculadoAListaEliminar();
		
		coordenadasRcoaBean.handleFileUpload(event);
		
        verProgresoCapas = true;

        RequestContext.getCurrentInstance().update("frmPreliminar:pnlContentCoordenadas");
        
        RequestContext.getCurrentInstance().execute("PF('prbAjax').setValue(0);");
        
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('prbAjax').start();");
        
	}
	
	public void procesarInterseccionCoordenadas() {
		coordenadasRcoaBean.procesarIntersecciones();
		proyecto.setConcesionCamaronera(null);
		proyecto.setSuperficie(coordenadasRcoaBean.getSuperficieProyecto());

		if(catalogoCamaronera_ciuu1) {
			parent1 = new SubActividades();
			subActividad1 = new SubActividades();
			listaSubActividadesHijas1 = new ArrayList<>();
		} else if(catalogoCamaronera_ciuu2) {
			parent2 = new SubActividades();
			subActividad2 = new SubActividades();
			listaSubActividadesHijas2 = new ArrayList<>();
		} else if(catalogoCamaronera_ciuu3) {
			parent3 = new SubActividades();
			subActividad3 = new SubActividades();
			listaSubActividadesHijas3 = new ArrayList<>();
		}
		
		limpiarCiiu1();
		limpiarCiiu2();
		limpiarCiiu3();
		limpiarNotificacion();
		
		RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('statusDialog').show();");
	}

	public Integer getValorProgresoInterseccion() {
        valorProgresoInterseccion = coordenadasRcoaBean.getProgresoInterseccionCapas();
        return valorProgresoInterseccion;
    }
    
	public void onComplete() {
		
		valorProgresoInterseccion = 0;
		verProgresoCapas = false;
		
		RequestContext.getCurrentInstance().update("frmPreliminar:pnlContentCoordenadas");
		
		RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('adjuntarCoordenadas').hide();");
    }
	
	public void handleFileUploadImple(final FileUploadEvent event) {
		coordenadasRcoaBean.handleFileUploadImple(event);
		proyecto.setSuperficie(coordenadasRcoaBean.getSuperficieProyecto());
	}
    
	public void setUbicacionSeleccionada(UbicacionesGeografica ubicacionesGeografica, int nivel) {
		if (nivel == 3) {
			parroquia = ubicacionesGeografica;
			canton = ubicacionesGeografica.getUbicacionesGeografica();
			provincia = ubicacionesGeografica.getUbicacionesGeografica().getUbicacionesGeografica();
		} else if (nivel == 2) {
			canton = ubicacionesGeografica;
			provincia = ubicacionesGeografica.getUbicacionesGeografica();
			cargarCantones();
			cargarParroquias();
		}
	}
	public void cargarCantones() {
		setCantones(new ArrayList<UbicacionesGeografica>());
		setParroquias(new ArrayList<UbicacionesGeografica>());
		if (getProvincia() != null)
			setCantones(ubicacionGeograficaFacade.getCantonesParroquia(getProvincia()));
		else
			setCantones(new ArrayList<UbicacionesGeografica>());
	}

	public void cargarParroquias() {
		setParroquias(new ArrayList<UbicacionesGeografica>());
		if (getCanton() != null)
			setParroquias(ubicacionGeograficaFacade.getCantonesParroquia(getCanton()));
		else
			setParroquias(new ArrayList<UbicacionesGeografica>());
	}
	
	public void agregarUbicacion() {
		boolean required = false;
		if (provincia == null) {
			JsfUtil.addMessageErrorForComponent("provincia" , JsfUtil.getMessageFromBundle(null,
					"javax.faces.component.UIInput.REQUIRED", "Provincia"));
			required = true;
		}
		if (canton == null) {
			JsfUtil.addMessageErrorForComponent("canton", JsfUtil.getMessageFromBundle(null,
					"javax.faces.component.UIInput.REQUIRED", "Cantón"));
			required = true;
		}
		if (required)
			return;

		UbicacionesGeografica ubicacionesGeografica = getUbicacionSeleccionada();
		if (coordenadasRcoaBean.getUbicacionesSeleccionadas()!=null) {
			coordenadasRcoaBean.getUbicacionesSeleccionadas().add(ubicacionesGeografica);
			}
		else if (!coordenadasRcoaBean.getUbicacionesSeleccionadas().contains(ubicacionesGeografica)) {
			coordenadasRcoaBean.getUbicacionesSeleccionadas().add(ubicacionesGeografica);
			setParroquia(null);
			setCanton(null);
			setProvincia(null);
		} 
		else
			JsfUtil.addMessageError("Esta ubicación ya está seleccionada.");
	}
	
	public UbicacionesGeografica getUbicacionSeleccionada() {
		if (parroquia != null)
			return parroquia;
		if (canton != null)
			return canton;
		return provincia;
	}
	
	public List<UbicacionesGeografica> cargarParroquiasPorPadre(UbicacionesGeografica ubicacionesGeografica) {
		listParroquias = new ArrayList<UbicacionesGeografica>();
		if (ubicacionesGeografica != null){
			listParroquias=(ubicacionGeograficaFacade.getUbicacionPadre(ubicacionesGeografica));
		}
		return listParroquias;
	}
	
	public void adicionarParroquias(String index){
		ubicacionRepetida=0;
		if(!parroquiaSeleccionadas.containsValue(parroquia)){
		parroquiaSeleccionadas.put(index, parroquia);
		}else {
			JsfUtil.addMessageError("Esta ubicación ya está seleccionada.");
			ubicacionRepetida=1;
		}
	}
	
	@Getter
	@Setter
	private boolean esSustanciaOtros;
	
	@Getter
	@Setter
	private boolean esSustanciaOtrosTransporta;
	
	public void sustanciaSelecionada(SustanciaQuimicaPeligrosa sustancia)
	{
		esSustanciaOtros=false;	
		if(!sustanciaQuimicaSeleccionada.contains(sustancia)) {
			if(!sustancia.getDescripcion().equals("Otros")) {
			sustanciaQuimicaSeleccionada.add(sustancia);
			}else {
				esSustanciaOtros=true;
			}
		}else
			JsfUtil.addMessageError("La sustancia ya fue ingresada.");
	
		controlSustancia();
	}
	
	public void eliminarSustancia(SustanciaQuimicaPeligrosa sustancia)
	{
		sustanciaQuimicaSeleccionada.remove(sustancia);
		esMercurio=false;
		fabricaSustancia=false;
			
		controlSustancia();
	}
	
	public void sustanciaSelecionadaTransporta(SustanciaQuimicaPeligrosa sustancia)
	{
		esSustanciaOtrosTransporta=false;
		if(!sustanciaQuimicaSeleccionadaTransporta.contains(sustancia)) {
			if(!sustancia.getDescripcion().equals("Otros")) {
			sustanciaQuimicaSeleccionadaTransporta.add(sustancia);
			}else {
				esSustanciaOtrosTransporta=true;
			}
		}else
			JsfUtil.addMessageError("La sustancia ya fue ingresada.");
			
		
		controlSustancia();
	}
	
	public void eliminarSustanciaTransporta(SustanciaQuimicaPeligrosa sustancia)
	{
		sustanciaQuimicaSeleccionadaTransporta.remove(sustancia);
		controlSustancia();
	}
	
	WolframVO wf1= new WolframVO();
	WolframVO wf2= new WolframVO();
	WolframVO wf3= new WolframVO();	
	WolframVO wfCalculo= new WolframVO();
	
	@Getter
	@Setter
	private boolean subOpciones1,subOpciones2,subOpciones3,esEstacionElectrica=false,mostrarSubBloque=false;
	@Getter
	@Setter
	private Integer tipoOpcion1,tipoOpcion2,tipoOpcion3;
	@Getter
	@Setter
	private List<SubActividades> listaSubActividades1 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividades2 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividades3 = new ArrayList<SubActividades>(),listaSubactividadesProyecto=new  ArrayList<SubActividades>();		
	@Getter
	@Setter
	private SubActividades subActividad1=new SubActividades(), subActividTem=new SubActividades();
	@Getter
	@Setter
	private SubActividades subActividad2=new SubActividades();
	@Getter
	@Setter
	private SubActividades subActividad3=new SubActividades();
	@Getter
	@Setter
	private SubActividades parent1 = new SubActividades();
	@Getter
	@Setter
	private SubActividades parent2 = new SubActividades();
	@Getter
	@Setter
	private SubActividades parent3 = new SubActividades();
	
	@Getter
	@Setter
	private SubActividades bancoEstado1=new SubActividades();
	@Getter
	@Setter
	private SubActividades bancoEstado2=new SubActividades();
	@Getter
	@Setter
	private SubActividades bancoEstado3=new SubActividades();
	
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadesHijas1 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadesHijas2 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadesHijas3 = new ArrayList<SubActividades>();	
	@Getter
	@Setter
	private SubActividades padre1 = new SubActividades();
	@Getter
	@Setter
	private SubActividades padre2 = new SubActividades();
	@Getter
	@Setter
	private SubActividades padre3 = new SubActividades();
	
	@Getter
	@Setter
	private List<SubactividadDto> listaSubActividadesPorBloque1 = new ArrayList<SubactividadDto>();
	@Getter
	@Setter
	private List<SubactividadDto> listaSubActividadesPorBloque2 = new ArrayList<SubactividadDto>();
	@Getter
	@Setter
	private List<SubactividadDto> listaSubActividadesPorBloque3 = new ArrayList<SubactividadDto>();
	
	@Getter
	@Setter
	private Boolean bloquearGad;
	@Getter
	@Setter
	private Boolean bloquearCiiu1 = false;
	@Getter
	@Setter
	private Boolean bloquearCiiu2 = false;
	@Getter
	@Setter
	private Boolean bloquearCiiu3 = false;
	@Getter
	@Setter
	private Boolean mostarMensaje;
	
	public void ciiu1(CatalogoCIUU catalogo, Boolean validarMayorImportancia)
	{	
		bloquearCiiu1 = bloqueoGad(catalogo);
		
		if(validarActividadesRepetidas(1, catalogo.getCodigo(), catalogo.getNombre()))
			return;

		if(ciiuPrincipal != null && ciiuPrincipal.getId() != null && !ciiuPrincipal.getId().equals(catalogo.getId()))
			limpiarCamposActividad(1);
		
		listaSubActividades1= subActividadesFacade.listaXactividad(catalogo);
		if(validarMayorImportancia) {
			limpiarCamposRelacionadosCiiu(1);
		}
		
		catalogoCamaronera_ciuu1 = false;
		
		if(catalogo.getTipoPregunta() != null) {
			getConsideracionesPorActividad(catalogo, 1);
		} else {
		esEstacionElectrica=false;
		if (catalogo.getCodigo().equals("D3510.02") || catalogo.getCodigo().equals("D3510.02.01")) {
			esEstacionElectrica=true;
			tipoOpcion1 = 11;
			subOpciones1=true;
			//subActividad1=listaSubActividades1.get(0);
			consideracionesBloques(catalogo);
		}
		
		if(listaSubActividades1.size()>0 && esEstacionElectrica==false)
		{			
			subOpciones1=true;
			if(subActividadesCiiuBean.esActividadRecupercionMateriales(catalogo)) {
				tipoOpcion1 = 6;
				listaSubActividades1 = subActividadesFacade.actividadPrincipalConHijos(catalogo);
			}else if(catalogo.getLibreAprovechamiento()) {
				tipoOpcion1 = 5;
				parent1 = listaSubActividades1.get(0);
				listaSubActividades1 = new ArrayList<SubActividades>();
			}else if(listaSubActividades1.get(0).getTipoPregunta()!=null &&(listaSubActividades1.get(0).getTipoPregunta().intValue()==7 ||listaSubActividades1.get(0).getTipoPregunta().intValue()==8)){
				tipoOpcion1=listaSubActividades1.get(0).getTipoPregunta().intValue();				
				subActividad1=listaSubActividades1.get(0);
				parent1 = listaSubActividades1.get(0);
			} else if(listaSubActividades1.get(0).getTipoPregunta()!=null &&(listaSubActividades1.get(0).getTipoPregunta().intValue()==9)) 
			{
				tipoOpcion1=9;
			} else if(listaSubActividades1.get(0).getTipoPregunta()!=null &&(listaSubActividades1.get(0).getTipoPregunta().intValue()==12)) {
				tipoOpcion1=12;
				
				List<SubActividades> listaSubActividadesAux = new ArrayList<>();
				listaSubActividadesAux.addAll(listaSubActividades1);
				for (SubActividades b : listaSubActividadesAux) {
					if (b.getEsMultiple() != null && !b.getEsMultiple()) {
						bancoEstado1 = b;
						
						listaSubActividades1.remove(b);
					}
				}
			}
			else if(subActividadesCiiuBean.esActividadOperacionRellenosSanitarios(catalogo)){				
				tipoOpcion1=21;
				bancoEstado1 = listaSubActividades1.get(0);
				subActividad1=listaSubActividades1.get(1);
				parent1 = listaSubActividades1.get(1);
			}
			else {
				listaSubActividades1= subActividadesFacade.listaTodoXactividad(catalogo);
				if(listaSubActividades1.size()>0 && listaSubActividades1.size()==1)
				{
					tipoOpcion1=1;
					subActividad1=listaSubActividades1.get(0);

					validarIngresoViabilidaTecnica(catalogo, 1);
				}
				else if(listaSubActividades1.size()>0)
				{	
					boolean doblePregunta=false;
					
					if(subActividadesCiiuBean.esActividadRecupercionMateriales(catalogo)) {
						tipoOpcion1 = 6;
						listaSubActividades1 = subActividadesFacade.actividadPrincipalConHijos(catalogo);
					} else if(catalogo.getLibreAprovechamiento()) {
						tipoOpcion1 = 5;
						parent1 = listaSubActividades1.get(0);
						listaSubActividades1 = new ArrayList<SubActividades>();
					}else {
						String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
						for (String actividad : actividadesDoblePreguntaArray) {
							for(SubActividades subA: listaSubActividades1)
							{
								if(subA.getCatalogoCIUU().getCodigo().equals(actividad))
								{
									tipoOpcion1=3;
									doblePregunta=true;
									if(subA.getFinanciadoBancoEstado()!=null && subA.getFinanciadoBancoEstado())
										bancoEstado1=subA;
									else
									{
										if(subA.getSubActividades()!=null)
										{
											listaSubActividades1 = new ArrayList<SubActividades>();
											parent1=subActividadesFacade.actividadParent(subA.getSubActividades().getId());
											listaSubActividades1=subActividadesFacade.actividadHijos(parent1.getId(),subA.getCatalogoCIUU());
										}
									}
								}
							}
						}
						if(!doblePregunta)
						{
							tipoOpcion1=2;
						}
						boolean alcantarillado=false;
						String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
						String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
						for (String actividades : actividadesAlcantarilladoArray) {
							if (catalogo.getCodigo().equals(actividades)){
								alcantarillado=true;
								subActivadesAlcantarillado1=false;
								for(SubActividades subA: listaSubActividades1)
								{
									if(subA.getSubActividades()!=null)
									{
										listaSubActividades1 = new ArrayList<SubActividades>();
										parent1=subActividadesFacade.actividadParent(subA.getSubActividades().getId());
										listaSubActividades1=subActividadesFacade.actividadHijos(parent1.getId(),subA.getCatalogoCIUU());
										break;
									}							
								}
								break;
							}
						}
						if(alcantarillado)
						{
							tipoOpcion1=4;
						}
						
						boolean galapagos=false;
						String actividadGalapagos= Constantes.getActividadesGalapagos();				
						String[] actividadGalapagosArray = actividadGalapagos.split(",");				
						for (String actividades : actividadGalapagosArray) {
							if (catalogo.getCodigo().equals(actividades)){
								galapagos=true;
								subActivadesGalapagos1=false;
								for(SubActividades subA: listaSubActividades1)
								{
									if(subA.getSubActividades()!=null)
									{
										listaSubActividades1 = new ArrayList<SubActividades>();
										parent1=subActividadesFacade.actividadParent(subA.getSubActividades().getId());
										listaSubActividades1=subActividadesFacade.actividadHijos(parent1.getId(),subA.getCatalogoCIUU());
										subActivadesGalapagos1=true;
										break;
									}							
								}
								break;
							}
						}
						if(galapagos)
						{
							tipoOpcion1=45;
						}
					}
				}
			}
		}
		}
		
		ciiuPrincipal=catalogo;
		wf1.setCatalago(ciiuPrincipal);
		wf1.setNivel5(ciiuPrincipal.getCodigo().substring(0,7));
		wf1.setNivel3(ciiuPrincipal.getCodigo().substring(0,4));
		wf1.setImportancia(catalogoCIUUFacade.catalogoCiuu(ciiuPrincipal.getCodigo().substring(0,7)).getImportanciaRelativa());
		wf1.setPuesto(1);
		
		if(validarMayorImportancia)
			catalogoMayorImportancia();//se habilita porque es necesario recuperar el sector y cual es la actividad principal
		
		esCiiu1HidrocarburoMineriaElectrico = false;
		if(ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null) {
			String nombreSector = ciiuPrincipal.getTipoSector().getNombre().toUpperCase();
			if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){// || nombreSector.equals("ELÉCTRICO")
				esCiiu1HidrocarburoMineriaElectrico = true;
				if(coordenadasRcoaBean.isEstadoZonaIntangibleAux())
					coordenadasRcoaBean.setEstadoZonaIntangible(true);
			}
		}
		validarSectorActividades();
		
		if(ciiuPrincipal != null && ciiuPrincipal.getId() != null) {
			rbVerificaEncuentraActividadCIIU = true;
			actividadEncontrada = true;
			notificacionActividadCIIU.setMensaje(null);
		}
		
		/**
		 * solo para minería
		 */
		
		if(ciiuComplementaria1 == null && ciiuComplementaria2 == null){
			mostrarPoseeContrato = false;
			tieneContratoMineria = null;
			esContratoOperacion = null;
			concesionMinera = null;
		}else if(ciiuComplementaria1 != null && ciiuComplementaria1.getId() == null && ciiuComplementaria2 == null){
			mostrarPoseeContrato = false;
			tieneContratoMineria = null;
			esContratoOperacion = null;
			concesionMinera = null;
		}else if(ciiuComplementaria1 != null && ciiuComplementaria1.getId() == null && ciiuComplementaria2 != null && ciiuComplementaria2.getId() == null){
			mostrarPoseeContrato = false;
			tieneContratoMineria = null;
			esContratoOperacion = null;
			concesionMinera = null;
		}
		
		if(ciiuComplementaria1 != null && ciiuComplementaria1.getTipoSector() != null){
			String nombreSector = ciiuComplementaria1.getTipoSector().getNombre().toUpperCase();
			if(!nombreSector.equals("MINERÍA")){
				if(ciiuComplementaria2 != null && ciiuComplementaria2.getTipoSector() != null){
					String nombreSector2 = ciiuComplementaria2.getTipoSector().getNombre().toUpperCase();
					if(!nombreSector2.equals("MINERÍA")){
						mostrarPoseeContrato = false;
						tieneContratoMineria = null;
						esContratoOperacion = null;
						concesionMinera = null;
					}else{
						if(!subOpciones3){
							mostrarPoseeContrato = false;
							tieneContratoMineria = null;
							esContratoOperacion = null;
							concesionMinera = null;
						}
					}
				}else if(ciiuComplementaria2 == null){
					mostrarPoseeContrato = false;
					tieneContratoMineria = null;
					esContratoOperacion = null;
					concesionMinera = null;
				}
			}else{
				if(!subOpciones2){
					mostrarPoseeContrato = false;
					tieneContratoMineria = null;
					esContratoOperacion = null;
					concesionMinera = null;
				}
			}
		}
		
		if(ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null) {
			String nombreSector = ciiuPrincipal.getTipoSector().getNombre().toUpperCase();
			if(nombreSector.equals("MINERÍA")){
				if(subOpciones1){
					esContratoOperacion = false;
					noEsMineriaArtesanal = false;
				}else{
					noEsMineriaArtesanal = true;
				}				
			}			
		}		
		
		RequestContext.getCurrentInstance().update("frmPreliminar:idDocAutorizacionesDectoriales"); 
		RequestContext.getCurrentInstance().update("frmPreliminar:pnlSector"); 
		RequestContext.getCurrentInstance().update("frmPreliminar:concesionesMinerasGeneralContainer");
		RequestContext.getCurrentInstance().update("frmPreliminar:pnlNotificacion");
	}
	
	public void ciiu2(CatalogoCIUU catalogo, Boolean validarMayorImportancia)
	{
		
		bloquearCiiu2 = bloqueoGad(catalogo);
		
		if(validarActividadesRepetidas(2, catalogo.getCodigo(), catalogo.getNombre()))
			return;

		if(ciiuComplementaria1 != null && ciiuComplementaria1.getId() != null && !ciiuComplementaria1.getId().equals(catalogo.getId()))
			limpiarCamposActividad(2);		
		
		listaSubActividades2= subActividadesFacade.listaXactividad(catalogo);
		if(validarMayorImportancia) {
			limpiarCamposRelacionadosCiiu(2);
		}

		catalogoCamaronera_ciuu2 = false;

		if(catalogo.getTipoPregunta() != null) {
			getConsideracionesPorActividad(catalogo, 2);
		} else {
		esEstacionElectrica=false;
		if (catalogo.getCodigo().equals("D3510.02") || catalogo.getCodigo().equals("D3510.02.01")) {
			esEstacionElectrica=true;
			tipoOpcion2 = 11;
			subOpciones2=true;
			consideracionesBloques(catalogo);
		}
		if(listaSubActividades2.size()>0 && !esEstacionElectrica)
		{
			subOpciones2=true;
			if(subActividadesCiiuBean.esActividadRecupercionMateriales(catalogo)) {
				tipoOpcion2 = 6;
				listaSubActividades2 = subActividadesFacade.actividadPrincipalConHijos(catalogo);
			} else if(catalogo.getLibreAprovechamiento()) {
				tipoOpcion2 = 5;
				parent2 = listaSubActividades2.get(0);
				listaSubActividades2 = new ArrayList<SubActividades>();
			}else if(subActividadesCiiuBean.esActividadGalapagos(catalogo)) {
				tipoOpcion2 = 5;
				parent2 = listaSubActividades2.get(0);
				listaSubActividades2 = new ArrayList<SubActividades>();
			}else if(listaSubActividades2.get(0).getTipoPregunta()!=null &&(listaSubActividades2.get(0).getTipoPregunta().intValue()==7 ||listaSubActividades2.get(0).getTipoPregunta().intValue()==8))
			{
				tipoOpcion2=listaSubActividades2.get(0).getTipoPregunta().intValue();
				subActividad2=listaSubActividades2.get(0);
				parent2 = listaSubActividades2.get(0);				
			} else if(listaSubActividades2.get(0).getTipoPregunta()!=null &&(listaSubActividades2.get(0).getTipoPregunta().intValue()==9))
			{
				tipoOpcion2=9;
			} else if(listaSubActividades2.get(0).getTipoPregunta()!=null && (listaSubActividades2.get(0).getTipoPregunta().intValue()==12)) {
				tipoOpcion2=12;
				
				List<SubActividades> listaSubActividadesAux = new ArrayList<>();
				listaSubActividadesAux.addAll(listaSubActividades2);
				for (SubActividades b : listaSubActividadesAux) {
					if (b.getEsMultiple() != null && !b.getEsMultiple()) {
						bancoEstado2 = b;
						
						listaSubActividades2.remove(b);
					}
				}
			}
			else if(subActividadesCiiuBean.esActividadOperacionRellenosSanitarios(catalogo)){
				tipoOpcion2 = 21;
				bancoEstado2 = listaSubActividades2.get(0);
				subActividad2 = listaSubActividades2.get(1);
				parent2 = listaSubActividades2.get(1);
			}else {
				listaSubActividades2= subActividadesFacade.listaTodoXactividad(catalogo);
				if(listaSubActividades2.size()>0 && listaSubActividades2.size()==1)
				{
					tipoOpcion2=1;
					subActividad2=listaSubActividades2.get(0);

					validarIngresoViabilidaTecnica(catalogo, 2);
				} 
				else if(listaSubActividades2.size()>0)
				{	
					boolean doblePregunta=false;

					if(subActividadesCiiuBean.esActividadRecupercionMateriales(catalogo)) {
						tipoOpcion2 = 6;
						listaSubActividades2 = subActividadesFacade.actividadPrincipalConHijos(catalogo);
					} else if(catalogo.getLibreAprovechamiento()) {
						tipoOpcion2 = 5;
						parent2 = listaSubActividades2.get(0);
						listaSubActividades2 = new ArrayList<SubActividades>();
					} else if(subActividadesCiiuBean.esActividadGalapagos(catalogo)) {
						tipoOpcion2 = 5;
						parent2 = listaSubActividades2.get(0);
						listaSubActividades2 = new ArrayList<SubActividades>();
					}else {
						String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
						for (String actividad : actividadesDoblePreguntaArray) {
							for(SubActividades subA: listaSubActividades2)
							{
								if(subA.getCatalogoCIUU().getCodigo().equals(actividad))
								{
									tipoOpcion2=3;
									doblePregunta=true;
									if(subA.getFinanciadoBancoEstado()!=null && subA.getFinanciadoBancoEstado())
										bancoEstado2=subA;
									else
									{
										if(subA.getSubActividades()!=null)
										{
											listaSubActividades2 = new ArrayList<SubActividades>();
											parent2=subActividadesFacade.actividadParent(subA.getSubActividades().getId());
											listaSubActividades2=subActividadesFacade.actividadHijos(parent2.getId(),subA.getCatalogoCIUU());
										}
									}
								}
							}
						}
						if(!doblePregunta)
						{
							tipoOpcion2=2;
						}
						boolean alcantarillado=false;
						String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
						String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
						for (String actividades : actividadesAlcantarilladoArray) {
							if (catalogo.getCodigo().equals(actividades)){
								alcantarillado=true;
								subActivadesAlcantarillado2=false;
								for(SubActividades subA: listaSubActividades2)
								{
									if(subA.getSubActividades()!=null)
									{
										listaSubActividades2 = new ArrayList<SubActividades>();
										parent2=subActividadesFacade.actividadParent(subA.getSubActividades().getId());
										listaSubActividades2=subActividadesFacade.actividadHijos(parent2.getId(),subA.getCatalogoCIUU());
										break;
									}							
								}
								break;
							}
						}
						if(alcantarillado)
						{
							tipoOpcion2=4;
						}
						boolean galapagos=false;
						String actividadGalapagos= Constantes.getActividadesGalapagos();				
						String[] actividadGalapagosArray = actividadGalapagos.split(",");				
						for (String actividades : actividadGalapagosArray) {
							if (catalogo.getCodigo().equals(actividades)){
								galapagos=true;
								subActivadesGalapagos2=false;
								for(SubActividades subA: listaSubActividades2)
								{
									if(subA.getSubActividades()!=null)
									{
										listaSubActividades2 = new ArrayList<SubActividades>();
										parent2=subActividadesFacade.actividadParent(subA.getSubActividades().getId());
										listaSubActividades2=subActividadesFacade.actividadHijos(parent2.getId(),subA.getCatalogoCIUU());
										break;
									}							
								}
								break;
							}
						}
						if(galapagos)
						{
							tipoOpcion2=45;
						}
					}
				}
			}
		}
		}
		
		ciiuComplementaria1=catalogo;
		wf2.setCatalago(ciiuComplementaria1);
		wf2.setNivel5(ciiuComplementaria1.getCodigo().substring(0,7));
		wf2.setNivel3(ciiuComplementaria1.getCodigo().substring(0,4));
		wf2.setImportancia(catalogoCIUUFacade.catalogoCiuu(ciiuComplementaria1.getCodigo().substring(0,7)).getImportanciaRelativa());
		wf2.setPuesto(2);
		
		if(validarMayorImportancia)
			catalogoMayorImportancia();//se habilita porque es necesario recuperar el sector y cual es la actividad principal
		
		esCiiu2HidrocarburoMineriaElectrico = false;
		if(ciiuComplementaria1 != null && ciiuComplementaria1.getTipoSector() != null) {
			String nombreSector = ciiuComplementaria1.getTipoSector().getNombre().toUpperCase();
			if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){ // || nombreSector.equals("ELÉCTRICO")
				esCiiu2HidrocarburoMineriaElectrico = true;
				if(coordenadasRcoaBean.isEstadoZonaIntangibleAux())
					coordenadasRcoaBean.setEstadoZonaIntangible(true);
			}
		}
		validarSectorActividades();
		
		/**
		 * solo para minería
		 */
		if(ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null){
			String nombreSector = ciiuPrincipal.getTipoSector().getNombre().toUpperCase();
			if(!nombreSector.equals("MINERÍA")){
				if(ciiuComplementaria2 != null && ciiuComplementaria2.getTipoSector() != null){
					String nombreSector2 = ciiuComplementaria2.getTipoSector().getNombre().toUpperCase();
					if(!nombreSector2.equals("MINERÍA")){
						mostrarPoseeContrato = false;
						tieneContratoMineria = null;
						esContratoOperacion = null;
						concesionMinera = null;
					}else{
						if(!subOpciones3){
							mostrarPoseeContrato = false;
							tieneContratoMineria = null;
							esContratoOperacion = null;
							concesionMinera = null;
						}
					}
				}else if(ciiuComplementaria2 == null){
					mostrarPoseeContrato = false;
					tieneContratoMineria = null;
					esContratoOperacion = null;
					concesionMinera = null;
				}
			}else{
				if(!subOpciones1){
					mostrarPoseeContrato = false;
					tieneContratoMineria = null;
					esContratoOperacion = null;
					concesionMinera = null;
				}
			}
		}
		
		if(ciiuComplementaria1 != null && ciiuComplementaria1.getTipoSector() != null) {
			String nombreSector = ciiuComplementaria1.getTipoSector().getNombre().toUpperCase();
			if(nombreSector.equals("MINERÍA")){
				if(subOpciones1){
					esContratoOperacion = false;
					noEsMineriaArtesanal = false;
				}else{
					noEsMineriaArtesanal = true;
				}	
			}			
		}			
		
		RequestContext.getCurrentInstance().update("frmPreliminar:concesionesMinerasGeneralContainer");
	}
	
	public void ciiu3(CatalogoCIUU catalogo, Boolean validarMayorImportancia)
	{	
		
		// actividades ciiu para bloqueo
		bloquearCiiu3 = bloqueoGad(catalogo);
		
		if(validarActividadesRepetidas(3, catalogo.getCodigo(), catalogo.getNombre()))
			return;

		if(ciiuComplementaria2 != null && ciiuComplementaria2.getId() != null && !ciiuComplementaria2.getId().equals(catalogo.getId()))
			limpiarCamposActividad(3);
		
		listaSubActividades3= subActividadesFacade.listaXactividad(catalogo);		
		if(validarMayorImportancia) {
			limpiarCamposRelacionadosCiiu(3);
		}

		catalogoCamaronera_ciuu3 = false;
		
		if(catalogo.getTipoPregunta() != null) {
			getConsideracionesPorActividad(catalogo, 3);
		} else {
		esEstacionElectrica=false;
		if (catalogo.getCodigo().equals("D3510.02") || catalogo.getCodigo().equals("D3510.02.01")) {
			esEstacionElectrica=true;
			tipoOpcion3 = 11;
			subOpciones3=true;
			//subActividad1=listaSubActividades1.get(0);
			consideracionesBloques(catalogo);
		}
		if(listaSubActividades3.size()>0 && !esEstacionElectrica)
		{
			subOpciones3=true;
			if(subActividadesCiiuBean.esActividadRecupercionMateriales(catalogo)) {
				tipoOpcion3 = 6;
				listaSubActividades3 = subActividadesFacade.actividadPrincipalConHijos(catalogo);
			} else if(catalogo.getLibreAprovechamiento()) {
				tipoOpcion3 = 5;
				parent3 = listaSubActividades3.get(0);
				listaSubActividades3 = new ArrayList<SubActividades>();
			}else if(subActividadesCiiuBean.esActividadGalapagos(catalogo)) {
				tipoOpcion3 = 5;
				parent3 = listaSubActividades3.get(0);
				listaSubActividades3 = new ArrayList<SubActividades>();
			} else if(listaSubActividades3.get(0).getTipoPregunta()!=null &&(listaSubActividades3.get(0).getTipoPregunta().intValue()==7 ||listaSubActividades3.get(0).getTipoPregunta().intValue()==8))
			{
				tipoOpcion3=listaSubActividades3.get(0).getTipoPregunta().intValue();
				subActividad3=listaSubActividades3.get(0);
				parent3 = listaSubActividades3.get(0);
			} else if(listaSubActividades3.get(0).getTipoPregunta()!=null &&(listaSubActividades3.get(0).getTipoPregunta().intValue()==9))
			{
				tipoOpcion3=9;
			} else if(listaSubActividades3.get(0).getTipoPregunta()!=null && (listaSubActividades3.get(0).getTipoPregunta().intValue()==12)) {
				tipoOpcion3=12;
				
				List<SubActividades> listaSubActividadesAux = new ArrayList<>();
				listaSubActividadesAux.addAll(listaSubActividades3);
				for (SubActividades b : listaSubActividadesAux) {
					if (b.getEsMultiple() != null && !b.getEsMultiple()) {
						bancoEstado3 = b;
						
						listaSubActividades3.remove(b);
					}
				}
			}
			else if(subActividadesCiiuBean.esActividadOperacionRellenosSanitarios(catalogo)){
				tipoOpcion3 = 21;
				bancoEstado3 = listaSubActividades3.get(0);
				subActividad3 = listaSubActividades3.get(1);
				parent3 = listaSubActividades3.get(1);
			}else {
				listaSubActividades3= subActividadesFacade.listaTodoXactividad(catalogo);	
				if(listaSubActividades3.size()>0 &&listaSubActividades3.size()==1)
				{
					tipoOpcion3=1;
					subActividad3=listaSubActividades3.get(0);
					
					validarIngresoViabilidaTecnica(catalogo, 3);
				}
				else if(listaSubActividades3.size()>0)
				{	
					boolean doblePregunta=false;

					if(subActividadesCiiuBean.esActividadRecupercionMateriales(catalogo)) {
						tipoOpcion3 = 6;
						listaSubActividades3 = subActividadesFacade.actividadPrincipalConHijos(catalogo);
					} else if(catalogo.getLibreAprovechamiento()) {
						tipoOpcion3 = 5;
						parent3 = listaSubActividades3.get(0);
						listaSubActividades3 = new ArrayList<SubActividades>();
					} else if(subActividadesCiiuBean.esActividadGalapagos(catalogo)) {
						tipoOpcion3 = 5;
						parent3 = listaSubActividades3.get(0);
						listaSubActividades3 = new ArrayList<SubActividades>();
					}else {
						String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
						for (String actividad : actividadesDoblePreguntaArray) {
							for(SubActividades subA: listaSubActividades3)
							{
								if(subA.getCatalogoCIUU().getCodigo().equals(actividad))
								{
									tipoOpcion3=3;
									doblePregunta=true;
									if(subA.getFinanciadoBancoEstado()!=null && subA.getFinanciadoBancoEstado())
										bancoEstado3=subA;
									else
									{
										if(subA.getSubActividades()!=null)
										{
											listaSubActividades3 = new ArrayList<SubActividades>();
											parent3=subActividadesFacade.actividadParent(subA.getSubActividades().getId());
											listaSubActividades3=subActividadesFacade.actividadHijos(parent3.getId(),subA.getCatalogoCIUU());
										}
									}
								}
							}
						}
						if(!doblePregunta)
						{
							tipoOpcion3=2;
						}
						boolean alcantarillado=false;
						String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
						String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
						for (String actividades : actividadesAlcantarilladoArray) {
							if (catalogo.getCodigo().equals(actividades)){
								alcantarillado=true;
								subActivadesAlcantarillado3=false;
								for(SubActividades subA: listaSubActividades3)
								{
									if(subA.getSubActividades()!=null)
									{
										listaSubActividades3 = new ArrayList<SubActividades>();
										parent3=subActividadesFacade.actividadParent(subA.getSubActividades().getId());
										listaSubActividades3=subActividadesFacade.actividadHijos(parent3.getId(),subA.getCatalogoCIUU());
										break;
									}							
								}
								break;
							}
						}
						if(alcantarillado)
						{
							tipoOpcion3=4;
						}
						boolean galapagos=false;
						String actividadGalapagos= Constantes.getActividadesGalapagos();				
						String[] actividadGalapagosArray = actividadGalapagos.split(",");				
						for (String actividades : actividadGalapagosArray) {
							if (catalogo.getCodigo().equals(actividades)){
								galapagos=true;
								subActivadesGalapagos3=false;
								for(SubActividades subA: listaSubActividades3)
								{
									if(subA.getSubActividades()!=null)
									{
										listaSubActividades3 = new ArrayList<SubActividades>();
										parent1=subActividadesFacade.actividadParent(subA.getSubActividades().getId());
										listaSubActividades3=subActividadesFacade.actividadHijos(parent3.getId(),subA.getCatalogoCIUU());
										break;
									}							
								}
								break;
							}
						}
						if(galapagos)
						{
							tipoOpcion3=45;
						}
					}
				}
			}
		}
		}
		
		ciiuComplementaria2=catalogo;
		wf3.setCatalago(ciiuComplementaria2);
		wf3.setNivel5(ciiuComplementaria2.getCodigo().substring(0,7));
		wf3.setNivel3(ciiuComplementaria2.getCodigo().substring(0,4));
		wf3.setImportancia(catalogoCIUUFacade.catalogoCiuu(ciiuComplementaria2.getCodigo().substring(0,7)).getImportanciaRelativa());
		wf3.setPuesto(3);
		
		if(validarMayorImportancia)
			catalogoMayorImportancia();//se habilita porque es necesario recuperar el sector y cual es la actividad principal
		
		esCiiu3HidrocarburoMineriaElectrico = false;
		if(ciiuComplementaria2 != null && ciiuComplementaria2.getTipoSector() != null) {
			String nombreSector = ciiuComplementaria2.getTipoSector().getNombre().toUpperCase();
			if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){ // || nombreSector.equals("ELÉCTRICO")
				esCiiu3HidrocarburoMineriaElectrico = true;
				if(coordenadasRcoaBean.isEstadoZonaIntangibleAux())
					coordenadasRcoaBean.setEstadoZonaIntangible(true);
			}
		}
		validarSectorActividades();
		
		/**
		 * solo para minería
		 */		
		if(ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null){
			String nombreSector = ciiuPrincipal.getTipoSector().getNombre().toUpperCase();
			if(!nombreSector.equals("MINERÍA")){
				if(ciiuComplementaria1 != null && ciiuComplementaria1.getTipoSector() != null){
					String nombreSector2 = ciiuComplementaria1.getTipoSector().getNombre().toUpperCase();
					if(!nombreSector2.equals("MINERÍA")){
						mostrarPoseeContrato = false;
						tieneContratoMineria = null;
						esContratoOperacion = null;
						concesionMinera = null;
					}else{
						if(!subOpciones2){
							mostrarPoseeContrato = false;
							tieneContratoMineria = null;
							esContratoOperacion = null;
							concesionMinera = null;
						}
					}
				}
			}else{
				if(!subOpciones1){
					mostrarPoseeContrato = false;
					tieneContratoMineria = null;
					esContratoOperacion = null;
					concesionMinera = null;
				}
			}
		}
		
		if(ciiuComplementaria2 != null && ciiuComplementaria2.getTipoSector() != null) {
			String nombreSector = ciiuComplementaria2.getTipoSector().getNombre().toUpperCase();
			if(nombreSector.equals("MINERÍA")){
				if(subOpciones1){
					esContratoOperacion = false;
					noEsMineriaArtesanal = false;
				}else{
					noEsMineriaArtesanal = true;
				}		
			}			
		}
		
		RequestContext.getCurrentInstance().update("frmPreliminar:concesionesMinerasGeneralContainer");
	}
	
	public Boolean validarActividadesRepetidas(Integer seleccion, String codigoSeleccion, String nombreSeleccion) {
		List<String> codigosSeleccionados = new ArrayList<>();
		List<String> nombresSeleccionados = new ArrayList<>();
		
		if(seleccion.equals(1)) {
			if(ciiuComplementaria1 != null && ciiuComplementaria1.getId() != null) {
				codigosSeleccionados.add(ciiuComplementaria1.getCodigo());
				nombresSeleccionados.add(ciiuComplementaria1.getNombre());
			}
			if(ciiuComplementaria2 != null && ciiuComplementaria2.getId() != null) {
				codigosSeleccionados.add(ciiuComplementaria2.getCodigo());
				nombresSeleccionados.add(ciiuComplementaria2.getNombre());
			}
		}
		
		if(seleccion.equals(2)) {
			if(ciiuPrincipal != null && ciiuPrincipal.getId() != null) {
				codigosSeleccionados.add(ciiuPrincipal.getCodigo());
				nombresSeleccionados.add(ciiuPrincipal.getNombre());
			}
			if(ciiuComplementaria2 != null && ciiuComplementaria2.getId() != null) { 
				codigosSeleccionados.add(ciiuComplementaria2.getCodigo());
				nombresSeleccionados.add(ciiuComplementaria2.getNombre());
			}
		}
		
		if(seleccion.equals(3)) {
			if(ciiuPrincipal != null && ciiuPrincipal.getId() != null) {
				codigosSeleccionados.add(ciiuPrincipal.getCodigo());
				nombresSeleccionados.add(ciiuPrincipal.getNombre());
			}
			if(ciiuComplementaria1 != null && ciiuComplementaria1.getId() != null) { 
				codigosSeleccionados.add(ciiuComplementaria1.getCodigo());
				nombresSeleccionados.add(ciiuComplementaria1.getNombre());
			}
		}
		
		for (String codeActividad : codigosSeleccionados) {
			if(codeActividad.equals(codigoSeleccion)) {
				JsfUtil.addMessageError("La actividad ya está seleccionada.");
				return true;
			}
		}
		
		for (String nameActividad : nombresSeleccionados) {
			if(nameActividad.equals(nombreSeleccion)) {
				JsfUtil.addMessageError("La actividad ya está seleccionada.");
				return true;
			}
		}
		
		return false;
	}
	
	public void catalogoMayorImportancia()
	{			
		try
		{	
			msgError= new ArrayList<String>();
			List<CatalogoImportanciaVO> importancias = new ArrayList<CatalogoImportanciaVO>();
			CatalogoImportanciaVO importCatalogo = new CatalogoImportanciaVO();
			Integer tipoPermiso=0;			

			if (ciiuPrincipal != null && ciiuPrincipal.getId() != null) {
				if(ciiuPrincipal.getTipoPregunta() != null) {
					importCatalogo = catalogoMayorImportanciaConsideraciones(1);
					importancias.add(importCatalogo);
				} else {
				if(tipoOpcion1!=null && (tipoOpcion1==7 ||tipoOpcion1==8)) {
					CombinacionSubActividades combinacionSubActividades= new CombinacionSubActividades();
					CatalogoCIUU catalogoCIUU = new CatalogoCIUU();				
					if(tipoOpcion1==7)
					{
						if (idSubactividades1 == null) {
							tipoPermiso=Integer.valueOf(subActividad1.getTipoPermiso());
							catalogoCIUU=subActividad1.getCatalogoCIUU();
							importCatalogo.setSubActividades(subActividad1);
							ciiu1.setSubActividad(subActividad1);
						} else {
							if(idSubactividades1.length==1)
							{
								for (Integer id : idSubactividades1) {							
									for (SubActividades subA : listaSubActividades1) {
										if(subA.getId().intValue()==id.intValue())
										{
											tipoPermiso=Integer.valueOf(subA.getTipoPermiso());
											catalogoCIUU=subA.getCatalogoCIUU();
											importCatalogo.setSubActividades(subA);
											ciiu1.setSubActividad(subA);
										}
									}		
								}
							}
							else
							{
								for (SubActividades subA : listaSubActividades1) {
									combinacionSubActividades=combinacionActividadesFacade.getPorCatalogoCombinacion(subA.getCatalogoCIUU(), combinacion1);
									tipoPermiso=Integer.valueOf(combinacionSubActividades.getTipoPermiso());
									catalogoCIUU=subA.getCatalogoCIUU();
									importCatalogo.setCombinacionSubActividades(combinacionSubActividades);
									ciiu1.setCombinacionSubActividades(combinacionSubActividades);
									break;
								}		
							}						
						}
					}
						
					if(tipoOpcion1==8)
					{	
						if(!parent1.getValorOpcion())
						{
							tipoPermiso=parent1.getOpcionPermisoNo();
							catalogoCIUU=parent1.getCatalogoCIUU();
							importCatalogo.setSubActividades(parent1);
							ciiu1.setSubActividad(parent1);
							ciiu1.setValorOpcion(parent1.getValorOpcion());
							requiereTablaResiduo1=parent1.getRequiereIngresoResiduos()==null?false:parent1.getRequiereIngresoResiduos();
							proyectoSubAct1 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct1.setSubActividad(parent1);
							proyectoSubAct1.setRespuesta(parent1.getValorOpcion());
							listaProyectoCiiuSubActividad1= new ArrayList<ProyectoCoaCiuuSubActividades>();
							listaProyectoCiiuSubActividad1.add(proyectoSubAct1);						
						}					
						else 
						{
							ciiu1.setSubActividad(parent1);
							ciiu1.setValorOpcion(parent1.getValorOpcion());
							proyectoSubAct1 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct1.setSubActividad(parent1);
							proyectoSubAct1.setRespuesta(parent1.getValorOpcion());
							listaProyectoCiiuSubActividad1.add(proyectoSubAct1);
	
							if(idSubactividades1.length==1)
							{
								for (Integer id : idSubactividades1) {							
									for (SubActividades subA : listaSubActividades1) {
										if(subA.getId().intValue()==id.intValue())
										{
											tipoPermiso=Integer.valueOf(subA.getTipoPermiso());
											catalogoCIUU=subA.getCatalogoCIUU();
											importCatalogo.setSubActividades(subA);
											ciiu1.setSubActividad(subA);
											ciiu1.setValorOpcion(null);
										}
									}		
								}
							}
							else
							{
								for (SubActividades subA : listaSubActividades1) {
									combinacionSubActividades=combinacionActividadesFacade.getPorCatalogoCombinacion(subA.getCatalogoCIUU(), combinacion1);
									tipoPermiso=Integer.valueOf(combinacionSubActividades.getTipoPermiso());
									catalogoCIUU=subA.getCatalogoCIUU();
									importCatalogo.setCombinacionSubActividades(combinacionSubActividades);
									ciiu1.setCombinacionSubActividades(combinacionSubActividades);
									break;
								}		
							}						
						}
					}				
					importCatalogo.setCatalogo(catalogoCIUU);
					importCatalogo.setTipoPermiso(tipoPermiso);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(catalogoCIUU.getCodigo().substring(0,7)).getImportanciaRelativa());
					importCatalogo.setWf("wf1");
					importancias.add(importCatalogo);
				}
				else if(subActividad1.getId()!=null || parent1.getId()!=null)
				{	
					importCatalogo = new CatalogoImportanciaVO();
					SubActividades actividad = subActividad1;
	//				---------
					boolean doblePregunta=false;
					String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
					String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");
					for (String actividadD : actividadesDoblePreguntaArray) {
						String acti=subActividad1.getId()==null?parent1.getCatalogoCIUU().getCodigo():subActividad1.getCatalogoCIUU().getCodigo();
						if(acti.equals(actividadD))
						{
							doblePregunta=true;
							break;
						}
					}
					if(doblePregunta)
					{
						if(parent1.getId()!=null && !parent1.getValorOpcion())
						{
							actividad=parent1;
						}
					}
					else
					{					
						if(parent1.getId()!=null && parent1.getValorOpcion() != null && parent1.getValorOpcion() && tipoOpcion1!=45)
						{
							actividad=parent1;
						}
						
						if(parent1.getId()!=null && parent1.getValorOpcion() != null && !parent1.getValorOpcion() && tipoOpcion1==45)
						{
							actividad=parent1;
							if (parent1.getCatalogoCIUU().getCodigo().compareTo("I5510.01") == 0) {
								actividad = subActividad1;
							}
						}						
	
					}				
					if(actividad.getEsMultiple()!=null && actividad.getEsMultiple())
					{
						tipoPermiso=Integer.valueOf(actividad.getTipoPermiso());				
					}
					else
					{	
						if(actividad.getValorOpcion()!=null && actividad.getValorOpcion())
						{
							tipoPermiso=actividad.getOpcionPermisoSi();					
						}
						else
						{						
							tipoPermiso=actividad.getOpcionPermisoNo();
						}
						if(tipoOpcion1==11 && tipoPermiso==null && !actividad.getEsMultiple()) {
							tipoPermiso=actividad.getOpcionPermisoSi();
						}
					}
					
					if(tipoPermiso == null && actividad.getFinanciadoBancoEstado() != null && actividad.getFinanciadoBancoEstado()) {
						importCatalogo = mayorImportanciaPorCatalogo(importCatalogo, 1);
						importancias.add(importCatalogo);
					} else {
						SubActividades parent = subActividadesFacade.actividadParent(actividad.getId());
						importCatalogo.setCatalogo(parent==null?actividad.getCatalogoCIUU():parent.getCatalogoCIUU());
						importCatalogo.setTipoPermiso(tipoPermiso);
						if(parent!=null || actividad.getCatalogoCIUU()!=null) {
						if(actividad.getNombre().equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION)){
							importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent.getCatalogoCIUU().getCodigo().substring(0,7)).getImportanciaRelativa());
							tipoPermiso=actividad.getOpcionPermisoSi();
						}
						else
							importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent==null?actividad.getCatalogoCIUU().getCodigo().substring(0,7):parent.getCatalogoCIUU().getCodigo().substring(0,7)).getImportanciaRelativa());
						}else {
							importCatalogo.setImportancia(0);
						}				
						importCatalogo.setWf("wf1");
						importancias.add(importCatalogo);		
					}
				}
				else
				{
					importCatalogo = mayorImportanciaPorCatalogo(importCatalogo, 1);
					importancias.add(importCatalogo);
				}
				}
			}
			
			if(ciiuComplementaria1 != null && ciiuComplementaria1.getId() != null)
			{
				if(ciiuComplementaria1.getTipoPregunta() != null) {
					importCatalogo = catalogoMayorImportanciaConsideraciones(2);
					importancias.add(importCatalogo);
				} else {
				tipoPermiso=0;
				importCatalogo = new CatalogoImportanciaVO();
				if(tipoOpcion2!=null && (tipoOpcion2==7 ||tipoOpcion2==8))
				{
					CombinacionSubActividades combinacionSubActividades= new CombinacionSubActividades();
					CatalogoCIUU catalogoCIUU = new CatalogoCIUU();				
					if(tipoOpcion2==7)
					{
						if(idSubactividades2.length==1)
						{
							for (Integer id : idSubactividades2) {							
								for (SubActividades subA : listaSubActividades2) {
									if(subA.getId().intValue()==id.intValue())
									{
										tipoPermiso=Integer.valueOf(subA.getTipoPermiso());
										catalogoCIUU=subA.getCatalogoCIUU();
										importCatalogo.setSubActividades(subA);
										ciiu2.setSubActividad(subA);
									}
								}		
							}
						}
						else
						{
							for (SubActividades subA : listaSubActividades2) {
								combinacionSubActividades=combinacionActividadesFacade.getPorCatalogoCombinacion(subA.getCatalogoCIUU(), combinacion2);
								tipoPermiso=Integer.valueOf(combinacionSubActividades.getTipoPermiso());
								catalogoCIUU=subA.getCatalogoCIUU();
								importCatalogo.setCombinacionSubActividades(combinacionSubActividades);
								ciiu2.setCombinacionSubActividades(combinacionSubActividades);
								break;
							}		
						}						
					}
						
					if(tipoOpcion2==8)
					{	
						if(!parent2.getValorOpcion())
						{
							tipoPermiso=parent2.getOpcionPermisoNo();
							catalogoCIUU=parent2.getCatalogoCIUU();
							importCatalogo.setSubActividades(parent2);
							ciiu2.setSubActividad(parent2);
							ciiu2.setValorOpcion(parent2.getValorOpcion());
							requiereTablaResiduo2=parent2.getRequiereIngresoResiduos()==null?false:parent2.getRequiereIngresoResiduos();
							proyectoSubAct2 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct2.setSubActividad(parent2);
							proyectoSubAct2.setRespuesta(parent2.getValorOpcion());
							listaProyectoCiiuSubActividad2= new ArrayList<ProyectoCoaCiuuSubActividades>();
							listaProyectoCiiuSubActividad2.add(proyectoSubAct2);						
						}					
						else 
						{
							ciiu2.setSubActividad(parent2);
							ciiu2.setValorOpcion(parent2.getValorOpcion());
							proyectoSubAct2 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct2.setSubActividad(parent2);
							proyectoSubAct2.setRespuesta(parent2.getValorOpcion());
							listaProyectoCiiuSubActividad2.add(proyectoSubAct2);
							if(idSubactividades2.length==1)
							{
								for (Integer id : idSubactividades2) {							
									for (SubActividades subA : listaSubActividades2) {
										if(subA.getId().intValue()==id.intValue())
										{
											tipoPermiso=Integer.valueOf(subA.getTipoPermiso());
											catalogoCIUU=subA.getCatalogoCIUU();
											importCatalogo.setSubActividades(subA);
											ciiu2.setSubActividad(subA);
											ciiu2.setValorOpcion(null);
										}
									}		
								}
							}
							else
							{
								for (SubActividades subA : listaSubActividades2) {
									combinacionSubActividades=combinacionActividadesFacade.getPorCatalogoCombinacion(subA.getCatalogoCIUU(), combinacion2);
									tipoPermiso=Integer.valueOf(combinacionSubActividades.getTipoPermiso());
									catalogoCIUU=subA.getCatalogoCIUU();
									importCatalogo.setCombinacionSubActividades(combinacionSubActividades);
									ciiu2.setCombinacionSubActividades(combinacionSubActividades);
									break;
								}		
							}						
						}
					}				
					importCatalogo.setCatalogo(catalogoCIUU);
					importCatalogo.setTipoPermiso(tipoPermiso);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(catalogoCIUU.getCodigo().substring(0,7)).getImportanciaRelativa());
					importCatalogo.setWf("wf2");
					importancias.add(importCatalogo);
				}
				else if(subActividad2.getId()!=null || parent2.getId()!=null)
				{	
					importCatalogo = new CatalogoImportanciaVO();
					SubActividades actividad = subActividad2;
//					---------
					boolean doblePregunta=false;
					String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
					String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");
					for (String actividadD : actividadesDoblePreguntaArray) {
						String acti=subActividad2.getId()==null?parent2.getCatalogoCIUU().getCodigo():subActividad2.getCatalogoCIUU().getCodigo();
						if(acti.equals(actividadD))
						{
							doblePregunta=true;
							break;
						}
					}
					if(doblePregunta)
					{
						if(parent2.getId()!=null && !parent2.getValorOpcion())
						{
							actividad=parent2;
						}
					}					
					else
					{					
						if(parent2.getId()!=null && parent2.getValorOpcion() != null && parent2.getValorOpcion() && tipoOpcion2!=45)
						{
							actividad=parent2;
						}
						
						if(parent2.getId()!=null && parent2.getValorOpcion() != null && !parent2.getValorOpcion() && tipoOpcion2==45)
						{
							actividad=parent2;
							if (parent2.getCatalogoCIUU().getCodigo().compareTo("I5510.01") == 0) {
								actividad = subActividad2;
							}
						}						

					}				
					if(actividad.getEsMultiple()!=null && actividad.getEsMultiple())
					{
						tipoPermiso=Integer.valueOf(actividad.getTipoPermiso());				
					}
					else
					{	
						if(actividad.getValorOpcion()!=null && actividad.getValorOpcion())
						{
							tipoPermiso=actividad.getOpcionPermisoSi();					
						}
						else
						{						
							tipoPermiso=actividad.getOpcionPermisoNo();
						}
					}
					
					if(tipoPermiso == null && actividad.getFinanciadoBancoEstado() != null && actividad.getFinanciadoBancoEstado()) {
						importCatalogo = mayorImportanciaPorCatalogo(importCatalogo, 2);
						importancias.add(importCatalogo);
					} else {
						SubActividades parent = subActividadesFacade.actividadParent(actividad.getId());
						if(parent!=null || actividad.getCatalogoCIUU()!=null) {
							importCatalogo.setCatalogo(parent==null?actividad.getCatalogoCIUU():parent.getCatalogoCIUU());
							importCatalogo.setTipoPermiso(tipoPermiso);
						if(actividad.getNombre().equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION)) {
							importCatalogo.setTipoPermiso(actividad.getOpcionPermisoSi());
							importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent.getCatalogoCIUU().getCodigo().substring(0,7)).getImportanciaRelativa());
						}
						else
							importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent==null?actividad.getCatalogoCIUU().getCodigo().substring(0,7):parent.getCatalogoCIUU().getCodigo().substring(0,7)).getImportanciaRelativa());
							importCatalogo.setWf("wf2");
							importancias.add(importCatalogo);
						}									
					}
				}
				else
				{
					importCatalogo = mayorImportanciaPorCatalogo(importCatalogo, 2);
					importancias.add(importCatalogo);
				}
				}
			}
			
			
			if(ciiuComplementaria2 != null && ciiuComplementaria2.getId() != null)
			{
				if(ciiuComplementaria2.getTipoPregunta() != null) {
					importCatalogo = catalogoMayorImportanciaConsideraciones(3);
					importancias.add(importCatalogo);
				} else {
				tipoPermiso=0;
				importCatalogo = new CatalogoImportanciaVO();
				if(tipoOpcion3!=null && (tipoOpcion3==7 ||tipoOpcion3==8))
				{
					CombinacionSubActividades combinacionSubActividades= new CombinacionSubActividades();
					CatalogoCIUU catalogoCIUU = new CatalogoCIUU();				
					if(tipoOpcion3==7)
					{
						if(idSubactividades3.length==1)
						{
							for (Integer id : idSubactividades3) {							
								for (SubActividades subA : listaSubActividades3) {
									if(subA.getId().intValue()==id.intValue())
									{
										tipoPermiso=Integer.valueOf(subA.getTipoPermiso());
										catalogoCIUU=subA.getCatalogoCIUU();
										importCatalogo.setSubActividades(subA);
										ciiu3.setSubActividad(subA);
									}
								}		
							}
						}
						else
						{
							for (SubActividades subA : listaSubActividades3) {
								combinacionSubActividades=combinacionActividadesFacade.getPorCatalogoCombinacion(subA.getCatalogoCIUU(), combinacion3);
								tipoPermiso=Integer.valueOf(combinacionSubActividades.getTipoPermiso());
								catalogoCIUU=subA.getCatalogoCIUU();
								importCatalogo.setCombinacionSubActividades(combinacionSubActividades);
								ciiu3.setCombinacionSubActividades(combinacionSubActividades);
								break;
							}		
						}						
					}
						
					if(tipoOpcion3==8)
					{	
						if(!parent3.getValorOpcion())
						{
							tipoPermiso=parent3.getOpcionPermisoNo();
							catalogoCIUU=parent3.getCatalogoCIUU();
							importCatalogo.setSubActividades(parent3);
							ciiu3.setSubActividad(parent3);
							ciiu3.setValorOpcion(parent3.getValorOpcion());
							requiereTablaResiduo3=parent3.getRequiereIngresoResiduos()==null?false:parent3.getRequiereIngresoResiduos();
							proyectoSubAct3 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct3.setSubActividad(parent3);
							proyectoSubAct3.setRespuesta(parent3.getValorOpcion());
							listaProyectoCiiuSubActividad3= new ArrayList<ProyectoCoaCiuuSubActividades>();
							listaProyectoCiiuSubActividad3.add(proyectoSubAct3);						
						}					
						else 
						{
							ciiu3.setSubActividad(parent3);
							ciiu3.setValorOpcion(parent3.getValorOpcion());
							proyectoSubAct3 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct3.setSubActividad(parent3);
							proyectoSubAct3.setRespuesta(parent3.getValorOpcion());
							listaProyectoCiiuSubActividad3.add(proyectoSubAct3);
							if(idSubactividades3.length==1)
							{
								for (Integer id : idSubactividades3) {							
									for (SubActividades subA : listaSubActividades3) {
										if(subA.getId().intValue()==id.intValue())
										{
											tipoPermiso=Integer.valueOf(subA.getTipoPermiso());
											catalogoCIUU=subA.getCatalogoCIUU();
											importCatalogo.setSubActividades(subA);
											ciiu3.setSubActividad(subA);
											ciiu3.setValorOpcion(null);
										}
									}		
								}
							}
							else
							{
								for (SubActividades subA : listaSubActividades3) {
									combinacionSubActividades=combinacionActividadesFacade.getPorCatalogoCombinacion(subA.getCatalogoCIUU(), combinacion3);
									tipoPermiso=Integer.valueOf(combinacionSubActividades.getTipoPermiso());
									catalogoCIUU=subA.getCatalogoCIUU();
									importCatalogo.setCombinacionSubActividades(combinacionSubActividades);
									ciiu3.setCombinacionSubActividades(combinacionSubActividades);
									break;
								}		
							}						
						}
					}				
					importCatalogo.setCatalogo(catalogoCIUU);
					importCatalogo.setTipoPermiso(tipoPermiso);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(catalogoCIUU.getCodigo().substring(0,7)).getImportanciaRelativa());
					importCatalogo.setWf("wf3");
					importancias.add(importCatalogo);
				}
				else if(subActividad3.getId()!=null || parent3.getId()!=null)
				{	
					importCatalogo = new CatalogoImportanciaVO();
					SubActividades actividad = subActividad3;
//					---------
					boolean doblePregunta=false;
					String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
					String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");
					for (String actividadD : actividadesDoblePreguntaArray) {
						String acti=subActividad3.getId()==null?parent3.getCatalogoCIUU().getCodigo():subActividad3.getCatalogoCIUU().getCodigo();
						if(acti.equals(actividadD))
						{
							doblePregunta=true;
							break;
						}
					}
					if(doblePregunta)
					{
						if(parent3.getId()!=null && !parent3.getValorOpcion())
						{
							actividad=parent3;
						}
					}
					else
					{					
						if(parent3.getId()!=null && parent3.getValorOpcion() != null && parent3.getValorOpcion() && tipoOpcion3!=45)
						{
							actividad=parent3;
						}
						
						if(parent3.getId()!=null && parent3.getValorOpcion() != null && !parent3.getValorOpcion() && tipoOpcion3==45)
						{
							actividad=parent3;
							if (parent3.getCatalogoCIUU().getCodigo().compareTo("I5510.01") == 0) {
								actividad = subActividad3;
							}
						}						

					}				
					if(actividad.getEsMultiple()!=null && actividad.getEsMultiple())
					{
						tipoPermiso=Integer.valueOf(actividad.getTipoPermiso());				
					}
					else
					{	
						if(actividad.getValorOpcion()!=null && actividad.getValorOpcion())
						{
							tipoPermiso=actividad.getOpcionPermisoSi();					
						}
						else
						{						
							tipoPermiso=actividad.getOpcionPermisoNo();
						}
					}
					
					if(tipoPermiso == null && actividad.getFinanciadoBancoEstado() != null && actividad.getFinanciadoBancoEstado()) {
						importCatalogo = mayorImportanciaPorCatalogo(importCatalogo, 3);
						importancias.add(importCatalogo);
					} else {
						SubActividades parent = subActividadesFacade.actividadParent(actividad.getId());
						if(parent!=null || actividad.getCatalogoCIUU()!=null) {
							importCatalogo.setCatalogo(parent==null?actividad.getCatalogoCIUU():parent.getCatalogoCIUU());
							importCatalogo.setTipoPermiso(tipoPermiso);
						if(actividad.getNombre().equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION)) {
							tipoPermiso=actividad.getOpcionPermisoSi();
							importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent.getCatalogoCIUU().getCodigo().substring(0,7)).getImportanciaRelativa());
						}
						else
							importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent==null?actividad.getCatalogoCIUU().getCodigo().substring(0,7):parent.getCatalogoCIUU().getCodigo().substring(0,7)).getImportanciaRelativa());
							importCatalogo.setWf("wf3");
							importancias.add(importCatalogo);
						}
					}
									
				}else
				{
					importCatalogo = mayorImportanciaPorCatalogo(importCatalogo, 3);
					importancias.add(importCatalogo);
				}
				}
			}
			
			Integer categoriaAlta = 0, importanciaAlta = 0;
			CatalogoCIUU catalogoMayorImp = new CatalogoCIUU();
			String wfPuesto="";
			actividadSubYCombi= new CatalogoImportanciaVO();
			for(CatalogoImportanciaVO catalogos: importancias)
			{
//				System.out.println("id:"+catalogos.getCatalogo().getId()+" categoria:"+catalogos.getTipoPermiso()+" Importancia:"+catalogos.getImportancia());
				Integer categoria = (catalogos.getTipoPermiso() == null) ? 0 : catalogos.getTipoPermiso();
				Integer importancia = catalogos.getImportancia();
				if (categoria == categoriaAlta) {
					if (importancia > importanciaAlta) {
						categoriaAlta = catalogos.getTipoPermiso();
						importanciaAlta = catalogos.getImportancia();
						catalogos.getCatalogo().getId();
						catalogoMayorImp=catalogos.getCatalogo();
						wfPuesto=catalogos.getWf();
						actividadSubYCombi.setCatalogo(catalogos.getCatalogo());
						actividadSubYCombi.setSubActividades(catalogos.getSubActividades());
						actividadSubYCombi.setCombinacionSubActividades(catalogos.getCombinacionSubActividades());
					}
				} else if (categoria > categoriaAlta) {
					categoriaAlta = catalogos.getTipoPermiso();
					importanciaAlta = catalogos.getImportancia();
					catalogoMayorImp=catalogos.getCatalogo();
					catalogos.getCatalogo().getId();
					wfPuesto=catalogos.getWf();
					actividadSubYCombi.setCatalogo(catalogos.getCatalogo());
					actividadSubYCombi.setSubActividades(catalogos.getSubActividades());
					actividadSubYCombi.setCombinacionSubActividades(catalogos.getCombinacionSubActividades());
				}
			}
//			System.out.println("categoria Alta:"+categoriaAlta+" id:"+id);
			
			
			if(catalogoMayorImp.getCodigo()!=null) {
				WolframVO wfResul= new WolframVO();
				wfResul.setCatalago(catalogoMayorImp);
				wfResul.setNivel5(catalogoMayorImp.getCodigo().substring(0,7));
				wfResul.setNivel3(catalogoMayorImp.getCodigo().substring(0,4));
				wfResul.setImportancia(catalogoCIUUFacade.catalogoCiuu(catalogoMayorImp.getCodigo().substring(0,7)).getImportanciaRelativa());
				if(wfPuesto.equals("wf1"))
					wfResul.setPuesto(1);
				if(wfPuesto.equals("wf2"))
					wfResul.setPuesto(2);
				if(wfPuesto.equals("wf3"))
					wfResul.setPuesto(3);
				
				wfCalculo=wfResul;
				nombreSector=wfResul.getCatalago().getTipoSector().getNombre().toUpperCase();
				actividadPrimaria=wfResul.getCatalago();
				wfCalculo.setCalculo(true);
				wfCalculo.setCatalago(catalogoCIUUFacade.catalogoCiuu(wfCalculo.getNivel5()));
			}
						
		}
		catch (Exception e) {
			msgError.add("Ocurrió un error al guardar la información");
			e.printStackTrace();
		}
	}
	
	public CatalogoImportanciaVO mayorImportanciaPorCatalogo(CatalogoImportanciaVO importCatalogo, Integer tipo) {
		Integer tipoPermisoActividad = 0;
		switch (tipo) {
		case 1:
			tipoPermisoActividad = importanciaXActiviad(ciiuPrincipal);
			if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuPrincipal) && listaSubActividades1 != null &&   listaSubActividades1.size() > 0) {
				ciiuPrincipal.setSubActividades(listaSubActividades1);
				subActividadesCiiuBean.recuperarConsideracionesPorSubActividades(ciiuPrincipal);
				if(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte() != null)
					tipoPermisoActividad = Integer.valueOf(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte().getTipoPermiso());
			}
			
			importCatalogo.setCatalogo(ciiuPrincipal);
			importCatalogo.setTipoPermiso(tipoPermisoActividad);
			importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(ciiuPrincipal.getCodigo().substring(0,7)).getImportanciaRelativa());
			importCatalogo.setWf("wf1");
			break;
		case 2:
			tipoPermisoActividad = importanciaXActiviad(ciiuComplementaria1);
			if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria1) && listaSubActividades2 != null &&   listaSubActividades2.size() > 0) {
				ciiuComplementaria1.setSubActividades(listaSubActividades2);
				subActividadesCiiuBean.recuperarConsideracionesPorSubActividades(ciiuComplementaria1);
				if(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte() != null)
					tipoPermisoActividad = Integer.valueOf(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte().getTipoPermiso());
			}
			
			importCatalogo.setCatalogo(ciiuComplementaria1);
			importCatalogo.setTipoPermiso(tipoPermisoActividad);
			importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(ciiuComplementaria1.getCodigo().substring(0,7)).getImportanciaRelativa());
			importCatalogo.setWf("wf2");
			break;
		case 3:
			tipoPermisoActividad = importanciaXActiviad(ciiuComplementaria2);
			if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria2) && listaSubActividades3 != null &&   listaSubActividades3.size() > 0) {
				ciiuComplementaria2.setSubActividades(listaSubActividades3);
				subActividadesCiiuBean.recuperarConsideracionesPorSubActividades(ciiuComplementaria2);
				if(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte() != null)
					tipoPermisoActividad = Integer.valueOf(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte().getTipoPermiso());
			}
			
			importCatalogo.setCatalogo(ciiuComplementaria2);
			importCatalogo.setTipoPermiso(tipoPermisoActividad);
			importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(ciiuComplementaria2.getCodigo().substring(0,7)).getImportanciaRelativa());
			importCatalogo.setWf("wf3");
			break;
		default:
			break;
		}
		
		return importCatalogo;
	}
	
	// funcion extraer categorizacion (1-2-3-4)
	public Integer importanciaXActiviad(CatalogoCIUU catalogo)
	{
		String nivel = "0";
		if(catalogo.getTipoPermiso()!=null && catalogo.getTipoPermiso().equals("CERTIFICADO AMBIENTAL"))
		{
			nivel="1";
		}		
		if(catalogo.getTipoPermiso()!=null && catalogo.getTipoPermiso().equals("REGISTRO AMBIENTAL"))
		{
			nivel="2";
		}
		if(catalogo.getTipoPermiso()!=null && catalogo.getTipoPermiso().equals("LICENCIA AMBIENTAL"))
		{
			nivel="3";
		}
		if(catalogo.getTipoImpacto()!=null && catalogo.getTipoImpacto().equals("IMPACTO ALTO"))
		{
			nivel="4";
		}
		return Integer.valueOf(nivel);
	}
	
	public Integer importanciaXActiviad(WolframVO wf, CatalogoCIUU catalogo)
	{
		String importancia = wf.getImportancia().toString();
		String nivel = "0";
		if(catalogo.getTipoPermiso()!=null && catalogo.getTipoPermiso().equals("CERTIFICADO AMBIENTAL"))
		{
			nivel="1";
		}		
		if(catalogo.getTipoPermiso()!=null && catalogo.getTipoPermiso().equals("REGISTRO AMBIENTAL"))
		{
			nivel="2";
		}
		if(catalogo.getTipoPermiso()!=null && catalogo.getTipoPermiso().equals("LICENCIA AMBIENTAL"))
		{
			nivel="3";
		}		
		return Integer.valueOf(importancia+nivel);
	}
	
	public void nivelCriterios1()
	{
		listaCriterioMagnitudConsumo= new ArrayList<VariableCriterio>();
		listaCriterioMagnitudConsumo=variableCriterioFacade.listaCriterios(1);
		listaValoresMagnitud1= new ArrayList<ValorMagnitud>();
		criteriomagnitud="";
	}
	public void nivelCriterios2()
	{
		listaCriterioMagnitudDimensionamiento= new ArrayList<VariableCriterio>();
		listaCriterioMagnitudDimensionamiento=variableCriterioFacade.listaCriterios(2);
		listaValoresMagnitud2= new ArrayList<ValorMagnitud>();
		criteriomagnitud="";		
	}
	public void nivelCriterios3()
	{
		listaCriterioMagnitudCapacidad= new ArrayList<VariableCriterio>();
		listaCriterioMagnitudCapacidad=variableCriterioFacade.listaCriterios(3);
		listaValoresMagnitud3= new ArrayList<ValorMagnitud>();
		criteriomagnitud="";
	}
	
	public void valorCriterios1()
	{
		VariableCriterio criterio = criterioSeleccion;
		criteriomagnitud=criterio.getNombre();
		listaValoresMagnitud1= new ArrayList<ValorMagnitud>();
		listaValoresMagnitud1=valorMagnitudFacade.listaValores(criterio);
		estadoMagnitud=true;
		
		criterioSeleccion = new VariableCriterio();
	}
	public void valorCriterios2()
	{
		VariableCriterio criterio = criterioSeleccion;
		criteriomagnitud=criterio.getNombre();
		listaValoresMagnitud2= new ArrayList<ValorMagnitud>();
		listaValoresMagnitud2=valorMagnitudFacade.listaValores(criterio);
		estadoMagnitud=true;
		
		criterioSeleccion = new VariableCriterio();
	}
	public void valorCriterios3()
	{
		VariableCriterio criterio = criterioSeleccion;
		criteriomagnitud=criterio.getNombre();
		listaValoresMagnitud3= new ArrayList<ValorMagnitud>();
		listaValoresMagnitud3=valorMagnitudFacade.listaValores(criterio);
		estadoMagnitud=true;
		
		criterioSeleccion = new VariableCriterio();
	}
	
	public int magnitudMayorImportancia()
	{
		int x=0;
		
		if(valorMagnitud1==null)
			valorMagnitud1= new ValorMagnitud();
		if(valorMagnitud2==null)
			valorMagnitud2= new ValorMagnitud();
		if(valorMagnitud3==null)
			valorMagnitud3= new ValorMagnitud();
		
		Integer importancia = valorMagnitud1.getValorVariable()==null?0:valorMagnitud1.getValorVariable();		
		if(valorMagnitud1.getValorVariable()!=null)
		{
			valorMagnitudCalculo=valorMagnitud1;
		}
		if (valorMagnitud2.getValorVariable() != null && valorMagnitud2.getValorVariable() > importancia) {
			importancia = valorMagnitud2.getValorVariable();
			valorMagnitudCalculo=valorMagnitud2;
		}
		if (valorMagnitud3.getValorVariable() != null && valorMagnitud3.getValorVariable() > importancia) {
			importancia = valorMagnitud3.getValorVariable();
			valorMagnitudCalculo=valorMagnitud3;
		}
		
		if(valorMagnitud1.getId()!=null)
			x++;
		if(valorMagnitud2.getId()!=null)
			x++;
		if(valorMagnitud3.getId()!=null)
			x++;
		
		return x;
	}
	
	public boolean calculoWolfram()
	{
		boolean estado = false;
		try {
			Integer p1=0,p2=0,p3=0,p4=0,p5=0,p6=0,p7=0,p8=0;
			p1=wfCalculo.getCatalago().getCriterioQuimico();
			p2=wfCalculo.getCatalago().getCriterioAmbiental();
			p3=wfCalculo.getCatalago().getCriterioConcentracion();
			p4=wfCalculo.getCatalago().getCriterioRiesgo();
			p5=wfCalculo.getCatalago().getCriterioSocial();
			p6=valorMagnitudCalculo.getValorVariable();
			p7=coordenadasRcoaBean.getValorCapacidadBiofisica();
			p8=coordenadasRcoaBean.getValorCapacidadSocial();
			JSONArray json = readJsonFromUrl(Constantes.getWolframRest()+"?p1="+p1+"&p2="+p2+"&p3="+p3+"&p4="+p4+"&p5="+p5+"&p6="+p6+"&p7="+p7+"&p8="+p8);
			categoria = (JSONObject)json.get(0);			
			if(categoria.get("categoria").equals(JSONObject.NULL))
			{
				System.out.println("error wolfram categoria:::"+categoria.get("categoria"));				
				estado=false;
			}
			else
				estado=true;
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error wolfram");
			estado=false;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return estado;
	}
	
	public void guardar() throws Exception
	{
		msgError= new ArrayList<String>();
		
		if(proyecto.getCodigoUnicoAmbiental()==null){
			proyecto.setCodigoUnicoAmbiental(secuenciasFacade.getSecuenciaProyecto());
		}
		
		String valCategoria=(String) categoria.get("categoria");//calculos de wolfram
		proyecto.setUsuario(loginBean.getUsuario());
		proyecto.setTipoProyecto(tipoProyecto);//1=Proyecto nuevo, 2=Proyecto en ejecucion
		proyecto.setCategorizacion(Integer.valueOf(valCategoria));
		proyecto.setCodigoPma(wfCalculo.getNivel3());
		proyecto.setProyectoFinalizado(false);
		if(!tramiteEnProceso)
			proyecto.setFechaGeneracionCua(new Date());
		proyecto.setInterecaSnap(false);
		proyecto.setInterecaBosqueProtector(false);
		proyecto.setInterecaPatrimonioForestal(false);
		proyecto.setInterecaRamsar(false);
		proyecto.setIdCantonOficina(coordenadasRcoaBean.getUbicacionOficinaTecnica().getId());
		proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
		
		//coordenadas de ubicacion geografica--------------
		if(coordenadasRcoaBean.getUploadedFileGeo()!=null)
		{
			proyectoLicenciaAmbientalCoaShapeFacade.eliminar(proyecto, 2, 0);
			
			for (int i = 0; i <= coordenadasRcoaBean.getCoordinatesWrappersGeo().size() - 1; i++) {
				shape= new ProyectoLicenciaAmbientalCoaShape();
				shape.setTipoForma(poligono);
				shape.setProyectoLicenciaCoa(proyecto);
				shape.setTipo(2);//2=coordenadas geograficas 1=coordenadas implantacion
				shape.setNumeroActualizaciones(0);
				shape.setSuperficie(coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getSuperficie());
				shape=proyectoLicenciaAmbientalCoaShapeFacade.guardar(shape);
				
				if(coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().size() <= 3000) {
					CoordenadasProyecto coor= new CoordenadasProyecto();
					for (int j = 0; j <=coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().size()-1; j++) {
						coor= new CoordenadasProyecto();
						coor.setProyectoLicenciaAmbientalCoaShape(shape);
						coor.setOrdenCoordenada(coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getOrdenCoordenada());
						coor.setX(coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getX());
						coor.setY(coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getY());
						coor.setAreaGeografica(coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getAreaGeografica());
						coor.setTipoCoordenada(2);//2=coordenadas geograficas 1=coordenadas implantacion 
						coor.setNumeroActualizaciones(0);
						coordenadasProyectoCoaFacade.guardar(coor);
					}
				} else {
					String queryCoordenadasGeo = "INSERT INTO coa_mae.coordinates_project_licencing_coa "
							+ "(prsh_id, coor_status, coor_order, prsh_type, coor_id, coor_creation_date, coor_creator_user, coor_update_number, coor_x, coor_y, coor_geographic_area)"
							+ "VALUES ";
					for (int j = 0; j <=coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().size()-1; j++) {
						String id = crudServiceBean.getSecuenceNextValue("coordinates_project_licencing_coa_coor_id_seq", "coa_mae").toString();
						String queryAdd = "(" + shape.getId() + ", true, "
								+ coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getOrdenCoordenada() + ", 2, " 
								+ id + ", now(), '" + JsfUtil.getLoggedUser().getNombre() + "', 0, "
								+ coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getX()+ ", "  
								+ coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getY() + ", "
								+ coordenadasRcoaBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getAreaGeografica() + "),";
						
						queryCoordenadasGeo += queryAdd;
					}
					queryCoordenadasGeo = queryCoordenadasGeo.substring(0, queryCoordenadasGeo.length() - 1);
					queryCoordenadasGeo += ";";

					crudServiceBean.insertUpdateByNativeQuery(queryCoordenadasGeo, null);
				}
			}
			
		}
		
		//coordenadas de ubicacion de implantacion--------------
		if(coordenadasRcoaBean.getUploadedFileImpl()!=null)
		{
			proyectoLicenciaAmbientalCoaShapeFacade.eliminar(proyecto, 1, 0);
			for(int i=0;i<=coordenadasRcoaBean.getCoordinatesWrappers().size()-1;i++){
				shape= new ProyectoLicenciaAmbientalCoaShape();
				shape.setTipoForma(poligono);
				shape.setProyectoLicenciaCoa(proyecto);
				shape.setTipo(1);//2=coordenadas geograficas 1=coordendas implantacion
				shape.setNumeroActualizaciones(0);
				shape.setSuperficie(coordenadasRcoaBean.getCoordinatesWrappers().get(i).getSuperficie());
				shape=proyectoLicenciaAmbientalCoaShapeFacade.guardar(shape);
				
				if(coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().size() <= 3000) {
					CoordenadasProyecto coorImpl= new CoordenadasProyecto();        	
					for (int j = 0; j <=coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().size()-1; j++) {
						coorImpl= new CoordenadasProyecto();
						coorImpl.setProyectoLicenciaAmbientalCoaShape(shape);
						coorImpl.setOrdenCoordenada(coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getOrdenCoordenada());
						coorImpl.setX(coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getX());
						coorImpl.setY(coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getY());
						coorImpl.setAreaGeografica(coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getAreaGeografica());
						coorImpl.setTipoCoordenada(1);//2=coordenadas geograficas 1=coordenadas implantacion
						coorImpl.setNumeroActualizaciones(0);
						coordenadasProyectoCoaFacade.guardar(coorImpl);                	
					}
				} else {
					String queryCoordenadasGeo = "INSERT INTO coa_mae.coordinates_project_licencing_coa "
							+ "(prsh_id, coor_status, coor_order, prsh_type, coor_id, coor_creation_date, coor_creator_user, coor_update_number, coor_x, coor_y, coor_geographic_area)"
							+ "VALUES ";
					for (int j = 0; j <=coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().size()-1; j++) {
						String id = crudServiceBean.getSecuenceNextValue("coordinates_project_licencing_coa_coor_id_seq", "coa_mae").toString();
						String queryAdd = "(" + shape.getId() + ", true, "
								+ coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getOrdenCoordenada() + ", 1, " 
								+ id + ", now(), '" + JsfUtil.getLoggedUser().getNombre() + "', 0, "
								+ coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getX()+ ", "  
								+ coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getY() + ", "
								+ coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getAreaGeografica() + "),";
						
						queryCoordenadasGeo += queryAdd;
					}
					queryCoordenadasGeo = queryCoordenadasGeo.substring(0, queryCoordenadasGeo.length() - 1);
					queryCoordenadasGeo += ";";

					crudServiceBean.insertUpdateByNativeQuery(queryCoordenadasGeo, null);
				}
			}

			//ubicaciones - parroquias------------------
			ProyectoLicenciaCoaUbicacion pro=new ProyectoLicenciaCoaUbicacion();
			proyectoLicenciaCoaUbicacionFacade.eliminar(proyecto, 0);
			for(UbicacionesGeografica ubi:coordenadasRcoaBean.getUbicacionesSeleccionadas())
			{
				pro=new ProyectoLicenciaCoaUbicacion();
				pro.setProyectoLicenciaCoa(proyecto);
				pro.setUbicacionesGeografica(ubi);
				if(coordenadasRcoaBean.getArea().getInec().equals(ubi.getCodificacionInec()))
					pro.setPrimario(true);
				else
					pro.setPrimario(false);
				proyectoLicenciaCoaUbicacionFacade.guardar(pro);
			}
		}

		//catalogo CIIU principal - complementario 1 - complementario 2
//		if(estadoCiiu)
//		{
			proyectoLicenciaCuaCiuuFacade.eliminar(proyecto);
			if(wf1.getPuesto()!=null)
			{				
				ciiu1.setCatalogoCIUU(ciiuPrincipal);
				ciiu1.setOrderJerarquia(wf1.getPuesto());
				ciiu1.setPrimario(false);
				ciiu1.setProyectoLicenciaCoa(proyecto);				
				if(wfCalculo.getPuesto()==wf1.getPuesto())
					ciiu1.setPrimario(true);
				//-------------sub actividades--------------------
				if(ciiu1.getId() != null){
					proyectoCoaCiuuSubActividadesFacade.eliminar(ciiu1); // eliminar las subactividades seleccionadas cuando se cambia de ciiu
				}
				
				if(listaProyectoCiiuSubActividad1.size()>0)
				{
					proyectoLicenciaCuaCiuuFacade.guardar(ciiu1);
					proyectoCoaCiuuSubActividadesFacade.eliminar(ciiu1);
					for(ProyectoCoaCiuuSubActividades val :  listaProyectoCiiuSubActividad1)
					{
						ProyectoCoaCiuuSubActividades obj = new ProyectoCoaCiuuSubActividades();
						obj.setEstado(true);
						obj.setProyectoLicenciaCuaCiuu(ciiu1);
						obj.setSubActividad(val.getSubActividad());
						obj.setRespuesta(val.getRespuesta());
						proyectoCoaCiuuSubActividadesFacade.guardar(obj);
					}
					residuosActividadesCiiuBean.guardarInfoResiduosActividad(ciiu1, 1);
				}
				else if(subActividad1.getId()!=null || parent1.getId()!=null)
				{
					boolean doblePregunta=false;
					boolean mineriaLibreAprovechamiento=false;
					String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
					String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
					SubActividades subcatalogo = subActividad1.getId()==null?parent1:subActividad1;
					for (String actividad : actividadesDoblePreguntaArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							doblePregunta=true;
						}
					}
					boolean galapagos=false;
					String actividadGalapagos= Constantes.getActividadesGalapagos();			
					String[] actividadesGalapagosArray = actividadGalapagos.split(",");				
					for (String actividad : actividadesGalapagosArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							galapagos=true;
						}					
					}
					if(doblePregunta)
					{
						if(parent1.getId()!=null && !parent1.getValorOpcion())
						{
							subcatalogo=parent1;
							subActividad1=parent1;
						}
					}					
					else
					{
						if(subcatalogo.getCatalogoCIUU().getLibreAprovechamiento()) {
							subcatalogo=parent1;
							mineriaLibreAprovechamiento = true;
						} else {
							if (ciiuPrincipal.getTipoPregunta() != null) {
								Integer tipoPregunta = ciiuPrincipal.getTipoPregunta();
	
								switch (tipoPregunta) {
								case 1:
									subcatalogo = parent1;
									subActividad1 = parent1;
									break;
								case 2:
									if (parent1.getHijos().size() == 0) {
										subcatalogo = parent1;
										subActividad1 = parent1;
									}
									break;
								case 4:
									if (parent1.getHijos().size() == 0) {
										subcatalogo = parent1;
										subActividad1 = parent1;
									}
									break;
								default:
									break;
								}
							} else if(parent1.getId()!=null && parent1.getCatalogoCIUU().getCodigo().compareTo("I5510.01")!=0){
								if(parent1.getValorOpcion() != null){
									if(parent1.getValorOpcion()){
										if(subcatalogo.getEntidadProceso() == null){
											subcatalogo=parent1;
											subActividad1=parent1;
										}else if(subcatalogo.getEntidadProceso().compareTo("5")!=0){
											subcatalogo=parent1;
											subActividad1=parent1;
										}
									}
									if(!parent1.getValorOpcion() && galapagos)
									{
										subcatalogo=parent1;
										subActividad1=parent1;
									}
								}
							}
						}
					}
					
					boolean cremacion=false;
					String actividadesCremacion= Constantes.getActividadesCremacion();				
					String[] actividadesCremacionArray = actividadesCremacion.split(",");					
					for (String actividad : actividadesCremacionArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							cremacion=true;
						}
					}
					boolean alcantarillado=false;
					String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
					String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
					for (String actividad : actividadesAlcantarilladoArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							alcantarillado=true;
						}					
					}
					
					if(ciiu1.getCatalogoCIUU().getTipoSector().getId()==1)
					{
						ciiu1.setAlmacenaHidrocarburos(subActividad1.getValorOpcion());
						ciiu1.setValorOpcion(subActividad1.getValorOpcion());
						ciiu1.setSubActividad(subActividad1);
					}
					if(ciiu1.getCatalogoCIUU().getTipoSector().getId()==2)
					{
						if(!mineriaLibreAprovechamiento)
							ciiu1.setMineriaArtesanal(subActividad1.getValorOpcion());
						
						ciiu1.setValorOpcion(subActividad1.getValorOpcion());
						ciiu1.setSubActividad(subActividad1);
					}
					if(ciiu1.getCatalogoCIUU().getTipoSector().getId()==3)
					{	
						ciiu1.setValorOpcion(subActividad1.getValorOpcion());
						ciiu1.setSubActividad(subActividad1);
						
						if(doblePregunta)
						{
							ciiu1.setPotabilizacionAgua(parent1.getValorOpcion());
							ciiu1.setValorOpcion(subActividad1.getValorOpcion());
							ciiu1.setSubActividad(subActividad1);
							ciiu1.setIdActividadFinanciadoBancoEstado(bancoEstado1.getId());
							ciiu1.setFinanciadoBancoDesarrollo(bancoEstado1.getValorOpcion());
							if(!parent1.getValorOpcion())
							{
								ciiu1.setValorOpcion(parent1.getValorOpcion());
								ciiu1.setSubActividad(parent1);
							}						
						}
						if(cremacion)
						{
							ciiu1.setRealizaCremacion(subActividad1.getValorOpcion());
							ciiu1.setValorOpcion(subActividad1.getValorOpcion());
							ciiu1.setSubActividad(subActividad1);
						}
						if(alcantarillado)
						{
							ciiu1.setPotabilizacionAgua(null);
							ciiu1.setFinanciadoBancoDesarrollo(null);
							ciiu1.setAlcantarilladoMunicipio(parent1.getValorOpcion());
							ciiu1.setValorOpcion(parent1.getValorOpcion());
							ciiu1.setSubActividad(parent1);							
							if(!parent1.getValorOpcion())
							{								
								ciiu1.setSubActividad(subActividad1);
							}							
						}
						if(galapagos)
						{							
							ciiu1.setValorOpcion(parent1.getValorOpcion());
							ciiu1.setSubActividad(parent1);							
							if(parent1.getValorOpcion() || (!parent1.getValorOpcion()&& parent1.getCatalogoCIUU().getCodigo().compareTo("I5510.01")==0))
							{								
								ciiu1.setSubActividad(subActividad1);
							}							
						}
						
						//guardar información de actividades financiadas
						if(subActividad1.getFinanciadoBancoEstado() != null && subActividad1.getFinanciadoBancoEstado()) {
							//para las actividades que tienen solo la pregunta del banco de SI y NO
							ciiu1.setValorOpcion(null);
							ciiu1.setSubActividad(null);
							ciiu1.setIdActividadFinanciadoBancoEstado(subActividad1.getId());
							ciiu1.setFinanciadoBancoDesarrollo(subActividad1.getValorOpcion());
						} else {
							//para las actividades que tienen 2 preguntas de SI y NO E3821.01,E3821.01.01
							if(bancoEstado1.getId() != null 
									&& bancoEstado1.getFinanciadoBancoEstado() != null && bancoEstado1.getFinanciadoBancoEstado()) {
								ciiu1.setIdActividadFinanciadoBancoEstado(bancoEstado1.getId());
								ciiu1.setFinanciadoBancoDesarrollo(bancoEstado1.getValorOpcion());
							}
						}
					}
					if(ciiu1.getCatalogoCIUU().getTipoSector().getId()==4)
					{
						ciiu1.setSubActividad(subActividad1);
					}
					
					if(ciiu1.getCatalogoCIUU().getRequiereViabilidadPngids() && 
							(ciiu1.getCatalogoCIUU().getCodigo().compareTo("E3821.01")==0 || ciiu1.getCatalogoCIUU().getCodigo().compareTo("E3821.01.01") == 0)){
						ciiu1.setSubActividad(subActividad1);
						ciiu1.setValorOpcion(subActividad1.getValorOpcion());
					}
				}
				else
					ciiu1.setSubActividad(null);
					
				//------fin sub actividades----------------------
				proyectoLicenciaCuaCiuuFacade.guardar(ciiu1);				
				
				if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiu1.getCatalogoCIUU())) {
					if(ciiu1.getPrimario()) {
						ciiu1.setCombinacionSubActividades(subActividadesCiiuBean.getCombinacionSubActividadesProcesos());
						proyectoLicenciaCuaCiuuFacade.guardar(ciiu1);
					}

					subActividadesCiiuBean.guardarCiiuSubACtividades(ciiu1, listaSubActividades1);
					//guardar residuos
					residuosActividadesCiiuBean.guardarInfoResiduosActividad(ciiu1, 1);
				}
			}
			if(wf2.getPuesto()!=null)
			{				
				ciiu2.setCatalogoCIUU(ciiuComplementaria1);
				ciiu2.setOrderJerarquia(wf2.getPuesto());
				ciiu2.setPrimario(false);
				ciiu2.setProyectoLicenciaCoa(proyecto);
				if(wfCalculo.getPuesto()==wf2.getPuesto())
					ciiu2.setPrimario(true);
				//-------------sub actiidades--------------------
				if(ciiu2.getId() != null){
					proyectoCoaCiuuSubActividadesFacade.eliminar(ciiu2); // eliminar las subactividades seleccionadas cuando se cambia de ciiu
				}
				
				if(listaProyectoCiiuSubActividad2.size()>0)
				{
					proyectoLicenciaCuaCiuuFacade.guardar(ciiu2);
					proyectoCoaCiuuSubActividadesFacade.eliminar(ciiu2);
					for(ProyectoCoaCiuuSubActividades val :  listaProyectoCiiuSubActividad2)
					{
						ProyectoCoaCiuuSubActividades obj = new ProyectoCoaCiuuSubActividades();
						obj.setEstado(true);
						obj.setProyectoLicenciaCuaCiuu(ciiu2);
						obj.setSubActividad(val.getSubActividad());
						obj.setRespuesta(val.getRespuesta());
						proyectoCoaCiuuSubActividadesFacade.guardar(obj);
					}
					residuosActividadesCiiuBean.guardarInfoResiduosActividad(ciiu2, 2);
				}
				else if(subActividad2.getId()!=null || parent2.getId()!=null)
				{
					boolean doblePregunta=false;
					boolean mineriaLibreAprovechamiento=false;
					String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
					String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
					SubActividades subcatalogo = subActividad2.getId()==null?parent2:subActividad2;
					for (String actividad : actividadesDoblePreguntaArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							doblePregunta=true;
						}
					}
					if(doblePregunta)
					{
						if(parent2.getId()!=null && !parent2.getValorOpcion())
						{
							subcatalogo=parent2;
							subActividad2=parent2;
						}
					}
					else
					{
						if(subcatalogo.getCatalogoCIUU().getLibreAprovechamiento()) {
							subcatalogo=parent2;
							mineriaLibreAprovechamiento = true;
						} else {
							if(ciiuComplementaria1.getTipoPregunta() != null) {
								Integer tipoPregunta = ciiuComplementaria1.getTipoPregunta();
								
								switch (tipoPregunta) {
								case 1:
									subcatalogo = parent2;
									subActividad2 = parent2;
									break;
								case 2:
									if (parent2.getHijos().size() == 0) {
										subcatalogo = parent2;
										subActividad2 = parent2;
									}
									break;
								case 4:
									if (parent2.getHijos().size() == 0) {
										subcatalogo = parent2;
										subActividad2 = parent2;
									}
									break;
								default:
									break;
								}
							} else if(parent2.getId()!=null && parent2.getValorOpcion())
							{
								subcatalogo=parent2;
								subActividad2=parent2;
							}
						}
					}
					
					boolean cremacion=false;
					String actividadesCremacion= Constantes.getActividadesCremacion();				
					String[] actividadesCremacionArray = actividadesCremacion.split(",");					
					for (String actividad : actividadesCremacionArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							cremacion=true;
						}
					}
					boolean alcantarillado=false;
					String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
					String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
					for (String actividad : actividadesAlcantarilladoArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							alcantarillado=true;
						}					
					}
					boolean galapagos=false;
					String actividadGalapagos= Constantes.getActividadesGalapagos();			
					String[] actividadesGalapagosArray = actividadGalapagos.split(",");				
					for (String actividad : actividadesGalapagosArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							galapagos=true;
						}					
					}
					if(ciiu2.getCatalogoCIUU().getTipoSector().getId()==1)
					{
						ciiu2.setAlmacenaHidrocarburos(subActividad2.getValorOpcion());
						ciiu2.setValorOpcion(subActividad2.getValorOpcion());
						ciiu2.setSubActividad(subActividad2);
					}
					if(ciiu2.getCatalogoCIUU().getTipoSector().getId()==2)
					{
						if(!mineriaLibreAprovechamiento)
							ciiu2.setMineriaArtesanal(subActividad2.getValorOpcion());
						
						ciiu2.setValorOpcion(subActividad2.getValorOpcion());
						ciiu2.setSubActividad(subActividad2);
					}
					if(ciiu2.getCatalogoCIUU().getTipoSector().getId()==3)
					{	
						ciiu2.setValorOpcion(subActividad2.getValorOpcion());
						ciiu2.setSubActividad(subActividad2);
						
						if(doblePregunta)
						{
							ciiu2.setPotabilizacionAgua(parent2.getValorOpcion());
							ciiu2.setValorOpcion(subActividad2.getValorOpcion());
							ciiu2.setSubActividad(subActividad2);
							ciiu2.setIdActividadFinanciadoBancoEstado(bancoEstado2.getId());
							ciiu2.setFinanciadoBancoDesarrollo(bancoEstado2.getValorOpcion());
							if(!parent2.getValorOpcion())
							{
								ciiu2.setValorOpcion(parent2.getValorOpcion());
								ciiu2.setSubActividad(parent2);
							}						
						}
						if(cremacion)
						{
							ciiu2.setRealizaCremacion(subActividad2.getValorOpcion());
							ciiu2.setValorOpcion(subActividad2.getValorOpcion());
							ciiu2.setSubActividad(subActividad2);
						}
						if(alcantarillado)
						{
							ciiu2.setPotabilizacionAgua(null);
							ciiu2.setFinanciadoBancoDesarrollo(null);
							ciiu2.setAlcantarilladoMunicipio(parent2.getValorOpcion());
							ciiu2.setValorOpcion(parent2.getValorOpcion());
							ciiu2.setSubActividad(parent2);							
							if(!parent2.getValorOpcion())
							{								
								ciiu2.setSubActividad(subActividad2);
							}							
						}
						if(galapagos)
						{							
							ciiu2.setValorOpcion(parent2.getValorOpcion());
							ciiu2.setSubActividad(parent2);							
							if(parent2.getValorOpcion() || (!parent2.getValorOpcion()&& parent2.getCatalogoCIUU().getCodigo().compareTo("I5510.01")==0))
							{								
								ciiu2.setSubActividad(subActividad2);
							}							
						}

						//guardar información de actividades financiadas
						if(subActividad2.getFinanciadoBancoEstado() != null && subActividad2.getFinanciadoBancoEstado()) {
							//para las actividades que tienen solo la pregunta del banco de SI y NO
							ciiu2.setValorOpcion(null);
							ciiu2.setSubActividad(null);
							ciiu2.setIdActividadFinanciadoBancoEstado(subActividad2.getId());
							ciiu2.setFinanciadoBancoDesarrollo(subActividad2.getValorOpcion());
						} else {
							//para las actividades que tienen 2 preguntas de SI y NO E3821.01,E3821.01.01
							if(bancoEstado2.getId() != null 
									&& bancoEstado2.getFinanciadoBancoEstado() != null && bancoEstado2.getFinanciadoBancoEstado()) {
								ciiu2.setIdActividadFinanciadoBancoEstado(bancoEstado2.getId());
								ciiu2.setFinanciadoBancoDesarrollo(bancoEstado2.getValorOpcion());
							}
						}
					}
					if(ciiu2.getCatalogoCIUU().getTipoSector().getId()==4)
					{
						ciiu2.setSubActividad(subActividad2);
					}					

					if(ciiu2.getCatalogoCIUU().getRequiereViabilidadPngids() && 
							(ciiu2.getCatalogoCIUU().getCodigo().compareTo("E3821.01")==0 || ciiu2.getCatalogoCIUU().getCodigo().compareTo("E3821.01.01") == 0)){
						ciiu2.setSubActividad(subActividad2);
						ciiu2.setValorOpcion(subActividad2.getValorOpcion());
					}
					
				}
				else
					ciiu2.setSubActividad(null);					
				//------fin sub actividades----------------------
				proyectoLicenciaCuaCiuuFacade.guardar(ciiu2);	

				if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiu2.getCatalogoCIUU())) {
					if(ciiu2.getPrimario()) {
						ciiu2.setCombinacionSubActividades(subActividadesCiiuBean.getCombinacionSubActividadesProcesos());
						proyectoLicenciaCuaCiuuFacade.guardar(ciiu2);
					}

					subActividadesCiiuBean.guardarCiiuSubACtividades(ciiu2, listaSubActividades2);
					//guardar residuos
					residuosActividadesCiiuBean.guardarInfoResiduosActividad(ciiu2, 2);
				}		
			}
			
			if(wf3.getPuesto()!=null)
			{				
				ciiu3.setCatalogoCIUU(ciiuComplementaria2);
				ciiu3.setOrderJerarquia(wf3.getPuesto());
				ciiu3.setPrimario(false);
				ciiu3.setProyectoLicenciaCoa(proyecto);
				if(wfCalculo.getPuesto()==wf3.getPuesto())
					ciiu3.setPrimario(true);
				//-------------sub actiidades--------------------
				if(ciiu3.getId() != null){
					proyectoCoaCiuuSubActividadesFacade.eliminar(ciiu3); // eliminar las subactividades seleccionadas cuando se cambia de ciiu
				}
				
				if(listaProyectoCiiuSubActividad3.size()>0)
				{
					proyectoLicenciaCuaCiuuFacade.guardar(ciiu3);
					proyectoCoaCiuuSubActividadesFacade.eliminar(ciiu3);
					for(ProyectoCoaCiuuSubActividades val :  listaProyectoCiiuSubActividad3)
					{
						ProyectoCoaCiuuSubActividades obj = new ProyectoCoaCiuuSubActividades();
						obj.setEstado(true);
						obj.setProyectoLicenciaCuaCiuu(ciiu3);
						obj.setSubActividad(val.getSubActividad());
						obj.setRespuesta(val.getRespuesta());
						proyectoCoaCiuuSubActividadesFacade.guardar(obj);
					}
					residuosActividadesCiiuBean.guardarInfoResiduosActividad(ciiu3, 3);
				}
				else if(subActividad3.getId()!=null || parent3.getId()!=null)
				{
					boolean doblePregunta=false;
					boolean mineriaLibreAprovechamiento=false;
					String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
					String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
					SubActividades subcatalogo = subActividad3.getId()==null?parent3:subActividad3;
					for (String actividad : actividadesDoblePreguntaArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							doblePregunta=true;
						}
					}
					if(doblePregunta)
					{
						if(parent3.getId()!=null && !parent3.getValorOpcion())
						{
							subcatalogo=parent3;
							subActividad3=parent3;
						}
					}
					else
					{
						if(subcatalogo.getCatalogoCIUU().getLibreAprovechamiento()) {
							subcatalogo=parent3;
							mineriaLibreAprovechamiento = true;
						} else {
							if(ciiuComplementaria2.getTipoPregunta() != null) {
								Integer tipoPregunta = ciiuComplementaria2.getTipoPregunta();
								
								switch (tipoPregunta) {
								case 1:
									subcatalogo = parent3;
									subActividad3 = parent3;
									break;
								case 2:
									if (parent3.getHijos().size() == 0) {
										subcatalogo = parent3;
										subActividad3 = parent3;
									}
									break;
								case 4:
									if (parent3.getHijos().size() == 0) {
										subcatalogo = parent3;
										subActividad3 = parent3;
									}
									break;
								default:
									break;
								}
							} else if(parent3.getId()!=null && parent3.getValorOpcion())
							{
								subcatalogo=parent3;
								subActividad3=parent3;
							}
						}
					}
					
					boolean cremacion=false;
					String actividadesCremacion= Constantes.getActividadesCremacion();				
					String[] actividadesCremacionArray = actividadesCremacion.split(",");					
					for (String actividad : actividadesCremacionArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							cremacion=true;
						}
					}
					boolean alcantarillado=false;
					String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
					String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
					for (String actividad : actividadesAlcantarilladoArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							alcantarillado=true;
						}					
					}
					boolean galapagos=false;
					String actividadGalapagos= Constantes.getActividadesGalapagos();			
					String[] actividadesGalapagosArray = actividadGalapagos.split(",");				
					for (String actividad : actividadesGalapagosArray) {
						if(subcatalogo.getCatalogoCIUU().getCodigo().equals(actividad))
						{
							galapagos=true;
						}					
					}
					
					if(ciiu3.getCatalogoCIUU().getTipoSector().getId()==1)
					{
						ciiu3.setAlmacenaHidrocarburos(subActividad3.getValorOpcion());
						ciiu3.setValorOpcion(subActividad3.getValorOpcion());
						ciiu3.setSubActividad(subActividad3);
					}
					if(ciiu3.getCatalogoCIUU().getTipoSector().getId()==2)
					{
						if(!mineriaLibreAprovechamiento)
							ciiu3.setMineriaArtesanal(subActividad3.getValorOpcion());
						
						ciiu3.setValorOpcion(subActividad3.getValorOpcion());
						ciiu3.setSubActividad(subActividad3);
					}
					if(ciiu3.getCatalogoCIUU().getTipoSector().getId()==3)
					{	
						ciiu3.setValorOpcion(subActividad3.getValorOpcion());
						ciiu3.setSubActividad(subActividad3);
						
						if(doblePregunta)
						{
							ciiu3.setPotabilizacionAgua(parent3.getValorOpcion());
							ciiu3.setValorOpcion(subActividad3.getValorOpcion());
							ciiu3.setSubActividad(subActividad3);
							ciiu3.setIdActividadFinanciadoBancoEstado(bancoEstado3.getId());
							ciiu3.setFinanciadoBancoDesarrollo(bancoEstado3.getValorOpcion());
							if(!parent3.getValorOpcion())
							{
								ciiu3.setValorOpcion(parent3.getValorOpcion());
								ciiu3.setSubActividad(parent3);
							}						
						}
						if(cremacion)
						{
							ciiu3.setRealizaCremacion(subActividad3.getValorOpcion());
							ciiu3.setValorOpcion(subActividad3.getValorOpcion());
							ciiu3.setSubActividad(subActividad3);
						}
						if(alcantarillado)
						{
							ciiu3.setPotabilizacionAgua(null);
							ciiu3.setFinanciadoBancoDesarrollo(null);
							ciiu3.setAlcantarilladoMunicipio(parent3.getValorOpcion());
							ciiu3.setValorOpcion(parent3.getValorOpcion());
							ciiu3.setSubActividad(parent3);							
							if(!parent3.getValorOpcion())
							{								
								ciiu3.setSubActividad(subActividad3);
							}
							
						}
						if(galapagos)
						{							
							ciiu3.setValorOpcion(parent3.getValorOpcion());
							ciiu3.setSubActividad(parent3);							
							if(parent3.getValorOpcion() || (!parent3.getValorOpcion()&& parent3.getCatalogoCIUU().getCodigo().compareTo("I5510.01")==0))
							{								
								ciiu3.setSubActividad(subActividad3);
							}							
						}

						//guardar información de actividades financiadas
						if(subActividad3.getFinanciadoBancoEstado() != null && subActividad3.getFinanciadoBancoEstado()) {
							//para las actividades que tienen solo la pregunta del banco de SI y NO
							ciiu3.setValorOpcion(null);
							ciiu3.setSubActividad(null);
							ciiu3.setIdActividadFinanciadoBancoEstado(subActividad3.getId());
							ciiu3.setFinanciadoBancoDesarrollo(subActividad3.getValorOpcion());
						} else {
							//para las actividades que tienen 2 preguntas de SI y NO E3821.01,E3821.01.01
							if(bancoEstado3.getId() != null 
									&& bancoEstado3.getFinanciadoBancoEstado() != null && bancoEstado3.getFinanciadoBancoEstado()) {
								ciiu3.setIdActividadFinanciadoBancoEstado(bancoEstado3.getId());
								ciiu3.setFinanciadoBancoDesarrollo(bancoEstado3.getValorOpcion());
							}
						}
					}
					if(ciiu3.getCatalogoCIUU().getTipoSector().getId()==4)
					{
						ciiu3.setSubActividad(subActividad3);
					}					
					
					if(ciiu3.getCatalogoCIUU().getRequiereViabilidadPngids() && 
							(ciiu3.getCatalogoCIUU().getCodigo().compareTo("E3821.01")==0 || ciiu3.getCatalogoCIUU().getCodigo().compareTo("E3821.01.01") == 0)){
						ciiu3.setSubActividad(subActividad3);
					}
					
				}
				else
					ciiu3.setSubActividad(null);					
				//------fin sub actividades----------------------
				proyectoLicenciaCuaCiuuFacade.guardar(ciiu3);				

				if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiu3.getCatalogoCIUU())) {
					if(ciiu3.getPrimario()) {
						ciiu3.setCombinacionSubActividades(subActividadesCiiuBean.getCombinacionSubActividadesProcesos());
						proyectoLicenciaCuaCiuuFacade.guardar(ciiu3);
					}

					subActividadesCiiuBean.guardarCiiuSubACtividades(ciiu3, listaSubActividades3);
					//guardar residuos
					residuosActividadesCiiuBean.guardarInfoResiduosActividad(ciiu3, 3);
				}
			}
			//		}
		if(ciiu1.getGenetico()!=null && ciiu1.getGenetico() && documentoGeneticoCiiu1.getContenidoDocumento()!=null)
			guardarDocGeneticCiiu1();
		if(ciiu2.getGenetico()!=null && ciiu2.getGenetico() && documentoGeneticoCiiu2.getContenidoDocumento()!=null)
			guardarDocGeneticCiiu2();
		if(ciiu3.getGenetico()!=null && ciiu3.getGenetico() && documentoGeneticoCiiu3.getContenidoDocumento()!=null)
			guardarDocGeneticCiiu3();
		
		//guardar sustancias quimicas------------------------------
		GestionarProductosQuimicosProyectoAmbiental sus = new GestionarProductosQuimicosProyectoAmbiental();
		gestionarProductosQuimicosProyectoAmbientalFacade.eliminar(proyecto);
		estadoSustanciasQuimicas=false;
		for(SustanciaQuimicaPeligrosa sustancias: sustanciaQuimicaSeleccionada)
		{
			sus = new GestionarProductosQuimicosProyectoAmbiental();
			sus.setProyectoLicenciaCoa(proyecto);
			sus.setSustanciaquimica(sustancias);
			sus.setControlSustancia(false);
			sus.setGestionaSustancia(true);
			sus.setTransportaSustancia(false);
			if(sustancias.getControlSustancia()!=null && sustancias.getControlSustancia())
			{
				sus.setControlSustancia(true);
				estadoSustanciasQuimicas=true;
			}			
			gestionarProductosQuimicosProyectoAmbientalFacade.guardar(sus);
		}
		
		for (SustanciaQuimicaPeligrosa sustanciast : sustanciaQuimicaSeleccionadaTransporta) {

			if (!sustanciaQuimicaSeleccionada.contains(sustanciast)) {
				sus = new GestionarProductosQuimicosProyectoAmbiental();
				sus.setProyectoLicenciaCoa(proyecto);
				sus.setSustanciaquimica(sustanciast);
				sus.setControlSustancia(false);
				sus.setGestionaSustancia(false);
				sus.setTransportaSustancia(true);
				if (sustanciast.getControlSustancia() != null && sustanciast.getControlSustancia()) {
					sus.setControlSustancia(true);
					estadoSustanciasQuimicas = true;
				}
				gestionarProductosQuimicosProyectoAmbientalFacade.guardar(sus);
			}else {
				GestionarProductosQuimicosProyectoAmbiental sust = gestionarProductosQuimicosProyectoAmbientalFacade.getSustanciaById(proyecto,sustanciast.getId());				
				sust.setTransportaSustancia(true);
				gestionarProductosQuimicosProyectoAmbientalFacade.guardar(sust);
			}

		}
		// guardar otras sustancias que no estan en el catalogo
		for (SustanciaQuimicaPeligrosa sustanciasOtros : listaSustanciaQuimicaSeleccionadaOtros) {
			sus = new GestionarProductosQuimicosProyectoAmbiental();
			sus.setProyectoLicenciaCoa(proyecto);
			sus.setSustanciaquimica(sustanciasOtros);
			sus.setOtraSustancia(sustanciasOtros.getDescripcion());
			sus.setControlSustancia(false);
			sus.setGestionaSustancia(true);
			sus.setTransportaSustancia(false);
			gestionarProductosQuimicosProyectoAmbientalFacade.guardar(sus);
		}

		for (SustanciaQuimicaPeligrosa sustanciasOtrosT : listaSustanciaQuimicaSeleccionadaOtrosTransporta) {
			
				GestionarProductosQuimicosProyectoAmbiental sustO = gestionarProductosQuimicosProyectoAmbientalFacade.getSustanciaByIdByDescri(proyecto, Constantes.ID_SUSTANCIA_OTROS, sustanciasOtrosT.getDescripcion().trim());
				if(sustO != null) {
					sustO.setTransportaSustancia(true);
					gestionarProductosQuimicosProyectoAmbientalFacade.guardar(sustO);
				}else {
					sus = new GestionarProductosQuimicosProyectoAmbiental();
					sus.setProyectoLicenciaCoa(proyecto);
					sus.setSustanciaquimica(sustanciasOtrosT);
					sus.setOtraSustancia(sustanciasOtrosT.getDescripcion().trim());
					sus.setControlSustancia(false);
					sus.setGestionaSustancia(false);
					sus.setTransportaSustancia(true);
					gestionarProductosQuimicosProyectoAmbientalFacade.guardar(sus);
				}				
			
		}
		
		
		
		//guardar rango de magnitud-------------------------------
		CriterioMagnitud criterio= new CriterioMagnitud();
		ValorCalculo calculo= new ValorCalculo();
		if(estadoMagnitud)
		{
			criterioMagnitudFacade.eliminar(proyecto);
			if(valorMagnitud1.getId()!=null)
			{
				criterio= new CriterioMagnitud();
				criterio.setProyectoLicenciaCoa(proyecto);
				criterio.setVariableCriterio(valorMagnitud1.getCriterioMagnitud().getId());
				criterio.setValorMagnitud(valorMagnitud1);
				criterio.setRango(valorMagnitud1.getValorVariable());
				criterio.setRangoDescripcion(valorMagnitud1.getRango());
				criterio.setValor(valorMagnitud1.getValor());
				criterio.setPrioridad(false);
				criterio=criterioMagnitudFacade.guardar(criterio);
				if(valorMagnitud1.equals(valorMagnitudCalculo))
				{
					calculo= new ValorCalculo();
					calculo.setProyectoLicenciaCoa(proyecto);
					calculo.setCatalogoCIUU(wfCalculo.getCatalago());
					calculo.setCriterioMagnitud(criterio);
					calculo.setCodigo(wfCalculo.getNivel5());
					calculo.setNivel(5);
					calculo.setValorQuimico(wfCalculo.getCatalago().getCriterioQuimico());
					calculo.setValorAmbiental(wfCalculo.getCatalago().getCriterioAmbiental());
					calculo.setValorConcentracion(wfCalculo.getCatalago().getCriterioConcentracion());
					calculo.setValorRiesgo(wfCalculo.getCatalago().getCriterioRiesgo());
					calculo.setValorSocial(wfCalculo.getCatalago().getCriterioSocial());
					calculo.setValorMagnitud(valorMagnitud1.getValorVariable());
					calculo.setValorCapacidadSocial(coordenadasRcoaBean.getValorCapacidadSocial());
					calculo.setValorCapacidadBiofisica(coordenadasRcoaBean.getValorCapacidadBiofisica());
					calculo.setValorImportanciaRelativa(wfCalculo.getImportancia());
					calculo.setValorResultadoFormula(Integer.valueOf(valCategoria));
					calculo.setCategorizacionAlternativa(Integer.valueOf(valCategoria));
					calculo.setRango(valorMagnitud1.getValorVariable());
					calculo.setValor(valorMagnitud1.getValor());
					valorCalculoFacade.guardar(calculo);

					criterio.setPrioridad(true);
					criterio=criterioMagnitudFacade.guardar(criterio);
				}
			}
			if(valorMagnitud2.getId()!=null)
			{
				criterio= new CriterioMagnitud();
				criterio.setProyectoLicenciaCoa(proyecto);
				criterio.setVariableCriterio(valorMagnitud2.getCriterioMagnitud().getId());
				criterio.setValorMagnitud(valorMagnitud2);
				criterio.setRango(valorMagnitud2.getValorVariable());
				criterio.setRangoDescripcion(valorMagnitud2.getRango());
				criterio.setValor(valorMagnitud2.getValor());
				criterio.setPrioridad(false);
				criterio=criterioMagnitudFacade.guardar(criterio);
				if(valorMagnitud2.equals(valorMagnitudCalculo))
				{
					calculo= new ValorCalculo();
					calculo.setProyectoLicenciaCoa(proyecto);
					calculo.setCatalogoCIUU(wfCalculo.getCatalago());
					calculo.setCriterioMagnitud(criterio);
					calculo.setCodigo(wfCalculo.getNivel5());
					calculo.setNivel(5);
					calculo.setValorQuimico(wfCalculo.getCatalago().getCriterioQuimico());
					calculo.setValorAmbiental(wfCalculo.getCatalago().getCriterioAmbiental());
					calculo.setValorConcentracion(wfCalculo.getCatalago().getCriterioConcentracion());
					calculo.setValorRiesgo(wfCalculo.getCatalago().getCriterioRiesgo());
					calculo.setValorSocial(wfCalculo.getCatalago().getCriterioSocial());
					calculo.setValorMagnitud(valorMagnitud2.getValorVariable());
					calculo.setValorCapacidadSocial(coordenadasRcoaBean.getValorCapacidadSocial());
					calculo.setValorCapacidadBiofisica(coordenadasRcoaBean.getValorCapacidadBiofisica());
					calculo.setValorImportanciaRelativa(wfCalculo.getImportancia());
					calculo.setValorResultadoFormula(Integer.valueOf(valCategoria));
					calculo.setCategorizacionAlternativa(Integer.valueOf(valCategoria));
					calculo.setRango(valorMagnitud2.getValorVariable());
					calculo.setValor(valorMagnitud2.getValor());
					valorCalculoFacade.guardar(calculo);

					criterio.setPrioridad(true);
					criterio=criterioMagnitudFacade.guardar(criterio);
				}
			}
			if(valorMagnitud3.getId()!=null)
			{
				criterio= new CriterioMagnitud();
				criterio.setProyectoLicenciaCoa(proyecto);
				criterio.setVariableCriterio(valorMagnitud3.getCriterioMagnitud().getId());
				criterio.setValorMagnitud(valorMagnitud3);
				criterio.setRango(valorMagnitud3.getValorVariable());
				criterio.setRangoDescripcion(valorMagnitud3.getRango());
				criterio.setValor(valorMagnitud3.getValor());
				criterio.setPrioridad(false);
				criterio=criterioMagnitudFacade.guardar(criterio);
				if(valorMagnitud3.equals(valorMagnitudCalculo))
				{
					calculo= new ValorCalculo();
					calculo.setProyectoLicenciaCoa(proyecto);
					calculo.setCatalogoCIUU(wfCalculo.getCatalago());
					calculo.setCriterioMagnitud(criterio);
					calculo.setCodigo(wfCalculo.getNivel5());
					calculo.setNivel(5);
					calculo.setValorQuimico(wfCalculo.getCatalago().getCriterioQuimico());
					calculo.setValorAmbiental(wfCalculo.getCatalago().getCriterioAmbiental());
					calculo.setValorConcentracion(wfCalculo.getCatalago().getCriterioConcentracion());
					calculo.setValorRiesgo(wfCalculo.getCatalago().getCriterioRiesgo());
					calculo.setValorSocial(wfCalculo.getCatalago().getCriterioSocial());
					calculo.setValorMagnitud(valorMagnitud3.getValorVariable());
					calculo.setValorCapacidadSocial(coordenadasRcoaBean.getValorCapacidadSocial());
					calculo.setValorCapacidadBiofisica(coordenadasRcoaBean.getValorCapacidadBiofisica());
					calculo.setValorImportanciaRelativa(wfCalculo.getImportancia());
					calculo.setValorResultadoFormula(Integer.valueOf(valCategoria));
					calculo.setCategorizacionAlternativa(Integer.valueOf(valCategoria));
					calculo.setRango(valorMagnitud3.getValorVariable());
					calculo.setValor(valorMagnitud3.getValor());
					valorCalculoFacade.guardar(calculo);

					criterio.setPrioridad(true);
					criterio=criterioMagnitudFacade.guardar(criterio);
				}
			}
			estadoMagnitud=false;
		}
		//guardar capas de interseccion y detalle de interseccion
		if(coordenadasRcoaBean.getUploadedFileImpl()!=null)
		{
			interseccionProyectoLicenciaAmbientalFacade.eliminar(proyecto, JsfUtil.getLoggedUser().getNombre(), 0);
			for (InterseccionProyectoLicenciaAmbiental i : coordenadasRcoaBean.getCapasIntersecciones().keySet()) {
				i.setProyectoLicenciaCoa(proyecto);
				i.setFechaproceso(new Date());
				i=interseccionProyectoLicenciaAmbientalFacade.guardar(i);
				for(DetalleInterseccionProyectoAmbiental j : coordenadasRcoaBean.getCapasIntersecciones().get(i))
				{
					j.setInterseccionProyectoLicenciaAmbiental(i);
					detalleInterseccionProyectoAmbientalFacade.guardar(j);					
				}
			}
		}  
		
		intersecciones=interseccionProyectoLicenciaAmbientalFacade.intersecan(proyecto);
		for(InterseccionProyectoLicenciaAmbiental inter:intersecciones)
		{
			if(inter.getCapa().getId()==3)//snap
				proyecto.setInterecaSnap(true);
			if(inter.getCapa().getId()==1)//bosques protectores
				proyecto.setInterecaBosqueProtector(true);
			if(inter.getCapa().getId()==8)//patrimonio forestal nacinal
				proyecto.setInterecaPatrimonioForestal(true);
			if(inter.getCapa().getId()==11)//ramsar
				proyecto.setInterecaRamsar(true);
		}
		
		ciiuArearesponsable=proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
		proyectoLicenciaAmbientalConcesionesMinerasFacade.eliminar(ciiuArearesponsable);
		for(ProyectoLicenciaAmbientalConcesionesMineras x: listaConcesionesMineras)
		{
			x.setProyectoLicenciaCuaCiuu(ciiuArearesponsable);
			proyectoLicenciaAmbientalConcesionesMinerasFacade.guardar(x);
		}
		
		proyectoLicenciaAmbientalCoaCiuuBloquesFacade.eliminar(ciiuArearesponsable);
		for(Bloque bloques: bloquesBean.getBloquesSeleccionados())
		{
			ProyectoLicenciaAmbientalCoaCiuuBloques obj = new ProyectoLicenciaAmbientalCoaCiuuBloques();
			obj.setBloque(bloques);
			obj.setProyectoLicenciaCuaCiuu(ciiuArearesponsable);
			proyectoLicenciaAmbientalCoaCiuuBloquesFacade.guardar(obj);
		}
		
		if(coordenadasRcoaBean.isEstadoZonaIntangible() && documentoDocSectorial.getContenidoDocumento()!=null)
			guardarDocSectorial();
//		if(estadoFrontera && documentoDocFrontera.getContenidoDocumento()!=null)
//			guardarDocFrontera();
		
		
		
		if(usuarioMsp && !otroMsp){
			
			nombreProyectoMsp = nombreProyectosMspFacade.buscarPorIdProyecto(proyecto.getId());
			
			if(nombreProyectoMsp != null){
				NombreProyectos proyectoNom = nombreProyectoMsp.getNombreProyectos();
				proyectoNom.setProyectoUsado(false);
				nombreProyectosFacade.save(proyectoNom, loginBean.getUsuario());	
				
				nombreProyectoMsp.setEstado(false);
				nombreProyectosMspFacade.save(nombreProyectoMsp, loginBean.getUsuario());
			}			
			
			NombreProyectosMsp proyectoMsp = new NombreProyectosMsp();
			proyectoMsp.setNombreProyectos(nombreProyecto);
			proyectoMsp.setRucMsp(rucMsp);
			proyectoMsp.setProyectoLicenciaCoa(proyecto);
			nombreProyectosMspFacade.save(proyectoMsp, loginBean.getUsuario());
			
			nombreProyecto.setProyectoUsado(true);			
			nombreProyectosFacade.save(nombreProyecto, loginBean.getUsuario());			
		}else if(usuarioMsp && otroMsp){
			nombreProyectoMsp = nombreProyectosMspFacade.buscarPorIdProyecto(proyecto.getId());
			
			if(nombreProyectoMsp != null){
				NombreProyectos proyectoNom = nombreProyectoMsp.getNombreProyectos();
				if(proyectoNom != null){
					proyectoNom.setProyectoUsado(false);
					nombreProyectosFacade.save(proyectoNom, loginBean.getUsuario());	
				}
				
				nombreProyectoMsp.setEstado(false);
				nombreProyectosMspFacade.save(nombreProyectoMsp, loginBean.getUsuario());
			}
		}
		
		//guardar RGD asociado
		eliminarVinculacionRgd();
		if(proyecto.getGeneraDesechos() && existeRgdObtenidoAnterior && esRgdValido) {
			if(registroGeneradorSeleccionado != null && registroGeneradorSeleccionado.getId() != null) {
				if(registroGeneradorSeleccionado.getSourceType().equals(GeneradorCustom.SOURCE_TYPE_SUIA_III)) {
					GeneradorDesechosPeligrosos generador = registroGeneradorDesechosFacade.get(registroGeneradorSeleccionado.getId());
					generador.setEsVinculado(true);
					registroGeneradorDesechosFacade.guardar(generador);
					
					VinculoProyectoRgdSuia nuevoVinculo = new VinculoProyectoRgdSuia();
					nuevoVinculo.setIdProyectoRcoa(proyecto.getId());
					nuevoVinculo.setIdRgdSuia(generador.getId());
					vinculoProyectoRgdSuiaFacade.guardar(nuevoVinculo);
				} else {
					RegistroGeneradorDesechosProyectosRcoa generador = registroGeneradorDesechosProyectosRcoaFacade.findById(registroGeneradorSeleccionado.getIdGeneradorProyectoRcoa());
					generador.setProyectoLicenciaCoa(proyecto);
					registroGeneradorDesechosProyectosRcoaFacade.save(generador, loginBean.getUsuario());
				}
			}
		}
		
//		guardarDocumentosSaneamiento();
		guardarDocumentosAutorizacionesPorSector();
		
//		guardarDocCamaroneras();
		
		//Rellenos sanitarios
		guardarDocumentosViabilidadPngids();
		guardarViabilidadTecnica();
		//fin rellenos sanitarios
		
		guardarDatosModificados();
		
		areaResponsable();
		//si el proyecto interseca con las areas que se deben considerar en electrica y es 
		if(esSubactividadSubestacionElectrica) {
			proyecto.setCategorizacion(3);
			proyectoLicenciaCoaFacade.guardar(proyecto);
		} 
		
		if(coordenadasRcoaBean.getIntersecaSnapChocoAndino()){
			enviarCorreo();
		}
				
		if(proyecto.getAreaResponsable() == null)
			msgError.add("Ocurrió un error al guardar la información");
		guardarDocAltoImpacto();
		guardarCoorGeografica();
		guardarCoorImplantacion();
		certificado();
		generarMapa(); //se genera primero el certificado por la url del QR del mapa
		
		oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
		if (oficioCI == null) 
			msgError.add("Ocurrió un error al guardar la información");
		
		RequestContext.getCurrentInstance().update("frmPreliminar:certificadoIntercepcionRcoa");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('certificadoIntercepcionRcoa').show();");
	}
	
	public void eliminarProyecto()
	{
		if(nombreProyectoMsp != null){
			NombreProyectos proyectoNom = nombreProyectoMsp.getNombreProyectos();
			proyectoNom.setProyectoUsado(false);
			nombreProyectosFacade.save(proyectoNom, loginBean.getUsuario());	
			
			nombreProyectoMsp.setEstado(false);
			nombreProyectosMspFacade.save(nombreProyectoMsp, loginBean.getUsuario());
		}	
		
		if(ciiu1 != null & ciiu1.getCatalogoCIUU() != null && ciiu1.getCatalogoCIUU().getRequiereViabilidadPngids() != null){
			List<ViabilidadTecnicaProyecto> lista = viabilidadTecnicaProyectoFacade.buscarPorProyecto(proyecto.getId());
			for(ViabilidadTecnicaProyecto via : lista){
				via.setEstado(false);
				viabilidadTecnicaProyectoFacade.guardar(via, JsfUtil.getLoggedUser());
			}
		} 
		if(ciiu2 != null & ciiu2.getCatalogoCIUU() != null && ciiu2.getCatalogoCIUU().getRequiereViabilidadPngids() != null){
			List<ViabilidadTecnicaProyecto> lista = viabilidadTecnicaProyectoFacade.buscarPorProyecto(proyecto.getId());
			for(ViabilidadTecnicaProyecto via : lista){
				via.setEstado(false);
				viabilidadTecnicaProyectoFacade.guardar(via, JsfUtil.getLoggedUser());
			}
		}
		if(ciiu3 != null & ciiu3.getCatalogoCIUU() != null && ciiu3.getCatalogoCIUU().getRequiereViabilidadPngids() != null){
			List<ViabilidadTecnicaProyecto> lista = viabilidadTecnicaProyectoFacade.buscarPorProyecto(proyecto.getId());
			for(ViabilidadTecnicaProyecto via : lista){
				via.setEstado(false);
				viabilidadTecnicaProyectoFacade.guardar(via, JsfUtil.getLoggedUser());
			}
		}
		
		//se elimina los RGDs vinculados
		try {
			eliminarVinculacionRgd();
			
			VinculoProyectoRgdSuia proyectoVinculado = vinculoProyectoRgdSuiaFacade.getProyectoVinculadoRgd(proyecto.getId());
			if(proyectoVinculado != null) {
				proyectoVinculado.setEstado(false);
				vinculoProyectoRgdSuiaFacade.guardar(proyectoVinculado);
				
				GeneradorDesechosPeligrosos generador = registroGeneradorDesechosFacade.get(proyectoVinculado.getIdRgdSuia());
				generador.setEsVinculado(false); //se cambia a desvinculado xq es un RGD por proyecto
				registroGeneradorDesechosFacade.guardar(generador);
			} else {
				List<RegistroGeneradorDesechosProyectosRcoa> listaRgdProyecto = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId());
				if(listaRgdProyecto != null && !listaRgdProyecto.isEmpty()){
					RegistroGeneradorDesechosProyectosRcoa generadorProyecto = listaRgdProyecto.get(0);
					generadorProyecto.setProyectoLicenciaCoa(null);
					registroGeneradorDesechosProyectosRcoaFacade.save(generadorProyecto, loginBean.getUsuario());
				}
			}
					
			proyecto.setEstado(false);
			proyecto.setEstadoRegistro(false);
			proyecto.setRazonEliminacion("Eliminación por parte del operador ya que no finalizo el registro del proyecto y no inicio las tareas correspondientes para el proyecto");
			proyectoLicenciaCoaFacade.guardar(proyecto);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
		}
		
	}
	
	@SuppressWarnings("unused")
	public void areaResponsable()
	{	
		Area enteAcreditadoProyecto=null;
		enteAcreditadoProyecto=areaService.getAreaEnteAcreditado(3, proyecto.getUsuario().getNombre());
		
		boolean areaMar=false;
		try
		{
		   Integer.valueOf(coordenadasRcoaBean.getUbicacionPrincipal().getCodificacionInec());
		   areaMar=false;
		}
		catch(Exception e) {areaMar=true;}
		
		UbicacionesGeografica provincia=coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica();
		UbicacionesGeografica canton=coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica();	
		
		ArrayList<Integer> listaProv=new ArrayList<Integer>();
		ArrayList<Integer> listaCant=new ArrayList<Integer>();
		Integer totalProvinciasDiferentes=0,totalCantonesDiferentes=0;
		for(UbicacionesGeografica ubi: proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyecto))
		{
			listaProv.add(ubi.getUbicacionesGeografica().getUbicacionesGeografica()==null?0:ubi.getUbicacionesGeografica().getUbicacionesGeografica().getId());
			listaCant.add(ubi.getUbicacionesGeografica().getId());
		}
		Set<Integer> proSet = new HashSet<Integer>(listaProv);
		for(int pro: proSet){
		 totalProvinciasDiferentes++;
		}
		
		Set<Integer> cantSet = new HashSet<Integer>(listaCant);
		for(int cant: cantSet){
		 totalCantonesDiferentes++;
		}

		ciiuArearesponsable=proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
		ValorCalculo valoresFormula = valorCalculoFacade.formulaProyecto(proyecto);
		categoriaXNormativa=0;
		if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuArearesponsable.getCatalogoCIUU())){
			if(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte() != null) {
				categoriaXNormativa = Integer.valueOf(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte().getTipoPermiso());
				tipoAutoridadAmbiental = Integer.valueOf(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte().getEntidadProceso());
			}
		} 
		else {
			List<ProyectoCoaCiuuSubActividades> cargarSubActividades=proyectoCoaCiuuSubActividadesFacade.cargarSubActividades(ciiuArearesponsable);
			if(cargarSubActividades.size()>0 && (cargarSubActividades.get(0).getSubActividad().getTipoPregunta().intValue()==7 || cargarSubActividades.get(0).getSubActividad().getTipoPregunta().intValue()==8))
			{			
				if(ciiuArearesponsable.getSubActividad()!=null && ciiuArearesponsable.getCombinacionSubActividades()==null)
				{
					for(ProyectoCoaCiuuSubActividades lista : cargarSubActividades)
					{
						if(lista.getRespuesta()!=null && !lista.getRespuesta())
						{
							categoriaXNormativa=lista.getSubActividad().getOpcionPermisoNo();
							tipoAutoridadAmbiental=lista.getSubActividad().getEntidadProcesoNo()==null?3:lista.getSubActividad().getEntidadProcesoNo();
						}
						else if(lista.getRespuesta()==null)
						{
							categoriaXNormativa=Integer.valueOf(lista.getSubActividad().getTipoPermiso());
							tipoAutoridadAmbiental=Integer.valueOf(lista.getSubActividad().getEntidadProceso()==null?"3":lista.getSubActividad().getEntidadProceso());
						}
					}

				}
				else
				{
					categoriaXNormativa=Integer.valueOf(ciiuArearesponsable.getCombinacionSubActividades().getTipoPermiso());
					tipoAutoridadAmbiental=Integer.valueOf(ciiuArearesponsable.getCombinacionSubActividades().getEntidadProceso()==null?"3":ciiuArearesponsable.getCombinacionSubActividades().getEntidadProceso());
				}
			}
			else if(ciiuArearesponsable.getSubActividad()!=null)
			{
				if(ciiuArearesponsable.getFinanciadoBancoDesarrollo()!=null && ciiuArearesponsable.getPotabilizacionAgua()!=null)
				{
					SubActividades bancoEstado=subActividadesFacade.actividadParent(ciiuArearesponsable.getIdActividadFinanciadoBancoEstado());
					if(ciiuArearesponsable.getFinanciadoBancoDesarrollo())
						tipoAutoridadAmbiental=bancoEstado.getEntidadProcesoSi()==null?3:bancoEstado.getEntidadProcesoSi();
					else
						tipoAutoridadAmbiental=bancoEstado.getEntidadProcesoNo()==null?3:bancoEstado.getEntidadProcesoNo();

					if(ciiuArearesponsable.getSubActividad().getEsMultiple())
					{
						categoriaXNormativa=Integer.valueOf(ciiuArearesponsable.getSubActividad().getTipoPermiso());					
					}
					else
					{	
						if(ciiuArearesponsable.getValorOpcion())
						{
							categoriaXNormativa=ciiuArearesponsable.getSubActividad().getOpcionPermisoSi();						
						}
						else
						{
							categoriaXNormativa=ciiuArearesponsable.getSubActividad().getOpcionPermisoNo();						
						}
					}
				} else if(ciiuArearesponsable.getIdActividadFinanciadoBancoEstado() != null) {
					//actividades con 2 preguntas una de ellas BDE
					SubActividades subActividadBancoEstado = subActividadesFacade.actividadParent(ciiuArearesponsable.getIdActividadFinanciadoBancoEstado());
					subActividadBancoEstado.setValorOpcion(ciiuArearesponsable.getFinanciadoBancoDesarrollo());
					if(subActividadesCiiuBean.esActividadOperacionRellenosSanitarios(ciiuArearesponsable.getCatalogoCIUU())){
						tipoAutoridadAmbiental = Integer.valueOf(ciiuArearesponsable.getSubActividad().getEntidadProceso()); //no se toma los valores de la pregunta BDE porque es solo informativa
					} else {
						if (ciiuArearesponsable.getFinanciadoBancoDesarrollo()) {
							tipoAutoridadAmbiental = subActividadBancoEstado.getEntidadProcesoSi() == null ? 2 : subActividadBancoEstado.getEntidadProcesoSi();
						} else {
							tipoAutoridadAmbiental = subActividadBancoEstado.getEntidadProcesoNo() == null ? 2 : subActividadBancoEstado.getEntidadProcesoNo();
						}
					}
					
					if(subActividadBancoEstado.getOpcionPermisoSi() != null) {
						if (ciiuArearesponsable.getFinanciadoBancoDesarrollo()) {
							categoriaXNormativa = subActividadBancoEstado.getOpcionPermisoSi();
						} else {
							categoriaXNormativa = subActividadBancoEstado.getOpcionPermisoNo();
						}
					} else {
						if (ciiuArearesponsable.getSubActividad().getEsMultiple()) {
							categoriaXNormativa = Integer.valueOf(ciiuArearesponsable.getSubActividad().getTipoPermiso());
						} else {
							if (ciiuArearesponsable.getValorOpcion()) {
								categoriaXNormativa = ciiuArearesponsable.getSubActividad().getOpcionPermisoSi();
							} else {
								categoriaXNormativa = ciiuArearesponsable.getSubActividad().getOpcionPermisoNo();
							}
						}
					}
				}
 				else if(ciiuArearesponsable.getSubActividad().getEsMultiple())
				{
					categoriaXNormativa=Integer.valueOf(ciiuArearesponsable.getSubActividad().getTipoPermiso());
					tipoAutoridadAmbiental=Integer.valueOf(ciiuArearesponsable.getSubActividad().getEntidadProceso()==null?"3":ciiuArearesponsable.getSubActividad().getEntidadProceso());
				}		
				else
				{	
					if(ciiuArearesponsable.getValorOpcion()==null) {
						categoriaXNormativa=ciiuArearesponsable.getSubActividad().getOpcionPermisoSi();
						tipoAutoridadAmbiental=ciiuArearesponsable.getSubActividad().getEntidadProcesoSi()==null ? 2 :ciiuArearesponsable.getSubActividad().getEntidadProcesoSi();
						
					}
					else
					if(ciiuArearesponsable.getValorOpcion())
					{
						categoriaXNormativa=ciiuArearesponsable.getSubActividad().getOpcionPermisoSi();
						tipoAutoridadAmbiental=ciiuArearesponsable.getSubActividad().getEntidadProcesoSi()==null?3:ciiuArearesponsable.getSubActividad().getEntidadProcesoSi();
					}
					else
					{
						categoriaXNormativa=ciiuArearesponsable.getSubActividad().getOpcionPermisoNo();
						tipoAutoridadAmbiental=ciiuArearesponsable.getSubActividad().getEntidadProcesoNo()==null?3:ciiuArearesponsable.getSubActividad().getEntidadProcesoNo();
						
						
					}
				}
			}
			else
			{
				if(ciiuArearesponsable.getIdActividadFinanciadoBancoEstado() != null 
						&& ciiuArearesponsable.getCatalogoCIUU().getTipoAreaActividadPrincipal() == null) {
					SubActividades subActividadBancoEstado = subActividadesFacade.actividadParent(ciiuArearesponsable.getIdActividadFinanciadoBancoEstado());
					subActividadBancoEstado.setValorOpcion(ciiuArearesponsable.getFinanciadoBancoDesarrollo());

					if (ciiuArearesponsable.getFinanciadoBancoDesarrollo()) {
						categoriaXNormativa = subActividadBancoEstado.getOpcionPermisoSi();
						tipoAutoridadAmbiental = subActividadBancoEstado.getEntidadProcesoSi() == null ? 2 : subActividadBancoEstado.getEntidadProcesoSi();
					} else {
						categoriaXNormativa = subActividadBancoEstado.getOpcionPermisoNo();
						tipoAutoridadAmbiental = subActividadBancoEstado.getEntidadProcesoNo() == null ? 2 : subActividadBancoEstado.getEntidadProcesoNo();
					}
					
				} else {
					if(ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso()!=null && ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso().equals("CERTIFICADO AMBIENTAL"))
					{
						categoriaXNormativa=1;
						tipoAutoridadAmbiental=ciiuArearesponsable.getCatalogoCIUU().getTipoAutoridadAmbiental();
					}		
					if(ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso()!=null && ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso().equals("REGISTRO AMBIENTAL"))
					{
						categoriaXNormativa=2;
						tipoAutoridadAmbiental=ciiuArearesponsable.getCatalogoCIUU().getTipoAutoridadAmbiental();
					}
					if(ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso()!=null && ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso().equals("LICENCIA AMBIENTAL"))
					{
						categoriaXNormativa=3;
						tipoAutoridadAmbiental=ciiuArearesponsable.getCatalogoCIUU().getTipoAutoridadAmbiental();
					}		
					if((valoresFormula.getCatalogoCIUU().getTipoImpacto()!=null && valoresFormula.getCatalogoCIUU().getTipoImpacto().equals("IMPACTO ALTO")) || (ciiuArearesponsable.getCatalogoCIUU().getTipoImpacto()!=null && ciiuArearesponsable.getCatalogoCIUU().getTipoImpacto().equals("IMPACTO ALTO")))
					{
						categoriaXNormativa=4;
						tipoAutoridadAmbiental=ciiuArearesponsable.getCatalogoCIUU().getTipoAutoridadAmbiental();
					}
				}
				
			}
		}
		
		List<ProyectoLicenciaCuaCiuu> listaActividades = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
		if(listaActividades.size() > 1) {
			CatalogoCIUU catalogoPrincipal = listaActividades.get(0).getCatalogoCIUU();
			if(catalogoPrincipal.getTipoAreaActividadPrincipal() != null) {
				tipoAutoridadAmbiental = catalogoPrincipal.getTipoAreaActividadPrincipal(); //actividades E3821.02, E3821.02.01
			} else if(ciiuArearesponsable.getIdActividadFinanciadoBancoEstado() == null) {
				//Cuando el proyecto tiene más de una actividad económica y cualquiera de las actividades seleccionadas 
				//es financiada por el Banco del Estado, el proyecto se direcciona a Dirección Zonal
				boolean existeFinanciamientoBde = false;
				for (ProyectoLicenciaCuaCiuu b : listaActividades) {
					if(b.getFinanciadoBancoDesarrollo() != null && b.getFinanciadoBancoDesarrollo()
							&& b.getCatalogoCIUU().getTipoAreaActividadPrincipal() == null) { //cuando se selecciona financiado para las actividades E3821.02, E3821.02.01 no aplica el cambio de autoridad
						existeFinanciamientoBde = true;
						break;
					}
				}
				
				if(existeFinanciamientoBde) {
					tipoAutoridadAmbiental = 2; //2='MAE ZONAL'
				}
			}
		}
		
		//cuando el proyecto involucra a más de 2 zonales 
		if (totalProvinciasDiferentes > 1 ) {
			ArrayList<Integer> listaZonales = new ArrayList<Integer>();
			try {
				for (int id : proSet) {
					UbicacionesGeografica ubicacion = ubicacionfacade.buscarPorId(id);
					if (ubicacion.getAreaCoordinacionZonal() != null)
						listaZonales.add(ubicacion.getAreaCoordinacionZonal().getId());
					else if (ubicacion.getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS))
						listaZonales.add(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
				}
				
				Set<Integer> setZonales = new HashSet<Integer>(listaZonales);
				
				if(setZonales.size() > 2) {
					if(proyecto.getTransportaSustanciasQuimicas() && subActividadesCiiuBean.esActividadExcepcionTransporteSustanciasQuimicas(ciiuArearesponsable.getCatalogoCIUU())) {
						// si el proyecto realiza transporte de sustancias y es H4923.01, H4923.01.01, H5011.01, H5011.01.01, H5120.01, H5120.01.01
						// la asignación se realiza normal
					} else {
						if (categoriaXNormativa > 0) {
							proyecto.setCategorizacion(categoriaXNormativa);
							valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
							valorCalculoFacade.guardar(valoresFormula);
						}
						proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
						proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
						proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}
				
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		
		if(provincia.getNombre().equals("GALAPAGOS"))
		{
			if(categoriaXNormativa>0)
			{
				proyecto.setCategorizacion(categoriaXNormativa);				
				valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				valorCalculoFacade.guardar(valoresFormula);
			}	
			proyecto.setAreaResponsable(areaService.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS));
			proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());				
			proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		}	
		if(areaMar &&  tipoAutoridadAmbiental==1)
		{
			if(categoriaXNormativa>0)
			{
				proyecto.setCategorizacion(categoriaXNormativa);				
				valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				valorCalculoFacade.guardar(valoresFormula);
			}	
			proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
			proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());				
			proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		}
		
		//validar si el proyecto interseca con mar para establecer el área pendiente de asignación
		areaPendienteAsignar = false;
		for(UbicacionesGeografica ubi:coordenadasRcoaBean.getUbicacionesSeleccionadas()) {
			if(ubi.getIdRegion() != null 
					&& ubi.getIdRegion().equals(6) 
					&& !ubi.getNombre().toUpperCase().contains("INSULAR")) {
				areaPendienteAsignar = true;
				
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}
				
				proyecto.setAreaResponsable(areaFacade.getAreaPorAreaAbreviacion("ND"));
				proyecto.setAreaInventarioForestal(null);
				proyecto.setEstadoAsignacionArea(2);
				proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
				
				return;
			}
		}
		
		if(areaMar &&  tipoAutoridadAmbiental>1)
		{
			if(categoriaXNormativa>0)
			{
				proyecto.setCategorizacion(categoriaXNormativa);				
				valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				valorCalculoFacade.guardar(valoresFormula);
			}	
			if(coordenadasRcoaBean.getUbicacionPrincipal().getNombre().toUpperCase().contains("INSULAR")) //si es mar insular
				proyecto.setAreaResponsable(areaService.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS));
			else
				proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
			
			proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());				
			proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		}		
		
		Area zonal = areaService.getAreaCoordinacionZonal(coordenadasRcoaBean.getUbicacionOficinaTecnica()); 
//		cambio de las consideraciones 09-11-20020
//		Boolean esActividadZonaRural = false; 
//		String actividadZonaRural= Constantes.getActividadesZonaRural();				
//		String[] actividadesZonaRuralArray = actividadZonaRural.split(",");
//		
//		for (String actividadRural : actividadesZonaRuralArray) {
//			if (ciiuArearesponsable.getCatalogoCIUU().getCodigo().equals(actividadRural)){
//				esActividadZonaRural = true;
//				if(proyecto.getTipoPoblacion().getId()==TipoPoblacion.TIPO_POBLACION_RURAL)
//					categoriaXNormativa=1;
//				else
//					categoriaXNormativa=2;
//				break;
//			}				
//		}
		
		if(totalProvinciasDiferentes==2 && tipoAutoridadAmbiental != 1 
				&& !(proyecto.getInterecaSnap() || proyecto.getInterecaBosqueProtector() || proyecto.getInterecaPatrimonioForestal() || proyecto.getInterecaRamsar())) {
			proyecto.setAreaResponsable(zonal);
			proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());			
			if(categoriaXNormativa>0)
			{
				proyecto.setCategorizacion(categoriaXNormativa);				
				valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				valorCalculoFacade.guardar(valoresFormula);
			}
			proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		}
		
		if(totalProvinciasDiferentes>2 && tipoAutoridadAmbiental != 1)
		{
			proyecto.setAreaResponsable(zonal);
			proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());				
			if(categoriaXNormativa>0)
			{
				proyecto.setCategorizacion(categoriaXNormativa);				
				valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				valorCalculoFacade.guardar(valoresFormula);
			}
			proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		}
		
		esActividadExtractiva = ciiuArearesponsable.getCatalogoCIUU().getBloques();
		
		if(proyecto.getInterecaSnap() || proyecto.getInterecaBosqueProtector() || proyecto.getInterecaPatrimonioForestal() || proyecto.getInterecaRamsar())
		{
			//Validar categorizacion por interseccion con capas especificas. Se hace el llamado a la función porque se puede requerir el cambio de categorizacion
			Boolean saltar = validarCategorizacionPorInterseccion(valoresFormula, zonal, enteAcreditadoProyecto, provincia);
			if(saltar) {
				return;
			}
			
			if(categoriaXNormativa>0)
			{
				// si las coordenadas intersecan y la subactividad es Líneas de Transmisión menor o igual a 230 KV con una longitud de 10 km y Subestaciones, se debe cambiar el tipo a licencia
				categoriaXNormativa= esSubactividadSubestacionElectrica ? 3 :categoriaXNormativa;
				proyecto.setCategorizacion(categoriaXNormativa);				
				valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				valorCalculoFacade.guardar(valoresFormula);
			}			
			if(tipoAutoridadAmbiental==1)
			{
				proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
				proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());				
				proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			}
			if(tipoAutoridadAmbiental==2 || tipoAutoridadAmbiental==3 || tipoAutoridadAmbiental==4)
			{
				proyecto.setAreaResponsable(zonal);
				proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
				proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			}									
				
		}
		else {
			//Validar categorizacion por interseccion con capas especificas
			Boolean saltar = validarCategorizacionPorInterseccion(valoresFormula, zonal, enteAcreditadoProyecto, provincia);
			if(saltar) {
				return;
			}
		}
		
		
		if(ciiuArearesponsable.getSubActividad()!=null)
		{
			if(categoriaXNormativa!=null &&categoriaXNormativa>0)
			{
				proyecto.setCategorizacion(categoriaXNormativa);				
				valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				valorCalculoFacade.guardar(valoresFormula);
			}
			
			if(ciiuArearesponsable.getCatalogoCIUU().getSaneamiento())
			{				
				if(tipoAutoridadAmbiental==2)
				{
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;					
				}
				else
				{
					if(enteAcreditadoProyecto!=null)
					{
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO"))
						{
							//El proyecto es promovido por un Gobierno municipal o metropolitano la Autoridad Ambiental competente debe ser la Dirección Provincial 
//							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO"))
						{
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;		
						}
					}
				}
			}
			//mtop
			if(ciiuArearesponsable.getCatalogoCIUU().getMtop())
			{	
				Boolean esUsuarioMtop = false;
				String usuariomtop= Constantes.getUsuarioMtop();				
				String[] usuarioMtopArray = usuariomtop.split(",");				
				for (String usuarioMt : usuarioMtopArray) {
					if (proyecto.getUsuario().getNombre().contains(usuarioMt)){
						esUsuarioMtop = true;
						break;
					}				
				}				
				if (esUsuarioMtop) {
					//si el usuario que registra el proyecto es MTOP gestionan las DPs
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
					proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}
			//LIBRES APROVECHAMIENTOS
			if(ciiuArearesponsable.getCatalogoCIUU().getLibreAprovechamiento())
			{
				if(totalCantonesDiferentes>=2)
				{
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(zonal);
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}

				if(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio() != null)
				{	
					if(enteAcreditadoProyecto!=null)
					{
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO"))
						{
							if(tipoAutoridadAmbiental==1)
								proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));							
							else if(tipoAutoridadAmbiental==2 || tipoAutoridadAmbiental == 4)
								proyecto.setAreaResponsable(zonal);								
							else if(tipoAutoridadAmbiental==3)
								proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
							
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO"))
						{
							if(tipoAutoridadAmbiental==1)
								proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));							
							if(tipoAutoridadAmbiental==2 || tipoAutoridadAmbiental==3)
								proyecto.setAreaResponsable(zonal);
							if(tipoAutoridadAmbiental == 4)
								proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio());
							
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					}
					else
					{
						if(tipoAutoridadAmbiental==1)
							proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));							
						if(tipoAutoridadAmbiental==2)
							proyecto.setAreaResponsable(zonal);								
						if(tipoAutoridadAmbiental==3)
							proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
						if(tipoAutoridadAmbiental == 4)
							proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio());
						
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}
				else
				{
//					if(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()!=null && coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado().getTipoEnteAcreditado().equals("MUNICIPIO"))
					if(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio()!=null && coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio().getTipoEnteAcreditado().equals("MUNICIPIO"))
						proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio());
					else
						proyecto.setAreaResponsable(zonal);
					
					proyecto.setAreaInventarioForestal(zonal);
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}
			
			//area responsable msp
			
			if(usuarioMsp){
				if(categoriaXNormativa>0)
				{
					proyecto.setCategorizacion(categoriaXNormativa);				
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}				
				if(otroMsp){	

					if(totalProvinciasDiferentes==1 && totalCantonesDiferentes>=2)
					{
//						proyecto.setAreaResponsable(zonal);
						proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
					
//					if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("GUAYAQUIL") || canton.getNombre().equals("CUENCA"))
					if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("CUENCA"))
					{
						proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;	
					}
					
					Area areaResp = areaService.getAreaGadProvincial(3 ,provincia);
					if(areaResp == null){
						areaResp = zonal;
					}
					
					proyecto.setAreaResponsable(areaResp);
					proyecto.setAreaInventarioForestal(zonal);
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;					
				}else{					
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}					
			}
			//fin msp
			
			if(tipoAutoridadAmbiental==1)
				proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));							
			if(tipoAutoridadAmbiental==2)
				proyecto.setAreaResponsable(zonal);								
			if(tipoAutoridadAmbiental==3)	
			{
				if(totalProvinciasDiferentes==1 && totalCantonesDiferentes>=2)
				{
					if (enteAcreditadoProyecto != null && proyecto.getUsuarioCreacion().equals(enteAcreditadoProyecto.getIdentificacionEnte())){
						proyecto.setAreaResponsable(zonal);
						proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;	
					}else{
						proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}				
				
//				if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("GUAYAQUIL") || canton.getNombre().equals("CUENCA"))
				if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("CUENCA"))
				{
					if (enteAcreditadoProyecto != null && proyecto.getUsuarioCreacion().equals(enteAcreditadoProyecto.getIdentificacionEnte())){
						
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO")){
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;		
						}else{
							proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
						
					}else{
						proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}					
				}				
				
				if(enteAcreditadoProyecto!=null)
				{
					if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO"))
					{
						//El proyecto es promovido por un Gobierno municipal o metropolitano la Autoridad Ambiental competente debe ser la Dirección Provincial 
//						proyecto.setAreaResponsable(zonal);
						proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
					if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO"))
					{
						proyecto.setAreaResponsable(zonal);
						proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;		
					}
				}				
				
				proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
			}
			
			if(tipoAutoridadAmbiental==5)	
			{
				proyecto.setAreaResponsable(areaService.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS));
				proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());				
				proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			}
			
			if(tipoAutoridadAmbiental==6)	
			{
				proyecto.setAreaResponsable(areaFacade.getAreaPorAreaAbreviacion("ND"));
				proyecto.setAreaInventarioForestal(null);
				proyecto.setEstadoAsignacionArea(2);				
				proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			}
			
			if ((catalogoCamaronera_ciuu1 || catalogoCamaronera_ciuu2 || catalogoCamaronera_ciuu3) && zona != null && Constantes.getActividadesCamaroneras().contains(ciiuArearesponsable.getCatalogoCIUU().getCodigo())){
//		          Validación de coordenadas PLAYA o ALTA						
					if(tipoAutoridadAmbiental==1)
					{
						if((provincia.getId() == 23 || provincia.getId() == 11 || provincia.getId() == 20 || provincia.getId()==15 || provincia.getId()==27 || provincia.getId()==28)) {
							if(zona.equals("PLAYA")) {
								proyecto.setAreaResponsable(areaService.getAreaGadProvincial(6, coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica())==null?areaService.getAreaCoordinacionZonal(coordenadasRcoaBean.getUbicacionOficinaTecnica()):areaService.getAreaGadProvincial(3, coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica()));
								proyecto.setZona_camaronera(zona);
//								proyecto.setAcuerdo_camaronera(coordenadasRcoaBean.getNam());		
								if(catalogoCamaronera_ciuu1) {proyecto.setCategorizacion(Integer.parseInt(subActividad1.getTipoPermiso().toString()));}else if(catalogoCamaronera_ciuu2) {proyecto.setCategorizacion(Integer.parseInt(subActividad2.getTipoPermiso().toString()));}else if(catalogoCamaronera_ciuu3){proyecto.setCategorizacion(Integer.parseInt(subActividad3.getTipoPermiso().toString()));}
								proyecto.setCategorizacion(Integer.parseInt(subActividad1.getTipoPermiso().toString()));
							}else if (zona.equals("ALTA")) {
								
//								if(canton.getNombre().equals("GUAYAQUIL")){
//									proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());							
//								}else{						
									proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica())==null?areaService.getAreaCoordinacionZonal(coordenadasRcoaBean.getUbicacionOficinaTecnica()):areaService.getAreaGadProvincial(3, coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica()));													
//								}
								proyecto.setZona_camaronera(zona);
//								proyecto.setAcuerdo_camaronera(coordenadasRcoaBean.getNam());					
								if(catalogoCamaronera_ciuu1) {proyecto.setCategorizacion(Integer.parseInt(subActividad1.getTipoPermiso().toString()));}else if(catalogoCamaronera_ciuu2) {proyecto.setCategorizacion(Integer.parseInt(subActividad2.getTipoPermiso().toString()));}else if(catalogoCamaronera_ciuu3){proyecto.setCategorizacion(Integer.parseInt(subActividad3.getTipoPermiso().toString()));}
							}else if(zona.equals("MIXTA")){
								
								proyecto.setAreaResponsable(zonal);
								
								proyecto.setZona_camaronera(zona);
								if (catalogoCamaronera_ciuu1) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad1.getTipoPermiso().toString()));
								} else if (catalogoCamaronera_ciuu2) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad2.getTipoPermiso().toString()));
								} else if (catalogoCamaronera_ciuu3) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad3.getTipoPermiso().toString()));
								}								
							}										
							proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());	
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}	
						
						if(zona.equals("MIXTA")){
							proyecto.setAreaResponsable(zonal);
							
							proyecto.setZona_camaronera(zona);
							if (catalogoCamaronera_ciuu1) {
								proyecto.setCategorizacion(Integer.parseInt(subActividad1.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu2) {
								proyecto.setCategorizacion(Integer.parseInt(subActividad2.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu3) {
								proyecto.setCategorizacion(Integer.parseInt(subActividad3.getTipoPermiso().toString()));
							}
							
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}						
					}	
					if(tipoAutoridadAmbiental==2 || tipoAutoridadAmbiental==3 || tipoAutoridadAmbiental==4)
					{
						if((provincia.getId() == 23 || provincia.getId() == 11 || provincia.getId() == 20 || provincia.getId()==15 || provincia.getId()==27 || provincia.getId()==28)) {
							if (zona.equals("PLAYA")) {
								proyecto.setAreaResponsable(areaService
										.getAreaGadProvincial(6, coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica()) == null 
										? areaService.getAreaCoordinacionZonal(coordenadasRcoaBean.getUbicacionOficinaTecnica())
										: areaService.getAreaGadProvincial(3, coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica()));
								proyecto.setZona_camaronera(zona);
//								proyecto.setAcuerdo_camaronera(coordenadasRcoaBean.getNam());
								if (catalogoCamaronera_ciuu1) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad1.getTipoPermiso().toString()));
								} else if (catalogoCamaronera_ciuu2) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad2.getTipoPermiso().toString()));
								} else if (catalogoCamaronera_ciuu3) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad3.getTipoPermiso().toString()));
								}
							} else if (zona.equals("ALTA")) {
								
//								if(canton.getNombre().equals("GUAYAQUIL")){
//									proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());						
//									
//								}else{
									proyecto.setAreaResponsable(areaService
											.getAreaGadProvincial(3, coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica()) == null 
											? areaService.getAreaCoordinacionZonal(coordenadasRcoaBean.getUbicacionOficinaTecnica())
											: areaService.getAreaGadProvincial(3,coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica()));
									
//								}
								proyecto.setZona_camaronera(zona);
//								proyecto.setAcuerdo_camaronera(coordenadasRcoaBean.getNam());
								if (catalogoCamaronera_ciuu1) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad1.getTipoPermiso().toString()));
								} else if (catalogoCamaronera_ciuu2) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad2.getTipoPermiso().toString()));
								} else if (catalogoCamaronera_ciuu3) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad3.getTipoPermiso().toString()));
								}
							}else if(zona.equals("MIXTA")){
								
								proyecto.setAreaResponsable(zonal);
								
								proyecto.setZona_camaronera(zona);
								if (catalogoCamaronera_ciuu1) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad1.getTipoPermiso().toString()));
								} else if (catalogoCamaronera_ciuu2) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad2.getTipoPermiso().toString()));
								} else if (catalogoCamaronera_ciuu3) {
									proyecto.setCategorizacion(Integer.parseInt(subActividad3.getTipoPermiso().toString()));
								}								
							}
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}	
						
						if(zona.equals("MIXTA")){
							proyecto.setAreaResponsable(zonal);
							
							proyecto.setZona_camaronera(zona);
							if (catalogoCamaronera_ciuu1) {
								proyecto.setCategorizacion(Integer.parseInt(subActividad1.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu2) {
								proyecto.setCategorizacion(Integer.parseInt(subActividad2.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu3) {
								proyecto.setCategorizacion(Integer.parseInt(subActividad3.getTipoPermiso().toString()));
							}
							
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					}
				} 
			
			proyecto.setAreaInventarioForestal(zonal);
			proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		}
		else
		{		
			if(ciiuArearesponsable.getCatalogoCIUU().getScoutDrilling())
			{
				categoriaXNormativa=2;
				proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
				proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
				proyecto.setCategorizacion(categoriaXNormativa);
				proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				valorCalculoFacade.guardar(valoresFormula);				
				return;
			}
			
			if(ciiuArearesponsable.getCatalogoCIUU().getSaneamiento())
			{
				if(categoriaXNormativa>0)
				{
					proyecto.setCategorizacion(categoriaXNormativa);				
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}
//				if(ciiuArearesponsable.getSaneamiento())
//				{
//					proyecto.setAreaResponsable(zonal);
//					proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
//					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
//					return;					
//				}
//				else
//				{
					
//					if(tipoAutoridadAmbiental==2)
//					{
//						proyecto.setAreaResponsable(areaService.getAreaDireccionProvincialPorUbicacion(2, coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica()));
//						proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
//						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
//						return;						
//					}
//					if(tipoAutoridadAmbiental==3 || tipoAutoridadAmbiental==4)
//					{
						if(enteAcreditadoProyecto!=null)
						{
							if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO"))
							{
								if(tipoAutoridadAmbiental==1)
								{
									proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
									proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
									proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
									return;
								}
								if(tipoAutoridadAmbiental==2)
								{
									proyecto.setAreaResponsable(zonal);
									proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
									proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
									return;						
								}
								else
								{
									proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
									proyecto.setAreaInventarioForestal(zonal);
									proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
									return;
								}
							}
							if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO"))
							{
								proyecto.setAreaResponsable(zonal);
								proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
								proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
								return;		
							}
						}
						else
						{
							if(tipoAutoridadAmbiental==1)
							{
								proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
								proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
								proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
								return;
							}
							if(tipoAutoridadAmbiental==2)
							{
								proyecto.setAreaResponsable(zonal);
								proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
								proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
								return;						
							}
							if(tipoAutoridadAmbiental==3)
							{
//								if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("GUAYAQUIL") || canton.getNombre().equals("CUENCA"))
								if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("CUENCA"))
								{
									proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
									proyecto.setAreaInventarioForestal(zonal);
									proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
									return;						
																	
								}else{
								
									proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
									proyecto.setAreaInventarioForestal(zonal);
									proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
									return;
								}
							}
						}
											
//					}
//				}
			}
			
//			if(esActividadZonaRural) cambio de las consideraciones 09-11-2020
//			{
//				if(categoriaXNormativa>0)
//				{
//					proyecto.setCategorizacion(categoriaXNormativa);				
//					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
//					valorCalculoFacade.guardar(valoresFormula);
//				}
//				if(enteAcreditadoProyecto!=null)
//				{		
//					if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO"))
//					{
//						proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
//						proyecto.setAreaInventarioForestal(zonal);
//						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
//						return;
//					}
//					if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO"))
//					{
//						proyecto.setAreaResponsable(zonal);							
//						proyecto.setAreaInventarioForestal(zonal);
//						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
//						return;
//					}
//				}
//				else
//				{
//					proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
//					proyecto.setAreaInventarioForestal(zonal);					
//					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
//					return;
//				}
//			}
			
			//hidrocarburos con bloques
			if(ciiuArearesponsable.getCatalogoCIUU().getBloques())
			{
				esActividadExtractiva=true;
				if(categoriaXNormativa>0)
				{
					proyecto.setCategorizacion(categoriaXNormativa);				
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso()!=null && ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso().equals("LICENCIA AMBIENTAL"))
				{
//					categoriaXNormativa=3;
					proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
					proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
					proyecto.setCategorizacion(categoriaXNormativa);
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
					return;
				}
				else					
				{					
					if(tipoAutoridadAmbiental==1)
					{
						proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
						proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
					if(tipoAutoridadAmbiental==2)
					{
						proyecto.setAreaResponsable(zonal);
						proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;						
					}
//					if(tipoAutoridadAmbiental==3)
//					{
//						proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
//						proyecto.setAreaInventarioForestal(areaService.getAreaDireccionProvincialPorUbicacion(2, coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica()));
//						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
//						return;						
//					}
				}
			} else 
				esActividadExtractiva=false;
			
			//mtop
			if(ciiuArearesponsable.getCatalogoCIUU().getMtop())
			{
				if(categoriaXNormativa>0)
				{
					proyecto.setCategorizacion(categoriaXNormativa);				
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}
				
				Boolean esUsuarioMtop = false;
				String usuariomtop= Constantes.getUsuarioMtop();				
				String[] usuarioMtopArray = usuariomtop.split(",");
				
				for (String usuarioMt : usuarioMtopArray) {
					if (proyecto.getUsuario().getNombre().contains(usuarioMt)){
						esUsuarioMtop = true;
						break;
					}				
				}
				
				if (esUsuarioMtop) {
					//si el usuario que registra el proyecto es MTOP gestionan las DPs
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
					proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				} else {
					if(enteAcreditadoProyecto!=null)
					{
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO"))
						{
							//El proyecto es promovido por un Gobierno municipal o metropolitano la Autoridad Ambiental competente debe ser la Dirección Provincial 
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO"))
						{
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;		
						}
					}
					
					if(tipoAutoridadAmbiental==3) {
						proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}
			}
			//gestion Concurrente
			if(ciiuArearesponsable.getCatalogoCIUU().getGestionConcurrente())
			{
				if(categoriaXNormativa>0)
				{
					proyecto.setCategorizacion(categoriaXNormativa);				
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}
				CatalogoCIUUConcurrente concurrenteMunicipio = null;
//				if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("GUAYAQUIL") || canton.getNombre().equals("CUENCA"))
				if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("CUENCA"))
					concurrenteMunicipio = catalogoCIUUConcurrenteFacade.gestionConcurrente(canton.getNombre(), ciiuArearesponsable.getCatalogoCIUU());
				
				CatalogoCIUUConcurrente concurrenteGad = catalogoCIUUConcurrenteFacade.gestionConcurrente(provincia.getNombre(), ciiuArearesponsable.getCatalogoCIUU()); 
				if(concurrenteMunicipio!=null)
				{
					if(enteAcreditadoProyecto!=null)
					{
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO") && concurrenteMunicipio.getAreaResponsable().getTipoEnteAcreditado().equals("MUNICIPIO"))
						{
							proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;						
						}
						else
						{
							//proyecto.setAreaResponsable(concurrenteMunicipio.getAreaResponsable());
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					}
					else
					{
						proyecto.setAreaResponsable(concurrenteMunicipio.getAreaResponsable());
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}
				else if(concurrenteGad!=null)
				{
					if(enteAcreditadoProyecto!=null)
					{
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO") && concurrenteGad.getAreaResponsable().getTipoEnteAcreditado().equals("GOBIERNO"))
						{
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
						else
						{
							proyecto.setAreaResponsable(concurrenteGad.getAreaResponsable());
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					}
					else
					{
						proyecto.setAreaResponsable(concurrenteGad.getAreaResponsable()); 
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}
				else
				{
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(zonal);
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}
			//LIBRES APROVECHAMIENTOS
			if(ciiuArearesponsable.getCatalogoCIUU().getLibreAprovechamiento())
			{
				if(categoriaXNormativa>0)
				{
					proyecto.setCategorizacion(categoriaXNormativa);				
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}
				if(totalCantonesDiferentes>=2)
				{
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(zonal);
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
//				if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("GUAYAQUIL") || canton.getNombre().equals("CUENCA"))
				if(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio() != null)
				{	
					if(enteAcreditadoProyecto!=null)
					{
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO"))
						{
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO"))
						{
							proyecto.setAreaResponsable(zonal);							
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					}
					else
					{
						if(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio()!=null && coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio().getTipoEnteAcreditado().equals("MUNICIPIO"))
							proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
						else
							proyecto.setAreaResponsable(zonal);
						
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}
				else
				{
//					proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio());
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(zonal);
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}
			
			//area responsable msp
			
			if(usuarioMsp){
				if(categoriaXNormativa>0)
				{
					proyecto.setCategorizacion(categoriaXNormativa);				
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}				
				if(otroMsp){		
					
					if(totalProvinciasDiferentes==1 && totalCantonesDiferentes>=2)
					{
//						proyecto.setAreaResponsable(zonal);
						proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
					
//					if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("GUAYAQUIL") || canton.getNombre().equals("CUENCA"))
					if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("CUENCA"))
					{
						proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;	
					}
					
					Area areaResp = areaService.getAreaGadProvincial(3 ,provincia);
					if(areaResp == null){
						areaResp = zonal;
					}
					
					proyecto.setAreaResponsable(areaResp);
					proyecto.setAreaInventarioForestal(zonal);
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;					
				}else{					
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}					
			}
			//fin msp			
			
			if(tipoAutoridadAmbiental==1)
			{
				if(categoriaXNormativa>0)
				{
					proyecto.setCategorizacion(categoriaXNormativa);				
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}
				proyecto.setAreaResponsable(areaService.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
				proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
				proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			}
			else if(tipoAutoridadAmbiental==2)
			{
				if(categoriaXNormativa>0)
				{
					proyecto.setCategorizacion(categoriaXNormativa);				
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}
				proyecto.setAreaResponsable(zonal);
				proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
				proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			}
			else if(tipoAutoridadAmbiental==3)
			{
				if(categoriaXNormativa>0)
				{
					proyecto.setCategorizacion(categoriaXNormativa);				
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}
//				if(totalProvinciasDiferentes==1 && totalCantonesDiferentes>=2)
//				{
//					proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
//					proyecto.setAreaInventarioForestal(zonal);
//					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
//					return;
//				}				
//				if (coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null)
//				{					
//					proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
//					proyecto.setAreaInventarioForestal(zonal);
//					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
//					return;
//				}
//				else
//				{
					if(enteAcreditadoProyecto!=null)
					{
						String ubicacionEsMunicipio="";
						if(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()!=null)
							ubicacionEsMunicipio=coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado().getTipoEnteAcreditado();
						else
							ubicacionEsMunicipio=coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio()==null?"":coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio().getTipoEnteAcreditado();
						
						if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO") && (ubicacionEsMunicipio.equals("MUNICIPIO")))
						{

							proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						} else if (proyecto.getUsuarioCreacion().equals(enteAcreditadoProyecto.getIdentificacionEnte())) {
							//el proyecto es promovido por un Gobierno provincial, la autoridad competente debe ser la Dirección Provincial
							
//							Area areaResponsableAux = zonal;
//							proyecto.setAreaResponsable(areaResponsableAux);
//							proyecto.setAreaInventarioForestal(areaResponsableAux);
//							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
//							return;
							
							if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO"))
							{
								proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
								proyecto.setAreaInventarioForestal(zonal);
								proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
								return;
							}
							if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO"))
							{
								proyecto.setAreaResponsable(zonal);							
								proyecto.setAreaInventarioForestal(zonal);
								proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
								return;
							}
						}
						else
						{
							proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					}
					else
					{
						if(totalProvinciasDiferentes==1 && totalCantonesDiferentes>=2)
						{
							proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}	
						
						
//						if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("GUAYAQUIL") || canton.getNombre().equals("CUENCA"))
						if(canton.getNombre().equals("DISTRITO METROPOLITANO DE QUITO") || canton.getNombre().equals("CUENCA"))
						{
							proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
							proyecto.setAreaInventarioForestal(zonal);
							proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;					
						}
						else
						{
//							if(totalProvinciasDiferentes==1 && totalCantonesDiferentes>=2)
//							{
//								proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
//								proyecto.setAreaInventarioForestal(zonal);
//								proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
//								return;
//							}				
							if (coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null)
							{					
								proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
								proyecto.setAreaInventarioForestal(zonal);
								proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
								return;
							}
							else
							{
	//							proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia)==null?zonal:areaService.getAreaGadProvincial(3, provincia));
								proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado()==null?zonal:coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
								proyecto.setAreaInventarioForestal(zonal);
								proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
								return;
							}
						}
					}
//				}
			}
			else if(tipoAutoridadAmbiental==4)
			{
				if(categoriaXNormativa>0)
				{
					proyecto.setCategorizacion(categoriaXNormativa);				
					valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					valorCalculoFacade.guardar(valoresFormula);
				}
				if(enteAcreditadoProyecto!=null)
				{
					if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO"))
					{
						proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
					else
					{
						proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;						
					}
				}
				else
				{
					proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditadomunicipio());
					proyecto.setAreaInventarioForestal(zonal);
					proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}
		}
		
	}
	
	public void guardarCoorGeografica()
	{
		if(coordenadasRcoaBean.getUploadedFileGeo()!=null)
		{
			DocumentosCOA documento = new DocumentosCOA();	        	
			documento.setContenidoDocumento(coordenadasRcoaBean.getUploadedFileGeo().getContents());
			documento.setExtencionDocumento(".xls");		
			documento.setTipo("application/vnd.ms-excel");
			documento.setIdTabla(proyecto.getId());	       		
			documento.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
			documento.setNombreDocumento("Coordenadas área geográfica (Anexo 1).xls");
			documento.setProyectoLicenciaCoa(proyecto);

			try {
				documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "Coordenadas_Geográficas", 1L, documento, TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA);
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void guardarCoorImplantacion()
	{
		if(coordenadasRcoaBean.getUploadedFileImpl()!=null)
		{
			DocumentosCOA documento = new DocumentosCOA();	        	
			documento.setContenidoDocumento(coordenadasRcoaBean.getUploadedFileImpl().getContents());
			documento.setExtencionDocumento(".xls");		
			documento.setTipo("application/vnd.ms-excel");
			documento.setIdTabla(proyecto.getId());	       		
			documento.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
			documento.setNombreDocumento("Coordenadas área de implantación.xls");
			documento.setProyectoLicenciaCoa(proyecto);

			try {
				documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "Coordenadas_Implantación", 1L, documento, TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION);
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {		
		documentoAltoImpacto=new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoAltoImpacto.setContenidoDocumento(contenidoDocumento);
		documentoAltoImpacto.setNombreDocumento(event.getFile().getFileName());
		documentoAltoImpacto.setExtencionDocumento(".pdf");		
		documentoAltoImpacto.setTipo("application/pdf");
		documentoAltoImpacto.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());		
	}
	
	public void uploadListenerGeneticCiiu1(FileUploadEvent event) {		
		documentoGeneticoCiiu1=new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoGeneticoCiiu1.setContenidoDocumento(contenidoDocumento);
		documentoGeneticoCiiu1.setNombreDocumento(event.getFile().getFileName());
		documentoGeneticoCiiu1.setExtencionDocumento(".pdf");		
		documentoGeneticoCiiu1.setTipo("application/pdf");
		documentoGeneticoCiiu1.setNombreTabla(ProyectoLicenciaCuaCiuu.class.getSimpleName());		
	}
	
	public void uploadListenerGeneticCiiu2(FileUploadEvent event) {		
		documentoGeneticoCiiu2=new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoGeneticoCiiu2.setContenidoDocumento(contenidoDocumento);
		documentoGeneticoCiiu2.setNombreDocumento(event.getFile().getFileName());
		documentoGeneticoCiiu2.setExtencionDocumento(".pdf");		
		documentoGeneticoCiiu2.setTipo("application/pdf");
		documentoGeneticoCiiu2.setNombreTabla(ProyectoLicenciaCuaCiuu.class.getSimpleName());		
	}
	
	public void uploadListenerGeneticCiiu3(FileUploadEvent event) {		
		documentoGeneticoCiiu3=new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoGeneticoCiiu3.setContenidoDocumento(contenidoDocumento);
		documentoGeneticoCiiu3.setNombreDocumento(event.getFile().getFileName());
		documentoGeneticoCiiu3.setExtencionDocumento(".pdf");		
		documentoGeneticoCiiu3.setTipo("application/pdf");
		documentoGeneticoCiiu3.setNombreTabla(ProyectoLicenciaCuaCiuu.class.getSimpleName());		
	}
	
	public void uploadListenerDocSectorial(FileUploadEvent event) {		
		documentoDocSectorial=new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoDocSectorial.setContenidoDocumento(contenidoDocumento);
		documentoDocSectorial.setNombreDocumento(event.getFile().getFileName());
		documentoDocSectorial.setExtencionDocumento(".pdf");		
		documentoDocSectorial.setTipo("application/pdf");
		documentoDocSectorial.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());		
	}
	
	public void uploadListenerDocFrontera(FileUploadEvent event) {		
		documentoDocFrontera=new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoDocFrontera.setContenidoDocumento(contenidoDocumento);
		documentoDocFrontera.setNombreDocumento(event.getFile().getFileName());
		documentoDocFrontera.setExtencionDocumento(".pdf");		
		documentoDocFrontera.setTipo("application/pdf");
		documentoDocFrontera.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());		
	}
	
	public void uploadListenerDocCamaronera(FileUploadEvent event) {		
		documentoDocCamaronera = new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoDocCamaronera.setContenidoDocumento(contenidoDocumento);
		documentoDocCamaronera.setNombreDocumento(event.getFile().getFileName());
		documentoDocCamaronera.setExtencionDocumento(".pdf");
		documentoDocCamaronera.setTipo("application/pdf");
		documentoDocCamaronera.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
	}
	
	public void guardarDocAltoImpacto()
	{
		if(proyecto.getAltoImpacto() && documentoAltoImpacto.getContenidoDocumento()!=null)
		{
			documentoAltoImpacto.setIdTabla(proyecto.getId());
			documentoAltoImpacto.setProyectoLicenciaCoa(proyecto);
			try {
				documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoAltoImpacto, TipoDocumentoSistema.RCOA_DOCUMENTO_ALTO_IMPACTO);
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void guardarDocGeneticCiiu1()
	{
		documentoGeneticoCiiu1.setIdTabla(ciiu1.getId());
		documentoGeneticoCiiu1.setProyectoLicenciaCoa(proyecto);
		try {
			documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoGeneticoCiiu1, TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO);
		} catch (ServiceException | CmisAlfrescoException e) {
			e.printStackTrace();
		}
	}
	
	public void guardarDocGeneticCiiu2()
	{
		documentoGeneticoCiiu2.setIdTabla(ciiu2.getId());
		documentoGeneticoCiiu2.setProyectoLicenciaCoa(proyecto);
		try {
			documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoGeneticoCiiu2, TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO);
		} catch (ServiceException | CmisAlfrescoException e) {
			e.printStackTrace();
		}
	}
	
	public void guardarDocGeneticCiiu3()
	{
		documentoGeneticoCiiu3.setIdTabla(ciiu3.getId());
		documentoGeneticoCiiu3.setProyectoLicenciaCoa(proyecto);
		try {
			documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoGeneticoCiiu3, TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO);
		} catch (ServiceException | CmisAlfrescoException e) {
			e.printStackTrace();
		}
	}
	
	public void guardarDocSectorial()
	{
		documentoDocSectorial.setIdTabla(proyecto.getId());
		documentoDocSectorial.setProyectoLicenciaCoa(proyecto);
		try {
			documentoDocSectorial = documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoDocSectorial, TipoDocumentoSistema.RCOA_DOCUMENTO_SECTORIAL);
		} catch (ServiceException | CmisAlfrescoException e) {
			e.printStackTrace();
		}
	}
	public void guardarDocFrontera()
	{
		documentoDocFrontera.setIdTabla(proyecto.getId());
		documentoDocFrontera.setProyectoLicenciaCoa(proyecto);
		try {
			documentoDocFrontera = documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoDocFrontera, TipoDocumentoSistema.RCOA_DOCUMENTO_FRONTERA);
		} catch (ServiceException | CmisAlfrescoException e) {
			e.printStackTrace();
		}
	}
	
	public void guardarDocCamaroneras()
	{
		if ((catalogoCamaronera_ciuu1 == true || catalogoCamaronera_ciuu2 == true || catalogoCamaronera_ciuu3 == true)
				&& documentoDocCamaronera.getContenidoDocumento() != null) {
			documentoDocCamaronera.setIdTabla(proyecto.getId());
			documentoDocCamaronera.setProyectoLicenciaCoa(proyecto);
			try {
				documentoDocCamaronera = documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoDocCamaronera, TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generarMapa()
	{
		
		ResponseCertificado resCer = new ResponseCertificado();
		resCer=wsMapas.getGenerarMapaWSPort().generarCertificadoInterseccion(proyecto.getCodigoUnicoAmbiental());
		if(resCer.getWorkspaceAlfresco()==null)
		{
			msgError.add("Ocurrió un error al generar el mapa de intersección");		
		}
		else
		{
			TipoDocumento tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA.getIdTipoDocumento());
			documentoMapa = new DocumentosCOA();
			documentoMapa.setIdAlfresco(resCer.getWorkspaceAlfresco());
			documentoMapa.setExtencionDocumento(".pdf");		
			documentoMapa.setTipo("application/pdf");
			documentoMapa.setTipoDocumento(tipoDocumento);
			documentoMapa.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
			documentoMapa.setNombreDocumento("Mapa_Certificado_intersección.pdf");
			documentoMapa.setIdTabla(proyecto.getId());
			documentoMapa.setProyectoLicenciaCoa(proyecto);
			documentoMapa = documentosFacade.guardar(documentoMapa);
		}
	}
    
	public void certificado() {
		try {
			
			if(!tramiteEnProceso)
				oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
			else
				oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyectoTarea(proyecto.getCodigoUnicoAmbiental(), bandejaTareasBean.getTarea().getTaskId());
			
			if (oficioCI == null) {
				oficioCI = new CertificadoInterseccionOficioCoa();				
				oficioCI.setProyectoLicenciaCoa(proyecto);
				
				if(tramiteEnProceso)
					oficioCI.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
			}
			
			ubicacionProyectoLista=proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyecto);			
			interseccionLista= detalleInterseccionProyectoAmbientalFacade.buscarPorProyecto(proyecto);			
			capasCoaLista=capasCoaFacade.listaCapasCertificadoInterseccion();
			
			//generacion Ubicacion
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
			
			//generacion Capas
			String strTableCapas = "";		
			for (CapasCoa capa : capasCoaLista) {			
				String fecha = "ND";
				if(capa.getFechaActualizacionCapa() != null)
					fecha = JsfUtil.getSimpleDateFormat(capa.getFechaActualizacionCapa());
				strTableCapas += capa.getNombre() + " (" + fecha + ")<br/>";			
			}
			
			//generacion interseccion
			List<String> detalleIntersecaCapasViabilidad=new ArrayList<String>();
			List<String> detalleIntersecaOtrasCapas=new ArrayList<String>();
			for (DetalleInterseccionProyectoAmbiental detalleInterseccion : interseccionLista) {
				String capaDetalle=detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getNombre()+": "+detalleInterseccion.getNombreGeometria();
				if(detalleInterseccion.getCodigoConvenio() != null)
					capaDetalle=detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getNombre()+": "+detalleInterseccion.getNombreGeometria() + " (" + detalleInterseccion.getCodigoConvenio() + ")";
				
				if(detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getViabilidad()){
					if(!detalleIntersecaCapasViabilidad.contains(capaDetalle))
						detalleIntersecaCapasViabilidad.add(capaDetalle);
				}else{				
					if(!detalleIntersecaOtrasCapas.contains(capaDetalle))
						detalleIntersecaOtrasCapas.add(capaDetalle);
				}
			}
			
			String strTableIntersecaViabilidad = "";		
			for (String detalleInterseca : detalleIntersecaCapasViabilidad) {			
				strTableIntersecaViabilidad += detalleInterseca+"<br/>";			
			}
			
			String strTableOtrasIntersecciones = "";		
			for (String detalleInterseca : detalleIntersecaOtrasCapas) {			
				strTableOtrasIntersecciones += detalleInterseca+"<br/>";			
			}
			
			Usuario usuarioAutoridad = null;
			if(!proyecto.getAreaResponsable().getAreaAbbreviation().equalsIgnoreCase("ND")) {
				Area areaInternaTramite = proyecto.getAreaInventarioForestal();
				String tipoRol = "role.esia.cz.autoridad";
				if(areaInternaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
					tipoRol = "role.esia.cz.autoridad";
					areaInternaTramite = areaInternaTramite.getArea();
				}else if(areaInternaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
					tipoRol = "role.esia.pc.autoridad";
				}else if(areaInternaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)){
					tipoRol = "role.esia.ga.autoridad";
				}
				
				String rolAutoridad = Constantes.getRoleAreaName(tipoRol);
				
				usuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaInternaTramite).get(0);
			}
			
			oficioCI.setUbicacion(strTableUbicacion);
			oficioCI.setCapas(strTableCapas);
			oficioCI.setInterseccionViabilidad(strTableIntersecaViabilidad);
			oficioCI.setOtraInterseccion(strTableOtrasIntersecciones);
			oficioCI.setFechaOficio(new Date());
			
			if(usuarioAutoridad != null) {
				Area areaAutoridad = new Area();
				
				if(usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1){
					areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
				}else{
					if(proyecto.getAreaInventarioForestal().getTipoArea().getSiglas().equals("DP")){//Galápagos
						areaAutoridad = proyecto.getAreaInventarioForestal();
					}else
						areaAutoridad = proyecto.getAreaInventarioForestal().getArea();
				}
				
				oficioCI.setAreaUsuarioFirma(areaAutoridad.getId());
	        	oficioCI.setUsuarioFirma(usuarioAutoridad.getNombre());
			} else {
				oficioCI.setAreaUsuarioFirma(null);
	        	oficioCI.setUsuarioFirma(null);
			}

			oficioCI=certificadoInterseccionCoaFacade.guardar(oficioCI);
			
			List<String> resultadoQr = GenerarQRCertificadoInterseccion.getCodigoQrUrlContent(true, 
					oficioCI.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), oficioCI.getCodigo(),
					GenerarQRCertificadoInterseccion.tipo_suia_rcoa, 0);
			oficioCI.setUrlCodigoValidacion(resultadoQr.get(0));
			oficioCI=certificadoInterseccionCoaFacade.guardar(oficioCI);
			
			Usuario uOperador=usuarioFacade.buscarUsuario(proyecto.getUsuarioCreacion());
			Organizacion orga=organizacionFacade.buscarPorRuc(proyecto.getUsuarioCreacion());
			
			nombreOperador=uOperador.getPersona().getNombre();
			cedulaOperador=uOperador.getPersona().getPin();
			razonSocial=orga==null?" ":orga.getNombre();
			
			autoridadMAE();

			//generarDocumento(false); //el documento se genera al momento de la firma para que salga con esa fecha
			               

		} catch (Exception e) {	
			e.printStackTrace();
			msgError.add("Ocurrió un error al guardar la información");
		}		
	}
	public void generarDocumento(boolean marcaAgua)
	{			
		FileOutputStream fileOutputStream;
		try {			
			String nombreReporte= "CertificadoInterseccionRcoa";
			codigoCiiu=ciiuPrincipal.getCodigo();
			String capasConali = capasCONALI();
			PlantillaReporte plantillaReporte = certificadoInterseccionCoaFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO.getIdTipoDocumento());
			File file = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new CertificadoInterseccionRcoaOficioHtml(oficioCI,nombreOperador,cedulaOperador,razonSocial,codigoCiiu,capasConali,usuarioAutoridad,ubicacionProyectoLista,interseccionLista,capasCoaLista),null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(file.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(file.getName()));
	        fileOutputStream = new FileOutputStream(fileArchivo);
	        fileOutputStream.write(byteArchivo);
	        fileOutputStream.close();
	        
	        if(!marcaAgua){
	        	DocumentosCOA documento = new DocumentosCOA();	        	
	        	documento.setContenidoDocumento(Files.readAllBytes(path));
	        	documento.setExtencionDocumento(".pdf");		
	        	documento.setTipo("application/pdf");
	        	documento.setIdTabla(oficioCI.getId());	       		
	        	documento.setNombreTabla(CertificadoInterseccionOficioCoa.class.getSimpleName());
	        	documento.setNombreDocumento(nombreReporte+"_"+oficioCI.getCodigo()+".pdf");
	        	documento.setProyectoLicenciaCoa(proyecto);
	        	
	        	documentoCertificado = documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "Certificado_Interseccion", 1L, documento, TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO);
	        }
	       
	       } catch (Exception e) {
	    	   msgError.add("Ocurrió un error al generar el certificado Intersección");
	       }
	}
	
	private String capasCONALI(){
		// obtengo la informacion geografica externa CONALI
		List<CatalogoGeneralCoa> listaCapas =catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.COA_CAPAS_EXTERNAS_CONALI);
		String strTableCapaCONALI = "";
		for (CatalogoGeneralCoa catalogoCapas : listaCapas) {		
			strTableCapaCONALI += catalogoCapas.getDescripcion() + "<br/>";
		}
		return strTableCapaCONALI;
	}
	
	public void validarConcesion()
	{
		DerechoMineroMAEDTO derechoMinero = new DerechoMineroMAEDTO();
		SuiaServices_Service_Arcon concesion;
		try {
			concesion = new SuiaServices_Service_Arcon(new URL(Constantes.getUrlWsRegistroCivilSri()));
			SuiaServicesArcon arcon=concesion.getSuiaServicesPort();
			derechoMinero=arcon.getConsultarCatastral(concesionMinera.getCodigo());
			
			Boolean esTitular = false;
			if(derechoMinero.getDerechoMinero().getTitularDocumento().length() == loginBean.getNombreUsuario().length())
				esTitular = derechoMinero.getDerechoMinero().getTitularDocumento().equals(loginBean.getNombreUsuario()) ? true : false;
			else if(derechoMinero.getDerechoMinero().getTitularDocumento().length() > loginBean.getNombreUsuario().length())
				esTitular = derechoMinero.getDerechoMinero().getTitularDocumento().contains(loginBean.getNombreUsuario()) ? true : false;
			else
				esTitular = loginBean.getNombreUsuario().contains(derechoMinero.getDerechoMinero().getTitularDocumento()) ? true : false;
				
			if(esTitular)
			{
				concesionMinera.setMaterial(derechoMinero.getDerechoMinero().getMaterialInteres());
				concesionMinera.setNombre(derechoMinero.getDerechoMinero().getNombreDerechoMinero());
				concesionMinera.setRegimen(derechoMinero.getDerechoMinero().getRegimen());
				concesionMinera.setArea(derechoMinero.getDerechoMinero().getSuperficie());
				if(!proyecto.getConcesionMinera())				
				{
					listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
					listaConcesionesMineras.add(concesionMinera);
				}
			}
			else
				JsfUtil.addMessageError("El Código de concesión minera no pertenece a este usuario");
				
		} catch (Exception e1) {
			JsfUtil.addMessageError("Sin servicio ARCOM");
		}
	}
	public void validarConcesiones()
	{
		if(listaConcesionesMineras.size() > 0) {
			for(ProyectoLicenciaAmbientalConcesionesMineras x: listaConcesionesMineras) {
				if(x.getCodigo().equals(concesionesMineras.getCodigo())) {
					JsfUtil.addMessageError("El Código de concesión ya se encuentra registrada");
					return;
				}
			}
		}
		
		DerechoMineroMAEDTO derechoMinero = new DerechoMineroMAEDTO();
		SuiaServices_Service_Arcon concesion;
		try {
			concesion = new SuiaServices_Service_Arcon(new URL(Constantes.getUrlWsRegistroCivilSri()));
			SuiaServicesArcon arcon=concesion.getSuiaServicesPort();
			derechoMinero=arcon.getConsultarCatastral(concesionesMineras.getCodigo());	
			Boolean esTitular = false;
			if(derechoMinero.getDerechoMinero().getTitularDocumento().length() == loginBean.getNombreUsuario().length())
				esTitular = derechoMinero.getDerechoMinero().getTitularDocumento().equals(loginBean.getNombreUsuario()) ? true : false;
			else if(derechoMinero.getDerechoMinero().getTitularDocumento().length() > loginBean.getNombreUsuario().length())
				esTitular = derechoMinero.getDerechoMinero().getTitularDocumento().contains(loginBean.getNombreUsuario()) ? true : false;
			else
				esTitular = loginBean.getNombreUsuario().contains(derechoMinero.getDerechoMinero().getTitularDocumento()) ? true : false;
				
			if(esTitular)
			{				
				concesionesMineras.setMaterial(derechoMinero.getDerechoMinero().getMaterialInteres());
				concesionesMineras.setNombre(derechoMinero.getDerechoMinero().getNombreDerechoMinero());
				concesionesMineras.setRegimen(derechoMinero.getDerechoMinero().getRegimen());
				concesionesMineras.setArea(derechoMinero.getDerechoMinero().getSuperficie());
				if(proyecto.getConcesionMinera())				
				{	
					if(listaConcesionesMineras.size()==0)
						listaConcesionesMineras.add(concesionesMineras);
					else
					{
						int rep=0;
						for(ProyectoLicenciaAmbientalConcesionesMineras x: listaConcesionesMineras)
						{
							if(x.getCodigo().equals(concesionesMineras.getCodigo()))
							{
								rep++;
							}
						}

						if(rep==0)
							listaConcesionesMineras.add(concesionesMineras);
						else
							JsfUtil.addMessageError("El Código de concesión ya se encuentra registrada");
							
					}
				}
			}
			else
				JsfUtil.addMessageError("El Código de concesión minera no pertenece a este usuario");

		} catch (Exception e) {
			JsfUtil.addMessageError("Sin servicio ARCOM");
		}
	}
	
	public void limpiarConcesiones()
	{	
		concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
		concesionesMineras = new ProyectoLicenciaAmbientalConcesionesMineras();
		listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
	}
	public void agregarConcesiones()
	{	
		concesionesMineras = new ProyectoLicenciaAmbientalConcesionesMineras();
	}
	
	public void eliminarConcesion(ProyectoLicenciaAmbientalConcesionesMineras concesion)
	{
		listaConcesionesMineras.remove(concesion);
	}
	
	public void autoridadMAE()
	{		
		Area area= areaFacade.getAreaSiglas(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL);
		List<Usuario>uList=usuarioFacade.buscarUsuariosPorRolYArea("AUTORIDAD AMBIENTAL MAE",area);
		if(uList==null || uList.size()==0)			
		{
//			JsfUtil.addMessageError("No se encontró el usuario en "+ area.getAreaName());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			return;
		}else{
			usuarioAutoridad=uList.get(0);
		}
		
//		Area areaSecretaria= areaFacade.getAreaSiglas(Constantes.SIGLAS_SUBSECRETARIA_PATRIMONIO_NATURAL);
//		List<Usuario>uList1=usuarioFacade.buscarUsuariosPorRolYArea("SUBSECRETARIO DE PATRIMONIO NATURAL",areaSecretaria);
//		if(uList1==null || uList1.size()==0)			
//		{
//			JsfUtil.addMessageError("No se encontró el usuario en "+ areaSecretaria.getAreaName());
//			return;
//		}else{
//			subsePatrimonioNatural=uList1.get(0);
//		}
	}
	
	public void cerrarDialogoArt()
	{
		dialogoArt=false;
	}
	
	public boolean validarActividadART()
	{
		boolean estado=false;
		dialogoArt=false;
//		if(ciiuArearesponsable.getCatalogoCIUU().getActividadART())
//		{
			if((proyecto.getGestionDesechos() || proyecto.getTransportaSustanciasQuimicas()) && proyecto.getCategorizacion()<=2)
			{
				if(proyecto.getCategorizacion()==1)
				{
					mensajeArt="Sr. Operador considerando que su actividad implica impacto ambiental no significativo no puede ser habilitado para la gestión de residuos o desechos peligroso y/o especiales "
							+ "y el transporte de sustancias químicas, por lo tanto, revise la información y modifique en el caso de ser necesario o desactive las opciones antes descritas";
				}
				if(proyecto.getCategorizacion()==2)
				{
					mensajeArt="Sr. Operador considerando que su actividad implica impacto ambiental bajo no puede ser habilitado para la gestión de residuos o desechos peligroso y/o especiales "
							+ "y el transporte de sustancias químicas, por lo tanto, revise la información y modifique en el caso de ser necesario o desactive las opciones antes descritas";
				}
				dialogoArt=true;
				estado=true;
			}
//			else
//			{
//				if((!proyecto.getGestionDesechos() && !proyecto.getTransportaSustanciasQuimicas()) && proyecto.getCategorizacion()>=3)
//				{
//					
//					mensajeArt="Sr. Operador considerando que su actividad requiere tanto gestión de residuos o desechos peligroso y/o especiales o como "
//							+ "el transporte de sustancias químicas, por lo tanto, revise la información y seleccione las opciones antes descritas";
//					dialogoArt=true;
//					estado=true;
//				}
//			}
//		}
//		else
//		{
//			if(proyecto.getGestionDesechos() || proyecto.getTransportaSustanciasQuimicas())
//			{
//				mensajeArt="Sr. Operador considerando que su actividad no puede ser habilitado para la gestión de residuos o desechos peligroso y/o especiales "
//						+ "y el transporte de sustancias químicas, por lo tanto, revise la información y modifique en el caso de ser necesario o desactive las opciones antes descritas";
//				dialogoArt=true;
//				estado=true;
//			}
//		}
		
		return estado;
	}
	
	
	public void iniciarProceso(){
		try {
			if(msgError.size()>0)
			{
				JsfUtil.addMessageError(msgError);
			}
			else
			{	
				//valida que haya seleccionado documentos RSQ
				if(estadoSustanciasQuimicas == true && proyecto.getTieneDocumentoRsq()==null) {
					JsfUtil.addMessageError("Ha ocurrido un error. Por favor modifique los datos del registro de información preliminar.");
					JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
					return;
				}
				
//				if(validacionActividadMinera()){
//					DefaultRequestContext.getCurrentInstance().execute("PF('mensajeMineria').show();");
//					return;
//				}
				if(validarActividadART())
				{
					return;
				}
				
				autoridadMAE();
				Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
				
				//parametros que se setean solo al inicio del proyecto
				parametros.put("u_operador", JsfUtil.getLoggedUser().getNombre());
				parametros.put("u_tramite",proyecto.getCodigoUnicoAmbiental());																	
				parametros.put("u_idProyecto", proyecto.getId());
				parametros.put("autoridadAmbiental", usuarioAutoridad.getNombre());
//				parametros.put("subsecretario", subsePatrimonioNatural.getNombre());
				parametros.put("viabilidadFavorable", false);
				parametros.put("esPrimeraVez", true);
				parametros.put("u_urlGeneracionAutomatica", Constantes.getUrlGeneracionAutomatica()+ proyecto.getId());
				
				parametros.put("requierePagoInicial", Constantes.getRequierePagoInicial());
				if (proyecto.getCategorizacion() == 1)
					parametros.put("requierePagoInicial", false); //se bloquea el pago para certificados ambientales
				
				Map<String, Object> parametrosComunes = variablesProceso();
				if(parametrosComunes == null)
					return;
				
				parametros.putAll(parametrosComunes);
				

				Long idProceso = procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_REGISTRO_PRELIMINAR, proyecto.getCodigoUnicoAmbiental(), parametros);
				
//				Map<String, Object> params = new ConcurrentHashMap<String, Object>();
//				String urlIniciarEia=Constantes.getiniciarEIAWS();
//				params.put("urlIniciarEIA",urlIniciarEia+idProceso+"-"+JsfUtil.getLoggedUser().getNombre());
////				params.put("ContentParam", "id="+idProceso+"&us="+JsfUtil.getLoggedUser().getNombre());
//				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), idProceso, params);
				
				List<DocumentosCOA> coordenadaImplantacion = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION, ProyectoLicenciaCoa.class.getSimpleName());
				if (coordenadaImplantacion.size() > 0) {
					DocumentosCOA coordenada = coordenadaImplantacion.get(0);
					coordenada.setIdProceso(idProceso);
					documentosFacade.guardar(coordenada);
				}
				
				List<DocumentosCOA> coordenadaGeografica = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA, ProyectoLicenciaCoa.class.getSimpleName());
				if (coordenadaGeografica.size() > 0) {
					DocumentosCOA coordenada = coordenadaGeografica.get(0);
					coordenada.setIdProceso(idProceso);
					documentosFacade.guardar(coordenada);
				}

				List<DocumentosCOA> listaDocumentosAltoImpacto = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_ALTO_IMPACTO,"ProyectoLicenciaCoa");
				if (listaDocumentosAltoImpacto.size() > 0) {
					DocumentosCOA coordenada = listaDocumentosAltoImpacto.get(0);
					coordenada.setIdProceso(idProceso);
					documentosFacade.guardar(coordenada);
				}

				List<DocumentosCOA> listaDocumentosFrontera = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_FRONTERA,"ProyectoLicenciaCoa");
				if (listaDocumentosFrontera.size() > 0) {
					DocumentosCOA coordenada = listaDocumentosFrontera.get(0);
					coordenada.setIdProceso(idProceso);
					documentosFacade.guardar(coordenada);
				}

				if(ciiu1 != null && ciiu1.getId() != null) {				
					List<DocumentosCOA> listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu1.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						DocumentosCOA coordenada = listaDocumentos.get(0);
						coordenada.setIdProceso(idProceso);
						documentosFacade.guardar(coordenada);
					}
					
					if(ciiuPrincipal.getRequiereViabilidadPngids()) {
						listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu1.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS,"ProyectoLicenciaCuaCiuu");
						if (listaDocumentos.size() > 0) {
							documentoViabilidadPngidsCiiu1 = listaDocumentos.get(0);
							documentoViabilidadPngidsCiiu1.setIdProceso(idProceso);
							documentosFacade.guardar(documentoViabilidadPngidsCiiu1);
						}
					}
				}

				if(ciiu2 != null && ciiu2.getId() != null) {
					List<DocumentosCOA> listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu2.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						DocumentosCOA coordenada = listaDocumentos.get(0);
						coordenada.setIdProceso(idProceso);
						documentosFacade.guardar(coordenada);
					}
					
					if(ciiuComplementaria1.getRequiereViabilidadPngids() != null && ciiuComplementaria1.getRequiereViabilidadPngids()) {
						listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu2.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS,"ProyectoLicenciaCuaCiuu");
						if (listaDocumentos.size() > 0) {
							documentoViabilidadPngidsCiiu2 = listaDocumentos.get(0);
							documentoViabilidadPngidsCiiu2.setIdProceso(idProceso);
							documentosFacade.guardar(documentoViabilidadPngidsCiiu2);
						}					
					}
				}

				if(ciiu3 != null && ciiu3.getId() != null) {
					List<DocumentosCOA>listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu3.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						DocumentosCOA coordenada = listaDocumentos.get(0);
						coordenada.setIdProceso(idProceso);
						documentosFacade.guardar(coordenada);
					}
					
					if(ciiuComplementaria2.getRequiereViabilidadPngids() != null && ciiuComplementaria2.getRequiereViabilidadPngids()) {
						listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu3.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS,"ProyectoLicenciaCuaCiuu");
						if (listaDocumentos.size() > 0) {
							documentoViabilidadPngidsCiiu3 = listaDocumentos.get(0);
							documentoViabilidadPngidsCiiu3.setIdProceso(idProceso);
							documentosFacade.guardar(documentoViabilidadPngidsCiiu3);
						}	
					}
				}
				
				proyecto.setEstadoRegistro(true);
				proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				RequestContext context = RequestContext.getCurrentInstance();
	            context.execute("PF('continuarDialog').show();");
	            
	            context.execute("PF('btn_eliminar').disable();");
	            context.execute("PF('btn_finalizar').disable();");
	            
	            if(!proyecto.getAreaResponsable().getAreaAbbreviation().equals("ND") 
	            		&& proyecto.getEstadoAsignacionArea().equals(1))
	            	context.execute("PF('btn_modificacion').disable();");
	            
//	            if(proyecto.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_EA)) {
//					DefaultRequestContext.getCurrentInstance().execute("PF('dlgBloqueEntes').show();");
//				}
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");			
		}
	}
	
	public String generarNumeroSolicitud() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-SOL-ART-" + secuenciasFacade.getCurrentYear() + "-"
					+ secuenciasFacade.getNextValueDedicateSequence("SOLICITUD_APROBACION_REQUISITOS_TECNICOS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void urlContinuar() {
		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
    }
	
	public void nombreProyectoMspListener(){
		try {
			
			nombreProyecto = new NombreProyectos();
			proyecto.setNombreProyecto(null);
			rucMsp = new RucMsp();

//			if(idRucMsp == 0){
//				otroMsp = true;
//				mostrarTextArea = true;
//			}else{
//				mostrarTextArea = false;
//				otroMsp = false;
				
				rucMsp = rucMspFacade.buscarPorRuc(loginBean.getNombreUsuario());
				nombreProyecto = nombreProyectosFacade.buscarPorId(idRucMsp);				
				
				if(rucMsp != null && nombreProyecto != null){
					
					proyecto.setNombreProyecto(nombreProyecto.getNombreProyectoMsp());
				}
//			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void descargarCoorGeo()
	{
		try {
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA,"ProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarExcel(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Coordenadas área geográfica (Anexo 1)");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	public void descargarCoorImpl()
	{
		try {
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION,"ProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarExcel(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Coordenadas área de implantación");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	public void descargarDocSectorial()
	{
		try {
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_SECTORIAL,"ProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Documento sectorial");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	public void descargarDocFrontera()
	{
		try {
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_FRONTERA,"ProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Documento frontera");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	public void descargarDocAltoImpacto()
	{
		try {
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_ALTO_IMPACTO,"ProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Documento alto impacto");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public void descargarCiiuGen1()
	{
		try {
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu1.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO,"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Documento CONABIO");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	public void descargarCiiuGen2()
	{
		try {
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu2.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO,"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Documento CONABIO");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	public void descargarCiiuGen3()
	{
		try {
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu3.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO,"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Documento CONABIO");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public void eliminarMagnitud1()
	{
		valorMagnitud1=new ValorMagnitud();
		listaValoresMagnitud1= new ArrayList<ValorMagnitud>();
		criteriomagnitud="";
	}
	
	public void eliminarMagnitud2()
	{
		valorMagnitud2=new ValorMagnitud();
		listaValoresMagnitud2= new ArrayList<ValorMagnitud>();
		criteriomagnitud="";
	}
	
	public void eliminarMagnitud3()
	{
		valorMagnitud3=new ValorMagnitud();
		listaValoresMagnitud3= new ArrayList<ValorMagnitud>();
		criteriomagnitud="";
	}
	
	public StreamedContent getAyudaCatalogoCiiu() throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (ayudaCatalogoCiiu != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(ayudaCatalogoCiiu));
                content.setName(Constantes.AYUDA_CATALOGO_CIIU);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
	
	public void validateCompletarDatos(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		Integer totalMagnitudes = 0;
		if(valorMagnitud1.getId() != null)
			totalMagnitudes++;
		if(valorMagnitud2.getId() != null)
			totalMagnitudes++;
		if(valorMagnitud3.getId() != null)
			totalMagnitudes++;
		
		if(totalMagnitudes < 1)
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar minimo un rango de operación.", null));
		if(tieneDocumentoRsq!=null && tieneDocumentoRsq && rsqSeleccionado==null)
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar un Código de registro de sustancia químicas.", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public String getNombreProponente() {

		String nombreOperador = "";
		
		if(loginBean.getUsuario() != null ) {
			Usuario usuarioOperador = loginBean.getUsuario();
			try {
				if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
					nombreOperador = usuarioOperador.getPersona().getNombre();
				} else {
					Organizacion organizacion = organizacionFacade
							.buscarPorRuc(usuarioOperador.getNombre());
					nombreOperador = organizacion.getNombre();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return nombreOperador;
	}

	public void limpiarCamposActividad(Integer seleccion) {
		if(seleccion.equals(1)) {
			ciiu1.setGenetico(null);
			ciiu1.setSaneamiento(null);
			ciiu1.setCombinacionSubActividades(null);
			ciiu1.setTipoRegimenMinero(null);
			ciiu1.setFinanciadoBancoDesarrollo(null);
			ciiu1.setIdActividadFinanciadoBancoEstado(null);
			
			if(documentoGeneticoCiiu1 != null && documentoGeneticoCiiu1.getId() != null) {
				listaDocumentosEliminar.add(documentoGeneticoCiiu1);
			}
			documentoGeneticoCiiu1 = null;
			
			if(documentoSenaguaCiiu1 != null && documentoSenaguaCiiu1.getId() != null) {
				listaDocumentosEliminar.add(documentoSenaguaCiiu1);
			}
			documentoSenaguaCiiu1 = null;
			
			if(documentoPorSectorCiiu1 != null && documentoPorSectorCiiu1.getId() != null) {
				listaDocumentosEliminar.add(documentoPorSectorCiiu1);
			}
			documentoPorSectorCiiu1 = null;
			
			if(documentoViabilidadPngidsCiiu1 != null && documentoViabilidadPngidsCiiu1.getId() != null) {
				listaDocumentosEliminar.add(documentoViabilidadPngidsCiiu1);
			}
			documentoViabilidadPngidsCiiu1 = null;
		} else if(seleccion.equals(2)) {
			ciiu2.setGenetico(null);
			ciiu2.setSaneamiento(null);
			ciiu2.setCombinacionSubActividades(null);
			ciiu2.setTipoRegimenMinero(null);
			ciiu2.setFinanciadoBancoDesarrollo(null);
			ciiu2.setIdActividadFinanciadoBancoEstado(null);
			
			if(documentoGeneticoCiiu2 != null && documentoGeneticoCiiu2.getId() != null) {
				listaDocumentosEliminar.add(documentoGeneticoCiiu2);
			}
			documentoGeneticoCiiu2 = null;
			
			if(documentoSenaguaCiiu2 != null && documentoSenaguaCiiu2.getId() != null) {
				listaDocumentosEliminar.add(documentoSenaguaCiiu2);
			}
			documentoSenaguaCiiu2 = null;
			
			if(documentoPorSectorCiiu2 != null && documentoPorSectorCiiu2.getId() != null) {
				listaDocumentosEliminar.add(documentoPorSectorCiiu2);
			}
			documentoPorSectorCiiu2 = null;
			
			if(documentoViabilidadPngidsCiiu2 != null && documentoViabilidadPngidsCiiu2.getId() != null) {
				listaDocumentosEliminar.add(documentoViabilidadPngidsCiiu2);
			}
			documentoViabilidadPngidsCiiu2= null;
		} else if(seleccion.equals(3)) {
			ciiu3.setGenetico(null);
			ciiu3.setSaneamiento(null);
			ciiu3.setCombinacionSubActividades(null);
			ciiu3.setTipoRegimenMinero(null);
			ciiu3.setFinanciadoBancoDesarrollo(null);
			ciiu3.setIdActividadFinanciadoBancoEstado(null);
			
			if(documentoGeneticoCiiu3 != null && documentoGeneticoCiiu3.getId() != null) {
				listaDocumentosEliminar.add(documentoGeneticoCiiu3);
			}
			documentoGeneticoCiiu3 = null;
			
			if(documentoSenaguaCiiu3 != null && documentoSenaguaCiiu3.getId() != null) {
				listaDocumentosEliminar.add(documentoSenaguaCiiu3);
			}
			documentoSenaguaCiiu3 = null;
			
			if(documentoPorSectorCiiu3 != null && documentoPorSectorCiiu3.getId() != null) {
				listaDocumentosEliminar.add(documentoPorSectorCiiu3);
			}
			documentoPorSectorCiiu3 = null;
			
			if(documentoViabilidadPngidsCiiu3 != null && documentoViabilidadPngidsCiiu3.getId() != null) {
				listaDocumentosEliminar.add(documentoViabilidadPngidsCiiu3);
			}
			documentoViabilidadPngidsCiiu3 = null;
		}
	}
	
	public void validarSectorActividades() {
		String nombreSector = "";
		Boolean existeMineriaAux = existeMineria;
		Boolean existeHidrocarburosAux = existeHidrocarburos;
		
		existeMineria = false;
		existeHidrocarburos = false;
		
		if (wf1.getCatalago() != null && wf1.getCatalago().getId() != null) {
			nombreSector=wf1.getCatalago().getTipoSector().getNombre().toUpperCase();
			
			if(nombreSector.equals("MINERÍA"))
				existeMineria = true;
			if(nombreSector.equals("HIDROCARBUROS") && wf1.getCatalago().getBloques())
				existeHidrocarburos = true;
		}

		if (wf2.getCatalago() != null && wf2.getCatalago().getId() != null) {
			nombreSector=wf2.getCatalago().getTipoSector().getNombre().toUpperCase();
			
			if(nombreSector.equals("MINERÍA"))
				existeMineria = true;
			if(nombreSector.equals("HIDROCARBUROS") && wf2.getCatalago().getBloques())
				existeHidrocarburos = true;
		}
		if (wf3.getCatalago() != null && wf3.getCatalago().getId() != null) {
			nombreSector=wf3.getCatalago().getTipoSector().getNombre().toUpperCase();
			
			if(nombreSector.equals("MINERÍA"))
				existeMineria = true;
			if(nombreSector.equals("HIDROCARBUROS") && wf3.getCatalago().getBloques())
				existeHidrocarburos = true;
		}
		
		if (existeMineriaAux && !existeMineria) {
			proyecto.setOperacionMinera(null);
			proyecto.setConcesionMinera(null);
			
			concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
			concesionesMineras = new ProyectoLicenciaAmbientalConcesionesMineras();
			listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
		}
		
		if(existeHidrocarburosAux && !existeHidrocarburos){
			bloquesBean.reset();
			bloquesBean.getBloquesSeleccionados();
		}
		
		this.nombreSector = nombreSector;
	}
	
	public void limpiarCamposModificarProyecto() {
		coordenadasRcoaBean.limpiarCampos();
		proyecto.setSuperficie(BigDecimal.ZERO);
		
		irFinalizar = false;
		areaPendienteAsignar = false;
		
		if(documentoDocFrontera != null && documentoDocFrontera.getId() != null) 
			listaDocumentosEliminar.add(documentoDocFrontera);
		
		if(documentoDocSectorial != null && documentoDocSectorial.getId() != null)
			listaDocumentosEliminar.add(documentoDocSectorial);
		
		if(documentoMapa != null && documentoMapa.getId() != null)
			listaDocumentosEliminar.add(documentoMapa);
		
		if(documentoCertificado != null && documentoCertificado.getId() != null)
			listaDocumentosEliminar.add(documentoCertificado);
		
		documentoDocFrontera = null;
		documentoDocSectorial = null;
	}
	
	public void guardarDatosModificados() {
		// elimino documentos que cambiaron por modificacion de proyecto
		if (listaDocumentosEliminar.size() > 0) {
			for (DocumentosCOA doc : listaDocumentosEliminar) {
				doc.setEstado(false);
				documentoFacade.guardar(doc);
			}
			
			listaDocumentosEliminar = new ArrayList<DocumentosCOA>();
		}
	}
	
	public void uploadListenerSenaguaCiiu1(FileUploadEvent event) {
		documentoSenaguaCiiu1=new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSenaguaCiiu1.setContenidoDocumento(contenidoDocumento);
		documentoSenaguaCiiu1.setNombreDocumento(event.getFile().getFileName());
		documentoSenaguaCiiu1.setExtencionDocumento(".pdf");		
		documentoSenaguaCiiu1.setTipo("application/pdf");
		documentoSenaguaCiiu1.setNombreTabla(ProyectoLicenciaCuaCiuu.class.getSimpleName());		
	}
	
	public void uploadListenerSenaguaCiiu2(FileUploadEvent event) {
		documentoSenaguaCiiu2=new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSenaguaCiiu2.setContenidoDocumento(contenidoDocumento);
		documentoSenaguaCiiu2.setNombreDocumento(event.getFile().getFileName());
		documentoSenaguaCiiu2.setExtencionDocumento(".pdf");		
		documentoSenaguaCiiu2.setTipo("application/pdf");
		documentoSenaguaCiiu2.setNombreTabla(ProyectoLicenciaCuaCiuu.class.getSimpleName());		
	}
	
	public void uploadListenerSenaguaCiiu3(FileUploadEvent event) {
		documentoSenaguaCiiu3=new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSenaguaCiiu3.setContenidoDocumento(contenidoDocumento);
		documentoSenaguaCiiu3.setNombreDocumento(event.getFile().getFileName());
		documentoSenaguaCiiu3.setExtencionDocumento(".pdf");		
		documentoSenaguaCiiu3.setTipo("application/pdf");
		documentoSenaguaCiiu3.setNombreTabla(ProyectoLicenciaCuaCiuu.class.getSimpleName());		
	}
	
	public void guardarDocumentosSaneamiento() {
		try {
			if(ciiuPrincipal.getSaneamiento()) {
				documentoSenaguaCiiu1.setIdTabla(ciiu1.getId());
				documentoSenaguaCiiu1.setProyectoLicenciaCoa(proyecto);
				
				documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoSenaguaCiiu1, TipoDocumentoSistema.RCOA_DOCUMENTO_SANEAMIENTO);
			}
			
			if(ciiuComplementaria1.getSaneamiento() != null && ciiuComplementaria1.getSaneamiento()) {
				documentoSenaguaCiiu2.setIdTabla(ciiu2.getId());
				documentoSenaguaCiiu2.setProyectoLicenciaCoa(proyecto);
				
				documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoSenaguaCiiu2, TipoDocumentoSistema.RCOA_DOCUMENTO_SANEAMIENTO);
			}
			
			if(ciiuComplementaria2.getSaneamiento() != null && ciiuComplementaria2.getSaneamiento()) {
				documentoSenaguaCiiu3.setIdTabla(ciiu3.getId());
				documentoSenaguaCiiu3.setProyectoLicenciaCoa(proyecto);
				
				documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoSenaguaCiiu3, TipoDocumentoSistema.RCOA_DOCUMENTO_SANEAMIENTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void descargarDocumentoSenagua(Integer tipoDocumento)
	{
		try {
			Integer idTabla = 0;
			if(tipoDocumento == 1)
				idTabla = ciiu1.getId();
			else if(tipoDocumento == 2)
				idTabla = ciiu2.getId();
			else if(tipoDocumento == 3)
				idTabla = ciiu3.getId();
			
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(idTabla, TipoDocumentoSistema.RCOA_DOCUMENTO_SANEAMIENTO,"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Documento SENAGUA");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public void uploadListenerAutorizacionSectoresHME(FileUploadEvent event) {
		DocumentosCOA autorizacionSector = new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		autorizacionSector.setContenidoDocumento(contenidoDocumento);
		autorizacionSector.setNombreDocumento(event.getFile().getFileName());
		autorizacionSector.setExtencionDocumento(".pdf");		
		autorizacionSector.setTipo("application/pdf");
		autorizacionSector.setNombreTabla(ProyectoLicenciaCuaCiuu.class.getSimpleName());
		
    	String tipoDocumentoString = (String) event.getComponent().getAttributes().get("tipoDocumento");
    	
    	int tipoDocumento = Integer.parseInt(tipoDocumentoString);
    	
		switch (tipoDocumento) {
		case 1:
			documentoPorSectorCiiu1 = autorizacionSector;
			break;
		case 2:
			documentoPorSectorCiiu2 = autorizacionSector;
			break;
		case 3:
			documentoPorSectorCiiu3 = autorizacionSector;
			break;
		default:
			break;
		}
	}
	
	public void guardarDocumentosAutorizacionesPorSector() {
		try {
			if(esCiiu1HidrocarburoMineriaElectrico){
				documentoPorSectorCiiu1.setIdTabla(ciiu1.getId());
				documentoPorSectorCiiu1.setProyectoLicenciaCoa(proyecto);
				
				if(documentoPorSectorCiiu1.getId() == null) {
					documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoPorSectorCiiu1, TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES);
				} else {
					documentoFacade.guardar(documentoPorSectorCiiu1);
				}
			}
			
			if(esCiiu2HidrocarburoMineriaElectrico){
				documentoPorSectorCiiu2.setIdTabla(ciiu2.getId());
				documentoPorSectorCiiu2.setProyectoLicenciaCoa(proyecto);
				
				if(documentoPorSectorCiiu2.getId() == null) {
					documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoPorSectorCiiu2, TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES);
				} else {
					documentoFacade.guardar(documentoPorSectorCiiu2);
				}
			}
			
			if(esCiiu3HidrocarburoMineriaElectrico){
				documentoPorSectorCiiu3.setIdTabla(ciiu3.getId());
				documentoPorSectorCiiu3.setProyectoLicenciaCoa(proyecto);
				
				
				if(documentoPorSectorCiiu3.getId() == null) {
					documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoPorSectorCiiu3, TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES);
				} else {
					documentoFacade.guardar(documentoPorSectorCiiu3);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void recuperarAutorizacionesPorSector() {
		List<DocumentosCOA> listaDocumentos;
		String nombreSector = "";
		try {
			if(ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null) {
				nombreSector = ciiuPrincipal.getTipoSector().getNombre().toUpperCase();
				
				if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){// || nombreSector.equals("ELÉCTRICO")
					esCiiu1HidrocarburoMineriaElectrico = true;
					listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu1.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						documentoPorSectorCiiu1 = listaDocumentos.get(0);
					}
				}
			}
			
			if(ciiuComplementaria1 != null && ciiuComplementaria1.getTipoSector() != null) {
				nombreSector = ciiuComplementaria1.getTipoSector().getNombre().toUpperCase();
				
				if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){// || nombreSector.equals("ELÉCTRICO")
					esCiiu2HidrocarburoMineriaElectrico = true;
					listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu2.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						documentoPorSectorCiiu2 = listaDocumentos.get(0);
					}
				}
			}
			
			if(ciiuComplementaria2 != null && ciiuComplementaria2.getTipoSector() != null) {
				nombreSector = ciiuComplementaria2.getTipoSector().getNombre().toUpperCase();
				
				if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){// || nombreSector.equals("ELÉCTRICO")
					esCiiu3HidrocarburoMineriaElectrico = true;
					listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu3.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						documentoPorSectorCiiu3 = listaDocumentos.get(0);
					}
				}
			}
		} catch (CmisAlfrescoException e) {
		}

	}
	
	public void descargarAutorizacionPorSector(Integer tipoDocumento)
	{
		try {
			Integer idTabla = 0;
			if(tipoDocumento == 1)
				idTabla = ciiu1.getId();
			else if(tipoDocumento == 2)
				idTabla = ciiu2.getId();
			else if(tipoDocumento == 3)
				idTabla = ciiu3.getId();
			
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(idTabla, TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Documento autorización");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public void uploadListenerViabilidadPngids(FileUploadEvent event) {
		DocumentosCOA viabilidadTecnica = new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		viabilidadTecnica.setContenidoDocumento(contenidoDocumento);
		viabilidadTecnica.setNombreDocumento(event.getFile().getFileName());
		viabilidadTecnica.setExtencionDocumento(".pdf");		
		viabilidadTecnica.setTipo("application/pdf");
		viabilidadTecnica.setNombreTabla(ProyectoLicenciaCuaCiuu.class.getSimpleName());
		
    	String tipoDocumentoString = (String) event.getComponent().getAttributes().get("tipoDocumento");
    	
    	int tipoDocumento = Integer.parseInt(tipoDocumentoString);
    	
		switch (tipoDocumento) {
		case 1:
			documentoViabilidadPngidsCiiu1 = viabilidadTecnica;
			break;
		case 2:
			documentoViabilidadPngidsCiiu2 = viabilidadTecnica;
			break;
		case 3:
			documentoViabilidadPngidsCiiu3 = viabilidadTecnica;
			break;
		default:
			break;
		}
	}
	
	public void guardarDocumentosViabilidadPngids() {
		try {
			if(ciiuPrincipal.getRequiereViabilidadPngids()) {
				Boolean guardar = validarRequiereIngresoViabilidadTecnica(ciiuPrincipal, parent1, 1);
				
				if(guardar) {
				documentoViabilidadPngidsCiiu1.setIdTabla(ciiu1.getId());
				documentoViabilidadPngidsCiiu1.setProyectoLicenciaCoa(proyecto);
				
				documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoViabilidadPngidsCiiu1, TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS);
				}
			}
			
			if(ciiuComplementaria1.getRequiereViabilidadPngids() != null && ciiuComplementaria1.getRequiereViabilidadPngids()) {
				Boolean guardar = validarRequiereIngresoViabilidadTecnica(ciiuComplementaria1, parent2, 2);
				
				if(guardar) {
				documentoViabilidadPngidsCiiu2.setIdTabla(ciiu2.getId());
				documentoViabilidadPngidsCiiu2.setProyectoLicenciaCoa(proyecto);
				
				documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoViabilidadPngidsCiiu2, TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS);
				}
			}
			
			if(ciiuComplementaria2.getRequiereViabilidadPngids() != null && ciiuComplementaria2.getRequiereViabilidadPngids()) {
				Boolean guardar = validarRequiereIngresoViabilidadTecnica(ciiuComplementaria2, parent3, 3);
				
				if(guardar) {
				documentoViabilidadPngidsCiiu3.setIdTabla(ciiu3.getId());
				documentoViabilidadPngidsCiiu3.setProyectoLicenciaCoa(proyecto);
				
				documentoFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 1L, documentoViabilidadPngidsCiiu3, TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void guardarViabilidadTecnica(){
		try {
			//buscamos todas las viabilidades guardadas y se las desactiva para ingresar nuevas.
						
			List<ViabilidadTecnicaProyecto> listaViabilidadesProyecto = new ArrayList<ViabilidadTecnicaProyecto>();
			listaViabilidadesProyecto = viabilidadTecnicaProyectoFacade.buscarPorProyecto(proyecto.getId());
			
			for(ViabilidadTecnicaProyecto viabilidadProy : listaViabilidadesProyecto){
				viabilidadProy.setEstado(false);
				viabilidadTecnicaProyectoFacade.guardar(viabilidadProy, loginBean.getUsuario());
			}
			
			if(ciiuPrincipal.getRequiereViabilidadPngids()) {
				Boolean guardar = validarRequiereIngresoViabilidadTecnica(ciiuPrincipal, parent1, 1);
				if(guardar) {
				ViabilidadTecnicaProyecto viabilidadProyecto = new ViabilidadTecnicaProyecto();
				viabilidadProyecto.setProyecto(proyecto);
				viabilidadProyecto.setViabilidadTecnica(viabilidad1);				
				
				viabilidadTecnicaProyectoFacade.guardar(viabilidadProyecto, loginBean.getUsuario());
				}
			}			
			if(ciiuComplementaria1.getRequiereViabilidadPngids() != null && ciiuComplementaria1.getRequiereViabilidadPngids()) {
				Boolean guardar = validarRequiereIngresoViabilidadTecnica(ciiuComplementaria1, parent2, 2);
				if(guardar) {
				ViabilidadTecnicaProyecto viabilidadProyecto = new ViabilidadTecnicaProyecto();
				viabilidadProyecto.setProyecto(proyecto);
				viabilidadProyecto.setViabilidadTecnica(viabilidad2);				
				
				viabilidadTecnicaProyectoFacade.guardar(viabilidadProyecto, loginBean.getUsuario());
				}
			}			
			if(ciiuComplementaria2.getRequiereViabilidadPngids() != null && ciiuComplementaria2.getRequiereViabilidadPngids()) {
				Boolean guardar = validarRequiereIngresoViabilidadTecnica(ciiuComplementaria2, parent3, 3);
				if(guardar) {
				ViabilidadTecnicaProyecto viabilidadProyecto = new ViabilidadTecnicaProyecto();
				viabilidadProyecto.setProyecto(proyecto);
				viabilidadProyecto.setViabilidadTecnica(viabilidad3);				
				
				viabilidadTecnicaProyectoFacade.guardar(viabilidadProyecto, loginBean.getUsuario());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void recuperarDocumentosViabilidadPngids() {
		List<DocumentosCOA> listaDocumentos;
		try {
			if(ciiuPrincipal != null && ciiuPrincipal.getRequiereViabilidadPngids() != null) {
				listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu1.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS,"ProyectoLicenciaCuaCiuu");
				if (listaDocumentos.size() > 0) {
					documentoViabilidadPngidsCiiu1 = listaDocumentos.get(0);
				}
			}
			
			if(ciiuComplementaria1 != null && ciiuComplementaria1.getRequiereViabilidadPngids() != null) {
				listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu2.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS,"ProyectoLicenciaCuaCiuu");
				if (listaDocumentos.size() > 0) {
					documentoViabilidadPngidsCiiu2 = listaDocumentos.get(0);
				}
			}
			
			if(ciiuComplementaria2 != null && ciiuComplementaria2.getRequiereViabilidadPngids() != null) {
				listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(ciiu3.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS,"ProyectoLicenciaCuaCiuu");
				if (listaDocumentos.size() > 0) {
					documentoViabilidadPngidsCiiu3 = listaDocumentos.get(0);
				}
			}
		} catch (CmisAlfrescoException e) {
		}
	}
	
	public void recuperarDocumentoCamaroneras() {
		List<DocumentosCOA> listaDocumentos;
		try {
			if (catalogoCamaronera_ciuu1 == true || catalogoCamaronera_ciuu2 == true || catalogoCamaronera_ciuu3 == true) {
				listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA, ProyectoLicenciaCoa.class.getSimpleName());
				if (listaDocumentos.size() > 0) {
					documentoDocCamaronera = listaDocumentos.get(0);
				}
			}
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		}
	}
	
	public void descargarDocumentosViabilidadPngids(Integer tipoDocumento)
	{
		try {
			Integer idTabla = 0;
			if(tipoDocumento == 1)
				idTabla = ciiu1.getId();
			else if(tipoDocumento == 2)
				idTabla = ciiu2.getId();
			else if(tipoDocumento == 3)
				idTabla = ciiu3.getId();
			
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(idTabla, TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS,"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Documento viabilidad técnica");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public void limpiarGeneracionDesechos() {
		if(proyecto.getGeneraDesechos() != null && !proyecto.getGeneraDesechos()) {
			existeRgdObtenidoAnterior = null;
			esRgdValido = null;
			proyecto.setCodigoRgdAsociado(null);
			
			enviarRgdVinculadoAListaEliminar();
		}
		
		RequestContext.getCurrentInstance().update("frmPreliminar:pnlExtraGeneracionRgd");
		RequestContext.getCurrentInstance().update("frmPreliminar:rbtTieneRgd");
	}
	
	public void limpiarTieneGenerador() {
		if(existeRgdObtenidoAnterior != null && !existeRgdObtenidoAnterior) {
			esRgdValido = null;
			proyecto.setCodigoRgdAsociado(null);
			
			enviarRgdVinculadoAListaEliminar();
		}
		
		RequestContext.getCurrentInstance().update("frmPreliminar:pnlBuscarRgd");
		
		validarCodigoRgd(false);
	}
	
	public void limpiarCodigoRgd() {
		esRgdValido = null;
		proyecto.setCodigoRgdAsociado(null);
		RequestContext.getCurrentInstance().update("frmPreliminar:pnlBuscarRgd");
	}
	
	public void enviarRgdVinculadoAListaEliminar() {
		if(registroGeneradorSeleccionado != null) {
			Boolean existe = false;
			for(GeneradorCustom vinculado : vinculacionesRgdEliminar) {
				if(vinculado.getId().equals(registroGeneradorSeleccionado.getId()) 
						&& vinculado.getSourceType().equals(registroGeneradorSeleccionado.getSourceType())) {
					existe = true;
					break;
				} 
			}
			
			if(!existe) {
				vinculacionesRgdEliminar.add(registroGeneradorSeleccionado);
			}
		}
		
		registroGeneradorSeleccionado = null;
	}
	
	public void eliminarVinculacionRgd() throws Exception {
		if(proyecto.getId() != null) {
			for(GeneradorCustom generadorVinculado : vinculacionesRgdEliminar) {
				if(generadorVinculado.getSourceType().equals(GeneradorCustom.SOURCE_TYPE_SUIA_III)) {
					GeneradorDesechosPeligrosos generador = registroGeneradorDesechosFacade.get(generadorVinculado.getId());
					generador.setEsVinculado(false); //se cambia a desvinculado xq es un RGD por proyecto
					registroGeneradorDesechosFacade.guardar(generador);
					
					VinculoProyectoRgdSuia proyectoVinculado = vinculoProyectoRgdSuiaFacade.getProyectoVinculadoRgd(proyecto.getId());
					if(proyectoVinculado != null) {
						proyectoVinculado.setEstado(false);
						vinculoProyectoRgdSuiaFacade.guardar(proyectoVinculado);
					}
				} else {
					List<RegistroGeneradorDesechosProyectosRcoa> listaRgdProyecto = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId());
					if(listaRgdProyecto != null && !listaRgdProyecto.isEmpty()){
						RegistroGeneradorDesechosProyectosRcoa generadorProyecto = listaRgdProyecto.get(0);
						generadorProyecto.setProyectoLicenciaCoa(null);
						registroGeneradorDesechosProyectosRcoaFacade.save(generadorProyecto, loginBean.getUsuario());
					}
				}
			}
		}
		
		vinculacionesRgdEliminar = new ArrayList<GeneradorCustom>();
	}
	
	public void validarCodigoRgd(Boolean desdeCarga) {
		listaRegistrosGeneradores = new ArrayList<>();
		listaRegistrosGeneradores = registroGeneradorDesechosProyectosRcoaFacade.listarGeneradoresActivosNoVinculados(JsfUtil.getLoggedUser().getNombre());
		
		List<GeneradorCustom> registrosSuiaiii =  registroGeneradorDesechosFacade.listarGeneradoresActivosNoVinculados(JsfUtil.getLoggedUser().getId());
		listaRegistrosGeneradores.addAll(registrosSuiaiii);
		
		if(desdeCarga) {
			listaRegistrosGeneradores.add(registroGeneradorSeleccionado);
		}
	}

	public void seleccionarGenerador() throws Exception {
		
		esRgdValido = false;
		proyecto.setCodigoRgdAsociado(null);
		
		String mensajeErrorPuntos = "Estimado Operador, el sistema no puede realizar esta vinculación debido a que el Registro de Generador de Residuos y Desechos seleccionado requiere ser actualizado a través del sistema SUIA. Una vez que se haya procedido con su actualización lo podrá vincular siempre y cuando las coordenadas del Registro de Generador de Residuos y Desechos se encuentren en el área de implantación del proyecto";
		
		if(registroGeneradorSeleccionado.getSourceType().equals(GeneradorCustom.SOURCE_TYPE_SUIA_III)) {
			List<String> errorMessages = new ArrayList<>();
			
			List<PuntoRecuperacion> listaPuntos = registroGeneradorDesechosFacade.listarPuntosPorIdGenerador(registroGeneradorSeleccionado.getId());
			if(listaPuntos != null && listaPuntos.size() > 0) {
				for (PuntoRecuperacion punto : listaPuntos) {
					for(FormaPuntoRecuperacion  formaPunto : punto.getFormasPuntoRecuperacion()) {
						String coordenadasPoligono = "";
						
						for(Coordenada coordenada : formaPunto.getCoordenadas()) {
							String coordenadaX = coordenada.getX().toString().replace(",", ".");
							String coordenadaY = coordenada.getY().toString().replace(",", ".");
							
							coordenadasPoligono += (coordenadasPoligono == "") ? coordenadaX + " " + coordenadaY : "," + coordenadaX + " " + coordenadaY;
						}

						String mensaje = validarCoordenadaAreaMuestraPoligono(coordenadasPoligono, formaPunto.getTipoForma().getId());
						
						if(mensaje != null && !errorMessages.contains(mensaje)) {
							errorMessages.add(mensaje);
						}
					}
				}
				
				if (!errorMessages.isEmpty()) {
					JsfUtil.addMessageError(errorMessages);
					registroGeneradorSeleccionado = null;
				} else {
					//se asigna el codigo porque con este campo = null se genera el RGD en licencia ambiental
					proyecto.setCodigoRgdAsociado(registroGeneradorSeleccionado.getDocumento());
					esRgdValido = true;
				}
				
			} else {
				//no hay puntos válidos se requiere actualizar el RGD
				JsfUtil.addMessageError(mensajeErrorPuntos);
				registroGeneradorSeleccionado = null;
			}
		} else {
			
			Boolean existeCoordenadas = true;
			List<String> errorMessages = new ArrayList<>();
			
			List<PuntoRecuperacionRgdRcoa> puntosRecuperacion = puntoRecuperacionRgdRcoaFacade.buscarPorRgd(registroGeneradorSeleccionado.getId());
			if(puntosRecuperacion != null && puntosRecuperacion.size() > 0) {
				for(PuntoRecuperacionRgdRcoa punto : puntosRecuperacion){
					String coordenadaString = "";
					if(punto.getFormasPuntoRecuperacionRgdRcoa() != null && !punto.getFormasPuntoRecuperacionRgdRcoa().isEmpty()){
						FormaPuntoRecuperacionRgdRcoa formaRgd = punto.getFormasPuntoRecuperacionRgdRcoa().get(0);
						int tipoForma = 0;
						if(formaRgd.getTipoForma() != null && formaRgd.getTipoForma().getId() != null) {
							tipoForma = formaRgd.getTipoForma().getId();
							
							if(formaRgd.getCoordenadas() != null && formaRgd.getCoordenadas().size() == 1) {
								tipoForma = TipoForma.TIPO_FORMA_PUNTO; //por error del shape en BDD q dice poligono pero solo tiene un punto de coordenadas
							}
						} else {
							if(formaRgd.getCoordenadas() != null && formaRgd.getCoordenadas().size() > 0) {
								tipoForma = (formaRgd.getCoordenadas().size() == 1) ? TipoForma.TIPO_FORMA_PUNTO : TipoForma.TIPO_FORMA_POLIGONO;
							}
						}
						
						if(formaRgd.getCoordenadas() != null && formaRgd.getCoordenadas().size() > 0) {
							for(CoordenadaRgdCoa coordenada : formaRgd.getCoordenadas()){
								DecimalFormat formato = new DecimalFormat("#.00000");
								String coorX = formato.format(coordenada.getX()).replace(",", ".");
								String coorY = formato.format(coordenada.getY()).replace(",", ".");
								
								coordenadaString += (coordenadaString == "") ? coorX.toString() + " " + coorY.toString() : "," + coorX.toString() + " " + coorY.toString();
							}
							
							String mensaje = validarCoordenadaAreaMuestraPoligono(coordenadaString, tipoForma);
							
							if(mensaje != null && !errorMessages.contains(mensaje)) {
								errorMessages.add(mensaje);
							}
						} else {
							existeCoordenadas = false;
						}
					} else {
						existeCoordenadas = false;
					}
				}
				
				if (!errorMessages.isEmpty()) {
					JsfUtil.addMessageError(errorMessages);
					registroGeneradorSeleccionado = null;
					return;
				}
			} else {
				existeCoordenadas = false;
			}
			
			if(!existeCoordenadas) {
				JsfUtil.addMessageError(mensajeErrorPuntos);
				registroGeneradorSeleccionado = null;
				return;
			}
			
			//se asigna el codigo porque con este campo = null se genera el RGD en licencia ambiental
			proyecto.setCodigoRgdAsociado(registroGeneradorSeleccionado.getDocumento());
			esRgdValido = true;
		}	
	}
	
	public String validarCoordenadaAreaMuestraPoligono(String coordenadaPunto, int tipoForma) throws RemoteException {
		String mensaje = null;
		
		String tipo = "";
		
		if (tipoForma == (TipoForma.TIPO_FORMA_PUNTO)) {
			tipo = "pu";
		} else if (tipoForma == (TipoForma.TIPO_FORMA_LINEA)) {
			tipo = "li";
		} else {
			tipo = "po";
		}

		SVA_Reproyeccion_IntersecadoPortTypeProxy ws = new SVA_Reproyeccion_IntersecadoPortTypeProxy();
		ws.setEndpoint(Constantes.getInterseccionesWS());

		Intersecado_entrada poligono = new Intersecado_entrada();
		poligono.setU(Constantes.getUserWebServicesSnap());
		poligono.setIsla(Constantes.SIGLAS_INSTITUCION + "-RA");
		poligono.setTog(tipo);
		poligono.setXy(coordenadaPunto);
		poligono.setShp("dp");

		try {
			Intersecado_resultado[] resultadoInterseccion = ws.interseccion(poligono);

			if (resultadoInterseccion[0].getInformacion().getError() != null) {
				mensaje = resultadoInterseccion[0].getInformacion().getError().toString();
			} else {
				String mensajeError = "Estimado operador, las coordenadas del Registro de Generador de Residuos y Desechos seleccionado, no se encuentran dentro del área de implantación del proyecto que está registrando, motivo por el cual no lo puede vincular. Deberá actualizar el Registro de generador de residuos y desechos en el sistema SUIA para poder realizar la vinculación.";
				Boolean intersecaProyecto = false;
				
				for(String coordenadasImplantacion : coordenadasRcoaBean.getCoordenadasImplantacion()) {
					if (tipoForma == (TipoForma.TIPO_FORMA_PUNTO)
							|| tipoForma == (TipoForma.TIPO_FORMA_LINEA)) {
						ContienePoligono_entrada verificarGeoImpla = new ContienePoligono_entrada();
						verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
						verificarGeoImpla.setTipo(tipo);
						verificarGeoImpla.setXy1(coordenadasImplantacion);
						verificarGeoImpla.setXy2(coordenadaPunto);

						ContienePoligono_resultado[] intRestGeoImpl = ws.contienePoligono(verificarGeoImpla);
						if (intRestGeoImpl[0].getInformacion().getError() != null) {
							mensaje = intRestGeoImpl[0].getInformacion().getError().toString();
							break;
						} else {
							if (intRestGeoImpl[0].getContienePoligono().getValor().equals("f")) {
								intersecaProyecto = false;
							} else {
								intersecaProyecto = true;
								break;
							}
						}
					} else {
						ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada();
						verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
						verificarGeoImpla.setXy1(coordenadasImplantacion);
						verificarGeoImpla.setXy2(coordenadaPunto);

						ContieneZona_resultado[] intRestGeoImpl = ws.contieneZona(verificarGeoImpla);
						if (intRestGeoImpl[0].getInformacion().getError() != null) {
							mensaje = intRestGeoImpl[0].getInformacion().getError().toString();
							break;
						} else {
							if (intRestGeoImpl[0].getContieneCapa().getValor().equals("f")) {
								intersecaProyecto = false;
							} else {
								intersecaProyecto = true;
								break;
							}
						}
					}
				}
				
				if(!intersecaProyecto && mensaje == null) {
					mensaje = mensajeError;
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
			mensaje = "Error inesperado, comuní­quese con mesa de ayuda";
		}
		
		return mensaje;
	}
	
	public void continuarProceso() {
		try {
			
			if(validarActividadART())
			{
				return;
			}
			
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			
			Map<String, Object> parametrosComunes = variablesProceso();
			if(parametrosComunes == null)
				return;
			
			parametros.putAll(parametrosComunes);
			
			parametros.put("esPrimeraVez", false);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
		}
	}
	
	public Map<String, Object> variablesProceso(){
		Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
		
		try {

			parametros.put("tipoProyecto", proyecto.getTipoProyecto());
			
			Boolean esProyectoEnEjecucionRegistroLicencia = false;
			if(proyecto.getTipoProyecto() == 2 && proyecto.getCategorizacion() > 1) 
				esProyectoEnEjecucionRegistroLicencia = true;
			// aumentado si es proyecto nuevo pero si requiere ingreso de diagnostico ambiental solo para licencias
			if(proyecto.getTipoProyecto() == 1 && (proyecto.getCategorizacion() == 3 || proyecto.getCategorizacion() == 4) 
					&& proyecto.getMotivoDiagnosico()!= null && !proyecto.getMotivoDiagnosico().isEmpty())
				esProyectoEnEjecucionRegistroLicencia = true;
			parametros.put("esProyectoEnEjecucion", esProyectoEnEjecucionRegistroLicencia);	
			
			if(proyecto.getInterecaSnap())
				parametros.put("u_intersecaSnap", true);
			else
				parametros.put("u_intersecaSnap", false);
			
			if(proyecto.getInterecaBosqueProtector() || proyecto.getInterecaPatrimonioForestal())
				parametros.put("u_intersecaForestal", true);
			else
				parametros.put("u_intersecaForestal", false);
			
			//variable que inicia el proceso de Viabilidad Ambiental para proyectos que intersecan con SNAP, Bosques y vegetación protectores y/o Patrimonio Forestal
			if(proyecto.getInterecaSnap() || proyecto.getInterecaBosqueProtector() || proyecto.getInterecaPatrimonioForestal())
				parametros.put("intersecaSnapForestIntanManglar", true);
			else
				parametros.put("intersecaSnapForestIntanManglar", false);

			if(proyecto.getCategorizacion()==1 || proyecto.getCategorizacion()==2)
				parametros.put("esCertificadoRegistro", true);
			else
				parametros.put("esCertificadoRegistro", false);


			parametros.put("coberturaVegetal", proyecto.getRenocionCobertura());
			if(proyecto.getRenocionCobertura() && (proyecto.getCategorizacion()==1 || proyecto.getCategorizacion()==2))
				parametros.put("u_existeInventarioForestal", true);
			else
				parametros.put("u_existeInventarioForestal", false);
			
			Boolean archivacionProyecto = false;
			List<DetalleInterseccionProyectoAmbiental> zonificacion=detalleInterseccionProyectoAmbientalFacade.zonificacionSnap(proyecto);
			if(zonificacion.size()>0)
			{
				parametros.put("zonificacionBiodiversidad", true);
				boolean permitirActividad=false;
				for(DetalleInterseccionProyectoAmbiental zon: zonificacion)
				{						
					if(zon.getNombreGeometria().equals("ZONA DE PROTECCION"))
					{
						if(ciiuArearesponsable.getCatalogoCIUU().getZonaDeProteccion())
							permitirActividad=true;
						else
						{
							permitirActividad=false;
							break;
						}
					}
					if(zon.getNombreGeometria().equals("ZONA DE RECUPERACION"))
					{
						if(ciiuArearesponsable.getCatalogoCIUU().getZonaDeRecuperacion())
							permitirActividad=true;
						else								
						{
							permitirActividad=false;
							break;
						}								
					}
					if(zon.getNombreGeometria().equals("ZONA DE USO PUBLICO. TURISMO Y RECREACION"))
					{
						if(ciiuArearesponsable.getCatalogoCIUU().getZonaTurismoRecreacion())
							permitirActividad=true;
						else								
						{
							permitirActividad=false;
							break;
						}								
					}
					if(zon.getNombreGeometria().equals("ZONA DE USO SOSTENIBLE"))
					{
						if(ciiuArearesponsable.getCatalogoCIUU().getZonaUsoSostenible())
							permitirActividad=true;
						else								
						{
							permitirActividad=false;
							break;
						}								
					}
					if(zon.getNombreGeometria().equals("ZONA DE MANEJO COMUNITARIO"))
					{
						if(ciiuArearesponsable.getCatalogoCIUU().getZonaMarinoCosteras())
							permitirActividad=true;
						else								
						{
							permitirActividad=false;
							break;
						}								
					}
				}					
				parametros.put("esPermitidaActividad", permitirActividad);
				
				if(!permitirActividad)
					archivacionProyecto = true;
			}
			else
			{
				parametros.put("zonificacionBiodiversidad", false);
				boolean tieneExcepcion=false;
									
				if(esActividadExtractiva)
				{
					//verificar si interseco por zonas protegidas,forestal y zonas intangibles
					if(proyecto.getInterecaSnap() || coordenadasRcoaBean.isEstadoZonaIntangible())
					{
						parametros.put("esActividadExtractiva", true);							
						List<ProyectoLicenciaAmbientalCoaCiuuBloques> bloques = proyectoLicenciaAmbientalCoaCiuuBloquesFacade.cargarBloques(proyecto);
						for(ProyectoLicenciaAmbientalCoaCiuuBloques bloque: bloques)
						{
							if(bloque.getBloque().getId()==Bloque.ID_BLOQUE_31 || bloque.getBloque().getId()==Bloque.ID_BLOQUE_43) {
								tieneExcepcion=true;
								break;
							}
						}
						parametros.put("tieneExcepcion", tieneExcepcion);						
						
						if(!tieneExcepcion)
							archivacionProyecto = true;
					}
					else
						parametros.put("esActividadExtractiva", false);
				}
				else
					parametros.put("esActividadExtractiva", false);
				
				if(existeMineria)
				{
					if(proyecto.getInterecaSnap())
					{
						parametros.put("esActividadExtractiva", true);
						parametros.put("tieneExcepcion", tieneExcepcion);
						archivacionProyecto = true;
					}
				}
			}
			
			if(proyecto.getGeneraDesechos() && (esRgdValido != null && esRgdValido))
				parametros.put("generaDesecho", false); //si tiene RGD anterior y es valido no ingresa al modulo de RGDP
			else
				parametros.put("generaDesecho", proyecto.getGeneraDesechos());
			
			if((proyecto.getGestionDesechos() || proyecto.getTransportaSustanciasQuimicas()) && proyecto.getCategorizacion()>=3)
			{
				parametros.put("gestionaResiduoTransporte", true);
				parametros.put("artRcoa", true);
				
				if(aprobacionRequisitosTecnicosFacade.getAprobacionRequisitosTecnicosByProyectoLicenciaAmbiental(proyecto.getCodigoUnicoAmbiental()) == null) {
					Area areaArt = proyecto.getAreaInventarioForestal();
					
					if(proyecto.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC))
						areaArt = areaService.getAreaCoordinacionZonal(ubicacionfacade.buscarPorId(proyecto.getIdCantonOficina()));
					
					AprobacionRequisitosTecnicos art = new AprobacionRequisitosTecnicos();
					art.setProyecto(proyecto.getCodigoUnicoAmbiental());
					art.setNombreProyecto(proyecto.getNombreProyecto());
					art.setSolicitud(generarNumeroSolicitud());
					art.setUsuario(JsfUtil.getLoggedUser());
					art.setAreaResponsable(areaArt);
					art.setTipoEstudio(1);
					art.setEstado(true);
					art.setIniciadoPorNecesidad(true);
					art.setVoluntario(false);
					art.setProvincia(coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica());
					if(proyecto.getGestionDesechos())
						art.setEsGestionPropia(proyecto.getEsGestionPropia());
					
					aprobacionRequisitosTecnicosFacade.guardar(art);
				}
			}
			else
				parametros.put("gestionaResiduoTransporte", false);
					
			if(estadoSustanciasQuimicas && !tieneDocumentoRsq) 
			{
				parametros.put("empleaSustanciaQuimica", true); //variable bpm
				parametros.put("requierePagoRSQ", false);	
				proyecto.setIniciaProcesoRsq(true);
				proyecto.setTieneDocumentoRsq(0);
				proyectoLicenciaCoaFacade.guardar(proyecto);
			}
			else{
				parametros.put("empleaSustanciaQuimica", false);
				proyecto.setIniciaProcesoRsq(false);
				proyectoLicenciaCoaFacade.guardar(proyecto);
			}
			
			parametros.put("gadMunicipalDesecho", false);
			parametros.put("u_categoria", proyecto.getCategorizacion());
			parametros.put("u_emisionVariosProyectos", false);				
			
			if(archivacionProyecto) {
				//Recuperar el usuario responsable de la firma del oficio de archivación de proyecto
				Area areaResponsable = proyecto.getAreaResponsable();
				String rolAutoridad = (areaResponsable.getTipoArea().getId().equals(1)) ? "AUTORIDAD AMBIENTAL MAE" : "AUTORIDAD AMBIENTAL";
				// validacion cuando es oficina tecnica busco la autoridad ambiental en la zonal
				if(areaResponsable.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
					areaResponsable = areaResponsable.getArea();
				List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaResponsable);
				if (listaUsuario == null || listaUsuario.size() == 0)			
				{
//					JsfUtil.addMessageError("No se encontró autoridad ambiental en "+ areaResponsable.getAreaName());
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					return null;
				}else{
					parametros.put("autoridadCompetente", listaUsuario.get(0).getNombre());
				}
			}
			
			
			//scout drilling--------------------------			
			if(ciiuArearesponsable.getCatalogoCIUU().getScoutDrilling())
			{
				parametros.put("scoutDrilling", true);
				parametros.put("u_nombreFormularioPMA", "/prevencion/categoria2/v2/fichaMineria020/default.jsf");				
				parametros.put("u_exentoPago", false);
				parametros.put("u_factorCovertura", Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal")));
				parametros.put("u_costoTramite", Float.parseFloat(Constantes.getPropertyAsString("costo.tramite.registro.ambiental")));
				parametros.put("u_vieneRcoa", true);
				
				if(proyecto.getGeneraDesechos() && (esRgdValido != null && esRgdValido)) {
					parametros.put("generaDesecho", false); //si tiene RGD anterior y es valido no ingresa al modulo de RGDP
				} else {
					parametros.put("generaDesecho", true);
					proyecto.setGeneraDesechos(true);
				}
			}
			else
			{
				parametros.put("scoutDrilling", false);
			}
			
			if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){				
				parametros.put("registroConPPC", true);
			}else{
				parametros.put("registroConPPC", false);
			}
			
            
		} catch (Exception e) {
			parametros = null;
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
		
		return parametros;
	}
	
	public boolean validarCoordenadasConcesion(String coordenadas) throws RemoteException
	{
		SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
        ws.setEndpoint(Constantes.getInterseccionesWS());
        
		String coodenadasImpla="";
		boolean validar = true;
		for(int i=0;i<=coordenadasRcoaBean.getCoordinatesWrappers().size()-1;i++){
			coodenadasImpla="";
			for (int j = 0; j <=coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().size()-1; j++) {
				coodenadasImpla += (coodenadasImpla == "") ? coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getX().toString()+" "+coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getY().toString() : ","+coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getX().toString()+" "+coordenadasRcoaBean.getCoordinatesWrappers().get(i).getCoordenadas().get(j).getY().toString();                	
			}
			ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada(); //verifica que el poligono este contenida dentro de la ubicación geográfica
			verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
			verificarGeoImpla.setXy1(coodenadasImpla);
			verificarGeoImpla.setXy2(coordenadas);
			ContieneZona_resultado[]intRestGeoImpl;
			intRestGeoImpl=ws.contieneZona(verificarGeoImpla);			
			if (intRestGeoImpl[0].getContieneCapa().getValor().equals("f")) {				
				validar = false;
				break;
			}
		}
		return validar;
	}
	
	public void limpiarCamposRelacionadosCiiu(Integer seleccion) {
		switch (seleccion) {
		case 1:
			subOpciones1 = false;
			subActividad1 = new SubActividades();
			bancoEstado1 = new SubActividades();
			parent1 = new SubActividades();
			tipoOpcion1 = null;
			idSubactividades1 = null;
			requiereTablaResiduo1 = false;
			codigoOficioGRECI1 = "";
			documentoViabilidadPngidsCiiu1 = new DocumentosCOA();
			viabilidad1 = new ViabilidadTecnica();
			catalogoCamaronera_ciuu1 = false;
			ingresoViabilidadTecnica1 = true;

			residuosActividadesCiiuBean.setListaCiiuResiduos1(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
			listaProyectoCiiuSubActividad1 = new ArrayList<ProyectoCoaCiuuSubActividades>();
			listaSubActividadesHijas1 = new ArrayList<>();
			break;
		case 2:
			subOpciones2 = false;
			subActividad2 = new SubActividades();
			bancoEstado2 = new SubActividades();
			parent2 = new SubActividades();
			tipoOpcion2 = null;
			idSubactividades2 = null;
			requiereTablaResiduo2 = false;
			codigoOficioGRECI2 = "";
			documentoViabilidadPngidsCiiu2 = new DocumentosCOA();
			viabilidad2 = new ViabilidadTecnica();
			catalogoCamaronera_ciuu2 = false;
			ingresoViabilidadTecnica2 = true;

			residuosActividadesCiiuBean.setListaCiiuResiduos2(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
			listaProyectoCiiuSubActividad2 = new ArrayList<ProyectoCoaCiuuSubActividades>();
			listaSubActividadesHijas2 = new ArrayList<>();
			break;
		case 3:
			subOpciones3 = false;
			subActividad3 = new SubActividades();
			bancoEstado3 = new SubActividades();
			parent3 = new SubActividades();
			tipoOpcion3 = null;
			idSubactividades3 = null;
			requiereTablaResiduo3 = false;
			codigoOficioGRECI3 = "";
			documentoViabilidadPngidsCiiu3 = new DocumentosCOA();
			viabilidad3 = new ViabilidadTecnica();
			catalogoCamaronera_ciuu3 = false;
			ingresoViabilidadTecnica3 = true;

			residuosActividadesCiiuBean.setListaCiiuResiduos3(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
			listaProyectoCiiuSubActividad3 = new ArrayList<ProyectoCoaCiuuSubActividades>();
			listaSubActividadesHijas3 = new ArrayList<>();
			break;
		default:
			break;
		}
		
		if (catalogoCamaronera_ciuu1 == false && catalogoCamaronera_ciuu2 == false && catalogoCamaronera_ciuu3 == false) {
			proyecto.setConcesionCamaronera(null);
			
			if(documentoDocCamaronera != null && documentoDocCamaronera.getId() != null) {
				listaDocumentosEliminar.add(documentoDocCamaronera);
			}
			documentoDocCamaronera = null;
		}
		
	}
	
	public void limpiarCiiu1(){
		ciiuPrincipal = new CatalogoCIUU();
		wf1 = new WolframVO();
		limpiarCamposActividad(1);
		RequestContext.getCurrentInstance().update("frmPreliminar:txtCiiuPrincipal");
		
		listaSubActividades1= new ArrayList<SubActividades>();	
		subOpciones1=false;
		subActividad1=new SubActividades();
		bancoEstado1=new SubActividades();
		parent1 = new SubActividades();
		tipoOpcion1 = null;
		residuosActividadesCiiuBean.setListaCiiuResiduos1(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad1= new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades1=null;
		requiereTablaResiduo1=false;
		codigoOficioGRECI1= "";
		documentoViabilidadPngidsCiiu1 = new DocumentosCOA();
		viabilidad1 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu1 = false;
		
		ciiuComplementaria1 = new CatalogoCIUU(); 
		limpiarCamposActividad(2);
		listaSubActividades2= new ArrayList<SubActividades>();		
		subOpciones2=false;
		subActividad2=new SubActividades();
		bancoEstado2=new SubActividades();
		parent2 = new SubActividades();		
		tipoOpcion2 = null;
		residuosActividadesCiiuBean.setListaCiiuResiduos2(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad2= new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades2=null;
		requiereTablaResiduo2=false;
		codigoOficioGRECI2="";
		documentoViabilidadPngidsCiiu2 = new DocumentosCOA();
		viabilidad2 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu2 = false;
		
		ciiuComplementaria2 = new CatalogoCIUU(); 
		limpiarCamposActividad(3);
		listaSubActividades3= new ArrayList<SubActividades>();	
		subOpciones3=false;
		subActividad3=new SubActividades();
		bancoEstado3=new SubActividades();
		parent3 = new SubActividades();
		tipoOpcion3 = null;
		residuosActividadesCiiuBean.setListaCiiuResiduos3(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad3= new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades3=null;
		requiereTablaResiduo3=false;
		codigoOficioGRECI3="";
		documentoViabilidadPngidsCiiu3 = new DocumentosCOA();
		viabilidad3 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu3 = false;
		
		coordenadasRcoaBean.setEstadoZonaIntangible(false);
		esCiiu1HidrocarburoMineriaElectrico = false;
		esCiiu2HidrocarburoMineriaElectrico = false;
		esCiiu3HidrocarburoMineriaElectrico = false;
		
		validarSectorActividades();
		
		rbVerificaEncuentraActividadCIIU = false;
		actividadEncontrada = false;
		notificacionActividadCIIU.setMensaje(null);
		
		proyecto.setConcesionCamaronera(null);
		if(documentoDocCamaronera != null && documentoDocCamaronera.getId() != null) {
			listaDocumentosEliminar.add(documentoDocCamaronera);
		}
		documentoDocCamaronera = null;
		
		mostrarPoseeContrato = false;
		tieneContratoMineria = null;
		esContratoOperacion = null;
		concesionMinera = null;
		
		zona="";
		
		RequestContext.getCurrentInstance().update("frmPreliminar:idActividadGenetica1");
		RequestContext.getCurrentInstance().update("frmPreliminar:idActividadGenetica2");
		RequestContext.getCurrentInstance().update("frmPreliminar:idActividadGenetica3");
		RequestContext.getCurrentInstance().update("frmPreliminar:pnlNotificacion");
		
	}
	
	public void limpiarCiiu2(){
		ciiuComplementaria1 = new CatalogoCIUU(); 
		wf2 = new WolframVO();
		limpiarCamposActividad(2);	
		
		listaSubActividades2= new ArrayList<SubActividades>();	
		subOpciones2=false;
		subActividad2=new SubActividades();
		bancoEstado2=new SubActividades();
		parent2 = new SubActividades();
		tipoOpcion2 = null;
		residuosActividadesCiiuBean.setListaCiiuResiduos2(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad2= new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades2=null;
		requiereTablaResiduo2=false;
		codigoOficioGRECI2= "";
		documentoViabilidadPngidsCiiu2 = new DocumentosCOA();
		viabilidad2 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu2 = false;
		
		ciiuComplementaria2 = new CatalogoCIUU(); 
		limpiarCamposActividad(3);
		listaSubActividades3= new ArrayList<SubActividades>();	
		subOpciones3=false;
		subActividad3=new SubActividades();
		bancoEstado3=new SubActividades();
		parent3 = new SubActividades();
		tipoOpcion3 = null;
		residuosActividadesCiiuBean.setListaCiiuResiduos3(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad3= new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades3=null;
		requiereTablaResiduo3=false;
		codigoOficioGRECI3="";
		documentoViabilidadPngidsCiiu3 = new DocumentosCOA();
		viabilidad3 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu3 = false;
		
		coordenadasRcoaBean.setEstadoZonaIntangible(false);		
		esCiiu2HidrocarburoMineriaElectrico = false;
		esCiiu3HidrocarburoMineriaElectrico = false;
		if(ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null) {
			String nombreSector = ciiuPrincipal.getTipoSector().getNombre().toUpperCase();
			if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){	// || nombreSector.equals("ELÉCTRICO")
				esCiiu1HidrocarburoMineriaElectrico = true;
				if(coordenadasRcoaBean.isEstadoZonaIntangibleAux())
					coordenadasRcoaBean.setEstadoZonaIntangible(true);
			}
		}
		
		if (catalogoCamaronera_ciuu1 == false) {
			proyecto.setConcesionCamaronera(null);
			
			if(documentoDocCamaronera != null && documentoDocCamaronera.getId() != null) {
				listaDocumentosEliminar.add(documentoDocCamaronera);
			}
			documentoDocCamaronera = null;
		}
		
		mostrarPoseeContrato = false;
		tieneContratoMineria = null;
		esContratoOperacion = null;
		concesionMinera = null;
		zona="";
		
		RequestContext.getCurrentInstance().update("frmPreliminar:idActividadGenetica1");
		RequestContext.getCurrentInstance().update("frmPreliminar:idActividadGenetica2");
		RequestContext.getCurrentInstance().update("frmPreliminar:idActividadGenetica3");
	}
	
	public void limpiarCiiu3(){
		ciiuComplementaria2 = new CatalogoCIUU();
		wf3 = new WolframVO();
		limpiarCamposActividad(3);	
		listaSubActividades3= new ArrayList<SubActividades>();	
		subOpciones3=false;
		subActividad3=new SubActividades();
		bancoEstado3=new SubActividades();
		parent3 = new SubActividades();
		tipoOpcion3 = null;
		residuosActividadesCiiuBean.setListaCiiuResiduos3(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad3= new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades3=null;
		requiereTablaResiduo3=false;
		codigoOficioGRECI3="";
		documentoViabilidadPngidsCiiu3 = new DocumentosCOA();
		viabilidad3 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu3 = false;
		
		coordenadasRcoaBean.setEstadoZonaIntangible(false);		
		esCiiu3HidrocarburoMineriaElectrico = false;
		if(ciiuComplementaria2 != null && ciiuComplementaria2.getTipoSector() != null) {
			String nombreSector = ciiuComplementaria2.getTipoSector().getNombre().toUpperCase();
			if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){ // || nombreSector.equals("ELÉCTRICO")
				esCiiu2HidrocarburoMineriaElectrico = true;
				if(coordenadasRcoaBean.isEstadoZonaIntangibleAux())
					coordenadasRcoaBean.setEstadoZonaIntangible(true);
			}
		}
		
		if (catalogoCamaronera_ciuu1 == false && catalogoCamaronera_ciuu2 == false) {
			proyecto.setConcesionCamaronera(null);
			
			if(documentoDocCamaronera != null && documentoDocCamaronera.getId() != null) {
				listaDocumentosEliminar.add(documentoDocCamaronera);
			}
			documentoDocCamaronera = null;
		}
		
		mostrarPoseeContrato = false;
		tieneContratoMineria = null;
		esContratoOperacion = null;
		concesionMinera = null;
		
		zona = "";
		
		RequestContext.getCurrentInstance().update("frmPreliminar:idActividadGenetica1");
		RequestContext.getCurrentInstance().update("frmPreliminar:idActividadGenetica2");
		RequestContext.getCurrentInstance().update("frmPreliminar:idActividadGenetica3");
	}
	
	public void activarOpcionesAlcantarillado1() {
		if(parent1.getValorOpcion())
			subActivadesAlcantarillado1=false;
		else
			subActivadesAlcantarillado1=true;
	}
	
	public void activarOpcionesAlcantarillado2() {
		if(parent2.getValorOpcion())
			subActivadesAlcantarillado2=false;
		else
			subActivadesAlcantarillado2=true;
	}
	
	public void activarOpcionesAlcantarillado3() {
		if(parent3.getValorOpcion())
			subActivadesAlcantarillado3=false;
		else
			subActivadesAlcantarillado3=true;
	}
	
	public void activarOpcionesGalapagos1() {
		if(parent1.getValorOpcion())
			subActivadesGalapagos1=true;
		else
			subActivadesGalapagos1=false;
		
		if(parent1.getCatalogoCIUU().getCodigo().compareTo("I5510.01")==0) {
			listaSubActividades1=subActividadesFacade.actividadHijosPorRespuestaPadre(parent1);
			subActivadesGalapagos1=true;
		}
	}
	
	public void activarOpcionesGalapagos2() {
		if(parent2.getValorOpcion())
			subActivadesGalapagos2=true;
		else
			subActivadesGalapagos2=false;
		
		if(parent2.getCatalogoCIUU().getCodigo().compareTo("I5510.01")==0) {
			listaSubActividades2=subActividadesFacade.actividadHijosPorRespuestaPadre(parent2);
			subActivadesGalapagos2=true;
		}
	}
	
	public void activarOpcionesGalapagos3() {
		if(parent3.getValorOpcion())
			subActivadesGalapagos3=true;
		else
			subActivadesGalapagos3=false;
		
		if(parent3.getCatalogoCIUU().getCodigo().compareTo("I5510.01")==0) {
			listaSubActividades3=subActividadesFacade.actividadHijosPorRespuestaPadre(parent3);
			subActivadesGalapagos3=true;
		}
	}
	
	public List<SubActividades> getHijosSubActividad(SubActividades parent, Integer nroActividad) {
		List<SubActividades>  listaSubActividades = subActividadesFacade.actividadHijosPorRespuestaPadre(parent);
		
		switch (nroActividad) {
		case 1:
			listaSubActividades1 = listaSubActividades;
			if (listaSubActividades1.size() == 0)
			{
				requiereTablaResiduo1=false;
				idSubactividades1=null;
			}
			if (listaSubActividades1.size() == 1)
				subActividad1 = listaSubActividades1.get(0);
			
			if(parent.getValorOpcion()){
				noEsMineriaArtesanal = true;
			}else{
				noEsMineriaArtesanal = false;
			}
			
			return listaSubActividades1;
		case 2:
			listaSubActividades2 = listaSubActividades;
			if (listaSubActividades2.size() == 0)
			{
				requiereTablaResiduo2=false;
				idSubactividades2=null;
			}
			if (listaSubActividades2.size() == 1)
				subActividad2 = listaSubActividades2.get(0);
			
			if(parent.getValorOpcion()){
				noEsMineriaArtesanal = true;
			}else{
				noEsMineriaArtesanal = false;
			}			
			return listaSubActividades2;
		case 3:
			listaSubActividades3 = listaSubActividades;
			if (listaSubActividades3.size() == 0)
			{
				requiereTablaResiduo3=false;
				idSubactividades3=null;
			}
			if (listaSubActividades3.size() == 1)
				subActividad3 = listaSubActividades3.get(0);
			
			if(parent.getValorOpcion()){
				noEsMineriaArtesanal = true;
			}else{
				noEsMineriaArtesanal = false;
			}
			
			return listaSubActividades3;
		default:
			return listaSubActividades;
		}

	}
	
	public String nombreSubactividad(String subactividad){
		String string = subactividad;
		String[] parts = string.split("<br/>");
		
		if(parts.length > 1){
			String part2 = parts[1];
			return part2;
		}else{
			return string;
		}
		
	}
	
	public String headerSubActividad(){
		
		if(ciiuPrincipal.getCodigo().equals("B0810.22") || ciiuPrincipal.getCodigo().equals("B0810.22.01") ){
			return "Volumen de Procesamiento";			
		}else{
			return "Actividad: (" + ciiuPrincipal.getCodigo() +") - " + ciiuPrincipal.getNombre();
		}
	}
	
	public String headerSubActividadCom1(){
		if(ciiuComplementaria1.getCodigo().equals("B0810.22") || ciiuComplementaria1.getCodigo().equals("B0810.22.01") ){
			return "Volumen de Procesamiento";			
		}else{
			return "Actividad: (" + ciiuComplementaria1.getCodigo() +") - " + ciiuComplementaria1.getNombre();
		}
	}
	
	public String headerSubActividadCom2(){
		if(ciiuComplementaria2.getCodigo().equals("B0810.22") || ciiuComplementaria2.getCodigo().equals("B0810.22.01") ){
			return "Volumen de Procesamiento";			
		}else{
			return "Actividad: (" + ciiuComplementaria2.getCodigo() +") - " + ciiuComplementaria2.getNombre();
		}
	}
	
	public void cerrar(){		
		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		DefaultRequestContext.getCurrentInstance().execute("PF('mensajeMsp').hide();");	
	}

	public void validateDatosSubActividades(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(listaSubActividades1.size() > 0 && subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuPrincipal)) {
			Boolean residuos =  residuosActividadesCiiuBean.getListaCiiuResiduos1().size() <= 0;
			errorMessages.addAll(subActividadesCiiuBean.validarDatosSubActividades(listaSubActividades1, residuos, 1));
		}
		
		if(listaSubActividades2.size() > 0 && subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria1)) {
			Boolean residuos =  residuosActividadesCiiuBean.getListaCiiuResiduos2().size() <= 0;
			errorMessages.addAll(subActividadesCiiuBean.validarDatosSubActividades(listaSubActividades2, residuos, 2));
		}
		
		if(listaSubActividades3.size() > 0 && subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria2)) {
			Boolean residuos =  residuosActividadesCiiuBean.getListaCiiuResiduos3().size() <= 0;
			errorMessages.addAll(subActividadesCiiuBean.validarDatosSubActividades(listaSubActividades3, residuos, 3));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);		
	}
	
	@Getter	
	private Integer[] idSubactividades1;
	
	@Getter	
	private Integer[] idSubactividades2;
	
	@Getter	
	private Integer[] idSubactividades3;
	
	private String combinacion1="";
	private String combinacion2="";
	private String combinacion3="";
	
	@Getter
	@Setter
	private boolean requiereTablaResiduo1,requiereTablaResiduo2,requiereTablaResiduo3;
	@EJB
	private CombinacionActividadesFacade combinacionActividadesFacade;
	
	@EJB
	private ProyectoCoaCiuuSubActividadesFacade proyectoCoaCiuuSubActividadesFacade;
	
	private  CatalogoImportanciaVO actividadSubYCombi = new CatalogoImportanciaVO();
	
	
	private List<ProyectoCoaCiuuSubActividades> listaProyectoCiiuSubActividad1 = new ArrayList<ProyectoCoaCiuuSubActividades>();
	private List<ProyectoCoaCiuuSubActividades> listaProyectoCiiuSubActividad2 = new ArrayList<ProyectoCoaCiuuSubActividades>();
	private List<ProyectoCoaCiuuSubActividades> listaProyectoCiiuSubActividad3 = new ArrayList<ProyectoCoaCiuuSubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadSeleccionad1 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadSeleccionad2 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadSeleccionad3 = new ArrayList<SubActividades>();
	private ProyectoCoaCiuuSubActividades proyectoSubAct1 = new ProyectoCoaCiuuSubActividades();
	private ProyectoCoaCiuuSubActividades proyectoSubAct2 = new ProyectoCoaCiuuSubActividades();
	private ProyectoCoaCiuuSubActividades proyectoSubAct3 = new ProyectoCoaCiuuSubActividades();
	
	
	public void setIdSubactividades1(Integer[] idSubactividades) {
		this.idSubactividades1= idSubactividades;
		List<String> numeroCombinacionesProcesos = new  ArrayList<String>();
		listaProyectoCiiuSubActividad1 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		listaSubActividadSeleccionad1 = new ArrayList<SubActividades>();
		combinacion1="";
		SubActividades subactividad=null;
		for (Integer id : idSubactividades1) {						
			for (SubActividades subA : listaSubActividades1) {
				if(subA.getId().intValue()==id.intValue()) {
					subactividad=subA;
					numeroCombinacionesProcesos.add(subactividad.getOrden().toString());
					proyectoSubAct1 = new ProyectoCoaCiuuSubActividades();
					proyectoSubAct1.setSubActividad(subactividad);
					listaProyectoCiiuSubActividad1.add(proyectoSubAct1);
					listaSubActividadSeleccionad1.add(subA);
					break;
				}
			}
			if(subactividad!=null) {
				subactividad.setSeleccionado(true);
			}			
		}
		combinacion1 = StringUtils.join(numeroCombinacionesProcesos, ",");
		if(idSubactividades1.length>1)
		{
			CombinacionSubActividades combinacionSubActividades=combinacionActividadesFacade.getPorCatalogoCombinacion(subactividad.getCatalogoCIUU(), combinacion1);
			requiereTablaResiduo1=combinacionSubActividades.getRequiereIngresoResiduos()==null?false:combinacionSubActividades.getRequiereIngresoResiduos();
		}
		else
			requiereTablaResiduo1=subactividad.getRequiereIngresoResiduos()==null?false:subactividad.getRequiereIngresoResiduos();
	}
	
	public void setIdSubactividades2(Integer[] idSubactividades) {
		this.idSubactividades2= idSubactividades;
		List<String> numeroCombinacionesProcesos = new  ArrayList<String>();
		listaProyectoCiiuSubActividad2 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		listaSubActividadSeleccionad2 = new ArrayList<SubActividades>();
		combinacion2="";
		SubActividades subactividad=null;
		for (Integer id : idSubactividades2) {						
			for (SubActividades subA : listaSubActividades2) {
				if(subA.getId().intValue()==id.intValue()) {
					subactividad=subA;
					numeroCombinacionesProcesos.add(subactividad.getOrden().toString());
					proyectoSubAct2 = new ProyectoCoaCiuuSubActividades();
					proyectoSubAct2.setSubActividad(subactividad);
					listaProyectoCiiuSubActividad2.add(proyectoSubAct2);
					listaSubActividadSeleccionad2.add(subA);
					break;
				}
			}
			if(subactividad!=null) {
				subactividad.setSeleccionado(true);
			}			
		}
		combinacion2 = StringUtils.join(numeroCombinacionesProcesos, ",");
		if(idSubactividades2.length>1)
		{
			CombinacionSubActividades combinacionSubActividades=combinacionActividadesFacade.getPorCatalogoCombinacion(subactividad.getCatalogoCIUU(), combinacion2);
			requiereTablaResiduo2=combinacionSubActividades.getRequiereIngresoResiduos()==null?false:combinacionSubActividades.getRequiereIngresoResiduos();
		}
		else
			requiereTablaResiduo2=subactividad.getRequiereIngresoResiduos()==null?false:subactividad.getRequiereIngresoResiduos();
	}
	
	public void setIdSubactividades3(Integer[] idSubactividades) {
		this.idSubactividades3= idSubactividades;
		List<String> numeroCombinacionesProcesos = new  ArrayList<String>();
		listaProyectoCiiuSubActividad3 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		listaSubActividadSeleccionad3 = new ArrayList<SubActividades>();
		combinacion3="";
		SubActividades subactividad=null;
		for (Integer id : idSubactividades3) {						
			for (SubActividades subA : listaSubActividades3) {
				if(subA.getId().intValue()==id.intValue()) {
					subactividad=subA;
					numeroCombinacionesProcesos.add(subactividad.getOrden().toString());
					proyectoSubAct3 = new ProyectoCoaCiuuSubActividades();
					proyectoSubAct3.setSubActividad(subactividad);
					listaProyectoCiiuSubActividad3.add(proyectoSubAct3);
					listaSubActividadSeleccionad3.add(subA);
					break;
				}
			}
			if(subactividad!=null) {
				subactividad.setSeleccionado(true);
			}			
		}
		combinacion3 = StringUtils.join(numeroCombinacionesProcesos, ",");
		if(idSubactividades3.length>1)
		{
			CombinacionSubActividades combinacionSubActividades=combinacionActividadesFacade.getPorCatalogoCombinacion(subactividad.getCatalogoCIUU(), combinacion3);
			requiereTablaResiduo3=combinacionSubActividades.getRequiereIngresoResiduos()==null?false:combinacionSubActividades.getRequiereIngresoResiduos();
		}
		else
			requiereTablaResiduo3=subactividad.getRequiereIngresoResiduos()==null?false:subactividad.getRequiereIngresoResiduos();
	}
	
	public void nombresMspListener(){
		try {
			
			if(idTipoNombreMsp == 0){
			otroMsp = true;
			mostrarTextArea = true;
		}else{
			mostrarTextArea = false;
			otroMsp = false;
			
			listaNombresProyectosMsp = new ArrayList<NombreProyectos>();			
			listaNombresProyectosMsp = rucMspFacade.buscarProyectosPorTipoRuc(loginBean.getNombreUsuario(), idTipoNombreMsp);
		}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void nivelesAtencion1(){
		try {			
			listaSubActividadesHijas1 = new ArrayList<>();
			listaSubActividadesHijas1 = subActividadesFacade.actividadHijosPorPadre(padre1);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void nivelesAtencion2(){
		try {			
			listaSubActividadesHijas2 = new ArrayList<>();
			listaSubActividadesHijas2 = subActividadesFacade.actividadHijosPorPadre(padre2);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void nivelesAtencion3(){
		try {			
			listaSubActividadesHijas3 = new ArrayList<>();
			listaSubActividadesHijas3 = subActividadesFacade.actividadHijosPorPadre(padre3);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void enviarNotificacionActividadCIIU(){
		try {
			List<Contacto> contactos = new ArrayList<Contacto>();
			Usuario uOperador=usuarioFacade.buscarUsuario(loginBean.getNombreUsuario());
			Organizacion uOrganizacion = organizacionFacade.buscarPorRuc(uOperador.getNombre());
			if (null != uOrganizacion) {
				notificacionActividadCIIU.setOrganizacion(uOrganizacion.getId());
				contactos = uOrganizacion.getContactos();
			} else {
				contactos = uOperador.getPersona().getContactos();
			}
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
					notificacionActividadCIIU.setContacto(contacto);
					break;
				}
			}
			String telefono = "";
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.TELEFONO) && contacto.getEstado()){
					telefono = (telefono == "") ? contacto.getValor() : ", "+contacto.getValor();
				}
			}
			String celular = "";
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.CELULAR) && contacto.getEstado()){
					celular = (celular == "") ? contacto.getValor() : ", "+contacto.getValor();
				}
			}
			
			notificacionActividadCIIU.setUsuario(uOperador);
			if (null != notificacionActividadCIIU.getMensaje() || null != notificacionActividadCIIU.getContacto()) {
				notificacionActividadCIIU = notificacionActividadCIIUFacade.guardar(notificacionActividadCIIU);
				List<String> emailsDestino = new ArrayList<>();
				List<String> emailsDestinoCopia = new ArrayList<>();
				String[] listDestinos = notificacionActividadCIIU.getDestinatario().split(",");
				for (String destino : listDestinos) {
					emailsDestino.add(destino);
				}
				// emailsDestinoCopia.add(notificacionActividadCIIU.getContacto().getValor());
				String mensajeCIIU = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionActividadCIIU", 
						new Object[]{uOperador.getPersona().getNombre(),uOperador.getNombre(),notificacionActividadCIIU.getMensaje(),notificacionActividadCIIU.getContacto().getValor(),telefono,celular});
				Email.sendEmail(emailsDestino, emailsDestinoCopia, "Sistema de Regularización y Control Ambiental – Actividad CIIU no encontrada", mensajeCIIU, proyecto.getCodigoUnicoAmbiental(), null, loginBean.getUsuario());
				limpiarNotificacion();
				rbVerificaEncuentraActividadCIIU = true;
				JsfUtil.addMessageInfo("La notificación sobre la actividad económica no encontrada fue enviada al técnico de categorización ambiental para su análisis.");
			}
		} catch (Exception e) {
			LOGGER.error("Actividad CIIU no encontrada - Mensaje/contacto no encontrado. "+e.getCause()+" "+e.getMessage());
		}			
	}
	
	public void limpiarNotificacion() {
		actividadEncontrada = true;
		if(ciiuPrincipal != null && ciiuPrincipal.getId() != null) {
			rbVerificaEncuentraActividadCIIU = true;
			notificacionActividadCIIU.setMensaje(null);
		} else {
			rbVerificaEncuentraActividadCIIU = false;
			notificacionActividadCIIU.setMensaje(null);
		}
		RequestContext.getCurrentInstance().update("frmPreliminar:pnlNotificacion");
	}
	
	public void abrirPendienteAsignacion()  {
		try {
	        if(areaPendienteAsignar) {
	        	RequestContext.getCurrentInstance().update("frmPreliminar:dlgAreaPendiente");
	        	
	        	RequestContext context = RequestContext.getCurrentInstance();
	        	context.execute("PF('dlgAreaPendiente').show();");
	        	
	        	//enviar notificación operador
	        	String nombreOperador = "";
	        	Organizacion organizacion = null;
	        	if (JsfUtil.getLoggedUser().getNombre().length()<=10) {
					nombreOperador = JsfUtil.getLoggedUser().getPersona().getNombre();
				
				} else {
					organizacion = organizacionFacade.buscarPorRuc(JsfUtil.getLoggedUser().getNombre());					
	
					if(organizacion==null){
						nombreOperador = JsfUtil.getLoggedUser().getPersona().getNombre();
					}else{
						nombreOperador = organizacion.getNombre();
					}
				}
	        	
	        	Object[] parametrosNotificacionOperador = new Object[] {nombreOperador, 
	        			proyecto.getNombreProyecto(), 
						proyecto.getCodigoUnicoAmbiental(), getMensajeIntersecaMar()};
				
				String notificacionOperador = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
								"bodyNotificacionAreaEnRevisonOperador",
								parametrosNotificacionOperador);
	
				if(organizacion != null)
					Email.sendEmail(organizacion, "Regularización Ambiental Nacional", notificacionOperador, proyecto.getCodigoUnicoAmbiental(), loginBean.getUsuario(), loginBean.getUsuario());
				else
					Email.sendEmail(proyecto.getUsuario(), "Regularización Ambiental Nacional", notificacionOperador, proyecto.getCodigoUnicoAmbiental(), loginBean.getUsuario());
	        	
	        	//enviar notificación técnicos DRA
				String rolTecnico = Constantes.getRoleAreaName("role.pc.tecnico.revision.autoridad");
				List<Usuario> listaUsuario = usuarioFacade.buscarUsuarioPorRol(rolTecnico);
				for (Usuario usuario : listaUsuario) {
					Object[] parametrosNotificacion = new Object[] {usuario.getPersona().getNombre(), 
		        			proyecto.getNombreProyecto(), 
							proyecto.getCodigoUnicoAmbiental(), getMensajeIntersecaMar()};
					
					String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
									"bodyNotificacionAreaPendienteRevisonTecnico",
									parametrosNotificacion);
					
					Email.sendEmail(usuario, "Regularización Ambiental Nacional", notificacion, proyecto.getCodigoUnicoAmbiental(), loginBean.getUsuario());
				}
	        }
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error en envío de notificación asignación área. "+e.getCause()+" "+e.getMessage());
		}
	}
	
	public String getMensajeIntersecaMar() {
		String mensaje = "";
		List<String> areas = new ArrayList<>();
		if(areaPendienteAsignar) {
			for(UbicacionesGeografica ubi:coordenadasRcoaBean.getUbicacionesSeleccionadas()) {
				if(ubi.getIdRegion() != null 
						&& ubi.getIdRegion().equals(6) 
						&& !ubi.getNombre().toUpperCase().contains("INSULAR")) {
					if(!areas.contains(ubi.getUbicacionesGeografica().getNombre())){
						areas.add(ubi.getUbicacionesGeografica().getNombre());
					}
				}
			}
			
			for (String nombre : areas) {
				mensaje += nombre + ", ";
			}
			
			if(mensaje != null && !mensaje.isEmpty()){
				mensaje = mensaje.substring(0, mensaje.length() - 2);
			}
			
		}
		
		return mensaje;
	}
	
	public boolean validacionActividadMinera(){
		try {
			
			boolean existeActividad1 = false;
			boolean existeActividad2 = false;
			boolean existeActividad3 = false;
			
			if(proyecto.getCategorizacion() != 2){
				return false;
			}
			
			if(ciiu1 != null){
				if(ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.01") || 
						ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.01.01") || ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.02") || 
						ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.02.01") || ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.09") || 
						ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.09.01")){					
					existeActividad1 = true;
				}
			}
			
			if(ciiu2 != null && ciiu2.getCatalogoCIUU() != null){
				if(ciiu2.getCatalogoCIUU().getCodigo().equals("B0990.01") || 
						ciiu2.getCatalogoCIUU().getCodigo().equals("B0990.01.01") || ciiu2.getCatalogoCIUU().getCodigo().equals("B0990.02") || 
						ciiu2.getCatalogoCIUU().getCodigo().equals("B0990.02.01") || ciiu2.getCatalogoCIUU().getCodigo().equals("B0990.09") || 
						ciiu2.getCatalogoCIUU().getCodigo().equals("B0990.09.01")){
					
					existeActividad2 = true;
				}
			}
			
			if(ciiu3 != null && ciiu3.getCatalogoCIUU() != null){

				if(ciiu3.getCatalogoCIUU().getCodigo().equals("B0990.01") || 
						ciiu3.getCatalogoCIUU().getCodigo().equals("B0990.01.01") || ciiu3.getCatalogoCIUU().getCodigo().equals("B0990.02") || 
						ciiu3.getCatalogoCIUU().getCodigo().equals("B0990.02.01") || ciiu3.getCatalogoCIUU().getCodigo().equals("B0990.09") || 
						ciiu3.getCatalogoCIUU().getCodigo().equals("B0990.09.01")){
					
					existeActividad3 = true;
				}
			}
			
			if(existeActividad1 || existeActividad2 || existeActividad3){
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void cerrarDiagMineria(){	
		
		DefaultRequestContext.getCurrentInstance().execute("PF('mensajeMineria').hide();");	
		
	}
		
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> sustanciaQuimicaSeleccionadaTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
	
	@Getter
	private List<RegistroSustanciaQuimica> listaRsqUsuario =new ArrayList<>();
	@Getter
	private List<RegistroSustanciaQuimica>listaRsqUsuarioSelec;
	
	@EJB
    private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
	
	private RegistroSustanciaQuimica rsqSeleccionado;
		
	@Getter
	@Setter
	private RegistroSustanciaQuimica registroSustanciasSelect =new RegistroSustanciaQuimica();
	
	@Setter
	@Getter
	private String sustanciaOtros;	
	
	@Setter
	@Getter
	private String sustanciaOtrosTransporta;
	
	@Getter
	@Setter
	private SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtros = new SustanciaQuimicaPeligrosa();
	
	@Getter
	@Setter
	private SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtrosTrasnporta = new SustanciaQuimicaPeligrosa();
	
	@Getter
	@Setter
	private boolean tblSustanciaOtros;
	
	@Getter
	@Setter
	private boolean tblSustanciaOtrosTransporta;
	
	@Getter
	@Setter
	private boolean tblTieneRsq;
	
	@Getter
	@Setter
	private boolean tblTieneRsqT;
	
	private void controlSustancia() {
		esMercurio=false;
		esMercurioTransporta=false;
		esControlSustancia=false;		
		tblSustanciaOtrosTransporta=false;
		tblSustanciaOtros = false;
		tblTieneRsq=false;	
				
		for(SustanciaQuimicaPeligrosa item: sustanciaQuimicaSeleccionada)
		{
			if(item.getControlSustancia()!=null && item.getControlSustancia()) {
				esControlSustancia=true;				
				if(item.getDescripcion().equals("Mercurio")) {
					esMercurio=true;				
				}
				
			}
		}
		
		for(SustanciaQuimicaPeligrosa item: sustanciaQuimicaSeleccionadaTransporta)
		{
			if(item.getControlSustancia()!=null && item.getControlSustancia()) {			
				esControlSustancia=true;
				if(item.getDescripcion().equals("Mercurio")) {
					esMercurioTransporta=true;				
			
				}
			}
		}
		
		if(!listaSustanciaQuimicaSeleccionadaOtros.isEmpty()) {
			tblSustanciaOtros = true;
		}
		
		if(!listaSustanciaQuimicaSeleccionadaOtrosTransporta.isEmpty()) {
			tblSustanciaOtrosTransporta=true;
		}
		
		if(!esControlSustancia) {
			rsqVigente=false;
			tieneDocumentoRsq=null;
			limpiarRsq();
		}else {			
			cargarListaRsqUsuario();
		}
		
		if (esControlSustancia == false) {
			if ((proyecto.getSustanciasQuimicas() == null || proyecto.getSustanciasQuimicas() == false) && (proyecto.getTransportaSustanciasQuimicas()==null || proyecto.getTransportaSustanciasQuimicas()==false)) {
				proyecto.setCodigoRsqAsociado(null);
				proyecto.setTieneDocumentoRsq(null);
			}
		}
	}
		
	public void cargarListaRsqUsuario() {		
			if(tieneDocumentoRsq != null && tieneDocumentoRsq) {
				listaRsqUsuario=registroSustanciaQuimicaFacade.obtenerRegistrosSustanciasPorUsuario(JsfUtil.getLoggedUser());
				rsqVigente=true;
				tblTieneRsq=true;
			}else if(tieneDocumentoRsq != null && tieneDocumentoRsq==false){
				rsqVigente=false;
				tblTieneRsq=false;
				proyecto.setTieneDocumentoRsq(0);
				limpiarRsq();
			}
		
	}
	
		
	public void limpiarTransportaSustanciasQuimicas() {
		rsqSeleccionado=null;		
		sustanciaQuimicaSeleccionadaTransporta.clear();	
		if(!proyecto.getTransportaSustanciasQuimicas()) {			
			sustanciaQuimicaSeleccionadaTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
			listaSustanciaQuimicaSeleccionadaOtrosTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
			tblSustanciaOtrosTransporta=false;
			esMercurioTransporta=false;
			
		}
		controlSustancia();
	}
	
	public void valorRequisitos()
	{
		
		RegistroSustanciaQuimica rsq = registroSustanciasSelect;		
		rsqSeleccionado=rsq;
		proyecto.setCodigoRsqAsociado(rsq.getNumeroAplicacion());	
		proyecto.setTieneDocumentoRsq(1);
		for (RegistroSustanciaQuimica item : listaRsqUsuario) {
			item.setSeleccionado(item.getId().intValue()==rsq.getId().intValue());
		}
	}
	
	
	
	public void limpiarFabricaSustanciasQuimicas() {		
		rsqSeleccionado=null;		
		sustanciaQuimicaSeleccionada.clear();
		if(!proyecto.getSustanciasQuimicas()) {			
			sustanciaQuimicaSeleccionada = new ArrayList<SustanciaQuimicaPeligrosa>();
			listaSustanciaQuimicaSeleccionadaOtros = new ArrayList<SustanciaQuimicaPeligrosa>();			
			tblSustanciaOtros=false;
			esMercurio=false;
			
		}
		controlSustancia();
	}
	
	public void agregarSustanciaOtros() {
		tblSustanciaOtros=true;
		SustanciaQuimicaPeligrosa sustanciaCatalogo = new SustanciaQuimicaPeligrosa();
		sustanciaCatalogo=proyectoLicenciaCoaFacade.buscaSustanciasQuimicas(sustanciaOtros);
		if(sustanciaCatalogo.getId() == null) {
		SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtros = new SustanciaQuimicaPeligrosa();
		if(sustanciaOtros.isEmpty()) {
			JsfUtil.addMessageError("Especifique la sustancia química.");
			if(listaSustanciaQuimicaSeleccionadaOtros.size()==0) {
				tblSustanciaOtros=false;
			}			
		}else {
			sustanciaQuimicaSeleccionadaOtros.setDescripcion(sustanciaOtros);
			sustanciaQuimicaSeleccionadaOtros.setId(Constantes.ID_SUSTANCIA_OTROS);
			if(!contains(listaSustanciaQuimicaSeleccionadaOtros,sustanciaQuimicaSeleccionadaOtros) 
			&& !contains(sustanciaQuimicaSeleccionada,sustanciaQuimicaSeleccionadaOtros) ) {
				listaSustanciaQuimicaSeleccionadaOtros.add(sustanciaQuimicaSeleccionadaOtros);
			}else {
				JsfUtil.addMessageError("La sustancia ya fue ingresada.");
			}
		}
		}else {
			JsfUtil.addMessageError("La sustancia ingresada existe en el catálogo, por favor seleccione la sustancia en el catálogo.");
		}		
		sustanciaOtros="";
	}
	
	public void eliminarSustanciaOtros(SustanciaQuimicaPeligrosa sustancia)
	{
		listaSustanciaQuimicaSeleccionadaOtros.remove(sustancia);
		if(listaSustanciaQuimicaSeleccionadaOtros.isEmpty()) {
			tblSustanciaOtros=false;
		}
	}
	
	public void agregarSustanciaOtrosTransporta() {
		tblSustanciaOtrosTransporta=true;
		//verifico si la sustancia ingresada existe en el catalogo
		SustanciaQuimicaPeligrosa sustanciaCatalogo = new SustanciaQuimicaPeligrosa();
		sustanciaCatalogo=proyectoLicenciaCoaFacade.buscaSustanciasQuimicas(sustanciaOtrosTransporta);
		if(sustanciaCatalogo.getId()==null) {
			SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtrosTransporta = new SustanciaQuimicaPeligrosa();
			if(sustanciaOtrosTransporta.isEmpty()) {
				JsfUtil.addMessageError("Especifique la sustancia química.");
				if(listaSustanciaQuimicaSeleccionadaOtrosTransporta.size()==0) {
					tblSustanciaOtrosTransporta=false;
				}
			}else {
				sustanciaQuimicaSeleccionadaOtrosTransporta.setDescripcion(sustanciaOtrosTransporta);
				sustanciaQuimicaSeleccionadaOtrosTransporta.setId(Constantes.ID_SUSTANCIA_OTROS);
				if(!contains(listaSustanciaQuimicaSeleccionadaOtrosTransporta,sustanciaQuimicaSeleccionadaOtrosTransporta) 
				&& !contains(sustanciaQuimicaSeleccionadaTransporta,sustanciaQuimicaSeleccionadaOtrosTransporta) ) {
					listaSustanciaQuimicaSeleccionadaOtrosTransporta.add(sustanciaQuimicaSeleccionadaOtrosTransporta);
				}else {
					JsfUtil.addMessageError("La sustancia ya fue ingresada.");
				}			
			}
		}else {
			JsfUtil.addMessageError("La sustancia ingresada existe en el catálogo, por favor seleccione la sustancia en el catálogo.");
		}
		sustanciaOtrosTransporta="";
	}
	
	public void eliminarSustanciaOtrosTransporta(SustanciaQuimicaPeligrosa sustancia)
	{
		listaSustanciaQuimicaSeleccionadaOtrosTransporta.remove(sustancia);	
		if(listaSustanciaQuimicaSeleccionadaOtrosTransporta.isEmpty()) {
			tblSustanciaOtrosTransporta=false;
		}
	}
	
	public void limpiarRsq()
	{
		registroSustanciasSelect=new RegistroSustanciaQuimica();
		rsqSeleccionado=null;		
		proyecto.setCodigoRsqAsociado(null);
	}
    private static boolean contains(List<SustanciaQuimicaPeligrosa> listaSustanciaQuimicaSeleccionadaOtros , SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtros) {
    	for (SustanciaQuimicaPeligrosa e : listaSustanciaQuimicaSeleccionadaOtros)  {
        	if ((e.getDescripcion().toLowerCase().equals( sustanciaQuimicaSeleccionadaOtros.getDescripcion().toLowerCase()))) {
                return true;
            }
        }
        return false;
    }
    
    public void validarCodigoCamaronera(){
		if (coordenadasRcoaBean.getIntersecaConCamaronera() != null
				&& coordenadasRcoaBean.getIntersecaConCamaronera() == true) {
			if(proyecto.getConcesionCamaronera() != null) {
				if(coordenadasRcoaBean.getNam() == null) {
					proyecto.setConcesionCamaronera(null);
					JsfUtil.addMessageError(mensajeNotificacionCamaroneras);
				} else if(!proyecto.getConcesionCamaronera().equals(coordenadasRcoaBean.getNam())) {
					proyecto.setConcesionCamaronera(null);
					JsfUtil.addMessageError("El acuerdo de concesión no corresponde a la intersección");
				}
			}
		}else {
			proyecto.setConcesionCamaronera(null);
			JsfUtil.addMessageError(mensajeNotificacionCamaroneras);
		}
	}
    
    @Getter
    @Setter
    private Boolean esMineriaArtesanal1 = false, esMineriaArtesanal2 = false, esMineriaArtesanal3 = false;
    
    @Getter
    @Setter
    private Boolean noEsMineriaArtesanal = false;
    
    public void limpiarSubactividad(Integer nroActividad) {
    	switch (nroActividad) {
		case 1:
			if(subActividad1 != null && subActividad1.getRequiereRegimenMinero()) {
	    		ciiu1.setTipoRegimenMinero(null);
	    	}
			/**Para mostrar contrato minero **/ 
			esMineriaArtesanal1 = false;
			if (subActividad1 != null
					&& subActividad1.getNombre() != null
					&& subActividad1.getNombre().equals("¿Su actividad es de minería ARTESANAL?") 
					&& subActividad1.getValorOpcion()) {
				esMineriaArtesanal1 = true;
			}
			
			break;
		case 2:
			if(subActividad2 != null && subActividad2.getRequiereRegimenMinero()) {
	    		ciiu2.setTipoRegimenMinero(null);
	    	}
			esMineriaArtesanal2 = false;
			if (subActividad2 != null
					&& subActividad2.getNombre() != null
					&& subActividad2.getNombre().equals("¿Su actividad es de minería ARTESANAL?")
					&& subActividad2.getValorOpcion()) {
				esMineriaArtesanal2 = true;
			}
			
			break;
		case 3:
			if(subActividad3 != null && subActividad3.getRequiereRegimenMinero()) {
	    		ciiu3.setTipoRegimenMinero(null);
	    	}
			esMineriaArtesanal3 = false;
			if (subActividad3 != null
					&& subActividad3.getNombre() != null
					&& subActividad3.getNombre().equals("¿Su actividad es de minería ARTESANAL?")
					&& subActividad3.getValorOpcion()) {
				esMineriaArtesanal3 = true;
			}
			
			break;
		default:
			break;
		}
    	
    	if(!esMineriaArtesanal1 && !esMineriaArtesanal2 && !esMineriaArtesanal3){
    		noEsMineriaArtesanal = true;
    		mostrarPoseeContrato = false;
    		listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
    		concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
    		esContratoOperacion = false;
    		tieneContratoMineria = null;
    	}else{
    		noEsMineriaArtesanal = false;
    		mostrarPoseeContrato = true;
    		listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
    		concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
    		tieneContratoMineria = null;
    		esContratoOperacion = false;
    	}
    }
    
	public boolean bloqueoGad(CatalogoCIUU objCat) {
		bloquearGad = false;

		for (UbicacionesGeografica ubiCiu : coordenadasRcoaBean.getUbicacionesSeleccionadas()) {
			if(ubiCiu.getEnteAcreditado() != null) {
				ubiCiu.getEnteAcreditado().getAreaAbbreviation();
				
				//if (gad.equals("GADPSDT") || gad.equals("GADMG")) Ticket#10435654 
				/*if (gad.equals("GADPSDT")) { //Ticket#10437991
					// actividades ciiu para bloqueo
					if (objCat.getCodigo().equals("G4773.93.02") || objCat.getCodigo().equals("G4730.01")
							|| objCat.getCodigo().equals("G4730.01.01") || objCat.getCodigo().equals("J6120.01.01")
							|| objCat.getCodigo().equals("J6120.01.02")) {
	
						bloquearGad = true;
					}
				}*/
			}
		}
		return bloquearGad;
	}
	
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadesBloque1 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadesBloque2 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadesBloque3 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadesSubBloque = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private String tituloBloque1="",tituloBloque2="",tituloBloque3="",nombrePadreActividad="";
	
    public void consideracionesBloques(CatalogoCIUU catalogo) {
    	listaSubActividadesBloque1=subActividadesFacade.actividadPorBloque(catalogo,1);
    	tituloBloque1=listaSubActividadesBloque1.get(0).getTituloHijos();
    	System.out.println(tituloBloque1);
    	
    	listaSubActividadesBloque2=subActividadesFacade.actividadPorBloque(catalogo,2);
    	tituloBloque2=listaSubActividadesBloque2.get(0).getTituloHijos();
    	System.out.println(tituloBloque2);

    	listaSubActividadesBloque3=subActividadesFacade.actividadPorBloque(catalogo,3);
    	tituloBloque3=listaSubActividadesBloque3.get(0).getTituloHijos();
    	System.out.println(tituloBloque3);
    }
    public void consideracionesSubBloques(int bloque,int subActividad) {
    	switch(bloque) {
	  	  case 1:
		    	actividadBloque2=null;
		      	actividadBloque3=null;
		      	nombrePadreActividad=actividadBloque1.getNombre();
		      	// si las coordenadas intersecan y la sub actividad es Líneas de Transmisión menor o igual a 230 KV con una longitud de 10 km y Subestaciones, se debe cambiar el tipo a licencia 
		      	if((coordenadasRcoaBean.getIntersecaBiosfera() || coordenadasRcoaBean.getInterseca()) && actividadBloque1.getNombre().equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION_MAYOR) ) {
		      		actividadBloque1.setTipoPermiso("3");
		      		esSubactividadSubestacionElectrica=true;
		      	}
		      	if(subActividad==1) 
		      		subActividad1=actividadBloque1;
		      	if(subActividad==2) 
		      		subActividad2=actividadBloque1;
		      	if(subActividad==3) 
		      		subActividad3=actividadBloque1;
		      	
		    	RequestContext.getCurrentInstance().update("frmPreliminar:idBloque"+subActividad+"2");
		      	RequestContext.getCurrentInstance().update("frmPreliminar:idBloque"+subActividad+"3");
		      	limpiarSubBloque2(subActividad);
	  	    break;
	  	  case 2:
	  		  	actividadBloque1=null;
	      		actividadBloque3=null;
	      		nombrePadreActividad=actividadBloque2.getNombre();;
	      		if(subActividad==1) 
	      			subActividad1=actividadBloque2;
		      	if(subActividad==2) 
		      		subActividad2=actividadBloque2;
		      	if(subActividad==3) 
		      		subActividad3=actividadBloque2;
	      		
		      	RequestContext.getCurrentInstance().update("frmPreliminar:idBloque"+subActividad+"1");
	      		RequestContext.getCurrentInstance().update("frmPreliminar:idBloque"+subActividad+"3");
	          	if(coordenadasRcoaBean.getInterseca() || coordenadasRcoaBean.getIntersecaBiosfera() ) {
	          		mostrarSubBloque=true;
	          		if(subActividad==1) {
	          			//nombrePadreActividad=subActividad1.getNombre();
		              	listaSubActividadesSubBloque=subActividadesFacade.actividadHijosPorPadre(subActividad1);
		              	RequestContext.getCurrentInstance().update("frmPreliminar:idSubBloque12");
	          		}
	          		if(subActividad==2) {
	          			//nombrePadreActividad=subActividad2.getNombre();
		              	listaSubActividadesSubBloque=subActividadesFacade.actividadHijosPorPadre(subActividad2);
		              	RequestContext.getCurrentInstance().update("frmPreliminar:idSubBloque22");
	          		}
	          		if(subActividad==3) {
	          			//nombrePadreActividad=subActividad3.getNombre();
		              	listaSubActividadesSubBloque=subActividadesFacade.actividadHijosPorPadre(subActividad3);
		              	RequestContext.getCurrentInstance().update("frmPreliminar:idSubBloque32");
	          		}
	          	}
	  	    break;
	  	  case 22:
	  		nombrePadreActividad=nombrePadreActividad+" - "+ subActividTem.getNombre();
	  		if(subActividad==1)
	      		subActividad1=subActividTem;
	      	if(subActividad==2)
	      		subActividad2=subActividTem;
	      	if(subActividad==3)
	      		subActividad3=subActividTem;
    		  break;	  	    
	  	  case 3:
	  		  	actividadBloque1=null;
		      	actividadBloque2=null;
		      	nombrePadreActividad=actividadBloque3.getNombre();
		      	if(subActividad==1) 
		      		subActividad1=actividadBloque3;
		      	if(subActividad==2) 
		      		subActividad2=actividadBloque3;
		      	if(subActividad==3) 
		      		subActividad3=actividadBloque3;
		      	RequestContext.getCurrentInstance().update("frmPreliminar:idBloque"+subActividad+"1");
		      	RequestContext.getCurrentInstance().update("frmPreliminar:idBloque"+subActividad+"2");
		      	limpiarSubBloque2(subActividad);
		      	
	  	    break;
	  	  default:
	  	    System.out.println("No existe actividad");
    	}
    	/*
    	subActividTem=subActividad1; */
      	if(subActividad==1)
      		validaSubcategoria=subActividad1.getNombre();
      	if(subActividad==2)
      		validaSubcategoria=subActividad2.getNombre();
      	if(subActividad==3)
      		validaSubcategoria=subActividad3.getNombre();

    	RequestContext.getCurrentInstance().update("frmPreliminar:valida");
    }
    public void limpiarSubBloque2(int subActividad) {
    	listaSubActividadesSubBloque=null;
    	RequestContext.getCurrentInstance().update("frmPreliminar:idSubBloque"+subActividad+"2");
    }
    
    /**
	 * Recupera las consideraciones de la actividad CIIU seleccionada 
	 * @param actividadCiiu
	 * @param tipoActividad define el tipo de actividad principal, complementaria 1, complementaria 2 para asignación de datos a los objetos correspondientes 
	 */
    public void getConsideracionesPorActividad(CatalogoCIUU actividadCiiu, Integer tipoActividad) {
		Integer tipoPregunta = actividadCiiu.getTipoPregunta();

		switch (tipoActividad) {
		case 1:
			switch (tipoPregunta) {
			case 1:
				listaSubActividades1 = subActividadesFacade.listaXactividad(actividadCiiu);
				subOpciones1 = true;
				break;
			case 2:
				listaSubActividades1 = subActividadesFacade.listaXactividad(actividadCiiu);
				subOpciones1 = true;
				break;
			case 4:
				Integer nroBloques = subActividadesFacade.getNumeroBloques(actividadCiiu);
				listaSubActividadesPorBloque1 = new ArrayList<>();
				
				for(int i = 1; i <= nroBloques; i++) {
					listaSubActividadesPorBloque1.add(subActividadesFacade.actividadesPadrePorBloque(actividadCiiu, i));
				}
				
				for (SubactividadDto b : listaSubActividadesPorBloque1) {
					if(b.getSubActividades().get(0).getFinanciadoBancoEstado() != null 
							&& b.getSubActividades().get(0).getFinanciadoBancoEstado()) {
						bancoEstado1 = b.getSubActividades().get(0);
					}
				}
				subOpciones1 = true;
				break;
			default:
				break;
			}
			break;
		case 2:
			switch (tipoPregunta) {
			case 1:
				listaSubActividades2 = subActividadesFacade.listaXactividad(actividadCiiu);
				subOpciones2 = true;
				break;
			case 2:
				listaSubActividades2 = subActividadesFacade.listaXactividad(actividadCiiu);
				subOpciones2 = true;
				break;
			case 4:
				Integer nroBloques = subActividadesFacade.getNumeroBloques(actividadCiiu);
				listaSubActividadesPorBloque2 = new ArrayList<>();
				
				for(int i = 1; i <= nroBloques; i++) {
					listaSubActividadesPorBloque2.add(subActividadesFacade.actividadesPadrePorBloque(actividadCiiu, i));
				}
				
				for (SubactividadDto b : listaSubActividadesPorBloque2) {
					if(b.getSubActividades().get(0).getFinanciadoBancoEstado() != null 
							&& b.getSubActividades().get(0).getFinanciadoBancoEstado()) {
						bancoEstado2 = b.getSubActividades().get(0);
					}
				}
				subOpciones2 = true;
				break;
			default:
				break;
			}
			break;
		case 3:
			switch (tipoPregunta) {
			case 1:
				listaSubActividades3 = subActividadesFacade.listaXactividad(actividadCiiu);
				subOpciones3 = true;
				break;
			case 2:
				listaSubActividades3 = subActividadesFacade.listaXactividad(actividadCiiu);
				subOpciones3 = true;
				break;
			case 4:
				Integer nroBloques = subActividadesFacade.getNumeroBloques(actividadCiiu);
				listaSubActividadesPorBloque3 = new ArrayList<>();
				
				for(int i = 1; i <= nroBloques; i++) {
					listaSubActividadesPorBloque3.add(subActividadesFacade.actividadesPadrePorBloque(actividadCiiu, i));
				}
				
				for (SubactividadDto b : listaSubActividadesPorBloque3) {
					if(b.getSubActividades().get(0).getFinanciadoBancoEstado() != null 
							&& b.getSubActividades().get(0).getFinanciadoBancoEstado()) {
						bancoEstado3 = b.getSubActividades().get(0);
					}
				}
				subOpciones3 = true;
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}
    
    /**
     * Recupera las consideraciones hijas de la consideración principal seleccionada.
     * Ejecuta funciones adicionales. Por ejemplo para actividades camaraneras verifica la intersección con el shape de camaroneras de acuerdo con la  consideración principal seleccionada
     * @param consideracionPadre
     * @param tipoActividad define el tipo de actividad principal, complementaria 1, complementaria 2 para asignación de datos a los objetos correspondientes
     */
    public void getSubActividadesHijas(SubActividades consideracionPadre, Integer tipoActividad) {
		try {
			switch (tipoActividad) {
			case 1:
				catalogoCamaronera_ciuu1 = false;
				subActividad1 = new SubActividades();
				listaSubActividadesHijas1 = new ArrayList<>();

				listaSubActividadesHijas1 = subActividadesFacade.actividadHijosPorPadre(consideracionPadre);
				
				validarCoordenadaCamaronera(consideracionPadre, tipoActividad);
				validarSeleccionEscombreras(consideracionPadre, tipoActividad);
				validarSeleccionBotaderos(consideracionPadre, tipoActividad);
				break;
			case 2:
				catalogoCamaronera_ciuu2 = false;
				subActividad2 = new SubActividades();
				listaSubActividadesHijas2 = new ArrayList<>();

				listaSubActividadesHijas2 = subActividadesFacade.actividadHijosPorPadre(consideracionPadre);
				
				validarCoordenadaCamaronera(consideracionPadre, tipoActividad);
				validarSeleccionEscombreras(consideracionPadre, tipoActividad);
				validarSeleccionBotaderos(consideracionPadre, tipoActividad);
				break;
			case 3:
				catalogoCamaronera_ciuu3 = false;
				subActividad3 = new SubActividades();
				listaSubActividadesHijas3 = new ArrayList<>();

				listaSubActividadesHijas3 = subActividadesFacade.actividadHijosPorPadre(consideracionPadre);
				
				validarCoordenadaCamaronera(consideracionPadre, tipoActividad);
				validarSeleccionEscombreras(consideracionPadre, tipoActividad);
				validarSeleccionBotaderos(consideracionPadre, tipoActividad);
				break;
			default:
				break;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void validarCoordenadaCamaronera(SubActividades padre, Integer tipoActividad) {
    	//Validación camaronera
		if (padre.getRequiereValidacionCoordenadas() != null
				&& padre.getRequiereValidacionCoordenadas().equals(1)) { //1 indica que requiere validacion de coordenadas para camaroneras
						
			if(tipoActividad.equals(1)) {
				catalogoCamaronera_ciuu1 = true;
			} else if(tipoActividad.equals(2)) {
				catalogoCamaronera_ciuu2 = true;
			} else if(tipoActividad.equals(3)) {
				catalogoCamaronera_ciuu3 = true;
			}
			
//			if (coordenadasRcoaBean.getIntersecaConCamaronera() != null) {
//				if (coordenadasRcoaBean.getIntersecaConCamaronera() == true) {
//					validarNextSetp = true;
//				} else {
//					JsfUtil.addMessageError(mensajeNotificacionCamaroneras);
//					validarNextSetp = false;
//
//					if(tipoActividad.equals(1)) {
//						parent1 = new SubActividades();
//						listaSubActividadesHijas1 = new ArrayList<>();
//					} else if(tipoActividad.equals(2)) {
//						parent2 = new SubActividades();
//						listaSubActividadesHijas2 = new ArrayList<>();
//					} else if(tipoActividad.equals(3)) {
//						parent2 = new SubActividades();
//						listaSubActividadesHijas2 = new ArrayList<>();
//					}
//					
//					RequestContext.getCurrentInstance().update("frmPreliminar:subActividadesContainer");
//				}
//			}
		}
    }

    public void validarSeleccionEscombreras(SubActividades padre, Integer tipoActividad) {
    	if(padre.getCatalogoCIUU().getTipoPregunta().equals(4)) {
    		
			if (tipoActividad.equals(1)) {
				ingresoViabilidadTecnica1 = true;
			} else if (tipoActividad.equals(2)) {
				ingresoViabilidadTecnica2 = true;
			} else if (tipoActividad.equals(3)) {
				ingresoViabilidadTecnica3 = true;
			}
    		
    		if(padre.getTieneInformacionAdicional() != null && padre.getTieneInformacionAdicional().equals(4)) {
    			Boolean bloquearEscombreras = false;

    			Organizacion orga = null;
				try {
					orga = organizacionFacade.buscarPorRuc(loginBean.getNombreUsuario());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				if(orga == null) {
					bloquearEscombreras = true;
				} else {
					OrganizacionViabilidadTecnica orgaViabilidad = organizacionViabilidadTecnicaFacade.getOrganizacionPorTipoProceso(orga, "tipo.proceso.viabilidad.tecnica.escombreras");
					if(orgaViabilidad == null) {
						bloquearEscombreras = true;
					} else {
						bloquearEscombreras = false;
						
						if(orgaViabilidad.getTipoEntidad().getValor().equals(OrganizacionViabilidadTecnica.TIPO_ENTIDAD_MINISTERIO) 
							|| orgaViabilidad.getTipoEntidad().getValor().equals(OrganizacionViabilidadTecnica.TIPO_ENTIDAD_GOBIERNO_PROVINCIAL) 
							|| orgaViabilidad.getTipoEntidad().getValor().equals(OrganizacionViabilidadTecnica.TIPO_ENTIDAD_EMPRESA_PROVINCIAL )) { 
							if (tipoActividad.equals(1)) {
								ingresoViabilidadTecnica1 = false;
							} else if (tipoActividad.equals(2)) {
								ingresoViabilidadTecnica2 = false;
							} else if (tipoActividad.equals(3)) {
								ingresoViabilidadTecnica3 = false;
							}
						}
					}
				}

    			if(bloquearEscombreras) {
    				RequestContext context = RequestContext.getCurrentInstance();
    	        	context.execute("PF('dlgBloqueoEscombreras').show();");
    	        	
    				if(tipoActividad.equals(1)) {
    					parent1 = new SubActividades();
    					subActividad1 = new SubActividades();
    					listaSubActividadesHijas1 = new ArrayList<>();
    				} else if(tipoActividad.equals(2)) {
    					parent2 = new SubActividades();
    					subActividad2 = new SubActividades();
    					listaSubActividadesHijas2 = new ArrayList<>();
    				} else if(tipoActividad.equals(3)) {
    					parent3 = new SubActividades();
    					subActividad3 = new SubActividades();
    					listaSubActividadesHijas3 = new ArrayList<>();
    				}
    			}
    		}
		}
    }
    
    /**
     * Para los proyectos guardados, recupera la información de las consideraciones seleccionadas
     * Ejecuta funciones adicionales relacionadas con la consideración seleccionada
     * @param tipoActividad define el tipo de actividad principal, complementaria 1, complementaria 2 para asignación de datos a los objetos correspondientes
     */
    public void cargarDatosConsideraciones(Integer tipoActividad) {
    	switch (tipoActividad) {
		case 1:
			if(ciiuPrincipal.getTipoPregunta() != null) {
				Integer tipoPregunta = ciiuPrincipal.getTipoPregunta();
				
				switch (tipoPregunta) {
				case 1:
					parent1 = subActividad1;
					
					subActividad1 = new SubActividades();
					listaSubActividadesHijas1 = new ArrayList<>();
					break;
				case 2:
					if(subActividad1.getSubActividades() != null  && subActividad1.getSubActividades().getId() != null) {
						listaSubActividadesHijas1 = new ArrayList<>();
						
						parent1 = subActividadesFacade.actividadParent(subActividad1.getSubActividades().getId());
						listaSubActividadesHijas1 = subActividadesFacade.actividadHijosPorPadre(parent1);
					} else {
						parent1 = subActividad1;
						
						subActividad1 = new SubActividades();
						listaSubActividadesHijas1 = new ArrayList<>();
					}
					
					catalogoCamaronera_ciuu1 = false;
					validarCoordenadaCamaronera(parent1, tipoActividad);
					break;
				case 4:
					if(subActividad1.getSubActividades() != null  && subActividad1.getSubActividades().getId() != null) {
						listaSubActividadesHijas1 = new ArrayList<>();
						
						parent1 = subActividadesFacade.actividadParent(subActividad1.getSubActividades().getId());
						listaSubActividadesHijas1 = subActividadesFacade.actividadHijosPorPadre(parent1);
						
						validarSeleccionEscombreras(parent1, tipoActividad);
						validarSeleccionBotaderos(subActividad1, tipoActividad);
					} else {
						parent1 = subActividad1;
						
						subActividad1 = new SubActividades();
						listaSubActividadesHijas1 = new ArrayList<>();
					}
					break;
				default:
					break;
				}
			}
			break;
		case 2:
			if(ciiuComplementaria1.getTipoPregunta() != null) {
				Integer tipoPregunta = ciiuComplementaria1.getTipoPregunta();
				
				switch (tipoPregunta) {
				case 1:
					parent2 = subActividad2;
						
					subActividad2 = new SubActividades();
					listaSubActividadesHijas2 = new ArrayList<>();
					break;
				case 2:
					if(subActividad2.getSubActividades() != null  && subActividad2.getSubActividades().getId() != null) {
						listaSubActividadesHijas2 = new ArrayList<>();
						
						parent2 = subActividadesFacade.actividadParent(subActividad2.getSubActividades().getId());
						listaSubActividadesHijas2 = subActividadesFacade.actividadHijosPorPadre(parent2);
					} else {
						parent2 = subActividad2;
						
						subActividad2 = new SubActividades();
						listaSubActividadesHijas2 = new ArrayList<>();
					}
					
					catalogoCamaronera_ciuu2 = false;
					validarCoordenadaCamaronera(parent2, tipoActividad);
					break;
				case 4:
					if(subActividad2.getSubActividades() != null  && subActividad2.getSubActividades().getId() != null) {
						listaSubActividadesHijas2 = new ArrayList<>();
						
						parent2 = subActividadesFacade.actividadParent(subActividad2.getSubActividades().getId());
						listaSubActividadesHijas2 = subActividadesFacade.actividadHijosPorPadre(parent2);
						
						validarSeleccionEscombreras(parent2, tipoActividad);
						validarSeleccionBotaderos(subActividad2, tipoActividad);
					} else {
						parent2 = subActividad2;
						
						subActividad2 = new SubActividades();
						listaSubActividadesHijas2 = new ArrayList<>();
					}
					break;
				default:
					break;
				}
			}
			break;
		case 3:
			if(ciiuComplementaria2.getTipoPregunta() != null) {
				Integer tipoPregunta = ciiuComplementaria2.getTipoPregunta();
				
				switch (tipoPregunta) {
				case 1:
					parent3 = subActividad3;
						
					subActividad3 = new SubActividades();
					listaSubActividadesHijas3 = new ArrayList<>();
					break;
				case 2:
					if(subActividad3.getSubActividades() != null  && subActividad3.getSubActividades().getId() != null) {
						listaSubActividadesHijas3 = new ArrayList<>();
						
						parent3 = subActividadesFacade.actividadParent(subActividad3.getSubActividades().getId());
						listaSubActividadesHijas3 = subActividadesFacade.actividadHijosPorPadre(parent3);
					} else {
						parent3 = subActividad3;
						
						subActividad3 = new SubActividades();
						listaSubActividadesHijas3 = new ArrayList<>();
					}
					
					catalogoCamaronera_ciuu3 = false;
					validarCoordenadaCamaronera(parent3, tipoActividad);
					break;
				case 4:
					if(subActividad3.getSubActividades() != null  && subActividad3.getSubActividades().getId() != null) {
						listaSubActividadesHijas3 = new ArrayList<>();
						
						parent3 = subActividadesFacade.actividadParent(subActividad3.getSubActividades().getId());
						listaSubActividadesHijas3 = subActividadesFacade.actividadHijosPorPadre(parent3);
						
						validarSeleccionEscombreras(parent3, tipoActividad);
						validarSeleccionBotaderos(subActividad3, tipoActividad);
					} else {
						parent3 = subActividad3;
						
						subActividad3 = new SubActividades();
						listaSubActividadesHijas3 = new ArrayList<>();
					}
					break;
				default:
					break;
				}
			}
			break;
		default:
			break;
		}
    }
    
    /**
     * Recupera información para asignación de importancia por actividad y consideraciones
     * @param tipoActividad define el tipo de actividad principal, complementaria 1, complementaria 2 para recuperación de datos desde los objetos correspondientes
     * @return
     */
	public CatalogoImportanciaVO catalogoMayorImportanciaConsideraciones(Integer tipoActividad) {
		CatalogoImportanciaVO importCatalogo = new CatalogoImportanciaVO();
		Integer tipoPregunta;
		Integer tipoPermisoActividad = null;
		
		switch (tipoActividad) {
		case 1:
			tipoPregunta = ciiuPrincipal.getTipoPregunta();
			
			tipoPermisoActividad = importanciaXActiviad(ciiuPrincipal);
			if (subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuPrincipal)
					&& listaSubActividades1 != null && listaSubActividades1.size() > 0) {
				ciiuPrincipal.setSubActividades(listaSubActividades1);
				subActividadesCiiuBean.recuperarConsideracionesPorSubActividades(ciiuPrincipal);
				if (subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte() != null)
					tipoPermisoActividad = Integer.valueOf(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte().getTipoPermiso());
			}

			importCatalogo.setCatalogo(ciiuPrincipal);
			importCatalogo.setTipoPermiso(tipoPermisoActividad);
			importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(ciiuPrincipal.getCodigo().substring(0, 7)).getImportanciaRelativa());
			importCatalogo.setWf("wf1");
			
			switch (tipoPregunta) {
			case 1:
				if (parent1 != null && parent1.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent1.getTipoPermiso());
					importCatalogo.setCatalogo(parent1.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent1.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf1");
				}
				break;
			case 2:
				if (subActividad1 != null && subActividad1.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(subActividad1.getTipoPermiso());
					importCatalogo.setCatalogo(subActividad1.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(subActividad1.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf1");
				} else if (parent1 != null && parent1.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent1.getTipoPermiso());
					importCatalogo.setCatalogo(parent1.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent1.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf1");
				}
				break;
			case 4:
				if (subActividad1 != null && subActividad1.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(subActividad1.getTipoPermiso());
					importCatalogo.setCatalogo(subActividad1.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(subActividad1.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf1");
				} else if (parent1 != null && parent1.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent1.getTipoPermiso());
					importCatalogo.setCatalogo(parent1.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent1.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf1");
				}
				break;
			default:
				break;
			}
			break;
		case 2:
			tipoPregunta = ciiuComplementaria1.getTipoPregunta();
			
			tipoPermisoActividad = importanciaXActiviad(ciiuComplementaria1);
			if (subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria1)
					&& listaSubActividades2 != null && listaSubActividades2.size() > 0) {
				ciiuComplementaria1.setSubActividades(listaSubActividades2);
				subActividadesCiiuBean.recuperarConsideracionesPorSubActividades(ciiuComplementaria1);
				if (subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte() != null)
					tipoPermisoActividad = Integer.valueOf(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte().getTipoPermiso());
			}

			importCatalogo.setCatalogo(ciiuComplementaria1);
			importCatalogo.setTipoPermiso(tipoPermisoActividad);
			importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(ciiuComplementaria1.getCodigo().substring(0, 7)).getImportanciaRelativa());
			importCatalogo.setWf("wf2");
			
			switch (tipoPregunta) {
			case 1:
				if (parent2 != null && parent2.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent2.getTipoPermiso());
					importCatalogo.setCatalogo(parent2.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent2.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf2");
				}
				break;
			case 2:
				if (subActividad2 != null && subActividad2.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(subActividad2.getTipoPermiso());
					importCatalogo.setCatalogo(subActividad2.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(subActividad2.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf2");
				} else if (parent2 != null && parent2.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent2.getTipoPermiso());
					importCatalogo.setCatalogo(parent2.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent2.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf2");
				}
				break;
			case 4:
				if (subActividad2 != null && subActividad2.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(subActividad2.getTipoPermiso());
					importCatalogo.setCatalogo(subActividad2.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(subActividad2.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf2");
				} else if (parent2 != null && parent2.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent2.getTipoPermiso());
					importCatalogo.setCatalogo(parent2.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent2.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf2");
				}
				break;
			default:
				break;
			}
			break;
		case 3:
			tipoPregunta = ciiuComplementaria2.getTipoPregunta();
			
			tipoPermisoActividad = importanciaXActiviad(ciiuComplementaria2);
			if (subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria2)
					&& listaSubActividades3 != null && listaSubActividades3.size() > 0) {
				ciiuComplementaria2.setSubActividades(listaSubActividades3);
				subActividadesCiiuBean.recuperarConsideracionesPorSubActividades(ciiuComplementaria2);
				if (subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte() != null)
					tipoPermisoActividad = Integer.valueOf(subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte().getTipoPermiso());
			}

			importCatalogo.setCatalogo(ciiuComplementaria2);
			importCatalogo.setTipoPermiso(tipoPermisoActividad);
			importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(ciiuComplementaria2.getCodigo().substring(0, 7)).getImportanciaRelativa());
			importCatalogo.setWf("wf3");
			
			switch (tipoPregunta) {
			case 1:
				if (parent3 != null && parent3.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent3.getTipoPermiso());
					importCatalogo.setCatalogo(parent3.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent3.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf3");
				}
				break;
			case 2:
				if (subActividad3 != null && subActividad3.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(subActividad3.getTipoPermiso());
					importCatalogo.setCatalogo(subActividad3.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(subActividad3.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf3");
				} else if (parent3 != null && parent3.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent3.getTipoPermiso());
					importCatalogo.setCatalogo(parent3.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent3.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf3");
				} 
				break;
			case 4:
				if (subActividad3 != null && subActividad3.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(subActividad3.getTipoPermiso());
					importCatalogo.setCatalogo(subActividad3.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(subActividad3.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf3");
				} else if (parent3 != null && parent3.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent3.getTipoPermiso());
					importCatalogo.setCatalogo(parent3.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(parent3.getCatalogoCIUU().getCodigo().substring(0, 7)).getImportanciaRelativa());
					importCatalogo.setWf("wf3");
				}
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}

		return importCatalogo;
	}
	
	@Getter
	@Setter
	private Boolean esContratoOperacion;
	@Getter
	@Setter 
	private Boolean mostrarPoseeContrato = false;
	
	public void esContratoMinero(){
		if(tieneContratoMineria){
			esContratoOperacion = true;
			noEsMineriaArtesanal = false;
			concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
			listaConcesionesMineras = new ArrayList<>();
		}else{
			esContratoOperacion = false;
			noEsMineriaArtesanal = true;
			listaConcesionesMineras = new ArrayList<>();
			concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
		}		
	}
	
	public void validarCodigoMineroContratoOperacion(){
		try {
			/**
			 * Primero se va a validar si es un ćodigo de conseción o un contrato de operacion, para obtener el codigo de concesión 
			 * para las siguientes validaciones si el código es un contrato de operación
			 */
			/**en el método verificar poligono se debe llenar el objeto de concesión minera  y valida las coordenadas de implantación
			 * con las coordenadas del archivo para saber si las coordenadas son de un contrato.**/
			coordenadasRcoaBean.verificarPoligono();
			
			if(coordenadasRcoaBean.getPoligonosIguales()){
				JsfUtil.addMessageError("Coordenadas no se encuentran dentro de la concesión minera la cual le otorgó el respectivo contrato de operación.");
				return;
			}
			
			/**
			 * Una vez validadas las coordenadas de implantación con el archivo si estás coinciden con algún valor entonces obtenemos el
			 * código de la concesión minera
			 */
			if(coordenadasRcoaBean.getConcesionMinera() != null && coordenadasRcoaBean.getConcesionMinera().getCodigo() != null){
				Boolean esTitular = false;
				Boolean tienePermiso = false;
				
				/**
				 * Validamos si el contrato todavía está vigente
				 */
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				
				Date date = format.parse(coordenadasRcoaBean.getConcesionMinera().getFechaSuscripcion());
				
				Integer meses = Integer.parseInt(coordenadasRcoaBean.getConcesionMinera().getMesesPlazo());
				
				Calendar calFechaSus= Calendar.getInstance();
				calFechaSus.setTime(date);
				calFechaSus.add(Calendar.MONTH, meses);
				
				Date fechaVencimiento = calFechaSus.getTime();
				
				Calendar calFechaActual= Calendar.getInstance();
				Date fechaActual = calFechaActual.getTime();
				
				boolean contratoTerminado = false;
				
				/**
				 * Validamos el código ingresado para conocer si el operador ingreso el código de concesión o el código del contrato
				 */
				if(coordenadasRcoaBean.getConcesionMinera().getCodigo().equals(concesionMinera.getCodigo())){
					tienePermiso = true;
				}else if(coordenadasRcoaBean.getConcesionMinera().getCodigoContrato().equals(concesionMinera.getCodigo())){					
					tienePermiso = true;					
				}
								
				/**
				 * Se valida si el operador logueado tiene el mismo ruc asociado al contrato.
				 */
				if(coordenadasRcoaBean.getConcesionMinera().getRucOperador() != null && 
						coordenadasRcoaBean.getConcesionMinera().getRucOperador().equals(loginBean.getNombreUsuario())){
					esTitular = true;						
				}else if(coordenadasRcoaBean.getConcesionMinera().getNombreOperador() != null && 
						coordenadasRcoaBean.getConcesionMinera().getNombreOperador().equals(loginBean.getUsuario().getPersona().getNombre())){
					esTitular = true;
				}
				
				if(!esTitular){
					JsfUtil.addMessageError("El Código de concesión minera no pertenece a este usuario");
					return;
				}
				
				if(tienePermiso){
					
					if(fechaVencimiento.before(fechaActual)){
						contratoTerminado = true;
					}
					
					if(contratoTerminado){
						JsfUtil.addMessageError("El tiempo de vigencia del contrato ha terminado");
						return;
					}
					
					/**
					 * Se valida las coordenadas del arcom con las coordenadas geográficas que ingreso el operador
					 */												
					coordenadasRcoaBean.verificarCoordenadasGeograficasYArcom(coordenadasRcoaBean.getConcesionMinera().getCodigo());
					
					if(coordenadasRcoaBean.getConcesionMinera().getCoordenadasCoinciden()){
						concesionMinera.setNombre(coordenadasRcoaBean.getConcesionMinera().getNombre());
						concesionMinera.setRegimen(coordenadasRcoaBean.getConcesionMinera().getRegimen());
						concesionMinera.setArea(coordenadasRcoaBean.getConcesionMinera().getArea());
						concesionMinera.setMaterial(coordenadasRcoaBean.getConcesionMinera().getMaterial());
						concesionMinera.setTieneContrato(true);
						listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
						listaConcesionesMineras.add(concesionMinera);		
						
						JsfUtil.addMessageInfo("El código minero o contrato de operación fue validado con éxito.");
						
					}else{						
						JsfUtil.addMessageError("Coordenadas no coinciden con el área geográfica de la concesión minera que le otorgó el contrato de operación");
						return;
					}					
				}else{
					JsfUtil.addMessageError("Coordenadas no se encuentran dentro de la concesión minera la cual le otorgó el respectivo contrato de operación.");
					return;
				}
			}else{
				JsfUtil.addMessageError("Coordenadas del área de implantación ingresadas no coinciden con las coordenadas del contrato de operación otorgado en la presente concesión minera");
				return;
			}						
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public Boolean validarCategorizacionPorInterseccion(ValorCalculo valoresFormula, Area zonal, Area enteAcreditadoProyecto, UbicacionesGeografica provincia) {
		Boolean saltar = false;
		// validar si la actividad ciiu principal tiene validacion por capas
		CatalogoCapaPermiso capaPermiso = catalogoCapaPermisoFacade.getCapaPermisoPorProyectoCiiu(proyecto.getId(), ciiuArearesponsable.getCatalogoCIUU().getId());
		if (capaPermiso != null && capaPermiso.getId() != null) {
			ciiuArearesponsable.setIdCatalogoCapaPermiso(capaPermiso.getId());
			proyectoLicenciaCuaCiuuFacade.guardar(ciiuArearesponsable);

			if (capaPermiso.getTipoPermiso() != null && capaPermiso.getTipoPermiso().getId() != null) {
				String codigo = capaPermiso.getTipoPermiso().getCodigo();
				switch (codigo) {
				case "I":
					categoriaXNormativa = 1;
					break;
				case "II":
					categoriaXNormativa = 2;
					break;
				case "III":
					categoriaXNormativa = 3;
					break;
				case "IV":
					categoriaXNormativa = 4;
					break;
				default:
					break;
				}
			}

			if (categoriaXNormativa > 0) {
				proyecto.setCategorizacion(categoriaXNormativa);
				valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				valorCalculoFacade.guardar(valoresFormula);
			}

			if (capaPermiso.getTipoAutoridadAmbiental() != null) {
				tipoAutoridadAmbiental = capaPermiso.getTipoAutoridadAmbiental();
				switch (tipoAutoridadAmbiental) {
				case 2:
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
					proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
					saltar = true;
					break;
				case 3:
					if (enteAcreditadoProyecto != null) {
						if (enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO")) {
							proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia) == null ? zonal : areaService.getAreaGadProvincial(3, provincia));
							proyecto.setAreaInventarioForestal(zonal);
							proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
							saltar = true;
						}else if (enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO")) {
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
							proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
							saltar = true;
						}
					}else {
						if (coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado() == null) {
							proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3, provincia) == null ? zonal : areaService.getAreaGadProvincial(3, provincia));
							proyecto.setAreaInventarioForestal(zonal);
							proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
							saltar = true;
						} else {
							proyecto.setAreaResponsable(coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado() == null ? zonal : coordenadasRcoaBean.getUbicacionPrincipal().getEnteAcreditado());
							proyecto.setAreaInventarioForestal(zonal);
							proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
							saltar = true;
						}
					}
					break;
				default:
					break;
				}
			}
		} else {
			if(ciiuArearesponsable.getIdCatalogoCapaPermiso() != null) {
				ciiuArearesponsable.setIdCatalogoCapaPermiso(null);
				proyectoLicenciaCuaCiuuFacade.guardar(ciiuArearesponsable);
			}
		}

		return saltar;
	}
	
	public void validarCoordenadasContiguas(){
		coordenadasRcoaBean.verificarCoordenadasContiguas();
		esValidacionZonaMixta = true;
	}
	
	public void zonaListener(){
		if(!zona.equals("MIXTA")){
			esValidacionZonaMixta = false;
		}	
		proyecto.setZona_camaronera(zona);
	}
	
	public Boolean validarRequiereIngresoViabilidadTecnica(CatalogoCIUU actividadCiiu, SubActividades subActividad, Integer tipoActividad) throws Exception {
    	Boolean validar = false;
		if(actividadCiiu.getTipoPregunta() == null) {
			if (tipoActividad.equals(1) && ingresoViabilidadTecnica1) {
				validar = true;
			} else if (tipoActividad.equals(2) && ingresoViabilidadTecnica2) {
				validar = true;
			} else if (tipoActividad.equals(3) && ingresoViabilidadTecnica3) {
				validar = true;
			}
		} else if(actividadCiiu.getTipoPregunta().equals(4)) {
			if(subActividad.getTieneInformacionAdicional() != null && !subActividad.getTieneInformacionAdicional().equals(4)) {
				validar = true;
			} else {
				if (tipoActividad.equals(1) && ingresoViabilidadTecnica1) {
					validar = true;
				} else if (tipoActividad.equals(2) && ingresoViabilidadTecnica2) {
					validar = true;
				} else if (tipoActividad.equals(3) && ingresoViabilidadTecnica3) {
					validar = true;
				}
			}
		}
		
		return validar;
    }
	
	@EJB
	private ContactoFacade contactoFacade;
	private void enviarCorreo(){
		try {
			
			List<Usuario> listaUsuarios = new ArrayList<>();
			
			listaUsuarios = usuarioFacade.buscarUsuarioPorRolActivo("USUARIO CORREO SNAP");
			
			String tipoProyecto = "";
			if(proyecto.getCategorizacion() == 1){
				tipoProyecto = "Certificado Ambiental";
			}else if(proyecto.getCategorizacion() == 2){
				tipoProyecto = "Registro Ambiental";
			}else if(proyecto.getCategorizacion() == 3 || proyecto.getCategorizacion() == 4){
				tipoProyecto = "Licencia Ambiental";
			}
			
			String nombreUsuario = "";
			if(proyecto.getUsuario().getNombre().length() == 10){
				
				nombreUsuario = proyecto.getUsuario().getNombre() + " - " + proyecto.getUsuario().getPersona().getNombre();
				
			}else{
				
				Organizacion org = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());
				nombreUsuario = org.getRuc() + " - " + org.getNombre();
			}
			String capas = "";
			if(coordenadasRcoaBean.getListaCapas() != null && !coordenadasRcoaBean.getListaCapas().isEmpty()){
				for(String capa : coordenadasRcoaBean.getListaCapas()){
					if(!capas.contains(capa)){
						capas += capa;
					}					
				}
			}			
			
			for(Usuario usuario : listaUsuarios){
				String emailDestino = "";	
				
				List<Contacto> contacto = contactoFacade.buscarPorPersona(usuario.getPersona());
				for (Contacto con : contacto){
					if(con.getFormasContacto().getId() == 5	&& con.getEstado().equals(true)){
						emailDestino = con.getValor(); 
						break;
					}
				}				
				
				Object[] parametrosCorreo = new Object[] {
						usuario.getPersona().getNombre(),
						proyecto.getCodigoUnicoAmbiental(), tipoProyecto,
						nombreUsuario, capas };
				
				String notificacion = "";
					notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
							"bodyNotificacionIngresoProyectoSnap",
							parametrosCorreo);	
				
				
				Usuario usuarioEnvio = new Usuario();
				usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
				NotificacionAutoridadesController email = new NotificacionAutoridadesController();
				email.sendEmailInformacionProponente(emailDestino, "", notificacion, "Emisión de nuevo Proyecto", proyecto.getCodigoUnicoAmbiental(), usuario, usuarioEnvio);		        	
		        
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void validarIngresoViabilidaTecnica(CatalogoCIUU catalogo, Integer tipoActividad) {
		if(catalogo.getRequiereViabilidadPngids()) {
			if (catalogo.getCodigo().equals("E3811.00") 
					|| catalogo.getCodigo().equals("E3811.00.02") 
					|| catalogo.getCodigo().equals("E3811.00.03")) {
				if (tipoActividad.equals(1)) {
					ingresoViabilidadTecnica1 = false;
				} else if (tipoActividad.equals(2)) {
					ingresoViabilidadTecnica2 = false;
				} else if (tipoActividad.equals(3)) {
					ingresoViabilidadTecnica3 = false;
				}
				
				Organizacion orga = null;
				try {
					orga = organizacionFacade.buscarPorRuc(loginBean.getNombreUsuario());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				if (orga != null) {
					OrganizacionViabilidadTecnica orgaViabilidad = organizacionViabilidadTecnicaFacade
							.getOrganizacionPorTipoProceso(orga, "tipo.proceso.viabilidad.tecnica.gestion.residuos");
					if (orgaViabilidad != null) {
						if (orgaViabilidad.getTipoEntidad().getValor().equals(OrganizacionViabilidadTecnica.TIPO_ENTIDAD_GOBIERNO_MUNICIPAL)
								|| orgaViabilidad.getTipoEntidad().getValor().equals(OrganizacionViabilidadTecnica.TIPO_ENTIDAD_EMPRESA_MUNICIPAL)) {
							if (tipoActividad.equals(1)) {
								ingresoViabilidadTecnica1 = true;
							} else if (tipoActividad.equals(2)) {
								ingresoViabilidadTecnica2 = true;
							} else if (tipoActividad.equals(3)) {
								ingresoViabilidadTecnica3 = true;
							}
						}
					}
				}
			} else {
				if (tipoActividad.equals(1)) {
					ingresoViabilidadTecnica1 = true;
				} else if (tipoActividad.equals(2)) {
					ingresoViabilidadTecnica2 = true;
				} else if (tipoActividad.equals(3)) {
					ingresoViabilidadTecnica3 = true;
				}
			}
		}
	}

	public void validarSeleccionBotaderos(SubActividades consideracion, Integer tipoActividad) {
		if(consideracion.getCatalogoCIUU().getTipoPregunta().equals(4)) {
    		if(consideracion.getTieneInformacionAdicional() != null && consideracion.getTieneInformacionAdicional().equals(5)) {
    			Boolean esMunicipio = false;
    			Area areaEnteAcreditado = areaService.getAreaEnteAcreditado(3, loginBean.getUsuario().getNombre());
    			if (areaEnteAcreditado != null
    					&& areaEnteAcreditado.getTipoEnteAcreditado().equals("MUNICIPIO")) {
    				esMunicipio = true;
    				if (tipoActividad.equals(1)) {
    					ingresoViabilidadTecnica1 = true;
    				} else if (tipoActividad.equals(2)) {
    					ingresoViabilidadTecnica2 = true;
    				} else if (tipoActividad.equals(3)) {
    					ingresoViabilidadTecnica3 = true;
    				}
    			}

    			if (!esMunicipio) {
    				RequestContext context = RequestContext.getCurrentInstance();
    				context.execute("PF('dlgBloqueoBotaderos').show();");

    				if (tipoActividad.equals(1)) {
    					parent1 = new SubActividades();
    					subActividad1 = new SubActividades();
    					listaSubActividadesHijas1 = new ArrayList<>();
    				} else if (tipoActividad.equals(2)) {
    					parent2 = new SubActividades();
    					subActividad2 = new SubActividades();
    					listaSubActividadesHijas2 = new ArrayList<>();
    				} else if (tipoActividad.equals(3)) {
    					parent3 = new SubActividades();
    					subActividad3 = new SubActividades();
    					listaSubActividadesHijas3 = new ArrayList<>();
    				}
    			}
    		}
		}
	}
}