package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CombinacionSubActividades;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CombinacionActividadesFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public CombinacionSubActividades getPorCatalogoCombinacion(CatalogoCIUU catalogo, String combinacion)
	{
		List<CombinacionSubActividades> lista = new ArrayList<CombinacionSubActividades>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select s from CombinacionSubActividades s where s.catalogoCIUU=:catalogo and combinaciones =:combinacion");
		sql.setParameter("catalogo", catalogo);
		sql.setParameter("combinacion", combinacion);
		if(sql.getResultList().size()>0)
			return (CombinacionSubActividades) sql.getResultList().get(0);
		else return null;
	}

}
