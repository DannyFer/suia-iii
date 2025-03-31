package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.DocumentoResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.ObservacionesResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.ResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ObservacionesResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ResolucionAmbiental;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class EmisionLicenciaAmbientalVisualizarController {
	
	private static final Logger LOG = Logger.getLogger(EmisionLicenciaAmbientalVisualizarController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	private Map<String, Object> variables;
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;
    
	/*EJBs*/
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
    
    // FACADES GENERALES
    @EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
    @EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
    @EJB
	private ResolucionAmbientalFacade resolucionAmbientalFacade;
    @EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
    @EJB
	private DocumentoResolucionAmbientalFacade  documentoResolucionAmbientalFacade;
    @EJB
	private ObservacionesResolucionAmbientalFacade observacionesResolucionAmbientalFacade;
    
    /*List*/
    @Getter
    @Setter
    private List<InstitucionFinanciera> institucionesFinancieras;
    @Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancieras = new ArrayList<TransaccionFinanciera>();
    @Setter
	@Getter
	private List<ObservacionesResolucionAmbiental> listObservacionesResolucionAmbiental = new ArrayList<ObservacionesResolucionAmbiental>();
    
    
    /*Object*/
    @Setter
    @Getter
    private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
    @Setter
    @Getter
    private ResolucionAmbiental resolucionAmbiental = new ResolucionAmbiental();
    @Setter
    @Getter
    private InformacionProyectoEia informacionProyectoEia = new InformacionProyectoEia();
    @Setter
    @Getter
    private InstitucionFinanciera institucionFinanciera = new InstitucionFinanciera();
    @Setter
    @Getter
    private TransaccionFinanciera transaccionFinancieraSelected = new TransaccionFinanciera();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental facturaPermisoAmbiental = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental pagoEmisionPermiso = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental polizaCostoImplementacion = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental justificacionCostoMedidas = new DocumentoResolucionAmbiental();
    @Setter
    @Getter
    private DocumentoResolucionAmbiental cronogramaValorado = new DocumentoResolucionAmbiental();
    
    
	/*Variables*/
    @Setter
    @Getter
    private Integer idRegistroPreliminar, cantidadObservacionesNoCorregidas;
    @Getter
	@Setter
    private String tramite, tecnicoFinanciero;
    @Setter
    @Getter
    private Boolean detalleRevisionEmision, habilitaPagoIF = false;
    @Setter
    @Getter
    private String fechaInicioVigenciaPoliza, fechaFinVigenciaPoliza;
    
    
    
    
    /*CONSTANTES*/
    public static final Double COSTO_MINIMO_BAJO_IMPACTO = 500.00;
    public static final Double COSTO_MINIMO_ALTO_IMPACTO = 1000.00;
    
   
    
    @PostConstruct
	public void init() {
    	try {
    		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
    		tecnicoFinanciero = (String) variables.get("tecnicoFinanciero");
    		idRegistroPreliminar = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
    		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idRegistroPreliminar);
    		cargarDatos();
    		institucionesFinancieras = institucionFinancieraFacade.obtenerListaInstitucionesFinancierasPC();
    		
    		transaccionesFinancieras = transaccionFinancieraFacade
					.cargarTransaccionesRcoaPorProceso(proyectoLicenciaCoa.getId(), bandejaTareasBean.getTarea().getProcessInstanceId());
    		
    		ResolucionAmbiental dataAux = new ResolucionAmbiental();
    		dataAux = resolucionAmbientalFacade.getByIdRegistroPreliminar(idRegistroPreliminar);
    		if((dataAux != null) && (dataAux.getId() != null))
    		{
        		if((resolucionAmbiental.getPagoTotalInspeccion() != null) && (resolucionAmbiental.getPagoTotalInspeccion() > 0.0))
        		{
        			habilitaPagoIF = true;
        		}
        		else
        		{
        			habilitaPagoIF = false;
        		}       			
    		}

    		
    	} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos de Emisión.");
		}
	}

    // 
    public void cargarDatos() {
    	try {
    		informacionProyectoEia = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectoLicenciaCoa);
    		resolucionAmbiental = resolucionAmbientalFacade.getByIdRegistroPreliminar(idRegistroPreliminar);
    		SimpleDateFormat fechaFormat = new SimpleDateFormat("dd/MM/yyy");
    		fechaInicioVigenciaPoliza = (resolucionAmbiental.getFechaInicioVigenciaPoliza() != null) ? fechaFormat.format(resolucionAmbiental.getFechaInicioVigenciaPoliza()): null;
    		fechaFinVigenciaPoliza = (resolucionAmbiental.getFechaFinVigenciaPoliza() != null) ? fechaFormat.format(resolucionAmbiental.getFechaFinVigenciaPoliza()) : null;
    		cantidadObservacionesNoCorregidas = numeroObservacionesNoCorregidas();
    		detalleRevisionEmision = (cantidadObservacionesNoCorregidas > 0) ? false : true;
    		cargarDocumentos();
		} catch (CmisAlfrescoException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los documentos.");
		}
    }
    
    public void cargarComprobante() {
    	 
    }

    public void cargarDocumentos() throws CmisAlfrescoException{
    	// 1. Factura por permiso ambiental
    	facturaPermisoAmbiental = documentoResolucionAmbientalFacade.getDocumentoByResolucionAmbiental(resolucionAmbiental.getId(), TipoDocumentoSistema.EMISION_LICENCIA_FACTURA_PERMISO_AMBIENTAL);
		if (facturaPermisoAmbiental.getId() == null) {
			facturaPermisoAmbiental = new DocumentoResolucionAmbiental();
		}

		// 2. Protocolarización del pago por permiso ambiental 
		pagoEmisionPermiso = documentoResolucionAmbientalFacade.getDocumentoByResolucionAmbiental(resolucionAmbiental.getId(), TipoDocumentoSistema.EMISION_LICENCIA_PAGO_PERMISO_AMBIENTAL);
		if(pagoEmisionPermiso.getId() == null){
			pagoEmisionPermiso = new DocumentoResolucionAmbiental();
		}

		// 3. Póliza o garantía bancaria por el 100% del costo de implementación del PMA
		polizaCostoImplementacion = documentoResolucionAmbientalFacade.getDocumentoByResolucionAmbiental(resolucionAmbiental.getId(), TipoDocumentoSistema.EMISION_LICENCIA_POLIZA_PMA);
		if(polizaCostoImplementacion.getId() == null){
			polizaCostoImplementacion = new DocumentoResolucionAmbiental();
		}
		
		// 4. Justificación del costo dentro de las medidas incluidas del PMA
		justificacionCostoMedidas = documentoResolucionAmbientalFacade.getDocumentoByResolucionAmbiental(resolucionAmbiental.getId(), TipoDocumentoSistema.EMISION_LICENCIA_MEDIDAS_PMA);
		if(justificacionCostoMedidas.getId() == null){
			justificacionCostoMedidas = new DocumentoResolucionAmbiental();
		}

		// 5. Cronograma valorado del PMA
		cronogramaValorado = documentoResolucionAmbientalFacade.getDocumentoByResolucionAmbiental(resolucionAmbiental.getId(), TipoDocumentoSistema.EMISION_LICENCIA_CRONOGRAMA_PMA);
		if(cronogramaValorado.getId() == null){
			cronogramaValorado = new DocumentoResolucionAmbiental();
		}
	}
    
    public StreamedContent descargarDocumento(DocumentoResolucionAmbiental documento){
		try {
			if(documento!=null){
				if (documento.getExtension().contains(".pdf")){
					if(documento.getContenidoDocumento()==null)
						documento.setContenidoDocumento(documentoResolucionAmbientalFacade.descargar(documento.getIdAlfresco()));	
					return getStreamedContent(documento);
				}else{
					DefaultStreamedContent content = null;
					if(documento.getContenidoDocumento() == null){
						documento.setContenidoDocumento(documentoResolucionAmbientalFacade.descargar(documento.getIdAlfresco()));	
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
    
    public static StreamedContent getStreamedContent(DocumentoResolucionAmbiental documento){		
		try {
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),documento.getMime(),documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;	
	}

    private Integer numeroObservacionesNoCorregidas() {
		Integer result = 0;
		Integer idClase=0;
		String nombreClase="";
		try {
			idClase = resolucionAmbiental.getId();
			nombreClase="ResolucionAmbiental";
			listObservacionesResolucionAmbiental = observacionesResolucionAmbientalFacade.listarPorIdClaseNombreClaseNoCorregidas(idClase, nombreClase);
			result = listObservacionesResolucionAmbiental.size();
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Error extraer las observaciones");
			e.printStackTrace();
			return 0;
		}
		return result;
	}

    public void enviar() {
    	try {
    		Map<String, Object> params=new HashMap<>();
    		
    		params.put("existeObservacion", !detalleRevisionEmision);
    		
    		procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
    		
    		procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
    		
    		enviarNotificacion(detalleRevisionEmision);
    		
    		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
    		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
    		
    	} catch (Exception e) {
    		JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
    		e.printStackTrace();
    	}
	}
    
    public void validarExisteObservaciones() {
		try {
			cantidadObservacionesNoCorregidas = numeroObservacionesNoCorregidas();
    		detalleRevisionEmision = (cantidadObservacionesNoCorregidas > 0) ? false : true;
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
    
    public String getConceptoPago(Integer tipoPago) {
    	String tipoConceptoPago = "";
	
		switch (tipoPago) {
		case 1:
			tipoConceptoPago = "Valor Total por Licencia Ambiental";
			break;
		case 2:
			tipoConceptoPago = "Pago por Inventario Forestal";
			break;
		case 3:
			tipoConceptoPago = "Pago por RGD";
			break;
		default:
			break;
		}
		
		return tipoConceptoPago;
    }
    
    public void enviarNotificacion(Boolean aprobado) throws ServiceException {
		String nombreOperador = "";
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		Object[] parametrosCorreoNuevo = new Object[] {nombreOperador,  proyectoLicenciaCoa.getCodigoUnicoAmbiental(),  
				proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getAreaResponsable().getAreaName()};
		String tipoNotifiacion = (aprobado) ? "bodyNotificacionConfimaPolizaResolucionAmbiental" : "bodyNotificacionValoresObservadosResolucionAmbiental";
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(tipoNotifiacion);
		String notificacionNuevo = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);
		
		Email.sendEmail(usuarioOperador, mensajeNotificacion.getAsunto(), notificacionNuevo, tramite, loginBean.getUsuario());
    }
    
}
