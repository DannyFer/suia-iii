package ec.gob.ambiente.rcoa.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.PagoRegistroAmbiental;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class PagoRegistroAmbientalFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public PagoRegistroAmbiental guardar(PagoRegistroAmbiental obj){
		return  crudServiceBean.saveOrUpdate(obj);   
	}
	
	
	@SuppressWarnings("unchecked")
	public PagoRegistroAmbiental obtenerPagoPorRegistro(Integer id){
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT p FROM PagoRegistroAmbiental p "
					+ "WHERE p.registroAmbientalRcoa.id = :id and p.estado = true");
			sql.setParameter("id", id);
			
			List<PagoRegistroAmbiental> lista = (List<PagoRegistroAmbiental>)sql.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
