package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
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

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformacionViabilidadLegalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformacionViabilidadLegal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CorregirObservacionesJuridicoController {
	
	private final Logger LOG = Logger.getLogger(CorregirObservacionesJuridicoController.class);
	
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
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@Getter
	@Setter
	private Integer idProyecto, idViabilidad, numeroObservaciones;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private InformacionViabilidadLegal informacionViabilidadLegal;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoJuridico;
	
	@Getter
	@Setter
	private Area areaTramite;
	
	@Getter
	@Setter
	private Boolean datosGuardados;
	
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
			
			nombreTarea = viabilidadCoaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			if(nombreTarea.equals("RecibirObservacionesJuridicoForestal")) {
				viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);
			} else if(nombreTarea.equals("RecibirObservacionesJuridicoBiodiversidad")) {
				viabilidadProyecto = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(idProyecto, false);
			} else if(nombreTarea.equals("RecibirObservacionesJuridicoSnapMae")) {
				viabilidadProyecto = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(idProyecto, true);
			}
			
			idViabilidad = viabilidadProyecto.getId();
			informacionViabilidadLegal= revisionTecnicoJuridicoFacade.getInformacionViabilidadLegalPorId(viabilidadProyecto.getId());
			
			if(informacionViabilidadLegal.getConflictoResuelto() != null && informacionViabilidadLegal.getConflictoResuelto()){
				List<DocumentoViabilidad> documentos = documentosFacade
						.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
								TipoDocumentoSistema.RCOA_VIABILIDAD_INFORMACION_RESPALDO_OPERADOR
										.getIdTipoDocumento());
				if (documentos.size() > 0) {
					documentoJuridico = documentos.get(0);
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
				"/pages/rcoa/viabilidadAmbiental/correccionesPromotor.jsf");
	}
	
	public void uploadFile(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoJuridico = new DocumentoViabilidad();
		documentoJuridico.setId(null);
		documentoJuridico.setContenidoDocumento(contenidoDocumento);
		documentoJuridico.setNombre(event.getFile().getFileName());
		documentoJuridico.setMime("application/pdf");
		documentoJuridico.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_INFORMACION_RESPALDO_OPERADOR.getIdTipoDocumento());
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoJuridico != null && documentoJuridico.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoJuridico.getIdAlfresco(), documentoJuridico.getFechaCreacion());
			} else if (documentoJuridico.getContenidoDocumento() != null) {
				documentoContent = documentoJuridico.getContenidoDocumento();
			}
			
			if (documentoJuridico != null && documentoJuridico.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoJuridico.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void validateDatos(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (documentoJuridico == null || (documentoJuridico.getId() == null && documentoJuridico.getContenidoDocumento() == null))
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Documentos de respaldo' es requerido.", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void guardarInforme() {
		try {
			revisionTecnicoJuridicoFacade.guardar(informacionViabilidadLegal);
			
			if (documentoJuridico.getContenidoDocumento() != null) {
				documentoJuridico.setIdViabilidad(viabilidadProyecto.getId());
				documentoJuridico = documentosFacade.guardarDocumentoProceso(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoJuridico, 1, JsfUtil.getCurrentProcessInstanceId());
				
				if(documentoJuridico == null || documentoJuridico.getId() == null) {
					documentoJuridico.setContenidoDocumento(null);
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
					return;
				}
			}

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
				
				String juridicoBpm = null;
				Boolean esForestal = false;
				if(nombreTarea.equals("RecibirObservacionesJuridicoForestal")) {
					juridicoBpm =  (String) variables.get("abogadoForestal");
					esForestal = true;
				} else if(nombreTarea.equals("RecibirObservacionesJuridicoBiodiversidad")) {
					juridicoBpm =  (String) variables.get("abogadoPatrimonio");
				} else if(nombreTarea.equals("RecibirObservacionesJuridicoSnapMae")) {
					juridicoBpm =  (String) variables.get("abogadoAreaProtegida");
				}
				
				List<Usuario> usuarioJuridico = null;
				String rolJuridico = "";
				Area areaJuridico;
				
				if(!esForestal) {
					areaJuridico = proyectoLicenciaCoa.getAreaResponsable().getArea()!=null?proyectoLicenciaCoa.getAreaResponsable().getArea():proyectoLicenciaCoa.getAreaResponsable(); //el juridico para SNAP siempre se toma de las zonales
					rolJuridico = Constantes.getRoleAreaName("role.tecnico.Juridico");
					usuarioJuridico = usuarioFacade.buscarUsuariosPorRolYAreaEspecifica(rolJuridico, areaJuridico);
				} else {
					areaJuridico = proyectoLicenciaCoa.getAreaResponsable().getArea()!=null?proyectoLicenciaCoa.getAreaResponsable().getArea():proyectoLicenciaCoa.getAreaResponsable();//el juridico se toma de las zonales observacion QA
					if(areaJuridico.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
						areaJuridico = areaFacade.getAreaPorAreaAbreviacion("DNPCA");
					} 
					rolJuridico = Constantes.getRoleAreaName("role.tecnico.Juridico");
					usuarioJuridico = usuarioFacade.buscarUsuariosPorRolYAreaEspecifica(rolJuridico, areaJuridico); //busca usuario solo de zonales no de OT
				}
				
				if(usuarioJuridico==null || usuarioJuridico.isEmpty()){
					LOG.error("No se encontro usuario " + rolJuridico + " en " + areaJuridico.getAreaName());
					//JsfUtil.addMessageError("No se encontro usuario " + rolJuridico + " en " + areaJuridico.getAreaName());
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
					return;
				}
				
				Usuario analistaJuridico = usuarioJuridico.get(0); 
				if (juridicoBpm == null || !juridicoBpm.equals(analistaJuridico.getNombre())) {
					if(nombreTarea.equals("RecibirObservacionesJuridicoForestal")) {
						parametros.put("abogadoForestal", analistaJuridico.getNombre());
					} else if(nombreTarea.equals("RecibirObservacionesJuridicoBiodiversidad")) {
						parametros.put("abogadoPatrimonio", analistaJuridico.getNombre());
					} else if(nombreTarea.equals("RecibirObservacionesJuridicoSnapMae")) {
						parametros.put("abogadoAreaProtegida", analistaJuridico.getNombre());
					}						
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
