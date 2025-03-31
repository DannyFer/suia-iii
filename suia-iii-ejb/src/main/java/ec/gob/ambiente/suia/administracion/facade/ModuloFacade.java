package ec.gob.ambiente.suia.administracion.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Modulo;

@LocalBean
@Stateless
public class ModuloFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<Modulo> listarModulosActivos(){
		
		List<Modulo> lista = new ArrayList<Modulo>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select m from Modulo m where estado = true");
			
			lista = (List<Modulo>)q.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Modulo> listarModulosBuscados(String nombre){
		
		List<Modulo> lista = new ArrayList<Modulo>();
		try {
			String busqueda = "%" + nombre.toUpperCase() + "%";
			Query q = crudServiceBean.getEntityManager().createQuery("Select m from Modulo m where estado = true and UPPER(nombre) like :busqueda");
			q.setParameter("busqueda", busqueda);
			
			lista = (List<Modulo>)q.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
		
	}

}
