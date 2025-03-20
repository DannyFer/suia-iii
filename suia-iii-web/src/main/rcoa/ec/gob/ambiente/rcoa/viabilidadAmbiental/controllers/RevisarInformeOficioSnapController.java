package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeRevisionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ObservacionesViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PronunciamientoBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ObservacionesViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisarInformeOficioSnapController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{oficioViabilidadSnapBean}")
	@Getter
	@Setter
	private OficioViabilidadSnapBean oficioViabilidadSnapBean;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private InformeInspeccionBiodiversidadFacade informeInspeccionFacade;
	
	@EJB
	private PronunciamientoBiodiversidadFacade pronunciamientoBiodiversidadFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
	private ObservacionesViabilidadFacade observacionesViabilidadFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private InformeRevisionForestalFacade informeInspeccionForestalFacade;
	
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
	private Boolean mostrarInforme, esReporteAprobacion, informeOficioCorrectos, mostrarFirma, esAutoridad;
	
	@Getter
	@Setter
	private String urlOficio, nombreOficio, nombreDocumentoFirmado, tipoPronunciamiento, urlInforme, nombreInforme;
	
	@Getter
	@Setter
	private byte[] archivoOficio, archivoInforme;

	@Getter
	@Setter
	private boolean token, subido, documentoDescargado, habilitarEnviar, soloToken;
	
	@Getter
	@Setter
	private Integer idProyecto, idViabilidad;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		
		mostrarInforme = true;
		mostrarFirma = false;
		
		viabilidadProyecto = oficioViabilidadSnapBean.getViabilidadProyecto();
		informeInspeccion = oficioViabilidadSnapBean.getInformeInspeccion();
		
		esReporteAprobacion = informeInspeccion.getEsPronunciamientoFavorable();
		
		soloToken = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
		
		esAutoridad = false;
		informeOficioCorrectos = true;
		token = true;
		
		if (JsfUtil.getCurrentTask().getTaskName().toUpperCase().contains("FIRMAR")) {
			esAutoridad = true;
			verificaToken();
		}
		
		tipoOficio = informeInspeccion.getEsPronunciamientoFavorable() ? TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_FAVORABLE : TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_NO_FAVORABLE;
		tipoPronunciamiento =  esReporteAprobacion ? "Pronunciamiento Aprobación" : "Pronunciamiento Observación";

		oficioViabilidadSnapBean.generarOficio(true);
		
		urlOficio = oficioViabilidadSnapBean.getOficioPronunciamiento().getOficioPath();
		nombreOficio = oficioViabilidadSnapBean.getOficioPronunciamiento().getNombreOficio();
		archivoOficio = oficioViabilidadSnapBean.getOficioPronunciamiento().getArchivoOficio();
		
		buscarInformeInspeccion();
		
		habilitarEnviar = false;
		if(token)
			habilitarEnviar = true;
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
				"/pages/rcoa/viabilidadAmbiental/snap/revisarInformeOficioSnap.jsf");
	}
	
	public void buscarInformeInspeccion() {
		try {
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_INFORME_INSPECCION.getIdTipoDocumento());
			if(documentos.size() > 0) {
				DocumentoViabilidad documentoInforme = documentos.get(0);
				
				File fileDoc = documentosFacade.getDocumentoPorIdAlfresco(documentoInforme.getIdAlfresco());
				
				Path path = Paths.get(fileDoc.getAbsolutePath());
				byte[] contenido = Files.readAllBytes(path);
				String reporteHtmlfinal = documentoInforme.getNombre().replace("/", "-");
				File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
				FileOutputStream file = new FileOutputStream(archivoFinal);
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
							TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_INFORME_INSPECCION.getIdTipoDocumento());
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
	
	public void validarExisteObservacionesOficio() {
		try {
			informeOficioCorrectos = false;
			
			List<ObservacionesViabilidad> observacionesOficio = observacionesViabilidadFacade.listarPorIdClaseNombreClaseNoCorregidas(
					oficioViabilidadSnapBean.getOficioPronunciamiento().getId(), "oficioViabilidadSnap");
			
			if(observacionesOficio.size() == 0) {
				informeOficioCorrectos = true;
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
	
	public void firmarOficio() {
		mostrarFirma = true;
	}
	
	//envia a correcciones o firma del director
	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
			
			if(viabilidadProyecto.getEsAdministracionMae()) {
				parametros.put("obsOficioSnapMae", !informeOficioCorrectos);
				
				String directorBpm =  (String) oficioViabilidadSnapBean.getVariables().get("directorProvincial");
				if (directorBpm == null || !directorBpm.equals(oficioViabilidadSnapBean.getUsuarioAutoridad().getNombre())) {
					parametros.put("directorProvincial", oficioViabilidadSnapBean.getUsuarioAutoridad().getNombre());
				}
				
				parametros.put("pronunciamientoSnapMaeCorrecto", true);//variable interna para regresar a la tarea de ingreso informe inspeccion
				
			} else {
				//no se buscar usuario xq el q revisa y firma es el Director
				parametros.put("obsOficioSnapDelegada", !informeOficioCorrectos);
				
				parametros.put("pronunciamientoSnapCorrecto", true);//variable interna para regresar a la tarea de ingreso informe inspeccion
			}
			
			try {
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
	
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			oficioViabilidadSnapBean.subirOficio();
			
			byte[] documentoContent = oficioViabilidadSnapBean.getOficioPronunciamiento().getArchivoOficio();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(oficioViabilidadSnapBean.getOficioPronunciamiento().getNombreOficio());
			} else
				JsfUtil.addMessageError("Error al generar el archivo");
			
			documentoDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public String firmarDocumento() {
		try {
			oficioViabilidadSnapBean.subirOficio();
			
			if(oficioViabilidadSnapBean.getDocumentoOficioPronunciamiento() != null) {
				String documentOffice = documentosFacade.direccionDescarga(oficioViabilidadSnapBean.getDocumentoOficioPronunciamiento());
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if(documentoDescargado) {
			
			oficioViabilidadSnapBean.crearDocumentoFirmaManual(event);
	        
	        nombreDocumentoFirmado = event.getFile().getFileName();

	        subido = true;
	        habilitarEnviar = true;
		} else{
			JsfUtil.addMessageError("No ha realizado la carga del documento");
		}
    }
	
	public void completarTarea() {
		try {
			if(subido){
				oficioViabilidadSnapBean.guardarDocumentoOficioFirmaManual();
			}
			
			if (token) {
				String idAlfresco = oficioViabilidadSnapBean.getDocumentoOficioPronunciamiento().getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}
			} else if (!token && !subido) {
				JsfUtil.addMessageError("Debe adjuntar el informe firmado.");
				return;
			}
			
			try {
				Map<String, Object> parametros = new HashMap<>();
				
				if(viabilidadProyecto.getEsAdministracionMae()) {
					parametros.put("obsOficioSnapMae", !informeOficioCorrectos);
					parametros.put("viabilidadSnapMaeFavorable", oficioViabilidadSnapBean.getInformeInspeccion().getEsPronunciamientoFavorable());
					
					parametros.put("pronunciamientoSnapMaeCorrecto", true);//variable interna para regresar a la tarea de ingreso informe inspeccion
				} else {				
					parametros.put("obsOficioSnapDelegada", !informeOficioCorrectos);
					parametros.put("viabilidadSnapDelegadaFavorable", oficioViabilidadSnapBean.getInformeInspeccion().getEsPronunciamientoFavorable());
					
					parametros.put("pronunciamientoSnapCorrecto", true);//variable interna para regresar a la tarea de ingreso informe inspeccion
				}
				
				viabilidadProyecto.setViabilidadCompletada(true);
				viabilidadProyecto.setEsPronunciamientoFavorable(oficioViabilidadSnapBean.getInformeInspeccion().getEsPronunciamientoFavorable());
				viabilidadCoaFacade.guardar(viabilidadProyecto);
				
				//para validar si la viabilidad es favorable o no
				List<ViabilidadCoa> viabilidadesProyecto = viabilidadCoaFacade.getViabilidadesPorProyecto(oficioViabilidadSnapBean.getIdProyecto());
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
					
					oficioViabilidadSnapBean.getProyectoLicenciaCoa().setTieneViabilidadFavorable(viabilidadAmbientalFavorable);
					if(!viabilidadAmbientalFavorable) {
						oficioViabilidadSnapBean.getProyectoLicenciaCoa().setProyectoFinalizado(true);
					}
					proyectoLicenciaCoaFacade.guardar(oficioViabilidadSnapBean.getProyectoLicenciaCoa());
				}
				//fin validacion
				
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
