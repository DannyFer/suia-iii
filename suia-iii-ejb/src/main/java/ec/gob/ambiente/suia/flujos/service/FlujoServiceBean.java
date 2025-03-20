package ec.gob.ambiente.suia.flujos.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Flujo;

@Stateless
public class FlujoServiceBean {
	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<Flujo> listarFlujos() {
		List<Flujo> flujos = crudServiceBean.getEntityManager()
				.createQuery("From Flujo f").getResultList();
		return flujos;
	}
	
	public Flujo obtenerFlujo(Integer id) {
		return crudServiceBean.find(Flujo.class, id);
	}

}
