package ec.gob.ambiente.rcoa.simulador.facade;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.simulador.model.SimuladorProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class SimuladorProyectoLicenciaAmbientalCoaShapeFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public SimuladorProyectoLicenciaAmbientalCoaShape guardar(SimuladorProyectoLicenciaAmbientalCoaShape obj) {        
        	return crudServiceBean.saveOrUpdate(obj);        
    }
	
	@SuppressWarnings("unchecked")
	public List<SimuladorProyectoLicenciaAmbientalCoaShape> buscarFormaGeograficaPorProyecto(SimuladorProyectoLicenciaCoa proyecto, Integer tipo){
		
		List<SimuladorProyectoLicenciaAmbientalCoaShape> lista = new ArrayList<SimuladorProyectoLicenciaAmbientalCoaShape>();	
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select p from SimuladorProyectoLicenciaCoa p where p.proyectoLicenciaCoa = :proyecto and p.estado = true and tipo = :tipo");
			sql.setParameter("proyecto", proyecto);
			sql.setParameter("tipo", tipo);
			
			if(sql.getResultList().size() > 0)
				lista = sql.getResultList();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return lista;
	}
	
	public void eliminar(SimuladorProyectoLicenciaCoa proyecto, Integer tipo){
		for(SimuladorProyectoLicenciaAmbientalCoaShape x: buscarFormaGeograficaPorProyecto(proyecto, tipo))
		{
			Query sqlUpdateShape = crudServiceBean.getEntityManager().createQuery("update SimuladorProyectoLicenciaCoa p set p.estado=false where p.id=:id");
			sqlUpdateShape.setParameter("id", x.getId());
			sqlUpdateShape.executeUpdate();
			
			Query sqlUpdateCoor = crudServiceBean.getEntityManager().createQuery("update SimuladorCoordenadasProyecto c set c.estado=false where c.proyectoLicenciaCoa.id = :id");
			sqlUpdateCoor.setParameter("id", x.getId());
			sqlUpdateCoor.executeUpdate();			
		}
	}
}
