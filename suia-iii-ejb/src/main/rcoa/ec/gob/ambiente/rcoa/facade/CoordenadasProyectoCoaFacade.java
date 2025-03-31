package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CoordenadasProyectoCoaFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(CoordenadasProyecto coordenada) {        
        	crudServiceBean.saveOrUpdate(coordenada);        
    }
	
	@SuppressWarnings("unchecked")
	public List<CoordenadasProyecto> buscarPorForma(ProyectoLicenciaAmbientalCoaShape forma){
		
		List<CoordenadasProyecto> lista = new ArrayList<CoordenadasProyecto>();
		
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("Select c from CoordenadasProyecto c where c.proyectoLicenciaAmbientalCoaShape = :forma and c.estado = true order by ordenCoordenada asc");
			sql.setParameter("forma", forma);
			
			if(sql.getResultList().size() > 0)
				lista = sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<CoordenadasProyecto> buscarPorFormaPorProyecto(ProyectoLicenciaCoa proyecto){
		
		List<CoordenadasProyecto> lista = new ArrayList<CoordenadasProyecto>();
		
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("Select c from CoordenadasProyecto c where c.proyectoLicenciaAmbientalCoaShape.proyectoLicenciaCoa.id = :proyectoId and c.estado = true and numeroActualizaciones = 0 order by ordenCoordenada asc");
			sql.setParameter("proyectoId", proyecto.getId());
			
			if(sql.getResultList().size() > 0)
				lista = sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
}
