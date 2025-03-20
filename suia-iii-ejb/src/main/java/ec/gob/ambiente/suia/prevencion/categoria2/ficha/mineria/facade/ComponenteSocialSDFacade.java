package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ComponenteSocialSD;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@LocalBean
@Stateless
public class ComponenteSocialSDFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(ComponenteSocialSD componenteSocialSD) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(componenteSocialSD);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
	
	
	public ComponenteSocialSD buscarComponenteSocialPorPerforacionExplorativa(Integer id){		
		try {
			ComponenteSocialSD resultado=null;
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT b FROM ComponenteSocialSD b WHERE "
					+ "perforacionExplorativa = :id and estado = true");
			query.setParameter("id", id);
			
			 if(query.getResultList().size()>0) 
				 resultado = (ComponenteSocialSD) query.getSingleResult();
			if(resultado != null)
				return resultado;
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
}
