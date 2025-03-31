package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.ExpedienteBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ExpedienteBPC;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.OficioResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ResolucionAmbiental;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.util.NotificacionInternaUtil;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class EmisionLicenciaAutoridadZonalController extends DocumentoReporteResolucionMemoController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5382084905489409831L;
	
	public EmisionLicenciaAutoridadZonalController() {	
		super();
	}

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;

	/* EJBs */
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
    private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	@EJB
	private GestionarProductosQuimicosProyectoAmbientalFacade gestionarProductosQuimicosProyectoAmbientalFacade;
	
	@EJB
	private ExpedienteBPCFacade expedienteBPCFacade;
	
	private Map<String, Object> variables;
	@Setter
    @Getter
    private DocumentoResolucionAmbiental documentoResolucionAmbiental = new DocumentoResolucionAmbiental();
	
	@Setter
    @Getter
    private DocumentoResolucionAmbiental documentoPronunciamientoAmbiental = new DocumentoResolucionAmbiental();
	
	@Setter
    @Getter
    private DocumentoResolucionAmbiental documentoFirmado = new DocumentoResolucionAmbiental();
	
	@Setter
    @Getter
	private RegistroSustanciaQuimica registroSustanciaQuimica;
	
	@Setter
	@Getter
	private String tramite, autoridadAmbiental, urlAlfresco, urlAlfrescoOficioRgd, urlAlfrescoPermisoRgd, urlAlfrescoPermisoRsq;
	
	@Setter
	@Getter
	private String nombreOficioFirmado;
	
	@Getter
	@Setter
	private Boolean token, firmaSoloToken, requiereFirmaRgd, requiereFirmaRsq;
	
	@Getter
	@Setter
	public Boolean resolucionFisica=false;
	
	@Getter
	@Setter
	private Boolean descargado1, descargado2, descargado3, descargado4, subido1, subido2, subido3, subido4;
	
	@Getter
	@Setter
	private Boolean visualizarRgd, visualizarRsq, bloquearFirmar;
	
	@Setter
    @Getter
    private Usuario operador = new Usuario();
	
	@Getter
	@Setter
	private ExpedienteBPC expedienteBPC = new ExpedienteBPC();
	
	@Getter
	@Setter
	private ResolucionAmbiental resolucionAmbientalA = new ResolucionAmbiental();
	
	/*CONSTANTES*/
    public static final Integer ID_TIPO_AREA_PLANTA_CENTRAL = 1;
	
	@PostConstruct
	public void init() {
		try {
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion"); 
			if(!firmaSoloToken){
				verificaToken();
			}else{
				token = true;
			}
	
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			autoridadAmbiental = (String) variables.get("autoridadAmbiental");
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			operador = usuarioFacade.buscarUsuario((String) variables.get("operador"));
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			expedienteBPC = expedienteBPCFacade.getByProyectoLicenciaCoa(proyectoLicenciaCoa.getId());
			ResolucionAmbiental resolucion = new ResolucionAmbiental();
			resolucion = resolucionAmbientalFacade.getByIdRegistroPreliminar(proyectoLicenciaCoa.getId());
			if ((resolucion != null) &&(resolucion.getId() != null))
			{
				resolucionAmbientalA = resolucion;
			}
			if((expedienteBPC != null) && (expedienteBPC.getId() != null) && (expedienteBPC.getTieneResolucionFisica() != null) && (expedienteBPC.getTieneResolucionFisica()))
			{
				resolucionFisica = true;
			}
			else
			{
				resolucionFisica = false;
			}
			if(proyectoLicenciaCoa.getGeneraDesechos() 
					&& (proyectoLicenciaCoa.getCodigoRgdAsociado() == null 
					|| proyectoLicenciaCoa.getCodigoRgdAsociado().isEmpty())) {
			
				requiereFirmaRgd = proyectoLicenciaCoa.getGeneraDesechos();
			}else{
				requiereFirmaRgd = false;
			}						
			
			requiereFirmaRsq = false;
			registroSustanciaQuimica = registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
			if (registroSustanciaQuimica != null && registroSustanciaQuimica.getId() != null)
				requiereFirmaRsq = true;
			
			inicializarResolucionPronunciamiento();
			visualizarResolucion(false);
			
			descargado1 = false;
			descargado2 = false;
			descargado3 = false;
			descargado4 = false;
			
			visualizarRgd = false;
			visualizarRsq = false;
			
			if(requiereFirmaRgd) {
				//generar documentos RGD
				visualizarRgd = true;
				generarDocumentosRgd();
			}
			if(requiereFirmaRsq) {
				//verificar si lo firma el mismo usuario, si es así generar docuemnto RSQ
				cargarDatosRsq();
				if(usuarioFirmaRsq != null && JsfUtil.getLoggedUser().getNombre().equals(usuarioFirmaRsq.getNombre())) {
					visualizarRsq = true;
					generarDocumentoRsq();
				}
			}
			
			urlAlfresco = "";
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Error visualizar resolucion / pronunciamiento");
			e.printStackTrace();
		}
	}
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
	
	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void guardarDocumentos() {
		try {
			String usuario = JsfUtil.getLoggedUser().getNombre();
			String usuarioFirma = JsfUtil.getLoggedUser().getNombre();
			
			buscarDocumentoPronunciamiento();
			
			Boolean guardarRes = false;
			if((expedienteBPC != null) && (expedienteBPC.getId() != null) && (expedienteBPC.getTieneResolucionFisica() != null) && (expedienteBPC.getTieneResolucionFisica()))
			{
				guardarRes = false;
			}
			else
			{
				if(documentoPronunciamientoAmbiental != null && documentoPronunciamientoAmbiental.getId() != null) {
					if(!documentoPronunciamientoAmbiental.getUsuarioCreacion().equals(usuario) 
							|| !JsfUtil.getSimpleDateFormat(documentoPronunciamientoAmbiental.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
						documentoPronunciamientoAmbiental.setEstado(false);
						documentoResolucionAmbientalFacade.guardar(documentoPronunciamientoAmbiental);
						
						guardarRes = true;
					}
				} else {
					guardarRes = true;
				}				
			}
			

			
			if(guardarRes) {
				visualizarResolucion(false);				
				documentoPronunciamientoAmbiental = new DocumentoResolucionAmbiental();
				documentoPronunciamientoAmbiental.setNombre("Resolución de Licencia Ambiental.pdf");
				documentoPronunciamientoAmbiental.setMime("application/pdf");
				documentoPronunciamientoAmbiental.setContenidoDocumento(documentoResolucion.getArchivoInforme());
				documentoPronunciamientoAmbiental.setExtension(".pdf");
				documentoPronunciamientoAmbiental.setNombreTabla(OficioResolucionAmbiental.class.getSimpleName());
				documentoPronunciamientoAmbiental.setResolucionAmbiental(resolucionAmbiental);
				documentoPronunciamientoAmbiental.setIdTabla(resolucionAmbiental.getId());
				documentoPronunciamientoAmbiental.setDescripcionTabla("Resolución de Licencia Ambiental");
				
				documentoPronunciamientoAmbiental = documentoResolucionAmbientalFacade.
						guardarDocumentoAlfrescoResolucionAmbiental(resolucionAmbiental.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
								"EMISION_LICENCIA", 
								0L, documentoPronunciamientoAmbiental, TipoDocumentoSistema.RCOA_LA_RESOLUCION);
			}
			
			if(documentoPronunciamientoAmbiental != null && documentoPronunciamientoAmbiental.getId() != null) {
				String documentOffice = documentoResolucionAmbientalFacade.direccionDescarga(documentoPronunciamientoAmbiental);
				urlAlfresco = DigitalSign.sign(documentOffice, usuarioFirma);
			}
			
			if(visualizarRgd){
				documentoOficioRgd = null;
				documentoPermisoRgd = null;
				
				List<DocumentosRgdRcoa> documentosOficiosList = documentosRgdRcoaFacade.descargarDocumentoRgd(registroRcoa.getId(),RegistroGeneradorDesechosProyectosRcoa.class.getSimpleName(),TipoDocumentoSistema.RGD_FINAL_OFICIO_PRONUNCIAMIENTO);
				if (documentosOficiosList != null && !documentosOficiosList.isEmpty()) {
					documentoOficioRgd = documentosOficiosList.get(0);
					if(!documentoOficioRgd.getUsuarioCreacion().equals(usuario) 
							|| !JsfUtil.getSimpleDateFormat(documentoOficioRgd.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
						documentoOficioRgd.setEstado(false);
						documentosRgdRcoaFacade.save(documentoOficioRgd, JsfUtil.getLoggedUser());
						
						documentoOficioRgd = guardarDocumentoOficioRgd();
					}
				} else {
					documentoOficioRgd = guardarDocumentoOficioRgd();
				}
				List<DocumentosRgdRcoa> documentoGeneradorList = documentosRgdRcoaFacade.descargarDocumentoRgd(registroRcoa.getId(),RegistroGeneradorDesechosProyectosRcoa.class.getSimpleName(),TipoDocumentoSistema.RGD_FINAL_REGISTRO_GENERADOR_DESECHOS);
				if (documentoGeneradorList != null && !documentoGeneradorList.isEmpty()) {
					documentoPermisoRgd = documentoGeneradorList.get(0);
					if(!documentoPermisoRgd.getUsuarioCreacion().equals(usuario) 
							|| !JsfUtil.getSimpleDateFormat(documentoPermisoRgd.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
						documentoPermisoRgd.setEstado(false);
						documentosRgdRcoaFacade.save(documentoPermisoRgd, JsfUtil.getLoggedUser());
						
						documentoPermisoRgd = guardarDocumentoRegistroRgd();
					}
				} else {
					documentoPermisoRgd = guardarDocumentoRegistroRgd();
				}
				
				if(documentoOficioRgd != null && documentoOficioRgd.getId() != null) {
					String documentOffice = documentosRgdRcoaFacade.direccionDescarga(documentoOficioRgd);
					urlAlfrescoOficioRgd = DigitalSign.sign(documentOffice, usuarioFirma);
				}
				
				if(documentoPermisoRgd != null && documentoPermisoRgd.getId() != null) {
					String documentOffice = documentosRgdRcoaFacade.direccionDescarga(documentoPermisoRgd);
					urlAlfrescoPermisoRgd = DigitalSign.sign(documentOffice, usuarioFirma);
				}
			}
			
			if(visualizarRsq) {
				List<DocumentosSustanciasQuimicasRcoa> documentoList = documentoSustanciasQuimicasFacade.documentoXIdTablaIdTipoDoc(registroSustanciaQuimica.getId(), RegistroSustanciaQuimica.class.getSimpleName(), TipoDocumentoSistema.RCOA_RSQ_FINAL_REGISTRO_SUSANCIAS_QUIMICAS);
				if(documentoList != null && !documentoList.isEmpty()){
					documentoPermisoRsq = documentoList.get(0);
					if(!documentoPermisoRsq.getUsuarioCreacion().equals(usuario) 
							|| !JsfUtil.getSimpleDateFormat(documentoPermisoRsq.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
						documentoPermisoRsq.setEstado(false);
						documentoSustanciasQuimicasFacade.save(documentoPermisoRsq, JsfUtil.getLoggedUser());
						
						documentoPermisoRsq = guardarDocumentoRsq();
					}
				} else {
					documentoPermisoRsq = guardarDocumentoRsq();
				}
				
				if(documentoPermisoRsq != null && documentoPermisoRsq.getId() != null) {
					String documentOffice = documentoSustanciasQuimicasFacade.direccionDescarga(documentoPermisoRsq);
					urlAlfrescoPermisoRsq = DigitalSign.sign(documentOffice, usuarioFirma);
				}
			}
		} catch (Exception e) {							
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
		
		RequestContext.getCurrentInstance().update("formDialog:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public void buscarDocumentoPronunciamiento() {
    	try {
    		List<DocumentoResolucionAmbiental> listaDocumentos = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(
    				resolucionAmbiental.getId(), 
    				TipoDocumentoSistema.RCOA_LA_RESOLUCION);
    		if(!listaDocumentos.isEmpty()) {    			
    			documentoPronunciamientoAmbiental = listaDocumentos.get(0);    			
    		} 		
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public void subirDocumento() {
		subido1 = true;
	}
	
	public void uploadListener(FileUploadEvent event) {
		Integer tipoDocumento = Integer.parseInt(event.getComponent().getAttributes().get("tipoDocumento").toString());
		String nombre = event.getFile().getFileName();
		
		switch (tipoDocumento) {
		case 1:
			if (descargado1) {
				documentoFirmado = new DocumentoResolucionAmbiental();

				byte[] contenidoDocumento = event.getFile().getContents();
				documentoFirmado.setContenidoDocumento(contenidoDocumento);
				documentoFirmado.setNombre(nombre);
				documentoFirmado.setExtension(".pdf");
				documentoFirmado.setMime("application/pdf");
				documentoFirmado.setResolucionAmbiental(resolucionAmbiental);
				documentoFirmado.setIdTabla(resolucionAmbiental.getId());
				documentoFirmado.setNombreTabla(OficioResolucionAmbiental.class.getSimpleName());
				documentoFirmado.setTipoDocumento(documentoPronunciamientoAmbiental.getTipoDocumento());
				documentoFirmado.setIdProceso(JsfUtil.getCurrentProcessInstanceId());
				documentoFirmado.setDescripcionTabla("Resolucion Registro Ambiental");
				subido1 = true;
			} else {
				JsfUtil.addMessageError("No ha descargado el documento Resolución de Licencia Ambiental");
			}
			break;
		case 2:
			if (descargado2) {
				TipoDocumento tipoDoc = new TipoDocumento();
		        tipoDoc.setId(TipoDocumentoSistema.RGD_FINAL_OFICIO_PRONUNCIAMIENTO.getIdTipoDocumento());
		        
				documentoOficioRgdFirmado = new DocumentosRgdRcoa();
		        documentoOficioRgdFirmado.setNombre(nombre);
		        documentoOficioRgdFirmado.setExtesion(".pdf");
		        documentoOficioRgdFirmado.setTipoContenido("application/pdf");
		        documentoOficioRgdFirmado.setMime("application/pdf");
		        documentoOficioRgdFirmado.setContenidoDocumento(archivoReporteOficioRgd);
		        documentoOficioRgdFirmado.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
		        documentoOficioRgdFirmado.setTipoDocumento(tipoDoc);
		        documentoOficioRgdFirmado.setIdTable(registroRcoa.getId());
		        documentoOficioRgdFirmado.setRegistroGeneradorDesechosRcoa(registroRcoa);
		        subido2 = true;
				break;
			} else {
				JsfUtil.addMessageError("No ha descargado el documento Oficio de aprobación");
			}
		case 3:
			if (descargado3) {
				TipoDocumento tipoDoc3 = new TipoDocumento();
		        tipoDoc3.setId(TipoDocumentoSistema.RGD_FINAL_REGISTRO_GENERADOR_DESECHOS.getIdTipoDocumento());
		        
				documentoPermisoRgdFirmado = new DocumentosRgdRcoa();
		        documentoPermisoRgdFirmado.setNombre(nombre);
		        documentoPermisoRgdFirmado.setExtesion(".pdf");
		        documentoPermisoRgdFirmado.setTipoContenido("application/pdf");
		        documentoPermisoRgdFirmado.setMime("application/pdf");
		        documentoPermisoRgdFirmado.setContenidoDocumento(archivoReporteRgd);
		        documentoPermisoRgdFirmado.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
		        documentoPermisoRgdFirmado.setTipoDocumento(tipoDoc3);
		        documentoPermisoRgdFirmado.setIdTable(registroRcoa.getId());
		        documentoPermisoRgdFirmado.setRegistroGeneradorDesechosRcoa(registroRcoa);
		        subido3 = true;
			} else {
				JsfUtil.addMessageError("No ha descargado el documento Registro de Generador");
			}
			
			break;
		case 4:
			if (descargado4 ) {
				TipoDocumento tipoDoc4 = new TipoDocumento();
		        tipoDoc4.setId(TipoDocumentoSistema.RCOA_RSQ_FINAL_REGISTRO_SUSANCIAS_QUIMICAS.getIdTipoDocumento());
		        
				documentoPermisoRsqFirmado = new DocumentosSustanciasQuimicasRcoa();
				documentoPermisoRsqFirmado.setNombre(nombre);
				documentoPermisoRsqFirmado.setExtesion(".pdf");
				documentoPermisoRsqFirmado.setMime("application/pdf");					
				documentoPermisoRsqFirmado.setContenidoDocumento(archivoReporteRsq);
				documentoPermisoRsqFirmado.setNombreTabla(RegistroSustanciaQuimica.class.getSimpleName());
				documentoPermisoRsqFirmado.setTipoDocumento(tipoDoc4);
				documentoPermisoRsqFirmado.setIdTable(registroSustanciaQuimica.getId());
				subido4 = true;
			} else {
				JsfUtil.addMessageError("No ha descargado el documento Registro de Sustancias Químicas");
			}
			
			break;
		default:
			break;
		}
	}
	 
	public void finalizar() {
		try {
			Boolean documentosFirmados = true;
			Boolean finTramite = false;
			
			if(token) {
				if((expedienteBPC != null) && (expedienteBPC.getId() != null) && (expedienteBPC.getTieneResolucionFisica() != null) && (expedienteBPC.getTieneResolucionFisica()))
				{
					documentosFirmados = true;
					subido1 = true;	
				}
				else
				{
					if (token && !documentosFacade.verificarFirmaVersion(documentoPronunciamientoAmbiental.getIdAlfresco())) {
						JsfUtil.addMessageError("El documento Resolución de Licencia Ambiental no está firmado electrónicamente.");
						documentosFirmados = false;
					}					
				}

				
				if (visualizarRgd && !documentosFacade.verificarFirmaVersion(documentoOficioRgd.getIdAlfresco())) {
					JsfUtil.addMessageError("El documento Oficio RGD no está firmado electrónicamente.");
					documentosFirmados = false;
				}
				
				if (visualizarRgd && !documentosFacade.verificarFirmaVersion(documentoPermisoRgd.getIdAlfresco())) {
					JsfUtil.addMessageError("El documento Registro de Generador no está firmado electrónicamente.");
					documentosFirmados = false;
				}
				
				if (visualizarRsq && !documentosFacade.verificarFirmaVersion(documentoPermisoRsq.getIdAlfresco())) {
					JsfUtil.addMessageError("El documento Registro de Sustancias Químicas no está firmado electrónicamente.");
					documentosFirmados = false;
				}
			} else {
				if((expedienteBPC != null) && (expedienteBPC.getId() != null) && (expedienteBPC.getTieneResolucionFisica() != null) && (expedienteBPC.getTieneResolucionFisica()))
				{
					documentosFirmados = true;
					subido1 = true;				
				}
				else
				{
					if(!subido1){
						documentosFirmados = false;
						JsfUtil.addMessageError("Debe adjuntar los documentos firmados.");
						return;
					}					
				}

				
				if(visualizarRgd && (!subido2 || !subido3)){
					documentosFirmados = false;
					JsfUtil.addMessageError("Debe adjuntar los documentos firmados.");
					return;
				}
				
				if(visualizarRsq && !subido4){
					documentosFirmados = false;
					JsfUtil.addMessageError("Debe adjuntar los documentos firmados.");
					return;
				}
				
				if((expedienteBPC != null) && (expedienteBPC.getId() != null) && (expedienteBPC.getTieneResolucionFisica() != null) && (expedienteBPC.getTieneResolucionFisica()))
				{
					subido1 = true;
				}
				else
				{
					if(subido1) {
						documentoPronunciamientoAmbiental = documentoResolucionAmbientalFacade.
								guardarDocumentoAlfrescoResolucionAmbiental(resolucionAmbiental.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
										"EMISION_LICENCIA", 
										0L, documentoFirmado, TipoDocumentoSistema.RCOA_LA_RESOLUCION);
					}					
				}
				
				if(visualizarRgd && subido2) {
					documentoOficioRgd = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
			                registroRcoa.getCodigo(), "GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documentoOficioRgdFirmado, TipoDocumentoSistema.RGD_FINAL_OFICIO_PRONUNCIAMIENTO);
				} 
				
				if(visualizarRgd && subido3) {
					documentoPermisoRgd = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
			                registroRcoa.getCodigo(), "GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documentoPermisoRgdFirmado, TipoDocumentoSistema.RGD_FINAL_REGISTRO_GENERADOR_DESECHOS);
				} 
				
				if(visualizarRsq && subido4) {
					documentoPermisoRsq = documentoSustanciasQuimicasFacade.guardarDocumentoAlfrescoImportacion(
							registroSustanciaQuimica.getNumeroAplicacion(),"REGISTRO SUSTANCIAS QUIMICAS", 0L, documentoPermisoRsqFirmado, TipoDocumentoSistema.RCOA_RSQ_FINAL_REGISTRO_SUSANCIAS_QUIMICAS);
				} 
			}
			
			if(documentosFirmados) {
				Map<String, Object> params = new HashMap<>();
				
				if(requiereFirmaRgd) {
					requiereFirmaRgd = false; //se cambia la variable para saltar la tarea de firma de RGD xq el RGD se firma junto con la resolucion
				} 
				
				if(requiereFirmaRsq && visualizarRsq) {
					requiereFirmaRsq = false; //se cambia la variable para saltar la tarea de firma de RSQ
					finTramite = true;
				}
				
				params.put("requiereFirmaRgd", requiereFirmaRgd);
				params.put("requiereFirmaRsq", requiereFirmaRsq);
				
				if(!requiereFirmaRgd && !requiereFirmaRsq) {
					finTramite = true;
				}
				
				if(requiereFirmaRsq && !finTramite) {
					registroSustanciaQuimica = registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
					
					String tipo = "autoridad";
					Area area = registroSustanciaQuimica.getArea();
					List<Usuario> listaUsuarios = null;
					
					String roleKey = "role.rsq." + (area.getTipoArea().getId().intValue() == 1 ? "pc" : "cz") + "." + tipo;
					String rolDirector = Constantes.getRoleAreaName(roleKey);
					try {
						if(area.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
							listaUsuarios = usuarioFacade.buscarUsuarioPorRol(rolDirector);
						} else {
							listaUsuarios = usuarioFacade.buscarUsuariosPorRolYAreaEspecifica(rolDirector, area.getArea()); //si no es PC es zonal
						}
						
						if(listaUsuarios != null && !listaUsuarios.isEmpty()){
							usuarioFirmaRsq = listaUsuarios.get(0);
							params.put("autoridadAmbientalRsq", usuarioFirmaRsq.getNombre());
						} else {
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
							LOG.error("No se encontro usuario " + rolDirector + " en " + area.getAreaName());						
							return;
						}
					} catch (Exception e) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						System.out.println("Error al recuperar la zonal correspondiente.");					
						return;
					}
				}
				
                procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getProcessId(), null);
				if((expedienteBPC != null) && (expedienteBPC.getId() != null) && (expedienteBPC.getTieneResolucionFisica() != null) && (expedienteBPC.getTieneResolucionFisica()))
				{	
					//documentoResolucion.setEstadoAprobacion(true);
					//documentoResolucion.setFechaReporte(new Date());
					//oficioResolucionAmbientalFacade.guardar(documentoResolucion);				
					//documentoPronunciamientoAmbiental.setIdProceso(bandejaTareasBean.getProcessId());
					//documentoResolucionAmbientalFacade.guardar(documentoPronunciamientoAmbiental);					
					if(visualizarRgd) {
						documentoOficioRgd.setIdProceso((int)bandejaTareasBean.getProcessId());
						documentosRgdRcoaFacade.save(documentoOficioRgd, JsfUtil.getLoggedUser());						
						documentoPermisoRgd.setIdProceso((int)bandejaTareasBean.getProcessId());
						documentosRgdRcoaFacade.save(documentoPermisoRgd, JsfUtil.getLoggedUser());
						
					}
					
					if(finTramite) {
						proyectoLicenciaCoa.setProyectoFinalizado(true);
						proyectoLicenciaCoa.setProyectoFechaFinalizado(resolucionAmbientalA.getFechaResolucion());			
						proyectoLicenciaCoaFacade.guardar(proyectoLicenciaCoa);

						/*Inicio proceso RSQ*/		
						
						if ((proyectoLicenciaCoa.getSustanciasQuimicas()) && ((proyectoLicenciaCoa.getTieneDocumentoRsq() == null) || (proyectoLicenciaCoa.getTieneDocumentoRsq() == 0)))
						{
							if (((proyectoLicenciaCoa.getIniciaProcesoRsq() == null)) || (!proyectoLicenciaCoa.getIniciaProcesoRsq()))
							{
								Boolean iniciaProceso = false;
								Boolean rsq = false;
								rsq = validaSustanciasQuimicas(proyectoLicenciaCoa);
								if (rsq) {
									iniciaProceso = true;
								} else {
									iniciaProceso = false;
								}
								if (iniciaProceso)
								{
									Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
									parametros.put("usuario_operador", operador.getNombre());
									parametros.put("tramite",proyectoLicenciaCoa.getCodigoUnicoAmbiental());																	
									parametros.put("idProyecto", proyectoLicenciaCoa.getId());
									parametros.put("requiere_pago", false);	
									proyectoLicenciaCoa.setIniciaProcesoRsq(true);
									proyectoLicenciaCoaFacade.guardar(proyectoLicenciaCoa);					
									Long idProceso = procesoFacade.iniciarProceso(operador, Constantes.RCOA_PROCESO_SUSTANCIAS_QUIMICAS, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), parametros);
								}
								else
								{
									JsfUtil.addMessageInfo("No se puede iniciar el proceso de Resolución Ambiental RSQ ya que el proyecto registrado no cumple con las condiciones de Sustancias Químicas necesarias (Constar por lo menos una de las sustancias: Mercurio, Cianuro de potasio K(CN), Cianuro de sodio Na(CN)).");
								}								
							}
						}
						
						enviarNotificacion();
					}
									
					
					String tecnicoResponsable = (String) variables.get("tecnicoResponsable");
					NotificacionInternaUtil.enviarNotificacionLiencia(proyectoLicenciaCoa, resolucionAmbiental.getNumeroResolucion(), tecnicoResponsable);
					
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");					
				}
				else
				{
					documentoResolucion.setEstadoAprobacion(true);
					documentoResolucion.setFechaReporte(new Date());
					oficioResolucionAmbientalFacade.guardar(documentoResolucion);
					
					resolucionAmbiental.setNumeroResolucion(documentoResolucion.getCodigoReporte());
					resolucionAmbiental.setFechaResolucion(documentoResolucion.getFechaReporte());
					resolucionAmbientalFacade.guardar(resolucionAmbiental);
					
					documentoPronunciamientoAmbiental.setIdProceso(bandejaTareasBean.getProcessId());
					documentoResolucionAmbientalFacade.guardar(documentoPronunciamientoAmbiental);
					
					if(visualizarRgd) {
						documentoOficioRgd.setIdProceso((int)bandejaTareasBean.getProcessId());
						documentosRgdRcoaFacade.save(documentoOficioRgd, JsfUtil.getLoggedUser());
						
						documentoPermisoRgd.setIdProceso((int)bandejaTareasBean.getProcessId());
						documentosRgdRcoaFacade.save(documentoPermisoRgd, JsfUtil.getLoggedUser());
						
					}
					
					if(visualizarRsq){
						documentoPermisoRsq.setProcessinstanceid((int)bandejaTareasBean.getProcessId());
						documentoSustanciasQuimicasFacade.save(documentoPermisoRsq, JsfUtil.getLoggedUser());
					}
					
					if(finTramite) {
						proyectoLicenciaCoa.setProyectoFinalizado(true);
						proyectoLicenciaCoa.setProyectoFechaFinalizado(new Date());			
						proyectoLicenciaCoaFacade.guardar(proyectoLicenciaCoa);
						
						/*Inicio proceso RSQ*/				 
						List<GestionarProductosQuimicosProyectoAmbiental> listaControlSustancia = gestionarProductosQuimicosProyectoAmbientalFacade.listaSustanciasQuimicasControl(proyectoLicenciaCoa);
						Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
						if(listaControlSustancia.size()>0 && proyectoLicenciaCoa.getTieneDocumentoRsq()==0) {					
							//parametros que se setean solo al inicio del proyecto bpm
							parametros.put("usuario_operador", operador.getNombre());
							parametros.put("tramite",proyectoLicenciaCoa.getCodigoUnicoAmbiental());																	
							parametros.put("idProyecto", proyectoLicenciaCoa.getId());
							parametros.put("requiere_pago", false);	
							proyectoLicenciaCoa.setIniciaProcesoRsq(true);
							proyectoLicenciaCoaFacade.guardar(proyectoLicenciaCoa);					
							Long idProceso = procesoFacade.iniciarProceso(operador, Constantes.RCOA_PROCESO_SUSTANCIAS_QUIMICAS, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), parametros);
						}	
						/*fin proceso rsq*/ 
						
						enviarNotificacion();
					}
					
					String tecnicoResponsable = (String) variables.get("tecnicoResponsable");
					NotificacionInternaUtil.enviarNotificacionLiencia(proyectoLicenciaCoa, resolucionAmbiental.getNumeroResolucion(), tecnicoResponsable);
					
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");					
				}

			}
		} catch (Exception e) { 
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}
	
	private boolean validaSustanciasQuimicas(ProyectoLicenciaCoa proyectoLicenciaCoaAux) {
		Boolean existeQuimico = false;
		try {
			String mercurio = "Mercurio";
			String potasio = "Cianuro de potasio K(CN)";
			String sodio = "Cianuro de sodio Na(CN)";
			List<GestionarProductosQuimicosProyectoAmbiental> listaQuimicos = new ArrayList<GestionarProductosQuimicosProyectoAmbiental>();
			listaQuimicos = gestionarProductosQuimicosProyectoAmbientalFacade
					.listadoSustanciasQuimicasProyectoRLA(proyectoLicenciaCoaAux.getId());
			if ((listaQuimicos != null) && (listaQuimicos.size() > 0)) {
				for (GestionarProductosQuimicosProyectoAmbiental producto : listaQuimicos) {
					if (producto.getSustanciaquimica().getDescripcion().equals(mercurio)) {
						existeQuimico = true;
						break;
					}
					if (producto.getSustanciaquimica().getDescripcion().equals(potasio)) {
						existeQuimico = true;
						break;
					}
					if (producto.getSustanciaquimica().getDescripcion().equals(sodio)) {
						existeQuimico = true;
						break;
					}
				}
			}
			return existeQuimico;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void enviarNotificacion() throws Exception {
		List<String> listaArchivos = new ArrayList<>();	
		if((expedienteBPC != null) && (expedienteBPC.getId() != null) && (expedienteBPC.getTieneResolucionFisica() != null) && (expedienteBPC.getTieneResolucionFisica()))
		{								
			try
			{
				ResolucionAmbiental resolucionAAA = new ResolucionAmbiental();
				resolucionAAA = resolucionAmbientalFacade.getByIdRegistroFlujoByPass(expedienteBPC.getProyectoLicenciaCoa().getId(), 1);
				if ((resolucionAAA != null) && (resolucionAAA.getId() != null))
				{
					List<DocumentoResolucionAmbiental> listado = new ArrayList<DocumentoResolucionAmbiental>();
					listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.RCOA_LA_RESOLUCION);
					if ((listado != null) && (listado.size() > 0))
					{
						DocumentoResolucionAmbiental documento = new DocumentoResolucionAmbiental();
						documento = listado.get(0);
							if(documento.getIdAlfresco() != null)
							{
								String nombreArchivo = documento.getNombre();
								File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreArchivo);
								try {
									FileOutputStream file;
									file = new FileOutputStream(fileArchivo);
									file.write(documentoResolucionAmbientalFacade.descargar(documento.getIdAlfresco()));
									file.close();
									listaArchivos.add(nombreArchivo);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}	
					}							
					listado = new ArrayList<DocumentoResolucionAmbiental>();
					listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.EMISION_LICENCIA_FACTURA_PERMISO_AMBIENTAL);
					if ((listado != null) && (listado.size() > 0))
					{
						DocumentoResolucionAmbiental documento = new DocumentoResolucionAmbiental();
						documento = listado.get(0);
							if(documento.getIdAlfresco() != null)
							{
								String nombreArchivo = documento.getNombre();
								File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreArchivo);
								try {
									FileOutputStream file;
									file = new FileOutputStream(fileArchivo);
									file.write(documentoResolucionAmbientalFacade.descargar(documento.getIdAlfresco()));
									file.close();
									listaArchivos.add(nombreArchivo);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}	
					}
					
					listado = new ArrayList<DocumentoResolucionAmbiental>();
					listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.EMISION_LICENCIA_PAGO_PERMISO_AMBIENTAL);
					if ((listado != null) && (listado.size() > 0))
					{
						DocumentoResolucionAmbiental documento = new DocumentoResolucionAmbiental();
						documento = listado.get(0);
							if(documento.getIdAlfresco() != null)
							{
								String nombreArchivo = documento.getNombre();
								File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreArchivo);
								try {
									FileOutputStream file;
									file = new FileOutputStream(fileArchivo);
									file.write(documentoResolucionAmbientalFacade.descargar(documento.getIdAlfresco()));
									file.close();
									listaArchivos.add(nombreArchivo);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}	
					}
					
					listado = new ArrayList<DocumentoResolucionAmbiental>();
					listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.EMISION_LICENCIA_POLIZA_PMA);
					if ((listado != null) && (listado.size() > 0))
					{
						DocumentoResolucionAmbiental documento = new DocumentoResolucionAmbiental();
						documento = listado.get(0);
							if(documento.getIdAlfresco() != null)
							{
								String nombreArchivo = documento.getNombre();
								File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreArchivo);
								try {
									FileOutputStream file;
									file = new FileOutputStream(fileArchivo);
									file.write(documentoResolucionAmbientalFacade.descargar(documento.getIdAlfresco()));
									file.close();
									listaArchivos.add(nombreArchivo);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}	
					}
					
					listado = new ArrayList<DocumentoResolucionAmbiental>();
					listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.EMISION_LICENCIA_MEDIDAS_PMA);
					if ((listado != null) && (listado.size() > 0))
					{
						DocumentoResolucionAmbiental documento = new DocumentoResolucionAmbiental();
						documento = listado.get(0);
							if(documento.getIdAlfresco() != null)
							{
								String nombreArchivo = documento.getNombre();
								File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreArchivo);
								try {
									FileOutputStream file;
									file = new FileOutputStream(fileArchivo);
									file.write(documentoResolucionAmbientalFacade.descargar(documento.getIdAlfresco()));
									file.close();
									listaArchivos.add(nombreArchivo);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}	
					}
					
					listado = new ArrayList<DocumentoResolucionAmbiental>();
					listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.EMISION_LICENCIA_CRONOGRAMA_PMA);
					if ((listado != null) && (listado.size() > 0))
					{
						DocumentoResolucionAmbiental documento = new DocumentoResolucionAmbiental();
						documento = listado.get(0);
							if(documento.getIdAlfresco() != null)
							{
								String nombreArchivo = documento.getNombre();
								File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreArchivo);
								try {
									FileOutputStream file;
									file = new FileOutputStream(fileArchivo);
									file.write(documentoResolucionAmbientalFacade.descargar(documento.getIdAlfresco()));
									file.close();
									listaArchivos.add(nombreArchivo);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}	
					}
				}	
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else
		{
			List<DocumentoResolucionAmbiental> listado = new ArrayList<DocumentoResolucionAmbiental>();
			listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAmbiental.getId(), TipoDocumentoSistema.RCOA_LA_RESOLUCION);
			if ((listado != null) && (listado.size() > 0))
			{
				DocumentoResolucionAmbiental documento = new DocumentoResolucionAmbiental();
				documento = listado.get(0);
					if(documento.getIdAlfresco() != null)
					{
						String nombreArchivo = documento.getNombre();
						File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreArchivo);
						try {
							FileOutputStream file;
							file = new FileOutputStream(fileArchivo);
							file.write(documentoResolucionAmbientalFacade.descargar(documento.getIdAlfresco()));
							file.close();
							listaArchivos.add(nombreArchivo);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}	
			}			
		}	
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		ProyectoLicenciaCoaUbicacion proyectoLicenciaCoaUbicacion = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa);
		UbicacionesGeografica ubicacion = proyectoLicenciaCoaUbicacion.getUbicacionesGeografica();
		
		String provincia = ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
		String canton = ubicacion.getUbicacionesGeografica().getNombre();
		String parroquia = ubicacion.getNombre();
		
		String nombreOperador = "";
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		Object[] parametrosCorreoNuevo = new Object[] {nombreOperador, documentoResolucion.getCodigoReporte(), 
				formatoFechaEmision.format(new Date()), proyectoLicenciaCoa.getNombreProyecto(), 
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(), provincia, canton, parroquia,
				proyectoLicenciaCoa.getAreaResponsable().getAreaName()};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionEmisionResolucionAmbiental");
		String notificacionNuevo = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);
		
		if ((listaArchivos != null) && (listaArchivos.size()> 0))
		{
			Email.sendEmailAdjuntos(usuarioOperador, mensajeNotificacion.getAsunto(), notificacionNuevo, listaArchivos,  tramite, loginBean.getUsuario());
		}
		else
		{
			Email.sendEmail(usuarioOperador, mensajeNotificacion.getAsunto(), notificacionNuevo, tramite, loginBean.getUsuario());
		}
		
		//Email.sendEmail(usuarioOperador, mensajeNotificacion.getAsunto(), notificacionNuevo, tramite, loginBean.getUsuario());
	}
	
	public StreamedContent descargarDocumentoFirma(Integer tipo) {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			String nombre = "";
			switch (tipo) {
			case 1:
				if (null != documentoPronunciamientoAmbiental && null != documentoPronunciamientoAmbiental.getIdAlfresco()) {
					documentoContent = documentoResolucionAmbientalFacade.descargar(documentoPronunciamientoAmbiental.getIdAlfresco());
				} else if (documentoPronunciamientoAmbiental.getContenidoDocumento() != null) {
					documentoContent = documentoPronunciamientoAmbiental.getContenidoDocumento();
				}
				nombre = documentoPronunciamientoAmbiental.getNombre();
				break;
			case 2:
				documentoContent = archivoReporteOficioRgd;
				nombre = nombreReporteOficioRgd;
				break;
			case 3:
				documentoContent = archivoReporteRgd;
				nombre = nombreReporteRgd;
				break;
			case 4:
				documentoContent = archivoReporteRsq;
				nombre = nombreReporteRsq;
				break;
			default:
				break;
			}
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent),  "application/pdf");
				content.setName(nombre);
			} else
				JsfUtil.addMessageError("Error al generar el archivo");
			
			switch (tipo) {
			case 1:
				descargado1 = true;
				break;
			case 2:
				descargado2 = true;
				break;
			case 3:
				descargado3 = true;
				break;
			case 4:
				descargado4 = true;
				break;
			default:
				break;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		
		return content;
	}

}
