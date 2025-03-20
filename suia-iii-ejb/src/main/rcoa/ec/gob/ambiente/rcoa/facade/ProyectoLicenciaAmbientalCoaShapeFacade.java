package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProyectoLicenciaAmbientalCoaShapeFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public ProyectoLicenciaAmbientalCoaShape guardar(ProyectoLicenciaAmbientalCoaShape obj) {        
        	return crudServiceBean.saveOrUpdate(obj);        
    }
	
	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciaAmbientalCoaShape> buscarFormaGeograficaPorProyecto(ProyectoLicenciaCoa proyecto, Integer tipo, Integer nroActualizacion){
		
		List<ProyectoLicenciaAmbientalCoaShape> lista = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select p from ProyectoLicenciaAmbientalCoaShape p where p.proyectoLicenciaCoa = :proyecto and p.estado = true and tipo = :tipo and numeroActualizaciones = :nroActualizacion");
			sql.setParameter("proyecto", proyecto);
			sql.setParameter("tipo", tipo);
			sql.setParameter("nroActualizacion", nroActualizacion);
			
			if(sql.getResultList().size() > 0)
				lista = sql.getResultList();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return lista;
	}
	
	public void eliminar(ProyectoLicenciaCoa proyecto, Integer tipo, Integer nroActualizacion){
		for(ProyectoLicenciaAmbientalCoaShape x: buscarFormaGeograficaPorProyecto(proyecto, tipo, nroActualizacion))
		{
			Query sqlUpdateShape = crudServiceBean.getEntityManager().createQuery("update ProyectoLicenciaAmbientalCoaShape p set p.estado=false where p.id=:id");
			sqlUpdateShape.setParameter("id", x.getId());
			sqlUpdateShape.executeUpdate();
			
			Query sqlUpdateCoor = crudServiceBean.getEntityManager().createQuery("update CoordenadasProyecto c set c.estado=false where c.proyectoLicenciaAmbientalCoaShape.id = :id");
			sqlUpdateCoor.setParameter("id", x.getId());
			sqlUpdateCoor.executeUpdate();			
		}
	}
}
