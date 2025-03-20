package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.RegistroAmbientalSumatoria;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class RegistroAmbientalSumatoriaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<RegistroAmbientalSumatoria> getByIdInventarioForestalRegistro(Integer idInventarioForestalAmbiental) {
		List<RegistroAmbientalSumatoria> list = new ArrayList<RegistroAmbientalSumatoria>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT ras FROM RegistroAmbientalSumatoria ras WHERE ras.inventarioForestalAmbiental.id=:idInventarioForestalAmbiental ORDER BY ras.id desc");
		sql.setParameter("idInventarioForestalAmbiental", idInventarioForestalAmbiental);
		sql.setMaxResults(1);
		if (sql.getResultList().size() > 0)
			list = (List<RegistroAmbientalSumatoria>) sql.getResultList();
		return list;
	}

	public RegistroAmbientalSumatoria guardar(RegistroAmbientalSumatoria sumaInventarioForestal){
		return crudServiceBean.saveOrUpdate(sumaInventarioForestal);
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroAmbientalSumatoria> getByIdInventarioForestal(Integer idInventarioForestalAmbiental) {
		List<RegistroAmbientalSumatoria> list = new ArrayList<RegistroAmbientalSumatoria>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT ras FROM RegistroAmbientalSumatoria ras WHERE ras.inventarioForestalAmbiental.id=:idInventarioForestalAmbiental ORDER BY ras.id desc");
		sql.setParameter("idInventarioForestalAmbiental", idInventarioForestalAmbiental);		
		if (sql.getResultList().size() > 0)
			list = (List<RegistroAmbientalSumatoria>) sql.getResultList();
		return list;
	}

}
