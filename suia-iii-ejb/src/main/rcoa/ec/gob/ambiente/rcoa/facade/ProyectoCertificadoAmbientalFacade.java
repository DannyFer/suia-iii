package ec.gob.ambiente.rcoa.facade;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProyectoCertificadoAmbientalFacade {

	@EJB
	
	CrudServiceBean crudServiceBean;
	
	
	public  ProyectoCertificadoAmbiental getProyectoCertificadoAmbientalPorCodigoProyecto(ProyectoLicenciaCoa proyecto) {
		ProyectoCertificadoAmbiental proyectoCertificadoAmbiental = new ProyectoCertificadoAmbiental();
		
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From ProyectoCertificadoAmbiental s "
				+ " where proyectoLicenciaCoa=:proyecto ");
		sql.setParameter("proyecto", proyecto);	
		if(sql.getResultList().size()>0)
			proyectoCertificadoAmbiental=(ProyectoCertificadoAmbiental) sql.getSingleResult();
		
		return proyectoCertificadoAmbiental;

	}
	
	public ProyectoCertificadoAmbiental guardarCertificadoAmbiental(ProyectoCertificadoAmbiental proyectoCertificadoAmbiental) {
		return crudServiceBean.saveOrUpdate(proyectoCertificadoAmbiental);
	}
	
}
