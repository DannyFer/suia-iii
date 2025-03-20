package ec.gob.ambiente.suia.proyectos.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;

@Stateless
public class CatalogoCategoriaServiceBean {

	@EJB
	private CrudServiceBean crudServiceCatalogoCategoriaBean;

	@SuppressWarnings("unchecked")
	public List<CatalogoCategoriaSistema> listarCatalogoCategorias() {
		List<CatalogoCategoriaSistema> catalogoCategorias = crudServiceCatalogoCategoriaBean
				.getEntityManager().createQuery("From CatalogoCategoriaSistema cc")
				.getResultList();
		return catalogoCategorias;
	}

	public CatalogoCategoriaSistema buscarCatalogoCategoriaPorId(
			String idCatalogoCategoria) {
		CatalogoCategoriaSistema catalogoCategoria = (CatalogoCategoriaSistema) crudServiceCatalogoCategoriaBean
				.getEntityManager()
				.createQuery(
						"From CatalogoCategoriaSistema cc where cc.id =:Identificador")
				.setParameter("Identificador", idCatalogoCategoria)
				.getSingleResult();
		return catalogoCategoria;
	}
}
