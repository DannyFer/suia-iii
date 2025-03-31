package ec.gob.ambiente.rcoa.facade;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.NombreProyectosMsp;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class NombreProyectosMspFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void save(NombreProyectosMsp obj, Usuario usuario){
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
	
	
	public NombreProyectosMsp buscarPorIdProyecto(Integer id){
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT n FROM NombreProyectosMsp n where n.proyectoLicenciaCoa.id = :id and n.estado = true");
			sql.setParameter("id", id);
			
			NombreProyectosMsp proyecto = (NombreProyectosMsp) sql.getSingleResult();
			return proyecto;			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return null;
	}

}
