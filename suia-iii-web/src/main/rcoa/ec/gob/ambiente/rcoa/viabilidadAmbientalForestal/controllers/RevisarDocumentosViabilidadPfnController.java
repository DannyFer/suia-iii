package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ObservacionesViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionForestal;
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
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class RevisarDocumentosViabilidadPfnController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RevisarDocumentosViabilidadPfnController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{oficioRevisionViabilidadPfnBean}")
	@Getter
	@Setter
	private OficioRevisionViabilidadPfnBean oficioRevisionViabilidadPfnBean;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
    private OrganizacionFacade organizacionFacade;
	
	@EJB
    private ObservacionesViabilidadFacade observacionesViabilidadFacade;
	
	@EJB
    private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private InformeInspeccionForestalFacade informeInspeccionFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
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
	private TipoDocumentoSistema tipoInforme;

	@Getter
	@Setter
	private Boolean documentosRequiereCorrecciones, habilitarEnviar, esProduccion, esInformeViabilidad;
	
	@Getter
	@Setter
	private String urlInforme, nombreInforme, tipoOficio, nombreDocumentoFirmado, urlOficio, nombreOficio, urlAlfresco;
	
	@Getter
	@Setter
	private String nombreTipoInforme, nombreTipoOficio, claseObservaciones, seccionObservaciones;
	
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
			
			numeroRevision = Integer.valueOf((String) oficioRevisionViabilidadPfnBean.getVariables().get("numeroRevisionInformacion"));
			
			viabilidadProyecto = oficioRevisionViabilidadPfnBean.getViabilidadProyecto();
			
			esInformeViabilidad = false;
			
			String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			switch (tarea) {
			case "revisarOficioObservacionesForestal":
				tipoInforme = TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_OBSERVACIONES;
				oficioRevisionViabilidadPfnBean.setTipoOficio(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_OFICIO_OBSERVACIONES);
				seccionObservaciones = "Revision informe y oficio de observaciones PFN";
				claseObservaciones = "revisionInformeOficioObservacionesPfn_" + numeroRevision;
				oficioRevisionViabilidadPfnBean.setNombreTipoOficio("Oficio de observaciones");
				break;
			case "revisarViabilidadForestal":
				esInformeViabilidad = true;
				tipoInforme = TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_VIABILIDAD;
				oficioRevisionViabilidadPfnBean.setTipoOficio(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_MEMORANDO_VIABILIDAD);
				seccionObservaciones = "Revision informe y memorando de viabilidad PFN";
				claseObservaciones = "revisionInformeMemoViabilidadPfn_" + numeroRevision;
				oficioRevisionViabilidadPfnBean.setNombreTipoOficio("Memorando");
				break;
			default:
				break;
			}
			
			if(!buscarInforme()) {
				return;
			}
						
			oficioRevisionViabilidadPfnBean.generarOficio(true);
			
			urlOficio = oficioRevisionViabilidadPfnBean.getOficioRevision().getOficioPath();
			nombreOficio = oficioRevisionViabilidadPfnBean.getOficioRevision().getNombreFichero();
			archivoOficio = oficioRevisionViabilidadPfnBean.getOficioRevision().getArchivoOficio();
			
			panelMostrar = 1;
			activeIndex = 0;
			
			nombreTipoInforme = (esInformeViabilidad) ? "Informe de viabilidad" : "Informe de observaciones";
			nombreTipoOficio = (esInformeViabilidad) ? "Memorando" : "Oficio de observaciones";
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		if(esInformeViabilidad) {
			JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
					"/pages/rcoa/viabilidadAmbientalForestal/revisarDocumentosViabilidad.jsf");
		} else {
			JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
					"/pages/rcoa/viabilidadAmbientalForestal/revisarDocumentosObservacion.jsf");
		}
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
	
	public Boolean buscarInforme() {
		try {
			
			InformeInspeccionForestal informeInspeccion = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			if(esInformeViabilidad && !informeInspeccion.getTipoInforme().equals(InformeInspeccionForestal.viabilidad)) {
				return false;
			}
			
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), tipoInforme.getIdTipoDocumento());
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
			return false;
		}
		
		return true;
	}
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
	
	public void validarExisteObservacionesInformeOficio() {
		try {
			documentosRequiereCorrecciones = false;
			List<ObservacionesViabilidad> observacionesInforme = observacionesViabilidadFacade.listarPorIdClaseNombreClaseNoCorregidas(
							oficioRevisionViabilidadPfnBean.getOficioRevision().getId(), claseObservaciones);
			
			if(observacionesInforme.size() > 0) {
				documentosRequiereCorrecciones = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones del informe oficio.");
		}
	}
	
	public void siguiente() {
        setActiveIndex(1);
        panelMostrar = 2;
	}
	
	public void atras() {
        setActiveIndex(0);
        panelMostrar = 1;
	}
	
	public void tabChange() {
		panelMostrar = activeIndex + 1;
	}
	
	public void guadarDocumentosFirma() { 
		try {
			
			oficioRevisionViabilidadPfnBean.guardarOficio();
			
			oficioRevisionViabilidadPfnBean.generarOficio(false);
			documentoOficio = oficioRevisionViabilidadPfnBean.guardarDocumentoOficioAlfresco();
			
			if(documentoOficio == null || documentoOficio.getId() == null) {
				LOG.error("No se encontro el oficio de pronunciamiento forestal");
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			String documentOffice = documentosFacade.direccionDescarga(documentoOficio);
			urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
		} catch (Exception e) {							
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
		
		RequestContext.getCurrentInstance().update("formDialogs:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public StreamedContent descargar() {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = oficioRevisionViabilidadPfnBean.getOficioRevision().getArchivoOficio();
			String nombreDoc= oficioRevisionViabilidadPfnBean.getOficioRevision().getNombreFichero();
			
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
			documentoFirmado.setIdTipoDocumento(oficioRevisionViabilidadPfnBean.getTipoOficio().getIdTipoDocumento());
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
			Area areaTramite = viabilidadProyecto.getAreaResponsable();
			String tipoRol = "role.va.pfn.tecnico.forestal";
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
			String usrTecnico = (String) oficioRevisionViabilidadPfnBean.getVariables().get("tecnicoResponsable");
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
				parametros.put("tecnicoResponsable", tecnicoResponsable.getNombre()); 
			}
			
			parametros.put("existeObservaciones", documentosRequiereCorrecciones);
			
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
					JsfUtil.addMessageError("El " + nombreTipoOficio + " no está firmado electrónicamente.");
					return;
				}
			} else {
				if(!subido) {
					JsfUtil.addMessageError("Debe adjuntar el " + nombreTipoOficio + " firmado.");
					return;
				}
				
				documentoOficio = documentosFacade.guardarDocumentoProceso(
						oficioRevisionViabilidadPfnBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoFirmado, 2, JsfUtil.getCurrentProcessInstanceId());
				
			}
			
			Usuario tecnicoResponsable = null;
			
			Map<String, Object> parametros = new HashMap<>();
			
			if(esInformeViabilidad) {
				// buscar usuarios por rol y area
				Area areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
				String tipoRol = "role.va.pfn.pc.tecnico.bosques";
				String rolTecnico = Constantes.getRoleAreaName(tipoRol);
				
				List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYAreaAleatorio(rolTecnico, areaTramite);
				if (listaUsuario == null || listaUsuario.size() == 0) {
					LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					return;
				}
				
				// recuperar tecnico de bpm y validar si el usuario existe en el
				// listado anterior
				String usrTecnico = (String) oficioRevisionViabilidadPfnBean.getVariables().get("tecnicoBosques");
				Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
				if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
					if (listaUsuario != null && listaUsuario.size() >= 0
							&& listaUsuario.contains(usuarioTecnico)) {
						tecnicoResponsable = usuarioTecnico;
					}
				}
				
				if (tecnicoResponsable == null) {
					String proceso = Constantes.RCOA_PROCESO_VIABILIDAD + "'' , ''" + Constantes.RCOA_PROCESO_VIABILIDAD_BYPASS + "'' , ''rcoa.EmisionViabilidadAmbientalForestal";
					List<Usuario> listaTecnicos = asignarTareaFacade
							.getCargaLaboralPorUsuariosProceso(listaUsuario,proceso);
					tecnicoResponsable = listaTecnicos.get(0);
					parametros.put("tecnicoBosques", tecnicoResponsable.getNombre()); 
				}
			} 
			
			parametros.put("existeObservaciones", false);
				
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			if(esInformeViabilidad) {
				oficioRevisionViabilidadPfnBean.actualizarOficioFirmado();
				
				enviarNotificacionInforme(tecnicoResponsable);
			} else {
				enviarNotificacionObservacion();
			}

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public void enviarNotificacionObservacion() throws Exception {
		ProyectoLicenciaCoa proyectoLicenciaCoa = oficioRevisionViabilidadPfnBean.getProyectoLicenciaCoa();
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
		
		Object[] parametrosCorreo = new Object[] {nombreOperador, proyectoLicenciaCoa.getNombreProyecto(),
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(), provincia, canton, parroquia, interseca};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionViabilidadPfnPronunciamientoObservado");
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		//guardar el documento de justificacion para adjuntarlo en la notificacion
		byte[] oficioContent = documentosFacade.descargar(documentoOficio.getIdAlfresco());
		
		String nombreOficio = documentoOficio.getNombre();
		File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreOficio);
		FileOutputStream file = new FileOutputStream(fileArchivo);
		file.write(oficioContent);
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
	
	public void enviarNotificacionInforme(Usuario tecnicoResponsable) throws Exception {
		ProyectoLicenciaCoa proyectoLicenciaCoa = oficioRevisionViabilidadPfnBean.getProyectoLicenciaCoa();
		String nombreOperador = tecnicoResponsable.getPersona().getNombre();
		
		List<String> infoUbicaciones = proyectoLicenciaCoaUbicacionFacade.getInfoUbicacionProyecto(proyectoLicenciaCoa, 2);
		String provincia = infoUbicaciones.get(0);
		String canton = infoUbicaciones.get(1);
		String parroquia = infoUbicaciones.get(2);
		
		String interseccionesForestal = interseccionViabilidadCoaFacade.getInterseccionesForestal(proyectoLicenciaCoa.getId(), 1);
		String interseccionesSnap = interseccionViabilidadCoaFacade.getNombresAreasProtegidasSnap(proyectoLicenciaCoa.getId(), 1);
		
		String interseca = (interseccionesSnap.equals("")) ? interseccionesForestal : interseccionesForestal + ", " + interseccionesSnap;
		
		Object[] parametrosCorreo = new Object[] {nombreOperador, oficioRevisionViabilidadPfnBean.getAreaTramite().getAreaName(),
				proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), provincia, canton, parroquia, interseca};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionViabilidadPfnInformeViabilidadAmbiental");
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
			
			Email.sendEmailAdjuntos(tecnicoResponsable, asunto, notificacion, listaArchivos, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
		} else {
			Email.sendEmail(tecnicoResponsable, asunto, notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
		}
		
		if(filesDocumentos!= null && filesDocumentos.size() > 0) {
			for (File item : filesDocumentos) {
				item.delete();
			}
		}
	}
	
}
