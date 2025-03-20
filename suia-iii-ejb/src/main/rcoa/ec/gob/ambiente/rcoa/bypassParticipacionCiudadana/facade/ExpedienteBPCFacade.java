package ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ExpedienteBPC;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class ExpedienteBPCFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public ExpedienteBPC getById(Integer idExpedienteBPC) {
		ExpedienteBPC result = new ExpedienteBPC();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT ex FROM ExpedienteBPC ex WHERE ex.id=:idExpedienteBPC ORDER BY ex.id desc");
		sql.setParameter("idExpedienteBPC", idExpedienteBPC);
		if (sql.getResultList().size() > 0)
			result = (ExpedienteBPC) sql.getResultList().get(0);
		return result;
	}
	
	public ExpedienteBPC getByProyectoLicenciaCoa(Integer idProyectoLicenciaCoa) {
		ExpedienteBPC list = new ExpedienteBPC();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT ex FROM ExpedienteBPC ex WHERE ex.proyectoLicenciaCoa.id=:idProyectoLicenciaCoa ORDER BY ex.id desc");
		sql.setParameter("idProyectoLicenciaCoa", idProyectoLicenciaCoa);
		if (sql.getResultList().size() > 0)
			list = (ExpedienteBPC) sql.getResultList().get(0);
		return list;
	}
	
	public ExpedienteBPC guardar(ExpedienteBPC expedienteBPC){
		return crudServiceBean.saveOrUpdate(expedienteBPC);
	}

}
