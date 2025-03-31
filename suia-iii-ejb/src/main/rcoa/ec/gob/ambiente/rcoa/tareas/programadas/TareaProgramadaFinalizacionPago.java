package ec.gob.ambiente.rcoa.tareas.programadas;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ModificacionEsIAFacade;

@LocalBean
@Singleton
public class TareaProgramadaFinalizacionPago {
	
	@EJB
	private ModificacionEsIAFacade modificacionEsIAFacade;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00",  minute = "00", hour = "03", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Terminacion Tarea de pago Estudio de Impacto Ambiental",persistent = true)
//	@Schedule(second = "*",  minute = "*/1", hour = "*", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Modificacion de Estudio de Impacto Ambiental",persistent = true)
//	@Schedule(hour = "", minute = "1", persistent = false)
	public void ejecutar(){
		System.out.println("entro pago de EsIA");
		modificacionEsIAFacade.terminarTareaPago();
	}

}
