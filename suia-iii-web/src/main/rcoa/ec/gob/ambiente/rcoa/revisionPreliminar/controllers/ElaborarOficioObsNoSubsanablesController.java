package ec.gob.ambiente.rcoa.revisionPreliminar.controllers;

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
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ObservacionesPreliminarFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ObservacionesPreliminar;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarOficioObsNoSubsanablesController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(ElaborarOficioObsNoSubsanablesController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{oficioPronunciamientoBean}")
	@Getter
	@Setter
	private OficioPronunciamientoBean oficioPronunciamientoBean;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
    private OrganizacionFacade organizacionFacade;
	
	@EJB
    private ObservacionesPreliminarFacade observacionesPreliminarFacade;
	
	@EJB
	private DocumentosCoaFacade documentoFacade;
	
	@Getter
	@Setter
	private DocumentosCOA documentoManual;

	@Getter
	@Setter
	private Boolean requiereCorrecciones, oficioGuardado;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte, tipoOficio, nombreDocumentoFirmado;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@PostConstruct
	private void iniciar() {
		try {
			
			oficioGuardado = false;
				
			generarOficio();
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void generarOficio() {
		oficioPronunciamientoBean.generarOficio(true);
		
		urlReporte = oficioPronunciamientoBean.getOficioPronunciamiento().getOficioPath();
		nombreReporte = oficioPronunciamientoBean.getOficioPronunciamiento().getNombreOficio();
		archivoReporte = oficioPronunciamientoBean.getOficioPronunciamiento().getArchivoOficio();
	}
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
	
	public void validarExisteObservacionesOficio() {
		try {
			requiereCorrecciones = false;
			List<ObservacionesPreliminar> observacionesOficio = observacionesPreliminarFacade.listarPorIdClaseNombreClaseNoCorregidas(
					oficioPronunciamientoBean.getOficioPronunciamiento().getId(), "oficioObservacionesNoSubsanables");
			
			if(observacionesOficio.size() > 0) {
				requiereCorrecciones = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public void guardarOficio() {
		oficioPronunciamientoBean.guardarOficio();
		oficioGuardado = true;
		
		generarOficio();
		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = oficioPronunciamientoBean.getOficioPronunciamiento().getArchivoOficio();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(oficioPronunciamientoBean.getOficioPronunciamiento().getNombreOficio());
			} else {
				JsfUtil.addMessageError("Error al generar el archivo");
			}

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
			
			try {
				//variable autoridad
				String autoridadCompetente =  (String) oficioPronunciamientoBean.getVariables().get("autoridadCompetente");
				if (autoridadCompetente == null || !autoridadCompetente.equals(oficioPronunciamientoBean.getUsuarioAutoridad().getNombre())) {
					parametros.put("autoridadCompetente", oficioPronunciamientoBean.getUsuarioAutoridad().getNombre());
				}
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				LOG.error(e, e);
			}
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
}
