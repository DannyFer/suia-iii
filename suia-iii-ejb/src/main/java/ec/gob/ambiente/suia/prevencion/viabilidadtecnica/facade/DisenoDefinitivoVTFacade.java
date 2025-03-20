package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.DisenoDefinitivoVTService;

@Stateless
public class DisenoDefinitivoVTFacade {
	
	@EJB
	private DisenoDefinitivoVTService disenoDefinitivoVTService;
	
	public void guardar(EstudioViabilidadTecnica estudio,
				EstudioViabilidadTecnicaDiagnostico diagnostico) {
		
		disenoDefinitivoVTService.guardar(estudio, diagnostico);
		
		
	
	}
	
	public EstudioViabilidadTecnicaDiagnostico cargarDiagnosticoFactibilidad(Integer idEstudioViabilidadTecnica) throws Exception {
		  return disenoDefinitivoVTService.cargarDiagnosticoFactibilidad(idEstudioViabilidadTecnica);
		 }

	
}
