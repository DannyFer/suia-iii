package ec.gob.ambiente.rcoa.digitalizacion;

import index.ContienePoligono_entrada;
import index.ContienePoligono_resultado;
import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.json.JSONException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AutorizacionesAdministrativasFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AreasProtegidasBosquesFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.CoordenadaDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.DetalleInterseccionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.DocumentoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.HistorialActualizacionesDigitalizacionFacade;
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
import ec.gob.ambiente.rcoa.digitalizacion.model.HistorialactualizacionesDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.InterseccionProyectoDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.ShapeDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.dto.EntityLicenciaFisica;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.CatalogoFasesCoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoFasesCoa;
import ec.gob.ambiente.rcoa.facade.CatalogoCIUUFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.InterseccionProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers.EmisionGeneradorConAAAController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FormaProyecto;
import ec.gob.ambiente.suia.domain.FormaPuntoRecuperacion;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityFichaCompletaRgd;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.tipoforma.facade.TipoFormaFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class IngresoDigitalizacionProyectosController {
	
	Logger LOG = Logger.getLogger(IngresoDigitalizacionProyectosController.class);
	
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private AutorizacionesAdministrativasFacade autorizacionesAdministrativasFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private InterseccionProyectoDigitalizacionFacade interseccionProyectoDigitalizacionFacade;
	@EJB
	private DetalleInterseccionDigitalizacionFacade detalleInterseccionDigitalizacionFacade;
	@EJB
	private CatalogoFasesCoaFacade catalogoFasesCoaFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private CatalogoCIUUFacade catalogoCIUUFacade;
	@EJB
	private TipoFormaFacade tipoFormaFacade;
	@EJB
	private LicenciaAmbientalFisicaFacade licenciaAmbientalFisicaFacade;
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
	private AreasProtegidasBosquesFacade areasProtegidasBosquesFacade;
	@EJB
	private HistorialActualizacionesDigitalizacionFacade historialActualizacionesDigitalizacionFacade;
	@EJB
	private ProcesoFacade procesoFacade;;
    @EJB
    private SecuenciasFacade secuenciasFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoAsociadoDigitalizacionFacade proyectoAsociadoDigitalizacionFacade;
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private ProyectoAsociadoDigitalizacionFacade proyectoAsociadoFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorFacade;
	@EJB
	private PuntoRecuperacionRgdRcoaFacade puntoRecuperacionRgdRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	//RCOA
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalShapeFacade;
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private InterseccionProyectoLicenciaAmbientalFacade interseccionProyectoLicenciaAmbientalFacade;
	@EJB
	private DocumentosCoaFacade documentosCoaFacade;
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
	private DatosOperadorRgdBean datosOperadorRgdBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{autorizacionAdministrativaAmbientalBean}")
	private AutorizacionAdministrativaAmbientalBean autorizacionAdministrativaAmbientalBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{adicionarUbicacionesPSADBean}")
	private AdicionarUbicacionesPSADBean adicionarUbicacionesPSADBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{cargarCoordenadasWGSBean}")
	private CargarCoordenadasWGSBean cargarCoordenadasWGSBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{cargarCoordenadasDigitalizacionBean}")
	private CargarCoordenadasDigitalizacionBean cargarCoordenadasDigitalizacionBean;
	
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativa, autorizacionPrincipal;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativaAmbiental> listaAutorizacionPrincipal;
	
	@Getter
	@Setter
	private List<Area> listaAreasEmisoras, listaAreasControl;
	
	@Getter
	@Setter
	private List<CatalogoFasesCoa> listaFases;
	
	@Getter
	@Setter
	private List<TipoSector> listaSectores;
	
	@Getter
	@Setter
	private List<String> listaTipoPermisoAmbiental, listaAreasProtegidas, listaBosquesProtectores, areasProtegidasSeleccionadas, bosquesProtectoresSeleccionados;
	
	@Getter
	@Setter
	private CatalogoCIUU ciiuPrincipal= new CatalogoCIUU();
	
	@Getter
	@Setter
	private List<CatalogoCIUU> listaCatalogoCiiu = new ArrayList<CatalogoCIUU>();
    
    @Getter
    @Setter
    private DocumentoDigitalizacion documentoMapa, documentoResolucion, certificadoInterseccion, documentoFichaAmbiental;
    
    @Getter
    @Setter
    private List<DocumentoDigitalizacion> listaDocumentosHabilitantes, listaOtrosDocumentos, listaDocumentosART, listaDocumentosRSQ, listaDocumentoMapaUpdate, listaDocumentoOficioUpdate, listaDocumentosIngreso;
    
    @Getter
    @Setter
    private List<String> listaMensajesErrores, listaSistemaReferencia;
	
	@Getter
	@Setter
	private Integer idSector, idFase, idAreaEmisora, idEnteControl, tipoCoordenadasSeleccionado;
    @Getter
    @Setter
    private Usuario usuarioTecnico, usuarioCreacion;
    
    @Getter
    @Setter
	private Date fechaActual;
    
    @Getter
    @Setter
    private String nombreAreaProtegida, nombreBosqueProtector, actividadAnterior, tipoDocumentoSelected, sistemareferenciaSeleccionado, zonaSeleccionada, mensajeCI;
	
	@Getter
	@Setter
	private boolean esTecnico, esTecnicoCalidad, habilitarEditar, tieneSector, bloquearFase, tieneTipoPermiso, tieneActividad, tieneCodigoTramite,
					tienecoordenadasWGS, btnFinalizar, tieneDiferenciaInterseccion, soloLectura, nuevoRegistro, existeProyectosEnLista, mostrarMensaje, tieneVinculados;

	@Getter
	@Setter
	private String tipoCategoriaProyecto;
	
	@Getter
	@Setter
	private Boolean mostrarOtrasCoordenadas;

	private String tramite;
	private Map<String, Object> variables;

	private static final String COORDENADASWGS = "WGS84";
	private static final String COORDENADASPSAD = "PSAD56";
	private static final Integer INGRESOAUTOMATICO = 1;
	private static final Integer INGRESOMANUAL = 2;
	private static final Integer INGRESOCARGAMANUAL = 3;
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
	@PostConstruct
	public void init(){
		try {
			tieneVinculados=false;
			soloLectura=false;
			tieneDiferenciaInterseccion=false;
			btnFinalizar=false;
			habilitarEditar=false;
			mostrarMensaje=false;
			tienecoordenadasWGS = false;
			mostrarOtrasCoordenadas = null;
			esTecnico = true;
			fechaActual = new Date();
			usuarioTecnico = new Usuario();
			usuarioCreacion = new Usuario();
			esTecnicoCalidad = tieneTipoPermiso = tieneActividad = tieneCodigoTramite = false;
			tipoCategoriaProyecto="";
			//autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
			documentoMapa = new DocumentoDigitalizacion();
			documentoResolucion = new DocumentoDigitalizacion();
			certificadoInterseccion = new DocumentoDigitalizacion();
			documentoFichaAmbiental = new DocumentoDigitalizacion();
			listaOtrosDocumentos = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentosHabilitantes = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentoMapaUpdate = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentoOficioUpdate = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentosART = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentosRSQ = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentosIngreso = new ArrayList<DocumentoDigitalizacion>();
			areasProtegidasSeleccionadas = new ArrayList<String>();
			bosquesProtectoresSeleccionados = new ArrayList<String>();
	        FacesContext ctx = FacesContext.getCurrentInstance();
	        String urls = ctx.getViewRoot().getViewId();
	        if(urls.contains("Ver")){
				soloLectura=true;
	        }
			// cargo informacion de catalogos
			listaAreasProtegidas = autorizacionAdministrativaAmbientalFacade.getAreasProtegidas();
			listaBosquesProtectores = autorizacionAdministrativaAmbientalFacade.getBosquesProtectores();
			cargarFases(true);
			cargarsectores();
			llenarListaTipoPermiso();
			cargarEntesResponsable();
			cargarSistemasReferencia();
			listaCatalogoCiiu=catalogoCIUUFacade.listaCatalogoCiuu();
			for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
				if (areaUser.getArea().getAreaName().contains("PROPONENTE")) {
					datosOperadorRgdBean.buscarDatosOperador(JsfUtil.getLoggedUser());
					esTecnico = false;
					break;
				}
			}
			if(esTecnico){
				String rol="role.resolucion.tecnico.responsable";
				String rolTecnicoCalidad = Constantes.getRoleAreaName(rol);
				esTecnicoCalidad= JsfUtil.getLoggedUser().isUserInRole(JsfUtil.getLoggedUser(), rolTecnicoCalidad);
			}
			//cargo informacion del proyecto seleccionado
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	    	if (request.getParameter("codigo") != null){
	    		tramite = request.getParameter("codigo").replace("?", "");
				soloLectura=true;
	    	}
			if(tramite == null){
				variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
				tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);	
			}
			if(tramite == null){
				tramite =(String)(JsfUtil.devolverObjetoSession("tramite"));
			}
			AutorizacionAdministrativa licenciaSuia = new AutorizacionAdministrativa();
			if(tramite != null){
				autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(tramite);
				if(autorizacionAdministrativa != null){
					if(!autorizacionAdministrativa.getUsuarioCreacion().isEmpty())
						usuarioCreacion  = usuarioFacade.buscarUsuario(autorizacionAdministrativa.getUsuarioCreacion());
					switch (autorizacionAdministrativa.getSistema().toString()) {
					case "0": // nuevo
						break;
					case "1":	//BDD_FISICO

						break;
					case "2":	//BDD_CUATRO_CATEGORIAS

						break;
					case "3":	//BDD_SECTOR_SUBSECTOR

						break;
					case "4":	//REGULARIZACIÓN
						licenciaSuia = cargarAAA();
						licenciaSuia.setFuente("2");
						//cargarInformacionSuia(licenciaSuia);
						break;
					case "5":	//RCOA

						break;
					default:
						break;
					}
				}else{
					licenciaSuia = autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaSeleccionada();
					if(licenciaSuia != null){
						cargarInformacionSuia(licenciaSuia);
					}
				}
			}
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
			EntityLicenciaFisica licenciaFisica = new EntityLicenciaFisica();
			
			if(!nuevoRegistro){
				licenciaFisica = autorizacionAdministrativaAmbientalBean.getAutorizacionFisicaSeleccionada();
				licenciaSuia = autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaSeleccionada();
			}else{
				licenciaFisica = null;
				licenciaSuia = null;
			}

			if(autorizacionAdministrativa == null){
				autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
				if(licenciaFisica != null){
					cargaInformacionProyectosFisicos(licenciaFisica);
				}
				if(licenciaSuia != null){
					cargarInformacionSuia(licenciaSuia);
				}
				if(licenciaFisica == null && licenciaSuia == null){
				}
			}else{
				cargarInformacionDigitalizacion();
				actividadAnterior = autorizacionAdministrativa.getActividadCatalogo();
			}
			if(!esTecnico){
				autorizacionAdministrativa.setIdentificacionUsuario(loginBean.getUsuario().getNombre());
				buscarUsuario();
			}
			if(nuevoRegistro){
				autorizacionAdministrativa.setSistema(0);
			}
			JsfUtil.cargarObjetoSession("nuevoRegistro", null);
			JsfUtil.cargarObjetoSession("licenciaSuia", null);
			JsfUtil.cargarObjetoSession("licenciaFisica", null);
			if(esTecnicoCalidad){
				usuarioTecnico = usuarioFacade.buscarUsuario(loginBean.getNombreUsuario());
				habilitarEditar=true;
				//habilito para que pueda modificar las coordenadas 
				tienecoordenadasWGS=false;
			}
			//verifico si tiene proyectos vinculados
			if(autorizacionAdministrativa.getId() != null){
				List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados = proyectoAsociadoDigitalizacionFacade.buscarProyectosAsociados(autorizacionAdministrativa.getId());
				tieneVinculados = !(listaProyectosAsociados == null || listaProyectosAsociados.size() == 0);
			}
		} catch (Exception e) {
			LOG.error("Error al recuperar la información", e);
		}
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

	private void llenarListaTipoPermiso(){
		listaTipoPermisoAmbiental = new ArrayList<String>();
		listaTipoPermisoAmbiental.add("Certificado Ambiental");
		listaTipoPermisoAmbiental.add("Registro Ambiental");
		listaTipoPermisoAmbiental.add("Licencia Ambiental");
	}

	private void cargarSistemasReferencia(){
		listaSistemaReferencia = new ArrayList<String>();
		listaSistemaReferencia.add(COORDENADASPSAD);
		listaSistemaReferencia.add(COORDENADASWGS);
	}
	
	private void cargarsectores(){
		listaSectores = new ArrayList<TipoSector>();
		for (TipoSector tipoSector : proyectoLicenciamientoAmbientalFacade.getTiposSectores()) {
			if(tipoSector.getId() < 5){
				listaSectores.add(tipoSector);
			}
		}
	}

	public void seleccionarDocumento(String documentoTipo){
	    tipoDocumentoSelected = documentoTipo;
		listaDocumentosIngreso = new ArrayList<DocumentoDigitalizacion>();
		String tipoHabilitantes = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.habilitantes" , true);
		String tipoOtrosDocumentos = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.otros" , true);
		String tipoDocumentosART = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.art" , true);
		String tipoDocumentosRSQ = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.rsq" , true);
		String tipoDocumentosOficio = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.actualizacion.oficio" , true);
		String tipoDocumentosMapa = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.actualizacion.mapa" , true);
		if(documentoTipo.equals(tipoHabilitantes)){
			if(listaDocumentosHabilitantes != null && listaDocumentosHabilitantes.size() > 0){
				listaDocumentosIngreso.addAll(listaDocumentosHabilitantes);	
			}
		}else if(documentoTipo.equals(tipoOtrosDocumentos)){
			if(listaOtrosDocumentos != null && listaOtrosDocumentos.size() > 0){
				listaDocumentosIngreso.addAll(listaOtrosDocumentos);	
			}
		}else if(documentoTipo.equals(tipoDocumentosART)){
			if(listaDocumentosART != null && listaDocumentosART.size() > 0){
				listaDocumentosIngreso.addAll(listaDocumentosART);	
			}
		}else if(documentoTipo.equals(tipoDocumentosRSQ)){
			if(listaDocumentosRSQ != null && listaDocumentosRSQ.size() > 0){
				listaDocumentosIngreso.addAll(listaDocumentosRSQ);	
			}
		}else if(documentoTipo.equals(tipoDocumentosOficio)){
			if(listaDocumentoOficioUpdate != null && listaDocumentoOficioUpdate.size() > 0){
				listaDocumentosIngreso.addAll(listaDocumentoOficioUpdate);	
			}
		}else if(documentoTipo.equals(tipoDocumentosMapa)){
			if(listaDocumentoMapaUpdate != null && listaDocumentoMapaUpdate.size() > 0){
				listaDocumentosIngreso.addAll(listaDocumentoMapaUpdate);	
			}
		}
	}
	
	private EntityLicenciaFisica cargarObjetoFisico(Integer id){
		return licenciaAmbientalFisicaFacade.buscarProyectoFisicoPorId(id);
	}
	
	private AutorizacionAdministrativa cargarAAA(){
		AutorizacionAdministrativa objAAA = new AutorizacionAdministrativa();
		objAAA.setCategoria(autorizacionAdministrativa.getTipoPermisoAmbiental());
		objAAA.setIdDigitalizacion(autorizacionAdministrativa.getId());
		objAAA.setIdProceso(autorizacionAdministrativa.getIdProceso());
		objAAA.setId(autorizacionAdministrativa.getIdProyecto());
		objAAA.setCodigo(autorizacionAdministrativa.getCodigoProyecto());
		objAAA.setNombre(autorizacionAdministrativa.getNombreProyecto());
		objAAA.setNombreProponente(autorizacionAdministrativa.getNombreUsuario());
		objAAA.setCedulaProponente(autorizacionAdministrativa.getIdentificacionUsuario());
		objAAA.setResolucion(autorizacionAdministrativa.getResolucion());
		return objAAA;
	}

	private void cargaInformacionProyectosFisicos(EntityLicenciaFisica licenciaFisica){
		try {
			cargarCoordenadasWGSBean.setAreasProtegidasWgs(new ArrayList<String>());
			cargarCoordenadasWGSBean.setBosquesProtectoresWgs(new ArrayList<String>());
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
					cargarCoordenadasWGSBean.getBosquesProtectoresWgs().add(licenciaFisica.getIntersectaDescripcion());
				}else{
					cargarCoordenadasWGSBean.getAreasProtegidasWgs().add(licenciaFisica.getIntersectaDescripcion());
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
					//cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().add(coorP);
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
					cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().add(coorP);
					ShapeDigitalizacion objShape = new ShapeDigitalizacion();
					objShape.setTipoForma(forma.getTipoForma());
					objShape.setSistemaReferencia("WGS84");
					objShape.setZona((tipoCoordenada == null ? "17S": tipoCoordenada));
					objShape.setTipoIngreso(1);
					cargarCoordenadasWGSBean.setShapeDigitalizacion(objShape);
					if(listaCoordenadasTrans != null && listaCoordenadasTrans.size() > 0)
						tienecoordenadasWGS = true;
				}
				cargarCoordenadasWGSBean.setUbicacionesSeleccionadas(proyecto.getUbicacionesGeograficas());
				List<InterseccionProyecto> listaIntersecciones = autorizacionAdministrativaAmbientalFacade.listaIntersecciones(proyecto);
				//Descarga de documentos
				List<Documento> documentosCerticado = documentosFacade.documentosTodosXTablaIdXIdDoc(proyecto.getId(), "ProyectoLicenciamientoAmbiental", TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_OFICIO);
				if(documentosCerticado != null && !documentosCerticado.isEmpty()){
					certificadoInterseccion = new DocumentoDigitalizacion();
					byte[] contenido=null;
					try{
						contenido = documentosFacade.descargar(documentosCerticado.get(0).getIdAlfresco(), documentosCerticado.get(0).getFechaCreacion());
						if(contenido != null){
							certificadoInterseccion = crearDocumentoSuia(contenido, documentosCerticado.get(0));
						}
					}catch(Exception e){
						
					}
				}
				List<Documento> documentosMapa = documentosFacade.documentosTodosXTablaIdXIdDoc(proyecto.getId(), "ProyectoLicenciamientoAmbiental", TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA);
				if(documentosMapa != null && !documentosMapa.isEmpty()){
					byte[] contenido = null;
					try{
						contenido= documentosFacade.descargar(documentosMapa.get(0).getIdAlfresco(), documentosMapa.get(0).getFechaCreacion());
						if(contenido != null){
							documentoMapa = crearDocumentoSuia(contenido, documentosMapa.get(0));
						}
					}catch(Exception e){
						
					}
				}
				
				//Documento resolucion
				if(autorizacionSistema.getCategoria().equals("Licencia Ambiental")){
					List<Documento> documentosResolucion = documentosFacade.documentosTodosXTablaIdXIdDoc(proyecto.getId(), "InformeTecnicoGeneralLA", TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA);
					if(documentosResolucion != null && !documentosResolucion.isEmpty()){
						byte[] contenido = null;
						try{
							contenido = documentosFacade.descargar(documentosResolucion.get(0).getIdAlfresco(), documentosResolucion.get(0).getFechaCreacion());
							if(contenido != null){
								documentoResolucion = crearDocumentoSuia(contenido, documentosResolucion.get(0));
							}
						}catch(Exception e){
							
						}
					}
				}else if(autorizacionSistema.getCategoria().equals("Registro Ambiental")){
					List<Documento> documentosResolucion = documentosFacade.documentosTodosXTablaIdXIdDoc(proyecto.getId(), "CategoriaIILicencia", TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL);
					if(documentosResolucion != null && !documentosResolucion.isEmpty()){
						byte[] contenido = null;
						try{
							contenido = documentosFacade.descargar(documentosResolucion.get(0).getIdAlfresco(), documentosResolucion.get(0).getFechaCreacion());
							if(contenido != null){
								documentoResolucion = crearDocumentoSuia(contenido, documentosResolucion.get(0));
							}
						}catch(Exception e){
							
						}
					}
				}else if(autorizacionSistema.getCategoria().equals("Certificado Ambiental")){
					List<Documento> documentosResolucion = documentosFacade.documentosTodosXTablaIdXIdDoc(proyecto.getId(), "ProyectoLicenciamientoAmbiental", TipoDocumentoSistema.TIPO_CERTIFICADO_CATEGORIA_UNO);
					if(documentosResolucion != null && !documentosResolucion.isEmpty()){
						byte[] contenido = null;
						try{
							contenido = documentosFacade.descargar(documentosResolucion.get(0).getIdAlfresco(), documentosResolucion.get(0).getFechaCreacion());
							if(contenido != null){
								documentoResolucion = crearDocumentoSuia(contenido, documentosResolucion.get(0));
							}
						}catch(Exception e){
							
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
						cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().add(coorP);
						ShapeDigitalizacion objShape = new ShapeDigitalizacion();
						objShape.setTipoForma(tipo);
						objShape.setSistemaReferencia(COORDENADASWGS);
						objShape.setZona("17S");
						objShape.setTipoIngreso(1);
						cargarCoordenadasWGSBean.setShapeDigitalizacion(objShape);
					}
				}
				cargarCoordenadasWGSBean.setUbicacionesSeleccionadas(new ArrayList<UbicacionesGeografica>());
				List<String> lista = licenciaAmbientalFisicaFacade.busquedaUbicacion4Cat(autorizacionSistema.getCodigo(), autorizacionAdministrativa.getSistema().toString());
				for(String inec : lista){
					UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarUbicacionPorCodigoInec(inec);
					if(ubicacion != null && ubicacion.getId() != null){
						cargarCoordenadasWGSBean.getUbicacionesSeleccionadas().add(ubicacion);
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
						cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().add(coorP);
						ShapeDigitalizacion objShape = new ShapeDigitalizacion();
						objShape.setTipoForma(tipo);
						objShape.setSistemaReferencia(COORDENADASWGS);
						objShape.setZona("17S");
						objShape.setTipoIngreso(INGRESOAUTOMATICO);
						cargarCoordenadasWGSBean.setShapeDigitalizacion(objShape);
					}
				}
				cargarCoordenadasWGSBean.setUbicacionesSeleccionadas(new ArrayList<UbicacionesGeografica>());
				List<String> lista = licenciaAmbientalFisicaFacade.busquedaUbicacion4Cat(autorizacionSistema.getCodigo(), autorizacionAdministrativa.getSistema().toString());
				for(String inec : lista){
					UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarUbicacionPorCodigoInec(inec);
					if(ubicacion != null && ubicacion.getId() != null){
						cargarCoordenadasWGSBean.getUbicacionesSeleccionadas().add(ubicacion);
					}
				}
			}//fin sector subsector
			else if(autorizacionSistema.getFuente().equals("7")){
				//RCOA
				ProyectoLicenciaCoa proyectoRCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
				
				autorizacionAdministrativa.setSistema(5);
				autorizacionAdministrativa.setNombreProyecto(proyectoRCoa.getNombreProyecto());
				autorizacionAdministrativa.setResumenProyecto(proyectoRCoa.getDescripcionProyecto());
				autorizacionAdministrativa.setUsuario(proyectoRCoa.getUsuario());
				autorizacionAdministrativa.setNombreUsuario(proyectoRCoa.getUsuario().getPersona().getNombre());
				autorizacionAdministrativa.setIdentificacionUsuario(proyectoRCoa.getUsuario().getNombre());
				autorizacionAdministrativa.setCodigoProyecto(proyectoRCoa.getCodigoUnicoAmbiental());
				autorizacionAdministrativa.setFechaRegistro(proyectoRCoa.getFechaCreacion());
				
				TipoSector tipoSector = new TipoSector();
				//busco el sector en base a la actividad principal
				// verifico si la actividad tiene paga o no
				ProyectoLicenciaCuaCiuu  actividadCiu = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoRCoa);
				if(actividadCiu != null && actividadCiu.getId() != null){
					autorizacionAdministrativa.setCatalogoCIUU(actividadCiu.getCatalogoCIUU());
					if(actividadCiu.getCatalogoCIUU().getTipoSector().getNombre().equals("Hidrocarburos")){
						tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(1);
						autorizacionAdministrativa.setTipoSector(tipoSector);
					}else if(actividadCiu.getCatalogoCIUU().getTipoSector().getNombre().equals("Minería")){
						tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(2);
						autorizacionAdministrativa.setTipoSector(tipoSector);
					}else if(actividadCiu.getCatalogoCIUU().getTipoSector().getNombre().equals("Eléctrico")){
						tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(4);
						autorizacionAdministrativa.setTipoSector(tipoSector);
					}else{
						tipoSector = proyectoLicenciamientoAmbientalFacade.getTipoSector(3);
						autorizacionAdministrativa.setTipoSector(tipoSector);
					}
				}
				idSector = autorizacionAdministrativa.getTipoSector().getId();
				
				if(listaAreasEmisoras.contains(proyectoRCoa.getAreaResponsable())){
					autorizacionAdministrativa.setAreaEmisora(proyectoRCoa.getAreaResponsable());
					idAreaEmisora = proyectoRCoa.getAreaResponsable().getId();
				}
				if(listaAreasControl.contains(proyectoRCoa.getAreaResponsable())){
					autorizacionAdministrativa.setAreaResponsableControl(proyectoRCoa.getAreaResponsable());
					idEnteControl = proyectoRCoa.getAreaResponsable().getId();
				}
				autorizacionAdministrativa.setTipoPermisoAmbiental(autorizacionSistema.getCategoria());
				autorizacionAdministrativa.setId(autorizacionSistema.getIdDigitalizacion());
				autorizacionAdministrativa.setIdProceso(autorizacionSistema.getIdProceso());
				autorizacionAdministrativa.setIdProyecto(autorizacionSistema.getId());
				
				autorizacionAdministrativa.setAutorizacionAdministrativaAmbiental(autorizacionSistema.getCategoria());
				tieneTipoPermiso = true;

				List<CoordenadaDigitalizacion> listaCoordenadasTrans = new ArrayList<CoordenadaDigitalizacion>();
				List<ProyectoLicenciaAmbientalCoaShape> formasImplantacion = proyectoLicenciaAmbientalShapeFacade.buscarFormaGeograficaPorProyecto(proyectoRCoa, 1, 0); //coordenadas implantacion
				
				if(formasImplantacion == null){
					formasImplantacion = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();				
				}else{
					for(ProyectoLicenciaAmbientalCoaShape forma : formasImplantacion){
						List<CoordenadasProyecto> coordenadasGeograficasImplantacion = coordenadasProyectoCoaFacade.buscarPorForma(forma);

						String tipoCoordenada = null;
						listaCoordenadasTrans = new ArrayList<CoordenadaDigitalizacion>();
						for(CoordenadasProyecto objCoordenada : coordenadasGeograficasImplantacion){
							CoordenadaDigitalizacion coorDig = new CoordenadaDigitalizacion();
							coorDig.setX(objCoordenada.getX());
							coorDig.setY(objCoordenada.getY());
							coorDig.setOrden(objCoordenada.getOrdenCoordenada());
							//tipoCoordenada = objCoordenada.getZona();
							listaCoordenadasTrans.add(coorDig);
						}
						CoordenadasPoligonos coorP = new CoordenadasPoligonos();
						coorP.setCoordenadas(listaCoordenadasTrans);
						coorP.setTipoForma(forma.getTipoForma());
						cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().add(coorP);
						ShapeDigitalizacion objShape = new ShapeDigitalizacion();
						objShape.setTipoForma(forma.getTipoForma());
						objShape.setSistemaReferencia("WGS84");
						objShape.setZona((tipoCoordenada == null ? "17S": tipoCoordenada));
						objShape.setTipoIngreso(1);
						cargarCoordenadasWGSBean.setShapeDigitalizacion(objShape);
						if(listaCoordenadasTrans != null && listaCoordenadasTrans.size() > 0)
							tienecoordenadasWGS = true;
					}				
				}

				cargarCoordenadasWGSBean.setUbicacionesSeleccionadas(proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoRCoa));
				List<InterseccionProyectoLicenciaAmbiental> intersecciones = interseccionProyectoLicenciaAmbientalFacade.intersecan(proyectoRCoa);

				//Descarga de documentos
				CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyectoRCoa.getCodigoUnicoAmbiental());
				if(oficioCI  != null) {
					List<DocumentosCOA> certificadoInter = documentosCoaFacade.documentoXTablaIdXIdDoc(oficioCI.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO, "CertificadoInterseccionOficioCoa");

					if(certificadoInter != null && !certificadoInter.isEmpty()){
						certificadoInterseccion = new DocumentoDigitalizacion();
						byte[] contenido=null;
						try{
							contenido = documentosFacade.descargar(certificadoInter.get(0).getIdAlfresco(), certificadoInter.get(0).getFechaCreacion());
							if(contenido != null){
								certificadoInterseccion = crearDocumentoRcoa(contenido, certificadoInter.get(0));
							}
						}catch(Exception e){
							
						}
					}
				}
				List<DocumentosCOA> documentosMapa = documentosCoaFacade.documentoXTablaIdXIdDoc(proyectoRCoa.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA, "ProyectoLicenciaCoa");
				if(documentosMapa != null && !documentosMapa.isEmpty()){
					byte[] contenido=null;
					try{
						contenido = documentosFacade.descargar(documentosMapa.get(0).getIdAlfresco(), documentosMapa.get(0).getFechaCreacion());
						if(contenido != null){
							documentoMapa = crearDocumentoRcoa(contenido, documentosMapa.get(0));
						}
					}catch(Exception e){
						
					}
				}
			}
			if(autorizacionAdministrativa.getCodigoProyecto() != null){
				tieneCodigoTramite = true;
			}
			autorizacionAdministrativa.setFechaRegistro(fechaRegistro);
			autorizacionAdministrativa.setFechaFinalizacionRegistro(fechaFinalizacion);
		} catch (Exception e) {
			LOG.error("Error al cargar la informacion de Regularización", e);
		}
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
		//si no tenia catalogo del anterior sistema
		if(autorizacionAdministrativa.getActividadCatalogo() == null && autorizacionAdministrativa.getCatalogoCIUU() != null)
			tieneActividad = false;
		obtenerTipoProyecto();
		//cargo catalogo ciiuu
		if(autorizacionAdministrativa.getCatalogoCIUU() != null)
			ciiuPrincipal = autorizacionAdministrativa.getCatalogoCIUU();
		// cargar coordenadas WGS
		cargarCoordenadasWGSBean.setAutorizacionAdministrativa(autorizacionAdministrativa);
		cargarCoordenadasWGSBean.cargarCoordenadasWgs();
		cargarCoordenadasWGSBean.getAreasProtegidasWgs().addAll(cargarListaAreaBosques(1, 3));//1=areas protegidas, 3= ingresadas mediante carga manual de archivo 
		cargarCoordenadasWGSBean.getBosquesProtectoresWgs().addAll(cargarListaAreaBosques(2, 3));//2=bosques , 3= ingresadas mediante carga manual de archivo
		if(cargarCoordenadasWGSBean.getShapeDigitalizacion() != null && cargarCoordenadasWGSBean.getShapeDigitalizacion().getTipoIngreso() != null && (cargarCoordenadasWGSBean.getShapeDigitalizacion().getTipoIngreso().equals(1) || cargarCoordenadasWGSBean.getShapeDigitalizacion().getTipoIngreso().equals(2))){
			// si son coordenadas cargadas del sistema original no se puede modificar
			if(cargarCoordenadasWGSBean.getShapeDigitalizacion().getTipoIngreso().equals(1) && cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(0).getCoordenadas().size() > 0)
				tienecoordenadasWGS = true;
			if(cargarCoordenadasWGSBean.getShapeDigitalizacion().getSistemaReferencia() != null)
				sistemareferenciaSeleccionado = cargarCoordenadasWGSBean.getShapeDigitalizacion().getSistemaReferencia();
			if(cargarCoordenadasWGSBean.getShapeDigitalizacion().getZona() != null)
				zonaSeleccionada = cargarCoordenadasWGSBean.getShapeDigitalizacion().getZona();
		}
		// cargar coordenadas diferentes a WGS zona 17S
		cargarCoordenadasDigitalizacionBean.setAutorizacionAdministrativa(autorizacionAdministrativa);
		cargarCoordenadasDigitalizacionBean.cargarCoordenadasOtroSistema();
		if(cargarCoordenadasDigitalizacionBean.getShapeDigitalizacion() != null && cargarCoordenadasDigitalizacionBean.getShapeDigitalizacion().getId() != null){
			if(cargarCoordenadasDigitalizacionBean.getShapeDigitalizacion().getSistemaReferencia() != null)
				sistemareferenciaSeleccionado=cargarCoordenadasDigitalizacionBean.getShapeDigitalizacion().getSistemaReferencia();
			zonaSeleccionada=cargarCoordenadasDigitalizacionBean.getShapeDigitalizacion().getZona();
			if(cargarCoordenadasDigitalizacionBean.getShapeDigitalizacion().getTipoForma() != null)
				tipoCoordenadasSeleccionado= cargarCoordenadasDigitalizacionBean.getShapeDigitalizacion().getTipoForma().getId();
			//cargo las ubicaciones 
			List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 2);
			adicionarUbicacionesPSADBean.setUbicacionesSeleccionadas(new ArrayList<UbicacionesGeografica>());
			if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
				for (UbicacionDigitalizacion ubicacionDigitalizacion : ListaUbicacionTipo) {
					adicionarUbicacionesPSADBean.getUbicacionesSeleccionadas().add(ubicacionDigitalizacion.getUbicacionesGeografica());
				}
			}
		}
		obtenerSistemaReferenciaActual();
		cargarDocumentosDigitalizacion();
		if(autorizacionAdministrativa.getCodigoProyecto() != null){
			tieneCodigoTramite = true;
		}
		// cargar intersecciones areas Protegidas
		areasProtegidasSeleccionadas = new ArrayList<String>();
		areasProtegidasSeleccionadas.addAll(cargarListaAreaBosques(1, 2));
		bosquesProtectoresSeleccionados = new ArrayList<String>();
		bosquesProtectoresSeleccionados.addAll(cargarListaAreaBosques(2, 2));
		tieneDiferenciaInterseccion=compararInterseccion();
	}
	
	private void obtenerTipoProyecto(){
		tipoCategoriaProyecto="";
		if(autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental() != null){
			switch (autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental()) {
			case "Certificado Ambiental":
				tipoCategoriaProyecto="I";
				break;
			case "Registro Ambiental":
				tipoCategoriaProyecto="II";
				break;
			case "Licencia Ambiental":
				tipoCategoriaProyecto="III";
				break;

			default:
				break;
			}
		}
	}
	
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
	
	public void obtenerSistemaReferenciaActual(){
		mostrarOtrasCoordenadas = null;
		if(sistemareferenciaSeleccionado != null && sistemareferenciaSeleccionado.equals("PSAD56"))
			zonaSeleccionada = null;
		cargarCoordenadasDigitalizacionBean.setSistemareferenciaSeleccionado(sistemareferenciaSeleccionado);
		cargarCoordenadasDigitalizacionBean.setZonaSeleccionada(zonaSeleccionada);
		if(sistemareferenciaSeleccionado == null || sistemareferenciaSeleccionado.isEmpty() || (sistemareferenciaSeleccionado.equals(COORDENADASWGS) && zonaSeleccionada == null))
			return;
		if(sistemareferenciaSeleccionado.equals(COORDENADASPSAD) || (sistemareferenciaSeleccionado.equals(COORDENADASWGS) && zonaSeleccionada != null && !zonaSeleccionada.equals("17S"))){
			mostrarOtrasCoordenadas = true;
		}else
			mostrarOtrasCoordenadas = false;
	}
	
	private List<String> cargarListaAreaBosques(Integer tipoId, Integer tipoIngreso){
		List<String> listaDatos = new ArrayList<String>();
		List<AreasProtegidasBosques> objAreaBosque = areasProtegidasBosquesFacade.obtenerInterseccionesPorIngreso(autorizacionAdministrativa.getId(), tipoId, tipoIngreso);
		if(objAreaBosque != null && objAreaBosque.size() > 0){
			for (AreasProtegidasBosques objAreaAux : objAreaBosque) {
				listaDatos.add(objAreaAux.getNombre());
			}
		}
		return listaDatos;
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
				certificadoInterseccion.setSubido(true);
			}
			List<DocumentoDigitalizacion> documentosMapa = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_MAPA);
			if(documentosMapa != null && !documentosMapa.isEmpty()){
				byte[] contenido = documentoDigitalizacionFacade.descargar(documentosMapa.get(0).getIdAlfresco());
				if(contenido != null){
					documentoMapa = documentosMapa.get(0);
					documentoMapa.setContenidoDocumento(documentosMapa.get(0).getContenidoDocumento());
				}
				documentoMapa.setSubido(true);
			}
			//Documento resolucion
			List<DocumentoDigitalizacion> documentosResolucion = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_RESOLUCION);
			if(documentosResolucion != null && !documentosResolucion.isEmpty()){
				byte[] contenido = documentoDigitalizacionFacade.descargar(documentosResolucion.get(0).getIdAlfresco());
				if(contenido != null){
					documentoResolucion = documentosResolucion.get(0);
					documentoResolucion.setContenidoDocumento(contenido);
				}
				documentoResolucion.setSubido(true);
			}
			//Documento ficha ambiental
			List<DocumentoDigitalizacion> documentosFichaA = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_FICHA_AMBIENTAL);
			if(documentosFichaA != null && !documentosFichaA.isEmpty()){
				byte[] contenido = documentoDigitalizacionFacade.descargar(documentosFichaA.get(0).getIdAlfresco());
				if(contenido != null){
					documentoFichaAmbiental = documentosFichaA.get(0);
					documentoFichaAmbiental.setContenidoDocumento(contenido);
				}
				documentoFichaAmbiental.setSubido(true);
			}
			//Documento resolucion
			listaDocumentosHabilitantes = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_HABILITANTES);
			if(listaDocumentosHabilitantes != null && !listaDocumentosHabilitantes.isEmpty()){
				for (DocumentoDigitalizacion documentoDigitalizacion : listaDocumentosHabilitantes) {
					byte[] contenido = documentoDigitalizacionFacade.descargar(documentoDigitalizacion.getIdAlfresco());
					if(contenido != null){
						documentoDigitalizacion.setContenidoDocumento(contenido);
					}
					documentoDigitalizacion.setSubido(true);
				}
			}
			//Otros Documentos
			listaOtrosDocumentos = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_OTROS_DOCUMENTOS);
			if(listaOtrosDocumentos != null && !listaOtrosDocumentos.isEmpty()){
				for (DocumentoDigitalizacion documentoDigitalizacion : listaOtrosDocumentos) {
					byte[] contenido = documentoDigitalizacionFacade.descargar(documentoDigitalizacion.getIdAlfresco());
					if(contenido != null){
						documentoDigitalizacion.setContenidoDocumento(contenido);
					}
					documentoDigitalizacion.setSubido(true);
				}
			}
			//Documentos ART
			listaDocumentosART = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_ART);
			if(listaDocumentosART != null && !listaDocumentosART.isEmpty()){
				for (DocumentoDigitalizacion documentoDigitalizacion : listaDocumentosART) {
					byte[] contenido = documentoDigitalizacionFacade.descargar(documentoDigitalizacion.getIdAlfresco());
					if(contenido != null){
						documentoDigitalizacion.setContenidoDocumento(contenido);
					}
					documentoDigitalizacion.setSubido(true);
				}
			}
			//Documentos RSQ
			listaDocumentosRSQ = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_RSQ);
			if(listaDocumentosRSQ != null && !listaDocumentosRSQ.isEmpty()){
				for (DocumentoDigitalizacion documentoDigitalizacion : listaDocumentosRSQ) {
					byte[] contenido = documentoDigitalizacionFacade.descargar(documentoDigitalizacion.getIdAlfresco());
					if(contenido != null){
						documentoDigitalizacion.setContenidoDocumento(contenido);
					}
					documentoDigitalizacion.setSubido(true);
				}
			}
			//Documentos oficio actualizacion
			listaDocumentoOficioUpdate = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_OFICIO_UPDATE);
			if(listaDocumentoOficioUpdate != null && !listaDocumentoOficioUpdate.isEmpty()){
				for (DocumentoDigitalizacion documentoDigitalizacion : listaDocumentoOficioUpdate) {
					byte[] contenido = documentoDigitalizacionFacade.descargar(documentoDigitalizacion.getIdAlfresco());
					if(contenido != null){
						documentoDigitalizacion.setContenidoDocumento(contenido);
					}
					documentoDigitalizacion.setSubido(true);
				}
			}
			//Documentos mapa actualizacion
			listaDocumentoMapaUpdate = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionAdministrativa.getId(), AutorizacionAdministrativaAmbiental.class.getSimpleName(), TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_MAPA_UPDATE);
			if(listaDocumentoMapaUpdate != null && !listaDocumentoMapaUpdate.isEmpty()){
				for (DocumentoDigitalizacion documentoDigitalizacion : listaDocumentoMapaUpdate) {
					byte[] contenido = documentoDigitalizacionFacade.descargar(documentoDigitalizacion.getIdAlfresco());
					if(contenido != null){
						documentoDigitalizacion.setContenidoDocumento(contenido);
					}
					documentoDigitalizacion.setSubido(true);
				}
			}
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		}
	}
	
	private DocumentoDigitalizacion crearDocumento4Cat(byte[] byteDocumento, String[] datosDocumentos){
		DocumentoDigitalizacion documentoAux = new DocumentoDigitalizacion();
		documentoAux.setContenidoDocumento(byteDocumento);
		documentoAux.setNombre(datosDocumentos[2]+"."+datosDocumentos[3]);
		documentoAux.setExtension((datosDocumentos[3].startsWith(".")?datosDocumentos[3]:"."+datosDocumentos[3]));
		documentoAux.setMime(datosDocumentos[4]);
		documentoAux.setTipoIngreso(INGRESOAUTOMATICO);
		return documentoAux;
	}
	
	private DocumentoDigitalizacion crearDocumentoSuia(byte[] byteDocumento, Documento documentoSuia){
		DocumentoDigitalizacion documentoAux = new DocumentoDigitalizacion();
		documentoAux.setContenidoDocumento(byteDocumento);
		documentoAux.setNombre(documentoSuia.getNombre());
		documentoAux.setExtension(documentoSuia.getExtesion());
		documentoAux.setMime(documentoSuia.getMime());
		documentoAux.setTipoIngreso(INGRESOAUTOMATICO);
		return documentoAux;
	}
	
	private DocumentoDigitalizacion crearDocumentoRcoa(byte[] byteDocumento, DocumentosCOA documentoRcoa){
		DocumentoDigitalizacion documentoAux = new DocumentoDigitalizacion();
		documentoAux.setContenidoDocumento(byteDocumento);
		documentoAux.setNombre(documentoRcoa.getNombreDocumento());
		documentoAux.setExtension(documentoRcoa.getExtencionDocumento());
		documentoAux.setMime(documentoRcoa.getTipo());
		documentoAux.setTipoIngreso(INGRESOAUTOMATICO);
		return documentoAux;
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
	
	private void cargarEntesResponsable(){
		listaAreasEmisoras = areaFacade.getAreasEmisorasAAA();
		listaAreasControl = areaFacade.getAreasSeguimientoAAA();
	}

	public void ciiu1(CatalogoCIUU catalogo){
		ciiuPrincipal = catalogo;
		autorizacionAdministrativa.setCatalogoCIUU(ciiuPrincipal);
	}
	
	public void limpiarCamposActividad(){
		ciiuPrincipal = new CatalogoCIUU();
		autorizacionAdministrativa.setCatalogoCIUU(null);
	}

	public void handleFileUpload(final FileUploadEvent event) {
		if(tipoCoordenadasSeleccionado == null){
			JsfUtil.addMessageError("El tipo de coordenadas es requerido");
			return;
		}
		cargarCoordenadasWGSBean.handleFileUploadWGSAux(event, tipoCoordenadasSeleccionado);
		tieneDiferenciaInterseccion=compararInterseccion();
	}

	public void handleFileUploadOtroSistema(final FileUploadEvent event) {
		if(tipoCoordenadasSeleccionado == null){
			JsfUtil.addMessageError("El tipo de coordenadas es requerido");
			return;
		}
		cargarCoordenadasDigitalizacionBean.handleFileUpload(event, tipoCoordenadasSeleccionado);
		tieneDiferenciaInterseccion=compararInterseccion();
	}
	
	public void cambioTipoCoordenada(){
		cargarCoordenadasDigitalizacionBean.setCoordinatesWrappersGeo(new ArrayList<CoordenadasPoligonos>());
		cargarCoordenadasDigitalizacionBean.setCoordenadasGeograficas(new ArrayList<CoordenadaDigitalizacion>());
	}
	
	public void guardar(){
		try {
			Wizard wizard = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:wizardDigitalizacion");
			String currentStep = wizard.getStep();
			if (currentStep == null || currentStep.equals("paso1")) {
				guardarPaso1();
			} else if (currentStep.equals("paso2")) {
				guardarPaso2();
			} else if (currentStep.equals("paso3")) {
				guardarPaso3();
			}
		} catch (Exception ae) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public boolean finalizarTarea(){
		guardarPaso3();
		if(validarInformacionPaso3()){
			return false;
		}
		if(autorizacionPrincipal == null){
			//variable para saber si es una actualizacion o un ingreso
			boolean esActualizacion = true;
			if(!autorizacionAdministrativa.isFinalizado() && autorizacionAdministrativa.getUsuarioCreacion().equals(loginBean.getUsuario().getNombre())){
				finalizarTareaBpm();
				autorizacionAdministrativaAmbientalBean.setEsRegistroNuevo(false);
				esActualizacion = false;
			}
			JsfUtil.cargarObjetoSession("autorizacionAdministrativa", autorizacionAdministrativa);
			if(autorizacionAdministrativaAmbientalBean.getIniciarRGD() != null && autorizacionAdministrativaAmbientalBean.getIniciarRGD()){
				EmisionGeneradorConAAAController rgdAAA = JsfUtil.getBean(EmisionGeneradorConAAAController.class);
				EntityFichaCompletaRgd proyectoSeleccionado = proyectoLicenciamientoAmbientalFacade.getProyectosDigitalizadoPorCodigo(loginBean.getUsuario().getNombre(), autorizacionAdministrativa.getCodigoProyecto());
				if(proyectoSeleccionado != null)
					autorizacionAdministrativaAmbientalBean.setProyectoSeleccionado(proyectoSeleccionado);
				autorizacionAdministrativaAmbientalBean.getProyectoSeleccionado().setEstadoProyecto("Completado");
				rgdAAA.setProyectoSelecionado(proyectoSeleccionado);
				rgdAAA.enviar();
			}else{
				if(!esActualizacion)
					JsfUtil.redirectTo("/pages/rcoa/digitalizacion/asociacionProyectoDigitalizacion.jsf");
				else if(habilitarEditar)
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
				proyectoAsociado.setSistemaOriginal(Integer.valueOf(sistema));//0 nuevo
			}
			proyectoAsociado.setNombreTabla("coa_digitalization_linkage.environmental_administrative_authorizations");
			autorizacionAdministrativa.setAutorizacionAdministrativa(autorizacionPrincipal);
			// finalizo la tarea
			if(!autorizacionAdministrativa.isFinalizado()){
				if(finalizarTareaBpm()){
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
		return true;
	}

	public void continuar(){
		JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacion.jsf");
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
	
	private boolean finalizarTareaBpm(){
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
				//finalizo la tarea pendiente del proyecto en el anterior sistema
				autorizacionAdministrativaAmbientalFacade.completarTareasPendientes(tramite, autorizacionAdministrativa.getSistema().toString(), autorizacionAdministrativa.getId());
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
	
	public String onFlowProcess(FlowEvent event) throws JSONException {
		Wizard wizard = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:wizardDigitalizacion");
		String currentStep = wizard.getStep();
		btnFinalizar=false;
		if(currentStep != null){
			if(currentStep.equals("paso1")){
				if(event.getNewStep().equals("paso2")) {
					if(!guardarPaso1()){
						return "paso1";
					}
				}
			}else if(currentStep.equals("paso2")){
				if(event.getNewStep().equals("paso3")) {
//					if(validarInformacionPaso2()){
//						return "paso2";
//					}
					guardarPaso2();
					if(autorizacionAdministrativa.getTieneCertificado() == null && (listaDocumentoOficioUpdate == null || listaDocumentoOficioUpdate.size() == 0) && (listaDocumentoMapaUpdate == null || listaDocumentoMapaUpdate.size() == 0)){
						mostrarMensaje=true;
					}
					btnFinalizar=true;
				}
			}
			
		}
		RequestContext.getCurrentInstance().update("btnFinalizar");
		RequestContext.getCurrentInstance().update("pnlButtons");

		return event.getNewStep();
	}
	
	private void guardarHistorico(){
		boolean existenCambios = false;
		AutorizacionAdministrativaAmbiental autorizacionOrginial = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(autorizacionAdministrativa.getId());
		// guarde historico si ya fue finalizado
		if(!autorizacionAdministrativa.isFinalizado())
			return;
		//numero de resolucion
		if(!existenCambios && autorizacionAdministrativa.getResolucion() != null && autorizacionOrginial.getResolucion() != null){
			if (!autorizacionAdministrativa.getResolucion().equals(autorizacionOrginial.getResolucion())){
				existenCambios = true;
				// guarde que se realiza cambio en la resolucion solo si ya fue finalizado
				if(autorizacionAdministrativa.isFinalizado())
					autorizacionAdministrativa.setActualizacionResolucion(true);
			}
		}else if(!existenCambios && autorizacionAdministrativa.getResolucion() != null || autorizacionOrginial.getResolucion() != null){
			existenCambios = true;
			// guarde que se realiza cambio en la resolucion solo si ya fue finalizado
			if(autorizacionAdministrativa.isFinalizado())
			autorizacionAdministrativa.setActualizacionResolucion(true);
		}
		//fecha de emision resolucion
		if(!existenCambios && autorizacionAdministrativa.getFechaResolucion() != null && autorizacionOrginial.getFechaResolucion() != null){
			if (!autorizacionAdministrativa.getFechaResolucion().equals(autorizacionOrginial.getFechaResolucion())){
				existenCambios = true;
				// guarde que se realiza cambio en la resolucion solo si ya fue finalizado
				if(autorizacionAdministrativa.isFinalizado())
				autorizacionAdministrativa.setActualizacionResolucion(true);
			}
		}else if(!existenCambios && autorizacionAdministrativa.getFechaResolucion() != null || autorizacionOrginial.getFechaResolucion() != null){
			existenCambios = true;
			// guarde que se realiza cambio en la resolucion solo si ya fue finalizado
			if(autorizacionAdministrativa.isFinalizado())
			autorizacionAdministrativa.setActualizacionResolucion(true);
		}
		//fecha de inicio del proceso de regularizacion
		if(!existenCambios && autorizacionAdministrativa.getFechaInicioResolucion() != null && autorizacionOrginial.getFechaInicioResolucion() != null){
			if (!autorizacionAdministrativa.getFechaInicioResolucion().equals(autorizacionOrginial.getFechaInicioResolucion())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getFechaInicioResolucion() != null || autorizacionOrginial.getFechaInicioResolucion() != null){
			existenCambios = true;
		}
		//nombre de proyectos
		if(!existenCambios && autorizacionAdministrativa.getNombreProyecto() != null && autorizacionOrginial.getNombreProyecto() != null){
			if (!autorizacionAdministrativa.getNombreProyecto().equals(autorizacionOrginial.getNombreProyecto())){
				existenCambios = true;
			}
		}else if(!existenCambios && autorizacionAdministrativa.getNombreProyecto() != null || autorizacionOrginial.getNombreProyecto() != null){
			existenCambios = true;
		}
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
			guardarProcesoActualizacion();
		}
	}
	
	private boolean guardarPaso1(){
		try{
			if(soloLectura)
				return true;
			if(validarInformacionPaso1()){
				return false;
			}
			String mensajeError = validarIngresoResolucion();
			if(!mensajeError.isEmpty()){
				JsfUtil.addMessageError(mensajeError);
				return false;
			}
			if(autorizacionAdministrativa.getCodigoProyecto() == null ){
				crearNumeroTramite();
			}
			autorizacionAdministrativa.setResolucion(autorizacionAdministrativa.getResolucion().toUpperCase());
			if(autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaSeleccionada() != null && autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaSeleccionada().getEstado() != null && autorizacionAdministrativaAmbientalBean.getAutorizacionAdministrativaSeleccionada().getEstado().equals("Actualización de Certificado de Intersección")){
				autorizacionAdministrativa.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental()+" - Actualización de Certificado de Intersección");
			}
			if(autorizacionAdministrativa.getIdentificacionUsuario() != null && !autorizacionAdministrativa.getIdentificacionUsuario().isEmpty()
					&& !autorizacionAdministrativa.getIdentificacionUsuario().equals("")){
				Usuario operador = usuarioFacade.buscarUsuario(autorizacionAdministrativa.getIdentificacionUsuario());
				if(operador != null && operador.getId() != null){
					autorizacionAdministrativa.setUsuario(operador);
				}
			}
			// si es tecnico de calidad guardo el historico del que estuvo antes de guardar los cambios
			if(habilitarEditar)
				guardarHistorico();
			autorizacionAdministrativaAmbientalFacade.save(autorizacionAdministrativa, loginBean.getUsuario());
			obtenerTipoProyecto();
			//guardo coordenadas traidas del proyecto original
			if(cargarCoordenadasWGSBean.getShapeDigitalizacion().getId() == null)
			{
				String sitemaRef = "", zonaref="";
				if(cargarCoordenadasWGSBean.getShapeDigitalizacion()==null)
					cargarCoordenadasWGSBean.setShapeDigitalizacion(new ShapeDigitalizacion());
				for (int i = 0; i <= cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().size() - 1; i++) {
					//valido que el shape tenga coordenadas
					if(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas() == null || cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().size() == 0)
						continue;
					ShapeDigitalizacion shape = new ShapeDigitalizacion();
					shape = cargarCoordenadasWGSBean.getShapeDigitalizacion();
					shape.setId(null);
					shape.setTipoForma(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getTipoForma());
					shape.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					shape.setNumeroActualizaciones(0);
					shape.setSuperficie(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getSuperficie());
					shape.setTipo(2);//2=coordenadas geograficas 1=coordenadas implantacion
					sistemareferenciaSeleccionado = sitemaRef = shape.getSistemaReferencia();
					zonaSeleccionada = zonaref = shape.getZona();
					cargarCoordenadasWGSBean.setShapeDigitalizacion(shapeDigitalizacionFacade.guardar(shape, loginBean.getUsuario()));
					CoordenadaDigitalizacion coor = new CoordenadaDigitalizacion();
					for (int j = 0; j <=cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().size()-1; j++) {
						coor= new CoordenadaDigitalizacion();
						coor.setShapeDigitalizacion(shape);
						coor.setOrden(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getOrden());
						coor.setX(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getX());
						coor.setY(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getY());
						coor.setAreaGeografica(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getAreaGeografica());
						coor.setTipo(2);//2=coordenadas geograficas 1=coordenadas implantacion
						coor.setNumeroActualizacion(0);
						coordenadaDigitalizacionFacade.guardar(coor, loginBean.getUsuario());
					}
				}
				cargarCoordenadasWGSBean.setUploadedFileGeo(null);

				// guardo ubicaciones generadas de las coordenadas 
				List<UbicacionDigitalizacion> listaUbicacionWgs = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), 2, sitemaRef, zonaref);
				if(cargarCoordenadasWGSBean.getUbicacionesSeleccionadas() != null && !cargarCoordenadasWGSBean.getUbicacionesSeleccionadas().isEmpty()){
					for(UbicacionesGeografica ubicacionSel : cargarCoordenadasWGSBean.getUbicacionesSeleccionadas()){
						boolean existe = false;
						if(listaUbicacionWgs != null && listaUbicacionWgs.size() > 0){
							for (UbicacionDigitalizacion ubicacionDigitalizacion : listaUbicacionWgs) {
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
						ubicacionD.setTipoIngreso(INGRESOAUTOMATICO);// se ingresa por medio de las coordenadas
						ubicacionD.setPrincipal(false);
						ubicacionD.setSistemaReferencia(sitemaRef);
						ubicacionD.setZona(zonaref);
						ubicacionDigitalizacionFacade.guardar(ubicacionD, loginBean.getUsuario());
					}
				}
				// elimino las ubicaciones guardadas anteriormente
				if(listaUbicacionWgs != null && listaUbicacionWgs.size() > 0){
					for (UbicacionDigitalizacion ubicacionDigitalizacion : listaUbicacionWgs) {
						if(!cargarCoordenadasWGSBean.getUbicacionesSeleccionadas().contains(ubicacionDigitalizacion.getUbicacionesGeografica())){
							ubicacionDigitalizacion.setEstado(false);
							ubicacionDigitalizacionFacade.guardar(ubicacionDigitalizacion, loginBean.getUsuario());
						}
					}
				}

				//eliminar para actualizar
				interseccionProyectoDigitalizacionFacade.eliminarInterseccion(autorizacionAdministrativa.getId(), 2, JsfUtil.getLoggedUser().getNombre());
				//guardar capas de interseccion y detalle de interseccion
				for (InterseccionProyectoDigitalizacion objInterseccion : cargarCoordenadasWGSBean.getListaCapasInterseccionPrincipal().keySet()) {
					objInterseccion.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					objInterseccion.setFechaProceso(new Date());
					objInterseccion.setTipoIngreso(INGRESOAUTOMATICO);// ingreso automatico por sistema 
					objInterseccion=interseccionProyectoDigitalizacionFacade.guardar(objInterseccion, loginBean.getUsuario());
					for(DetalleInterseccionDigitalizacion j : cargarCoordenadasWGSBean.getCapasIntersecciones().get(objInterseccion))
					{
						j.setInterseccionProyectoDigitalizacion(objInterseccion);
						detalleInterseccionDigitalizacionFacade.guardar(j, loginBean.getUsuario());				
					}
				}
				if(cargarCoordenadasWGSBean.getAreasProtegidasWgs() != null && cargarCoordenadasWGSBean.getAreasProtegidasWgs().size() > 0){
					int i = 1;
					for(String area : cargarCoordenadasWGSBean.getAreasProtegidasWgs()){
						AreasProtegidasBosques areaP = new AreasProtegidasBosques();
						areaP.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
						areaP.setNombre(area);
						areaP.setTipo(1); // 1 areas protegidas
						areaP.setTipoIngreso(INGRESOAUTOMATICO);// ingreso automatico por sistema 
						areaP.setOrden(i);
						i++;
						areasProtegidasBosquesFacade.guardar(areaP, loginBean.getUsuario());
					}
				}
				if(cargarCoordenadasWGSBean.getBosquesProtectoresWgs() != null && cargarCoordenadasWGSBean.getBosquesProtectoresWgs().size() > 0){
					int i = 1;
					for(String area : cargarCoordenadasWGSBean.getAreasProtegidasWgs()){
						AreasProtegidasBosques areaP = new AreasProtegidasBosques();
						areaP.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
						areaP.setNombre(area);
						areaP.setTipo(2); // 1 BOSQUES PROTECTORES
						areaP.setTipoIngreso(INGRESOAUTOMATICO);// ingreso automatico por sistema 
						areaP.setOrden(i);
						i++;
						areasProtegidasBosquesFacade.guardar(areaP, loginBean.getUsuario());
					}
				}
				obtenerSistemaReferenciaActual();
			}
			//guardo documentos traidos del proyecto original
			guardarDocumentos();
			if(autorizacionAdministrativa.getIdProceso() == null && !autorizacionAdministrativa.isFinalizado() && autorizacionAdministrativa.getUsuarioCreacion().equals(loginBean.getUsuario().getNombre())){
				iniciarProcesodigitalizacion();
			}
			if(!soloLectura)
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void guardarDocumentos(){
		try{
			if(certificadoInterseccion != null && certificadoInterseccion.getContenidoDocumento() != null && !certificadoInterseccion.isSubido()){
				byte[] byteContenido = certificadoInterseccion.getContenidoDocumento();
				certificadoInterseccion.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
				certificadoInterseccion.setIdTabla(autorizacionAdministrativa.getId());
				certificadoInterseccion.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				certificadoInterseccion = documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Certificado Interseccion", 1L, certificadoInterseccion, TipoDocumentoSistema.DIGITALIZACION_CERTIFICADO_INTERSECCION);
				certificadoInterseccion.setSubido(true);
				certificadoInterseccion.setContenidoDocumento(byteContenido);
				guardarProcesoActualizacion();
			}
			
			if(documentoMapa != null && documentoMapa.getContenidoDocumento() != null && !documentoMapa.isSubido()){
				byte[] byteContenido = documentoMapa.getContenidoDocumento();
				documentoMapa.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
				documentoMapa.setIdTabla(autorizacionAdministrativa.getId());
				documentoMapa.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				documentoMapa = documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Mapa", 1L, documentoMapa, TipoDocumentoSistema.DIGITALIZACION_MAPA);
				documentoMapa.setSubido(true);
				documentoMapa.setContenidoDocumento(byteContenido);
				guardarProcesoActualizacion();
			}
			
			if(documentoResolucion != null && documentoResolucion.getContenidoDocumento() != null && !documentoResolucion.isSubido()){
				byte[] byteContenido = documentoResolucion.getContenidoDocumento();
				documentoResolucion.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
				documentoResolucion.setIdTabla(autorizacionAdministrativa.getId());
				documentoResolucion.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				documentoResolucion = documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Resolucion", 1L, documentoResolucion, TipoDocumentoSistema.DIGITALIZACION_RESOLUCION);
				documentoResolucion.setSubido(true);
				documentoResolucion.setContenidoDocumento(byteContenido);
				guardarProcesoActualizacion();
			}
			
			if(documentoFichaAmbiental != null && documentoFichaAmbiental.getContenidoDocumento() != null && !documentoFichaAmbiental.isSubido()){
				byte[] byteContenido = documentoFichaAmbiental.getContenidoDocumento();
				documentoFichaAmbiental.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
				documentoFichaAmbiental.setIdTabla(autorizacionAdministrativa.getId());
				documentoFichaAmbiental.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				documentoFichaAmbiental = documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Ficha Ambiental", 1L, documentoFichaAmbiental, TipoDocumentoSistema.DIGITALIZACION_FICHA_AMBIENTAL);
				documentoFichaAmbiental.setSubido(true);
				documentoFichaAmbiental.setContenidoDocumento(byteContenido);
				guardarProcesoActualizacion();
			}
			
			for (DocumentoDigitalizacion objDocumento : listaDocumentosHabilitantes) {
				if(objDocumento != null && objDocumento.getContenidoDocumento() != null && !objDocumento.isSubido()){
					objDocumento.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
					objDocumento.setIdTabla(autorizacionAdministrativa.getId());
					objDocumento.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Documentos Habilitantes", 1L, objDocumento, TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_HABILITANTES);
					objDocumento.setSubido(true);
					guardarProcesoActualizacion();
				}
			}
			
			for (DocumentoDigitalizacion objDocumento : listaDocumentosART) {
				if(objDocumento != null && objDocumento.getContenidoDocumento() != null && !objDocumento.isSubido()){
					objDocumento.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
					objDocumento.setIdTabla(autorizacionAdministrativa.getId());
					objDocumento.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Documentos ART", 1L, objDocumento, TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_ART);
					objDocumento.setSubido(true);
					guardarProcesoActualizacion();
				}
			}
			
			for (DocumentoDigitalizacion objDocumento : listaDocumentosRSQ) {
				if(objDocumento != null && objDocumento.getContenidoDocumento() != null && !objDocumento.isSubido()){
					objDocumento.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
					objDocumento.setIdTabla(autorizacionAdministrativa.getId());
					objDocumento.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Documentos RSQ", 1L, objDocumento, TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_RSQ);
					objDocumento.setSubido(true);
					guardarProcesoActualizacion();
				}
			}
			
			for (DocumentoDigitalizacion objDocumento : listaOtrosDocumentos) {
				if(objDocumento != null && objDocumento.getContenidoDocumento() != null && !objDocumento.isSubido()){
					objDocumento.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
					objDocumento.setIdTabla(autorizacionAdministrativa.getId());
					objDocumento.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Otros Documentos", 1L, objDocumento, TipoDocumentoSistema.DIGITALIZACION_OTROS_DOCUMENTOS);
					objDocumento.setSubido(true);
					guardarProcesoActualizacion();
				}
			}
			
			for (DocumentoDigitalizacion objDocumento : listaDocumentoOficioUpdate) {
				if(objDocumento != null && objDocumento.getContenidoDocumento() != null && !objDocumento.isSubido()){
					objDocumento.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
					objDocumento.setIdTabla(autorizacionAdministrativa.getId());
					objDocumento.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Oficio actualizacion", 1L, objDocumento, TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_OFICIO_UPDATE);
					objDocumento.setSubido(true);
					guardarProcesoActualizacion();
				}
			}
			
			for (DocumentoDigitalizacion objDocumento : listaDocumentoMapaUpdate) {
				if(objDocumento != null && objDocumento.getContenidoDocumento() != null && !objDocumento.isSubido()){
					objDocumento.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
					objDocumento.setIdTabla(autorizacionAdministrativa.getId());
					objDocumento.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Mapa actualizacion", 1L, objDocumento, TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_MAPA_UPDATE);
					objDocumento.setSubido(true);
					guardarProcesoActualizacion();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean guardarPaso2(){
		try{
			if(soloLectura)
				return true;
//			if(mostrarOtrasCoordenadas == null){
//				JsfUtil.addMessageError("El sistema de referencia es requerido.");
//				return false;
//			}
			if(!tieneDiferenciaInterseccion){
				autorizacionAdministrativa.setJustificacionInterseccion(null);
			}
			//si es actualizacion y hay cambios en coordenadas guardo para saber que hubo cambio de coordenadas
			if(habilitarEditar && autorizacionAdministrativa.isFinalizado() && (cargarCoordenadasWGSBean.getUploadedFileGeo() !=null || cargarCoordenadasWGSBean.getShapeDigitalizacion().getId() == null))
			{
				autorizacionAdministrativa.setActualizacionCoordenadas(true);
				autorizacionAdministrativa.setEstadoActualizacion(true);
			}
			autorizacionAdministrativaAmbientalFacade.save(autorizacionAdministrativa, loginBean.getUsuario());
			String zonaWgs="", sistemareferecianW="";
			if(cargarCoordenadasWGSBean.getShapeDigitalizacion() != null){
				if(cargarCoordenadasWGSBean.getShapeDigitalizacion().getSistemaReferencia() != null)
					sistemareferecianW=cargarCoordenadasWGSBean.getShapeDigitalizacion().getSistemaReferencia();
				if(cargarCoordenadasWGSBean.getShapeDigitalizacion().getZona() != null)
					zonaWgs=cargarCoordenadasWGSBean.getShapeDigitalizacion().getZona();
			}
			if(cargarCoordenadasWGSBean.getUploadedFileGeo() !=null || cargarCoordenadasWGSBean.getShapeDigitalizacion().getId() == null)
			{
				guardarFileCoorGeografica();
				shapeDigitalizacionFacade.eliminarShapePorSistema(autorizacionAdministrativa.getId(), 2, true, loginBean.getUsuario());
				if(cargarCoordenadasWGSBean.getShapeDigitalizacion()==null)
					cargarCoordenadasWGSBean.setShapeDigitalizacion(new ShapeDigitalizacion());
				for (int i = 0; i <= cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().size() - 1; i++) {
					//valido que el shape tenga coordenadas
					if(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas() == null || cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().size() == 0)
						continue;
					ShapeDigitalizacion shape = new ShapeDigitalizacion();
					shape = cargarCoordenadasWGSBean.getShapeDigitalizacion();
					shape.setId(null);
					shape.setTipoForma(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getTipoForma());
					shape.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					shape.setNumeroActualizaciones(0);
					shape.setTipo(2);//2=coordenadas geograficas 1=coordenadas implantacion
					shape.setSuperficie(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getSuperficie());
					cargarCoordenadasWGSBean.setShapeDigitalizacion(shapeDigitalizacionFacade.guardar(shape, loginBean.getUsuario()));
					CoordenadaDigitalizacion coor = new CoordenadaDigitalizacion();
					for (int j = 0; j <=cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().size()-1; j++) {
						coor= new CoordenadaDigitalizacion();
						coor.setShapeDigitalizacion(shape);
						coor.setOrden(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getOrden());
						coor.setX(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getX());
						coor.setY(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getY());
						coor.setAreaGeografica(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getAreaGeografica());
						coor.setTipo(2);//2=coordenadas geograficas 1=coordenadas implantacion
						coor.setNumeroActualizacion(0);
						coordenadaDigitalizacionFacade.guardar(coor, loginBean.getUsuario());
					}
				}
				cargarCoordenadasWGSBean.setUploadedFileGeo(null);

				// guardo ubicaciones generadas de las coordenadas 
				List<UbicacionDigitalizacion> listaUbicacionWgs = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), 3, sistemareferecianW, zonaWgs);
				if(cargarCoordenadasWGSBean.getUbicacionesSeleccionadas() != null && !cargarCoordenadasWGSBean.getUbicacionesSeleccionadas().isEmpty()){
					for(UbicacionesGeografica ubicacionSel : cargarCoordenadasWGSBean.getUbicacionesSeleccionadas()){
						boolean existe = false;
						if(listaUbicacionWgs != null && listaUbicacionWgs.size() > 0){
							for (UbicacionDigitalizacion ubicacionDigitalizacion : listaUbicacionWgs) {
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
						ubicacionD.setTipoIngreso(INGRESOCARGAMANUAL);// se ingresa por medio de las coordenadas cargadas manualmente
						ubicacionD.setPrincipal(false);
						ubicacionD.setSistemaReferencia(sistemareferecianW);
						ubicacionD.setZona(zonaWgs);
						ubicacionDigitalizacionFacade.guardar(ubicacionD, loginBean.getUsuario());
					}
				}
				// elimino las ubicaciones guardadas anteriormente
				if(listaUbicacionWgs != null && listaUbicacionWgs.size() > 0){
					for (UbicacionDigitalizacion ubicacionDigitalizacion : listaUbicacionWgs) {
						if(!cargarCoordenadasWGSBean.getUbicacionesSeleccionadas().contains(ubicacionDigitalizacion.getUbicacionesGeografica())){
							ubicacionDigitalizacion.setEstado(false);
							ubicacionDigitalizacionFacade.guardar(ubicacionDigitalizacion, loginBean.getUsuario());
						}
					}
				}
				//guardar capas de interseccion y detalle de interseccion
				//eliminar para actualizar
				interseccionProyectoDigitalizacionFacade.eliminarInterseccion(autorizacionAdministrativa.getId(), INGRESOCARGAMANUAL, JsfUtil.getLoggedUser().getNombre());
				//guardar capas de interseccion y detalle de interseccion
				for (InterseccionProyectoDigitalizacion objInterseccion : cargarCoordenadasWGSBean.getListaCapasInterseccionPrincipal().keySet()) {
					objInterseccion.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
					objInterseccion.setFechaProceso(new Date());
					objInterseccion.setTipoIngreso(INGRESOCARGAMANUAL);// se ingresa por medio de las coordenadas cargadas manualmente
					objInterseccion=interseccionProyectoDigitalizacionFacade.guardar(objInterseccion, loginBean.getUsuario());
					for(DetalleInterseccionDigitalizacion j : cargarCoordenadasWGSBean.getCapasIntersecciones().get(objInterseccion))
					{
						j.setInterseccionProyectoDigitalizacion(objInterseccion);
						detalleInterseccionDigitalizacionFacade.guardar(j, loginBean.getUsuario());				
					}
				}
				guardarProcesoActualizacion();
			}
			if(cargarCoordenadasWGSBean.getAreasProtegidasWgs() != null && cargarCoordenadasWGSBean.getAreasProtegidasWgs().size() > 0){
				Integer tipoArea = 1;//areas protegidas
				List<String> listaAreasGuardadas = new ArrayList<String>();
				listaAreasGuardadas.addAll(cargarListaAreaBosques(tipoArea, 3));
				int i = 1;
				for(String area : cargarCoordenadasWGSBean.getAreasProtegidasWgs()){
					if(!listaAreasGuardadas.contains(area)){
						AreasProtegidasBosques areaP = new AreasProtegidasBosques();
						areaP.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
						areaP.setNombre(area);
						areaP.setTipo(tipoArea); // 1 areas protegidas
						areaP.setTipoIngreso(INGRESOCARGAMANUAL);// por medio de las coordenadas cargadas manualmente
						areaP.setOrden(i);
						i++;
						areasProtegidasBosquesFacade.guardar(areaP, loginBean.getUsuario());
						guardarProcesoActualizacion();
					}
				}
				// elimino las area protegidas eliminadas
				for(String area : listaAreasGuardadas){
					if(!cargarCoordenadasWGSBean.getAreasProtegidasWgs().contains(area)){
						areasProtegidasBosquesFacade.eliminarIntersecciones(autorizacionAdministrativa.getId(), tipoArea, area, loginBean.getUsuario());
						guardarProcesoActualizacion();
					}
				}
			}
			if(cargarCoordenadasWGSBean.getBosquesProtectoresWgs() != null && cargarCoordenadasWGSBean.getBosquesProtectoresWgs().size() > 0){
				Integer tipoArea = 2;// BOSQUES PROTECTORE
				List<String> listaAreasGuardadas = new ArrayList<String>();
				listaAreasGuardadas.addAll(cargarListaAreaBosques(tipoArea, 3));
				int i = 1;
				for(String area : cargarCoordenadasWGSBean.getBosquesProtectoresWgs()){
					if(!listaAreasGuardadas.contains(area)){
						AreasProtegidasBosques areaP = new AreasProtegidasBosques();
						areaP.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
						areaP.setNombre(area);
						areaP.setTipo(tipoArea); // 2 BOSQUES PROTECTORES
						areaP.setTipoIngreso(INGRESOCARGAMANUAL);// por medio de las coordenadas cargadas manualmente
						areaP.setOrden(i);
						i++;
						areasProtegidasBosquesFacade.guardar(areaP, loginBean.getUsuario());
						guardarProcesoActualizacion();
					}
				}
				// elimino las area protegidas eliminadas
				for(String area : listaAreasGuardadas){
					if(!cargarCoordenadasWGSBean.getBosquesProtectoresWgs().contains(area)){
						areasProtegidasBosquesFacade.eliminarIntersecciones(autorizacionAdministrativa.getId(), tipoArea, area, loginBean.getUsuario());
						guardarProcesoActualizacion();
					}
				}
			}
			
			/*********************************************************
			 * coordenadas diferentes de WGS84 zona 17S
			 ********************************************************/
			if(mostrarOtrasCoordenadas != null && mostrarOtrasCoordenadas){
				String zonaPsad=zonaSeleccionada, sistemareferecianPsad=sistemareferenciaSeleccionado;

				if(cargarCoordenadasDigitalizacionBean.getUploadedFileGeo() !=null || cargarCoordenadasDigitalizacionBean.getShapeDigitalizacion().getId() == null)
				{
					guardarFileCoorGeografica();
					shapeDigitalizacionFacade.eliminarShapePorSistema(autorizacionAdministrativa.getId(), 2, false, loginBean.getUsuario());
					if(cargarCoordenadasDigitalizacionBean.getShapeDigitalizacion()==null)
						cargarCoordenadasDigitalizacionBean.setShapeDigitalizacion(new ShapeDigitalizacion());
					for (int i = 0; i <= cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo().size() - 1; i++) {
						//valido que el shape tenga coordenadas
						if(cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo().get(i).getCoordenadas() == null || cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().size() == 0)
							continue;
						ShapeDigitalizacion shape = new ShapeDigitalizacion();
						shape = cargarCoordenadasDigitalizacionBean.getShapeDigitalizacion();
						shape.setId(null);
						shape.setTipoForma(cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo().get(i).getTipoForma());
						shape.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
						shape.setNumeroActualizaciones(0);
						shape.setSistemaReferencia(sistemareferenciaSeleccionado);
						shape.setZona(zonaSeleccionada);
						shape.setTipo(2);//2=coordenadas geograficas 1=coordenadas implantacion
						shape.setSuperficie(cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo().get(i).getSuperficie());
						cargarCoordenadasDigitalizacionBean.setShapeDigitalizacion(shapeDigitalizacionFacade.guardar(shape, loginBean.getUsuario()));
						CoordenadaDigitalizacion coor = new CoordenadaDigitalizacion();
						for (int j = 0; j <=cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().size()-1; j++) {
							coor= new CoordenadaDigitalizacion();
							coor.setShapeDigitalizacion(shape);
							coor.setOrden(cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getOrden());
							coor.setX(cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getX());
							coor.setY(cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getY());
							coor.setAreaGeografica(cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().get(j).getAreaGeografica());
							coor.setTipo(2);//2=coordenadas geograficas 1=coordenadas implantacion
							coor.setNumeroActualizacion(0);
							coordenadaDigitalizacionFacade.guardar(coor, loginBean.getUsuario());
						}
					}
					cargarCoordenadasDigitalizacionBean.setUploadedFileGeo(null);
					guardarProcesoActualizacion();
				}
				// guardo ubicaciones seleccionadas manualmente
			 	List<UbicacionDigitalizacion> listaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 2);
				if(adicionarUbicacionesPSADBean.getUbicacionesSeleccionadas() != null && !adicionarUbicacionesPSADBean.getUbicacionesSeleccionadas().isEmpty()){
					for(UbicacionesGeografica ubicacionSel : adicionarUbicacionesPSADBean.getUbicacionesSeleccionadas()){
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
						ubicacionD.setTipoIngreso(INGRESOMANUAL);
						ubicacionD.setPrincipal(false);
						ubicacionD.setSistemaReferencia(sistemareferenciaSeleccionado);
						ubicacionD.setZona(zonaSeleccionada);
						ubicacionDigitalizacionFacade.guardar(ubicacionD, loginBean.getUsuario());
						guardarProcesoActualizacion();
					}
				}
				// elimino las ubicaciones que no fueron seleccionadas o borradas
				listaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), 2, sistemareferecianPsad, zonaPsad);
				if(listaUbicacionTipo != null && listaUbicacionTipo.size() > 0){
					for (UbicacionDigitalizacion ubicacionDigitalizacion : listaUbicacionTipo) {
						if(!adicionarUbicacionesPSADBean.getUbicacionesSeleccionadas().contains(ubicacionDigitalizacion.getUbicacionesGeografica())){
							ubicacionDigitalizacion.setEstado(false);
							ubicacionDigitalizacionFacade.guardar(ubicacionDigitalizacion, loginBean.getUsuario());
							guardarProcesoActualizacion();
						}
					}
				}
				
				List<String> listaAreasGuardadas = new ArrayList<String>();
				Integer tipoArea = 1;//areas protegidas
				listaAreasGuardadas.addAll(cargarListaAreaBosques(tipoArea, 2));
				if(areasProtegidasSeleccionadas != null && !areasProtegidasSeleccionadas.isEmpty()){
					int i = 1;
					for(String area : areasProtegidasSeleccionadas){
						if(!listaAreasGuardadas.contains(area)){
							AreasProtegidasBosques areaP = new AreasProtegidasBosques();
							areaP.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
							areaP.setNombre(area);
							areaP.setTipo(tipoArea);
							areaP.setOrden(i);
							areaP.setTipoIngreso(INGRESOMANUAL);
							i++;
							areasProtegidasBosquesFacade.guardar(areaP, loginBean.getUsuario());
							guardarProcesoActualizacion();
						}
					}
				}
				// elimino las area protegidas eliminadas
				for(String area : listaAreasGuardadas){
					if(!areasProtegidasSeleccionadas.contains(area)){
						areasProtegidasBosquesFacade.eliminarIntersecciones(autorizacionAdministrativa.getId(), tipoArea, area, loginBean.getUsuario());
						guardarProcesoActualizacion();
					}
				}
				tipoArea =  2;//bosques protectores
				List<String> listaBosquesGuardados = new ArrayList<String>();
				listaBosquesGuardados.addAll(cargarListaAreaBosques(tipoArea, 2));
				if(bosquesProtectoresSeleccionados != null & !bosquesProtectoresSeleccionados.isEmpty()){
					int i = 1;
					for(String bosque : bosquesProtectoresSeleccionados){
						if(!listaBosquesGuardados.contains(bosque)){
							AreasProtegidasBosques bosqueP = new AreasProtegidasBosques();
							bosqueP.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
							bosqueP.setNombre(bosque);
							bosqueP.setTipo(tipoArea);
							bosqueP.setOrden(i);
							bosqueP.setTipoIngreso(INGRESOMANUAL);
							i++;
							areasProtegidasBosquesFacade.guardar(bosqueP, loginBean.getUsuario());
							guardarProcesoActualizacion();
						}
					}
				}
				// elimino las area protegidas eliminadas
				for(String bosque : listaBosquesGuardados){
					if(!bosquesProtectoresSeleccionados.contains(bosque)){
						areasProtegidasBosquesFacade.eliminarIntersecciones(autorizacionAdministrativa.getId(), tipoArea, bosque, loginBean.getUsuario());
						guardarProcesoActualizacion();
					}
				}
			}else{
				// elimino coordenadas guardadaas en otros sistemas de referencia
				shapeDigitalizacionFacade.eliminarShapePorSistema(autorizacionAdministrativa.getId(), 2, false, loginBean.getUsuario());
				// elimino ubicaciones seleccionadas manualmente
			 	List<UbicacionDigitalizacion> listaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 2);
				for (UbicacionDigitalizacion ubicacionDigitalizacion : listaUbicacionTipo) {
					ubicacionDigitalizacion.setEstado(false);
					ubicacionDigitalizacion.setFechaModificacion(new Date());
					ubicacionDigitalizacion.setUsuarioModificacion(loginBean.getUsuario().getNombre());
					ubicacionDigitalizacionFacade.guardar(ubicacionDigitalizacion, loginBean.getUsuario());
					guardarProcesoActualizacion();
				}
				//elimino areas protegidas seleccionados
				Integer tipoId = 1;
				List<AreasProtegidasBosques> objAreaBosque = areasProtegidasBosquesFacade.obtenerInterseccionesPorIngreso(autorizacionAdministrativa.getId(), tipoId, 2);
				if(objAreaBosque != null && objAreaBosque.size() > 0){
					for (AreasProtegidasBosques objAreaAux : objAreaBosque) {
						objAreaAux.setEstado(false);
						objAreaAux.setFechaModificacion(new Date());
						objAreaAux.setUsuarioModificacion(loginBean.getUsuario().getNombre());
						areasProtegidasBosquesFacade.guardar(objAreaAux, loginBean.getUsuario());
						guardarProcesoActualizacion();
					}
				}
				//elimino bosques protegidos seleccionados
				tipoId = 2;
				List<AreasProtegidasBosques> objBosque = areasProtegidasBosquesFacade.obtenerInterseccionesPorIngreso(autorizacionAdministrativa.getId(), tipoId, 2);
				if(objBosque != null && objBosque.size() > 0){
					for (AreasProtegidasBosques objBosqueAux : objBosque) {
						objBosqueAux.setEstado(false);
						objBosqueAux.setFechaModificacion(new Date());
						objBosqueAux.setUsuarioModificacion(loginBean.getUsuario().getNombre());
						areasProtegidasBosquesFacade.guardar(objBosqueAux, loginBean.getUsuario());
						guardarProcesoActualizacion();
					}
				}
			}
			/*********************************************************
			 * fin coordenadas diferentes de WGS84 zona 17S
			 ********************************************************/
			if(!soloLectura)
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean guardarPaso3(){
		try{
			if(soloLectura)
				return true;
			autorizacionAdministrativaAmbientalFacade.save(autorizacionAdministrativa, loginBean.getUsuario());
			guardarDocumentos();
			btnFinalizar=true;
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void finalizarActualizacion(){
		if(autorizacionAdministrativa.isFinalizado() && (autorizacionAdministrativa.getMotivoActualizacion() == null || autorizacionAdministrativa.getMotivoActualizacion().isEmpty())){
			JsfUtil.addMessageError("El campo motivo de actualización es requerido");
			return;
		}
		HistorialactualizacionesDigitalizacion actualizacionHis = new HistorialactualizacionesDigitalizacion();
		actualizacionHis.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
		actualizacionHis.setDescripcionActualizacion(autorizacionAdministrativa.getMotivoActualizacion());
		actualizacionHis.setUsuario(loginBean.getUsuario());
		historialActualizacionesDigitalizacionFacade.save(actualizacionHis, loginBean.getUsuario());
		// desbloqueo el proceso para que otro tecnico pueda modificar
		autorizacionAdministrativa.setEstadoActualizacion(false);
		autorizacionAdministrativaAmbientalFacade.save(autorizacionAdministrativa, loginBean.getUsuario());
		enviarNotificacion();
		redireccionarProyectosUpdate();
	}
	
	private void guardarProcesoActualizacion(){
		if(habilitarEditar && (autorizacionAdministrativa.getEstadoActualizacion() == null || !autorizacionAdministrativa.getEstadoActualizacion())){
			autorizacionAdministrativa.setActualizacionCoordenadas(false);
			autorizacionAdministrativa.setActualizacionResolucion(false);
			autorizacionAdministrativa.setEstadoActualizacion(true);
			autorizacionAdministrativaAmbientalFacade.save(autorizacionAdministrativa, loginBean.getUsuario());
		}
	}
	
	public void guardarFileCoorGeografica()
	{
		if(cargarCoordenadasWGSBean.getUploadedFileGeo()!=null)
		{
			DocumentoDigitalizacion documento = new DocumentoDigitalizacion();
			documento.setContenidoDocumento(cargarCoordenadasWGSBean.getUploadedFileGeo().getContents());
			documento.setExtension(".xls");
			documento.setTipoContenido("application/vnd.ms-excel");
			documento.setIdTabla(autorizacionAdministrativa.getId()); 		
			documento.setNombreTabla(AutorizacionAdministrativaAmbiental.class.getSimpleName());
			documento.setNombre("Coordenadas Digitalizacion(subidas).xls");
			documento.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);

			try {
				documentoDigitalizacionFacade.guardarDocumentoAlfresco(autorizacionAdministrativa.getCodigoProyecto(), "Coordenadas_Digitalizacion", 1L, documento, TipoDocumentoSistema.DIGITALIZACION_DOCUMENTO_COORDENADAS);
			} catch (Exception e) {
				LOG.error("Error al guardar documento Coordenadas", e);
			}
		}
	}
	
	public boolean validarInformacionPaso1(){
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
		if(autorizacionAdministrativa.getAreaResponsableControl() == null){
			listaMensajesErrores.add("El campo Ente responsable de control y seguimiento de la AAA es requerido");
		}
		/*if(esTecnico && !habilitarEditar){
			if(autorizacionAdministrativa.getAreaDZGTecnico() == null){
				listaMensajesErrores.add("El campo Direcciones Zonales y Galápagos es requerido");
			}
		}*/
		if(autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental() == null || autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental().equals("")){
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
		if(autorizacionAdministrativa.getCatalogoCIUU() == null){
			listaMensajesErrores.add("Seleccione el código CIIU de su actividad es requerido");
		}
		if(listaMensajesErrores.isEmpty()){
			return false;
		}else{
			JsfUtil.addMessageError(listaMensajesErrores);
			return true;
		}
	}
	
	public boolean validarInformacionPaso2(){
		listaMensajesErrores = new ArrayList<String>();
		if(mostrarOtrasCoordenadas == null){
			listaMensajesErrores.add("El sistema de referencia es requerido.");
		}
		// si las coordenadas son diferentes a WGS zona 17S
		if(mostrarOtrasCoordenadas != null && mostrarOtrasCoordenadas){
			if(cargarCoordenadasDigitalizacionBean.getShapeDigitalizacion() == null || cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo() == null || cargarCoordenadasDigitalizacionBean.getCoordinatesWrappersGeo().size() == 0){
				listaMensajesErrores.add("Las coordenadas son requeridas.");
			}
			if(adicionarUbicacionesPSADBean.getUbicacionesSeleccionadas() == null || adicionarUbicacionesPSADBean.getUbicacionesSeleccionadas().size() == 0){
				listaMensajesErrores.add("Las ubicaciones de las coordenadas son requeridas.");
			}
			if(cargarCoordenadasWGSBean.getShapeDigitalizacion() == null || cargarCoordenadasWGSBean.getCoordinatesWrappersGeo() == null || cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().size() == 0){
				listaMensajesErrores.add("Las coordenadas WGS zona 17S son requeridas.");
			}
			if(tieneDiferenciaInterseccion && (autorizacionAdministrativa.getJustificacionInterseccion() == null || autorizacionAdministrativa.getJustificacionInterseccion().isEmpty())){
				listaMensajesErrores.add("Las diferencias de intersección son requeridas.");
			}
		}
		if(cargarCoordenadasWGSBean.getShapeDigitalizacion()==null){
			listaMensajesErrores.add("Las coordenadas son requeridas.");
		}else{
			if(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo() == null || cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().size() == 0){
				listaMensajesErrores.add("Las coordenadas son requeridas.");
			}
		}
		if(listaMensajesErrores.isEmpty()){
			return false;
		}else{
			JsfUtil.addMessageError(listaMensajesErrores);
			return true;
		}
	}
	
	public boolean validarInformacionPaso3(){
		listaMensajesErrores = new ArrayList<String>();
//		if(certificadoInterseccion == null || certificadoInterseccion.getId() == null){
//			listaMensajesErrores.add("El certificado de intersección es requerido");
//		}
//		if(documentoMapa == null || documentoMapa.getId() == null){
//			listaMensajesErrores.add("El mapa de certificado de intersección es requerido");
//		}
		if(tipoCategoriaProyecto.equals("I")){
			if(documentoFichaAmbiental == null || documentoFichaAmbiental.getId() == null){
				listaMensajesErrores.add("El certificado ambiental es requerido");
			}
		}else{
			if(documentoResolucion == null || documentoResolucion.getId() == null){
				listaMensajesErrores.add((tipoCategoriaProyecto.equals("II")?"El registro":"La licencia")+" ambiental es requerido");
			}
//			if(listaDocumentosHabilitantes == null || listaDocumentosHabilitantes.size() == 0){
//				listaMensajesErrores.add("Los documentos habilitantes propios de la autorización es requerido");
//			}
		}
		if(autorizacionAdministrativa.getTieneCertificado()){
//			if(listaDocumentoOficioUpdate == null || listaDocumentoOficioUpdate.size() == 0){
//				listaMensajesErrores.add("Los documentos de Actualizaciones del Oficio del Certificado de Intersección es requerido");
//			}
//			if(listaDocumentoMapaUpdate == null || listaDocumentoMapaUpdate.size() == 0){
//				listaMensajesErrores.add("Los documentos de Actualizaciones del Mapa del Certificado de Intersección es requerido");
//			}
		}
		if(listaMensajesErrores.isEmpty()){
			return false;
		}else{
			JsfUtil.addMessageError(listaMensajesErrores);
			return true;
		}
	}
	
	public void crearNumeroTramite(){
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
						RequestContext.getCurrentInstance().update(":form:txtResolucion");
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error al validar el número de resolución", e);
		}
		return msjError;
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
	
	public void ingresarAreasProtegidas(){
		if(nombreAreaProtegida != null && !nombreAreaProtegida.isEmpty() && !nombreAreaProtegida.equals("")){
			if(areasProtegidasSeleccionadas.contains(nombreAreaProtegida)){
				JsfUtil.addMessageError("El Área Protegida ya se encuentra seleccionada.");
			}else{
				areasProtegidasSeleccionadas.add(nombreAreaProtegida);
			}
		}
		tieneDiferenciaInterseccion=compararInterseccion();
	}
	
	public void ingresarBosquesProtectores(){
		if(nombreBosqueProtector != null && !nombreBosqueProtector.isEmpty() && !nombreBosqueProtector.equals("")){
			if(bosquesProtectoresSeleccionados.contains(nombreBosqueProtector)){
				JsfUtil.addMessageError("El Bosque Protector ya se encuentra seleccionado");
			}else{
				bosquesProtectoresSeleccionados.add(nombreBosqueProtector);
			}
		}
		tieneDiferenciaInterseccion=compararInterseccion();
	}
	
	private boolean compararInterseccion(){
		boolean diferente=false;
		if(areasProtegidasSeleccionadas.size() > 0 || bosquesProtectoresSeleccionados.size() > 0){
			if((areasProtegidasSeleccionadas.size() != cargarCoordenadasWGSBean.getAreasProtegidasWgs().size()) || (bosquesProtectoresSeleccionados.size() != cargarCoordenadasWGSBean.getBosquesProtectoresWgs().size())){
				diferente=true;
			}else{
				if(areasProtegidasSeleccionadas.size() == cargarCoordenadasWGSBean.getAreasProtegidasWgs().size()){
					for(String area : areasProtegidasSeleccionadas){
						boolean existe = false;
						for(String areaAux : cargarCoordenadasWGSBean.getAreasProtegidasWgs()){
							if(area.toUpperCase().equals(areaAux.toUpperCase())){
								existe=true;
							}
						}
						// si no existe no son iguales
						if(!existe)
							diferente=true;
					}
				}
				if(bosquesProtectoresSeleccionados.size() == cargarCoordenadasWGSBean.getBosquesProtectoresWgs().size()){
					for(String bosque : bosquesProtectoresSeleccionados){
						boolean existe = false;
						for(String bosqueAux : cargarCoordenadasWGSBean.getBosquesProtectoresWgs()){
							if(bosque.toUpperCase().equals(bosqueAux.toUpperCase())){
								existe=true;
							}
						}
						// si no existe no son iguales
						if(!existe)
							diferente=true;
					}
				}
			}
		}
		return diferente;
	}
	
	public void eliminarAreaProtegida(String area){
		areasProtegidasSeleccionadas.remove(area);
		tieneDiferenciaInterseccion=compararInterseccion();
	}
	
	public void eliminarBosqueProtector(String bosque){
		bosquesProtectoresSeleccionados.remove(bosque);
		tieneDiferenciaInterseccion=compararInterseccion();
	}
	
	public void tieneActualizacion(boolean bandera){
		autorizacionAdministrativa.setTieneCertificado(bandera);
		mostrarMensaje=false;
	}
	
	public StreamedContent getDocumentoDownload(DocumentoDigitalizacion objDocumento){
		try {
			if (objDocumento != null && objDocumento.getContenidoDocumento() !=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(objDocumento.getContenidoDocumento()),objDocumento.getMime(),objDocumento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {
			LOG.error("Error al guardar documento Certificado", e);
		}
		return null;
	}

	public void eliminarDocumento(String documentoTipo){
		String tipoCertificadoInterseccion = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.certificado.interseccion" , true);
		String tipoMapa = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.mapa.certificado" , true);
		String tipoCertificadoAmbiental = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.certificado.ambiental" , true);
		String tipoRegistroAmbiental = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.registro.ambiental" , true);
		DocumentoDigitalizacion objDocumento = new DocumentoDigitalizacion();
		if(documentoTipo.equals(tipoCertificadoInterseccion)){
			objDocumento = certificadoInterseccion;
			certificadoInterseccion = new DocumentoDigitalizacion();
		}else if(documentoTipo.equals(tipoMapa)){
			objDocumento = documentoMapa;
			documentoMapa = new DocumentoDigitalizacion();
		}else if(documentoTipo.equals(tipoCertificadoAmbiental)){
			objDocumento = documentoFichaAmbiental;
			documentoFichaAmbiental = new DocumentoDigitalizacion();
		}if(documentoTipo.equals(tipoRegistroAmbiental)){
			objDocumento = documentoResolucion;
			documentoResolucion = new DocumentoDigitalizacion();
		}
		
		if(objDocumento.getId() != null){
			objDocumento.setEstado(false);
			documentoDigitalizacionFacade.guardar(objDocumento);
		}
	}

	
		public void eliminarDocumentoListaGeneral(String documentoTipo, DocumentoDigitalizacion objDocumento){
			String tipoHabilitantes = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.habilitantes" , true);
			String tipoOtrosDocumentos = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.otros" , true);
			String tipoDocumentosART = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.art" , true);
			String tipoDocumentosRSQ = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.rsq" , true);
			String tipoDocumentosOficio = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.actualizacion.oficio" , true);
			String tipoDocumentosMapa = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.actualizacion.mapa" , true);

			if(documentoTipo.equals(tipoHabilitantes)){
				listaDocumentosHabilitantes = new ArrayList<DocumentoDigitalizacion>();
				listaDocumentosHabilitantes.addAll(listaDocumentosIngreso);
			}else if(documentoTipo.equals(tipoOtrosDocumentos)){
				listaOtrosDocumentos = new ArrayList<DocumentoDigitalizacion>();
				listaOtrosDocumentos.addAll(listaDocumentosIngreso);
			}else if(documentoTipo.equals(tipoDocumentosART)){
				listaDocumentosART = new ArrayList<DocumentoDigitalizacion>();
				listaDocumentosART.addAll(listaDocumentosIngreso);
			}else if(documentoTipo.equals(tipoDocumentosRSQ)){
				listaDocumentosRSQ = new ArrayList<DocumentoDigitalizacion>();
				listaDocumentosRSQ.addAll(listaDocumentosIngreso);
			}else if(documentoTipo.equals(tipoDocumentosOficio)){
				listaDocumentoOficioUpdate = new ArrayList<DocumentoDigitalizacion>();
				listaDocumentoOficioUpdate.addAll(listaDocumentosIngreso);
			}else if(documentoTipo.equals(tipoDocumentosMapa)){
				listaDocumentoMapaUpdate = new ArrayList<DocumentoDigitalizacion>();
				listaDocumentoMapaUpdate.addAll(listaDocumentosIngreso);
			}
			eliminarDocumentoList(documentoTipo, objDocumento);
		}
		
		public void eliminarDocumentoList(String documentoTipo, DocumentoDigitalizacion objDocumento){
		String tipoHabilitantes = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.habilitantes" , true);
		String tipoOtrosDocumentos = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.otros" , true);
		String tipoDocumentosART = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.art" , true);
		String tipoDocumentosRSQ = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.rsq" , true);
		String tipoDocumentosOficio = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.actualizacion.oficio" , true);
		String tipoDocumentosMapa = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.actualizacion.mapa" , true);


		listaDocumentosIngreso = new ArrayList<DocumentoDigitalizacion>();
		if(documentoTipo.equals(tipoHabilitantes)){
			listaDocumentosIngreso.addAll(listaDocumentosHabilitantes);
		}else if(documentoTipo.equals(tipoOtrosDocumentos)){
			listaDocumentosIngreso.addAll(listaOtrosDocumentos);
		}else if(documentoTipo.equals(tipoDocumentosART)){
			listaDocumentosIngreso.addAll(listaDocumentosART);
		}else if(documentoTipo.equals(tipoDocumentosRSQ)){
			listaDocumentosIngreso.addAll(listaDocumentosRSQ);
		}else if(documentoTipo.equals(tipoDocumentosOficio)){
			listaDocumentosIngreso.addAll(listaDocumentoOficioUpdate);
		}else if(documentoTipo.equals(tipoDocumentosMapa)){
			listaDocumentosIngreso.addAll(listaDocumentoMapaUpdate);
		}
		
		for (DocumentoDigitalizacion documento : listaDocumentosIngreso) {
			if(documento.getContenidoDocumento().equals(objDocumento.getContenidoDocumento())){
				listaDocumentosIngreso.remove(documento);
				break;
			}
		}
		
		if(documentoTipo.equals(tipoHabilitantes)){
			listaDocumentosHabilitantes = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentosHabilitantes.addAll(listaDocumentosIngreso);
		}else if(documentoTipo.equals(tipoOtrosDocumentos)){
			listaOtrosDocumentos = new ArrayList<DocumentoDigitalizacion>();
			listaOtrosDocumentos.addAll(listaDocumentosIngreso);
		}else if(documentoTipo.equals(tipoDocumentosART)){
			listaDocumentosART = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentosART.addAll(listaDocumentosIngreso);
		}else if(documentoTipo.equals(tipoDocumentosRSQ)){
			listaDocumentosRSQ = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentosRSQ.addAll(listaDocumentosIngreso);
		}else if(documentoTipo.equals(tipoDocumentosOficio)){
			listaDocumentoOficioUpdate = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentoOficioUpdate.addAll(listaDocumentosIngreso);
		}else if(documentoTipo.equals(tipoDocumentosMapa)){
			listaDocumentoMapaUpdate = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentoMapaUpdate.addAll(listaDocumentosIngreso);
		}
		if(objDocumento.getId() != null){
			objDocumento.setEstado(false);
			documentoDigitalizacionFacade.guardar(objDocumento);
		}
	}

	public void cargarlistaDocumento(String documentoTipo){
		String tipoHabilitantes = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.habilitantes" , true);
		String tipoOtrosDocumentos = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.otros" , true);
		String tipoDocumentosART = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.art" , true);
		String tipoDocumentosRSQ = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.rsq" , true);
		String tipoDocumentosOficio = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.actualizacion.oficio" , true);
		String tipoDocumentosMapa = JsfUtil.getMessageFromBundle("labels", "label.proceso.digitalizacion.documento.actualizacion.mapa" , true);
		if(documentoTipo.equals(tipoHabilitantes)){
			listaDocumentosHabilitantes = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentosHabilitantes.addAll(listaDocumentosIngreso);
		}else if(documentoTipo.equals(tipoOtrosDocumentos)){
			listaOtrosDocumentos = new ArrayList<DocumentoDigitalizacion>();
			listaOtrosDocumentos.addAll(listaDocumentosIngreso);
		}else if(documentoTipo.equals(tipoDocumentosART)){
			listaDocumentosART = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentosART.addAll(listaDocumentosIngreso);
		}else if(documentoTipo.equals(tipoDocumentosRSQ)){
			listaDocumentosRSQ = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentosRSQ.addAll(listaDocumentosIngreso);
		}else if(documentoTipo.equals(tipoDocumentosOficio)){
			listaDocumentoOficioUpdate = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentoOficioUpdate.addAll(listaDocumentosIngreso);
		}else if(documentoTipo.equals(tipoDocumentosMapa)){
			listaDocumentoMapaUpdate = new ArrayList<DocumentoDigitalizacion>();
			listaDocumentoMapaUpdate.addAll(listaDocumentosIngreso);
		}
	}

	public void documentoCreacionListener(FileUploadEvent event) {
		DocumentoDigitalizacion documentoSubido = (DocumentoDigitalizacion) event.getComponent().getAttributes().get("documentId");
		documentoSubido.setSubido(false);
		documentoSubido.setTipoIngreso(2);
		uploadListener(event, documentoSubido, AutorizacionAdministrativaAmbiental.class);
	}

	public void documentoCreacionListListener(FileUploadEvent event) {
		DocumentoDigitalizacion documentoSubido = new DocumentoDigitalizacion();
		documentoSubido.setSubido(false);
		documentoSubido.setTipoIngreso(2);
		documentoSubido = uploadListener(event, documentoSubido, AutorizacionAdministrativaAmbiental.class);
		listaDocumentosIngreso.add(documentoSubido);
	}
	
	private DocumentoDigitalizacion uploadListener(FileUploadEvent event, DocumentoDigitalizacion documento, Class<?> clazz){
		byte[] contenidoDocumento = event.getFile().getContents();
		documento.setNombre(event.getFile().getFileName());
		documento.setContenidoDocumento(contenidoDocumento);
		String[] split=documento.getNombre().split("\\.");
		documento.setExtension("."+split[split.length-1]);
		documento.setMime(event.getFile().getContentType().compareTo("application/download")!=0?event.getFile().getContentType():"application/pdf");
		return documento;
	}
	
	private void enviarNotificacion(){
		boolean tieneRGD = false;
		try{
			if(autorizacionAdministrativa.isActualizacionResolucion() || autorizacionAdministrativa.isActualizacionCoordenadas()){
				tieneRGD = tieneRgdAsociado();
				// si tiene rgd asociado envio correo
				if(tieneRGD){
					List<Contacto> contactos = contactoFacade.buscarUsuarioNativeQuery(autorizacionAdministrativa.getUsuario().getNombre());
					String email= null;
					String nombreProponente=null;
					for (Contacto contacto : contactos) {
						if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)){
							email=contacto.getValor();
							nombreProponente=contacto.getOrganizacion()!=null?contacto.getOrganizacion().getNombre():contacto.getPersona().getNombre();
							if(nombreProponente!=null)
								break;
						}
					}
					//envio notificacion si hubo cambios en la resolucion o fecha de resolucion
					if(autorizacionAdministrativa.isActualizacionResolucion()){
						String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionOperadorActualizarRgd", new Object[]{});
						enviarMail(mensaje, email);
					}
					//envio notificacion si hubo cambios en las coordenadas 
					if(autorizacionAdministrativa.isActualizacionCoordenadas()){
						String coordenadasProyecto="", coodenadasPuntoVerificacion="";
						Integer tipoForma = 3;
						boolean esPoligono = true, correcto = true;
						//obtengo el rgd asociado al proyecto suia_iii
						List<GeneradorDesechosPeligrosos> listaGenerador = registroGeneradorDesechosFacade.findRGDByProyectoLicenciaAmbientalId(autorizacionAdministrativa.getIdProyecto());
						// verifico si el punto de verificacion esta dentro del area geografica modificada
						if(listaGenerador != null && listaGenerador.size() > 0){
				    		List<PuntoRecuperacion> listaPuntos = registroGeneradorDesechosFacade.cargarPuntosPorIdGenerador(listaGenerador.get(0).getId());
							for (PuntoRecuperacion puntoRecuperacion : listaPuntos){
								for (FormaPuntoRecuperacion forma : puntoRecuperacion.getFormasPuntoRecuperacion()){
									coodenadasPuntoVerificacion="";
									esPoligono=forma.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO);
									for (Coordenada objCoordenada : forma.getCoordenadas()) {
										coodenadasPuntoVerificacion += (coodenadasPuntoVerificacion == "") ? BigDecimal.valueOf(objCoordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(objCoordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString() : ","+BigDecimal.valueOf(objCoordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(objCoordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString();
									}
									correcto = verificarPuntoVerficacionRGD(esPoligono, coodenadasPuntoVerificacion);
									//si no esta dentro del area ingresada envio correo y desvinculo el RGD
									if(!correcto){
										String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionOperadorActualizarVincularRgd", new Object[]{});
										registroGeneradorDesechosFacade.desvincularRGDAsociado(tramite);
										desvinculoRGD(listaGenerador.get(0), null);
										enviarMail(mensaje, email);
										break;
									}
								}
							}
						}
						//obtengo el RGD AAA asociado al proyecto digitalizado
						RegistroGeneradorDesechosRcoa registroGeneradorDesechos = registroGeneradorFacade.buscarRGDPorProyectoDigitalizado(autorizacionAdministrativa.getId());
						if(registroGeneradorDesechos != null){
							List<PuntoRecuperacionRgdRcoa> puntosRecuperacion = puntoRecuperacionRgdRcoaFacade.buscarPorRgd(registroGeneradorDesechos.getId());
							for(PuntoRecuperacionRgdRcoa punto : puntosRecuperacion){
								coodenadasPuntoVerificacion = "";
								if(punto.getFormasPuntoRecuperacionRgdRcoa() != null && !punto.getFormasPuntoRecuperacionRgdRcoa().isEmpty()){
									esPoligono=punto.getFormasPuntoRecuperacionRgdRcoa().get(0).getId().equals(TipoForma.TIPO_FORMA_POLIGONO);
									for(CoordenadaRgdCoa coordenada : punto.getFormasPuntoRecuperacionRgdRcoa().get(0).getCoordenadas()){
										DecimalFormat formato = new DecimalFormat("#.00000");
										String coorX = formato.format(coordenada.getX()).replace(",", ".");
										String coorY = formato.format(coordenada.getY()).replace(",", ".");
										coodenadasPuntoVerificacion += (coodenadasPuntoVerificacion == "") ? coorX.toString() + " " + coorY.toString() : "," + coorX.toString() + " " + coorY.toString();
									}
								}
								correcto = verificarPuntoVerficacionRGD(esPoligono, coodenadasPuntoVerificacion);
								//si no esta dentro del area ingresada envio correo y desvinculo el RGD
								if(!correcto){
									String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionOperadorActualizarVincularRgd", new Object[]{});
									desvinculoRGD(null, registroGeneradorDesechos);
									enviarMail(mensaje, email);
									break;
								}
							}
						}
						//obtengo el rgd asociado al proyecto en digitalizacion
						List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados = proyectoAsociadoDigitalizacionFacade.buscarProyectosAsociados(autorizacionAdministrativa.getId());
						for (ProyectoAsociadoDigitalizacion proyectoAsociado : listaProyectosAsociados) {
							//verifico si es un RGD
							if(proyectoAsociado.getTipoProyecto() != null && proyectoAsociado.getTipoProyecto().toString().equals("4")){
								// si es RGD AAA ya no valido xq se valido antes
								if(proyectoAsociado.getNombreTabla().startsWith("coa"))
									continue;
								//obtengo el rgd asociado al proyecto suia_iii
								GeneradorDesechosPeligrosos objGenerador = registroGeneradorDesechosFacade.get(proyectoAsociado.getProyectoAsociadoId());
								// verifico si el punto de verificacion esta dentro del area geografica modificada
					    		List<PuntoRecuperacion> listaPuntos = registroGeneradorDesechosFacade.cargarPuntosPorIdGenerador(objGenerador.getId());
								for (PuntoRecuperacion puntoRecuperacion : listaPuntos){
									for (FormaPuntoRecuperacion forma : puntoRecuperacion.getFormasPuntoRecuperacion()){
										coodenadasPuntoVerificacion="";
										esPoligono=forma.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO);
										for (Coordenada objCoordenada : forma.getCoordenadas()) {
											coodenadasPuntoVerificacion += (coodenadasPuntoVerificacion == "") ? BigDecimal.valueOf(objCoordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(objCoordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString() : ","+BigDecimal.valueOf(objCoordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(objCoordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString();
										}
										correcto = verificarPuntoVerficacionRGD(esPoligono, coodenadasPuntoVerificacion);
										//si no esta dentro del area ingresada envio correo y desvinculo el RGD
										if(!correcto){
											String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionOperadorActualizarVincularRgd", new Object[]{});
											desvinculoRGD(objGenerador, null);
											enviarMail(mensaje, email);
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error al recuperar la información", e);
		}
	}
	
	/**
	 * metodo para verificar que el punto de verificacion del rgd este dentro de alguna area del proyecto
	 * @param esPoligono
	 * @param coodenadasPuntoVerificacion
	 * @return
	 */
	private boolean verificarPuntoVerficacionRGD(boolean esPoligono, String coodenadasPuntoVerificacion){
		boolean esCorrecto = false;
		Integer tipoForma = 3;
		String coordenadasProyecto="";
		//obtengo las coordenadas WGS84 17z
		for (int i = 0; i <= cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().size() - 1; i++) {
			//valido que el shape tenga coordenadas
			if(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas() == null || cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas().size() == 0)
				continue;
			if(cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getTipoForma() != null && cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getTipoForma().getId() != null)
				tipoForma = cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getTipoForma().getId();
			coordenadasProyecto="";
			for (CoordenadaDigitalizacion objCoordenada : cargarCoordenadasWGSBean.getCoordinatesWrappersGeo().get(i).getCoordenadas()) {
				coordenadasProyecto += (coordenadasProyecto == "") ? objCoordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+objCoordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString() : ","+objCoordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+objCoordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString();
			}
			esCorrecto = validarCoordenadas(tipoForma, esPoligono, coordenadasProyecto, coodenadasPuntoVerificacion);
			if(esCorrecto)
				break;
		}
		return esCorrecto;
	}
		
	private void desvinculoRGD(GeneradorDesechosPeligrosos objGenerador, RegistroGeneradorDesechosRcoa registroGeneradorDesechos){
		try{
			// si es RGD de suia_iii
			if(objGenerador != null){
				//guardo el historial del registro
				GeneradorDesechosPeligrosos objGeneradorAux = new GeneradorDesechosPeligrosos();
				objGeneradorAux.setResponsabilidadExtendida(objGenerador.getResponsabilidadExtendida());
				objGeneradorAux.setEliminacionDentroEstablecimiento(objGenerador.isEliminacionDentroEstablecimiento());
				objGeneradorAux.setFinalizado(objGenerador.isFinalizado());
				objGeneradorAux.setCodigo(objGenerador.getCodigo());
				objGeneradorAux.setObservaciones(objGenerador.getObservaciones());
				objGeneradorAux.setSolicitud(objGenerador.getSolicitud());
				objGeneradorAux.setMotivoEliminacion(objGenerador.getMotivoEliminacion());
				objGeneradorAux.setRespuestasAclaratorias(objGenerador.getRespuestasAclaratorias());
				objGeneradorAux.setFisico(objGenerador.isFisico());
				objGeneradorAux.setTipoIngreso(objGenerador.getTipoIngreso());
				objGeneradorAux.setEnaaId(autorizacionAdministrativa.getId());
				objGeneradorAux.setIdPadreHistorial(objGenerador.getId());
				objGeneradorAux.setProyecto(objGenerador.getProyecto());
				objGeneradorAux.setTipoSector(objGenerador.getTipoSector());
				objGeneradorAux.setPoliticaDesecho(objGenerador.getPoliticaDesecho());
				objGeneradorAux.setPoliticaDesechoActividad(objGenerador.getPoliticaDesechoActividad());
				objGeneradorAux.setUsuario(objGenerador.getUsuario());
				objGeneradorAux.setAreaResponsable(objGenerador.getAreaResponsable());
				objGeneradorAux.setDocumentoBorrador(objGenerador.getDocumentoBorrador());
				objGeneradorAux.setDocumentoJustificacionProponente(objGenerador.getDocumentoJustificacionProponente());
				objGeneradorAux.setJustificacionProponente(objGenerador.getJustificacionProponente());
				objGeneradorAux.setEstado(false);
				objGeneradorAux.setUsuarioModificacion(loginBean.getUsuario().getNombre());
				objGeneradorAux.setFechaModificacion(new Date());
				//registroGeneradorDesechosFacade.guardar(objGeneradorAux);
				// desviculo el rgd del proyecto
				objGenerador.setProyecto(null);
				registroGeneradorDesechosFacade.guardar(objGenerador);
				//verifico si la AAA tiene vinculado un RGD en digitalizacion
				List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados = proyectoAsociadoDigitalizacionFacade.buscarProyectosAsociados(autorizacionAdministrativa.getId());
				for (ProyectoAsociadoDigitalizacion proyectoAsociado : listaProyectosAsociados) {
					if(proyectoAsociado.getTipoProyecto() != null && proyectoAsociado.getTipoProyecto().toString().equals("4")){
						if(objGenerador.getId().equals(proyectoAsociado.getProyectoAsociadoId())){
							proyectoAsociado.setEstado(false);
							proyectoAsociadoDigitalizacionFacade.guardar(proyectoAsociado, loginBean.getUsuario());
						}
					}
				}
			}else if(registroGeneradorDesechos != null){
				//guardo el historial del registro
				List<RegistroGeneradorDesechosProyectosRcoa> ListRGDProyecto = registroGeneradorDesechosProyectosRcoaFacade.buscarPorRegistroGenerador(registroGeneradorDesechos.getId());
				if(ListRGDProyecto != null && ListRGDProyecto.size() > 0){
					RegistroGeneradorDesechosProyectosRcoa rgdProyecto = ListRGDProyecto.get(0);
					RegistroGeneradorDesechosProyectosRcoa objGeneradorAux = (RegistroGeneradorDesechosProyectosRcoa) SerializationUtils.clone(rgdProyecto);
					objGeneradorAux.setProyectoDigitalizado(autorizacionAdministrativa);
					objGeneradorAux.setId(null);
					objGeneradorAux.setEstado(false);
					objGeneradorAux.setIdPadreHistorial(rgdProyecto.getId());
					objGeneradorAux.setUsuarioModificacion(loginBean.getUsuario().getNombre());
					objGeneradorAux.setFechaModificacion(new Date());
					registroGeneradorDesechosProyectosRcoaFacade.save(objGeneradorAux, loginBean.getUsuario());
					// desviculo el rgd del proyecto
					rgdProyecto.setProyectoId(null);
					rgdProyecto.setProyectoDigitalizado(null);
					registroGeneradorDesechosProyectosRcoaFacade.save(rgdProyecto, loginBean.getUsuario());	
				}
				//verifico si la AAA tiene vinculado un RGD en digitalizacion
				List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados = proyectoAsociadoDigitalizacionFacade.buscarProyectosAsociados(autorizacionAdministrativa.getId());
				for (ProyectoAsociadoDigitalizacion proyectoAsociado : listaProyectosAsociados) {
					if(proyectoAsociado.getTipoProyecto() != null && proyectoAsociado.getTipoProyecto().toString().equals("4")){
						if(registroGeneradorDesechos.getId().equals(proyectoAsociado.getProyectoAsociadoId())){
							proyectoAsociado.setEstado(false);
							proyectoAsociadoDigitalizacionFacade.guardar(proyectoAsociado, loginBean.getUsuario());
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * valida si las coordenadas ingresads estan dentro del area de implantacion
	 * @param tipoFormaPoligono forma del poligono contenedor
	 * @param poligonoTipo forma del poligono contenido
	 * @param coordenadasArea  coordenadas del poligono contenedor (area geografica o implantacion)
	 * @param coordenadasingresadas
	 * @return
	 */
	public boolean validarCoordenadas(Integer tipoFormaPoligono, boolean poligonoTipo, String coordenadasArea, String coordenadasingresadas){
		try{
			boolean estaDentro = false;
	        SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
	        ws.setEndpoint(Constantes.getInterseccionesWS());
			if(tipoFormaPoligono.equals(TipoForma.TIPO_FORMA_POLIGONO)){
				if(poligonoTipo){
					ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada(); //verifica que el poligono este contenida dentro de la ubicación geográfica
	        		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
	        		verificarGeoImpla.setXy1(coordenadasArea);
	        		verificarGeoImpla.setXy2(coordenadasingresadas);
	        		ContieneZona_resultado[]intRestGeoImpl;
	        		intRestGeoImpl=ws.contieneZona(verificarGeoImpla);
	        		if (intRestGeoImpl[0].getInformacion().getError() != null) {
	        			
	           		}else if (intRestGeoImpl[0].getContieneCapa().getValor().equals("t")){
	           			estaDentro=true;
	           		}
				}else{
					ContienePoligono_entrada verificarGeoImpla = new ContienePoligono_entrada(); //verifica que el punto este contenida dentro de la ubicación geográfica o de implantacion
	        		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
	        		verificarGeoImpla.setTipo("pu");
	        		verificarGeoImpla.setXy1(coordenadasArea);
	        		verificarGeoImpla.setXy2(coordenadasingresadas);
	        		ContienePoligono_resultado[]intRestGeoImpl;
	        		intRestGeoImpl=ws.contienePoligono(verificarGeoImpla);
	        		if (intRestGeoImpl[0].getInformacion().getError() != null) {
	           		}else if (intRestGeoImpl[0].getContienePoligono().getValor().equals("t")){
	           			estaDentro=true;
	              	}
				}
			}else{
    			if (coordenadasArea.equals(coordenadasingresadas)){
    				estaDentro = true;
    			}else if (coordenadasArea.contains(coordenadasingresadas)){
    				estaDentro = true;
    			}
			}
			return estaDentro;
		}catch(RemoteException e){
			return false;
		}
	}
	
	private boolean tieneRgdAsociado(){
		boolean tieneRGD = false;
		//verifico si la AAA tiene vinculado un RGD en digitalizacion
		List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados = proyectoAsociadoDigitalizacionFacade.buscarProyectosAsociados(autorizacionAdministrativa.getId());
		for (ProyectoAsociadoDigitalizacion proyectoAsociado : listaProyectosAsociados) {
			if(proyectoAsociado.getTipoProyecto() != null && proyectoAsociado.getTipoProyecto().toString().equals("4")){
				tieneRGD = true;
				break;
			}
		}
		// verifico si el proyecto digitalozado tienen un RGD AAA
		if(!tieneRGD && (autorizacionAdministrativa.getSistema().toString().equals("0") || autorizacionAdministrativa.getSistema().toString().equals("1"))){ // si nuevo o fisico
			RegistroGeneradorDesechosRcoa registroGeneradorDesechos = registroGeneradorFacade.buscarRGDPorProyectoDigitalizado(autorizacionAdministrativa.getId());
			if(registroGeneradorDesechos != null && registroGeneradorDesechos.getId() != null){
				tieneRGD = true;
			}
		}
		// si no tiene RGD vinculado en digitalizacion verifico si tiene rgd asociado en el sistema anterior
		if(!tieneRGD && autorizacionAdministrativa.getSistema().toString().equals("4")){ // si es proyecto suia_iii
			List<GeneradorDesechosPeligrosos> registrosGeneradores = generadorDesechosPeligrososFacade.getByProyectoLicenciaAmbiental(autorizacionAdministrativa.getCodigoProyecto());
			if(registrosGeneradores != null && registrosGeneradores.size() > 0){
				tieneRGD = true;
			}
		}
		return tieneRGD;
	}
	
	private void enviarMail(String mensaje, String email){
		mensaje = mensaje.replace("codigo_proyecto", autorizacionAdministrativa.getCodigoProyecto());
		mensaje = mensaje.replace("resolucion_ambiental", autorizacionAdministrativa.getResolucion());
		mensaje = mensaje.replace("nombre_proyecto", autorizacionAdministrativa.getNombreProyecto());
		mensaje = mensaje.replace("tipo_proceso", "Registro de Generador de Residuos y Desechos Peligrosos y/o Especiales");
		NotificacionAutoridadesController mail = new NotificacionAutoridadesController();
		mail.sendEmailInformacionProponente(email, "", mensaje, "Notificación", autorizacionAdministrativa.getCodigoProyecto(), autorizacionAdministrativa.getUsuario(), loginBean.getUsuario());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void redireccionarProyectos(){
		JsfUtil.redirectTo("/pages/rcoa/digitalizacion/digitalizacionAAA.jsf");
	}
	
	public void redireccionarProyectosUpdate(){
		JsfUtil.redirectTo("/pages/rcoa/digitalizacion/actualizcionProyectoDigitalizacion.jsf");
	}
	
	public void redireccionarBandeja(){
		JsfUtil.redirectToBandeja();
	}
	public void validarTareaBpm() {

	}
}
