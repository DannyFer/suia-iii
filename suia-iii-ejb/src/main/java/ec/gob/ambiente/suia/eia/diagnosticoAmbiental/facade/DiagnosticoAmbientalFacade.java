package ec.gob.ambiente.suia.eia.diagnosticoAmbiental.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.DiagnosticoAmbiental;
import ec.gob.ambiente.suia.eia.diagnosticoAmbiental.service.DiagnosticoAmbientalService;

@Stateless
public class DiagnosticoAmbientalFacade {

	@EJB
	DiagnosticoAmbientalService diagnosticoAmbientalService;
	
	public void guardarDiagnostico(DiagnosticoAmbiental diagnosticoAmbiental) throws Exception{
		diagnosticoAmbientalService.guardarDiagnostico(diagnosticoAmbiental);
	}

	public DiagnosticoAmbiental obtenerDiagnostico(Integer id) throws Exception{
		return diagnosticoAmbientalService.obtenerDiagnostico(id);
	}
}
