package ec.gob.ambiente.rcoa.participacionCiudadana.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class RevisarOpnObsEmitidasPublicacionEIAController {

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
    private boolean variosFacilitadores=false;
	
	@PostConstruct
	public void inicio()
	{
		try {			
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
			tramite = (String) variables.get("tramite");
			variosFacilitadores = Boolean.valueOf((String) variables.get("variosFacilitadores"));
		} catch (JbpmException e) {

		}
	}
	
	public void completarTarea(){
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			if(variosFacilitadores)
			{
				String[] facilitadoresLista = new String[1];
				List<String> facilitadores = new ArrayList<String>();
				facilitadores.add("1721166864");
				Integer cont = 0;
				for (String usuario : facilitadores) {
					facilitadoresLista[cont++] = usuario;
				}
				params.put("listaFacilitadores", facilitadoresLista);
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
			}
			
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
            
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}
}
