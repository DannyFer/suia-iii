package ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ReporteBPC;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

/**
 * <b> FACADE </b>
 *
 * @author Luis Lema
 * @version Revision: 1.0
 */

@Stateless
public class ReporteBPCFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public ReporteBPC guardar(ReporteBPC reporteBPC){
		return crudServiceBean.saveOrUpdate(reporteBPC);
	}
	
	public ReporteBPC getById(Integer idReporteBPC) {
		ReporteBPC result = new ReporteBPC();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT r FROM ReporteBPC r WHERE r.id=:idReporteBPC");
		sql.setParameter("idReporteBPC", idReporteBPC);
		if (sql.getResultList().size() > 0)
			result = (ReporteBPC) sql.getResultList().get(0);
		return result;
	}
	
	
	public ReporteBPC getByIdExpediente(Integer idExpediente, Integer idTipoDocumento) {
		ReporteBPC result = new ReporteBPC();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT r FROM ReporteBPC r WHERE r.expedienteBPC.id=:idExpediente and r.tipoDocumento=:idTipoDocumento order by r.id desc");
		sql.setParameter("idExpediente", idExpediente);
		sql.setParameter("idTipoDocumento", idTipoDocumento);
		if (sql.getResultList().size() > 0){
			result = (ReporteBPC) sql.getResultList().get(0);
		}			
		return result;	
	}
	
}
