package ec.gob.ambiente.rcoa.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.CodigosResoluciones;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;

@Stateless
public class CodigosResolucionesFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public CodigosResoluciones codigoCertificadoAmbiental(Area area, String anio){	
		Integer anios=Integer.valueOf(anio);
		CodigosResoluciones obj = new CodigosResoluciones();
		Query sql=crudServiceBean.getEntityManager().createQuery("Select p from CodigosResoluciones p where p.areaResponsable=:area and p.anios=:anios");
		sql.setParameter("area", area);
		sql.setParameter("anios", anios);
		if(sql.getResultList().size()>0)
			obj=(CodigosResoluciones) sql.getSingleResult();
		
		return obj;
	}
	
	public CodigosResoluciones guardarSecuencial(CodigosResoluciones codigosResoluciones) {
		return crudServiceBean.saveOrUpdate(codigosResoluciones);
	}
	
}
