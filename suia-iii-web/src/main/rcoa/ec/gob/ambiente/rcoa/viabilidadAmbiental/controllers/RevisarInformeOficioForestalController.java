package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ObservacionesViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ObservacionesViabilidad;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisarInformeOficioForestalController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RevisarInformeOficioForestalController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{informeOficioViabilidadForestalBean}")
	@Getter
	@Setter
	private InformeOficioViabilidadForestalBean informeOficioViabilidadForestalBean;
	
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

	@Getter
	@Setter
	private Boolean mostrarInforme, esReporteAprobacion, informeOficioCorrectos, mostrarFirma, esAutoridad;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte, tipoOficio, nombreDocumentoFirmado;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private Integer panelMostrar;

	@Getter
	@Setter
	private boolean token, subido;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			esAutoridad = false;
			
			String nombre = viabilidadCoaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			if (nombre.contains("Director")) {
				esAutoridad = true;
			}
				
			informeOficioViabilidadForestalBean.generarInforme(true);
			mostrarInforme = true;
			panelMostrar = 1;
			esReporteAprobacion = informeOficioViabilidadForestalBean.getInformeRevision().getEsPronunciamientoFavorable();
			
			urlReporte = informeOficioViabilidadForestalBean.getInformeRevision().getInformePath();
			nombreReporte = informeOficioViabilidadForestalBean.getInformeRevision().getNombreReporte();
			archivoReporte = informeOficioViabilidadForestalBean.getInformeRevision().getArchivoInforme();
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/forestal/revisarInformeOficioForestal.jsf");
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
					.getDocumentoPorTipoTramite(informeOficioViabilidadForestalBean.getViabilidadProyecto().getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_INSPECCION.getIdTipoDocumento());
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
			} else {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void guardar() {
		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		informeOficioViabilidadForestalBean.generarInforme(true);
	}
	
	public void aceptarInforme() {
		informeOficioViabilidadForestalBean.generarOficio(true);
		
		urlReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getOficioPath();
		nombreReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getNombreOficio();
		archivoReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getArchivoOficio();
		
		mostrarInforme = false;
	}
	
	public void verInforme() {
		informeOficioViabilidadForestalBean.generarInforme(true);
		
		urlReporte = informeOficioViabilidadForestalBean.getInformeRevision().getInformePath();
		nombreReporte = informeOficioViabilidadForestalBean.getInformeRevision().getNombreReporte();
		archivoReporte= informeOficioViabilidadForestalBean.getInformeRevision().getArchivoInforme();
		
		mostrarInforme = true;
	}
	
	public void validarExisteObservacionesInformeOficio() {
		try {
			informeOficioCorrectos = false;
			List<ObservacionesViabilidad> observacionesInforme = observacionesViabilidadFacade.listarPorIdClaseNombreClaseNoCorregidas(
							informeOficioViabilidadForestalBean.getInformeRevision().getId(), "informeRevisionForestalViabilidad");
			
			informeOficioViabilidadForestalBean.getOficio();
			List<ObservacionesViabilidad> observacionesOficio = observacionesViabilidadFacade.listarPorIdClaseNombreClaseNoCorregidas(
					informeOficioViabilidadForestalBean.getOficioPronunciamiento().getId(), "oficioViabilidadForestal");
			
			if(observacionesInforme.size() == 0 && observacionesOficio.size() == 0) {
				informeOficioCorrectos = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones del informe oficio RETCE.");
		}
	}
	
	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
			
			if(informeOficioViabilidadForestalBean.getInformeRevision().getArchivoInforme() == null 
					|| archivoReporte == null) {
				System.out.println("Error al generar el documento del informe u oficio forestal");
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return;
			}
			

			String usuarioAutoridadBpm = (String) informeOficioViabilidadForestalBean.getVariables().get("directorForestal");
			if (usuarioAutoridadBpm == null || !usuarioAutoridadBpm.equals(informeOficioViabilidadForestalBean.getUsuarioAutoridad().getNombre())) {
				parametros.put("directorForestal", informeOficioViabilidadForestalBean.getUsuarioAutoridad().getNombre());
			}
			
			parametros.put("obsOficioForestal", !informeOficioCorrectos);
			
			//si no hay obs y si es autoridad generar los documentos y subir al alfresco
			
			if(esAutoridad && informeOficioCorrectos) {
				informeOficioViabilidadForestalBean.guardarInforme();
				informeOficioViabilidadForestalBean.guardarOficio();
				
				informeOficioViabilidadForestalBean.generarInforme(false);
				informeOficioViabilidadForestalBean.generarOficio(false);
				
				informeOficioViabilidadForestalBean.guardarDocumentosAlfresco();
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
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public void atras() {
		switch (panelMostrar) {
		case 2:
			informeOficioViabilidadForestalBean.generarInforme(true);
			
			urlReporte = informeOficioViabilidadForestalBean.getInformeRevision().getInformePath();
			nombreReporte = informeOficioViabilidadForestalBean.getInformeRevision().getNombreReporte();
			archivoReporte= informeOficioViabilidadForestalBean.getInformeRevision().getArchivoInforme();
			
			mostrarInforme = true;
			panelMostrar = 1;
			break;
		case 3:
			informeOficioViabilidadForestalBean.generarOficio(true);
			urlReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getOficioPath();
			nombreReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getNombreOficio();
			archivoReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getArchivoOficio();
			
			mostrarInforme = false;
			panelMostrar = 2;
			break;
		default:
			break;
		}
		
	}
	
	public void siguiente() {	
		if(panelMostrar == 1) {
			informeOficioViabilidadForestalBean.getOficio();
			informeOficioViabilidadForestalBean.guardarOficio();
			informeOficioViabilidadForestalBean.generarOficio(true);
			urlReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getOficioPath();
			nombreReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getNombreOficio();
			archivoReporte = informeOficioViabilidadForestalBean.getOficioPronunciamiento().getArchivoOficio();
			
			mostrarInforme = false;
			panelMostrar = 2;
		}
	}
}
