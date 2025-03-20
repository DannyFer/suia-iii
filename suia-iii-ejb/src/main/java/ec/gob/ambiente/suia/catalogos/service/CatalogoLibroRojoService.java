package ec.gob.ambiente.suia.catalogos.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoLibroRojo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class CatalogoLibroRojoService {

	@EJB
	private CrudServiceBean crudServiceBean;


	public List<CatalogoLibroRojo> obtenerListaCatalogoLibroRojo() {

		return crudServiceBean.getEntityManager()
				.createQuery("From CatalogoLibroRojo c order by c.id")
				.getResultList();

	}

}
