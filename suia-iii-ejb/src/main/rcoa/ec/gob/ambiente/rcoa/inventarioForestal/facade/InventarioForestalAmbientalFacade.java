package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class InventarioForestalAmbientalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public InventarioForestalAmbiental getByIdRegistroPreliminar(Integer idRegistroPreliminar) {
		InventarioForestalAmbiental result = new InventarioForestalAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT iva FROM InventarioForestalAmbiental iva WHERE iva.proyectoLicenciaCoa.id=:idRegistroPreliminar");
		sql.setParameter("idRegistroPreliminar", idRegistroPreliminar);
		if (sql.getResultList().size() > 0)
			result = (InventarioForestalAmbiental) sql.getResultList().get(0);
		return result;
	}
	
	public InventarioForestalAmbiental guardar(InventarioForestalAmbiental inventarioForestalAmbiental){
		return crudServiceBean.saveOrUpdate(inventarioForestalAmbiental);
	}

}
