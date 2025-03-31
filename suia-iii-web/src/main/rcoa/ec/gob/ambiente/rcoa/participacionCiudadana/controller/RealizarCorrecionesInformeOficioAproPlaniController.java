package ec.gob.ambiente.rcoa.participacionCiudadana.controller;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RealizarCorrecionesInformeOficioAproPlaniController {
	
	@EJB
	private ProcesoFacade procesoFacade;
	
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
	private Map<String, Object> variables;
	
	@Getter
    @Setter
    private String tramite = "";
	
	@PostConstruct
	public void inicio()
	{
		try {
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
			tramite = (String) variables.get("tramite");
		} catch (JbpmException e) {

		}
	}
	
	public void completarTarea(){
		try {
			procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), null);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}

}
