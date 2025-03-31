package ec.gob.ambiente.suia.administracion.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.UnidadArea;

@Stateless
public class UnidadAreaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	
	@SuppressWarnings("unchecked")	
	public List<UnidadArea> obtenerUnidadesAreaPorArea(Integer id){
		
		List<UnidadArea> lista = new ArrayList<UnidadArea>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select u from UnidadArea u where area.id = :id and estado = true");
			q.setParameter("id", id);
			lista = (List<UnidadArea>)q.getResultList();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public UnidadArea buscarPorID(Integer id){
		Query q = crudServiceBean.getEntityManager().createQuery("Select u from UnidadArea u where u.id = :id");
		q.setParameter("id", id);
		
		return (UnidadArea) q.getSingleResult();
	}

}
