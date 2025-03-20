package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.ShapeInventarioForestalCertificado;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class ShapeInventarioForestalCertificadoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<ShapeInventarioForestalCertificado> getByInventarioForestalAmbiental(Integer idInventarioForestalAmbiental) {
		List<ShapeInventarioForestalCertificado> list = new ArrayList<ShapeInventarioForestalCertificado>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT sic FROM ShapeInventarioForestalCertificado sic WHERE sic.inventarioForestalAmbiental.id=:idInventarioForestalAmbiental ORDER BY sic.id asc");
		sql.setParameter("idInventarioForestalAmbiental", idInventarioForestalAmbiental);
		if (sql.getResultList().size() > 0)
			list = (List<ShapeInventarioForestalCertificado>) sql.getResultList();
		return list;
	}

	public ShapeInventarioForestalCertificado guardar(ShapeInventarioForestalCertificado shapeInventarioForestalCertificado){
		return crudServiceBean.saveOrUpdate(shapeInventarioForestalCertificado);
	}

}
