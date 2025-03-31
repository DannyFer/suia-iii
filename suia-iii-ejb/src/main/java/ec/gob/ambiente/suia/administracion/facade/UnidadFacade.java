package ec.gob.ambiente.suia.administracion.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Unidad;

@LocalBean
@Stateless
public class UnidadFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<Unidad> listarUnidadesActivas(){
		
		List<Unidad> lista = new ArrayList<Unidad>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select u from Unidad u where estado = true");
			
			lista = (List<Unidad>)q.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
		
	}
	
	@SuppressWarnings("unchecked")
	public Unidad buscarPorId(Integer id){
		try {
			Query q = crudServiceBean.getEntityManager().createQuery("Select u from Unidad u where estado = true and u.id = :id");
			q.setParameter("id", id);
			
			List<Unidad> lista = (ArrayList<Unidad>)q.getResultList();
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}else{
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Unidad> listarUnidadesActivasPadres(){
		
		List<Unidad> lista = new ArrayList<Unidad>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select u from Unidad u where estado = true");
			
			lista = (List<Unidad>)q.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
		
	}
	
			
	@SuppressWarnings("unchecked")
	public List<Unidad> listarUnidadesActivasHijas(Integer id){
		
		List<Unidad> lista = new ArrayList<Unidad>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select u from Unidad u where estado = true and unidadPadre.id = :id");
			q.setParameter("id", id);
			
			lista = (List<Unidad>)q.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
		
	}

}
