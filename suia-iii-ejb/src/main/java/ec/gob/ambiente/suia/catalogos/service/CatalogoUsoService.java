package ec.gob.ambiente.suia.catalogos.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoUso;

@Stateless
public class CatalogoUsoService {

	@EJB
	private CrudServiceBean crudServiceBean;


	public List<CatalogoUso> obtenerListaCatalogosUsos() {

		return crudServiceBean.getEntityManager()
				.createQuery("From CatalogoUso c order by c.id")
				.getResultList();

	}

	public List<CatalogoUso> obtenerCatalogosUsosByNormativa() {

		return crudServiceBean.getEntityManager()
				.createQuery("From CatalogoUso c order by c.id")
				.getResultList();

	}

}
