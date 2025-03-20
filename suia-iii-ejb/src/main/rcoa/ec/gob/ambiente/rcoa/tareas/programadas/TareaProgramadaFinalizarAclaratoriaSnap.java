package ec.gob.ambiente.rcoa.tareas.programadas;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.FinalizarAclaratoriaSnapFacade;

@LocalBean
@Singleton
public class TareaProgramadaFinalizarAclaratoriaSnap {
	
	@EJB
	private FinalizarAclaratoriaSnapFacade finalizarAclaratoriaSnapFacade;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00",  minute = "40", hour = "03", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Finalizar Ingreso Aclaratoria SNAP",persistent = true)
	public void ejecutar(){
		finalizarAclaratoriaSnapFacade.finalizarTareaIngresoAclaratoria();
	}

}
