package ec.gob.ambiente.rcoa.resolucionLicenciaAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.DocumentoResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.ResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ResolucionAmbiental;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.VinculoProyectoRgdSuiaFacade;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.VinculoProyectoRgdSuia;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class IngresoInformacionResolucionLAController {
	// Facades Ejb	
	@EJB
	private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;	
	@EJB
	private ResolucionAmbientalFacade resolucionAmbientalFacade;
	@EJB
	private ProcesoFacade procesoFacade;	
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private DocumentoResolucionAmbientalFacade documentoResolucionAmbientalFacade;
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;	
	@EJB
	private GestionarProductosQuimicosProyectoAmbientalFacade gestionarProductosQuimicosProyectoAmbientalFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
	private VinculoProyectoRgdSuiaFacade vinculoProyectoRgdSuiaFacade;
    @EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;	
	// List
	@Setter
	@Getter
	private List<DocumentoResolucionAmbiental> listadocumentoResolucionAmbiental;		
	@Setter
	@Getter
	private List<DocumentoResolucionAmbiental> listadocumentoFacturaEmisionAmbiental;	
	@Setter
	@Getter
	private List<DocumentoResolucionAmbiental> listadocumentoProtocolizacionPagoAmbiental;	
	@Setter
	@Getter
	private List<DocumentoResolucionAmbiental> listadocumentoPolizaGarantiaBancariaAmbiental;	
	@Setter
	@Getter
	private List<DocumentoResolucionAmbiental> listadocumentoJustificacionCostoMedidasAmbiental;	
	@Setter
	@Getter
	private List<DocumentoResolucionAmbiental> listadocumentoCronogramaPMAAmbiental;	
	@Setter
	@Getter
	private List<DocumentoResolucionAmbiental> listadocumentoFacturaTasaCS;	
	@Setter
	@Getter
	private List<DocumentoResolucionAmbiental> listadocumentoFacturaIF;	
	// Object
    @Setter
    @Getter
    private InformacionProyectoEia informacionProyectoEia = new InformacionProyectoEia();
	@Getter
	@Setter
	private Usuario usuarioOperador;
    @Setter
    @Getter
    private Organizacion organizacion;
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;	
	@Getter
	@Setter
	private ResolucionAmbiental resolucionAAA;
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
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;	
	// Variables
	@Getter
	@Setter
	private Map<String, Object> variables;	
	@Getter
	@Setter
	private String numeroResolucion;
	@Getter
	@Setter
	private Date fechaEmision;	
	@Getter
	@Setter
	private Double costoPMA=0.0;
	@Getter
	@Setter
	private Date fechaInicio;
	@Getter
	@Setter
	private Date fechaFin;
	@Getter
	@Setter
	private Boolean resolucionValida=false;		
	@Getter
	@Setter
	private Boolean ambienteProduccion=false;	
	@Getter
	@Setter
	private Boolean aaa=false;	
	@Getter
	@Setter
	private Boolean facturaemisionAAA=false;	
	@Getter
	@Setter
	private Boolean protocolizacionpagoAAA=false;	
	@Getter
	@Setter
	private Boolean polizaimplementacionPMA=false;	
	@Getter
	@Setter
	private Boolean justificacioncostosPMA=false;	
	@Getter
	@Setter
	private Boolean cronogramaPMA=false;	
	@Getter
	@Setter
	private String tramite,operador,idProyecto,autoridadAmbiental;	
	@Getter
	@Setter
	private String validar_proceso;	
	@Getter
	@Setter
	private Long idProcesoResolucion;
	@Getter
	@Setter
	private Date fechaInicioControl;	
	@Getter
	@Setter
	private Integer tipoPronunciamiento;
	@Getter
	@Setter
	private Boolean cierreFlujoRGD = false;
	@Getter
	@Setter
	private Boolean cierreFlujoRSQ = false;	
	@Getter
	@Setter
	private Boolean tieneFlujoRGD = false;		
	@Getter
	@Setter
	private Boolean tieneFlujoRSQ = false;		
	@Getter
	@Setter
	private Boolean verificaFirma = false;	
	@Getter
	@Setter
	private Boolean panel1=true;
	@Getter
	@Setter
	private Boolean panel2=true;	
	@Getter
	@Setter
	private Boolean panel3=true;	
	@Getter
	@Setter
	private Boolean panel4=true;	
	@Getter
	@Setter
	private Boolean panel5=false;	
	@Getter
	@Setter
	private Boolean panel6=true;	
	@Getter
	@Setter
	private Boolean panel7=true;	
	@Getter
	@Setter
	private Boolean panel8=true;		
	// Constantes
	@Getter
	@Setter
	public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	private static final String IGUAL_MAS_PARTICIPACION = "EMPRESAS MIXTAS CUANDO SU CAPITAL SUSCRITO PERTENEZCA POR "
			+ "LO MENOS A LAS DOS TERCERAS PARTES A ENTIDADES DE DERECHO PÚBLICO.";
		
	
	@PostConstruct
	private void init() throws ServiceException {
		try {
			iniciarlizarCampos();			
			if (bandejaTareasBean.getTarea() != null) {
				variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
				tramite = (String) variables.get("tramite");
				idProcesoResolucion = bandejaTareasBean.getTarea().getProcessInstanceId();
				proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
				idProyecto = (String) variables.get("idProyecto");
				operador = (String) variables.get("operador");
				usuarioOperador = usuarioFacade.buscarUsuarioCompleta(operador); 
				autoridadAmbiental = (String) variables.get("autoridadAmbiental");
				informacionProyectoEia = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
				if (informacionProyectoEia != null) {
					if((informacionProyectoEia.getValorPorInventarioForestal() == null) || (informacionProyectoEia.getValorPorInventarioForestal() <= 0.0))
					{
						panel8 = false;
					}
				}
				organizacion = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());				
				if (organizacion != null && organizacion.getId() != null) {
					if (organizacion.getTipoOrganizacion().getDescripcion().equals("EP") // 7 = Empresas Públicas
							|| organizacion.getTipoOrganizacion().getDescripcion().equals("GA")) // 5 = Gobiernos
																									// Autónomos
					{
						panel2 = false;
						panel3 = false;
						panel4 = false;
					} else if (organizacion.getTipoOrganizacion().getDescripcion().equals("EM")) { // 8 = Empresas //
																									// Mixtas
						if (organizacion.getParticipacionEstado() != null
								|| organizacion.getParticipacionEstado().equals(IGUAL_MAS_PARTICIPACION)) {
							panel2 = false;
							panel3 = false;
							panel4 = false;
						}
					}
				}			
			}			
			////valida si ya existe la informacion 
			cargaInformacion();
			if (bandejaTareasBean.getTarea() != null) {
				validar_proceso = bandejaTareasBean.getTarea().getTaskName();
			}			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void iniciarlizarCampos()
	{
		usuarioOperador = new Usuario();
		proyecto = new ProyectoLicenciaCoa();
		resolucionAAA = new ResolucionAmbiental();
		listadocumentoResolucionAmbiental = new ArrayList<DocumentoResolucionAmbiental>();			
		listadocumentoFacturaEmisionAmbiental = new ArrayList<DocumentoResolucionAmbiental>();	
		listadocumentoProtocolizacionPagoAmbiental = new ArrayList<DocumentoResolucionAmbiental>();
		listadocumentoPolizaGarantiaBancariaAmbiental = new ArrayList<DocumentoResolucionAmbiental>();
		listadocumentoJustificacionCostoMedidasAmbiental = new ArrayList<DocumentoResolucionAmbiental>();
		listadocumentoCronogramaPMAAmbiental = new ArrayList<DocumentoResolucionAmbiental>();		
		listadocumentoFacturaTasaCS = new ArrayList<DocumentoResolucionAmbiental>();
		listadocumentoFacturaIF = new ArrayList<DocumentoResolucionAmbiental>();	
		aaa=false;	
		facturaemisionAAA=false;	
		protocolizacionpagoAAA=false;	
		polizaimplementacionPMA=false;	
		justificacioncostosPMA=false;	
		cronogramaPMA=false;
		fechaInicioControl= new Date();	
	}
	
	
	private void cargaInformacion()
	{
		try
		{
			ResolucionAmbiental resolucion = new ResolucionAmbiental();
			resolucion = resolucionAmbientalFacade.getByIdRegistroFlujoByPass(proyecto.getId(), 1);
			if ((resolucion != null) && (resolucion.getId() != null))
			{
				resolucionValida = true;
				resolucionAAA = resolucion; 
				List<DocumentoResolucionAmbiental> listado = new ArrayList<DocumentoResolucionAmbiental>();
				listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.RCOA_LA_RESOLUCION);
				if ((listado != null) && (listado.size() > 0))
				{
					aaa=true;	
					for(DocumentoResolucionAmbiental dato : listado)
					{
						if(dato.getIdAlfresco() != null)
						{
							dato.setContenidoDocumento(documentoResolucionAmbientalFacade.descargar(dato.getIdAlfresco()));
							listadocumentoResolucionAmbiental.add(dato);
							break;
						}
					}	
				}							
				listado = new ArrayList<DocumentoResolucionAmbiental>();
				listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.EMISION_LICENCIA_FACTURA_PERMISO_AMBIENTAL);
				if ((listado != null) && (listado.size() > 0))
				{
					facturaemisionAAA=true;	
					for(DocumentoResolucionAmbiental dato : listado)
					{
						if(dato.getIdAlfresco() != null)
						{
							dato.setContenidoDocumento(documentoResolucionAmbientalFacade.descargar(dato.getIdAlfresco()));
							listadocumentoFacturaEmisionAmbiental.add(dato);
							break;
						}
					}	
				}				
				listado = new ArrayList<DocumentoResolucionAmbiental>();
				listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.EMISION_LICENCIA_PAGO_PERMISO_AMBIENTAL);
				if ((listado != null) && (listado.size() > 0))
				{
					protocolizacionpagoAAA=true;
					for(DocumentoResolucionAmbiental dato : listado)
					{
						if(dato.getIdAlfresco() != null)
						{
							dato.setContenidoDocumento(documentoResolucionAmbientalFacade.descargar(dato.getIdAlfresco()));
							listadocumentoProtocolizacionPagoAmbiental.add(dato);
							break;
						}
					}						
				}				
				listado = new ArrayList<DocumentoResolucionAmbiental>();
				listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.EMISION_LICENCIA_POLIZA_PMA);
				if ((listado != null) && (listado.size() > 0))
				{
					polizaimplementacionPMA=true;	
					for(DocumentoResolucionAmbiental dato : listado)
					{
						if(dato.getIdAlfresco() != null)
						{
							dato.setContenidoDocumento(documentoResolucionAmbientalFacade.descargar(dato.getIdAlfresco()));
							listadocumentoPolizaGarantiaBancariaAmbiental.add(dato);
							break;
						}
					}					
				}				
				listado = new ArrayList<DocumentoResolucionAmbiental>();
				listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.EMISION_LICENCIA_MEDIDAS_PMA);
				if ((listado != null) && (listado.size() > 0))
				{
					justificacioncostosPMA=true;	
					for(DocumentoResolucionAmbiental dato : listado)
					{
						if(dato.getIdAlfresco() != null)
						{
							dato.setContenidoDocumento(documentoResolucionAmbientalFacade.descargar(dato.getIdAlfresco()));
							listadocumentoJustificacionCostoMedidasAmbiental.add(dato);
							break;
						}
					}						
				}				
				listado = new ArrayList<DocumentoResolucionAmbiental>();
				listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.EMISION_LICENCIA_CRONOGRAMA_PMA);
				if ((listado != null) && (listado.size() > 0))
				{
					cronogramaPMA=true;
					for(DocumentoResolucionAmbiental dato : listado)
					{
						if(dato.getIdAlfresco() != null)
						{
							dato.setContenidoDocumento(documentoResolucionAmbientalFacade.descargar(dato.getIdAlfresco()));
							listadocumentoCronogramaPMAAmbiental.add(dato);
							break;
						}
					}					
				}
				listado = new ArrayList<DocumentoResolucionAmbiental>();
				listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.FACTURA_PAGO_TASA_CONTROL_SEGUIMIENTO);
				if ((listado != null) && (listado.size() > 0))
				{
					for(DocumentoResolucionAmbiental dato : listado)
					{
						if(dato.getIdAlfresco() != null)
						{
							dato.setContenidoDocumento(documentoResolucionAmbientalFacade.descargar(dato.getIdAlfresco()));
							listadocumentoFacturaTasaCS.add(dato);
							break;
						}
					}					
				}
				listado = new ArrayList<DocumentoResolucionAmbiental>();
				listado = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(resolucionAAA.getId(), TipoDocumentoSistema.FACTURA_PAGO_TASA_INVENTARIO_FORESTAL);
				if ((listado != null) && (listado.size() > 0))
				{
					for(DocumentoResolucionAmbiental dato : listado)
					{
						if(dato.getIdAlfresco() != null)
						{
							dato.setContenidoDocumento(documentoResolucionAmbientalFacade.descargar(dato.getIdAlfresco()));
							listadocumentoFacturaIF.add(dato);
							break;
						}
					}					
				}				
			}
			else
			{
				resolucionAmbientalFacade.guardar(resolucionAAA);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void verificaNoExisteNumeroResolucion()
	{
		try
		{
			resolucionValida = false;
			ResolucionAmbiental aaa = new ResolucionAmbiental();
			aaa = resolucionAmbientalFacade.getByCodigoResolucion(resolucionAAA.getNumeroResolucion());	
			if((aaa != null) && (aaa.getId() != null))
			{
				resolucionValida = false;
				resolucionAAA.setNumeroResolucion(null);
				JsfUtil.addMessageError("El proyecto: " + aaa.getProyectoLicenciaCoa().getCodigoUnicoAmbiental() + " con No. Resolución Administrativa Ambiental: " + aaa.getNumeroResolucion() + " se encuentra digitalizado, el sistema no permitirá que continúe el proceso");
			}
			else
			{
				resolucionValida = true;
			}
		}catch (Exception e) {
			
		}
	}

	public void uploadListenerAAA(FileUploadEvent event) {
		try {		
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			auxTipoDocumento = documentoResolucionAmbientalFacade.obtenerTipoDocumento(TipoDocumentoSistema.RCOA_LA_RESOLUCION.getIdTipoDocumento());
			String[] split = event.getFile().getContentType().split("/");
			String extension = "." + split[split.length - 1];
			DocumentoResolucionAmbiental documentoAAA = new DocumentoResolucionAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoAAA.setNombre(event.getFile().getFileName());
			documentoAAA.setExtension(extension);		
			documentoAAA.setMime(event.getFile().getContentType());
			documentoAAA.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			documentoAAA.setDescripcionTabla("Documento de Autorización Administrativa Ambiental");
			documentoAAA.setIdProceso(idProcesoResolucion);
			documentoAAA.setContenidoDocumento(contenidoDocumento);
			documentoAAA.setTipoDocumento(auxTipoDocumento);
			documentoAAA.setEstado(true);				
			DocumentoResolucionAmbiental documentoFinal = new DocumentoResolucionAmbiental();
			documentoAAA.setIdTabla(resolucionAAA.getId());
			documentoFinal = documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(tramite, "RESOLUCION LICENCIA AMBIENTAL", idProcesoResolucion, documentoAAA, TipoDocumentoSistema.RCOA_LA_RESOLUCION);
			if(documentoFinal.getIdAlfresco() != null)
			{
				String idAlfresco = documentoFinal.getIdAlfresco(); 
				if (!documentoResolucionAmbientalFacade.verificarFirmaAlfresco(idAlfresco)) {
					documentoFinal.setEstado(false);
					documentoResolucionAmbientalFacade.guardar(documentoFinal);
					JsfUtil.addMessageWarning("El documento de Resolución de Licencia Ambiental emitida en físico no está firmado electrónicamente.");
					verificaFirma = false;
				}
				else
				{
					verificaFirma = true;
					listadocumentoResolucionAmbiental = new ArrayList<DocumentoResolucionAmbiental>();
					listadocumentoResolucionAmbiental.add(documentoFinal);
					JsfUtil.addMessageInfo("Documento de Autorización Administrativa Ambiental Cargado Correctamente");	
				}
			}
			RequestContext.getCurrentInstance().execute("PF('adjuntarArchivoAAA').hide();");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al cargar el documento");
		}
	}
	
	public void uploadListenerFacturaAAA(FileUploadEvent event) {
		try {			
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			auxTipoDocumento = documentoResolucionAmbientalFacade.obtenerTipoDocumento(TipoDocumentoSistema.EMISION_LICENCIA_FACTURA_PERMISO_AMBIENTAL.getIdTipoDocumento());
			String[] split = event.getFile().getContentType().split("/");
			String extension = "." + split[split.length - 1];
			DocumentoResolucionAmbiental documentoAAA = new DocumentoResolucionAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoAAA.setNombre(event.getFile().getFileName());
			documentoAAA.setExtension(extension);		
			documentoAAA.setMime(event.getFile().getContentType());
			documentoAAA.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			documentoAAA.setDescripcionTabla("Documento Factura Emisión de Autorización Administrativa Ambiental");
			documentoAAA.setIdProceso(idProcesoResolucion);
			documentoAAA.setContenidoDocumento(contenidoDocumento);
			documentoAAA.setTipoDocumento(auxTipoDocumento);
			documentoAAA.setEstado(true);					
			listadocumentoFacturaEmisionAmbiental = new ArrayList<DocumentoResolucionAmbiental>();	
			listadocumentoFacturaEmisionAmbiental.add(documentoAAA);
			JsfUtil.addMessageInfo("Documento Factura Emisión de Autorización Administrativa Ambiental Cargado Correctamente");	
			RequestContext.getCurrentInstance().execute("PF('adjuntarFacturaAAA').hide();");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al cargar el documento");
		}
	}
	
	public void uploadListenerProtocolizacionAAA(FileUploadEvent event) {
		try {			
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			auxTipoDocumento = documentoResolucionAmbientalFacade.obtenerTipoDocumento(TipoDocumentoSistema.EMISION_LICENCIA_PAGO_PERMISO_AMBIENTAL.getIdTipoDocumento());
			String[] split = event.getFile().getContentType().split("/");
			String extension = "." + split[split.length - 1];
			DocumentoResolucionAmbiental documentoAAA = new DocumentoResolucionAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoAAA.setNombre(event.getFile().getFileName());
			documentoAAA.setExtension(extension);		
			documentoAAA.setMime(event.getFile().getContentType());
			documentoAAA.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			documentoAAA.setDescripcionTabla("Documento de Protocolización Pago Emisión de Autorización Administrativa Ambiental");
			documentoAAA.setIdProceso(idProcesoResolucion);
			documentoAAA.setContenidoDocumento(contenidoDocumento);
			documentoAAA.setTipoDocumento(auxTipoDocumento);
			documentoAAA.setEstado(true);				
			listadocumentoProtocolizacionPagoAmbiental = new ArrayList<DocumentoResolucionAmbiental>();
			listadocumentoProtocolizacionPagoAmbiental.add(documentoAAA);
			JsfUtil.addMessageInfo("Documento de Protocolización Pago Emisión de Autorización Administrativa Ambiental Cargado Correctamente");	
			RequestContext.getCurrentInstance().execute("PF('adjuntarProtocolizacionAAA').hide();");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al cargar el documento");
		}
	}
	
	public void uploadListenerPolizaAAA(FileUploadEvent event) {
		try {			
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			auxTipoDocumento = documentoResolucionAmbientalFacade.obtenerTipoDocumento(TipoDocumentoSistema.EMISION_LICENCIA_POLIZA_PMA.getIdTipoDocumento());
			String[] split = event.getFile().getContentType().split("/");
			String extension = "." + split[split.length - 1];
			DocumentoResolucionAmbiental documentoAAA = new DocumentoResolucionAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoAAA.setNombre(event.getFile().getFileName());
			documentoAAA.setExtension(extension);		
			documentoAAA.setMime(event.getFile().getContentType());
			documentoAAA.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			documentoAAA.setDescripcionTabla("Documento de Póliza o Garantía del Costo de Implementación del PMA");
			documentoAAA.setIdProceso(idProcesoResolucion);
			documentoAAA.setContenidoDocumento(contenidoDocumento);
			documentoAAA.setTipoDocumento(auxTipoDocumento);
			documentoAAA.setEstado(true);
			listadocumentoPolizaGarantiaBancariaAmbiental = new ArrayList<DocumentoResolucionAmbiental>();			
			listadocumentoPolizaGarantiaBancariaAmbiental.add(documentoAAA);
			JsfUtil.addMessageInfo("Documento de Póliza o Garantía del Costo de Implementación del PMA Cargado Correctamente");	
			RequestContext.getCurrentInstance().execute("PF('adjuntarPolizaAAA').hide();");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al cargar el documento");
		}
	}	
	
	public void uploadListenerJustificacionCostoPMA(FileUploadEvent event) {
		try {			
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			auxTipoDocumento = documentoResolucionAmbientalFacade.obtenerTipoDocumento(TipoDocumentoSistema.EMISION_LICENCIA_MEDIDAS_PMA.getIdTipoDocumento());
			String[] split = event.getFile().getContentType().split("/");
			String extension = "." + split[split.length - 1];
			DocumentoResolucionAmbiental documentoAAA = new DocumentoResolucionAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoAAA.setNombre(event.getFile().getFileName());
			documentoAAA.setExtension(extension);		
			documentoAAA.setMime(event.getFile().getContentType());
			documentoAAA.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			documentoAAA.setDescripcionTabla("Documento de Justificación del costo de las medidas incluidas en el PMA");
			documentoAAA.setIdProceso(idProcesoResolucion);
			documentoAAA.setContenidoDocumento(contenidoDocumento);
			documentoAAA.setTipoDocumento(auxTipoDocumento);
			documentoAAA.setEstado(true);					
			listadocumentoJustificacionCostoMedidasAmbiental = new ArrayList<DocumentoResolucionAmbiental>();
			listadocumentoJustificacionCostoMedidasAmbiental.add(documentoAAA);
			JsfUtil.addMessageInfo("Documento de Justificación del costo de las medidas incluidas en el PMA Cargado Correctamente");	
			RequestContext.getCurrentInstance().execute("PF('adjuntarJustificacionCostoPMA').hide();");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al cargar el documento");
		}
	}	
	
	public void uploadListenerCronogramaValoradoPMA(FileUploadEvent event) {
		try {			
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			auxTipoDocumento = documentoResolucionAmbientalFacade.obtenerTipoDocumento(TipoDocumentoSistema.EMISION_LICENCIA_CRONOGRAMA_PMA.getIdTipoDocumento());
			String[] split = event.getFile().getContentType().split("/");
			String extension = "." + split[split.length - 1];
			DocumentoResolucionAmbiental documentoAAA = new DocumentoResolucionAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoAAA.setNombre(event.getFile().getFileName());
			documentoAAA.setExtension(extension);		
			documentoAAA.setMime(event.getFile().getContentType());
			documentoAAA.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			documentoAAA.setDescripcionTabla("Documento del Cronograma valorado del PMA");
			documentoAAA.setIdProceso(idProcesoResolucion);
			documentoAAA.setContenidoDocumento(contenidoDocumento);
			documentoAAA.setTipoDocumento(auxTipoDocumento);
			documentoAAA.setEstado(true);	
			listadocumentoCronogramaPMAAmbiental = new ArrayList<DocumentoResolucionAmbiental>();
			listadocumentoCronogramaPMAAmbiental.add(documentoAAA);
			JsfUtil.addMessageInfo("Documento del Cronograma valorado del PMA Cargado Correctamente");	
			RequestContext.getCurrentInstance().execute("PF('adjuntarCronogramaPMA').hide();");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al cargar el documento");
		}
	}
	
	
	public void uploadListenerFacturaTasaCS(FileUploadEvent event) {
		try {			
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			auxTipoDocumento = documentoResolucionAmbientalFacade.obtenerTipoDocumento(TipoDocumentoSistema.FACTURA_PAGO_TASA_CONTROL_SEGUIMIENTO.getIdTipoDocumento());
			String[] split = event.getFile().getContentType().split("/");
			String extension = "." + split[split.length - 1];
			DocumentoResolucionAmbiental documentoAAA = new DocumentoResolucionAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoAAA.setNombre(event.getFile().getFileName());
			documentoAAA.setExtension(extension);		
			documentoAAA.setMime(event.getFile().getContentType());
			documentoAAA.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			documentoAAA.setDescripcionTabla("Factura por pago de tasas por control y seguimiento");
			documentoAAA.setIdProceso(idProcesoResolucion);
			documentoAAA.setContenidoDocumento(contenidoDocumento);
			documentoAAA.setTipoDocumento(auxTipoDocumento);
			documentoAAA.setEstado(true);
			listadocumentoFacturaTasaCS= new ArrayList<DocumentoResolucionAmbiental>();			
			listadocumentoFacturaTasaCS.add(documentoAAA);
			JsfUtil.addMessageInfo("Factura por pago de tasas por Control y Seguimiento cargado correctamente");	
			RequestContext.getCurrentInstance().execute("PF('adjuntarFacturaTasaCS').hide();");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al cargar el documento");
		}
	}	
	
	public void uploadListenerFacturaTasaIF(FileUploadEvent event) {
		try {			
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			auxTipoDocumento = documentoResolucionAmbientalFacade.obtenerTipoDocumento(TipoDocumentoSistema.FACTURA_PAGO_TASA_INVENTARIO_FORESTAL.getIdTipoDocumento());
			String[] split = event.getFile().getContentType().split("/");
			String extension = "." + split[split.length - 1];
			DocumentoResolucionAmbiental documentoAAA = new DocumentoResolucionAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoAAA.setNombre(event.getFile().getFileName());
			documentoAAA.setExtension(extension);		
			documentoAAA.setMime(event.getFile().getContentType());
			documentoAAA.setNombreTabla(ResolucionAmbiental.class.getSimpleName());
			documentoAAA.setDescripcionTabla("Factura por pago de tasas por Inventario Forestal");
			documentoAAA.setIdProceso(idProcesoResolucion);
			documentoAAA.setContenidoDocumento(contenidoDocumento);
			documentoAAA.setTipoDocumento(auxTipoDocumento);
			documentoAAA.setEstado(true);					
			listadocumentoFacturaIF = new ArrayList<DocumentoResolucionAmbiental>();
			listadocumentoFacturaIF.add(documentoAAA);
			JsfUtil.addMessageInfo("Factura por pago de tasas por Inventario Forestal cargado correctamente");	
			RequestContext.getCurrentInstance().execute("PF('adjuntarFacturaTasaIF').hide();");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al cargar el documento");
		}
	}	
	
    public StreamedContent descargarDocumentos(DocumentoResolucionAmbiental item) throws IOException {
		DefaultStreamedContent content = null;
		byte[] documentoContent = null;
		try {
			DocumentoResolucionAmbiental documentoDescarga = new DocumentoResolucionAmbiental();
			documentoDescarga = item;
			documentoContent = documentoDescarga.getContenidoDocumento();					
			if (documentoDescarga != null
					&& documentoDescarga.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDescarga.getNombre());
			} else
				JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
    
    public void eliminarDocumentoFicheros(DocumentoResolucionAmbiental item, Integer caso)
    {
    	DocumentoResolucionAmbiental documentoDescarga = new DocumentoResolucionAmbiental();
    	documentoDescarga = item;
    	switch(caso)
    	{
    		case 1:
    			if(listadocumentoResolucionAmbiental.contains(documentoDescarga))
    			{
    				listadocumentoResolucionAmbiental.remove(documentoDescarga);
    			}
    			if(listadocumentoResolucionAmbiental.size() == 0)
    			{
    				aaa = false;
    			}
    			if(listadocumentoResolucionAmbiental.size() > 0)
    			{
    				aaa = true;
    			} 			
    			break;
    		case 2:
    			if(listadocumentoFacturaEmisionAmbiental.contains(documentoDescarga))
    			{
    				listadocumentoFacturaEmisionAmbiental.remove(documentoDescarga);
    			}
    			if(listadocumentoFacturaEmisionAmbiental.size() == 0)
    			{
    				facturaemisionAAA = false;
    			}
    			if(listadocumentoFacturaEmisionAmbiental.size() > 0)
    			{
    				facturaemisionAAA = true;
    			} 	    			
    			break;
    		case 3:
    			if(listadocumentoProtocolizacionPagoAmbiental.contains(documentoDescarga))
    			{
    				listadocumentoProtocolizacionPagoAmbiental.remove(documentoDescarga);
    			}
    			if(listadocumentoProtocolizacionPagoAmbiental.size() == 0)
    			{
    				protocolizacionpagoAAA = false;
    			}
    			if(listadocumentoProtocolizacionPagoAmbiental.size() > 0)
    			{
    				protocolizacionpagoAAA = true;
    			} 	    			
    			break;
    		case 4:
    			if(listadocumentoPolizaGarantiaBancariaAmbiental.contains(documentoDescarga))
    			{
    				listadocumentoPolizaGarantiaBancariaAmbiental.remove(documentoDescarga);
    			}
    			if(listadocumentoPolizaGarantiaBancariaAmbiental.size() == 0)
    			{
    				polizaimplementacionPMA = false;
    			}
    			if(listadocumentoPolizaGarantiaBancariaAmbiental.size() > 0)
    			{
    				polizaimplementacionPMA = true;
    			} 	    			
    			break;
    		case 5:
    			if(listadocumentoJustificacionCostoMedidasAmbiental.contains(documentoDescarga))
    			{
    				listadocumentoJustificacionCostoMedidasAmbiental.remove(documentoDescarga);
    			}
    			if(listadocumentoJustificacionCostoMedidasAmbiental.size() == 0)
    			{
    				justificacioncostosPMA = false;
    			}
    			if(listadocumentoJustificacionCostoMedidasAmbiental.size() > 0)
    			{
    				justificacioncostosPMA = true;
    			} 	    			
    			break;
    		case 6:
    			if(listadocumentoCronogramaPMAAmbiental.contains(documentoDescarga))
    			{
    				listadocumentoCronogramaPMAAmbiental.remove(documentoDescarga);
    			}
    			if(listadocumentoCronogramaPMAAmbiental.size() == 0)
    			{
    				cronogramaPMA = false;
    			}
    			if(listadocumentoCronogramaPMAAmbiental.size() > 0)
    			{
    				cronogramaPMA = true;
    			} 	    			
    			break;        			
    		case 7:
    			if(listadocumentoFacturaTasaCS.contains(documentoDescarga))
    			{
    				listadocumentoFacturaTasaCS.remove(documentoDescarga);
    			}	    			
    			break;       			
    		case 8:
    			if(listadocumentoFacturaIF.contains(documentoDescarga))
    			{
    				listadocumentoFacturaIF.remove(documentoDescarga);
    			}	    			
    			break;        			
    	}
    }
    
    public void cancelar()
    {
		resolucionAAA = new ResolucionAmbiental();
		listadocumentoResolucionAmbiental = new ArrayList<DocumentoResolucionAmbiental>();			
		listadocumentoFacturaEmisionAmbiental = new ArrayList<DocumentoResolucionAmbiental>();	
		listadocumentoProtocolizacionPagoAmbiental = new ArrayList<DocumentoResolucionAmbiental>();
		listadocumentoPolizaGarantiaBancariaAmbiental = new ArrayList<DocumentoResolucionAmbiental>();
		listadocumentoJustificacionCostoMedidasAmbiental = new ArrayList<DocumentoResolucionAmbiental>();
		listadocumentoCronogramaPMAAmbiental = new ArrayList<DocumentoResolucionAmbiental>();	
		listadocumentoFacturaTasaCS = new ArrayList<DocumentoResolucionAmbiental>();
		listadocumentoFacturaIF = new ArrayList<DocumentoResolucionAmbiental>();	
		aaa=false;	
		facturaemisionAAA=false;	
		protocolizacionpagoAAA=false;	
		polizaimplementacionPMA=false;	
		justificacioncostosPMA=false;	
		cronogramaPMA=false;
		fechaInicioControl= new Date();	   	
		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
    }
    
	private String validarDatosFormulario()
	{
		String mensaje="";
		if ((proyecto != null) && (proyecto.getId() != null))
		{
			mensaje = "";
		}
		else
		{
			mensaje = "No se encuentra el proyecto.";
			return mensaje;
		}
		if (resolucionValida)
		{
			mensaje = "";
		} 
		else
		{
			mensaje = "Ingrese el Número de Resolución Administrativa Ambiental.";
			return mensaje;
		}
		if (resolucionAAA.getFechaResolucion() != null)
		{
			mensaje = "";
		}
		else
		{
			mensaje = "Ingrese la Fecha de emisión de la Autorización Administrativa Ambiental.";
			return mensaje;
		}
		if((listadocumentoResolucionAmbiental != null) && (listadocumentoResolucionAmbiental.size() > 0))
		{
			mensaje = "";
			aaa=true;
			resolucionAAA.setSubeAutorizacionAdministrativaAmbiental(true); 
		}
		else
		{
			aaa=false;
			mensaje = "Debe adjuntar el documento de Resolución Administrativa Ambiental firmado.";
			return mensaje;
		}
		
		if(panel2)
		{
			if((resolucionAAA.getNumeroPoliza() != null) && (resolucionAAA.getNumeroPoliza().length() > 0))
			{
				mensaje = "";			
			}
			else
			{
				mensaje = "Ingresar el Número de Póliza o Garantía Bancaria.";
				return mensaje;
			}	
			
			if((resolucionAAA.getCostoImplementacion() != null) && (resolucionAAA.getCostoImplementacion() > 0))
			{
				mensaje = "";
			}
			else
			{
				mensaje = "Ingresar el Costo de implementación del PMA.";
				return mensaje;
			}				
			if(resolucionAAA.getFechaInicioVigenciaPoliza() != null)
			{
				mensaje = "";
			}
			else
			{
				mensaje = "Ingresar la fecha inicial de la Vigencia de la Póliza o Garantía Bancaria.";
				return mensaje;
			}	
			if(resolucionAAA.getFechaFinVigenciaPoliza() != null)
			{
				mensaje = "";
			}
			else
			{
				mensaje = "Ingresar la fecha final de la Vigencia de la Póliza o Garantía Bancaria.";
				return mensaje;
			}			
		}
		if(panel3)
		{
			if((listadocumentoFacturaEmisionAmbiental != null) && (listadocumentoFacturaEmisionAmbiental.size() > 0))
			{
				mensaje = "";
				facturaemisionAAA = true;
				resolucionAAA.setSubeFacturaPermiso(true); 
			}
			else
			{
				facturaemisionAAA = false;
				mensaje = "Debe adjuntar la Factura por emisión de Autorización Administrativa Ambiental.";
				return mensaje;
			}	
		}	
		if(panel4)
		{
			if((listadocumentoProtocolizacionPagoAmbiental != null) && (listadocumentoProtocolizacionPagoAmbiental.size() > 0))
			{
				mensaje = "";
				protocolizacionpagoAAA = true;
				resolucionAAA.setSubeDocumentoPago(true);
			}
			else
			{
				protocolizacionpagoAAA = false;
				mensaje = "Debe adjuntar la Protocolización del pago por emisión de Autorización Administrativa Ambiental.";
				return mensaje;
			}	
		}
		if(panel2)
		{
			if((listadocumentoPolizaGarantiaBancariaAmbiental != null) && (listadocumentoPolizaGarantiaBancariaAmbiental.size() > 0))
			{
				mensaje = "";
				polizaimplementacionPMA=true;
				resolucionAAA.setSubeCostoImplementacion(true);
			}
			else
			{
				polizaimplementacionPMA=false;
				mensaje = "Debe adjuntar la Póliza o Garantía Bancaria por el 100% del costo de implementación del PMA.";
				return mensaje;
			}	
		}
		if (panel5)
		{
			if((listadocumentoJustificacionCostoMedidasAmbiental != null) && (listadocumentoJustificacionCostoMedidasAmbiental.size() > 0))
			{
				mensaje = "";
				justificacioncostosPMA=true;
				resolucionAAA.setSubeJustificacionCosto(true);
			}
			else
			{
				justificacioncostosPMA=false;
				mensaje = "Debe adjuntar la justificación del costo de las medidas incluidas dentro del PMA.";
				return mensaje;
			}	
		}		
		if((listadocumentoCronogramaPMAAmbiental != null) && (listadocumentoCronogramaPMAAmbiental.size() > 0))
		{
			mensaje = "";
			cronogramaPMA=true;
			resolucionAAA.setSubeCronogramaPMA(true);
		}
		else
		{
			cronogramaPMA=false;
			mensaje = "Debe adjuntar el cronograma valorado del PMA.";
			return mensaje;
		}	
		return mensaje;		
	}
	
	public void guardarResolucion()
	{
		String mensaje = "";
		try
		{
			mensaje = validarDatosFormulario();
			if ((mensaje != null) && (mensaje.length() > 0))
			{
				JsfUtil.addMessageError(mensaje);
			}
			else
			{
				resolucionAAA.setFlujo(1);
				resolucionAAA.setProyectoLicenciaCoa(proyecto);	
				resolucionAmbientalFacade.guardar(resolucionAAA);
				guardarDocumentosAlfresco();
					JsfUtil.addMessageInfo("Resolución generada de manera correcta.");
					if ((proyecto != null) && (proyecto.getGeneraDesechos()))
					{										
						validaRGD(proyecto);
					} 
					else if ((proyecto != null) && (proyecto.getSustanciasQuimicas()))
					{
						validaRSQPrincipal(proyecto);
					}				
					if ((proyecto != null) && (!proyecto.getGeneraDesechos()) && (!proyecto.getSustanciasQuimicas()))
					{										
						Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
						parametros.put("RGD", false);
						parametros.put("operador", operador);
						try {
							procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
							procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
							proyecto.setProyectoFinalizado(true);
							proyecto.setProyectoFechaFinalizado(resolucionAAA.getFechaResolucion());
							proyectoLicenciaCoaFacade.guardar(proyecto);
							JsfUtil.addMessageInfo("Proceso de Resolución finalizado correctamente");
						} catch (JbpmException e) {
							e.printStackTrace();
						}
					} 
					JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error en el registro la Resolución de la Licencia Ambiental. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	private void validaRGD(ProyectoLicenciaCoa proyecto)
	{
		try
		{
			Integer historial_vinculado = 0;
			String RGD="";
			Boolean generaResiduos = false;
			Boolean continua = false;
			GeneradorDesechosPeligrosos generador = new GeneradorDesechosPeligrosos();
			VinculoProyectoRgdSuia obj = new VinculoProyectoRgdSuia();
			obj = vinculoProyectoRgdSuiaFacade.getProyectoVinculadoRgd(proyecto.getId()); 
			//////////////////////////////RCOA
			RegistroGeneradorDesechosRcoa objRCOA = new RegistroGeneradorDesechosRcoa();
			List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
			lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId());
			if ((lista != null) && (lista.size() > 0))
			{
				Boolean finalizado1 = false;
				if ((lista.get(0).getIdPadreHistorial() != null) && (lista.get(0).getIdPadreHistorial() > 0))
				{
					historial_vinculado = lista.get(0).getIdPadreHistorial();
					List<ProyectoLicenciaCoa>  proyHistorial = new ArrayList<ProyectoLicenciaCoa>();
					proyHistorial = proyectoLicenciaCoaFacade.buscarProyectoHistorialRGD(historial_vinculado);
					if ((proyHistorial != null) && (proyHistorial.size() > 0))
					{
						finalizado1 = registroGeneradorDesechosRcoaFacade.RGDPagado(proyHistorial.get(0).getCodigoUnicoAmbiental());
					}
				}
				else
				{
					Boolean finalizadoSoloRGD = false;
					finalizadoSoloRGD = registroGeneradorDesechosRcoaFacade.RGDPagadoSinProyectos(lista.get(0).getRegistroGeneradorDesechosRcoa().getCodigo()); 
					if (finalizadoSoloRGD)
					{
						finalizado1 = true;
					}
				}
				objRCOA = lista.get(0).getRegistroGeneradorDesechosRcoa();	
				if (finalizado1)
				{
					continua = false;	
				}
				else
				{					
					continua = true;
					generaResiduos=true;
					RGD = objRCOA.getCodigo();
				}				
			}	
			if (continua)
			{				
				proyectosBean.setCodRGDs(RGD);
				proyectosBean.setNumAAA(resolucionAAA.getNumeroResolucion());
				proyectosBean.setFlujoRLA(resolucionAAA.getFlujo());
				if (generaResiduos)
				{		
					tieneFlujoRGD = true;
					Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
					parametros.put("tramiteRGD", RGD);
					parametros.put("RGD", true);
					parametros.put("operador", operador);
					try {
						procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
						procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
						JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
					} catch (JbpmException e) {
						e.printStackTrace();
					}					
				}					
			}
			else
			{
				validaRSQPrincipal(proyecto);				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}				
	}
	
	private void validaRSQ(ProyectoLicenciaCoa proyecto)
	{
		if ((proyecto.getSustanciasQuimicas()) && ((proyecto.getTieneDocumentoRsq() != null) && (proyecto.getTieneDocumentoRsq() == 1)))
		{
			JsfUtil.addMessageInfo("Proceso de Resolución finalizado correctamente");		
		} else if ((proyecto.getSustanciasQuimicas()) && ((proyecto.getTieneDocumentoRsq() == null) || (proyecto.getTieneDocumentoRsq() == 0)))
		{
			Boolean iniciaProceso = false;
			Boolean rsq = false;
			rsq = validaSustanciasQuimicas(proyecto);
			if (rsq) {
				iniciaProceso = true;
			} else {
				iniciaProceso = false;
			}
			if (iniciaProceso)
			{
					lanzarProcesoRSQ(proyecto);	
			}
			else
			{
				JsfUtil.addMessageInfo("No se puede iniciar el proceso de Resolución Ambiental RSQ ya que el proyecto registrado no cumple con las condiciones de Sustancias Químicas necesarias (Constar por lo menos una de las sustancias: Mercurio, Cianuro de potasio K(CN), Cianuro de sodio Na(CN)).");
			}
		}
	}
	
	
	private void validaRSQPrincipal(ProyectoLicenciaCoa proyecto)
	{
		Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
		parametros.put("RGD", false);
		parametros.put("operador", operador);
		try {
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			proyecto.setProyectoFinalizado(true);
			proyecto.setProyectoFechaFinalizado(resolucionAAA.getFechaResolucion());
			proyectoLicenciaCoaFacade.guardar(proyecto);
		} catch (JbpmException e) {
			e.printStackTrace();
		}
		if ((proyecto.getSustanciasQuimicas()) && ((proyecto.getTieneDocumentoRsq() != null) && (proyecto.getTieneDocumentoRsq() == 1)))
		{			
			JsfUtil.addMessageInfo("Proceso de Resolución finalizado correctamente");		
		} else if ((proyecto.getSustanciasQuimicas()) && ((proyecto.getTieneDocumentoRsq() == null) || (proyecto.getTieneDocumentoRsq() == 0)))
		{
			Boolean iniciaProceso = false;
			Boolean rsq = false;
			rsq = validaSustanciasQuimicas(proyecto);
			if (rsq) {
				iniciaProceso = true;
			} else {
				iniciaProceso = false;
			}
			if (iniciaProceso)
			{
					lanzarProcesoRSQ(proyecto);
			}
			else
			{
				JsfUtil.addMessageInfo("Proceso de Resolución finalizado correctamente");	
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void lanzarProcesoRSQ(ProyectoLicenciaCoa proyecto)
	{
		if ((proyecto.getIniciaProcesoRsq() != null) && (proyecto.getIniciaProcesoRsq()))
		{}
		else
		{
			/*Inicio proceso RSQ*/	
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
				parametros.put("usuario_operador", operador);
				parametros.put("tramite",proyecto.getCodigoUnicoAmbiental());																	
				parametros.put("idProyecto", proyecto.getId());
				parametros.put("requiere_pago", false);		
				proyecto.setIniciaProcesoRsq(true);
				proyectoLicenciaCoaFacade.guardar(proyecto);	
				try {
				Long idProceso = procesoFacade.iniciarProceso(loginBean.getUsuario(), Constantes.RCOA_PROCESO_SUSTANCIAS_QUIMICAS, proyecto.getCodigoUnicoAmbiental(), parametros);	
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				} catch (JbpmException e) {
					e.printStackTrace();
				}		
		}		
	}
	
	
	private Boolean guardarDocumentosAlfrescoFirmado()
	{
		Boolean verifica = false;
		try
		{
			for(DocumentoResolucionAmbiental documento : listadocumentoResolucionAmbiental)
			{
				DocumentoResolucionAmbiental documentoFinal = new DocumentoResolucionAmbiental();
				documento.setIdTabla(resolucionAAA.getId());
				documentoFinal = documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(tramite, "RESOLUCION LICENCIA AMBIENTAL", idProcesoResolucion, documento, TipoDocumentoSistema.RCOA_LA_RESOLUCION);
				if(documentoFinal.getIdAlfresco() != null)
				{
					String idAlfresco = documentoFinal.getIdAlfresco(); 
					if (!documentoResolucionAmbientalFacade.verificarFirmaAlfresco(idAlfresco)) {
						documentoFinal.setEstado(false);
						documentoResolucionAmbientalFacade.guardar(documento);
						JsfUtil.addMessageError("El documento de Resolución de Licencia Ambiental emitida en físico no está firmado electrónicamente.");
						verifica = false;
					}
					else
					{
						verifica = true;
					}
				}
			}
			return verifica;
		}
		catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al subir el archivo al repositorio Alfresco. Por favor comunicarse con mesa de ayuda.");
			return false;
		}		
	}
	
	private Boolean guardarDocumentosAlfresco()
	{
		try
		{
			for(DocumentoResolucionAmbiental documento : listadocumentoFacturaEmisionAmbiental)
			{
				documento.setIdTabla(resolucionAAA.getId());
				documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(tramite, "RESOLUCION LICENCIA AMBIENTAL", idProcesoResolucion, documento, TipoDocumentoSistema.EMISION_LICENCIA_FACTURA_PERMISO_AMBIENTAL);
			}				
			for(DocumentoResolucionAmbiental documento : listadocumentoProtocolizacionPagoAmbiental)
			{
				documento.setIdTabla(resolucionAAA.getId());
				documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(tramite, "RESOLUCION LICENCIA AMBIENTAL", idProcesoResolucion, documento, TipoDocumentoSistema.EMISION_LICENCIA_PAGO_PERMISO_AMBIENTAL);
			}
			for(DocumentoResolucionAmbiental documento : listadocumentoPolizaGarantiaBancariaAmbiental)
			{
				documento.setIdTabla(resolucionAAA.getId());
				documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(tramite, "RESOLUCION LICENCIA AMBIENTAL", idProcesoResolucion, documento, TipoDocumentoSistema.EMISION_LICENCIA_POLIZA_PMA);
			}
			for(DocumentoResolucionAmbiental documento : listadocumentoJustificacionCostoMedidasAmbiental)
			{
				documento.setIdTabla(resolucionAAA.getId());
				documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(tramite, "RESOLUCION LICENCIA AMBIENTAL", idProcesoResolucion, documento, TipoDocumentoSistema.EMISION_LICENCIA_MEDIDAS_PMA);
			}	
			for(DocumentoResolucionAmbiental documento : listadocumentoCronogramaPMAAmbiental)
			{
				documento.setIdTabla(resolucionAAA.getId());
				documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(tramite, "RESOLUCION LICENCIA AMBIENTAL", idProcesoResolucion, documento, TipoDocumentoSistema.EMISION_LICENCIA_CRONOGRAMA_PMA);
			}
			if ((listadocumentoFacturaTasaCS != null) && (listadocumentoFacturaTasaCS.size() > 0))
			{
				for(DocumentoResolucionAmbiental documento : listadocumentoFacturaTasaCS)
				{
					documento.setIdTabla(resolucionAAA.getId());
					documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(tramite, "RESOLUCION LICENCIA AMBIENTAL", idProcesoResolucion, documento, TipoDocumentoSistema.FACTURA_PAGO_TASA_CONTROL_SEGUIMIENTO);
				}				
			}
			if ((listadocumentoFacturaIF != null) && (listadocumentoFacturaIF.size() > 0))
			{
				for(DocumentoResolucionAmbiental documento : listadocumentoFacturaIF)
				{
					documento.setIdTabla(resolucionAAA.getId());
					documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(tramite, "RESOLUCION LICENCIA AMBIENTAL", idProcesoResolucion, documento, TipoDocumentoSistema.FACTURA_PAGO_TASA_INVENTARIO_FORESTAL);
				}					
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al subir el archivo al repositorio Alfresco. Por favor comunicarse con mesa de ayuda.");
			return false;
		}
	}
	
	public void enviarNotificacion(Usuario destinatario, String nombreNotificacion, String asunto, String rgd) {
		try{
			String tipoUsuario = operador;
			Object[] parametrosCorreo = new Object[] { tipoUsuario,
					destinatario.getPersona().getNombre(),tramite, rgd};
			String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(nombreNotificacion, parametrosCorreo);
			notificacion = notificacion.replaceAll("nombre_proponente", destinatario.getPersona().getNombre());
			notificacion = notificacion.replaceAll("numero_proyecto", tramite);
			notificacion = notificacion.replaceAll("registro_pago", rgd);
			Email.sendEmail(destinatario, "Solicitud Pago Registro", notificacion, tramite, JsfUtil.getLoggedUser());
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
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
}
