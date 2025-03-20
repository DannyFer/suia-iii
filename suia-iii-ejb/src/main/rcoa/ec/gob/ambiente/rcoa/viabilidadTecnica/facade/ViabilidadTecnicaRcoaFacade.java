package ec.gob.ambiente.rcoa.viabilidadTecnica.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnica;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class ViabilidadTecnicaRcoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(ViabilidadTecnica obj, Usuario usuario){
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
	
	public ViabilidadTecnica buscarPorId(Integer id){
		try {			
			return (ViabilidadTecnica)crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM ViabilidadTecnica o where o.id = :id")
					.setParameter("id", id).getSingleResult();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ViabilidadTecnica> buscarPorCodigoUsuario(String codigo, Usuario usuario){
		
		List<ViabilidadTecnica> lista = new ArrayList<ViabilidadTecnica>();
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("Select v from ViabilidadTecnica v where v.estado = true "
					+ "and v.numeroOficio = :codigo and v.ruc = :usuario");
			
			query.setParameter("codigo", codigo);
			query.setParameter("usuario", usuario.getNombre());
			
			lista = (List<ViabilidadTecnica>)query.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ViabilidadTecnica> buscarPorCodigo(String codigo){
		
		List<ViabilidadTecnica> lista = new ArrayList<ViabilidadTecnica>();
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("Select v from ViabilidadTecnica v where "
					+ "v.numeroOficio = :codigo and v.estado = true");
			
			query.setParameter("codigo", codigo);
			
			lista = (List<ViabilidadTecnica>)query.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ViabilidadTecnica> buscarPorCodigoUsuarioTipo(String codigo, Usuario usuario, List<Integer> tipo){
		
		List<ViabilidadTecnica> lista = new ArrayList<ViabilidadTecnica>();
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("Select v from ViabilidadTecnica v where v.estado = true "
					+ "and v.numeroOficio = :codigo and v.ruc = :usuario and v.tipoViabilidad in :tipo");
			
			query.setParameter("codigo", codigo);
			query.setParameter("usuario", usuario.getNombre());
			query.setParameter("tipo", tipo);
			
			lista = (List<ViabilidadTecnica>)query.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	
	

}
