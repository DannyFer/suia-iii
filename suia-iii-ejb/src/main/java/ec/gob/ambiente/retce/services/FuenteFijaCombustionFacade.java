package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.FuenteFijaCombustion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class FuenteFijaCombustionFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public FuenteFijaCombustion findById(Integer id){
		try {
			FuenteFijaCombustion fuenteFijaCombustion = (FuenteFijaCombustion) crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT o FROM FuenteFijaCombustion o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return fuenteFijaCombustion;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(FuenteFijaCombustion obj, Usuario usuario){
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
	public List<FuenteFijaCombustion> findByTipoSector(String tipoSector){
		List<FuenteFijaCombustion> lista = new ArrayList<FuenteFijaCombustion>();
		try {
			lista = (ArrayList<FuenteFijaCombustion>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM FuenteFijaCombustion o where o.estado = true and o.tipoSector.nombre = :tipoSector order by 1")
					.setParameter("tipoSector", tipoSector).getResultList();
			return lista;			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<FuenteFijaCombustion> findByTipoSectorId(Integer id){
		List<FuenteFijaCombustion> lista = new ArrayList<FuenteFijaCombustion>();
		try {
			lista = (ArrayList<FuenteFijaCombustion>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM FuenteFijaCombustion o where o.estado = true and o.tipoSector.id = :id order by 1")
					.setParameter("id", id).getResultList();
			return lista;			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

}
