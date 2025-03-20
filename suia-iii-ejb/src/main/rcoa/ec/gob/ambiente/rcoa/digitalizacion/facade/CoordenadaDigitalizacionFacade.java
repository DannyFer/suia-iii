package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.digitalizacion.model.CoordenadaDigitalizacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CoordenadaDigitalizacionFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardar(CoordenadaDigitalizacion obj, Usuario usuario){
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(obj);	
	}


	public List<CoordenadaDigitalizacion> obtenerCoordenadas(Integer shapeId){
		List<CoordenadaDigitalizacion> listaCoordenadas = new ArrayList<CoordenadaDigitalizacion>();
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select p from CoordenadaDigitalizacion p where p.shapeDigitalizacion.id = :shapeId and p.estado = true order by p.orden ");
			sql.setParameter("shapeId", shapeId);
			if(sql.getResultList().size() > 0)
				listaCoordenadas = sql.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaCoordenadas;
	}
}
