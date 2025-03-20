package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoTipoCuerpoReceptor;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoTipoCuerpoReceptorFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CatalogoTipoCuerpoReceptor findById(Integer id){
		try {
			CatalogoTipoCuerpoReceptor catalogoTipoCuerpoReceptor = (CatalogoTipoCuerpoReceptor) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoTipoCuerpoReceptor o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return catalogoTipoCuerpoReceptor;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(CatalogoTipoCuerpoReceptor obj, Usuario usuario){
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
	public List<CatalogoTipoCuerpoReceptor> findAll(){
		List<CatalogoTipoCuerpoReceptor> catalogoTipoCuerpoReceptorList = new ArrayList<CatalogoTipoCuerpoReceptor>();
		try {
			catalogoTipoCuerpoReceptorList = (ArrayList<CatalogoTipoCuerpoReceptor>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoTipoCuerpoReceptor o where o.estado = true order by o.orden")
					.getResultList();			
			return catalogoTipoCuerpoReceptorList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return catalogoTipoCuerpoReceptorList;
	}

}
