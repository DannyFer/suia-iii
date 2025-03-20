package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.FaseViabilidadTecnica;
import ec.gob.ambiente.suia.domain.FaseViabilidadTecnicaDiagnostico;

@Stateless
public class FaseViabilidadTecnicaDiagnosticoService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private CrudServiceBean crudServiceBean;

	public void ingresarFasesDiagnosticoViabiliad(List<FaseViabilidadTecnica> fasesViabilidadTecnica,
			EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico) {
		for (FaseViabilidadTecnica faseViabilidadTecnica : fasesViabilidadTecnica) {
			FaseViabilidadTecnicaDiagnostico faseViabilidadTecnicaDiagnostico = new FaseViabilidadTecnicaDiagnostico();
			faseViabilidadTecnicaDiagnostico.setFaseViabilidadTecnica(faseViabilidadTecnica);
			faseViabilidadTecnicaDiagnostico
					.setEstudioViabilidadTecnicaDiagnostico(estudioViabilidadTecnicaDiagnostico);
			crudServiceBean.saveOrUpdate(faseViabilidadTecnicaDiagnostico);
		}
	}
}
