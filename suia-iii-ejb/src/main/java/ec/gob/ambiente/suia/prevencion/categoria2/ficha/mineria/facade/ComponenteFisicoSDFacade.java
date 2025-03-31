package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ComponenteFisicoPendienteSD;
import ec.gob.ambiente.suia.domain.ComponenteFisicoSD;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@LocalBean
@Stateless
public class ComponenteFisicoSDFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(ComponenteFisicoSD componenteFisicoSD) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(componenteFisicoSD);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }	
	
	public void guardarPendiente(List<ComponenteFisicoPendienteSD> pendientes){
		try {			
			crudServiceBean.saveOrUpdate(pendientes);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ComponenteFisicoSD guardarComponente(ComponenteFisicoSD componenteFisicoSD) throws ServiceException {
        try {
            return crudServiceBean.saveOrUpdate(componenteFisicoSD);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
	
	public ComponenteFisicoSD buscarComponenteFisicoPorPerforacionExplorativa(Integer id){		
		try {
			ComponenteFisicoSD resultado=null;
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT b FROM ComponenteFisicoSD b WHERE b.perforacionExplorativa =:id and b.estado = true");
			query.setParameter("id", id);
			 if(query.getResultList().size()>0)
				 resultado=(ComponenteFisicoSD) query.getSingleResult();
			 
			if(resultado != null)
				return resultado;			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void guardarPendiente(ComponenteFisicoPendienteSD pendiente){
		try {			
			crudServiceBean.saveOrUpdate(pendiente);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}