package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoFacilidadesMonitoreo;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoFacilidadesMonitoreoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CatalogoFacilidadesMonitoreo findById(Integer id){
		try {
			CatalogoFacilidadesMonitoreo catalogoFacilidadesMonitoreo = (CatalogoFacilidadesMonitoreo) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT c FROM CatalogoFacilidadesMonitoreo c where c.id = :id")
					.setParameter("id", id).getSingleResult();
			return catalogoFacilidadesMonitoreo;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(CatalogoFacilidadesMonitoreo obj, Usuario usuario){
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
	public List<CatalogoFacilidadesMonitoreo> findAll(){
		List<CatalogoFacilidadesMonitoreo> catalogoFacilidadesMonitoreoList = new ArrayList<CatalogoFacilidadesMonitoreo>();
		try {
			catalogoFacilidadesMonitoreoList = (ArrayList<CatalogoFacilidadesMonitoreo>) crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT c FROM CatalogoFacilidadesMonitoreo c where c.estado = true order by c.orden")
					.getResultList();
			
			return catalogoFacilidadesMonitoreoList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return catalogoFacilidadesMonitoreoList;
	}

}
