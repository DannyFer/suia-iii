package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.NombreProyectos;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class NombreProyectosFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public NombreProyectos buscarPorId(Integer id){
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT n FROM NombreProyectos n where id = :id");
			sql.setParameter("id", id);
			
			NombreProyectos nombreProyecto = (NombreProyectos) sql.getSingleResult();
			
			return nombreProyecto;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(NombreProyectos obj, Usuario usuario){
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
	
	@SuppressWarnings("unchecked")
	public List<CatalogoGeneralCoa> listaTiposNombres(Integer id){
		List<CatalogoGeneralCoa> lista = new ArrayList<>();
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT c FROM CatalogoGeneralCoa c where c.catalogoTipoCoa.id = :id order by orden asc");
			sql.setParameter("id", id);
			
			lista =sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return  lista;
	}

}
