package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ComponenteBioticoSD;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@LocalBean
@Stateless
public class ComponenteBioticoSDFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(ComponenteBioticoSD componenteBioticoSD) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(componenteBioticoSD);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
	
	public void guardarLista(List<ComponenteBioticoSD> componenteBioticoSDList) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(componenteBioticoSDList);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
	
	public List<ComponenteBioticoSD> buscarComponenteBioticoPorPerforacionExplorativa(Integer id){		
		try {
			List<ComponenteBioticoSD> resultado=null;
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT b FROM ComponenteBioticoSD b WHERE "
					+ "perforacionExplorativa = :id and estado = true");
			query.setParameter("id", id);	
			if(query.getResultList().size()>0)
				resultado = query.getResultList();
			
			if(resultado != null)
				return resultado;
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
