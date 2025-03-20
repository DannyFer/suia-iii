package ec.gob.ambiente.rcoa.tareas.programadas;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ComparacionDeclaracionesRSQFacade;

@LocalBean
@Singleton
public class TareaProgramadaPagoDeclaracionRSQ {
	
	@EJB
	private ComparacionDeclaracionesRSQFacade comparacionDeclaracionesRSQFacade;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "30", minute = "40", hour = "4", dayOfMonth = "*", month = "*", year = "*", info = "Declaraciones RSQ",persistent = true)
	public void PagoDeclaracionRSQ() {
		
		comparacionDeclaracionesRSQFacade.validarDiaPago();		
		
	}

}
