package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
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

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ProyectoTipoViabilidadCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class RegistroForestalController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RegistrarSnapController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
    private ProcesoFacade procesoFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosViabilidadFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoGuiaRespuestas;
	
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
	Integer idProyecto;
	
	@Getter
	@Setter
	private Boolean guiaDescargada;
	
	@Getter
	@Setter
	private String nombreGuia, nombreGuiaDescarga;
	
	private Map<String, Object> variables;

	@PostConstruct
	private void iniciar() {
		
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
	
			String idProyectoString=(String)variables.get("idProyecto");
			
			idProyecto = Integer.valueOf(idProyectoString);
			guiaDescargada = false;
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			proyectoTipoViabilidadCoa = viabilidadCoaFacade.getTipoViabilidadPorProyecto(idProyecto, false); //false busco viabilidad forestal
			
			if(proyectoTipoViabilidadCoa == null) {
				proyectoTipoViabilidadCoa = new ProyectoTipoViabilidadCoa();
				proyectoTipoViabilidadCoa.setIdProyectoLicencia(idProyecto);
				proyectoTipoViabilidadCoa.setEsViabilidadSnap(false);
				viabilidadCoaFacade.guardarProyectoTipoViabilidadCoa(proyectoTipoViabilidadCoa);
			}
	
			// buscar viabilidad por proyecto
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadPorTipoProyecto(proyectoTipoViabilidadCoa.getId());
			
			if(viabilidadProyecto != null) {
				List<DocumentoViabilidad> documentos = documentosViabilidadFacade
						.getDocumentoPorTipoTramite(
								viabilidadProyecto.getId(),
								TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_ANEXO
										.getIdTipoDocumento());
				if (documentos.size() > 0) {
					documentoGuiaRespuestas = documentos.get(0);
				}
			}
			
			ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
			CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();			
			if (actividadPrincipal.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS) ||
					actividadPrincipal.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_MINERIA) ||
					actividadPrincipal.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_ELECTRICO) ||
					proyectoLicenciaCoa.getAltoImpacto()) {
				nombreGuia = "DF RCOA ANEXO I VIABILIDAD AMBIENTAL.docx";
				nombreGuiaDescarga = "AnexoViabilidadForestal.docx";
			} else {
				nombreGuia = "DF RCOA ANEXO II VIABILIDAD AMBIENTAL.docx";
				nombreGuiaDescarga = "AnexoViabilidadForestal.docx";
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos RegistroForestalController.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/forestal/ingresarForestal.jsf");
	}
	
	public StreamedContent descargarGuia() {
        try {
        	DefaultStreamedContent content = null;
        	
            byte[] documentoGuia = documentosFacade.descargarDocumentoPorNombre(nombreGuia);
            if (documentoGuia != null) {
                guiaDescargada = true;
                content = new DefaultStreamedContent(new ByteArrayInputStream(documentoGuia));
				content.setName(nombreGuiaDescarga);
				return content;
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
        } catch (Exception e) {
            JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
        }
    }
	
	public void uploadFile(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoGuiaRespuestas = new DocumentoViabilidad();
		documentoGuiaRespuestas.setId(null);
		documentoGuiaRespuestas.setContenidoDocumento(contenidoDocumento);
		documentoGuiaRespuestas.setNombre(event.getFile().getFileName());
		documentoGuiaRespuestas.setMime("application/pdf");
		documentoGuiaRespuestas.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_ANEXO.getIdTipoDocumento());
	}
	
	public void validateDatosIngreso(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (documentoGuiaRespuestas == null || (documentoGuiaRespuestas.getId() == null && documentoGuiaRespuestas.getContenidoDocumento() == null))
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Guía metodológica con respuestas para Viabilidad Ambiental' es requerido.", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public StreamedContent descargar(DocumentoViabilidad documentoDescarga) throws IOException {
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
	
	public void finalizar() {
		try {
			UbicacionesGeografica ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa).getUbicacionesGeografica();
			
			Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
			String tipoRol = "role.va.cz.tecnico.forestal";
			
			if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				tipoRol = "role.va.pc.tecnico.forestal";
				areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
			} 
			
			if(areaTramite == null) {
				LOG.error("No se encontro área responsable para la Viabilidad Forestal del proyecto " + proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			if (viabilidadProyecto == null) {
				viabilidadProyecto = new ViabilidadCoa();
				viabilidadProyecto.setIdProyectoLicencia(idProyecto);
				viabilidadProyecto.setIdProyectoTipoViabilidad(proyectoTipoViabilidadCoa.getId());
				viabilidadProyecto.setEsViabilidadSnap(false);
				viabilidadProyecto.setAreaResponsable(areaTramite);
				viabilidadProyecto.setViabilidadCompletada(false);

				viabilidadCoaFacade.guardar(viabilidadProyecto);
			}			
			
			String nombreAreaProtegida = interseccionViabilidadCoaFacade.getInterseccionesForestal(idProyecto, 1);

			Map<String, Object> parametros = new HashMap<>();

			String rolTecnico = Constantes.getRoleAreaName(tipoRol);

			List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());			

			if(listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
				LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
				//JsfUtil.addMessageError("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}

			Usuario tecnicoResponsable = listaTecnicosResponsables.get(0);
			parametros.put("tecnicoForestal", tecnicoResponsable.getNombre()); //tecnicoForestal
			parametros.put("idViabilidad", viabilidadProyecto.getId());
			parametros.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, JsfUtil.getLoggedUser().getNombre());
			
			if (documentoGuiaRespuestas.getContenidoDocumento() != null) {
				documentoGuiaRespuestas.setIdViabilidad(viabilidadProyecto.getId());
				documentoGuiaRespuestas = documentosViabilidadFacade.guardarDocumentoProceso(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoGuiaRespuestas, 1, JsfUtil.getCurrentProcessInstanceId());
				
				if(documentoGuiaRespuestas == null || documentoGuiaRespuestas.getId() == null) {
					documentoGuiaRespuestas.setContenidoDocumento(null);
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
					return;
				}
			}

			try {
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				//notificacion a los tecnicos
				Object[] parametrosCorreoTecnicos = new Object[] {
						proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), 
						ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre(), nombreAreaProtegida };
				String notificacionTecnicos = mensajeNotificacionFacade
						.recuperarValorMensajeNotificacion(
								"bodyNotificacionIngresoTramiteViabilidadTecnico",
								parametrosCorreoTecnicos);

				Email.sendEmail(tecnicoResponsable, "Nuevo trámite Viabilidad Ambiental", notificacionTecnicos, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());

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
}
