package ec.gob.ambiente.prevencion.viabilidadtecnica.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.DisenoDefinitivoVTFacade;

@ManagedBean
@ViewScoped
public class DisenoDefinitivoVTController implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4497235975244393322L;
	
	@EJB
	private DisenoDefinitivoVTFacade disenoDefinitivoVTFacade;
	
	@PostConstruct
	public void init() {
		
	}
	
	
	
	public void guardar(EstudioViabilidadTecnica estudio,
				EstudioViabilidadTecnicaDiagnostico diagnostico) {
		
		
		System.out.println("guardar Facade");

		disenoDefinitivoVTFacade.guardar(estudio,diagnostico);
	}
	
	

}
