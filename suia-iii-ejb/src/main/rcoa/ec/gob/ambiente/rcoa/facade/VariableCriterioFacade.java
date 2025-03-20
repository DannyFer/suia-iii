package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.VariableCriterio;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class VariableCriterioFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<VariableCriterio> listaCriterios(Integer nivel)
	{
		List<VariableCriterio> lista = new ArrayList<VariableCriterio>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from VariableCriterio c where c.nivelMagnitud.id=:nivel and c.estado=true");
		sql.setParameter("nivel", nivel);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;		
	}
	
	public VariableCriterio buscarVariableCriterioPorId(Integer id){
		
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("select c from VariableCriterio c where c.id = :id and c.estado = true");
			sql.setParameter("id", id);
			
			if(sql.getResultList() != null && !sql.getResultList().isEmpty()){
				return (VariableCriterio) sql.getResultList().get(0);
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
