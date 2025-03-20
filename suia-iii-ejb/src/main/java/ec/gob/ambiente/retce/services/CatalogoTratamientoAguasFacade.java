package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoTratamientoAguas;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoTratamientoAguasFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CatalogoTratamientoAguas findById(Integer id){
		try {
			CatalogoTratamientoAguas catalogoTratamientoAguas = (CatalogoTratamientoAguas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoTratamientoAguas o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return catalogoTratamientoAguas;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(CatalogoTratamientoAguas obj, Usuario usuario){
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
	public List<CatalogoTratamientoAguas> findAll(){
		List<CatalogoTratamientoAguas> catalogoTratamientoAguasList = new ArrayList<CatalogoTratamientoAguas>();
		try {
			catalogoTratamientoAguasList = (ArrayList<CatalogoTratamientoAguas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoTratamientoAguas o where o.estado = true order by o.orden")
					.getResultList();			
			return catalogoTratamientoAguasList;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return catalogoTratamientoAguasList;
	}
}
