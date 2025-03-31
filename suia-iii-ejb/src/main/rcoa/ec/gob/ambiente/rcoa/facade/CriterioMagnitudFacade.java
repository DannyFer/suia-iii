package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.CriterioMagnitud;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CriterioMagnitudFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public CriterioMagnitud guardar(CriterioMagnitud criterio) {        
		return crudServiceBean.saveOrUpdate(criterio);        
	}
	
	@SuppressWarnings("unchecked")
	public List<CriterioMagnitud> buscarCriterioMagnitudPorProyecto(ProyectoLicenciaCoa proyecto){
		
		List<CriterioMagnitud> lista = new ArrayList<CriterioMagnitud>();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select c From CriterioMagnitud c "
				+ " where c.proyectoLicenciaCoa=:proyecto and c.estado = true");
		
		sql.setParameter("proyecto", proyecto);
		
		lista = (List<CriterioMagnitud>) sql.getResultList();
		
		return lista;
		
	}
	
	public void eliminar(ProyectoLicenciaCoa proyecto){

		Query sqlUpdateMC = crudServiceBean.getEntityManager().createQuery("update CriterioMagnitud c set c.estado=false where c.proyectoLicenciaCoa.id=:id");
		sqlUpdateMC.setParameter("id", proyecto.getId());
		sqlUpdateMC.executeUpdate();

		Query sqlUpdateC = crudServiceBean.getEntityManager().createQuery("update ValorCalculo c set c.estado=false where c.proyectoLicenciaCoa.id=:id");
		sqlUpdateC.setParameter("id", proyecto.getId());
		sqlUpdateC.executeUpdate();			

	}

}
