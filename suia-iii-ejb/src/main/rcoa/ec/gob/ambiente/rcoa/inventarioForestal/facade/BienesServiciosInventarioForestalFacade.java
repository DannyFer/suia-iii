package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.BienesServiciosInventarioForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class BienesServiciosInventarioForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public BienesServiciosInventarioForestal getByIdInventarioForestalRegistro(Integer idInventarioForestalAmbiental) {
		BienesServiciosInventarioForestal result = new BienesServiciosInventarioForestal();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT bsi FROM BienesServiciosInventarioForestal bsi WHERE bsi.inventarioForestalAmbiental.id=:idInventarioForestalAmbiental ORDER BY bsi.id asc");
		sql.setParameter("idInventarioForestalAmbiental", idInventarioForestalAmbiental);
		if (sql.getResultList().size() > 0)
			result = (BienesServiciosInventarioForestal) sql.getResultList().get(0);
		return result;
	}

	public BienesServiciosInventarioForestal guardar(BienesServiciosInventarioForestal bienesServiciosInventarioForestal){
		return crudServiceBean.saveOrUpdate(bienesServiciosInventarioForestal);
	}

}
