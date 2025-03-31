package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
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
public class EmisionLicenciaAmbientalCoordJuriRevisarDocumentosController extends DocumentoReporteResolucionMemoController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5382084905489409831L;

	private static final Logger LOG = Logger
			.getLogger(EmisionLicenciaAmbientalCoordResolucionMemoController.class);
	
	public EmisionLicenciaAmbientalCoordJuriRevisarDocumentosController() {	
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

	private Map<String, Object> variables;
	@Setter
    @Getter
    private DocumentoResolucionAmbiental documentoResolucionAmbiental = new DocumentoResolucionAmbiental();
	
	@Setter
    @Getter
    private DocumentoResolucionAmbiental documentoPronunciamientoJuridico = new DocumentoResolucionAmbiental();
	
	@Setter
    @Getter
    private DocumentoResolucionAmbiental documentoFirmado = new DocumentoResolucionAmbiental();
	
	
	@Getter
	@Setter
	private boolean correccionInformeOficio;
	@Setter
	@Getter
	private String tramite, autoridadAmbiental, tecnicoJuridico, claseObservaciones, nombreTipoDocumento;
	@Setter
	@Getter
	private String nombreOficioFirmado, urlAlfresco;
	@Getter
	@Setter
	private boolean token, firmaSoloToken;
	@Getter
	@Setter
	private Boolean datosGuardados = false;
	@Getter
	@Setter
	private Boolean descargado, subido, esPronunciamientoConformidadLegal;
	@Setter
	@Getter
	private Integer idDocumentoPronunciamiento;
	
	/*CONSTANTES*/
	// Id's tabla public.areas_types
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
			tecnicoJuridico = (String) variables.get("tecnicoJuridico");
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			
			descargado = false;
			
			esPronunciamientoConformidadLegal = Boolean.parseBoolean( (String) variables.get("pronunciamientoConformidadLegal"));

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			inicializarMemorandoPronunciamiento(false);
			visualizarResolucion(true);
			if(esPronunciamientoConformidadLegal) {
				visualizarPronunciamiento(true);
				idDocumentoPronunciamiento = documentoPronunciamiento.getId();
				claseObservaciones = "PronunciamientoConformidadResolucion";
				nombreTipoDocumento = "Pronunciamiento";
			} else {
				visualizarMemorandoTecnicoJuridico(true);
				idDocumentoPronunciamiento = documentoMemorandoTecnicoJuridico.getId();
				claseObservaciones = "MemorandoJuridicoResolucion";
				nombreTipoDocumento = "Memorando";
			}
			
			correccionInformeOficio = observacionesPendientesRevision();
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Error visualizar la resolución / memorando");
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
	
	private boolean observacionesPendientesRevision() {
		Integer idClase=0;
		String nombreClase="";
		try {
			idClase = idDocumentoPronunciamiento;
			nombreClase = claseObservaciones;
			List<ObservacionesResolucionAmbiental> listObservacionesDocumentos = observacionesResolucionAmbientalFacade.listarPorIdClaseNombreClaseNoCorregidas(idClase, nombreClase);
			    			
			if (listObservacionesDocumentos != null && !listObservacionesDocumentos.isEmpty()) {
				return true;
			} else {
				return false;
			}
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Error extraer las observaciones");
			e.printStackTrace();
			return false;
		}
	}
	
	public void validarObservaciones() {
		correccionInformeOficio = observacionesPendientesRevision();
	}

	public void guardar() {
		try {
			documentoMemorandoTecnicoJuridico.setEstadoAprobacion(correccionInformeOficio);
			
			datosGuardados = true;
			
			JsfUtil.addMessageInfo("Datos Guardados Correctamente");
		} catch (Exception e) {							
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void prepararParaFirma() { 
		try {
			TipoDocumentoSistema tipoDoc = (!esPronunciamientoConformidadLegal) ? TipoDocumentoSistema.EMISION_LICENCIA_MEMORANDO_TECNICO_JURIDICO : TipoDocumentoSistema.RCOA_LA_PRONUNCIAMIENTO;
			
			String nombreDoc = "";
			String descripcionDoc = "";
			Integer idTabladocumento = 0;
			
			if(!esPronunciamientoConformidadLegal) {
				visualizarMemorandoTecnicoJuridico(false);
				nombreDoc = "Memorando juridico.pdf";
				descripcionDoc = "Memorando juridico";
				idTabladocumento = documentoMemorandoTecnicoJuridico.getId();
			} else {
				visualizarPronunciamiento(false);
				nombreDoc = "Pronunciamiento de conformidad legal.pdf";
				descripcionDoc = "Pronunciamiento de conformidad legal";
				idTabladocumento = documentoPronunciamiento.getId();
			}
			
			byte[] contenidoDocumento = (!esPronunciamientoConformidadLegal) ? documentoMemorandoTecnicoJuridico.getArchivoInforme() : documentoPronunciamiento.getArchivoInforme();
			
			List<DocumentoResolucionAmbiental> listaDocumentos = documentoResolucionAmbientalFacade.getDocumentosByIdTablaTipoDocumento(
    				idTabladocumento, tipoDoc);

    		if(!listaDocumentos.isEmpty()) {    			
    			documentoPronunciamientoJuridico = listaDocumentos.get(0);
    			
    			if(documentoPronunciamientoJuridico != null && documentoPronunciamientoJuridico.getId() != null) {
    				documentoPronunciamientoJuridico.setEstado(false);
    				documentoResolucionAmbientalFacade.guardar(documentoPronunciamientoJuridico);
    			}
    		}
    		
    		documentoPronunciamientoJuridico = new DocumentoResolucionAmbiental();
			documentoPronunciamientoJuridico.setNombre(nombreDoc);
			documentoPronunciamientoJuridico.setMime("application/pdf");
			documentoPronunciamientoJuridico.setContenidoDocumento(contenidoDocumento);
			documentoPronunciamientoJuridico.setExtension(".pdf");
			documentoPronunciamientoJuridico.setResolucionAmbiental(resolucionAmbiental);
			documentoPronunciamientoJuridico.setIdTabla(idTabladocumento);
			documentoPronunciamientoJuridico.setNombreTabla(OficioResolucionAmbiental.class.getSimpleName());
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
			
			if(esPronunciamientoConformidadLegal) {
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

			Integer idTabladocumento = (!esPronunciamientoConformidadLegal) ? documentoMemorandoTecnicoJuridico.getId() : documentoPronunciamiento.getId();
			
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoFirmado.setContenidoDocumento(contenidoDocumento);
			documentoFirmado.setNombre(event.getFile().getFileName());
			documentoFirmado.setExtension(".pdf");
			documentoFirmado.setMime("application/pdf");
			documentoFirmado.setResolucionAmbiental(resolucionAmbiental);
			documentoFirmado.setIdTabla(idTabladocumento);
			documentoFirmado.setNombreTabla(OficioResolucionAmbiental.class.getSimpleName());
			documentoFirmado.setTipoDocumento(documentoPronunciamientoJuridico.getTipoDocumento());
			documentoFirmado.setIdProceso(JsfUtil.getCurrentProcessInstanceId());			
			documentoFirmado.setDescripcionTabla(documentoPronunciamientoJuridico.getDescripcionTabla());

			subido = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento del memorando");
		}
	}
	
	private Usuario asignarTecnicoJuridico() {
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
		
		if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
			areaTramite = areaFacade.getAreaSiglas("CGAJ") ;
		} else if (areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT))
			areaTramite = areaTramite.getArea();
		
		String rolPrefijo =  "role.resolucion.tecnico.juridico";
		String rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
			
    	List<Usuario> listaUsuariosCargaLaboral = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());			

		if (listaUsuariosCargaLaboral==null || listaUsuariosCargaLaboral.isEmpty()){
			LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
			return null;
		}

		Usuario nuevoResponsable = null;
		
		// recuperar tecnico de bpm y validar si el usuario existe en el listado anterior		
		Usuario usuarioTecnico = usuarioFacade.buscarUsuario(tecnicoJuridico);
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
	
	private Usuario asignarDirector() {
		String rolPrefijo = "role.resolucion.coordinador.juridico";
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();

		if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
			rolPrefijo = "role.resolucion.pc.director";
		} else if (areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT)) {
			rolPrefijo = "role.esia.cz.coordinador";
			areaTramite = areaTramite.getArea();
		} else if (areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
    		rolPrefijo = "role.resolucion.galapagos.director.calidad";
		} else {
			rolPrefijo = "role.esia.gad.coordinador";
		}	
		
		String rolDirector = Constantes.getRoleAreaName(rolPrefijo);
    	List<Usuario> listaUsuariosResponsables = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolDirector, areaTramite.getAreaName());			

		if (listaUsuariosResponsables==null || listaUsuariosResponsables.isEmpty()){
			LOG.error("No se encontro usuario " + rolDirector + " en " + areaTramite.getAreaName());
			return null;
		}
		
		Usuario usuarioResponsable = listaUsuariosResponsables.get(0);
		return usuarioResponsable;
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
		} else if (areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
    		rolPrefijo = "role.resolucion.galapagos.autoridad";
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
	
	public void enviar() {
		try {
			Map<String, Object> params=new HashMap<>();
			// se envia al tecnico Juridico para correccion
			Usuario nuevoResponsable = asignarTecnicoJuridico();
			if(nuevoResponsable == null) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			} else if (tecnicoJuridico == null || !tecnicoJuridico.equals(nuevoResponsable.getNombre())) {
				params.put("tecnicoJuridico", nuevoResponsable.getNombre());
			}
			
			params.put("existeObservacion", correccionInformeOficio);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {							
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void finalizar(){
    	try {			
			if (documentoPronunciamientoJuridico.getId() != null) {					

				String idAlfresco = documentoPronunciamientoJuridico.getIdAlfresco();

				if (token && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El pronunciamiento no está firmado electrónicamente.");
					return;
				} else if (!token && !subido) {
					JsfUtil.addMessageError("Debe adjuntar el pronunciamiento firmado.");
					return;
				}
				
				if(!token){
					TipoDocumentoSistema tipoDoc = (!esPronunciamientoConformidadLegal) ? TipoDocumentoSistema.EMISION_LICENCIA_MEMORANDO_TECNICO_JURIDICO : TipoDocumentoSistema.RCOA_LA_PRONUNCIAMIENTO;
					documentoResolucionAmbientalFacade.guardarDocumentoAlfrescoResolucionAmbiental(
							resolucionAmbiental.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
							"EMISION_LICENCIA", 0L, documentoFirmado, tipoDoc);
				}
				
				Map<String, Object> parametros = new HashMap<>();
				
				if(!esPronunciamientoConformidadLegal) {
					//si no es pronunciamiento de conformidad se envía al coordinador o DRA para revisión
					Usuario usuarioDirector = asignarDirector();
					if(usuarioDirector == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
						return;
					}
					
					Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
					if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC) 
							|| areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
						parametros.put("directorDRA", usuarioDirector.getNombre());
					} else {
						parametros.put("coordinadorSector", usuarioDirector.getNombre());
					}
				} else {
					//si es pronunciamiento de conformidad se envía a la autoridad para firma
					Usuario usuarioAutoridad = asignarAutoridad();
					if(usuarioAutoridad == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
						return;
					}
					
					parametros.put("autoridadAmbiental", usuarioAutoridad.getNombre());
					
					DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
					String fecha = formatoFechaEmision.format(new Date());
					Object[] parametrosMensaje = new Object[] {documentoPronunciamiento.getCodigoReporte(), fecha, 
							areaFirmaJuridico.getAreaName(), proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental()};
					
					CatalogoGeneralCoa catalogoConsiderando = catalogoCoaFacade.obtenerCatalogoPorCodigo("resolucion.texto.legal");
		            String considerandoLegal = String.format(catalogoConsiderando.getValor(), parametrosMensaje);
		            
		            documentoResolucion.setConsiderandoLegal(considerandoLegal);
		            oficioResolucionAmbientalFacade.guardar(documentoResolucion);
				}
				
				parametros.put("existeObservacion", correccionInformeOficio);
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);

				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getProcessId(), null);
				
				if(!esPronunciamientoConformidadLegal) {
					documentoMemorandoTecnicoJuridico.setEstadoAprobacion(true);
					oficioResolucionAmbientalFacade.guardar(documentoMemorandoTecnicoJuridico);
				}
				
				documentoPronunciamientoJuridico.setIdProceso(bandejaTareasBean.getProcessId());
				documentoResolucionAmbientalFacade.guardar(documentoPronunciamientoJuridico);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
    }
	

}
