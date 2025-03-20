package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade.ObservacionesResolucionAmbientalFacade;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.DocumentoResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ObservacionesResolucionAmbiental;
import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.OficioResolucionAmbiental;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class EmisionLicenciaAmbientalTecnicoJuriResolucionMemoController extends DocumentoReporteResolucionMemoController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5382084905489409831L;

	private static final Logger LOG = Logger
			.getLogger(EmisionLicenciaAmbientalTecnicoJuriResolucionMemoController.class);
	
	public EmisionLicenciaAmbientalTecnicoJuriResolucionMemoController() {	
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
	
	@Getter
	@Setter
	private OficioResolucionAmbiental documentoParaObservar = new OficioResolucionAmbiental();
	
	@Getter
	@Setter
	private DocumentoResolucionAmbiental documentoPronunciamientoJuridico, documentoFirmado;

	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private Boolean correccionResolucion, datosGuardados, habilitarFirma, firmaSoloToken, descargado, subido, existeObservaciones;
	@Setter
    @Getter
    private String tramite, coordinadorJuridico, autoridadAmbiental, claseObservaciones, nombreTipoDocumento;
	@Getter
	@Setter
	private String urlAlfresco;
	@Getter
	@Setter
	private boolean token;
	@Getter
	@Setter
	private boolean pronunciamientoConformidadLegal;
	@Setter
	@Getter
	private Integer idDocumentoPronunciamiento;
	
	/*CONSTANTES*/
	// Id's tabla public.areas_types
    public static final Integer ID_TIPO_AREA_PLANTA_CENTRAL = 1;
	
	@PostConstruct
	public void init() {
		try {
			
			urlAlfresco = "";
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
			if(!firmaSoloToken){
				verificaToken();
			}else{
				token = true;
			}
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			autoridadAmbiental = (String) variables.get("autoridadAmbiental");
			coordinadorJuridico = (String) variables.get("coordinadorJuridico");
			
			pronunciamientoConformidadLegal = variables.containsKey("pronunciamientoConformidadLegal") ? (Boolean.valueOf((String) variables.get("pronunciamientoConformidadLegal"))) : false;
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			inicializarMemorandoPronunciamiento(true);
			
			datosGuardados = false;
			correccionResolucion = false;
			descargado = false;
			
			visualizarResolucion(true);
			visualizarMemorandoTecnicoJuridico(true);
			visualizarPronunciamiento(true);
			
			validarExisteObservacionesJuridicoResolucion();
			
			Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
			String tipoArea = areaTramite.getTipoArea().getSiglas().toUpperCase();
			//si es PC, GAD, PNG lo firma el director sino los coordinadores			
			habilitarFirma = false;
			if(tipoArea.equals(Constantes.SIGLAS_TIPO_AREA_OT))
				habilitarFirma = true;
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Error extraer las observaciones");
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
	
	public void validarExisteObservacionesJuridicoResolucion() {
		try {
			correccionResolucion = false;
			List<ObservacionesResolucionAmbiental> observaciones = observacionesResolucionAmbientalFacade.listarPorIdClaseNombreClaseNoCorregidas(documentoResolucion.getId(), "ObservacionesResolucionAmbientalJuridico");
			if(observaciones.size() > 0) {
				correccionResolucion = true;
			}
			
			if(correccionResolucion) {
				idDocumentoPronunciamiento = documentoMemorandoTecnicoJuridico.getId();
				claseObservaciones = "MemorandoJuridicoResolucion";
				nombreTipoDocumento = "Memorando";
			} else {
				idDocumentoPronunciamiento = documentoPronunciamiento.getId();
				claseObservaciones = "PronunciamientoConformidadResolucion";
				nombreTipoDocumento = "Pronunciamiento";
			}
			
			existeObservaciones = false;
			List<ObservacionesResolucionAmbiental> observacionesDocumento = observacionesResolucionAmbientalFacade.listarPorIdClaseNombreClaseNoCorregidas(idDocumentoPronunciamiento, claseObservaciones);
			if(observacionesDocumento.size() > 0) {
				existeObservaciones = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public void guardar() {
		try {
			if(correccionResolucion) {
				oficioResolucionAmbientalFacade.guardar(documentoMemorandoTecnicoJuridico);
				
				visualizarMemorandoTecnicoJuridico(true);
			} else {
				oficioResolucionAmbientalFacade.guardar(documentoPronunciamiento);
				
				visualizarPronunciamiento(true);
			}
			
			datosGuardados = true;
			JsfUtil.addMessageInfo("Datos Guardados Correctamente");
		} catch (Exception e) {							
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void subirDocumento() { 
		try {

			if (!validarDatos()) {
				return;
			}
			String nombreDoc = "";
			String descripcionDoc = "";
			Integer idTabladocumento = 0;
			if(correccionResolucion) {
				visualizarMemorandoTecnicoJuridico(false);
				nombreDoc = "Memorando juridico.pdf";
				descripcionDoc = "Memorando juridico";
				idTabladocumento = documentoMemorandoTecnicoJuridico.getId();
			} else {
				visualizarPronunciamiento(false);
				nombreDoc = "Pronunciamiento de conformidad legal.pdf";
				descripcionDoc = "Pronunciamiento de confirmidad legal";
				idTabladocumento = documentoPronunciamiento.getId();
			}
			
			TipoDocumentoSistema tipoDoc = (correccionResolucion) ? TipoDocumentoSistema.EMISION_LICENCIA_MEMORANDO_TECNICO_JURIDICO : TipoDocumentoSistema.RCOA_LA_PRONUNCIAMIENTO;
			
			List<DocumentoResolucionAmbiental> listaDocumentos = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(
					idTabladocumento, tipoDoc);
    		if(!listaDocumentos.isEmpty()) {    			
    			documentoPronunciamientoJuridico = listaDocumentos.get(0);
    			
    			if(documentoPronunciamientoJuridico != null && documentoPronunciamientoJuridico.getId() != null) {
    				documentoPronunciamientoJuridico.setEstado(false);
    				documentoResolucionAmbientalFacade.guardar(documentoPronunciamientoJuridico);
    			}
    		}
			
			byte[] contenidoDocumento = (correccionResolucion) ? documentoMemorandoTecnicoJuridico.getArchivoInforme() : documentoPronunciamiento.getArchivoInforme();
			
			documentoPronunciamientoJuridico = new DocumentoResolucionAmbiental();
			documentoPronunciamientoJuridico.setNombre(nombreDoc);
			documentoPronunciamientoJuridico.setMime("application/pdf");
			documentoPronunciamientoJuridico.setContenidoDocumento(contenidoDocumento);
			documentoPronunciamientoJuridico.setExtension(".pdf");
			documentoPronunciamientoJuridico.setNombreTabla(OficioResolucionAmbiental.class.getSimpleName());
			documentoPronunciamientoJuridico.setResolucionAmbiental(resolucionAmbiental);
			documentoPronunciamientoJuridico.setIdTabla(idTabladocumento);
			documentoPronunciamientoJuridico.setDescripcionTabla(descripcionDoc);
			
			documentoPronunciamientoJuridico = documentoResolucionAmbientalFacade.
					guardarDocumentoAlfrescoResolucionAmbiental(resolucionAmbiental.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
							"EMISION_LICENCIA", 
							0L, documentoPronunciamientoJuridico, tipoDoc);
			
			String documentOffice = documentoResolucionAmbientalFacade.direccionDescarga(documentoPronunciamientoJuridico);
			urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			
		} catch (Exception e) {							
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
		
		RequestContext.getCurrentInstance().update("formDialogs:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = null;
			String nombreDoc= "";
			
			if(!correccionResolucion) {
				documentoContent = documentoPronunciamiento.getArchivoInforme();
				nombreDoc = documentoPronunciamiento.getNombreFichero();
			} else {
				documentoContent = documentoMemorandoTecnicoJuridico.getArchivoInforme();
				nombreDoc = documentoMemorandoTecnicoJuridico.getNombreFichero();
			}
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent),  "application/pdf");
				content.setName(nombreDoc);
			} else
				JsfUtil.addMessageError("Error al generar el archivo");
			
			descargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		
		if(descargado){
			documentoFirmado = new DocumentoResolucionAmbiental();
			
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoFirmado.setContenidoDocumento(contenidoDocumento);
			documentoFirmado.setNombre(event.getFile().getFileName());
			documentoFirmado.setExtension(".pdf");
			documentoFirmado.setMime("application/pdf");
			documentoFirmado.setResolucionAmbiental(resolucionAmbiental);
			documentoFirmado.setIdTabla(documentoPronunciamientoJuridico.getIdTabla());
			documentoFirmado.setNombreTabla(OficioResolucionAmbiental.class.getSimpleName());
			documentoFirmado.setTipoDocumento(documentoPronunciamientoJuridico.getTipoDocumento());
			documentoFirmado.setIdProceso(JsfUtil.getCurrentProcessInstanceId());
			documentoFirmado.setDescripcionTabla(documentoPronunciamientoJuridico.getDescripcionTabla());
			subido = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento del memorando");
		}
	}
	
	private Boolean validarDatos() {
		Boolean validar = true;
		List<String> msg= new ArrayList<String>();
		if (correccionResolucion) {
			if (documentoMemorandoTecnicoJuridico.getAsuntoOficio() == null 
					|| documentoMemorandoTecnicoJuridico.getAsuntoOficio().isEmpty()) {
				msg.add("El campo 'Asunto' es requerido");
			}
			if (documentoMemorandoTecnicoJuridico.getPronunciamientoOficio() == null
					|| documentoMemorandoTecnicoJuridico.getPronunciamientoOficio().isEmpty()) {
				msg.add("El campo 'Pronunciamiento' es requerido");
			}
		} else {
			if (documentoPronunciamiento.getAsuntoOficio() == null || documentoPronunciamiento.getAsuntoOficio().isEmpty()) {
				msg.add("El campo 'Asunto' es requerido");
			}
			if (documentoPronunciamiento.getPronunciamientoOficio() == null || documentoPronunciamiento.getPronunciamientoOficio().isEmpty()) {
				msg.add("El campo 'Pronunciamiento' es requerido");
			}
		}

		if (msg.size() > 0) {
			JsfUtil.addMessageError(msg);
			validar = false;
			datosGuardados = false;
		}
		return validar;
	}
	
	private Usuario asignarCoordinadorJuridico() {
		Boolean esPlantaCentral = proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC);
		String rolPrefijo = esPlantaCentral ? "role.resolucion.pc.coordinador.juridico" : "role.resolucion.coordinador.juridico";
		String rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
		
		Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
		if(esPlantaCentral) {
			areaTramite = areaFacade.getAreaSiglas("CGAJ") ;
		}
		
    	List<Usuario> listaUsuario = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());			

		if (listaUsuario==null || listaUsuario.isEmpty()){
			LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
			return null;
		}
		
		// recuperar tecnico de bpm y validar si el usuario existe en el listado anterior
		Usuario tecnicoResponsable = null;
		Usuario usuarioTecnico = usuarioFacade.buscarUsuario(coordinadorJuridico);
		if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
			if (listaUsuario != null && listaUsuario.size() >= 0
					&& listaUsuario.contains(usuarioTecnico)) {
				tecnicoResponsable = usuarioTecnico;
			}
		}
		
		// si no se encontró el usuario se realiza la busqueda de uno nuevo y se actualiza en el bpm
		if (tecnicoResponsable == null) {
			tecnicoResponsable = listaUsuario.get(0);
		}

		return tecnicoResponsable;
    }
	
	public void enviar() {
		if (validarDatos()) {
			try {
				Map<String, Object> params=new HashMap<>();
				// se envia al Coordinador Juridico para firma para PC, GADs, PNG
				Usuario userCoordinadorJuridico = asignarCoordinadorJuridico();
				if(userCoordinadorJuridico == null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
					return;
				} else if (coordinadorJuridico == null || !coordinadorJuridico.equals(userCoordinadorJuridico.getNombre())) {
					params.put("coordinadorJuridico", userCoordinadorJuridico.getNombre());
				}
				
				if(!correccionResolucion) {
					documentoResolucion.setConsiderandoLegal(generarConsiderandoLegal());
		            oficioResolucionAmbientalFacade.guardar(documentoResolucion);
				}
				
				params.put("pronunciamientoConformidadLegal", !correccionResolucion);
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getProcessId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (Exception e) {
				e.printStackTrace();
				JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
			}
		}
	}
	
	public void completarTarea() {
		if (validarDatos()) {
			try {
				if(!token){
	    			if (!subido) {
						JsfUtil.addMessageError("Debe adjuntar el documento firmado.");
						return;
	    			} else {
	    				TipoDocumentoSistema tipoDoc = (correccionResolucion) ? TipoDocumentoSistema.EMISION_LICENCIA_MEMORANDO_TECNICO_JURIDICO : TipoDocumentoSistema.RCOA_LA_PRONUNCIAMIENTO;
	    				
	    				documentoPronunciamientoJuridico = documentoResolucionAmbientalFacade.
								guardarDocumentoAlfrescoResolucionAmbiental(resolucionAmbiental.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
										"EMISION_LICENCIA", 
										0L, documentoFirmado, tipoDoc);
	    			}
				} else {
					if (documentoPronunciamientoJuridico.getId() != null) {					
						String idAlfresco = documentoPronunciamientoJuridico.getIdAlfresco();
						if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
							JsfUtil.addMessageError("El documento no está firmado electrónicamente.");
							return;
						} 
					}
				}
				
				Map<String, Object> params=new HashMap<>();
				
				if(correccionResolucion) {
					//buscar coordinador sector para corregir resolucion
					String coordinadorSector = (String) variables.get("coordinadorSector");
					Usuario tecnico = asignarCoordinadorSector(coordinadorSector);
	    			if(tecnico == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
    					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
    					return;
					} else if (coordinadorSector == null || !coordinadorSector.equals(tecnico.getNombre())) {
						params.put("coordinadorSector",tecnico.getNombre());
					}

				} else {
					//buscar autoridad
					Usuario usuarioAutoridad = asignarAutoridad();
					if(usuarioAutoridad == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
						return;
					}
					
					params.put("autoridadAmbiental", usuarioAutoridad.getNombre());
				}
				
				params.put("pronunciamientoConformidadLegal", !correccionResolucion);
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getProcessId(), null);
				
				if(correccionResolucion) {
					documentoMemorandoTecnicoJuridico.setEstadoAprobacion(true);
					oficioResolucionAmbientalFacade.guardar(documentoMemorandoTecnicoJuridico);
				} else {
					documentoResolucion.setConsiderandoLegal(generarConsiderandoLegal());
		            oficioResolucionAmbientalFacade.guardar(documentoResolucion);
		            
					documentoPronunciamiento.setEstadoAprobacion(true);
					oficioResolucionAmbientalFacade.guardar(documentoPronunciamiento);
				}
				
				
				documentoPronunciamientoJuridico.setIdProceso(bandejaTareasBean.getProcessId());
				documentoResolucionAmbientalFacade.guardar(documentoPronunciamientoJuridico);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (Exception e) {							
				JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
			}
		}
	}
	
	private Usuario asignarCoordinadorSector(String coordinadorSector) {
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
    	String rolPrefijo = "role.esia.cz.coordinador";    	
		String rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
    	
		//se busca solo para OT
    	List<Usuario> listaUsuariosCargaLaboral = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());			

		if (listaUsuariosCargaLaboral==null || listaUsuariosCargaLaboral.isEmpty()){
			LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
			return null;
		}
		
		Usuario nuevoResponsable = null;
		
		// recuperar tecnico de bpm y validar si el usuario existe en el listado anterior		
		Usuario usuarioTecnico = usuarioFacade.buscarUsuario(coordinadorSector);
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
	
	private Usuario asignarAutoridad() {		
		String rolPrefijo = "role.resolucion.cz.gad.autoridad";
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
		
		if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
			rolPrefijo = "role.resolucion.pc.autoridad";
			
			List<Usuario> listaUsuariosResponsables = usuarioFacade.buscarUsuarioPorRol(Constantes.getRoleAreaName(rolPrefijo));
			if (listaUsuariosResponsables==null || listaUsuariosResponsables.isEmpty()){
				LOG.error("No se encontro usuario " + Constantes.getRoleAreaName(rolPrefijo));
				return null;
			}
			
			return listaUsuariosResponsables.get(0);
			
		} else if (areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT)) {
			areaTramite = areaTramite.getArea();
		} 
		
		String rolDirector = Constantes.getRoleAreaName(rolPrefijo);
		Usuario usuarioResponsable = buscarUsuarioBean.buscarUsuario(rolPrefijo, areaTramite);

		if (usuarioResponsable == null){
			LOG.error("No se encontro usuario " + rolDirector + " en " + areaTramite.getAreaName());
			return null;
		}
		
		return usuarioResponsable;
    }
	
	private String generarConsiderandoLegal() {
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		String fecha = formatoFechaEmision.format(new Date());
		Object[] parametrosMensaje = new Object[] {documentoPronunciamiento.getCodigoReporte(), fecha, 
				areaFirmaJuridico.getAreaName(), proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental()};
		
		CatalogoGeneralCoa catalogoConsiderando = catalogoCoaFacade.obtenerCatalogoPorCodigo("resolucion.texto.legal");
        String considerandoLegal = String.format(catalogoConsiderando.getValor(), parametrosMensaje);
        
        return considerandoLegal;
	}

}
