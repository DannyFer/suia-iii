package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.TipoMuestra;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
 
@Stateless
public class TipoMuestraFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public TipoMuestra findById(Integer id){
		try {
			TipoMuestra fuente = (TipoMuestra) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM TipoMuestra o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return fuente;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(TipoMuestra obj, Usuario usuario){
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
	public List<TipoMuestra> findAll(){
		List<TipoMuestra> lista = new ArrayList<TipoMuestra>();
		try {
			lista = (ArrayList<TipoMuestra>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM TipoMuestra o where o.estado = true order by o.orden")
					.getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}

}
