package ec.gob.ambiente.suia.administracion.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoUsuario;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class TipoUsuarioFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<TipoUsuario> obtenerListaTipoUsuario(Usuario usuario){
		
		List<TipoUsuario> lista = new ArrayList<>();
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("Select t from TipoUsuario t where usuario.id = :id and estado = true");
			sql.setParameter("id", usuario.getId());
			
			lista = (ArrayList<TipoUsuario>) sql.getResultList();
			
			return lista;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return lista;
	}
	
	public void guardar(TipoUsuario tipoUsuario){
		crudServiceBean.saveOrUpdate(tipoUsuario);
	}

}
