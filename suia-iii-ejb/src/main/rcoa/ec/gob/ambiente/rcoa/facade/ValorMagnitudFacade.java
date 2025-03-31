package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.VariableCriterio;
import ec.gob.ambiente.rcoa.model.ValorMagnitud;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ValorMagnitudFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<ValorMagnitud> listaValores(VariableCriterio criterio)
	{
		List<ValorMagnitud> lista = new ArrayList<ValorMagnitud>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select v from ValorMagnitud v where v.criterioMagnitud=:criterio and v.estado=true");
		sql.setParameter("criterio", criterio);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
}
