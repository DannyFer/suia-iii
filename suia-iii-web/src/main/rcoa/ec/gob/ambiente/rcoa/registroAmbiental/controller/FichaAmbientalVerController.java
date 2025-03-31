package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.registroAmbiental.bean.DatosFichaRegistroAmbientalBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FichaAmbientalVerController {
	
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
	@ManagedProperty(value = "#{datosFichaRegistroAmbientalBean}")
    private DatosFichaRegistroAmbientalBean datosFichaRegistroAmbientalBean;

	private Map<String, Object> variables;
		
	@PostConstruct
	private void init(){
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			datosFichaRegistroAmbientalBean.caragrNormativa();
			datosFichaRegistroAmbientalBean.setTramite((String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE));
			datosFichaRegistroAmbientalBean.cargarDatosregistroambiental();
		} catch (JbpmException e) {
			e.printStackTrace();
		}    
	}
	
	public void revisarFicha(){
		datosFichaRegistroAmbientalBean.setSeRevisoFicha(true);
	}
	
	public void validarTareaBpm(){
		
	}
	
	public void completarTarea(){
		Map<String, Object> parametros = new HashMap<>();
//		parametros.put("director", JsfUtil.getLoggedUser().getNombre());
//		parametros.put("esPpcFisico", false);
//		parametros.put("aprobado", true);
//		parametros.put("tieneResolucion", true);
		//para ingresar expediente
		//para ir a cragar expediente
//		parametros.put("esPpcFisico", true);
//		parametros.put("aprobado", true);
//		parametros.put("tieneResolucion", true);
		//para elaborar oficio 
		parametros.put("esPpcFisico", false);
		parametros.put("aprobado", true);
		try {

		//	procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
//			procesoFacade.reasignarTarea(JsfUtil.getLoggedUser(), 
//					JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskId(),
//					JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskSummary().getActualOwner().getId(), 
//					JsfUtil.getLoggedUser().getNombre(),
//					"ec.gob.ambiente:suia-iii:3.6.1");
//			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), parametros);
		} catch (JbpmException e) {
			e.printStackTrace();
		}
	}
}