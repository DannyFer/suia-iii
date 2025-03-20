package ec.gob.ambiente.rcoa.participacionCiudadana.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RevisarInformeOficioPronunciamientoObsAccionesComplementariasController {

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
	
	@Getter
    @Setter
    private String tipo = "";
	
	@Getter
    @Setter
    private Boolean observacionInforme;
	
	@Getter
    @Setter
    private Boolean observacionOficio;
	
	@PostConstruct
	public void inicio()
	{
		try {
			Map<String, String> paramSesion = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            if (paramSesion.containsKey("f")) {
                tipo = paramSesion.get("f");                
            }
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
			tramite = (String) variables.get("tramite");
		} catch (JbpmException e) {

		}
	}
	
	public void completarTarea(){
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			if(observacionInforme || observacionOficio)
			{
				params.put("existenObservaciones", true);
			}
			else 
			{
				params.put("existenObservaciones", false);
				//autoridad ambiental
				if(tipo.equals("informe"))
					params.put("autoridadAmbiental", "1717679441");		
			}
			
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
            
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), null);
            
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);            
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}
}
