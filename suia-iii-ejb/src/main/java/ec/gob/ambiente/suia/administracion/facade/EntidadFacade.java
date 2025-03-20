package ec.gob.ambiente.suia.administracion.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Entidad;

@LocalBean
@Stateless
public class EntidadFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<Entidad> listarEntidades(){
		
		List<Entidad> lista =new ArrayList<Entidad>();
		try {
			Query q = crudServiceBean.getEntityManager().createQuery("Select e from Entidad e where e.estado = true");
			
			lista = (List<Entidad>) q.getResultList();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}

}
