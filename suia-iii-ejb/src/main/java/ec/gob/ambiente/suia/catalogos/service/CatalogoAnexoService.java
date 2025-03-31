package ec.gob.ambiente.suia.catalogos.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoAnexo;

@Stateless
public class CatalogoAnexoService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@Deprecated
	@SuppressWarnings("unchecked")
	public List<CatalogoAnexo> obtenerListaAnexo() {

		return crudServiceBean.getEntityManager()
				.createQuery("From CatalogoAnexo c order by c.id")
				.getResultList();

	}
	
	@SuppressWarnings("unchecked")
	public List<CatalogoAnexo> obtenerListaAnexoParents() {

		return crudServiceBean.getEntityManager()
				.createQuery("From CatalogoAnexo c where c.estado=true and c.catalogoAnexo is null order by c.id")
				.getResultList();

	}

}
