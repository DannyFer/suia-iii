package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.RegistroEspeciesForestales;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;



@Stateless
public class RegistroEspeciesForestalesFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<RegistroEspeciesForestales> getByInventarioForestalCertificado(Integer idInventarioForestalAmbiental) {
		List<RegistroEspeciesForestales> list = new ArrayList<RegistroEspeciesForestales>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT ref FROM RegistroEspeciesForestales ref WHERE ref.inventarioForestalAmbiental.id=:idInventarioForestalAmbiental ORDER BY ref.id asc");
		sql.setParameter("idInventarioForestalAmbiental", idInventarioForestalAmbiental);
		if (sql.getResultList().size() > 0)
			list = (List<RegistroEspeciesForestales>) sql.getResultList();
		return list;
	}
	
	public RegistroEspeciesForestales guardar(RegistroEspeciesForestales registroEspeciesForestales){
		return crudServiceBean.saveOrUpdate(registroEspeciesForestales);
	}

}
