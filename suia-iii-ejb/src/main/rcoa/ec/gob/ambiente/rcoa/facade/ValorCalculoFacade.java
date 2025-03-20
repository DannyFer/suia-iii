package ec.gob.ambiente.rcoa.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ValorCalculo;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ValorCalculoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(ValorCalculo calculo) {        
		crudServiceBean.saveOrUpdate(calculo);        
	}
	
	public ValorCalculo formulaProyecto(ProyectoLicenciaCoa proyecto)
	{
		Query sql = crudServiceBean.getEntityManager().createQuery("select v from ValorCalculo v where v.proyectoLicenciaCoa=:proyecto and v.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			return (ValorCalculo) sql.getResultList().get(0);
		
		return null;
	}

}
