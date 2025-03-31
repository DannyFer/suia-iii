package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoTipoDescarga;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoTipoDescargaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CatalogoTipoDescarga findById(Integer id){
		try {
			CatalogoTipoDescarga catalogoTipoDescarga = (CatalogoTipoDescarga) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoTipoDescarga o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			
			return catalogoTipoDescarga;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(CatalogoTipoDescarga obj, Usuario usuario){
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
	public List<CatalogoTipoDescarga> findAll(){
		List<CatalogoTipoDescarga> catalogoTipoDescargaList = new ArrayList<CatalogoTipoDescarga>();
		try {
			catalogoTipoDescargaList = (ArrayList<CatalogoTipoDescarga>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoTipoDescarga o where o.estado = true order by o.orden")
					.getResultList();			
			return catalogoTipoDescargaList;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return catalogoTipoDescargaList;
	}

}
