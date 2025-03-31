package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CatalogoGeneralRetce;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class DetalleCatalogoGeneralFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public DetalleCatalogoGeneral findById(Integer id){
		try {
			DetalleCatalogoGeneral detalleCatalogoGeneral = (DetalleCatalogoGeneral) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleCatalogoGeneral o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return detalleCatalogoGeneral;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(DetalleCatalogoGeneral obj, Usuario usuario){
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
	public List<DetalleCatalogoGeneral> findAll(){
		List<DetalleCatalogoGeneral>  lista = new ArrayList<DetalleCatalogoGeneral>();
		try {
			lista = (ArrayList<DetalleCatalogoGeneral>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleCatalogoGeneral o where o.estado = true")
					.getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;	
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleCatalogoGeneral> findByCatalogoGeneral(CatalogoGeneralRetce catalogo){
		List<DetalleCatalogoGeneral>  lista = new ArrayList<DetalleCatalogoGeneral>();
		try {
			lista = (ArrayList<DetalleCatalogoGeneral>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleCatalogoGeneral o where o.estado = true and o.catalogoGeneral.id = :id order by o.orden")
					.setParameter("id", catalogo.getId()).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;	
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleCatalogoGeneral> findByCatalogoGeneralString(String codigo){
		List<DetalleCatalogoGeneral>  lista = new ArrayList<DetalleCatalogoGeneral>();
		try {
			lista = (ArrayList<DetalleCatalogoGeneral>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleCatalogoGeneral o where o.estado = true and o.catalogoGeneral.codigo = :codigo order by o.orden,o.descripcion")
					.setParameter("codigo", codigo).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;	
	}
	
	@SuppressWarnings("unchecked")
	public DetalleCatalogoGeneral findByCodigo(String codigo){
		List<DetalleCatalogoGeneral>  lista = new ArrayList<DetalleCatalogoGeneral>();
		try {
			lista = (ArrayList<DetalleCatalogoGeneral>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleCatalogoGeneral o where o.estado = true and o.codigo = :codigo order by o.orden")
					.setParameter("codigo", codigo).getResultList();
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	
	}

}
