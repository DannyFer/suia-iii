package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class AreasSnapProvinciaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public AreasSnapProvincia guardar(AreasSnapProvincia obj){
		return crudServiceBean.saveOrUpdate(obj);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<AreasSnapProvincia> consultarAreasProtegidasSimilares(AreasSnapProvincia area){
		List<AreasSnapProvincia> lista = new ArrayList<>();
		
		try {
			
			lista = crudServiceBean.getEntityManager()
					.createQuery("SELECT a FROM AreasSnapProvincia a where a.estado = true and a.nombreAreaProtegida = :nombreArea and a.zona = :zona")
					.setParameter("nombreArea", area.getNombreAreaProtegida()).setParameter("zona", area.getZona()).getResultList();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<AreasSnapProvincia> consultarAreasProtegidasPorUsuario(Usuario usuario){
		List<AreasSnapProvincia> lista = new ArrayList<>();
		
		try {
			
			lista = crudServiceBean.getEntityManager()
					.createQuery("SELECT a FROM AreasSnapProvincia a where a.estado = true and a.usuario.id = :id")
					.setParameter("id", usuario.getId()).getResultList();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> consultarUsuarios(){
		List<Usuario> usuarios = new ArrayList<>();
		
		try {
			
			usuarios = crudServiceBean.getEntityManager()
					.createQuery("SELECT a.usuario FROM AreasSnapProvincia a where a.estado = true")
					.getResultList();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return usuarios;
	}

}
