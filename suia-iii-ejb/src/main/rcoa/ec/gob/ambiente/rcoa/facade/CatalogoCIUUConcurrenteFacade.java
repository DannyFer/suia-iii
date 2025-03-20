package ec.gob.ambiente.rcoa.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CatalogoCIUUConcurrente;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CatalogoCIUUConcurrenteFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public CatalogoCIUUConcurrente gestionConcurrente(String ubicacion, CatalogoCIUU catalogo)
	{
		CatalogoCIUUConcurrente obj = null;
		Query sql =crudServiceBean.getEntityManager().createQuery("select c from CatalogoCIUUConcurrente c where c.catalogoCIUU=:catalogo and c.nombreUbicacion=:ubicacion and c.estado=true");
		sql.setParameter("ubicacion", ubicacion);
		sql.setParameter("catalogo", catalogo);
		if(sql.getResultList().size()>0)
			obj=(CatalogoCIUUConcurrente) sql.getResultList().get(0);
		
		return obj;
	}
}
