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
public class TareaProgramadaRegistrarFechaReunion {
	
	@EJB
	private ModificacionEsIAFacade modificacionEsIAFacade;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00",  minute = "20", hour = "02", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Modificacion de Estudio de Impacto Ambiental Reunión",persistent = true)
	public void ejecutar(){
		System.out.println("entro en terminar tarea registrar reunión de EsIA");
		modificacionEsIAFacade.obtenerRegistraFechaReunion();
	}

}
