package ec.gob.ambiente.rcoa.simulador.controller;

import index.ContienePoligono_entrada;
import index.ContienePoligono_resultado;
import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
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
import java.util.Map.Entry;
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

import lombok.Getter;
import lombok.Setter;

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
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.client.SuiaServicesArcon;
import ec.gob.ambiente.client.SuiaServices_Service_Arcon;
//import ec.gob.ambiente.rcoa.facade.CapasGeoServerFacade;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.mapa.webservices.GenerarMapaSimuladorWS_Service;
import ec.gob.ambiente.mapa.webservices.ResponseCertificado;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.BloquesBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.dto.GeneradorCustom;
import ec.gob.ambiente.rcoa.dto.SubactividadDto;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCIUUConcurrenteFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCIUUFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCapaPermisoFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CombinacionActividadesFacade;
import ec.gob.ambiente.rcoa.facade.CriterioMagnitudFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.NombreProyectosFacade;
import ec.gob.ambiente.rcoa.facade.NombreProyectosMspFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoCoaCiuuSubActividadesFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaCiuuBloquesFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalConcesionesMinerasFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RucMspFacade;
import ec.gob.ambiente.rcoa.facade.SubActividadesFacade;
import ec.gob.ambiente.rcoa.facade.ValorCalculoFacade;
import ec.gob.ambiente.rcoa.facade.ValorMagnitudFacade;
import ec.gob.ambiente.rcoa.facade.VariableCriterioFacade;
import ec.gob.ambiente.rcoa.facade.VinculoProyectoRgdSuiaFacade;
//import ec.gob.ambiente.rcoa.formas.FormasCoordenadas;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CatalogoCIUUConcurrente;
import ec.gob.ambiente.rcoa.model.CatalogoCapaPermiso;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.CombinacionSubActividades;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.CriterioMagnitud;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.model.NombreProyectos;
import ec.gob.ambiente.rcoa.model.NombreProyectosMsp;
import ec.gob.ambiente.rcoa.model.ProyectoCoaCiuuSubActividades;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaCiuuBloques;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalConcesionesMineras;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaCiiuResiduos;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RucMsp;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.rcoa.model.ValorCalculo;
import ec.gob.ambiente.rcoa.model.ValorMagnitud;
import ec.gob.ambiente.rcoa.model.VariableCriterio;
import ec.gob.ambiente.rcoa.preliminar.contoller.CoordenadasRcoaBean;
import ec.gob.ambiente.rcoa.preliminar.contoller.ResiduosActividadesCiiuBean;
import ec.gob.ambiente.rcoa.preliminar.contoller.SubActividadesCiiuBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PermisoRegistroGeneradorDesechosFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.FormaPuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.simulador.Utils.SimuladorCoordendasPoligonos;
import ec.gob.ambiente.rcoa.simulador.facade.SimuladorCoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.simulador.facade.SimuladorDocumentosCoaFacade;
import ec.gob.ambiente.rcoa.simulador.facade.SimuladorInterseccionProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.rcoa.simulador.facade.SimuladorProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.simulador.facade.SimuladorProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorCoordenadasProyecto;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorDocumentosCOA;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorInterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.util.CatalogoImportanciaVO;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.rcoa.util.MayorAreaVO;
import ec.gob.ambiente.rcoa.util.WolframVO;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.OrganizacionViabilidadTecnicaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecnicaProyectoFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecnicaRcoaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.OrganizacionViabilidadTecnica;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnica;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnicaProyecto;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.administracion.service.AreaService;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Bloque;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.FormaPuntoRecuperacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
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
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.tipoforma.facade.TipoFormaFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.arcom.dm.ws.DerechoMineroMAEDTO;

@ManagedBean
@ViewScoped
public class RegistroProyectoSimuladorController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger
			.getLogger(RegistroProyectoSimuladorController.class);
	private static final String IMAGEN_LICENCIA_AMBIENTAL = "/resources/images/mensajes/rcoa_fin_registro_preliminar.png";
	private static final String MENSAJE_FINALIZADO = "Para continuar con el proceso, usted debe seguir en el sistema los pasos descritos en la imagen";

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
	private SimuladorProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private SimuladorCoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private TipoFormaFacade tipoFormaFacade;
	@EJB
	private SimuladorProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalCoaShapeFacade;
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
	private SimuladorInterseccionProyectoLicenciaAmbientalFacade interseccionProyectoLicenciaAmbientalFacade;
	@EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
	@EJB
	private AreaService areaService;
	@EJB
	private SimuladorDocumentosCoaFacade documentosFacade;
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
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesSeleccionadas = new ArrayList<UbicacionesGeografica>();

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
	private UploadedFile uploadedFileGeo;
	@Getter
	private UploadedFile uploadedFileImpl;
	@Getter
	@Setter
	private List<SimuladorCoordenadasProyecto> coordenadasGeograficas = new ArrayList<SimuladorCoordenadasProyecto>();
	@Getter
	@Setter
	private List<CatalogoCIUU> listaCatalogoCiiu = new ArrayList<CatalogoCIUU>();

	private TipoPoblacion tipoPoblacionSimple;
	private Integer ubicacionRepetida = 0;
	@Getter
	private List<TipoForma> tiposFormas;

	private TipoForma poligono;

	@Setter
	private String armarDiagrama;

	@Getter
	@Setter
	private SimuladorProyectoLicenciaCoa proyecto = new SimuladorProyectoLicenciaCoa();

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;

	private SimuladorProyectoLicenciaAmbientalCoaShape shape = new SimuladorProyectoLicenciaAmbientalCoaShape();

	JSONObject categoria;

	private Integer categoriaXNormativa = 0;

	private Integer tipoAutoridadAmbiental = 1;

	@Getter
	@Setter
	private CatalogoCIUU ciiuPrincipal = new CatalogoCIUU();
	@Getter
	@Setter
	private CatalogoCIUU ciiuComplementaria1 = new CatalogoCIUU();
	@Getter
	@Setter
	private CatalogoCIUU ciiuComplementaria2 = new CatalogoCIUU();
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu ciiuArearesponsable = new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	private List<VariableCriterio> listaCriterioMagnitudConsumo = new ArrayList<VariableCriterio>();

	@Getter
	@Setter
	private VariableCriterio criterioSeleccion = new VariableCriterio();
	
	@Getter
	@Setter
	private ValorCalculo valoresFormula = new ValorCalculo();
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
	private String criteriomagnitud = "";

	@Getter
	@Setter
	private List<SimuladorInterseccionProyectoLicenciaAmbiental> intersecciones = new ArrayList<SimuladorInterseccionProyectoLicenciaAmbiental>();

	@Getter
	@Setter
	private SimuladorDocumentosCOA documentoMapa, documentoCertificado,
			documentoAltoImpacto, documentoGeneticoCiiu1,
			documentoGeneticoCiiu2, documentoGeneticoCiiu3,
			documentoDocSectorial, documentoDocFrontera,
			documentoDocCamaronera;

	private GenerarMapaSimuladorWS_Service wsMapas;

	private Usuario usuarioAutoridad;

	@Getter
	@Setter
	private UbicacionesGeografica ubicacionPrincipal = new UbicacionesGeografica();
	private UbicacionesGeografica ubicacionOficinaTecnica = new UbicacionesGeografica();
	private MayorAreaVO area = new MayorAreaVO();
	private List<CapasCoa> capas = new ArrayList<CapasCoa>();

	private Integer valorCapacidadSocial = 0;
	private Integer valorCapacidadBiofisica = 0;

	private boolean estadoMagnitud;
	private boolean estadoSustanciasQuimicas;
	@Getter
	@Setter
	private boolean esMercurio;
	private boolean esActividadExtractiva = false;
	@Getter
	@Setter
	private boolean fabricaSustancia;
	@Getter
	@Setter
	private String nombreSector = "";
	@Getter
	@Setter
	private boolean estadoZonaIntangible, estadoZonaIntangibleAux;
	@Getter
	@Setter
	private boolean estadoFrontera;
	@Getter
	@Setter
	private String responsable;

	@Getter
	@Setter
	private CatalogoCIUU actividadPrimaria = new CatalogoCIUU();

	private List<String> msgError = new ArrayList<String>();

	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu ciiu1 = new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu ciiu2 = new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu ciiu3 = new ProyectoLicenciaCuaCiuu();
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
	private String pathImagen = IMAGEN_LICENCIA_AMBIENTAL;
	@Getter
	@Setter
	private String mensaje = MENSAJE_FINALIZADO;

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
	private BigDecimal superficieMetrosCuadrados;
	@Getter
	@Setter
	private double superficieGeograficaMetros, superficieGeograficaHa;

	@Getter
	@Setter
	private boolean mostrarDetalleInterseccion = false;

	@Getter
	@Setter
	private boolean esZonaSnapEspecial = false;
	
	@Getter
	@Setter
	private String zonasInterccionDetalle = "";

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
	private Boolean esCiiu1HidrocarburoMineriaElectrico = false,
			esCiiu2HidrocarburoMineriaElectrico = false,
			esCiiu3HidrocarburoMineriaElectrico = false;

	@Setter
	@Getter
	private List<SimuladorDocumentosCOA> listaDocumentosEliminar = new ArrayList<SimuladorDocumentosCOA>();

	@Getter
	@Setter
	private SimuladorDocumentosCOA documentoSenaguaCiiu1,
			documentoSenaguaCiiu2, documentoSenaguaCiiu3;

	@Getter
	@Setter
	private SimuladorDocumentosCOA documentoPorSectorCiiu1,
			documentoPorSectorCiiu2, documentoPorSectorCiiu3; // autorizaciones
																// para las
																// actividades
																// de
																// Hidrocarburos,
																// Minería o
																// Eléctrico

	@Getter
	@Setter
	private SimuladorDocumentosCOA documentoViabilidadPngidsCiiu1,
			documentoViabilidadPngidsCiiu2, documentoViabilidadPngidsCiiu3;

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
	private boolean subActivadesAlcantarillado1, subActivadesAlcantarillado2,
			subActivadesAlcantarillado3;

	@Getter
	@Setter
	private boolean subActivadesGalapagos1, subActivadesGalapagos2,
			subActivadesGalapagos3;

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
	private Boolean areaPendienteAsignar = false;

	@Getter
	@Setter
	private String nombreUsuario;

	@Getter
	@Setter
	private String rucCedula;

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
	private String codigoOficioGRECI1, codigoOficioGRECI2, codigoOficioGRECI3;

	@Getter
	@Setter
	private ViabilidadTecnica viabilidad1, viabilidad2, viabilidad3;

	@Getter
	@Setter
	private List<GeneradorCustom> listaRegistrosGeneradores,
			vinculacionesRgdEliminar;

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
	private boolean esMercurioTransporta;

	@Getter
	@Setter
	private Boolean tieneDocumentoRsq;

	@Getter
	@Setter
	private Boolean rsqVigente;
	
	@Getter
	@Setter
	private SubActividades actividadBloque1 = new SubActividades(),
			actividadBloque2 = new SubActividades(),
			actividadBloque3 = new SubActividades(),
			subActividadBloque2 = new SubActividades();

	@Getter
	@Setter
	private String validaSubcategoria = null;
	
	@Getter
	@Setter
	private boolean esSubactividadSubestacionElectrica = false;
	
	@Getter
	@Setter
	private Boolean ingresoViabilidadTecnica1, ingresoViabilidadTecnica2,
			ingresoViabilidadTecnica3;

	@Getter
	@Setter
	private Boolean tieneContratoMineria;
	
	@Getter
	@Setter
	private String zona;

	@Getter
	@Setter
	private Boolean esValidacionZonaMixta = false;

	@PostConstruct
	public void inicio() {
		rucCedula = (String) JsfUtil.devolverObjetoSession("rucCedula");
		nombreUsuario = (String) JsfUtil.devolverObjetoSession("nombreUsuario");

		loginBean.setUsuario(usuarioFacade.buscarUsuario(rucCedula));
		System.out.println("usuario: " + loginBean.getUsuario().getNombre());
		if (loginBean.getUsuario().getNombre() != null) {
			loginBean.setNombreUsuario(rucCedula);
			loginBean.setPassword(loginBean.getUsuario().getPassword());
		} else {
			loginBean.setUsuario(new Usuario());
			loginBean.setNombreUsuario(rucCedula);
			loginBean.getUsuario().setNombre(rucCedula);
			loginBean.getUsuario().setPersona(new Persona());
			loginBean.getUsuario().getPersona().setNombre(nombreUsuario);
			loginBean.getUsuario().getPersona().setPin(rucCedula);
		}
		try {
			ayudaCatalogoCiiu = documentosSuiaIIIFacade
					.descargarDocumentoPorNombreYDirectorioBase(
							Constantes.AYUDA_CATALOGO_CIIU, null);
		} catch (Exception e) {
		}
		tiposPoblaciones = proyectoLicenciamientoAmbientalFacade
				.getTiposPoblaciones();
		tiposPoblaciones
				.remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_PERIFERICA));
		tiposPoblaciones
				.remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_CONTINENTAL));
		tiposPoblaciones
				.remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_NO_CONTINENTAL));
		tiposPoblaciones
				.remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_MIXTO));
		listaSustanciasQuimicas = proyectoLicenciaCoaFacade
				.listaSustanciasQuimicas();
		tiposFormas = tipoFormaFacade.listarTiposForma();
		poligono = getTipoForma(TipoForma.TIPO_FORMA_POLIGONO);
		listaCatalogoCiiu = catalogoCIUUFacade.listaCatalogoCiuu();
		proyecto.setSuperficie(BigDecimal.ZERO);
		capas = capasCoaFacade.listaCapas();
		superficieMetrosCuadrados = BigDecimal.ZERO;
		nivelCriterios1();
		nivelCriterios2();
		nivelCriterios3();
		validarCodigoRgd(false);
		
		ingresoViabilidadTecnica1 = true;
		ingresoViabilidadTecnica2 = true;
		ingresoViabilidadTecnica3 = true;

		// mineria =MAE-RA-2020-415509
		// hidrocarburos =MAE-RA-2020-415514
		// proyecto=proyectoLicenciaCoaFacade.buscarProyecto("MAE-RA-2020-415560");
		listaSustanciaQuimicaSeleccionadaOtros = new ArrayList<SustanciaQuimicaPeligrosa>();
		listaSustanciaQuimicaSeleccionadaOtrosTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
		tipoProyecto = proyectosBean.getTipoProyectoRcoa();
		if (proyecto.getCodigoUnicoAmbiental() != null) {
			// cargaDatos();
		} else {
			// cuando el proyecto viene desde proyecto pendiente o por
			// modificación de informacion
			if (proyectosBean.getProyectoRcoa() != null) {
				//proyecto = proyectosBean.getProyectoRcoa();
				tipoProyecto = proyecto.getTipoProyecto();

				if (proyectosBean.getModificarProyectoRcoa())
					irFinalizar = true;
				else
					tramiteEnProceso = true;
				// cargaDatos();

				proyectosBean.setModificarProyectoRcoa(false);
				proyectosBean.setProyectoRcoa(null);
			}
		}

		listaProyectosMsp = new ArrayList<RucMsp>();
		listaNombresProyectosMsp = new ArrayList<NombreProyectos>();
		listaNombresProyectosMspVal = new ArrayList<NombreProyectos>();
		vinculacionesRgdEliminar = new ArrayList<>();

		// listaNombresProyectosMspVal =
		// rucMspFacade.buscarProyectosPorRuc(loginBean.getNombreUsuario());

		RucMsp userMsp = rucMspFacade
				.buscarPorRuc(rucCedula);

		if (userMsp != null && userMsp.getId() != null) {
			usuarioMsp = true;
			mostrarTextArea = false;

			listaTiposNombresMsp = nombreProyectosFacade.listaTiposNombres(6);
			setIdTipoNombreMsp(-1);

			if (proyecto != null && proyecto.getId() != null) {
				nombreProyectoMsp = nombreProyectosMspFacade
						.buscarPorIdProyecto(proyecto.getId());

				if (nombreProyectoMsp != null) {
					if (nombreProyectoMsp.getNombreProyectos() != null) {

						nombreProyecto = nombreProyectoMsp.getNombreProyectos();

						if (nombreProyectoMsp.getRucMsp() != null) {
							rucMsp = nombreProyectoMsp.getRucMsp();
						}
						if (nombreProyecto.getCatalogoGeneralCoa() != null) {
							setIdTipoNombreMsp(nombreProyecto
									.getCatalogoGeneralCoa().getId());
						}
						setIdRucMsp(nombreProyecto.getId());

					} else {
						if (proyecto.getNombreProyecto() != null) {
							setIdTipoNombreMsp(0);
							setIdRucMsp(0);
							mostrarTextArea = true;
							otroMsp = true;
						}
					}
				} else {
					if (proyecto.getNombreProyecto() != null) {
						setIdTipoNombreMsp(0);
						setIdRucMsp(0);
						mostrarTextArea = true;
						otroMsp = true;
					}
				}
			}
		}

		operadorEsGobiernoOMunicipio = false;
		Area areaEnteAcreditado = areaService.getAreaEnteAcreditado(3,
				loginBean.getUsuario().getNombre());
		if (areaEnteAcreditado != null
				&& (areaEnteAcreditado.getTipoEnteAcreditado().equals(
						"MUNICIPIO") || areaEnteAcreditado
						.getTipoEnteAcreditado().equals("GOBIERNO")))
			operadorEsGobiernoOMunicipio = true;

		residuosActividadesCiiuBean.buscarCatalogoResiduosActividades();

		mensajeNotificacionCamaroneras = "Las coordenadas ingresadas no corresponden a la concesión otorgada por favor verifique y vuelva a adjuntar las coordenadas. En el caso de que el inconveniente persista, por favor comunicarse con la Subsecretaría de Acuacultura.";
		try {
			mensajeBotonAyudaCamaroneras = mensajeNotificacionFacade.recuperarValorMensajeNotificacionSD("mensajeRegistroPreliminarBotonAyudaCamaroneras");
		} catch (ServiceException e) {
			e.printStackTrace();
		};

		listaTipoRegimenMinero = catalogoCoaFacade
				.obtenerCatalogo(CatalogoTipoCoaEnum.TIPO_REGIMEN_MINERO);
		mostrarSubBloque=false;

	}
	
	public boolean validarServicios() {
		boolean estado = false;
		try {
			wsMapas = new GenerarMapaSimuladorWS_Service(new URL(Constantes
					.getGenerarMapaWS().replace("generarMapaWS",
							"generarMapaSimuladorWS")));
			estado = true;
		} catch (WebServiceException e) {
			estado = false;
			e.printStackTrace();
			System.out.println("Servicio no disponible ---> "
					+ Constantes.getGenerarMapaWS());
		} catch (MalformedURLException e) {
			estado = false;
		}
		return estado;
	}

	public String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return "[" + sb.toString() + "]";
	}

	public JSONArray readJsonFromUrl(String url) throws IOException {
		InputStream is = new URL(url).openStream();
		JSONArray json = null;
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
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
			if (tf.getId() == id)
				return tf;
		}
		return null;
	}

	public CapasCoa getCapa(String capa) {
		for (CapasCoa c : capas) {
			if (c.getAbreviacion().equals(capa))
				return c;
		}
		return null;
	}

	public String onFlowProcess(FlowEvent event) throws Exception {
		List<String> msg = new ArrayList<String>();
		// valida el paso siguiente para proyectos de camaronera
		if (validarNextSetp != null) {
			if (event.getNewStep().equals("paso2")
					&& (validarNextSetp == false)
					&& (catalogoCamaronera_ciuu1 == true
							|| catalogoCamaronera_ciuu2 == true || catalogoCamaronera_ciuu3 == true)) {
				JsfUtil.addMessageError(mensajeNotificacionCamaroneras);
				limpiarCamposModificarProyecto();
				limpiarCiiu1();
				limpiarCiiu2();
				limpiarCiiu3();
				// limpiarNotificacion();
				RequestContext.getCurrentInstance()
						.execute("resetPageScroll()");
				validarNextSetp = true;
				return "paso1";
			}
			if (event.getNewStep().equals("paso2")
					&& (catalogoCamaronera_ciuu1 == true
							|| catalogoCamaronera_ciuu2 == true || catalogoCamaronera_ciuu3 == true)
					&& validarNextSetp == true
					&& coordenadasRcoaBean.getIntersecaConCamaronera() == false) {
				JsfUtil.addMessageError(mensajeNotificacionCamaroneras);
				limpiarCamposModificarProyecto();
				limpiarCiiu1();
				limpiarCiiu2();
				limpiarCiiu3();
				// limpiarNotificacion();
				RequestContext.getCurrentInstance()
						.execute("resetPageScroll()");
				validarNextSetp = true;
				return "paso1";
			}
		}
		mostarMensaje=false;
		/*Integer interseca = coordenadasRcoaBean.getListaNombresCapasInterseca().size();
		if ((event.getNewStep().equals("paso2") 
				&& ((bloquearCiiu1) || (bloquearCiiu2) || (bloquearCiiu3)) && interseca==0)) {				
			limpiarCamposModificarProyecto();
			limpiarCiiu1();
			limpiarCiiu2();
			limpiarCiiu3();
			RequestContext.getCurrentInstance().execute("resetPageScroll()");
			RequestContext context = RequestContext.getCurrentInstance();
			mostarMensaje = true;
			bloquearGad = false;
			bloquearCiiu1 = false;
			bloquearCiiu2 = false;
			bloquearCiiu3 = false;
			context.execute("PF('dlgBloqueoGad').show();");
			return "paso1";
		}*/
		
		if (event.getOldStep().equals("paso3")) {
			// cuando se regresa desde el paso3 Finalizar para modificar los
			// datos
			limpiarCamposModificarProyecto();

			RequestContext.getCurrentInstance().execute("resetPageScroll()");
			return "paso1"; // se activa el primer paso
		}
		if (event.getNewStep().equals("paso2")) {
			if (coordenadasGeograficas.size() == 0)
				msg.add("Ingrese coordenadas del área geográfica");
			if (coordinatesWrappers.size() == 0)
				msg.add("Ingrese coordenadas del área implantación");
			if (ubicacionesSeleccionadas.size() == 0
					&& coordinatesWrappers.size() > 0)
				msg.add("No se ha establecido la ubicación del proyecto obra o actividad, comuníquese con mesa de ayuda");
			if (ubicacionRepetida == 1)
				msg.add("Parroquia se encuentra repetida");
			if (ciiuPrincipal.getId() == null)
				msg.add("Seleccione la actividad principal del CIIU");
			if (ciiu1.getGenetico() != null && ciiu1.getGenetico()
					&& documentoGeneticoCiiu1 == null)
				msg.add("Adjuntar la resolución otorgada por la Comisión Nacional de Bioseguridad CONABIO, Actividad Principal");
			if (ciiu2.getGenetico() != null && ciiu2.getGenetico()
					&& documentoGeneticoCiiu2 == null)
				msg.add("Adjuntar la resolución otorgada por la Comisión Nacional de Bioseguridad CONABIO, Actividad Complementaria 1");
			if (ciiu3.getGenetico() != null && ciiu3.getGenetico()
					&& documentoGeneticoCiiu3 == null)
				msg.add("Adjuntar la resolución otorgada por la Comisión Nacional de Bioseguridad CONABIO, Actividad Complementaria 2");
			if (estadoZonaIntangible && documentoDocSectorial == null)
				msg.add("Adjuntar autorizaciones o pronunciamientos sectoriales");
			if (nombreSector.equals("MINERÍA")
					&& listaConcesionesMineras.size() == 0) {
				if (esContratoOperacion) {
					msg.add("Realizar la validación de campo Código minero o contrato de operación");
				} else {
					msg.add("Agregar la o las concesiones minera");
				}
			}
			if (nombreSector.equals("HIDROCARBUROS")
					&& actividadPrimaria.getBloques()
					&& bloquesBean.getBloquesSeleccionados().size() == 0)
				msg.add("Seleccionar minimo un bloque");
			if (esCiiu1HidrocarburoMineriaElectrico
					&& documentoPorSectorCiiu1 == null)
				msg.add("Adjuntar la resolución de asignación de bloque o campo, la autorización de operación o factibilidad o título minero emitida por la autoridad sectorial de hidrocarburos y minería, según corresponda, Actividad Principal");
			if (esCiiu2HidrocarburoMineriaElectrico
					&& documentoPorSectorCiiu2 == null)
				msg.add("Adjuntar la resolución de asignación de bloque o campo, la autorización de operación o factibilidad o título minero emitida por la autoridad sectorial de hidrocarburos y minería, según corresponda, Actividad Complementaria 1");
			if (esCiiu3HidrocarburoMineriaElectrico
					&& documentoPorSectorCiiu3 == null)
				msg.add("Adjuntar la resolución de asignación de bloque o campo, la autorización de operación o factibilidad o título minero emitida por la autoridad sectorial de hidrocarburos y minería, según corresponda, Actividad Complementaria 2");

			catalogoMayorImportancia();
			bloqueoCamposGestionTransporte = false;
			if (actividadPrimaria.getScoutDrilling() != null
					&& proyecto.getConcesionMinera() != null) {
				if (actividadPrimaria.getScoutDrilling()
						&& proyecto.getConcesionMinera()
						&& listaConcesionesMineras.size() > 5)
					msg.add("El límite máximo de concesiones es 5");

				// las actividades de scout drilling inician obligatorio el
				// proceso de RGD
				if (actividadPrimaria.getScoutDrilling())
					proyecto.setGeneraDesechos(true);
				else if (proyecto.getId() == null)
					proyecto.setGeneraDesechos(null);
			} else {
				if (subActividadesCiiuBean
						.esActividadRecupercionMateriales(actividadPrimaria)) {
					subActividadesCiiuBean
							.recuperarConsideracionesPorSubActividades(actividadPrimaria);
					if (subActividadesCiiuBean
							.getCombinacionSubActividadesTipoEnte() == null) {
						msg.add("Revise que todas las opciones para la actividad estén seleccionadas");
					}
					if (subActividadesCiiuBean
							.getCombinacionSubActividadesProcesos() != null) {
						proyecto.setGestionDesechos(subActividadesCiiuBean
								.getCombinacionSubActividadesProcesos()
								.getRequiereGestionResiduosDesechos());
						proyecto.setTransportaSustanciasQuimicas(subActividadesCiiuBean
								.getCombinacionSubActividadesProcesos()
								.getRequiereTransporteSustanciasQuimicas());
						bloqueoCamposGestionTransporte = true;
					} else {
						proyecto.setGestionDesechos(null);
						proyecto.setTransportaSustanciasQuimicas(null);
					}
				}
			}
			if (actividadSubYCombi.getSubActividades() != null
					|| actividadSubYCombi.getCombinacionSubActividades() != null) {
				Boolean gestion = null, transporteSustancias = null;
				if (actividadSubYCombi.getSubActividades() != null) {
					gestion = actividadSubYCombi.getSubActividades()
							.getRequiereGestionResiduosDesechos();
					transporteSustancias = actividadSubYCombi
							.getSubActividades()
							.getRequiereTransporteSustanciasQuimicas();
				}
				if (actividadSubYCombi.getCombinacionSubActividades() != null) {
					gestion = actividadSubYCombi.getCombinacionSubActividades()
							.getRequiereGestionResiduosDesechos();
					transporteSustancias = actividadSubYCombi
							.getCombinacionSubActividades()
							.getRequiereTransporteSustanciasQuimicas();
				}
				if (gestion != null || transporteSustancias != null)
					bloqueoCamposGestionTransporte = true;

				proyecto.setGestionDesechos(gestion);
				proyecto.setTransportaSustanciasQuimicas(transporteSustancias);
			}
			String actividadesBloqueoGestionTransporte = Constantes
					.getActividadesBloquearGestionTransporte();
			List<String> actividadesBloqueoGestionTransporteArray = Arrays
					.asList(actividadesBloqueoGestionTransporte.split(","));
			if (actividadSubYCombi.getCatalogo() != null) {
				if (actividadesBloqueoGestionTransporteArray
						.contains(actividadSubYCombi.getCatalogo().getCodigo())) {
					proyecto.setGestionDesechos(false);
					proyecto.setTransportaSustanciasQuimicas(false);
					bloqueoCamposGestionTransporte = true;
				}
			}

			if (requiereTablaResiduo1
					&& residuosActividadesCiiuBean.getListaCiiuResiduos1()
							.size() == 0)
				msg.add("Ingrese mínimo una identificación de residuos o desechos no peligrosos o especiales no peligrosos");
			if (requiereTablaResiduo2
					&& residuosActividadesCiiuBean.getListaCiiuResiduos2()
							.size() == 0)
				msg.add("Ingrese mínimo una identificación de residuos o desechos no peligrosos o especiales no peligrosos");
			if (requiereTablaResiduo3
					&& residuosActividadesCiiuBean.getListaCiiuResiduos3()
							.size() == 0)
				msg.add("Ingrese mínimo una identificación de residuos o desechos no peligrosos o especiales no peligrosos");

			if (ciiuPrincipal.getRequiereViabilidadPngids() != null
					&& ciiuPrincipal.getRequiereViabilidadPngids()) {
				Boolean validar = validarRequiereIngresoViabilidadTecnica(
						ciiuPrincipal, parent1, 1);
				if (validar) {
					if (codigoOficioGRECI1.isEmpty()) {
						msg.add("Ingrese el código del Oficio de Viabilidad Técnica");
					} else {
						String mensaje = validarCodigoViabilidad(
								codigoOficioGRECI1, 1, ciiuPrincipal, parent1);
						if (mensaje != null) {
							msg.add(mensaje);
						}
					}

					if (documentoViabilidadPngidsCiiu1 == null
							|| documentoViabilidadPngidsCiiu1
									.getContenidoDocumento() == null)
						msg.add("Adjuntar el oficio de aprobación de viabilidad técnica emitido por el Proyecto Gestión de Residuos Sólidos y Economía Circular Inclusiva GRECI");
				}
			}
			if (ciiuComplementaria1 != null
					&& ciiuComplementaria1.getRequiereViabilidadPngids() != null
					&& ciiuComplementaria1.getRequiereViabilidadPngids()) {
				Boolean validar = validarRequiereIngresoViabilidadTecnica(
						ciiuComplementaria1, parent2, 2);
				if (validar) {
					if (codigoOficioGRECI2.isEmpty()) {
						msg.add("Ingrese el código del Oficio de Viabilidad Técnica");
					} else {
						String mensaje = validarCodigoViabilidad(
								codigoOficioGRECI2, 2, ciiuComplementaria1,
								parent2);
						if (mensaje != null) {
							msg.add(mensaje);
						}
					}

					if (documentoViabilidadPngidsCiiu2 == null
							|| documentoViabilidadPngidsCiiu2
									.getContenidoDocumento() == null)
						msg.add("Adjuntar el oficio de aprobación de viabilidad técnica emitido por el Proyecto Gestión de Residuos Sólidos y Economía Circular Inclusiva GRECI");
				}
			}
			if (ciiuComplementaria2 != null
					&& ciiuComplementaria2.getRequiereViabilidadPngids() != null
					&& ciiuComplementaria2.getRequiereViabilidadPngids()) {
				Boolean validar = validarRequiereIngresoViabilidadTecnica(
						ciiuComplementaria2, parent3, 3);

				if (validar) {
					if (codigoOficioGRECI3.isEmpty()) {
						msg.add("Ingrese el código del Oficio de Viabilidad Técnica");
					} else {
						String mensaje = validarCodigoViabilidad(
								codigoOficioGRECI3, 3, ciiuComplementaria2,
								parent3);
						if (mensaje != null) {
							msg.add(mensaje);
						}
					}

					if (documentoViabilidadPngidsCiiu3 == null
							|| documentoViabilidadPngidsCiiu3
									.getContenidoDocumento() == null)
						msg.add("Adjuntar el oficio de aprobación de viabilidad técnica emitido por el Proyecto Gestión de Residuos Sólidos y Economía Circular Inclusiva GRECI");
				}
			}
			
			if (catalogoCamaronera_ciuu1 == true
					|| catalogoCamaronera_ciuu2 == true
					|| catalogoCamaronera_ciuu3 == true) {
				if(zona.equals("")){
					msg.add("Seleccione la zona en la cuál se encuentra su proyecto");
				}else if(!esValidacionZonaMixta && zona.equals("MIXTA")){
					msg.add("Debe realizar validación de los polígonos por seleccionar zona Mixta");
				}else if(!coordenadasRcoaBean.getPoligonosContiguos() && zona.equals("MIXTA")){
					msg.add("Los polígonos ingresados no son contiguos");
				}
			}

			if (msg.size() > 0) {
				JsfUtil.addMessageError(msg);
				return event.getOldStep();
			} else {
				RequestContext.getCurrentInstance()
						.execute("resetPageScroll()");
				return event.getNewStep();
			}
		}

		if (event.getNewStep().equals("paso3")) {
			if (proyecto.getAltoImpacto() != null && proyecto.getAltoImpacto()) {
				if (documentoAltoImpacto == null)
					msg.add("Adjuntar documento resolución de alto impacto ambiental o interés nacional");
			}

			if (proyecto.getSustanciasQuimicas())
				if (sustanciaQuimicaSeleccionada.size() == 0)
					msg.add("Seleccionar una sustancia química");

			if (proyecto.getTransportaSustanciasQuimicas())
				if (sustanciaQuimicaSeleccionadaTransporta.size() == 0)
					msg.add("Seleccionar una sustancia química");

			magnitudMayorImportancia();

			if (!calculoWolfram()) {
				System.out.println("error wolfram");
				msg.add("Error inesperado, comuníquese con mesa de ayuda");
			}
			if (!validarServicios()) {
				msg.add("Error inesperado, comuníquese con mesa de ayuda");
			}

			if (msg.size() > 0) {
				JsfUtil.addMessageError(msg);
				return event.getOldStep();
			} else {
				if (!irFinalizar)
					presentar();
				else {
					RequestContext.getCurrentInstance().update(
							"frmPreliminar:certificadoIntercepcionRcoa");
					RequestContext context = RequestContext
							.getCurrentInstance();

					if (proyecto.getAreaResponsable() != null
							&& proyecto.getAreaResponsable()
									.getAreaAbbreviation()
									.equalsIgnoreCase("ND")) {
						if (!getMensajeIntersecaMar().equals("")) {
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

		if (skip) {
			skip = false; // reset in case user goes back
			return "confirm";
		} else {
			return event.getNewStep();
		}
	}
	
	private String validarCodigoViabilidad(String codigo, int ciiu,
			CatalogoCIUU ciiuC, SubActividades parent) {
		int respuesta = validarCodigoViabilidadInterno(codigo, ciiu, ciiuC,
				parent);
		String mensaje = "";

		switch (respuesta) {
		case 3:
			mensaje = null;
			break;
		case 1:
			mensaje = "El código de oficio " + codigo
					+ " ya fue utilizado en otro trámite.";
			break;
		case 2:
			mensaje = "El código de oficio " + codigo + " no fue encontrado";
			break;
		case 0:
			mensaje = "Código de oficio no encontrado";
			break;
		case 4:
			mensaje = "El código de oficio " + codigo
					+ " corresponde a otro operador";

			if (ciiuC.getTipoPregunta() != null
					&& ciiuC.getTipoPregunta().equals(4)
					&& parent.getTieneInformacionAdicional() != null
					&& parent.getTieneInformacionAdicional().equals(4)) {
				mensaje = "Estimado usuario/a el oficio ingresado no corresponde a la Empresa Pública que usted representa";
			}
			break;
		case 5:
			mensaje = "El código de oficio " + codigo
					+ " no corresponde a Celda Emergente";
			break;
		case 6:
			mensaje = "El código de oficio " + codigo
					+ " no corresponde a Cierres Técnicos o GIRS";
			break;
		case 7:
			mensaje = "El código de oficio "
					+ codigo
					+ " corresponde a otro operador y ya fue utilizado en otro trámite";
			break;
		case 8:
			mensaje = "El código de oficio " + codigo
					+ " no corresponde a Escombreras";
			break;
		case 9:
			mensaje = "El código de oficio " + codigo
					+ " no corresponde a Relleno sanitario";
			break;
		case 10:
			mensaje = "El código de oficio " + codigo
					+ " no corresponde a Cierres Técnicos";
			break;
		default:
			break;
		}

		return mensaje;
	}

	private Integer validarCodigoViabilidadInterno(String codigo, int ciiu,
			CatalogoCIUU ciiuC, SubActividades parent) {
		try {
			boolean modificacion = false;
			boolean celdaEmergente = false;
			boolean escombreras = false;
			boolean rellenoSanitario = false;
			boolean cierreBotaderos = false;

			List<ViabilidadTecnicaProyecto> listaViabilidadesProyecto = new ArrayList<>();
			List<ViabilidadTecnicaProyecto> listaViabilidadesProyecto2 = new ArrayList<>();
			if (ciiuC.getCodigo().compareTo("E3821.01") == 0
					|| ciiuC.getCodigo().compareTo("E3821.01.01") == 0) {
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
						listaViabilidadesProyecto = viabilidadTecnicaProyectoFacade
								.buscarPorCodigoOficioProyectoTipoExiste(
										codigo, proyecto.getId(),
										tipoOficioViabilidad);
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
						listaViabilidadesProyecto = viabilidadTecnicaProyectoFacade
								.buscarPorCodigoOficioProyectoTipoExiste(
										codigo, proyecto.getId(), 3);
						if (listaViabilidadesProyecto != null
								&& !listaViabilidadesProyecto.isEmpty()) {
							modificacion = true;
						}
					} else {
						modificacion = false;
					}
				}

			} else {
				celdaEmergente = false;
				if (proyecto.getId() != null) {
					listaViabilidadesProyecto = viabilidadTecnicaProyectoFacade
							.buscarPorCodigoOficioProyectoTipoExiste(codigo,
									proyecto.getId(), 1);
					listaViabilidadesProyecto2 = viabilidadTecnicaProyectoFacade
							.buscarPorCodigoOficioProyectoTipoExiste(codigo,
									proyecto.getId(), 2);

					if ((listaViabilidadesProyecto != null && !listaViabilidadesProyecto
							.isEmpty())
							|| (listaViabilidadesProyecto2 != null && !listaViabilidadesProyecto2
									.isEmpty())) {
						modificacion = true;
					}
				} else {
					modificacion = false;
				}
			}

			if (!modificacion) {

				List<ViabilidadTecnicaProyecto> listaAux = viabilidadTecnicaProyectoFacade
						.buscarPorCodigoOficioExiste(codigo);
				boolean desvincular = false;

				if (listaAux != null && !listaAux.isEmpty()) {
					if (!listaAux.get(0).getProyecto().getEstado()) {
						desvincular = true;
					} else {
						// viabilidad
						if (listaAux.get(0).getProyecto()
								.getTieneViabilidadFavorable() != null
								&& !listaAux.get(0).getProyecto()
										.getTieneViabilidadFavorable()) {
							desvincular = true;
						}
						// estuido

						desvincular = viabilidadTecnicaProyectoFacade
								.validarArchivoProyecto(listaAux.get(0)
										.getProyecto());
					}
				}

				if (desvincular) {
					ViabilidadTecnicaProyecto viabilidadP = listaAux.get(0);
					viabilidadP.setEstado(false);
					// viabilidadTecnicaProyectoFacade.guardar(viabilidadP,
					// JsfUtil.getLoggedUser());
				}

				List<ViabilidadTecnicaProyecto> lista = viabilidadTecnicaProyectoFacade
						.buscarPorCodigoOficioExiste(codigo);
				if (lista != null && !lista.isEmpty()) {
					if (lista.get(0).getViabilidadTecnica().getRuc()
							.equals(JsfUtil.getLoggedUser().getNombre())) {
						return 1; // el código del oficio fue utilizado en otro
									// proyecto.
					} else {
						return 7;
					}
				} else {
					List<ViabilidadTecnica> listaViabilidades = new ArrayList<>();

					List<Integer> tipos = new ArrayList<>();

					if (celdaEmergente) {
						tipos.add(2);
					} else if (escombreras) {
						tipos.add(4);
					} else if (rellenoSanitario) {
						tipos.add(3);
					} else if (cierreBotaderos) {
						tipos.add(1);
					} else {
						tipos.add(1);
						tipos.add(3);
					}

					listaViabilidades = viabilidadTecnicaFacade
							.buscarPorCodigoUsuarioTipo(codigo,
									JsfUtil.getLoggedUser(), tipos);
					if (rellenoSanitario
							&& listaViabilidades.size() > 0
							&& (listaViabilidades.get(0)
									.getEsRellenoSanitario() == null || !listaViabilidades
									.get(0).getEsRellenoSanitario())) {
						listaViabilidades = new ArrayList<>();
					}

					if (listaViabilidades == null
							|| listaViabilidades.isEmpty()) {
						List<ViabilidadTecnica> listaExisteCodigo = viabilidadTecnicaFacade
								.buscarPorCodigo(codigo);
						if (listaExisteCodigo != null
								&& !listaExisteCodigo.isEmpty()) {
							if (listaExisteCodigo
									.get(0)
									.getRuc()
									.equals(JsfUtil.getLoggedUser().getNombre())) {
								if (celdaEmergente)
									return 5;
								else if (escombreras) {
									return 8;
								} else if (rellenoSanitario) {
									return 9;
								} else if (cierreBotaderos) {
									return 10;
								} else
									return 6;
							} else {
								return 4;
							}
						} else {
							return 2; // el codigo del oficio no existe
						}
					} else {
						if (ciiu == 1) {
							viabilidad1 = listaViabilidades.get(0);
						} else if (ciiu == 2) {
							viabilidad2 = listaViabilidades.get(0);
						} else if (ciiu == 3) {
							viabilidad3 = listaViabilidades.get(0);
						}
					}
				}
			} else {
				if (ciiu == 1) {
					viabilidad1 = listaViabilidadesProyecto.get(0)
							.getViabilidadTecnica();
				} else if (ciiu == 2) {
					viabilidad2 = listaViabilidadesProyecto.get(0)
							.getViabilidadTecnica();
				} else if (ciiu == 3) {
					viabilidad3 = listaViabilidadesProyecto.get(0)
							.getViabilidadTecnica();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 3;
	}

	public TipoPoblacion getTipoPoblacionSimple(Integer id) {
		if (tipoPoblacionSimple != null
				&& !tipoPoblacionSimple.getId().equals(id))
			tipoPoblacionSimple = null;
		return tipoPoblacionSimple == null ? tipoPoblacionSimple = new TipoPoblacion(
				id) : tipoPoblacionSimple;
	}

	public void handleFileUpload(final FileUploadEvent event) {

		uploadedFileGeo = event.getFile();

		coordenadasRcoaBean.handleFileUpload(event);
		coordenadasRcoaBean.procesarIntersecciones();

		coordenadasGeograficas = new ArrayList<SimuladorCoordenadasProyecto>();
		coordinatesWrappersGeo = new ArrayList<SimuladorCoordendasPoligonos>();
		capasIntersecciones = new HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
		listaCapasInterseccionPrincipal = new HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();

		for (CoordenadasProyecto b : coordenadasRcoaBean
				.getCoordenadasGeograficas()) {
			SimuladorCoordenadasProyecto coordenada = new SimuladorCoordenadasProyecto();
			coordenada.setAreaGeografica(b.getAreaGeografica());
			coordenada.setOrdenCoordenada(b.getOrdenCoordenada());
			coordenada.setX(b.getX());
			coordenada.setY(b.getY());
			coordenada.setTipoCoordenada(2);

			coordenadasGeograficas.add(coordenada);
		}

		for (CoordendasPoligonos c : coordenadasRcoaBean
				.getCoordinatesWrappersGeo()) {
			List<SimuladorCoordenadasProyecto> coordenadasGeograficasAux = new ArrayList<SimuladorCoordenadasProyecto>();
			for (CoordenadasProyecto b : c.getCoordenadas()) {
				SimuladorCoordenadasProyecto coordenada = new SimuladorCoordenadasProyecto();
				coordenada.setAreaGeografica(b.getAreaGeografica());
				coordenada.setOrdenCoordenada(b.getOrdenCoordenada());
				coordenada.setX(b.getX());
				coordenada.setY(b.getY());
				coordenada.setTipoCoordenada(2);

				coordenadasGeograficasAux.add(coordenada);
			}
			SimuladorCoordendasPoligonos coordinatesWrapper = new SimuladorCoordendasPoligonos(
					null, poligono);
			coordinatesWrapper.setCoordenadas(coordenadasGeograficasAux);
			coordinatesWrappersGeo.add(coordinatesWrapper);
		}

		ubicacionOficinaTecnica = coordenadasRcoaBean
				.getUbicacionOficinaTecnica();
		estadoZonaIntangibleAux = coordenadasRcoaBean
				.isEstadoZonaIntangibleAux();
		estadoFrontera = coordenadasRcoaBean.isEstadoFrontera();
		esZonaSnapEspecial = coordenadasRcoaBean.isEsZonaSnapEspecial();

		Iterator<Entry<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>> it = coordenadasRcoaBean
				.getCapasIntersecciones().entrySet().iterator();
		while (it.hasNext()) {
			Entry<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> item = it
					.next();

			SimuladorInterseccionProyectoLicenciaAmbiental capaInterseccion = new SimuladorInterseccionProyectoLicenciaAmbiental();
			List<DetalleInterseccionProyectoAmbiental> listaIntersecciones = new ArrayList<DetalleInterseccionProyectoAmbiental>();

			capaInterseccion.setCapa(item.getKey().getCapa());
			capaInterseccion.setProyectoLicenciaCoa(proyecto);
			capaInterseccion.setDescripcionCapa(item.getKey()
					.getDescripcionCapa());

			listaIntersecciones = item.getValue();

			capasIntersecciones.put(capaInterseccion, listaIntersecciones);
		}

		Iterator<Entry<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>> itI = coordenadasRcoaBean
				.getListaCapasInterseccionPrincipal().entrySet().iterator();
		while (itI.hasNext()) {
			Entry<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> item = itI
					.next();

			SimuladorInterseccionProyectoLicenciaAmbiental capaInterseccion = new SimuladorInterseccionProyectoLicenciaAmbiental();
			List<DetalleInterseccionProyectoAmbiental> listaIntersecciones = new ArrayList<DetalleInterseccionProyectoAmbiental>();

			capaInterseccion.setCapa(item.getKey().getCapa());
			capaInterseccion.setProyectoLicenciaCoa(proyecto);
			capaInterseccion.setDescripcionCapa(item.getKey()
					.getDescripcionCapa());

			listaIntersecciones = item.getValue();

			listaCapasInterseccionPrincipal.put(capaInterseccion,
					listaIntersecciones);
		}

		ubicacionesSeleccionadas = coordenadasRcoaBean
				.getUbicacionesSeleccionadas();
		superficieGeograficaHa = coordenadasRcoaBean
				.getSuperficieGeograficaHa();
		superficieGeograficaMetros = coordenadasRcoaBean
				.getSuperficieGeograficaMetros();
		mostrarDetalleInterseccion = coordenadasRcoaBean
				.getMostrarDetalleInterseccion();
		zonasInterccionDetalle = coordenadasRcoaBean
				.getZonasInterseccionDetalle();

		area = coordenadasRcoaBean.getArea();
		ubicacionPrincipal = coordenadasRcoaBean.getUbicacionPrincipal();

		parroquiaSeleccionadas = new HashMap<String, UbicacionesGeografica>();
		
		proyecto.setConcesionCamaronera(null);
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

	}

	@Getter
	@Setter
	private List<SimuladorCoordendasPoligonos> coordinatesWrappers = new ArrayList<SimuladorCoordendasPoligonos>();
	@Getter
	@Setter
	private List<SimuladorCoordendasPoligonos> coordinatesWrappersGeo = new ArrayList<SimuladorCoordendasPoligonos>();

	private HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> capasIntersecciones = new HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
	private HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> listaCapasInterseccionPrincipal = new HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();

	public void handleFileUploadImple(final FileUploadEvent event) {

		coordenadasRcoaBean.handleFileUploadImple(event);

		coordinatesWrappers = new ArrayList<SimuladorCoordendasPoligonos>();

		uploadedFileImpl = event.getFile();
		for (CoordendasPoligonos c : coordenadasRcoaBean
				.getCoordinatesWrappers()) {
			List<SimuladorCoordenadasProyecto> coordenadasGeograficasAux = new ArrayList<SimuladorCoordenadasProyecto>();
			for (CoordenadasProyecto b : c.getCoordenadas()) {
				SimuladorCoordenadasProyecto coordenada = new SimuladorCoordenadasProyecto();
				coordenada.setAreaGeografica(b.getAreaGeografica());
				coordenada.setOrdenCoordenada(b.getOrdenCoordenada());
				coordenada.setX(b.getX());
				coordenada.setY(b.getY());
				coordenada.setTipoCoordenada(2);

				coordenadasGeograficasAux.add(coordenada);
			}
			SimuladorCoordendasPoligonos coordinatesWrapper = new SimuladorCoordendasPoligonos(
					null, poligono);
			coordinatesWrapper.setCoordenadas(coordenadasGeograficasAux);
			coordinatesWrappers.add(coordinatesWrapper);
		}

		zonasInterccionDetalle = "";
		mostrarDetalleInterseccion = false;

		valorCapacidadSocial = coordenadasRcoaBean.getValorCapacidadSocial();
		valorCapacidadBiofisica = coordenadasRcoaBean
				.getValorCapacidadBiofisica();

		proyecto.setSuperficie(new BigDecimal(coordenadasRcoaBean
				.getSuperficie()).setScale(5, BigDecimal.ROUND_HALF_UP));
		superficieMetrosCuadrados = new BigDecimal(
				coordenadasRcoaBean.getSuperficieMetros()).setScale(5,
				BigDecimal.ROUND_HALF_UP);

	}

	@Getter
	@Setter
	private boolean esSustanciaOtros;

	@Getter
	@Setter
	private boolean esSustanciaOtrosTransporta;

	public void sustanciaSelecionada(SustanciaQuimicaPeligrosa sustancia) {
		esSustanciaOtros = false;
		if (!sustanciaQuimicaSeleccionada.contains(sustancia)) {
			if (!sustancia.getDescripcion().equals("Otros")) {
				sustanciaQuimicaSeleccionada.add(sustancia);
			} else {
				esSustanciaOtros = true;
			}
		} else
			JsfUtil.addMessageError("La sustancia ya fue ingresada.");

		controlSustancia();
	}

	public void eliminarSustancia(SustanciaQuimicaPeligrosa sustancia) {
		sustanciaQuimicaSeleccionada.remove(sustancia);
		esMercurio = false;
		fabricaSustancia = false;

		controlSustancia();
	}

	public void sustanciaSelecionadaTransporta(
			SustanciaQuimicaPeligrosa sustancia) {
		esSustanciaOtrosTransporta = false;
		if (!sustanciaQuimicaSeleccionadaTransporta.contains(sustancia)) {
			if (!sustancia.getDescripcion().equals("Otros")) {
				sustanciaQuimicaSeleccionadaTransporta.add(sustancia);
			} else {
				esSustanciaOtrosTransporta = true;
			}
		} else
			JsfUtil.addMessageError("La sustancia ya fue ingresada.");

		controlSustancia();
	}

	public void eliminarSustanciaTransporta(SustanciaQuimicaPeligrosa sustancia) {
		sustanciaQuimicaSeleccionadaTransporta.remove(sustancia);
		controlSustancia();
	}

	WolframVO wf1 = new WolframVO();
	WolframVO wf2 = new WolframVO();
	WolframVO wf3 = new WolframVO();
	WolframVO wfCalculo = new WolframVO();

	@Getter
	@Setter
	private boolean subOpciones1, subOpciones2, subOpciones3,
			esEstacionElectrica = false, mostrarSubBloque = false;
	@Getter
	@Setter
	private Integer tipoOpcion1, tipoOpcion2, tipoOpcion3;
	@Getter
	@Setter
	private List<SubActividades> listaSubActividades1 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividades2 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividades3 = new ArrayList<SubActividades>(),
			listaSubactividadesProyecto = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private SubActividades subActividad1 = new SubActividades(),
			subActividTem = new SubActividades();
	@Getter
	@Setter
	private SubActividades subActividad2 = new SubActividades();
	@Getter
	@Setter
	private SubActividades subActividad3 = new SubActividades();
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
	private SubActividades bancoEstado1 = new SubActividades();
	@Getter
	@Setter
	private SubActividades bancoEstado2 = new SubActividades();
	@Getter
	@Setter
	private SubActividades bancoEstado3 = new SubActividades();

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

	public void ciiu1(CatalogoCIUU catalogo, Boolean validarMayorImportancia) {
		
		bloquearCiiu1 = bloqueoGad(catalogo);
		
		if (validarActividadesRepetidas(1, catalogo.getCodigo(),
				catalogo.getNombre()))
			return;

		if (ciiuPrincipal != null && ciiuPrincipal.getId() != null
				&& !ciiuPrincipal.getId().equals(catalogo.getId()))
			limpiarCamposActividad(1);

		listaSubActividades1 = subActividadesFacade.listaXactividad(catalogo);
		if (validarMayorImportancia) {
			limpiarCamposRelacionadosCiiu(1);
		}

		subOpciones1 = false;
		subActividad1 = new SubActividades();
		bancoEstado1 = new SubActividades();
		parent1 = new SubActividades();
		catalogoCamaronera_ciuu1 = false;
		
		if (catalogo.getTipoPregunta() != null) {
			getConsideracionesPorActividad(catalogo, 1);
		} else {
		esEstacionElectrica = false;
		if (catalogo.getCodigo().equals("D3510.02")
				|| catalogo.getCodigo().equals("D3510.02.01")) {
			esEstacionElectrica = true;
			tipoOpcion1 = 11;
			subOpciones1 = true;
			// subActividad1=listaSubActividades1.get(0);
			consideracionesBloques(catalogo);
		}
		
		if (listaSubActividades1.size() > 0 && esEstacionElectrica == false) {
			subOpciones1 = true;
			if (subActividadesCiiuBean
					.esActividadRecupercionMateriales(catalogo)) {
				tipoOpcion1 = 6;
				listaSubActividades1 = subActividadesFacade
						.actividadPrincipalConHijos(catalogo);
			} else if (catalogo.getLibreAprovechamiento()) {
				tipoOpcion1 = 5;
				parent1 = listaSubActividades1.get(0);
				listaSubActividades1 = new ArrayList<SubActividades>();
			} else if (listaSubActividades1.get(0).getTipoPregunta() != null
					&& (listaSubActividades1.get(0).getTipoPregunta()
							.intValue() == 7 || listaSubActividades1.get(0)
							.getTipoPregunta().intValue() == 8)) {
				tipoOpcion1 = listaSubActividades1.get(0).getTipoPregunta()
						.intValue();
				subActividad1 = listaSubActividades1.get(0);
				parent1 = listaSubActividades1.get(0);
			} else if (listaSubActividades1.get(0).getTipoPregunta() != null
					&& (listaSubActividades1.get(0).getTipoPregunta()
							.intValue() == 9)) {
				tipoOpcion1 = 9;
			} else if (listaSubActividades1.get(0).getTipoPregunta() != null
					&& (listaSubActividades1.get(0).getTipoPregunta()
							.intValue() == 12)) {
				tipoOpcion1 = 12;

				List<SubActividades> listaSubActividadesAux = new ArrayList<>();
				listaSubActividadesAux.addAll(listaSubActividades1);
				for (SubActividades b : listaSubActividadesAux) {
					if (b.getEsMultiple() != null && !b.getEsMultiple()) {
						bancoEstado1 = b;

						listaSubActividades1.remove(b);
					}
				}
			} else if (subActividadesCiiuBean
					.esActividadOperacionRellenosSanitarios(catalogo)) {
				tipoOpcion1 = 21;
				bancoEstado1 = listaSubActividades1.get(0);
				subActividad1 = listaSubActividades1.get(1);
				parent1 = listaSubActividades1.get(1);
			} else {
				listaSubActividades1 = subActividadesFacade
						.listaTodoXactividad(catalogo);
				if (listaSubActividades1.size() > 0
						&& listaSubActividades1.size() == 1) {
					tipoOpcion1 = 1;
					subActividad1 = listaSubActividades1.get(0);
					validarIngresoViabilidaTecnica(catalogo, 1);
				} else if (listaSubActividades1.size() > 0) {
					boolean doblePregunta = false;

					if (subActividadesCiiuBean
							.esActividadRecupercionMateriales(catalogo)) {
						tipoOpcion1 = 6;
						listaSubActividades1 = subActividadesFacade
								.actividadPrincipalConHijos(catalogo);
					} else if (catalogo.getLibreAprovechamiento()) {
						tipoOpcion1 = 5;
						parent1 = listaSubActividades1.get(0);
						listaSubActividades1 = new ArrayList<SubActividades>();
					} else {
						String actividadesDoblePregunta = Constantes
								.getActividadesDoblePregunta();
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta
								.split(",");
						for (String actividad : actividadesDoblePreguntaArray) {
							for (SubActividades subA : listaSubActividades1) {
								if (subA.getCatalogoCIUU().getCodigo()
										.equals(actividad)) {
									tipoOpcion1 = 3;
									doblePregunta = true;
									if (subA.getFinanciadoBancoEstado() != null
											&& subA.getFinanciadoBancoEstado())
										bancoEstado1 = subA;
									else {
										if (subA.getSubActividades() != null) {
											listaSubActividades1 = new ArrayList<SubActividades>();
											parent1 = subActividadesFacade
													.actividadParent(subA
															.getSubActividades()
															.getId());
											listaSubActividades1 = subActividadesFacade
													.actividadHijos(parent1
															.getId(), subA
															.getCatalogoCIUU());
										}
									}
								}
							}
						}
						if (!doblePregunta) {
							tipoOpcion1 = 2;
						}
						boolean alcantarillado = false;
						String actividadAlcantarillado = Constantes
								.getActividadesZonaRural();
						String[] actividadesAlcantarilladoArray = actividadAlcantarillado
								.split(",");
						for (String actividades : actividadesAlcantarilladoArray) {
							if (catalogo.getCodigo().equals(actividades)) {
								alcantarillado = true;
								subActivadesAlcantarillado1 = false;
								for (SubActividades subA : listaSubActividades1) {
									if (subA.getSubActividades() != null) {
										listaSubActividades1 = new ArrayList<SubActividades>();
										parent1 = subActividadesFacade
												.actividadParent(subA
														.getSubActividades()
														.getId());
										listaSubActividades1 = subActividadesFacade
												.actividadHijos(
														parent1.getId(),
														subA.getCatalogoCIUU());
										break;
									}
								}
								break;
							}
						}
						if (alcantarillado) {
							tipoOpcion1 = 4;
						}

						boolean galapagos = false;
						String actividadGalapagos = Constantes
								.getActividadesGalapagos();
						String[] actividadGalapagosArray = actividadGalapagos
								.split(",");
						for (String actividades : actividadGalapagosArray) {
							if (catalogo.getCodigo().equals(actividades)) {
								galapagos = true;
								subActivadesGalapagos1 = false;
								for (SubActividades subA : listaSubActividades1) {
									if (subA.getSubActividades() != null) {
										listaSubActividades1 = new ArrayList<SubActividades>();
										parent1 = subActividadesFacade
												.actividadParent(subA
														.getSubActividades()
														.getId());
										listaSubActividades1 = subActividadesFacade
												.actividadHijos(
														parent1.getId(),
														subA.getCatalogoCIUU());
										subActivadesGalapagos1 = true;
										break;
									}
								}
								break;
							}
						}
						if (galapagos) {
							tipoOpcion1 = 45;
						}
					}
				}
			}
		}
		}

		ciiuPrincipal = catalogo;
		wf1.setCatalago(ciiuPrincipal);
		wf1.setNivel5(ciiuPrincipal.getCodigo().substring(0, 7));
		wf1.setNivel3(ciiuPrincipal.getCodigo().substring(0, 4));
		wf1.setImportancia(catalogoCIUUFacade.catalogoCiuu(
				ciiuPrincipal.getCodigo().substring(0, 7))
				.getImportanciaRelativa());
		wf1.setPuesto(1);

		if (validarMayorImportancia)
			catalogoMayorImportancia();// se habilita porque es necesario
										// recuperar el sector y cual es la
										// actividad principal

		esCiiu1HidrocarburoMineriaElectrico = false;
		if (ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null) {
			String nombreSector = ciiuPrincipal.getTipoSector().getNombre()
					.toUpperCase();
			if (nombreSector.equals("MINERÍA")
					|| nombreSector.equals("HIDROCARBUROS")) {// ||
																// nombreSector.equals("ELÉCTRICO")
				esCiiu1HidrocarburoMineriaElectrico = true;
				if (estadoZonaIntangibleAux)
					estadoZonaIntangible = true;
			}
		}
		validarSectorActividades();
		
		/**
		 * solo para minería
		 */

		if (ciiuComplementaria1 == null && ciiuComplementaria2 == null) {
			mostrarPoseeContrato = false;
			tieneContratoMineria = null;
			esContratoOperacion = null;
			concesionMinera = null;
		} else if (ciiuComplementaria1 != null
				&& ciiuComplementaria1.getId() == null
				&& ciiuComplementaria2 == null) {
			mostrarPoseeContrato = false;
			tieneContratoMineria = null;
			esContratoOperacion = null;
			concesionMinera = null;
		} else if (ciiuComplementaria1 != null
				&& ciiuComplementaria1.getId() == null
				&& ciiuComplementaria2 != null
				&& ciiuComplementaria2.getId() == null) {
			mostrarPoseeContrato = false;
			tieneContratoMineria = null;
			esContratoOperacion = null;
			concesionMinera = null;
		}

		if (ciiuComplementaria1 != null
				&& ciiuComplementaria1.getTipoSector() != null) {
			String nombreSector = ciiuComplementaria1.getTipoSector()
					.getNombre().toUpperCase();
			if (!nombreSector.equals("MINERÍA")) {
				if (ciiuComplementaria2 != null
						&& ciiuComplementaria2.getTipoSector() != null) {
					String nombreSector2 = ciiuComplementaria2.getTipoSector()
							.getNombre().toUpperCase();
					if (!nombreSector2.equals("MINERÍA")) {
						mostrarPoseeContrato = false;
						tieneContratoMineria = null;
						esContratoOperacion = null;
						concesionMinera = null;
					} else {
						if (!subOpciones3) {
							mostrarPoseeContrato = false;
							tieneContratoMineria = null;
							esContratoOperacion = null;
							concesionMinera = null;
						}
					}
				} else if (ciiuComplementaria2 == null) {
					mostrarPoseeContrato = false;
					tieneContratoMineria = null;
					esContratoOperacion = null;
					concesionMinera = null;
				}
			} else {
				if (!subOpciones2) {
					mostrarPoseeContrato = false;
					tieneContratoMineria = null;
					esContratoOperacion = null;
					concesionMinera = null;
				}
			}
		}

		if (ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null) {
			String nombreSector = ciiuPrincipal.getTipoSector().getNombre()
					.toUpperCase();
			if (nombreSector.equals("MINERÍA")) {
				if (subOpciones1) {
					esContratoOperacion = false;
					noEsMineriaArtesanal = false;
				} else {
					noEsMineriaArtesanal = true;
				}
			}
		}

		RequestContext.getCurrentInstance().update(
				"frmPreliminar:idDocAutorizacionesDectoriales");
		RequestContext.getCurrentInstance().update("frmPreliminar:pnlSector");
		RequestContext.getCurrentInstance().update(
				"frmPreliminar:concesionesMinerasGeneralContainer");
	}

	public void ciiu2(CatalogoCIUU catalogo, Boolean validarMayorImportancia) {
		
		bloquearCiiu2 = bloqueoGad(catalogo);
		
		if (validarActividadesRepetidas(2, catalogo.getCodigo(),
				catalogo.getNombre()))
			return;

		if (ciiuComplementaria1 != null && ciiuComplementaria1.getId() != null
				&& !ciiuComplementaria1.getId().equals(catalogo.getId()))
			limpiarCamposActividad(2);

		listaSubActividades2 = subActividadesFacade.listaXactividad(catalogo);
		if (validarMayorImportancia) {
			limpiarCamposRelacionadosCiiu(2);
		}

		subOpciones2 = false;
		subActividad2 = new SubActividades();
		bancoEstado2 = new SubActividades();
		parent2 = new SubActividades();
		catalogoCamaronera_ciuu2 = false;

		if(catalogo.getTipoPregunta() != null) {
			getConsideracionesPorActividad(catalogo, 2);
		} else {
		esEstacionElectrica = false;
		if (catalogo.getCodigo().equals("D3510.02")
				|| catalogo.getCodigo().equals("D3510.02.01")) {
			esEstacionElectrica = true;
			tipoOpcion2 = 11;
			subOpciones2 = true;
			consideracionesBloques(catalogo);
		}
		if (listaSubActividades2.size() > 0 && !esEstacionElectrica) {
			subOpciones2 = true;
			if (subActividadesCiiuBean
					.esActividadRecupercionMateriales(catalogo)) {
				tipoOpcion2 = 6;
				listaSubActividades2 = subActividadesFacade
						.actividadPrincipalConHijos(catalogo);
			} else if (catalogo.getLibreAprovechamiento()) {
				tipoOpcion2 = 5;
				parent2 = listaSubActividades2.get(0);
				listaSubActividades2 = new ArrayList<SubActividades>();
			} else if (subActividadesCiiuBean.esActividadGalapagos(catalogo)) {
				tipoOpcion2 = 5;
				parent2 = listaSubActividades2.get(0);
				listaSubActividades2 = new ArrayList<SubActividades>();
			} else if (listaSubActividades2.get(0).getTipoPregunta() != null
					&& (listaSubActividades2.get(0).getTipoPregunta()
							.intValue() == 7 || listaSubActividades2.get(0)
							.getTipoPregunta().intValue() == 8)) {
				tipoOpcion2 = listaSubActividades2.get(0).getTipoPregunta()
						.intValue();
				subActividad2 = listaSubActividades2.get(0);
				parent2 = listaSubActividades2.get(0);
			} else if (listaSubActividades2.get(0).getTipoPregunta() != null
					&& (listaSubActividades2.get(0).getTipoPregunta()
							.intValue() == 9)) {
				tipoOpcion2 = 9;
			} else if (listaSubActividades2.get(0).getTipoPregunta() != null
					&& (listaSubActividades2.get(0).getTipoPregunta()
							.intValue() == 12)) {
				tipoOpcion2 = 12;

				List<SubActividades> listaSubActividadesAux = new ArrayList<>();
				listaSubActividadesAux.addAll(listaSubActividades2);
				for (SubActividades b : listaSubActividadesAux) {
					if (b.getEsMultiple() != null && !b.getEsMultiple()) {
						bancoEstado2 = b;

						listaSubActividades2.remove(b);
					}
				}
			} else if (subActividadesCiiuBean
					.esActividadOperacionRellenosSanitarios(catalogo)) {
				tipoOpcion2 = 21;
				bancoEstado2 = listaSubActividades2.get(0);
				subActividad2 = listaSubActividades2.get(1);
				parent2 = listaSubActividades2.get(1);
			} else {
				listaSubActividades2 = subActividadesFacade
						.listaTodoXactividad(catalogo);
				if (listaSubActividades2.size() > 0
						&& listaSubActividades2.size() == 1) {
					tipoOpcion2 = 1;
					subActividad2 = listaSubActividades2.get(0);
					
					validarIngresoViabilidaTecnica(catalogo, 2);
				} else if (listaSubActividades2.size() > 0) {
					boolean doblePregunta = false;

					if (subActividadesCiiuBean
							.esActividadRecupercionMateriales(catalogo)) {
						tipoOpcion2 = 6;
						listaSubActividades2 = subActividadesFacade
								.actividadPrincipalConHijos(catalogo);
					} else if (catalogo.getLibreAprovechamiento()) {
						tipoOpcion2 = 5;
						parent2 = listaSubActividades2.get(0);
						listaSubActividades2 = new ArrayList<SubActividades>();
					} else if (subActividadesCiiuBean
							.esActividadGalapagos(catalogo)) {
						tipoOpcion2 = 5;
						parent2 = listaSubActividades2.get(0);
						listaSubActividades2 = new ArrayList<SubActividades>();
					} else {
						String actividadesDoblePregunta = Constantes
								.getActividadesDoblePregunta();
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta
								.split(",");
						for (String actividad : actividadesDoblePreguntaArray) {
							for (SubActividades subA : listaSubActividades2) {
								if (subA.getCatalogoCIUU().getCodigo()
										.equals(actividad)) {
									tipoOpcion2 = 3;
									doblePregunta = true;
									if (subA.getFinanciadoBancoEstado() != null
											&& subA.getFinanciadoBancoEstado())
										bancoEstado2 = subA;
									else {
										if (subA.getSubActividades() != null) {
											listaSubActividades2 = new ArrayList<SubActividades>();
											parent2 = subActividadesFacade
													.actividadParent(subA
															.getSubActividades()
															.getId());
											listaSubActividades2 = subActividadesFacade
													.actividadHijos(parent2
															.getId(), subA
															.getCatalogoCIUU());
										}
									}
								}
							}
						}
						if (!doblePregunta) {
							tipoOpcion2 = 2;
						}
						boolean alcantarillado = false;
						String actividadAlcantarillado = Constantes
								.getActividadesZonaRural();
						String[] actividadesAlcantarilladoArray = actividadAlcantarillado
								.split(",");
						for (String actividades : actividadesAlcantarilladoArray) {
							if (catalogo.getCodigo().equals(actividades)) {
								alcantarillado = true;
								subActivadesAlcantarillado2 = false;
								for (SubActividades subA : listaSubActividades2) {
									if (subA.getSubActividades() != null) {
										listaSubActividades2 = new ArrayList<SubActividades>();
										parent2 = subActividadesFacade
												.actividadParent(subA
														.getSubActividades()
														.getId());
										listaSubActividades2 = subActividadesFacade
												.actividadHijos(
														parent2.getId(),
														subA.getCatalogoCIUU());
										break;
									}
								}
								break;
							}
						}
						if (alcantarillado) {
							tipoOpcion2 = 4;
						}
						boolean galapagos = false;
						String actividadGalapagos = Constantes
								.getActividadesGalapagos();
						String[] actividadGalapagosArray = actividadGalapagos
								.split(",");
						for (String actividades : actividadGalapagosArray) {
							if (catalogo.getCodigo().equals(actividades)) {
								galapagos = true;
								subActivadesGalapagos2 = false;
								for (SubActividades subA : listaSubActividades2) {
									if (subA.getSubActividades() != null) {
										listaSubActividades2 = new ArrayList<SubActividades>();
										parent2 = subActividadesFacade
												.actividadParent(subA
														.getSubActividades()
														.getId());
										listaSubActividades2 = subActividadesFacade
												.actividadHijos(
														parent2.getId(),
														subA.getCatalogoCIUU());
										break;
									}
								}
								break;
							}
						}
						if (galapagos) {
							tipoOpcion2 = 45;
						}
					}
				}
			}
		}
		}

		ciiuComplementaria1 = catalogo;
		wf2.setCatalago(ciiuComplementaria1);
		wf2.setNivel5(ciiuComplementaria1.getCodigo().substring(0, 7));
		wf2.setNivel3(ciiuComplementaria1.getCodigo().substring(0, 4));
		wf2.setImportancia(catalogoCIUUFacade.catalogoCiuu(
				ciiuComplementaria1.getCodigo().substring(0, 7))
				.getImportanciaRelativa());
		wf2.setPuesto(2);

		if (validarMayorImportancia)
			catalogoMayorImportancia();// se habilita porque es necesario
										// recuperar el sector y cual es la
										// actividad principal

		esCiiu2HidrocarburoMineriaElectrico = false;
		if (ciiuComplementaria1 != null
				&& ciiuComplementaria1.getTipoSector() != null) {
			String nombreSector = ciiuComplementaria1.getTipoSector()
					.getNombre().toUpperCase();
			if (nombreSector.equals("MINERÍA")
					|| nombreSector.equals("HIDROCARBUROS")) {// ||
																// nombreSector.equals("ELÉCTRICO")
				esCiiu2HidrocarburoMineriaElectrico = true;
				if (estadoZonaIntangibleAux)
					estadoZonaIntangible = true;
			}
		}
		validarSectorActividades();
		
		/**
		 * solo para minería
		 */
		if (ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null) {
			String nombreSector = ciiuPrincipal.getTipoSector().getNombre()
					.toUpperCase();
			if (!nombreSector.equals("MINERÍA")) {
				if (ciiuComplementaria2 != null
						&& ciiuComplementaria2.getTipoSector() != null) {
					String nombreSector2 = ciiuComplementaria2.getTipoSector()
							.getNombre().toUpperCase();
					if (!nombreSector2.equals("MINERÍA")) {
						mostrarPoseeContrato = false;
						tieneContratoMineria = null;
						esContratoOperacion = null;
						concesionMinera = null;
					} else {
						if (!subOpciones3) {
							mostrarPoseeContrato = false;
							tieneContratoMineria = null;
							esContratoOperacion = null;
							concesionMinera = null;
						}
					}
				} else if (ciiuComplementaria2 == null) {
					mostrarPoseeContrato = false;
					tieneContratoMineria = null;
					esContratoOperacion = null;
					concesionMinera = null;
				}
			} else {
				if (!subOpciones1) {
					mostrarPoseeContrato = false;
					tieneContratoMineria = null;
					esContratoOperacion = null;
					concesionMinera = null;
				}
			}
		}

		if (ciiuComplementaria1 != null
				&& ciiuComplementaria1.getTipoSector() != null) {
			String nombreSector = ciiuComplementaria1.getTipoSector()
					.getNombre().toUpperCase();
			if (nombreSector.equals("MINERÍA")) {
				if (subOpciones1) {
					esContratoOperacion = false;
					noEsMineriaArtesanal = false;
				} else {
					noEsMineriaArtesanal = true;
				}
			}
		}

		RequestContext.getCurrentInstance().update(
				"frmPreliminar:concesionesMinerasGeneralContainer");
	}

	public void ciiu3(CatalogoCIUU catalogo, Boolean validarMayorImportancia) {
		
		// actividades ciiu para bloqueo
		bloquearCiiu3 = bloqueoGad(catalogo);

		if (validarActividadesRepetidas(3, catalogo.getCodigo(),
				catalogo.getNombre()))
			return;

		if (ciiuComplementaria2 != null && ciiuComplementaria2.getId() != null
				&& !ciiuComplementaria2.getId().equals(catalogo.getId()))
			limpiarCamposActividad(3);

		listaSubActividades3 = subActividadesFacade.listaXactividad(catalogo);
		if (validarMayorImportancia) {
			limpiarCamposRelacionadosCiiu(3);
		}

		subOpciones3 = false;
		subActividad3 = new SubActividades();
		bancoEstado3 = new SubActividades();
		parent3 = new SubActividades();
		catalogoCamaronera_ciuu3 = false;
		
		if(catalogo.getTipoPregunta() != null) {
			getConsideracionesPorActividad(catalogo, 3);
		} else {
		esEstacionElectrica = false;
		if (catalogo.getCodigo().equals("D3510.02")
				|| catalogo.getCodigo().equals("D3510.02.01")) {
			esEstacionElectrica = true;
			tipoOpcion3 = 11;
			subOpciones3 = true;
			// subActividad1=listaSubActividades1.get(0);
			consideracionesBloques(catalogo);
		}
		if (listaSubActividades3.size() > 0 && !esEstacionElectrica) {
			subOpciones3 = true;
			if (subActividadesCiiuBean
					.esActividadRecupercionMateriales(catalogo)) {
				tipoOpcion3 = 6;
				listaSubActividades3 = subActividadesFacade
						.actividadPrincipalConHijos(catalogo);
			} else if (catalogo.getLibreAprovechamiento()) {
				tipoOpcion3 = 5;
				parent3 = listaSubActividades3.get(0);
				listaSubActividades3 = new ArrayList<SubActividades>();
			} else if (subActividadesCiiuBean.esActividadGalapagos(catalogo)) {
				tipoOpcion3 = 5;
				parent3 = listaSubActividades3.get(0);
				listaSubActividades3 = new ArrayList<SubActividades>();
			} else if (listaSubActividades3.get(0).getTipoPregunta() != null
					&& (listaSubActividades3.get(0).getTipoPregunta()
							.intValue() == 7 || listaSubActividades3.get(0)
							.getTipoPregunta().intValue() == 8)) {
				tipoOpcion3 = listaSubActividades3.get(0).getTipoPregunta()
						.intValue();
				subActividad3 = listaSubActividades3.get(0);
				parent3 = listaSubActividades3.get(0);
			} else if (listaSubActividades3.get(0).getTipoPregunta() != null
					&& (listaSubActividades3.get(0).getTipoPregunta()
							.intValue() == 9)) {
				tipoOpcion3 = 9;
			} else if (listaSubActividades3.get(0).getTipoPregunta() != null
					&& (listaSubActividades3.get(0).getTipoPregunta()
							.intValue() == 12)) {
				tipoOpcion3 = 12;

				List<SubActividades> listaSubActividadesAux = new ArrayList<>();
				listaSubActividadesAux.addAll(listaSubActividades3);
				for (SubActividades b : listaSubActividadesAux) {
					if (b.getEsMultiple() != null && !b.getEsMultiple()) {
						bancoEstado3 = b;

						listaSubActividades3.remove(b);
					}
				}
			} else if (subActividadesCiiuBean
					.esActividadOperacionRellenosSanitarios(catalogo)) {
				tipoOpcion3 = 21;
				bancoEstado3 = listaSubActividades3.get(0);
				subActividad3 = listaSubActividades3.get(1);
				parent3 = listaSubActividades3.get(1);
			} else {
				listaSubActividades3 = subActividadesFacade
						.listaTodoXactividad(catalogo);
				if (listaSubActividades3.size() > 0
						&& listaSubActividades3.size() == 1) {
					tipoOpcion3 = 1;
					subActividad3 = listaSubActividades3.get(0);
					
					validarIngresoViabilidaTecnica(catalogo, 3);
				} else if (listaSubActividades3.size() > 0) {
					boolean doblePregunta = false;

					if (subActividadesCiiuBean
							.esActividadRecupercionMateriales(catalogo)) {
						tipoOpcion3 = 6;
						listaSubActividades3 = subActividadesFacade
								.actividadPrincipalConHijos(catalogo);
					} else if (catalogo.getLibreAprovechamiento()) {
						tipoOpcion3 = 5;
						parent3 = listaSubActividades3.get(0);
						listaSubActividades3 = new ArrayList<SubActividades>();
					} else if (subActividadesCiiuBean
							.esActividadGalapagos(catalogo)) {
						tipoOpcion3 = 5;
						parent3 = listaSubActividades3.get(0);
						listaSubActividades3 = new ArrayList<SubActividades>();
					} else {
						String actividadesDoblePregunta = Constantes
								.getActividadesDoblePregunta();
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta
								.split(",");
						for (String actividad : actividadesDoblePreguntaArray) {
							for (SubActividades subA : listaSubActividades3) {
								if (subA.getCatalogoCIUU().getCodigo()
										.equals(actividad)) {
									tipoOpcion3 = 3;
									doblePregunta = true;
									if (subA.getFinanciadoBancoEstado() != null
											&& subA.getFinanciadoBancoEstado())
										bancoEstado3 = subA;
									else {
										if (subA.getSubActividades() != null) {
											listaSubActividades3 = new ArrayList<SubActividades>();
											parent3 = subActividadesFacade
													.actividadParent(subA
															.getSubActividades()
															.getId());
											listaSubActividades3 = subActividadesFacade
													.actividadHijos(parent3
															.getId(), subA
															.getCatalogoCIUU());
										}
									}
								}
							}
						}
						if (!doblePregunta) {
							tipoOpcion3 = 2;
						}
						boolean alcantarillado = false;
						String actividadAlcantarillado = Constantes
								.getActividadesZonaRural();
						String[] actividadesAlcantarilladoArray = actividadAlcantarillado
								.split(",");
						for (String actividades : actividadesAlcantarilladoArray) {
							if (catalogo.getCodigo().equals(actividades)) {
								alcantarillado = true;
								subActivadesAlcantarillado3 = false;
								for (SubActividades subA : listaSubActividades3) {
									if (subA.getSubActividades() != null) {
										listaSubActividades3 = new ArrayList<SubActividades>();
										parent3 = subActividadesFacade
												.actividadParent(subA
														.getSubActividades()
														.getId());
										listaSubActividades3 = subActividadesFacade
												.actividadHijos(
														parent3.getId(),
														subA.getCatalogoCIUU());
										break;
									}
								}
								break;
							}
						}
						if (alcantarillado) {
							tipoOpcion3 = 4;
						}
						boolean galapagos = false;
						String actividadGalapagos = Constantes
								.getActividadesGalapagos();
						String[] actividadGalapagosArray = actividadGalapagos
								.split(",");
						for (String actividades : actividadGalapagosArray) {
							if (catalogo.getCodigo().equals(actividades)) {
								galapagos = true;
								subActivadesGalapagos3 = false;
								for (SubActividades subA : listaSubActividades3) {
									if (subA.getSubActividades() != null) {
										listaSubActividades3 = new ArrayList<SubActividades>();
										parent1 = subActividadesFacade
												.actividadParent(subA
														.getSubActividades()
														.getId());
										listaSubActividades3 = subActividadesFacade
												.actividadHijos(
														parent3.getId(),
														subA.getCatalogoCIUU());
										break;
									}
								}
								break;
							}
						}
						if (galapagos) {
							tipoOpcion3 = 45;
						}
					}
				}
			}
		}
		}

		ciiuComplementaria2 = catalogo;
		wf3.setCatalago(ciiuComplementaria2);
		wf3.setNivel5(ciiuComplementaria2.getCodigo().substring(0, 7));
		wf3.setNivel3(ciiuComplementaria2.getCodigo().substring(0, 4));
		wf3.setImportancia(catalogoCIUUFacade.catalogoCiuu(
				ciiuComplementaria2.getCodigo().substring(0, 7))
				.getImportanciaRelativa());
		wf3.setPuesto(3);

		if (validarMayorImportancia)
			catalogoMayorImportancia();// se habilita porque es necesario
										// recuperar el sector y cual es la
										// actividad principal

		esCiiu3HidrocarburoMineriaElectrico = false;
		if (ciiuComplementaria2 != null
				&& ciiuComplementaria2.getTipoSector() != null) {
			String nombreSector = ciiuComplementaria2.getTipoSector()
					.getNombre().toUpperCase();
			if (nombreSector.equals("MINERÍA")
					|| nombreSector.equals("HIDROCARBUROS")) {// ||
																// nombreSector.equals("ELÉCTRICO")
				esCiiu3HidrocarburoMineriaElectrico = true;
				if (estadoZonaIntangibleAux)
					estadoZonaIntangible = true;
			}
		}
		validarSectorActividades();
		
		/**
		 * solo para minería
		 */
		if (ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null) {
			String nombreSector = ciiuPrincipal.getTipoSector().getNombre()
					.toUpperCase();
			if (!nombreSector.equals("MINERÍA")) {
				if (ciiuComplementaria1 != null
						&& ciiuComplementaria1.getTipoSector() != null) {
					String nombreSector2 = ciiuComplementaria1.getTipoSector()
							.getNombre().toUpperCase();
					if (!nombreSector2.equals("MINERÍA")) {
						mostrarPoseeContrato = false;
						tieneContratoMineria = null;
						esContratoOperacion = null;
						concesionMinera = null;
					} else {
						if (!subOpciones2) {
							mostrarPoseeContrato = false;
							tieneContratoMineria = null;
							esContratoOperacion = null;
							concesionMinera = null;
						}
					}
				}
			} else {
				if (!subOpciones1) {
					mostrarPoseeContrato = false;
					tieneContratoMineria = null;
					esContratoOperacion = null;
					concesionMinera = null;
				}
			}
		}

		if (ciiuComplementaria2 != null
				&& ciiuComplementaria2.getTipoSector() != null) {
			String nombreSector = ciiuComplementaria2.getTipoSector()
					.getNombre().toUpperCase();
			if (nombreSector.equals("MINERÍA")) {
				if (subOpciones1) {
					esContratoOperacion = false;
					noEsMineriaArtesanal = false;
				} else {
					noEsMineriaArtesanal = true;
				}
			}
		}

		RequestContext.getCurrentInstance().update(
				"frmPreliminar:concesionesMinerasGeneralContainer");
	}

	public Boolean validarActividadesRepetidas(Integer seleccion,
			String codigoSeleccion, String nombreSeleccion) {
		List<String> codigosSeleccionados = new ArrayList<>();
		List<String> nombresSeleccionados = new ArrayList<>();

		if (seleccion.equals(1)) {
			if (ciiuComplementaria1 != null
					&& ciiuComplementaria1.getId() != null) {
				codigosSeleccionados.add(ciiuComplementaria1.getCodigo());
				nombresSeleccionados.add(ciiuComplementaria1.getNombre());
			}
			if (ciiuComplementaria2 != null
					&& ciiuComplementaria2.getId() != null) {
				codigosSeleccionados.add(ciiuComplementaria2.getCodigo());
				nombresSeleccionados.add(ciiuComplementaria2.getNombre());
			}
		}

		if (seleccion.equals(2)) {
			if (ciiuPrincipal != null && ciiuPrincipal.getId() != null) {
				codigosSeleccionados.add(ciiuPrincipal.getCodigo());
				nombresSeleccionados.add(ciiuPrincipal.getNombre());
			}
			if (ciiuComplementaria2 != null
					&& ciiuComplementaria2.getId() != null) {
				codigosSeleccionados.add(ciiuComplementaria2.getCodigo());
				nombresSeleccionados.add(ciiuComplementaria2.getNombre());
			}
		}

		if (seleccion.equals(3)) {
			if (ciiuPrincipal != null && ciiuPrincipal.getId() != null) {
				codigosSeleccionados.add(ciiuPrincipal.getCodigo());
				nombresSeleccionados.add(ciiuPrincipal.getNombre());
			}
			if (ciiuComplementaria1 != null
					&& ciiuComplementaria1.getId() != null) {
				codigosSeleccionados.add(ciiuComplementaria1.getCodigo());
				nombresSeleccionados.add(ciiuComplementaria1.getNombre());
			}
		}

		for (String codeActividad : codigosSeleccionados) {
			if (codeActividad.equals(codigoSeleccion)) {
				JsfUtil.addMessageError("La actividad ya está seleccionada.");
				return true;
			}
		}

		for (String nameActividad : nombresSeleccionados) {
			if (nameActividad.equals(nombreSeleccion)) {
				JsfUtil.addMessageError("La actividad ya está seleccionada.");
				return true;
			}
		}

		return false;
	}

	public void catalogoMayorImportancia() {
		try {
			msgError = new ArrayList<String>();
			List<CatalogoImportanciaVO> importancias = new ArrayList<CatalogoImportanciaVO>();
			CatalogoImportanciaVO importCatalogo = new CatalogoImportanciaVO();
			Integer tipoPermiso = 0;
			
			if (ciiuPrincipal != null && ciiuPrincipal.getId() != null) {
				if(ciiuPrincipal.getTipoPregunta() != null) {
					importCatalogo = catalogoMayorImportanciaConsideraciones(1);
					importancias.add(importCatalogo);
				} else {
				if(tipoOpcion1!=null && (tipoOpcion1==7 ||tipoOpcion1==8)) {
					CombinacionSubActividades combinacionSubActividades = new CombinacionSubActividades();
					CatalogoCIUU catalogoCIUU = new CatalogoCIUU();
					if (tipoOpcion1 == 7) {
						if (idSubactividades1 == null) {
							tipoPermiso = Integer.valueOf(subActividad1
									.getTipoPermiso());
							catalogoCIUU = subActividad1.getCatalogoCIUU();
							importCatalogo.setSubActividades(subActividad1);
							ciiu1.setSubActividad(subActividad1);
						} else {
							if (idSubactividades1.length == 1) {
								for (Integer id : idSubactividades1) {
									for (SubActividades subA : listaSubActividades1) {
										if (subA.getId().intValue() == id
												.intValue()) {
											tipoPermiso = Integer.valueOf(subA
													.getTipoPermiso());
											catalogoCIUU = subA.getCatalogoCIUU();
											importCatalogo.setSubActividades(subA);
											ciiu1.setSubActividad(subA);
										}
									}
								}
							} else {
								for (SubActividades subA : listaSubActividades1) {
									combinacionSubActividades = combinacionActividadesFacade
											.getPorCatalogoCombinacion(
													subA.getCatalogoCIUU(),
													combinacion1);
									tipoPermiso = Integer
											.valueOf(combinacionSubActividades
													.getTipoPermiso());
									catalogoCIUU = subA.getCatalogoCIUU();
									importCatalogo
											.setCombinacionSubActividades(combinacionSubActividades);
									ciiu1.setCombinacionSubActividades(combinacionSubActividades);
									break;
								}
							}
						}
					}
	
					if (tipoOpcion1 == 8) {
						if (!parent1.getValorOpcion()) {
							tipoPermiso = parent1.getOpcionPermisoNo();
							catalogoCIUU = parent1.getCatalogoCIUU();
							importCatalogo.setSubActividades(parent1);
							ciiu1.setSubActividad(parent1);
							ciiu1.setValorOpcion(parent1.getValorOpcion());
							requiereTablaResiduo1 = parent1
									.getRequiereIngresoResiduos() == null ? false
									: parent1.getRequiereIngresoResiduos();
							proyectoSubAct1 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct1.setSubActividad(parent1);
							proyectoSubAct1.setRespuesta(parent1.getValorOpcion());
							listaProyectoCiiuSubActividad1 = new ArrayList<ProyectoCoaCiuuSubActividades>();
							listaProyectoCiiuSubActividad1.add(proyectoSubAct1);
						} else {
							ciiu1.setSubActividad(parent1);
							ciiu1.setValorOpcion(parent1.getValorOpcion());
							proyectoSubAct1 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct1.setSubActividad(parent1);
							proyectoSubAct1.setRespuesta(parent1.getValorOpcion());
							listaProyectoCiiuSubActividad1.add(proyectoSubAct1);
	
							if (idSubactividades1.length == 1) {
								for (Integer id : idSubactividades1) {
									for (SubActividades subA : listaSubActividades1) {
										if (subA.getId().intValue() == id
												.intValue()) {
											tipoPermiso = Integer.valueOf(subA
													.getTipoPermiso());
											catalogoCIUU = subA.getCatalogoCIUU();
											importCatalogo.setSubActividades(subA);
											ciiu1.setSubActividad(subA);
											ciiu1.setValorOpcion(null);
										}
									}
								}
							} else {
								for (SubActividades subA : listaSubActividades1) {
									combinacionSubActividades = combinacionActividadesFacade
											.getPorCatalogoCombinacion(
													subA.getCatalogoCIUU(),
													combinacion1);
									tipoPermiso = Integer
											.valueOf(combinacionSubActividades
													.getTipoPermiso());
									catalogoCIUU = subA.getCatalogoCIUU();
									importCatalogo
											.setCombinacionSubActividades(combinacionSubActividades);
									ciiu1.setCombinacionSubActividades(combinacionSubActividades);
									break;
								}
							}
						}
					}
					importCatalogo.setCatalogo(catalogoCIUU);
					importCatalogo.setTipoPermiso(tipoPermiso);
					importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(
							catalogoCIUU.getCodigo().substring(0, 7))
							.getImportanciaRelativa());
					importCatalogo.setWf("wf1");
					importancias.add(importCatalogo);
				} else if (subActividad1.getId() != null || parent1.getId() != null) {
					importCatalogo = new CatalogoImportanciaVO();
					SubActividades actividad = subActividad1;
					// ---------
					boolean doblePregunta = false;
					String actividadesDoblePregunta = Constantes
							.getActividadesDoblePregunta();
					String[] actividadesDoblePreguntaArray = actividadesDoblePregunta
							.split(",");
					for (String actividadD : actividadesDoblePreguntaArray) {
						String acti = subActividad1.getId() == null ? parent1
								.getCatalogoCIUU().getCodigo() : subActividad1
								.getCatalogoCIUU().getCodigo();
						if (acti.equals(actividadD)) {
							doblePregunta = true;
							break;
						}
					}
					if (doblePregunta) {
						if (parent1.getId() != null && !parent1.getValorOpcion()) {
							actividad = parent1;
						}
					} else {
						if (parent1.getId() != null
								&& parent1.getValorOpcion() != null
								&& parent1.getValorOpcion() && tipoOpcion1 != 45) {
							actividad = parent1;
						}
	
						if (parent1.getId() != null
								&& parent1.getValorOpcion() != null
								&& !parent1.getValorOpcion() && tipoOpcion1 == 45) {
							actividad = parent1;
							if (parent1.getCatalogoCIUU().getCodigo().compareTo("I5510.01") == 0) {
								actividad = subActividad1;
							}
						}
	
					}
					if (actividad.getEsMultiple() != null
							&& actividad.getEsMultiple()) {
						tipoPermiso = Integer.valueOf(actividad.getTipoPermiso());
					} else {
						if (actividad.getValorOpcion() != null
								&& actividad.getValorOpcion()) {
							tipoPermiso = actividad.getOpcionPermisoSi();
						} else {
							tipoPermiso = actividad.getOpcionPermisoNo();
						}
						if (tipoOpcion1 == 11 && tipoPermiso == null
								&& !actividad.getEsMultiple()) {
							tipoPermiso = actividad.getOpcionPermisoSi();
						}
					}
	
					if (tipoPermiso == null
							&& actividad.getFinanciadoBancoEstado() != null
							&& actividad.getFinanciadoBancoEstado()) {
						importCatalogo = mayorImportanciaPorCatalogo(
								importCatalogo, 1);
						importancias.add(importCatalogo);
					} else {
						SubActividades parent = subActividadesFacade
								.actividadParent(actividad.getId());
						importCatalogo.setCatalogo(parent == null ? actividad
								.getCatalogoCIUU() : parent.getCatalogoCIUU());
						importCatalogo.setTipoPermiso(tipoPermiso);
						if (parent != null || actividad.getCatalogoCIUU() != null) {
							if (actividad
									.getNombre()
									.equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION)) {
								importCatalogo.setImportancia(catalogoCIUUFacade
										.catalogoCiuu(
												parent.getCatalogoCIUU()
														.getCodigo()
														.substring(0, 7))
										.getImportanciaRelativa());
								tipoPermiso = actividad.getOpcionPermisoSi();
							} else
								importCatalogo.setImportancia(catalogoCIUUFacade
										.catalogoCiuu(
												parent == null ? actividad
														.getCatalogoCIUU()
														.getCodigo()
														.substring(0, 7) : parent
														.getCatalogoCIUU()
														.getCodigo()
														.substring(0, 7))
										.getImportanciaRelativa());
						} else {
							importCatalogo.setImportancia(0);
						}
						importCatalogo.setWf("wf1");
						importancias.add(importCatalogo);
					}
				} else {
					importCatalogo = mayorImportanciaPorCatalogo(importCatalogo, 1);
					importancias.add(importCatalogo);
				}
				}
			}
			
			if (ciiuComplementaria1 != null
					&& ciiuComplementaria1.getId() != null) {
				if(ciiuComplementaria1.getTipoPregunta() != null) {
					importCatalogo = catalogoMayorImportanciaConsideraciones(2);
					importancias.add(importCatalogo);
				} else {
				tipoPermiso = 0;
				importCatalogo = new CatalogoImportanciaVO();
				if (tipoOpcion2 != null
						&& (tipoOpcion2 == 7 || tipoOpcion2 == 8)) {
					CombinacionSubActividades combinacionSubActividades = new CombinacionSubActividades();
					CatalogoCIUU catalogoCIUU = new CatalogoCIUU();
					if (tipoOpcion2 == 7) {
						if (idSubactividades2.length == 1) {
							for (Integer id : idSubactividades2) {
								for (SubActividades subA : listaSubActividades2) {
									if (subA.getId().intValue() == id
											.intValue()) {
										tipoPermiso = Integer.valueOf(subA
												.getTipoPermiso());
										catalogoCIUU = subA.getCatalogoCIUU();
										importCatalogo.setSubActividades(subA);
										ciiu2.setSubActividad(subA);
									}
								}
							}
						} else {
							for (SubActividades subA : listaSubActividades2) {
								combinacionSubActividades = combinacionActividadesFacade
										.getPorCatalogoCombinacion(
												subA.getCatalogoCIUU(),
												combinacion2);
								tipoPermiso = Integer
										.valueOf(combinacionSubActividades
												.getTipoPermiso());
								catalogoCIUU = subA.getCatalogoCIUU();
								importCatalogo
										.setCombinacionSubActividades(combinacionSubActividades);
								ciiu2.setCombinacionSubActividades(combinacionSubActividades);
								break;
							}
						}
					}

					if (tipoOpcion2 == 8) {
						if (!parent2.getValorOpcion()) {
							tipoPermiso = parent2.getOpcionPermisoNo();
							catalogoCIUU = parent2.getCatalogoCIUU();
							importCatalogo.setSubActividades(parent2);
							ciiu2.setSubActividad(parent2);
							ciiu2.setValorOpcion(parent2.getValorOpcion());
							requiereTablaResiduo2 = parent2
									.getRequiereIngresoResiduos() == null ? false
									: parent2.getRequiereIngresoResiduos();
							proyectoSubAct2 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct2.setSubActividad(parent2);
							proyectoSubAct2.setRespuesta(parent2
									.getValorOpcion());
							listaProyectoCiiuSubActividad2 = new ArrayList<ProyectoCoaCiuuSubActividades>();
							listaProyectoCiiuSubActividad2.add(proyectoSubAct2);
						} else {
							ciiu2.setSubActividad(parent2);
							ciiu2.setValorOpcion(parent2.getValorOpcion());
							proyectoSubAct2 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct2.setSubActividad(parent2);
							proyectoSubAct2.setRespuesta(parent2
									.getValorOpcion());
							listaProyectoCiiuSubActividad2.add(proyectoSubAct2);
							if (idSubactividades2.length == 1) {
								for (Integer id : idSubactividades2) {
									for (SubActividades subA : listaSubActividades2) {
										if (subA.getId().intValue() == id
												.intValue()) {
											tipoPermiso = Integer.valueOf(subA
													.getTipoPermiso());
											catalogoCIUU = subA
													.getCatalogoCIUU();
											importCatalogo
													.setSubActividades(subA);
											ciiu2.setSubActividad(subA);
											ciiu2.setValorOpcion(null);
										}
									}
								}
							} else {
								for (SubActividades subA : listaSubActividades2) {
									combinacionSubActividades = combinacionActividadesFacade
											.getPorCatalogoCombinacion(
													subA.getCatalogoCIUU(),
													combinacion2);
									tipoPermiso = Integer
											.valueOf(combinacionSubActividades
													.getTipoPermiso());
									catalogoCIUU = subA.getCatalogoCIUU();
									importCatalogo
											.setCombinacionSubActividades(combinacionSubActividades);
									ciiu2.setCombinacionSubActividades(combinacionSubActividades);
									break;
								}
							}
						}
					}
					importCatalogo.setCatalogo(catalogoCIUU);
					importCatalogo.setTipoPermiso(tipoPermiso);
					importCatalogo.setImportancia(catalogoCIUUFacade
							.catalogoCiuu(
									catalogoCIUU.getCodigo().substring(0, 7))
							.getImportanciaRelativa());
					importCatalogo.setWf("wf2");
					importancias.add(importCatalogo);
				} else if (subActividad2.getId() != null
						|| parent2.getId() != null) {
					importCatalogo = new CatalogoImportanciaVO();
					SubActividades actividad = subActividad2;
					// ---------
					boolean doblePregunta = false;
					String actividadesDoblePregunta = Constantes
							.getActividadesDoblePregunta();
					String[] actividadesDoblePreguntaArray = actividadesDoblePregunta
							.split(",");
					for (String actividadD : actividadesDoblePreguntaArray) {
						String acti = subActividad2.getId() == null ? parent2
								.getCatalogoCIUU().getCodigo() : subActividad2
								.getCatalogoCIUU().getCodigo();
						if (acti.equals(actividadD)) {
							doblePregunta = true;
							break;
						}
					}
					if (doblePregunta) {
						if (parent2.getId() != null
								&& !parent2.getValorOpcion()) {
							actividad = parent2;
						}
					} else {
						if (parent2.getId() != null
								&& parent2.getValorOpcion() != null
								&& parent2.getValorOpcion()
								&& tipoOpcion2 != 45) {
							actividad = parent2;
						}

						if (parent2.getId() != null
								&& parent2.getValorOpcion() != null
								&& !parent2.getValorOpcion()
								&& tipoOpcion2 == 45) {
							actividad = parent2;
							if (parent2.getCatalogoCIUU().getCodigo().compareTo("I5510.01") == 0) {
								actividad = subActividad2;
							}
						}

					}
					if (actividad.getEsMultiple() != null
							&& actividad.getEsMultiple()) {
						tipoPermiso = Integer.valueOf(actividad
								.getTipoPermiso());
					} else {
						if (actividad.getValorOpcion() != null
								&& actividad.getValorOpcion()) {
							tipoPermiso = actividad.getOpcionPermisoSi();
						} else {
							tipoPermiso = actividad.getOpcionPermisoNo();
						}
					}

					if (tipoPermiso == null
							&& actividad.getFinanciadoBancoEstado() != null
							&& actividad.getFinanciadoBancoEstado()) {
						importCatalogo = mayorImportanciaPorCatalogo(
								importCatalogo, 2);
						importancias.add(importCatalogo);
					} else {
						SubActividades parent = subActividadesFacade
								.actividadParent(actividad.getId());
						if (parent != null
								|| actividad.getCatalogoCIUU() != null) {
							importCatalogo
									.setCatalogo(parent == null ? actividad
											.getCatalogoCIUU() : parent
											.getCatalogoCIUU());
							importCatalogo.setTipoPermiso(tipoPermiso);
							if (actividad
									.getNombre()
									.equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION)) {
								importCatalogo.setTipoPermiso(actividad
										.getOpcionPermisoSi());
								importCatalogo
										.setImportancia(catalogoCIUUFacade
												.catalogoCiuu(
														parent.getCatalogoCIUU()
																.getCodigo()
																.substring(0, 7))
												.getImportanciaRelativa());
							} else
								importCatalogo
										.setImportancia(catalogoCIUUFacade
												.catalogoCiuu(
														parent == null ? actividad
																.getCatalogoCIUU()
																.getCodigo()
																.substring(0, 7)
																: parent.getCatalogoCIUU()
																		.getCodigo()
																		.substring(
																				0,
																				7))
												.getImportanciaRelativa());
							importCatalogo.setWf("wf2");
							importancias.add(importCatalogo);
						}
					}
				} else {
					importCatalogo = mayorImportanciaPorCatalogo(
							importCatalogo, 2);
					importancias.add(importCatalogo);
				}
				}
			}

			if (ciiuComplementaria2 != null
					&& ciiuComplementaria2.getId() != null) {
				if(ciiuComplementaria2.getTipoPregunta() != null) {
					importCatalogo = catalogoMayorImportanciaConsideraciones(3);
					importancias.add(importCatalogo);
				} else {
				tipoPermiso = 0;
				importCatalogo = new CatalogoImportanciaVO();
				if (tipoOpcion3 != null
						&& (tipoOpcion3 == 7 || tipoOpcion3 == 8)) {
					CombinacionSubActividades combinacionSubActividades = new CombinacionSubActividades();
					CatalogoCIUU catalogoCIUU = new CatalogoCIUU();
					if (tipoOpcion3 == 7) {
						if (idSubactividades3.length == 1) {
							for (Integer id : idSubactividades3) {
								for (SubActividades subA : listaSubActividades3) {
									if (subA.getId().intValue() == id
											.intValue()) {
										tipoPermiso = Integer.valueOf(subA
												.getTipoPermiso());
										catalogoCIUU = subA.getCatalogoCIUU();
										importCatalogo.setSubActividades(subA);
										ciiu3.setSubActividad(subA);
									}
								}
							}
						} else {
							for (SubActividades subA : listaSubActividades3) {
								combinacionSubActividades = combinacionActividadesFacade
										.getPorCatalogoCombinacion(
												subA.getCatalogoCIUU(),
												combinacion3);
								tipoPermiso = Integer
										.valueOf(combinacionSubActividades
												.getTipoPermiso());
								catalogoCIUU = subA.getCatalogoCIUU();
								importCatalogo
										.setCombinacionSubActividades(combinacionSubActividades);
								ciiu3.setCombinacionSubActividades(combinacionSubActividades);
								break;
							}
						}
					}

					if (tipoOpcion3 == 8) {
						if (!parent3.getValorOpcion()) {
							tipoPermiso = parent3.getOpcionPermisoNo();
							catalogoCIUU = parent3.getCatalogoCIUU();
							importCatalogo.setSubActividades(parent3);
							ciiu3.setSubActividad(parent3);
							ciiu3.setValorOpcion(parent3.getValorOpcion());
							requiereTablaResiduo3 = parent3
									.getRequiereIngresoResiduos() == null ? false
									: parent3.getRequiereIngresoResiduos();
							proyectoSubAct3 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct3.setSubActividad(parent3);
							proyectoSubAct3.setRespuesta(parent3
									.getValorOpcion());
							listaProyectoCiiuSubActividad3 = new ArrayList<ProyectoCoaCiuuSubActividades>();
							listaProyectoCiiuSubActividad3.add(proyectoSubAct3);
						} else {
							ciiu3.setSubActividad(parent3);
							ciiu3.setValorOpcion(parent3.getValorOpcion());
							proyectoSubAct3 = new ProyectoCoaCiuuSubActividades();
							proyectoSubAct3.setSubActividad(parent3);
							proyectoSubAct3.setRespuesta(parent3
									.getValorOpcion());
							listaProyectoCiiuSubActividad3.add(proyectoSubAct3);
							if (idSubactividades3.length == 1) {
								for (Integer id : idSubactividades3) {
									for (SubActividades subA : listaSubActividades3) {
										if (subA.getId().intValue() == id
												.intValue()) {
											tipoPermiso = Integer.valueOf(subA
													.getTipoPermiso());
											catalogoCIUU = subA
													.getCatalogoCIUU();
											importCatalogo
													.setSubActividades(subA);
											ciiu3.setSubActividad(subA);
											ciiu3.setValorOpcion(null);
										}
									}
								}
							} else {
								for (SubActividades subA : listaSubActividades3) {
									combinacionSubActividades = combinacionActividadesFacade
											.getPorCatalogoCombinacion(
													subA.getCatalogoCIUU(),
													combinacion3);
									tipoPermiso = Integer
											.valueOf(combinacionSubActividades
													.getTipoPermiso());
									catalogoCIUU = subA.getCatalogoCIUU();
									importCatalogo
											.setCombinacionSubActividades(combinacionSubActividades);
									ciiu3.setCombinacionSubActividades(combinacionSubActividades);
									break;
								}
							}
						}
					}
					importCatalogo.setCatalogo(catalogoCIUU);
					importCatalogo.setTipoPermiso(tipoPermiso);
					importCatalogo.setImportancia(catalogoCIUUFacade
							.catalogoCiuu(
									catalogoCIUU.getCodigo().substring(0, 7))
							.getImportanciaRelativa());
					importCatalogo.setWf("wf3");
					importancias.add(importCatalogo);
				} else if (subActividad3.getId() != null
						|| parent3.getId() != null) {
					importCatalogo = new CatalogoImportanciaVO();
					SubActividades actividad = subActividad3;
					// ---------
					boolean doblePregunta = false;
					String actividadesDoblePregunta = Constantes
							.getActividadesDoblePregunta();
					String[] actividadesDoblePreguntaArray = actividadesDoblePregunta
							.split(",");
					for (String actividadD : actividadesDoblePreguntaArray) {
						String acti = subActividad3.getId() == null ? parent3
								.getCatalogoCIUU().getCodigo() : subActividad3
								.getCatalogoCIUU().getCodigo();
						if (acti.equals(actividadD)) {
							doblePregunta = true;
							break;
						}
					}
					if (doblePregunta) {
						if (parent3.getId() != null
								&& !parent3.getValorOpcion()) {
							actividad = parent3;
						}
					} else {
						if (parent3.getId() != null
								&& parent3.getValorOpcion() != null
								&& parent3.getValorOpcion()
								&& tipoOpcion3 != 45) {
							actividad = parent3;
						}

						if (parent3.getId() != null
								&& parent3.getValorOpcion() != null
								&& !parent3.getValorOpcion()
								&& tipoOpcion3 == 45) {
							actividad = parent3;
							if (parent3.getCatalogoCIUU().getCodigo().compareTo("I5510.01") == 0) {
								actividad = subActividad3;
							}
						}

					}
					if (actividad.getEsMultiple() != null
							&& actividad.getEsMultiple()) {
						tipoPermiso = Integer.valueOf(actividad
								.getTipoPermiso());
					} else {
						if (actividad.getValorOpcion() != null
								&& actividad.getValorOpcion()) {
							tipoPermiso = actividad.getOpcionPermisoSi();
						} else {
							tipoPermiso = actividad.getOpcionPermisoNo();
						}
					}

					if (tipoPermiso == null
							&& actividad.getFinanciadoBancoEstado() != null
							&& actividad.getFinanciadoBancoEstado()) {
						importCatalogo = mayorImportanciaPorCatalogo(
								importCatalogo, 3);
						importancias.add(importCatalogo);
					} else {
						SubActividades parent = subActividadesFacade
								.actividadParent(actividad.getId());
						if (parent != null
								|| actividad.getCatalogoCIUU() != null) {
							importCatalogo
									.setCatalogo(parent == null ? actividad
											.getCatalogoCIUU() : parent
											.getCatalogoCIUU());
							importCatalogo.setTipoPermiso(tipoPermiso);
							if (actividad
									.getNombre()
									.equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION)) {
								tipoPermiso = actividad.getOpcionPermisoSi();
								importCatalogo
										.setImportancia(catalogoCIUUFacade
												.catalogoCiuu(
														parent.getCatalogoCIUU()
																.getCodigo()
																.substring(0, 7))
												.getImportanciaRelativa());
							} else
								importCatalogo
										.setImportancia(catalogoCIUUFacade
												.catalogoCiuu(
														parent == null ? actividad
																.getCatalogoCIUU()
																.getCodigo()
																.substring(0, 7)
																: parent.getCatalogoCIUU()
																		.getCodigo()
																		.substring(
																				0,
																				7))
												.getImportanciaRelativa());
							importCatalogo.setWf("wf3");
							importancias.add(importCatalogo);
						}
					}

				} else {
					importCatalogo = mayorImportanciaPorCatalogo(
							importCatalogo, 3);
					importancias.add(importCatalogo);
				}
				}
			}

			Integer categoriaAlta = 0, importanciaAlta = 0;
			CatalogoCIUU catalogoMayorImp = new CatalogoCIUU();
			String wfPuesto = "";
			actividadSubYCombi = new CatalogoImportanciaVO();
			for (CatalogoImportanciaVO catalogos : importancias) {
				// System.out.println("id:"+catalogos.getCatalogo().getId()+" categoria:"+catalogos.getTipoPermiso()+" Importancia:"+catalogos.getImportancia());
				Integer categoria = (catalogos.getTipoPermiso() == null) ? 0
						: catalogos.getTipoPermiso();
				Integer importancia = catalogos.getImportancia();
				if (categoria == categoriaAlta) {
					if (importancia > importanciaAlta) {
						categoriaAlta = catalogos.getTipoPermiso();
						importanciaAlta = catalogos.getImportancia();
						// id = catalogos.getCatalogo().getId();
						catalogoMayorImp = catalogos.getCatalogo();
						wfPuesto = catalogos.getWf();
						actividadSubYCombi.setCatalogo(catalogos.getCatalogo());
						actividadSubYCombi.setSubActividades(catalogos
								.getSubActividades());
						actividadSubYCombi
								.setCombinacionSubActividades(catalogos
										.getCombinacionSubActividades());
					}
				} else if (categoria > categoriaAlta) {
					categoriaAlta = catalogos.getTipoPermiso();
					importanciaAlta = catalogos.getImportancia();
					catalogoMayorImp = catalogos.getCatalogo();
					// id = catalogos.getCatalogo().getId();
					wfPuesto = catalogos.getWf();
					actividadSubYCombi.setCatalogo(catalogos.getCatalogo());
					actividadSubYCombi.setSubActividades(catalogos
							.getSubActividades());
					actividadSubYCombi.setCombinacionSubActividades(catalogos
							.getCombinacionSubActividades());
				}
			}
			// System.out.println("categoria Alta:"+categoriaAlta+" id:"+id);

			if (catalogoMayorImp.getCodigo() != null) {
				WolframVO wfResul = new WolframVO();
				wfResul.setCatalago(catalogoMayorImp);
				wfResul.setNivel5(catalogoMayorImp.getCodigo().substring(0, 7));
				wfResul.setNivel3(catalogoMayorImp.getCodigo().substring(0, 4));
				wfResul.setImportancia(catalogoCIUUFacade.catalogoCiuu(
						catalogoMayorImp.getCodigo().substring(0, 7))
						.getImportanciaRelativa());
				if (wfPuesto.equals("wf1"))
					wfResul.setPuesto(1);
				if (wfPuesto.equals("wf2"))
					wfResul.setPuesto(2);
				if (wfPuesto.equals("wf3"))
					wfResul.setPuesto(3);

				wfCalculo = wfResul;
				nombreSector = wfResul.getCatalago().getTipoSector()
						.getNombre().toUpperCase();
				actividadPrimaria = wfResul.getCatalago();
				wfCalculo.setCalculo(true);
				wfCalculo.setCatalago(catalogoCIUUFacade.catalogoCiuu(wfCalculo
						.getNivel5()));
			}

		} catch (Exception e) {
			msgError.add("Ocurrió un error al guardar la información");
			e.printStackTrace();
		}
	}
	
	public CatalogoImportanciaVO mayorImportanciaPorCatalogo(
			CatalogoImportanciaVO importCatalogo, Integer tipo) {
		Integer tipoPermisoActividad = 0;
		switch (tipo) {
		case 1:
			tipoPermisoActividad = importanciaXActiviad(ciiuPrincipal);
			if (subActividadesCiiuBean
					.esActividadRecupercionMateriales(ciiuPrincipal)
					&& listaSubActividades1 != null
					&& listaSubActividades1.size() > 0) {
				ciiuPrincipal.setSubActividades(listaSubActividades1);
				subActividadesCiiuBean
						.recuperarConsideracionesPorSubActividades(ciiuPrincipal);
				if (subActividadesCiiuBean
						.getCombinacionSubActividadesTipoEnte() != null)
					tipoPermisoActividad = Integer
							.valueOf(subActividadesCiiuBean
									.getCombinacionSubActividadesTipoEnte()
									.getTipoPermiso());
			}

			importCatalogo.setCatalogo(ciiuPrincipal);
			importCatalogo.setTipoPermiso(tipoPermisoActividad);
			importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(
					ciiuPrincipal.getCodigo().substring(0, 7))
					.getImportanciaRelativa());
			importCatalogo.setWf("wf1");
			break;
		case 2:
			tipoPermisoActividad = importanciaXActiviad(ciiuComplementaria1);
			if (subActividadesCiiuBean
					.esActividadRecupercionMateriales(ciiuComplementaria1)
					&& listaSubActividades2 != null
					&& listaSubActividades2.size() > 0) {
				ciiuComplementaria1.setSubActividades(listaSubActividades2);
				subActividadesCiiuBean
						.recuperarConsideracionesPorSubActividades(ciiuComplementaria1);
				if (subActividadesCiiuBean
						.getCombinacionSubActividadesTipoEnte() != null)
					tipoPermisoActividad = Integer
							.valueOf(subActividadesCiiuBean
									.getCombinacionSubActividadesTipoEnte()
									.getTipoPermiso());
			}

			importCatalogo.setCatalogo(ciiuComplementaria1);
			importCatalogo.setTipoPermiso(tipoPermisoActividad);
			importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(
					ciiuComplementaria1.getCodigo().substring(0, 7))
					.getImportanciaRelativa());
			importCatalogo.setWf("wf2");
			break;
		case 3:
			tipoPermisoActividad = importanciaXActiviad(ciiuComplementaria2);
			if (subActividadesCiiuBean
					.esActividadRecupercionMateriales(ciiuComplementaria2)
					&& listaSubActividades3 != null
					&& listaSubActividades3.size() > 0) {
				ciiuComplementaria2.setSubActividades(listaSubActividades3);
				subActividadesCiiuBean
						.recuperarConsideracionesPorSubActividades(ciiuComplementaria2);
				if (subActividadesCiiuBean
						.getCombinacionSubActividadesTipoEnte() != null)
					tipoPermisoActividad = Integer
							.valueOf(subActividadesCiiuBean
									.getCombinacionSubActividadesTipoEnte()
									.getTipoPermiso());
			}

			importCatalogo.setCatalogo(ciiuComplementaria2);
			importCatalogo.setTipoPermiso(tipoPermisoActividad);
			importCatalogo.setImportancia(catalogoCIUUFacade.catalogoCiuu(
					ciiuComplementaria2.getCodigo().substring(0, 7))
					.getImportanciaRelativa());
			importCatalogo.setWf("wf3");
			break;
		default:
			break;
		}

		return importCatalogo;
	}

	// funcion extraer categorizacion (1-2-3-4)
	public Integer importanciaXActiviad(CatalogoCIUU catalogo) {
		String nivel = "0";
		if (catalogo.getTipoPermiso() != null
				&& catalogo.getTipoPermiso().equals("CERTIFICADO AMBIENTAL")) {
			nivel = "1";
		}
		if (catalogo.getTipoPermiso() != null
				&& catalogo.getTipoPermiso().equals("REGISTRO AMBIENTAL")) {
			nivel = "2";
		}
		if (catalogo.getTipoPermiso() != null
				&& catalogo.getTipoPermiso().equals("LICENCIA AMBIENTAL")) {
			nivel = "3";
		}
		if (catalogo.getTipoImpacto() != null
				&& catalogo.getTipoImpacto().equals("IMPACTO ALTO")) {
			nivel = "4";
		}
		return Integer.valueOf(nivel);
	}

	public Integer importanciaXActiviad(WolframVO wf, CatalogoCIUU catalogo) {
		String importancia = wf.getImportancia().toString();
		String nivel = "0";
		if (catalogo.getTipoPermiso() != null
				&& catalogo.getTipoPermiso().equals("CERTIFICADO AMBIENTAL")) {
			nivel = "1";
		}
		if (catalogo.getTipoPermiso() != null
				&& catalogo.getTipoPermiso().equals("REGISTRO AMBIENTAL")) {
			nivel = "2";
		}
		if (catalogo.getTipoPermiso() != null
				&& catalogo.getTipoPermiso().equals("LICENCIA AMBIENTAL")) {
			nivel = "3";
		}
		return Integer.valueOf(importancia + nivel);
	}

	public void nivelCriterios1() {
		listaCriterioMagnitudConsumo = new ArrayList<VariableCriterio>();
		listaCriterioMagnitudConsumo = variableCriterioFacade.listaCriterios(1);
		listaValoresMagnitud1 = new ArrayList<ValorMagnitud>();
		criteriomagnitud = "";
	}

	public void nivelCriterios2() {
		listaCriterioMagnitudDimensionamiento = new ArrayList<VariableCriterio>();
		listaCriterioMagnitudDimensionamiento = variableCriterioFacade
				.listaCriterios(2);
		listaValoresMagnitud2 = new ArrayList<ValorMagnitud>();
		criteriomagnitud = "";
	}

	public void nivelCriterios3() {
		listaCriterioMagnitudCapacidad = new ArrayList<VariableCriterio>();
		listaCriterioMagnitudCapacidad = variableCriterioFacade
				.listaCriterios(3);
		listaValoresMagnitud3 = new ArrayList<ValorMagnitud>();
		criteriomagnitud = "";
	}

	public void valorCriterios1() {
		VariableCriterio criterio = criterioSeleccion;
		criteriomagnitud = criterio.getNombre();
		listaValoresMagnitud1 = new ArrayList<ValorMagnitud>();
		listaValoresMagnitud1 = valorMagnitudFacade.listaValores(criterio);
		estadoMagnitud = true;

		criterioSeleccion = new VariableCriterio();
	}

	public void valorCriterios2() {
		VariableCriterio criterio = criterioSeleccion;
		criteriomagnitud = criterio.getNombre();
		listaValoresMagnitud2 = new ArrayList<ValorMagnitud>();
		listaValoresMagnitud2 = valorMagnitudFacade.listaValores(criterio);
		estadoMagnitud = true;

		criterioSeleccion = new VariableCriterio();
	}

	public void valorCriterios3() {
		VariableCriterio criterio = criterioSeleccion;
		criteriomagnitud = criterio.getNombre();
		listaValoresMagnitud3 = new ArrayList<ValorMagnitud>();
		listaValoresMagnitud3 = valorMagnitudFacade.listaValores(criterio);
		estadoMagnitud = true;

		criterioSeleccion = new VariableCriterio();
	}

	public int magnitudMayorImportancia() {
		int x = 0;

		if (valorMagnitud1 == null)
			valorMagnitud1 = new ValorMagnitud();
		if (valorMagnitud2 == null)
			valorMagnitud2 = new ValorMagnitud();
		if (valorMagnitud3 == null)
			valorMagnitud3 = new ValorMagnitud();

		Integer importancia = valorMagnitud1.getValorVariable() == null ? 0
				: valorMagnitud1.getValorVariable();
		if (valorMagnitud1.getValorVariable() != null) {
			valorMagnitudCalculo = valorMagnitud1;
		}
		if (valorMagnitud2.getValorVariable() != null
				&& valorMagnitud2.getValorVariable() > importancia) {
			importancia = valorMagnitud2.getValorVariable();
			valorMagnitudCalculo = valorMagnitud2;
		}
		if (valorMagnitud3.getValorVariable() != null
				&& valorMagnitud3.getValorVariable() > importancia) {
			importancia = valorMagnitud3.getValorVariable();
			valorMagnitudCalculo = valorMagnitud3;
		}

		if (valorMagnitud1.getId() != null)
			x++;
		if (valorMagnitud2.getId() != null)
			x++;
		if (valorMagnitud3.getId() != null)
			x++;

		return x;
	}

	public boolean calculoWolfram() {
		boolean estado = false;
		try {
			Integer p1 = 0, p2 = 0, p3 = 0, p4 = 0, p5 = 0, p6 = 0, p7 = 0, p8 = 0;
			p1 = wfCalculo.getCatalago().getCriterioQuimico();
			p2 = wfCalculo.getCatalago().getCriterioAmbiental();
			p3 = wfCalculo.getCatalago().getCriterioConcentracion();
			p4 = wfCalculo.getCatalago().getCriterioRiesgo();
			p5 = wfCalculo.getCatalago().getCriterioSocial();
			p6 = valorMagnitudCalculo.getValorVariable();
			p7 = valorCapacidadBiofisica;
			p8 = valorCapacidadSocial;
			JSONArray json = readJsonFromUrl(Constantes.getWolframRest()
					+ "?p1=" + p1 + "&p2=" + p2 + "&p3=" + p3 + "&p4=" + p4
					+ "&p5=" + p5 + "&p6=" + p6 + "&p7=" + p7 + "&p8=" + p8);
			categoria = (JSONObject) json.get(0);
			if (categoria.get("categoria").equals(JSONObject.NULL)) {
				System.out.println("error wolfram categoria:::"
						+ categoria.get("categoria"));
				estado = false;
			} else
				estado = true;

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error wolfram");
			estado = false;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return estado;
	}

	private String generarCodigo() {
		String codigo = Constantes.SIGLAS_INSTITUCION + "-RA-";
		Date date = new Date();
		SimpleDateFormat getAnio = new SimpleDateFormat("yyyy");
		String anioActual = getAnio.format(date);
		SimpleDateFormat getHora = new SimpleDateFormat("HHmmss");
		String horaActual = getHora.format(date);

		codigo += anioActual + "-" + loginBean.getNombreUsuario().substring(6)
				+ "-" + horaActual;
		return codigo;
	}

	public void presentar() throws JSONException {
		msgError = new ArrayList<String>();

		if (proyecto.getCodigoUnicoAmbiental() == null) {
			proyecto.setCodigoUnicoAmbiental(generarCodigo());
		}

		String valCategoria = (String) categoria.get("categoria");// calculos de
																	// wolfram
		// proyecto.setUsuario(loginBean.getUsuario());
		proyecto.setTipoProyecto(1);// 1=Proyecto nuevo, 2=Proyecto en ejecucion
		proyecto.setCategorizacion(Integer.valueOf(valCategoria));
		proyecto.setCodigoPma(wfCalculo.getNivel3());
		proyecto.setProyectoFinalizado(false);
		if (!tramiteEnProceso)
			proyecto.setFechaGeneracionCua(new Date());
		proyecto.setInterecaSnap(false);
		proyecto.setInterecaBosqueProtector(false);
		proyecto.setInterecaPatrimonioForestal(false);
		proyecto.setInterecaRamsar(false);
		proyecto.setIdCantonOficina(ubicacionOficinaTecnica.getId());
		proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);

		// coordenadas de ubicacion geografica--------------
		if (uploadedFileGeo != null) {
			// proyectoLicenciaAmbientalCoaShapeFacade.eliminar(proyecto, 2);
			for (int i = 0; i <= coordinatesWrappersGeo.size() - 1; i++) {
				shape = new SimuladorProyectoLicenciaAmbientalCoaShape();
				shape.setTipoForma(poligono);
				shape.setProyectoLicenciaCoa(proyecto);
				shape.setTipo(2);// 2=coordenadas geograficas 1=coordenadas
									// implantacion
				shape.setNumeroActualizaciones(0);
				shape.setSuperficie(coordinatesWrappersGeo.get(i)
						.getSuperficie());
				shape = proyectoLicenciaAmbientalCoaShapeFacade.guardar(shape);

				if(coordinatesWrappersGeo.get(i).getCoordenadas().size() <= 3000) {
					SimuladorCoordenadasProyecto coor;
					for (int j = 0; j <= coordinatesWrappersGeo.get(i)
							.getCoordenadas().size() - 1; j++) {
						coor = new SimuladorCoordenadasProyecto();
						coor.setProyectoLicenciaAmbientalCoaShape(shape);
						coor.setOrdenCoordenada(coordinatesWrappersGeo.get(i)
								.getCoordenadas().get(j).getOrdenCoordenada());
						coor.setX(coordinatesWrappersGeo.get(i).getCoordenadas()
								.get(j).getX());
						coor.setY(coordinatesWrappersGeo.get(i).getCoordenadas()
								.get(j).getY());
						coor.setAreaGeografica(coordinatesWrappersGeo.get(i)
								.getCoordenadas().get(j).getAreaGeografica());
						coor.setTipoCoordenada(2);// 2=coordenadas geograficas
													// 1=coordenadas implantacion
						coor.setNumeroActualizaciones(0);
						coordenadasProyectoCoaFacade.guardar(coor);
					}
				} else {
					String queryCoordenadasGeo = "INSERT INTO coa_simulator.coordinates_project_licencing_coa "
							+ "(prsh_id, coor_status, coor_order, prsh_type, coor_id, coor_creation_date, coor_creator_user, coor_update_number, coor_x, coor_y, coor_geographic_area)"
							+ "VALUES ";
					for (int j = 0; j <=coordinatesWrappersGeo.get(i).getCoordenadas().size()-1; j++) {
						String id = crudServiceBean.getSecuenceNextValue("coordinates_project_licencing_coa_coor_id_seq", "coa_mae").toString();
						String queryAdd = "(" + shape.getId() + ", true, "
								+ coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getOrdenCoordenada() + ", 2, " 
								+ id + ", now(), '" + loginBean.getUsuario().getNombre() + "', 0, "
								+ coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getX()+ ", "  
								+ coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getY() + ", "
								+ coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getAreaGeografica() + "),";
						
						queryCoordenadasGeo += queryAdd;
					}
					queryCoordenadasGeo = queryCoordenadasGeo.substring(0, queryCoordenadasGeo.length() - 1);
					queryCoordenadasGeo += ";";

					crudServiceBean.insertUpdateByNativeQuery(queryCoordenadasGeo, null);
				}
				
			}
		}

		// coordenadas de ubicacion de implantacion--------------
		if (uploadedFileImpl != null) {
			// proyectoLicenciaAmbientalCoaShapeFacade.eliminar(proyecto, 1);
			for (int i = 0; i <= coordinatesWrappers.size() - 1; i++) {
				shape = new SimuladorProyectoLicenciaAmbientalCoaShape();
				shape.setTipoForma(poligono);
				shape.setProyectoLicenciaCoa(proyecto);
				shape.setTipo(1);// 2=coordenadas geograficas 1=coordendas
									// implantacion
				shape.setNumeroActualizaciones(0);
				shape.setSuperficie(coordinatesWrappers.get(i).getSuperficie());
				shape = proyectoLicenciaAmbientalCoaShapeFacade.guardar(shape);
				
				if(coordinatesWrappers.get(i).getCoordenadas().size() <= 3000) {
					SimuladorCoordenadasProyecto coorImpl;
					for (int j = 0; j <= coordinatesWrappers.get(i)
							.getCoordenadas().size() - 1; j++) {
						coorImpl = new SimuladorCoordenadasProyecto();
						coorImpl.setProyectoLicenciaAmbientalCoaShape(shape);
						coorImpl.setOrdenCoordenada(coordinatesWrappers.get(i)
								.getCoordenadas().get(j).getOrdenCoordenada());
						coorImpl.setX(coordinatesWrappers.get(i).getCoordenadas()
								.get(j).getX());
						coorImpl.setY(coordinatesWrappers.get(i).getCoordenadas()
								.get(j).getY());
						coorImpl.setAreaGeografica(coordinatesWrappers.get(i)
								.getCoordenadas().get(j).getAreaGeografica());
						coorImpl.setTipoCoordenada(1);// 2=coordenadas geograficas
														// 1=coordenadas
														// implantacion
						coorImpl.setNumeroActualizaciones(0);
						coordenadasProyectoCoaFacade.guardar(coorImpl);
					}
				} else {
					String queryCoordenadasImpl = "INSERT INTO coa_simulator.coordinates_project_licencing_coa "
							+ "(prsh_id, coor_status, coor_order, prsh_type, coor_id, coor_creation_date, coor_creator_user, coor_update_number, coor_x, coor_y, coor_geographic_area)"
							+ "VALUES ";
					for (int j = 0; j <=coordinatesWrappers.get(i).getCoordenadas().size()-1; j++) {
						String id = crudServiceBean.getSecuenceNextValue("coordinates_project_licencing_coa_coor_id_seq", "coa_mae").toString();
						String queryAdd = "(" + shape.getId() + ", true, "
								+ coordinatesWrappers.get(i).getCoordenadas().get(j).getOrdenCoordenada() + ", 1, " 
								+ id + ", now(), '" + loginBean.getUsuario().getNombre() + "', 0, "
								+ coordinatesWrappers.get(i).getCoordenadas().get(j).getX()+ ", "  
								+ coordinatesWrappers.get(i).getCoordenadas().get(j).getY() + ", "
								+ coordinatesWrappers.get(i).getCoordenadas().get(j).getAreaGeografica() + "),";
						
						queryCoordenadasImpl += queryAdd;
					}
					queryCoordenadasImpl = queryCoordenadasImpl.substring(0, queryCoordenadasImpl.length() - 1);
					queryCoordenadasImpl += ";";

					crudServiceBean.insertUpdateByNativeQuery(queryCoordenadasImpl, null);
				}
				
			}

			// ubicaciones - parroquias------------------
			ProyectoLicenciaCoaUbicacion pro = new ProyectoLicenciaCoaUbicacion();
			// proyectoLicenciaCoaUbicacionFacade.eliminar(proyecto);
			for (UbicacionesGeografica ubi : ubicacionesSeleccionadas) {
				pro = new ProyectoLicenciaCoaUbicacion();
				// pro.setProyectoLicenciaCoa(proyecto);
				pro.setUbicacionesGeografica(ubi);
				if (area.getInec().equals(ubi.getCodificacionInec()))
					pro.setPrimario(true);
				else
					pro.setPrimario(false);

			}
		}

		// catalogo CIIU principal - complementario 1 - complementario 2
		// if(estadoCiiu)
		// {
		if (wf1.getPuesto() != null) {
			ciiu1.setCatalogoCIUU(ciiuPrincipal);
			ciiu1.setOrderJerarquia(wf1.getPuesto());
			ciiu1.setPrimario(false);
			// ciiu1.setProyectoLicenciaCoa(proyecto);
			if (wfCalculo.getPuesto() == wf1.getPuesto())
				ciiu1.setPrimario(true);
			// -------------sub actividades--------------------
			if (listaProyectoCiiuSubActividad1.size() > 0) {

				for (ProyectoCoaCiuuSubActividades val : listaProyectoCiiuSubActividad1) {
					ProyectoCoaCiuuSubActividades obj = new ProyectoCoaCiuuSubActividades();
					obj.setEstado(true);
					obj.setProyectoLicenciaCuaCiuu(ciiu1);
					obj.setSubActividad(val.getSubActividad());
					obj.setRespuesta(val.getRespuesta());

				}
			} else if (subActividad1.getId() != null || parent1.getId() != null) {
				boolean doblePregunta = false;
				boolean mineriaLibreAprovechamiento = false;
				String actividadesDoblePregunta = Constantes
						.getActividadesDoblePregunta();
				String[] actividadesDoblePreguntaArray = actividadesDoblePregunta
						.split(",");
				SubActividades subcatalogo = subActividad1.getId() == null ? parent1
						: subActividad1;
				for (String actividad : actividadesDoblePreguntaArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						doblePregunta = true;
					}
				}
				boolean galapagos = false;
				String actividadGalapagos = Constantes
						.getActividadesGalapagos();
				String[] actividadesGalapagosArray = actividadGalapagos
						.split(",");
				for (String actividad : actividadesGalapagosArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						galapagos = true;
					}
				}
				if (doblePregunta) {
					if (parent1.getId() != null && !parent1.getValorOpcion()) {
						subcatalogo = parent1;
						subActividad1 = parent1;
					}
				} else {
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

				boolean cremacion = false;
				String actividadesCremacion = Constantes
						.getActividadesCremacion();
				String[] actividadesCremacionArray = actividadesCremacion
						.split(",");
				for (String actividad : actividadesCremacionArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						cremacion = true;
					}
				}
				boolean alcantarillado = false;
				String actividadAlcantarillado = Constantes
						.getActividadesZonaRural();
				String[] actividadesAlcantarilladoArray = actividadAlcantarillado
						.split(",");
				for (String actividad : actividadesAlcantarilladoArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						alcantarillado = true;
					}
				}

				if (ciiu1.getCatalogoCIUU().getTipoSector().getId() == 1) {
					ciiu1.setAlmacenaHidrocarburos(subActividad1
							.getValorOpcion());
					ciiu1.setValorOpcion(subActividad1.getValorOpcion());
					ciiu1.setSubActividad(subActividad1);
				}
				if (ciiu1.getCatalogoCIUU().getTipoSector().getId() == 2) {
					if (!mineriaLibreAprovechamiento)
						ciiu1.setMineriaArtesanal(subActividad1
								.getValorOpcion());

					ciiu1.setValorOpcion(subActividad1.getValorOpcion());
					ciiu1.setSubActividad(subActividad1);
				}
				if (ciiu1.getCatalogoCIUU().getTipoSector().getId() == 3) {
					ciiu1.setValorOpcion(subActividad1.getValorOpcion());
					ciiu1.setSubActividad(subActividad1);

					if (doblePregunta) {
						ciiu1.setPotabilizacionAgua(parent1.getValorOpcion());
						ciiu1.setValorOpcion(subActividad1.getValorOpcion());
						ciiu1.setSubActividad(subActividad1);
						ciiu1.setIdActividadFinanciadoBancoEstado(bancoEstado1
								.getId());
						ciiu1.setFinanciadoBancoDesarrollo(bancoEstado1
								.getValorOpcion());
						if (!parent1.getValorOpcion()) {
							ciiu1.setValorOpcion(parent1.getValorOpcion());
							ciiu1.setSubActividad(parent1);
						}
					}
					if (cremacion) {
						ciiu1.setRealizaCremacion(subActividad1
								.getValorOpcion());
						ciiu1.setValorOpcion(subActividad1.getValorOpcion());
						ciiu1.setSubActividad(subActividad1);
					}
					if (alcantarillado) {
						ciiu1.setPotabilizacionAgua(null);
						ciiu1.setFinanciadoBancoDesarrollo(null);
						ciiu1.setAlcantarilladoMunicipio(parent1
								.getValorOpcion());
						ciiu1.setValorOpcion(parent1.getValorOpcion());
						ciiu1.setSubActividad(parent1);
						if (!parent1.getValorOpcion()) {
							ciiu1.setSubActividad(subActividad1);
						}
					}
					if (galapagos) {
						ciiu1.setValorOpcion(parent1.getValorOpcion());
						ciiu1.setSubActividad(parent1);
						if (parent1.getValorOpcion()
								|| (!parent1.getValorOpcion() && parent1
										.getCatalogoCIUU().getCodigo()
										.compareTo("I5510.01") == 0)) {
							ciiu1.setSubActividad(subActividad1);
						}
					}

					// guardar información de actividades financiadas
					if (subActividad1.getFinanciadoBancoEstado() != null
							&& subActividad1.getFinanciadoBancoEstado()) {
						// para las actividades que tienen solo la pregunta del
						// banco de SI y NO
						ciiu1.setValorOpcion(null);
						ciiu1.setSubActividad(null);
						ciiu1.setIdActividadFinanciadoBancoEstado(subActividad1
								.getId());
						ciiu1.setFinanciadoBancoDesarrollo(subActividad1
								.getValorOpcion());
					} else {
						// para las actividades que tienen 2 preguntas de SI y
						// NO E3821.01,E3821.01.01
						if (bancoEstado1.getId() != null
								&& bancoEstado1.getFinanciadoBancoEstado() != null
								&& bancoEstado1.getFinanciadoBancoEstado()) {
							ciiu1.setIdActividadFinanciadoBancoEstado(bancoEstado1
									.getId());
							ciiu1.setFinanciadoBancoDesarrollo(bancoEstado1
									.getValorOpcion());
						}
					}
				}
				if (ciiu1.getCatalogoCIUU().getTipoSector().getId() == 4) {
					ciiu1.setSubActividad(subActividad1);
				}
				
				if (ciiu1.getCatalogoCIUU().getRequiereViabilidadPngids()
						&& (ciiu1.getCatalogoCIUU().getCodigo()
								.compareTo("E3821.01") == 0 || ciiu1
								.getCatalogoCIUU().getCodigo()
								.compareTo("E3821.01.01") == 0)) {
					ciiu1.setSubActividad(subActividad1);
					ciiu1.setValorOpcion(subActividad1.getValorOpcion());
				}
			} else
				ciiu1.setSubActividad(null);

			// ------fin sub actividades----------------------
			// proyectoLicenciaCuaCiuuFacade.guardar(ciiu1);

			if (subActividadesCiiuBean.esActividadRecupercionMateriales(ciiu1
					.getCatalogoCIUU())) {
				if (ciiu1.getPrimario()) {
					ciiu1.setCombinacionSubActividades(subActividadesCiiuBean
							.getCombinacionSubActividadesProcesos());
				}
			}
		}
		if (wf2.getPuesto() != null) {
			ciiu2.setCatalogoCIUU(ciiuComplementaria1);
			ciiu2.setOrderJerarquia(wf2.getPuesto());
			ciiu2.setPrimario(false);
			// ciiu2.setProyectoLicenciaCoa(proyecto);
			if (wfCalculo.getPuesto() == wf2.getPuesto())
				ciiu2.setPrimario(true);
			// -------------sub actiidades--------------------
			if (listaProyectoCiiuSubActividad2.size() > 0) {

				for (ProyectoCoaCiuuSubActividades val : listaProyectoCiiuSubActividad2) {
					ProyectoCoaCiuuSubActividades obj = new ProyectoCoaCiuuSubActividades();
					obj.setEstado(true);
					obj.setProyectoLicenciaCuaCiuu(ciiu2);
					obj.setSubActividad(val.getSubActividad());
					obj.setRespuesta(val.getRespuesta());
				}
			} else if (subActividad2.getId() != null || parent2.getId() != null) {
				boolean doblePregunta = false;
				boolean mineriaLibreAprovechamiento = false;
				String actividadesDoblePregunta = Constantes
						.getActividadesDoblePregunta();
				String[] actividadesDoblePreguntaArray = actividadesDoblePregunta
						.split(",");
				SubActividades subcatalogo = subActividad2.getId() == null ? parent2
						: subActividad2;
				for (String actividad : actividadesDoblePreguntaArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						doblePregunta = true;
					}
				}
				if (doblePregunta) {
					if (parent2.getId() != null && !parent2.getValorOpcion()) {
						subcatalogo = parent2;
						subActividad2 = parent2;
					}
				} else {
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

				boolean cremacion = false;
				String actividadesCremacion = Constantes
						.getActividadesCremacion();
				String[] actividadesCremacionArray = actividadesCremacion
						.split(",");
				for (String actividad : actividadesCremacionArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						cremacion = true;
					}
				}
				boolean alcantarillado = false;
				String actividadAlcantarillado = Constantes
						.getActividadesZonaRural();
				String[] actividadesAlcantarilladoArray = actividadAlcantarillado
						.split(",");
				for (String actividad : actividadesAlcantarilladoArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						alcantarillado = true;
					}
				}
				boolean galapagos = false;
				String actividadGalapagos = Constantes
						.getActividadesGalapagos();
				String[] actividadesGalapagosArray = actividadGalapagos
						.split(",");
				for (String actividad : actividadesGalapagosArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						galapagos = true;
					}
				}
				if (ciiu2.getCatalogoCIUU().getTipoSector().getId() == 1) {
					ciiu2.setAlmacenaHidrocarburos(subActividad2
							.getValorOpcion());
					ciiu2.setValorOpcion(subActividad2.getValorOpcion());
					ciiu2.setSubActividad(subActividad2);
				}
				if (ciiu2.getCatalogoCIUU().getTipoSector().getId() == 2) {
					if (!mineriaLibreAprovechamiento)
						ciiu2.setMineriaArtesanal(subActividad2
								.getValorOpcion());

					ciiu2.setValorOpcion(subActividad2.getValorOpcion());
					ciiu2.setSubActividad(subActividad2);
				}
				if (ciiu2.getCatalogoCIUU().getTipoSector().getId() == 3) {
					ciiu2.setValorOpcion(subActividad2.getValorOpcion());
					ciiu2.setSubActividad(subActividad2);

					if (doblePregunta) {
						ciiu2.setPotabilizacionAgua(parent2.getValorOpcion());
						ciiu2.setValorOpcion(subActividad2.getValorOpcion());
						ciiu2.setSubActividad(subActividad2);
						ciiu2.setIdActividadFinanciadoBancoEstado(bancoEstado2
								.getId());
						ciiu2.setFinanciadoBancoDesarrollo(bancoEstado2
								.getValorOpcion());
						if (!parent2.getValorOpcion()) {
							ciiu2.setValorOpcion(parent2.getValorOpcion());
							ciiu2.setSubActividad(parent2);
						}
					}
					if (cremacion) {
						ciiu2.setRealizaCremacion(subActividad2
								.getValorOpcion());
						ciiu2.setValorOpcion(subActividad2.getValorOpcion());
						ciiu2.setSubActividad(subActividad2);
					}
					if (alcantarillado) {
						ciiu2.setPotabilizacionAgua(null);
						ciiu2.setFinanciadoBancoDesarrollo(null);
						ciiu2.setAlcantarilladoMunicipio(parent2
								.getValorOpcion());
						ciiu2.setValorOpcion(parent2.getValorOpcion());
						ciiu2.setSubActividad(parent2);
						if (!parent2.getValorOpcion()) {
							ciiu2.setSubActividad(subActividad2);
						}
					}
					if (galapagos) {
						ciiu2.setValorOpcion(parent2.getValorOpcion());
						ciiu2.setSubActividad(parent2);
						if (parent2.getValorOpcion()
								|| (!parent2.getValorOpcion() && parent2
										.getCatalogoCIUU().getCodigo()
										.compareTo("I5510.01") == 0)) {
							ciiu2.setSubActividad(subActividad2);
						}
					}

					// guardar información de actividades financiadas
					if (subActividad2.getFinanciadoBancoEstado() != null
							&& subActividad2.getFinanciadoBancoEstado()) {
						// para las actividades que tienen solo la pregunta del
						// banco de SI y NO
						ciiu2.setValorOpcion(null);
						ciiu2.setSubActividad(null);
						ciiu2.setIdActividadFinanciadoBancoEstado(subActividad2
								.getId());
						ciiu2.setFinanciadoBancoDesarrollo(subActividad2
								.getValorOpcion());
					} else {
						// para las actividades que tienen 2 preguntas de SI y
						// NO E3821.01,E3821.01.01
						if (bancoEstado2.getId() != null
								&& bancoEstado2.getFinanciadoBancoEstado() != null
								&& bancoEstado2.getFinanciadoBancoEstado()) {
							ciiu2.setIdActividadFinanciadoBancoEstado(bancoEstado2
									.getId());
							ciiu2.setFinanciadoBancoDesarrollo(bancoEstado2
									.getValorOpcion());
						}
					}
				}
				if (ciiu2.getCatalogoCIUU().getTipoSector().getId() == 4) {
					ciiu2.setSubActividad(subActividad2);
				}
				
				if (ciiu2.getCatalogoCIUU().getRequiereViabilidadPngids()
						&& (ciiu2.getCatalogoCIUU().getCodigo()
								.compareTo("E3821.01") == 0 || ciiu2
								.getCatalogoCIUU().getCodigo()
								.compareTo("E3821.01.01") == 0)) {
					ciiu2.setSubActividad(subActividad2);
					ciiu2.setValorOpcion(subActividad2.getValorOpcion());
				}
				
			} else
				ciiu2.setSubActividad(null);
			// ------fin sub actividades----------------------

			if (subActividadesCiiuBean.esActividadRecupercionMateriales(ciiu2
					.getCatalogoCIUU())) {
				if (ciiu2.getPrimario()) {
					ciiu2.setCombinacionSubActividades(subActividadesCiiuBean
							.getCombinacionSubActividadesProcesos());
				}
				subActividadesCiiuBean.guardarCiiuSubACtividades(ciiu2,
						listaSubActividades2);

			}
		}

		if (wf3.getPuesto() != null) {
			ciiu3.setCatalogoCIUU(ciiuComplementaria2);
			ciiu3.setOrderJerarquia(wf3.getPuesto());
			ciiu3.setPrimario(false);
			// ciiu3.setProyectoLicenciaCoa(proyecto);
			if (wfCalculo.getPuesto() == wf3.getPuesto())
				ciiu3.setPrimario(true);
			// -------------sub actiidades--------------------
			if (listaProyectoCiiuSubActividad3.size() > 0) {
				proyectoCoaCiuuSubActividadesFacade.eliminar(ciiu3);
				for (ProyectoCoaCiuuSubActividades val : listaProyectoCiiuSubActividad3) {
					ProyectoCoaCiuuSubActividades obj = new ProyectoCoaCiuuSubActividades();
					obj.setEstado(true);
					obj.setProyectoLicenciaCuaCiuu(ciiu3);
					obj.setSubActividad(val.getSubActividad());
					obj.setRespuesta(val.getRespuesta());
				}
			} else if (subActividad3.getId() != null || parent3.getId() != null) {
				boolean doblePregunta = false;
				boolean mineriaLibreAprovechamiento = false;
				String actividadesDoblePregunta = Constantes
						.getActividadesDoblePregunta();
				String[] actividadesDoblePreguntaArray = actividadesDoblePregunta
						.split(",");
				SubActividades subcatalogo = subActividad3.getId() == null ? parent3
						: subActividad3;
				for (String actividad : actividadesDoblePreguntaArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						doblePregunta = true;
					}
				}
				if (doblePregunta) {
					if (parent3.getId() != null && !parent3.getValorOpcion()) {
						subcatalogo = parent3;
						subActividad3 = parent3;
					}
				} else {
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

				boolean cremacion = false;
				String actividadesCremacion = Constantes
						.getActividadesCremacion();
				String[] actividadesCremacionArray = actividadesCremacion
						.split(",");
				for (String actividad : actividadesCremacionArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						cremacion = true;
					}
				}
				boolean alcantarillado = false;
				String actividadAlcantarillado = Constantes
						.getActividadesZonaRural();
				String[] actividadesAlcantarilladoArray = actividadAlcantarillado
						.split(",");
				for (String actividad : actividadesAlcantarilladoArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						alcantarillado = true;
					}
				}
				boolean galapagos = false;
				String actividadGalapagos = Constantes
						.getActividadesGalapagos();
				String[] actividadesGalapagosArray = actividadGalapagos
						.split(",");
				for (String actividad : actividadesGalapagosArray) {
					if (subcatalogo.getCatalogoCIUU().getCodigo()
							.equals(actividad)) {
						galapagos = true;
					}
				}

				if (ciiu3.getCatalogoCIUU().getTipoSector().getId() == 1) {
					ciiu3.setAlmacenaHidrocarburos(subActividad3
							.getValorOpcion());
					ciiu3.setValorOpcion(subActividad3.getValorOpcion());
					ciiu3.setSubActividad(subActividad3);
				}
				if (ciiu3.getCatalogoCIUU().getTipoSector().getId() == 2) {
					if (!mineriaLibreAprovechamiento)
						ciiu3.setMineriaArtesanal(subActividad3
								.getValorOpcion());

					ciiu3.setValorOpcion(subActividad3.getValorOpcion());
					ciiu3.setSubActividad(subActividad3);
				}
				if (ciiu3.getCatalogoCIUU().getTipoSector().getId() == 3) {
					ciiu3.setValorOpcion(subActividad3.getValorOpcion());
					ciiu3.setSubActividad(subActividad3);

					if (doblePregunta) {
						ciiu3.setPotabilizacionAgua(parent3.getValorOpcion());
						ciiu3.setValorOpcion(subActividad3.getValorOpcion());
						ciiu3.setSubActividad(subActividad3);
						ciiu3.setIdActividadFinanciadoBancoEstado(bancoEstado3
								.getId());
						ciiu3.setFinanciadoBancoDesarrollo(bancoEstado3
								.getValorOpcion());
						if (!parent3.getValorOpcion()) {
							ciiu3.setValorOpcion(parent3.getValorOpcion());
							ciiu3.setSubActividad(parent3);
						}
					}
					if (cremacion) {
						ciiu3.setRealizaCremacion(subActividad3
								.getValorOpcion());
						ciiu3.setValorOpcion(subActividad3.getValorOpcion());
						ciiu3.setSubActividad(subActividad3);
					}
					if (alcantarillado) {
						ciiu3.setPotabilizacionAgua(null);
						ciiu3.setFinanciadoBancoDesarrollo(null);
						ciiu3.setAlcantarilladoMunicipio(parent3
								.getValorOpcion());
						ciiu3.setValorOpcion(parent3.getValorOpcion());
						ciiu3.setSubActividad(parent3);
						if (!parent3.getValorOpcion()) {
							ciiu3.setSubActividad(subActividad3);
						}

					}
					if (galapagos) {
						ciiu3.setValorOpcion(parent3.getValorOpcion());
						ciiu3.setSubActividad(parent3);
						if (parent3.getValorOpcion()
								|| (!parent3.getValorOpcion() && parent3
										.getCatalogoCIUU().getCodigo()
										.compareTo("I5510.01") == 0)) {
							ciiu3.setSubActividad(subActividad3);
						}
					}

					// guardar información de actividades financiadas
					if (subActividad3.getFinanciadoBancoEstado() != null
							&& subActividad3.getFinanciadoBancoEstado()) {
						// para las actividades que tienen solo la pregunta del
						// banco de SI y NO
						ciiu3.setValorOpcion(null);
						ciiu3.setSubActividad(null);
						ciiu3.setIdActividadFinanciadoBancoEstado(subActividad3
								.getId());
						ciiu3.setFinanciadoBancoDesarrollo(subActividad3
								.getValorOpcion());
					} else {
						// para las actividades que tienen 2 preguntas de SI y
						// NO E3821.01,E3821.01.01
						if (bancoEstado3.getId() != null
								&& bancoEstado3.getFinanciadoBancoEstado() != null
								&& bancoEstado3.getFinanciadoBancoEstado()) {
							ciiu3.setIdActividadFinanciadoBancoEstado(bancoEstado3
									.getId());
							ciiu3.setFinanciadoBancoDesarrollo(bancoEstado3
									.getValorOpcion());
						}
					}
				}
				if (ciiu3.getCatalogoCIUU().getTipoSector().getId() == 4) {
					ciiu3.setSubActividad(subActividad3);
				}
				
				if (ciiu3.getCatalogoCIUU().getRequiereViabilidadPngids()
						&& (ciiu3.getCatalogoCIUU().getCodigo()
								.compareTo("E3821.01") == 0 || ciiu3
								.getCatalogoCIUU().getCodigo()
								.compareTo("E3821.01.01") == 0)) {
					ciiu3.setSubActividad(subActividad3);
				}
				
			} else
				ciiu3.setSubActividad(null);
			// ------fin sub actividades----------------------
			if (subActividadesCiiuBean.esActividadRecupercionMateriales(ciiu3
					.getCatalogoCIUU())) {
				if (ciiu3.getPrimario()) {
					ciiu3.setCombinacionSubActividades(subActividadesCiiuBean
							.getCombinacionSubActividadesProcesos());
				}
				subActividadesCiiuBean.guardarCiiuSubACtividades(ciiu3,
						listaSubActividades3);
			}
		}
		// // }
		// if(ciiu1.getGenetico()!=null && ciiu1.getGenetico() &&
		// documentoGeneticoCiiu1.getContenidoDocumento()!=null)
		// guardarDocGeneticCiiu1();
		// if(ciiu2.getGenetico()!=null && ciiu2.getGenetico() &&
		// documentoGeneticoCiiu2.getContenidoDocumento()!=null)
		// guardarDocGeneticCiiu2();
		// if(ciiu3.getGenetico()!=null && ciiu3.getGenetico() &&
		// documentoGeneticoCiiu3.getContenidoDocumento()!=null)
		// guardarDocGeneticCiiu3();

		// guardar sustancias quimicas------------------------------
		GestionarProductosQuimicosProyectoAmbiental sus = new GestionarProductosQuimicosProyectoAmbiental();
		estadoSustanciasQuimicas = false;
		for (SustanciaQuimicaPeligrosa sustancias : sustanciaQuimicaSeleccionada) {
			sus = new GestionarProductosQuimicosProyectoAmbiental();
			// sus.setProyectoLicenciaCoa(proyecto);
			sus.setSustanciaquimica(sustancias);
			sus.setControlSustancia(false);
			sus.setGestionaSustancia(true);
			sus.setTransportaSustancia(false);
			if (sustancias.getControlSustancia() != null
					&& sustancias.getControlSustancia()) {
				sus.setControlSustancia(true);
				estadoSustanciasQuimicas = true;
			}
		}
		
		for (SustanciaQuimicaPeligrosa sustanciast : sustanciaQuimicaSeleccionadaTransporta) {

			if (!sustanciaQuimicaSeleccionada.contains(sustanciast)) {
				sus = new GestionarProductosQuimicosProyectoAmbiental();
				//sus.setProyectoLicenciaCoa(proyecto);
				sus.setSustanciaquimica(sustanciast);
				sus.setControlSustancia(false);
				sus.setGestionaSustancia(false);
				sus.setTransportaSustancia(true);
				if (sustanciast.getControlSustancia() != null
						&& sustanciast.getControlSustancia()) {
					sus.setControlSustancia(true);
					estadoSustanciasQuimicas = true;
				}
			} else {
				//GestionarProductosQuimicosProyectoAmbiental sust = gestionarProductosQuimicosProyectoAmbientalFacade.getSustanciaById(proyecto, sustanciast.getId());
				//sust.setTransportaSustancia(true);
				//gestionarProductosQuimicosProyectoAmbientalFacade.guardar(sust);
			}

		}
		// guardar otras sustancias que no estan en el catalogo
		for (SustanciaQuimicaPeligrosa sustanciasOtros : listaSustanciaQuimicaSeleccionadaOtros) {
			sus = new GestionarProductosQuimicosProyectoAmbiental();
			//sus.setProyectoLicenciaCoa(proyecto);
			sus.setSustanciaquimica(sustanciasOtros);
			sus.setOtraSustancia(sustanciasOtros.getDescripcion());
			sus.setControlSustancia(false);
			sus.setGestionaSustancia(true);
			sus.setTransportaSustancia(false);
		}

		for (SustanciaQuimicaPeligrosa sustanciasOtrosT : listaSustanciaQuimicaSeleccionadaOtrosTransporta) {

			sus = new GestionarProductosQuimicosProyectoAmbiental();
			//sus.setProyectoLicenciaCoa(proyecto);
			sus.setSustanciaquimica(sustanciasOtrosT);
			sus.setOtraSustancia(sustanciasOtrosT.getDescripcion().trim());
			sus.setControlSustancia(false);
			sus.setGestionaSustancia(false);
			sus.setTransportaSustancia(true);
		}

		// guardar rango de magnitud-------------------------------
		CriterioMagnitud criterio = new CriterioMagnitud();
		ValorCalculo calculo = new ValorCalculo();
		if (estadoMagnitud) {
			if (valorMagnitud1.getId() != null) {
				criterio = new CriterioMagnitud();
				// criterio.setProyectoLicenciaCoa(proyecto);
				criterio.setVariableCriterio(valorMagnitud1
						.getCriterioMagnitud().getId());
				criterio.setValorMagnitud(valorMagnitud1);
				criterio.setRango(valorMagnitud1.getValorVariable());
				criterio.setRangoDescripcion(valorMagnitud1.getRango());
				criterio.setValor(valorMagnitud1.getValor());
				criterio.setPrioridad(false);
				if (valorMagnitud1.equals(valorMagnitudCalculo)) {
					calculo = new ValorCalculo();
					// calculo.setProyectoLicenciaCoa(proyecto);
					calculo.setCatalogoCIUU(wfCalculo.getCatalago());
					calculo.setCriterioMagnitud(criterio);
					calculo.setCodigo(wfCalculo.getNivel5());
					calculo.setNivel(5);
					calculo.setValorQuimico(wfCalculo.getCatalago()
							.getCriterioQuimico());
					calculo.setValorAmbiental(wfCalculo.getCatalago()
							.getCriterioAmbiental());
					calculo.setValorConcentracion(wfCalculo.getCatalago()
							.getCriterioConcentracion());
					calculo.setValorRiesgo(wfCalculo.getCatalago()
							.getCriterioRiesgo());
					calculo.setValorSocial(wfCalculo.getCatalago()
							.getCriterioSocial());
					calculo.setValorMagnitud(valorMagnitud1.getValorVariable());
					calculo.setValorCapacidadSocial(valorCapacidadSocial);
					calculo.setValorCapacidadBiofisica(valorCapacidadBiofisica);
					calculo.setValorImportanciaRelativa(wfCalculo
							.getImportancia());
					calculo.setValorResultadoFormula(Integer
							.valueOf(valCategoria));
					calculo.setCategorizacionAlternativa(Integer
							.valueOf(valCategoria));
					calculo.setRango(valorMagnitud1.getValorVariable());
					calculo.setValor(valorMagnitud1.getValor());
					criterio.setPrioridad(true);
				}
			}
			if (valorMagnitud2.getId() != null) {
				criterio = new CriterioMagnitud();
				// criterio.setProyectoLicenciaCoa(proyecto);
				criterio.setVariableCriterio(valorMagnitud2
						.getCriterioMagnitud().getId());
				criterio.setValorMagnitud(valorMagnitud2);
				criterio.setRango(valorMagnitud2.getValorVariable());
				criterio.setRangoDescripcion(valorMagnitud2.getRango());
				criterio.setValor(valorMagnitud2.getValor());
				criterio.setPrioridad(false);

				if (valorMagnitud2.equals(valorMagnitudCalculo)) {
					calculo = new ValorCalculo();
					// calculo.setProyectoLicenciaCoa(proyecto);
					calculo.setCatalogoCIUU(wfCalculo.getCatalago());
					calculo.setCriterioMagnitud(criterio);
					calculo.setCodigo(wfCalculo.getNivel5());
					calculo.setNivel(5);
					calculo.setValorQuimico(wfCalculo.getCatalago()
							.getCriterioQuimico());
					calculo.setValorAmbiental(wfCalculo.getCatalago()
							.getCriterioAmbiental());
					calculo.setValorConcentracion(wfCalculo.getCatalago()
							.getCriterioConcentracion());
					calculo.setValorRiesgo(wfCalculo.getCatalago()
							.getCriterioRiesgo());
					calculo.setValorSocial(wfCalculo.getCatalago()
							.getCriterioSocial());
					calculo.setValorMagnitud(valorMagnitud2.getValorVariable());
					calculo.setValorCapacidadSocial(valorCapacidadSocial);
					calculo.setValorCapacidadBiofisica(valorCapacidadBiofisica);
					calculo.setValorImportanciaRelativa(wfCalculo
							.getImportancia());
					calculo.setValorResultadoFormula(Integer
							.valueOf(valCategoria));
					calculo.setCategorizacionAlternativa(Integer
							.valueOf(valCategoria));
					calculo.setRango(valorMagnitud2.getValorVariable());
					calculo.setValor(valorMagnitud2.getValor());
					criterio.setPrioridad(true);

				}
			}
			if (valorMagnitud3.getId() != null) {
				criterio = new CriterioMagnitud();
				// criterio.setProyectoLicenciaCoa(proyecto);
				criterio.setVariableCriterio(valorMagnitud3
						.getCriterioMagnitud().getId());
				criterio.setValorMagnitud(valorMagnitud3);
				criterio.setRango(valorMagnitud3.getValorVariable());
				criterio.setRangoDescripcion(valorMagnitud3.getRango());
				criterio.setValor(valorMagnitud3.getValor());
				criterio.setPrioridad(false);
				if (valorMagnitud3.equals(valorMagnitudCalculo)) {
					calculo = new ValorCalculo();
					// calculo.setProyectoLicenciaCoa(proyecto);
					calculo.setCatalogoCIUU(wfCalculo.getCatalago());
					calculo.setCriterioMagnitud(criterio);
					calculo.setCodigo(wfCalculo.getNivel5());
					calculo.setNivel(5);
					calculo.setValorQuimico(wfCalculo.getCatalago()
							.getCriterioQuimico());
					calculo.setValorAmbiental(wfCalculo.getCatalago()
							.getCriterioAmbiental());
					calculo.setValorConcentracion(wfCalculo.getCatalago()
							.getCriterioConcentracion());
					calculo.setValorRiesgo(wfCalculo.getCatalago()
							.getCriterioRiesgo());
					calculo.setValorSocial(wfCalculo.getCatalago()
							.getCriterioSocial());
					calculo.setValorMagnitud(valorMagnitud3.getValorVariable());
					calculo.setValorCapacidadSocial(valorCapacidadSocial);
					calculo.setValorCapacidadBiofisica(valorCapacidadBiofisica);
					calculo.setValorImportanciaRelativa(wfCalculo
							.getImportancia());
					calculo.setValorResultadoFormula(Integer
							.valueOf(valCategoria));
					calculo.setCategorizacionAlternativa(Integer
							.valueOf(valCategoria));
					calculo.setRango(valorMagnitud3.getValorVariable());
					calculo.setValor(valorMagnitud3.getValor());
					criterio.setPrioridad(true);

				}
			}
			estadoMagnitud = false;
			
			valoresFormula = calculo;
		}

		nombreSector = ciiu1.getCatalogoCIUU().getTipoSector().getNombre();

		// guardar capas de interseccion y detalle de interseccion
		if (uploadedFileImpl != null) {
			// interseccionProyectoLicenciaAmbientalFacade.eliminar(proyecto,
			// JsfUtil.getLoggedUser().getNombre());
			for (SimuladorInterseccionProyectoLicenciaAmbiental i : capasIntersecciones
					.keySet()) {
				i.setProyectoLicenciaCoa(proyecto);
				i.setFechaproceso(new Date());
				i = interseccionProyectoLicenciaAmbientalFacade.guardar(i);
				// for(DetalleInterseccionProyectoAmbiental j :
				// capasIntersecciones.get(i))
				// {
				// j.setInterseccionProyectoLicenciaAmbiental(i);
				// detalleInterseccionProyectoAmbientalFacade.guardar(j);
				// }
			}
		}

		intersecciones = interseccionProyectoLicenciaAmbientalFacade
				.intersecan(proyecto);
		for (SimuladorInterseccionProyectoLicenciaAmbiental inter : intersecciones) {
			if (inter.getCapa().getId() == 3)// snap
				proyecto.setInterecaSnap(true);
			if (inter.getCapa().getId() == 1)// bosques protectores
				proyecto.setInterecaBosqueProtector(true);
			if (inter.getCapa().getId() == 8)// patrimonio forestal nacinal
				proyecto.setInterecaPatrimonioForestal(true);
			if (inter.getCapa().getId() == 11)// ramsar
				proyecto.setInterecaRamsar(true);
		}

		for (Bloque bloques : bloquesBean.getBloquesSeleccionados()) {
			ProyectoLicenciaAmbientalCoaCiuuBloques obj = new ProyectoLicenciaAmbientalCoaCiuuBloques();
			obj.setBloque(bloques);
			obj.setProyectoLicenciaCuaCiuu(ciiuArearesponsable);
		}

		areaResponsable();
		proyectoLicenciaCoaFacade.guardar(proyecto);

		generarMapa();
		responsable = proyecto.getAreaResponsable().getAreaName();

		RequestContext.getCurrentInstance().update(
				"frmPreliminar:certificadoIntercepcionRcoa");
		RequestContext.getCurrentInstance().update(
				"frmPreliminar:procesoDialog");
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('certificadoIntercepcionRcoa').show();");
	}

	public String getArmarDiagrama() {
		StringBuilder lista = new StringBuilder("0,");
		try {
			// interseca
			if ((proyecto.getInterecaBosqueProtector() != null && proyecto
					.getInterecaBosqueProtector())
					|| (proyecto.getInterecaPatrimonioForestal() != null && proyecto
							.getInterecaPatrimonioForestal())
					|| (proyecto.getInterecaSnap() != null && proyecto
							.getInterecaSnap())) {
				lista.append("1");
			}
			// genera desechos
			if (proyecto.getGeneraDesechos() != null
					&& proyecto.getGeneraDesechos()) {
				lista.append(agregarItem(lista.toString(), "2"));

			}
			// remosio o covertura vegental
			if (proyecto.getRenocionCobertura() != null
					&& proyecto.getRenocionCobertura()
					&& proyecto.getCategorizacion() <= 2) {
				lista.append(agregarItem(lista.toString(), "3"));
			}

			// desechos solidos no peligrosos
			if (proyecto.getGeneraDesechos() != null
					&& !proyecto.getGeneraDesechos()) {
				lista.append(agregarItem(lista.toString(), "4"));
			}

			// gestiona residuos o desechos
			if (proyecto.getGestionDesechos() != null
					&& proyecto.getGestionDesechos()) {
				lista.append(agregarItem(lista.toString(), "5"));
			}

			// emplea sustancias Quimicas
			if (proyecto.getSustanciasQuimicas() != null
					&& proyecto.getSustanciasQuimicas()) {
				lista.append(agregarItem(lista.toString(), "6"));
			}
			if (proyecto.getCategorizacion() == 1) {
				lista.append(agregarItem(lista.toString(), "7"));
			} else if (proyecto.getCategorizacion() == 2) {
				lista.append(agregarItem(lista.toString(), "8"));
			} else {
				lista.append(agregarItem(lista.toString(), "9"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
		armarDiagrama = lista.toString();
		return armarDiagrama;
	}

	private String agregarItem(String valor, String siguiente) {
		String continuar = valor.substring(valor.length() - 1);
		String cadena = "";
		if (continuar.equals(",")) {
			cadena = siguiente;
		} else {
			cadena = "," + siguiente;
		}
		return cadena;
	}

	public void areaResponsable() {
		Area enteAcreditadoProyecto = null;
		enteAcreditadoProyecto = areaService.getAreaEnteAcreditado(3, loginBean
				.getUsuario().getNombre());

		boolean areaMar = false;
		try {
			Integer.valueOf(ubicacionPrincipal.getCodificacionInec());
			areaMar = false;
		} catch (Exception e) {
			areaMar = true;
		}

		UbicacionesGeografica provincia = ubicacionPrincipal
				.getUbicacionesGeografica().getUbicacionesGeografica();
		UbicacionesGeografica canton = ubicacionPrincipal
				.getUbicacionesGeografica();

		ArrayList<Integer> listaProv = new ArrayList<Integer>();
		ArrayList<Integer> listaCant = new ArrayList<Integer>();
		Integer totalProvinciasDiferentes = 0, totalCantonesDiferentes = 0;
		for (UbicacionesGeografica ubi : ubicacionesSeleccionadas) {
			listaProv.add(ubi.getUbicacionesGeografica()
					.getUbicacionesGeografica() == null ? 0 : ubi
					.getUbicacionesGeografica().getUbicacionesGeografica()
					.getId());
			listaCant.add(ubi.getUbicacionesGeografica().getId());
		}
		Set<Integer> proSet = new HashSet<Integer>(listaProv);
		for (int pro : proSet) {
			totalProvinciasDiferentes++;
		}

		Set<Integer> cantSet = new HashSet<Integer>(listaCant);
		for (int cant : cantSet) {
			totalCantonesDiferentes++;
		}

		List<ProyectoCoaCiuuSubActividades> cargarSubActividades = new ArrayList<>();
		
		if (ciiu1.getCatalogoCIUU() != null
				&& ciiu1.getCatalogoCIUU().getId() != null
				&& ciiu1.getCatalogoCIUU().getId()
						.equals(actividadPrimaria.getId())) {
			ciiuArearesponsable = ciiu1;
			cargarSubActividades = listaProyectoCiiuSubActividad1;
		} else if (ciiu2.getCatalogoCIUU() != null
				&& ciiu2.getCatalogoCIUU().getId() != null
				&& ciiu2.getCatalogoCIUU().getId()
						.equals(actividadPrimaria.getId())) {
			ciiuArearesponsable = ciiu2;
			cargarSubActividades = listaProyectoCiiuSubActividad2;
		} else if (ciiu3.getCatalogoCIUU() != null
				&& ciiu3.getCatalogoCIUU().getId() != null
				&& ciiu3.getCatalogoCIUU().getId()
						.equals(actividadPrimaria.getId())) {
			ciiuArearesponsable = ciiu3;
			cargarSubActividades = listaProyectoCiiuSubActividad3;
		}
		// proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);

		// ValorCalculo valoresFormula =
		// valorCalculoFacade.formulaProyecto(proyecto);
		categoriaXNormativa = 0;
		if (subActividadesCiiuBean
				.esActividadRecupercionMateriales(ciiuArearesponsable
						.getCatalogoCIUU())) {
			if (subActividadesCiiuBean.getCombinacionSubActividadesTipoEnte() != null) {
				categoriaXNormativa = Integer.valueOf(subActividadesCiiuBean
						.getCombinacionSubActividadesTipoEnte()
						.getTipoPermiso());
				tipoAutoridadAmbiental = Integer.valueOf(subActividadesCiiuBean
						.getCombinacionSubActividadesTipoEnte()
						.getEntidadProceso());
			}
		} else {
			if (cargarSubActividades.size() > 0
					&& (cargarSubActividades.get(0).getSubActividad()
							.getTipoPregunta().intValue() == 7 || cargarSubActividades
							.get(0).getSubActividad().getTipoPregunta()
							.intValue() == 8)) {
				if (ciiuArearesponsable.getSubActividad() != null
						&& ciiuArearesponsable.getCombinacionSubActividades() == null) {
					for (ProyectoCoaCiuuSubActividades lista : cargarSubActividades) {
						if (lista.getRespuesta() != null
								&& !lista.getRespuesta()) {
							categoriaXNormativa = lista.getSubActividad()
									.getOpcionPermisoNo();
							tipoAutoridadAmbiental = lista.getSubActividad()
									.getEntidadProcesoNo() == null ? 3 : lista
									.getSubActividad().getEntidadProcesoNo();
						} else if (lista.getRespuesta() == null) {
							categoriaXNormativa = Integer.valueOf(lista
									.getSubActividad().getTipoPermiso());
							tipoAutoridadAmbiental = Integer
									.valueOf(lista.getSubActividad()
											.getEntidadProceso() == null ? "3"
											: lista.getSubActividad()
													.getEntidadProceso());
						}
					}

				} else {
					categoriaXNormativa = Integer.valueOf(ciiuArearesponsable
							.getCombinacionSubActividades().getTipoPermiso());
					tipoAutoridadAmbiental = Integer
							.valueOf(ciiuArearesponsable
									.getCombinacionSubActividades()
									.getEntidadProceso() == null ? "3"
									: ciiuArearesponsable
											.getCombinacionSubActividades()
											.getEntidadProceso());
				}
			} else if (ciiuArearesponsable.getSubActividad() != null) {
				if (ciiuArearesponsable.getFinanciadoBancoDesarrollo() != null
						&& ciiuArearesponsable.getPotabilizacionAgua() != null) {
					SubActividades bancoEstado = subActividadesFacade
							.actividadParent(ciiuArearesponsable
									.getIdActividadFinanciadoBancoEstado());
					if (ciiuArearesponsable.getFinanciadoBancoDesarrollo())
						tipoAutoridadAmbiental = bancoEstado
								.getEntidadProcesoSi() == null ? 3
								: bancoEstado.getEntidadProcesoSi();
					else
						tipoAutoridadAmbiental = bancoEstado
								.getEntidadProcesoNo() == null ? 3
								: bancoEstado.getEntidadProcesoNo();

					if (ciiuArearesponsable.getSubActividad().getEsMultiple()) {
						categoriaXNormativa = Integer
								.valueOf(ciiuArearesponsable.getSubActividad()
										.getTipoPermiso());
					} else {
						if (ciiuArearesponsable.getValorOpcion()) {
							categoriaXNormativa = ciiuArearesponsable
									.getSubActividad().getOpcionPermisoSi();
						} else {
							categoriaXNormativa = ciiuArearesponsable
									.getSubActividad().getOpcionPermisoNo();
						}
					}
				} else if (ciiuArearesponsable
						.getIdActividadFinanciadoBancoEstado() != null) {
					// actividades con 2 preguntas una de ellas BDE
					SubActividades subActividadBancoEstado = subActividadesFacade
							.actividadParent(ciiuArearesponsable
									.getIdActividadFinanciadoBancoEstado());
					subActividadBancoEstado.setValorOpcion(ciiuArearesponsable
							.getFinanciadoBancoDesarrollo());
					if (subActividadesCiiuBean
							.esActividadOperacionRellenosSanitarios(ciiuArearesponsable
									.getCatalogoCIUU())) {
						tipoAutoridadAmbiental = Integer
								.valueOf(ciiuArearesponsable.getSubActividad()
										.getEntidadProceso()); // no se toma los valores de la pregunta BDE porque es solo informativa
					} else {
						if (ciiuArearesponsable.getFinanciadoBancoDesarrollo()) {
							tipoAutoridadAmbiental = subActividadBancoEstado
									.getEntidadProcesoSi() == null ? 2
									: subActividadBancoEstado
											.getEntidadProcesoSi();
						} else {
							tipoAutoridadAmbiental = subActividadBancoEstado
									.getEntidadProcesoNo() == null ? 2
									: subActividadBancoEstado
											.getEntidadProcesoNo();
						}
					}

					if (subActividadBancoEstado.getOpcionPermisoSi() != null) {
						if (ciiuArearesponsable.getFinanciadoBancoDesarrollo()) {
							categoriaXNormativa = subActividadBancoEstado
									.getOpcionPermisoSi();
						} else {
							categoriaXNormativa = subActividadBancoEstado
									.getOpcionPermisoNo();
						}
					} else {
						if (ciiuArearesponsable.getSubActividad()
								.getEsMultiple()) {
							categoriaXNormativa = Integer
									.valueOf(ciiuArearesponsable
											.getSubActividad().getTipoPermiso());
						} else {
							if (ciiuArearesponsable.getValorOpcion()) {
								categoriaXNormativa = ciiuArearesponsable
										.getSubActividad().getOpcionPermisoSi();
							} else {
								categoriaXNormativa = ciiuArearesponsable
										.getSubActividad().getOpcionPermisoNo();
							}
						}
					}
				} else if (ciiuArearesponsable.getSubActividad().getEsMultiple()) {
					categoriaXNormativa = Integer.valueOf(ciiuArearesponsable
							.getSubActividad().getTipoPermiso());
					tipoAutoridadAmbiental = Integer
							.valueOf(ciiuArearesponsable.getSubActividad()
									.getEntidadProceso() == null ? "3"
									: ciiuArearesponsable.getSubActividad()
											.getEntidadProceso());
				} else {
					if (ciiuArearesponsable.getValorOpcion() == null) {
						categoriaXNormativa = ciiuArearesponsable
								.getSubActividad().getOpcionPermisoSi();
						tipoAutoridadAmbiental = ciiuArearesponsable
								.getSubActividad().getEntidadProcesoSi() == null ? 2
								: ciiuArearesponsable.getSubActividad()
										.getEntidadProcesoSi();

					} else if (ciiuArearesponsable.getValorOpcion()) {
						categoriaXNormativa = ciiuArearesponsable
								.getSubActividad().getOpcionPermisoSi();
						tipoAutoridadAmbiental = ciiuArearesponsable
								.getSubActividad().getEntidadProcesoSi() == null ? 3
								: ciiuArearesponsable.getSubActividad()
										.getEntidadProcesoSi();
					} else {
						categoriaXNormativa = ciiuArearesponsable
								.getSubActividad().getOpcionPermisoNo();
						tipoAutoridadAmbiental = ciiuArearesponsable
								.getSubActividad().getEntidadProcesoNo() == null ? 3
								: ciiuArearesponsable.getSubActividad()
										.getEntidadProcesoNo();

					}
				}
			} else {
				if (ciiuArearesponsable.getIdActividadFinanciadoBancoEstado() != null
						&& ciiuArearesponsable.getCatalogoCIUU()
								.getTipoAreaActividadPrincipal() == null) {
					SubActividades subActividadBancoEstado = subActividadesFacade
							.actividadParent(ciiuArearesponsable
									.getIdActividadFinanciadoBancoEstado());
					subActividadBancoEstado.setValorOpcion(ciiuArearesponsable
							.getFinanciadoBancoDesarrollo());

					if (ciiuArearesponsable.getFinanciadoBancoDesarrollo()) {
						categoriaXNormativa = subActividadBancoEstado
								.getOpcionPermisoSi();
						tipoAutoridadAmbiental = subActividadBancoEstado
								.getEntidadProcesoSi() == null ? 2
								: subActividadBancoEstado.getEntidadProcesoSi();
					} else {
						categoriaXNormativa = subActividadBancoEstado
								.getOpcionPermisoNo();
						tipoAutoridadAmbiental = subActividadBancoEstado
								.getEntidadProcesoNo() == null ? 2
								: subActividadBancoEstado.getEntidadProcesoNo();
					}

				} else {
					if (ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso() != null
							&& ciiuArearesponsable.getCatalogoCIUU()
									.getTipoPermiso()
									.equals("CERTIFICADO AMBIENTAL")) {
						categoriaXNormativa = 1;
						tipoAutoridadAmbiental = ciiuArearesponsable
								.getCatalogoCIUU().getTipoAutoridadAmbiental();
					}
					if (ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso() != null
							&& ciiuArearesponsable.getCatalogoCIUU()
									.getTipoPermiso()
									.equals("REGISTRO AMBIENTAL")) {
						categoriaXNormativa = 2;
						tipoAutoridadAmbiental = ciiuArearesponsable
								.getCatalogoCIUU().getTipoAutoridadAmbiental();
					}
					if (ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso() != null
							&& ciiuArearesponsable.getCatalogoCIUU()
									.getTipoPermiso()
									.equals("LICENCIA AMBIENTAL")) {
						categoriaXNormativa = 3;
						tipoAutoridadAmbiental = ciiuArearesponsable
								.getCatalogoCIUU().getTipoAutoridadAmbiental();
					}
					if ((valoresFormula.getCatalogoCIUU().getTipoImpacto() != null && valoresFormula
							.getCatalogoCIUU().getTipoImpacto()
							.equals("IMPACTO ALTO"))
							|| (ciiuArearesponsable.getCatalogoCIUU()
									.getTipoImpacto() != null && ciiuArearesponsable
									.getCatalogoCIUU().getTipoImpacto()
									.equals("IMPACTO ALTO"))) {
						categoriaXNormativa = 4;
						tipoAutoridadAmbiental = ciiuArearesponsable
								.getCatalogoCIUU().getTipoAutoridadAmbiental();
					}
				}

			}
		}
		
		List<ProyectoLicenciaCuaCiuu> listaActividades = new ArrayList<>();
		if (ciiu1.getCatalogoCIUU() != null
				&& ciiu1.getCatalogoCIUU().getId() != null) {
			listaActividades.add(ciiu1);
		} 
		if (ciiu2.getCatalogoCIUU() != null
				&& ciiu2.getCatalogoCIUU().getId() != null) {
			listaActividades.add(ciiu2);
		} 
		if (ciiu3.getCatalogoCIUU() != null
				&& ciiu3.getCatalogoCIUU().getId() != null) {
			listaActividades.add(ciiu3);
		}

		if(listaActividades.size() > 1) {
			CatalogoCIUU catalogoPrincipal = listaActividades.get(0).getCatalogoCIUU();
			if (catalogoPrincipal.getTipoAreaActividadPrincipal() != null) {
				tipoAutoridadAmbiental = catalogoPrincipal
						.getTipoAreaActividadPrincipal(); //actividades E3821.02, E3821.02.01
			} else if (ciiuArearesponsable
					.getIdActividadFinanciadoBancoEstado() == null) {
				//Cuando el proyecto tiene más de una actividad económica y cualquiera de las actividades seleccionadas 
				//es financiada por el Banco del Estado, el proyecto se direcciona a Dirección Zonal
				boolean existeFinanciamientoBde = false;
				for (ProyectoLicenciaCuaCiuu b : listaActividades) {
					if (b.getFinanciadoBancoDesarrollo() != null
							&& b.getFinanciadoBancoDesarrollo()
							&& b.getCatalogoCIUU()
									.getTipoAreaActividadPrincipal() == null) {  //cuando se selecciona financiado para las actividades E3821.02, E3821.02.01 no aplica el cambio de autoridad
						existeFinanciamientoBde = true;
						break;
					}
				}

				if (existeFinanciamientoBde) {
					tipoAutoridadAmbiental = 2; // 2='MAE ZONAL'
				}
			}
		}

		// cuando el proyecto involucra a más de 2 zonales
		if (totalProvinciasDiferentes > 1) {
			ArrayList<Integer> listaZonales = new ArrayList<Integer>();
			try {
				for (int id : proSet) {
					UbicacionesGeografica ubicacion = ubicacionfacade
							.buscarPorId(id);
					if (ubicacion.getAreaCoordinacionZonal() != null)
						listaZonales.add(ubicacion.getAreaCoordinacionZonal()
								.getId());
					else if (ubicacion.getCodificacionInec().equals(
							Constantes.CODIGO_INEC_GALAPAGOS))
						listaZonales
								.add(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
				}

				Set<Integer> setZonales = new HashSet<Integer>(listaZonales);

				if (setZonales.size() > 2) {
					if (proyecto.getTransportaSustanciasQuimicas()
							&& subActividadesCiiuBean
									.esActividadExcepcionTransporteSustanciasQuimicas(ciiuArearesponsable
											.getCatalogoCIUU())) {
						// si el proyecto realiza transporte de sustancias y es
						// H4923.01, H4923.01.01, H5011.01, H5011.01.01,
						// H5120.01, H5120.01.01
						// la asignación se realiza normal
					} else {
						if (categoriaXNormativa > 0) {
							proyecto.setCategorizacion(categoriaXNormativa);
							// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
							// valorCalculoFacade.guardar(valoresFormula);
						}
						proyecto.setAreaResponsable(areaService
								.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
						proyecto.setAreaInventarioForestal(proyecto
								.getAreaResponsable());
						proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}

			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}

		if (provincia.getNombre().equals("GALAPAGOS")) {
			if (categoriaXNormativa > 0) {
				proyecto.setCategorizacion(categoriaXNormativa);
				// .setCategorizacionAlternativa(categoriaXNormativa);
				// valorCalculoFacade.guardar(valoresFormula);
			}
			proyecto.setAreaResponsable(areaService
					.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS));
			proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
			// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		}
		if (areaMar && tipoAutoridadAmbiental == 1) {
			if (categoriaXNormativa > 0) {
				proyecto.setCategorizacion(categoriaXNormativa);
				// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				// valorCalculoFacade.guardar(valoresFormula);
			}
			proyecto.setAreaResponsable(areaService
					.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
			proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
			// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		}
		
		// validar si el proyecto interseca con mar para establecer el área pendiente de asignación
		areaPendienteAsignar = false;
		for (UbicacionesGeografica ubi : ubicacionesSeleccionadas) {
			if (ubi.getIdRegion() != null && ubi.getIdRegion().equals(6)
					&& !ubi.getNombre().toUpperCase().contains("INSULAR")) {
				areaPendienteAsignar = true;

				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					//valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					//valorCalculoFacade.guardar(valoresFormula);
				}

				proyecto.setAreaResponsable(areaFacade.getAreaPorAreaAbreviacion("ND"));
				proyecto.setAreaInventarioForestal(null);
				//proyecto.setEstadoAsignacionArea(2);
				//proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);

				return;
			}
		}
				
		if (areaMar && tipoAutoridadAmbiental > 1) {
			if (categoriaXNormativa > 0) {
				proyecto.setCategorizacion(categoriaXNormativa);
				// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				// valorCalculoFacade.guardar(valoresFormula);
			}
			if (ubicacionPrincipal.getNombre().toUpperCase()
					.contains("INSULAR")) // si es mar insular
				proyecto.setAreaResponsable(areaService
						.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS));
			else
				proyecto.setAreaResponsable(areaService
						.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));

			proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
			proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		}

		Area zonal = areaService
				.getAreaCoordinacionZonal(ubicacionOficinaTecnica);
		// cambio de las consideraciones 09-11-20020
		// Boolean esActividadZonaRural = false;
		// String actividadZonaRural= Constantes.getActividadesZonaRural();
		// String[] actividadesZonaRuralArray = actividadZonaRural.split(",");
		//
		// for (String actividadRural : actividadesZonaRuralArray) {
		// if
		// (ciiuArearesponsable.getCatalogoCIUU().getCodigo().equals(actividadRural)){
		// esActividadZonaRural = true;
		// if(proyecto.getTipoPoblacion().getId()==TipoPoblacion.TIPO_POBLACION_RURAL)
		// categoriaXNormativa=1;
		// else
		// categoriaXNormativa=2;
		// break;
		// }
		// }

		if (totalProvinciasDiferentes == 2
				&& tipoAutoridadAmbiental != 1
				&& !(proyecto.getInterecaSnap()
						|| proyecto.getInterecaBosqueProtector()
						|| proyecto.getInterecaPatrimonioForestal() || proyecto
							.getInterecaRamsar())) {
			proyecto.setAreaResponsable(zonal);
			proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
			if (categoriaXNormativa > 0) {
				proyecto.setCategorizacion(categoriaXNormativa);
				// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				// valorCalculoFacade.guardar(valoresFormula);
			}
			proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		}

		if (totalProvinciasDiferentes > 2 && tipoAutoridadAmbiental != 1) {
			proyecto.setAreaResponsable(zonal);
			proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
			if (categoriaXNormativa > 0) {
				proyecto.setCategorizacion(categoriaXNormativa);
				// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				// valorCalculoFacade.guardar(valoresFormula);
			}
			proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		}

		esActividadExtractiva = ciiuArearesponsable.getCatalogoCIUU()
				.getBloques();

		if (proyecto.getInterecaSnap() || proyecto.getInterecaBosqueProtector()
				|| proyecto.getInterecaPatrimonioForestal()
				|| proyecto.getInterecaRamsar()) {
			// Validar categorizacion por interseccion con capas especificas. Se
			// hace el llamado a la función porque se puede requerir el cambio
			// de categorizacion
			Boolean saltar = validarCategorizacionPorInterseccion(
					valoresFormula, zonal, enteAcreditadoProyecto, provincia);
			if (saltar) {
				return;
			}
						
			if (categoriaXNormativa > 0) {
				// si las coordenadas intersecan y la subactividad es Líneas de
				// Transmisión menor o igual a 230 KV con una longitud de 10 km
				// y Subestaciones, se debe cambiar el tipo a licencia
				categoriaXNormativa = esSubactividadSubestacionElectrica ? 3
						: categoriaXNormativa;
				proyecto.setCategorizacion(categoriaXNormativa);
				// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				// valorCalculoFacade.guardar(valoresFormula);
			}
			if (tipoAutoridadAmbiental == 1) {
				proyecto.setAreaResponsable(areaService
						.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
				proyecto.setAreaInventarioForestal(proyecto
						.getAreaResponsable());
				// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			}
			if (tipoAutoridadAmbiental == 2 || tipoAutoridadAmbiental == 3
					|| tipoAutoridadAmbiental == 4) {
				proyecto.setAreaResponsable(zonal);
				proyecto.setAreaInventarioForestal(proyecto
						.getAreaResponsable());
				// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			}

		} else {
			// Validar categorizacion por interseccion con capas especificas
			Boolean saltar = validarCategorizacionPorInterseccion(
					valoresFormula, zonal, enteAcreditadoProyecto, provincia);
			if (saltar) {
				return;
			}
		}

		if (ciiuArearesponsable.getSubActividad() != null) {
			if (categoriaXNormativa > 0) {
				proyecto.setCategorizacion(categoriaXNormativa);
				// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				// valorCalculoFacade.guardar(valoresFormula);
			}

			if (ciiuArearesponsable.getCatalogoCIUU().getSaneamiento()) {
				if (tipoAutoridadAmbiental == 2) {
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto
							.getAreaResponsable());
					proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				} else {
					if (enteAcreditadoProyecto != null) {
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("MUNICIPIO")) {
							// El proyecto es promovido por un Gobierno
							// municipal o metropolitano la Autoridad Ambiental
							// competente debe ser la Dirección Provincial
							// proyecto.setAreaResponsable(zonal);
							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(3, provincia) == null ? zonal
									: areaService.getAreaGadProvincial(3,
											provincia));
							proyecto.setAreaInventarioForestal(zonal);
							proyecto = proyectoLicenciaCoaFacade
									.guardar(proyecto);
							return;
						}
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("GOBIERNO")) {
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(proyecto
									.getAreaResponsable());
							proyecto = proyectoLicenciaCoaFacade
									.guardar(proyecto);
							return;
						}
					}
				}
			}
			// mtop
			if (ciiuArearesponsable.getCatalogoCIUU().getMtop()) {
				Boolean esUsuarioMtop = false;
				String usuariomtop = Constantes.getUsuarioMtop();
				String[] usuarioMtopArray = usuariomtop.split(",");
				for (String usuarioMt : usuarioMtopArray) {
					if (loginBean.getUsuario().getNombre().contains(usuarioMt)) {
						esUsuarioMtop = true;
						break;
					}
				}
				if (esUsuarioMtop) {
					// si el usuario que registra el proyecto es MTOP gestionan
					// las DPs
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto
							.getAreaResponsable());
					proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}
			// LIBRES APROVECHAMIENTOS
			if (ciiuArearesponsable.getCatalogoCIUU().getLibreAprovechamiento()) {
				if (totalCantonesDiferentes >= 2) {
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(zonal);
					proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}

				if (ubicacionPrincipal.getEnteAcreditadomunicipio() != null) {
					if (enteAcreditadoProyecto != null) {
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("MUNICIPIO")) {
							if (tipoAutoridadAmbiental == 1)
								proyecto.setAreaResponsable(areaService
										.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
							else if (tipoAutoridadAmbiental == 2
									|| tipoAutoridadAmbiental == 4)
								proyecto.setAreaResponsable(zonal);
							else if (tipoAutoridadAmbiental == 3)
								proyecto.setAreaResponsable(areaService
										.getAreaGadProvincial(3, provincia) == null ? zonal
										: areaService.getAreaGadProvincial(3,
												provincia));

							proyecto.setAreaInventarioForestal(zonal);
							proyecto = proyectoLicenciaCoaFacade
									.guardar(proyecto);
							return;
						}
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("GOBIERNO")) {
							if (tipoAutoridadAmbiental == 1)
								proyecto.setAreaResponsable(areaService
										.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
							if (tipoAutoridadAmbiental == 2
									|| tipoAutoridadAmbiental == 3)
								proyecto.setAreaResponsable(zonal);
							if (tipoAutoridadAmbiental == 4)
								proyecto.setAreaResponsable(ubicacionPrincipal
										.getEnteAcreditadomunicipio() == null ? zonal
										: ubicacionPrincipal
												.getEnteAcreditadomunicipio());

							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					} else {
						if (tipoAutoridadAmbiental == 1)
							proyecto.setAreaResponsable(areaService
									.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
						if (tipoAutoridadAmbiental == 2)
							proyecto.setAreaResponsable(zonal);
						if (tipoAutoridadAmbiental == 3)
							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(3, provincia) == null ? zonal
									: areaService.getAreaGadProvincial(3,
											provincia));
						if (tipoAutoridadAmbiental == 4)
							proyecto.setAreaResponsable(ubicacionPrincipal
									.getEnteAcreditadomunicipio() == null ? zonal
									: ubicacionPrincipal
											.getEnteAcreditadomunicipio());

						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				} else {
					// if(ubicacionPrincipal.getEnteAcreditado()!=null &&
					// ubicacionPrincipal.getEnteAcreditado().getTipoEnteAcreditado().equals("MUNICIPIO"))
					if (ubicacionPrincipal.getEnteAcreditadomunicipio() != null
							&& ubicacionPrincipal.getEnteAcreditadomunicipio()
									.getTipoEnteAcreditado()
									.equals("MUNICIPIO"))
						proyecto.setAreaResponsable(ubicacionPrincipal
								.getEnteAcreditadomunicipio() == null ? zonal
								: ubicacionPrincipal
										.getEnteAcreditadomunicipio());
					else
						proyecto.setAreaResponsable(zonal);

					proyecto.setAreaInventarioForestal(zonal);
					// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}

			// area responsable msp

			if (usuarioMsp) {
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
				}
				if (otroMsp) {

					if (totalProvinciasDiferentes == 1
							&& totalCantonesDiferentes >= 2) {
						//proyecto.setAreaResponsable(zonal);
						proyecto.setAreaResponsable(areaService
								.getAreaGadProvincial(3, provincia) == null ? zonal
								: areaService
										.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(proyecto
								.getAreaResponsable());
						proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}

					if (canton.getNombre().equals(
							"DISTRITO METROPOLITANO DE QUITO")
//							|| canton.getNombre().equals("GUAYAQUIL")
							|| canton.getNombre().equals("CUENCA")) {
						proyecto.setAreaResponsable(ubicacionPrincipal
								.getEnteAcreditado() == null ? zonal
								: ubicacionPrincipal.getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}

					Area areaResp = areaService.getAreaGadProvincial(3,
							provincia);
					if (areaResp == null) {
						areaResp = zonal;
					}

					proyecto.setAreaResponsable(areaResp);
					proyecto.setAreaInventarioForestal(zonal);
					// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				} else {
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto
							.getAreaResponsable());
					// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}
			// fin msp

			if (tipoAutoridadAmbiental == 1)
				proyecto.setAreaResponsable(areaService
						.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
			if (tipoAutoridadAmbiental == 2)
				proyecto.setAreaResponsable(zonal);
			if (tipoAutoridadAmbiental == 3) {
				if (totalProvinciasDiferentes == 1
						&& totalCantonesDiferentes >= 2) {
					if (enteAcreditadoProyecto != null
							&& proyecto.getUsuarioCreacion().equals(
									enteAcreditadoProyecto
											.getIdentificacionEnte())) {
						proyecto.setAreaResponsable(zonal);
						proyecto.setAreaInventarioForestal(proyecto
								.getAreaResponsable());
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					} else {
						proyecto.setAreaResponsable(areaService
								.getAreaGadProvincial(3, provincia) == null ? zonal
								: areaService
										.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}

				if (canton.getNombre()
						.equals("DISTRITO METROPOLITANO DE QUITO")
//						|| canton.getNombre().equals("GUAYAQUIL")
						|| canton.getNombre().equals("CUENCA")) {
					if (enteAcreditadoProyecto != null
							&& proyecto.getUsuarioCreacion().equals(
									enteAcreditadoProyecto
											.getIdentificacionEnte())) {

						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("GOBIERNO")) {
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(proyecto
									.getAreaResponsable());
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						} else {
							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(3, provincia) == null ? zonal
									: areaService.getAreaGadProvincial(3,
											provincia));
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}

					} else {
						proyecto.setAreaResponsable(ubicacionPrincipal
								.getEnteAcreditado() == null ? zonal
								: ubicacionPrincipal.getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}

				if (enteAcreditadoProyecto != null) {
					if (enteAcreditadoProyecto.getTipoEnteAcreditado().equals(
							"MUNICIPIO")) {
						// El proyecto es promovido por un Gobierno municipal o
						// metropolitano la Autoridad Ambiental competente debe
						// ser la Dirección Provincial
						// proyecto.setAreaResponsable(zonal);
						proyecto.setAreaResponsable(areaService
								.getAreaGadProvincial(3, provincia) == null ? zonal
								: areaService
										.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
					if (enteAcreditadoProyecto.getTipoEnteAcreditado().equals(
							"GOBIERNO")) {
						proyecto.setAreaResponsable(zonal);
						proyecto.setAreaInventarioForestal(proyecto
								.getAreaResponsable());
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}

				proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3,
						provincia) == null ? zonal : areaService
						.getAreaGadProvincial(3, provincia));
			}
			
			if (tipoAutoridadAmbiental == 5) {
				proyecto.setAreaResponsable(areaService
						.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS));
				proyecto.setAreaInventarioForestal(proyecto
						.getAreaResponsable());
				//proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			}

			if (tipoAutoridadAmbiental == 6) {
				proyecto.setAreaResponsable(areaFacade
						.getAreaPorAreaAbreviacion("ND"));
				proyecto.setAreaInventarioForestal(null);
				//proyecto.setEstadoAsignacionArea(2);
				//proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			}
			
			if ((catalogoCamaronera_ciuu1 || catalogoCamaronera_ciuu2 || catalogoCamaronera_ciuu3)
					&& zona != null
					&& Constantes.getActividadesCamaroneras().contains(
							ciiuArearesponsable.getCatalogoCIUU().getCodigo())) {
				// Validación de coordenadas PLAYA o ALTA
				if (tipoAutoridadAmbiental == 1) {
					if ((provincia.getId() == 23 || provincia.getId() == 11
							|| provincia.getId() == 20
							|| provincia.getId() == 15
							|| provincia.getId() == 27 || provincia.getId() == 28)) {
						if (zona.equals("PLAYA")) {
							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(6,
											coordenadasRcoaBean
													.getUbicacionPrincipal()
													.getUbicacionesGeografica()
													.getUbicacionesGeografica()) == null ? areaService
									.getAreaCoordinacionZonal(coordenadasRcoaBean
											.getUbicacionOficinaTecnica())
									: areaService
											.getAreaGadProvincial(
													3,
													coordenadasRcoaBean
															.getUbicacionPrincipal()
															.getUbicacionesGeografica()
															.getUbicacionesGeografica()));
							proyecto.setZona_camaronera(zona);
							// proyecto.setAcuerdo_camaronera(coordenadasRcoaBean.getNam());
							if (catalogoCamaronera_ciuu1) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad1
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu2) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad2
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu3) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad3
												.getTipoPermiso().toString()));
							}
							proyecto.setCategorizacion(Integer
									.parseInt(subActividad1.getTipoPermiso()
											.toString()));
						} else if (zona.equals("ALTA")) {

//							if (canton.getNombre().equals("GUAYAQUIL")) {
//								proyecto.setAreaResponsable(coordenadasRcoaBean
//										.getUbicacionPrincipal()
//										.getEnteAcreditado() == null ? zonal
//										: coordenadasRcoaBean
//												.getUbicacionPrincipal()
//												.getEnteAcreditado());
//							} else {
								proyecto.setAreaResponsable(areaService
										.getAreaGadProvincial(
												3,
												coordenadasRcoaBean
														.getUbicacionPrincipal()
														.getUbicacionesGeografica()
														.getUbicacionesGeografica()) == null ? areaService
										.getAreaCoordinacionZonal(coordenadasRcoaBean
												.getUbicacionOficinaTecnica())
										: areaService
												.getAreaGadProvincial(
														3,
														coordenadasRcoaBean
																.getUbicacionPrincipal()
																.getUbicacionesGeografica()
																.getUbicacionesGeografica()));
//							}
							proyecto.setZona_camaronera(zona);
							// proyecto.setAcuerdo_camaronera(coordenadasRcoaBean.getNam());
							if (catalogoCamaronera_ciuu1) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad1
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu2) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad2
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu3) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad3
												.getTipoPermiso().toString()));
							}
						} else if (zona.equals("MIXTA")) {

							proyecto.setAreaResponsable(zonal);

							proyecto.setZona_camaronera(zona);
							if (catalogoCamaronera_ciuu1) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad1
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu2) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad2
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu3) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad3
												.getTipoPermiso().toString()));
							}
						}
						proyecto.setAreaInventarioForestal(proyecto
								.getAreaResponsable());
						proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}

					if (zona.equals("MIXTA")) {
						proyecto.setAreaResponsable(zonal);

						proyecto.setZona_camaronera(zona);
						if (catalogoCamaronera_ciuu1) {
							proyecto.setCategorizacion(Integer
									.parseInt(subActividad1.getTipoPermiso()
											.toString()));
						} else if (catalogoCamaronera_ciuu2) {
							proyecto.setCategorizacion(Integer
									.parseInt(subActividad2.getTipoPermiso()
											.toString()));
						} else if (catalogoCamaronera_ciuu3) {
							proyecto.setCategorizacion(Integer
									.parseInt(subActividad3.getTipoPermiso()
											.toString()));
						}

						proyecto.setAreaInventarioForestal(zonal);
						proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}
				if (tipoAutoridadAmbiental == 2 || tipoAutoridadAmbiental == 3
						|| tipoAutoridadAmbiental == 4) {
					if ((provincia.getId() == 23 || provincia.getId() == 11
							|| provincia.getId() == 20
							|| provincia.getId() == 15
							|| provincia.getId() == 27 || provincia.getId() == 28)) {
						if (zona.equals("PLAYA")) {
							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(6,
											coordenadasRcoaBean
													.getUbicacionPrincipal()
													.getUbicacionesGeografica()
													.getUbicacionesGeografica()) == null ? areaService
									.getAreaCoordinacionZonal(coordenadasRcoaBean
											.getUbicacionOficinaTecnica())
									: areaService
											.getAreaGadProvincial(
													3,
													coordenadasRcoaBean
															.getUbicacionPrincipal()
															.getUbicacionesGeografica()
															.getUbicacionesGeografica()));
							proyecto.setZona_camaronera(zona);
							// proyecto.setAcuerdo_camaronera(coordenadasRcoaBean.getNam());
							if (catalogoCamaronera_ciuu1) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad1
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu2) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad2
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu3) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad3
												.getTipoPermiso().toString()));
							}
						} else if (zona.equals("ALTA")) {

//							if (canton.getNombre().equals("GUAYAQUIL")) {
//								proyecto.setAreaResponsable(coordenadasRcoaBean
//										.getUbicacionPrincipal()
//										.getEnteAcreditado() == null ? zonal
//										: coordenadasRcoaBean
//												.getUbicacionPrincipal()
//												.getEnteAcreditado());
//
//							} else {
								proyecto.setAreaResponsable(areaService
										.getAreaGadProvincial(
												3,
												coordenadasRcoaBean
														.getUbicacionPrincipal()
														.getUbicacionesGeografica()
														.getUbicacionesGeografica()) == null ? areaService
										.getAreaCoordinacionZonal(coordenadasRcoaBean
												.getUbicacionOficinaTecnica())
										: areaService
												.getAreaGadProvincial(
														3,
														coordenadasRcoaBean
																.getUbicacionPrincipal()
																.getUbicacionesGeografica()
																.getUbicacionesGeografica()));

//							}
							proyecto.setZona_camaronera(zona);
							// proyecto.setAcuerdo_camaronera(coordenadasRcoaBean.getNam());
							if (catalogoCamaronera_ciuu1) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad1
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu2) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad2
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu3) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad3
												.getTipoPermiso().toString()));
							}
						} else if (zona.equals("MIXTA")) {

							proyecto.setAreaResponsable(zonal);

							proyecto.setZona_camaronera(zona);
							if (catalogoCamaronera_ciuu1) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad1
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu2) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad2
												.getTipoPermiso().toString()));
							} else if (catalogoCamaronera_ciuu3) {
								proyecto.setCategorizacion(Integer
										.parseInt(subActividad3
												.getTipoPermiso().toString()));
							}
						}
						proyecto.setAreaInventarioForestal(zonal);
						proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}

					if (zona.equals("MIXTA")) {
						proyecto.setAreaResponsable(zonal);

						proyecto.setZona_camaronera(zona);
						if (catalogoCamaronera_ciuu1) {
							proyecto.setCategorizacion(Integer
									.parseInt(subActividad1.getTipoPermiso()
											.toString()));
						} else if (catalogoCamaronera_ciuu2) {
							proyecto.setCategorizacion(Integer
									.parseInt(subActividad2.getTipoPermiso()
											.toString()));
						} else if (catalogoCamaronera_ciuu3) {
							proyecto.setCategorizacion(Integer
									.parseInt(subActividad3.getTipoPermiso()
											.toString()));
						}

						proyecto.setAreaInventarioForestal(zonal);
						proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}
			}

			proyecto.setAreaInventarioForestal(zonal);
			// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			return;
		} else {
			if (ciiuArearesponsable.getCatalogoCIUU().getScoutDrilling()) {
				categoriaXNormativa = 2;
				proyecto.setAreaResponsable(areaService
						.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
				proyecto.setAreaInventarioForestal(proyecto
						.getAreaResponsable());
				proyecto.setCategorizacion(categoriaXNormativa);
				// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
				// valorCalculoFacade.guardar(valoresFormula);
				return;
			}

			if (ciiuArearesponsable.getCatalogoCIUU().getSaneamiento()) {
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
				}
				// if(ciiuArearesponsable.getSaneamiento())
				// {
				// proyecto.setAreaResponsable(zonal);
				// proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
				// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				// return;
				// }
				// else
				// {

				// if(tipoAutoridadAmbiental==2)
				// {
				// proyecto.setAreaResponsable(areaService.getAreaDireccionProvincialPorUbicacion(2,
				// ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica()));
				// proyecto.setAreaInventarioForestal(proyecto.getAreaResponsable());
				// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				// return;
				// }
				// if(tipoAutoridadAmbiental==3 || tipoAutoridadAmbiental==4)
				// {
				if (enteAcreditadoProyecto != null) {
					if (enteAcreditadoProyecto.getTipoEnteAcreditado().equals(
							"MUNICIPIO")) {
						if (tipoAutoridadAmbiental == 1) {
							proyecto.setAreaResponsable(areaService
									.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
							proyecto.setAreaInventarioForestal(proyecto
									.getAreaResponsable());
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
						if (tipoAutoridadAmbiental == 2) {
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(proyecto
									.getAreaResponsable());
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						} else {
							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(3, provincia) == null ? zonal
									: areaService.getAreaGadProvincial(3,
											provincia));
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					}
					if (enteAcreditadoProyecto.getTipoEnteAcreditado().equals(
							"GOBIERNO")) {
						proyecto.setAreaResponsable(zonal);
						proyecto.setAreaInventarioForestal(proyecto
								.getAreaResponsable());
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				} else {
					if (tipoAutoridadAmbiental == 1) {
						proyecto.setAreaResponsable(areaService
								.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
						proyecto.setAreaInventarioForestal(proyecto
								.getAreaResponsable());
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
					if (tipoAutoridadAmbiental == 2) {
						proyecto.setAreaResponsable(zonal);
						proyecto.setAreaInventarioForestal(proyecto
								.getAreaResponsable());
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
					if (tipoAutoridadAmbiental == 3) {
						if (canton.getNombre().equals(
								"DISTRITO METROPOLITANO DE QUITO")
//								|| canton.getNombre().equals("GUAYAQUIL")
								|| canton.getNombre().equals("CUENCA")) {
							proyecto.setAreaResponsable(ubicacionPrincipal
									.getEnteAcreditado() == null ? zonal
									: ubicacionPrincipal
									.getEnteAcreditado());
							proyecto.setAreaInventarioForestal(zonal);
							//proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						} else {

							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(3, provincia) == null ? zonal
									: areaService.getAreaGadProvincial(3,
											provincia));
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					}
				}

				// }
				// }
			}

			// if(esActividadZonaRural) cambio de las consideraciones 09-11-2020
			// {
			// if(categoriaXNormativa>0)
			// {
			// proyecto.setCategorizacion(categoriaXNormativa);
			// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
			// valorCalculoFacade.guardar(valoresFormula);
			// }
			// if(enteAcreditadoProyecto!=null)
			// {
			// if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("MUNICIPIO"))
			// {
			// proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3,
			// provincia)==null?zonal:areaService.getAreaGadProvincial(3,
			// provincia));
			// proyecto.setAreaInventarioForestal(zonal);
			// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			// return;
			// }
			// if(enteAcreditadoProyecto.getTipoEnteAcreditado().equals("GOBIERNO"))
			// {
			// proyecto.setAreaResponsable(zonal);
			// proyecto.setAreaInventarioForestal(zonal);
			// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			// return;
			// }
			// }
			// else
			// {
			// proyecto.setAreaResponsable(ubicacionPrincipal.getEnteAcreditado()==null?zonal:ubicacionPrincipal.getEnteAcreditado());
			// proyecto.setAreaInventarioForestal(zonal);
			// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
			// return;
			// }
			// }

			// hidrocarburos con bloques
			if (ciiuArearesponsable.getCatalogoCIUU().getBloques()) {
				esActividadExtractiva = true;
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
				}
				if (ciiuArearesponsable.getCatalogoCIUU().getTipoPermiso() != null
						&& ciiuArearesponsable.getCatalogoCIUU()
								.getTipoPermiso().equals("LICENCIA AMBIENTAL")) {
					// categoriaXNormativa=3;
					proyecto.setAreaResponsable(areaService
							.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
					proyecto.setAreaInventarioForestal(proyecto
							.getAreaResponsable());
					proyecto.setCategorizacion(categoriaXNormativa);
					proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
					return;
				} else {
					if (tipoAutoridadAmbiental == 1) {
						proyecto.setAreaResponsable(areaService
								.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
						proyecto.setAreaInventarioForestal(proyecto
								.getAreaResponsable());
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
					if (tipoAutoridadAmbiental == 2) {
						proyecto.setAreaResponsable(zonal);
						proyecto.setAreaInventarioForestal(proyecto
								.getAreaResponsable());
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
					// if(tipoAutoridadAmbiental==3)
					// {
					// proyecto.setAreaResponsable(ubicacionPrincipal.getEnteAcreditado());
					// proyecto.setAreaInventarioForestal(areaService.getAreaDireccionProvincialPorUbicacion(2,
					// ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica()));
					// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					// return;
					// }
				}
			} else
				esActividadExtractiva = false;

			// mtop
			if (ciiuArearesponsable.getCatalogoCIUU().getMtop()) {
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
				}

				Boolean esUsuarioMtop = false;
				String usuariomtop = Constantes.getUsuarioMtop();
				String[] usuarioMtopArray = usuariomtop.split(",");

				for (String usuarioMt : usuarioMtopArray) {
					if (loginBean.getUsuario().getNombre().contains(usuarioMt)) {
						esUsuarioMtop = true;
						break;
					}
				}

				if (esUsuarioMtop) {
					// si el usuario que registra el proyecto es MTOP gestionan
					// las DPs
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto
							.getAreaResponsable());
					// proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				} else {
					if (enteAcreditadoProyecto != null) {
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("MUNICIPIO")) {
							// El proyecto es promovido por un Gobierno
							// municipal o metropolitano la Autoridad Ambiental
							// competente debe ser la Dirección Provincial
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("GOBIERNO")) {
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(proyecto
									.getAreaResponsable());
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					}

					if (tipoAutoridadAmbiental == 3) {
						proyecto.setAreaResponsable(areaService
								.getAreaGadProvincial(3, provincia) == null ? zonal
								: areaService
										.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				}
			}
			// gestion Concurrente
			if (ciiuArearesponsable.getCatalogoCIUU().getGestionConcurrente()) {
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
				}
				CatalogoCIUUConcurrente concurrenteMunicipio = null;
				if (canton.getNombre()
						.equals("DISTRITO METROPOLITANO DE QUITO")
//						|| canton.getNombre().equals("GUAYAQUIL")
						|| canton.getNombre().equals("CUENCA"))
					concurrenteMunicipio = catalogoCIUUConcurrenteFacade
							.gestionConcurrente(canton.getNombre(),
									ciiuArearesponsable.getCatalogoCIUU());

				CatalogoCIUUConcurrente concurrenteGad = catalogoCIUUConcurrenteFacade
						.gestionConcurrente(provincia.getNombre(),
								ciiuArearesponsable.getCatalogoCIUU());
				if (concurrenteMunicipio != null) {
					if (enteAcreditadoProyecto != null) {
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("MUNICIPIO")
								&& concurrenteMunicipio.getAreaResponsable()
										.getTipoEnteAcreditado()
										.equals("MUNICIPIO")) {
							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(3, provincia) == null ? zonal
									: areaService.getAreaGadProvincial(3,
											provincia));
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						} else {
							// proyecto.setAreaResponsable(concurrenteMunicipio.getAreaResponsable());
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					} else {
						proyecto.setAreaResponsable(concurrenteMunicipio
								.getAreaResponsable());
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				} else if (concurrenteGad != null) {
					if (enteAcreditadoProyecto != null) {
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("GOBIERNO")
								&& concurrenteGad.getAreaResponsable()
										.getTipoEnteAcreditado()
										.equals("GOBIERNO")) {
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						} else {
							proyecto.setAreaResponsable(concurrenteGad
									.getAreaResponsable());
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					} else {
						proyecto.setAreaResponsable(concurrenteGad
								.getAreaResponsable());
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				} else {
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(zonal);
					// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}
			// LIBRES APROVECHAMIENTOS
			if (ciiuArearesponsable.getCatalogoCIUU().getLibreAprovechamiento()) {
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
				}
				if (totalCantonesDiferentes >= 2) {
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(zonal);
					// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}

				if (ubicacionPrincipal.getEnteAcreditadomunicipio() != null) {
					if (enteAcreditadoProyecto != null) {
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("MUNICIPIO")) {
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("GOBIERNO")) {
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					} else {
						if (ubicacionPrincipal.getEnteAcreditadomunicipio() != null
								&& ubicacionPrincipal
										.getEnteAcreditadomunicipio()
										.getTipoEnteAcreditado()
										.equals("MUNICIPIO"))
							proyecto.setAreaResponsable(ubicacionPrincipal
									.getEnteAcreditado() == null ? zonal
									: ubicacionPrincipal.getEnteAcreditado());
						else
							proyecto.setAreaResponsable(zonal);

						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				} else {
					// proyecto.setAreaResponsable(ubicacionPrincipal.getEnteAcreditadomunicipio()==null?zonal:ubicacionPrincipal.getEnteAcreditadomunicipio());
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(zonal);
					// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}

			// area responsable msp

			if (usuarioMsp) {
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
				}
				if (otroMsp) {

					if (totalProvinciasDiferentes == 1
							&& totalCantonesDiferentes >= 2) {
						// proyecto.setAreaResponsable(zonal);
						proyecto.setAreaResponsable(areaService
								.getAreaGadProvincial(3, provincia) == null ? zonal
								: areaService
										.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(proyecto
								.getAreaResponsable());
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}

					if (canton.getNombre().equals(
							"DISTRITO METROPOLITANO DE QUITO")
							|| canton.getNombre().equals("GUAYAQUIL")
							|| canton.getNombre().equals("CUENCA")) {
						proyecto.setAreaResponsable(ubicacionPrincipal
								.getEnteAcreditado() == null ? zonal
								: ubicacionPrincipal.getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}

					Area areaResp = areaService.getAreaGadProvincial(3,
							provincia);
					if (areaResp == null) {
						areaResp = zonal;
					}

					proyecto.setAreaResponsable(areaResp);
					proyecto.setAreaInventarioForestal(zonal);
					// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				} else {
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto
							.getAreaResponsable());
					// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}
			// fin msp

			if (tipoAutoridadAmbiental == 1) {
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
				}
				proyecto.setAreaResponsable(areaService
						.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL));
				proyecto.setAreaInventarioForestal(proyecto
						.getAreaResponsable());
				// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			} else if (tipoAutoridadAmbiental == 2) {
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
				}
				proyecto.setAreaResponsable(zonal);
				proyecto.setAreaInventarioForestal(proyecto
						.getAreaResponsable());
				// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				return;
			} else if (tipoAutoridadAmbiental == 3) {
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
				}
				// if(totalProvinciasDiferentes==1 &&
				// totalCantonesDiferentes>=2)
				// {
				// proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3,
				// provincia)==null?zonal:areaService.getAreaGadProvincial(3,
				// provincia));
				// proyecto.setAreaInventarioForestal(zonal);
				// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				// return;
				// }
				// if (ubicacionPrincipal.getEnteAcreditado()==null)
				// {
				// proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3,
				// provincia)==null?zonal:areaService.getAreaGadProvincial(3,
				// provincia));
				// proyecto.setAreaInventarioForestal(zonal);
				// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
				// return;
				// }
				// else
				// {
				if (enteAcreditadoProyecto != null) {
					String ubicacionEsMunicipio = "";
					if (ubicacionPrincipal.getEnteAcreditado() != null)
						ubicacionEsMunicipio = ubicacionPrincipal
								.getEnteAcreditado().getTipoEnteAcreditado();
					else
						ubicacionEsMunicipio = ubicacionPrincipal
								.getEnteAcreditadomunicipio() == null ? ""
								: ubicacionPrincipal
										.getEnteAcreditadomunicipio()
										.getTipoEnteAcreditado();

					if (enteAcreditadoProyecto.getTipoEnteAcreditado().equals(
							"MUNICIPIO")
							&& (ubicacionEsMunicipio.equals("MUNICIPIO"))) {

						proyecto.setAreaResponsable(areaService
								.getAreaGadProvincial(3, provincia) == null ? zonal
								: areaService
										.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					} else if (proyecto.getUsuarioCreacion().equals(
							enteAcreditadoProyecto.getIdentificacionEnte())) {
						// el proyecto es promovido por un Gobierno provincial,
						// la autoridad competente debe ser la Dirección
						// Provincial

						// Area areaResponsableAux = zonal;
						// proyecto.setAreaResponsable(areaResponsableAux);
						// proyecto.setAreaInventarioForestal(areaResponsableAux);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						// return;

						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("MUNICIPIO")) {
							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(3, provincia) == null ? zonal
									: areaService.getAreaGadProvincial(3,
											provincia));
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("GOBIERNO")) {
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					} else {
						proyecto.setAreaResponsable(ubicacionPrincipal
								.getEnteAcreditado() == null ? zonal
								: ubicacionPrincipal.getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				} else {
					if (totalProvinciasDiferentes == 1
							&& totalCantonesDiferentes >= 2) {
						proyecto.setAreaResponsable(areaService
								.getAreaGadProvincial(3, provincia) == null ? zonal
								: areaService
										.getAreaGadProvincial(3, provincia));
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}

					if (canton.getNombre().equals(
							"DISTRITO METROPOLITANO DE QUITO")
							|| canton.getNombre().equals("GUAYAQUIL")
							|| canton.getNombre().equals("CUENCA")) {
						proyecto.setAreaResponsable(ubicacionPrincipal
								.getEnteAcreditado() == null ? zonal
								: ubicacionPrincipal.getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					} else {
						// if(totalProvinciasDiferentes==1 &&
						// totalCantonesDiferentes>=2)
						// {
						// proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3,
						// provincia)==null?zonal:areaService.getAreaGadProvincial(3,
						// provincia));
						// proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						// return;
						// }
						if (ubicacionPrincipal.getEnteAcreditado() == null) {
							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(3, provincia) == null ? zonal
									: areaService.getAreaGadProvincial(3,
											provincia));
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						} else {
							// proyecto.setAreaResponsable(areaService.getAreaGadProvincial(3,
							// provincia)==null?zonal:areaService.getAreaGadProvincial(3,
							// provincia));
							proyecto.setAreaResponsable(ubicacionPrincipal
									.getEnteAcreditado() == null ? zonal
									: ubicacionPrincipal.getEnteAcreditado());
							proyecto.setAreaInventarioForestal(zonal);
							// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
							return;
						}
					}
				}
				// }
			} else if (tipoAutoridadAmbiental == 4) {
				if (categoriaXNormativa > 0) {
					proyecto.setCategorizacion(categoriaXNormativa);
					// valoresFormula.setCategorizacionAlternativa(categoriaXNormativa);
					// valorCalculoFacade.guardar(valoresFormula);
				}
				if (enteAcreditadoProyecto != null) {
					if (enteAcreditadoProyecto.getTipoEnteAcreditado().equals(
							"MUNICIPIO")) {
						proyecto.setAreaResponsable(ubicacionPrincipal
								.getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					} else {
						proyecto.setAreaResponsable(ubicacionPrincipal
								.getEnteAcreditado());
						proyecto.setAreaInventarioForestal(zonal);
						// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
						return;
					}
				} else {
					proyecto.setAreaResponsable(ubicacionPrincipal
							.getEnteAcreditadomunicipio());
					proyecto.setAreaInventarioForestal(zonal);
					// proyecto=proyectoLicenciaCoaFacade.guardar(proyecto);
					return;
				}
			}
		}

	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		documentoAltoImpacto = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoAltoImpacto.setContenidoDocumento(contenidoDocumento);
		documentoAltoImpacto.setNombreDocumento(event.getFile().getFileName());
		documentoAltoImpacto.setExtencionDocumento(".pdf");
		documentoAltoImpacto.setTipo("application/pdf");
		documentoAltoImpacto.setNombreTabla(SimuladorProyectoLicenciaCoa.class
				.getSimpleName());
	}

	public void uploadListenerGeneticCiiu1(FileUploadEvent event) {
		documentoGeneticoCiiu1 = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoGeneticoCiiu1.setContenidoDocumento(contenidoDocumento);
		documentoGeneticoCiiu1
				.setNombreDocumento(event.getFile().getFileName());
		documentoGeneticoCiiu1.setExtencionDocumento(".pdf");
		documentoGeneticoCiiu1.setTipo("application/pdf");
		documentoGeneticoCiiu1.setNombreTabla(ProyectoLicenciaCuaCiuu.class
				.getSimpleName());
	}

	public void uploadListenerGeneticCiiu2(FileUploadEvent event) {
		documentoGeneticoCiiu2 = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoGeneticoCiiu2.setContenidoDocumento(contenidoDocumento);
		documentoGeneticoCiiu2
				.setNombreDocumento(event.getFile().getFileName());
		documentoGeneticoCiiu2.setExtencionDocumento(".pdf");
		documentoGeneticoCiiu2.setTipo("application/pdf");
		documentoGeneticoCiiu2.setNombreTabla(ProyectoLicenciaCuaCiuu.class
				.getSimpleName());
	}

	public void uploadListenerGeneticCiiu3(FileUploadEvent event) {
		documentoGeneticoCiiu3 = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoGeneticoCiiu3.setContenidoDocumento(contenidoDocumento);
		documentoGeneticoCiiu3
				.setNombreDocumento(event.getFile().getFileName());
		documentoGeneticoCiiu3.setExtencionDocumento(".pdf");
		documentoGeneticoCiiu3.setTipo("application/pdf");
		documentoGeneticoCiiu3.setNombreTabla(ProyectoLicenciaCuaCiuu.class
				.getSimpleName());
	}

	public void uploadListenerDocSectorial(FileUploadEvent event) {
		documentoDocSectorial = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoDocSectorial.setContenidoDocumento(contenidoDocumento);
		documentoDocSectorial.setNombreDocumento(event.getFile().getFileName());
		documentoDocSectorial.setExtencionDocumento(".pdf");
		documentoDocSectorial.setTipo("application/pdf");
		documentoDocSectorial.setNombreTabla(SimuladorProyectoLicenciaCoa.class
				.getSimpleName());
	}

	public void uploadListenerDocFrontera(FileUploadEvent event) {
		documentoDocFrontera = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoDocFrontera.setContenidoDocumento(contenidoDocumento);
		documentoDocFrontera.setNombreDocumento(event.getFile().getFileName());
		documentoDocFrontera.setExtencionDocumento(".pdf");
		documentoDocFrontera.setTipo("application/pdf");
		documentoDocFrontera.setNombreTabla(SimuladorProyectoLicenciaCoa.class
				.getSimpleName());
	}
	
	public void uploadListenerDocCamaronera(FileUploadEvent event) {
		documentoDocCamaronera = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoDocCamaronera.setContenidoDocumento(contenidoDocumento);
		documentoDocCamaronera.setNombreDocumento(event.getFile().getFileName());
		documentoDocCamaronera.setExtencionDocumento(".pdf");
		documentoDocCamaronera.setTipo("application/pdf");
		documentoDocCamaronera.setNombreTabla(SimuladorProyectoLicenciaCoa.class
				.getSimpleName());
	}

	public void generarMapa() {
		ResponseCertificado resCer = new ResponseCertificado();
		try {
			resCer = wsMapas.getGenerarMapaSimuladorWSPort()
					.generarCertificadoInterseccionSimulador(
							proyecto.getCodigoUnicoAmbiental());
			if (resCer.getWorkspaceAlfresco() == null) {
				msgError.add("Ocurrió un error al generar el mapa de intersección");
			} else {
				TipoDocumento tipoDocumento = new TipoDocumento();
				tipoDocumento
						.setId(TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA
								.getIdTipoDocumento());
				documentoMapa = new SimuladorDocumentosCOA();
				documentoMapa.setIdAlfresco(resCer.getWorkspaceAlfresco());
				documentoMapa.setExtencionDocumento(".pdf");
				documentoMapa.setTipo("application/pdf");
				documentoMapa.setTipoDocumento(tipoDocumento);
				documentoMapa.setNombreTabla(SimuladorProyectoLicenciaCoa.class
						.getSimpleName());
				documentoMapa
						.setNombreDocumento("Mapa_Certificado_intersección.pdf");
				documentoMapa.setIdTabla(proyecto.getId());
				documentoMapa.setProyectoLicenciaCoa(proyecto);
				documentoMapa = documentosFacade.guardar(documentoMapa);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al generar el mapa de intersección");
		}

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
	public void validarConcesiones() {
		if (listaConcesionesMineras.size() > 0) {
			for (ProyectoLicenciaAmbientalConcesionesMineras x : listaConcesionesMineras) {
				if (x.getCodigo().equals(concesionesMineras.getCodigo())) {
					JsfUtil.addMessageError("El Código de concesión ya se encuentra registrada");
					return;
				}
			}
		}

		DerechoMineroMAEDTO derechoMinero = new DerechoMineroMAEDTO();
		SuiaServices_Service_Arcon concesion;
		try {
			concesion = new SuiaServices_Service_Arcon(new URL(
					Constantes.getUrlWsRegistroCivilSri()));
			SuiaServicesArcon arcon = concesion.getSuiaServicesPort();
			derechoMinero = arcon.getConsultarCatastral(concesionesMineras
					.getCodigo());
			Boolean esTitular = false;
			if (derechoMinero.getDerechoMinero().getTitularDocumento().length() == loginBean
					.getNombreUsuario().length())
				esTitular = derechoMinero.getDerechoMinero()
						.getTitularDocumento()
						.equals(loginBean.getNombreUsuario()) ? true : false;
			else if (derechoMinero.getDerechoMinero().getTitularDocumento()
					.length() > loginBean.getNombreUsuario().length())
				esTitular = derechoMinero.getDerechoMinero()
						.getTitularDocumento()
						.contains(loginBean.getNombreUsuario()) ? true : false;
			else
				esTitular = loginBean.getNombreUsuario().contains(
						derechoMinero.getDerechoMinero().getTitularDocumento()) ? true
						: false;

			if (esTitular) {
				concesionesMineras.setMaterial(derechoMinero.getDerechoMinero()
						.getMaterialInteres());
				concesionesMineras.setNombre(derechoMinero.getDerechoMinero()
						.getNombreDerechoMinero());
				concesionesMineras.setRegimen(derechoMinero.getDerechoMinero()
						.getRegimen());
				concesionesMineras.setArea(derechoMinero.getDerechoMinero()
						.getSuperficie());
				if (proyecto.getConcesionMinera()) {
					if (listaConcesionesMineras.size() == 0)
						listaConcesionesMineras.add(concesionesMineras);
					else {
						int rep = 0;
						for (ProyectoLicenciaAmbientalConcesionesMineras x : listaConcesionesMineras) {
							if (x.getCodigo().equals(
									concesionesMineras.getCodigo())) {
								rep++;
							}
						}

						if (rep == 0)
							listaConcesionesMineras.add(concesionesMineras);
						else
							JsfUtil.addMessageError("El Código de concesión ya se encuentra registrada");

					}
				}
			} else
				JsfUtil.addMessageError("El Código de concesión minera no pertenece a este usuario");

		} catch (Exception e) {
			JsfUtil.addMessageError("Sin servicio ARCOM");
		}
	}

	public void limpiarConcesiones() {
		concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
		concesionesMineras = new ProyectoLicenciaAmbientalConcesionesMineras();
		listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
	}

	public void agregarConcesiones() {
		concesionesMineras = new ProyectoLicenciaAmbientalConcesionesMineras();
	}

	public void eliminarConcesion(
			ProyectoLicenciaAmbientalConcesionesMineras concesion) {
		listaConcesionesMineras.remove(concesion);
	}

	public void autoridadMAE() {
		Area area = areaFacade
				.getAreaSiglas(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL);
		List<Usuario> uList = usuarioFacade.buscarUsuariosPorRolYArea(
				"AUTORIDAD AMBIENTAL MAE", area);
		if (uList == null || uList.size() == 0) {
			// JsfUtil.addMessageError("No se encontró el usuario en "+
			// area.getAreaName());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			return;
		} else {
			usuarioAutoridad = uList.get(0);
		}

		// Area areaSecretaria=
		// areaFacade.getAreaSiglas(Constantes.SIGLAS_SUBSECRETARIA_PATRIMONIO_NATURAL);
		// List<Usuario>uList1=usuarioFacade.buscarUsuariosPorRolYArea("SUBSECRETARIO DE PATRIMONIO NATURAL",areaSecretaria);
		// if(uList1==null || uList1.size()==0)
		// {
		// JsfUtil.addMessageError("No se encontró el usuario en "+
		// areaSecretaria.getAreaName());
		// return;
		// }else{
		// subsePatrimonioNatural=uList1.get(0);
		// }
	}

	public void cerrarDialogoArt() {
		dialogoArt = false;
	}

	public boolean validarActividadART() {
		boolean estado = false;
		dialogoArt = false;
		// if(ciiuArearesponsable.getCatalogoCIUU().getActividadART())
		// {
		if ((proyecto.getGestionDesechos() || proyecto
				.getTransportaSustanciasQuimicas())
				&& proyecto.getCategorizacion() <= 2) {
			if (proyecto.getCategorizacion() == 1) {
				mensajeArt = "Sr. Operador considerando que su actividad implica impacto ambiental no significativo no puede ser habilitado para la gestión de residuos o desechos peligroso y/o especiales "
						+ "y el transporte de sustancias químicas, por lo tanto, revise la información y modifique en el caso de ser necesario o desactive las opciones antes descritas";
			}
			if (proyecto.getCategorizacion() == 2) {
				mensajeArt = "Sr. Operador considerando que su actividad implica impacto ambiental bajo no puede ser habilitado para la gestión de residuos o desechos peligroso y/o especiales "
						+ "y el transporte de sustancias químicas, por lo tanto, revise la información y modifique en el caso de ser necesario o desactive las opciones antes descritas";
			}
			dialogoArt = true;
			estado = true;
		}
		// else
		// {
		// if((!proyecto.getGestionDesechos() &&
		// !proyecto.getTransportaSustanciasQuimicas()) &&
		// proyecto.getCategorizacion()>=3)
		// {
		//
		// mensajeArt="Sr. Operador considerando que su actividad requiere tanto gestión de residuos o desechos peligroso y/o especiales o como "
		// +
		// "el transporte de sustancias químicas, por lo tanto, revise la información y seleccione las opciones antes descritas";
		// dialogoArt=true;
		// estado=true;
		// }
		// }
		// }
		// else
		// {
		// if(proyecto.getGestionDesechos() ||
		// proyecto.getTransportaSustanciasQuimicas())
		// {
		// mensajeArt="Sr. Operador considerando que su actividad no puede ser habilitado para la gestión de residuos o desechos peligroso y/o especiales "
		// +
		// "y el transporte de sustancias químicas, por lo tanto, revise la información y modifique en el caso de ser necesario o desactive las opciones antes descritas";
		// dialogoArt=true;
		// estado=true;
		// }
		// }

		return estado;
	}

	public void iniciarProceso() {
		try {
			if (msgError.size() > 0) {
				JsfUtil.addMessageError(msgError);
			} else {
				if (validacionActividadMinera()) {
					DefaultRequestContext.getCurrentInstance().execute(
							"PF('mensajeMineria').show();");
					return;
				}
				if (validarActividadART()) {
					return;
				}

				autoridadMAE();
				Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();

				// parametros que se setean solo al inicio del proyecto
				parametros.put("u_operador", JsfUtil.getLoggedUser()
						.getNombre());
				parametros.put("u_tramite", proyecto.getCodigoUnicoAmbiental());
				parametros.put("u_idProyecto", proyecto.getId());
				parametros.put("autoridadAmbiental",
						usuarioAutoridad.getNombre());
				// parametros.put("subsecretario",
				// subsePatrimonioNatural.getNombre());
				parametros.put("viabilidadFavorable", false);
				parametros.put("esPrimeraVez", true);

				parametros.put("requierePagoInicial",
						Constantes.getRequierePagoInicial());
				if (proyecto.getCategorizacion() == 1)
					parametros.put("requierePagoInicial", false); // se bloquea
																	// el pago
																	// para
																	// certificados
																	// ambientales

				Map<String, Object> parametrosComunes = variablesProceso();
				if (parametrosComunes == null)
					return;

				parametros.putAll(parametrosComunes);

				Long idProceso = procesoFacade.iniciarProceso(
						JsfUtil.getLoggedUser(),
						Constantes.RCOA_REGISTRO_PRELIMINAR,
						proyecto.getCodigoUnicoAmbiental(), parametros);

				// Map<String, Object> params = new ConcurrentHashMap<String,
				// Object>();
				// String urlIniciarEia=Constantes.getiniciarEIAWS();
				// params.put("urlIniciarEIA",urlIniciarEia+idProceso+"-"+JsfUtil.getLoggedUser().getNombre());
				// // params.put("ContentParam",
				// "id="+idProceso+"&us="+JsfUtil.getLoggedUser().getNombre());
				// procesoFacade.modificarVariablesProceso(loginBean.getUsuario(),
				// idProceso, params);

				List<SimuladorDocumentosCOA> coordenadaImplantacion = documentosFacade
						.documentoXTablaIdXIdDoc(
								proyecto.getId(),
								TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION,
								SimuladorProyectoLicenciaCoa.class
										.getSimpleName());
				if (coordenadaImplantacion.size() > 0) {
					SimuladorDocumentosCOA coordenada = coordenadaImplantacion
							.get(0);
					coordenada.setIdProceso(idProceso);
					// documentosFacade.guardar(coordenada);
				}

				List<SimuladorDocumentosCOA> coordenadaGeografica = documentosFacade
						.documentoXTablaIdXIdDoc(
								proyecto.getId(),
								TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA,
								SimuladorProyectoLicenciaCoa.class
										.getSimpleName());
				if (coordenadaGeografica.size() > 0) {
					SimuladorDocumentosCOA coordenada = coordenadaGeografica
							.get(0);
					coordenada.setIdProceso(idProceso);
					// documentosFacade.guardar(coordenada);
				}

				List<SimuladorDocumentosCOA> listaDocumentosAltoImpacto = documentosFacade
						.documentoXTablaIdXIdDoc(
								proyecto.getId(),
								TipoDocumentoSistema.RCOA_DOCUMENTO_ALTO_IMPACTO,
								"ProyectoLicenciaCoa");
				if (listaDocumentosAltoImpacto.size() > 0) {
					SimuladorDocumentosCOA coordenada = listaDocumentosAltoImpacto
							.get(0);
					coordenada.setIdProceso(idProceso);
					// documentosFacade.guardar(coordenada);
				}

				List<SimuladorDocumentosCOA> listaDocumentosFrontera = documentosFacade
						.documentoXTablaIdXIdDoc(proyecto.getId(),
								TipoDocumentoSistema.RCOA_DOCUMENTO_FRONTERA,
								"ProyectoLicenciaCoa");
				if (listaDocumentosFrontera.size() > 0) {
					SimuladorDocumentosCOA coordenada = listaDocumentosFrontera
							.get(0);
					coordenada.setIdProceso(idProceso);
					// documentosFacade.guardar(coordenada);
				}

				if (ciiu1 != null && ciiu1.getId() != null) {
					List<SimuladorDocumentosCOA> listaDocumentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									ciiu1.getId(),
									TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,
									"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						SimuladorDocumentosCOA coordenada = listaDocumentos
								.get(0);
						coordenada.setIdProceso(idProceso);
						// documentosFacade.guardar(coordenada);
					}
				}

				if (ciiu2 != null && ciiu2.getId() != null) {
					List<SimuladorDocumentosCOA> listaDocumentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									ciiu2.getId(),
									TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,
									"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						SimuladorDocumentosCOA coordenada = listaDocumentos
								.get(0);
						coordenada.setIdProceso(idProceso);
						// documentosFacade.guardar(coordenada);
					}
				}

				if (ciiu3 != null && ciiu3.getId() != null) {
					List<SimuladorDocumentosCOA> listaDocumentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									ciiu3.getId(),
									TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,
									"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						SimuladorDocumentosCOA coordenada = listaDocumentos
								.get(0);
						coordenada.setIdProceso(idProceso);
						// documentosFacade.guardar(coordenada);
					}
				}

				proyecto.setEstadoRegistro(true);
				proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('continuarDialog').show();");
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
		}
	}

	public String generarNumeroSolicitud() {
		try {
			return Constantes.SIGLAS_INSTITUCION
					+ "-SOL-ART-"
					+ secuenciasFacade.getCurrentYear()
					+ "-"
					+ secuenciasFacade
							.getNextValueDedicateSequence("SOLICITUD_APROBACION_REQUISITOS_TECNICOS");
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

	public void verInterseccionesDialogo() {
		zonasInterccionDetalle = "";
		HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> lista = new HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
		lista = interseccionProyectoLicenciaAmbientalFacade
				.listaIntersecan(proyecto);
		if (lista.size() > 0) {
			zonasInterccionDetalle = "Su proyecto obra o actividad interseca con: ";
			zonasInterccionDetalle = zonasInterccionDetalle + "<ul>";
			String nombreCapa = "";
			for (SimuladorInterseccionProyectoLicenciaAmbiental i : lista
					.keySet()) {
				nombreCapa = i.getDescripcionCapa();
				String nombreInterseccion = "";
				for (DetalleInterseccionProyectoAmbiental j : lista.get(i)) {
					nombreInterseccion += (nombreInterseccion == "") ? j
							.getNombreGeometria() : ","
							+ j.getNombreGeometria();
				}
				zonasInterccionDetalle = zonasInterccionDetalle + "<li>"
						+ nombreCapa + ": " + nombreInterseccion + "</li>";
			}
			zonasInterccionDetalle = zonasInterccionDetalle + "</ul>";
		}
	}

	public void descargarCoorGeo() {
		try {
			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(
					proyecto.getId(),
					TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA,
					"SimuladorProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarExcel(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()),
						"Coordenadas área geográfica (Anexo 1)");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void descargarCoorImpl() {
		try {
			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(
					proyecto.getId(),
					TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION,
					"SimuladorProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarExcel(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()),
						"Coordenadas área de implantación");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void descargarDocSectorial() {
		try {
			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(
					proyecto.getId(),
					TipoDocumentoSistema.RCOA_DOCUMENTO_SECTORIAL,
					"SimuladorProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()), "Documento sectorial");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void descargarDocFrontera() {
		try {
			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(
					proyecto.getId(),
					TipoDocumentoSistema.RCOA_DOCUMENTO_FRONTERA,
					"SimuladorProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()), "Documento frontera");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void descargarDocAltoImpacto() {
		try {
			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(
					proyecto.getId(),
					TipoDocumentoSistema.RCOA_DOCUMENTO_ALTO_IMPACTO,
					"SimuladorProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()), "Documento alto impacto");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void descargarCiiuGen1() {
		try {
			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(
					ciiu1.getId(),
					TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO,
					"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()), "Documento CONABIO");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void descargarCiiuGen2() {
		try {
			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(
					ciiu2.getId(),
					TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO,
					"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()), "Documento CONABIO");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void descargarCiiuGen3() {
		try {
			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(
					ciiu3.getId(),
					TipoDocumentoSistema.RCOA_DOCUMENTO_GENETICO,
					"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()), "Documento CONABIO");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void eliminarMagnitud1() {
		valorMagnitud1 = new ValorMagnitud();
		listaValoresMagnitud1 = new ArrayList<ValorMagnitud>();
		criteriomagnitud = "";
	}

	public void eliminarMagnitud2() {
		valorMagnitud2 = new ValorMagnitud();
		listaValoresMagnitud2 = new ArrayList<ValorMagnitud>();
		criteriomagnitud = "";
	}

	public void eliminarMagnitud3() {
		valorMagnitud3 = new ValorMagnitud();
		listaValoresMagnitud3 = new ArrayList<ValorMagnitud>();
		criteriomagnitud = "";
	}

	public StreamedContent getAyudaCatalogoCiiu() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (ayudaCatalogoCiiu != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						ayudaCatalogoCiiu));
				content.setName(Constantes.AYUDA_CATALOGO_CIIU);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public void validateCompletarDatos(FacesContext context,
			UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();

		Integer totalMagnitudes = 0;
		if (valorMagnitud1.getId() != null)
			totalMagnitudes++;
		if (valorMagnitud2.getId() != null)
			totalMagnitudes++;
		if (valorMagnitud3.getId() != null)
			totalMagnitudes++;

		if (totalMagnitudes < 1)
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar minimo un rango de operación.", null));
		if (tieneDocumentoRsq != null && tieneDocumentoRsq
				&& rsqSeleccionado == null)
			errorMessages
					.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe seleccionar un Código de registro de sustancia químicas.",
							null));

		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public String getNombreProponente() {

		String nombreOperador = "";

		if (loginBean.getUsuario() != null) {
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
		if (seleccion.equals(1)) {
			ciiu1.setGenetico(null);
			ciiu1.setSaneamiento(null);
			ciiu1.setCombinacionSubActividades(null);
			ciiu1.setTipoRegimenMinero(null);
			ciiu1.setFinanciadoBancoDesarrollo(null);
			ciiu1.setIdActividadFinanciadoBancoEstado(null);

			if (documentoGeneticoCiiu1 != null
					&& documentoGeneticoCiiu1.getId() != null) {
				listaDocumentosEliminar.add(documentoGeneticoCiiu1);
			}
			documentoGeneticoCiiu1 = null;

			if (documentoSenaguaCiiu1 != null
					&& documentoSenaguaCiiu1.getId() != null) {
				listaDocumentosEliminar.add(documentoSenaguaCiiu1);
			}
			documentoSenaguaCiiu1 = null;

			if (documentoPorSectorCiiu1 != null
					&& documentoPorSectorCiiu1.getId() != null) {
				listaDocumentosEliminar.add(documentoPorSectorCiiu1);
			}
			documentoPorSectorCiiu1 = null;

			if (documentoViabilidadPngidsCiiu1 != null
					&& documentoViabilidadPngidsCiiu1.getId() != null) {
				listaDocumentosEliminar.add(documentoViabilidadPngidsCiiu1);
			}
			documentoViabilidadPngidsCiiu1 = null;
		} else if (seleccion.equals(2)) {
			ciiu2.setGenetico(null);
			ciiu2.setSaneamiento(null);
			ciiu2.setCombinacionSubActividades(null);
			ciiu2.setTipoRegimenMinero(null);
			ciiu2.setFinanciadoBancoDesarrollo(null);
			ciiu2.setIdActividadFinanciadoBancoEstado(null);
			
			if (documentoGeneticoCiiu2 != null
					&& documentoGeneticoCiiu2.getId() != null) {
				listaDocumentosEliminar.add(documentoGeneticoCiiu2);
			}
			documentoGeneticoCiiu2 = null;

			if (documentoSenaguaCiiu2 != null
					&& documentoSenaguaCiiu2.getId() != null) {
				listaDocumentosEliminar.add(documentoSenaguaCiiu2);
			}
			documentoSenaguaCiiu2 = null;

			if (documentoPorSectorCiiu2 != null
					&& documentoPorSectorCiiu2.getId() != null) {
				listaDocumentosEliminar.add(documentoPorSectorCiiu2);
			}
			documentoPorSectorCiiu2 = null;

			if (documentoViabilidadPngidsCiiu2 != null
					&& documentoViabilidadPngidsCiiu2.getId() != null) {
				listaDocumentosEliminar.add(documentoViabilidadPngidsCiiu2);
			}
			documentoViabilidadPngidsCiiu2 = null;
		} else if (seleccion.equals(3)) {
			ciiu3.setGenetico(null);
			ciiu3.setSaneamiento(null);
			ciiu3.setCombinacionSubActividades(null);
			ciiu3.setTipoRegimenMinero(null);
			ciiu3.setFinanciadoBancoDesarrollo(null);
			ciiu3.setIdActividadFinanciadoBancoEstado(null);

			if (documentoGeneticoCiiu3 != null
					&& documentoGeneticoCiiu3.getId() != null) {
				listaDocumentosEliminar.add(documentoGeneticoCiiu3);
			}
			documentoGeneticoCiiu3 = null;

			if (documentoSenaguaCiiu3 != null
					&& documentoSenaguaCiiu3.getId() != null) {
				listaDocumentosEliminar.add(documentoSenaguaCiiu3);
			}
			documentoSenaguaCiiu3 = null;

			if (documentoPorSectorCiiu3 != null
					&& documentoPorSectorCiiu3.getId() != null) {
				listaDocumentosEliminar.add(documentoPorSectorCiiu3);
			}
			documentoPorSectorCiiu3 = null;

			if (documentoViabilidadPngidsCiiu3 != null
					&& documentoViabilidadPngidsCiiu3.getId() != null) {
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
			nombreSector = wf1.getCatalago().getTipoSector().getNombre()
					.toUpperCase();

			if (nombreSector.equals("MINERÍA"))
				existeMineria = true;
			if (nombreSector.equals("HIDROCARBUROS")
					&& wf1.getCatalago().getBloques())
				existeHidrocarburos = true;
		}

		if (wf2.getCatalago() != null && wf2.getCatalago().getId() != null) {
			nombreSector = wf2.getCatalago().getTipoSector().getNombre()
					.toUpperCase();

			if (nombreSector.equals("MINERÍA"))
				existeMineria = true;
			if (nombreSector.equals("HIDROCARBUROS")
					&& wf2.getCatalago().getBloques())
				existeHidrocarburos = true;
		}
		if (wf3.getCatalago() != null && wf3.getCatalago().getId() != null) {
			nombreSector = wf3.getCatalago().getTipoSector().getNombre()
					.toUpperCase();

			if (nombreSector.equals("MINERÍA"))
				existeMineria = true;
			if (nombreSector.equals("HIDROCARBUROS")
					&& wf3.getCatalago().getBloques())
				existeHidrocarburos = true;
		}

		if (existeMineriaAux && !existeMineria) {
			proyecto.setOperacionMinera(null);
			proyecto.setConcesionMinera(null);

			concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
			concesionesMineras = new ProyectoLicenciaAmbientalConcesionesMineras();
			listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
		}

		if (existeHidrocarburosAux && !existeHidrocarburos) {
			bloquesBean.reset();
			bloquesBean.getBloquesSeleccionados();
		}

		this.nombreSector = nombreSector;
	}

	public void limpiarCamposModificarProyecto() {
		coordenadasGeograficas = new ArrayList<SimuladorCoordenadasProyecto>(); // se elimina las coordenadas geograficas para obligar al usuario a cargar nuevamente
		coordinatesWrappersGeo = new ArrayList<SimuladorCoordendasPoligonos>();
		coordinatesWrappers = new ArrayList<SimuladorCoordendasPoligonos>(); // se elimina las coordenadas de implantancin para obligar al usuario a cargar nuevamente
		ubicacionesSeleccionadas = new ArrayList<UbicacionesGeografica>();
		proyecto.setSuperficie(BigDecimal.ZERO);
		superficieMetrosCuadrados = BigDecimal.ZERO;
		zonasInterccionDetalle = "";
		superficieGeograficaHa = 0.0;
		superficieGeograficaMetros = 0.0;

		estadoFrontera = false;
		estadoZonaIntangible = false;
		estadoZonaIntangibleAux = false;
		esZonaSnapEspecial = false;
		irFinalizar = false;
		areaPendienteAsignar = false;

		if (documentoDocFrontera != null
				&& documentoDocFrontera.getId() != null)
			listaDocumentosEliminar.add(documentoDocFrontera);

		if (documentoDocSectorial != null
				&& documentoDocSectorial.getId() != null)
			listaDocumentosEliminar.add(documentoDocSectorial);

		if (documentoMapa != null && documentoMapa.getId() != null)
			listaDocumentosEliminar.add(documentoMapa);

		if (documentoCertificado != null
				&& documentoCertificado.getId() != null)
			listaDocumentosEliminar.add(documentoCertificado);

		documentoDocFrontera = null;
		documentoDocSectorial = null;
	}

	public void guardarDatosModificados() {
		// elimino documentos que cambiaron por modificacion de proyecto
		if (listaDocumentosEliminar.size() > 0) {
			for (SimuladorDocumentosCOA doc : listaDocumentosEliminar) {
				doc.setEstado(false);
				documentosFacade.guardar(doc);
			}

			listaDocumentosEliminar = new ArrayList<SimuladorDocumentosCOA>();
		}
	}

	public void uploadListenerSenaguaCiiu1(FileUploadEvent event) {
		documentoSenaguaCiiu1 = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSenaguaCiiu1.setContenidoDocumento(contenidoDocumento);
		documentoSenaguaCiiu1.setNombreDocumento(event.getFile().getFileName());
		documentoSenaguaCiiu1.setExtencionDocumento(".pdf");
		documentoSenaguaCiiu1.setTipo("application/pdf");
		documentoSenaguaCiiu1.setNombreTabla(ProyectoLicenciaCuaCiuu.class
				.getSimpleName());
	}

	public void uploadListenerSenaguaCiiu2(FileUploadEvent event) {
		documentoSenaguaCiiu2 = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSenaguaCiiu2.setContenidoDocumento(contenidoDocumento);
		documentoSenaguaCiiu2.setNombreDocumento(event.getFile().getFileName());
		documentoSenaguaCiiu2.setExtencionDocumento(".pdf");
		documentoSenaguaCiiu2.setTipo("application/pdf");
		documentoSenaguaCiiu2.setNombreTabla(ProyectoLicenciaCuaCiuu.class
				.getSimpleName());
	}

	public void uploadListenerSenaguaCiiu3(FileUploadEvent event) {
		documentoSenaguaCiiu3 = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSenaguaCiiu3.setContenidoDocumento(contenidoDocumento);
		documentoSenaguaCiiu3.setNombreDocumento(event.getFile().getFileName());
		documentoSenaguaCiiu3.setExtencionDocumento(".pdf");
		documentoSenaguaCiiu3.setTipo("application/pdf");
		documentoSenaguaCiiu3.setNombreTabla(ProyectoLicenciaCuaCiuu.class
				.getSimpleName());
	}

	public void descargarDocumentoSenagua(Integer tipoDocumento) {
		try {
			Integer idTabla = 0;
			if (tipoDocumento == 1)
				idTabla = ciiu1.getId();
			else if (tipoDocumento == 2)
				idTabla = ciiu2.getId();
			else if (tipoDocumento == 3)
				idTabla = ciiu3.getId();

			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(idTabla,
					TipoDocumentoSistema.RCOA_DOCUMENTO_SANEAMIENTO,
					"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()), "Documento SENAGUA");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void uploadListenerAutorizacionSectoresHME(FileUploadEvent event) {
		SimuladorDocumentosCOA autorizacionSector = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		autorizacionSector.setContenidoDocumento(contenidoDocumento);
		autorizacionSector.setNombreDocumento(event.getFile().getFileName());
		autorizacionSector.setExtencionDocumento(".pdf");
		autorizacionSector.setTipo("application/pdf");
		autorizacionSector.setNombreTabla(ProyectoLicenciaCuaCiuu.class
				.getSimpleName());

		String tipoDocumentoString = (String) event.getComponent()
				.getAttributes().get("tipoDocumento");

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

	public void recuperarAutorizacionesPorSector() {
		List<SimuladorDocumentosCOA> listaDocumentos;
		String nombreSector = "";
		try {
			if (ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null) {
				nombreSector = ciiuPrincipal.getTipoSector().getNombre()
						.toUpperCase();

				if (nombreSector.equals("MINERÍA")
						|| nombreSector.equals("HIDROCARBUROS")) {// ||
																	// nombreSector.equals("ELÉCTRICO")
					esCiiu1HidrocarburoMineriaElectrico = true;
					listaDocumentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									ciiu1.getId(),
									TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,
									"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						documentoPorSectorCiiu1 = listaDocumentos.get(0);
					}
				}
			}

			if (ciiuComplementaria1 != null
					&& ciiuComplementaria1.getTipoSector() != null) {
				nombreSector = ciiuComplementaria1.getTipoSector().getNombre()
						.toUpperCase();

				if (nombreSector.equals("MINERÍA")
						|| nombreSector.equals("HIDROCARBUROS")) {// ||
																	// nombreSector.equals("ELÉCTRICO")
					esCiiu2HidrocarburoMineriaElectrico = true;
					listaDocumentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									ciiu2.getId(),
									TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,
									"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						documentoPorSectorCiiu2 = listaDocumentos.get(0);
					}
				}
			}

			if (ciiuComplementaria2 != null
					&& ciiuComplementaria2.getTipoSector() != null) {
				nombreSector = ciiuComplementaria2.getTipoSector().getNombre()
						.toUpperCase();

				if (nombreSector.equals("MINERÍA")
						|| nombreSector.equals("HIDROCARBUROS")) {// ||
																	// nombreSector.equals("ELÉCTRICO")
					esCiiu3HidrocarburoMineriaElectrico = true;
					listaDocumentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									ciiu3.getId(),
									TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,
									"ProyectoLicenciaCuaCiuu");
					if (listaDocumentos.size() > 0) {
						documentoPorSectorCiiu3 = listaDocumentos.get(0);
					}
				}
			}
		} catch (CmisAlfrescoException e) {
		}

	}

	public void descargarAutorizacionPorSector(Integer tipoDocumento) {
		try {
			Integer idTabla = 0;
			if (tipoDocumento == 1)
				idTabla = ciiu1.getId();
			else if (tipoDocumento == 2)
				idTabla = ciiu2.getId();
			else if (tipoDocumento == 3)
				idTabla = ciiu3.getId();

			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(idTabla,
					TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,
					"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()), "Documento autorización");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void uploadListenerViabilidadPngids(FileUploadEvent event) {
		SimuladorDocumentosCOA viabilidadTecnica = new SimuladorDocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		viabilidadTecnica.setContenidoDocumento(contenidoDocumento);
		viabilidadTecnica.setNombreDocumento(event.getFile().getFileName());
		viabilidadTecnica.setExtencionDocumento(".pdf");
		viabilidadTecnica.setTipo("application/pdf");
		viabilidadTecnica.setNombreTabla(ProyectoLicenciaCuaCiuu.class
				.getSimpleName());

		String tipoDocumentoString = (String) event.getComponent()
				.getAttributes().get("tipoDocumento");

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

	public void recuperarDocumentosViabilidadPngids() {
		List<SimuladorDocumentosCOA> listaDocumentos;
		try {
			if (ciiuPrincipal != null
					&& ciiuPrincipal.getRequiereViabilidadPngids() != null) {
				listaDocumentos = documentosFacade
						.documentoXTablaIdXIdDoc(
								ciiu1.getId(),
								TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS,
								"ProyectoLicenciaCuaCiuu");
				if (listaDocumentos.size() > 0) {
					documentoPorSectorCiiu1 = listaDocumentos.get(0);
				}
			}

			if (ciiuComplementaria1 != null
					&& ciiuComplementaria1.getRequiereViabilidadPngids() != null) {
				listaDocumentos = documentosFacade
						.documentoXTablaIdXIdDoc(
								ciiu2.getId(),
								TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS,
								"ProyectoLicenciaCuaCiuu");
				if (listaDocumentos.size() > 0) {
					documentoPorSectorCiiu2 = listaDocumentos.get(0);
				}
			}

			if (ciiuComplementaria2 != null
					&& ciiuComplementaria2.getRequiereViabilidadPngids() != null) {
				listaDocumentos = documentosFacade
						.documentoXTablaIdXIdDoc(
								ciiu3.getId(),
								TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS,
								"ProyectoLicenciaCuaCiuu");
				if (listaDocumentos.size() > 0) {
					documentoPorSectorCiiu3 = listaDocumentos.get(0);
				}
			}
		} catch (CmisAlfrescoException e) {
		}
	}

	public void descargarDocumentosViabilidadPngids(Integer tipoDocumento) {
		try {
			Integer idTabla = 0;
			if (tipoDocumento == 1)
				idTabla = ciiu1.getId();
			else if (tipoDocumento == 2)
				idTabla = ciiu2.getId();
			else if (tipoDocumento == 3)
				idTabla = ciiu3.getId();

			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade
					.documentoXTablaIdXIdDoc(
							idTabla,
							TipoDocumentoSistema.RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS,
							"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()),
						"Documento viabilidad técnica");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void limpiarGeneracionDesechos() {
		if (proyecto.getGeneraDesechos() != null
				&& !proyecto.getGeneraDesechos()) {
			existeRgdObtenidoAnterior = null;
			esRgdValido = null;
			proyecto.setCodigoRgdAsociado(null);
		}

		RequestContext.getCurrentInstance().update(
				"frmPreliminar:pnlExtraGeneracionRgd");
		RequestContext.getCurrentInstance().update("frmPreliminar:rbtTieneRgd");
	}

	public void limpiarTieneGenerador() {
		if (existeRgdObtenidoAnterior != null && !existeRgdObtenidoAnterior) {
			esRgdValido = null;
			proyecto.setCodigoRgdAsociado(null);
		}

		RequestContext.getCurrentInstance()
				.update("frmPreliminar:pnlBuscarRgd");
		
		validarCodigoRgd(false);
	}

	public void limpiarCodigoRgd() {
		esRgdValido = null;
		proyecto.setCodigoRgdAsociado(null);
		RequestContext.getCurrentInstance()
				.update("frmPreliminar:pnlBuscarRgd");
	}

	public void validarCodigoRgd(Boolean desdeCarga) {
		listaRegistrosGeneradores = new ArrayList<>();
		listaRegistrosGeneradores = registroGeneradorDesechosProyectosRcoaFacade
				.listarGeneradoresActivosNoVinculados(rucCedula);

		List<GeneradorCustom> registrosSuiaiii = registroGeneradorDesechosFacade
				.listarGeneradoresActivosNoVinculados(loginBean.getUsuario().getId());
		listaRegistrosGeneradores.addAll(registrosSuiaiii);

		if (desdeCarga) {
			listaRegistrosGeneradores.add(registroGeneradorSeleccionado);
		}
	}

	public void seleccionarGenerador() throws Exception {

		esRgdValido = false;
		proyecto.setCodigoRgdAsociado(null);

		String mensajeErrorPuntos = "Estimado Operador, el sistema no puede realizar esta vinculación debido a que el Registro de Generador de Residuos y Desechos seleccionado requiere ser actualizado a través del sistema SUIA. Una vez que se haya procedido con su actualización lo podrá vincular siempre y cuando las coordenadas del Registro de Generador de Residuos y Desechos se encuentren en el área de implantación del proyecto";

		if (registroGeneradorSeleccionado.getSourceType().equals(
				GeneradorCustom.SOURCE_TYPE_SUIA_III)) {
			List<String> errorMessages = new ArrayList<>();

			List<PuntoRecuperacion> listaPuntos = registroGeneradorDesechosFacade
					.listarPuntosPorIdGenerador(registroGeneradorSeleccionado
							.getId());
			if (listaPuntos != null && listaPuntos.size() > 0) {
				for (PuntoRecuperacion punto : listaPuntos) {
					for (FormaPuntoRecuperacion formaPunto : punto
							.getFormasPuntoRecuperacion()) {
						String coordenadasPoligono = "";

						for (Coordenada coordenada : formaPunto
								.getCoordenadas()) {
							String coordenadaX = coordenada.getX().toString()
									.replace(",", ".");
							String coordenadaY = coordenada.getY().toString()
									.replace(",", ".");

							coordenadasPoligono += (coordenadasPoligono == "") ? coordenadaX
									+ " " + coordenadaY
									: "," + coordenadaX + " " + coordenadaY;
						}

						String mensaje = validarCoordenadaAreaMuestraPoligono(
								coordenadasPoligono, formaPunto.getTipoForma()
										.getId());

						if (mensaje != null && !errorMessages.contains(mensaje)) {
							errorMessages.add(mensaje);
						}
					}
				}

				if (!errorMessages.isEmpty()) {
					JsfUtil.addMessageError(errorMessages);
					registroGeneradorSeleccionado = null;
				} else {
					// se asigna el codigo porque con este campo = null se
					// genera el RGD en licencia ambiental
					proyecto.setCodigoRgdAsociado(registroGeneradorSeleccionado
							.getDocumento());
					esRgdValido = true;
				}

			} else {
				// no hay puntos válidos se requiere actualizar el RGD
				JsfUtil.addMessageError(mensajeErrorPuntos);
				registroGeneradorSeleccionado = null;
			}
		} else {

			Boolean existeCoordenadas = true;
			List<String> errorMessages = new ArrayList<>();

			List<PuntoRecuperacionRgdRcoa> puntosRecuperacion = puntoRecuperacionRgdRcoaFacade
					.buscarPorRgd(registroGeneradorSeleccionado.getId());
			if (puntosRecuperacion != null && puntosRecuperacion.size() > 0) {
				for (PuntoRecuperacionRgdRcoa punto : puntosRecuperacion) {
					String coordenadaString = "";
					if (punto.getFormasPuntoRecuperacionRgdRcoa() != null
							&& !punto.getFormasPuntoRecuperacionRgdRcoa()
									.isEmpty()) {
						FormaPuntoRecuperacionRgdRcoa formaRgd = punto
								.getFormasPuntoRecuperacionRgdRcoa().get(0);
						int tipoForma = 0;
						if (formaRgd.getTipoForma() != null
								&& formaRgd.getTipoForma().getId() != null) {
							tipoForma = formaRgd.getTipoForma().getId();

							if (formaRgd.getCoordenadas() != null
									&& formaRgd.getCoordenadas().size() == 1) {
								tipoForma = TipoForma.TIPO_FORMA_PUNTO; // por
																		// error
																		// del
																		// shape
																		// en
																		// BDD q
																		// dice
																		// poligono
																		// pero
																		// solo
																		// tiene
																		// un
																		// punto
																		// de
																		// coordenadas
							}
						} else {
							if (formaRgd.getCoordenadas() != null
									&& formaRgd.getCoordenadas().size() > 0) {
								tipoForma = (formaRgd.getCoordenadas().size() == 1) ? TipoForma.TIPO_FORMA_PUNTO
										: TipoForma.TIPO_FORMA_POLIGONO;
							}
						}

						if (formaRgd.getCoordenadas() != null
								&& formaRgd.getCoordenadas().size() > 0) {
							for (CoordenadaRgdCoa coordenada : formaRgd
									.getCoordenadas()) {
								DecimalFormat formato = new DecimalFormat(
										"#.00000");
								String coorX = formato
										.format(coordenada.getX()).replace(",",
												".");
								String coorY = formato
										.format(coordenada.getY()).replace(",",
												".");

								coordenadaString += (coordenadaString == "") ? coorX
										.toString() + " " + coorY.toString()
										: "," + coorX.toString() + " "
												+ coorY.toString();
							}

							String mensaje = validarCoordenadaAreaMuestraPoligono(
									coordenadaString, tipoForma);

							if (mensaje != null
									&& !errorMessages.contains(mensaje)) {
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

			if (!existeCoordenadas) {
				JsfUtil.addMessageError(mensajeErrorPuntos);
				registroGeneradorSeleccionado = null;
				return;
			}

			// se asigna el codigo porque con este campo = null se genera el RGD
			// en licencia ambiental
			proyecto.setCodigoRgdAsociado(registroGeneradorSeleccionado
					.getDocumento());
			esRgdValido = true;
		}
	}

	public String validarCoordenadaAreaMuestraPoligono(String coordenadaPunto,
			int tipoForma) throws RemoteException {
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
			Intersecado_resultado[] resultadoInterseccion = ws
					.interseccion(poligono);

			if (resultadoInterseccion[0].getInformacion().getError() != null) {
				mensaje = resultadoInterseccion[0].getInformacion().getError()
						.toString();
			} else {
				String mensajeError = "Estimado operador, las coordenadas del Registro de Generador de Residuos y Desechos seleccionado, no se encuentran dentro del área de implantación del proyecto que está registrando, motivo por el cual no lo puede vincular. Deberá actualizar el Registro de generador de residuos y desechos en el sistema SUIA para poder realizar la vinculación.";
				Boolean intersecaProyecto = false;

				for (String coordenadasImplantacion : coordenadasRcoaBean
						.getCoordenadasImplantacion()) {
					if (tipoForma == (TipoForma.TIPO_FORMA_PUNTO)
							|| tipoForma == (TipoForma.TIPO_FORMA_LINEA)) {
						ContienePoligono_entrada verificarGeoImpla = new ContienePoligono_entrada();
						verificarGeoImpla.setU(Constantes
								.getUserWebServicesSnap());
						verificarGeoImpla.setTipo(tipo);
						verificarGeoImpla.setXy1(coordenadasImplantacion);
						verificarGeoImpla.setXy2(coordenadaPunto);

						ContienePoligono_resultado[] intRestGeoImpl = ws
								.contienePoligono(verificarGeoImpla);
						if (intRestGeoImpl[0].getInformacion().getError() != null) {
							mensaje = intRestGeoImpl[0].getInformacion()
									.getError().toString();
							break;
						} else {
							if (intRestGeoImpl[0].getContienePoligono()
									.getValor().equals("f")) {
								intersecaProyecto = false;
							} else {
								intersecaProyecto = true;
								break;
							}
						}
					} else {
						ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada();
						verificarGeoImpla.setU(Constantes
								.getUserWebServicesSnap());
						verificarGeoImpla.setXy1(coordenadasImplantacion);
						verificarGeoImpla.setXy2(coordenadaPunto);

						ContieneZona_resultado[] intRestGeoImpl = ws
								.contieneZona(verificarGeoImpla);
						if (intRestGeoImpl[0].getInformacion().getError() != null) {
							mensaje = intRestGeoImpl[0].getInformacion()
									.getError().toString();
							break;
						} else {
							if (intRestGeoImpl[0].getContieneCapa().getValor()
									.equals("f")) {
								intersecaProyecto = false;
							} else {
								intersecaProyecto = true;
								break;
							}
						}
					}
				}

				if (!intersecaProyecto && mensaje == null) {
					mensaje = mensajeError;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			mensaje = "Error inesperado, comuní­quese con mesa de ayuda";
		}

		return mensaje;
	}

	public Map<String, Object> variablesProceso() {
		Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();

		try {

			parametros.put("tipoProyecto", proyecto.getTipoProyecto());

			Boolean esProyectoEnEjecucionRegistroLicencia = false;
			if (proyecto.getTipoProyecto() == 2
					&& proyecto.getCategorizacion() > 1)
				esProyectoEnEjecucionRegistroLicencia = true;
			// aumentado si es proyecto nuevo pero si requiere ingreso de
			// diagnostico ambiental solo para licencias
			if (proyecto.getTipoProyecto() == 1
					&& (proyecto.getCategorizacion() == 3 || proyecto
							.getCategorizacion() == 4)
					&& proyecto.getMotivoDiagnosico() != null
					&& !proyecto.getMotivoDiagnosico().isEmpty())
				esProyectoEnEjecucionRegistroLicencia = true;
			parametros.put("esProyectoEnEjecucion",
					esProyectoEnEjecucionRegistroLicencia);

			if (proyecto.getInterecaSnap())
				parametros.put("u_intersecaSnap", true);
			else
				parametros.put("u_intersecaSnap", false);

			if (proyecto.getInterecaBosqueProtector()
					|| proyecto.getInterecaPatrimonioForestal())
				parametros.put("u_intersecaForestal", true);
			else
				parametros.put("u_intersecaForestal", false);

			// variable que inicia el proceso de Viabilidad Ambiental para
			// proyectos que intersecan con SNAP, Bosques y vegetación
			// protectores y/o Patrimonio Forestal
			if (proyecto.getInterecaSnap()
					|| proyecto.getInterecaBosqueProtector()
					|| proyecto.getInterecaPatrimonioForestal())
				parametros.put("intersecaSnapForestIntanManglar", true);
			else
				parametros.put("intersecaSnapForestIntanManglar", false);

			if (proyecto.getCategorizacion() == 1
					|| proyecto.getCategorizacion() == 2)
				parametros.put("esCertificadoRegistro", true);
			else
				parametros.put("esCertificadoRegistro", false);

			parametros.put("coberturaVegetal", proyecto.getRenocionCobertura());
			if (proyecto.getRenocionCobertura()
					&& (proyecto.getCategorizacion() == 1 || proyecto
							.getCategorizacion() == 2))
				parametros.put("u_existeInventarioForestal", true);
			else
				parametros.put("u_existeInventarioForestal", false);

			Boolean archivacionProyecto = false;

			parametros.put("zonificacionBiodiversidad", false);
			boolean tieneExcepcion = false;
			if (existeMineria) {
				if (proyecto.getInterecaSnap()) {
					parametros.put("esActividadExtractiva", true);
					parametros.put("tieneExcepcion", tieneExcepcion);
					archivacionProyecto = true;
				}
			}
			if (esActividadExtractiva) {
				// verificar si interseco por zonas protegidas,forestal y zonas
				// intangibles
				if (proyecto.getInterecaSnap()
						|| proyecto.getInterecaBosqueProtector()
						|| proyecto.getInterecaPatrimonioForestal()
						|| estadoZonaIntangible) {

					parametros.put("tieneExcepcion", tieneExcepcion);

					if (!tieneExcepcion)
						archivacionProyecto = true;
				} else
					parametros.put("esActividadExtractiva", false);
			} else
				parametros.put("esActividadExtractiva", false);

			if (proyecto.getGeneraDesechos()
					&& proyecto.getCodigoRgdAsociado() != null && esRgdValido)
				parametros.put("generaDesecho", false); // si tiene RGD anterior
														// y es valido no
														// ingresa al modulo de
														// RGDP
			else
				parametros.put("generaDesecho", proyecto.getGeneraDesechos());

			if ((proyecto.getGestionDesechos() || proyecto
					.getTransportaSustanciasQuimicas())
					&& proyecto.getCategorizacion() >= 3) {
				parametros.put("gestionaResiduoTransporte", true);
				parametros.put("artRcoa", true);

				if (aprobacionRequisitosTecnicosFacade
						.getAprobacionRequisitosTecnicosByProyectoLicenciaAmbiental(proyecto
								.getCodigoUnicoAmbiental()) == null) {
					AprobacionRequisitosTecnicos art = new AprobacionRequisitosTecnicos();
					art.setProyecto(proyecto.getCodigoUnicoAmbiental());
					art.setNombreProyecto(proyecto.getNombreProyecto());
					art.setSolicitud(generarNumeroSolicitud());
					art.setUsuario(JsfUtil.getLoggedUser());
					art.setAreaResponsable(proyecto.getAreaInventarioForestal());
					art.setTipoEstudio(1);
					art.setEstado(true);
					art.setIniciadoPorNecesidad(true);
					art.setProvincia(ubicacionPrincipal
							.getUbicacionesGeografica()
							.getUbicacionesGeografica());
					if (proyecto.getGestionDesechos())
						art.setEsGestionPropia(proyecto.getEsGestionPropia());

					aprobacionRequisitosTecnicosFacade.guardar(art);
				}
			} else
				parametros.put("gestionaResiduoTransporte", false);

			if (estadoSustanciasQuimicas) {
				parametros.put("empleaSustanciaQuimica", true);
				parametros.put("requierePagoRSQ", false);
			} else
				parametros.put("empleaSustanciaQuimica", false);

			parametros.put("gadMunicipalDesecho", false);
			parametros.put("u_categoria", proyecto.getCategorizacion());
			parametros.put("u_emisionVariosProyectos", false);

			if (archivacionProyecto) {
				// Recuperar el usuario responsable de la firma del oficio de
				// archivación de proyecto
				Area areaResponsable = proyecto.getAreaResponsable();
				String rolAutoridad = (areaResponsable.getTipoArea().getId()
						.equals(1)) ? "AUTORIDAD AMBIENTAL MAE"
						: "AUTORIDAD AMBIENTAL";

				List<Usuario> listaUsuario = usuarioFacade
						.buscarUsuariosPorRolYArea(rolAutoridad,
								areaResponsable);
				if (listaUsuario == null || listaUsuario.size() == 0) {
					// JsfUtil.addMessageError("No se encontró autoridad ambiental en "+
					// areaResponsable.getAreaName());
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					return null;
				} else {
					parametros.put("autoridadCompetente", listaUsuario.get(0)
							.getNombre());
				}
			}

			// scout drilling--------------------------
			if (ciiuArearesponsable.getCatalogoCIUU().getScoutDrilling()) {
				parametros.put("scoutDrilling", true);
				parametros
						.put("u_nombreFormularioPMA",
								"/prevencion/categoria2/v2/fichaMineria020/default.jsf");
				parametros.put("u_exentoPago", false);
				parametros
						.put("u_factorCovertura",
								Float.parseFloat(Constantes
										.getPropertyAsString("costo.factor.covertura.vegetal")));
				parametros
						.put("u_costoTramite",
								Float.parseFloat(Constantes
										.getPropertyAsString("costo.tramite.registro.ambiental")));
				parametros.put("u_vieneRcoa", true);
				parametros.put("generaDesecho", true);
			} else {
				parametros.put("scoutDrilling", false);
			}

		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}

		return parametros;
	}

	public boolean validarCoordenadasConcesion(String coordenadas)
			throws RemoteException {
		SVA_Reproyeccion_IntersecadoPortTypeProxy ws = new SVA_Reproyeccion_IntersecadoPortTypeProxy();
		ws.setEndpoint(Constantes.getInterseccionesWS());

		String coodenadasImpla = "";
		boolean validar = true;
		for (int i = 0; i <= coordinatesWrappers.size() - 1; i++) {
			coodenadasImpla = "";
			for (int j = 0; j <= coordinatesWrappers.get(i).getCoordenadas()
					.size() - 1; j++) {
				coodenadasImpla += (coodenadasImpla == "") ? coordinatesWrappers
						.get(i).getCoordenadas().get(j).getX().toString()
						+ " "
						+ coordinatesWrappers.get(i).getCoordenadas().get(j)
								.getY().toString()
						: ","
								+ coordinatesWrappers.get(i).getCoordenadas()
										.get(j).getX().toString()
								+ " "
								+ coordinatesWrappers.get(i).getCoordenadas()
										.get(j).getY().toString();
			}
			ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada(); // verifica
																					// que
																					// el
																					// poligono
																					// este
																					// contenida
																					// dentro
																					// de
																					// la
																					// ubicación
																					// geográfica
			verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
			verificarGeoImpla.setXy1(coodenadasImpla);
			verificarGeoImpla.setXy2(coordenadas);
			ContieneZona_resultado[] intRestGeoImpl;
			intRestGeoImpl = ws.contieneZona(verificarGeoImpla);
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
			documentoViabilidadPngidsCiiu1 = new SimuladorDocumentosCOA();
			viabilidad1 = new ViabilidadTecnica();
			catalogoCamaronera_ciuu1 = false;
			ingresoViabilidadTecnica1 = true;

			residuosActividadesCiiuBean
					.setListaCiiuResiduos1(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
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
			documentoViabilidadPngidsCiiu2 = new SimuladorDocumentosCOA();
			viabilidad2 = new ViabilidadTecnica();
			catalogoCamaronera_ciuu2 = false;
			ingresoViabilidadTecnica2 = true;

			residuosActividadesCiiuBean
					.setListaCiiuResiduos2(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
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
			documentoViabilidadPngidsCiiu3 = new SimuladorDocumentosCOA();
			viabilidad3 = new ViabilidadTecnica();
			catalogoCamaronera_ciuu3 = false;
			ingresoViabilidadTecnica3 = true;

			residuosActividadesCiiuBean
					.setListaCiiuResiduos3(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
			listaProyectoCiiuSubActividad3 = new ArrayList<ProyectoCoaCiuuSubActividades>();
			listaSubActividadesHijas3 = new ArrayList<>();
			break;
		default:
			break;
		}

		if (catalogoCamaronera_ciuu1 == false
				&& catalogoCamaronera_ciuu2 == false
				&& catalogoCamaronera_ciuu3 == false) {
			proyecto.setConcesionCamaronera(null);

			if (documentoDocCamaronera != null
					&& documentoDocCamaronera.getId() != null) {
				listaDocumentosEliminar.add(documentoDocCamaronera);
			}
			documentoDocCamaronera = null;
		}

	}

	public void limpiarCiiu1() {
		ciiuPrincipal = new CatalogoCIUU();
		wf1 = new WolframVO();
		limpiarCamposActividad(1);
		RequestContext.getCurrentInstance().update(
				"frmPreliminar:txtCiiuPrincipal");

		listaSubActividades1 = new ArrayList<SubActividades>();
		subOpciones1 = false;
		subActividad1 = new SubActividades();
		bancoEstado1 = new SubActividades();
		parent1 = new SubActividades();
		tipoOpcion1 = null;
		residuosActividadesCiiuBean
				.setListaCiiuResiduos1(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad1 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades1 = null;
		requiereTablaResiduo1 = false;
		codigoOficioGRECI1 = "";
		documentoViabilidadPngidsCiiu1 = new SimuladorDocumentosCOA();
		viabilidad1 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu1 = false;

		ciiuComplementaria1 = new CatalogoCIUU();
		limpiarCamposActividad(2);
		listaSubActividades2 = new ArrayList<SubActividades>();
		subOpciones2 = false;
		subActividad2 = new SubActividades();
		bancoEstado2 = new SubActividades();
		parent2 = new SubActividades();
		tipoOpcion2 = null;
		residuosActividadesCiiuBean
				.setListaCiiuResiduos2(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad2 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades2 = null;
		requiereTablaResiduo2 = false;
		codigoOficioGRECI2 = "";
		documentoViabilidadPngidsCiiu2 = new SimuladorDocumentosCOA();
		viabilidad2 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu2 = false;

		ciiuComplementaria2 = new CatalogoCIUU();
		limpiarCamposActividad(3);
		listaSubActividades3 = new ArrayList<SubActividades>();
		subOpciones3 = false;
		subActividad3 = new SubActividades();
		bancoEstado3 = new SubActividades();
		parent3 = new SubActividades();
		tipoOpcion3 = null;
		residuosActividadesCiiuBean
				.setListaCiiuResiduos3(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad3 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades3 = null;
		requiereTablaResiduo3 = false;
		codigoOficioGRECI3 = "";
		documentoViabilidadPngidsCiiu3 = new SimuladorDocumentosCOA();
		viabilidad3 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu3 = false;

		coordenadasRcoaBean.setEstadoZonaIntangible(false);
		esCiiu1HidrocarburoMineriaElectrico = false;
		esCiiu2HidrocarburoMineriaElectrico = false;
		esCiiu3HidrocarburoMineriaElectrico = false;

		validarSectorActividades();

		proyecto.setConcesionCamaronera(null);
		if (documentoDocCamaronera != null
				&& documentoDocCamaronera.getId() != null) {
			listaDocumentosEliminar.add(documentoDocCamaronera);
		}
		documentoDocCamaronera = null;
		
		mostrarPoseeContrato = false;
		tieneContratoMineria = null;
		esContratoOperacion = null;
		concesionMinera = null;
		
		zona = "";

		RequestContext.getCurrentInstance().update(
				"frmPreliminar:idActividadGenetica1");
		RequestContext.getCurrentInstance().update(
				"frmPreliminar:idActividadGenetica2");
		RequestContext.getCurrentInstance().update(
				"frmPreliminar:idActividadGenetica3");
		RequestContext.getCurrentInstance().update(
				"frmPreliminar:pnlNotificacion");

	}

	public void limpiarCiiu2() {
		ciiuComplementaria1 = new CatalogoCIUU();
		wf2 = new WolframVO();
		limpiarCamposActividad(2);

		listaSubActividades2 = new ArrayList<SubActividades>();
		subOpciones2 = false;
		subActividad2 = new SubActividades();
		bancoEstado2 = new SubActividades();
		parent2 = new SubActividades();
		tipoOpcion2 = null;
		residuosActividadesCiiuBean
				.setListaCiiuResiduos2(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad2 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades2 = null;
		requiereTablaResiduo2 = false;
		codigoOficioGRECI2 = "";
		documentoViabilidadPngidsCiiu2 = new SimuladorDocumentosCOA();
		viabilidad2 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu2 = false;

		ciiuComplementaria2 = new CatalogoCIUU();
		limpiarCamposActividad(3);
		listaSubActividades3 = new ArrayList<SubActividades>();
		subOpciones3 = false;
		subActividad3 = new SubActividades();
		bancoEstado3 = new SubActividades();
		parent3 = new SubActividades();
		tipoOpcion3 = null;
		residuosActividadesCiiuBean
				.setListaCiiuResiduos3(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad3 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades3 = null;
		requiereTablaResiduo3 = false;
		codigoOficioGRECI3 = "";
		documentoViabilidadPngidsCiiu3 = new SimuladorDocumentosCOA();
		viabilidad3 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu3 = false;

		coordenadasRcoaBean.setEstadoZonaIntangible(false);
		esCiiu2HidrocarburoMineriaElectrico = false;
		esCiiu3HidrocarburoMineriaElectrico = false;
		if (ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null) {
			String nombreSector = ciiuPrincipal.getTipoSector().getNombre()
					.toUpperCase();
			if (nombreSector.equals("MINERÍA")
					|| nombreSector.equals("HIDROCARBUROS")) { // ||
																// nombreSector.equals("ELÉCTRICO")
				esCiiu1HidrocarburoMineriaElectrico = true;
				if (coordenadasRcoaBean.isEstadoZonaIntangibleAux())
					coordenadasRcoaBean.setEstadoZonaIntangible(true);
			}
		}

		if (catalogoCamaronera_ciuu1 == false) {
			proyecto.setConcesionCamaronera(null);

			if (documentoDocCamaronera != null
					&& documentoDocCamaronera.getId() != null) {
				listaDocumentosEliminar.add(documentoDocCamaronera);
			}
			documentoDocCamaronera = null;
		}
		
		mostrarPoseeContrato = false;
		tieneContratoMineria = null;
		esContratoOperacion = null;
		concesionMinera = null;
		
		zona = "";

		RequestContext.getCurrentInstance().update(
				"frmPreliminar:idActividadGenetica1");
		RequestContext.getCurrentInstance().update(
				"frmPreliminar:idActividadGenetica2");
		RequestContext.getCurrentInstance().update(
				"frmPreliminar:idActividadGenetica3");
	}

	public void limpiarCiiu3() {
		ciiuComplementaria2 = new CatalogoCIUU();
		wf3 = new WolframVO();
		limpiarCamposActividad(3);
		listaSubActividades3 = new ArrayList<SubActividades>();
		subOpciones3 = false;
		subActividad3 = new SubActividades();
		bancoEstado3 = new SubActividades();
		parent3 = new SubActividades();
		tipoOpcion3 = null;
		residuosActividadesCiiuBean
				.setListaCiiuResiduos3(new ArrayList<ProyectoLicenciaCoaCiiuResiduos>());
		listaProyectoCiiuSubActividad3 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		idSubactividades3 = null;
		requiereTablaResiduo3 = false;
		codigoOficioGRECI3 = "";
		documentoViabilidadPngidsCiiu3 = new SimuladorDocumentosCOA();
		viabilidad3 = new ViabilidadTecnica();
		catalogoCamaronera_ciuu3 = false;

		coordenadasRcoaBean.setEstadoZonaIntangible(false);
		esCiiu3HidrocarburoMineriaElectrico = false;
		if (ciiuComplementaria2 != null
				&& ciiuComplementaria2.getTipoSector() != null) {
			String nombreSector = ciiuComplementaria2.getTipoSector()
					.getNombre().toUpperCase();
			if (nombreSector.equals("MINERÍA")
					|| nombreSector.equals("HIDROCARBUROS")) { // ||
																// nombreSector.equals("ELÉCTRICO")
				esCiiu2HidrocarburoMineriaElectrico = true;
				if (coordenadasRcoaBean.isEstadoZonaIntangibleAux())
					coordenadasRcoaBean.setEstadoZonaIntangible(true);
			}
		}

		if (catalogoCamaronera_ciuu1 == false
				&& catalogoCamaronera_ciuu2 == false) {
			proyecto.setConcesionCamaronera(null);

			if (documentoDocCamaronera != null
					&& documentoDocCamaronera.getId() != null) {
				listaDocumentosEliminar.add(documentoDocCamaronera);
			}
			documentoDocCamaronera = null;
		}
		
		mostrarPoseeContrato = false;
		tieneContratoMineria = null;
		esContratoOperacion = null;
		concesionMinera = null;
		
		zona = "";

		RequestContext.getCurrentInstance().update(
				"frmPreliminar:idActividadGenetica1");
		RequestContext.getCurrentInstance().update(
				"frmPreliminar:idActividadGenetica2");
		RequestContext.getCurrentInstance().update(
				"frmPreliminar:idActividadGenetica3");
	}

	public void activarOpcionesAlcantarillado1() {
		if (parent1.getValorOpcion())
			subActivadesAlcantarillado1 = false;
		else
			subActivadesAlcantarillado1 = true;
	}

	public void activarOpcionesAlcantarillado2() {
		if (parent2.getValorOpcion())
			subActivadesAlcantarillado2 = false;
		else
			subActivadesAlcantarillado2 = true;
	}

	public void activarOpcionesAlcantarillado3() {
		if (parent3.getValorOpcion())
			subActivadesAlcantarillado3 = false;
		else
			subActivadesAlcantarillado3 = true;
	}
	
	public void activarOpcionesGalapagos1() {
		if (parent1.getValorOpcion())
			subActivadesGalapagos1 = true;
		else
			subActivadesGalapagos1 = false;

		if (parent1.getCatalogoCIUU().getCodigo().compareTo("I5510.01") == 0) {
			listaSubActividades1 = subActividadesFacade
					.actividadHijosPorRespuestaPadre(parent1);
			subActivadesGalapagos1 = true;
		}
	}

	public void activarOpcionesGalapagos2() {
		if (parent2.getValorOpcion())
			subActivadesGalapagos2 = true;
		else
			subActivadesGalapagos2 = false;

		if (parent2.getCatalogoCIUU().getCodigo().compareTo("I5510.01") == 0) {
			listaSubActividades2 = subActividadesFacade
					.actividadHijosPorRespuestaPadre(parent2);
			subActivadesGalapagos2 = true;
		}
	}

	public void activarOpcionesGalapagos3() {
		if (parent3.getValorOpcion())
			subActivadesGalapagos3 = true;
		else
			subActivadesGalapagos3 = false;

		if (parent3.getCatalogoCIUU().getCodigo().compareTo("I5510.01") == 0) {
			listaSubActividades3 = subActividadesFacade
					.actividadHijosPorRespuestaPadre(parent3);
			subActivadesGalapagos3 = true;
		}
	}

	public List<SubActividades> getHijosSubActividad(SubActividades parent,
			Integer nroActividad) {
		List<SubActividades> listaSubActividades = subActividadesFacade
				.actividadHijosPorRespuestaPadre(parent);

		switch (nroActividad) {
		case 1:
			listaSubActividades1 = listaSubActividades;
			if (listaSubActividades1.size() == 0) {
				requiereTablaResiduo1 = false;
				idSubactividades1 = null;
			}
			if (listaSubActividades1.size() == 1)
				subActividad1 = listaSubActividades1.get(0);
			
			if (parent.getValorOpcion()) {
				noEsMineriaArtesanal = true;
			} else {
				noEsMineriaArtesanal = false;
			}
			
			return listaSubActividades1;
		case 2:
			listaSubActividades2 = listaSubActividades;
			if (listaSubActividades2.size() == 0) {
				requiereTablaResiduo2 = false;
				idSubactividades2 = null;
			}
			if (listaSubActividades2.size() == 1)
				subActividad2 = listaSubActividades2.get(0);
			
			if (parent.getValorOpcion()) {
				noEsMineriaArtesanal = true;
			} else {
				noEsMineriaArtesanal = false;
			}
			return listaSubActividades2;
		case 3:
			listaSubActividades3 = listaSubActividades;
			if (listaSubActividades3.size() == 0) {
				requiereTablaResiduo3 = false;
				idSubactividades3 = null;
			}
			if (listaSubActividades3.size() == 1)
				subActividad3 = listaSubActividades3.get(0);
			
			if (parent.getValorOpcion()) {
				noEsMineriaArtesanal = true;
			} else {
				noEsMineriaArtesanal = false;
			}
			
			return listaSubActividades3;
		default:
			return listaSubActividades;
		}

	}

	public String nombreSubactividad(String subactividad) {
		String string = subactividad;
		String[] parts = string.split("<br/>");

		if (parts.length > 1) {
			String part2 = parts[1];
			return part2;
		} else {
			return string;
		}

	}

	public String headerSubActividad() {

		if (ciiuPrincipal.getCodigo().equals("B0810.22")
				|| ciiuPrincipal.getCodigo().equals("B0810.22.01")) {
			return "Volumen de Procesamiento";
		} else {
			return "Actividad: (" + ciiuPrincipal.getCodigo() + ") - "
					+ ciiuPrincipal.getNombre();
		}
	}

	public String headerSubActividadCom1() {
		if (ciiuComplementaria1.getCodigo().equals("B0810.22")
				|| ciiuComplementaria1.getCodigo().equals("B0810.22.01")) {
			return "Volumen de Procesamiento";
		} else {
			return "Actividad: (" + ciiuComplementaria1.getCodigo() + ") - "
					+ ciiuComplementaria1.getNombre();
		}
	}

	public String headerSubActividadCom2() {
		if (ciiuComplementaria2.getCodigo().equals("B0810.22")
				|| ciiuComplementaria2.getCodigo().equals("B0810.22.01")) {
			return "Volumen de Procesamiento";
		} else {
			return "Actividad: (" + ciiuComplementaria2.getCodigo() + ") - "
					+ ciiuComplementaria2.getNombre();
		}
	}

	public void cerrar() {
		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		DefaultRequestContext.getCurrentInstance().execute(
				"PF('mensajeMsp').hide();");
	}

	public void validateDatosSubActividades(FacesContext context,
			UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();

		if (listaSubActividades1.size() > 0
				&& subActividadesCiiuBean
						.esActividadRecupercionMateriales(ciiuPrincipal)) {
			Boolean residuos = residuosActividadesCiiuBean
					.getListaCiiuResiduos1().size() <= 0;
			errorMessages.addAll(subActividadesCiiuBean
					.validarDatosSubActividades(listaSubActividades1, residuos,
							1));
		}

		if (listaSubActividades2.size() > 0
				&& subActividadesCiiuBean
						.esActividadRecupercionMateriales(ciiuComplementaria1)) {
			Boolean residuos = residuosActividadesCiiuBean
					.getListaCiiuResiduos2().size() <= 0;
			errorMessages.addAll(subActividadesCiiuBean
					.validarDatosSubActividades(listaSubActividades2, residuos,
							2));
		}

		if (listaSubActividades3.size() > 0
				&& subActividadesCiiuBean
						.esActividadRecupercionMateriales(ciiuComplementaria2)) {
			Boolean residuos = residuosActividadesCiiuBean
					.getListaCiiuResiduos3().size() <= 0;
			errorMessages.addAll(subActividadesCiiuBean
					.validarDatosSubActividades(listaSubActividades3, residuos,
							3));
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

	private String combinacion1 = "";
	private String combinacion2 = "";
	private String combinacion3 = "";

	@Getter
	@Setter
	private boolean requiereTablaResiduo1, requiereTablaResiduo2,
			requiereTablaResiduo3;
	@EJB
	private CombinacionActividadesFacade combinacionActividadesFacade;

	@EJB
	private ProyectoCoaCiuuSubActividadesFacade proyectoCoaCiuuSubActividadesFacade;

	private CatalogoImportanciaVO actividadSubYCombi = new CatalogoImportanciaVO();

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
		this.idSubactividades1 = idSubactividades;
		List<String> numeroCombinacionesProcesos = new ArrayList<String>();
		listaProyectoCiiuSubActividad1 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		listaSubActividadSeleccionad1 = new ArrayList<SubActividades>();
		combinacion1 = "";
		SubActividades subactividad = null;
		for (Integer id : idSubactividades1) {
			for (SubActividades subA : listaSubActividades1) {
				if (subA.getId().intValue() == id.intValue()) {
					subactividad = subA;
					numeroCombinacionesProcesos.add(subactividad.getOrden()
							.toString());
					proyectoSubAct1 = new ProyectoCoaCiuuSubActividades();
					proyectoSubAct1.setSubActividad(subactividad);
					listaProyectoCiiuSubActividad1.add(proyectoSubAct1);
					listaSubActividadSeleccionad1.add(subA);
					break;
				}
			}
			if (subactividad != null) {
				subactividad.setSeleccionado(true);
			}
		}
		combinacion1 = StringUtils.join(numeroCombinacionesProcesos, ",");
		if (idSubactividades1.length > 1) {
			CombinacionSubActividades combinacionSubActividades = combinacionActividadesFacade
					.getPorCatalogoCombinacion(subactividad.getCatalogoCIUU(),
							combinacion1);
			requiereTablaResiduo1 = combinacionSubActividades
					.getRequiereIngresoResiduos() == null ? false
					: combinacionSubActividades.getRequiereIngresoResiduos();
		} else
			requiereTablaResiduo1 = subactividad.getRequiereIngresoResiduos() == null ? false
					: subactividad.getRequiereIngresoResiduos();
	}

	public void setIdSubactividades2(Integer[] idSubactividades) {
		this.idSubactividades2 = idSubactividades;
		List<String> numeroCombinacionesProcesos = new ArrayList<String>();
		listaProyectoCiiuSubActividad2 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		listaSubActividadSeleccionad2 = new ArrayList<SubActividades>();
		combinacion2 = "";
		SubActividades subactividad = null;
		for (Integer id : idSubactividades2) {
			for (SubActividades subA : listaSubActividades2) {
				if (subA.getId().intValue() == id.intValue()) {
					subactividad = subA;
					numeroCombinacionesProcesos.add(subactividad.getOrden()
							.toString());
					proyectoSubAct2 = new ProyectoCoaCiuuSubActividades();
					proyectoSubAct2.setSubActividad(subactividad);
					listaProyectoCiiuSubActividad2.add(proyectoSubAct2);
					listaSubActividadSeleccionad2.add(subA);
					break;
				}
			}
			if (subactividad != null) {
				subactividad.setSeleccionado(true);
			}
		}
		combinacion2 = StringUtils.join(numeroCombinacionesProcesos, ",");
		if (idSubactividades2.length > 1) {
			CombinacionSubActividades combinacionSubActividades = combinacionActividadesFacade
					.getPorCatalogoCombinacion(subactividad.getCatalogoCIUU(),
							combinacion2);
			requiereTablaResiduo2 = combinacionSubActividades
					.getRequiereIngresoResiduos() == null ? false
					: combinacionSubActividades.getRequiereIngresoResiduos();
		} else
			requiereTablaResiduo2 = subactividad.getRequiereIngresoResiduos() == null ? false
					: subactividad.getRequiereIngresoResiduos();
	}

	public void setIdSubactividades3(Integer[] idSubactividades) {
		this.idSubactividades3 = idSubactividades;
		List<String> numeroCombinacionesProcesos = new ArrayList<String>();
		listaProyectoCiiuSubActividad3 = new ArrayList<ProyectoCoaCiuuSubActividades>();
		listaSubActividadSeleccionad3 = new ArrayList<SubActividades>();
		combinacion3 = "";
		SubActividades subactividad = null;
		for (Integer id : idSubactividades3) {
			for (SubActividades subA : listaSubActividades3) {
				if (subA.getId().intValue() == id.intValue()) {
					subactividad = subA;
					numeroCombinacionesProcesos.add(subactividad.getOrden()
							.toString());
					proyectoSubAct3 = new ProyectoCoaCiuuSubActividades();
					proyectoSubAct3.setSubActividad(subactividad);
					listaProyectoCiiuSubActividad3.add(proyectoSubAct3);
					listaSubActividadSeleccionad3.add(subA);
					break;
				}
			}
			if (subactividad != null) {
				subactividad.setSeleccionado(true);
			}
		}
		combinacion3 = StringUtils.join(numeroCombinacionesProcesos, ",");
		if (idSubactividades3.length > 1) {
			CombinacionSubActividades combinacionSubActividades = combinacionActividadesFacade
					.getPorCatalogoCombinacion(subactividad.getCatalogoCIUU(),
							combinacion3);
			requiereTablaResiduo3 = combinacionSubActividades
					.getRequiereIngresoResiduos() == null ? false
					: combinacionSubActividades.getRequiereIngresoResiduos();
		} else
			requiereTablaResiduo3 = subactividad.getRequiereIngresoResiduos() == null ? false
					: subactividad.getRequiereIngresoResiduos();
	}
	
	public void nombresMspListener() {
		try {

			if (idTipoNombreMsp == 0) {
				otroMsp = true;
				mostrarTextArea = true;
			} else {
				mostrarTextArea = false;
				otroMsp = false;

				listaNombresProyectosMsp = new ArrayList<NombreProyectos>();
				listaNombresProyectosMsp = rucMspFacade
						.buscarProyectosPorTipoRuc(
								loginBean.getNombreUsuario(), idTipoNombreMsp);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void nivelesAtencion1() {
		try {
			listaSubActividadesHijas1 = new ArrayList<>();
			listaSubActividadesHijas1 = subActividadesFacade
					.actividadHijosPorPadre(padre1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void nivelesAtencion2() {
		try {
			listaSubActividadesHijas2 = new ArrayList<>();
			listaSubActividadesHijas2 = subActividadesFacade
					.actividadHijosPorPadre(padre2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void nivelesAtencion3() {
		try {
			listaSubActividadesHijas3 = new ArrayList<>();
			listaSubActividadesHijas3 = subActividadesFacade
					.actividadHijosPorPadre(padre3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void abrirPendienteAsignacion() {
		try {
			if (areaPendienteAsignar) {
				RequestContext.getCurrentInstance().update(
						"frmPreliminar:dlgAreaPendiente");

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dlgAreaPendiente').show();");

			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error en envío de notificación asignación área. "
					+ e.getCause() + " " + e.getMessage());
		}
	}

	public String getMensajeIntersecaMar() {
		String mensaje = "";
		List<String> areas = new ArrayList<>();
		if (areaPendienteAsignar) {
			for (UbicacionesGeografica ubi : coordenadasRcoaBean
					.getUbicacionesSeleccionadas()) {
				if (ubi.getIdRegion() != null && ubi.getIdRegion().equals(6)
						&& !ubi.getNombre().toUpperCase().contains("INSULAR")) {
					if (!areas.contains(ubi.getUbicacionesGeografica()
							.getNombre())) {
						areas.add(ubi.getUbicacionesGeografica().getNombre());
					}
				}
			}

			for (String nombre : areas) {
				mensaje += nombre + ", ";
			}

			if (mensaje != null && !mensaje.isEmpty()) {
				mensaje = mensaje.substring(0, mensaje.length() - 2);
			}

		}

		return mensaje;
	}

	public boolean validacionActividadMinera() {
		try {

			boolean existeActividad1 = false;
			boolean existeActividad2 = false;
			boolean existeActividad3 = false;

			if (proyecto.getCategorizacion() != 2) {
				return false;
			}

			if (ciiu1 != null) {
				if (ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.01")
						|| ciiu1.getCatalogoCIUU().getCodigo()
								.equals("B0990.01.01")
						|| ciiu1.getCatalogoCIUU().getCodigo()
								.equals("B0990.02")
						|| ciiu1.getCatalogoCIUU().getCodigo()
								.equals("B0990.02.01")
						|| ciiu1.getCatalogoCIUU().getCodigo()
								.equals("B0990.09")
						|| ciiu1.getCatalogoCIUU().getCodigo()
								.equals("B0990.09.01")) {
					existeActividad1 = true;
				}
			}

			if (ciiu2 != null && ciiu2.getCatalogoCIUU() != null) {
				if (ciiu2.getCatalogoCIUU().getCodigo().equals("B0990.01")
						|| ciiu2.getCatalogoCIUU().getCodigo()
								.equals("B0990.01.01")
						|| ciiu2.getCatalogoCIUU().getCodigo()
								.equals("B0990.02")
						|| ciiu2.getCatalogoCIUU().getCodigo()
								.equals("B0990.02.01")
						|| ciiu2.getCatalogoCIUU().getCodigo()
								.equals("B0990.09")
						|| ciiu2.getCatalogoCIUU().getCodigo()
								.equals("B0990.09.01")) {

					existeActividad2 = true;
				}
			}

			if (ciiu3 != null && ciiu3.getCatalogoCIUU() != null) {

				if (ciiu3.getCatalogoCIUU().getCodigo().equals("B0990.01")
						|| ciiu3.getCatalogoCIUU().getCodigo()
								.equals("B0990.01.01")
						|| ciiu3.getCatalogoCIUU().getCodigo()
								.equals("B0990.02")
						|| ciiu3.getCatalogoCIUU().getCodigo()
								.equals("B0990.02.01")
						|| ciiu3.getCatalogoCIUU().getCodigo()
								.equals("B0990.09")
						|| ciiu3.getCatalogoCIUU().getCodigo()
								.equals("B0990.09.01")) {

					existeActividad3 = true;
				}
			}

			if (existeActividad1 || existeActividad2 || existeActividad3) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public void cerrarDiagMineria() {

		DefaultRequestContext.getCurrentInstance().execute(
				"PF('mensajeMineria').hide();");

	}
	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> sustanciaQuimicaSeleccionadaTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();

	@Getter
	private List<RegistroSustanciaQuimica> listaRsqUsuario = new ArrayList<>();
	@Getter
	private List<RegistroSustanciaQuimica> listaRsqUsuarioSelec;

	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;

	private RegistroSustanciaQuimica rsqSeleccionado;

	@Getter
	@Setter
	private RegistroSustanciaQuimica registroSustanciasSelect = new RegistroSustanciaQuimica();

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
		esMercurio = false;
		esMercurioTransporta = false;
		esControlSustancia = false;
		tblSustanciaOtrosTransporta = false;
		tblSustanciaOtros = false;
		tblTieneRsq = false;

		for (SustanciaQuimicaPeligrosa item : sustanciaQuimicaSeleccionada) {
			if (item.getControlSustancia() != null
					&& item.getControlSustancia()) {
				esControlSustancia = true;
				if (item.getDescripcion().equals("Mercurio")) {
					esMercurio = true;
				}

			}
		}

		for (SustanciaQuimicaPeligrosa item : sustanciaQuimicaSeleccionadaTransporta) {
			if (item.getControlSustancia() != null
					&& item.getControlSustancia()) {
				esControlSustancia = true;
				if (item.getDescripcion().equals("Mercurio")) {
					esMercurioTransporta = true;

				}
			}
		}

		if (!listaSustanciaQuimicaSeleccionadaOtros.isEmpty()) {
			tblSustanciaOtros = true;
		}

		if (!listaSustanciaQuimicaSeleccionadaOtrosTransporta.isEmpty()) {
			tblSustanciaOtrosTransporta = true;
		}

		if (!esControlSustancia) {
			rsqVigente = false;
			tieneDocumentoRsq = null;
			limpiarRsq();
		} else {
			cargarListaRsqUsuario();
		}

		if (esControlSustancia == false) {
			if ((proyecto.getSustanciasQuimicas() == null || proyecto
					.getSustanciasQuimicas() == false)
					&& (proyecto.getTransportaSustanciasQuimicas() == null || proyecto
							.getTransportaSustanciasQuimicas() == false)) {
				proyecto.setCodigoRsqAsociado(null);
			}
		}
	}

	public void cargarListaRsqUsuario() {
		if (tieneDocumentoRsq != null && tieneDocumentoRsq) {
			listaRsqUsuario = registroSustanciaQuimicaFacade
					.obtenerRegistrosSustanciasPorUsuario(JsfUtil
							.getLoggedUser());
			rsqVigente = true;
			tblTieneRsq = true;
		} else if (tieneDocumentoRsq != null && tieneDocumentoRsq == false) {
			rsqVigente = false;
			tblTieneRsq = false;
			limpiarRsq();
		}

	}

	public void limpiarTransportaSustanciasQuimicas() {
		rsqSeleccionado = null;
		sustanciaQuimicaSeleccionadaTransporta.clear();
		if (!proyecto.getTransportaSustanciasQuimicas()) {
			sustanciaQuimicaSeleccionadaTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
			listaSustanciaQuimicaSeleccionadaOtrosTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
			tblSustanciaOtrosTransporta = false;
			esMercurioTransporta = false;

		}
		controlSustancia();
	}

	public void valorRequisitos() {

		RegistroSustanciaQuimica rsq = registroSustanciasSelect;
		rsqSeleccionado = rsq;
		proyecto.setCodigoRsqAsociado(rsq.getNumeroAplicacion());
		for (RegistroSustanciaQuimica item : listaRsqUsuario) {
			item.setSeleccionado(item.getId().intValue() == rsq.getId()
					.intValue());
		}
	}

	public void limpiarFabricaSustanciasQuimicas() {
		rsqSeleccionado = null;
		sustanciaQuimicaSeleccionada.clear();
		if (!proyecto.getSustanciasQuimicas()) {
			sustanciaQuimicaSeleccionada = new ArrayList<SustanciaQuimicaPeligrosa>();
			listaSustanciaQuimicaSeleccionadaOtros = new ArrayList<SustanciaQuimicaPeligrosa>();
			tblSustanciaOtros = false;
			esMercurio = false;

		}
		controlSustancia();
	}

	public void agregarSustanciaOtros() {
		tblSustanciaOtros = true;
		SustanciaQuimicaPeligrosa sustanciaCatalogo = new SustanciaQuimicaPeligrosa();
		sustanciaCatalogo = proyectoLicenciaCoaFacade
				.buscaSustanciasQuimicas(sustanciaOtros);
		if (sustanciaCatalogo.getId() == null) {
			SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtros = new SustanciaQuimicaPeligrosa();
			if (sustanciaOtros.isEmpty()) {
				JsfUtil.addMessageError("Especifique la sustancia química.");
				if (listaSustanciaQuimicaSeleccionadaOtros.size() == 0) {
					tblSustanciaOtros = false;
				}
			} else {
				sustanciaQuimicaSeleccionadaOtros
						.setDescripcion(sustanciaOtros);
				sustanciaQuimicaSeleccionadaOtros
						.setId(Constantes.ID_SUSTANCIA_OTROS);
				if (!contains(listaSustanciaQuimicaSeleccionadaOtros,
						sustanciaQuimicaSeleccionadaOtros)
						&& !contains(sustanciaQuimicaSeleccionada,
								sustanciaQuimicaSeleccionadaOtros)) {
					listaSustanciaQuimicaSeleccionadaOtros
							.add(sustanciaQuimicaSeleccionadaOtros);
				} else {
					JsfUtil.addMessageError("La sustancia ya fue ingresada.");
				}
			}
		} else {
			JsfUtil.addMessageError("La sustancia ingresada existe en el catálogo, por favor seleccione la sustancia en el catálogo.");
		}
		sustanciaOtros = "";
	}

	public void eliminarSustanciaOtros(SustanciaQuimicaPeligrosa sustancia) {
		listaSustanciaQuimicaSeleccionadaOtros.remove(sustancia);
		if (listaSustanciaQuimicaSeleccionadaOtros.isEmpty()) {
			tblSustanciaOtros = false;
		}
	}

	public void agregarSustanciaOtrosTransporta() {
		tblSustanciaOtrosTransporta = true;
		// verifico si la sustancia ingresada existe en el catalogo
		SustanciaQuimicaPeligrosa sustanciaCatalogo = new SustanciaQuimicaPeligrosa();
		sustanciaCatalogo = proyectoLicenciaCoaFacade
				.buscaSustanciasQuimicas(sustanciaOtrosTransporta);
		if (sustanciaCatalogo.getId() == null) {
			SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtrosTransporta = new SustanciaQuimicaPeligrosa();
			if (sustanciaOtrosTransporta.isEmpty()) {
				JsfUtil.addMessageError("Especifique la sustancia química.");
				if (listaSustanciaQuimicaSeleccionadaOtrosTransporta.size() == 0) {
					tblSustanciaOtrosTransporta = false;
				}
			} else {
				sustanciaQuimicaSeleccionadaOtrosTransporta
						.setDescripcion(sustanciaOtrosTransporta);
				sustanciaQuimicaSeleccionadaOtrosTransporta
						.setId(Constantes.ID_SUSTANCIA_OTROS);
				if (!contains(listaSustanciaQuimicaSeleccionadaOtrosTransporta,
						sustanciaQuimicaSeleccionadaOtrosTransporta)
						&& !contains(sustanciaQuimicaSeleccionadaTransporta,
								sustanciaQuimicaSeleccionadaOtrosTransporta)) {
					listaSustanciaQuimicaSeleccionadaOtrosTransporta
							.add(sustanciaQuimicaSeleccionadaOtrosTransporta);
				} else {
					JsfUtil.addMessageError("La sustancia ya fue ingresada.");
				}
			}
		} else {
			JsfUtil.addMessageError("La sustancia ingresada existe en el catálogo, por favor seleccione la sustancia en el catálogo.");
		}
		sustanciaOtrosTransporta = "";
	}

	public void eliminarSustanciaOtrosTransporta(
			SustanciaQuimicaPeligrosa sustancia) {
		listaSustanciaQuimicaSeleccionadaOtrosTransporta.remove(sustancia);
		if (listaSustanciaQuimicaSeleccionadaOtrosTransporta.isEmpty()) {
			tblSustanciaOtrosTransporta = false;
		}
	}

	public void limpiarRsq() {
		registroSustanciasSelect = new RegistroSustanciaQuimica();
		rsqSeleccionado = null;
		proyecto.setCodigoRsqAsociado(null);
	}

	private static boolean contains(
			List<SustanciaQuimicaPeligrosa> listaSustanciaQuimicaSeleccionadaOtros,
			SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtros) {
		for (SustanciaQuimicaPeligrosa e : listaSustanciaQuimicaSeleccionadaOtros) {
			if ((e.getDescripcion().toLowerCase()
					.equals(sustanciaQuimicaSeleccionadaOtros.getDescripcion()
							.toLowerCase()))) {
				return true;
			}
		}
		return false;
	}

	public void validarCodigoCamaronera() {
		if(coordenadasRcoaBean.getIntersecaConCamaronera() != null
				&& coordenadasRcoaBean.getIntersecaConCamaronera() == true ) {
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
	private Boolean esMineriaArtesanal1 = false, esMineriaArtesanal2 = false,
			esMineriaArtesanal3 = false;

	@Getter
	@Setter
	private Boolean noEsMineriaArtesanal = false;

	public void limpiarSubactividad(Integer nroActividad) {
		switch (nroActividad) {
		case 1:
			if (subActividad1 != null
					&& subActividad1.getRequiereRegimenMinero()) {
				ciiu1.setTipoRegimenMinero(null);
			}
			
			/** Para mostrar contrato minero **/
			esMineriaArtesanal1 = false;
			if (subActividad1 != null
					&& subActividad1.getNombre() != null
					&& subActividad1.getNombre().equals(
							"¿Su actividad es de minería ARTESANAL?")
					&& subActividad1.getValorOpcion()) {
				esMineriaArtesanal1 = true;
			}
			
			break;
		case 2:
			if (subActividad2 != null
					&& subActividad2.getRequiereRegimenMinero()) {
				ciiu2.setTipoRegimenMinero(null);
			}
			
			esMineriaArtesanal2 = false;
			if (subActividad2 != null
					&& subActividad2.getNombre() != null
					&& subActividad2.getNombre().equals(
							"¿Su actividad es de minería ARTESANAL?")
					&& subActividad2.getValorOpcion()) {
				esMineriaArtesanal2 = true;
			}
			
			break;
		case 3:
			if (subActividad3 != null
					&& subActividad3.getRequiereRegimenMinero()) {
				ciiu3.setTipoRegimenMinero(null);
			}
			
			esMineriaArtesanal3 = false;
			if (subActividad3 != null
					&& subActividad3.getNombre() != null
					&& subActividad3.getNombre().equals(
							"¿Su actividad es de minería ARTESANAL?")
					&& subActividad3.getValorOpcion()) {
				esMineriaArtesanal3 = true;
			}
			
			break;
		default:
			break;
		}
		
		if (!esMineriaArtesanal1 && !esMineriaArtesanal2
				&& !esMineriaArtesanal3) {
			noEsMineriaArtesanal = true;
			mostrarPoseeContrato = false;
			listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
			concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
			esContratoOperacion = false;
			tieneContratoMineria = null;
		} else {
			noEsMineriaArtesanal = false;
			mostrarPoseeContrato = true;
			listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
			concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
			tieneContratoMineria = null;
			esContratoOperacion = false;
		}
	}
	
	public void descargarMapaInterseccion() {
		try {
			List<SimuladorDocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(
					proyecto.getId(),
					TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA,
					"SimuladorProyectoLicenciaCoa");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean
						.downloadDocumentById(listaDocumentos.get(0)
								.getIdAlfresco()), "Documento MAPA");
			}
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public void getDetalleIntersecciones() {
		mostrarDetalleInterseccion = false;
		zonasInterccionDetalle = "";

		HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> lista = listaCapasInterseccionPrincipal;

		if (lista.size() > 0) {
			zonasInterccionDetalle = "Su proyecto obra o actividad interseca con: ";
			zonasInterccionDetalle = zonasInterccionDetalle + "<ul>";
			String nombreCapa = "";
			for (SimuladorInterseccionProyectoLicenciaAmbiental i : lista
					.keySet()) {
				nombreCapa = i.getDescripcionCapa();
				String nombreInterseccion = "";
				for (DetalleInterseccionProyectoAmbiental j : lista.get(i)) {
					nombreInterseccion += (nombreInterseccion == "") ? j
							.getNombreGeometria() : ","
							+ j.getNombreGeometria();
				}
				zonasInterccionDetalle = zonasInterccionDetalle + "<li>"
						+ nombreCapa + ": " + nombreInterseccion + "</li>";
			}
			zonasInterccionDetalle = zonasInterccionDetalle + "</ul>";
		}

		if (zonasInterccionDetalle != "")
			mostrarDetalleInterseccion = true;

		if (estadoZonaIntangible) {
			mostrarDetalleInterseccion = true;
		}
		// if(estadoFrontera || estadoZonaIntangible){
		// mostrarDetalleInterseccion = true;
		// }
	}
	
	public boolean bloqueoGad(CatalogoCIUU objCat) {
		bloquearGad = false;

		for (UbicacionesGeografica ubiCiu : coordenadasRcoaBean.getUbicacionesSeleccionadas()) {
			if(ubiCiu.getEnteAcreditado() != null) {
				//String gad = ubiCiu.getEnteAcreditado().getAreaAbbreviation();
				//System.out.println(gad);
				//Ticket#10428871
				/*if (gad.equals("GADPN")) {
					// actividades ciiu para bloqueo
					if (objCat.getCodigo().equals("G4730.01") || objCat.getCodigo().equals("G4730.01.01")
							|| objCat.getCodigo().equals("J6120.01.01") || objCat.getCodigo().equals("J6120.01.02")) {
	
						bloquearGad = true;
					}
	
				}*/
				
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
		      	// si las coordenadas intersecan y la sub actividad es Líneas de
				// Transmisión menor o igual a 230 KV con una longitud de 10 km y
				// Subestaciones, se debe cambiar el tipo a licencia
				if ((coordenadasRcoaBean.getIntersecaBiosfera() || coordenadasRcoaBean
						.getInterseca())
						&& actividadBloque1
								.getNombre()
								.equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION_MAYOR)) {
					actividadBloque1.setTipoPermiso("3");
					esSubactividadSubestacionElectrica = true;
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
	          	if(coordenadasRcoaBean.getInterseca() || coordenadasRcoaBean.getIntersecaBiosfera()) {
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
				listaSubActividades1 = subActividadesFacade
						.listaXactividad(actividadCiiu);
				subOpciones1 = true;
				break;
			case 2:
				listaSubActividades1 = subActividadesFacade.listaXactividad(actividadCiiu);
				subOpciones1 = true;
				break;
			case 4:
				Integer nroBloques = subActividadesFacade
						.getNumeroBloques(actividadCiiu);
				listaSubActividadesPorBloque1 = new ArrayList<>();

				for (int i = 1; i <= nroBloques; i++) {
					listaSubActividadesPorBloque1.add(subActividadesFacade
							.actividadesPadrePorBloque(actividadCiiu, i));
				}

				for (SubactividadDto b : listaSubActividadesPorBloque1) {
					if (b.getSubActividades().get(0).getFinanciadoBancoEstado() != null
							&& b.getSubActividades().get(0)
									.getFinanciadoBancoEstado()) {
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
				listaSubActividades2 = subActividadesFacade
						.listaXactividad(actividadCiiu);
				subOpciones2 = true;
				break;
			case 2:
				listaSubActividades2 = subActividadesFacade.listaXactividad(actividadCiiu);
				subOpciones2 = true;
				break;
			case 4:
				Integer nroBloques = subActividadesFacade
						.getNumeroBloques(actividadCiiu);
				listaSubActividadesPorBloque2 = new ArrayList<>();

				for (int i = 1; i <= nroBloques; i++) {
					listaSubActividadesPorBloque2.add(subActividadesFacade
							.actividadesPadrePorBloque(actividadCiiu, i));
				}

				for (SubactividadDto b : listaSubActividadesPorBloque2) {
					if (b.getSubActividades().get(0).getFinanciadoBancoEstado() != null
							&& b.getSubActividades().get(0)
									.getFinanciadoBancoEstado()) {
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
				listaSubActividades3 = subActividadesFacade
						.listaXactividad(actividadCiiu);
				subOpciones3 = true;
				break;
			case 2:
				listaSubActividades3 = subActividadesFacade.listaXactividad(actividadCiiu);
				subOpciones3 = true;
				break;
			case 4:
				Integer nroBloques = subActividadesFacade
						.getNumeroBloques(actividadCiiu);
				listaSubActividadesPorBloque3 = new ArrayList<>();

				for (int i = 1; i <= nroBloques; i++) {
					listaSubActividadesPorBloque3.add(subActividadesFacade
							.actividadesPadrePorBloque(actividadCiiu, i));
				}

				for (SubactividadDto b : listaSubActividadesPorBloque3) {
					if (b.getSubActividades().get(0).getFinanciadoBancoEstado() != null
							&& b.getSubActividades().get(0)
									.getFinanciadoBancoEstado()) {
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
		}
    }
    
    public void validarSeleccionEscombreras(SubActividades padre,
			Integer tipoActividad) {
		if (padre.getCatalogoCIUU().getTipoPregunta().equals(4)) {

			if (tipoActividad.equals(1)) {
				ingresoViabilidadTecnica1 = true;
			} else if (tipoActividad.equals(2)) {
				ingresoViabilidadTecnica2 = true;
			} else if (tipoActividad.equals(3)) {
				ingresoViabilidadTecnica3 = true;
			}

			if (padre.getTieneInformacionAdicional() != null
					&& padre.getTieneInformacionAdicional().equals(4)) {
				Boolean bloquearEscombreras = false;

				Organizacion orga = null;
				try {
					orga = organizacionFacade.buscarPorRuc(loginBean
							.getNombreUsuario());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				if (orga == null) {
					bloquearEscombreras = true;
				} else {
					OrganizacionViabilidadTecnica orgaViabilidad = organizacionViabilidadTecnicaFacade
							.getOrganizacionPorTipoProceso(orga, "tipo.proceso.viabilidad.tecnica.escombreras");
					if (orgaViabilidad == null) {
						bloquearEscombreras = true;
					} else {
						bloquearEscombreras = false;

						if (orgaViabilidad
								.getTipoEntidad()
								.getValor()
								.equals(OrganizacionViabilidadTecnica.TIPO_ENTIDAD_MINISTERIO)
								|| orgaViabilidad
										.getTipoEntidad()
										.getValor()
										.equals(OrganizacionViabilidadTecnica.TIPO_ENTIDAD_GOBIERNO_PROVINCIAL)
								|| orgaViabilidad
										.getTipoEntidad()
										.getValor()
										.equals(OrganizacionViabilidadTecnica.TIPO_ENTIDAD_EMPRESA_PROVINCIAL)) {
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

				if (bloquearEscombreras) {
					RequestContext context = RequestContext
							.getCurrentInstance();
					context.execute("PF('dlgBloqueoEscombreras').show();");

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
					if (subActividad1.getSubActividades() != null
							&& subActividad1.getSubActividades().getId() != null) {
						listaSubActividadesHijas1 = new ArrayList<>();

						parent1 = subActividadesFacade
								.actividadParent(subActividad1
										.getSubActividades().getId());
						listaSubActividadesHijas1 = subActividadesFacade
								.actividadHijosPorPadre(parent1);

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
					if (subActividad2.getSubActividades() != null
							&& subActividad2.getSubActividades().getId() != null) {
						listaSubActividadesHijas2 = new ArrayList<>();

						parent2 = subActividadesFacade
								.actividadParent(subActividad2
										.getSubActividades().getId());
						listaSubActividadesHijas2 = subActividadesFacade
								.actividadHijosPorPadre(parent2);

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
					if (subActividad3.getSubActividades() != null
							&& subActividad3.getSubActividades().getId() != null) {
						listaSubActividadesHijas3 = new ArrayList<>();

						parent3 = subActividadesFacade
								.actividadParent(subActividad3
										.getSubActividades().getId());
						listaSubActividadesHijas3 = subActividadesFacade
								.actividadHijosPorPadre(parent3);

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
					tipoPermisoActividad = Integer.valueOf(parent1
							.getTipoPermiso());
					importCatalogo.setCatalogo(parent1.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade
							.catalogoCiuu(
									parent1.getCatalogoCIUU().getCodigo()
											.substring(0, 7))
							.getImportanciaRelativa());
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
					tipoPermisoActividad = Integer.valueOf(subActividad1
							.getTipoPermiso());
					importCatalogo.setCatalogo(subActividad1.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade
							.catalogoCiuu(
									subActividad1.getCatalogoCIUU().getCodigo()
											.substring(0, 7))
							.getImportanciaRelativa());
					importCatalogo.setWf("wf1");
				} else if (parent1 != null && parent1.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent1
							.getTipoPermiso());
					importCatalogo.setCatalogo(parent1.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade
							.catalogoCiuu(
									parent1.getCatalogoCIUU().getCodigo()
											.substring(0, 7))
							.getImportanciaRelativa());
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
					tipoPermisoActividad = Integer.valueOf(parent2
							.getTipoPermiso());
					importCatalogo.setCatalogo(parent2.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade
							.catalogoCiuu(
									parent2.getCatalogoCIUU().getCodigo()
											.substring(0, 7))
							.getImportanciaRelativa());
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
					tipoPermisoActividad = Integer.valueOf(subActividad2
							.getTipoPermiso());
					importCatalogo.setCatalogo(subActividad2.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade
							.catalogoCiuu(
									subActividad2.getCatalogoCIUU().getCodigo()
											.substring(0, 7))
							.getImportanciaRelativa());
					importCatalogo.setWf("wf2");
				} else if (parent2 != null && parent2.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent2
							.getTipoPermiso());
					importCatalogo.setCatalogo(parent2.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade
							.catalogoCiuu(
									parent2.getCatalogoCIUU().getCodigo()
											.substring(0, 7))
							.getImportanciaRelativa());
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
					tipoPermisoActividad = Integer.valueOf(parent3
							.getTipoPermiso());
					importCatalogo.setCatalogo(parent3.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade
							.catalogoCiuu(
									parent3.getCatalogoCIUU().getCodigo()
											.substring(0, 7))
							.getImportanciaRelativa());
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
					tipoPermisoActividad = Integer.valueOf(subActividad3
							.getTipoPermiso());
					importCatalogo.setCatalogo(subActividad3.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade
							.catalogoCiuu(
									subActividad3.getCatalogoCIUU().getCodigo()
											.substring(0, 7))
							.getImportanciaRelativa());
					importCatalogo.setWf("wf3");
				} else if (parent3 != null && parent3.getId() != null) {
					tipoPermisoActividad = Integer.valueOf(parent3
							.getTipoPermiso());
					importCatalogo.setCatalogo(parent3.getCatalogoCIUU());
					importCatalogo.setTipoPermiso(tipoPermisoActividad);
					importCatalogo.setImportancia(catalogoCIUUFacade
							.catalogoCiuu(
									parent3.getCatalogoCIUU().getCodigo()
											.substring(0, 7))
							.getImportanciaRelativa());
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

	public void esContratoMinero() {
		if (tieneContratoMineria) {
			esContratoOperacion = true;
			noEsMineriaArtesanal = false;
			concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
			listaConcesionesMineras = new ArrayList<>();
		} else {
			esContratoOperacion = false;
			noEsMineriaArtesanal = true;
			listaConcesionesMineras = new ArrayList<>();
			concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
		}
	}

	public void validarCodigoMineroContratoOperacion() {
		try {
			/**
			 * Primero se va a validar si es un ćodigo de conseción o un
			 * contrato de operacion, para obtener el codigo de concesión para
			 * las siguientes validaciones si el código es un contrato de
			 * operación
			 */
			/**
			 * en el método verificar poligono se debe llenar el objeto de
			 * concesión minera y valida las coordenadas de implantación con las
			 * coordenadas del archivo para saber si las coordenadas son de un
			 * contrato.
			 **/
			coordenadasRcoaBean.verificarPoligono();

			if (coordenadasRcoaBean.getPoligonosIguales()) {
				JsfUtil.addMessageError("Coordenadas no se encuentran dentro de la concesión minera la cual le otorgó el respectivo contrato de operación.");
				return;
			}

			/**
			 * Una vez validadas las coordenadas de implantación con el archivo
			 * si estás coinciden con algún valor entonces obtenemos el código
			 * de la concesión minera
			 */
			if (coordenadasRcoaBean.getConcesionMinera() != null
					&& coordenadasRcoaBean.getConcesionMinera().getCodigo() != null) {
				Boolean esTitular = false;
				Boolean tienePermiso = false;

				/**
				 * Validamos si el contrato todavía está vigente
				 */
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

				Date date = format.parse(coordenadasRcoaBean
						.getConcesionMinera().getFechaSuscripcion());

				Integer meses = Integer.parseInt(coordenadasRcoaBean
						.getConcesionMinera().getMesesPlazo());

				Calendar calFechaSus = Calendar.getInstance();
				calFechaSus.setTime(date);
				calFechaSus.add(Calendar.MONTH, meses);

				Date fechaVencimiento = calFechaSus.getTime();

				Calendar calFechaActual = Calendar.getInstance();
				Date fechaActual = calFechaActual.getTime();

				boolean contratoTerminado = false;

				/**
				 * Validamos el código ingresado para conocer si el operador
				 * ingreso el código de concesión o el código del contrato
				 */
				if (coordenadasRcoaBean.getConcesionMinera().getCodigo()
						.equals(concesionMinera.getCodigo())) {
					tienePermiso = true;
				} else if (coordenadasRcoaBean.getConcesionMinera()
						.getCodigoContrato()
						.equals(concesionMinera.getCodigo())) {
					tienePermiso = true;
				}

				/**
				 * Se valida si el operador logueado tiene el mismo ruc asociado
				 * al contrato.
				 */
				if (coordenadasRcoaBean.getConcesionMinera().getRucOperador() != null
						&& coordenadasRcoaBean.getConcesionMinera()
								.getRucOperador()
								.equals(loginBean.getNombreUsuario())) {
					esTitular = true;
				} else if (coordenadasRcoaBean.getConcesionMinera()
						.getNombreOperador() != null
						&& coordenadasRcoaBean
								.getConcesionMinera()
								.getNombreOperador()
								.equals(loginBean.getUsuario().getPersona()
										.getNombre())) {
					esTitular = true;
				}

				if (!esTitular) {
					JsfUtil.addMessageError("El Código de concesión minera no pertenece a este usuario");
					return;
				}

				if (tienePermiso) {

					if (fechaVencimiento.before(fechaActual)) {
						contratoTerminado = true;
					}

					if (contratoTerminado) {
						JsfUtil.addMessageError("El tiempo de vigencia del contrato ha terminado");
						return;
					}

					/**
					 * Se valida las coordenadas del arcom con las coordenadas
					 * geográficas que ingreso el operador
					 */
					coordenadasRcoaBean
							.verificarCoordenadasGeograficasYArcom(coordenadasRcoaBean
									.getConcesionMinera().getCodigo());

					if (coordenadasRcoaBean.getConcesionMinera()
							.getCoordenadasCoinciden()) {
						concesionMinera.setNombre(coordenadasRcoaBean
								.getConcesionMinera().getNombre());
						concesionMinera.setRegimen(coordenadasRcoaBean
								.getConcesionMinera().getRegimen());
						concesionMinera.setArea(coordenadasRcoaBean
								.getConcesionMinera().getArea());
						concesionMinera.setMaterial(coordenadasRcoaBean
								.getConcesionMinera().getMaterial());
						concesionMinera.setTieneContrato(true);
						listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
						listaConcesionesMineras.add(concesionMinera);

						JsfUtil.addMessageInfo("El código minero o contrato de operación fue validado con éxito.");

					} else {
						JsfUtil.addMessageError("Coordenadas no coinciden con el área geográfica de la concesión minera que le otorgó el contrato de operación");
						return;
					}
				} else {
					JsfUtil.addMessageError("Coordenadas no se encuentran dentro de la concesión minera la cual le otorgó el respectivo contrato de operación.");
					return;
				}
			} else {
				JsfUtil.addMessageError("Coordenadas del área de implantación ingresadas no coinciden con las coordenadas del contrato de operación otorgado en la presente concesión minera");
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Boolean validarCategorizacionPorInterseccion(
			ValorCalculo valoresFormula, Area zonal,
			Area enteAcreditadoProyecto, UbicacionesGeografica provincia) {
		Boolean saltar = false;
		// validar si la actividad ciiu principal tiene validacion por capas
		CatalogoCapaPermiso capaPermiso = catalogoCapaPermisoFacade
				.getCapaPermisoPorProyectoCiiu(proyecto.getId(),
						ciiuArearesponsable.getCatalogoCIUU().getId());
		if (capaPermiso != null && capaPermiso.getId() != null) {
			ciiuArearesponsable.setIdCatalogoCapaPermiso(capaPermiso.getId());
			proyectoLicenciaCuaCiuuFacade.guardar(ciiuArearesponsable);

			if (capaPermiso.getTipoPermiso() != null
					&& capaPermiso.getTipoPermiso().getId() != null) {
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
				valoresFormula
						.setCategorizacionAlternativa(categoriaXNormativa);
				valorCalculoFacade.guardar(valoresFormula);
			}

			if (capaPermiso.getTipoAutoridadAmbiental() != null) {
				tipoAutoridadAmbiental = capaPermiso
						.getTipoAutoridadAmbiental();
				switch (tipoAutoridadAmbiental) {
				case 2:
					proyecto.setAreaResponsable(zonal);
					proyecto.setAreaInventarioForestal(proyecto
							.getAreaResponsable());
					proyecto = proyectoLicenciaCoaFacade.guardar(proyecto);
					saltar = true;
					break;
				case 3:
					if (enteAcreditadoProyecto != null) {
						if (enteAcreditadoProyecto.getTipoEnteAcreditado()
								.equals("MUNICIPIO")) {
							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(3, provincia) == null ? zonal
									: areaService.getAreaGadProvincial(3,
											provincia));
							proyecto.setAreaInventarioForestal(zonal);
							proyecto = proyectoLicenciaCoaFacade
									.guardar(proyecto);
							saltar = true;
						} else if (enteAcreditadoProyecto
								.getTipoEnteAcreditado().equals("GOBIERNO")) {
							proyecto.setAreaResponsable(zonal);
							proyecto.setAreaInventarioForestal(proyecto
									.getAreaResponsable());
							proyecto = proyectoLicenciaCoaFacade
									.guardar(proyecto);
							saltar = true;
						}
					} else {
						if (coordenadasRcoaBean.getUbicacionPrincipal()
								.getEnteAcreditado() == null) {
							proyecto.setAreaResponsable(areaService
									.getAreaGadProvincial(3, provincia) == null ? zonal
									: areaService.getAreaGadProvincial(3,
											provincia));
							proyecto.setAreaInventarioForestal(zonal);
							proyecto = proyectoLicenciaCoaFacade
									.guardar(proyecto);
							saltar = true;
						} else {
							proyecto.setAreaResponsable(coordenadasRcoaBean
									.getUbicacionPrincipal()
									.getEnteAcreditado() == null ? zonal
									: coordenadasRcoaBean
											.getUbicacionPrincipal()
											.getEnteAcreditado());
							proyecto.setAreaInventarioForestal(zonal);
							proyecto = proyectoLicenciaCoaFacade
									.guardar(proyecto);
							saltar = true;
						}
					}
					break;
				default:
					break;
				}
			}
		} else {
			if (ciiuArearesponsable.getIdCatalogoCapaPermiso() != null) {
				ciiuArearesponsable.setIdCatalogoCapaPermiso(null);
				proyectoLicenciaCuaCiuuFacade.guardar(ciiuArearesponsable);
			}
		}

		return saltar;
	}
	
	public void validarCoordenadasContiguas() {
		coordenadasRcoaBean.verificarCoordenadasContiguas();
		esValidacionZonaMixta = true;
	}

	public void zonaListener() {
		if (!zona.equals("MIXTA")) {
			esValidacionZonaMixta = false;
		}
		proyecto.setZona_camaronera(zona);
	}

	public Boolean validarRequiereIngresoViabilidadTecnica(
			CatalogoCIUU actividadCiiu, SubActividades subActividad,
			Integer tipoActividad) throws Exception {
		Boolean validar = false;
		if (actividadCiiu.getTipoPregunta() == null) {
			if (tipoActividad.equals(1) && ingresoViabilidadTecnica1) {
				validar = true;
			} else if (tipoActividad.equals(2) && ingresoViabilidadTecnica2) {
				validar = true;
			} else if (tipoActividad.equals(3) && ingresoViabilidadTecnica3) {
				validar = true;
			}
		} else if (actividadCiiu.getTipoPregunta().equals(4)) {
			if (subActividad.getTieneInformacionAdicional() != null
					&& !subActividad.getTieneInformacionAdicional().equals(4)) {
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
