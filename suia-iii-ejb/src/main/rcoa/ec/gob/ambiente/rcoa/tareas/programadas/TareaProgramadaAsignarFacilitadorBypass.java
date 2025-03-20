package ec.gob.ambiente.rcoa.tareas.programadas;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ec.gob.ambiente.rcoa.participacionCiudadana.facade.AsignarFacilitadorPPCBypassFacade;

@LocalBean
@Singleton
public class TareaProgramadaAsignarFacilitadorBypass {
	
	@EJB
	private AsignarFacilitadorPPCBypassFacade asignarFacilitadorPPCBypassFacade;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00", minute = "30", hour = "03", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Rechazo facilitador 3 días hábiles",persistent = true)
	public void ejecutarRechazoFacilitador() {		
		System.out.println("Rechazo de facilitador");
		asignarFacilitadorPPCBypassFacade.asignarNuevoFacilitador();
	}
	
}
