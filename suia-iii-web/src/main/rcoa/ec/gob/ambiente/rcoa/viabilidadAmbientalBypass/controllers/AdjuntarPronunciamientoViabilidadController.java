package ec.gob.ambiente.rcoa.viabilidadAmbientalBypass.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PronunciamientoBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PronunciamientoForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ProyectoTipoViabilidadCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AdjuntarPronunciamientoViabilidadController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(AdjuntarPronunciamientoViabilidadController.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
	@EJB
    private DocumentosCoaFacade documentoCoaFacade;
	
	@EJB
    private ProcesoFacade procesoFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private PronunciamientoBiodiversidadFacade pronunciamientoBiodiversidadFacade;
	
	@EJB
	private PronunciamientoForestalFacade pronunciamientoForestalFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private ProyectoTipoViabilidadCoa proyectoTipoViabilidadCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoOficio, documentoInforme;

	@Getter
	@Setter
	private Boolean proyectoRetroceso, esForestal, oficioDescargado, mapaDescargado, coordenadasDescargadas, guiaDescargada;
	
	@Getter
	@Setter
	private Integer idTipoDocumentoInforme, idTipoDocumentoOficio;
	
	@Getter
	@Setter
	private String numeroOficioArchivo, recomendaciones;
	
	@Getter
	@Setter
	private Date fechaOficioArchivo;
	
	@Getter
	@Setter
	Integer idProyecto;
	
	private Map<String, Object> variables;


	@PostConstruct
	private void iniciar() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			idProyecto = Integer.valueOf(variables.get("idProyecto").toString()); 
			
			esForestal = false;
			String nombreTarea = viabilidadCoaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			if(nombreTarea.equals("adjuntarOficioForestal")) {
				esForestal = true;
			}
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			proyectoTipoViabilidadCoa = viabilidadCoaFacade.getTipoViabilidadPorProyecto(idProyecto, !esForestal);
			
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadPorTipoProyecto(proyectoTipoViabilidadCoa.getId());
			
			idTipoDocumentoInforme = (esForestal) ? TipoDocumentoSistema.RCOA_VIABILIDAD_BYPASS_INFORME_FORESTAL.getIdTipoDocumento() : TipoDocumentoSistema.RCOA_VIABILIDAD_BYPASS_INFORME_SNAP.getIdTipoDocumento();
			idTipoDocumentoOficio = (esForestal) ? TipoDocumentoSistema.RCOA_VIABILIDAD_BYPASS_OFICIO_FORESTAL.getIdTipoDocumento() : TipoDocumentoSistema.RCOA_VIABILIDAD_BYPASS_OFICIO_SNAP.getIdTipoDocumento();
	
			List<DocumentoViabilidad> documentosInforme = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), idTipoDocumentoInforme);
			if (documentosInforme.size() > 0) {
				documentoInforme = documentosInforme.get(0);
			}
			
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), idTipoDocumentoOficio);
			if (documentos.size() > 0) {
				documentoOficio = documentos.get(0);
			}
			
			proyectoRetroceso = false;
			oficioDescargado = false;
			mapaDescargado = false;
			coordenadasDescargadas = false;
			guiaDescargada = false;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos para revisión.");
		}
	}
	
	public void validarTareaBpm() {
		String url = (esForestal) ? "/pages/rcoa/viabilidadAmbientalBypass/adjuntarForestal.jsf" : "/pages/rcoa/viabilidadAmbientalBypass/adjuntarAreasProtegidas.jsf";
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(), url);
	}
	
	public StreamedContent descargar(Integer tipoDocumento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			String idDocAlfresco = null;
			String nombreDocumento = null;
			
			switch (tipoDocumento) {
			case 1:				
				CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				
				List<DocumentosCOA> certificadoInter = documentoCoaFacade.documentoXTablaIdXIdDoc(oficioCI.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO, "CertificadoInterseccionOficioCoa");
				if (certificadoInter.size() > 0) {
					idDocAlfresco = certificadoInter.get(0).getIdAlfresco();
					nombreDocumento = certificadoInter.get(0).getNombreDocumento();
				}
				
				break;
			case 2:
				List<DocumentosCOA> docsMapas = documentoCoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA, "ProyectoLicenciaCoa");
				if (docsMapas.size() > 0) {
					idDocAlfresco = docsMapas.get(0).getIdAlfresco();
					nombreDocumento = docsMapas.get(0).getNombreDocumento();
				}
				break;
			case 3:
				List<DocumentosCOA> docsCoordenadas = documentoCoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA, "ProyectoLicenciaCoa");
				if (docsCoordenadas.size() > 0) {
					idDocAlfresco = docsCoordenadas.get(0).getIdAlfresco();
					nombreDocumento = docsCoordenadas.get(0).getNombreDocumento();
				}
				break;
			case 4:				
				Integer idTipoDocumento = (esForestal) ? TipoDocumentoSistema.RCOA_VIABILIDAD_GUIA_OPERADOR_FORESTAL.getIdTipoDocumento() : TipoDocumentoSistema.RCOA_VIABILIDAD_GUIA_OPERADOR_SNAP.getIdTipoDocumento();
				List<DocumentoViabilidad> listaDocumentos = documentosFacade
						.getDocumentoPorTipoViabilidadTramite(
								proyectoTipoViabilidadCoa.getId(), idTipoDocumento);
				
				if (listaDocumentos.size() > 0) {
					idDocAlfresco = listaDocumentos.get(0).getIdAlfresco();
					nombreDocumento = listaDocumentos.get(0).getNombre();
				}	
				break;
			default:
				break;
			}
			
			
			byte[] documentoContent = null;
			if (idDocAlfresco != null) {
				documentoContent = documentosFacade.descargar(idDocAlfresco);
			} 

			if (nombreDocumento != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(nombreDocumento);
				
				switch (tipoDocumento) {
				case 1:				
					oficioDescargado = true;
					break;
				case 2:
					mapaDescargado = true;
					break;
				case 3:
					coordenadasDescargadas = true;
					break;
				case 4:				
					guiaDescargada = true;
					break;
				default:
					break;
				}
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void validateDatosIngreso(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if(!oficioDescargado) {
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"La descarga del Oficio del Certificado de intersección es requerida.", null));
		}
		
		if(!mapaDescargado) {
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"La descarga del Mapa del Certificado de intersección es requerida.", null));
		}
		
		if(!coordenadasDescargadas) {
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"La descarga de Coordenadas del proyecto es requerida.", null));
		}
		
		if(!guiaDescargada) {
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"La descarga de Respuestas del operador es requerida.", null));
		}
		
		if (documentoOficio == null || (documentoOficio.getId() == null && documentoOficio.getContenidoDocumento() == null))
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Adjuntar oficio de pronunciamiento' es requerido.", null));
		
		if (documentoInforme == null || (documentoInforme.getId() == null && documentoInforme.getContenidoDocumento() == null))
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Adjuntar Informe de Viabilidad Ambiental' es requerido.", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void uploadInforme(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoInforme = new DocumentoViabilidad();
		documentoInforme.setId(null);
		documentoInforme.setContenidoDocumento(contenidoDocumento);
		documentoInforme.setNombre(event.getFile().getFileName());
		documentoInforme.setMime("application/pdf");
		documentoInforme.setIdTipoDocumento(idTipoDocumentoInforme);
	}
	
	public void uploadOficio(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoOficio = new DocumentoViabilidad();
		documentoOficio.setId(null);
		documentoOficio.setContenidoDocumento(contenidoDocumento);
		documentoOficio.setNombre(event.getFile().getFileName());
		documentoOficio.setMime("application/pdf");
		documentoOficio.setIdTipoDocumento(idTipoDocumentoOficio);
	}
	
	public StreamedContent descargarArchivo(DocumentoViabilidad documentoDescarga) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoDescarga != null && documentoDescarga.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoDescarga.getIdAlfresco(), documentoDescarga.getFechaCreacion());
			} else if (documentoDescarga.getContenidoDocumento() != null) {
				documentoContent = documentoDescarga.getContenidoDocumento();
			}
			
			if (documentoDescarga != null && documentoDescarga.getNombre() != null
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

	public void enviar() {
		try {
			
			if (documentoInforme.getContenidoDocumento() != null) {
				documentoInforme.setIdViabilidad(viabilidadProyecto.getId());
				documentoInforme = documentosFacade.guardarDocumentoProceso(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoInforme, 1, 0L);
				
				if(documentoInforme == null || documentoInforme.getId() == null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
					return;
				}
			}
			
			if (documentoOficio.getContenidoDocumento() != null) {
				documentoOficio.setIdViabilidad(viabilidadProyecto.getId());
				documentoOficio = documentosFacade.guardarDocumentoProceso(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoOficio, 1, JsfUtil.getCurrentProcessInstanceId());
				
				if(documentoOficio == null || documentoOficio.getId() == null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
					return;
				}
			}
			
			Usuario usuarioResponsable = null;
			
			viabilidadProyecto.setViabilidadCompletada(true);
			viabilidadCoaFacade.guardar(viabilidadProyecto);
			
			if(esForestal) {
				PronunciamientoForestal oficioForestal = pronunciamientoForestalFacade.getInformePorViabilidad(viabilidadProyecto.getId());
				if(oficioForestal == null) {
					oficioForestal = new PronunciamientoForestal();
					oficioForestal.setIdViabilidad(viabilidadProyecto.getId());
				}
				
				oficioForestal.setNumeroOficio(numeroOficioArchivo);
				oficioForestal.setFechaOficio(fechaOficioArchivo);
				oficioForestal.setRecomendaciones(recomendaciones);
				
				pronunciamientoForestalFacade.guardar(oficioForestal);
			} else {
				PronunciamientoBiodiversidad oficioSnap = pronunciamientoBiodiversidadFacade.getInformePorViabilidad(viabilidadProyecto.getId());
				if(oficioSnap == null) {
					oficioSnap = new PronunciamientoBiodiversidad();
					oficioSnap.setIdViabilidad(viabilidadProyecto.getId());
				}
				
				oficioSnap.setNumeroOficio(numeroOficioArchivo);
				oficioSnap.setFechaOficio(fechaOficioArchivo);
				oficioSnap.setRecomendaciones(recomendaciones);
				
				pronunciamientoBiodiversidadFacade.guardar(oficioSnap);
			}
			
			//para validar si la viabilidad es favorable o no  
			List<ViabilidadCoa> viabilidadesProyecto = viabilidadCoaFacade.getViabilidadesPorProyecto(idProyecto);
			
			Boolean viabilidadAmbientalFavorable = null;
			Boolean viabilidadesCompletas = true;
			for (ViabilidadCoa viabilidadCoa : viabilidadesProyecto) {
				if(!viabilidadCoa.getViabilidadCompletada()) {
					viabilidadesCompletas = false;
					break;
				}
			}
			
			if(viabilidadesCompletas) {
				viabilidadAmbientalFavorable = true;
				for (ViabilidadCoa viabilidadCoa : viabilidadesProyecto) {
					if(!viabilidadCoa.getEsPronunciamientoFavorable()) {
						viabilidadAmbientalFavorable = false;
						break;
					}
				}
				
				if(!viabilidadAmbientalFavorable) {
					usuarioResponsable = asignarAutoridad();    	
			    	if(usuarioResponsable == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
						return;
					}
				} 
				
				proyectoLicenciaCoa.setTieneViabilidadFavorable(viabilidadAmbientalFavorable);
				proyectoLicenciaCoaFacade.guardar(proyectoLicenciaCoa);
				
				Map<String, Object> parametros = new HashMap<>();
				parametros.put("viabilidadAmbientalFavorable", viabilidadAmbientalFavorable);
				
				if(!viabilidadAmbientalFavorable) {
					parametros.put("autoridadAmbiental", usuarioResponsable.getNombre());
				}
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			}
			//fin validacion
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			if(viabilidadesCompletas) {
				if(viabilidadAmbientalFavorable) {
					notificarFavorable(viabilidadesProyecto);
				} else {
					notificarNoFavorableAutoridad(usuarioResponsable);
				}
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public Usuario asignarAutoridad() {
		String rolPrefijo = "role.va.bypass.cz.autoridad";
		String rolDirector = "";
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
    	
    	List<Usuario> listaUsuariosResponsables = new ArrayList<Usuario>();
		
		if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
			rolPrefijo = "role.va.bypass.pc.autoridad";
			rolDirector = Constantes.getRoleAreaName(rolPrefijo);
			
			listaUsuariosResponsables = usuarioFacade.buscarUsuarioPorRol(rolDirector);
		} else {
			if (areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
	    		rolPrefijo = "role.va.bypass.ga.autoridad";
			} else if (areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT)) {
				areaTramite = areaTramite.getArea();
			}
			
			rolDirector = Constantes.getRoleAreaName(rolPrefijo);
	    	listaUsuariosResponsables = usuarioFacade.buscarUsuariosPorRolYAreaEspecifica(rolDirector, areaTramite);
		}		

		if (listaUsuariosResponsables == null || listaUsuariosResponsables.isEmpty()) {
			LOG.error("No se encontro usuario " + rolDirector + " en " + areaTramite.getAreaName());
			return null;
		}
		
		Usuario usuarioResponsable = listaUsuariosResponsables.get(0);
		return usuarioResponsable;
	}
	
	public void notificarFavorable(List<ViabilidadCoa> viabilidadesProyecto) throws Exception {
		String nombreOperador = "";
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		for (ViabilidadCoa viabilidadCoa : viabilidadesProyecto) {
			Integer idTipoDocumentoInforme = (!viabilidadCoa.getEsViabilidadSnap()) 
					? TipoDocumentoSistema.RCOA_VIABILIDAD_BYPASS_INFORME_FORESTAL.getIdTipoDocumento() 
							: TipoDocumentoSistema.RCOA_VIABILIDAD_BYPASS_INFORME_SNAP.getIdTipoDocumento();
	
			List<DocumentoViabilidad> documentosInforme = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadCoa.getId(), idTipoDocumentoInforme);
			if (documentosInforme.size() > 0) {
				DocumentoViabilidad documentoInforme = documentosInforme.get(0);
				documentoInforme.setIdProceso(JsfUtil.getCurrentProcessInstanceId());
				documentosFacade.guardar(documentoInforme);
			}
		}
		
		ProyectoLicenciaCoaUbicacion proyectoLicenciaCoaUbicacion = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa);
		UbicacionesGeografica ubicacion = proyectoLicenciaCoaUbicacion.getUbicacionesGeografica();
		
		String provincia = ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
		String canton = ubicacion.getUbicacionesGeografica().getNombre();
		String parroquia = ubicacion.getNombre();
		
		String intersecaF = interseccionViabilidadCoaFacade.getNombresAreasProtegidasForestal(idProyecto, 1);
		String intersecaS = interseccionViabilidadCoaFacade.getNombresAreasProtegidasSnap(idProyecto, 1);
		String interseca = (!intersecaS.equals("")) ? ((!intersecaF.equals("")) ? intersecaS + ", " + intersecaF : intersecaS) : intersecaF;
		
		Object[] parametrosCorreoNuevo = new Object[] {nombreOperador, proyectoLicenciaCoa.getNombreProyecto(),
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(), provincia, canton, parroquia,
				interseca};
		
		String plantillaMsj = "bodyNotificacionViabilidadBypassFavorable";
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(plantillaMsj);
		
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);
		
		Email.sendEmail(usuarioOperador, mensajeNotificacion.getAsunto(), notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
		
	}
	
	public void notificarNoFavorableAutoridad(Usuario usuarioAutoridad) throws Exception {
		String nombreOperador = "";
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		String notificacionAutoridad = "bodyNotificacionViabilidadBypassNoFavorableAutoridad" ;
		
		Object[] parametrosCorreo = new Object[] {usuarioAutoridad.getPersona().getNombre(), proyectoLicenciaCoa.getNombreProyecto(),
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(), nombreOperador};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(notificacionAutoridad);
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Email.sendEmail(usuarioAutoridad, mensajeNotificacion.getAsunto(), notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());

	}
	
}
