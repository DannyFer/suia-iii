package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeRevisionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeTecnicoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class FirmarInformeOficioForestalController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(FirmarInformeOficioForestalController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{informeOficioViabilidadForestalBean}")
	@Getter
	@Setter
	private InformeOficioViabilidadForestalBean informeOficioViabilidadForestalBean;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private InformeRevisionForestalFacade informeInspeccionFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
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
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoInforme, documentoOficio, documentoInformeManual, documentoOficioManual;
	
	@Getter
	@Setter
	private InformeTecnicoForestal informeRevision;	

	@Getter
	@Setter
	private boolean token, informeSubido, documentoDescargado, oficioSubido, deshabilitarToken, habilitarEnviar, soloToken;
	
	@Getter
	@Setter
	private Integer idProyecto, idViabilidad;
	
	@Getter
	@Setter
	private String usuarioAutoridad, nombreDocumentoFirmado, nombreOficioFirmado;
	
	@Getter
	@Setter
	private boolean esTecnico, esCoordinador, esAutoridad;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@PostConstruct
	private void iniciar() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			
			String idProyectoString=(String)variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);
			
			usuarioAutoridad=(String)variables.get("autoridadAmbiental");
			
			soloToken = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
			
			verificaToken();
			
			esTecnico = false;
			esCoordinador = false;
			esAutoridad = false;
						
			String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			switch (tarea) {
			case "FirmarInformeRevisionForestalTecnico":
				esTecnico = true;
				break;
			case "FirmarInformeRevisionForestalCoordinador":
				esCoordinador = true;
				break;
			case "FirmarInformeOficioForestal":
				esAutoridad = true;
				break;
			default:
				break;
			}
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);
			idViabilidad = viabilidadProyecto.getId();
			
			informeRevision = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(
							viabilidadProyecto.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_REVISION
									.getIdTipoDocumento());
			if (documentos.size() > 0) {
				documentoInforme = documentos.get(0);
			} else {
				String nombre = viabilidadCoaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
                if (nombre.contains("FirmarInformeRevisionForestalTecnico")) {
                    informeOficioViabilidadForestalBean.generarInforme(false);
                    informeOficioViabilidadForestalBean.generarOficio(false);
                   
                    informeOficioViabilidadForestalBean.guardarDocumentosAlfresco();
                   
                    documentos = documentosFacade
                            .getDocumentoPorTipoTramite(
                                    viabilidadProyecto.getId(),
                                    TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_REVISION
                                            .getIdTipoDocumento());
                    if (documentos.size() > 0) {
                        documentoInforme = documentos.get(0);
                    } else {
                        LOG.error("No se encontro el informe de revisión forestal para el proyecto " + proyectoLicenciaCoa.getCodigoUnicoAmbiental());
                        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
                    }
                }
			}
			
			if (documentosFacade.verificarFirmaVersion(documentoInforme.getIdAlfresco())) {
				deshabilitarToken = true;
				token = true;
			}
			
			if(informeRevision != null && informeRevision.getEsPronunciamientoFavorable())
				 tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_FAVORABLE;
			else 
				tipoOficio = TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_NO_FAVORABLE;
			
			
			if(esAutoridad) {
				List<DocumentoViabilidad> documentosOficio = documentosFacade
						.getDocumentoPorTipoTramite(
								viabilidadProyecto.getId(), tipoOficio.getIdTipoDocumento());
				if (documentosOficio.size() > 0) {
					documentoOficio = documentosOficio.get(0);
					
					if(!documentoOficio.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre()) 
							|| !JsfUtil.getSimpleDateFormat(documentoOficio.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
						informeOficioViabilidadForestalBean.generarInforme(false);
						informeOficioViabilidadForestalBean.generarOficio(false);
						
						documentoOficio = informeOficioViabilidadForestalBean.guardarDocumentoOficioAlfresco();
					}
				} else {
					LOG.error("No se encontro el oficio de pronunciamiento forestal para el proyecto " + proyectoLicenciaCoa.getCodigoUnicoAmbiental());
					
					informeOficioViabilidadForestalBean.generarInforme(false);
					informeOficioViabilidadForestalBean.generarOficio(false);
					
					documentoOficio = informeOficioViabilidadForestalBean.guardarDocumentoOficioAlfresco();
				}
				
				if(documentoOficio == null || documentoOficio.getId() == null)
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
			
			habilitarEnviar = false;
			if(token)
				habilitarEnviar = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public boolean verificaToken() {
		token = false;
		habilitarEnviar = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken()) {
			token = true;
			habilitarEnviar = true;
		}
		
		if(soloToken) {
			token = true;
			habilitarEnviar = true;
		}
		
		return token;
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/forestal/firmarInformeOficioForestal.jsf");
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
	
	public StreamedContent descargarInforme() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoInforme != null && documentoInforme.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoInforme.getIdAlfresco(), documentoInforme.getFechaCreacion());
			}
			
			if (documentoInforme != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoInforme.getNombre());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			documentoDescargado = true;

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public String firmarInforme() {
		try {
			
			if(documentoInforme != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoInforme);
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
	
	public void uploadListenerInformeFirmado(FileUploadEvent event) {
		if(documentoDescargado) {
			
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInformeManual = new DocumentoViabilidad();
			documentoInformeManual.setId(null);
			documentoInformeManual.setContenidoDocumento(contenidoDocumento);
			documentoInformeManual.setNombre(event.getFile().getFileName());
			documentoInformeManual.setMime("application/pdf");
			documentoInformeManual.setIdViabilidad(viabilidadProyecto.getId());
			documentoInformeManual.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_REVISION.getIdTipoDocumento());

	        informeSubido = true;
	        habilitarEnviar = true;
	        nombreDocumentoFirmado = event.getFile().getFileName();
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
    }
	
	public StreamedContent descargarOficio() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoOficio != null && documentoOficio.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoOficio.getIdAlfresco(), documentoOficio.getFechaCreacion());
			}
			
			if (documentoOficio != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoOficio.getNombre());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			documentoDescargado = true;

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public String firmarOficio() {
		try {
			
			if(documentoOficio != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoOficio);
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
	
	public void uploadListenerOficioFirmado(FileUploadEvent event) {
		if(documentoDescargado) {
			
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoOficioManual = new DocumentoViabilidad();
			documentoOficioManual.setId(null);
			documentoOficioManual.setContenidoDocumento(contenidoDocumento);
			documentoOficioManual.setNombre(event.getFile().getFileName());
			documentoOficioManual.setMime("application/pdf");
			documentoOficioManual.setIdViabilidad(viabilidadProyecto.getId());
			documentoOficioManual.setIdTipoDocumento(tipoOficio.getIdTipoDocumento());

	        oficioSubido = true;
	        habilitarEnviar = true;
	        nombreOficioFirmado = event.getFile().getFileName();
		} else{
			JsfUtil.addMessageError("No ha realizado la carga del documento");
		}
    }
	
	public void completarTarea() {
		try {
			if(informeSubido){
				documentoInforme = documentosFacade.guardarDocumentoProceso(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoInformeManual, 2, JsfUtil.getCurrentProcessInstanceId());
			}
			
			if(esAutoridad && oficioSubido){
				documentoOficio = documentosFacade.guardarDocumentoProceso(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoOficioManual, 2, JsfUtil.getCurrentProcessInstanceId());
			}
			
			if (token) {
				if(!esAutoridad) {
					//File file = documentosFacade.getDocumentoPorIdAlfresco(documentoInforme.getIdAlfresco());
					//Boolean firmaUsuario = UtilViabilidad.verificarFirmaUsuario(file, JsfUtil.getLoggedUser().getNombre());
					Boolean firmaUsuario = documentosFacade.verificarFirmaVersion(documentoInforme.getIdAlfresco());
					if (!firmaUsuario) {
						JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
						return;
					}
				}
				if(esAutoridad) {
					//valida q el oficio este firmado
					String idAlfrescoOficio = documentoOficio.getIdAlfresco();

					if (!documentosFacade.verificarFirmaVersion(idAlfrescoOficio)) {
						JsfUtil.addMessageError("El oficio no está firmado electrónicamente.");
						return;
					}
				}
			} else {
				if (!esAutoridad && !informeSubido) {
					JsfUtil.addMessageError("Debe adjuntar el informe firmado.");
					return;
				}
				if(esAutoridad && !oficioSubido) {
					JsfUtil.addMessageError("Debe adjuntar el oficio firmado.");
					return;
				}
			}
			
			try {
				Map<String, Object> parametros = new HashMap<>();
				
				Area areaTramite = viabilidadProyecto.getAreaResponsable();
				
				if(esTecnico) {
					String tipoRol = "role.va.cz.coordinador.forestal";
					
					if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC))
						tipoRol = "role.va.pc.coordinador.forestal";
					
					String rolCoordinador = Constantes.getRoleAreaName(tipoRol);
					List<Usuario> usuarioCoordinador = usuarioFacade.buscarUsuariosPorRolYArea(rolCoordinador, areaTramite);
					
					if(usuarioCoordinador==null || usuarioCoordinador.isEmpty()){
						LOG.error("No se encontro usuario " + rolCoordinador + " en " + areaTramite.getAreaName());
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
						JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
						return;
					}
					
					Usuario coordinador = usuarioCoordinador.get(0);
					String coordinadorBpm =  (String) variables.get("coordinadorForestal");
					if (coordinadorBpm == null || !coordinadorBpm.equals(coordinador.getNombre())) {
						parametros.put("coordinadorForestal", coordinador.getNombre());
					}
				} 
				
				if(esCoordinador) {
					String roldirector = "";
					if (areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
						roldirector = Constantes.getRoleAreaName("role.va.pc.director.forestal");
					} else {
						roldirector = Constantes.getRoleAreaName("role.va.cz.director.forestal");
						areaTramite = areaTramite.getArea();
					}
				
					List<Usuario> listaUsuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(roldirector, areaTramite);
					
					if(listaUsuarioAutoridad==null || listaUsuarioAutoridad.isEmpty()){
						LOG.error("No se encontro usuario " + roldirector + " en " + areaTramite.getAreaName());
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
						JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
						return;
					}
					
					Usuario usuarioAutoridad = listaUsuarioAutoridad.get(0);
					String usuarioAutoridadBpm = (String) variables.get("autoridadAmbiental");
					if (usuarioAutoridadBpm == null || !usuarioAutoridadBpm.equals(usuarioAutoridad.getNombre())) {
						parametros.put("autoridadAmbiental", usuarioAutoridad.getNombre());
					}
				} 
				
				parametros.put("pronunciamientoForestalCorrecto", true);//variable interna para regresar a la tarea de elaboracion del informe
				
				if(esAutoridad) {
					parametros.put("viabilidadForestalFavorable",informeRevision.getEsPronunciamientoFavorable());
					
					viabilidadProyecto.setViabilidadCompletada(true);
					viabilidadProyecto.setEsPronunciamientoFavorable(informeRevision.getEsPronunciamientoFavorable());
					viabilidadCoaFacade.guardar(viabilidadProyecto);
					
					//para validar si la viabilidad es favorable o no  
					List<ViabilidadCoa> viabilidadesProyecto = viabilidadCoaFacade.getViabilidadesPorProyecto(idProyecto);
					Boolean viabilidadesCompletas = true;
					for (ViabilidadCoa viabilidadCoa : viabilidadesProyecto) {
						if(!viabilidadCoa.getViabilidadCompletada()) {
							viabilidadesCompletas = false;
							break;
						}
					}
					
					if(viabilidadesCompletas) {
						Boolean viabilidadAmbientalFavorable = true;
						for (ViabilidadCoa viabilidadCoa : viabilidadesProyecto) {
							if(!viabilidadCoa.getEsPronunciamientoFavorable()) {
								viabilidadAmbientalFavorable = false;
								break;
							}
						}
						
						proyectoLicenciaCoa.setTieneViabilidadFavorable(viabilidadAmbientalFavorable);
						
						if(!viabilidadAmbientalFavorable) {
							proyectoLicenciaCoa.setProyectoFinalizado(true);
						}
						proyectoLicenciaCoaFacade.guardar(proyectoLicenciaCoa);
					}
					//fin validacion
				}
					
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				
			} catch (Exception e) {
				e.printStackTrace();
				JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
}
