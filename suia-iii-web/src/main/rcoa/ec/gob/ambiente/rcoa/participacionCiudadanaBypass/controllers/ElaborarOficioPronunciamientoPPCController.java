package ec.gob.ambiente.rcoa.participacionCiudadanaBypass.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;


import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarOficioPronunciamientoPPCController {
	
	private static final Logger LOG = Logger.getLogger(ElaborarOficioPronunciamientoPPCController.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{oficioPPCBypassBean}")
    private OficioPPCBypassBean oficioPPCBypassBean;

    @EJB
	private ProcesoFacade procesoFacade;

    @Getter
    @Setter
    private Boolean oficioGuardado = false, esAprobacion;
    
	@PostConstruct
	public void inicio() {
		try {
			
			oficioPPCBypassBean.generarOficio(true);

			esAprobacion = oficioPPCBypassBean.getEsAprobacion();
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Error visualizar informe / oficio");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/participacionCiudadanaBypass/elaborarOficioPronunciamiento.jsf");
	}
	
	public void guardarOficio() {
		try {
			oficioPPCBypassBean.guardarOficio(true);
			
			oficioGuardado = true;

		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void completarTarea(){
		try {
			
			oficioPPCBypassBean.guardarOficio(false);

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("autoridadAmbiental", oficioPPCBypassBean.getUsuarioAutoridad().getNombre());
				
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
}
