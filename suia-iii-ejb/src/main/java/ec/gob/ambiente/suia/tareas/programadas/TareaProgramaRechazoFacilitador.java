package ec.gob.ambiente.suia.tareas.programadas;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ec.gob.ambiente.suia.facilitador.facade.FacilitadorRechazoFacade;

@LocalBean
@Singleton
public class TareaProgramaRechazoFacilitador {
	
	@EJB
	private FacilitadorRechazoFacade facilitadorRechazoFacade;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00", minute = "00", hour = "03", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Rechazo facilitador 3 días hábiles",persistent = true)
	public void ejecutarRechazoFacilitador() {		
		System.out.println("prueba de schedule para rechazo");
//		facilitadorRechazoFacade.obtenerTareasFacilitador();
		
	}

}
