package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoSustanciasRetce;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoSustanciasRetceFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CatalogoSustanciasRetce findById(Integer id){
		try {
			CatalogoSustanciasRetce catalogoSustanciasRetce = (CatalogoSustanciasRetce) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoSustanciasRetce o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return catalogoSustanciasRetce;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(CatalogoSustanciasRetce obj, Usuario usuario){
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
	public List<CatalogoSustanciasRetce> findAll(){
		List<CatalogoSustanciasRetce> catalogoSustanciasRetceList = new ArrayList<CatalogoSustanciasRetce>();
		try {
			catalogoSustanciasRetceList = (ArrayList<CatalogoSustanciasRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoSustanciasRetce o where o.estado = true order by o.orden")
					.getResultList();
			
			return catalogoSustanciasRetceList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return catalogoSustanciasRetceList;
	}
	
	@SuppressWarnings("unchecked")
	public List<CatalogoSustanciasRetce> findByTipoComponente(Integer tipo, Integer tipoComun){
		List<CatalogoSustanciasRetce> catalogoSustanciasRetceList = new ArrayList<CatalogoSustanciasRetce>();
		try {
			catalogoSustanciasRetceList = (ArrayList<CatalogoSustanciasRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoSustanciasRetce o where o.estado = true and tipoComponente in (:tipo, :tipoComun) order by o.orden")
					.setParameter("tipo", tipo)
					.setParameter("tipoComun", tipoComun)
					.getResultList();
			
			return catalogoSustanciasRetceList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return catalogoSustanciasRetceList;
	}

}
