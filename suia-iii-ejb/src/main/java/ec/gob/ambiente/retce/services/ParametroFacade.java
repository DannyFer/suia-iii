package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.Parametro;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class ParametroFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public Parametro findById(Integer id){
		try {
			Parametro parametro = (Parametro) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM Parametro o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return parametro;			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(Parametro obj, Usuario usuario){
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
	public List<Parametro> findAll(){
		List<Parametro> parametrosList = new ArrayList<Parametro>();
		try {
			parametrosList = (ArrayList<Parametro>) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM Parametro o where o.estado = true")
					.getResultList();
			return parametrosList;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return parametrosList;
	}
}
