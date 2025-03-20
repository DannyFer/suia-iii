package ec.gob.ambiente.suia.administracion.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.UnidadArea;
import ec.gob.ambiente.suia.domain.UnidadAreaUsuario;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class UnidadAreaUsuarioFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<UnidadAreaUsuario> buscarporUsuario(AreaUsuario areaUsuario){
		try {
			List<UnidadAreaUsuario> lista = new ArrayList<UnidadAreaUsuario>();
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select a from UnidadAreaUsuario a where a.estado = true and a.areaUsuario.id = :id");
			q.setParameter("id", areaUsuario.getId());
			
			lista = (ArrayList<UnidadAreaUsuario>)q.getResultList();
			
			if(lista != null & !lista.isEmpty()){
				return lista;
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<UnidadArea> buscarUnidadAreaporUsuario(AreaUsuario areaUsuario){
		try {
			List<UnidadArea> lista = new ArrayList<UnidadArea>();
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select a.unidadArea from UnidadAreaUsuario a where a.estado = true and a.areaUsuario.id = :id");
			q.setParameter("id", areaUsuario.getId());
			
			lista = (ArrayList<UnidadArea>)q.getResultList();
			
			if(lista != null & !lista.isEmpty()){
				return lista;
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	

}
