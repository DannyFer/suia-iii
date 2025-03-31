package ec.gob.ambiente.rcoa.pronunciamiento.aprobado.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class IngresarEiaPAController {
	
	private static final Logger LOG = Logger.getLogger(IngresarEiaPAController.class);
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	   
    /*EJBs*/   
	@EJB
    private ProcesoFacade procesoFacade;
		
	@PostConstruct
	public void init(){		
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public void enviar() {
		Map<String, Object> variables =new HashMap<>();
		variables.put("usuario_tecnico", "0401593876");
		
		try {
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(),null);

			JsfUtil.addMessageInfo("ok");
			JsfUtil.redirectToBandeja();
		} catch (JbpmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>ERROR");
		}
	}
	
	
	
}