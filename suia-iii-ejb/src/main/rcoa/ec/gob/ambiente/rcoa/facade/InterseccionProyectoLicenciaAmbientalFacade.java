package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class InterseccionProyectoLicenciaAmbientalFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public InterseccionProyectoLicenciaAmbiental guardar(InterseccionProyectoLicenciaAmbiental capa) {        
        	return crudServiceBean.saveOrUpdate(capa);        
    }
	
	@SuppressWarnings("unchecked")
	public List<InterseccionProyectoLicenciaAmbiental> intersecan(ProyectoLicenciaCoa proyecto)
	{
		List<InterseccionProyectoLicenciaAmbiental> lista = new ArrayList<InterseccionProyectoLicenciaAmbiental>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select i from InterseccionProyectoLicenciaAmbiental i where i.proyectoLicenciaCoa=:proyecto and i.capa.id in(1,3,8,11) and i.capa.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<InterseccionProyectoLicenciaAmbiental> getByIdProyectoCoa(Integer idProyectoCoa, Integer nroActualizacion) {
		List<InterseccionProyectoLicenciaAmbiental> list = new ArrayList<InterseccionProyectoLicenciaAmbiental>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select i from InterseccionProyectoLicenciaAmbiental i where i.proyectoLicenciaCoa.id=:idProyectoCoa and i.estado=true and i.nroActualizacion = :nroActualizacion");
		sql.setParameter("idProyectoCoa", idProyectoCoa);
		sql.setParameter("nroActualizacion", nroActualizacion);
		if (sql.getResultList().size() > 0)
			list = (List<InterseccionProyectoLicenciaAmbiental>) sql.getResultList();
		return list;
	}
	
	
	public void eliminar(ProyectoLicenciaCoa proyecto, String usuarioModifica, Integer nroActualizacion){
		for(InterseccionProyectoLicenciaAmbiental x : getByIdProyectoCoa(proyecto.getId(), nroActualizacion))
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
	public HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> listaIntersecan(ProyectoLicenciaCoa proyecto)
	{
		HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> lista = new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
		List<InterseccionProyectoLicenciaAmbiental> listaInt = new ArrayList<InterseccionProyectoLicenciaAmbiental>();
		List<DetalleInterseccionProyectoAmbiental> listaDet = new ArrayList<DetalleInterseccionProyectoAmbiental>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select i from InterseccionProyectoLicenciaAmbiental i where i.proyectoLicenciaCoa=:proyecto and i.capa.id in(1,3,8) and i.capa.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
		{
			listaInt=sql.getResultList();
			for(InterseccionProyectoLicenciaAmbiental x: listaInt)
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
