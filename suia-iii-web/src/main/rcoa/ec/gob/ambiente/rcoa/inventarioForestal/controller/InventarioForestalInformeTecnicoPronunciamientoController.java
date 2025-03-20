package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ObservacionesInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ReporteInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ObservacionesInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ReporteInventarioForestal;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class InventarioForestalInformeTecnicoPronunciamientoController extends DocumentoReporteInformeTecnicoController implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(InventarioForestalInformeTecnicoPronunciamientoController.class);
	
	
	public InventarioForestalInformeTecnicoPronunciamientoController() {
		super();
	}
	
	/* BEANs */
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;

	/* EJBs */
	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private 
	InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
	@EJB
	private ReporteInventarioForestalFacade reporteInventarioForestalFacade;
	@EJB
	private ObservacionesInventarioForestalFacade observacionesInventarioForestalFacade;
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	private DocumentoInventarioForestal documentoInventarioForestal;
	@Getter
	@Setter
	private InventarioForestalAmbiental inventarioForestalAmbiental;
	@Setter
	@Getter
	private List<ObservacionesInventarioForestal> listObservacionesInventarioForestal = new ArrayList<ObservacionesInventarioForestal>();

	

	@Getter
	@Setter
	private Boolean requiereInspeccion;
	@Getter
	@Setter
	private Boolean correccionInformeOficio, pronunciamientoParaArchivo, pronunciamientoFavorable;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;
	@Setter
    @Getter
	private DocumentoInventarioForestal documentoInformeTecnico = new DocumentoInventarioForestal();
	@Setter
    @Getter
	private DocumentoInventarioForestal documentoOficioPronunciamiento = new DocumentoInventarioForestal();
	
	@Getter
    @Setter
    private String autoridadAmbiental;
	@Getter
    @Setter
    private Integer categoria, cantidadObservacionesNoCorregidas;
	@Getter
	@Setter
	private boolean token, subido, firmaSoloToken; 
	@Setter
    @Getter
    private DocumentoInventarioForestal documentoFirmado = new DocumentoInventarioForestal();
	@Getter
	@Setter
	private boolean guardadoAutoridad = false;

	
	/*CONSTANTES*/
    public static final String TAREA_REVISION_OFICIO_PRONUNCIAMIENTO_TECNICO = "Revisar informe y oficio de pronunciamiento";
    public static final String TAREA_REVISION_OFICIO_PRONUNCIAMIENTO_DIRECTOR = "Revisar informe y oficio de pronunciamiento - Autoridad";
    private static final String DOFI_MIME = "application/";
	
	@PostConstruct
	public void init() {
		try {
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			autoridadAmbiental = (String) variables.get("autoridadAmbiental");
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			categoria = Integer.valueOf((String)variables.get("categoria"));
			cantidadNotificaciones = Integer.valueOf((String)variables.get("cantidadNotificaciones"));

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			cantidadObservacionesNoCorregidas = numeroObservacionesNoCorregidas();
			pronunciamientoFavorable=null;
			
			if (cantidadNotificaciones >= 2) {
				pronunciamientoParaArchivo= (cantidadObservacionesNoCorregidas>0) ? true : false;
				if (pronunciamientoParaArchivo == false) {
					pronunciamientoFavorable=(cantidadObservacionesNoCorregidas>0) ? false : true;
				}
			} else {
				pronunciamientoFavorable=(cantidadObservacionesNoCorregidas>0) ? false : true;
				pronunciamientoParaArchivo=false;
			}
						
			
			String tarea = bandejaTareasBean.getTarea().getTaskNameHuman();
			if (tarea.equals(TAREA_REVISION_OFICIO_PRONUNCIAMIENTO_TECNICO) 
					|| tarea.equals(TAREA_REVISION_OFICIO_PRONUNCIAMIENTO_DIRECTOR)) {
				buscarDocumentoInformeFirmado();
			} else {
				visualizarInforme(true);
			}
			
			visualizarOficio(true);
			
			correccionInformeOficio = informeTecnico.getRealizarCorrecion();
			
			inventarioForestalAmbiental = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(proyectoLicenciaCoa.getId());
			
			subido = false;
        	verificaToken();
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		} catch (Exception e) {
			JsfUtil.addMessageError("Error visualizar informe / oficio");
			e.printStackTrace();
		}
	}
	
	private Integer numeroObservacionesNoCorregidas() {
		Integer result = 0;
		Integer idClase=0;
		String nombreClase="";
		try {
			InventarioForestalAmbiental inventarioForestalAmbiental = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(idProyecto);
			idClase = inventarioForestalAmbiental.getId();
			nombreClase="InventarioForestalAmbiental";
			listObservacionesInventarioForestal = observacionesInventarioForestalFacade.listarPorIdClaseNombreClaseNoCorregidas(idClase, nombreClase);
			result = listObservacionesInventarioForestal.size();
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Error extraer las observaciones");
			e.printStackTrace();
			return 0;
		}
		return result;
	}
	
	private Usuario asignarDirectorForestal() {
		Area areaTramite;
    	if(proyectoLicenciaCoa.getAreaInventarioForestal().getAreaAbbreviation().contains(Constantes.SIGLAS_TIPO_AREA_OT))
    		areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal().getArea();
		else
			areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal();
    	String rolPrefijo;
		String rolDirector;
    	if (areaTramite.getTipoArea().getId().equals(Constantes.ID_TIPO_AREA_PC)) {
    		rolPrefijo = "role.inventario.pc.autoridad";
		} else {
			rolPrefijo = "role.inventario.cz.autoridad";
		}
    	rolDirector = Constantes.getRoleAreaName(rolPrefijo);
    	List<Usuario> listaTecnicosResponsables = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolDirector, areaTramite.getAreaName());			

		if (listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
			LOG.error("No se encontro usuario " + rolDirector + " en " + areaTramite.getAreaName());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}
		Usuario tecnicoResponsable = listaTecnicosResponsables.get(0);
		return tecnicoResponsable;
    }
	
	private boolean validarInformeTecnico() {
		Boolean validarIT = true;
		List<String> msg= new ArrayList<String>();
		if (informeTecnico.getSuperficieCoberturaVegetal() == null) {
    		msg.add("Ingrese la Superficie con covertura vegetal nativa a desbrozar");
		}
		if (informeTecnico.getNombresDelegadoInspeccion() == null) {
    		msg.add("Ingrese el Equipo técnico delegado para la inspección");
		}
		if (informeTecnico.getAreaDelegado() == null) {
			msg.add("Ingrese el Equipo técnico delegado para la inspección");
		}
		if (informeTecnico.getCargoDelegado() == null) {
			msg.add("Ingrese el Equipo técnico delegado para la inspección");
		}
		if (informeTecnico.getAntecedentes() == null) {
    		msg.add("Ingrese el Antecedentes");
		}
		if (informeTecnico.getMarcoLegal() == null) {
    		msg.add("Ingrese el Marco Legal");
		}
		if (informeTecnico.getObjetivo() == null) {
    		msg.add("Ingrese el Objetivo");
		}
		if (informeTecnico.getResultadoRevision() == null) {
    		msg.add("Ingrese el Resultado de la revisión");
		}
		if (informeTecnico.getConclusiones() == null) {
    		msg.add("Ingrese las Conclusiones");
		}
		if (informeTecnico.getRecomendaciones() == null) {
    		msg.add("Ingrese las Recomendaciones");
		}
		if (msg.size() > 0) {
			JsfUtil.addMessageError(msg);
			validarIT = false;
		}
		return validarIT;
	}
	
	private boolean validarOficioPronunciamiento() {
		Boolean validarOP = true;
		List<String> msg= new ArrayList<String>();
		if (oficioPronunciamiento.getAsuntoOficio() == null || oficioPronunciamiento.getAsuntoOficio().isEmpty()) {
    		msg.add("Ingrese el Asunto");
		}
		if (oficioPronunciamiento.getAntecedentes() == null || oficioPronunciamiento.getAntecedentes().isEmpty()) {
    		msg.add("Ingrese el Antecedente");
		}
		if (oficioPronunciamiento.getMarcoLegal() == null || oficioPronunciamiento.getMarcoLegal().isEmpty()) {
    		msg.add("Ingrese el Marco legal");
		}
		if (oficioPronunciamiento.getPronunciamiento() == null || oficioPronunciamiento.getPronunciamiento().isEmpty()) {
    		msg.add("Ingrese el Pronunciamiento");
		}
		if (msg.size() > 0) {
			JsfUtil.addMessageError(msg);
			validarOP = false;
		}
		return validarOP;
	}

	public void guardar() {
		try {
			reporteInventarioForestalFacade.guardar(informeTecnico);
			reporteInventarioForestalFacade.guardar(oficioPronunciamiento);
			asignaInformeTecnico();
			asignaOficioPronunciamiento();
			visualizarInforme(true);
			visualizarOficio(true);
			JsfUtil.addMessageInfo("Datos Guardados Correctamente");
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void enviar() {
		if (validarInformeTecnico() && validarOficioPronunciamiento()) {
			
			if(token) {
				try {
					crearDocumento();//genera el documento para firma con token
				} catch (Exception e) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
					e.printStackTrace();
				}
			}
			
			RequestContext.getCurrentInstance().execute("PF('signDialog').show();");			
			
			RequestContext.getCurrentInstance().update("formDialog:pnlFirmar");
		}
	}
	
	public void guardarRevision(){
		try {
			informeTecnico.setRealizarCorrecion(correccionInformeOficio);
			informeTecnico = reporteInventarioForestalFacade.guardar(informeTecnico);
			oficioPronunciamiento.setRealizarCorrecion(correccionInformeOficio);
			oficioPronunciamiento = reporteInventarioForestalFacade.guardar(oficioPronunciamiento);			
			
			guardadoAutoridad = true;
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void firmar(){
		try {
			System.out.println("ingreso a firma");
			
			if(observacionesPendientesRevision()){
				JsfUtil.addMessageError("Existen observaciones de documentos que deben ser subsanadas antes de la firma");
				return;
			}	
			
			if(!guardadoAutoridad){
				JsfUtil.addMessageError("Debe guardar la información");
				return;
			}
			
			String tarea = bandejaTareasBean.getTarea().getTaskNameHuman();		
			
			if (tarea.equals(TAREA_REVISION_OFICIO_PRONUNCIAMIENTO_DIRECTOR)) {				
				crearDocumentoOficio();
				RequestContext.getCurrentInstance().execute("PF('signDialogOficio').show();");
				RequestContext.getCurrentInstance().update("formDialog:pnlFirmarOficio");
			}else{			
				RequestContext.getCurrentInstance().execute("PF('signDialog').show();");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	
	public void enviarRevision() {
		try {
			
			if(!observacionesPendientesRevision()){
				JsfUtil.addMessageError("No existen observaciones para corrección del técnico");
				return;
			}		
			
			Map<String, Object> params=new HashMap<String, Object>();
			String tarea = bandejaTareasBean.getTarea().getTaskNameHuman();
			informeTecnico.setRealizarCorrecion(correccionInformeOficio);
			informeTecnico = reporteInventarioForestalFacade.guardar(informeTecnico);
			oficioPronunciamiento.setRealizarCorrecion(correccionInformeOficio);
			oficioPronunciamiento = reporteInventarioForestalFacade.guardar(oficioPronunciamiento);
			
			if (tarea.equals(TAREA_REVISION_OFICIO_PRONUNCIAMIENTO_TECNICO)) {
				params.put("existeObservacionCoordinacion",	correccionInformeOficio);
			}
			if (tarea.equals(TAREA_REVISION_OFICIO_PRONUNCIAMIENTO_DIRECTOR)) {
				params.put("existeObservacionDireccion",correccionInformeOficio);
			}
			

			if (autoridadAmbiental == null) {
				Usuario director = asignarDirectorForestal();
				params.put("autoridadAmbiental",director.getNombre());
			} else {
				Usuario directorBPM = usuarioFacade.buscarUsuario(autoridadAmbiental);
				if (directorBPM.getEstado() == false) {
					Usuario director = asignarDirectorForestal();
					params.put("autoridadAmbiental",director.getNombre());
				}
			}
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		} 
	}
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		
		if(firmaSoloToken)
			token = true;
		
		return token;
	}
	
	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String firmarDocumento() {
		try {						
			 if(documentoInformeTecnico != null && documentoInformeTecnico.getIdAlfresco() != null){
				 String documentOffice = documentoInventarioForestalFacade.direccionDescarga(documentoInformeTecnico); 
				 return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			 }		
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del oficio por el director", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
    
    public void crearDocumento() throws Exception {
		try {
			asignaInformeTecnico();
			visualizarInforme(false);

			documentoInformeTecnico.setNombreDocumento(informeTecnico.getNombreFichero());
			documentoInformeTecnico.setInventarioForestalAmbiental(inventarioForestalAmbiental);
			documentoInformeTecnico.setReporteInventarioForestal(informeTecnico);
			documentoInformeTecnico.setIdTabla(informeTecnico.getId());
			documentoInformeTecnico.setNombreTabla(ReporteInventarioForestal.class.getSimpleName());
			documentoInformeTecnico.setDescripcionTabla("Informe Tecnico");
			documentoInformeTecnico.setMimeDocumento(DOFI_MIME + "pdf");
			documentoInformeTecnico.setContenidoDocumento(informeTecnico.getArchivoInforme());
			documentoInformeTecnico.setExtencionDocumento(".pdf");
			documentoInformeTecnico = documentoInventarioForestalFacade
					.guardarDocumentoAlfrescoInventarioForestal(
							proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
							"INVENTARIO_FORESTAL",
							0L,
							documentoInformeTecnico,
							TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_TECNICO);

		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (CmisAlfrescoException e) {
			JsfUtil.addMessageError("Error al guardar el Informe de Inspección de Campo. Por favor comuníquese con Mesa de Ayuda");
			LOG.error(e);
		}
    }
    
    public void subirDocumento(){
    	try {
    		if(documentoFirmado.getContenidoDocumento() != null){
    			documentoFirmado.setNombreDocumento(informeTecnico.getNombreFichero());
    			documentoFirmado.setReporteInventarioForestal(informeTecnico);
    			documentoFirmado.setIdTabla(informeTecnico.getId());
    			documentoFirmado.setNombreTabla(ReporteInventarioForestal.class.getSimpleName());
    			documentoFirmado.setDescripcionTabla("Informe Tecnico");
    			documentoFirmado.setMimeDocumento(DOFI_MIME + "pdf");
    			documentoFirmado.setExtencionDocumento(".pdf");
				documentoFirmado = documentoInventarioForestalFacade
						.guardarDocumentoAlfrescoInventarioForestal(
								proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
								"INVENTARIO_FORESTAL",
								0L,
								documentoFirmado,
								TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_TECNICO);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void finalizar(){
    	try {			
    		if(token){
    			if (documentoInformeTecnico.getId() != null) {	
    				String idAlfresco = documentoInformeTecnico.getIdAlfresco();
        			if (token && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
    					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
    					return;
    				}
    			}else{
    				return;
    			}    			
    		}else{
    			if(!subido){
    				JsfUtil.addMessageError("Debe adjuntar el informe firmado.");
					return;
    			}
    		}  		
				
				if(!token){
					subirDocumento();
				}
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
    }    
   
    public void uploadListenerDocumentos(FileUploadEvent event) {
		if(documentoFirmado != null){
			byte[] contenidoDocumento = event.getFile().getContents();

			documentoFirmado.setContenidoDocumento(contenidoDocumento);
			documentoFirmado.setNombreDocumento(event.getFile().getFileName());			

			subido = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento del oficio");
		}		
	}
    
    public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			crearDocumento();
			
			byte[] documentoContent = null;
						
			if (documentoInformeTecnico != null && documentoInformeTecnico.getIdAlfresco() != null) {
				documentoContent = documentoInventarioForestalFacade.descargar(documentoInformeTecnico.getIdAlfresco());
			} else if (documentoInformeTecnico.getContenidoDocumento() != null) {
				documentoContent = documentoInformeTecnico.getContenidoDocumento();
			}
			
			if (documentoInformeTecnico != null && documentoInformeTecnico.getNombreDocumento() != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoInformeTecnico.getNombreDocumento());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
    public void buscarDocumentoInforme(){
    	try {
			
    		List<DocumentoInventarioForestal> listaDocumentos = documentoInventarioForestalFacade.getByInventarioForestalReporteTipo(informeTecnico.getId(), TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_TECNICO);
    		    		
    		if(listaDocumentos != null && !listaDocumentos.isEmpty()){    			
    			documentoInformeTecnico = listaDocumentos.get(0);    			
    		} 		
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
    public String firmarDocumentoRevision() {
		try {		
			buscarDocumentoInforme();
			
			 if(documentoInformeTecnico != null){
				 String documentOffice = documentoInventarioForestalFacade.direccionDescarga(documentoInformeTecnico); 
				 return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			 }		
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del oficio por el director", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
    
    public StreamedContent descargarRevision() throws IOException {
		DefaultStreamedContent content = null;
		try {
			buscarDocumentoInforme();
			System.out.println("workspace. " + documentoInformeTecnico.getIdAlfresco());
			
			byte[] documentoContent = null;
						
			if (documentoInformeTecnico != null && documentoInformeTecnico.getIdAlfresco() != null) {
				documentoContent = documentoInventarioForestalFacade.descargar(documentoInformeTecnico.getIdAlfresco());
			} else if (documentoInformeTecnico.getContenidoDocumento() != null) {
				documentoContent = documentoInformeTecnico.getContenidoDocumento();
			}
			
			if (documentoInformeTecnico != null && documentoInformeTecnico.getNombreDocumento() != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoInformeTecnico.getNombreDocumento());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
    
    public void finalizarRevision(){  	
    	try {
    		
    		Map<String, Object> params=new HashMap<String, Object>();
						
			params.put("existeObservacionCoordinacion",correccionInformeOficio);
    		
    		if (documentoInformeTecnico.getId() != null) {								

				String idAlfresco = documentoInformeTecnico.getIdAlfresco();

				if (token && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				} else if (!token && !subido) {
					JsfUtil.addMessageError("Debe adjuntar el informe firmado.");
					return;
				}
				
				if(!token){
					subirDocumento();
				}   
    		}
    		
    		if (autoridadAmbiental == null) {
    			Usuario director = asignarDirectorForestal();
    			params.put("autoridadAmbiental",director.getNombre());
    		} else {
    			Usuario directorBPM = usuarioFacade.buscarUsuario(autoridadAmbiental);
    			if (directorBPM == null || directorBPM.getEstado() == false) {
    				Usuario director = asignarDirectorForestal();
    				params.put("autoridadAmbiental",director.getNombre());
    			}
    		}
    		procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
    		procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
    		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
    		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
		}   	 	
    	
    }
    
    private void crearDocumentoOficio(){
    	try {
    	    visualizarOficio(false);
			documentoOficioPronunciamiento.setNombreDocumento(oficioPronunciamiento.getNombreFichero());
			documentoOficioPronunciamiento.setInventarioForestalAmbiental(inventarioForestalAmbiental);
			documentoOficioPronunciamiento.setReporteInventarioForestal(oficioPronunciamiento);
			documentoOficioPronunciamiento.setIdTabla(oficioPronunciamiento.getId());
			documentoOficioPronunciamiento.setNombreTabla(ReporteInventarioForestal.class.getSimpleName());
			documentoOficioPronunciamiento.setDescripcionTabla("Oficio Pronunciamiento");
			documentoOficioPronunciamiento.setMimeDocumento(DOFI_MIME + "pdf");
			documentoOficioPronunciamiento.setContenidoDocumento(oficioPronunciamiento.getArchivoInforme());
			documentoOficioPronunciamiento.setExtencionDocumento(".pdf");
			documentoOficioPronunciamiento = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "INVENTARIO_FORESTAL", 0L, documentoOficioPronunciamiento, TipoDocumentoSistema.INVENTARIO_FORESTAL_OFICIO_PRONUNCIAMIENTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void buscarDocumentoOficio(){
    	try {
			
    		List<DocumentoInventarioForestal> listaDocumentos = documentoInventarioForestalFacade.getByInventarioForestalReporteTipo(oficioPronunciamiento.getId(), TipoDocumentoSistema.INVENTARIO_FORESTAL_OFICIO_PRONUNCIAMIENTO);
    		    		
    		if(listaDocumentos != null && !listaDocumentos.isEmpty()){    			
    			documentoOficioPronunciamiento= listaDocumentos.get(0);    			
    		} 		
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
    public String firmarDocumentoOficio() {
		try {			
			buscarDocumentoOficio();
			
			 if(documentoOficioPronunciamiento != null && documentoOficioPronunciamiento.getIdAlfresco() != null){
				 String documentOffice = documentoInventarioForestalFacade.direccionDescarga(documentoOficioPronunciamiento); 
				 return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			 }		
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del oficio por el director", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
    
    public StreamedContent descargarOficio() throws IOException {
		DefaultStreamedContent content = null;
		try {
			
			buscarDocumentoOficio();
			System.out.println("workspace. " + documentoOficioPronunciamiento.getIdAlfresco());
			
			byte[] documentoContent = null;
						
			if (documentoOficioPronunciamiento != null && documentoOficioPronunciamiento.getIdAlfresco() != null) {
				documentoContent = documentoInventarioForestalFacade.descargar(documentoOficioPronunciamiento.getIdAlfresco());
			} else if (documentoOficioPronunciamiento.getContenidoDocumento() != null) {
				documentoContent = documentoOficioPronunciamiento.getContenidoDocumento();
			}
			
			if (documentoOficioPronunciamiento != null && documentoOficioPronunciamiento.getNombreDocumento() != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoOficioPronunciamiento.getNombreDocumento());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
    
    public void finalizarRevisionOficio(){  	
    	try {
    		
    		Map<String, Object> params=new HashMap<String, Object>();						
			
			params.put("existeObservacionDireccion",correccionInformeOficio);			
    		
    		if (documentoOficioPronunciamiento.getId() != null) {								

				String idAlfresco = documentoOficioPronunciamiento.getIdAlfresco();

				if (token && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El oficio no está firmado electrónicamente.");
					return;
				} else if (!token && !subido) {
					JsfUtil.addMessageError("Debe adjuntar el oficio firmado.");
					return;
				}
				
				if(!token){
					subirDocumentoOficio();
				}   
    		}
    		
    		if (autoridadAmbiental == null) {
    			Usuario director = asignarDirectorForestal();
    			params.put("autoridadAmbiental",director.getNombre());
    		} else {
    			Usuario directorBPM = usuarioFacade.buscarUsuario(autoridadAmbiental);
    			if (directorBPM.getEstado() == false) {
    				Usuario director = asignarDirectorForestal();
    				params.put("autoridadAmbiental",director.getNombre());
    			}
    		}
    		procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
    		procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
    		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
    		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
		}    	
    }
    
    private boolean observacionesPendientesRevision() {
    	
		Integer idClase=0;
		String nombreClase="";
		try {
			idClase = informeTecnico.getId();
			nombreClase="ReporteInventarioForestal";
			List<ObservacionesInventarioForestal> listObservacionesDocumentos = observacionesInventarioForestalFacade.listarPorIdClaseNombreClaseNoCorregidas(idClase, nombreClase);
			    			
			if(listObservacionesDocumentos != null && !listObservacionesDocumentos.isEmpty()){
				return true;
			}else
				return false;
			
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Error extraer las observaciones");
			e.printStackTrace();
			return false;
		}
	}
    
    public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
    
    public void subirDocumentoOficio(){
    	try {
    		if(documentoFirmado.getContenidoDocumento() != null){
    			documentoFirmado.setNombreDocumento(oficioPronunciamiento.getNombreFichero());
    			documentoFirmado.setReporteInventarioForestal(oficioPronunciamiento);
    			documentoFirmado.setIdTabla(oficioPronunciamiento.getId());
    			documentoFirmado.setNombreTabla(ReporteInventarioForestal.class.getSimpleName());
    			documentoFirmado.setDescripcionTabla("OficioPronunciamiento");
    			documentoFirmado.setMimeDocumento(DOFI_MIME + "pdf");
    			documentoFirmado.setExtencionDocumento(".pdf");
				documentoFirmado = documentoInventarioForestalFacade
						.guardarDocumentoAlfrescoInventarioForestal(
								proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
								"INVENTARIO_FORESTAL",
								0L,
								documentoFirmado,
								TipoDocumentoSistema.INVENTARIO_FORESTAL_OFICIO_PRONUNCIAMIENTO);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public String cancelar() {
		return JsfUtil.actionNavigateToBandeja();
	}
    
    public void buscarDocumentoInformeFirmado(){
    	try {
			consultarInformeTecnico();
			
    		List<DocumentoInventarioForestal> listaDocumentos = documentoInventarioForestalFacade.getByInventarioForestalReporteTipo(informeTecnico.getId(), TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_TECNICO);
    		    		
    		if(listaDocumentos != null && !listaDocumentos.isEmpty()){    			
    			documentoInformeTecnico = listaDocumentos.get(0);
    			
    			File informePdf = documentoInventarioForestalFacade.getDocumentoPorIdAlfresco(documentoInformeTecnico.getIdAlfresco());
    			
    			Path path = Paths.get(informePdf.getAbsolutePath());
    			informeTecnico.setArchivoInforme(Files.readAllBytes(path));
    			
    			String reporteHtmlfinal = informeTecnico.getNombreFichero().replace("/", "-");
    			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
    			FileOutputStream file = new FileOutputStream(archivoFinal);
    			file.write(informeTecnico.getArchivoInforme());
    			file.close();
    			informeTecnico.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informeTecnico.getNombreFichero()));
    			Boolean realizarCorrecion = (informeTecnico.getRealizarCorrecion() == null) ? false : informeTecnico.getRealizarCorrecion();
    			informeTecnico.setRealizarCorrecion(realizarCorrecion);
    		} 		
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
