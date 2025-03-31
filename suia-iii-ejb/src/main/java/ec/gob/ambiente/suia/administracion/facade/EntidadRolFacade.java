package ec.gob.ambiente.suia.administracion.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EntidadRol;
import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.domain.Usuario;

@LocalBean
@Stateless
public class EntidadRolFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<EntidadRol> buscarPorRol(Rol rol){
		
		List<EntidadRol> lista = new ArrayList<EntidadRol>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("SELECT e from EntidadRol e where e.estado = true and e.rol.id = :id");
			q.setParameter("id", rol.getId());
			
			lista = (List<EntidadRol>)q.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public void guardar(EntidadRol obj, Usuario usuario){
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
	
	@SuppressWarnings("unchecked")
	public List<EntidadRol> buscarPorRolZonal(){
		
		List<EntidadRol> lista = new ArrayList<EntidadRol>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("SELECT e from EntidadRol e where e.estado = true and e.entidad.nombre = 'Direcciones Zonales'");			
			
			lista = (List<EntidadRol>)q.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<EntidadRol> buscarPorRolMunicipio(){
		
		List<EntidadRol> lista = new ArrayList<EntidadRol>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("SELECT e from EntidadRol e where e.estado = true and e.entidad.nombre = 'Gobiernos Autónomos Descentralizados Municipales'");
						
			lista = (List<EntidadRol>)q.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<EntidadRol> buscarPorRolProvincial(){
		
		List<EntidadRol> lista = new ArrayList<EntidadRol>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("SELECT e from EntidadRol e where e.estado = true and e.entidad.nombre = 'Gobiernos Autónomos Descentralizados Provinciales'");
						
			lista = (List<EntidadRol>)q.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
		

}
