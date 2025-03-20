package ec.gob.ambiente.rcoa.simulador.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.simulador.model.SimuladorCoordenadasProyecto;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class SimuladorCoordenadasProyectoCoaFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(SimuladorCoordenadasProyecto coordenada) {        
        	crudServiceBean.saveOrUpdate(coordenada);        
    }
	
	@SuppressWarnings("unchecked")
	public List<SimuladorCoordenadasProyecto> buscarPorForma(SimuladorProyectoLicenciaAmbientalCoaShape forma){
		
		List<SimuladorCoordenadasProyecto> lista = new ArrayList<SimuladorCoordenadasProyecto>();
		
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("Select c from SimuladorCoordenadasProyecto c where c.proyectoLicenciaAmbientalCoaShape = :forma and c.estado = true order by ordenCoordenada asc");
			sql.setParameter("forma", forma);
			
			if(sql.getResultList().size() > 0)
				lista = sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<SimuladorCoordenadasProyecto> buscarPorFormaPorProyecto(SimuladorProyectoLicenciaCoa proyecto){
		
		List<SimuladorCoordenadasProyecto> lista = new ArrayList<SimuladorCoordenadasProyecto>();
		
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("Select c from SimuladorCoordenadasProyecto c where c.proyectoLicenciaAmbientalCoaShape.proyectoLicenciaCoa.id = :proyectoId and c.estado = true order by ordenCoordenada asc");
			sql.setParameter("proyectoId", proyecto.getId());
			
			if(sql.getResultList().size() > 0)
				lista = sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
}
