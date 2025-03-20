package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

import java.io.ByteArrayInputStream;
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
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarOficioViabilidadPfnController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(ElaborarOficioViabilidadPfnController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{oficioViabilidadPfnBean}")
	@Getter
	@Setter
	private OficioViabilidadPfnBean oficioViabilidadPfnBean;
	
	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	private Boolean habilitarFirma;

	@Getter
	@Setter
	private Integer numeroRevision;
	
	@PostConstruct
	private void iniciar() {

		numeroRevision = Integer.valueOf((String) oficioViabilidadPfnBean.getVariables().get("numeroRevisionInformacion"));
		
		oficioViabilidadPfnBean.generarOficio(true);
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalForestal/elaborarOficioViabilidad.jsf");
	}
	
	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		
		if (oficioViabilidadPfnBean.getOficioPronunciamiento().getArchivoOficio() != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					oficioViabilidadPfnBean.getOficioPronunciamiento().getArchivoOficio()), "application/octet-stream");
			content.setName(oficioViabilidadPfnBean.getOficioPronunciamiento().getNombreFichero());
		}	
		
		return content;
	}
	
	public void validateDatosIngresoOficio(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(oficioViabilidadPfnBean.getOficioPronunciamiento().getAntecedentes() == null || 
				oficioViabilidadPfnBean.getOficioPronunciamiento().getAntecedentes().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Antecedente' es requerido", null));
		
		if(oficioViabilidadPfnBean.getOficioPronunciamiento().getMarcoLegal()== null || 
				oficioViabilidadPfnBean.getOficioPronunciamiento().getMarcoLegal().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Marco Legal' es requerido", null));
		
		if(oficioViabilidadPfnBean.getOficioPronunciamiento().getConclusiones() == null || 
				oficioViabilidadPfnBean.getOficioPronunciamiento().getConclusiones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Conclusiones/Observaciones' es requerido", null));
		
		if(oficioViabilidadPfnBean.getOficioPronunciamiento().getRecomendaciones()== null || 
				oficioViabilidadPfnBean.getOficioPronunciamiento().getRecomendaciones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Recomendaciones' es requerido", null));
		
		if(oficioViabilidadPfnBean.getOficioPronunciamiento().getPronunciamiento()== null || 
				oficioViabilidadPfnBean.getOficioPronunciamiento().getPronunciamiento().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Pronunciamiento' es requerido", null));

		if(context.getMessageList().size() > 0 || errorMessages.size() > 0)
			habilitarFirma = false;
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void guardarOficio() {
		try {			
			oficioViabilidadPfnBean.guardarOficio();
	
			oficioViabilidadPfnBean.generarOficio(true);
			
			habilitarFirma = true;
			
			RequestContext.getCurrentInstance().update("form:pnlDocumentoOficio");
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			habilitarFirma = false;
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void enviar() {
		try {
			
			oficioViabilidadPfnBean.guardarOficio();
			
			Map<String, Object> variables = oficioViabilidadPfnBean.getVariables();
			
			Map<String, Object> parametros = new HashMap<>();
			
			Usuario usuarioAutoridad = oficioViabilidadPfnBean.getUsuarioAutoridad();
			String usuarioAutoridadBpm = (String) variables.get("directorBosques");
			if (usuarioAutoridadBpm == null || !usuarioAutoridadBpm.equals(usuarioAutoridad.getNombre())) {
				parametros.put("directorBosques", usuarioAutoridad.getNombre());
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
}
