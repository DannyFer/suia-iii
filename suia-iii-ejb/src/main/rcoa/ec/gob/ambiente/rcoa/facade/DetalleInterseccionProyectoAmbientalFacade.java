package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class DetalleInterseccionProyectoAmbientalFacade {	
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(DetalleInterseccionProyectoAmbiental intersecciones) {        
        	crudServiceBean.saveOrUpdate(intersecciones);        
    }
	
	@SuppressWarnings("unchecked")
	public List<DetalleInterseccionProyectoAmbiental> buscarPorProyecto(ProyectoLicenciaCoa proyecto)
	{
		List<DetalleInterseccionProyectoAmbiental> lista = new ArrayList<DetalleInterseccionProyectoAmbiental>();
		try {			
			Query query = crudServiceBean.getEntityManager().createQuery("select o from DetalleInterseccionProyectoAmbiental o where o.estado=true and o.interseccionProyectoLicenciaAmbiental.proyectoLicenciaCoa.codigoUnicoAmbiental=:codigoUnicoAmbiental order by 1,2,3");
			query.setParameter("codigoUnicoAmbiental", proyecto.getCodigoUnicoAmbiental());
			return (List<DetalleInterseccionProyectoAmbiental>)query.getResultList();
		}catch (NoResultException e) {
			// TODO: handle exception
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleInterseccionProyectoAmbiental> getByInterseccionProyecto(Integer idInterseccionProyecto) {
		List<DetalleInterseccionProyectoAmbiental> lista = new ArrayList<DetalleInterseccionProyectoAmbiental>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select d from DetalleInterseccionProyectoAmbiental d where d.interseccionProyectoLicenciaAmbiental.id=:idInterseccionProyecto and d.estado=true");
		sql.setParameter("idInterseccionProyecto", idInterseccionProyecto);
		if (sql.getResultList().size() > 0)
			lista = (List<DetalleInterseccionProyectoAmbiental>) sql.getResultList();
		
		return lista;
	}
	
	public Boolean isProyectoIntersecaCapas(String codigo) {
		List<DetalleInterseccionProyectoAmbiental> lista = new ArrayList<DetalleInterseccionProyectoAmbiental>();
		
		Query query = crudServiceBean.getEntityManager().createQuery("select o from DetalleInterseccionProyectoAmbiental o where o.estado=true and o.interseccionProyectoLicenciaAmbiental.proyectoLicenciaCoa.codigoUnicoAmbiental=:codigoUnicoAmbiental order by 1,2,3");
		query.setParameter("codigoUnicoAmbiental", codigo);
		
		if (query.getResultList().size() > 0){
			lista = (List<DetalleInterseccionProyectoAmbiental>) query.getResultList();
			for (DetalleInterseccionProyectoAmbiental detalleInterseccion : lista) {
				if(detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getViabilidad()){
					return true;
				}
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleInterseccionProyectoAmbiental> zonificacionSnap(ProyectoLicenciaCoa proyecto)
	{
		List<DetalleInterseccionProyectoAmbiental> lista = new ArrayList<DetalleInterseccionProyectoAmbiental>();
		Query query = crudServiceBean.getEntityManager().createQuery("select o from DetalleInterseccionProyectoAmbiental o "
					+ "where o.estado=true "
					+ "and o.interseccionProyectoLicenciaAmbiental.proyectoLicenciaCoa=:proyecto "
					+ "and o.interseccionProyectoLicenciaAmbiental.capa.id=:capa");
			query.setParameter("proyecto", proyecto);
			query.setParameter("capa", CapasCoa.ID_ZONIFICACION_SNAP);
			if(query.getResultList().size()>0)
				lista=query.getResultList();
		
		return lista;
	}
	
	public boolean zonaFrontera(ProyectoLicenciaCoa proyecto)
	{
		boolean estado=false;
		Query query = crudServiceBean.getEntityManager().createQuery("select o from DetalleInterseccionProyectoAmbiental o "
					+ "where o.estado=true "
					+ "and o.interseccionProyectoLicenciaAmbiental.proyectoLicenciaCoa=:proyecto "
					+ "and o.interseccionProyectoLicenciaAmbiental.capa.id=:capa");
			query.setParameter("proyecto", proyecto);
			query.setParameter("capa", CapasCoa.ID_LIMITE_INTERNO_20_KM);
			if(query.getResultList().size()>0)
				estado=true;
		
		return estado;
	}
	
	public boolean zonaIntangible(ProyectoLicenciaCoa proyecto)
	{
		boolean estado=false;
		Query query = crudServiceBean.getEntityManager().createQuery("select o from DetalleInterseccionProyectoAmbiental o "
					+ "where o.estado=true "
					+ "and o.interseccionProyectoLicenciaAmbiental.proyectoLicenciaCoa=:proyecto "
					+ "and o.interseccionProyectoLicenciaAmbiental.capa.id in("+CapasCoa.ID_INTANGIBLES_AMORTIGUAMIENTO+","+CapasCoa.ID_AMORTIGUAMIENTO_YASUNI+")");
			query.setParameter("proyecto", proyecto);
			if(query.getResultList().size()>0)	
				estado=true;
		
		return estado;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> nombresZonificacionSnap(ProyectoLicenciaCoa proyecto)
	{
		List<String> lista = new ArrayList<String>();
		Query query = crudServiceBean.getEntityManager().createQuery("select distinct o.nombreGeometria from DetalleInterseccionProyectoAmbiental o "
					+ "where o.estado=true "
					+ "and o.interseccionProyectoLicenciaAmbiental.proyectoLicenciaCoa=:proyecto "
					+ "and o.interseccionProyectoLicenciaAmbiental.capa.id=:capa");
			query.setParameter("proyecto", proyecto);
			query.setParameter("capa", CapasCoa.ID_ZONIFICACION_SNAP);
			if(query.getResultList().size()>0)
				lista=query.getResultList();
		
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<DetalleInterseccionProyectoAmbiental> getDetalleProyectoCapa(ProyectoLicenciaCoa proyecto, Integer idCapa)
	{
		List<DetalleInterseccionProyectoAmbiental> lista = new ArrayList<DetalleInterseccionProyectoAmbiental>();
		Query query = crudServiceBean.getEntityManager().createQuery("select o from DetalleInterseccionProyectoAmbiental o "
					+ "where o.estado=true "
					+ "and o.interseccionProyectoLicenciaAmbiental.proyectoLicenciaCoa=:proyecto "
					+ "and o.interseccionProyectoLicenciaAmbiental.capa.id=:capa");
			query.setParameter("proyecto", proyecto);
			query.setParameter("capa", idCapa);
			if(query.getResultList().size()>0)
				lista=query.getResultList();
		
		return lista;
	}
	
}
