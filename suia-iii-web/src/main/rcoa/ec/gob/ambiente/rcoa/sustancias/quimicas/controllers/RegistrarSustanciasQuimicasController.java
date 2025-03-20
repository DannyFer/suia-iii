package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import index.ContienePoligono_entrada;
import index.ContienePoligono_resultado;
import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.Intersecado_capa;
import index.Intersecado_coordenada;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import _154._1._180._10._8080.consultatitulossenescytwsv3.servicioconsultatitulo.GraduadoReporteDTO;
import _154._1._180._10._8080.consultatitulossenescytwsv3.servicioconsultatitulo.NivelTitulosDTO;
import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ActividadSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ResponsableSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.UbicacionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadNivel;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.CaracteristicaActividad;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ResponsableSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.SustanciasQuimicaPeligrosaTransporteFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.SustanciaQuimicaPeligrosaTransporte;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;

@ManagedBean
@ViewScoped
public class RegistrarSustanciasQuimicasController {
	
	private static final Logger LOG = Logger.getLogger(RegistrarSustanciasQuimicasController.class);
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{wizardBean}")
	@Getter
	@Setter
	private WizardBean wizardBean;
    
    /*EJBs*/   
	@EJB
    private ActividadSustanciaQuimicaFacade actividadSustanciaQuimicaFacade;
	
	@EJB
    private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
    private CatalogoCoaFacade catalogoCoaFacade;
	
	@EJB
    private ContactoFacade contactoFacade;
	
	@EJB
    private DocumentosRSQFacade documentosRSQFacade;
	
	@EJB
    private GestionarProductosQuimicosProyectoAmbientalFacade sustanciasProyectoFacade;
	
	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;	
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
    private OrganizacionFacade organizacionFacade;    
    
    @EJB
    private ProcesoFacade procesoFacade;
    
    @EJB
    private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
    
    @EJB
    private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
    
    @EJB
    private ResponsableSustanciaQuimicaFacade responsableSustanciaQuimicaFacade;
    
    @EJB
    private SustanciasQuimicaPeligrosaTransporteFacade sustanciaQuimicaPeligrosaTransporteFacade;
    
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    
    @EJB
    private UbicacionSustanciaQuimicaFacade ubicacionSustanciaQuimicaFacade;
    
    @EJB
    private UsuarioFacade usuarioFacade;
    
    @EJB
    private ProyectoLicenciaAmbientalCoaShapeFacade  proyectoLicenciaAmbientalShapeFacade;
    
    @EJB
    private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
    
    @EJB
    private UbicacionGeograficaFacade ubicacionFacade;
    
    @EJB
    private CrudServiceBean crudServiceBean;
    
    @EJB
    private PersonaFacade personaFacade;
           
    /*List*/    
    private List<ActividadSustancia> actividadSustanciaProyectoLista;
    
    @Getter
    private List<ActividadNivel> actividadesNivel1;
    
    @Getter
    private List<ActividadNivel> actividadesNivel2, actividadesNivel3;
    
    @Getter
    private List<CatalogoGeneralCoa> lugaresLista;
    
    @Getter
    private List<CatalogoGeneralCoa> tipoIdentificacionLista;
    
    @Getter
    private List<CatalogoGeneralCoa> tipoIdentificacionResponsableLista;
    
    @Getter
    private List<CatalogoGeneralCoa> unidadesMedidaLista;
    
    @Getter
    private List<InformeOficioRSQ> informeOficioRSQLista;
        
    private List<ResponsableSustanciaQuimica> responsableSustanciaQuimicaLista;
        
    private List<ResponsableSustanciaQuimica> responsableSustanciaQuimicaEliminadosLista;
    
    @Getter
    private List<SustanciaQuimicaPeligrosaTransporte> sustanciasVehiculoTransporteLista;
    
    @Getter
    private List<GestionarProductosQuimicosProyectoAmbiental> gestionarProductosQuimicosProyectoAmbientalLista;
    
    @Getter
    private List<UbicacionesGeografica> provinciasLista;
    
    @Getter
    private List<UbicacionSustancia> ubicacionSustanciaProyectoLista;
    
    private List<UbicacionSustancia> ubicacionSustanciaProyectoEliminadosLista;
       
	/*Object*/
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
	@Getter
    @Setter
    private DocumentosSustanciasQuimicasRcoa documentoInformeInspeccion;
			
	@Getter
	private DocumentosSustanciasQuimicasRcoa documentoEmisionRsq,documentoJustificaciones,documentoProrroga;
	
	private Map<String, Object> variables;
	
	
	private Usuario usuarioOperador;
	
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private RegistroSustanciaQuimica registroSustanciaQuimica;
	
	@Getter
	@Setter
	private ResponsableSustanciaQuimica responsable;	
	
	@Getter
	@Setter
	private GestionarProductosQuimicosProyectoAmbiental gestionarProductosQuimicosProyectoAmbientalSeleccionado;
	
	@Getter
	@Setter
    private UbicacionesGeografica provinciaSeleccionada;
	
	@Getter
	@Setter
    private UbicacionSustancia ubicacionSustanciaProyecto;
		
	/*Boolean*/	
	@Getter
	@Setter
	private boolean aceptaTerminos;
	
	@Getter
	private boolean habilitarIngreso,verObservaciones,editarObservaciones,msgCorrecciones;
	
	@Getter 
	@Setter
	private Boolean requiereProrroga;
	
	@Getter 
	@Setter
	private boolean verProrroga;
	
    /*Integer*/
	private Integer numeroRevision;  
    
	/*String*/
	private String varUsuarioOperador,varTramite;
	
	//Datos del operador
    @Getter       
    private String rucCedula,nombreRazonSocial,telefono,correo,direccion,provincia,canton,parroquia,cedulaPersona,nombrePersona;    
    
    @Getter
	@Setter
	private String cedulaResponsable,nombreResponsable;
    
    @Getter
    @Setter
    private String nombreTarea;
    
    @Getter
    @Setter
    private Boolean deshabilitar = false;
    
    @Getter
   	@Setter
    private List<CoordendasPoligonos> coordinatesWrappersPre;
    
    @Getter
    @Setter
    private List<GestionarProductosQuimicosProyectoAmbiental> listaSustanciasTecnico;
    
    private String coodenadasgeograficas="";       
    
    private HashMap<String, Double> varUbicacionArea= new HashMap<String,Double>();
	
    private static final String ALMACENAMIENTO_PROPIO = "ALMACENAMIENTO PROPIO";
    private static final String ALMACENAMIENTO = "ALMACENAMIENTO";
    private static final String BODEGA = "BODEGA";
    private static final String EDUCATIVA = "EDUCATIVA";
    private static final String ESTUDIANTE = "ESTUDIANTE";
    private static final String ESTUDIANTES = "ESTUDIANTES";
    private static final String TRANSPORTE = "TRANSPORTE";
    private static final String MERCURIO = "MERCURIO";
    private static final String TECNICO = "TÉCNICO";
    
	@PostConstruct
	public void init(){		
		try {
			cargarDatosIniciales();
			Object rsqIdLectura = JsfUtil.devolverObjetoSession("rsqIdLectura");
			if (rsqIdLectura != null && !rsqIdLectura.toString().isEmpty()) {
				registroSustanciaQuimica = registroSustanciaQuimicaFacade.obtenerRegistroPorId(Integer.parseInt(rsqIdLectura.toString()));
				varTramite = registroSustanciaQuimica.getCodigo();
				varUsuarioOperador = registroSustanciaQuimica.getUsuarioCreacion();
			} else {
				cargarDatosTarea();
			}
			cargarDatosOperador();
			cargarDatosProyecto();
		} catch (Exception e) {
			e.printStackTrace();
		}
 	}
	
	private void cargarDatosIniciales(){		
		//Iniciar Objetos
		ubicacionSustanciaProyecto=new UbicacionSustancia();
		responsable=new ResponsableSustanciaQuimica();
		
		//Iniciar Listas
		actividadSustanciaProyectoLista=new ArrayList<>();
		ubicacionSustanciaProyectoLista=new ArrayList<>();
		ubicacionSustanciaProyectoEliminadosLista=new ArrayList<>();
		responsableSustanciaQuimicaLista=new ArrayList<>();
		responsableSustanciaQuimicaEliminadosLista=new ArrayList<>();
		
		//Cargar Catalogos 
		provinciasLista=ubicacionGeograficaFacade.getProvincias();
		lugaresLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.RSQ_TIPO_LUGAR);
		tipoIdentificacionLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.TIPO_IDENTIFICACION);
		tipoIdentificacionResponsableLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.RSQ_TIPO_IDENTIFICACION_RESPONSABLE);
		unidadesMedidaLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.RSQ_UNIDAD_MEDIDA);
		
		listaSustanciasTecnico = new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
	}
			
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());			
			varUsuarioOperador=(String)variables.get("usuario_operador");
			varTramite=(String)variables.get("tramite");
			numeroRevision=variables.containsKey("numero_revision")?(Integer.valueOf((String)variables.get("numero_revision"))):1;
			
			habilitarIngreso=false;
			verObservaciones=false;
			editarObservaciones=false;
			
			nombreTarea=bandejaTareasBean.getTarea().getTaskName().toUpperCase();
			if(nombreTarea.contains("INGRESAR INFORMACION") || nombreTarea.contains("REMITIR RESPUESTAS ACLARATORIAS") 
					|| nombreTarea.contains("INGRESAR CORRECCIONES") || nombreTarea.contains("INGRESAR INFORMACIÓN")) {
				
				habilitarIngreso=false;
				verObservaciones=variables.get("observaciones_rsq")!=null;
				msgCorrecciones=verObservaciones;
				
				//Si pasaron 18 dias, al dia 19 se muestra la opcion de solicitar prorroga
				if(nombreTarea.contains("REMITIR RESPUESTAS ACLARATORIAS")) {
					Date fechaTarea=JsfUtil.getFecha(bandejaTareasBean.getTarea().getActivationDate(), "dd/MM/yyyy h:mm a");  
					Date fechaMaxima=JsfUtil.sumarDiasAFecha(fechaTarea, 18);
						
					verProrroga=fechaMaxima.before(new Date());
					System.out.println("Ver Prorroga"+ verProrroga);
				}
			}
			if(nombreTarea.contains("REVISAR INFORMACION") || nombreTarea.contains("REVISAR INFORMACIÓN")) {				
				verObservaciones=true;
				editarObservaciones=true;
			}
			
		} catch (JbpmException e) {
			LOG.error("Error al recuperar tarea "+e.getCause()+" "+e.getMessage());
		}
	}
	
	private void cargarDatosOperador() throws ServiceException{	
		
		usuarioOperador=usuarioFacade.buscarUsuario(varUsuarioOperador);
		rucCedula=usuarioOperador.getNombre();
				
		Organizacion orga= organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
		nombreRazonSocial=orga!=null?orga.getNombre():usuarioOperador.getPersona().getNombre();	
		
		cedulaPersona=orga!=null?orga.getPersona().getPin():usuarioOperador.getPersona().getPin();
		nombrePersona=orga!=null?orga.getPersona().getNombre():usuarioOperador.getPersona().getNombre();
		
		provincia=usuarioOperador.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
		canton=usuarioOperador.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
		parroquia=usuarioOperador.getPersona().getUbicacionesGeografica().getNombre();
		
		List<Contacto> contactos=orga!=null?contactoFacade.buscarPorOrganizacion(orga):contactoFacade.buscarPorPersona(usuarioOperador.getPersona());
		for (Contacto contacto : contactos) {
			switch (contacto.getFormasContacto().getId()) {
			case FormasContacto.TELEFONO:
				telefono=contacto.getValor();
				break;
			case FormasContacto.DIRECCION:
				direccion=contacto.getValor();
				break;
			case FormasContacto.EMAIL:
				correo=contacto.getValor();
				break;	

			default:
				break;
			} 
		}		
	}
	
	private void cargarDatosProyecto() throws ServiceException{
		if(varTramite.startsWith("MAE-RSQ-")
			|| varTramite.startsWith("MAAE-RSQ-")
			|| varTramite.startsWith("MAATE-RSQ-")) {
			registroSustanciaQuimica=registroSustanciaQuimicaFacade.obtenerRegistroPorCodigo(varTramite);
			if(registroSustanciaQuimica == null){
				registroSustanciaQuimica=registroSustanciaQuimicaFacade.obtenerRegistroPorCodigoDRSQ(varTramite);
			}
			
			proyectoLicenciaCoa=registroSustanciaQuimica.getProyectoLicenciaCoa();
		}else {
			proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyecto(varTramite);
			registroSustanciaQuimica=registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
		}
		
		if(registroSustanciaQuimica != null && registroSustanciaQuimica.getId() != null){
			responsableSustanciaQuimicaLista=responsableSustanciaQuimicaFacade.obtenerResponsablePorRsq(registroSustanciaQuimica);
		}else{
			responsableSustanciaQuimicaLista = new ArrayList<ResponsableSustanciaQuimica>();
		}
		
		AprobacionRequisitosTecnicos art=aprobacionRequisitosTecnicosFacade.getAprobacionRequisitosTecnicos(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		
		if(registroSustanciaQuimica!=null) {
			
			actividadSustanciaProyectoLista=actividadSustanciaQuimicaFacade.obtenerActividadesPorRSQ(registroSustanciaQuimica);
			ubicacionSustanciaProyectoLista=ubicacionSustanciaQuimicaFacade.obtenerUbicacionesPorRSQ(registroSustanciaQuimica);
		
			documentoEmisionRsq=documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_EMISION_RSQ_INSTITUCION_EDUCATIVA, RegistroSustanciaQuimica.class.getSimpleName(), registroSustanciaQuimica.getId());
			documentoJustificaciones=documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_JUSTIFICAIONES_ADICIONALES, RegistroSustanciaQuimica.class.getSimpleName(), registroSustanciaQuimica.getId());
			
			if(verObservaciones) {
				informeOficioRSQLista=informesOficiosRSQFacade.obtenerPorRSQLista(registroSustanciaQuimica, TipoInformeOficioEnum.INFORME_INSPECCION, numeroRevision);
				
				for (InformeOficioRSQ item : informeOficioRSQLista) {
					//Informe de Inspeccion del Tecnico Principal
					if(item.getArea().getId().intValue()==registroSustanciaQuimica.getArea().getId().intValue()) {
						documentoInformeInspeccion=documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_INFORME_INSPECCION_APROBADO_OBSERVADO, InformeOficioRSQ.class.getSimpleName(), item.getId());	
					}
					
					//Informes de Inspeccion  de los Tecnicos de apoyo
					item.setDocumento(documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_INFORME_INSPECCION_APROBADO_OBSERVADO, InformeOficioRSQ.class.getSimpleName(), item.getId()));
				}
				
				//Documentos de Justificaion a los Informes Inspeccion  de los Tecnicos de apoyo
				for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
					item.setDocumentoJustificativo(documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_JUSTIFICAIONES_ADICIONALES, UbicacionSustancia.class.getSimpleName(), item.getId()));
				}
			}
			
			
		}else {
			registroSustanciaQuimica=new RegistroSustanciaQuimica(proyectoLicenciaCoa);
		}			
		
		gestionarProductosQuimicosProyectoAmbientalLista=new ArrayList<>();
		List<GestionarProductosQuimicosProyectoAmbiental> sustanciasProyectoList=sustanciasProyectoFacade.listaSustanciasQuimicas(proyectoLicenciaCoa);
		
		if(sustanciasProyectoList == null || sustanciasProyectoList.isEmpty()){
			sustanciasProyectoList=sustanciasProyectoFacade.listaSustanciasQuimicasTransporta(proyectoLicenciaCoa);
		}		
		
		for (GestionarProductosQuimicosProyectoAmbiental sustanciaProyecto : sustanciasProyectoList) {
			if(sustanciaProyecto.getSustanciaquimica().getControlSustancia()!=null 
			&& sustanciaProyecto.getSustanciaquimica().getControlSustancia())//verificar el estado para identificar las sustancias permitidas
				gestionarProductosQuimicosProyectoAmbientalLista.add(sustanciaProyecto);
		}
//		gestionarProductosQuimicosProyectoAmbientalSeleccionado=gestionarProductosQuimicosProyectoAmbientalLista.get(0);
		
		boolean listaVacia = false;
		
		if(actividadSustanciaProyectoLista.isEmpty()) {
			listaVacia = true;
			List<CaracteristicaActividad> caracteristicaActividadList= actividadSustanciaQuimicaFacade.obtenerCaracteristicasActividades();
			for (GestionarProductosQuimicosProyectoAmbiental sustancia : sustanciasProyectoList) {				
				for (CaracteristicaActividad caracteristicaActividad : caracteristicaActividadList) {
					ActividadSustancia actividad=new ActividadSustancia(registroSustanciaQuimica,sustancia,caracteristicaActividad);
					actividadSustanciaProyectoLista.add(actividad);
				}
			}
		}
		
		actividadesNivel1=new ArrayList<>();
		actividadesNivel2=new ArrayList<>();
		actividadesNivel3=new ArrayList<>();
		
		List<GestionarProductosQuimicosProyectoAmbiental> sustanciasActividades = new ArrayList<>();
		
		for (ActividadSustancia actividad : actividadSustanciaProyectoLista) {
			if(!listaVacia && !sustanciasActividades.contains(actividad.getGestionarProductosQuimicosProyectoAmbiental())){
				sustanciasActividades.add(actividad.getGestionarProductosQuimicosProyectoAmbiental());
			}
			
			if(actividad.getCaracteristicaActividad().getActividadNivel().getNivel()==1 && !actividadesNivel1.contains(actividad.getCaracteristicaActividad().getActividadNivel()))
				actividadesNivel1.add(actividad.getCaracteristicaActividad().getActividadNivel());
			if(actividad.getCaracteristicaActividad().getActividadNivel().getNivel()==2 && !actividadesNivel2.contains(actividad.getCaracteristicaActividad().getActividadNivel()))
				actividadesNivel2.add(actividad.getCaracteristicaActividad().getActividadNivel());
			if(actividad.getCaracteristicaActividad().getActividadNivel().getNivel()==3 && 
					!actividadesNivel3.contains(actividad.getCaracteristicaActividad().getActividadNivel()))
				actividadesNivel3.add(actividad.getCaracteristicaActividad().getActividadNivel());
		}		
		
		for(GestionarProductosQuimicosProyectoAmbiental sustanciaUno : gestionarProductosQuimicosProyectoAmbientalLista){
			for(GestionarProductosQuimicosProyectoAmbiental sustanciaDos : sustanciasActividades){
				if(sustanciaUno.equals(sustanciaDos)){
					sustanciaUno.setInformacionIngresada(true);
				}
			}
		}
		
		for (ResponsableSustanciaQuimica responsable : responsableSustanciaQuimicaLista) {
			responsable.setDocumentoResponsabilidadTec(documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_RESPONSABILIDAD_TECNICO, ResponsableSustanciaQuimica.class.getSimpleName(), responsable.getId()));
			responsable.setDocumentoTitProfesional(documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_TITULO_PROFESIONAL_TECNICO, ResponsableSustanciaQuimica.class.getSimpleName(), responsable.getId()));
			responsable.setDocumentoTitArtesanal(documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_TITULO_ARTESANAL_TECNICO, ResponsableSustanciaQuimica.class.getSimpleName(), responsable.getId()));
			
			String[] sustanciaArray=responsable.getSustanciasQuimicas().split(";");
			String sustancias = "";
			for (String sus : sustanciaArray) {
				GestionarProductosQuimicosProyectoAmbiental sustanciasR =sustanciasProyectoFacade.buscarPorId(Integer.valueOf(sus));
				sustancias += sustancias.equals("") ? sustanciasR.getSustanciaquimica().getDescripcion() : " " + sustanciasR.getSustanciaquimica().getDescripcion();
			}
			
			responsable.setSustancias(sustancias);
		}
		
		if(art!=null) {
			sustanciasVehiculoTransporteLista=sustanciaQuimicaPeligrosaTransporteFacade.getListaSustanciaQuimicaPeligrosaTransporte(art.getId());
		}
		
		coordinatesWrappersPre = new ArrayList<CoordendasPoligonos>();
		if (proyectoLicenciaCoa != null && proyectoLicenciaCoa.getId() != null) {
			List<ProyectoLicenciaAmbientalCoaShape> formasImplantacion = proyectoLicenciaAmbientalShapeFacade
					.buscarFormaGeograficaPorProyecto(proyectoLicenciaCoa, 1, 0); // coordenadas
																					// implantacion

			if (formasImplantacion == null) {
				formasImplantacion = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();
			} else {
				for (ProyectoLicenciaAmbientalCoaShape forma : formasImplantacion) {
					List<CoordenadasProyecto> coordenadasGeograficasImplantacion = coordenadasProyectoCoaFacade
							.buscarPorForma(forma);

					CoordendasPoligonos poligono = new CoordendasPoligonos();
					poligono.setCoordenadas(coordenadasGeograficasImplantacion);
					poligono.setTipoForma(forma.getTipoForma());

					coordinatesWrappersPre.add(poligono);

					for (CoordenadasProyecto coordenada : coordenadasGeograficasImplantacion) {
						coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString() + " "
								+ coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString(): ","	+ coordenada.getX().setScale(5,	RoundingMode.HALF_DOWN).toString()
										+ " " + coordenada.getY().setScale(5,RoundingMode.HALF_DOWN).toString();
					}
				}
			}
		}
		 
		 Organizacion orga= organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
		 
		 if(orga != null){
				
				Persona persona = orga.getPersona();			
				
//				List<Contacto> contactosRep=contactoFacade.buscarPorPersona(persona);
				
				registroSustanciaQuimica.setIdentificacionRepLegal(persona.getPin());
				registroSustanciaQuimica.setNombreRepLegal(persona.getNombre());
				
				
//				for (Contacto contacto : contactosRep) {
//					switch (contacto.getFormasContacto().getId()) {
//					case FormasContacto.TELEFONO:
//						registroSustanciaQuimica.setTelefonoRepLegal(contacto.getValor());					
//						break;
//					case FormasContacto.DIRECCION:
//						registroSustanciaQuimica.setDireccionRepLegal(contacto.getValor());
//						break;
//					case FormasContacto.EMAIL:
//						registroSustanciaQuimica.setCorreoRepLegal(contacto.getValor());
//						break;	
//
//					default:
//						break;
//					} 
//				}			
			}else{
				registroSustanciaQuimica.setIdentificacionRepLegal(cedulaPersona);
				registroSustanciaQuimica.setNombreRepLegal(nombrePersona);
				
//				registroSustanciaQuimica.setTelefonoRepLegal(telefono);
//				registroSustanciaQuimica.setDireccionRepLegal(direccion);
//				registroSustanciaQuimica.setCorreoRepLegal(correo);
			}
	}
	
	public void iniciarCorrecciones() {
		msgCorrecciones=false;
	}
	
	public boolean esART() {
		if(sustanciasVehiculoTransporteLista!=null && !sustanciasVehiculoTransporteLista.isEmpty()) {
			for (GestionarProductosQuimicosProyectoAmbiental sustanciaRsq : gestionarProductosQuimicosProyectoAmbientalLista) {
				for (SustanciaQuimicaPeligrosaTransporte sustanciaArt : sustanciasVehiculoTransporteLista) {
					if(sustanciaRsq.getSustanciaquimica().getDescripcion().toUpperCase().compareTo(sustanciaArt.getSustanciaQuimicaPeligrosa().getDescripcion().toUpperCase())==0){
						return true;
					}
				}
			}
		}		
		return false;
	}
	
	public boolean verJustificativo() {
		if(verObservaciones && !ubicacionSustanciaProyectoLista.isEmpty()) {
			for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
				if(item.getNecesitaInspeccion()!=null && item.getNecesitaInspeccion()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public InformeOficioRSQ buscarInforme(UbicacionSustancia ubicacionSustancia) {
		try {			
			for (InformeOficioRSQ item : informeOficioRSQLista) {
				if(ubicacionSustancia.getArea()!=null && ubicacionSustancia.getArea().getId().intValue()==item.getArea().getId().intValue()) {
					return item;					
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent descargarDocumento(UbicacionSustancia ubicacionSustancia) {
		InformeOficioRSQ informe=buscarInforme(ubicacionSustancia);
		if(informe!=null)
			return descargarDocumento(informe.getDocumento());
		return null;
	}
	
	private StreamedContent descargarDocumento(DocumentosSustanciasQuimicasRcoa documento) {
		try {
			if(documento.getContenidoDocumento()==null) {
				documento.setContenidoDocumento(documentosRSQFacade.descargar(documento.getIdAlfresco()));
			}
			
            byte[] byteFile = documento.getContenidoDocumento();
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                return new DefaultStreamedContent(is, "application/pdf", documento.getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}
	
	public List<ActividadSustancia> getCaracteristicaActividad(ActividadNivel actividadNivel){
		List<ActividadSustancia> lista=new ArrayList<>();
		for (ActividadSustancia actividad : actividadSustanciaProyectoLista) {
			if(gestionarProductosQuimicosProyectoAmbientalSeleccionado!=null
					&& actividad.getCaracteristicaActividad().getActividadNivel().equals(actividadNivel)
					&& actividad.getGestionarProductosQuimicosProyectoAmbiental().equals(gestionarProductosQuimicosProyectoAmbientalSeleccionado)
					&& !lista.contains(actividad))
				lista.add(actividad);
		}
		return lista;
	}
	
	
	
	public boolean habilitarActividad(ActividadSustancia actividad) {
		
		if(!habilitarIngreso)
			return false;
		
		if(actividad.getCaracteristicaActividad().getTipo()==0)
			return false;
		
		if(!esART()) {
			String nombreActividad=actividad.getCaracteristicaActividad().getNombre().toUpperCase();
			if(actividad.getCaracteristicaActividad().getActividadNivel().getNombreActividad().toUpperCase().contains(TRANSPORTE)
			&& nombreActividad.contains(TRANSPORTE)) {
				return false;
			}
		}
		// verifico si existe la sustancia en art y es la actividad de trasnporte
		if(!existeEnART(actividad)){
			return false;
		}
		return true;
	}
	
	public boolean existeEnART(ActividadSustancia actividad) {
		String nombreActividad=actividad.getCaracteristicaActividad().getNombre().toUpperCase();
		if(actividad.getCaracteristicaActividad().getActividadNivel().getNombreActividad().toUpperCase().contains(TRANSPORTE)
				&& nombreActividad.contains(TRANSPORTE)) {
			if(sustanciasVehiculoTransporteLista==null||sustanciasVehiculoTransporteLista.isEmpty())
				return true;
			for (SustanciaQuimicaPeligrosaTransporte actividadSustancia : sustanciasVehiculoTransporteLista) {
				if(gestionarProductosQuimicosProyectoAmbientalSeleccionado.getSustanciaquimica().getDescripcion().toUpperCase().equals(actividadSustancia.getSustanciaQuimicaPeligrosa().getDescripcion().toUpperCase())){
					return true;
				}
			}
			return false;
		}
		return true;
	}		
	
	/**
	 * Buscar actividad de la sustancia selccionada
	 * @param sustancia,actividadNivel,nombreActividad
	 * @return
	 */
	public ActividadSustancia buscarActividad(GestionarProductosQuimicosProyectoAmbiental sustancia,String actividadNivel,String nombreActividad){
		for (ActividadSustancia actividad : actividadSustanciaProyectoLista) {
			if(sustancia!=null					
					&& actividad.getGestionarProductosQuimicosProyectoAmbiental().equals(sustancia)
					&& actividad.getCaracteristicaActividad().getNombre().toUpperCase().contains(nombreActividad.toUpperCase())
					&& actividad.getCaracteristicaActividad().getActividadNivel().getNombreActividad().toUpperCase().contains(actividadNivel.toUpperCase()))
				return actividad;
		}
		return null;
	}
	
	List<String> listaSeleccion = new ArrayList<>();
	public void actividadListener(ActividadSustancia actividad) {
		
		String nombreActividad=actividad.getCaracteristicaActividad().getNombre().toUpperCase();
		if(actividad.getCaracteristicaActividad().getActividadNivel().getNombreActividad().toUpperCase().contains("USO")
		&& (nombreActividad.contains(ESTUDIANTES)||nombreActividad.contains(EDUCATIVA)||nombreActividad.contains("INDUSTRIAL"))) {
			if(actividad.getActividadSeleccionada()) {
				ActividadSustancia actividadModificar=buscarActividad(gestionarProductosQuimicosProyectoAmbientalSeleccionado,"USO",ALMACENAMIENTO_PROPIO);
				if(actividadModificar!=null) {
					actividadModificar.setActividadSeleccionada(true);
					listaSeleccion.add(actividadModificar.getDescripcion());
				}					
			}else{
				ActividadSustancia actividadModificar=buscarActividad(gestionarProductosQuimicosProyectoAmbientalSeleccionado,"USO",ALMACENAMIENTO_PROPIO);
				if(actividadModificar!=null) {
					
					ActividadSustancia actividadVerificar1=buscarActividad(gestionarProductosQuimicosProyectoAmbientalSeleccionado,"USO",ESTUDIANTES);
					ActividadSustancia actividadVerificar2=buscarActividad(gestionarProductosQuimicosProyectoAmbientalSeleccionado,"USO",EDUCATIVA);
					ActividadSustancia actividadVerificar3=buscarActividad(gestionarProductosQuimicosProyectoAmbientalSeleccionado,"USO", "INDUSTRIAL");
					
					if((actividadVerificar1 == null || !actividadVerificar1.getActividadSeleccionada()) && 
							(actividadVerificar2 == null || !actividadVerificar2.getActividadSeleccionada()) && 
							(actividadVerificar3 == null || !actividadVerificar3.getActividadSeleccionada())){
						actividadModificar.setActividadSeleccionada(false);		
						listaSeleccion.remove(actividadModificar.getDescripcion());
					}
				}
			}
		}else{
			ActividadSustancia actividadModificar=buscarActividad(gestionarProductosQuimicosProyectoAmbientalSeleccionado,"USO",ALMACENAMIENTO_PROPIO);
			if(actividadModificar!=null) {
				if(!listaSeleccion.isEmpty()){
					actividadModificar.setActividadSeleccionada(false);
					listaSeleccion = new ArrayList<String>();
				}
			}
		}
		
		if(actividad.getCaracteristicaActividad().getActividadNivel().getNombreActividad().toUpperCase().contains("USO")
		&& nombreActividad.contains(ALMACENAMIENTO_PROPIO)) {
			if(!actividad.getActividadSeleccionada()) {
				ActividadSustancia actividadVerificar1=buscarActividad(gestionarProductosQuimicosProyectoAmbientalSeleccionado,"USO",ESTUDIANTES);
				ActividadSustancia actividadVerificar2=buscarActividad(gestionarProductosQuimicosProyectoAmbientalSeleccionado,"USO",EDUCATIVA);
				ActividadSustancia actividadVerificar3=buscarActividad(gestionarProductosQuimicosProyectoAmbientalSeleccionado,"USO","INDUSTRIAL");
				if((actividadVerificar1!=null && actividadVerificar1.getActividadSeleccionada())
				 ||(actividadVerificar2!=null && actividadVerificar2.getActividadSeleccionada()) 
				 ||(actividadVerificar3!=null && actividadVerificar3.getActividadSeleccionada())) {
					actividad.setActividadSeleccionada(true);			
				}
			}			
		}
	}
	
	public void validarActividades(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(gestionarProductosQuimicosProyectoAmbientalSeleccionado != null){
//		for (GestionarProductosQuimicosProyectoAmbiental sustancia : gestionarProductosQuimicosProyectoAmbientalLista) {
			GestionarProductosQuimicosProyectoAmbiental sustancia = gestionarProductosQuimicosProyectoAmbientalSeleccionado;
			String nombreSustancia=sustancia.getSustanciaquimica().getDescripcion();
			
			//Validar Actividad Almacenamiento
			ActividadSustancia almacenamientoPropio=buscarActividad(sustancia,ALMACENAMIENTO,ALMACENAMIENTO_PROPIO);
			ActividadSustancia almacenamientoPrestacion=buscarActividad(sustancia,ALMACENAMIENTO,"SERVICIO DE ALMACENAMIENTO");
			ActividadSustancia almacenamientoContratacion=buscarActividad(sustancia,"CONTRATAR",ALMACENAMIENTO);
			
//			if(!almacenamientoPropio.getActividadSeleccionada()
//			 && !almacenamientoPrestacion.getActividadSeleccionada()
//			 && !almacenamientoContratacion.getActividadSeleccionada()) {
//				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,nombreSustancia+": Debe llenar el campo de Contratación servicio de almacenamiento", null));
//			}
			
			//Validar Actividad Transporte
			ActividadSustancia transportePropio=buscarActividad(sustancia,TRANSPORTE,"TRANSPORTE PROPIO");
			ActividadSustancia transportePrestacion=buscarActividad(sustancia,TRANSPORTE,"SERVICIO DE TRANSPORTE");
			ActividadSustancia transporteContratacion=buscarActividad(sustancia,"CONTRATAR",TRANSPORTE);
			
			if(!transportePropio.getActividadSeleccionada()
			 && !transportePrestacion.getActividadSeleccionada()
			 && !transporteContratacion.getActividadSeleccionada()) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,nombreSustancia+": Debe llenar el campo de Contratación servicio de transporte", null));
			}
			
			//Validar Actividad USO
			ActividadSustancia usoEstudiante=buscarActividad(sustancia,"USO",ESTUDIANTES);
			ActividadSustancia usoInsEducatica=buscarActividad(sustancia,"USO",EDUCATIVA);
			ActividadSustancia usoAlmacenamiento=buscarActividad(sustancia,"USO",ALMACENAMIENTO_PROPIO);
			
			if((usoEstudiante.getActividadSeleccionada()
			 || usoInsEducatica.getActividadSeleccionada())
			 && !usoAlmacenamiento.getActividadSeleccionada()) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,nombreSustancia+": Debe Seleccionar la actividad Uso: Almacenamiento propio", null));
			}
//		}	
			
			
			if (!errorMessages.isEmpty()){
				throw new ValidatorException(errorMessages);
			}else{
				gestionarProductosQuimicosProyectoAmbientalSeleccionado.setInformacionIngresada(true);
			}
		
			setGestionarProductosQuimicosProyectoAmbientalSeleccionado(null);
		}
	}
	
	public List<UbicacionesGeografica> getCantonesLista(){
		if(provinciaSeleccionada!=null)
			return ubicacionGeograficaFacade.getUbicacionPadre(provinciaSeleccionada);
		else
			return new ArrayList<UbicacionesGeografica>();
	}
	
	public void crearUbicacionSustancia(){
		ubicacionSustanciaProyecto=new UbicacionSustancia(registroSustanciaQuimica);
		provinciaSeleccionada=null;
	}
	
	public void agregarUbicacionSustancia(){		
		ubicacionSustanciaProyecto.setRegistroEdicion(false);
		if(!ubicacionSustanciaProyectoLista.contains(ubicacionSustanciaProyecto))
			ubicacionSustanciaProyectoLista.add(ubicacionSustanciaProyecto);
	}
	
	public void editarUbicacionSustancia(UbicacionSustancia item){
		ubicacionSustanciaProyecto=item;
		provinciaSeleccionada=item.getUbicacionesGeografica().getUbicacionesGeografica();
		ubicacionSustanciaProyecto.setRegistroEdicion(true);
	}
	
	public void eliminarUbicacionSustancia(UbicacionSustancia item){
		if(item.getId()!=null)
			ubicacionSustanciaProyectoEliminadosLista.add(item);
		ubicacionSustanciaProyectoLista.remove(item);		
	}
	
	public List<UbicacionSustancia> ubicacionSustanciaProyectoListaCupo(){
		List<UbicacionSustancia> lista=new ArrayList<>();
		for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
			String nombreSustancia= item.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion().toUpperCase();
			if(nombreSustancia.contains(MERCURIO) || actividadNivel2(item.getGestionarProductosQuimicosProyectoAmbiental())) {
				lista.add(item);
			}			
		}
		
		return lista;
	}
			
	public void editarJustificativo(UbicacionSustancia item){
		ubicacionSustanciaProyecto=item;
	}
	
	public void guardarJustificativo(){
		if(ubicacionSustanciaProyecto.getDocumentoJustificativo()!=null && ubicacionSustanciaProyecto.getDocumentoJustificativo().isContenidoActualizado()) {
			try {
				documentosRSQFacade.guardarDocumento(varTramite, ubicacionSustanciaProyecto.getDocumentoJustificativo(), ubicacionSustanciaProyecto.getId());
				ubicacionSustanciaProyecto.getDocumentoJustificativo().setContenidoActualizado(false);
			}catch (Exception e) {				
				LOG.error("ERROR: No se guardo el documento justificativo. "+e.getMessage());
			}			
		}
		
	}
	
	public List<String> getUbicacionSustancia(GestionarProductosQuimicosProyectoAmbiental sustancia,boolean soloBodega){
		List<String> lista=new ArrayList<>();
		if(sustancia!=null) {
			for (UbicacionSustancia ubicacion : ubicacionSustanciaProyectoLista) {
				if(ubicacion.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getId().intValue()==sustancia.getSustanciaquimica().getId().intValue()) {
					String[] lugarArray=ubicacion.getLugar().split(";");
					for (String lugar : lugarArray) {
						if(!soloBodega || (soloBodega && lugar.toUpperCase().contains(BODEGA))) {
							lista.add(lugar+" "+ubicacion.getUbicacionesGeografica().getNombre());
						}					
					}
				}	
			}
		}		
		return lista;
	}
	
	public String getLabelLugares(){		
		if(ubicacionSustanciaProyecto==null || ubicacionSustanciaProyecto.getLugares().isEmpty()) {
			return "Seleccione";
		}else {
			return ubicacionSustanciaProyecto.getLugares();
		}		
		
	}
	
	public void validarUbicaciones(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(!validarUbicaciones()) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,"Debe seleccionar al menos 1 ubicación por cada sustancia.", null));
		}		
						
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void validarUbicacionRepetida(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(ubicacionSustanciaProyecto.getUbicacionesGeografica() == null){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,"Ingrese coordenadas correctas del punto geográfico", null));
		}
		
		if(ubicacionSustanciaProyectoLista != null && !ubicacionSustanciaProyectoLista.isEmpty()){
			for (UbicacionSustancia ubicacion : ubicacionSustanciaProyectoLista) {
//				if(!ubicacion.equals(ubicacionSustanciaProyecto)
//				&& ubicacion.getGestionarProductosQuimicosProyectoAmbiental().equals(ubicacionSustanciaProyecto.getGestionarProductosQuimicosProyectoAmbiental())
//				&& ubicacion.getUbicacionesGeografica().equals(ubicacionSustanciaProyecto.getUbicacionesGeografica())){
//					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,"Esta ubicacion ya se encuentra agregada.", null));
//					break;
//				}
				
				if(!ubicacion.equals(ubicacionSustanciaProyecto) && 
						ubicacion.getLugar().equals(ubicacionSustanciaProyecto.getLugar()) && 
						ubicacion.getUbicacionesGeografica().equals(ubicacionSustanciaProyecto.getUbicacionesGeografica()) &&
						ubicacion.getCoordenadaX().equals(ubicacionSustanciaProyecto.getCoordenadaX()) && 
						ubicacion.getCoordenadaY().equals(ubicacionSustanciaProyecto.getCoordenadaY()) &&
						ubicacion.getTelefono().equals(ubicacionSustanciaProyecto.getTelefono()) && 
						ubicacion.getCorreo().equals(ubicacionSustanciaProyecto.getCorreo())){
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,"Esta ubicacion ya se encuentra agregada.", null));
					break;
				}
				
			}
		}		
						
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	/**
	 * Buscar si selecciona solamente actividades de Importacion o Exportacion
	 * en el nivel 1
	 * @return boolean
	 */
	public boolean actividadExpImp() {	
		for (GestionarProductosQuimicosProyectoAmbiental sustancia : gestionarProductosQuimicosProyectoAmbientalLista) {
			if(!actividadExpImp(sustancia)) {
				return false;
			}
		}
		return true;		
	}
	
	private boolean actividadExpImp(GestionarProductosQuimicosProyectoAmbiental sustancia) {
		
		ActividadSustancia actividadImporta=buscarActividad(sustancia,"ABASTECIMIENTO","IMPORTA");
		ActividadSustancia actividadExporta=buscarActividad(sustancia,"EXPORTA","EXPORTA");
//		ActividadSustancia actividadAlmacenamiento=buscarActividad(sustancia,"ALMACENAMIENTO","ALMACENAMIENTO PROPIO");
//		ActividadSustancia actividadPrestacion=buscarActividad(sustancia,"ALMACENAMIENTO","PRESTACIÓN DE SERVICIO DE ALAMACENAMIENTO");
		
		if(actividadImporta.getActividadSeleccionada()||actividadExporta.getActividadSeleccionada()) {
			for (ActividadSustancia item : actividadSustanciaProyectoLista) {
				if(item.getActividadSeleccionada() 
				&& item.getGestionarProductosQuimicosProyectoAmbiental().getId().intValue()==sustancia.getId().intValue()
 				&& item.getCaracteristicaActividad().getActividadNivel().getNivel()==1 
				&& !item.getCaracteristicaActividad().getNombre().toUpperCase().contains("IMPORTA")
				&& !item.getCaracteristicaActividad().getNombre().toUpperCase().contains("EXPORTA")){
					return false;
				}
			}
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Buscar si selecciona la actividad de Estudiante
	 * @return boolean
	 */
	public boolean actividadUsoEstudiante() {
		for (GestionarProductosQuimicosProyectoAmbiental sustancia : gestionarProductosQuimicosProyectoAmbientalLista) {
			ActividadSustancia actividadEstudiante=buscarActividad(sustancia,"USO",ESTUDIANTE);
			if(actividadEstudiante!=null && actividadEstudiante.getActividadSeleccionada()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean actividadUsoEstudianteTodas() {
		for (GestionarProductosQuimicosProyectoAmbiental sustancia : gestionarProductosQuimicosProyectoAmbientalLista) {
			ActividadSustancia actividad=buscarActividad(sustancia, "USO", ESTUDIANTE);
			if(actividad.getActividadSeleccionada()==null||!actividad.getActividadSeleccionada()) {
				return false;
			}			
		}		
		
		for (ActividadSustancia item : actividadSustanciaProyectoLista) {		
			if(item.getActividadSeleccionada() 
			&& (item.getCaracteristicaActividad().getActividadNivel().getNivel()==1 ||item.getCaracteristicaActividad().getActividadNivel().getNivel()==2)
			&& !item.getCaracteristicaActividad().getNombre().toUpperCase().contains(ESTUDIANTE)
			&& !item.getCaracteristicaActividad().getNombre().toUpperCase().contains(ALMACENAMIENTO_PROPIO)){
				return false;
			}
		}
		
		return true;		
		
	}
	
	private boolean actividadNivel2(GestionarProductosQuimicosProyectoAmbiental sustancia) {
		
		for (ActividadSustancia item : actividadSustanciaProyectoLista) {
			if(item.getActividadSeleccionada() 
			&& item.getCaracteristicaActividad().getActividadNivel().getNivel()==2
			&& item.getGestionarProductosQuimicosProyectoAmbiental().getId().intValue()==sustancia.getId().intValue()){
				return true;
			}
		}
		return false;
	}
	
	private boolean actividadNivel2() {
		
		for (GestionarProductosQuimicosProyectoAmbiental sustancia : gestionarProductosQuimicosProyectoAmbientalLista) {
			if(actividadNivel2(sustancia)) {
				return true;
			}			
		}
		return false;
	}
	
	public boolean bloquearMercurio() {
		
		for (GestionarProductosQuimicosProyectoAmbiental sustancia : gestionarProductosQuimicosProyectoAmbientalLista) {
			if(sustancia.getSustanciaquimica().getDescripcion().toUpperCase().contains(MERCURIO)) {
				if(habilitarIngreso){
					return false;
				}else{
					return true;	
				}
			}
		}
		return true;	
	}
		
	
	public boolean bloquearCupo(ActividadSustancia actividadSustancia){
		String nombreSustancia=actividadSustancia.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion().toUpperCase();
		if(actividadExpImp(actividadSustancia.getGestionarProductosQuimicosProyectoAmbiental()) && !nombreSustancia.contains(MERCURIO) && !actividadNivel2()) {
			return true;
		}
		return false;
	}
	
	public boolean bloquearProceso(UbicacionSustancia ubicacionSustancia){
		String nombreSustancia=ubicacionSustancia.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion().toUpperCase();
		if(actividadExpImp(ubicacionSustancia.getGestionarProductosQuimicosProyectoAmbiental()) && !nombreSustancia.contains(MERCURIO)) {
			return true;
		}
		return false;
	}
	
	public void fileUploadEmisionRsqListener(FileUploadEvent event) {
		if(documentoEmisionRsq==null) {
			documentoEmisionRsq=new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_EMISION_RSQ_INSTITUCION_EDUCATIVA, RegistroSustanciaQuimica.class.getSimpleName(), registroSustanciaQuimica.getId(), bandejaTareasBean.getTarea().getProcessInstanceId());
		}
		documentoEmisionRsq.cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());
	}
	
	public void fileUploadTitProfesionalListener(FileUploadEvent event) {
		if(responsable.getDocumentoTitProfesional()==null) {
			responsable.setDocumentoTitProfesional(new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_TITULO_PROFESIONAL_TECNICO, ResponsableSustanciaQuimica.class.getSimpleName(), responsable.getId(), bandejaTareasBean.getTarea().getProcessInstanceId()));
		}
		responsable.getDocumentoTitProfesional().cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());
	}
	
	public void fileUploadTitArtesanalListener(FileUploadEvent event) {		
		if(responsable.getDocumentoTitArtesanal()==null) {
			responsable.setDocumentoTitArtesanal(new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_TITULO_ARTESANAL_TECNICO, ResponsableSustanciaQuimica.class.getSimpleName(), responsable.getId(), bandejaTareasBean.getTarea().getProcessInstanceId()));
		}
		responsable.getDocumentoTitArtesanal().cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());
	}	
	
	public void fileUploadResponsabilidadTeclListener(FileUploadEvent event) {		
		if(responsable.getDocumentoResponsabilidadTec()==null) {
			responsable.setDocumentoResponsabilidadTec(new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_RESPONSABILIDAD_TECNICO, ResponsableSustanciaQuimica.class.getSimpleName(), responsable.getId(), bandejaTareasBean.getTarea().getProcessInstanceId()));
		}
		responsable.getDocumentoResponsabilidadTec().cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());
	}
	
	public void fileUploadJustificativoListener(FileUploadEvent event) {		
		if(ubicacionSustanciaProyecto.getDocumentoJustificativo()==null) {
			ubicacionSustanciaProyecto.setDocumentoJustificativo(new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_JUSTIFICAIONES_ADICIONALES, UbicacionSustancia.class.getSimpleName(), ubicacionSustanciaProyecto.getId(), bandejaTareasBean.getTarea().getProcessInstanceId()));
		}
		ubicacionSustanciaProyecto.getDocumentoJustificativo().cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());
	}
	
	public void fileUploadJustificacionesAdicionalesListener(FileUploadEvent event) {
		if(documentoJustificaciones==null) {
			documentoJustificaciones=new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_JUSTIFICAIONES_ADICIONALES, RegistroSustanciaQuimica.class.getSimpleName(), registroSustanciaQuimica.getId(), bandejaTareasBean.getTarea().getProcessInstanceId());
		}		
		documentoJustificaciones.cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());
	}
	
	public void fileUploadProrrogaListener(FileUploadEvent event) {
		if(documentoProrroga==null) {
			documentoProrroga=new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_JUSTIFICACION_PRORROGA, RegistroSustanciaQuimica.class.getSimpleName(), registroSustanciaQuimica.getId(), bandejaTareasBean.getTarea().getProcessInstanceId());
		}		
		documentoProrroga.cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());
	}
	
	public void validarDocumentoEmisionRsq(FacesContext context, UIComponent validate, Object value) {
		if (documentoEmisionRsq==null || (documentoEmisionRsq.getId()==null && documentoEmisionRsq.getContenidoDocumento()==null)) {			
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Adjuntar un documento válido", null));
		}
	}
	
	public void validarDocumentoTitProfesional(FacesContext context, UIComponent validate, Object value) {
		if (responsable.getNumeroTitulo()==null && responsable.getDocumentoTitProfesional()==null) {			
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Título profesional: Adjuntar un documento válido", null));
		}
	}
	
	public void validarDocumentoTitArtesanal(FacesContext context, UIComponent validate, Object value) {
		if (responsable.getDocumentoTitArtesanal()==null) {			
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Título Artesanal: Adjuntar un documento válido", null));
		}
	}
	
	public void validarDocumentoResponsabilidadTec(FacesContext context, UIComponent validate, Object value) {
		if (responsable.getDocumentoResponsabilidadTec()==null) {			
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Documento de Responsabilidad: Adjuntar un documento válido", null));
		}
	}
	
	public void validarDocumentoProrroga(FacesContext context, UIComponent validate, Object value) {
		if (documentoProrroga==null) {			
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Documento de Prórroga: Adjuntar un documento válido", null));
		}
	}
	
	public void validarCorreoUbicacion(FacesContext context, UIComponent validate, Object value) {
		if (!JsfUtil.validarMail(ubicacionSustanciaProyecto.getCorreo()+"")) {			
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Correo no válido", null));
		}
	}
	
	public void validarCorreoResponsable(FacesContext context, UIComponent validate, Object value) {
		if (!JsfUtil.validarMail(responsable.getCorreo()+"")) {			
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Correo no válido", null));
		}
	}
	
	public void validarNombreResponsableRL(FacesContext context, UIComponent validate, Object value) {
		if (registroSustanciaQuimica.getTipoIdentificacion()!=null
				&& registroSustanciaQuimica.getTipoIdentificacion().getNombre().toUpperCase().contains("CEDULA")
				&& (registroSustanciaQuimica.getNombreRepLegal()==null || registroSustanciaQuimica.getNombreRepLegal().isEmpty())) {			
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Nombre no válido", null));
		}
	}
	
	public void validarNombreResponsable(FacesContext context, UIComponent validate, Object value) {
		if (responsable.getTipoIdentificacion()!=null
				&& responsable.getTipoIdentificacion().getNombre().toUpperCase().contains("CEDULA")
				&& (nombreResponsable==null || nombreResponsable.isEmpty())) {			
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
			"Nombre no válido", null));
		}
	}
	
	public void tipoIdentificaionListenerRL() {
		registroSustanciaQuimica.setIdentificacionRepLegal(null);
		registroSustanciaQuimica.setNombreRepLegal(null);
	}
	
	public void tipoIdentificaionListener() {
		cedulaResponsable=null;
		nombreResponsable=null;
	}
	
	public void crearResponsable(){
		responsable=new ResponsableSustanciaQuimica(registroSustanciaQuimica);
		tipoIdentificaionListener();
		listaSustanciasTecnico = new ArrayList<>();
		lugaresSeleccionados = new ArrayList<String>();
		lugaresArray = new ArrayList<String>();
	}
	
	public void agregarResponsable(boolean respBodega){
		List<CatalogoGeneralCoa> catalogoLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.RSQ_TIPO_RESPONSABLE);
		CatalogoGeneralCoa catalogoResp=null;
		for (CatalogoGeneralCoa catalogo : catalogoLista) {
			if(catalogo.getNombre().toUpperCase().contains(respBodega?BODEGA:TECNICO)) {
				catalogoResp=catalogo;
				break;
			}
		}
		
		responsable.setIdentificacion(cedulaResponsable);
		responsable.setNombre(nombreResponsable);
		responsable.setTipoResponsable(catalogoResp);
		
		String idSustancias = "";
		String nombreSustancia = "";
		for(GestionarProductosQuimicosProyectoAmbiental sustancia : listaSustanciasTecnico){			
			idSustancias += idSustancias.equals("") ? sustancia.getId().toString() : ";" +  sustancia.getId().toString();
			nombreSustancia += nombreSustancia.equals("") ? sustancia.getSustanciaquimica().getDescripcion() : ", " + sustancia.getSustanciaquimica().getDescripcion();
		}		
		
		responsable.setSustanciasQuimicas(idSustancias);
		responsable.setSustancias(nombreSustancia);
		
		String lugares = "";
		for(String lugar : lugaresSeleccionados){
			lugares+= lugares.equals("") ? lugar : ";" + lugar;
		}
		responsable.setLugar(lugares);
		
		
		if(!respBodega && responsable.getTipoIdentificacion().getNombre().toUpperCase().contains("ARTESANAL")) {
			responsable.setIdentificacion(null);
			responsable.setNombre(null);
			responsable.setNumeroTitulo(null);
			responsable.setNombreTitulo(null);
			responsable.setTelefono(null);
			responsable.setCorreo(null);
			responsable.setDireccion(null);
			responsable.setDocumentoTitProfesional(null);
			responsable.setDocumentoResponsabilidadTec(null);		
			responsable.setLugar(null);
		}
		
		if(!responsableSustanciaQuimicaLista.contains(responsable))
			responsableSustanciaQuimicaLista.add(responsable);		
		
		responsable = new ResponsableSustanciaQuimica();
		listaSustanciasTecnico = new ArrayList<>();
		lugaresSeleccionados = new ArrayList<>();
	}
	
	public void editarResponsable(ResponsableSustanciaQuimica item){
		responsable=item;
		cedulaResponsable=responsable.getIdentificacion();
		nombreResponsable=responsable.getNombre();
		
		lugaresSeleccionados = new ArrayList<String>();
		listaSustanciasTecnico = new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
		
		String[] sustanciaArray=responsable.getSustanciasQuimicas().split(";");
		String sustancias = "";
		for (String sus : sustanciaArray) {
			GestionarProductosQuimicosProyectoAmbiental sustanciasR =sustanciasProyectoFacade.buscarPorId(Integer.valueOf(sus));
			listaSustanciasTecnico.add(sustanciasR);
		}
		
		responsable.setSustancias(sustancias);		
		
		obtenerLugares(false);
		
		String[] lugarArray = responsable.getLugaresArray();
		
		for (String lugar : lugarArray) {
			lugaresSeleccionados.add(lugar);
		}
	}
	
	public void eliminarResponsable(ResponsableSustanciaQuimica item){
		if(item.getId()!=null)
			responsableSustanciaQuimicaEliminadosLista.add(item);
		responsableSustanciaQuimicaLista.remove(item);		
	}
	
	private String validarCedula(String cedulaNum){				
		if(JsfUtil.validarCedulaORUC(cedulaNum)) {
			Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, cedulaNum);
			if(cedula != null && cedula.getCedula() != null) {
	           return cedula.getNombre();
			}			
		}
		JsfUtil.addMessageError("Introduzca una cédula válida.");
		return null;       
	}
	
	public void validarCedulaRepLegal(){		
		registroSustanciaQuimica.setNombreRepLegal(validarCedula(registroSustanciaQuimica.getIdentificacionRepLegal()));		        
	}
	
	public void validarCedulaResponsable(){		
		nombreResponsable=validarCedula(cedulaResponsable);
		validarTituloResponsable();
	}
	
	public void validarTituloResponsable(){
		String numerosTitulos=null;
		String nombresTitulos=null;
		GraduadoReporteDTO titulos = consultaRucCedula.obtenerTitulo(cedulaResponsable);
		if(titulos != null && !titulos.getNiveltitulos().isEmpty()) {
           for (NivelTitulosDTO titulo : titulos.getNiveltitulos()) {
        	   numerosTitulos=numerosTitulos==null?"":numerosTitulos+";";
        	   nombresTitulos=nombresTitulos==null?"":nombresTitulos+";";
        	   numerosTitulos+=titulo.getTitulo().get(0).getNumeroRegistro();
        	   nombresTitulos+=titulo.getTitulo().get(0).getNombreTitulo();        	   
           }
           if(nombreResponsable==null ||nombreResponsable.isEmpty()) {
        	   nombreResponsable=titulos.getNiveltitulos().get(0).getTitulo().get(0).getNombres();
           }
		}
		responsable.setNumeroTitulo(numerosTitulos);
		responsable.setNombreTitulo(nombresTitulos);		       
	}
	
	public boolean getMostrarRepresenanteLegal() {
		return !actividadUsoEstudianteTodas();
	}
	
	public boolean getMostrarResponsablesTecnicos() {
		if ((actividadExpImp() && !actividadNivel2()) || actividadUsoEstudiante()) {
			return false;
		}
		return true;
	}
	
	public boolean getMostrarResponsablesBodega() {	
		if (actividadExpImp() || actividadUsoEstudianteTodas()) {
			return false;
		}
		
		if(actividadAlmacenamiento()){
			return true;
		}
		
		for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
			if(item.getLugar().toUpperCase().contains(BODEGA)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean actividadAlmacenamiento(GestionarProductosQuimicosProyectoAmbiental sustancia) {
		
		for (ActividadSustancia item : actividadSustanciaProyectoLista) {
			if(item.getActividadSeleccionada()
			&& item.getGestionarProductosQuimicosProyectoAmbiental().getId().intValue()==sustancia.getId().intValue() 
			&& item.getCaracteristicaActividad().getActividadNivel().getNivel()==1
			&& (item.getCaracteristicaActividad().getNombre().toUpperCase().contains(ALMACENAMIENTO))){				
				return true;
			}
		}
		return false;
	}
	
	private boolean actividadAlmacenamiento() {
		
		for (GestionarProductosQuimicosProyectoAmbiental sustancia : gestionarProductosQuimicosProyectoAmbientalLista) {
			if(actividadAlmacenamiento(sustancia)) {
				return true;
			}			
		}
		return false;
	}
	
	
	public boolean getMostrarResponsableEstudiante() {
		return actividadUsoEstudiante();
	}
	
	public void validarResponsablesTecnicos(FacesContext context, UIComponent validate, Object value) {		
		
		for (GestionarProductosQuimicosProyectoAmbiental sustancia : gestionarProductosQuimicosProyectoAmbientalLista) {
			boolean respTecnicoIngresado=false;		
			
			if(actividadExpImp(sustancia)) {
				respTecnicoIngresado=true;
			}else {
				for (ResponsableSustanciaQuimica responsable : responsableSustanciaQuimicaLista) {
					if(responsable.getSustanciasQuimicas().contains(sustancia.getId().toString()) 
							&& responsable.getTipoResponsable().getNombre().toUpperCase().contains(TECNICO)){
						respTecnicoIngresado=true;
						break;
					}					
					
//					if(responsable.getSustanciaProyecto()!=null
//						&& responsable.getSustanciaProyecto().getId().intValue()==sustancia.getId().intValue()
//						&& responsable.getTipoResponsable().getNombre().toUpperCase().contains(TECNICO)){
//						respTecnicoIngresado=true;
//						break;
//					}
				}
			}			
			
			if(!respTecnicoIngresado) {
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Responsable TÉCNICO es requerido por cada sustancia", null));
			}
			
		}
		
	}
	
	public void validarResponsablesBodega(FacesContext context, UIComponent validate, Object value) {		
		for (ResponsableSustanciaQuimica responsable : responsableSustanciaQuimicaLista) {
			if(responsable.getTipoResponsable().getNombre().toUpperCase().contains(BODEGA))
				return;
		}		
		throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		"Responsable de Bodega es requerido", null));
	}
	
	public String getSustanciasEstudiante() {
		String ret="";
		for (GestionarProductosQuimicosProyectoAmbiental sustancia : gestionarProductosQuimicosProyectoAmbientalLista) {
			ActividadSustancia actividadEstudiante=buscarActividad(sustancia,"USO",ESTUDIANTE);
			if(actividadEstudiante!=null && actividadEstudiante.getActividadSeleccionada() && !ret.contains(sustancia.getSustanciaquimica().getDescripcion())) {
				ret+=ret.isEmpty()?"":", ";
				ret+=sustancia.getSustanciaquimica().getDescripcion();
			}
		}
		return ret;
	}
	
	public List<ResponsableSustanciaQuimica> getResponsableSustanciaQuimicaLista(String respBodega){
		List<ResponsableSustanciaQuimica> lista=new ArrayList<>();
		for (ResponsableSustanciaQuimica responsable : responsableSustanciaQuimicaLista) {
			if(responsable.getTipoResponsable().getNombre().toUpperCase().contains(respBodega) && 
					!responsable.getTipoIdentificacion().getDescripcion().toUpperCase().contains("ARTESANAL"))//?BODEGA:TECNICO
				lista.add(responsable);
				
			if(responsable.getTipoIdentificacion().getDescripcion().toUpperCase().contains(respBodega)){
				lista.add(responsable);
			}
		}		
		return lista;
	}
	
	public List<ActividadSustancia> getActividadSustanciaProyectoSeleccionadasLista(){
		List<ActividadSustancia> lista=new ArrayList<>();
		for (ActividadSustancia item : actividadSustanciaProyectoLista) {
			String nombreSustancia=item.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion().toUpperCase();		

//			if(item.getActividadSeleccionada() 
//					&& item.getCaracteristicaActividad().getActividadNivel().getNivel() != 3
//					&& !(item.getCaracteristicaActividad().getActividadNivel().getNivel().intValue()==1 && !nombreSustancia.contains(MERCURIO))) {				
//				lista.add(item);
//			}
			
			if(item.getActividadSeleccionada() 
					&& item.getCaracteristicaActividad().getActividadNivel().getNivel() != 3) {		
				
				if((item.getCaracteristicaActividad().getNombre().toUpperCase().contains("IMPORTA")
				|| item.getCaracteristicaActividad().getNombre().toUpperCase().contains("EXPORTA"))){
					if(nombreSustancia.contains(MERCURIO)){
						lista.add(item);
					}					
				}else{
					lista.add(item);
				}
			}
		}
		
		return lista;
	}
	
	/**
	 * Bucar Sustancias que tengan como actividad diferente exportacion importacion
	 * @return
	 */
	public List<GestionarProductosQuimicosProyectoAmbiental> getSustanciasParaUbicacionLista(){
		List<GestionarProductosQuimicosProyectoAmbiental> lista=new ArrayList<>();
		for (GestionarProductosQuimicosProyectoAmbiental item : gestionarProductosQuimicosProyectoAmbientalLista) {
			if(!actividadExpImp(item) && !lista.contains(item)) {
				lista.add(item);
			}
		}
		
		return lista;
	}
	
	public void validarLLenadoActividades(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		for (GestionarProductosQuimicosProyectoAmbiental sustancia : gestionarProductosQuimicosProyectoAmbientalLista) {	
			if(sustancia.getInformacionIngresada() == null || !sustancia.getInformacionIngresada()){
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,"Debe seleccionar al menos 1 actividad por " + sustancia.getSustanciaquimica().getDescripcion(), null));				
			}				
		}

		if (!errorMessages.isEmpty()){
			throw new ValidatorException(errorMessages);
		}
	}
	
	private boolean validarActividades() {		
		for (ActividadSustancia item : actividadSustanciaProyectoLista) {				
			if(item.getActividadSeleccionada())
				return true;
		}				
		return false;
	}
	
	public boolean validarTodasActividades() {
		List<FacesMessage> errorMessages = new ArrayList<>();		
		
		for (GestionarProductosQuimicosProyectoAmbiental sustancia : gestionarProductosQuimicosProyectoAmbientalLista) {
			String nombreSustancia=sustancia.getSustanciaquimica().getDescripcion();
			
			//Validar Actividad Almacenamiento
			ActividadSustancia almacenamientoPropio=buscarActividad(sustancia,ALMACENAMIENTO,ALMACENAMIENTO_PROPIO);
			ActividadSustancia almacenamientoPrestacion=buscarActividad(sustancia,ALMACENAMIENTO,"SERVICIO DE ALMACENAMIENTO");
			ActividadSustancia almacenamientoContratacion=buscarActividad(sustancia,"CONTRATAR",ALMACENAMIENTO);
			
//			if(!almacenamientoPropio.getActividadSeleccionada()
//			 && !almacenamientoPrestacion.getActividadSeleccionada()
//			 && !almacenamientoContratacion.getActividadSeleccionada()) {
//				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,nombreSustancia+": Debe llenar el campo de Contratación servicio de almacenamiento", null));
//			}
			
			//Validar Actividad Transporte
			ActividadSustancia transportePropio=buscarActividad(sustancia,TRANSPORTE,"TRANSPORTE PROPIO");
			ActividadSustancia transportePrestacion=buscarActividad(sustancia,TRANSPORTE,"SERVICIO DE TRANSPORTE");
			ActividadSustancia transporteContratacion=buscarActividad(sustancia,"CONTRATAR",TRANSPORTE);
			
			if(!transportePropio.getActividadSeleccionada()
			 && !transportePrestacion.getActividadSeleccionada()
			 && !transporteContratacion.getActividadSeleccionada()) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,nombreSustancia+": Debe llenar el campo de Contratación servicio de transporte", null));
			}
			
			//Validar Actividad USO
			ActividadSustancia usoEstudiante=buscarActividad(sustancia,"USO",ESTUDIANTES);
			ActividadSustancia usoInsEducatica=buscarActividad(sustancia,"USO",EDUCATIVA);
			ActividadSustancia usoAlmacenamiento=buscarActividad(sustancia,"USO",ALMACENAMIENTO_PROPIO);
			
			if((usoEstudiante.getActividadSeleccionada()
			 || usoInsEducatica.getActividadSeleccionada())
			 && !usoAlmacenamiento.getActividadSeleccionada()) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,nombreSustancia+": Debe Seleccionar la actividad Uso: Almacenamiento propio", null));
			}
		}	
						
		if (!errorMessages.isEmpty()){
			return false;
		}else{
			return true;
		}		
	}
	
	private boolean validarUbicaciones() {
		for (GestionarProductosQuimicosProyectoAmbiental sustancia : getSustanciasParaUbicacionLista()) {
			boolean ubicacionAgregada=false;
			for (UbicacionSustancia ubicacion : ubicacionSustanciaProyectoLista) {
				if(sustancia.getId().intValue()==ubicacion.getGestionarProductosQuimicosProyectoAmbiental().getId().intValue()) {
					ubicacionAgregada=true;
					break;
				}
			}
			if(!ubicacionAgregada) {
				return false;
			}
		}
		return true;				
	}
	
	public boolean validarResponsables() {
		if(actividadUsoEstudiante() && documentoEmisionRsq== null)
			return false;
		if(!actividadUsoEstudiante() && !actividadExpImp() && responsableSustanciaQuimicaLista.isEmpty())
			return false;
		return true;
	}
	
	private void guardarRSQ() {
		//Guardar tabla principal RSQ
		registroSustanciaQuimica.setUsuario(JsfUtil.getLoggedUser());
		if(registroSustanciaQuimica.getCodigo() == null){
			registroSustanciaQuimica.setCodigo(registroSustanciaQuimica.getNumeroAplicacion());
		}
				
		registroSustanciaQuimicaFacade.guardar(registroSustanciaQuimica, JsfUtil.getLoggedUser());	
	}
	
	private void guardarActividades() {
		//Guardar Actividades Sustancias
		for (ActividadSustancia item : actividadSustanciaProyectoLista) {
			item.setCupoControl(item.getCupo());
			actividadSustanciaQuimicaFacade.guardar(item,  JsfUtil.getLoggedUser());
		}
		if(actividadExpImp()) {
			for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
				if(item.getId()!=null) {
					item.setEstado(false);
					ubicacionSustanciaQuimicaFacade.guardar(item, JsfUtil.getLoggedUser());
				}
			}
			ubicacionSustanciaProyectoLista.clear();
			for (ResponsableSustanciaQuimica item : responsableSustanciaQuimicaLista) {				
				if(item.getId()!=null) {
					item.setEstado(false);
					responsableSustanciaQuimicaFacade.guardar(item, JsfUtil.getLoggedUser());
				}
				
			}
			responsableSustanciaQuimicaLista.clear();
		}
	}
	
	private void guardarUbicaciones() {
		//Guardar UbicacionSustancia, estado false en eliminados
		for (UbicacionSustancia item : ubicacionSustanciaProyectoEliminadosLista) {
			item.setEstado(false);
			ubicacionSustanciaQuimicaFacade.guardar(item,  JsfUtil.getLoggedUser());
		}			
		ubicacionSustanciaProyectoEliminadosLista.clear();
		for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {				
			ubicacionSustanciaQuimicaFacade.guardar(item,  JsfUtil.getLoggedUser());
		}	
	}
	
	/**
	 * Guardar Responsables
	 * Si es estudiante se guarda documento de emision de rsq emitido por la institucion educativa
	 * caso contrario 
	 * se guardan los tecnicos responsables
	 */
	private void guardarResponsables() {
		try {
			if(actividadUsoEstudiante()) {
				if(documentoEmisionRsq!=null && documentoEmisionRsq.isContenidoActualizado()) {
					documentosRSQFacade.guardarDocumento(varTramite, documentoEmisionRsq,registroSustanciaQuimica.getId());
					documentoEmisionRsq.setContenidoActualizado(false);
				}
			}
			
			if(getMostrarResponsablesTecnicos() || getMostrarResponsablesBodega()) {
				for (ResponsableSustanciaQuimica item : responsableSustanciaQuimicaLista) {				
					responsableSustanciaQuimicaFacade.guardar(item,  JsfUtil.getLoggedUser());
					if(item.getDocumentoResponsabilidadTec()!=null && item.getDocumentoResponsabilidadTec().isContenidoActualizado()) {
						documentosRSQFacade.guardarDocumento(registroSustanciaQuimica.getCodigo(), item.getDocumentoResponsabilidadTec(),item.getId());
						item.getDocumentoResponsabilidadTec().setContenidoActualizado(false);
					}
					if(item.getDocumentoTitProfesional()!=null && item.getDocumentoTitProfesional().isContenidoActualizado()) {
						documentosRSQFacade.guardarDocumento(registroSustanciaQuimica.getCodigo(), item.getDocumentoTitProfesional(),item.getId());
						item.getDocumentoTitProfesional().setContenidoActualizado(false);
					}
					if(item.getDocumentoTitArtesanal()!=null && item.getDocumentoTitArtesanal().isContenidoActualizado()) {
						documentosRSQFacade.guardarDocumento(registroSustanciaQuimica.getCodigo(), item.getDocumentoTitArtesanal(),item.getId());
						item.getDocumentoTitArtesanal().setContenidoActualizado(false);
					}
					
				}
			}
			
			//Desactivar ResponsableSustancia  eliminados
			for (ResponsableSustanciaQuimica item : responsableSustanciaQuimicaEliminadosLista) {
				item.setEstado(false);
				responsableSustanciaQuimicaFacade.guardar(item,  JsfUtil.getLoggedUser());
			}			
			responsableSustanciaQuimicaEliminadosLista.clear();
			
		} catch (Exception e) {
			LOG.error("Error al guardar responsables de registro de sustancias quimicas.");
			e.printStackTrace();
		}
		
	}
	
	public void guardar() {        
        String currentStep = wizardBean.getCurrentStep()==null?"pasoDatosActividad":wizardBean.getCurrentStep();  
        if(nombreTarea.contains("INGRESAR INFORMACION") || nombreTarea.contains("REMITIR RESPUESTAS ACLARATORIAS") || 
        		nombreTarea.contains("INGRESAR CORRECCIONES") || nombreTarea.contains("INGRESAR INFORMACIÓN") ) {			
        	habilitarIngreso = true;
        }
        
        if(habilitarIngreso)
        	switch (currentStep) {		
    		case "pasoDatosActividad":
    			guardarRSQ();
    			deshabilitar = true;
    			break;
    		case "pasoActividadRealizar":    			    			
    			if(validarActividades()) {
    				if(validarTodasActividades()){
    					guardarActividades();
        				JsfUtil.addMessageInfo("Información Guardada Correctamente");
        				deshabilitar = false;
    				}else{
    					JsfUtil.addMessageError("Debe seleccionar al menos 1 actividad para todas las sustancias");
    					deshabilitar = true;
    				}    				
    			}else {
    				JsfUtil.addMessageError("Debe seleccionar al menos 1 actividad");
    				deshabilitar = true;
    			}
    			break;
    		case "pasoUbicacion":    			
    			if(validarUbicaciones()) {
    				guardarUbicaciones();
    				JsfUtil.addMessageInfo("Información Guardada Correctamente");
    			}else {
    				JsfUtil.addMessageError("Debe seleccionar al menos 1 ubicación por cada sustancia.");
    			}
    			    			
    			break;
    		case "pasoResponsables":    			
    			if(validarResponsables()) {
    				guardarRSQ();
        			guardarResponsables();
    				JsfUtil.addMessageInfo("Información Guardada Correctamente");
    			}else {    				
    				JsfUtil.addMessageError("Debe seleccionar al menos 1 responsable");
    			}    			
    			break;
    		case "pasoCupo":
    			guardarRSQ();
    			guardarActividades();
    			guardarUbicaciones();
    			JsfUtil.addMessageInfo("Información Guardada Correctamente");
    			break;
    		case "pasoFinalizar":
    			JsfUtil.addMessageInfo("Información Guardada Correctamente");
    			break;
    		default:
    			break;
    		}
                
    }
	
	public void btnAtras() {

		String currentStep = wizardBean.getCurrentStep();	
		if(currentStep != null && currentStep.equals("pasoActividadRealizar")){
			habilitarIngreso = false;
		}		
	}
	
	/**
	 * Planta Central para actividades en el nivel 1 o Varias Provincias
	 * Direccion Provincial actividades nivel 2
	 * @return
	 */
	private String tipoArea() {
		for (ActividadSustancia item : actividadSustanciaProyectoLista) {
			if(item.getCaracteristicaActividad().getActividadNivel().getNivel()==1 && item.getActividadSeleccionada()) {
				return "pc";
			}
		}
		
		if(ubicacionSustanciaProyectoLista.isEmpty()) {
			return "pc";
		}	
		
		List<Integer> unicacionesIdLista=new ArrayList<>();
		for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
			int id=item.getUbicacionesGeografica().getUbicacionesGeografica().getId().intValue();
			if(!unicacionesIdLista.contains(id)) {
				unicacionesIdLista.add(id);
				if(unicacionesIdLista.size()>1) {
					return "pc";
				}
			}			
		}	
		
		return "cz";
	}
	
	private Usuario buscarTecnicoRevisionProyecto(){
		String tipoArea=tipoArea();
		String roleKey="role.rsq."+tipoArea+".tecnico.revision.proyecto";
		
		try {
			return buscarUsuarioBean.buscarUsuario(roleKey,registroSustanciaQuimica.getArea());
		} catch (Exception e) {
			LOG.error("No se ha encontrado usuario con en rol "+Constantes.getRoleAreaName(roleKey)+"("+roleKey+") en el area "+registroSustanciaQuimica.getArea().getAreaName());			
		}
		
		return null;
	}
	
	public void enviar(){
		boolean operacionCorrecta=false;		
		try {
			
			String tipoArea=tipoArea();
			
			Area area=tipoArea.equals("pc")?
					areaFacade.getArea(Constantes.getRoleAreaName("area.rsq.pc"))://DMPNP
					areaFacade.getAreaCoordinacionZonal(ubicacionSustanciaProyectoLista.get(0).getUbicacionesGeografica());
			
			if(area==null) {
				return;
			}			
					
			registroSustanciaQuimica.setArea(area);				
			guardarRSQ();
			
			if(verObservaciones && documentoJustificaciones!=null && documentoJustificaciones.isContenidoActualizado()) {
				documentosRSQFacade.guardarDocumento(varTramite, documentoJustificaciones,registroSustanciaQuimica.getId());
				documentoJustificaciones.setContenidoActualizado(false);
			}
			
			Usuario uTecnico=buscarTecnicoRevisionProyecto();
			
			if(uTecnico!=null) {
				if(registroSustanciaQuimica.pronunciamientoObservado()) {
					notificacionObservaciones(uTecnico);
				}				
				Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
				parametros.put("usuario_tecnico", uTecnico.getNombre());
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getProcessInstanceId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				operacionCorrecta=true;
			}else {
				JsfUtil.addMessageError();
			}			
			
		} catch (Exception e) {					
			e.printStackTrace();
		}
		
		if(operacionCorrecta){
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		}
	}
	
	public void guardarProrroga(){
		if(requiereProrroga!=null) {
			boolean operacionCorrecta=false;		
			try {
				if(requiereProrroga && documentoProrroga!=null) {
					documentosRSQFacade.guardarDocumento(varTramite, documentoProrroga, registroSustanciaQuimica.getId());
				}
				
				Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
				parametros.put("requiere_prorroga", requiereProrroga);
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getProcessInstanceId(), parametros);
				if(requiereProrroga) {
					procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				}				
				operacionCorrecta=true;		
				
			} catch (Exception e) {					
				e.printStackTrace();
			}
			
			if(operacionCorrecta && requiereProrroga){
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			}
		}
		
	}
	
	private void notificacionObservaciones(Usuario usuarioNotifica){		
		try {
			Usuario uOperador=registroSustanciaQuimica.getProyectoLicenciaCoa().getUsuario();		
			String nombreOperador=JsfUtil.getNombreOperador(uOperador, organizacionFacade.buscarPorRuc(uOperador.getNombre()));
			String nombreDestino=usuarioNotifica.getPersona().getNombre();			
			String nombreProyecto=registroSustanciaQuimica.getProyectoLicenciaCoa().getNombreProyecto();
			String codigoTramite=registroSustanciaQuimica.getProyectoLicenciaCoa().getCodigoUnicoAmbiental();
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionRSQRevisarRespuestasObservaciones", new Object[]{nombreDestino,nombreOperador,nombreProyecto, codigoTramite});
			Email.sendEmail(usuarioNotifica, "Registro Sustancias Quimicas", mensaje, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), loginBean.getUsuario());				
		
		} catch (Exception e) {
			LOG.error("No se envio la notificacion al usuario. "+e.getCause()+" "+e.getMessage());
		}			
	}
	
	/** 
	 * NUEVO CODIGO
	 */
	public void cargarSustanciaSeleccionada(GestionarProductosQuimicosProyectoAmbiental sustancia){
		
		 setGestionarProductosQuimicosProyectoAmbientalSeleccionado(sustancia);
		 listaSeleccion = new ArrayList<String>();
		 
		 RequestContext context = RequestContext.getCurrentInstance();
	     context.execute("PF('actividadesRealizar').show();");
	}
	
	public void validarSustanciaQuimica(GestionarProductosQuimicosProyectoAmbiental sustancia){
		
		RequestContext context = RequestContext.getCurrentInstance();
	    context.execute("PF('actividadesRealizar').hide();");		
	}
	
	public void validarCoordenada() {
		
		if(ubicacionSustanciaProyecto.getCoordenadaX() == null || ubicacionSustanciaProyecto.getCoordenadaY() == null){
			return;
		}
        
        String coodenadas="";
        try{        	
            
            SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
            ws.setEndpoint(Constantes.getInterseccionesWS());
            
            boolean tipoPoligono = false;
        	coodenadas="";
          
            coodenadas = BigDecimal.valueOf(ubicacionSustanciaProyecto.getCoordenadaX()).setScale(5, RoundingMode.HALF_DOWN).toString()
            		+" "+BigDecimal.valueOf(ubicacionSustanciaProyecto.getCoordenadaY()).setScale(5, RoundingMode.HALF_DOWN).toString();
                                                        
            Intersecado_entrada poligono = new Intersecado_entrada();//verifica que este bien el poligono
            poligono.setU(Constantes.getUserWebServicesSnap());
            poligono.setIsla(Constantes.SIGLAS_INSTITUCION + "-RA");
            poligono.setTog(tipoPoligono?"po":"pu");
            poligono.setXy(coodenadas);
            poligono.setShp("dp");
            Intersecado_resultado[]intRest;                
            HashMap<String, Double>varUbicacion= new HashMap<String,Double>();
            String parroquia="";
            Double valorParroquia=0.0;
            Integer orden=2;
            try {
            	intRest=ws.interseccion(poligono);
            	if (intRest[0].getInformacion().getError() != null) {
            		JsfUtil.addMessageError(intRest[0].getInformacion().getError().toString());
            	}else{
            		
            		boolean correcto=false;
            		if(coordinatesWrappersPre != null && coordinatesWrappersPre.size()> 0){
                		for (CoordendasPoligonos coordenadaRmpl : coordinatesWrappersPre) {
                			coodenadasgeograficas="";
							for (CoordenadasProyecto coordenada : coordenadaRmpl.getCoordenadas()) {
								coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString() : ","+coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString();
							}
							correcto = validarCoordenadas(TipoForma.TIPO_FORMA_POLIGONO, tipoPoligono, coodenadasgeograficas, coodenadas);
							if(correcto)
								break;							
                		}
            		}
            		
        			if (!correcto){
        				JsfUtil.addMessageError("Estimado usuario las coordenadas ingresadas deben encontrarse dentro del área de geográfica del proyecto, misma que fue ingresada en el registro del proyecto.");            			
        			}
        			else{
        				
       					//carga las parroquias ----------------------------------
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
        									varUbicacionArea.put(parroquia,valorParroquia);                							                								
        									if (varUbicacion.get(parroquia) != null) {
        										if (valorParroquia >= varUbicacion.get(parroquia)) {
        											varUbicacion.put(parroquia,orden.doubleValue());
        										}
        									} else {
        										varUbicacion.put(parroquia,orden.doubleValue());
        									}
        								}
        							}
        							orden ++;
        						}
        					}
        				}
        				cargarUbicacionProyecto(varUbicacion);        				     
        			}
            	}

            }
            catch (RemoteException e) {
            	JsfUtil.addMessageError("Error insesperado, comuníquese con mesa de ayuda");
            	System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
            }          
                       
        }catch(Exception ex){
        	ex.printStackTrace();
        }
	}
	
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
	
	public void cargarUbicacionProyecto(HashMap<String, Double> parroquia){

		Iterator it = parroquia.entrySet().iterator();
		Integer orden=0;
		String inec="";
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			orden = e.getValue().intValue();
			if (!inec.equals("")) {
				
				UbicacionesGeografica parroquiaUbicacion = ubicacionFacade.buscarUbicacionPorCodigoInec(inec);
				
				ubicacionSustanciaProyecto.setUbicacionesGeografica(parroquiaUbicacion);				
			}
		}
	}
	
	public void ubicacionMasArea(){
		
		Iterator it = varUbicacionArea.entrySet().iterator();
		double orden=0;
		String inec="";
		double maxArea = 0;
		String inecMayor = "";
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			orden = e.getValue().doubleValue();
			if (orden >= maxArea) {
				maxArea = orden;
				inecMayor = inec;
			}
		}
		
		if(inecMayor == null || inecMayor.isEmpty())
			return;
				
		ubicacionSustanciaProyecto.setUbicacionesGeografica(ubicacionFacade.buscarUbicacionPorCodigoInec(inecMayor));		
	}
	
	
	@Getter
	@Setter
	private List<String> lugaresArray;
	
	@Getter
	@Setter
	private List<String> lugaresSeleccionados;
	
	public void obtenerLugares(boolean soloBodega){			
		
		lugaresArray = new ArrayList<String>();
		
		for(GestionarProductosQuimicosProyectoAmbiental sustancia : listaSustanciasTecnico){
			
			List<UbicacionSustancia> listaUbicacionSustancia = ubicacionSustanciaQuimicaFacade.obtenerUbicacionesPorIdSustancia(sustancia);
			
			for(UbicacionSustancia ubicacion : listaUbicacionSustancia){
				
				for(String ub : ubicacion.getLugaresArray()){
					String lugar = ub + " " + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "-" + sustancia.getSustanciaquimica().getDescripcion();
					if(soloBodega) { //lugar.toUpperCase().contains(BODEGA)
						lugaresArray.add(lugar);
					}else if(!soloBodega){
						lugaresArray.add(lugar);
					}
				}
			}
			
		}
	}	
	
}
