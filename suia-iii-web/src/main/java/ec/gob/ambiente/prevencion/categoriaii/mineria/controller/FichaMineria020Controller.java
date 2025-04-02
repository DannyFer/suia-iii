package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ec.gob.ambiente.client.SuiaServicesArcon;
import ec.gob.ambiente.client.SuiaServices_Service_Arcon;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.categoria2.v2.controller.FichaAmbientalGeneralFinalizarControllerV2;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.consultores.facade.ConsultorRcoaFacade;
import ec.gob.ambiente.rcoa.consultores.model.ConsultorRcoa;
import ec.gob.ambiente.rcoa.consultores.model.EquipoMultidisciplinarioConsultor;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalConcesionesMinerasFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalConcesionesMineras;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoBioticoFacade;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoFisicoFacade;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoSocialFacade;
import ec.gob.ambiente.suia.catalogos.service.UnidadMedidaService;
import ec.gob.ambiente.suia.certificadointerseccion.service.CapasFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionService;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoViabilidadAmbientalInterseccionProyectoFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.desechoPeligroso.facade.DesechoPeligrosoFacade;
import ec.gob.ambiente.suia.domain.ActividadesImpactoProyecto;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Capa;
import ec.gob.ambiente.suia.domain.CatalogoGeneralBiotico;
import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.CategoriaFuenteDesechoPeligroso;
import ec.gob.ambiente.suia.domain.CategoriaIILicencia;
import ec.gob.ambiente.suia.domain.CertificadoInterseccion;
import ec.gob.ambiente.suia.domain.ComponenteBioticoSD;
import ec.gob.ambiente.suia.domain.ComponenteFisicoPendienteSD;
import ec.gob.ambiente.suia.domain.ComponenteFisicoSD;
import ec.gob.ambiente.suia.domain.ComponenteSocialSD;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.Consultor;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.DetalleInterseccionProyecto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.FuenteDesechoPeligroso;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PerforacionCoordenadas;
import ec.gob.ambiente.suia.domain.PerforacionCronogramaPma;
import ec.gob.ambiente.suia.domain.PerforacionDesechosNoPeligrosos;
import ec.gob.ambiente.suia.domain.PerforacionDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.PerforacionEquipoMultidisciplinario;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.PerforacionMaquinasEquipos;
import ec.gob.ambiente.suia.domain.PerforacionMaterialInsumo;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalProyecto;
import ec.gob.ambiente.suia.domain.PoliticaDesechosActividad;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.domain.TipoPlanManejoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.UnidadMedida;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityActividad;
import ec.gob.ambiente.suia.dto.EntityFichaMineria020Reporte;
import ec.gob.ambiente.suia.eia.descripcionProyecto.facade.DescripcionProyectoFacade;
import ec.gob.ambiente.suia.eia.ficha.facade.ConsultorCalificadoFacade;
import ec.gob.ambiente.suia.eia.pma.facade.TipoPlanManejoAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.ActividadesImpactoFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.ComponenteBioticoSDFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.ComponenteFisicoSDFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.ComponenteSocialSDFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.PlanManejoAmbiental020Facade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import ec.gob.arcom.dm.ws.Coordenadas;
import ec.gob.arcom.dm.ws.DerechoMineroMAEDTO;

@ManagedBean
@ViewScoped
public class FichaMineria020Controller {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FichaMineria020Controller.class);
	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";

	@EJB
	private DesechoPeligrosoFacade desechoPeligrosoFacade;

	@EJB
	private ConsultorRcoaFacade consultorRcoaFacade;

	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@EJB
	private TipoPlanManejoAmbientalFacade tipoPlanManejoAmbientalFacade;

	@EJB
	private ConsultorCalificadoFacade consultorCalificadoFacade;

	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;

	@EJB
	private UnidadMedidaService unidadService;

	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;

	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;

	@EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;

	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;

	@EJB
	private ProyectoLicenciaAmbientalConcesionesMinerasFacade proyectoConcesionesMinerasFacade;

	@EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalShapeFacade;

	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;

	@EJB
	private DocumentosCoaFacade documentosRcoaFacade;

	@EJB
	private CatalogoFisicoFacade catalogoFisicoFacade;

	@EJB
	private CatalogoBioticoFacade catalogoBioticoFacade;

	@EJB
	private CatalogoSocialFacade catalogoSocialFacade;

	@EJB
	private ComponenteBioticoSDFacade componenteBioticoFacade;

	@EJB
	private ContactoFacade contactoFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private ActividadesImpactoFacade actividadesImpactoFacade;

	@EJB
	private ComponenteFisicoSDFacade componenteFisicoFacade;

	@EJB
	private ComponenteSocialSDFacade componenteSocialFacade;

	@EJB
	private CertificadoViabilidadAmbientalInterseccionProyectoFacade certificadoViabilidadAmbientalInterseccionProyectoService;

	@EJB
	private PlanManejoAmbiental020Facade planManejoAmbiental020Facade;

	@EJB
	private DescripcionProyectoFacade descripcionProyectoFacade;

	@EJB
	private CertificadoInterseccionService certificadoInterseccionService;

	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionFacade;

	@EJB
	private CapasFacade capasFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Setter
	protected CompleteOperation completeOperationOnDelete;
	@Setter
	protected CompleteOperation completeOperationOnAdd;
	@Getter
	@Setter
	private boolean edicionInsumo = false;
	@Setter
	private String filter;
	@Getter
	@Setter
	private PerforacionCronogramaPma perforacionCronogramaPma;
	@Getter
	@Setter
	private List<PerforacionCronogramaPma> listaCronogramaPma = new ArrayList<PerforacionCronogramaPma>();
	@Getter
	@Setter
	private List<ConsultorRcoa> listadoConsultores = new ArrayList<ConsultorRcoa>();
	@Getter
	@Setter
	private Consultor consultor = new Consultor();
	@Getter
	@Setter
	private ConsultorRcoa consultorRcoa = new ConsultorRcoa();
	@Getter
	@Setter
	private List<Consultor> listconsultores = new ArrayList<Consultor>();
	@Getter
	@Setter
	private PerforacionExplorativa perforacionExplorativa = new PerforacionExplorativa();
	@Getter
	@Setter
	private PerforacionCoordenadas coordendas = new PerforacionCoordenadas();
	@Getter
	@Setter
	private List<PerforacionCoordenadas> listaCoordenadas = new ArrayList<PerforacionCoordenadas>();
	@Getter
	@Setter
	private PerforacionEquipoMultidisciplinario equipoMultidisciplinario = new PerforacionEquipoMultidisciplinario();
	@Getter
	@Setter
	private List<PerforacionEquipoMultidisciplinario> listaEquipoMultidis = new ArrayList<PerforacionEquipoMultidisciplinario>();
	@Getter
	@Setter
	private List<PerforacionEquipoMultidisciplinario> listaEquipoMultidisEliminados = new ArrayList<PerforacionEquipoMultidisciplinario>();
	@Getter
	@Setter
	private List<UnidadMedida> listaUnidades = new ArrayList<UnidadMedida>();
	@Getter
	@Setter
	private List<UnidadMedida> listaUnidades20 = new ArrayList<UnidadMedida>();
	@Getter
	@Setter
	private UnidadMedida unidadSeleccionada = new UnidadMedida();
	@Getter
	@Setter
	private PerforacionMaquinasEquipos maquinariaEquipos = new PerforacionMaquinasEquipos();
	@Getter
	@Setter
	private List<PerforacionMaquinasEquipos> listaMaquinariaEquipos = new ArrayList<PerforacionMaquinasEquipos>();
	@Getter
	@Setter
	private PerforacionMaterialInsumo materialInsumo = new PerforacionMaterialInsumo();
	@Getter
	@Setter
	private List<PerforacionMaterialInsumo> listaMaterialInsumo = new ArrayList<PerforacionMaterialInsumo>();
	@Getter
	@Setter
	private PerforacionDesechosNoPeligrosos desechonopeligrosos = new PerforacionDesechosNoPeligrosos();
	@Getter
	@Setter
	private List<PerforacionDesechosNoPeligrosos> listadesechonopeligrosos = new ArrayList<PerforacionDesechosNoPeligrosos>();
	@Getter
	@Setter
	private PerforacionDesechosPeligrosos desechopeligrosos = new PerforacionDesechosPeligrosos();
	@Getter
	@Setter
	private List<PerforacionDesechosPeligrosos> listadesechopeligrosos = new ArrayList<PerforacionDesechosPeligrosos>();
	@Getter
	@Setter
	private Map<String, Integer> listaErrores;
	@Setter
	@Getter
	private TreeNode catalogo;
	@Setter
	@Getter
	private TreeNode catalogoSeleccionado;
	@Setter
	private List<DesechoPeligroso> desechosSeleccionados;
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto = new ProyectoLicenciamientoAmbiental();
	@Getter
	@Setter
	private boolean edicionDesechoPeligroso = false;
	@Getter
	@Setter
	private boolean edicionMaquinaria = false;
	@Getter
	@Setter
	private String parroquia = "";
	@Getter
	@Setter
	private String canton = "";
	@Getter
	@Setter
	private String provincia = "";
	@Getter
	@Setter
	private double total;
	@Getter
	@Setter
	private List<UnidadMedida> catalogoUnidadMedida;
	@Getter
	@Setter
	private Integer index = 1;
	@Getter
	@Setter
	private boolean panelErrores;
	@Getter
	@Setter
	private Boolean condiciones = false;
	@Getter
	@Setter
	private String email = "";
	@Getter
	@Setter
	private String correo = null;
	@Getter
	@Setter
	private String telefono = null;
	@Getter
	@Setter
	private String cell = "";
	@Getter
	@Setter
	private String titular = "";
	@Getter
	@Setter
	private String representanteLegal = "";
	@Getter
	@Setter
	private String direccion = "";
	@Getter
	@Setter
	private List<String> superficie = new ArrayList<String>();
	@Setter
	@Getter
	private String file;
	@Getter
	@Setter
	private String mensaje;
	@Getter
	@Setter
	private Map<String, List<PlanManejoAmbientalProyecto>> parameters = new HashMap<String, List<PlanManejoAmbientalProyecto>>();
	@Getter
	@Setter
	private List<EntityActividad> listaActividades;
	@Getter
	@Setter
	private List<ActividadesImpactoProyecto> listaActividadesImpactoPerndientes;
	@Getter
	@Setter
	private List<UnidadMedida> listaUnidadesMasa = new ArrayList<UnidadMedida>();
	@Getter
	@Setter
	private SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionada;
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> sustanciaQuimicasPeligrosas;
	@Getter
	@Setter
	private List<PerforacionMaterialInsumo> listaMaterialInsumoEliminados = new ArrayList<PerforacionMaterialInsumo>();
	@Getter
	@Setter
	private List<PerforacionMaquinasEquipos> listaMaquinariaEquiposEliminados = new ArrayList<PerforacionMaquinasEquipos>();
	@Getter
	@Setter
	private List<PerforacionDesechosNoPeligrosos> listaDesechoNoPeligrososEliminados = new ArrayList<PerforacionDesechosNoPeligrosos>();
	@Getter
	@Setter
	private List<PerforacionDesechosPeligrosos> listaDesechoPeligrososEliminados = new ArrayList<PerforacionDesechosPeligrosos>();
	@Getter
	@Setter
	private boolean edicionDesechoNoPeligroso = false;
	@Getter
	@Setter
	private DesechoPeligroso desechoPeligrosoSeleccionado;
	@Getter
	@Setter
	private CertificadoInterseccion ci;
	@Getter
	@Setter
	private List<Capa> capas;
	@Getter
	@Setter
	private String mensajePunto5 = "Deberá contener una síntesis de todo el proyecto en sus aspectos más relevantes, como la descripción de la fase del proyecto, las características del área de implantación del proyecto, etc.";
	@Setter
	@Getter
	private String mensajePunto4 = "Ejemplo: Con fecha xx de xx de xx, la Subsecretaría de Minas del ex Ministerio de Recursos Naturales No Renovables, resolvió otorgar a favor xxxxx, el título de concesión para minerales metálicos en el área denominada “xxxx (MAYUSCULAS)” código XX, ubicada en la parroquia xx, cantón xx, provincia xx.";
	@Setter
	@Getter
	private String mensajePunto41 = "Mediante oficio Nro. "
			+ Constantes.SIGLAS_INSTITUCION
			+ "-DNF-XXXX-XXXX de XX de XX de XX, la Dirección Nacional Forestal otorgó el Certificado de Viabilidad Ambiental a la concesión minera xxx (CÓDIGO xxxx).";
	@Setter
	@Getter
	private Boolean esProyectoRcoa;
	@Setter
	@Getter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	@Setter
	@Getter
	private List<ProyectoLicenciaAmbientalConcesionesMineras> concesionesMinerasRcoa;
	@Setter
	@Getter
	private ProyectoLicenciaCuaCiuu actividadPrincipal;
	@Getter
	@Setter
	private List<CoordendasPoligonos> coordenadasImplantacion;
	@Getter
	@Setter
	private EntityFichaMineria020Reporte entityFichaMineria020Reporte;
	@Getter
	@Setter
	private String certificado;
	@Getter
	@Setter
	private String identificacion;
	@Getter
	@Setter
	private String nombreConsultor;
	@Getter
	@Setter
	private Integer id;
	@Getter
	@Setter
	private ConsultorRcoa consultorSeleccionado;
	@Getter
	@Setter
	private List<EquipoMultidisciplinarioConsultor> listEquipoMultidisciplinario;
	@Getter
	@Setter
	private List<EquipoMultidisciplinarioConsultor> listaDelEquipo = new ArrayList<>();
	@Getter
	@Setter
	private List<ConsultorRcoa> listaConsultoresMostrar;
	@Getter
	@Setter
	private List<ConsultorRcoa> listconsultoresRcoa;
	@Getter
	@Setter
	private List<EquipoMultidisciplinarioConsultor> listaEquipoConsultor = new ArrayList<>();
	@Getter
	@Setter
	private List<DocumentosCOA> listDocumentosTituloMinero = new ArrayList<DocumentosCOA>();
	@Getter
	@Setter
	private List<DocumentosCOA> listDocumentosGarantia = new ArrayList<DocumentosCOA>();
	@Getter
	@Setter
	private List<DocumentosCOA> listDocumentosDeclaracion = new ArrayList<DocumentosCOA>();
	@Getter
	@Setter
	private List<DocumentosCOA> listDocumentosFacturas = new ArrayList<DocumentosCOA>();

	private Map<String, List<CatalogoGeneralBiotico>> listaGeneralBiotico;
	private List<CatalogoGeneralBiotico> coberturaVegetalList,
			pisosZooGeograficosList, componenteBioticoNativoList,
			componenteBioticoIntroducidoList, aspectosEcologicosList,
			areasSensiblesList, coberturaVegetalPadreList;
	private List<ComponenteBioticoSD> listaBioticoCoberturaVegetal,
			listaBioticoPisosZooGeograficos, listaBioticoNativo,
			listaBioticoIntroducido, listaBioticoAspectoEcologico,
			listaBioticoAreasSensibles;
	boolean editarCoordenada = false;
	boolean editarEquipo = false;
	private DerechoMineroMAEDTO derechoMinero = new DerechoMineroMAEDTO();
	private List<DesechoPeligroso> desechosPeligrosoDisponibles;
	private boolean vi;
	private FichaAmbientalMineria fichaAmbientalMineria = new FichaAmbientalMineria();

	@PostConstruct
	public void init() throws Exception {
		// catalogo
		// 21.02.03.05 EXPLORACIÓN INICIAL POR SONDEO
		// 21.02.04.03 EXPLORACIÓN INICIAL POR SONDEO
		// 21.02.05.03 EXPLORACIÓN INICIAL POR SONDEO
		// 21.02.02.03 EXPLORACIÓN INICIAL CON SONDEOS DE PRUEBA O
		// RECONOCIMIENTO
		// 21.02.02.01 EXPLORACIÓN INICIAL EN MEDIANA Y GRAN MINERÍA (METÁLICOS
		// Y NO METÁLICOS)

		esProyectoRcoa = false;
		if (proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null) {
			esProyectoRcoa = true;
			proyectoLicenciaCoa = proyectosBean.getProyectoRcoa();
			actividadPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal("MAATE-RA-2025-537238");
			coordenadasImplantacion = new ArrayList<CoordendasPoligonos>();
			List<ProyectoLicenciaAmbientalCoaShape> formasImplantacion = proyectoLicenciaAmbientalShapeFacade.buscarFormaGeograficaPorProyecto(proyectoLicenciaCoa, 1, 0); // coordenadas // implantacion

			if (formasImplantacion != null) {
				for (ProyectoLicenciaAmbientalCoaShape forma : formasImplantacion) {
					List<CoordenadasProyecto> coordenadasGeograficasImplantacion = coordenadasProyectoCoaFacade.buscarPorForma(forma);
					CoordendasPoligonos poligono = new CoordendasPoligonos();
					poligono.setCoordenadas(coordenadasGeograficasImplantacion);
					poligono.setTipoForma(forma.getTipoForma());
					coordenadasImplantacion.add(poligono);
				}
			}
		}

		proyecto = proyectosBean.getProyecto();
		// perforacionExplorativa=fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyecto);
		String certificadoInter = "";
		if (!esProyectoRcoa) {
			perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyectosBean.getProyecto());
			traerCertificado();
			certificadoInter = "Mediante oficio No. "
					+ ci.getCodigo()
					+ " del "
					+ getFechaCreacionCertificado(ci.getFechaCreacion())
					+ ", "
					+ "la Dirección Nacional de Prevención de la Contaminación Ambiental del Ministerio del Ambiente y Agua, "
					+ "emitió el certificado de intersección del proyecto "
					+ proyecto.getCodigo()
					+ ", "
					+ "ubicado en la provincia de "
					+ getUbicacionProyecto(proyecto.getUbicacionesGeograficas()).toUpperCase() 
					+ ", concluyendo que el proyecto "
					+ nombresIntersecciones(proyecto.getCodigo()) + "";
		} else {
			perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyectoLicenciaCoa.getId());
			CertificadoInterseccionOficioCoa ci = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
			List<UbicacionesGeografica> ubicacionProyectoLista = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa);

			certificadoInter = "Mediante oficio No. "
					+ ci.getCodigo()
					+ " del "
					+ getFechaCreacionCertificado(ci.getFechaCreacion())
					+ ", "
					+ "la Dirección de Regularización Ambiental del Ministerio de Ambiente y Agua, "
					+ "emitió el certificado de intersección del proyecto "
					+ proyectoLicenciaCoa.getCodigoUnicoAmbiental()
					+ ", "
					+ "ubicado en la provincia de "
					+ getUbicacionProyecto(ubicacionProyectoLista).toUpperCase() 
					+ ", concluyendo que el proyecto "
					+ nombresInterseccionesRcoa() + "";

			if (proyecto == null)
				proyecto = new ProyectoLicenciamientoAmbiental();

			proyecto.setUsuario(proyectoLicenciaCoa.getUsuario());
			proyecto.setNombre(proyectoLicenciaCoa.getNombreProyecto());

		}
		perforacionExplorativa.setIntersectionCertificate(certificadoInter);

		vi = getViabilidad();

		mensaje = getNotaResponsabilidadInformacionRegistroProyecto(loginBean.getUsuario());
		List<Contacto> contactos = contactoFacade.buscarUsuarioNativeQuery(String.valueOf(proyecto.getUsuario().getNombre()));
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)) {
				email = contacto.getValor();
			}
			if (contacto.getFormasContacto().getId().equals(FormasContacto.TELEFONO)) {
				telefono = contacto.getValor();
			}
			if (contacto.getFormasContacto().getId().equals(FormasContacto.CELULAR)) {
				cell = contacto.getValor();
			}
			if (contacto.getFormasContacto().getId().equals(FormasContacto.DIRECCION)) {
				direccion = contacto.getValor();
			}
			if (contacto.getOrganizacion() != null) {
				titular = contacto.getOrganizacion().getNombre();
				String org = proyecto.getUsuario().getNombre();
				Organizacion orgd = organizacionFacade.buscarPorRuc(org);
				representanteLegal = orgd.getPersona().getNombre();
			} else {
				titular = contacto.getPersona().getNombre();
				representanteLegal = contacto.getPersona().getNombre();
			}
		}

		if (!esProyectoRcoa) {
			if (proyecto.getConcesionesMineras().size() > 0) {
				// superficie.add(proyecto.getArea()+" "+proyecto.getUnidad());
				for (ConcesionMinera consesiones : proyecto.getConcesionesMineras()) {
					superficie.add(consesiones.getNombre() + " Código:"
							+ consesiones.getCodigo() + " "
							+ consesiones.getArea() + " "
							+ consesiones.getUnidad());
				}
			} else {
				superficie.add(proyecto.getArea() + " " + proyecto.getUnidad()
						+ " Código:" + proyecto.getCodigoMinero());
			}
		} else {
			concesionesMinerasRcoa = proyectoConcesionesMinerasFacade.cargarConcesiones(proyectoLicenciaCoa);

			if (concesionesMinerasRcoa.size() > 0) {
				for (ProyectoLicenciaAmbientalConcesionesMineras concesiones : concesionesMinerasRcoa) {
					superficie.add(concesiones.getNombre() + " Código:"
							+ concesiones.getCodigo() + " "
							+ concesiones.getArea());
				}
			}
		}

		this.total = 0;
		listaCronogramaPma = armarCronograma();
		
		if (perforacionExplorativa.getId() != null) {
			if (!esProyectoRcoa) {
				fichaAmbientalMineria.setProyectoLicenciamientoAmbiental(proyecto);
				fichaAmbientalMineria.setProyectoLicenciaCoa(null);
				consultor = perforacionExplorativa.getConsultor();
			} else {
				fichaAmbientalMineria.setProyectoLicenciamientoAmbiental(null);
				fichaAmbientalMineria.setProyectoLicenciaCoa(proyectoLicenciaCoa);
				if(perforacionExplorativa.getConsultor() != null){
				consultorRcoa = consultorRcoaFacade.getConsultorPorRuc(perforacionExplorativa.getConsultor().getRuc());
				}
			}

			listaCoordenadas = fichaAmbientalMineria020Facade.cargarPerforacionCoordendas(perforacionExplorativa);
			if (listaCoordenadas.size() == 0)
				guardarConcesiones();

			listaEquipoMultidis = fichaAmbientalMineria020Facade.cargarEquipoMultidisciplinario(perforacionExplorativa);
			listaMaquinariaEquipos = fichaAmbientalMineria020Facade.cargarMaquinariaEquipo(perforacionExplorativa);
			listaMaterialInsumo = fichaAmbientalMineria020Facade.cargarMaterialInsumo(perforacionExplorativa);
			listadesechonopeligrosos = fichaAmbientalMineria020Facade.cargarDesechosNoPeligrosos(perforacionExplorativa);
			listadesechopeligrosos = fichaAmbientalMineria020Facade.cargarDesechosPeligrosos(perforacionExplorativa);
			listaCronogramaPma = fichaAmbientalMineria020Facade.cargarCronogramaPma(perforacionExplorativa);
			if (listaCronogramaPma.size() == 0) {
				listaCronogramaPma = armarCronograma();
			}
			totaliza();
		} else {
			if (proyecto.getId() != null && (proyecto.getCatalogoCategoria().getCodigo().equals("21.02.02.01") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.06")))
				perforacionExplorativa.setCodeUpdate(proyecto.getCatalogoCategoria().getCodigo());
			// TODOMG verificar si para rcoa se debe utilizar el campo
			// setCodeUpdate

			perforacionExplorativa.setConsultor(null);
			if (!esProyectoRcoa) {
				perforacionExplorativa.setProyectoLicenciamientoAmbiental(proyecto);
				perforacionExplorativa.setProyectoLicenciaCoa(null);

				fichaAmbientalMineria.setProyectoLicenciamientoAmbiental(proyecto);
				fichaAmbientalMineria.setProyectoLicenciaCoa(null);
			} else {
				perforacionExplorativa.setProyectoLicenciamientoAmbiental(null);
				perforacionExplorativa.setProyectoLicenciaCoa(proyectoLicenciaCoa);

				fichaAmbientalMineria.setProyectoLicenciamientoAmbiental(null);
				fichaAmbientalMineria.setProyectoLicenciaCoa(proyectoLicenciaCoa);
			}
			perforacionExplorativa = fichaAmbientalMineria020Facade.guardarPerforacionExplorativa(perforacionExplorativa);

			fichaAmbientalMineriaFacade.guardarFicha(fichaAmbientalMineria);

			guardarConcesiones();
		}
		try {
			if (!esProyectoRcoa) {
				parroquia = proyecto.getProyectoUbicacionesGeograficas().get(0)	.getUbicacionesGeografica().getNombre();
				canton = proyecto.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
				provincia = proyecto.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
			} else {
				UbicacionesGeografica ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa).getUbicacionesGeografica();
				parroquia = ubicacionPrincipal.getNombre();
				canton = ubicacionPrincipal.getUbicacionesGeografica().getNombre();
				provincia = ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		listconsultores = consultorCalificadoFacade.consultoresCalificados();
		listaUnidades = unidadService.listaUnidadesMedida();
		listaUnidades20 = unidadService.listaUnidadesMedidaTipo020();
		listaUnidadesMasa = unidadService.listaUnidadesMedidaTipo("masa");
		sustanciasQuimicas();
		
		listconsultoresRcoa = consultorRcoaFacade.consultoresCalificadosRcoa();
		listaConsultoresMostrar = new ArrayList<>();
		mostrarConsultores();
		seleccionarConsultor(consultorRcoa);
		List<PerforacionEquipoMultidisciplinario> equipoSeleccionado = fichaAmbientalMineria020Facade.cargarEquipoMultidisciplinario(perforacionExplorativa);
		for (EquipoMultidisciplinarioConsultor equipo : listaEquipoConsultor) {
			for (PerforacionEquipoMultidisciplinario consultor : equipoSeleccionado) {
				if (consultor.getName().equalsIgnoreCase(equipo.getNombre())) {
					equipo.setSeleccionado(consultor.getEstado());
					break;
				}
			}
		}
	}
	
	public List<ConsultorRcoa> mostrarConsultores() {
		try {
			listaConsultoresMostrar.clear();
			for (ConsultorRcoa consultor : listconsultoresRcoa) {
				consultor.getId();
				consultor.getIdentificacion();
				consultor.getRepresentante();
				consultor.getCertificado();
				consultor.getCorreo();
				consultor.getTelefono();
				listaConsultoresMostrar.add(consultor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaConsultoresMostrar;
	}

	public void seleccionarConsultor(ConsultorRcoa consultor) {
		this.consultorRcoa = consultor;
		listaEquipoConsultor = consultorRcoaFacade.getEquipoMultidisciplinario(consultor.getId());
	}

	public void guardarConcesiones() throws ServiceException {
		if (!esProyectoRcoa) {
			if (proyecto.getConcesionesMineras().size() > 0) {
				for (ConcesionMinera consesiones : proyecto.getConcesionesMineras()) {
					coordendasConcesiones(consesiones.getCodigo());
				}

				for (PerforacionCoordenadas perforacionCoordenadas : listaCoordenadas) {
					perforacionCoordenadas.setPerforacionExplorativa(perforacionExplorativa);
					fichaAmbientalMineria020Facade.guardarPerforacionCoordenada(perforacionCoordenadas);
				}

			} else {
				coordendasConcesiones(proyecto.getCodigoMinero());
				for (PerforacionCoordenadas perforacionCoordenadas : listaCoordenadas) {
					perforacionCoordenadas.setPerforacionExplorativa(perforacionExplorativa);
					fichaAmbientalMineria020Facade.guardarPerforacionCoordenada(perforacionCoordenadas);
				}
			}
		} else {
			if (concesionesMinerasRcoa.size() > 0) {
				for (ProyectoLicenciaAmbientalConcesionesMineras concesiones : concesionesMinerasRcoa) {
					coordendasConcesiones(concesiones.getCodigo());
				}

				for (PerforacionCoordenadas perforacionCoordenadas : listaCoordenadas) {
					perforacionCoordenadas.setPerforacionExplorativa(perforacionExplorativa);
					fichaAmbientalMineria020Facade.guardarPerforacionCoordenada(perforacionCoordenadas);
				}
			}
		}
	}

	private URL getUrlWS() throws MalformedURLException {
		URL url = null;
		URL baseUrl;
		baseUrl = ec.gob.ambiente.client.SuiaServices_Service.class.getResource(".");
		url = new URL(baseUrl, Constantes.getUrlWsRegistroCivilSri());
		return url;
	}

	public void coordendasConcesiones(String codigoMinero) {
		try {
			SuiaServices_Service_Arcon concesion;
			try {
				concesion = new SuiaServices_Service_Arcon(getUrlWS());
				SuiaServicesArcon arcon = concesion.getSuiaServicesPort();
				derechoMinero = arcon.getConsultarCatastral(codigoMinero);
			} catch (Exception e1) {
				JsfUtil.addMessageError("Sin servicio");
			}

			if (derechoMinero.getCoordenadasPSAD56().size() > 0) {
				int x = 1;
				for (Coordenadas concesiones : derechoMinero.getCoordenadasPSAD56()) {
					coordendas = new PerforacionCoordenadas();
					coordendas.setOrden(x);
					coordendas.setX(Double.valueOf(concesiones.getUtmEste()));
					coordendas.setY(Double.valueOf(concesiones.getUtmNorte()));
					listaCoordenadas.add(coordendas);
					x++;
				}
				for (Coordenadas concesiones : derechoMinero.getCoordenadasPSAD56()) {
					coordendas = new PerforacionCoordenadas();
					coordendas.setOrden(x);
					coordendas.setX(Double.valueOf(concesiones.getUtmEste()));
					coordendas.setY(Double.valueOf(concesiones.getUtmNorte()));
					listaCoordenadas.add(coordendas);
					x++;
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Error al conectarce con Arcom");
		}
	}

	public boolean getViabilidad() {
		if (!esProyectoRcoa) {
			String codigoProyecto = proyecto.getCodigo();
			vi = certificadoInterseccionService.isProyectoIntersecaCapas(codigoProyecto);
		} else {
			vi = detalleInterseccionProyectoAmbientalFacade.isProyectoIntersecaCapas(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		}
		return vi;
	}

	private String getNotaResponsabilidadInformacionRegistroProyecto(
			Usuario persona) {
		String[] parametros = { persona.getPersona().getNombre(),persona.getPin() };
		return DocumentoPDFPlantillaHtml.getPlantillaConParametros("nota_responsabilidad_certificado_interseccion", parametros);
	}

	public List<PerforacionCronogramaPma> armarCronograma() throws Exception {
		List<PerforacionCronogramaPma> cronograma = new ArrayList<PerforacionCronogramaPma>();
		for (TipoPlanManejoAmbiental tipo : this.tipoPlanManejoAmbientalFacade.obtenerListaTipoPlanManejoAmbientalPorTipo("REGISTRO_AMBIENTAL_M")) {
			PerforacionCronogramaPma crono = new PerforacionCronogramaPma();
			crono.setEstado(true);
			crono.setPerforacionExplorativa(perforacionExplorativa.getId());
			crono.setTipoPlanManejoAmbiental(tipo);
			crono.setMonth1(false);
			crono.setMonth2(false);
			crono.setMonth3(false);
			crono.setMonth4(false);
			crono.setMonth5(false);
			crono.setMonth6(false);
			crono.setMonth7(false);
			crono.setMonth8(false);
			crono.setMonth9(false);
			crono.setMonth10(false);
			crono.setMonth11(false);
			crono.setMonth12(false);
			crono.setBudget(0.0);
			cronograma.add(crono);
		}
		return cronograma;
	}

	public void totaliza() {

		total = 0;

		for (PerforacionCronogramaPma crono : listaCronogramaPma) {
			total += crono.getBudget();
		}
		try {
			perforacionExplorativa.setTotalPma(total);
			fichaAmbientalMineria020Facade.guardarPerforacionExplorativa(perforacionExplorativa);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void nuevaCoordenada() throws ServiceException {
		coordendas = new PerforacionCoordenadas();
		editarCoordenada = false;
	}

	public void adicionarCoordenada() throws ServiceException {
		if (!editarCoordenada)
			listaCoordenadas.add(coordendas);
	}

	public void nuevoEquipoMultidis() throws ServiceException {
		equipoMultidisciplinario = new PerforacionEquipoMultidisciplinario();
		editarEquipo = false;
	}

	public void adicionarEquipoMultidis() throws ServiceException {
		if (!editarEquipo)
			listaEquipoMultidis.add(equipoMultidisciplinario);
	}

	public void nuevoMaquinaEquipos() throws ServiceException {
		maquinariaEquipos = new PerforacionMaquinasEquipos();
		edicionMaquinaria = false;
	}

	public void nuevoMaterialInsumo() throws ServiceException {
		materialInsumo = new PerforacionMaterialInsumo();
		sustanciaQuimicaSeleccionada = new SustanciaQuimicaPeligrosa();
		unidadSeleccionada = new UnidadMedida();
		edicionInsumo = false;
	}

	public void nuevoDesechoNoPeligroso() throws ServiceException {
		desechonopeligrosos = new PerforacionDesechosNoPeligrosos();
		unidadSeleccionada = new UnidadMedida();
		edicionDesechoNoPeligroso = false;
	}

	public void unidadSelecionada(Integer x) {
		if (x == 1)
			maquinariaEquipos.setUnit(unidadSeleccionada.getSiglas());
		if (x == 2)
			materialInsumo.setUnit(unidadSeleccionada.getSiglas());
		if (x == 3)
			desechonopeligrosos.setUnit(unidadSeleccionada.getSiglas());
		if (x == 4)
			desechopeligrosos.setUnit(unidadSeleccionada.getSiglas());
	}

	public void guardar3() throws ServiceException {
		if (listaCoordenadas.size() == 0) {
			JsfUtil.addMessageError("Ingrese Coordenadas Geográficas(PSAD 56) Derecho Minero");
			return;
		}
		if (consultorRcoa == null) {
			JsfUtil.addMessageError("Seleccione un consultor");
			return;
		}
		if (consultorRcoa.getIdOrganizacion() != null) {
			boolean tieneSocioeconomico = false;
			boolean tieneFisico = false;
			boolean tieneBiotico = false;
			boolean tieneGeografico = false;

			for (EquipoMultidisciplinarioConsultor consultor : listaEquipoConsultor) {
				if (consultor.isSeleccionado()) {
					switch (consultor.getComponente().getNombre()) {
					case "Socioeconómico":
						tieneSocioeconomico = true;
						break;
					case "Físico":
						tieneFisico = true;
						break;
					case "Biótico":
						tieneBiotico = true;
						break;
					case "Geográfico":
						tieneGeografico = true;
						break;
					}
				}
			}

			if (!tieneSocioeconomico) {
				JsfUtil.addMessageError("Debe seleccionar al menos un consultor del componente Socioeconómico.");
			}
			if (!tieneFisico) {
				JsfUtil.addMessageError("Debe seleccionar al menos un consultor del componente Físico.");
			}
			if (!tieneBiotico) {
				JsfUtil.addMessageError("Debe seleccionar al menos un consultor del componente Biótico.");
			}
			if (!tieneGeografico) {
				JsfUtil.addMessageError("Debe seleccionar al menos un consultor del componente Geográfico.");
			}
			if (!tieneSocioeconomico || !tieneFisico || !tieneBiotico
					|| !tieneGeografico) {
				return;
			}
		}

		Consultor nuevoConsultor = new Consultor();
		nuevoConsultor = consultorCalificadoFacade.buscarConsultorPorRuc(consultorRcoa.getIdentificacion());
		perforacionExplorativa.setConsultor(nuevoConsultor);
		perforacionExplorativa = fichaAmbientalMineria020Facade.guardarPerforacionExplorativa(perforacionExplorativa);

		List<PerforacionEquipoMultidisciplinario> equipoCompleto = fichaAmbientalMineria020Facade.equipoMultidisciplinarioCompleto(perforacionExplorativa);
		for (EquipoMultidisciplinarioConsultor equipo : listaEquipoConsultor) {
			boolean existe = false;
			PerforacionEquipoMultidisciplinario consultorExistente = null;
			for (PerforacionEquipoMultidisciplinario existente : equipoCompleto) {
				if (existente.getName().equalsIgnoreCase(equipo.getNombre())) {
					existe = true;
					consultorExistente = existente;
					break;
				}
			}
			if (equipo.isSeleccionado()) {
				if (!existe) {
					PerforacionEquipoMultidisciplinario consultor = new PerforacionEquipoMultidisciplinario();
					consultor.setPerforacionExplorativa(perforacionExplorativa.getId());
					consultor.setName(equipo.getNombre());
					consultor.setVocationalTraining(equipo.getTitulo());
					consultor.setComponent(equipo.getComponente().getDescripcion());
					consultor.setEstado(true);
					fichaAmbientalMineria020Facade.guardarEquipoMultidisciplinario(consultor);
				} else if (consultorExistente != null
						&& !consultorExistente.getEstado()) {
					consultorExistente.setEstado(true);
					fichaAmbientalMineria020Facade.guardarEquipoMultidisciplinario(consultorExistente);
				}
			} else {
				if (existe && consultorExistente != null) {
					consultorExistente.setEstado(false);
					fichaAmbientalMineria020Facade.guardarEquipoMultidisciplinario(consultorExistente);
				}
			}
		}
		listaEquipoMultidisEliminados = new ArrayList<PerforacionEquipoMultidisciplinario>();
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
	}

	public void guardar4() throws ServiceException {
		if (vi) {
			if (perforacionExplorativa.getProjectExecutionPermit().length() == 0) {
				JsfUtil.addMessageError("Ingresar Permiso de ejecución del proyecto");
				return;
			}

			if (perforacionExplorativa.getFeasibilityCertificate().length() == 0) {
				JsfUtil.addMessageError("Ingresar Certificado de Viabilidad emitido por la Dirección Nacional Forestal");
				return;
			}
		} else {
			if (perforacionExplorativa.getProjectExecutionPermit().length() == 0) {
				JsfUtil.addMessageError("Ingresar Permiso de ejecución del proyecto");
				return;
			}

		}
		perforacionExplorativa = fichaAmbientalMineria020Facade
				.guardarPerforacionExplorativa(perforacionExplorativa);
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);

	}

	public void guardar5() throws ServiceException {
		if (perforacionExplorativa.getProjectSummary().length() == 0) {
			JsfUtil.addMessageError("Ingresar Resumen del Proyecto");
			return;
		}

		perforacionExplorativa = fichaAmbientalMineria020Facade
				.guardarPerforacionExplorativa(perforacionExplorativa);
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);

	}

	public void guardar6() throws ServiceException {
		if (perforacionExplorativa.getIntervenedAreas().length() == 0) {
			JsfUtil.addMessageError("Describir la totalidad de actividades a desarrollarse");
			return;
		}
		if (perforacionExplorativa.getExplorationTechniques().length() == 0) {
			JsfUtil.addMessageError("Describir las técnicas de exploración a emplearse");
			return;
		}
		if (perforacionExplorativa.getProjectClosureStages().length() == 0) {
			JsfUtil.addMessageError("Detallar las actividades que se desarrollarán durante las etapas de cierre de proyecto");
			return;
		}
		// if(perforacionExplorativa.getInfrastructureComplementaryActivities().length()==0)
		// {
		// JsfUtil.addMessageError("Describir la relación a la infraestructura y actividades complementarias de la actividad");
		// return;
		// }
		if (perforacionExplorativa.getAccessesTrails().length() == 0) {
			JsfUtil.addMessageError("Describir Accesos y/o trochas hacia los sitios de desarrollo de las actividades");
			return;
		}
		if (perforacionExplorativa.getTemporaryInfrastructureImplementation()
				.length() == 0) {
			JsfUtil.addMessageError("Describir Áreas a ser desbrozadas para realizar las actividades mineras");
			return;
		}
		if (perforacionExplorativa.getTemporaryCamps().length() == 0) {
			JsfUtil.addMessageError("Describir las Características constructivas y dimensiones de campamentos temporales");
			return;
		}

		if (listaMaquinariaEquipos.size() == 0) {
			JsfUtil.addMessageError("Ingrese Maquinaria, equipos y materiales");
			return;
		}
		if (listaMaterialInsumo.size() == 0) {
			JsfUtil.addMessageError("Ingrese Materiales y/o Insumos");
			return;
		}
		if (listadesechonopeligrosos.size() == 0) {
			JsfUtil.addMessageError("Ingrese un registro de generación de residuos y desechos no peligrosos");
			return;
		}
		if (listadesechopeligrosos.size() == 0) {
			JsfUtil.addMessageError("Ingrese un registro de generación de desechos y residuos peligrosos y/o especiales");
			return;
		}

		if (perforacionExplorativa.getQualifiedHandWork().length() == 0) {
			JsfUtil.addMessageError("Detallar el requerimiento de mano de obra calificada y no calificada");
			return;
		}
		if (perforacionExplorativa.getBaseFlowDiagram().length() == 0) {
			JsfUtil.addMessageError("Incluir un diagrama de flujo en base a las cantidades de uso de agua en las actividades del proyecto");
			return;
		}

		perforacionExplorativa = fichaAmbientalMineria020Facade
				.guardarPerforacionExplorativa(perforacionExplorativa);

		/**
		 * Bbyron Modificacion para que solo guarde los registros modificados
		 * aumentar el guardado de los registros eliminados 2019-11-22
		 */
		boolean actualizacionMaquinaria = false, actualizacionInsumo = false, actualizacionDesechosNoP = false, actualizacionDesechos = false;
		for (PerforacionMaquinasEquipos perforacionMaquinasEquipos : listaMaquinariaEquipos) {
			if (perforacionMaquinasEquipos.isModificado()) {
				perforacionMaquinasEquipos.setPerforacionExplorativa(perforacionExplorativa.getId());
				fichaAmbientalMineria020Facade.guardarMaquinariaEquipo(perforacionMaquinasEquipos);
				actualizacionMaquinaria = true;
			}
		}
		if (actualizacionMaquinaria) {
			listaMaquinariaEquipos = fichaAmbientalMineria020Facade.cargarMaquinariaEquipo(perforacionExplorativa);
		}
		for (PerforacionMaterialInsumo perforacionMaterialesInsumos : listaMaterialInsumo) {
			if (perforacionMaterialesInsumos.isModificado()) {
				perforacionMaterialesInsumos.setPerforacionExplorativa(perforacionExplorativa.getId());
				fichaAmbientalMineria020Facade.guardarMaterialInsumo(perforacionMaterialesInsumos);
				actualizacionInsumo = true;
			}
		}
		if (actualizacionInsumo) {
			listaMaterialInsumo = fichaAmbientalMineria020Facade.cargarMaterialInsumo(perforacionExplorativa);
		}
		for (PerforacionDesechosNoPeligrosos perforacionDesechosNoPeligrosos : listadesechonopeligrosos) {
			if (perforacionDesechosNoPeligrosos.isModificado()) {
				perforacionDesechosNoPeligrosos.setPerforacionExplorativa(perforacionExplorativa.getId());
				fichaAmbientalMineria020Facade.guardarDesechosNoPeligrosos(perforacionDesechosNoPeligrosos);
				actualizacionDesechosNoP = true;
			}
		}
		if (actualizacionDesechosNoP) {
			listadesechonopeligrosos = fichaAmbientalMineria020Facade.cargarDesechosNoPeligrosos(perforacionExplorativa);
		}
		for (PerforacionDesechosPeligrosos perforacionDesechosPeligrosos : listadesechopeligrosos) {
			if (perforacionDesechosPeligrosos.isModificado()) {
				perforacionDesechosPeligrosos.setPerforacionExplorativa(perforacionExplorativa.getId());
				fichaAmbientalMineria020Facade.guardarDesechosPeligrosos(perforacionDesechosPeligrosos);
				actualizacionDesechos = true;
			}
		}
		if (actualizacionDesechos) {
			listadesechopeligrosos = fichaAmbientalMineria020Facade.cargarDesechosPeligrosos(perforacionExplorativa);
		}

		// elimino los items borrados
		for (PerforacionMaquinasEquipos perforacionMaquinasEquipos : listaMaquinariaEquiposEliminados) {
			perforacionMaquinasEquipos.setEstado(false);
			fichaAmbientalMineria020Facade.guardarMaquinariaEquipo(perforacionMaquinasEquipos);
		}
		for (PerforacionMaterialInsumo perforacionMaterialesInsumos : listaMaterialInsumoEliminados) {
			perforacionMaterialesInsumos.setEstado(false);
			fichaAmbientalMineria020Facade.guardarMaterialInsumo(perforacionMaterialesInsumos);
		}
		for (PerforacionDesechosNoPeligrosos perforacionDesechosNoPeligrosos : listaDesechoNoPeligrososEliminados) {
			perforacionDesechosNoPeligrosos.setEstado(false);
			fichaAmbientalMineria020Facade.guardarDesechosNoPeligrosos(perforacionDesechosNoPeligrosos);
		}
		for (PerforacionDesechosPeligrosos perforacionDesechosPeligrosos : listaDesechoPeligrososEliminados) {
			perforacionDesechosPeligrosos.setEstado(false);
			fichaAmbientalMineria020Facade.guardarDesechosPeligrosos(perforacionDesechosPeligrosos);
		}
		limpiarListaEliminar();
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
	}

	private void limpiarListaEliminar() {
		listaMaquinariaEquiposEliminados = new ArrayList<PerforacionMaquinasEquipos>();
		listaMaterialInsumoEliminados = new ArrayList<PerforacionMaterialInsumo>();
		listaDesechoNoPeligrososEliminados = new ArrayList<PerforacionDesechosNoPeligrosos>();
		listaDesechoPeligrososEliminados = new ArrayList<PerforacionDesechosPeligrosos>();
	}

	public void guardar7() throws ServiceException {

		if (perforacionExplorativa.getWaterHumanConsumption() && (perforacionExplorativa.getWaterConsumption() == null || perforacionExplorativa.getWaterConsumption().equals(""))) {
			JsfUtil.addMessageError("Ingrese consumo de agua");
			return;
		}
		if (perforacionExplorativa.getWaterDrilling() && (perforacionExplorativa.getWaterConsumptionDrilling() == null || perforacionExplorativa.getWaterConsumptionDrilling().equals(""))) {
			JsfUtil.addMessageError("Ingrese consumo de agua para perforación");
			return;
		}
		if (perforacionExplorativa.getElectricPower() && (perforacionExplorativa.getElectricPowerConsumption() == null || perforacionExplorativa.getElectricPowerConsumption().equals(""))) {
			JsfUtil.addMessageError("Ingrese consumo de energía eléctrica");
			return;
		}
		if (perforacionExplorativa.getFuelConsumption()	&& (perforacionExplorativa.getFuelTypeConsumption() == null || perforacionExplorativa.getFuelTypeConsumption().equals(""))) {
			JsfUtil.addMessageError("Ingrese consumo y tipo de combustible");
			return;
		}

		perforacionExplorativa = fichaAmbientalMineria020Facade.guardarPerforacionExplorativa(perforacionExplorativa);
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
	}

	public void guardar8() throws ServiceException {
		if (perforacionExplorativa.getDirectPhysicalInfluence().length() == 0) {
			JsfUtil.addMessageError("Ingrese área de influencia directa física");
			return;
		}
		if (perforacionExplorativa.getDirectBioticInfluence().length() == 0) {
			JsfUtil.addMessageError("Ingrese área de influencia directa biótica");
			return;
		}
		if (perforacionExplorativa.getDirectSocialInfluence().length() == 0) {
			JsfUtil.addMessageError("Ingrese área de influencia directa social");
			return;
		}
		if (perforacionExplorativa.getIndirectPhysicalInfluence().length() == 0) {
			JsfUtil.addMessageError("Ingrese área de influencia indirecta social");
			return;
		}
		if (perforacionExplorativa.getIndirectBioticInfluence().length() == 0) {
			JsfUtil.addMessageError("Ingrese área de influencia indirecta social");
			return;
		}
		if (perforacionExplorativa.getIndirectSocialInfluence().length() == 0) {
			JsfUtil.addMessageError("Ingrese área de influencia indirecta social");
			return;
		}
		perforacionExplorativa = fichaAmbientalMineria020Facade.guardarPerforacionExplorativa(perforacionExplorativa);
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
	}

	public void guardarCronograma() {
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
	}

	public void guardar10() throws ServiceException {
		if (!perforacionExplorativa.getLegalFramework()) {
			JsfUtil.addMessageError("Aceptar las Normativas legales que aplican a su Proyecto, obra o actividad.");
			return;
		}

		perforacionExplorativa = fichaAmbientalMineria020Facade.guardarPerforacionExplorativa(perforacionExplorativa);
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
	}

	public void finalizar() throws JbpmException, ServiceException {
		perforacionExplorativa.setFinalized(true);
		perforacionExplorativa = fichaAmbientalMineria020Facade.guardarPerforacionExplorativa(perforacionExplorativa);
		long idInstanciaProceso = bandejaTareasBean.getProcessId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("requiereCoberturaVegetal", false);
		params.put("metrosCobertura", 0);
		params.put("factorCobertura", 0);
		procesoFacade.modificarVariablesProceso(loginBean.getUsuario(),idInstanciaProceso, params);

		taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
				bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null, loginBean.getPassword(), Constantes.getUrlBusinessCentral(),
				Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

		JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);

	}

	public void validador() {
		listaErrores = new HashMap<String, Integer>();
		if (listaCoordenadas.size() == 0 || consultor == null || listaEquipoMultidis.size() == 0) {
			listaErrores.put("3. FICHA TÉCNICA", 3);
		}

		if (vi) {
			if (perforacionExplorativa.getProjectExecutionPermit() == null || perforacionExplorativa.getProjectExecutionPermit().equals("")) {
				listaErrores.put("4. ANTECEDENTES DEL PROYECTO", 4);
			}

			if (perforacionExplorativa.getFeasibilityCertificate() == null || perforacionExplorativa.getFeasibilityCertificate().equals("")) {
				listaErrores.put("4. ANTECEDENTES DEL PROYECTO", 4);
			}
		} else {
			if (perforacionExplorativa.getProjectExecutionPermit() == null || perforacionExplorativa.getProjectExecutionPermit().equals("")) {
				listaErrores.put("4. ANTECEDENTES DEL PROYECTO", 4);
			}

		}

		// if((perforacionExplorativa.getProjectExecutionPermit()==null ||
		// perforacionExplorativa.getProjectExecutionPermit().equals(""))||
		// (perforacionExplorativa.getIntersectionCertificate()==null ||
		// perforacionExplorativa.getIntersectionCertificate().equals(""))||
		// (perforacionExplorativa.getFeasibilityCertificate()==null ||
		// perforacionExplorativa.getFeasibilityCertificate().equals("")) ||
		// (perforacionExplorativa.getPreviousEnvironmentalPermits()==null
		// ||perforacionExplorativa.getPreviousEnvironmentalPermits().equals("")))
		// {
		// listaErrores.put("4. ANTECEDENTES DEL PROYECTO",4);
		// }

		if (perforacionExplorativa.getProjectSummary() == null || perforacionExplorativa.getProjectSummary().equals("")) {
			listaErrores.put("5. RESUMEN DEL PROYECTO", 5);
		}
		if ((perforacionExplorativa.getIntervenedAreas() == null 
				|| perforacionExplorativa.getIntervenedAreas().equals(""))
				|| (perforacionExplorativa.getExplorationTechniques() == null 
				|| perforacionExplorativa.getExplorationTechniques().equals(""))
				|| (perforacionExplorativa.getProjectClosureStages() == null 
				|| perforacionExplorativa.getProjectClosureStages().equals(""))
//				|| (perforacionExplorativa.getInfrastructureComplementaryActivities()==null
//				|| perforacionExplorativa.getInfrastructureComplementaryActivities().equals(""))
				|| (perforacionExplorativa.getAccessesTrails() == null || perforacionExplorativa.getAccessesTrails().equals(""))
				|| (perforacionExplorativa.getTemporaryInfrastructureImplementation() == null 
				|| perforacionExplorativa.getTemporaryInfrastructureImplementation().equals(""))
				|| (perforacionExplorativa.getTemporaryCamps() == null || perforacionExplorativa.getTemporaryCamps().equals(""))
				|| listaMaquinariaEquipos.size() == 0
				|| listaMaterialInsumo.size() == 0
				|| listadesechonopeligrosos.size() == 0
				|| listadesechopeligrosos.size() == 0
				|| (perforacionExplorativa.getQualifiedHandWork() == null || perforacionExplorativa.getQualifiedHandWork().equals(""))
				|| (perforacionExplorativa.getBaseFlowDiagram() == null || perforacionExplorativa.getBaseFlowDiagram().equals(""))) {
			listaErrores.put("6. DESCRIPCIÓN DEL PROYECTO", 6);
		}

		Integer xrp = 0;
		if (perforacionExplorativa.getWaterHumanConsumption() == null)
			xrp = 1;
		if (perforacionExplorativa.getWaterDrilling() == null)
			xrp = 1;
		if (perforacionExplorativa.getElectricPower() == null)
			xrp = 1;
		if (perforacionExplorativa.getFuelConsumption() == null)
			xrp = 1;
		if (xrp == 0) {
			if ((perforacionExplorativa.getWaterHumanConsumption() && (perforacionExplorativa.getWaterConsumption() == null || perforacionExplorativa.getWaterConsumption().equals("")))
					|| (perforacionExplorativa.getWaterDrilling() && (perforacionExplorativa.getWaterConsumptionDrilling() == null || perforacionExplorativa.getWaterConsumptionDrilling().equals("")))
					|| (perforacionExplorativa.getElectricPower() && (perforacionExplorativa.getElectricPowerConsumption() == null || perforacionExplorativa.getElectricPowerConsumption().equals("")))
					|| (perforacionExplorativa.getFuelConsumption() && (perforacionExplorativa.getFuelTypeConsumption() == null || perforacionExplorativa.getFuelTypeConsumption().equals("")))) {
				xrp = 1;
			}
		}

		if (xrp == 1) {
			listaErrores.put("7. REQUERIMIENTOS DEL PROYECTO", 7);
		}

		if ((perforacionExplorativa.getDirectPhysicalInfluence() == null || perforacionExplorativa.getDirectPhysicalInfluence().equals(""))
				|| (perforacionExplorativa.getDirectBioticInfluence() == null || perforacionExplorativa.getDirectBioticInfluence().equals(""))
				|| (perforacionExplorativa.getDirectSocialInfluence() == null || perforacionExplorativa.getDirectSocialInfluence().equals(""))
				|| (perforacionExplorativa.getIndirectPhysicalInfluence() == null || perforacionExplorativa.getIndirectPhysicalInfluence().equals(""))
				|| (perforacionExplorativa.getIndirectBioticInfluence() == null || perforacionExplorativa.getIndirectBioticInfluence().equals(""))
				|| (perforacionExplorativa.getIndirectSocialInfluence() == null || perforacionExplorativa.getIndirectSocialInfluence().equals(""))) {
			listaErrores.put(
					"8. AREA DE INFLUENCIA SOCIAL DIRECTA E INDIRECTA", 8);
		}

		// punto 9-------------------------------------------
		try {
			Integer xps = 0;
			ComponenteFisicoSD componenteFisico = new ComponenteFisicoSD();
			ComponenteSocialSD componenteSocial = new ComponenteSocialSD();
			componenteFisico = componenteFisicoFacade.buscarComponenteFisicoPorPerforacionExplorativa(perforacionExplorativa.getId());
			componenteSocial = componenteSocialFacade.buscarComponenteSocialPorPerforacionExplorativa(perforacionExplorativa.getId());
			if (componenteFisico == null)
				componenteFisico = new ComponenteFisicoSD();
			if (componenteSocial == null)
				componenteSocial = new ComponenteSocialSD();

			if (componenteFisico.getPrecipitacionAnual() == null || componenteFisico.getPrecipitacionAnual().equals(""))
				xps = 1;
			if (componenteFisico.getTemperaturaMediaAnual() == null || componenteFisico.getTemperaturaMediaAnual().equals(""))
				xps = 1;
			if (componenteFisico.getAltitud() == null || componenteFisico.getAltitud().equals(""))
				xps = 1;
			if (componenteFisico.getComponenteHidrico() == null || componenteFisico.getComponenteHidrico().equals(""))
				xps = 1;
			if (componenteFisico.getComponenteFisicoPendienteList() == null)
				xps = 1;

			if (componenteSocial.getComunidades() == null || componenteSocial.getComunidades().equals(""))
				xps = 1;
			if (componenteSocial.getPoblaciones() == null || componenteSocial.getPoblaciones().equals(""))
				xps = 1;
			if (componenteSocial.getPredios() == null || componenteSocial.getPredios().equals(""))
				xps = 1;
			if (componenteSocial.getInfraestructura() == null || componenteSocial.getInfraestructura().equals(""))
				xps = 1;
			if (componenteSocial.getAsentamientosGruposEtnicos() == null || componenteSocial.getAsentamientosGruposEtnicos().equals(""))
				xps = 1;
			if (componenteSocial.getInfraestructuraSaludPublica() == null || componenteSocial.getInfraestructuraSaludPublica().equals(""))
				xps = 1;
			if (componenteSocial.getElementosCulturales() == null || componenteSocial.getElementosCulturales().equals(""))
				xps = 1;

			// List<Documento> documentoComunidadesList =
			// documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
			// CategoriaIILicencia.class.getSimpleName(),
			// TipoDocumentoSistema.DOCUMENTO_COMUNIDADES_020);
			// if(documentoComunidadesList.isEmpty())
			// xps=1;
			// List<Documento> documentoPrediosList =
			// documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
			// CategoriaIILicencia.class.getSimpleName(),
			// TipoDocumentoSistema.DOCUMENTO_PREDIOS_020);
			// if(documentoPrediosList.isEmpty())
			// xps=1;
			// List<Documento> documentoInfraestruturaList =
			// documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
			// CategoriaIILicencia.class.getSimpleName(),
			// TipoDocumentoSistema.DOCUMENTO_INFRAESTRUCTURA_020);
			// if(documentoInfraestruturaList.isEmpty())
			// xps=1;
			// List<Documento> documentoInfraestruturaSaludList =
			// documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
			// CategoriaIILicencia.class.getSimpleName(),
			// TipoDocumentoSistema.DOCUMENTO_INFRAESTRUCTURA_SALUD_020);
			// if(documentoInfraestruturaSaludList.isEmpty())
			// xps=1;

			if (xps == 1) {
				listaErrores.put("9. LINEA BASE", 9);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// --------------------------------------------------
		if (perforacionExplorativa.getLegalFramework() == null) {
			listaErrores.put("10. MARCO LEGAL", 10);
		}

		try {
			listaActividades = actividadesImpactoFacade.obtenerActividdaesProyecto(perforacionExplorativa.getId());
			Integer xa = 0;
			for (EntityActividad objListaImpacto : listaActividades) {
				for (ActividadesImpactoProyecto objImpacto : objListaImpacto.getSubactividades()) {
					// if((!"".equals(objImpacto.getHerramientas().trim()) &&
					// !"".equals(objImpacto.getImpacto().trim()) &&
					// !"".equals(objImpacto.getDescripcionImpacto().trim()))
					if ((!"".equals(objImpacto.getHerramientas()) && !"".equals(objImpacto.getImpacto()) && !"".equals(objImpacto.getDescripcionImpacto()))
							&& (objImpacto.isAgua() || objImpacto.isAire()
									|| objImpacto.isSocial()
									|| objImpacto.isSuelo()
									|| objImpacto.isBiotico() || objImpacto.isPaisaje())) {
					} else
						xa = 1;
				}
			}
			if (xa == 1) {
				listaErrores.put("11. DESCRIPCIÓN DE ACTIVIDADES E IDENTIFICACIÓN DE IMPACTOS AMBIENTALES",11);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		parameters = planManejoAmbiental020Facade.obtenerActividdaesProyecto(perforacionExplorativa.getId());
		Integer xma = 0;
		for (Map.Entry<String, List<PlanManejoAmbientalProyecto>> entry : parameters.entrySet()) {
			List<PlanManejoAmbientalProyecto> datos = entry.getValue();
			for (PlanManejoAmbientalProyecto datosGuardar : datos) {
				if (datosGuardar.getCalificacion() != null) {
					if (datosGuardar.getCalificacion() == 0) {
						xma = 1;
						break;
					}
				}
			}
		}
		if (xma == 1) {
			listaErrores.put("12. PLAN DE MANEJO AMBIENTAL", 12);
		}

//		Integer xpma = 0;
//		for (PerforacionCronogramaPma crono : listaCronogramaPma) {
//			if (crono.getBudget() == 0) {
//				xpma = 1;
//				break;
//			}
//			if (!crono.getMonth1() && !crono.getMonth2() && !crono.getMonth3()
//					&& !crono.getMonth4() && !crono.getMonth5()
//					&& !crono.getMonth6() && !crono.getMonth7()
//					&& !crono.getMonth8() && !crono.getMonth9()
//					&& !crono.getMonth10() && !crono.getMonth11()
//					&& !crono.getMonth12()) {
//				xpma = 1;
//				break;
//			}
//		}
//		if (xpma == 1) {
//			listaErrores.put("13. CRONOGRAMA VALORADO DE ACTIVIDADES", 13);
//		} else {
//			totaliza();
//		}

		Integer anexo = 0;
		if (!esProyectoRcoa)
			anexo = validarAnexos();
		else
			anexo = validarAnexosRcoa();

		if (anexo == 1) {
			listaErrores.put("14. ANEXO", 14);
		}

		listaErrores = sortByValue(listaErrores);
	}

	private static Map<String, Integer> sortByValue(
			Map<String, Integer> unsortMap) {

		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public void activeIndex(Integer index) {
		this.index = index;
		panelErrores = false;
		if (index == 13) {
			for (int i = 0; i < listaCronogramaPma.size(); i++) {
				double calificacion = 0.0;
				PerforacionCronogramaPma cronogramaPma = listaCronogramaPma
						.get(i);
				if (!esProyectoRcoa)
					calificacion = fichaAmbientalMineria020Facade
							.sumaCalificacionPmaPorProyecto(proyecto.getId(),
									cronogramaPma.getTipoPlanManejoAmbiental()
											.getTipo().toUpperCase());
				else
					calificacion = fichaAmbientalMineria020Facade
							.sumaCalificacionPmaPorProyectoRcoa(
									proyectoLicenciaCoa.getId(), cronogramaPma
											.getTipoPlanManejoAmbiental()
											.getTipo().toUpperCase());
				cronogramaPma.setBudget(calificacion);
				listaCronogramaPma.set(i, cronogramaPma);
			}
			try {
				fichaAmbientalMineria020Facade.guardarCronogramaPma(listaCronogramaPma);
				totaliza();
			} catch (ServiceException e) {
				LOG.error(e, e);
				JsfUtil.addMessageError("Error al realizar la operación en Cronograma valorado de actividades.");
			}
		}
	}

	@SuppressWarnings("resource")
	public void enviarFicha() {
		try {
			validador();
			if (!listaErrores.isEmpty()) {
				panelErrores = true;
			} else {
				panelErrores = false;

				String numeroRes = null;
				if (!esProyectoRcoa)
					numeroRes = fichaAmbientalMineriaFacade
							.generarNoResolucion(proyecto);
				else
					numeroRes = fichaAmbientalMineriaFacade
							.generarNoResolucionRcoa();

				fichaAmbientalMineria.setNumeroResolucion(numeroRes);
				fichaAmbientalMineriaFacade.guardarFicha(fichaAmbientalMineria);

				byte[] contentFile = null;
				if (!esProyectoRcoa) {
					FichaAmbientalGeneralFinalizarControllerV2 fichaAmbientalGeneralFinalizarControllerV2 = JsfUtil
							.getBean(FichaAmbientalGeneralFinalizarControllerV2.class);
					contentFile = fichaAmbientalGeneralFinalizarControllerV2
							.generarFichaRegistroAmbiental();
				} else
					contentFile = generarFicha020();

				File files = null;
				files = new File(JsfUtil.devolverPathReportesHtml("ficha-"
						+ new Date().getTime() + ".pdf"));
				FileOutputStream fileOuputStream = null;
				fileOuputStream = new FileOutputStream(files);
				fileOuputStream.write(contentFile);
				file = JsfUtil.devolverContexto("/reportesHtml/"
						+ files.getName());
				index = 16;
			}
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}

	public void resetValue(String field) {
		switch (field) {
		case "waterHumanConsumption":
			perforacionExplorativa.setWaterConsumption(null);
			break;
		case "waterDrilling":
			perforacionExplorativa.setWaterConsumptionDrilling(null);
			break;
		case "electricPower":
			perforacionExplorativa.setElectricPowerConsumption(null);
			break;
		case "fuelConsumption":
			perforacionExplorativa.setFuelTypeConsumption(null);
			break;

		default:
			break;
		}
	}

	public void eliminarCoordenadas(PerforacionCoordenadas perforacionCoordenadas) {
		perforacionCoordenadas.setEstado(false);
		listaCoordenadas.remove(perforacionCoordenadas);
	}

	public void modificarCoordenadas(PerforacionCoordenadas perforacionCoordenadas) {
		editarCoordenada = true;
		coordendas = perforacionCoordenadas;
	}

	public void eliminarEquipo(PerforacionEquipoMultidisciplinario equipoMultidisciplinario) {
		equipoMultidisciplinario.setEstado(false);
		if (equipoMultidisciplinario.getId() != null) {
			listaEquipoMultidisEliminados.add(equipoMultidisciplinario);
		}
		listaEquipoMultidis.remove(equipoMultidisciplinario);
	}

	public void modificarEquipo(PerforacionEquipoMultidisciplinario equipo) {
		editarEquipo = true;
		equipoMultidisciplinario = equipo;
	}

	public void adicionarMaquinariaEquipo() throws ServiceException {
		if (!edicionMaquinaria) {
			maquinariaEquipos.setModificado(true);
			listaMaquinariaEquipos.add(maquinariaEquipos);
			unidadSeleccionada = new UnidadMedida();
		}
	}

	public void editarMaquinaria(PerforacionMaquinasEquipos maquinaria) {
		maquinariaEquipos = maquinaria;
		maquinariaEquipos.setModificado(true);
		edicionMaquinaria = true;
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('idMaquinariaEquipo').show();");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al cargar la información");
			LOG.error(e, e);
		}
	}

	public void eliminarMaquinaria(PerforacionMaquinasEquipos maquinaria) {
		try {
			if (maquinaria.getId() != null) {
				maquinaria.setEstado(false);
				listaMaquinariaEquiposEliminados.add(maquinaria);
			}
			listaMaquinariaEquipos.remove(maquinaria);
		} catch (Exception e) {
			LOG.info("Error al eliminar", e);
		}
	}

	public void adicionarMaterialInsumo() throws ServiceException {
		materialInsumo.setSustanciaQuimicaPeligrosa(sustanciaQuimicaSeleccionada);
		if (!sustanciaQuimicaSeleccionada.getId().equals(5187)) {
			String numeroCas = (sustanciaQuimicaSeleccionada.getNumeroCas() == null) ? "" : sustanciaQuimicaSeleccionada.getNumeroCas();
			String codigoOnu = (sustanciaQuimicaSeleccionada.getCodigoOnu() == null) ? "" : sustanciaQuimicaSeleccionada.getCodigoOnu();
			materialInsumo.setCasonu(numeroCas + " / " + codigoOnu);
		}
		if (!edicionInsumo) {
			materialInsumo.setModificado(true);
			listaMaterialInsumo.add(materialInsumo);
			unidadSeleccionada = new UnidadMedida();
		}
	}

	public void editarInsumos(PerforacionMaterialInsumo insumo) {
		materialInsumo = insumo;
		materialInsumo.setModificado(true);
		edicionInsumo = true;
		sustanciaQuimicaSeleccionada = insumo.getSustanciaQuimicaPeligrosa();
		for (UnidadMedida unidadMedida : listaUnidades) {
			if (unidadMedida.getSiglas().compareTo(materialInsumo.getUnit()) == 0) {
				unidadSeleccionada = unidadMedida;
				break;
			}
		}
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('idMaterialInsumo').show();");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al cargar la información");
			LOG.error(e, e);
		}
	}

	public void eliminarInsumos(PerforacionMaterialInsumo insumo) {
		try {
			if (insumo.getId() != null) {
				insumo.setEstado(false);
				listaMaterialInsumoEliminados.add(insumo);
			}
			listaMaterialInsumo.remove(insumo);
		} catch (Exception e) {
			LOG.info("Error al eliminar", e);
		}
	}

	public void adicionarDesechoNoPeligroso() throws ServiceException {
		if (!edicionDesechoNoPeligroso) {
			desechonopeligrosos.setModificado(true);
			listadesechonopeligrosos.add(desechonopeligrosos);
			unidadSeleccionada = new UnidadMedida();
		}
	}

	public void editarDesechosNoPeligrosos(PerforacionDesechosNoPeligrosos desechoNoPeligroso) {
		desechonopeligrosos = desechoNoPeligroso;
		desechonopeligrosos.setModificado(true);
		edicionDesechoNoPeligroso = true;
		for (UnidadMedida unidadMedida : listaUnidades) {
			if (unidadMedida.getSiglas().compareTo(
					desechonopeligrosos.getUnit()) == 0) {
				unidadSeleccionada = unidadMedida;
				break;
			}
		}
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('idDesechosNoPeligrosos').show();");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al cargar la información");
			LOG.error(e, e);
		}
	}

	public void eliminarDesechosNoPeligrosos(
			PerforacionDesechosNoPeligrosos desechoNoPeligroso) {
		try {
			if (desechoNoPeligroso.getId() != null) {
				desechoNoPeligroso.setEstado(false);
				listaDesechoNoPeligrososEliminados.add(desechoNoPeligroso);
			}
			listadesechonopeligrosos.remove(desechoNoPeligroso);
		} catch (Exception e) {
			LOG.info("Error al eliminar", e);
		}
	}

	public void sustanciasQuimicas() {
		sustanciaQuimicasPeligrosas = descripcionProyectoFacade.obtenerSustanciasQuimicasPeligrosas();
	}

	public List<DesechoPeligroso> getDesechosPeligrosoDisponibles() {
		if (desechosPeligrosoDisponibles == null) {
			desechosPeligrosoDisponibles = buscarDesechos();
		}
		return desechosPeligrosoDisponibles;
	}

	@SuppressWarnings("unchecked")
	private List<DesechoPeligroso> buscarDesechos() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<DesechoPeligroso> listaGgeneradores = (List<DesechoPeligroso>) crudServiceBean
				.findByNamedQuery(DesechoPeligroso.FIND_ALL, parameters);
		return listaGgeneradores;
	}

	public void seleccionarDesecho() {
		desechopeligrosos.setWasteType(desechoPeligrosoSeleccionado.getDescripcion());
		desechopeligrosos.setCode(desechoPeligrosoSeleccionado.getClave());
		desechopeligrosos.setCrtib(desechoPeligrosoSeleccionado.getCritb());
	}

	public void nuevoDesechoPeligroso() throws ServiceException {
		desechopeligrosos = new PerforacionDesechosPeligrosos();
		desechoPeligrosoSeleccionado = new DesechoPeligroso();
		unidadSeleccionada = new UnidadMedida();
		edicionDesechoPeligroso = false;
	}

	public void eliminarDesechoPeligroso(PerforacionDesechosPeligrosos desecho)
			throws ServiceException {
		if (desecho.getId() != null) {
			desecho.setEstado(false);
			listaDesechoPeligrososEliminados.add(desecho);
		}
		listadesechopeligrosos.remove(desecho);
	}

	public void editarDesechoPeligroso(PerforacionDesechosPeligrosos desecho)
			throws ServiceException {
		desechopeligrosos = desecho;
		edicionDesechoPeligroso = true;

		desechoPeligrosoSeleccionado = new DesechoPeligroso();
		unidadSeleccionada = new UnidadMedida();

		if (desechosPeligrosoDisponibles != null) {
			for (DesechoPeligroso desechoPeligroso : desechosPeligrosoDisponibles) {
				if (desechoPeligroso.getDescripcion().compareTo(
						desechopeligrosos.getWasteType()) == 0) {
					desechoPeligrosoSeleccionado = desechoPeligroso;
					break;
				}
			}
		}

		for (UnidadMedida unidadMedida : listaUnidades) {
			if (unidadMedida.getSiglas().compareTo(desechopeligrosos.getUnit()) == 0) {
				unidadSeleccionada = unidadMedida;
				break;
			}
		}

	}

	public void adicionarDesechoPeligroso() throws ServiceException {
		if (!edicionDesechoPeligroso) {
			listadesechopeligrosos.add(desechopeligrosos);
		}
		desechopeligrosos.setModificado(true);
	}

	public void traerCertificado() {
		ci = certificadoInterseccionFacade.recuperarCertificadoInterseccion(proyecto);
	}

	public String getvalorCertificadoInterseccion() {
		boolean valor = certificadoInterseccionFacade.valorInterseccionProyecto(proyecto.getCodigo());
		if (valor == true) {
			return "SI";
		}
		return "NO";

	}

	public String getFechaCreacionCertificado(Date fechaCreacion) {
		DateFormat fecha = DateFormat.getDateInstance(DateFormat.FULL,
				new Locale("es"));
		return fecha.format(fechaCreacion);
	}

	public List<InterseccionProyecto> interseccionesProyecto(
			String codigoProyecto) {
		List<InterseccionProyecto> intersecciones = certificadoInterseccionService.getListaInterseccionProyectoIntersecaCapas(codigoProyecto);
		return intersecciones;
	}

	public String nombresIntersecciones(String codigoProyecto)
			throws ServiceException {
		String intersect = "";
		List<InterseccionProyecto> intersecciones = this.interseccionesProyecto(codigoProyecto);

		capas = capasFacade.listaCapas();

		for (InterseccionProyecto interseccion : intersecciones) {
			List<DetalleInterseccionProyecto> detalleInter = certificadoInterseccionService.getDetallesInterseccion(interseccion.getId());
			for (DetalleInterseccionProyecto detalleInterseccionProyecto : detalleInter) {
				intersect += detalleInterseccionProyecto.getInterseccionProyecto().getDescripcion()
						+ ":"
						+ detalleInterseccionProyecto.getNombre() + ",";
			}
		}
		if (intersect.equals("")) {
			intersect = "NO INTERSECA con ";
			String nota = " el Sistema Nacional de Áreas Protegidas (SNAP), Patrimonio Forestal del Estado (PFE), Bosques y Vegetación Protectora (BVP)";
			// for(Capa capa: capas){
			// intersect+=capa.getNombre()+",";
			// }
			// intersect=intersect.substring(0,intersect.lastIndexOf(","));
			// intersect=intersect;
			return intersect + nota;
		} else {
			String valor = " SI INTERSECA con " + intersect;
			intersect = valor;
			intersect = intersect.substring(0, intersect.lastIndexOf(","));
			return intersect;
		}
	}

	public void descargarViabilidad() {
		try {
			UtilDocumento.descargarPDF(
					certificadoViabilidadAmbientalInterseccionProyectoService.descargarCertifiadoViabilidad(proyectosBean.getProyecto()),
					"Oficio de certificado de viabilidad");
		} catch (Exception e) {
			LOG.error("error al descargar", e);
		}
	}

	public List<DesechoPeligroso> getDesechosSeleccionados() {
		if (desechosSeleccionados == null)
			desechosSeleccionados = new ArrayList<DesechoPeligroso>();
		return desechosSeleccionados;
	}

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	public void initRgd() {
		setCatalogo(generarArbol(null));
	}

	public void init(Integer idDesecho) {
		setCatalogo(generarArbol(idDesecho));
	}

	public void reset(Integer id) {
		filter = null;
		init(id);
	}

	public void reset() {
		reset(null);
	}

	public TreeNode generarArbol(Integer idDesecho) {
		TreeNode root = new DefaultTreeNode();
		if (idDesecho == null || idDesecho == 0) {
			if (getFilter().trim().isEmpty()) {
				List<CategoriaFuenteDesechoPeligroso> categorias = desechoPeligrosoFacade
						.buscarCategoriasFuentesDesechosPeligroso();
				List<CategoriaFuenteDesechoPeligroso> categoriasGenerar = new ArrayList<CategoriaFuenteDesechoPeligroso>();
				if (categorias != null) {
					CategoriaFuenteDesechoPeligroso categoriaFuentesEspecificas = new CategoriaFuenteDesechoPeligroso();
					categoriaFuentesEspecificas
							.setNombre(CategoriaFuenteDesechoPeligroso.NOMBRE_FUENTES_ESPECIFICAS);
					categoriaFuentesEspecificas
							.setCodigo(CategoriaFuenteDesechoPeligroso.CODIGO_ESPECIFICAS);

					for (CategoriaFuenteDesechoPeligroso categoria : categorias) {
						if (!categoria
								.getCodigo()
								.equals(CategoriaFuenteDesechoPeligroso.CODIGO_NO_ESPECIFICA)
								&& !categoria
										.getCodigo()
										.equals(CategoriaFuenteDesechoPeligroso.CODIGO_ESPECIALES))
							categoriaFuentesEspecificas
									.getCategoriasFuenteDesechoPeligroso().add(
											categoria);
						else
							categoriasGenerar.add(categoria);
					}
					if (!categoriaFuentesEspecificas
							.getCategoriasFuenteDesechoPeligroso().isEmpty())
						categoriasGenerar.add(0, categoriaFuentesEspecificas);

					for (CategoriaFuenteDesechoPeligroso categoria : categoriasGenerar) {
						fillTree(categoria, root, true);
					}
				}
			} else {
				List<DesechoPeligroso> desechos = desechoPeligrosoFacade
						.buscarDesechosPeligrosoPorDescripcion(getFilter());
				for (DesechoPeligroso desecho : desechos) {
					new DefaultTreeNode(getTipoNotod(desecho), desecho, root);
				}
			}
		} else {
			List<PoliticaDesechosActividad> lista = desechoPeligrosoFacade
					.politicaDesechosActividad(idDesecho);
			if (lista.size() > 0) {
				for (PoliticaDesechosActividad desecho : lista) {
					new DefaultTreeNode(
							getTipoNotod(desecho.getDesechoPeligroso()),
							desecho.getDesechoPeligroso(), root);
				}
			}
			// DesechoPeligroso desecho = desechoPeligrosoFacade.get(idDesecho);
			// new DefaultTreeNode(getTipoNotod(desecho), desecho, root);
		}
		return root;
	}

	public void onNodeExpand(NodeExpandEvent event) {
		List<TreeNode> nodes = event.getTreeNode().getChildren();
		for (TreeNode tree : nodes) {
			fillTree((EntidadBase) tree.getData(), tree, false);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) {
		DesechoPeligroso desecho = (DesechoPeligroso) event.getTreeNode()
				.getData();
		desechoPeligrosoSeleccionado = desecho;
		seleccionarDesecho();
		if (!getDesechosSeleccionados().contains(desecho)) {
			getDesechosSeleccionados().add(desecho);
			if (completeOperationOnAdd != null)
				completeOperationOnAdd.endOperation(desecho);
		}
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
		eliminarDesecho((DesechoPeligroso) event.getTreeNode().getData());
	}

	public void eliminarDesecho(DesechoPeligroso desecho) {
		if (getDesechosSeleccionados().contains(desecho)) {
			getDesechosSeleccionados().remove(desecho);
			// modificacionesEliminaciones = true;
			if (completeOperationOnDelete != null)
				completeOperationOnDelete.endOperation(desecho);
		}
	}

	public void validateDesechos(FacesContext context, UIComponent validate,
			Object value) {
		if (getDesechosSeleccionados().isEmpty())
			throw new ValidatorException(
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe seleccionar, al menos, un desecho especial/peligroso.",
							null));
	}

	private void fillTree(EntidadBase entidadBase, TreeNode root,
			boolean include) {
		TreeNode parent = null;
		if (include)
			parent = new DefaultTreeNode(getTipoNotod(entidadBase),
					entidadBase, root);
		else
			parent = root;
		if (parent.getType().equals(TYPE_FOLDER)) {
			parent.setSelectable(false);
			if (entidadBase instanceof CategoriaFuenteDesechoPeligroso) {
				CategoriaFuenteDesechoPeligroso categoria = (CategoriaFuenteDesechoPeligroso) entidadBase;
				if (categoria.getCodigo().equals(
						CategoriaFuenteDesechoPeligroso.CODIGO_ESPECIFICAS)) {
					for (CategoriaFuenteDesechoPeligroso subCategoria : categoria
							.getCategoriasFuenteDesechoPeligroso()) {
						new DefaultTreeNode(getTipoNotod(subCategoria),
								subCategoria, parent);
					}
				} else {
					List<FuenteDesechoPeligroso> fuentes = desechoPeligrosoFacade
							.buscarFuentesDesechosPeligrosoPorCategeria(categoria);
					for (FuenteDesechoPeligroso fuente : fuentes) {
						List<DesechoPeligroso> desechos = desechoPeligrosoFacade
								.buscarDesechosPeligrosoPorFuente(fuente);
						for (DesechoPeligroso desecho : desechos) {
							new DefaultTreeNode(getTipoNotod(desecho), desecho,
									parent);
						}
					}
				}
			}
			if (entidadBase instanceof FuenteDesechoPeligroso) {
				List<DesechoPeligroso> desechos = desechoPeligrosoFacade
						.buscarDesechosPeligrosoPorFuente((FuenteDesechoPeligroso) entidadBase);
				for (DesechoPeligroso desecho : desechos) {
					new DefaultTreeNode(getTipoNotod(desecho), desecho, parent);
				}
			}
		}
	}

	private String getTipoNotod(EntidadBase entidadBase) {
		return entidadBase instanceof DesechoPeligroso ? TYPE_DOCUMENT
				: TYPE_FOLDER;
	}

	private String getUbicacionProyecto(List<UbicacionesGeografica> ubicaciones)
			throws Exception {
		Set<UbicacionesGeografica> ubicacionesProvincia = new LinkedHashSet<UbicacionesGeografica>();
		for (UbicacionesGeografica ubicacionesGeografica : ubicaciones) {
			ubicacionesProvincia.add(ubicacionesGeografica
					.getUbicacionesGeografica().getUbicacionesGeografica());
		}
		String stringUbicaciones = ubicacionesProvincia.toString();
		return stringUbicaciones.replace('[', '(').replace(']', ')');

	}

	public String nombresInterseccionesRcoa() throws ServiceException {
		String intersect = "";

		List<DetalleInterseccionProyectoAmbiental> detalleInter = detalleInterseccionProyectoAmbientalFacade
				.buscarPorProyecto(proyectoLicenciaCoa);

		for (DetalleInterseccionProyectoAmbiental detalleInterseccionProyecto : detalleInter) {
			intersect += detalleInterseccionProyecto
					.getInterseccionProyectoLicenciaAmbiental()
					.getDescripcionCapa()
					+ ":"
					+ detalleInterseccionProyecto.getNombreGeometria()
					+ ",";
		}
		if (intersect.isEmpty()) {
			String nota = " el Sistema Nacional de Áreas Protegidas (SNAP), Patrimonio Forestal del Estado (PFE), Bosques y Vegetación Protectora (BVP)";
			return "NO INTERSECA con " + nota;
		} else {
			intersect = "SI INTERSECA con " + intersect;
			return intersect.substring(0, intersect.lastIndexOf(","));
		}
	}

	public Integer validarAnexos() {
		Integer anexo = 0;
		List<Documento> documentoLegalList = documentosFacade
				.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
						CategoriaIILicencia.class.getSimpleName(),
						TipoDocumentoSistema.DOCUMENTOS_LEGALES_020);
		if (documentoLegalList.isEmpty())
			anexo = 1;
		List<Documento> documentoregistroFotograficolList = documentosFacade
				.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
						CategoriaIILicencia.class.getSimpleName(),
						TipoDocumentoSistema.REGISTRO_FOTOGRAFICO_020);
		if (documentoregistroFotograficolList.isEmpty())
			anexo = 1;
		List<Documento> documentoMapalList = documentosFacade
				.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
						CategoriaIILicencia.class.getSimpleName(),
						TipoDocumentoSistema.MAPAS_020);
		if (documentoMapalList.isEmpty())
			anexo = 1;
		List<Documento> documentoPolizaList = documentosFacade
				.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
						CategoriaIILicencia.class.getSimpleName(),
						TipoDocumentoSistema.POLIZA_GARANTIA_020);
		if (documentoPolizaList.isEmpty())
			anexo = 1;

		return anexo;
	}

	public Integer validarAnexosRcoa() {
		Integer anexo = 0;
		try {
			List<DocumentosCOA> listaTituloMinero = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.TITULO_MINERO_020,CategoriaIILicencia.class.getSimpleName());
			if (listaTituloMinero == null || listaTituloMinero.isEmpty())
				anexo = 1;

			List<DocumentosCOA> listaGarantia = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.GARANTIA_020_020,CategoriaIILicencia.class.getSimpleName());
			if (listaGarantia == null || listaGarantia.isEmpty())
				anexo = 1;

			List<DocumentosCOA> listaDeclaracion = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.DECLARACION_020,CategoriaIILicencia.class.getSimpleName());
			if (listaDeclaracion == null || listaDeclaracion.isEmpty())
				anexo = 1;

			List<DocumentosCOA> listaFacturas = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.FACTURAS_020,CategoriaIILicencia.class.getSimpleName());
			if (listaFacturas == null || listaFacturas.isEmpty())
				anexo = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return anexo;
	}

	public byte[] generarFicha020() {
		try {
			File file = cargarDatosPdfFile();

			long idProcessInstance = bandejaTareasBean.getTarea().getProcessInstanceId();

			MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
			byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

			DocumentosCOA documentoFicha = new DocumentosCOA();
			documentoFicha.setContenidoDocumento(data);
			documentoFicha.setNombreDocumento("Ficha Mineria.pdf");
			documentoFicha.setExtencionDocumento(mimeTypesMap.getContentType(file));
			documentoFicha.setTipo(mimeTypesMap.getContentType(file));
			documentoFicha.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
			documentoFicha.setIdTabla(proyectoLicenciaCoa.getId());
			documentoFicha.setProyectoLicenciaCoa(proyectoLicenciaCoa);
			documentoFicha.setIdProceso(idProcessInstance);

			documentoFicha = documentosRcoaFacade.guardarDocumentoAlfresco(
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					"LicenciaAmbientalCategoriaII", idProcessInstance,
					documentoFicha,
					TipoDocumentoSistema.TIPO_DOCUMENTO_FICHA_MINERIA);

			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public File cargarDatosPdfFile() {

		cargaDatosMineria020();

		String fecha;
		fecha = "Quito " + JsfUtil.devuelveFechaEnLetrasSinHora(new Date());

		String[] parametros = { proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
				fecha };

		Area area = proyectoLicenciaCoa.getAreaResponsable();

		Object x = entityFichaMineria020Reporte;

		String cadenaHtml = UtilFichaMineria.extraeHtml(JsfUtil
				.devolverPathReportesHtml("fichaAmbientalMineria020.html"));

		return UtilFichaMineria.generarFichero(cadenaHtml, x, "fichaMnieria",
				true, area, parametros);

	}

	public void cargaDatosMineria020() {
		try {

			entityFichaMineria020Reporte = new EntityFichaMineria020Reporte();

			PerforacionExplorativa perforacionExplorativa = fichaAmbientalMineria020Facade
					.cargarPerforacionExplorativaRcoa(proyectoLicenciaCoa
							.getId());

			String titulo = "";
			if (perforacionExplorativa.getCodeUpdate() == null
					|| perforacionExplorativa.getCodeUpdate().equals(""))
				titulo = "REGISTRO AMBIENTAL PARA EXPLORACIÓN INICIAL EN EL QUE SE INCLUYE SONDEOS DE PRUEBA O RECONOCIMIENTO";
			else
				titulo = "ACTUALIZACIÓN DEL REGISTRO AMBIENTAL PARA EXPLORACIÓN INICIAL EN EL QUE SE INCLUYE SONDEOS DE PRUEBA O RECONOCIMIENTO";

			entityFichaMineria020Reporte.setTitulo(titulo);
			entityFichaMineria020Reporte.setNombre_proyecto(proyectoLicenciaCoa
					.getNombreProyecto());
			entityFichaMineria020Reporte.setNombre_catalogo(actividadPrincipal
					.getCatalogoCIUU().getNombre());

			// punto 3
			entityFichaMineria020Reporte.setCodigo_suia(proyectoLicenciaCoa
					.getCodigoUnicoAmbiental());
			String superficie = "";
			concesionesMinerasRcoa = proyectoConcesionesMinerasFacade
					.cargarConcesiones(proyectoLicenciaCoa);

			if (concesionesMinerasRcoa.size() > 0) {
				for (ProyectoLicenciaAmbientalConcesionesMineras concesiones : concesionesMinerasRcoa) {
					superficie = superficie + "<p>" + concesiones.getNombre()
							+ " Código:" + concesiones.getCodigo() + " "
							+ concesiones.getArea() + "</p>";
				}
			}
			entityFichaMineria020Reporte.setSuperficie_concesion(superficie);

			UbicacionesGeografica ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade
					.ubicacionPrincipal(proyectoLicenciaCoa)
					.getUbicacionesGeografica();
			parroquia = ubicacionPrincipal.getNombre();
			canton = ubicacionPrincipal.getUbicacionesGeografica().getNombre();
			provincia = ubicacionPrincipal.getUbicacionesGeografica()
					.getUbicacionesGeografica().getNombre();

			entityFichaMineria020Reporte.setParroquia(parroquia);
			entityFichaMineria020Reporte.setCanton(canton);
			entityFichaMineria020Reporte.setProvincia(provincia);
			entityFichaMineria020Reporte.setTipo_parroquia(proyectoLicenciaCoa
					.getTipoPoblacion().getNombre());

			String coordenadas = "<table width=\"100%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">";
			for (CoordendasPoligonos formacoordenadas : coordenadasImplantacion) {
				coordenadas = coordenadas + "<tr>"
						+ "<td colspan=\"2\">Grupo de coordenadas ("
						+ formacoordenadas.getTipoForma().getNombre()
						+ ")</td>" + "</tr>" + "<tr>" + " <td>X</td>"
						+ " <td>Y</td>" + "</tr>";

				for (CoordenadasProyecto listcordenadas : formacoordenadas
						.getCoordenadas()) {
					coordenadas = coordenadas + "<tr>" + " <td>"
							+ listcordenadas.getX() + "</td>" + "<td>"
							+ listcordenadas.getY() + "</td>" + "</tr>";
				}
			}

			coordenadas = coordenadas + "</table>";
			entityFichaMineria020Reporte.setCoordenadas(coordenadas);

			String coordenadasMineria = "<table width=\"100%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>" + " <td>X</td>" + " <td>Y</td>" + "</tr>";
			List<PerforacionCoordenadas> listaCoordenadas = new ArrayList<PerforacionCoordenadas>();
			try {
				listaCoordenadas = fichaAmbientalMineria020Facade
						.cargarPerforacionCoordendas(perforacionExplorativa);
				for (PerforacionCoordenadas perforacionCoordenadas : listaCoordenadas) {
					coordenadasMineria = coordenadasMineria + "<tr>" + " <td>"
							+ perforacionCoordenadas.getX() + "</td>" + "<td>"
							+ perforacionCoordenadas.getY() + "</td>" + "</tr>";

				}
				coordenadasMineria = coordenadasMineria + "</table>";
				entityFichaMineria020Reporte
						.setCoordenadas_derecho_minero(coordenadasMineria);
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}

			try {
				List<Contacto> contactos = contactoFacade
						.buscarUsuarioNativeQuery(String
								.valueOf(proyectoLicenciaCoa.getUsuario()
										.getNombre()));
				for (Contacto contacto : contactos) {
					if (contacto.getFormasContacto().getId()
							.equals(FormasContacto.EMAIL)) {
						entityFichaMineria020Reporte.setEmail(contacto
								.getValor());
					}
					if (contacto.getFormasContacto().getId()
							.equals(FormasContacto.TELEFONO)) {
						entityFichaMineria020Reporte.setTelefono(contacto
								.getValor());
					}
					if (contacto.getFormasContacto().getId()
							.equals(FormasContacto.CELULAR)) {
						entityFichaMineria020Reporte.setCelular(contacto
								.getValor());
					}
					if (contacto.getFormasContacto().getId()
							.equals(FormasContacto.DIRECCION)) {
						entityFichaMineria020Reporte.setDomicilio(contacto
								.getValor());
					}
					if (contacto.getOrganizacion() != null) {
						entityFichaMineria020Reporte.setTitular_minero(contacto
								.getOrganizacion().getNombre());
						String org = proyectoLicenciaCoa.getUsuario()
								.getNombre();
						Organizacion orgd = organizacionFacade
								.buscarPorRuc(org);
						entityFichaMineria020Reporte.setRepresentate_legal(orgd
								.getPersona().getNombre());
					} else {
						entityFichaMineria020Reporte.setTitular_minero(contacto
								.getPersona().getNombre());
						entityFichaMineria020Reporte
								.setRepresentate_legal(contacto.getPersona()
										.getNombre());
					}
				}
			} catch (ServiceException e) {
				e.printStackTrace();
			}

			if (perforacionExplorativa.getJudicialLocker() == null)
				entityFichaMineria020Reporte.setCasillero_judicial("S/N");
			else
				entityFichaMineria020Reporte
						.setCasillero_judicial(perforacionExplorativa
								.getJudicialLocker());

			Consultor consultor = perforacionExplorativa.getConsultor();
			entityFichaMineria020Reporte.setConsultor(consultor.getNombre());
			entityFichaMineria020Reporte.setRegistro_consultor(consultor
					.getRegistro());
			entityFichaMineria020Reporte.setEmail_consultor(consultor
					.getEmail());
			entityFichaMineria020Reporte.setTelefono_consultor(consultor
					.getTelefono());

			String equipoMultidisciplinario = "<table width=\"100%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
					+ "<tr>"
					+ "<td style=\"width: 33%\">Nombre</td>"
					+ "<td style=\"width: 33%\">Formación Profecional</td>"
					+ "<td style=\"width: 33%\">Componente</td>" + "</tr>";
			List<PerforacionEquipoMultidisciplinario> listaEquipoMultidis = new ArrayList<PerforacionEquipoMultidisciplinario>();
			try {
				listaEquipoMultidis = fichaAmbientalMineria020Facade
						.cargarEquipoMultidisciplinario(perforacionExplorativa);
				for (PerforacionEquipoMultidisciplinario perforacionEquipoMultidisciplinario : listaEquipoMultidis) {
					equipoMultidisciplinario = equipoMultidisciplinario
							+ "<tr>"
							+ "<td>"
							+ perforacionEquipoMultidisciplinario.getName()
							+ "</td>"
							+ "<td>"
							+ perforacionEquipoMultidisciplinario
									.getVocationalTraining()
							+ "</td>"
							+ "<td>"
							+ perforacionEquipoMultidisciplinario
									.getComponent() + "</td>" + "</tr>";
				}
				equipoMultidisciplinario = equipoMultidisciplinario
						+ "</table>";
				entityFichaMineria020Reporte
						.setTabla_equipo_multidiciplinario(equipoMultidisciplinario);

			} catch (ServiceException e) {
				e.printStackTrace();
			}

			// punto 4

			// permiso_ejecucion
			// certificado_interseccion
			// certificado_viabilidad
			// permisos_ambientales_anteriores

			String antecedenteProyecto = "";
			if (detalleInterseccionProyectoAmbientalFacade
					.isProyectoIntersecaCapas(proyectoLicenciaCoa
							.getCodigoUnicoAmbiental())) {
				antecedenteProyecto += "<p><b>Permiso de ejecución del proyecto (título minero)</b></p>";
				antecedenteProyecto += "<p align=\"justify\">"
						+ perforacionExplorativa.getProjectExecutionPermit()
						+ "</p>";
				antecedenteProyecto += "<p><b>Certificado de Intersección</b></p>";
				antecedenteProyecto += "<p align=\"justify\">"
						+ perforacionExplorativa.getIntersectionCertificate()
						+ "</p>";
				antecedenteProyecto += "<p><b>Certificado de Viabilidad emitido por la Dirección Nacional Forestal</b></p>";
				antecedenteProyecto += "<p align=\"justify\">"
						+ perforacionExplorativa.getFeasibilityCertificate()
						+ "</p>";
			} else {
				antecedenteProyecto += "<p>Permiso de ejecución del proyecto (título minero)</p>";
				antecedenteProyecto += "<p align=\"justify\">"
						+ perforacionExplorativa.getProjectExecutionPermit()
						+ "</p>";
				antecedenteProyecto += "<p>Certificado de Intersección</p>";
				antecedenteProyecto += "<p align=\"justify\">"
						+ perforacionExplorativa.getIntersectionCertificate()
						+ "</p>";
			}
			entityFichaMineria020Reporte
					.setAntecedentes_proyecto(antecedenteProyecto);

			// punto 5
			entityFichaMineria020Reporte
					.setResumen_proyecto(perforacionExplorativa
							.getProjectSummary());

			// punto 6
			entityFichaMineria020Reporte
					.setAreas_intervenidas(perforacionExplorativa
							.getIntervenedAreas());
			entityFichaMineria020Reporte
					.setTecnicas_exploracion(perforacionExplorativa
							.getExplorationTechniques());
			entityFichaMineria020Reporte
					.setEtapas_cierre_proyecto(perforacionExplorativa
							.getProjectClosureStages());
			entityFichaMineria020Reporte
					.setActivdades_complementarias(perforacionExplorativa
							.getInfrastructureComplementaryActivities());
			entityFichaMineria020Reporte
					.setAcceso_sitios(perforacionExplorativa
							.getAccessesTrails());
			entityFichaMineria020Reporte
					.setInfraestructura_temporal(perforacionExplorativa
							.getTemporaryInfrastructureImplementation());
			entityFichaMineria020Reporte
					.setCampamentos_temporles(perforacionExplorativa
							.getTemporaryCamps());

			String maquinariasEquipos = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
					+ "<tr>"
					+ "<td style=\"width: 25%\">Denominación de la maquinaria, equipos y materiales</td>"
					+ "<td style=\"width: 25%\">Cantidad (unidades)</td>"
					+ "<td style=\"width: 25%\">Características</td>"
					+ "<td style=\"width: 25%\">Uso / Proceso</td>" + "</tr>";
			List<PerforacionMaquinasEquipos> listaMaquinariaEquipos = new ArrayList<PerforacionMaquinasEquipos>();
			try {
				listaMaquinariaEquipos = fichaAmbientalMineria020Facade
						.cargarMaquinariaEquipo(perforacionExplorativa);
				for (PerforacionMaquinasEquipos perforacionMaquinasEquipos : listaMaquinariaEquipos) {
					maquinariasEquipos = maquinariasEquipos + "<tr>" + "<td>"
							+ perforacionMaquinasEquipos.getName() + "</td>"
							+ "<td>" + perforacionMaquinasEquipos.getUnit()
							+ "</td>" + "<td>"
							+ perforacionMaquinasEquipos.getCharacteristics()
							+ "</td>" + "<td>"
							+ perforacionMaquinasEquipos.getProcess() + "</td>"
							+ "</tr>";
				}
				maquinariasEquipos = maquinariasEquipos + "</table>";
				entityFichaMineria020Reporte
						.setTabla_maquinaria_equipos(maquinariasEquipos);
			} catch (ServiceException e) {
				e.printStackTrace();
			}

			String materialesInsumo = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
					+ "<tr>"
					+ "<td style=\"width: 25%\">Material (combustibles, insumos, productos químicos)</td>"
					+ "<td style=\"width: 25%\">Cantidad (Unidades, kg, gal, etc.) /año</td>"
					+ "<td style=\"width: 25%\">Proceso en el que es empleado</td>"
					+ "<td style=\"width: 25%\">No. CAS /ONU</td>" + "</tr>";
			List<PerforacionMaterialInsumo> listaMaterialInsumo = new ArrayList<PerforacionMaterialInsumo>();
			try {
				listaMaterialInsumo = fichaAmbientalMineria020Facade
						.cargarMaterialInsumo(perforacionExplorativa);
				for (PerforacionMaterialInsumo perforacionMaterialInsumo : listaMaterialInsumo) {
					materialesInsumo = materialesInsumo
							+ "<tr>"
							+ "<td>"
							+ perforacionMaterialInsumo
									.getSustanciaQuimicaPeligrosa()
									.getDescripcion() + "</td>" + "<td>"
							+ perforacionMaterialInsumo.getValue() + " "
							+ perforacionMaterialInsumo.getUnit() + "</td>"
							+ "<td>" + perforacionMaterialInsumo.getProcess()
							+ "</td>" + "<td>"
							+ perforacionMaterialInsumo.getCasonu() + "</td>"
							+ "</tr>";
				}
				materialesInsumo = materialesInsumo + "</table>";
				entityFichaMineria020Reporte
						.setTabla_materiales_insumos(materialesInsumo);
			} catch (ServiceException e) {
				e.printStackTrace();
			}

			String desechonopeligrosos = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
					+ "<tr>"
					+ "<td style=\"width: 25%\">Tipo de residuo/ desecho (Orgánico, Papel, Cartón, Plástico, Vidrio, etc.)</td>"
					+ "<td style=\"width: 25%\">Cantidad Generada aproximada /Mes (kg, ton, etc.)</td>"
					+ "<td style=\"width: 25%\">Reducción, tratamiento</td>"
					+ "<td style=\"width: 25%\">Disposición Final</td>"
					+ "</tr>";
			List<PerforacionDesechosNoPeligrosos> listadesechonopeligrosos = new ArrayList<PerforacionDesechosNoPeligrosos>();
			try {
				listadesechonopeligrosos = fichaAmbientalMineria020Facade
						.cargarDesechosNoPeligrosos(perforacionExplorativa);
				for (PerforacionDesechosNoPeligrosos perforacionDesechosNoPeligrosos : listadesechonopeligrosos) {
					desechonopeligrosos = desechonopeligrosos
							+ "<tr>"
							+ "<td>"
							+ perforacionDesechosNoPeligrosos.getWasteType()
							+ "</td>"
							+ "<td>"
							+ perforacionDesechosNoPeligrosos.getValue()
							+ " "
							+ perforacionDesechosNoPeligrosos.getUnit()
							+ "</td>"
							+ "<td>"
							+ perforacionDesechosNoPeligrosos.getTreatment()
							+ "</td>"
							+ "<td>"
							+ perforacionDesechosNoPeligrosos
									.getFinalArrangement() + "</td>" + "</tr>";
				}
				desechonopeligrosos = desechonopeligrosos + "</table>";
				entityFichaMineria020Reporte
						.setTabla_desechos_no_peligrosos(desechonopeligrosos);
			} catch (ServiceException e) {
				e.printStackTrace();
			}

			String desechopeligrosos = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
					+ "<tr>"
					+ "<td style=\"width: 15%\">Tipo de Desecho</td>"
					+ "<td style=\"width: 15%\">Código (AM NO. 142 o el que lo reemplace)</td>"
					+ "<td style=\"width: 15%\">CRTIB</td>"
					+ "<td style=\"width: 15%\">Cantidad Generada aproximada /Mes</td>"
					+ "<td style=\"width: 15%\">Proceso o unidad operativa</td>"
					+ "<td style=\"width: 15%\">Tipo de Eliminación o Disposición final</td>"
					+ "</tr>";
			List<PerforacionDesechosPeligrosos> listadesechopeligrosos = new ArrayList<PerforacionDesechosPeligrosos>();
			try {
				listadesechopeligrosos = fichaAmbientalMineria020Facade
						.cargarDesechosPeligrosos(perforacionExplorativa);
				for (PerforacionDesechosPeligrosos perforacionDesechosPeligrosos : listadesechopeligrosos) {
					desechopeligrosos = desechopeligrosos
							+ "<tr>"
							+ "<td>"
							+ perforacionDesechosPeligrosos.getWasteType()
							+ "</td>"
							+ "<td>"
							+ perforacionDesechosPeligrosos.getCode()
							+ "</td>"
							+ "<td>"
							+ perforacionDesechosPeligrosos.getCrtib()
							+ "</td>"
							+ "<td>"
							+ perforacionDesechosPeligrosos.getValue()
							+ " "
							+ perforacionDesechosPeligrosos.getUnit()
							+ "</td>"
							+ "<td>"
							+ perforacionDesechosPeligrosos.getProcess()
							+ "</td>"
							+ "<td>"
							+ perforacionDesechosPeligrosos
									.getFinalArrangement() + "</td>" + "</tr>";
				}
				desechopeligrosos = desechopeligrosos + "</table>";
				entityFichaMineria020Reporte
						.setTabla_desechos_peligrosos(desechopeligrosos);
			} catch (ServiceException e) {
				e.printStackTrace();
			}

			entityFichaMineria020Reporte
					.setMano_obra_calificada(perforacionExplorativa
							.getQualifiedHandWork());
			entityFichaMineria020Reporte
					.setDiagrama_flujo_base(perforacionExplorativa
							.getBaseFlowDiagram());

			// punto 7
			entityFichaMineria020Reporte
					.setAgua_consumo_humano(perforacionExplorativa
							.getWaterHumanConsumption() ? "SI" : "NO");
			entityFichaMineria020Reporte
					.setAgua_perforacion(perforacionExplorativa
							.getWaterDrilling() ? "SI" : "NO");
			entityFichaMineria020Reporte
					.setEnergia_electrica(perforacionExplorativa
							.getElectricPower() ? "SI" : "NO");
			entityFichaMineria020Reporte.setCombustible(perforacionExplorativa
					.getFuelConsumption() ? "SI" : "NO");
			entityFichaMineria020Reporte.setConsumo_agua(perforacionExplorativa
					.getWaterConsumption());
			entityFichaMineria020Reporte
					.setConsumo_agua_perforacion(perforacionExplorativa
							.getWaterConsumptionDrilling());
			entityFichaMineria020Reporte
					.setConsumo_enegia_eletrica(perforacionExplorativa
							.getElectricPowerConsumption());
			entityFichaMineria020Reporte
					.setConsumo_combustible(perforacionExplorativa
							.getFuelTypeConsumption());

			// punto 8

			entityFichaMineria020Reporte
					.setInfluencia_directa_fisica(perforacionExplorativa
							.getDirectPhysicalInfluence());
			entityFichaMineria020Reporte
					.setInfluencia_directa_biotica(perforacionExplorativa
							.getDirectBioticInfluence());
			entityFichaMineria020Reporte
					.setInfluencia_directa_social(perforacionExplorativa
							.getDirectSocialInfluence());
			entityFichaMineria020Reporte
					.setInfluencia_indirecta_social(perforacionExplorativa
							.getIndirectPhysicalInfluence());
			entityFichaMineria020Reporte
					.setInfluencia_indirecta_social(perforacionExplorativa
							.getIndirectBioticInfluence());
			entityFichaMineria020Reporte
					.setInfluencia_indirecta_social(perforacionExplorativa
							.getIndirectSocialInfluence());

			// punto 9

			ComponenteFisicoSD componenteFisico = new ComponenteFisicoSD();
			ComponenteSocialSD componenteSocial = new ComponenteSocialSD();
			componenteFisico = componenteFisicoFacade
					.buscarComponenteFisicoPorPerforacionExplorativa(perforacionExplorativa
							.getId());
			componenteSocial = componenteSocialFacade
					.buscarComponenteSocialPorPerforacionExplorativa(perforacionExplorativa
							.getId());

			entityFichaMineria020Reporte
					.setPrecipitacion_anual(componenteFisico
							.getPrecipitacionAnual());
			entityFichaMineria020Reporte.setTemperatura_anual(componenteFisico
					.getTemperaturaMediaAnual());
			entityFichaMineria020Reporte.setAltitud(componenteFisico
					.getAltitud());
			entityFichaMineria020Reporte.setComponente_hidrico(componenteFisico
					.getComponenteHidrico());

			String geomorfologia = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 60%\">Pendiente</td>"
					+ " <td style=\"width: 40%\">Seleccionado</td>" + "</tr>";

			List<String> codigosFisicos = new ArrayList<String>();
			Map<String, List<CatalogoGeneralFisico>> listaGeneralFisico;
			listaGeneralFisico = new HashMap<String, List<CatalogoGeneralFisico>>();
			codigosFisicos.add(TipoCatalogo.CODIGO_PENDIENTE);
			List<CatalogoGeneralFisico> fisicos = catalogoFisicoFacade
					.obtenerListaFisicoTipo(codigosFisicos);
			for (CatalogoGeneralFisico catalogoGeneralFisico : fisicos) {
				List<CatalogoGeneralFisico> tmp = new ArrayList<CatalogoGeneralFisico>();
				String key = catalogoGeneralFisico.getTipoCatalogo()
						.getCodigo();
				if (listaGeneralFisico.containsKey(key)) {
					tmp = listaGeneralFisico.get(key);
				}
				tmp.add(catalogoGeneralFisico);
				listaGeneralFisico.put(key, tmp);
			}
			List<CatalogoGeneralFisico> pendienteSuelo = null;
			if (listaGeneralFisico.containsKey(TipoCatalogo.CODIGO_PENDIENTE)) {
				pendienteSuelo = listaGeneralFisico
						.get(TipoCatalogo.CODIGO_PENDIENTE);
			}

			for (ComponenteFisicoPendienteSD pendiente : componenteFisico
					.getComponenteFisicoPendienteList()) {
				for (CatalogoGeneralFisico pendienteCat : pendienteSuelo) {
					if (pendiente.getCatalogoFisico().getId()
							.equals(pendienteCat.getId())
							&& pendiente.getEstado() == true) {
						pendienteCat.setSeleccionado(true);
					}
				}
			}

			for (CatalogoGeneralFisico pendiente : pendienteSuelo) {
				String selecc = "";
				if (pendiente.isSeleccionado())
					selecc = "X";

				geomorfologia = geomorfologia + "<tr>" + "<td>"
						+ pendiente.getDescripcion() + "</td>" + "<td>"
						+ selecc + "</td>" + "</tr>";
			}
			geomorfologia = geomorfologia + "</table>";
			entityFichaMineria020Reporte.setPendiente(geomorfologia);

			inicializarDatosGeneralesBiotico();
			coberturaVegetalList = inicializarBiotico(TipoCatalogo.CODIGO_COBERTURA_VEGETAL_SD);
			pisosZooGeograficosList = inicializarBiotico(TipoCatalogo.CODIGO_PISOS_ZOOGEOGRAFICOS_SD);
			componenteBioticoNativoList = inicializarBiotico(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_NATIVO_SD);
			componenteBioticoIntroducidoList = inicializarBiotico(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_INTRODUCIDO_SD);
			aspectosEcologicosList = inicializarBiotico(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS_SD);
			areasSensiblesList = inicializarBiotico(TipoCatalogo.CODIGO_AREAS_SENSIBLES_SD);

			inicializarCobertura();

			listaBioticoCoberturaVegetal = new ArrayList<ComponenteBioticoSD>();
			listaBioticoPisosZooGeograficos = new ArrayList<ComponenteBioticoSD>();
			listaBioticoNativo = new ArrayList<ComponenteBioticoSD>();
			listaBioticoIntroducido = new ArrayList<ComponenteBioticoSD>();
			listaBioticoAspectoEcologico = new ArrayList<ComponenteBioticoSD>();
			listaBioticoAreasSensibles = new ArrayList<ComponenteBioticoSD>();

			List<ComponenteBioticoSD> listaBiotico;

			listaBiotico = componenteBioticoFacade
					.buscarComponenteBioticoPorPerforacionExplorativa(perforacionExplorativa
							.getId());
			for (ComponenteBioticoSD biotico : listaBiotico) {
				for (CatalogoGeneralBiotico cobertura : coberturaVegetalList) {
					if (cobertura.getCatalogoGeneralBioticoList() != null) {
						for (CatalogoGeneralBiotico coberturaHijo : cobertura
								.getCatalogoGeneralBioticoList()) {
							if (biotico.getCatalogoGeneralBiotico().getId()
									.equals(coberturaHijo.getId())) {
								coberturaHijo.setSeleccionado(true);
								listaBioticoCoberturaVegetal.add(biotico);
							}
						}
					}
				}

				for (CatalogoGeneralBiotico pisos : pisosZooGeograficosList) {
					if (biotico.getCatalogoGeneralBiotico().getId()
							.equals(pisos.getId())) {
						pisos.setSeleccionado(true);
						listaBioticoPisosZooGeograficos.add(biotico);
					}
				}

				for (CatalogoGeneralBiotico nativo : componenteBioticoNativoList) {
					if (biotico.getCatalogoGeneralBiotico().getId()
							.equals(nativo.getId())) {
						nativo.setSeleccionado(true);
						listaBioticoNativo.add(biotico);
					}
				}

				for (CatalogoGeneralBiotico introducido : componenteBioticoIntroducidoList) {
					if (biotico.getCatalogoGeneralBiotico().getId()
							.equals(introducido.getId())) {
						introducido.setSeleccionado(true);
						listaBioticoIntroducido.add(biotico);
					}
				}

				for (CatalogoGeneralBiotico ecologico : aspectosEcologicosList) {
					if (biotico.getCatalogoGeneralBiotico().getId()
							.equals(ecologico.getId())) {
						ecologico.setSeleccionado(true);
						listaBioticoAspectoEcologico.add(biotico);
					}
				}

				for (CatalogoGeneralBiotico area : areasSensiblesList) {
					if (biotico.getCatalogoGeneralBiotico().getId()
							.equals(area.getId())) {
						area.setSeleccionado(true);
						listaBioticoAreasSensibles.add(biotico);
					}
				}
			}

			String coberturavegetal = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 60%\">Cobertura Nivel II</td>"
					+ " <td style=\"width: 40%\">Seleccionado</td>" + "</tr>";

			for (CatalogoGeneralBiotico coberturaVegetalPadre : coberturaVegetalPadreList) {
				coberturavegetal = coberturavegetal
						+ "<tr><td style=\"text-align: left;\" colspan=\"2\">"
						+ coberturaVegetalPadre.getDescripcion() + "</td></tr>";
				for (CatalogoGeneralBiotico coberturaVegetalhijos : coberturaVegetalPadre
						.getCatalogoGeneralBioticoList()) {
					String selecc = "";
					if (coberturaVegetalhijos.isSeleccionado())
						selecc = "X";
					coberturavegetal = coberturavegetal + "<tr>"
							+ "<td style=\"text-align: left;\">"
							+ coberturaVegetalhijos.getDescripcion() + "</td>"
							+ "<td>" + selecc + "</td>" + "</tr>";
				}
			}
			coberturavegetal = coberturavegetal + "</table>";
			entityFichaMineria020Reporte
					.setTabla_componente_biotico_cobertura_v(coberturavegetal);

			String zooGeograficos = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 40%\">Pisos Zoo Geográficos</td>"
					+ " <td style=\"width: 40%\">Simbología</td>"
					+ " <td style=\"width: 20%\">Seleccionado</td>" + "</tr>";
			for (CatalogoGeneralBiotico pisosZooGeograficos : pisosZooGeograficosList) {
				String selecc = "";
				if (pisosZooGeograficos.isSeleccionado())
					selecc = "X";
				zooGeograficos = zooGeograficos + "<tr>"
						+ "<td style=\"text-align: left;\">"
						+ pisosZooGeograficos.getDescripcion() + "</td>"
						+ "<td style=\"text-align: left;\">"
						+ pisosZooGeograficos.getAyuda() + "</td>" + "<td>"
						+ selecc + "</td>" + "</tr>";
			}
			zooGeograficos = zooGeograficos + "</table>";
			entityFichaMineria020Reporte
					.setTabla_componente_biotico_pisos_zoogeograficos(zooGeograficos);

			String bioticoNativo = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td >Componentes bióticos nativos</td>"
					+ " <td>Seleccionado</td>" + "</tr>";
			for (CatalogoGeneralBiotico componenteBioticoNativo : componenteBioticoNativoList) {
				String selecc = "";
				if (componenteBioticoNativo.isSeleccionado())
					selecc = "X";
				bioticoNativo = bioticoNativo + "<tr>"
						+ "<td style=\"text-align: left;\">"
						+ componenteBioticoNativo.getDescripcion() + "</td>"
						+ "<td>" + selecc + "</td>" + "</tr>";
			}
			bioticoNativo = bioticoNativo + "</table>";
			entityFichaMineria020Reporte
					.setTabla_componente_biotico_nativo(bioticoNativo);

			String bioticoIntroducido = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 60%\">Componente biótico introducido</td>"
					+ " <td style=\"width: 40%\">Seleccionado</td>" + "</tr>";
			for (CatalogoGeneralBiotico componenteBioticoIntroducido : componenteBioticoIntroducidoList) {
				String selecc = "";
				if (componenteBioticoIntroducido.isSeleccionado())
					selecc = "X";
				bioticoIntroducido = bioticoIntroducido + "<tr>"
						+ "<td style=\"text-align: left;\">"
						+ componenteBioticoIntroducido.getDescripcion()
						+ "</td>" + "<td>" + selecc + "</td>" + "</tr>";
			}
			bioticoIntroducido = bioticoIntroducido + "</table>";
			entityFichaMineria020Reporte
					.setTabla_componente_biotico_introducido(bioticoIntroducido);

			String aspectosEcologicos = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 60%\">Aspectos ecológicos</td>"
					+ " <td style=\"width: 40%\">Seleccionado</td>" + "</tr>";
			for (CatalogoGeneralBiotico aspectosEcologicosL : aspectosEcologicosList) {
				String selecc = "";
				if (aspectosEcologicosL.isSeleccionado())
					selecc = "X";
				aspectosEcologicos = aspectosEcologicos + "<tr>"
						+ "<td style=\"text-align: left;\">"
						+ aspectosEcologicosL.getDescripcion() + "</td>"
						+ "<td>" + selecc + "</td>" + "</tr>";
			}
			aspectosEcologicos = aspectosEcologicos + "</table>";
			entityFichaMineria020Reporte
					.setTabla_componente_biotico_aspectos_ecologicos(aspectosEcologicos);

			String areasSensibles = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 60%\">Aspectos ecológicos</td>"
					+ " <td style=\"width: 40%\">Seleccionado</td>" + "</tr>";
			for (CatalogoGeneralBiotico areasSensiblesL : areasSensiblesList) {
				String selecc = "";
				if (areasSensiblesL.isSeleccionado())
					selecc = "X";
				areasSensibles = areasSensibles + "<tr>"
						+ "<td style=\"text-align: left;\">"
						+ areasSensiblesL.getDescripcion() + "</td>" + "<td>"
						+ selecc + "</td>" + "</tr>";
			}
			areasSensibles = areasSensibles + "</table>";
			entityFichaMineria020Reporte
					.setTabla_componente_biotico_areas_sensibles(areasSensibles);

			entityFichaMineria020Reporte.setComunidades(componenteSocial
					.getComunidades());
			entityFichaMineria020Reporte.setPoblaciones(componenteSocial
					.getPoblaciones());
			entityFichaMineria020Reporte.setPredios(componenteSocial
					.getPredios());
			entityFichaMineria020Reporte.setInfraestructura(componenteSocial
					.getInfraestructura());
			entityFichaMineria020Reporte
					.setAsentamientos_grupos_etnicos(componenteSocial
							.getAsentamientosGruposEtnicos());
			entityFichaMineria020Reporte
					.setInfraestructura_salud_publica(componenteSocial
							.getInfraestructuraSaludPublica());
			entityFichaMineria020Reporte
					.setElementos_culturales(componenteSocial
							.getElementosCulturales());

			// punto 11
			List<EntityActividad> listaActividades;
			String impactosAmbientales = "<table width=\"70%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td>Sub Actividad</td>"
					+ " <td>Equipos / Herramientas</td>"
					+ " <td>Impactos Identificados</td>"
					+ " <td>Descripción del impacto</td>"
					+ " <td>Agua</td>"
					+ " <td>Aire</td>"
					+ " <td>Suelo</td>"
					+ " <td>Biotico</td>"
					+ " <td>Paisaje</td>"
					+ " <td>Social</td>" + "</tr>";
			try {
				listaActividades = actividadesImpactoFacade
						.obtenerActividdaesProyecto(perforacionExplorativa
								.getId());
				for (EntityActividad objListaImpacto : listaActividades) {
					impactosAmbientales = impactosAmbientales
							+ "<tr><td style=\"text-align: left;\" colspan=\"10\"><strong>"
							+ objListaImpacto.getActividad()
							+ "</strong></td></tr>";
					for (ActividadesImpactoProyecto objImpacto : objListaImpacto
							.getSubactividades()) {
						String agua = objImpacto.isAgua() ? "X" : "";
						String aire = objImpacto.isAire() ? "X" : "";
						String suelo = objImpacto.isSuelo() ? "X" : "";
						String biotico = objImpacto.isBiotico() ? "X" : "";
						String paisaje = objImpacto.isPaisaje() ? "X" : "";
						String social = objImpacto.isSocial() ? "X" : "";
						impactosAmbientales = impactosAmbientales + "<tr>"
								+ "<td style=\"text-align: left;\">"
								+ objImpacto.getActividad().getNombre()
								+ "</td>" + "<td style=\"text-align: left;\">"
								+ objImpacto.getHerramientas() + "</td>"
								+ "<td style=\"text-align: left;\">"
								+ objImpacto.getImpacto() + "</td>"
								+ "<td style=\"text-align: left;\">"
								+ objImpacto.getDescripcionImpacto() + "</td>"
								+ "<td>" + agua + "</td>" + "<td>" + aire
								+ "</td>" + "<td>" + suelo + "</td>" + "<td>"
								+ biotico + "</td>" + "<td>" + paisaje
								+ "</td>" + "<td>" + social + "</td>" + "</tr>";
					}
				}
				impactosAmbientales = impactosAmbientales + "</table>";
				entityFichaMineria020Reporte
						.setTabla_identificacion_impactos_ambientales(impactosAmbientales);
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}
			// punto 12
			String pma = "<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td>MEDIDAS PROPUESTAS</td>"
					+ " <td>INDICADOR</td>"
					+ " <td>MEDIO DE VERIFICACIÓN</td>"
					+ " <td>FRECUENCIA</td>"
					+ " <td>PRESUPUESTO</td>"
					+ "</tr>";
			Map<String, List<PlanManejoAmbientalProyecto>> parameters = new HashMap<String, List<PlanManejoAmbientalProyecto>>();
			parameters = planManejoAmbiental020Facade
					.obtenerActividdaesProyecto(perforacionExplorativa.getId());
			for (Map.Entry<String, List<PlanManejoAmbientalProyecto>> entry : parameters
					.entrySet()) {
				String key = entry.getKey();
				pma = pma
						+ "<tr><td style=\"text-align: center;\" colspan=\"5\"><strong>"
						+ key + "</strong></td></tr>";
				List<PlanManejoAmbientalProyecto> values = entry.getValue();
				for (PlanManejoAmbientalProyecto planManejoAmbientalProyecto : values) {
					if (planManejoAmbientalProyecto.getPlanManejoAmbiental020() == null) {
						pma = pma
								+ "<tr>"
								+ " <td style=\"width: 20%\">"
								+ planManejoAmbientalProyecto
										.getMedidasPropuestas()
								+ "</td>"
								+ " <td style=\"width: 20%\">"
								+ planManejoAmbientalProyecto.getIndicador()
								+ "</td>"
								+ " <td style=\"width: 20%\">"
								+ planManejoAmbientalProyecto
										.getVerificacionMedios() + "</td>"
								+ " <td style=\"width: 20%\">"
								+ planManejoAmbientalProyecto.getFrecuencia()
								+ "</td>" + " <td style=\"width: 20%\">"
								+ planManejoAmbientalProyecto.getCalificacion()
								+ "</td>" + "</tr>";
					} else {
						if (planManejoAmbientalProyecto.getCalificacion() == null)
							pma = pma
									+ "<tr><td style=\"text-align: center;\" colspan=\"5\"><strong>"
									+ planManejoAmbientalProyecto
											.getPlanManejoAmbiental020()
											.getDescripcion()
									+ "</strong></td></tr>";
						else {
							pma = pma
									+ "<tr>"
									+ " <td style=\"width: 20%\">"
									+ planManejoAmbientalProyecto
											.getPlanManejoAmbiental020()
											.getMedidasPropuestas()
									+ "</td>"
									+ " <td style=\"width: 20%\">"
									+ planManejoAmbientalProyecto
											.getPlanManejoAmbiental020()
											.getIndicador()
									+ "</td>"
									+ " <td style=\"width: 20%\">"
									+ planManejoAmbientalProyecto
											.getPlanManejoAmbiental020()
											.getVerificacionMedios()
									+ "</td>"
									+ " <td style=\"width: 20%\">"
									+ planManejoAmbientalProyecto
											.getPlanManejoAmbiental020()
											.getFrecuencia()
									+ "</td>"
									+ " <td style=\"width: 20%\">"
									+ planManejoAmbientalProyecto
											.getCalificacion() + "</td>"
									+ "</tr>";
						}
					}
				}
			}
			pma = pma + "</table>";
			entityFichaMineria020Reporte.setTabla_prevencion_impactos(pma);
			// punto 13
			double total;
			String cronogramaPma = "<table width=\"700px\" border=\"1\" cellpadding=\"5\" bordercolor=\"#000000\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ "<td rowspan=\"2\">SUB PLAN-ACTIVIDADES</td>"
					+ "<td colspan=\"12\">MESES</td>"
					+ "<td rowspan=\"2\">VALOR</td>"
					+ "</tr>"
					+ "<tr>"
					+ "<td>1</td>"
					+ "<td>2</td>"
					+ "<td>3</td>"
					+ "<td>4</td>"
					+ "<td>5</td>"
					+ "<td>6</td>"
					+ "<td>7</td>"
					+ "<td>8</td>"
					+ "<td>9</td>"
					+ "<td>10</td>"
					+ "<td>11</td>"
					+ "<td>12</td>" + "</tr>";
			List<PerforacionCronogramaPma> listaCronogramaPma = new ArrayList<PerforacionCronogramaPma>();
			try {
				listaCronogramaPma = fichaAmbientalMineria020Facade
						.cargarCronogramaPma(perforacionExplorativa);
				total = 0;
				for (PerforacionCronogramaPma perforacionCronogramaPma : listaCronogramaPma) {
					String m1 = perforacionCronogramaPma.getMonth1() ? "X" : "";
					String m2 = perforacionCronogramaPma.getMonth2() ? "X" : "";
					String m3 = perforacionCronogramaPma.getMonth3() ? "X" : "";
					String m4 = perforacionCronogramaPma.getMonth4() ? "X" : "";
					String m5 = perforacionCronogramaPma.getMonth5() ? "X" : "";
					String m6 = perforacionCronogramaPma.getMonth6() ? "X" : "";
					String m7 = perforacionCronogramaPma.getMonth7() ? "X" : "";
					String m8 = perforacionCronogramaPma.getMonth8() ? "X" : "";
					String m9 = perforacionCronogramaPma.getMonth9() ? "X" : "";
					String m10 = perforacionCronogramaPma.getMonth10() ? "X"
							: "";
					String m11 = perforacionCronogramaPma.getMonth11() ? "X"
							: "";
					String m12 = perforacionCronogramaPma.getMonth12() ? "X"
							: "";
					cronogramaPma = cronogramaPma
							+ "<tr>"
							+ "<td style=\"text-align: left;\">"
							+ perforacionCronogramaPma
									.getTipoPlanManejoAmbiental().getTipo()
							+ "</td>" + "<td>" + m1 + "</td>" + "<td>" + m2
							+ "</td>" + "<td>" + m3 + "</td>" + "<td>" + m4
							+ "</td>" + "<td>" + m5 + "</td>" + "<td>" + m6
							+ "</td>" + "<td>" + m7 + "</td>" + "<td>" + m8
							+ "</td>" + "<td>" + m9 + "</td>" + "<td>" + m10
							+ "</td>" + "<td>" + m11 + "</td>" + "<td>" + m12
							+ "</td>" + "<td>"
							+ perforacionCronogramaPma.getBudget() + "</td>"
							+ "</tr>";
					total += perforacionCronogramaPma.getBudget();
				}
				cronogramaPma = cronogramaPma
						+ "<tr>"
						+ "<td colspan=\"13\" style=\"text-align: right;\">TOTAL</td>"
						+ "<td>" + total + "</td>" + "</tr></table>";
				entityFichaMineria020Reporte
						.setTabla_cronograma_actividades(cronogramaPma);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void inicializarDatosGeneralesBiotico() {
		List<String> codigosBioticos = new ArrayList<String>();
		listaGeneralBiotico = new HashMap<String, List<CatalogoGeneralBiotico>>();
		codigosBioticos.add(TipoCatalogo.CODIGO_COBERTURA_VEGETAL_SD);
		codigosBioticos.add(TipoCatalogo.CODIGO_PISOS_ZOOGEOGRAFICOS_SD);
		codigosBioticos.add(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_NATIVO_SD);
		codigosBioticos
				.add(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_INTRODUCIDO_SD);
		codigosBioticos.add(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS_SD);
		codigosBioticos.add(TipoCatalogo.CODIGO_AREAS_SENSIBLES_SD);
		List<CatalogoGeneralBiotico> bioticos = catalogoBioticoFacade
				.obtenerListaBioticoTipo(codigosBioticos);
		for (CatalogoGeneralBiotico catalogoGeneralBiotico : bioticos) {
			List<CatalogoGeneralBiotico> tmp = new ArrayList<CatalogoGeneralBiotico>();
			String key = catalogoGeneralBiotico.getTipoCatalogo().getCodigo();
			if (listaGeneralBiotico.containsKey(key)) {
				tmp = listaGeneralBiotico.get(key);
			}
			tmp.add(catalogoGeneralBiotico);
			listaGeneralBiotico.put(key, tmp);
		}
	}

	public List<CatalogoGeneralBiotico> inicializarBiotico(String codigo) {

		if (listaGeneralBiotico.containsKey(codigo)) {
			return listaGeneralBiotico.get(codigo);
		}
		return new ArrayList<CatalogoGeneralBiotico>();
	}

	public void inicializarCobertura() {
		coberturaVegetalPadreList = new ArrayList<>();
		for (CatalogoGeneralBiotico cobertura : coberturaVegetalList) {
			if (cobertura.getCatalogoPadre() == null) {
				coberturaVegetalPadreList.add(cobertura);
			}
		}
	}
}