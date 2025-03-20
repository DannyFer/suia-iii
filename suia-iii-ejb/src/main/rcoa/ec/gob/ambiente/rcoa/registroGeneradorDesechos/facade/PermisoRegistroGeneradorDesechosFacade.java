package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PermisoRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class PermisoRegistroGeneradorDesechosFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public PermisoRegistroGeneradorDesechos findById(Integer id){
		try {
			
			PermisoRegistroGeneradorDesechos resultado = (PermisoRegistroGeneradorDesechos) crudServiceBean.getEntityManager()
					.createQuery("SELECT p FROM PermisoRegistroGeneradorDesechos p WHERE p.id = :id")
					.setParameter("id", id).getSingleResult();
			
			return resultado;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public PermisoRegistroGeneradorDesechos findByIdrRCOA(Integer id){
		try {
			
			PermisoRegistroGeneradorDesechos resultado = (PermisoRegistroGeneradorDesechos) crudServiceBean.getEntityManager()
					.createQuery("SELECT p FROM PermisoRegistroGeneradorDesechos p WHERE p.ware_id = :id")
					.setParameter("ware_id", id).getSingleResult();
			
			return resultado;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<PermisoRegistroGeneradorDesechos> findByRegistroGenerador(Integer id){
		List<PermisoRegistroGeneradorDesechos> resultado = new ArrayList<PermisoRegistroGeneradorDesechos>();
		try {
			
			resultado = (List<PermisoRegistroGeneradorDesechos>) crudServiceBean
					.getEntityManager().createQuery("SELECT p FROM PermisoRegistroGeneradorDesechos p WHERE p.registroGeneradorDesechosRcoa.id = :id and p.estado = true order by 1 DESC").setParameter("id", id)
					.getResultList();
			
			return resultado;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultado;
	}
	
	public void save(PermisoRegistroGeneradorDesechos obj, Usuario usuario){
		try {
			if(obj.getId()==null){
				obj.setUsuarioCreacion(usuario.getNombre());
				obj.setFechaCreacion(new Date());			
			}
			else{
				obj.setUsuarioModificacion(usuario.getNombre());
				obj.setFechaModificacion(new Date());			
			}
			crudServiceBean.saveOrUpdate(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
