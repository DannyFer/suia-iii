package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.FaseRetce;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class FaseRetceFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public FaseRetce findById(Integer id){
		try {
			FaseRetce fase = (FaseRetce) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM FaseRetce o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return fase;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(FaseRetce obj, Usuario usuario){
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
	public List<FaseRetce> findByTipoSector(TipoSector tipoSector){
		List<FaseRetce> faseList = new ArrayList<FaseRetce>();
		try {
			faseList = (ArrayList<FaseRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM FaseRetce o where o.estado = true and o.tipoSector.id = :id order by orden ")
					.setParameter("id", tipoSector.getId()).getResultList();
			return faseList;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return faseList;
	}
}
