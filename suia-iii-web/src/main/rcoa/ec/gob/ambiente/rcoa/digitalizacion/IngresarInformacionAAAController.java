package ec.gob.ambiente.rcoa.digitalizacion;

import index.Campos_coordenada;
import index.ContieneCapa_entrada;
import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.IntersecadoCapa_capa;
import index.Intersecado_capa;
import index.Intersecado_coordenada;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.InterseccionCapa_resultado;
import index.Reproyeccion_entrada;
import index.Reproyeccion_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;

import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AutorizacionesAdministrativasFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AreasProtegidasBosquesFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.CoordenadaDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.DetalleInterseccionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.DocumentoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.InterseccionProyectoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.LicenciaAmbientalFisicaFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProyectoAsociadoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ShapeDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.UbicacionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AreasProtegidasBosques;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.CoordenadaDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.DetalleInterseccionDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.DocumentoDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.InterseccionProyectoDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.ShapeDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.dto.EntityLicenciaFisica;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.CatalogoFasesCoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoFasesCoa;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCIUUFacade;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers.EmisionGeneradorConAAAController;
import ec.gob.ambiente.rcoa.util.MayorAreaVO;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FormaProyecto;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityFichaCompletaRgd;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.Categoria1Facade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.tipoforma.facade.TipoFormaFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;
import lombok.Getter;
import lombok.Setter;

@ViewScoped
@ManagedBean
public class IngresarInformacionAAAController {
	
	Logger LOG = Logger.getLogger(IngresarInformacionAAAController.class);
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private CatalogoFasesCoaFacade catalogoFasesCoaFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private CatalogoCIUUFacade catalogoCIUUFacade;
	@EJB
    private CrudServiceBean crudServiceBean;
	@EJB
	private CapasCoaFacade capasCoaFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private DocumentoDigitalizacionFacade documentoDigitalizacionFacade;
	@EJB
	private ShapeDigitalizacionFacade shapeDigitalizacionFacade;
	@EJB
	private CoordenadaDigitalizacionFacade coordenadaDigitalizacionFacade;
	@EJB
	private UbicacionDigitalizacionFacade ubicacionDigitalizacionFacade;
	@EJB
	private InterseccionProyectoDigitalizacionFacade interseccionProyectoDigitalizacionFacade;
	@EJB
	private DetalleInterseccionDigitalizacionFacade detalleInterseccionDigitalizacionFacade;
	@EJB
	private AreasProtegidasBosquesFacade areasProtegidasBosquesFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoAsociadoDigitalizacionFacade proyectoAsociadoFacade;
	@EJB
	private LicenciaAmbientalFisicaFacade licenciaAmbientalFisicaFacade;
	@EJB
	private TipoFormaFacade tipoFormaFacade;
	@EJB
	private CatalogoCategoriasFacade catalogoCategoriaFaseFacade;
	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	@EJB
	private Categoria1Facade categoria1Facade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionFacade;
	@EJB
	private InformeOficioFacade informeOficioFacade;
	@EJB
	private AutorizacionesAdministrativasFacade autorizacionesAdministrativasFacade;
	@EJB
	private ProcesoFacade procesoFacade;
    @EJB
    private SecuenciasFacade secuenciasFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{autorizacionAdministrativaAmbientalBean}")
	private AutorizacionAdministrativaAmbientalBean autorizacionAdministrativaAmbientalBean;

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
	private List<String> listaBosques;
	
	@Getter
	@Setter
	private List<String> listaSnap;
	
	@Getter
	@Setter
	private String nombreAreaProtegida;
	
	@Getter
	@Setter
	private String nombreBosqueProtector;
	
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativa;
	
	@Getter
	@Setter
	private List<TipoSector> listaSectores;
	
	@Getter
	@Setter
	private Integer idSector;
	
	@Getter
	@Setter
	private Integer idFase;
	
	@Getter
	@Setter
	private List<CatalogoFasesCoa> listaFases;
	
	@Getter
	@Setter
	private boolean bloquearFase;
	
	@Getter
	@Setter
	private List<Area> listaAreasEmisoras;
	
	@Getter
	@Setter
	private List<Area> listaAreasControl;
	
	@Getter
	@Setter
	private List<Area> listaDireccionesZonales;
	
	@Getter
	@Setter
	private Integer idAreaEmisora;
	
	@Getter
	@Setter
	private Integer idDireccionZonal;
	
	@Getter
	@Setter
	private boolean esTecnico, esTecnicoCalidad;
	
	@Getter
	@Setter
	private boolean tieneTipoPermiso;
	
	@Getter
	@Setter
	private Integer idTipoPermiso;
	
	@Getter
	@Setter
	private List<String> listaTipoPermisoAmbiental;
	
	@Getter
	@Setter
	private Integer idEnteControl;
	
	@Getter
	@Setter
	private boolean tieneEnteControl;
	
	@Getter
	@Setter
	private CatalogoCIUU ciiuPrincipal= new CatalogoCIUU();
	
	@Getter
	@Setter
	private List<CatalogoCIUU> listaCatalogoCiiu = new ArrayList<CatalogoCIUU>();
	
	@Getter
	@Setter
	private boolean tieneActividad;
	
	@Getter
	@Setter
	private String permisoFisico;
	
	/**
	 * Variables de coordenadas
	 */
	@Getter
	@Setter
	private ShapeDigitalizacion shapeDigitalizacion;
	
	@Getter
    @Setter
    private List<CoordenadasPoligonos> coordinatesWrappers = new ArrayList<CoordenadasPoligonos>();
	
	@Getter
    @Setter
	private List<CoordenadasPoligonos> coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
	
	@Getter
	@Setter
	private List<CoordenadaDigitalizacion> coordenadasGeograficas;
	
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
	
	private HashMap<String, Double> varUbicacionArea= new HashMap<String,Double>();
	
	private HashMap<String, Double> varUbicacionAux = new HashMap<String,Double>();
	
	@Getter
    private UploadedFile uploadedFileGeo;
	
	private TipoForma poligono;
	
	@Setter
	@Getter
	private HashMap<String, UbicacionesGeografica> parroquiaSeleccionadas;
	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionPrincipal = new UbicacionesGeografica();
	
	private  HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>> capasIntersecciones= new HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>>();
	private  HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>> listaCapasInterseccionPrincipal= new HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>>();
	private List<String> listaNombresCapasInterseca = new ArrayList<>();
	
	@Getter
	@Setter
	private boolean estadoZonaIntangible,estadoZonaIntangibleAux;
	
	@Getter
	@Setter
	private boolean estadoFrontera;
	
	@Getter
	@Setter
	private boolean esZonaSnapEspecial = false;
	
	@Getter
	@Setter
	private boolean mostrarDetalleInterseccion = false;
	
	private List<Integer> wolframSocial= new ArrayList<Integer>();
    private List<Integer> wolframBiofisica= new ArrayList<Integer>();
    
    private UbicacionesGeografica ubicacionOficinaTecnica = new UbicacionesGeografica();
    
    private List<CapasCoa> capas = new ArrayList<CapasCoa>();
    
    @Getter
	@Setter
	private String zonasInterccionDetalle="";
       
	/**
	 * fin variables de coordenadas
	 */
	
    @Getter
    @Setter
    private List<String> areasProtegidas;
    
    @Getter
    @Setter
    private List<String> bosquesProtectores;
    
    @Getter
    @Setter
    private List<String> listaAreasProtegidas;
    
    @Getter
    @Setter
    private List<String> listaBosquesProtectores;
    
    @Getter
    @Setter
    private List<UbicacionesGeografica> listaProvincias;
    
    @Getter
    @Setter
    private List<UbicacionesGeografica> listaCantones;
    
    @Getter
    @Setter
    private List<UbicacionesGeografica> listaParroquias;
    
    @Getter
    @Setter
    private Integer idProvincia, idCanton, idParroquia;
    
    @Getter
    @Setter
    private List<UbicacionesGeografica> listaUbicacionesSeleccionadas;
    
    @Getter
    @Setter
    private List<String> areasProtegidasSeleccionadas;
    
    @Getter
    @Setter
    private List<String> bosquesProtectoresSeleccionados;
    
    @Getter
    @Setter
	private Date fechaActual;
    
    /**
     * Documentos
     */
    @Getter
    @Setter
    private Usuario usuarioTecnico;
    
    @Getter
    @Setter
    private DocumentoDigitalizacion certificadoInterseccion;
    
    @Getter
    @Setter
    private DocumentoDigitalizacion documentoMapa;
    
    @Getter
    @Setter
    private DocumentoDigitalizacion documentoResolucion;
    
    @Getter
    @Setter
    private DocumentoDigitalizacion documentoEstudioImpactoAmbiental;
    
    @Getter
    @Setter
    private DocumentoDigitalizacion documentoFichaAmbiental;
    
    @Getter
    @Setter
    private DocumentoDigitalizacion documentosHabilitantes;
    
    @Getter
    @Setter
    private DocumentoDigitalizacion otrosDocumentos;
    
    @Getter
    @Setter
    private List<String> listaSistemaReferencia;
    
    @Getter
    @Setter
    private List<String> listaMensajesErrores;
    
    @Getter
    @Setter
    private boolean nuevoRegistro;
    
    @Getter
    @Setter
    private boolean mostrarDocumentoFicha, mostrarDocumentoEstudio;
    
    /**
     * Ingreso de proyectos asociados
     */
    @Getter
    @Setter
    private AutorizacionAdministrativaAmbiental autorizacionPrincipal;
    
    @Getter
    @Setter
    private List<AutorizacionAdministrativa> listaProyectosSeleccionados;
    
    /**
     * fin asociado
     */
    
    @Getter
    @Setter
    private boolean deshabilitarSiguiente = false;
    
    @Getter
    @Setter
    private boolean tieneSector = false, habilitarEditar;
    
    @Getter
    @Setter
    private String actividadAnterior, mensajeCI="";
    
    @Getter
    @Setter
    private boolean tieneCodigoTramite = false, esCertificadoInterseccion = false;
    
    @Getter
    @Setter
    private boolean existeProyectosEnLista = false;
	private String tramite;
	private Map<String, Object> variables;
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
    
	@PostConstruct
	private void init(){
		try {
			if(bandejaTareasBean.getProcessId() >= 0){
				JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacion.jsf");
				return;
			}
			fechaActual = new Date();
			listaAreasControl = areaFacade.getAreasResponsables();
			llenarListaTipoPermiso();
			listaCatalogoCiiu=catalogoCIUUFacade.listaCatalogoCiuu();
			capas=capasCoaFacade.listaCapas();
			listaAreasProtegidas = autorizacionAdministrativaAmbientalFacade.getAreasProtegidas();
			listaBosquesProtectores = autorizacionAdministrativaAmbientalFacade.getBosquesProtectores();
			listaUbicacionesSeleccionadas = new ArrayList<UbicacionesGeografica>();
			areasProtegidasSeleccionadas = new ArrayList<String>();
			bosquesProtectoresSeleccionados = new ArrayList<String>();
			usuarioTecnico = new Usuario();
			listaSistemaReferencia = new ArrayList<String>();
			listaSistemaReferencia.add("PSAD56");
			listaSistemaReferencia.add("WGS84");
			habilitarEditar=(boolean)(JsfUtil.devolverObjetoSession("editardatos")==null?false:JsfUtil.devolverObjetoSession("editardatos"));
			//cargo el tecnico de calidad que actualiza
			if(habilitarEditar){
				usuarioTecnico = usuarioFacade.buscarUsuario(loginBean.getNombreUsuario());
			}
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			if(tramite == null){
				tramite =(String)(JsfUtil.devolverObjetoSession("tramite"));
			}
			if(tramite == null){
		    	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		    	if (request.getParameter("codigo") != null){
		    		tramite = request.getParameter("codigo").replace("?", "");
		    	}
			}
			if(tramite != null){
				autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(tramite);
				if(autorizacionAdministrativa != null){
					autorizacionAdministrativaAmbientalBean.setEsRegistroNuevo(false);

					AutorizacionAdministrativa objAAA = cargarDatosDigitalizacion(autorizacionAdministrativa);
					switch (autorizacionAdministrativa.getSistema().toString()) {
					case "0": // nuevo
						autorizacionAdministrativaAmbientalBean.setEsRegistroNuevo(true);
						break;
					case "1":	//BDD_FISICO

						break;
					case "2":	//BDD_CUATRO_CATEGORIAS

						break;
					case "3":	//BDD_SECTOR_SUBSECTOR

						break;
					case "4":	//REGULARIZACIÓN
						objAAA.setFuente("2");
						autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(objAAA);
						break;
					case "5":	//RCOA

						break;
					default:
						break;
					}
				}
			}
			actividadAnterior="";
			/**
			 * campos desde asociacion
			 */
			if(autorizacionAdministrativaAmbientalBean.getEsRegistroNuevo() == null){
				if(JsfUtil.devolverObjetoSession("nuevoRegistro") != null)
					nuevoRegistro =(boolean)(JsfUtil.devolverObjetoSession("nuevoRegistro"));
				else
					nuevoRegistro =false;
			}else{
				nuevoRegistro = autorizacionAdministrativaAmbientalBean.getEsRegistroNuevo();
			}
			
			if(autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaPrincipal() != null){
				autorizacionPrincipal = autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaPrincipal();
			}
			
			List<AutorizacionAdministrativa> listaSeleccionadas = autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados();
			existeProyectosEnLista=false;
			if(listaSeleccionadas != null){
				existeProyectosEnLista=true;
				if(!nuevoRegistro){
					for(AutorizacionAdministrativa aaa : listaSeleccionadas){
						autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaLista(aaa);
						autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(aaa.getCodigo());
						mensajeCI="";
						if(!aaa.getEstado().isEmpty() && aaa.getEstado().equals("Actualización de Certificado de Intersección")){
							mensajeCI = "Favor indicar si este proyecto "+aaa.getCodigo()+" corresponde a una"
									+ " Actualización de Certificado de Intersección de la Autorización Administrativa Ambiental ";
						}
						if(aaa.getFuente().equals("0")){
							autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(cargarObjetoFisico(aaa.getId()));
							break;
						}else{
							autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(aaa);
							break;
						}
					}
				}
			}
			
			/**
			 * fin campos desde asociacion
			 */
			EntityLicenciaFisica licenciaFisica = new EntityLicenciaFisica();
			AutorizacionAdministrativa licenciaSuia = new AutorizacionAdministrativa();
			
			if(!nuevoRegistro){
				licenciaFisica = autorizacionAdministrativaAmbientalBean.getAutorizacionFisicaSeleccionada();
				licenciaSuia = autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaSeleccionada();
			}else{
				licenciaFisica = null;
				licenciaSuia = null;
			}
			
			/**
			 * VALIDAR SI ES TECNICO O SUJETO DE CONTROL
			 */
//			esTecnico = true;
			tieneTipoPermiso = false;
			tieneEnteControl = false;
			tieneActividad = false;
			
			if(nuevoRegistro){
				tieneTipoPermiso = false;
				tieneEnteControl = false;
				tieneActividad = false;
			}else{
				
			}
			
			esTecnico = true;
			esTecnicoCalidad = false;
			for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
				if (areaUser.getArea().getAreaName().contains("PROPONENTE")) {
					esTecnico = false;
					break;
				}
			}
			if(esTecnico){
				String rol="role.resolucion.tecnico.responsable";
				String rolTecnicoCalidad = Constantes.getRoleAreaName(rol);
				esTecnicoCalidad= JsfUtil.getLoggedUser().isUserInRole(JsfUtil.getLoggedUser(), rolTecnicoCalidad);
			}
			shapeDigitalizacion = new ShapeDigitalizacion();
			
			listaSectores = new ArrayList<TipoSector>();
			for (TipoSector tipoSector : proyectoLicenciamientoAmbientalFacade.getTiposSectores()) {
				if(tipoSector.getId() < 5){
					listaSectores.add(tipoSector);
				}
			}
			
			if(autorizacionAdministrativa == null){
				autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
				if(licenciaFisica != null){
					cargarEntesResponsable("1");
					cargaInformacionProyectosFisicos(licenciaFisica);
				}
				if(licenciaSuia != null){
					cargarEntesResponsable(equivaleciaFunete(licenciaSuia.getFuente()));
					cargarInformacionSuia(licenciaSuia);
				}
				if(licenciaFisica == null && licenciaSuia == null){
					cargarEntesResponsable("1");
				}
			}else{
				cargarInformacionDigitalizacion();
				cargarEntesResponsable(autorizacionAdministrativa.getSistema().toString());
				actividadAnterior = autorizacionAdministrativa.getActividadCatalogo();
			}

			cargarFases(true);
			if(!esTecnico){
				autorizacionAdministrativa.setIdentificacionUsuario(loginBean.getUsuario().getNombre());
				buscarUsuario();
			}else{
				listaDireccionesZonales = areaFacade.getAreasDireccionaZonales();
			}
			if(nuevoRegistro){
				autorizacionAdministrativa.setSistema(0);
			}
			
			JsfUtil.cargarObjetoSession("nuevoRegistro", null);
			JsfUtil.cargarObjetoSession("licenciaSuia", null);
			JsfUtil.cargarObjetoSession("licenciaFisica", null);

			if(!mensajeCI.isEmpty()){
				RequestContext context = RequestContext.getCurrentInstance();
	        	context.execute("PF('certificadoInterseccion').show();");
			}else if(autorizacionAdministrativa.isFinalizado() && existeProyectosEnLista && autorizacionPrincipal!= null){
				//JsfUtil.addMessageInfo("El proyecto "+autorizacionAdministrativa.getCodigoProyecto()+" ya se encuentra asociado");
				siguiente();
			}

		} catch (Exception e) {
			LOG.error("Error al iniciar el ingreso de informacion", e);
			e.printStackTrace();
		}
	}

	public void esCI(){
		esCertificadoInterseccion=true;
		if(autorizacionAdministrativa.isFinalizado() && existeProyectosEnLista && autorizacionPrincipal!= null){
			JsfUtil.addMessageInfo("El proyecto "+autorizacionAdministrativa.getCodigoProyecto()+" ya se encuentra asociado");
			siguiente();
		}
	}

	public void noEsCI(){
		esCertificadoInterseccion=false;
		if(!nuevoRegistro)
			autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados().remove(autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaLista());
		if(autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados() == null || autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados().isEmpty()){
			RequestContext context = RequestContext.getCurrentInstance();
        	context.execute("PF('mdlFinalizacion').show();");
		}else{
			autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(null);
			autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(null);
			autorizacionAdministrativaAmbientalBean.setEsRegistroNuevo(false);
			JsfUtil.cargarObjetoSession("tramite", null);
			bandejaTareasBean.setProcessId(0);
	        continuarSiguiente();
		}
	}
	
	private String equivaleciaFunete(String fuente){
		String fuenteAux="";
		switch (fuente) {
		case "2":
			fuenteAux="4";
			break;
		case "1":
			fuenteAux="2";
			break;
		case "5":
			fuenteAux="3";
			break;

		default:
			break;
		}
		return fuenteAux;
	}
	
	private AutorizacionAdministrativa cargarDatosDigitalizacion(AutorizacionAdministrativaAmbiental autorizacion){
		AutorizacionAdministrativa aux = new AutorizacionAdministrativa();
		aux.setId(autorizacion.getIdProyecto());
		aux.setCodigo(autorizacion.getCodigoProyecto());
		aux.setNombre(autorizacion.getNombreProyecto());
		aux.setCedulaProponente(autorizacion.getIdentificacionUsuario());
		aux.setNombreProponente(autorizacion.getNombreUsuario());
		if(autorizacion.getTipoSector() != null)
			aux.setSector(autorizacion.getTipoSector().getNombre());
		aux.setFuente(autorizacion.getSistema().toString());
		aux.setCategoria(autorizacion.getAutorizacionAdministrativaAmbiental());
		aux.setEstado("Completado");
		//aux.setNroTareas();
		aux.setFecha(autorizacion.getFechaResolucion().toString());
		//aux.setFechaFin();
		aux.setResumen(autorizacion.getResumenProyecto());
		//aux.setActividad();
		aux.setDigitalizado(autorizacion.isFinalizado());
		aux.setIdDigitalizacion(autorizacion.getId());
		aux.setIdProceso(autorizacion.getIdProceso());
		return aux;
	}
	
	private EntityLicenciaFisica cargarObjetoFisico(Integer id){
		return licenciaAmbientalFisicaFacade.buscarProyectoFisicoPorId(id);
	}
	
	private void cargaInformacionProyectosFisicos(EntityLicenciaFisica licenciaFisica){
		try {
			areasProtegidas = new ArrayList<String>();
			bosquesProtectores = new ArrayList<String>();
			autorizacionAdministrativa.setNombreUsuario(licenciaFisica.getNombreOperador());
			if(licenciaFisica.getNumeroResolucion() != null && !licenciaFisica.getNumeroResolucion().isEmpty())
				autorizacionAdministrativa.setResolucion(licenciaFisica.getNumeroResolucion());
			autorizacionAdministrativa.setNombreProyecto(licenciaFisica.getNombreProyecto());
			autorizacionAdministrativa.setSistema(1);
			autorizacionAdministrativa.setIdProceso(licenciaFisica.getIdProceso());
			autorizacionAdministrativa.setIdProyecto(licenciaFisica.getId());
			if(licenciaFisica.getFechaEmision() != null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date fechaEmision = sdf.parse(licenciaFisica.getFechaEmision());
				autorizacionAdministrativa.setFechaResolucion(fechaEmision);
			}
			if(licenciaFisica.getIntersectaDescripcion() != null && !licenciaFisica.getIntersectaDescripcion().equals("")){	
				if(licenciaFisica.getIntersectaDescripcion().toUpperCase().contains("BOSQUES")){
					bosquesProtectores.add(licenciaFisica.getIntersectaDescripcion());
				}else{
					areasProtegidas.add(licenciaFisica.getIntersectaDescripcion());
				}
			}
			
			List<CoordenadaDigitalizacion> listaCoordenadasFisicas = new ArrayList<CoordenadaDigitalizacion>();
			listaCoordenadasFisicas = licenciaAmbientalFisicaFacade.busquedaCoordenadasPorCodigo(licenciaFisica.getId());
			
			if(listaCoordenadasFisicas != null && !listaCoordenadasFisicas.isEmpty()){
				int i = 1;
				for(CoordenadaDigitalizacion cd : listaCoordenadasFisicas){
					cd.setOrden(i);
					i++;
				}
				if(licenciaFisica.getShape() != null){
					TipoForma tipo = tipoFormaFacade.buscarPorNombre(licenciaFisica.getShape());
					CoordenadasPoligonos coorP = new CoordenadasPoligonos();
					coorP.setCoordenadas(listaCoordenadasFisicas);
					coorP.setTipoForma(tipo);
					coordinatesWrappersGeo.add(coorP);
				}
			}
			
			//sector
			if(licenciaFisica.getSector() != null && !licenciaFisica.getSector().equals("")){
				TipoSector tipoSector = new TipoSector();
				
				if(licenciaFisica.getSector().equals("ELÉCTRICO")){
					tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(4);
					autorizacionAdministrativa.setTipoSector(tipoSector);
					idSector = tipoSector.getId();
				}else if(licenciaFisica.getSector().equals("HIDROCARBURÍFERO")){
					tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(1);
					autorizacionAdministrativa.setTipoSector(tipoSector);
				}else if(licenciaFisica.getSector().equals("MINERO")){
					tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(2);
					autorizacionAdministrativa.setTipoSector(tipoSector);
				}else{
					tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(3);
					autorizacionAdministrativa.setTipoSector(tipoSector);
				}
				idSector = tipoSector.getId();
				cargarFases(true);
			}
			
			//Actividad Económica
			//Se debe validar con lo que envíe el área requirente para la comparacion
			if(licenciaFisica.getActividad() != null){
				actividadAnterior = licenciaFisica.getActividad();
				//guardar la correcta cuando den validaciones
				autorizacionAdministrativa.setActividadCatalogo(licenciaFisica.getActividad());
				//tieneActividad = true;
			}
		} catch (Exception e) {
			LOG.error("Error al obtener información del proyecto físico", e);
		}
	}
	
	private void cargarInformacionSuia(AutorizacionAdministrativa autorizacionSistema){
		try {
			if(autorizacionAdministrativa == null){
				autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
			}
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date fechaRegistro = null, fechaFinalizacion = null, fechaResolucion = null;
			if(autorizacionSistema.getFecha() != null && !autorizacionSistema.getFecha().isEmpty() && autorizacionSistema.getFecha().length()>=10){
				try{
					fechaRegistro = formato.parse(autorizacionSistema.getFecha());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(autorizacionSistema.getFechaFin() != null && !autorizacionSistema.getFechaFin().isEmpty() && autorizacionSistema.getFechaFin().length()>=10){
				try{
					fechaFinalizacion = formato.parse(autorizacionSistema.getFechaFin());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			autorizacionAdministrativa.setResolucion(autorizacionSistema.getResolucion());
			if(autorizacionSistema.getFechaResolucion() != null && !autorizacionSistema.getFechaResolucion().isEmpty() && autorizacionSistema.getFechaResolucion().length()>=10){
				try{
					fechaResolucion = formato.parse(autorizacionSistema.getFechaResolucion());
					autorizacionAdministrativa.setFechaResolucion(fechaResolucion);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(autorizacionSistema.getFuente().equals("2")){
				//regularizacion suia_iii
				
				ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(autorizacionSistema.getId());
				
				autorizacionAdministrativa.setSistema(4);
				autorizacionAdministrativa.setNombreProyecto(proyecto.getNombre());
				autorizacionAdministrativa.setResumenProyecto(proyecto.getResumen());
				autorizacionAdministrativa.setUsuario(proyecto.getUsuario());
				autorizacionAdministrativa.setNombreUsuario(proyecto.getUsuario().getPersona().getNombre());
				autorizacionAdministrativa.setIdentificacionUsuario(proyecto.getUsuario().getNombre());
				autorizacionAdministrativa.setCodigoProyecto(proyecto.getCodigo());
				autorizacionAdministrativa.setFechaRegistro(proyecto.getFechaCreacion());
				
				TipoSector tipoSector = new TipoSector();
				
				if(proyecto.getTipoSector().getNombre().equals("Hidrocarburos")){
					tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(1);
					autorizacionAdministrativa.setTipoSector(tipoSector);
				}else if(proyecto.getTipoSector().getNombre().equals("Minería")){
					tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(2);
					autorizacionAdministrativa.setTipoSector(tipoSector);
				}else if(proyecto.getTipoSector().getNombre().equals("Eléctrico")){
					tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(4);
					autorizacionAdministrativa.setTipoSector(tipoSector);
				}else{
					tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(3);
					autorizacionAdministrativa.setTipoSector(tipoSector);
				}
				
//				autorizacionAdministrativa.setTipoSector(proyecto.getTipoSector());
				idSector = autorizacionAdministrativa.getTipoSector().getId();
				
				if(listaAreasEmisoras.contains(proyecto.getAreaResponsable())){
					autorizacionAdministrativa.setAreaEmisora(proyecto.getAreaResponsable());
					idAreaEmisora = proyecto.getAreaResponsable().getId();
				}
				if(listaAreasControl.contains(proyecto.getAreaResponsable())){
					autorizacionAdministrativa.setAreaResponsableControl(proyecto.getAreaResponsable());
					idEnteControl = proyecto.getAreaResponsable().getId();
				}
				autorizacionAdministrativa.setTipoPermisoAmbiental(autorizacionSistema.getCategoria());
				autorizacionAdministrativa.setId(autorizacionSistema.getIdDigitalizacion());
				autorizacionAdministrativa.setIdProceso(autorizacionSistema.getIdProceso());
				autorizacionAdministrativa.setIdProyecto(autorizacionSistema.getId());
				
				autorizacionAdministrativa.setAutorizacionAdministrativaAmbiental(autorizacionSistema.getCategoria());
				tieneTipoPermiso = true;
				
				cambioAutorizacionAdministrativaAmbiental();
				
				// falata ver la equivalencia entre catalogos
				if(proyecto.getCatalogoCategoria()!= null){
					actividadAnterior = proyecto.getCatalogoCategoria().getCatalogoCategoriaPublico().getNombre();
					autorizacionAdministrativa.setActividadCatalogo(proyecto.getCatalogoCategoria().getCatalogoCategoriaPublico().getNombre());
					tieneActividad = false;
				}
				
				List<FormaProyecto> listaCoor = proyecto.getFormasProyectos();
				List<CoordenadaDigitalizacion> listaCoordenadasTrans = new ArrayList<CoordenadaDigitalizacion>();
				for(FormaProyecto forma : listaCoor){
					String tipoCoordenada = null;
					listaCoordenadasTrans = new ArrayList<CoordenadaDigitalizacion>();
					for(Coordenada coor : forma.getCoordenadas()){
						CoordenadaDigitalizacion coorDig = new CoordenadaDigitalizacion();
						coorDig.setX(BigDecimal.valueOf(coor.getX()));
						coorDig.setY(BigDecimal.valueOf(coor.getY()));
						coorDig.setOrden(coor.getOrden());
						tipoCoordenada = coor.getZona();
						listaCoordenadasTrans.add(coorDig);
					}
					
					CoordenadasPoligonos coorP = new CoordenadasPoligonos();
					coorP.setCoordenadas(listaCoordenadasTrans);
					coorP.setTipoForma(forma.getTipoForma());
					coordinatesWrappersGeo.add(coorP);
					shapeDigitalizacion.setTipoForma(forma.getTipoForma());
					shapeDigitalizacion.setSistemaReferencia(proyecto.getFormato());
					shapeDigitalizacion.setZona(tipoCoordenada);
					shapeDigitalizacion.setTipoIngreso(1);
				}
				ubicacionesSeleccionadas = proyecto.getUbicacionesGeograficas();
				List<InterseccionProyecto> listaIntersecciones = autorizacionAdministrativaAmbientalFacade.listaIntersecciones(proyecto);
				//Descarga de documentos
				List<Documento> documentosCerticado = documentosFacade.documentosTodosXTablaIdXIdDoc(proyecto.getId(), "ProyectoLicenciamientoAmbiental", TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_OFICIO);
				if(documentosCerticado != null && !documentosCerticado.isEmpty()){
					certificadoInterseccion = new DocumentoDigitalizacion();
					byte[] contenido = documentosFacade.descargar(documentosCerticado.get(0).getIdAlfresco(), documentosCerticado.get(0).getFechaCreacion());
					if(contenido != null){
						certificadoInterseccion = crearDocumentoSuia(contenido, documentosCerticado.get(0));
					}
				}
				List<Documento> documentosMapa = documentosFacade.documentosTodosXTablaIdXIdDoc(proyecto.getId(), "ProyectoLicenciamientoAmbiental", TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA);
				if(documentosMapa != null && !documentosMapa.isEmpty()){
					byte[] contenido = documentosFacade.descargar(documentosMapa.get(0).getIdAlfresco(), documentosMapa.get(0).getFechaCreacion());
					if(contenido != null){
						documentoMapa = crearDocumentoSuia(contenido, documentosMapa.get(0));
					}
				}
				
				//Documento resolucion
				if(autorizacionSistema.getCategoria().equals("Licencia Ambiental")){
					List<Documento> documentosResolucion = documentosFacade.documentosTodosXTablaIdXIdDoc(proyecto.getId(), "InformeTecnicoGeneralLA", TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA);
					if(documentosResolucion != null && !documentosResolucion.isEmpty()){
						byte[] contenido = documentosFacade.descargar(documentosResolucion.get(0).getIdAlfresco(), documentosResolucion.get(0).getFechaCreacion());
						if(contenido != null){
							documentoResolucion = crearDocumentoSuia(contenido, documentosResolucion.get(0));
						}
					}
				}else if(autorizacionSistema.getCategoria().equals("Registro Ambiental")){
					List<Documento> documentosResolucion = documentosFacade.documentosTodosXTablaIdXIdDoc(proyecto.getId(), "CategoriaIILicencia", TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL);
					if(documentosResolucion != null && !documentosResolucion.isEmpty()){
						byte[] contenido = documentosFacade.descargar(documentosResolucion.get(0).getIdAlfresco(), documentosResolucion.get(0).getFechaCreacion());
						
						if(contenido != null){
							documentoResolucion = new DocumentoDigitalizacion();
							documentoResolucion.setContenidoDocumento(contenido);
							documentoResolucion.setNombre(documentosResolucion.get(0).getNombre());
							documentoResolucion.setExtension(documentosResolucion.get(0).getExtesion());
							documentoResolucion.setMime(documentosResolucion.get(0).getMime());
						}
					}
				}else if(autorizacionSistema.getCategoria().equals("Certificado Ambiental")){
					List<Documento> documentosResolucion = documentosFacade.documentosTodosXTablaIdXIdDoc(proyecto.getId(), "ProyectoLicenciamientoAmbiental", TipoDocumentoSistema.TIPO_CERTIFICADO_CATEGORIA_UNO);
					if(documentosResolucion != null && !documentosResolucion.isEmpty()){
						byte[] contenido = documentosFacade.descargar(documentosResolucion.get(0).getIdAlfresco(), documentosResolucion.get(0).getFechaCreacion());
						if(contenido != null){
							documentoResolucion = new DocumentoDigitalizacion();
							documentoResolucion.setContenidoDocumento(contenido);
							documentoResolucion.setNombre(documentosResolucion.get(0).getNombre());
							documentoResolucion.setExtension(documentosResolucion.get(0).getExtesion());
							documentoResolucion.setMime(documentosResolucion.get(0).getMime());
						}
					}
				}
				
				// estudio o ficha falta la consultar de estos documentos
				if(mostrarDocumentoFicha){
				}
				if(mostrarDocumentoEstudio){
					List<String[]> lista = proyectoLicenciamientoAmbientalFacade.obtenerProcesoIdPorTramite(proyecto.getCodigo());
					for (String[] codigoProyecto : lista) {
						String idProceso = codigoProyecto[1];
						List<Documento> documentosEstudio = documentosFacade.recuperarDocumentosConArchivosPorFlujoTodasVersionesTipo(Long.valueOf(idProceso), TipoDocumentoSistema.DOCUMENTO_DEL_PROPONENTE_EIA.getIdTipoDocumento());
						if(documentosEstudio != null && !documentosEstudio.isEmpty()){
							byte[] contenido = documentosFacade.descargar(documentosEstudio.get(0).getIdAlfresco(), documentosEstudio.get(0).getFechaCreacion());
							if(contenido != null){
								documentoEstudioImpactoAmbiental = new DocumentoDigitalizacion();
								documentoEstudioImpactoAmbiental.setContenidoDocumento(contenido);
								documentoEstudioImpactoAmbiental.setNombre(documentosEstudio.get(0).getNombre());
								documentoEstudioImpactoAmbiental.setExtension(documentosEstudio.get(0).getExtesion());
								documentoEstudioImpactoAmbiental.setMime(documentosEstudio.get(0).getMime());
							}
						}
					}
				}
			}else if(autorizacionSistema.getFuente().equals("1")){ //4 categorías
				tieneActividad=false;
				autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
				
				autorizacionAdministrativa.setSistema(2);
				autorizacionAdministrativa.setCodigoProyecto(autorizacionSistema.getCodigo());
				autorizacionAdministrativa.setNombreProyecto(autorizacionSistema.getNombre());
				
				autorizacionAdministrativa.setNombreUsuario(autorizacionSistema.getNombreProponente());
				autorizacionAdministrativa.setIdentificacionUsuario(autorizacionSistema.getCedulaProponente());
				
				if(autorizacionSistema.getCategoria() != null){
					tieneTipoPermiso = true;
					if(autorizacionSistema.getCategoria().equals("I")){
						autorizacionAdministrativa.setTipoPermisoAmbiental("Categoría I");
					}else if(autorizacionSistema.getCategoria().equals("II")){
						autorizacionAdministrativa.setTipoPermisoAmbiental("Categoría II");
					}else if(autorizacionSistema.getCategoria().equals("III")){
						autorizacionAdministrativa.setTipoPermisoAmbiental("Categoría III");
					}else if(autorizacionSistema.getCategoria().equals("IV")){
						autorizacionAdministrativa.setTipoPermisoAmbiental("Categoría IV");
					}
					cambioAutorizacionAdministrativaAmbiental();
				}
				
				autorizacionAdministrativa.setResumenProyecto(autorizacionSistema.getResumen());
				autorizacionAdministrativa.setActividadCatalogo(autorizacionSistema.getActividad());
				
				TipoSector tipoSector = new TipoSector();
				if(autorizacionSistema.getSector().equals("Licenciamiento")){
					tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(3);
				}else if(autorizacionSistema.getSector().equals("Hidrocarburos")){
					tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(1);
				}
				
				idSector = tipoSector.getId();
				autorizacionAdministrativa.setTipoSector(tipoSector);
				
				listaFases = catalogoFasesCoaFacade.obtenerFasesPorSector(idSector);
				
				List<ShapeDigitalizacion> listaShape = licenciaAmbientalFisicaFacade.busquedaShape4CategoriasPorCodigo(autorizacionSistema.getCodigo());

				if(listaShape != null && !listaShape.isEmpty()){
					for (ShapeDigitalizacion objShapeDigitalizacion : listaShape) {
						TipoForma tipo = tipoFormaFacade.buscarPorId(objShapeDigitalizacion.getIdTipoForma());
						
						CoordenadasPoligonos coorP = new CoordenadasPoligonos();
						List<CoordenadaDigitalizacion> listaCoordenadas = new ArrayList<CoordenadaDigitalizacion>();
						listaCoordenadas = licenciaAmbientalFisicaFacade.busquedaCoordenadas4CategoriasPorCodigo(autorizacionSistema.getCodigo(), objShapeDigitalizacion.getId());
						if(listaCoordenadas != null && !listaCoordenadas.isEmpty()){
							int i = 1;
							for(CoordenadaDigitalizacion cd : listaCoordenadas){
								cd.setOrden(i);
								i++;
							}
							/*String coordenadasAux="";
							for (CoordenadaDigitalizacion coordenada : listaCoordenadas) {
								coordenadasAux += (coordenadasAux == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
							}
							verificarCapasInterseccion(coordenadasAux);*/
						}
						coorP.setCoordenadas(listaCoordenadas);
						coorP.setTipoForma(tipo);
						coordinatesWrappersGeo.add(coorP);
						shapeDigitalizacion.setTipoForma(tipo);
						shapeDigitalizacion.setSistemaReferencia("WGS84");
						shapeDigitalizacion.setZona("17S");
						shapeDigitalizacion.setTipoIngreso(1);
					}
				}
				
				List<String> lista = licenciaAmbientalFisicaFacade.busquedaUbicacion4Cat(autorizacionSistema.getCodigo(), autorizacionAdministrativa.getSistema().toString());
				for(String inec : lista){
					UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarUbicacionPorCodigoInec(inec);
					if(ubicacion != null && ubicacion.getId() != null){
						ubicacionesSeleccionadas.add(ubicacion);
					}
				}
				// docu,emtos
				obtenerDocumento4Cat(autorizacionSistema.getCodigo(), "MapaCIFinal");
				if(documentoMapa == null)
					obtenerDocumento4Cat(autorizacionSistema.getCodigo(), "MapaCIProvisional");
				obtenerDocumento4Cat(autorizacionSistema.getCodigo(), "pciCertificadoInterseccion");
				if(certificadoInterseccion == null)
					obtenerDocumento4Cat(autorizacionSistema.getCodigo(), "CertificadoInterseccionAutomatico");
				obtenerDocumento4Cat(autorizacionSistema.getCodigo(), "catIIFichaAmbiental");
				obtenerDocumento4Cat(autorizacionSistema.getCodigo(), "MapaCIFinal");
				obtenerDocumento4Cat(autorizacionSistema.getCodigo(), "MapaCIFinal");
			}//fin 4 categorias
			else if(autorizacionSistema.getFuente().equals("5")){//sector subsector
				tieneActividad=false;
				autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
				
				autorizacionAdministrativa.setSistema(3);
				autorizacionAdministrativa.setCodigoProyecto(autorizacionSistema.getCodigo());
				autorizacionAdministrativa.setNombreProyecto(autorizacionSistema.getNombre());
				
				autorizacionAdministrativa.setNombreUsuario(autorizacionSistema.getNombreProponente());
				autorizacionAdministrativa.setIdentificacionUsuario(autorizacionSistema.getCedulaProponente());
				
				// busqueda con script paravariable
				
				if(autorizacionSistema.getCategoria() != null){
					tieneTipoPermiso = true;
					if(autorizacionSistema.getCategoria().equals("I")){
						autorizacionAdministrativa.setTipoPermisoAmbiental("Categoría I");
					}else if(autorizacionSistema.getCategoria().equals("II")){
						autorizacionAdministrativa.setTipoPermisoAmbiental("Categoría II");
					}else if(autorizacionSistema.getCategoria().equals("III")){
						autorizacionAdministrativa.setTipoPermisoAmbiental("Categoría III");
					}else if(autorizacionSistema.getCategoria().equals("IV")){
						autorizacionAdministrativa.setTipoPermisoAmbiental("Categoría IV");
					}
					cambioAutorizacionAdministrativaAmbiental();
				}
				
				autorizacionAdministrativa.setResumenProyecto(autorizacionSistema.getResumen());
				autorizacionAdministrativa.setActividadCatalogo(autorizacionSistema.getActividad());
				
				if(autorizacionSistema.getSector() != null && !autorizacionSistema.getSector().equals("")){
					TipoSector tipoSector = new TipoSector();
					
					if(autorizacionSistema.getSector().equals("ELÉCTRICO")){
						tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(4);
						autorizacionAdministrativa.setTipoSector(tipoSector);
						idSector = tipoSector.getId();
					}else if(autorizacionSistema.getSector().equals("HIDROCARBURÍFERO")){
						tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(1);
						autorizacionAdministrativa.setTipoSector(tipoSector);
					}else if(autorizacionSistema.getSector().equals("MINERO")){
						tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(2);
						autorizacionAdministrativa.setTipoSector(tipoSector);
					}else{
						tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(3);
						autorizacionAdministrativa.setTipoSector(tipoSector);
					}
					idSector = tipoSector.getId();
				}
				String categorizacion = autorizacionesAdministrativasFacade.getVariableSectorSubsector(autorizacionSistema.getCodigo(), "Categorización");
				autorizacionAdministrativa.setTipoPermisoAmbiental(categorizacion);
				if(autorizacionAdministrativa.getTipoPermisoAmbiental() != null){
					tieneTipoPermiso = true;
					cambioAutorizacionAdministrativaAmbiental();
				}
				
				List<ShapeDigitalizacion> listaShape = licenciaAmbientalFisicaFacade.busquedaShapeSectorPorCodigo(autorizacionSistema.getCodigo());

				if(listaShape != null && !listaShape.isEmpty()){
					for (ShapeDigitalizacion objShapeDigitalizacion : listaShape) {
						TipoForma tipo = tipoFormaFacade.buscarPorId(objShapeDigitalizacion.getIdTipoForma());
						CoordenadasPoligonos coorP = new CoordenadasPoligonos();

						List<CoordenadaDigitalizacion> listaCoordenadas = new ArrayList<CoordenadaDigitalizacion>();
						listaCoordenadas = licenciaAmbientalFisicaFacade.busquedaCoordenadasSectorPorCodigo(autorizacionSistema.getCodigo(), objShapeDigitalizacion.getId());
						if(listaCoordenadas != null && !listaCoordenadas.isEmpty()){
							int i = 1;
							for(CoordenadaDigitalizacion cd : listaCoordenadas){
								cd.setOrden(i);
								i++;
							}
						}
						coorP.setCoordenadas(listaCoordenadas);
						coorP.setTipoForma(tipo);
						coordinatesWrappersGeo.add(coorP);
						shapeDigitalizacion.setTipoForma(tipo);
						shapeDigitalizacion.setSistemaReferencia("WGS84");
						shapeDigitalizacion.setZona("17S");
						shapeDigitalizacion.setTipoIngreso(1);
					}
				}
				
				List<String> lista = licenciaAmbientalFisicaFacade.busquedaUbicacion4Cat(autorizacionSistema.getCodigo(), autorizacionAdministrativa.getSistema().toString());
				for(String inec : lista){
					UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarUbicacionPorCodigoInec(inec);
					if(ubicacion != null && ubicacion.getId() != null){
						ubicacionesSeleccionadas.add(ubicacion);
					}
				}
			}//fin sector subsector
			if(autorizacionAdministrativa.getCodigoProyecto() != null){
				tieneCodigoTramite = true;
			}
			autorizacionAdministrativa.setFechaRegistro(fechaRegistro);
			autorizacionAdministrativa.setFechaFinalizacionRegistro(fechaFinalizacion);
		} catch (Exception e) {
			LOG.error("Error al cargar la informacion de Regularización", e);
		}
	}
	
	/*
	private void obtenerResolucion(String tipoPermiso, Integer idProyecto){
		if(tipoPermiso.equals("Licencia Ambiental")){
			InformeTecnicoGeneralLA informeTecnicoGeneralLA = informeOficioFacade
					.obtenerInformeTecnicoLAGeneralPorProyectoId(TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA
									.getIdTipoDocumento(), idProyecto);
			if (informeTecnicoGeneralLA != null	&& informeTecnicoGeneralLA.getNumeroOficio() != null) {
				autorizacionAdministrativa.setResolucion(informeTecnicoGeneralLA.getNumeroResolucion());
				if(informeTecnicoGeneralLA.getFechaModificacion() != null){
					autorizacionAdministrativa.setFechaResolucion(informeTecnicoGeneralLA.getFechaModificacion());
				}else{
					autorizacionAdministrativa.setFechaResolucion(informeTecnicoGeneralLA.getFechaCreacion());
				}
			}
		}else if(tipoPermiso.equals("Certificado Ambiental")){
			List<CertificadoRegistroAmbiental> listaCertificados = categoria1Facade.getCertificadoRegistroAmbientalPorIdProyecto(idProyecto);
			if(listaCertificados != null && !listaCertificados.isEmpty()){
				if(listaCertificados.get(0).getDocumento().getCodigoPublico() != null){
					String numeroRes = listaCertificados.get(0).getDocumento().getCodigoPublico();
					autorizacionAdministrativa.setResolucion(numeroRes);
				}
				if(listaCertificados.get(0).getDocumento().getFechaModificacion() != null){
					autorizacionAdministrativa.setFechaResolucion(listaCertificados.get(0).getDocumento().getFechaModificacion());
				}else{
					autorizacionAdministrativa.setFechaResolucion(listaCertificados.get(0).getDocumento().getFechaCreacion());
				}
			}
		}else if(tipoPermiso.equals("Registro Ambiental")){
			FichaAmbientalPma fichaRegistro = fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyecto(idProyecto);
			if(fichaRegistro.getNumeroOficio() != null){
				autorizacionAdministrativa.setResolucion(fichaRegistro.getNumeroOficio());
			}else{
				autorizacionAdministrativa.setResolucion(fichaRegistro.getNumeroLicencia());
			}
			if(fichaRegistro.getFechaModificacion() != null){
				autorizacionAdministrativa.setFechaResolucion(fichaRegistro.getFechaModificacion());
			}else{
				autorizacionAdministrativa.setFechaResolucion(fichaRegistro.getFechaCreacion());
			}
		}
	}*/
	
	private byte[] obtenerDocumento4Cat(String codigoProyecto, String tipoDocumento) throws ParseException{
		byte[] byteDocumento = null;
		try {
			List<String> listaDocumentos = licenciaAmbientalFisicaFacade.busquedaDocumentoTipo(codigoProyecto, tipoDocumento);
			if(listaDocumentos != null && listaDocumentos.size() > 0){
				String[] datosDocumentos = listaDocumentos.get(0).split("##");
				if(datosDocumentos[1] != null && !datosDocumentos[1].isEmpty()){
					byteDocumento = documentoDigitalizacionFacade.descargar(datosDocumentos[1]);
				}else{
					String workSpaces = documentoDigitalizacionFacade.getUrl(datosDocumentos[0], datosDocumentos[5]);
					byteDocumento = documentoDigitalizacionFacade.descargar(workSpaces);
				}
				if(byteDocumento != null){
					switch (tipoDocumento) {
					case "MapaCIFinal":
						documentoMapa = crearDocumento4Cat(byteDocumento, datosDocumentos);
						break;
					case "pciCertificadoInterseccion":
						certificadoInterseccion = crearDocumento4Cat(byteDocumento, datosDocumentos);
						break;
					case "documentoResolucion":
						documentoResolucion = crearDocumento4Cat(byteDocumento, datosDocumentos);
						break;
					case "estudioImpactoAmbientalAprobado":
						documentoEstudioImpactoAmbiental = crearDocumento4Cat(byteDocumento, datosDocumentos);
						break;
					case "catIIFichaAmbiental":
						documentoFichaAmbiental = crearDocumento4Cat(byteDocumento, datosDocumentos);
						break;
					default:
						break;
					}
				}
			}
		} catch (CmisAlfrescoException e) {
			return null;
		}
		return byteDocumento;
	}
	
	private DocumentoDigitalizacion crearDocumento4Cat(byte[] byteDocumento, String[] datosDocumentos){
		DocumentoDigitalizacion documentoAux = new DocumentoDigitalizacion();
		documentoAux.setContenidoDocumento(byteDocumento);
		documentoAux.setNombre(datosDocumentos[2]+"."+datosDocumentos[3]);
		documentoAux.setExtension((datosDocumentos[3].startsWith(".")?datosDocumentos[3]:"."+datosDocumentos[3]));
		documentoAux.setMime(datosDocumentos[4]);
		documentoAux.setTipoIngreso(1);
		return documentoAux;
	}
	
	private DocumentoDigitalizacion crearDocumentoSuia(byte[] byteDocumento, Documento documentoSuia){
		DocumentoDigitalizacion documentoAux = new DocumentoDigitalizacion();
		documentoAux.setContenidoDocumento(byteDocumento);
		documentoAux.setNombre(documentoSuia.getNombre());
		documentoAux.setExtension(documentoSuia.getExtesion());
		documentoAux.setMime(documentoSuia.getMime());
		documentoAux.setTipoIngreso(1);
		return documentoAux;
	}
	
	private void cargarInformacionDigitalizacion() throws Exception{
		if(autorizacionAdministrativa.getTipoPermisoAmbiental() != null)
			tieneTipoPermiso = true;
		if(autorizacionAdministrativa.getTipoSector() != null)
			idSector = autorizacionAdministrativa.getTipoSector().getId();
		if(autorizacionAdministrativa.getCatalogoFasesCoa() != null)
			idFase = autorizacionAdministrativa.getCatalogoFasesCoa().getId();
		cargarFases(true);
		if(autorizacionAdministrativa.getAreaEmisora() != null)
			idAreaEmisora = autorizacionAdministrativa.getAreaEmisora().getId();
		if(autorizacionAdministrativa.getAreaResponsableControl() != null)
			idEnteControl = autorizacionAdministrativa.getAreaResponsableControl().getId();
		if(autorizacionAdministrativa.getAreaDZGTecnico() != null)
			idDireccionZonal = autorizacionAdministrativa.getAreaDZGTecnico().getId();
		cambioAutorizacionAdministrativaAmbiental();
		if(autorizacionAdministrativa.getCatalogoCIUU() != null)
			tieneActividad = true;
		//cargo las ubicaciones 
		List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 1);
		ubicacionesSeleccionadas = new ArrayList<UbicacionesGeografica>();
		if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
			for (UbicacionDigitalizacion ubicacionDigitalizacion : ListaUbicacionTipo) {
				ubicacionesSeleccionadas.add(ubicacionDigitalizacion.getUbicacionesGeografica());
			}
		}
		//cargo las ubicaciones ingresadas manualmente
		ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 2);
		listaUbicacionesSeleccionadas = new ArrayList<UbicacionesGeografica>();
		if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
			for (UbicacionDigitalizacion ubicacionDigitalizacion : ListaUbicacionTipo) {
				listaUbicacionesSeleccionadas.add(ubicacionDigitalizacion.getUbicacionesGeografica());
			}
		}
		
		cargarCoordenadasDigitalizacion();
		cargarDocumentosDigitalizacion();
		if(autorizacionAdministrativa.getCodigoProyecto() != null){
			tieneCodigoTramite = true;
		}
		// cargar intersecciones areas Protegidas
		areasProtegidasSeleccionadas = new ArrayList<String>();
		areasProtegidasSeleccionadas.addAll(cargarListaAreaBosques(1));
		bosquesProtectoresSeleccionados = new ArrayList<String>();
		bosquesProtectoresSeleccionados.addAll(cargarListaAreaBosques(2));
	}
	
	private void cargarDocumentosDigitalizacion(){
		//Descarga de documentos
		try {
			List<DocumentoDigitalizacion> documentosCerticado = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_CERTIFICADO_INTERSECCION);
			if(documentosCerticado != null && !documentosCerticado.isEmpty()){
				certificadoInterseccion = documentosCerticado.get(0);
				byte[] contenido;
					contenido = documentoDigitalizacionFacade.descargar(documentosCerticado.get(0).getIdAlfresco());
				if(contenido != null){
					certificadoInterseccion.setContenidoDocumento(contenido);
				}
			}
			List<DocumentoDigitalizacion> documentosMapa = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_MAPA);
			if(documentosMapa != null && !documentosMapa.isEmpty()){
				byte[] contenido = documentoDigitalizacionFacade.descargar(documentosMapa.get(0).getIdAlfresco());
				if(contenido != null){
					documentoMapa = documentosMapa.get(0);
					documentoMapa.setContenidoDocumento(documentosMapa.get(0).getContenidoDocumento());
				}
			}
			//Documento resolucion
			List<DocumentoDigitalizacion> documentosResolucion = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_RESOLUCION);
			if(documentosResolucion != null && !documentosResolucion.isEmpty()){
				byte[] contenido = documentoDigitalizacionFacade.descargar(documentosResolucion.get(0).getIdAlfresco());
				if(contenido != null){
					documentoResolucion = documentosResolucion.get(0);
					documentoResolucion.setContenidoDocumento(contenido);
				}
			}
			//Documento ficha ambiental
			List<DocumentoDigitalizacion> documentosFichaA = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_FICHA_AMBIENTAL);
			if(documentosFichaA != null && !documentosFichaA.isEmpty()){
				byte[] contenido = documentoDigitalizacionFacade.descargar(documentosFichaA.get(0).getIdAlfresco());
				if(contenido != null){
					documentoFichaAmbiental = documentosFichaA.get(0);
					documentoFichaAmbiental.setContenidoDocumento(contenido);
				}
			}
			//Documento estudio impacto ambiental
			List<DocumentoDigitalizacion> documentosEIA = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_ESTUDIO_IMPACTO_AMBIENTAL);
			if(documentosEIA != null && !documentosEIA.isEmpty()){
				byte[] contenido = documentoDigitalizacionFacade.descargar(documentosEIA.get(0).getIdAlfresco());
				if(contenido != null){
					documentoEstudioImpactoAmbiental = documentosEIA.get(0);
					documentoEstudioImpactoAmbiental.setContenidoDocumento(contenido);
				}
			}
			//Documento resolucion
			List<DocumentoDigitalizacion> ListaDocumentos = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_HABILITANTES);
			if(ListaDocumentos != null && !ListaDocumentos.isEmpty()){
				byte[] contenido = documentoDigitalizacionFacade.descargar(ListaDocumentos.get(0).getIdAlfresco());
				if(contenido != null){
					documentosHabilitantes = ListaDocumentos.get(0);
					documentosHabilitantes.setContenidoDocumento(contenido);
				}
			}
			//Documento resolucion
			ListaDocumentos = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_OTROS_DOCUMENTOS);
			if(ListaDocumentos != null && !ListaDocumentos.isEmpty()){
				byte[] contenido = documentoDigitalizacionFacade.descargar(ListaDocumentos.get(0).getIdAlfresco());
				if(contenido != null){
					otrosDocumentos = ListaDocumentos.get(0);
					otrosDocumentos.setContenidoDocumento(contenido);
				}
			}
		} catch (CmisAlfrescoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void cargarCoordenadasDigitalizacion() throws Exception{
		List<ShapeDigitalizacion> objShape = shapeDigitalizacionFacade.obtenerShape(autorizacionAdministrativa.getId(), 2);
		if(objShape != null && objShape.size() > 0){
			for (ShapeDigitalizacion shapeAux : objShape) {
				List<CoordenadaDigitalizacion> coordenadas = coordenadaDigitalizacionFacade.obtenerCoordenadas(shapeAux.getId());
				CoordenadasPoligonos coorP = new CoordenadasPoligonos();
				coorP.setCoordenadas(coordenadas);
				coorP.setTipoForma(shapeAux.getTipoForma());
				coordinatesWrappersGeo.add(coorP);
				shapeDigitalizacion = shapeAux;
			}
		}
	}
	
	private void llenarListaTipoPermiso(){
		listaTipoPermisoAmbiental = new ArrayList<String>();
		listaTipoPermisoAmbiental.add("Licencia Ambiental 1");
		listaTipoPermisoAmbiental.add("Licencia Ambiental 2");
		listaTipoPermisoAmbiental.add("Licencia Ambiental 3");
		listaTipoPermisoAmbiental.add("Licencia Ambiental 4");
		listaTipoPermisoAmbiental.add("Licencia Ambiental B");
		listaTipoPermisoAmbiental.add("Licencia Ambiental C");
	}

	public void cargarFases(boolean noModificable){
		try {
			tieneSector=false;
			if(idSector != null){
				if(noModificable)
					tieneSector=true;
				if(autorizacionAdministrativa != null && autorizacionAdministrativa.getSistema() != null && !autorizacionAdministrativa.getSistema().equals(0))
					tieneSector=false;
				TipoSector tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(idSector);
				autorizacionAdministrativa.setTipoSector(tipoSector);
				listaFases = catalogoFasesCoaFacade.obtenerFasesPorSector(idSector);
				if(listaFases !=  null && !listaFases.isEmpty()){
					bloquearFase = false;
				}else{
					bloquearFase = true;
					autorizacionAdministrativa.setCatalogoFasesCoa(null);
					listaFases = new ArrayList<CatalogoFasesCoa>();
				}
			}
		} catch (Exception e) {
			LOG.error("No se puedo cargar la información de fases", e);
		}
	}
	
	private void cargarEntesResponsable(String sistema){
		// recupero la informacion del aaa digitalizada 
		switch (sistema) {
		case "0": // nuevo
		case "1":	//BDD_FISICO
		case "2":	//BDD_CUATRO_CATEGORIAS
		case "3":	//BDD_SECTOR_SUBSECTOR
			listaAreasEmisoras = areaFacade.getAreasEmisorasAAA();
			break;
		case "4":	//REGULARIZACIÓN
		case "5":	//RCOA
			listaAreasEmisoras = listaAreasControl;
			break;
		default:
			break;
		}
	}
	
	public void buscarFase(){
		try {
			if(idFase != null && idFase > 0){
				CatalogoFasesCoa fase = catalogoFasesCoaFacade.obtenerFasesPorId(idFase) ;
				autorizacionAdministrativa.setCatalogoFasesCoa(fase);
			}else{
				autorizacionAdministrativa.setCatalogoFasesCoa(null);
			}
		} catch (Exception e) {
			LOG.error("No se puedo cargar la información de la fase", e);
		}
	}
	
	public void buscarArea(){
		try {
			if(idAreaEmisora != null && idAreaEmisora > 0){
				Area area = areaFacade.getAreaPorId(idAreaEmisora);
				autorizacionAdministrativa.setAreaEmisora(area);
			}else{
				autorizacionAdministrativa.setAreaEmisora(null);
			}
		} catch (Exception e) {
			LOG.error("No se puedo cargar la información del area emisora", e);
		}
	}
	
	public void buscarDireccion(){
		try {
			if(idDireccionZonal != null && idDireccionZonal > 0){
				Area area = areaFacade.getArea(idDireccionZonal);
				autorizacionAdministrativa.setAreaDZGTecnico(area);
			}else{
				autorizacionAdministrativa.setAreaDZGTecnico(null);
			}
		} catch (Exception e) {
			LOG.error("No se pudo cargar la información de la direccion zonal", e);
		}
	}
	
	public void cambioAutorizacionAdministrativaAmbiental(){
		try {
			mostrarDocumentoFicha = false;
			mostrarDocumentoEstudio = false;
			if(autorizacionAdministrativa.getTipoPermisoAmbiental() != null ){
				if(!autorizacionAdministrativa.getTipoPermisoAmbiental().isEmpty()){
					if(autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Licencia Ambiental 1") ||
						autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Categoría I")){
						autorizacionAdministrativa.setAutorizacionAdministrativaAmbiental("Certificado Ambiental");	
					}else if(autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Licencia Ambiental 2") || 
						autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Ficha Ambiental") ||
						autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Categoría II")){
						autorizacionAdministrativa.setAutorizacionAdministrativaAmbiental("Registro Ambiental");
					}else if(autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Licencia Ambiental 3") ||
						autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Licencia Ambiental 4") ||
						autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Licencia Ambiental C") ||
						autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Licencia Ambiental B") ||
						autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Licencia Ambiental") ||
						autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Declaración Ambiental") ||
						autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Categoría III") ||
						autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Categoría IV") ){
						autorizacionAdministrativa.setAutorizacionAdministrativaAmbiental("Licencia Ambiental");
					}
				}
				if(autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Ficha Ambiental") ||
					autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Categoría II") ||
					autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental().equals("Registro Ambiental")){
					mostrarDocumentoFicha = true;
					mostrarDocumentoEstudio = false;
				}
			}
			if(autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental() != null && (autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental().equals("Licencia Ambiental") ||
				autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Categoría III") ||
				autorizacionAdministrativa.getTipoPermisoAmbiental().equals("Categoría IV"))){
				mostrarDocumentoFicha = false;
				mostrarDocumentoEstudio = true;
			}
			RequestContext.getCurrentInstance().update("form:pnlDocumentos");
		} catch (Exception e) {
			LOG.error("No se pudo cambiar el tipo de permiso ambiental", e);
		}
	}
	
	public void buscarAreaEnteControl(){
		try {
			if(idEnteControl != null && idEnteControl > 0){
				Area area = areaFacade.getArea(idEnteControl);
				autorizacionAdministrativa.setAreaResponsableControl(area);
			}
		} catch (Exception e) {
			LOG.error("No se puedo cargar la información del area emisora", e);
		}
	}
	
	public void limpiarCamposActividad(){
		ciiuPrincipal = new CatalogoCIUU();
		autorizacionAdministrativa.setCatalogoCIUU(null);
	}
	
	public void ciiu1(CatalogoCIUU catalogo){
		ciiuPrincipal = catalogo;
		autorizacionAdministrativa.setCatalogoCIUU(ciiuPrincipal);
	}
	
	private String coodenadasgeograficas="";
	public void handleFileUpload(final FileUploadEvent event) {
		if(shapeDigitalizacion.getZona() == null){
			JsfUtil.addMessageError("Debe seleccionar la zona antes de subir el archivo");
			return;
		}
		
		if(shapeDigitalizacion.getSistemaReferencia() == null){
			JsfUtil.addMessageError("Debe seleccionar el sistema de referencia antes de subir el archivo");
			return;
		}
		shapeDigitalizacion.setTipoIngreso(2);
		coodenadasgeograficas="";
		Integer poligonoAnterior = 0;
        int rows = 0;
        List<String> listCoodenadasgeograficas = new ArrayList<String>();
        coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
        List<CoordenadaDigitalizacion> coordenadasGeograficasAux  = new ArrayList<CoordenadaDigitalizacion>();
        coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
        
        coordinatesWrappers = new ArrayList<CoordenadasPoligonos>();
        ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
        
		varUbicacionArea = new HashMap<String,Double>();
		varUbicacionAux = new HashMap<String,Double>();
        try {
            uploadedFileGeo = event.getFile();
            Workbook workbook = new HSSFWorkbook(uploadedFileGeo.getInputstream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                boolean isEmptyRow = true;
                for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK
                            && StringUtils.isNotBlank(cell.toString())) {
                        isEmptyRow = false;
                    }
                }
                if (isEmptyRow)
                    break;

                if (rows > 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    CoordenadaDigitalizacion coordenada = new CoordenadaDigitalizacion();
                    Integer poligonoActual = (int) cellIterator.next().getNumericCellValue();
                    coordenada.setAreaGeografica(poligonoActual);
                    coordenada.setOrden((int) cellIterator.next().getNumericCellValue());
                    coordenada.setX(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(5, RoundingMode.HALF_DOWN));
                    coordenada.setY(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(5, RoundingMode.HALF_DOWN));
                    coordenadasGeograficas.add(coordenada);
                    
                    // coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
                    if (poligonoAnterior == 0) {
                    	poligonoAnterior = poligonoActual;
					}
                    if (poligonoAnterior == poligonoActual) {
                    	coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
                    	if (sheet.getLastRowNum() == rows) {
                    		listCoodenadasgeograficas.add(coodenadasgeograficas);
    						
    						coordenadasGeograficasAux.add(coordenada);
    						CoordenadasPoligonos coordinatesWrapper = new CoordenadasPoligonos(null, poligono);
    		                coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
    		                coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
    		                coordinatesWrappersGeo.add(coordinatesWrapper);
    		                
    		                coodenadasgeograficas = "";
    		                coordenadasGeograficasAux = new ArrayList<>();
						}
					} else {
						listCoodenadasgeograficas.add(coodenadasgeograficas);
						CoordenadasPoligonos coordinatesWrapper = new CoordenadasPoligonos(null, poligono);
                        coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
                        coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
                        coordinatesWrappersGeo.add(coordinatesWrapper);
                        coordenadasGeograficasAux = new ArrayList<>();
                        
						coodenadasgeograficas = "";
						coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
						poligonoAnterior = poligonoActual;
					}
                    coordenadasGeograficasAux.add(coordenada);
                }
                rows++;
            }

            if(coodenadasgeograficas != "") {
            	listCoodenadasgeograficas.add(coodenadasgeograficas);
            	
            	CoordenadasPoligonos coordinatesWrapper = new CoordenadasPoligonos(null, poligono);
                coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
                coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
                coordinatesWrappersGeo.add(coordinatesWrapper);
                coordenadasGeograficasAux = new ArrayList<>();
            }
            
            obtenerUbicacionesPorCordenadas();
            /***********************************************************************************************/
            // verificarCapasInterseccion(coodenadasgeograficas);
            capasIntersecciones= new HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>>();
			listaCapasInterseccionPrincipal = new HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>>();
			estadoZonaIntangible=false;
			estadoZonaIntangibleAux=false;
			estadoFrontera=false;
			esZonaSnapEspecial = false;
            
            listaNombresCapasInterseca = new ArrayList<String>();
            
            for (String coodenadasgeograficas : listCoodenadasgeograficas) {
            	verificarCapasInterseccion(coodenadasgeograficas);
            }
            
            getDetalleIntersecciones();
			
            /*********************************************************************************************************/
            
        } catch (Exception e) {
        	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
            
        	LOG.error("Error procesando el excel de coordenadas", e);
            coordinatesWrappers = new ArrayList<CoordenadasPoligonos>();
            ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
    		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
    		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
    		mostrarDetalleInterseccion = false;
    		estadoFrontera=false;
    		
    		varUbicacionArea = new HashMap<String,Double>();
    		varUbicacionAux = new HashMap<String,Double>();
        }
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
			
			/*if (intRestCapaImpl[0].getInformacion().getError() != null) {            		
        		JsfUtil.addMessageError(intRestCapaImpl[0].getInformacion().getError().toString());
        		return;
        	}*/
			
			InterseccionProyectoDigitalizacion capaInterseccion = new InterseccionProyectoDigitalizacion();	                			
			List<DetalleInterseccionDigitalizacion> listaIntersecciones = new ArrayList<DetalleInterseccionDigitalizacion>();
			if(intRestCapaImpl[0].getCapa() == null)
				return;
			for (IntersecadoCapa_capa capas : intRestCapaImpl[0].getCapa()) {
				if(Integer.valueOf(capas.getNum())>0)
				{
					capaInterseccion = new InterseccionProyectoDigitalizacion();
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
						for(Campos_coordenada oficina : capas.getCampos())
						{
							System.out.println("oficina tecnica:"+ oficina.getFcode());
							ubicacionOficinaTecnica=ubicacionGeograficaFacade.buscarUbicacionPorCodigoInec(oficina.getFcode());
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
						if(!capas.getNombre().equals("MAR TERRITORIAL"))
						{
	//						System.out.println("CAPA:::"+capas.getNombre());
							capaInterseccion.setCapa(getCapa(capas.getNombre()));
							capaInterseccion.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
							capaInterseccion.setDescripcionCapa(capas.getNombre());
							DetalleInterseccionDigitalizacion detallecapa = new DetalleInterseccionDigitalizacion();
							listaIntersecciones = new ArrayList<DetalleInterseccionDigitalizacion>();
							for(Campos_coordenada interseccion : capas.getCampos())
							{
								detallecapa = new DetalleInterseccionDigitalizacion();
								if(interseccion.getTphmd()!=null)
								{
									detallecapa.setNombreGeometria(interseccion.getTphmd());
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								if(interseccion.getTpcnv()!=null)
								{	                							
									detallecapa.setNombreGeometria(interseccion.getTpcnv());
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								if(interseccion.getNam()!=null)
								{	                							
									String nombreGeom = interseccion.getNam();
									if(capas.getNombre().equals("SNAP")) {
										nombreGeom = interseccion.getMap() + " " + interseccion.getNam();//,
	
										if(interseccion.getNam().equals("YASUNI") || interseccion.getNam().equals("CUYABENO")) {
											estadoZonaIntangibleAux = true;
											esZonaSnapEspecial = true;
										}
									}
									detallecapa.setNombreGeometria(nombreGeom);
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								if(interseccion.getNameco()!=null)
								{
									detallecapa.setNombreGeometria(interseccion.getNameco());
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								if(interseccion.getCtn2()!=null)
								{
									detallecapa.setNombreGeometria(interseccion.getCtn2());
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								if(interseccion.getFcode()!=null)
								{
									if(capas.getNombre().equals("SNAP"))
										detallecapa.setCodigoUnicoCapa(interseccion.getFcode());
									else
										detallecapa.setCodigoConvenio(interseccion.getFcode());
								}
								if(capas.getNombre().equals("LIMITE INTERNO 20 KM"))
								{
									detallecapa.setNombreGeometria("SI");
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								listaIntersecciones.add(detallecapa);
							}
							capasIntersecciones.put(capaInterseccion, listaIntersecciones);
	
							if(capaInterseccion.getCapa() != null && (capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_SNAP) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_BP) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_PFE))) {
								List<DetalleInterseccionDigitalizacion> listaInterseccionesAux = new ArrayList<DetalleInterseccionDigitalizacion>();
								for (DetalleInterseccionDigitalizacion detalle : listaIntersecciones) {
									String capaInterseca = capaInterseccion.getCapa().getId() + "- " + detalle.getIdGeometria();
									
									if(!listaNombresCapasInterseca.contains(capaInterseca)) {
										listaNombresCapasInterseca.add(capaInterseca);
										listaInterseccionesAux.add(detalle);
									}
								}
								
								if(listaInterseccionesAux.size() > 0) {
									Iterator<Entry<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>>> it = listaCapasInterseccionPrincipal.entrySet().iterator();
								    while (it.hasNext())
								    {
								       Entry<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>> item = it.next();
								       if(item.getKey().getCapa().getId().equals(capaInterseccion.getCapa().getId())) {
								    	   listaInterseccionesAux.addAll(item.getValue());
								    	   listaCapasInterseccionPrincipal.remove(item.getKey());
								       }
								    }
								    listaCapasInterseccionPrincipal.put(capaInterseccion, listaInterseccionesAux);
								}
							}
						}
					}	                					
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
             	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
             	System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
             	
             	ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
                
             	coordinatesWrappers = new ArrayList<CoordenadasPoligonos>();
        		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
        		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
             	mostrarDetalleInterseccion = false;
         		estadoFrontera=false;
             }
	}
	
	public CapasCoa getCapa(String capa) {
        for (CapasCoa c : capas) {
            if (c.getAbreviacion().equals(capa))
                return c;
        }
        return null;
    }
			
	/**
	 * obtiene las áreas con la cuál intersecan las coordenadas
	 * 
	 */
	public void getDetalleIntersecciones()
	{	
		mostrarDetalleInterseccion = false;
		zonasInterccionDetalle="";
		
		areasProtegidas = new ArrayList<String>();
		bosquesProtectores = new ArrayList<String>();
		
		HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>> lista = listaCapasInterseccionPrincipal;
		
		if(lista.size()>0)
		{			
			String nombreCapa="";
			for (InterseccionProyectoDigitalizacion i : lista.keySet()) {
				nombreCapa=i.getDescripcionCapa();
				for(DetalleInterseccionDigitalizacion j : lista.get(i))
				{
					if(i.getCapa().getAbreviacion().equals("SNAP")){
						areasProtegidas.add(j.getNombreGeometria());
					}else{
						bosquesProtectores.add(j.getNombreGeometria());
					}								
				}
			}
			zonasInterccionDetalle =zonasInterccionDetalle + "</ul>";
		}
		
		if(zonasInterccionDetalle != "")
			mostrarDetalleInterseccion = true;
		
		if(estadoZonaIntangible){
			mostrarDetalleInterseccion = true;
		}
	}
	
	private Double recuperarAreaPoligono(String coordenada1, String coordenada2) throws Exception {
		SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
        ws.setEndpoint(Constantes.getInterseccionesWS());
        
		ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada(); //verifica que el poligono este contenida dentro de la ubicación geográfica
		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
		verificarGeoImpla.setXy1(coordenada1);
		verificarGeoImpla.setXy2(coordenada2);
		ContieneZona_resultado[]intRestGeoImpl = ws.contieneZona(verificarGeoImpla);
		if (intRestGeoImpl[0].getInformacion().getError() != null) { 
			return null;
		}
		else
		{
			if (!intRestGeoImpl[0].getContieneCapa().getValor().equals("f")) {
				Double areaPoligono = Double.valueOf(intRestGeoImpl[0].getValorArea().getArea());
				return areaPoligono;
			}
		}
		return null;
	}
		
	private void recuperarParroquiasInterseccion(Double areaPoligono, Intersecado_resultado[]intRest) throws Exception {
		String parroquia="";
        Double valorParroquia=0.0;
        Integer orden=2; 
        
        
		for (Intersecado_capa intersecado_capa : intRest[0].getCapa()) { 
			String capaNombre=intersecado_capa.getNombre();   
			if(intersecado_capa.getError()!=null){
				JsfUtil.addMessageError(intersecado_capa.getError().toString());
			}
			Intersecado_coordenada[] intersecadoCoordenada=intersecado_capa.getCruce();
			if (intersecadoCoordenada != null){

				for (Intersecado_coordenada intersecado_coordenad : intersecadoCoordenada) {
					if (intersecado_coordenad.getValor() != null) {
						valorParroquia = Double.valueOf(intersecado_coordenad.getValor());
					}
					if (valorParroquia >= 0) {
						if (capaNombre.equals("dpa")) {                							
							parroquia=intersecado_coordenad.getObjeto();
							Double areaValorParroquia = areaPoligono * valorParroquia; //calcula el área de intersección de la parroquia
							
							if (varUbicacionArea.get(parroquia) != null) {
								varUbicacionArea.put(parroquia, areaValorParroquia + varUbicacionArea.get(parroquia)); // agrega el area al total de la parroquia, cuando son varios poligonos
							} else {
								varUbicacionArea.put(parroquia,areaValorParroquia);
							}
							
							if (varUbicacionAux.get(parroquia) != null) {
								if (valorParroquia >= varUbicacionAux.get(parroquia)) {
									varUbicacionAux.put(parroquia,orden.doubleValue());
								}
							} else {
								varUbicacionAux.put(parroquia,orden.doubleValue());
							}
						}
					}
					orden ++;
				}
			}
		}
	}
	
	public void cargarUbicacionProyecto(HashMap<String, Double> parroquia){
		ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
		parroquiaSeleccionadas= new HashMap<String, UbicacionesGeografica>();

		Iterator it = parroquia.entrySet().iterator();
		Integer orden=0;
		String inec="";
		
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			orden = e.getValue().intValue();
			if (!inec.equals("")) {
				Map<String, Object> parametros = new HashMap<String, Object>();
				parametros.put("codeInec", inec);
				List<UbicacionesGeografica> lista = crudServiceBean.findByNamedQueryGeneric(UbicacionesGeografica.FIND_PARROQUIA,parametros);
				ubicacionesSeleccionadas.addAll(lista);
				
			}
		}
	}
	
	private void obtenerUbicacionesPorCordenadas() throws Exception{
		boolean esPoligono = false;
        for(int i=0;i<=coordinatesWrappersGeo.size()-1;i++){
        	for (int j = 0; j <=coordinatesWrappersGeo.get(i).getCoordenadas().size()-1; j++) {
        		if(j==0){
        			coodenadasgeograficas = coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getX().toString().replace("," , ".")+" "+coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getY().toString().replace("," , ".")+" "+ shapeDigitalizacion.getZona();
        		}else {
        			coodenadasgeograficas = coodenadasgeograficas +","+coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getX().toString().replace("," , ".")+" "+coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getY().toString().replace("," , ".")+" "+shapeDigitalizacion.getZona();
        		}
        		if(j > 2)
        			esPoligono=true;
        	}
        	permisoFisico = shapeDigitalizacion.getSistemaReferencia();
        	Reproyeccion_entrada poligono = new Reproyeccion_entrada(Constantes.getUserWebServicesSnap(), autorizacionAdministrativa.getResolucion()!=null?autorizacionAdministrativa.getResolucion():"",permisoFisico, coodenadasgeograficas);
        	
            SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
            ws.setEndpoint(Constantes.getInterseccionesWS());
            Reproyeccion_resultado[]intRest;
            try {
            	intRest=ws.reproyeccion(poligono);
            	
            	if (intRest[0].getCoordenada()[0].getError()!=null) {
            		JsfUtil.addMessageError(intRest[0].getInformacion().getError().toString());
            		System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
            		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
            		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
            		return;
            	}            	
            	if (intRest[0].getCoordenada()[0].getError()!=null) {
            		JsfUtil.addMessageError(intRest[0].getCoordenada()[0].getError());
            		System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
            		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
            		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
            		return;
            	} 
            	
            	for (int j = 0; j <=coordinatesWrappersGeo.get(i).getCoordenadas().size()-1; j++) {
                    String[] parts=intRest[0].getCoordenada()[j].getXy().split(" ");
                    String x=parts[0];
                    String y=parts[1];
                    BigDecimal bx=new BigDecimal(x);
                    BigDecimal by=new BigDecimal(y);
                    coordinatesWrappersGeo.get(i).getCoordenadas().get(j).setX(bx);
                    coordinatesWrappersGeo.get(i).getCoordenadas().get(j).setY(by);
                    if(coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getDescripcion()==null){
                    	coordinatesWrappersGeo.get(i).getCoordenadas().get(j).setDescripcion("");
                        }
                }
            	
            	/**
            	 * obteniendo nuevas coordenadas ya que pueden ingresar en otro sistema que no se wgs84
            	 */
            	String coordenadasTransformadas = "";
            	for (int j = 0; j <=coordinatesWrappersGeo.get(i).getCoordenadas().size()-1; j++) {
            		if(j==0){
            			coordenadasTransformadas = coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getX().toString().replace("," , ".")+" "+coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getY().toString().replace("," , ".");
            		}else {
            			coordenadasTransformadas = coordenadasTransformadas +","+coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getX().toString().replace("," , ".")+" "+coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getY().toString().replace("," , ".");
            		}          	
            	}
            	
            	Intersecado_entrada poligono_ = new Intersecado_entrada();
                poligono_.setU(Constantes.getUserWebServicesSnap());
                poligono_.setIsla(Constantes.SIGLAS_INSTITUCION + "-RA");
                poligono_.setTog(esPoligono?"po":"pu");
                poligono_.setXy(coordenadasTransformadas);
                poligono_.setShp("dp");
            	
            	SVA_Reproyeccion_IntersecadoPortTypeProxy ws_=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
                ws_.setEndpoint(Constantes.getInterseccionesWS());
                Intersecado_resultado[]intRest_;
                 
                intRest_=ws_.interseccion(poligono_);
              	if (intRest_[0].getInformacion().getError() != null) {
              		JsfUtil.addMessageError(intRest_[0].getInformacion().getError().toString());
              		System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
              		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
              		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
              		return;
              	}            	
              	if (intRest_[0].getCapa()[0].getError() != null) {
              		JsfUtil.addMessageError(intRest_[0].getCapa()[0].getError().toString());
              		System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
              		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
              		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
              		return;
              	}
              
            	Double areaPoligonoMetros = esPoligono ? recuperarAreaPoligono(coordenadasTransformadas, coordenadasTransformadas): 0.0;
            	if(areaPoligonoMetros == null) {
            		JsfUtil.addMessageError("Error a recuperar la ubicación del proyecto");
            		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
            		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
            		return;
            	}
            	
            	Double areaPoligono = areaPoligonoMetros/10000;
            	
            	coordinatesWrappersGeo.get(i).setSuperficie(new BigDecimal(areaPoligono));
            	recuperarParroquiasInterseccion(areaPoligono, intRest_);
            	cargarUbicacionProyecto(varUbicacionAux);
            	ubicacionMasArea();
            	
            }
            catch (RemoteException e) {
            	e.printStackTrace();
            	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
            	coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
            	coordinatesWrappers = new ArrayList<CoordenadasPoligonos>();
            	coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();                	
        		ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();            		
        		varUbicacionArea = new HashMap<String,Double>();
        		varUbicacionAux = new HashMap<String,Double>();
            }
		}
	}
	
	private MayorAreaVO area = new MayorAreaVO();
	public void ubicacionMasArea()
	{	
		Iterator it = varUbicacionArea.entrySet().iterator();
		double orden=0;
		String inec="";
		double maxArea = 0;
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			orden = e.getValue().doubleValue();
			if (orden >= maxArea) {
				maxArea = orden;
				area.setInec(inec);
				area.setValor(orden);
			}
		}
		try
		{
			ubicacionPrincipal=ubicacionGeograficaFacade.buscarUbicacionPorCodigoInec(area.getInec());
		} catch(Exception e)
		{
			ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
			System.out.println("geographical_locations --> codeInec:::: " +inec+ " ::::: no existe para la ubicacionPrincipal");
		}
	}
	
	public void buscarCantonesListener(){
		try {
			
			listaCantones = ubicacionGeograficaFacade.buscarUbicacionGeografica(idProvincia);
			
		} catch (Exception e) {
			LOG.error("Error al consultar cantones", e);
		}
	}
	
	public void buscarParroquiasListener(){
		try {
			
			listaParroquias = ubicacionGeograficaFacade.buscarUbicacionGeografica(idCanton);
			
		} catch (Exception e) {
			LOG.error("Error al consultar parroquias", e);
		}
	}
	
	public void llenarTablaUbicaciones(){
		try {
			
			if(idParroquia != null){
				
				UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(idParroquia);
				
				if(listaUbicacionesSeleccionadas.contains(ubicacion))			{
					JsfUtil.addMessageError("La ubicación ya se encuentra seleccionada");
				}else{
					listaUbicacionesSeleccionadas.add(ubicacion);
					
					idProvincia = null;
					idCanton = null;
					idParroquia = null;
					
					setIdProvincia(null);
					setIdCanton(null);
					setIdParroquia(null);
					
					RequestContext.getCurrentInstance().update("form:ubicacionDlg");
					RequestContext.getCurrentInstance().execute("PF('ubicacionDlg').hide()");
				}				
			}else{
				JsfUtil.addMessageError("Debe seleccionar la parroquia del cantón.");
			}
			
		} catch (Exception e) {
			LOG.error("Error al consultar parroquias", e);
		}
	}
	
	public void eliminarUbicacion(UbicacionesGeografica ubiciacionEliminada){
		try {
			listaUbicacionesSeleccionadas.remove(ubiciacionEliminada);
		} catch (Exception e) {
			LOG.error("Error al eliminar ubicacion", e);
		}
	}
	
	public void abrirDialogoUbicaciones(){
		try {
			listaProvincias = ubicacionGeograficaFacade.getProvincias();
			listaCantones = new ArrayList<UbicacionesGeografica>();
			listaParroquias = new ArrayList<UbicacionesGeografica>();
			
			idProvincia = null;
			idCanton = null;
			idParroquia = null;
			
			setIdProvincia(null);
			setIdCanton(null);
			setIdParroquia(null);
			
			RequestContext.getCurrentInstance().update("form:cmbProvincia");
			RequestContext.getCurrentInstance().update("form:cmbCanton");
			RequestContext.getCurrentInstance().update("form:cmbParroquia");
			RequestContext.getCurrentInstance().update("form:ubicacionDlg");
			RequestContext.getCurrentInstance().execute("PF('ubicacionDlg').show()");
		} catch (Exception e) {
			LOG.error("Error al cargar ubicaciones", e);
		}		
	}
	
	public void ingresarAreasProtegidas(){
		if(nombreAreaProtegida != null && !nombreAreaProtegida.isEmpty() && !nombreAreaProtegida.equals("")){
			if(areasProtegidasSeleccionadas.contains(nombreAreaProtegida)){
				JsfUtil.addMessageError("El Área Protegida ya se encuentra seleccionada.");
			}else{
				areasProtegidasSeleccionadas.add(nombreAreaProtegida);
			}
		}
	}
	
	public void ingresarBosquesProtectores(){
		if(nombreBosqueProtector != null && !nombreBosqueProtector.isEmpty() && !nombreBosqueProtector.equals("")){
			if(bosquesProtectoresSeleccionados.contains(nombreBosqueProtector)){
				JsfUtil.addMessageError("El Bosque Protector ya se encuentra seleccionado");
			}else{
				bosquesProtectoresSeleccionados.add(nombreBosqueProtector);
			}
		}
	}
	
	public void eliminarAreaProtegida(String area){
		areasProtegidasSeleccionadas.remove(area);
	}
	
	public void eliminarBosqueProtector(String bosque){
		bosquesProtectoresSeleccionados.remove(bosque);
	}
	
	private DocumentoDigitalizacion uploadListener(FileUploadEvent event, Class<?> clazz){
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentoDigitalizacion documento = new DocumentoDigitalizacion();
		documento.setNombre(event.getFile().getFileName());
		documento.setContenidoDocumento(contenidoDocumento);
		String[] split=documento.getNombre().split("\\.");
		documento.setExtension("."+split[split.length-1]);
		documento.setMime(event.getFile().getContentType().compareTo("application/download")!=0?event.getFile().getContentType():"application/pdf");
		return documento;
	}
	
	public void uploadDocumentoCertificado(FileUploadEvent event){
		certificadoInterseccion = new DocumentoDigitalizacion();
		certificadoInterseccion = this.uploadListener(event, AutorizacionAdministrativaAmbiental.class);
		certificadoInterseccion.setTipoIngreso(2);
	}
	
	public void uploadDocumentoMapa(FileUploadEvent event){
		documentoMapa = new DocumentoDigitalizacion();
		documentoMapa = this.uploadListener(event, AutorizacionAdministrativaAmbiental.class);
		documentoMapa.setTipoIngreso(2);
	}
	
	public void uploadDocumentoResolucion(FileUploadEvent event){
		documentoResolucion = new DocumentoDigitalizacion();
		documentoResolucion = this.uploadListener(event, AutorizacionAdministrativaAmbiental.class);
		documentoResolucion.setTipoIngreso(2);
	}
	
	public void uploadDocumentoFichaAmbiental(FileUploadEvent event){
		documentoFichaAmbiental = new DocumentoDigitalizacion();
		documentoFichaAmbiental = this.uploadListener(event, AutorizacionAdministrativaAmbiental.class);
		documentoFichaAmbiental.setTipoIngreso(2);
	}
	
	public void uploadDocumentoEstudioImpactoAmbiental(FileUploadEvent event){
		documentoEstudioImpactoAmbiental = new DocumentoDigitalizacion();
		documentoEstudioImpactoAmbiental = this.uploadListener(event, AutorizacionAdministrativaAmbiental.class);
		documentoEstudioImpactoAmbiental.setTipoIngreso(2);
	}
	
	public void uploadDocumentosHabilitantes(FileUploadEvent event){
		documentosHabilitantes = new DocumentoDigitalizacion();
		documentosHabilitantes = this.uploadListener(event, AutorizacionAdministrativaAmbiental.class);
		documentosHabilitantes.setTipoIngreso(2);
	}
	
	public void uploadOtrosDocumentos(FileUploadEvent event){
		otrosDocumentos = new DocumentoDigitalizacion();
		otrosDocumentos = this.uploadListener(event, AutorizacionAdministrativaAmbiental.class);
		otrosDocumentos.setTipoIngreso(2);
	}
	
	public StreamedContent getDocumentoCertificadoDownload(){
		try {
			if (certificadoInterseccion != null && certificadoInterseccion.getContenidoDocumento() !=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(certificadoInterseccion.getContenidoDocumento()),certificadoInterseccion.getMime(),certificadoInterseccion.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {
			LOG.error("Error al guardar documento Certificado", e);
		}
		return null;
	}
	
	public StreamedContent getDocumentoMapaDownload(){		
		try {
			if (documentoMapa != null && documentoMapa.getContenidoDocumento() !=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documentoMapa.getContenidoDocumento()),documentoMapa.getMime(),documentoMapa.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {
			LOG.error("Error al guardar documento Certificado", e);
		}
		return null;
	}
	
	public StreamedContent getDocumentoResolucionDownload() {
		try {
			if (documentoResolucion != null	&& documentoResolucion.getContenidoDocumento() != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(documentoResolucion.getContenidoDocumento()),
						documentoResolucion.getMime(),
						documentoResolucion.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {
			LOG.error("Error al guardar documento Resolución", e);
		}
		return null;
	}
	
	public StreamedContent getDocumentoFichaAmbientalDownload() {
		try {
			if (documentoFichaAmbiental != null	&& documentoFichaAmbiental.getContenidoDocumento() != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(documentoFichaAmbiental.getContenidoDocumento()),
						documentoFichaAmbiental.getMime(),
						documentoFichaAmbiental.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {
			LOG.error("Error al guardar documento Ficha Ambiental", e);
		}
		return null;
	}
	
	public StreamedContent getDocumentoEstudioImpactoAmbientalDownload() {
		try {
			if (documentoEstudioImpactoAmbiental != null	&& documentoEstudioImpactoAmbiental.getContenidoDocumento() != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(documentoEstudioImpactoAmbiental.getContenidoDocumento()),
						documentoEstudioImpactoAmbiental.getMime(),
						documentoEstudioImpactoAmbiental.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {
			LOG.error("Error al guardar documento Estudio Impacto Ambiental", e);
		}
		return null;
	}
	
	public StreamedContent getDocumentosHabilitantesDownload() {
		try {
			if (documentosHabilitantes != null	&& documentosHabilitantes.getContenidoDocumento() != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(documentosHabilitantes.getContenidoDocumento()),
						documentosHabilitantes.getMime(),
						documentosHabilitantes.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {
			LOG.error("Error al guardar documentos Habilitantes", e);
		}
		return null;
	}
	
	public StreamedContent getOtrosDocumentosDownload() {
		try {
			if (otrosDocumentos != null	&& otrosDocumentos.getContenidoDocumento() != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(otrosDocumentos.getContenidoDocumento()),
						otrosDocumentos.getMime(),
						otrosDocumentos.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {
			LOG.error("Error al guardar otros documentos", e);
		}
		return null;
	}
	
	public void redireccionarProyectos(){
		if(habilitarEditar)
			JsfUtil.redirectTo("/pages/rcoa/digitalizacion/listadoProcesosDigitalizados.jsf");
		else
			JsfUtil.redirectTo("/pages/rcoa/digitalizacion/digitalizacionAAA.jsf");
	}
	
	public void crearNumeroTramite(){
		//validarIngresoResolucion();
		if(!tieneCodigoTramite){
			if(autorizacionAdministrativa != null && autorizacionAdministrativa.getFechaResolucion() != null && autorizacionAdministrativa.getResolucion() != null && !autorizacionAdministrativa.getResolucion().isEmpty()){
				if(autorizacionAdministrativa.getCodigoProyecto() == null || autorizacionAdministrativa.getCodigoProyecto().isEmpty()){
					Date fechaIngresada = autorizacionAdministrativa.getFechaResolucion();
					Calendar fechaEmision = Calendar.getInstance();
					fechaEmision.setTime(fechaIngresada);
					int anioEmision = fechaEmision.get(Calendar.YEAR);
					try {
						autorizacionAdministrativa.setCodigoProyecto(Constantes.SIGLAS_INSTITUCION + "-RA-FIS" + "-" + anioEmision+"-"+
								secuenciasFacade.getNextValueDedicateSequence("FIS-" + anioEmision, 4));
					} catch (Exception e) {
						e.printStackTrace();
					}
					RequestContext.getCurrentInstance().update(":form:txtResolucion");
				}
			}
		}
	}
	
	public String validarIngresoResolucion(){
		String msjError= "";
		try {
			if(autorizacionAdministrativa != null && autorizacionAdministrativa.getResolucion() != null && autorizacionAdministrativa.getFechaResolucion() != null && autorizacionAdministrativa.getAreaEmisora() != null){
				List<AutorizacionAdministrativaAmbiental> lista = autorizacionAdministrativaAmbientalFacade.obtenerNumeroResolucion(autorizacionAdministrativa);
				if(lista != null && !lista.isEmpty()){
					AutorizacionAdministrativaAmbiental autorizacionConsultada = lista.get(0);
					Date fechaIngresada = autorizacionAdministrativa.getFechaResolucion();
					Date fechaConsulta = autorizacionConsultada.getFechaResolucion();
					Calendar fechaEmision = Calendar.getInstance();
					fechaEmision.setTime(fechaIngresada);
					int anioEmision = fechaEmision.get(Calendar.YEAR);
					Calendar fechaCon = Calendar.getInstance();
					fechaCon.setTime(fechaConsulta);
					int anioConsulta = fechaCon.get(Calendar.YEAR);
					if(anioEmision == anioConsulta && autorizacionAdministrativa.getAreaEmisora().equals(autorizacionConsultada.getAreaEmisora())){
						msjError = "El número de la Resolución ya está ingresado para la fecha y área seleccionada";
						//autorizacionAdministrativa.setResolucion(null);
						RequestContext.getCurrentInstance().update(":form:txtResolucion");
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error al validar el número de resolución", e);
		}
		return msjError;
	}
	
	public void guardar(boolean mostrarMensaje){
		try {
			if(autorizacionAdministrativa.getResolucion() == null || autorizacionAdministrativa.getResolucion().isEmpty()){
				JsfUtil.addMessageError("Debe ingresar el campo N° Resolución Administrativa Ambiental");
				return;
			}
			if(autorizacionAdministrativa.getFechaResolucion() == null ){
				JsfUtil.addMessageError("Debe seleccionar la fecha de emisión de la Autorización Administrativa Ambiental");
				return;
			}
			if(autorizacionAdministrativa.getAreaEmisora() == null){
				JsfUtil.addMessageError("El campo Autoridad Ambiental emisora de la AAA es requerido");
				return;
			}
			String mensajeError = validarIngresoResolucion();
			if(!mensajeError.isEmpty()){
				JsfUtil.addMessageError(mensajeError);
				return;
			}
			/*if(autorizacionAdministrativa.getCodigoProyecto() == null){
				JsfUtil.addMessageError("Debe ingresar el campo código del proyecto.");
				return;
			}*/


			if(autorizacionAdministrativa.getNombreUsuario() == null || autorizacionAdministrativa.getNombreUsuario().equals("")){
				JsfUtil.addMessageError("El campo Nombre del operador es requerido");
				return;
			}
			if(autorizacionAdministrativa.getIdentificacionUsuario() == null || autorizacionAdministrativa.getIdentificacionUsuario().equals("")){
				JsfUtil.addMessageError("El campo Cédula / RUC operador es requerido");
				return;
			}
			if(shapeDigitalizacion.getSistemaReferencia() == null || shapeDigitalizacion.getSistemaReferencia().equals("")){
				JsfUtil.addMessageError("El campo Sistema de referencia es requerido");
				return;
			}
			
			if(shapeDigitalizacion.getZona() == null || shapeDigitalizacion.getZona().equals("")){
				JsfUtil.addMessageError("El campo Zona es requerido");
				return;
			}
			if(autorizacionAdministrativa.getIdentificacionUsuario() != null && !autorizacionAdministrativa.getIdentificacionUsuario().isEmpty()
					&& !autorizacionAdministrativa.getIdentificacionUsuario().equals("")){
				Usuario operador = usuarioFacade.buscarUsuario(autorizacionAdministrativa.getIdentificacionUsuario());
				if(operador != null && operador.getId() != null){
					autorizacionAdministrativa.setUsuario(operador);
				}
			}
			if(autorizacionPrincipal != null){
				autorizacionAdministrativa.setNombreUsuario(autorizacionPrincipal.getNombreUsuario());
				autorizacionAdministrativa.setIdentificacionUsuario(autorizacionPrincipal.getIdentificacionUsuario());
				autorizacionAdministrativa.setUsuario(autorizacionPrincipal.getUsuario());
			}
			// si es tecnico de calidad guardo el historico del que estuvo antes de guardar los cambios
			if(habilitarEditar)
				guardarHistorico();

			if(autorizacionAdministrativa.getCodigoProyecto() == null ){
				crearNumeroTramite();
			}
			autorizacionAdministrativa.setResolucion(autorizacionAdministrativa.getResolucion().toUpperCase());
			if(autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaSeleccionada() != null && autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaSeleccionada().getEstado().equals("Actualización de Certificado de Intersección")){
				autorizacionAdministrativa.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental()+" - Actualización de Certificado de Intersección");
			}
			autorizacionAdministrativaAmbientalFacade.save(autorizacionAdministrativa, loginBean.getUsuario());
			if(autorizacionAdministrativa.getIdProceso() == null && !autorizacionAdministrativa.isFinalizado() && !habilitarEditar){
				iniciarProcesodigitalizacion();
			}
			
			guardarCoorGeografica();
			
			if(uploadedFileGeo!=null || shapeDigitalizacion.getId() == null)
			{
				shapeDigitalizacionFacade.eliminarShape(autorizacionAdministrativa.getId(), 2);
				String tipoZona, sistemaReferencias;
				Integer tipoIngreso;
				tipoZona=shapeDigitalizacion.getZona();
				sistemaReferencias=shapeDigitalizacion.getSistemaReferencia();
				tipoIngreso=shapeDigitalizacion.getTipoIngreso();
				if(uploadedFileGeo!=null)
					shapeDigitalizacion = new ShapeDigitalizacion();
				for (int i = 0; i <= coordinatesWrappersGeo.size() - 1; i++) {
					shapeDigitalizacion = new ShapeDigitalizacion();
					//valido que el shape tenga coordenadas
					if(coordinatesWrappersGeo.get(i).getCoordenadas() == null || coordinatesWrappersGeo.get(i).getCoordenadas().size() == 0)
						continue;
					shapeDigitalizacion.setTipoForma(coordinatesWrappersGeo.get(i).getTipoForma());
					shapeDigitalizacion.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					shapeDigitalizacion.setTipo(2);//2=coordenadas geograficas 1=coordenadas implantacion
					shapeDigitalizacion.setZona(tipoZona);
					shapeDigitalizacion.setSistemaReferencia(sistemaReferencias);
					shapeDigitalizacion.setTipoIngreso(tipoIngreso);
					shapeDigitalizacion.setNumeroActualizaciones(0);
					shapeDigitalizacion.setSuperficie(coordinatesWrappersGeo.get(i).getSuperficie());
					shapeDigitalizacion=shapeDigitalizacionFacade.guardar(shapeDigitalizacion, loginBean.getUsuario());
					
					CoordenadaDigitalizacion coor = new CoordenadaDigitalizacion();
					for (int j = 0; j <=coordinatesWrappersGeo.get(i).getCoordenadas().size()-1; j++) {
						coor= new CoordenadaDigitalizacion();
						coor.setShapeDigitalizacion(shapeDigitalizacion);
						coor.setOrden(coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getOrden());
						coor.setX(coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getX());
						coor.setY(coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getY());
						coor.setAreaGeografica(coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getAreaGeografica());
						coor.setTipo(2);//2=coordenadas geograficas 1=coordenadas implantacion
						coor.setNumeroActualizacion(0);
						coordenadaDigitalizacionFacade.guardar(coor, loginBean.getUsuario());
					}
				}
				uploadedFileGeo = null;
			}else{
				shapeDigitalizacion.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				shapeDigitalizacion=shapeDigitalizacionFacade.guardar(shapeDigitalizacion, loginBean.getUsuario());
			}
			
			//cargo las ubicaciones de acuerdo a las coordenadas
			if(ubicacionesSeleccionadas.size() == 0 && shapeDigitalizacion.getSistemaReferencia() != null && shapeDigitalizacion.getZona() != null)
				obtenerUbicacionesPorCordenadas();
			List<UbicacionDigitalizacion> ubicacionEliminar = new ArrayList<UbicacionDigitalizacion>();
			List<UbicacionDigitalizacion> listaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 1);
			if(listaUbicacionTipo == null)
				listaUbicacionTipo = new ArrayList<UbicacionDigitalizacion>();
			//eliminar las anteriores 
			for (UbicacionDigitalizacion ubicacionDigitalizacion : listaUbicacionTipo) {
				boolean existe = false;
				for(UbicacionesGeografica ubi : ubicacionesSeleccionadas){
					if(ubicacionDigitalizacion.getUbicacionesGeografica().equals(ubi)){
						existe = true;
					}
				}
				if(!existe)
					ubicacionEliminar.add(ubicacionDigitalizacion);
			}
			// elimino los que ya fueron guardados pero ya no se encuentran en las coordenadas actuales
			if(ubicacionEliminar.size() > 0){
				for (UbicacionDigitalizacion objUbicacion : ubicacionEliminar) {
					objUbicacion.setEstado(false);
					ubicacionDigitalizacionFacade.guardar(objUbicacion, loginBean.getUsuario());
				}
				listaUbicacionTipo.removeAll(ubicacionEliminar);
			}
			for(UbicacionesGeografica ubi : ubicacionesSeleccionadas){
				boolean existe = false;
				if(listaUbicacionTipo != null && listaUbicacionTipo.size() > 0){
					for (UbicacionDigitalizacion ubicacionDigitalizacion : listaUbicacionTipo) {
						if(ubicacionDigitalizacion.getUbicacionesGeografica().equals(ubi)){
							existe = true;
							break;
						}
					}
				}
				// si ya existe no guardo
				if(existe)
					continue;
				UbicacionDigitalizacion ubicacion = new UbicacionDigitalizacion();
				ubicacion.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				ubicacion.setUbicacionesGeografica(ubi);
				ubicacion.setTipoIngreso(1);
				if(area.getInec() != null && area.getInec().equals(ubi.getCodificacionInec())){
					ubicacion.setPrincipal(true);
				}else{
					ubicacion.setPrincipal(false);
				}
				ubicacionDigitalizacionFacade.guardar(ubicacion, loginBean.getUsuario());
			}
			//guardar capas de interseccion y detalle de interseccion
			if(uploadedFileGeo!=null)
			{
//				interseccionProyectoLicenciaAmbientalFacade.eliminar(proyecto, JsfUtil.getLoggedUser().getNombre(), 0);
				//eliminar para actualizar
				for (InterseccionProyectoDigitalizacion i : capasIntersecciones.keySet()) {
					i.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					i.setFechaProceso(new Date());
					i=interseccionProyectoDigitalizacionFacade.guardar(i, loginBean.getUsuario());
					for(DetalleInterseccionDigitalizacion j : capasIntersecciones.get(i))
					{
						j.setInterseccionProyectoDigitalizacion(i);
						detalleInterseccionDigitalizacionFacade.guardar(j, loginBean.getUsuario());				
					}
				}
			}
			List<String> listaAreasGuardadas = new ArrayList<String>();
			Integer tipoArea = 1;
			listaAreasGuardadas.addAll(cargarListaAreaBosques(tipoArea));
			if(areasProtegidasSeleccionadas != null && !areasProtegidasSeleccionadas.isEmpty()){
				int i = 1;
				for(String area : areasProtegidasSeleccionadas){
					if(!listaAreasGuardadas.contains(area)){
						AreasProtegidasBosques areaP = new AreasProtegidasBosques();
						areaP.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
						areaP.setNombre(area);
						areaP.setTipo(tipoArea);
						areaP.setOrden(i);
						i++;
						areasProtegidasBosquesFacade.guardar(areaP, loginBean.getUsuario());
					}
				}
			}
			// elimino las area protegidas eliminadas
			for(String area : listaAreasGuardadas){
				if(!areasProtegidasSeleccionadas.contains(area)){
					areasProtegidasBosquesFacade.eliminarIntersecciones(autorizacionAdministrativa.getId(), tipoArea, area, loginBean.getUsuario());
				}
			}
			tipoArea =  2;
			List<String> listaBosquesGuardados = new ArrayList<String>();
			listaBosquesGuardados.addAll(cargarListaAreaBosques(tipoArea));
			if(bosquesProtectoresSeleccionados != null & !bosquesProtectoresSeleccionados.isEmpty()){
				int i = 1;
				for(String bosque : bosquesProtectoresSeleccionados){
					if(!listaBosquesGuardados.contains(bosque)){
						AreasProtegidasBosques bosqueP = new AreasProtegidasBosques();
						bosqueP.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
						bosqueP.setNombre(bosque);
						bosqueP.setTipo(tipoArea);
						bosqueP.setOrden(i);
						i++;
						areasProtegidasBosquesFacade.guardar(bosqueP, loginBean.getUsuario());
					}
				}
			}
			// elimino las area protegidas eliminadas
			for(String bosque : listaBosquesGuardados){
				if(!bosquesProtectoresSeleccionados.contains(bosque)){
					areasProtegidasBosquesFacade.eliminarIntersecciones(autorizacionAdministrativa.getId(), tipoArea, bosque, loginBean.getUsuario());
				}
			}
			// guardo ubicaciones seleccionadas manualmente
			listaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 2);
			if(listaUbicacionesSeleccionadas != null && !listaUbicacionesSeleccionadas.isEmpty()){
				for(UbicacionesGeografica ubicacionSel : listaUbicacionesSeleccionadas){
					boolean existe = false;
					if(listaUbicacionTipo != null && listaUbicacionTipo.size() > 0){
						for (UbicacionDigitalizacion ubicacionDigitalizacion : listaUbicacionTipo) {
							if(ubicacionDigitalizacion.getUbicacionesGeografica().equals(ubicacionSel)){
								existe = true;
								break;
							}
						}
					}
					// si ya existe no guardo
					if(existe)
						continue;
					UbicacionDigitalizacion ubicacionD = new UbicacionDigitalizacion();
					ubicacionD.setUbicacionesGeografica(ubicacionSel);
					ubicacionD.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					ubicacionD.setTipoIngreso(2);
					ubicacionD.setPrincipal(false);
					ubicacionDigitalizacionFacade.guardar(ubicacionD, loginBean.getUsuario());
				}
			}
			// elimino las ubicaciones que no fueron seleccionadas o borradas
			listaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 2);
			if(listaUbicacionTipo != null && listaUbicacionTipo.size() > 0){
				for (UbicacionDigitalizacion ubicacionDigitalizacion : listaUbicacionTipo) {
					if(!listaUbicacionesSeleccionadas.contains(ubicacionDigitalizacion.getUbicacionesGeografica())){
						ubicacionDigitalizacion.setEstado(false);
						ubicacionDigitalizacionFacade.guardar(ubicacionDigitalizacion, loginBean.getUsuario());
					}
				}
			}
			
			if(certificadoInterseccion != null && certificadoInterseccion.getContenidoDocumento() != null && certificadoInterseccion.getId() == null){
				certificadoInterseccion.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
				certificadoInterseccion.setIdTabla(autorizacionAdministrativa.getId());
				certificadoInterseccion.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Certificado Interseccion", 1L, certificadoInterseccion, TipoDocumentoSistema.DIGITALIZACION_CERTIFICADO_INTERSECCION);
			}
			
			if(documentoMapa != null && documentoMapa.getContenidoDocumento() != null && documentoMapa.getId() == null){
				documentoMapa.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
				documentoMapa.setIdTabla(autorizacionAdministrativa.getId());
				documentoMapa.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Mapa", 1L, documentoMapa, TipoDocumentoSistema.DIGITALIZACION_MAPA);
			}
			
			if(documentoResolucion != null && documentoResolucion.getContenidoDocumento() != null && documentoResolucion.getId() == null){
				documentoResolucion.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
				documentoResolucion.setIdTabla(autorizacionAdministrativa.getId());
				documentoResolucion.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Resolucion", 1L, documentoResolucion, TipoDocumentoSistema.DIGITALIZACION_RESOLUCION);
			}
			
			if(documentoFichaAmbiental != null && documentoFichaAmbiental.getContenidoDocumento() != null && documentoFichaAmbiental.getId() == null){
				documentoFichaAmbiental.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
				documentoFichaAmbiental.setIdTabla(autorizacionAdministrativa.getId());
				documentoFichaAmbiental.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Ficha Ambiental", 1L, documentoFichaAmbiental, TipoDocumentoSistema.DIGITALIZACION_FICHA_AMBIENTAL);
			}
			
			if(documentoEstudioImpactoAmbiental != null && documentoEstudioImpactoAmbiental.getContenidoDocumento() != null && documentoEstudioImpactoAmbiental.getId() == null){
				documentoEstudioImpactoAmbiental.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
				documentoEstudioImpactoAmbiental.setIdTabla(autorizacionAdministrativa.getId());
				documentoEstudioImpactoAmbiental.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Estudio Impacto Ambiental", 1L, documentoEstudioImpactoAmbiental, TipoDocumentoSistema.DIGITALIZACION_ESTUDIO_IMPACTO_AMBIENTAL);
			}
			
			if(documentosHabilitantes != null && documentosHabilitantes.getContenidoDocumento() != null && documentosHabilitantes.getId() == null){
				documentosHabilitantes.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
				documentosHabilitantes.setIdTabla(autorizacionAdministrativa.getId());
				documentosHabilitantes.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Documentos habilitantes Propios de la Autorizacion", 1L, documentosHabilitantes, TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_HABILITANTES);
			}
			
			if(otrosDocumentos != null && otrosDocumentos.getContenidoDocumento() != null && otrosDocumentos.getId() == null){
				otrosDocumentos.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
				otrosDocumentos.setIdTabla(autorizacionAdministrativa.getId());
				otrosDocumentos.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Otros Documentos", 1L, otrosDocumentos, TipoDocumentoSistema.DIGITALIZACION_OTROS_DOCUMENTOS);
			}
			
			if(mostrarMensaje){
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}
		} catch (Exception e) {
			LOG.error("Se produjo un error al guardar la información", e);
		}
	}
	
	private void guardarHistorico(){
		boolean existenCambios = false;
		AutorizacionAdministrativaAmbiental autorizacionOrginial = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(autorizacionAdministrativa.getId());
		//resumen de proyectos
		if(!existenCambios && autorizacionAdministrativa.getResumenProyecto() != null && autorizacionOrginial.getResumenProyecto() != null){
			if (!autorizacionAdministrativa.getResumenProyecto().equals(autorizacionOrginial.getResumenProyecto())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getResumenProyecto() != null || autorizacionOrginial.getResumenProyecto() != null){
			existenCambios = true;
		}
		//tipo permiso
		if(!existenCambios && autorizacionAdministrativa.getTipoPermisoAmbiental() != null && autorizacionOrginial.getTipoPermisoAmbiental() != null){
			if (!autorizacionAdministrativa.getTipoPermisoAmbiental().equals(autorizacionOrginial.getTipoPermisoAmbiental())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getTipoPermisoAmbiental() != null || autorizacionOrginial.getTipoPermisoAmbiental() != null){
			existenCambios = true;
		}
		//autorizacion administrativa
		if(!existenCambios && autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental() != null && autorizacionOrginial.getAutorizacionAdministrativaAmbiental() != null){
			if (!autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental().equals(autorizacionOrginial.getAutorizacionAdministrativaAmbiental())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental() != null || autorizacionOrginial.getAutorizacionAdministrativaAmbiental() != null){
			existenCambios = true;
		}
		//codigo actividad catalogo
		if(!existenCambios && autorizacionAdministrativa.getCodigoActividadCatalogo() != null && autorizacionOrginial.getCodigoActividadCatalogo() != null){
			if (!autorizacionAdministrativa.getCodigoActividadCatalogo().equals(autorizacionOrginial.getCodigoActividadCatalogo())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getCodigoActividadCatalogo() != null || autorizacionOrginial.getCodigoActividadCatalogo() != null){
			existenCambios = true;
		}
		//actividad catalogo
		if(!existenCambios && autorizacionAdministrativa.getActividadCatalogo() != null && autorizacionOrginial.getActividadCatalogo() != null){
			if (!autorizacionAdministrativa.getActividadCatalogo().equals(autorizacionOrginial.getActividadCatalogo())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getActividadCatalogo() != null || autorizacionOrginial.getActividadCatalogo() != null){
			existenCambios = true;
		}
		//area emisora
		if(!existenCambios && autorizacionAdministrativa.getAreaEmisora() != null && autorizacionOrginial.getAreaEmisora() != null){
			if (!autorizacionAdministrativa.getAreaEmisora().equals(autorizacionOrginial.getAreaEmisora())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getAreaEmisora() != null || autorizacionOrginial.getAreaEmisora() != null){
			existenCambios = true;
		}
		// area direccion zona
		if(!existenCambios && autorizacionAdministrativa.getAreaDZGTecnico() != null && autorizacionOrginial.getAreaDZGTecnico() != null){
			if (!autorizacionAdministrativa.getAreaDZGTecnico().equals(autorizacionOrginial.getAreaDZGTecnico())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getAreaDZGTecnico() != null || autorizacionOrginial.getAreaDZGTecnico() != null){
			existenCambios = true;
		}
		// area de control
		if(!existenCambios && autorizacionAdministrativa.getAreaResponsableControl() != null && autorizacionOrginial.getAreaResponsableControl() != null){
			if (!autorizacionAdministrativa.getAreaResponsableControl().equals(autorizacionOrginial.getAreaResponsableControl())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getAreaResponsableControl() != null || autorizacionOrginial.getAreaResponsableControl() != null){
			existenCambios = true;
		}
		// catalogo ciiu
		if(!existenCambios && autorizacionAdministrativa.getCatalogoCIUU() != null && autorizacionOrginial.getCatalogoCIUU() != null){
			if (!autorizacionAdministrativa.getCatalogoCIUU().equals(autorizacionOrginial.getCatalogoCIUU())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getCatalogoCIUU() != null || autorizacionOrginial.getCatalogoCIUU() != null){
			existenCambios = true;
		}
		// fases 
		if(!existenCambios && autorizacionAdministrativa.getCatalogoFasesCoa() != null && autorizacionOrginial.getCatalogoFasesCoa() != null){
			if (!autorizacionAdministrativa.getCatalogoFasesCoa().equals(autorizacionOrginial.getCatalogoFasesCoa())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getCatalogoFasesCoa() != null || autorizacionOrginial.getCatalogoFasesCoa() != null){
			existenCambios = true;
		}
		// sector 
		if(!existenCambios && autorizacionAdministrativa.getTipoSector() != null && autorizacionOrginial.getTipoSector() != null){
			if (!autorizacionAdministrativa.getTipoSector().equals(autorizacionOrginial.getTipoSector())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getTipoSector() != null || autorizacionOrginial.getTipoSector() != null){
			existenCambios = true;
		}
		// 
		if(!existenCambios && autorizacionAdministrativa.getCodigoCIIUEquivalente() != null && autorizacionOrginial.getCodigoCIIUEquivalente() != null){
			if (!autorizacionAdministrativa.getCodigoCIIUEquivalente().equals(autorizacionOrginial.getCodigoCIIUEquivalente())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getCodigoCIIUEquivalente() != null || autorizacionOrginial.getCodigoCIIUEquivalente() != null){
			existenCambios = true;
		}
		if(existenCambios){
			autorizacionOrginial.setId(null);
			autorizacionOrginial.setEstado(false);
			autorizacionOrginial.setHistorico(true);
			autorizacionOrginial.setIdPadreHistorico(autorizacionAdministrativa.getId());
			autorizacionAdministrativaAmbientalFacade.save(autorizacionOrginial, loginBean.getUsuario());
		}
	}
	
	private List<String> cargarListaAreaBosques(Integer tipoId){
		List<String> listaDatos = new ArrayList<String>();
		List<AreasProtegidasBosques> objAreaBosque = areasProtegidasBosquesFacade.obtenerIntersecciones(autorizacionAdministrativa.getId(), tipoId);
		if(objAreaBosque != null && objAreaBosque.size() > 0){
			for (AreasProtegidasBosques objAreaAux : objAreaBosque) {
				listaDatos.add(objAreaAux.getNombre());
			}
		}
		return listaDatos;
	}
	
	public void guardarCoorGeografica()
	{
		if(uploadedFileGeo!=null)
		{
			DocumentoDigitalizacion documento = new DocumentoDigitalizacion();
			documento.setContenidoDocumento(uploadedFileGeo.getContents());
			documento.setExtension(".xls");
			documento.setTipoContenido("application/vnd.ms-excel");
			documento.setIdTabla(autorizacionAdministrativa.getId()); 		
			documento.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
			documento.setNombre("Coordenadas Digitalizacion(subidas).xls");
			documento.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);

			try {
				documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Coordenadas_Digitalizacion", 1L, documento, TipoDocumentoSistema.DIGITALIZACION_DOCUMENTO_COORDENADAS);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOG.error("Error al guardar documento Coordenadas", e);
			}
		}
	}
	
	public boolean validarInformacion(){
		listaMensajesErrores = new ArrayList<String>();
		if(autorizacionPrincipal == null){
			if(autorizacionAdministrativa.getNombreUsuario() == null || autorizacionAdministrativa.getNombreUsuario().equals("")){
				listaMensajesErrores.add("El campo Nombre del operador es requerido");
			}
			if(autorizacionAdministrativa.getIdentificacionUsuario() == null || autorizacionAdministrativa.getIdentificacionUsuario().equals("")){
				listaMensajesErrores.add("El campo Cédula / RUC operador es requerido");
			}
		}
		if(autorizacionAdministrativa.getResolucion() == null || autorizacionAdministrativa.getResolucion().isEmpty()){
			listaMensajesErrores.add("Debe ingresar el campo N° Resolución Administrativa Ambiental");
		}
		if(autorizacionAdministrativa.getFechaResolucion() == null || autorizacionAdministrativa.getResolucion().isEmpty()){
			listaMensajesErrores.add("Debe seleccionar la fecha de emisión de la Autorización Administrativa Ambiental");
		}
		if(autorizacionAdministrativa.getSistema() == 0 || autorizacionAdministrativa.getSistema() == 1){
			if(autorizacionAdministrativa.getFechaInicioResolucion() == null ){
				listaMensajesErrores.add("Debe seleccionar la fecha de inicio de la Autorización Administrativa Ambiental");
			}
		}
		if(autorizacionAdministrativa.getTipoSector() == null){
			listaMensajesErrores.add("El campo Sector es requerido");
		}
		
		if(autorizacionAdministrativa.getTipoSector() != null && !autorizacionAdministrativa.getTipoSector().getNombre().equals("Otros Sectores")){
			if(autorizacionAdministrativa.getCatalogoFasesCoa() == null){
				listaMensajesErrores.add("El campo Fase / Etapa es requerido");
			}
		}
		
		if(autorizacionAdministrativa.getAreaEmisora() == null){
			listaMensajesErrores.add("El campo Autoridad Ambiental emisora de la AAA es requerido");
		}
		
		if(esTecnico && !habilitarEditar){
			if(autorizacionAdministrativa.getAreaDZGTecnico() == null){
				listaMensajesErrores.add("El campo Direcciones Zonales y Galápagos es requerido");
			}
		}
		
		if(autorizacionAdministrativa.getTipoPermisoAmbiental() == null || autorizacionAdministrativa.getTipoPermisoAmbiental().equals("")){
			listaMensajesErrores.add("El campo Tipo de permiso Ambiental es requerido");
		}
		
		if(autorizacionAdministrativa.getAreaResponsableControl() == null){
			listaMensajesErrores.add("El campo Ente responsable de Control y Seguimiento es requerido");
		}
		
		if(autorizacionAdministrativa.getNombreProyecto() == null || autorizacionAdministrativa.getNombreProyecto().equals("")){
			listaMensajesErrores.add("El campo Nombre del proyecto, obra o actividad es requerido");
		}
		
		if(autorizacionAdministrativa.getResumenProyecto() == null || autorizacionAdministrativa.getResumenProyecto().equals("")){
			listaMensajesErrores.add("El campo Resumen del proyecto, obra o actividad es requerido");
		}
		
		//descomentar cuando se tenga equivalencia
		if(autorizacionAdministrativa.getCatalogoCIUU() == null){
			listaMensajesErrores.add("Seleccione el código CIIU de su actividad es requerido");
		}
		
		if(shapeDigitalizacion.getSistemaReferencia() == null || shapeDigitalizacion.getSistemaReferencia().equals("")){
			listaMensajesErrores.add("El campo Sistema de referencia es requerido");
		}
		
		if(shapeDigitalizacion.getZona() == null || shapeDigitalizacion.getZona().equals("")){
			listaMensajesErrores.add("El campo Zona es requerido");
		}
		// valido las coordenadas
		if((shapeDigitalizacion == null || shapeDigitalizacion.getId() == null) && uploadedFileGeo == null ){
			listaMensajesErrores.add("Las coordenadas es requerido");
		}
		
		if((ubicacionesSeleccionadas == null || ubicacionesSeleccionadas.size() == 0)
				&& (listaUbicacionesSeleccionadas == null || listaUbicacionesSeleccionadas.size() == 0)){
			listaMensajesErrores.add("Los campos ubicacion es requerido");
		}
		//ver los documentos
		
		//if(nuevoRegistro){
			if(certificadoInterseccion == null || certificadoInterseccion.getContenidoDocumento() == null){
				listaMensajesErrores.add("Debe adjuntar el documento Certificado de Intersección");
			}
			
			if(documentoMapa == null || documentoMapa.getContenidoDocumento() == null){
				listaMensajesErrores.add("Debe adjuntar el documento Mapa");
			}
			
			if(documentoResolucion == null || documentoResolucion.getContenidoDocumento() == null){
				listaMensajesErrores.add("Debe adjuntar el documento Resolución");
			}
			
			if(mostrarDocumentoFicha){
				if(documentoFichaAmbiental == null || documentoFichaAmbiental.getContenidoDocumento() == null){
					listaMensajesErrores.add("Debe adjuntar el documento Ficha Ambiental");
				}
			}
			
			if(mostrarDocumentoEstudio){
				if(documentoEstudioImpactoAmbiental == null || documentoEstudioImpactoAmbiental.getContenidoDocumento() == null){
					listaMensajesErrores.add("Debe adjuntar el documento Estudio de Impacto Ambiental");
				}
			}
			
			if(documentosHabilitantes == null || documentosHabilitantes.getContenidoDocumento() == null){
				listaMensajesErrores.add("Debe adjuntar le documento habilitante");
			}
		//}
		
		if(listaMensajesErrores.isEmpty()){
			return false;
		}else{
			JsfUtil.addMessageError(listaMensajesErrores);
			return true;
		}
	}
	
	public void siguiente(){
		try {
			if(!autorizacionAdministrativa.isFinalizado()){
				if(validarInformacion()){
					RequestContext.getCurrentInstance().execute("PF('siguienteDlg').hide()");
					return;
				}
			}
			deshabilitarSiguiente = true;
			guardar(false);
			if(autorizacionPrincipal == null){
				if(!habilitarEditar){
					finalizarTarea();
					autorizacionAdministrativaAmbientalBean.setEsRegistroNuevo(false);
				}
				JsfUtil.cargarObjetoSession("autorizacionAdministrativa", autorizacionAdministrativa);
				if(autorizacionAdministrativaAmbientalBean.getIniciarRGD()){
					EmisionGeneradorConAAAController rgdAAA = JsfUtil.getBean(EmisionGeneradorConAAAController.class);
					EntityFichaCompletaRgd proyectoSeleccionado = proyectoLicenciamientoAmbientalFacade.getProyectosDigitalizadoPorCodigo(loginBean.getUsuario().getNombre(), autorizacionAdministrativa.getCodigoProyecto());
					if(proyectoSeleccionado != null)
						autorizacionAdministrativaAmbientalBean.setProyectoSeleccionado(proyectoSeleccionado);
					autorizacionAdministrativaAmbientalBean.getProyectoSeleccionado().setEstadoProyecto("Completado");
					rgdAAA.setProyectoSelecionado(proyectoSeleccionado);
					rgdAAA.enviar();
				}else{
					if(habilitarEditar)
						JsfUtil.redirectTo("/pages/rcoa/digitalizacion/listadoProcesosDigitalizados.jsf");
					else
						JsfUtil.redirectTo("/pages/rcoa/digitalizacion/asociacionProyectoDigitalizacion.jsf");
				}
			}else{
				ProyectoAsociadoDigitalizacion proyectoAsociado = new ProyectoAsociadoDigitalizacion();
				List<ProyectoAsociadoDigitalizacion> listaProyectoasociado = proyectoAsociadoFacade.buscarProyectoAsociadoPorProyecto(autorizacionPrincipal.getId(), autorizacionAdministrativa.getId());
				if(listaProyectoasociado == null || listaProyectoasociado.size() == 0){
					proyectoAsociado.setAutorizacionAdministrativaAmbiental(autorizacionPrincipal);
					proyectoAsociado.setProyectoAsociadoId(autorizacionAdministrativa.getId());
				}else{
					proyectoAsociado = listaProyectoasociado.get(0);
					bandejaTareasBean.setProcessId(autorizacionAdministrativa.getIdProceso());
				}
				if(nuevoRegistro){
					proyectoAsociado.setSistemaOriginal(0);//0 nuevo
				}else{
					//para los demás sistemas
					String sistema = "";
					if(autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaSeleccionada() != null){
						sistema = autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaSeleccionada().getFuente();
					}else{
						if(autorizacionAdministrativa.getSistema() != null)
							sistema = autorizacionAdministrativa.getSistema().toString();
					}
					String sistemaOr = "";
					if(autorizacionAdministrativaAmbientalBean.getAutorizacionFisicaSeleccionada() != null){ // bdd fisico
						sistemaOr = "1";
					}
					/*else if(sistema.equals("1")){ //4 categorias
						sistemaOr = "2";
					}else if(sistema.equals("2")){ // regularizacion
						sistemaOr = "4";
					}else if(sistema.equals("5")){ //sector subsector
						sistemaOr = "3";
					}*/
					proyectoAsociado.setSistemaOriginal(Integer.valueOf(sistema));//0 nuevo
				}
				proyectoAsociado.setNombreTabla("coa_digitalization_linkage.environmental_administrative_authorizations");
				autorizacionAdministrativa.setAutorizacionAdministrativa(autorizacionPrincipal);
				// finalizo la tarea
				if(!autorizacionAdministrativa.isFinalizado()){
					if(finalizarTarea()){
						autorizacionAdministrativaAmbientalFacade.save(autorizacionAdministrativa, loginBean.getUsuario());
						proyectoAsociadoFacade.guardar(proyectoAsociado, loginBean.getUsuario());
					}
				}else{
					autorizacionAdministrativaAmbientalFacade.save(autorizacionAdministrativa, loginBean.getUsuario());
					proyectoAsociadoFacade.guardar(proyectoAsociado, loginBean.getUsuario());
				}
				// quito el proyecto asociado de la lista
				if(!nuevoRegistro)
					autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados().remove(autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaLista());
				
				if(autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados() == null || autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados().isEmpty()){
					RequestContext context = RequestContext.getCurrentInstance();
		        	context.execute("PF('mdlFinalizacion').show();");
				}else{
					autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(null);
					autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(null);
					autorizacionAdministrativaAmbientalBean.setEsRegistroNuevo(false);
					JsfUtil.cargarObjetoSession("tramite", null);
					bandejaTareasBean.setProcessId(0);
					//RequestContext context = RequestContext.getCurrentInstance();
			        //context.execute("PF('siguienteDlgDigitalizacion').show();");
			        continuarSiguiente();
				}
			}
			
		} catch (Exception e) {
			deshabilitarSiguiente = false;
			LOG.error("Error al enviar la información", e);
		}
	}
	
	private boolean finalizarTarea(){
		boolean correcto=false;
		TaskSummary tareaActual;
		try {
			if(autorizacionAdministrativa.getIdProceso() != null)
				bandejaTareasBean.setProcessId(autorizacionAdministrativa.getIdProceso());
			tareaActual = procesoFacade.getCurrenTask(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
			if(tareaActual != null && tareaActual.getId() > 0){
				Usuario usuario = usuarioFacade.buscarUsuario(tareaActual.getActualOwner().getId());
				procesoFacade.aprobarTarea(usuario, tareaActual.getId(), bandejaTareasBean.getProcessId(), null);
				autorizacionAdministrativa.setFinalizado(true);
				if(autorizacionAdministrativa.getCodigoDigitalizacion() == null || autorizacionAdministrativa.getCodigoDigitalizacion().isEmpty()){
					String idSecuencia = "DIG-", tipoAAA=autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental();
					tipoAAA = tipoAAA.replace(" - Actualización de Certificado de Intersección", "");
					switch (tipoAAA) {
					case "Certificado Ambiental":
						idSecuencia +="CA";
						break;
					case "Licencia Ambiental":
						idSecuencia +="LA";
						break;
					case "Registro Ambiental":
						idSecuencia +="RA";
						break;
					default:
						break;
					}
					idSecuencia = Constantes.SIGLAS_INSTITUCION + "-"+idSecuencia+"-"+Calendar.getInstance().get(Calendar.YEAR)+"-" + secuenciasFacade.getNextValueDedicateSequence(idSecuencia, 4);
					autorizacionAdministrativa.setCodigoDigitalizacion(idSecuencia);
				}
				autorizacionAdministrativaAmbientalFacade.save(autorizacionAdministrativa, loginBean.getUsuario());
				correcto=true;
				// inicializo en 0 porque ya finalizo la tarea
				bandejaTareasBean.setProcessId(0);
			}
		} catch (JbpmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return correcto;
	}
	
	public void continuar(){
		JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoInformacionAAA.jsf");
	}
	
	public void continuarSiguiente(){
		RequestContext context = RequestContext.getCurrentInstance();
		if(autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados().size() > 0){
        	context.execute("PF('siguienteDlgDigitalizacion').show();");
		}else{
        	context.execute("PF('siguienteDlgDigitalizacion').hide();");
		}
	}

	public void cancelarSiguiente(){
		if(autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados() != null && autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados().size() > 0){
			for(AutorizacionAdministrativa aaa : autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados()){
				autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados().remove(aaa);
				break;
			}	
		}
		if(autorizacionAdministrativaAmbientalBean.getListaProyectosSeleccionados().size() > 0){
			continuarSiguiente();
		}else{
			JsfUtil.redirectTo("/pages/rcoa/digitalizacion/asociacionProyectoDigitalizacion.jsf");
		}
	}
	
	private void iniciarProcesodigitalizacion(){
		try {
			tramite = autorizacionAdministrativa.getCodigoProyecto();
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("usuarioIngreso", JsfUtil.getLoggedUser().getNombre());
			parametros.put("tramite", tramite);
			parametros.put("fuente", "2");
			Long procesoInstanceId = procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.PROCESO_DIGITALIZACION,tramite, parametros);
			bandejaTareasBean.setProcessId(procesoInstanceId);
			autorizacionAdministrativa.setIdProceso(procesoInstanceId.intValue());
			autorizacionAdministrativaAmbientalFacade.save(autorizacionAdministrativa, loginBean.getUsuario());
		} catch (Exception e) {
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
		}
	}
	
	public void buscarUsuario() {
		if(!autorizacionAdministrativa.getIdentificacionUsuario().isEmpty()){
			Usuario operador = usuarioFacade.buscarUsuario(autorizacionAdministrativa.getIdentificacionUsuario());
			if(operador != null && operador.getId() != null){
				autorizacionAdministrativa.setNombreUsuario(operador.getPersona().getNombre());
			}else{
				Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC,
						Constantes.PASSWORD_WS_MAE_SRI_RC, autorizacionAdministrativa.getIdentificacionUsuario());
				if (cedula != null && cedula.getError().equals(Constantes.NO_ERROR_WS_MAE_SRI_RC)) {
					autorizacionAdministrativa.setNombreUsuario(cedula.getNombre());
				}else{
					ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula.obtenerPorRucSRI(
							Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, autorizacionAdministrativa.getIdentificacionUsuario());
					if (contribuyenteCompleto != null && contribuyenteCompleto.getRazonSocial() != null) {
							autorizacionAdministrativa.setNombreUsuario(contribuyenteCompleto.getRazonSocial());
					}else{
						autorizacionAdministrativa.setNombreUsuario("");
					}
				}
			}
		}
	}
	
	public void redireccionarBandeja(){
			JsfUtil.redirectToBandeja();
	}
}
