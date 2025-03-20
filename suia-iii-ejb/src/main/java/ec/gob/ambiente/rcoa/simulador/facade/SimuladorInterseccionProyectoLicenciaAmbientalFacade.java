package ec.gob.ambiente.rcoa.simulador.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorInterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class SimuladorInterseccionProyectoLicenciaAmbientalFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public SimuladorInterseccionProyectoLicenciaAmbiental guardar(SimuladorInterseccionProyectoLicenciaAmbiental capa) {        
        	return crudServiceBean.saveOrUpdate(capa);        
    }
	
	@SuppressWarnings("unchecked")
	public List<SimuladorInterseccionProyectoLicenciaAmbiental> intersecan(SimuladorProyectoLicenciaCoa proyecto)
	{
		List<SimuladorInterseccionProyectoLicenciaAmbiental> lista = new ArrayList<SimuladorInterseccionProyectoLicenciaAmbiental>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select i from SimuladorInterseccionProyectoLicenciaAmbiental i where i.proyectoLicenciaCoa=:proyecto and i.capa.id in(1,3,8) and i.capa.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<SimuladorInterseccionProyectoLicenciaAmbiental> getByIdProyectoCoa(Integer idProyectoCoa) {
		List<SimuladorInterseccionProyectoLicenciaAmbiental> list = new ArrayList<SimuladorInterseccionProyectoLicenciaAmbiental>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select i from SimuladorInterseccionProyectoLicenciaAmbiental i where i.proyectoLicenciaCoa.id=:idProyectoCoa and i.estado=true");
		sql.setParameter("idProyectoCoa", idProyectoCoa);
		if (sql.getResultList().size() > 0)
			list = (List<SimuladorInterseccionProyectoLicenciaAmbiental>) sql.getResultList();
		return list;
	}
	
	
	public void eliminar(SimuladorProyectoLicenciaCoa proyecto, String usuarioModifica){
		for(SimuladorInterseccionProyectoLicenciaAmbiental x : getByIdProyectoCoa(proyecto.getId()))
		{
			Query sqlUpdateI = crudServiceBean.getEntityManager().createQuery("update InterseccionProyectoLicenciaAmbiental i set i.estado=false, i.fechaModificacion = now(), i.usuarioModificacion=:user where i.id=:id");
			sqlUpdateI.setParameter("id", x.getId());
			sqlUpdateI.setParameter("user", usuarioModifica);
			sqlUpdateI.executeUpdate();
			
			Query sqlUpdateD = crudServiceBean.getEntityManager().createQuery("update DetalleInterseccionProyectoAmbiental d set d.estado=false, d.fechaModificacion = now(), d.usuarioModificacion=:user where d.interseccionProyectoLicenciaAmbiental.id=:id");
			sqlUpdateD.setParameter("id", x.getId());
			sqlUpdateD.setParameter("user", usuarioModifica);
			sqlUpdateD.executeUpdate();
		}
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> listaIntersecan(SimuladorProyectoLicenciaCoa proyecto)
	{
		HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> lista = new HashMap<SimuladorInterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
		List<SimuladorInterseccionProyectoLicenciaAmbiental> listaInt = new ArrayList<SimuladorInterseccionProyectoLicenciaAmbiental>();
		List<DetalleInterseccionProyectoAmbiental> listaDet = new ArrayList<DetalleInterseccionProyectoAmbiental>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select i from InterseccionProyectoLicenciaAmbiental i where i.proyectoLicenciaCoa=:proyecto and i.capa.id in(1,3,8) and i.capa.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
		{
			listaInt=sql.getResultList();
			for(SimuladorInterseccionProyectoLicenciaAmbiental x: listaInt)
			{
				Query sqlDetalle = crudServiceBean.getEntityManager().createQuery("select d from DetalleInterseccionProyectoAmbiental d where d.interseccionProyectoLicenciaAmbiental=:interseccion and d.estado=true");
				sqlDetalle.setParameter("interseccion", x);
				if(sqlDetalle.getResultList().size()>0)
				{
					listaDet=sqlDetalle.getResultList();
					lista.put(x, listaDet);
				}
			}
		}
		
		return lista;
	}
	
}
