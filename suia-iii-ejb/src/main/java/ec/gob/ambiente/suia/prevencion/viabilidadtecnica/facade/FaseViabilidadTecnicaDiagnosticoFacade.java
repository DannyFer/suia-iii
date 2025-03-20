package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.FaseViabilidadTecnica;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.FaseViabilidadTecnicaDiagnosticoService;

@Stateless
public class FaseViabilidadTecnicaDiagnosticoFacade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private FaseViabilidadTecnicaDiagnosticoService faseViabilidadTecnicaDiagnosticoService;
				
	public void ingresarFasesDiagnosticoViabilidad(List<FaseViabilidadTecnica> fasesViabilidadTecnica,
			EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico) {
		faseViabilidadTecnicaDiagnosticoService.ingresarFasesDiagnosticoViabiliad(fasesViabilidadTecnica,
				estudioViabilidadTecnicaDiagnostico);
	}

	
}
