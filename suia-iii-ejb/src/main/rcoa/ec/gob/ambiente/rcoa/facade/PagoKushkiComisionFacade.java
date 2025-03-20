package ec.gob.ambiente.rcoa.facade;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.Entity;

import ec.gob.ambiente.rcoa.model.PagoKushkiComision;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class PagoKushkiComisionFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public PagoKushkiComision obtenerValoresComision(Double valor) {
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT p FROM PagoKushkiComision p WHERE :valor between p.pacoMinimumValue and p.pacoMaximumValue and p.pacostatus=true");
			query.setParameter("valor", new BigDecimal(valor));		
			return (PagoKushkiComision)query.getSingleResult();
		} catch (Exception e) {
			return null;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public PagoKushkiComision obtenerValoresComisionMaxima(Double valor) {
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT p FROM PagoKushkiComision p WHERE p.pacoMinimumValue >= :valor and p.pacostatus=true");
			query.setParameter("valor", new BigDecimal(valor));		
			return (PagoKushkiComision)query.getSingleResult();
		} catch (Exception e) {
			return null;
		}		
	}	
	
}
