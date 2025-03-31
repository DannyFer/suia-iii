package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.FormaPuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class FormaPuntoRecuperacionRgdRcoaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public FormaPuntoRecuperacionRgdRcoa findById(Integer id){
		try {
			return (FormaPuntoRecuperacionRgdRcoa) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM FormaPuntoRecuperacionRgdRcoa o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(FormaPuntoRecuperacionRgdRcoa obj, Usuario usuario){
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
	public List<FormaPuntoRecuperacionRgdRcoa> buscarPorPunto(Integer id){
		List<FormaPuntoRecuperacionRgdRcoa> lista = new ArrayList<FormaPuntoRecuperacionRgdRcoa>();
		try {
			lista = (List<FormaPuntoRecuperacionRgdRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM FormaPuntoRecuperacionRgdRcoa o where o.puntoRecuperacionRgdRcoa.id = :id and o.estado = true")
					.setParameter("id", id)					
					.getResultList();
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

}
