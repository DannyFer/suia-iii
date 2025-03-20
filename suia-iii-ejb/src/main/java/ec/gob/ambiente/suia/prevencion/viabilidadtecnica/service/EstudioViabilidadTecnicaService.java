package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;

@Stateless
public class EstudioViabilidadTecnicaService implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public void ingresarEstudioViabilidadTecnicaDiagnostico(EstudioViabilidadTecnica estudioViabilidadTecnica){
		
		crudServiceBean.saveOrUpdate(estudioViabilidadTecnica);
	}
}
