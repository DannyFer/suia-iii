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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PronunciamientoBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarOficioSnapController implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{oficioViabilidadSnapBean}")
	@Getter
	@Setter
	private OficioViabilidadSnapBean oficioViabilidadSnapBean;

	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private InformeInspeccionBiodiversidadFacade informeInspeccionFacade;
	
	@EJB
	private PronunciamientoBiodiversidadFacade pronunciamientoBiodiversidadFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
    private ProcesoFacade procesoFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
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
	private Boolean esReporteAprobacion, oficioGuardado;
	
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
			viabilidadProyecto = oficioViabilidadSnapBean.getViabilidadProyecto();
			informeInspeccion = oficioViabilidadSnapBean.getInformeInspeccion();
			
			esReporteAprobacion = informeInspeccion.getEsPronunciamientoFavorable();
			
			oficioViabilidadSnapBean.generarOficio(true);
			
			oficioGuardado = false;
			
			urlOficio = oficioViabilidadSnapBean.getOficioPronunciamiento().getOficioPath();
			nombreOficio = oficioViabilidadSnapBean.getOficioPronunciamiento().getNombreOficio();
			archivoOficio = oficioViabilidadSnapBean.getOficioPronunciamiento().getArchivoOficio();
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos OficioSnap.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(), "/pages/rcoa/viabilidadAmbiental/snap/elaborarOficioSnap.jsf");
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
	
	public void guardar() {
		try {
			pronunciamientoBiodiversidadFacade.guardar(oficioViabilidadSnapBean.getOficioPronunciamiento());

			oficioGuardado = true;

			oficioViabilidadSnapBean.generarOficio(true);
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}

	public void aceptar() {
		try {
			Map<String, Object> parametros = new HashMap<>();
			
			if(viabilidadProyecto.getEsAdministracionMae()) {
				
				String directorBpm =  (String) oficioViabilidadSnapBean.getVariables().get("directorProvincial");
				if (directorBpm == null || !directorBpm.equals(oficioViabilidadSnapBean.getUsuarioAutoridad().getNombre())) {
					parametros.put("directorProvincial", oficioViabilidadSnapBean.getUsuarioAutoridad().getNombre());
				}
				
				parametros.put("pronunciamientoSnapMaeCorrecto", true);//variable interna para regresar a la tarea de ingreso informe inspeccion
				
				parametros.put("esGalapagos", false);
				if(viabilidadProyecto.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) { //272 ID AREA DIRECCIÓN DEL PARQUE NACIONAL GALÁPAGOS
					parametros.put("esGalapagos", true);
				} else {
					Usuario responsableAreasP = oficioViabilidadSnapBean.getResponsableAreasProtegidas();
					
					if(responsableAreasP==null ){
						System.out.println("No se encontro usuario RESPONSABLE DE AREAS PROTEGIDAS");
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
						JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
						return;
					}
					
					String responsableAreasBpm =  (String) oficioViabilidadSnapBean.getVariables().get("responsableAreasPC");
					if (responsableAreasBpm == null || !responsableAreasBpm.equals(responsableAreasP.getNombre())) {
						parametros.put("responsableAreasPC", responsableAreasP.getNombre());
					}
				}
				
			} else {				
				String directorBpm =  (String) oficioViabilidadSnapBean.getVariables().get("directorPatrimonio");
				if (directorBpm == null || !directorBpm.equals(oficioViabilidadSnapBean.getUsuarioAutoridad().getNombre())) {
					parametros.put("directorPatrimonio", oficioViabilidadSnapBean.getUsuarioAutoridad().getNombre());
				}
				
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
	
}
