package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.CultivoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.DocumentoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.DosisCultivoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.EfectoOrganismosAcuaticosFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.OficioPronunciamientoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.PlagaCultivoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.PlagaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProductoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasDetalleFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.Cultivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DosisCultivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.EfectoOrganismosAcuaticos;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.OficioPronunciamientoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.Plaga;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.PlagaCultivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProductoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidas;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidasDetalle;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.TipoOrganismoAcuatico;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.bandeja.controllers.BandejaTareasController;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ViewScoped
@ManagedBean
public class ActualizarInformacionEtiquetaController implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(ActualizarInformacionEtiquetaController.class);
	
	@ManagedProperty(value = "#{wizardBean}")
	@Getter
	@Setter
	private WizardBean wizardBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProductoPquaFacade productoPquaFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private CultivoFacade cultivoFacade;
	@EJB
	private PlagaFacade plagaFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
    private OrganizacionFacade organizacionFacade;
	@EJB
    private ProyectoPlaguicidasFacade proyectoPlaguicidasFacade;
	@EJB
	private BandejaFacade bandejaFacade;
	@EJB
    private ProyectoPlaguicidasDetalleFacade proyectoPlaguicidasDetalleFacade;
	@EJB
    private PlagaCultivoFacade plagaCultivoFacade;
	@EJB
    private DosisCultivoFacade dosisCultivoFacade;
	@EJB
    private EfectoOrganismosAcuaticosFacade efectoOrganismosAcuaticosFacade;
	@EJB
	private DocumentoPquaFacade documentosFacade;
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	@EJB
    private AreaFacade areaFacade;
	@EJB
	private OficioPronunciamientoPquaFacade oficioPronunciamientoPquaFacade;
	@EJB
	private ContactoFacade contactoFacade;
	
	@Getter
	@Setter
	private List<ProductoPqua> listaProductos;
	
	@Getter
	@Setter
	private List<CatalogoGeneralCoa> listaTipoProducto, listaTipoRecipiente, listaTipoCategoria, listaColor, listaUnidades;
	
	@Getter
	@Setter
	private List<Cultivo> listaCultivos;
	
	@Getter
	@Setter
	private List<Plaga> listaPlagas;
	
	@Getter
	@Setter
	private List<ProyectoPlaguicidasDetalle> listaDetalleProyectoPlaguicidas, listaDetalleEliminar;
	
	@Getter
	@Setter
	private List<PlagaCultivo> listaPlagaCultivo, listaPlagaCultivoEliminar;
	
	@Getter
	@Setter
	private List<DosisCultivo> listaDosisCultivo, listaDosisCultivoEliminar;
	
	@Getter
	@Setter
	private List<String> listaNombresCultivos, listaNombresPlagas;
	
	@Getter
	@Setter
	private List<EfectoOrganismosAcuaticos> listaEfectosPeces, listaEfectosCrustaceos, listaEfectosAlgas, listaComponentesResumen, listaEfectosEliminar;
	
	@Getter
	@Setter
	private ProductoPqua productoReporte, productoReporteEliminar;

	@Getter
	@Setter
	private DocumentoPqua documentoOficioProrroga, documentoRespaldo, documentoEtiqueta;
	
	@Getter
	@Setter
	private ProyectoPlaguicidas proyectoPlaguicidas;
	
	@Getter
	@Setter
	private ProyectoPlaguicidasDetalle detalleProyectoPlaguicidas, detalleProyectoSeleccionado;
	
	@Getter
	@Setter
	private PlagaCultivo nuevoPlagaCultivo;
	
	@Getter
	@Setter
	private DosisCultivo nuevaDosisCultivo;
	
	@Getter
	@Setter
	private Boolean guardado, aceptarCondiciones, habilitarCultivo, editarDetalle, esSubsanacion, bloquearSubsanacion;
	
	@Getter
	@Setter
	private String codigoProyecto, nombreComercial, dosis, unidadMedida, nombreOperador, cedula, correo;
	
	@Getter
	@Setter
	private Integer numeroRevision, numeroRevisionOficio;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@PostConstruct
	public void init(){
		
		try {
			
			inicializarObjetos();
			
			esSubsanacion = false;
			bloquearSubsanacion = false;
			
			if(bandejaTareasBean.getProcessId() > 0) {
				//si ya existe el proceso
				variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
				codigoProyecto = (String) variables.get("tramite");
				
				proyectoPlaguicidas = proyectoPlaguicidasFacade.getPorCodigoProyecto(codigoProyecto);
				productoReporte = proyectoPlaguicidas.getProductoPqua();
				
				cargarDatos();
				
				String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
				if (tarea.equals("subsanarObservacionesPqua")) {
					esSubsanacion = true;
				}
			}
			
			if(esSubsanacion) {
				//Validar que este dentro de los 30 días
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				
				Date fechaActual = formatter.parse(formatter.format(new Date()));
				
				numeroRevision = Integer.valueOf((String) variables.get("numeroRevision"));
				OficioPronunciamientoPqua oficioRevision = oficioPronunciamientoPquaFacade.getPorProyectoRevision(proyectoPlaguicidas.getId(), numeroRevision);
				
				numeroRevisionOficio = oficioRevision.getNumeroRevision();
				
				Date fechaOficio = oficioRevision.getFechaFirma();
					    
				Date fechaFirmaObservacion = formatter.parse(formatter.format(fechaOficio));
				Calendar fechaFin = Calendar.getInstance();
				fechaFin.setTime(fechaFirmaObservacion);
				fechaFin.add(Calendar.DATE, 30);
				
				Date maxFechaRespuesta = fechaFin.getTime();
				
				if(maxFechaRespuesta.compareTo(fechaActual) < 0) {
					bloquearSubsanacion = true;
					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('dlgMsjTiempoSubanacion').show();");
				}
			}
			
			if(proyectoPlaguicidas.getId() != null) {
				listaProductos.add(proyectoPlaguicidas.getProductoPqua()); //se agrega el producto para visualización en el combobox
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}
	}
	
	public void validarTareaBpm() {
		if(JsfUtil.getCurrentTask() != null) {
			JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
					"/pages/rcoa/plaguicidasQuimicosUsoAgricola/ingresarInformacionProducto.jsf");
		}
	}
	
	public void inicializarObjetos() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		
		String sDate1 = "01/" + month + "/" + year;
		Date fechaMesIngreso = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
		
		listaProductos = productoPquaFacade.listaProductosPorUsuarioFecha(JsfUtil.getLoggedUser().getNombre(), fechaMesIngreso);
		
		productoReporte = new ProductoPqua();
		proyectoPlaguicidas = new ProyectoPlaguicidas();
		listaDetalleProyectoPlaguicidas = new ArrayList<>();
		listaDosisCultivoEliminar = new ArrayList<>();
		listaPlagaCultivoEliminar = new ArrayList<>();
		listaDetalleEliminar = new ArrayList<>();
		listaEfectosPeces = new ArrayList<>();
		listaEfectosCrustaceos = new ArrayList<>();
		listaEfectosAlgas = new ArrayList<>();
		listaComponentesResumen = new ArrayList<>();
		listaEfectosEliminar = new ArrayList<>();
		
		listaTipoProducto = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_TIPO_PRODUCTO);
		listaTipoRecipiente = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_TIPO_RECIPIENTE);
		listaTipoCategoria = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_TIPO_CATEGORIA);
		listaColor = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_COLOR_FRANJA);
		listaUnidades = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_UNIDADES);
		
		listaCultivos = cultivoFacade.listaCultivos();
		listaPlagas = plagaFacade.listaPlagas();
		
		unidadMedida = EfectoOrganismosAcuaticos.UNIDAD_MEDIDA;
		
		List<Contacto> listaContactos = new ArrayList<>();
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario.getId());
		Persona titular = usuario.getPersona();
		Organizacion org = organizacionFacade.buscarPorPersona(titular, usuario.getNombre());	            
		if (org == null) {
			nombreOperador = titular.getNombre();
			cedula = titular.getPin();
			
			listaContactos = titular.getContactos();
		} else {	            	
			nombreOperador = org.getPersona().getNombre();	                
			cedula=org.getPersona().getPin();
			
			listaContactos = org.getContactos();
		}
		
		habilitarCultivo = true;
		guardado = false;
		
		
		correo = "";
		for(Contacto contacto : listaContactos){
			if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
				correo = contacto.getValor();
				break;
			}            						
		} 
	}
	
	public void cargarDatos() throws Exception {
		listaDetalleProyectoPlaguicidas = proyectoPlaguicidasDetalleFacade.getDetallePorProyecto(proyectoPlaguicidas.getId());
		
		listaEfectosPeces = efectoOrganismosAcuaticosFacade.getPorProyectoTipo(proyectoPlaguicidas.getId(), TipoOrganismoAcuatico.ID_PECES);
		listaEfectosCrustaceos = efectoOrganismosAcuaticosFacade.getPorProyectoTipo(proyectoPlaguicidas.getId(), TipoOrganismoAcuatico.ID_CRUSTACEOS);
		listaEfectosAlgas = efectoOrganismosAcuaticosFacade.getPorProyectoTipo(proyectoPlaguicidas.getId(), TipoOrganismoAcuatico.ID_ALGAS);
		
		List<DocumentoPqua> documentosRespaldo = documentosFacade.documentoPorTablaIdPorIdDoc(proyectoPlaguicidas.getId(), TipoDocumentoSistema.PQUA_DOCUMENTO_RESPALDO, "RespaldoProyectoPlaguicidas");
		if(documentosRespaldo != null && documentosRespaldo.size() > 0) {
			documentoRespaldo = documentosRespaldo.get(0);
		}
		
		List<DocumentoPqua> documentosEtiqueta = documentosFacade.documentoPorTablaIdPorIdDoc(proyectoPlaguicidas.getId(), TipoDocumentoSistema.PQUA_DOCUMENTO_ETIQUETA, "RespaldoEtiquetaProyectoPlaguicidas");
		if(documentosEtiqueta != null && documentosEtiqueta.size() > 0) {
			documentoEtiqueta = documentosEtiqueta.get(0);
		}
		
		List<DocumentoPqua> documentosProrroga = documentosFacade.documentoPorTablaIdPorIdDoc(proyectoPlaguicidas.getId(), TipoDocumentoSistema.PQUA_OFICIO_PRORROGA, "RespaldoOficioProrroga");
		if(documentosProrroga != null && documentosProrroga.size() > 0) {
			documentoOficioProrroga = documentosProrroga.get(0);
		}
	}
	
	public String btnSiguiente(){
		guardado = false;

		return null;

	}
	
	public String btnAtras(){
		guardado = false;
		
		return null;
	}
	
	public void ocultarFormulario() {

		wizardBean.setCurrentStep("paso1");
		Wizard wizard = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:wizardGenerador");
		wizard.setStep("paso1");
		RequestContext.getCurrentInstance().update("form:wizardGenerador");
	}
	
	public void seleccionarProductoReporte() {
		
		if(proyectoPlaguicidas.getId() != null && proyectoPlaguicidas.getProductoPqua().getId() != productoReporte.getId()) {
			productoReporteEliminar = proyectoPlaguicidas.getProductoPqua();
			
			listaEfectosEliminar.addAll(listaEfectosPeces);
			listaEfectosEliminar.addAll(listaEfectosCrustaceos);
			listaEfectosEliminar.addAll(listaEfectosAlgas);
			
			listaEfectosPeces = new ArrayList<>();
			listaEfectosCrustaceos = new ArrayList<>();
			listaEfectosAlgas = new ArrayList<>();
			listaComponentesResumen = new ArrayList<>();
		}
		
		if(!esSubsanacion && productoReporte != null && productoReporte.getProrroga()) {
			Date fechaRegistro = productoReporte.getRegistroProducto().getMesIngreso();
			
			Calendar calendarFechaRegistro = Calendar.getInstance();
			calendarFechaRegistro.setTime(fechaRegistro);
			calendarFechaRegistro.add(Calendar.DATE, 30);
			
			Date fechaConProrroga = calendarFechaRegistro.getTime();
			
			if(fechaConProrroga.before(new Date())) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dlgMsjProrroga').show();");
				context.update("form");
				
				return;
			}
		}
		
		guardado = false;
	}

	public void uploadProrroga(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoOficioProrroga = new DocumentoPqua();
		documentoOficioProrroga.setContenidoDocumento(contenidoDocumento);
		documentoOficioProrroga.setNombre(event.getFile().getFileName());
		documentoOficioProrroga.setMime("application/pdf");
		documentoOficioProrroga.setIdTipoDocumento(TipoDocumentoSistema.PQUA_OFICIO_PRORROGA.getIdTipoDocumento());
	}

	public void validateRegistroProducto(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(productoReporte != null && productoReporte.getProrroga() != null && productoReporte.getProrroga()) {
			if(documentoOficioProrroga == null || (documentoOficioProrroga.getContenidoDocumento() == null 
				&& documentoOficioProrroga.getId() == null)){
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar Oficio de prórroga", null));
			}
		}
		
		if (!errorMessages.isEmpty()) {
			guardado = false;
			throw new ValidatorException(errorMessages);
		}
	}
	
	public void guardarRegistroProducto() {
		try {
			Boolean iniciarProceso = (proyectoPlaguicidas.getId() == null) ? true : false;
			
			if(proyectoPlaguicidas.getId() == null) {
				ProyectoPlaguicidas proy = proyectoPlaguicidasFacade.getPorProducto(productoReporte.getId());
				if(proy != null && proy.getId() != null) {
					proyectoPlaguicidas = proy;
					iniciarProceso = proyectoPlaguicidasFacade.validarInicioTramite(proyectoPlaguicidas.getCodigoProyecto());
				}
			} else if(bandejaTareasBean.getProcessId() == 0) {
				iniciarProceso = proyectoPlaguicidasFacade.validarInicioTramite(proyectoPlaguicidas.getCodigoProyecto());
			}
				
						
			proyectoPlaguicidas.setProductoPqua(productoReporte);
			proyectoPlaguicidasFacade.guardar(proyectoPlaguicidas);
			
			productoReporte.setProductoActualizado(true);
			productoPquaFacade.guardar(productoReporte);
			
			proyectoPlaguicidas.setProductoPqua(productoReporte); //para setear el producto con el cambio de estado
			
			if(productoReporteEliminar != null && productoReporteEliminar.getId() != null) {
				for(EfectoOrganismosAcuaticos efecto : listaEfectosEliminar) {
					if(efecto.getId() != null) {
						efecto.setEstado(false);
						
						efectoOrganismosAcuaticosFacade.guardar(efecto);
					}
				}
				
				productoReporteEliminar.setProductoActualizado(false);
				productoPquaFacade.guardar(productoReporteEliminar);
			}
			
			if(productoReporte.getProrroga() != null && productoReporte.getProrroga()) {
				if(documentoOficioProrroga != null && documentoOficioProrroga.getContenidoDocumento() != null) {
					documentoOficioProrroga.setIdTabla(proyectoPlaguicidas.getId());
					documentoOficioProrroga.setNombreTabla("RespaldoOficioProrroga");
					
					documentoOficioProrroga = documentosFacade.guardarDocumentoAlfresco(proyectoPlaguicidas.getCodigoProyecto(), "ACTUALIZACION_ETIQUETADO_PLAGUICIDAS", 
							0L, documentoOficioProrroga, TipoDocumentoSistema.PQUA_OFICIO_PRORROGA);
				}
			}
			
			if(iniciarProceso) {
				BandejaTareasController bandejaTareasController =JsfUtil.getBean(BandejaTareasController.class);
				bandejaTareasController.cargarInfoProponente(JsfUtil.getLoggedUser()); //para cargar la informacion del operador en la sección del contexto
				
				Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
				parametros.put("tramite", proyectoPlaguicidas.getCodigoProyecto());
				parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
				parametros.put("numeroRevision", 1);
				parametros.put("idProyecto", proyectoPlaguicidas.getId());
				parametros.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, JsfUtil.getLoggedUser().getNombre());
				
				try {
					Long procesoInstanceId = procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_PROCESO_ACTUALIZACION_ETIQUETADO_PLAGUICIDAS, proyectoPlaguicidas.getCodigoProyecto(), parametros);
					bandejaTareasBean.setProcessId(procesoInstanceId);
				} catch (Exception ex) {
					ex.printStackTrace();
					productoReporte.setProductoActualizado(false);
					productoPquaFacade.guardar(productoReporte);
					
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
					
					return;
				}
				
				ProcessInstanceLog proceso = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
				TaskSummary tareaActual = procesoFacade.getCurrenTask(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
				TaskSummaryCustom nueva = new TaskSummaryCustomBuilder().fromSuiaIII1(proceso.getProcessName(),tareaActual, proyectoPlaguicidas.getCodigoProyecto()).build();
				bandejaTareasBean.setTarea(nueva);
			}
			
			guardado = true;
			
			List<String> listaComposicionProducto = Arrays.asList(productoReporte.getComposicionProducto().split(";"));
			for (String ingrediente : listaComposicionProducto) {
				Boolean ingresarPeces = true;
				Boolean ingresarCrustaceos = true;
				Boolean ingresarAlgas = true;
				
				if(listaEfectosPeces != null && listaEfectosPeces.size() > 0) {
					for (EfectoOrganismosAcuaticos item : listaEfectosPeces) {
						if(item.getIngredienteActivo().equals(ingrediente)) {
							ingresarPeces = false;
						}
					}
				} 
				
				if(listaEfectosCrustaceos != null && listaEfectosCrustaceos.size() > 0) {
					for (EfectoOrganismosAcuaticos item : listaEfectosCrustaceos) {
						if(item.getIngredienteActivo().equals(ingrediente)) {
							ingresarCrustaceos = false;
						}
					}
				} 
				
				if(listaEfectosAlgas != null && listaEfectosAlgas.size() > 0) {
					for (EfectoOrganismosAcuaticos item : listaEfectosAlgas) {
						if(item.getIngredienteActivo().equals(ingrediente)) {
							ingresarAlgas = false;
						}
					}
				} 
				
				if(ingresarPeces) {
					EfectoOrganismosAcuaticos efectoPeces = new EfectoOrganismosAcuaticos();
					efectoPeces.setIngredienteActivo(ingrediente);
					efectoPeces.setIdTipoOrganismo(TipoOrganismoAcuatico.ID_PECES);
					listaEfectosPeces.add(efectoPeces);
				}
				
				if(ingresarCrustaceos) {
					EfectoOrganismosAcuaticos efectoCrustaceos = new EfectoOrganismosAcuaticos();
					efectoCrustaceos.setIngredienteActivo(ingrediente);
					efectoCrustaceos.setIdTipoOrganismo(TipoOrganismoAcuatico.ID_CRUSTACEOS);
					listaEfectosCrustaceos.add(efectoCrustaceos);
				}
				
				if(ingresarAlgas) {
					EfectoOrganismosAcuaticos efectoAlgas = new EfectoOrganismosAcuaticos();
					efectoAlgas.setIngredienteActivo(ingrediente);
					efectoAlgas.setIdTipoOrganismo(TipoOrganismoAcuatico.ID_ALGAS);
					listaEfectosAlgas.add(efectoAlgas);
				}
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void bloquearIngresoTramite() {
		try {
			
			productoReporte.setProductoActualizado(true);
			productoPquaFacade.guardar(productoReporte);
			
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void bloquearIngresoSubsanacion() {
		try {
			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("cumpleSubsanacion", false);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			proyectoPlaguicidas.setResultadoRevision(ProyectoPlaguicidas.TRAMITE_ARCHIVADO);
			proyectoPlaguicidas.setFechaResultado(new Date());
			proyectoPlaguicidasFacade.guardar(proyectoPlaguicidas);
			
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	/*
	 * ACTUALIZACIÓN DE ETIQUETADO
	 */
	public void validarColorEtiqueta() {
		CatalogoGeneralCoa colorFranja = null;
		for (CatalogoGeneralCoa item : listaColor) {
			if(item.getNombre().toLowerCase().equals(proyectoPlaguicidas.getProductoPqua().getCategoriaFinal().getValor().toLowerCase())) {
				colorFranja = item;
				break;
			}
		}
		
		proyectoPlaguicidas.getProductoPqua().setColorFranjaFinal(colorFranja);
	}
	
	public void agregarDetalle() {
		guardado = false;
		habilitarCultivo = true;
		editarDetalle = false;
		
		detalleProyectoPlaguicidas = new ProyectoPlaguicidasDetalle();
		nuevoPlagaCultivo = new PlagaCultivo();
		nuevaDosisCultivo = new DosisCultivo();
		
		listaPlagaCultivo = new ArrayList<>();
		listaDosisCultivo = new ArrayList<>();
		
		RequestContext.getCurrentInstance().update("pnlButtonsEtiqueta");
	}
	
	public void recuperarNombresCientificos() {
		String nombre = detalleProyectoPlaguicidas.getCultivo().getNombreCientifico();
		listaNombresCultivos = Arrays.asList(nombre.split(";"));
		
		detalleProyectoPlaguicidas.setNombreCientificoCultivo(null);
		nuevoPlagaCultivo = new PlagaCultivo();
		listaNombresPlagas = new ArrayList<>();
	}
	
	public void recuperarNombresComunes() {
		
		listaNombresPlagas = new ArrayList<>();
		if(nuevoPlagaCultivo.getPlaga() != null) {
			String nombre = nuevoPlagaCultivo.getPlaga().getNombreComun();
			listaNombresPlagas = Arrays.asList(nombre.split(";"));
		}
		
		nuevoPlagaCultivo.setNombreComunPlaga(null);
	}
	
	public void aceptarPlaga() {
		habilitarCultivo = false;
		
		listaPlagaCultivo.add(nuevoPlagaCultivo);
		
		nuevoPlagaCultivo = new PlagaCultivo();
		listaNombresPlagas = new ArrayList<>();
	}
	
	public void aceptarDosis() {
		habilitarCultivo = false;
		
		listaDosisCultivo.add(nuevaDosisCultivo);
		
		nuevaDosisCultivo = new DosisCultivo();
	}
	
	public void eliminarPlaga(PlagaCultivo plaga) {
		if(plaga.getId() != null) {
			plaga.setEstado(false);
			listaPlagaCultivoEliminar.add(plaga);
		}
		
		listaPlagaCultivo.remove(plaga);
	}
	
	public void eliminarDosis(DosisCultivo dosis) {
		if(dosis.getId() != null) {
			dosis.setEstado(false);
			listaDosisCultivoEliminar.add(dosis);
		}
		
		listaDosisCultivo.remove(dosis);
	}
	
	public void validatePlagasDosis(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(listaPlagaCultivo == null 
				|| listaPlagaCultivo.size() == 0){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar información de plagas", null));
		}
		
		if(listaDosisCultivo == null 
				|| listaDosisCultivo.size() == 0){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar información de dosis", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void aceptarAdicionarCultivo() {
		
		detalleProyectoPlaguicidas.setListaPlagaCultivo(listaPlagaCultivo);
		detalleProyectoPlaguicidas.setListaDosisCultivo(listaDosisCultivo);
		
		listaDetalleProyectoPlaguicidas.add(detalleProyectoPlaguicidas);
		
		habilitarCultivo = true;
		
		detalleProyectoPlaguicidas = new ProyectoPlaguicidasDetalle();
		nuevoPlagaCultivo = new PlagaCultivo();
		nuevaDosisCultivo = new DosisCultivo();
		
		listaPlagaCultivo = new ArrayList<>();
		listaDosisCultivo = new ArrayList<>();
	}
	
	public void limpiarAdicionarCultivo() {
		habilitarCultivo = true;
		
		if(editarDetalle) {
			listaDetalleProyectoPlaguicidas.add(detalleProyectoSeleccionado);
		}
		
		detalleProyectoPlaguicidas = new ProyectoPlaguicidasDetalle();
		nuevoPlagaCultivo = new PlagaCultivo();
		nuevaDosisCultivo = new DosisCultivo();
		
		listaPlagaCultivo = new ArrayList<>();
		listaDosisCultivo = new ArrayList<>();
		
		listaNombresCultivos = new ArrayList<>();
		listaNombresPlagas = new ArrayList<>();
	}
	
	public void editarDetalle(ProyectoPlaguicidasDetalle detalle) {
		habilitarCultivo = false;
		editarDetalle = true;
		
		detalleProyectoSeleccionado = (ProyectoPlaguicidasDetalle) SerializationUtils.clone(detalle);
		
		detalleProyectoPlaguicidas = detalle;
		listaPlagaCultivo = detalle.getListaPlagaCultivo();
		listaDosisCultivo = detalle.getListaDosisCultivo();
		
		String nombre = detalleProyectoPlaguicidas.getCultivo().getNombreCientifico();
		listaNombresCultivos = Arrays.asList(nombre.split(";"));
		
		nuevoPlagaCultivo = new PlagaCultivo();
		nuevaDosisCultivo = new DosisCultivo();
		
		listaDetalleProyectoPlaguicidas.remove(detalle);
	}
	
	public void eliminarDetalle(ProyectoPlaguicidasDetalle detalle) {
		if(detalle.getId() != null) {
			detalle.setEstado(false);
			listaDetalleEliminar.add(detalle);
		}
		
		listaDetalleProyectoPlaguicidas.remove(detalle);
	}

	public void validateActualizacionEtiqueta(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(listaDetalleProyectoPlaguicidas == null || listaDetalleProyectoPlaguicidas.size() == 0){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar el Detalle del producto", null));
		}
		
		if (!errorMessages.isEmpty()) {
			guardado = false;
			throw new ValidatorException(errorMessages);
		}
	}
	
	public void guardarActualizacionEtiqueta() {
		try {
			
			productoPquaFacade.guardar(proyectoPlaguicidas.getProductoPqua());
			
			proyectoPlaguicidasFacade.guardar(proyectoPlaguicidas);
			
			
			for (ProyectoPlaguicidasDetalle detalle : listaDetalleProyectoPlaguicidas) {
				
				List<PlagaCultivo> listaPlagasCultivo = new ArrayList<>();
				List<DosisCultivo> listaDosisCultivo = new ArrayList<>();
				
				listaPlagasCultivo.addAll(detalle.getListaPlagaCultivo());
				listaDosisCultivo.addAll(detalle.getListaDosisCultivo());
				
				detalle.setProyecto(proyectoPlaguicidas);
				proyectoPlaguicidasDetalleFacade.guardar(detalle);
				
				for (PlagaCultivo plagaCultivo : listaPlagasCultivo) {
					plagaCultivo.setIdDetalleProyecto(detalle.getId());
					
					plagaCultivoFacade.guardar(plagaCultivo);
				}
				
				for (DosisCultivo dosisCultivo : listaDosisCultivo) {
					dosisCultivo.setIdDetalleProyecto(detalle.getId());
					
					dosisCultivoFacade.guardar(dosisCultivo);
				}
				
			}
			
			for (ProyectoPlaguicidasDetalle detalle : listaDetalleEliminar) {
				proyectoPlaguicidasDetalleFacade.eliminar(detalle);
			}
			
			for (PlagaCultivo detalle : listaPlagaCultivoEliminar) {
				plagaCultivoFacade.guardar(detalle);
			}
			
			for (DosisCultivo detalle : listaDosisCultivoEliminar) {
				dosisCultivoFacade.guardar(detalle);
			}
			
			listaDetalleProyectoPlaguicidas = proyectoPlaguicidasDetalleFacade.getDetallePorProyecto(proyectoPlaguicidas.getId());
			listaDetalleEliminar = new ArrayList<>();
			listaPlagaCultivoEliminar = new ArrayList<>();
			listaDosisCultivoEliminar = new ArrayList<>();
			
			guardado = true;
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	// fin actualizacion de etiquetado
	
	/*
	 * EFECTOS ORGANISMOS ACUATICOS
	 */
	public void uploadListenerRespaldo(FileUploadEvent event) {
		EfectoOrganismosAcuaticos efecto = new EfectoOrganismosAcuaticos();
		efecto = (EfectoOrganismosAcuaticos) event.getComponent().getAttributes().get("efecto");
		
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentoPqua documentoRespaldo = new DocumentoPqua();
		documentoRespaldo.setContenidoDocumento(contenidoDocumento);
		documentoRespaldo.setNombre(event.getFile().getFileName());
		documentoRespaldo.setMime("application/pdf");
		documentoRespaldo.setIdTipoDocumento(TipoDocumentoSistema.PQUA_RESPALDO_EFECTOS_ACUATICOS.getIdTipoDocumento());
		
		efecto.setRespaldo(documentoRespaldo);
		
		guardado = false;
	}
	
	public void validateEfectosAcuaticos(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		List<EfectoOrganismosAcuaticos> listaEfectosTotal = new ArrayList<>();
		listaEfectosTotal.addAll(listaEfectosPeces);
		listaEfectosTotal.addAll(listaEfectosCrustaceos);
		listaEfectosTotal.addAll(listaEfectosAlgas);
		
		for (EfectoOrganismosAcuaticos item : listaEfectosTotal) {
			if(item.getRespaldo() == null 
					|| (item.getRespaldo().getContenidoDocumento() == null && item.getRespaldo().getId() == null)) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,"El campo 'Adjuntar respaldo' es requerido", null));
			}
		}
		
		if (!errorMessages.isEmpty()) {
			guardado = false;
			throw new ValidatorException(errorMessages);
		}
	}
	
	public void guardarEfectosAcuaticos() {
		try {
			
			List<EfectoOrganismosAcuaticos> listaEfectosTotal = new ArrayList<>();
			listaEfectosTotal.addAll(listaEfectosPeces);
			listaEfectosTotal.addAll(listaEfectosCrustaceos);
			listaEfectosTotal.addAll(listaEfectosAlgas);
			
			listaComponentesResumen = new ArrayList<>();
			
			for (EfectoOrganismosAcuaticos item : listaEfectosTotal) {
				item.setIdProyecto(proyectoPlaguicidas.getId());
				item.setUnidad(unidadMedida);
				
				recuperarInfoEfectosAcuaticos(item);
				
				efectoOrganismosAcuaticosFacade.guardar(item);
				
				if(item.getRespaldo() != null && item.getRespaldo().getContenidoDocumento() != null) {
					DocumentoPqua documentoA = item.getRespaldo();
					documentoA.setIdTabla(item.getId());
					documentoA.setNombreTabla("RespaldoEfectosOrganismosAcuaticos");
					
					documentosFacade.guardarDocumentoAlfresco(proyectoPlaguicidas.getCodigoProyecto(), "ACTUALIZACION_ETIQUETADO_PLAGUICIDAS", 
							0L, documentoA, TipoDocumentoSistema.PQUA_RESPALDO_EFECTOS_ACUATICOS);
				}
				
				List<EfectoOrganismosAcuaticos> listaComponentesResumenAux = new ArrayList<>();
				listaComponentesResumenAux.addAll(listaComponentesResumen);
				
				if(listaComponentesResumen.size() > 0) {
					Boolean existeComponente = false;
					for (EfectoOrganismosAcuaticos componente : listaComponentesResumenAux) {
						if(componente.getIngredienteActivo().equals(item.getIngredienteActivo())) {
							existeComponente = true;
							if(item.getCategoria() != null && item.getCategoria().getId() != null) {
								Integer ultimaCategoria = 0;
								if(componente.getCategoria() != null && componente.getCategoria().getId() != null) {
									ultimaCategoria = Integer.valueOf(componente.getCategoria().getValor().toString());
								}
								Integer nuevaCategoria = Integer.valueOf(item.getCategoria().getValor().toString()); 
								
								if(ultimaCategoria == 0 || nuevaCategoria < ultimaCategoria) {
									listaComponentesResumen.remove(componente);
									listaComponentesResumen.add(item);
								}
							}
						}
					}
					
					if(!existeComponente) {
						listaComponentesResumen.add(item);
					}
				} else {
					listaComponentesResumen.add(item);
				}
			}
			
			guardado = true;
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void recuperarInfoEfectosAcuaticos(EfectoOrganismosAcuaticos efectoOrganismo) {
		if(efectoOrganismo.getValor() != null) {
			Integer valorCategoria = 0;
			if(efectoOrganismo.getValor() <= 1) {
				valorCategoria = 1;
			} else if(efectoOrganismo.getValor() > 1 && efectoOrganismo.getValor() <= 10) {
				valorCategoria = 2;
			} else if(efectoOrganismo.getValor() > 10) {
				valorCategoria = 3;
			}
			
			Boolean pictograma = valorCategoria.equals(1);
			String advertencia = (pictograma) ? EfectoOrganismosAcuaticos.ADVERTENCIA : EfectoOrganismosAcuaticos.SIN_ADVERTENCIA;
			String indicaciones = null;
			
			switch (valorCategoria) {
			case 1:
				indicaciones = EfectoOrganismosAcuaticos.INDICACIONES_1;
				break;
			case 2:
				indicaciones = EfectoOrganismosAcuaticos.INDICACIONES_2;
				break;
			case 3:
				indicaciones = EfectoOrganismosAcuaticos.INDICACIONES_3;
				break;
			default:
				break;
			}
			
			CatalogoGeneralCoa catalogoCategoria = catalogoCoaFacade.obtenerCatalogoPorCodigo("pqua.valor.categoria." + valorCategoria);
			
			efectoOrganismo.setCategoria(catalogoCategoria);
			efectoOrganismo.setPictograma(pictograma);
			efectoOrganismo.setPalabraAdvertencia(advertencia);
			efectoOrganismo.setIndicacionesPeligro(indicaciones);
		}
	}
	//fin efectos organismos acuaticos
	
	public void uploadRespaldoSolicitud(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoRespaldo = new DocumentoPqua();
		documentoRespaldo.setContenidoDocumento(contenidoDocumento);
		documentoRespaldo.setNombre(event.getFile().getFileName());
		documentoRespaldo.setMime("application/pdf");
		documentoRespaldo.setIdTipoDocumento(TipoDocumentoSistema.PQUA_DOCUMENTO_RESPALDO.getIdTipoDocumento());
	}
	
	public void uploadEtiqueta(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoEtiqueta = new DocumentoPqua();
		documentoEtiqueta.setContenidoDocumento(contenidoDocumento);
		documentoEtiqueta.setNombre(event.getFile().getFileName());
		documentoEtiqueta.setMime("application/pdf");
		documentoEtiqueta.setIdTipoDocumento(TipoDocumentoSistema.PQUA_DOCUMENTO_ETIQUETA.getIdTipoDocumento());
	}
	
	public void validateFinalizarActualizacion(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if (documentoRespaldo == null
				|| (documentoRespaldo.getContenidoDocumento() == null && documentoRespaldo.getId() == null)) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe adjuntar el documento de respaldo", null));
		}
		
		if (documentoEtiqueta == null
				|| (documentoEtiqueta.getContenidoDocumento() == null && documentoEtiqueta.getId() == null)) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe adjuntar el documento de la etiqueta y hoja de seguridad SGA", null));
		}
		
		if(aceptarCondiciones == null || !aceptarCondiciones) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Para finalizar debe aceptar la declaración de responsabilidad", null));
		}
		
		if (!errorMessages.isEmpty()) {
			guardado = false;
			throw new ValidatorException(errorMessages);
		}
	}
	
	public void guardarFinalizar(){
		try {
			if(documentoRespaldo != null && documentoRespaldo.getContenidoDocumento() != null) {
				List<DocumentoPqua> documentosRespaldo = documentosFacade.documentoPorTablaIdPorIdDoc(proyectoPlaguicidas.getId(), TipoDocumentoSistema.PQUA_DOCUMENTO_RESPALDO, "RespaldoProyectoPlaguicidas");
				if(documentosRespaldo != null && documentosRespaldo.size() > 0) {
					DocumentoPqua documentoA = documentosRespaldo.get(0);
					documentoA.setEstado(false);
					documentosFacade.guardar(documentoA);
				}
				
				documentoRespaldo.setIdTabla(proyectoPlaguicidas.getId());
				documentoRespaldo.setNombreTabla("RespaldoProyectoPlaguicidas");
				documentosFacade.guardarDocumentoAlfresco(proyectoPlaguicidas.getCodigoProyecto(), "ACTUALIZACION_ETIQUETADO_PLAGUICIDAS", 
						0L, documentoRespaldo, TipoDocumentoSistema.PQUA_DOCUMENTO_RESPALDO);
			}
			
			if(documentoEtiqueta != null && documentoEtiqueta.getContenidoDocumento() != null) {
				List<DocumentoPqua> documentosEtiqueta = documentosFacade.documentoPorTablaIdPorIdDoc(proyectoPlaguicidas.getId(), TipoDocumentoSistema.PQUA_DOCUMENTO_ETIQUETA, "RespaldoEtiquetaProyectoPlaguicidas");
				if(documentosEtiqueta != null && documentosEtiqueta.size() > 0) {
					DocumentoPqua documentoA = documentosEtiqueta.get(0);
					documentoA.setEstado(false);
					documentosFacade.guardar(documentoA);
				}
				
				documentoEtiqueta.setIdTabla(proyectoPlaguicidas.getId());
				documentoEtiqueta.setNombreTabla("RespaldoEtiquetaProyectoPlaguicidas");
				documentosFacade.guardarDocumentoAlfresco(proyectoPlaguicidas.getCodigoProyecto(), "ACTUALIZACION_ETIQUETADO_PLAGUICIDAS", 
						0L, documentoEtiqueta, TipoDocumentoSistema.PQUA_DOCUMENTO_ETIQUETA);
			}

			guardado = true;

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void enviar(){
		try{
			Map<String, Object> parametros = new HashMap<>();
			
			String rolTecnico = "role.pqua.pc.tecnico";
			
			List<Usuario> listaUsuariosTecnicos = areaFacade.getUsuariosPlantaCentralPorRol(rolTecnico);
	
			List<Usuario> listaTecnicosResponsables = asignarTareaFacade.obtenerCargaPorUsuariosV2(listaUsuariosTecnicos);
	
			if(listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
				LOG.error("No se encontro usuario " + rolTecnico + " en PC");
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			Usuario tecnicoResponsable = null;
			if(esSubsanacion) {
				// recuperar tecnico de bpm y validar si el usuario existe en el listado anterior
				String usrTecnico = (String) variables.get("tecnico");
				Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
				if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
					if (listaTecnicosResponsables != null && listaTecnicosResponsables.size() >= 0
							&& listaTecnicosResponsables.contains(usuarioTecnico)) {
						tecnicoResponsable = usuarioTecnico;
					}
				}
				
				if (tecnicoResponsable == null) { //si no existe se actualiza en el bpm 
					tecnicoResponsable = listaTecnicosResponsables.get(0);
					parametros.put("tecnico", tecnicoResponsable.getNombre());
				}
				
				parametros.put("cumpleSubsanacion", true);
			} else {
				tecnicoResponsable = listaTecnicosResponsables.get(0);
				parametros.put("tecnico", tecnicoResponsable.getNombre());
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			if(esSubsanacion) {
				Integer nroRevision = numeroRevisionOficio + 1;
				parametros = new HashMap<>();
				parametros.put("numeroRevision", nroRevision);
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			} else {
				proyectoPlaguicidas.setFechaEnvioRevision(new Date());
				proyectoPlaguicidasFacade.guardar(proyectoPlaguicidas);
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void cerrar(){
		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}
	
}
