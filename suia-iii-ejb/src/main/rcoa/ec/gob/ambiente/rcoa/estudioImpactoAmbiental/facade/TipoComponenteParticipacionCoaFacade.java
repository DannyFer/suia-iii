package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.TipoComponenteParticipacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class TipoComponenteParticipacionCoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;

	public List<TipoComponenteParticipacion> obtenerListaCatalogoComponenteParticipacion() {
		Query sql=crudServiceBean.getEntityManager().createQuery("Select p from TipoComponenteParticipacion p where p.estado=true");
		return  sql.getResultList();	
	}
}