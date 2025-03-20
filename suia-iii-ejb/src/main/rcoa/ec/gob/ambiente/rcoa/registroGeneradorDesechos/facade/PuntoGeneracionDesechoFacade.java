package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionDesecho;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class PuntoGeneracionDesechoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public PuntoGeneracionDesecho findById(Integer id){
		try {
			return (PuntoGeneracionDesecho) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM PuntoGeneracionDesecho o where o.id = :id")
					.setParameter("id", id).getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(PuntoGeneracionDesecho obj, Usuario usuario){
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
	public List<PuntoGeneracionDesecho> buscarPorDesechoRcoa(Integer id){
		
		List<PuntoGeneracionDesecho> lista = new ArrayList<PuntoGeneracionDesecho>();
		try {
			
			lista = (List<PuntoGeneracionDesecho>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT p FROM PuntoGeneracionDesecho p where p.desechoRegistroGeneradorRcoa.id = :id and p.estado = true")
					.setParameter("id", id).getResultList();

			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<PuntoGeneracionDesecho> buscarPorDesechoRcoaPuntoGeneracion(Integer id, Integer idPunto){
		
		List<PuntoGeneracionDesecho> lista = new ArrayList<PuntoGeneracionDesecho>();
		try {
			
			lista = (List<PuntoGeneracionDesecho>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT p FROM PuntoGeneracionDesecho p where p.desechoRegistroGeneradorRcoa.id = :id and p.puntoGeneracionRgdRcoa.id = :idPunto and p.estado = true")
					.setParameter("id", id).setParameter("idPunto", idPunto).getResultList();

			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	

}
