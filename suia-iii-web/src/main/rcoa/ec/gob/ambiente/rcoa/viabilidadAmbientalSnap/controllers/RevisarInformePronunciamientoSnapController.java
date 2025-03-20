package ec.gob.ambiente.rcoa.viabilidadAmbientalSnap.controllers;

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

import org.jbpm.process.audit.ProcessInstanceLog;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ObservacionesViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.UtilViabilidadSnap;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ObservacionesViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
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

@ManagedBean
@ViewScoped
public class RevisarInformePronunciamientoSnapController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{generarOficioViabilidadSnapBean}")
	@Getter
	@Setter
	private GenerarOficioViabilidadSnapBean generarOficioViabilidadSnapBean;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
	private ObservacionesViabilidadFacade observacionesViabilidadFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private InformeInspeccionBiodiversidad informeInspeccion;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoOficio;

	@Getter
	@Setter
	private Boolean esReporteAprobacion, informeRequiereCorrecciones, oficioRequiereCorrecciones, esAutoridad;
	
	@Getter
	@Setter
	private String urlOficio, nombreOficio, tipoPronunciamiento, urlInforme, nombreInforme, urlAlfresco;
	
	@Getter
	@Setter
	private byte[] archivoOficio, archivoInforme;

	@Getter
	@Setter
	private boolean token, subido, documentoDescargado, esProduccion;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		
		try {
			
			viabilidadProyecto = generarOficioViabilidadSnapBean.getViabilidadProyecto();
			informeInspeccion = generarOficioViabilidadSnapBean.getInformeInspeccion();
			
			esReporteAprobacion = informeInspeccion.getEsPronunciamientoFavorable();
			
			esProduccion = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
			
			esAutoridad = false;
			informeRequiereCorrecciones = false;
			oficioRequiereCorrecciones = false;
			token = true;
			
			String tarea = null;
			try {
				tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (tarea.toUpperCase().contains("FIRMAR")) {
				esAutoridad = true;
				verificaToken();
			}
			
			tipoOficio = informeInspeccion.getEsPronunciamientoFavorable() ? TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_FAVORABLE : TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_NO_FAVORABLE;
			tipoPronunciamiento =  esReporteAprobacion ? "Pronunciamiento Aprobación" : "Pronunciamiento Observación";

			generarOficioViabilidadSnapBean.generarOficio(true);
			
			urlOficio = generarOficioViabilidadSnapBean.getOficioPronunciamiento().getOficioPath();
			nombreOficio = generarOficioViabilidadSnapBean.getOficioPronunciamiento().getNombreOficio();
			archivoOficio = generarOficioViabilidadSnapBean.getOficioPronunciamiento().getArchivoOficio();
			
			buscarInformeInspeccion();
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al iniciar la tarea.");
		}
	}
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken()) {
			token = true;
		}
		
		if(esProduccion) {
			token = true;
		}
		
		return token;
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalSnap/revisarInformeOficio.jsf");
	}
	
	public void buscarInformeInspeccion() {
		try {
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_II_INFORME_TECNICO.getIdTipoDocumento());
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
				
				urlInforme = JsfUtil.devolverContexto("/reportesHtml/" + documentoInforme.getNombre());
				nombreInforme = documentoInforme.getNombre();
				archivoInforme = contenido;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
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
	
	public StreamedContent descargarInforme() throws IOException {
		DefaultStreamedContent content = null;
		try {
			DocumentoViabilidad documentoDescarga = null;
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_II_INFORME_TECNICO.getIdTipoDocumento());
			if (documentos.size() > 0) {
				documentoDescarga = documentos.get(0);
			}	
			
			byte[] documentoContent = null;

			if (documentoDescarga != null && documentoDescarga.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(
						documentoDescarga.getIdAlfresco(),
						documentoDescarga.getFechaCreacion());
			} 

			if (documentoDescarga != null
					&& documentoDescarga.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDescarga.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void validarExisteObservacionesInforme() {
		try {
			informeRequiereCorrecciones = true;
			
			List<ObservacionesViabilidad> observacionesOficio = observacionesViabilidadFacade.listarPorIdClaseNombreClaseNoCorregidas(
					viabilidadProyecto.getId(), "revisionFinalInformeSnap");
			
			if(observacionesOficio.size() == 0) {
				informeRequiereCorrecciones = false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones del oficio SNAP.");
		}
	}
	
	public void validarExisteObservacionesOficio() {
		try {
			oficioRequiereCorrecciones = true;
			
			List<ObservacionesViabilidad> observacionesOficio = observacionesViabilidadFacade.listarPorIdClaseNombreClaseNoCorregidas(
					viabilidadProyecto.getId(), "revisionFinalOficioSnap");
			
			if(observacionesOficio.size() == 0) {
				oficioRequiereCorrecciones = false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones del oficio SNAP.");
		}
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
	
	//envia a correcciones o firma del director
	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put("existeObservacionesInforme", informeRequiereCorrecciones);
			parametros.put("existeObservacionesOficio", oficioRequiereCorrecciones);
			
			Boolean documentosCorrectos = false;
			Usuario tecnicoResponsable = null;
			
			if(!informeRequiereCorrecciones && !oficioRequiereCorrecciones) {
				String autoridadBpm =  (String) generarOficioViabilidadSnapBean.getVariables().get("autoridadAmbiental");
				if (autoridadBpm == null || !autoridadBpm.equals(generarOficioViabilidadSnapBean.getUsuarioAutoridad().getNombre())) {
					parametros.put("autoridadAmbiental", generarOficioViabilidadSnapBean.getUsuarioAutoridad().getNombre());
				}
				
				documentosCorrectos = true;
			} else {
				String tipoUsuario = (viabilidadProyecto.getEsAdministracionMae()) ? "responsableAreaProtegida" : "responsableAreasPC";
				String jefePrincipalBpm = generarOficioViabilidadSnapBean.getVariables().get(tipoUsuario).toString();
				
				AreasSnapProvincia areaSnap = UtilViabilidadSnap.getInfoAreaSnap(viabilidadProyecto.getAreaSnap().getCodigoSnap(), viabilidadProyecto.getAreaSnap().getZona());
				tecnicoResponsable = (areaSnap != null) ? areaSnap.getTecnicoResponsable() : null;
				
				if(areaSnap == null || tecnicoResponsable == null || !tecnicoResponsable.getEstado()) {
					System.out.println("No se encontro usuario responsable para el área " + areaSnap.getNombreAreaProtegida());
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
					return;
				}
				
				if(!jefePrincipalBpm.equals(tecnicoResponsable.getNombre())) {
					parametros.put(tipoUsuario, tecnicoResponsable.getNombre());
				}
				
				Integer numeroRevision = Integer.valueOf((String) generarOficioViabilidadSnapBean.getVariables().get("numeroRevision"));
				numeroRevision = numeroRevision + 1;
				parametros.put("numeroRevision", numeroRevision);
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			if(documentosCorrectos) {
				if(!esAutoridad) {
					notificarRevision(generarOficioViabilidadSnapBean.getUsuarioAutoridad());
				}
			} else {
				notificarObservaciones(tecnicoResponsable);
			}

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void notificarRevision(Usuario tecnicoResponsable) throws ServiceException {
		Object[] parametrosCorreo = new Object[] {tecnicoResponsable.getPersona().getNombre(),
				generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getNombreProyecto(), 
				generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental()};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionViabilidadSnapRevisarInformeAutoridad");
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Email.sendEmail(tecnicoResponsable, mensajeNotificacion.getAsunto(), notificacion, generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
	}
	
	public void notificarObservaciones(Usuario tecnicoResponsable) throws ServiceException {
		Object[] parametrosCorreo = new Object[] {tecnicoResponsable.getPersona().getNombre(),
				generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getNombreProyecto(), 
				generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental()};
		
		String nombreMsj = (esAutoridad) ? "bodyNotificacionViabilidadSnapSubsanarObservacionesTecnicoAutoridad" : "bodyNotificacionViabilidadSnapSubsanarObservacionesTecnico";
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(nombreMsj);
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Email.sendEmail(tecnicoResponsable, mensajeNotificacion.getAsunto(), notificacion, generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			generarOficioViabilidadSnapBean.subirOficio();
			
			byte[] documentoContent = generarOficioViabilidadSnapBean.getOficioPronunciamiento().getArchivoOficio();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(generarOficioViabilidadSnapBean.getOficioPronunciamiento().getNombreOficio());
			} else
				JsfUtil.addMessageError("Error al generar el archivo");
			
			documentoDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void guardarDocumentos() {
		try {
				generarOficioViabilidadSnapBean.subirOficio();
			
				if(generarOficioViabilidadSnapBean.getDocumentoOficioPronunciamiento() != null) {
				String documentOffice = documentosFacade.direccionDescarga(generarOficioViabilidadSnapBean.getDocumentoOficioPronunciamiento());
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		
		RequestContext.getCurrentInstance().update("formDialogs");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public void completarTarea() {
		try {
			
			DocumentoViabilidad documentoOficio = generarOficioViabilidadSnapBean.getDocumentoOficioPronunciamiento();
			
			String idAlfresco = documentoOficio.getIdAlfresco();
			if (esProduccion && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
				JsfUtil.addMessageError("El oficio no está firmado electrónicamente.");
				return;
			}
			
			Boolean viabilidadAmbientalFavorable = generarOficioViabilidadSnapBean.getInformeInspeccion().getEsPronunciamientoFavorable();
			
			Usuario usuarioAutoridadArchivo = null;
			if(!viabilidadAmbientalFavorable) {
				usuarioAutoridadArchivo = asignarAutoridad();
				if(usuarioAutoridadArchivo == null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
					return;
				}
			}			
			
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put("existeObservacionesInforme", informeRequiereCorrecciones);
			parametros.put("existeObservacionesOficio", oficioRequiereCorrecciones);
			parametros.put("esViabilidadSnapFavorable", viabilidadAmbientalFavorable);
			
			if(!viabilidadAmbientalFavorable) {
				parametros.put("autoridadFirmaArchivo", usuarioAutoridadArchivo.getNombre());
			}
			
			viabilidadProyecto.setViabilidadCompletada(true);
			viabilidadProyecto.setEsPronunciamientoFavorable(viabilidadAmbientalFavorable);
			viabilidadCoaFacade.guardar(viabilidadProyecto);
			
			//establecer tipo viabilidad en el proyectoCoa principal
			ViabilidadCoa viabilidadForestal = null;
			ProcessInstanceLog procesoViabilidadActual = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			Map<String, Object> variablesRipoa = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), procesoViabilidadActual.getParentProcessInstanceId());
			
			Boolean intersecaForestal = Boolean.parseBoolean(variablesRipoa.get("u_intersecaForestal").toString());
			if(intersecaForestal) {
				//si interseca con PFN y si la viabilidad está completada se verifica el tipo de pronunciamiento
				viabilidadForestal = viabilidadCoaFacade.getViabilidadForestalPorProyecto(generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getId());
				if(viabilidadForestal != null && viabilidadForestal.getId() != null) {					
					if(viabilidadForestal.getViabilidadCompletada()) {
						Boolean pronunciamientoFinal = (viabilidadAmbientalFavorable && viabilidadForestal.getEsPronunciamientoFavorable()) ? true : false;
						generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().setTieneViabilidadFavorable(pronunciamientoFinal);
						proyectoLicenciaCoaFacade.guardar(generarOficioViabilidadSnapBean.getProyectoLicenciaCoa());
					} 
				} 
			} else {
				//sino interseca con PFN solo tiene el pronunciamiento SNAP
				generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().setTieneViabilidadFavorable(viabilidadAmbientalFavorable);
				proyectoLicenciaCoaFacade.guardar(generarOficioViabilidadSnapBean.getProyectoLicenciaCoa());
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			notificarPronunciamientos();
			
			if(!viabilidadAmbientalFavorable) {
				Boolean enviarNotificacion = true;
				//recuperar variables de preliminar y validar si se ejecutaron los 2 procesos
				//si hay forestal buscar la viabilidad y verificar si termino y el tipo de pronunciamiento
				if(intersecaForestal) {
					if(viabilidadForestal != null && viabilidadForestal.getId() != null) {
						if(viabilidadForestal.getViabilidadCompletada()) {
							enviarNotificacion = true;
						} else {
							enviarNotificacion = false;
							System.out.println("No se envia notificacion de archivo. Proyecto " + generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental() + " no finaliza viabilidad forestal");
						}
					} else {
						enviarNotificacion = false;
						System.out.println("No se envia notificacion de archivo. Proyecto " + generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental() + " aún no inicia  viabilidad forestal");
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
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public Usuario asignarAutoridad() throws ServiceException {
		String rolPrefijo = "role.va.snap.cz.autoridad";
		String rolAutoridad = "";
    	Area areaTramite = generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getAreaResponsable();
    	
    	List<Usuario> listaUsuariosResponsables = new ArrayList<Usuario>();
		
		if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
			rolPrefijo = "role.va.snap.pc.autoridad";
			rolAutoridad = Constantes.getRoleAreaName(rolPrefijo);
			
			Usuario autoridad = areaFacade.getUsuarioPorRolArea(rolPrefijo,areaTramite);
			if(autoridad != null && autoridad.getId() != null) {
				listaUsuariosResponsables.add(autoridad);
			}
		} else {
			if (areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
	    		rolPrefijo = "role.va.snap.ga.autoridad";
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
	
	public void notificarPronunciamientos() throws Exception {
		ProyectoLicenciaCoa proyectoLicenciaCoa = generarOficioViabilidadSnapBean.getProyectoLicenciaCoa();
		String nombreOperador = "";
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		Object[] parametrosCorreo = new Object[] {nombreOperador, proyectoLicenciaCoa.getNombreProyecto(),
				proyectoLicenciaCoa.getCodigoUnicoAmbiental()};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionViabilidadSnapPronunciamientoEmitido");
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		//guardar el documento de justificacion para adjuntarlo en la notificacion
		String nombreOficio = generarOficioViabilidadSnapBean.getOficioPronunciamiento().getNombreFichero().replace("/", "-");
		File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreOficio);
		FileOutputStream file = new FileOutputStream(fileArchivo);
		file.write(generarOficioViabilidadSnapBean.getOficioPronunciamiento().getArchivoOficio());
		file.close();
		
		File fileArchivoInforme = new File(System.getProperty("java.io.tmpdir") + "/" + nombreInforme);
		FileOutputStream fileInforme = new FileOutputStream(fileArchivoInforme);
		fileInforme.write(archivoInforme);
		fileInforme.close();
		
		List<File> filesDocumentos = new ArrayList<>();
		filesDocumentos.add(fileArchivo);
		filesDocumentos.add(fileArchivoInforme);
		
		double tamanioOficio = fileArchivo.length() / (1024 * 1024);
		double tamanioInforme = fileArchivoInforme.length() / (1024 * 1024);
		
		if(tamanioOficio <= 20 && tamanioInforme <= 20) {
			List<String> listaArchivos = new ArrayList<>();
			listaArchivos.add(nombreOficio);
			
			String nombreDocInforme = nombreInforme.replace("/", "-"); 
			listaArchivos.add(nombreDocInforme);
			
			Email.sendEmailAdjuntos(usuarioOperador, mensajeNotificacion.getAsunto(), notificacion, listaArchivos, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
		} else {
			Email.sendEmail(usuarioOperador, mensajeNotificacion.getAsunto(), notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
		}
		
		if(filesDocumentos!= null && filesDocumentos.size() > 0) {
			for (File item : filesDocumentos) {
				item.delete();
			}
		}
	}
	
	public void notificarNoFavorableAutoridad(Usuario usuarioAutoridad) throws Exception {
		String nombreOperador = "";
		Usuario usuarioOperador = generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		String notificacionAutoridad = "bodyNotificacionViabilidadesNoFavorableAutoridad" ;
		
		Object[] parametrosCorreo = new Object[] {usuarioAutoridad.getPersona().getNombre(), generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getNombreProyecto(),
				generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), nombreOperador};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(notificacionAutoridad);
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Email.sendEmail(usuarioAutoridad, mensajeNotificacion.getAsunto(), notificacion, generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());

	}
	
}
