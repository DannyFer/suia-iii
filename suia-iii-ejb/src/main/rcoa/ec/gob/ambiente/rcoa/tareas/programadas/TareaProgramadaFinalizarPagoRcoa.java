package ec.gob.ambiente.rcoa.tareas.programadas;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ec.gob.ambiente.rcoa.pagos.facade.FinalizarPagosRcoaFacade;

@LocalBean
@Singleton
public class TareaProgramadaFinalizarPagoRcoa {
	
	@EJB
	private FinalizarPagosRcoaFacade finalizarPagosRcoaFacade;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00",  minute = "*/5", hour = "*", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Terminacion Tarea de pago Estudio de Impacto Ambiental",persistent = true)
	public void ejecutar(){
		System.out.println("entro a finalizar pagos rcoa");
		finalizarPagosRcoaFacade.obtenerPagosRealizados();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00",  minute = "00", hour = "*", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Terminacion Tarea de pago Estudio de Impacto Ambiental",persistent = true)
	public void ejecutarDesactivarNut(){
		System.out.println("entro a deshactivar NUT ");
		finalizarPagosRcoaFacade.deshactivarNut();
	}
}
