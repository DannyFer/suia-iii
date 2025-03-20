package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ActividadSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DeclaracionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DetalleSolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosSustanciasQuimicasRcoaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.MovimientoDeclaracionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.PermisoDeclaracionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DeclaracionSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DetalleSolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.MovimientoDeclaracionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.PermisoDeclaracionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroOperador;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Holiday;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class DeclaracionIngresarInfoRSQController {
	
	private static final Logger LOG = Logger.getLogger(DeclaracionIngresarInfoRSQController.class);
	
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
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
	
	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;
	    
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
    private DeclaracionSustanciaQuimicaFacade declaracionSustanciaQuimicaFacade;
	
	@EJB
    private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
    private MovimientoDeclaracionRSQFacade movimientoDeclaracionRSQFacade;
	
	@EJB
    private OrganizacionFacade organizacionFacade;
		   
    @EJB
    private ProcesoFacade procesoFacade;
    
    @EJB
    private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
    
    @EJB
    private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
        
    @EJB
    private UsuarioFacade usuarioFacade;
    
    @EJB
    private GestionarProductosQuimicosProyectoAmbientalFacade sustanciasProyectoFacade;
    
    @EJB
    private InformesOficiosRSQFacade informesOficiosRSQFacade;
    
    @EJB
    private DocumentosRSQFacade documentosRSQFacade;
    
    @EJB
	private DocumentosSustanciasQuimicasRcoaFacade documentosFacade;
    
    @EJB
	private CrudServiceBean crudServiceBean;
    
    @EJB
    private SolicitudImportacionRSQFacade solicitudImportacionRSQFacade;
    
    @EJB
    private ContactoFacade contactoFacade;
    
    @EJB
    private PermisoDeclaracionRSQFacade permisoDeclaracionRSQFacade;
    
    @EJB
    private DetalleSolicitudImportacionRSQFacade detalleImportacionRSQFacade;
    
    @EJB
    private FeriadosFacade feriadoFacade;
           
    /*List*/
    @Getter
	@Setter
    private List<CatalogoGeneralCoa> listaTipoTransaccion, listaTipoPresentacion;
    
    @Getter
    private List<MovimientoDeclaracionRSQ> movimientoDeclaracionRSQLista;
    
    private List<MovimientoDeclaracionRSQ> movimientoDeclaracionRSQEliminadosLista;
    
    @Getter
	@Setter
    private List<SustanciaQuimicaPeligrosa> sustanciasRsq;
      
    @Getter
	@Setter
    private List<RegistroOperador> listaRegistrosOperadores;
	
	@Getter
	@Setter
	private DeclaracionSustanciaQuimica declaracionSustanciaQuimica;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private MovimientoDeclaracionRSQ movimientoDeclaracionRSQ, movimientoDeclaracionRSQSelected;
				
	@Getter
	@Setter
	private RegistroSustanciaQuimica registroSustanciaQuimica;
	
	@Getter
	@Setter
	private RegistroOperador operadorTransaccion;
	
	/*Boolean*/
	@Getter
	boolean habilitarIngresoDatos;
	
    /*Integer*/
	
    
	/*String*/
	private String varTramite;
	
	//Datos del operador
    @Getter       
    private String nombreRazonSocial;
    
    @Getter
	@Setter
    private Integer ordenConsumo, ordenImportacion, ordenOtraPresentacion, ordenExportacion;
    
    @Getter
   	@Setter
    private Boolean habilitarIngreso, habilitarEgreso,habilitarRegistroMov;
    
    private boolean tareaCargada;
    
    @Getter
    @Setter
    private String nombreTarea;
    
    @Getter
	@Setter
	private DocumentosSustanciasQuimicasRcoa documentoOficio;
    
    @Getter
    @Setter
    private boolean habilitarDescargar;
    
    public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
    
    @Getter
    @Setter
    private List<SolicitudImportacionRSQ> listaAutorizacionesImps;
    
    @Getter
    @Setter
    private PermisoDeclaracionRSQ permisoDeclaracion;
    
    @Getter
    @Setter
    private boolean desHabilitarSustancias;
    
    @Getter
    @Setter
    private boolean mostrarOperador = true;
    
    @Getter
    @Setter
    private boolean tareaDeActualizacion = false;
	
	@PostConstruct
	public void init(){		
		try {
			cargarDatosIniciales();
			cargarDatosOperador();
			cargarDatosProyecto();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cargarDatosIniciales(){		
		//Iniciar Objetos
		movimientoDeclaracionRSQ = new MovimientoDeclaracionRSQ();
		
		//Iniciar Listas
		movimientoDeclaracionRSQLista=new ArrayList<>();
		movimientoDeclaracionRSQEliminadosLista=new ArrayList<>();
		
		
		//Cargar Catalogos		
		listaTipoTransaccion = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.DSQ_TIPO_TRANSACCION);
		listaTipoPresentacion = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.DSQ_TIPO_PRESENTACION);		
		
//		listaAutorizacionesImps = solicitudImportacionRSQFacade.listaImportaciones();		
		
		habilitarIngreso = false;
		habilitarEgreso = false;
		habilitarRegistroMov=false;
		desHabilitarSustancias = true;
		
		ordenConsumo = 5;
		ordenImportacion = 8;
		ordenOtraPresentacion = 6;
		ordenExportacion = 9;
		
		permisoDeclaracion = new PermisoDeclaracionRSQ();
	}
			
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			varTramite=(String)variables.get("tramite");
			
			nombreTarea = bandejaTareasBean.getTarea().getTaskName();
			
			if(nombreTarea.equals("Actualizar la Informacion Ingresada")){
				tareaDeActualizacion =  true;
			}
			
			int anioDeclaracion=variables.containsKey("anio_declaracion")?(Integer.valueOf((String)variables.get("anio_declaracion"))):0;
			int mesDeclaracion=variables.containsKey("mes_declaracion")?(Integer.valueOf((String)variables.get("mes_declaracion"))):0;
			
			if(anioDeclaracion != 0){
				
				listaRegistrosOperadores = registroSustanciaQuimicaFacade.obtenerUsuariosRegistrosRSQ(anioDeclaracion);
				
				List<RegistroOperador> listaRegistrosProvisionales = registroSustanciaQuimicaFacade.obtenerUsuariosRegistrosRSQProvisionales();
				
				listaRegistrosOperadores.addAll(listaRegistrosProvisionales);
			}			
			
			declaracionSustanciaQuimica = declaracionSustanciaQuimicaFacade.buscarPorTramite(varTramite);
			
			if(declaracionSustanciaQuimica == null || declaracionSustanciaQuimica.getId() == null){
				String idProyecto = (String)variables.get("idProyecto");
				
				if(idProyecto!=null) {
					int idProyectoRsq = Integer.valueOf(idProyecto);
					registroSustanciaQuimica = registroSustanciaQuimicaFacade.obtenerRegistroPorId(idProyectoRsq);
					
					if(registroSustanciaQuimica == null){
						registroSustanciaQuimica = registroSustanciaQuimicaFacade.obtenerRegistroPorCodigo(varTramite);
						declaracionSustanciaQuimica=declaracionSustanciaQuimicaFacade.obtenerPorRSQ(registroSustanciaQuimica, anioDeclaracion, mesDeclaracion);
					}else{
						declaracionSustanciaQuimica=declaracionSustanciaQuimicaFacade.obtenerPorRSQ(registroSustanciaQuimica, anioDeclaracion, mesDeclaracion);
					}
				}
			}else{
				registroSustanciaQuimica=declaracionSustanciaQuimicaFacade.obtenerRegistroPorCodigo(varTramite);
				List<PermisoDeclaracionRSQ> listaPermisos = permisoDeclaracionRSQFacade.obtenerSustanciasPorRSQSustancia(registroSustanciaQuimica, declaracionSustanciaQuimica.getSustanciaQuimica());
				if(listaPermisos != null && !listaPermisos.isEmpty()){
					permisoDeclaracion = listaPermisos.get(0);
				}
			}			
			
			if(declaracionSustanciaQuimica.getTramite() == null){
				declaracionSustanciaQuimica.setTramite(registroSustanciaQuimica.getNumeroAplicacion());
			}
						
			List<InformeOficioRSQ> oficios=informesOficiosRSQFacade.obtenerPorRSQListaPorTipo(registroSustanciaQuimica,TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO);
			if(oficios != null && !oficios.isEmpty()){
				InformeOficioRSQ oficio = oficios.get(0);
				documentoOficio=documentosRSQFacade.obtenerDocumentoPorTipo(registroSustanciaQuimica.pronunciamientoAprobado()?TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_OFICIO_NEGADO,"InformeOficioRSQ",oficio.getId());				
			}
			
			if(documentoOficio != null && documentoOficio.getId() != null){
				habilitarDescargar = true;
			}
			
			listaAutorizacionesImps = solicitudImportacionRSQFacade.obtenerImportaciones(JsfUtil.getLoggedUser().getNombre(), declaracionSustanciaQuimica.getSustanciaQuimica().getId());
			
			tareaCargada=true;
		} catch (Exception e) {
			LOG.error("Error al recuperar tarea "+e.getCause()+" "+e.getMessage());
		}		
	}
	
	private void cargarDatosOperador() throws ServiceException{			
		Usuario usuarioOperador=JsfUtil.getLoggedUser();		
		Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
		nombreRazonSocial=organizacion!=null?organizacion.getNombre():usuarioOperador.getPersona().getNombre();
	}
	
	private void cargarDatosProyecto() throws ServiceException{
		
		if(bandejaTareasBean.getTarea()!=null 
		&& bandejaTareasBean.getTarea().getProcessId().compareTo(Constantes.RCOA_DECLARACION_SUSTANCIA_QUIMICA)==0) {
			cargarDatosTarea();
		}else {
			Integer idDeclaracionSQ = (Integer)JsfUtil.devolverObjetoSession("idDeclaracionSQ");			
			if(idDeclaracionSQ!=null) {
				declaracionSustanciaQuimica = declaracionSustanciaQuimicaFacade.obtenerRegistroPorId(idDeclaracionSQ);				
			}
		}	
			
		if(declaracionSustanciaQuimica!=null && declaracionSustanciaQuimica.getId() != null) {
			
			habilitarIngresoDatos=declaracionSustanciaQuimica.getEstadoDeclaracion().getOrden().intValue()==1;
			registroSustanciaQuimica = declaracionSustanciaQuimica.getRegistroSustanciaQuimica();
			
			sustanciasRsq = new ArrayList<>();
			
			sustanciasRsq = permisoDeclaracionRSQFacade.obtenerSustanciasPorRSQ(registroSustanciaQuimica);
			
			if(sustanciasRsq == null || sustanciasRsq.isEmpty()){
				if(declaracionSustanciaQuimica.getSustanciaQuimica() != null){
					sustanciasRsq.add(declaracionSustanciaQuimica.getSustanciaQuimica());
				}else{
					List<GestionarProductosQuimicosProyectoAmbiental> GestionarProductosQuimicosProyectoAmbientalList = new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
					
					GestionarProductosQuimicosProyectoAmbientalList=actividadSustanciaQuimicaFacade.obtenerActividadesPorRSQDistinct(registroSustanciaQuimica);
					
					for(GestionarProductosQuimicosProyectoAmbiental sustancia : GestionarProductosQuimicosProyectoAmbientalList){
						sustanciasRsq.add(sustancia.getSustanciaquimica());
					}
					desHabilitarSustancias = false;
				}				
			}
			
			movimientoDeclaracionRSQLista = movimientoDeclaracionRSQFacade.obtenerMovPorDeclaracion(declaracionSustanciaQuimica);
			
			habilitarIngresoDatos = true;
				
		}else {
			JsfUtil.addMessageError("Declaración de sustancias químicas no encontrada.");
			return;
		}			
	}
		
	public String getMesNombre() {		
		return JsfUtil.devuelveMes(declaracionSustanciaQuimica.getMesDeclaracion().intValue() -1);
	}
	
	public void crearMovimiento(){
		movimientoDeclaracionRSQ=new MovimientoDeclaracionRSQ(declaracionSustanciaQuimica);
		operadorTransaccion=new RegistroOperador();
	}
	
	public void agregarMovimiento() {
		try {
//			movimientoDeclaracionRSQSelected=movimientoDeclaracionRSQ;
			if(movimientoDeclaracionRSQ.getTipoMovimiento().getValor().contains("SIN TRANSACCION")) {
				for (MovimientoDeclaracionRSQ item : movimientoDeclaracionRSQLista) {
					if(item.getId()!=null)
						movimientoDeclaracionRSQEliminadosLista.add(item);
				}
				movimientoDeclaracionRSQLista.clear();
			}		
			
			if(!movimientoDeclaracionRSQ.getTipoMovimiento().getNombre().equals("IMPORTACIÓN")){
				if(!movimientoDeclaracionRSQ.getTipoMovimiento().getNombre().equals("EXPORTACIÓN")){
					if(!movimientoDeclaracionRSQ.getTipoMovimiento().getValor().contains("SIN TRANSACCION")){
						//buscar usuario a partir de los nuevos datos del operador
						
						Usuario operadorM = null;
						if(operadorTransaccion != null && operadorTransaccion.getIdUsuario() != null){
							operadorM = usuarioFacade.buscarUsuarioPorId(operadorTransaccion.getIdUsuario());
						}else{
							RegistroSustanciaQuimica registroMov = new RegistroSustanciaQuimica();
							registroMov = registroSustanciaQuimicaFacade.obtenerRegistroPorId(operadorTransaccion.getIdRegistro());
							operadorM = usuarioFacade.buscarUsuario(registroMov.getUsuarioCreacion());
						}
						movimientoDeclaracionRSQ.setOperador(operadorM == null ? null : operadorM);
					}
				}				
			}
		
//			movimientoDeclaracionRSQ.setOperador(operadorTransaccion==null?null:operadorTransaccion.getUsuario() == null ? null : operadorTransaccion.getUsuario());
			movimientoDeclaracionRSQ.setDescripcion(operadorTransaccion==null?null:operadorTransaccion.getCodigoRSQ());	
			movimientoDeclaracionRSQ.setIdRegistroSustancias(operadorTransaccion==null?null : operadorTransaccion.getIdRegistro());
					
			movimientoDeclaracionRSQLista.remove(movimientoDeclaracionRSQSelected);
			if(!movimientoDeclaracionRSQLista.contains(movimientoDeclaracionRSQ))
				movimientoDeclaracionRSQLista.add(movimientoDeclaracionRSQ);
			
			movimientoDeclaracionRSQ = new MovimientoDeclaracionRSQ();
			movimientoDeclaracionRSQSelected = new MovimientoDeclaracionRSQ();
			operadorTransaccion = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editarMovimiento(MovimientoDeclaracionRSQ item){
		movimientoDeclaracionRSQSelected=item;
		movimientoDeclaracionRSQ=new MovimientoDeclaracionRSQ();
		movimientoDeclaracionRSQ.setId(item.getId());
		movimientoDeclaracionRSQ.setDeclaracionSustanciaQuimica(item.getDeclaracionSustanciaQuimica());
		movimientoDeclaracionRSQ.setTipoMovimiento(item.getTipoMovimiento());
		movimientoDeclaracionRSQ.setTipoPresentacion(item.getTipoPresentacion());
		movimientoDeclaracionRSQ.setOperador(item.getOperador());
		movimientoDeclaracionRSQ.setNumeroFactura(item.getNumeroFactura());
		movimientoDeclaracionRSQ.setOpcionPresentacion(item.getOpcionPresentacion());
		movimientoDeclaracionRSQ.setProductoTerminado(item.getProductoTerminado());
		movimientoDeclaracionRSQ.setObservacionCambioEstado(item.getObservacionCambioEstado());
		movimientoDeclaracionRSQ.setNumeroSerieEnvases(item.getNumeroSerieEnvases());
		movimientoDeclaracionRSQ.setValorIngreso(item.getValorIngreso());
		movimientoDeclaracionRSQ.setValorEgreso(item.getValorEgreso());
		movimientoDeclaracionRSQ.setCantidadEnvases(item.getCantidadEnvases());
		movimientoDeclaracionRSQ.setEstadoRevision(item.getEstadoRevision());
		movimientoDeclaracionRSQ.setFechaRevision(item.getFechaRevision());
		movimientoDeclaracionRSQ.setDescripcion(item.getDescripcion());
		movimientoDeclaracionRSQ.setCorrecto(item.getCorrecto());
		movimientoDeclaracionRSQ.setEstado(item.getEstado());
		movimientoDeclaracionRSQ.setUsuarioCreacion(item.getUsuarioCreacion());
		movimientoDeclaracionRSQ.setUsuarioModificacion(item.getUsuarioModificacion());
		movimientoDeclaracionRSQ.setFechaCreacion(item.getFechaCreacion());
		movimientoDeclaracionRSQ.setFechaModificacion(item.getFechaModificacion());
		movimientoDeclaracionRSQ.setSolicitudImportacion(item.getSolicitudImportacion());
		
		if(item.getOperador() != null){
			operadorTransaccion=buscarOperador(item.getOperador().getNombre(), item.getDescripcion());
		}
		
		if(operadorTransaccion==null)
			operadorTransaccion=new RegistroOperador();
		habilitarIngreso = false;
		habilitarEgreso = false;
		habilitarRegistroMov=false;
		if(!movimientoDeclaracionRSQ.getTipoMovimiento().getValor().contains("SIN TRANSACCION")) {
			habilitarRegistroMov=true;
			if(movimientoDeclaracionRSQ.getTipoMovimiento().getValor().contains("INGRESO"))
				habilitarIngreso = true;
			else if(movimientoDeclaracionRSQ.getTipoMovimiento().getValor().contains("EGRESO"))
				habilitarEgreso = true;
		}
	}
	
	public void eliminarMovimiento(MovimientoDeclaracionRSQ item){
		if(item.getId()!=null)
			movimientoDeclaracionRSQEliminadosLista.add(item);
		movimientoDeclaracionRSQLista.remove(item);	
		
		double cantidadFin=declaracionSustanciaQuimica.getCantidadInicio()==null?0.0:declaracionSustanciaQuimica.getCantidadInicio();
		if(declaracionSustanciaQuimica.getCantidadFin() != null){
			cantidadFin-=item.getValorIngreso()==null?0.0:item.getValorIngreso();
			cantidadFin+=item.getValorEgreso()==null?0.0:item.getValorEgreso();
		}
		declaracionSustanciaQuimica.setCantidadFin(cantidadFin);
		
	}
	
	public boolean getHabilitarAgregarMov() {
		for (MovimientoDeclaracionRSQ item : movimientoDeclaracionRSQLista) {
			if(item.getTipoMovimiento().getValor().contains("SIN TRANSACCION"))
				return false;
		}
		
		return true;
	}
	
	public void validarMovimientos(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(!movimientoDeclaracionRSQLista.isEmpty()) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_WARN,"Debe agregar al menos 1 movimiento.", null));
		}		
						
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void tipoTransaccionListener() {
		habilitarIngreso = false;
		habilitarEgreso = false;
		habilitarRegistroMov=false;
		
		movimientoDeclaracionRSQ.setValorEgreso(null);
		movimientoDeclaracionRSQ.setValorIngreso(null);
		
		if(movimientoDeclaracionRSQ.getTipoMovimiento().getValor().contains("SIN TRANSACCION")) {
			movimientoDeclaracionRSQ.setValorEgreso(0.0);;
			movimientoDeclaracionRSQ.setValorIngreso(0.0);
		}else if(movimientoDeclaracionRSQ.getTipoMovimiento().getNombre().contains("IMPORTACIÓN")){
			listaAutorizacionesImps = solicitudImportacionRSQFacade.obtenerImportaciones(JsfUtil.getLoggedUser().getNombre(), declaracionSustanciaQuimica.getSustanciaQuimica().getId());
			habilitarIngreso = false;
			habilitarEgreso = false;	
			habilitarRegistroMov=true;
		}else {
			habilitarRegistroMov=true;
			
			if(movimientoDeclaracionRSQ.getTipoMovimiento().getValor().contains("INGRESO"))
				habilitarIngreso = true;
			else if(movimientoDeclaracionRSQ.getTipoMovimiento().getValor().contains("EGRESO"))
				habilitarEgreso = true;	
						
			if(movimientoDeclaracionRSQ.getTipoMovimiento().getNombre().contains("CONSUMO")) {
				if(registroSustanciaQuimica.getUsuario() == null){
					operadorTransaccion=buscarOperador(registroSustanciaQuimica.getUsuarioCreacion(), registroSustanciaQuimica.getNumeroAplicacion());
				}else{
					operadorTransaccion=buscarOperador(registroSustanciaQuimica.getUsuario().getNombre(), registroSustanciaQuimica.getNumeroAplicacion());
				}
				
			}else{
				operadorTransaccion = null;
			}			
		}
		mostrarOperador = true;
		if(movimientoDeclaracionRSQ.getTipoMovimiento().getOrden().equals(ordenExportacion) || 
				movimientoDeclaracionRSQ.getTipoMovimiento().getOrden().equals(ordenImportacion)){
			mostrarOperador = false;
		}
	}
	
	public void cargarInformacionImportacion(){
		if(movimientoDeclaracionRSQ.getSolicitudImportacion() != null){
			DetalleSolicitudImportacionRSQ detalle = detalleImportacionRSQFacade.buscarPorSolicitud(movimientoDeclaracionRSQ.getSolicitudImportacion());
			
			if(detalle != null && detalle.getId() != null){
				movimientoDeclaracionRSQ.setValorIngreso(detalle.getPesoNeto().doubleValue());
			}
		}
	}
	
	private RegistroOperador buscarOperador(String usuario, String descripcion){
		try {
			for (RegistroOperador item : listaRegistrosOperadores) {
				if(item.getUsuario() != null){
					if(item.getUsuario().getNombre().compareTo(usuario)==0){
						if(descripcion == null)
							return item;
						if(!descripcion.isEmpty() && item.getRegistro() != null && item.getRegistro().getNumeroAplicacion() != null && descripcion.equals(item.getRegistro().getNumeroAplicacion()))
							return item;
					}
				}else if(item.getIdUsuario() != null){
					Usuario usuarioB = usuarioFacade.buscarUsuarioPorId(item.getIdUsuario());
					
					if(usuarioB.getNombre().compareTo(usuario)==0){
						if(descripcion == null)
							return item;
						if(!descripcion.isEmpty() && item.getCodigoRSQ() != null && descripcion.equals(item.getCodigoRSQ()))
							return item;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void guardar(boolean mostrarMsg) {

		double cantidadFin=declaracionSustanciaQuimica.getCantidadInicio()==null?0.0:declaracionSustanciaQuimica.getCantidadInicio();
		
		if(declaracionSustanciaQuimica.getCantidadInicio() > cantidadFin){
			JsfUtil.addMessageError("Se rechaza su declaración debido a que los ingresos son mayores a los ingresos reportados");
			return;
		}						
		
		for (MovimientoDeclaracionRSQ movimiento : movimientoDeclaracionRSQLista) {			
			cantidadFin+=movimiento.getValorIngreso()==null?0.0:movimiento.getValorIngreso();
			cantidadFin-=movimiento.getValorEgreso()==null?0.0:movimiento.getValorEgreso();
		}
		
		if(cantidadFin < 0){
			JsfUtil.addMessageError("Se rechaza su declaración debido a que los valores ingresados en los movimientos generan un valor negativo.");
			return;
		}		
		
		for (MovimientoDeclaracionRSQ movimiento : movimientoDeclaracionRSQEliminadosLista) {
			movimiento.setEstado(false); 
			movimientoDeclaracionRSQFacade.guardar(movimiento, JsfUtil.getLoggedUser());			
		}
		movimientoDeclaracionRSQEliminadosLista.clear();
		
		for (MovimientoDeclaracionRSQ movimiento : movimientoDeclaracionRSQLista) {
			movimiento.setDeclaracionSustanciaQuimica(declaracionSustanciaQuimica);
			
			if(!movimiento.getTipoMovimiento().getNombre().equals("SIN MOVIMIENTOS")){
				if(movimiento.getNumeroFactura() != null){
					String factura = movimiento.getNumeroFactura().trim();
					movimiento.setNumeroFactura(factura);
				}				
			}		
			
			movimientoDeclaracionRSQFacade.guardar(movimiento, JsfUtil.getLoggedUser());
		}
		declaracionSustanciaQuimica.setPagoPendiente(false);
		declaracionSustanciaQuimica.setCantidadFin(cantidadFin);		
		declaracionSustanciaQuimicaFacade.guardar(declaracionSustanciaQuimica, JsfUtil.getLoggedUser());
		
		if(mostrarMsg){
			JsfUtil.addMessageInfo("Información guardada correctamente");
		}		
	}
		
	public void enviar(){
		boolean operacionCorrecta=false;		
		try {
			guardar(false);
			if(movimientoDeclaracionRSQLista.isEmpty()) {
				JsfUtil.addMessageWarning("No se han agregado Movimientos");
				return;
			}
			
			CatalogoGeneralCoa estadoEnviado = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.DSQ_ESTADOS_DECLARACION, 2);
			declaracionSustanciaQuimica.setEstadoDeclaracion(estadoEnviado);
			declaracionSustanciaQuimica.setPagoPendiente(false);
			declaracionSustanciaQuimicaFacade.guardar(declaracionSustanciaQuimica, JsfUtil.getLoggedUser()); 
			
			if(!tareaDeActualizacion){
				if(permisoDeclaracion != null && permisoDeclaracion.getId() != null){
					permisoDeclaracion.setStockActual(declaracionSustanciaQuimica.getCantidadFin());
					permisoDeclaracionRSQFacade.guardar(permisoDeclaracion);
				}				
			}			
			
			/**vemos si los movimientos realizados en la declaración deben ser comparados o no.*/
			boolean realizaComparacion = false;
			
			List<MovimientoDeclaracionRSQ> listaMovimientosAux = new ArrayList<>();
			listaMovimientosAux.addAll(movimientoDeclaracionRSQLista);
			
			for(MovimientoDeclaracionRSQ mov : movimientoDeclaracionRSQLista){
				
				if(mov.getTipoMovimiento().getDescripcion().equals("CONSUMO") || 
						mov.getTipoMovimiento().getDescripcion().equals("IMPORTACIÓN") || 
						mov.getTipoMovimiento().getDescripcion().equals("EXPORTACIÓN") || 
						mov.getTipoMovimiento().getDescripcion().equals("SIN MOVIMIENTOS") || 
						mov.getTipoMovimiento().getDescripcion().equals("ELIMINACIÓN O DISPOSICIÓN FINAL")){
					listaMovimientosAux.remove(mov);					
				}else{
					realizaComparacion = true;
				}
			}
			
			Map<String, Object> params=new HashMap<>();
			params.put("operador",JsfUtil.getLoggedUser().getNombre());			
			params.put("idDeclaracion", declaracionSustanciaQuimica.getId());
			params.put("declaracionPendiente", false);
			params.put("declaracionEnviada", true);
			
			if(!realizaComparacion){
				/**
				 * Para las declaraciones que tienen  tipos de movimientos que no se  compara
				 */
				params.put("declaracionCorrecta", true);
				params.put("compararInformacion", false);
			}else{
				/**
				 * AQUI SE REALIZA LA COMPARACIÓN PARA SABER SI SE TERMINA EL TRÁMITE O SE INICIA LA TAREA DE ACTUALIZACIÓN 
				 */
				if(compararDeclaraciones(listaMovimientosAux, declaracionSustanciaQuimica)){
					params.put("declaracionCorrecta", true);
					params.put("compararInformacion", false);
					/**
					 * Se cambia esta variable para que envíe el correo de declaración terminada.
					 */
					realizaComparacion = false;
				}else{
					params.put("declaracionCorrecta", false);
					params.put("compararInformacion", true);
				}				
			}
					
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			Map<String, Object> param=new HashMap<>();
			aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), param);
			
			try {
				Locale locale = new Locale("es", "ES");
			    Calendar calendarInicio = Calendar.getInstance();
			    /*Restamos 1 a la variable porque el mes en calendar es uno menos*/
			    calendarInicio.set(Calendar.MONTH, declaracionSustanciaQuimica.getMesDeclaracion()-1);
			    String monthName=calendarInicio.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);	
				
				Object[] parametrosCorreo = new Object[] {JsfUtil.getLoggedUser().getPersona().getNombre(), 
						declaracionSustanciaQuimica.getRegistroSustanciaQuimica().getNumeroAplicacion(), monthName.toUpperCase()};
				
				String notificacion = "";
				if(!realizaComparacion){
					notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
							"bodyNotificacionAprobacionDeclaracionRSQ",
							parametrosCorreo);	
				}else{
					notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
							"bodyNotificacionObservacionDeclaracionRSQ",
							parametrosCorreo);	
				}								
				
				String emailDestino = "";
				if(JsfUtil.getLoggedUser().getNombre().length() == 10){
					List<Contacto> contacto = contactoFacade.buscarPorPersona(JsfUtil.getLoggedUser().getPersona());
					for (Contacto con : contacto){
						if(con.getFormasContacto().getId() == 5	&& con.getEstado().equals(true)){
							emailDestino = con.getValor();
							break;
						}
					}
				}else{
					Organizacion organizacion = organizacionFacade.buscarPorRuc(JsfUtil.getLoggedUser().getNombre());
					if(organizacion != null){
						List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);
						for (Contacto con : contacto){
							if(con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)){
								emailDestino = con.getValor();
								break;
							}
						}
					}else{
						List<Contacto> contacto = contactoFacade.buscarPorPersona(JsfUtil.getLoggedUser().getPersona());
						for (Contacto con : contacto){
							if(con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)){
								emailDestino = con.getValor();
								break;
							}
						}
					}
				}				

				Usuario usuarioEnvio = new Usuario();
				usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
				NotificacionAutoridadesController email = new NotificacionAutoridadesController();
				email.sendEmailInformacionProponente(emailDestino, "", notificacion, "Declaración de Sustancias Químicas", 
						declaracionSustanciaQuimica.getRegistroSustanciaQuimica().getNumeroAplicacion(), JsfUtil.getLoggedUser(), usuarioEnvio);
						
				operacionCorrecta=true;		
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(!tareaDeActualizacion){
				/**
				 * Esta parte solo se realiza cuando se tiene declaraciones atrasadas y cuando se encuentra en la tarea de Declaracion de Sustancias 
				 * no en tareas de actualización.
				 */
				int mesSiguiente = declaracionSustanciaQuimica.getMesDeclaracion();
				int anioSiguiente = declaracionSustanciaQuimica.getAnioDeclaracion();
										
				if(declaracionSustanciaQuimica.getMesDeclaracion() == 12){
					mesSiguiente = 1;
					anioSiguiente = declaracionSustanciaQuimica.getAnioDeclaracion() + 1;
				}else{
					mesSiguiente = declaracionSustanciaQuimica.getMesDeclaracion() + 1;
				}					
				
				/**en esta parte se inicia las declaraciones si existen*/
				DeclaracionSustanciaQuimica declaracionSiguiente = declaracionSustanciaQuimicaFacade.obtenerPorRSQ(registroSustanciaQuimica, anioSiguiente, mesSiguiente);
				
				if(declaracionSiguiente != null && declaracionSiguiente.getId() != null){
					/**
					 * se debe verificar que la declaración se haga dentro de los 10 días laborables, sino es así se le debe hacer pagar.
					 */					
					
					if(declaracionSiguiente.getPagoPendiente() == false){
						if(validarDiaPago()){						
							CatalogoGeneralCoa valorTasa = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.DSQ_VALOR_TASA, 1);
				    		
				    		double valorMulta = Double.valueOf(valorTasa.getValor());
							declaracionSiguiente.setPagoPendiente(true);
							declaracionSiguiente.setValorMulta(valorMulta);
						}else{
							declaracionSiguiente.setPagoPendiente(false);
						}
					}else{
						declaracionSiguiente.setPagoPendiente(false);
					}					
										
					declaracionSiguiente.setCantidadInicio(declaracionSustanciaQuimica.getCantidadFin());
					declaracionSustanciaQuimicaFacade.guardar(declaracionSiguiente, JsfUtil.getLoggedUser());
					
					declaracionSustanciaQuimicaFacade.iniciarProceso(declaracionSiguiente, JsfUtil.getLoggedUser());
				}				
			}else{
				
				/**
				 * Se tiene las declaraciones en orden para poder aumentar o disminuir el valor de la cantidad de inicio y fin y por rectificar cuando 
				 * se haya realizado alguna modificación en la tarea de actualizar
				 */
				List<DeclaracionSustanciaQuimica> listaSustanciasPosteriores = declaracionSustanciaQuimicaFacade.obtenerPorRSQ(registroSustanciaQuimica);
				
				double cantidadComparar = declaracionSustanciaQuimica.getCantidadFin();
				boolean suma = false;
				
				int i = 0;
				for(DeclaracionSustanciaQuimica decPosterior : listaSustanciasPosteriores){					
										
					if(!decPosterior.getId().equals(declaracionSustanciaQuimica.getId()) && decPosterior.getMesDeclaracion().equals(declaracionSustanciaQuimica.getMesDeclaracion() + i)){
						if(cantidadComparar < decPosterior.getCantidadInicio()){
							suma = false;
						}else if(cantidadComparar > decPosterior.getCantidadInicio()){
							suma = true;
						}else{
							continue;
						}
						
						
						double aumento = 0;
						double diferencia = 0;
						if(suma){
							aumento = cantidadComparar - decPosterior.getCantidadInicio();
							
							decPosterior.setCantidadInicio(cantidadComparar);
							
							if(decPosterior.getCantidadFin() != null){
								double cantidadFin = decPosterior.getCantidadFin();	
								decPosterior.setCantidadFin(cantidadFin + aumento);
								cantidadComparar = decPosterior.getCantidadFin();
							}
							declaracionSustanciaQuimicaFacade.guardar(decPosterior, JsfUtil.getLoggedUser());							
							
						}else{
							diferencia = decPosterior.getCantidadInicio() - cantidadComparar;
							
							decPosterior.setCantidadInicio(cantidadComparar);
							
							if(decPosterior.getCantidadFin() != null){
								double cantidadFin = decPosterior.getCantidadFin();	
								decPosterior.setCantidadFin(cantidadFin - diferencia);
								cantidadComparar = decPosterior.getCantidadFin();
							}
							declaracionSustanciaQuimicaFacade.guardar(decPosterior, JsfUtil.getLoggedUser());
						}
					}
					i ++;
				}
				
				List<DeclaracionSustanciaQuimica> listaSusP= declaracionSustanciaQuimicaFacade.obtenerPorRSQ(registroSustanciaQuimica);
				if(listaSusP != null && !listaSusP.isEmpty()){
					int index = listaSusP.size();
					
					DeclaracionSustanciaQuimica declaracion = listaSusP.get(index -1);
					
					if(permisoDeclaracion != null && permisoDeclaracion.getId() != null){
						if(declaracion.getCantidadFin() != null){
							permisoDeclaracion.setStockActual(declaracion.getCantidadFin());
						}else{
							permisoDeclaracion.setStockActual(declaracion.getCantidadInicio());
						}
						permisoDeclaracionRSQFacade.guardar(permisoDeclaracion);
					}					
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(operacionCorrecta){
			JsfUtil.cargarObjetoSession("idDeclaracionSQ",null);
			JsfUtil.addMessageInfo("Declaración Realizada");
			JsfUtil.redirectToBandeja();
		}
	}
	
	public boolean validarDiaPago(){
		try {
//			SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
			
			Calendar fechaActual = Calendar.getInstance();
			int mesActual = fechaActual.get(Calendar.MONTH);
			int anioActual = fechaActual.get(Calendar.YEAR);								
			
			Calendar inicioMes = Calendar.getInstance();
			inicioMes.set(Calendar.DATE, 1);
			inicioMes.set(Calendar.MONTH, mesActual);
			inicioMes.set(Calendar.YEAR, anioActual);			
			
			CatalogoGeneralCoa valorDias = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.DSQ_DIAS_DECLARACION, 1);
    		
    		int dias = Integer.valueOf(valorDias.getValor()) + 1;
			
			Date fechaLimite = fechaFinal(inicioMes.getTime(), dias);
			
			if(fechaActual.getTime().after(fechaLimite)){
				return true;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private Date fechaFinal(Date fechaInicial, int diasRequisitos) throws ServiceException{
		Date fechaFinal = new Date();		
		Calendar fechaPrueba = Calendar.getInstance();
		fechaPrueba.setTime(fechaInicial);	
		
		int i = 0;
		while(i < diasRequisitos){
			fechaPrueba.add(Calendar.DATE, 1);
			
			Date fechaFeriado = fechaPrueba.getTime();
			
			List<Holiday> listaFeriados = feriadoFacade.listarFeriadosNacionalesPorRangoFechas(fechaFeriado, fechaFeriado);
			
			if(fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){
				if(listaFeriados == null || listaFeriados.isEmpty()){
					i++;
				}					
			}					
		}
		
		fechaFinal = fechaPrueba.getTime();			
		return fechaFinal;	
	}
	
	public boolean compararDeclaraciones(List<MovimientoDeclaracionRSQ> movimientos, DeclaracionSustanciaQuimica declaracion){
		try {
			
			List<String> listaMovimientosIncorrectos = new ArrayList<>();
			
			for (MovimientoDeclaracionRSQ movimiento : movimientos) {
				
				if (movimiento.getIdRegistroSustancias() != null) {
					
					List<MovimientoDeclaracionRSQ> movimientosOpuestos = movimientoDeclaracionRSQFacade
							.obtenerListaPorRegistroFacMesAnio(movimiento.getIdRegistroSustancias(), declaracion.getMesDeclaracion(), declaracion.getAnioDeclaracion(), 
									movimiento.getNumeroFactura(), declaracion.getSustanciaQuimica().getId(), movimiento.getTipoPresentacion().getId());
					
					if(movimientosOpuestos != null && !movimientosOpuestos.isEmpty()){
						for(MovimientoDeclaracionRSQ movOpuesto : movimientosOpuestos){
							if(movimiento.getValorEgreso() != null && !movimiento.getValorEgreso().equals(0.0)){
								if(movOpuesto.getValorIngreso() != null && !movOpuesto.getValorIngreso().equals(0.0)){
									if(!movimiento.getValorEgreso().equals(movOpuesto.getValorIngreso())){
										listaMovimientosIncorrectos.add("Incorrecto");
									}									
								}else{
									listaMovimientosIncorrectos.add("Incorrecto");
								}
							}else if(movimiento.getValorIngreso() != null && !movimiento.getValorIngreso().equals(0.0)){	
								if(movOpuesto.getValorEgreso() != null && !movOpuesto.getValorEgreso().equals(0.0)){
									if(!movimiento.getValorIngreso().equals(movOpuesto.getValorEgreso())){
										listaMovimientosIncorrectos.add("Incorrecto");
									}
								}else{
									listaMovimientosIncorrectos.add("Incorrecto");
								}
							}else{
								listaMovimientosIncorrectos.add("Incorrecto");
							}
						}
					}else{
						listaMovimientosIncorrectos.add("Incorrecto");
					}
				}else{
					listaMovimientosIncorrectos.add("Incorrecto");
				}		
			}
			
			if(listaMovimientosIncorrectos.isEmpty()){
				return true;
			}else{
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public void cancelar() {
    	JsfUtil.cargarObjetoSession("idDeclaracionSQ",null);
    	JsfUtil.redirectToBandeja();
    }
	
	public synchronized void aprobarTarea(Usuario usuario, Long taskId, Long processId, Map<String, Object> data)
			throws JbpmException {
		if(data != null) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				if(entry.getValue() instanceof String) {
					data.put(entry.getKey(), new String(((String) entry.getValue()).getBytes(Charset.forName("UTF-8"))));
				}
			}
		}
		taskBeanFacade.approveTask(usuario.getNombre(), taskId, processId, data, usuario.getPassword(),
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(),
				Constantes.getNotificationService());
	}
	
	public StreamedContent descargarDocumento() throws Exception {		
		
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documentoOficio != null) {
			if (documentoOficio.getContenidoDocumento() == null) {
				documentoOficio.setContenidoDocumento(documentosFacade
						.descargar(documentoOficio.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documentoOficio.getContenidoDocumento()), documentoOficio.getExtesion());
			content.setName(documentoOficio.getNombre());
		}
		
		return content;
	}
	
	public String operadorListener(Usuario usuario, MovimientoDeclaracionRSQ mov){
		String operador = "";
		try {		
			if(usuario != null){
				if(usuario.getNombre().length() == 13){
					
					Organizacion org = organizacionFacade.buscarPorRuc(usuario.getNombre());
					if(org != null){
						operador = org.getNombre();
					}else{
						operador = usuario.toString();
					}
					
				}else{
					operador = usuario.toString();
				}
			}else{
				RegistroSustanciaQuimica reg = registroSustanciaQuimicaFacade.obtenerRegistroPorId(mov.getIdRegistroSustancias());
				operador = reg.getNombreRepLegal();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return operador;
	}
	
	
	
}
