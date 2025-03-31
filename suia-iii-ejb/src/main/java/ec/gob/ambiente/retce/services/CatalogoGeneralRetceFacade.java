package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoGeneralRetce;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoGeneralRetceFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CatalogoGeneralRetce findById(Integer id){
		try {
			CatalogoGeneralRetce catalogoGeneral = (CatalogoGeneralRetce) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT c FROM CatalogoGeneral c where c.id  = :id")
					.setParameter("id", id).getSingleResult();
			
			return catalogoGeneral;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(CatalogoGeneralRetce obj, Usuario usuario){
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
	public List<CatalogoGeneralRetce> findAll(){
		List<CatalogoGeneralRetce> catalogoGeneralList = new ArrayList<CatalogoGeneralRetce>();
		try {
			catalogoGeneralList = (ArrayList<CatalogoGeneralRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT c FROM CatalogoGeneral c where c.estado = true order by c.orden")
					.getResultList();
			
			return catalogoGeneralList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return catalogoGeneralList;
	}

}
