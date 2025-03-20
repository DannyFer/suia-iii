package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.ReporteInventarioForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

/**
 * <b> FACADE </b>
 *
 * @author Luis Lema
 * @version Revision: 1.0
 */

@Stateless
public class ReporteInventarioForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public ReporteInventarioForestal guardar(ReporteInventarioForestal reporteInventarioForestal){
		return crudServiceBean.saveOrUpdate(reporteInventarioForestal);
	}
	
	public ReporteInventarioForestal getByIdReporteInventarioForestal(Integer idReporteInventarioForestal) {
		ReporteInventarioForestal result = new ReporteInventarioForestal();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT r FROM ReporteInventarioForestal r WHERE r.id=:idReporteInventarioForestal");
		sql.setParameter("idReporteInventarioForestal", idReporteInventarioForestal);
		if (sql.getResultList().size() > 0)
			result = (ReporteInventarioForestal) sql.getResultList().get(0);
		return result;
	}
	
	
	public ReporteInventarioForestal getByIdCertificadoByIdTipoDocumento(Integer idCertificado, Integer idTipoDocumento) {
		ReporteInventarioForestal result = new ReporteInventarioForestal();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT rif FROM ReporteInventarioForestal rif WHERE rif.inventarioForestalAmbiental.id=:idCertificado and rif.tipoReporteInventarioForestal.id=:idTipoDocumento");
		sql.setParameter("idCertificado", idCertificado);
		sql.setParameter("idTipoDocumento", idTipoDocumento);
		if (sql.getResultList().size() > 0){
			result =(ReporteInventarioForestal) sql.getResultList().get(0);
		}			
		return result;	
	}
	

	
	@SuppressWarnings("unchecked")
	public List<ReporteInventarioForestal> getByTipoReporte(Integer idTipoReporte) {
		List<ReporteInventarioForestal> list = new ArrayList<ReporteInventarioForestal>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT r FROM ReporteInventarioForestal r WHERE r.tipoReporteInventarioForestal.id=:idTipoReporte");
		sql.setParameter("idTipoReporte", idTipoReporte);
		if (sql.getResultList().size() > 0)
			list =(List<ReporteInventarioForestal>) sql.getResultList();
		return list;
	}

}
