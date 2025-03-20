package ec.gob.ambiente.suia.tareas.programadas;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ComparacionDeclaracionesRSQFacade;

@LocalBean
@Singleton
public class TareaProgramadaComparacionDeclaracionRSQ {
	
	@EJB
	private ComparacionDeclaracionesRSQFacade comparacionDeclaracionesRSQFacade;
	
	
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//	@Schedule(second = "30", minute = "30", hour = "4", dayOfMonth = "*", month = "*", year = "*", info = "Declaraciones Movimientos Sustancias Quimicas",persistent = true)
//	public void comparacionDeclaracionesRSQ() {
//		
//		comparacionDeclaracionesRSQFacade.validarDia();		
//		
//	}

}
