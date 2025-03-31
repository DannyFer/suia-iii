package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.ObservacionesResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ObservacionesResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.OficioResolucionAmbiental;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class EmisionLicenciaAmbientalCoordResolucionMemoController extends DocumentoReporteResolucionMemoController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5382084905489409831L;

	private static final Logger LOG = Logger
			.getLogger(EmisionLicenciaAmbientalCoordResolucionMemoController.class);
	
	public EmisionLicenciaAmbientalCoordResolucionMemoController() {	
		super();
	}
	
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;

	/* EJBs */
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ObservacionesResolucionAmbientalFacade observacionesResolucionAmbientalFacade;
	@EJB
	private AreaFacade areaFacade;
	
	private Map<String, Object> variables;
	@Setter
    @Getter
    private DocumentoResolucionAmbiental documentoResolucionAmbiental = new DocumentoResolucionAmbiental();
	@Setter
    @Getter
    private DocumentoResolucionAmbiental documentoMemorandoAmbiental = new DocumentoResolucionAmbiental();
	
	@Setter
    @Getter
    private DocumentoResolucionAmbiental documentoFirmado;
	
	@Setter
	@Getter
	private String tramite, autoridadAmbiental, directorDRA, coordinadorJuridico, tecnicoJuridico;
	@Getter
	@Setter
	private Boolean esPlantaCentral, esCoordinacionZonal; 
	@Setter
	@Getter
	private String nombreOficioFirmado;
	@Getter
	@Setter
	private Boolean correccionInformeOficio, token, subido, descargado, firmaSoloToken;
	@Getter
	@Setter
	private Boolean guardadoAutoridad = false, esAutoridad = false;
	
	
	/*CONSTANTES*/
	// Id's tabla public.areas_types
    public static final Integer ID_TIPO_AREA_PLANTA_CENTRAL = 1;
    public static final Integer ID_TIPO_AREA_COORDINACION_ZONAL = 5;
	
	@PostConstruct
	public void init() {
		try {
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
			
			verificaToken();
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			autoridadAmbiental = (String) variables.get("autoridadAmbiental");
			directorDRA = (String) variables.get("directorDRA");
			coordinadorJuridico = (String) variables.get("coordinadorJuridico");
			tecnicoJuridico = (String) variables.get("tecnicoJuridico");
			esPlantaCentral = variables.containsKey("esPlantaCentral") ? (Boolean.valueOf((String) variables.get("esPlantaCentral"))) : null;
			esCoordinacionZonal = variables.containsKey("esCoordinacionZonal") ? (Boolean.valueOf((String) variables.get("esCoordinacionZonal"))) : null;
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			
			inicializarResolucionMemorando();
			correccionInformeOficio = false;
			
			visualizarResolucion(true);
			visualizarMemorando(true);
			verificaEsAutoridad();
			
			descargado = false;
			
		} catch (JbpmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			JsfUtil.addMessageError("Error visualizar informe / oficio");
			e.printStackTrace();
		}
	}
	
	private void verificaEsAutoridad() {
		String usuario = JsfUtil.getLoggedUser().getNombre();
		Usuario usuarioLogeado = usuarioFacade.buscarUsuario(usuario);
		Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
		String tipoArea = areaTramite.getTipoArea().getSiglas().toUpperCase();
		String rolPrefijo;
		
		//si es PC lo firma el director sino los coordinadores			
		if(tipoArea.equals(Constantes.SIGLAS_TIPO_AREA_PC) || areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
			rolPrefijo = (tipoArea.equals(Constantes.SIGLAS_TIPO_AREA_PC)) ? "role.resolucion.pc.director" : "role.resolucion.galapagos.director.calidad";
			
			usuarioFirma = buscarUsuarioBean.buscarUsuario(rolPrefijo, areaTramite);
			
			if (usuarioLogeado.equals(usuarioFirma)) {
				esAutoridad = true;
			}
		} else {
			esAutoridad = true;
		}
    }
	
	private Boolean verificaEsPlantaCentral() {
		Boolean verifica = false;
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
    	if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
    		verifica = true;
		}
    	
    	return verifica;
    }
	private Boolean verificaEsCoordinacionZonal() {
		Boolean verifica = false;
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
    	if (areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
    		verifica = true;
		} 
    	
    	return verifica;
    }
	private Boolean verificaRequiereDirector() {
		Boolean verifica = false;
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
    	if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
    		verifica = true;
		}else if(areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)){
			verifica = true;
		} 
    	return verifica;
    }
	
	public boolean verificaToken() {
		if(firmaSoloToken) {
			token = true;
			return token;
		}
		
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
	
	public void validarExisteObservacionesDocumentos() {
		try {
			correccionInformeOficio = false;
			List<ObservacionesResolucionAmbiental> observaciones = observacionesResolucionAmbientalFacade.listarPorIdClaseNombreClaseNoCorregidas(documentoMemorando.getId(), "RevisionResolucionMemorando");
			if(observaciones.size() > 0) {
				correccionInformeOficio = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public void guardar() {
		try {			
			guardadoAutoridad = true;
			JsfUtil.addMessageInfo("Datos Guardados Correctamente");
		} catch (Exception e) {							
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public String firmar() {
		try {
			if(null != documentoMemorandoAmbiental && documentoMemorandoAmbiental.getId() != null) {
				String documentOffice = documentoResolucionAmbientalFacade.direccionDescarga(documentoMemorandoAmbiental);
				return DigitalSign.sign(documentOffice, loginBean.getUsuario().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
    	return "";
	}
	
	public void crearResolucionMemorando() {
		try {
				
			buscarDocumentoMemorando();
			
			if(documentoMemorandoAmbiental != null && documentoMemorandoAmbiental.getId() != null) {
				documentoMemorandoAmbiental.setEstado(false);
				documentoResolucionAmbientalFacade.guardar(documentoMemorandoAmbiental);
			}
			
			visualizarMemorando(false);
			
			documentoMemorandoAmbiental = new DocumentoResolucionAmbiental();
			documentoMemorandoAmbiental.setNombre("Memorando Licencia Ambiental.pdf");
			documentoMemorandoAmbiental.setMime("application/pdf");
			documentoMemorandoAmbiental.setContenidoDocumento(documentoMemorando.getArchivoInforme());
			documentoMemorandoAmbiental.setExtension(".pdf");
			documentoMemorandoAmbiental.setResolucionAmbiental(resolucionAmbiental);
			documentoMemorandoAmbiental.setIdTabla(documentoMemorando.getId());
			documentoMemorandoAmbiental.setNombreTabla(OficioResolucionAmbiental.class.getSimpleName());
			documentoMemorandoAmbiental.setDescripcionTabla("Memorando Licencia Ambiental");
			
			documentoMemorandoAmbiental = documentoResolucionAmbientalFacade.
					guardarDocumentoAlfrescoResolucionAmbiental(resolucionAmbiental.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
							"EMISION_LICENCIA", 
							0L, documentoMemorandoAmbiental, TipoDocumentoSistema.RCOA_LA_MEMORANDO);
			
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void prepararParaFirma() {
		try {
			if(correccionInformeOficio){
				JsfUtil.addMessageError("Existen observaciones de documentos que deben ser subsanadas antes de la firma");
				return;
			}	
			
			if(!guardadoAutoridad){
				JsfUtil.addMessageError("Debe guardar la información");
				return;
			}

			crearResolucionMemorando();
			
			RequestContext.getCurrentInstance().execute("PF('signDialog').show();");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public StreamedContent descargar() {
		DefaultStreamedContent content = null;
		try {
			buscarDocumentoMemorando();
			System.out.println("workspace. " + documentoMemorandoAmbiental.getIdAlfresco());
			
			byte[] documentoContent = null;
						
			if (null != documentoMemorandoAmbiental && null != documentoMemorandoAmbiental.getIdAlfresco()) {
				documentoContent = documentoResolucionAmbientalFacade.descargar(documentoMemorandoAmbiental.getIdAlfresco());
			} else if (null != documentoMemorandoAmbiental.getContenidoDocumento()) {
				documentoContent = documentoMemorandoAmbiental.getContenidoDocumento();
			}
			
			if (null != documentoMemorandoAmbiental && null != documentoMemorandoAmbiental.getNombre() 
					&& null != documentoContent) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoMemorandoAmbiental.getNombre());
				
				descargado = true;
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void buscarDocumentoResolucion() {
    	try {
    		List<DocumentoResolucionAmbiental> listaDocumentos = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(
    				resolucionAmbiental.getId(), 
    				TipoDocumentoSistema.RCOA_LA_RESOLUCION);
    		if(!listaDocumentos.isEmpty()) {    			
    			documentoResolucionAmbiental = listaDocumentos.get(0);    			
    		} 		
    		
		} catch (Exception e) {
			documentoResolucionAmbiental = new DocumentoResolucionAmbiental();
			e.printStackTrace();
		}
    }
	
	public void buscarDocumentoMemorando() {
    	try {
    		List<DocumentoResolucionAmbiental> listaDocumentos = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(
    				documentoMemorando.getId(), 
    				TipoDocumentoSistema.RCOA_LA_MEMORANDO);
    		if(!listaDocumentos.isEmpty()) {    			
    			documentoMemorandoAmbiental = listaDocumentos.get(0);    			
    		} 		
    		
		} catch (Exception e) {
			documentoMemorandoAmbiental = new DocumentoResolucionAmbiental();
			e.printStackTrace();
		}
    }
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		
		if(descargado){
			documentoFirmado = new DocumentoResolucionAmbiental();
			TipoDocumento tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(TipoDocumentoSistema.RCOA_LA_MEMORANDO.getIdTipoDocumento());
			
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoFirmado.setContenidoDocumento(contenidoDocumento);
			documentoFirmado.setNombre(event.getFile().getFileName());
			documentoFirmado.setExtension(".pdf");
			documentoFirmado.setMime("application/pdf");
			documentoFirmado.setIdTabla(documentoMemorando.getId());
			documentoFirmado.setResolucionAmbiental(resolucionAmbiental);
			documentoFirmado.setNombreTabla(OficioResolucionAmbiental.class.getSimpleName());
			documentoFirmado.setTipoDocumento(tipoDocumento);
			documentoFirmado.setIdProceso(JsfUtil.getCurrentProcessInstanceId());
			documentoMemorandoAmbiental.setDescripcionTabla("Memorando Licencia Ambiental");			
			subido = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento del memorando");
		}
	}
	
	private Usuario asignarUsuarioAlBPM(String rolPrefijo, String usuarioBpm) {
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
		
		if(rolPrefijo == "director") {
			rolPrefijo = "role.resolucion.pc.director";
			if(areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS))
				rolPrefijo = "role.resolucion.galapagos.director.calidad";
		} else {
			if(areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				areaTramite = areaFacade.getAreaSiglas("CGAJ") ;
			} else if (areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT))
				areaTramite = areaTramite.getArea();
		}
		
		String rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
    	List<Usuario> listaUsuariosCargaLaboral = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());			

		if (listaUsuariosCargaLaboral==null || listaUsuariosCargaLaboral.isEmpty()){
			LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
			return null;
		}

		Usuario nuevoResponsable = null;
		
		// recuperar tecnico de bpm y validar si el usuario existe en el listado anterior		
		Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usuarioBpm);
		if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
			if (listaUsuariosCargaLaboral != null && listaUsuariosCargaLaboral.size() >= 0
					&& listaUsuariosCargaLaboral.contains(usuarioTecnico)) {
				nuevoResponsable = usuarioTecnico;
			}
		}
		
		// si no se encontró el usuario se realiza la busqueda de uno nuevo y se actualiza en el bpm
		if (nuevoResponsable == null) {
			nuevoResponsable = listaUsuariosCargaLaboral.get(0);
		}

		return nuevoResponsable;
    }
	
	public void finalizar(){
    	try {			
    		if(!token){
    			if (!subido) {
					JsfUtil.addMessageError("Debe adjuntar el memorando firmado.");
					return;
    			} else {
					documentoMemorandoAmbiental = documentoResolucionAmbientalFacade.
							guardarDocumentoAlfrescoResolucionAmbiental(resolucionAmbiental.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
									"EMISION_LICENCIA", 
									0L, documentoFirmado, TipoDocumentoSistema.RCOA_LA_MEMORANDO);
    			}
			}
    		
    		if (documentoMemorandoAmbiental.getId() != null) {					
				String idAlfresco = documentoMemorandoAmbiental.getIdAlfresco();
				if (token && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El memorando no está firmado electrónicamente.");
					return;
				} 				
				
				Map<String, Object> params=new HashMap<>();
				params.put("requiereRevisionDirector", verificaRequiereDirector());
				
				// Es planta central
				if (esPlantaCentral == null) {
					esPlantaCentral = verificaEsPlantaCentral();
					params.put("esPlantaCentral",esPlantaCentral);
				}
				
				// Es coordinacion zonal
				if (esCoordinacionZonal == null) {
					esCoordinacionZonal = verificaEsCoordinacionZonal();
					params.put("esCoordinacionZonal",esCoordinacionZonal);
				}
				
				if(esPlantaCentral || (!esPlantaCentral && !esCoordinacionZonal)) {
					String tipoRol = proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC) ? "role.resolucion.pc.coordinador.juridico" : "role.resolucion.coordinador.juridico";
					Usuario nuevoResponsable = asignarUsuarioAlBPM(tipoRol, coordinadorJuridico);
					if(nuevoResponsable == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
	    				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	    				return;
					} else if (coordinadorJuridico == null || !coordinadorJuridico.equals(nuevoResponsable.getNombre())) {
						params.put("coordinadorJuridico",nuevoResponsable.getNombre());
					}
				}
				
				if(!esPlantaCentral && esCoordinacionZonal) {
					// Tecnico Juridico
					String tipoRol = "role.resolucion.tecnico.juridico";
					Usuario nuevoResponsable = asignarUsuarioAlBPM(tipoRol, tecnicoJuridico);
					if(nuevoResponsable == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
	    				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	    				return;
					} else if (tecnicoJuridico == null || !tecnicoJuridico.equals(nuevoResponsable.getNombre())) {
						params.put("tecnicoJuridico",nuevoResponsable.getNombre());
					}
				}
				
				documentoMemorando.setEstadoAprobacion(true);
				oficioResolucionAmbientalFacade.guardar(documentoMemorando);
				
				params.put("existeObservacion", correccionInformeOficio);
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getProcessId(), null);
				
				documentoMemorandoAmbiental.setIdProceso(bandejaTareasBean.getProcessId());
				documentoResolucionAmbientalFacade.guardar(documentoMemorandoAmbiental);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
    }
	
	public void enviar(){
		try {
			
			Map<String, Object> parametros = new HashMap<>();
			Boolean requiereDirector = verificaRequiereDirector();
			
			parametros.put("existeObservacion", correccionInformeOficio);
			parametros.put("requiereRevisionDirector", requiereDirector);
			
			if (!variables.containsKey("esPlantaCentral")) {
				esPlantaCentral = verificaEsPlantaCentral();
				parametros.put("esPlantaCentral",esPlantaCentral);
			}
			
			if (!variables.containsKey("esCoordinacionZonal")) {
				esCoordinacionZonal = verificaEsCoordinacionZonal();
				parametros.put("esCoordinacionZonal",esCoordinacionZonal);
			}
			
			if(requiereDirector) {
				Usuario nuevoResponsable = asignarUsuarioAlBPM("director", directorDRA);
				if(nuevoResponsable == null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
	    			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	    			return;
				} else if (directorDRA == null || !directorDRA.equals(nuevoResponsable.getNombre())) {
					parametros.put("directorDRA",nuevoResponsable.getNombre());
				}
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);	
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectToBandeja();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
