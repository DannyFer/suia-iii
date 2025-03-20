package ec.gob.ambiente.suia.AutorizacionCatalogo.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreasAutorizadasCatalogo;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;

@Stateless
public class AutorizaionCatalogoService {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public AreasAutorizadasCatalogo getAreasAutorizadasCatalogo(CatalogoCategoriaSistema categoriaSistema, Area area) {
		List<AreasAutorizadasCatalogo> areasAutorizadasCatalogos= crudServiceBean
		.getEntityManager()
		.createQuery(
				" FROM AreasAutorizadasCatalogo a where a.area = :area and a.categoriaSistema = :categoriaSistema")
		.setParameter("area", area)
		.setParameter("categoriaSistema", categoriaSistema)
		.getResultList();
		
		if (areasAutorizadasCatalogos.isEmpty())
			return null;
		else
			return areasAutorizadasCatalogos.get(0);
	}
}
