package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.EstudioViabilidadTecnicaService;

@Stateless
public class EstudioViabilidadTecnicalFacade implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EJB
	private EstudioViabilidadTecnicaService estudioViabilidadTecnicaService;

	public void ingresarEstudioViabilidadTecnicaDiagnostico(EstudioViabilidadTecnica estudioViabilidadTecnica){
		
		estudioViabilidadTecnicaService.ingresarEstudioViabilidadTecnicaDiagnostico(estudioViabilidadTecnica);
	}
	
	
}
