package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.CoordenadasInventarioForestalCertificado;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class CoordenadasInventarioForestalCertificadoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<CoordenadasInventarioForestalCertificado> getByShape(Integer idShapeInventarioForestalCertificado) {
		List<CoordenadasInventarioForestalCertificado> list = new ArrayList<CoordenadasInventarioForestalCertificado>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT cic FROM CoordenadasInventarioForestalCertificado cic WHERE cic.shapeInventarioForestalCertificado.id=:idShapeInventarioForestalCertificado ORDER BY cic.id asc");
		sql.setParameter("idShapeInventarioForestalCertificado", idShapeInventarioForestalCertificado);
		if (sql.getResultList().size() > 0)
			list = (List<CoordenadasInventarioForestalCertificado>) sql.getResultList();
		return list;
	}

	public CoordenadasInventarioForestalCertificado guardar(CoordenadasInventarioForestalCertificado coordenadasInventarioForestalCertificado){
		crudServiceBean.saveOrUpdate(coordenadasInventarioForestalCertificado);
		return coordenadasInventarioForestalCertificado;
	}
	
	public List<CoordenadasInventarioForestalCertificado> getByInspeccion(Integer id) {
		List<CoordenadasInventarioForestalCertificado> list = new ArrayList<CoordenadasInventarioForestalCertificado>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT cic FROM CoordenadasInventarioForestalCertificado cic WHERE cic.reporteInventarioForestal.id=:id ORDER BY cic.id asc");
		sql.setParameter("id", id);
		if (sql.getResultList().size() > 0)
			list = (List<CoordenadasInventarioForestalCertificado>) sql.getResultList();
		return list;
	}

}
