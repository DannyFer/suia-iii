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
public class TareaProgramadaSubirActaReunion {
	
	@EJB
	private ModificacionEsIAFacade modificacionEsIAFacade;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00",  minute = "40", hour = "02", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Modificacion de Estudio de Impacto Ambiental acta",persistent = true)
	public void ejecutar(){
		System.out.println("entro en subir acta reunión de EsIA");
		modificacionEsIAFacade.obtenerTareaSubirActaReunion();
	}

}
