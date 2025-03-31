package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformacionViabilidadLegalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformacionViabilidadLegal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisionTecnicoJuridicoController {
	
	private final Logger LOG = Logger.getLogger(RevisionTecnicoJuridicoController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private	InformacionViabilidadLegalFacade revisionTecnicoJuridicoFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@Getter
	@Setter
	private Integer idProyecto, idViabilidad, numeroObservaciones;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private InformacionViabilidadLegal informacionViabilidadLegal, respuestaViabilidadLegal;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoViabilidad;
	
	@Getter
	@Setter
	private Area areaTramite;
	
	@Getter
	@Setter
	private Boolean datosGuardados, esTecnico;
	
	@Getter
	@Setter
	private String nombreTarea;
	
	private Map<String, Object> variables;
	
	@PostConstruct
	public void init() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			
			String idProyectoString=(String)variables.get("idProyecto");
			
			idProyecto = Integer.valueOf(idProyectoString);
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			datosGuardados = false;
			esTecnico = true;
			
			nombreTarea = viabilidadCoaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			if(nombreTarea.equals("RevisarTramiteForestal")) {
				viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);
			} else if(nombreTarea.equals("RevisarTramiteBiodiversidad")) {
				viabilidadProyecto = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(idProyecto, false);
			} else if(nombreTarea.equals("RevisarTramiteSnapMae")) {
				viabilidadProyecto = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(idProyecto, true);
			}
			
			idViabilidad = viabilidadProyecto.getId();
			
			List<InformacionViabilidadLegal> conflictosLegales = revisionTecnicoJuridicoFacade.getListaConflictosPorIdViabilidad(viabilidadProyecto.getId());
			
			if(conflictosLegales == null){
				informacionViabilidadLegal = new InformacionViabilidadLegal();
			} else {
				//si la ultima esta resuelta genero una nueva
				if(conflictosLegales.get(0).getConflictoResuelto() != null && conflictosLegales.get(0).getConflictoResuelto()) {
					informacionViabilidadLegal = new InformacionViabilidadLegal();
				}else {
					informacionViabilidadLegal = conflictosLegales.get(0);
				}
				
				for (InformacionViabilidadLegal conflicto : conflictosLegales) {
					if(conflicto.getConflictoResuelto() != null && conflicto.getConflictoResuelto()) {
						respuestaViabilidadLegal = conflicto;
						esTecnico = false;
						break;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos Informe Revision Forestal.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/revisionTecnico.jsf");
	}
	
	public StreamedContent descargarInformacionRespaldo() throws IOException {
		DefaultStreamedContent content = null;
		try {
			DocumentoViabilidad documentoDescarga = null;
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_ANALISIS_JURIDICO.getIdTipoDocumento());
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
	
	public StreamedContent descargarInformacionRespaldoOperador() throws IOException {
		DefaultStreamedContent content = null;
		try {
			DocumentoViabilidad documentoDescarga = null;
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_INFORMACION_RESPALDO_OPERADOR.getIdTipoDocumento());
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
	
	public void guardarInforme() {
		try {
			informacionViabilidadLegal.setViabilidadCoa(viabilidadProyecto);
			revisionTecnicoJuridicoFacade.guardar(informacionViabilidadLegal);
			datosGuardados = true;
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
		}
	}
	
	public void finalizar() {
		try {
			try {
				Map<String, Object> parametros = new HashMap<>();
			
				if(nombreTarea.equals("RevisarTramiteForestal")) {
					parametros.put("aprobadoConflictoForestal", informacionViabilidadLegal.getPasoLegal());
				} else if(nombreTarea.equals("RevisarTramiteBiodiversidad")) {
					parametros.put("aprobadoConflictoSnapDelgada", informacionViabilidadLegal.getPasoLegal());
				} else if(nombreTarea.equals("RevisarTramiteSnapMae")) {
					parametros.put("aprobadoConflictoSnapMae", informacionViabilidadLegal.getPasoLegal());
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
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
}
