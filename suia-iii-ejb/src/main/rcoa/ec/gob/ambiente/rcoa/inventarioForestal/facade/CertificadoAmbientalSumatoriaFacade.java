package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.CertificadoAmbientalSumatoria;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class CertificadoAmbientalSumatoriaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CertificadoAmbientalSumatoria getByIdInventarioForestalAmbiental(Integer idInventarioForestalAmbiental) {
		CertificadoAmbientalSumatoria result = new CertificadoAmbientalSumatoria();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT ifa FROM CertificadoAmbientalSumatoria ifa WHERE ifa.inventarioForestalAmbiental.id=:idInventarioForestalAmbiental");
		sql.setParameter("idInventarioForestalAmbiental", idInventarioForestalAmbiental);
		if (sql.getResultList().size() > 0)
			result = (CertificadoAmbientalSumatoria) sql.getResultList().get(0);
		return result;
	}
	
	public CertificadoAmbientalSumatoria getByCodigoProyecto(String codigoProyecto) {
		CertificadoAmbientalSumatoria result = new CertificadoAmbientalSumatoria();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT ifa FROM CertificadoAmbientalSumatoria ifa WHERE ifa.inventarioForestalAmbiental.proyectoLicenciaCoa.codigoUnicoAmbiental=:codigoProyecto");
		sql.setParameter("codigoProyecto", codigoProyecto);
		if (sql.getResultList().size() > 0)
			result = (CertificadoAmbientalSumatoria) sql.getResultList().get(0);
		return result;
	}
	
	public CertificadoAmbientalSumatoria guardar(CertificadoAmbientalSumatoria certificadoAmbientalSumatoria){
		return crudServiceBean.saveOrUpdate(certificadoAmbientalSumatoria);
	}

}
