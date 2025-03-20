package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RecibirObservacionesViabilidadPfnController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoInforme, documentoOficio;
	
	@Getter
	@Setter
	private Integer idProyecto;
	
	@Getter
	@Setter
	private Boolean informeDescargado, oficioDescargado;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@PostConstruct
	private void iniciar() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			
			String idProyectoString=(String)variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			ViabilidadCoa viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);
			
			List<DocumentoViabilidad> documentosOficio = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_OFICIO_OBSERVACIONES.getIdTipoDocumento());
			if (documentosOficio.size() > 0) {
				documentoOficio = documentosOficio.get(0);
			}
			
			List<DocumentoViabilidad> documentosInforme = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_OBSERVACIONES.getIdTipoDocumento());
			if (documentosInforme.size() > 0) {
				documentoInforme = documentosInforme.get(0);
			}
			
			informeDescargado = false;
			oficioDescargado = false;
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalForestal/recibirObservaciones.jsf");
	}
	
	public StreamedContent descargar(Integer tipoDocumento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			DocumentoViabilidad documento = null;
			
			if(tipoDocumento.equals(1)) {
				documento = documentoInforme;
				informeDescargado = false;
			} else {
				documento = documentoOficio;
				oficioDescargado = false;
			}
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco(), documento.getFechaCreacion());
			} else if (documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
				
				if(tipoDocumento.equals(1)) {
					informeDescargado = true;
				} else {
					oficioDescargado = true;
				}
				
				return content;
			} else {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return null;
	}
	
	public void finalizar() {
		try {
			
			if(!informeDescargado || !oficioDescargado) {
				JsfUtil.addMessageInfo("Debe realizar la descarga de todos los documentos.");
				return;
			}
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
}
