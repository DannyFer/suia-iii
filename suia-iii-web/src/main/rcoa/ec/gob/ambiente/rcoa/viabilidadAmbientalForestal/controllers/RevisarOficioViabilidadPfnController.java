package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

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

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ObservacionesViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ObservacionesViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class RevisarOficioViabilidadPfnController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RevisarDocumentosApoyoViabilidadPfnController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{oficioViabilidadPfnBean}")
	@Getter
	@Setter
	private OficioViabilidadPfnBean oficioViabilidadPfnBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
    private ObservacionesViabilidadFacade observacionesViabilidadFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
    private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoOficio, documentoFirmado;

	@Getter
	@Setter
	private Boolean documentosRequiereCorrecciones, habilitarEnviar, esProduccion;
	
	@Getter
	@Setter
	private String nombreInforme, urlOficio, nombreOficio, urlAlfresco;
	
	@Getter
	@Setter
	private byte[] archivoOficio, archivoInforme;
	
	@Getter
	@Setter
	private Integer panelMostrar, activeIndex, numeroRevision;

	@Getter
	@Setter
	private boolean token, descargado, subido;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			
			esProduccion = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
			
			verificaToken();
			
			viabilidadProyecto = oficioViabilidadPfnBean.getViabilidadProyecto();
						
			generarOficio(true);
			
			numeroRevision = Integer.valueOf((String) oficioViabilidadPfnBean.getVariables().get("numeroRevisionInformacion"));
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalForestal/revisarOficioViabilidad.jsf");
	}
	
	public boolean verificaToken() {
		if(esProduccion) {
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
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
	
	public void validarExisteObservacionesOficio() {
		try {
			documentosRequiereCorrecciones = false;
			List<ObservacionesViabilidad> observacionesInforme = observacionesViabilidadFacade.listarPorIdClaseNombreClaseNoCorregidas(
							oficioViabilidadPfnBean.getOficioPronunciamiento().getId(), "revisionOficioViabilidadPfn_" + numeroRevision);
			
			if(observacionesInforme.size() > 0) {
				documentosRequiereCorrecciones = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones del informe oficio.");
		}
	}
	
	public void generarOficio(Boolean marcaAgua) {
		oficioViabilidadPfnBean.generarOficio(marcaAgua);
		
		urlOficio = oficioViabilidadPfnBean.getOficioPronunciamiento().getOficioPath();
		nombreOficio = oficioViabilidadPfnBean.getOficioPronunciamiento().getNombreFichero();
		archivoOficio = oficioViabilidadPfnBean.getOficioPronunciamiento().getArchivoOficio();
	}
	
	public void guardarDocumentosFirma() { 
		try {
			generarOficio(false);
			documentoOficio = oficioViabilidadPfnBean.guardarDocumentoOficioAlfresco();
			
			if(documentoOficio == null || documentoOficio.getId() == null) {
				LOG.error("No se encontro el oficio de pronunciamiento forestal");
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			String documentOffice = documentosFacade.direccionDescarga(documentoOficio);
			urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
		} catch (Exception e) {	
			e.printStackTrace();						
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
		
		RequestContext.getCurrentInstance().update("formDialogs:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public StreamedContent descargar() {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = oficioViabilidadPfnBean.getOficioPronunciamiento().getArchivoOficio();
			String nombreDoc= oficioViabilidadPfnBean.getOficioPronunciamiento().getNombreFichero();
			
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
			documentoFirmado = new DocumentoViabilidad();
			documentoFirmado.setNombre(event.getFile().getFileName());
			documentoFirmado.setContenidoDocumento(event.getFile().getContents());
			documentoFirmado.setMime("application/pdf");
			documentoFirmado.setIdTipoDocumento(oficioViabilidadPfnBean.getTipoOficio().getIdTipoDocumento());
			documentoFirmado.setIdViabilidad(viabilidadProyecto.getId());

			subido = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento");
		}
	}
	
	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
			
			// buscar usuarios por rol y area
			Area areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
			String tipoRol = "role.va.pfn.pc.tecnico.bosques";
			String rolTecnico = Constantes.getRoleAreaName(tipoRol);

			List<Usuario> listaTecnicos = asignarTareaFacade
					.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());			

			if(listaTecnicos==null || listaTecnicos.isEmpty()){
				LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			// recuperar tecnico de bpm y validar si el usuario existe en el listado anterior
			String usrTecnico = (String) oficioViabilidadPfnBean.getVariables().get("tecnicoBosques");
			Usuario tecnicoResponsable = null;
			Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
			if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
				if (listaTecnicos != null && listaTecnicos.size() >= 0
						&& listaTecnicos.contains(usuarioTecnico)) {
					tecnicoResponsable = usuarioTecnico;
				}
			}
			
			if (tecnicoResponsable == null) {
				tecnicoResponsable = listaTecnicos.get(0);
				parametros.put("tecnicoBosques", tecnicoResponsable.getNombre()); 
			}
			
			parametros.put("existeObservacionesOficio", documentosRequiereCorrecciones);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public void finalizar() {
		try {
			if (token) {
				String idAlfrescoOficio = documentoOficio.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoOficio)) {
					JsfUtil.addMessageError("El Oficio no está firmado electrónicamente.");
					return;
				}
			} else {
				if(!subido) {
					JsfUtil.addMessageError("Debe adjuntar el Oficio firmado.");
					return;
				}
				
				documentoOficio = documentosFacade.guardarDocumentoProceso(
						oficioViabilidadPfnBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoFirmado, 2, JsfUtil.getCurrentProcessInstanceId());
				
			}
			
			Boolean viabilidadAmbientalFavorable = oficioViabilidadPfnBean.getOficioPronunciamiento().getEsPronunciamientoFavorable();
			
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put("existeObservacionesOficio", false);
			parametros.put("esViabilidadForestalFavorable", viabilidadAmbientalFavorable);
			
			Usuario usuarioAutoridadArchivo = null;
			if(!viabilidadAmbientalFavorable) {
				//busca autoridad archivo
				usuarioAutoridadArchivo = asignarAutoridad();
				if(usuarioAutoridadArchivo == null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
					return;
				}
				
				parametros.put("autoridadFirmaArchivo", usuarioAutoridadArchivo.getNombre());
			}
			
			viabilidadProyecto.setViabilidadCompletada(true);
			viabilidadProyecto.setEsPronunciamientoFavorable(viabilidadAmbientalFavorable);
			viabilidadCoaFacade.guardar(viabilidadProyecto);
			
			//establecer tipo viabilidad en el proyectoCoa principal
			ViabilidadCoa viabilidadSnap = null;
			ProcessInstanceLog procesoViabilidadActual = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			Map<String, Object> variablesRipoa = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), procesoViabilidadActual.getParentProcessInstanceId());
			
			Boolean intersecaSnap = Boolean.parseBoolean(variablesRipoa.get("u_intersecaSnap").toString()); //recuperar si interseca con snap
			if(intersecaSnap) {
				//si interseca con SNAP si la viabilidad está completada se verifica el tipo de pronunciamiento
				viabilidadSnap = viabilidadCoaFacade.getViabilidadSnapPorProyecto(oficioViabilidadPfnBean.getProyectoLicenciaCoa().getId());
				if(viabilidadSnap != null && viabilidadSnap.getId() != null) {					
					if(viabilidadSnap.getViabilidadCompletada()) {
						Boolean pronunciamientoFinal = (viabilidadAmbientalFavorable && viabilidadSnap.getEsPronunciamientoFavorable()) ? true : false;
						oficioViabilidadPfnBean.getProyectoLicenciaCoa().setTieneViabilidadFavorable(pronunciamientoFinal);
						proyectoLicenciaCoaFacade.guardar(oficioViabilidadPfnBean.getProyectoLicenciaCoa());
					} 
				} 
			} else {
				//sino interseca con SNAP solo tiene el pronunciamiento forestal
				oficioViabilidadPfnBean.getProyectoLicenciaCoa().setTieneViabilidadFavorable(viabilidadAmbientalFavorable);
				proyectoLicenciaCoaFacade.guardar(oficioViabilidadPfnBean.getProyectoLicenciaCoa());
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			oficioViabilidadPfnBean.actualizarOficioFirmado();
			
			documentoOficio.setIdProceso(JsfUtil.getCurrentProcessInstanceId());
			documentosFacade.guardar(documentoOficio);
			
			enviarNotificacionOperador(viabilidadAmbientalFavorable);

			if(!viabilidadAmbientalFavorable) {
				Boolean enviarNotificacion = true;
				//recuperar variables de preliminar y validar si se ejecutaron los 2 procesos
				//si hay SNAP buscar la viabilidad y verificar si termino y el tipo de pronunciamiento
				if(intersecaSnap) {
					if(viabilidadSnap != null && viabilidadSnap.getId() != null) {
						if(viabilidadSnap.getViabilidadCompletada()) {
							enviarNotificacion = true;
						} else {
							enviarNotificacion = false;
							System.out.println("No se envia notificacion de archivo. Proyecto " + oficioViabilidadPfnBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental() + " no finaliza viabilidad SNAP");
						}
					} else {
						enviarNotificacion = false;
						System.out.println("No se envia notificacion de archivo. Proyecto " + oficioViabilidadPfnBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental() + " aún no inicia viabilidad SNAP");
					}
				}
				
				if(enviarNotificacion) {
					notificarNoFavorableAutoridad(usuarioAutoridadArchivo);
				}
				
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public Usuario asignarAutoridad() throws ServiceException {
		String rolPrefijo = "role.va.pfn.cz.autoridad";
		String rolAutoridad = "";
    	Area areaTramite = oficioViabilidadPfnBean.getProyectoLicenciaCoa().getAreaResponsable();
    	
    	List<Usuario> listaUsuariosResponsables = new ArrayList<Usuario>();
		
		if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
			rolPrefijo = "role.va.pfn.pc.autoridad";
			rolAutoridad = Constantes.getRoleAreaName(rolPrefijo);
			
			Usuario autoridad = areaFacade.getUsuarioPorRolArea(rolPrefijo,areaTramite);
			if(autoridad != null && autoridad.getId() != null) {
				listaUsuariosResponsables.add(autoridad);
			}
		} else {
			if (areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
	    		rolPrefijo = "role.va.pfn.ga.autoridad";
			} else if (areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT)) {
				areaTramite = areaTramite.getArea();
			}
			
			rolAutoridad = Constantes.getRoleAreaName(rolPrefijo);
	    	listaUsuariosResponsables = usuarioFacade.buscarUsuariosPorRolYAreaEspecifica(rolAutoridad, areaTramite);
		}		

		if (listaUsuariosResponsables == null || listaUsuariosResponsables.isEmpty()) {
			System.out.println("No se encontro usuario " + rolAutoridad + " en " + areaTramite.getAreaName());
			return null;
		}

		Usuario usuarioResponsable = listaUsuariosResponsables.get(0);
		return usuarioResponsable;
	}
	
	public void notificarNoFavorableAutoridad(Usuario usuarioAutoridad) throws Exception {
		String nombreOperador = "";
		Usuario usuarioOperador = oficioViabilidadPfnBean.getProyectoLicenciaCoa().getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		String notificacionAutoridad = "bodyNotificacionViabilidadesNoFavorableAutoridad" ;
		
		Object[] parametrosCorreo = new Object[] {usuarioAutoridad.getPersona().getNombre(), oficioViabilidadPfnBean.getProyectoLicenciaCoa().getNombreProyecto(),
				oficioViabilidadPfnBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), nombreOperador};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(notificacionAutoridad);
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Email.sendEmail(usuarioAutoridad, mensajeNotificacion.getAsunto(), notificacion, oficioViabilidadPfnBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());

	}
	
	public void enviarNotificacionOperador(Boolean viabilidadAmbientalFavorable) throws Exception {
		ProyectoLicenciaCoa proyectoLicenciaCoa = oficioViabilidadPfnBean.getProyectoLicenciaCoa();
		String nombreOperador = "";
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		List<String> infoUbicaciones = proyectoLicenciaCoaUbicacionFacade.getInfoUbicacionProyecto(proyectoLicenciaCoa, 2);
		String provincia = infoUbicaciones.get(0);
		String canton = infoUbicaciones.get(1);
		String parroquia = infoUbicaciones.get(2);
		
		String interseccionesForestal = interseccionViabilidadCoaFacade.getInterseccionesForestal(proyectoLicenciaCoa.getId(), 1);
		String interseccionesSnap = interseccionViabilidadCoaFacade.getNombresAreasProtegidasSnap(proyectoLicenciaCoa.getId(), 1);
		
		String interseca = (interseccionesSnap.equals("")) ? interseccionesForestal : interseccionesForestal + ", " + interseccionesSnap;
		
		String pronunciamiento = (viabilidadAmbientalFavorable) ? "Favorable" : "No Favorable";
		
		Object[] parametrosCorreo = new Object[] {nombreOperador, proyectoLicenciaCoa.getNombreProyecto(),
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(), provincia, canton, parroquia, interseca, pronunciamiento};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionViabilidadPfnPronunciamientoViabilidad");
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Object[] parametrosAsunto = new Object[] {proyectoLicenciaCoa.getCodigoUnicoAmbiental()};
		String asunto = String.format(mensajeNotificacion.getAsunto(), parametrosAsunto);
		
		//guardar el documento de justificacion para adjuntarlo en la notificacion
		byte[] oficioContent = documentosFacade.descargar(documentoOficio.getIdAlfresco());
		
		String nombreOficio = documentoOficio.getNombre();
		File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreOficio);
		FileOutputStream file = new FileOutputStream(fileArchivo);
		file.write(oficioContent);
		file.close();
		
		List<File> filesDocumentos = new ArrayList<>();
		filesDocumentos.add(fileArchivo);
		
		buscarInforme();
		double tamanioInforme = 0;
		if(archivoInforme != null) {
			File fileArchivoInforme = new File(System.getProperty("java.io.tmpdir") + "/" + nombreInforme);
			FileOutputStream fileInforme = new FileOutputStream(fileArchivoInforme);
			fileInforme.write(archivoInforme);
			fileInforme.close();
			
			filesDocumentos.add(fileArchivoInforme);
			
			tamanioInforme = fileArchivoInforme.length() / (1024 * 1024);
		}
		
		double tamanioOficio = fileArchivo.length() / (1024 * 1024);
		
		if(tamanioOficio <= 20 && tamanioInforme <= 20) {
			List<String> listaArchivos = new ArrayList<>();
			listaArchivos.add(nombreOficio);
			
			String nombreDocInforme = nombreInforme.replace("/", "-"); 
			listaArchivos.add(nombreDocInforme);
			
			Email.sendEmailAdjuntos(usuarioOperador, asunto, notificacion, listaArchivos, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
		} else {
			Email.sendEmail(usuarioOperador, asunto, notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
		}
		
		if(filesDocumentos!= null && filesDocumentos.size() > 0) {
			for (File item : filesDocumentos) {
				item.delete();
			}
		}
	}
	
	public void buscarInforme() throws IOException {
		List<DocumentoViabilidad> documentos = documentosFacade
				.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_VIABILIDAD.getIdTipoDocumento());
		if(documentos.size() > 0) {
			DocumentoViabilidad documentoInforme = documentos.get(0);
			
			File fileDoc = documentosFacade.getDocumentoPorIdAlfresco(documentoInforme.getIdAlfresco());
			
			Path path = Paths.get(fileDoc.getAbsolutePath());
			byte[] contenido = Files.readAllBytes(path);
			String reporteHtmlfinal = documentoInforme.getNombre().replace("/", "-");
			File fileInforme = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(fileInforme);
			file.write(contenido);
			file.close();
			
			nombreInforme = documentoInforme.getNombre();
			archivoInforme = contenido;
		}
	}
	
}
