package ec.gob.ambiente.rcoa.viabilidadAmbientalSnap.controllers;

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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PronunciamientoBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarOficioViabilidadSnapController implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{generarOficioViabilidadSnapBean}")
	@Getter
	@Setter
	private GenerarOficioViabilidadSnapBean generarOficioViabilidadSnapBean;

	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private PronunciamientoBiodiversidadFacade pronunciamientoBiodiversidadFacade;
	
	@EJB
    private ProcesoFacade procesoFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private InformeInspeccionBiodiversidad informeInspeccion;
	
	@Getter
	@Setter
	private Boolean esReporteAprobacion, oficioGuardado, requiereCorreccion;
	
	@Getter
	@Setter
	private String urlOficio, nombreOficio;
	
	@Getter
	@Setter
	private Integer idProyecto, idViabilidad;
	
	@Getter
	@Setter
	private byte[] archivoOficio;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		try{
			viabilidadProyecto = generarOficioViabilidadSnapBean.getViabilidadProyecto();
			informeInspeccion = generarOficioViabilidadSnapBean.getInformeInspeccion();
			proyectoLicenciaCoa = generarOficioViabilidadSnapBean.getProyectoLicenciaCoa();
			
			esReporteAprobacion = informeInspeccion.getEsPronunciamientoFavorable();
			
			generarOficioViabilidadSnapBean.generarOficio(true);
			
			oficioGuardado = false;
			requiereCorreccion = generarOficioViabilidadSnapBean.getVariables().containsKey("existeObservacionesOficio") 
					? (Boolean.valueOf((String) generarOficioViabilidadSnapBean.getVariables().get("existeObservacionesOficio"))) : false;;
			
			urlOficio = generarOficioViabilidadSnapBean.getOficioPronunciamiento().getOficioPath();
			nombreOficio = generarOficioViabilidadSnapBean.getOficioPronunciamiento().getNombreOficio();
			archivoOficio = generarOficioViabilidadSnapBean.getOficioPronunciamiento().getArchivoOficio();
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al iniciar la tarea.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(), "/pages/rcoa/viabilidadAmbientalSnap/elaborarOficio.jsf");
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
	
	public void guardar() {
		try {
			pronunciamientoBiodiversidadFacade.guardar(generarOficioViabilidadSnapBean.getOficioPronunciamiento());

			oficioGuardado = true;

			generarOficioViabilidadSnapBean.generarOficio(true);
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}

	public void aceptar() {
		try {
			
			if (generarOficioViabilidadSnapBean.getOficioPronunciamiento().getRecomendaciones() == null 
					|| generarOficioViabilidadSnapBean.getOficioPronunciamiento().getRecomendaciones().isEmpty()) {
	    		JsfUtil.addMessageError("El campo 'Recomendaciones' es requerido");
	    		return;
			}
			
			pronunciamientoBiodiversidadFacade.guardar(generarOficioViabilidadSnapBean.getOficioPronunciamiento());
			generarOficioViabilidadSnapBean.generarOficio(true);
			
			Usuario tecnicoPc = null;
			
			Map<String, Object> parametros = new HashMap<>();
			
			if(viabilidadProyecto.getEsAdministracionMae()) {
				parametros.put("esGalapagos", false);
				if(viabilidadProyecto.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
					parametros.put("esGalapagos", true);
					
					String autoridadBpm =  (String) generarOficioViabilidadSnapBean.getVariables().get("autoridadAmbiental");
					if (autoridadBpm == null || !autoridadBpm.equals(generarOficioViabilidadSnapBean.getUsuarioAutoridad().getNombre())) {
						parametros.put("autoridadAmbiental", generarOficioViabilidadSnapBean.getUsuarioAutoridad().getNombre());
					}
				} else {
					Usuario responsableAreasP = generarOficioViabilidadSnapBean.getResponsableAreasProtegidas();
					
					if (responsableAreasP == null) {
						System.out.println("No se encontro usuario RESPONSABLE DE AREAS PROTEGIDAS");
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
						JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
						return;
					}
					
					String responsableAreasBpm =  (String) generarOficioViabilidadSnapBean.getVariables().get("responsableAreasPC");
					if (responsableAreasBpm == null || !responsableAreasBpm.equals(responsableAreasP.getNombre())) {
						parametros.put("responsableAreasPC", responsableAreasP.getNombre());
					}
					
					tecnicoPc = responsableAreasP;
				}
				
			} else {				
				String autoridadBpm =  (String) generarOficioViabilidadSnapBean.getVariables().get("autoridadAmbiental");
				if (autoridadBpm == null || !autoridadBpm.equals(generarOficioViabilidadSnapBean.getUsuarioAutoridad().getNombre())) {
					parametros.put("autoridadAmbiental", generarOficioViabilidadSnapBean.getUsuarioAutoridad().getNombre());
				}
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			if(viabilidadProyecto.getEsAdministracionMae()) {
				if(tecnicoPc != null && tecnicoPc.getId() != null) {
					notificarTecnico(tecnicoPc);
				}
			} else {
				notificarAutoridad(generarOficioViabilidadSnapBean.getUsuarioAutoridad());
			}

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public void notificarTecnico(Usuario tecnicoResponsable) throws ServiceException {
		Object[] parametrosCorreo = new Object[] {tecnicoResponsable.getPersona().getNombre(),
				proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), 
				viabilidadProyecto.getAreaSnap().getNombreAreaCompleto()};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionViabilidadSnapRevisarInformeOficioPc");
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Email.sendEmail(tecnicoResponsable, mensajeNotificacion.getAsunto(), notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
	}
	
	public void notificarAutoridad(Usuario autoridadResponsable) throws ServiceException {
		Object[] parametrosCorreo = new Object[] {autoridadResponsable.getPersona().getNombre(),
				generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getNombreProyecto(), 
				generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental()};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionViabilidadSnapDelegadaRevisarInformeAutoridad");
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Email.sendEmail(autoridadResponsable, mensajeNotificacion.getAsunto(), notificacion, generarOficioViabilidadSnapBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
	}
}
