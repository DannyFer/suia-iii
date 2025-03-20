package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalDetalle;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class InventarioForestalDetalleFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public InventarioForestalDetalle getByInventarioForestalAmbiental(Integer idInventarioForestalAmbiental) {
		InventarioForestalDetalle result = new InventarioForestalDetalle();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT ifd FROM InventarioForestalDetalle ifd WHERE ifd.inventarioForestalAmbiental.id=:idInventarioForestalAmbiental");
		sql.setParameter("idInventarioForestalAmbiental", idInventarioForestalAmbiental);
		if (sql.getResultList().size() > 0)
			result = (InventarioForestalDetalle) sql.getResultList().get(0);
		return result;
	}
	
	public InventarioForestalDetalle guardar(InventarioForestalDetalle inventarioForestalDetalle){
		return crudServiceBean.saveOrUpdate(inventarioForestalDetalle);
	}

}
