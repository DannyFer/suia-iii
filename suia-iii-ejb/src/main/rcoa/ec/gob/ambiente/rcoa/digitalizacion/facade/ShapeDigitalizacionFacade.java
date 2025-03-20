package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.ShapeDigitalizacion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class ShapeDigitalizacionFacade {

	private static final Logger LOG = Logger.getLogger(ShapeDigitalizacionFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public ShapeDigitalizacion guardar(ShapeDigitalizacion obj, Usuario usuario){
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(obj);
		
		return obj;
	}
	
	public List<ShapeDigitalizacion> obtenerShape(Integer proyectoId, Integer tipo){
		List<ShapeDigitalizacion> listaShape = new ArrayList<ShapeDigitalizacion>();
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select p from ShapeDigitalizacion p where p.autorizacionAdministrativaAmbiental.id = :proyectoId and p.estado = true and tipo = :tipo ");
			sql.setParameter("proyectoId", proyectoId);
			sql.setParameter("tipo", tipo);
			if(sql.getResultList().size() > 0)
				listaShape = sql.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaShape;
	}
	
	public void eliminarShape(Integer proyectoId, Integer tipo){
		for (ShapeDigitalizacion shape : obtenerShape(proyectoId, tipo)) {
			Query sqlUpdateCoor = crudServiceBean.getEntityManager().createQuery("update CoordenadaDigitalizacion c set c.estado=false where c.shapeDigitalizacion.id = :id");
			sqlUpdateCoor.setParameter("id", shape.getId());
			sqlUpdateCoor.executeUpdate();	
		}
		Query sqlUpdateShape = crudServiceBean.getEntityManager().createQuery("update ShapeDigitalizacion p set p.estado=false where p.autorizacionAdministrativaAmbiental.id=:id");
		sqlUpdateShape.setParameter("id", proyectoId);
		sqlUpdateShape.executeUpdate();
	}
	
	public List<ShapeDigitalizacion> obtenerShapePorSistema(Integer proyectoId, Integer tipo, boolean wgs17s){
		List<ShapeDigitalizacion> listaShape = new ArrayList<ShapeDigitalizacion>();
		Query sql = null;
		try {
			if(wgs17s)
				sql = crudServiceBean.getEntityManager().createQuery("Select p from ShapeDigitalizacion p where p.autorizacionAdministrativaAmbiental.id = :proyectoId and p.estado = true and p.tipo = :tipo and p.sistemaReferencia = :sistema  and p.zona = :zona ");
			else
				sql = crudServiceBean.getEntityManager().createQuery("Select p from ShapeDigitalizacion p where p.autorizacionAdministrativaAmbiental.id = :proyectoId and p.estado = true and p.tipo = :tipo and (p.sistemaReferencia != :sistema  or (p.sistemaReferencia = :sistema and p.zona != :zona ))  ");
			sql.setParameter("proyectoId", proyectoId);
			sql.setParameter("tipo", tipo);
			sql.setParameter("sistema", "WGS84");
			sql.setParameter("zona", "17S");
			if(sql.getResultList().size() > 0)
				listaShape = sql.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaShape;
	}
	
	public void eliminarShapePorSistema(Integer proyectoId, Integer tipo, boolean wgs17s, Usuario usuario){
		Date fecha = new Date();
		for (ShapeDigitalizacion shape : obtenerShapePorSistema(proyectoId, tipo, wgs17s)) {
			Query sqlUpdateCoor = crudServiceBean.getEntityManager().createQuery("update CoordenadaDigitalizacion c set c.estado=false, c.fechaModificacion = :fechaActual, c.usuarioModificacion = :usuario  where c.shapeDigitalizacion.id = :id ");
			sqlUpdateCoor.setParameter("id", shape.getId());
			sqlUpdateCoor.setParameter("usuario", usuario.getNombre());
			sqlUpdateCoor.setParameter("fechaActual", fecha);
			sqlUpdateCoor.executeUpdate();

			Query sqlUpdateShape = crudServiceBean.getEntityManager().createQuery("update ShapeDigitalizacion p set p.estado=false, p.fechaModificacion = :fechaActual, p.usuarioModificacion = :usuario where p.autorizacionAdministrativaAmbiental.id=:proyectoId and p.id=:idShape ");
			sqlUpdateShape.setParameter("idShape", shape.getId());
			sqlUpdateShape.setParameter("proyectoId", proyectoId);
			sqlUpdateShape.setParameter("usuario", usuario.getNombre());
			sqlUpdateShape.setParameter("fechaActual", fecha);
			sqlUpdateShape.executeUpdate();
		}
	}
}
