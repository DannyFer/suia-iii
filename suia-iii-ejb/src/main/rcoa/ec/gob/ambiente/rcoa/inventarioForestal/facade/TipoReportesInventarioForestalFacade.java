package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.TipoReporteInventarioForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

/**
 * <b> FACADE </b>
 *
 * @author Luis Lema
 * @version Revision: 1.0
 */

@Stateless
public class TipoReportesInventarioForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public TipoReporteInventarioForestal guardar(TipoReporteInventarioForestal tipoReporteInventarioForestal){
		return crudServiceBean.saveOrUpdate(tipoReporteInventarioForestal);
	}
	
	public TipoReporteInventarioForestal getByIdTipoReporteInventarioForestal(Integer idTipoReporteInventarioForestal) {
		TipoReporteInventarioForestal result = new TipoReporteInventarioForestal();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT tr FROM TipoReporteInventarioForestal tr WHERE tr.id=:idTipoReporteInventarioForestal");
		sql.setParameter("idTipoReporteInventarioForestal", idTipoReporteInventarioForestal);
		if (sql.getResultList().size() > 0)
			result = (TipoReporteInventarioForestal) sql.getResultList().get(0);
		return result;
	}
	
}
