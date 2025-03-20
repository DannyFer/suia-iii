package ec.gob.ambiente.suia.reasignacionproyecto.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ReasignacionTareaProyectos;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class ReasignacionProyectosFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	
	public void modificarReasignacionProyectos(ReasignacionTareaProyectos reasignacionProyectos) throws ServiceException {
		crudServiceBean.saveOrUpdatetotal(reasignacionProyectos);
	}
	
}