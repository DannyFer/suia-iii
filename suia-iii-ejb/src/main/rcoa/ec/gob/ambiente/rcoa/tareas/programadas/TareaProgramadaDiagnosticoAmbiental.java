package ec.gob.ambiente.rcoa.tareas.programadas;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ec.gob.ambiente.rcoa.facade.ArchivarDiagnosticoAmbientalFacade;

@LocalBean
@Singleton
public class TareaProgramadaDiagnosticoAmbiental {
	
	@EJB
	private ArchivarDiagnosticoAmbientalFacade archivarDiagnosticoAmbientalFacade;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00",  minute = "10", hour = "03", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Terminacion procesos que no iniciaron el proceso de reguarización ambiental",persistent = true)
	public void archivarPorNoInicioRegularizacion(){
		archivarDiagnosticoAmbientalFacade.archivarPorNoInicioRegularizacion();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00",  minute = "20", hour = "03", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Terminacion procesos que no subsanaron observaciones",persistent = true)
	public void finalizarCorreccionDiagnostico(){
		archivarDiagnosticoAmbientalFacade.finalizarCorreccionDiagnostico();
	}

}
