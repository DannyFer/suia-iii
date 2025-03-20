package ec.gob.ambiente.rcoa.demo.facade;

import javax.ejb.EJB;

import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.demo.model.Demo;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class DemoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public Demo guardar(Demo demo) {
		return crudServiceBean.saveOrUpdate(demo); 
	}

}
