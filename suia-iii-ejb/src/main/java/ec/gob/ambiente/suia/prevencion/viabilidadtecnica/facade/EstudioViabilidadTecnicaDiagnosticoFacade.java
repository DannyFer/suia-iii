package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.EstudioViabilidadTecnicaDiagnosticoService;

@Stateless
public class EstudioViabilidadTecnicaDiagnosticoFacade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private EstudioViabilidadTecnicaDiagnosticoService estudioViabilidadTecnicaDiagnosticoService;

	public void ingresarEstudioViabilidadTecnicaDiagnostico(EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico) {

		estudioViabilidadTecnicaDiagnosticoService.ingresarEstudioViabilidadTecnicaDiagnostico(estudioViabilidadTecnicaDiagnostico);
	}

	
}
