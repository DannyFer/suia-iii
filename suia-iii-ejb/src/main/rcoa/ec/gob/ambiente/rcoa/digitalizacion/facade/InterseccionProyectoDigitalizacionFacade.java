package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.digitalizacion.model.DetalleInterseccionDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.InterseccionProyectoDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.ShapeDigitalizacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class InterseccionProyectoDigitalizacionFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public InterseccionProyectoDigitalizacion guardar(InterseccionProyectoDigitalizacion obj, Usuario usuario){
		InterseccionProyectoDigitalizacion inter = new InterseccionProyectoDigitalizacion();
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		inter = crudServiceBean.saveOrUpdate(obj);
		return inter;
	}

	public List<InterseccionProyectoDigitalizacion> obtenerInterseccionesPorProyecto(Integer idDigitalizacion, Integer tipoIngreso){
		List<InterseccionProyectoDigitalizacion> listaUbicaciones = new ArrayList<InterseccionProyectoDigitalizacion>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("Select a from InterseccionProyectoDigitalizacion a where a.autorizacionAdministrativaAmbiental.id = :id and a.tipoIngreso = :tipoIngreso and  a.estado = true");
			query.setParameter("id", idDigitalizacion);
			query.setParameter("tipoIngreso", tipoIngreso);
			listaUbicaciones = (List<InterseccionProyectoDigitalizacion>) query.getResultList();
			if(listaUbicaciones != null && listaUbicaciones.size() > 0)
				return listaUbicaciones;
		} catch (Exception e) {
			
		}
		return new ArrayList<InterseccionProyectoDigitalizacion>();
	}

	public void eliminarInterseccion(Integer idDigitalizacion, Integer tipoIngreso, String usuario){
		Date fecha = new Date();
		for (InterseccionProyectoDigitalizacion objInterseccion : obtenerInterseccionesPorProyecto(idDigitalizacion, tipoIngreso)) {
			Query sqlUpdateCoor = crudServiceBean.getEntityManager().createQuery("update DetalleInterseccionDigitalizacion c set c.estado=false, fechaModificacion=:fecha, usuarioModificacion=:usuario where c.interseccionProyectoDigitalizacion.id = :id");
			sqlUpdateCoor.setParameter("id", objInterseccion.getId());
			sqlUpdateCoor.setParameter("fecha", fecha);
			sqlUpdateCoor.setParameter("usuario", usuario);
			sqlUpdateCoor.executeUpdate();	
		}
		Query sqlUpdateShape = crudServiceBean.getEntityManager().createQuery("update InterseccionProyectoDigitalizacion p set p.estado=false, fechaModificacion=:fecha, usuarioModificacion=:usuario  where p.autorizacionAdministrativaAmbiental.id=:id");
		sqlUpdateShape.setParameter("id", idDigitalizacion);
		sqlUpdateShape.setParameter("fecha", fecha);
		sqlUpdateShape.setParameter("usuario", usuario);
		sqlUpdateShape.executeUpdate();
	}
}
