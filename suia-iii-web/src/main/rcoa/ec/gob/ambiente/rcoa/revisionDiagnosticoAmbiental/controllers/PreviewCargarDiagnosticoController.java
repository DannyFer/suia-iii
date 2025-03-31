package ec.gob.ambiente.rcoa.revisionDiagnosticoAmbiental.controllers;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class PreviewCargarDiagnosticoController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	private Map<String, Object> variables;
	
	@Setter
    @Getter
	private String urlFormulario;
	
	@PostConstruct
	public void init() {
		try {
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			Integer nroRevision = variables.containsKey("numeroRevisionDiagnostico") ? Integer.valueOf(variables.get("numeroRevisionDiagnostico").toString()) : 0;
			
			Boolean tramiteNotificado = variables.containsKey("notificacionDiagnostico") ? (Boolean.valueOf((String) variables.get("notificacionDiagnostico"))) : false;
			
			if(tramiteNotificado) {
				//nuevo formulario modificado para proyectos antiguos
				urlFormulario = "/pages/rcoa/revisionDiagnosticoAmbiental/cargarDiagnosticoPlanAccionV2.xhtml";
			} else if(nroRevision >= 1) {
				//formulario actual para proyectos que han sido observados
				urlFormulario = "/pages/rcoa/revisionDiagnosticoAmbiental/cargarDiagnosticoAmbientalV1.xhtml";
			} else {
				//formulario para proyectos antiguos sin observación
				urlFormulario = "/pages/rcoa/revisionDiagnosticoAmbiental/cargarDiagnosticoPlanAccionV2.xhtml";
			}
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Error visualizar resolucion / pronunciamiento");
			e.printStackTrace();
		}
	}
}
