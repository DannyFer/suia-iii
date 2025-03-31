package ec.gob.ambiente.suia.administracion.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ModuloRol;
import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.domain.Usuario;

@LocalBean
@Stateless
public class ModuloRolFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<ModuloRol> buscarPorRol(Rol rol){
		List<ModuloRol> lista = new ArrayList<>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select m from ModuloRol m where m.estado = true and m.rol.id = :id");
			q.setParameter("id", rol.getId());
			
			lista = (List<ModuloRol>)q.getResultList();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	public void guardarModuloRol(ModuloRol obj, Usuario usuario){
		try {
			if(obj.getId()==null){
				obj.setUsuarioCreacion(usuario.getNombre());
				obj.setFechaCreacion(new Date());			
			}
			else{
				obj.setUsuarioModificacion(usuario.getNombre());
				obj.setFechaModificacion(new Date());			
			}
			crudServiceBean.saveOrUpdate(obj);	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
