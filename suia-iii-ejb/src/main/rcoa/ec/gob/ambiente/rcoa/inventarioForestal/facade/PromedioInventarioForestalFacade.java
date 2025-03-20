package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.PromedioInventarioForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class PromedioInventarioForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public PromedioInventarioForestal getByIdInventarioForestalRegistro(Integer idInventarioForestalAmbiental) {
		PromedioInventarioForestal result = new PromedioInventarioForestal();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT pif FROM PromedioInventarioForestal pif WHERE pif.inventarioForestalAmbiental.id=:idInventarioForestalAmbiental ORDER BY pif.id desc");
		sql.setParameter("idInventarioForestalAmbiental", idInventarioForestalAmbiental);
		if (sql.getResultList().size() > 0)
			result = (PromedioInventarioForestal) sql.getResultList().get(0);
		return result;
	}

	public PromedioInventarioForestal guardar(PromedioInventarioForestal promedioInventarioForestal){
		return crudServiceBean.saveOrUpdate(promedioInventarioForestal);
	}

}
